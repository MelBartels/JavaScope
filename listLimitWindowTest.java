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

public class listLimitWindowTest {
    Iterator it;
    limitWindow lw;
    java.util.List llw = new ArrayList();

    listLimitWindowTest() {
        System.out.println("test of listLimitWindow");
        System.out.println("adding 3 new limitWindows to the list, then sorting");

        llw.add(new limitWindow());
        lw = (limitWindow) llw.get(llw.size()-1);
        lw.z = 1.*units.DEG_TO_RAD;

        llw.add(new limitWindow());
        lw = (limitWindow) llw.get(llw.size()-1);
        lw.z = 3.*units.DEG_TO_RAD;

        llw.add(new limitWindow());
        lw = (limitWindow) llw.get(llw.size()-1);
        lw.z = 2.*units.DEG_TO_RAD;

        Collections.sort(llw, new listLimitWindowComparator(LINK_POS_SORT_KEY.azAscend));

        it = llw.iterator();
        while (it.hasNext()) {
            lw = (limitWindow) it.next();
            System.out.print(lw.z*units.RAD_TO_DEG + "   ");
        }
        System.out.println("");
        System.out.println("end of listLimitWindow test");
    }
}

