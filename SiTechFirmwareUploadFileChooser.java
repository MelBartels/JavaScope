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
 * chooses a SiTech firmware upload file, placing results in file, fileSelected;
 */
public class SiTechFirmwareUploadFileChooser {
    private JFileChooser fc;
    File file;
    boolean fileSelected;

    SiTechFirmwareUploadFileChooser(JFrame jf, String title) {
        fc = new JFileChooser();

        fc.setDialogTitle(title);
        fc.addChoosableFileFilter(new SiTechFirmwareUpdateFileFilters());
        fc.setCurrentDirectory(new File(cfg.getInstance().SiTechFirmwareFileLocation));
        if (fc.showOpenDialog(jf) == JFileChooser.APPROVE_OPTION) {
            if (fc.getSelectedFile() != null) {
                file = fc.getSelectedFile().getAbsoluteFile();
                cfg.getInstance().SiTechFirmwareFileLocation = file.getParent();
                if (!cfg.getInstance().SiTechFirmwareFileLocation.endsWith(file.separator))
                    cfg.getInstance().SiTechFirmwareFileLocation += file.separator;
                console.stdOutLn("SiTechFirmwareUploadFileChooser set cfg.getInstance().SiTechFirmwareFileLocation to " + cfg.getInstance().SiTechFirmwareFileLocation);
                fileSelected = true;
                console.stdOutLn("SiTechFirmwareUploadFileChooser file: " + file.getAbsoluteFile().toString());
            }
        }
        else
            fileSelected = false;
    }
}

