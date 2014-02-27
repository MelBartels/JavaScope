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

public final class COORDINATE_TYPE {
    private String id;
    public final int KEY;
    private COORDINATE_TYPE prev;
    private COORDINATE_TYPE next;

    private static int itemCount;
    private static COORDINATE_TYPE first;
    private static COORDINATE_TYPE last;

    private COORDINATE_TYPE(String id) {
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
            private COORDINATE_TYPE current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                COORDINATE_TYPE c = current;
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

    public static COORDINATE_TYPE first() {
        return first;
    }

    public static COORDINATE_TYPE last() {
        return last;
    }

    public COORDINATE_TYPE prev() {
        return this.prev;
    }

    public COORDINATE_TYPE next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of COORDINATE_TYPE, which has "
        + itemCount
        + " elements:");
        COORDINATE_TYPE current = first;
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

        COORDINATE_TYPE current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static COORDINATE_TYPE matchKey(int i) {
        COORDINATE_TYPE O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static COORDINATE_TYPE matchStr(String s) {
        COORDINATE_TYPE O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final COORDINATE_TYPE altaz = new COORDINATE_TYPE("altaz");
    public static final COORDINATE_TYPE equat = new COORDINATE_TYPE("equat");
}

