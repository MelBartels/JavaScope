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

public final class TRAJ_MOVE_STATUS {
    private String id;
    public final int KEY;
    private TRAJ_MOVE_STATUS prev;
    private TRAJ_MOVE_STATUS next;

    private static int itemCount;
    private static TRAJ_MOVE_STATUS first;
    private static TRAJ_MOVE_STATUS last;

    private TRAJ_MOVE_STATUS(String id) {
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
            private TRAJ_MOVE_STATUS current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                TRAJ_MOVE_STATUS c = current;
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

    public static TRAJ_MOVE_STATUS first() {
        return first;
    }

    public static TRAJ_MOVE_STATUS last() {
        return last;
    }

    public TRAJ_MOVE_STATUS prev() {
        return this.prev;
    }

    public TRAJ_MOVE_STATUS next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of TRAJ_MOVE_STATUS, which has "
        + itemCount
        + " elements:");
        TRAJ_MOVE_STATUS current = first;
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

        TRAJ_MOVE_STATUS current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static TRAJ_MOVE_STATUS matchKey(int i) {
        TRAJ_MOVE_STATUS O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static TRAJ_MOVE_STATUS matchStr(String s) {
        TRAJ_MOVE_STATUS O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final TRAJ_MOVE_STATUS waitToBuildTraj = new TRAJ_MOVE_STATUS("waitToBuildTraj");
    public static final TRAJ_MOVE_STATUS trajStarted = new TRAJ_MOVE_STATUS("trajStarted");
    public static final TRAJ_MOVE_STATUS waitForRampDown = new TRAJ_MOVE_STATUS("waitForRampDown");
    public static final TRAJ_MOVE_STATUS rampDownStarted = new TRAJ_MOVE_STATUS("rampDownStarted");
}

