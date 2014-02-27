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

public class StatusTypeInits extends IJFrameStatusTextAreaBase implements IJFrameStatusTextArea {
    public String update(track t) {
        if ( t != null) {
            s = t.c.buildInitString();
        }
        else
            s = "track module not instantiated";
        return s;
    }
}

