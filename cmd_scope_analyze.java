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

public class cmd_scope_analyze extends cmdScopeBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        return parseSecondsAndComment(st);
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        position p = new position("cmd_scope_analyze");
        // use current altazimuth coordinates, current sidereal time, and input equatorial coordinates
        p.copy(cfg.getInstance().current);
        p.ra.rad = cl.t.in.ra.rad;
        p.dec.rad = cl.t.in.dec.rad;
        cl.t.c.calcAnalysisErrorAddToAnalysisFile(cl.t.lpAnalysis, p);
        return true;
    }
}

