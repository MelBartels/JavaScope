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
 * common console utility functions
 */
public class console {
    static boolean stdOutOK;
    static boolean errOutOK;

    static byte b;
    static boolean O;
    static int i;
    static long l;
    static double d;
    static String s;

    static void stdOut(String s) {
        if (stdOutOK)
            System.out.print(s);
    }

    static void stdOutLn(String s) {
        if (stdOutOK)
            System.out.println(s);
    }

    static void stdOutChar(char c) {
        if (stdOutOK)
            System.out.print(c);
    }

    static void errOut(String s) {
        if (errOutOK)
            System.out.println("***ERROR: "
            + s
            + " ***");
    }

    static boolean getByte() {
        // wrap input with buffered reader to gain access to functions like readLine()
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        // StringTokenizer splits string using white space as tokens
        StringTokenizer st;

        try {
            String s = r.readLine();
            st = new StringTokenizer(s);
            if (st.countTokens() > 0)
                try {
                    // nextToken() ignores leading and trailing white space; parseInt() converts string to int
                    b = Byte.parseByte(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    return false;
                }
            else
                return false;
        }
        catch (IOException ioe) {
            //console.errOut(ioe);
            return false;
        }
    }

    static boolean getBoolean() {
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        try {
            String s = r.readLine();
            st = new StringTokenizer(s);
            if (st.countTokens() > 0)
                try {
                    O = new Boolean(st.nextToken()).booleanValue();
                    return true;
                }
                catch (NumberFormatException nfe) {
                    return false;
                }
            else
                return false;
        }
        catch (IOException ioe) {
            console.errOut(ioe.toString());
            return false;
        }
    }

    static boolean getInt() {
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        try {
            String s = r.readLine();
            st = new StringTokenizer(s);
            if (st.countTokens() > 0)
                try {
                    i = Integer.parseInt(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    return false;
                }
            else
                return false;
        }
        catch (IOException ioe) {
            console.errOut(ioe.toString());
            return false;
        }
    }

    static boolean getLong() {
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        try {
            String s = r.readLine();
            st = new StringTokenizer(s);
            if (st.countTokens() > 0)
                try {
                    l = Long.parseLong(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    return false;
                }
            else
                return false;
        }
        catch (IOException ioe) {
            console.errOut(ioe.toString());
            return false;
        }
    }

    static boolean getDouble() {
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        try {
            String s = r.readLine();
            st = new StringTokenizer(s);
            if (st.countTokens() > 0)
                try {
                    d = Double.parseDouble(st.nextToken());
                    return true;
                }
                catch (NumberFormatException nfe) {
                    return false;
                }
            else
                return false;
        }
        catch (IOException ioe) {
            console.errOut(ioe.toString());
            return false;
        }
    }

    static boolean getString() {
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));

        try {
            s = r.readLine();
            return true;
        }
        catch (IOException ioe) {
            console.errOut(ioe.toString());
            return false;
        }
    }

    /**
     * two basic coordinate file formats:
     *     raDeg decDeg altDeg azDeg sidTDeg, and
     *     RaHr RaMin RaSec decDeg DecMin DecSec;
     *
     * specific formats (object name optional):
     * 1. fReadLinePositionDeg(): raDeg decDeg altDeg azDeg sidTDeg objName
     * 2. fReadLinePositionDegWithAltazErrArcmin(): raDeg decDeg AltDeg AzDeg SidTDeg altErrArcmin azErrArcmin objName (errors optional)
     * 3. fReadLineEquatCoord(): RaHr RaMin RaSec decDeg DecMin DecSec objectName
     * 4. fReadLineEquatCoordWithDrift(): RaHr RaMin RaSec decDeg DecMin DecSec raDriftHr RaDriftMin RaDriftSec DecDriftDeg DecDriftMin DecDriftSec objectName
     */

    /**
     * reads in a position in degrees, ie, raDeg decDeg altDeg azDeg sidTDeg objName (name optional)
     */
    static boolean fReadLinePositionDeg(BufferedReader input, position p) {
        StringTokenizer st;
        String s;

        try {
            s = input.readLine();
            if (s == null)
                return false;
            else {
                st = new StringTokenizer(s);
                if (st.countTokens() < 5)
                    return false;
                else {
                    common.fReadPositionDeg(p, st);
                    p.objName(common.tokensToString(st));
                    if (cfg.getInstance().precessionNutationAberration)
                        p.applyPrecessionCorrection();
                    return true;
                }
            }
        }
        catch (IOException ioe) {
            return false;
        }
    }

    /**
     * reads in a position in degrees, ie, raDeg decDeg AltDeg AzDeg SidTDeg altErrArcmin azErrArcmin objName:
     * errors and name optional;
     */
    static boolean fReadLinePositionDegWithAltazErrArcmin(BufferedReader input, position p) {
        StringTokenizer st;
        String s;

        try {
            s = input.readLine();
            if (s == null)
                return false;
            else {
                st = new StringTokenizer(s);
                if (st.countTokens() < 5)
                    return false;
                else {
                    common.fReadPositionDegWithAltazErrArcmin(p, st);
                    p.objName(common.tokensToString(st));
                    if (cfg.getInstance().precessionNutationAberration)
                        p.applyPrecessionCorrection();
                    return true;
                }
            }
        }
        catch (IOException ioe) {
            return false;
        }
    }

    /**
     * reads in a position in equatorial coordinates including object name, ie, RaHr RaMin RaSec decDeg DecMin DecSec objectName (name optional)
     */
    static boolean fReadLineEquatCoord(BufferedReader input, position p) {
        StringTokenizer st;
        String s;

        try {
            s = input.readLine();
            if (s == null)
                return false;
            else {
                st = new StringTokenizer(s);
                if (st.countTokens() < 6)
                    return false;
                else {
                    common.fReadEquatCoord(p, st);
                    p.objName(common.tokensToString(st));
                    if (cfg.getInstance().precessionNutationAberration)
                        p.applyPrecessionCorrection();
                    return true;
                }
            }
        }
        catch (IOException ioe) {
            return false;
        }
    }

    /**
     * reads in a position in equatorial coordinates and reads in drift in equatorial coordinates including name, ie,
     * RaHr RaMin RaSec decDeg DecMin DecSec raDriftHr RaDriftMin RaDriftSec DecDriftDeg DecDriftMin DecDriftSec objectName
     */
    static boolean fReadLineEquatCoordWithDrift(BufferedReader input, position p) {
        StringTokenizer st;
        String s;

        try {
            s = input.readLine();
            if (s == null)
                return false;
            else {
                st = new StringTokenizer(s);
                if (st.countTokens() < 12)
                    return false;
                else {
                    common.fReadEquatCoordWithDrift(p, st);
                    p.objName(common.tokensToString(st));
                    if (cfg.getInstance().precessionNutationAberration)
                        p.applyPrecessionCorrection();
                    return true;
                }
            }
        }
        catch (IOException ioe) {
            return false;
        }
    }
}

