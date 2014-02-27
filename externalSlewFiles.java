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
 * methods to communicate with programs that recognize external control files such as Project Pluto Guide
 */
public class externalSlewFiles {
    static final int UPDATE_TIME_SEC = 1;

    static BufferedReader input;
    static PrintStream output;
    static String slewFile;
    static String slewOutFile;
    static String GuideStartupMarFile;
    static String ScopeStartupMarFile;

    long lastModified;
    long prevLastModified;
    File equatSlewDatFile;

    externalSlewFiles() {
        slewFile = cfg.getInstance().ProjectPlutoGuidePath + eString.SLEW_FILE;
        slewOutFile = cfg.getInstance().ProjectPlutoGuidePath + eString.SLEW_OUT_FILE;
        GuideStartupMarFile = cfg.getInstance().ProjectPlutoGuidePath + eString.GUIDE_STARTUP_MAR_FILE;
        ScopeStartupMarFile = cfg.getInstance().ProjectPlutoGuidePath + eString.SCOPE_STARTUP_MAR_FILE;

        equatSlewDatFile = new File(slewFile);
        newEquatSlewDat();
    }

    /**
     * read (rb) slew.dat where:
     * R <ra degrees>
     * D <dec degrees>
     * p <alt degrees>
     * q <az degrees>
     * ...
     * t <time()> <JD>
     * L <lat> <long>
     */
    void inputEquatSlewDat(position p) {
        String s;
        String recordType;
        StringTokenizer st;
        double deg;

        try {
            input = new BufferedReader(new FileReader(slewFile));
            s = input.readLine();
            while (s != null) {
                st = new StringTokenizer(s);
                if (st.countTokens() >= 2) {
                    recordType = st.nextToken();
                    if (recordType.equals("R")) {
                        deg = Double.parseDouble(st.nextToken());
                        p.ra.rad = deg * units.DEG_TO_RAD;
                        p.ra.getHMSM();
                        p.ra.d.rad = p.ra.rad;
                    }
                    if (recordType.equals("D")) {
                        deg = Double.parseDouble(st.nextToken());
                        p.dec.rad = deg * units.DEG_TO_RAD;
                        p.dec.getDMS();
                        p.dec.d.rad = p.dec.rad;
                    }
                }
                s = input.readLine();
            }
            input.close();
            // Guide outputs J2000 mean coordinates
            if (cfg.getInstance().precessionNutationAberration)
                p.applyPrecessionCorrectionFromEpochYear(cfg.getInstance().dataFileCoordYear);
        }
        catch (IOException ioe) {
            console.errOut("could not open " + slewFile);
        }
    }

    boolean newEquatSlewDat() {
        if (equatSlewDatFile.exists() && equatSlewDatFile.isFile() && !equatSlewDatFile.isDirectory()) {
            lastModified = equatSlewDatFile.lastModified();
            if (lastModified > prevLastModified) {
                prevLastModified = lastModified;
                return true;
            }
        }
        return false;
    }

    void writeEquatSlewOutFile(position p) {
        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(slewOutFile)));
            output.println("r "
            + p.ra.rad*units.RAD_TO_DEG
            + " "
            + p.dec.rad*units.RAD_TO_DEG);
            output.close();
        }
        catch (IOException ioe) {
            console.errOut("could not open " + slewOutFile);
            cfg.getInstance().readWriteExternalSlewFiles = false;
        }
    }

    void writeAltazSlewOutFile(position p) {
        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(slewOutFile)));
            output.println(p.alt.rad*units.RAD_TO_DEG
            + " "
            + p.az.rad*units.RAD_TO_DEG);
            output.close();
        }
        catch (IOException ioe) {
            console.errOut("could not open " + slewOutFile);
            cfg.getInstance().readWriteExternalSlewFiles = false;
        }
    }

    void ProjectPlutoGuide(position in, position Out) {
        String s = "";

        if (writeGuideStartup(in)) {
            s += "cd "
            + cfg.getInstance().ProjectPlutoGuidePath
            + "\n"
            + "@echo off\n"
            + "if not exist "
            + eString.SCOPE_STARTUP_MAR_FILE
            + " goto err_msg\n"
            + "copy "
            + eString.GUIDE_STARTUP_MAR_FILE
            + " startup.old\n"
            + "del "
            + eString.GUIDE_STARTUP_MAR_FILE
            + "\n"
            + "copy "
            + eString.SCOPE_STARTUP_MAR_FILE
            + " "
            + eString.GUIDE_STARTUP_MAR_FILE
            + "\n"
            + "goto start_guide\n"
            + ":err_msg\n"
            + "echo WARNING: Scope.mar wasn't found!\n"
            + "echo Project Pluto guide will not start at the correct coordinates.\n"
            + "pause\n"
            + ":start_guide\n"
            + cfg.getInstance().ProjectPlutoGuideExec;

            common.writeFileExecFile(eString.GUIDE_BAT_FILENAME, s, true);

            readGuideStartup(Out);
        }
    }

    boolean writeGuideStartup(position p) {
        String s;
        StringTokenizer st;
        String firstToken;
        String secondToken;

        try {
            input = new BufferedReader(new FileReader(GuideStartupMarFile));
        }
        catch (IOException ioe) {
            console.errOut("could not open " + GuideStartupMarFile);
            return false;
        }
        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(ScopeStartupMarFile)));
        }
        catch (IOException ioe) {
            console.errOut("could not open " + ScopeStartupMarFile);
        }
        if (input != null && output != null) {
            try {
                s = input.readLine();
                while (s != null) {
                    st = new StringTokenizer(s);
                    firstToken = st.nextToken();
                    secondToken = st.nextToken();
                    if (secondToken.equalsIgnoreCase("ra"))
                        output.println(firstToken
                        + "  ra       "
                        + (360. - p.ra.rad*units.RAD_TO_DEG));
                    else
                        if (secondToken.equalsIgnoreCase("dec"))
                            output.println(firstToken
                            + "  dec      "
                            + (p.dec.rad*units.RAD_TO_DEG));
                        else
                            output.println(s);
                    s = input.readLine();
                }
                input.close();
                output.close();
                return true;
            }
            catch (IOException ioe) {
                console.errOut("IOException in writeGuideStartup()");
                return false;
            }
        }
        if (input != null)
            try {
                input.close();
            }
            catch (IOException ioe) {
                console.errOut("could not close input in writeInputDataHistFile()");
                return false;
            }
        if (output != null)
            output.close();
        return false;
    }

    boolean readGuideStartup(position p) {
        String s;
        StringTokenizer st;
        String firstToken;
        String secondToken;
        double raDeg = 0.;
        double decDeg = 0.;
        boolean readRa = false;
        boolean readDec = false;

        try {
            input = new BufferedReader(new FileReader(GuideStartupMarFile));
            s = input.readLine();
            while (s != null && (!readRa || !readDec)) {
                st = new StringTokenizer(s);
                // ie, 1  ra       311.658032
                // and 2  dec      40.113879
                if (st.countTokens() >= 3) {
                    firstToken = st.nextToken();
                    secondToken = st.nextToken();
                    if (secondToken.equalsIgnoreCase("ra")) {
                        raDeg = Double.parseDouble(st.nextToken());
                        readRa = true;
                    }
                    if (secondToken.equalsIgnoreCase("dec")) {
                        decDeg = Double.parseDouble(st.nextToken());
                        readDec = true;
                    }
                }
                s = input.readLine();
            }
            input.close();
            if (readRa && readDec) {
                p.ra.rad = (360. - raDeg) * units.DEG_TO_RAD;
                p.ra.getHMSM();
                p.ra.d.rad = p.ra.rad;
                p.dec.rad = decDeg * units.DEG_TO_RAD;
                p.dec.getDMS();
                p.dec.d.rad = p.dec.rad;
                writeOutGuideFile(p);
                return true;
            }
            else
                return false;
        }
        catch (IOException ioe) {
            console.errOut("could not open " + GuideStartupMarFile);
            return false;
        }
    }

    void writeOutGuideFile(position p) {
        try {
            // append
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(eString.OUT_GUIDE_FILE, true)));
            output.println(p.ra.getStringHMS(eString.SPACE)
            + "   "
            + p.dec.getStringDMS(eString.SPACE)
            + "   from_guide");
            output.close();
        }
        catch (IOException ioe) {
            console.errOut("could not open "
            + eString.OUT_GUIDE_FILE
            + " for append");
        }
    }

    void testProjectPlutoGuide() {
        position in = new position("in");
        position Out = new position("out");

        System.out.println("test of testProjectPlutoGuide routines");

        in.ra.rad = 30.*units.DEG_TO_RAD;
        in.dec.rad = -10.*units.DEG_TO_RAD;

        System.out.println("starting coordinates are (360-)raDeg "
        + (360. - in.ra.rad*units.RAD_TO_DEG)
        + " "
        + in.ra.getStringHMSM()
        + " and decDeg "
        + in.dec.rad*units.RAD_TO_DEG
        + " "
        + in.dec.getStringDMS());

        ProjectPlutoGuide(in, Out);

        System.out.println("return coordinates are (360-)raDeg "
        + (360. - Out.ra.rad*units.RAD_TO_DEG)
        + " "
        + Out.ra.getStringHMSM()
        + " and decDeg "
        + Out.dec.rad*units.RAD_TO_DEG
        + " "
        + Out.dec.getStringDMS());
    }
}

