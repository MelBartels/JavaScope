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

public class cmd_scope_handpad_CCW_stop extends cmdScopeBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        return parseSecondsAndComment(st);
    }

    public boolean process(cmdScopeList cl) {
        int id;

        if (cfg.getInstance().handpadMode == HANDPAD_MODE.handpadModeFRFocus)
            id = SERVO_ID.focus.KEY;
        else
            id = SERVO_ID.azRa.KEY;
        cl.t.stopDeviceMove(CMD_DEVICE.cmdScopeList, id);
        return true;
    }
}

