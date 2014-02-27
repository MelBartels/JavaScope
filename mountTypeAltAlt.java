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

public class mountTypeAltAlt extends mountBase implements Mount {
    mountTypeAltAlt() {
        alignment = ALIGNMENT.altaz;
        canMoveToPole = true;
        canMoveThruPole = false;
        primaryAxisFullyRotates = false;
        meridianFlipPossible(false);
    }
}

