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

public class limit_motion_equat extends limitMotionBase implements LimitMotion {
    double ha;

    limit_motion_equat() {
    }

    public boolean limitExceeded() {
        ha = eMath.validRadPi(cfg.getInstance().current.sidT.rad - cfg.getInstance().current.ra.rad);
        return super.limitExceeded(cfg.getInstance().current.dec.rad, ha);
    }

    public boolean limitExceeded(double alt, double az) {
        return super.limitExceeded(alt, az);
    }
}

