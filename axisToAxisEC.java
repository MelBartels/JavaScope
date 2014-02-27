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
 * calculate error correction value for an axis based on current coordinate of an axis for AltAzEC,
 * calculate altitude correction based on azimuth coordinate: currentCoord is cfg.getInstance().current.az.rad for AltAltEC,
 * calculate altitude correction based on altitude coordinate: currentCoord is cfg.getInstance().current.alt.rad for AzAzEC,
 * calculate azimuth correction based on azimuth coordinate: currentCoord is cfg.getInstance().current.az.rad
 *
 * map negative coordinate range into positive space, ie, alt of -30 becomes alt of 330
 *
 * array of axis vs axis error correction values with EC_RESOLUTION_DEG spacing between indexes:
 * if resolution of indeces is 10 degrees, then each index represents an increment of 10 degrees
 *    so that [0] represents 0 deg, [1] represents 10 deg, and so forth;
 * values stored at each index are the error correcting values, ie if ec[0]==4 and ec[1]==2,
 *    then at index of 0 which is 0 deg, error correction value is 4 deg,
 *    and at index of 1 which is 10 deg, error correction value is 2 deg;
 *
 * EC resolution of 1 deg will adequately resolve errors that are 2 deg in size;
 */
public class axisToAxisEC {
    private BufferedReader input;
    private PrintStream output;
    AXIS_TO_AXIS_EC axisToAxisName;
    String filename;
    boolean active;
    static final int EC_RESOLUTION_DEG = 1;
    static final int EC_SIZE = (360/EC_RESOLUTION_DEG);
    double[] ec = new double[EC_SIZE];
    double ecRad;

    axisToAxisEC(AXIS_TO_AXIS_EC axisToAxisName) {
        this.axisToAxisName = axisToAxisName;
        filename = axisToAxisName.toString() + eString.EC_EXT;
        loadFromFile();
    }

    boolean loadFromFile() {
        int deg = 0;
        double ecValue = 0.;
        int ix;
        String s;
        StringTokenizer st;

        try {
            input = new BufferedReader(new FileReader(filename));
            console.stdOutLn("Found " + filename);
            s = input.readLine();
            while (s != null) {
                st = new StringTokenizer(s);
                if (st.countTokens() == 2) {
                    try {
                        deg = Integer.parseInt(st.nextToken());
                    }
                    catch (NumberFormatException nfe) {
                        console.errOut("bad number for deg in " + filename);
                    }
                    try {
                        ecValue = Double.parseDouble(st.nextToken());
                    }
                    catch (NumberFormatException nfe) {
                        console.errOut("bad number for ecValue in " + filename);
                    }
                    ix = deg / EC_RESOLUTION_DEG;
                    if (ix < 0 || ix >= EC_SIZE)
                        common.badExit("ix of "
                        + ix
                        + " is out of bounds in LoadECFilename()");
                    ec[ix] = ecValue;
                }
                s = input.readLine();
            }
            input.close();
            return true;
        }
        catch (IOException ioe) {
            for (ix = 0; ix < EC_SIZE; ix++)
                ec[ix] = 0.;
            console.errOut("did not find "
            + filename
            + ", creating new one with zeroed values");
            saveToFile();
            return false;
        }
    }

    void saveToFile() {
        int ix;

        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(filename)));

            for (ix = 0; ix < EC_SIZE; ix++)
                output.println(ix*EC_RESOLUTION_DEG
                + "   "
                + ec[ix]);

            output.close();
        }
        catch (IOException ioe) {
            console.errOut(ioe.toString());
            ioe.printStackTrace();
            System.err.println("could not open " + filename);
        }
    }

    void setECValue(double currentCoord) {
        int ixA, ixB;
        double dblIx, frac;

        currentCoord = eMath.validRad(currentCoord);
        dblIx = currentCoord*units.RAD_TO_DEG/EC_RESOLUTION_DEG + .5;
        while (dblIx >= EC_SIZE)
            dblIx -= EC_SIZE;
        while (dblIx < 0)
            dblIx += EC_SIZE;
        ixA = (int) dblIx;
        if (ixA < 0 || ixA >= EC_SIZE)
            common.badExit("Bad ixA of "
            + ixA
            + " in setECValue()");
        ixB = ixA + 1;
        if (ixB >= EC_SIZE)
            ixB = 0;
        frac = dblIx - ixA;
        ecRad = ec[ixA] + frac * (ec[ixB] - ec[ixA]);
        ecRad *= units.DEG_TO_RAD;
    }

    /**
     * return sort based on (second) axis
     */
    private LINK_POS_SORT_KEY selectSortKey() {
        if (axisToAxisName == AXIS_TO_AXIS_EC.altAz)
            return LINK_POS_SORT_KEY.azAscend;
        else if (axisToAxisName == AXIS_TO_AXIS_EC.altAlt)
            return LINK_POS_SORT_KEY.absAltAscend;
        else if (axisToAxisName == AXIS_TO_AXIS_EC.azAz)
            return LINK_POS_SORT_KEY.azAscend;
        else {
            common.badExit(" unhandled AXIS_TO_AXIS_EC of " + axisToAxisName + " in selectSortKey()");
            return LINK_POS_SORT_KEY.azAscend;
        }
    }

    /**
     * return coordinate of (second) axis
     */
    private double returnCoordRad(position p) {
        double rValue = 0.;

        if (axisToAxisName == AXIS_TO_AXIS_EC.altAz)
            rValue = p.az.rad;
        else if (axisToAxisName == AXIS_TO_AXIS_EC.altAlt)
            rValue = p.alt.rad;
        else if (axisToAxisName == AXIS_TO_AXIS_EC.azAz)
            rValue = p.az.rad;
        else
            common.badExit(" unhandled AXIS_TO_AXIS_EC of "
            + axisToAxisName
            + " in returnCoordRad()");
        rValue = eMath.validRad(rValue);
        return rValue;
    }

    /**
     * return error of (first) axis based on coordinate of (second) axis
     */
    private double returnErrRad(position p) {
        if (axisToAxisName == AXIS_TO_AXIS_EC.altAz)
            return p.azErr.a;
        else if (axisToAxisName == AXIS_TO_AXIS_EC.altAlt)
            return p.azErr.a;
        else if (axisToAxisName == AXIS_TO_AXIS_EC.azAz)
            return p.azErr.z;
        else {
            common.badExit(" unhandled AXIS_TO_AXIS_EC of "
            + axisToAxisName
            + " in returnErrRad()");
            return p.azErr.a;
        }
    }

    void build_EC_FromAnalysisErrorsInMemory(listPosition lp) {
        LINK_POS_SORT_KEY key;
        double coordRad;
        int ix;
        position lowListPosition;
        position highListPosition;
        Iterator it;
        double lowSep, highSep;

        if (lp.size() > 0) {
            lp.sort(selectSortKey());

            for (ix = 0; ix < EC_SIZE; ix++) {
            /**
             * set high link (axis value that ec[] represents that is immediately higher than coordinate);
             * if value >= highest entry, then set high link to first entry;
             * low link (axis value that ec[] represents that is immediately lower than coordinate)
             *    will be previous entry;
             * if high link is first entry, then set low link to last entry;
             * ie, if EC array size is 3 and analysis file has 2 entries: pos 1.5, err=0, and pos=2.5, err=2,
             *   then for EC index 0: low entry will be 2.5:2 pair and high entry will be 1.5:0 pair,
             *      highSep = 1.5-0 = 1.5 and lowSep = 0-2.5 = normalized to .5
             *   then for EC index 1: low entry will be 2.5:2 pair and high entry will be 1.5:0 pair,
             *      highSep = .5-0 = .5 and lowSep = 1-2.5 = normalized to 1.5
             *   then for EC index 2: low entry will be 1.5:0 pair and high entry will be 2.5:2 pair,
             *      highSep = 2.5-2 = .5 and lowSep = 2-1.5 = .5
             *   so EC table will look like:
             *      0 : 1.5
             *      1 : .5
             *      2 : 1
             * this works if there is enough resolution in the EC (size large enough) to capture the
             * error highs and lows: an EC resolution of 1 deg will adequately resolve 2 deg features
             */
                lowListPosition = highListPosition = null;
                it = lp.iterator();
                while (it.hasNext()) {
                    highListPosition = (position) it.next();
                    if (returnCoordRad(highListPosition) < ix*EC_RESOLUTION_DEG*units.DEG_TO_RAD)
                        lowListPosition = highListPosition;
                    else
                        break;
                }
                // if ran out of entries while value < highest entry, then value must be > highest entry,
                // therefore set high link to first entry
                if (lowListPosition == highListPosition)
                    highListPosition = lp.get(0);
                // if high link is first entry then set low link to last entry
                if (highListPosition == lp.get(0))
                    lowListPosition = lp.get(lp.size()-1);

                // set coordinate distances to ec index
                highSep = returnCoordRad(highListPosition) - ix*EC_RESOLUTION_DEG*units.DEG_TO_RAD;
                highSep = eMath.validRad(highSep);
                lowSep = ix*EC_RESOLUTION_DEG*units.DEG_TO_RAD - returnCoordRad(lowListPosition);
                lowSep = eMath.validRad(lowSep);

                // set ec[] value based on interpolation;
                // total separation is lowSep + highSep, so position multiplier (from 0 to 1) along line
                // between the low point and the high point is low/total separation == low/(low+high) where total=low+high
                ec[ix] = units.RAD_TO_DEG * (returnErrRad(lowListPosition) + (returnErrRad(highListPosition) -
                returnErrRad(lowListPosition)) * lowSep / (lowSep + highSep));
                /**
                console.stdOutLn("\nIx="
                 + ix
                 + " HighCoord="
                 + returnCoordRad(highListPosition)*units.RAD_TO_DEG
                 + " LowCoord="
                 + returnCoordRad(lowListPosition)*units.RAD_TO_DEG
                 + " lowSep="
                 + lowSep*units.RAD_TO_DEG
                 + " highSep="
                 + highSep*units.RAD_TO_DEG
                 + " LowErr="
                 + returnErrRad(lowListPosition)*units.RAD_TO_DEG
                 + " HighErr="
                 + returnErrRad(highListPosition)*units.RAD_TO_DEG);
                 */
            }
        }
    }
}

