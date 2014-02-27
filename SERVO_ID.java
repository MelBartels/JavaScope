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
 * typesafe enumerations:
 *
 * class defined as final therefore cannot be subclassed
 * + constructor private so other classes cannot use class to create new objects =
 * only objects created are the static objects class creates for itself first time class is referenced,
 * a variation of Simpleton pattern;
 *
 * class uses itself to create new objects; when static object referenced for first time static object created;
 * a variable of type of particular class can never be assigned to something other than the objects that the class creates;
 */
public final class SERVO_ID {
    private String id;
    // blank final variable: can be assigned only once (ie, a non-static final var);
    // necessary to be declared final as anonymous inner class accesses it;
    public final int KEY;
    private SERVO_ID prev;
    private SERVO_ID next;

    private static int itemCount;
    private static SERVO_ID first;
    private static SERVO_ID last;

    private SERVO_ID(String id) {
        this.id = new String(id);
        this.KEY = itemCount++;
        if (first == null)
            first = this;
        if (last != null) {
            this.prev = last;
            last.next = this;
        }
        last = this;
    }

    /**
     * Enumeration interface in java.util;
     * has 2 methods, hasMoreElements() returns boolean and nextElement() returns object of type object;
     * use interface as data type where Enumeration e = SERVO_ID.elements() invokes class regardless of class type;
     *
     * code uses anonymous inner class;*
     * inner class is class defined inside a class so it cannot be used outside of class;
     * anonymous class is class defined without name used once to instantiate a single object (can be used to extend existing
     * class or implement an interface - defines the implementation on the fly), has been called an inline coded class;
     * 'new' creates new object using definition contained in following brackets;
     * anonymous inner class can access enclosing class methods, ie, .next();
     *
     * 'return new Enumeration()' creates anonymous inner class with definition immediately following, of which two
     * functions must be implemented from the interface, and returns the Enumeration datatype when .elements()
     * is called externally;
     *
     * compiler compiles the anonymous inner classes into separate class files with names outerclassname$innerclassname.class
     * (if innerclassname anonymous, then outerclassname$1.class
     */
    public static Enumeration elements() {
        return new Enumeration() {
            private SERVO_ID current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                SERVO_ID c = current;
                current = current.next();
                return c;
            }
            // ';' required here because of 'return'
        };
    }

    /**
     * when string needed, Java invokes toString() searching inheritance hierarchy
     * (top is object class returning class name), so toString() should be defined;
     * can be invoked automatically by System.out.println("" + class_name),
     * don't need System.out.println("" + class_name.toString());
     */
    public String toString() {
        return this.id;
    }

    public static int size() {
        return itemCount;
    }

    public static SERVO_ID first() {
        return first;
    }

    public static SERVO_ID last() {
        return last;
    }

    public SERVO_ID prev() {
        return this.prev;
    }

    public SERVO_ID next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of SERVO_ID, which has "
        + itemCount
        + " elements:");
        SERVO_ID current = first;
        while (current != null) {
            console.stdOutLn(current.id
            + ": "
            + current.KEY);
            current = current.next;
        }
        console.stdOutLn("end of display");
    }

    public static String returnItemListAsString() {
        String s = "";

        SERVO_ID current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static SERVO_ID matchKey(int i) {
        SERVO_ID O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static SERVO_ID matchStr(String s) {
        SERVO_ID O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final SERVO_ID altDec = new SERVO_ID("altDec");
    public static final SERVO_ID azRa = new SERVO_ID("azRa");
    public static final SERVO_ID fieldR = new SERVO_ID("fieldR");
    public static final SERVO_ID focus = new SERVO_ID("focus");
}

