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

public class cmd_scope_focus_abs extends cmdScopeFocusBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        if (parseFocus(st)) {
            parseMoveTimeSec(st);
            return parseSecondsAndComment(st);
        }
        return false;
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        double deltaFocusRad;

        cl.t.stopMotorSmoothly(SERVO_ID.focus.KEY);
        cl.t.waitForMotorStop(SERVO_ID.focus.KEY);

        cfg.getInstance().spf.targetPosition = focusPosition;
        cfg.getInstance().spf.backlash.addBacklashToTargetPosition(SERVO_ID.focus.KEY);

        cfg.getInstance().spf.cmdDevice = CMD_DEVICE.cmdScopeList;

        if (moveTimeSec > 0.) {
            deltaFocusRad = (focusPosition - cfg.getInstance().spf.actualPosition) * cfg.getInstance().spf.countToRad;
            // assumes instantaneous acceleration
            cfg.getInstance().spf.velRadSec = Math.abs(deltaFocusRad / moveTimeSec);
            cl.t.validVelRadSec(SERVO_ID.focus.KEY);
        }
        else
            cfg.getInstance().spf.velRadSec = cfg.getInstance().spf.fastSpeedRadSec;

        moveToTargetFocus();
        return true;
    }
}

