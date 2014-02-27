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
 * handpad design factory
 */
public class handpadDesignFactory {
    HandpadDesigns HandpadDesigns;

    // returns pointer to HandpadDesigns object if successful, otherwise returns null
    HandpadDesigns build(HANDPAD_DESIGN handpadDesign) {
        try {
            // create new class by class name, use it to create new object instance, then return correct cast
            HandpadDesigns = (HandpadDesigns) Class.forName(handpadDesign.toString()).newInstance();
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

        if (HandpadDesigns == null)
            console.errOut("unhandled handpadDesign: " + handpadDesign);
        else {
            HandpadDesigns.handpadDesign(handpadDesign);
            console.stdOutLn("built HandpadDesigns object: " + HandpadDesigns.handpadDesign());
        }

        return HandpadDesigns;
    }
}

