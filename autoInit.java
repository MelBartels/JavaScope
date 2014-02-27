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
 * returns 2 most suitable initialization positions;
 * based on elevation above horizon;
 */
public class autoInit {
    dataFile df;
    convertTrig ct;

    autoInit() {
        loadDataFile();
    }

    boolean loadDataFile() {
        int ix;

        df = new dataFile();
        df.loadFromFile(cfg.getInstance().autoGenInitFile);
        if (df.count < 1) {
            console.errOut("no coordinate positions found in datafile " + df.filename);
            return false;
        }
        else
            return true;
    }

    void getInits() {
        int ix;

        ct = new convertTrig("autoInit");
        astroTime.getInstance().calcSidT();
        for (ix = 0; ix < df.count; ix++) {
            ct.p.ra.rad = df.lp.get(ix).ra.rad;
            ct.p.dec.rad = df.lp.get(ix).dec.rad;
            ct.getAltaz();
            df.lp.get(ix).alt.rad = ct.p.alt.rad;
            df.lp.get(ix).az.rad = ct.p.az.rad;
        }
        df.lp.sort(LINK_POS_SORT_KEY.altDescend);
        //display();
    }

    boolean initsAvail(double altLimitRad) {
        int ix;
        int avail;

        avail = 0;
        for (ix = 0; ix < df.count; ix++) {
            if (df.lp.get(ix).alt.rad >= altLimitRad)
                avail++;
            if (avail >= 2)
                break;
        }
        if (avail >= 2)
            return true;
        else
            return false;
    }

    position p1() {
        if (df == null)
            return null;
        if (df.lp == null)
            return null;
        return df.lp.get(0);
    }

    position p2() {
        if (df == null)
            return null;
        if (df.lp == null)
            return null;
        return df.lp.get(1);
    }

    void display() {
        int ix;

        for (ix = 0; ix < df.count; ix++)
            df.lp.get(ix).showCoordDeg();
    }
}

