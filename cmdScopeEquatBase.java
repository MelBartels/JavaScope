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

public class cmdScopeEquatBase extends cmdScopePositionBase {
    double begSidT;
    double lastSidT;
    double endSidT;
    azDouble rateRadSec = new azDouble();

    boolean parseSecondsAndObjectName(StringTokenizer st) {
        boolean rtnValue;
        
        rtnValue = parseSecondsAndComment(st);

        if (p!=null)
            p.objName = comment;

        return rtnValue;
    }

    boolean parseEquat(StringTokenizer st) {
        position p2 = new position("parseEquat p2");

        // p is from cmdScopePositionBase
        p = new position("from cmdScopePositionBase");
        try {
            common.fReadEquatCoord(p2, st);
            // don't read name here: need to read optional moveTimeSec then comment instead of name
            if (cfg.getInstance().precessionNutationAberration)
                p2.applyPrecessionCorrection();
            p.copy(p2);
            return true;
        }
        catch (NumberFormatException nfe) {
            console.errOut("bad number for " + cmdTypeScope);
        }
        return false;
    }

    boolean parseEquatArcmin(StringTokenizer st) {
        // p is from cmdScopePositionBase
        p = new position("from cmdScopePositionBase");
        try {
            if (st.countTokens() > 0)
                p.ra.rad = Double.parseDouble(st.nextToken()) * units.ARCMIN_TO_RAD;
            else
                return false;
            if (st.countTokens() > 0)
                p.dec.rad = Double.parseDouble(st.nextToken()) * units.ARCMIN_TO_RAD;
            else
                return false;
            return true;
        }
        catch (NumberFormatException nfe) {
            console.errOut("bad number for " + cmdTypeScope);
        }
        return false;
    }

    /**
     * if equatorial move over time, then use repeated calls of cmdInProgress() in the main event track loop to
     * increment target equatorial coordinates;
     * calling sequence is sequencer() -> cmdCol.checkProcessCmd() -> cmdInProgress();
     * eventually, cmdInProgress() will detect that move is finished and return false, ending command;
     * not .cmdDevice = CMD_DEVICE.cmdScopeList as tracking to target will not occur;
     */
    void equatMoveOverTime() {
        cmdInProgress = true;
        lastSidT = begSidT = astroTime.getInstance().sidT.rad;
        endSidT = eMath.validRad(begSidT + moveTimeSec*units.SEC_TO_RAD);
        rateRadSec.a = eMath.validRadPi(cl.t.target.getDecCorrectedForNutationAnnualAberration() - cfg.getInstance().current.dec.rad) / moveTimeSec;
        rateRadSec.z = eMath.validRadPi(cl.t.target.getRaCorrectedForNutationAnnualAberration() - cfg.getInstance().current.ra.rad) / moveTimeSec;
        // set target to current to begin timed equatorial velocity move, let input remain as a record of
        // the final targeted position
        cl.t.target.copy(cfg.getInstance().current);
    }

    public boolean cmdInProgress() {
        if (moveTimeSec > 0.)
            return cmdInProgressTimedMove();
        else
            return false;
    }

    boolean cmdInProgressTimedMove() {
        double tRemainRad;
        double tIntervalRad;

        cmdInProgress = false;

        if ((cfg.getInstance().spa.cmdDevice == CMD_DEVICE.trackByVel
        || cfg.getInstance().spa.cmdDevice == CMD_DEVICE.trackByPos
        || cfg.getInstance().spa.cmdDevice == CMD_DEVICE.none)
        && (cfg.getInstance().spz.cmdDevice == CMD_DEVICE.trackByVel
        || cfg.getInstance().spz.cmdDevice == CMD_DEVICE.trackByPos
        || cfg.getInstance().spz.cmdDevice == CMD_DEVICE.none)) {

            tRemainRad = eMath.validRad(endSidT - astroTime.getInstance().sidT.rad);
            if (tRemainRad < units.HALF_REV) {
                // update target equat coord based on interval since last call
                tIntervalRad = eMath.validRad(astroTime.getInstance().sidT.rad - lastSidT);
                // do not apply nutationAnnualAberration corrections here as the tracking routine will do this
                cl.t.target.dec.rad += rateRadSec.a * tIntervalRad * units.RAD_TO_SEC;
                cl.t.target.ra.rad = eMath.validRad(cl.t.target.ra.rad + rateRadSec.z * tIntervalRad * units.RAD_TO_SEC);
                lastSidT = astroTime.getInstance().sidT.rad;
                cmdInProgress = true;
                /**
                System.out.println("cmdInProgressTimedMove() distances in arcseconds Ra "
                + rateRadSec.z * units.RAD_TO_ARCSEC * tIntervalRad * units.RAD_TO_SEC
                + " Dec "
                + rateRadSec.a * units.RAD_TO_ARCSEC * tIntervalRad * units.RAD_TO_SEC);
                */
            }
        }
        return cmdInProgress;
    }
}

