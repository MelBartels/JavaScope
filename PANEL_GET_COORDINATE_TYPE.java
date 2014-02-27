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

public final class PANEL_GET_COORDINATE_TYPE {
    private String id;
    public final String description;
    public final int KEY;
    private PANEL_GET_COORDINATE_TYPE prev;
    private PANEL_GET_COORDINATE_TYPE next;

    private static int itemCount;
    private static PANEL_GET_COORDINATE_TYPE first;
    private static PANEL_GET_COORDINATE_TYPE last;

    private PANEL_GET_COORDINATE_TYPE(String id) {
        this(id, "");
    }

    private PANEL_GET_COORDINATE_TYPE(String id, String description) {
        this.id = new String(id);
        this.description = description;
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
            private PANEL_GET_COORDINATE_TYPE current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                PANEL_GET_COORDINATE_TYPE c = current;
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

    public static PANEL_GET_COORDINATE_TYPE first() {
        return first;
    }

    public static PANEL_GET_COORDINATE_TYPE last() {
        return last;
    }

    public PANEL_GET_COORDINATE_TYPE prev() {
        return this.prev;
    }

    public PANEL_GET_COORDINATE_TYPE next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of PANEL_GET_COORDINATE_TYPE, which has "
        + itemCount
        + " elements:");
        PANEL_GET_COORDINATE_TYPE current = first;
        while (current != null) {
            console.stdOutLn(current.id
            + ": "
            + current.KEY);
            current = current.next;
        }
        console.stdOutLn("end of display");
    }

    public static void displayCmdScope() {
        console.stdOut("display of PANEL_GET_COORDINATE_TYPE, which has "
        + itemCount
        + " elements:"
        + "\n\n");
        PANEL_GET_COORDINATE_TYPE current = first;
        while (current != null) {
            console.stdOut(current.id
            + ": "
            + current.description
            + "\n\n");
            current = current.next;
        }
        console.stdOutLn("end of display");
    }

    public static String returnItemListAsString() {
        String s = "";

        PANEL_GET_COORDINATE_TYPE current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static PANEL_GET_COORDINATE_TYPE matchKey(int i) {
        PANEL_GET_COORDINATE_TYPE O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static PANEL_GET_COORDINATE_TYPE matchStr(String s) {
        PANEL_GET_COORDINATE_TYPE O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static PANEL_GET_COORDINATE_TYPE matchDesc(String s) {
        PANEL_GET_COORDINATE_TYPE O = first();
        while (O != null && !s.equalsIgnoreCase(O.description))
            O = O.next();
        return O;
    }

    public static final PANEL_GET_COORDINATE_TYPE newTarget = new PANEL_GET_COORDINATE_TYPE("newTarget",  "new target");
    public static final PANEL_GET_COORDINATE_TYPE resetCurrent = new PANEL_GET_COORDINATE_TYPE("resetCurrent", "reset current");
    public static final PANEL_GET_COORDINATE_TYPE setInput = new PANEL_GET_COORDINATE_TYPE("setInput", "set input");
    public static final PANEL_GET_COORDINATE_TYPE autoInit1 = new PANEL_GET_COORDINATE_TYPE("autoInit1", "set autoInit #1");
    public static final PANEL_GET_COORDINATE_TYPE autoInit2 = new PANEL_GET_COORDINATE_TYPE("autoInit2", "set autoInit #2");
    public static final PANEL_GET_COORDINATE_TYPE init1 = new PANEL_GET_COORDINATE_TYPE("init1", "set init #1");
    public static final PANEL_GET_COORDINATE_TYPE init2 = new PANEL_GET_COORDINATE_TYPE("init2", "set init #2");
    public static final PANEL_GET_COORDINATE_TYPE init3 = new PANEL_GET_COORDINATE_TYPE("init3", "set init #3");
    public static final PANEL_GET_COORDINATE_TYPE polarAlign1 = new PANEL_GET_COORDINATE_TYPE("polarAlign1", "set polarAlign #1");
    public static final PANEL_GET_COORDINATE_TYPE polarAlign2 = new PANEL_GET_COORDINATE_TYPE("polarAlign2", "set polarAlign #2");
    public static final PANEL_GET_COORDINATE_TYPE polarAlign3 = new PANEL_GET_COORDINATE_TYPE("polarAlign3", "set polarAlign #3");
}

