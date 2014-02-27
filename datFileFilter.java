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
 * file filter that uses eString.DAT_EXT;
 * see class DataFileFilters which allows multiple extensions;
 * also compare to class fileFilter which implements FilenameFilter;
 */
public class datFileFilter extends javax.swing.filechooser.FileFilter {
    public boolean accept(File file) {
        return file.isDirectory() || file.getName().toLowerCase().endsWith(eString.DAT_EXT.toLowerCase());
    }

    public String getDescription() {
        return "coordinate data files *." + eString.DAT_EXT;
    }
}

