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
 * timer to update fields in the 'set coordinates' panel in the main form
 */
public class mainEncodersActionListener implements ActionListener {
    private javax.swing.JLabel jLabelCheckDirectionAltDecEncoder;
    private javax.swing.JLabel jLabelCheckDirectionAzRaEncoder;
    private javax.swing.JLabel jLabelCheckDirectionFieldREncoder;
    private javax.swing.JLabel jLabelCheckDirectionFocusEncoder;
    private javax.swing.JLabel jLabelStartupScopePosition;
    private javax.swing.JLabel jLabelStartupEncodersPosition;

    private javax.swing.Timer timer;
    track t;

    mainEncodersActionListener(
    javax.swing.JLabel jLabelCheckDirectionAltDecEncoder,
    javax.swing.JLabel jLabelCheckDirectionAzRaEncoder,
    javax.swing.JLabel jLabelCheckDirectionFieldREncoder,
    javax.swing.JLabel jLabelCheckDirectionFocusEncoder,
    javax.swing.JLabel jLabelStartupScopePosition,
    javax.swing.JLabel jLabelStartupEncodersPosition) {

        this.jLabelCheckDirectionAltDecEncoder = jLabelCheckDirectionAltDecEncoder;
        this.jLabelCheckDirectionAzRaEncoder = jLabelCheckDirectionAzRaEncoder;
        this.jLabelCheckDirectionFieldREncoder = jLabelCheckDirectionFieldREncoder;
        this.jLabelCheckDirectionFocusEncoder = jLabelCheckDirectionFocusEncoder;
        this.jLabelStartupScopePosition = jLabelStartupScopePosition;
        this.jLabelStartupEncodersPosition = jLabelStartupEncodersPosition;

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
        jLabelStartupScopePosition.setText("Alt: "
        + eString.doubleToStringNoGrouping(cfg.getInstance().current.alt.rad*units.RAD_TO_DEG, 3, 2)
        + " Az: "
        + eString.doubleToStringNoGrouping(cfg.getInstance().current.az.rad*units.RAD_TO_DEG, 3, 2)
        + " deg");

        if (t != null
        && t.ef != null
        && t.ef.E != null
        && t.ef.E.portOpened()) {
            jLabelStartupEncodersPosition.setText(t.ef.E.buildPositionsString());

            jLabelCheckDirectionAltDecEncoder.setText(eString.intToString(t.ef.E.getAltDecCount(), 9));
            jLabelCheckDirectionAzRaEncoder.setText(eString.intToString(t.ef.E.getAzRaCount(), 9));
            jLabelCheckDirectionFieldREncoder.setText("n/a");
            jLabelCheckDirectionFocusEncoder.setText("n/a");
        }
        else {
            jLabelStartupEncodersPosition.setText("encoder readings not available");

            jLabelCheckDirectionAltDecEncoder.setText("n/a");
            jLabelCheckDirectionAzRaEncoder.setText("n/a");
            jLabelCheckDirectionFieldREncoder.setText("n/a");
            jLabelCheckDirectionFocusEncoder.setText("n/a");
        }
    }
}

