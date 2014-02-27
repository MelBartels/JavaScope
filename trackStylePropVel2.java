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
 * this tracking style implements a proportional velocity control;
 * moveToTargetEquat() produces two positions, one for 'now' and one for timeIncr later;
 * move at percentage of velocity to position 1 + calculated velocity between Positions 1 and 2;
 * acceleration lowered if distance to move < .posMoveThresholdArcmin;
 * conclusion: works well, this trackStyle recommended
 */
public class trackStylePropVel2 extends trackStyleBase implements TrackStyle {
    public void track(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        trackCalcDeltaPosVel(id);

        if (Math.abs(sp.deltaPosRad) > sp.posMoveThresholdArcmin*units.ARCMIN_TO_RAD)
            sp.acceleration = sp.longRangeAccel;
        else
            sp.acceleration = sp.shortRangeAccel;

        sp.propVel = sp.dampenFactor*sp.deltaPos_1VelRadSec;
        sp.velRadSec = sp.propVel + sp.deltaPos1_2VelRadSec;

        if (deccelCheck)
            checkDeccelToFinalPos(id);

        trackByVelSubr(id);

        sp.acceleration = sp.longRangeAccel;
    }

    public void displayTrackingVars(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        displayTrackingVarsHdr(id);

        console.stdOutLn("Delta\" " + sp.deltaPosRad*units.RAD_TO_ARCSEC);
        console.stdOutLn("deltaPos2Rad\" " + sp.deltaPos2Rad*units.RAD_TO_ARCSEC);
        console.stdOutLn("Interval " + cfg.getInstance().moveToTargetTimeSec);
        console.stdOutLn("Delta_1VelRadSec\" " + sp.deltaPos_1VelRadSec*units.RAD_TO_ARCSEC);
        console.stdOutLn("Delta1_2VelRadSec\" " + sp.deltaPos1_2VelRadSec*units.RAD_TO_ARCSEC);
        console.stdOutLn("velRadSec\" " + sp.velRadSec*units.RAD_TO_ARCSEC);
        console.stdOutLn("AvgVel\" " + sp.avgVelRadSec*units.RAD_TO_ARCSEC);
    }
}

