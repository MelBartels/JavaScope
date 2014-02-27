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

public class JPanelColor extends javax.swing.JPanel {
    JPanelColor() {}

    void lightColor() {
        if (cfg.getInstance().usePanelColors)
            setBackground(new java.awt.Color(
            cfg.getInstance().lightPanel.r,
            cfg.getInstance().lightPanel.g,
            cfg.getInstance().lightPanel.b));
    }

    void mediumColor() {
        if (cfg.getInstance().usePanelColors)
            setBackground(new java.awt.Color(
            cfg.getInstance().mediumPanel.r,
            cfg.getInstance().mediumPanel.g,
            cfg.getInstance().mediumPanel.b));
    }

    void darkColor() {
        if (cfg.getInstance().usePanelColors)
            setBackground(new java.awt.Color(
            cfg.getInstance().darkPanel.r,
            cfg.getInstance().darkPanel.g,
            cfg.getInstance().darkPanel.b));
    }
}

