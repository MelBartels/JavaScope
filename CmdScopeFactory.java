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
 * builds a cmdScope
 */
public class CmdScopeFactory {
    cmdScope cmd;

    cmdScope build(CMD_SCOPE cmdTypeScope, StringTokenizer st) {
        try {
            // create new class by class name, use it to create new object instance, then return correct cast
            cmd = (cmdScope) Class.forName(cmdTypeScope.toString()).newInstance();
        }
        catch (ClassNotFoundException cnfe) {
            console.errOut(cnfe.toString());
        }
        catch (InstantiationException ie) {
            console.errOut(ie.toString());
        }
        catch (IllegalAccessException iae) {
            console.errOut(iae.toString());
        }
        if (cmd == null)
            console.errOut("unhandled cmdTypeScope: " + cmdTypeScope);
        else {
            cmd.cmdTypeScope(cmdTypeScope);
            if (!cmd.parseCmd(st)) {
                cmd = null;
                console.errOut("could not parse command");
            }
        }
        return cmd;
    }
}

