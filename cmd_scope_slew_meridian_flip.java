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

public class cmd_scope_slew_meridian_flip extends cmdScopeAltazBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        return parseSecondsAndComment(st);
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        double DecDistance;
        double HADistance;

        cl.t.turnTrackingOffStopAllMotors();
        cl.t.waitForAllMotorsStop();

        DecDistance = units.QTR_REV - cfg.getInstance().current.alt.rad;
        // if not flipped, then scope is on east side of pier facing west, if true, then scope on west side facing east:
        // consequently, travel CW to flipped position, travel CCW in southern hemisphere;
        // if alt<pole (DecDistance>0), then not flipped; altitude stays positive in southern hemisphere;
        HADistance = units.HALF_REV;
        if (cfg.getInstance().latitudeDeg >= 0.) {
            if (DecDistance < 0.)
                HADistance = -units.HALF_REV;
        }
        else
            if (DecDistance >= 0.)
                HADistance = -units.HALF_REV;

        cfg.getInstance().spa.targetPosition = cfg.getInstance().spa.actualPosition + (long) (DecDistance / cfg.getInstance().spa.countToRad);
        cfg.getInstance().spz.backlash.addBacklashToTargetPosition(SERVO_ID.altDec.KEY);

        cfg.getInstance().spz.targetPosition = cfg.getInstance().spz.actualPosition + (long) (HADistance / cfg.getInstance().spz.countToRad);
        cfg.getInstance().spz.backlash.addBacklashToTargetPosition(SERVO_ID.azRa.KEY);

        cfg.getInstance().spa.velRadSec = cfg.getInstance().spa.fastSpeedRadSec;
        cfg.getInstance().spz.velRadSec = cfg.getInstance().spz.fastSpeedRadSec;

        moveToTargetAZ();
        return true;
    }
}

