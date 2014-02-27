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
 *selects a cmd_scope file and executes various actions against it, eg load, execute, cancel
 */
class cmdScopeListMethods {
    private cmdFileChooser cfc;
    private track t;
    
    cmdScopeListMethods() {}
    
    public void registerTrackReference(track t) {
        this.t = t;
    }
    
    public String selectedFilename() {
        if (t != null && cfc != null && cfc.fileSelected) 
            return cfc.file.getAbsoluteFile().toString();
        else
            return "";
    }
    
    public boolean fileSelected() {
        return cfc.fileSelected;
    }

    public boolean load() {
        if (t != null) {
            cfc = new cmdFileChooser(new JFrame());
            if (cfc.fileSelected) {
                t.cmdCol.cmdScopeList.resetVars();            
                return true;
            }
        }
        return false;
    }
    
    public void execute() {
        if (t != null && cfc.fileSelected)
            t.cmdCol.UICmd.newCmd("UI",
            CMD_SCOPE.cmd_scope_exec_cmd_file.toString()
            + " "
            + cfc.file.getAbsoluteFile().toString());
    }
    
    public void cancel() {
        if (t != null)
            t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_cancel_all_cmds.toString());
    }
    
    public void previous() {
        if (t != null) 
            t.cmdCol.cmdScopeList.checkProcessCmd(DIRECTION.backward, t.cmdCol.cmdScopeList.EXEC_CMD);
    }

    public void next() {
        if (t != null) 
            t.cmdCol.cmdScopeList.checkProcessCmd(DIRECTION.forward, t.cmdCol.cmdScopeList.EXEC_CMD);
    }

    public void newIndex(int index) {
        if (t != null) {
            t.cmdCol.cmdScopeList.setCurrentToIndex(index-1);
            next();
        }
    }
}
