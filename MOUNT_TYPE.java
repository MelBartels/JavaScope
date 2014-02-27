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

public final class MOUNT_TYPE {
    private String id;
    public final int KEY;
    private MOUNT_TYPE prev;
    private MOUNT_TYPE next;

    private static int itemCount;
    private static MOUNT_TYPE first;
    private static MOUNT_TYPE last;

    private MOUNT_TYPE(String id) {
        this.id = new String(id);
        this.KEY = itemCount++;
        if (first == null)
            first = this;
        if (last != null) {
            this.prev = last;
            last.next = this;
        }
        last = this;
    }

    public static Enumeration elements() {
        return new Enumeration() {
            private MOUNT_TYPE current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                MOUNT_TYPE c = current;
                current = current.next();
                return c;
            }
        };
    }

    public String toString() {
        return this.id;
    }

    public static int size() {
        return itemCount;
    }

    public static MOUNT_TYPE first() {
        return first;
    }

    public static MOUNT_TYPE last() {
        return last;
    }

    public MOUNT_TYPE prev() {
        return this.prev;
    }

    public MOUNT_TYPE next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of MOUNT_TYPE, which has "
        + itemCount
        + " elements:");
        MOUNT_TYPE current = first;
        while (current != null) {
            console.stdOutLn(current.id
            + ": "
            + current.KEY);
            current = current.next;
        }
        console.stdOutLn("end of display");
    }

    public static String returnItemListAsString() {
        String s = "";

        MOUNT_TYPE current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static MOUNT_TYPE matchKey(int i) {
        MOUNT_TYPE O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static MOUNT_TYPE matchStr(String s) {
        MOUNT_TYPE O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final MOUNT_TYPE mountTypeNone = new MOUNT_TYPE("mountTypeNone");
    public static final MOUNT_TYPE mountTypeCustom = new MOUNT_TYPE("mountTypeCustom");
    // plain old equatorial mount where primary axis that rotates 360 deg aimed at pole
    public static final MOUNT_TYPE mountTypeEquatorial = new MOUNT_TYPE("mountTypeEquatorial");
    // plain old altazimuth mount where primary axis that rotates 360 deg aimed at local zenith
    public static final MOUNT_TYPE mountTypeAltazimuth = new MOUNT_TYPE("mountTypeAltazimuth");
    // altazimuth mount where where primary axis that rotates 360 deg aimed at horizon
    public static final MOUNT_TYPE mountTypeAltAlt = new MOUNT_TYPE("mountTypeAltAlt");
    // can swing to pole but cannot swing past pole
    public static final MOUNT_TYPE mountTypeHorseshoe = new MOUNT_TYPE("mountTypeHorseshoe");
    // can swing to and past pole into sub-polar region
    public static final MOUNT_TYPE mountTypeEquatorialFork = new MOUNT_TYPE("mountTypeEquatorialFork");
    // cannot swing near pole
    public static final MOUNT_TYPE mountTypeEquatorialYoke = new MOUNT_TYPE("mountTypeEquatorialYoke");
    // can track well past meridian when aimed toward celestial equator,
    // but cannot cross meridian while aimed at sub-polar region due to poleward support post
    public static final MOUNT_TYPE mountTypeCrossAxisEnglish = new MOUNT_TYPE("mountTypeCrossAxisEnglish");
    // can swing through pole, cannot rotate primary axis full circle in RA
    public static final MOUNT_TYPE mountTypeSplitRing = new MOUNT_TYPE("mountTypeSplitRing");
    // requires meridian flip
    public static final MOUNT_TYPE mountTypeGermanEquatorialMount = new MOUNT_TYPE("mountTypeGermanEquatorialMount");
    // no meridian flip, no pole support to impede crossing meridian while pointing underneath pole
    public static final MOUNT_TYPE mountTypeExtendedGerman = new MOUNT_TYPE("mountTypeExtendedGerman");
    // configured same as extended german
    public static final MOUNT_TYPE mountTypeOffAxisTorqueTube = new MOUNT_TYPE("mountTypeOffAxisTorqueTube");
    // per famous Zeiss example: dec and ota pivot on top of RA axis,
    // counterweights held by bars that are placed outside ota and hang down past pivot
    public static final MOUNT_TYPE mountTypeWeightStressCompensated = new MOUNT_TYPE("mountTypeWeightStressCompensated");
    // top of RA axis is split into fork that moves in dec, ota held by outside inverted fork that
    // fits over the RA fork, this outside fork also holds the counterweights for the ota
    public static final MOUNT_TYPE mountTypeInvertedFork = new MOUNT_TYPE("mountTypeInvertedFork");
    // means stationary star, tube horizontal pointed at pole, flat is mounted equatorially
    public static final MOUNT_TYPE mountTypeSiderostat = new MOUNT_TYPE("mountTypeSiderostat");
    // means stationary star, tube parallel to polar axis looking down into flat, flat is mounted equatorially
    public static final MOUNT_TYPE mountTypePolarSiderostat = new MOUNT_TYPE("mountTypePolarSiderostat");
    // tube horizontal pointed at pole, flat is mounted altazimuthly
    public static final MOUNT_TYPE mountTypeUranostat = new MOUNT_TYPE("mountTypeUranostat");
    // same as siderostat but looks at the Sun, only one axis of movement;
    public static final MOUNT_TYPE mountTypeHeliostat = new MOUNT_TYPE("mountTypeHeliostat");
    // same as heliostat but tube aimed up at polar axis
    public static final MOUNT_TYPE mountTypePolarHeliostat = new MOUNT_TYPE("mountTypePolarHeliostat");
    // developed from siderostat;
    // means stationary sky, siderostat mirror fixed parallel to polar axis, tube moves in dec;
    // plane mirror mounted facing the celestial equator on axis pointing to celestial pole,
    //    when driven around axis the reflected beam remains stationary and does not alter its orientation;
    // sometimes a 2nd mirror used to reflect the light into the telescope
    public static final MOUNT_TYPE mountTypeCoelostat = new MOUNT_TYPE("mountTypeCoelostat");
    // 2 mirrors, the upper rotates in Dec, the lower rotates in RA
    public static final MOUNT_TYPE mountTypeCoude = new MOUNT_TYPE("mountTypeCoude");
    // 2 diagonals produce stationary eyepiece, needs meridian flip
    public static final MOUNT_TYPE mountTypeSpringfield = new MOUNT_TYPE("mountTypeSpringfield");
}

