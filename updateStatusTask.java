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

public class updateStatusTask extends TimerTask {
    track t;

    updateStatusTask(track t) {
        this.t = t;
    }

    public void run() {
        t.writeStatusHTMLFile();
        t.writeServoStatusHTMLFile();
    }
}

