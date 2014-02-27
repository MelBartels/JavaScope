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

public final class TEST_SELECT {
    private String id;
    public final int KEY;
    private TEST_SELECT prev;
    private TEST_SELECT next;

    private static int itemCount;
    private static TEST_SELECT first;
    private static TEST_SELECT last;

    private TEST_SELECT(String id) {
        this.id = new String(id);
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
            private TEST_SELECT current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                TEST_SELECT c = current;
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

    public static TEST_SELECT first() {
        return first;
    }

    public static TEST_SELECT last() {
        return last;
    }

    public TEST_SELECT prev() {
        return this.prev;
    }

    public TEST_SELECT next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of TEST_SELECT, which has "
        + itemCount
        + " elements:");
        TEST_SELECT current = first;
        while (current != null) {
            console.stdOutLn(current.id
            + ": "
            + current.KEY);
            current = current.next;
        }
        console.stdOutLn("end of display");
    }

    public static String returnItemListAsString() {
        String s = "";

        TEST_SELECT current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static TEST_SELECT matchKey(int i) {
        TEST_SELECT O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static TEST_SELECT matchStr(String s) {
        TEST_SELECT O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final TEST_SELECT SERVO_ID = new TEST_SELECT("SERVO_ID");
    public static final TEST_SELECT eString = new TEST_SELECT("eString");
    public static final TEST_SELECT cfg = new TEST_SELECT("cfg");
    public static final TEST_SELECT aTimes = new TEST_SELECT("astroTime");
    public static final TEST_SELECT beep = new TEST_SELECT("beep");
    public static final TEST_SELECT refract = new TEST_SELECT("refract");
    public static final TEST_SELECT equatRefract = new TEST_SELECT("equatRefract");
    public static final TEST_SELECT precession = new TEST_SELECT("precession");
    public static final TEST_SELECT nutation = new TEST_SELECT("nutation");
    public static final TEST_SELECT annualAberration = new TEST_SELECT("annualAberration");
    public static final TEST_SELECT coordinateCorrections = new TEST_SELECT("coordinateCorrections");
    public static final TEST_SELECT llPos = new TEST_SELECT("llPos");
    public static final TEST_SELECT reInitConversionMatrix = new TEST_SELECT("reInitConversionMatrix");
    public static final TEST_SELECT trackRates = new TEST_SELECT("trackRates");
    public static final TEST_SELECT convertTrig = new TEST_SELECT("convertTrig");
    public static final TEST_SELECT convertMatrix = new TEST_SELECT("convertMatrix");
    public static final TEST_SELECT coordHysteresis = new TEST_SELECT("coordHysteresis");
    public static final TEST_SELECT fieldRotation = new TEST_SELECT("fieldRotation");
    public static final TEST_SELECT altAltAzTrack = new TEST_SELECT("altAltAzTrack");
    public static final TEST_SELECT bestZ123FromAlteredPosition = new TEST_SELECT("bestZ123FromAlteredPosition");
    public static final TEST_SELECT setZ123RtnErrFromAnalysisFile = new TEST_SELECT("setZ123RtnErrFromAnalysisFile");
    public static final TEST_SELECT bestZ123FromAnalysisFile = new TEST_SELECT("bestZ123FromAnalysisFile");
    public static final TEST_SELECT z12Comparison = new TEST_SELECT("z12Comparison");
    public static final TEST_SELECT altOffset = new TEST_SELECT("altOffset");
    public static final TEST_SELECT altOffsetFromCfgOneTwoPos = new TEST_SELECT("altOffsetFromCfgOneTwoPos");
    public static final TEST_SELECT linkGuide = new TEST_SELECT("linkGuide");
    public static final TEST_SELECT guide = new TEST_SELECT("guide");
    public static final TEST_SELECT PEC = new TEST_SELECT("PEC");
    public static final TEST_SELECT axisToAxisEC = new TEST_SELECT("axisToAxisEC");
    public static final TEST_SELECT listAxisToAxisEC = new TEST_SELECT("listAxisToAxisEC");
    public static final TEST_SELECT PMC = new TEST_SELECT("PMC");
    public static final TEST_SELECT verifyLimitMotion = new TEST_SELECT("verifyLimitMotion");
    public static final TEST_SELECT limitWindow = new TEST_SELECT("limitWindow");
    public static final TEST_SELECT limitMotionBase = new TEST_SELECT("limitMotionBase");
    public static final TEST_SELECT limitMotionCollection = new TEST_SELECT("limitMotionCollection");
    public static final TEST_SELECT ioFile = new TEST_SELECT("ioFile");
    public static final TEST_SELECT serialPort = new TEST_SELECT("serialPort");
    public static final TEST_SELECT UDP = new TEST_SELECT("UDP");
    public static final TEST_SELECT TCPserver = new TEST_SELECT("TCPserver");
    public static final TEST_SELECT TCPclient = new TEST_SELECT("TCPclient");
    public static final TEST_SELECT TCP = new TEST_SELECT("TCP");
    public static final TEST_SELECT IOFactory = new TEST_SELECT("IOFactory");
    public static final TEST_SELECT serveHtml = new TEST_SELECT("serveHtml");
    public static final TEST_SELECT ioRelay = new TEST_SELECT("ioRelay");
    public static final TEST_SELECT verifyEncoders = new TEST_SELECT("verifyEncoders");
    public static final TEST_SELECT encoders = new TEST_SELECT("encoders");
    public static final TEST_SELECT setDate = new TEST_SELECT("SetDate");
    public static final TEST_SELECT servo = new TEST_SELECT("servo");
    public static final TEST_SELECT trajectory = new TEST_SELECT("trajectory");
    public static final TEST_SELECT PICServoSim = new TEST_SELECT("PICServoSim");
    public static final TEST_SELECT PICServoMotorsSimulator = new TEST_SELECT("PICServoMotorsSimulator");
    public static final TEST_SELECT handpad = new TEST_SELECT("handpad");
    public static final TEST_SELECT verifyHandpadDesigns = new TEST_SELECT("verifyHandpadDesigns");
    public static final TEST_SELECT verifyHandpadModes = new TEST_SELECT("verifyHandpadModes");
    public static final TEST_SELECT verifyMountTypes = new TEST_SELECT("verifyMountTypes");
    public static final TEST_SELECT verifyTrackStyle = new TEST_SELECT("verifyTrackStyle");
    public static final TEST_SELECT trackStyle = new TEST_SELECT("trackStyle");
    public static final TEST_SELECT LX200 = new TEST_SELECT("LX200");
    public static final TEST_SELECT dataFile = new TEST_SELECT("dataFile");
    public static final TEST_SELECT inputFileAvoidObject = new TEST_SELECT("inputFileAvoidObject");
    public static final TEST_SELECT cometFile = new TEST_SELECT("cometFile");
    public static final TEST_SELECT fileFilter = new TEST_SELECT("fileFilter");
    public static final TEST_SELECT findObjectInFileSet = new TEST_SELECT("findObjectInFileSet");
    public static final TEST_SELECT findAllObjectsInFileSet = new TEST_SELECT("findAllObjectsInFileSet");
    public static final TEST_SELECT ProjectPlutoGuide = new TEST_SELECT("ProjectPlutoGuide");
    public static final TEST_SELECT spiralSearch = new TEST_SELECT("spiralSearch");
    public static final TEST_SELECT parseTestCmdFile = new TEST_SELECT("parseTestCmdFile");
    public static final TEST_SELECT listAllCmdScope = new TEST_SELECT("listAllCmdScope");
    public static final TEST_SELECT verifyCmdScope = new TEST_SELECT("verifyCmdScope");
    public static final TEST_SELECT execStringCmdScope = new TEST_SELECT("execStringCmdScope");
    public static final TEST_SELECT cmdScopeInHtmlFormat = new TEST_SELECT("cmdScopeInHtmlFormat");
    public static final TEST_SELECT parmsInHtmlFormat = new TEST_SELECT("parmsInHtmlFormat");
    public static final TEST_SELECT filesUsedInHtmlFormat = new TEST_SELECT("filesUsedInHtmlFormat");
    public static final TEST_SELECT cmdLineArgsInHtmlFormat = new TEST_SELECT("cmdLineArgsInHtmlFormat");
    public static final TEST_SELECT testDataFileChooserLoadListPos = new TEST_SELECT("testDataFileChooserLoadListPos");
    public static final TEST_SELECT testCmdFileChooser = new TEST_SELECT("testCmdFileChooser");
    public static final TEST_SELECT testFindAllObjectsInDataDir = new TEST_SELECT("testFindAllObjectsInDataDir");
    public static final TEST_SELECT testDataFileListPositions = new TEST_SELECT("testDataFileListPositions");
    public static final TEST_SELECT precessDataFiles = new TEST_SELECT("precessDataFiles");
    public static final TEST_SELECT verifyJFrameStatusTextArea = new TEST_SELECT("verifyJFrameStatusTextArea");
    public static final TEST_SELECT temp = new TEST_SELECT("temp");
    public static final TEST_SELECT quit = new TEST_SELECT("quit");
}

