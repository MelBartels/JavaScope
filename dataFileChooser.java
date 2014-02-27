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
 * chooses a data file, placing results in file, fileSelected;
 */
public class dataFileChooser {
    private JFileChooser fc;
    File file;
    boolean fileSelected;

    dataFileChooser(JFrame jf, String title) {
        fc = new JFileChooser();

        fc.setDialogTitle(title);
        fc.addChoosableFileFilter(new DataFileFilters());
        fc.setCurrentDirectory(new File(cfg.getInstance().datFileLocation));
        if (fc.showOpenDialog(jf) == JFileChooser.APPROVE_OPTION) {
            if (fc.getSelectedFile() != null) {
                file = fc.getSelectedFile().getAbsoluteFile();
                cfg.getInstance().datFileLocation = file.getParent();
                if (!cfg.getInstance().datFileLocation.endsWith(file.separator))
                    cfg.getInstance().datFileLocation += file.separator;
                console.stdOutLn("dataFileChooser set cfg.getInstance().datFileLocation to " + cfg.getInstance().datFileLocation);
                fileSelected = true;
            }
        }
        else
            fileSelected = false;
    }
}

