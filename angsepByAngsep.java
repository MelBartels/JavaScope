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

class angsepByAngsep implements Comparator {
    public int compare(Object o1, Object o2) {
        angsep as1 = (angsep) o1;
        angsep as2 = (angsep) o2;
        return eMath.compareDouble(as1.angsep, as2.angsep);
    }
}
