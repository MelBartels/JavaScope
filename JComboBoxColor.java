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

public class JComboBoxColor extends javax.swing.JComboBox {
    JComboBoxColor() {
        defaultColor();
    }

    void defaultColor() {
        setBackground(new java.awt.Color(
        cfg.getInstance().comboBox.r,
        cfg.getInstance().comboBox.g,
        cfg.getInstance().comboBox.b));
    }
}

