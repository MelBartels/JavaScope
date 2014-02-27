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
 * values returned and set from getall and setall commands and the methods to work on them
 */
public class SiTechCfg {
    
    /**
     * vars returned and set in a single get/set command 
     */
    
    // unsigned
    long XRampSpeed;
    long XVelocity;
    int XErrorLimit;
    int XPropBand;
    int XIntegral;
    int XDerivative;
    int XOutLimit;
    int XCurrentLimit;
    int XIntegralLimit;
    int XBits;
    long YRampSpeed;
    long YVelocity;
    int YErrorLimit;
    int YPropBand;
    int YIntegral;
    int YDerivative;
    int YOutLimit;
    int YCurrentLimit;
    int YIntegralLimit;
    int YBits;
    int ModuleAddress;

    //signed
    long PlatformTrackingRate;
    long PlatformUpDownAdjust;
    long PlatformGoal;
    int Latitude;
    long AzimuthEncoderTicksPerRev;
    long AltitudeEncoderTicksPerRev;
    long AzimuthMotorTicksPerRev;
    long AltitudeMotorTicksPerRev;
    long XSlewRate;
    long YSlewRate;
    long XPanRate;
    long YPanRate;
    long XGuideRate;
    long YGuideRate;

    // unsigned
    int PicServoCommTimeout;

    /**
     * array of bytes holding the values
     */
    
    static final int VALID_RETURN_BYTE_COUNT = 130;
    byte[] getAllBytes = new byte[VALID_RETURN_BYTE_COUNT];
    int getAllBytesIx;
    boolean getAllStatus;

    SiTechCfg() {}
    
    boolean getAll(IO io) {
        int count = 0;

        getAllStatus = false;
        getAllBytesIx = 0;
        if (io != null && io.portOpened()) {
            common.threadSleep(cfg.getInstance().servoPortWaitTimeMilliSecs);
            while (io.readSerialBuffer()) {
                getAllBytes[getAllBytesIx] = io.returnByteRead();
                count++;
                if (getAllBytesIx < VALID_RETURN_BYTE_COUNT)
                    getAllBytesIx++;
            }
            // 130 come back: 126 + 2 byte error checksum + lf+cr;
            // some values read bytes in reverse order: controller uses assembly and C vars, the C vars bytes are reversed;
            displayGetAllBytes();
            if (count == VALID_RETURN_BYTE_COUNT) {
                getAllStatus = true;

                getAllBytesIx = 0;

                XRampSpeed = extractLongFromByteArray();
                XVelocity = extractLongFromByteArray();
                XErrorLimit = extractIntFromByteArray();
                XPropBand = extractIntFromByteArray();
                XIntegral = extractIntFromByteArray();
                XDerivative = extractIntFromByteArray();
                XOutLimit = extractIntFromByteArray();
                XCurrentLimit = extractIntFromByteArray();
                XIntegralLimit = extractIntFromByteArray();
                XBits = extractIntFromByteArray();
                
                YRampSpeed = extractLongFromByteArray();
                YVelocity = extractLongFromByteArray();
                YErrorLimit = extractIntFromByteArray();
                YPropBand = extractIntFromByteArray();
                YIntegral = extractIntFromByteArray();
                YDerivative = extractIntFromByteArray();
                YOutLimit = extractIntFromByteArray();
                YCurrentLimit = extractIntFromByteArray();
                YIntegralLimit = extractIntFromByteArray();
                YBits = extractIntFromByteArray();

                ModuleAddress = extractIntFromByteArray();
                PlatformTrackingRate = extractSignedLongFromByteArray();
                PlatformUpDownAdjust = extractSignedLongFromByteArray();
                PlatformGoal = extractSignedLongFromByteArray();
                Latitude = extractSignedIntFromByteArrayReverseByteOrder();
                AzimuthEncoderTicksPerRev = extractSignedLongFromByteArrayReverseByteOrder();
                AltitudeEncoderTicksPerRev = extractSignedLongFromByteArrayReverseByteOrder();
                AzimuthMotorTicksPerRev = extractSignedLongFromByteArrayReverseByteOrder();
                AltitudeMotorTicksPerRev = extractSignedLongFromByteArrayReverseByteOrder();
                XSlewRate = extractSignedLongFromByteArray();
                YSlewRate = extractSignedLongFromByteArray();
                XPanRate = extractSignedLongFromByteArray();
                YPanRate = extractSignedLongFromByteArray();
                XGuideRate = extractSignedLongFromByteArray();
                YGuideRate = extractSignedLongFromByteArray();
                PicServoCommTimeout = extractIntFromByteArray();
            }
        }
        return getAllStatus;
    }
    
    void displayGetAllBytes() {
        int ix;

        console.stdOutLn("display of getAllBytes() array");
        for (ix = 0; ix < VALID_RETURN_BYTE_COUNT; ix++)
            console.stdOut(eString.intToString((int) getAllBytes[ix], 3) + "   ");
        console.stdOutLn("\nend display of getAllBytes() array");
    }

    int extractSignedIntFromByteArrayReverseByteOrder() {
        int ix;
        long l;
        long v = 0;

        for (ix = 0; ix < 2; ix++) {
            l = getAllBytes[getAllBytesIx++];
            if (l < 0)
                l+=256;
            // reverse the byte order
            v += l * eMath.longPow(256, 1 - ix);
        }
        if (v >= 32768)
            v -= 65536;
        return (int) v;
    }

    int extractIntFromByteArray() {
        int ix;
        long l;
        long v = 0;

        for (ix = 0; ix < 2; ix++) {
            l = getAllBytes[getAllBytesIx++];
            if (l < 0)
                l+=256;
            v += l * eMath.longPow(256, ix);
        }
        return (int) v;
    }

    long extractSignedLongFromByteArrayReverseByteOrder() {
        int ix;
        long l;
        long v = 0;

        for (ix = 0; ix < 4; ix++) {
            l = getAllBytes[getAllBytesIx++];
            if (l < 0)
                l+=256;
            // reverse the byte order
            v += l * eMath.longPow(256, 3 - ix);
        }
        if (v >= 2147483648L)
            v -= 4294967296L;
        return v;
    }

    long extractSignedLongFromByteArray() {
        int ix;
        long l;
        long v = 0;

        for (ix = 0; ix < 4; ix++) {
            l = getAllBytes[getAllBytesIx++];
            if (l < 0)
                l+=256;
            v += l * eMath.longPow(256, ix);
        }
        if (v >= 2147483648L)
            v -= 4294967296L;
        return v;
    }

    long extractLongFromByteArray() {
        int ix;
        long l;
        long v = 0;

        for (ix = 0; ix < 4; ix++) {
            l = getAllBytes[getAllBytesIx++];
            if (l < 0)
                l+=256;
            v += l * eMath.longPow(256, ix);
        }
        return v;
    }

    void longValueToOutBufReverseByteOrder(long l) {
        int a;
        int b;
        int c;
        int d;

        d = (int) (l/16777216);
        c = (int) ((l - d*16777216)/65536);
        b = (int) ((l - d*16777216 - c*65536)/256);
        a = (int) (l - d*16777216 - c*65536 - b*256);
        // reverse order
        getAllBytes[getAllBytesIx++] = (byte) d;
        getAllBytes[getAllBytesIx++] = (byte) c;
        getAllBytes[getAllBytesIx++] = (byte) b;
        getAllBytes[getAllBytesIx++] = (byte) a;
    }

    void longValueToOutBuf(long l) {
        int a;
        int b;
        int c;
        int d;

        d = (int) (l/16777216);
        c = (int) ((l - d*16777216)/65536);
        b = (int) ((l - d*16777216 - c*65536)/256);
        a = (int) (l - d*16777216 - c*65536 - b*256);
        getAllBytes[getAllBytesIx++] = (byte) a;
        getAllBytes[getAllBytesIx++] = (byte) b;
        getAllBytes[getAllBytesIx++] = (byte) c;
        getAllBytes[getAllBytesIx++] = (byte) d;
    }

    void intValueToGetAllBytesReverseByteOrder(int l) {
        int a;
        int b;

        b = l/256;
        a = l - b*256;
        // reverse the order
        getAllBytes[getAllBytesIx++] = (byte) b;
        getAllBytes[getAllBytesIx++] = (byte) a;
    }

    void intValueToGetAllBytes(int l) {
        int a;
        int b;

        b = l/256;
        a = l - b*256;
        getAllBytes[getAllBytesIx++] = (byte) a;
        getAllBytes[getAllBytesIx++] = (byte) b;
    }

    void zeroOutRemainingBytes() {
        for ( ; getAllBytesIx < VALID_RETURN_BYTE_COUNT; getAllBytesIx++)
            getAllBytes[getAllBytesIx] = 0;
    }

    int getChkSum() {
        int i;
        int chkSum = 0;
        
        for (getAllBytesIx = 0; getAllBytesIx < VALID_RETURN_BYTE_COUNT; getAllBytesIx++) {
            i = (int) getAllBytes[getAllBytesIx];
            if (i < 0)
                i += 256;
            chkSum += i;
        }
        return chkSum;
    }
    
    void fillByteArray(byte[] fill, int startX) {
        for (getAllBytesIx = 0; getAllBytesIx < SiTechCfg.VALID_RETURN_BYTE_COUNT-2; getAllBytesIx++)
            fill[getAllBytesIx+startX] = getAllBytes[getAllBytesIx];
    }

     boolean extractSignedLongFromTextFieldReverseByteOrder(javax.swing.JTextField jTextField) {
        long l;
        double value = 0;

        try {
            value = Double.parseDouble(jTextField.getText());
            l = (long) value;
            if (l < 0)
                l += 4294967296L;
            longValueToOutBufReverseByteOrder(l);
            return true;
        } 
        catch (NumberFormatException nfe) {
            console.errOut("invalid number in SiTechCfg.extractSignedLongFromTextField(): " + value);
        }
        return false;
    }

     boolean extractSignedLongFromTextField(javax.swing.JTextField jTextField) {
        long l;
        double value = 0;

        try {
            value = Double.parseDouble(jTextField.getText());
            l = (long) value;
            if (l < 0)
                l += 4294967296L;
            longValueToOutBuf(l);
            return true;
        }
        catch (NumberFormatException nfe) {
            console.errOut("invalid number in SiTechCfg.extractSignedLongFromTextField(): " + value);
        }
        return false;
    }

     boolean extractLongFromTextField(javax.swing.JTextField jTextField) {
        double value = 0;

        try {
            value = Double.parseDouble(jTextField.getText());
            longValueToOutBuf((long) value);
            return true;
        }
        catch (NumberFormatException nfe) {
            console.errOut("invalid number in SiTechCfg.extractLongFromTextField(): " + value);
        }
        return false;
    }

     boolean extractIntFromTextField(javax.swing.JTextField jTextField) {
        double value = 0;

        try {
            value = Double.parseDouble(jTextField.getText());
            intValueToGetAllBytes((int) value);
            return true;
        }
        catch (NumberFormatException nfe) {
            console.errOut("invalid number in SiTechCfg.extractIntFromTextField(): " + value);
        }
        return false;
    }
}

