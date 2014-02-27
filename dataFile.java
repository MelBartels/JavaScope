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
 * dataFile class is a file of objects with coordinates in dataFile format;
 * currentIx == -1 indicates that list of positions has not yet been started;
 */
public class dataFile {
    BufferedReader input;
    PrintStream output;
    String filename;
    long lastModified;
    listPosition lp;
    int count;
    // initial condition upon loading file is -1
    int currentIx;

    boolean loadFromFile(String filename) {
        lp = new listPosition();
        position p = new position("dataFile loadFromFile");

        this.filename = new String(filename);

        console.stdOutLn("dataFile:loadFromFile " + filename);

        count = 0;
        try {
            File f = new File(filename);
            if (f.exists() && f.isFile() && !f.isDirectory())
                lastModified = f.lastModified();

            input = new BufferedReader(new FileReader(filename));
            while (console.fReadLineEquatCoord(input, p)) {
                /*
                 * datafiles are precessed to J2000 mean coordinates;
                 * datafile coordinate entries do not contain an epoch, so after reading in positions,
                 * apply precession correction;
                 */
                if (cfg.getInstance().precessionNutationAberration)
                    p.applyPrecessionCorrectionFromEpochYear(cfg.getInstance().dataFileCoordYear);
                lp.add(p);
                count++;
            }
            input.close();
            currentIx = -1;
            return true;
        }
        catch (IOException ioe) {
            return false;
        }
    }

    boolean openFile(String filename) {
        lp = new listPosition();
        this.filename = new String(filename);
        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(filename)));
            output.close();
            console.stdOutLn("dataFile:openFile: " + filename);
            return true;
        }
        catch (IOException ioe) {
            console.errOut("could not open "
            + filename
            + " in dataFile.openFile()");
            return false;
        }
    }

    boolean writeEquatCoordToFile(String filename, position p, String reason) {
        try {
            // open for append
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(filename, true)));

            output.print(p.writeOutEpoch()
            + " "
            + p.ra.getStringHMS(eString.SPACE)
            + "   "
            + p.dec.getStringDMS(eString.SPACE)
            + "   ");

            output.println(p.objName
            + "   "
            + reason
            + "   "
            + astroTime.getInstance().buildStringCurrentDateTime());

            output.close();
            return true;
        }
        catch (IOException ioe) {
            console.errOut("could not open "
            + filename
            + " for append in writeEquatCoordToFile()");
            return false;
        }
    }

    /**
     * finds nearest object to matchPos from data file: matchPos is position to match against,
     * and nearestPos is the resulting nearest position;
     * currentIx set to point to nearest position
     */
    boolean findNearestDataFileObject(position matchPos, position nearestPos) {
        double sep;
        double minSep = Double.MAX_VALUE;
        int ix;
        position p;
        position nearestLP = null;

        if (lp == null || lp.size() < 1)
            return false;

        for (ix = 0; ix < lp.size(); ix++) {
            p = lp.get(ix);
            sep = Math.abs(celeCoordCalcs.calcEquatAngularSepViaRa(matchPos, p));
            if (sep < minSep) {
                minSep = sep;
                nearestLP = p;
                currentIx = ix;
            }
        }
        if (nearestLP != null)
            nearestPos.copy(nearestLP);

        if (minSep < Double.MAX_VALUE)
            return true;
        else
            return false;
    }

    /**
     * finds nearest object to matchPos from data file that does not have same coordinates as 'avoidPos':
     * matchPos is position to match against, avoidPos is position to avoID;
     * ll.current set to point to nearest position
     */
    boolean findNearestDataFileObjectAvoidPos(position matchPos, position nearestPos, position avoidPos) {
        double sep;
        double minSep = Double.MAX_VALUE;
        double threshholdSep = 2.*units.ARCSEC_TO_RAD;
        int ix;
        position p;
        position nearestLP = null;

        if (lp == null || lp.size() < 1)
            return false;

        for (ix = 0; ix < lp.size(); ix++) {
            p = lp.get(ix);
            sep = Math.abs(celeCoordCalcs.calcEquatAngularSepViaRa(matchPos, p));
            // handle slight loss of precision, ignoring differences below 1 arcsecond
            if (sep < minSep && Math.abs(celeCoordCalcs.calcEquatAngularSepViaRa(p, avoidPos)) > threshholdSep) {
                minSep = sep;
                nearestLP = p;
            }
        }

        if (nearestLP != null)
            nearestPos.copy(nearestLP);

        if (minSep < Double.MAX_VALUE)
            return true;
        else
            return false;
    }

    /**
     * object must match either name or first string token of pos.name;
     */
    boolean findObject(String object, position p) {
        String s;
        StringTokenizer st;
        int ix;
        position tempP;

        for (ix = 0; ix < lp.size(); ix++) {
            tempP = lp.get(ix);

            if (object.trim().equalsIgnoreCase(tempP.objName)) {
                p.copy(tempP);
                return true;
            }

            st = new StringTokenizer(tempP.objName);
            if (st.countTokens() > 0 && object.trim().equalsIgnoreCase(st.nextToken())) {
                p.copy(tempP);
                return true;
            }
        }
        return false;
    }

    /**
     * each substring of the search string 'object' must match with a substring in the listPos's name
     */
    boolean findAllObjects(String object, listPosition listPos) {
        String s;
        StringTokenizer searchTokens;
        StringTokenizer posObjNameTokens;
        int ix;
        position p;
        boolean objectFound = false;

        if (object.length() == 0)
            return false;
        
        for (ix = 0; ix < lp.size(); ix++) {
            p = lp.get(ix);
            objectFound = true;
            searchTokens = new StringTokenizer(object);
            while (searchTokens.countTokens() > 0 && objectFound) {
                s = searchTokens.nextToken();
                posObjNameTokens = new StringTokenizer(p.objName);
                while (posObjNameTokens.countTokens() > 0) {
                    objectFound = s.equalsIgnoreCase(posObjNameTokens.nextToken());
                    if (objectFound)
                        break;
                }
            }
            if (objectFound)
                listPos.add(p);
        }
        return objectFound;
    }
    
    void incrementCurrentIx() {
        currentIx++;
        if (currentIx == lp.size())
            currentIx = 0;
    }

    void decrementCurrentIx() {
        currentIx--;
        if (currentIx < 0)
            currentIx = lp.size()-1;
    }

    /**
     * wrap around to end if index < 0
     */
     
    void setCurrentIx(int index) {
        if (index < 0)
            currentIx = lp.size()-1;
        else {
            currentIx = index;
            if (currentIx >= lp.size())
                currentIx = lp.size()-1;
       }
    }
    
    void display() {
        Iterator it;
        position p;

        console.stdOutLn(filename + ": displaying list of positions in datafile format:");
        it = lp.iterator();
        while (it.hasNext()) {
            p = (position) it.next();
            p.showDataFileFormat();
        }
        console.stdOutLn("end of position list for " + filename);
    }

    void testNearestObject() {
        position matchPos = new position("matchPos");
        position nearestPos = new position("nearestPos");

        System.out.println("test to find nearest object in " + filename);
        System.out.print("ra (deg)?");
        console.getDouble();
        matchPos.ra.rad = console.d*units.DEG_TO_RAD;
        System.out.print("dec (deg)? ");
        console.getDouble();
        matchPos.dec.rad = console.d*units.DEG_TO_RAD;
        findNearestDataFileObject(matchPos, nearestPos);
        System.out.println("nearest position is: " + nearestPos.objName);
        nearestPos.showCoord();
        findNearestDataFileObjectAvoidPos(matchPos, nearestPos, matchPos);
        System.out.println("nearest position avoiding inputed position is: " + nearestPos.objName);
        nearestPos.showCoord();
    }

    void test() {
        dataFile Messier = new dataFile();
        Messier.loadFromFile("messier.dat");
        Messier.display();
        Messier.testNearestObject();

        listPosition listPos = new listPosition();
        String searchString = "M1";
        System.out.println("testing 'findAllObjects()' by searching on " + searchString);
        Messier.findAllObjects(searchString, listPos);

        System.out.println("found "
        + listPos.size()
        + " objects");

        listPos.display();
        System.out.println("end testing 'findAllObjects()'");
    }
}

