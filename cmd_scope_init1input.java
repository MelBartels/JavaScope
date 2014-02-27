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

public class cmd_scope_init1input extends cmdScopeBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        return parseSecondsAndComment(st);
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        cfg.getInstance().current.ra.rad = cl.t.in.ra.rad;
        cfg.getInstance().current.dec.rad = cl.t.in.dec.rad;
        cl.t.c.initMatrix(1, WHY_INIT.cmdScopeList);
        return true;
    }
}

