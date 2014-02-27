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

public class cmd_scope_reset_meridian_flip extends cmdScopeBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        return parseSecondsAndComment(st);
    }
    
    public boolean process(cmdScopeList cl) {
        super.process(cl);
        cl.t.turnTrackingOffStopAllMotors();
        cl.t.waitForAllMotorsStop();
        if (cfg.getInstance().Mount.meridianFlipPossible()) {
            
            cfg.getInstance().Mount.meridianFlip().flipped = !cfg.getInstance().Mount.meridianFlip().flipped;
            
            //cl.t.setCurrentAltazGetEquatCopyTarget();
            
            cfg.getInstance().current.alt.rad = units.HALF_REV - cfg.getInstance().current.alt.rad;
            if (cfg.getInstance().current.alt.rad > units.HALF_REV)
                cfg.getInstance().current.alt.rad -= units.ONE_REV;
            
            cl.t.setServoPositionToCurrentAltaz();
            cfg.getInstance().current.sidT.rad = astroTime.getInstance().sidT.rad;
            cl.t.c.getEquat();
            // moveToTargetEquat() tracks to target.ra.rad and target.dec.rad so update target equat coord
            cl.t.target.copy(cfg.getInstance().current);
        }
        return true;
    }
}

