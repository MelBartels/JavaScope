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
 * builds mounting types;
 * for different mounting types and their descriptions, see class MOUNT_TYPE;
 * each mounting type built is responsible for building a meridianFlip object if appropriate:
 *    do this by calling meridianFlipPossible(true) in the mounting type constructor,
 *    now meridianFlip properties/methods/state accessible through the interface's meridianFlip() method, ie,
 *    Mount.meridianFlip().autoFuzzRad = 0.,
 *    always check before accessing meridianFlip() by Mount.meridianFlipPossible();
 *   build/destroy meridianFlip objects by calling Mount.meridianFlipPossible(true) or meridianFlipPossible(false);
 */
public class mountFactory {
    Mount Mount;

    // returns pointer to mountType object if successful, otherwise returns null
    Mount build(MOUNT_TYPE mt) {
        try {
            // create new class by class name, use it to create new object instance, then return correct cast
            Mount = (Mount) Class.forName(mt.toString()).newInstance();
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

        if (Mount == null)
            console.errOut("unhandled mountType: " + mt);
        else {
            Mount.mountType(mt);
            console.stdOutLn("built mountType object: " + Mount.mountType());
        }

        return Mount;
    }
}

