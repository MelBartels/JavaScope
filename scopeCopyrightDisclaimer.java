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

public class scopeCopyrightDisclaimer {
    static final String S =
    "\nCopyright BBAstroDesigns Inc. 2002-2004\n"
    + "LIMITED WARRANTY This software is provided ``as is'' and any express or "
    + "implied warranties, including, but not limited to, the implied warranties "
    + "of merchantability and fitness for a particular purpose are disclaimed. "
    + "in no event shall BBAstroDesigns be liable for any direct, indirect, "
    + "incidental, special, exemplary, nor consequential damages (including, but "
    + "not limited to, procurement of substitute goods or services, loss of use, "
    + "date, or profits, or business interruption) however caused and on any "
    + "theory of liability, whether in contract, strict liability, or tort "
    + "(including negligence or otherwise) arising in any way out of the use of "
    + "this software, even if advised of the possibility of such damage.\n";

    static void display() {
        console.stdOutLn(S);
    }
}
