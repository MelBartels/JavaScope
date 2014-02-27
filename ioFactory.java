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
 * factory for IO objects;
 * TCP/IP is either a client, or if no server found, a server
 */
public class ioFactory {
    IO io;
    ioArgs args;

    ioFactory() {
        args = new ioArgs();
    }

    /**
     * builds IO object, opens the port, and returns pointer to IO object if successful, otherwise returns null
     */
    IO build(IO_TYPE ioType) {
        if (ioType == IO_TYPE.ioTCP)
            return buildTCP();
        try {
            // create new class by class name, use it to create new object instance, then return correct cast, eg
            // Class NewClass = Class.forName("MyClass");
            // object NewObject = NewClass.newInstance();
            // MyObject MyObject = (MyObject) NewObject;
            io = (IO) Class.forName(ioType.toString()).newInstance();
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

        if (io == null)
            console.errOut("unhandled ioType: " + ioType);
        else {
            io.args(args);
            if (io.openWithArgs())
                console.stdOutLn("built IO object: " + io.description());
            else
                io = null;
        }
        return io;
    }

    /**
     * ioTCP is either an instance of the TCP client or of the TCP server; if the remote machine is acting as a TCP server,
     * then the TCP client is used, otherwise a TCP server is setup to wait for a remote machine to contact it
     */
    IO buildTCP() {
        io = null;
        console.stdOutLn("attempting to build TCP client...");
        build(IO_TYPE.ioTCPclient);
        if (io == null) {
            console.stdOutLn("unable to build TCP client: no server listening; attempting to build TCP server");
            build(IO_TYPE.ioTCPserver);
        }
        return io;
    }

    void test() {
        boolean quit = false;
        int select;

        System.out.println("test of ioFactory");

        while (!quit) {
            System.out.println("select from the following, or any other number to quit");

            Enumeration eIO_TYPE = IO_TYPE.elements();
            while (eIO_TYPE.hasMoreElements()) {
                IO_TYPE iot = (IO_TYPE) eIO_TYPE.nextElement();
                System.out.println("      "
                + (iot.KEY+1)
                + ". "
                + iot);
            }
            console.getInt();
            select = console.i;
            if (select < 1 || select > IO_TYPE.size()) {
                quit = true;
                break;
            }
            else if (select == IO_TYPE.ioNone.KEY+1) {
                System.out.print("ioNone selected");
                build(IO_TYPE.ioNone);
                if (io != null)
                    io.close();
            }
            else if (select == IO_TYPE.ioFile.KEY+1) {
                System.out.print("enter fileLocation ");
                console.getString();
                args.fileLocation = console.s;
                build(IO_TYPE.ioFile);
                if (io != null)
                    io.close();
            }
            else if (select == IO_TYPE.ioSerial.KEY+1) {
                System.out.print("enter portName ");
                console.getString();
                args.serialPortName = console.s;
                System.out.print("enter baudRate ");
                console.getInt();
                args.baudRate = console.i;
                System.out.print("turn on trace log (true/false) ? ");
                args.trace = console.getBoolean();
                build(IO_TYPE.ioSerial);
                if (io != null)
                    io.close();
            }
            else if (select == IO_TYPE.ioUDP.KEY+1) {
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
                build(IO_TYPE.ioUDP);
                if (io != null)
                    io.close();
            }
            else if (select == IO_TYPE.ioTCPserver.KEY+1) {
                System.out.print("enter local server IP port ");
                console.getInt();
                args.homeIPPort = console.i;
                System.out.print("turn on trace log (true/false) ? ");
                args.trace = console.getBoolean();
                build(IO_TYPE.ioTCPserver);
                if (io != null)
                    io.close();
            }
            else if (select == IO_TYPE.ioTCPclient.KEY+1) {
                System.out.print("enter remote computer's name or IP address ");
                console.getString();
                args.remoteIPName = console.s;
                System.out.print("enter remote server IP port ");
                console.getInt();
                args.remoteIPPort = console.i;
                System.out.print("turn on trace log (true/false) ? ");
                args.trace = console.getBoolean();
                build(IO_TYPE.ioTCPclient);
                if (io != null)
                    io.close();
            }
            else if (select == IO_TYPE.ioTCP.KEY+1) {
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
                build(IO_TYPE.ioTCP);
                if (io != null)
                    io.close();
            }
        }
    }
}

