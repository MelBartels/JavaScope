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

public final class INIT_STATE {
    private String id;
    public final int KEY;
    private INIT_STATE prev;
    private INIT_STATE next;

    private static int itemCount;
    private static INIT_STATE first;
    private static INIT_STATE last;

    private INIT_STATE(String id) {
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
            private INIT_STATE current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                INIT_STATE c = current;
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

    public static INIT_STATE first() {
        return first;
    }

    public static INIT_STATE last() {
        return last;
    }

    public INIT_STATE prev() {
        return this.prev;
    }

    public INIT_STATE next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of INIT_STATE, which has "
        + itemCount
        + " elements:");
        INIT_STATE current = first;
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

        INIT_STATE current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static INIT_STATE matchKey(int i) {
        INIT_STATE O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static INIT_STATE matchStr(String s) {
        INIT_STATE O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final INIT_STATE equatAlign = new INIT_STATE("EquatorialAlignment");
    public static final INIT_STATE altazAlign = new INIT_STATE("AltazimuthAlignment");
    public static final INIT_STATE altAltAlign = new INIT_STATE("AltAltAlignment");
    public static final INIT_STATE configFileAlign = new INIT_STATE("ConfigFileAlignment");
    public static final INIT_STATE userAlign = new INIT_STATE("UserAlignment");
    public static final INIT_STATE noAlign = new INIT_STATE("NoAlignment");
}

