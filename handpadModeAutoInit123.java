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

public class handpadModeAutoInit123 extends handpadModeBase implements HandpadModes {
    public void processModeKeys() {
        if (leftOrRightKeyPressed() && handpadButtonTimer == 0) {
            handpadModeBeep();
            handpadButtonTimer = STARTING_HANDPAD_TIMER_VALUE;
            // use current altaz and position 'in' equat coordinates to init with
            cfg.getInstance().current.ra.rad = t.in.ra.rad;
            cfg.getInstance().current.dec.rad = t.in.dec.rad;
            if (cfg.getInstance().three.init)
                // replace previous closest init with new init composed of in equat + current altaz
                t.c.initMatrix(t.c.findClosestInit(), WHY_INIT.handpad);
            else if (cfg.getInstance().two.init) {
                t.c.initMatrix(3, WHY_INIT.handpad);
                t.setSiTechAlt();
            }
            else if (cfg.getInstance().one.init) {
                t.c.initMatrix(2, WHY_INIT.handpad);
                t.getEquatSetTarget();
                t.setSiTechAlt();
            }
            else
                t.c.initMatrix(1, WHY_INIT.handpad);
        }
    }
}

