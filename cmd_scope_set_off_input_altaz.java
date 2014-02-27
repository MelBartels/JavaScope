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

public class cmd_scope_set_off_input_altaz extends cmdScopeAltazBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        if (parseAZ(st))
            return parseSecondsAndComment(st);
        return false;
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        cl.t.in.alt.rad = cfg.getInstance().current.alt.rad + p.alt.rad;
        cl.t.in.az.rad = eMath.validRad(cfg.getInstance().current.az.rad + p.az.rad);
        return true;
    }
}

