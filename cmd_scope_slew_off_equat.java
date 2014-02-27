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

public class cmd_scope_slew_off_equat extends cmdScopeEquatBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        if (parseEquat(st)) {
            parseMoveTimeSec(st);
            return parseSecondsAndObjectName(st);
        }
        return false;
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        position p2 = new position("cmd_scope_slew_off_equat");

        p2.ra.rad = cfg.getInstance().current.ra.rad + p.ra.rad;
        p2.dec.rad = cfg.getInstance().current.dec.rad + p.dec.rad;
        cl.t.copyPosToInAndTargetThenTurnTrackingOn(p2);
        if (moveTimeSec > 0.)
            equatMoveOverTime();
        return true;
    }
}

