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
 * the handpad panel
 */
public class jPanelHandpad extends JPanelColor {
    track t;
    CMD_SCOPE cs;

    private buttonsOffMediator motionButtonsOffMediator;
    private buttonsOffMediator stopButtonsOffMediator;
    private handpadActionListener handpadActionListener;

    private JRadioButtonColor jRadioButtonHandpadFast;
    private JRadioButtonColor jRadioButtonHandpadSlow;

    private JToggleButtonColor jToggleButtonHandpadStop;
    private JToggleButtonColor jToggleButtonHandpadCCW;
    private JToggleButtonColor jToggleButtonHandpadCW;
    private JToggleButtonColor jToggleButtonHandpadDown;
    private JToggleButtonColor jToggleButtonHandpadUp;
    private JToggleButtonColor jToggleButtonHandpadModeOn;
    private JToggleButtonColor jToggleButtonHandpadModeOff;

    private javax.swing.ButtonGroup buttonGroupFastSlow;
    private JComboBoxColor jComboBoxHandpadModes;
    private javax.swing.JLabel jLabelHandpadTitle;
    private javax.swing.JLabel jLabelHandpadMode;
    private javax.swing.JLabel jLabelHandpadFastSpeed;
    private javax.swing.JLabel jLabelHandpadSlowSpeed;
    private javax.swing.JPanel jPanelHandpadMode;
    private javax.swing.JTextField jTextFieldHandpadFastSpeed;
    private javax.swing.JTextField jTextFieldHandpadSlowSpeed;

    public jPanelHandpad() {
        int ix;

        lightColor();

        initComponents();

        handpadActionListener = new handpadActionListener(jComboBoxHandpadModes);
        handpadActionListener.startTimer();

        // fill in handpad modes
        Enumeration eHANDPAD_MODE = HANDPAD_MODE.elements();
        while (eHANDPAD_MODE.hasMoreElements()) {
            HANDPAD_MODE hm = (HANDPAD_MODE) eHANDPAD_MODE.nextElement();
            jComboBoxHandpadModes.addItem(hm);
        }

        // start with slow speed selected
        jRadioButtonHandpadSlow.setSelected(true);

        // try to display common fast/slow speeds
        if (cfg.getInstance().spa.fastSpeedDegSec == cfg.getInstance().spz.fastSpeedDegSec)
            jTextFieldHandpadFastSpeed.setText(eString.doubleToStringNoGrouping(cfg.getInstance().spa.fastSpeedDegSec, 2, 1));
        if (cfg.getInstance().spa.slowSpeedArcsecSec == cfg.getInstance().spz.slowSpeedArcsecSec)
            jTextFieldHandpadSlowSpeed.setText(eString.doubleToStringNoGrouping(cfg.getInstance().spa.slowSpeedArcsecSec, 4, 1));
    }

    void registerTrackReference(track t) {
        this.t = t;
        handpadActionListener.registerTrackReference(t);
    }

    void addMotionButtonsOffMediator(buttonsOffMediator motionButtonsOffMediator) {
        this.motionButtonsOffMediator = motionButtonsOffMediator;

        motionButtonsOffMediator.register(jToggleButtonHandpadUp);
        motionButtonsOffMediator.register(jToggleButtonHandpadDown);
        motionButtonsOffMediator.register(jToggleButtonHandpadCW);
        motionButtonsOffMediator.register(jToggleButtonHandpadCCW);
    }

    void addStopButtonsOffMediator(buttonsOffMediator stopButtonsOffMediator) {
        this.stopButtonsOffMediator = stopButtonsOffMediator;

        stopButtonsOffMediator.register(jToggleButtonHandpadStop);
    }

    private void initComponents() {
        jRadioButtonHandpadFast = new JRadioButtonColor();
        jRadioButtonHandpadSlow = new JRadioButtonColor();

        (jToggleButtonHandpadStop = new JToggleButtonColor()).stopColor();
        (jToggleButtonHandpadCCW = new JToggleButtonColor()).goColor();
        (jToggleButtonHandpadCW = new JToggleButtonColor()).goColor();
        (jToggleButtonHandpadDown = new JToggleButtonColor()).goColor();
        (jToggleButtonHandpadUp = new JToggleButtonColor()).goColor();
        jToggleButtonHandpadModeOff = new JToggleButtonColor();
        jToggleButtonHandpadModeOn = new JToggleButtonColor();

        jPanelHandpadMode = new javax.swing.JPanel();
        buttonGroupFastSlow = new javax.swing.ButtonGroup();
        jComboBoxHandpadModes = new JComboBoxColor();
        jLabelHandpadTitle = new javax.swing.JLabel();
        jLabelHandpadFastSpeed = new javax.swing.JLabel();
        jLabelHandpadMode = new javax.swing.JLabel();
        jLabelHandpadSlowSpeed = new javax.swing.JLabel();
        jTextFieldHandpadFastSpeed = new javax.swing.JTextField();
        jTextFieldHandpadSlowSpeed = new javax.swing.JTextField();

        setLayout(new AbsoluteLayout());

        setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelHandpadTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelHandpadTitle.setText("Handpad");
        add(jLabelHandpadTitle, new AbsoluteConstraints(80, 10, -1, -1));

        jComboBoxHandpadModes.setToolTipText("select a handpad mode");
        jComboBoxHandpadModes.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxHandpadModesItemStateChanged(evt);
            }
        });

        add(jComboBoxHandpadModes, new AbsoluteConstraints(10, 30, 180, -1));

        jToggleButtonHandpadUp.setText("up");
        jToggleButtonHandpadUp.setToolTipText("move up (altitude/declination motor clockwise)");
        jToggleButtonHandpadUp.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonHandpadUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHandpadUpActionPerformed(evt);
            }
        });

        add(jToggleButtonHandpadUp, new AbsoluteConstraints(70, 110, 60, -1));

        jToggleButtonHandpadDown.setText("down");
        jToggleButtonHandpadDown.setToolTipText("move down (altitude/declination motor counterclockwise)");
        jToggleButtonHandpadDown.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonHandpadDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHandpadDownActionPerformed(evt);
            }
        });

        add(jToggleButtonHandpadDown, new AbsoluteConstraints(70, 170, 60, -1));

        jToggleButtonHandpadCCW.setText("ccw");
        jToggleButtonHandpadCCW.setToolTipText("move counterclockwise (azimuth/right ascension motor counterclockwise)");
        jToggleButtonHandpadCCW.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonHandpadCCW.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHandpadCCWActionPerformed(evt);
            }
        });

        add(jToggleButtonHandpadCCW, new AbsoluteConstraints(10, 140, 60, -1));

        jToggleButtonHandpadCW.setText("cw");
        jToggleButtonHandpadCW.setToolTipText("move clockwise (azimuth/right ascension motor clockwise)");
        jToggleButtonHandpadCW.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonHandpadCW.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHandpadCWActionPerformed(evt);
            }
        });

        add(jToggleButtonHandpadCW, new AbsoluteConstraints(130, 140, 60, -1));

        jRadioButtonHandpadFast.setText("fast");
        jRadioButtonHandpadFast.setToolTipText("set virtual handpad speed to fast");
        buttonGroupFastSlow.add(jRadioButtonHandpadFast);
        jRadioButtonHandpadFast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonHandpadFastActionPerformed(evt);
            }
        });

        add(jRadioButtonHandpadFast, new AbsoluteConstraints(40, 200, 60, -1));

        jRadioButtonHandpadSlow.setText("slow");
        jRadioButtonHandpadSlow.setToolTipText("set virtual handpad speed to slow");
        buttonGroupFastSlow.add(jRadioButtonHandpadSlow);
        jRadioButtonHandpadSlow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonHandpadSlowActionPerformed(evt);
            }
        });

        add(jRadioButtonHandpadSlow, new AbsoluteConstraints(110, 200, 60, -1));

        jToggleButtonHandpadStop.setText("stop");
        jToggleButtonHandpadStop.setToolTipText("stop all motors");
        jToggleButtonHandpadStop.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonHandpadStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHandpadStopActionPerformed(evt);
            }
        });

        add(jToggleButtonHandpadStop, new AbsoluteConstraints(70, 140, 60, -1));

        jLabelHandpadFastSpeed.setText("fast speed degrees/sec");
        add(jLabelHandpadFastSpeed, new AbsoluteConstraints(10, 230, -1, -1));

        jLabelHandpadSlowSpeed.setText("slow speed arcsec/sec");
        add(jLabelHandpadSlowSpeed, new AbsoluteConstraints(10, 250, -1, -1));

        jTextFieldHandpadFastSpeed.setToolTipText("fast speed in degrees per second");
        add(jTextFieldHandpadFastSpeed, new AbsoluteConstraints(150, 230, 40, -1));

        jTextFieldHandpadSlowSpeed.setToolTipText("slow speed in arcseconds per second");
        add(jTextFieldHandpadSlowSpeed, new AbsoluteConstraints(150, 250, 40, -1));

        jToggleButtonHandpadModeOn.setText("on");
        jToggleButtonHandpadModeOn.setToolTipText("activate selected handpad mode");
        jToggleButtonHandpadModeOn.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonHandpadModeOn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHandpadModeOnActionPerformed(evt);
            }
        });

        add(jToggleButtonHandpadModeOn, new AbsoluteConstraints(10, 60, 50, -1));

        jLabelHandpadMode.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelHandpadMode.setText("mode");
        add(jLabelHandpadMode, new AbsoluteConstraints(70, 60, 60, 30));

        jToggleButtonHandpadModeOff.setText("off");
        jToggleButtonHandpadModeOff.setToolTipText("turn off selected handpad mode");
        jToggleButtonHandpadModeOff.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonHandpadModeOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHandpadModeOffActionPerformed(evt);
            }
        });

        add(jToggleButtonHandpadModeOff, new AbsoluteConstraints(141, 60, 50, -1));
    }

    private void jToggleButtonHandpadStopActionPerformed(java.awt.event.ActionEvent evt) {
        if (t != null && jToggleButtonHandpadStop.isSelected())
            t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_stop.toString());
        motionButtonsOffMediator.off();
    }

    private void jComboBoxHandpadModesItemStateChanged(java.awt.event.ItemEvent evt) {
        // only fire once, and don't fire when jComboBox model loaded
        if (evt.getStateChange() == evt.DESELECTED)
            cfg.getInstance().handpadMode = (HANDPAD_MODE) jComboBoxHandpadModes.getSelectedItem();
    }

    private void jRadioButtonHandpadFastActionPerformed(java.awt.event.ActionEvent evt) {
        checkChangeSpeedsMidstream();
    }

    private void jRadioButtonHandpadSlowActionPerformed(java.awt.event.ActionEvent evt) {
        checkChangeSpeedsMidstream();
    }

    private void jToggleButtonHandpadCWActionPerformed(java.awt.event.ActionEvent evt) {
        // turn off counterpart
        jToggleButtonHandpadCCW.setSelected(false);
        if (t != null) {
            if (jToggleButtonHandpadCW.isSelected()) {
                stopButtonsOffMediator.off();
                sendSpeedCmd();

                if (jRadioButtonHandpadFast.isSelected())
                    t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_handpad_CW_fast.toString());
                else
                    t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_handpad_CW_slow.toString());
            }
            else
                t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_handpad_CW_stop.toString());
        }
    }

    private void jToggleButtonHandpadCCWActionPerformed(java.awt.event.ActionEvent evt) {
        // turn off counterpart
        jToggleButtonHandpadCW.setSelected(false);
        if (t != null) {
            if (jToggleButtonHandpadCCW.isSelected()) {
                stopButtonsOffMediator.off();
                sendSpeedCmd();

                if (jRadioButtonHandpadFast.isSelected())
                    t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_handpad_CCW_fast.toString());
                else
                    t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_handpad_CCW_slow.toString());
            }
            else
                t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_handpad_CCW_stop.toString());
        }
    }

    private void jToggleButtonHandpadDownActionPerformed(java.awt.event.ActionEvent evt) {
        // turn off counterpart
        jToggleButtonHandpadUp.setSelected(false);
        if (t != null) {
            if (jToggleButtonHandpadDown.isSelected()) {
                stopButtonsOffMediator.off();
                sendSpeedCmd();

                if (jRadioButtonHandpadFast.isSelected())
                    t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_handpad_down_fast.toString());
                else
                    t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_handpad_down_slow.toString());
            }
            else
                t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_handpad_down_stop.toString());
        }
    }

    private void jToggleButtonHandpadUpActionPerformed(java.awt.event.ActionEvent evt) {
        // turn off counterpart
        jToggleButtonHandpadDown.setSelected(false);
        if (t != null) {
            if (jToggleButtonHandpadUp.isSelected()) {
                stopButtonsOffMediator.off();
                sendSpeedCmd();

                if (jRadioButtonHandpadFast.isSelected())
                    t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_handpad_up_fast.toString());
                else
                    t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_handpad_up_slow.toString());
            }
            else
                t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_handpad_up_stop.toString());
        }
    }

    private void jToggleButtonHandpadModeOffActionPerformed(java.awt.event.ActionEvent evt) {
        if (t != null) {
            jToggleButtonHandpadModeOff.setSelected(false);
            t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_handpad_right_mode_key.toString());
        }
    }

    private void jToggleButtonHandpadModeOnActionPerformed(java.awt.event.ActionEvent evt) {
        if (t != null) {
            jToggleButtonHandpadModeOn.setSelected(false);
            t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_handpad_left_mode_key.toString());
        }
    }

    private void sendSpeedCmd() {
        String value;
        double fastSpeed;
        double slowSpeed;

        value = jTextFieldHandpadFastSpeed.getText();
        if (value.length() > 0)
            try {
                fastSpeed = Double.parseDouble(value);
                t.cmdCol.UICmd.newCmd("UI",
                CMD_SCOPE.cmd_scope_fast_speed.toString()
                + " "
                + value);
            }
            catch (NumberFormatException nfe) {
                console.errOut("invalid number in jPanelHandpad.checkSpeed(): " + value);
                jTextFieldHandpadFastSpeed.setText("");
            }

        value = jTextFieldHandpadSlowSpeed.getText();
        if (value.length() > 0)
            try {
                slowSpeed = Double.parseDouble(value);
                t.cmdCol.UICmd.newCmd("UI",
                CMD_SCOPE.cmd_scope_slow_speed.toString()
                + " "
                + value);
            }
            catch (NumberFormatException nfe) {
                console.errOut("invalid number in jPanelHandpad.checkSpeed(): " + value);
                jTextFieldHandpadSlowSpeed.setText("");
            }
    }

    // change speeds in midstream if movement underway
    void checkChangeSpeedsMidstream() {
        if (jToggleButtonHandpadCW.isSelected())
            jToggleButtonHandpadCWActionPerformed(null);
        if (jToggleButtonHandpadCCW.isSelected())
            jToggleButtonHandpadCCWActionPerformed(null);
        if (jToggleButtonHandpadUp.isSelected())
            jToggleButtonHandpadUpActionPerformed(null);
        if (jToggleButtonHandpadDown.isSelected())
            jToggleButtonHandpadDownActionPerformed(null);
    }

    void close() {
        cfg.getInstance().handpadMode = (HANDPAD_MODE) jComboBoxHandpadModes.getSelectedItem();
    }
}

