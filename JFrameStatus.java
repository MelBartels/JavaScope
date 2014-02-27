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
 *this class puts jPanelStatus into its own frame
 */
public class JFrameStatus {
    private track t;
    private JFrame frame;
    private JPanel panel;
    private jPanelStatus jPanelStatus;

    JFrameStatus(String title) {
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        panel = (JPanel) frame.getContentPane();
        panel.setLayout(new AbsoluteLayout());
        jPanelStatus = new jPanelStatus();
        panel.add(jPanelStatus, new AbsoluteConstraints(0, 0, 500, 500));
        frame.pack();
        screenPlacement.getInstance().center(frame);

        // stop panel timer that updates textarea if window closed to save cpu cycles
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                jPanelStatus.stopTimer();
            }
        });
    }

    void registerTrackReference(track t) {
        this.t = t;
        jPanelStatus.registerTrackReference(t);
    }

    void registerIJFrameStatusTextArea(IJFrameStatusTextArea IJFrameStatusTextArea) {
        jPanelStatus.registerIJFrameStatusTextArea(IJFrameStatusTextArea);
    }

    void setVisible(boolean value) {
        frame.setVisible(value);
        jPanelStatus.update();
    }
}

