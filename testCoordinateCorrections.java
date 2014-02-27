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

public class testCoordinateCorrections {
    testCoordinateCorrections() {
        int ix;
        String filename;
        PrintStream output;
        position start = new position();

        astroTime.getInstance().newSidT();

        position p = new position("testCoords");
        StringTokenizer st;
        double testJD;

        System.out.println("testCoordinateCorrections");
        System.out.println("\ntest#1: calcCorrectionsForEpochJD(testJD)");
        System.out.print("enter raHr, raMin, raSec, decDeg, decMin, decSec ");
        console.getString();
        st = new StringTokenizer(console.s);
        common.fReadEquatCoord(p, st);
        System.out.print("enter target year (hit return for today's date) ");
        console.d = 0.;
        console.getDouble();
        if (console.d == 0.) {
            astroTime.getInstance().calcSidT();
            testJD = astroTime.getInstance().JD;
        }
        else
            testJD = astroTime.getInstance().calcJDFromYear(console.d);
        p.applyPrecessionCorrectionFromEpochJDToEpochJD(eMath.JD2000, testJD);
        p.calcNutationAnnualAberrationCorrectionsForEpochJD(testJD);
        p.buildStringCorrections();
        System.out.println("\n" + p.stringCorrections);

        filename = "testCoordinateCorrections.txt";
        System.out.println("\ntest#2 writing out daily values for 2000 days starting at Y2000 to file " + filename);
        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(filename)));
            for (ix = 0; ix < 2000; ix++) {
                st = new StringTokenizer(console.s);
                common.fReadEquatCoord(p, st);
                p.applyPrecessionCorrectionFromEpochJDToEpochJD(eMath.JD2000, eMath.JD2000+ix);
                p.calcNutationAnnualAberrationCorrectionsForEpochJD(eMath.JD2000+ix);
                if (ix == 0)
                    start.copy(p);
                p.buildStringCorrections();
                output.println(p.stringCorrections
                + " dataFile format "
                + p.buildStringDataFileFormat()
                + " corrected coordinates (ra/dec) in degrees "
                + p.getRaCorrectedForNutationAnnualAberration()*units.RAD_TO_DEG
                + "  "
                + p.getDecCorrectedForNutationAnnualAberration()*units.RAD_TO_DEG);
            }
            output.close();
        }
        catch (IOException ioe) {
            console.errOut(ioe.toString());
            ioe.printStackTrace();
            System.err.println("Could not create " + filename);
        }
        System.out.println("\nend of testCoordinateCorrections");
    }
}

