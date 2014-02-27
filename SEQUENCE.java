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

public final class SEQUENCE {
    private String id;
    public final int KEY;
    private SEQUENCE prev;
    private SEQUENCE next;

    private static int itemCount;
    private static SEQUENCE first;
    private static SEQUENCE last;

    private SEQUENCE(String id) {
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
            private SEQUENCE current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                SEQUENCE c = current;
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

    public static SEQUENCE first() {
        return first;
    }

    public static SEQUENCE last() {
        return last;
    }

    public SEQUENCE prev() {
        return this.prev;
    }

    public SEQUENCE next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of SEQUENCE, which has "
        + itemCount
        + " elements:");
        SEQUENCE current = first;
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

        SEQUENCE current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static SEQUENCE matchKey(int i) {
        SEQUENCE O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static SEQUENCE matchStr(String s) {
        SEQUENCE O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final SEQUENCE beg = new SEQUENCE("beg");
    public static final SEQUENCE end = new SEQUENCE("end");
}

