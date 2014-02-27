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
 * for Dave Ek unit:
 * uses straight through cable;
 * to set: transmit 'z' followed by 4 bytes: dec enc. low byte, dec enc. high byte, RA enc. low byte, RA enc. high byte;
 *    expect 'r' in return;
 * to read: transmit 'y', receive 4 bytes of enc. Positions per byte order above
 */
public class encoderDaveEk extends encoderBase implements Encoders {
    encoderDaveEk() {
        queryByte = (byte) 'y';
    }

    public boolean reset() {
        byte buf[] = new byte[5];
        byte ret;

        resetNeeded = true;
        if (portOpened) {
            buf[0] = (byte) 'z';

            buf[2] = (byte) (cfg.getInstance().encoderAltDecCountsPerRev/256);
            buf[1] = (byte) (cfg.getInstance().encoderAltDecCountsPerRev - buf[2]*256);

            buf[4] = (byte) (cfg.getInstance().encoderAzRaCountsPerRev/256);
            buf[3] = (byte) (cfg.getInstance().encoderAzRaCountsPerRev - buf[4]*256);

            io.writeByteArrayPauseUntilXmtFinished(buf, 5);
            ret = 0;
            io.waitForReadBytes(1, MILLI_SEC_WAIT);
            while (io.readSerialBuffer())
                ret = io.returnByteRead();
            if (ret == (byte) 'r')
                resetNeeded = false;
        }
        return !resetNeeded;
    }

    public boolean read() {
        int l, h;

        if (portOpened) {
            io.waitForReadBytes(4, MILLI_SEC_WAIT);
            if (io.countReadBytes() == 4) {
                l = io.readByteReturnUnsignedInt();
                h = io.readByteReturnUnsignedInt();
                altDecCount = h*256 + l;

                l = io.readByteReturnUnsignedInt();
                h = io.readByteReturnUnsignedInt();
                azRaCount = h*256 + l;

                return true;
            }
        }
        return false;
    }
}

