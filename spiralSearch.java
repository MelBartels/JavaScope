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
 * spiral search movement sequence:
 * 1 right
 * 1 up
 * 2 left
 * 2 down
 * 3 right
 * 3 up
 * 4 left
 * 4 down
 * 5 right
 * 5 up
 * 6 left
 * 6 down
 * ...
 *
 * movement needs to be measured by angular change at focal plane;
 * movement needs to include tracking such that the center of the spiral search pattern
 * does not drift backwards with respect to right ascension;
 *
 * algorithm:
 * up/down/left/right oriented with altaz axis;
 * from current equat coordinates, increment/decrement either alt or az axis depending on movement
 * sequence from above by the desired angular change at the focal plane, calculate resulting
 * equat coordinates, then move to these target equat coordinates over a period of time based on
 * desired speed, move to next sequence and repeat;
 */
public class spiralSearch {
    int NUM_LOOPS = 5;
    // loop consists of 4 direction changes
    int MAX_DIR = 4 * NUM_LOOPS;
    // stores net changes in equat coord (also stores net altaz changes, but these should not be used
    // to move the scope as they do not take into account the change in position due to tracking)
    listPosition listPos;
    position tempP;
    convertMatrix c;

    spiralSearch(convertMatrix c) {
        this.c = c;
        listPos = new listPosition();
        tempP = new position();
    }

    void buildSpiralSearchPatternListPosition() {
        position newPos = new position();
        double angMoveRad = cfg.getInstance().spiralSearchRadiusDeg * units.DEG_TO_RAD;
        double speedRadSec = cfg.getInstance().spiralSearchSpeedDegSec * units.DEG_TO_RAD;
        // start with move to the right or increasing az
        SPIRAL_SEARCH_SEQUENCE searchSeq = SPIRAL_SEARCH_SEQUENCE.right;
        // the total net change in altaz
        azDouble az = new azDouble();
        int steps = 1;
        double moveTimeSec = 0.;
        int dir;

        az.a = az.z = 0.;
        // set starting altaz
        tempP.copy(cfg.getInstance().current);
        astroTime.getInstance().calcSidT();
        c.getAltaz();
        // build a sequence of altaz w/ matching equat coord, placing in posCol
        for (dir = 0; dir < MAX_DIR; dir++) {
            if (searchSeq == SPIRAL_SEARCH_SEQUENCE.right) {
                az.z = angMoveRad * ((double) steps) / Math.cos(cfg.getInstance().current.alt.rad);
                cfg.getInstance().current.az.rad += az.z;
                searchSeq = SPIRAL_SEARCH_SEQUENCE.up;
                moveTimeSec = (double) steps * angMoveRad / speedRadSec;
            }
            else if (searchSeq == SPIRAL_SEARCH_SEQUENCE.up) {
                az.a = angMoveRad * (double) steps;
                cfg.getInstance().current.alt.rad += az.a;
                searchSeq = SPIRAL_SEARCH_SEQUENCE.left;
                moveTimeSec = (double) steps * angMoveRad / speedRadSec;
                steps++;
            }
            else if (searchSeq == SPIRAL_SEARCH_SEQUENCE.left) {
                az.z = -angMoveRad * ((double) steps) / Math.cos(cfg.getInstance().current.alt.rad);
                cfg.getInstance().current.az.rad += az.z;
                searchSeq = SPIRAL_SEARCH_SEQUENCE.down;
                moveTimeSec = (double) steps * angMoveRad / speedRadSec;
            }
            else if (searchSeq == SPIRAL_SEARCH_SEQUENCE.down) {
                az.a = -angMoveRad * (double) steps;
                cfg.getInstance().current.alt.rad += az.a;
                searchSeq = SPIRAL_SEARCH_SEQUENCE.right;
                moveTimeSec = (double) steps * angMoveRad / speedRadSec;
                steps++;
            }
            c.getEquat();
            newPos.ra.rad = eMath.validRadPi(cfg.getInstance().current.ra.rad - tempP.ra.rad);
            newPos.dec.rad = cfg.getInstance().current.dec.rad - tempP.dec.rad;
            newPos.alt.rad = az.a;
            newPos.az.rad = az.z;
            newPos.sidT.rad = moveTimeSec*units.SEC_TO_RAD;
            listPos.add(newPos);
        }
        cfg.getInstance().current.copy(tempP);
    }

    void test() {
        buildSpiralSearchPatternListPosition();
        listPos.display();
    }
}

