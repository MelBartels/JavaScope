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
 * adds in drag drift from config.dat, then removes it;
 * if drift were to be updated, then follow this algorithm (work only with altaz drift as driftdrag does all 4 axes):
 * if cfg.getInstance().current drift = 10, and dragdrift = 20, then drift will be 30, at end of guide cycle, drift calculated from guide
 * will = -20, take the difference between dragdrift and calculated drift (here 20-20=0) and add it to the drift of 30,
 * then remove dragdrift of 20 for final cfg.getInstance().current drift of 10; if drift should really be 5, then drift calculated from
 * guide would be -25, amount to subtract would be -5, and after subtracting dragdrift of 20, result would be 5
 */
public class handpadModeGuideStayDrag extends handpadModeBase implements HandpadModes {
    void postInitAllGuide() {
        t.addGuideDragToDrift();
        t.calcDriftArcsecPerMin();
    }
    
    void preStopAllGuide() {
        // this method will cause the scope to stay on the guide star when guiding finished
        t.addAccumGuideDriftToCurrentPosition();
    }
    
    void postStopAllGuide() {
        t.removeGuideDragFromDrift();
        t.calcDriftArcsecPerMin();
    }

    public void processModeKeys() {
        processGuideModeKeys();
    }
}

