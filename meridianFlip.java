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
 * class detailing a mounting's meridian flip properties and state;
 */
public class meridianFlip {
    boolean required;
    boolean auto;
    double autoFuzzDeg;

    // if false, then scope is on east side of pier facing west, if true, then scope on west side facing east
    boolean flipped;
    double autoFuzzRad;

    meridianFlip() {
        console.stdOutLn("creating meridianFlip object");
        autoFuzzDeg = 7.5;
    }
}

