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

public class cmd_scope_new_z123_reinit extends cmdScopeBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {

        if (parseDouble(st)) {
            cfg.getInstance().z1Deg = d;
            if (parseDouble(st)) {
                cfg.getInstance().z2Deg = d;
                if (parseDouble(st)) {
                    cfg.getInstance().z3Deg = d;
                    return parseSecondsAndComment(st);
                }
            }
        }
        return false;
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        cl.t.pauseSequencer = true;
        position p = new position("cmd_scope_new_z123_reinit");
        p.copy(cfg.getInstance().current);
        cl.t.c.setMountErrorsDeg(cfg.getInstance().z1Deg, cfg.getInstance().z2Deg, cfg.getInstance().z3Deg);
        cfg.getInstance().current.copy(cfg.getInstance().one);
        cl.t.c.initMatrix(1, WHY_INIT.reInitConversionMatrix);
        cfg.getInstance().current.copy(p);
        cl.t.pauseSequencer = false;
        return true;
    }
}

