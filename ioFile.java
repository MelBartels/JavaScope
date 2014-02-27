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
 * use files to communicate input and output;
 * hardcoded files names: output = eString.IO_FILE_WRITE and input = eString.IO_FILE_READ;
 * reading is via a timer thread that fires at an interval of extPortWaitTimeMilliSecs;
 */
public class ioFile extends ioBase implements IO {
    File dir;
    String writeFilename;
    String readFilename;
    static BufferedReader input;
    static PrintStream output;
    long lastModified;
    long prevLastModified;
    File readFile;
    java.util.Timer readTimer;

    public void close() {
        readTimer.cancel();
        portOpened = false;
    }

    public boolean openWithArgs() {
        ioType = IO_TYPE.ioFile;
        dir = new File(args.fileLocation);
        if (dir.exists()) {
            writeFilename = args.fileLocation + eString.IO_FILE_WRITE;
            readFilename = args.fileLocation + eString.IO_FILE_READ;
            readFile = new File(readFilename);
            readTimer = new java.util.Timer();
            readTimer.schedule(new read(), 0, cfg.getInstance().extPortWaitTimeMilliSecs);
            portOpened = true;
            return true;
        }
        return false;
    }

    class read extends TimerTask {
        public void run() {
            int i;
            if (getFileDateTimeStamp() && modifiedSinceLastCheck()) {
                console.stdOutLn(readFilename + " modified");
                try {
                    input = new BufferedReader(new FileReader(readFilename));
                    i = input.read();
                    while (i != -1) {
                        processReceivedChar((byte) i);
                        i = input.read();
                    }
                    input.close();
                }
                catch (IOException ioe) {
                    console.errOut("could not open " + readFilename);
                }
            }
        }
    }

    private boolean getFileDateTimeStamp() {
        if (readFile.exists() && readFile.isFile() && !readFile.isDirectory()) {
            lastModified = readFile.lastModified();
            return true;
        }
        return false;
    }

    private boolean modifiedSinceLastCheck() {
        if (lastModified > prevLastModified) {
            prevLastModified = lastModified;
            return true;
        }
        return false;
    }

    public void writeByte(byte b) {
        writeString(String.valueOf(b));
    }

    public void writeByteArray(byte b[], int len) {
        int ix;
        String s = "";

        for (ix = 0; ix < len; ix++)
            s += String.valueOf(b[ix]);
        writeString(s);
    }

    public void writeByteArrayPauseUntilXmtFinished(byte b[], int len) {
        writeByteArray(b, len);
    }

    public void writeBytePauseUntilXmtFinished(byte b) {
        writeByte(b);
    }

    public void writeString(String s) {
        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(writeFilename)));
            output.println(s);
            output.close();
        }
        catch (IOException ioe) {
            console.errOut("could not open " + writeFilename);
        }
    }

    public void writeStringPauseUntilXmtFinished(String s) {
        writeString(s);
    }

    public void test() {
        System.out.println("ioFile test");

        System.out.print("enter fileLocation ");
        console.getString();
        args.fileLocation = console.s;
        System.out.println("valid fileLocation? " + openWithArgs());
        System.out.print("turn on trace log (true/false) ? ");
        args.trace = console.getBoolean();
        displayReceivedChar = true;
        if (openWithArgs()) {
            System.out.println("enter '!' to exit");
            while (true) {
                console.getString();
                if (console.s.equalsIgnoreCase("!"))
                    break;
                else
                    writeString(console.s);
            }
            close();
        }
        displayReceivedChar = false;
        System.out.println("end of ioFile test");
    }
}

