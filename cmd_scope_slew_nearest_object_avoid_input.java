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

public class cmd_scope_slew_nearest_object_avoid_input extends cmdScopeObjectBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        if (parseFilename(st))
            return parseSecondsAndComment(st);
        return false;
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        position p = new position("cmd_scope_slew_nearest_object_avoid_input");
        dataFile df = new dataFile();

        df.loadFromFile(filename);
        df.findNearestDataFileObjectAvoidPos(cfg.getInstance().current, p, cl.t.in);
        cl.t.copyPosToInAndTargetThenTurnTrackingOn(p);
        return true;
    }
}

