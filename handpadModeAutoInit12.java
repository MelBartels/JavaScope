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

public class handpadModeAutoInit12 extends handpadModeBase implements HandpadModes {
    public void processModeKeys() {
        if (leftKeyPressed() && handpadButtonTimer == 0) {
            handpadModeBeep();
            handpadButtonTimer = STARTING_HANDPAD_TIMER_VALUE;
            // use current altaz to init with
            cfg.getInstance().current.ra.rad = t.autoInit1.ra.rad;
            cfg.getInstance().current.dec.rad = t.autoInit1.dec.rad;
            cfg.getInstance().current.objName = t.autoInit1.objName;
            t.c.initMatrix(1, WHY_INIT.handpad);
        }
        else if (rightKeyPressed() && handpadButtonTimer == 0) {
            handpadModeBeep();
            handpadButtonTimer = STARTING_HANDPAD_TIMER_VALUE;
            // use current altaz to init with
            cfg.getInstance().current.ra.rad = t.autoInit2.ra.rad;
            cfg.getInstance().current.dec.rad = t.autoInit2.dec.rad;
            cfg.getInstance().current.objName = t.autoInit2.objName;
            if (cfg.getInstance().one.init) {
                t.c.initMatrix(2, WHY_INIT.handpad);
                t.getEquatSetTarget();
                t.setSiTechAlt();
            }
        }

    }
}

