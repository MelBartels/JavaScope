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
 * traditional spherical trigometric solution to coordinate frame translation
 */
public class convertTrig {
    position p;

    convertTrig(String positionName) {
        p = new position(positionName);
    }

    /**
     * this function gets altazimuth coordinates using the site's cfg longitude (used to compute sidT) and cfg latitude;
     * if site or sky's altaz coord desired, then set pos vars:
     *    1. current local sidereal time (stored in current coord) which is based on:
     *       system date, time, timezone, daylight savings time,
     *    2. current equatorial coordinates;
     *
     * unlike convertMatrix equatorial alignment where atlaz is scope's altaz, here, altaz is site altaz,
     * hence northern hemisphere intersection of meridian and celestial equator is az of 180 (convertMatrix's az is 0);
     *
     * since altaz refers to site altaz, flipped has no place in calculating altaz coordinates from equatorial values;
     */
    void getAltaz() {
        p.ha.rad = p.sidT.rad - p.ra.rad;
        p.alt.rad = convertSecAxis(cfg.getInstance().latitudeDeg*units.DEG_TO_RAD, p.ha.rad, p.dec.rad);
        p.az.rad = convertPriAxis(cfg.getInstance().latitudeDeg*units.DEG_TO_RAD, p.alt.rad, p.ha.rad, p.dec.rad);
    }

    /**
     * set pos alt, az, and sidT before calling;
     */
    void getEquat() {
        p.dec.rad = convertSecAxis(cfg.getInstance().latitudeDeg*units.DEG_TO_RAD, p.az.rad, p.alt.rad);
        p.ha.rad = convertPriAxis(cfg.getInstance().latitudeDeg*units.DEG_TO_RAD, p.dec.rad, p.az.rad, p.alt.rad);
        p.ra.rad = eMath.validRad(p.sidT.rad - p.ha.rad);
    }

    double convertSecAxis(double lat, double fromPri, double fromSec) {
        double sinToSec;
        double toSec;

        // cos(lat) where lat = 90 or -90 zeroes out, leaving first term only
        if (lat == units.QTR_REV || lat == -units.QTR_REV)
            toSec = fromSec;
        else {
            sinToSec = Math.sin(fromSec) * Math.sin(lat) + Math.cos(fromSec) * Math.cos(lat) * Math.cos(fromPri);
            sinToSec = eMath.boundsSinCos(sinToSec);
            toSec = Math.asin(sinToSec);
        }
        return toSec;
    }

    double convertPriAxis(double lat, double toSec, double fromPri, double fromSec) {
        double cosToPri;
        double toPri;

        // take care of the situation where (Math.sin(fromSec) - Math.sin(lat) * Math.sin(toSec)) = 0
        if (lat == units.QTR_REV || lat == -units.QTR_REV)
            toPri = fromPri;
        else {
            cosToPri = (Math.sin(fromSec) - Math.sin(lat) * Math.sin(toSec)) / (Math.cos(lat) * Math.cos(toSec));
            cosToPri = eMath.boundsSinCos(cosToPri);
            toPri = Math.acos(cosToPri);
            // heading east or west of 0 pt?
            if (Math.sin(fromPri) > 0.)
                toPri = units.ONE_REV - toPri;
        }
        return toPri;
    }

    void test() {
        System.out.println("test of convertTrig routines");
        cfg.getInstance().latitudeDeg = 45.;
        p.sidT.rad = units.HALF_REV;

        System.out.println("\ngetAltaz(): altazimuth alignment, latitude = 45 deg, 6 hr west of meridian");
        System.out.println("altaz should be on horizon facing directly west");
        p.ra.rad = p.sidT.rad - 6. * units.HR_TO_RAD;
        p.dec.rad = 0. * units.DEG_TO_RAD;
        getAltaz();
        p.showCoordDeg();
        System.out.println("getEquat() from altaz coordinates just calculated");
        getEquat();
        p.showCoordDeg();

        System.out.println("\ngetAltaz(): altazimuth alignment, latitude = 45 deg, 6 hr east of meridian");
        System.out.println("altaz should be on horizon facing directly east");
        p.ra.rad = p.sidT.rad + 6. * units.HR_TO_RAD;
        getAltaz();
        p.showCoordDeg();
        System.out.println("getEquat() from altaz coordinates just calculated");
        getEquat();
        p.showCoordDeg();

        cfg.getInstance().latitudeDeg = 89.9;

        System.out.println("\nsetting latitude to "
        + cfg.getInstance().latitudeDeg);

        System.out.println("\ngetAltaz(): equatorial alignment, northern hemisphere, 1 hr west of meridian");
        System.out.println("az should be close to 195 deg");
        p.ra.rad = p.sidT.rad - 1. * units.HR_TO_RAD;
        p.dec.rad = 45. * units.DEG_TO_RAD;
        getAltaz();
        p.showCoordDeg();
        System.out.println("getEquat() from altaz coordinates just calculated");
        getEquat();
        p.showCoordDeg();

        System.out.println("\ngetAltaz(): equatorial alignment, northern hemisphere, 1 hr east of meridian");
        System.out.println("az should be close to 165 deg");
        p.ra.rad = p.sidT.rad + 1. * units.HR_TO_RAD;
        p.dec.rad = 45. * units.DEG_TO_RAD;
        getAltaz();
        p.showCoordDeg();
        System.out.println("getEquat() from altaz coordinates just calculated");
        getEquat();
        p.showCoordDeg();
        System.out.println("end of convertTrig test");
    }
}

