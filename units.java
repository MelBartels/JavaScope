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
 * unit conversion constants
 */
public class units {
    // 'class' variables: vars that are created once and referred to by class name, not by object name
    static final char MINUS;
    static final char PLUS;
    // 1 revolution = 2 Pi Radians = 360 Degrees = 24 Hours
    static final double ONE_REV;
    static final double THREE_FOURTHS_REV;
    static final double HALF_REV;
    static final double QTR_REV;
    static final double RAD_TO_DEG;
    static final double DEG_TO_RAD;
    static final double RAD_TO_ARCMIN;
    static final double ARCMIN_TO_RAD;
    static final double RAD_TO_ARCSEC;
    static final double ARCSEC_TO_RAD;
    static final double RAD_TO_TENTHS_ARCSEC;
    static final double TENTHS_ARCSEC_TO_RAD;
    static final double RAD_TO_MILLI_DEG;
    static final double MILLI_DEG_TO_RAD;
    static final double RAD_TO_HR;
    static final double HR_TO_RAD;
    static final double RAD_TO_MIN;
    static final double MIN_TO_RAD;
    static final double RAD_TO_SEC;
    static final double SEC_TO_RAD;
    static final double RAD_TO_HUND_SEC;
    static final double HUND_SEC_TO_RAD;
    static final double RAD_TO_MILLI_SEC;
    static final double MILLI_SEC_TO_RAD;
    static final double DAY_TO_HR;
    static final double HR_TO_DAY;
    static final double DAY_TO_MIN;
    static final double MIN_TO_DAY;
    static final double DAY_TO_SEC;
    static final double SEC_TO_DAY;
    static final double REV_TO_ARCSEC;
    static final double ARCSEC_TO_REV;
    static final double DEG_TO_ARCSEC;
    static final double ARCSEC_TO_DEG;
    static final double SID_RATE;

    /**
     * calculated once when class is loaded
     */
    static {
        MINUS = '-';
        PLUS = '+';
        ONE_REV = 2.*Math.PI;
        THREE_FOURTHS_REV = ONE_REV*3./4.;
        HALF_REV = ONE_REV/2.;
        QTR_REV = ONE_REV/4.;
        RAD_TO_DEG = 360./ONE_REV;
        DEG_TO_RAD = ONE_REV/360.;
        RAD_TO_ARCMIN = 60.*RAD_TO_DEG;
        ARCMIN_TO_RAD = DEG_TO_RAD/60.;
        RAD_TO_ARCSEC = 60.*RAD_TO_ARCMIN;
        ARCSEC_TO_RAD = ARCMIN_TO_RAD/60.;
        RAD_TO_TENTHS_ARCSEC = 10.*RAD_TO_ARCSEC;
        TENTHS_ARCSEC_TO_RAD = ARCSEC_TO_RAD/10.;
        RAD_TO_MILLI_DEG = 1000.*RAD_TO_DEG;
        MILLI_DEG_TO_RAD = DEG_TO_RAD/1000.;
        RAD_TO_HR = 24./ONE_REV;
        HR_TO_RAD = ONE_REV/24.;
        RAD_TO_MIN = 60.*RAD_TO_HR;
        MIN_TO_RAD = HR_TO_RAD/60.;
        RAD_TO_SEC = 60.*RAD_TO_MIN;
        SEC_TO_RAD = MIN_TO_RAD/60.;
        RAD_TO_HUND_SEC = 100.*RAD_TO_SEC;
        HUND_SEC_TO_RAD = SEC_TO_RAD/100.;
        RAD_TO_MILLI_SEC = 1000.*RAD_TO_SEC;
        MILLI_SEC_TO_RAD = SEC_TO_RAD/1000.;
        DAY_TO_HR = 24.;
        HR_TO_DAY = 1./DAY_TO_HR;
        DAY_TO_MIN = 60.*DAY_TO_HR;
        MIN_TO_DAY = 1./DAY_TO_MIN;
        DAY_TO_SEC = 60.*DAY_TO_MIN;
        SEC_TO_DAY = 1./DAY_TO_SEC;
        REV_TO_ARCSEC = 1296000.;
        ARCSEC_TO_REV = 1./REV_TO_ARCSEC;
        DEG_TO_ARCSEC = 3600.;
        ARCSEC_TO_DEG = 1./DEG_TO_ARCSEC;
        SID_RATE = 1.002737909;
    }
}

