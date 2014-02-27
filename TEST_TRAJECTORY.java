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

public final class TEST_TRAJECTORY {
    private String id;
    public final int KEY;
    private TEST_TRAJECTORY prev;
    private TEST_TRAJECTORY next;

    private static int itemCount;
    private static TEST_TRAJECTORY first;
    private static TEST_TRAJECTORY last;

    private TEST_TRAJECTORY(String id) {
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
            private TEST_TRAJECTORY current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                TEST_TRAJECTORY c = current;
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

    public static TEST_TRAJECTORY first() {
        return first;
    }

    public static TEST_TRAJECTORY last() {
        return last;
    }

    public TEST_TRAJECTORY prev() {
        return this.prev;
    }

    public TEST_TRAJECTORY next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of TEST_TRAJECTORY, which has "
        + itemCount
        + " elements:");
        TEST_TRAJECTORY current = first;
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

        TEST_TRAJECTORY current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static TEST_TRAJECTORY matchKey(int i) {
        TEST_TRAJECTORY O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static TEST_TRAJECTORY matchStr(String s) {
        TEST_TRAJECTORY O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final TEST_TRAJECTORY timeDistanceFromAccelBegVelEndVel =
    new TEST_TRAJECTORY("timeDistanceFromAccelBegVelEndVel");
    public static final TEST_TRAJECTORY timeDistanceFromTimeAccelBegVelEndVel =
    new TEST_TRAJECTORY("timeDistanceFromTimeAccelBegVelEndVel");
    public static final TEST_TRAJECTORY finalVelFromAccelDistanceTimeDiff =
    new TEST_TRAJECTORY("finalVelFromAccelDistanceTimeDiff");
    public static final TEST_TRAJECTORY triggerTimeDistanceFromAccelBegVelEndVel =
    new TEST_TRAJECTORY("triggerTimeDistanceFromAccelBegVelEndVel");
    public static final TEST_TRAJECTORY maxVelFromAccel_UnCompDistance_BegVelEndVel =
    new TEST_TRAJECTORY("MaxVelFromAccel_UnCompDistance_BegVelEndVel");
    public static final TEST_TRAJECTORY maxVelFromAccelDistanceBegVelEndVel =
    new TEST_TRAJECTORY("MaxVelFromAccelDistanceBegVelEndVel");
    public static final TEST_TRAJECTORY maxVelFromAccelDistanceBegVelEndVelFirstTrajZeroEndVel =
    new TEST_TRAJECTORY("MaxVelFromAccelDistanceBegVelEndVelFirstTrajZeroEndVel");
    public static final TEST_TRAJECTORY maxVelFromTime_UnCompDistance_BegVelEndVel =
    new TEST_TRAJECTORY("MaxVelFromTime_UnCompDistance_BegVelEndVel");
    public static final TEST_TRAJECTORY maxVelFromTimeDistanceBegVelEndVel =
    new TEST_TRAJECTORY("MaxVelFromTimeDistanceBegVelEndVel");
    public static final TEST_TRAJECTORY trajFromTimeDistanceBegVelEndVel =
    new TEST_TRAJECTORY("trajFromTimeDistanceBegVelEndVel");
    public static final TEST_TRAJECTORY trajFromAccelDistanceMaxVel =
    new TEST_TRAJECTORY("trajFromAccelDistanceMaxVel");
    public static final TEST_TRAJECTORY distanceFromTimeAccelDistanceMaxVel =
    new TEST_TRAJECTORY("distanceFromTimeAccelDistanceMaxVel");
    public static final TEST_TRAJECTORY velFromTimeAccelDistanceMaxVel =
    new TEST_TRAJECTORY("velFromTimeAccelDistanceMaxVel");
    public static final TEST_TRAJECTORY trajFromAccelDistanceMaxVelBegVelEndVel =
    new TEST_TRAJECTORY("trajFromAccelDistanceMaxVelBegVelEndVel");
    public static final TEST_TRAJECTORY trajFromAccelDistanceMaxVelBegVelEndVelFirstTrajZeroEndVel =
    new TEST_TRAJECTORY("trajFromAccelDistanceMaxVelBegVelEndVelFirstTrajZeroEndVel");
    public static final TEST_TRAJECTORY trajFromAccelBegVelTargetVel =
    new TEST_TRAJECTORY("trajFromAccelBegVelTargetVel");
    public static final TEST_TRAJECTORY distanceFromTimeAccelBegVelTargetVel =
    new TEST_TRAJECTORY("distanceFromTimeAccelBegVelTargetVel");
    public static final TEST_TRAJECTORY velFromTimeAccelBegVelTargetVel =
    new TEST_TRAJECTORY("velFromTimeAccelBegVelTargetVel");
    public static final TEST_TRAJECTORY quit =
    new TEST_TRAJECTORY("quit");
}

