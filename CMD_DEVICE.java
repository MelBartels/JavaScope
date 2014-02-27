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

public final class CMD_DEVICE {
    private String id;
    public final int KEY;
    private CMD_DEVICE prev;
    private CMD_DEVICE next;

    private static int itemCount;
    private static CMD_DEVICE first;
    private static CMD_DEVICE last;

    private CMD_DEVICE(String id) {
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
            private CMD_DEVICE current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                CMD_DEVICE c = current;
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

    public static CMD_DEVICE first() {
        return first;
    }

    public static CMD_DEVICE last() {
        return last;
    }

    public CMD_DEVICE prev() {
        return this.prev;
    }

    public CMD_DEVICE next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of CMD_DEVICE, which has "
        + itemCount
        + " elements:");
        CMD_DEVICE current = first;
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

        CMD_DEVICE current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static CMD_DEVICE matchKey(int i) {
        CMD_DEVICE O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static CMD_DEVICE matchStr(String s) {
        CMD_DEVICE O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final CMD_DEVICE none = new CMD_DEVICE("none");
    public static final CMD_DEVICE handpad = new CMD_DEVICE("handpad");
    public static final CMD_DEVICE SiTechHandpad = new CMD_DEVICE("SiTechHandpad");
    public static final CMD_DEVICE LX200 = new CMD_DEVICE("LX200");
    public static final CMD_DEVICE cmdScopeList = new CMD_DEVICE("cmdScopeList");
    public static final CMD_DEVICE trackByVel = new CMD_DEVICE("trackByVel");
    public static final CMD_DEVICE trackByPos = new CMD_DEVICE("trackByPos");
}

