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
 * builds limtMotion objects based on LIMIT_MOTION_TYPE
 */
public class limitMotionFactory {
    LimitMotion LimitMotion;

    // returns pointer to LimitMotion object if successful, otherwise returns null
    LimitMotion build(LIMIT_MOTION_TYPE lmt) {
        try {
            // create new class by class name, use it to create new object instance, then return correct cast
            LimitMotion = (LimitMotion) Class.forName(lmt.toString()).newInstance();
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

        if (LimitMotion == null)
            console.errOut("unhandled limitMotionType: " + lmt);
        else {
            LimitMotion.limitMotionType(lmt);
            LimitMotion.enable(true);
            console.stdOutLn("built LimitMotionType object: " + LimitMotion.limitMotionType());
        }

        return LimitMotion;
    }
}

