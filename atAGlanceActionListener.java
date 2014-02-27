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
 * timer to update fields in the 'at a glance' panel in the main form
 */
public class atAGlanceActionListener implements ActionListener {
    private javax.swing.JLabel jLabelAtAGlanceCurrentEquatCoord;
    private javax.swing.JLabel jLabelAtAGlanceTargetEquatCoord;
    private javax.swing.JLabel jLabelAtAGlanceCurrentAltazCoord;
    private javax.swing.JLabel jLabelAtAGlanceTargetAltazCoord;

    private javax.swing.Timer timer;
    track t;

    atAGlanceActionListener(
    javax.swing.JLabel jLabelAtAGlanceCurrentEquatCoord,
    javax.swing.JLabel jLabelAtAGlanceTargetEquatCoord,
    javax.swing.JLabel jLabelAtAGlanceCurrentAltazCoord,
    javax.swing.JLabel jLabelAtAGlanceTargetAltazCoord) {

        this.jLabelAtAGlanceCurrentEquatCoord = jLabelAtAGlanceCurrentEquatCoord;
        this.jLabelAtAGlanceTargetEquatCoord = jLabelAtAGlanceTargetEquatCoord;
        this.jLabelAtAGlanceCurrentAltazCoord = jLabelAtAGlanceCurrentAltazCoord;
        this.jLabelAtAGlanceTargetAltazCoord = jLabelAtAGlanceTargetAltazCoord;

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
        if (cfg.getInstance().initialized())
            jLabelAtAGlanceCurrentEquatCoord.setText("current " + cfg.getInstance().current.buildStringDataFileFormat());
        else
            jLabelAtAGlanceCurrentEquatCoord.setText("not initialized");

        if (t == null)
            jLabelAtAGlanceTargetEquatCoord.setText("waiting for track module startup...");
        else
            jLabelAtAGlanceTargetEquatCoord.setText("target   " + t.target.buildStringDataFileFormat());

        jLabelAtAGlanceCurrentAltazCoord.setText("Alt: "
        + eString.doubleToStringNoGrouping(cfg.getInstance().current.alt.rad*units.RAD_TO_DEG, 3, 2)
        + "   Az: "
        + eString.doubleToStringNoGrouping(cfg.getInstance().current.az.rad*units.RAD_TO_DEG, 3, 2));

        if (t != null)
            jLabelAtAGlanceTargetAltazCoord.setText("Alt: "
            + eString.doubleToStringNoGrouping(t.target.alt.rad*units.RAD_TO_DEG, 3, 2)
            + "   Az: "
            + eString.doubleToStringNoGrouping(t.target.az.rad*units.RAD_TO_DEG, 3, 2));
        else
            jLabelAtAGlanceTargetAltazCoord.setText("");
    }
}

