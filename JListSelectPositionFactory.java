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
 * factory that builds particular flavors of JListSelectPosition...
 */
public class JListSelectPositionFactory {
    IJListSelectPosition build(listPosition listPos, updateLabelMediator updateLabelMediator) {
        return new JListSelectPosition(listPos, updateLabelMediator);
    }

    IJListSelectPosition buildWithAngsep(listPosition listPos, updateLabelMediator updateLabelMediator) {
        return new JListSelectPositionWithAngsep(listPos, updateLabelMediator);
    }
}

