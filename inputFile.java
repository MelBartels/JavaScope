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
 * inputFile is a recording of session's input equatorial coordinates
 */
public class inputFile extends dataFile {
    String inputDataHistFilename;

    inputFile() {
        //loadFromFile();
        openFile();
    }

    boolean openFile() {
        return super.openFile(eString.INPUT_FILENAME);
    }

    boolean loadFromFile() {
        return super.loadFromFile(eString.INPUT_FILENAME);
    }

    boolean writeEquatCoordToFile(position p, String reason) {
        return super.writeEquatCoordToFile(eString.INPUT_FILENAME, p, reason);
    }

    boolean addPosToLLPosWriteFile(position p, String reason) {
        lp.add(p);
        return writeEquatCoordToFile(p, reason);
    }

    /**
     * no need for function corresponding to dataFile.findNearestDataFileObjectAvoidPos() because object can
     * be added to inputFile beforehand
     */
    boolean findNearestDataFileObjectNotInInputFile(position matchPos, position nearestPos, dataFile df) {
        double sep;
        double sepInput;
        double minSep = Double.MAX_VALUE;
        double threshholdSep = 1.*units.ARCMIN_TO_RAD;
        int ix, ixB;
        position lpp;
        position dfp;

        if (df.lp == null)
            return false;
        if (lp == null)
            return false;

        // run through dataFile's objects
        for (ix = 0; ix < df.lp.size(); ix++) {
            dfp = df.lp.get(ix);
            sep = Math.abs(celeCoordCalcs.calcEquatAngularSepViaRa(matchPos, dfp));
            //System.out.println("\n\n\nmatchPos separation arcmin " + sep*units.RAD_TO_ARCMIN);
            //matchPos.showDataFileFormat();
            //dfp.showDataFileFormat();
            if (sep < minSep) {
                // now run through inputFile's objects for a match
                for ( ixB = 0; ixB < lp.size(); ixB++) {
                    lpp = lp.get(ixB);
                    // don't use perfect match algorithm:
                    //if (dfp.ra.rad == lpp.ra.rad && dfp.dec.rad == lpp.dec.rad)
                    // instead use 'imperfect match'
                    sepInput = Math.abs(celeCoordCalcs.calcEquatAngularSepViaRa(dfp, lpp));
                    //System.out.println("\n\n\ninputFile separation arcmin " + sepInput*units.RAD_TO_ARCMIN);
                    //dfp.showDataFileFormat();
                    //lpp.showDataFileFormat();
                    if (sepInput < threshholdSep)
                        break;
                }
                // if no match found (which is what we want), then set new minimum separation
                if (ixB == lp.size()) {
                    minSep = sep;
                    nearestPos.copy(dfp);
                }
            }
        }
        if (minSep < Double.MAX_VALUE)
            return true;
        else
            return false;
    }

    void writeInputDataHistFile() {
        input = null;
        output = null;
        String s;

        inputDataHistFilename = astroTime.getInstance().buildStringcurrentDateTimeNoDelimeters() + eString.INPUT_FILENAME;

        try {
            input = new BufferedReader(new FileReader(eString.INPUT_FILENAME));
        }
        catch (IOException ioe) {
            console.errOut("could not open " + eString.INPUT_FILENAME);
        }
        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(inputDataHistFilename)));
        }
        catch (IOException ioe) {
            console.errOut("could not open " + inputDataHistFilename);
        }
        if (input != null && output != null) {
            try {
                s = input.readLine();
                while (s != null) {
                    output.println(s);
                    s = input.readLine();
                }
            }
            catch (IOException ioe) {
                console.errOut("IOException in writeInputDataHistFile()");
            }
        }
        if (input != null)
            try {
                input.close();
            }
            catch (IOException ioe) {
                console.errOut("could not close input in writeInputDataHistFile()");
            }
        if (output != null)
            output.close();
    }

    void test() {
        String DataFilename = "messier.dat";
        position matchPos = new position("matchPos");
        position nearestPos = new position("nearestPos");
        dataFile Messier;

        System.out.println("test of findNearestDataFileObjectNotInInputFile() using "
        + DataFilename
        + " input.dat: input.dat.M87test");
        Messier = new dataFile();
        Messier.loadFromFile(DataFilename);
        Messier.display();

        loadFromFile("input.dat.M87test");
        display();

        System.out.println("(if M87 in input file, enter 187.76, then, at prompt enter 12.38)");
        System.out.print("ra (deg)?");
        console.getDouble();
        matchPos.ra.rad = console.d*units.DEG_TO_RAD;
        System.out.print("dec (deg)? ");
        console.getDouble();
        matchPos.dec.rad = console.d*units.DEG_TO_RAD;
        findNearestDataFileObjectNotInInputFile(matchPos, nearestPos, Messier);
        System.out.println("nearest position not in inputFile is: " + nearestPos.objName);
        nearestPos.showCoord();

        //System.out.println("test of writeInputDataHistFile():");
        //writeInputDataHistFile();
        //System.out.println("created input history file " + inputDataHistFilename);
    }
}

