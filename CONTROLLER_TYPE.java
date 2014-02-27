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

public final class CONTROLLER_TYPE {
    private String id;
    public final int KEY;
    private CONTROLLER_TYPE prev;
    private CONTROLLER_TYPE next;

    private static int itemCount;
    private static CONTROLLER_TYPE first;
    private static CONTROLLER_TYPE last;

    private CONTROLLER_TYPE(String id) {
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
            private CONTROLLER_TYPE current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                CONTROLLER_TYPE c = current;
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

    public static CONTROLLER_TYPE first() {
        return first;
    }

    public static CONTROLLER_TYPE last() {
        return last;
    }

    public CONTROLLER_TYPE prev() {
        return this.prev;
    }

    public CONTROLLER_TYPE next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of CONTROLLER_TYPE, which has "
        + itemCount
        + " elements:");
        CONTROLLER_TYPE current = first;
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

        CONTROLLER_TYPE current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static CONTROLLER_TYPE matchKey(int i) {
        CONTROLLER_TYPE O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static CONTROLLER_TYPE matchStr(String s) {
        CONTROLLER_TYPE O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final CONTROLLER_TYPE motor = new CONTROLLER_TYPE("motor");
    public static final CONTROLLER_TYPE encoder = new CONTROLLER_TYPE("encoder");
    public static final CONTROLLER_TYPE io = new CONTROLLER_TYPE("IO");
}

