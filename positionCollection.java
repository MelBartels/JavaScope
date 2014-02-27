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
 * a collection pool of positions to be drawn against based on position.available;
 * this to avoid creating/destroying thousands of position objects in short periods of time;
 * based on ArrayList() as is class listPosition, but difference in concept and use;
 */
public class positionCollection {
    java.util.List lp;

    positionCollection() {
        lp = new ArrayList();
    }

    private position add() {
        position p;

        lp.add( new position());
        p = (position) lp.get(lp.size()-1);
        p.available = true;
        console.stdOutLn("added to positionCollection: count = " + lp.size());
        return p;
    }

    position nextAvail() {
        Iterator it;
        position p = null;

        it = lp.iterator();
        while (it.hasNext()) {
            p = (position) it.next();
            if (p.available)
                break;
        }
        // if collection empty, or, no position available in collection then add
        if (p == null || !p.available)
            p = add();
        // whether adding new position or retrieving available position, mark it not available
        p.available = false;
        return p;
    }

    void display() {
        Iterator it;
        position p = null;

        it = lp.iterator();
        while (it.hasNext()) {
            p = (position) it.next();
            System.out.print("available "
            + p.available
            + ": ");
            p.showCoordDeg();
        }
    }
}

