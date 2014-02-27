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
 * interface for handpad designs or layouts
 */
public interface HandpadDesigns {
    void handpadDesign(HANDPAD_DESIGN handpadDesign);
    HANDPAD_DESIGN handpadDesign();
    void handpad(int handpad);
    boolean handpadSuccessfullyRead();
    boolean upDownButtonsReversed();
    void readHandpad();
    int buttons();
    void passiveObserver(boolean passiveObserver);
    boolean passiveObserver();
    String displayHandpad();
}

