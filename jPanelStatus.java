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
 * generic JPanel for status frames
 */
public class jPanelStatus extends JPanelColor implements ActionListener {
    private track t;
    private IJFrameStatusTextArea IJFrameStatusTextArea;
    private javax.swing.Timer timer;

    private JToggleButtonColor jToggleButtonStatusAutoUpdate;

    private javax.swing.JTextArea jTextAreaStatus;
    private javax.swing.JScrollPane jScrollPaneStatus;

    public jPanelStatus() {
        mediumColor();
        timer = new javax.swing.Timer(cfg.getInstance().updateUImilliSec, this);
        initComponents();
    }

    void registerTrackReference(track t) {
        this.t = t;
    }

    void registerIJFrameStatusTextArea(IJFrameStatusTextArea IJFrameStatusTextArea) {
        this.IJFrameStatusTextArea = IJFrameStatusTextArea;
    }

    private void initComponents() {
        jToggleButtonStatusAutoUpdate = new JToggleButtonColor();
        jTextAreaStatus = new javax.swing.JTextArea();
        jScrollPaneStatus = new javax.swing.JScrollPane();

        // be sure to remove the jPanelStatus. prefix when copying over from GUI Editing

        setLayout(new AbsoluteLayout());

        jToggleButtonStatusAutoUpdate.setText("auto update");
        jToggleButtonStatusAutoUpdate.setToolTipText("click here to refresh the status display");
        jToggleButtonStatusAutoUpdate.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonStatusAutoUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonStatusAutoUpdateActionPerformed(evt);
            }
        });

        add(jToggleButtonStatusAutoUpdate, new AbsoluteConstraints(390, 0, 110, -1));

        jTextAreaStatus.setEditable(false);
        jTextAreaStatus.setFont(new java.awt.Font("Dialog", 0, 10));
        jTextAreaStatus.setLineWrap(true);
        jTextAreaStatus.setText("(status display)");
        jTextAreaStatus.setWrapStyleWord(true);
        jScrollPaneStatus.setViewportView(jTextAreaStatus);

        add(jScrollPaneStatus, new AbsoluteConstraints(0, 30, 500, 480));
    }

    // timer stopped by JFrame window close event in class JFrameStatus
    private void jToggleButtonStatusAutoUpdateActionPerformed(java.awt.event.ActionEvent evt) {
        if (jToggleButtonStatusAutoUpdate.isSelected())
            startTimer();
        else
            stopTimer();
    }

    void startTimer() {
        timer.start();
    }

    void stopTimer() {
        timer.stop();
    }

    public void actionPerformed(ActionEvent e) {
        update();
    }

    // IJFrameStatusTextArea reference set in statusFrameCollection and passed in through JFrameStatus: this because of
    // private access on JPanel reference;
    // similarly, track reference passed in;
    void update() {
        jTextAreaStatus.setText(IJFrameStatusTextArea.update(t));
    }
}

