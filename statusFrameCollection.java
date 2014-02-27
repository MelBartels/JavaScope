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
 * holds a collection of statusFrames as defined in STATUS_TYPE;
 * allows generic JPanelStatus status frames to be linked to status updating through the JFrameStatusTextAreaFactory();
 */
public class statusFrameCollection {
    java.util.List lsf;
    private JFrameStatus JFrameStatus;
    private JFrameStatusTextAreaFactory JFrameStatusTextAreaFactory;
    private IJFrameStatusTextArea IJFrameStatusTextArea;

    statusFrameCollection() {
        lsf = new ArrayList();
        add();
    }

    private void add() {
        int ix;

        JFrameStatusTextAreaFactory = new JFrameStatusTextAreaFactory();
        for (ix = 0; ix < STATUS_TYPE.size(); ix++) {
            JFrameStatus = new JFrameStatus(STATUS_TYPE.matchKey(ix).description);
            IJFrameStatusTextArea = JFrameStatusTextAreaFactory.build(STATUS_TYPE.matchKey(ix));
            JFrameStatus.registerIJFrameStatusTextArea(IJFrameStatusTextArea);
            lsf.add(JFrameStatus);
        }
    }

    void registerTrackReference(track t) {
        int ix;

        for (ix = 0; ix < STATUS_TYPE.size(); ix++)
            ((JFrameStatus) lsf.get(ix)).registerTrackReference(t);
    }

    void setVisible(STATUS_TYPE st, boolean visible) {
        ((JFrameStatus) lsf.get(st.KEY)).setVisible(visible);
    }
}

