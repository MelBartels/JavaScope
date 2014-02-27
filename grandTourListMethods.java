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
 * design separates forms from methods, segregating grand tour methods into a class, 
 * and likewise for cmd_scope methods;
 * these method classes are meant to be encapsulated w/in any form with events that 
 * will control grandtour and cmd_scope lists;
 *
 * JFrameCmdsScrollable (form with events to be extended) 
 *    ^
 *    |
 * JFrameCmdsScrollableActionListener (adds timer to update form) 
 *    ^
 *    |
 * JFrameCmdsScrollableBase (holds common vars and methods)
 *    ^                                       ^
 *    |                                       |
 * JFrameGrandTour                         JFrameCmdScope    (UIs for grandtour and cmd_scope: contains 
 *     event handlers that call actions in encapsulted class)
 *    ^                                       ^
 * (composition)                           (composition)
 *    v                                       v
 *    |                                       |
 * grandTourListMethods    cmdFileChooserCmdLauncher 
 *     calls appropriate file loader (respectively dataFileChooser and cmdFileChooser);
 *     executes grand tour and cmd_scope actions; 
 *     contains track reference;
 *     these two classes also encapsulated w/in JFrameMain;
 *     these objects instantiated in JFrameMain and references passed to JFrameGrandTour and JFrameCmdScope
 *     so that both forms can act on same grandtour or cmd_scope list;
 */

/**
 *selects a grand tour file and executes various actions against it, eg load, nearest
 */
class grandTourListMethods {
    private dataFileChooser dtc;
    private track t;
    
    grandTourListMethods() {}
    
    public void registerTrackReference(track t) {
        this.t = t;
    }
    
    public boolean chooseFileLaunchCmd() {
        if (t != null) {
            dtc = new dataFileChooser(new JFrame(), "open a grandtour datafile");
            if (dtc.fileSelected) {
                t.cmdCol.UICmd.newCmd("UI",
                CMD_SCOPE.cmd_scope_grandtour_file.toString()
                + " "
                + dtc.file.getAbsoluteFile());
                cfg.getInstance().handpadMode = HANDPAD_MODE.handpadModeGrandTour;
                
                return true;
            }
        }
        return false;
    }
    
    public String selectedFilename() {
        if (t != null && dtc != null && dtc.fileSelected) 
            return dtc.file.getAbsoluteFile().toString();
        else
            return "";
    }
    
    public boolean fileSelected() {
        return dtc.fileSelected;
    }
    
    public boolean load() {
        return chooseFileLaunchCmd();
    }
    
    public void nearest() {
        if (t != null)
            t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_grandtour_nearest_object.toString());
    }

    public void previous() {
        if (t != null)  
            t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_grandtour_previous_object.toString());
    }
    
    public void next() {
        if (t != null) 
            t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_grandtour_next_object.toString());
    }
    
    public void newIndex(int index) {
        if (t != null)
            t.grandTour.setCurrentIx(index);
    }
}

