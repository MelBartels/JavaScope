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

public class cmd_scope_pec_synch extends cmdScopePECBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        if (parsePEC(st))
            return parseSecondsAndComment(st);
        return false;
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        if (servoID != null) {
            cl.t.synchPEC(servoID.KEY);
            return true;
        }
        return false;
    }
}

