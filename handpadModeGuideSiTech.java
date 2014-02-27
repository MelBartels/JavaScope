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

public class handpadModeGuideSiTech extends handpadModeBase implements HandpadModes {
    void preInitAllGuide() {
        if (cfg.getInstance().handpadUpdateDrift)
            t.startDriftT();
        t.setSiTechGuideMode(true);
    }

    void preStopAllGuide() {
        if (cfg.getInstance().handpadUpdateDrift) {
            t.setEndDriftT();
            t.updateDriftCalculatedFromAccumGuide();
        }
        t.setSiTechGuideMode(false);
    }

    public void processModeKeys() {
        processGuideModeKeys();
    }
}


