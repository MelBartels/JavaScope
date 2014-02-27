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
 * from Jean Meeus' Astronomical Formulae for Calculators second edition, 1982, revised per later editions;
 */
public class celeCoordCalcs {
    static double t;

    static double sunMeanLongitude;
    static double sunMeanAnomaly;
    static double sunEquationOfTheCenter;
    static double sunTrueLongitude;
    static double sunTrueAnomaly;

    static double moonMeanLongitude;
    static double moonMeanAnomaly;

    static double longitudeMoonAscendingNode;
    static double longitudePerihelionEarthOrbit;
    static double eccentricityEarthOrbit;
    static double obliquityEcliptic;
    static double meanObliquityEcliptic;

    static double nutationLongitude;
    static double nutationObliquity;

    static double angsep;
    static double angSepDiff;

    celeCoordCalcs() {}

    static void calcVars(double JD) {
        // old formula from earlier edition
        /**
        t = (JD - 2415020.) / 36525.;

        sunMeanLongitude = eMath.validRad((279.6967+36000.7689*t+.000303*t*t)*units.DEG_TO_RAD);
        sunMeanAnomaly = eMath.validRad((358.4758+35999.0498*t-.00015*t*t)*units.DEG_TO_RAD);
        moonMeanLongitude = eMath.validRad((270.4342+481267.8831*t-.001133*t*t)*units.DEG_TO_RAD);
        moonMeanAnomaly = eMath.validRad((296.1046+477198.8491*t+.009192*t*t)*units.DEG_TO_RAD);
        longitudeMoonAscendingNode = eMath.validRad((259.1833-1934.142*t+.002078*t*t)*units.DEG_TO_RAD);
        obliquityEcliptic = (23.452294-.0130125*t-.00000164*t*t+.000000503*t*t*t)*units.DEG_TO_RAD;

        sunEquationOfTheCenter = (1.91946-.004789*t-.000014*t*t)*Math.sin(sunMeanAnomaly)
        +(.020094-.0001*t)*Math.sin(2*sunMeanAnomaly)
        +.000293*Math.sin(3*sunMeanAnomaly);
        sunEquationOfTheCenter *= units.DEG_TO_RAD;

        sunTrueLongitude = sunMeanLongitude + sunEquationOfTheCenter;
        sunTrueAnomaly = sunMeanAnomaly + sunEquationOfTheCenter;

        nutationLongitude = -17.2327*Math.sin(longitudeMoonAscendingNode)
        -1.2729*Math.sin(2*sunMeanLongitude);

        nutationObliquity = 9.21*Math.cos(longitudeMoonAscendingNode)
        +.5522*Math.cos(2*sunMeanLongitude);
         */

        // new formula from latest edition;
        // JD2000 = JD 2451545 = Greenwich noon on Jan 1 2000
 	t = (JD - eMath.JD2000) / 36525.;

        sunMeanLongitude = eMath.validRad((280.46646+36000.76983*t+.0003032*t*t)*units.DEG_TO_RAD);
	moonMeanLongitude = eMath.validRad((218.3165 + 481267.8813*t)*units.DEG_TO_RAD);
	eccentricityEarthOrbit = .016708634-.000042037*t-.0000001267*t*t;
	longitudePerihelionEarthOrbit = eMath.validRad((102.93735+1.71946*t+.00046*t*t)*units.DEG_TO_RAD);
	sunMeanAnomaly = eMath.validRad((357.52911+35999.05029*t-.0001537*t*t)*units.DEG_TO_RAD);
	moonMeanAnomaly = eMath.validRad((134.96298+477198.867398*t-0.0086972*t*t)*units.DEG_TO_RAD);

	longitudeMoonAscendingNode = eMath.validRad((125.04452-1934.136261*t+.0020708*t*t+t*t*t/450000.)*units.DEG_TO_RAD);
	meanObliquityEcliptic = (23.43929-46.8150/3600.*t-.00059/3600.*t*t+.001813/3600.*t*t*t)*units.DEG_TO_RAD;
	sunEquationOfTheCenter = (1.914602-.004817*t-.000014*t*t)*Math.sin(sunMeanAnomaly)
	+(.019993-.000101*t)*Math.sin(2*sunMeanAnomaly)
	+.000289*Math.sin(3*sunMeanAnomaly);
	sunEquationOfTheCenter *= units.DEG_TO_RAD;
	sunTrueLongitude = eMath.validRad(sunMeanLongitude + sunEquationOfTheCenter);

	nutationLongitude = -17.20*Math.sin(longitudeMoonAscendingNode)
        -1.32*Math.sin(2*sunMeanLongitude)
        -.23*Math.sin(2*moonMeanLongitude)
        +.21*Math.sin(2*longitudeMoonAscendingNode);

        nutationObliquity = 9.20*Math.cos(longitudeMoonAscendingNode)
        +.57*Math.cos(2*sunMeanLongitude)
        +.1*Math.cos(2*moonMeanLongitude)
        -.09*Math.cos(2*longitudeMoonAscendingNode);

        obliquityEcliptic = meanObliquityEcliptic + nutationObliquity*units.ARCSEC_TO_RAD;
    }

    /**
     * angular separation methods: two to calculate the angular separation of equatorial coordinates, two
     * to calculate the angular separation of altazimuth coordinates, and the final to calculate the difference
     * between the equatorial and azimuth angular separations
     */
    static double calcEquatAngularSepViaHrAngle(position a, position z) {
        double angsep;
        double aHA, zHA, diffHA;

        // hour angles
        aHA = a.sidT.rad - a.ra.rad;
        zHA = z.sidT.rad - z.ra.rad;
        diffHA = aHA - zHA;

        angsep = Math.acos(Math.sin(a.dec.rad) * Math.sin(z.dec.rad) + Math.cos(a.dec.rad) * Math.cos(z.dec.rad) * Math.cos(diffHA));

        return angsep;
    }

    static double calcEquatAngularSepViaRa(position a, position z) {
        double diffRa;

        diffRa = eMath.validRadPi(z.ra.rad - a.ra.rad);
        angsep = Math.acos(Math.sin(a.dec.rad) * Math.sin(z.dec.rad) + Math.cos(a.dec.rad) * Math.cos(z.dec.rad) * Math.cos(diffRa));
        return angsep;
    }

    static double calcEquatAngularSepViaRaLwp(lightweightPosition a, lightweightPosition z) {
        double diffRa;

        diffRa = eMath.validRadPi(z.ra - a.ra);
        angsep = Math.acos(Math.sin(a.dec) * Math.sin(z.dec) + Math.cos(a.dec) * Math.cos(z.dec) * Math.cos(diffRa));
        return angsep;
    }

    static double calcAltazAngularSep(position a, position z) {
        // cos of angle same as cos of -angle, so doesn't matter if diffAZ is positive or negative;
        double diffAz = a.az.rad - z.az.rad;
        angsep = Math.acos(Math.sin(a.alt.rad) * Math.sin(z.alt.rad) + Math.cos(a.alt.rad) * Math.cos(z.alt.rad) * Math.cos(diffAz));
        return angsep;
    }

    static double angSepDiffViaHrAngle(position a, position z) {
        angSepDiff = Math.abs(Math.abs(calcEquatAngularSepViaHrAngle(a, z)) - Math.abs(calcAltazAngularSep(a, z)));
        return angSepDiff;
    }

    static double angSepDiffViaRa(position a, position z) {
        angSepDiff = Math.abs(Math.abs(calcEquatAngularSepViaRa(a, z)) - Math.abs(calcAltazAngularSep(a, z)));
        return angSepDiff;
    }
}

