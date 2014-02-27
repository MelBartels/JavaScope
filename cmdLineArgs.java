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

public class cmdLineArgs {
    static final String S =
    "\n\n\n*** scope help ***"
    + "\n\ncommand line options:"
    + "\n\nif -c ('c' for config file) then use following string as configuration file name, ie, "
    + "'scope -c cfg.dat' will result in cfg.dat being used"
    + "\n\nif -x ('x' for command) then use following string as command filename, executing command file "
    + "upon program startup, ie, 'scope -x nan.cmd' will cause nan.cmd to be loaded and run"
    + "\n\nif -xh ('xh' for command help) then display command help"
    + "\n\nif -sitech ('sitech' for Sidereal Technology) then run the Sidereal Technology controller configuration frame"
    + "\n\nif -sim ('sim' for simulator) then run as JRKerr servo motor controller simulator"
    + "\n\nif -relay ('relay' for ioRelay) then run in relay mode, relaying data from one IO type or communication channel to another"
    + "\n\nif -t ('t' for test) then run tests"
    + "\n\nif -h ('h' for help) or /h or /he or /help or /? or ? then print out command line options"
    + "\n\n*** end of scope help ***\n\n\n";

    static void display() {
        console.stdOutLn(S);
    }
}

