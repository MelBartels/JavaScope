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

public class cmd_scope_init2 extends cmdScopeEquatBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        if (parseEquat(st))
            return parseSecondsAndObjectName(st);
        return false;
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        cl.t.in.copy(p);
        cfg.getInstance().current.objName = cl.t.in.objName;
        cfg.getInstance().current.ra.rad = cl.t.in.ra.rad;
        cfg.getInstance().current.dec.rad = cl.t.in.dec.rad;
        if (cfg.getInstance().one.init) {
            cl.t.c.initMatrix(2, WHY_INIT.cmdScopeList);
            cl.t.getEquatSetTarget();
            cl.t.setSiTechAlt();
            return true;
        }
        return false;
    }
}

