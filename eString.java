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
 * string constants
 */
public class eString {
    static final String PGM_NAME = "scope";

    static final String SCOPE_START_LINE = "\n\n\n********** start of java "
    + PGM_NAME
    + " **********";
    static final String BUILD_DATE = "October 22, 2006";
    static final String SCOPE_BUILD_LINE = "\n\nbuilt " + BUILD_DATE;
    static final String SCOPE_END_LINE   = "\n\n\n*********** end of java "
    + PGM_NAME
    + " ***********";

    static final char TAB = (char) 9;
    static final char LINE_FEED = (char) 10;
    static final char RETURN = (char) 13;
    static final char SPACE = (char) 32;

    static final String IO_PREFIX = "IO";
    static final String ENCODER_PREFIX = "encoder";
    static final String CMD_SCOPE_PREFIX = "cmd_scope_";
    static final String MOUNT_TYPE_PREFIX = "mountType";
    static final String HANDPAD_DESIGN_PREFIX = "handpadDesign";
    static final String HANDPAD_MODE_PREFIX = "handpadMode";

    static final String TXT_EXT = ".txt";
    static final String CFG_EXT = ".cfg";
    static final String CMD_EXT = ".cmd";
    static final String PEC_EXT = ".pec";
    static final String LOG_EXT = ".log";
    static final String DAT_EXT = "DAT";
    static final String DATA_EXT = ".data";
    static final String COMET_EXT = ".comet";
    static final String GUIDE_EXT = ".guide";
    static final String EC_EXT = ".ec";
    static final String LIMIT_MOTION_EXT = ".lm";
    static final String PMC_EXT = ".pmc";
    static final String ANALYSIS_EXT = ".analysis";
    static final String ERR_EXT = ".err";
    static final String INITHIST_EXT = ".inithist";
    static final String EQUAT_EXT = ".equat";
    static final String ALTAZ_EXT = ".altaz";
    static final String TRACK_LOG_EXT = ".track" + LOG_EXT;
    static final String SERVO_CMD_LOG_EXT = ".servocmd" + LOG_EXT;
    static final String ACCUM_GUIDE_LOG_EXT = ".accumguide" + LOG_EXT;
    static final String ENCODER_RESET_LOG_EXT = ".encoders.reset" + LOG_EXT;
    static final String POLARALIGN_LOG_EXT = ".polaralign" + LOG_EXT;
    static final String HTML_EXT = ".html";

    static final String SLEW_FILE = "slew.dat";
    // DAT_EXT does not include the '.'
    static final String SLEW_OUT_FILE = "SLEW_OUT." + DAT_EXT;
    // DAT_EXT does not include the '.'
    static final String OUT_GUIDE_FILE = "OUTGUIDE." + DAT_EXT;
    static final String INPUT_FILENAME = "input." + DAT_EXT;
    static final String SETDATE_BAT_FILENAME = "setdate.bat";
    static final String SETTIME_BAT_FILENAME = "settime.bat";
    static final String GUIDE_BAT_FILENAME = "guide.bat";
    static final String GUIDE_STARTUP_MAR_FILE = "STARTUP.MAR";
    static final String SCOPE_STARTUP_MAR_FILE = "SCOPE.MAR";

    static final String IO_FILE_WRITE = "ioFileWrite.txt";
    static final String IO_FILE_READ = "ioFileRead.txt";

    static final String LIBRARY_FILE_NAME = "library.DAT";

    static final String SITECH_FIRMWARE_UPDATE_FILE_EXT = ".bin";

    static final String STATUS_HTML_PAGE = "status.html";
    static final String SERVO_STATUS_HTML_PAGE = "servoStatus.html";
    static final String STATUS_STYLE_SHEET = "status.css";
    static final String CURR_EQUAT_COORD_HTML_PAGE = "currEquatCoord.html";
    static final String CURR_EQUAT_COORD_STYLE_SHEET = "currEquatCoord.css";
    static final String CMD_SCOPE_HTML_PAGE = "cmdScope.html";
    static final String PARMS_HTML_PAGE = "parms.html";
    static final String FILES_USED_HTML_PAGE = "filesUsed.html";
    static final String CMD_LINE_HTML_PAGE = "cmdLineArgs.html";

    static final String INIT_INSTRUCTIONS_AUTO = ""
    + "using handpad to move the telescope (can move by hand if external encoders present):\n"
    + " 1. point telescope to init#1 object, then press handpad mode 'on' key\n"
    + " 2. point telescope to init#2 object, then press handpad mode 'off' key";

    static final String INIT_INSTRUCTIONS_INITIALIZED = ""
    + "initialization complete:\n"
    + "telescope ready for goto and tracking";

    /**
     * locale.CANADA
     * locale.CANADA_FRENCH
     * locale.CHINA
     * locale.FRANCE
     * locale.GERMANY
     * locale.ITALY
     * locale.JAPAN
     * locale.KOREA
     * locale.PRC
     * locale.TAIWAN
     * locale.UK
     * locale.US
     * locale.CHINESE
     * locale.ENGLISH
     * locale.FRENCH
     * locale.GERMAN
     * locale.ITALIAN
     * locale.JAPANESE
     * locale.KOREAN
     * locale.SIMPLIFIED_CHINESE
     * locale.TRADITIONAL_CHINESE
     */
    static Locale scopeLocale = Locale.US;
    static NumberFormat nf = NumberFormat.getInstance(scopeLocale);

    /**
     * convert an integer to a string with digits number of digits
     */
    static String intToString(int i, int digits) {
        // so as to include any leading zeroes
        nf.setMinimumIntegerDigits(digits);
        nf.setMaximumIntegerDigits(digits);
        nf.setMaximumFractionDigits(0);
        return nf.format(i);
    }

    static String intToStringNoGroupingNoLeadingZeros(int i, int digits) {
        // so as to include any leading zeroes
        nf.setMaximumIntegerDigits(digits);
        nf.setMaximumFractionDigits(0);
        nf.setGroupingUsed(false);
        return nf.format(i);
    }

    /**
     * convert a long to a string with digits number of digits
     */
    static String longToString(long l, int digits) {
        // so as to include any leading zeroes
        nf.setMinimumIntegerDigits(digits);
        nf.setMaximumIntegerDigits(digits);
        nf.setMaximumFractionDigits(0);
        return nf.format(l);
    }

    static String longToStringNoGroupingNoLeadingZeros(long l, int digits) {
        // so as to include any leading zeroes
        nf.setMaximumIntegerDigits(digits);
        nf.setMaximumFractionDigits(0);
        nf.setGroupingUsed(false);
        return nf.format(l);
    }

    /**
     * convert a double to a string with leftDigits to left of decimal point and rightDigits to right of decimal point
     */
    static String doubleToString(double d, int leftDigits, int rightDigits) {
        nf.setMinimumIntegerDigits(1);
        nf.setMaximumIntegerDigits(leftDigits);
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(rightDigits);
        return nf.format(d);
    }

    /**
     * convert a double to a string with leftDigits to left of decimal point and rightDigits to right of decimal point;
     * no grouping means no 1,234,567, instead, 1234567 will result;
     */
    static String doubleToStringNoGrouping(double d, int leftDigits, int rightDigits) {
        nf.setMinimumIntegerDigits(1);
        nf.setMaximumIntegerDigits(leftDigits);
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(rightDigits);
        nf.setGroupingUsed(false);
        return nf.format(d);
    }

    /**
     * increase size of String s to length l
     */
    static String padString(String s, int l) {
        // make sure at least one space is strung in
        s += " ";
        for (int ix = s.length()+1; ix < l; ix++)
            s += " ";
        return s;
    }

    static int unsignedInt(int i) {
        if (i<0)
            i+=256;
        return i;
    }

    static String intToHex(int i) {
        if (i<0)
            i+=256;
        return "0x" + byteToHexChar(i/16) + byteToHexChar(i%16);
    }

    static char byteToHexChar(int i) {
        if (i<10)
            return (char) (i+'0');
        else
            return (char) (i-10+'a');
    }

    static void test() {
        System.out.println("12.345 displayed with 3 digits to either side of decimal point is "
        + eString.doubleToStringNoGrouping(12.345, 3, 3));
        System.out.println("123 displayed with 4 digits to left side of decimal point is "
        + eString.intToString(123, 4));
    }
}

