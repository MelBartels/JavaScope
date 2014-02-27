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
 * interface for JFrameStatusTextArea operations
 */
public interface IJFrameStatusTextArea {
    void statusType(STATUS_TYPE st);
    STATUS_TYPE statusType();
    String update(track t);
}

