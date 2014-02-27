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

public class cmd_scope_guide_off extends cmdScopeBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        return parseSecondsAndComment(st);
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        if (cl.t.guideActive) {
            if (cl.t.HandpadModes != null)
                cl.t.HandpadModes.handpadModeBeep();
            if (cfg.getInstance().handpadUpdateDrift) {
                cl.t.setEndDriftT();
                cl.t.updateDriftCalculatedFromAccumGuide();
            }
            cl.t.stopAllGuide();
            return true;
        }
        return false;
    }
}

