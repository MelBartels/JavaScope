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
 * interface for LimitMotion objects
 */
public interface LimitMotion {
    void limitMotionType(LIMIT_MOTION_TYPE lmt);
    LIMIT_MOTION_TYPE limitMotionType();
    void enable(boolean enabled);
    boolean enable();
    java.util.List listLimitWindow();
    void addLimitWindow(limitWindow lw);
    boolean limitExceeded();
    boolean limitExceeded(double alt, double az);
    void display();
    void saveToFile();
    boolean loadFromFile();
    void test();
}

