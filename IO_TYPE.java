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

public final class IO_TYPE {
    private String id;
    public final int KEY;
    private IO_TYPE prev;
    private IO_TYPE next;

    private static int itemCount;
    private static IO_TYPE first;
    private static IO_TYPE last;

    private IO_TYPE(String id) {
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
            private IO_TYPE current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                IO_TYPE c = current;
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

    public static IO_TYPE first() {
        return first;
    }

    public static IO_TYPE last() {
        return last;
    }

    public IO_TYPE prev() {
        return this.prev;
    }

    public IO_TYPE next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of IO_TYPE, which has "
        + itemCount
        + " elements:");
        IO_TYPE current = first;
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

        IO_TYPE current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static IO_TYPE matchKey(int i) {
        IO_TYPE O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static IO_TYPE matchStr(String s) {
        IO_TYPE O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final IO_TYPE ioNone = new IO_TYPE("ioNone");
    public static final IO_TYPE ioFile = new IO_TYPE("ioFile");
    public static final IO_TYPE ioSerial = new IO_TYPE("ioSerial");
    public static final IO_TYPE ioUDP = new IO_TYPE("ioUDP");
    public static final IO_TYPE ioTCPserver = new IO_TYPE("ioTCPserver");
    public static final IO_TYPE ioTCPclient = new IO_TYPE("ioTCPclient");
    public static final IO_TYPE ioTCP = new IO_TYPE("ioTCP");
}

