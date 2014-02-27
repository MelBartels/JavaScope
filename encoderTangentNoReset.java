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
 * for Tangent types such as MicroGuider, Ouranos, BBox, NGC models, Celestron Advanced AstroMaster, Lumicon, SkyWizard, and StarPort units:
 * transmit 'Q', receive '+12345<tab>+67890<return>' = total of 14 chars;
 * +12345 = azimuth, <tab> = decimal 9, +67890 = altitude, <return> = decimal 13;
 *
 * if 4000 count encoders, then output is from -2000 to +1999, representing -180 to 180 degrees;
 *
 * models MicroGuider, Ouranos, NGC, Astromaster:
 * change resolution by outputting 'Rxxxxx<tab>yyyyy<return>' where xxxxx is azimuth and yyyyy is altitude,
 * if resolution > 32767 then counts returned as unsigned, counts go from 0 to resolution-1, max resolution is 65534,
 * if command is successful, receive 'R'
 *
 * some models BBox, NGC-MAX (according to Bisque):
 * set resolution by outputting 'Zxxxxx<space>yyyyy<return>' (some docs indicate 'z<space>xxxxx<space>yyyyy') where xxxxx
 * is resolution in x axis and yyyyy is resolution in y axis;
 * properly formatted 'Z' command returns "*";
 *
 * check current configuration by sending 'h', unit will respond by sending current configuration
 *
 * some models cannot set resolution (Celestron Advanced AstroMaster, NGC models)
 *
 * use straight through cable
 */
public class encoderTangentNoReset extends encoderBase implements Encoders {
    byte resetByte;
    // reset needs 13 bytes and read needs 14 bytes
    byte buf[] = new byte[14];

    encoderTangentNoReset() {
        queryByte = (byte) 'Q';
    }

    void buildBuf(int azRaIx, int altDecIx) {
        int ix;
        int res;
        int factor;

        for (ix=azRaIx, res=cfg.getInstance().encoderAzRaCountsPerRev, factor=10000; ix<azRaIx+5; ix++, res-=((res/factor)*factor), factor/=10)
            buf[ix] = (byte) ('0' + res/factor);

        for (ix=altDecIx, res=cfg.getInstance().encoderAltDecCountsPerRev, factor=10000; ix<altDecIx+5; ix++, res-=((res/factor)*factor), factor/=10)
            buf[ix] = (byte) ('0' + res/factor);
    }

    public boolean reset() {
        byte ret;

        resetNeeded = true;
        if (portOpened) {
            if (resetByte == (byte) 'R') {
                buf[0] = resetByte;
                buf[6] = eString.TAB;
                buf[12] = eString.RETURN;
                buildBuf(1, 7);
                io.writeByteArrayPauseUntilXmtFinished(buf, 13);
                ret = 0;
                io.waitForReadBytes(1, MILLI_SEC_WAIT);
                while (io.readSerialBuffer())
                    ret = io.returnByteRead();
                if (ret == (byte) 'R')
                    resetNeeded = false;
            }
            else
                if (resetByte == (byte) 'Z') {
                    buf[0] = resetByte;
                    buf[6] = (byte) ' ';
                    buf[12] = eString.RETURN;
                    buildBuf(1, 7);
                    io.writeByteArrayPauseUntilXmtFinished(buf, 13);
                    ret = 0;
                    io.waitForReadBytes(1, MILLI_SEC_WAIT);
                    while (io.readSerialBuffer())
                        ret = io.returnByteRead();
                    if (ret == (byte) '*')
                        resetNeeded = false;
                }
                else
                    resetNeeded = false;
        }
        return !resetNeeded;
    }

    public boolean read() {
        int ix;

        if (portOpened) {
            io.waitForReadBytes(14, MILLI_SEC_WAIT);
            if (io.countReadBytes() == 14) {
                ix = 0;
                while (ix < 14) {
                    io.readSerialBuffer();
                    buf[ix++] = (byte) io.returnByteRead();
                }
                if (ix == 14
                && (buf[6] == (byte) eString.TAB || buf[6] == (byte) ' ')
                && buf[13] == (byte) eString.RETURN) {
                    azRaCount = 0;
                    for (ix = 1; ix < 6; ix++)
                        azRaCount += (int) ((buf[ix]-'0') * eMath.intPow(10, 5-ix));
                    if (buf[0] == (byte) '-')
                        azRaCount = -azRaCount;

                    altDecCount = 0;
                    for (ix = 8; ix < 13; ix++)
                        altDecCount += (int) ((buf[ix]-'0') * eMath.intPow(10, 12-ix));
                    if (buf[7] == (byte) '-')
                        altDecCount = -altDecCount;

                    return true;
                }
            }
        }
        return false;
    }

    /**
     * override encoderBase method;
     * if encoders count up in opposite direction: if encoders range from minus to positive values, then reverse sign,
     * otherwise if encoders range from zero to maxvalue, subtract count from maxvalue
     */
    public void adjustForEncodersDir() {
        if (cfg.getInstance().encoderAzRaDir == ROTATION.CCW)
            if (resetByte == (byte) 'R' && cfg.getInstance().encoderAzRaCountsPerRev <= 32767)
                azRaCount = -azRaCount;
            else
                azRaCount = cfg.getInstance().encoderAzRaCountsPerRev - azRaCount;

        if (cfg.getInstance().encoderAltDecDir == ROTATION.CCW)
            if (resetByte == (byte) 'R' && cfg.getInstance().encoderAltDecCountsPerRev <= 32767)
                altDecCount = -altDecCount;
            else
                altDecCount = cfg.getInstance().encoderAltDecCountsPerRev - altDecCount;
    }
}

