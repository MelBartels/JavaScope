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
 * this tracking style uses a simple velocity feedback based on distance to move;
 * conclusion: without differential filter, feedback quickly oscillates out of control, or, permanently lags behind desired
 * target: this trackStyle not recommended
 */
public class trackStyleVelocity extends trackStyleBase implements TrackStyle {
    public void track(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        trackCalcDeltaPosVel(id);
        // velocity based on distance to first position
        sp.velRadSec = sp.dampenFactor*sp.deltaPos_1VelRadSec;
        if (deccelCheck)
            checkDeccelToFinalPos(id);

        trackByVelSubr(id);
    }

    public void displayTrackingVars(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        displayTrackingVarsHdr(id);

        console.stdOutLn("Delta\" " + sp.deltaPosRad*units.RAD_TO_ARCSEC);
        console.stdOutLn("Delta_1VelRadSec\" " + sp.deltaPos_1VelRadSec*units.RAD_TO_ARCSEC);
        console.stdOutLn("velRadSec\" " + sp.velRadSec*units.RAD_TO_ARCSEC);
        console.stdOutLn("AvgVel\" " + sp.avgVelRadSec*units.RAD_TO_ARCSEC);
    }
}

