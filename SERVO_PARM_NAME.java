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

public final class SERVO_PARM_NAME {
    private String id;
    public final String description;
    public final int KEY;
    private SERVO_PARM_NAME prev;
    private SERVO_PARM_NAME next;

    private static int itemCount;
    private static SERVO_PARM_NAME first;
    private static SERVO_PARM_NAME last;

    private SERVO_PARM_NAME(String id) {
        this(id, "");
    }

    private SERVO_PARM_NAME(String id, String description) {
        this.id = new String(id);
        this.description = description;
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
            private SERVO_PARM_NAME current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                SERVO_PARM_NAME c = current;
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

    public static SERVO_PARM_NAME first() {
        return first;
    }

    public static SERVO_PARM_NAME last() {
        return last;
    }

    public SERVO_PARM_NAME prev() {
        return this.prev;
    }

    public SERVO_PARM_NAME next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of SERVO_PARM_NAME, which has "
        + itemCount
        + " elements:");
        SERVO_PARM_NAME current = first;
        while (current != null) {
            console.stdOutLn(current.id
            + ": "
            + current.KEY);
            current = current.next;
        }
        console.stdOutLn("end of display");
    }

    public static void displayCmdScope() {
        console.stdOut("display of SERVO_PARM_NAME, which has "
        + itemCount
        + " elements:"
        + "\n\n");
        SERVO_PARM_NAME current = first;
        while (current != null) {
            console.stdOut(current.id
            + ": "
            + current.description
            + "\n\n");
            current = current.next;
        }
        console.stdOutLn("end of display");
    }

    public static String returnItemListAsString() {
        String s = "";

        SERVO_PARM_NAME current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static SERVO_PARM_NAME matchKey(int i) {
        SERVO_PARM_NAME O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static SERVO_PARM_NAME matchStr(String s) {
        SERVO_PARM_NAME O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static SERVO_PARM_NAME matchDesc(String s) {
        SERVO_PARM_NAME O = first();
        while (O != null && !s.equalsIgnoreCase(O.description))
            O = O.next();
        return O;
    }

    public static final SERVO_PARM_NAME controllerType = new SERVO_PARM_NAME("controllerType",
    "type of PICServo controller, options are:\n" + SERVO_ID.returnItemListAsString());

    public static final SERVO_PARM_NAME controllerActive = new SERVO_PARM_NAME("controllerActive",
    "'true' if the PICServo controller is powered on");

    public static final SERVO_PARM_NAME ampEnableActiveHigh = new SERVO_PARM_NAME("ampEnableActiveHigh",
    "'true' if the amplifier is enabled when control line set to logical high, default is 'false'");

    public static final SERVO_PARM_NAME positionGainKp = new SERVO_PARM_NAME("positionGainKp",
    "value of the PID filter proportional component: position Gain Kp");

    public static final SERVO_PARM_NAME velGainKd = new SERVO_PARM_NAME("velGainKd",
    "value of the PID filter differential component: velocity Gain Kd");

    public static final SERVO_PARM_NAME positionGainKi = new SERVO_PARM_NAME("positionGainKi",
    "value of the PID filter integral component: position Gain Ki");

    public static final SERVO_PARM_NAME integrationLimitIL = new SERVO_PARM_NAME("integrationLimitIL",
    "value of the PID filter integration limit IL");

    public static final SERVO_PARM_NAME outputLimitOL = new SERVO_PARM_NAME("outputLimitOL",
    "output limit, max 255");

    public static final SERVO_PARM_NAME currentLimitCL = new SERVO_PARM_NAME("currentLimitCL",
    "current limit, max 255");

    public static final SERVO_PARM_NAME positionErrorLimitEL = new SERVO_PARM_NAME("positionErrorLimitEL",
    "allowable Positional error before servo is disabled, max 16383");

    public static final SERVO_PARM_NAME rateDivisorSR = new SERVO_PARM_NAME("rateDivisorSR",
    "PICServo PID servo loop timing rate divisor, a number higher than 1 causes the servo loop to proportionally run slower");

    public static final SERVO_PARM_NAME ampDeadbandComp = new SERVO_PARM_NAME("ampDeadbandComp",
    "amplifier deadband compensation at PWM of 0");

    public static final SERVO_PARM_NAME track = new SERVO_PARM_NAME("track",
    "'true' if tracking is turned on for this motor");

    public static final SERVO_PARM_NAME stepsPerRev = new SERVO_PARM_NAME("stepsPerRev",
    "motor steps per telescope's axis rotation");

    public static final SERVO_PARM_NAME encoderCountsPerRev = new SERVO_PARM_NAME("encoderCountsPerRev",
    "motor encoder counts per motor shaft revolution");

    public static final SERVO_PARM_NAME reverseMotor = new SERVO_PARM_NAME("reverseMotor",
    "'true' if motor direction should be reversed, else 'false'");

    public static final SERVO_PARM_NAME accelDegSecSec = new SERVO_PARM_NAME("accelDegSecSec",
    "motor acceleration in degrees per second per second");

    public static final SERVO_PARM_NAME fastSpeedDegSec = new SERVO_PARM_NAME("fastSpeedDegSec",
    "motor highest speed in degrees per second");

    public static final SERVO_PARM_NAME slowSpeedArcsecSec = new SERVO_PARM_NAME("slowSpeedArcsecSec",
    "motor slow speed in degrees per second");

    public static final SERVO_PARM_NAME dampenFactor = new SERVO_PARM_NAME("dampenFactor",
    "motor tracking aggressiveness, too high and motor will overshoot, too low and motor will not reach target");

    public static final SERVO_PARM_NAME homeDeg = new SERVO_PARM_NAME("homeDeg",
    "axis home position in degrees");

    public static final SERVO_PARM_NAME sectorDeg = new SERVO_PARM_NAME("sectorDeg",
    "if axis employs a sector drive, size of sector in degrees, else leave at '0'");

    public static final SERVO_PARM_NAME softLimitOn = new SERVO_PARM_NAME("softLimitOn",
    "software checked limits to motion is active");

    public static final SERVO_PARM_NAME currentPositionDeg = new SERVO_PARM_NAME("currentPositionDeg",
    "motor current position in degrees");

    public static final SERVO_PARM_NAME backlashArcmin = new SERVO_PARM_NAME("backlashArcmin",
    "amount of backlash in arcminutes");

    public static final SERVO_PARM_NAME guideArcsecSec = new SERVO_PARM_NAME("guideArcsecSec",
    "guiding rate in arcseconds per second of time");

    public static final SERVO_PARM_NAME guideDragArcsecPerMin = new SERVO_PARM_NAME("guideDragArcsecPerMin",
    "drift to drag a guide star so that an autoguider can update in one direction only");

    public static final SERVO_PARM_NAME driftArcsecPerMin = new SERVO_PARM_NAME("driftArcsecPerMin",
    "drift rate in arcseconds per minute of time");

    public static final SERVO_PARM_NAME PECActive = new SERVO_PARM_NAME("PECActive",
    "'true' if periodic error correction is turned on, else 'false'");

    public static final SERVO_PARM_NAME autoPECSyncDetect = new SERVO_PARM_NAME("autoPECSyncDetect",
    "'true' if automatic detection of the PEC synchronization point is used, else 'false'");
}

