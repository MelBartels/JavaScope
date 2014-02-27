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
 * label that reports progress of directory scan for datafiles
 */
public class monitorFindAllObjectsInDataDirLabel extends javax.swing.JLabel implements ActionListener {
    private findAllObjectsInFileSet faofs;
    javax.swing.Timer timer;

    monitorFindAllObjectsInDataDirLabel(findAllObjectsInFileSet faofs) {
        super("scanning directory for datafiles");
        this.faofs = faofs;
        timer = new javax.swing.Timer(100, this);
    }

    void startTimer() {
        timer.start();
    }

    void stopTimer() {
        timer.stop();
    }

    public void actionPerformed(ActionEvent e) {
        if (faofs.df!= null)
            setText("...scanned "
            + faofs.ix
            + " of "
            + faofs.fileList.length
            + " files, "
            + faofs.objectCount
            + " objects searched...");
    }
}

