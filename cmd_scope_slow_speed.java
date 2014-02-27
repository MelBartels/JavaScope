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

public class cmd_scope_slow_speed extends cmdScopeBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        if (parseDouble(st))
            return parseSecondsAndComment(st);
        return false;
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        int id;

        for (id = 0; id < SERVO_ID.size(); id++) {
            if (SERVO_ID.matchKey(id) != SERVO_ID.focus) {
                cfg.getInstance().servoParm[id].slowSpeedArcsecSec = d;
                cfg.getInstance().servoParm[id].slowSpeedRadSec = cfg.getInstance().servoParm[id].slowSpeedArcsecSec * units.ARCSEC_TO_RAD;
            }
        }
        if (cfg.getInstance().controllerManufacturer == CONTROLLER_MANUFACTURER.SiTech)
            cl.t.initSiTechHandpadSlewPanGuideSpeeds();
        return true;
    }
}

