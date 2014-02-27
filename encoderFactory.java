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
 * encoder class factory
 */
public class encoderFactory {
    Encoders E;

    // returns pointer to encoders object if successful, otherwise returns null
    Encoders build(ENCODER_TYPE encoderType,
    IO_TYPE ioType,
    String serialPortName,
    int serialBaudRate,
    int homeIPPort,
    String remoteIPName,
    int remoteIPPort,
    String fileLocation,
    boolean trace) {
        if (encoderType == ENCODER_TYPE.encoderNone)
            console.stdOutLn("no encoders specified");
        else {
            try {
                // create new class by class name, use it to create new object instance, then return correct cast
                E = (Encoders) Class.forName(encoderType.toString()).newInstance();
            }
            catch (ClassNotFoundException cnfe) {
                console.errOut(cnfe.toString());
            }
            catch (InstantiationException ie) {
                console.errOut(ie.toString());
            }
            catch (IllegalAccessException iae) {
                console.errOut(iae.toString());
            }
            catch (NullPointerException npe) {
                console.errOut(npe.toString());
            }
            if (E == null)
                console.errOut("unhandled encoderType: " + encoderType);
            else {
                E.encoderType(encoderType);
                console.stdOutLn("built encoders object: " + E.encoderType());
                openAndRead(ioType,
                serialPortName,
                serialBaudRate,
                homeIPPort,
                remoteIPName,
                remoteIPPort,
                fileLocation,
                trace);
            }
        }
        return E;
    }

    // for testing of encoder factory only
    Encoders build(ENCODER_TYPE encoderType) {
        if (encoderType == ENCODER_TYPE.encoderNone)
            console.stdOutLn("no encoders specified");
        else {
            try {
                // create new class by class name, use it to create new object instance, then return correct cast
                E = (Encoders) Class.forName(encoderType.toString()).newInstance();
            }
            catch (ClassNotFoundException cnfe) {
                console.errOut(cnfe.toString());
            }
            catch (InstantiationException ie) {
                console.errOut(ie.toString());
            }
            catch (IllegalAccessException iae) {
                console.errOut(iae.toString());
            }
            if (E == null)
                console.errOut("unhandled encoderType: " + encoderType);
            else {
                E.encoderType(encoderType);
                console.stdOutLn("built encoders object: " + E.encoderType());
            }
        }
        return E;
    }

    boolean openAndRead(IO_TYPE ioType,
    String serialPortName,
    int serialBaudRate,
    int homeIPPort,
    String remoteIPName,
    int remoteIPPort,
    String fileLocation,
    boolean trace) {
        if (E != null
        && E.openPort(ioType,
                    serialPortName,
                    serialBaudRate,
                    homeIPPort,
                    remoteIPName,
                    remoteIPPort,
                    fileLocation,
                    trace))
            return E.queryAndRead();

        console.errOut("could not startup encoders");
        return false;
    }
}

