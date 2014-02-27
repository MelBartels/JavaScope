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
 * tests dataFileList using cfg's datFileLocation
 */
public class dataFileListPositionsTest {
    dataFileListPositionsTest() {
        System.out.println("test of dataFileListPositions:");

        System.out.println("loading dataFiles from: " + cfg.getInstance().datFileLocation);
        dataFileListPositions.getInstance().loadFromDir(cfg.getInstance().datFileLocation);
        System.out.println(dataFileListPositions.getInstance().lp.size() + " objects loaded");

        listPosition lp = new listPosition();
        System.out.println("searching for closest objects to Ra 12 hrs, Dec 30 deg, press return to continue...");
        console.getString();
        cfg.getInstance().current.ra.rad = 12. * units.HR_TO_RAD;
        cfg.getInstance().current.dec.rad = 30. * units.DEG_TO_RAD;
        dataFileListPositions.getInstance().findNearestObjects(20, lp);
        dataFileListPositions.getInstance().displayNearestObjects(lp);

        System.out.println("writing library file, press return to continue...");
        console.getString();
        dataFileListPositions.getInstance().writeLibraryFile();

        System.out.println("end of dataFileListPositions test");
    }
}

