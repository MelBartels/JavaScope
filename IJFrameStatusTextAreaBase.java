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
 * base class for StatusArea classes
 */
public class IJFrameStatusTextAreaBase {
    STATUS_TYPE st;
    String s;

    public void statusType(STATUS_TYPE st) {
        this.st = st;
    }

    public STATUS_TYPE statusType() {
        return st;
    }

    void buildServoStatusString(int id, track t) {
        s = t.buildStatusReturnString(id)
        + t.buildSimCurrentPosVelString(id)
        + t.buildStickyBitsString(id)
        + t.buildDisplayMoveString(id)
        + cfg.getInstance().servoParm[id].taa.buildMostRecentTrackErrorString(10)
        + t.buildGuidePECString(id)
        + t.buildBacklashString(id)
        + t.buildAxisToAxisCorrectionString(id)
        + t.buildPMCString(id);
    }
}

