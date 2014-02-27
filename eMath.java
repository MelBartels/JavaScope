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
 * common math functions
 */
public class eMath {
    // JD on Greenwich Jan 1, 2000, noon
    static final double JD2000 = 2451545.;

    /**
     * return the log10 of a number
     */
    static double log10(double num) {
        return Math.log(num)/Math.log(10);
    }

    /**
     * return the whole portion of a double
     */
    static double wholeNum(double num) {
        if (num < 0.)
            return Math.ceil(num);
        else
            return Math.floor(num);
    }

    /**
     * return the fractional portion of a double
     */
    static double fractional(double num) {
        return num - wholeNum(num);
    }

    /**
     * return the modulus or remainder of a double divided by a double, ie,
     */
    static double fModulus(double num, double divisor) {
        return fractional(num/divisor)*divisor;
    }

    /**
     * return a integer of an integer raised to an integer power
     */
    static int intPow(int i, int x) {
        int ix;
        int r = 1;

        for (ix = 0; ix < x; ix++)
            r *= i;

        return r;
    }

    /**
     * return a long of an integer raised to an integer power
     */
    static long longPow(int i, int x) {
        int ix;
        long r = 1;

        for (ix = 0; ix < x; ix++)
            r *= (long) i;

        return r;
    }

    static double sqr(double value) {
        return value * value;
    }

    /**
     * bring a number in radians within the bounds of 0 to 2*Pi
     */
    static double validRad(double rad) {
        rad %= units.ONE_REV;
        if (rad < 0.)
            rad += units.ONE_REV;
        return rad;
    }

    /**
     * bring a number in radians within the bounds of 0 to 2*Pi but then adjust
     * the return value to be between -Pi to +Pi
     */
    static double validRadPi(double rad) {
        rad = validRad(rad);
        if (rad > units.HALF_REV)
            rad -= units.ONE_REV;
        return rad;
    }

    /**
     * bring a number within legal bounds of sine and cosine (between -1 and 1)
     */
    static double boundsSinCos(double value) {
        if (value > 1.)
            value = 1.;
        else if (value < -1.)
            value = -1.;

        return value;
    }

    static double cot(double value) {
        return 1./Math.tan(value);
    }

    static int compareDouble(double a, double b) {
        if (a < b)
            return -1;
        else if (a == b)
            return 0;
        else
            return 1;
    }
}

