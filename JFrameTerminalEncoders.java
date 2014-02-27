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
 * extends JFrameTerminal to add start/stop of encoders thread
 */
public class JFrameTerminalEncoders extends JFrameTerminal {
    track t;

    JFrameTerminalEncoders(track t) {
        super("encoders controller terminal window");
        this.t = t;
        registerTrackReference(t);
        registerIOReference(t.ef.E.io());
    }

    void start() {
        t.pauseSequencer = true;
        t.stopEncodersThread();
        super.start();
    }

    /*
     * JFrameTerminal's windowClosing() event's stop() comes here first
     */
    void stop() {
        t.startEncodersThread();
        super.stop();
    }
}

