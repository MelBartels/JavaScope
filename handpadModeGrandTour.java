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

public class handpadModeGrandTour extends handpadModeBase implements HandpadModes {
    public void processModeKeys() {
        /**
         * external control should load grand tour file and set current pointer to first link, and, end grand tour by setting
         * current pointer to null;
         * grand tour can be interrupted by changing handpad mode;
         * left key moves onto next object in list, right key returns to previous object in list
         */
        if (cfg.getInstance().initialized() && leftOrRightKeyPressed() && grandTourTimer == 0)
            if (t.grandTour.lp.size() > 0) {
                handpadModeBeep();
                grandTourTimer = STARTING_HANDPAD_TIMER_VALUE;
                if (leftKeyPressed())
                    t.grandTour.incrementCurrentIx();
                if (rightKeyPressed())
                    t.grandTour.decrementCurrentIx();
                t.in.copy(t.grandTour.lp.get(t.grandTour.currentIx));
                t.target.copy(t.in);
            }
    }
}

