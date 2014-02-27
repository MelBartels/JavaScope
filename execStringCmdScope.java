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
 * build a command list using a passed in string command, and, execute the command;
 *
 * complete the constructor, then call checkProcessCmd() which will result in two lines of code, ie
 *     execStringCmdScope esc = new execStringCmdScope(...);
 *     esc.checkProcessCmd();
 * do not attach checkProcessCmd() to the end of the constructor,
 * otherwise execStringCmdScope will remain null for the calling object, and since checkProcessCmd() can gain a
 * reference to the calling object thus obtaining a reference to itself, any 'super' calls via this circular
 * reference will therefore fail;
 */
public class execStringCmdScope {
    cmdScopeList cl;

    execStringCmdScope(String name, track t, String cmdString) {
        cl = new cmdScopeList(name);
        cl.init(t);
        cl.debug = false;
        cl.parseCmdFromString(cmdString);
    }

    boolean checkProcessCmd() {
        return cl.checkProcessCmd(DIRECTION.forward, cmdScopeList.EXEC_CMD);
    }
}

