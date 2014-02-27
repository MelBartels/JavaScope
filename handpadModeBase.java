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
 * methods and properties that are common to all handpadModes
 */
public class handpadModeBase {
    static final int STARTING_HANDPAD_TIMER_VALUE = 3;
    int handpadButtonTimer;
    int recordEquatTimer;
    int recordAltazTimer;
    int grandTourTimer;
    int scrollTimer;
    HANDPAD_MODE handpadMode;
    track t;

    public void init(HANDPAD_MODE handpadMode, track t) {
        this.handpadMode = handpadMode;
        this.t = t;
    }

    public HANDPAD_MODE handpadMode() {
        return handpadMode;
    }

    public void handpadModeBeep() {
        common.beep(1, 0);
    }

    public void processTimers() {
        // allow time for handpad button to be released (if LX200 control calls this function, then timer will decrement twice in
        // one call to sequencer() since sequencer() also calls this function if LEFT_KEY or RIGHT_KEY pressed)
        if (handpadButtonTimer > 0)
            handpadButtonTimer--;
        if (recordEquatTimer > 0)
            recordEquatTimer--;
        if (recordAltazTimer > 0)
            recordAltazTimer--;
        if (grandTourTimer > 0)
            grandTourTimer--;
        if (scrollTimer > 0)
            scrollTimer--;
    }

    boolean leftKeyPressed() {
        return (t.buttons & handpadDesignBase.LEFT_KEY) == handpadDesignBase.LEFT_KEY;
    }

    boolean rightKeyPressed() {
        return (t.buttons & handpadDesignBase.RIGHT_KEY) == handpadDesignBase.RIGHT_KEY;
    }

    boolean leftOrRightKeyPressed() {
        return leftKeyPressed() || rightKeyPressed();
    }

    void processGuideModeKeys() {
        if (leftKeyPressed() && cfg.getInstance().initialized() && handpadButtonTimer == 0) 
            processGuideModeLeftKeyPressTemplate();
        else if (rightKeyPressed() && handpadButtonTimer == 0) 
            processGuideModeRightKeyPressTemplate();
    }
    
    void processGuideModeLeftKeyPressTemplate() {
        if (!t.guideActive) {
            handpadModeBeep();
            handpadButtonTimer = STARTING_HANDPAD_TIMER_VALUE;
            preInitAllGuide();
            t.initAllGuide();
            postInitAllGuide();
        }
        else
            if (t.checkStartGuideForWriteNSave()) {
                handpadModeBeep();
                handpadButtonTimer = STARTING_HANDPAD_TIMER_VALUE;
            }
    }

    /**
     * these empty functions are to be implemented as desired by the various guide modes
     */
    void preInitAllGuide() {}
    
    void postInitAllGuide() {}

    void preStopAllGuide() {}

    void postStopAllGuide() {}
    
    void processGuideModeRightKeyPressTemplate() {
        if (t.checkReadyToSaveGuide()) {
            handpadModeBeep();
            handpadButtonTimer = STARTING_HANDPAD_TIMER_VALUE;
        }
        else if (t.guideActive) {
            handpadModeBeep();
            handpadButtonTimer = STARTING_HANDPAD_TIMER_VALUE;
            preStopAllGuide();
            t.stopAllGuide();
            postStopAllGuide();
        }
    }
}
