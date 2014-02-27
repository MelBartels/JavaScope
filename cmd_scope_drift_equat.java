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

public class cmd_scope_drift_equat extends cmdScopeEquatBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        if (parseEquat(st))
            return parseSecondsAndObjectName(st);
        return false;
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        // equat m in units per hour
        if (sec <= 0.)
            sec = 3600.;
        cl.t.driftRaHr.rad = p.alt.rad / (3600.*sec);
        cl.t.driftDecHr.rad = p.az.rad / (3600.*sec);
        return true;
    }
}

