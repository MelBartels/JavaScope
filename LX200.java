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
 * LX200 protocol...
 * search websites for LX200 protocol notes;
 * the serial interrupt routine reads input from an external device outputting LX200 protocol serial commands;
 * periodically new characters stored in the serial input buffer are copied to the LX200 circular queue,
 * these characters are then scanned and acted upon;
 * some custom commands added including a string command that passes commands into cmdScopeList style for execution, ie, ':XZslew_home#'
 *
 * this class needs access to the track class, so design is as follows:
 * object LX200 declared in track class but not instantiated,
 * external object that declares and instantiates track class then instantiates this LX200 class previously
 * declared in track class and passes reference to track to LX200.init(), ie,
 *
 * t = new track();
 * if (cfg.getInstance().LX200Control) {
 *    t.LX200 = new LX200();
 *    t.LX200.init(t, true);
 *    t.LX200.OpenSerialPort();
 * }
 *
 *
 * configuring for ASCOM: use Meade generic LX200 driver via a com port:
 *
 * 1. connect to a second PC/laptop running controlling planetarium program via a serial null modem cable,
 * 2. as above, but use serial to TCP converter like a Lantroix unit and configure Scope II's LX200 ioPort for TCP,
 * 3. if wishing to run Scope II on same PC/laptop as controlling planetarium program, then use software that creates
 *    virtual com ports, ie, the free HW Virtual port manager or similar such as VSP, and VSPK: search web: 'virtual com port';
 *
 *    configure as follows:
 *
 * HW Virtual port manager notes:
 * HW config:
 * 1. set ip to local machine ip (eg 10.146.98.32, if unknown, at command prompt, type 'ipconfig/all')
 * 2. select a port (eg 1000)
 * 3. select a new virtual com port name (eg com5)
 * 4. select server port (same port number as above, eg 1000)
 * 5. settings tab: NVT | Keep connection -> check on
 *
 * Scope II config in the scope.cfg file:
 * 1. turn on LX200 control (eg LX200Control true)
 * 2. locate the LX200 input protocol section and set IOType to TCPServer (eg LX200IOType TCPServer)
 * 3. set both IPPorts to the same port number as in the HW config (eg LX200homeIPPort and LX200RemoteIPPort 1000)
 *
 * example config:
 * [*** LX200 input protocol section ***]
 * LX200Control               true
 * LX200IOType                TCPServer
 * LX200homeIPPort            1000
 * LX200remoteIPName          10.146.98.32
 * LX200RemoteIPPort          1000
 *
 * Controlling software, ie, planetarium program config:
 * 1. select ASCOM generic Meade LX200 driver
 * 2. configure com port to match HW config's com port (eg com5)
 *
 * Verify that all works:
 * 1. fire up HW Virtual port manager and turn on logging: you will see traffic as it occurs
 * 2. fire up Scope II: you should see msg at startup:
 *
 * TCP local server socket connection set on port 1000
 * ...waiting to receive TCP packets...
 *
 * 3. fire up planetarium control program and attempt to connect: you should obtain a successful connection,
 * and see traffic in HW Virtual port manager log window;
 *
 * see in Scope II terminal window:
 *
 * client has closed TCP connection
 * closed TCP server listening on port 1000
 * TCP serverThread ending
 * TCP local server socket connection set on port 1000
 * ...waiting to receive TCP packets...;
 *
 * and in Scope II LX200 status with auto update turned on, the various LC200 commands flowing into Scope II;
 *
 * and finally in the controlling planetarium program, after turning on any necessary 'display telescope' or
 * 'track telescope' options, see the scope's position in realtime.
 * eof
 *
 */
public class LX200 {
    private static final byte ACK = (byte) 6;
    private static final byte OK = (byte) ((char) '1');
    private static final byte DEL = (byte) 127;
    private static final int QUEUE_SIZE = 4096;
    private static final int CMD_ARRAY_SIZE = 256;
    // wait time for port to be quiet before processing commands
    private static final double PORT_QUIET_TIME = units.SEC_TO_RAD/18.;
    
    // data available on the port
    private boolean dataAvailable;
    // data ready to be processed
    private boolean dataReady;
    private double dataAvailableSidT;
    
    IO io;
    
    private CMD_LX200 motorCmd;
    private CMD_LX200 speedCmd;
    private CMD_LX200 focusCmd;
    private CMD_LX200 focusSpeedCmd;
    
    private CMD_DEVICE cmdDevice;
    
    // qPtr is pointer to next available entry in queue;
    // pPtr is pointer to next queue entry to process
    private int qPtr;
    private int pPtr;
    // count of received chars
    private int qCount;
    private String mostRecentString;
    
    // circular queue that LX200 commands are read into from the serial port queue
    private byte queue[] = new byte[QUEUE_SIZE];
    
    // a command to process
    private static final int CMD_SIZE = 80;
    private int cmdPtr;
    private byte cmd[] = new byte[CMD_SIZE];
    
    private int cmdArrayIx;
    // history of commands in a circular queue
    private CMD_LX200 cmdArray[] = new CMD_LX200[CMD_ARRAY_SIZE];
    
    // count of commands processed
    private int cmdCount;
    
    // command as a String
    private String cmdString;
    
    boolean portOpened;
    private int minQualityFind;
    int accumGuideAction;
    boolean displayDiagnostics;
    String logString;
    
    track t;
    
    void init(track t) {
        int ix;
        
        this.t = t;
        
        displayDiagnostics = false;
        motorCmd = CMD_LX200.placeHolder;
        // start with guiding speed: for Guidecam.exe which never sends a speed setting
        speedCmd = CMD_LX200.setMotionRateGuide;
        focusSpeedCmd = CMD_LX200.focusSetFast;
        focusCmd = CMD_LX200.focusQuit;
        for (ix = 0; ix < QUEUE_SIZE; ix++)
            queue[ix] = ' ';
        for (ix = 0; ix < CMD_ARRAY_SIZE; ix++)
            cmdArray[ix] = CMD_LX200.placeHolder;
        cmdArrayIx = 0;
    }
    
    boolean openPort(IO_TYPE ioType,
            String serialPortName,
            int serialBaudRate,
            int homeIPPort,
            String remoteIPName,
            int remoteIPPort,
            String fileLocation,
            boolean trace) {
        ioFactory iof = new ioFactory();
        
        console.stdOutLn("opening LX200 port");
        iof.args.serialPortName = serialPortName;
        iof.args.baudRate = serialBaudRate;
        iof.args.homeIPPort = homeIPPort;
        iof.args.remoteIPName = remoteIPName;
        iof.args.remoteIPPort = remoteIPPort;
        iof.args.fileLocation = fileLocation;
        iof.args.trace = trace;
        io = iof.build(ioType);
        if (io == null) {
            portOpened = false;
            console.errOut("could not open LX200 port");
        } else
            portOpened = true;
        return portOpened;
    }
    
    private void insertCmdIntoArray(CMD_LX200 cmdToAdd) {
        cmdArray[cmdArrayIx] = cmdToAdd;
        cmdArrayIx++;
        cmdArrayIx &= (CMD_ARRAY_SIZE-1);
        cmdCount++;
    }
    
    String buildLogString(int numToDisplay) {
        int begIx, endIx, ix, count;
        
        logString = "LX200 command log; total commands "
                + cmdCount
                + "\n";
        
        begIx = cmdCount - numToDisplay;
        if (begIx < 0)
            begIx = 0;
        begIx &= (CMD_ARRAY_SIZE-1);
        endIx = begIx - 1;
        endIx &= (CMD_ARRAY_SIZE-1);
        ix = begIx;
        count = 0;
        while (ix!=endIx && count<cmdCount && count<numToDisplay) {
            logString += eString.padString(cmdArray[ix].toString(), 25);
            if ((count+1)%5 == 0)
                logString += "\n";
            count++;
            ix++;
            ix &= (CMD_ARRAY_SIZE-1);
        }
        logString += "\n";
        return logString;
    }
    
    void displayLogString(int numToDisplay) {
        console.stdOut(buildLogString(numToDisplay));
    }
    
    void close() {
        if (portOpened) {
            console.stdOutLn("closing LX200 serial port...");
            io.close();
        }
    }
    
    void writeBytePauseUntilXmtFinished(byte b) {
        if (io != null)
            io.writeBytePauseUntilXmtFinished(b);
    }
    
    void writeStringPauseUntilXmtFinished(String s) {
        if (io != null)
            io.writeStringPauseUntilXmtFinished(s);
    }
    
    /**
     * data is read; once port is quiet for a certain time, data is processed or marked incomplete:
     * this is to avoid transmitting reponses when commands are still coming down the pipe
     */
    void readLX200Input() {
        byte b;
        double timeDiff;
        
        // check for and load incoming data
        dataAvailable = false;
        while (io.readSerialBuffer()) {
            b = io.returnByteRead();
            dataAvailable = true;
            queue[qPtr] = b;
            qPtr++;
            qPtr &= (QUEUE_SIZE-1);
            qCount++;
            if (queue[qPtr] == ACK) {
                writeBytePauseUntilXmtFinished((byte) 'A');
                insertCmdIntoArray(CMD_LX200.ack);
                // start fresh, ignoring any previous commands
                pPtr = qPtr;
                pPtr &= (QUEUE_SIZE-1);
                dataAvailable = false;
            }
        }
        
        astroTime.getInstance().calcSidT();
        // as long as incoming data, re-start timer
        if (dataAvailable) {
            dataAvailableSidT = astroTime.getInstance().sidT.rad;
            dataReady = true;
        }
        // no (more) incoming data
        else
            // only get here if data available and data ready
            if (dataReady) {
            // check for time since data was last ready
            timeDiff = astroTime.getInstance().sidT.rad - dataAvailableSidT;
            timeDiff = eMath.validRad(timeDiff);
            // after the port has been quiet for an amount of time, process all commands in queue
            if (timeDiff > PORT_QUIET_TIME) {
                processCmds();
                dataReady = false;
            }
            }
    }
    
    /**
     * instead of reading from LX200 queue, read from passed in string
     */
    void readLX200Input(String cmd) {
        int ix;
        
        for (ix = 0; ix < cmd.length(); ix++) {
            queue[qPtr] = (byte) cmd.charAt(ix);
            qPtr++;
            qPtr &= (QUEUE_SIZE-1);
            qCount++;
        }
        if (queue[qPtr] == ACK) {
            writeBytePauseUntilXmtFinished((byte) 'A');
            insertCmdIntoArray(CMD_LX200.ack);
            // start fresh, ignoring any previous commands
            pPtr = qPtr;
            pPtr &= (QUEUE_SIZE-1);
        } else
            processCmds();
    }
    
    String buildMostRecentString(int num) {
        int ix;
        
        mostRecentString = "LX200 received chars: ";
        
        if (num > qCount)
            num = qCount;
        ix = qPtr - num;
        if (ix < 0)
            ix += QUEUE_SIZE;
        while (ix != qPtr) {
            mostRecentString += (char) queue[ix];
            ix++;
            if (ix >= QUEUE_SIZE)
                ix = 0;
        }
        mostRecentString += " end of LX200 received chars\n";
        
        return mostRecentString;
    }
    
    void displayMostRecent(int num) {
        console.stdOut(buildMostRecentString(num));
    }
    
    private void processCmds() {
        boolean buildCmd = false;
        cmdPtr = 0;
        
        // all commands start with ':' and end with '#'
        while (pPtr != qPtr) {
            if (buildCmd) {
                // find ending '#'
                if (queue[pPtr] == (byte) '#') {
                    cmd[cmdPtr] = queue[pPtr];
                    cmdPtr++;
                    process_A_Cmd();
                    buildCmd = false;
                    cmdPtr = 0;
                }
                // else continue building command
                else {
                    cmd[cmdPtr] = queue[pPtr];
                    cmdPtr++;
                    if (cmdPtr > CMD_SIZE) {
                        process_A_Cmd();
                        buildCmd = false;
                        cmdPtr = 0;
                    }
                }
            }
            // else not yet building a command
            else
                // find beginning ':'
                if (queue[pPtr] == (byte) ':') {
                buildCmd = true;
                cmdPtr = 0;
                }
            
            pPtr++;
            pPtr &= (QUEUE_SIZE-1);
        }
    }
    
    String buildCmdString() {
        int ix;
        
        cmdString = "";
        for (ix = 0; ix < cmdPtr; ix++)
            cmdString += (char) cmd[ix];
        
        return cmdString;
    }
    
    private void display_A_Cmd() {
        console.stdOutLn("\nprocess LX200 command: " + buildCmdString());
    }
    
    private void process_A_Cmd() {
        int ix;
        int holdButtons;
        String s;
        execStringCmdScope esc;
        
        if (displayDiagnostics)
            display_A_Cmd();
        
        switch(cmd[0]) {
            case '$':
                switch(cmd[1]) {
                    case 'Q':
                        insertCmdIntoArray(CMD_LX200.PECOnOff);
                        cfg.getInstance().spa.PECActive = !cfg.getInstance().spa.PECActive;
                        cfg.getInstance().spz.PECActive = !cfg.getInstance().spz.PECActive;
                        cfg.getInstance().spr.PECActive = !cfg.getInstance().spr.PECActive;
                        cfg.getInstance().spf.PECActive = !cfg.getInstance().spf.PECActive;
                        break;
                    default:
                        insertCmdIntoArray(CMD_LX200.unknown);
                }
                break;
            case 'h':
                switch(cmd[1]) {
                    case 'P':
                        insertCmdIntoArray(CMD_LX200.slewHomePosition);
                        esc = new execStringCmdScope("LX200", t, "cmd_scope_slew_home");
                        esc.checkProcessCmd();
                        break;
                    default:
                        insertCmdIntoArray(CMD_LX200.unknown);
                }
                break;
            case 'r':
                switch(cmd[1]) {
                    case '+':
                        insertCmdIntoArray(CMD_LX200.FROn);
                        cfg.getInstance().spr.track = true;
                        break;
                    case '-':
                        insertCmdIntoArray(CMD_LX200.FROff);
                        cfg.getInstance().spr.track = false;
                        break;
                    default:
                        insertCmdIntoArray(CMD_LX200.unknown);
                }
                break;
            case 'A':
                switch(cmd[1]) {
                    case 'A':
                        insertCmdIntoArray(CMD_LX200.alignAltaz);
                        t.c.initConvertAltaz();
                        break;
                    case 'C':
                        switch(cmd[1]) {
                            case 'K':
                                insertCmdIntoArray(CMD_LX200.alignACK);
                                if (cfg.getInstance().initState.KEY == INIT_STATE.altazAlign.KEY
                                        || cfg.getInstance().initState.KEY == INIT_STATE.altAltAlign.KEY)
                                    writeStringPauseUntilXmtFinished("A#");
                                else if (cfg.getInstance().initState.KEY == INIT_STATE.noAlign.KEY)
                                    writeStringPauseUntilXmtFinished("L#");
                                else
                                    writeStringPauseUntilXmtFinished("P#");                                
                                break;
                            default:
                                insertCmdIntoArray(CMD_LX200.unknown);
                        }
                        break;
                    case 'L':
                        insertCmdIntoArray(CMD_LX200.alignLand);
                        t.c.initConvertAltaz();
                        break;
                    case 'P':
                        insertCmdIntoArray(CMD_LX200.alignPolar);
                        t.c.initConvertEquat();
                        break;
                    default:
                        insertCmdIntoArray(CMD_LX200.unknown);
                }
                break;
                // increases (b+) or decreases (b-) reticle brightness, or sets to one of the flashing modes (B0, b1, b2 or B3)
            case 'B':
                switch(cmd[1]) {
                    case '+':
                    case '-':
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                        insertCmdIntoArray(CMD_LX200.reticle);
                        break;
                    default:
                        insertCmdIntoArray(CMD_LX200.unknown);
                }
                break;
            case 'C':
                switch(cmd[1]) {
                    case 'M':
                        insertCmdIntoArray(CMD_LX200.sync);
                        // reset to input equatorial coordinates
                        cfg.getInstance().current.ra.rad = t.in.ra.rad;
                        cfg.getInstance().current.dec.rad = t.in.dec.rad;
                        getRa();
                        getDec();
                        t.resetToCurrentEquatCoord();
                        break;
                    default:
                        insertCmdIntoArray(CMD_LX200.unknown);
                }
                break;
            case 'D':
                insertCmdIntoArray(CMD_LX200.distance);
                getDistance();
                break;
            case 'F':
                switch(cmd[1]) {
                    case '+':
                        insertCmdIntoArray(CMD_LX200.focusOut);
                        focusCmd = CMD_LX200.focusOut;
                        cfg.getInstance().spf.cmdDevice = CMD_DEVICE.LX200;
                        if (focusSpeedCmd == CMD_LX200.focusSetSlow)
                            cfg.getInstance().spf.velRadSec = cfg.getInstance().spf.slowSpeedRadSec;
                        else
                            cfg.getInstance().spf.velRadSec = cfg.getInstance().spf.fastSpeedRadSec;
                        cfg.getInstance().spf.targetVelDir = ROTATION.CW;
                        cfg.getInstance().spf.moveCmd = SERVO_MOVE_CMD.startVelMove;
                        cfg.getInstance().spf.moveNow = true;
                        cfg.getInstance().spf.servoCmdProcessed = false;
                        t.processMoveCmdControl();
                        break;
                    case '-':
                        insertCmdIntoArray(CMD_LX200.focusIn);
                        focusCmd = CMD_LX200.focusIn;
                        if (focusSpeedCmd == CMD_LX200.focusSetSlow)
                            cfg.getInstance().spf.velRadSec = cfg.getInstance().spf.slowSpeedRadSec;
                        else
                            cfg.getInstance().spf.velRadSec = cfg.getInstance().spf.fastSpeedRadSec;
                        cfg.getInstance().spf.targetVelDir = ROTATION.CCW;
                        cfg.getInstance().spf.moveCmd = SERVO_MOVE_CMD.startVelMove;
                        cfg.getInstance().spf.moveNow = true;
                        cfg.getInstance().spf.servoCmdProcessed = false;
                        t.processMoveCmdControl();
                        break;
                    case 'F':
                        insertCmdIntoArray(CMD_LX200.focusSetFast);
                        focusSpeedCmd = CMD_LX200.focusSetFast;
                        break;
                    case 'Q':
                        insertCmdIntoArray(CMD_LX200.focusQuit);
                        focusCmd = CMD_LX200.focusQuit;
                        cfg.getInstance().spf.moveCmd = SERVO_MOVE_CMD.stopMoveSmoothly;
                        cfg.getInstance().spf.servoCmdProcessed = false;
                        t.processMoveCmdControl();
                        break;
                    case 'S':
                        insertCmdIntoArray(CMD_LX200.focusSetSlow);
                        focusSpeedCmd = CMD_LX200.focusSetSlow;
                        break;
                    default:
                        insertCmdIntoArray(CMD_LX200.unknown);
                }
                break;
            case 'G':
                switch(cmd[1]) {
                    case 'A':
                        insertCmdIntoArray(CMD_LX200.getAlt);
                        getAlt();
                        break;
                    case 'a':
                        insertCmdIntoArray(CMD_LX200.getLocalT12);
                        getLocalT();
                        break;
                    case 'C':
                        insertCmdIntoArray(CMD_LX200.getDate);
                        getDate();
                        break;
                    case 'c':
                        insertCmdIntoArray(CMD_LX200.getClockStatus);
                        writeStringPauseUntilXmtFinished("(24)#");
                        break;
                    case 'D':
                    case 'd':
                        insertCmdIntoArray(CMD_LX200.getDec);
                        getDec();
                        break;
                        /**
                         * command is GF for get FIELD radius of the field operation
                         * gets the field radius of the FIELD operation and returns NNN#
                         */
                    case 'F':
                        insertCmdIntoArray(CMD_LX200.getField);
                        writeStringPauseUntilXmtFinished("100#");
                        break;
                    case 'G':
                        insertCmdIntoArray(CMD_LX200.getTz);
                        getTz();
                        break;
                    case 'g':
                        insertCmdIntoArray(CMD_LX200.getLongitude);
                        getLong();
                        break;
                        /**
                         * :GL# is get time in 24 hr format while :Ga# is get time in 12 hr format, but
                         * tests with LX200 scopes show that both return 24 hr format
                         */
                    case 'L':
                        insertCmdIntoArray(CMD_LX200.getLocalT24);
                        getLocalT();
                        break;
                        // site name
                    case 'M':
                    case 'N':
                    case 'O':
                        /**
                         * gets site name (XYZ): GM GN GO gp correspond to 1 through 4; returns XYZ#
                         */
                    case 'P':
                        insertCmdIntoArray(CMD_LX200.getSiteName);
                        writeStringPauseUntilXmtFinished(cfg.getInstance().siteName + "#");
                        break;
                    case 'q':
                        insertCmdIntoArray(CMD_LX200.getMinQualityFind);
                        getMinQualityFind();
                        break;
                    case 'R':
                    case 'r':
                        insertCmdIntoArray(CMD_LX200.getRa);
                        getRa();
                        break;
                    case 'S':
                        insertCmdIntoArray(CMD_LX200.getSidT);
                        getSidT();
                        break;
                    case 't':
                        insertCmdIntoArray(CMD_LX200.getLat);
                        getLat();
                        break;
                    case 'V':
                        switch(cmd[2]) {
                            case 'D':
                                insertCmdIntoArray(CMD_LX200.getFirmwareDate);
                                writeStringPauseUntilXmtFinished(eString.BUILD_DATE + "#");
                                break;
                            case 'F':
                                insertCmdIntoArray(CMD_LX200.getFirmwareIDString);
                                writeStringPauseUntilXmtFinished("BartelsStepper#");
                                break;
                            case 'P':
                                insertCmdIntoArray(CMD_LX200.getProductName);
                                writeStringPauseUntilXmtFinished(eString.PGM_NAME + "#");
                                break;
                            default:
                                insertCmdIntoArray(CMD_LX200.unknown);
                        }
                        break;
                    case 'Z':
                        insertCmdIntoArray(CMD_LX200.getAz);
                        getAz();
                        break;
                    default:
                        insertCmdIntoArray(CMD_LX200.unknown);
                }
                break;
            case 'L':
                switch(cmd[1]) {
                    /**
                     * library object information - might expect (from ACP):
                     * ' Read the scope's current object info and display in
                     * ' "friendly" format. The format of s.ObjectInformation() is:
                     * ' 00000000011111111112222222223333
                     * ' 12345678901234567890123456789012
                     * ' --------------------------------
                     * '  NGC4594 VG GAL MAG 8.3 SZ  8.9'
                     * '  NGC0     Coordinates Only
                     * ' STAR100     STARMAG 1.4
                     * '  JUPITER  MAG-2.7 SZ   46"
                     * '    M1    EX PNEBMAG 8.4 SZ  6.0'
                     * '    M20   EX OPNBMAG 6.3 SZ 29.0'
                     * '    M21   GD OPENMAG 5.9 SZ 13.0'
                     * '    M28   GD GLOBMAG 6.9 SZ 11.2'
                     * '    M78   VG DNEBMAG11.3 SZ  8.0'
                     */
                    case 'I':
                        insertCmdIntoArray(CMD_LX200.liCommand);
                        writeBytePauseUntilXmtFinished((byte) '#');
                        break;
                        // sets the NGC object library:  0 is the NGC library, 1 is the IC library and 2 is the UGC library;
                        // value ignored
                    case 'o':
                        insertCmdIntoArray(CMD_LX200.setNGCLibrary);
                        writeBytePauseUntilXmtFinished(OK);
                        break;
                        // sets the STAR object library type: 0 is the STAR library; 1 is the SAO library, and 2 is the GCVS library;
                        // value ignored
                    case 's':
                        insertCmdIntoArray(CMD_LX200.setStarLibrary);
                        writeBytePauseUntilXmtFinished(OK);
                        break;
                    default:
                        insertCmdIntoArray(CMD_LX200.unknown);
                }
                break;
            case 'M':
                switch(cmd[1]) {
                    // LX200 GPS guide command :Mgx where x is direction n,s,e, or w, followed by 4 digit time in milliseconds
                    case 'g':
                        switch(cmd[2]) {
                            case 'n':
                                insertCmdIntoArray(CMD_LX200.nudgeGuideNorth);
                                nudgeGuide(CMD_LX200.nudgeGuideNorth);
                                break;
                            case 's':
                                insertCmdIntoArray(CMD_LX200.nudgeGuideSouth);
                                nudgeGuide(CMD_LX200.nudgeGuideSouth);
                                break;
                            case 'e':
                                insertCmdIntoArray(CMD_LX200.nudgeGuideEast);
                                nudgeGuide(CMD_LX200.nudgeGuideEast);
                                break;
                            case 'w':
                                insertCmdIntoArray(CMD_LX200.nudgeGuideWest);
                                nudgeGuide(CMD_LX200.nudgeGuideWest);
                                break;
                            default:
                                insertCmdIntoArray(CMD_LX200.unknown);
                        }
                        break;
                    case 'n':
                        insertCmdIntoArray(CMD_LX200.moveDirRateNorth);
                        motorCmd = CMD_LX200.moveDirRateNorth;
                        moveDirRate();
                        break;
                    case 's':
                        insertCmdIntoArray(CMD_LX200.moveDirRateSouth);
                        motorCmd = CMD_LX200.moveDirRateSouth;
                        moveDirRate();
                        break;
                    case 'e':
                        insertCmdIntoArray(CMD_LX200.moveDirRateEast);
                        motorCmd = CMD_LX200.moveDirRateEast;
                        moveDirRate();
                        break;
                    case 'w':
                        insertCmdIntoArray(CMD_LX200.moveDirRateWest);
                        motorCmd = CMD_LX200.moveDirRateWest;
                        moveDirRate();
                        break;
                    case 'G':
                    case 'S':
                        insertCmdIntoArray(CMD_LX200.startSlew);
                        writeBytePauseUntilXmtFinished((byte) '0');
                        moveSlew();
                        break;
                    default:
                        insertCmdIntoArray(CMD_LX200.unknown);
                }
                break;
            case 'Q':
                switch(cmd[1]) {
                    case '#':
                        insertCmdIntoArray(CMD_LX200.stopSlew);
                        motorCmd = CMD_LX200.stopSlew;
                        stopSlew();
                        break;
                    case 'n':
                        insertCmdIntoArray(CMD_LX200.stopMotionNorth);
                        motorCmd = CMD_LX200.stopMotionNorth;
                        stopMotion();
                        break;
                    case 's':
                        insertCmdIntoArray(CMD_LX200.stopMotionSouth);
                        motorCmd = CMD_LX200.stopMotionSouth;
                        stopMotion();
                        break;
                    case 'e':
                        insertCmdIntoArray(CMD_LX200.stopMotionEast);
                        motorCmd = CMD_LX200.stopMotionEast;
                        stopMotion();
                        break;
                    case 'w':
                        insertCmdIntoArray(CMD_LX200.stopMotionWest);
                        motorCmd = CMD_LX200.stopMotionWest;
                        stopMotion();
                        break;
                    default:
                        insertCmdIntoArray(CMD_LX200.unknown);
                }
                break;
            case 'R':
                switch(cmd[1]) {
                    case '1':   // new sub for SetMotionRateGuide
                    case 'G':
                        insertCmdIntoArray(CMD_LX200.setMotionRateGuide);
                        speedCmd = CMD_LX200.setMotionRateGuide;
                        break;
                    case 'C':
                        insertCmdIntoArray(CMD_LX200.setMotionRateCenter);
                        speedCmd = CMD_LX200.setMotionRateCenter;
                        break;
                    case 'g':
                        insertCmdIntoArray(CMD_LX200.getGuideArcsecSec);
                        setGuideArcsecSec();
                        break;
                    case 'M':
                        insertCmdIntoArray(CMD_LX200.setMotionRateFind);
                        speedCmd = CMD_LX200.setMotionRateFind;
                        break;
                    case 'S':
                        insertCmdIntoArray(CMD_LX200.setMotionRateSlew);
                        speedCmd = CMD_LX200.setMotionRateSlew;
                        break;
                    default:
                        insertCmdIntoArray(CMD_LX200.unknown);
                }
                break;
            case 'S':
                switch(cmd[1]) {
                    // sets the brighter magnitude limit for the FIND operation; example is Sb+08.2; value ignored
                    case 'b':
                        insertCmdIntoArray(CMD_LX200.setBrightMagLimitFind);
                        writeBytePauseUntilXmtFinished(OK);
                        break;
                    case 'C':
                        insertCmdIntoArray(CMD_LX200.setDate);
                        setCurrentDate();
                        break;
                    case 'd':
                        insertCmdIntoArray(CMD_LX200.setDec);
                        writeBytePauseUntilXmtFinished(OK);
                        setDec();
                        break;
                        // sets the field radius of the FIELD operation; example SF 010 value ignored
                    case 'F':
                        insertCmdIntoArray(CMD_LX200.setField);
                        writeBytePauseUntilXmtFinished(OK);
                        break;
                        // sets the fainter magnitude limit for the FIND operation; example is Sf-02.0; value ignored
                    case 'f':
                        insertCmdIntoArray(CMD_LX200.setFaintMagLimitFind);
                        writeBytePauseUntilXmtFinished(OK);
                        break;
                        // set GMT offset; ignored
                    case 'G':
                        insertCmdIntoArray(CMD_LX200.setGMTOffset);
                        writeBytePauseUntilXmtFinished(OK);
                        break;
                        // set longitude; ignored
                    case 'g':
                        insertCmdIntoArray(CMD_LX200.setLat);
                        writeBytePauseUntilXmtFinished(OK);
                        break;
                        // Sh DD# sets the current 'higher' limit; value ignored
                    case 'h':
                        insertCmdIntoArray(CMD_LX200.setCurrentHigherLimit);
                        writeBytePauseUntilXmtFinished(OK);
                        break;
                    case 'L':
                        insertCmdIntoArray(CMD_LX200.setLocalT);
                        setLocalT();
                        break;
                        // sets the larger size limit for the FIND operation
                    case 'l':
                        insertCmdIntoArray(CMD_LX200.largeSizeLimitFind);
                        writeBytePauseUntilXmtFinished(OK);
                        break;
                        // set site number; ignored
                    case 'M':
                    case 'N':
                    case 'O':
                    case 'P':
                        insertCmdIntoArray(CMD_LX200.setSiteNumber_S);
                        writeBytePauseUntilXmtFinished(OK);
                        break;
                        // steps to the next minimum quality for the FIND operation
                    case 'q':
                        insertCmdIntoArray(CMD_LX200.nextMinQualityFind);
                        minQualityFind++;
                        if (minQualityFind > 6)
                            minQualityFind = 0;
                        break;
                    case 'r':
                        insertCmdIntoArray(CMD_LX200.setRa);
                        writeBytePauseUntilXmtFinished(OK);
                        setRa();
                        break;
                    case 'S':
                        insertCmdIntoArray(CMD_LX200.setSidT);
                        setSidT();
                        break;
                        // sets the smaller size limit for the FIND operation
                    case 's':
                        insertCmdIntoArray(CMD_LX200.smallSizeLimitFind);
                        writeBytePauseUntilXmtFinished(OK);
                        break;
                        // set latitude; ignored
                    case 't':
                        insertCmdIntoArray(CMD_LX200.setLat);
                        writeBytePauseUntilXmtFinished(OK);
                        break;
                        // set max slew rate in deg/sec: single digit value of 2, 3, or 4
                    case 'w':
                        insertCmdIntoArray(CMD_LX200.swCommand);
                        setMaxSlew();
                        writeBytePauseUntilXmtFinished(OK);
                        break;
                        /**
                         * command is 'SyGPDCO' and gets the ‘type' string for the FIND operation:
                         * capitalized means on, small letters means off: galaxy, planetary nebula, diffuse,
                         * globular cluster, open cluster: ACP sends SyGPdco
                         */
                    case 'y':
                        insertCmdIntoArray(CMD_LX200.setTypeStringForFind);
                        writeBytePauseUntilXmtFinished(OK);
                        break;
                    default:
                        insertCmdIntoArray(CMD_LX200.unknown);
                }
                break;
            case 'T':
                switch(cmd[1]) {
                    case 'Q':
                        insertCmdIntoArray(CMD_LX200.timeQuartz);
                        t.zeroDrift();
                        break;
                    default:
                        insertCmdIntoArray(CMD_LX200.unknown);
                }
                break;
            case 'U':
                insertCmdIntoArray(CMD_LX200.toggleLongFormat);
                cfg.getInstance().LX200_LongFormat = !cfg.getInstance().LX200_LongFormat;
                break;
            case 'w':
                switch(cmd[1]) {
                    // set current site number
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                        insertCmdIntoArray(CMD_LX200.setSiteNumber_W);
                        break;
                    default:
                        insertCmdIntoArray(CMD_LX200.unknown);
                }
                break;
                // customized extensions to the LX200 protocol
            case 'X':
                switch(cmd[1]) {
                    case 'A':
                        switch(cmd[2]) {
                            case 'M':
                                insertCmdIntoArray(CMD_LX200.getASCOMmountType);
                                getASCOMmountType();
                                break;
                            default:
                                insertCmdIntoArray(CMD_LX200.unknown);
                        }
                        break;
                    case 'G':
                        switch(cmd[2]) {
                            case 'f':
                                insertCmdIntoArray(CMD_LX200.getFocusSlowSpeedArcsecSec);
                                writeStringPauseUntilXmtFinished(eString.doubleToString(cfg.getInstance().spf.slowSpeedArcsecSec, 3, 0));
                                break;
                            case 'F':
                                insertCmdIntoArray(CMD_LX200.getFocusFastSpeedDegSec);
                                writeStringPauseUntilXmtFinished(eString.doubleToString(cfg.getInstance().spf.fastSpeedDegSec, 3, 0));
                                break;
                            case 'G':
                                insertCmdIntoArray(CMD_LX200.getGuideArcsecSec);
                                // use 'A' motor's value
                                writeStringPauseUntilXmtFinished(eString.intToString((int) cfg.getInstance().spa.guideArcsecSec, 4));
                                break;
                            case 'P':
                                insertCmdIntoArray(CMD_LX200.getFocusPos);
                                writeStringPauseUntilXmtFinished(eString.longToString(cfg.getInstance().spf.actualPosition, 4));
                                break;
                                // field rotation in degrees 999.99
                            case 'R':
                                insertCmdIntoArray(CMD_LX200.sendFieldR);
                                writeStringPauseUntilXmtFinished(eString.doubleToString(t.c.fieldRotation*units.RAD_TO_DEG, 3, 2));
                                break;
                            default:
                                insertCmdIntoArray(CMD_LX200.unknown);
                        }
                        break;
                    case 'H':
                        if (cmd[2] >= 'a' && cmd[2] < 'a' + HANDPAD_MODE.size()) {
                            insertCmdIntoArray(CMD_LX200.setHandpadMode);
                            cfg.getInstance().handpadMode = HANDPAD_MODE.matchKey(cmd[2]-'a');
                        } else if (cmd[2] == 'L') {
                            insertCmdIntoArray(CMD_LX200.handpadLeftKey);
                            holdButtons = t.buttons;
                            t.buttons = handpadDesignBase.LEFT_KEY;
                            t.processHandpadModeSwitch();
                            t.buttons = holdButtons;
                        } else if (cmd[2] == 'R') {
                            insertCmdIntoArray(CMD_LX200.handpadRightKey);
                            holdButtons = t.buttons;
                            t.buttons = handpadDesignBase.RIGHT_KEY;
                            t.processHandpadModeSwitch();
                            t.buttons = holdButtons;
                        } else
                            insertCmdIntoArray(CMD_LX200.unknown);
                        break;
                    case 'I':
                        switch(cmd[2]) {
                            case '1':
                                insertCmdIntoArray(CMD_LX200.setInit1);
                                // use current altaz and in equat coordinates to init with
                                cfg.getInstance().current.ra.rad = t.in.ra.rad;
                                cfg.getInstance().current.dec.rad = t.in.dec.rad;
                                t.c.initMatrix(1, WHY_INIT.LX200);
                                break;
                            case '2':
                                insertCmdIntoArray(CMD_LX200.setInit2);
                                if (cfg.getInstance().one.init) {
                                    // use current altaz and in equat coordinates to init with
                                    cfg.getInstance().current.ra.rad = t.in.ra.rad;
                                    cfg.getInstance().current.dec.rad = t.in.dec.rad;
                                    t.c.initMatrix(2, WHY_INIT.LX200);
                                    t.getEquatSetTarget();
                                    t.setSiTechAlt();
                                    break;
                                } else
                                    common.badExit("LX200 Init2 command received but no init 1");
                                break;
                            case '3':
                                insertCmdIntoArray(CMD_LX200.setInit3);
                                if (cfg.getInstance().one.init && cfg.getInstance().initialized()) {
                                    // use current altaz and in equat coordinates to init with
                                    cfg.getInstance().current.ra.rad = t.in.ra.rad;
                                    cfg.getInstance().current.dec.rad = t.in.dec.rad;
                                    t.c.initMatrix(3, WHY_INIT.LX200);
                                    t.setSiTechAlt();
                                    break;
                                } else
                                    common.badExit("LX200 Init3 command received but no init 2");
                                break;
                            default:
                                insertCmdIntoArray(CMD_LX200.unknown);
                        }
                        break;
                    case 'P':
                        insertCmdIntoArray(CMD_LX200.getASCOMmountType);
                        getPierSide();
                        break;
                    case 'S':
                        switch(cmd[2]) {
                            case 'f':
                                insertCmdIntoArray(CMD_LX200.setFocusSlowSpeedArcsecSec);
                                setFocusSlowSpeedArcsecSec();
                                break;
                            case 'F':
                                insertCmdIntoArray(CMD_LX200.setFocusFastSpeedDegSec);
                                setFocusFastSpeedDegSec();
                                break;
                            case 'G':
                                insertCmdIntoArray(CMD_LX200.setGuideArcsecSec);
                                setGuideArcsecSec();
                                break;
                            case 'N':
                                insertCmdIntoArray(CMD_LX200.setObjectName);
                                s = "";
                                for (ix = 3; cmd[ix] !=(byte) '#' && ix < CMD_SIZE; ix++)
                                    s += (char) cmd[ix];
                                t.in.objName = s;
                                break;
                            default:
                                insertCmdIntoArray(CMD_LX200.unknown);
                        }
                        break;
                        // accept command but don't do anything: command for scope.exe DOS stepper version
                    case 'X':
                        insertCmdIntoArray(CMD_LX200.clearDisplay);
                        break;
                        // send string command to cmdScopeList executor
                    case 'Z':
                        insertCmdIntoArray(CMD_LX200.stringCommand);
                        s = "";
                        for (ix = 2; cmd[ix] !=(byte) '#' && ix < CMD_SIZE; ix++)
                            s += (char) cmd[ix];
                        esc = new execStringCmdScope("LX200", t, s);
                        esc.checkProcessCmd();
                        break;
                    default:
                        insertCmdIntoArray(CMD_LX200.unknown);
                }
                break;
            default:
                insertCmdIntoArray(CMD_LX200.unknown);
        }
    }
    
    private void moveSlew() {
        speedCmd = motorCmd = CMD_LX200.startSlew;
        t.target.ra.rad = t.in.ra.rad;
        t.target.dec.rad = t.in.dec.rad;
        if (t.guideActive)
            t.stopAllGuide();
        // merely sets the track flags
        t.turnTrackingOn();
    }
    
    private void moveDirRate() {
        int id;
        
        if (speedCmd == CMD_LX200.setMotionRateGuide) {
            if (!t.guideActive)
                t.initAllGuide();
        } else
            if (t.guideActive)
                t.stopAllGuide();
        
        if (speedCmd == CMD_LX200.setMotionRateGuide) {
            if (motorCmd == CMD_LX200.moveDirRateNorth)
                accumGuideAction = handpadDesignBase.UP_KEY;
            else if (motorCmd == CMD_LX200.moveDirRateSouth)
                accumGuideAction = handpadDesignBase.DOWN_KEY;
            else if (motorCmd == CMD_LX200.moveDirRateEast)
                accumGuideAction = handpadDesignBase.CCW_KEY;
            else if (motorCmd == CMD_LX200.moveDirRateWest)
                accumGuideAction = handpadDesignBase.CW_KEY;
        } else {
            if (motorCmd == CMD_LX200.moveDirRateNorth || motorCmd == CMD_LX200.moveDirRateSouth)
                id = SERVO_ID.altDec.KEY;
            else
                id = SERVO_ID.azRa.KEY;
            if (motorCmd == CMD_LX200.moveDirRateNorth || motorCmd == CMD_LX200.moveDirRateWest)
                cfg.getInstance().servoParm[id].targetVelDir = ROTATION.CW;
            else
                cfg.getInstance().servoParm[id].targetVelDir = ROTATION.CCW;
            if (speedCmd == CMD_LX200.setMotionRateSlew)
                cfg.getInstance().servoParm[id].velRadSec = cfg.getInstance().servoParm[id].fastSpeedRadSec;
            else
                cfg.getInstance().servoParm[id].velRadSec = cfg.getInstance().servoParm[id].slowSpeedRadSec;
            cfg.getInstance().servoParm[id].cmdDevice = CMD_DEVICE.LX200;
            cfg.getInstance().servoParm[id].moveCmd = SERVO_MOVE_CMD.startVelMove;
            cfg.getInstance().servoParm[id].moveNow = true;
            cfg.getInstance().servoParm[id].servoCmdProcessed = false;
            t.processMoveCmdControl();
        }
    }
    
    private void nudgeGuide(CMD_LX200 nudgeDir) {
        int guideTimeMilliSec;
        
        if (!t.guideActive)
            t.initAllGuide();
        
        guideTimeMilliSec = return4digitInt();
        cfg.getInstance().spa.guideIncr = cfg.getInstance().spa.guideRadSec * guideTimeMilliSec / 1000.;
        cfg.getInstance().spz.guideIncr = cfg.getInstance().spz.guideRadSec * guideTimeMilliSec / 1000.;
        
        if (cfg.getInstance().handpadMode == HANDPAD_MODE.handpadModeGuideStayRotate) {
            t.calcGuideFRAngle();
            if (nudgeDir == CMD_LX200.nudgeGuideNorth) {
                cfg.getInstance().spa.accumGuideRad -= Math.cos(t.guideFRAngle) * cfg.getInstance().spa.guideIncr;
                cfg.getInstance().spz.accumGuideRad -= Math.sin(t.guideFRAngle) * cfg.getInstance().spz.guideIncr;
            }
            if (nudgeDir == CMD_LX200.nudgeGuideSouth) {
                cfg.getInstance().spa.accumGuideRad += Math.cos(t.guideFRAngle) * cfg.getInstance().spa.guideIncr;
                cfg.getInstance().spz.accumGuideRad += Math.sin(t.guideFRAngle) * cfg.getInstance().spz.guideIncr;
            }
            if (nudgeDir == CMD_LX200.nudgeGuideWest) {
                cfg.getInstance().spz.accumGuideRad -= Math.cos(t.guideFRAngle) * cfg.getInstance().spz.guideIncr;
                cfg.getInstance().spa.accumGuideRad += Math.sin(t.guideFRAngle) * cfg.getInstance().spa.guideIncr;
            }
            if (nudgeDir == CMD_LX200.nudgeGuideEast) {
                cfg.getInstance().spz.accumGuideRad += Math.cos(t.guideFRAngle) * cfg.getInstance().spz.guideIncr;
                cfg.getInstance().spa.accumGuideRad -= Math.sin(t.guideFRAngle) * cfg.getInstance().spa.guideIncr;
            }
        } else {
            if (nudgeDir == CMD_LX200.nudgeGuideNorth)
                cfg.getInstance().spa.accumGuideRad -= cfg.getInstance().spa.guideIncr;
            if (nudgeDir == CMD_LX200.nudgeGuideSouth)
                cfg.getInstance().spa.accumGuideRad += cfg.getInstance().spa.guideIncr;
            if (nudgeDir == CMD_LX200.nudgeGuideWest)
                cfg.getInstance().spz.accumGuideRad -= cfg.getInstance().spz.guideIncr;
            if (nudgeDir == CMD_LX200.nudgeGuideEast)
                cfg.getInstance().spz.accumGuideRad += cfg.getInstance().spz.guideIncr;
        }
        
        cfg.getInstance().spa.lagr.add(cfg.getInstance().spa.accumGuideRad, astroTime.getInstance().sidT.rad);
        cfg.getInstance().spz.lagr.add(cfg.getInstance().spz.accumGuideRad, astroTime.getInstance().sidT.rad);
        
        t.writeToGuideArrays();
    }
    
    private void stopSlew() {
        int id;
        
        if (cfg.getInstance().LX200_ContinueTrack)
            for (id = 0; id < SERVO_ID.size(); id++)
                cfg.getInstance().servoParm[id].holdTrack = cfg.getInstance().servoParm[id].track;
        
        t.turnTrackingOffStopAllMotors();
        
        if (cfg.getInstance().LX200_ContinueTrack)
            for (id = 0; id < SERVO_ID.size(); id++)
                cfg.getInstance().servoParm[id].track = cfg.getInstance().servoParm[id].holdTrack;
    }
    
    private void stopMotion() {
        int id;
        
        if (speedCmd == CMD_LX200.setMotionRateGuide)
            accumGuideAction = 0;
        else {
            if (motorCmd == CMD_LX200.stopMotionNorth || motorCmd == CMD_LX200.stopMotionSouth)
                id = SERVO_ID.altDec.KEY;
            else
                id = SERVO_ID.azRa.KEY;
            cfg.getInstance().servoParm[id].moveCmd = SERVO_MOVE_CMD.stopMoveSmoothly;
            cfg.getInstance().servoParm[id].servoCmdProcessed = false;
            t.processMoveCmdControl();
        }
    }
    
    /**
     * if slewing, return <DEL> or 0x7F followed by #, else return only a #
     */
    private void getDistance() {
        if (t.slewing)
            writeStringPauseUntilXmtFinished(DEL + "#");
        else
            writeStringPauseUntilXmtFinished("#");
    }
    
    /**
     * ECU sets its longFormat flag based on style of first coordinate returned
     */
    private void getRa() {
        cfg.getInstance().current.ra.getStringLX200(cfg.getInstance().LX200_LongFormat);
        if (displayDiagnostics)
            console.stdOutLn("GetRa: " + cfg.getInstance().current.ra.stringLX200);
        writeStringPauseUntilXmtFinished(cfg.getInstance().current.ra.stringLX200);
    }
    
    private void getDec() {
        boolean signed = true;
        
        cfg.getInstance().current.dec.getStringLX200(cfg.getInstance().LX200_LongFormat, signed);
        if (displayDiagnostics)
            console.stdOutLn("GetDec: " + cfg.getInstance().current.dec.stringLX200);
        writeStringPauseUntilXmtFinished(cfg.getInstance().current.dec.stringLX200);
    }
    
    private void getAlt() {
        boolean signed = true;
        
        cfg.getInstance().current.alt.getStringLX200(cfg.getInstance().LX200_LongFormat, signed);
        if (displayDiagnostics)
            console.stdOutLn("GetAlt: " + cfg.getInstance().current.alt.stringLX200);
        writeStringPauseUntilXmtFinished(cfg.getInstance().current.alt.stringLX200);
    }
    
    /**
     * hh:mm:ss#
     */
    private void getLocalT() {
        String s;
        
        // astroTime.getInstance().calcSidT() (which updates local time) already called in readLX200Input()
        
        s = eString.intToString(astroTime.getInstance().h, 2)
        + ":"
                + eString.intToString(astroTime.getInstance().m, 2)
                + ":"
                + eString.intToString(astroTime.getInstance().s, 2)
                + "#";
        
        if (displayDiagnostics)
            console.stdOutLn("GetLocalT: " + s);
        writeStringPauseUntilXmtFinished(s);
    }
    
    /**
     * mm/dd/yy#
     */
    private void getDate() {
        String s;
        
        // astroTime.getInstance().calcSidT() (which updates local time) already called in readLX200Input()
        
        s = eString.intToString(astroTime.getInstance().m, 2)
        + "/"
                + eString.intToString(astroTime.getInstance().d, 2)
                + "/"
                + eString.intToString(astroTime.getInstance().y%100, 2)
                + "#";
        
        if (displayDiagnostics)
            console.stdOutLn("GetDate: " + s);
        writeStringPauseUntilXmtFinished(s);
    }
    
    /**
     * gets the timezone and returns sHH#
     */
    private void getTz() {
        String s = "";
        
        // astroTime.getInstance().calcSidT() (which updates local time) already called in readLX200Input()
        
        s += eString.intToString(astroTime.getInstance().tz, 2) + "#";
        writeStringPauseUntilXmtFinished(s);
    }
    
    private void getLong() {
        boolean signed = false;
        
        dms l = new dms();
        l.rad = cfg.getInstance().longitudeDeg * units.DEG_TO_RAD;
        writeStringPauseUntilXmtFinished(l.getStringLX200(cfg.getInstance().LX200_LongFormat, signed));
    }
    
    /**
     * gets the current minimum quality for the FIND operation, returns  SU#,  EX#,  VG#,  GD#,  FR#,
     * PR#  or VP# - superior, excellent, very good, good, fair, poor, very poor?
     */
    private void getMinQualityFind() {
        String s = "";
        
        switch(minQualityFind) {
            case 0:
                s = "SU#";
                break;
            case 1:
                s = "EX#";
                break;
            case 2:
                s = "VG#";
                break;
            case 3:
                s = "GD#";
                break;
            case 4:
                s = "FR#";
                break;
            case 5:
                s = "PR#";
                break;
            case 6:
                s = "VP#";
                break;
        }
        writeStringPauseUntilXmtFinished(s);
    }
    
    private void getSidT() {
        cfg.getInstance().current.sidT.getStringLX200(cfg.getInstance().LX200_LongFormat);
        if (displayDiagnostics)
            console.stdOutLn("GetSidT: " + cfg.getInstance().current.sidT.stringLX200);
        writeStringPauseUntilXmtFinished(cfg.getInstance().current.sidT.stringLX200);
    }
    
    private void getLat() {
        boolean signed = true;
        
        dms l = new dms();
        l.rad = cfg.getInstance().latitudeDeg * units.DEG_TO_RAD;
        writeStringPauseUntilXmtFinished(l.getStringLX200(cfg.getInstance().LX200_LongFormat, signed));
    }
    
    private void getAz() {
        boolean signed = false;
        
        cfg.getInstance().current.az.getStringLX200(cfg.getInstance().LX200_LongFormat, signed);
        if (displayDiagnostics)
            console.stdOutLn("GetAz: " + cfg.getInstance().current.az.stringLX200);
        writeStringPauseUntilXmtFinished(cfg.getInstance().current.az.stringLX200);
    }
    
    /**
     * 0 == altaz, 1 == polar, 2 == polar+gemFlip
     */
    private void getASCOMmountType() {
        if (cfg.getInstance().Mount.mountType() == MOUNT_TYPE.mountTypeAltazimuth
                || cfg.getInstance().Mount.mountType() == MOUNT_TYPE.mountTypeAltAlt)
            writeStringPauseUntilXmtFinished("0");
        else
            if (cfg.getInstance().Mount.meridianFlipPossible())
                writeStringPauseUntilXmtFinished("2");
            else
                writeStringPauseUntilXmtFinished("1");
    }
    
    /**
     * 0 == east facing west, 1 == west facing east
     */
    private void getPierSide() {
        if (cfg.getInstance().Mount.meridianFlip().flipped)
            writeStringPauseUntilXmtFinished("1");
        else
            writeStringPauseUntilXmtFinished("0");
    }
    
    /**
     * 012345678901
     *   _MM/DD/yy#
     */
    private void setCurrentDate() {
        String s;
        int ix;
        int m;
        int d;
        int y;
        
        ix = 0;
        if (common.isNumericChar(cmd[2]))
            ix = 2;
        else
            ix = 3;
        
        m = 0;
        while (common.isNumericChar(cmd[ix])) {
            m = m*10 + (int) ((char) cmd[ix]-'0');
            ix++;
        }
        
        d = 0;
        ix++;
        while (common.isNumericChar(cmd[ix])) {
            d = d*10 + (int) ((char) cmd[ix]-'0');
            ix++;
        }
        
        y = 0;
        ix++;
        while (common.isNumericChar(cmd[ix])) {
            y = y*10 + (int) ((char) cmd[ix]-'0');
            ix++;
        }
        
        s = eString.intToString(m, 2)
        + "-"
                + eString.intToString(d, 2)
                + "-"
                + eString.intToString(y, 2);
        
        common.writeFileExecFile(eString.SETDATE_BAT_FILENAME, "date " + s, true);
        
        // repopulate the vars
        astroTime.getInstance().getCurrentDateTime();
    }
    
    /**
     * 0123456789012
     *   _sDD*MM:SS#   or
     *   _sDD*MM#
     * test to see if leading blank is skipped; xephrem drops it;
     * ':' and DegSym (223) may substitude for '*'; values may be 1 or 2 chars long;
     * ASCOM generic lx200 driver uses a space instead of a colon to separate MM and SS;
     */
    private void setDec() {
        int ix;
        char sign;
        int deg;
        int min;
        int sec;
        
        ix = 0;
        if ((char) cmd[ix+2] == '-' || (char) cmd[ix+2] == '+')
            ix = 2;
        else
            ix = 3;
        
        sign = (char) cmd[ix];
        
        deg = 0;
        ix++;
        while (common.isNumericChar(cmd[ix])) {
            deg = deg*10 + (int) ((char) cmd[ix]-'0');
            ix++;
        }
        
        min = 0;
        ix++;
        while (common.isNumericChar(cmd[ix])) {
            min = min*10 + (int) ((char) cmd[ix]-'0');
            ix++;
        }
        
        sec = 0;
        if (cmd[ix] == '.' || cmd[ix] == ' ') {
            ix++;
            while (common.isNumericChar(cmd[ix])) {
                sec = sec*10 + (int) ((char) cmd[ix]-'0');
                ix++;
            }
        }
        
        t.in.dec.sign = sign;
        t.in.dec.deg = deg;
        t.in.dec.min = min;
        t.in.dec.sec = sec;
        t.in.dec.calcRad();
        
        if (displayDiagnostics)
            console.stdOutLn("SetDec: " + t.in.dec.getStringDMS());
    }
    
    /**
     * 012345678901
     *   _HH:MM:SS#
     */
    private void setLocalT() {
        String s;
        int ix;
        int hr;
        int min;
        int sec;
        
        ix = 0;
        if (common.isNumericChar(cmd[2]))
            ix = 2;
        else
            ix = 3;
        
        hr = 0;
        while (common.isNumericChar(cmd[ix])) {
            hr = hr*10 + (int) ((char) cmd[ix]-'0');
            ix++;
        }
        
        min = 0;
        ix++;
        while (common.isNumericChar(cmd[ix])) {
            min = min*10 + (int) ((char) cmd[ix]-'0');
            ix++;
        }
        
        sec = 0;
        ix++;
        while (common.isNumericChar(cmd[ix])) {
            sec = sec*10 + (int) ((char) cmd[ix]-'0');
            ix++;
        }
        
        s = eString.intToString(hr, 2)
        + ":"
                + eString.intToString(min, 2)
                + ":"
                + eString.intToString(sec, 2);
        
        common.writeFileExecFile(eString.SETTIME_BAT_FILENAME, "time " + s, true);
        
        // repopulate the vars
        astroTime.getInstance().getCurrentDateTime();
    }
    
    /**
     * 012345678901
     *   _HH:MM:SS#   or
     *   _HH:MM.t#    or
     *   _HH:MM:SS.mmm#
     * values may be 1 or 2 chars long with milliseconds to 3 chars,
     * so look for ':' and '.' instead of looking at specific array locations;
     * ASCOM generic lx200 driver uses a space instead of a colon to separate MM and SS;
     */
    private void setRa() {
        int ix;
        int hr;
        int min;
        int sec;
        int milliSec;
        int digits;
        
        ix = 0;
        if (common.isNumericChar(cmd[2]))
            ix = 2;
        else
            ix = 3;
        
        hr = 0;
        while (common.isNumericChar(cmd[ix])) {
            hr = hr*10 + (int) ((char) cmd[ix]-'0');
            ix++;
        }
        
        min = 0;
        ix++;
        while (common.isNumericChar(cmd[ix])) {
            min = min*10 + (int) ((char) cmd[ix]-'0');
            ix++;
        }
        
        sec = 0;
        milliSec = 0;
        digits = 0;
        // place ix++ after the character check
        if (cmd[ix] == '.' || cmd[ix] == ' ') {
            ix++;
            if (cmd[ix] >= '0' && cmd[ix] <= '9')
                sec = 6*(int) ((char) cmd[ix]-'0');
        } else if (cmd[ix] == ':') {
            ix++;
            while (common.isNumericChar(cmd[ix])) {
                sec = sec*10 + (int) ((char) cmd[ix]-'0');
                ix++;
            }
            if (cmd[ix] == '.') {
                ix++;
                while (common.isNumericChar(cmd[ix])) {
                    milliSec = milliSec*10 + (int) ((char) cmd[ix]-'0');
                    ix++;
                    digits++;
                }
                if (digits == 1)
                    milliSec *= 100;
                else if (digits == 2)
                    milliSec *= 10;
            }
        }
        
        t.in.ra.hr = hr;
        t.in.ra.min = min;
        t.in.ra.sec = sec;
        t.in.ra.milliSec = milliSec;
        t.in.ra.calcRad();
        
        if (displayDiagnostics)
            console.stdOutLn("SetRa: " + t.in.ra.getStringHMSM());
    }
    
    /**
     * 012345678901
     *   _HH:MM:SS#
     */
    private void setSidT() {
        String s;
        int ix;
        int hr;
        int min;
        int sec;
        double newSidT;
        double SidtDiff;
        double tDiff;
        double currentSolarTimeRad;
        double newSolarTimeRad;
        int nHr;
        int nMin;
        int nSec;
        
        ix = 0;
        if (common.isNumericChar(cmd[2]))
            ix = 2;
        else
            ix = 3;
        
        hr = 0;
        while (common.isNumericChar(cmd[ix])) {
            hr = hr*10 + (int) ((char) cmd[ix]-'0');
            ix++;
        }
        
        min = 0;
        ix++;
        while (common.isNumericChar(cmd[ix])) {
            min = min*10 + (int) ((char) cmd[ix]-'0');
            ix++;
        }
        
        sec = 0;
        ix++;
        while (common.isNumericChar(cmd[ix])) {
            sec = sec*10 + (int) ((char) cmd[ix]-'0');
            ix++;
        }
        
        newSidT = (double) hr*units.HR_TO_RAD + (double) min*units.MIN_TO_RAD + (double) sec*units.SEC_TO_RAD;
        // get sidereal time difference
        SidtDiff = newSidT - cfg.getInstance().current.sidT.rad;
        // convert time difference to solar time
        tDiff = SidtDiff / units.SID_RATE;
        // get current time in radians
        currentSolarTimeRad = (double) astroTime.getInstance().h*units.HR_TO_RAD
                + (double) astroTime.getInstance().m*units.MIN_TO_RAD
                + (double) astroTime.getInstance().s*units.SEC_TO_RAD
                + (double) astroTime.getInstance().ms*units.MILLI_SEC_TO_RAD;
        // calculate new solar time
        newSolarTimeRad = currentSolarTimeRad + tDiff;
        newSolarTimeRad = eMath.validRad(newSolarTimeRad);
        nHr = (int) (newSolarTimeRad*units.RAD_TO_HR);
        nMin = (int) ((newSolarTimeRad - (double) nHr*units.HR_TO_RAD) * units.RAD_TO_MIN);
        nSec = (int) ((newSolarTimeRad - (double) nHr*units.HR_TO_RAD - (double) nMin*units.MIN_TO_RAD) * units.RAD_TO_SEC);
        // set new time
        s = eString.intToString(nHr, 2)
        + ":"
                + eString.intToString(nMin, 2)
                + ":"
                + eString.intToString(nSec, 2);
        
        common.writeFileExecFile(eString.SETTIME_BAT_FILENAME, "time " + s, true);
        
        // repopulate the vars
        astroTime.getInstance().getCurrentDateTime();
    }
    
    /**
     * 01234567
     *    gggg#
     */
    private void setGuideArcsecSec() {
        int id;
        int rtnVal;
        
        rtnVal = return4digitInt();
        for (id = 0; id < SERVO_ID.size();id++)
            cfg.getInstance().servoParm[id].guideArcsecSec = rtnVal;
    }
    
    /*
     * XS(F|f) Set focuser (fast|slow) speed
     */
    private void setFocusFastSpeedDegSec() {
        int rtnVal;
        
        rtnVal = return4digitInt();
        cfg.getInstance().spf.fastSpeedDegSec = (double) rtnVal;
        cfg.getInstance().spf.fastSpeedRadSec = cfg.getInstance().spf.fastSpeedDegSec * units.DEG_TO_RAD;
    }
    
    private void setFocusSlowSpeedArcsecSec() {
        int rtnVal;
        
        rtnVal = return4digitInt();
        cfg.getInstance().spf.slowSpeedArcsecSec = (double) rtnVal;
        cfg.getInstance().spf.slowSpeedRadSec = cfg.getInstance().spf.slowSpeedArcsecSec * units.ARCSEC_TO_RAD;
    }
    
    /**
     * set max slew rate in deg/sec: single digit value of 2, 3, or 4
     */
    private void setMaxSlew() {
        int slewRate;
        int id;
        
        if (common.isNumericChar(cmd[3])) {
            slewRate = (int) ((char) cmd[3]-'0');
            for (id = 0; id < SERVO_ID.size(); id++) {
                cfg.getInstance().servoParm[id].fastSpeedDegSec = (double) slewRate;
                cfg.getInstance().servoParm[id].fastSpeedRadSec = cfg.getInstance().servoParm[id].fastSpeedDegSec * units.DEG_TO_RAD;
            }
        }
    }
    
    private int return4digitInt() {
        int rtn = 0;
        int ix = 3;
        
        while (common.isNumericChar(cmd[ix])) {
            rtn = rtn*10 + (int) ((char) cmd[ix]-'0');
            ix++;
        }
        return rtn;
    }
}

