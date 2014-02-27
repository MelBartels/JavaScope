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
 * main class that is a coordinate position; encompases hmsm and dms objects each representing a style of an axis value
 */
public class position {
    boolean available;
    boolean init;
    // position's name, ie, current, target...
    String posName;
    // object name, ie, M13 Herculus cluster
    String objName;
    String stringPosition;
    String stringCorrections;
    String stringDataFileFormat;
    String stringCometFileFormat;
    String stringDataFileWithAltazFormat;

    hmsm ra = new hmsm();
    hmsm ha = new hmsm();
    dms dec = new dms();
    dms alt = new dms();
    dms az = new dms();
    dms Ax3 = new dms();
    hmsm sidT = new hmsm();
    hmsm raDriftHr = new hmsm();
    dms decDriftHr = new dms();

    azDouble azErr = new azDouble();
    azDouble azWeightedErr = new azDouble();

    /**
     * a position's equatorial coordinates need an associated epoch, or date of the coordinates;
     * this date then is used to calculate precession, nutation, and annual aberration to arrive
     * at current date's apparent equatorial coordinates;
     * coordinates stored in files or databases with an epoch are expected to be the mean position,
     * that is, corrected for precession, but not corrected for nutation and annual aberration;
     * nutation and annual aberration dealt with in the move and tracking routines as corrections to
     * the targeted position;
     * the epoch can be in the form of either a (double) year, or a (double) Julian Date;
     * if in year form, then JD is calculated from the year;
     * sequence of reading in a datafile:
     *     position read in and precession correction applied,
     *     coordinate transfered via scope_cmd which uses the corrected position,
     *     position target then is corrected for nutation, annual aberration in tracking routine,
     *     target display will show nutation annual aberration but no precession;
     */
    double originalEpochJD;
    double epochJD;
    // if true, then epoch, preferably in year format, will be written out with coordinates
    boolean writeOutEpoch;
    boolean precessionCorrectionApplied;
    boolean nutationAnnualAberrationCorrectionsCalculated;
    precession precession = new precession();
    nutation nutation = new nutation();
    annualAberration annualAberration = new annualAberration();

    position() {
        this("");
    }

    position(String posName) {
        this.posName = new String(posName);
        // to avoid nulls later on if object's name never set
        objName = "";
        resetEpochs();
    }

    void objName(String objName) {
        this.objName = objName;
    }

    void resetEpochs() {
        originalEpochJD = epochJD = 0.;
        precessionCorrectionApplied = false;
    }

    // if originalEpochJD set, then correct precession to current date, otherwise, no correction
    // as coordinates w/o originalEpochJD set are assumed to be current mean coordinates;
    boolean applyPrecessionCorrection() {
        if (originalEpochJD > 0.) {
            return applyPrecessionCorrectionFromEpochJD(originalEpochJD);
        }
        return false;
    }

    boolean applyPrecessionCorrectionFromEpochYear(double fromYear) {
        return applyPrecessionCorrectionFromEpochJD(astroTime.getInstance().calcJDFromYear(fromYear));
    }

    boolean applyPrecessionCorrectionFromEpochJD(double originalEpochJD) {
        astroTime.getInstance().calcSidT();
        return applyPrecessionCorrectionFromEpochJDToEpochJD(originalEpochJD, astroTime.getInstance().JD);
    }

    boolean applyPrecessionCorrectionFromEpochYearToEpochYear(double fromYear, double toYear) {
        return applyPrecessionCorrectionFromEpochJDToEpochJD(astroTime.getInstance().calcJDFromYear(fromYear),
        astroTime.getInstance().calcJDFromYear(toYear));
    }

    boolean applyPrecessionCorrectionFromEpochJDToEpochJD(double originalEpochJD, double epochJD) {
        if (!precessionCorrectionApplied)
            return applyPrecessionCorrectionFromEpochJDToEpochJDSubr(originalEpochJD, epochJD);
        return false;
    }

    /**
     * position assumed to come in as mean positions, with precession corrected for the given epoch;
     */
    boolean applyPrecessionCorrectionFromEpochJDToEpochJDSubr(double originalEpochJD, double epochJD) {
        precession.calc(this, (epochJD-originalEpochJD)/365.25);
        ra.rad += precession.deltaRa;
        ra.rad = eMath.validRad(ra.rad);
        dec.rad += precession.deltaDec;
        precessionCorrectionApplied = true;
        return true;
    }

    // if no date given, assume current date
    boolean calcNutationAnnualAberrationCorrections() {
        astroTime.getInstance().calcSidT();
        return calcNutationAnnualAberrationCorrectionsForEpochJD(astroTime.getInstance().JD);
    }

    boolean calcNutationAnnualAberrationCorrectionsForEpochYear(double epochYear) {
        return calcNutationAnnualAberrationCorrectionsForEpochJD(astroTime.getInstance().calcJDFromYear(epochYear));
    }

    // only calc, do not actually apply corrections: this keeps coordinates as mean coordinates with
    // corrections onhand when desiring to apply them
    boolean calcNutationAnnualAberrationCorrectionsForEpochJD(double epochJD) {
        nutation.calcForJD(this, epochJD);
        annualAberration.calcForJD(this, epochJD);
        nutationAnnualAberrationCorrectionsCalculated = true;
        return true;
    }

    double getDecCorrectedForNutationAnnualAberration() {
        if (cfg.getInstance().precessionNutationAberration) {
            if (!nutationAnnualAberrationCorrectionsCalculated)
                calcNutationAnnualAberrationCorrections();
            return dec.rad + nutation.deltaDec + annualAberration.deltaDec;
        }
        else
            return dec.rad;
    }

    double getRaCorrectedForNutationAnnualAberration() {
        if (cfg.getInstance().precessionNutationAberration) {
            if (!nutationAnnualAberrationCorrectionsCalculated)
                calcNutationAnnualAberrationCorrections();
            return eMath.validRad(ra.rad + nutation.deltaRa + annualAberration.deltaRa);
        }
        else
            return ra.rad;
    }

    void copyDeep(position p) {
        copy(p);
        stringPosition = p.stringPosition;
        ra.copy(p.ra);
        ha.copy(p.ha);
        dec.copy(p.dec);
        alt.copy(p.alt);
        az.copy(p.az);
        Ax3.copy(p.Ax3);
        sidT.copy(p.sidT);
        raDriftHr.copy(p.raDriftHr);
        decDriftHr.copy(p.decDriftHr);
    }

    void copy(position p) {
        originalEpochJD = p.originalEpochJD;
        epochJD = p.epochJD;
        writeOutEpoch = p.writeOutEpoch;
        precessionCorrectionApplied = p.precessionCorrectionApplied;
        nutationAnnualAberrationCorrectionsCalculated = p.nutationAnnualAberrationCorrectionsCalculated;
        stringCorrections = p.stringCorrections;
        precession.copy(p.precession);
        nutation.copy(p.nutation);
        annualAberration.copy(p.annualAberration);

        init = p.init;
        // do not transfer position's name: posName = p.posName;
        objName = p.objName;
        ra.rad = p.ra.rad;
        ha.rad = p.ha.rad;
        dec.rad = p.dec.rad;
        alt.rad = p.alt.rad;
        az.rad = p.az.rad;
        Ax3.rad = p.Ax3.rad;
        sidT.rad = p.sidT.rad;
    }

    void setCoordDeg(double raDeg, double decDeg, double AltDeg, double AzDeg, double SidTDeg) {
        ra.rad = raDeg*units.DEG_TO_RAD;
        dec.rad = decDeg*units.DEG_TO_RAD;
        alt.rad = AltDeg*units.DEG_TO_RAD;
        az.rad = AzDeg*units.DEG_TO_RAD;
        sidT.rad = SidTDeg*units.DEG_TO_RAD;
    }

    // write out epoch only if flag says to do so
    String writeOutEpochWithCheck() {
        if (writeOutEpoch)
            return writeOutEpoch();
        return "";
    }

    // if no epoch present, then assume current epoch
    String writeOutEpoch() {
        if (epochJD > 0.)
            return eString.doubleToStringNoGrouping(epochJD, 7, 1) + " ";
        else
            return eString.doubleToStringNoGrouping(astroTime.getInstance().JD, 7, 1) + " ";
    }

    /*
     * all string position builders use stringPosition except for stringDataFileFormat, buildStringDataFileFormatRaw, and stringCometFileFormat;
     */

    String buildString() {
        stringPosition = writeOutEpochWithCheck()
        + "Ra:"
        + ra.getStringHMSM()
        + " Dec:"
        + dec.getStringDMS()
        + " Alt:"
        + alt.getStringDM()
        + " Az:"
        + az.getStringDM()
        + " SidTime:"
        + sidT.getStringHMSM();

        return stringPosition;
    }

    String buildStringDeg() {
        stringPosition = writeOutEpochWithCheck()
        + "Ra:"
        + ra.getStringDM()
        + " Dec:"
        + dec.getStringDM()
        + " Alt:"
        + alt.getStringDM()
        + " Az:"
        + az.getStringDM()
        + " SidTime:"
        + sidT.getStringDM();

        return stringPosition;
    }

    String stringPosName() {
        return eString.padString(posName, 12);
    }

    String stringObjName() {
        return eString.padString(objName, 12);
    }

    void showCoord() {
        console.stdOutLn(stringPosName()
        + ": "
        + buildString());
    }

    void showCoordDeg() {
        console.stdOutLn(stringPosName()
        + ": "
        + buildStringDeg());
    }

    String buildCoordDegRaw() {
        stringPosition = writeOutEpochWithCheck()
        + ra.rad*units.RAD_TO_DEG
        + " "
        + dec.rad*units.RAD_TO_DEG
        + " "
        + alt.rad*units.RAD_TO_DEG
        + " "
        + az.rad*units.RAD_TO_DEG
        + " "
        + sidT.rad*units.RAD_TO_DEG;
        return stringPosition;
    }

    String buildStringDataFileFormat() {
        stringDataFileFormat = writeOutEpochWithCheck()
        + "Ra: "
        + ra.getStringHMSM()
        + " Dec: "
        + dec.getStringDMS()
        + " "
        + objName;

        return stringDataFileFormat;
    }

    void showDataFileFormat() {
        console.stdOutLn(buildStringDataFileFormat());
    }

    String buildStringDataFileFormatRaw() {
        stringDataFileFormat = writeOutEpoch()
        + " "
        + ra.getStringHMSM(' ')
        + " "
        + dec.getStringDMS(' ')
        + " "
        + objName;

        return stringDataFileFormat;
    }

    void showDataFileFormatRaw() {
        console.stdOutLn(buildStringDataFileFormatRaw());
    }

    String buildStringDataFileWithAltazFormat() {
        stringDataFileWithAltazFormat = writeOutEpochWithCheck()
        + "Ra: "
        + ra.getStringHMSM()
        + " Dec: "
        + dec.getStringDMS()
        + " Alt: "
        + alt.rad*units.RAD_TO_DEG
        + " Az: "
        + az.rad*units.RAD_TO_DEG
        + " "
        + objName;

        return stringDataFileWithAltazFormat;
    }

    void showStringDataFileWithAltazFormat() {
        console.stdOutLn(buildStringDataFileWithAltazFormat());
    }

    String buildStringCometFileFormat() {
        stringCometFileFormat = writeOutEpochWithCheck()
        + "Ra: "
        + ra.getStringHMSM()
        + " Dec: "
        + dec.getStringDMS()
        + " RaDriftHr: "
        + raDriftHr.getStringHMSM()
        + " DecDriftHr: "
        + decDriftHr.getStringDMS()
        + " "
        + objName;

        return stringCometFileFormat;
    }

    void showCometFileFormat() {
        console.stdOutLn(stringCometFileFormat);
    }

    void buildStringCorrections() {
        if (precessionCorrectionApplied || nutationAnnualAberrationCorrectionsCalculated) {
            stringCorrections = posName
            + " corrections for epoch "
            + writeOutEpoch()
            + " total: Ra: "
            + eString.doubleToStringNoGrouping((precession.deltaRa+nutation.deltaRa+annualAberration.deltaRa)*units.RAD_TO_ARCSEC, 4, 1)
            + "\" Dec: "
            + eString.doubleToStringNoGrouping((precession.deltaDec+nutation.deltaDec+annualAberration.deltaDec)*units.RAD_TO_ARCSEC, 4, 1)
            + "\""
            + " precession: Ra: "
            + eString.doubleToStringNoGrouping(precession.deltaRa*units.RAD_TO_ARCSEC, 4, 1)
            + "\" Dec: "
            + eString.doubleToStringNoGrouping(precession.deltaDec*units.RAD_TO_ARCSEC, 4, 1)
            + "\""
            + " nutation: Ra: "
            + eString.doubleToStringNoGrouping(nutation.deltaRa*units.RAD_TO_ARCSEC, 4, 1)
            + "\" Dec: "
            + eString.doubleToStringNoGrouping(nutation.deltaDec*units.RAD_TO_ARCSEC, 4, 1)
            + "\""
            + " annualAberration: Ra: "
            + eString.doubleToStringNoGrouping(annualAberration.deltaRa*units.RAD_TO_ARCSEC, 4, 1)
            + "\" Dec: "
            + eString.doubleToStringNoGrouping(annualAberration.deltaDec*units.RAD_TO_ARCSEC, 4, 1)
            + "\"";
        }
        else
            stringCorrections = "no corrections available for " + posName;
    }

    String showStringCorrections() {
        buildStringCorrections();
        return stringCorrections;
    }
}

