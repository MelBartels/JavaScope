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

public final class AXIS {
    private String id;
    public final int KEY;
    private AXIS prev;
    private AXIS next;

    private static int itemCount;
    private static AXIS first;
    private static AXIS last;

    private AXIS(String id) {
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
            private AXIS current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                AXIS c = current;
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

    public static AXIS first() {
        return first;
    }

    public static AXIS last() {
        return last;
    }

    public AXIS prev() {
        return this.prev;
    }

    public AXIS next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of AXIS, which has "
        + itemCount
        + " elements:");
        AXIS current = first;
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

        AXIS current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static AXIS matchKey(int i) {
        AXIS O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static AXIS matchStr(String s) {
        AXIS O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final AXIS altDec = new AXIS("AltitudeDeclination");
    public static final AXIS azHA = new AXIS("AzimuthHourAngle");
    public static final AXIS thirdAxis = new AXIS("thirdAxis");
}

