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
 * sets a filter to use with files and directories
 */
public class fileFilter implements FilenameFilter {
    String filter;

    fileFilter(String filter) {
        this.filter = new String(filter);
    }

    public boolean accept(File dir, String name) {
        // case sensitive
        if (name.endsWith(filter))
            return true;
        return false;
    }
}

