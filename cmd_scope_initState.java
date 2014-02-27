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

public class cmd_scope_initState extends cmdScopeEquatBase implements cmdScope {
    INIT_STATE initState;

    public boolean parseCmd(StringTokenizer st) {
        if (st.countTokens() > 0) {
            initState = INIT_STATE.matchStr(st.nextToken());
            if (initState != null)
                return parseSecondsAndComment(st);
        }
        return false;
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        cl.t.c.init(initState);
        return true;
    }
}

