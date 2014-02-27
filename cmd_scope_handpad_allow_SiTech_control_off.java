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

public class cmd_scope_handpad_allow_SiTech_control_off extends cmdScopeBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        return parseSecondsAndComment(st);
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        if (cl.t!=null
        && cfg.getInstance().controllerManufacturer==CONTROLLER_MANUFACTURER.SiTech
        && cfg.getInstance().handpadPresent == true
        && cfg.getInstance().handpadDesign==HANDPAD_DESIGN.handpadDesignSiTech) {
            cl.t.HandpadDesigns.passiveObserver(false);
            return true;
        }
        return false;
    }
}

