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

/**
 * an array or collection of all possible LimitMotion objects, each a list of limitWindows
 */
public class limitMotionCollection {
    boolean enabled;
    // an array of LimitMotion objects, which is an interface
    LimitMotion[] lm;

    limitMotionCollection() {
        enabled = true;

        lm = new LimitMotion[ LIMIT_MOTION_TYPE.size()];
        // build each element of LimitMotion[] based on the LIMIT_MOTION_TYPE collection
        Enumeration eLIMIT_MOTION_TYPE = LIMIT_MOTION_TYPE.elements();
        while (eLIMIT_MOTION_TYPE.hasMoreElements()) {
            LIMIT_MOTION_TYPE lmt = (LIMIT_MOTION_TYPE) eLIMIT_MOTION_TYPE.nextElement();
            System.out.println("adding " + lmt);
            lm[lmt.KEY] = new limitMotionFactory().build(lmt);
        }
    }

    void enable(boolean enabled) {
        this.enabled = enabled;
    }

    void enableAll(boolean enabled) {
        int ix;

        enable(enabled);
        for (ix = 0; ix < LIMIT_MOTION_TYPE.size(); ix++)
            lm[ix].enable(enabled);
    }

    void addLimitWindow(LIMIT_MOTION_TYPE lmt, limitWindow lw) {
        lm[lmt.KEY].addLimitWindow(lw);
    }

    void display() {
        int ix;

        console.stdOutLn("display of limitMotionCollection:");
        for (ix = 0; ix < LIMIT_MOTION_TYPE.size(); ix++)
            lm[ix].display();
        console.stdOutLn("end of limitMotionCollection display");
    }

    void saveToFiles() {
        int ix;

        for (ix = 0; ix < LIMIT_MOTION_TYPE.size(); ix++)
            lm[ix].saveToFile();
    }

    void loadFromFiles() {

        int ix;

        for (ix = 0; ix < LIMIT_MOTION_TYPE.size(); ix++)
            lm[ix].loadFromFile();
    }

    boolean limitExceeded() {
        int ix;

        if (enabled)
            for (ix = 0; ix < LIMIT_MOTION_TYPE.size(); ix++)
                if (lm[ix].limitExceeded()) {
                    console.stdOutLn(lm[ix].limitMotionType().toString() + " limited exceeded");
                    return true;
                }
        return false;
    }

    boolean limitExceeded(double alt, double az) {
        int ix;

        if (enabled)
            for (ix = 0; ix < LIMIT_MOTION_TYPE.size(); ix++)
                if (lm[ix].limitExceeded(alt, az)) {
                    console.stdOutLn(lm[ix].limitMotionType().toString() + " limited exceeded");
                    return true;
                }
        return false;
    }

    void test() {
        limitWindow lw = new limitWindow();

        System.out.println("test of limitMotionCollection");
        System.out.println("building a limitMotion:");
        lw.z = 1.*units.DEG_TO_RAD;
        lw.lowA = 2.*units.DEG_TO_RAD;
        lw.highA = 81.*units.DEG_TO_RAD;
        addLimitWindow(LIMIT_MOTION_TYPE.limit_motion_equat, lw);
        lw.z = 3.*units.DEG_TO_RAD;
        lw.lowA = 4.*units.DEG_TO_RAD;
        lw.highA = 82.*units.DEG_TO_RAD;
        addLimitWindow(LIMIT_MOTION_TYPE.limit_motion_equat, lw);
        display();

        System.out.println("loading limitMotionCollection from files");
        loadFromFiles();
        display();

        System.out.println("checking limitExceeded against limitMotionCollection");
        System.out.println("alt=40, az=160 should be ok, limitExceeded=" + limitExceeded(40.*units.DEG_TO_RAD, 160.*units.DEG_TO_RAD));
        System.out.println("alt=70, az=160 should NOT be ok, limitExceeded=" + limitExceeded(70.*units.DEG_TO_RAD, 160.*units.DEG_TO_RAD));

        System.out.println("end of limitMotionCollection test");
    }
}

