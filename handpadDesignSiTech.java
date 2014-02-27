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
 * optional SiTech controller will report the handpad and move status via the ID byte in the status return;
 * SiTech controller always moves the motors in response to the handpad;
 * movement detected via status ID byte, bit 7 (DAN_GRAY_USER_MOVED_SCOPE), of both motors;
 * handpad is more responsive with SiTech direct control;
 * passiveObserver mediates whether scope is merely observing handpad button presses and subsequent movement,
 * or, is to actively move the motors with velocity commands in response to decoding the handpad data lines;
 */
public class handpadDesignSiTech extends handpadDesignBase implements HandpadDesigns {
    static final int DAN_GRAY_LEFT = 1;
    static final int DAN_GRAY_RIGHT = 2;
    static final int DAN_GRAY_UP = 4;
    static final int DAN_GRAY_DOWN = 8;
    static final int DAN_GRAY_SWITCH = 16;
    static final int DAN_GRAY_TOP_RIGHT = 32;
    static final int DAN_GRAY_TOP_LEFT = 64;
    static final int DAN_GRAY_USER_MOVED_SCOPE = 128;

    double directionButtonReleaseSidTRad;
    boolean motionButtonPressed;

    handpadDesignSiTech() {
        passiveObserver = true;
        directionButtonReleaseSidTRad = astroTime.getInstance().sidT.rad;
    }

    // overrides base method to make sure that no button reversal occurs
    void setUpDownButtonsReversed() {
    }
    
    // overrides base method; 
    // handpad value passed in by handpad setter method found in base class and processed by setButtons();
    // handpad value set in status read of either motor;
    public void readHandpad() {
        boolean directionButtonFinished;
        handpadSuccessfullyRead = true;
        setUpDownButtonsReversed();
        setButtons();
        // set direction button release delay to avoid back tracking to position when button first released
        if (motionButtonPressed)
            directionButtonReleaseSidTRad = eMath.validRad(astroTime.getInstance().sidT.rad + cfg.getInstance().SiTechRampDownDelaySec*units.SEC_TO_RAD);
        // no debounce() needed here: deboucing is responsibility of controller that is reading the handpad;
        /**
         * if external controller indicates that it is moving motors on its own initiative based on its
         * reading of the handpad, then set .cmdDevice's to .SiTechHandpad;
         * when external handpad control no longer indicates motion button press, wait a short time,
         * then set .cmdDevice's to .none;
         */
        directionButtonFinished = directionButtonReleaseDelayFinished();
        
        if (!directionButtonFinished || (handpad & DAN_GRAY_USER_MOVED_SCOPE) == DAN_GRAY_USER_MOVED_SCOPE)
            cfg.getInstance().spa.cmdDevice = cfg.getInstance().spz.cmdDevice = CMD_DEVICE.SiTechHandpad;

        if (directionButtonFinished) {
            if (cfg.getInstance().spa.cmdDevice == CMD_DEVICE.SiTechHandpad)
                cfg.getInstance().spa.cmdDevice = CMD_DEVICE.none;
            if (cfg.getInstance().spz.cmdDevice == CMD_DEVICE.SiTechHandpad)
                cfg.getInstance().spz.cmdDevice = CMD_DEVICE.none;
        }
    }

    boolean directionButtonReleaseDelayFinished() {
        double tDiffRad;

        tDiffRad =  eMath.validRadPi(directionButtonReleaseSidTRad - astroTime.getInstance().sidT.rad);
        if (tDiffRad >= 0.)
            return false;
        // keep button release time same as current sidereal time, otherwise after 12 hrs of inactivity, method will fail
        directionButtonReleaseSidTRad = astroTime.getInstance().sidT.rad;
        return true;
    }

    void setButtons() {
        buttons = 0;
        motionButtonPressed = false;
        if ((handpad & DAN_GRAY_LEFT) == DAN_GRAY_LEFT) {
            buttons += CCW_KEY;
            motionButtonPressed = true;
        }
        if ((handpad & DAN_GRAY_RIGHT) == DAN_GRAY_RIGHT) {
            buttons += CW_KEY;
            motionButtonPressed = true;
        }
        if ((handpad & DAN_GRAY_UP) == DAN_GRAY_UP) {
            buttons += UP_KEY;
            motionButtonPressed = true;
        }
        if ((handpad & DAN_GRAY_DOWN) == DAN_GRAY_DOWN) {
            buttons += DOWN_KEY;
            motionButtonPressed = true;
        }
        if ((handpad & DAN_GRAY_SWITCH) == DAN_GRAY_SWITCH)
            buttons += SLOW_KEY;
        if ((handpad & DAN_GRAY_TOP_LEFT) == DAN_GRAY_TOP_LEFT)
            buttons += LEFT_KEY;
        if ((handpad & DAN_GRAY_TOP_RIGHT) == DAN_GRAY_TOP_RIGHT)
            buttons += RIGHT_KEY;
    }

    public String displayHandpad() {
        String s = super.displayHandpad();

        if ((handpad & DAN_GRAY_USER_MOVED_SCOPE) == DAN_GRAY_USER_MOVED_SCOPE)
            s += " controller moved scope,";
        s += " SiTech controller operating handpad";
        return s;
    }
}

