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

public final class LINK_POS_SORT_KEY {
    private String id;
    public final int KEY;
    private LINK_POS_SORT_KEY prev;
    private LINK_POS_SORT_KEY next;

    private static int itemCount;
    private static LINK_POS_SORT_KEY first;
    private static LINK_POS_SORT_KEY last;

    private LINK_POS_SORT_KEY(String id) {
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
            private LINK_POS_SORT_KEY current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                LINK_POS_SORT_KEY c = current;
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

    public static LINK_POS_SORT_KEY first() {
        return first;
    }

    public static LINK_POS_SORT_KEY last() {
        return last;
    }

    public LINK_POS_SORT_KEY prev() {
        return this.prev;
    }

    public LINK_POS_SORT_KEY next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of LINK_POS_SORT_KEY, which has "
        + itemCount
        + " elements:");
        LINK_POS_SORT_KEY current = first;
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

        LINK_POS_SORT_KEY current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static LINK_POS_SORT_KEY matchKey(int i) {
        LINK_POS_SORT_KEY O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static LINK_POS_SORT_KEY matchStr(String s) {
        LINK_POS_SORT_KEY O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final LINK_POS_SORT_KEY raAscend = new LINK_POS_SORT_KEY("raAscend");
    public static final LINK_POS_SORT_KEY decAscend = new LINK_POS_SORT_KEY("Dec__Ascend");
    public static final LINK_POS_SORT_KEY altAscend = new LINK_POS_SORT_KEY("altAscend");
    public static final LINK_POS_SORT_KEY absAltAscend = new LINK_POS_SORT_KEY("absAltAscend");
    public static final LINK_POS_SORT_KEY azAscend = new LINK_POS_SORT_KEY("azAscend");
    public static final LINK_POS_SORT_KEY sidTAscend = new LINK_POS_SORT_KEY("sidTAscend");
    public static final LINK_POS_SORT_KEY raDescend = new LINK_POS_SORT_KEY("raDescend");
    public static final LINK_POS_SORT_KEY decDescend = new LINK_POS_SORT_KEY("decDescend");
    public static final LINK_POS_SORT_KEY altDescend = new LINK_POS_SORT_KEY("altDescend");
    public static final LINK_POS_SORT_KEY absAltDescend = new LINK_POS_SORT_KEY("absAltDescend");
    public static final LINK_POS_SORT_KEY azDescend = new LINK_POS_SORT_KEY("azDescend");
    public static final LINK_POS_SORT_KEY sidTDescend = new LINK_POS_SORT_KEY("sidTDescend");
}

