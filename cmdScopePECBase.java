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

public class cmdScopePECBase extends cmdScopeBase {
    SERVO_ID servoID;

    boolean parsePEC(StringTokenizer st) {
        // get SERVO_ID
        if (st.countTokens() >= 1) {
            servoID = null;
            servoID = SERVO_ID.matchStr(st.nextToken());
            if (servoID == null)
                console.errOut("bad SERVO_ID in " + cmdTypeScope);
            else
                return true;
        }
        else
            console.errOut("inadequate tokens in " + cmdTypeScope);
        return false;
    }

    public String buildCmdString() {
        String s;

        if (servoID != null)
            s = "   servoID: "
            + servoID
            + "\n";
        else
            s = "";

        return super.buildCmdString() + s;
    }
}

