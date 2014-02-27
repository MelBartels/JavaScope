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
 * a list of lightweight positions from all dataFiles;
 * use singleton pattern and treat it as a database;
 */
public class dataFileListPositions {
    listLightweightPosition lp;
    java.util.List angsepList;

    private static dataFileListPositions INSTANCE;

    public static dataFileListPositions getInstance() {
        if (INSTANCE == null)
            synchronized(dataFileListPositions.class) {
                if (INSTANCE == null) {
                    INSTANCE = new dataFileListPositions();
                    INSTANCE.initialize();
                }
            }
        return INSTANCE;
    }

    private void initialize() {
        lp = new listLightweightPosition();
    }

    void loadFromDir(String dir) {
        int ix;
        fileFilter dataFileFilter = new fileFilter(eString.DAT_EXT);
        File files = new File(dir);
        File[] fileList;
        dataFile df = new dataFile();
        BufferedReader input;
        position p = new position();
        lightweightPosition lwp = new lightweightPosition();

        if (files != null) {
            fileList = files.listFiles(dataFileFilter);
            if (fileList != null)
                for (ix = 0; ix < fileList.length; ix++)
                    if (!fileList[ix].isDirectory()) {
                        console.stdOutLn("dataFileList adding " + fileList[ix].toString());
                        try {
                            input = new BufferedReader(new FileReader(fileList[ix].toString()));
                            while (console.fReadLineEquatCoord(input, p)) {
                                if (cfg.getInstance().precessionNutationAberration)
                                    p.applyPrecessionCorrectionFromEpochYear(cfg.getInstance().dataFileCoordYear);
                                listLightweightPosition.copyPositionToLwp(p, lwp, fileList[ix].toString());
                                lp.add(lwp);
                            }
                            input.close();
                            console.stdOutLn("dataFileList size " + lp.size());
                        }
                        catch (IOException ioe) {}
                    }
        }
    }

    /**
     * each substring of the search string 'object' must match with a substring in the listPos's name
     */
    boolean findAllObjects(String object, listPosition listPos) {
        String s;
        StringTokenizer searchTokens;
        StringTokenizer posObjNameTokens;
        int ix;
        lightweightPosition lwp;
        position p = new position();
        boolean objectFound = false;

        if (object.length() == 0)
            return false;
        
        for (ix = 0; ix < lp.size(); ix++) {
            lwp = lp.get(ix);

            objectFound = true;
            searchTokens = new StringTokenizer(object);
            while (searchTokens.countTokens() > 0 && objectFound) {
                s = searchTokens.nextToken();
                posObjNameTokens = new StringTokenizer(lwp.name);
                while (posObjNameTokens.countTokens() > 0) {
                    objectFound = s.equalsIgnoreCase(posObjNameTokens.nextToken());
                    if (objectFound)
                        break;
                }
            }
            if (objectFound) {
                listLightweightPosition.copyLwpToPosition(lwp, p);
                listPos.add(p);
            }
        }
        return objectFound;
    }

    /*
     * fill angsepList and listPos with closest objects from this class's lp;
     */
    boolean findNearestObjects(int objectCount, listPosition listPos) {
        return findNearestObjectsFromLwp(objectCount, listPos, lp);
    }

    boolean findNearestObjectsFromListPos(int objectCount, listPosition listPos, listPosition sourceListPos) {
        int ix;
        lightweightPosition lwp = new lightweightPosition();
        listLightweightPosition lp = new listLightweightPosition();

        for (ix = 0; ix < sourceListPos.size(); ix++) {
            position p = sourceListPos.get(ix);
            listLightweightPosition.copyPositionToLwp(p, lwp, "");
            lp.add(lwp);
        }
        return findNearestObjectsFromLwp(objectCount, listPos, lp);
    }

    private boolean findNearestObjectsFromLwp(int objectCount, listPosition listPos, listLightweightPosition lp) {
        int ix, ixB;
        lightweightPosition lwp;
        lightweightPosition current = new lightweightPosition();
        boolean objectFound = false;

        final int MAX_ANGSEP_SIZE = 20;
        double maxAngsepList = 0.;
        int angsepListIxOfMaxSep = 0;
        double sep;
        angsep as;
        angsepList = new ArrayList();

        // compare to current position
        listLightweightPosition.copyPositionToLwp(cfg.getInstance().current, current, "current");

        // step through lightweightPosition listArray
        for (ix = 0; ix < lp.size(); ix++) {
            lwp = lp.get(ix);

            sep = celeCoordCalcs.calcEquatAngularSepViaRaLwp(current, lwp);
            // if room in angsepList to add new entry...
            if (angsepList.size() <  MAX_ANGSEP_SIZE - 1) {
                as = new angsep();
                as.ix = ix;
                as.angsep = sep;
                angsepList.add(as);
                // if first object
                if (angsepList.size() == 1) {
                    objectFound = true;
                    angsepListIxOfMaxSep = 0;
                    maxAngsepList = sep;
                }
                // add regardless of separation since room remains in angsepList
                else
                    if (sep > maxAngsepList) {
                        angsepListIxOfMaxSep = angsepList.size() - 1;
                        maxAngsepList = sep;
                    }
            }
            else {
                // list full, so replace max separation with new smaller max separation
                if (sep < maxAngsepList) {
                    // replace
                    as = (angsep) angsepList.get(angsepListIxOfMaxSep);
                    as.ix = ix;
                    as.angsep = sep;
                    maxAngsepList = 0.;
                    // find new max separation entry in angsepList
                    for (ixB = 0; ixB < angsepList.size(); ixB++) {
                        as = (angsep) angsepList.get(ixB);
                        if (maxAngsepList < as.angsep) {
                            maxAngsepList = as.angsep;
                            angsepListIxOfMaxSep = ixB;
                        }
                    }
                }
            }
        }
        // search completed: sort
        Collections.sort(angsepList, new angsepByAngsep());
        // fill results listPos;
        // index in listPos will match index in angsep;
        for (ixB = 0; ixB < angsepList.size(); ixB++) {
            as = (angsep) angsepList.get(ixB);
            lwp = (lightweightPosition) lp.get(as.ix);
            position p = new position();
            listLightweightPosition.copyLwpToPosition(lwp, p);
            listPos.add(p);
        }
        //displayNearestObjects(listPos);
        return objectFound;
    }

    String buildNearestObjectDisplayLine(listPosition lp, int ix) {
        String s;

        position p = lp.get(ix);
        s = "distance deg "
        + eString.doubleToStringNoGrouping(((angsep) dataFileListPositions.getInstance().angsepList.get(ix)).angsep*units.RAD_TO_DEG, 3, 1)
        + " "
        + p.buildStringDataFileFormat();

        return s;
    }

    void displayNearestObjects(listPosition lp) {
        int ix;

        for (ix = 0; ix < lp.size(); ix++)
            System.out.println(buildNearestObjectDisplayLine(lp, ix));
    }

    void writeLibraryFile() {
        int ix;
        PrintStream output;
        lightweightPosition lwp;
        position p = new position();

        lp.sortByName();

        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(eString.LIBRARY_FILE_NAME)));
            System.out.println("building library datafile");
            for (ix = 0; ix < lp.size(); ix++) {
                lwp = lp.get(ix);
                listLightweightPosition.copyLwpToPosition(lwp, p);
                output.println(p.buildStringDataFileFormatRaw());
            }
            output.close();
            System.out.println("finished writing library datafile");
        }
        catch (IOException ioe) {
            console.errOut("could not open " + eString.LIBRARY_FILE_NAME);
        }
    }
}

