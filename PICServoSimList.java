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
 * an array of PICServo simulator actions including methods that shield the array index
 */
public class PICServoSimList {
    int count;
    private int begIx;
    private int prevPssIx;
    private int pssIx;
    String PICServoSimListString;
    private final int PSS_SIZE = 100;
    private PICServoSim[] pss;

    PICServoSimList(double startJD) {
        pss = new PICServoSim[PSS_SIZE];
        for (begIx = 0; begIx < PSS_SIZE; begIx++)
            pss[begIx] = new PICServoSim();

        // build first entry as a reset to position 0
        begIx = pssIx = 0;
        pss[pssIx].buildResetPos(startJD, 0);
        count = 1;
    }

    void incrIndeces() {
        prevPssIx = pssIx;
        pssIx++;
        if (pssIx >= PSS_SIZE)
            pssIx = 0;
        if (begIx == pssIx) {
            begIx++;
            if (begIx >= PSS_SIZE)
                begIx = 0;
        }
        count++;
    }

    void addResetPos(double startJD, double startPosRad) {
        if (startPosRad != pss[pssIx].startPosRad) {
            incrIndeces();
            pss[pssIx].buildResetPos(startJD, startPosRad);
        }
    }

    void addPos(double startJD, double accelRadSecSec, double targetDistanceRad, double maxVelRadSec) {
        incrIndeces();
        pss[pssIx].startPosRad = pss[prevPssIx].currentPosRad(startJD);
        pss[pssIx].buildPos(startJD, accelRadSecSec, targetDistanceRad-pss[pssIx].startPosRad, maxVelRadSec);
    }

    void addVel(double startJD, double accelRadSecSec, double targetVelRadSec) {
        incrIndeces();
        pss[pssIx].startPosRad = pss[prevPssIx].currentPosRad(startJD);
        pss[pssIx].startVelRadSec = pss[prevPssIx].currentVelRadSec(startJD);
        pss[pssIx].buildVel(startJD, accelRadSecSec, pss[pssIx].startVelRadSec, targetVelRadSec);
    }

    void stopMotorSmoothly(double startJD) {
        incrIndeces();
        pss[pssIx].startPosRad = pss[prevPssIx].currentPosRad(startJD);
        pss[pssIx].startVelRadSec = pss[prevPssIx].currentVelRadSec(startJD);
        pss[pssIx].buildVel(startJD, pss[prevPssIx].accelRadSecSec, pss[pssIx].startVelRadSec, 0.);
    }

    void stopMotorAbruptly(double startJD) {
        incrIndeces();
        addResetPos(startJD, pss[prevPssIx].currentPosRad(startJD));
    }

    double currentPosRad(double JD) {
        return pss[pssIx].currentPosRad( JD);
    }

    double currentVelRadSec(double JD) {
        return pss[pssIx].currentVelRadSec(JD);
    }

    TRAJECTORY_STYLE trajectoryStyle() {
        return pss[pssIx].trajectoryStyle;
    }

    /**
     * following methods based on (see servo class notes) Move state:
     * uses         STATUS_BYTE_MOVE_DONE, AUX_STATUS_BYTE_ACCEL_DONE, AUX_STATUS_BYTE_SLEW_DONE
     * velocity move:
     *    ramping up         false
     *    at max vel         TRUE
     * position move:
     *    ramping up         false                  false
     *    at max vel         false                  TRUE                   false
     *    ramping down       false                  TRUE                   TRUE
     *    finished           TRUE                   TRUE                   TRUE
     */

    // if vel mode, if ramping concluded; if position mode, if trapezoidal trajectory completed
    boolean moveDone(double JD) {
        double timeDiff;

        if (pss[pssIx].trajectoryStyle == TRAJECTORY_STYLE.velocity) {
            timeDiff = JD - (pss[pssIx].startJD + pss[pssIx].traj.rampUp.time*units.SEC_TO_DAY);
            if (timeDiff < -.5)
                timeDiff += 1.;
            if (timeDiff >= 0.)
                return true;
        }
        else if (pss[pssIx].trajectoryStyle == TRAJECTORY_STYLE.position) {
            timeDiff = JD - (pss[pssIx].startJD + pss[pssIx].traj.totalTime*units.SEC_TO_DAY);
            if (timeDiff < -.5)
                timeDiff += 1.;
            if (timeDiff >= 0.)
                return true;
        }
        return false;
    }

    // max vel completed and ramping down
    boolean slewDone(double JD) {
        double timeDiff;

        if (pss[pssIx].trajectoryStyle == TRAJECTORY_STYLE.position) {
            timeDiff = JD - (pss[pssIx].startJD + (pss[pssIx].traj.rampUp.time+pss[pssIx].traj.maxVel.time)*units.SEC_TO_DAY);
            if (timeDiff < -.5)
                timeDiff += 1.;
            if (timeDiff >= 0.)
                return true;
        }
        return false;
    }

   // ramp up completed
    boolean accelDone(double JD) {
        double timeDiff;

        if (pss[pssIx].trajectoryStyle == TRAJECTORY_STYLE.position) {
            timeDiff = JD - (pss[pssIx].startJD + pss[pssIx].traj.rampUp.time*units.SEC_TO_DAY);
            if (timeDiff < -.5)
                timeDiff += 1.;
            if (timeDiff >= 0.)
                return true;
        }
        return false;
    }

    String buildPICServoSimListString() {
        int ix;

        PICServoSimListString = "";
        ix = begIx;
        while (ix != pssIx) {
            PICServoSimListString += pss[ix].buildPICServoSimString() + "\n";
            ix++;
            if (ix >= PSS_SIZE)
                ix = 0;
        }
        return PICServoSimListString;
    }

    void display() {
        console.stdOutLn("\ndisplay of PICServoSimList");
        console.stdOutLn(buildPICServoSimListString());
        console.stdOutLn("end of PICServoSimList display\n");
    }
}

