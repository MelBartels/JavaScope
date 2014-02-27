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

public class testEncoders {
    void test() {
        int holdACount = -Integer.MAX_VALUE;
        int holdZCount = -Integer.MAX_VALUE;

        testBuildBuf();

        // can do encoders e = new encoderFactory().build(); but have no access to the encoderFactory object and its methods
        encoderFactory ef = new encoderFactory();

        ef.build(cfg.getInstance().encoderType,
        cfg.getInstance().encoderIOType,
        cfg.getInstance().encoderSerialPortName,
        cfg.getInstance().encoderBaudRate,
        cfg.getInstance().encoderHomeIPPort,
        cfg.getInstance().encoderRemoteIPName,
        cfg.getInstance().encoderRemoteIPPort,
        cfg.getInstance().encoderFileLocation,
        cfg.getInstance().encoderTrace);

        if (ef.E != null && ef.E.portOpened()) {
            System.out.println("press return to read encoders, press any key then return to exit");
            console.s = "";
            while (console.s.equalsIgnoreCase("")) {
                if (ef.E.queryAndRead()) {
                    System.out.println("encoders query and read successful");
                    if (holdACount != ef.E.getAltDecCount() || holdZCount != ef.E.getAzRaCount()) {
                        ef.E.displayCounts();
                        holdACount = ef.E.getAltDecCount();
                        holdZCount = ef.E.getAzRaCount();
                    }
                }
                else
                    System.out.println("could not query and read encoders");
                console.getString();
            }
            ef.E.close();
        }
    }

    void testBuildBuf() {
        int ix;
        int res;
        int factor;
        byte buf[] = new byte[13];

        System.out.println("test of buildBuf()");

        buf[0] = (byte) 'R';
        buf[6] = eString.TAB;
        buf[12] = eString.RETURN;

        for (ix=1, res=cfg.getInstance().encoderAzRaCountsPerRev, factor=10000; ix<6; ix++, res-=((res/factor)*factor), factor/=10) {
            buf[ix] = (byte) ('0' + res/factor);
            System.out.println(ix
            + " "
            + res
            + " "
            + factor
            + " "
            + (res/factor)
            + " "
            + buf[ix]);
        }
        for (ix=7, res=cfg.getInstance().encoderAltDecCountsPerRev, factor=10000; ix<12; ix++, res-=((res/factor)*factor), factor/=10) {
            buf[ix] = (byte) ('0' + res/factor);
            System.out.println(ix
            + " "
            + res
            + " "
            + factor
            + " "
            + (res/factor)
            + " "
            + buf[ix]);
        }
        for (ix = 0; ix < 12; ix++)
            System.out.print((char) buf[ix] + " ");

        System.out.println("\nend of buildBuf() test, press return to continue");
        console.getString();
    }
}

