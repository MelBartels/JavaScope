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
 * common methods and properties for all encoder classes
 *
 * Integer.MAX_VALUE = 2147483647 so ok to use int for counts per encoder shaft revolution
 * cfg vars to work with and their defaults:
 * encoderType               encoderDaveEk
 * encoderSerialPortName     COM1
 * encoderBaudRate           9600
 * encoderAltDecCountsPerRev 8096
 * encoderAzRaCountsPerRev   8096
 * encoderAltDecDir          CW
 * encoderAzRaDir            CW
 * encoderFieldRDir          CW
 * encoderFocusDir           CW
 * encoderErrorThresholdDeg  1
 * encoderAltDecOffset       0.0
 * encoderAzRaOffset         0.0
 */
public class encoderBase {
    private ENCODER_TYPE encoderType;
    static final int MILLI_SEC_WAIT = 250;
    static final int UPDATE_ENCODERS_TIME_SEC = 2;
    byte queryByte;
    java.util.List listEncoderReset;
    boolean portOpened;
    IO io;

    int queryCount;
    int readCount;

    boolean resetNeeded;
    boolean queryAndReadSuccess;
    private boolean newOffsetsToBeCalculated;

    int altDecCount;
    int azRaCount;

    double altDecRad;
    double azRaRad;

    String countsString;
    String positionsString;

    encoderBase() {
        listEncoderReset = new ArrayList();
        resetNeeded = true;
        newOffsetsToBeCalculated = true;
        queryAndReadSuccess = false;
        portOpened = false;
        queryCount = 0;
        readCount = 0;
    }

    public void encoderType(ENCODER_TYPE encoderType) {
        this.encoderType = encoderType;
    }

    public ENCODER_TYPE encoderType() {
        return encoderType;
    }

    public void addEncoderReset(ENCODER_RESET_TYPE ert, double scopeA, double scopeZ, double encoderA, double encoderZ, double sidT) {
        listEncoderReset.add( new encoderReset());
        encoderReset er = (encoderReset) listEncoderReset.get(listEncoderReset.size()-1);
        er.encoderResetType = ert;
        er.scope.a = scopeA;
        er.scope.z = scopeZ;
        er.encoders.a = encoderA;
        er.encoders.z = encoderZ;
        er.sidT = sidT;

    }

    public void saveEncodersResetLog() {
        PrintStream output;
        Iterator it;
        encoderReset er;

        String filename = eString.PGM_NAME + eString.ENCODER_RESET_LOG_EXT;

        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(filename)));
            it = listEncoderReset.iterator();
            while (it.hasNext()) {
                er = (encoderReset) it.next();

                output.println(er.encoderResetType
                + " scope (deg): "
                + eString.padString(eString.doubleToStringNoGrouping(er.scope.a*units.RAD_TO_DEG, 3, 3), 9)
                + " "
                + eString.padString(eString.doubleToStringNoGrouping(er.scope.z*units.RAD_TO_DEG, 3, 3), 9)
                + " encoders (deg): "
                + eString.padString(eString.doubleToStringNoGrouping(er.encoders.a*units.RAD_TO_DEG, 3, 3), 9)
                + " "
                + eString.padString(eString.doubleToStringNoGrouping(er.encoders.z*units.RAD_TO_DEG, 3, 3), 9)
                + " sidT (hr): "
                + eString.doubleToStringNoGrouping(er.sidT*units.RAD_TO_HR, 3, 3));
            }
            output.close();
        }
        catch (IOException ioe) {
            console.errOut("could not open " + filename);
        }

    }

    public boolean openPort(IO_TYPE ioType,
    String serialPortName,
    int serialBaudRate,
    int homeIPPort,
    String remoteIPName,
    int remoteIPPort,
    String fileLocation,
    boolean trace) {
        ioFactory iof = new ioFactory();

        console.stdOutLn("opening encoder port");
        iof.args.serialPortName = serialPortName;
        iof.args.baudRate = serialBaudRate;
        iof.args.homeIPPort = homeIPPort;
        iof.args.remoteIPName = remoteIPName;
        iof.args.fileLocation = fileLocation;
        iof.args.remoteIPPort = remoteIPPort;

        io = iof.build(ioType);
        if (io == null) {
            portOpened = false;
            console.errOut("could not open encoder port");
        }
        else
            portOpened = true;
        return portOpened;
    }

    public IO io() {
        return io;
    }

    public void portOpened(boolean portOpened) {
        this.portOpened = portOpened;
    }

    public boolean portOpened() {
        return portOpened;
    }

    public void close() {
        if (portOpened) {
            console.stdOutLn("closing encoder port...");
            io.close();
        }
    }

    public void setResetNeeded(boolean resetNeeded) {
        this.resetNeeded = resetNeeded;
    }

    public int getQueryCount() {
        return queryCount;
    }

    public int getReadCount() {
        return readCount;
    }

    public void query() {
        if (portOpened) {
            io.writeBytePauseUntilXmtFinished(queryByte);
            queryCount++;
        }
    }

    public boolean queryAndRead() {
        queryAndReadSuccess = false;
        if (portOpened) {
            checkMiscChars();

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

    public boolean getQueryAndReadSuccess() {
        return queryAndReadSuccess;
    }

    public int getAltDecCount() {
        return altDecCount;
    }

    public int getAzRaCount() {
        return azRaCount;
    }

    public double getAltDecRad() {
        return altDecRad;
    }

    public double getAzRaRad() {
        return azRaRad;
    }

    public String buildCountsString() {
        countsString = "altDecCount "
        + altDecCount
        + "   azRaCount "
        + azRaCount;
        return countsString;
    }

    public String buildPositionsString() {
        positionsString = "Alt: "
        + eString.doubleToStringNoGrouping(altDecRad*units.RAD_TO_DEG, 3, 2)
        + " Az: "
        + eString.doubleToStringNoGrouping(azRaRad*units.RAD_TO_DEG, 3, 2)
        + " deg";
        return positionsString;
    }

    public void displayCounts() {
        console.stdOutLn(buildCountsString());
    }

    public void displayPositions() {
        console.stdOutLn(buildPositionsString());
    }

    public void setNewOffsetsToBeCalculated(boolean newValue) {
        newOffsetsToBeCalculated = newValue;
    }

    public void processPositions() {
        // setCurrentAltazToServoPosition() sets cfg.getInstance().current.alt.rad, cfg.getInstance().current.az.rad
        if (newOffsetsToBeCalculated)
            setNewOffset();
        calcPositions();
    }

    public boolean readEncodersAndProcessPositions() {
        if (portOpened)
            if (queryAndRead()) {
                processPositions();
                //displayCounts();
                //displayPositions();
                return true;
            }
        return false;
    }

    public void adjustForEncodersDir() {
        if (cfg.getInstance().encoderAltDecDir == ROTATION.CCW)
            altDecCount = cfg.getInstance().encoderAltDecCountsPerRev - altDecCount;
        if (cfg.getInstance().encoderAzRaDir == ROTATION.CCW)
            azRaCount = cfg.getInstance().encoderAzRaCountsPerRev - azRaCount;
    }

    public boolean reset() {
        return true;
    }

    public boolean read() {
        return true;
    }

    void checkMiscChars() {
        String s;

        s = io.readString();
        if (s.length() > 0)
            console.stdOutLn("encoders returned misc chars "
            + s
            + "\n");
    }

    double altDecCountToRad() {
        return units.ONE_REV * (double) altDecCount / (double) cfg.getInstance().encoderAltDecCountsPerRev;
    }

    double azRaCountToRad() {
        return units.ONE_REV * (double) azRaCount / (double) cfg.getInstance().encoderAzRaCountsPerRev;
    }

    /**
     * offset = current - actual; current = offset + actual
     */
    void setNewOffset() {
        cfg.getInstance().encoderAltDecOffset = cfg.getInstance().current.alt.rad - altDecCountToRad();
        cfg.getInstance().encoderAzRaOffset = cfg.getInstance().current.az.rad - azRaCountToRad();
        newOffsetsToBeCalculated = false;
    }

    void calcPositions() {
        // azimuth/Right Ascension should read 0 to 360
        azRaRad = eMath.validRad(cfg.getInstance().encoderAzRaOffset + azRaCountToRad());
        // altitude/Declination should read -180 to 180,
        // except when meridian flipped, where altDec should read -90 to 270
        altDecRad = eMath.validRadPi(cfg.getInstance().encoderAltDecOffset + altDecCountToRad());
        if (cfg.getInstance().Mount != null
        && cfg.getInstance().Mount.meridianFlipPossible()
        && cfg.getInstance().Mount.meridianFlip().flipped)
            if (altDecRad < -units.QTR_REV)
                altDecRad += units.ONE_REV;
    }
}

