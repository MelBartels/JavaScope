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

public class cmd_scope_new_z12_from_inEquat_currAltaz extends cmdScopeBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        return parseSecondsAndComment(st);
    }

    public boolean process(cmdScopeList cl) {
        boolean rtn;

        super.process(cl);
        position p = new position("cmd_scope_new_z12_from_inEquat_currAltaz");
        cl.t.pauseSequencer = true;
        // use current altazimuth coordinates, current sidereal time, and input equatorial coordinates
        p.copy(cfg.getInstance().current);
        p.ra.rad = cl.t.in.ra.rad;
        p.dec.rad = cl.t.in.dec.rad;
        rtn = cl.t.c.computeBestZ12FromPosition(p);
        cl.t.pauseSequencer = false;
        return rtn;
    }
}

