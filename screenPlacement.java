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

import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 *com.sun.java.swing.plaf.motif.MotifLookAndFeel
 *com.sun.java.swing.plaf.windows.WindowsLookAndFeel
 *com.sun.java.swing.plaf.gtk.GTKLookAndFeel
 *javax.swing.plaf.metal.MetalLookAndFeel   same as basic
 *javax.swing.plaf.basic.BasicLookAndFeel   same as metal
 *on Mac:
 *    apple.laf.AquaLookAndFeel
 *    from SwingSet2.java demo by Sun: com.sun.java.swing.plaf.mac.MacLookAndFeel
 *
 *UIManager.setLookAndFeel(currentLookAndFeel);
 *
 *do this to get l&f:
 *System.out.println(UIManager.getSystemLookAndFeelClassName());
 */
public class screenPlacement extends javax.swing.JFrame {
    private Dimension display;
    private static screenPlacement INSTANCE;

    private screenPlacement() {}

    /**
     * double checked locking pattern for multi-threading protection:
     * synchronized block attempts to gain exclusive lock on cfg class; block code not executed until lock obtained;
     * if thread pre-empted after first INSTANCE==null comparision, then INSTANCE==null check within synchronized block
     * will tell the thread that object created behind its back;
     * not synchronized public static cfg getInstance() as this thread will block until getInstance() completes, slowing execution;
     */
    public static screenPlacement getInstance() {
        if (INSTANCE == null)
            synchronized(screenPlacement.class) {
                if (INSTANCE == null) {
                    INSTANCE = new screenPlacement();
                    INSTANCE.initialize();
                }
            }
        return INSTANCE;
    }

    private void initialize() {
        display = getToolkit().getScreenSize();
        console.stdOutLn("display size: "
        + display.width
        + " by "
        + display.height);

        if (cfg.getInstance().UILookAndFeel.length() > 0)
            try {
                if (cfg.getInstance().UILookAndFeel.equals("systemLookAndFeel"))
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                else if (cfg.getInstance().UILookAndFeel.equals("crossPlatformLookAndFeel"))
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                else
                    UIManager.setLookAndFeel(cfg.getInstance().UILookAndFeel);
            }
            catch (UnsupportedLookAndFeelException exc) {
                console.errOut("unsupportedLookAndFeel: " + cfg.getInstance().UILookAndFeel);
            }
            catch (Exception exc) {
                console.errOut("problem loading "
                + cfg.getInstance().UILookAndFeel
                + ": "
                + exc);
	    }
    }

    void center(JFrame frame) {
        frame.setLocation(display.width/2 - frame.getWidth()/2, display.height/2 - frame.getHeight()/2);
    }
}

