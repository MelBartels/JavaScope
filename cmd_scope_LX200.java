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

public class cmd_scope_LX200 extends cmdScopeBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        String s;

        LX200str = "";
        do {
            if (st.countTokens() > 0) {
                LX200str += st.nextToken();
                if (LX200str.indexOf('#') > -1)
                    break;
            }

        }while (st.countTokens() > 0);

        // build new string that does not contain the delimiters as tokens: StringTokenizer created with delimiters as tokens
        // for this class in cmdScopeList.parseStringBuildCmd()
        s = "";
        while (st.countTokens() > 0)
            s += st.nextToken();

        return parseSecondsAndComment(new StringTokenizer(s));
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        console.stdOutLn(cl.name
        + " LX200 command: "
        + LX200str()
        + " "
        + comment);
        cl.t.cmdCol.cmdLX200.readLX200Input(LX200str());
        return true;
    }
}

