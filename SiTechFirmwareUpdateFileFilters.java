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
 * wraps FileFilterBuilder, setting up a file filter based on eString.SITECH_FIRMWARE_UPDATE_FILE_EXT;
 * FileFilterBuilder allows multiple extensions, see datFileFilter which is for a single extension;
 */
public class SiTechFirmwareUpdateFileFilters extends FileFilterBuilder {
    SiTechFirmwareUpdateFileFilters() {
        super(new String[] {eString.SITECH_FIRMWARE_UPDATE_FILE_EXT}, "Sidereal Technology firmware upload file");
    }

    public boolean accept(File f) {
        return super.accept(f);
    }

    public String getDescription() {
        return super.getDescription();
    }
}

