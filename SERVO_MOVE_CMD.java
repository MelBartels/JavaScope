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

public final class SERVO_MOVE_CMD {
    private String id;
    public final int KEY;
    private SERVO_MOVE_CMD prev;
    private SERVO_MOVE_CMD next;

    private static int itemCount;
    private static SERVO_MOVE_CMD first;
    private static SERVO_MOVE_CMD last;

    private SERVO_MOVE_CMD(String id) {
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
            private SERVO_MOVE_CMD current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                SERVO_MOVE_CMD c = current;
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

    public static SERVO_MOVE_CMD first() {
        return first;
    }

    public static SERVO_MOVE_CMD last() {
        return last;
    }

    public SERVO_MOVE_CMD prev() {
        return this.prev;
    }

    public SERVO_MOVE_CMD next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of SERVO_MOVE_CMD, which has "
        + itemCount
        + " elements:");
        SERVO_MOVE_CMD current = first;
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

        SERVO_MOVE_CMD current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static SERVO_MOVE_CMD matchKey(int i) {
        SERVO_MOVE_CMD O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static SERVO_MOVE_CMD matchStr(String s) {
        SERVO_MOVE_CMD O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final SERVO_MOVE_CMD noMove = new SERVO_MOVE_CMD("noMove");
    public static final SERVO_MOVE_CMD startSlowVelMove = new SERVO_MOVE_CMD("StartSlowVelocityMove");
    public static final SERVO_MOVE_CMD startFastVelMove = new SERVO_MOVE_CMD("StartFastVelocityMove");
    public static final SERVO_MOVE_CMD startVelMove = new SERVO_MOVE_CMD("StartVelocityMove");
    public static final SERVO_MOVE_CMD posMoveStopCurrent = new SERVO_MOVE_CMD("PositionMoveStopCurrent");
    public static final SERVO_MOVE_CMD posMoveWaitForStop = new SERVO_MOVE_CMD("PositionMoveWaitForStop");
    public static final SERVO_MOVE_CMD posMoveStart = new SERVO_MOVE_CMD("PositionMoveStart");
    public static final SERVO_MOVE_CMD startDelayedMove = new SERVO_MOVE_CMD("startDelayedMove");
    public static final SERVO_MOVE_CMD stopMoveSmoothly = new SERVO_MOVE_CMD("stopMoveSmoothly");
    public static final SERVO_MOVE_CMD stopMoveAbruptly = new SERVO_MOVE_CMD("stopMoveAbruptly");
    public static final SERVO_MOVE_CMD turnMotorOnAmpOn = new SERVO_MOVE_CMD("turnMotorOnAmpOn");
    public static final SERVO_MOVE_CMD turnMotorOffAmpOn = new SERVO_MOVE_CMD("turnMotorOffAmpOn");
    public static final SERVO_MOVE_CMD disableAmp = new SERVO_MOVE_CMD("disableAmp");
    public static final SERVO_MOVE_CMD waitForMoveStateFinished = new SERVO_MOVE_CMD("waitForMoveStateFinished");
    public static final SERVO_MOVE_CMD waitForStop = new SERVO_MOVE_CMD("waitForStop");
}

