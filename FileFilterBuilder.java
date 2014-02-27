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
 * file filter using an array of filters;
 * down cased so is case insensitive;
 * based on O'Reilly Java Swing, chapter 12: Chooser Dialogs, page 383;
 */
public class FileFilterBuilder extends javax.swing.filechooser.FileFilter {
    private String[] exts;
    private String desc;

    FileFilterBuilder(String ext) {
        this (new String[] {ext}, null);
    }

    // rebuilds the String array everytime method is called
    FileFilterBuilder(String[] exts, String desc) {
        int ix;

        // clone the extensions
        this.exts = new String[exts.length];
        for (ix = exts.length-1; ix >= 0; ix--)
            this.exts[ix] = exts[ix].toLowerCase();
        // add a default description if necessary
        this.desc = (desc==null? exts[0]+" files": desc);
    }

    public boolean accept(File f) {
        String name;
        int ix;

        if (f.isDirectory())
            return true;
        name = f.getName();
        for (ix = exts.length-1; ix >= 0; ix--)
            if (name.toLowerCase().endsWith(exts[ix]))
                return true;
        return false;
    }

    public String getDescription() {
        return desc;
    }
}

