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

public class cmd_scope_reset_encoders_type extends cmdScopeBase implements cmdScope {
    ENCODER_TYPE encoderType;

    public boolean parseCmd(StringTokenizer st) {
        if (st.countTokens() > 0)
            encoderType = ENCODER_TYPE.matchStr(st.nextToken());
        return parseSecondsAndComment(st);
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);

        if (encoderType == null)
            return false;
        if (cl.t.ef != null
            && cl.t.ef.E != null
            && cl.t.ef.E.portOpened())
                return cl.t.ef.E.reset();
        return false;
    }
}

