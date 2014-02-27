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
 * reads handpad based on handpadDesign as built by handpadDesignFactory();
 */
public class handpad {
    HandpadDesigns HandpadDesigns;
    // if FR and Focus motor control active on handpad
    boolean handpadFRFocusOn;
    // from particular handpadDesign;
    boolean handpadSuccessfullyRead;
    // read in from the particular handpadDesign, then speed switch setting subtracted;
    int buttons;
    // handpad's speed switch setting: if slowSpeedSwitch == SLOW_KEY then speed setting is 'slow', otherwise speed setting is 'fast'
    int slowSpeedSwitch;
    String handpadStatusString;

    handpad() {
        initHandpad();
    }

    void initHandpad() {}

    void readHandpad() {
        if (cfg.getInstance().handpadPresent)
            readHandpadPresent();
        else
            readHandpadNotPresent();
    }

    private void readHandpadPresent() {
        if (cfg.getInstance().handpadDesign == null
        || HandpadDesigns == null
        || cfg.getInstance().handpadDesign != HandpadDesigns.handpadDesign()) {
            HandpadDesigns = new handpadDesignFactory().build(cfg.getInstance().handpadDesign);
            if (HandpadDesigns == null)
                HandpadDesigns = new handpadDesignFactory().build(HANDPAD_DESIGN.handpadDesignStandard);
            cfg.getInstance().handpadDesign = HandpadDesigns.handpadDesign();
        }
        HandpadDesigns.readHandpad();
        handpadSuccessfullyRead = HandpadDesigns.handpadSuccessfullyRead();
        buttons = HandpadDesigns.buttons();
        // remove slow key value from buttons and place it in its own boolean parameter
        if ((buttons & handpadDesignBase.SLOW_KEY) == handpadDesignBase.SLOW_KEY) {
            buttons -= handpadDesignBase.SLOW_KEY;
            slowSpeedSwitch = handpadDesignBase.SLOW_KEY;
        }
        else
            slowSpeedSwitch = 0;
    }

    private void readHandpadNotPresent() {
        buttons = 0;
        slowSpeedSwitch = handpadDesignBase.SLOW_KEY;
        handpadSuccessfullyRead = true;
    }

    String buildHandpadStatusString() {
        if (cfg.getInstance().handpadPresent)
            if (cfg.getInstance().spa.handpadPortsRead)
                if (cfg.getInstance().spz.handpadPortsRead)
                    if (handpadSuccessfullyRead)
                        handpadStatusString = "handpad status: "
                        + (slowSpeedSwitch>0?"s":"f")
                        + ((buttons & handpadDesignBase.LEFT_KEY)==handpadDesignBase.LEFT_KEY?"l":" ")
                        + ((buttons & handpadDesignBase.RIGHT_KEY)==handpadDesignBase.RIGHT_KEY?"r":" ")
                        + ((buttons & handpadDesignBase.UP_KEY)==handpadDesignBase.UP_KEY?"U":" ")
                        + ((buttons & handpadDesignBase.DOWN_KEY)==handpadDesignBase.DOWN_KEY?"d":" ")
                        + ((buttons & handpadDesignBase.CW_KEY)==handpadDesignBase.CW_KEY?"c":" ")
                        + ((buttons & handpadDesignBase.CCW_KEY)==handpadDesignBase.CCW_KEY?"w":" ")
                        + (HandpadDesigns!=null && HandpadDesigns.upDownButtonsReversed()?"x":" ")
                        + HandpadDesigns.displayHandpad()
                        + "\n";
                    else
                        handpadStatusString = "\nhandpad in transition\n";
                else
                    handpadStatusString = "\n"
                    + SERVO_ID.altDec
                    + " handpad ports not read\n";
            else
                handpadStatusString = "\n"
                + SERVO_ID.azRa
                + " handpad ports not read\n";
        else
            handpadStatusString = "\nhandpad not present\n";

        return handpadStatusString;
    }

    void displayHandpadStatus() {
        console.stdOutLn(buildHandpadStatusString());
    }
}

