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
 * interface for all IO objects; some methods implemented in ioBase
 */
public interface IO {
    String ioType();
    void args(ioArgs args);
    ioArgs args();
    boolean portOpened();
    void description(String description);
    String description();
    boolean openWithArgs();
    void close();
    void displayReceivedChar(boolean value);
    void displayReceivedCharAsHex(boolean value);
    void trace(boolean value);
    String buildMostRecentString(int num);
    int countReadBytes();
    boolean waitForReadBytes(int numBytesToWaitFor, int bailIntervalMilliSec);
    boolean readSerialBuffer();
    byte returnByteRead();
    int readByteReturnUnsignedInt();
    String readString();
    void writeByte(byte b);
    void writeByteArray(byte b[], int len);
    void writeBytePauseUntilXmtFinished(byte b);
    void writeByteArrayPauseUntilXmtFinished(byte b[], int len);
    void writeString(String s);
    void writeStringPauseUntilXmtFinished(String s);
    void test();
}

