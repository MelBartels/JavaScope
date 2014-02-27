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

public class cmdScopeFocusBase extends cmdScopeBase {
    long focusPosition;
    String eyepieceName;

    boolean parseFocus(StringTokenizer st) {
        try {
            if (st.countTokens() > 0)
                focusPosition = Long.parseLong(st.nextToken());
            else
                return false;
            return true;
        }
        catch (NumberFormatException nfe) {
            console.errOut("bad number for focusPosition in " + cmdTypeScope);
        }
        return false;
    }

    /**
     * set target position or velocity in rad/sec for focus motor before calling
     */
    void moveToTargetFocus() {
        cfg.getInstance().spf.moveCmd = SERVO_MOVE_CMD.posMoveStopCurrent;
        cfg.getInstance().spf.moveNow = true;
        cfg.getInstance().spf.servoCmdProcessed = false;
        cl.t.processMoveCmdControl();
    }

    public boolean cmdInProgress() {
        return (cfg.getInstance().spf.cmdDevice == CMD_DEVICE.cmdScopeList && cfg.getInstance().spf.moveState != SERVO_MOVE_STATE.finished);
    }
}

