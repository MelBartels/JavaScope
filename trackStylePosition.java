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
 * this tracking style uses the trajectory class to calculate a trapezoidal position move;
 * take into account drift in target position (assume final vel does not change) that occurs due to
 * the time it takes to complete the position move (position move always ends with final vel of zero);
 * conclusion: this style included for curiosity's sake: this trackStyle not recommended
 */
public class trackStylePosition extends trackStyleBase implements TrackStyle {
    public void track(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        // if motor stopped then start a new position move
        if (sp.cmdDevice == CMD_DEVICE.none) {
            trackCalcDeltaPosVel(id);
            // calculate in radians and seconds
            trajectory.trajFromAccelDistanceMaxVelBegVelEndVelFirstTrajZeroEndVel(sp.traj,
            sp.calcAccelDegSecSecFromAcceleration()*units.DEG_TO_RAD,
            sp.deltaPos1Rad,
            sp.fastSpeedRadSec,
            0.,
            sp.deltaPos1_2VelRadSec);

            sp.targetPosition = (long) ((double) sp.actualPosition +
            sp.traj.totalDistance / sp.countToRad);

            sp.displayPositionStart = true;
            sp.cmdDevice = CMD_DEVICE.trackByPos;
            sp.velRadSec = sp.fastSpeedRadSec;
            // standard way to start a position move: if necessary, commands and waits for motor stop before starting position move
            sp.moveCmd = SERVO_MOVE_CMD.posMoveStopCurrent;
            sp.moveNow = true;
            sp.servoCmdProcessed = false;
        }
        // else if motor tracking, bring it to a full stop before allowing position TrackStyle
        else
            if (sp.cmdDevice == CMD_DEVICE.trackByVel) {
                sp.moveCmd = SERVO_MOVE_CMD.stopMoveSmoothly;
                sp.servoCmdProcessed = false;
            }
    }

    public void displayTrackingVars(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        displayTrackingVarsHdr(id);

        console.stdOutLn("Delta\" " + sp.deltaPosRad*units.RAD_TO_ARCSEC);
        if (sp.displayPositionStart) {
            console.stdOutLn("position move started " + sp.traj.totalDistance*units.RAD_TO_ARCSEC + "\"");
            sp.displayPositionStart = false;
        }
    }
}

