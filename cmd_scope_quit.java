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

public class cmd_scope_quit extends cmdScopeBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        comment = common.tokensToString(st);
        return true;
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        console.stdOutLn("quitting now " + comment);
        cl.t.turnTrackingOffStopAllMotors();
        cl.t.shutdown = true;
        return true;
    }
}

