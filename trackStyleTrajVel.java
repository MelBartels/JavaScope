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
 * this tracking style uses the trajectory class to build a trajectory with the PICServo simulator's current velocity
 * method as an input, then sends a desired velocity command with a calculated acceleration parameter;
 * unless the beginning velocity or final velocity matches the calculated maximum velocity so that the two trajectories
 * will merge on time at the same position with a single velocity/acceleration command, a second pass through the track()
 * method will be required for the last portion of the sawtooth or trapezoidal trajectory;
 * if calculated velocity/acceleration exceeds allowed maximum velocity or allowed maximum acceleration,
 * then make initial velocity/acceleration command for maximum velocity at maximum acceleration;
 * use repeated calls to track() to approach target position;
 * as the target is approached, track() weights more towards the final velocity and less towards the maxvel of the trapezoid;
 * conclusion: this tracking style reaches target many times quicker than trackStyleProp2: recommended
 */
public class trackStyleTrajVel extends trackStyleBase implements TrackStyle {
    double propVel;
    double totalDampenFactor;
    double factorThresholdRad;

    public void track(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        factorThresholdRad = 10. * sp.countToRad;

        trackCalcDeltaPosVel(id);

        trajectory.trajFromTimeDistanceBegVelEndVel(sp.traj,
        cfg.getInstance().moveToTargetTimeSec,
        sp.deltaPos1Rad*units.RAD_TO_ARCSEC,
        sp.PICServoSimList.currentVelRadSec(astroTime.getInstance().JD)*units.RAD_TO_ARCSEC,
        sp.deltaPos1_2VelRadSec*units.RAD_TO_ARCSEC);

        propVel = sp.traj.rampUp.endVel*units.ARCSEC_TO_RAD - sp.deltaPos1_2VelRadSec;
        if (Math.abs(sp.deltaPosRad) > factorThresholdRad)
            totalDampenFactor = sp.dampenFactor;
        else
            totalDampenFactor = Math.pow(sp.dampenFactor, 2.);

        sp.velRadSec = totalDampenFactor * propVel + sp.deltaPos1_2VelRadSec;

        // don't allow a zero accel;
        // if rampUp.accel is zero, then leave acceleration as is
        if (sp.traj.rampUp.accel != 0.)
            sp.acceleration = (long) (Math.abs(sp.traj.rampUp.accel) * units.ARCSEC_TO_DEG*sp.accelDegSecSecToCountsTickTick());

        if (deccelCheck)
            checkDeccelToFinalPos(id);

        trackByVelSubr(id);

        // restore acceleration changed above
        sp.acceleration = sp.longRangeAccel;
    }

    public void displayTrackingVars(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        displayTrackingVarsHdr(id);

        console.stdOutLn("moveToTargetTimeSec "
        + cfg.getInstance().moveToTargetTimeSec
        +  " deltaPosArcsec "
        + sp.deltaPosRad*units.RAD_TO_ARCSEC
        + " deltaPos1Arcsec "
        + sp.deltaPos1Rad*units.RAD_TO_ARCSEC
        + " deltaPos2Arcsec "
        + sp.deltaPos2Rad*units.RAD_TO_ARCSEC
        + " deltaPos_1VelRadSec "
        + sp.deltaPos_1VelRadSec*units.RAD_TO_ARCSEC
        + " deltaPos1_2VelRadSec "
        + sp.deltaPos1_2VelRadSec*units.RAD_TO_ARCSEC
        + " propVel "
        + propVel
        + " maxVelArcsecSec "
        + sp.traj.rampUp.endVel
        + " accelArcsecSec "
        + sp.traj.rampUp.accel);

        sp.traj.display();
    }
}

