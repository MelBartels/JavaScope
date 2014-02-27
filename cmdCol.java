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
 * a collection of the various cmd channels;
 * created by trackBuilder(); track object passed in so that cmd_scope actions can operate on class track methods in
 * cmdScopeList that belongs to the cmd channel in question;
 * regardless of source, commands processed in order that they are received and eligible in time to be processed;
 * consequently check needs to be made for re-entrant scope behavioral issues that may to occur if new command is
 * received while old command still in progress;
 * within a command channel sequence, cmdInProgress() used to prevent further execution until one of the above move
 * commands are completed; reloading that channel with a new command sequence will set the flag to false by default;
 */
public class cmdCol {
    // methods to communicate with programs that recognize external control files such as Project Pluto Guide
    externalSlewFiles extSlewFiles;
    // a list of cmd_scope and the methods to act on the list, including loading from a file;
    // auto executing is off by default
    cmdScopeList cmdScopeList;
    // uses one of the IO methods, receives a command string, sends it to an execStringCmdScope object, where a cmdScopeList
    // is created and executed
    ioCmd ioCmd;
    // user interface generated command string: sends it to an execStringCmdScope object, where a cmdScopeList
    // is created and executed
    UICmd UICmd;
    // a common LX200 object used to process LX200 commands from channels other than explicit LX200 command channel
    LX200 cmdLX200;
    // explicit LX200 command channel using any of the available IO methods
    LX200 LX200;

    cmdCol(track t) {
        extSlewFiles = new externalSlewFiles();

        cmdScopeList = new cmdScopeList("cmdCol");
        cmdScopeList.init(t);

        ioCmd = new ioCmd(t);

        UICmd = new UICmd(t);

        cmdLX200 = new LX200();
        cmdLX200.init(t);
        cmdLX200.portOpened = true;
        cmdLX200.displayDiagnostics = true;

        if (cfg.getInstance().LX200Control) {
            LX200 = new LX200();
            LX200.init(t);
            LX200.openPort(cfg.getInstance().LX200IOType,
            cfg.getInstance().LX200SerialPortName,
            cfg.getInstance().LX200BaudRate,
            cfg.getInstance().LX200homeIPPort,
            cfg.getInstance().LX200remoteIPName,
            cfg.getInstance().LX200RemoteIPPort,
            cfg.getInstance().LX200FileLocation,
            cfg.getInstance().LX200Trace);
        }
    }

    void checkProcessCmd() {
        // make check for scroll file - automatically executing cmdScopeList here

        if (cmdScopeList != null)
            cmdScopeList.checkProcessCmd(DIRECTION.forward, cmdScopeList.CHECK_AUTO_ON_ONLY);

        // check for commands via one of the IO methods
        if (ioCmd.portOpened)
            ioCmd.checkProcessCmd();

        // check for commands from the user interface
        if (UICmd != null)
            UICmd.checkProcessCmd();

        // check for LX200 commands
        if (cfg.getInstance().LX200Control && LX200.portOpened)
            LX200.readLX200Input();
    }

    void cancelAll() {
        // use this .t method as it will always be available
        cmdLX200.t.stopAllMotorsSmoothly();

        if (cmdScopeList != null)
            cmdScopeList.resetVars();

        if (ioCmd.portOpened)
            if (ioCmd.esc != null && ioCmd.esc.cl != null)
                ioCmd.esc.cl.resetVars();

        if (UICmd != null && UICmd.esc.cl != null)
            UICmd.esc.cl.resetVars();
    }

    void close() {
        if (cfg.getInstance().LX200Control)
            LX200.close();
    }
}

