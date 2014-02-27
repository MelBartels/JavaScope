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

public final class HANDPAD_MODE {
    private String id;
    public final int KEY;
    private HANDPAD_MODE prev;
    private HANDPAD_MODE next;

    private static int itemCount;
    private static HANDPAD_MODE first;
    private static HANDPAD_MODE last;

    private HANDPAD_MODE(String id) {
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
            private HANDPAD_MODE current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                HANDPAD_MODE c = current;
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

    public static HANDPAD_MODE first() {
        return first;
    }

    public static HANDPAD_MODE last() {
        return last;
    }

    public HANDPAD_MODE prev() {
        return this.prev;
    }

    public HANDPAD_MODE next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of HANDPAD_MODE, which has "
        + itemCount
        + " elements:");
        HANDPAD_MODE current = first;
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

        HANDPAD_MODE current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static HANDPAD_MODE matchKey(int i) {
        HANDPAD_MODE O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static HANDPAD_MODE matchStr(String s) {
        HANDPAD_MODE O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final HANDPAD_MODE handpadModeOff = new HANDPAD_MODE("handpadModeOff");
    public static final HANDPAD_MODE handpadModeAutoInit12 = new HANDPAD_MODE("handpadModeAutoInit12");
    public static final HANDPAD_MODE handpadModeAutoInit123 = new HANDPAD_MODE("handpadModeAutoInit123");
    public static final HANDPAD_MODE handpadModeInit1 = new HANDPAD_MODE("handpadModeInit1");
    public static final HANDPAD_MODE handpadModeInit2 = new HANDPAD_MODE("handpadModeInit2");
    public static final HANDPAD_MODE handpadModeInit3 = new HANDPAD_MODE("handpadModeInit3");
    public static final HANDPAD_MODE handpadModePolarAlign = new HANDPAD_MODE("handpadModePolarAlign");
    public static final HANDPAD_MODE handpadModeAnalyze = new HANDPAD_MODE("handpadModeAnalyze");
    public static final HANDPAD_MODE handpadModePECSynch = new HANDPAD_MODE("handpadModePECSynch");
    public static final HANDPAD_MODE handpadModeGuide = new HANDPAD_MODE("handpadModeGuide");
    public static final HANDPAD_MODE handpadModeGuideStay = new HANDPAD_MODE("handpadModeGuideStay");
    public static final HANDPAD_MODE handpadModeGuideStayRotate = new HANDPAD_MODE("handpadModeGuideStayRotate");
    public static final HANDPAD_MODE handpadModeGuideStayDrag = new HANDPAD_MODE("handpadModeGuideStayDrag");
    public static final HANDPAD_MODE handpadModeGuideSiTech = new HANDPAD_MODE("handpadModeGuideSiTech");
    public static final HANDPAD_MODE handpadModeGrandTour = new HANDPAD_MODE("handpadModeGrandTour");
    public static final HANDPAD_MODE handpadModeScrollTour = new HANDPAD_MODE("handpadModeScrollTour");
    public static final HANDPAD_MODE handpadModeScrollTourAuto = new HANDPAD_MODE("handpadModeScrollTourAuto");
    public static final HANDPAD_MODE handpadModeRecordEquat = new HANDPAD_MODE("handpadModeRecordEquat");
    public static final HANDPAD_MODE handpadModeRecordAltaz = new HANDPAD_MODE("handpadModeRecordAltaz");
    public static final HANDPAD_MODE handpadModeToggleTrack = new HANDPAD_MODE("handpadModeToggleTrack");
    public static final HANDPAD_MODE handpadModeFRFocus = new HANDPAD_MODE("handpadModeFRFocus");
    public static final HANDPAD_MODE handpadModeSpiralSearch = new HANDPAD_MODE("handpadModeSpiralSearch");
}

