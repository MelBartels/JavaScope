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

public final class LIMIT_MOTION_TYPE {
    private String id;
    public final int KEY;
    private LIMIT_MOTION_TYPE prev;
    private LIMIT_MOTION_TYPE next;

    private static int itemCount;
    private static LIMIT_MOTION_TYPE first;
    private static LIMIT_MOTION_TYPE last;

    private LIMIT_MOTION_TYPE(String id) {
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
            private LIMIT_MOTION_TYPE current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                LIMIT_MOTION_TYPE c = current;
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

    public static LIMIT_MOTION_TYPE first() {
        return first;
    }

    public static LIMIT_MOTION_TYPE last() {
        return last;
    }

    public LIMIT_MOTION_TYPE prev() {
        return this.prev;
    }

    public LIMIT_MOTION_TYPE next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of LIMIT_MOTION_TYPE, which has "
        + itemCount
        + " elements:");
        LIMIT_MOTION_TYPE current = first;
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

        LIMIT_MOTION_TYPE current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static LIMIT_MOTION_TYPE matchKey(int i) {
        LIMIT_MOTION_TYPE O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static LIMIT_MOTION_TYPE matchStr(String s) {
        LIMIT_MOTION_TYPE O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final LIMIT_MOTION_TYPE limit_motion_siteAltaz = new LIMIT_MOTION_TYPE("limit_motion_siteAltaz");
    public static final LIMIT_MOTION_TYPE limit_motion_equat = new LIMIT_MOTION_TYPE("limit_motion_equat");
}

