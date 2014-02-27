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

public final class SPIRAL_SEARCH_SEQUENCE {
    private String id;
    public final int KEY;
    private SPIRAL_SEARCH_SEQUENCE prev;
    private SPIRAL_SEARCH_SEQUENCE next;

    private static int itemCount;
    private static SPIRAL_SEARCH_SEQUENCE first;
    private static SPIRAL_SEARCH_SEQUENCE last;

    private SPIRAL_SEARCH_SEQUENCE(String id) {
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
            private SPIRAL_SEARCH_SEQUENCE current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                SPIRAL_SEARCH_SEQUENCE c = current;
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

    public static SPIRAL_SEARCH_SEQUENCE first() {
        return first;
    }

    public static SPIRAL_SEARCH_SEQUENCE last() {
        return last;
    }

    public SPIRAL_SEARCH_SEQUENCE prev() {
        return this.prev;
    }

    public SPIRAL_SEARCH_SEQUENCE next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of SPIRAL_SEARCH_SEQUENCE, which has "
        + itemCount
        + " elements:");
        SPIRAL_SEARCH_SEQUENCE current = first;
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

        SPIRAL_SEARCH_SEQUENCE current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static SPIRAL_SEARCH_SEQUENCE matchKey(int i) {
        SPIRAL_SEARCH_SEQUENCE O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static SPIRAL_SEARCH_SEQUENCE matchStr(String s) {
        SPIRAL_SEARCH_SEQUENCE O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final SPIRAL_SEARCH_SEQUENCE right = new SPIRAL_SEARCH_SEQUENCE("right");
    public static final SPIRAL_SEARCH_SEQUENCE up = new SPIRAL_SEARCH_SEQUENCE("up");
    public static final SPIRAL_SEARCH_SEQUENCE left = new SPIRAL_SEARCH_SEQUENCE("left");
    public static final SPIRAL_SEARCH_SEQUENCE down = new SPIRAL_SEARCH_SEQUENCE("down");
}

