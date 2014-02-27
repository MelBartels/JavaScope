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
 * TCP client connects to a TCP server, which must already be running
 */
public class ioTCPclient extends ioBase implements IO {
    private static final int PACKET_SIZE = 1024;
    private String remoteServerInetAddr;
    private int remoteServerIPPort;
    private Socket RemoteSocket;
    private InetAddress remoteServerAddress;
    private byte[] sendData;
    private byte[] receiveData;
    private serverThread receiveThread;
    private InputStream RemoteIn;
    private OutputStream RemoteOut;

    ioTCPclient() {
        sendData = new byte[PACKET_SIZE];
    }

    public boolean openWithArgs() {
        return open(args.remoteIPName, args.remoteIPPort, args.trace);
    }

    private boolean open(String remoteServerInetAddr, int remoteServerIPPort, boolean trace) {
        this.remoteServerInetAddr = remoteServerInetAddr;
        this.remoteServerIPPort = remoteServerIPPort;
        ioType = IO_TYPE.ioTCPclient;

        description(ioType.toString()
        + "."
        + remoteServerInetAddr
        + "."
        + remoteServerIPPort);

        console.stdOutLn("opening TCP client " + description());
        trace(trace);
        return OpenRemoteSocket();
    }

    private boolean OpenRemoteSocket() {
        try {
            // create socket that is connected to remote server machine on specified port
            if (remoteServerInetAddr.length() > 0)
                remoteServerAddress = InetAddress.getByName(remoteServerInetAddr);
            else
                remoteServerAddress = InetAddress.getLocalHost();
            RemoteSocket = new Socket(remoteServerAddress, remoteServerIPPort);
            RemoteIn = RemoteSocket.getInputStream();
            RemoteOut = RemoteSocket.getOutputStream();
            console.stdOutLn("TCP client has contacted TCP server");

            // create an inner serverThread class to process the receive() function, otherwise main thread
            // will block on read, stalling the program
            receiveThread = new serverThread("TCPclient");
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
        if (portOpened) {
            portOpened = false;
            try {
                if (RemoteSocket != null)
                    RemoteSocket.close();
                if (RemoteIn != null)
                    RemoteIn.close();
                if (RemoteOut != null) {
                    RemoteOut.flush();
                    RemoteOut.close();
                }
            }
            catch (IOException ioe) {
                console.errOut("IOException " + ioe);
            }
            console.stdOutLn("closed TCP client to "
            + remoteServerAddress
            + ":"
            + remoteServerIPPort);
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
            RemoteOut.write(byteArray, 0, length);

            if (trace) {
                for (ix = 0; ix < length; ix++)
                    addTrace(XMIT_MARK, byteArray[ix]);
            }

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

        console.stdOutLn("...waiting to receive TCP packets...");

        receiveData = new byte[PACKET_SIZE];
        try {
            while ((msgSize = RemoteIn.read(receiveData)) != -1)
                for (ix = 0; ix < msgSize; ix++)
                    processReceivedChar(receiveData[ix]);
            if (msgSize == -1) {
                console.stdOut("\nserver has closed TCP connection\n");
                close();
                return false;
            }
            else
                return true;
        }
        catch (IOException ie) {
            if (portOpened) {
                console.errOut("TCP could not receive: " + ie.getMessage());
                close();
            }
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
        System.out.println("TCP client test");
        System.out.print("enter remote computer's name or IP address ");
        console.getString();
        args.remoteIPName = console.s;
        System.out.print("enter remote server IP port ");
        console.getInt();
        args.remoteIPPort = console.i;
        System.out.print("turn on trace log (true/false) ? ");
        args.trace = console.getBoolean();

        displayReceivedChar = true;
        
        if (openWithArgs()) {
            System.out.println("enter message to send to server, '!' to quit");
            while (portOpened) {
                console.getString();
                if (console.s.equalsIgnoreCase("!"))
                    break;
                else
                    writeString(console.s);
            }
            close();
        }
        System.out.println("end of TCP client test");
    }
}

