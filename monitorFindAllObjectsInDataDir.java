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
 *sets up a window with a label to monitor progress of directory scan for datafiles
 */
public class monitorFindAllObjectsInDataDir {
    private JFrame frame;
    private JPanel panel;
    private monitorFindAllObjectsInDataDirLabel label;

    monitorFindAllObjectsInDataDir(findAllObjectsInFileSet faofs) {
        frame = new JFrame("monitor directory scan of datafiles");
        frame.setSize(350, 75);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        panel = (JPanel) frame.getContentPane();
        label = new monitorFindAllObjectsInDataDirLabel(faofs);
        panel.add(label, BorderLayout.CENTER);
        screenPlacement.getInstance().center(frame);
        frame.setVisible(true);
    }

    void startTimer() {
        label.startTimer();
    }

    void shutDown() {
        frame.setVisible(false);
        label.stopTimer();
    }
}

