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

public class cmdScopeObjectBase extends cmdScopeBase {
    String filename;
    String dir;
    String object;

    boolean parseFilename(StringTokenizer st) {
        if (st.countTokens() >= 1) {
            filename = st.nextToken();
            return true;
        }
        return false;
    }

    boolean parseDirObject(StringTokenizer st) {
        if (st.countTokens() >= 2) {
            dir = st.nextToken();
            object = st.nextToken();
            return true;
        }
        return false;
    }
}

