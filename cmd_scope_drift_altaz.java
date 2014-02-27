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

public class cmd_scope_drift_altaz extends cmdScopeAltazBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        if (parseAZ(st))
            return parseSecondsAndComment(st);
        return false;
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        // altaz drift in units per minute
        if (sec <= 0.)
            sec = 60.;
        cfg.getInstance().spa.driftRadMin = p.alt.rad / (60.*sec);
        cfg.getInstance().spz.driftRadMin = p.az.rad / (60.*sec);
        cl.t.calcDriftArcsecPerMin();
        return true;
    }
}

