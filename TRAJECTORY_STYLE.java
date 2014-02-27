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

public final class TRAJECTORY_STYLE {
    private String id;
    public final int KEY;
    private TRAJECTORY_STYLE prev;
    private TRAJECTORY_STYLE next;

    private static int itemCount;
    private static TRAJECTORY_STYLE first;
    private static TRAJECTORY_STYLE last;

    private TRAJECTORY_STYLE(String id) {
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
            private TRAJECTORY_STYLE current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                TRAJECTORY_STYLE c = current;
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

    public static TRAJECTORY_STYLE first() {
        return first;
    }

    public static TRAJECTORY_STYLE last() {
        return last;
    }

    public TRAJECTORY_STYLE prev() {
        return this.prev;
    }

    public TRAJECTORY_STYLE next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of TRAJECTORY_STYLE, which has "
        + itemCount
        + " elements:");
        TRAJECTORY_STYLE current = first;
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

        TRAJECTORY_STYLE current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static TRAJECTORY_STYLE matchKey(int i) {
        TRAJECTORY_STYLE O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static TRAJECTORY_STYLE matchStr(String s) {
        TRAJECTORY_STYLE O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final TRAJECTORY_STYLE position = new TRAJECTORY_STYLE("position");
    public static final TRAJECTORY_STYLE velocity = new TRAJECTORY_STYLE("velocity");
}

