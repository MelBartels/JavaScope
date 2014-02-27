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

public final class ENCODER_RESET_TYPE {
    private String id;
    public final int KEY;
    private ENCODER_RESET_TYPE prev;
    private ENCODER_RESET_TYPE next;

    private static int itemCount;
    private static ENCODER_RESET_TYPE first;
    private static ENCODER_RESET_TYPE last;

    private ENCODER_RESET_TYPE(String id) {
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
            private ENCODER_RESET_TYPE current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                ENCODER_RESET_TYPE c = current;
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

    public static ENCODER_RESET_TYPE first() {
        return first;
    }

    public static ENCODER_RESET_TYPE last() {
        return last;
    }

    public ENCODER_RESET_TYPE prev() {
        return this.prev;
    }

    public ENCODER_RESET_TYPE next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of ENCODER_RESET_TYPE, which has "
        + itemCount
        + " elements:");
        ENCODER_RESET_TYPE current = first;
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

        ENCODER_RESET_TYPE current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static ENCODER_RESET_TYPE matchKey(int i) {
        ENCODER_RESET_TYPE O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static ENCODER_RESET_TYPE matchStr(String s) {
        ENCODER_RESET_TYPE O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final ENCODER_RESET_TYPE scopeToEncoders = new ENCODER_RESET_TYPE("scopeToEncoders");
    public static final ENCODER_RESET_TYPE encodersToScope = new ENCODER_RESET_TYPE("encodersToScope");
}

