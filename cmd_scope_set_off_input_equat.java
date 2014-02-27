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

public class cmd_scope_set_off_input_equat extends cmdScopeEquatBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        if (parseEquat(st))
            return parseSecondsAndObjectName(st);
        return false;
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        cl.t.in.ra.rad = eMath.validRad(cfg.getInstance().current.ra.rad + p.ra.rad);
        cl.t.in.dec.rad = cfg.getInstance().current.dec.rad + p.dec.rad;
        return true;
    }
}

