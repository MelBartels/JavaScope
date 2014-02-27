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
 *buttons registered with this class are turned off as a group;
 */
public class buttonsOffMediator {
        java.util.List tba;
        String name;

    buttonsOffMediator(String name) {
        tba = new ArrayList();
        this.name = name;
    }

    void register(JToggleButtonColor button) {
        tba.add(button);

        //console.stdOutLn(name
        //+ " added button: "
        //+ button.getText());
    }

    void off() {
        Iterator it;
        JToggleButtonColor button;

        it = tba.iterator();
        while (it.hasNext()) {
            button = (JToggleButtonColor) it.next();
            button.setSelected(false);
        }
    }
}

