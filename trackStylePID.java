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
 * this tracking style implements a PID filter in software to control a PICServo;
 * feedback is the PICServo position via status return and the time of the status return;
 * the time increment as fed to moveToTargetEquatSubr() should be the same or larger as the
 * time it takes to execute a PID control loop;
 * conclusion: time consuming and challenging to tune, plus have to re-tune when servo characteristics change, like
 * acceleration number or moveToTargetTimeSec: this trackStyle not recommended
 */
public class trackStylePID extends trackStyleBase implements TrackStyle {
    public void track(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        sp.iRunningSum += sp.deltaPosRad;
        // limit integration running total
        if (sp.iRunningSum > sp.softKiLimit)
            sp.iRunningSum = sp.softKiLimit;
        else if (sp.iRunningSum < -sp.softKiLimit)
            sp.iRunningSum = -sp.softKiLimit;
        sp.kdDeltaPosRad = sp.deltaPosRad-sp.lastPIDDeltaPosRad;
        // calculate PID
        sp.velRadSec = sp.softKp*sp.deltaPosRad + sp.softKd*sp.kdDeltaPosRad + sp.softKi*sp.iRunningSum;
        // save to last Positional error
        sp.lastPIDDeltaPosRad = sp.deltaPosRad;

        trackByVelSubr(id);
    }

    public void displayTrackingVars(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        displayTrackingVarsHdr(id);

        console.stdOutLn("Delta\" " + sp.deltaPosRad*units.RAD_TO_ARCSEC);
        console.stdOut("contribution of softKp\" " + sp.softKp*sp.deltaPosRad*units.RAD_TO_ARCSEC);
        console.stdOut(" softKd\" " + sp.softKd*sp.kdDeltaPosRad*units.RAD_TO_ARCSEC);
        console.stdOutLn(" softKi\" " + sp.softKi*sp.iRunningSum*units.RAD_TO_ARCSEC);
        console.stdOutLn("velRadSec\" " + sp.velRadSec*units.RAD_TO_ARCSEC);
        console.stdOutLn("AvgVel\" " + sp.avgVelRadSec*units.RAD_TO_ARCSEC);
    }
}

