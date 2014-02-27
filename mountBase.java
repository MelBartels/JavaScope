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
 * contains methods and properties common to all mounting types;
 * includes state information;
 * includes a reference to a meridianFlip object;
 * meridianFlip reference set to null if it is not a characteristic of the mount, check with meridianFlipPossible;
 */
public class mountBase {
    MOUNT_TYPE mountType;
    ALIGNMENT alignment;

    boolean canMoveToPole;
    boolean canMoveThruPole;
    boolean primaryAxisFullyRotates;
    boolean meridianFlipPossible;

    meridianFlip meridianFlip;

    mountBase() {}

    public void mountType(MOUNT_TYPE mt) {
        this.mountType = mt;
    }

    public MOUNT_TYPE mountType() {
        return mountType;
    }

    public void canMoveToPole(boolean canMoveToPole) {
        this.canMoveToPole = canMoveToPole;
    }

    public boolean canMoveToPole() {
        return canMoveToPole;
    }

    public void canMoveThruPole(boolean canMoveThruPole) {
        this.canMoveThruPole = canMoveThruPole;
    }

    public boolean canMoveThruPole() {
        return canMoveThruPole;
    }

    public void primaryAxisFullyRotates(boolean primaryAxisFullyRotates) {
        this.primaryAxisFullyRotates = primaryAxisFullyRotates;
    }

    public boolean primaryAxisFullyRotates() {
        return primaryAxisFullyRotates;
    }

    public void meridianFlipPossible(boolean meridianFlipPossible) {
        this.meridianFlipPossible = meridianFlipPossible;
        if (meridianFlipPossible) {
            if (meridianFlip == null)
                meridianFlip = new meridianFlip();
        }
        else
            meridianFlip = null;
    }

    public boolean meridianFlipPossible() {
        return meridianFlipPossible;
    }

    public meridianFlip meridianFlip() {
        return meridianFlip;
    }
}

