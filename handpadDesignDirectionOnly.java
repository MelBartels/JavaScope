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
 * in this design, simultaneous directions are possible since each data line is a direction line
 */
public class handpadDesignDirectionOnly extends handpadDesignBase implements HandpadDesigns {
    private static final int RAW_UP_KEY = 16;
    private static final int RAW_DOWN_KEY = 32;
    private static final int RAW_CCW_KEY = 64;
    private static final int RAW_CW_KEY = 128;

    handpadDesignDirectionOnly() {
        passiveObserver = false;
    }

    void setButtons() {
        if ((handpad & RAW_UP_KEY) > 0)
            if (upDownButtonsReversed)
                buttons = DOWN_KEY;
            else
                buttons = UP_KEY;

        if ((handpad & RAW_DOWN_KEY) > 0)
            if (upDownButtonsReversed)
                buttons += UP_KEY;
            else
                buttons += DOWN_KEY;

        if ((handpad & RAW_CCW_KEY) > 0)
            buttons += CCW_KEY;

        if ((handpad & RAW_CW_KEY) > 0)
            buttons += CW_KEY;
    }
}

