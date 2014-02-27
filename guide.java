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
 * each PEC gets its own guide object
 */
public class guide extends PEC {
    private BufferedReader input;
    private PrintStream output;
    private String guideFilename;
    int guidingCycles;
    private int MAX_GUIDE_IX;
    // if this guide object is active
    boolean guideActive;
    GUIDE_ARRAY_STATE guideArrayState;
    private int guideIx;
    private guideArray[] guides;
    private PECwork[] p;
    // direction of motion when writing PECIx's to guides[]
    private ROTATION guideDir;
    private int PECIxCrossZero;
    private int startGuidesIxCrossZeroPECIx;
    private int endGuidesIxCrossZeroPECIx;
    private int reps;

    guide(guideParms gp) {
        // invokes PEC constructor with different parameter list than guide
        super(gp.servoIDStr, gp.descriptStr, gp.motorStepsPerPECArray, gp.PECSize, gp.PECRotation, gp.PECIxOffset);

        int ix;

        guideFilename = new String(PECFilenameSubStr + eString.GUIDE_EXT);
        guidingCycles = gp.guidingCycles;
        MAX_GUIDE_IX = gp.PECSize * gp.guidingCycles + 1;

        guides = new guideArray[MAX_GUIDE_IX];
        for (ix = 0; ix < MAX_GUIDE_IX; ix++)
            guides[ix] = new guideArray();

        p = new PECwork[gp.PECSize];
        for (ix = 0; ix < gp.PECSize; ix++)
            p[ix] = new PECwork();

        guideActive = true;
        guideArrayState = GUIDE_ARRAY_STATE.off;
    }

    void initGuide() {
        int ix;

        guideIx = 0;
        guideDir = ROTATION.no;
        PECIxCrossZero = 0;
        reps = 0;
        for (ix = 0; ix < MAX_GUIDE_IX; ix++) {
            guides[ix].PECIx = 0;
            guides[ix].arcsec = 0.;
        }
    }

    void saveGuide() {
        int ix;

        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(guideFilename)));

            for (ix = 0; ix < guideIx; ix++)
                output.println(guides[ix].PECIx
                + "   "
                + guides[ix].arcsec);

            output.close();
        }
        catch (IOException ioe) {
            console.errOut(ioe.toString());
            ioe.printStackTrace();
            System.err.println("could not open " + guideFilename);
        }
    }

    /**
     * each entry in the guide array contains the PEC index and accumulated guiding corrections;
     * it is possible for successive entries in the guide array to skip over PEC indexes since the motor
     * may have rotated more than the angular distance between PEC indexes
     */
    void writeToGuideArray(double accumGuideRad) {
        int ix;

        // average in the new entry with the old value
        if (PECIx == holdPECIx) {
            reps++;
            guides[guideIx].arcsec = (guides[guideIx].arcsec * reps + accumGuideRad*units.RAD_TO_ARCSEC) / (reps+1);
        }
        // write new entry in the guide array
        else {
            // make sound when PECIx goes through zero
            if ((holdPECIx-PECIx > PECSize/2 && PECIx < holdPECIx) || (PECIx-holdPECIx > PECSize/2 && PECIx > holdPECIx))
                common.beep(2, 60);

            // write an entry in the guide array
            guides[guideIx].PECIx = PECIx;
            guides[guideIx].arcsec = accumGuideRad*units.RAD_TO_ARCSEC;
            holdPECIx = PECIx;
            reps = 0;
            guideIx++;
        }
        if (guideIx >= MAX_GUIDE_IX) {
            guideArrayState = GUIDE_ARRAY_STATE.readyToSave;
            common.beep(3, 60);
        }
    }

    boolean loadGuide() {
        int ix = 0;
        double arcsec = 0.;
        String s;
        StringTokenizer st;

        try {
            input = new BufferedReader(new FileReader(guideFilename));
            s = input.readLine();
            while (s != null) {
                st = new StringTokenizer(s);
                if (st.countTokens() == 2) {
                    try {
                        ix = Integer.parseInt(st.nextToken());
                    }
                    catch (NumberFormatException nfe) {
                        console.errOut("bad number for ix in " + guideFilename);
                    }
                    try {
                        arcsec = Double.parseDouble(st.nextToken());
                    }
                    catch (NumberFormatException nfe) {
                        console.errOut("bad number for arcsec in " + guideFilename);
                    }
                    guides[guideIx].PECIx = ix;
                    guides[guideIx].arcsec = arcsec;
                    guideIx++;
                }
                s = input.readLine();
            }
            input.close();
            return true;
        }
        catch (IOException ioe) {
            console.errOut("could not open " + guideFilename);
            return false;
        }
    }

    /**
     * analyze from a file, not from what happens to be in memory as a stopGuide()/initGuide sequence can
     * reset the array pointers; consequently call loadGuide() first;
     * creates guide analysis files;
     */
    void analyzeGuideArray() {
        int cwcount = 0;
        int ccwcount = 0;
        int lastGuideIx;
        int ix, ixA, ixB;
        double drift;
        double offset;
        int fileCount;

        // determine direction of motion while guides was recorded
        ix = 1;
        while (ix < MAX_GUIDE_IX && Math.abs(cwcount - ccwcount) < 3) {
            if (guides[ix+1].PECIx > guides[ix].PECIx)
                cwcount++;
            else
                ccwcount++;
            ix++;
        }
        if (cwcount > ccwcount)
            guideDir = ROTATION.CW;
        else
            guideDir = ROTATION.CCW;

        PECIxCrossZero = 0;
        // run through guides array and preserve guideIx since it is the marker for the end of valid guides array values
        lastGuideIx = guideIx;
        for (ixB = 1; ixB < lastGuideIx; ixB++)
            // check for PECIx of 0 crossing, ie, 198,199,0,1 or 1,0,199,198 but not 80,81,80,81
            if ((guideDir == ROTATION.CW && guides[ixB-1].PECIx > guides[ixB].PECIx + PECSize/2)
            || (guideDir == ROTATION.CCW && guides[ixB-1].PECIx < guides[ixB].PECIx - PECSize/2)) {
                PECIxCrossZero++;
                if (PECIxCrossZero == 1)
                    startGuidesIxCrossZeroPECIx = endGuidesIxCrossZeroPECIx = ixB;
                else
                    startGuidesIxCrossZeroPECIx = endGuidesIxCrossZeroPECIx;
                if (PECIxCrossZero > 1) {
                    endGuidesIxCrossZeroPECIx = ixB;

                    // zero out working array
                    for (ix = 0; ix < PECSize; ix++) {
                        p[ix].arcsec = 0.;
                        p[ix].entry = false;
                    }

                    guideIx = startGuidesIxCrossZeroPECIx;
                    // fill working PEC array with values from guides[]
                    while (guideIx != endGuidesIxCrossZeroPECIx) {
                        // guideIx is index into guides array (the record of guiding corrections,
                        //    where the PEC index and accumulated guiding amount is stored);
                        // if motor moving CCW, then PEC index starts at max and decreases as guides array is
                        //    stepped through ascending from startGuidesIxCrossZeroPECIx. to endGuidesIxCrossZeroPECIx.
                        ix = guides[guideIx].PECIx;
                        p[ix].arcsec = guides[guideIx].arcsec;
                        p[ix].entry = true;
                        guideIx++;
                    }

                    // fill in missing values: if value is unentered, then use value from previous entry
                    if (!p[0].entry)
                        p[0].arcsec = 0.;
                    for (ix = 1; ix < PECSize; ix++)
                        if (!p[ix].entry)
                            p[ix].arcsec = p[ix-1].arcsec;

                    // compensate for drift
                    drift = p[PECSize-1].arcsec - p[0].arcsec;
                    for (ix = 0; ix < PECSize; ix++)
                        p[ix].arcsec -= ((double) ix / (double) PECSize) * drift;

                    // eliminate beginning offset
                    offset = p[0].arcsec;
                    for (ix = 0; ix < PECSize; ix++)
                        p[ix].arcsec -= offset;

                    // build filename
                    fileCount = findNextIncrFilename(PECFilenameSubStr, eString.PEC_EXT);
                    PECFilename = PECFilenameSubStr
                    + eString.intToString(fileCount, 2)
                    + eString.PEC_EXT;
                    console.stdOutLn("analyzeGuideArray(): saving guiding for PEC analysis to " + PECFilename);

                    // write data to file
                    try {
                        output = new PrintStream(new BufferedOutputStream(new FileOutputStream(PECFilename)));
                        for (ix = 0; ix < PECSize; ix++)
                            output.println(ix
                            + "   "
                            + p[ix].arcsec);
                        output.close();
                    }
                    catch (IOException ioe) {
                        console.errOut(ioe.toString());
                        ioe.printStackTrace();
                        System.err.println("could not open " + PECFilename);
                    }
                }
            }
        // restore guideIx since it marks the end of valid guides values
        guideIx = lastGuideIx;
    }

    private int findNextIncrFilename(String nameSubStr, String ext) {
        int onesDigit = 0;
        int tensDigit = 0;
        boolean fileFound = true;
        boolean slotAvail = true;
        String matchFilename;
        File f = null;
        String[] s;

        while (fileFound && slotAvail) {
            matchFilename = nameSubStr
            + eString.intToString(tensDigit, 1)
            + eString.intToString(onesDigit, 1)
            + ext;
            f = new File(matchFilename);
            if (f.exists()) {
                fileFound = true;
                if (onesDigit == 9)
                    if (tensDigit == 9)
                        slotAvail = false;
                    else {
                        tensDigit++;
                        onesDigit = 0;
                    }
                else
                    onesDigit++;
            }
            else {
                fileFound = false;
            }
        }
        return tensDigit*10 + onesDigit;
    }
}

