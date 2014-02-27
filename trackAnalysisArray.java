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
 * circular queue of track analysis rows;
 *
 * trackAnalysis table: circular queue where valid values start at taBegIx and end at taIx
 * truth table: assume array size is 4
 * count of recorded Positions | taIx | taBegIx
 * 0 0 0
 * 1 1 0
 * 2 2 0
 * 3 3 0  <-
 * 0 0 1
 * 1 1 2
 * 2 2 3
 * 3 3 0  <- repeats
 * 0 0 1
 */
public class trackAnalysisArray {
    // circular queue: ~1 minute of tracking data
    private static final int TA_SIZE = 240;
    private trackAnalysis[] ta = new trackAnalysis[TA_SIZE];
    private int taIx;
    private int taBegIx;
    private int count;
    private int id;
    double rms;
    int rmsCount;
    // peak to valley error
    double pv;
    private String filename;
    private String title;
    private String strLine;
    // sized for column titles above
    private static final int COL_SIZE = 18;
    String rms_pv_string;
    String mostRecentString;
    String mostRecentTrackErrorString;

    trackAnalysisArray(int id) {
        for (taIx = 0; taIx < TA_SIZE; taIx++)
            ta[taIx] = new trackAnalysis();
        taIx = 0;
        this.id = id;
        filename = SERVO_ID.matchKey(id) + eString.TRACK_LOG_EXT;
        title = "\ntracking/slew position error array (arcsec) for motor: " + SERVO_ID.matchKey(id);
    }

    void add() {
        int ix = 0;
        double vel;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        ta[taIx].cmdDevice = sp.cmdDevice;

        ta[taIx].v[ix++] = sp.deltaPosRad*units.RAD_TO_ARCSEC;

        vel = sp.velRadSec*units.RAD_TO_ARCSEC;
        if (sp.targetVelDir == ROTATION.CCW)
            vel = -vel;
        ta[taIx].v[ix++] = vel;

        ta[taIx].v[ix++] = astroTime.getInstance().JD*units.DAY_TO_SEC;

        taIx++;
        if (taIx >= TA_SIZE)
            taIx = 0;
        if (taBegIx == taIx) {
            taBegIx++;
            if (taBegIx >= TA_SIZE)
                taBegIx = 0;
        }
        count++;
    }

    void calcRmsPv(int num) {
        int ix;
        double err;
        double sum = 0.;
        double min = 0.;
        double max = 0.;

        rmsCount = 0;
        if (num > count)
            num = count;
        ix = taIx - num;
        if (ix < 0)
            ix += TA_SIZE;
        while (ix != taIx) {
            err = ta[ix].v[0];
            sum += Math.pow(err, 2);
            rmsCount++;
            if (err > max)
                max = err;
            else if (err < min)
                min = err;
            ix++;
            if (ix >= TA_SIZE)
                ix = 0;
        }
        if (rmsCount > 0)
            rms = Math.pow(sum / (double) rmsCount, .5);
        else
            rms = 0.;
        pv = max - min;
    }


    void buildStrLine(int ix) {
        strLine = eString.padString(ta[ix].cmdDevice.toString(), COL_SIZE);
        for (ta[ix].ix = 0; ta[ix].ix < trackAnalysis.MAX_V; ta[ix].ix++)
            strLine += eString.padString(eString.doubleToStringNoGrouping(ta[ix].v[ ta[ix].ix ], 6, 2), COL_SIZE);
    }

    void display() {
        int ix;

        console.stdOutLn(title);
        for (ix = 0; ix < trackAnalysis.MAX_S; ix++)
            console.stdOut(trackAnalysis.s[ix] + eString.TAB);
        console.stdOutLn("");
        ix = taBegIx;
        while (ix != taIx) {
            buildStrLine(ix);
            console.stdOutLn(strLine);
            ix++;
            if (ix >= TA_SIZE)
                ix = 0;
        }
        console.stdOutLn(buildRmsPvString(10));
        console.stdOutLn("end of array");
    }

    String buildRmsPvString(int num) {
        calcRmsPv( num);

        rms_pv_string = "rms "
        + eString.padString(eString.doubleToStringNoGrouping(rms, 6, 2), COL_SIZE)
        + " peak-valley "
        + eString.padString(eString.doubleToStringNoGrouping(pv, 6, 2), COL_SIZE)
        + "\n";

        return rms_pv_string;
    }

    String buildMostRecentString(int num) {
        int ix;

        mostRecentString = title;
        // build column headings
        for (ix = 0; ix < trackAnalysis.MAX_S; ix++)
            mostRecentString += trackAnalysis.s[ix] + eString.TAB;
        mostRecentString += "\n";
        if (num > count)
            num = count;
        ix = taIx - num;
        if (ix < 0)
            ix += TA_SIZE;
        while (ix != taIx) {
            buildStrLine(ix);
            mostRecentString += strLine + "\n";
            ix++;
            if (ix >= TA_SIZE)
                ix = 0;
        }
        mostRecentString += buildRmsPvString(num);
        mostRecentString += "end of array\n";

        return mostRecentString;
    }

    String buildMostRecentTrackErrorString(int num) {
        int ix;

        mostRecentTrackErrorString = "track error and velocity in arcseconds, time in seconds:\n";
        if (num > count)
            num = count;
        ix = taIx - num;
        if (ix < 0)
            ix += TA_SIZE;
        while (ix != taIx) {
            buildStrLine(ix);
            mostRecentTrackErrorString += strLine + "\n";
            ix++;
            if (ix >= TA_SIZE)
                ix = 0;
        }
        mostRecentTrackErrorString += buildRmsPvString(num);
        mostRecentTrackErrorString += "end of tracking errors\n";

        return mostRecentTrackErrorString;
    }

    void displayMostRecentTrackError(int num) {
        console.stdOut(buildMostRecentTrackErrorString(num));
    }

    void displayMostRecent(int num) {
        console.stdOut(buildMostRecentString(num));
    }
}

