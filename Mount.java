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
 * interface for all mounting types where mounting types denoted by MOUNT_TYPE;
 * includes access to the meridianFlip object;
 */
public interface Mount {
    void mountType(MOUNT_TYPE mt);
    MOUNT_TYPE mountType();
    void canMoveToPole(boolean canMoveToPole);
    boolean canMoveToPole();
    void canMoveThruPole(boolean canMoveThruPole);
    boolean canMoveThruPole();
    void primaryAxisFullyRotates(boolean primaryAxisFullyRotates);
    boolean primaryAxisFullyRotates();
    void meridianFlipPossible(boolean meridianFlipPossible);
    boolean meridianFlipPossible();
    meridianFlip meridianFlip();
}

