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
 * arrays hold values that show scope movement errors in arcseconds with PEC rotation;
 * positive values indicate excess CW movement;
 * PEC can be applied to a single direction of motion;
 * each PEC cycle is divided into PECSize units and covers motorStepsPerPECArray motor steps (motor steps per PEC
 *    index can be fractional);
 * typically PEC covers number of steps per one complete motor revolution since greatest PEC contributor is the worm
 *    directly in front of the motor; for double or quad worms, set motorStepsPerPECArray to cover 2x or 4x worm rotation;
 *    for gearing downstream, set motorStepsPerPECArray to total number of motor counts that completes one revolution of
 *    the gearing in question;
 * PEC direction checked and compared to PECRotation in setPECIxPECToAddFromServoActualPosition();
 * PEC read in and built when configuration file read in;
 * this PEC's PECActive set true if this PEC is built, PECActive for the motor set from configuration file;
 * backlash ignored as it typically is less than 1/200 of rotation, thus less than a single PECIx (if it is not, PECSize
 *    can be reduced or PEC for each direction could be generated);
 * if backlash taken into account, need to consider:
 *    - as backlash taken up when direction reversed, PEC should not change from original direction's last PEC value
 *    - backlash awareness of the PEC synch point
 *    - varying backlash for each PEC
 *    - takeup of backlash of downstream PEC can only begin when backlash of immediately upstream PEC has been taken up
 */
public class PEC {
    private BufferedReader input;
    private PrintStream output;
    public String PECDataFilename;
    String PECFilename;
    private String servoIDStr;
    String descriptStr;
    // servoIDStr + descriptStr + "PEC"
    String PECFilenameSubStr;
    int PECSize;
    private int PECFilenameCount;
    private int ix;
    private double arcsec;
    // motor steps per one complete PEC revolution
    double motorStepsPerPECArray;
    // motor steps per PEC index
    double motorStepsPerPECIx;
    // index to PEC array
    int PECIx;
    int lastPECIx;
    int holdPECIx;
    // based on PECDir, the next PECIx
    private int nextPECIx;
    // index offset between motor position of zero and synch point - can be fractional
    double PECIxOffset;
    // preserve last PECIxOffset to compare with PECIxOffset in order to synch point changes
    double lastPECIxOffset;
    // direction of motion where PEC is to be applied
    ROTATION PECRotation;
    // PEC to adjust current coordinates by in arcsecond
    double PECToAddArcsec;
    // fractional value of PECIx, ie, if motor position says PEC index is 1.6, then PECIx is 1 and fractionalPECIx is .6
    private double fractionalPECIx;
    // direction of movement: if CW then PECIx is increasing, else decreasing
    private ROTATION PECDir;
    // array of PEC
    private double[] PECs;
    // temp work array of PEC used in various functions
    private double[] p;
    // string of PECIx and PECToAddArcsec
    String stringPEC;
    boolean PECSyncReady;
    boolean PECActive;
    
    PEC(String servoIDStr, String descriptStr, double motorStepsPerPECArray, int PECSize, ROTATION PECRotation, double PECIxOffset) {
        this.servoIDStr = new String(servoIDStr);
        this.descriptStr = new String(descriptStr);
        PECFilenameSubStr = new String(servoIDStr
                + descriptStr
                + "PEC");
        PECDataFilename = new String(PECFilenameSubStr + eString.DATA_EXT);
        this.motorStepsPerPECArray = motorStepsPerPECArray;
        this.PECSize = PECSize;
        PECs = new double[PECSize];
        p = new double[PECSize];
        this.PECIxOffset = PECIxOffset;
        this.PECRotation = PECRotation;
        
        initPEC();
    }
    
    private void initPEC() {
        PECFilenameCount = 0;
        motorStepsPerPECIx = motorStepsPerPECArray / (double) PECSize;
        zeroPEC();
        loadPEC();
        lastPECIxOffset = 0.;
        PECSyncReady = false;
        PECActive = true;
    }
    
    void zeroPEC() {
        for (ix = 0; ix < PECSize; ix++)
            PECs[ix] = 0.;
    }
    
    void loadPEC() {
        int ix = 0;
        double arcsec = 0.;
        String s;
        StringTokenizer st;
        
        try {
            input = new BufferedReader(new FileReader(PECDataFilename));
            console.stdOutLn("Found " + PECDataFilename);
            s = input.readLine();
            while (s != null) {
                st = new StringTokenizer(s);
                if (st.countTokens() == 2) {
                    try {
                        ix = Integer.parseInt(st.nextToken());
                    } catch (NumberFormatException nfe) {
                        console.errOut("bad number for ix in " + PECDataFilename);
                    }
                    try {
                        arcsec = Double.parseDouble(st.nextToken());
                    } catch (NumberFormatException nfe) {
                        console.errOut("bad number for arcsec in " + PECDataFilename);
                    }
                    if (ix < 0 || ix >= PECSize)
                        common.badExit("ix of "
                                + ix
                                + " is out of bounds in loadPEC()");
                    PECs[ix] = arcsec;
                }
                s = input.readLine();
            }
            input.close();
        } catch (IOException ioe) {
            console.errOut("Did not find "
                    + PECDataFilename
                    + ", using flat PEC table");
        }
    }
    
    void savePEC(String filename) {
        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(filename)));
            
            for (PECIx = 0; PECIx < PECSize; PECIx++)
                output.println(PECIx
                        + "   "
                        + PECs[PECIx]);
            
            output.close();
        } catch (IOException ioe) {
            console.errOut(ioe.toString());
            ioe.printStackTrace();
            System.err.println("could not open " + filename);
        }
    }
    
    /**
     * this function sets an index offset so that the PEC ix is matched to current motor angle - can be fractional
     */
    void setPECIxOffset(long servoMotorActualPosition) {
        PECIxOffset = ((double) servoMotorActualPosition % motorStepsPerPECArray) / motorStepsPerPECIx;
    }
    
    /**
     * this function sets the PEC array indexes based on motor angle or position; ignores backlash
     */
    void setPECIxPECToAddFromServoActualPosition(long servoMotorActualPosition) {
        double doublePECIx;
        
        // get integer and fractional PECIx based on motor position
        doublePECIx = ((double) servoMotorActualPosition%motorStepsPerPECArray) / motorStepsPerPECIx - PECIxOffset;
        doublePECIx = boundsPECSize(doublePECIx);
        PECIx = (int) doublePECIx;
        fractionalPECIx = doublePECIx - (double) PECIx;
        
        /**
         * get PEC direction - necessary because PEC curve not necessarily symmetric about the PECIx;
         * can skip count of PECIx up to 1/2 PECSize between calls to this function (after slew or rePositioning,
         * direction and consequent calculation of PECToAddArcsec may be in error [likely small error] until PECIx
         * changes during tracking)
         */
        if (PECIx - lastPECIx > 0)
            if (PECIx-lastPECIx > PECSize/2)
                
                PECDir = ROTATION.CCW;
            else
                PECDir = ROTATION.CW;
        else
            if (lastPECIx-PECIx > PECSize/2)
                PECDir = ROTATION.CW;
            else
                PECDir = ROTATION.CCW;
        
        // set nextPECIx based on direction
        if (PECDir == ROTATION.CW) {
            nextPECIx = PECIx + 1;
            if (nextPECIx >= PECSize)
                nextPECIx = 0;
        } else {
            nextPECIx = PECIx - 1;
            if (nextPECIx < 0)
                nextPECIx = PECSize - 1;
            // if going CCW, then invert fractional portion
            fractionalPECIx = 1 - fractionalPECIx;
        }
        
        // calculate PEC to add in arcseconds and set lastPECIx;
        // check PEC direction, if not valid, then set PEC to add to zero
        if (PECRotation == ROTATION.biDir || PECDir == PECRotation)
            PECToAddArcsec = PECs[PECIx] + fractionalPECIx * (PECs[nextPECIx] - PECs[PECIx]);
        else
            PECToAddArcsec = 0.;
        lastPECIx = PECIx;
    }
    
    private double boundsPECSize(double value) {
        while (value < 0.)
            value += PECSize;
        while (value >= PECSize)
            value -= PECSize;
        return value;
    }
    
    boolean averagePECAnalysisFiles() {
        int filenum;
        double arcsec = 0.;
        String s;
        StringTokenizer st;
        int ix;
        String filename;
        int filesRead = 0;
        
        for (ix = 0; ix < PECSize; ix++)
            p[ix] = 0.;
        
        for (filenum = 0; filenum <= 99; filenum++) {
            filename = PECFilenameSubStr
                    + eString.intToString(filenum, 2)
                    + eString.PEC_EXT;
            try {
                input = new BufferedReader(new FileReader(filename));
                console.stdOutLn("Using " + filename);
                s = input.readLine();
                while (s != null) {
                    st = new StringTokenizer(s);
                    if (st.countTokens() == 2) {
                        try {
                            ix = Integer.parseInt(st.nextToken());
                        } catch (NumberFormatException nfe) {
                            console.errOut("bad number for ix in " + filename);
                        }
                        try {
                            arcsec = Double.parseDouble(st.nextToken());
                        } catch (NumberFormatException nfe) {
                            console.errOut("bad number for arcsec in " + filename);
                        }
                        if (ix < 0 || ix >= PECSize)
                            common.badExit("ix of "
                                    + ix
                                    + " is out of bounds in averagePECAnalysisFiles()");
                        // add to accumulating total of the fileset
                        p[ix] += arcsec;
                    }
                    s = input.readLine();
                }
                filesRead++;
                input.close();
            } catch (IOException ioe) {}
        }
        if (filesRead > 0) {
            console.stdOutLn("Averaging "
                    + filesRead
                    + " files");
            for (ix = 0; ix < PECSize; ix++)
                p[ix] /= filesRead;
            console.stdOutLn("Adding averaged files to PEC");
            // positive PEC indicates excess CW movement; if scope is moving clockwise, current. must be increased by adding PEC
            // so that (target current. - corrected current.) is a smaller value resulting in less clockwise motion;
            // positive p or guide values mean the same so add p to PEC array
            for (ix = 0; ix < PECSize; ix++)
                PECs[ix] += p[ix];
            console.stdOutLn("Saving PEC to " + PECDataFilename);
            savePEC(PECDataFilename);
            return true;
        } else
            return false;
    }
    
    void smoothPEC() {
        int ixA, ixB;
        int sortIx, sortIxB, sortIxC;
        double tot, avg;
        
        int num = 20;
        
        // round up to odd number
        if (num%2 == 0)
            num++;
        
        // num size + 1 for swap
        double[] SortArray = new double[num+1];
        // fill array
        for (ix = 0; ix < PECSize; ix++)
            p[ix] = PECs[ix];
        // ix will be the base index to work from
        for (ix = 0; ix < PECSize; ix++) {
            tot = 0.;
            sortIx = num/2;
            // so that SortArray mirrors PEC values with middle of SortArray being the base 'ix' value of the PEC array:
            // start with middle of sort array which starts at ix
            for (ixA = 0; ixA <= num/2; ixA++) {
                ixB = ix + ixA;
                if (ixB >= PECSize)
                    ixB -= PECSize;
                tot += p[ixB];
                SortArray[sortIx++] = p[ixB];
            }
            // now do beginning of sort array which starts at ix - num/2
            sortIx = 0;
            for (ixA = -num/2; ixA < 0; ixA++) {
                ixB = ix + ixA;
                if (ixB < 0)
                    ixB += PECSize;
                tot += p[ixB];
                SortArray[sortIx++] = p[ixB];
            }
            // bubble sort SortArray
            for (sortIxC = num - 1; sortIxC > 0; sortIxC--)
                for (sortIxB = 1; sortIxB <= sortIxC; sortIxB++) {
                sortIx = sortIxB - 1;
                if (SortArray[sortIx] > SortArray[sortIxB]) {
                    SortArray[num] = SortArray[sortIxB];
                    SortArray[sortIxB] = SortArray[sortIx];
                    SortArray[sortIx] = SortArray[num];
                }
                }
            avg = tot / num;
            // update PEC array with new value
            PECs[ix] = avg;
            // for pure median smoothing use this:
            // PECs[ix] = SortArray[num/2];
        }
        console.stdOutLn("after smoothPEC, saving PEC to " + PECDataFilename);
        savePEC(PECDataFilename);
    }
    
    String buildPECString() {
        stringPEC =  "\nPEC for "
                + PECFilenameSubStr
                + " is "
                + (PECActive?"ON ":"off")
                + "\n"
                + "   motorStepsPerPECArray="
                + motorStepsPerPECArray
                + " PECSize="
                + PECSize
                + " PECRotation="
                + PECRotation
                + "\n"
                + "   PECIndex="
                + PECIx
                + " PECValue="
                + eString.doubleToStringNoGrouping(PECToAddArcsec, 3, 1)
                + " arcsec"
                + " PECIxOffset="
                + PECIxOffset
                + " lastPECIxOffset="
                + lastPECIxOffset
                + "\n";
        
        return stringPEC;
    }
}

