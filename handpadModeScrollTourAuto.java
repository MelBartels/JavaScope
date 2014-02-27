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

public class handpadModeScrollTourAuto extends handpadModeBase implements HandpadModes {
    public void processModeKeys() {
        // if in autoscrolling mode, left key turns on autoscrolling and right key ends autoscrolling
        if (scrollTimer == 0)
            if (t.cmdCol.cmdScopeList.current != null) {
                handpadModeBeep();
                if (leftKeyPressed())
                    t.cmdCol.cmdScopeList.autoOn = true;
                else if (rightKeyPressed())
                    t.cmdCol.cmdScopeList.autoOn = false;
            }
    }
}

