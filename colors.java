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
 * this class holds colors in form of red, green, blue
 */
public class colors {
    static final int NUM_COLORS = 3;
    int r, g, b;

    colors() {
        // set for gray
        r = g = b = 204;
    }
}

