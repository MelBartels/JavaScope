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
 * hour - minute - second - millisecond object; encompases a degree object
 */
public class hmsm {
    char sign;
    double rad;
    int hr;
    int min;
    int sec;
    int milliSec;
    String stringHMSM;
    String stringHMS;
    String stringLX200;
    degree d = new degree();

    void getHMSM() {
        double hrRad;
        double minRad;
        double SecRad;
        double lMilliSec;
        double lRad;

        lRad = rad;
        if (lRad < 0.) {
            sign = units.MINUS;
            lRad = -lRad;
        }
        else
            sign = units.PLUS;
        // rounding at millisecond level
        lRad += .5*units.MILLI_SEC_TO_RAD;
        hr = (int) (lRad*units.RAD_TO_HR);
        hrRad = hr*units.HR_TO_RAD;
        min = (int) ((lRad - hrRad)*units.RAD_TO_MIN);
        minRad = min*units.MIN_TO_RAD;
        sec = (int) ((lRad - hrRad - minRad)*units.RAD_TO_SEC);
        SecRad = sec*units.SEC_TO_RAD;
        lMilliSec = (lRad - hrRad - minRad - SecRad)*units.RAD_TO_MILLI_SEC;
        milliSec = (int) lMilliSec;
    }

    void calcRad() {
        if (hr < 0) {
            hr = -hr;
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
        if (milliSec < 0) {
            milliSec = -milliSec;
            sign = units.MINUS;
        }
        rad = (double) hr*units.HR_TO_RAD + (double) min*units.MIN_TO_RAD + (double) sec*units.SEC_TO_RAD + (double) milliSec*units.MILLI_SEC_TO_RAD;
        if (sign == units.MINUS)
            rad = -rad;
    }

    void copy(hmsm h) {
        sign = h.sign;
        hr = h.hr;
        min = h.min;
        sec = h.sec;
        milliSec = h.milliSec;
        rad = h.rad;
        stringHMSM = h.stringHMSM;
        stringLX200 = h.stringLX200;
        d.copy(h.d);
    }

    private String buildStringHMSM() {
        stringHMSM = sign
        + eString.intToString(hr, 2)
        + ":"
        + eString.intToString(min, 2)
        + ":"
        + eString.intToString(sec, 2)
        + "."
        + eString.intToString(milliSec, 3);

        return stringHMSM;
    }

    private String buildStringHMSM(char delimiter) {
        stringHMSM = eString.intToString(hr, 2)
        + delimiter
        + eString.intToString(min, 2)
        + delimiter
        + eString.doubleToString((double) sec + (double) milliSec/1000., 2, 3);

        return stringHMSM;
    }

    private String buildStringHMS() {
        int lhr, lmin, lsec;

        lhr = hr;
        lmin = min;
        lsec = sec;
        if (milliSec >= 500)
            lsec++;
        if (lsec >= 60) {
            lsec -= 60;
            lmin++;
        }
        if (lmin >= 60) {
            lmin -= 60;
            lhr++;
        }
        if (lhr >= 24)
            lhr -= 24;

        stringHMS = eString.intToString(lhr, 2)
        + ":"
        + eString.intToString(lmin, 2)
        + ":"
        + eString.intToString(lsec, 2);

        return stringHMS;

    }

    // data file format
    private String buildStringHMS(char delimiter) {
        stringHMS = eString.intToString(hr, 2)
        + delimiter
        + eString.intToString(min, 2)
        + delimiter
        + eString.intToString(sec, 2);

        return stringHMS;
    }

    String getStringHMSM() {
        getHMSM();
        return buildStringHMSM();
    }

    String getStringHMSM(char delimiter) {
        rad = eMath.validRad(rad);
        getHMSM();
        return buildStringHMSM(delimiter);
    }

    String getStringHMS() {
        rad = eMath.validRad(rad);
        getHMSM();
        return buildStringHMS();
    }

    String getStringHMS(char delimiter) {
        rad = eMath.validRad(rad);
        getHMSM();
        return buildStringHMS(delimiter);
    }

    String getStringDM() {
        d.rad = rad;
        return d.getStringDM();
    }

    /**
     * standard format 12:34.5#
     * long format     12:34:56#
     * must use leading zeroes - no blanks, ie, 02:34.5# is correct while _2:34.5# will fail
     */
    String getStringLX200(boolean longFormat) {
        int roundedSec;
        int tenthsMin;

        rad = eMath.validRad(rad);

        getHMSM();

        roundedSec = (int) (sec + (double) milliSec/1000 + .5);
        tenthsMin = (int) ((double) sec/6. + (double) milliSec/6000.);

        stringLX200 = "";
        stringLX200 += eString.intToString(hr, 2) + ":";
        stringLX200 += eString.intToString(min, 2);
        if (longFormat)
            stringLX200 += ":"
            + eString.intToString(roundedSec, 2)
            + "#";
        else
            stringLX200 += "."
            + eString.intToString(tenthsMin, 1)
            + "#";

        return stringLX200;
    }
}

