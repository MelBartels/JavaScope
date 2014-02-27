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
 * astronomy time, a singleton class; calculates Julian Date and sidereal time
 */
public class astroTime extends currentDateTime {
    double JD;
    hmsm sidT = new hmsm();
    private static astroTime INSTANCE;

    public static astroTime getInstance() {
        if (INSTANCE == null)
            synchronized(astroTime.class) {
                if (INSTANCE == null)
                    INSTANCE = new astroTime();
            }
        return INSTANCE;
    }

    double calcJDFromYear(double year) {
        long longYear;
        double holdJD;
        double longYearJD;
        double elapsedDays;
        double JDFromYear;

        // start with calculating JD for Jan 1 of year, noon
        longYear = (long) year;
        holdJD = JD;
        calcJD(longYear, 1, 1, 0, 0, 12, 0, 0, 0);
        longYearJD = JD;
        JD = holdJD;
        // get elapsed days based on fractional part of year
        elapsedDays = (year - (double) longYear) * 365.25;
        // add elapsed days
        JDFromYear = (double) longYearJD + elapsedDays;
        return JDFromYear;
    }

    void calcJD(long y,  int mon,  int d,  int tz,  int dst,  int h,  int m,  int s, int ms) {
        long a;
        double b;

        // return 1st formula if Gregorian calendar, otherwise 2nd formula for Julian calendar;
        // from Sky and Telescope, August, 1991, pg 183

        if ((y > 1582) || y == 1582 && mon > 10 || y == 1582 && mon == 10 && d > 15)
            a = 367*y - 7*(y + (mon+9)/12)/4 - 3*((y + (mon-9)/7)/100 + 1)/4 + 275*mon/9 + d + 1721029L;
        else
            a = 367*y - 7*(y + 5001 + (mon-9)/7)/4 + 275*mon/9 + d + 1729777L;
        // subtract 12hrs since JD starts at 12noon UT
        // dst is -1 in Java
        if (dst != 0)
            dst = 1;
        b = (tz - dst - 12. + h + m/60. + s/3600. + ms/3600000.)/24.;
        JD = a + b;
    }

    // while calculating sidT, also calculates JD and local time
    double calcSidT() {
        // JD at Greenwich (will be in form of a whole number + 0.5)
        double GreenwichJD;
        // fractional part of JD beyond GreenwichJD
        double fracDay;
        // intermediate calculated result
        double t;
        // sidereal time at 0hrs UT
        double GreenwichSidTHr;
        // sidereal time in hours
        double sidHr;

        getCurrentDateTime();
        calcJD(y, mon, d, tz, dst, h, m, s, ms);

        fracDay = eMath.fModulus(JD, 1.);
        // fracDay meas. from 0hr UT or < JD > .5
        if (fracDay > 0.5)
            fracDay -= 0.5;
        else
            fracDay += 0.5;
        GreenwichJD = JD - fracDay;

        // Astronomical Formulae for Calculators, by Jean Meeus, pg 39
        t = (GreenwichJD - 2415020.)/36525.;
        GreenwichSidTHr = 6.6460656 + 2400.051262*t + 0.00002581*t*t;
        sidHr = eMath.fModulus(fracDay*units.SID_RATE*24. + GreenwichSidTHr - cfg.getInstance().longitudeDeg/15., 24);
        sidT.rad = sidHr*units.HR_TO_RAD;
        return sidT.rad;
    }

    // return true if sidT changes from last calculation of sidT
    boolean newSidT() {
        double holdSidT = sidT.rad;

        calcSidT();
        if (sidT.rad != holdSidT)
            return true;
        else
            return false;
    }

    void waitForNewSidT() {
        double holdSidT = sidT.rad;

        while (sidT.rad == holdSidT)
            calcSidT();
    }

    void wait(int ms) {
        double waitTimeRad = (double) ms * units.MILLI_SEC_TO_RAD;
        double holdSidT = sidT.rad;
        double tDiffRad;

        do {
            calcSidT();
            tDiffRad = sidT.rad - holdSidT;
            tDiffRad = eMath.validRad(tDiffRad);
        }while (tDiffRad < waitTimeRad);
    }

    void test() {
        int ix = 0;
        double lastSidT = 0.;

        System.out.println("class astroTime test");
        getCurrentDateTime();

        System.out.println("timezone is "
        + tz
        + ", daylight savings time is "
        + dst);

        while (ix < 10) {
            // calls getCurrentDateTime() and calcJD()
            calcSidT();
            if (sidT.rad != lastSidT) {
                ix++;

                System.out.println("Julian Date = "
                + JD
                + ", Sidereal time = "
                + sidT.rad*units.RAD_TO_HR
                + " "
                + sidT.getStringHMSM()
                + " ("
                + sidT.getStringDM()
                + ")"
                + ", diff = "
                + (sidT.rad - lastSidT)*units.RAD_TO_SEC
                + "sec");

                lastSidT = sidT.rad;
            }
        }
    }
}

