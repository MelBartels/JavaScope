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

public final class SERVO_CMD {
    private String id;
    public final int KEY;
    private SERVO_CMD prev;
    private SERVO_CMD next;

    private static int itemCount;
    private static SERVO_CMD first;
    private static SERVO_CMD last;

    private SERVO_CMD(String id) {
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
            private SERVO_CMD current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                SERVO_CMD c = current;
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

    public static SERVO_CMD first() {
        return first;
    }

    public static SERVO_CMD last() {
        return last;
    }

    public SERVO_CMD prev() {
        return this.prev;
    }

    public SERVO_CMD next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of SERVO_CMD, which has "
        + itemCount
        + " elements:");
        SERVO_CMD current = first;
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

        SERVO_CMD current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static SERVO_CMD matchKey(int i) {
        SERVO_CMD O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static SERVO_CMD matchStr(String s) {
        SERVO_CMD O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final SERVO_CMD hardReset = new SERVO_CMD("hardReset");
    public static final SERVO_CMD setAddress = new SERVO_CMD("setAddress");
    public static final SERVO_CMD defineStatus = new SERVO_CMD("defineStatus");
    public static final SERVO_CMD getStatus = new SERVO_CMD("getStatus");
    public static final SERVO_CMD resetPosition = new SERVO_CMD("resetPosition");
    public static final SERVO_CMD setGain = new SERVO_CMD("setGain");
    public static final SERVO_CMD startMotor = new SERVO_CMD("startMotor");
    public static final SERVO_CMD stopMotor = new SERVO_CMD("stopMotor");
    public static final SERVO_CMD clearStickyBits = new SERVO_CMD("clearStickyBits");
    public static final SERVO_CMD loadTrajPos = new SERVO_CMD("loadTrajPos");
    public static final SERVO_CMD loadTrajVel = new SERVO_CMD("loadTrajVel");
    public static final SERVO_CMD SiTechHandpadSlewPanSpeeds = new SERVO_CMD("SiTechHandpadSlewPanSpeeds");
    public static final SERVO_CMD SiTechHandpadGuideSpeed = new SERVO_CMD("SiTechHandpadGuideSpeed");
    public static final SERVO_CMD nopController = new SERVO_CMD("nopController");
    public static final SERVO_CMD unknown = new SERVO_CMD("unknown");
}

