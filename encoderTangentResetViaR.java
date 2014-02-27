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
 * uses the tangent code base but changes the resetByte to 'R'
 */
public class encoderTangentResetViaR extends encoderTangentNoReset {
    encoderTangentResetViaR() {
        resetByte = (byte) 'R';
    }
}

