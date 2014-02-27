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

public class eyepieceFocus {
    String name;
    long position;

    eyepieceFocus() {}

    eyepieceFocus(String name) {
        this.name = name;
    }

    eyepieceFocus(String name, long position) {
        this.name = name;
        this.position = position;
    }

    void copy(eyepieceFocus ef) {
        name = ef.name;
        position = ef.position;
    }

    void display() {
        System.out.println("eyepieceFocus name: "
        + name
        + " position: "
        + position);
    }
}

