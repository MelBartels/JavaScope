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

public class cmd_scope_slew_home extends cmdScopeAltazBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        return parseSecondsAndComment(st);
    }

    public boolean process(cmdScopeList cl) {
        double distanceRad;
        
        super.process(cl);
        cl.t.turnTrackingOffStopAllMotors();
        cl.t.waitForAllMotorsStop();

        distanceRad = cfg.getInstance().spa.homeRad - cfg.getInstance().spa.currentPositionOffsetRad;
        distanceRad = eMath.validRadPi(distanceRad);
        cfg.getInstance().spa.targetPosition = (long) (distanceRad / cfg.getInstance().spa.countToRad);
        cfg.getInstance().spa.backlash.addBacklashToTargetPosition(SERVO_ID.altDec.KEY);

        distanceRad = cfg.getInstance().spz.homeRad - cfg.getInstance().spz.currentPositionOffsetRad;
        distanceRad = eMath.validRadPi(distanceRad);
        cfg.getInstance().spz.targetPosition = (long) (distanceRad / cfg.getInstance().spz.countToRad);
        cfg.getInstance().spz.backlash.addBacklashToTargetPosition(SERVO_ID.azRa.KEY);

        cfg.getInstance().spa.velRadSec = cfg.getInstance().spa.fastSpeedRadSec;
        cfg.getInstance().spz.velRadSec = cfg.getInstance().spz.fastSpeedRadSec;

        moveToTargetAZ();
        return true;
    }
}

