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
 *this mediator updates the passed in label to the String value as passed in the update() method;
 */
public class updateLabelMediator {
    JLabel label;

    updateLabelMediator(JLabel label) {
        this.label = label;
    }

    void update(String s) {
        label.setText(s);
    }
}

