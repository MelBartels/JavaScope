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
 *using a passed in searchString, findAllObjectsInDataDir opens a dataDirChooser(); if a directory was selected,
 *then findAllObjectsInFileSet.findAllObjects() called, which runs through the file list,
 *calling dataFile.loadFromFile(), and dataFile.findAllObjects();
 *results placed in listPos;
 */
public class findAllObjectsInDataDir {
    String searchString;
    private boolean testing;
    private dataDirChooser ddc;
    listPosition listPos;
    findAllObjectsInFileSet faofs;
    monitorFindAllObjectsInDataDir monitorFindAllObjectsInDataDir;

    findAllObjectsInDataDir(final String searchString, final boolean testing) {
        this.searchString = searchString;
        this.testing = testing;
        ddc = new dataDirChooser(new JFrame());
        if (ddc.dirSelected) {
            listPos = new listPosition();
            // dataDirChooser sets cfg.getInstance().datFileLocation to selected directory
            faofs = new findAllObjectsInFileSet(searchString, cfg.getInstance().datFileLocation, testing);
            // set up the monitor before staring the search
            monitorFindAllObjectsInDataDir = new monitorFindAllObjectsInDataDir(faofs);
            monitorFindAllObjectsInDataDir.startTimer();
            faofs.findAllObjects(listPos);
            monitorFindAllObjectsInDataDir.shutDown();
        }
    }

    boolean dirSelected() {
        return ddc.dirSelected;
    }
}

