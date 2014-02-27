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
 * refraction makes an object appear higher in the sky than it really is when close to the horizon;
 * if scope aimed at horizon, then it will actually be seeing an object -34.5' below horizon;
 * translate scope->sky as 0 -> -34.5 and sky->scope as -34.5 -> 0
 * causes tracking rate to slow down the closer the scope is to the horizon;
 * refraction handled by routines that translate between scope's position (cfg.getInstance().servoParm[].actualPosition)
 *    and sky altitude (cfg.getInstance().current altaz coord);
 *
 * interpolate:
 * 1. find points that the angle fits between
 *    ex: angle of 10 has end point of r[10][0] and beginning point of r[9][0]
 * 2. get position between end points
 *    position = (a-bp)/(ep-bp)
 *    ex: a=1, bp=2, ep=0
 *        position = (1-2)/(0-2) = .5
 * 3. scope->sky refraction = amount of refraction at beginning point +
 *                            position * (amount of refract at end point - amount of refract at beg point)
 *    r = br + p*(er-br), r = br + (a-bp)/(ep-bp)*(er-br), r = br + (a-bp)*(er-br)/(ep-bp)
 *    ex: br=18, er=34.5
 *        r = 18 + .5*(34.5-18) = 26.25 arcmin
 * 4. corrected angle = angle - refraction
 *    ex: c = a-r = c = 60 arcmin - 26.25 arcmin = 33.75 arcmin
 *
 * to reverse (sky->scope): have corrected angle of c, find altitude of a;
 *    ex: c = 60 arcmin - 26.25 arcmin = 33.75 arcmin, solve for a:
 * 1. c = a - r, a = c + r, a = (c+br)(ep-bp) + (a-bp)*(er-br)/(ep-bp),
 *    a(ep-bp) = c*ep - c*bp + br*ep - br*bp + a*er - a*br - bp*er + bp*br,
 *    a*ep - a*bp - a*er + a*br = c*ep - c*bp + br*ep - br*bp - bp*er + bp*br,
 *    a*(ep-bp-er+br) = bp(-c-br-er+br) + ep(c+br),
 *    a = (bp(-c-er) + ep(c+br)) / (ep-bp-er+br),
 *    ex: using example from above, convert all units to armin...
 *        c=33.75 arcmin
 *        br=18
 *        er=34.5
 *        bp=120
 *        ep=0
 *    a = (120(-33.75-34.5) + 0) / (0-120-34.5+18),
 *    a = 120*-68.25 / -136.5,
 *    a = 60 armin
 *
 * (if refract added to angle, eg, corrected angle = angle + refraction, ie, c=a+r,
 *  then use the following to back out the correction:
 *  to reverse: have corrected angle of ca, find altitude of a
 *     ex: ca = 1deg + 26.25arcmin = 86.25arcmin, solve for a
 *  1. ca = a + r, a = ca - r, a = ca - br - (a-bp)*(er-br)/(ep-bp),
 *     a*(ep-bp)+(a-bp)*(er-br) = (ca-br)*(ep-bp),
 *     a*ep-a*bp+a*er-a*br-bp*er+bp*br = ca*ep-ca*bp-br*ep+br*bp,
 *     a*(ep-bp+er-br) = ca*ep-ca*bp-br*ep+br*bp+bp*er-bp*br,
 *     a*(ep-bp+er-br) = ca*(ep-bp)-br*ep+bp*er,
 *     a = (ca*(ep-bp)-br*ep+bp*er) / (ep-bp+er-br),
 *     ex: convert all units to armin...
 *         a = 86.25-18-(34.5-18)(60-120)/(0-120) = 86.25-18-8.25 = 60 (from 1st line)
 *         a = (86.25*(0-120)-18*0+120*34.5)/(0-120+34.5-18) = (-10350+4140)/(-103.5) = 60)
 */
public class refract {
    double refract;
    static final int MAX_REFRACT_IX = 12;
    // to compute scope->sky refraction, subtract interpolation of r table from altitude
    // to compute sky->scope refraction, add interpolation of r table to altitude
    double r[][] = new double[MAX_REFRACT_IX+1][2];
    // work vars
    double a;
    double bp;
    double ep;
    double br;
    double er;

    refract() {
        // table of refraction per altitude angle:
        // r[][0] is altitude angle in degrees
        // r[][1] is refraction in arcminutes of corresponding altitude angles
        r[0][0] = 90.;   r[0][1] = 0.;
        r[1][0] = 60.;   r[1][1] = .55;
        r[2][0] = 30.;   r[2][1] = 1.7;
        r[3][0] = 20.;   r[3][1] = 2.6;
        r[4][0] = 15.;   r[4][1] = 3.5;
        r[5][0] = 10.;   r[5][1] = 5.2;
        r[6][0] =  8.;   r[6][1] = 6.4;
        r[7][0] =  6.;   r[7][1] = 8.3;
        r[8][0] =  4.;   r[8][1] = 11.5;
        r[9][0] =  2.;   r[9][1] = 18.;
        r[10][0] = 0.;   r[10][1] = 34.5;
        // to allow for sky->scope interpolation when scope->sky results in negative elevation
        r[11][0] = -1.;  r[11][1] = 42.75;
    };

    // utility function called by CalcRefract...() functions
    void setWorkVars(double alt) {
        int ix;

        // alt is in radians; convert to degrees for use with refraction table
        a = alt*units.RAD_TO_DEG;
        for (ix = 0; a <= r[ix][0] && ix < MAX_REFRACT_IX; ix++)
            ;
        bp = r[ix-1][0];
        ep = r[ix][0];
        br = r[ix-1][1];
        er = r[ix][1];
    }

    /**
     * this function calcs refraction at a particular aimed altitude, or translate scope->sky coordinates
     * ie, at the horizon, refraction will be 34.5 arcmin, resulting in a lower aimed at sky coordinate
     */
    void calcRefractScopeToSky(double alt) {
        if (alt >= units.QTR_REV)
            refract = 0.;
        else {
            setWorkVars(alt);
            refract = br + (a-bp)*(er-br)/(ep-bp);
            // table gives values in arcmin, so convert to radians
            refract *= units.ARCMIN_TO_RAD;
        }
    }

    /**
     * this function calcs refraction to remove from an already compensated altitude, eg translate sky->scope coordinates
     * ie, at 34.5 arcmin below horizon, value to remove is 34.5 arcmin, resulting in a higher scope coordinate
     */
    void calcRefractSkyToScope(double alt) {
        double a1;

        if (alt >= units.QTR_REV)
            refract = 0.;
        else {
            setWorkVars(alt);
            // convert deg to arcmin
            a *= 60.;
            bp *= 60;
            ep *= 60;
            // 'a' = corrected altitude
            a1 = (bp*(-a-er) + ep*(a+br)) / (ep-bp-er+br);
            // table gives values in arcmin, so convert to radians
            refract = a1*units.ARCMIN_TO_RAD - alt;
        }
    }

    void test() {
        double skyElevation;

        System.out.println("refract class test");
        System.out.print("input telescope elevation above horizon in degrees: ");
        console.getDouble();
        calcRefractScopeToSky(console.d*units.DEG_TO_RAD);
        skyElevation = console.d*units.DEG_TO_RAD - refract;
        System.out.println("scope->sky refraction is "
        + refract*units.RAD_TO_ARCMIN
        + " armin ("
        + refract*units.RAD_TO_ARCSEC
        + " arcsec)");
        System.out.println("scope actually sees object of elevation "
        + skyElevation*units.RAD_TO_DEG
        + " degrees");
        System.out.println("...starting with sky elevation of "
        + skyElevation*units.RAD_TO_DEG
        + ":");
        calcRefractSkyToScope(skyElevation);
        System.out.println("sky->scope refraction = "
        + refract*units.RAD_TO_ARCMIN
        + " armin ("
        + refract*units.RAD_TO_ARCSEC
        + " arcsec)");
        System.out.println("giving scope elevation of "
        + (skyElevation
        + refract)*units.RAD_TO_DEG
        + " degrees");
        System.out.println("end of refract class test");
    }
}

