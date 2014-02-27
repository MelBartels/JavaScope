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
 * adds drift values to dataFile format
 */
public class cometFile extends dataFile {
    boolean loadFromFile(String filename) {
        lp = new listPosition();
        position p = new position("cometFile loadFromFile");

        this.filename = new String(filename);

        console.stdOutLn("cometFile:loadFromFile " + filename);

        count = 0;
        try {
            input = new BufferedReader(new FileReader(filename));
            while (console.fReadLineEquatCoordWithDrift(input, p)) {
                if (cfg.getInstance().precessionNutationAberration)
                    p.applyPrecessionCorrectionFromEpochYear(cfg.getInstance().dataFileCoordYear);
                lp.add(p);
                count++;
            }
            input.close();
            return true;
        }
        catch (IOException ioe) {
            return false;
        }
    }

    void display() {
        Iterator it;
        position p;

        console.stdOutLn(filename + ": displaying list of positions in datafile format:");
        it = lp.iterator();
        while (it.hasNext()) {
            p = (position) it.next();
            p.showCometFileFormat();
        }
        console.stdOutLn("end of position list for " + filename);
    }

    void test() {
        cometFile cf = new cometFile();
        cf.loadFromFile("test.comet");
        cf.display();
    }
}

