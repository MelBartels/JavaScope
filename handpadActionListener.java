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
 * updates handpad fields as needed
 */
public class handpadActionListener implements ActionListener {
    private JComboBoxColor jComboBoxHandpadModes;

    private javax.swing.Timer timer;
    track t;

    handpadActionListener(JComboBoxColor jComboBoxHandpadModes) {
        this.jComboBoxHandpadModes = jComboBoxHandpadModes;
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
        if ((HANDPAD_MODE) jComboBoxHandpadModes.getSelectedItem() != cfg.getInstance().handpadMode)
            jComboBoxHandpadModes.setSelectedItem(cfg.getInstance().handpadMode);
    }
}

