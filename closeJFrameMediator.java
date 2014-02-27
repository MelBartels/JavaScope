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
 *use to communite frame closure from an subclassed object
 */
public class closeJFrameMediator {
    private JFrame frame;
    private javax.swing.JLabel label;

    closeJFrameMediator(JFrame frame) {
        this.frame = frame;
    }

    void close() {
        frame.setVisible(false);
    }
}

