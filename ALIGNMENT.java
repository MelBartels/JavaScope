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

public final class ALIGNMENT {
    private String id;
    public final int KEY;
    private ALIGNMENT prev;
    private ALIGNMENT next;

    private static int itemCount;
    private static ALIGNMENT first;
    private static ALIGNMENT last;

    private ALIGNMENT(String id) {
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
            private ALIGNMENT current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                ALIGNMENT c = current;
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

    public static ALIGNMENT first() {
        return first;
    }

    public static ALIGNMENT last() {
        return last;
    }

    public ALIGNMENT prev() {
        return this.prev;
    }

    public ALIGNMENT next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of ALIGNMENT, which has "
        + itemCount
        + " elements:");
        ALIGNMENT current = first;
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

        ALIGNMENT current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static ALIGNMENT matchKey(int i) {
        ALIGNMENT O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static ALIGNMENT matchStr(String s) {
        ALIGNMENT O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final ALIGNMENT none = new ALIGNMENT("none");
    public static final ALIGNMENT altaz = new ALIGNMENT("altaz");
    public static final ALIGNMENT equat = new ALIGNMENT("equat");
}

