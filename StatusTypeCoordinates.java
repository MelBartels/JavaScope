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

public class StatusTypeCoordinates extends IJFrameStatusTextAreaBase implements IJFrameStatusTextArea {
    public String update(track t) {
        astroTime.getInstance().calcSidT();

        s = "date/time: "
        + astroTime.getInstance().buildStringCurrentDateTime()
        + " JD: "
        + astroTime.getInstance().JD
        + " local sidereal time: "
        + astroTime.getInstance().sidT.getStringHMS()
        + "\n\n"
        + cfg.getInstance().current.stringPosName()
        + ": "
        + cfg.getInstance().current.buildString()
        + "\n"
        + cfg.getInstance().current.showStringCorrections()
        + "\n\n";

        if (t != null)
            s += t.target.stringPosName()
            + ": "
            + t.target.buildString()
            + "\n"
            + t.target.showStringCorrections()
            + "\n\n"
            + t.in.stringPosName()
            + ": "
            + t.in.buildString()
            + "\n"
            + t.savedIn.stringPosName()
            + ": "
            + t.savedIn.buildString()
            + "\n"
            + t.autoInit1.stringPosName()
            + ": "
            + t.autoInit1.buildString()
            + "\n"
            + t.autoInit2.stringPosName()
            + ": "
            + t.autoInit2.buildString()
            + "\n"
            + t.polarAlign1.stringPosName()
            + ": "
            + t.polarAlign1.buildString()
            + "\n"
            + t.polarAlign2.stringPosName()
            + ": "
            + t.polarAlign2.buildString()
            + "\n"
            + t.polarAlign3.stringPosName()
            + ": "
            + t.polarAlign3.buildString()
            + "\n"
            + t.buildDriftString();

        if (t != null && t.ef.E != null && t.ef.E.portOpened()) {
            s += "\nencoders status: query/read state: "
            + t.ef.E.getQueryAndReadSuccess()
            + "\n"
            + t.ef.E.buildCountsString()
            + "   "
            + t.ef.E.buildPositionsString()
            + "\n\n";
        }
        else
            s += "\n\nno encoders\n\n";

        if (t != null)
            if (cfg.getInstance().initialized()) {
                if (cfg.getInstance().Mount.meridianFlipPossible())
                    s += t.c.buildMeridianFlipStatusString();
                s += t.c.buildAdditionalConvertVarsString();
            }

        return s;
    }
}

