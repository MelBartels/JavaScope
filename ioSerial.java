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

import gnu.io.*;

/**
 * linux ports named /dev/ttyS0, /dev/ttyS1... win32 ports named COM1, COM2...(upper/lower case matters);
 * Make sure you add yourself to uucp and lock groups.  You may also have to change permissions on /dev/ttyS0.
 *
 * 25 pin connector: 2=transmit, 3=receive, 7=ground
 * 9 pin connector: 3=transmit, 2=receive, 5=ground
 * see http://www1.cs.columbia.edu/~cvaill/nullmodem/serial1.html

 * DB9 to RJ45 serial port adapter (based on Gary Myers prototype board):
 * DB9 pinout:      RJ45 male showing DB9 pin#s: (face-on, clip down, cable to rear):
 * --------------   -------------------
 * \ 5 4 3 2 1 /    | 1 4 8 3 7 2 5 9 |
 *  \ 9 8 7 6 /     -------     -------
 *   --------              |||||
 */
public class ioSerial extends ioBase implements SerialPortEventListener, IO {
    private String portName;
    private static CommPortIdentifier portId;
    private static Enumeration portList;
    static java.util.List commPortList;
    private SerialPort serialPort;
    private OutputStream outputStream;
    private InputStream inputStream;
    // for case SerialPortEvent.DATA_AVAILABLE: in serialEvent(SerialPortEvent event)
    private byte[] tmpReadBuf = new byte[1024];
    private boolean outputBufferEmpty = true;

    static void listSerialPorts() {
        console.stdOutLn("finding serial ports...");
        commPortList = new ArrayList();
        // check all available ports
        portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements()) {
            // assign port
            portId = (CommPortIdentifier) portList.nextElement();
            // if serial port
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                console.stdOutLn("   " + portId.getName());
                commPortList.add(portId.getName());
            }
        }
        console.stdOutLn("...end of serial ports");
    }

    public boolean openWithArgs() {
        return open(args.serialPortName, args.baudRate, args.trace);
    }

    /**
     * Integer.MAX_VALUE = 2147483647 so ok to use int for baudRate
     */
    private boolean open(String portName, int baudRate, boolean trace) {
        this.portName = new String(portName);
        ioType = IO_TYPE.ioSerial;

        description(ioType.toString()
        + "."
        + portName
        + "."
        + baudRate);

        console.stdOutLn("opening serial port "
        + description()
        + "...");

        // check all available ports
        portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements()) {
            // assign port
            portId = (CommPortIdentifier) portList.nextElement();
            // if serial port
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                // if choosen port
                if (portId.getName().equalsIgnoreCase(portName)) {
                    try {
                        // open port connection, parms: app name, timeout
                        serialPort = (SerialPort) portId.open("Scope", 10000);
                        // open input stream
                        inputStream = serialPort.getInputStream();
                        // open output stream
                        outputStream = serialPort.getOutputStream();
                        // set connection
                        serialPort.setSerialPortParams(baudRate,
                        serialPort.DATABITS_8,
                        serialPort.STOPBITS_1,
                        serialPort.PARITY_NONE);
                        try {
                            serialPort.setFlowControlMode(serialPort.FLOWCONTROL_NONE);
                        }
                        catch (UnsupportedCommOperationException ucoe) {
                            console.errOut(ucoe.toString());
                            throw ucoe;
                        }
                        // add port to events listener
                        serialPort.addEventListener(this);
                        // do not notify when transmit buffer empty
                        serialPort.notifyOnOutputEmpty(false);
                        // notify when data available
                        serialPort.notifyOnDataAvailable(true);
                        // in some situations, need to transmit a return in order to receive data
                        //writeByte((byte) eString.RETURN);
                        trace(trace);
                        portOpened = true;
                        console.stdOutLn( "serial port connection set");
                        return true;
                    }
                    catch (UnsupportedCommOperationException ucoe) {
                        ucoe.printStackTrace();
                    }
                    catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    catch (PortInUseException piue) {
                        piue.printStackTrace();
                    }
                    catch (TooManyListenersException tmle) {
                        tmle.printStackTrace();
                    }
                }
            }
        }
        console.errOut("could not open serial port " + portName);
        return false;
    }

    public void close() {
        if (portOpened) {
            portOpened = false;
            serialPort.notifyOnDataAvailable(false);
            serialPort.removeEventListener();
            if (inputStream != null)
                try {
                    inputStream.close();
                    inputStream = null;
                }
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            if (outputStream != null)
                try {
                    outputStream.close();
                    outputStream = null;
                }
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            serialPort.close();
            console.stdOutLn("closed serial port " + portName);
            serialPort = null;
            trace(false);
        }
    }

    // "class ioSerial implements SerialPortEventListener" demands that serialEvent(SerialPortEvent event) be implemented (must be public)
    public void serialEvent(SerialPortEvent event) {
        int ix;
        int numBytes;

        switch(event.getEventType()) {
            // for these events to be raised, need to call serialPort.notifyOn...(true); for each event type in open()
            case SerialPortEvent.BI:   // break interrupt
                break;
            case SerialPortEvent.OE:   // timerOverrun error
                break;
            case SerialPortEvent.FE:   // framing error
                break;
            case SerialPortEvent.PE:   // parity error
                break;
            case SerialPortEvent.CD:   // carrier detect
                break;
            case SerialPortEvent.CTS:  // clear to send
                break;
            case SerialPortEvent.DSR:  // data set ready
                break;
            case SerialPortEvent.RI:   // ring indicator
                break;
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                // turn off notification when transmit buffer empty
                serialPort.notifyOnOutputEmpty(false);
                outputBufferEmpty = true;
                // use the following to detect transmission delay problem
                // console.stdOutLn("output buffer empty");
                break;
            case SerialPortEvent.DATA_AVAILABLE:
                // use block read to temporary buffer, then copy to circular queue;
                // get a byte at a time: b = (byte) inputStream.read();
                try {
                    while (inputStream.available() > 0) {
                        numBytes = inputStream.read(tmpReadBuf);
                        for (ix = 0; ix < numBytes; ix++)
                            processReceivedChar(tmpReadBuf[ix]);
                    }
                }
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                break;
        }
    }

    public void writeByte(byte b) {
        try {
            outputStream.write(b);
            if (trace)
                addTrace(XMIT_MARK, b);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // speed gain is 3x to 6x when calling write() once compared to calling write() for each byte
    public void writeByteArray(byte b[], int len) {
        int ix;

        try {
            outputStream.write(b, 0, len);
            if (trace)
                for (ix = 0; ix < len; ix++)
                    addTrace(XMIT_MARK, b[ix]);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    // 2 methods to pause until transmit is finished: 1.waiting for output buffer empty signal, and, 2.flushing.
    public void writeBytePauseUntilXmtFinished(byte b) {
        writeByte(b);
        //while (!outputBufferEmpty)
        //    ;
        try {
           outputStream.flush();
        }
        catch (IOException ioe) {
           ioe.printStackTrace();
        }
    }

    public void writeByteArrayPauseUntilXmtFinished(byte b[], int len) {
        writeByteArray(b, len);
        //while (!outputBufferEmpty)
        //    ;
        try {
           outputStream.flush();
        }
        catch (IOException ioe) {
           ioe.printStackTrace();
        }
    }

    public void writeString(String s) {
        int ix;
        byte bArray[];

        if (s.length() > 0)
            try {
                outputBufferEmpty = false;
                // notify when transmit buffer empty
                serialPort.notifyOnOutputEmpty(true);
                // uses default encoding!   encoding options include "ASCII"
                outputStream.write(s.getBytes());
                if (trace) {
                    bArray = s.getBytes();
                    for (ix = 0; ix < s.length(); ix++)
                        addTrace(XMIT_MARK, bArray[ix]);
                }
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
    }

    public void writeStringPauseUntilXmtFinished(String s) {
        writeString(s);
        // either pause by waiting for output buffer to report empty
        //while (!outputBufferEmpty)
        //    ;
        // or, pause by flushing output stream, which causes function to pause until all data transmitted
        try {
           outputStream.flush();
        }
        catch (IOException ioe) {
           ioe.printStackTrace();
        }
    }

    public void test() {
        boolean append;

        System.out.println("serial port test");
        listSerialPorts();
        System.out.print("enter portName ");
        console.getString();
        args.serialPortName = console.s;
        System.out.print("enter baudRate ");
        console.getInt();
        args.baudRate = console.i;
        System.out.print("turn on trace log (true/false) ? ");
        args.trace = console.getBoolean();
        System.out.print("display as hex or integer (enter 'h' or 'i' or any other key to display as ASCII): ");
        console.getString();
        displayReceivedChar = false;
        displayReceivedCharAsInt = false;
        displayReceivedCharAsHex = false;
        if (console.s.equalsIgnoreCase("h"))
            displayReceivedCharAsHex = true;
        else
            if (console.s.equalsIgnoreCase("i"))
                displayReceivedCharAsInt = true;
            else
                displayReceivedChar = true;

        System.out.print("append a return to every line (int:13 or hex:0d) (y/n)?: ");
        console.getString();
        if (console.s.equalsIgnoreCase("y"))
            append = true;
        else
            append = false;

        if (openWithArgs()) {
            System.out.println("enter '!' to exit");
            while (true) {
                console.getString();
                // example: display 1st char of string: System.out.println("char=" + console.s.charAt(0));
                if (console.s.equalsIgnoreCase("!"))
                    break;
                else {
                    if (append)
                        console.s += eString.RETURN;
                    //writeString(console.s);
                    writeStringPauseUntilXmtFinished(console.s);
                    while (!outputBufferEmpty) {
                        System.out.println("waiting for xmt buffer to empty");
                        common.threadSleep(250);
                   }
                }
            }
            close();
        }
        displayReceivedChar = false;
        displayReceivedCharAsInt = false;
        displayReceivedCharAsHex = false;
        System.out.println("end of serial port test");
    }
}


