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

public class mountTypeNone extends mountBase implements Mount {
    mountTypeNone() {
        alignment = ALIGNMENT.none;
        canMoveToPole = true;
        canMoveThruPole = false;
        primaryAxisFullyRotates = true;
        meridianFlipPossible(true);
        meridianFlip().required = false;
    }
}

