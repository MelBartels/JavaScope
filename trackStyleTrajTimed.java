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
 * this tracking style uses the trajectory class to build a trajectory, then sends velocity commands at particular
 * times based on the trajectory calculated;
 * conclusion: handpad move still needs to be handled: this trackStyle not recommended
 */
public class trackStyleTrajTimed extends trackStyleBase implements TrackStyle {
    public void track(int id) {
        double tDiff;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        if (sp.traj.moveStatus == TRAJ_MOVE_STATUS.trajStarted)
            sp.traj.moveStatus = TRAJ_MOVE_STATUS.waitForRampDown;
        else if (sp.traj.moveStatus == TRAJ_MOVE_STATUS.rampDownStarted)
            sp.traj.moveStatus = TRAJ_MOVE_STATUS.waitToBuildTraj;

        if (sp.traj.moveStatus == TRAJ_MOVE_STATUS.waitToBuildTraj) {
            tDiff = astroTime.getInstance().sidT.rad - sp.traj.endSidT;
            if (tDiff < -units.HALF_REV)
                tDiff += units.ONE_REV;
            if (tDiff >= 0.) {
                buildTraj(id);
                sp.traj.moveStatus = TRAJ_MOVE_STATUS.trajStarted;
                // trajectory units are in arcseconds and seconds
                sp.velRadSec = sp.traj.rampUp.endVel*units.ARCSEC_TO_RAD;
                trackByVelSubr(id);
            }
        }
        else if (sp.traj.moveStatus == TRAJ_MOVE_STATUS.waitForRampDown) {
            tDiff = astroTime.getInstance().sidT.rad - sp.traj.startRampDownSidT;
            // start ramp down at or before desired time
            tDiff += cfg.getInstance().moveToTargetTimeSec*units.SEC_TO_RAD;
            if (tDiff < -units.HALF_REV)
                tDiff += units.ONE_REV;
            if (tDiff >= 0.) {
                sp.traj.moveStatus = TRAJ_MOVE_STATUS.rampDownStarted;
                // trajectory units are in arcseconds and seconds
                // calculate new decceleration so that target is not reached early: a=(f-b)/t
                // note: trajectory runs behind schedule because motor actually starts smidgen later than trajectory start time
                sp.accelDegSecSec = Math.abs(units.ARCSEC_TO_DEG
                * (sp.traj.rampDown.begVel - sp.traj.rampDown.endVel)
                / (sp.traj.rampDown.time + tDiff*units.RAD_TO_SEC*units.SID_RATE));
                sp.acceleration = (long) (sp.accelDegSecSec * sp.accelDegSecSecToCountsTickTick());
                sp.velRadSec = sp.traj.rampDown.endVel*units.ARCSEC_TO_RAD;
                trackByVelSubr(id);
            }
        }
    }

    private void buildTraj(int id) {
        double begVel;
        double distance;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        // compare immediate last trajectory move to actual distance moved (error will be exaggerated by delay between
        // end of trajectory move and last reading of current position)
        sp.traj.deltaPosDeg = sp.currentPositionDeg - sp.traj.holdPosDeg;
        sp.traj.errorPosDeg = sp.traj.deltaPosDeg - sp.traj.totalDistance*units.ARCSEC_TO_DEG;
        sp.traj.holdPosDeg = sp.currentPositionDeg;

        trackCalcDeltaPosVel(id);

        begVel = sp.PICServoSimList.currentVelRadSec(astroTime.getInstance().JD)*units.RAD_TO_ARCSEC;
        if (sp.targetVelDir == ROTATION.CW)
            begVel = Math.abs(begVel);
        else
            begVel = -Math.abs(begVel);

        distance = sp.deltaPos1Rad*units.RAD_TO_ARCSEC;

        // adjust acceleration to better match distance to move, that is, use gentler acceleration for shorter moves:
        // amount determined empirically
        sp.accelDegSecSec = 2.*Math.abs(distance*units.ARCSEC_TO_DEG);
        // don't allow accel to exceed config's starting value,
        if (sp.accelDegSecSec > sp.cfgAccelDegSecSec)
            sp.accelDegSecSec = sp.cfgAccelDegSecSec;
        sp.acceleration = (long) (sp.accelDegSecSec * sp.accelDegSecSecToCountsTickTick());

        // calculate in arcseconds and seconds
        trajectory.trajFromAccelDistanceMaxVelBegVelEndVel(sp.traj,
        sp.accelDegSecSec*units.DEG_TO_ARCSEC,
        distance,
        sp.fastSpeedRadSec*units.RAD_TO_ARCSEC,
        begVel,
        sp.deltaPos1_2VelRadSec*units.RAD_TO_ARCSEC);

        sp.traj.startSidT = astroTime.getInstance().sidT.rad;

        sp.traj.startRampDownSidT = sp.traj.startSidT
        + (sp.traj.rampUp.time
        + sp.traj.maxVel.time) * units.SEC_TO_RAD / units.SID_RATE;
        sp.traj.startRampDownSidT = eMath.validRad(sp.traj.startRampDownSidT);

        sp.traj.endSidT = sp.traj.startRampDownSidT + sp.traj.rampDown.time * units.SEC_TO_RAD / units.SID_RATE;
        sp.traj.endSidT = eMath.validRad(sp.traj.endSidT);
    }

    public void displayTrackingVars(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        displayTrackingVarsHdr(id);

        console.stdOutLn("Delta\" " + sp.deltaPosRad*units.RAD_TO_ARCSEC);
        console.stdOutLn("velRadSec\" " + sp.velRadSec*units.RAD_TO_ARCSEC);
        if (sp.traj.moveStatus == TRAJ_MOVE_STATUS.trajStarted) {
            console.stdOutLn("RampUpStarted: previous distance moved\" " + sp.traj.deltaPosDeg*units.DEG_TO_ARCSEC);
            console.stdOutLn("error\" " + sp.traj.errorPosDeg*units.DEG_TO_ARCSEC);
            sp.traj.display();
        }
        if (sp.traj.moveStatus == TRAJ_MOVE_STATUS.rampDownStarted) {
            console.stdOutLn("rampDownStarted");
        }
    }
}

