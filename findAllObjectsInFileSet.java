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
 * finds all objects in a set of files;
 * leave some properties public so that a monitor can display progress
 */
public class findAllObjectsInFileSet {
    private String object;
    private String dir;
    private fileFilter dataFileFilter;
    private File files;
    File[] fileList;
    int ix;
    int objectCount;
    private boolean objectFound;
    private boolean testing;
    dataFile df = new dataFile();

    findAllObjectsInFileSet(String object, String dir, boolean testing) {
        this.object = object;
        this.dir = dir;
        this.testing = testing;
    }

    boolean findAllObjects(listPosition listPos) {
        objectCount = 0;
        objectFound = false;
        dataFileFilter = new fileFilter(eString.DAT_EXT);
        files = new File(dir);
        if (files != null) {
            fileList = files.listFiles(dataFileFilter);
            if (fileList != null)
                for (ix = 0; ix < fileList.length; ix++)
                    if (!fileList[ix].isDirectory()) {
                        if (testing)
                            console.stdOut("loading file "
                            + fileList[ix]
                            + "...");
                        df.loadFromFile(fileList[ix].toString());
                        if (testing)
                            console.stdOutLn("   searching "
                            + df.count
                            + " lines...");
                        objectCount += df.count;
                        if (df.count > 0)
                            if (df.findAllObjects(object, listPos)) {
                                if (testing) {
                                    console.stdOutLn("*** found "
                                    + object
                                    + " in "
                                    + fileList[ix]);
                                    listPos.showDataFileFormat();
                                }
                                objectFound = true;
                            }
                        if (Thread.interrupted()) {
                            return objectFound;
                        }
                    }
        }
        return objectFound;
    }
}

