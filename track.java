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
 * track class:
 *    adds coordinate conversion routines and error correction routines,
 *    develops and tests several TrackStyles for tracking,
 *    incorporates handpad motor control into the tracking including handpad mode operations,
 *    adds encoder interoperability,
 *    writes html pages;
 *
 * functions directly related to tracking:
 *    track()
 *    void close()
 *    void recordEquat()
 *    void recordAltaz()
 *    addInToInputFile()
 *    void writeInputDataHistFile()
 *    void turnTrackingOn()
 *    void turnTrackingOffStopAllMotors()
 *    void backlashActive(boolean backlashActive)
 *    void setCurrentAltazToServoPosition()
 *    void setServoPositionToCurrentAltaz()
 *    void checkForEquatMountCanMoveThruPole()
 *    boolean moveToTargetProcessSoftLimits()
 *    double shortestAltitudeDistance(double a)
 *    double shortestAzimuthDistance(double z)
 *    void setSlewing()
 *    boolean meridianNeedsFlipping()
 *    void moveToTargetEquat(int ts)
 *    void moveToTargetEquatSubr(int moveSequence, double timeIncrRad)
 *    boolean checkMotorStopSetCmdDeviceNone(int id)
 *    void checkMotorStopSetCmdDeviceNone()
 *    boolean checkMotorStateFinishSetCmdDeviceNone(int id)
 *    void checkMotorStateFinishSetCmdDeviceNone()
 *    boolean checkMotorMoveCompleteSetCmdDeviceNone(int id)
 *    void checkMotorMoveCompleteSetCmdDeviceNone()
 *    void setCurrentAltazGetEquatCopyTarget()
 *    void sequencer(int motor, boolean displayDiagnostics)
 *    void checkProcessHandpadCmd(int motor, boolean displayDiagnostics)
 * functions supporting handpad actions based on mode (called from within sequencer()):
 *    String buildGuidePECString(int id)
 *    void calcGuideFRAngle()
 *    void zeroDrift()
 *    void updateDriftCalculatedFromAccumGuide()
 *    void startDriftT()
 *    void setEndDriftT()
 *    void calcAccumGuide()
 *    void setLastAccumGuideAction()
 *    void writeToGuideArrays()
 *    void calcWriteSiTechAccumGuide()
 *    void buildGuideAnalysisFiles()
 *    void createPECfromGuideAnalysisFiles()
 *    void savePEC()
 *    void addAccumGuideDriftToCurrentPosition()
 *    void initAllGuide()
 *    void stopAllGuide()
 *    void initSaveGuideArray()
 *    void SaveGuideArray()
 *    void setAllGuideArrayStates(GUIDE_ARRAY_STATE GAF)
 *    boolean checkAllGuideArrayStates(GUIDE_ARRAY_STATE GAF)
 *    void saveAllGuide()
 *    boolean OkToStartGuideForWriteNSave()
 *    boolean checkStartGuideForWriteNSave()
 *    boolean checkReadyToSaveGuide()
 *    boolean checkSaveGuideArray()
 *    void processHandpadModeSwitch()
 *    void processPolarAlign()
 *    boolean writePolarAlignFile(String s)
 *    void resetToCurrentEquatCoord()
 *    void resetToCurrentAltazCoord()
 *    void getEquatSetTarget()
 *    void copyPosToInAndTargetThenTurnTrackingOn(position p)
 *    void executeSpiralSearch
 *    void stopSpiralSearch
 * functions supporting SiTech controller:
 *    void setSiTechGuideMode(boolean setting)
 *    void setSiTechAlt()
 *
 * functions supporting encoders:
 *    void buildEncoders()
 *    void startEncodersThread()
 *    void stopEncodersThread()
 *    boolean resetScopeToEncoders()
 *    boolean resetEncodersToScope()
 *    boolean checkEncoderThreshold()
 * functions building html pages:
 *    String buildHandpadModeString()
 *    void displayHandpadModeString()
 *    String buildDriftString()
 *    String addToAAECString(axisToAxisEC aaec)
 *    String buildAxisToAxisCorrectionString(int id)
 *    String buildPMCString(int id)
 *    void writeCurrEquatCoordHTMLFile()
 *    void writeStatusHTMLFile()
 *    void writeServoStatusHTMLFile()
 * test functions:
 *    void testTrackingSubr(int ts, boolean displaySequencerDiagnostics)
 *    void testTracking(boolean displaySequencerDiagnostics)
 *    void testWaitMoveFinishedBreak()
 */
public class track extends handpadControl {
    boolean shutdown;
    boolean pauseSequencer;
    
    static BufferedReader input;
    static PrintStream output;
    
    String recordEquatFilename;
    String recordAltazFilename;
    
    // used to calculate drift during guiding
    double startDriftSidT;
    double endDriftSidT;
    double driftSidtDiff;
    hmsm driftRaHr;
    dms driftDecHr;
    String driftString;
    
    // for calculating accumGuide
    double lastAccumGuideTimeRad;
    // current and last actions for calcAccumGuide()
    int currentAccumGuideAction;
    int lastAccumGuideAction;
    // for writing SiTech controller accumulated guiding corrections to log file
    azDouble lastAccumGuideRad = new azDouble();
    
    boolean SiTechGuideActive;
    boolean guideActive;
    GUIDE_ARRAY_STATE guideArrayState;
    double guideFRAngle;
    double guideFRAngleOffset;
    boolean calcAccumGuideCalled;
    
    boolean softLimitTripped1;
    boolean meridianFlipped1;
    boolean softLimitTripped2;
    boolean meridianFlipped2;
    
    boolean slewing;
    boolean lastSlewing;
    
    // about 1 seconds worth
    static final int STARTING_HANDPAD_TIMER_VALUE = 3;
    int handpadButtonTimer;
    int recordEquatTimer;
    int recordAltazTimer;
    int grandTourTimer;
    int scrollTimer;
    
    int holdHandpad;
    String handpadModeString;
    
    position in;
    position savedIn;
    position target;
    position currentToServoTempPos;
    position servoToCurrentTempPos;
    position autoInit1;
    position autoInit2;
    
    java.util.List laaec;
    String aaecString;
    
    position polarAlign1;
    position polarAlign2;
    position polarAlign3;
    POLAR_ALIGN_STAGE polarAlignStage;
    
    PMC PMC;
    String PMCString;
    
    TrackStyle TrackStyle;
    limitMotionCollection limitMotionCollection;
    convertMatrix c;
    refract refract;
    listPosition lpAnalysis;
    HandpadModes HandpadModes;
    dataFile grandTour;
    inputFile inputFile;
    cmdCol cmdCol;
    
    encoderFactory ef;
    java.util.Timer updateEncodersTimer;
    
    track() {
        axisToAxisEC aaec;
        
        c = new convertMatrix("convert");
        
        recordEquatFilename = eString.PGM_NAME + eString.EQUAT_EXT;
        recordAltazFilename = eString.PGM_NAME + eString.ALTAZ_EXT;
        
        driftRaHr = new hmsm();
        driftDecHr = new dms();
        driftRaHr.rad = cfg.getInstance().driftRaDegPerHr * units.DEG_TO_RAD;
        driftDecHr.rad = cfg.getInstance().driftDecDegPerHr * units.DEG_TO_RAD;
        
        guideArrayState = GUIDE_ARRAY_STATE.off;
        
        refract = new refract();
        
        in = new position("in");
        savedIn = new position("savedIn");
        target = new position("target");
        currentToServoTempPos = new position("currentToServoTempPos");
        servoToCurrentTempPos = new position("servoToCurrentTempPos");
        autoInit1 = new position("autoInit1");
        autoInit2 = new position("autoInit2");
        
        lpAnalysis = new listPosition();
        
        laaec = new ArrayList();
        // adding new linkAxisToAxisEC attempts to load the appropriate error correcting file from disk
        laaec.add(new axisToAxisEC(AXIS_TO_AXIS_EC.altAlt));
        aaec = (axisToAxisEC) laaec.get(laaec.size()-1);
        aaec.active = cfg.getInstance().useAltAltEC;
        laaec.add(new axisToAxisEC(AXIS_TO_AXIS_EC.altAz));
        aaec = (axisToAxisEC) laaec.get(laaec.size()-1);
        aaec.active = cfg.getInstance().useAltAzEC;
        laaec.add(new axisToAxisEC(AXIS_TO_AXIS_EC.azAz));
        aaec = (axisToAxisEC) laaec.get(laaec.size()-1);
        aaec.active = cfg.getInstance().useAzAzEC;
        
        // instantiating object attempts to load the pmc file from disk
        PMC = new PMC();
        PMC.active = cfg.getInstance().usePMC;
        
        limitMotionCollection = new limitMotionCollection();
        limitMotionCollection.loadFromFiles();
        
        polarAlign1 = new position("polarAlign1");
        polarAlign2 = new position("polarAlign2");
        polarAlign3 = new position("polarAlign3");
        polarAlignStage = POLAR_ALIGN_STAGE.load3Stars;
        
        grandTour = new dataFile();
        //for testing purposes
        //grandTour.loadFromFile("messier.dat");
        
        inputFile = new inputFile();
        
        backlashActive(cfg.getInstance().backlashActive);
        
        startDriftT();
        setCurrentAltazToServoPosition();
    }
    
    void buildEncoders() {
        ef = new encoderFactory();
        
        ef.build(cfg.getInstance().encoderType,
                cfg.getInstance().encoderIOType,
                cfg.getInstance().encoderSerialPortName,
                cfg.getInstance().encoderBaudRate,
                cfg.getInstance().encoderHomeIPPort,
                cfg.getInstance().encoderRemoteIPName,
                cfg.getInstance().encoderRemoteIPPort,
                cfg.getInstance().encoderFileLocation,
                cfg.getInstance().encoderTrace);
        
        if (ef.E != null && ef.E.portOpened() && ef.E.getQueryAndReadSuccess()) {
            ef.E.processPositions();
            ef.E.displayCounts();
            ef.E.displayPositions();
            stopEncodersThread();
            /*
             * encoder interface controller boxes need time to be queried, calculate positions, and return results;
             * hence, put the encoder query and read into its own thread, except for SiTech type encoder controller
             * where results are available the instance the motor controller is read;
             */
            if (ef.E.encoderType() != ENCODER_TYPE.encoderSiTech)
                startEncodersThread();
        }
    }
    
    void startEncodersThread() {
        console.stdOutLn("starting encoder thread");
        updateEncodersTimer = new java.util.Timer();
        updateEncodersTimer.schedule(new updateEncodersTask(this), 0, encoderBase.UPDATE_ENCODERS_TIME_SEC * 1000);
    }
    
    void stopEncodersThread() {
        if (updateEncodersTimer != null) {
            console.stdOutLn("stopping encoder thread");
            updateEncodersTimer.cancel();
        }
    }
    
    void close() {
        int id;
        axisToAxisEC aaec;
        Iterator it;
        
        cfg.getInstance().driftRaDegPerHr = driftRaHr.rad * units.RAD_TO_DEG;
        cfg.getInstance().driftDecDegPerHr = driftDecHr.rad * units.RAD_TO_DEG;
        
        it = laaec.iterator();
        while (it.hasNext()) {
            aaec = (axisToAxisEC) it.next();
            if (aaec.axisToAxisName == AXIS_TO_AXIS_EC.altAlt)
                cfg.getInstance().useAltAltEC = aaec.active;
            if (aaec.axisToAxisName == AXIS_TO_AXIS_EC.altAz)
                cfg.getInstance().useAltAzEC = aaec.active;
            if (aaec.axisToAxisName == AXIS_TO_AXIS_EC.azAz)
                cfg.getInstance().useAzAzEC = aaec.active;
        }
        
        cfg.getInstance().usePMC = PMC.active;
        
        c.close();
        cmdCol.close();
        
        if (ef.E != null) {
            ef.E.saveEncodersResetLog();
            ef.E.close();
            stopEncodersThread();
            updateEncodersTimer = null;
        }
        
        super.close();
    }
    
    /**
     * record equatorial position when commanded, ie, by handpad mode switch activated
     */
    void recordEquat() {
        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(recordEquatFilename, true)));
            output.println(cfg.getInstance().current.ra.getStringHMS(eString.SPACE)
            + "   "
                    + cfg.getInstance().current.dec.getStringDMS(eString.SPACE)
                    + "   handpad_record");
            output.close();
        } catch (IOException ioe) {
            console.errOut(ioe.toString());
            ioe.printStackTrace();
            System.err.println("could not open " + recordEquatFilename);
        }
    }
    
    /**
     * record altazimuth position when commanded, ie, by handpad mode switch activated
     */
    void recordAltaz() {
        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(recordAltazFilename, true)));
            output.println(cfg.getInstance().current.alt.getStringDM()
            + "   "
                    + cfg.getInstance().current.az.getStringDM()
                    + "   handpad_record");
            output.close();
        } catch (IOException ioe) {
            console.errOut(ioe.toString());
            ioe.printStackTrace();
            System.err.println("could not open " + recordAltazFilename);
        }
    }
    
    void addInToInputFile(String reason) {
        if (inputFile != null)
            inputFile.addPosToLLPosWriteFile(in, reason);
    }
    
    void writeInputDataHistFile() {
        if (inputFile != null)
            inputFile.writeInputDataHistFile();
    }
    
    /**
     * sets all .track to true which will activate tracking to target equatorial position
     */
    void turnTrackingOn() {
        int id;
        
        for (id = 0; id < SERVO_ID.size(); id++)
            cfg.getInstance().servoParm[id].track = true;
    }
    
    /**
     * sets all .track to off and stops all motors smoothly
     */
    void turnTrackingOffStopAllMotors() {
        int id;
        
        for (id = 0; id < SERVO_ID.size(); id++)
            cfg.getInstance().servoParm[id].track = false;
        stopAllMotorsSmoothly();
        in.copy(cfg.getInstance().current);
    }
    
    void backlashActive(boolean backlashActive) {
        int id;
        
        cfg.getInstance().backlashActive = backlashActive;
        for (id = 0; id < SERVO_ID.size(); id++)
            cfg.getInstance().servoParm[id].backlash.active = backlashActive;
    }
    
    /**
     * adds guideDrag to drift: use when guiding is turned on
     */
    void addGuideDragToDrift() {
        int id;
        
        for (id = 0; id < SERVO_ID.size(); id++)
            cfg.getInstance().servoParm[id].driftRadMin += cfg.getInstance().servoParm[id].guideDragArcsecPerMin * units.ARCSEC_TO_RAD;
        
        driftRaHr.rad += cfg.getInstance().guideDragRaArcsecPerMin * units.ARCSEC_TO_RAD / 60.;
        driftDecHr.rad += cfg.getInstance().guideDragDecArcsecPerMin * units.ARCSEC_TO_RAD / 60.;
    }
    
    /**
     * removes guideDrag to drift: use when guiding is turned off
     */
    void removeGuideDragFromDrift() {
        int id;
        
        for (id = 0; id < SERVO_ID.size(); id++)
            cfg.getInstance().servoParm[id].driftRadMin -= cfg.getInstance().servoParm[id].guideDragArcsecPerMin * units.ARCSEC_TO_RAD;
        
        driftRaHr.rad -= cfg.getInstance().guideDragRaArcsecPerMin * units.ARCSEC_TO_RAD / 60.;
        driftDecHr.rad -= cfg.getInstance().guideDragDecArcsecPerMin * units.ARCSEC_TO_RAD / 60.;
    }
    
    /**
     * cfg.getInstance().servoParm[].actualPosition is the telescope position from the motor's perspective while cfg.getInstance().current. is
     * the telescope's actual pointing position as projected into the sky; current = actual + offset
     * errors to compensate for:
     *    backlash (depends on .actualPosition)
     *    PEC (depends on .actualPosition)
     *    altaz drift (depends on .accumDriftRad)
     *    (since drift values were calculated for altaz drift, take opportunity to update equat drift (depends on driftRaHr and driftDecHr))
     *    guide (depends on .accumGuideRad)
     *    refraction (depends on cfg.getInstance().current altaz coords and site coordinate translation values)
     *    axis vs axis corrections (depends on cfg.getInstance().current altaz coords)
     *    PMC (depends on cfg.getInstance().current altaz coords);
     * turn on refraction correction, then get the axis vs axis corrections using the corrected-for-refraction altaz coordinates,
     * then get the PMC values, using the corrected-for-refraction-and-axis-vs-axis altaz coordinates; if axis vs axis corrections
     * redone after PMC, then PMC first needs to be turned off, then axis vs axis corrections done, then PMC redone (axis vs axis
     * corrections similarly need to be done in correct order per sequence in track.track() altalt, altaz, azaz
     */
    void setCurrentAltazToServoPosition() {
        axisToAxisEC aaec;
        Iterator it;
        
        // set to cfg.getInstance().servoParm[].actualPosition
        cfg.getInstance().current.alt.rad = (double) cfg.getInstance().spa.actualPosition * cfg.getInstance().spa.countToRad + cfg.getInstance().spa.currentPositionOffsetRad;
        cfg.getInstance().current.az.rad = (double) cfg.getInstance().spz.actualPosition * cfg.getInstance().spz.countToRad + cfg.getInstance().spz.currentPositionOffsetRad;
        
        /**
         * actualBacklashRad is the amount of backlash that has been taken up in the CCW direction;
         *  if > 0, motors have been spun extra CCW to takeup backlash up with no change in scope's apparent pointing direction
         * resulting in cfg.getInstance().servoParm[].actualPosition being too low, so must add this value to cfg.getInstance().servoParm[].actualPosition
         * to arrive at true pointing position;
         * ie, if actualbacklash 0 and motor at 20, then scope at 20;
         *     if motor moves CCW 10 and if backlash 5, then motor moves from 20 to 10, but scope only moves from 20 to 15,
         *        and actualbacklash goes to 5, since 5 of the move was taken up by the backlash
         */
        cfg.getInstance().current.alt.rad += cfg.getInstance().spa.actualBacklashRad;
        cfg.getInstance().current.az.rad += cfg.getInstance().spz.actualBacklashRad;
        
        /**
         * positive PEC, drift, and guide, indicate excess CW movement, that is, the scope with respect to the stars has
         * drifted CW (eg, hit a bump on the shaft), therefore the current. altaz position that the servo position would yield
         * is too small and must be increased to match the altaz coordinates as derived from the stars;
         * in the same manner, positive accumGuide means that the CCW button had to be pressed to compensate for excess CW
         * movement (CCW button reduces servo position and would result in lower current. unless accumGuide added in to
         * compensate); AccumDrift is based on a guiding session where a drift rate of accumGuide over time is calculated
         *
         * each SERVO_ID has a PECActive that turns on/off all PEC for that controller, and,
         * each PEC has a PECActive that turns on/off that particular PEC;
         * class cfg contains static array of SERVO_PARMS called servoParm,
         * each class SERVO_PARMS contains a list of Guides, linkGuide,
         * linkGuide contains a guide array called g,
         * class guide extends PEC, so each g contains a PEC (that is, each PEC gets its own guide object)
         * all this so that each SERVO_ID card can have multiple PEC(s), each PEC with its own Guiding numbers, ie,
         * cfg.getInstance().spa.linkGuide.g.PECToAddArcsec
         */
        if (cfg.getInstance().spa.PECActive) {
            it = cfg.getInstance().spa.lg.iterator();
            while (it.hasNext()) {
                guide g = (guide) it.next();
                if (g.PECActive) {
                    g.setPECIxPECToAddFromServoActualPosition(cfg.getInstance().spa.actualPosition);
                    cfg.getInstance().current.alt.rad += g.PECToAddArcsec * units.ARCSEC_TO_RAD;
                }
            }
        }
        if (cfg.getInstance().spz.PECActive) {
            it = cfg.getInstance().spz.lg.iterator();
            while (it.hasNext()) {
                guide g = (guide) it.next();
                if (g.PECActive) {
                    g.setPECIxPECToAddFromServoActualPosition(cfg.getInstance().spz.actualPosition);
                    cfg.getInstance().current.az.rad += g.PECToAddArcsec * units.ARCSEC_TO_RAD;
                }
            }
        }
        
        /**
         * assume that drift time is under 12 hours to allow going back in time to calculate drift;
         * drift rate is radians per minute, and the time difference since drift first initiated is in radians,
         * so accumulated drift to add = rate * time, or, driftRadMin * driftSidtDiff*MIN_TO_RAD;
         * (interim or running total) AccumDrift calculated so that it can be removed while in setServoPositionToCurrentAltaz()
         */
        driftSidtDiff = astroTime.getInstance().calcSidT() - startDriftSidT;
        driftSidtDiff = eMath.validRadPi(driftSidtDiff);
        
        cfg.getInstance().spa.accumDriftRad = cfg.getInstance().spa.driftRadMin * driftSidtDiff * units.RAD_TO_MIN;
        cfg.getInstance().spz.accumDriftRad = cfg.getInstance().spz.driftRadMin * driftSidtDiff * units.RAD_TO_MIN;
        cfg.getInstance().current.alt.rad += cfg.getInstance().spa.accumDriftRad;
        cfg.getInstance().current.az.rad += cfg.getInstance().spz.accumDriftRad;
        
        /**
         * take opportunity to update equatorial drift - this is not an error correction to back out
         */
        if (cfg.getInstance().initialized()) {
            cfg.getInstance().current.ra.rad += driftRaHr.rad * driftSidtDiff * units.HR_TO_RAD;
            cfg.getInstance().current.dec.rad += driftDecHr.rad * driftSidtDiff * units.HR_TO_RAD;
        }
        
        /**
         * each motor stores its own guide parameters, using GuideArcsec > 0 if guiding is active;
         * (guiding may or may not be actively writing to a file containing PEC data, if so, guideActive in the
         * appropriate guide object is set to true and accumGuide in that object is used after being set to
         * accumGuide for the motor);
         * don't account for accumGuide if SiTech controller is handling its own guiding
         */
        if (!SiTechGuideActive) {
            if (cfg.getInstance().spa.guideArcsecSec > 0.)
                cfg.getInstance().current.alt.rad += cfg.getInstance().spa.accumGuideRad;
            if (cfg.getInstance().spz.guideArcsecSec > 0.)
                cfg.getInstance().current.az.rad += cfg.getInstance().spz.accumGuideRad;
        }
        
        /**
         * compensate for refraction after arriving at pointing position as represented by sky coordinates;
         * refraction causes an object near the horizon to appear higher than it really is;
         * therefore cfg.getInstance().current altaz coord, must show a higher value, ie,
         * if scope/encoder/servos coordinate is 0 deg, then cfg.getInstance().current altaz/true pointing/ sky position should read -34.5 arcmin
         */
        if (cfg.getInstance().refractAlign == ALIGNMENT.altaz) {
            refract.calcRefractScopeToSky(cfg.getInstance().current.alt.rad);
            cfg.getInstance().current.alt.rad -= refract.refract;
        }
        if (cfg.getInstance().refractAlign == ALIGNMENT.equat && cfg.getInstance().initialized()) {
            currentToServoTempPos.copy(cfg.getInstance().current);
            // current altaz -> current equat -> site equat -> site altaz -> refraction values
            c.getEquat();
            c.site.p.ra.rad = cfg.getInstance().current.ra.rad;
            c.site.p.dec.rad = cfg.getInstance().current.dec.rad;
            c.site.p.sidT.rad = cfg.getInstance().current.sidT.rad;
            c.site.getAltaz();
            c.calcSiteRefractScopeToSky();
            cfg.getInstance().current.copy(currentToServoTempPos);
            cfg.getInstance().current.az.rad += c.RefractRa;
            cfg.getInstance().current.alt.rad -= c.RefractDec;
        }
        
        /**
         * for following error correcting objects, positive corrective values mean scope ended up too far CW,
         * therefore servo position is too large and cfg.getInstance().current. must be reduced;
         * this is different than PEC, drift, and guide where positive values mean that the scope drifted too far CW,
         * CCW button was pressed to compensate, and thus the servo position is too small and therefore must be increased
         * in order to arrive at the true pointing position
         */
        //System.out.println("setCurrentAltazToServoPosition() before axis-axis EC: "
        //+ eString.doubleToStringNoGrouping(cfg.getInstance().current.alt.rad*units.RAD_TO_DEG, 6, 3)
        //+ " "
        //+ eString.doubleToStringNoGrouping(cfg.getInstance().current.az.rad*units.RAD_TO_DEG, 6, 3));
        it = laaec.iterator();
        while (it.hasNext()) {
            aaec = (axisToAxisEC) it.next();
            if (aaec.axisToAxisName == AXIS_TO_AXIS_EC.altAlt || aaec.axisToAxisName == AXIS_TO_AXIS_EC.altAz)
                if (aaec.active) {
                aaec.setECValue(cfg.getInstance().current.alt.rad);
                cfg.getInstance().current.alt.rad -= aaec.ecRad;
                }
            if (aaec.axisToAxisName == AXIS_TO_AXIS_EC.azAz)
                if (aaec.active) {
                aaec.setECValue(cfg.getInstance().current.az.rad);
                cfg.getInstance().current.az.rad -= aaec.ecRad;
                }
        }
        //System.out.println("setCurrentAltazToServoPosition() after axis-axis EC: "
        //+ eString.doubleToStringNoGrouping(cfg.getInstance().current.alt.rad*units.RAD_TO_DEG, 6, 3)
        //+ " "
        //+ eString.doubleToStringNoGrouping(cfg.getInstance().current.az.rad*units.RAD_TO_DEG, 6, 3));
        
        if (PMC.active) {
            PMC.calcCorrection(cfg.getInstance().current.alt.rad, cfg.getInstance().current.az.rad);
            cfg.getInstance().current.alt.rad -= PMC.correction.a;
            cfg.getInstance().current.az.rad -= PMC.correction.z;
        }
        
        cfg.getInstance().current.az.rad = eMath.validRad(cfg.getInstance().current.az.rad);
    }
    
    /**
     * remove error correction in reverse order of setCurrentAltazToServoPosition() error corrections
     */
    void setServoPositionToCurrentAltaz() {
        azDouble holdAZ = new azDouble();
        Iterator it;
        axisToAxisEC aaec;
        
        // use holdAZ to calculate alt and az for cfg.getInstance().servoParm[].currentPositionOffsetRad: this leaves cfg.getInstance().current. values unchanged
        holdAZ.a = cfg.getInstance().current.alt.rad;
        holdAZ.z = cfg.getInstance().current.az.rad;
        
        // PMC
        if (PMC.active) {
            PMC.calcCorrection(holdAZ.a, holdAZ.z);
            holdAZ.a += PMC.correction.a;
            holdAZ.z += PMC.correction.z;
        }
        
        // axis vs axis corrections - in reverse order
        
        //System.out.println("setServoPositionToCurrentAltaz() before axis-axis EC: "
        //+ eString.doubleToStringNoGrouping(holdAZ.a*units.RAD_TO_DEG, 6, 3)
        //+ " "
        //+ eString.doubleToStringNoGrouping(holdAZ.z*units.RAD_TO_DEG, 6, 3));
        it = laaec.iterator();
        while (it.hasNext()) {
            aaec = (axisToAxisEC) it.next();
            if (aaec.axisToAxisName == AXIS_TO_AXIS_EC.altAlt || aaec.axisToAxisName == AXIS_TO_AXIS_EC.altAz)
                if (aaec.active) {
                aaec.setECValue(cfg.getInstance().current.alt.rad);
                holdAZ.a += aaec.ecRad;
                }
            if (aaec.axisToAxisName == AXIS_TO_AXIS_EC.azAz)
                if (aaec.active) {
                aaec.setECValue(cfg.getInstance().current.az.rad);
                holdAZ.z += aaec.ecRad;
                }
        }
        //System.out.println("setServoPositionToCurrentAltaz() after axis-axis EC: "
        //+ eString.doubleToStringNoGrouping(holdAZ.a*units.RAD_TO_DEG, 6, 3)
        //+ " "
        //+ eString.doubleToStringNoGrouping(holdAZ.z*units.RAD_TO_DEG, 6, 3));
        
        // refraction corrections
        if (cfg.getInstance().refractAlign == ALIGNMENT.altaz) {
            refract.calcRefractSkyToScope(cfg.getInstance().current.alt.rad);
            holdAZ.a += refract.refract;
        }
        if (cfg.getInstance().refractAlign == ALIGNMENT.equat && cfg.getInstance().initialized()) {
            servoToCurrentTempPos.copy(cfg.getInstance().current);
            cfg.getInstance().current.alt.rad = holdAZ.a;
            cfg.getInstance().current.az.rad = holdAZ.z;
            // current altaz -> current equat -> site equat -> site altaz -> refraction values
            c.getEquat();
            c.site.p.ra.rad = cfg.getInstance().current.ra.rad;
            c.site.p.dec.rad = cfg.getInstance().current.dec.rad;
            c.site.p.sidT.rad = cfg.getInstance().current.sidT.rad;
            c.site.getAltaz();
            c.calcSiteRefractSkyToScope();
            cfg.getInstance().current.copy(servoToCurrentTempPos);
            holdAZ.z -= c.RefractRa;
            holdAZ.a += c.RefractDec;
        }
        
        // guide
        if (!SiTechGuideActive) {
            if (cfg.getInstance().spa.guideArcsecSec > 0.)
                holdAZ.a -= cfg.getInstance().spa.accumGuideRad;
            if (cfg.getInstance().spz.guideArcsecSec > 0.)
                holdAZ.z -= cfg.getInstance().spz.accumGuideRad;
        }
        
        // drift
        driftSidtDiff = astroTime.getInstance().calcSidT() - startDriftSidT;
        driftSidtDiff = eMath.validRadPi(driftSidtDiff);
        
        cfg.getInstance().spa.accumDriftRad = cfg.getInstance().spa.driftRadMin * driftSidtDiff * units.RAD_TO_MIN;
        cfg.getInstance().spz.accumDriftRad = cfg.getInstance().spz.driftRadMin * driftSidtDiff * units.RAD_TO_MIN;
        holdAZ.a -= cfg.getInstance().spa.accumDriftRad;
        holdAZ.z -= cfg.getInstance().spz.accumDriftRad;
        
        // take opportunity to update equatorial drift - this is not an error correction to back out
        if (cfg.getInstance().initialized()) {
            cfg.getInstance().current.ra.rad += driftRaHr.rad * driftSidtDiff * units.HR_TO_RAD;
            cfg.getInstance().current.dec.rad += driftDecHr.rad * driftSidtDiff * units.HR_TO_RAD;
        }
        
        // PEC
        if (cfg.getInstance().spa.PECActive) {
            it = cfg.getInstance().spa.lg.iterator();
            while (it.hasNext()) {
                guide g = (guide) it.next();
                if (g.PECActive) {
                    g.setPECIxPECToAddFromServoActualPosition(cfg.getInstance().spa.actualPosition);
                    holdAZ.a -= g.PECToAddArcsec * units.ARCSEC_TO_RAD;
                }
            }
        }
        if (cfg.getInstance().spz.PECActive) {
            it = cfg.getInstance().spz.lg.iterator();
            while (it.hasNext()) {
                guide g = (guide) it.next();
                if (g.PECActive) {
                    g.setPECIxPECToAddFromServoActualPosition(cfg.getInstance().spz.actualPosition);
                    holdAZ.z -= g.PECToAddArcsec * units.ARCSEC_TO_RAD;
                }
            }
        }
        
        // backlash
        holdAZ.a -= cfg.getInstance().spa.actualBacklashRad;
        holdAZ.z -= cfg.getInstance().spz.actualBacklashRad;
        
        // now that all corrections are backed out or reversed, can set new offset: offset = current - actual
        // servo.setCurrentPositionDeg() also called from servo.readStatus()
        
        cfg.getInstance().spa.currentPositionOffsetRad = holdAZ.a - (double) cfg.getInstance().spa.actualPosition * cfg.getInstance().spa.countToRad;
        cfg.getInstance().spz.currentPositionOffsetRad = holdAZ.z - (double) cfg.getInstance().spz.actualPosition * cfg.getInstance().spz.countToRad;
        setCurrentPositionDeg(SERVO_ID.altDec.KEY);
        setCurrentPositionDeg(SERVO_ID.azRa.KEY);
    }
    
    /**
     * for canMoveThruPole mounts: normally the az would be flipped 180 deg when moving
     * to other side of zenith, however, certain mounts cannot make this flip because the polar axis would
     * rotate to unacceptable position, hence the need for this check;
     * equatorial alignment: meridian + celestial equator is 0 az, then acceptable az range assumed to be 270->0->90:
     * therefore move through pole should occur for az between 90 and 270 (true for both northern and southern hemispheres);
     * altazimuth alignment: meridian + celestial equator is 180 az for northern hemisphere, 0 az for southern hemisphere;
     *   for nothern hemisphere acceptable range is 90->180->270, move through pole should occur for > 270 or < 90;
     *   for southern hemisphere, same as equatorial alignment;
     */
    void checkForMoveThruPole() {
        if (cfg.getInstance().Mount.canMoveThruPole()) {
            // altazimuth types
            if (cfg.getInstance().Mount.mountType() == MOUNT_TYPE.mountTypeNone
                    || cfg.getInstance().Mount.mountType() == MOUNT_TYPE.mountTypeCustom
                    || cfg.getInstance().Mount.mountType() == MOUNT_TYPE.mountTypeAltazimuth
                    || cfg.getInstance().Mount.mountType() == MOUNT_TYPE.mountTypeAltAlt
                    || cfg.getInstance().Mount.mountType() == MOUNT_TYPE.mountTypeUranostat) {
                if (cfg.getInstance().latitudeDeg >= 0.) {
                    if (cfg.getInstance().current.az.rad > units.THREE_FOURTHS_REV || cfg.getInstance().current.az.rad < units.QTR_REV)
                        c.translateAltazAcrossPole();
                } else
                    if (cfg.getInstance().current.az.rad > units.QTR_REV && cfg.getInstance().current.az.rad < units.THREE_FOURTHS_REV)
                        c.translateAltazAcrossPole();
            }
            // else equatorial types
            else {
                if (cfg.getInstance().current.az.rad > units.QTR_REV && cfg.getInstance().current.az.rad < units.THREE_FOURTHS_REV)
                    c.translateAltazAcrossPole();
            }
        }
    }
    
    /**
     * check against limits as defined in the limitMotionCollection class
     */
    boolean moveToTargetProcessSoftLimits() {
        if (cfg.getInstance().spa.softLimitOn && cfg.getInstance().spz.softLimitOn)
            return !limitMotionCollection.limitExceeded();
        return true;
    }
    
    double shortestAltitudeDistance(double a) {
        /**
         * ie, a move of 270 should result in a move of -90;
         * altitude is wrapped at 180 and -180
         */
        if (a > units.HALF_REV)
            a = units.ONE_REV - a;
        if (a < -units.HALF_REV)
            a = -units.ONE_REV - a;
        return a;
    }
    
    double shortestAzimuthDistance(double z) {
        /**
         * select shortest distance for azimuth move:
         * 4 possibilities of calculating az steps to move:
         *    start    end      delta    + 360 if nec.  > 180?   < -180?   result
         * 1. 350,     200,     -150,    210,           true,    no,       -150
         * 2. 200,     350,     150,                    no,      no,       150
         * 3. 350,     100,     -250,    110,           no,      true,     110
         * 4. 100,     350,     250,                    true,    no,       -110
         */
        return eMath.validRadPi(z);
    }
    
    /**
     * if both motors are within an arcminute of target, consider slew finished
     */
    void setSlewing() {
        if (Math.abs(cfg.getInstance().spa.deltaPosRad) <= units.ARCMIN_TO_RAD && Math.abs(cfg.getInstance().spz.deltaPosRad) <= units.ARCMIN_TO_RAD)
            slewing = false;
        else
            slewing = true;
        
        // sound a beep if slewing has finished
        if (lastSlewing && !slewing)
            common.beep(1, 0);
        
        lastSlewing = slewing;
    }
    
    /**
     * meridianNeedsFlipping() determined solely by azimuth;
     *
     * equatorially aligned, in northern hemisphere az increases as scope moves west of meridian while in southern
     * hemisphere az decreases as scope moves west of meridian;
     * when on meridian, if no meridian flip (scope is east facing west), az == 0 else if meridian flip, az = 180;
     * in nothern hemisphere, if not meridian flip, then az ranges from 0 to 90, and if meridian flip, then from 180 to 90
     *    (scope is flipped 180 degrees, plus scope is aimed at opposite horizon);
     * in southern hemisphere, if not meridian flip, then az ranges from 360 to 270, and if meridian flip, then from 180 to 270;
     * ie, assume flip fuzz of 1 deg:
     * if northern hemisphere,
     *    not flipped, aimed typically at 45, ok if coord between 359 and 180, otherwise flip if coord between 180 and 359,
     *    now flip scope by rotating az by 180 and aim scope to east, typically at 135 (meridian is now 180),
     *    ok if coord between 0 and 181, otherwise unflip if coord between 181 and 360;
     * if southern hemisphere,
     *    not flipped, aimed typically at 315, ok if coord between 180 and 1, otherwise flip if coord between 1 and 180,
     *    now flip scope by rotating az by 180 and aim scope to east, typically at 225 (meridian is 180),
     *    ok if coord between 179 and 0, otherwise unflip if coord between 0 and 179;
     *
     * also see getEquat() notes
     */
    boolean meridianNeedsFlipping() {
        ROTATION dir = cfg.getInstance().spz.targetVelDir;
        
        if (cfg.getInstance().Mount.meridianFlipPossible()) {
            if (cfg.getInstance().Mount.meridianFlip().required
                    && cfg.getInstance().Mount.meridianFlip().auto)
                if (cfg.getInstance().latitudeDeg >= 0.)
                    if (cfg.getInstance().Mount.meridianFlip().flipped) {
                if (cfg.getInstance().current.az.rad > units.HALF_REV+cfg.getInstance().Mount.meridianFlip().autoFuzzRad
                        && cfg.getInstance().current.az.rad < units.ONE_REV)
                    return true;
                    } else {
                if (cfg.getInstance().current.az.rad > units.HALF_REV
                        && cfg.getInstance().current.az.rad < units.ONE_REV-cfg.getInstance().Mount.meridianFlip().autoFuzzRad)
                    return true;
                    } else   // southern hemisphere
                        if (cfg.getInstance().Mount.meridianFlip().flipped) {
                if (cfg.getInstance().current.az.rad > 0.
                        && cfg.getInstance().current.az.rad < units.HALF_REV-cfg.getInstance().Mount.meridianFlip().autoFuzzRad)
                    return true;
                        } else {
                if (cfg.getInstance().current.az.rad > cfg.getInstance().Mount.meridianFlip().autoFuzzRad
                        && cfg.getInstance().current.az.rad < units.HALF_REV)
                    return true;
                        }
        }
        return false;
    }
    
    /**
     * checks each motor for tracking suitability and will allow it to track regardless of status of other motors
     */
    void moveToTargetEquat() {
        int id;
        timeDistance td = new timeDistance();
        
        // update current sidereal time and coordinates
        cfg.getInstance().current.sidT.rad = astroTime.getInstance().calcSidT();
        // current altaz coordinates contain error correction values
        setCurrentAltazToServoPosition();
        
        // initial calculation of distance to move
        moveToTargetEquatSubr(SEQUENCE.beg, 0.);
        setSlewing();
        if (softLimitTripped1)
            turnTrackingOffStopAllMotors();
        else {
            // final calculation of distance to move
            moveToTargetEquatSubr(SEQUENCE.end, cfg.getInstance().moveToTargetTimeSec*units.SEC_TO_RAD);
            for (id = 0; id < SERVO_ID.size(); id++)
                if (cfg.getInstance().servoParm[id].track && cfg.getInstance().servoParm[id].readStatusSuccessful) {
                if (cfg.getInstance().servoParm[id].cmdDevice == CMD_DEVICE.trackByVel
                        || cfg.getInstance().servoParm[id].cmdDevice == CMD_DEVICE.trackByPos
                        || cfg.getInstance().servoParm[id].cmdDevice == CMD_DEVICE.none) {
                    cfg.getInstance().servoParm[id].backlash.addBacklashToDeltaPosRads(id);
                    TrackStyle.track(id);
                }
                // add to track Analysis Array
                cfg.getInstance().servoParm[id].taa.add();
                }
            processMoveCmdControl();
            for (id = 0; id < SERVO_ID.size(); id++)
                // reset acceleration to default value;
                // longRangeAccel set to .acceleration in initServoIDVars()
                cfg.getInstance().servoParm[id].acceleration = cfg.getInstance().servoParm[id].longRangeAccel;
        }
    }
    
    void moveToTargetEquatSubr(SEQUENCE seq, double timeIncrRad) {
        int id;
        double holdFieldRotation;
        
        position hold = new position("moveToTargetEquatSubr");
        
        if (seq == SEQUENCE.beg)
            softLimitTripped1 = meridianFlipped1 = false;
        else
            softLimitTripped2 = meridianFlipped2 = false;
        
        // save current values
        hold.copy(cfg.getInstance().current);
        holdFieldRotation = c.fieldRotation;
        // set current to target in order to calculate position to move to
        cfg.getInstance().current.ra.rad = target.getRaCorrectedForNutationAnnualAberration();
        cfg.getInstance().current.dec.rad = target.getDecCorrectedForNutationAnnualAberration();
        cfg.getInstance().current.sidT.rad += timeIncrRad;
        
        // calculate new altazimuth position to move to
        
        // getAltaz() includes coordinate translation to actual altaz if meridianFlip;
        c.getAltaz();
        // meridianFlip mounts cannot also be canMoveThruPole mounts
        checkForMoveThruPole();
        /**
         * check for meridian flip; if flip needed, slew will be minimum distances;
         * ie, flipped and scope tracking past meridian in northern hemisphere with default autoFuzzDeg:
         *    as az exceeds 187.5, meridianNeedsFlipping() returns true, destination az is 7.5+ with az move of 180-;
         */
        if (meridianNeedsFlipping()) {
            // (cfg.getInstance().Mount.meridianFlipPossible()) already checked in meridianNeedsFlipping()
            cfg.getInstance().Mount.meridianFlip().flipped = !cfg.getInstance().Mount.meridianFlip().flipped;
            c.getAltaz();
            cfg.getInstance().Mount.meridianFlip().flipped = !cfg.getInstance().Mount.meridianFlip().flipped;
            if (seq == SEQUENCE.beg)
                meridianFlipped1 = true;
            else
                meridianFlipped2 = true;
        }
        
        // check limits
        if (moveToTargetProcessSoftLimits()) {
            // update target altaz from updated current altaz coordinates calculated in getAltaz()
            target.alt.rad = cfg.getInstance().current.alt.rad;
            target.az.rad = cfg.getInstance().current.az.rad;
            // save offset values that are about to be changed
            cfg.getInstance().spa.holdOffsetRad = cfg.getInstance().spa.currentPositionOffsetRad;
            cfg.getInstance().spz.holdOffsetRad = cfg.getInstance().spz.currentPositionOffsetRad;
            // get corrected offsets of target coordinates: this includes all error corrections
            setServoPositionToCurrentAltaz();
            // get change in offsets which is change in actual motor positions
            cfg.getInstance().spa.deltaPosRad = cfg.getInstance().spa.currentPositionOffsetRad - cfg.getInstance().spa.holdOffsetRad;
            cfg.getInstance().spa.deltaPosRad = shortestAltitudeDistance(cfg.getInstance().spa.deltaPosRad);
            cfg.getInstance().spz.deltaPosRad = cfg.getInstance().spz.currentPositionOffsetRad - cfg.getInstance().spz.holdOffsetRad;
            cfg.getInstance().spz.deltaPosRad = shortestAzimuthDistance(cfg.getInstance().spz.deltaPosRad);
            if (cfg.getInstance().spa.sectorRad !=0.)
                if (cfg.getInstance().spa.deltaPosRad > cfg.getInstance().spa.sectorRad || cfg.getInstance().spa.deltaPosRad < -cfg.getInstance().spa.sectorRad) {
                cfg.getInstance().spa.deltaPosRad = 0.;
                cfg.getInstance().spa.sectorTriggered = true;
                }
            if (cfg.getInstance().spz.sectorRad !=0.)
                if (cfg.getInstance().spz.deltaPosRad > cfg.getInstance().spz.sectorRad || cfg.getInstance().spz.deltaPosRad < -cfg.getInstance().spz.sectorRad) {
                cfg.getInstance().spz.deltaPosRad = 0.;
                cfg.getInstance().spz.sectorTriggered = true;
                }
            if (cfg.getInstance().spr.controllerActive && cfg.getInstance().spr.track) {
                // do field rotation motor based on projected current as calculated earlier in function
                c.calcFieldRotation();
                // no error correcting for FR motor, so can simply find difference in Positions
                cfg.getInstance().spr.deltaPosRad = c.fieldRotation - cfg.getInstance().spr.currentPositionDeg * units.DEG_TO_RAD;
                cfg.getInstance().spr.deltaPosRad = shortestAzimuthDistance(cfg.getInstance().spr.deltaPosRad);
                if (cfg.getInstance().spr.sectorRad !=0.)
                    if (cfg.getInstance().spr.deltaPosRad > cfg.getInstance().spr.sectorRad || cfg.getInstance().spr.deltaPosRad < -cfg.getInstance().spr.sectorRad) {
                    cfg.getInstance().spr.deltaPosRad = 0.;
                    cfg.getInstance().spr.sectorTriggered = true;
                    }
            }
        } else {
            // else softlimit violation: set target to hold current
            target.copy(hold);
            if (seq == SEQUENCE.beg)
                softLimitTripped1 = true;
            else
                softLimitTripped2 = true;
        }
        // restore current and other values
        cfg.getInstance().current.copy(hold);
        if (cfg.getInstance().spa.sectorTriggered)
            cfg.getInstance().current.alt.rad = target.alt.rad;
        if (cfg.getInstance().spz.sectorTriggered)
            cfg.getInstance().current.az.rad = target.az.rad;
        // restore offsets used to get change in actual motor positions above
        cfg.getInstance().spa.currentPositionOffsetRad = cfg.getInstance().spa.holdOffsetRad;
        cfg.getInstance().spz.currentPositionOffsetRad = cfg.getInstance().spz.holdOffsetRad;
        // calculate new position if change of position was greater than the sector amount
        if (cfg.getInstance().spr.sectorTriggered) {
            cfg.getInstance().spr.currentPositionOffsetRad =
                    (double) cfg.getInstance().spr.actualPosition * cfg.getInstance().spr.countToRad - c.fieldRotation;
            setCurrentPositionDeg(SERVO_ID.fieldR.KEY);
        } else
            c.fieldRotation = holdFieldRotation;
        
        for (id = 0; id < SERVO_ID.size(); id++) {
            if (seq == SEQUENCE.beg)
                if (softLimitTripped1)
                    cfg.getInstance().servoParm[id].deltaPos1Rad = 0;
                else
                    cfg.getInstance().servoParm[id].deltaPos1Rad = cfg.getInstance().servoParm[id].deltaPosRad;
            else if (seq == SEQUENCE.end)
                if (softLimitTripped2)
                    cfg.getInstance().servoParm[id].deltaPos2Rad = 0;
                else {
                cfg.getInstance().servoParm[id].deltaPos2Rad = cfg.getInstance().servoParm[id].deltaPosRad;
                // reset deltaPosRad to first Positional difference
                cfg.getInstance().servoParm[id].deltaPosRad = cfg.getInstance().servoParm[id].deltaPos1Rad;
                }
        }
    }
    
    // if motion stopped, then commanding device finished with its movement command;
    // cannot use .finished as this merely indicates that final velocity has been reached, not that
    // move has been completed;
    boolean checkMotorStopSetCmdDeviceNone(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        if (sp.readStatusSuccessful && sp.motorStopped) {
            sp.cmdDevice = CMD_DEVICE.none;
            return true;
        }
        return false;
    }
    
    void checkMotorStopSetCmdDeviceNone() {
        int id;
        
        for (id = 0; id < SERVO_ID.size(); id++)
            checkMotorStopSetCmdDeviceNone(id);
    }
    
    /**
     * if motion finished, then commanding device finished with its movement command;
     * cannot use .motorStopped as position move takes time to get underway;
     * if cmdScopeList (only positional moves can step to here), then start with moveStateFinishedCheckOK=false,
     * wait until SERVO_MOVE_STATE is not .finished which means that the positional move has gotten underway,
     * then set moveStateFinishedCheckOK=true to allow the detection of .finished;
     */
    boolean checkMotorStateFinishSetCmdDeviceNone(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        if (sp.moveState == SERVO_MOVE_STATE.finished) {
            if (sp.cmdDevice != CMD_DEVICE.cmdScopeList || (sp.cmdDevice == CMD_DEVICE.cmdScopeList && sp.moveStateFinishedCheckOK)) {
                sp.cmdDevice = CMD_DEVICE.none;
                sp.moveStateFinishedCheckOK = false;
                return true;
            }
        } else
            if (sp.cmdDevice == CMD_DEVICE.cmdScopeList && !sp.moveStateFinishedCheckOK)
                sp.moveStateFinishedCheckOK = true;
        
        return false;
    }
    
    void checkMotorStateFinishSetCmdDeviceNone() {
        int id;
        
        for (id = 0; id < SERVO_ID.size(); id++)
            checkMotorStateFinishSetCmdDeviceNone(id);
    }
    
    // check for position move first, if not position move, then check to see if motion stopped
    boolean checkMotorMoveCompleteSetCmdDeviceNone(int id) {
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        if (sp.trajectoryStyle == TRAJECTORY_STYLE.position)
            return checkMotorStateFinishSetCmdDeviceNone(id);
        else
            return checkMotorStopSetCmdDeviceNone(id);
    }
    
    void checkMotorMoveCompleteSetCmdDeviceNone() {
        int id;
        
        for (id = 0; id < SERVO_ID.size(); id++)
            checkMotorMoveCompleteSetCmdDeviceNone(id);
    }
    
    /**
     * displays that can be turned on/off:
     *    track.sequencer(): if displayDiagnostics:
     *       displayHandpadStatus()
     *       displayMove()
     *       TrackStyle.displayTrackingVars()
     *       cfg.getInstance().current.showCoord()
     *
     *    servo.readStatus():
     *       servo.displayCmd   (also used in hardReset())
     *          displayCmdResults()
     *       servo.displayOutBuf
     *          displayOutBuf()
     *       servo.displayStatus
     *          displayStatusReturn()
     *       servo.displayStickyBits
     *          displayStickyBits()
     *       servo.displayBadStatusReturn
     *
     *    servoPort(class ioSerial).displayReceivedChar
     *
     *    LX200.displayDiagnostics via LX200.io.displayReceivedChar()
     *
     * astroTime.getInstance().calcSidT() called in
     *    waitForReadBytes()
     *    calcPostInitVars()
     *    readLX200Input()
     *    setCurrentAltazToServoPosition()
     *    setServoPositionToCurrentAltaz()
     *    moveToTargetEquat()
     *    sequencer()
     */
    void setCurrentAltazGetEquatCopyTarget() {
        cfg.getInstance().current.sidT.rad = astroTime.getInstance().sidT.rad;
        setCurrentAltazToServoPosition();
        if (cfg.getInstance().initialized()) {
            // meridianFlip updated per altaz coordinates, which then allows correct equatorial coordinates to be calculated;
            c.detectMeridianFlipFromCurrentAltaz();
            c.getEquat();
            // moveToTargetEquat() tracks to target.ra.rad and target.dec.rad so update target equat coord
            target.copy(cfg.getInstance().current);
        }
    }
    
    /**
     * called by trackBuilder.run()
     */
    void sequencer(int motor, boolean displayDiagnostics) {
        /**
         * check for various cmd channel inputs here,
         * including checking for scroll file  - automatically executing cmdScopeList here;
         * manually executed commands are called in processHandpadModeSwitch();
         * processHandpadModeSwitch() is called from checkProcessHandpadCmd():
         *    from cmdCol.LX200.process_A_Cmd() and from cmdCol.cmdScopeList.processCmd()
         * .checkProcessCmd() -> cmdInProgress() can do additional processing if command to be
         * executed over a period of time, ie, equatorial move over time will increment the target
         * equatorial coordinates each call;
         */
        cmdCol.checkProcessCmd();
        
        // reads handpad
        getStatusAll();
        
        // includes check for read status success
        // don't let .SiTechHandpad be set to .DeviceNone as SiTech handpad button delay will be shortcircuited
        if (!(cfg.getInstance().spa.cmdDevice == CMD_DEVICE.SiTechHandpad
                || cfg.getInstance().spz.cmdDevice == CMD_DEVICE.SiTechHandpad))
            checkMotorMoveCompleteSetCmdDeviceNone();
        
        if (cfg.getInstance().controllerManufacturer == CONTROLLER_MANUFACTURER.SiTech
                && cfg.getInstance().spz.readStatusSuccessful)
            checkPECSynchFromYBits();
        else {
            if (cfg.getInstance().spa.readStatusSuccessful)
                checkPECSynchFromLimit2(SERVO_ID.altDec.KEY);
            if (cfg.getInstance().spz.readStatusSuccessful)
                checkPECSynchFromLimit2(SERVO_ID.azRa.KEY);
        }
        
        if (cfg.getInstance().spa.readStatusSuccessful && cfg.getInstance().spz.readStatusSuccessful) {
            checkProcessHandpadCmd(motor, displayDiagnostics);
            
            /**
             * if any motor is executing a non-tracking move, update to new target equatorial position;
             * SiTech controller in guide mode also updates new target equatorial position when guiding
             * correction occurs: easier to passively observe controller and update target equat as needed
             * and resume normal tracking rather than attempt to deduce guiding corrections executed by
             * the controller autonomously and apply these corrections to current altaz via accumGuide;
             */
            if (cfg.getInstance().spa.cmdDevice == CMD_DEVICE.handpad
                    || cfg.getInstance().spz.cmdDevice == CMD_DEVICE.handpad
                    || cfg.getInstance().spa.cmdDevice == CMD_DEVICE.SiTechHandpad
                    || cfg.getInstance().spz.cmdDevice == CMD_DEVICE.SiTechHandpad
                    || cfg.getInstance().spa.cmdDevice == CMD_DEVICE.LX200
                    || cfg.getInstance().spz.cmdDevice == CMD_DEVICE.LX200
                    || cfg.getInstance().spa.cmdDevice == CMD_DEVICE.cmdScopeList
                    || cfg.getInstance().spz.cmdDevice == CMD_DEVICE.cmdScopeList) {
                /**
                 * if SiTech guiding and CMD_DEVICE.SiTechHandpad, then guiding action has occured;
                 * SiTech controller guiding calculates accumulated guide differently: simulator must be running;
                 */
                if (SiTechGuideActive)
                    calcWriteSiTechAccumGuide();
                setCurrentAltazGetEquatCopyTarget();
            }
            
            /**
             * time interval necessary to calculate guiding distance from guiding speed;
             * timer starts at end of calcAccumGuide() function;
             * calcAccumGuide() must be called next time through with lastAccumGuideAction == currentAccumGuideAction
             * for time interval to be calculated, only then will guideIncr be calculated and added to accumGuide;
             * currentAccumGuideAction set to buttons and calcAccumGuide() called from processHandpad();
             * LX200 control calls calcAccumGuide() here because LX200 issues a single start guide followed by an
             * eventual stop guide command, unlike the handpad which issues a continuous stream of guiding commands
             */
            if (cfg.getInstance().LX200Control && cmdCol.LX200.portOpened)
                if (cmdCol.LX200.accumGuideAction > 0) {
                currentAccumGuideAction = cmdCol.LX200.accumGuideAction;
                calcAccumGuide();
                }
            
            // since calcAccumGuide() has to be called twice, cannot call this method until now
            setLastAccumGuideAction();
            
            if (cfg.getInstance().initialized()) {
                /**
                 * moveToTargetEquat() has checks for tracking suitability and will allow individual motors to track regardless
                 * of status of other motors;
                 * moveToTargetEquat() incorporates astroTime.getInstance().calcSidT() and setCurrentAltazToServoPosition(): wait until here to set
                 * current altaz coordinates so that corrective values to altaz coordinates can be set by functions above
                 */
                moveToTargetEquat();
                c.detectMeridianFlipFromCurrentAltaz();
                if (displayDiagnostics)
                    TrackStyle.displayTrackingVars(motor);
                
                // update current equatorial coordinates
                c.getEquat();
            } else {
                cfg.getInstance().current.sidT.rad = astroTime.getInstance().sidT.rad;
                setCurrentAltazToServoPosition();
            }
            // if SiTech encoders, encoder positions read in motor controller status return;
            if (ef != null && ef.E != null && ef.E.encoderType() == ENCODER_TYPE.encoderSiTech) {
                ef.E.readEncodersAndProcessPositions();
                // setCurrentAltazToServoPosition() called either just above or in moveToTargetEquat() resulting in setting
                // current altaz coordinates: compare to encoder coordinates and adjust based on threshold
                checkEncoderThreshold();
            }
            
            if (displayDiagnostics)
                cfg.getInstance().current.showCoord();
        }
    }
    
    void checkProcessHandpadCmd(int motor, boolean displayDiagnostics) {
        if (displayDiagnostics) {
            displayHandpadStatus();
            displayMove(motor);
        }
        // process handpad commands: checks that the input ports used for the handpad have been read and debounced successfully
        if (handpadSuccessfullyRead) {
            /**
             * handpad velocity profile will assume control and set .cmdDevice = CMD_DEVICE.handpad regardless of previous move action;
             * status for motor with a new handpad action (direction button press/release, speed change while direction button pressed)
             * will be updated in handpadProcessDirButtons();
             * SiTech controller in guide mode is in charge of its own guiding corrections - don't calculate them here
             */
            if (guideActive && !SiTechGuideActive) {
                currentAccumGuideAction = buttons;
                calcAccumGuide();
            } else
                // do not send handpad velocity commands if handpad is passively observing handpad activity
                if (!HandpadDesigns.passiveObserver())
                    handpadProcessDirButtons();
            // if handpad mode is set, then process
            if (HandpadModes != null) {
                // decrement handpad button timers regardless of button state
                HandpadModes.processTimers();
                checkSaveGuideArray();
            }
            if ((buttons & handpadDesignBase.LEFT_KEY) == handpadDesignBase.LEFT_KEY
                    || (buttons & handpadDesignBase.RIGHT_KEY) == handpadDesignBase.RIGHT_KEY)
                processHandpadModeSwitch();
        }
    }
    
    void testTrackingSubr(int ts, boolean displaySequencerDiagnostics) {
        int id;
        int motor = 0;
        
        turnTrackingOn();
        
        for (id = 0; id < SERVO_ID.size(); id++)
            cfg.getInstance().servoParm[id].cfgAccelDegSecSec = cfg.getInstance().servoParm[id].accelDegSecSec;
        
        if (cfg.getInstance().initialized()) {
            cfg.getInstance().current.alt.rad = 50.*units.DEG_TO_RAD;
            cfg.getInstance().current.az.rad = 100.*units.DEG_TO_RAD;
            cfg.getInstance().current.sidT.rad = astroTime.getInstance().calcSidT();
            c.getEquat();
            setServoPositionToCurrentAltaz();
            target.copy(cfg.getInstance().current);
            
            System.out.println("try .33");
            System.out.print("moveToTargetTimeSec? ");
            console.getDouble();
            cfg.getInstance().moveToTargetTimeSec = console.d;
            
            System.out.print("amount to slew in RA (deg)? ");
            console.getDouble();
            target.ra.rad += console.d*units.DEG_TO_RAD;
            
            if (ts == TRACK_STYLE_ID.trackStylePID.KEY) {
                System.out.println("try .5 .5 .5 .00015 0");
                System.out.print("SoftKp? ");
                console.getDouble();
                for (id = 0; id < SERVO_ID.size(); id++)
                    cfg.getInstance().servoParm[id].softKp = console.d;
                System.out.print("SoftKd? ");
                console.getDouble();
                for (id = 0; id < SERVO_ID.size(); id++)
                    cfg.getInstance().servoParm[id].softKd = console.d;
                System.out.print("SoftKi? ");
                console.getDouble();
                for (id = 0; id < SERVO_ID.size(); id++)
                    cfg.getInstance().servoParm[id].softKi = console.d;
                System.out.print("SoftKiLimit? ");
                console.getDouble();
                for (id = 0; id < SERVO_ID.size(); id++)
                    cfg.getInstance().servoParm[id].softKiLimit = console.d;
                System.out.print("Include deccel check (1/0)? ");
                console.getInt();
                if (console.i == 1)
                    TrackStyle.setDeccelCheck(true);
                else
                    TrackStyle.setDeccelCheck(false);
            } else if (ts == TRACK_STYLE_ID.trackStyleVelocity.KEY) {
                System.out.println("(similar to PID with Kp*moveToTargetTimeSec only) try .5");
                System.out.print("dampenFactor? ");
                console.getDouble();
                for (id = 0; id < SERVO_ID.size(); id++)
                    cfg.getInstance().servoParm[id].dampenFactor = console.d;
                System.out.print("Include deccel check? (1/0)? ");
                console.getInt();
                if (console.i == 1)
                    TrackStyle.setDeccelCheck(true);
                else
                    TrackStyle.setDeccelCheck(false);
            } else if (ts == TRACK_STYLE_ID.trackStylePropVel.KEY) {
                System.out.println("try .01");
                System.out.print("dampenFactor? ");
                console.getDouble();
                for (id = 0; id < SERVO_ID.size(); id++)
                    cfg.getInstance().servoParm[id].dampenFactor = console.d;
                System.out.print("Include deccel check? (1/0)? ");
                console.getInt();
                if (console.i == 1)
                    TrackStyle.setDeccelCheck(true);
                else
                    TrackStyle.setDeccelCheck(false);
            } else if (ts == TRACK_STYLE_ID.trackStylePropVel2.KEY) {
                System.out.println("percentage of velocity to position 1, try .1 ");
                System.out.print("dampenFactor? ");
                console.getDouble();
                for (id = 0; id < SERVO_ID.size(); id++)
                    cfg.getInstance().servoParm[id].dampenFactor = console.d;
                System.out.print("Include deccel check? (1/0)? ");
                console.getInt();
                if (console.i == 1)
                    TrackStyle.setDeccelCheck(true);
                else
                    TrackStyle.setDeccelCheck(false);
            } else if (ts == TRACK_STYLE_ID.trackStylePosition.KEY)
                ;
            else if (ts == TRACK_STYLE_ID.trackStyleTrajTimed.KEY)
                ;
            else if (ts == TRACK_STYLE_ID.trackStylePositionWithPropVel.KEY) {
                System.out.println("try .01");
                System.out.print("dampenFactor? ");
                console.getDouble();
                for (id = 0; id < SERVO_ID.size(); id++)
                    cfg.getInstance().servoParm[id].dampenFactor = console.d;
                System.out.print("Include deccel check? (1/0)? ");
                console.getInt();
                if (console.i == 1)
                    TrackStyle.setDeccelCheck(true);
                else
                    TrackStyle.setDeccelCheck(false);
            } else if (ts == TRACK_STYLE_ID.trackStylePositionWithPropVel2.KEY) {
                System.out.println("try .1");
                System.out.print("dampenFactor? ");
                console.getDouble();
                for (id = 0; id < SERVO_ID.size(); id++)
                    cfg.getInstance().servoParm[id].dampenFactor = console.d;
                System.out.print("Include deccel check? (1/0)? ");
                console.getInt();
                if (console.i == 1)
                    TrackStyle.setDeccelCheck(true);
                else
                    TrackStyle.setDeccelCheck(false);
            } else if (ts == TRACK_STYLE_ID.trackStyleTrajVel.KEY) {
                System.out.println("try .15");
                System.out.print("dampenFactor? ");
                console.getDouble();
                for (id = 0; id < SERVO_ID.size(); id++)
                    cfg.getInstance().servoParm[id].dampenFactor = console.d;
                System.out.print("Include deccel check? (1/0)? ");
                console.getInt();
                if (console.i == 1)
                    TrackStyle.setDeccelCheck(true);
                else
                    TrackStyle.setDeccelCheck(false);
            }
            
            System.out.println("what motor to display vars for? ");
            for (id = 0; id < SERVO_ID.size(); id++)
                if (cfg.getInstance().servoParm[id].controllerActive)
                    System.out.print(id+ ":"
                            + SERVO_ID.matchKey(id)
                            + "   ");
            console.getInt();
            motor = console.i;
            
            buttons = 0;
            cfg.getInstance().servoParm[motor].accumGuideRad = 0.;
            cfg.getInstance().handpadMode = HANDPAD_MODE.handpadModeGuide;
            System.out.println("handpadMode is " + cfg.getInstance().handpadMode);
            System.out.println("use handpad right mode key to stop...");
            
            // shutdown set by external control file, or, cmdScopeList command 'quit'
            while (((buttons & handpadDesignBase.RIGHT_KEY) != handpadDesignBase.RIGHT_KEY) && !shutdown)
                sequencer(motor, displaySequencerDiagnostics);
            
            if (shutdown)
                System.out.println("ending sequencer() due to shutdown");
            if ((buttons & handpadDesignBase.RIGHT_KEY) == handpadDesignBase.RIGHT_KEY)
                System.out.println("ending sequencer() due to handpad RIGHT_KEY press");
            
            System.out.print("returning motors to start position...");
            for (id = 0; id < SERVO_ID.size(); id++) {
                cfg.getInstance().servoParm[id].targetPosition = 0;
                cfg.getInstance().servoParm[id].moveNow = true;
                cfg.getInstance().servoParm[id].velRadSec = cfg.getInstance().servoParm[id].fastSpeedRadSec;
                cfg.getInstance().servoParm[id].moveCmd = SERVO_MOVE_CMD.posMoveStopCurrent;
                cfg.getInstance().servoParm[id].servoCmdProcessed = false;
            }
            processMoveCmdControl();
            do {
                getStatusAll();
            }while (cfg.getInstance().spa.actualPosition != 0 || cfg.getInstance().spz.actualPosition != 0);
            displayStatusReturn(SERVO_ID.altDec.KEY);
            displayStatusReturn(SERVO_ID.azRa.KEY);
            setCurrentAltazToServoPosition();
            TrackStyle.displayTrackingVars(motor);
            cfg.getInstance().servoParm[motor].taa.display();
            turnTrackingOffStopAllMotors();
            waitForAllMotorsStop();
            lscr.displayMostRecent(20);
        } else
            System.out.println("no one and two: could not test tracking routines");
        
        for (id = 0; id < SERVO_ID.size(); id++) {
            cfg.getInstance().servoParm[id].accelDegSecSec = cfg.getInstance().servoParm[id].cfgAccelDegSecSec;
            cfg.getInstance().servoParm[id].acceleration = (long) (cfg.getInstance().servoParm[id].accelDegSecSec * cfg.getInstance().servoParm[id].accelDegSecSecToCountsTickTick());
        }
    }
    
    void testTracking(boolean displaySequencerDiagnostics) {
        int id;
        TRACK_STYLE_ID ts = null;
        int mainSelect;
        boolean quit = false;
        
        while (!quit) {
            do {
                System.out.println("test of track module");
                // stop all prior motion
                for (id = 0; id < SERVO_ID.size(); id++) {
                    cfg.getInstance().servoParm[id].moveCmd = SERVO_MOVE_CMD.stopMoveSmoothly;
                    cfg.getInstance().servoParm[id].servoCmdProcessed = false;
                }
                processMoveCmdControl();
                System.out.println("test of tracking routines, select from:");
                
                Enumeration eTRACK_STYLE = TRACK_STYLE_ID.elements();
                while (eTRACK_STYLE.hasMoreElements()) {
                    ts = (TRACK_STYLE_ID) eTRACK_STYLE.nextElement();
                    System.out.println("      "
                            + (ts.KEY+1)
                            + ". "
                            + ts);
                }
                System.out.println("      "
                        + (TRACK_STYLE_ID.size()+1)
                        + ". quit");
                
                console.getInt();
                mainSelect = console.i;
            }
            while (mainSelect < 1);
            
            if (mainSelect >= TRACK_STYLE_ID.size()+1)
                quit = true;
            else {
                TrackStyle = new trackStyleFactory().build(TRACK_STYLE_ID.matchKey(mainSelect-1));
                testTrackingSubr(mainSelect-1, displaySequencerDiagnostics);
            }
        }
    }
    
    String buildGuidePECString(int id) {
        Iterator it;
        SERVO_PARMS sp = cfg.getInstance().servoParm[id];
        
        guidePECString = "\nAccumGuide = "
                + eString.doubleToStringNoGrouping(sp.accumGuideRad * units.RAD_TO_ARCSEC, 3, 1)
                + " arcsec, saving guide for PEC: "
                + guideArrayState.toString();
        
        guidePECString += "\nPEC is " + (sp.PECActive?"ON ":"off");
        it = sp.lg.iterator();
        while (it.hasNext()) {
            guide g = (guide) it.next();
            if (g.PECActive)
                guidePECString += g.buildPECString();
        }
        return guidePECString;
    }
    
    void calcGuideFRAngle() {
        guideFRAngle = c.fieldRotation - guideFRAngleOffset;
        guideFRAngle = eMath.validRad(guideFRAngle);
    }
    
    void zeroDrift() {
        int id;
        
        for (id = 0; id < SERVO_ID.size(); id++)
            cfg.getInstance().servoParm[id].driftRadMin = 0.;
        
        cfg.getInstance().driftRaDegPerHr = 0.;
        cfg.getInstance().driftDecDegPerHr = 0.;
    }
    
    /**
     * add to previous drift: allows PEC routines to refine drift iteratively as guiding cycles are accomplished
     */
    void updateDriftCalculatedFromAccumGuide() {
        driftSidtDiff = endDriftSidT - startDriftSidT;
        driftSidtDiff = eMath.validRad(driftSidtDiff);
        
        cfg.getInstance().spa.driftRadMin += cfg.getInstance().spa.accumGuideRad/(driftSidtDiff*units.RAD_TO_MIN);
        cfg.getInstance().spz.driftRadMin += cfg.getInstance().spz.accumGuideRad/(driftSidtDiff*units.RAD_TO_MIN);
        
        calcDriftArcsecPerMin();
    }
    
    void startDriftT() {
        startDriftSidT = astroTime.getInstance().sidT.rad;
    }
    
    void setEndDriftT() {
        endDriftSidT = astroTime.getInstance().sidT.rad;
        endDriftSidT = eMath.validRad(endDriftSidT);
    }
    
    /**
     * a guide action in a direction sets currentAccumGuideAction prior to function call;
     * next time function called, if guiding action remains the same, the action will be acted upon;
     * lastAccumGuideAction will be set to 0 any time sequencer() is called with no guiding action;
     * all this to set lastAccumGuideTimeRad to ensure a valid time interval to calculate guideIncr;
     * function called from sequencer in two different places, one when processing handpad buttons
     * if guideActive, and the other based on LX200 commands;
     * a log of accumulated guiding corrections versus time is always written;
     * if PEC 'on' then accum guide written to the appropriate guide array which will include PEC index
     */
    void calcAccumGuide() {
        calcAccumGuideCalled = true;
        
        if (lastAccumGuideTimeRad > astroTime.getInstance().sidT.rad)
            lastAccumGuideTimeRad -= units.ONE_REV;
        
        // amount to increment accumGuide = guide speed in radians/second * time increment in seconds
        cfg.getInstance().spa.guideIncr = cfg.getInstance().spa.guideArcsecSec*units.ARCSEC_TO_RAD * (astroTime.getInstance().sidT.rad-lastAccumGuideTimeRad)*units.RAD_TO_SEC;
        cfg.getInstance().spz.guideIncr = cfg.getInstance().spz.guideArcsecSec*units.ARCSEC_TO_RAD * (astroTime.getInstance().sidT.rad-lastAccumGuideTimeRad)*units.RAD_TO_SEC;
        
        // currentAccumGuideAction and lastAccumGuideAction manipulated outside of this function
        if (currentAccumGuideAction == lastAccumGuideAction) {
            if (cfg.getInstance().handpadMode == HANDPAD_MODE.handpadModeGuideStayRotate) {
                calcGuideFRAngle();
                if ((currentAccumGuideAction & handpadDesignBase.UP_KEY) == handpadDesignBase.UP_KEY) {
                    cfg.getInstance().spa.accumGuideRad -= Math.cos(guideFRAngle) * cfg.getInstance().spa.guideIncr;
                    cfg.getInstance().spz.accumGuideRad -= Math.sin(guideFRAngle) * cfg.getInstance().spz.guideIncr;
                }
                if ((currentAccumGuideAction & handpadDesignBase.DOWN_KEY) == handpadDesignBase.DOWN_KEY) {
                    cfg.getInstance().spa.accumGuideRad += Math.cos(guideFRAngle) * cfg.getInstance().spa.guideIncr;
                    cfg.getInstance().spz.accumGuideRad += Math.sin(guideFRAngle) * cfg.getInstance().spz.guideIncr;
                }
                if ((currentAccumGuideAction & handpadDesignBase.CW_KEY) == handpadDesignBase.CW_KEY) {
                    cfg.getInstance().spz.accumGuideRad -= Math.cos(guideFRAngle) * cfg.getInstance().spz.guideIncr;
                    cfg.getInstance().spa.accumGuideRad += Math.sin(guideFRAngle) * cfg.getInstance().spa.guideIncr;
                }
                if ((currentAccumGuideAction & handpadDesignBase.CCW_KEY) == handpadDesignBase.CCW_KEY) {
                    cfg.getInstance().spz.accumGuideRad += Math.cos(guideFRAngle) * cfg.getInstance().spz.guideIncr;
                    cfg.getInstance().spa.accumGuideRad -= Math.sin(guideFRAngle) * cfg.getInstance().spa.guideIncr;
                }
            } else {
                if ((currentAccumGuideAction & handpadDesignBase.UP_KEY) == handpadDesignBase.UP_KEY)
                    cfg.getInstance().spa.accumGuideRad -= cfg.getInstance().spa.guideIncr;
                if ((currentAccumGuideAction & handpadDesignBase.DOWN_KEY) == handpadDesignBase.DOWN_KEY)
                    cfg.getInstance().spa.accumGuideRad += cfg.getInstance().spa.guideIncr;
                if ((currentAccumGuideAction & handpadDesignBase.CW_KEY) == handpadDesignBase.CW_KEY)
                    cfg.getInstance().spz.accumGuideRad -= cfg.getInstance().spz.guideIncr;
                if ((currentAccumGuideAction & handpadDesignBase.CCW_KEY) == handpadDesignBase.CCW_KEY)
                    cfg.getInstance().spz.accumGuideRad += cfg.getInstance().spz.guideIncr;
            }
            
            // add to log files
            cfg.getInstance().spa.lagr.add(cfg.getInstance().spa.accumGuideRad, lastAccumGuideTimeRad);
            cfg.getInstance().spz.lagr.add(cfg.getInstance().spz.accumGuideRad, lastAccumGuideTimeRad);
            
            writeToGuideArrays();
        }
        lastAccumGuideTimeRad = astroTime.getInstance().sidT.rad;
    }
    
    void setLastAccumGuideAction() {
        if (calcAccumGuideCalled)
            lastAccumGuideAction = currentAccumGuideAction;
        else
            lastAccumGuideAction = 0;
        calcAccumGuideCalled = false;
    }
    
    // PEC must be active in order to generate PEXIx, and guide must be active too
    void writeToGuideArrays() {
        Iterator it;
        
        // check to see if altitude accumGuideRad should be written to a guide array:
        it = cfg.getInstance().spa.lg.iterator();
        while (it.hasNext()) {
            guide g = (guide) it.next();
            if (g.PECActive)
                if (g.guideArrayState == GUIDE_ARRAY_STATE.writing) {
                g.writeToGuideArray(cfg.getInstance().spa.accumGuideRad);
                }
        }
        // check to see if azimuth accumGuideRad should be written to a guide array
        it = cfg.getInstance().spz.lg.iterator();
        while (it.hasNext()) {
            guide g = (guide) it.next();
            if (g.PECActive)
                if (g.guideArrayState == GUIDE_ARRAY_STATE.writing) {
                g.writeToGuideArray(cfg.getInstance().spz.accumGuideRad);
                }
        }
    }
    
    /**
     * since SiTech controller executes guiding corrections autonomously, and because it's impossible to obtain
     * the exact guiding correction executed due to not knowing the precise time the controller position reading
     * was made, deduce controller guiding corrections by comparing the controller actual position to the simulator
     * position;
     * + posErr results from down/CCW key press, and + accumGuide indicates excessive up/CW movement;
     * therefore down/CCW movement to compensate for excessive up/CW movement should result in + accumGuide
     */
    void calcWriteSiTechAccumGuide() {
        cfg.getInstance().spa.accumGuideRad = cfg.getInstance().spa.posErrRad - cfg.getInstance().spa.startPosErrRad;
        cfg.getInstance().spz.accumGuideRad = cfg.getInstance().spz.posErrRad - cfg.getInstance().spz.startPosErrRad;
        
        if (lastAccumGuideRad.a != cfg.getInstance().spa.accumGuideRad) {
            lastAccumGuideRad.a = cfg.getInstance().spa.accumGuideRad;
            cfg.getInstance().spa.lagr.add(cfg.getInstance().spa.accumGuideRad, cfg.getInstance().current.sidT.rad);
        }
        if (lastAccumGuideRad.z != cfg.getInstance().spz.accumGuideRad) {
            lastAccumGuideRad.z = cfg.getInstance().spz.accumGuideRad;
            cfg.getInstance().spz.lagr.add(cfg.getInstance().spz.accumGuideRad, cfg.getInstance().current.sidT.rad);
        }
        writeToGuideArrays();
    }
    
    void buildGuideAnalysisFiles() {
        Iterator it;
        
        // altitude
        it = cfg.getInstance().spa.lg.iterator();
        while (it.hasNext()) {
            guide g = (guide) it.next();
            if (g.PECActive)
                if (g.loadGuide())
                    g.analyzeGuideArray();
        }
        // azimuth
        it = cfg.getInstance().spz.lg.iterator();
        while (it.hasNext()) {
            guide g = (guide) it.next();
            if (g.PECActive)
                if (g.loadGuide())
                    g.analyzeGuideArray();
        }
    }
    
    void createPECfromGuideAnalysisFiles() {
        Iterator it;
        
        // altitude
        it = cfg.getInstance().spa.lg.iterator();
        while (it.hasNext()) {
            guide g = (guide) it.next();
            if (g.PECActive) {
                g.averagePECAnalysisFiles();
                g.smoothPEC();
            }
        }
        // azimuth
        it = cfg.getInstance().spz.lg.iterator();
        while (it.hasNext()) {
            guide g = (guide) it.next();
            if (g.PECActive) {
                g.averagePECAnalysisFiles();
                g.smoothPEC();
            }
        }
    }
    
    void savePEC() {
        Iterator it;
        
        // altitude
        it = cfg.getInstance().spa.lg.iterator();
        while (it.hasNext()) {
            guide g = (guide) it.next();
            g.savePEC(g.PECDataFilename);
        }
        // azimuth
        it = cfg.getInstance().spz.lg.iterator();
        while (it.hasNext()) {
            guide g = (guide) it.next();
            g.savePEC(g.PECDataFilename);
        }
    }
    
    // add accum guide, drift to cfg.getInstance().current position
    void addAccumGuideDriftToCurrentPosition() {
        setCurrentAltazToServoPosition();
        c.getEquat();
        target.copy(cfg.getInstance().current);
    }
    
    /**
     * guideActive is used by the handpad routine and other routines to test if guiding should be looked at in general;
     * cfg.getInstance().servoParm[id].GuideArcsec>0 is looked at in the routines that set servo position to current coordinates and visa versa;
     * cfg.getInstance().servoParm[id].lg.guideActive are used with respect to recording guiding efforts
     */
    void initAllGuide() {
        int id;
        
        guideActive = true;
        
        cfg.getInstance().spa.lagr.begIx = 0;
        cfg.getInstance().spa.lagr.lagIx = 0;
        cfg.getInstance().spz.lagr.begIx = 0;
        cfg.getInstance().spz.lagr.lagIx = 0;
        
        for (id = 0; id < SERVO_ID.size(); id++)
            cfg.getInstance().servoParm[id].accumGuideRad = 0.;
    }
    
    void stopAllGuide() {
        int id;
        Iterator it;
        
        guideActive = false;
        
        for (id = 0; id < SERVO_ID.size(); id++)
            cfg.getInstance().servoParm[id].accumGuideRad = 0.;
    }
    
    void initSaveGuideArray() {
        Iterator it;
        
        it = cfg.getInstance().spa.lg.iterator();
        while (it.hasNext()) {
            guide g = (guide) it.next();
            if (g.guideActive)
                g.initGuide();
        }
        it = cfg.getInstance().spz.lg.iterator();
        while (it.hasNext()) {
            guide g = (guide) it.next();
            if (g.guideActive)
                g.initGuide();
        }
        
        // guide array will not be written to unless .guideArrayState set to .writing
        setAllGuideArrayStates(GUIDE_ARRAY_STATE.writing);
    }
    
    void SaveGuideArray() {
        saveAllGuide();
        setAllGuideArrayStates(GUIDE_ARRAY_STATE.off);
    }
    
    void setAllGuideArrayStates(GUIDE_ARRAY_STATE GAF) {
        Iterator it;
        
        guideArrayState = GAF;
        
        it = cfg.getInstance().spa.lg.iterator();
        while (it.hasNext()) {
            guide g = (guide) it.next();
            if (g.guideActive)
                g.guideArrayState = GAF;
        }
        it = cfg.getInstance().spz.lg.iterator();
        while (it.hasNext()) {
            guide g = (guide) it.next();
            if (g.guideActive)
                g.guideArrayState = GAF;
        }
    }
    
    boolean checkAllGuideArrayStates(GUIDE_ARRAY_STATE GAF) {
        Iterator it;
        
        if (guideArrayState == GAF)
            return true;
        
        it = cfg.getInstance().spa.lg.iterator();
        while (it.hasNext()) {
            guide g = (guide) it.next();
            if (g.guideArrayState == GAF)
                return true;
        }
        it = cfg.getInstance().spz.lg.iterator();
        while (it.hasNext()) {
            guide g = (guide) it.next();
            if (g.guideArrayState == GAF)
                return true;
        }
        
        return false;
    }
    
    void saveAllGuide() {
        Iterator it;
        
        it = cfg.getInstance().spa.lg.iterator();
        while (it.hasNext()) {
            guide g = (guide) it.next();
            if (g.guideArrayState == GUIDE_ARRAY_STATE.readyToSave)
                g.saveGuide();
        }
        it = cfg.getInstance().spz.lg.iterator();
        while (it.hasNext()) {
            guide g = (guide) it.next();
            if (g.guideArrayState == GUIDE_ARRAY_STATE.readyToSave)
                g.saveGuide();
        }
    }
    
    // guide with save on can be ended by handpad or by filling up the guide array in writeToGuideArray();
    // writeToGuideArray() only called in calcAccumGuide() when .PECActive and .guideArrayState == GUIDE_ARRAY_STATE.writing;
    boolean OkToStartGuideForWriteNSave() {
        Iterator it;
        
        if (!cfg.getInstance().initialized())
            return false;
        
        if (!guideActive)
            return false;
        
        if (guideArrayState != GUIDE_ARRAY_STATE.off)
            return false;
        
        it = cfg.getInstance().spa.lg.iterator();
        while (it.hasNext()) {
            guide g = (guide) it.next();
            if (g.PECActive && g.guideActive)
                return true;
        }
        it = cfg.getInstance().spz.lg.iterator();
        while (it.hasNext()) {
            guide g = (guide) it.next();
            if (g.PECActive && g.guideActive)
                return true;
        }
        return false;
    }
    
    boolean checkStartGuideForWriteNSave() {
        if (OkToStartGuideForWriteNSave()) {
            initSaveGuideArray();
            return true;
        }
        return false;
    }
    
    boolean checkReadyToSaveGuide() {
        if (guideArrayState == GUIDE_ARRAY_STATE.writing) {
            setAllGuideArrayStates(GUIDE_ARRAY_STATE.readyToSave);
            return true;
        }
        return false;
    }
    
    boolean checkSaveGuideArray() {
        if (checkAllGuideArrayStates(GUIDE_ARRAY_STATE.readyToSave)) {
            SaveGuideArray();
            return true;
        }
        return false;
    }
    
    /**
     * LX200 extended control commands 'left key press' and 'right key press' also call this function as does cmdScopeList extended cmds;
     * this method builds a new handpadmode if necessary
     */
    void processHandpadModeSwitch() {
        if (cfg.getInstance().handpadMode == null || HandpadModes == null || cfg.getInstance().handpadMode != HandpadModes.handpadMode()) {
            HandpadModes = new handpadModeFactory().build(cfg.getInstance().handpadMode, this);
            if (HandpadModes == null)
                HandpadModes = new handpadModeFactory().build(HANDPAD_MODE.handpadModeOff, this);
            cfg.getInstance().handpadMode = HandpadModes.handpadMode();
        }
        HandpadModes.processModeKeys();
    }
    
    /**
     * routine courtesy of Don Ware;
     * routine is broken into several parts to allow user to recenter stars and finally physically adjust the mount;
     * stages are advanced by pressing or simulating the pressing of the left/right handpad buttons in processHandpadModeSwitch()
     * which repeatedly calls this function if handpad is in PolarAlign mode;
     * 1. load 3 stars into polarAlign1, 2, and 3
     * 2. user presses a mode key, and if 3 stars loaded then scope slews to star 1
     * 3. user centers star 1 in eyepiece
     * 4. user presses a mode key, equat coord are reset to star 1, and scope slews to star 2
     * 5. user centers star 2 in eyepiece
     * 6. user presses a mode key, function calculates polar offset and slews to newly calculated offset star 3
     * 7. user physically adjusts the mount in altazmith axes (not equatorial axes) to center star 3 in eyepiece
     * 8. user presses a mode key, scope resets to original star 3 equat coord and slews back to star 1, which should be centered
     */
    void processPolarAlign() {
        String s;
        
        position star1 = new position("star1");
        position star2 = new position("star2");
        position star3 = new position("star3");
        position apparentStar2 = new position("apparentStar2");
        position correctedStar3 = new position("correctedStar3");
        position fixedStar1 = new position("fixedStar1");
        position fixedStar2 = new position("fixedStar2");
        position fixedStar3 = new position("fixedStar3");
        position fixedApparentStar2 = new position("fixedApparentStar2");
        position errorStar2 = new position("errorStar2");
        position polarAxisError = new position("polarAxisError");
        position offsetStar3 = new position("offsetStar3");
        
        double sin1;
        double sin2;
        double sin3;
        double cos1;
        double cos2;
        double cos3;
        double tan1;
        double tan2;
        double tan3;
        double d;
        
        POLAR_ALIGN_STAGE startPolarAlignStage;
        
        // check to see if 3 stars have been selected; adjust polarAlignStage as needed
        if (polarAlign1.ra.rad == 0. && polarAlign1.dec.rad == 0.
                || polarAlign2.ra.rad == 0. && polarAlign2.dec.rad == 0.
                || polarAlign3.ra.rad == 0. && polarAlign3.dec.rad == 0.)
            polarAlignStage = POLAR_ALIGN_STAGE.load3Stars;
        else
            if (polarAlignStage == POLAR_ALIGN_STAGE.load3Stars)
                polarAlignStage = POLAR_ALIGN_STAGE.gotoStar1;
        
        // so as to avoid executing the next conditional when polarAlignStage is advanced
        startPolarAlignStage = polarAlignStage;
        
        // this stage slews to star #1
        if (startPolarAlignStage == POLAR_ALIGN_STAGE.gotoStar1) {
            star1.copy(polarAlign1);
            star2.copy(polarAlign2);
            star3.copy(polarAlign3);
            
            s = c.buildInitString()
            + star1.stringObjName()
            + ": "
                    + star1.buildString()
                    + "\n"
                    + star2.stringObjName()
                    + ": "
                    + star2.buildString()
                    + "\n"
                    + star3.stringObjName()
                    + ": "
                    + star3.buildString()
                    + "\n\n\nmoving to star1...";
            writePolarAlignFile(s);
            
            in.copy(star1);
            target.copy(in);
            
            polarAlignStage = polarAlignStage.next();
        }
        
        // assuming that user has centered star #1, reset to its equat coord and slew to star #2;
        // resets altaz coord - ok for equat aligned mount
        else if (startPolarAlignStage == POLAR_ALIGN_STAGE.resetToStar1GotoStar2) {
            // in should also contain star1 equat coordinates
            cfg.getInstance().current.copy(star1);
            resetToCurrentEquatCoord();
            
            s = "reset to "
                    + cfg.getInstance().current.stringObjName()
                    + ": "
                    + cfg.getInstance().current.buildString()
                    + "\nmoving to star2";
            writePolarAlignFile(s);
            
            in.copy(star2);
            target.copy(in);
            
            polarAlignStage = polarAlignStage.next();
        }
        
        // assuming that user has centered star #2, calc polar axis offset, calc star #3 offset, goto corrected star #3 position
        else if (startPolarAlignStage == POLAR_ALIGN_STAGE.calcPolarOffsetGotoRevisedStar3) {
            // save centered on star #2 coordinates
            apparentStar2.copy(cfg.getInstance().current);
            s = "centered on "
                    + apparentStar2.stringObjName()
                    + ": "
                    + apparentStar2.buildString()
                    + "\n";
            writePolarAlignFile(s);
            
            // have all info to calculate polar axis offset;
            // equations independent of sidereal time, local longitude, latitude;
            
            // fix star #1 position if across the ra 0/24 line
            if (star2.ra.rad - star1.ra.rad > units.HALF_REV || star3.ra.rad - star1.ra.rad > units.HALF_REV)
                fixedStar1.ra.rad = star1.ra.rad + units.ONE_REV;
            else
                fixedStar1.ra.rad = star1.ra.rad;
            
            // fix star #2 position if across the ra 0/24 line
            if (star1.ra.rad - star2.ra.rad  > units.HALF_REV || star3.ra.rad - star2.ra.rad > units.HALF_REV)
                fixedStar2.ra.rad = star2.ra.rad + units.ONE_REV;
            else
                fixedStar2.ra.rad = star2.ra.rad;
            
            // fix star #3 position if across the ra 0/24 line
            if (star1.ra.rad - star3.ra.rad  > units.HALF_REV || star2.ra.rad - star3.ra.rad > units.HALF_REV)
                fixedStar3.ra.rad = star3.ra.rad + units.ONE_REV;
            else
                fixedStar3.ra.rad = star3.ra.rad;
            
            // fix apparent star #2 position if across the ra 0/24 line
            if (fixedStar2.ra.rad - apparentStar2.ra.rad  > units.HALF_REV)
                fixedApparentStar2.ra.rad = apparentStar2.ra.rad + units.ONE_REV;
            else
                fixedApparentStar2.ra.rad = apparentStar2.ra.rad;
            
            sin1 = Math.sin(fixedStar1.ra.rad);
            sin2 = Math.sin(fixedStar2.ra.rad);
            sin3 = Math.sin(fixedStar3.ra.rad);
            cos1 = Math.cos(fixedStar1.ra.rad);
            cos2 = Math.cos(fixedStar2.ra.rad);
            cos3 = Math.cos(fixedStar3.ra.rad);
            tan1 = Math.tan(star1.dec.rad);
            tan2 = Math.tan(star2.dec.rad);
            tan3 = Math.tan(star3.dec.rad);
            
            errorStar2.dec.rad = apparentStar2.dec.rad - star2.dec.rad;
            errorStar2.ra.rad = fixedApparentStar2.ra.rad - fixedStar2.ra.rad;
            
            d = (tan1 + tan2) * (1 - Math.cos((fixedStar1.ra.rad - fixedStar2.ra.rad)));
            
            polarAxisError.dec.rad = (errorStar2.ra.rad*(sin2-sin1) - errorStar2.dec.rad*(tan1*cos1 - tan2*cos2))/d;
            polarAxisError.ra.rad = (errorStar2.ra.rad*(cos1-cos2) + errorStar2.dec.rad*(tan2*sin2 - tan1*sin1))/d;
            
            // Star 3 coordinate offset
            offsetStar3.ra.rad = polarAxisError.dec.rad*(tan3*sin3 - tan1*sin1) + polarAxisError.ra.rad*(tan1*cos1 - tan3*cos3);
            offsetStar3.dec.rad = polarAxisError.dec.rad*(cos3 - cos1) + polarAxisError.ra.rad*(sin3 - sin1);
            
            correctedStar3.ra.rad = star3.ra.rad - offsetStar3.ra.rad;
            correctedStar3.dec.rad = star3.dec.rad - offsetStar3.dec.rad;
            
            s = errorStar2.stringObjName()
            + ": "
                    + errorStar2.buildString()
                    + "\n"
                    + polarAxisError.stringObjName()
                    + ": "
                    + polarAxisError.buildString()
                    + "\n"
                    + offsetStar3.stringObjName()
                    + ": "
                    + offsetStar3.buildString()
                    + "\n"
                    + correctedStar3.stringObjName()
                    + ": "
                    + correctedStar3.buildString()
                    + "\n";
            writePolarAlignFile(s);
            
            // goto corrected star3 position
            in.copy(correctedStar3);
            target.copy(in);
            
            polarAlignStage = polarAlignStage.next();
        }
        
        // assumes that user has centered corrected star #3
        else if (startPolarAlignStage == POLAR_ALIGN_STAGE.adjustBasedOnStar3) {
            // reset to original star #3 position
            cfg.getInstance().current.copy(star3);
            resetToCurrentEquatCoord();
            
            s = "returning to star1";
            writePolarAlignFile(s);
            
            in.copy(star1);
            target.copy(in);
            
            polarAlignStage = POLAR_ALIGN_STAGE.gotoStar1;
        }
    }
    
    boolean writePolarAlignFile(String s) {
        PrintStream output;
        String filename = eString.PGM_NAME + eString.POLARALIGN_LOG_EXT;
        
        try {
            // open for append
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(filename, true)));
            output.println("polar alignment procedure stage is " + polarAlignStage);
            output.println(s);
            output.close();
            return true;
        } catch (IOException ioe) {
            console.errOut("could not open "
                    + filename
                    + " for append in writePolarAlignFile()");
            return false;
        }
    }
    
    /**
     * set cfg.getInstance().current equatorial coordinates, then call this function to set new altazimuth and equatorial coordinates;
     * do not change meridianFlip status;
     * contrast with setCurrentAltazGetEquatCopyTarget() where current altaz is set from servo position, then current equat is calculated
     */
    void resetToCurrentEquatCoord() {
        // only call conversion routine to altaz coord if initalized
        if (cfg.getInstance().initialized()) {
            c.getAltaz();
            // encoders are reset in this method
            resetToCurrentAltazCoord();
        }
    }
    
    /**
     * set cfg.getInstance().current altazimuth coordinates, then call this function
     */
    void resetToCurrentAltazCoord() {
        setServoPositionToCurrentAltaz();
        // astroTime.getInstance().calcSidT() called in setServoPositionToCurrentAltaz()
        if (cfg.getInstance().initialized()) {
            c.detectMeridianFlipFromCurrentAltaz();
            getEquatSetTarget();
        }
        // reset encoders
        resetEncodersToScope();
    }
    
    void getEquatSetTarget() {
        c.getEquat();
        target.copy(cfg.getInstance().current);
    }
    
    void copyPosToInAndTargetThenTurnTrackingOn(position p) {
        in.copy(p);
        target.copy(in);
        cfg.getInstance().current.objName = in.objName;
        turnTrackingOn();
    }
    
    /**
     * builds a spiral search pattern and loads it into the cmdCol.cmdScopeList for execution
     */
    void executeSpiralSearch() {
        spiralSearchCmdScopeList spiralSearchCmdScopeList;
        
        spiralSearchCmdScopeList = new spiralSearchCmdScopeList(c);
        spiralSearchCmdScopeList.buildList();
        spiralSearchCmdScopeList.cmdScopeList.t = this;
        cmdCol.cmdScopeList = spiralSearchCmdScopeList.cmdScopeList;
    }
    
    void stopSpiralSearch() {
        if (cmdCol.cmdScopeList != null) {
            cmdCol.cmdScopeList.resetVars();
            copyPosToInAndTargetThenTurnTrackingOn(cfg.getInstance().current);
        }
    }
    
    /**
     * this ensures that the other xbit settings are preserved unchanged
     */
    void setSiTechGuideMode(boolean setting) {
        pauseSequencer = true;
        // wait for loop to run its course
        common.threadSleep((long) (2000 * cfg.getInstance().moveToTargetTimeSec));
        extNOPSwitchToAsciiMode();
        // clears out return buffer; includes an opening delay
        SiTechAsciiCmd.xmtReturnGetResults();
        common.threadSleep(cfg.getInstance().servoPortWaitTimeMilliSecs);
        SiTechAsciiCmd.setGuideMode(setting);
        SiTechGuideActive = setting;
        if (SiTechGuideActive) {
            cfg.getInstance().spa.startPosErrRad = cfg.getInstance().spa.posErrRad;
            cfg.getInstance().spz.startPosErrRad = cfg.getInstance().spz.posErrRad;
        }
        pauseSequencer = false;
    }
    
    /**
     * send alt to the controller after an init2 or 3
     */
    void setSiTechAlt() {
        if (cfg.getInstance().controllerManufacturer==CONTROLLER_MANUFACTURER.SiTech) {
            pauseSequencer = true;
            // wait for loop to run its course
            common.threadSleep((long) (2000 * cfg.getInstance().moveToTargetTimeSec));
            extNOPSwitchToAsciiMode();
            // clears out return buffer; includes an opening delay
            SiTechAsciiCmd.xmtReturnGetResults();
            common.threadSleep(cfg.getInstance().servoPortWaitTimeMilliSecs);
            SiTechAsciiCmd.setAlt(cfg.getInstance().current.alt.rad);
            pauseSequencer = false;
        }
    }
    
    
    /**
     * encoders vs scope:
     * to reset encoders to current scope (cfg.getInstance().current.alt.rad and cfg.getInstance().current.az.rad):
     *    call ef.E.setNewOffsetsToBeCalculated(true) which will calculate new offsets and new encoder coordinates
     *       (does not change encoder counts)
     * to reset current scope to encoders:
     *    set cfg.getInstance().current.alt.rad and cfg.getInstance().current.az.rad to ef.E.convDecRad and ef.E.convRaRad
     *    then call routines to reset current: do not call resetToCurrentAltazCoord() as it calls reset encoders to scope
     */
    
    boolean resetScopeToEncoders() {
        if (ef.E != null && ef.E.portOpened()) {
            console.stdOutLn("resetting scope to encoder coordinates");
            
            ef.E.addEncoderReset(ENCODER_RESET_TYPE.scopeToEncoders,
                    cfg.getInstance().current.alt.rad,
                    cfg.getInstance().current.az.rad,
                    ef.E.getAltDecRad(),
                    ef.E.getAzRaRad(),
                    astroTime.getInstance().sidT.rad);
            
            cfg.getInstance().current.alt.rad = ef.E.getAltDecRad();
            cfg.getInstance().current.az.rad = ef.E.getAzRaRad();
            
            setServoPositionToCurrentAltaz();
            // astroTime.getInstance().calcSidT() called in setServoPositionToCurrentAltaz()
            if (cfg.getInstance().initialized()) {
                c.detectMeridianFlipFromCurrentAltaz();
                c.getEquat();
                
                if (cfg.getInstance().resetScopeToEncodersTrackOffResetTarget
                        && !cfg.getInstance().spa.track
                        && !cfg.getInstance().spz.track)
                    target.copy(cfg.getInstance().current);
                
                if (cfg.getInstance().resetScopeToEncodersTrackingResetTarget
                        && cfg.getInstance().spa.track
                        && cfg.getInstance().spz.track
                        && !slewing)
                    target.copy(cfg.getInstance().current);
                
                if (cfg.getInstance().resetScopeToEncodersSlewingResetTarget
                        && cfg.getInstance().spa.track
                        && cfg.getInstance().spz.track
                        && slewing)
                    target.copy(cfg.getInstance().current);
                
                return true;
            }
        }
        return false;
    }
    
    boolean resetEncodersToScope() {
        if (ef.E != null && ef.E.portOpened()) {
            console.stdOutLn("resetting encoder to scope coordinates");
            
            ef.E.addEncoderReset(ENCODER_RESET_TYPE.scopeToEncoders,
                    cfg.getInstance().current.alt.rad,
                    cfg.getInstance().current.az.rad,
                    ef.E.getAltDecRad(),
                    ef.E.getAzRaRad(),
                    astroTime.getInstance().sidT.rad);
            
            ef.E.setNewOffsetsToBeCalculated(true);
            return true;
        }
        return false;
    }
    
    boolean checkEncoderThreshold() {
        azDouble diff = new azDouble();
        azDouble threshold = new azDouble();
        
        if (ef.E != null && ef.E.portOpened() && ef.E.getQueryAndReadSuccess()) {
            diff.a = Math.abs(eMath.validRadPi(cfg.getInstance().current.alt.rad - ef.E.getAltDecRad()));
            diff.z = Math.abs(eMath.validRadPi(cfg.getInstance().current.az.rad - ef.E.getAzRaRad()));
            
            // allow for some lag caused by scope's movement during query and read
            threshold.a = cfg.getInstance().encoderErrorThresholdDeg*units.DEG_TO_RAD
                    + Math.abs(cfg.getInstance().spa.avgVelRadSec*encoderBase.UPDATE_ENCODERS_TIME_SEC);
            
            threshold.z = cfg.getInstance().encoderErrorThresholdDeg*units.DEG_TO_RAD
                    + Math.abs(cfg.getInstance().spz.avgVelRadSec*encoderBase.UPDATE_ENCODERS_TIME_SEC);
            
            if (diff.a > threshold.a) {
                console.stdOutLn("scope vs encoder altitude difference of "
                        + diff.a*units.RAD_TO_DEG
                        + " deg exceeds encoder error threshold of "
                        + threshold.a*units.RAD_TO_DEG);
                return resetScopeToEncoders();
            }
            if (diff.z > threshold.z) {
                console.stdOutLn("scope vs encoder azimuth difference of "
                        + diff.z*units.RAD_TO_DEG
                        + " deg exceeds encoder error threshold of "
                        + threshold.z*units.RAD_TO_DEG);
                return resetScopeToEncoders();
            }
        }
        return false;
    }
    
    String buildHandpadModeString() {
        if (cfg.getInstance().handpadPresent)
            handpadModeString = "\nhandpadmode="
                    + cfg.getInstance().handpadMode
                    + "   handpadFRFocusOn="
                    + handpadFRFocusOn
                    + "   guideActive="
                    + guideActive;
        else
            handpadModeString = "\nhandpad not present";
        return handpadModeString;
    }
    
    void displayHandpadModeString() {
        console.stdOutLn(buildHandpadModeString());
    }
    
    String buildDriftString() {
        driftString = "\ndrift rates: Ra = "
                + eString.doubleToStringNoGrouping(driftRaHr.rad*units.RAD_TO_DEG, 2, 2)
                + " deg/hr,"
                + "   Dec = "
                + eString.doubleToStringNoGrouping(driftDecHr.rad*units.RAD_TO_DEG, 2, 2)
                + " deg/hr,"
                + "   Alt = "
                + eString.doubleToStringNoGrouping(cfg.getInstance().spa.driftRadMin * units.RAD_TO_DEG, 2, 2)
                + " deg/min,"
                + "   Az = "
                + eString.doubleToStringNoGrouping(cfg.getInstance().spz.driftRadMin * units.RAD_TO_DEG, 2, 2)
                + " deg/min"
                + "\naccumulated drift: Alt = "
                + eString.doubleToStringNoGrouping(cfg.getInstance().spa.accumDriftRad * units.RAD_TO_DEG, 2, 2)
                + " deg,"
                + "   Az = "
                + eString.doubleToStringNoGrouping(cfg.getInstance().spz.accumDriftRad * units.RAD_TO_DEG, 2, 2)
                + " deg\n";
        
        return driftString;
    }
    
    String addToAAECString(axisToAxisEC aaec) {
        return aaec.axisToAxisName
                + " correction is "
                + -aaec.ecRad * units.RAD_TO_ARCSEC
                + " arcsec\n";
    }
    
    String buildAxisToAxisCorrectionString(int id) {
        aaecString = "";
        axisToAxisEC aaec;
        Iterator it;
        
        it = laaec.iterator();
        while (it.hasNext()) {
            aaec = (axisToAxisEC) it.next();
            if (aaec.active) {
                if (id == SERVO_ID.altDec.KEY)
                    if (aaec.axisToAxisName == AXIS_TO_AXIS_EC.altAlt || aaec.axisToAxisName == AXIS_TO_AXIS_EC.altAz)
                        aaecString += addToAAECString(aaec);
                if (id == SERVO_ID.azRa.KEY)
                    if (aaec.axisToAxisName == AXIS_TO_AXIS_EC.azAz)
                        aaecString += addToAAECString(aaec);
            }
        }
        if (aaecString.length() == 0)
            aaecString = "no axis to axis correction";
        aaecString += "\n";
        return aaecString;
    }
    
    String buildPMCString(int id) {
        PMCString = "";
        
        if (PMC.active) {
            PMCString += "\n";
            if (id == SERVO_ID.altDec.KEY)
                PMCString += SERVO_ID.matchKey(id)
                + " Pointing Model correction = "
                        + -PMC.correction.a*units.RAD_TO_ARCSEC
                        + " arcsec";
            if (id == SERVO_ID.azRa.KEY)
                PMCString += SERVO_ID.matchKey(id)
                + " Pointing Model correction = "
                        + -PMC.correction.z*units.RAD_TO_ARCSEC
                        + " arcsec";
            PMCString += "\n";
        } else
            PMCString = "no PMC correction";
        return PMCString;
    }
    
    void writeCurrEquatCoordHTMLFile() {
        String contents;
        
        contents = "RA   "
                + cfg.getInstance().current.ra.getStringHMS()
                + "\nDec "
                + cfg.getInstance().current.dec.getStringDMS()
                + "\n\n";
        html.writeHTMLFile(eString.CURR_EQUAT_COORD_HTML_PAGE, eString.CURR_EQUAT_COORD_STYLE_SHEET, html.updateCurrEquatCoordTimeSec,
                "current Equatorial Coordinates", contents);
    }
    
    void writeStatusHTMLFile() {
        String contents;
        
        contents = astroTime.getInstance().buildStringCurrentDateTime()
        + "   JD "
                + astroTime.getInstance().JD
                + "\n"
                + "current Equatorial Coordinates are   RA "
                + cfg.getInstance().current.ra.getStringHMS()
                + "   DEC "
                + cfg.getInstance().current.dec.getStringDMS()
                + "\n\n"
                + cfg.getInstance().current.stringPosName()
                + ": "
                + cfg.getInstance().current.buildString()
                + "\n"
                + cfg.getInstance().current.showStringCorrections()
                + "\n"
                + target.stringPosName()
                + ": "
                + target.buildString()
                + "\n"
                + target.showStringCorrections()
                + "\n"
                + in.stringPosName()
                + ": "
                + in.buildString()
                + "\n"
                + savedIn.stringPosName()
                + ": "
                + savedIn.buildString()
                + "\n"
                + autoInit1.stringPosName()
                + ": "
                + autoInit1.buildString()
                + "\n"
                + autoInit2.stringPosName()
                + ": "
                + autoInit2.buildString()
                + "\n"
                + polarAlign1.stringPosName()
                + ": "
                + polarAlign1.buildString()
                + "\n"
                + polarAlign2.stringPosName()
                + ": "
                + polarAlign2.buildString()
                + "\n"
                + polarAlign3.stringPosName()
                + ": "
                + polarAlign3.buildString()
                + "\n\n";
        
        if (ef.E != null && ef.E.portOpened()) {
            contents += "encoders query/read state: "
                    + ef.E.getQueryAndReadSuccess()
                    + "\n"
                    + ef.E.buildCountsString()
                    + "   "
                    + ef.E.buildPositionsString()
                    + "\n";
        } else
            contents += "no encoders\n";
        
        contents += buildDriftString();
        
        if (cfg.getInstance().initialized()) {
            if (cfg.getInstance().Mount.meridianFlipPossible() && cfg.getInstance().Mount.meridianFlip().required)
                contents += c.buildMeridianFlipStatusString();
            contents += c.buildAdditionalConvertVarsString();
        }
        contents += c.buildInitString()
        + "\n"
                + cmdHistory.getInstance().buildMostRecentString(10);
        if (cfg.getInstance().LX200Control && cmdCol.LX200.portOpened) {
            contents += "\n" + cmdCol.LX200.buildLogString(50);
            contents += cmdCol.LX200.buildMostRecentString(500);
        }
        
        html.writeHTMLFile(eString.STATUS_HTML_PAGE, eString.STATUS_STYLE_SHEET, html.refreshStatusTimeSec, "Status", contents);
    }
    
    void writeServoStatusHTMLFile() {
        int id;
        int tableCols;
        int colWidthPercent;
        String contents;
        
        contents = buildHandpadModeString()
        + "   "
                + buildHandpadStatusString()
                + lscr.buildMostRecentString(10)
                + io.buildMostRecentString(100);
        
        tableCols = 0;
        for (id = 0; id < SERVO_ID.size(); id++)
            if (cfg.getInstance().servoParm[id].controllerActive)
                tableCols++;
        if (tableCols == 0)
            colWidthPercent = 100;
        else
            colWidthPercent = 100/tableCols;
        
        contents += "<TABLE WIDTH=100% BORDER=1 CELLPADDING=4 CELLSPACING=3 STYLE=\"page-break-before: always\">\n<TR VALIGN=TOP>\n";
        
        for (id = 0; id < SERVO_ID.size(); id++)
            if (cfg.getInstance().servoParm[id].controllerActive) {
            contents += "<td WIDTH="
                    + colWidthPercent
                    + "%><PRE>\n"
                    + buildStatusReturnString(id)
                    + buildSimCurrentPosVelString(id)
                    + buildStickyBitsString(id)
                    + buildDisplayMoveString(id)
                    + cfg.getInstance().servoParm[id].taa.buildMostRecentTrackErrorString(10)
                    + buildGuidePECString(id)
                    + buildBacklashString(id)
                    + buildAxisToAxisCorrectionString(id)
                    + buildPMCString(id)
                    + "</PRE></td>";
            }
        
        contents += "</TR>\n"
                + "</TABLE>\n";
        
        html.writeHTMLFile(eString.SERVO_STATUS_HTML_PAGE, eString.STATUS_STYLE_SHEET, html.refreshStatusTimeSec, "ServoStatus", contents);
    }
}

