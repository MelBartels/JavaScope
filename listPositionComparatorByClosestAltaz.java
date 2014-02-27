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

public class listPositionComparatorByClosestAltaz implements Comparator {
    position p;

    listPositionComparatorByClosestAltaz(position p) {
        this.p = p;
    }

    public int compare(Object o1, Object o2) {
        double sep1, sep2;

        position p1 = (position) o1;
        position p2 = (position) o2;

        sep1 = celeCoordCalcs.calcAltazAngularSep(p1, p);
        sep2 = celeCoordCalcs.calcAltazAngularSep(p2, p);
        if (sep1 < sep2)
            return -1;
        if (sep1 == sep2)
            return 0;
        return 1;
    }
}

