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
 * from Jean Meeus' Astronomical Formulae for Calculators second edition, 1982;
 * starlight seems to come from a different direction than if earth at rest: this effect is called annual aberration;
 * diurnal aberration is due to earth's daily rotation and is of .3" value so will be ignored
 */
public class annualAberration {
    double preRa;
    double preDec;
    double deltaRa;
    double deltaDec;

    void copy(annualAberration a) {
        preRa = a.preRa;
        preDec = a.preDec;
        deltaRa = a.deltaRa;
        deltaDec = a.deltaDec;
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
        calcSubr(p, JD);
    }

    void calcSubr(position p, double JD) {
        celeCoordCalcs.calcVars(JD);

        preRa = p.ra.rad;
        preDec = p.dec.rad;

        // from 1982 edition
        /**
        deltaRa = -20.49*(Math.cos(preRa)*Math.cos(sunTrueLongitude)*Math.cos(obliquityEcliptic)
        + Math.sin(preRa)*Math.sin(sunTrueLongitude)) / Math.cos(preDec);
        deltaRa *= units.ARCSEC_TO_RAD;

        deltaDec = -20.49*(Math.cos(sunTrueLongitude)*Math.cos(obliquityEcliptic)
        *(Math.tan(obliquityEcliptic)*Math.cos(preDec)-Math.sin(preRa)*Math.sin(preDec))
        +Math.cos(preRa)*Math.sin(preDec)*Math.sin(sunTrueLongitude));
        deltaDec *= units.ARCSEC_TO_RAD;
         */

        // from 2000 edition
	deltaRa = -20.49552*((Math.cos(preRa)*Math.cos(celeCoordCalcs.sunTrueLongitude)*Math.cos(celeCoordCalcs.obliquityEcliptic)
	+ Math.sin(preRa)*Math.sin(celeCoordCalcs.sunTrueLongitude)) / Math.cos(preDec))
        + celeCoordCalcs.eccentricityEarthOrbit*20.49552*((Math.cos(preRa)
        *Math.cos(celeCoordCalcs.longitudePerihelionEarthOrbit)*Math.cos(celeCoordCalcs.obliquityEcliptic)
        + Math.sin(preRa)*Math.sin(celeCoordCalcs.longitudePerihelionEarthOrbit))/Math.cos(preDec));
	deltaRa *= units.ARCSEC_TO_RAD;

	deltaDec = -20.49552*(Math.cos(celeCoordCalcs.sunTrueLongitude)
        *Math.cos(celeCoordCalcs.obliquityEcliptic)*(Math.tan(celeCoordCalcs.obliquityEcliptic)*Math.cos(preDec)
        - Math.sin(preRa)*Math.sin(preDec))
	+ Math.cos(preRa)*Math.sin(preDec)*Math.sin(celeCoordCalcs.sunTrueLongitude))
        + celeCoordCalcs.eccentricityEarthOrbit*20.49552*(Math.cos(celeCoordCalcs.longitudePerihelionEarthOrbit)
        *Math.cos(celeCoordCalcs.obliquityEcliptic)*(Math.tan(celeCoordCalcs.obliquityEcliptic)*Math.cos(preDec)
        - Math.sin(preRa)*Math.sin(preDec))+Math.cos(preRa)*Math.sin(preDec)*Math.sin(celeCoordCalcs.longitudePerihelionEarthOrbit));
	deltaDec *= units.ARCSEC_TO_RAD;
    }

    void test() {
        System.out.println("test of annual aberration routine");
        System.out.println("\nannual aberration test#1 from 1982 edition");
        double testJD = 2443825.69;
        position p = new position("annual aberration test");
        p.ra.rad = 40.687*units.DEG_TO_RAD;
        p.dec.rad = 49.14*units.DEG_TO_RAD;
        calcForJD(p, testJD);
        System.out.println("sunTrueLongitude "
        + celeCoordCalcs.sunTrueLongitude*units.RAD_TO_DEG
        + " obliquityEcliptic "
        + celeCoordCalcs.obliquityEcliptic*units.RAD_TO_DEG);
        System.out.println("change values should be 29.619 6.554");
        System.out.println("change in arcseconds in Ra "
        + deltaRa*units.RAD_TO_ARCSEC
        + " Dec "
        + deltaDec*units.RAD_TO_ARCSEC);

        System.out.println("\nannual aberration test#2 from 2000 edition");
        testJD = 2462088.69;
        p.ra.rad = 41.547214*units.DEG_TO_RAD;
        p.dec.rad = 49.348483*units.DEG_TO_RAD;
        calcForJD(p, testJD);
        System.out.println("sunTrueLongitude "
        + celeCoordCalcs.sunTrueLongitude*units.RAD_TO_DEG
        + " obliquityEcliptic "
        + celeCoordCalcs.obliquityEcliptic*units.RAD_TO_DEG);
        System.out.println("change values should be 30.045 6.697");
        System.out.println("change in arcseconds in Ra "
        + deltaRa*units.RAD_TO_ARCSEC
        + " Dec "
        + deltaDec*units.RAD_TO_ARCSEC);

        System.out.println("end of annual aberration test");
    }
}

