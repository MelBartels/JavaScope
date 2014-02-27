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
 * degree object
 */
public class degree {
    char sign;
    int deg;
    int milliDeg;
    double rad;
    String stringDM;

    void getDM() {
        double degRad;
        double lMilliDeg;
        double lRad;

        lRad = rad;
        if (lRad < 0.) {
            sign = units.MINUS;
            lRad = -lRad;
        }
        else
            sign = units.PLUS;
        // rounding at milliDeg level
        lRad += .5*units.MILLI_DEG_TO_RAD;
        deg = (int) (lRad*units.RAD_TO_DEG);
        degRad = deg*units.DEG_TO_RAD;
        lMilliDeg = (lRad - degRad)*units.RAD_TO_MILLI_DEG;
        milliDeg = (int) lMilliDeg;
    }

    void copy(degree d) {
        sign = d.sign;
        deg = d.deg;
        milliDeg = d.milliDeg;
        rad = d.rad;
        stringDM = d.stringDM;
    }

    String buildStringDM() {
        stringDM = sign
        + eString.intToString(deg, 3)
        + "."
        + eString.intToString(milliDeg, 3);

        return stringDM;
    }

    String getStringDM() {
        getDM();
        return buildStringDM();
    }
}

