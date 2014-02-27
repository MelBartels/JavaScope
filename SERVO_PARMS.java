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
 * configuration for PIC SERVO control chip
 */
public class SERVO_PARMS {
    static final double TICK_FREQ_HZ = 1953.;

    CONTROLLER_TYPE controllerType;
    boolean controllerActive;
    boolean ampEnableActiveHigh;
    int positionGainKp;
    int velGainKd;
    int positionGainKi;
    int integrationLimitIL;
    int outputLimitOL;
    int currentLimitCL;
    int positionErrorLimitEL;
    int rateDivisorSR;
    int ampDeadbandComp;

    // move vars for low level routines that command a servo motor
    // must be positive value
    long targetAccel;
    long targetPosition;
    // must be positive value
    long targetVel;
    // must be positive value
    double velRadSec;
    ROTATION targetVelDir;
    TRAJECTORY_STYLE trajectoryStyle;
    boolean moveNow;

    SERVO_MOVE_STATE moveState;
    SERVO_MOVE_CMD moveCmd;
    SERVO_MOVE_CMD lastMoveCmd;
    SERVO_MOVE_CMD holdMoveCmd;
    // device used to make a move: if motor has stopped then cmdDevice is set to none
    CMD_DEVICE cmdDevice;
    // ok to check for positional moveStateFinished: to allow positional move to get underway before allowing
    // a check for SERVO_MOVE_STATE.finished
    boolean moveStateFinishedCheckOK;
    boolean servoCmdProcessed;
    // mirror of actualPosition, but in degrees
    double actualPositionDeg;
    long lastActualPosition;
    long lastTargetVel;
    ROTATION lastTargetVelDir;
    // if consecutive status reads show that motor position has not changed, then set motorStopped to true
    boolean motorStopped;
    // average velocity based on lastActualPosition, actualPosition, lastStatusSidT, and statusSidT: can be negative value:
    // used by encoder threshold routine;
    double avgVelRadSec;

    // used in moveToTargetEquatSubr()
    // work var used to hold primary change in position
    double deltaPosRad;
    // difference in position to target from current in initial pass of moveToTargetEquatSubr()
    double deltaPos1Rad;
    // difference in position to target from current in final pass of moveToTargetEquatSubr()
    double deltaPos2Rad;

    // following velocities used in trackCalcDeltaPosVel(), which is used by velocity, proportional velocity, trajectory, and position TrackStyles
    // velocity needed to move starting at posRad and ending at pos1Rad over moveToTargetTimeSec
    double deltaPos_1VelRadSec;
    // velocity needed to move starting at pos1Rad and ending at Pos2Rad over moveToTargetTimeSec
    double deltaPos1_2VelRadSec;

    // used by position+velocity combination TrackStyles
    boolean trackError;

    // for PID control of servo motion
    double softKp;
    double softKd;
    double softKi;
    double softKiLimit;
    double iRunningSum;
    // Positional difference to calculate Kd
    double kdDeltaPosRad;
    // last Positional change
    double lastPIDDeltaPosRad;

    // if deccel has occured which is used by velocity and proportional velocity TrackStyles
    boolean deccelCommanded;
    // used in TrackStyle.checkDeccelToFinalPos()
    timeDistance td = new timeDistance();

    // used by proportional velocity TrackStyles
    // compensation factor to narrow Positional difference in moveToTargetEquatSubr()
    double propVel;

    // used by Positional and proportional velocity TrackStyles
    boolean displayPositionStart;

    // used by trajectory TrackStyle
    // to hold trajectory parms if moving by trajectory
    trajectory traj = new trajectory();
    // to hold and restore original configuration
    double cfgAccelDegSecSec;

    // PICServo simulator
    PICServoSimList PICServoSimList;

    // holds backlash vars for each motor
    backlash backlash = new backlash();

    trackAnalysisArray taa;

    // user configurated
    boolean track;
    double stepsPerRev;
    int encoderCountsPerRev;
    boolean reverseMotor;
    double accelDegSecSec;
    double fastSpeedDegSec;
    double slowSpeedArcsecSec;
    // used in velocity and both proportional velocity TrackStyles
    double dampenFactor;
    // used in velocity and both proportional velocity TrackStyles
    double posMoveThresholdArcmin;
    double homeDeg;
    double sectorDeg;
    double softHighLimitDeg;
    double softLowLimitDeg;
    // "current" is externally measured position, "actual" is reported position by PIC, "offset" is used to translate between the two
    double currentPositionDeg;
    // backlash of gearing to take up in arcminutes
    double backlashArcmin;
    // guiding speed
    double guideArcsecSec;
    // drift to drag a guide star so that an autoguider can update in one direction only
    double guideDragArcsecPerMin;
    // starting drift values
    double driftArcsecPerMin;
    // use PEC: turns on/off all PEC for this SERVO_ID; individual PEC(s) made active by PECActive in each PEC object
    boolean PECActive;
    // set to Off if not using automatic synchronization of PEC alignment, otherwise set to appropriate value depending if
    // high to low voltage transition, or, low to high voltage transition should trigger the synchronization
    AUTO_PEC_SYNC_DETECT autoPECSyncDetect;

    // variables used internally
    static final double SHORT_RANGE_ACCEL_FACTOR = 4.;
    long acceleration;
    // set to 1x acceleration: use for long distance moves to max velocity
    long longRangeAccel;
    // set to SHORT_RANGE_ACCEL_FACTOR times accleration: use for short distance moves
    long shortRangeAccel;
    byte ampOnValue;
    byte ampOffValue;
    // if 10 counts per rad, then countToRad = 0.1
    double countToRad;
    double fastSpeedRadSec;
    double slowSpeedRadSec;
    double homeRad;
    double sectorRad;
    boolean softLimitOn;
    // offset = current - actual
    double currentPositionOffsetRad;
    double holdOffsetRad;
    // backlash of gearing to take up in radians
    double backlashRad;
    // as above but in encoder counts
    long backlashCount;
    // if backlash fully taken up in CW direction, then actualBacklash will be 0,
    // if backlash fully taken up in CCW direction, then actualBacklash will be backlash
    double actualBacklashRad;
    // as above but in encoder counts
    long actualBacklashCount;
    // guiding speed: > 0 indicates guiding is active for this motor
    double guideRadSec;
    // accumulated guiding correction
    double accumGuideRad;
    // guiding correction to add per cycle
    double guideIncr;
    // position errors between simulated controller and actual controller's returned position
    double startPosErrRad;
    double posErrRad;
    // amount of drift for this motor
    double driftRadMin;
    // accumulated drift correction
    double accumDriftRad;
    boolean lastLimit2;
    boolean sectorTriggered;
    // log of accum guide vs time
    logAccumGuideResults lagr;
    // PEC/guide array: each SERVO_ID card can have multiple PEC
    java.util.List lg = new ArrayList();
    // PEC sync detected and ready to be acted upon
    boolean PECSyncReady;
    boolean holdTrack;

    // status vars
    double statusSidT;
    double lastStatusSidT;
    int readStatus;
    byte lastDefinedStatus;
    boolean handpadPortsRead;
    int bytesReturned;
    byte controlByte;
    byte sendChecksum;
    boolean receiveChecksumOK;
    int unexpectedReceiveBytes;
    boolean readStatusSuccessful;
    int consecutiveUnsuccessfulReadStatusCount;
    long readStatusCount;
    long readStatusSuccessfulCount;

    // variables returned by PIC SERVO
    byte receiveChecksum;
    byte statusByte;
    byte auxStatusByte;
    boolean moveDone;
    boolean cksumError;
    boolean overCurrent;
    boolean powerOn;
    boolean posError;
    boolean limit1;
    boolean limit2;
    boolean homeInProgress;
    boolean index;
    boolean posWrap;
    boolean servoOn;
    boolean accelDone;
    boolean slewDone;
    boolean timerOverrun;
    // range is +- x7FFFFFFF or max +-1657 counts per arcsecond
    long actualPosition;
    byte ADValue;
    int actualVel;
    long homePosition;
    byte deviceID;
    byte version;

    // additional vars for SiTech controller
    long scopeEncoder;
    long handpadSlewVel;
    long handpadPanVel;
    long handpadGuideVel;
    boolean ADValueIsMotorVoltOrCPUTemp;

    SERVO_PARMS() {
        astroTime.getInstance().calcSidT();
        PICServoSimList = new PICServoSimList(astroTime.getInstance().JD);
        /**
         * defaults for SiTech's controller:
         *ampEnableActiveHigh        false
         *positionGainKp             20000
         *velGainKd                  6000
         *positionGainKi             6000
         *integrationLimitIL         20000
         *outputLimitOL              255
         *currentLimitCL             200
         *positionErrorLimitEL       32000
         *rateDivisorSR              1
         *ampDeadbandComp            0
         */
        controllerType = CONTROLLER_TYPE.motor;
        controllerActive = false;
        ampEnableActiveHigh = false;
        positionGainKp = 200;
        velGainKd = 700;
        positionGainKi = 200;
        integrationLimitIL = 700;
        outputLimitOL = 255;
        currentLimitCL = 255;
        // limited to 0x3FFF
        positionErrorLimitEL = 16383;
        rateDivisorSR = 1;
        ampDeadbandComp = 0;
        track = false;
        stepsPerRev = 12960000;
        encoderCountsPerRev = 2048;
        reverseMotor = false;
        accelDegSecSec = 1.;
        fastSpeedDegSec = 2.5;
        slowSpeedArcsecSec = 300.;
        dampenFactor = .15;
        posMoveThresholdArcmin = 10.;
        homeDeg = 0.;
        sectorDeg = 0.;
        softHighLimitDeg = 0.;
        softLowLimitDeg = 0.;
        currentPositionDeg = 0.;
        backlashArcmin = 0.;
        guideArcsecSec = 5.;
        guideDragArcsecPerMin = 0.;
        driftArcsecPerMin = 0.;
        PECActive = false;
        autoPECSyncDetect = AUTO_PEC_SYNC_DETECT.Off;
    }

    /**
     * velocity in counts/tick = velocity in rad/sec * counts/rad * rateDivisor/(ticks/sec);
     * counts/tick = velocity rad/sec * counts/rad * rateDivisor*sec/tick;
     * counts    rad   counts   sec
     * ------ =  --- * ------ * ----
     * tick      sec   rad      tick
     * ie, if higher resolution encoder with more counts per revolution, then step size will be smaller,
     * causing motor to move at higher counts/tick, equalling the original rate of rotation;
     * countToRad = rads per count = rad/count
     *
     * speed in rad/sec: (double) targetVel / velRadSecToTargetVel();
     */
    double velRadSecToTargetVel() {
        double f = 65536. * (double) rateDivisorSR / (TICK_FREQ_HZ * countToRad);
        return f;
    }

    /**
     * accel(counts/tick/tick) = accel(rad/sec/sec) = count/rad * sec/tick * sec/tick;
     * accel(deg/sec/sec) = accel(rad/sec/sec) * deg/rad;
     * invert all since returning value to convert other way (deg/sec/sec to counts/tick/tick);
     *
     * accel(deg/sec/sec): (double) acceleration / accelDegSecSecToCountsTickTick());
     */
    double accelDegSecSecToCountsTickTick() {
        double a;

        a = 65536. * units.DEG_TO_RAD * (1./countToRad) * ((double) rateDivisorSR / TICK_FREQ_HZ) * ((double) rateDivisorSR / TICK_FREQ_HZ);
        return a;
    }

    double calcAccelDegSecSecFromAcceleration() {
        accelDegSecSec = (double) acceleration / accelDegSecSecToCountsTickTick();
        return accelDegSecSec;
    }

    double calcAccelDegSecSecFromAcceleration(long acceleration) {
        accelDegSecSec = (double) acceleration / accelDegSecSecToCountsTickTick();
        return accelDegSecSec;
    }
}

