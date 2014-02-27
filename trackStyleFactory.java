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
 * factory class for tracking styles
 */
public class trackStyleFactory {
    TrackStyle ts;

    TrackStyle build(TRACK_STYLE_ID trackStyleID) {
        try {
            // create new class by class name, use it to create new object instance, then return correct cast
            ts = (TrackStyle) Class.forName(trackStyleID.toString()).newInstance();
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

        if (ts == null)
            console.errOut("unhandled trackStyleID: " + trackStyleID);
        ts.trackStyleID(trackStyleID);

        return ts;
    }
}

