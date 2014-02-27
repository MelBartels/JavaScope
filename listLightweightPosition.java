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
public class listLightweightPosition {
    java.util.List lp;

    listLightweightPosition() {
        lp = new ArrayList();
    }

    void init() {
        free();
    }

    void add(lightweightPosition p) {
        lightweightPosition tempP;

        lp.add(new lightweightPosition());
        tempP = (lightweightPosition) lp.get(lp.size()-1);
        copyLwpToLwp(p, tempP);
    }

    int size() {
        return lp.size();
    }

    lightweightPosition get(int ix) {
        return (lightweightPosition) lp.get(ix);
    }

    Iterator iterator() {
        return lp.iterator();
    }

    void sortByName() {
        Collections.sort(lp, new listLightweightPositionByName());
    }

    void sortBySource() {
        Collections.sort(lp, new listLightweightPositionBySource());
    }

    void free() {
        lp.clear();
    }

    void display() {
        Iterator it;
        lightweightPosition p;

        System.out.println("display of lightweightPosition:");
        it = lp.iterator();
        while (it.hasNext()) {
            p = (lightweightPosition) it.next();
            System.out.println(p.name
            + " "
            + p.ra*units.RAD_TO_DEG
            + " "
            + p.dec*units.RAD_TO_DEG);
        }
        System.out.println("end of listPosition display");
    }

    static void copyLwpToLwp(lightweightPosition lwp, lightweightPosition lwp2) {
        lwp2.ra = lwp.ra;
        lwp2.dec = lwp.dec;
        lwp2.name = lwp.name;
        lwp2.source = lwp.source;
    }

    static void copyLwpToPosition(lightweightPosition lwp, position p) {
        p.ra.rad = lwp.ra;
        p.dec.rad = lwp.dec;
        p.objName = lwp.name;
    }

    static void copyPositionToLwp(position p, lightweightPosition lwp, String source) {
        lwp.ra = p.ra.rad;
        lwp.dec = p.dec.rad;
        lwp.name = p.objName;
        lwp.source = source;
    }
}

