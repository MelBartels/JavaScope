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
 * generic JPanel for terminal frames
 */
public class jPanelTerminal extends JPanelColor {
    PipedInputStream piOut;
    PipedOutputStream poOut;
    PrintStream holdOut;

    readerThread rt;
    writerThread wt;
    boolean stopThreads;

    int recChars;
    int xmtChars;
    boolean append;

    track t;
    IO io;

    private javax.swing.JScrollPane jScrollPaneTerminal;
    private javax.swing.JTextArea jTextAreaTerminal;

    jPanelTerminal() {
        holdOut = System.out;

        try {
            // set System.out
            piOut = new PipedInputStream();
            poOut = new PipedOutputStream(piOut);
            System.setOut(new PrintStream(poOut, true));
        }
        catch (IOException e) {}

        mediumColor();
        initComponents();
    }

    private void initComponents() {
        jScrollPaneTerminal = new javax.swing.JScrollPane();
        jTextAreaTerminal = new javax.swing.JTextArea();

        setLayout(new AbsoluteLayout());

        jScrollPaneTerminal.setViewportView(jTextAreaTerminal);

        add(jScrollPaneTerminal, new AbsoluteConstraints(0, 0, 400, 300));
    }

    void registerTrackReference(track t) {
        this.t = t;
    }

    void registerIOReference(IO io) {
        this.io = io;
    }

    void appendReturn(boolean append) {
        this.append = append;
    }

    void start() {
        /*
         * turn on display so that console will display to the piped stream;
         * could simply print the io received chars to the textarea though that would mean consuming them;
         */
        if (io!=null)
            io.displayReceivedChar(true);
        jTextAreaTerminal.setText("");
        recChars = xmtChars = 0;
        stopThreads = false;
        rt = new readerThread(piOut);
        rt.start();
        wt = new writerThread();
        wt.start();
    }

    void stop() {
        if (io!=null)
            io.displayReceivedChar(false);
        stopThreads = true;
        System.setOut(holdOut);
        if (t!=null)
            t.pauseSequencer = false;
    }

    class readerThread extends Thread {
        final byte[] buf = new byte[1024];
        PipedInputStream pis;

        readerThread(PipedInputStream pis) {
            this.pis = pis;
        }

        public void run() {
            try {
                // place while() inside of try so that an error concludes the outer run()
                while (!stopThreads) {
                    // blocking read
                    final int len = pis.read(buf);
                    if (len == -1)
                        break;
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            jTextAreaTerminal.append(new String(buf, 0, len));
                            // last line always visible
                            jTextAreaTerminal.setCaretPosition(jTextAreaTerminal.getDocument().getLength());
                            recChars += len;
                        }
                    });
                }
            }
            catch (IOException e) {}
        }
    }

    class writerThread extends Thread {
        String s = "";

        writerThread() {
        }

        public void run() {
            while (!stopThreads) {
                /**
                 * if jTextArea exceeds total of received chars + processed transmitted chars, then it is
                 * because user has typed in some chars;
                 * continue adding typed in chars until return is encountered, at which time, send the
                 * accumulated xmt chars out the io port;
                 */
                if (jTextAreaTerminal.getDocument().getLength() > recChars + xmtChars) {
                    s += jTextAreaTerminal.getText().substring(recChars + xmtChars);
                    xmtChars += jTextAreaTerminal.getDocument().getLength() - (recChars + xmtChars);
                    if (s.endsWith("\n")) {
                        if (append)
                            s += eString.RETURN;
                        io.writeStringPauseUntilXmtFinished(s);
                        s = "";
                    }
                }
                else
                    common.threadSleep(250);
            }
        }
    }
}

