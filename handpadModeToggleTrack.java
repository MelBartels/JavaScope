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

public class handpadModeToggleTrack extends handpadModeBase implements HandpadModes {
    public void processModeKeys() {
        if (leftKeyPressed()) {
            handpadModeBeep();
            t.turnTrackingOn();
            t.setCurrentAltazGetEquatCopyTarget();
        }
        else if (rightKeyPressed()) {
            handpadModeBeep();
            t.turnTrackingOffStopAllMotors();
        }
    }
}

