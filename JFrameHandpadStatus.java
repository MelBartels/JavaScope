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
 * handpad status frame
 */
public class JFrameHandpadStatus extends javax.swing.JFrame {

    // change from private to protected
    // Variables declaration - do not modify
    protected javax.swing.JLabel jLabelHandpadDesign;
    protected javax.swing.JLabel jLabelHandpadMode;
    protected javax.swing.JLabel jLabelHandpadStatus;
    protected javax.swing.JPanel jPanelHandpadStatus;
    protected javax.swing.JRadioButton jRadioButtonHandpadFast;
    protected javax.swing.JRadioButton jRadioButtonHandpadSlow;
    protected javax.swing.JToggleButton jToggleButtonHandpadCCW;
    protected javax.swing.JToggleButton jToggleButtonHandpadCW;
    protected javax.swing.JToggleButton jToggleButtonHandpadDown;
    protected javax.swing.JToggleButton jToggleButtonHandpadModeOff;
    protected javax.swing.JToggleButton jToggleButtonHandpadModeOn;
    protected javax.swing.JToggleButton jToggleButtonHandpadUp;
    // End of variables declaration

    public JFrameHandpadStatus() {
        super("handpad status");
        initComponents();
        screenPlacement.getInstance().center(this);
    }

    private void initComponents() {
        jPanelHandpadStatus = new javax.swing.JPanel();
        jToggleButtonHandpadUp = new javax.swing.JToggleButton();
        jToggleButtonHandpadDown = new javax.swing.JToggleButton();
        jToggleButtonHandpadCCW = new javax.swing.JToggleButton();
        jToggleButtonHandpadCW = new javax.swing.JToggleButton();
        jRadioButtonHandpadFast = new javax.swing.JRadioButton();
        jRadioButtonHandpadSlow = new javax.swing.JRadioButton();
        jToggleButtonHandpadModeOn = new javax.swing.JToggleButton();
        jToggleButtonHandpadModeOff = new javax.swing.JToggleButton();
        jLabelHandpadDesign = new javax.swing.JLabel();
        jLabelHandpadMode = new javax.swing.JLabel();
        jLabelHandpadStatus = new javax.swing.JLabel();

        getContentPane().setLayout(new AbsoluteLayout());

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jPanelHandpadStatus.setLayout(new AbsoluteLayout());

        jPanelHandpadStatus.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToggleButtonHandpadUp.setText("up");
        jToggleButtonHandpadUp.setToolTipText("");
        jToggleButtonHandpadUp.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jPanelHandpadStatus.add(jToggleButtonHandpadUp, new AbsoluteConstraints(80, 60, 40, -1));

        jToggleButtonHandpadDown.setText("down");
        jToggleButtonHandpadDown.setToolTipText("");
        jToggleButtonHandpadDown.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jPanelHandpadStatus.add(jToggleButtonHandpadDown, new AbsoluteConstraints(80, 150, 40, -1));

        jToggleButtonHandpadCCW.setText("ccw");
        jToggleButtonHandpadCCW.setToolTipText("");
        jToggleButtonHandpadCCW.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jPanelHandpadStatus.add(jToggleButtonHandpadCCW, new AbsoluteConstraints(20, 100, 40, -1));

        jToggleButtonHandpadCW.setText("cw");
        jToggleButtonHandpadCW.setToolTipText("");
        jToggleButtonHandpadCW.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jPanelHandpadStatus.add(jToggleButtonHandpadCW, new AbsoluteConstraints(140, 100, 40, -1));

        jRadioButtonHandpadFast.setText("fast");
        jRadioButtonHandpadFast.setToolTipText("");
        jPanelHandpadStatus.add(jRadioButtonHandpadFast, new AbsoluteConstraints(70, 90, 60, -1));

        jRadioButtonHandpadSlow.setText("slow");
        jRadioButtonHandpadSlow.setToolTipText("");
        jPanelHandpadStatus.add(jRadioButtonHandpadSlow, new AbsoluteConstraints(70, 110, 60, -1));

        jToggleButtonHandpadModeOn.setText("on");
        jToggleButtonHandpadModeOn.setToolTipText("");
        jToggleButtonHandpadModeOn.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jPanelHandpadStatus.add(jToggleButtonHandpadModeOn, new AbsoluteConstraints(40, 20, 40, -1));

        jToggleButtonHandpadModeOff.setText("off");
        jToggleButtonHandpadModeOff.setToolTipText("");
        jToggleButtonHandpadModeOff.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jPanelHandpadStatus.add(jToggleButtonHandpadModeOff, new AbsoluteConstraints(120, 20, 40, -1));

        jLabelHandpadDesign.setText("design");
        jPanelHandpadStatus.add(jLabelHandpadDesign, new AbsoluteConstraints(20, 190, 160, -1));

        jLabelHandpadMode.setText("mode");
        jPanelHandpadStatus.add(jLabelHandpadMode, new AbsoluteConstraints(20, 210, 160, -1));

        jLabelHandpadStatus.setText("status");
        jPanelHandpadStatus.add(jLabelHandpadStatus, new AbsoluteConstraints(20, 230, 160, -1));

        getContentPane().add(jPanelHandpadStatus, new AbsoluteConstraints(0, 0, 200, 260));

        pack();
    }

    /** Exit the Application */
    // change from private to public so that it can be overridden
    void exitForm(java.awt.event.WindowEvent evt) {
        setVisible(false);
    }
}

