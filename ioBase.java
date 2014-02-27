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
 * methods and variables common to all IO objects
 */
public class ioBase extends traceBuffer {
    IO_TYPE ioType;
    ioArgs args;
    public String description;
    boolean portOpened;
    private byte b;
    // read and write pointers point to next available queue entry
    int readBufferWritePtr;
    int readBufferReadPtr;
    // ring buffer: must be 2^n in size for (& ptrs with 2^n-1) to work: very much faster than comparing and setting to zero
    static final int READ_BUFFER_SIZE = 4096;
    byte[] readBuffer = new byte[READ_BUFFER_SIZE];
    boolean displayReceivedChar;
    boolean displayReceivedCharAsInt;
    boolean displayReceivedCharAsHex;

    ioBase() {
        args = new ioArgs();
    }

    public String ioType() {
        return ioType.toString();
    }

    public void args(ioArgs args) {
        this.args = args;
    }

    public ioArgs args() {
        return args;
    }

    public void description(String description) {
        this.description = description;
        super.description = description;
    }

    public String description() {
        return description;
    }

    public boolean portOpened() {
        return portOpened;
    }

    public void displayReceivedChar(boolean value) {
        displayReceivedChar = value;
    }

    public void displayReceivedCharAsHex(boolean value) {
        displayReceivedCharAsHex = value;
    }

    void processReceivedChar(byte b) {
        readBuffer[readBufferWritePtr] = b;

        if (displayReceivedChar)
            console.stdOutChar((char) readBuffer[readBufferWritePtr]);
        if (displayReceivedCharAsInt)
            console.stdOut(" "
            + eString.unsignedInt((int) readBuffer[readBufferWritePtr])
            + " ");
        if (displayReceivedCharAsHex)
            console.stdOut(" "
            + eString.intToHex((int) readBuffer[readBufferWritePtr])
            + " ");
        if (trace)
            addTrace(REC_MARK, readBuffer[readBufferWritePtr]);

        readBufferWritePtr++;
        readBufferWritePtr &= (READ_BUFFER_SIZE-1);
    }

    public int countReadBytes() {
        int count = readBufferWritePtr - readBufferReadPtr;

        if (count < 0)
            count += READ_BUFFER_SIZE;
        return count;
    }

    public boolean waitForReadBytes(int numBytesToWaitFor, int bailIntervalMilliSec) {
        double bailTRad;

        astroTime.getInstance().calcSidT();
        bailTRad = astroTime.getInstance().sidT.rad + bailIntervalMilliSec * units.MILLI_SEC_TO_RAD;
        bailTRad = eMath.validRad(bailTRad);

        // handle situation where start time is 23 hrs, interval is 2 hrs, thus bail time is 1 hr by checking for 23-1>12
        while (countReadBytes()<numBytesToWaitFor && (bailTRad>astroTime.getInstance().sidT.rad || (astroTime.getInstance().sidT.rad-bailTRad)>units.HALF_REV))
            astroTime.getInstance().waitForNewSidT();

        if (countReadBytes() >= numBytesToWaitFor)
            return true;
        else
            return false;
    }

    public boolean readSerialBuffer() {
        if (readBufferReadPtr == readBufferWritePtr)
            return false;
        else {
            b = readBuffer[readBufferReadPtr];
            readBufferReadPtr++;
            readBufferReadPtr &= (READ_BUFFER_SIZE-1);
            return true;
        }
    }

    public byte returnByteRead() {
        return b;
    }

    public int readByteReturnUnsignedInt() {
        readSerialBuffer();
        return eString.unsignedInt(b);
    }

    public String readString() {
        String s = "";

        while (readSerialBuffer())
            s += (char) b;

        return s;
    }
}

