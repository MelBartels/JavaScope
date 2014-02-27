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
 * builds IJFrameStatusTextArea styled objects: returning a StatusType styled object with an update() method;
 */
public class JFrameStatusTextAreaFactory {
    IJFrameStatusTextArea IJFrameStatusTextArea;

    // returns pointer to IJFrameStatusTextArea object if successful, otherwise returns null
    IJFrameStatusTextArea build(STATUS_TYPE st) {
        try {
            // create new class by class name, use it to create new object instance, then return correct cast
            IJFrameStatusTextArea = (IJFrameStatusTextArea) Class.forName(st.toString()).newInstance();
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

        if (IJFrameStatusTextArea == null)
            console.errOut("unhandled mountTypeIJFrameStatusTextArea: " + st);
        else {
            IJFrameStatusTextArea.statusType(st);
            console.stdOutLn("built IJFrameStatusTextArea object: " + IJFrameStatusTextArea.statusType());
        }

        return IJFrameStatusTextArea;
    }
}

