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

public class handpadModeInit3 extends handpadModeBase implements HandpadModes {
    public void processModeKeys() {
        if (leftOrRightKeyPressed() && handpadButtonTimer == 0)
            if (cfg.getInstance().initialized()) {
                handpadModeBeep();
                handpadButtonTimer = STARTING_HANDPAD_TIMER_VALUE;
                // use cfg.getInstance().current altaz and t.in equat coordinates to init with
                cfg.getInstance().current.ra.rad = t.in.ra.rad;
                cfg.getInstance().current.dec.rad = t.in.dec.rad;
                t.c.initMatrix(3, WHY_INIT.handpad);
                t.getEquatSetTarget();
                t.setSiTechAlt();
            }
            else
                console.errOut("cannot init#3: must init 1 and 2 first");
    }
}

