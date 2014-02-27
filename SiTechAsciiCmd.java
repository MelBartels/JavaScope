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
 * see SITECH_ASCII_CMDS notes
 */
public class SiTechAsciiCmd {
    private final int MAX_BUF_IX = 79;
    private int inBufIx;
    private int inBufIxB;
    private byte inBuf[] = new byte[MAX_BUF_IX+1];

    private IO io;
    private boolean portOpened;

    private boolean returnStatus;
    private int XBits;
    private int YBits;
    private String results;
    private double value;
    
    SiTechCfg SiTechCfg = new SiTechCfg();

    SiTechAsciiCmd() {}
    
    boolean portOpened() {
        return portOpened;
    }
    
    void portOpened(boolean portOpened) {
        this.portOpened = portOpened;
    }

    IO io() {
        return io;
    }
    
    boolean returnStatus() {
        return returnStatus;
    }

    double value() {
        return value;
    }

    void value(double value) {
        this.value = value;
    }
    
    int XBits() {
        return XBits;
    }
    
    int YBits() {
        return YBits;
    }
    
    void XBits(int XBits) {
        this.XBits = XBits;
    }
    
    void YBits(int YBits) {
        this.YBits = YBits;
    }
    
    void registerIo(IO io) {
        this.io = io;
        if (io != null)
            portOpened = io.portOpened();
        else
            portOpened = false;
    }
    
    boolean openPort() {
        ioFactory iof = new ioFactory();

        iof.args.serialPortName = cfg.getInstance().servoSerialPortName;
        iof.args.baudRate = 19200;
        iof.args.trace = cfg.getInstance().servoTrace;

        io = iof.build(IO_TYPE.ioSerial);
        if (io == null) {
            portOpened = false;
            console.errOut("could not open communications port to servo(s)");
        }
        else
            portOpened = true;

        return portOpened;
    }

    boolean closePort() {
        if (portOpened) {
            console.stdOutLn("closing servo serial port...");
            if (io != null)
                io.close();
            portOpened = false;
            return true;
        }
        return false;
    }

    boolean getXBitsValue() {
        if (getValue(SITECH_ASCII_CMDS.getSetXbits.toString())) {
            XBits = (int) value();
            return true;
        }
        return false;
    }

    boolean getYBitsValue() {
        if (getValue(SITECH_ASCII_CMDS.getSetYbits.toString())) {
            YBits = (int) value();
            return true;
        }
        return false;
    }

    boolean setXBits(int i, boolean b) {
        if (getXBitsValue()) {
            XBits -= XBits & i;
            if (b)
                XBits += i;
            xmtCmd(SITECH_ASCII_CMDS.getSetXbits.toString() + (int) XBits);
            getResults();
            xmtReturnGetResults();
            return true;
        }
        return false;
    }

    boolean setYBits(int i, boolean b) {
        if (getYBitsValue()) {
            YBits -= YBits & i;
            if (b)
                YBits += i;
            xmtCmd(SITECH_ASCII_CMDS.getSetYbits.toString() + (int) YBits);
            getResults();
            xmtReturnGetResults();
            return true;
        }
        return false;
    }

    String getResults() {
        if (portOpened()) {
            common.threadSleep(cfg.getInstance().servoPortWaitTimeMilliSecs);
            inBufIx = 0;
            while (io().readSerialBuffer())
                inBuf[inBufIx++] = io().returnByteRead();
            if (inBufIx >= 2
            && inBuf[inBufIx-2] == (byte) eString.RETURN
            && inBuf[inBufIx-1] == (byte) eString.LINE_FEED)
                returnStatus = true;
            else
                returnStatus = false;
        }
        results = "";
        for (int ix = 0; ix < inBufIx; ix++)
            results += (char) inBuf[ix];

        console.stdOutLn("rec: " + results);
        return results;
    }

    void xmtCmd(String cmd) {
        if (portOpened) {
            // clear out read buffer before transmitting new request
            while (io.readSerialBuffer())
                ;
            io.writeStringPauseUntilXmtFinished(cmd + eString.RETURN);
        }
        console.stdOutLn("xmt: " + cmd);
    }
    
    String xmtCmdGetResults(String cmd) {
        xmtCmd(cmd);
        return getResults();
    }
    
    String xmtReturnGetResults() {
        common.threadSleep(cfg.getInstance().servoPortWaitTimeMilliSecs);
        xmtCmd(SITECH_ASCII_CMDS.getDefaultResponse.toString());
        console.stdOutLn("X,Y pos., X,Y encoder pos., X,Y amps*100, volt*10, cpuTempF, auto/man");
        return getResults();
    }

    boolean getValue(String cmd) {
        value = 0;
        xmtCmd(cmd);
        results = getResults();
        if (results.length() > 0) {
            try {
                String subResults;
                if (results.indexOf('b') > -1)
                    subResults = results.substring(results.indexOf('b') + 1);
                else if (results.indexOf('B') > -1)
                    subResults = results.substring(results.indexOf('B') + 1);
                else
                    subResults = results;
                value = Double.parseDouble(subResults);
                return true;
            }
            catch (NumberFormatException nfe) {
                console.errOut("invalid number in SiTechAsciiCmd.getValue(): " + value);
            }
        }
        return false;
    }

    boolean setValue(String cmd, String text) {
        value = 0;
        try {
            value = Double.parseDouble(text);
            xmtCmd(cmd + (int) value);
            getResults();
            xmtReturnGetResults();
            return true;
        }
        catch (NumberFormatException nfe) {
            console.errOut("invalid number in SiTechAsciiCmd.setValue(): " + value);
        }
        return false;
    }

    // servo.GUIDE_MODE is defined as (byte) 0x80 which equals -128, so make it a positive number
    // transmit a command for the Dec motor for faraway position (not maxlong) at 0 speed: 
    // this makes the Dec motor's dir consistent while in guiding mode (a bug in the controller v1.5 software)

    boolean setGuideMode(boolean setting) {
        xmtCmd("X999999999S0");
        xmtReturnGetResults();
        return setXBits(servo.GUIDE_MODE+256, setting);
    }
    
    boolean getGuideMode() {
        getXBitsValue();
        return ((XBits & servo.GUIDE_MODE)==servo.GUIDE_MODE+256? true:false);
    }
    
    /**
     * eg, if 39.95, then transmit XXX3995
     * alt in deg * 100, with no decimal point
     */
    void setAlt(double altRad) {
        xmtCmd("XXX" + eString.doubleToStringNoGrouping(100. * altRad * units.RAD_TO_DEG, 4, 0));
    }
    
    boolean getAll() {
        xmtCmd(SITECH_ASCII_CMDS.getAll.toString());
        if (io != null && portOpened)
            return SiTechCfg.getAll(io);
        return false;
    }
}
