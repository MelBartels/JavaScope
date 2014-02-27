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
 * add state variables to the PICServo simulator list
 */
public class PICServoSimListState {
    int address;
    byte groupAddress;

    byte defineStatus;
    boolean ampEnabled;
    boolean startMotionNow;
    ROTATION dir;
    boolean velProfile;
    long position;
    long vel;
    long accel;

    // status byte bits
    byte moveDone;
    byte checksumError;
    byte overCurrent;
    byte powerOn;
    byte positionError;
    byte limit1;
    byte limit2;
    byte homeInProgress;

    // aux status byte bits
    byte index;
    byte positionWrap;
    byte servoOn;
    byte accelDone;
    byte slewDone;
    byte timerOverrun;

    boolean stickyBitPosError;
    boolean stickyBitOverCurrent;
    boolean stickyBitTimerOverrun;
    boolean stickyBitPosWrap;

    PICServoSimList pssl;

    PICServoSimListState(double startJD) {
        pssl = new PICServoSimList(startJD);
        groupAddress = servo.GROUP_ADDRESS;
    }
}

