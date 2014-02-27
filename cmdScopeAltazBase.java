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

public class cmdScopeAltazBase extends cmdScopePositionBase {
    // 9999 means use current altaz coord at time of command execution,
    // so if 9999, then simply store it
    boolean parseAZ(StringTokenizer st) {
        p = new position("cmdScopeAltazBase");
        try {
            if (st.countTokens() > 0)
                p.alt.rad = Double.parseDouble(st.nextToken());
            else
                return false;
            if (p.alt.rad != 9999.)
                p.alt.rad *= units.DEG_TO_RAD;
            if (st.countTokens() > 0)
                p.az.rad = Double.parseDouble(st.nextToken());
            else
                return false;
            if (p.az.rad != 9999.)
                p.az.rad *= units.DEG_TO_RAD;
            return true;
        }
        catch (NumberFormatException nfe) {
            console.errOut("bad number for " + cmdTypeScope);
        }
        return false;
    }

    /**
     * set target position or velocity in rad/sec for both alt and az motors before calling
     */
    void moveToTargetAZ() {
        cfg.getInstance().spa.cmdDevice = CMD_DEVICE.cmdScopeList;
        cfg.getInstance().spz.cmdDevice = CMD_DEVICE.cmdScopeList;

        cfg.getInstance().spa.moveCmd = cfg.getInstance().spz.moveCmd = SERVO_MOVE_CMD.posMoveStopCurrent;
        cfg.getInstance().spa.moveNow = cfg.getInstance().spz.moveNow = true;
        cfg.getInstance().spa.servoCmdProcessed = cfg.getInstance().spz.servoCmdProcessed = false;
        cl.t.processMoveCmdControl();
    }

    public boolean cmdInProgress() {
        if (cfg.getInstance().spa.cmdDevice != CMD_DEVICE.cmdScopeList
        || cfg.getInstance().spz.cmdDevice != CMD_DEVICE.cmdScopeList)
            return false;
        if (cl.t.checkMotorMoveCompleteSetCmdDeviceNone(SERVO_ID.altDec.KEY)
        || cl.t.checkMotorMoveCompleteSetCmdDeviceNone(SERVO_ID.azRa.KEY))
            return false;
        return true;
    }
}

