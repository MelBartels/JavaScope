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

public class mountTypeAltazimuth extends mountBase implements Mount {
    mountTypeAltazimuth() {
        alignment = ALIGNMENT.altaz;
        canMoveToPole = true;
        canMoveThruPole = false;
        primaryAxisFullyRotates = true;
        meridianFlipPossible(false);
    }
}

