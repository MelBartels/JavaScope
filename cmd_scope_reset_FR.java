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

public class cmd_scope_reset_FR extends cmdScopeEquatBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        return parseSecondsAndComment(st);
    }
    
    public boolean process(cmdScopeList cl) {
        super.process(cl);
        if (cfg.getInstance().initialized()) {
            cl.t.c.getEquat();
            cl.t.c.calcFieldRotation();
            console.stdOutLn("resetting to current FR angle: " + cl.t.c.fieldRotation * units.RAD_TO_DEG + " deg");
            console.stdOutLn("old FR position: " + cfg.getInstance().spr.actualPositionDeg + " deg");
            cfg.getInstance().spr.currentPositionOffsetRad =
                    cl.t.c.fieldRotation - (double) cfg.getInstance().spr.actualPosition * cfg.getInstance().spr.countToRad;
            cl.t.setCurrentPositionDeg(SERVO_ID.fieldR.KEY);
            console.stdOutLn("new FR position: " + cfg.getInstance().spr.currentPositionDeg + " deg");
            return true;
        }
        return false;
    }
}

