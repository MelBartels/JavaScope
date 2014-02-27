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
 * user interface command string: send it to an execStringCmdScope object for execution
 */
public class UICmd {
    track t;
    execStringCmdScope esc;

    UICmd(track t) {
        this.t = t;
    }

    void newCmd(String name, String cmdString) {
        esc = new execStringCmdScope(name, t, cmdString);
        esc.checkProcessCmd();
    }

    boolean checkProcessCmd() {
        if (esc != null && esc.cl != null)
            return esc.cl.checkProcessCmd(DIRECTION.forward, cmdScopeList.EXEC_CMD);
        return false;
    }
}

