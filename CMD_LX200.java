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

public final class CMD_LX200 {
    private String id;
    public final int KEY;
    private CMD_LX200 prev;
    private CMD_LX200 next;

    private static int itemCount;
    private static CMD_LX200 first;
    private static CMD_LX200 last;

    private CMD_LX200(String id) {
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
            private CMD_LX200 current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                CMD_LX200 c = current;
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

    public static CMD_LX200 first() {
        return first;
    }

    public static CMD_LX200 last() {
        return last;
    }

    public CMD_LX200 prev() {
        return this.prev;
    }

    public CMD_LX200 next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of CMD_LX200, which has "
        + itemCount
        + " elements:");
        CMD_LX200 current = first;
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

        CMD_LX200 current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static CMD_LX200 matchKey(int i) {
        CMD_LX200 O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static CMD_LX200 matchStr(String s) {
        CMD_LX200 O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final CMD_LX200 ack = new CMD_LX200("ack");
    public static final CMD_LX200 alignAltaz = new CMD_LX200("alignAltaz");
    public static final CMD_LX200 alignACK = new CMD_LX200("alignACK");
    public static final CMD_LX200 alignLand = new CMD_LX200("alignLand");
    public static final CMD_LX200 alignPolar = new CMD_LX200("alignPolar");
    public static final CMD_LX200 reticle = new CMD_LX200("reticle");
    public static final CMD_LX200 distance = new CMD_LX200("distance");
    public static final CMD_LX200 focusOut = new CMD_LX200("focusOut");
    public static final CMD_LX200 focusIn = new CMD_LX200("focusIn");
    public static final CMD_LX200 focusQuit = new CMD_LX200("focusQuit");
    public static final CMD_LX200 focusSetFast = new CMD_LX200("focusSetFast");
    public static final CMD_LX200 focusSetSlow = new CMD_LX200("focusSetSlow");
    public static final CMD_LX200 getASCOMmountType = new CMD_LX200("getASCOMmountType");
    public static final CMD_LX200 getRa = new CMD_LX200("getRa");
    public static final CMD_LX200 getDec = new CMD_LX200("getDec");
    public static final CMD_LX200 getAlt = new CMD_LX200("getAlt");
    public static final CMD_LX200 getAz = new CMD_LX200("getAz");
    public static final CMD_LX200 getSidT = new CMD_LX200("getSidT");
    public static final CMD_LX200 getLocalT24 = new CMD_LX200("getLocalT24");
    public static final CMD_LX200 getLocalT12 = new CMD_LX200("getLocalT12");
    public static final CMD_LX200 getSiteName = new CMD_LX200("getSiteName");
    public static final CMD_LX200 getMinQualityFind = new CMD_LX200("getMinQualityFind");
    public static final CMD_LX200 getDate = new CMD_LX200("getDate");
    public static final CMD_LX200 getClockStatus = new CMD_LX200("getClockStatus");
    public static final CMD_LX200 getLat = new CMD_LX200("getLat");
    public static final CMD_LX200 getLongitude = new CMD_LX200("getLongitude");
    public static final CMD_LX200 getTz = new CMD_LX200("getTz");
    public static final CMD_LX200 getField = new CMD_LX200("getField");
    public static final CMD_LX200 getFocusFastSpeedDegSec = new CMD_LX200("getFocusFastSpeedDegSec");
    public static final CMD_LX200 getFocusSlowSpeedArcsecSec = new CMD_LX200("getFocusSlowSpeedArcsecSec");
    public static final CMD_LX200 getFocusPos = new CMD_LX200("getFocusPos");
    public static final CMD_LX200 timeQuartz = new CMD_LX200("timeQuartz");
    public static final CMD_LX200 liCommand = new CMD_LX200("LI");
    public static final CMD_LX200 setNGCLibrary = new CMD_LX200("setNGCLibrary");
    public static final CMD_LX200 setStarLibrary = new CMD_LX200("setStarLibrary");
    public static final CMD_LX200 nudgeGuideNorth = new CMD_LX200("nudgeGuideNorth");
    public static final CMD_LX200 nudgeGuideSouth = new CMD_LX200("nudgeGuideSouth");
    public static final CMD_LX200 nudgeGuideEast = new CMD_LX200("nudgeGuideEast");
    public static final CMD_LX200 nudgeGuideWest = new CMD_LX200("nudgeGuideWest");
    public static final CMD_LX200 moveDirRateNorth = new CMD_LX200("moveDirRateNorth");
    public static final CMD_LX200 moveDirRateSouth = new CMD_LX200("moveDirRateSouth");
    public static final CMD_LX200 moveDirRateEast = new CMD_LX200("moveDirRateEast");
    public static final CMD_LX200 moveDirRateWest = new CMD_LX200("moveDirRateWest");
    public static final CMD_LX200 startSlew = new CMD_LX200("startSlew");
    public static final CMD_LX200 stopSlew = new CMD_LX200("stopSlew");
    public static final CMD_LX200 stopMotionNorth = new CMD_LX200("stopMotionNorth");
    public static final CMD_LX200 stopMotionSouth = new CMD_LX200("stopMotionSouth");
    public static final CMD_LX200 stopMotionEast = new CMD_LX200("stopMotionEast");
    public static final CMD_LX200 stopMotionWest = new CMD_LX200("stopMotionWest");
    public static final CMD_LX200 setMotionRateGuide = new CMD_LX200("setMotionRateGuide");
    public static final CMD_LX200 setMotionRateCenter = new CMD_LX200("setMotionRateCenter");
    public static final CMD_LX200 setMotionRateFind = new CMD_LX200("setMotionRateFind");
    public static final CMD_LX200 setMotionRateSlew = new CMD_LX200("setMotionRateSlew");
    public static final CMD_LX200 setRate = new CMD_LX200("setRate");
    public static final CMD_LX200 setRa = new CMD_LX200("setRa");
    public static final CMD_LX200 setDec = new CMD_LX200("setDec");
    public static final CMD_LX200 setField = new CMD_LX200("setField");
    public static final CMD_LX200 setFocusFastSpeedDegSec = new CMD_LX200("setFocusFastSpeedDegSec");
    public static final CMD_LX200 setFocusSlowSpeedArcsecSec = new CMD_LX200("setFocusSlowSpeedArcsecSec");
    public static final CMD_LX200 setCurrentHigherLimit = new CMD_LX200("setCurrentHigherLimit");
    public static final CMD_LX200 setSidT = new CMD_LX200("setSidT");
    public static final CMD_LX200 setLocalT = new CMD_LX200("setLocalT");
    public static final CMD_LX200 setDate = new CMD_LX200("setDate");
    public static final CMD_LX200 setGMTOffset = new CMD_LX200("setGMTOffset");
    public static final CMD_LX200 setSiteNumber_S = new CMD_LX200("setSiteNumber_S");
    public static final CMD_LX200 setLat = new CMD_LX200("setLat");
    public static final CMD_LX200 setLongitude = new CMD_LX200("setLongitude");
    public static final CMD_LX200 setBrightMagLimitFind = new CMD_LX200("setBrightMagLimitFind");
    public static final CMD_LX200 setFaintMagLimitFind = new CMD_LX200("setFaintMagLimitFind");
    public static final CMD_LX200 largeSizeLimitFind = new CMD_LX200("largeSizeLimitFind");
    public static final CMD_LX200 smallSizeLimitFind = new CMD_LX200("smallSizeLimitFind");
    public static final CMD_LX200 nextMinQualityFind = new CMD_LX200("nextMinQualityFind");
    public static final CMD_LX200 setTypeStringForFind = new CMD_LX200("setTypeStringForFind");
    public static final CMD_LX200 swCommand = new CMD_LX200("swCommand");
    public static final CMD_LX200 sync = new CMD_LX200("sync");
    public static final CMD_LX200 setSiteNumber_W = new CMD_LX200("setSiteNumber_W");
    public static final CMD_LX200 toggleLongFormat = new CMD_LX200("toggleLongFormat");
    public static final CMD_LX200 getFirmwareIDString = new CMD_LX200("getFirmwareIDString");
    public static final CMD_LX200 getFirmwareDate = new CMD_LX200("getFirmwareDate");    

    public static final CMD_LX200 getProductName = new CMD_LX200("getProductName");
    public static final CMD_LX200 getGuideArcsecSec = new CMD_LX200("getGuideArcsecSec");
    public static final CMD_LX200 setGuideArcsecSec = new CMD_LX200("setGuideArcsecSec");
    public static final CMD_LX200 setHandpadMode = new CMD_LX200("setHandpadMode");
    public static final CMD_LX200 handpadLeftKey = new CMD_LX200("handpadLeftKey");
    public static final CMD_LX200 handpadRightKey = new CMD_LX200("handpadRightKey");
    public static final CMD_LX200 setInit1 = new CMD_LX200("setInit1");
    public static final CMD_LX200 setInit2 = new CMD_LX200("setInit2");
    public static final CMD_LX200 setInit3 = new CMD_LX200("setInit3");
    public static final CMD_LX200 sendFieldR  = new CMD_LX200("sendFieldR");
    public static final CMD_LX200 setObjectName = new CMD_LX200("setObjectName");
    public static final CMD_LX200 PECOnOff = new CMD_LX200("PECOnOff");
    public static final CMD_LX200 slewHomePosition = new CMD_LX200("slewHomePosition");
    public static final CMD_LX200 FROn = new CMD_LX200("FROn");
    public static final CMD_LX200 FROff = new CMD_LX200("FROff");
    public static final CMD_LX200 clearDisplay = new CMD_LX200("clearDisplay");
    public static final CMD_LX200 stringCommand = new CMD_LX200("stringCommand");

    public static final CMD_LX200 placeHolder = new CMD_LX200("placeHolder");
    public static final CMD_LX200 unknown = new CMD_LX200("unknown");
}

