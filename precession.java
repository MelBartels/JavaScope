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
 * proper motion:
 *
 * from http://www.seds.org/~spider/spider/ScholarX/coord_ch.html;
 * The star with the largest observed proper motion is 9.7 mag Barnard's Star in Ophiuchus with 10.27 "/y (arc seconds per year).
 * According to F. Schmeidler, only about 500 stars are known to have proper motions of more than 1 "/y.
 */

/**
 * from http://www.seds.org/~spider/spider/ScholarX/coord_ch.html#precession;
 * low precision but quick processing routine for precession;
 * high precision routine from Meeus;
 */
public class precession {
    double deltaYr;
    double preRa;
    double preDec;
    double deltaRa;
    double deltaDec;

    void copy(precession p) {
        deltaYr = p.deltaYr;
        preRa = p.preRa;
        preDec = p.preDec;
        deltaRa = p.deltaRa;
        deltaDec = p.deltaDec;
    }

    void calc(position p, double deltaYr) {
        preRa = p.ra.rad;
        preDec = p.dec.rad;
        this.deltaYr = deltaYr;
        calcSubr();
    }

    /**
     * Precession of the Earth's polar axis is caused by the gravitational pull of the Sun and the Moon on the equatorial
     * bulge of the flattened rotating Earth. It makes the polar axis precess around the pole of the ecliptic,
     * with a period of 25,725 years (the so-called Platonic year).
     * The effect is large enough for changing the equatorial coordinate system significantly in comparatively short times
     * (therefore, Hipparchus was able to discover it around 130 B.C.).
     * Sun and moon together give rise to the lunisolar precession p0, while the other planets contribute the
     * significantly smaller planetary precession p1, which sum up to the general precession p;
     * numerical values for these quantities are (from Schmeidler; t is the time in tropical years from 2000.0):
     * p0 =  50.3878" + 0.000049" * t
     * p1 = - 0.1055" + 0.000189" * t
     * p  =  50.2910" + 0.000222" * t
     *
     * These values give the annual increase of ecliptical longitude for all stars.
     * The effect on equatorial coordinates is formally more complicated, and approximately given by
     * p_RA  = m + n * sin RA * tan Dec
     * p_Dec = n * cos RA
     * (my note: p_... is per year)
     * where the constants m and n are the precession components given by
     * m = + 46.124" + 0.000279" * t
     *   =   3.0749 s + 0.0000186 s * t
     * n = + 20.043" - 0.000085" * t
     *   =   1.3362s - 0.0000056 s * t
     */
    private void calcSubr() {
        double m, n;

        m = 46.124 + 0.000279 * deltaYr/100.;
        n = 20.043 - 0.0085 * deltaYr/100.;
        deltaRa = deltaYr * (m + n * Math.sin(preRa) * Math.tan(preDec)) * units.ARCSEC_TO_RAD;
        deltaDec = deltaYr * n * Math.cos(preRa) * units.ARCSEC_TO_RAD;
    }

    private void calcSubrRigorous() {
        double t;
        double eta;
        double z;
        double theta;
        double a, b, c;
        double ra, dec;

        t = deltaYr/100.;

	  eta = (2306.2181*t +  .30188*t*t + .017998*t*t*t)*units.ARCSEC_TO_RAD;
	    z = (2306.2181*t + 1.09468*t*t + .018203*t*t*t)*units.ARCSEC_TO_RAD;
	theta = (2004.3109*t -  .42665*t*t + .041883*t*t*t)*units.ARCSEC_TO_RAD;

	a = eMath.validRad(Math.cos(preDec)*Math.sin(preRa+eta));
	b = eMath.validRad(Math.cos(theta)*Math.cos(preDec)*Math.cos(preRa+eta) - Math.sin(theta)*Math.sin(preDec));
	c = eMath.validRad(Math.sin(theta)*Math.cos(preDec)*Math.cos(preRa+eta) + Math.cos(theta)*Math.sin(preDec));
 	
	ra = Math.atan(a/b) + z;
	dec = Math.asin(c);

        deltaRa = eMath.validRadPi(ra - preRa);
        deltaDec = dec - preDec;
    }

    void test() {
        position p = new position("precession test");
        StringTokenizer st;
        double startJD;
        double endJD;

        System.out.println("precession test (low-precision)");
        System.out.println("example: change over 5 years (from 2000 to 2005) using position 00:00:00 00:00:00 is 00:00:15.375 00:01:40");
        System.out.print("enter raHr, raMin, raSec, decDeg, decMin, decSec ");
        console.getString();
        st = new StringTokenizer(console.s);
        common.fReadEquatCoord(p, st);
        System.out.print("enter starting year ");
        console.getDouble();
        startJD = astroTime.getInstance().calcJDFromYear(console.d);
        console.d = 0.;
        System.out.print("enter ending year (press return for current date) ");
        console.getDouble();
        if (console.d == 0.) {
            astroTime.getInstance().calcSidT();
            endJD = astroTime.getInstance().JD;
        }
        else
            endJD = astroTime.getInstance().calcJDFromYear(console.d);
        calc(p, (endJD-startJD)/365.25);

        System.out.println("change in arcseconds in Ra "
        + deltaRa*units.RAD_TO_ARCSEC
        + " Dec "
        + deltaDec*units.RAD_TO_ARCSEC);

        calcSubrRigorous();
        System.out.println("rigorous calculation:");
        System.out.println("change in arcseconds in Ra "
        + deltaRa*units.RAD_TO_ARCSEC
        + " Dec "
        + deltaDec*units.RAD_TO_ARCSEC);

        p.ra.rad += deltaRa;
        p.ra.rad = eMath.validRad(p.ra.rad);
        p.dec.rad += deltaDec;
        p.showDataFileFormat();
        System.out.println("end of precession test");
    }
}

