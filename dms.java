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
 * degree - minute - second object; encompases a degree object
 */
public class dms {
    char sign;
    int deg;
    int min;
    int sec;
    double rad;
    static final byte LX200_DEG_SYM = (byte) 223;
    String stringDMS;
    String stringLX200;
    degree d = new degree();

    void getDMS() {
        double degRad;
        double minRad;
        double lSec;
        double lRad;

        lRad = rad;
        if (lRad < 0.) {
            sign = units.MINUS;
            lRad = -lRad;
        }
        else
            sign = units.PLUS;
        // rounding at arcsec level
        lRad += .5*units.ARCSEC_TO_RAD;
        deg = (int) (lRad*units.RAD_TO_DEG);
        degRad = deg*units.DEG_TO_RAD;
        min = (int) ((lRad - degRad)*units.RAD_TO_ARCMIN);
        minRad = min*units.ARCMIN_TO_RAD;
        lSec = (lRad - degRad - minRad)*units.RAD_TO_ARCSEC;
        sec = (int) lSec;
    }

    void calcRad() {
        if (deg < 0) {
            deg = -deg;
            sign = units.MINUS;
        }
        if (min < 0) {
            min = -min;
            sign = units.MINUS;
        }
        if (sec < 0) {
            sec = -sec;
            sign = units.MINUS;
        }
        rad = (double) deg*units.DEG_TO_RAD + (double) min*units.ARCMIN_TO_RAD + (double) sec*units.ARCSEC_TO_RAD;
        if (sign == units.MINUS)
            rad = -rad;
    }

    void copy(dms d) {
        sign = d.sign;
        deg = d.deg;
        min = d.min;
        sec = d.sec;
        rad = d.rad;
        stringDMS = d.stringDMS;
        stringLX200 = d.stringLX200;
        // copy passed in dms degree() to this degree()
        this.d.copy(d.d);
    }

    private String buildStringDMS() {
        stringDMS = sign
        + eString.intToString(deg, 2)
        + ":"
        + eString.intToString(min, 2)
        + ":"
        + eString.intToString(sec, 2);

        return stringDMS;
    }

    private String buildStringDMS(char delimiter) {
        stringDMS = sign
        + eString.intToString(deg, 2)
        + delimiter
        + eString.intToString(min, 2)
        + delimiter
        + eString.intToString(sec, 2);

        return stringDMS;
    }

    String getStringDMS() {
        getDMS();
        return buildStringDMS();
    }

    String getStringDMS(char delimiter) {
        getDMS();
        return buildStringDMS(delimiter);
    }

    String getStringDM() {
        d.rad = rad;
        return d.getStringDM();
    }

    /**
     *  standard format -12^34#   where ^ stands for the degree symbol
     * long format     -12^34:56#
     * must use leading zeroes - no blanks, ie, 02^34# is correct while _2^34# will fail
     * if no sign, then use 3 digits for degree value
     */
    String getStringLX200(boolean longFormat, boolean signed) {
        int lMin;
        int lDeg;

        if (!signed)
            rad = eMath.validRad(rad);

        getDMS();

        lDeg = deg;
        if (longFormat)
            lMin = min;
        else {
            lMin = (int) (min + (double) sec/60. + .5);
            if (lMin == 60) {
                lMin = 0;
                lDeg++;
            }
        }

        stringLX200 = "";
        if (signed)
            stringLX200 += sign + eString.intToString(lDeg, 2);
        else
            stringLX200 += eString.intToString(lDeg, 3);
        stringLX200 += (char) LX200_DEG_SYM;

        if (longFormat) {
            stringLX200 += eString.intToString(lMin, 2)
            + ":"
            + eString.intToString(sec, 2)
            + "#";
        }
        else
            stringLX200 += eString.intToString(lMin, 2)
            + "#";

        return stringLX200;
    }
}

