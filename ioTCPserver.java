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
 * TCP server must be listening before TCP client started
 *
 * client          |   server
 * socket          |   server socket
 * xmit            |   recv
 * remote server   |   local server
 * send            |   server()
 *                 |   receive
 */
public class ioTCPserver extends ioBase implements IO {
    static final int PACKET_SIZE = 1024;
    private int localServerIPPort;
    private ServerSocket localSocket;
    private byte[] receiveData;
    private byte[] sendData;
    private Socket receiveSocket;
    private serverThread receiveThread;
    private InputStream clientIn;
    private OutputStream clientOut;
    private boolean KeepPortOpen;
    static final String CLOSE_STRING = "close";

    ioTCPserver() {
        sendData = new byte[PACKET_SIZE];
    }

    public boolean openWithArgs() {
        return open(args.homeIPPort, args.trace);
    }

    private boolean open(int localServerIPPort, boolean trace) {
        this.localServerIPPort = localServerIPPort;
        ioType = IO_TYPE.ioTCPserver;

        description(ioType.toString()
        + "."
        + localServerIPPort);

        console.stdOutLn("opening TCP server " + description());
        trace(trace);
        KeepPortOpen = true;
        new KeepPortOpenThread("KeepPortOpenThread").start();
        return true;
    }

    /**
     * keep port open by invoking thread that will re-open port;
     * client may close connection accidently (or close stream on purpose if browser request),
     * so re-start server to listen again;
     * if client should send message equal to CLOSE_STRING then close server permanently;
     * browser needs output stream closed to indicate that data download is complete
     */
    private class KeepPortOpenThread extends Thread {
        KeepPortOpenThread(String name) {
            super(name);
        }

        public void run() {
            while (KeepPortOpen)
                if (!portOpened)
                    KeepPortOpen = OpenLocalSocket();
            closeSocket();
        }
    }

    private boolean OpenLocalSocket() {
        try {
            // create a server socket to accept connection requests
            localSocket = new ServerSocket(localServerIPPort);
            console.stdOutLn("TCP local server socket connection set on port " + localServerIPPort);
            // create an inner serverThread class to process the receive() function, otherwise main thread
            // will block on read, stalling the program
            receiveThread = new serverThread("TCPserver");
            receiveThread.start();
            portOpened = true;
            return true;
        }
        catch (IOException ioe) {
            console.errOut("IOException " + ioe);
            return false;
        }
    }

    public void close() {
        // send signal to KeepPortOpenThread thread to close connection
        KeepPortOpen = false;
    }

    private void closeSocket() {
        if (portOpened) {
            portOpened = false;
            try {
                if (localSocket != null)
                    localSocket.close();
                if (clientIn != null)
                    clientIn.close();
                if (clientOut != null)
                    clientOut.close();
                if (receiveSocket != null)
                    receiveSocket.close();
            }
            catch (IOException ioe) {
                console.errOut("IOException " + ioe);
            }
            console.stdOutLn("closed TCP server listening on port " + localServerIPPort);
            trace(false);
        }
    }

    private boolean sendString(String SendStr) {
        sendData = SendStr.getBytes();
        return send(sendData, sendData.length);
    }

    private boolean send(byte[] byteArray, int length) {
        int ix;

        try {
            clientOut.write(byteArray, 0, length);

            if (trace)
                for (ix = 0; ix < length; ix++)
                    addTrace(XMIT_MARK, byteArray[ix]);

            return true;
        }
        catch (IOException ioe) {
            console.errOut("could not send");
            return false;
        }
    }

    public void writeByte(byte b) {
        sendData[0] = b;
        send(sendData, 1);
    }

    public void writeByteArray(byte b[], int len) {
        int ix;

        for (ix = 0; ix < len; ix++)
            sendData[ix] = b[ix];
        send(sendData, len);
    }

    public void writeBytePauseUntilXmtFinished(byte b) {
        writeByte(b);
    }

    public void writeByteArrayPauseUntilXmtFinished(byte b[], int len) {
        writeByteArray(b, len);
    }

    public void writeString(String s) {
        sendString(s);
    }

    public void writeStringPauseUntilXmtFinished(String s) {
        writeString(s);
    }

    private boolean server() {
        int ix;
        int msgSize;
        String receiveDataString;

        console.stdOutLn("...waiting to receive TCP packets...");

        receiveData = new byte[PACKET_SIZE];
        try {
            // get client connection
            receiveSocket = localSocket.accept();
            receiveSocket.setTcpNoDelay(true);
            console.stdOutLn("Handling client at "
            + receiveSocket.getInetAddress().getHostAddress()
            + ":"
            + receiveSocket.getPort());
            clientIn = receiveSocket.getInputStream();
            clientOut = receiveSocket.getOutputStream();

            while ((msgSize = clientIn.read(receiveData)) != -1) {
                for (ix = 0; ix < msgSize; ix++)
                    processReceivedChar(receiveData[ix]);
                receiveDataString = "";
                for (ix = 0; ix < msgSize; ix++)
                    receiveDataString += (char) receiveData[ix];
                if (receiveDataString.equalsIgnoreCase(CLOSE_STRING))
                    KeepPortOpen = false;
            }
            if (msgSize == -1) {
                console.stdOut("\nclient has closed TCP connection\n");
                closeSocket();
                return false;
            }
            return true;
        }
        catch (IOException ie) {
            if (portOpened) {
                console.errOut("TCP could not receive, closing socket and ending server(): " + ie.getMessage());
                closeSocket();
            }
            // else portOpened == false, so don't display exception
            return false;
        }
    }

    private class serverThread extends Thread {
        serverThread(String name) {
            super(name);
        }

        public void run() {
            while (server())
                ;
            console.stdOutLn("TCP serverThread ending");
        }
    }

    public void test() {
        System.out.println("TCP server test");
        System.out.print("enter local server IP port ");
        console.getInt();
        args.homeIPPort = console.i;
        System.out.print("turn on trace log (true/false) ? ");
        args.trace = console.getBoolean();

        displayReceivedChar = true;
        
        if (openWithArgs()) {
            System.out.println("enter message to send to server, '!' to quit");
            while (KeepPortOpen) {
                console.getString();
                if (console.s.equalsIgnoreCase("!")) {
                    KeepPortOpen = false;
                    break;
                }
                else
                    if (KeepPortOpen)
                        writeString(console.s);
            }
        }
        common.threadSleep(1000);
        System.out.println("end of TCP server test");
    }
}

