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
 * timer to update fields in the initialization panel in the main form
 */
public class initializationFieldsActionListener implements ActionListener {
    private javax.swing.JTextArea jTextAreaInitializationInstructions;
    private javax.swing.JTextField jTextFieldInitializationInit1;
    private javax.swing.JTextField jTextFieldInitializationInit2;
    private javax.swing.JTextField jTextFieldInitializationInit3;
    private javax.swing.JTextField jTextFieldInitializationAltOffset;

    private boolean lastInit3State;
    private int instructionSetting;
    private boolean lastCfgInitialized;
    autoInit autoInit;

    private javax.swing.Timer timer;
    track t;

    initializationFieldsActionListener(
    javax.swing.JTextArea jTextAreaInitializationInstructions,
    javax.swing.JTextField jTextFieldInitializationInit1,
    javax.swing.JTextField jTextFieldInitializationInit2,
    javax.swing.JTextField jTextFieldInitializationInit3,
    javax.swing.JTextField jTextFieldInitializationAltOffset) {

        this.jTextAreaInitializationInstructions = jTextAreaInitializationInstructions;
        this.jTextFieldInitializationInit1 = jTextFieldInitializationInit1;
        this.jTextFieldInitializationInit2 = jTextFieldInitializationInit2;
        this.jTextFieldInitializationInit3 = jTextFieldInitializationInit3;
        this.jTextFieldInitializationAltOffset = jTextFieldInitializationAltOffset;

        lastCfgInitialized = !cfg.getInstance().initialized();

        instructionSetting = 0;
        autoInit = new autoInit();

        timer = new javax.swing.Timer(cfg.getInstance().updateUImilliSec, this);
    }

    void registerTrackReference(track t) {
        this.t = t;
    }

    void startTimer() {
        timer.start();
    }

    void stopTimer() {
        timer.stop();
    }

    public void actionPerformed(ActionEvent ae) {
        if ( t != null) {
            if (!cfg.getInstance().initialized() && lastCfgInitialized)
                autoInits();
            lastCfgInitialized = cfg.getInstance().initialized();

            update();
        }
    }

    // select 2 brightest stars and put handpad into auto init mode
    private void autoInits() {
        autoInit.getInits();

        // set limit at 15 degrees elevation above horizon
        if (autoInit.initsAvail(15.*units.DEG_TO_RAD)) {

            cfg.getInstance().handpadMode = HANDPAD_MODE.handpadModeAutoInit12;

            t.cmdCol.UICmd.newCmd("UI",
            CMD_SCOPE.cmd_scope_autoInit1
            + " "
            + autoInit.p1().ra.getStringHMS(eString.SPACE)
            + " "
            + autoInit.p1().dec.getStringDMS(eString.SPACE)
            + " 0 0 "
            + autoInit.p1().objName);

            final position p2 = autoInit.p2();
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    do {
                        common.threadSleep(1000);
                    }while (t.cmdCol.UICmd.esc == null || t.cmdCol.UICmd.esc.cl == null || t.cmdCol.UICmd.esc.cl.cmdIsProgressing);

                    t.cmdCol.UICmd.newCmd("UI",
                    CMD_SCOPE.cmd_scope_autoInit2
                    + " "
                    + p2.ra.getStringHMS(eString.SPACE)
                    + " "
                    + p2.dec.getStringDMS(eString.SPACE)
                    + " 0 0 "
                    + p2.objName);
                }
            });
        }
    }

    private void update() {
        if (cfg.getInstance().one.init)
            jTextFieldInitializationInit1.setText("(initialized)");
        else
            jTextFieldInitializationInit1.setText(t.autoInit1.buildStringDataFileFormat());

        if (cfg.getInstance().two.init)
            jTextFieldInitializationInit2.setText("(initialized)");
        else
            jTextFieldInitializationInit2.setText(t.autoInit2.buildStringDataFileFormat());

        if (cfg.getInstance().three.init)
            jTextFieldInitializationInit3.setText("(initialized)");
        else
            // only blank the display if three.init was previously true
            if (lastInit3State)
                jTextFieldInitializationInit3.setText("");
        lastInit3State = cfg.getInstance().three.init;

        if (cfg.getInstance().initialized() & instructionSetting != 2) {
            jTextAreaInitializationInstructions.setText(eString.INIT_INSTRUCTIONS_INITIALIZED);
            instructionSetting = 2;
        }
        else if (!cfg.getInstance().initialized() & instructionSetting != 1) {
            jTextAreaInitializationInstructions.setText(eString.INIT_INSTRUCTIONS_AUTO);
            instructionSetting = 1;
        }

        if (cfg.getInstance().initialized() && t != null)
            jTextFieldInitializationAltOffset.setText(eString.doubleToString(t.c.altOffset*units.RAD_TO_DEG, 2, 2));
        else
            jTextFieldInitializationAltOffset.setText("n/a");
    }
}

