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
 * date and time class; uses computer date and time
 */
public class currentDateTime {
    int y;
    int mon;
    int d;
    int h;
    int m;
    int s;
    int ms;
    String stringCurrentDateTime;
    String stringcurrentDateTimeNoDelimiters;
    int tz;
    int dst;

    void getCurrentDateTime() {
        Calendar calendar = new GregorianCalendar();
        y = calendar.get(Calendar.YEAR);
        // first month is 0
        mon = 1 + calendar.get(Calendar.MONTH);
        d = calendar.get(Calendar.DAY_OF_MONTH);
        h = calendar.get(Calendar.HOUR_OF_DAY);
        m = calendar.get(Calendar.MINUTE);
        s = calendar.get(Calendar.SECOND);
        ms = calendar.get(Calendar.MILLISECOND);
        tz = -(int) (calendar.get(Calendar.ZONE_OFFSET) / 3600000L);
        dst = -(int) (calendar.get(Calendar.DST_OFFSET) / 3600000L);

    }

    String buildStringCurrentDateTime() {
        // to avoid formatted 4 digit integer, ie, 2,002
        stringCurrentDateTime = eString.intToString(y/100, 2)
        + eString.intToString(y%100, 2)
        + "-"
        + eString.intToString(mon, 2)
        + "-"
        + eString.intToString(d, 2)
        + "/"
        + eString.intToString(h, 2)
        + ":"
        + eString.intToString(m, 2)
        + ":"
        + eString.intToString(s, 2)
        + "."
        + eString.intToString(ms, 3);

        return stringCurrentDateTime;
    }

    // to nearest second
    String buildStringcurrentDateTimeNoDelimeters() {
        // to avoid formatted 4 digit integer, ie, 2,002
        stringcurrentDateTimeNoDelimiters = eString.intToString(y/100, 2)
        + eString.intToString(y%100, 2)
        + eString.intToString(mon, 2)
        + eString.intToString(d, 2)
        + eString.intToString(h, 2)
        + eString.intToString(m, 2)
        + eString.intToString(s, 2);

        return stringcurrentDateTimeNoDelimiters;
    }
}

