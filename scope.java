/**
 *
 * copyright 2002 - 2005 Mel Bartels
 * coded and tested in Sun Java 1.4, 1.5
 * graphical components created and edited in NetBeans
 *
 * see the accompanying readme.html;
 *
 * uses RXTX serial communication package (http://www.rxtx.org/) that runs on a variety of platforms:
 * for RXTXcomm.jar
 *    Win32: (make sure file name is lower case)
 *       put rxtxcomm.jar and rxtxSerial.dll in \jdk1...\jre\lib\ext and place in path (working directory or \windows)
 *          only need place both in working path if running \jdk1...\java -cp scope.jar;RXTXcomm.jar scope,
 *             but need both in \jre\lib\ext for netbeans debugger
 *       Linux: put RXTXcomm.jar in /jdk1.../jre/lib/ext and place librxtxSerial.so in /jdk1.../jre/lib/i386
 *              user running program should have ability to create /var/lock (default is root only writable)
 *
 * program uses the n-tier model:
 *    commands are outputted to the servo motor microcontrollers on a serial bus and can be separated from the driver by this program running
 *       in relay mode on a separate tier where the servo commands are sent via an IO method (say TCP/IP) then relayed to the serial port;
 *    the servo driver layer is one half of the application tier (handpadControl extends servo extends handpad extends handpadDesignBase);
 *    the other half of the application tier handles the coordinate conversion and command sequencer;
 *    these two halves meet in class track (track extends handpadControl and composes encoderFactory and composes convertMatrix which composes convertTrig);
 *    class trackBuilder builds track and all accompanying objects;
 *    track.sequencer() is the primary control function that repeats multiple times per second, checking for control input from a variety
 *       of sources, calling the functions that calculate distances to move, and calling the servo functions to cause motor movement;
 *
 * to run in NetBeans, add command line option to find configuration file scope.cfg
 *
 * UI:
 *    make simplest possible
 *    make hard things easier
 *    remove things that are rarely used
 *    otoh, pretty interfaces build support, ugly ones kill it: perception is king
 *    users know what they want, they don't know how they want it: job of UI to give them what they want
 *    avoid required double clicks: hard for elderly and others to use
 *    drop down menu alternative: one menu program shows initially, has list of available tasks, each task a 
 *       modal form with two options OK and RETURN; users get used to form quickly and make few errors
 *
 *todo:
 * guide on/off
 *    stay at ending position when guiding turned off y/n
 *    update drift y/n
 *    rotate guiding corrections angle
 *    analyze guiding efforts for PEC
 * accuracy
 *    polar align routine for equat mounts
 *    build pointing analysis data
 *       add analysis point "select object from DB or enter coord, center object with handpad, then press handpad leftkey"
 *       analyze data for error corrections: z123, axis vs axis, pmc
 */

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
import javax.swing.plaf.metal.MetalLookAndFeel;

// for applet...
import java.applet.Applet;
import java.awt.Graphics;

public class scope {
    static void exit() {
        console.stdOutLn(eString.SCOPE_END_LINE);
        System.exit(0);
    }

    public static void main(String args[]) {
        JFrameMain jfm;
        int ix;
        String cmdFilename = "";
        boolean startCmd = false;
        boolean displayHelp = false;
        boolean displayCmdScope = false;
        boolean test = false;
        boolean runRelay = false;
        boolean runSiTech = false;
        boolean runSim = false;

        console.stdOutOK = true;
        console.errOutOK = true;
        
        console.stdOutLn(eString.SCOPE_START_LINE);
        console.stdOutLn(eString.SCOPE_BUILD_LINE);
        
        scopeCopyrightDisclaimer.display();
        
        // name of calling program known (unlike c, c++) since class name with main() must be the same, so args[] starts at zero
        
        for (ix = 0; ix < args.length; ix++)
            if (args[ix].equalsIgnoreCase("-c") && ix < args.length-1)
                cfg.getInstance().filename = new String(args[ix+1]);
            else if (args[ix].equalsIgnoreCase("-h")
            || args[ix].equalsIgnoreCase("/h")
            || args[ix].equalsIgnoreCase("/he")
            || args[ix].equalsIgnoreCase("/help")
            || args[ix].equalsIgnoreCase("/?")
            || args[ix].equalsIgnoreCase("?"))
                displayHelp = true;
            else if (args[ix].equalsIgnoreCase("-relay"))
                runRelay = true;
            else if (args[ix].equalsIgnoreCase("-sitech"))
                runSiTech = true;
            else if (args[ix].equalsIgnoreCase("-sim"))
                runSim = true;
            else if (args[ix].equalsIgnoreCase("-t"))
                test = true;
            else if (args[ix].equalsIgnoreCase("-x") && ix < args.length-1) {
                cmdFilename = new String(args[ix+1]);
                startCmd = true;
            }
            else if (args[ix].equalsIgnoreCase("-xh"))
                displayCmdScope = true;

        if (displayHelp) {
            cmdLineArgs.display();
            exit();
        }
        if (displayCmdScope) {
            CMD_SCOPE.displayCmdScope();
            exit();
        }
        if (runRelay) {
            testIORelay ior = new testIORelay();
            ior.test();
            exit();
        }
            
        cfg.getInstance().defaults();
        cfg.getInstance().read();
        
        // for debugging out of NetBeans, uncomment the following line so as to go into the test functions
        // test = true;
        if (test) {
            runTests rt = new runTests();
            rt.runTestsFromConsole();
            exit();
        }
        else if (runSim) {
            new PICServoMotorsSimulator().run();
            exit();
        }
        else if (runSiTech) {
            screenPlacement.getInstance();
            new JFrameSiTech().show();
        }
        else {
            // main program execution
            // call stack: main() -> JFrameMain.run() -> trackBuilder.run() -> track.sequencer()

            // launch the user interface;
            // set look and feel in screenPlacement before instantiating any JFrames;
            screenPlacement.getInstance();
            jfm = new JFrameMain();
            jfm.show();
            jfm.run(startCmd, cmdFilename);

            // do actions based on command line options !!!

            if (jfm == null) {
                trackBuilder tb = new trackBuilder(null);
                tb.build();
                if (startCmd)
                    tb.t.cmdCol.cmdScopeList.parseCmdFromFile(cmdFilename);
                tb.run();
            }
            exit();
        }
    }
}

