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
 * this class updates encoders asynchronously;
 */
public class updateEncodersTask extends TimerTask {
    track t;

    updateEncodersTask(track t) {
        this.t = t;
    }

    public void run() {
        if (t.ef != null && t.ef.E != null)
            t.ef.E.readEncodersAndProcessPositions();
    }
}

