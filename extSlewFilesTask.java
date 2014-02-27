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
 * this class checks asynchronously for changes in external slew files;
 * a change indicates new coordinates to act on;
 */
public class extSlewFilesTask extends TimerTask {
    track t;

    extSlewFilesTask(track t) {
        this.t = t;
    }

    public void run() {
        if (t.cmdCol.extSlewFiles.newEquatSlewDat()) {
            t.cmdCol.extSlewFiles.inputEquatSlewDat(t.in);
            t.target.copy(t.in);
            console.stdOutLn("updated "
            + t.cmdCol.extSlewFiles.slewFile
            + " found; new target equatorial coordinates are:");
            t.target.showCoord();
        }
        t.cmdCol.extSlewFiles.writeEquatSlewOutFile(cfg.getInstance().current);
    }
}

