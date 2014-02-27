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

public class handpadModeGuideStayRotate extends handpadModeBase implements HandpadModes {
    void preInitAllGuide() {
        if (cfg.getInstance().handpadUpdateDrift)
            t.startDriftT();
    }

    void preStopAllGuide() {
        // this method will cause the scope to stay on the guide star when guiding finished
        t.addAccumGuideDriftToCurrentPosition();
        if (cfg.getInstance().handpadUpdateDrift) {
            t.setEndDriftT();
            t.updateDriftCalculatedFromAccumGuide();
        }
    }
    
    public void processModeKeys() {
        processGuideModeKeys();
    }
}

