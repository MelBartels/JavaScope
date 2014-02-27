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

public class StatusTypeLX200 extends IJFrameStatusTextAreaBase implements IJFrameStatusTextArea {
    public String update(track t) {
        if (t != null)
            if (cfg.getInstance().LX200Control && t.cmdCol.LX200.portOpened) {
                s = t.cmdCol.LX200.buildLogString(100);
                s += t.cmdCol.LX200.buildMostRecentString(500);
            }
            else
                s = "no LX200 port opened";
        else
            s = "track module not instantiated";
        return s;
    }
}

