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

public final class IO_ARG {
    private String id;
    public final int KEY;
    private IO_ARG prev;
    private IO_ARG next;

    private static int itemCount;
    private static IO_ARG first;
    private static IO_ARG last;

    private IO_ARG(String id) {
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
            private IO_ARG current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                IO_ARG c = current;
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

    public static IO_ARG first() {
        return first;
    }

    public static IO_ARG last() {
        return last;
    }

    public IO_ARG prev() {
        return this.prev;
    }

    public IO_ARG next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of IO_ARG, which has "
        + itemCount
        + " elements:");
        IO_ARG current = first;
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

        IO_ARG current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static IO_ARG matchKey(int i) {
        IO_ARG O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static IO_ARG matchStr(String s) {
        IO_ARG O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final IO_ARG serialPortName = new IO_ARG("serialPortName");
    public static final IO_ARG baudRate = new IO_ARG("baudRate");
    public static final IO_ARG homeIPPort = new IO_ARG("homeIPPort");
    public static final IO_ARG remoteIPName = new IO_ARG("remoteIPName");
    public static final IO_ARG remoteIPPort = new IO_ARG("remoteIPPort");
    public static final IO_ARG trace = new IO_ARG("trace");
}

