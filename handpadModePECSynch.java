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

public class handpadModePECSynch extends handpadModeBase implements HandpadModes {
    public void processModeKeys() {
        if (leftKeyPressed()) {
            handpadModeBeep();
            if (cfg.getInstance().servoParm[SERVO_ID.altDec.KEY].PECActive) {
                t.synchPEC(SERVO_ID.altDec.KEY);
            }
        }
        else if (rightKeyPressed()) {
            handpadModeBeep();
            if (cfg.getInstance().servoParm[SERVO_ID.azRa.KEY].PECActive) {
                t.synchPEC(SERVO_ID.azRa.KEY);
            }
        }
    }
}

