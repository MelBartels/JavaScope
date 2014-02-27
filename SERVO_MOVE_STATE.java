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

public final class SERVO_MOVE_STATE {
    private String id;
    public final int KEY;
    private SERVO_MOVE_STATE prev;
    private SERVO_MOVE_STATE next;

    private static int itemCount;
    private static SERVO_MOVE_STATE first;
    private static SERVO_MOVE_STATE last;

    private SERVO_MOVE_STATE(String id) {
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
            private SERVO_MOVE_STATE current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                SERVO_MOVE_STATE c = current;
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

    public static SERVO_MOVE_STATE first() {
        return first;
    }

    public static SERVO_MOVE_STATE last() {
        return last;
    }

    public SERVO_MOVE_STATE prev() {
        return this.prev;
    }

    public SERVO_MOVE_STATE next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of SERVO_MOVE_STATE, which has " +
        itemCount
        + " elements:");
        SERVO_MOVE_STATE current = first;
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

        SERVO_MOVE_STATE current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static SERVO_MOVE_STATE matchKey(int i) {
        SERVO_MOVE_STATE O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static SERVO_MOVE_STATE matchStr(String s) {
        SERVO_MOVE_STATE O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final SERVO_MOVE_STATE posMoveStarted = new SERVO_MOVE_STATE("PositionMoveStarted");
    public static final SERVO_MOVE_STATE posMoveRampUp = new SERVO_MOVE_STATE("PostionMoveRampUp");
    public static final SERVO_MOVE_STATE posMoveMaxVel = new SERVO_MOVE_STATE("PositionMoveMaximumVelocity");
    public static final SERVO_MOVE_STATE posMoveRampDown = new SERVO_MOVE_STATE("PositionMoveRampDown");
    public static final SERVO_MOVE_STATE velMoveRamp = new SERVO_MOVE_STATE("VelocityMoveRamp");
    public static final SERVO_MOVE_STATE finished = new SERVO_MOVE_STATE("finished");
    public static final SERVO_MOVE_STATE unknown = new SERVO_MOVE_STATE("unknown");
}

