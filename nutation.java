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
 * from Jean Meeus' Astronomical Formulae for Calculators second edition, 1982, pg 69-70, revised per later editions;
 * nutation is variation or fluctuation in rate of precession;
 * caused by difference between ecliptic and earth-moon plane, the latter which precesses 18.6 years;
 * thus, plane of the moon's orbit and the effect of the moon's pull on the earth varies, causing
 * fluctuations in the rate of precession;
 */
public class nutation {
    double preRa;
    double preDec;
    double deltaRa;
    double deltaDec;

    void copy(nutation n) {
        preRa = n.preRa;
        preDec = n.preDec;
        deltaRa = n.deltaRa;
        deltaDec = n.deltaDec;
    }

    // if no JD passed in, assume current epoch
    void calc(position p) {
        astroTime.getInstance().calcSidT();
        calcForJD(p, astroTime.getInstance().JD);
    }

    void calcForYear(position p, double year) {
        calcForJD(p, astroTime.getInstance().calcJDFromYear(year));
    }

    void calcForJD(position p, double JD) {
        nutationSubr(p, JD);
    }

    void nutationSubr(position p, double JD) {
        celeCoordCalcs.calcVars(JD);
        deltaEquatCoord(p);
    }

    void deltaEquatCoord(position p) {
        preRa = p.ra.rad;
        preDec = p.dec.rad;

        deltaRa = (Math.cos(celeCoordCalcs.obliquityEcliptic)+Math.sin(celeCoordCalcs.obliquityEcliptic)
        *Math.sin(preRa)*Math.tan(preDec))
        *celeCoordCalcs.nutationLongitude
        -(Math.cos(preRa)*Math.tan(preDec))*celeCoordCalcs.nutationObliquity;
        deltaRa *= units.ARCSEC_TO_RAD;

        deltaDec = Math.sin(celeCoordCalcs.obliquityEcliptic)*Math.cos(preRa)*celeCoordCalcs.nutationLongitude
        +Math.sin(preRa)*celeCoordCalcs.nutationObliquity;
        deltaDec *= units.ARCSEC_TO_RAD;
    }

    static void displayVars() {
        System.out.println("t "
        + celeCoordCalcs.t
        + " sunMeanLongitude "
        + celeCoordCalcs.sunMeanLongitude*units.RAD_TO_DEG
        + " moonMeanLongitude "
        + celeCoordCalcs.moonMeanLongitude*units.RAD_TO_DEG
        + " sunMeanAnomaly "
        + celeCoordCalcs.sunMeanAnomaly*units.RAD_TO_DEG
        + " moonMeanAnomaly "
        + celeCoordCalcs.moonMeanAnomaly*units.RAD_TO_DEG
        + " longitudeMoonAscendingNode "
        + celeCoordCalcs.longitudeMoonAscendingNode*units.RAD_TO_DEG
        + " eccentricityEarthOrbit "
        + celeCoordCalcs.eccentricityEarthOrbit
        + " longitudePerihelionEarthOrbit "
        + celeCoordCalcs.longitudePerihelionEarthOrbit*units.RAD_TO_DEG
        + " meanObliquityEcliptic "
        + celeCoordCalcs.meanObliquityEcliptic);
    }

    void test() {
        System.out.println("test of nutation routine");

        System.out.println("\nnutation test #1 from 1982 edition");
        double testJD = 2443825.69;
        position p = new position("nutation test");
        p.ra.rad = 40.687*units.DEG_TO_RAD;
        p.dec.rad = 49.14*units.DEG_TO_RAD;
        System.out.println("values should be -3.378 -9.321");
        calcForJD(p, testJD);
        System.out.println("result "
        + celeCoordCalcs.nutationLongitude
        + " "
        + celeCoordCalcs.nutationObliquity);
        displayVars();
        System.out.println("following change values should be 4.059 -7.096");
        System.out.println("change in arcseconds in Ra "
        + deltaRa*units.RAD_TO_ARCSEC
        + " Dec "
        + deltaDec*units.RAD_TO_ARCSEC);

        System.out.println("\nnutation test #2 from 2000 edition");
        testJD = 2462088.69;
        p.ra.rad = 41.547214*units.DEG_TO_RAD;
        p.dec.rad = 49.348483*units.DEG_TO_RAD;
        System.out.println("values should be 14.861 2.705");
        calcForJD(p, testJD);
        System.out.println("result "
        + celeCoordCalcs.nutationLongitude
        + " "
        + celeCoordCalcs.nutationObliquity);
        displayVars();
        System.out.println("following change values should be 15.843 6.219");
        System.out.println("change in arcseconds in Ra "
        + deltaRa*units.RAD_TO_ARCSEC
        + " Dec "
        + deltaDec*units.RAD_TO_ARCSEC);

        System.out.println("end of nutation test");
    }
}

