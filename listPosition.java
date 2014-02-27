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
 * a list of positions and methods to operate on the list
 */
public class listPosition {
    java.util.List lp;

    listPosition() {
        lp = new ArrayList();
    }

    void init() {
        free();
    }

    void add(position p) {
        position tempP;

        lp.add(new position());
        tempP = (position) lp.get(lp.size()-1);
        tempP.copy(p);
    }

    int size() {
        return lp.size();
    }

    position get(int ix) {
        return (position) lp.get(ix);
    }

    Iterator iterator() {
        return lp.iterator();
    }

    void sort(LINK_POS_SORT_KEY key) {
        Collections.sort(lp, new listPositionComparator(key));
    }

    void sortClosestAltaz(position p) {
        Collections.sort(lp, new listPositionComparatorByClosestAltaz(p));
    }

    void free() {
        lp.clear();
    }

    void display() {
        Iterator it;
        position p;

        System.out.println("display of listPosition:");
        it = lp.iterator();
        while (it.hasNext()) {
            p = (position) it.next();
            p.showCoord();
        }
        System.out.println("end of listPosition display");
    }

    void showDataFileFormat() {
        Iterator it;
        position p;

        System.out.println("display of listPosition in dataFile format:");
        it = lp.iterator();
        while (it.hasNext()) {
            p = (position) it.next();
            p.showDataFileFormat();
        }
        System.out.println("end of listPosition display");
    }

    void test() {
        position p = new position("listPosition test");

        System.out.println("class listPosition test");
        System.out.println("adding 3 new positions to the list");
        p.ra.rad = 1.;
        add(p);
        p.ra.rad = 3.;
        add(p);
        p.ra.rad = 2.;
        add(p);
        sort(LINK_POS_SORT_KEY.raAscend);
        display();
        System.out.println("ra coordinates displayed should be in ascending order");
        System.out.println("freeing all positions");
        free();
        System.out.println("size " + size());
        display();
    }
}

