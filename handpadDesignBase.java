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
 * handpad hookup for servo system:
 *
 * Cable -
 * The handpad uses 4 data lines and 2 +5V DC supplied in a flat 6 wire RJ12 phone style cable. Cable is
 * a straight through arrangement where RJ12 connectors show same wiring color sequence from left to right
 * when placed side by side with clips down and cables heading away.  See SiTech's optional modification
 * of the wiring scheme below where one of the +5VDC lines is used as a ground...
 *
 * Connections -
 * with Gary Myers unit, plug the cable into the port on the slave - not the master
 *
 * Use Jameco RJ12 connector part number 124038 on the servo control board side.
 * The data lines are attached to the altDec motor and the azRa motor limit1 and
 * index input pins as follows:
 * looking into the handpad's RJ12 connector with clip down numbering from left to right, and on the cable's other
 * end, wire colors coming from the servo control boards' connector:
 * pin 1 blue +5vdc
 * pin 2 yellow altDec motor1 limit1
 * pin 3 green +5vdc
 * pin 4 white azRa motor2 index
 * pin 5 black altDec motor1 index
 * pin 6 red azRa motor2 limit1
 *
 * in SiTech's controller -
 * Pin 3 is GND or +5 volts, depending on the jumper setting of the jumber between the serial connector and hand pad connector.
 * If the jumper is toward the edge of the board, it is connected to ground, and it is not "Mel Bartels handpad compatible".
 * If the jumper is toward the MAX233 chip, pin 3 is connected to +5 volts, and is compatible with Mel Bartels handpad.
 *
 * handpad switches and push buttons:
 *
 * upper part of handpad is a 3 way switch, or alternatively, two 2 way switches, and is wired in the following manner:
 * switch position #1, for initializing position #1 and other functions, activates motor1 limit1, motor1 index
 * switch position #2, neutral: does nothing, activates nothing
 * switch position #3, for initializing position #2 and other functions, activates motor2 index, motor1 index
 * switch also used for other functions via keyboard direction;
 * lower part of handpad consists of 4 momentary on buttons in the following pattern with a 2 way
 * switch in the middle:
 *   *
 * * s *
 *   *
 * upper button = Up, activates motor1 limit1
 * lower button = Down, activates motor2 index
 * left button = CCW, activates motor1 index
 * right button = CW, activates motor1 limit1, motor2 index
 * (alternate handpad arrangement for external guiding inputs that precludes speed switch and mode switch since
 * all 4 lines are devoted to directional input is for right button = CW to activate motor2 limit1)
 * switch position #1, slow speed, activates motor2 limit1
 * switch position #2, fast speed, activates nothing
 *
 * SiTech's arrangement:
 *
 * upper part of handpad is a 3 way switch, or alternatively, two 2 way switches, and is wired in the following manner:
 * switch position #1, for initializing position #1 and other functions, activates motor2 limit1, motor1 index
 * switch position #2, neutral: does nothing, activates nothing
 * switch position #3, for initializing position #2 and other functions, activates motor2 index, motor1 limit1
 * switch also used for other functions via keyboard direction;
 * lower part of handpad consists of 4 momentary on buttons in the following pattern with a 2 way
 * switch in the middle:
 *   *
 * * s *
 *   *
 * upper button = Up, activates motor1 limit1
 * lower button = Down, activates motor2 index
 * left button = CCW, activates motor1 index
 * right button = CW, activates motor2 limit1
 * switch position #1, slow speed, activates motor1 limit1, motor2 index, motor1 index
 * switch position #2, fast speed, activates nothing
 *
 * SiTech's controller returns handpad state in the ID byte of the status version/ID bytes return -
 * When you read the version and ID, instead of returning the id, it returns the keys:
 * You can read this unsigned char, and the bits mean the following:
 * ;KeyBits.0  LEFT
 * ;KeyBits.1  RIGHT
 * ;KeyBits.2  UP
 * ;KeyBits.3  DOWN
 * ;KeyBits.4  SWITCH
 * ;KeyBits.5  TOP_RIGHT
 * ;KeyBits.6  TOP_LEFT
 * ;KeyBits.7  USER_MOVED_SCOPE
 * If you find bit 7 set, you should issue another tracking command.  It means, a user has pressed a direction
 * key to adjust the scope. When you issue a define status command, if you set bit 7, microcontroller operation
 * of the hand paddle is disabled. If you leave it clear, its enabled.  This is only true for the lower address module (altitude)
 * Bit 7 will clear as soon as you issue a load trajectory with move now, or a move now command (either motor).
 * the SiTech microcontroller always controls the motor based on handpad reads.
 * When a user lets up on the handpaddle, the motors spin at old rate and direction until a new move motor command is sent.
 *
 * handpad is read after every getStatus() but .handpadPortsRead check and debouncing routine ensure that both motor's ports are
 * read successfully before handpadSuccessfullyRead is set true
 *
 * handpad functions to be used outside of class:
 *    void initHandpad()
 *    void readHandpad()
 *    String buildHandpadStatusString()
 *    void displayHandpadStatus()
 */
public class handpadDesignBase {
    static final int UP_KEY = 1;
    static final int DOWN_KEY = 2;
    static final int CCW_KEY = 4;
    static final int CW_KEY = 8;
    static final int LEFT_KEY = 16;
    static final int RIGHT_KEY = 32;
    static final int SLOW_KEY = 64;

    static final int A_MOTOR_LIMIT_1 = 16;
    static final int Z_MOTOR_INDEX = 32;
    static final int A_MOTOR_INDEX = 64;
    static final int Z_MOTOR_LIMIT_1 = 128;

    HANDPAD_DESIGN handpadDesign;

    // number of times for handpad to be read with same value before handpad return value allowed to be reported as accurate:
    // 4 data lines are used on 2 servo boards (altDdec and azRa motors) which do not all respond simultaneously
    private static final int MAX_DEBOUNCE_HANDPAD = 2;
    // debounce the read value: all elements in array must agree
    int[] debounceHandpad = new int[MAX_DEBOUNCE_HANDPAD];
    int lastDebounceHandpadIx;
    // if handpad was successfully read and debounced
    boolean handpadSuccessfullyRead;
    // up, down buttons reversed
    boolean upDownButtonsReversed;
    // raw value from the input ports
    int handpad;
    // handpad decoded into button presses
    int buttons;
    // if false then scope should merely observe controller's operation of handpad;
    // at this time this is only a SiTech option;
    boolean passiveObserver;

    handpadDesignBase() {
        lastDebounceHandpadIx = 0;
    }

    public void handpadDesign(HANDPAD_DESIGN handpadDesign) {
        this.handpadDesign = handpadDesign;
    }

    public HANDPAD_DESIGN handpadDesign() {
        return handpadDesign;
    }

    public void handpad(int handpad) {
        this.handpad = handpad;
    }

    public boolean handpadSuccessfullyRead() {
        return handpadSuccessfullyRead;
    }

    public boolean upDownButtonsReversed() {
        return upDownButtonsReversed;
    }

    void setUpDownButtonsReversed() {
        if (cfg.getInstance().handpadFollowMeridianFlip)
            upDownButtonsReversed = !cfg.getInstance().handpadFlipUpDown;
        else
            upDownButtonsReversed = cfg.getInstance().handpadFlipUpDown;
    }

    void buildHandpadValue() {
        handpad = 0;
        if (cfg.getInstance().spa.limit1)
            handpad += A_MOTOR_LIMIT_1;
        if (cfg.getInstance().spa.index)
            handpad += A_MOTOR_INDEX;
        if (cfg.getInstance().spz.limit1)
            handpad += Z_MOTOR_LIMIT_1;
        if (cfg.getInstance().spz.index)
            handpad += Z_MOTOR_INDEX;
    }

    void debounceHandpad() {
        int ix;

        lastDebounceHandpadIx++;
        if (lastDebounceHandpadIx >= MAX_DEBOUNCE_HANDPAD)
            lastDebounceHandpadIx = 0;
        debounceHandpad[lastDebounceHandpadIx] = handpad;
        for (ix = 0; ix < MAX_DEBOUNCE_HANDPAD; ix++)
            if (handpad != debounceHandpad[ix]) {
                buttons = 0;
                handpadSuccessfullyRead = false;
                break;
            }
    }

    public void readHandpad() {
        if (cfg.getInstance().spa.handpadPortsRead && cfg.getInstance().spz.handpadPortsRead) {
            handpadSuccessfullyRead = true;
            buildHandpadValue();
            setUpDownButtonsReversed();
            setButtons();
            debounceHandpad();
        }
        else
            handpadSuccessfullyRead = false;
    }

    void setButtons() {
    }

    public int buttons() {
        return buttons;
    }

    public void passiveObserver(boolean passiveObserver) {
        this.passiveObserver = passiveObserver;
    }

    public boolean passiveObserver() {
        return passiveObserver;
    }

    public String displayHandpad() {
        String s;

        s = ((handpad&A_MOTOR_LIMIT_1)==A_MOTOR_LIMIT_1?"aLimit1":"  ")
        + ((handpad&A_MOTOR_INDEX)==A_MOTOR_INDEX?"Aindex":"  ")
        + ((handpad&Z_MOTOR_LIMIT_1)==Z_MOTOR_LIMIT_1?"zLimit1":"  ")
        + ((handpad&Z_MOTOR_INDEX)==Z_MOTOR_INDEX?"Zindex":"  ");

        return s;
    }
}

