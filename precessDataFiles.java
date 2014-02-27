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
 * takes a directory of data files, precesses coordinates, and writes out converted data files to another directory
  */
public class precessDataFiles {

    precessDataFiles() {
        int ix, ixB;
        File files;
        File[] fileList;
        dataFile df = new dataFile();
        String filename;
        String targetFilename;
        PrintStream output;

        String origDir;
        String targetDir;
        double origEpochYr;
        double targetEpochYr;

        System.out.println("precess a directory of data files");
        System.out.print("enter directory where data files are located: ");
        console.getString();
        origDir = console.s;
        System.out.print("enter target directory where precessed data files will be placed: ");
        console.getString();
        targetDir = console.s;
        System.out.print("enter coordinate year of existing data files: ");
        console.getDouble();
        origEpochYr = console.d;
        System.out.print("enter year to precess coordinates to: ");
        console.getDouble();
        targetEpochYr = console.d;

        cfg.getInstance().precessionNutationAberration = true;
        files = new File(origDir);

        if (files != null) {
            fileList = files.listFiles();
            if (fileList != null)
                for (ix = 0; ix < fileList.length; ix++)
                    if (!fileList[ix].isDirectory() 
                    && fileList[ix].getName().toLowerCase().endsWith(eString.DAT_EXT.toLowerCase())) {
                        filename = fileList[ix].toString();
                        df.loadFromFile(filename);
                        System.out.println("object count: " + df.count);
                        targetFilename = targetDir + fileList[ix].getName();
                        try {
                            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(targetFilename)));
                            for (ixB = 0; ixB < df.lp.size(); ixB++) {
                                position p = df.lp.get(ixB);
                                output.println(" "
                                + p.ra.getStringHMS(' ')
                                + "   "
                                + p.dec.getStringDMS(' ')
                                + "   "
                                + p.objName);
                            }
                            output.close();
                        }
                        catch (IOException ioe) {
                            console.errOut("could not open " + filename);
                        }
                    }
        }
        else
            System.out.println("no files found");
        System.out.println("finished precessing data files");
    }
}

