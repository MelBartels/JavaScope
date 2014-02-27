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
 * class to test axisToAxisEC list
 */
public class listAxisToAxisECTest {
    listAxisToAxisECTest() {
        Iterator it;
        System.out.println("test of list AxisToAxisEC");
        java.util.List laaec = new ArrayList();
        laaec.add(new axisToAxisEC(AXIS_TO_AXIS_EC.altAlt));
        laaec.add(new axisToAxisEC(AXIS_TO_AXIS_EC.altAz));
        laaec.add(new axisToAxisEC(AXIS_TO_AXIS_EC.azAz));
        it = laaec.iterator();
        while (it.hasNext()) {
            axisToAxisEC aaec = (axisToAxisEC) it.next();
            System.out.println(aaec.axisToAxisName);
        }

        // build_EC_FromAnalysisErrorsInMemory() test done in TEST_SELECT.axisToAxisEC
        System.out.println("end of test of list AxisToAxisEC");
    }
}

