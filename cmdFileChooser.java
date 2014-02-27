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
 * chooses a cmd file, placing results in file, fileSelected, and listPos;
 */
public class cmdFileChooser {
    private JFileChooser fc;
    File file;
    boolean fileSelected;

    cmdFileChooser(JFrame jf) {
        fc = new JFileChooser();

        fc.setDialogTitle("select a command file");
        fc.addChoosableFileFilter(new CmdFileFilters());
        fc.setCurrentDirectory(new File(cfg.getInstance().cmdFileLocation));
        if (fc.showOpenDialog(jf) == JFileChooser.APPROVE_OPTION) {
            if (fc.getSelectedFile() != null) {
                file = fc.getSelectedFile().getAbsoluteFile();
                cfg.getInstance().cmdFileLocation = file.getParent();
                if (!cfg.getInstance().cmdFileLocation.endsWith(file.separator))
                    cfg.getInstance().cmdFileLocation += file.separator;
                console.stdOutLn("cmdFileChooser set cfg.getInstance().cmdFileLocation to " + cfg.getInstance().cmdFileLocation);
                fileSelected = true;
                console.stdOutLn("cmdFileChooser file: " + file.getAbsoluteFile().toString());
            }
        }
        else
            fileSelected = false;
    }
}

