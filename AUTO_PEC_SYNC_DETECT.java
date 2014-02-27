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

public final class AUTO_PEC_SYNC_DETECT {
    private String id;
    public final int KEY;
    private AUTO_PEC_SYNC_DETECT prev;
    private AUTO_PEC_SYNC_DETECT next;

    private static int itemCount;
    private static AUTO_PEC_SYNC_DETECT first;
    private static AUTO_PEC_SYNC_DETECT last;

    private AUTO_PEC_SYNC_DETECT(String id) {
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
            private AUTO_PEC_SYNC_DETECT current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                AUTO_PEC_SYNC_DETECT c = current;
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

    public static AUTO_PEC_SYNC_DETECT first() {
        return first;
    }

    public static AUTO_PEC_SYNC_DETECT last() {
        return last;
    }

    public AUTO_PEC_SYNC_DETECT prev() {
        return this.prev;
    }

    public AUTO_PEC_SYNC_DETECT next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of AUTO_PEC_SYNC_DETECT, which has "
        + itemCount
        + " elements:");
        AUTO_PEC_SYNC_DETECT current = first;
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

        AUTO_PEC_SYNC_DETECT current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static AUTO_PEC_SYNC_DETECT matchKey(int i) {
        AUTO_PEC_SYNC_DETECT O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static AUTO_PEC_SYNC_DETECT matchStr(String s) {
        AUTO_PEC_SYNC_DETECT O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final AUTO_PEC_SYNC_DETECT Off = new AUTO_PEC_SYNC_DETECT("Off");
    public static final AUTO_PEC_SYNC_DETECT lowHigh = new AUTO_PEC_SYNC_DETECT("lowHigh");
    public static final AUTO_PEC_SYNC_DETECT highLow = new AUTO_PEC_SYNC_DETECT("highLow");
}

