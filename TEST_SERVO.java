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

public final class TEST_SERVO {
    private String id;
    public final int KEY;
    private TEST_SERVO prev;
    private TEST_SERVO next;

    private static int itemCount;
    private static TEST_SERVO first;
    private static TEST_SERVO last;

    private TEST_SERVO(String id) {
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
            private TEST_SERVO current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                TEST_SERVO c = current;
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

    public static TEST_SERVO first() {
        return first;
    }

    public static TEST_SERVO last() {
        return last;
    }

    public TEST_SERVO prev() {
        return this.prev;
    }

    public TEST_SERVO next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of TEST_SERVO, which has "
        + itemCount
        + " elements:");
        TEST_SERVO current = first;
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

        TEST_SERVO current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static TEST_SERVO matchKey(int i) {
        TEST_SERVO O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static TEST_SERVO matchStr(String s) {
        TEST_SERVO O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final TEST_SERVO moveToPosition = new TEST_SERVO("moveToPosition");
    public static final TEST_SERVO moveToPositionNoStopCheck = new TEST_SERVO("moveToPositionNoStopCheck");
    public static final TEST_SERVO moveAtVelocity = new TEST_SERVO("moveAtVelocity");
    public static final TEST_SERVO startDelayedMotion = new TEST_SERVO("startDelayedMotion");
    public static final TEST_SERVO stopSmoothly = new TEST_SERVO("stopSmoothly");
    public static final TEST_SERVO stopAbruptly = new TEST_SERVO("stopAbruptly");
    public static final TEST_SERVO turnOnMotorTurnOnAmp = new TEST_SERVO("turnOnMotorTurnOnAmp");
    public static final TEST_SERVO turnOffMotorTurnOnAmp = new TEST_SERVO("turnOffMotorTurnOnAmp");
    public static final TEST_SERVO turnOffAmp = new TEST_SERVO("turnOffAmp");
    public static final TEST_SERVO resetPosition = new TEST_SERVO("resetPosition");
    public static final TEST_SERVO clearStickyBits = new TEST_SERVO("clearStickyBits");
    public static final TEST_SERVO changeStatusReturn = new TEST_SERVO("changeStatusReturn");
    public static final TEST_SERVO displayStatus = new TEST_SERVO("displayStatus");
    public static final TEST_SERVO moveStatus = new TEST_SERVO("moveStatus");
}

