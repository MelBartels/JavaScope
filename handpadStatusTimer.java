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

public class handpadStatusTimer extends JFrameHandpadStatus implements ActionListener {
    track t;
    boolean upDownButtonsReverse;
    javax.swing.Timer timer;

    handpadStatusTimer() {
        timer = new javax.swing.Timer(1000, this);
    }

    void registerTrackReference(track t) {
        this.t = t;
    }

    void startTimer() {
        setVisible(true);
        timer.start();
    }

    void exitForm(java.awt.event.WindowEvent evt) {
        setVisible(false);
        timer.stop();
    }

    public void actionPerformed(ActionEvent e) {
        if (t != null)
            if (cfg.getInstance().handpadPresent) {
                if (upDownButtonsReverse != t.HandpadDesigns.upDownButtonsReversed()) {
                    if (t.HandpadDesigns.upDownButtonsReversed()) {
                        jToggleButtonHandpadUp.setText("down");
                        jToggleButtonHandpadDown.setText("up");
                    }
                    else {
                        jToggleButtonHandpadUp.setText("up");
                        jToggleButtonHandpadDown.setText("down");
                    }
                    upDownButtonsReverse = t.HandpadDesigns.upDownButtonsReversed();
                }
                jRadioButtonHandpadSlow.setSelected(t.slowSpeedSwitch>0);
                jRadioButtonHandpadFast.setSelected(t.slowSpeedSwitch==0);
                jToggleButtonHandpadCCW.setSelected((t.buttons & handpadDesignBase.CCW_KEY)==handpadDesignBase.CCW_KEY);
                jToggleButtonHandpadCW.setSelected((t.buttons & handpadDesignBase.CW_KEY)==handpadDesignBase.CW_KEY);
                jToggleButtonHandpadDown.setSelected((t.buttons & handpadDesignBase.DOWN_KEY)==handpadDesignBase.DOWN_KEY);
                jToggleButtonHandpadModeOff.setSelected((t.buttons & handpadDesignBase.RIGHT_KEY)==handpadDesignBase.RIGHT_KEY);
                jToggleButtonHandpadModeOn.setSelected((t.buttons & handpadDesignBase.LEFT_KEY)==handpadDesignBase.LEFT_KEY);
                jToggleButtonHandpadUp.setSelected((t.buttons & handpadDesignBase.UP_KEY)==handpadDesignBase.UP_KEY);

                jLabelHandpadDesign.setText(cfg.getInstance().handpadDesign.toString());
                jLabelHandpadMode.setText(cfg.getInstance().handpadMode.toString());

                if (cfg.getInstance().spa.cmdDevice == CMD_DEVICE.SiTechHandpad
                && cfg.getInstance().spz.cmdDevice == CMD_DEVICE.SiTechHandpad)
                    jLabelHandpadStatus.setText("SiTech operating");
                else
                    jLabelHandpadStatus.setText("");
            }
            else
                jLabelHandpadStatus.setText("handpad not present");
        else
            jLabelHandpadStatus.setText("no track module");
    }
}

