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

public final class CONVERT_SUBR_SELECT {
    private String id;
    public final int KEY;
    private CONVERT_SUBR_SELECT prev;
    private CONVERT_SUBR_SELECT next;

    private static int itemCount;
    private static CONVERT_SUBR_SELECT first;
    private static CONVERT_SUBR_SELECT last;

    private CONVERT_SUBR_SELECT(String id) {
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
            private CONVERT_SUBR_SELECT current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                CONVERT_SUBR_SELECT c = current;
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

    public static CONVERT_SUBR_SELECT first() {
        return first;
    }

    public static CONVERT_SUBR_SELECT last() {
        return last;
    }

    public CONVERT_SUBR_SELECT prev() {
        return this.prev;
    }

    public CONVERT_SUBR_SELECT next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of CONVERT_SUBR_SELECT, which has "
        + itemCount
        + " elements:");
        CONVERT_SUBR_SELECT current = first;
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

        CONVERT_SUBR_SELECT current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static CONVERT_SUBR_SELECT matchKey(int i) {
        CONVERT_SUBR_SELECT O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static CONVERT_SUBR_SELECT matchStr(String s) {
        CONVERT_SUBR_SELECT O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final CONVERT_SUBR_SELECT TakiSimple = new CONVERT_SUBR_SELECT("TakiSimple");
    public static final CONVERT_SUBR_SELECT TakiSmallAngle = new CONVERT_SUBR_SELECT("TakiSmallAngle");
    public static final CONVERT_SUBR_SELECT BellIterative = new CONVERT_SUBR_SELECT("BellIterative");
    public static final CONVERT_SUBR_SELECT TakiIterative = new CONVERT_SUBR_SELECT("TakiIterative");
    public static final CONVERT_SUBR_SELECT BellTaki = new CONVERT_SUBR_SELECT("BellTaki");
}

