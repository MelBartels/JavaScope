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

/**
 * one row or record of track analysis data
 */
public class trackAnalysis {
    CMD_DEVICE cmdDevice;
    static final int MAX_S = 4;
    static final int MAX_V = 3;
    static final String[] s = new String[MAX_S];
    double[] v = new double[MAX_V];
    int ix;

    trackAnalysis() {
        s[ix++] = "cmdDevice        ";
        s[ix++] = "posErrArcsec     ";
        s[ix++] = "velArcsecSec     ";
        s[ix++] = "JDsec            ";

        ix = 0;
    }
}

