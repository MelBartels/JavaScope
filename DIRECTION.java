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

public final class DIRECTION {
    private String id;
    public final int KEY;
    private DIRECTION prev;
    private DIRECTION next;

    private static int itemCount;
    private static DIRECTION first;
    private static DIRECTION last;

    private DIRECTION(String id) {
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
            private DIRECTION current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                DIRECTION c = current;
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

    public static DIRECTION first() {
        return first;
    }

    public static DIRECTION last() {
        return last;
    }

    public DIRECTION prev() {
        return this.prev;
    }

    public DIRECTION next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of DIRECTION, which has "
        + itemCount
        + " elements:");
        DIRECTION current = first;
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

        DIRECTION current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static DIRECTION matchKey(int i) {
        DIRECTION O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static DIRECTION matchStr(String s) {
        DIRECTION O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final DIRECTION forward = new DIRECTION("forward");
    public static final DIRECTION backward = new DIRECTION("backward");
}

