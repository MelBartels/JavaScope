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
 *this class puts jPanelTerminal into its own frame
 */
public class JFrameTerminal {
    protected JFrame frame;
    private JPanel panel;
    private jPanelTerminal jPanelTerminal;

    JFrameTerminal(String title) {
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        panel = (JPanel) frame.getContentPane();
        panel.setLayout(new AbsoluteLayout());
        jPanelTerminal = new jPanelTerminal();
        panel.add(jPanelTerminal, new AbsoluteConstraints(0, 0, 400, 300));
        frame.pack();
        screenPlacement.getInstance().center(frame);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                stop();
            }
        });
    }

    void registerTrackReference(track t) {
        jPanelTerminal.registerTrackReference(t);
    }

    void registerIOReference(IO io) {
        jPanelTerminal.registerIOReference(io);
    }

    void appendReturn(boolean append) {
        jPanelTerminal.appendReturn(append);
    }

    void setVisible(boolean value) {
        frame.setVisible(value);
    }

    void start() {
        setVisible(true);
        jPanelTerminal.start();
    }

    void stop() {
        jPanelTerminal.stop();
        setVisible(false);
    }
}

