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
 * chooses a coordinate data file, placing results in file, fileSelected, and listPos;
 */
public class dataFileChooserLoadListPos {
    private BufferedReader input;
    private position p;
    private JFileChooser fc;
    File file;
    boolean fileSelected;
    listPosition listPos;

    dataFileChooserLoadListPos(JFrame jf) {
        p = new position("dataFileChooserLoadListPos");
        fc = new JFileChooser();
        listPos = new listPosition();

        fc.setDialogTitle("open a coordinate data file");
        fc.addChoosableFileFilter(new DataFileFilters());
        fc.setCurrentDirectory(new File(cfg.getInstance().datFileLocation));
        if (fc.showOpenDialog(jf) == JFileChooser.APPROVE_OPTION) {
            if (fc.getSelectedFile() != null) {
                file = fc.getSelectedFile().getAbsoluteFile();
                cfg.getInstance().datFileLocation = file.getParent();
                if (!cfg.getInstance().datFileLocation.endsWith(file.separator))
                    cfg.getInstance().datFileLocation += file.separator;
                console.stdOutLn("dataFileChooserLoadListPos set cfg.getInstance().datFileLocation to " + cfg.getInstance().datFileLocation);
                fileSelected = true;
                try {
                    console.stdOutLn("dataFileChooserLoadListPos file: " + file.getAbsoluteFile().toString());
                    input = new BufferedReader(new FileReader(file));
                    while (console.fReadLineEquatCoord(input, p)) {
                        if (cfg.getInstance().precessionNutationAberration)
                            p.applyPrecessionCorrectionFromEpochYear(cfg.getInstance().dataFileCoordYear);
                        listPos.add(p);
                    }
                    input.close();
                    //listPos.display();
                }
                catch (IOException ioe) {
                    console.errOut("dataFileChooserLoadListPos could not open file: " + file.getAbsoluteFile().toString());
                }
            }
        }
        else
            fileSelected = false;
    }
}

