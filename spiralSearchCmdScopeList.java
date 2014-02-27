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

public class spiralSearchCmdScopeList extends spiralSearch {
    cmdScopeList cmdScopeList;

    spiralSearchCmdScopeList(convertMatrix c) {
        super(c);
        cmdScopeList = new cmdScopeList("spiralSearch");
        cmdScopeList.init(null);
    }

    void buildList() {
        int ix;
        position p;
        String s;

        cmdScopeList.resetVars();
        cmdScopeList.parseStringBuildCmd("cmd_scope_auto_scroll_on");

        buildSpiralSearchPatternListPosition();
        for (ix = 0; ix < listPos.lp.size(); ix++) {
            p = (position) listPos.lp.get(ix);

            s = "cmd_scope_slew_off_equat "
            + p.ra.getStringHMS(eString.SPACE)
            + " "
            + p.dec.getStringDMS(eString.SPACE)
            + " "
            + p.sidT.rad*units.RAD_TO_SEC;

            cmdScopeList.parseStringBuildCmd(s);
            cmdScopeList.OKtoExecute = true;
        }
    }

    void test() {
        buildList();
        listPos.display();
        cmdScopeList.display();
    }
}

