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
 * handpadControl functions:
 *    void handpadProcessDirButtons()
 *    void moveVelSpeedDir(CMD_DEVICE device,  int id,  int speed,  ROTATION dir)
 *    void stopDeviceMove(CMD_DEVICE device,  int id)
 *    void testHandpadReturn()
 *    void testHandpadMotorControl()
 *    void testHandpad()
 */
public class handpadControl extends servo {
    /**
     * if no buttons pressed, nothing happens;
     * device can only stop a move that it has initiated; to stop a move started by another device, use
     * any device to start then immediately stop the move;
     * start of move denoted by setting cmdDevice to initiating device: stop move command completes only
     * when cmdDevice matches passed device type
     */
    void handpadProcessDirButtons() {
        SERVO_ID vertMotor;
        SERVO_ID horizMotor;

        if (cfg.getInstance().handpadMode == HANDPAD_MODE.handpadModeFRFocus && handpadFRFocusOn) {
            vertMotor = SERVO_ID.fieldR;
            horizMotor = SERVO_ID.focus;
        }
        else {
            vertMotor = SERVO_ID.altDec;
            horizMotor = SERVO_ID.azRa;
        }

        if ((buttons & handpadDesignBase.UP_KEY) == handpadDesignBase.UP_KEY)
            moveVelSpeedDir(CMD_DEVICE.handpad, vertMotor.KEY, slowSpeedSwitch, ROTATION.CW);
        else
            if ((buttons & handpadDesignBase.DOWN_KEY) == handpadDesignBase.DOWN_KEY)
                moveVelSpeedDir(CMD_DEVICE.handpad, vertMotor.KEY, slowSpeedSwitch, ROTATION.CCW);
            else
                stopDeviceMove(CMD_DEVICE.handpad, vertMotor.KEY);

        if ((buttons & handpadDesignBase.CW_KEY) == handpadDesignBase.CW_KEY)
            moveVelSpeedDir(CMD_DEVICE.handpad, horizMotor.KEY, slowSpeedSwitch, ROTATION.CW);
        else
            if ((buttons & handpadDesignBase.CCW_KEY) == handpadDesignBase.CCW_KEY)
                moveVelSpeedDir(CMD_DEVICE.handpad, horizMotor.KEY, slowSpeedSwitch, ROTATION.CCW);
            else
                stopDeviceMove(CMD_DEVICE.handpad, horizMotor.KEY);
    }

    /**
     * moveVelSpeedDir() and stopDeviceMove() ensure that while any device can start a move, only the device that starts a move can
     * stop the move;
     * to stop a move started by another device, start/stop move by same device, ie, to stop LX200 move, tap a handpad direction button
     */
    void moveVelSpeedDir(CMD_DEVICE device,  int id,  int speed,  ROTATION dir) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        // set backlash direction so that remaining backlash will be taken up by backlash.addBacklashToDeltaPosRads()
        sp.backlash.setBacklashDir(id, dir);

        sp.cmdDevice = device;
        sp.moveNow = true;
        if (speed == handpadDesignBase.SLOW_KEY)
            sp.moveCmd = SERVO_MOVE_CMD.startSlowVelMove;
        else {
            sp.moveCmd = SERVO_MOVE_CMD.startFastVelMove;
            // high speed move assumes that drift should start over
            sp.accumDriftRad = 0;
        }
        if (dir == ROTATION.CW)
            sp.targetVelDir = ROTATION.CW;
        else
            sp.targetVelDir = ROTATION.CCW;
        sp.servoCmdProcessed = false;
        processMoveCmdControl();
    }

    void stopDeviceMove(CMD_DEVICE device,  int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];

        if (sp.cmdDevice == device) {
            sp.moveCmd = SERVO_MOVE_CMD.stopMoveSmoothly;
            sp.servoCmdProcessed = false;
            processMoveCmdControl();
            // if sp.cmdDevice not set to CMD_DEVICE.none here, then must be periodically checked for and
            // set elsewhere: see checkMotorStopSetCmdDeviceNone() which does this which is called from sequencer()
        }
    }

    void testHandpadReturn() {
        System.out.println("test of handpad return values");
        do {
            getStatus(SERVO_ID.altDec.KEY);
            getStatus(SERVO_ID.azRa.KEY);
            displayHandpadStatus();
            System.out.println("press return to continue, any other key to quit");
            console.getString();
        }while (console.s.equalsIgnoreCase(""));
    }

    void testHandpadMotorControl() {
        System.out.println("test of handpad motor control");
        displayCmd = true;
        displayStatus = true;
        do {
            getStatus(SERVO_ID.altDec.KEY);
            getStatus(SERVO_ID.azRa.KEY);
            displayHandpadStatus();
            if (handpadSuccessfullyRead)
                handpadProcessDirButtons();
            displayMove(SERVO_ID.altDec.KEY);
            displayMove(SERVO_ID.azRa.KEY);
            System.out.println("press return to continue, any other key to quit");
            console.getString();
        }while (console.s.equalsIgnoreCase(""));
    }

    void testHandpad() {
        System.out.println("test of handpad");
        testHandpadReturn();
        testHandpadMotorControl();
    }
}

