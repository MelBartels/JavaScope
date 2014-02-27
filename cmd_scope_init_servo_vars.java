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

public class cmd_scope_init_servo_vars extends cmdScopeBase implements cmdScope {
    String s;
    int id;

    public boolean parseCmd(StringTokenizer st) {
        id = -1;
        if (parseInt(st)) {
            id = i;
            return parseSecondsAndComment(st);
        }
        return false;
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        if (id < 0 || id > SERVO_ID.size())
            return false;
        else {
            cl.t.initServoIDVars(id);
            return true;
        }
    }
}

