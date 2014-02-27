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

public class cmd_scope_focus_slow_speed extends cmdScopeFocusBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
         if (parseDouble(st))
             return parseSecondsAndComment(st);
         return false;
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        // arcsec/sec = encoderCounts/sec * arcsec/rev * rev/encoderCounts
        cfg.getInstance().spf.slowSpeedArcsecSec = d * units.REV_TO_ARCSEC / cfg.getInstance().spf.encoderCountsPerRev;
        cfg.getInstance().spf.slowSpeedRadSec = cfg.getInstance().spf.slowSpeedArcsecSec * units.ARCSEC_TO_RAD;
        return true;
    }
}

