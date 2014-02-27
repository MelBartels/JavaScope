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
 * calculate the instantaneous drive rates, knowing latitude, altitude, and azimuth;
 * for positioning, would need at least encoder feedback;
 */
public class trackRates {
    azDouble rateRadSec;
    convertTrig ct;

    trackRates() {
        rateRadSec = new azDouble();
        ct = new convertTrig("trackRates");
    }

    void setPos(double altRad, double azRad) {
        ct.p.alt.rad = altRad;
        ct.p.az.rad = azRad;
    }

    void calcTrackRates() {
        astroTime.getInstance().calcSidT();
        ct.getEquat();
        // equations use azimuth as measured westward from north, as opposed to measured eastward from south,
        // so convert via new az = 180 - old az
        rateRadSec.a = Math.sin(units.HALF_REV-ct.p.az.rad)*Math.cos(cfg.getInstance().latitudeDeg*units.DEG_TO_RAD);
        rateRadSec.z = Math.sin(cfg.getInstance().latitudeDeg*units.DEG_TO_RAD)
        + (eMath.cot(units.QTR_REV-ct.p.alt.rad))*Math.cos(units.HALF_REV-ct.p.az.rad)*Math.cos(cfg.getInstance().latitudeDeg*units.DEG_TO_RAD);
        rateRadSec.a *= 15.*units.ARCSEC_TO_RAD;
        rateRadSec.z *= 15.*units.ARCSEC_TO_RAD;
    }

    void test() {
        azDouble initialPosRad = new azDouble();
        double elapsedTime = units.SEC_TO_RAD;

        System.out.println("trackRates test");
        System.out.print("enter latitude in degrees ");
        console.getDouble();
        cfg.getInstance().latitudeDeg = console.d;
        System.out.print("enter declination in degrees ");
        console.getDouble();
        ct.p.dec.rad = console.d*units.DEG_TO_RAD;
        System.out.print("enter hour angle in degrees (+ value means west of meridian, - value means east of meridian) ");
        console.getDouble();
        ct.p.ha.rad = console.d*units.DEG_TO_RAD;

        System.out.println("rates (distances in arcseconds per second of time) should agree closely");
        astroTime.getInstance().calcSidT();
        ct.p.sidT.rad = cfg.getInstance().current.sidT.rad;
        ct.p.ra.rad = eMath.validRad(ct.p.sidT.rad - ct.p.ha.rad);
        ct.getAltaz();
        ct.p.showCoordDeg();
        initialPosRad.a = ct.p.alt.rad;
        initialPosRad.z = ct.p.az.rad;
        ct.p.sidT.rad += elapsedTime;
        ct.getAltaz();
        System.out.println("convertTrig altitude rate: "
        + ((ct.p.alt.rad-initialPosRad.a)*units.RAD_TO_ARCSEC/(elapsedTime*units.RAD_TO_SEC))
        + " arcseconds, azimuth rate: "
        + (eMath.validRadPi((ct.p.az.rad-initialPosRad.z))*units.RAD_TO_ARCSEC/(elapsedTime*units.RAD_TO_SEC))
        + " arcseconds");

        setPos(initialPosRad.a, initialPosRad.z);
        calcTrackRates();
        ct.p.showCoordDeg();
        System.out.println("trackRates altitude rate: "
        + rateRadSec.a*units.RAD_TO_ARCSEC
        + " arcseconds, azimuth rate: "
        + rateRadSec.z*units.RAD_TO_ARCSEC
        + " arcseconds, at hour angle of "
        + eMath.validRadPi(ct.p.ha.rad)*units.RAD_TO_DEG
        + " degrees");

        System.out.println("end of trackRates test");
    }
}

