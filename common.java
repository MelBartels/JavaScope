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
 * common utlity routines
 */
public class common {
    private static boolean minusSignDetected;

    static void badExit(String BeString) {
        System.out.println(BeString);
        System.exit(-1);
    }

    static void beep(int numBeeps, int milliSecWait) {
        int ix;

        for (ix = 0; ix < numBeeps; ix++) {
            System.out.print("\007");
            if (milliSecWait > 0)
                threadSleep(milliSecWait);
        }
    }

    static void testBeep() {
        int numBeeps;
        int milliSecWait;

        System.out.println("beep test");
        System.out.print("enter number of beeps ");
        console.getInt();
        numBeeps = console.i;
        System.out.print("enter millisecond wait between beeps ");
        console.getInt();
        milliSecWait = console.i;
        beep(numBeeps, milliSecWait);
        System.out.println("end of beep test");
    }

    // thread put to sleep for a time (will not run, even if processor available), then returned to runnable state

    static void threadSleep(long MilliSeconds) {
        try {
            Thread.sleep(MilliSeconds);
        }
        catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * for more on Runtime, see http://www.mountainstorm.com/publications/javazine.html
     * and http://developer.java.sun.com/developer/qow/archive/68/
     * and http://mindprod.com/jglossexec.html
     * for Windows OS, create a bat file then execute the file
     * for XP, can skip the bat file and exec("cmd.exe /c date " + s)
     */
    static boolean writeFileExecFile(String filename, String contents, boolean wait) {
        PrintStream output;
        Process p = null;

        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(filename)));
            output.println(contents);
            output.close();
        }
        catch (IOException ioe) {
            console.errOut("could not open " + filename);
            return false;
        }

        try {
            p = Runtime.getRuntime().exec(filename);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        if (wait)
            try {
                p.waitFor();
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        return true;
    }

    static void fReadPositionDeg(position p, StringTokenizer st) {
        boolean yrDetected = false;
        boolean JDdetected = false;
        double yrJD;
        String s;

        // grab first token
        if (st.countTokens() > 0) {
            p.resetEpochs();
            s = st.nextToken();
            yrJD = 0.;
            try {
                yrJD = Double.parseDouble(s);
            }
            catch (NumberFormatException nfe) {
                console.errOut("bad initial number token in fReadPositionDeg(): " + s);
            }
            // if value indicates a year or Julian Date,
            // then 1) read next token and set ra rad, 2) decipher if year or JD,
            // else set ra rad to already read in token;
            if (yrJD > 999) {
                if (st.countTokens() > 0)
                    p.ra.rad = Double.parseDouble(st.nextToken()) * units.DEG_TO_RAD;
                if (yrJD > 99999)
                    p.originalEpochJD = yrJD;
                else
                    p.originalEpochJD = astroTime.getInstance().calcJDFromYear(yrJD);
            }
            else
                p.ra.rad = Double.parseDouble(s) * units.DEG_TO_RAD;
        }
        // nextToken() ignores leading and trailing white space; x.parseX() converts string to type x
        p.dec.rad = Double.parseDouble(st.nextToken()) * units.DEG_TO_RAD;
        p.alt.rad = Double.parseDouble(st.nextToken()) * units.DEG_TO_RAD;
        p.az.rad = Double.parseDouble(st.nextToken()) * units.DEG_TO_RAD;
        p.sidT.rad = Double.parseDouble(st.nextToken()) * units.DEG_TO_RAD;
        p.init = true;
    }

    static void fReadPositionDegWithAltazErrArcmin(position p, StringTokenizer st) {
        String s;

        fReadPositionDeg(p, st);

        if (st.countTokens() >= 2) {
            p.azErr.a = Double.parseDouble(st.nextToken()) * units.ARCMIN_TO_RAD;
            p.azErr.z = Double.parseDouble(st.nextToken()) * units.ARCMIN_TO_RAD;
        }
    }

    static boolean isNumericChar(byte b) {
        if (b == '0'
        || b == '1'
        || b == '2'
        || b == '3'
        || b == '4'
        || b == '5'
        || b == '6'
        || b == '7'
        || b == '8'
        || b == '9')
            return true;
        else
            return false;
    }

    /**
     * detect coordinates -00 45 00 as -00:45:00 by noting any leading minus sign
     * need to strip any leading '+' because Integer.parseInt() doesn't like leading '+'
     */
    static int getIntDetectMinusSign(String s) {
        if (s.charAt(0) == '-')
            minusSignDetected = true;
        else if (s.charAt(0) == '+')
            //or s = s.replace('+', '0');
            s = s.substring(1, s.length());
        return Integer.parseInt(s);
    }

    static double getDoubleDetectMinusSign(String s) {
        if (s.charAt(0) == '-')
            minusSignDetected = true;
        else if (s.charAt(0) == '+')
            //or s = s.replace('+', '0');
            s = s.substring(1, s.length());
        // check for coordinates in form of 12 45 .7 which translates to 12:45:42
        else if (s.charAt(0) == '.')
            return (Double.parseDouble(s) * 6.);
        return Double.parseDouble(s);
    }

    static String tokensToString(StringTokenizer st) {
        String s = "";

        while (st.countTokens() > 0)
            s += st.nextToken() + " ";

        return s.trim();
    }

    static void fReadEquatCoord(position p, StringTokenizer st) {
        boolean yrDetected = false;
        boolean JDdetected = false;
        double yrJD;
        String s;
        double sec;

        minusSignDetected = false;
        // grab first token
        if (st.countTokens() > 0) {
            p.resetEpochs();
            s = st.nextToken();
            yrJD = 0.;
            try {
                yrJD = Double.parseDouble(s);
            }
            catch (NumberFormatException nfe) {
                console.errOut("bad initial number token in fReadEquatCoord(): " + s);
            }
            // if value indicates a year or Julian Date,
            //     then 1) read next token and set ra hr, 2) decipher if year or JD and convert,
            // else, set ra hr to already read in token;
            if (yrJD > 999) {
                if (st.countTokens() > 0)
                    p.ra.hr = getIntDetectMinusSign(st.nextToken());
                if (yrJD > 99999)
                    p.originalEpochJD = yrJD;
                else
                    p.originalEpochJD = astroTime.getInstance().calcJDFromYear(yrJD);
            }
            else
                p.ra.hr = getIntDetectMinusSign(s);
        }
        if (st.countTokens() > 0)
            p.ra.min = getIntDetectMinusSign(st.nextToken());
        if (st.countTokens() > 0)
            sec = getDoubleDetectMinusSign(st.nextToken());
        else
            sec = 0.;
        p.ra.sec = (int) sec;
        p.ra.milliSec = (int) ((sec - (double) p.ra.sec) * 1000);
        p.ra.calcRad();
        if (minusSignDetected)
            p.ra.rad = -Math.abs(p.ra.rad);

        minusSignDetected = false;
        if (st.countTokens() > 0)
            p.dec.deg = getIntDetectMinusSign(st.nextToken());
        if (st.countTokens() > 0)
            p.dec.min = getIntDetectMinusSign(st.nextToken());
        if (st.countTokens() > 0)
            p.dec.sec = getIntDetectMinusSign(st.nextToken());
        p.dec.sign = units.PLUS;
        p.dec.calcRad();
        if (minusSignDetected)
            p.dec.rad = -Math.abs(p.dec.rad);
    }

    static void fReadEquatCoordWithDrift(position p, StringTokenizer st) {
        // read equat coord first
        fReadEquatCoord(p, st);

        // now read drift coord
        minusSignDetected = false;
        if (st.countTokens() > 0)
            p.raDriftHr.hr = getIntDetectMinusSign(st.nextToken());
        if (st.countTokens() > 0)
            p.raDriftHr.min = getIntDetectMinusSign(st.nextToken());
        if (st.countTokens() > 0)
            p.raDriftHr.sec = getIntDetectMinusSign(st.nextToken());
        p.raDriftHr.calcRad();
        if (minusSignDetected)
            p.raDriftHr.rad = -Math.abs(p.raDriftHr.rad);

        minusSignDetected = false;
        if (st.countTokens() > 0)
            p.decDriftHr.deg = getIntDetectMinusSign(st.nextToken());
        if (st.countTokens() > 0)
            p.decDriftHr.min = getIntDetectMinusSign(st.nextToken());
        if (st.countTokens() > 0)
            p.decDriftHr.sec = getIntDetectMinusSign(st.nextToken());
        p.decDriftHr.sign = units.PLUS;
        p.decDriftHr.calcRad();
        if (minusSignDetected)
            p.decDriftHr.rad = -Math.abs(p.decDriftHr.rad);
    }

    static void fReadEquatAltazCoord(position p, StringTokenizer st) {
        String s;

        // read equat coord first
        fReadEquatCoord(p, st);

        // now read altaz
        if (st.countTokens() > 0) {
            s = st.nextToken();
            try {
                p.alt.rad = Double.parseDouble(s) * units.DEG_TO_RAD;
            }
            catch (NumberFormatException nfe) {
                console.errOut("could not parse "
                + s
                + " in fReadEquatAltazCoord()");
            }
        }
        if (st.countTokens() > 0) {
            s = st.nextToken();
            try {
                p.az.rad = Double.parseDouble(s) * units.DEG_TO_RAD;
            }
            catch (NumberFormatException nfe) {
                console.errOut("could not parse "
                + s
                + " in fReadEquatAltazCoord()");
            }
        }
    }
}

