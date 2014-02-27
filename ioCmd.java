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
 * using one of the IO methods, receive a command string, sending it to an execStringCmdScope object for execution
 */
public class ioCmd {
    track t;
    boolean portOpened;
    execStringCmdScope esc;
    IO io;
    ioFactory iof = new ioFactory();

    ioCmd(track t) {
        this.t = t;

        iof.args.serialPortName = cfg.getInstance().extSerialPortName;
        iof.args.baudRate = cfg.getInstance().extBaudRate;
        iof.args.homeIPPort = cfg.getInstance().extHomeIPPort;
        iof.args.remoteIPName = cfg.getInstance().extRemoteIPName;
        iof.args.remoteIPPort = cfg.getInstance().extRemoteIPPort;
        iof.args.fileLocation = cfg.getInstance().extFileLocation;
        iof.args.trace = cfg.getInstance().extTrace;

        io = iof.build(cfg.getInstance().extIOType);
        if (io != null)
            portOpened = true;
        else
            portOpened = false;
    }

    boolean checkProcessCmd() {
        if (io.countReadBytes() > 0) {
            common.threadSleep(cfg.getInstance().extPortWaitTimeMilliSecs);
            esc = new execStringCmdScope(io.ioType(), t, io.readString());
            esc.checkProcessCmd();
            esc.cl.debug = false;
            if (esc.cl.first == null)
                io.writeString("no valid commands received");
            else {
                io.writeString(esc.cl.buildCmdScopeListString());
                return true;
            }
        }
        else if (esc != null && esc.cl != null)
            return esc.cl.checkProcessCmd(DIRECTION.forward, cmdScopeList.EXEC_CMD);

        return false;
    }
}

