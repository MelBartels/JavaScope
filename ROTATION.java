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

public final class ROTATION {
    private String id;
    public final int KEY;
    private ROTATION prev;
    private ROTATION next;

    private static int itemCount;
    private static ROTATION first;
    private static ROTATION last;

    private ROTATION(String id) {
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
            private ROTATION current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                ROTATION c = current;
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

    public static ROTATION first() {
        return first;
    }

    public static ROTATION last() {
        return last;
    }

    public ROTATION prev() {
        return this.prev;
    }

    public ROTATION next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of ROTATION, which has "
        + itemCount
        + " elements:");
        ROTATION current = first;
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

        ROTATION current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static ROTATION matchKey(int i) {
        ROTATION O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static ROTATION matchStr(String s) {
        ROTATION O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final ROTATION no = new ROTATION("no");
    public static final ROTATION CW = new ROTATION("CW");
    public static final ROTATION CCW = new ROTATION("CCW");
    public static final ROTATION biDir = new ROTATION("biDir");
}

