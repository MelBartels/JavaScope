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
 *this class puts jPanelGetCoordinate into its own frame
 */
public class JFrameGetCoordinate {
    private track t;
    private JFrame frame;
    private JPanel panel;
    private jPanelGetCoordinate jPanelGetCoordinate;
    private closeJFrameMediator closeJFrameMediator;

    JFrameGetCoordinate() {
        frame = new JFrame("Get Coordinate");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        panel = (JPanel) frame.getContentPane();
        jPanelGetCoordinate = new jPanelGetCoordinate();
        panel.add(jPanelGetCoordinate, BorderLayout.CENTER);
        frame.pack();
        screenPlacement.getInstance().center(frame);
        frame.setVisible(true);
        closeJFrameMediator = new closeJFrameMediator(frame);
        jPanelGetCoordinate.registerCloseJFrameMediator(closeJFrameMediator);
    }

    void registerTrackReference(track t) {
        this.t = t;
        jPanelGetCoordinate.registerTrackReference(t);
    }

    void setVisible(boolean value) {
        frame.setVisible(value);
    }

    void updateGetCoordinateType(PANEL_GET_COORDINATE_TYPE getCoordinateType) {
        jPanelGetCoordinate.updateGetCoordinateType(getCoordinateType);
    }

    position selectedPosition() {
        return jPanelGetCoordinate.selectedPosition();
    }
}

