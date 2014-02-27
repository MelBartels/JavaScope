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

public class cmdScopePositionBase extends cmdScopeBase {
    position p;

    public String buildCmdString() {
        String s;

        if (p != null)
            s = "   position: "
            + p.stringPosName()
            + ": "
            + p.buildStringDeg()
            + "\n";
        else
            s = "";

        return super.buildCmdString() + s;
    }
}

