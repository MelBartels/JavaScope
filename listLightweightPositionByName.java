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

public class listLightweightPositionByName implements Comparator {
    public int compare(Object o1, Object o2) {
        lightweightPosition lwp1 = (lightweightPosition) o1;
        lightweightPosition lwp2 = (lightweightPosition) o2;
        return lwp1.name.compareToIgnoreCase(lwp2.name);
    }
}

