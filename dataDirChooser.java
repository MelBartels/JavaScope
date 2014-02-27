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
 *selects a directory, placing results in dir, dirSelected, and cfg.getInstance().datFileLocation
 */
public class dataDirChooser {
    private JFileChooser fc;
    File dir;
    boolean dirSelected;

    dataDirChooser(JFrame jf) {
        fc = new JFileChooser();

        fc.setDialogTitle("select a coordinate files directory");
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setCurrentDirectory(new File(cfg.getInstance().datFileLocation));
        // these look and feel do not automatically fill the file textarea: this is important as otherwise
        // it takes several awkward steps by the user to place a directory name in the file textarea
        if (cfg.getInstance().UILookAndFeel.equals("com.sun.java.swing.plaf.motif.MotifLookAndFeel")
        || cfg.getInstance().UILookAndFeel.equals("com.sun.java.swing.plaf.gtk.GTKLookAndFeel"))
            fc.setSelectedFile(new File(cfg.getInstance().datFileLocation));
        if (fc.showOpenDialog(jf) == JFileChooser.APPROVE_OPTION) {
            if (fc.getSelectedFile() != null) {
                // getAbsoluteFile() includes the last subdirectory
                dir = fc.getSelectedFile().getAbsoluteFile();
                // some look and feels lead user to end with desired subdirectory included
                // in directory path, and, be the file name also, consequently doubling up:
                // if so, then remove doubled up name and try again;
                if (!dir.exists()) {
                    console.errOut("directory " + dir + " does not exist, trying parent");
                    dir = new File(dir.getParent());
                }
                cfg.getInstance().datFileLocation = dir.getAbsolutePath();
                if (!cfg.getInstance().datFileLocation.endsWith(dir.separator))
                    cfg.getInstance().datFileLocation += dir.separator;
                console.stdOutLn("dataDirChooser set cfg.getInstance().datFileLocation to " + cfg.getInstance().datFileLocation);
                dirSelected = true;
            }
        }
        else
            dirSelected = false;
    }
}

