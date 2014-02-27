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
 * holds vars and methods common to all classes that extend JFrameCmdsScrollableActionListener->JFrameCmdsScrollable
 */
class JFrameCmdsScrollableBase extends JFrameCmdsScrollableActionListener {
    private updateTextFieldMediator updateTextFieldMediator;
    protected DefaultListModel dlm;
    protected track t;
    
    JFrameCmdsScrollableBase(String title) {
        super(title);
        jToggleButtonAutoUpdate.setSelected(true);
    }
    
    public void registerTextFieldMediator(updateTextFieldMediator updateTextFieldMediator) {
        this.updateTextFieldMediator = updateTextFieldMediator;
    }
    
    public void registerTrackReference(track t) {
        this.t = t;
    }
    
    public void updateTextField(String s) {
        updateTextFieldMediator.update(s);
    }
    
    public void setFilename(String title) { 
        jTextFieldFilename.setText(title);
    }
    
    public void loadFile(String filename) {
        BufferedReader input;
        String s;
        
        jListCmd.setModel(new DefaultListModel());
        dlm = (DefaultListModel) jListCmd.getModel();
        
        try {
            input = new BufferedReader(new FileReader(filename));
            s = input.readLine();
            while (s != null) {
                dlm.addElement(s);
                s = input.readLine();
            }
            input.close();
        }
        catch (IOException ioe) {
            dlm.addElement("could not open " + filename);
            console.errOut("could not open " + filename);
        }
    }
    
    public void makeSelectionVisible() {
        jListCmd.ensureIndexIsVisible(jListCmd.getSelectedIndex());
    }
}
