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

public final class CFG_PARM_NAME {
    private String id;
    public final String description;
    public final int KEY;
    private CFG_PARM_NAME prev;
    private CFG_PARM_NAME next;

    private static int itemCount;
    private static CFG_PARM_NAME first;
    private static CFG_PARM_NAME last;

    private CFG_PARM_NAME(String id) {
        this(id, "");
    }

    private CFG_PARM_NAME(String id, String description) {
        this.id = new String(id);
        this.description = description;
        this.KEY = itemCount++;
        if (first == null)
            first = this;
        if (last != null) {
            this.prev = last;
            last.next = this;
        }
        last = this;
    }

    public static Enumeration elements() {
        return new Enumeration() {
            private CFG_PARM_NAME current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                CFG_PARM_NAME c = current;
                current = current.next();
                return c;
            }
        };
    }

    public String toString() {
        return this.id;
    }

    public static int size() {
        return itemCount;
    }

    public static CFG_PARM_NAME first() {
        return first;
    }

    public static CFG_PARM_NAME last() {
        return last;
    }

    public CFG_PARM_NAME prev() {
        return this.prev;
    }

    public CFG_PARM_NAME next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of CFG_PARM_NAME, which has "
        + itemCount
        + " elements:");
        CFG_PARM_NAME current = first;
        while (current != null) {
            console.stdOutLn(current.id
            + ": "
            + current.KEY);
            current = current.next;
        }
        console.stdOutLn("end of display");
    }

    public static void displayCmdScope() {
        console.stdOut("display of CFG_PARM_NAME, which has "
        + itemCount
        + " elements:"
        + "\n\n");
        CFG_PARM_NAME current = first;
        while (current != null) {
            console.stdOut(current.id
            + ": "
            + current.description
            + "\n\n");
            current = current.next;
        }
        console.stdOutLn("end of display");
    }

    public static String returnItemListAsString() {
        String s = "";

        CFG_PARM_NAME current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static CFG_PARM_NAME matchKey(int i) {
        CFG_PARM_NAME O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static CFG_PARM_NAME matchStr(String s) {
        CFG_PARM_NAME O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static CFG_PARM_NAME matchDesc(String s) {
        CFG_PARM_NAME O = first();
        while (O != null && !s.equalsIgnoreCase(O.description))
            O = O.next();
        return O;
    }

    public static final CFG_PARM_NAME siteName = new CFG_PARM_NAME("siteName",
    "geographic location's name");

    public static final CFG_PARM_NAME latitudeDeg = new CFG_PARM_NAME("latitudeDeg",
    "geographic location's latitude in decimal degrees");

    public static final CFG_PARM_NAME longitudeDeg = new CFG_PARM_NAME("longitudeDeg",
    "geographic location's longitude in decimal degrees; positive values indicates west of Greenwich");

    public static final CFG_PARM_NAME mountType = new CFG_PARM_NAME("mountType",
    "mounting type, options are:\n"
    + MOUNT_TYPE.returnItemListAsString()
    + "\ndescriptions:"
    + "\nmountTypeCustom: user configured"
    + "\nmountTypeEquatorial: plain old equatorial mount where primary axis that rotates 360 deg aimed at pole"
    + "\nmountTypeAltazimuth: plain old altazimuth mount where primary axis that rotates 360 deg aimed at local zenith"
    + "\nmountTypeAltAlt: altazimuth mount where where primary axis that rotates 360 deg aimed at horizon"
    + "\nmountTypeHorseshoe: can swing to pole but cannot swing past pole"
    + "\nmountTypeEquatorialFork: can swing to and past pole into sub-polar region"
    + "\nmountTypeEquatorialYoke: cannot swing near pole"
    + "\nmountTypeCrossAxisEnglish: can track well past meridian when aimed toward celestial equator, but cannot cross meridian while aimed at sub-polar region due to poleward support post"
    + "\nmountTypeSplitRing: can swing through pole, cannot rotate primary axis full circle in RA"
    + "\nmountTypeGermanEquatorialMount: requires meridian flip"
    + "\nmountTypeExtendedGerman: no meridian flip, no pole support to impede crossing meridian while pointing underneath pole"
    + "\nmountTypeOffAxisTorqueTube: configured same as extended german"
    + "\nmountTypeWeightStressCompensated: per famous Zeiss example: dec and ota pivot on top of RA axis, counterweights held by bars that are placed outside ota and hang down past pivot"
    + "\nmountTypeInvertedFork: top of RA axis is split into fork that moves in dec, ota held by outside inverted fork that fits over the RA fork, this outside fork also holds the counterweights for the ota"
    + "\nmountTypeSiderostat: means stationary star, tube horizontal pointed at pole, flat is mounted equatorially"
    + "\nmountTypePolarSiderostat: means stationary star, tube parallel to polar axis looking down into flat, flat is mounted equatorially"
    + "\nmountTypeUranostat: tube horizontal pointed at pole, flat is mounted altazimuthly"
    + "\nmountTypeHeliostat: same as siderostat but looks at the Sun, only one axis of movement"
    + "mountTypePolarHeliostat: same as heliostat but tube aimed up at polar axis"
    + "\nmountTypeCoelostat: developed from siderostat; means stationary sky, siderostat mirror fixed parallel to polar axis, tube moves in dec; plane mirror mounted facing the celestial equator on axis pointing to celestial pole, when driven around axis the reflected beam remains stationary and does not alter its orientation; sometimes a 2nd mirror used to reflect the light into the telescope"
    + "\nmountTypeCoude: 2 mirrors, the upper rotates in Dec, the lower rotates in RA"
    + "\nmountTypeSpringfield: 2 diagonals produce stationary eyepiece, needs meridian flip");

    public static final CFG_PARM_NAME canMoveToPole = new CFG_PARM_NAME("canMoveToPole",
    "if the telescope can point at the pole, then set this value to 'true', else 'false'");

    public static final CFG_PARM_NAME canMoveThruPole = new CFG_PARM_NAME("canMoveThruPole",
    "if the telescope can swing through the pole into the sub-polar region, then set this value to 'true', else 'false'");

    public static final CFG_PARM_NAME primaryAxisFullyRotates = new CFG_PARM_NAME("primaryAxisFullyRotates",
    "if the mount's primary axis can rotate through a full circle, then set this value to 'true', else 'false'");

    public static final CFG_PARM_NAME meridianFlipPossible = new CFG_PARM_NAME("meridianFlipPossible",
    "if a meridian flip is possible, then set this value to 'true', else 'false'");

    public static final CFG_PARM_NAME meridianFlipRequired = new CFG_PARM_NAME("meridianFlipRequired",
    "if a flip is required when the telescope crosses the meridian, then set this value to 'true', else 'false'");

    public static final CFG_PARM_NAME autoMeridianFlip = new CFG_PARM_NAME("autoMeridianFlip",
    "automatically flip the telescope if crossing the meridian; true/false");

    public static final CFG_PARM_NAME autoMeridianFlipFuzzDeg = new CFG_PARM_NAME("autoMeridianFlipFuzzDeg",
    "distance that scope can track past the meridian before engaging the meridian flip, in decimal degrees");

    public static final CFG_PARM_NAME eyepieceFocus = new CFG_PARM_NAME("eyepieceFocus",
    "an eyepiece's name and focus position");

    public static final CFG_PARM_NAME handpadPresent = new CFG_PARM_NAME("handpadPresent",
    "if handpad is connected to the PIC servo control boards, then 'true' else 'false'");

    public static final CFG_PARM_NAME handpadMode = new CFG_PARM_NAME("handpadMode",
    "starting handpad mode, options are:\n" + HANDPAD_MODE.returnItemListAsString());

    public static final CFG_PARM_NAME handpadDesign = new CFG_PARM_NAME("handpadDesign",
    "starting handpad direction button meaning, options are:\n" + HANDPAD_DESIGN.returnItemListAsString());

    public static final CFG_PARM_NAME handpadFlipUpDown = new CFG_PARM_NAME("handpadFlipUpDown",
    "reverse the up/down handpad buttons");

    public static final CFG_PARM_NAME handpadFollowMeridianFlip = new CFG_PARM_NAME("handpadFollowMeridianFlip",
    "flip handpad's up/down buttons if meridian flipped since declination direction inverted when meridian flipped");

    public static final CFG_PARM_NAME SiTechRampDownDelaySec = new CFG_PARM_NAME("SiTechRampDownDelaySec",
    "ramp down delay after releasing a handpad direction button: prevents the scope from back tracking");

    public static final CFG_PARM_NAME spiralSearchRadiusDeg = new CFG_PARM_NAME("spiralSearchRadiusDeg",
    "spiral search pattern's radius, or, the distance between spiral sweeps");

    public static final CFG_PARM_NAME spiralSearchSpeedDegSec = new CFG_PARM_NAME("spiralSearchSpeedDegSec",
    "speed at which to traverse the spiral search pattern");

    public static final CFG_PARM_NAME initState = new CFG_PARM_NAME("initState",
    "starting initialization state, options are:\n" + INIT_STATE.returnItemListAsString());

    public static final CFG_PARM_NAME initOne = new CFG_PARM_NAME("initOne",
    "let the program enter these values; initialization one coordinate values, ie, '0.0 90.0 44.0 0.0 0.0'");

    public static final CFG_PARM_NAME initTwo = new CFG_PARM_NAME("initTwo",
    "let the program enter these values; initialization two coordinate values, ie, '0.0 90.0 44.0 0.0 0.0'");

    public static final CFG_PARM_NAME initThree = new CFG_PARM_NAME("initThree",
    "let the program enter these values; initialization three coordinate values, ie, '0.0 90.0 44.0 0.0 0.0'");

    public static final CFG_PARM_NAME z1Deg = new CFG_PARM_NAME("z1Deg",
    "axis misalignment error in decimal degrees");

    public static final CFG_PARM_NAME z2Deg = new CFG_PARM_NAME("z2Deg",
    "azimuth offset error in decimal degrees");

    public static final CFG_PARM_NAME z3Deg = new CFG_PARM_NAME("z3Deg",
    "altitude offset error in decimal degrees");

    public static final CFG_PARM_NAME dataFileCoordYear = new CFG_PARM_NAME("dataFileCoordYear",
    "coordinate year for the data files");

    public static final CFG_PARM_NAME precessionNutationAberration = new CFG_PARM_NAME("precessionNutationAberration",
    "turn on/off precession, nutation, and annual aberration corrections, particularly when reading the data files");

    public static final CFG_PARM_NAME refractAlign = new CFG_PARM_NAME("refractAlign",
    "mount alignment to use to calculate refraction, options are:\n" + ALIGNMENT.returnItemListAsString());

    public static final CFG_PARM_NAME backlashActive = new CFG_PARM_NAME("backlashActive",
    "'true' if backlash correction active, otherwise 'false'");

    public static final CFG_PARM_NAME useAltAzEC = new CFG_PARM_NAME("useAltAzEC",
    "'true' if using altitude vs azimuth axis error correction, otherwise 'false'");

    public static final CFG_PARM_NAME useAltAltEC = new CFG_PARM_NAME("useAltAltEC",
    "'true' if using altitude vs altitude axis error correction, otherwise 'false'");

    public static final CFG_PARM_NAME useAzAzEC = new CFG_PARM_NAME("useAzAzEC",
    "'true' if using azimuth vs azimuth axis error correction, otherwise 'false'");

    public static final CFG_PARM_NAME usePMC = new CFG_PARM_NAME("usePMC",
    "'true' if using pointing model error correction, otherwise 'false'");

    public static final CFG_PARM_NAME buildPEC = new CFG_PARM_NAME("buildPEC",
    "how to build PEC file, format: servoID  description  motorStepsPerPECArray  PECSize  guidingCycles  Rotation  PECIxOffset");

    public static final CFG_PARM_NAME guideDragRaArcsecPerMin = new CFG_PARM_NAME("guideDragRaArcsecPerMin",
    "Right Ascension drag in arcseconds per minute of time so that guiding corrections occur in one direction when handpadMode is handpadModeGuideStayDrag");

    public static final CFG_PARM_NAME guideDragDecArcsecPerMin = new CFG_PARM_NAME("guideDragDecArcsecPerMin",
    "Declination drag in arcseconds per minute of time so that guiding corrections occur in one direction when handpadMode is handpadModeGuideStayDrag");

    public static final CFG_PARM_NAME handpadUpdateDrift = new CFG_PARM_NAME("handpadUpdateDrift",
    "if 'true', then adopt drift values as calculated from guiding efforts, else 'false'");

    public static final CFG_PARM_NAME driftRaDegPerHr = new CFG_PARM_NAME("driftRaDegPerHr",
    "drift in Right Ascension in degrees per hour");

    public static final CFG_PARM_NAME driftDecDegPerHr = new CFG_PARM_NAME("driftDecDegPerHr",
    "drift in Declination in degrees per hour");

    public static final CFG_PARM_NAME encoderType = new CFG_PARM_NAME("encoderType",
    "encoder interface box type, options are:\n" + ENCODER_TYPE.returnItemListAsString());

    public static final CFG_PARM_NAME encoderIOType = new CFG_PARM_NAME("encoderIOType",
    "what type of IO method to use for the encoders, options are:\n" + IO_TYPE.returnItemListAsString());

    public static final CFG_PARM_NAME encoderSerialPortName = new CFG_PARM_NAME("encoderSerialPortName",
    "serial port to use for the encoders, ie, 'COM1' if Windows, '/dev/ttyS0' if Linux'");

    public static final CFG_PARM_NAME encoderBaudRate = new CFG_PARM_NAME("encoderBaudRate",
    "serial port baud rate for encoders");

    public static final CFG_PARM_NAME encoderHomeIPPort = new CFG_PARM_NAME("encoderHomeIPPort",
    "if using UDP method for the encoders, then the home port# to use");

    public static final CFG_PARM_NAME encoderRemoteIPName = new CFG_PARM_NAME("encoderRemoteIPName",
    "if using UDP or TCP method for the encoders, then the remote machine's IP address");

    public static final CFG_PARM_NAME encoderRemoteIPPort = new CFG_PARM_NAME("encoderRemoteIPPort",
    "if using UDP or TCP method for the encoders, then the remote machine's port#");

    public static final CFG_PARM_NAME encoderFileLocation = new CFG_PARM_NAME("encoderFileLocation",
    "if using files to transmit/receive, then location of the files");

    public static final CFG_PARM_NAME encoderTrace = new CFG_PARM_NAME("encoderTrace",
    "record all communications to trace file");

    public static final CFG_PARM_NAME encoderAltDecCountsPerRev = new CFG_PARM_NAME("encoderAltDecCountsPerRev",
    "altitude axis encoder counts per shaft revolution");

    public static final CFG_PARM_NAME encoderAzRaCountsPerRev = new CFG_PARM_NAME("encoderAzRaCountsPerRev",
    "azimith axis encoder counts per shaft revolution");

    public static final CFG_PARM_NAME encoderAltDecDir = new CFG_PARM_NAME("encoderAltDecDir",
    "direction that altitude axis encoder counts increment, options are:\n" + ROTATION.returnItemListAsString());

    public static final CFG_PARM_NAME encoderAzRaDir = new CFG_PARM_NAME("encoderAzRaDir",
    "direction that azimuth axis encoder counts increment, options are:\n" + ROTATION.returnItemListAsString());

    public static final CFG_PARM_NAME encoderFieldRDir = new CFG_PARM_NAME("encoderFieldRDir",
    "direction that the field rotation axis encoder counts increment, options are:\n" + ROTATION.returnItemListAsString());

    public static final CFG_PARM_NAME encoderFocusDir = new CFG_PARM_NAME("encoderFocusDir",
    "direction that the focuser axis encoder counts increment, options are:\n" + ROTATION.returnItemListAsString());

    public static final CFG_PARM_NAME encoderErrorThresholdDeg = new CFG_PARM_NAME("encoderErrorThresholdDeg",
    "error threshold of encoder vs current position before current position is reset to encoder position, in decimal degrees");

    public static final CFG_PARM_NAME resetScopeToEncodersTrackOffResetTarget = new CFG_PARM_NAME("resetScopeToEncodersTrackOffResetTarget",
    "if encoder vs current position exceeds error threshold when tracking is off, then reset the target coordinates");

    public static final CFG_PARM_NAME resetScopeToEncodersTrackingResetTarget = new CFG_PARM_NAME("resetScopeToEncodersTrackingResetTarget",
    "if encoder vs current position exceeds error threshold when tracking, then reset the target coordinates");

    public static final CFG_PARM_NAME resetScopeToEncodersSlewingResetTarget = new CFG_PARM_NAME("resetScopeToEncodersSlewingResetTarget",
    "if encoder vs current position exceeds error threshold when slewing to a target position, then reset the target coordinates");

    public static final CFG_PARM_NAME encoderAltDecOffset = new CFG_PARM_NAME("encoderAltDecOffset",
    "set by the program; difference between encoder and current altitude axis position");

    public static final CFG_PARM_NAME encoderAzRaOffset = new CFG_PARM_NAME("encoderAzRaOffset",
    "set by the program; difference between encoder and current azimuth axis position");

    public static final CFG_PARM_NAME ProjectPlutoGuidePath = new CFG_PARM_NAME("ProjectPlutoGuidePath",
    "directory of Project Pluto's guide planetarium program and thus location of slewFile and slewOutFile, ie 'c:\\GUIDE8\\'");

    public static final CFG_PARM_NAME ProjectPlutoGuideExec = new CFG_PARM_NAME("ProjectPlutoGuideExec",
    "name of Project Pluto's guide executable program, ie Guide8.exe");

    public static final CFG_PARM_NAME readWriteExternalSlewFiles = new CFG_PARM_NAME("readWriteExternalSlewFiles",
    "if 'true', actively read/write the external slew files, else 'false'");

    public static final CFG_PARM_NAME updateHTMLFreqSec = new CFG_PARM_NAME("updateHTMLFreqSec",
    "html pages (ie, coordinate display and status displays) update frequency in seconds; if '0', then no update");

    public static final CFG_PARM_NAME extIOType = new CFG_PARM_NAME("extIOType",
    "what type of IO method to use for external program control, options are:\n" + IO_TYPE.returnItemListAsString());

    public static final CFG_PARM_NAME extSerialPortName = new CFG_PARM_NAME("extSerialPortName",
    "serial port to use for external program control, ie, 'COM1' if Windows, '/dev/ttyS0' if Linux'");

    public static final CFG_PARM_NAME extBaudRate = new CFG_PARM_NAME("extBaudRate",
    "baud rate for serial port for external program control");

    public static final CFG_PARM_NAME extHomeIPPort = new CFG_PARM_NAME("extHomeIPPort",
    "if using UDP method for external program control, then the home port# to use");

    public static final CFG_PARM_NAME extRemoteIPName = new CFG_PARM_NAME("extRemoteIPName",
    "if using UDP or TCP method for external program control, then the remote machine's IP address");

    public static final CFG_PARM_NAME extRemoteIPPort = new CFG_PARM_NAME("extRemoteIPPort",
    "if using UDP or TCP method for external program control, then the remote machine's port#");

    public static final CFG_PARM_NAME extFileLocation = new CFG_PARM_NAME("extFileLocation",
    "if using files to transmit/receive for external program control, then location of the files");

    public static final CFG_PARM_NAME extTrace = new CFG_PARM_NAME("extTrace",
    "record all communications to trace file");

    public static final CFG_PARM_NAME extPortWaitTimeMilliSecs = new CFG_PARM_NAME("extPortWaitTimeMilliSecs",
    "time in milliseconds to wait for completion of data transmit on the external port");

    public static final CFG_PARM_NAME LX200Control = new CFG_PARM_NAME("LX200Control",
    "'true' if software will receive LX200 styled commands, else 'false'");

    public static final CFG_PARM_NAME LX200IOType = new CFG_PARM_NAME("LX200IOType",
    "what type of IO method to use for LX200 styled commands, options are:\n" + IO_TYPE.returnItemListAsString());

    public static final CFG_PARM_NAME LX200SerialPortName = new CFG_PARM_NAME("LX200SerialPortName",
    "serial port to use for LX200 styled commands, ie, 'COM1' if Windows, '/dev/ttyS0' if Linux'");

    public static final CFG_PARM_NAME LX200BaudRate = new CFG_PARM_NAME("LX200BaudRate",
    "serial port baud rate for LX200 styled commands");

    public static final CFG_PARM_NAME LX200homeIPPort = new CFG_PARM_NAME("LX200homeIPPort",
    "if using UDP method for LX200 styled commands, then the home port# to use");

    public static final CFG_PARM_NAME LX200remoteIPName = new CFG_PARM_NAME("LX200remoteIPName",
    "if using UDP or TCP method for LX200 styled commands, then the remote machine's IP address");

    public static final CFG_PARM_NAME LX200RemoteIPPort = new CFG_PARM_NAME("LX200RemoteIPPort",
    "if using UDP or TCP method for LX200 styled commands, then the remote machine's port#");

    public static final CFG_PARM_NAME LX200FileLocation = new CFG_PARM_NAME("LX200FileLocation",
    "if using files to transmit/receive LX200 styled commands, then location of the files");

    public static final CFG_PARM_NAME LX200Trace = new CFG_PARM_NAME("LX200Trace",
    "record all communications to trace file");

    public static final CFG_PARM_NAME LX200MotionTimeoutSec = new CFG_PARM_NAME("LX200MotionTimeoutSec",
    "timeout in seconds to stop LX200 styled commanded motion if no stop command received");

    public static final CFG_PARM_NAME LX200_LongFormat = new CFG_PARM_NAME("LX200_LongFormat",
    "'true' if long format coordinate form is used with the LX200 styled commands, otherwise 'false'");

    public static final CFG_PARM_NAME LX200_ContinueTrack = new CFG_PARM_NAME("LX200_ContinueTrack",
    "'true' if tracking should continue after a LX200 Quit Motion or Stop Slew command is received, otherwise 'false'");

    public static final CFG_PARM_NAME controllerManufacturer = new CFG_PARM_NAME("controllerManufacturer",
    "controller manufacturer, options are:\n" + CONTROLLER_MANUFACTURER.returnItemListAsString());

    public static final CFG_PARM_NAME servoIOType = new CFG_PARM_NAME("servoIOType",
    "what type of IO method to use to communicate with the servo motor controllers, options are:\n" + IO_TYPE.returnItemListAsString());

    public static final CFG_PARM_NAME servoSerialPortName = new CFG_PARM_NAME("servoSerialPortName",
    "serial port to use to communicate with the servo motor controllers, ie, 'COM1' if Windows, '/dev/ttyS0' if Linux'");

    public static final CFG_PARM_NAME servoBaudRate = new CFG_PARM_NAME("servoBaudRate",
    "serial port baud rate to communicate with the servo motor controllers");

    public static final CFG_PARM_NAME servoHomeIPPort = new CFG_PARM_NAME("servoHomeIPPort",
    "if using UDP method to communicate with the servo motor controllers, then the home port# to use");

    public static final CFG_PARM_NAME servoRemoteIPName = new CFG_PARM_NAME("servoRemoteIPName",
    "if using UDP or TCP method to communicate with the servo motor controllers, then the remote machine's IP address");

    public static final CFG_PARM_NAME servoRemoteIPPort = new CFG_PARM_NAME("servoRemoteIPPort",
    "if using UDP or TCP method to communicate with the servo motor controllers, then the remote machine's IP port#");

    public static final CFG_PARM_NAME servoFileLocation = new CFG_PARM_NAME("servoFileLocation",
    "if using files to transmit/receive to communicate with the servo motor controllers, then location of the files");

    public static final CFG_PARM_NAME servoTrace = new CFG_PARM_NAME("servoTrace",
    "record all communications to trace file");

    public static final CFG_PARM_NAME servoPortWaitTimeMilliSecs = new CFG_PARM_NAME("servoPortWaitTimeMilliSecs",
    "time in milliseconds to wait for servo motor controller response before giving up");

    public static final CFG_PARM_NAME moveToTargetTimeSec = new CFG_PARM_NAME("moveToTargetTimeSec",
    "look ahead time in seconds to calculate motor's next move command");

    public static final CFG_PARM_NAME sequencerSleepTimeMilliSec = new CFG_PARM_NAME("sequencerSleepTimeMilliSec",
    "application's heartbeat sleep time in milliseconds; use to reduce cpu usage");

    public static final CFG_PARM_NAME UILookAndFeel = new CFG_PARM_NAME("UILookAndFeel",
    "look and feel of the user interface");

    public static final CFG_PARM_NAME usePanelColors = new CFG_PARM_NAME("usePanelColors",
    "use user-defined panel colors, otherwise use default colors");

    public static final CFG_PARM_NAME lightPanel = new CFG_PARM_NAME("lightPanel",
    "set the red, green, blue colors of light colored panels");

    public static final CFG_PARM_NAME mediumPanel = new CFG_PARM_NAME("mediumPanel",
    "set the red, green, blue colors of medium colored panels");

    public static final CFG_PARM_NAME darkPanel = new CFG_PARM_NAME("darkPanel",
    "set the red, green, blue colors of dark colored panels");

    public static final CFG_PARM_NAME stopToggle = new CFG_PARM_NAME("stopToggle",
    "set the red, green, blue colors of stop toggle buttons");

    public static final CFG_PARM_NAME goToggle = new CFG_PARM_NAME("goToggle",
    "set the red, green, blue colors of go toggle buttons");

    public static final CFG_PARM_NAME toggle = new CFG_PARM_NAME("toggle",
    "set the red, green, blue colors of toggle buttons");

    public static final CFG_PARM_NAME radioButton = new CFG_PARM_NAME("radioButton",
    "set the red, green, blue colors of radio buttons");

    public static final CFG_PARM_NAME comboBox = new CFG_PARM_NAME("comboBox",
    "set the red, green, blue colors of comboBoxes");

    public static final CFG_PARM_NAME updateUImilliSec = new CFG_PARM_NAME("updateUImilliSec",
    "time in milliseconds to update user interface displays");

    public static final CFG_PARM_NAME datFileLocation = new CFG_PARM_NAME("datFileLocation",
    "directory where the coordinate data files are located");

    public static final CFG_PARM_NAME autoGenInitFile = new CFG_PARM_NAME("autoGenInitFile",
    "name of the object datafile to use for automatic generation of initializations");

    public static final CFG_PARM_NAME cmdFileLocation = new CFG_PARM_NAME("cmdFileLocation",
    "directory where the command files are located");

    public static final CFG_PARM_NAME SiTechFirmwareFileLocation = new CFG_PARM_NAME("SiTechFirmwareFileLocation",
    "directory where the SiTech controller firmware upgrade files are located");
}

