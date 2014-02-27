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

public final class WHY_INIT {
    private String id;
    public final int KEY;
    private WHY_INIT prev;
    private WHY_INIT next;

    private static int itemCount;
    private static WHY_INIT first;
    private static WHY_INIT last;

    private WHY_INIT(String id) {
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
            private WHY_INIT current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                WHY_INIT c = current;
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

    public static WHY_INIT first() {
        return first;
    }

    public static WHY_INIT last() {
        return last;
    }

    public WHY_INIT prev() {
        return this.prev;
    }

    public WHY_INIT next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of WHY_INIT, which has "
        + itemCount
        + " elements:");
        WHY_INIT current = first;
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

        WHY_INIT current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static WHY_INIT matchKey(int i) {
        WHY_INIT O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static WHY_INIT matchStr(String s) {
        WHY_INIT O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final WHY_INIT equat = new WHY_INIT("equat");
    public static final WHY_INIT altaz = new WHY_INIT("altaz");
    public static final WHY_INIT altAlt = new WHY_INIT("altAlt");
    public static final WHY_INIT altAltAz = new WHY_INIT("altAltAz");
    public static final WHY_INIT cfgFile = new WHY_INIT("cfgFile");
    public static final WHY_INIT reInitConversionMatrix = new WHY_INIT("reInitConversionMatrix");
    public static final WHY_INIT z123 = new WHY_INIT("z123");
    public static final WHY_INIT test = new WHY_INIT("Test");
    public static final WHY_INIT manual = new WHY_INIT("manual");
    public static final WHY_INIT keyboard = new WHY_INIT("keyboard");
    public static final WHY_INIT handpad = new WHY_INIT("handpad");
    public static final WHY_INIT altOffset = new WHY_INIT("Altoffset");
    public static final WHY_INIT cmdScopeList = new WHY_INIT("cmdScopeList");
    public static final WHY_INIT LX200 = new WHY_INIT("LX200");
    public static final WHY_INIT encoders = new WHY_INIT("encoders");
    public static final WHY_INIT closestInitHistory = new WHY_INIT("closestInitHistory");
}

