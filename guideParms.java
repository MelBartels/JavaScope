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
 * a class containing parameters needed for the recording of guiding efforts vs PEC
 */
public class guideParms {
    String servoIDStr;
    String descriptStr;
    double motorStepsPerPECArray;
    int PECSize;
    ROTATION PECRotation;
    int guidingCycles;
    double PECIxOffset;
}

