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

public final class TRACK_STYLE_ID {
    private String id;
    public final int KEY;
    private TRACK_STYLE_ID prev;
    private TRACK_STYLE_ID next;

    private static int itemCount;
    private static TRACK_STYLE_ID first;
    private static TRACK_STYLE_ID last;

    private TRACK_STYLE_ID(String id) {
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
            private TRACK_STYLE_ID current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                TRACK_STYLE_ID c = current;
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

    public static TRACK_STYLE_ID first() {
        return first;
    }

    public static TRACK_STYLE_ID last() {
        return last;
    }

    public TRACK_STYLE_ID prev() {
        return this.prev;
    }

    public TRACK_STYLE_ID next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of TRACK_STYLE_ID, which has "
        + itemCount
        + " elements:");
        TRACK_STYLE_ID current = first;
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

        TRACK_STYLE_ID current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static TRACK_STYLE_ID matchKey(int i) {
        TRACK_STYLE_ID O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static TRACK_STYLE_ID matchStr(String s) {
        TRACK_STYLE_ID O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final TRACK_STYLE_ID trackStylePID = new TRACK_STYLE_ID("trackStylePID");
    public static final TRACK_STYLE_ID trackStyleVelocity = new TRACK_STYLE_ID("trackStyleVelocity");
    public static final TRACK_STYLE_ID trackStylePropVel = new TRACK_STYLE_ID("trackStylePropVel");
    public static final TRACK_STYLE_ID trackStylePropVel2 = new TRACK_STYLE_ID("trackStylePropVel2");
    public static final TRACK_STYLE_ID trackStylePosition = new TRACK_STYLE_ID("trackStylePosition");
    public static final TRACK_STYLE_ID trackStyleTrajTimed = new TRACK_STYLE_ID("trackStyleTrajTimed");
    public static final TRACK_STYLE_ID trackStylePositionWithPropVel = new TRACK_STYLE_ID("trackStylePositionWithPropVel");
    public static final TRACK_STYLE_ID trackStylePositionWithPropVel2 = new TRACK_STYLE_ID("trackStylePositionWithPropVel2");
    public static final TRACK_STYLE_ID trackStyleTrajVel = new TRACK_STYLE_ID("trackStyleTrajVel");
}

