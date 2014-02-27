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

public class listEyepieceFocus {
    java.util.List lef;

    listEyepieceFocus() {
        lef = new ArrayList();
    }

    void init() {
        free();
    }

    void add(eyepieceFocus ef) {
        eyepieceFocus tempEf;

        lef.add(new eyepieceFocus());
        tempEf = (eyepieceFocus) lef.get(lef.size()-1);
        tempEf.copy(ef);
    }

    int size() {
        return lef.size();
    }

    eyepieceFocus get(int ix) {
        return (eyepieceFocus) lef.get(ix);
    }

    Iterator iterator() {
        return lef.iterator();
    }

    void free() {
        lef.clear();
    }

    void display() {
        Iterator it;
        eyepieceFocus ef;

        it = lef.iterator();
        while (it.hasNext()) {
            ef = (eyepieceFocus) it.next();
            ef.display();
        }
    }
}

