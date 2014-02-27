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

public final class PICSERVO_SIM_CMD {
    private String id;
    public final int KEY;
    private PICSERVO_SIM_CMD prev;
    private PICSERVO_SIM_CMD next;

    private static int itemCount;
    private static PICSERVO_SIM_CMD first;
    private static PICSERVO_SIM_CMD last;

    private PICSERVO_SIM_CMD(String id) {
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
            private PICSERVO_SIM_CMD current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                PICSERVO_SIM_CMD c = current;
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

    public static PICSERVO_SIM_CMD first() {
        return first;
    }

    public static PICSERVO_SIM_CMD last() {
        return last;
    }

    public PICSERVO_SIM_CMD prev() {
        return this.prev;
    }

    public PICSERVO_SIM_CMD next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of PICSERVO_SIM_CMD, which has "
        + itemCount
        + " elements:");
        PICSERVO_SIM_CMD current = first;
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

        PICSERVO_SIM_CMD current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static PICSERVO_SIM_CMD matchKey(int i) {
        PICSERVO_SIM_CMD O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static PICSERVO_SIM_CMD matchStr(String s) {
        PICSERVO_SIM_CMD O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final PICSERVO_SIM_CMD PICServoSimCmdResetPos = new PICSERVO_SIM_CMD("PICServoSimCmdResetPos");
    public static final PICSERVO_SIM_CMD PICServoSimCmdPos = new PICSERVO_SIM_CMD("PICServoSimCmdPos");
    public static final PICSERVO_SIM_CMD PICServoSimCmdVel = new PICSERVO_SIM_CMD("PICServoSimCmdVel");
}

