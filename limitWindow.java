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
 * a window to limit motion within;
 * at azimuth 'z', motion is legal at altitudes between lowA and highA;
 */
public class limitWindow {
    double z;
    double lowA;
    double highA;
    String displayString;

    limitWindow() {
        z = 0.;
        lowA = -units.QTR_REV;
        highA = units.QTR_REV;
    }

    limitWindow(double z, double lowA, double highA) {
        this.z = z;
        this.lowA = lowA;
        this.highA = highA;
    }

    void copy(limitWindow lw) {
        this.z = lw.z;
        this.lowA = lw.lowA;
        this.highA = lw.highA;
    }

    String buildDisplayString() {
        displayString = "z: "
        + z*units.RAD_TO_DEG
        + " lowA: "
        + lowA*units.RAD_TO_DEG
        + " highA: "
        + highA*units.RAD_TO_DEG;
        return displayString;
    }

    void display() {
        console.stdOutLn(buildDisplayString());
    }
}

