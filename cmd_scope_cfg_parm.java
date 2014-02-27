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

public class cmd_scope_cfg_parm extends cmdScopeCfgParmBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        cfgParmString = common.tokensToString(st);
        return true;
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        String name;
        StringTokenizer st = new StringTokenizer(cfgParmString);

        if (st.countTokens() >= 2) {
            name = st.nextToken();
            if (cfg.getInstance().processCfgParm(name, st))
                return true;
            else if (cfg.getInstance().processServoParm(name, st))
                return true;
            else
                console.errOut("could not interpret configuration line: " + cfgParmString);
        }
        return false;
    }
}

