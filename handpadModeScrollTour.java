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

public class handpadModeScrollTour extends handpadModeBase implements HandpadModes {
    public void processModeKeys() {
        /**
         * external control should load cmdScopeList; execution of command list ends when end of file reached;
         * left key moves onto next command (which can be a command to turn on autoscrolling), right key returns to previous command
         */
        if (scrollTimer == 0)
            if (leftOrRightKeyPressed())
                if (t.cmdCol.cmdScopeList.current != null) {
                    handpadModeBeep();
                    if (leftKeyPressed())
                        t.cmdCol.cmdScopeList.checkProcessCmd(DIRECTION.forward, t.cmdCol.cmdScopeList.EXEC_CMD);
                    if (rightKeyPressed())
                        t.cmdCol.cmdScopeList.checkProcessCmd(DIRECTION.backward, t.cmdCol.cmdScopeList.EXEC_CMD);
                }
    }
}

