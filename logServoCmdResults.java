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
 * a log of servo commands: this class holds a single log entry;
 * see trackAnalysis class for further comments
 */
public class logServoCmdResults {
    // circular queue: ~1 minute of servo commands
    private final int LSC_SIZE = 512;
    private logServoCmd[] LSC = new logServoCmd[LSC_SIZE];
    private int begIx;
    private int lscIx;
    private int count;
    private String filename;
    private String title;
    String mostRecentString;

    logServoCmdResults() {
        for (lscIx = 0; lscIx < LSC_SIZE; lscIx++)
            LSC[lscIx] = new logServoCmd();
        begIx = lscIx = count = 0;
        filename = eString.PGM_NAME + eString.SERVO_CMD_LOG_EXT;
        title = "\nLog of servo command results\nServoID - Command - StatusResult\n";
    }

    void add(int id, byte cmd, boolean readStatusSuccessful) {
        LSC[lscIx].id = id;
        LSC[lscIx].cmd = cmd;
        LSC[lscIx].readStatusSuccessful = readStatusSuccessful;

        lscIx++;
        if (lscIx >= LSC_SIZE)
            lscIx = 0;
        if (begIx == lscIx) {
            begIx++;
            if (begIx >= LSC_SIZE)
                begIx = 0;
        }
        count++;
    }

    void display() {
        int ix;

        console.stdOutLn(title);
        ix = begIx;
        while (ix != lscIx) {
            console.stdOutLn(buildString(ix));
            ix++;
            if (ix >= LSC_SIZE)
                ix = 0;
        }
        console.stdOutLn("end of log");
    }

    String buildString(int ix) {
        return SERVO_ID.matchKey(LSC[ix].id)
        + " "
        + servoCmdTranslator.getString(LSC[ix].cmd)
        + " "
        + (LSC[ix].readStatusSuccessful?"ok":"BAD");
    }

    String buildMostRecentString(int num) {
        int ix;

        mostRecentString = title;
        if (num > count)
            num = count;
        ix = lscIx - num;
        if (ix < 0)
            ix += LSC_SIZE;
        while (ix != lscIx) {
            mostRecentString += buildString(ix) + "\n";
            ix++;
            if (ix >= LSC_SIZE)
                ix = 0;
        }
        mostRecentString += "end of log\n";

        return mostRecentString;
    }

    void displayMostRecent(int num) {
        console.stdOut(buildMostRecentString(num));
    }

    void saveToFile() {
        int ix;
        PrintStream output;

        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(filename)));
            output.println(title);
            ix = begIx;
            while (ix != lscIx) {
                output.println(buildString(ix));
                ix++;
                if (ix >= LSC_SIZE)
                    ix = 0;
            }
            output.close();
        }
        catch (IOException ioe) {
            console.errOut("could not open " + filename);
        }
    }
}

