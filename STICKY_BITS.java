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

public final class STICKY_BITS {
    private String id;
    public final int KEY;
    private STICKY_BITS prev;
    private STICKY_BITS next;

    private static int itemCount;
    private static STICKY_BITS first;
    private static STICKY_BITS last;

    private STICKY_BITS(String id) {
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
            private STICKY_BITS current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                STICKY_BITS c = current;
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

    public static STICKY_BITS first() {
        return first;
    }

    public static STICKY_BITS last() {
        return last;
    }

    public STICKY_BITS prev() {
        return this.prev;
    }

    public STICKY_BITS next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of STICKY_BITS, which has "
        + itemCount
        + " elements:");
        STICKY_BITS current = first;
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

        STICKY_BITS current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static STICKY_BITS matchKey(int i) {
        STICKY_BITS O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static STICKY_BITS matchStr(String s) {
        STICKY_BITS O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final STICKY_BITS overCurrent = new STICKY_BITS("OverCurrentError");
    public static final STICKY_BITS posError = new STICKY_BITS("PositionError");
    public static final STICKY_BITS posWrap = new STICKY_BITS("PositionWrap");
    public static final STICKY_BITS timerOverrun = new STICKY_BITS("timerOverrun");
}

