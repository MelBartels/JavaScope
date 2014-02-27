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

public class listPositionComparator implements Comparator {
    LINK_POS_SORT_KEY key;

    listPositionComparator(LINK_POS_SORT_KEY key) {
        this.key = key;
    }

    public int compare(Object o1, Object o2) {
        position p1 = (position) o1;
        position p2 = (position) o2;
        double bAlt, iAlt;

        // map altitudes into 0 to 360 deg space
        bAlt = p1.alt.rad;
        if (bAlt < 0.)
            bAlt += units.ONE_REV;
        iAlt = p2.alt.rad;
        if (iAlt < 0.)
            iAlt += units.ONE_REV;

        if (key == LINK_POS_SORT_KEY.raAscend)
            return eMath.compareDouble(p1.ra.rad, p2.ra.rad);
        else if (key == LINK_POS_SORT_KEY.decAscend)
            return eMath.compareDouble(p1.dec.rad, p2.dec.rad);
        else if (key == LINK_POS_SORT_KEY.absAltAscend)
            return eMath.compareDouble(bAlt, iAlt);
        else if (key == LINK_POS_SORT_KEY.altAscend)
            return eMath.compareDouble(p1.alt.rad, p2.alt.rad);
        else if (key == LINK_POS_SORT_KEY.azAscend)
            return eMath.compareDouble(p1.az.rad, p2.az.rad);
        else if (key == LINK_POS_SORT_KEY.sidTAscend)
            return eMath.compareDouble(p1.sidT.rad, p2.sidT.rad);
        else if (key == LINK_POS_SORT_KEY.raDescend)
            return eMath.compareDouble(p2.ra.rad, p1.ra.rad);
        else if (key == LINK_POS_SORT_KEY.decDescend)
            return eMath.compareDouble(p2.dec.rad, p1.dec.rad);
        else if (key == LINK_POS_SORT_KEY.absAltDescend)
            return eMath.compareDouble(iAlt, bAlt);
        else if (key == LINK_POS_SORT_KEY.altDescend)
            return eMath.compareDouble(p2.alt.rad, p1.alt.rad);
        else if (key == LINK_POS_SORT_KEY.azDescend)
            return eMath.compareDouble(p2.az.rad, p1.az.rad);
        else if (key == LINK_POS_SORT_KEY.sidTDescend)
            return eMath.compareDouble(p2.sidT.rad, p1.sidT.rad);
        return 0;
    }
}

