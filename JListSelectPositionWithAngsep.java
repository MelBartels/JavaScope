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
 * enhances JListSelectPosition's model to include angular separation from current position
 */
public class JListSelectPositionWithAngsep extends JListSelectPosition {
    JListSelectPositionWithAngsep(listPosition listPos, updateLabelMediator updateLabelMediator) {
        super(listPos, updateLabelMediator);
    }

    void createList() {
        int ix;

        for (ix = 0; ix < listPos.size(); ix++)
            model.addElement(dataFileListPositions.getInstance().buildNearestObjectDisplayLine(listPos, ix));
    }
}

