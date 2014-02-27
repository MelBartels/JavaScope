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

public class cmd_scope_focus_fast_speed extends cmdScopeFocusBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
         if (parseDouble(st))
             return parseSecondsAndComment(st);
         return false;
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        // deg/sec = encoderCounts/sec * deg/rev * rev/encoderCounts
        cfg.getInstance().spf.fastSpeedDegSec = d * 360. / cfg.getInstance().spf.encoderCountsPerRev;
        cfg.getInstance().spf.fastSpeedRadSec = cfg.getInstance().spf.fastSpeedDegSec * units.DEG_TO_RAD;
        return true;
    }
}

