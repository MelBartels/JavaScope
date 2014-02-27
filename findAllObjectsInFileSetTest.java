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
 * test of findAllObjectsInFileSet class
 */
public class findAllObjectsInFileSetTest {
        String object;
        String dir;
        listPosition listPos = new listPosition();

    void test() {
        console.stdOut("Please enter object name (ie, M42): ");
        console.getString();
        object = console.s;
        console.stdOut("Please enter the directory to search the data files in (ie, c:/temp/): ");
        console.getString();
        dir = console.s;

        findAllObjectsInFileSet faofs = new findAllObjectsInFileSet(object, dir, true);
        faofs.findAllObjects(listPos);
        listPos.showDataFileFormat();
    }
}

