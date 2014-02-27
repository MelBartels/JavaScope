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

public class handpadModePolarAlign extends handpadModeBase implements HandpadModes {
    public void processModeKeys() {
        if (leftOrRightKeyPressed() && handpadButtonTimer == 0)
            if (cfg.getInstance().initialized()) {
                handpadModeBeep();
                handpadButtonTimer = STARTING_HANDPAD_TIMER_VALUE;
                t.processPolarAlign();
            }
            else
                console.errOut("cannot run the polar align procedure: must init 1 and 2 first");
    }
}

