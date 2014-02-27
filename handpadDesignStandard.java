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
 * in standard design, data lines are multiplexed to indicate button/key, hence only 1 button/key can be indicated at a time
 */
public class handpadDesignStandard extends handpadDesignBase implements HandpadDesigns {
    private static final int RAW_UP_KEY = 16;
    private static final int RAW_DOWN_KEY = 32;
    private static final int RAW_CCW_KEY = 64;
    private static final int RAW_CW_KEY = 16 + 32;
    private static final int RAW_LEFT_KEY = 16 + 64;
    private static final int RAW_RIGHT_KEY = 32 + 64;
    private static final int RAW_SLOW_KEY = 128;

    handpadDesignStandard() {
        passiveObserver = false;
    }

    void setButtons() {
        int rButtons;

        rButtons = handpad - (handpad&RAW_SLOW_KEY);
        if (upDownButtonsReversed)
            if (rButtons == RAW_UP_KEY)
                rButtons = RAW_DOWN_KEY;
            else if (rButtons == RAW_DOWN_KEY)
                rButtons = RAW_UP_KEY;
        switch(rButtons) {
            case RAW_UP_KEY:
                buttons = UP_KEY;
                break;
            case RAW_DOWN_KEY:
                buttons = DOWN_KEY;
                break;
            case RAW_CCW_KEY:
                buttons = CCW_KEY;
                break;
            case RAW_CW_KEY:
                buttons = CW_KEY;
                break;
            case RAW_LEFT_KEY:
                buttons = LEFT_KEY;
                break;
            case RAW_RIGHT_KEY:
                buttons = RIGHT_KEY;
                break;
            case 0:
                buttons = 0;
        }
        if ((handpad & RAW_SLOW_KEY) == RAW_SLOW_KEY)
            buttons += SLOW_KEY;
    }
}

