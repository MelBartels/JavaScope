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

public class cmd_scope_handpad_right_mode_key extends cmdScopeBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        return parseSecondsAndComment(st);
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        int holdButtons;

        holdButtons = cl.t.buttons;
        cl.t.buttons = handpadDesignBase.RIGHT_KEY;
        cl.t.processHandpadModeSwitch();
        cl.t.buttons = holdButtons;
        return true;
    }
}

