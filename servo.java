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
 * servo class design notes:
 *
 * low level functions -
 * void readStatus(int id)
 * void readStatusSubr(int id)
 * void setAddress(int id)
 * void defineAllStatus()
 * void defineStatus(int id, byte controlByte)
 * void getStatus(int id)
 * void hardReset()
 * void setGain(int id)
 * void stopMotor(int id, byte stopStyle)
 * void resetPosition(int id)
 * boolean stickyBitSet(int id)
 * void clearStickyBits(int id)
 * void loadTrajectoryPosition(int id)
 * void fixAccel(int id)
 * void loadTrajectoryVel(int id)
 * void startMotor(int id)
 * void disableAmp(int id, byte stopStyle)
 *
 * low level support functions -
 * void displayCmdResults(int id)
 * logServoCmdResults class
 * void displayStatusReturn(int id)
 * void displayStickyBits(int id)
 * void buildChecksum(int id)
 * void displayOutBuf()
 * void processOutBuf(int id)
 * void longValueToOutBuf(long l)
 *
 * SiTech controller extended functions -
 * void extNOPReadCfgFromFlash(int id)
 * void extNOPWriteCfgFromFlash(int id)
 * void extNOPUseSaveFactoryDefaults(int id)
 * void extNOPSwitchToAsciiMode()
 * void extNOPInvertServoEncoder(int id)
 * void extNOPInvertMotorDir(int id)
 * void extNOPInvertScopeEncoder(int id)
 * void extNOPDragNDrop()
 * void extNOPSlewNDrop()
 * void extNOPPlatformMode()
 * void extNOPSimulDirHandpadDesign()
 * void extNOPGuideMode()
 * void extNOPExitGuideMode(boolean enableHandpad, boolean simulDirHandpad)
 * void extNOPScopeEncoder(int id)
 * void extNOPGuideSpeed(int id)
 * void extNOPHandpadSlewPanSpeeds(int id)
 *
 * mid-level functions -
 * void initServoID()
 * void initSiTechHandpadSlewPanGuideSpeeds()
 *
 * high level functions -
 * boolean cmdsNeedProcessing()
 * void processMoveCmd(int id)
 * and its helper functions -
 *    void processMoveCmdVelSubR(int id)
 *    boolean stopMotorControl(int id, byte stopStyle)
 *    void processMoveCmdPosMoveStart(int id)
 *    double velRadSecToTargetVel(int id)
 *
 * highest level functions -
 * void getStatusAll()
 * void stopMotorSmoothly(int id)
 * void stopAllMotorsSmoothly()
 * void enableAmpAllMotors()
 * void disableAmpAllMotors()
 * void waitForMotorStop(int id)
 * void waitForAllMotorsStop()
 * void validVelRadSec(int id)
 * void breakProcessMoveCmdSeq(int id)
 * void processMoveCmdControl()
 * void checkPECSynchFromLimit2(int id)
 * void checkPECSynchFromYBits()
 * void synchPEC(int id)
 *
 * general functions -
 * servo()
 * void close()
 * startControllers();
 * void initServoIDVars(int id)
 * void setCurrentPositionDeg(int id)
 * void defineStatus(int id)
 *
 * String buildDisplayMoveString(int id)
 * void displayMove(int id)
 * String buildBacklashString(int id)
 * String buildSimCurrentPosVelString(int id)
 *
 * test functions -
 * void displayMove(int id)
 * void testServos()
 * void testServo(int id)
 * void testSerialOutput()
 * void test()
 *
 * gearing discussion:
 *
 * Pittman (www.pittmannet.com) servo motor geared 5.9:1 with 512 counts decoded to 2048 counts per revolution yields 12083.2 counts/rev;
 * for step size of 1 arcsec, 107:1 further reduction needed;
 * pulley has 28 teeth with 0.080" circular pitch (or spacing) for pitch dia=0.713" (OD=0.693") (space for 0.375" wide belt);
 * similar pulleys at Berg's:
 *    http://www.wmberg.com/catalog/catresultsmain.cfm?catpage=b00a125a (aluminum hubs, teeth counts to 120 [120:28~4.3:1)
 * timing belts to match: http://www.wmberg.com/catalog/catresults.cfm?CatType=BELTS&CatGroup=TIMING%20BELTS
 *
 * specific discussion with 20" f/4.8 where main drive rims have dia=36":
 * a.ratios are 38:.5 (main rim reduction), 5.9:1 (gear reducer on motor), 2048:1 (motor encoder resolution), 4:1 additional reduction,
 *   resulting in 0.35 arcsec per step (max slew based on observed max speed with 12volt input to servo drive unit ~ 9deg/sec)
 * b.non-geared motor (Pittman 9234S005, 12 volts, 500 cpr) with 38" dia rim and 1/2x13 molded threads for threaded rod drive:
 *   2000 counts per rev yields 648x further reduction required for 1 arcsec step size; 1552 threads around the drive rim yields
 *   0.43" step size - (slew speed ~ 11eg/sec)
 *
 *
 * servo control notes:
 *
 * PID stands for Proportional-Integral-Derivative.
 * This is a type of feedback controller whose output is generally based on the error between some
 * user-defined set point (sp) and some measured process variable (PV).
 * Each element of the PID controller refers to a particular action taken on the error:
 * Proportional: error multiplied by a gain, Kp. This is an adjustable amplifier. in many systems Kp is
 * responsible for process stability: too low and the PV can drift away; too high and the PV can oscillate.
 * Integral: the integral (error correction accumulated over time) of error multiplied by a gain, Ki. in many
 * systems Ki is responsible for driving error to zero, but to set Ki too high is to invite oscillation or
 * instability or integrator windup or actuator saturation.
 * Derivative: the rate of change of error multiplied by a gain, Kd. in many systems Kd is responsible
 * for system response: too low and the PV will oscillate by overshooting; too high and the PV will respond
 * sluggishly. The designer should also note that derivative action amplifies any noise in the error signal.
 *
 * tuning PID:
 * 1. set position gain Kp and integral gain Ki to 0; keep increasing derivative gain Kd until motor
 * starts to hum, then back off a little bit; motor should feel more sluggish as value for Kd is
 * increased
 * 2. with Kd set at this maximal value, start increasing Kp and commmanding test motions until the
 * motor starts to overshoot the goal, then back off a little; test motions should be small motions
 * with large acceleration and velocity, causing trapezoidal profiling to jump to goal position in a
 * single tick, giving the true step response of the motor
 * 3. depending on dynamics of system, the motor may have a steady state error with Kp and Kd set as
 * above; if so, first set value for IL of 16000 then start increassing the value of Ki until the
 * steady state error is reduced to an acceptable level within an acceptable time; increasing Ki will
 * typically introduce some overshoot in the position; best value for Kp will be some compromise
 * between overshoot and settling time
 * 4. finally reduce value of IL to minimum which will still cancel out any steady state error
 *
 * Pittman servo motor wiring:
 *
 * Pittman motor wiring to DB9:
 * motor color scheme: red=+vdc, black=ground, green=encoder signal channel a, yellow=encoder signal channel b
 * DB9#s: 1 = n.c. 2,3=motor black 4,5=motor red 6=encoder black 7=encoder red 8=encoder yellow 9=encoder blue/green
 *
 * Mel's wiring to connect to Gary Myer's board(s)
 *
 * DB9 to RJ45 adapter for motor:
 *    DB9 to RJ45:
 *    DB9 pinout:      RJ45 female showing DB9 pin#s: (face-on, clip down, cable to rear):
 *    --------------   -------------------
 *    \ 5 4 3 2 1 /    | 9 8 7 6 5 4 3 2 | (blue,orange,black,red,green,yellow,brown,white)
 *     \ 9 8 7 6 /     -------     -------
 *      --------              |||||
 * with clip up and cable to rear:
 * DB9#:               1     2      3      4       5      6          7        8           9
 * rj45 lead colors:   n.c.  white  brown  yellow  green  red        black    orange      blue
 * motor:                    motor red     motor black    enc black  end red  enc yellow  enc blue/green
 *                           m+            m-             e-         e+       eB          eA
 * DB9 labeled by motor leads:
 * -------------------
 * \ m- m- m+ m+ nc /
 *  \ eA eB e+ e- /
 *   ------------
 *
 * RJ45 keystone modular jack:
 * RJ45 female pin #s, face-on, clip down, cable to rear:
 * (one view: middle pair 5,4 to lower left, middle outside pair 6,3 to upper right, outside pair 2,1 to upper left reversed,
 *  outside pair 8,7 to lower right;
 *  next view: ethernet pairing 1,2 to upper left, 6,3 to upper right)
 * --------   -------------------
 * | 2  3 |   | 8 7 6 5 4 3 2 1 |
 * | 1  6 |   -------     -------
 * | 4  7 |          |||||
 * | 5  8 |
 * ---  ---
 *    ||
 *
 * RJ45 keystone modular jack:
 * RJ45 male pin #s, face-on, clip down, cable to rear:
 * --------   -------------------
 * | 7  6 |   | 1 2 3 4 5 6 7 8 |
 * | 8  3 |   -------     -------
 * | 5  2 |          |||||
 * | 4  1 |
 * ---  ---
 *    ||
 *
 * DB9#s:    motor leads:  RJ45 female showing DB9 pin#s:
 *                         (face-on, clip down, cable to rear) (as above in the DB9 to RJ45 adaptor for motor):
 * --------   ----------   -------------------
 * | 3  4 |   | m+  m- |   | 9 8 7 6 5 4 3 2 |
 * | 2  7 |   | m+  e+ |   -------     -------
 * | 5  8 |   | m-  eB |          |||||
 * | 6  9 |   | e-  eA |
 * ---  ---   ----  ----
 *    ||          ||
 *
 * encoder wire pinout for PIC SERVO PCB:
 * on servo:
 *    chnB, +5vdc, chnA, n.c., ground
 *    yellow, red, blue/green, black
 * PCB as viewed from underneath:
 *       red     black
 * yellow    blue/green
 *
 * Gary Myers prototype board servo motor encoder RJ11:
 * RJ11 male face-on, clip down, cable to rear:
 * -----------   ---------------
 * | 1 2 3 4 |   | e- eA e+ eB |
 * ----   ----   ------   ------
 *    |||             |||
 *
 * Gary Myers prototype board:
 *    altitude/declination motor is main board, azimuth/right ascension motor is 2nd board;
 *    black servo motor lead closest to narrow edge, red servo motor lead furthest from narrow edge;
 *    encoder ports at far end of boards;
 *    handpad port on 2nd board in middle;
 *
 * Be careful to get the polarity right on the connectors - there is no reverse polarity protection.
 * The center pin is + on both - the outer shell being 0V. If any doubt pull the fuses and check the
 * voltages on the fuse holder relative to the tab on any of the large TO devices (0V common).
 * The fuses are 1/4A for the digital supply (FS1) and 5A for the servo supply (FS2).
 *
 * There are switchers on both - the 5V supply will draw (from 12V) ~ 45% of its requirement.
 * The servo is opposite - big time! The pot on there allows for a low end supply of about 18V
 * (counterclockwise to reduce). At this setting the 12V requirement will be ~1.6x the output and the
 * max servo current would be ~2A. At the high setting on the pot (full CW) the voltage is ~39V - the
 * current from the 12V line will be ~3.6x the servo current. This is the limit of the system. At this
 * setting the servo current is limited to ~1A and then, if continuous, it would probably shut off due
 * to thermals in due time (a long time and telescope drive "type" of usage is not of this nature so it
 * is highly unlikely this will happen).
 *
 * The Thermal output from the 18200 amp is tied to the limit2 input. If the limit2 goes low then the
 * Over Travel limit has been hit and used OR the thermal limit on the output amp has been triggered
 * and that axis should be turned off.
 *
 * Never enter Positional move called trapezoidal profiling while motor is moving;
 * never change acceleration, target velocity, or target position while in Positional move;
 * if necessary to stop a Positional move, switch to velocity profiling then command motor to stop.
 *
 * Notes about sending and receiving values when broken into individual bytes:
 *
 * bytes are read/written in low to high byte order
 * initially make all bytes positive then after bytes assembled into a value -
 * if return value >= 1/2 the max amount, then negative value was sent; make it negative by subtracting the max amount
 * if writing a negative value, bring value into upper half of positive space by adding the max amount to it
 * ie, mapping is 2  2   ie 2  2   so map by adding or subtracting max amount
 *                1  1      1  1   ie, in last example, if return value is 4, then - 6, giving true value of -2
 *                0  0      0  0   boundary to start mapping is 1/2 of 6 = 3
 *              255 -1      5 -1
 *              254 -2      4 -2
 *              253 -3      3 -3
 *              252 -4
 *              251 -5 <- this is equivalent to twos-compliment (invert all the bits, then add 1, ie, 5=0101, invert=1010,
 *                        twos-complement=(add 1)=1011 or 251 in 8 bit space
 *
 * Move state:
 *    uses         STATUS_BYTE_MOVE_DONE, AUX_STATUS_BYTE_ACCEL_DONE, AUX_STATUS_BYTE_SLEW_DONE
 * velocity move:
 *    ramping up         false
 *    at max vel         TRUE
 * position move:
 *    ramping up         false                  false
 *    at max vel         false                  TRUE                   false
 *    ramping down       false                  TRUE                   TRUE
 *    finished           TRUE                   TRUE                   TRUE
 *
 * Serial output of servo constructor and servo close functions:
 *
 * (possible return to clear java serial read buffer   0xd)
 * clear pic servo input buffer              0  0  0  0  0  0  0  0  0  0  0  0  0  0  0  0
 * hard reset      0xaa  0xff  0xf   0xe
 * set address 0   0xaa  0     0x21  0x1   0xff  0x21
 * set address 1   0xaa  0     0x21  0x2   0xff  0x22
 * define status 0 0xaa  0x1   0x12  0x2f  0x42
 * set gain 0      0xaa  0x1   0xe6  0xc8  0     0xbc  0x2  0xc8  0  0xbc  0x2  0xff  0xff  0  0x7d  0x1  0  0x6f
 * stop motor 0    0xaa  0x1   0x17  0x2   0x1a
 * define status 1 0xaa  0x2   0x12  0x2f  0x43
 * set gain 1      0xaa  0x2   0xe6  0xc8  0     0xbc  0x2  0xc8  0  0xbc  0x2  0xff  0xff  0  0x7d  0x1  0  0x70
 * stop motor 1    0xaa  0x2   0x17  0x2   0x1b
 *
 * output for load trajectory velocity command sequence:
 * get status:    0xaa  0x1  0xe  0xf
 * clear sticky bits:   0xaa  0x1  0xb  0xc
 * load trajectory velocity:   0xaa  0x1  0x94  0x36  0x40  0x42  0xf  0  0x6a  0x2  0  0  0xc8
 *
 *reversing motor direction handled by flipping position read in readStatusSubr(), flipping goto
 *position in loadTrajectoryPosition(), and flipping target velocity in loadTrajectoryVel();
 *
 */
public class servo extends handpad {
    static final byte HEADER_BYTE = (byte) 0xAA;
    static final byte GROUP_ADDRESS = (byte) 0xFF;
    
    static final byte SEND_POSITION = (byte) 0x01;
    static final byte SEND_AD = (byte) 0x02;
    static final byte SEND_ACTUAL_VEL = (byte) 0x04;
    static final byte SEND_AUX_STATUS = (byte) 0x08;
    static final byte SEND_HOME_POSITION = (byte) 0x10;
    static final byte SEND_DEVICE_ID = (byte) 0x20;
    
    static final byte READ_STATUS = (byte) 0x40;
    static final byte READ_CHECKSUM = (byte) 0x80;
    
    static final byte LOAD_POSITION = (byte) 0x01;
    static final byte LOAD_VEL = (byte) 0x02;
    static final byte LOAD_ACCEL = (byte) 0x04;
    static final byte SET_MVMT_MODE = (byte) 0x10;
    static final byte TRAPEZOIDAL_PROFILE = (byte) 0x00;
    static final byte VEL_PROFILE = (byte) 0x20;
    static final byte FORWARD_DIR = (byte) 0x00;
    static final byte REVERSE_DIR = (byte) 0x40;
    static final byte START_MOTION_LATER = (byte) 0x00;
    static final byte START_MOTION_NOW = (byte) 0x80;
    
    static final byte MOTOR_POWERED = (byte) 0x00;
    static final byte AMP_ENABLE = (byte) 0x01;
    static final byte TURN_MOTOR_OFF = (byte) 0x02;
    static final byte STOP_ABRUPTLY = (byte) 0x04;
    static final byte STOP_SMOOTHLY = (byte) 0x08;
    
    static final byte CMD_BYTE = (byte) 0x02;
    
    // following used by class servoCmdTranslator so don't mark them private
    static final byte CMD_SET_ADDRESS = (byte) 0x21;
    static final byte CMD_DEFINE_STATUS = (byte) 0x12;
    static final byte CMD_NOP = (byte) 0x0E;
    static final byte CMD_HARD_RESET = (byte) 0x0F;
    static final byte CMD_RESET_POSITION = (byte) 0x00;
    static final byte CMD_SET_GAIN = (byte) 0xE6;
    static final byte CMD_START_MOTOR = (byte) 0x05;
    static final byte CMD_STOP_MOTOR = (byte) 0x17;
    static final byte CMD_CLEAR_STICKY_BITS = (byte) 0x0B;
    static final byte CMD_LOAD_TRAJECTORY_POSITION = (byte) 0xD4;
    static final byte CMD_LOAD_TRAJECTORY_VEL = (byte) 0x94;
    
    static final byte STATUS_BYTE_MOVE_DONE = (byte) 0x01;
    static final byte STATUS_BYTE_CKSUM_ERR = (byte) 0x02;
    static final byte STATUS_BYTE_OVER_CURRENT = (byte) 0x04;
    static final byte STATUS_BYTE_POWER_ON = (byte) 0x08;
    static final byte STATUS_BYTE_POS_ERR = (byte) 0x10;
    static final byte STATUS_BYTE_LIMIT1 = (byte) 0x20;
    static final byte STATUS_BYTE_LIMIT2 = (byte) 0x40;
    static final byte STATUS_BYTE_HOME_IN_PROGRESS = (byte) 0x80;
    
    static final byte AUX_STATUS_BYTE_INDEX = (byte) 0x01;
    static final byte AUX_STATUS_BYTE_POS_WRAP = (byte) 0x02;
    static final byte AUX_STATUS_BYTE_SERVO_ON = (byte) 0x04;
    static final byte AUX_STATUS_BYTE_ACCEL_DONE = (byte) 0x08;
    static final byte AUX_STATUS_BYTE_SLEW_DONE = (byte) 0x10;
    static final byte AUX_STATUS_BYTE_TIMER_OVERRUN = (byte) 0x20;
    
    // extensions for SiTech controller
    // odd address sends motor supply voltage (*10 to get voltage);
    // even address sends cpu chip temp in F;
    static final byte READ_MOTOR_SUPPLY_VOLTAGE_OR_CPU_TEMP = (byte) 0x40;
    static final byte DISABLE_DAN_GRAY_HANDPAD = (byte) 0x80;
    
    static final byte CMD_EXT_NOP_CONTROLLER = (byte) 0x1E;
    static final byte CMD_EXT_NOP_X_BITS = (byte) 0x2E;
    static final byte CMD_EXT_NOP_SCOPE_ENCODER = (byte) 0x5E;
    static final byte CMD_EXT_NOP_GUIDE_SPEED = (byte) 0x5E;
    static final byte CMD_EXT_NOP_HANDPAD_SLEW_PAN_SPEEDS = (byte) 0x9E;
    
    static final byte WRITE_SCOPE_ENCODER = (byte) 0x01;
    static final byte WRITE_X_BITS = (byte) 0x02;
    static final byte READ_CFG_FROM_FLASH = (byte) 0x04;
    static final byte WRITE_CFG_FROM_FLASH = (byte) 0x08;
    static final byte USE_SAVE_FACTORY_DEFAULTS = (byte) 0x10;
    static final byte SWITCH_TO_ASCII_MODE = (byte) 0x20;
    static final byte WRITE_HANDPAD_SLEW_PAN_SPEEDS = (byte) 0x20;
    static final byte WRITE_GUIDE_SPEED = (byte) 0x40;
    
    static final byte INVERT_SERVO_ENCODER = (byte) 0x01;
    static final byte INVERT_MOTOR_DIR = (byte) 0x02;
    static final byte INVERT_SCOPE_ENCODER = (byte) 0x04;
    static final byte DRAG_N_DROP = (byte) 0x08;              // x only
    static final byte SLEW_N_DROP = (byte) 0x08;              // Y only
    static final byte PLATFORM_MODE = (byte) 0x10;            // x only
    static final byte PEC_SYNC_AZ = (byte) 0x10;              // Y only
    static final byte PEC_SYNC_ALT = (byte) 0x20;             // Y only
    static final byte ENABLE_HANDPAD = (byte) 0x20;           // x only
    static final byte SIMUL_DIR_HANDPAD_DESIGN = (byte) 0x40; // x only
    static final byte GUIDE_MODE = (byte) 0x80;               // x only
    
    static final int SITECH_VERSION = 255;
    // read from version byte
    int SiTechXbits;
    int SiTechYbits;
    int lastSiTechXbits;
    int lastSiTechYbits;
    
    boolean displayCmd;
    boolean displayOutBuf;
    boolean displayStatus;
    boolean displayStickyBits;
    boolean displayBadStatusReturn;
    
    private static final int MAX_OUT_BUF_IX = 79;
    private int outBufIx;
    private byte outBuf[] = new byte[MAX_OUT_BUF_IX+1];
    logServoCmdResults lscr = new logServoCmdResults();
    
    String statusReturnString;
    String stickyBitsString;
    String displayMoveString;
    String guidePECString;
    String backlashString;
    String simCurrentPosVelString;
    
    boolean portOpened;
    IO io;
    SiTechAsciiCmd SiTechAsciiCmd;
    
    servo() {
        SiTechAsciiCmd = new SiTechAsciiCmd();
        if (openPort()) {
            io.displayReceivedChar(false);
            //io.displayReceivedChar(true);
            //io.displayReceivedCharAsHex(true);
            displayStatus = false;
            displayBadStatusReturn = true;
            displayOutBuf = false;
            displayStickyBits = false;
            startControllers();
        }
    }
    
    boolean startControllers() {
        int id;
        
        if (portOpened == true) {
            initHandpad();
            // fill up any partially filled command buffers
            for (outBufIx = 0; outBufIx < 16; outBufIx++)
                outBuf[outBufIx] = (byte) 0x00;
            io.writeByteArrayPauseUntilXmtFinished(outBuf, outBufIx);
            astroTime.getInstance().wait(cfg.getInstance().servoPortWaitTimeMilliSecs);
            // flush any incoming bytes from receive buffer
            while (io.readSerialBuffer())
                ;
            for (id = 0; id < SERVO_ID.size(); id++)
                initServoIDVars(id);
            displayCmd = true;
            initServoID();
            if (cfg.getInstance().controllerManufacturer == CONTROLLER_MANUFACTURER.SiTech)
                initSiTechHandpadSlewPanGuideSpeeds();
            displayCmd = false;
            return true;
        }
        return false;
    }
    
    boolean openPort() {
        ioFactory iof = new ioFactory();
        
        iof.args.serialPortName = cfg.getInstance().servoSerialPortName;
        iof.args.baudRate = cfg.getInstance().servoBaudRate;
        iof.args.homeIPPort = cfg.getInstance().servoHomeIPPort;
        iof.args.remoteIPName = cfg.getInstance().servoRemoteIPName;
        iof.args.remoteIPPort = cfg.getInstance().servoRemoteIPPort;
        iof.args.fileLocation = cfg.getInstance().servoFileLocation;
        iof.args.trace = cfg.getInstance().servoTrace;
        
        io = iof.build(cfg.getInstance().servoIOType);
        if (io == null) {
            portOpened = false;
            console.errOut("could not open communications port to servo(s)");
        } else
            portOpened = true;
        
        SiTechAsciiCmd.registerIo(io);
        
        return portOpened;
    }
    
    void calcDriftArcsecPerMin() {
        int id;
        
        for (id = 0; id < SERVO_ID.size(); id++)
            cfg.getInstance().servoParm[id].driftArcsecPerMin = cfg.getInstance().servoParm[id].driftRadMin * units.RAD_TO_ARCSEC;
    }
    
    void close() {
        int id;
        
        stopAllMotorsSmoothly();
        waitForAllMotorsStop();
        // if SiTech controller, switch to ASCII mode and do not disable amp so that controller can continue operation
        if (cfg.getInstance().controllerManufacturer == CONTROLLER_MANUFACTURER.SiTech)
            extNOPSwitchToAsciiMode();
        else
            disableAmpAllMotors();
        
        calcDriftArcsecPerMin();
        
        for (id = 0; id < SERVO_ID.size(); id++) {
            cfg.getInstance().servoParm[id].guideArcsecSec = cfg.getInstance().servoParm[id].guideRadSec * units.RAD_TO_ARCSEC;
            cfg.getInstance().servoParm[id].stepsPerRev = units.ONE_REV / cfg.getInstance().servoParm[id].countToRad;
            cfg.getInstance().servoParm[id].fastSpeedDegSec = cfg.getInstance().servoParm[id].fastSpeedRadSec * units.RAD_TO_DEG;
            cfg.getInstance().servoParm[id].slowSpeedArcsecSec = cfg.getInstance().servoParm[id].slowSpeedRadSec * units.RAD_TO_ARCSEC;
            cfg.getInstance().servoParm[id].homeDeg = cfg.getInstance().servoParm[id].homeRad * units.RAD_TO_DEG;
            cfg.getInstance().servoParm[id].sectorDeg = cfg.getInstance().servoParm[id].sectorRad * units.RAD_TO_DEG;
            cfg.getInstance().servoParm[id].backlashArcmin = cfg.getInstance().servoParm[id].backlashRad * units.RAD_TO_ARCMIN;
            // not necessary to call setCurrentPositionDeg() as it is called in readStatus() called eventually in waitForAllMotorsStop()
        }
        
        if (portOpened) {
            console.stdOutLn("closing servo serial port...");
            io.close();
            portOpened = false;
            SiTechAsciiCmd.portOpened(false);
        }
        
        lscr.saveToFile();
    }
    
    void getStatusAll() {
        int id;
        
        for (id = 0; id < SERVO_ID.size(); id++)
            if (cfg.getInstance().servoParm[id].controllerActive)
                getStatus(id);
    }
    
    void stopMotorSmoothly(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        sp.moveCmd = SERVO_MOVE_CMD.stopMoveSmoothly;
        sp.servoCmdProcessed = false;
        processMoveCmdControl();
    }
    
    void stopAllMotorsSmoothly() {
        int id;
        
        for (id = 0; id < SERVO_ID.size(); id++) {
            cfg.getInstance().servoParm[id].moveCmd = SERVO_MOVE_CMD.stopMoveSmoothly;
            cfg.getInstance().servoParm[id].servoCmdProcessed = false;
        }
        processMoveCmdControl();
    }
    
    void enableAmpAllMotors() {
        int id;
        
        for (id = 0; id < SERVO_ID.size(); id++) {
            cfg.getInstance().servoParm[id].moveCmd = SERVO_MOVE_CMD.turnMotorOnAmpOn;
            cfg.getInstance().servoParm[id].servoCmdProcessed = false;
        }
        processMoveCmdControl();
    }
    
    void disableAmpAllMotors() {
        int id;
        
        for (id = 0; id < SERVO_ID.size(); id++) {
            cfg.getInstance().servoParm[id].moveCmd = SERVO_MOVE_CMD.disableAmp;
            cfg.getInstance().servoParm[id].servoCmdProcessed = false;
        }
        processMoveCmdControl();
    }
    
    /**
     * eg, processMoveCmdControl() repeatedly calls processMoveCmd(id) which in turn calls getStatus(id),
     *     later in processMoveCmd(id), switch statement case .waitForStop checks for cfg.getInstance().servoParm[id].motorStopped,
     *     which if so, sets cfg.getInstance().servoParm[id].servoCmdProcessed = true which causes processMoveCmdControl() to end
     */
    void waitForMotorStop(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        sp.moveCmd = SERVO_MOVE_CMD.waitForStop;
        sp.servoCmdProcessed = false;
        processMoveCmdControl();
    }
    
    void waitForAllMotorsStop() {
        int id;
        
        for (id = 0; id < SERVO_ID.size(); id++) {
            cfg.getInstance().servoParm[id].moveCmd = SERVO_MOVE_CMD.waitForStop;
            cfg.getInstance().servoParm[id].servoCmdProcessed = false;
        }
        processMoveCmdControl();
    }
    
    void initServoIDVars(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        if (sp.ampEnableActiveHigh) {
            sp.ampOnValue = AMP_ENABLE;
            sp.ampOffValue = 0;
        } else {
            sp.ampOnValue = 0;
            sp.ampOffValue = AMP_ENABLE;
        }
        // max allowed is 0x3FFF
        if (sp.positionErrorLimitEL > 16383)
            sp.positionErrorLimitEL = 16383;
        sp.driftRadMin = sp.driftArcsecPerMin * units.ARCSEC_TO_RAD;
        sp.accumDriftRad = 0;
        sp.guideRadSec = sp.guideArcsecSec * units.ARCSEC_TO_RAD;
        sp.accumGuideRad = 0;
        sp.countToRad = units.ONE_REV / sp.stepsPerRev;
        sp.fastSpeedRadSec = sp.fastSpeedDegSec * units.DEG_TO_RAD;
        sp.slowSpeedRadSec = sp.slowSpeedArcsecSec * units.ARCSEC_TO_RAD;
        sp.velRadSec = 0.;
        sp.homeRad = sp.homeDeg * units.DEG_TO_RAD;
        sp.sectorRad = sp.sectorDeg * units.DEG_TO_RAD;
        sp.currentPositionOffsetRad = sp.currentPositionDeg*units.DEG_TO_RAD - (double) sp.actualPosition * sp.countToRad;
        sp.lastActualPosition = sp.actualPosition;
        sp.moveState = SERVO_MOVE_STATE.finished;
        sp.moveCmd = SERVO_MOVE_CMD.noMove;
        sp.servoCmdProcessed = true;
        sp.lastMoveCmd = SERVO_MOVE_CMD.noMove;
        sp.trajectoryStyle = TRAJECTORY_STYLE.velocity;
        sp.targetVel = 0;
        sp.targetVelDir = ROTATION.no;
        sp.lastTargetVel = 0;
        sp.lastTargetVelDir = ROTATION.no;
        sp.cmdDevice = CMD_DEVICE.none;
        sp.acceleration = (long) (sp.accelDegSecSec * sp.accelDegSecSecToCountsTickTick());
        sp.longRangeAccel = sp.acceleration;
        sp.shortRangeAccel = (long) (SERVO_PARMS.SHORT_RANGE_ACCEL_FACTOR * (double) sp.longRangeAccel);
        sp.backlashRad = sp.backlashArcmin * units.ARCMIN_TO_RAD;
        sp.backlashCount = (long) (sp.backlashRad / sp.countToRad);
        // assume that backlash is always taken up in CW direction when starting
        sp.actualBacklashCount = 0;
        sp.actualBacklashRad = sp.actualBacklashCount * sp.countToRad;
        sp.lastLimit2 = sp.limit2;
        sp.readStatusCount = 0;
        sp.readStatusSuccessfulCount = 0;
        sp.consecutiveUnsuccessfulReadStatusCount = 0;
    }
    
    void initServoID() {
        int id;
        
        hardReset();
        for (id = 0; id < SERVO_ID.size(); id++)
            if (cfg.getInstance().servoParm[id].controllerActive) {
            cfg.getInstance().servoParm[id].lastDefinedStatus = 0;
            setAddress(id);
            }
        defineAllStatus();
    }
    
    void defineAllStatus() {
        int id;
        
        for (id = 0; id < SERVO_ID.size(); id++)
            if (cfg.getInstance().servoParm[id].controllerActive)
                defineStatus(id);
    }
    
    void defineStatus(int id) {
        byte bit6 = 0x00;
        byte bit7 = 0x00;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        /**
         * if SiTech controller, ADValue can optionally be either motor voltage (*10) or cpu temp in F;
         */
        if (cfg.getInstance().controllerManufacturer==CONTROLLER_MANUFACTURER.SiTech
                && sp.ADValueIsMotorVoltOrCPUTemp)
            bit6 = READ_MOTOR_SUPPLY_VOLTAGE_OR_CPU_TEMP;
        /**
         * if SiTech controller, switch between active control of handpad and merely observing controller's
         * operation of handpad based on passiveObserver() value;
         * normal operation is for SiTech controller to operate the handpad and consequently for bit7 to be set;
         */
        if (id==SERVO_ID.altDec.KEY
                && cfg.getInstance().controllerManufacturer==CONTROLLER_MANUFACTURER.SiTech
                && cfg.getInstance().handpadPresent
                && cfg.getInstance().handpadDesign==HANDPAD_DESIGN.handpadDesignSiTech
                && !HandpadDesigns.passiveObserver())
            bit7 = DISABLE_DAN_GRAY_HANDPAD;
        
        defineStatus(id, (byte) (SEND_POSITION
                + SEND_AD
                + SEND_ACTUAL_VEL
                + SEND_AUX_STATUS
                + SEND_HOME_POSITION
                + SEND_DEVICE_ID
                + bit6
                + bit7
                ));
        setGain(id);
        // turn on servo amplifier;
        // not stopMotor(id, TURN_MOTOR_OFF); as SiTech's controller expects the motors be powered up
        // for handpad operation via the controller;
        stopMotor(id, MOTOR_POWERED);
    }
    
    void setCurrentPositionDeg(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        sp.currentPositionDeg = units.RAD_TO_DEG * ((double) sp.actualPosition * sp.countToRad + sp.currentPositionOffsetRad);
    }
    
    void readStatus(int id) {
        if (io != null)
            readStatusSubr(id);
    }
    
    private void readStatusSubr(int id) {
        int ix;
        byte checksum = 0;
        long holdLong;
        int holdInt;
        int i;
        long l;
        int expectedBytesReturned;
        double tDiffRad;
        long posDiff;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        sp.lastStatusSidT = sp.statusSidT;
        sp.statusSidT = astroTime.getInstance().calcSidT();
        tDiffRad = sp.statusSidT - sp.lastStatusSidT;
        tDiffRad = eMath.validRad(tDiffRad);
        
        // status byte and checksum byte always returned
        expectedBytesReturned = 2;
        if ((sp.lastDefinedStatus & SEND_POSITION) == SEND_POSITION)
            expectedBytesReturned += 4;
        if ((sp.lastDefinedStatus & SEND_AD) == SEND_AD)
            expectedBytesReturned += 1;
        if ((sp.lastDefinedStatus & SEND_ACTUAL_VEL) == SEND_ACTUAL_VEL)
            expectedBytesReturned += 2;
        if ((sp.lastDefinedStatus & SEND_AUX_STATUS) == SEND_AUX_STATUS)
            expectedBytesReturned += 1;
        if ((sp.lastDefinedStatus & SEND_HOME_POSITION) == SEND_HOME_POSITION)
            expectedBytesReturned += 4;
        if ((sp.lastDefinedStatus & SEND_DEVICE_ID) == SEND_DEVICE_ID)
            expectedBytesReturned += 2;
        
        // to ensure that the wait occurs for at least one 'time period' of servoPortWaitTimeMilliSecs,
        // make the serial read wait for two time period transitions
        io.waitForReadBytes(expectedBytesReturned, 2*cfg.getInstance().servoPortWaitTimeMilliSecs);
        
        // set starting state of flags after wait period expires so that concurrent threads do not read and
        // falsely report flags during wait period before they have a chance to be set properly later on
        
        // these are sticky bit errors that could be displayed as error messages
        sp.posError = false;
        sp.overCurrent = false;
        sp.timerOverrun = false;
        sp.posWrap = false;
        
        sp.cksumError = false;
        
        sp.readStatus = 0;
        sp.bytesReturned = 0;
        sp.handpadPortsRead = false;
        sp.receiveChecksumOK = false;
        sp.receiveChecksum = 0;
        sp.unexpectedReceiveBytes = 0;
        sp.moveState = SERVO_MOVE_STATE.unknown;
        sp.readStatusSuccessful = false;
        
        if (expectedBytesReturned == io.countReadBytes()) {
            if (io.readSerialBuffer()) {
                checksum += io.returnByteRead();
                sp.bytesReturned++;
                sp.readStatus = READ_STATUS;
                sp.statusByte = io.returnByteRead();
                
                if ((sp.statusByte & STATUS_BYTE_MOVE_DONE) == STATUS_BYTE_MOVE_DONE)
                    sp.moveDone = true;
                else
                    sp.moveDone = false;
                
                if ((sp.statusByte & STATUS_BYTE_CKSUM_ERR) == STATUS_BYTE_CKSUM_ERR)
                    sp.cksumError = true;
                else
                    sp.cksumError = false;
                
                if ((sp.statusByte & STATUS_BYTE_OVER_CURRENT) == STATUS_BYTE_OVER_CURRENT)
                    sp.overCurrent = true;
                else
                    sp.overCurrent = false;
                
                if ((sp.statusByte & STATUS_BYTE_POWER_ON) == STATUS_BYTE_POWER_ON)
                    sp.powerOn = true;
                else
                    sp.powerOn = false;
                
                if ((sp.statusByte & STATUS_BYTE_POS_ERR) == STATUS_BYTE_POS_ERR)
                    sp.posError = true;
                else
                    sp.posError = false;
                
                if ((sp.statusByte & STATUS_BYTE_LIMIT1) == STATUS_BYTE_LIMIT1)
                    sp.limit1 = true;
                else
                    sp.limit1 = false;
                
                if ((sp.statusByte & STATUS_BYTE_LIMIT2) == STATUS_BYTE_LIMIT2)
                    sp.limit2 = true;
                else
                    sp.limit2 = false;
                
                if ((sp.statusByte & STATUS_BYTE_HOME_IN_PROGRESS) == STATUS_BYTE_HOME_IN_PROGRESS)
                    sp.homeInProgress = true;
                else
                    sp.homeInProgress = false;
                
                if ((sp.statusByte & STATUS_BYTE_MOVE_DONE) == STATUS_BYTE_MOVE_DONE)
                    sp.moveState = SERVO_MOVE_STATE.finished;
                else
                    if (sp.trajectoryStyle == TRAJECTORY_STYLE.position)
                        sp.moveState = SERVO_MOVE_STATE.posMoveStarted;
                    else
                        sp.moveState = SERVO_MOVE_STATE.velMoveRamp;
                
                // detect low to high transition or high to low transition depending on setup
                if ((sp.autoPECSyncDetect == AUTO_PEC_SYNC_DETECT.lowHigh
                        && !sp.lastLimit2 && sp.limit2)
                        || (sp.autoPECSyncDetect == AUTO_PEC_SYNC_DETECT.highLow
                        && sp.lastLimit2 && !sp.limit2))
                    sp.PECSyncReady = true;
                
                sp.lastLimit2 = sp.limit2;
                
                // send position block:
                // return 4 bytes into a long value; order is low to high; if value >= 1/2 the max amount, then value is
                // negative and should have max amount subtracted
                if ((sp.lastDefinedStatus & SEND_POSITION) == SEND_POSITION) {
                    holdLong = sp.actualPosition;
                    sp.actualPosition = 0;
                    for (ix = 0; ix < 4 && io.readSerialBuffer(); ix++) {
                        checksum += io.returnByteRead();
                        sp.bytesReturned++;
                        l = io.returnByteRead();
                        if (l < 0)
                            l+=256;
                        sp.actualPosition += l * eMath.longPow(256, ix);
                    }
                    if (ix == 4) {
                        sp.readStatus += SEND_POSITION;
                        // if position actually negative
                        if (sp.actualPosition >= 2147483648L)
                            sp.actualPosition -= 4294967296L;
                        if (sp.reverseMotor)
                            sp.actualPosition = -sp.actualPosition;
                        setCurrentPositionDeg(id);
                        sp.actualPositionDeg = sp.actualPosition*sp.countToRad*units.RAD_TO_DEG;
                        sp.lastActualPosition = holdLong;
                        posDiff = sp.actualPosition - sp.lastActualPosition;
                        if (posDiff == 0) {
                            sp.motorStopped = true;
                            sp.PICServoSimList.addResetPos(astroTime.getInstance().JD,
                                    sp.actualPosition * sp.countToRad);
                        } else
                            sp.motorStopped = false;
                        
                        // can be negative or positive value, unlike velRadSec which can only be positive
                        sp.avgVelRadSec = (double) posDiff*sp.countToRad / (tDiffRad*units.RAD_TO_SEC);
                        // backlash takeup
                        // max backlash in CCW direction = backlashCount, max backlash in CW direction = 0
                        sp.actualBacklashCount -= posDiff;
                        if (sp.actualBacklashCount < 0)
                            sp.actualBacklashCount = 0;
                        if (sp.actualBacklashCount > sp.backlashCount)
                            sp.actualBacklashCount = sp.backlashCount;
                        sp.actualBacklashRad = sp.actualBacklashCount * sp.countToRad;
                    } else {
                        sp.actualPosition = holdLong;
                        if (displayBadStatusReturn && displayStatus)
                            console.errOut("did not receive actualPosition for " + SERVO_ID.matchKey(id));
                    }
                }
                
                if ((sp.lastDefinedStatus & SEND_AD) == SEND_AD)
                    if (io.readSerialBuffer()) {
                    checksum += io.returnByteRead();
                    sp.bytesReturned++;
                    sp.readStatus += SEND_AD;
                    sp.ADValue = io.returnByteRead();
                    } else
                        if (displayBadStatusReturn && displayStatus)
                            console.errOut("did not receive SEND_AD for " + SERVO_ID.matchKey(id));
                
                // return 2 bytes into a long value; order is low to high; take into account negative value
                if ((sp.lastDefinedStatus & SEND_ACTUAL_VEL) == SEND_ACTUAL_VEL) {
                    holdInt = sp.actualVel;
                    sp.actualVel = 0;
                    for (ix = 0; ix < 2 && io.readSerialBuffer(); ix++) {
                        checksum += io.returnByteRead();
                        sp.bytesReturned++;
                        i = io.returnByteRead();
                        if (i < 0)
                            i+=256;
                        sp.actualVel += i * eMath.intPow(256, ix);
                    }
                    if (ix == 2) {
                        sp.readStatus += SEND_ACTUAL_VEL;
                        if (sp.actualVel >= 32768)
                            sp.actualVel -= 65536;
                    } else
                        sp.actualVel = holdInt;
                }
                
                if ((sp.lastDefinedStatus & SEND_AUX_STATUS) == SEND_AUX_STATUS)
                    if (io.readSerialBuffer()) {
                    checksum += io.returnByteRead();
                    sp.bytesReturned++;
                    sp.readStatus += SEND_AUX_STATUS;
                    // handpad uses statusByte.limit1 and auxStatusByte.index, so must read auxStatusByte
                    sp.handpadPortsRead = true;
                    sp.auxStatusByte = io.returnByteRead();
                    
                    if ((sp.auxStatusByte & AUX_STATUS_BYTE_INDEX) == AUX_STATUS_BYTE_INDEX)
                        sp.index = true;
                    else
                        sp.index = false;
                    
                    if ((sp.auxStatusByte & AUX_STATUS_BYTE_POS_WRAP) == AUX_STATUS_BYTE_POS_WRAP)
                        sp.posWrap = true;
                    else
                        sp.posWrap = false;
                    
                    if ((sp.auxStatusByte & AUX_STATUS_BYTE_SERVO_ON) == AUX_STATUS_BYTE_SERVO_ON)
                        sp.servoOn = true;
                    else
                        sp.servoOn = false;
                    
                    if ((sp.auxStatusByte & AUX_STATUS_BYTE_ACCEL_DONE) == AUX_STATUS_BYTE_ACCEL_DONE)
                        sp.accelDone = true;
                    else
                        sp.accelDone = false;
                    
                    if ((sp.auxStatusByte & AUX_STATUS_BYTE_SLEW_DONE) == AUX_STATUS_BYTE_SLEW_DONE)
                        sp.slewDone = true;
                    else
                        sp.slewDone = false;
                    
                    if ((sp.auxStatusByte & AUX_STATUS_BYTE_TIMER_OVERRUN) == AUX_STATUS_BYTE_TIMER_OVERRUN)
                        sp.timerOverrun = true;
                    else
                        sp.timerOverrun = false;
                    
                    if (sp.trajectoryStyle == TRAJECTORY_STYLE.position)
                        if (!sp.moveDone)
                            if (sp.accelDone)
                                if (sp.slewDone)
                                    sp.moveState = SERVO_MOVE_STATE.posMoveRampDown;
                                else
                                    sp.moveState = SERVO_MOVE_STATE.posMoveMaxVel;
                            else
                                sp.moveState = SERVO_MOVE_STATE.posMoveRampUp;
                    } else
                        if (displayBadStatusReturn && displayStatus)
                            console.errOut("did not receive auxStatusByte for " + SERVO_ID.matchKey(id));
                //else no aux status anticipated
                
                if ((sp.lastDefinedStatus & SEND_HOME_POSITION) == SEND_HOME_POSITION) {
                    holdLong = sp.homePosition;
                    sp.homePosition = 0;
                    for (ix = 0; ix < 4 && io.readSerialBuffer(); ix++) {
                        checksum += io.returnByteRead();
                        sp.bytesReturned++;
                        l = io.returnByteRead();
                        if (l < 0)
                            l+=256;
                        sp.homePosition += l * eMath.longPow(256, ix);
                    }
                    if (ix == 4) {
                        sp.readStatus += SEND_HOME_POSITION;
                        if (sp.homePosition >= 2147483648L)
                            sp.homePosition -= 4294967296L;
                    } else {
                        sp.homePosition = holdLong;
                        if (displayBadStatusReturn && displayStatus)
                            console.errOut("did not receive homePosition for " + SERVO_ID.matchKey(id));
                    }
                }
                
                if ((sp.lastDefinedStatus & SEND_DEVICE_ID) == SEND_DEVICE_ID) {
                    if (io.readSerialBuffer()) {
                        checksum += io.returnByteRead();
                        sp.bytesReturned++;
                        sp.deviceID = io.returnByteRead();
                        /**
                         * detect if handpad should be under SiTech control, if not, make it so;
                         * set handpad value to deviceID: both servo status deviceID returns carry the
                         * SiTech handpad information;
                         */
                        if (cfg.getInstance().controllerManufacturer == CONTROLLER_MANUFACTURER.SiTech) {
                            if (cfg.getInstance().handpadDesign != HANDPAD_DESIGN.handpadDesignSiTech) {
                                cfg.getInstance().handpadDesign = HANDPAD_DESIGN.handpadDesignSiTech;
                                cfg.getInstance().handpadPresent = true;
                            }
                            if (HandpadDesigns == null)
                                HandpadDesigns = new handpadDesignFactory().build(HANDPAD_DESIGN.handpadDesignSiTech);
                            // only pass in the handpad value if telescope motor controller being read
                            if (id==SERVO_ID.altDec.KEY || id==SERVO_ID.azRa.KEY)
                                HandpadDesigns.handpad(sp.deviceID);
                        }
                    } else
                        if (displayBadStatusReturn && displayStatus)
                            console.errOut("did not receive deviceID for " + SERVO_ID.matchKey(id));
                    if (io.readSerialBuffer()) {
                        checksum += io.returnByteRead();
                        sp.bytesReturned++;
                        // if SiTech controller, version used for x,y bits
                        byte b = io.returnByteRead();
                        if (cfg.getInstance().controllerManufacturer == CONTROLLER_MANUFACTURER.SiTech) {
                            if (id==SERVO_ID.altDec.KEY)
                                SiTechXbits = b;
                            else if (id==SERVO_ID.azRa.KEY)
                                SiTechYbits = b;
                            sp.version = (byte) SITECH_VERSION;
                        } else
                            sp.version = io.returnByteRead();
                        sp.readStatus += SEND_DEVICE_ID;
                    } else
                        if (displayBadStatusReturn && displayStatus)
                            console.errOut("did not receive version for " + SERVO_ID.matchKey(id));
                }
                
                if (io.readSerialBuffer()) {
                    sp.bytesReturned++;
                    sp.readStatus += READ_CHECKSUM;
                    sp.receiveChecksum = io.returnByteRead();
                    if (checksum == sp.receiveChecksum)
                        sp.receiveChecksumOK = true;
                    else
                        if (displayBadStatusReturn && displayStatus)
                            console.errOut("bad checksum "
                                    + checksum
                                    + " sp.receiveChecksum "
                                    + sp.receiveChecksum);
                } else
                    if (displayBadStatusReturn && displayStatus)
                        console.errOut("did not receive receiveChecksum for " + SERVO_ID.matchKey(id));
            } else
                if (displayBadStatusReturn && displayStatus)
                    console.errOut("did not receive statusByte for " + SERVO_ID.matchKey(id));
        } else
            if (displayBadStatusReturn && displayStatus)
                console.errOut("expectedBytesReturned of "
                        + expectedBytesReturned
                        + " != countReadBytes "
                        + io.countReadBytes());
        
        while (io.readSerialBuffer())
            sp.unexpectedReceiveBytes++;
        if (sp.unexpectedReceiveBytes > 0)
            if (displayBadStatusReturn && displayStatus)
                console.errOut("unexpectedReceiveBytes " + sp.unexpectedReceiveBytes);
        
        if (sp.receiveChecksumOK && sp.unexpectedReceiveBytes == 0) {
            sp.readStatusSuccessful = true;
            sp.readStatusSuccessfulCount++;
            sp.consecutiveUnsuccessfulReadStatusCount = 0;
        } else
            sp.consecutiveUnsuccessfulReadStatusCount++;
        
        sp.readStatusCount++;
        lscr.add(id, outBuf[CMD_BYTE], sp.readStatusSuccessful);
        
        // read handpad after every telescope motor controller's getStatus():
        // non-SiTech readHandpad() contains successful handpad read check and debouncing routine that
        // ensures that both motor's getStatus() are executed successfully;
        // SiTech controller handpad value set by sp.deviceID above;
        // only read handpad if SiTech telescope motor controller being read, or if PICServo scope axis
        // motor controller being read;
        if (id==SERVO_ID.altDec.KEY || id==SERVO_ID.azRa.KEY)
            readHandpad();
        
        if (displayCmd)
            displayCmdResults(id);
        if (displayStatus)
            displayStatusReturn(id);
        if (displayStickyBits)
            displayStickyBits(id);
    }
    
    private void displayCmdResults(int id) {
        SERVO_ID servoID;
        byte b = outBuf[CMD_BYTE];
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        console.stdOut("PICServo "
                + SERVO_ID.matchKey(id)
                + ": "
                + servoCmdTranslator.getString(b));
        if (b == CMD_HARD_RESET)
            console.stdOutLn(" (there is no status return for hard reset)");
        else if (sp.readStatus != 0) {
            if ((sp.lastDefinedStatus & SEND_DEVICE_ID) == SEND_DEVICE_ID) {
                console.stdOut(" id"
                        + sp.deviceID);
                if (cfg.getInstance().controllerManufacturer == CONTROLLER_MANUFACTURER.SiTech) {
                    if (id==SERVO_ID.altDec.KEY)
                        console.stdOut(" :Xbits"
                                + SiTechXbits);
                    else if (id==SERVO_ID.azRa.KEY)
                        console.stdOut(" :Ybits"
                                + SiTechYbits);
                } else
                    console.stdOut(" :Ver"
                            + sp.version);
            }
            console.stdOutLn(" RtnBytes "
                    + sp.bytesReturned
                    + " RtnStatusChksum "
                    + (sp.receiveChecksumOK?"OK":"bad"));
        } else
            console.errOut(" warning: no status returned");
    }
    
    String buildStatusReturnString(int id) {
        int u;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        long deltaActualPosition;
        double deltaStatusSidT;
        double velRadSec;
        
        statusReturnString = "status "
                + SERVO_ID.matchKey(id)
                + "\n";
        
        if (sp.readStatusSuccessful) {
            u = sp.statusByte;
            if (u < 0)
                u+=256;
            statusReturnString += "statusByte:"
                    + u
                    + " HomeInProg:"
                    + (sp.homeInProgress?"1":"0")
                    + " limit1:"
                    + (sp.limit1?"1":"0")
                    + " limit2:"
                    + (sp.limit2?"1":"0")
                    + " PosErr:"
                    + (sp.posError?"1":"0")
                    + " powerOn:"
                    + (sp.powerOn?"1":"0")
                    + " OverCurr:"
                    + (sp.overCurrent?"1":"0")
                    + " CksumErr:"
                    + (sp.cksumError?"1":"0")
                    + " moveDone:"
                    + (sp.moveDone?"1":"0")
                    + "\n";
            
            if ((sp.readStatus & SEND_AUX_STATUS) == SEND_AUX_STATUS) {
                statusReturnString += " auxStatusByte:"
                        + sp.auxStatusByte
                        + " timerOverrun:"
                        + (sp.timerOverrun?"1":"0")
                        + " slewDone:"
                        + (sp.slewDone?"1":"0")
                        + " accelDone:"
                        + (sp.accelDone?"1":"0")
                        + " servoOn:"
                        + (sp.servoOn?"1":"0")
                        + " posWrap:"
                        + (sp.posWrap?"1":"0")
                        + " index:"
                        + (sp.index?"1":"0")
                        + "\n";
            }
            
            if ((sp.readStatus & SEND_POSITION) == SEND_POSITION)
                statusReturnString += "ActPos: "
                        + sp.actualPosition
                        + " ActPosDeg: "
                        + eString.doubleToStringNoGrouping(sp.actualPositionDeg, 3, 3)
                        + " ActBacklashCount: "
                        + sp.actualBacklashCount;
            
            if ((sp.readStatus & SEND_AD) == SEND_AD)
                if (cfg.getInstance().controllerManufacturer==CONTROLLER_MANUFACTURER.SiTech
                    && sp.ADValueIsMotorVoltOrCPUTemp)
                    if ((id/2)==0)
                        statusReturnString += " CPU F: " + sp.ADValue;
                    else
                        statusReturnString += " MotorVolt: " + (int)sp.ADValue*10;
                else
                    statusReturnString += " AD: " + sp.ADValue;
            
            if ((sp.readStatus & SEND_ACTUAL_VEL) == SEND_ACTUAL_VEL)
                statusReturnString += " ActVel: "
                        + sp.actualVel;
            
            if ((sp.readStatus & SEND_HOME_POSITION) == SEND_HOME_POSITION)
                statusReturnString += " HomePos: "
                        + sp.homePosition;
            
            if ((sp.readStatus & SEND_DEVICE_ID) == SEND_DEVICE_ID) {
                statusReturnString += " id:"
                        + sp.deviceID;
                if (cfg.getInstance().controllerManufacturer == CONTROLLER_MANUFACTURER.SiTech) {
                    if (id==SERVO_ID.altDec.KEY)
                        statusReturnString += " :Xbits"
                                + SiTechXbits;
                    else if (id==SERVO_ID.azRa.KEY)
                        statusReturnString += " :Ybits"
                                + SiTechYbits;
                } else
                    statusReturnString += " :Ver"
                            + sp.version;
            }
            
            deltaActualPosition = sp.actualPosition - sp.lastActualPosition;
            deltaStatusSidT = eMath.validRad(sp.statusSidT - sp.lastStatusSidT);
            if (deltaStatusSidT == 0.)
                velRadSec = 0.;
            else
                velRadSec = (double) deltaActualPosition * sp.countToRad / (deltaStatusSidT*units.RAD_TO_SEC);
            statusReturnString += "\nposition change: "
                    + deltaActualPosition
                    + " over: "
                    + eString.doubleToStringNoGrouping(deltaStatusSidT*units.RAD_TO_SEC, 3, 2)
                    + " sec, velocity: "
                    + eString.doubleToStringNoGrouping(velRadSec*units.RAD_TO_ARCSEC, 6, 1)
                    + "\"/sec";
            
            statusReturnString += "\n";
        } else
            statusReturnString += "read unsuccessful\n";
        
        statusReturnString += "status return count (successful/total/consecutive_unsuccessful) "
                + sp.readStatusSuccessfulCount
                + "/"
                + sp.readStatusCount
                + "/"
                + sp.consecutiveUnsuccessfulReadStatusCount
                + "\n";
        
        return statusReturnString;
    }
    
    void displayStatusReturn(int id) {
        console.stdOutLn(buildStatusReturnString(id));
    }
    
    String buildStickyBitsString(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        stickyBitsString = "StickyBits for "
                + SERVO_ID.matchKey(id)
                + ": ";
        
        // current limiting occurred
        if (sp.readStatus > 0 && sp.overCurrent)
            stickyBitsString += STICKY_BITS.overCurrent + "   ";
        
        // position error limit exceeded, or, position servo disabled
        if (sp.readStatus > 0 && sp.posError)
            stickyBitsString += STICKY_BITS.posError + "   ";
        
        // 32 bit position counter wrap
        if ((sp.readStatus & SEND_AUX_STATUS) == SEND_AUX_STATUS && sp.posWrap)
            stickyBitsString += STICKY_BITS.posWrap + "   ";
        
        // servo, profiling, and command processing time exceeded .51msec
        if ((sp.readStatus & SEND_AUX_STATUS) == SEND_AUX_STATUS && sp.timerOverrun)
            stickyBitsString += STICKY_BITS.timerOverrun + "   ";
        
        stickyBitsString += "\n";
        
        return stickyBitsString;
    }
    
    void displayStickyBits(int id) {
        console.stdOutLn(buildStickyBitsString(id));
    }
    
    private void buildChecksum(int id) {
        int ix;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        // build checksum, skipping header byte
        sp.sendChecksum = 0;
        for (ix = 1; ix < outBufIx; ix++)
            sp.sendChecksum += outBuf[ix];
    }
    
    // use ix<=outBufIx because the last outBufIx is taken by the checksum value
    private void displayOutBuf() {
        int ix;
        int n;
        
        console.stdOut("\nOutBuf: ");
        for (ix = 0; ix <= outBufIx; ix++) {
            n = outBuf[ix];
            if (n < 0)
                n += 256;
            console.stdOut(n + " ");
        }
        console.stdOut("\n");
    }
    
    private void processOutBuf(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        buildChecksum(id);
        outBuf[outBufIx++] = sp.sendChecksum;
        if (displayOutBuf)
            displayOutBuf();
        if (io != null)
            io.writeByteArray(outBuf, outBufIx);
    }
    
    /**
     * outBuf[] should read 0xAA 0x00 0x21 0x01 0xFF 0x21
     */
    private void setAddress(int id) {
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address of next PICServo controller available to have its address set
        outBuf[outBufIx++] = 0x00;
        // command byte: set address
        outBuf[outBufIx++] = CMD_SET_ADDRESS;
        // set address
        outBuf[outBufIx++] = (byte) (id+1);
        // set group address
        outBuf[outBufIx++] = GROUP_ADDRESS;
        processOutBuf(id);
        readStatus(id);
    }
    
    private void defineStatus(int id, byte controlByte) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte: define status
        outBuf[outBufIx++] = CMD_DEFINE_STATUS;
        // control byte
        sp.controlByte = controlByte;
        outBuf[outBufIx++] = sp.controlByte;
        processOutBuf(id);
        sp.lastDefinedStatus = sp.controlByte;
        readStatus(id);
    }
    
    void getStatus(int id) {
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte: no operation - returns status
        outBuf[outBufIx++] = CMD_NOP;
        processOutBuf(id);
        readStatus(id);
    }
    
    /**
     * does not return servo status
     */
    private void hardReset() {
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // group address
        outBuf[outBufIx++] = GROUP_ADDRESS;
        // command byte: hard reset to power on state
        outBuf[outBufIx++] = CMD_HARD_RESET;
        processOutBuf(0);
        // does not return status, so don't call readStatus(), instead, wait for controller reset
        // to complete and empty serial read buffer
        astroTime.getInstance().wait(cfg.getInstance().servoPortWaitTimeMilliSecs);
        while (io.readSerialBuffer())
            console.stdOutChar((char) io.returnByteRead());
        // add command to log and display
        lscr.add(0, outBuf[CMD_BYTE], true);
        if (displayCmd)
            displayCmdResults(0);
    }
    
    private void setGain(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte: set gain (least significant byte first)
        outBuf[outBufIx++] = CMD_SET_GAIN;
        // build least to most significant bytes; all values must be positive numbers except for positionErrorLimitEL
        // which is limited to 0x3FFF or 16383
        outBuf[outBufIx++] = (byte) (sp.positionGainKp%256);
        outBuf[outBufIx++] = (byte) (sp.positionGainKp/256);
        outBuf[outBufIx++] = (byte) (sp.velGainKd%256);
        outBuf[outBufIx++] = (byte) (sp.velGainKd/256);
        outBuf[outBufIx++] = (byte) (sp.positionGainKi%256);
        outBuf[outBufIx++] = (byte) (sp.positionGainKi/256);
        outBuf[outBufIx++] = (byte) (sp.integrationLimitIL%256);
        outBuf[outBufIx++] = (byte) (sp.integrationLimitIL/256);
        outBuf[outBufIx++] = (byte) (sp.outputLimitOL % 0x100);
        outBuf[outBufIx++] = (byte) (sp.currentLimitCL % 0x100);
        outBuf[outBufIx++] = (byte) (sp.positionErrorLimitEL%256);
        outBuf[outBufIx++] = (byte) (sp.positionErrorLimitEL/256);
        outBuf[outBufIx++] = (byte) (sp.rateDivisorSR % 0x100);
        outBuf[outBufIx++] = (byte) (sp.ampDeadbandComp % 0x100);
        processOutBuf(id);
        readStatus(id);
    }
    
    private void stopMotor(int id, byte stopStyle) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte: stop motor
        outBuf[outBufIx++] = CMD_STOP_MOTOR;
        // stopping style
        outBuf[outBufIx++] = (byte) (stopStyle + sp.ampOnValue);
        
        if (stopStyle == TURN_MOTOR_OFF || stopStyle == MOTOR_POWERED)
            sp.PICServoSimList.addResetPos(astroTime.getInstance().JD, 0.);
        else if (stopStyle == STOP_ABRUPTLY)
            sp.PICServoSimList.stopMotorAbruptly(astroTime.getInstance().JD);
        else if (stopStyle == STOP_SMOOTHLY)
            sp.PICServoSimList.stopMotorSmoothly(astroTime.getInstance().JD);
        
        processOutBuf(id);
        readStatus(id);
    }
    
    private void resetPosition(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte: reset position
        outBuf[outBufIx++] = CMD_RESET_POSITION;
        
        sp.PICServoSimList.addResetPos(astroTime.getInstance().JD, 0.);
        
        processOutBuf(id);
        readStatus(id);
    }
    
    boolean stickyBitSet(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        if (sp.overCurrent
                || sp.posError
                || sp.posWrap
                || sp.timerOverrun)
            return true;
        return false;
    }
    
    private void clearStickyBits(int id) {
        if (stickyBitSet(id)) {
            outBufIx = 0;
            // header byte
            outBuf[outBufIx++] = HEADER_BYTE;
            // module address
            outBuf[outBufIx++] = (byte) (id+1);
            // command byte: clear sticky bits
            outBuf[outBufIx++] = CMD_CLEAR_STICKY_BITS;
            processOutBuf(id);
            readStatus(id);
        }
    }
    
    /**
     * load data least significant byte first; if < 0, then bring value into upper half of positive 'long value' space
     */
    private void longValueToOutBuf(long l) {
        int a;
        int b;
        int c;
        int d;
        
        if (l < 0)
            l += 4294967296L;
        d = (int) (l/16777216);
        c = (int) ((l - d*16777216)/65536);
        b = (int) ((l - d*16777216 - c*65536)/256);
        a = (int) (l - d*16777216 - c*65536 - b*256);
        outBuf[outBufIx++] = (byte) a;
        outBuf[outBufIx++] = (byte) b;
        outBuf[outBufIx++] = (byte) c;
        outBuf[outBufIx++] = (byte) d;
    }
    
    private void loadTrajectoryPosition(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte: load trajectory + 12 bytes
        outBuf[outBufIx++] = CMD_LOAD_TRAJECTORY_POSITION;
        // control byte
        sp.controlByte = (byte) (LOAD_POSITION
                + LOAD_VEL
                + LOAD_ACCEL
                + SET_MVMT_MODE
                + TRAPEZOIDAL_PROFILE);
        if (sp.moveNow)
            sp.controlByte += START_MOTION_NOW;
        outBuf[outBufIx++] = sp.controlByte;
        
        // load position data (least significant byte first); can be + or - value
        if (sp.reverseMotor)
            longValueToOutBuf(-sp.targetPosition);
        else
            longValueToOutBuf(sp.targetPosition);
        
        // load velocity; must be a positive number
        longValueToOutBuf(sp.targetVel);
        
        if (sp.targetAccel == 0) {
            console.errOut("loadTrajectoryPosition(): zero accel for motor "
                    + SERVO_ID.matchKey(id)
                    + " targetVel="
                    + sp.targetVel
                    + " targetPosition="
                    + sp.targetPosition
                    + " actualPosition="
                    + sp.actualPosition
                    + ": fixing accel");
            
            fixAccel(id);
        }
        
        // load acceleration; must be a positive number
        longValueToOutBuf(sp.targetAccel);
        
        sp.PICServoSimList.addPos(astroTime.getInstance().JD,
                sp.calcAccelDegSecSecFromAcceleration(sp.targetAccel)*units.DEG_TO_RAD,
                sp.targetPosition * sp.countToRad,
                sp.velRadSec);
        
        processOutBuf(id);
        
        readStatus(id);
    }
    
    private void fixAccel(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        sp.cfgAccelDegSecSec = sp.accelDegSecSec = 1.;
        sp.acceleration = (long) (sp.accelDegSecSec * sp.accelDegSecSecToCountsTickTick());
        sp.targetAccel = sp.acceleration;
        sp.longRangeAccel = sp.acceleration;
        sp.shortRangeAccel = (long) (SERVO_PARMS.SHORT_RANGE_ACCEL_FACTOR * (double) sp.longRangeAccel);
    }
    
    private void loadTrajectoryVel(int id) {
        double vel;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte: load trajectory + 8 bytes
        outBuf[outBufIx++] = CMD_LOAD_TRAJECTORY_VEL;
        // control byte
        sp.controlByte = (byte) (LOAD_VEL
                + LOAD_ACCEL
                + SET_MVMT_MODE
                + VEL_PROFILE);
        if (sp.moveNow)
            sp.controlByte += START_MOTION_NOW;
        
        // zero velocity for SiTech controller must always keep CW rotation
        if (sp.targetVel == 0)
            if (sp.reverseMotor)
                sp.controlByte += REVERSE_DIR;
            else
                sp.controlByte += FORWARD_DIR;
        else
            if (sp.targetVelDir == ROTATION.CCW && !sp.reverseMotor
                || sp.targetVelDir == ROTATION.CW && sp.reverseMotor)
                sp.controlByte += REVERSE_DIR;
            else
                sp.controlByte += FORWARD_DIR;
        
        outBuf[outBufIx++] = sp.controlByte;
        
        // load velocity; must be a positive number
        longValueToOutBuf(sp.targetVel);
        
        if (sp.targetAccel == 0) {
            console.errOut("loadTrajectoryVel(): zero accel for motor "
                    + SERVO_ID.matchKey(id)
                    + " targetVel="
                    + sp.targetVel
                    + ": fixing accel");
            
            fixAccel(id);
        }
        
        // load acceleration; must be a positive number
        longValueToOutBuf(sp.targetAccel);
        
        vel = sp.targetVel/sp.velRadSecToTargetVel();
        if (sp.targetVelDir == ROTATION.CCW)
            vel = -vel;
        sp.PICServoSimList.addVel(astroTime.getInstance().JD,
                sp.calcAccelDegSecSecFromAcceleration(sp.targetAccel)*units.DEG_TO_RAD,
                vel);
        
        processOutBuf(id);
        
        readStatus(id);
}
    
    private void startMotor(int id) {
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte: stop motor
        outBuf[outBufIx++] = CMD_START_MOTOR;
        processOutBuf(id);
        readStatus(id);
    }
    
    private void disableAmp(int id, byte stopStyle) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte: stop motor
        outBuf[outBufIx++] = CMD_STOP_MOTOR;
        // stopping style
        outBuf[outBufIx++] = (byte) (stopStyle + sp.ampOffValue);
        processOutBuf(id);
        readStatus(id);
    }
    
    // following methods are for SiTech controller...
    
    void extNOPReadCfgFromFlash(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte
        outBuf[outBufIx++] = CMD_EXT_NOP_CONTROLLER;
        // control byte
        sp.controlByte = READ_CFG_FROM_FLASH;
        outBuf[outBufIx++] = sp.controlByte;
        processOutBuf(id);
        readStatus(id);
    }
    
    void extNOPWriteCfgFromFlash(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte
        outBuf[outBufIx++] = CMD_EXT_NOP_CONTROLLER;
        // control byte
        sp.controlByte = WRITE_CFG_FROM_FLASH;
        outBuf[outBufIx++] = sp.controlByte;
        processOutBuf(id);
        readStatus(id);
    }
    
    void extNOPUseSaveFactoryDefaults(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte
        outBuf[outBufIx++] = CMD_EXT_NOP_CONTROLLER;
        // control byte
        sp.controlByte = USE_SAVE_FACTORY_DEFAULTS;
        outBuf[outBufIx++] = sp.controlByte;
        processOutBuf(id);
        readStatus(id);
    }
    
    void extNOPSwitchToAsciiMode() {
        int id = SERVO_ID.altDec.KEY;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte
        outBuf[outBufIx++] = CMD_EXT_NOP_CONTROLLER;
        // control byte
        sp.controlByte = SWITCH_TO_ASCII_MODE;
        outBuf[outBufIx++] = sp.controlByte;
        processOutBuf(id);
        // not readStatus(id); as controller will revert back to PICServo mode
    }
    
    void extNOPInvertServoEncoder(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte
        outBuf[outBufIx++] = CMD_EXT_NOP_X_BITS;
        // control byte
        sp.controlByte = WRITE_X_BITS;
        outBuf[outBufIx++] = sp.controlByte;
        // value
        outBuf[outBufIx++] = INVERT_SERVO_ENCODER;
        processOutBuf(id);
        readStatus(id);
    }
    
    void extNOPInvertMotorDir(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte
        outBuf[outBufIx++] = CMD_EXT_NOP_X_BITS;
        // control byte
        sp.controlByte = WRITE_X_BITS;
        outBuf[outBufIx++] = sp.controlByte;
        // value
        outBuf[outBufIx++] = INVERT_MOTOR_DIR;
        processOutBuf(id);
        readStatus(id);
    }
    
    void extNOPInvertScopeEncoder(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte
        outBuf[outBufIx++] = CMD_EXT_NOP_X_BITS;
        // control byte
        sp.controlByte = WRITE_X_BITS;
        outBuf[outBufIx++] = sp.controlByte;
        // value
        outBuf[outBufIx++] = INVERT_SCOPE_ENCODER;
        processOutBuf(id);
        readStatus(id);
    }
    
    void extNOPDragNDrop() {
        int id = SERVO_ID.altDec.KEY;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte
        outBuf[outBufIx++] = CMD_EXT_NOP_X_BITS;
        // control byte
        sp.controlByte = WRITE_X_BITS;
        outBuf[outBufIx++] = sp.controlByte;
        // value
        outBuf[outBufIx++] = DRAG_N_DROP;
        processOutBuf(id);
        readStatus(id);
    }
    
    void extNOPSlewNDrop() {
        int id = SERVO_ID.azRa.KEY;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte
        outBuf[outBufIx++] = CMD_EXT_NOP_X_BITS;
        // control byte
        sp.controlByte = WRITE_X_BITS;
        outBuf[outBufIx++] = sp.controlByte;
        // value
        outBuf[outBufIx++] = SLEW_N_DROP;
        processOutBuf(id);
        readStatus(id);
    }
    
    void extNOPPlatformMode() {
        int id = SERVO_ID.altDec.KEY;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte
        outBuf[outBufIx++] = CMD_EXT_NOP_X_BITS;
        // control byte
        sp.controlByte = WRITE_X_BITS;
        outBuf[outBufIx++] = sp.controlByte;
        // value
        outBuf[outBufIx++] = PLATFORM_MODE;
        processOutBuf(id);
        readStatus(id);
    }
    
    void extNOPSimulDirHandpadDesign() {
        int id = SERVO_ID.altDec.KEY;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte
        outBuf[outBufIx++] = CMD_EXT_NOP_X_BITS;
        // control byte
        sp.controlByte = WRITE_X_BITS;
        outBuf[outBufIx++] = sp.controlByte;
        // value
        outBuf[outBufIx++] = SIMUL_DIR_HANDPAD_DESIGN;
        processOutBuf(id);
        readStatus(id);
    }
    
    void extNOPGuideMode() {
        int id = SERVO_ID.altDec.KEY;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte
        outBuf[outBufIx++] = CMD_EXT_NOP_X_BITS;
        // control byte
        sp.controlByte = WRITE_X_BITS;
        outBuf[outBufIx++] = sp.controlByte;
        // value
        outBuf[outBufIx++] = GUIDE_MODE;
        processOutBuf(id);
        readStatus(id);
        console.stdOutLn("entered SiTech guide mode");
    }
    
    void extNOPExitGuideMode(boolean enableHandpad, boolean simulDirHandpad) {
        byte value;
        int id = SERVO_ID.altDec.KEY;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte
        outBuf[outBufIx++] = CMD_EXT_NOP_X_BITS;
        // control byte
        sp.controlByte = WRITE_X_BITS;
        outBuf[outBufIx++] = sp.controlByte;
        // value
        value = 0;
        if (enableHandpad)
            value += ENABLE_HANDPAD;
        if (simulDirHandpad)
            value += SIMUL_DIR_HANDPAD_DESIGN;
        outBuf[outBufIx++] = value;
        processOutBuf(id);
        readStatus(id);
        console.stdOutLn("quit SiTech guide mode, "
                + "handpad "
                + (enableHandpad?"enabled":"disabled")
                + ", simultaneous directions "
                + (simulDirHandpad?"enabled":"disabled"));
    }
    
    void extNOPScopeEncoder(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte
        outBuf[outBufIx++] = CMD_EXT_NOP_SCOPE_ENCODER;
        // control byte
        sp.controlByte = WRITE_SCOPE_ENCODER;
        outBuf[outBufIx++] = sp.controlByte;
        longValueToOutBuf(sp.scopeEncoder);
        processOutBuf(id);
        readStatus(id);
    }
    
    void extNOPGuideSpeed(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte
        outBuf[outBufIx++] = CMD_EXT_NOP_GUIDE_SPEED;
        // control byte
        sp.controlByte = WRITE_GUIDE_SPEED;
        outBuf[outBufIx++] = sp.controlByte;
        longValueToOutBuf(sp.handpadGuideVel);
        processOutBuf(id);
        readStatus(id);
    }
    
    void extNOPHandpadSlewPanSpeeds(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        outBufIx = 0;
        // header byte
        outBuf[outBufIx++] = HEADER_BYTE;
        // module address
        outBuf[outBufIx++] = (byte) (id+1);
        // command byte
        outBuf[outBufIx++] = CMD_EXT_NOP_HANDPAD_SLEW_PAN_SPEEDS;
        // control byte
        sp.controlByte = WRITE_HANDPAD_SLEW_PAN_SPEEDS;
        outBuf[outBufIx++] = sp.controlByte;
        longValueToOutBuf(sp.handpadSlewVel);
        longValueToOutBuf(sp.handpadPanVel);
        processOutBuf(id);
        readStatus(id);
    }
    
    void initSiTechHandpadSlewPanGuideSpeeds() {
        int id;
        
        for (id = 0; id < SERVO_ID.size(); id++) {
            if (cfg.getInstance().servoParm[id].controllerActive) {
                cfg.getInstance().servoParm[id].handpadSlewVel
                        = (long) (cfg.getInstance().servoParm[id].fastSpeedRadSec * cfg.getInstance().servoParm[id].velRadSecToTargetVel());
                
                cfg.getInstance().servoParm[id].handpadPanVel
                        = (long) (cfg.getInstance().servoParm[id].slowSpeedRadSec * cfg.getInstance().servoParm[id].velRadSecToTargetVel());
                
                extNOPHandpadSlewPanSpeeds(id);
                
                cfg.getInstance().servoParm[id].handpadGuideVel
                        = (long) (cfg.getInstance().servoParm[id].guideRadSec * cfg.getInstance().servoParm[id].velRadSecToTargetVel());
                
                extNOPGuideSpeed(id);
            }
        }
    }
    
    void validVelRadSec(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        if (sp.velRadSec < 0.)
            sp.velRadSec = -sp.velRadSec;
        if (sp.velRadSec > sp.fastSpeedRadSec)
            sp.velRadSec = sp.fastSpeedRadSec;
    }
    
    void breakProcessMoveCmdSeq(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        sp.servoCmdProcessed = true;
    }
    
    /**
     * this function ensures that multi-command sequences are executed to their conclusion;
     * before calling this function, set the following as appropriate:
     * cfg.getInstance().servoParm[id].moveNow
     * cfg.getInstance().servoParm[id].targetPosition, or, cfg.getInstance().servoParm[id].targetVelDir and
     *    cfg.getInstance().servoParm[id].velRadSec (not needed for startSlowVelMove & startFastVelMove commands but include
     *    so that velocity is recorded)
     * cfg.getInstance().servoParm[id].moveCmd
     * cfg.getInstance().servoParm[id].servoCmdProcessed = false;
     *
     * can execute velocity move while in trapezoidal position move, but cannot enter position move
     * while in velocity mode, nor should a new position move be commanded while previous position
     * move is underway, so always issue a stop motor command before continuing with a new position move;
     *
     * position move sequence:
     *    posMoveStopCurrent (issues a stop motor command to stop the current move),
     *    posMoveWaitForStop (waits for motor stop),
     *    posMoveStart (issues actual position move command);
     */
    boolean cmdsNeedProcessing() {
        int id;
        
        for (id = 0; id < SERVO_ID.size(); id++)
            if (cfg.getInstance().servoParm[id].controllerActive
                && !cfg.getInstance().servoParm[id].servoCmdProcessed
                && cfg.getInstance().servoParm[id].consecutiveUnsuccessfulReadStatusCount < 6)
                return true;
        
        return false;
    }
    
    void processMoveCmdControl() {
        int id;
        
        while (cmdsNeedProcessing())
            for (id = 0; id < SERVO_ID.size(); id++)
                if (cfg.getInstance().servoParm[id].controllerActive && !cfg.getInstance().servoParm[id].servoCmdProcessed)
                    processMoveCmd(id);
    }
    
    /**
     * subroutine called from processMoveCmd() which performs a routine common to all three
     * velocity commands (fast, slow, custom); cfg.getInstance().servoParm[id].targetVel should be set before calling
     */
    private void processMoveCmdVelSubR(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        // prevent repetition of velocity move commands
        if (sp.lastMoveCmd == sp.moveCmd
                && sp.lastTargetVel == sp.targetVel
                && sp.lastTargetVelDir == sp.targetVelDir)
            
            sp.servoCmdProcessed = true;
        
        else {
            sp.targetAccel = sp.acceleration;
            sp.trajectoryStyle = TRAJECTORY_STYLE.velocity;
            sp.lastTargetVel = sp.targetVel;
            sp.lastTargetVelDir = sp.targetVelDir;
            clearStickyBits(id);
            loadTrajectoryVel(id);
            sp.servoCmdProcessed = true;
        }
    }
    
    /**
     * subroutine called from processMoveCmd() which prevents stopMotor() from being called unnecessarily;
     * function returns true if stopMotor() command issued;
     * return false if status shows motor position unchanged or command already sent;
     */
    private boolean stopMotorControl(int id, byte stopStyle) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        if (sp.motorStopped || sp.lastMoveCmd == sp.moveCmd)
            return false;
        else {
            // when stopping, the PIC servo enters velocity mode, so set the trajectory style so that the
            // readStatus() can set the appropriate ServoMoveState
            sp.trajectoryStyle = TRAJECTORY_STYLE.velocity;
            stopMotor(id, stopStyle);
        }
        return true;
    }
    
    /**
     * set targetVelDir based upon the position move
     */
    private void setTargetVelDirFromPosition(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        if (sp.targetPosition > sp.actualPosition)
            sp.targetVelDir = ROTATION.CW;
        else
            sp.targetVelDir = ROTATION.CCW;
    }
    
    /**
     * subroutine called from processMoveCmd() which starts a position move
     */
    private void processMoveCmdPosMoveStart(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        setTargetVelDirFromPosition(id);
        sp.moveCmd = SERVO_MOVE_CMD.posMoveStart;
        sp.targetAccel = sp.acceleration;
        sp.targetVel = (long) (sp.velRadSec * sp.velRadSecToTargetVel());
        validVelRadSec(id);
        sp.trajectoryStyle = TRAJECTORY_STYLE.position;
        clearStickyBits(id);
        loadTrajectoryPosition(id);
        sp.servoCmdProcessed = true;
    }
    
    private void processMoveCmd(int id) {
        boolean processedCmd;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        getStatus(id);
        if (sp.readStatusSuccessful) {
            processedCmd = false;
            
            if (sp.moveCmd == SERVO_MOVE_CMD.startSlowVelMove) {
                processedCmd = true;
                sp.targetVel = (long) (sp.slowSpeedRadSec * sp.velRadSecToTargetVel());
                processMoveCmdVelSubR(id);
            } else if (sp.moveCmd == SERVO_MOVE_CMD.startFastVelMove) {
                processedCmd = true;
                sp.targetVel = (long) (sp.fastSpeedRadSec * sp.velRadSecToTargetVel());
                processMoveCmdVelSubR(id);
            } else if (sp.moveCmd == SERVO_MOVE_CMD.startVelMove) {
                processedCmd = true;
                validVelRadSec(id);
                sp.targetVel = (long) (sp.velRadSec * sp.velRadSecToTargetVel());
                processMoveCmdVelSubR(id);
            }
            /** to normally start a position move, use SERVO_MOVE_CMD.posMoveStopCurrent or SERVO_MOVE_CMD.posMoveWaitForStop
             * to ensure that prior motion ceases before starting the position move; SERVO_MOVE_CMD.posMoveStart should only
             * be used after insuring that the motor has stopped, otherwise unexpected severe motor motion will occur;
             * set sp.velRadSec beforehand */
            else if (sp.moveCmd == SERVO_MOVE_CMD.posMoveStart) {
                processedCmd = true;
                processMoveCmdPosMoveStart(id);
            } else if (sp.moveCmd == SERVO_MOVE_CMD.posMoveStopCurrent) {
                processedCmd = true;
                if (stopMotorControl(id, STOP_SMOOTHLY))
                    sp.moveCmd = SERVO_MOVE_CMD.posMoveWaitForStop;
                else
                    // if motor already stopped, start the position move
                    processMoveCmdPosMoveStart(id);
            } else if (sp.moveCmd == SERVO_MOVE_CMD.posMoveWaitForStop) {
                processedCmd = true;
                if (sp.motorStopped)
                    processMoveCmdPosMoveStart(id);
            } else if (sp.moveCmd == SERVO_MOVE_CMD.startDelayedMove) {
                processedCmd = true;
                startMotor(id);
                sp.servoCmdProcessed = true;
            } else if (sp.moveCmd == SERVO_MOVE_CMD.stopMoveSmoothly) {
                processedCmd = true;
                stopMotorControl(id, STOP_SMOOTHLY);
                sp.servoCmdProcessed = true;
            } else if (sp.moveCmd == SERVO_MOVE_CMD.stopMoveAbruptly) {
                processedCmd = true;
                stopMotorControl(id, STOP_ABRUPTLY);
                sp.servoCmdProcessed = true;
            } else if (sp.moveCmd == SERVO_MOVE_CMD.turnMotorOnAmpOn) {
                processedCmd = true;
                stopMotorControl(id, MOTOR_POWERED);
                sp.servoCmdProcessed = true;
            } else if (sp.moveCmd == SERVO_MOVE_CMD.turnMotorOffAmpOn) {
                processedCmd = true;
                stopMotorControl(id, TURN_MOTOR_OFF);
                sp.servoCmdProcessed = true;
            } else if (sp.moveCmd == SERVO_MOVE_CMD.disableAmp) {
                processedCmd = true;
                disableAmp(id, STOP_ABRUPTLY);
                sp.servoCmdProcessed = true;
            } else if (sp.moveCmd == SERVO_MOVE_CMD.waitForMoveStateFinished) {
                processedCmd = true;
                if (sp.moveState == SERVO_MOVE_STATE.finished)
                    sp.servoCmdProcessed = true;
            } else if (sp.moveCmd == SERVO_MOVE_CMD.waitForStop) {
                processedCmd = true;
                if (sp.motorStopped)
                    sp.servoCmdProcessed = true;
            }
            
            if (!processedCmd)
                common.badExit("unprocessed SERVO_MOVE_CMD of "
                        + sp.moveCmd
                        + " of SERVO_ID "
                        + SERVO_ID.matchKey(id)
                        + " in processMoveCmd()");
            
            sp.lastMoveCmd = sp.moveCmd;
        }
    }
    
    /**
     * check to see if synch sensor has signaled
     */
    void checkPECSynchFromLimit2(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        if (sp.PECActive && sp.autoPECSyncDetect != AUTO_PEC_SYNC_DETECT.Off)
            // set via .lastLimit2 data input in readStatus which sets based on high/low or low/high choice
            if (sp.PECSyncReady) {
            sp.PECSyncReady = false;
            synchPEC(id);
            }
    }
    
    /**
     * check to see if synch sensor has signaled from the SiTech controller's ybits read
     * (version byte of the 2nd motor)
     */
    void checkPECSynchFromYBits() {
        if (cfg.getInstance().spa.PECActive && cfg.getInstance().spa.autoPECSyncDetect != AUTO_PEC_SYNC_DETECT.Off) {
            if (cfg.getInstance().spa.autoPECSyncDetect == AUTO_PEC_SYNC_DETECT.lowHigh
                    && (SiTechYbits & servo.PEC_SYNC_ALT) == servo.PEC_SYNC_ALT
                    && (lastSiTechYbits & servo.PEC_SYNC_ALT) == 0
                    || cfg.getInstance().spa.autoPECSyncDetect == AUTO_PEC_SYNC_DETECT.highLow
                    && (SiTechYbits & servo.PEC_SYNC_ALT) == 0
                    && (lastSiTechYbits & servo.PEC_SYNC_ALT) == servo.PEC_SYNC_ALT) {
                console.stdOutLn("signaling alt synch " + cfg.getInstance().spa.autoPECSyncDetect.toString());
                synchPEC(SERVO_ID.altDec.KEY);
            }
        }
        if (cfg.getInstance().spz.PECActive && cfg.getInstance().spz.autoPECSyncDetect != AUTO_PEC_SYNC_DETECT.Off) {
            if (cfg.getInstance().spz.autoPECSyncDetect == AUTO_PEC_SYNC_DETECT.lowHigh
                    && (SiTechYbits & servo.PEC_SYNC_AZ) == servo.PEC_SYNC_AZ
                    && (lastSiTechYbits & servo.PEC_SYNC_AZ) == 0
                    || cfg.getInstance().spz.autoPECSyncDetect == AUTO_PEC_SYNC_DETECT.highLow
                    && (SiTechYbits & servo.PEC_SYNC_AZ) == 0
                    && (lastSiTechYbits & servo.PEC_SYNC_AZ) == servo.PEC_SYNC_AZ) {
                console.stdOutLn("signaling az synch " + cfg.getInstance().spz.autoPECSyncDetect.toString());
                synchPEC(SERVO_ID.azRa.KEY);
            }
            lastSiTechYbits = SiTechYbits;
        }
    }
    
    /**
     * synch all PEC via setPECIxOffset()
     */
    void synchPEC(int id) {
        Iterator it;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        if (sp.PECActive) {
            it = sp.lg.iterator();
            while (it.hasNext()) {
                guide g = (guide) it.next();
                if (g.PECActive) {
                    g.lastPECIxOffset = g.PECIxOffset;
                    g.setPECIxOffset(sp.actualPosition);
                    // compare .lastPECIxOffset to .PECIxOffset in order to detect unexpected synch point
                    if (g.PECIxOffset != g.lastPECIxOffset)
                        console.errOut("PEC synch not at expected time for " + SERVO_ID.matchKey(id).toString());
                }
            }
        }
    }
    
    String buildDisplayMoveString(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        displayMoveString = "display move "
                + SERVO_ID.matchKey(id)
                + " "
                + sp.moveCmd
                + " "
                + sp.moveState
                + " "
                + sp.cmdDevice;
        
        if ((sp.lastDefinedStatus & SEND_AD) == SEND_AD)
            displayMoveString += " ADValue:" + sp.ADValue;
        
        displayMoveString += "\n";
        
        return displayMoveString;
    }
    
    void displayMove(int id) {
        console.stdOut(buildDisplayMoveString(id));
    }
    
    String buildBacklashString(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        if (sp.backlashRad > 0.)
            backlashString = "\nbacklash taken up in CCW direction = "
                    + sp.actualBacklashRad*units.RAD_TO_ARCMIN
                    + " arcminutes,  "
                    + eString.doubleToStringNoGrouping(100.*sp.actualBacklashRad/sp.backlashRad, 3, 0)
                    + "% of total\n";
        else
            backlashString = "\nno backlash present\n";
        
        return backlashString;
    }
    
    String buildSimCurrentPosVelString(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        long currentPos;
        long posErr;
        double velArcsecSec;
        
        currentPos = (long) (sp.PICServoSimList.currentPosRad(astroTime.getInstance().JD)/sp.countToRad);
        posErr = currentPos - sp.actualPosition;
        sp.posErrRad = posErr*sp.countToRad;
        velArcsecSec = sp.PICServoSimList.currentVelRadSec(astroTime.getInstance().JD)*units.RAD_TO_ARCSEC;
        
        simCurrentPosVelString =
                "\nPICServoSim position in counts "
                + currentPos
                + " velocity (\"/sec): current "
                + eString.doubleToStringNoGrouping(velArcsecSec, 6, 2)
                + " average "
                + eString.doubleToStringNoGrouping(sp.avgVelRadSec*units.RAD_TO_ARCSEC, 6, 2)
                + "\n   position error: "
                + posErr
                + "   counts/sec: "
                + eString.doubleToStringNoGrouping(((double) posErr)/velArcsecSec, 8, 2)
                + "\n";
        
        return simCurrentPosVelString;
    }
    
    void testServos() {
        int mainSelect;
        Enumeration eSERVO_ID;
        SERVO_ID si = null;
        
        System.out.println("servo motor test");
        displayCmd = true;
        displayStatus = true;
        do {
            System.out.println("Please select from the following, or any other number to quit");
            eSERVO_ID = SERVO_ID.elements();
            while (eSERVO_ID.hasMoreElements()) {
                si = (SERVO_ID) eSERVO_ID.nextElement();
                System.out.println(si.KEY+1
                        + ":test "
                        + si);
            }
            System.out.println(si.size()+1 + ":hard reset all motors");
            System.out.println(si.size()+2 + ":init all motors");
            console.getInt();
            mainSelect = console.i;
            if (mainSelect > 0 && mainSelect < si.size()+2)
                testServo(console.i-1);
            else
                if (console.i == si.size()+1)
                    hardReset();
                else if (console.i == si.size()+2)
                    initServoID();
        }while (mainSelect > 0 && mainSelect <= si.size()+2);
        
        lscr.displayMostRecent(20);
        displayCmd = false;
        displayStatus = false;
        System.out.println("end of servo motor test");
    }
    
    void testServo(int id) {
        int mainSelect;
        Enumeration eTEST_SERVO;
        TEST_SERVO ts;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        do {
            System.out.println("\nTest of motor "
                    + SERVO_ID.matchKey(id)
                    + " at address "
                    + (id+1));
            System.out.println("Please select from the following, or any other number to quit");
            eTEST_SERVO = TEST_SERVO.elements();
            while (eTEST_SERVO.hasMoreElements()) {
                ts = (TEST_SERVO) eTEST_SERVO.nextElement();
                System.out.println(ts.KEY+1
                        + ":test "
                        + ts);
            }
            console.getInt();
            mainSelect = console.i;
            if (mainSelect == TEST_SERVO.moveToPosition.KEY+1) {
                System.out.print("enter position: ");
                console.getLong();
                sp.targetPosition = console.l;
                sp.velRadSec = sp.fastSpeedRadSec;
                System.out.println("(note: velocity set by fast speed parameter in the configuration file)");
                System.out.print("move now or later (1=now)");
                console.getInt();
                System.out.println("");
                if (console.i == 1)
                    sp.moveNow = true;
                else
                    sp.moveNow = false;
                sp.moveCmd = SERVO_MOVE_CMD.posMoveStopCurrent;
                sp.servoCmdProcessed = false;
                processMoveCmdControl();
            } else if (mainSelect == TEST_SERVO.moveToPositionNoStopCheck.KEY+1) {
                System.out.print("enter position: ");
                console.getLong();
                sp.targetPosition = console.l;
                sp.velRadSec = sp.fastSpeedRadSec;
                sp.moveCmd = SERVO_MOVE_CMD.posMoveStart;
                sp.servoCmdProcessed = false;
                processMoveCmdControl();
            } else if (mainSelect == TEST_SERVO.moveAtVelocity.KEY+1) {
                System.out.print("enter velocity (must be positive value): ");
                console.getLong();
                sp.targetVel = console.l;
                if (sp.targetVel < 0) {
                    sp.targetVel = -sp.targetVel;
                    sp.targetVelDir = ROTATION.CCW;
                } else
                    sp.targetVelDir = ROTATION.CW;
                System.out.print(" move now or later (1=now)");
                console.getInt();
                System.out.println("");
                if (console.i == 1)
                    sp.moveNow = true;
                else
                    sp.moveNow = false;
                sp.velRadSec = (double) sp.targetVel / sp.velRadSecToTargetVel();
                sp.moveCmd = SERVO_MOVE_CMD.startVelMove;
                sp.servoCmdProcessed = false;
                // to force call of loadTrajectoryVel(): normally a velocity command is not processed if velocity
                // has not changed and velocity move underway
                sp.lastTargetVel = 0;
                processMoveCmdControl();
            } else if (mainSelect == TEST_SERVO.startDelayedMotion.KEY+1) {
                sp.moveCmd = SERVO_MOVE_CMD.startDelayedMove;
                sp.servoCmdProcessed = false;
                processMoveCmdControl();
            } else if (mainSelect == TEST_SERVO.stopSmoothly.KEY+1) {
                sp.moveCmd = SERVO_MOVE_CMD.stopMoveSmoothly;
                sp.servoCmdProcessed = false;
                processMoveCmdControl();
            } else if (mainSelect == TEST_SERVO.stopAbruptly.KEY+1) {
                sp.moveCmd = SERVO_MOVE_CMD.stopMoveAbruptly;
                sp.servoCmdProcessed = false;
                processMoveCmdControl();
            } else if (mainSelect == TEST_SERVO.turnOnMotorTurnOnAmp.KEY+1) {
                sp.moveCmd = SERVO_MOVE_CMD.turnMotorOnAmpOn;
                sp.servoCmdProcessed = false;
                processMoveCmdControl();
            } else if (mainSelect == TEST_SERVO.turnOffMotorTurnOnAmp.KEY+1) {
                sp.moveCmd = SERVO_MOVE_CMD.turnMotorOffAmpOn;
                sp.servoCmdProcessed = false;
                processMoveCmdControl();
            }
            
            else if (mainSelect == TEST_SERVO.turnOffAmp.KEY+1) {
                sp.moveCmd = SERVO_MOVE_CMD.disableAmp;
                sp.servoCmdProcessed = false;
                processMoveCmdControl();
            } else if (mainSelect == TEST_SERVO.clearStickyBits.KEY+1) {
                clearStickyBits(id);
                getStatus(id);
                displayStickyBits(id);
            } else if (mainSelect == TEST_SERVO.resetPosition.KEY+1) {
                resetPosition(id);
            } else if (mainSelect == TEST_SERVO.changeStatusReturn.KEY+1) {
                System.out.print("enter command byte: ");
                console.getInt();
                defineStatus(id, (byte)console.i);
            } else if (mainSelect == TEST_SERVO.displayStatus.KEY+1) {
                getStatus(id);
                displayStatusReturn(id);
                System.out.println(buildSimCurrentPosVelString(id));
            } else if (mainSelect == TEST_SERVO.moveStatus.KEY+1) {
                getStatus(id);
                displayMove(id);
            }
        }while (mainSelect > 0 && mainSelect <= TEST_SERVO.size());
    }
    
    void testSerialOutput() {
        boolean holdDisplayOutBuf = displayOutBuf;
        
        System.out.println("test of serial output");
        displayOutBuf = true;
        setAddress(SERVO_ID.altDec.KEY);
        System.out.println("outBuf[] should read 0xAA 0x00 0x21 0x01 0xFF 0x21");
        displayOutBuf = holdDisplayOutBuf;
    }
    
    void test() {
        System.out.println("servo tests");
        
        System.out.print("test serial output (y/n)? ");
        console.getString();
        if (console.s.equalsIgnoreCase("y"))
            testSerialOutput();
        
        System.out.print("\ntest servos (y/n)? ");
        console.getString();
        if (console.s.equalsIgnoreCase("y"))
            testServos();
    }
}

