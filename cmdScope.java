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
 * interface for all cmdScope commands
 */
public interface cmdScope {
    void cmdTypeScope(CMD_SCOPE cmdTypeScope);
    CMD_SCOPE cmdTypeScope();
    void prev(cmdScope c);
    cmdScope prev();
    void next(cmdScope c);
    cmdScope next();
    double sec();
    String LX200str();
    boolean parseCmd(StringTokenizer st);
    boolean process(cmdScopeList cl);
    boolean cmdInProgress();
    String buildCmdString();
}

