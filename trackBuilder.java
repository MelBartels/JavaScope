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
 * following coordinate processing through the program...
 * 1. starting coordinates obtained from configuration file variable currentPositionDeg for each motor in the SERVO_ID section
 *    in cfg.getInstance().read(); current altaz coord set to currentPositionDeg (for convertMatrix initialization as it comes before servo init)
 * 2. servo() constructor preserves .currentPositionDeg during call to initServoID() which sets .currentPositionDeg to zero due to
 *    various servo motor commands being issued, each of which calls readStatus() which in turn calls setCurrentPositionDeg()
 * 3. servo() constructor then calls initServoIDVars() which sets .currentPositionOffsetRad from current - actual (actual always zero
 *    when motor controllers first initialized)
 * 4. now subsequent calls to readStatus() will calculate correct .currentPositionDeg by calling setCurrentPositionDeg()
 *    which uses .currentPositionOffsetRad per current = actual + offset
 * 5. track() constructor creates convertMatrix object, then calls setCurrentAltazToServoPosition(), then if one and two are initialized,
 *    current equat coord are calculated and target coord set to current coord
 * 6. setCurrentAltazToServoPosition() starts with setting cfg.getInstance().current altaz coords = .actualPosition + .currentPositionOffsetRad,
 *    then adjusts cfg.getInstance().current altaz coords based on the various error values such as backlash, PEC, drift, guiding, axis vs axis
 *    corrections, PMC, and refraction both altaz and equat
 * 7. cfg.getInstance().current altaz coords updated by repeated calls to setCurrentAltazToServoPosition()
 *    while other motors such as field rotation updated by repeated readStatus() calls (which calls setCurrentPositionDeg() as
 *    mentioned above)
 * 8. if cfg.getInstance().current altaz coords are changed, say by user input, or by encoders, then .currentPositionOffsetRad updated after
 *    all corrections are backed out or reversed via offset = current - actual in function setServoPositionToCurrentAltaz()
 * 9. shutdown of trackBuilder object done by calling trackBuilder.close() which calls track.close() which calls servo.close()
 *    which smoothly stops all motors, and waits for motors to stop: during wait for motors to stop, readStatus() repeatedly
 *    called which calls setCurrentPositionDeg()
 *10. setCurrentPositionDeg then optionally saved per user wish in cfg.getInstance().askAndWrite();
 *
 * target coordinates handled as follows:
 *    turnTrackingOn(), so that scope will track/slew to previously entered target coordinates,
 *    if desiring to track to current location, then immediately call setCurrentAltazGetEquatCopyTarget() before continuing;
 *
 * encoders handled as follows:
 *    at startup, encoders reset to current values;
 *    separate thread gets encoder values at periodic interval (1 sec),
 *    sequencer() calls checkEncoderThreshold() which will reset scope to encoder values if threshold exceeded;
 *
 * main control method is sequencer() which checks for various events and if none, calls moveToTargetEquat();
 */
public class trackBuilder {
    track t;
    JFrameMain jfm;
    java.util.Timer updateCurrEquatCoordTimer;
    java.util.Timer updateStatusTimer;
    java.util.Timer extSlewFilesTimer;
    boolean extSlewFilesTaskStarted;
    boolean HTMLThreadsStarted;

    trackBuilder(JFrameMain jfm) {
        this.jfm = jfm;
    }

    void build() {
        // see sequencer() for status that can be turned on/off
        t = new track();
        t.buildEncoders();
        // set the tracking style
        //t.TrackStyle = new trackStyleFactory().build(TRACK_STYLE_ID.trackStylePropVel2);
        t.TrackStyle = new trackStyleFactory().build(TRACK_STYLE_ID.trackStyleTrajVel);
        console.stdOutLn("using tracking style of " + t.TrackStyle.trackStyleID());
        t.TrackStyle.setDeccelCheck(true);
        // create the cmd collection
        t.cmdCol = new cmdCol(t);
        // set the inits
        cfg.getInstance().killInits();
        // if no user interface, then ask for starting init state via the command line
        if (jfm == null)
            cfg.getInstance().setInitStateFromCmdLine();
        // init based on config's initState (possibly changed by user just above)
        t.c.init(cfg.getInstance().initState);
        /**
         * if initialized, then set target and input to current coordinates;
         * if inits come from configuration file or user selected initialization at startup, then input and target must be set
         * before commencing tracking;
         * when initialized via handpad et al, target coord will use the input coord used to create the 2nd init point, and, scope
         * will track to that equat coord;
         * target set afresh when command cmd_scope_trackon executed;
         */
        if (cfg.getInstance().initialized()) {
            t.setCurrentAltazGetEquatCopyTarget();
            t.in.copy(cfg.getInstance().current);
        }
        if (jfm != null)
            jfm.registerTrackReference(t);

        // start the html and external slew files listener threads
        html.updateStatusTimeSec = html.refreshStatusTimeSec = html.updateCurrEquatCoordTimeSec = (int) cfg.getInstance().updateHTMLFreqSec;
        if ((int) cfg.getInstance().updateHTMLFreqSec > 0) {
            startHTMLThreads();
            HTMLThreadsStarted = true;
        }
        if (cfg.getInstance().readWriteExternalSlewFiles) {
            startExtSlewFiles();
            extSlewFilesTaskStarted = true;
        }
    }

    void run() {
        int threadSleepMs = cfg.getInstance().sequencerSleepTimeMilliSec;
        
        // shutdown can come from UI or from external control channel
        while (!t.shutdown) {
            if (!t.pauseSequencer)
                t.sequencer(SERVO_ID.azRa.KEY, false);
            if (html.timersChanged) {
                stopHTMLThreads();
                if (html.updateStatusTimeSec >= 1)
                    startHTMLThreads();
                html.timersChanged = false;
            }
            if (extSlewFilesTaskStarted && !cfg.getInstance().readWriteExternalSlewFiles)
                stopExtSlewFilesThread();
            // quietdown some of the cpu activity
            try {
                Thread.sleep(threadSleepMs);
            } catch (InterruptedException e) {
 		console.errOut(e.toString());               
            }                    
        }
        close();
        //cfg.getInstance().askAndWrite();
        cfg.getInstance().write();
    }

    void startHTMLThreads() {
        console.stdOutLn("starting html threads");
        updateStatusTimer = new java.util.Timer();
        updateStatusTimer.schedule(new updateStatusTask(t), 0, html.updateStatusTimeSec * 1000);
        updateCurrEquatCoordTimer = new java.util.Timer();
        updateCurrEquatCoordTimer.schedule(new updateCurrEquatCoordTask(t), 0, html.updateCurrEquatCoordTimeSec * 1000);
    }

    void startExtSlewFiles() {
        console.stdOutLn("starting external slew files thread");
        extSlewFilesTimer = new java.util.Timer();
        extSlewFilesTimer.schedule(new extSlewFilesTask(t), 0, t.cmdCol.extSlewFiles.UPDATE_TIME_SEC * 1000);
    }

    void close() {
        if (HTMLThreadsStarted)
            stopHTMLThreads();
        if (extSlewFilesTaskStarted)
            stopExtSlewFilesThread();
        t.close();
    }

    void stopHTMLThreads() {
        console.stdOutLn("stopping html threads");
        if (updateStatusTimer != null)
            updateStatusTimer.cancel();
        if (updateCurrEquatCoordTimer != null)
            updateCurrEquatCoordTimer.cancel();
        HTMLThreadsStarted = false;
    }

    void stopExtSlewFilesThread() {
        console.stdOutLn("stopping external slew files thread");
        extSlewFilesTimer.cancel();
        extSlewFilesTaskStarted = false;
    }

    // overrides trackBuilder stop()
    void stop() {
        updateStatusTimer = null;
        updateCurrEquatCoordTimer = null;
    }
}

