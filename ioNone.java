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

public class ioNone extends ioBase implements IO {

    public void close() {}

    public boolean openWithArgs() {
        ioType = IO_TYPE.ioNone;
        return false;
    }

    public void writeByte(byte b) {}

    public void writeByteArray(byte b[], int len) {}

    public void writeByteArrayPauseUntilXmtFinished(byte b[], int len) {}

    public void writeBytePauseUntilXmtFinished(byte b) {}

    public void writeString(String s) {}

    public void writeStringPauseUntilXmtFinished(String s) {}

    public void test() {}
}

