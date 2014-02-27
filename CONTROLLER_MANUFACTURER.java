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

public final class CONTROLLER_MANUFACTURER {
    private String id;
    public final int KEY;
    private CONTROLLER_MANUFACTURER prev;
    private CONTROLLER_MANUFACTURER next;

    private static int itemCount;
    private static CONTROLLER_MANUFACTURER first;
    private static CONTROLLER_MANUFACTURER last;

    private CONTROLLER_MANUFACTURER(String id) {
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
            private CONTROLLER_MANUFACTURER current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                CONTROLLER_MANUFACTURER c = current;
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

    public static CONTROLLER_MANUFACTURER first() {
        return first;
    }

    public static CONTROLLER_MANUFACTURER last() {
        return last;
    }

    public CONTROLLER_MANUFACTURER prev() {
        return this.prev;
    }

    public CONTROLLER_MANUFACTURER next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of CONTROLLER_MANUFACTURER, which has "
        + itemCount
        + " elements:");
        CONTROLLER_MANUFACTURER current = first;
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

        CONTROLLER_MANUFACTURER current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static CONTROLLER_MANUFACTURER matchKey(int i) {
        CONTROLLER_MANUFACTURER O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static CONTROLLER_MANUFACTURER matchStr(String s) {
        CONTROLLER_MANUFACTURER O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final CONTROLLER_MANUFACTURER JRKerr = new CONTROLLER_MANUFACTURER("JRKerr");
    public static final CONTROLLER_MANUFACTURER SiTech = new CONTROLLER_MANUFACTURER("SiTech");
}

