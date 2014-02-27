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
 * singleton history of all commands executed, ie, 'trackon', 'quit'
 */
public class cmdHistory {
    final String title = "cmd history";
    final int HISTORY_SIZE = 100;
    String[] history = new String[HISTORY_SIZE];
    String mostRecentString;
    int chIx;
    int begIx;
    int count;
    private static cmdHistory INSTANCE;

    private cmdHistory() {}

    public static cmdHistory getInstance() {
        if (INSTANCE == null) {
            synchronized(cmdHistory.class) {
                if (INSTANCE == null)
                    INSTANCE = new cmdHistory();
            }
        }
        return INSTANCE;
    }

    void add(String s) {
        history[chIx] = new String(s);

        chIx++;
        if (chIx >= HISTORY_SIZE)
            chIx = 0;
        if (begIx == chIx) {
            begIx++;
            if (begIx >= HISTORY_SIZE)
                begIx = 0;
        }
        count++;
    }

    void display() {
        int ix;

        console.stdOutLn(title + ":");
        ix = begIx;
        while (ix != chIx) {
            console.stdOutLn(history[ix]);
            ix++;
            if (ix >= HISTORY_SIZE)
                ix = 0;
        }
        console.stdOutLn("end of " + title);
    }

    String buildMostRecentString(int num) {
        int ix;
        int localChIx = chIx;

        if (num > HISTORY_SIZE)
            num = HISTORY_SIZE;
        if (num > count)
            num = count;
        ix = localChIx - num;
        if (ix < 0)
            ix += HISTORY_SIZE;

        mostRecentString = title + ":\n";

        while (ix != localChIx) {
            mostRecentString += history[ix];
            ix++;
            if (ix >= HISTORY_SIZE)
                ix = 0;
        }
        mostRecentString += "end of "
        + title
        + "\n";

        return mostRecentString;
    }

    void displayMostRecent(int num) {
        console.stdOut(buildMostRecentString(num));
    }
}

