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
 * uses the home position from the servo controller's status return per SiTech's extension of the JRKerr PICServo protocol
 */
public class encoderSiTech extends encoderBase implements Encoders {
    encoderSiTech() {
        resetNeeded = false;
    }

    public boolean openPort(IO_TYPE ioType,
    String serialPortName,
    int serialBaudRate,
    int homeIPPort,
    String remoteIPName,
    int remoteIPPort,
    String fileLocation,
    boolean trace) {
        console.stdOutLn("opening servo controller encoder port");
        portOpened = true;
        return portOpened;
    }

    public void close() {
        console.stdOutLn("closing servo controller encoder port");
        portOpened = false;
    }

    public boolean reset() {
        resetNeeded = false;
        return true;
    }

    public void query() {
        if (portOpened)
            queryCount++;
    }

    public boolean queryAndRead() {
        queryAndReadSuccess = false;
        if (portOpened) {

            if (resetNeeded)
                if (!reset())
                    console.errOut("unable to reset encoders");

            if (!resetNeeded) {
                query();
                if (read()) {
                    readCount++;
                    queryAndReadSuccess = true;
                    adjustForEncodersDir();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean read() {
        if (cfg.getInstance().spa.readStatusSuccessful
        && cfg.getInstance().spz.readStatusSuccessful
        && ((cfg.getInstance().spa.lastDefinedStatus & servo.SEND_HOME_POSITION) == servo.SEND_HOME_POSITION)
        && ((cfg.getInstance().spz.lastDefinedStatus & servo.SEND_HOME_POSITION) == servo.SEND_HOME_POSITION)) {

            altDecCount = (int) cfg.getInstance().spa.homePosition;
            azRaCount = (int) cfg.getInstance().spz.homePosition;
            return true;
        }
        return false;
    }
}

