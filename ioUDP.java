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
 * there are 3 IP ports used: the first port, left up to the client constructor, is used to send with;
 * the second port (localServerIPPort), is used for the receive method to retrieve data sent to this machine;
 * and the third port (remoteServerIPPort), is the port on the remote machine to use in combination with its IP address;

 * if remote machine is truly remote and not another instance of the program running on the local machine, then the
 * local and remote IP ports can be the same;
 * if two instances of program running on same machine, then routine will talk to itself and not to the other instance, which
 * will not be able to use the assigned port
 */
public class ioUDP extends ioBase implements IO {
    private static final int PACKET_SIZE = 1024;

    private DatagramSocket clientSocket;
    private DatagramPacket clientPacket;
    private InetAddress clientAddress;
    private byte[] sendData;

    private int remoteServerIPPort;
    private InetAddress remoteServerAddress;

    private int localServerIPPort;
    private DatagramSocket localServerSocket;
    private DatagramPacket receivePacket;
    private byte[] receiveData;
    private serverThread receiveThread;

    public boolean openWithArgs() {
        return open(args.homeIPPort, args.remoteIPName, args.remoteIPPort, args.trace);
    }

    private boolean open(int localServerIPPort, String remoteServerInetAddr, int remoteServerIPPort, boolean trace) {
        ioType = IO_TYPE.ioUDP;

        description(ioType.toString()
        + "."
        + localServerIPPort
        + "."
        + remoteServerInetAddr
        + "."
        + remoteServerIPPort);

        console.stdOutLn("opening UDP port " + description());
        try {
            clientAddress = InetAddress.getLocalHost();
            // client datagram socket takes no arguments;
            // constructor binds the socket to any available local port;
            // the datagram that is sent will contain the addressing information
            clientSocket = new DatagramSocket();
            sendData = new byte[PACKET_SIZE];
            console.stdOutLn("UDP client socket connection set");

            // contact remote server machine and get its internet address: used to xmit data
            this.remoteServerIPPort = remoteServerIPPort;
            if (remoteServerInetAddr.length() > 0)
                remoteServerAddress = InetAddress.getByName(remoteServerInetAddr);
            else
                remoteServerAddress = InetAddress.getLocalHost();
            console.stdOutLn("UDP remote server contacted");

            // open a socket on a port to receive datagrams packets on;
            // server datagram socket takes a port argument
            this.localServerIPPort = localServerIPPort;
            localServerSocket = new DatagramSocket(localServerIPPort);
            console.stdOutLn("UDP local server socket connection set");
            trace(trace);
            portOpened = true;
        }
        catch (UnknownHostException uhe) {
            console.errOut("UnknownHostException " + uhe);
            return false;
        }
        catch (SocketException se) {
            console.errOut("SocketException " + se);
            return false;
        }

        // create an inner serverThread class to process the receive() function, otherwise main thread
        // will block on read, stalling the program
        receiveThread = new serverThread("UdpServer");
        receiveThread.start();

        return true;
    }

    public void close() {
        if (portOpened) {
            portOpened = false;
            localServerSocket.close();

            console.stdOutLn("closed UDP port to "
            + remoteServerAddress
            + ":"
            + remoteServerIPPort
            + ", listening on port "
            + localServerIPPort);

            trace(false);
        }
    }

    private boolean sendString(String SendStr) {
        sendData = SendStr.getBytes();
        return send(sendData, sendData.length);
    }

    private boolean send(byte[] byteArray, int length) {
        int ix;

        clientPacket = new DatagramPacket(byteArray, length, remoteServerAddress, remoteServerIPPort);
        try {
            clientSocket.send(clientPacket);

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
        int port;
        InetAddress address;
        String serverStr;

        //console.stdOutLn("...waiting to receive UDP packets...");

        receiveData = new byte[PACKET_SIZE];
        receivePacket = new DatagramPacket(receiveData, PACKET_SIZE);
        try {
            localServerSocket.receive(receivePacket);

            address = receivePacket.getAddress();
            port = receivePacket.getPort();

            //serverStr = "";
            //for (ix = 0; ix < receivePacket.getLength(); ix++)
            //    serverStr += (char) receiveData[ix];
            //console.stdOutLn("Message " + serverStr);
            //console.stdOutLn("From "
            //+ address
            //+ ":"
            //+ port);

            for (ix = 0; ix < receivePacket.getLength(); ix++)
                processReceivedChar(receiveData[ix]);

            return true;
        }
        catch (IOException ie) {
            if (portOpened)
                console.errOut("could not receive: " + ie.getMessage());
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
        }
    }

    private void testClient() {
        System.out.println("starting client test");
        System.out.println("client address " + clientAddress);
        System.out.println("remote server address "
        + remoteServerAddress
        + ":"
        + remoteServerIPPort);
        System.out.println("enter message to send to server, '!' to quit");
        while (true) {
            console.getString();
            if (console.s.equalsIgnoreCase("!"))
                break;
            else
                writeString(console.s);
        }
        System.out.println("Client test finished");
    }

    private void testRecv() {
        while (server())
            ;
    }

    public void test() {
        System.out.println("UDP test");
        System.out.print("enter local server IP port ");
        console.getInt();
        args.homeIPPort = console.i;
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
            testClient();
            close();
        }
    }
}

