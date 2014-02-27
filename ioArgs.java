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

public class ioArgs {
    // comm port to use
    String serialPortName;
    int baudRate;
    // local port to transmit to external controlling program via UDP
    int homeIPPort;
    // external controlling program TCP or UDP IP address name and port
    String remoteIPName;
    int remoteIPPort;
    String fileLocation;
    boolean trace;
}

