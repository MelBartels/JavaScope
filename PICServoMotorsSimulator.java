import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.colorchooser.*;
import javax.swing.filechooser.*;
import javax.accessibility.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import java.io.*;
import java.text.*;
import java.net.*;

/**
 * simuator for an array of PICServoSimListState, size of SERVO_ID.size();
 * simulates a network of servo microcontrollers using the JRKerr protocol;
 */
public class PICServoMotorsSimulator {
    // pause long enough for other apps/threads/etc to grab some cpu cycles
    private static final int runPauseMilliSec = 100;
    boolean displayDiagnostics;
    private int select;
    private boolean quit;
    private ioFactory iof;
    private IO io;

    private static final int MAX_BUF_IX = 79;
    private int inBufIx;
    private int inBufIxB;
    private byte inBuf[] = new byte[MAX_BUF_IX+1];
    private int outBufIx;
    private byte outBuf[] = new byte[MAX_BUF_IX+1];

    private static final byte addressIx = 1;
    private static final byte cmdIx = 2;
    private static final byte controlByteIx = 3;
    private static final byte newAddressIx = 3;
    private static final byte newGroupAddressIx = 4;

    private byte b;
    private byte checksum;
    private byte outChecksum;

    PICServoSimListState[] pssls;

    PICServoMotorsSimulator() {
        int id;

        astroTime.getInstance().calcSidT();

        pssls = new PICServoSimListState[SERVO_ID.size()];
        for (id = 0; id < SERVO_ID.size(); id++)
            pssls[id] = new PICServoSimListState(astroTime.getInstance().JD);

        // needed for cfg.getInstance().servoParm[id].velRadSecToTargetVel()
        // and cfg.getInstance().servoParm[id].accelDegSecSecToCountsTickTick()
        for (id = 0; id < SERVO_ID.size(); id++)
            cfg.getInstance().servoParm[id].countToRad = units.ONE_REV / cfg.getInstance().servoParm[id].stepsPerRev;

        setIO();

        if (displayDiagnostics)
            io.displayReceivedCharAsHex(true);
        else
            io.displayReceivedChar(false);
    }

    void setIO() {
        iof = new ioFactory();
        System.out.println("PICServo motor simulator");

        System.out.print("display diagnostics (true/false) ? ");
        displayDiagnostics =  console.getBoolean();

        System.out.println("select the IO type, or any other number to quit");

        Enumeration eIO_TYPE = IO_TYPE.elements();
        while (eIO_TYPE.hasMoreElements()) {
            IO_TYPE iot = (IO_TYPE) eIO_TYPE.nextElement();
            System.out.println("      "
            + (iot.KEY+1)
            + ". "
            + iot);
        }
        console.getInt();
        select = console.i;
        if (select < 1 || select > IO_TYPE.size())
            quit = true;

        else if (select == IO_TYPE.ioNone.KEY+1) {
            System.out.print("ioNone selected");
            io = iof.build(IO_TYPE.ioNone);
        }
        else if (select == IO_TYPE.ioFile.KEY+1) {
            System.out.print("enter fileLocation ");
            console.getString();
            iof.args.fileLocation = console.s;
            io = iof.build(IO_TYPE.ioFile);
        }
        else if (select == IO_TYPE.ioSerial.KEY+1) {
            System.out.print("enter portName ");
            console.getString();
            iof.args.serialPortName = console.s;
            System.out.print("enter baudRate ");
            console.getInt();
            iof.args.baudRate = console.i;
            System.out.print("turn on trace log (true/false) ? ");
            iof.args.trace = console.getBoolean();
            io = iof.build(IO_TYPE.ioSerial);
        }
        else if (select == IO_TYPE.ioUDP.KEY+1) {
            System.out.print("enter local server IP port ");
            console.getInt();
            iof.args.homeIPPort = console.i;
            System.out.print("enter remote computer's name or IP address ");
            console.getString();
            iof.args.remoteIPName = console.s;
            System.out.print("enter remote server IP port ");
            console.getInt();
            iof.args.remoteIPPort = console.i;
            System.out.print("turn on trace log (true/false) ? ");
            iof.args.trace = console.getBoolean();
            io = iof.build(IO_TYPE.ioUDP);
        }
        else if (select == IO_TYPE.ioTCPserver.KEY+1) {
            System.out.print("enter local server IP port ");
            console.getInt();
            iof.args.homeIPPort = console.i;
            System.out.print("turn on trace log (true/false) ? ");
            iof.args.trace = console.getBoolean();
            io = iof.build(IO_TYPE.ioTCPserver);
        }
        else if (select == IO_TYPE.ioTCPclient.KEY+1) {
            System.out.print("enter remote computer's name or IP address ");
            console.getString();
            iof.args.remoteIPName = console.s;
            System.out.print("enter remote server IP port ");
            console.getInt();
            iof.args.remoteIPPort = console.i;
            System.out.print("turn on trace log (true/false) ? ");
            iof.args.trace = console.getBoolean();
            io = iof.build(IO_TYPE.ioTCPclient);
        }
        else if (select == IO_TYPE.ioTCP.KEY+1) {
            System.out.print("enter local server IP port ");
            console.getInt();
            iof.args.homeIPPort = console.i;
            System.out.print("enter remote computer's name or IP address ");
            console.getString();
            iof.args.remoteIPName = console.s;
            System.out.print("enter remote server IP port ");
            console.getInt();
            iof.args.remoteIPPort = console.i;
            System.out.print("turn on trace log (true/false) ? ");
            iof.args.trace = console.getBoolean();
            io = iof.build(IO_TYPE.ioTCP);
        }
    }

    void run() {
        int id;

        if (!quit && io != null) {
            while (!quit) {
                common.threadSleep(runPauseMilliSec);
                if (io.countReadBytes() >= 3) {
                    astroTime.getInstance().calcSidT();
                    io.readSerialBuffer();
                    b = io.returnByteRead();
                    if (b == servo.HEADER_BYTE) {
                         if (displayDiagnostics)
                            System.out.println("HEADER_BYTE");
                        inBuf[0] = servo.HEADER_BYTE;
                        for (inBufIx = 1; inBufIx < 3; inBufIx++) {
                            io.readSerialBuffer();
                            inBuf[inBufIx] = io.returnByteRead();
                        }

                        if (displayDiagnostics) {
                            // display servo command string: see servoCmdTranslator class which contains
                            // all servo command names as strings
                            System.out.print(servoCmdTranslator.getString(inBuf[cmdIx]));
                            for (id = 0; id < SERVO_ID.size(); id++)
                                if (matchAddress(id))
                                    System.out.print(" " + SERVO_ID.matchKey(id));
                            System.out.println("");
                        }
                        switch (inBuf[cmdIx]) {
                            case servo.CMD_HARD_RESET:
                                calcCheckSum();
                                if (compareCheckSum())
                                    hardReset();
                                break;
                            case servo.CMD_SET_ADDRESS:
                                if (readMoreBytes(2)) {
                                    calcCheckSum();
                                    if (compareCheckSum())
                                        setAddress();
                                }
                                break;
                            case servo.CMD_DEFINE_STATUS:
                                if (readMoreBytes(1)) {
                                    calcCheckSum();
                                    if (compareCheckSum())
                                        defineStatus();
                                }
                                break;
                            case servo.CMD_SET_GAIN:
                                if (readMoreBytes(14)) {
                                    calcCheckSum();
                                    if (compareCheckSum())
                                        setGain();
                                }
                                break;
                            case servo.CMD_STOP_MOTOR:
                                if (readMoreBytes(1)) {
                                    calcCheckSum();
                                    if (compareCheckSum())
                                        stopMotor();
                                }
                                break;
                            // used to ask for status return
                            case servo.CMD_NOP:
                                calcCheckSum();
                                if (compareCheckSum())
                                    getStatus();
                                break;
                            case servo.CMD_CLEAR_STICKY_BITS:
                                calcCheckSum();
                                if (compareCheckSum())
                                    clearStickyBits();
                                break;
                            case servo.CMD_LOAD_TRAJECTORY_POSITION:
                            case servo.CMD_LOAD_TRAJECTORY_VEL:
                                loadTraj();
                                break;
                            case servo.CMD_RESET_POSITION:
                                calcCheckSum();
                                if (compareCheckSum())
                                    resetPos();
                                break;
                            case servo.CMD_START_MOTOR:
                                calcCheckSum();
                                if (compareCheckSum())
                                    startMotor();
                                break;
                            case servo.CMD_EXT_NOP_HANDPAD_SLEW_PAN_SPEEDS:
                                if (readMoreBytes(9)) {
                                    calcCheckSum();
                                    if (compareCheckSum())
                                        getStatus();
                                }
                                break;
                            case servo.CMD_EXT_NOP_GUIDE_SPEED:
                                if (readMoreBytes(5)) {
                                    calcCheckSum();
                                    if (compareCheckSum())
                                        getStatus();
                                }
                                break;
                            case servo.CMD_EXT_NOP_CONTROLLER:
                                if (readMoreBytes(1)) {
                                    calcCheckSum();
                                    if (compareCheckSum())
                                        getStatus();
                                    }
                                break;
                        }
                    }
                }
            }
            io.close();
        }
        System.out.println("end of PICServo motor simulator");
    }

    boolean readMoreBytes(int count) {
        int stopIx = inBufIx + count;

        if (io.waitForReadBytes(count, cfg.getInstance().servoPortWaitTimeMilliSecs)) {
            for ( ; inBufIx < stopIx; inBufIx++) {
                io.readSerialBuffer();
                inBuf[inBufIx] = io.returnByteRead();
            }
            return true;
        }
        return false;
    }

    void calcCheckSum() {
        int ix;

        checksum = 0;
        for (ix = 1; ix < inBufIx; ix++)
            checksum += inBuf[ix];
    }

    boolean compareCheckSum() {
        byte sum = 0;

        if (io.countReadBytes() >= 1) {
            io.readSerialBuffer();
            sum = io.returnByteRead();
            if (sum == checksum)
                return true;
        }
        System.out.println("*** Error: bad checksum: received value of "
        + sum
        + " != calculated value of "
        + checksum
        + ": calculated from "
        + inBufIx
        + " bytes ***");
        return false;
    }

    boolean matchAddress(int id) {
        return (pssls[id].groupAddress == inBuf[addressIx] || pssls[id].address == inBuf[addressIx]);
    }

    void hardReset() {
        int id = 0;

        for (id = 0; id < SERVO_ID.size(); id++) {
            pssls[id].address = 0;
            pssls[id].groupAddress = 0;
            pssls[id].defineStatus = 0;
            pssls[id].pssl.addResetPos(astroTime.getInstance().JD, 0.);
        }
        for (id = 0; id < SERVO_ID.size(); id++)
            if (matchAddress(id)) {
                pssls[id].address = 0;
                pssls[id].groupAddress = servo.GROUP_ADDRESS;
                pssls[id].defineStatus = 0;
                System.out.println("hardReset for SERVO_ID " + SERVO_ID.matchKey(id));
            }
    }

    boolean setAddress() {
        int id = 0;
        int gAddr;

        for (id = 0; id < SERVO_ID.size(); id++) {
            if (matchAddress(id)) {
                pssls[id].address = inBuf[newAddressIx];
                pssls[id].groupAddress = inBuf[newGroupAddressIx];
                gAddr = pssls[id].groupAddress;
                if (gAddr < 0)
                    gAddr += 256;
                System.out.println("setting address for SERVO_ID "
                + SERVO_ID.matchKey(id)
                + " to "
                + pssls[id].address
                + ", group "
                + gAddr);

                sendStatus(id);
                return true;
            }
        }
        System.out.println("setAddress command received, but all SERVO_ID addresses assigned");
        return false;
    }

    void defineStatus() {
        int id;

        for (id = 0; id < SERVO_ID.size(); id++) {
            if (matchAddress(id)) {
                pssls[id].defineStatus = inBuf[controlByteIx];
                sendStatus(id);
                break;
            }
        }
    }

    void setGain() {
        int id;

        for (id = 0; id < SERVO_ID.size(); id++) {
            if (matchAddress(id)) {
                sendStatus(id);
                break;
            }
        }
    }

    void stopMotor() {
        int id;

        for (id = 0; id < SERVO_ID.size(); id++) {
            if (matchAddress(id)) {
                if ((inBuf[controlByteIx] & servo.AMP_ENABLE) == cfg.getInstance().servoParm[id].ampOnValue)
                    pssls[id].ampEnabled = true;
                else
                    pssls[id].ampEnabled = true;
                if (displayDiagnostics)
                    System.out.println("ampEnabled is " + pssls[id].ampEnabled);

                // use previously sent acceleration to decelerate to a stop
                if ((inBuf[controlByteIx] & servo.STOP_SMOOTHLY) == servo.STOP_SMOOTHLY)
                    pssls[id].pssl.stopMotorSmoothly(astroTime.getInstance().JD);
                else if ((inBuf[controlByteIx] & servo.STOP_ABRUPTLY) == servo.STOP_ABRUPTLY)
                    pssls[id].pssl.stopMotorAbruptly(astroTime.getInstance().JD);
                // else TURN_MOTOR_OFF or MOTOR_POWERED, both requiring a reset;
                // be careful when handling MOTOR_POWERED as it is 0;
                else
                    pssls[id].pssl.addResetPos(astroTime.getInstance().JD, 0.);

                sendStatus(id);
                break;
            }
        }
    }

    void getStatus() {
        int id;

        for (id = 0; id < SERVO_ID.size(); id++) {
            if (matchAddress(id)) {

                sendStatus(id);
                break;
            }
        }
    }

    void clearStickyBits() {
        int id;

        for (id = 0; id < SERVO_ID.size(); id++) {
            if (matchAddress(id)) {
                pssls[id].stickyBitPosError = false;
                pssls[id].stickyBitOverCurrent = false;
                pssls[id].stickyBitTimerOverrun = false;
                pssls[id].stickyBitPosWrap = false;

                sendStatus(id);
                break;
            }
        }
    }

    void loadTraj() {
        int id;
        int bytesToRead = 0;

        // get controlByte to determine how many more bytes to read
        if (readMoreBytes(1)) {
            if ((inBuf[controlByteIx] & servo.LOAD_POSITION) == servo.LOAD_POSITION)
                bytesToRead += 4;
            if ((inBuf[controlByteIx] & servo.LOAD_VEL) == servo.LOAD_VEL)
                bytesToRead += 4;
            if ((inBuf[controlByteIx] & servo.LOAD_ACCEL) == servo.LOAD_ACCEL)
                bytesToRead += 4;
            // read in all the bytes
            if (readMoreBytes(bytesToRead)) {
                calcCheckSum();
                if (compareCheckSum()) {
                    for (id = 0; id < SERVO_ID.size(); id++) {
                        if (matchAddress(id)) {
                            // extract the values received
                            inBufIxB = controlByteIx + 1;
                            if ((inBuf[controlByteIx] & servo.LOAD_POSITION) == servo.LOAD_POSITION)
                                pssls[id].position = bufToLongValue();
                            if ((inBuf[controlByteIx] & servo.LOAD_VEL) == servo.LOAD_VEL)
                                pssls[id].vel = bufToLongValue();
                            if ((inBuf[controlByteIx] & servo.LOAD_ACCEL) == servo.LOAD_ACCEL)
                                pssls[id].accel = bufToLongValue();

                            // set values
                            if ((inBuf[controlByteIx] & servo.START_MOTION_NOW) == servo.START_MOTION_NOW)
                                pssls[id].startMotionNow = true;
                            // not .FORWARD_DIR as it = 0
                            if ((inBuf[controlByteIx] & servo.REVERSE_DIR) == servo.REVERSE_DIR)
                                pssls[id].dir = ROTATION.CCW;
                            else
                                pssls[id].dir = ROTATION.CW;
                            if ((inBuf[controlByteIx] & servo.VEL_PROFILE) == servo.VEL_PROFILE)
                                pssls[id].velProfile = true;
                            else
                                // positional move
                                pssls[id].velProfile = false;

                            if (pssls[id].startMotionNow == true)
                                startMotionNow(id);

                            sendStatus(id);
                            break;
                        }
                    }
                }
            }
        }
    }

    // set inBufIxB before calling
    long bufToLongValue() {
        int ix;
        long v;
        long rV;

        rV = 0;
        for (ix = 0; ix < 4; ix++) {
            v = (long) inBuf[inBufIxB++];
            if (v < 0)
                v += 256;
            rV += v * eMath.longPow(256, ix);
        }
        if (rV >= 2147483648L)
            rV -= 4294967296L;
        return rV;
    }

    void startMotor() {
        int id = 0;

        for (id = 0; id < SERVO_ID.size(); id++)
            if (matchAddress(id))
                startMotionNow(id);
    }

    void startMotionNow(int id) {
        if (pssls[id].velProfile == true)
            loadTrajVel(id);
        else
            loadTrajPos(id);
    }

    void loadTrajVel(int id) {
        double vel;
        double accel;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        vel = (double) pssls[id].vel / sp.velRadSecToTargetVel();
        if (pssls[id].dir == ROTATION.CCW)
            vel = -vel;
        accel = ((double) pssls[id].accel / sp.accelDegSecSecToCountsTickTick()) * units.DEG_TO_RAD;

        if (displayDiagnostics) {
            System.out.println(SERVO_ID.matchKey(id)
            + " "
            + pssls[id].vel
            + " "
            + sp.velRadSecToTargetVel()
            + " "
            + vel
            + " "
            + pssls[id].accel
            + " "
            + sp.accelDegSecSecToCountsTickTick()
            + " "
            + accel);
        }

        pssls[id].pssl.addVel(astroTime.getInstance().JD, accel, vel);
    }

    void loadTrajPos(int id) {
        double vel;
        double accel;
        double position;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        vel = (double) pssls[id].vel / sp.velRadSecToTargetVel();
        if (pssls[id].dir == ROTATION.CCW)
            vel = -vel;
        accel = ((double) pssls[id].accel / sp.accelDegSecSecToCountsTickTick()) * units.DEG_TO_RAD;
        position = (double) pssls[id].position * sp.countToRad;
        pssls[id].pssl.addPos(astroTime.getInstance().JD, accel, position, vel);
    }

    void resetPos() {
        int id = 0;

        for (id = 0; id < SERVO_ID.size(); id++)
            if (matchAddress(id))
                pssls[id].pssl.addResetPos(astroTime.getInstance().JD, 0.);
    }

    void sendOutBuf() {
        if (displayDiagnostics)
            System.out.println("sending "
            + outBufIx
            + " bytes");
        io.writeByteArrayPauseUntilXmtFinished(outBuf, outBufIx);
    }

    void sendStatus(int id) {
        int ix;
        long posCount;
        int vel;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        if (pssls[id].pssl.moveDone(astroTime.getInstance().JD))
            pssls[id].moveDone = servo.STATUS_BYTE_MOVE_DONE;
        else
            pssls[id].moveDone = 0;

        if (pssls[id].pssl.slewDone(astroTime.getInstance().JD))
            pssls[id].slewDone = servo.AUX_STATUS_BYTE_SLEW_DONE;
        else
            pssls[id].slewDone = 0;

        if (pssls[id].pssl.accelDone(astroTime.getInstance().JD))
            pssls[id].accelDone = servo.AUX_STATUS_BYTE_ACCEL_DONE;
        else
            pssls[id].accelDone = 0;

        // status byte and checksum byte always returned
        outBufIx = 0;
        outBuf[outBufIx] = 0;
        outBuf[outBufIx] += pssls[id].moveDone;
        outBuf[outBufIx] += pssls[id].checksumError;
        outBuf[outBufIx] += pssls[id].overCurrent;
        outBuf[outBufIx] += pssls[id].powerOn;
        outBuf[outBufIx] += pssls[id].positionError;
        outBuf[outBufIx] += pssls[id].limit1;
        outBuf[outBufIx] += pssls[id].limit2;
        outBuf[outBufIx] += pssls[id].homeInProgress;
        outBufIx++;

        // send 4 bytes of a long value; order is low to high; if value < 0 then add max amount to bring value
        // within range of 1/2x to 1x the max amount;
        posCount = (long) (pssls[id].pssl.currentPosRad(astroTime.getInstance().JD) / sp.countToRad);
        if (posCount < 0)
            posCount += 4294967296L;
        if ((pssls[id].defineStatus & servo.SEND_POSITION) == servo.SEND_POSITION) {
            for (ix = 0; ix < 4; ix++)
                /**
                 * ie, if posCount is 555:
                 * ix = 0: 555%256/1 = 43/1 = 43 (remainder of 555/256, or what goes in the first byte to the right of the '256' 2nd byte)
                 * ix = 1: 555%65536/256 = 555/256 = 2
                 * ix = 2: 555%16777216/65536 = 555/65536 = 0
                 * 43 + 2*256 + 0*65536 = 555
                 */
                outBuf[outBufIx++] = (byte) (((posCount % eMath.longPow(256, ix+1)) / eMath.longPow(256, ix)));
        }
        if ((pssls[id].defineStatus & servo.SEND_AD) == servo.SEND_AD)
            outBuf[outBufIx++] = 0;
        if ((pssls[id].defineStatus & servo.SEND_ACTUAL_VEL) == servo.SEND_ACTUAL_VEL) {
            vel = (int) (pssls[id].pssl.currentVelRadSec(astroTime.getInstance().JD) * sp.velRadSecToTargetVel() / 65536);
            if (vel < 0)
                vel += 65536;
            for (ix = 0; ix < 2; ix++)
                outBuf[outBufIx++] = (byte) (((vel % eMath.longPow(256, ix+1)) / eMath.longPow(256, ix)));
        }
        if ((pssls[id].defineStatus & servo.SEND_AUX_STATUS) == servo.SEND_AUX_STATUS) {
            outBuf[outBufIx] = 0;
            outBuf[outBufIx] += pssls[id].index;
            outBuf[outBufIx] += pssls[id].positionWrap;
            outBuf[outBufIx] += pssls[id].servoOn;
            outBuf[outBufIx] += pssls[id].accelDone;
            outBuf[outBufIx] += pssls[id].slewDone;
            outBuf[outBufIx] += pssls[id].timerOverrun;
            outBufIx++;
        }
        // send position as home position to simulate encoders return via SiTech controller
        if ((pssls[id].defineStatus & servo.SEND_HOME_POSITION) == servo.SEND_HOME_POSITION)
            for (ix = 0; ix < 4; ix++)
                outBuf[outBufIx++] = (byte) (((posCount % eMath.longPow(256, ix+1)) / eMath.longPow(256, ix)));
        if ((pssls[id].defineStatus & servo.SEND_DEVICE_ID) == servo.SEND_DEVICE_ID)
            for (ix = 0; ix < 2; ix++)
                outBuf[outBufIx++] = 0;

        outChecksum = 0;
        for (ix = 0; ix < outBufIx; ix++)
            outChecksum += outBuf[ix];
        outBuf[outBufIx++] = outChecksum;

        sendOutBuf();
    }
}

