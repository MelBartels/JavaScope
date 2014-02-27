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
 * extend JFrameCmdsScrollable to add a timer to update JFrameCmdsScrollable's textarea
 */
public class JFrameCmdsScrollableActionListener extends JFrameCmdsScrollable implements ActionListener {
    private javax.swing.Timer timer;

    JFrameCmdsScrollableActionListener(String title) {
        super(title);
        timer = new javax.swing.Timer(cfg.getInstance().updateUImilliSec, this);
    }

    void startTimer() {
        timer.start();
    }

    void stopTimer() {
        timer.stop();
    }

    // meant to be overrided by child class
    public void actionPerformed(ActionEvent ae) { }
}

