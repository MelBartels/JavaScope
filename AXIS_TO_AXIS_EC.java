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

public final class AXIS_TO_AXIS_EC {
    private String id;
    public final int KEY;
    private AXIS_TO_AXIS_EC prev;
    private AXIS_TO_AXIS_EC next;

    private static int itemCount;
    private static AXIS_TO_AXIS_EC first;
    private static AXIS_TO_AXIS_EC last;

    private AXIS_TO_AXIS_EC(String id) {
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
            private AXIS_TO_AXIS_EC current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                AXIS_TO_AXIS_EC c = current;
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

    public static AXIS_TO_AXIS_EC first() {
        return first;
    }

    public static AXIS_TO_AXIS_EC last() {
        return last;
    }

    public AXIS_TO_AXIS_EC prev() {
        return this.prev;
    }

    public AXIS_TO_AXIS_EC next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of AXIS_TO_AXIS_EC, which has "
        + itemCount
        + " elements:");
        AXIS_TO_AXIS_EC current = first;
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

        AXIS_TO_AXIS_EC current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static AXIS_TO_AXIS_EC matchKey(int i) {
        AXIS_TO_AXIS_EC O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static AXIS_TO_AXIS_EC matchStr(String s) {
        AXIS_TO_AXIS_EC O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final AXIS_TO_AXIS_EC altAz = new AXIS_TO_AXIS_EC("AltitudeToAzimuth");
    public static final AXIS_TO_AXIS_EC altAlt = new AXIS_TO_AXIS_EC("AltitudeToAltitude");
    public static final AXIS_TO_AXIS_EC azAz = new AXIS_TO_AXIS_EC("AzimuthToAzimuth");
}

