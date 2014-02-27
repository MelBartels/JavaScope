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
 * Sky Commander: transmit 'e'; receive az most significant byte, az least significant byte, alt most
 * significant byte, alt least significant byte
 */
public class encoderSkyCommander extends encoderBase implements Encoders {
    encoderSkyCommander() {
        queryByte = (byte) 'e';
        resetNeeded = false;
    }

    public boolean reset() {
        resetNeeded = false;
        return true;
    }

    public boolean read() {
        int l, h;

        if (portOpened) {
            io.waitForReadBytes(4, MILLI_SEC_WAIT);
            if (io.countReadBytes() == 4) {
                h = io.readByteReturnUnsignedInt();
                l = io.readByteReturnUnsignedInt();
                azRaCount = h*256 + l;

                h = io.readByteReturnUnsignedInt();
                l = io.readByteReturnUnsignedInt();
                altDecCount = h*256 + l;

                return true;
            }
        }
        return false;
    }
}

