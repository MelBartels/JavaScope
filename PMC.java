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
 * pointing model corrections class
 */
public class PMC {
    private PrintStream output;
    private BufferedReader input;

    private double maxPointErr;
    private double pointErrRMS;

    String PMCFile;

    boolean active;
    boolean loadPMCSuccessful;
    azDouble correction = new azDouble();
    java.util.List lpe = new ArrayList();

    PMC() {
        PMCFile = eString.PGM_NAME + eString.PMC_EXT;
        loadListPositionErrFromPMCFile();
    }

    /**
     * will create file if necessary
     */
    void emptyPMCFile() {
        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(PMCFile)));
            output.close();
        }
        catch (IOException ioe) {
            console.errOut(ioe.toString());
            ioe.printStackTrace();
            System.err.println("could not open " + PMCFile);
        }
    }

    void appendListPositionToPMCFile(listPosition lp) {
        String s;
        Iterator it;
        position p;

        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(PMCFile, true)));

            it = lp.iterator();
            while (it.hasNext()) {
                p = (position) it.next();
                s = eString.doubleToStringNoGrouping(p.alt.rad*units.RAD_TO_DEG, 3, 3)
                + " "
                + eString.doubleToStringNoGrouping(p.az.rad*units.RAD_TO_DEG, 3, 3)
                + " "
                + eString.doubleToStringNoGrouping(p.azErr.a*units.RAD_TO_DEG, 3, 3)
                + " "
                + eString.doubleToStringNoGrouping(p.azErr.z*units.RAD_TO_DEG, 3, 3);
                output.println(s);
            }
            output.close();
        }
        catch (IOException ioe) {
            console.errOut(ioe.toString());
            ioe.printStackTrace();
            System.err.println("could not open "
            + PMCFile
            + " for append");
        }
    }

    private void addListPositionToListPosErr(listPosition lp) {
        String s;
        Iterator it;
        position p;

        it = lp.iterator();
        while (it.hasNext()) {
            p = (position) it.next();
            lpe.add( new posErr());
            posErr pe = (posErr) lpe.get(lpe.size()-1);
            pe.az.a = p.alt.rad;
            pe.az.z = p.az.rad;
            pe.azErr.a = p.azErr.a;
            pe.azErr.z = p.azErr.z;
        }
    }

    boolean loadListPositionErrFromPMCFile() {
        azDouble az = new azDouble();
        azDouble azErr = new azDouble();
        StringTokenizer st;
        String s;

        try {
            input = new BufferedReader(new FileReader(PMCFile));
            console.stdOutLn("found " + PMCFile);
            do {
                try {
                    s = input.readLine();
                    if (s != null) {
                        st = new StringTokenizer(s);
                        if (st.countTokens() == 4) {
                            // nextToken() ignores leading and trailing white space; x.parseX() converts string to type x
                            az.a = Double.parseDouble(st.nextToken());
                            az.z = Double.parseDouble(st.nextToken());
                            azErr.a = Double.parseDouble(st.nextToken());
                            azErr.z = Double.parseDouble(st.nextToken());
                            lpe.add( new posErr());
                            posErr pe = (posErr) lpe.get(lpe.size()-1);
                            pe.az.a = az.a * units.DEG_TO_RAD;
                            pe.az.z = az.z * units.DEG_TO_RAD;
                            pe.azErr.a = azErr.a * units.DEG_TO_RAD;
                            pe.azErr.z = azErr.z * units.DEG_TO_RAD;
                        }
                    }
                }
                catch (IOException ioe) {
                    loadPMCSuccessful = false;
                    return false;
                }
            }while (s != null);
            input.close();
            loadPMCSuccessful = true;
            return true;
        }
        catch (IOException ioe) {
            console.stdOutLn("did not find " + PMCFile);
            loadPMCSuccessful = false;
            return false;
        }
    }

    private void calcPointErrRMSMaxPointErr() {
        Iterator it;
        double pointErr = 0.;
        double pointErrTot = 0.;

        maxPointErr = 0.;

        it = lpe.iterator();
        while (it.hasNext()) {
            posErr pe = (posErr) it.next();
            // azimuth errors in terms of true field decrease towards the zenith
            pointErr = Math.sqrt(pe.azErr.a * pe.azErr.a + pe.azErr.z * pe.azErr.z);
            pointErrTot += pointErr;
            if (pointErr > maxPointErr)
                maxPointErr = pointErr;
        }
        if (lpe.size() > 0)
            pointErrRMS = pointErrTot / lpe.size();
        else
            pointErrRMS = 0.;
    }

    void calcCorrection(double a, double z) {
        double sinA, cosA;
        double angsep;
        double totalAngsep;
        double PMCinverseAngularSeparationFactor;
        azDouble azWeightedErr = new azDouble();
        Iterator it;

        totalAngsep = 0.;
        azWeightedErr.a = azWeightedErr.z = 0.;
        sinA = Math.sin(a);
        cosA = Math.cos(a);
        it = lpe.iterator();
        while (it.hasNext()) {
            posErr pe = (posErr) it.next();
            angsep = Math.acos(sinA * Math.sin(pe.az.a) + cosA * Math.cos(pe.az.a) * Math.cos(z - pe.az.z));
            PMCinverseAngularSeparationFactor = 1./(angsep*angsep*angsep);
            totalAngsep += PMCinverseAngularSeparationFactor;
            azWeightedErr.a += pe.azErr.a * PMCinverseAngularSeparationFactor;
            azWeightedErr.z += pe.azErr.z * PMCinverseAngularSeparationFactor;
        }
        if (totalAngsep > 0.) {
            // positive values mean scope pointed too far CW
            correction.a = azWeightedErr.a / totalAngsep;
            correction.z = azWeightedErr.z / totalAngsep;
        }
        else {
            correction.a = 0.;
            correction.z = 0.;
        }
    }

    void displayListPositionErr() {
        Iterator it;

        it = lpe.iterator();
        while (it.hasNext()) {
            posErr pe = (posErr) it.next();
            console.stdOutLn(eString.doubleToStringNoGrouping(pe.az.a * units.RAD_TO_DEG, 3, 3)
            + " "
            + eString.doubleToStringNoGrouping(pe.az.z * units.RAD_TO_DEG, 3, 3)
            + " "
            + eString.doubleToStringNoGrouping(pe.azErr.a * units.RAD_TO_DEG, 3, 3)
            + " "
            + eString.doubleToStringNoGrouping(pe.azErr.z * units.RAD_TO_DEG, 3, 3));
        }
    }
}

