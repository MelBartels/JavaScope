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

public final class STATUS_TYPE {
    private String id;
    public final String description;
    public final int KEY;
    private STATUS_TYPE prev;
    private STATUS_TYPE next;

    private static int itemCount;
    private static STATUS_TYPE first;
    private static STATUS_TYPE last;

    private STATUS_TYPE(String id) {
        this(id, "");
    }

    private STATUS_TYPE(String id, String description) {
        this.id = new String(id);
        this.description = description;
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
            private STATUS_TYPE current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                STATUS_TYPE c = current;
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

    public static STATUS_TYPE first() {
        return first;
    }

    public static STATUS_TYPE last() {
        return last;
    }

    public STATUS_TYPE prev() {
        return this.prev;
    }

    public STATUS_TYPE next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of STATUS_TYPE, which has "
        + itemCount
        + " elements:");
        STATUS_TYPE current = first;
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

        STATUS_TYPE current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static STATUS_TYPE matchKey(int i) {
        STATUS_TYPE O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static STATUS_TYPE matchStr(String s) {
        STATUS_TYPE O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static STATUS_TYPE matchDesc(String s) {
        STATUS_TYPE O = first();
        while (O != null && !s.equalsIgnoreCase(O.description))
            O = O.next();
        return O;
    }

    public static final STATUS_TYPE StatusTypeCoordinates = new STATUS_TYPE("StatusTypeCoordinates",
    "status of time/date, all coordinates including encoders");
    public static final STATUS_TYPE StatusTypeInits = new STATUS_TYPE("StatusTypeInits",
    "status of initializations");
    public static final STATUS_TYPE StatusTypeLX200 = new STATUS_TYPE("StatusTypeLX200",
    "status of LX200 commands");
    public static final STATUS_TYPE StatusTypeCmdHistory = new STATUS_TYPE("StatusTypeCmdHistory",
    "command history from all command sources");
    public static final STATUS_TYPE StatusTypeServoComm = new STATUS_TYPE("StatusTypeServoComm",
    "status of servo controller communications");
    public static final STATUS_TYPE StatusTypeServoAltDec = new STATUS_TYPE("StatusTypeServoAltDec",
    "status of altitude/declination motor");
    public static final STATUS_TYPE StatusTypeServoAzRa = new STATUS_TYPE("StatusTypeServoAzRa",
    "status of azimuth/Right Ascension motor");
    public static final STATUS_TYPE StatusTypeServoFieldR = new STATUS_TYPE("StatusTypeServoFieldR",
    "status of field rotation or third axis motor");
    public static final STATUS_TYPE StatusTypeServoFocus = new STATUS_TYPE("StatusTypeServoFocus",
    "status of focus motor");
}

