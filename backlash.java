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
 * incorporate backlash to move to make, making it one move; if backlash handled at a lower level, then there will be 2
 * moves, one for backlash and one for the original move to make;
 *
 * backlash characterized by:
 *    1. total backlash (backlashArcmin, backlashRad, and backlashCount);
 *    2. amount taken up (actualBacklashRad, actualBacklashCount);
 *    3. direction that the amount is taken up in (class backlash.ROTATION), which is a state;
 *
 * if backlash fully taken up in CW direction, then actualBacklash will be 0,
 * if backlash fully taken up in CCW direction, then actualBacklash will be backlash,
 * so start with backlash fully taken up in CW direction;
 *
 * if considering change in target positions as calculated from coordinate translation routines:
 *    if moving CW, then do nothing;
 *    if moving CCW, then lower target by backlash;
 *    the goal when moving CW is to have zero actualBacklash, therefore, target remains unchanged, ie, if motor is less
 *    than or below or behind scope because of backlash that has yet to be taken up, then motor will move target - Scope
 *    + actualBacklash, and since motor is behind scope by actualBacklash, target does not need to be changed;
 *    the goal when moving CCW is to have actualBacklash equal to backlash, therefore, target needs to be reduced by
 *    backlash, ie, motor should move to target - backlash in order to takeout all backlash in CCW direction;
 *
 *    ie, backlash of 10
 *    motor  scope  actbklsh  move  target  target+bklsh  motordistance  scopedistance  endmotor  endscope  endactbklsh
 *     20     20     0         10    30      30 (30)       10             10             30        30        0
 *     30     30     0        -10    20      10 (20-10)   -20            -10             10        20       10
 *     10     20    10        -10    10       0 (10-10)   -10            -10              0        10       10
 *      0     10    10         10    20      20 (20)       20             10             20        20        0
 *     20     20     0        -10    10       0 (10-10)   -20            -10
 *        move stopped before completion: motor distance traveled before stop is -7:     13        20        7
 *     13     20     7         10    30      30 (30)       17             10             30        30        0
 *     30     30     0        -10    20      10 (20-10)   -20            -10
 *        move stopped before completion: motor distance traveled before stop is -7:     23        30        7
 *     23     30     7        -10    20      10 (20-10)   -13            -10             10        20       10
 *
 * if considering change in motor Positions as calculated from coordinate translation routines then altered to include
 * error corrections by setServoPositionToCurrentAltaz():
 *    setServoPositionToCurrentAltaz() is unaware of direction of motion, subtracting actual backlash regardless of
 *    direction, so is not aware when backlash should be taken up;
 *    this results in backlash being taken up when motor reverses direction as follows:
 *       at first motor is commanded to move at standard tracking rate,
 *       however, servo.readStatus() captures the motion by increasing the actualBacklash, thus reporting no motor
 *          movement, even though motor is moving,
 *       the servo feedback loop control algorithm sees the increasing discrepancy between desired position and motor
 *          position and responds by increasing the commanded velocity,
 *       this builds to a crescendo until backlash fully taken up,
 *       at which time, the control algorithm commands the previously apparently unmoving motor to catch up to the
 *          desired target position;
 *
 * consequently, the control algorithm needs to be informed ahead of time of the backlash to take up, hence, the
 *    following code which is called immediately prior to calling the particular tracking algorithm in use:
 *
 * based on consistent direction, the backlash to be taken up is added to the position difference sent to the
 *   tracking algorithm;
 *
 * if considering change in target positions as calculated from altazimuth move routines, simply subtract the required
 * backlash to be taken up to the target position;
 *
 * don't add to backlash direction array unless motor is going to move a sufficient amount (arcsec);
 */
public class backlash {
    // direction of backlash takeup must be consistent for this number of consecutive calls
    private static final int SAME_READ_COUNT = 2;
    private ROTATION[] dir = new ROTATION[SAME_READ_COUNT];
    private int dirIx;
    boolean active;
    ROTATION Rotation = ROTATION.CW;

    backlash() {
        int ix;

        for (ix = 0; ix < SAME_READ_COUNT; ix++)
            dir[ix] = ROTATION.CW;
        active = true;
    }

    /**
     * routine to use when scope tracking, called from track.moveToTargetEquat()
     */
    void addBacklashToDeltaPosRads(int id) {
        if (active)
            addBacklashToDeltaPosRadsSubr(id);
    }

    private void addBacklashToDeltaPosRadsSubr(int id) {
        int ix;
        boolean backlashUnanimous;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        if (Math.abs(sp.deltaPosRad) > units.ARCSEC_TO_RAD) {
            sp.backlash.dirIx++;
            if (sp.backlash.dirIx == backlash.SAME_READ_COUNT)
                sp.backlash.dirIx = 0;
            // .targetVelDir set in both velocity and Positional moves, though not needed for the later - see setTargetVelDirFromPosition()
            sp.backlash.dir[sp.backlash.dirIx] = sp.targetVelDir;
        }

        backlashUnanimous = true;
        for (ix = 1; ix < backlash.SAME_READ_COUNT; ix++)
            if (sp.backlash.dir[ix] != sp.backlash.dir[0]) {
                backlashUnanimous = false;
                break;
            }

        if (backlashUnanimous)
            sp.backlash.Rotation = sp.backlash.dir[0];

        if (sp.backlash.Rotation == ROTATION.CW) {
            sp.deltaPosRad += sp.actualBacklashRad;
            sp.deltaPos1Rad += sp.actualBacklashRad;
            sp.deltaPos2Rad += sp.actualBacklashRad;
        }
        else
            if (sp.backlash.Rotation == ROTATION.CCW) {
                sp.deltaPosRad -= sp.backlashRad - sp.actualBacklashRad;
                sp.deltaPos1Rad -= sp.backlashRad - sp.actualBacklashRad;
                sp.deltaPos2Rad -= sp.backlashRad - sp.actualBacklashRad;
            }
    }

    /**
     * routine to use for altazimuth moves and when scope is not tracking;
     * if backlash fully taken out in CW, then actual = 0, if fully taken out in CCW, then actual = max;
     * added in cmd_scope_slew_abs_altaz.process(),
     *    cmd_scope_slew_off_altaz.process(),
     *    cmd_scope_slew_meridian_flip.process(),
     *    cmd_scope_slew_home.process()
     *    cmd_scope_focus_abs.process()
     *    cmd_scope_focus_rel.process()
     *    cmd_scope_focus_eyepiece.process()
     */
    void addBacklashToTargetPosition(int id) {
        if (active)
            addBacklashToTargetPositionSubr(id);
    }

    private void addBacklashToTargetPositionSubr(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        if (sp.targetPosition >= sp.actualPosition)
            sp.targetPosition += sp.actualBacklashCount;
        else
            sp.targetPosition -= sp.backlashCount - sp.actualBacklashCount;
    }

    /**
     * used to set new backlash takeup direction after a move commanded by handpad or similar;
     * prevents to/fro backlash takeup (eg, move was CCW, handpad moved CW, tracking starts CW);
     * needed when handpad moving opposite direction compared to tracking, otherwise backlash will
     * merely be taken up resulting in appearance at eyepiece of tracking being stopped during
     * the handpad move (no contrary movement will show at eyepiece unless handpad move long enough
     * to fully encompase backlash);
     */
    void setBacklashDir(int id, ROTATION dir) {
        if (active)
            setBacklashDirSubr(id, dir);
    }

    private void setBacklashDirSubr(int id, ROTATION dir) {
        int ix;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        for (ix = 0; ix < SAME_READ_COUNT; ix++)
            sp.backlash.dir[ix] = dir;
    }
}

