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
 * this is a list of Cmds available for executing;
 * Cmds are loaded via parseCmdFromFile() or parseCmdFromString(), then executed one at a time when checkProcessCmd() is called;
 * processCmd() loads pause time in seconds, then executes command: next command will not execute until pause time expires;
 * to add Cmds, add to CMD_SCOPE, then add class by command name adding methods:
 *    boolean parseCmd(StringTokenizer st) and boolean process(cmdScopeList s);
 * history of Cmds executed kept in cmdHistory;
 *
 * pauseSec is the time in seconds that the cmd list will pause for before executing the next command;
 * the pause time starts counting as soon as the command begins execution, ie, if cmd is 'stop 5', then
 * the motors will be commanded to stop and 5 secs later the next command will execute: if the motors
 * take 3 seconds to ramp down, then there will be a 2 sec pause, if the motors take 8 seconds to ramp
 * down, then there will no pause since commands are allowed to finish before next command is executed;
 * cmd_scope_quit has no pause time since any subsequent commands will not be executed;
 */
public class cmdScopeList {
    static final boolean CHECK_AUTO_ON_ONLY = true;
    static final boolean EXEC_CMD = false;

    boolean debug;
    String name;
    private static BufferedReader input;
    private String filename;
    String cmdScopeListString;

    boolean OKtoExecute;
    cmdScope first;
    cmdScope last;
    cmdScope current;
    int cmdsLoadedCount;
    int processingCmdsCount;
    boolean autoOn;
    double sidT;
    hmsm hmsm;

    boolean cmdIsProgressing;

    track t;

    cmdScopeList(String name) {
        this.name = new String(name);
        hmsm = new hmsm();
    }

    /**
     * following functions load in commands: can read from file or from string
     */
    
    /**
     * return true/fase based on if file successfully opened, not if command properly formatted
     */
    boolean parseCmdFromFile(String filename) {
        return parseCmdFromFile(filename, true);
    }
    
    boolean parseCmdFromFile(String filename, boolean OKtoExecute) {
        String s;

        if (filename == null) {
            console.errOut("cannot execute cmd: null filename in "
            + name
            + ": cmdScopeList.parseCmdFromFile()");
            return false;
        }

        this.filename = new String(filename);
        this.OKtoExecute = OKtoExecute;
        resetVars();

        try {
            input = new BufferedReader(new FileReader(filename));
            s = input.readLine();
            while (s != null) {
                parseStringBuildCmd(s);
                s = input.readLine();
            }
            input.close();
            if (debug)
                display();
            return true;
        }
        catch (IOException ioe) {
            console.errOut("could not open command file " + filename );
            return false;
        }
    }

    void parseCmdFromString(String s) {
        parseCmdFromString(s, true);
    }
    
    void parseCmdFromString(String s, boolean OKtoExecute) {
        filename = "(from command string)";
        this.OKtoExecute = OKtoExecute;
        resetVars();
        parseStringBuildCmd(s);
        if (debug)
            display();
    }

    void parseStringBuildCmd(String s) {
        String CmdName;
        String FullCmdName;
        StringTokenizer encoderst;
        StringTokenizer st;
        cmdScope cmd;

        CMD_SCOPE Type;
        azLong az = new azLong();

        // if s starts with ':' and contains a '#', then it is a LX200 command, so append to create correct class name
        if (s.length() > 0 && s.charAt(0) == ':' && s.indexOf('#') > -1) {
            s = eString.CMD_SCOPE_PREFIX
            + "LX200 "
            + s;
            // create tokens that include the delimiters so as to preserve the spaces in the LX200 commands for use
            // in class cmd_scope_LX200
            st = new StringTokenizer(s, " \t\n\r\f", true);
        }
        else
            st = new StringTokenizer(s);
        if (st.countTokens() > 0) {
            CmdName = st.nextToken();

            // ignore if comment
            if (CmdName.charAt(0) != ';' && CmdName.charAt(0) != '\'') {
                if (CmdName.startsWith(eString.CMD_SCOPE_PREFIX))
                    FullCmdName = CmdName;
                else
                    FullCmdName = eString.CMD_SCOPE_PREFIX + CmdName;
                Type = CMD_SCOPE.matchStr(FullCmdName);
                if (Type != null) {
                    cmd = new CmdScopeFactory().build(Type, st);
                    if (cmd != null)
                        add(cmd);
                }
                else
                    console.errOut("unrecognized cmdScopeList command of "
                    + CmdName
                    + " in cmdScopeList.parseStringBuildCmd()");
            }
        }
    }

    void init(track t) {
        this.t = t;
        first = last = current = null;
        debug = false;
        //debug = true;
    }

    void resetVars() {
        if (last != null)
            free();

        cmdIsProgressing = true;
        cmdsLoadedCount = 0;
        processingCmdsCount = 0;
        autoOn = false;
        sidT = astroTime.getInstance().calcSidT();
    }

    void resetToBeg() {
        current = null;
        processingCmdsCount = 0;
        autoOn = false;
        sidT = astroTime.getInstance().calcSidT();
    }

    /**
     * after adding the command to the list with default values, fill in the values by using the pointer last
     */
    void add(cmdScope c) {
        cmdsLoadedCount++;

        if (first == null) {
            c.prev(null);
            c.next(null);
            first = c;
        }
        else {
            c.prev(last);
            c.next(null);
            last.next(c);
        }
        // newly minted cmd is last in list so set last point to it
        last = c;
    }

    void remove(cmdScope c) {
        current = first;
        while (current != null && current != c)
            current = current.next();
        if (current == null) {
            if (cmdsLoadedCount > 0)
                common.badExit("command to remove not found in cmdScopeList.remove()");
        }
        else {
            cmdsLoadedCount--;
            if (current == first)
                if (current.next() == null)
                    first = last = current = null;
                else {
                    current.next().prev(null);
                    first = current = current.next();
                }
            else
                if (current == last) {
                    current.prev().next(null);
                    last = current = current.prev();
                }
                else {
                    current.next().prev(current.prev());
                    current.prev().next(current.next());
                }
        }
    }

    void free() {
        while (last != null)
            remove(last);
    }

    /**
     * return executing cmd number:
     * 0 = no cmd has been executed,
     * 1 = 1st cmd executing,
     * all cmds executed results in return value > cmdsLoadedCount
     */
    int executingCmdNum() {
        int num = 0;
        
        // no cmds executed yet
        if (cmdsLoadedCount == 0)
            num = 0;
        // cmds executing
        else if (processingCmdsCount < cmdsLoadedCount)
            num = processingCmdsCount;
        // on final cmd: check to see if final cmd is in progress: if not, mark as all completed
        else if (processingCmdsCount == cmdsLoadedCount)
            if (cmdIsProgressing || !checkTime())
                num = processingCmdsCount;
            else
                num = processingCmdsCount + 1;

        return num;
    }

    /**
     * same numbering scheme as executingCmdNum()
     */
    public void setCurrentToIndex(int index) {
        int ix;
        cmdScope counter;

        if (index == 0)
            resetToBeg();
        else {
            counter = first;
            processingCmdsCount = 1;
            for (ix = 1; ix < index && counter != null; ix++) {
                counter = counter.next();
                if (counter == null)
                    break;
                processingCmdsCount++;
            }
            current = counter;
        }
    }

    /**
     * return true if command time has expired
     */
    boolean checkTime() {
        double tDiff;

        tDiff = astroTime.getInstance().calcSidT() - sidT;
        if (tDiff < -units.HALF_REV)
            tDiff += units.ONE_REV;

        if (debug)
            System.out.println("debug checkTime() for "
            + name
            + " tDiff "
            + tDiff*units.RAD_TO_SEC);

        if (tDiff > 0.)
            return true;
        else
            return false;
    }

    /**
     * dir == DIRECTION.BACKWARD execute previous command, dir = anything else execute next command;
     * if autoOnOnly, then process next/prev command only if autoOn
     */
    boolean checkProcessCmd(DIRECTION dir, boolean autoOnOnly) {
        boolean cmdInProgressResult;

        if (debug)
            System.out.println("debug checkProcessCmd() for "
            + name
            + " cmdsLoadedCount "
            + cmdsLoadedCount
            + " processingCmdsCount "
            + processingCmdsCount
            + " current "
            + (current==null?"null":current.cmdTypeScope().toString())
            + " cmdInProgress() "
            + (current==null?"null":current.cmdInProgress()?"true":"false")
            + " first "
            + (first==null?"null":first.cmdTypeScope().toString())
            + " autoOnOnly "
            + autoOnOnly
            + " autoOn "
            + autoOn
            + " OKtoExecute "
            + OKtoExecute);
        // if ok to execute cmd sequence and any commands loaded
        if (OKtoExecute && cmdsLoadedCount > 0)
            // no commands processed or all commands processed
            if (current == null) {
                /**
                 * if no commands processed then set to first;
                 * once cmdScopeList is executed, that is, all commands processed, no further processing allowed;
                 * if last command is cmd_scope_reload_scroll and first command is cmd_scope_auto_scroll_on,
                 * then cmdScopeList will execute in endless loop
                 */
                if (processingCmdsCount == 0 && dir == DIRECTION.forward) {
                    current = first;
                    return processCmd();
                }
            }
            /**
             * certain commands like altazimuth moves and meridian flips must be allowed to run to completion before next command executed;
             * current.cmdInProgress() returns false by external functions that monitor progress or by loading new cmdScopeList
             */
            else {
                cmdInProgressResult = current.cmdInProgress();
                if (cmdInProgressResult && !cmdIsProgressing) {
                    console.stdOutLn("command in progress: " + name + " " + current.cmdTypeScope().toString());
                    cmdIsProgressing = true;
                }
                if (!cmdInProgressResult && cmdIsProgressing) {
                    console.stdOutLn("command has finished: " + name + " " + current.cmdTypeScope().toString());
                    cmdIsProgressing = false;
                }
                if (!cmdInProgressResult)
                    if (checkTime())
                        // either explicitly wish to process next/prev command, or, if autoOnOnly then execute only if auto scrolling is on
                        if (!autoOnOnly || autoOnOnly && autoOn) {
                            if (dir == DIRECTION.backward)
                                current = current.prev();
                            else
                                current = current.next();
                            if (current != null)
                                return processCmd();
                        }
            }
        return false;
    }
    
    void setProcessingCmdsCount() {
        int count;
        cmdScope counter;

        count = 1;
        if (current != null) {
            counter = first;
            while (counter != current) {
                count++;
                counter = counter.next();
            }
        }
        processingCmdsCount = count;
    }
    
    /**
     * process the cmds, return command success;
     * called from checkProcessCmd()
     */
    boolean processCmd() {
        if (current != null) {
            sidT = current.sec()*units.SEC_TO_RAD + astroTime.getInstance().sidT.rad;
            sidT = eMath.validRad(sidT);
            
            // was processingCmdsCount++;
            setProcessingCmdsCount();

            hmsm.rad = sidT;
            cmdHistory.getInstance().add(name
            + " "
            + hmsm.getStringHMSM()
            + " ("
            + processingCmdsCount
            + "/"
            + cmdsLoadedCount
            + "): "
            + current.buildCmdString());
        }

        // current may be set to null by another thread in middle of process, so continue
        // to check that current is valid
        if (current != null)
            console.stdOutLn("processing cmd: "
            + name
            + " ("
            + processingCmdsCount
            + "/"
            + cmdsLoadedCount
            + "): "
            + current.buildCmdString());

        if (current != null)
            return current.process(this);
        else
            return false;
    }

    String buildCmdScopeListString() {
        cmdScope current;
        int count = 1;

        cmdScopeListString  = "";
        current = first;
        while (current != null) {
            cmdScopeListString += name
            + "("
            + count++
            + "/"
            + cmdsLoadedCount
            + "): "
            + current.buildCmdString();
            current = current.next();
        }
        return cmdScopeListString;
    }

    void display() {
        console.stdOutLn("\ndisplay of cmdScopeList '"
        + name
        + "' "
        + filename
        + " commands loaded count "
        + cmdsLoadedCount);
        console.stdOutLn(buildCmdScopeListString());
        console.stdOutLn("end of cmdScopeList display\n");
    }
}

