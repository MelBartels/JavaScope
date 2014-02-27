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
 * a series of TrackStyles or tracking methods: use to investigate tracking algorithms keeping in mind that
 * 1.the PICServo controllers implement their own control loops at ~2kH frequency,
 * 2.scope control should loop several times a second when undergoing high rate of velocity change for to handle error corrections,
 * 3.operating system cannot guarantee timings
 */
public class trackStyleBase {
    TRACK_STYLE_ID trackStyleID;
    boolean deccelCheck;

    public void trackStyleID(TRACK_STYLE_ID trackStyleID) {
        this.trackStyleID = trackStyleID;
    }

    public String trackStyleID() {
        return trackStyleID.toString();
    }

    public void setDeccelCheck(boolean value) {
        deccelCheck = value;
    }

    public void trackByVelSubr(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        sp.cmdDevice = CMD_DEVICE.trackByVel;
        if (sp.velRadSec < 0.) {
            sp.velRadSec = -sp.velRadSec;
            sp.targetVelDir = ROTATION.CCW;
        }
        else
            sp.targetVelDir = ROTATION.CW;
        sp.moveCmd = SERVO_MOVE_CMD.startVelMove;
        sp.moveNow = true;
        sp.servoCmdProcessed = false;
    }

    void trackCalcDeltaPosVel(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        sp.deltaPos_1VelRadSec = sp.deltaPos1Rad / cfg.getInstance().moveToTargetTimeSec;
        sp.deltaPos1_2VelRadSec = (sp.deltaPos2Rad - sp.deltaPos1Rad) / cfg.getInstance().moveToTargetTimeSec;
    }

    boolean checkDeccelToFinalPos(int id) {
        double vel;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        vel = sp.PICServoSimList.currentVelRadSec(astroTime.getInstance().JD);

        trajectory.triggerTimeDistanceFromAccelBegVelEndVel(sp.td,
        sp.calcAccelDegSecSecFromAcceleration()*units.DEG_TO_RAD,
        vel,
        sp.deltaPos1_2VelRadSec);
        // if deccelerating and if distance overrun
        if (Math.abs(vel) > Math.abs(sp.deltaPos1_2VelRadSec)
        && (sp.deltaPos2Rad > 0. && sp.td.distance >= sp.deltaPos2Rad
        || sp.deltaPos2Rad < 0. && sp.td.distance <= sp.deltaPos2Rad)) {
            sp.velRadSec = sp.deltaPos1_2VelRadSec;
            sp.deccelCommanded = true;
        }
        else
            sp.deccelCommanded = false;

        return sp.deccelCommanded;
    }

    void displayTrackingVarsHdr(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        if (id == SERVO_ID.altDec.KEY)
            console.stdOutLn("\nCurrent.alt "
            + cfg.getInstance().current.alt.rad*units.RAD_TO_DEG
            + " currentPositionDeg "
            + sp.currentPositionDeg);
        if (id == SERVO_ID.azRa.KEY)
            console.stdOutLn("\nCurrent.az "
            + cfg.getInstance().current.az.rad*units.RAD_TO_DEG
            + " currentPositionDeg "
            + sp.currentPositionDeg);

        if (sp.deccelCommanded) {
            console.stdOutLn("*** deccel commanded ***");
            console.stdOutLn("deccel distance\""
            + sp.td.distance*units.RAD_TO_ARCSEC
            + " > DeltaPos2=\""
            + sp.deltaPos2Rad*units.RAD_TO_ARCSEC);
        }
    }

    void displayRmsPv(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        console.stdOutLn(sp.taa.buildRmsPvString(10));
    }
}

