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
 * log of accumulated guiding corrections
 */
public class logAccumGuideResults {
    // circular queue: ~1 minute of guiding data
    private final int LAG_SIZE = 200;
    logAccumGuide[] lag = new logAccumGuide[LAG_SIZE];
    int begIx;
    int lagIx;
    private int count;
    private int id;
    private String filename;
    private String title;
    String mostRecentString;

    logAccumGuideResults(int id) {
        for (lagIx = 0; lagIx < LAG_SIZE; lagIx++)
            lag[lagIx] = new logAccumGuide();
        lagIx = 0;
        this.id = id;
        filename = SERVO_ID.matchKey(id) + eString.ACCUM_GUIDE_LOG_EXT;
        title = "\nLog of accumulated guide in arcseconds and seconds of time\n";
    }

    void add(double accumGuideRad, double sidT) {
        lag[lagIx].accumGuideRad = accumGuideRad;
        lag[lagIx].sidT = sidT;

        lagIx++;
        if (lagIx >= LAG_SIZE)
            lagIx = 0;
        if (begIx == lagIx) {
            begIx++;
            if (begIx >= LAG_SIZE)
                begIx = 0;
        }
        count++;
    }

    void display() {
        int ix;

        console.stdOutLn(title);
        ix = begIx;
        while (ix != lagIx) {
            console.stdOutLn(buildString(ix));
            ix++;
            if (ix >= LAG_SIZE)
                ix = 0;
        }
        console.stdOutLn("end of log");
    }

    String buildString(int ix) {
        return lag[ix].accumGuideRad*units.RAD_TO_ARCSEC
        + " "
        + lag[ix].sidT*units.RAD_TO_SEC;
    }

    String buildMostRecentString(int num) {
        int ix;

        mostRecentString = title;

        if (num > count)
            num = count;
        ix = lagIx - num;
        if (ix < 0)
            ix += LAG_SIZE;
        while (ix != lagIx) {
            mostRecentString += buildString(ix) + "\n";
            ix++;
            if (ix >= LAG_SIZE)
                ix = 0;
        }
        mostRecentString += "end of log\n";

        return mostRecentString;
    }

    void displayMostRecent(int num) {
        console.stdOut(buildMostRecentString(num));
    }
}

