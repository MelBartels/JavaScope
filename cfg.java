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
 * singleton design pattern of the configuration vars and methods that work on them
 */
public class cfg {
    String filename;
    BufferedReader input;
    PrintStream output;
    int id;
    int ix;
    String s;
    int lineCount;

    String siteName;
    double latitudeDeg;
    double longitudeDeg;

    Mount Mount;
    private MOUNT_TYPE mountType;
    // mounting type parms found in Mount;
    // if Mount parms read in before Mount built, then they will be lost

    // a list of eyepiece names and focus positions
    listEyepieceFocus lef;
    static final String EYEPIECE_FOCUS_DEMO_NAME = "22mmDemo";

    // if handpad present, then set to true
    boolean handpadPresent;
    HANDPAD_MODE handpadMode;
    HANDPAD_DESIGN handpadDesign;
    // flip handpad's up/down buttons
    boolean handpadFlipUpDown;
    // flip handpad's up/down buttons if meridian flipped since declination direction inverted when meridian flipped
    boolean handpadFollowMeridianFlip;
    // ramp down delay after releasing a handpad direction button: prevents the scope from back tracking
    double SiTechRampDownDelaySec;
    // spiral search pattern radius or distance between sweeps, and, the speed to traverse the pattern
    double spiralSearchRadiusDeg;
    double spiralSearchSpeedDegSec;

    // program startup state of initializations
    INIT_STATE initState;
    double z1Deg;
    double z2Deg;
    double z3Deg;
    double dataFileCoordYear;

    boolean precessionNutationAberration;
    // mount alignment to calculate refraction, ie, altaz means to calculate refraction from cfg.getInstance().current.alt.rad,
    // equat means to calculate based on site coordinates (adjusts both alt and az), none means no correction to be taken
    ALIGNMENT refractAlign;
    boolean backlashActive;
    boolean useAltAzEC;
    boolean useAltAltEC;
    boolean useAzAzEC;
    // pointing model compensation in use
    boolean usePMC;

    // drift to drag a guide star so that an autoguider can update in one direction only;
    // added/subtracted to/from DriftRa/DecHr.rad in add/removeGuideDragFromDrift()
    double guideDragRaArcsecPerMin;
    double guideDragDecArcsecPerMin;
    // if true, drift updated at end of handpaddle guiding session
    boolean handpadUpdateDrift;

    // starting drift values
    double driftRaDegPerHr;
    double driftDecDegPerHr;

    ENCODER_TYPE encoderType;
    // what method of IO method to use
    IO_TYPE encoderIOType;
    // comm port to use
    String encoderSerialPortName;
    int encoderBaudRate;
    // local port to transmit on via UDP
    int encoderHomeIPPort;
    // remote TCP or UDP IP address name and port
    String encoderRemoteIPName;
    int encoderRemoteIPPort;
    String encoderFileLocation;
    boolean encoderTrace;
    int encoderAltDecCountsPerRev;
    int encoderAzRaCountsPerRev;
    ROTATION encoderAltDecDir;
    ROTATION encoderAzRaDir;
    ROTATION encoderFieldRDir;
    ROTATION encoderFocusDir;
    double encoderErrorThresholdDeg;
    boolean resetScopeToEncodersTrackOffResetTarget;
    boolean resetScopeToEncodersTrackingResetTarget;
    boolean resetScopeToEncodersSlewingResetTarget;
    double encoderAltDecOffset;
    double encoderAzRaOffset;

    // directory of Project Pluto's guide planetarium program and thus location of slewFile and slewOutFile
    String ProjectPlutoGuidePath;
    // name of Project Pluto's guide executable program
    String ProjectPlutoGuideExec;
    // if true, actively read/write the external slew files
    boolean readWriteExternalSlewFiles;
    // html pages (ie, coordinate display and status displays) update frequency in seconds; if zero, then no update
    double updateHTMLFreqSec;
    // what type of IO method to use for external program control
    IO_TYPE extIOType;
    // comm port to use
    String extSerialPortName;
    int extBaudRate;
    // local port to transmit to external controlling program via UDP
    int extHomeIPPort;
    // external controlling program TCP or UDP IP address name and port
    String extRemoteIPName;
    int extRemoteIPPort;
    String extFileLocation;
    boolean extTrace;
    int extPortWaitTimeMilliSecs;

    boolean LX200Control;
    // what method of IO method to use
    IO_TYPE LX200IOType;
    // comm port to use
    String LX200SerialPortName;
    int LX200BaudRate;
    // local port to transmit to external controlling program via UDP
    int LX200homeIPPort;
    // external controlling program TCP or UDP IP address name and port
    String LX200remoteIPName;
    int LX200RemoteIPPort;
    String LX200FileLocation;
    boolean LX200Trace;
    double LX200MotionTimeoutSec;
    // toggles between standard and long format for coordinates
    boolean LX200_LongFormat;
    boolean LX200_ContinueTrack;

    CONTROLLER_MANUFACTURER controllerManufacturer;
    IO_TYPE servoIOType;
    String servoSerialPortName;
    int servoBaudRate;
    int servoHomeIPPort;
    String servoRemoteIPName;
    int servoRemoteIPPort;
    String servoFileLocation;
    boolean servoTrace;
    int servoPortWaitTimeMilliSecs;
    // look ahead time to calculate tracking moves - the granularity of the tracking function, eg, error correcting values
    // that come and go faster than this time value will not be responded to
    double moveToTargetTimeSec;
    // thread sleep time for application's heartbeat; applied in the sequencer; use to reduce cpu usage
    int sequencerSleepTimeMilliSec;
    
    String UILookAndFeel;
    boolean usePanelColors;
    colors lightPanel;
    colors mediumPanel;
    colors darkPanel;
    colors stopToggle;
    colors goToggle;
    colors toggle;
    colors radioButton;
    colors comboBox;

    int updateUImilliSec;
    String datFileLocation;
    String autoGenInitFile;
    String cmdFileLocation;
    String SiTechFirmwareFileLocation;

    SERVO_PARMS[] servoParm;
    SERVO_PARMS spa;
    SERVO_PARMS spz;
    SERVO_PARMS spr;
    SERVO_PARMS spf;

    position current;
    position one;
    position bkupOne;
    position two;
    position bkupTwo;
    position three;
    position bkupThree;

    private static cfg INSTANCE;

    private cfg() {}

    /**
     * double checked locking pattern for multi-threading protection:
     * synchronized block attempts to gain exclusive lock on cfg class; block code not executed until lock obtained;
     * if thread pre-empted after first INSTANCE==null comparision, then INSTANCE==null check within synchronized block
     * will tell the thread that object created behind its back;
     * not synchronized public static cfg getInstance() as this thread will block until getInstance() completes, slowing execution;
     */
    public static cfg getInstance() {
        if (INSTANCE == null)
            synchronized(cfg.class) {
                if (INSTANCE == null) {
                    INSTANCE = new cfg();
                    INSTANCE.initialize();
                }
            }
        return INSTANCE;
    }

    // see above: must call immediately after INSTANCE = new cfg(); be sure to use INSTANCE.initialize(); as you want to
    // run INSTANCE's initialize(), not this.initialize()
    private void initialize() {
        ioSerial.listSerialPorts();

        lef = new listEyepieceFocus();

        lightPanel = new colors();
        mediumPanel = new colors();
        darkPanel = new colors();
        stopToggle = new colors();
        goToggle = new colors();
        toggle = new colors();
        radioButton = new colors();
        comboBox = new colors();

        current = new position("current");
        one = new position("one");
        bkupOne = new position("bkupOne");
        two = new position("two");
        bkupTwo = new position("bkupTwo");
        three = new position("three");
        bkupThree = new position("bkupthree");

        servoParm = new SERVO_PARMS[SERVO_ID.size()];

        for (id = 0; id < SERVO_ID.size(); id++) {
            servoParm[id] = new SERVO_PARMS();
            servoParm[id].taa = new trackAnalysisArray(id);
            servoParm[id].lagr = new logAccumGuideResults(id);
        }

        spa = servoParm[SERVO_ID.altDec.KEY];
        spz = servoParm[SERVO_ID.azRa.KEY];
        spr = servoParm[SERVO_ID.fieldR.KEY];
        spf = servoParm[SERVO_ID.focus.KEY];

        filename = eString.PGM_NAME + eString.CFG_EXT;
    }

    void defaults() {
        // for altDec and azRa motors, change default from false to true
        spa.controllerActive = true;
        spz.controllerActive = true;

        siteName = "default";
        latitudeDeg = 44.;
        longitudeDeg = 123.;

        mountType = MOUNT_TYPE.mountTypeNone;
        // build Mount including default parms
        Mount = new mountFactory().build(mountType);

        // ef = new eyepieceFocus("22mmNagler", 1000);
        // lef.add(ef);

        handpadPresent = true;
        handpadMode = HANDPAD_MODE.handpadModeOff;
        handpadDesign = HANDPAD_DESIGN.handpadDesignStandard;
        handpadFlipUpDown = false;
        handpadFollowMeridianFlip = false;
        SiTechRampDownDelaySec = 3.;
        spiralSearchRadiusDeg = .5;
        spiralSearchSpeedDegSec = .1;

        initState = INIT_STATE.noAlign;
        z1Deg = 0.;
        z2Deg = 0.;
        z3Deg = 0.;
        dataFileCoordYear = 2005.;

        precessionNutationAberration = false;
        refractAlign = ALIGNMENT.none;
        backlashActive = true;
        useAltAzEC = false;
        useAltAltEC = false;
        useAzAzEC = false;
        usePMC = false;

        // only active when handpadMode is handpadModeGuideStayDrag
        guideDragRaArcsecPerMin = 10.;
        guideDragDecArcsecPerMin = 10.;

        handpadUpdateDrift = true;

        driftRaDegPerHr = 0.;
        driftDecDegPerHr = 0.;

        encoderType = ENCODER_TYPE.encoderNone;
        encoderIOType = IO_TYPE.ioSerial;
        encoderSerialPortName = "COM2";
        encoderBaudRate = 9600;
        encoderHomeIPPort = 1961;
        encoderRemoteIPName = "bbad";
        encoderRemoteIPPort = 1962;
        encoderFileLocation = ".\\";
        encoderTrace = false;
        encoderAltDecCountsPerRev = 8096;
        encoderAzRaCountsPerRev = 8096;
        encoderAltDecDir = ROTATION.CW;
        encoderAzRaDir = ROTATION.CW;
        encoderFieldRDir = ROTATION.CW;
        encoderFocusDir = ROTATION.CW;
        encoderErrorThresholdDeg = 1.;
        resetScopeToEncodersTrackOffResetTarget = true;
        resetScopeToEncodersTrackingResetTarget = false;
        resetScopeToEncodersSlewingResetTarget = false;
        encoderAltDecOffset = 0.;
        encoderAzRaOffset = 0.;

        ProjectPlutoGuidePath = "c:\\GUIDE8\\";
        ProjectPlutoGuideExec = "guide8.exe";
        readWriteExternalSlewFiles = true;
        updateHTMLFreqSec = 0.;
        extIOType = IO_TYPE.ioNone;
        extSerialPortName = "COM2";
        extBaudRate = 9600;
        extHomeIPPort = 1956;
        extRemoteIPName = "bbad";
        extRemoteIPPort = 1957;
        extFileLocation = ".\\";
        extTrace = false;
        extPortWaitTimeMilliSecs = 250;

        LX200Control = false;
        LX200IOType = IO_TYPE.ioSerial;
        LX200SerialPortName = "COM3";
        LX200BaudRate = 9600;
        LX200homeIPPort = 1958;
        LX200remoteIPName = "bbad";
        LX200RemoteIPPort = 1959;
        LX200FileLocation = ".\\";
        LX200Trace = false;
        LX200MotionTimeoutSec = 5.;
        LX200_LongFormat = false;
        LX200_ContinueTrack = false;

        controllerManufacturer = CONTROLLER_MANUFACTURER.SiTech;
        //controllerManufacturer = CONTROLLER_MANUFACTURER.JRKerr;
        servoIOType = IO_TYPE.ioSerial;
        servoSerialPortName = "COM1";
        servoBaudRate = 19200;
        servoHomeIPPort = 1954;
        servoRemoteIPName = "bbad";
        servoRemoteIPPort = 1955;
        servoFileLocation = ".\\";
        servoTrace = false;
        servoPortWaitTimeMilliSecs = 250;
        moveToTargetTimeSec = .33;
        sequencerSleepTimeMilliSec = 100;

        UILookAndFeel = "systemLookAndFeel";
        usePanelColors = false;
        lightPanel.r = 192;
        lightPanel.g = 202;
        lightPanel.b = 219;
        mediumPanel.r = 158;
        mediumPanel.g = 174;
        mediumPanel.b = 192;
        darkPanel.r = 128;
        darkPanel.g = 140;
        darkPanel.b = 156;
        stopToggle.r = 255;
        stopToggle.g = 0;
        stopToggle.b = 0;
        goToggle.r = 0;
        goToggle.g = 255;
        goToggle.b = 0;
        toggle.r = 220;
        toggle.g = 235;
        toggle.b = 235;
        radioButton.r = 177;
        radioButton.g = 219;
        radioButton.b = 224;
        comboBox.r = 168;
        comboBox.g = 199;
        comboBox.b = 215;
        updateUImilliSec = 1000;
        datFileLocation = "c:\\mel\\cot\\DAT\\";
        autoGenInitFile = "26bstars.DAT";
        cmdFileLocation = "c:\\mel\\cot\\servo\\java\\";
        SiTechFirmwareFileLocation = "c:\\mel\\cot\\servo\\";
    }

    boolean initialized() {
        if (one.init && two.init)
            return true;
        else
            return false;
    }

    void killInits() {
        cfg.getInstance().one.init = cfg.getInstance().two.init = cfg.getInstance().three.init = false;
    }

    void setInitStateFromCmdLine() {
        do {
            // display should match INIT_STATE order
            console.stdOutLn("\nCoordinate initialization using latitudeDeg, longitudeDeg from " + filename);
            console.stdOutLn("   would you like to:");
            console.stdOutLn("    1. adopt an equatorial alignment");
            console.stdOutLn("    2. adopt an altazimuth alignment (set 0 azimuth towards pole)");
            console.stdOutLn("    3. adopt an altalt alignment (primary axis aimed towards pole)");
            // if cfg.getInstance().one and cfg.getInstance().two read
            if (initialized())
                console.stdOutLn("    4. use alignment data from " + filename);
            console.stdOutLn("    5. adopt no alignment and start from scratch");
            console.stdOut("   what is your choice? ");
            console.getInt();
        }
        while (console.i < 1 || console.i > 5);

        initState = INIT_STATE.matchKey(console.i-1);
    }

    void askAndWrite() {
        console.stdOut("save "
        + filename
        + " ('y' followed by enter key for yes)? ");
        console.getString();
        if (console.s.equalsIgnoreCase("y"))
            write();
    }

    boolean read() {
        StringTokenizer st;
        String name;

        errorString.es = "";
        try {
            input = new BufferedReader(new FileReader(filename));
            console.stdOutLn("reading configuration from "
            + filename
            + "...");
            lineCount = 0;
            s = input.readLine();
            while (s != null) {
                lineCount++;
                st = new StringTokenizer(s);
                if (st.countTokens() >= 2) {
                    name = st.nextToken();

                    // ignore if comment
                    if (name.charAt(0) == ';'
                    || name.charAt(0) == '['
                    || name.charAt(0) == '\''
                    || name.charAt(0) == '/')
                        ;
                    else
                        if (!processCfgParm(name, st))
                            if (!processServoParm(name, st))
                                console.errOut("could not interpret line "
                                + lineCount
                                + " "
                                + s);
                }
                s = input.readLine();
            }
            input.close();

            processPostRead();

            if (errorString.es.length() > 0)
                console.errOut("configuration file errors:\n" + errorString.es);
            console.stdOutLn("finished reading configuration file " + filename);
            return true;
        }
        catch (IOException ioe) {
            console.errOut("could not open "
            + filename
            + "; using internal default values");
            return false;
        }
    }

    void processPostRead() {
        // set current altaz to servo motor currentPositionDeg: necessary for convertMatrix initialization as it comes before servo init
        current.alt.rad = spa.currentPositionDeg*units.DEG_TO_RAD;
        current.az.rad = spz.currentPositionDeg*units.DEG_TO_RAD;
        // handle Mount init
        if (Mount.meridianFlipPossible())
            Mount.meridianFlip().autoFuzzRad = Mount.meridianFlip().autoFuzzDeg*units.DEG_TO_RAD;
    }

    boolean processCfgParm(String name, StringTokenizer st) {
        String q;
        String r;
        SERVO_ID servoID;
        guideParms gp = new guideParms();
        eyepieceFocus ef = new eyepieceFocus();
        boolean error;

        if (name.equalsIgnoreCase(CFG_PARM_NAME.siteName.toString())) {
            siteName = new String(st.nextToken());
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.latitudeDeg.toString()))
            try {
                latitudeDeg = Double.parseDouble(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.longitudeDeg.toString()))
            try {
                longitudeDeg = Double.parseDouble(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.mountType.toString())
        || (eString.MOUNT_TYPE_PREFIX + name).equalsIgnoreCase(CFG_PARM_NAME.mountType.toString())) {
            q = st.nextToken();
            if (!q.startsWith(eString.MOUNT_TYPE_PREFIX))
                q = eString.MOUNT_TYPE_PREFIX + q;
            mountType = MOUNT_TYPE.matchStr(q);
            if (mountType == null) {
                errorString.es += "bad "
                + name
                + " of "
                + q
                + " in "
                + filename
                + " line "
                + lineCount
                + "; must be one of the following:"
                + MOUNT_TYPE.returnItemListAsString();
            }
            else {
                Mount = new mountFactory().build(mountType);
                if (Mount == null)
                    return false;
                return true;
            }
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.canMoveToPole.toString())) {
            Mount.canMoveToPole(new Boolean(st.nextToken()).booleanValue());
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.canMoveThruPole.toString())) {
            Mount.canMoveThruPole(new Boolean(st.nextToken()).booleanValue());
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.primaryAxisFullyRotates.toString())) {
            Mount.primaryAxisFullyRotates(new Boolean(st.nextToken()).booleanValue());
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.meridianFlipPossible.toString())) {
            Mount.meridianFlipPossible(new Boolean(st.nextToken()).booleanValue());
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.meridianFlipRequired.toString())) {
            if (Mount.meridianFlipPossible())
                Mount.meridianFlip().required = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.autoMeridianFlip.toString())) {
            if (Mount.meridianFlipPossible())
                Mount.meridianFlip().auto = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.autoMeridianFlipFuzzDeg.toString()))
            try {
            if (Mount.meridianFlipPossible())
                Mount.meridianFlip().autoFuzzDeg = Double.parseDouble(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.eyepieceFocus.toString())) {
            if (st.countTokens() == 2) {
                ef.name = new String(st.nextToken());
                try {
                    ef.position = Long.parseLong(st.nextToken());
                    lef.add(ef);
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
            }
            else
                errorString.es += "line "
                + lineCount
                + ": bad number of tokens in "
                + filename;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.handpadPresent.toString())) {
            handpadPresent = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.handpadMode.toString())
        || (eString.HANDPAD_MODE_PREFIX + name).equalsIgnoreCase(CFG_PARM_NAME.handpadMode.toString())) {
            q = st.nextToken();
            if (!q.startsWith(eString.HANDPAD_MODE_PREFIX))
                q = eString.HANDPAD_MODE_PREFIX + q;
            handpadMode = HANDPAD_MODE.matchStr(q);
            if (handpadMode == null) {
                errorString.es += "bad "
                + name
                + " of "
                + q
                + " in "
                + filename
                + " line "
                + lineCount
                + "; must be one of the following:"
                + HANDPAD_MODE.returnItemListAsString();
            }
            else
                return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.handpadDesign.toString())
        || (eString.HANDPAD_DESIGN_PREFIX + name).equalsIgnoreCase(CFG_PARM_NAME.handpadDesign.toString())) {
            q = st.nextToken();
            if (!q.startsWith(eString.HANDPAD_DESIGN_PREFIX))
                q = eString.HANDPAD_DESIGN_PREFIX + q;
            handpadDesign = HANDPAD_DESIGN.matchStr(q);
            if (handpadDesign == null) {
                errorString.es += "bad "
                + name
                + " of "
                + q
                + " in "
                + filename
                + " line "
                + lineCount
                + "; must be one of the following:"
                + HANDPAD_DESIGN.returnItemListAsString()
                + "setting to handpadDesignStandard";
                handpadDesign = HANDPAD_DESIGN.handpadDesignStandard;
            }
            else
                return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.handpadFlipUpDown.toString())) {
            handpadFlipUpDown = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.handpadFollowMeridianFlip.toString())) {
            handpadFollowMeridianFlip = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.SiTechRampDownDelaySec.toString()))
            try {
                SiTechRampDownDelaySec = Double.parseDouble(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.spiralSearchRadiusDeg.toString()))
            try {
                spiralSearchRadiusDeg = Double.parseDouble(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.spiralSearchSpeedDegSec.toString()))
            try {
                spiralSearchSpeedDegSec = Double.parseDouble(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.initState.toString())) {
            q = st.nextToken();
            initState = INIT_STATE.matchStr(q);
            if (initState == null) {
                errorString.es += "bad "
                + name
                + " of "
                + q
                + " in "
                + filename
                + " line "
                + lineCount
                + "; must be one of the following:"
                + INIT_STATE.returnItemListAsString();
            }
            else
                return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.initOne.toString())) {
            if (st.countTokens() >= 5)
                try {
                    common.fReadPositionDeg(one, st);
                    one.objName(common.tokensToString(st));
                    if (cfg.getInstance().precessionNutationAberration)
                        one.applyPrecessionCorrection();
                    bkupOne.copy(one);
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.initTwo.toString())) {
            if (st.countTokens() >= 5)
                try {
                    common.fReadPositionDeg(two, st);
                    two.objName(common.tokensToString(st));
                    if (cfg.getInstance().precessionNutationAberration)
                        two.applyPrecessionCorrection();
                    bkupTwo.copy(two);
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.initThree.toString())) {
            if (st.countTokens() >= 5)
                try {
                    common.fReadPositionDeg(three, st);
                    three.objName(common.tokensToString(st));
                    if (cfg.getInstance().precessionNutationAberration)
                        three.applyPrecessionCorrection();
                    bkupThree.copy(three);
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.z1Deg.toString()))
            try {
                z1Deg = Double.parseDouble(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.z2Deg.toString()))
            try {
                z2Deg = Double.parseDouble(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.z3Deg.toString()))
            try {
                z3Deg = Double.parseDouble(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.dataFileCoordYear.toString()))
            try {
                dataFileCoordYear = Double.parseDouble(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.precessionNutationAberration.toString())) {
            precessionNutationAberration = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.refractAlign.toString())) {
            q = st.nextToken();
            refractAlign = ALIGNMENT.matchStr(q);
            if (refractAlign == null) {
                errorString.es += "bad "
                + name
                + " of "
                + q
                + " in "
                + filename
                + " line "
                + lineCount
                + "; must be one of the following:"
                + ALIGNMENT.returnItemListAsString();
            }
            else
                return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.backlashActive.toString())) {
            backlashActive = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.useAltAzEC.toString())) {
            useAltAzEC = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.useAltAltEC.toString())) {
            useAltAltEC = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.useAzAzEC.toString())) {
            useAzAzEC = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.usePMC.toString())) {
            usePMC = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.buildPEC.toString())) {
            error = false;
            // name takes up first token
            if (st.countTokens() == 7) {
                q = st.nextToken();
                servoID = SERVO_ID.matchStr(q);
                if (servoID == null) {
                    errorString.es += "bad "
                    + name
                    + " servoID of "
                    + q
                    + " in "
                    + filename
                    + " line "
                    + lineCount
                    + "; must be one of the following:"
                    + SERVO_ID.returnItemListAsString();
                    error = true;
                }
                else
                    gp.servoIDStr = servoID.toString();
                if (!error) {
                    gp.descriptStr = st.nextToken();

                    try {
                        gp.motorStepsPerPECArray = Double.parseDouble(st.nextToken());
                    }
                    catch (NumberFormatException nfe) {
                        errorString.es += "line "
                        + lineCount
                        + ": bad motorStepsPerPECArray number for "
                        + name
                        + " in "
                        + filename;
                        error = true;
                    }
                }
                if (!error)
                    try {
                        gp.PECSize = Integer.parseInt(st.nextToken());
                    }
                    catch (NumberFormatException nfe) {
                        errorString.es += "line "
                        + lineCount
                        + ": bad PECSize number for "
                        + name
                        + " in "
                        + filename;
                        error = true;
                    }
                if (!error)
                    try {
                        gp.guidingCycles = Integer.parseInt(st.nextToken());
                    }
                    catch (NumberFormatException nfe) {
                        errorString.es += "line "
                        + lineCount
                        + ": bad guidingCycles number for "
                        + name
                        + " in "
                        + filename;
                        error = true;
                    }
                if (!error) {
                    q = st.nextToken();
                    gp.PECRotation = ROTATION.matchStr(q);
                    if (gp.PECRotation == null) {
                        errorString.es += "bad "
                        + name
                        + " Rotation of "
                        + q
                        + " in "
                        + filename
                        + " line "
                        + lineCount
                        + "; must be one of the following:"
                        + ROTATION.returnItemListAsString();
                        error = true;
                    }
                }
                if (!error)
                    try {
                        gp.PECIxOffset = Double.parseDouble(st.nextToken());
                    }
                    catch (NumberFormatException nfe) {
                        errorString.es += "line "
                        + lineCount
                        + ": bad PECIxOffset number for "
                        + name
                        + " in "
                        + filename;
                        error = true;
                    }
                if (!error) {
                    servoParm[servoID.KEY].lg.add( new guide( gp));
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.guideDragRaArcsecPerMin.toString()))
            try {
                guideDragRaArcsecPerMin = Double.parseDouble(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.guideDragDecArcsecPerMin.toString()))
            try {
                guideDragDecArcsecPerMin = Double.parseDouble(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.handpadUpdateDrift.toString())) {
            handpadUpdateDrift = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.driftRaDegPerHr.toString()))
            try {
                driftRaDegPerHr = Double.parseDouble(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.driftDecDegPerHr.toString()))
            try {
                driftDecDegPerHr = Double.parseDouble(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.encoderType.toString())
        || (eString.ENCODER_PREFIX + name).equalsIgnoreCase(CFG_PARM_NAME.encoderType.toString())) {
            q = st.nextToken();
            if (!q.startsWith(eString.ENCODER_PREFIX))
                q = eString.ENCODER_PREFIX + q;
            encoderType = ENCODER_TYPE.matchStr(q);
            if (encoderType == null) {
                errorString.es += "bad "
                + name
                + " of "
                + q
                + " in "
                + filename
                + " line "
                + lineCount
                + "; must be one of the following:"
                + ENCODER_TYPE.returnItemListAsString();
            }
            else
                return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.encoderIOType.toString())
        || (eString.IO_PREFIX + name).equalsIgnoreCase(CFG_PARM_NAME.encoderIOType.toString())) {
            q = st.nextToken();
            if (!q.startsWith(eString.IO_PREFIX))
                q = eString.IO_PREFIX + q;
            encoderIOType = IO_TYPE.matchStr(q);
            if (encoderIOType == null) {
                errorString.es += "bad "
                + name
                + " of "
                + q
                + " in "
                + filename
                + " line "
                + lineCount
                + "; must be one of the following:"
                + IO_TYPE.returnItemListAsString();
            }
            else
                return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.encoderSerialPortName.toString())) {
            encoderSerialPortName = common.tokensToString(st);
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.encoderBaudRate.toString()))
            try {
                encoderBaudRate = Integer.parseInt(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.encoderHomeIPPort.toString()))
            try {
                encoderHomeIPPort = Integer.parseInt(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.encoderRemoteIPName.toString())) {
            encoderRemoteIPName = new String(st.nextToken());
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.encoderRemoteIPPort.toString()))
            try {
                encoderRemoteIPPort = Integer.parseInt(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.encoderFileLocation.toString())) {
            encoderFileLocation = new String(st.nextToken());
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.encoderTrace.toString())) {
            encoderTrace = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.encoderAltDecCountsPerRev.toString()))
            try {
                encoderAltDecCountsPerRev = Integer.parseInt(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.encoderAzRaCountsPerRev.toString()))
            try {
                encoderAzRaCountsPerRev = Integer.parseInt(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.encoderAltDecDir.toString())) {
            q = st.nextToken();
            encoderAltDecDir = ROTATION.matchStr(q);
            if (encoderAltDecDir == null) {
                errorString.es += "bad "
                + name
                + " Rotation of "
                + q
                + " in "
                + filename
                + " line "
                + lineCount
                + "; must be one of the following:"
                + ROTATION.returnItemListAsString();
            }
            else
                return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.encoderAzRaDir.toString())) {
            q = st.nextToken();
            encoderAzRaDir = ROTATION.matchStr(q);
            if (encoderAzRaDir == null) {
                errorString.es += "bad "
                + name
                + " Rotation of "
                + q
                + " in "
                + filename
                + " line "
                + lineCount
                + "; must be one of the following:"
                + ROTATION.returnItemListAsString();
            }
            else
                return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.encoderFieldRDir.toString())) {
            q = st.nextToken();
            encoderFieldRDir = ROTATION.matchStr(q);
            if (encoderFieldRDir == null) {
                errorString.es += "bad "
                + name
                + " Rotation of "
                + q
                + " in "
                + filename
                + " line "
                + lineCount
                + "; must be one of the following:"
                + ROTATION.returnItemListAsString();
            }
            else
                return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.encoderFocusDir.toString())) {
            q = st.nextToken();
            encoderFocusDir = ROTATION.matchStr(q);
            if (encoderFocusDir == null) {
                errorString.es += "bad "
                + name
                + " Rotation of "
                + q
                + " in "
                + filename
                + " line "
                + lineCount
                + "; must be one of the following:"
                + ROTATION.returnItemListAsString();
            }
            else
                return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.encoderErrorThresholdDeg.toString()))
            try {
                encoderErrorThresholdDeg = Double.parseDouble(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.resetScopeToEncodersTrackOffResetTarget.toString())) {
            resetScopeToEncodersTrackOffResetTarget = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.resetScopeToEncodersTrackingResetTarget.toString())) {
            resetScopeToEncodersTrackingResetTarget = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.resetScopeToEncodersSlewingResetTarget.toString())) {
            resetScopeToEncodersSlewingResetTarget = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.encoderAltDecOffset.toString()))
            try {
                encoderAltDecOffset = Double.parseDouble(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.encoderAzRaOffset.toString()))
            try {
                encoderAzRaOffset = Double.parseDouble(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.ProjectPlutoGuidePath.toString())) {
            ProjectPlutoGuidePath = new String(st.nextToken());
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.ProjectPlutoGuideExec.toString())) {
            ProjectPlutoGuideExec = new String(st.nextToken());
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.readWriteExternalSlewFiles.toString())) {
            readWriteExternalSlewFiles = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.updateHTMLFreqSec.toString()))
            try {
                updateHTMLFreqSec = Double.parseDouble(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.extIOType.toString())
        || (eString.IO_PREFIX + name).equalsIgnoreCase(CFG_PARM_NAME.extIOType.toString())) {
            q = st.nextToken();
            if (!q.startsWith(eString.IO_PREFIX))
                q = eString.IO_PREFIX + q;
            extIOType = IO_TYPE.matchStr(q);
            if (extIOType == null) {
                errorString.es += "bad "
                + name
                + " of "
                + q
                + " in "
                + filename
                + " line "
                + lineCount
                + "; must be one of the following:"
                + IO_TYPE.returnItemListAsString();
            }
            else
                return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.extSerialPortName.toString())) {
            extSerialPortName = common.tokensToString(st);
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.extBaudRate.toString()))
            try {
                extBaudRate = Integer.parseInt(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.extHomeIPPort.toString()))
            try {
                extHomeIPPort = Integer.parseInt(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.extRemoteIPName.toString())) {
            extRemoteIPName = new String(st.nextToken());
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.extRemoteIPPort.toString()))
            try {
                extRemoteIPPort = Integer.parseInt(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.extFileLocation.toString())) {
            extFileLocation = new String(st.nextToken());
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.extTrace.toString())) {
            extTrace = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.extPortWaitTimeMilliSecs.toString()))
            try {
                extPortWaitTimeMilliSecs = Integer.parseInt(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.LX200Control.toString())) {
            LX200Control = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.LX200IOType.toString())
        || (eString.IO_PREFIX + name).equalsIgnoreCase(CFG_PARM_NAME.LX200IOType.toString())) {
            q = st.nextToken();
            if (!q.startsWith(eString.IO_PREFIX))
                q = eString.IO_PREFIX + q;
            LX200IOType = IO_TYPE.matchStr(q);
            if (LX200IOType == null) {
                errorString.es += "bad "
                + name
                + " of "
                + q
                + " in "
                + filename
                + " line "
                + lineCount
                + "; must be one of the following:"
                + IO_TYPE.returnItemListAsString();
            }
            else
                return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.LX200SerialPortName.toString())) {
            LX200SerialPortName = common.tokensToString(st);
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.LX200BaudRate.toString()))
            try {
                LX200BaudRate = Integer.parseInt(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.LX200homeIPPort.toString()))
            try {
                LX200homeIPPort = Integer.parseInt(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.LX200remoteIPName.toString())) {
            LX200remoteIPName = new String(st.nextToken());
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.LX200RemoteIPPort.toString()))
            try {
                LX200RemoteIPPort = Integer.parseInt(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.LX200FileLocation.toString())) {
            LX200FileLocation = new String(st.nextToken());
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.LX200Trace.toString())) {
            LX200Trace = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.LX200MotionTimeoutSec.toString()))
            try {
                LX200MotionTimeoutSec = Double.parseDouble(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.LX200_LongFormat.toString())) {
            LX200_LongFormat = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.LX200_ContinueTrack.toString())) {
            LX200_ContinueTrack = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.controllerManufacturer.toString())) {
            q = st.nextToken();
            controllerManufacturer = CONTROLLER_MANUFACTURER.matchStr(q);
            if (controllerManufacturer == null) {
                errorString.es += "bad "
                + name
                + " of "
                + q
                + " in "
                + filename
                + " line "
                + lineCount
                + "; must be one of the following:"
                + CONTROLLER_MANUFACTURER.returnItemListAsString()
                + "setting to controllerManufacturer";
                controllerManufacturer = CONTROLLER_MANUFACTURER.SiTech;
            }
            else
                return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.servoIOType.toString())
        || (eString.IO_PREFIX + name).equalsIgnoreCase(CFG_PARM_NAME.servoIOType.toString())) {
            q = st.nextToken();
            if (!q.startsWith(eString.IO_PREFIX))
                q = eString.IO_PREFIX + q;
            servoIOType = IO_TYPE.matchStr(q);
            if (servoIOType == null) {
                errorString.es += "bad "
                + name
                + " of "
                + q
                + " in "
                + filename
                + " line "
                + lineCount
                + "; must be one of the following:"
                + IO_TYPE.returnItemListAsString()
                + "setting to ioSerial";
                servoIOType = IO_TYPE.ioSerial;
            }
            else
                return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.servoSerialPortName.toString())) {
            servoSerialPortName = common.tokensToString(st);
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.servoBaudRate.toString()))
            try {
                servoBaudRate = Integer.parseInt(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.servoHomeIPPort.toString()))
            try {
                servoHomeIPPort = Integer.parseInt(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.servoRemoteIPName.toString())) {
            servoRemoteIPName = common.tokensToString(st);
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.servoRemoteIPPort.toString()))
            try {
                servoRemoteIPPort = Integer.parseInt(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.servoFileLocation.toString())) {
            servoFileLocation = new String(st.nextToken());
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.servoTrace.toString())) {
            servoTrace = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.servoPortWaitTimeMilliSecs.toString())
        || name.equalsIgnoreCase("SerialWriteDelayMs"))
            try {
                servoPortWaitTimeMilliSecs = Integer.parseInt(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.moveToTargetTimeSec.toString()))
            try {
                moveToTargetTimeSec = Double.parseDouble(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.sequencerSleepTimeMilliSec.toString()))
            try {
                sequencerSleepTimeMilliSec = Integer.parseInt(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.UILookAndFeel.toString())) {
            UILookAndFeel = common.tokensToString(st);
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.usePanelColors.toString())) {
            usePanelColors = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.lightPanel.toString())) {
            if (st.countTokens() == colors.NUM_COLORS) {
                try {
                    lightPanel.r = Integer.parseInt(st.nextToken());
                    lightPanel.g = Integer.parseInt(st.nextToken());
                    lightPanel.b = Integer.parseInt(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
            }
            return false;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.mediumPanel.toString())) {
            if (st.countTokens() == colors.NUM_COLORS) {
                try {
                    mediumPanel.r = Integer.parseInt(st.nextToken());
                    mediumPanel.g = Integer.parseInt(st.nextToken());
                    mediumPanel.b = Integer.parseInt(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
            }
            return false;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.darkPanel.toString())) {
            if (st.countTokens() == colors.NUM_COLORS) {
                try {
                    darkPanel.r = Integer.parseInt(st.nextToken());
                    darkPanel.g = Integer.parseInt(st.nextToken());
                    darkPanel.b = Integer.parseInt(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
            }
            return false;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.stopToggle.toString())) {
            if (st.countTokens() == colors.NUM_COLORS) {
                try {
                    stopToggle.r = Integer.parseInt(st.nextToken());
                    stopToggle.g = Integer.parseInt(st.nextToken());
                    stopToggle.b = Integer.parseInt(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
            }
            return false;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.goToggle.toString())) {
            if (st.countTokens() == colors.NUM_COLORS) {
                try {
                    goToggle.r = Integer.parseInt(st.nextToken());
                    goToggle.g = Integer.parseInt(st.nextToken());
                    goToggle.b = Integer.parseInt(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
            }
            return false;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.toggle.toString())) {
            if (st.countTokens() == colors.NUM_COLORS) {
                try {
                    toggle.r = Integer.parseInt(st.nextToken());
                    toggle.g = Integer.parseInt(st.nextToken());
                    toggle.b = Integer.parseInt(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
            }
            return false;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.radioButton.toString())) {
            if (st.countTokens() == colors.NUM_COLORS) {
                try {
                    radioButton.r = Integer.parseInt(st.nextToken());
                    radioButton.g = Integer.parseInt(st.nextToken());
                    radioButton.b = Integer.parseInt(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
            }
            return false;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.comboBox.toString())) {
            if (st.countTokens() == colors.NUM_COLORS) {
                try {
                    comboBox.r = Integer.parseInt(st.nextToken());
                    comboBox.g = Integer.parseInt(st.nextToken());
                    comboBox.b = Integer.parseInt(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
            }
            return false;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.updateUImilliSec.toString())
        || name.equalsIgnoreCase("SerialWriteDelayMs"))
            try {
                updateUImilliSec = Integer.parseInt(st.nextToken());
                return true;
            }
            catch (NumberFormatException nfe) {
                errorString.es += "line "
                + lineCount
                + ": bad number for "
                + name
                + " in "
                + filename;
            }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.datFileLocation.toString())) {
            datFileLocation = common.tokensToString(st);
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.autoGenInitFile.toString())) {
            autoGenInitFile = common.tokensToString(st);
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.cmdFileLocation.toString())) {
            cmdFileLocation = common.tokensToString(st);
            return true;
        }
        else if (name.equalsIgnoreCase(CFG_PARM_NAME.SiTechFirmwareFileLocation.toString())) {
            SiTechFirmwareFileLocation = common.tokensToString(st);
            return true;
        }

        return false;
    }

    boolean processServoParm(String name, StringTokenizer st) {
        String q;
        String r;

        if (name.equalsIgnoreCase(SERVO_PARM_NAME.controllerType.toString())) {
            if (st.countTokens() == SERVO_ID.size())
                for (id = 0; id < SERVO_ID.size(); id++) {
                    q = st.nextToken();
                    servoParm[id].controllerType = CONTROLLER_TYPE.matchStr(q);
                    if (servoParm[id].controllerType == null) {
                        errorString.es += "bad "
                        + name
                        + " of "
                        + q
                        + " in "
                        + filename
                        + " line "
                        + lineCount
                        + "; must be one of the following:"
                        + CONTROLLER_TYPE.returnItemListAsString();
                    }
                    else
                        return true;
                }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.controllerActive.toString())
        || name.equalsIgnoreCase("UseMotor")) {
            if (st.countTokens() == SERVO_ID.size()) {
                for (id = 0; id < SERVO_ID.size(); id++)
                    servoParm[id].controllerActive = new Boolean(st.nextToken()).booleanValue();
                return true;
            }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.ampEnableActiveHigh.toString())) {
            if (st.countTokens() == SERVO_ID.size()) {
                for (id = 0; id < SERVO_ID.size(); id++)
                    servoParm[id].ampEnableActiveHigh = new Boolean(st.nextToken()).booleanValue();
                return true;
            }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.positionGainKp.toString())) {
            if (st.countTokens() == SERVO_ID.size())
                try {
                    for (id = 0; id < SERVO_ID.size(); id++)
                        servoParm[id].positionGainKp = Integer.parseInt(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.velGainKd.toString())) {
            if (st.countTokens() == SERVO_ID.size())
                try {
                    for (id = 0; id < SERVO_ID.size(); id++)
                        servoParm[id].velGainKd = Integer.parseInt(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.positionGainKi.toString())) {
            if (st.countTokens() == SERVO_ID.size())
                try {
                    for (id = 0; id < SERVO_ID.size(); id++)
                        servoParm[id].positionGainKi = Integer.parseInt(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.integrationLimitIL.toString())) {
            if (st.countTokens() == SERVO_ID.size())
                try {
                    for (id = 0; id < SERVO_ID.size(); id++)
                        servoParm[id].integrationLimitIL = Integer.parseInt(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.outputLimitOL.toString())) {
            if (st.countTokens() == SERVO_ID.size())
                try {
                    for (id = 0; id < SERVO_ID.size(); id++)
                        servoParm[id].outputLimitOL = Integer.parseInt(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.currentLimitCL.toString())) {
            if (st.countTokens() == SERVO_ID.size())
                try {
                    for (id = 0; id < SERVO_ID.size(); id++)
                        servoParm[id].currentLimitCL = Integer.parseInt(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.positionErrorLimitEL.toString())) {
            if (st.countTokens() == SERVO_ID.size())
                try {
                    for (id = 0; id < SERVO_ID.size(); id++)
                        servoParm[id].positionErrorLimitEL = Integer.parseInt(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.rateDivisorSR.toString())) {
            if (st.countTokens() == SERVO_ID.size())
                try {
                    for (id = 0; id < SERVO_ID.size(); id++)
                        servoParm[id].rateDivisorSR = Integer.parseInt(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.ampDeadbandComp.toString())) {
            if (st.countTokens() == SERVO_ID.size())
                try {
                    for (id = 0; id < SERVO_ID.size(); id++)
                        servoParm[id].ampDeadbandComp = Integer.parseInt(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.stepsPerRev.toString())) {
            if (st.countTokens() == SERVO_ID.size())
                try {
                    for (id = 0; id < SERVO_ID.size(); id++)
                        servoParm[id].stepsPerRev = Double.parseDouble(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.encoderCountsPerRev.toString())) {
            if (st.countTokens() == SERVO_ID.size())
                try {
                    for (id = 0; id < SERVO_ID.size(); id++)
                        servoParm[id].encoderCountsPerRev = Integer.parseInt(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.reverseMotor.toString())) {
            if (st.countTokens() == SERVO_ID.size()) {
                for (id = 0; id < SERVO_ID.size(); id++)
                    servoParm[id].reverseMotor = new Boolean(st.nextToken()).booleanValue();
                return true;
            }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.accelDegSecSec.toString())) {
            if (st.countTokens() == SERVO_ID.size())
                try {
                    for (id = 0; id < SERVO_ID.size(); id++)
                        servoParm[id].cfgAccelDegSecSec = servoParm[id].accelDegSecSec = Double.parseDouble(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.fastSpeedDegSec.toString())) {
            if (st.countTokens() == SERVO_ID.size())
                try {
                    for (id = 0; id < SERVO_ID.size(); id++)
                        servoParm[id].fastSpeedDegSec = Double.parseDouble(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.slowSpeedArcsecSec.toString())) {
            if (st.countTokens() == SERVO_ID.size())
                try {
                    for (id = 0; id < SERVO_ID.size(); id++)
                        servoParm[id].slowSpeedArcsecSec = Double.parseDouble(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.dampenFactor.toString())) {
            if (st.countTokens() == SERVO_ID.size())
                try {
                    for (id = 0; id < SERVO_ID.size(); id++)
                        servoParm[id].dampenFactor = Double.parseDouble(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.homeDeg.toString())) {
            if (st.countTokens() == SERVO_ID.size())
                try {
                    for (id = 0; id < SERVO_ID.size(); id++)
                        servoParm[id].homeDeg = Double.parseDouble(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.sectorDeg.toString())) {
            if (st.countTokens() == SERVO_ID.size())
                try {
                    for (id = 0; id < SERVO_ID.size(); id++)
                        servoParm[id].sectorDeg = Double.parseDouble(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.softLimitOn.toString())) {
            if (st.countTokens() == SERVO_ID.size())
                for (id = 0; id < SERVO_ID.size(); id++)
                    servoParm[id].softLimitOn = new Boolean(st.nextToken()).booleanValue();
            return true;
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.currentPositionDeg.toString())) {
            if (st.countTokens() == SERVO_ID.size())
                try {
                    for (id = 0; id < SERVO_ID.size(); id++)
                        servoParm[id].currentPositionDeg = Double.parseDouble(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.backlashArcmin.toString())) {
            if (st.countTokens() == SERVO_ID.size())
                try {
                    for (id = 0; id < SERVO_ID.size(); id++)
                        servoParm[id].backlashArcmin = Double.parseDouble(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.guideArcsecSec.toString())) {
            if (st.countTokens() == SERVO_ID.size())
                try {
                    for (id = 0; id < SERVO_ID.size(); id++)
                        servoParm[id].guideArcsecSec = Double.parseDouble(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.guideDragArcsecPerMin.toString())) {
            if (st.countTokens() == SERVO_ID.size())
                try {
                    for (id = 0; id < SERVO_ID.size(); id++)
                        servoParm[id].guideDragArcsecPerMin = Double.parseDouble(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.driftArcsecPerMin.toString())) {
            if (st.countTokens() == SERVO_ID.size())
                try {
                    for (id = 0; id < SERVO_ID.size(); id++)
                        servoParm[id].driftArcsecPerMin = Double.parseDouble(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    errorString.es += "line "
                    + lineCount
                    + ": bad number for "
                    + name
                    + " in "
                    + filename;
                }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.PECActive.toString())) {
            if (st.countTokens() == SERVO_ID.size()) {
                for (id = 0; id < SERVO_ID.size(); id++)
                    servoParm[id].PECActive = new Boolean(st.nextToken()).booleanValue();
                return true;
            }
        }
        else if (name.equalsIgnoreCase(SERVO_PARM_NAME.autoPECSyncDetect.toString())) {
            if (st.countTokens() == SERVO_ID.size()) {
                for (id = 0; id < SERVO_ID.size(); id++) {
                    q = st.nextToken();
                    servoParm[id].autoPECSyncDetect = AUTO_PEC_SYNC_DETECT.matchStr(q);
                    if (servoParm[id].autoPECSyncDetect == null) {
                        errorString.es += "bad "
                        + name
                        + " of "
                        + q
                        + " in "
                        + filename
                        + " line "
                        + lineCount
                        + "; must be one of the following:"
                        + AUTO_PEC_SYNC_DETECT.returnItemListAsString();
                    }
                }
                return true;
            }
        }
        return false;
    }

    void write() {
        Enumeration eSERVO_ID;
        String s1;
        String s2;
        String s3;
        boolean bool;
        double dbl;
        int ix;
        int length;
        Iterator it;
        guide g;
        eyepieceFocus ef;
        int parmPosColWidth;
        int[] parmPos = new int[SERVO_ID.size()+1];

        // first column is name of configuration variable, so give it enough room: check CFG_PARM_NAME and SERVO_PARM_NAME
        parmPos[0] = 0;
        for (ix = 0; ix < CFG_PARM_NAME.size(); ix++) {
            length = CFG_PARM_NAME.matchKey(ix).toString().length();
            if (parmPos[0] < length)
                parmPos[0] = length;
        }
        for (ix = 0; ix < SERVO_PARM_NAME.size(); ix++) {
            length = SERVO_PARM_NAME.matchKey(ix).toString().length();
            if (parmPos[0] < length)
                parmPos[0] = length;
        }
        // parmPos[0] is length of var name, so make next char a space, and next char beyond that the beginning of the next string
        parmPos[0] += 2;

        // set column width for servo parms based on max size of SERVO_ID + 1 space
        parmPosColWidth = 0;
        for (ix = 0; ix < SERVO_ID.size(); ix++) {
            length = SERVO_ID.matchKey(ix).toString().length();
            if (parmPosColWidth < length)
                parmPosColWidth = length;
        }
        parmPosColWidth += 1;

        // build array of column widths
        for (id = 1; id <= SERVO_ID.size(); id++)
            parmPos[id] = parmPos[id-1] + parmPosColWidth;

        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(filename)));

            astroTime.getInstance().getCurrentDateTime();
            output.println("\n'created on " + astroTime.getInstance().buildStringCurrentDateTime());

            output.println("\n[*** geographic location section ***]");

            s = CFG_PARM_NAME.siteName.toString();
            output.println(eString.padString(s, parmPos[0]) + siteName);

            s = CFG_PARM_NAME.latitudeDeg.toString();
            output.println(eString.padString(s, parmPos[0]) + latitudeDeg);

            s = CFG_PARM_NAME.longitudeDeg.toString();
            output.println(eString.padString(s, parmPos[0]) + longitudeDeg);

            output.println("\n[*** mount section ***]");

            // cfg.mountType is starting mounting type, while cfg.Mount.mountType is current
            // mounting type that should be saved
            s = CFG_PARM_NAME.mountType.toString();
            if (Mount.mountType() == null)
                s1 = "(unknown)";
            else {
                s1 = Mount.mountType().toString();
                // remove MOUNT_TYPE_PREFIX
                s1 = s1.substring(eString.MOUNT_TYPE_PREFIX.length(), s1.length());
            }
            output.println(eString.padString(s, parmPos[0]) + s1);

            s = CFG_PARM_NAME.canMoveToPole.toString();
            output.println(eString.padString(s, parmPos[0]) + Mount.canMoveToPole());

            s = CFG_PARM_NAME.canMoveThruPole.toString();
            output.println(eString.padString(s, parmPos[0]) + Mount.canMoveThruPole());

            s = CFG_PARM_NAME.primaryAxisFullyRotates.toString();
            output.println(eString.padString(s, parmPos[0]) + Mount.primaryAxisFullyRotates());

            s = CFG_PARM_NAME.meridianFlipPossible.toString();
            output.println(eString.padString(s, parmPos[0]) + Mount.meridianFlipPossible());

            s = CFG_PARM_NAME.meridianFlipRequired.toString();
            if (Mount.meridianFlip() == null)
                bool = false;
            else
                bool = Mount.meridianFlip().required;
            output.println(eString.padString(s, parmPos[0]) + bool);

            if (Mount.meridianFlip() == null)
                bool = false;
            else
                bool = Mount.meridianFlip().auto;
            s = CFG_PARM_NAME.autoMeridianFlip.toString();
            output.println(eString.padString(s, parmPos[0]) + bool);

            if (Mount.meridianFlip() == null)
                dbl = 0.;
            else
                dbl = Mount.meridianFlip().autoFuzzDeg;
            s = CFG_PARM_NAME.autoMeridianFlipFuzzDeg.toString();
            output.println(eString.padString(s, parmPos[0]) + dbl);

            output.println("\n[*** eyepiece section ***]");
            output.println("'format: eyepieceFocus <eyepieceName> <position>");
            it = lef.iterator();
            while (it.hasNext()) {
                s = CFG_PARM_NAME.eyepieceFocus.toString();
                ef = (eyepieceFocus) it.next();
                output.println(eString.padString(s, parmPos[0])
                + ef.name
                + "   "
                + ef.position);
            }

            output.println("\n[*** handpad section ***]");

            s = CFG_PARM_NAME.handpadPresent.toString();
            output.println(eString.padString(s, parmPos[0]) + handpadPresent);

            s = CFG_PARM_NAME.handpadMode.toString();
            if (handpadMode == null)
                s1 = "(unknown)";
            else {
                // remove HANDPAD_MODE_PREFIX
                s1 = handpadMode.toString();
                s1 = s1.substring(eString.HANDPAD_MODE_PREFIX.length(), s1.length());
            }
            output.println(eString.padString(s, parmPos[0]) + s1);

            s = CFG_PARM_NAME.handpadDesign.toString();
            if (handpadDesign == null)
                s1 = "(unknown)";
            else {
                // remove HANDPAD_DESIGN_PREFIX
                s1 = handpadDesign.toString();
                s1 = s1.substring(eString.HANDPAD_DESIGN_PREFIX.length(), s1.length());
            }
            output.println(eString.padString(s, parmPos[0]) + s1);

            s = CFG_PARM_NAME.handpadFlipUpDown.toString();
            output.println(eString.padString(s, parmPos[0]) + handpadFlipUpDown);

            s = CFG_PARM_NAME.handpadFollowMeridianFlip.toString();
            output.println(eString.padString(s, parmPos[0]) + handpadFollowMeridianFlip);

            s = CFG_PARM_NAME.SiTechRampDownDelaySec.toString();
            output.println(eString.padString(s, parmPos[0]) + SiTechRampDownDelaySec);

            s = CFG_PARM_NAME.spiralSearchRadiusDeg.toString();
            output.println(eString.padString(s, parmPos[0]) + spiralSearchRadiusDeg);

            s = CFG_PARM_NAME.spiralSearchSpeedDegSec.toString();
            output.println(eString.padString(s, parmPos[0]) + spiralSearchSpeedDegSec);

            output.println("\n[*** coordinate conversion section ***]");

            s = CFG_PARM_NAME.initState.toString();
            output.println(eString.padString(s, parmPos[0]) + initState);

            if (one.init) {
                s = CFG_PARM_NAME.initOne.toString();
                s = eString.padString(s, parmPos[0]);
                output.println(s 
                + one.buildCoordDegRaw()
                + " "
                + one.objName);
            }
            if (two.init) {
                s = CFG_PARM_NAME.initTwo.toString();
                s = eString.padString(s, parmPos[0]);
                output.println(s 
                + two.buildCoordDegRaw()
                + " "
                + two.objName);
            }
            if (three.init) {
                s = CFG_PARM_NAME.initThree.toString();
                s = eString.padString(s, parmPos[0]);
                output.println(s 
                + three.buildCoordDegRaw()
                + " "
                + three.objName);
            }
            s = CFG_PARM_NAME.z1Deg.toString();
            output.println(eString.padString(s, parmPos[0]) + z1Deg);

            s = CFG_PARM_NAME.z2Deg.toString();
            output.println(eString.padString(s, parmPos[0]) + z2Deg);

            s = CFG_PARM_NAME.z3Deg.toString();
            output.println(eString.padString(s, parmPos[0]) + z3Deg);

            s = CFG_PARM_NAME.dataFileCoordYear.toString();
            output.println(eString.padString(s, parmPos[0]) + dataFileCoordYear);

            output.println("\n[*** error correction section ***]");

            s = CFG_PARM_NAME.precessionNutationAberration.toString();
            output.println(eString.padString(s, parmPos[0]) + precessionNutationAberration);

            s = CFG_PARM_NAME.refractAlign.toString();
            output.println(eString.padString(s, parmPos[0]) + refractAlign);

            s = CFG_PARM_NAME.backlashActive.toString();
            output.println(eString.padString(s, parmPos[0]) + backlashActive);

            s = CFG_PARM_NAME.useAltAzEC.toString();
            output.println(eString.padString(s, parmPos[0]) + useAltAzEC);

            s = CFG_PARM_NAME.useAltAltEC.toString();
            output.println(eString.padString(s, parmPos[0]) + useAltAltEC);

            s = CFG_PARM_NAME.useAzAzEC.toString();
            output.println(eString.padString(s, parmPos[0]) + useAzAzEC);

            s = CFG_PARM_NAME.usePMC.toString();
            output.println(eString.padString(s, parmPos[0]) + usePMC);

            output.println("\n[*** PEC section ***]");
            output.println("'format: buildPEC servoID  description  motorStepsPerPECArray  PECSize  guidingCycles  Rotation  PECIxOffset");

            // SERVO_ID already substantiated, not necessary to create an Enumeration object
            eSERVO_ID = SERVO_ID.elements();
            while (eSERVO_ID.hasMoreElements()) {
                SERVO_ID sid = (SERVO_ID) eSERVO_ID.nextElement();
                id = sid.KEY;
                it = servoParm[id].lg.iterator();
                while (it.hasNext()) {
                    g = (guide) it.next();
                    // id string should be same as servoIDStr
                    output.println(CFG_PARM_NAME.buildPEC.toString()
                    + "   "
                    + sid
                    + "   "
                    + g.descriptStr
                    + "   "
                    + g.motorStepsPerPECArray
                    + "   "
                    + g.PECSize
                    + "   "
                    + g.guidingCycles
                    + "   "
                    + g.PECRotation
                    + "   "
                    + g.PECIxOffset);
                }
            }

            output.println("\n[*** guide section ***]");

            s = CFG_PARM_NAME.guideDragRaArcsecPerMin.toString();
            output.println(eString.padString(s, parmPos[0]) + guideDragRaArcsecPerMin);

            s = CFG_PARM_NAME.guideDragDecArcsecPerMin.toString();
            output.println(eString.padString(s, parmPos[0]) + guideDragDecArcsecPerMin);

            s = CFG_PARM_NAME.handpadUpdateDrift.toString();
            output.println(eString.padString(s, parmPos[0]) + handpadUpdateDrift);

            output.println("\n[*** drift section ***]");

            s = CFG_PARM_NAME.driftRaDegPerHr.toString();
            output.println(eString.padString(s, parmPos[0]) + driftRaDegPerHr);

            s = CFG_PARM_NAME.driftDecDegPerHr.toString();
            output.println(eString.padString(s, parmPos[0]) + driftDecDegPerHr);

            output.println("\n[*** encoder section ***]");

            s = CFG_PARM_NAME.encoderType.toString();
            if (encoderType == null)
                s1 = "(unknown)";
            else {
                // remove leading ENCODER_PREFIX
                s1 = encoderType.toString();
                s1 = s1.substring(eString.ENCODER_PREFIX.length(), s1.length());
            }
            output.println(eString.padString(s, parmPos[0]) + s1);

            s = CFG_PARM_NAME.encoderIOType.toString();
            if (encoderIOType == null)
                s1 = "(unknown)";
            else {
                // remove IO_PREFIX
                s1 = encoderIOType.toString();
                s1 = s1.substring(eString.IO_PREFIX.length(), s1.length());
            }
            output.println(eString.padString(s, parmPos[0]) + s1);

            s = CFG_PARM_NAME.encoderSerialPortName.toString();
            output.println(eString.padString(s, parmPos[0]) + encoderSerialPortName);

            s = CFG_PARM_NAME.encoderBaudRate.toString();
            output.println(eString.padString(s, parmPos[0]) + encoderBaudRate);

            s = CFG_PARM_NAME.encoderHomeIPPort.toString();
            output.println(eString.padString(s, parmPos[0]) + encoderHomeIPPort);

            s = CFG_PARM_NAME.encoderRemoteIPName.toString();
            output.println(eString.padString(s, parmPos[0]) + encoderRemoteIPName);

            s = CFG_PARM_NAME.encoderRemoteIPPort.toString();
            output.println(eString.padString(s, parmPos[0]) + encoderRemoteIPPort);

            s = CFG_PARM_NAME.encoderFileLocation.toString();
            output.println(eString.padString(s, parmPos[0]) + encoderFileLocation);

            s = CFG_PARM_NAME.encoderTrace.toString();
            output.println(eString.padString(s, parmPos[0]) + encoderTrace);

            s = CFG_PARM_NAME.encoderAltDecCountsPerRev.toString();
            output.println(eString.padString(s, parmPos[0]) + encoderAltDecCountsPerRev);

            s = CFG_PARM_NAME.encoderAzRaCountsPerRev.toString();
            output.println(eString.padString(s, parmPos[0]) + encoderAzRaCountsPerRev);

            s = CFG_PARM_NAME.encoderAltDecDir.toString();
            output.println(eString.padString(s, parmPos[0]) + encoderAltDecDir);

            s = CFG_PARM_NAME.encoderAzRaDir.toString();
            output.println(eString.padString(s, parmPos[0]) + encoderAzRaDir);

            s = CFG_PARM_NAME.encoderFieldRDir.toString();
            output.println(eString.padString(s, parmPos[0]) + encoderFieldRDir);

            s = CFG_PARM_NAME.encoderFocusDir.toString();
            output.println(eString.padString(s, parmPos[0]) + encoderFocusDir);

            s = CFG_PARM_NAME.encoderErrorThresholdDeg.toString();
            output.println(eString.padString(s, parmPos[0]) + encoderErrorThresholdDeg);

            s = CFG_PARM_NAME.resetScopeToEncodersTrackOffResetTarget.toString();
            output.println(eString.padString(s, parmPos[0]) + resetScopeToEncodersTrackOffResetTarget);

            s = CFG_PARM_NAME.resetScopeToEncodersTrackingResetTarget.toString();
            output.println(eString.padString(s, parmPos[0]) + resetScopeToEncodersTrackingResetTarget);

            s = CFG_PARM_NAME.resetScopeToEncodersSlewingResetTarget.toString();
            output.println(eString.padString(s, parmPos[0]) + resetScopeToEncodersSlewingResetTarget);

            s = CFG_PARM_NAME.encoderAltDecOffset.toString();
            output.println(eString.padString(s, parmPos[0]) + encoderAltDecOffset);

            s = CFG_PARM_NAME.encoderAzRaOffset.toString();
            output.println(eString.padString(s, parmPos[0]) + encoderAzRaOffset);

            output.println("\n[*** external program interface section ***]");

            s = CFG_PARM_NAME.ProjectPlutoGuidePath.toString();
            output.println(eString.padString(s, parmPos[0]) + ProjectPlutoGuidePath);

            s = CFG_PARM_NAME.ProjectPlutoGuideExec.toString();
            output.println(eString.padString(s, parmPos[0]) + ProjectPlutoGuideExec);

            s = CFG_PARM_NAME.readWriteExternalSlewFiles.toString();
            output.println(eString.padString(s, parmPos[0]) + readWriteExternalSlewFiles);

            s = CFG_PARM_NAME.updateHTMLFreqSec.toString();
            output.println(eString.padString(s, parmPos[0]) + updateHTMLFreqSec);

            s = CFG_PARM_NAME.extIOType.toString();
            if (extIOType == null)
                s1 = "(unknown)";
            else {
                // remove IO_PREFIX
                s1 = extIOType.toString();
                s1 = s1.substring(eString.IO_PREFIX.length(), s1.length());
            }
            output.println(eString.padString(s, parmPos[0]) + s1);

            s = CFG_PARM_NAME.extSerialPortName.toString();
            output.println(eString.padString(s, parmPos[0]) + extSerialPortName);

            s = CFG_PARM_NAME.extBaudRate.toString();
            output.println(eString.padString(s, parmPos[0]) + extBaudRate);

            s = CFG_PARM_NAME.extHomeIPPort.toString();
            output.println(eString.padString(s, parmPos[0]) + extHomeIPPort);

            s = CFG_PARM_NAME.extRemoteIPName.toString();
            output.println(eString.padString(s, parmPos[0]) + extRemoteIPName);

            s = CFG_PARM_NAME.extRemoteIPPort.toString();
            output.println(eString.padString(s, parmPos[0]) + extRemoteIPPort);

            s = CFG_PARM_NAME.extFileLocation.toString();
            output.println(eString.padString(s, parmPos[0]) + extFileLocation);

            s = CFG_PARM_NAME.extTrace.toString();
            output.println(eString.padString(s, parmPos[0]) + extTrace);

            s = CFG_PARM_NAME.extPortWaitTimeMilliSecs.toString();
            output.println(eString.padString(s, parmPos[0]) + extPortWaitTimeMilliSecs);

            output.println("\n[*** LX200 input protocol section ***]");

            s = CFG_PARM_NAME.LX200Control.toString();
            output.println(eString.padString(s, parmPos[0]) + LX200Control);

            s = CFG_PARM_NAME.LX200IOType.toString();
            if (LX200IOType == null)
                s1 = "(unknown)";
            else {
                // remove IO_PREFIX
                s1 = LX200IOType.toString();
                s1 = s1.substring(eString.IO_PREFIX.length(), s1.length());
            }
            output.println(eString.padString(s, parmPos[0]) + s1);

            s = CFG_PARM_NAME.LX200SerialPortName.toString();
            output.println(eString.padString(s, parmPos[0]) + LX200SerialPortName);

            s = CFG_PARM_NAME.LX200BaudRate.toString();
            output.println(eString.padString(s, parmPos[0]) + LX200BaudRate);

            s = CFG_PARM_NAME.LX200homeIPPort.toString();
            output.println(eString.padString(s, parmPos[0]) + LX200homeIPPort);

            s = CFG_PARM_NAME.LX200remoteIPName.toString();
            output.println(eString.padString(s, parmPos[0]) + LX200remoteIPName);

            s = CFG_PARM_NAME.LX200RemoteIPPort.toString();
            output.println(eString.padString(s, parmPos[0]) + LX200RemoteIPPort);

            s = CFG_PARM_NAME.LX200FileLocation.toString();
            output.println(eString.padString(s, parmPos[0]) + LX200FileLocation);

            s = CFG_PARM_NAME.LX200Trace.toString();
            output.println(eString.padString(s, parmPos[0]) + LX200Trace);

            s = CFG_PARM_NAME.LX200MotionTimeoutSec.toString();
            output.println(eString.padString(s, parmPos[0]) + LX200MotionTimeoutSec);

            s = CFG_PARM_NAME.LX200_LongFormat.toString();
            output.println(eString.padString(s, parmPos[0]) + LX200_LongFormat);

            s = CFG_PARM_NAME.LX200_ContinueTrack.toString();
            output.println(eString.padString(s, parmPos[0]) + LX200_ContinueTrack);

            output.println("\n[*** servo section ***]");

            s = CFG_PARM_NAME.controllerManufacturer.toString();
            if (controllerManufacturer == null)
                s1 = "(unknown)";
            else
                s1 = controllerManufacturer.toString();
            output.println(eString.padString(s, parmPos[0]) + s1);

            s = CFG_PARM_NAME.servoIOType.toString();
            if (servoIOType == null)
                s1 = "(unknown)";
            else {
                // remove IO_PREFIX
                s1 = servoIOType.toString();
                s1 = s1.substring(eString.IO_PREFIX.length(), s1.length());
            }
            output.println(eString.padString(s, parmPos[0]) + s1);

            s = CFG_PARM_NAME.servoSerialPortName.toString();
            output.println(eString.padString(s, parmPos[0]) + servoSerialPortName);

            s = CFG_PARM_NAME.servoBaudRate.toString();
            output.println(eString.padString(s, parmPos[0]) + servoBaudRate);

            s = CFG_PARM_NAME.servoHomeIPPort.toString();
            output.println(eString.padString(s, parmPos[0]) + servoHomeIPPort);

            s = CFG_PARM_NAME.servoRemoteIPName.toString();
            output.println(eString.padString(s, parmPos[0]) + servoRemoteIPName);

            s = CFG_PARM_NAME.servoRemoteIPPort.toString();
            output.println(eString.padString(s, parmPos[0]) + servoRemoteIPPort);

            s = CFG_PARM_NAME.servoFileLocation.toString();
            output.println(eString.padString(s, parmPos[0]) + servoFileLocation);

            s = CFG_PARM_NAME.servoTrace.toString();
            output.println(eString.padString(s, parmPos[0]) + servoTrace);

            s = CFG_PARM_NAME.servoPortWaitTimeMilliSecs.toString();
            output.println(eString.padString(s, parmPos[0]) + servoPortWaitTimeMilliSecs);

            s = CFG_PARM_NAME.moveToTargetTimeSec.toString();
            output.println(eString.padString(s, parmPos[0]) + moveToTargetTimeSec);

            output.println("\n[*** user interface section ***]");

            s = CFG_PARM_NAME.UILookAndFeel.toString();
            output.println(eString.padString(s, parmPos[0]) + UILookAndFeel);

            s = CFG_PARM_NAME.usePanelColors.toString();
            output.println(eString.padString(s, parmPos[0]) + usePanelColors);

            s = CFG_PARM_NAME.lightPanel.toString();
            s1 = eString.intToString(lightPanel.r, 3);
            s2 = eString.intToString(lightPanel.g, 3);
            s3 = eString.intToString(lightPanel.b, 3);
            output.println(eString.padString(s, parmPos[0])
            + s1
            + " "
            + s2
            + " "
            + s3);

            s = CFG_PARM_NAME.mediumPanel.toString();
            s1 = eString.intToString(mediumPanel.r, 3);
            s2 = eString.intToString(mediumPanel.g, 3);
            s3 = eString.intToString(mediumPanel.b, 3);
            output.println(eString.padString(s, parmPos[0])
            + s1
            + " "
            + s2
            + " "
            + s3);

            s = CFG_PARM_NAME.darkPanel.toString();
            s1 = eString.intToString(darkPanel.r, 3);
            s2 = eString.intToString(darkPanel.g, 3);
            s3 = eString.intToString(darkPanel.b, 3);
            output.println(eString.padString(s, parmPos[0])
            + s1
            + " "
            + s2
            + " "
            + s3);

            s = CFG_PARM_NAME.stopToggle.toString();
            s1 = eString.intToString(stopToggle.r, 3);
            s2 = eString.intToString(stopToggle.g, 3);
            s3 = eString.intToString(stopToggle.b, 3);
            output.println(eString.padString(s, parmPos[0])
            + s1
            + " "
            + s2
            + " "
            + s3);

            s = CFG_PARM_NAME.goToggle.toString();
            s1 = eString.intToString(goToggle.r, 3);
            s2 = eString.intToString(goToggle.g, 3);
            s3 = eString.intToString(goToggle.b, 3);
            output.println(eString.padString(s, parmPos[0])
            + s1
            + " "
            + s2
            + " "
            + s3);

            s = CFG_PARM_NAME.toggle.toString();
            s1 = eString.intToString(toggle.r, 3);
            s2 = eString.intToString(toggle.g, 3);
            s3 = eString.intToString(toggle.b, 3);
            output.println(eString.padString(s, parmPos[0])
            + s1
            + " "
            + s2
            + " "
            + s3);

            s = CFG_PARM_NAME.radioButton.toString();
            s1 = eString.intToString(radioButton.r, 3);
            s2 = eString.intToString(radioButton.g, 3);
            s3 = eString.intToString(radioButton.b, 3);
            output.println(eString.padString(s, parmPos[0])
            + s1
            + " "
            + s2
            + " "
            + s3);

            s = CFG_PARM_NAME.comboBox.toString();
            s1 = eString.intToString(comboBox.r, 3);
            s2 = eString.intToString(comboBox.g, 3);
            s3 = eString.intToString(comboBox.b, 3);
            output.println(eString.padString(s, parmPos[0])
            + s1
            + " "
            + s2
            + " "
            + s3);

            s = CFG_PARM_NAME.updateUImilliSec.toString();
            output.println(eString.padString(s, parmPos[0]) + updateUImilliSec);

            s = CFG_PARM_NAME.datFileLocation.toString();
            output.println(eString.padString(s, parmPos[0]) + datFileLocation);

            s = CFG_PARM_NAME.autoGenInitFile.toString();
            output.println(eString.padString(s, parmPos[0]) + autoGenInitFile);

            s = CFG_PARM_NAME.cmdFileLocation.toString();
            output.println(eString.padString(s, parmPos[0]) + cmdFileLocation);

            s = CFG_PARM_NAME.SiTechFirmwareFileLocation.toString();
            output.println(eString.padString(s, parmPos[0]) + SiTechFirmwareFileLocation);

            output.println("\n[*** SERVO_ID section ***]");

            s = ";SERVO_ID";
            s = eString.padString(s, parmPos[0]);
            id = 1;
            eSERVO_ID = SERVO_ID.elements();
            while (eSERVO_ID.hasMoreElements()) {
                SERVO_ID sid = (SERVO_ID) eSERVO_ID.nextElement();
                s1 = "";
                // otherwise incompatible type error
                s1 += sid;
                s2 = "";
                if (s1.length() >= parmPosColWidth-1)
                    s2 = s1.substring(0, parmPosColWidth-1);
                else
                    s2 = s1;
                s += s2;
                s = eString.padString(s, parmPos[id++]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.controllerType.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += servoParm[id].controllerType;
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.controllerActive.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += servoParm[id].controllerActive;
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.ampEnableActiveHigh.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += servoParm[id].ampEnableActiveHigh;
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.positionGainKp.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += servoParm[id].positionGainKp;
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.velGainKd.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += servoParm[id].velGainKd;
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.positionGainKi.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += servoParm[id].positionGainKi;
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.integrationLimitIL.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += servoParm[id].integrationLimitIL;
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.outputLimitOL.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += servoParm[id].outputLimitOL;
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.currentLimitCL.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += servoParm[id].currentLimitCL;
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.positionErrorLimitEL.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += servoParm[id].positionErrorLimitEL;
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.rateDivisorSR.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += servoParm[id].rateDivisorSR;
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.ampDeadbandComp.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += servoParm[id].ampDeadbandComp;
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            // don't write value of track into configuration file because tracking should be off when program starts

            s = SERVO_PARM_NAME.stepsPerRev.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += servoParm[id].stepsPerRev;
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.encoderCountsPerRev.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += servoParm[id].encoderCountsPerRev;
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.reverseMotor.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += servoParm[id].reverseMotor;
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            // use cfgAccelDegSecSec as accelDegSecSec changed during program operation
            s = SERVO_PARM_NAME.accelDegSecSec.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += eString.doubleToStringNoGrouping(servoParm[id].cfgAccelDegSecSec, 2, 2);
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.fastSpeedDegSec.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += eString.doubleToStringNoGrouping(servoParm[id].fastSpeedDegSec, 2, 2);
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.slowSpeedArcsecSec.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += eString.doubleToStringNoGrouping(servoParm[id].slowSpeedArcsecSec, 4, 2);
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.dampenFactor.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += servoParm[id].dampenFactor;
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.homeDeg.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += servoParm[id].homeDeg;
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.sectorDeg.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += eString.doubleToStringNoGrouping(servoParm[id].sectorDeg, 3, 2);
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.softLimitOn.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += servoParm[id].softLimitOn;
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.currentPositionDeg.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += eString.doubleToStringNoGrouping(servoParm[id].currentPositionDeg, 3, 4);
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.backlashArcmin.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += eString.doubleToStringNoGrouping(servoParm[id].backlashArcmin, 3, 3);
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.guideArcsecSec.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += eString.doubleToStringNoGrouping(servoParm[id].guideArcsecSec, 2, 2);
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.guideDragArcsecPerMin.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += servoParm[id].guideDragArcsecPerMin;
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.driftArcsecPerMin.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += servoParm[id].driftArcsecPerMin;
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.PECActive.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += servoParm[id].PECActive;
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            s = SERVO_PARM_NAME.autoPECSyncDetect.toString();
            s = eString.padString(s, parmPos[0]);
            for (id = 0; id < SERVO_ID.size(); id++) {
                s += servoParm[id].autoPECSyncDetect;
                s = eString.padString(s, parmPos[id+1]);
            }
            output.println(s);

            output.println("\n'end of configuration file\n");
            output.close();
            console.stdOutLn("saved configuration file " + filename);
        }
        catch (IOException ioe) {
            console.errOut(ioe.toString());
            ioe.printStackTrace();
            System.err.println("could not open " + filename);
        }
    }
}

