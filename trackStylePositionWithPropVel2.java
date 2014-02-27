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
 * this tracking style combines proportional velocity style #2 with positional style for moves > posMoveThresholdArcmin;
 * see trackStylePropVel2 for more comments;
 */
public class trackStylePositionWithPropVel2 extends trackStyleBase implements TrackStyle {
    trackStylePosition trackStylePosition = new trackStylePosition();
    trackStylePropVel2 trackStylePropVel2 = new trackStylePropVel2();

    public void track(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        sp.trackError = false;
        if (Math.abs(sp.deltaPosRad) > sp.posMoveThresholdArcmin*units.ARCMIN_TO_RAD)
            trackStylePosition.track(id);
        else if (sp.cmdDevice == CMD_DEVICE.trackByVel || sp.cmdDevice == CMD_DEVICE.none)
            trackStylePropVel2.track(id);
        else
            // waiting for motor to come to stop which sets cmdDevice to none
            sp.trackError = true;
    }

    public void displayTrackingVars(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        displayTrackingVarsHdr(id);

        console.stdOutLn("Delta\" " + sp.deltaPosRad*units.RAD_TO_ARCSEC);
        if (sp.trackError)
            console.stdOutLn("trackError: cmdDevice is '"
            + sp.cmdDevice
            + "' but needs to be 'track' or 'none'");
        else if (sp.displayPositionStart) {
            console.stdOutLn("position move started "
            + sp.traj.totalDistance*units.RAD_TO_ARCSEC
            + "\"");
            sp.displayPositionStart = false;
        }
        else
            console.stdOutLn("velRadSec\" " + sp.velRadSec*units.RAD_TO_ARCSEC);
    }
}

