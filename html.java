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
 * class generates html pages displaying status of various systems
 */
public class html {
    static int updateStatusTimeSec;
    static int refreshStatusTimeSec;
    static int updateCurrEquatCoordTimeSec;
    static boolean timersChanged;
    static String cmdScopeHtmlString;
    static String cfgParmsHtmlString;
    static String servoParmsHtmlString;
    static String cfgServoParmsHtmlString;
    static String filesUsedHtmlString;
    static String cmdLineArgsHtmlString;

    static void writeHTMLFile(String filename, String css, int refreshInterval, String title, String content) {
        PrintStream output;
        String html;

        html = "<html>\n"
        + "<head>\n"
        + "<title>"
        + title
        + "</title>\n"
        + "<link rel=\"stylesheet\" type=\"text/css\" href=\""
        + css
        + "\">\n";
        if (refreshInterval > 0)
            html += "<META HTTP-EQUIV=\"Refresh\" CONTENT=\""
            + refreshInterval
            + "\">\n";
        html += "</head>\n"
        + "<body>\n"
        + "<pre>\n"
        + content
        + "\n"
        + "</pre>\n"
        + "</body>\n"
        + "</html>\n";

        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(filename)));
            output.print(html);
            output.close();
        }
        catch (IOException ioe) {
            console.errOut("could not open " + filename);
        }
    }

    static String convertBR(String s) {
        int ix;
        String r = "";

        for (ix = 0; ix < s.length(); ix++)
            if (s.charAt(ix) == '\n')
                r += "<br>";
            else
                r += s.charAt(ix);

        return r;
    }

    static String buildCmdScopeHtmlString() {
        cmdScopeHtmlString = "";
        cmdScopeHtmlString += "<table border=\"1\" width=\"100%\">" + "\n";

        Enumeration eCMD_SCOPE = CMD_SCOPE.elements();
        while (eCMD_SCOPE.hasMoreElements()) {
            CMD_SCOPE cs = (CMD_SCOPE) eCMD_SCOPE.nextElement();
            cmdScopeHtmlString += "<tr>" + "\n"
            + "<td width=\"30%\">"
            + cs
            + "</td>"
            + "<td width=\"70%\">"
            + convertBR(cs.description)
            + "</td>"
            + "</tr>"
            + "\n";
        }
        cmdScopeHtmlString += "</table>" + "\n";

        return cmdScopeHtmlString;
    }

    static String buildCfgParmsHtmlString() {
        cfgParmsHtmlString = "";
        cfgParmsHtmlString += "<table border=\"1\" width=\"100%\">" + "\n";

        Enumeration eCFG_PARM_NAME = CFG_PARM_NAME.elements();
        while (eCFG_PARM_NAME.hasMoreElements()) {
            CFG_PARM_NAME cp = (CFG_PARM_NAME) eCFG_PARM_NAME.nextElement();
            cfgParmsHtmlString += "<tr>"
            + "\n"
            + "<td width=\"20%\">"
            + cp
            + "</td>"
            + "<td width=\"80%\">"
            + convertBR(cp.description)
            + "</td>"
            + "</tr>"
            + "\n";
        }
        cfgParmsHtmlString += "</table>" + "\n";

        return cfgParmsHtmlString;
    }

    static String buildServoParmsHtmlString() {
        servoParmsHtmlString = "";
        servoParmsHtmlString += "<table border=\"1\" width=\"100%\">" + "\n";

        Enumeration eSERVO_PARM_NAME = SERVO_PARM_NAME.elements();
        while (eSERVO_PARM_NAME.hasMoreElements()) {
            SERVO_PARM_NAME sp = (SERVO_PARM_NAME) eSERVO_PARM_NAME.nextElement();
            servoParmsHtmlString += "<tr>"
            + "\n"
            + "<td width=\"20%\">"
            + sp
            + "</td>"
            + "<td width=\"80%\">"
            + convertBR(sp.description)
            + "</td>"
            + "</tr>"
            + "\n";
        }
        servoParmsHtmlString += "</table>" + "\n";

        return servoParmsHtmlString;
    }

    static String buildCfgServoParmsHtmlString() {
        cfgServoParmsHtmlString = "<h1><center>Configuration parameters</center></h1>";
        cfgServoParmsHtmlString += html.buildCfgParmsHtmlString();
        cfgServoParmsHtmlString += "<h1><center>servo configuration parameters</center></h1>";
        cfgServoParmsHtmlString += html.buildServoParmsHtmlString();

        return cfgServoParmsHtmlString;
    }

    static String buildFilesUsedHtmlString() {
        filesUsedHtmlString = "<h1><center>files Used</center></h1>";
        filesUsedHtmlString += convertBR(filesUsed.s);

        return filesUsedHtmlString;
    }

    static String buildCmdLineArgsHtmlString() {
        filesUsedHtmlString = "<h1><center>Command Line Arguments</center></h1>";
        cmdLineArgsHtmlString = convertBR(cmdLineArgs.S);

        return cmdLineArgsHtmlString;
    }
}

