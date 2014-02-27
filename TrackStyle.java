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
 * all tracking styles must implement this interface
 */
public interface TrackStyle {
    public void trackStyleID(TRACK_STYLE_ID trackStyleID);
    public String trackStyleID();
    public void setDeccelCheck(boolean value);
    public void trackByVelSubr(int id);
    void track(int id);
    void displayTrackingVars(int id);
}

