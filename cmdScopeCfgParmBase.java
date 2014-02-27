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

public class cmdScopeCfgParmBase extends cmdScopeBase {
    String cfgParmString;

    public String buildCmdString() {
        String s;

        if (cfgParmString != null && cfgParmString.length() > 0)
            s = "   cfgParm: "
            + cfgParmString
            + "\n";
        else
            s = "";

        return super.buildCmdString() + s;
    }
}

