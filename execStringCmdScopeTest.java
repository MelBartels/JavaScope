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
 * test for execStringCmdScope class
 */
public class execStringCmdScopeTest {
    track t; {
        System.out.println("executing command from parsed string test");
        System.out.println("enter the command using the cmdScopeList command format:");
        console.getString();
        execStringCmdScope esc = new execStringCmdScope("Test", t, console.s);
        esc.checkProcessCmd();
    }
}

