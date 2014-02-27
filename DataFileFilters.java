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
 * wraps FileFilterBuilder, setting up a file filter based on eString.DAT_EXT;
 * FileFilterBuilder allows multiple extensions, see datFileFilter which is for a single extension;
 */
public class DataFileFilters extends FileFilterBuilder {
    DataFileFilters() {
        super(new String[] {eString.DAT_EXT}, "coordinate data files");
    }

    public boolean accept(File f) {
        return super.accept(f);
    }

    public String getDescription() {
        return super.getDescription();
    }
}

