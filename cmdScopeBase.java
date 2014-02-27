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
 * common methods and properties for cmdScope
 */
public class cmdScopeBase {
    CMD_SCOPE cmdTypeScope;
    cmdScopeList cl;
    private cmdScope prev;
    private cmdScope next;
    double sec;
    String comment;
    boolean cmdInProgress;
    double moveTimeSec;
    int i;
    double d;
    String LX200str;
    String cmdString;

    public void cmdTypeScope(CMD_SCOPE cmdTypeScope) {
        this.cmdTypeScope = cmdTypeScope;
    }

    public CMD_SCOPE cmdTypeScope() {
        return cmdTypeScope;
    }

    public void prev(cmdScope c) {
        prev = c;
    }

    public cmdScope prev() {
        return prev;
    }

    public void next(cmdScope c) {
        next = c;
    }

    public cmdScope next() {
        return next;
    }

    public double sec() {
        return sec;
    }

    public String LX200str() {
        return LX200str;
    }

    boolean parseSecondsAndComment(StringTokenizer st) {
        String s;

        sec = 0.;
        comment = "";
        
        if (st.countTokens() > 0) {
            s = st.nextToken();
            try {
                sec = Double.parseDouble(s);
            }
            catch (NumberFormatException nfe) {
                comment += s;
            }
        }
        comment += common.tokensToString(st);
        return true;
    }

    boolean parseInt(StringTokenizer st) {
        try {
            if (st.countTokens() > 0)
                i = Integer.parseInt(st.nextToken());
            return true;
        }
        catch (NumberFormatException nfe) {
            console.errOut("bad number for " + cmdTypeScope);
        }
        return false;
    }

    boolean parseDouble(StringTokenizer st) {
        try {
            if (st.countTokens() > 0)
                d = Double.parseDouble(st.nextToken());
            return true;
        }
        catch (NumberFormatException nfe) {
            console.errOut("bad number for " + cmdTypeScope);
        }
        return false;
    }

    void parseMoveTimeSec(StringTokenizer st) {
        moveTimeSec = 0.;
        try {
            if (st.countTokens() > 0)
                moveTimeSec = Double.parseDouble(st.nextToken());
        }
        catch (NumberFormatException nfe) { }
    }

    public boolean process(cmdScopeList cl) {
        this.cl = cl;
        return true;
    }

    public boolean cmdInProgress() {
        return cmdInProgress;
    }

    public String buildCmdString() {
        cmdString = cmdTypeScope
        + " sec: "
        + sec
        + " comment: "
        + comment
        + "\n";

        return cmdString;
    }
}

