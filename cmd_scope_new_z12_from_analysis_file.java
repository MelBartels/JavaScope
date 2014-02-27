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

public class cmd_scope_new_z12_from_analysis_file extends cmdScopeBase implements cmdScope {
    public boolean parseCmd(StringTokenizer st) {
        return parseSecondsAndComment(st);
    }

    public boolean process(cmdScopeList cl) {
        boolean rtn;

        super.process(cl);
        cl.t.pauseSequencer = true;
        rtn = cl.t.c.computeBestZ12FromAnalysisFile(cl.t.lpAnalysis);
        cl.t.pauseSequencer = false;
        return rtn;
    }
}

