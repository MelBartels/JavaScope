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
 * this class holds a single servo command log entry
 * see trackAnalysis class for further comments
 */
public class logServoCmd {
    int id;
    byte cmd;
    boolean readStatusSuccessful;
}

