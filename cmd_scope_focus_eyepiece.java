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

public class cmd_scope_focus_eyepiece extends cmdScopeFocusBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        if (st.countTokens() > 0) {
            eyepieceName = new String(st.nextToken());
            moveTimeSec = 0.;
            try {
                if (st.countTokens() > 0)
                moveTimeSec = Double.parseDouble(st.nextToken());
            }
            catch (NumberFormatException nfe) {
                console.errOut("bad moveTimeSec for cmd_scope_focus_eypiece");
            }
            return parseSecondsAndComment(st);
        }
        return false;
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        Iterator it;
        eyepieceFocus ef;

        it = cfg.getInstance().lef.iterator();
        while( it.hasNext()) {
            ef = (eyepieceFocus) it.next();
            if (eyepieceName.equalsIgnoreCase(ef.name)) {
                cfg.getInstance().spf.targetPosition = ef.position;
                cfg.getInstance().spf.backlash.addBacklashToTargetPosition(SERVO_ID.focus.KEY);
                cfg.getInstance().spf.cmdDevice = CMD_DEVICE.cmdScopeList;
                cfg.getInstance().spf.velRadSec = cfg.getInstance().spf.fastSpeedRadSec;

                moveToTargetFocus();
                return true;
            }
        }
        return false;
    }
}

