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

public final class GUIDE_ARRAY_STATE {
    private String id;
    public final int KEY;
    private GUIDE_ARRAY_STATE prev;
    private GUIDE_ARRAY_STATE next;

    private static int itemCount;
    private static GUIDE_ARRAY_STATE first;
    private static GUIDE_ARRAY_STATE last;

    private GUIDE_ARRAY_STATE(String id) {
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
            private GUIDE_ARRAY_STATE current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                GUIDE_ARRAY_STATE c = current;
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

    public static GUIDE_ARRAY_STATE first() {
        return first;
    }

    public static GUIDE_ARRAY_STATE last() {
        return last;
    }

    public GUIDE_ARRAY_STATE prev() {
        return this.prev;
    }

    public GUIDE_ARRAY_STATE next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of GUIDE_ARRAY_STATE, which has "
        + itemCount
        + " elements:");
        GUIDE_ARRAY_STATE current = first;
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

        GUIDE_ARRAY_STATE current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static GUIDE_ARRAY_STATE matchKey(int i) {
        GUIDE_ARRAY_STATE O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static GUIDE_ARRAY_STATE matchStr(String s) {
        GUIDE_ARRAY_STATE O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final GUIDE_ARRAY_STATE off = new GUIDE_ARRAY_STATE("off");
    public static final GUIDE_ARRAY_STATE writing = new GUIDE_ARRAY_STATE("writing");
    public static final GUIDE_ARRAY_STATE readyToSave = new GUIDE_ARRAY_STATE("readyToSave");
}

