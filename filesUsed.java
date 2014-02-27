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
 * contains files used documentation in string format
 */
public class filesUsed {
    static final String s =

    "scope.cfg is the configuration file, read at startup, optional save at shutdown\n"
    + "[SERVO_ID name]+[description]+[direction]PEC.data, ie, AltDecMotorMainBiDirPEC.data are the PEC files,"
    + "read in per configuration file, or read and saved during program execution\n"
    + "[SERVO_ID name]+[description]+[direction]PECxx.pec, ie, AltDecMotorMainBiDirPEC07.pec are the guide analysis files used to build"
    + "PEC, used in averagePECAnalysisFiles(), and created in analyzeGuideArray()\n"
    + "[SERVO_ID name]+[description]+[direction]PEC.guide, ie, AltDecMotorMainBiDirPEC.guide are the record of guiding corrections vs"
    + "PEC indeces, saved in saveGuide(), loaded in loadGuide(): guiding corrections are used to create guide analysis files which are used"
    + "to create PEC files; contrast with *.accumguide, which is a record of guiding corrects vs time\n"
    + "[SERVO_ID name].track.log, ie, altDec.track.log are the tracking analyses files, one per active motor, saved in track.close()\n"
    + "[SERVO_ID name].accumguide.log is log of accumulated guiding values vs time\n"
    + "[io port name].log is log of all receive and transmit traffic on that io port, "
    + "the port name being the concatenation of pertinent port descriptors, ie baud rate, port #, and so forth\n"
    + "scope.servocmd.log is log of servo command results\n"
    + "scope.encoders.reset.log is a log of encoder to scope and scope to encoder resets\n"
    + "scope.polaralign.log is record of polar alignment procedure\n"
    + "[axisToAxisName].ec, ie, AltitudeToAzimuth.ec holds axis vs axis pointing correction values\n"
    + "[limitMotionName].lm, ie, limit_motion_siteAltaz.lm holds numbers outlining a polygon to limit the scope's motion\n"
    + "scope.equat is record of equatorial Positions\n"
    + "scope.altaz is record of altazimuth Positions\n"
    + "*.DAT are datafiles of objects\n"
    + "*.comet are datafiles of comets\n"
    + "*.cmd are command files\n"
    + "ioFileWrite.txt and ioFileRead.txt are the hardcoded file names used for the file method of input-output "
    + "since last check, then contents are read and executed (all commands will be executed before updated file will be read in)\n"
    + "scope.pmc contains pointing model correction values\n"
    + "scope.analysis contains input equatorial coordinates, actual altazimuth coordinates, sidereal time of altaz coordinate "
    + "calculation, optional errors in altazimuth coordinates calculated from input equatorial coordinates converted to altaz "
    + "coordinates minus the actual altaz coordinates, and optional name\n"
    + "scope.inithist is history of all initializations for each session\n"
    + "slew.dat is a file containing an equatorial position to slew to\n"
    + "SLEW_OUT.DAT is a file containing the current equatorial position to slew an external program\n"
    + "startup.mar is the startup filed used by ProjectPluto's guide that includes the position that guide should center on\n"
    + "scope.mar is the startup file for ProjectPluto guide created by scope\n"
    + "guide.bat is the batch file used to copy the startup for for ProjectPluto guide, then to launch ProjectPluto guide\n"
    + "setdate.bat is the batch file used to set the system date\n"
    + "settime.bat is the batch file used to set the system time\n"
    + "input.DAT is current sessions' recording of input coordinates saved with datetime\n"
    + "input[savedDateTime].DAT is a copy of a session's input.DAT file\n"
    + "\n"
    + "status.html is the status html page\n"
    + "servoStatus.html is the servo controller status html page\n"
    + "currEquatCoord.html is the current equatorial coordinate html page\n"
    + "cmdScope.html is the cmd_scope commands and their descriptions html page\n"
    + "parms.html is the configuration and servo parameters and their descriptions html page\n"
    + "filesUsed.html is the files used html page\n"
    + "cmdLineArgs.html is the command line html page\n"
    + "status.css is the cascading style sheet for the status files\n"
    + "currEquatCoord.css is the cascading style sheet for the currEquatCoord.html file\n"
    + "\n"
    + "Windows bat files for program manipulation (for Linux, change backslashes to forward slashes, ie, ../j2sdk1.4.2_02/bin/java...):\n"
    + "scope.bat to run scope.jar                   c:\\j2sdk1.4.2_02\bin\\java -cp scope.jar;RXTXcomm.jar scope\n"
    + "test.bat to run scope.java in test mode      c:\\j2sdk1.4.2_02\\bin\\java -cp scope.jar;RXTXcomm.jar scope -t\n"
    + "\n"
    + "manifest.mf file contains the line:   Main-Class: scope\n"
    + "\n";

    static void display() {
        console.stdOutLn(s);
    }
}

