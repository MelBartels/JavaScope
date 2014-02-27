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
 * relays or transfers data IO from one type of IO to another
 */
public class testIORelay {
    private int select;
    private boolean quit;
    private boolean displayTraffic;
    private ioFactory iof;
    private IO io;
    private IO io1;
    private IO io2;
    private byte b1;
    private byte b2;

    void test() {
        iof = new ioFactory();
        System.out.println("test of IO relay");

        System.out.print("display relayed traffic (y/n)? ");
        console.getString();
        if (console.s.equalsIgnoreCase("y"))
            displayTraffic = true;
        else
            displayTraffic = false;

        while (!quit && io2 == null) {
            if (io1 == null)
                System.out.println("select 1st channel from the following, or any other number to quit");
            else
                System.out.println("select 2nd channel from the following, or any other number to quit");

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
                io = iof.build(IO_TYPE.ioNone);
            }
            else if (select == IO_TYPE.ioFile.KEY+1) {
                System.out.print("enter fileLocation ");
                console.getString();
                iof.args.fileLocation = console.s;
                io = iof.build(IO_TYPE.ioFile);
            }
            else if (select == IO_TYPE.ioSerial.KEY+1) {
                System.out.print("enter portName ");
                console.getString();
                iof.args.serialPortName = console.s;
                System.out.print("enter baudRate ");
                console.getInt();
                iof.args.baudRate = console.i;
                System.out.print("turn on trace log (true/false) ? ");
                iof.args.trace = console.getBoolean();
                io = iof.build(IO_TYPE.ioSerial);
            }
            else if (select == IO_TYPE.ioUDP.KEY+1) {
                System.out.print("enter local server IP port ");
                console.getInt();
                iof.args.homeIPPort = console.i;
                System.out.print("enter remote computer's name or IP address ");
                console.getString();
                iof.args.remoteIPName = console.s;
                System.out.print("enter remote server IP port ");
                console.getInt();
                iof.args.remoteIPPort = console.i;
                System.out.print("turn on trace log (true/false) ? ");
                iof.args.trace = console.getBoolean();
                io = iof.build(IO_TYPE.ioUDP);
            }
            else if (select == IO_TYPE.ioTCPserver.KEY+1) {
                System.out.print("enter local server IP port ");
                console.getInt();
                iof.args.homeIPPort = console.i;
                System.out.print("turn on trace log (true/false) ? ");
                iof.args.trace = console.getBoolean();
                io = iof.build(IO_TYPE.ioTCPserver);
            }
            else if (select == IO_TYPE.ioTCPclient.KEY+1) {
                System.out.print("enter remote computer's name or IP address ");
                console.getString();
                iof.args.remoteIPName = console.s;
                System.out.print("enter remote server IP port ");
                console.getInt();
                iof.args.remoteIPPort = console.i;
                System.out.print("turn on trace log (true/false) ? ");
                iof.args.trace = console.getBoolean();
                io = iof.build(IO_TYPE.ioTCPclient);
            }
            else if (select == IO_TYPE.ioTCP.KEY+1) {
                System.out.print("enter local server IP port ");
                console.getInt();
                iof.args.homeIPPort = console.i;
                System.out.print("enter remote computer's name or IP address ");
                console.getString();
                iof.args.remoteIPName = console.s;
                System.out.print("enter remote server IP port ");
                console.getInt();
                iof.args.remoteIPPort = console.i;
                System.out.print("turn on trace log (true/false) ? ");
                iof.args.trace = console.getBoolean();
                io = iof.build(IO_TYPE.ioTCP);
            }
            if (io1 == null)
                io1 = io;
            else
                io2 = io;
        }
        if (io1 != null && io2 != null) {
            System.out.println("enter '#' from either IO to end");
            while (!quit) {
                if (io1.countReadBytes() > 0)
                    do {
                        io1.readSerialBuffer();
                        b1 = io1.returnByteRead();
                        if (displayTraffic)
                            System.out.println("from IO#1: " + (char) b1);
                        if (b1 == '#')
                            quit = true;
                        io2.writeByte(b1);
                    }while (io1.countReadBytes() > 0);

                if (io2.countReadBytes() > 0)
                    do {
                        io2.readSerialBuffer();
                        b2 = io2.returnByteRead();
                        if (displayTraffic)
                            System.out.println("from IO#2: " + (char) b2);
                        if (b2 == '#')
                            quit = true;
                        io1.writeByte(b2);
                    }while (io2.countReadBytes() > 0);
            }
            io1.close();
            io2.close();
        }
        System.out.println("end of IO relay test");
    }
}

