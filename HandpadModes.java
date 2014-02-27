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
 * interface for all the handpadModes to implement, either via handpadModeBase or via custom method
 */
public interface HandpadModes {
    void init(HANDPAD_MODE handpadMode, track t);
    HANDPAD_MODE handpadMode();
    void handpadModeBeep();
    void processTimers();
    void processModeKeys();
}

