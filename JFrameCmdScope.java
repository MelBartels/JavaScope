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
 * window for scroll file manipulation
 */

class JFrameCmdScope extends JFrameCmdsScrollableBase {
    private cmdScopeListMethods cmdScopeListMethods;
    private boolean readyToDisplayCmdsOnly;

    JFrameCmdScope() {
        super("Scope Commands");
        jToggleButtonOption1.setText("execute");
        jToggleButtonOption2.setText("cancel");
    }

    public void registerCmdScopeListMethods(cmdScopeListMethods cmdScopeListMethods) {
        this.cmdScopeListMethods = cmdScopeListMethods;
    }
    
    protected void jToggleButtonLoadActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonLoad.setSelected(false);
        load();
    }

    protected void jToggleButtonOption1ActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonOption1.setSelected(false);
        execute();
    }

    protected void jToggleButtonPreviousActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonPrevious.setSelected(false);
        cmdScopeListMethods.previous();
    }

    protected void jToggleButtonNextActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonNext.setSelected(false);
        cmdScopeListMethods.next();
    }

    protected void jToggleButtonOption2ActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonOption2.setSelected(false);
        cancel();
    }

    protected void jListCmdMouseReleased(java.awt.event.MouseEvent evt) {
        cmdScopeListMethods.newIndex(jListCmd.getSelectedIndex());
    }

    protected void exitForm(java.awt.event.WindowEvent evt) {
        setVisible(false);
        stopTimer();
    }
    
    public void actionPerformed(ActionEvent ae) {
        int cmdNum;
        cmdScope current;
        
        if (t != null && jToggleButtonAutoUpdate.isSelected()) { 
            cmdNum = t.cmdCol.cmdScopeList.executingCmdNum();
            
            if (readyToDisplayCmdsOnly && cmdNum > 0) {
                // change display from file listing to cmd listing, removing comments 
                // and malformed cmds
                setVisible(false);
                readyToDisplayCmdsOnly = false;
                jListCmd.setModel(new DefaultListModel());
                dlm = (DefaultListModel) jListCmd.getModel();
                current = t.cmdCol.cmdScopeList.first;
                while (current != null) {
                    dlm.addElement(current.buildCmdString());
                    current = current.next();
                }
                dlm.insertElementAt((Object) "--- ready to execute ---", 0);
                dlm.addElement((Object) "--- finished ---");
                setVisible(true);
            }

            if (!readyToDisplayCmdsOnly && cmdNum > 0) {
                jListCmd.setSelectedIndex(cmdNum);
                makeSelectionVisible();
            }
        }
    }
    
    public boolean load() {
        if (cmdScopeListMethods != null && cmdScopeListMethods.load()) {
            setFilename(cmdScopeListMethods.selectedFilename());
            updateTextField(cmdScopeListMethods.selectedFilename());
            // will include comments and any malformed cmds also
            loadFile(cmdScopeListMethods.selectedFilename());
            readyToDisplayCmdsOnly = true;
            show();
            return true;
        }
        return false;
    }
    
    public void show() {
        super.show();
        // start timer so as to look for beginning of execution
        startTimer();
    }
    
    public void execute() {
        if (cmdScopeListMethods != null && cmdScopeListMethods.fileSelected())
            cmdScopeListMethods.execute();
    }
    
    public void cancel() {
        cmdScopeListMethods.cancel();
    }
}
