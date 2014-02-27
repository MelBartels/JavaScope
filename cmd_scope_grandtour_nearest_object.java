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

public class cmd_scope_grandtour_nearest_object extends cmdScopeBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        return parseSecondsAndComment(st);
    }

    public boolean process(cmdScopeList cl) {
        super.process(cl);
        position p = new position("cmd_scope_grandtour_nearest_object");

        if (cl.t.grandTour.lp.size() > 0)
            // cl.t.grandTour.currentIx set in following function
            return cl.t.grandTour.findNearestDataFileObject(cfg.getInstance().current, p);
        return false;
    }
}

