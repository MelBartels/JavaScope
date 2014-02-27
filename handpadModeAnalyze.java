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

public class handpadModeAnalyze extends handpadModeBase implements HandpadModes {
    public void processModeKeys() {
        position p = new position("handpadModeAnalyze");

        if (leftOrRightKeyPressed() && handpadButtonTimer == 0)
            if (cfg.getInstance().initialized()) {
                handpadModeBeep();
                handpadButtonTimer = STARTING_HANDPAD_TIMER_VALUE;
                // use current altazimuth coordinates, current sidereal time, and input equatorial coordinates
                p.copy(cfg.getInstance().current);
                p.ra.rad = t.in.ra.rad;
                p.dec.rad = t.in.dec.rad;
                t.c.calcAnalysisErrorAddToAnalysisFile(t.lpAnalysis, p);
            }
            else
                console.errOut("cannot turn on the analysis mode: must init 1 and 2 first");
    }
}

