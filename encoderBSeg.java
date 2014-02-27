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
 * for BSeg unit:
 * transmit 'B' for native mode, receive 'B'<return> if successful;
 * transmit 'Q', receive 'HHHH<tab>HHHH<tab>HHHH<return> where HHHH is an unsigned hexidecimal number;
 * set encoder resolution via jumpers on BSeg PCB;
 *                          1111   1
 * index: 0123  4  5678  9  0123   4
 * char:  HHHH<tab>HHHH<tab>HHHH<return>
 */
public class encoderBSeg extends encoderBase implements Encoders {
    int thirdAxisCount;

    encoderBSeg() {
        queryByte = (byte) 'Q';
    }

    public boolean reset() {
        byte ret;

        resetNeeded = true;
        if (portOpened) {
            io.writeBytePauseUntilXmtFinished((byte) 'B');
            ret = 0;
            io.waitForReadBytes(1, MILLI_SEC_WAIT);
            while (io.readSerialBuffer())
                ret = io.returnByteRead();
            if (ret == (byte) 'B')
                resetNeeded = false;
        }
        return !resetNeeded;
    }

    public boolean read() {
        int ix;
        int mult;
        int x = 0;
        int y = 0;
        int z = 0;

        if (portOpened) {
            io.waitForReadBytes(15, MILLI_SEC_WAIT);
            if (io.countReadBytes() == 15) {
                for (ix=0, mult=1; ix<4; ix++, mult*=16)
                    x += io.readByteReturnUnsignedInt() * mult;

                io.readSerialBuffer();
                if (io.returnByteRead() == (byte) eString.TAB) {
                    for (ix=0, mult=1; ix<4; ix++, mult*=16)
                        y += io.readByteReturnUnsignedInt() * mult;

                    io.readSerialBuffer();
                    if (io.returnByteRead() == (byte) eString.TAB) {
                        for (ix=0, mult=1; ix<4; ix++, mult*=16)
                            z += io.readByteReturnUnsignedInt() * mult;

                        io.readSerialBuffer();
                        if (io.returnByteRead() == (byte) eString.RETURN) {
                            azRaCount = x;
                            altDecCount = y;
                            thirdAxisCount = z;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}

