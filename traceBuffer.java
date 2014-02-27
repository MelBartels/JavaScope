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

public class traceBuffer {
    String description;
    boolean trace;
    private static final int TRACE_BUFFER_SIZE = 1024;
    private traceElement[] traceBuffer;
    private int traceIx;
    private int begTraceIx;
    static final byte REC_MARK = (byte) 'r';
    static final byte XMIT_MARK = (byte) 't';
    private int traceCount;
    String mostRecentString;

    traceBuffer() {
        int ix;

        this.description = description;
        traceBuffer = new traceElement[TRACE_BUFFER_SIZE];
        for (ix = 0; ix < TRACE_BUFFER_SIZE; ix++)
            traceBuffer[ix] = new traceElement();
    }

    public void trace(boolean value) {
        boolean lastTrace = trace;
        trace = value;
        if (trace == true)
            traceIx = begTraceIx = traceCount = 0;
        else
            if (lastTrace == true)
                saveTraceToFile();
    }

    synchronized void addTrace( byte mark, byte b) {
        traceBuffer[traceIx].dir = mark;
        traceBuffer[traceIx].value = b;
        incrTraceIndeces();
        traceCount++;
    }

    void incrTraceIndeces() {
        traceIx++;
        if (traceIx >= TRACE_BUFFER_SIZE)
            traceIx = 0;
        if (begTraceIx == traceIx) {
            begTraceIx++;
            if (begTraceIx >= TRACE_BUFFER_SIZE)
                begTraceIx = 0;
        }
    }

    public String buildMostRecentString(int num) {
        if (trace)
            return buildMostRecentStringSubr(num);
        else {
            mostRecentString = "trace turned off";
            return mostRecentString;
        }
    }

    String buildMostRecentStringSubr(int num) {
        int ix;
        byte holdMark;

        mostRecentString = num + " most recent trace log entries";

        if (num > traceCount)
            num = traceCount;
        ix = traceIx - num;
        if (ix < 0)
            ix += TRACE_BUFFER_SIZE;

        holdMark = eString.SPACE;
        while (ix != traceIx) {
            if (holdMark != traceBuffer[ix].dir) {
                if (traceBuffer[ix].dir == REC_MARK)
                    mostRecentString += "\nrec: ";
                else if (traceBuffer[ix].dir == XMIT_MARK)
                    mostRecentString += "\nxmt: ";
                else
                    mostRecentString += "\n???: ";

                holdMark = traceBuffer[ix].dir;
            }
            // not (char) traceBuffer[ix] as chars printed to file, when read back in, may cause file to be unreadable
            mostRecentString += eString.intToHex((int) traceBuffer[ix].value) + "  ";
            ix++;
            if (ix >= TRACE_BUFFER_SIZE)
                ix = 0;
        }
        mostRecentString += "end of array\n";

        return mostRecentString;
    }

    void saveTraceToFile() {
        PrintStream output;
        String filename;
        int ix;
        byte holdMark;

        filename = description + eString.LOG_EXT;
        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(filename)));
            console.stdOutLn("saving trace log file " + filename);

            output.println("traceCount = " + traceCount);

            ix = begTraceIx;
            holdMark = eString.SPACE;
            while (ix != traceIx) {
                if (holdMark != traceBuffer[ix].dir) {
                    if (traceBuffer[ix].dir == REC_MARK)
                        output.print("\nrec: ");
                    else if (traceBuffer[ix].dir == XMIT_MARK)
                        output.print("\nxmt: ");
                    else
                        output.print("\n???: ");

                    holdMark = traceBuffer[ix].dir;
                }
                if (ix >= TRACE_BUFFER_SIZE)
                    ix = 0;
                // not (char) traceBuffer[ix] as chars printed to file, when read back in, may cause file to be unreadable
                output.print(eString.intToHex((int) traceBuffer[ix].value) + "  ");
                ix++;
                if (ix >= TRACE_BUFFER_SIZE)
                    ix = 0;
            }

            output.close();
        }
        catch (IOException ioe) {
            console.errOut("could not open " + filename);
        }
    }
}

