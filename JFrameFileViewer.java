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
 * this class puts jPanelFileViewer into its own frame
 */
public class JFrameFileViewer {
    protected JFrame frame;
    private JPanel panel;
    private jPanelFileViewer jPanelFileViewer;

    JFrameFileViewer(String title, String filename) {
        BufferedReader input;
        String s;

        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        panel = (JPanel) frame.getContentPane();
        panel.setLayout(new AbsoluteLayout());
        jPanelFileViewer = new jPanelFileViewer();
        panel.add(jPanelFileViewer, new AbsoluteConstraints(0, 0, 500, 500));
        frame.pack();
        screenPlacement.getInstance().center(frame);
        jPanelFileViewer.jTextAreaViewer.setText("");
        jPanelFileViewer.jLabelFilename.setText(filename);

        try {
            input = new BufferedReader(new FileReader(filename));
            s = input.readLine();
            while (s != null) {
                jPanelFileViewer.jTextAreaViewer.append(s);
                s = input.readLine();
            }
            input.close();
            frame.setVisible(true);
        }
        catch (IOException ioe) {
            jPanelFileViewer.jTextAreaViewer.append("could not open " + filename);
            console.errOut("could not open " + filename);
        }
    }
}

