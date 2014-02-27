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

public class cmd_scope_encoder_type extends cmdScopeBase implements cmdScope {
    ENCODER_TYPE encoderType;

    public boolean parseCmd(StringTokenizer st) {
        if (st.countTokens() > 0)
            encoderType = ENCODER_TYPE.matchStr(st.nextToken());
        return parseSecondsAndComment(st);
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);

        if (encoderType == null) {
            console.errOut("cmd_scope_encoder_type encoderType==null");
            return false;
        }
        // if port open, then close it
        if (cl.t.ef != null
            && cl.t.ef.E != null
            && cl.t.ef.E.encoderType() != encoderType
            && cl.t.ef.E.portOpened())
                cl.t.ef.E.close();
        // build new type if: 1) no type built, or, 2) new type different than current type
        if (cl.t.ef == null
        || cl.t.ef.E == null
        || cl.t.ef.E.encoderType() != encoderType) {
            cfg.getInstance().encoderType = encoderType;
            cl.t.buildEncoders();
        }
        else
            console.stdOutLn("cmd_scope_encoder_type did not need to build encoders");
        return true;
    }
}

