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

public class testTCP {
    IO io;
    ioFactory iof = new ioFactory();

    testTCP() {
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
        // wait a second for socket to be built
        common.threadSleep(1000);
        if (io != null) {
            while (io.portOpened()) {
                console.getString();
                if (console.s.equalsIgnoreCase("!"))
                    break;
                else
                    io.writeString(console.s);
            }
            io.close();
        }
    }
}

