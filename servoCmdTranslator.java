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
 * class containing string descriptions of each servo command recordable in the servo command log
 */
public class servoCmdTranslator {
    static String getString(byte cmd) {
        return getServoCmd(cmd).toString();
    }

    static SERVO_CMD getServoCmd(byte cmd) {
        switch(cmd) {
            case servo.CMD_HARD_RESET:
                return SERVO_CMD.hardReset;
            case servo.CMD_SET_ADDRESS:
                return SERVO_CMD.setAddress;
            case servo.CMD_DEFINE_STATUS:
                return SERVO_CMD.defineStatus;
            case servo.CMD_NOP:
                return SERVO_CMD.getStatus;
            case servo.CMD_RESET_POSITION:
                return SERVO_CMD.resetPosition;
            case servo.CMD_SET_GAIN:
                return SERVO_CMD.setGain;
            case servo.CMD_START_MOTOR:
                return SERVO_CMD.startMotor;
            case servo.CMD_STOP_MOTOR:
                return SERVO_CMD.stopMotor;
            case servo.CMD_CLEAR_STICKY_BITS:
                return SERVO_CMD.clearStickyBits;
            case servo.CMD_LOAD_TRAJECTORY_POSITION:
                return SERVO_CMD.loadTrajPos;
            case servo.CMD_LOAD_TRAJECTORY_VEL:
                return SERVO_CMD.loadTrajVel;
            case servo.CMD_EXT_NOP_HANDPAD_SLEW_PAN_SPEEDS:
                return SERVO_CMD.SiTechHandpadSlewPanSpeeds;
            case servo.CMD_EXT_NOP_GUIDE_SPEED:
                return SERVO_CMD.SiTechHandpadGuideSpeed;
            case servo.CMD_EXT_NOP_CONTROLLER:
                return SERVO_CMD.nopController;
            default:
                return SERVO_CMD.unknown;
        }
    }
}

