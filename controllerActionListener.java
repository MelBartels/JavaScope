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
 * timer to update fields in the 'controller' panel in the main form
 */
public class controllerActionListener implements ActionListener {
    private JComboBoxColor jComboBoxEncodersControllerType;

    private javax.swing.Timer timer;
    track t;

    controllerActionListener(JComboBoxColor jComboBoxEncodersControllerType) {
        this.jComboBoxEncodersControllerType = jComboBoxEncodersControllerType;

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
        if (t != null)
            if (t.ef != null
            && t.ef.E != null
            && t.ef.E.encoderType() != (ENCODER_TYPE) jComboBoxEncodersControllerType.getSelectedItem()
            && t.ef.E.portOpened())
                jComboBoxEncodersControllerType.setSelectedItem(t.ef.E.encoderType());
    }
}

