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

public final class HANDPAD_DESIGN {
    private String id;
    public final int KEY;
    private HANDPAD_DESIGN prev;
    private HANDPAD_DESIGN next;

    private static int itemCount;
    private static HANDPAD_DESIGN first;
    private static HANDPAD_DESIGN last;

    private HANDPAD_DESIGN(String id) {
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
            private HANDPAD_DESIGN current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                HANDPAD_DESIGN c = current;
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

    public static HANDPAD_DESIGN first() {
        return first;
    }

    public static HANDPAD_DESIGN last() {
        return last;
    }

    public HANDPAD_DESIGN prev() {
        return this.prev;
    }

    public HANDPAD_DESIGN next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of HANDPAD_DESIGN, which has "
        + itemCount
        + " elements:");
        HANDPAD_DESIGN current = first;
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

        HANDPAD_DESIGN current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static HANDPAD_DESIGN matchKey(int i) {
        HANDPAD_DESIGN O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static HANDPAD_DESIGN matchStr(String s) {
        HANDPAD_DESIGN O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final HANDPAD_DESIGN handpadDesignStandard = new HANDPAD_DESIGN("handpadDesignStandard");
    public static final HANDPAD_DESIGN handpadDesignDirectionOnly = new HANDPAD_DESIGN("handpadDesignDirectionOnly");
    public static final HANDPAD_DESIGN handpadDesignSiTech = new HANDPAD_DESIGN("handpadDesignSiTech");
}

