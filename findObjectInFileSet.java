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
 * finds an object in a set of files
 */
public class findObjectInFileSet {
    String object;
    String dir;
    fileFilter dataFileFilter;
    File files;
    File[] fileList;
    int ix;
    position p = new position("findObjectInFileSet");
    dataFile df = new dataFile();

    /**
     * returns true at first match
     */
    boolean findObject() {
        console.stdOut("Please enter object name (ie, M42): ");
        console.getString();
        object = console.s;
        console.stdOut("Please enter the directory to search the data files in (ie, c:/temp/): ");
        console.getString();
        dir = console.s;
        dataFileFilter = new fileFilter(eString.DAT_EXT);
        files = new File(dir);
        if (files != null) {
            fileList = files.listFiles(dataFileFilter);
            if (fileList != null)
                for (ix = 0; ix < fileList.length; ix++)
                    if (!fileList[ix].isDirectory()) {
                        console.stdOut("loading file "
                        + fileList[ix]
                        + "...");
                        df.loadFromFile(fileList[ix].toString());
                        console.stdOutLn("   searching "
                        + df.count
                        + " lines...");
                        if (df.count > 0)
                            if (df.findObject(object, p)) {
                                console.stdOutLn("*** found "
                                + object
                                + " in "
                                + fileList[ix]);
                                p.showDataFileFormat();
                                return true;
                            }
                    }
        }
        return false;
    }
}

