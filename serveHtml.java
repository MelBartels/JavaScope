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
 * tests browser calls against the ioTCPserver class by returning a dynamic html page
 */
public class serveHtml {
    IO io;
    ioFactory iof;

    serveHtml() {
        iof = new ioFactory();
    }

    void test() {
        while (true) {
            System.out.println("enter text to display in webpage or '!' to quit");
            console.getString();
            if (console.s.equalsIgnoreCase("!"))
                break;
            iof.args.homeIPPort = 80;
            iof.args.trace = false;
            io = iof.build(IO_TYPE.ioTCPserver);
            System.out.println("now launch the browser with address of http://127.0.0.1/");
            while (io != null)
                if (io.countReadBytes() > 0) {
                    do {
                        io.readString();
                    }while (io.countReadBytes() > 0);

                    System.out.println("sending html");

                    io.writeString("<html><head><title>test</title></head><body>text sent is "
                    + console.s
                    + "</body></html>");

                    common.threadSleep(1000);
                    io.close();
                    // need to set to null
                    io = null;
                }
        }
    }
}

