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

public class cmd_scope_reset_focus extends cmdScopeFocusBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        if (parseFocus(st))
            return parseSecondsAndComment(st);
        return false;
    }

    public boolean process(cmdScopeList cl) {
        // offset = current - actual
        cfg.getInstance().spf.currentPositionOffsetRad = (focusPosition - (double) cfg.getInstance().spf.actualPosition) * cfg.getInstance().spf.countToRad;
        cl.t.setCurrentPositionDeg(SERVO_ID.focus.KEY);
        return true;
    }
}

