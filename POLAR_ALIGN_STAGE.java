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

public final class POLAR_ALIGN_STAGE {
    private String id;
    public final int KEY;
    private POLAR_ALIGN_STAGE prev;
    private POLAR_ALIGN_STAGE next;

    private static int itemCount;
    private static POLAR_ALIGN_STAGE first;
    private static POLAR_ALIGN_STAGE last;

    private POLAR_ALIGN_STAGE(String id) {
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
            private POLAR_ALIGN_STAGE current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                POLAR_ALIGN_STAGE c = current;
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

    public static POLAR_ALIGN_STAGE first() {
        return first;
    }

    public static POLAR_ALIGN_STAGE last() {
        return last;
    }

    public POLAR_ALIGN_STAGE prev() {
        return this.prev;
    }

    public POLAR_ALIGN_STAGE next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of POLAR_ALIGN_STAGE, which has "
        + itemCount
        + " elements:");
        POLAR_ALIGN_STAGE current = first;
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

        POLAR_ALIGN_STAGE current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static POLAR_ALIGN_STAGE matchKey(int i) {
        POLAR_ALIGN_STAGE O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static POLAR_ALIGN_STAGE matchStr(String s) {
        POLAR_ALIGN_STAGE O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final POLAR_ALIGN_STAGE load3Stars = new POLAR_ALIGN_STAGE("load3Stars");
    public static final POLAR_ALIGN_STAGE gotoStar1 = new POLAR_ALIGN_STAGE("gotoStar1");
    public static final POLAR_ALIGN_STAGE resetToStar1GotoStar2 = new POLAR_ALIGN_STAGE("resetToStar1GotoStar2");
    public static final POLAR_ALIGN_STAGE calcPolarOffsetGotoRevisedStar3 = new POLAR_ALIGN_STAGE("calcPolarOffsetGotoRevisedStar3");
    public static final POLAR_ALIGN_STAGE adjustBasedOnStar3 = new POLAR_ALIGN_STAGE("adjustBasedOnStar3");
}

