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
 *this mediator updates the passed in textField to the String value as passed in the update() method;
 */
public class updateTextFieldMediator {
    JTextField textField;

    updateTextFieldMediator(JTextField textField) {
        this.textField = textField;
    }

    void update(String s) {
        textField.setText(s);
    }
}
