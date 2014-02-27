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
 * window for grand tour manipulation
 */

class JFrameGrandTour extends JFrameCmdsScrollableBase {
    private grandTourListMethods grandTourListMethods;

    JFrameGrandTour() {
        super("Grand Tour Objects");
        jToggleButtonOption1.setText("nearest");
        jToggleButtonOption2.setVisible(false);
    }

    public void registerGrandTourListMethods(grandTourListMethods grandTourListMethods) {
        this.grandTourListMethods = grandTourListMethods;
    }
    
    protected void jToggleButtonLoadActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonLoad.setSelected(false);
        load();
    }
    
    protected void jToggleButtonOption1ActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonOption1.setSelected(false);
        nearest();
    }
    
    protected void jToggleButtonPreviousActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonPrevious.setSelected(false);
        grandTourListMethods.previous();
    }

    protected void jToggleButtonNextActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonNext.setSelected(false);
        grandTourListMethods.next();
    }

    protected void jListCmdMouseReleased(java.awt.event.MouseEvent evt) {
        grandTourListMethods.newIndex(jListCmd.getSelectedIndex());
    }

    protected void exitForm(java.awt.event.WindowEvent evt) {
        setVisible(false);
        stopTimer();
    }
    
    public void actionPerformed(ActionEvent ae) {
        if (t != null && jToggleButtonAutoUpdate.isSelected()) {
            jListCmd.setSelectedIndex(t.grandTour.currentIx);
            makeSelectionVisible();
        }        
    }
    
    public boolean load() {
        if (grandTourListMethods.load()) {
            setFilename(grandTourListMethods.selectedFilename());
            updateTextField(grandTourListMethods.selectedFilename());
            loadFile(grandTourListMethods.selectedFilename());
            show();
            return true;
        }
        return false;
    }
    
    public void show() {
        super.show();
        startTimer();
    }
    
    public void nearest() {
        if (grandTourListMethods != null && grandTourListMethods.fileSelected())
            grandTourListMethods.nearest();
    }
}
