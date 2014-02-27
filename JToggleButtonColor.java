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

public class JToggleButtonColor extends javax.swing.JToggleButton  {
    JToggleButtonColor() {
        defaultColor();
    }

    void defaultColor() {
        setBackground(new java.awt.Color(
        cfg.getInstance().toggle.r,
        cfg.getInstance().toggle.g,
        cfg.getInstance().toggle.b));
    }

    void goColor() {
        setBackground(new java.awt.Color(
        cfg.getInstance().goToggle.r,
        cfg.getInstance().goToggle.g,
        cfg.getInstance().goToggle.b));
    }

    void stopColor() {
        setBackground(new java.awt.Color(
        cfg.getInstance().stopToggle.r,
        cfg.getInstance().stopToggle.g,
        cfg.getInstance().stopToggle.b));
    }
}

