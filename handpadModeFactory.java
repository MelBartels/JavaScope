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
 * factory to create handpadModes
 */
public class handpadModeFactory {
    HandpadModes HandpadModes;

    // returns pointer to handpadMode object if successful, otherwise returns null
    HandpadModes build(HANDPAD_MODE handpadMode, track t) {
        try {
            // create new class by class name, use it to create new object instance, then return correct cast
            HandpadModes = (HandpadModes) Class.forName(handpadMode.toString()).newInstance();
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

        if (HandpadModes == null)
            console.errOut("unhandled handpadMode: " + handpadMode);
        else {
            HandpadModes.init(handpadMode, t);
            console.stdOutLn("built handpadMode object: " + HandpadModes.handpadMode());
        }

        return HandpadModes;
    }
}

