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
 * generic JPanel for viewing text or similar files
 */
public class jPanelFileViewer extends JPanelColor {
    private javax.swing.JPanel jPanelFileViewer;
    private javax.swing.JScrollPane jScrollPaneViewer;
    protected javax.swing.JLabel jLabelFilename;
    protected javax.swing.JTextArea jTextAreaViewer;

    jPanelFileViewer() {
        mediumColor();
        initComponents();
    }

    private void initComponents() {
        jPanelFileViewer = new javax.swing.JPanel();
        jLabelFilename = new javax.swing.JLabel();
        jScrollPaneViewer = new javax.swing.JScrollPane();
        jTextAreaViewer = new javax.swing.JTextArea();

        setLayout(new AbsoluteLayout());

        jLabelFilename.setText("(filename)");
        jLabelFilename.setToolTipText("");
        add(jLabelFilename, new AbsoluteConstraints(1, 5, 360, -1));

        jTextAreaViewer.setEditable(false);
        jTextAreaViewer.setFont(new java.awt.Font("Dialog", 0, 10));
        jTextAreaViewer.setLineWrap(true);
        jTextAreaViewer.setWrapStyleWord(true);
        jScrollPaneViewer.setViewportView(jTextAreaViewer);

        add(jScrollPaneViewer, new AbsoluteConstraints(0, 30, 500, 470));
    }
}

