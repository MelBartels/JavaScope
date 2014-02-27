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
 *exercises class findAllObjectsInDataDir
 */
public class testFindAllObjectsInDataDir {
    testFindAllObjectsInDataDir() {
        System.out.print("enter object name to search for ");
        console.getString();
        findAllObjectsInDataDir faoidd = new findAllObjectsInDataDir(console.s, true);
        if (faoidd.dirSelected())
            faoidd.listPos.showDataFileFormat();
    }
}

