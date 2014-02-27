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
 * common methods and members for the LimitMotion interface;
 * data in file saved in degrees, not radians
 */
public class limitMotionBase {
    LIMIT_MOTION_TYPE lmt;
    String filename;
    boolean enabled;
    java.util.List llw;
    private listLimitWindowComparator lwComparator;
    private limitWindow first;
    private limitWindow last;
    private Iterator it;

    limitMotionBase() {
        llw = new ArrayList();
        lwComparator = new listLimitWindowComparator(LINK_POS_SORT_KEY.azAscend);
        enabled = true;
    }

    public void limitMotionType(LIMIT_MOTION_TYPE lmt) {
        this.lmt = lmt;
        filename = lmt.toString() + eString.LIMIT_MOTION_EXT;
    }

    public LIMIT_MOTION_TYPE limitMotionType() {
        return lmt;
    }

    public void enable(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean enable() {
        return enabled;
    }

    public java.util.List listLimitWindow() {
        return llw;
    }

    public void addLimitWindow(limitWindow lw) {
        llw.add(new limitWindow(lw.z, lw.lowA, lw.highA));
        Collections.sort(llw, lwComparator);
        it = llw.iterator();
        first = (limitWindow) it.next();
        last = first;
        while (it.hasNext())
            last = (limitWindow) it.next();
    }

    boolean limitExceeded(double alt, double az) {
        double lowA;
        double highA;
        double prop;
        double wrapComp;
        double wrapEnd;
        limitWindow start;
        limitWindow end;
        limitWindow prev;

        if (!enabled)
            return false;

        // if no limitWindows defined, then coordinate in question is ok
        if (llw.size() == 0)
            return false;
        // if only 1 limitWindow defined, then check limit based on its low and high alt
        else if (llw.size() == 1)
            if (alt < first.lowA || alt > first.highA)
                return true;
            else
                return false;
        else {
            // find starting and ending limit windows that envelope the coordinate in question;
            // last and first set in addLimitWindow(limitWindow lw) after limitWindows is also sorted;
            it = llw.iterator();
            end = first;
            prev = null;
            // iterate through list until an entry's value exceeds value in question
            while (end.z <= az) {
                if (it.hasNext()) {
                    // we know that there is a next entry so assign prev to this one
                    prev = end;
                    end = (limitWindow) it.next();
                }
                // else ran out of entries, so make end be the first entry
                else {
                    end = first;
                    break;
                }
            }
            // if value < first entry's value or value > last entry's value:
            // add one revolution to the end value so that prop calculation makes sense;
            if (end == first) {
                start = last;
                wrapEnd = units.ONE_REV;
                // set wrapComp based on if value > last entry's value: if not, then
                if (az > last.z)
                    wrapComp = 0.;
                else
                    wrapComp = units.ONE_REV;
            }
            // else normal situation where value is inside two neighboring entries
            else {
                start = prev;
                wrapComp = wrapEnd = 0.;
            }

            // get proportion of starting and ending limitWindow azimuths,
            // ie, if az=2, start=1, end=3, size=5, then prop=(2-1)/(3-1)=1/2;
            // if az=4, start=3, end=1, then prop=(4-3)/(5+1-3)=1/3 (az=4 is 1/3 of the way between 3 and 6);
            // if az=.5, start=3, end=1, then prop=(5+.5-3)/(5+1-3)=5/6 (az=5.5 is 5/6 of way between 3 and 6);
            prop = (wrapComp+az - start.z) / (wrapEnd+end.z - start.z);

            // calc limits
            lowA = start.lowA + prop * (end.lowA - start.lowA);
            highA = start.highA + prop * (end.highA - start.highA);

            /**
             System.out.println("   alt "
             + alt*units.RAD_TO_DEG
             + " prop "
             + prop
             + " lowA "
             + lowA*units.RAD_TO_DEG
             + " highA "
             + highA*units.RAD_TO_DEG);
             */

            // check limits
            if (alt < lowA || alt > highA)
                return true;
            else
                return false;
        }
    }

    public void display() {
        limitWindow lw;

        console.stdOutLn(lmt + ": ");
        it = llw.iterator();
        while (it.hasNext()) {
            lw = (limitWindow) it.next();
            lw.display();
        }
    }

    public void saveToFile() {
        PrintStream output;
        limitWindow lw;

        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(filename)));
            it = llw.iterator();
            while (it.hasNext()) {
                lw = (limitWindow) it.next();
                output.println(lw.buildDisplayString());
            }
            output.close();
        }
        catch (IOException ioe) {
            console.errOut("could not open " + filename);
        }
    }

    // format (in degrees): z: <double>   lowA: <double>   highA: <double>
    public boolean loadFromFile() {
        BufferedReader input;
        limitWindow lw = new limitWindow();
        String s;
        StringTokenizer st;

        try {
            input = new BufferedReader(new FileReader(filename));
            console.stdOutLn("Found " + filename);
            s = input.readLine();
            while (s != null) {
                st = new StringTokenizer(s);
                if (st.countTokens() == 6) {
                    st.nextToken();
                    try {
                        lw.z = Double.parseDouble(st.nextToken()) * units.DEG_TO_RAD;
                    }
                    catch (NumberFormatException nfe) {
                        console.errOut("bad number for z " + filename);
                    }
                    st.nextToken();
                    try {
                        lw.lowA = Double.parseDouble(st.nextToken()) * units.DEG_TO_RAD;
                    }
                    catch (NumberFormatException nfe) {
                        console.errOut("bad number for lowA " + filename);
                    }
                    st.nextToken();
                    try {
                        lw.highA = Double.parseDouble(st.nextToken()) * units.DEG_TO_RAD;
                    }
                    catch (NumberFormatException nfe) {
                        console.errOut("bad number for highA " + filename);
                    }
                    addLimitWindow(lw);
                }
                else
                    console.errOut("limitMotionBase.loadFromFile(): "
                    + lmt.toString()
                    + "bad number of tokens in: "
                    + s);
                s = input.readLine();
            }
            input.close();
            return true;
        }
        catch (IOException ioe) {
            console.stdOutLn("did not find "
            + filename
            + ", no limitMotion polygon built for "
            + lmt);
            return false;
        }
    }

    public void test() {
        limitWindow lw = new limitWindow();

        System.out.println("limitMotionBase tests:");
        limitMotionType(LIMIT_MOTION_TYPE.limit_motion_siteAltaz);

        System.out.println("limitExceeded test:");

        lw.lowA = 20.*units.DEG_TO_RAD;
        lw.highA = 60.*units.DEG_TO_RAD;
        lw.z = 200.*units.DEG_TO_RAD;
        addLimitWindow(lw);

        lw.lowA = 10.*units.DEG_TO_RAD;
        lw.highA = 50.*units.DEG_TO_RAD;
        lw.z = 100.*units.DEG_TO_RAD;
        addLimitWindow(lw);

        lw.lowA = 30.*units.DEG_TO_RAD;
        lw.highA = 70.*units.DEG_TO_RAD;
        lw.z = 300.*units.DEG_TO_RAD;
        addLimitWindow(lw);

        display();

        System.out.println("alt=40, az=30 should be ok, limitExceeded="
        + limitExceeded(40.*units.DEG_TO_RAD, 30.*units.DEG_TO_RAD));

        System.out.println("alt=40, az=160 should be ok, limitExceeded="
        + limitExceeded(40.*units.DEG_TO_RAD, 160.*units.DEG_TO_RAD));

        System.out.println("alt=80, az=160 should NOT be ok, limitExceeded="
        + limitExceeded(80.*units.DEG_TO_RAD, 160.*units.DEG_TO_RAD));

        System.out.println("alt=80, az=330 should NOT be ok, limitExceeded="
        + limitExceeded(80.*units.DEG_TO_RAD, 330.*units.DEG_TO_RAD));

        System.out.println("saving limit motion file test: " + lmt);
        saveToFile();

        System.out.println("loading limit motion file test: " + lmt);
        llw.clear();
        loadFromFile();
        display();
    }
}

