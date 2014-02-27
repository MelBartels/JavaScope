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

public class linkGuideTest {
    linkGuideTest() {
        System.out.println("testing linkGuide, guide, PEC");
        System.out.println("a PEC table should be created for each of the following 3 scenarios:");
        guideParms gp = new guideParms();
        gp.servoIDStr = SERVO_ID.altDec.toString();
        gp.descriptStr = "Main";
        gp.motorStepsPerPECArray = 2048;
        gp.PECSize = 200;
        gp.guidingCycles = 10;
        gp.PECRotation = ROTATION.biDir;
        gp.PECIxOffset = 0.;
        System.out.println("   creating PEC...");
        PEC p = new PEC(gp.servoIDStr, gp.descriptStr, gp.motorStepsPerPECArray, gp.PECSize, gp.PECRotation, gp.PECIxOffset);
        System.out.println("   creating guide...");
        guide g = new guide(gp);
        java.util.List lg = new ArrayList();
        System.out.println("   adding a guide to newly minted linkGuide...");
        lg.add(new guide( gp));
        System.out.println("end of test linkGuide, guide, PEC");
    }
}

