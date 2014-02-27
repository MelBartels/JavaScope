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

public class cmd_scope_slew_abs_altaz extends cmdScopeAltazBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        if (parseAZ(st)) {
            parseMoveTimeSec(st);
            return parseSecondsAndComment(st);
        }
        return false;
    }

    public boolean process(cmdScopeList cl) {
        double distanceRad;
        
        super.process(cl);
        cl.t.turnTrackingOffStopAllMotors();
        cl.t.waitForAllMotorsStop();

        if (p.alt.rad == 9999.)
            p.alt.rad = cfg.getInstance().current.alt.rad;
        if (p.az.rad == 9999.)
            p.az.rad = cfg.getInstance().current.az.rad;

        // given current = servoParm[].actualPosition * servoParm[].countToRad + servoParm[].currentPositionOffsetRad :
        // so current = actual + offset; actual = current - offset, or, target = new coordinates - offset
        distanceRad = p.alt.rad - cfg.getInstance().spa.currentPositionOffsetRad;
        distanceRad = eMath.validRadPi(distanceRad);
        cfg.getInstance().spa.targetPosition = (long) (distanceRad / cfg.getInstance().spa.countToRad);
        cfg.getInstance().spa.backlash.addBacklashToTargetPosition(SERVO_ID.altDec.KEY);

        distanceRad = p.az.rad - cfg.getInstance().spz.currentPositionOffsetRad;
        distanceRad = eMath.validRadPi(distanceRad);
        cfg.getInstance().spz.targetPosition = (long) (distanceRad / cfg.getInstance().spz.countToRad);
        cfg.getInstance().spz.backlash.addBacklashToTargetPosition(SERVO_ID.azRa.KEY);

        cfg.getInstance().spa.cmdDevice = CMD_DEVICE.cmdScopeList;
        cfg.getInstance().spz.cmdDevice = CMD_DEVICE.cmdScopeList;

        if (moveTimeSec > 0.) {
            // assumes instantaneous acceleration
            cfg.getInstance().spa.velRadSec = Math.abs((p.alt.rad-cfg.getInstance().current.alt.rad) / moveTimeSec);
            cl.t.validVelRadSec(SERVO_ID.altDec.KEY);
            cfg.getInstance().spz.velRadSec = Math.abs((p.az.rad-cfg.getInstance().current.az.rad) / moveTimeSec);
            cl.t.validVelRadSec(SERVO_ID.azRa.KEY);
        }
        else {
            cfg.getInstance().spa.velRadSec = cfg.getInstance().spa.fastSpeedRadSec;
            cfg.getInstance().spz.velRadSec = cfg.getInstance().spz.fastSpeedRadSec;
        }

        moveToTargetAZ();
        return true;
    }
}

