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

public class limit_motion_siteAltaz extends limitMotionBase implements LimitMotion {
    convertTrig site;

    limit_motion_siteAltaz() {
        site = new convertTrig("limitMotion");
    }

    public boolean limitExceeded() {
        site.p.ra.rad = cfg.getInstance().current.ra.rad;
        site.p.dec.rad = cfg.getInstance().current.dec.rad;
        site.p.sidT.rad = cfg.getInstance().current.sidT.rad;
        site.getAltaz();
        return super.limitExceeded(site.p.alt.rad, site.p.az.rad);
    }

    public boolean limitExceeded(double alt, double az) {
        return super.limitExceeded(alt, az);
    }
}

