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

public class listLimitWindowComparator implements Comparator {
    LINK_POS_SORT_KEY key;

    listLimitWindowComparator(LINK_POS_SORT_KEY key) {
        this.key = key;
    }

    public int compare(Object o1, Object o2) {
        limitWindow lw1 = (limitWindow) o1;
        limitWindow lw2 = (limitWindow) o2;

        if (key == LINK_POS_SORT_KEY.azAscend || key == LINK_POS_SORT_KEY.raAscend)
            return eMath.compareDouble(lw1.z, lw2.z);
        else if (key == LINK_POS_SORT_KEY.azDescend || key == LINK_POS_SORT_KEY.raDescend)
            return eMath.compareDouble(lw2.z, lw1.z);
        else if (key == LINK_POS_SORT_KEY.altAscend || key == LINK_POS_SORT_KEY.absAltAscend || key == LINK_POS_SORT_KEY.decAscend)
            return eMath.compareDouble(lw1.lowA, lw2.lowA);
        else if (key == LINK_POS_SORT_KEY.altDescend || key == LINK_POS_SORT_KEY.absAltDescend || key == LINK_POS_SORT_KEY.decDescend)
            return eMath.compareDouble(lw2.lowA, lw1.lowA);
        return 0;
    }
}

