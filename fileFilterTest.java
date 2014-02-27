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
 * test of fileFilter class
 */
public class fileFilterTest {
    void test() {
        String filter;
        String dir;
        fileFilter testFilter;
        File files;
        File[] fileList;
        int ix;

        System.out.println("fileFilter test:");
        System.out.print("Please enter an extension to search on (ie, DAT): ");
        console.getString();
        filter = console.s;
        System.out.print("Please enter the directory to search in (ie, c:/temp/): ");
        console.getString();
        dir = console.s;

        System.out.println("list of files are:");
        // case sensitive
        testFilter = new fileFilter(filter);
        files = new File(dir);
        if (files != null) {
            fileList = files.listFiles(testFilter);
            if (fileList != null)
                for (ix = 0; ix < fileList.length; ix++)
                    // possible to be neither directory nor file
                    System.out.println("   "
                    + (fileList[ix].isDirectory()?"(*DIR*)":"")
                    + (fileList[ix].isFile()?"(file)":"")
                    + fileList[ix]
                    + " "
                    + fileList[ix].lastModified()
                    + " "
                    + new Date(fileList[ix].lastModified()));
        }
        System.out.println("end of file list");
    }
}

