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
 * each entry in PECwork contains the PEC index and accumulated guiding correction;
 * it is possible for successive entries in the guide array to skip over PEC indexes since the motor
 * may have rotated more than the angular distance between PEC indexes
 */
public class PECwork {
    double arcsec;
    boolean entry;
}

