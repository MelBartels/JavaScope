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
 * encoder methods that must be implemented
 */
public interface Encoders {
    // methods in encoderBase to be exposed through the interface
    void encoderType(ENCODER_TYPE encoderType);
    ENCODER_TYPE encoderType();
    void addEncoderReset(ENCODER_RESET_TYPE ert, double scopeA, double scopeZ, double encoderA, double encoderZ, double sidT);
    void saveEncodersResetLog();

    boolean openPort(IO_TYPE ioType,
    String serialPortName,
    int serialBaudRate,
    int homeIPPort,
    String remoteIPName,
    int remoteIPPort,
    String fileLocation,
    boolean trace);

    IO io();
    void portOpened(boolean portOpened);
    boolean portOpened();
    void close();
    void setResetNeeded(boolean resetNeeded);
    int getQueryCount();
    int getReadCount();
    void query();
    boolean queryAndRead();
    boolean getQueryAndReadSuccess();
    int getAltDecCount();
    int getAzRaCount();
    double getAltDecRad();
    double getAzRaRad();
    String buildCountsString();
    String buildPositionsString();
    void displayCounts();
    void displayPositions();
    void setNewOffsetsToBeCalculated(boolean newValue);
    void processPositions();
    boolean readEncodersAndProcessPositions();
    // overridden for Tangent type encoders
    void adjustForEncodersDir();
    // methods to be individually implemented for each encoder type; must also set name and queryByte in each constructor
    boolean reset();
    boolean read();
}

