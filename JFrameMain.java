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
 *user interface's main frame;
 *track reference can be passed in at any time;
 *coded by inspecting NetBeans' GUI editing results;
 */
public class JFrameMain extends javax.swing.JFrame {
    private track t;
    private execStringCmdScope esc;
    private cmdFileChooser cfc;
    private dataFileChooser dtc;

    private buttonsOffMediator motionButtonsOffMediator;
    private buttonsOffMediator stopButtonsOffMediator;

    private atAGlanceActionListener atAGlanceActionListener;
    private initializationFieldsActionListener initializationFieldsActionListener;
    private controllerActionListener controllerActionListener;
    private mainEncodersActionListener mainEncodersActionListener;

    private grandTourListMethods grandTourListMethods;
    private cmdScopeListMethods cmdScopeListMethods;
    private updateTextFieldMediator JFrameGrandTourTextFieldMediator;
    private updateTextFieldMediator JFrameCmdScopeTextFieldMediator;
    private JFrameGrandTour JFrameGrandTour;
    private JFrameCmdScope JFrameCmdScope;

    private JFrameTerminal JFrameTerminalMotors;
    private JFrameTerminalEncoders JFrameTerminalEncoders;
    private handpadStatusTimer handpadStatusTimer;
    private JFrameSiTech JFrameSiTech;

    private statusFrameCollection statusFrameCollection;

    private JFrameGetCoordinate JFrameGetCoordinate;
    private jPanelHandpad jPanelHandpad;

    private JPanelColor jPanelMain;
    private JPanelColor jPanelController;
    private JPanelColor jPanelMainCmds;
    private JPanelColor jPanelMainGoto;
    private JPanelColor jPanelMainInitialization;
    private JPanelColor jPanelMainStartup;
    private JPanelColor jPanelAtAGlance;
    private JPanelColor jPanelCmdsCmdFile;
    private JPanelColor jPanelCmdsCmdScope;
    private JPanelColor jPanelCmdsGrandTour;
    private JPanelColor jPanelControllerDir;
    private JPanelColor jPanelControllerSteps;
    private JPanelColor jPanelControllerType;
    private JPanelColor jPanelGotoNearestObject;
    private JPanelColor jPanelGotoSpiralSearch;
    private JPanelColor jPanelInitializationAdvanced;
    private JPanelColor jPanelInitializationInit12;
    private JPanelColor jPanelInitializationInstructions;
    private JPanelColor jPanelInitializationStartInitState;
    private JPanelColor jPanelStartupSetCoordinates;
    private JPanelColor jPanelStartupSite;
    private JPanelColor jPanelStartupTelescopeMount;
    private JPanelColor jPanelStatus;
    private JPanelColor jPanelTitle;

    private JRadioButtonColor jRadioButtonCheckDirectionAltDecReverse;
    private JRadioButtonColor jRadioButtonCheckDirectionAltDecReverseEncoder;
    private JRadioButtonColor jRadioButtonCheckDirectionAzRaReverse;
    private JRadioButtonColor jRadioButtonCheckDirectionAzRaReverseEncoder;
    private JRadioButtonColor jRadioButtonCheckDirectionFieldRReverse;
    private JRadioButtonColor jRadioButtonCheckDirectionFieldRReverseEncoder;
    private JRadioButtonColor jRadioButtonCheckDirectionFocusReverse;
    private JRadioButtonColor jRadioButtonCheckDirectionFocusReverseEncoder;
    private JRadioButtonColor jRadioButtonSetCoordinatesSetMeridianFlipOff;
    private JRadioButtonColor jRadioButtonSetCoordinatesSetMeridianFlipOn;
    private JRadioButtonColor jRadioButtonStartupAutoMeridianFlip;
    private JRadioButtonColor jRadioButtonStartupCanMoveThruPole;
    private JRadioButtonColor jRadioButtonStartupCanMoveToPole;
    private JRadioButtonColor jRadioButtonStartupMeridianFlipPossible;
    private JRadioButtonColor jRadioButtonStartupMeridianFlipRequired;
    private JRadioButtonColor jRadioButtonStartupPrimaryAxisFullyRotates;

    private JToggleButtonColor jToggleButtonAtAGlanceNewTargetCoord;
    private JToggleButtonColor jToggleButtonAtAGlanceResetCurrentCoord;
    private JToggleButtonColor jToggleButtonAtAGlanceSetAnyCoordinate;
    private JToggleButtonColor jToggleButtonAtAGlanceStop;
    private JToggleButtonColor jToggleButtonAtAGlanceTrack;
    private JToggleButtonColor jToggleButtonCheckDirectionAltDec;
    private JToggleButtonColor jToggleButtonCheckDirectionAzRa;
    private JToggleButtonColor jToggleButtonCheckDirectionFieldR;
    private JToggleButtonColor jToggleButtonCheckDirectionFocus;
    private JToggleButtonColor jToggleButtonCheckDirectionStop;
    private JToggleButtonColor jToggleButtonStatusCmdHistory;
    private JToggleButtonColor jToggleButtonCmdsCmdFile;
    private JToggleButtonColor jToggleButtonCmdsCmdFileCancel;
    private JToggleButtonColor jToggleButtonCmdsCmdFileExecute;
    private JToggleButtonColor jToggleButtonCmdsCmdScopeExecute;
    private JToggleButtonColor jToggleButtonCmdsGrandTour;
    private JToggleButtonColor jToggleButtonCmdsGrandTourNearest;
    private JToggleButtonColor jToggleButtonControllerEncodersReset;
    private JToggleButtonColor jToggleButtonControllerMotorReset;
    private JToggleButtonColor jToggleButtonControllerOpenTerminalWindowEncoders;
    private JToggleButtonColor jToggleButtonControllerOpenTerminalWindowMotors;
    private JToggleButtonColor jToggleButtonControllerSiTechCfg;
    private JToggleButtonColor jToggleButtonGotoHome;
    private JToggleButtonColor jToggleButtonGotoMeridianFlip;
    private JToggleButtonColor jToggleButtonGotoNearestObject;
    private JToggleButtonColor jToggleButtonGotoNearestObjectNotInput;
    private JToggleButtonColor jToggleButtonGotoNewTargetCoordinates;
    private JToggleButtonColor jToggleButtonGotoNextNearestObject;
    private JToggleButtonColor jToggleButtonGotoSpiralSearchBegin;
    private JToggleButtonColor jToggleButtonGotoSpiralSearchEnd;
    private JToggleButtonColor jToggleButtonGotoViewInputHistory;
    private JToggleButtonColor jToggleButtonInitializationFixAltOffset;
    private JToggleButtonColor jToggleButtonInitializationInit1;
    private JToggleButtonColor jToggleButtonInitializationInit2;
    private JToggleButtonColor jToggleButtonInitializationInit3;
    private JToggleButtonColor jToggleButtonInitializationReInit;
    private JToggleButtonColor jToggleButtonInitializationUseClosest;
    private JToggleButtonColor jToggleButtonInitializationViewInitHistory;
    private JToggleButtonColor jToggleButtonMainExit;
    private JToggleButtonColor jToggleButtonMainFileSaveCfg;
    private JToggleButtonColor jToggleButtonStartupResetCurrentCoordinates;
    private JToggleButtonColor jToggleButtonStartupResetEncodersToScope;
    private JToggleButtonColor jToggleButtonStartupResetScopeToEncoders;
    private JToggleButtonColor jToggleButtonStartupResetThresholdTelescopeOK;
    private JToggleButtonColor jToggleButtonStartupSiteOK;
    private JToggleButtonColor jToggleButtonStartupTelescopeOK;
    private JToggleButtonColor jToggleButtonStatusControllerComm;
    private JToggleButtonColor jToggleButtonStatusCoordinates;
    private JToggleButtonColor jToggleButtonStatusFocus;
    private JToggleButtonColor jToggleButtonStatusHandpad;
    private JToggleButtonColor jToggleButtonStatusFR;
    private JToggleButtonColor jToggleButtonStatusInits;
    private JToggleButtonColor jToggleButtonStatusLX200;
    private JToggleButtonColor jToggleButtonStatusServoAltDec;
    private JToggleButtonColor jToggleButtonStatusServoAzRa;
    private JToggleButtonColor jToggleButtonStepRevOK;

    private JComboBoxColor jComboBoxCmdsCmdScope;
    private JComboBoxColor jComboBoxControllerEncoderCommPort;
    private JComboBoxColor jComboBoxControllerMotorCommPort;
    private JComboBoxColor jComboBoxEncodersControllerType;
    private JComboBoxColor jComboBoxInitializationStartInitState;
    private JComboBoxColor jComboBoxMotorControllerType;
    private JComboBoxColor jComboBoxStartupMountType;

    private javax.swing.JLabel jLabeStartuplSetCoordinates;
    private javax.swing.JLabel jLabelAtAGlanceCurrentAltazCoord;
    private javax.swing.JLabel jLabelAtAGlanceCurrentEquatCoord;
    private javax.swing.JLabel jLabelAtAGlanceTargetAltazCoord;
    private javax.swing.JLabel jLabelAtAGlanceTargetEquatCoord;
    private javax.swing.JLabel jLabelCheckDirectionAltDecEncoder;
    private javax.swing.JLabel jLabelCheckDirectionAzRaEncoder;
    private javax.swing.JLabel jLabelCheckDirectionEncoders;
    private javax.swing.JLabel jLabelCheckDirectionFieldREncoder;
    private javax.swing.JLabel jLabelCheckDirectionFocusEncoder;
    private javax.swing.JLabel jLabelCheckDirectionMotors;
    private javax.swing.JLabel jLabelCmdsCmdScope;
    private javax.swing.JLabel jLabelControllerCheckDirectionMovement;
    private javax.swing.JLabel jLabelControllerEncodersType;
    private javax.swing.JLabel jLabelControllerMotorType;
    private javax.swing.JLabel jLabelControllerNum1;
    private javax.swing.JLabel jLabelControllerNum2;
    private javax.swing.JLabel jLabelControllerNum3;
    private javax.swing.JLabel jLabelControllerTypes;
    private javax.swing.JLabel jLabelGotoSpiralSearch;
    private javax.swing.JLabel jLabelGotoSpiralSearchRad;
    private javax.swing.JLabel jLabelGotoSpiralSearchSpeed;
    private javax.swing.JLabel jLabelInitializationAdvanced;
    private javax.swing.JLabel jLabelInitializationInit1;
    private javax.swing.JLabel jLabelInitializationInit2;
    private javax.swing.JLabel jLabelInitializationInit3;
    private javax.swing.JLabel jLabelInitializationInstructions;
    private javax.swing.JLabel jLabelInitializationNewZ1Deg;
    private javax.swing.JLabel jLabelInitializationNewZ2Deg;
    private javax.swing.JLabel jLabelInitializationNewZ3Deg;
    private javax.swing.JLabel jLabelInitializationNum1;
    private javax.swing.JLabel jLabelInitializationNum2;
    private javax.swing.JLabel jLabelInitializationNum3;
    private javax.swing.JLabel jLabelInitializationNum4;
    private javax.swing.JLabel jLabelInitializationStartInitState;
    private javax.swing.JLabel jLabelMainStartupSiteName;
    private javax.swing.JLabel jLabelStartupAutoMeridianFlipFuzzDeg;
    private javax.swing.JLabel jLabelStartupEncodersPosition;
    private javax.swing.JLabel jLabelStartupMounting;
    private javax.swing.JLabel jLabelStartupNum1;
    private javax.swing.JLabel jLabelStartupNum2;
    private javax.swing.JLabel jLabelStartupNum3;
    private javax.swing.JLabel jLabelStartupResetThreshold;
    private javax.swing.JLabel jLabelStartupScopePosition;
    private javax.swing.JLabel jLabelStartupSite;
    private javax.swing.JLabel jLabelStartupSiteLatitude;
    private javax.swing.JLabel jLabelStartupSiteLongitude;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JLabel jLabelStepRev;
    private javax.swing.JLabel jLabelStepRevAltDec;
    private javax.swing.JLabel jLabelStepRevAzRa;
    private javax.swing.JLabel jLabelStepRevEncoders;
    private javax.swing.JLabel jLabelStepRevFieldR;
    private javax.swing.JLabel jLabelStepRevFocus;
    private javax.swing.JLabel jLabelStepRevMotors;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JTabbedPane jTabbedPaneMain;
    private javax.swing.JTextArea jTextAreaCheckDirection;
    private javax.swing.JTextArea jTextAreaInitializationInstructions;
    private javax.swing.JTextField jTextFieldCmdsCmdFileFilename;
    private javax.swing.JTextField jTextFieldCmdsCmdScope;
    private javax.swing.JTextField jTextFieldCmdsGrandTour;
    private javax.swing.JTextField jTextFieldGotoSpiralSearchRad;
    private javax.swing.JTextField jTextFieldGotoSpiralSearchSpeed;
    private javax.swing.JTextField jTextFieldInitializationAltOffset;
    private javax.swing.JTextField jTextFieldInitializationInit1;
    private javax.swing.JTextField jTextFieldInitializationInit2;
    private javax.swing.JTextField jTextFieldInitializationInit3;
    private javax.swing.JTextField jTextFieldInitializationNewZ1Deg;
    private javax.swing.JTextField jTextFieldInitializationNewZ2Deg;
    private javax.swing.JTextField jTextFieldInitializationNewZ3Deg;
    private javax.swing.JTextField jTextFieldStartupAutoMeridianFlipFuzzDeg;
    private javax.swing.JTextField jTextFieldStartupResetThreshold;
    private javax.swing.JTextField jTextFieldStartupSiteLatitude;
    private javax.swing.JTextField jTextFieldStartupSiteLongitude;
    private javax.swing.JTextField jTextFieldStartupSiteName;
    private javax.swing.JTextField jTextFieldStepRevEncoderAltDec;
    private javax.swing.JTextField jTextFieldStepRevEncoderAzRa;
    private javax.swing.JTextField jTextFieldStepRevEncoderFieldR;
    private javax.swing.JTextField jTextFieldStepRevEncoderFocus;
    private javax.swing.JTextField jTextFieldStepRevMotorAltDec;
    private javax.swing.JTextField jTextFieldStepRevMotorAzRa;
    private javax.swing.JTextField jTextFieldStepRevMotorFieldR;
    private javax.swing.JTextField jTextFieldStepRevMotorFocus;

    public JFrameMain() {
        super("Scope II");

        int ix;

        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        initComponents();
        setIconImage((new ImageIcon("ScopeII.gif")).getImage());
        screenPlacement.getInstance().center(this);

        handpadStatusTimer = new handpadStatusTimer();
        statusFrameCollection = new statusFrameCollection();

        atAGlanceActionListener = new atAGlanceActionListener(
        jLabelAtAGlanceCurrentEquatCoord,
        jLabelAtAGlanceTargetEquatCoord,
        jLabelAtAGlanceCurrentAltazCoord,
        jLabelAtAGlanceTargetAltazCoord);
        atAGlanceActionListener.startTimer();

        initializationFieldsActionListener = new initializationFieldsActionListener(
        jTextAreaInitializationInstructions,
        jTextFieldInitializationInit1,
        jTextFieldInitializationInit2,
        jTextFieldInitializationInit3,
        jTextFieldInitializationAltOffset);
        initializationFieldsActionListener.startTimer();

        controllerActionListener = new controllerActionListener(jComboBoxEncodersControllerType);
        controllerActionListener.startTimer();

        mainEncodersActionListener = new mainEncodersActionListener(
        jLabelCheckDirectionAltDecEncoder,
        jLabelCheckDirectionAzRaEncoder,
        jLabelCheckDirectionFieldREncoder,
        jLabelCheckDirectionFocusEncoder,
        jLabelStartupScopePosition,
        jLabelStartupEncodersPosition);
        mainEncodersActionListener.startTimer();

        grandTourListMethods = new grandTourListMethods();
        JFrameGrandTourTextFieldMediator = new updateTextFieldMediator(jTextFieldCmdsGrandTour);
        JFrameGrandTour = new JFrameGrandTour();
        JFrameGrandTour.registerGrandTourListMethods(grandTourListMethods);
        JFrameGrandTour.registerTextFieldMediator(JFrameGrandTourTextFieldMediator);

        cmdScopeListMethods = new cmdScopeListMethods();
        JFrameCmdScopeTextFieldMediator = new updateTextFieldMediator(jTextFieldCmdsCmdFileFilename);
        JFrameCmdScope = new JFrameCmdScope();
        JFrameCmdScope.registerCmdScopeListMethods(cmdScopeListMethods);
        JFrameCmdScope.registerTextFieldMediator(JFrameCmdScopeTextFieldMediator);

        motionButtonsOffMediator = new buttonsOffMediator("handpadAtAGlanceMotionButtons");
        motionButtonsOffMediator.register(jToggleButtonAtAGlanceTrack);
        motionButtonsOffMediator.register(jToggleButtonCheckDirectionAltDec);
        motionButtonsOffMediator.register(jToggleButtonCheckDirectionAzRa);
        motionButtonsOffMediator.register(jToggleButtonCheckDirectionFieldR);
        motionButtonsOffMediator.register(jToggleButtonCheckDirectionFocus);
        jPanelHandpad.addMotionButtonsOffMediator(motionButtonsOffMediator);

        stopButtonsOffMediator = new buttonsOffMediator("handpadAtAGlanceStopButtons");
        stopButtonsOffMediator.register(jToggleButtonAtAGlanceStop);
        stopButtonsOffMediator.register(jToggleButtonCheckDirectionStop);
        jPanelHandpad.addStopButtonsOffMediator(stopButtonsOffMediator);

        jTextFieldStepRevMotorAltDec.setText(eString.doubleToStringNoGrouping(cfg.getInstance().spa.stepsPerRev, 9, 0));
        jTextFieldStepRevMotorAzRa.setText(eString.doubleToStringNoGrouping(cfg.getInstance().spz.stepsPerRev, 9, 0));
        jTextFieldStepRevMotorFieldR.setText(eString.doubleToStringNoGrouping(cfg.getInstance().spr.stepsPerRev, 9, 0));
        jTextFieldStepRevMotorFocus.setText(eString.doubleToStringNoGrouping(cfg.getInstance().spf.stepsPerRev, 9, 0));
        jTextFieldStepRevEncoderAltDec.setText(eString.doubleToStringNoGrouping(cfg.getInstance().encoderAltDecCountsPerRev, 9, 0));
        jTextFieldStepRevEncoderAzRa.setText(eString.doubleToStringNoGrouping(cfg.getInstance().encoderAzRaCountsPerRev, 9, 0));
        if (cfg.getInstance().spa.reverseMotor)
            jRadioButtonCheckDirectionAltDecReverse.setSelected(true);
        if (cfg.getInstance().spz.reverseMotor)
            jRadioButtonCheckDirectionAzRaReverse.setSelected(true);
        if (cfg.getInstance().spr.reverseMotor)
            jRadioButtonCheckDirectionFieldRReverse.setSelected(true);
        if (cfg.getInstance().spf.reverseMotor)
            jRadioButtonCheckDirectionFocusReverse.setSelected(true);
        if (cfg.getInstance().encoderAltDecDir == ROTATION.CCW)
            jRadioButtonCheckDirectionAltDecReverseEncoder.setSelected(true);
        if (cfg.getInstance().encoderAzRaDir == ROTATION.CCW)
            jRadioButtonCheckDirectionAzRaReverseEncoder.setSelected(true);
        if (cfg.getInstance().encoderAzRaDir == ROTATION.CCW)
            jRadioButtonCheckDirectionAzRaReverseEncoder.setSelected(true);
        if (cfg.getInstance().encoderFieldRDir == ROTATION.CCW)
            jRadioButtonCheckDirectionFieldRReverseEncoder.setSelected(true);
        if (cfg.getInstance().encoderFocusDir == ROTATION.CCW)
            jRadioButtonCheckDirectionFocusReverseEncoder.setSelected(true);
        jTextFieldStartupResetThreshold.setText(eString.doubleToStringNoGrouping(cfg.getInstance().encoderErrorThresholdDeg, 3, 2));
        jTextFieldStartupSiteName.setText(cfg.getInstance().siteName);
        jTextFieldStartupSiteLatitude.setText(eString.doubleToStringNoGrouping(cfg.getInstance().latitudeDeg, 3, 2));
        jTextFieldStartupSiteLongitude.setText(eString.doubleToStringNoGrouping(cfg.getInstance().longitudeDeg, 3, 2));
        if (cfg.getInstance().Mount.meridianFlipPossible())
            jTextFieldStartupAutoMeridianFlipFuzzDeg.setText(eString.doubleToStringNoGrouping(cfg.getInstance().Mount.meridianFlip().autoFuzzDeg, 2, 2));
        jTextFieldInitializationNewZ1Deg.setText(eString.doubleToStringNoGrouping(cfg.getInstance().z1Deg, 2, 2));
        jTextFieldInitializationNewZ2Deg.setText(eString.doubleToStringNoGrouping(cfg.getInstance().z2Deg, 2, 2));
        jTextFieldInitializationNewZ3Deg.setText(eString.doubleToStringNoGrouping(cfg.getInstance().z3Deg, 2, 2));
        jTextFieldGotoSpiralSearchRad.setText(eString.doubleToStringNoGrouping(cfg.getInstance().spiralSearchRadiusDeg, 2, 2));
        jTextFieldGotoSpiralSearchSpeed.setText(eString.doubleToStringNoGrouping(cfg.getInstance().spiralSearchSpeedDegSec, 2, 2));

        Enumeration eINIT_STATE = INIT_STATE.elements();
        while (eINIT_STATE.hasMoreElements()) {
            INIT_STATE sis = (INIT_STATE) eINIT_STATE.nextElement();
            jComboBoxInitializationStartInitState.addItem(sis);
        }
        jComboBoxInitializationStartInitState.setSelectedItem(cfg.getInstance().initState);

        Enumeration eMOUNT_TYPE = MOUNT_TYPE.elements();
        while (eMOUNT_TYPE.hasMoreElements()) {
            MOUNT_TYPE mt = (MOUNT_TYPE) eMOUNT_TYPE.nextElement();
            jComboBoxStartupMountType.addItem(mt);
        }
        jComboBoxStartupMountType.setSelectedItem(cfg.getInstance().Mount.mountType());
        mountTypeParmsMediator(cfg.getInstance().Mount);

        Enumeration eCMD_SCOPE = CMD_SCOPE.elements();
        while (eCMD_SCOPE.hasMoreElements()) {
            CMD_SCOPE cs = (CMD_SCOPE) eCMD_SCOPE.nextElement();
            jComboBoxCmdsCmdScope.addItem(cs);
        }

        Enumeration eCONTROLLER_MANUFACTURER = CONTROLLER_MANUFACTURER.elements();
        while (eCONTROLLER_MANUFACTURER.hasMoreElements()) {
            CONTROLLER_MANUFACTURER cm = (CONTROLLER_MANUFACTURER) eCONTROLLER_MANUFACTURER.nextElement();
            jComboBoxMotorControllerType.addItem(cm);
        }
        jComboBoxMotorControllerType.setSelectedItem(cfg.getInstance().controllerManufacturer);

        Enumeration eENCODER_TYPE = ENCODER_TYPE.elements();
        while (eENCODER_TYPE.hasMoreElements()) {
            ENCODER_TYPE et = (ENCODER_TYPE) eENCODER_TYPE.nextElement();
            jComboBoxEncodersControllerType.addItem(et);
        }
        // not jComboBoxEncodersControllerType.setSelectedItem(cfg.getInstance().encoderType);
        // as encoderType in cfg is abbreviated, and encoder object likely not yet built

        for (ix = 0; ix < ioSerial.commPortList.size(); ix++) {
            jComboBoxControllerMotorCommPort.addItem(ioSerial.commPortList.get(ix));
            jComboBoxControllerEncoderCommPort.addItem(ioSerial.commPortList.get(ix));
        }
        jComboBoxControllerMotorCommPort.setSelectedItem(cfg.getInstance().servoSerialPortName);
        jComboBoxControllerEncoderCommPort.setSelectedItem(cfg.getInstance().encoderSerialPortName);
    }

    // so as to run from swing's thread
    void run(boolean startCmd, String cmdFilename) {
        trackBuilder tb = new trackBuilder(this);
        tb.build();

        launchThreadLoadDataFileList();

        if (startCmd)
            tb.t.cmdCol.cmdScopeList.parseCmdFromFile(cmdFilename);
        tb.run();
    }

    /**
     * loads dataFileList in separate thread as this is a lengthy process
     */
    void launchThreadLoadDataFileList() {
        Thread longWork = new Thread() {
            public void run() {
                console.stdOutLn("loading dataFiles from: " + cfg.getInstance().datFileLocation);
                dataFileListPositions.getInstance().loadFromDir(cfg.getInstance().datFileLocation);
            }
        };
        longWork.start();
    }

    void registerTrackReference(track t) {
        this.t = t;
        jPanelHandpad.registerTrackReference(t);
        handpadStatusTimer.registerTrackReference(t);
        statusFrameCollection.registerTrackReference(t);
        atAGlanceActionListener.registerTrackReference(t);
        initializationFieldsActionListener.registerTrackReference(t);
        controllerActionListener.registerTrackReference(t);
        mainEncodersActionListener.registerTrackReference(t);
        grandTourListMethods.registerTrackReference(t);
        cmdScopeListMethods.registerTrackReference(t);
        JFrameGrandTour.registerTrackReference(t);
        JFrameCmdScope.registerTrackReference(t);
    }

    private void initComponents() {
        jPanelHandpad = new jPanelHandpad();

        (jPanelMain = new JPanelColor()).darkColor();
        (jPanelMainInitialization = new JPanelColor()).mediumColor();
        (jPanelMainCmds = new JPanelColor()).mediumColor();
        (jPanelController = new JPanelColor()).mediumColor();
        (jPanelMainGoto = new JPanelColor()).mediumColor();
        (jPanelMainStartup = new JPanelColor()).mediumColor();
        (jPanelAtAGlance = new JPanelColor()).lightColor();
        (jPanelCmdsCmdFile = new JPanelColor()).lightColor();
        (jPanelCmdsCmdScope = new JPanelColor()).lightColor();
        (jPanelCmdsGrandTour = new JPanelColor()).lightColor();
        (jPanelControllerDir = new JPanelColor()).lightColor();
        (jPanelControllerSteps = new JPanelColor()).lightColor();
        (jPanelControllerType = new JPanelColor()).lightColor();
        (jPanelGotoNearestObject = new JPanelColor()).lightColor();
        (jPanelGotoSpiralSearch = new JPanelColor()).lightColor();
        (jPanelInitializationAdvanced = new JPanelColor()).lightColor();
        (jPanelInitializationInit12 = new JPanelColor()).lightColor();
        (jPanelInitializationStartInitState = new JPanelColor()).lightColor();
        (jPanelInitializationInstructions = new JPanelColor()).lightColor();
        (jPanelStartupSite = new JPanelColor()).lightColor();
        (jPanelStartupTelescopeMount = new JPanelColor()).lightColor();
        (jPanelStartupSetCoordinates = new JPanelColor()).lightColor();
        (jPanelStatus = new JPanelColor()).lightColor();
        (jPanelTitle = new JPanelColor()).lightColor();

        jRadioButtonCheckDirectionAltDecReverse = new JRadioButtonColor();
        jRadioButtonCheckDirectionAltDecReverseEncoder = new JRadioButtonColor();
        jRadioButtonCheckDirectionAzRaReverse = new JRadioButtonColor();
        jRadioButtonCheckDirectionAzRaReverseEncoder = new JRadioButtonColor();
        jRadioButtonCheckDirectionFieldRReverse = new JRadioButtonColor();
        jRadioButtonCheckDirectionFieldRReverseEncoder = new JRadioButtonColor();
        jRadioButtonCheckDirectionFocusReverse = new JRadioButtonColor();
        jRadioButtonCheckDirectionFocusReverseEncoder = new JRadioButtonColor();
        jRadioButtonSetCoordinatesSetMeridianFlipOff = new JRadioButtonColor();
        jRadioButtonSetCoordinatesSetMeridianFlipOn = new JRadioButtonColor();
        jRadioButtonStartupAutoMeridianFlip = new JRadioButtonColor();
        jRadioButtonStartupCanMoveThruPole = new JRadioButtonColor();
        jRadioButtonStartupCanMoveToPole = new JRadioButtonColor();
        jRadioButtonStartupMeridianFlipPossible = new JRadioButtonColor();
        jRadioButtonStartupMeridianFlipRequired = new JRadioButtonColor();
        jRadioButtonStartupPrimaryAxisFullyRotates = new JRadioButtonColor();

        (jToggleButtonAtAGlanceStop = new JToggleButtonColor()).stopColor();
        (jToggleButtonCheckDirectionStop = new JToggleButtonColor()).stopColor();
        (jToggleButtonAtAGlanceTrack = new JToggleButtonColor()).goColor();
        jToggleButtonAtAGlanceNewTargetCoord = new JToggleButtonColor();
        jToggleButtonAtAGlanceResetCurrentCoord = new JToggleButtonColor();
        jToggleButtonAtAGlanceSetAnyCoordinate = new JToggleButtonColor();
        jToggleButtonCheckDirectionAltDec = new JToggleButtonColor();
        jToggleButtonCheckDirectionAzRa = new JToggleButtonColor();
        jToggleButtonCheckDirectionFieldR = new JToggleButtonColor();
        jToggleButtonCheckDirectionFocus = new JToggleButtonColor();
        jToggleButtonStatusCmdHistory = new JToggleButtonColor();
        jToggleButtonCmdsCmdFile = new JToggleButtonColor();
        jToggleButtonCmdsCmdFileCancel = new JToggleButtonColor();
        jToggleButtonCmdsCmdFileExecute = new JToggleButtonColor();
        jToggleButtonCmdsCmdScopeExecute = new JToggleButtonColor();
        jToggleButtonCmdsGrandTour = new JToggleButtonColor();
        jToggleButtonCmdsGrandTourNearest = new JToggleButtonColor();
        jToggleButtonControllerEncodersReset = new JToggleButtonColor();
        jToggleButtonControllerMotorReset = new JToggleButtonColor();
        jToggleButtonControllerOpenTerminalWindowEncoders = new JToggleButtonColor();
        jToggleButtonControllerOpenTerminalWindowMotors = new JToggleButtonColor();
        jToggleButtonControllerSiTechCfg = new JToggleButtonColor();
        jToggleButtonGotoHome = new JToggleButtonColor();
        jToggleButtonGotoMeridianFlip = new JToggleButtonColor();
        jToggleButtonGotoNearestObject = new JToggleButtonColor();
        jToggleButtonGotoNearestObjectNotInput = new JToggleButtonColor();
        jToggleButtonGotoNewTargetCoordinates = new JToggleButtonColor();
        jToggleButtonGotoNextNearestObject = new JToggleButtonColor();
        jToggleButtonGotoSpiralSearchBegin = new JToggleButtonColor();
        jToggleButtonGotoSpiralSearchEnd = new JToggleButtonColor();
        jToggleButtonGotoViewInputHistory = new JToggleButtonColor();
        jToggleButtonInitializationFixAltOffset = new JToggleButtonColor();
        jToggleButtonInitializationInit1 = new JToggleButtonColor();
        jToggleButtonInitializationInit2 = new JToggleButtonColor();
        jToggleButtonInitializationInit3 = new JToggleButtonColor();
        jToggleButtonInitializationReInit = new JToggleButtonColor();
        jToggleButtonInitializationUseClosest = new JToggleButtonColor();
        jToggleButtonInitializationViewInitHistory = new JToggleButtonColor();
        jToggleButtonMainExit = new JToggleButtonColor();
        jToggleButtonMainFileSaveCfg = new JToggleButtonColor();
        jToggleButtonStartupResetCurrentCoordinates = new JToggleButtonColor();
        jToggleButtonStartupResetEncodersToScope = new JToggleButtonColor();
        jToggleButtonStartupResetScopeToEncoders = new JToggleButtonColor();
        jToggleButtonStartupResetThresholdTelescopeOK = new JToggleButtonColor();
        jToggleButtonStartupSiteOK = new JToggleButtonColor();
        jToggleButtonStartupTelescopeOK = new JToggleButtonColor();
        jToggleButtonStatusControllerComm = new JToggleButtonColor();
        jToggleButtonStatusCoordinates = new JToggleButtonColor();
        jToggleButtonStatusFocus = new JToggleButtonColor();
        jToggleButtonStatusFR = new JToggleButtonColor();
        jToggleButtonStatusHandpad = new JToggleButtonColor();
        jToggleButtonStatusInits = new JToggleButtonColor();
        jToggleButtonStatusLX200 = new JToggleButtonColor();
        jToggleButtonStatusServoAltDec = new JToggleButtonColor();
        jToggleButtonStatusServoAzRa = new JToggleButtonColor();
        jToggleButtonStepRevOK = new JToggleButtonColor();

        jComboBoxCmdsCmdScope = new JComboBoxColor();
        jComboBoxControllerEncoderCommPort = new JComboBoxColor();
        jComboBoxControllerMotorCommPort = new JComboBoxColor();
        jComboBoxEncodersControllerType = new JComboBoxColor();
        jComboBoxInitializationStartInitState = new JComboBoxColor();
        jComboBoxMotorControllerType = new JComboBoxColor();
        jComboBoxStartupMountType = new JComboBoxColor();
        jLabelAtAGlanceCurrentAltazCoord = new javax.swing.JLabel();
        jLabelAtAGlanceCurrentEquatCoord = new javax.swing.JLabel();
        jLabelAtAGlanceTargetAltazCoord = new javax.swing.JLabel();
        jLabelAtAGlanceTargetEquatCoord = new javax.swing.JLabel();
        jLabelCheckDirectionAltDecEncoder = new javax.swing.JLabel();
        jLabelCheckDirectionAzRaEncoder = new javax.swing.JLabel();
        jLabelCheckDirectionEncoders = new javax.swing.JLabel();
        jLabelCheckDirectionFieldREncoder = new javax.swing.JLabel();
        jLabelCheckDirectionFocusEncoder = new javax.swing.JLabel();
        jLabelCheckDirectionMotors = new javax.swing.JLabel();
        jLabelCmdsCmdScope = new javax.swing.JLabel();
        jLabelControllerCheckDirectionMovement = new javax.swing.JLabel();
        jLabelControllerEncodersType = new javax.swing.JLabel();
        jLabelControllerMotorType = new javax.swing.JLabel();
        jLabelControllerNum1 = new javax.swing.JLabel();
        jLabelControllerNum2 = new javax.swing.JLabel();
        jLabelControllerNum3 = new javax.swing.JLabel();
        jLabelControllerTypes = new javax.swing.JLabel();
        jLabelGotoSpiralSearch = new javax.swing.JLabel();
        jLabelGotoSpiralSearchRad = new javax.swing.JLabel();
        jLabelGotoSpiralSearchSpeed = new javax.swing.JLabel();
        jLabelInitializationAdvanced = new javax.swing.JLabel();
        jLabelInitializationInit1 = new javax.swing.JLabel();
        jLabelInitializationInit2 = new javax.swing.JLabel();
        jLabelInitializationInit3 = new javax.swing.JLabel();
        jLabelInitializationInstructions = new javax.swing.JLabel();
        jLabelInitializationNewZ1Deg = new javax.swing.JLabel();
        jLabelInitializationNewZ2Deg = new javax.swing.JLabel();
        jLabelInitializationNewZ3Deg = new javax.swing.JLabel();
        jLabelInitializationNum1 = new javax.swing.JLabel();
        jLabelInitializationNum2 = new javax.swing.JLabel();
        jLabelInitializationNum3 = new javax.swing.JLabel();
        jLabelInitializationNum4 = new javax.swing.JLabel();
        jLabelInitializationStartInitState = new javax.swing.JLabel();
        jLabelMainStartupSiteName = new javax.swing.JLabel();
        jLabelStartupAutoMeridianFlipFuzzDeg = new javax.swing.JLabel();
        jLabelStartupEncodersPosition = new javax.swing.JLabel();
        jLabelStartupMounting = new javax.swing.JLabel();
        jLabelStartupNum1 = new javax.swing.JLabel();
        jLabelStartupNum2 = new javax.swing.JLabel();
        jLabelStartupNum3 = new javax.swing.JLabel();
        jLabelStartupResetThreshold = new javax.swing.JLabel();
        jLabelStartupScopePosition = new javax.swing.JLabel();
        jLabelStartupSite = new javax.swing.JLabel();
        jLabelStartupSiteLatitude = new javax.swing.JLabel();
        jLabelStartupSiteLongitude = new javax.swing.JLabel();
        jLabelStatus = new javax.swing.JLabel();
        jLabelStepRev = new javax.swing.JLabel();
        jLabelStepRevAltDec = new javax.swing.JLabel();
        jLabelStepRevAzRa = new javax.swing.JLabel();
        jLabelStepRevEncoders = new javax.swing.JLabel();
        jLabelStepRevFieldR = new javax.swing.JLabel();
        jLabelStepRevFocus = new javax.swing.JLabel();
        jLabelStepRevMotors = new javax.swing.JLabel();
        jLabelTitle = new javax.swing.JLabel();
        jLabeStartuplSetCoordinates = new javax.swing.JLabel();
        jTabbedPaneMain = new javax.swing.JTabbedPane();
        jTextAreaCheckDirection = new javax.swing.JTextArea();
        jTextAreaInitializationInstructions = new javax.swing.JTextArea();
        jTextFieldCmdsCmdFileFilename = new javax.swing.JTextField();
        jTextFieldCmdsCmdScope = new javax.swing.JTextField();
        jTextFieldCmdsGrandTour = new javax.swing.JTextField();
        jTextFieldGotoSpiralSearchRad = new javax.swing.JTextField();
        jTextFieldGotoSpiralSearchSpeed = new javax.swing.JTextField();
        jTextFieldInitializationAltOffset = new javax.swing.JTextField();
        jTextFieldInitializationInit1 = new javax.swing.JTextField();
        jTextFieldInitializationInit2 = new javax.swing.JTextField();
        jTextFieldInitializationInit3 = new javax.swing.JTextField();
        jTextFieldInitializationNewZ1Deg = new javax.swing.JTextField();
        jTextFieldInitializationNewZ2Deg = new javax.swing.JTextField();
        jTextFieldInitializationNewZ3Deg = new javax.swing.JTextField();
        jTextFieldStartupAutoMeridianFlipFuzzDeg = new javax.swing.JTextField();
        jTextFieldStartupResetThreshold = new javax.swing.JTextField();
        jTextFieldStartupSiteLatitude = new javax.swing.JTextField();
        jTextFieldStartupSiteLongitude = new javax.swing.JTextField();
        jTextFieldStartupSiteName = new javax.swing.JTextField();
        jTextFieldStepRevEncoderAltDec = new javax.swing.JTextField();
        jTextFieldStepRevEncoderAzRa = new javax.swing.JTextField();
        jTextFieldStepRevEncoderFieldR = new javax.swing.JTextField();
        jTextFieldStepRevEncoderFocus = new javax.swing.JTextField();
        jTextFieldStepRevMotorAltDec = new javax.swing.JTextField();
        jTextFieldStepRevMotorAzRa = new javax.swing.JTextField();
        jTextFieldStepRevMotorFieldR = new javax.swing.JTextField();
        jTextFieldStepRevMotorFocus = new javax.swing.JTextField();

        getContentPane().setLayout(new AbsoluteLayout());

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jPanelMain.setLayout(new AbsoluteLayout());

        jPanelTitle.setLayout(new AbsoluteLayout());

        jLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitle.setText("Scope II   by Mel Bartels");
        jLabelTitle.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jLabelTitle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelTitle.add(jLabelTitle, new AbsoluteConstraints(200, 7, 370, -1));

        jToggleButtonMainExit.setText("exit");
        jToggleButtonMainExit.setToolTipText("shutdown the program");
        jToggleButtonMainExit.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonMainExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonMainExitActionPerformed(evt);
            }
        });

        jPanelTitle.add(jToggleButtonMainExit, new AbsoluteConstraints(720, 7, 70, -1));

        jToggleButtonMainFileSaveCfg.setText("file save cfg");
        jToggleButtonMainFileSaveCfg.setToolTipText("save the configuration file");
        jToggleButtonMainFileSaveCfg.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonMainFileSaveCfg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonMainFileSaveCfgActionPerformed(evt);
            }
        });

        jPanelTitle.add(jToggleButtonMainFileSaveCfg, new AbsoluteConstraints(10, 7, -1, -1));

        jPanelMain.add(jPanelTitle, new AbsoluteConstraints(0, 0, 800, 30));

        jPanelAtAGlance.setLayout(new AbsoluteLayout());

        jPanelAtAGlance.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelAtAGlanceCurrentEquatCoord.setBackground(new java.awt.Color(255, 255, 255));
        jLabelAtAGlanceCurrentEquatCoord.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelAtAGlanceCurrentEquatCoord.setText("current equatorial coordinates");
        jLabelAtAGlanceCurrentEquatCoord.setToolTipText("telescope is aimed at this equatorial coordinate");
        jLabelAtAGlanceCurrentEquatCoord.setBorder(new javax.swing.border.EtchedBorder());
        jLabelAtAGlanceCurrentEquatCoord.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabelAtAGlanceCurrentEquatCoord.setOpaque(true);
        jPanelAtAGlance.add(jLabelAtAGlanceCurrentEquatCoord, new AbsoluteConstraints(80, 10, 330, -1));

        jToggleButtonAtAGlanceTrack.setText("track");
        jToggleButtonAtAGlanceTrack.setToolTipText("track to target equatorial coordinates");
        jToggleButtonAtAGlanceTrack.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonAtAGlanceTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonAtAGlanceTrackActionPerformed(evt);
            }
        });

        jPanelAtAGlance.add(jToggleButtonAtAGlanceTrack, new AbsoluteConstraints(220, 60, 60, 20));

        jToggleButtonAtAGlanceStop.setText("stop");
        jToggleButtonAtAGlanceStop.setToolTipText("stop all motors");
        jToggleButtonAtAGlanceStop.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonAtAGlanceStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonAtAGlanceStopActionPerformed(evt);
            }
        });

        jPanelAtAGlance.add(jToggleButtonAtAGlanceStop, new AbsoluteConstraints(320, 60, 60, 20));

        jToggleButtonAtAGlanceResetCurrentCoord.setText("reset");
        jToggleButtonAtAGlanceResetCurrentCoord.setToolTipText("reset current coordinates");
        jToggleButtonAtAGlanceResetCurrentCoord.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonAtAGlanceResetCurrentCoord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonAtAGlanceResetCurrentCoordActionPerformed(evt);
            }
        });

        jPanelAtAGlance.add(jToggleButtonAtAGlanceResetCurrentCoord, new AbsoluteConstraints(10, 10, 60, 20));

        jLabelAtAGlanceTargetEquatCoord.setText("target equatorial coordinates");
        jLabelAtAGlanceTargetEquatCoord.setToolTipText("target equatorial coordinate: telescope will slew then track at this coordinate");
        jPanelAtAGlance.add(jLabelAtAGlanceTargetEquatCoord, new AbsoluteConstraints(80, 30, 330, -1));

        jToggleButtonAtAGlanceNewTargetCoord.setText("new");
        jToggleButtonAtAGlanceNewTargetCoord.setToolTipText("change target coordinates");
        jToggleButtonAtAGlanceNewTargetCoord.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonAtAGlanceNewTargetCoord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonAtAGlanceNewTargetCoordActionPerformed(evt);
            }
        });

        jPanelAtAGlance.add(jToggleButtonAtAGlanceNewTargetCoord, new AbsoluteConstraints(10, 30, 60, 20));

        jLabelAtAGlanceTargetAltazCoord.setText("target altazimuth coordinates");
        jLabelAtAGlanceTargetAltazCoord.setToolTipText("target altazimuth coordinate: telescope will slew then track at this coordinate");
        jPanelAtAGlance.add(jLabelAtAGlanceTargetAltazCoord, new AbsoluteConstraints(425, 30, 150, -1));

        jLabelAtAGlanceCurrentAltazCoord.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelAtAGlanceCurrentAltazCoord.setText("current altazimuth coordinates");
        jLabelAtAGlanceCurrentAltazCoord.setToolTipText("telescope is aimed at this altazimuth coordinate");
        jLabelAtAGlanceCurrentAltazCoord.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jPanelAtAGlance.add(jLabelAtAGlanceCurrentAltazCoord, new AbsoluteConstraints(425, 10, 150, -1));

        jToggleButtonAtAGlanceSetAnyCoordinate.setText("set any coordinate");
        jToggleButtonAtAGlanceSetAnyCoordinate.setToolTipText("open 'get coordinate' popup to set a coordinate type:select desired coordinate type when panel opens");
        jToggleButtonAtAGlanceSetAnyCoordinate.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonAtAGlanceSetAnyCoordinate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonAtAGlanceSetAnyCoordinateActionPerformed(evt);
            }
        });

        jPanelAtAGlance.add(jToggleButtonAtAGlanceSetAnyCoordinate, new AbsoluteConstraints(10, 60, 140, -1));

        jPanelMain.add(jPanelAtAGlance, new AbsoluteConstraints(0, 40, 580, 90));

        jTabbedPaneMain.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanelController.setLayout(new AbsoluteLayout());

        jPanelControllerType.setLayout(new AbsoluteLayout());

        jPanelControllerType.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelControllerNum1.setText("1.");
        jPanelControllerType.add(jLabelControllerNum1, new AbsoluteConstraints(10, 10, -1, -1));

        jLabelControllerMotorType.setText("Motors");
        jPanelControllerType.add(jLabelControllerMotorType, new AbsoluteConstraints(30, 35, -1, -1));

        jComboBoxMotorControllerType.setToolTipText("type of motor controller");
        jComboBoxMotorControllerType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxMotorControllerTypeItemStateChanged(evt);
            }
        });

        jPanelControllerType.add(jComboBoxMotorControllerType, new AbsoluteConstraints(80, 30, 170, -1));

        jLabelControllerEncodersType.setText("Encoders");
        jPanelControllerType.add(jLabelControllerEncodersType, new AbsoluteConstraints(10, 65, -1, -1));

        jComboBoxEncodersControllerType.setToolTipText("type of encoder controller");
        jComboBoxEncodersControllerType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxEncodersControllerTypeItemStateChanged(evt);
            }
        });

        jPanelControllerType.add(jComboBoxEncodersControllerType, new AbsoluteConstraints(80, 60, 170, -1));

        jLabelControllerTypes.setText("Set Controller Types");
        jPanelControllerType.add(jLabelControllerTypes, new AbsoluteConstraints(30, 10, -1, -1));

        jToggleButtonControllerMotorReset.setText("reset");
        jToggleButtonControllerMotorReset.setToolTipText("reset the motor controller");
        jToggleButtonControllerMotorReset.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonControllerMotorReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonControllerMotorResetActionPerformed(evt);
            }
        });

        jPanelControllerType.add(jToggleButtonControllerMotorReset, new AbsoluteConstraints(360, 30, -1, -1));

        jToggleButtonControllerEncodersReset.setText("reset");
        jToggleButtonControllerEncodersReset.setToolTipText("reset the encoder controller");
        jToggleButtonControllerEncodersReset.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonControllerEncodersReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonControllerEncodersResetActionPerformed(evt);
            }
        });

        jPanelControllerType.add(jToggleButtonControllerEncodersReset, new AbsoluteConstraints(360, 60, -1, -1));

        jToggleButtonControllerOpenTerminalWindowMotors.setText("open terminal window");
        jToggleButtonControllerOpenTerminalWindowMotors.setToolTipText("open ASCII terminal window to motor controller");
        jToggleButtonControllerOpenTerminalWindowMotors.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonControllerOpenTerminalWindowMotors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonControllerOpenTerminalWindowMotorsActionPerformed(evt);
            }
        });

        jPanelControllerType.add(jToggleButtonControllerOpenTerminalWindowMotors, new AbsoluteConstraints(410, 30, -1, -1));

        jToggleButtonControllerOpenTerminalWindowEncoders.setText("open terminal window");
        jToggleButtonControllerOpenTerminalWindowEncoders.setToolTipText("open ASCII terminal window to encoders controller");
        jToggleButtonControllerOpenTerminalWindowEncoders.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonControllerOpenTerminalWindowEncoders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonControllerOpenTerminalWindowEncodersActionPerformed(evt);
            }
        });

        jPanelControllerType.add(jToggleButtonControllerOpenTerminalWindowEncoders, new AbsoluteConstraints(410, 60, -1, -1));

        jComboBoxControllerMotorCommPort.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxControllerMotorCommPortItemStateChanged(evt);
            }
        });

        jPanelControllerType.add(jComboBoxControllerMotorCommPort, new AbsoluteConstraints(260, 30, 80, -1));

        jComboBoxControllerEncoderCommPort.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxControllerEncoderCommPortItemStateChanged(evt);
            }
        });

        jPanelControllerType.add(jComboBoxControllerEncoderCommPort, new AbsoluteConstraints(260, 60, 80, -1));

        jToggleButtonControllerSiTechCfg.setText("SiTech cfg");
        jToggleButtonControllerSiTechCfg.setToolTipText("launch the Sidereal Technology controller configuration window");
        jToggleButtonControllerSiTechCfg.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonControllerSiTechCfg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonControllerSiTechCfgActionPerformed(evt);
            }
        });

        jPanelControllerType.add(jToggleButtonControllerSiTechCfg, new AbsoluteConstraints(410, 10, -1, -1));

        jPanelController.add(jPanelControllerType, new AbsoluteConstraints(10, 10, 550, 90));

        jPanelControllerDir.setLayout(new AbsoluteLayout());

        jPanelControllerDir.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelControllerNum3.setText("3.");
        jPanelControllerDir.add(jLabelControllerNum3, new AbsoluteConstraints(10, 10, -1, -1));

        jLabelControllerCheckDirectionMovement.setText("Check Direction of Movement");
        jPanelControllerDir.add(jLabelControllerCheckDirectionMovement, new AbsoluteConstraints(30, 10, -1, -1));

        jTextAreaCheckDirection.setEditable(false);
        jTextAreaCheckDirection.setFont(new java.awt.Font("Dialog", 0, 10));
        jTextAreaCheckDirection.setLineWrap(true);
        jTextAreaCheckDirection.setText("Axis should move 'up' or 'clockwise'.  If not, stop the motor and flip the motor's 'reverse' button.   Encoders should count upward.  If not, flip the encoder's 'reverse' button.");
        jTextAreaCheckDirection.setWrapStyleWord(true);
        jPanelControllerDir.add(jTextAreaCheckDirection, new AbsoluteConstraints(10, 40, 530, 40));

        jToggleButtonCheckDirectionAltDec.setText("alt/Dec motor up");
        jToggleButtonCheckDirectionAltDec.setToolTipText("move altitude/declination motor upward");
        jToggleButtonCheckDirectionAltDec.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonCheckDirectionAltDec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonCheckDirectionAltDecActionPerformed(evt);
            }
        });

        jPanelControllerDir.add(jToggleButtonCheckDirectionAltDec, new AbsoluteConstraints(30, 110, 180, -1));

        jRadioButtonCheckDirectionAltDecReverse.setText("reverse");
        jRadioButtonCheckDirectionAltDecReverse.setToolTipText("reverse the altitude/declination motor's direction");
        jRadioButtonCheckDirectionAltDecReverse.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonCheckDirectionAltDecReverseItemStateChanged(evt);
            }
        });

        jPanelControllerDir.add(jRadioButtonCheckDirectionAltDecReverse, new AbsoluteConstraints(210, 110, 80, -1));

        jToggleButtonCheckDirectionStop.setText("stop motors");
        jToggleButtonCheckDirectionStop.setToolTipText("stop all motors");
        jToggleButtonCheckDirectionStop.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonCheckDirectionStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonCheckDirectionStopActionPerformed(evt);
            }
        });

        jPanelControllerDir.add(jToggleButtonCheckDirectionStop, new AbsoluteConstraints(80, 90, 80, 20));

        jToggleButtonCheckDirectionAzRa.setText("az/Ra motor clockwise");
        jToggleButtonCheckDirectionAzRa.setToolTipText("move azimuth/right ascension motor clockwise");
        jToggleButtonCheckDirectionAzRa.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonCheckDirectionAzRa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonCheckDirectionAzRaActionPerformed(evt);
            }
        });

        jPanelControllerDir.add(jToggleButtonCheckDirectionAzRa, new AbsoluteConstraints(30, 130, 180, -1));

        jRadioButtonCheckDirectionAzRaReverse.setText("reverse");
        jRadioButtonCheckDirectionAzRaReverse.setToolTipText("reverse the azimuth/right ascension motor's direction");
        jRadioButtonCheckDirectionAzRaReverse.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonCheckDirectionAzRaReverseItemStateChanged(evt);
            }
        });

        jPanelControllerDir.add(jRadioButtonCheckDirectionAzRaReverse, new AbsoluteConstraints(210, 130, 80, -1));

        jToggleButtonCheckDirectionFieldR.setText("fieldRotation motor clockwise");
        jToggleButtonCheckDirectionFieldR.setToolTipText("move field rotation motor clockwise");
        jToggleButtonCheckDirectionFieldR.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonCheckDirectionFieldR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonCheckDirectionFieldRActionPerformed(evt);
            }
        });

        jPanelControllerDir.add(jToggleButtonCheckDirectionFieldR, new AbsoluteConstraints(30, 150, 180, -1));

        jRadioButtonCheckDirectionFieldRReverse.setText("reverse");
        jRadioButtonCheckDirectionFieldRReverse.setToolTipText("reverse the field rotation motor's direction");
        jRadioButtonCheckDirectionFieldRReverse.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonCheckDirectionFieldRReverseItemStateChanged(evt);
            }
        });

        jPanelControllerDir.add(jRadioButtonCheckDirectionFieldRReverse, new AbsoluteConstraints(210, 150, 80, -1));

        jToggleButtonCheckDirectionFocus.setText("focus motor clockwise");
        jToggleButtonCheckDirectionFocus.setToolTipText("move focuser motor clockwise");
        jToggleButtonCheckDirectionFocus.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonCheckDirectionFocus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonCheckDirectionFocusActionPerformed(evt);
            }
        });

        jPanelControllerDir.add(jToggleButtonCheckDirectionFocus, new AbsoluteConstraints(30, 170, 180, -1));

        jRadioButtonCheckDirectionFocusReverse.setText("reverse");
        jRadioButtonCheckDirectionFocusReverse.setToolTipText("reverse the focuser motor's direction");
        jRadioButtonCheckDirectionFocusReverse.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRadioButtonCheckDirectionFocusReverseItemStateChanged(evt);
            }
        });

        jPanelControllerDir.add(jRadioButtonCheckDirectionFocusReverse, new AbsoluteConstraints(210, 170, 80, -1));

        jRadioButtonCheckDirectionAzRaReverseEncoder.setText("reverse");
        jRadioButtonCheckDirectionAzRaReverseEncoder.setToolTipText("reverse the azimuth/right ascension encoder direction");
        jRadioButtonCheckDirectionAzRaReverseEncoder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonCheckDirectionAzRaReverseEncoderActionPerformed(evt);
            }
        });

        jPanelControllerDir.add(jRadioButtonCheckDirectionAzRaReverseEncoder, new AbsoluteConstraints(410, 130, 80, -1));

        jRadioButtonCheckDirectionFieldRReverseEncoder.setText("reverse");
        jRadioButtonCheckDirectionFieldRReverseEncoder.setToolTipText("reverse the field rotation encoder direction");
        jRadioButtonCheckDirectionFieldRReverseEncoder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonCheckDirectionFieldRReverseEncoderActionPerformed(evt);
            }
        });

        jPanelControllerDir.add(jRadioButtonCheckDirectionFieldRReverseEncoder, new AbsoluteConstraints(410, 150, 80, -1));

        jRadioButtonCheckDirectionFocusReverseEncoder.setText("reverse");
        jRadioButtonCheckDirectionFocusReverseEncoder.setToolTipText("reverse the focuser encoder direction");
        jRadioButtonCheckDirectionFocusReverseEncoder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonCheckDirectionFocusReverseEncoderActionPerformed(evt);
            }
        });

        jPanelControllerDir.add(jRadioButtonCheckDirectionFocusReverseEncoder, new AbsoluteConstraints(410, 170, 80, -1));

        jRadioButtonCheckDirectionAltDecReverseEncoder.setText("reverse");
        jRadioButtonCheckDirectionAltDecReverseEncoder.setToolTipText("reverse the altitude/declination encoder direction");
        jRadioButtonCheckDirectionAltDecReverseEncoder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonCheckDirectionAltDecReverseEncoderActionPerformed(evt);
            }
        });

        jPanelControllerDir.add(jRadioButtonCheckDirectionAltDecReverseEncoder, new AbsoluteConstraints(410, 110, 80, -1));

        jLabelCheckDirectionMotors.setText("motors");
        jPanelControllerDir.add(jLabelCheckDirectionMotors, new AbsoluteConstraints(220, 90, -1, -1));

        jLabelCheckDirectionEncoders.setText("encoders");
        jPanelControllerDir.add(jLabelCheckDirectionEncoders, new AbsoluteConstraints(380, 90, -1, -1));

        jLabelCheckDirectionAltDecEncoder.setText("(altDec cnt)");
        jLabelCheckDirectionAltDecEncoder.setToolTipText("encoder counts for the altitude/declination axis");
        jPanelControllerDir.add(jLabelCheckDirectionAltDecEncoder, new AbsoluteConstraints(330, 115, 80, -1));

        jLabelCheckDirectionAzRaEncoder.setText("(azRa cnt)");
        jLabelCheckDirectionAzRaEncoder.setToolTipText("encoder counts for the azimuth/right ascension axis");
        jPanelControllerDir.add(jLabelCheckDirectionAzRaEncoder, new AbsoluteConstraints(330, 135, 80, -1));

        jLabelCheckDirectionFieldREncoder.setText("(fieldR cnt)");
        jLabelCheckDirectionFieldREncoder.setToolTipText("encoder counts for the field rotation axis");
        jPanelControllerDir.add(jLabelCheckDirectionFieldREncoder, new AbsoluteConstraints(330, 155, 80, -1));

        jLabelCheckDirectionFocusEncoder.setText("(focus cnt)");
        jLabelCheckDirectionFocusEncoder.setToolTipText("encoder counts for the focuser axis");
        jPanelControllerDir.add(jLabelCheckDirectionFocusEncoder, new AbsoluteConstraints(330, 175, 80, -1));

        jPanelController.add(jPanelControllerDir, new AbsoluteConstraints(10, 220, 550, 200));

        jPanelControllerSteps.setLayout(new AbsoluteLayout());

        jPanelControllerSteps.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelControllerNum2.setText("2.");
        jPanelControllerSteps.add(jLabelControllerNum2, new AbsoluteConstraints(10, 10, -1, -1));

        jLabelStepRev.setText("Set Counts Per Scope Axis Revolution");
        jPanelControllerSteps.add(jLabelStepRev, new AbsoluteConstraints(30, 10, -1, -1));

        jLabelStepRevMotors.setText("motors");
        jPanelControllerSteps.add(jLabelStepRevMotors, new AbsoluteConstraints(70, 50, -1, -1));

        jLabelStepRevEncoders.setText("encoders");
        jPanelControllerSteps.add(jLabelStepRevEncoders, new AbsoluteConstraints(60, 70, -1, -1));

        jLabelStepRevAltDec.setText("alt/Dec");
        jPanelControllerSteps.add(jLabelStepRevAltDec, new AbsoluteConstraints(140, 30, -1, -1));

        jLabelStepRevAzRa.setText("az/Ra");
        jPanelControllerSteps.add(jLabelStepRevAzRa, new AbsoluteConstraints(220, 30, -1, -1));

        jLabelStepRevFieldR.setText("fieldRotation");
        jPanelControllerSteps.add(jLabelStepRevFieldR, new AbsoluteConstraints(280, 30, -1, -1));

        jLabelStepRevFocus.setText("focus");
        jPanelControllerSteps.add(jLabelStepRevFocus, new AbsoluteConstraints(380, 30, -1, -1));

        jTextFieldStepRevMotorAltDec.setToolTipText("motor counts per full revolution of the altitude or declination telescope axis");
        jPanelControllerSteps.add(jTextFieldStepRevMotorAltDec, new AbsoluteConstraints(120, 50, 80, -1));

        jTextFieldStepRevMotorAzRa.setToolTipText("motor counts per full revolution of the azimuth or right ascension telescope axis");
        jPanelControllerSteps.add(jTextFieldStepRevMotorAzRa, new AbsoluteConstraints(200, 50, 80, -1));

        jTextFieldStepRevMotorFieldR.setToolTipText("motor counts per full revolution of the field rotation telescope axis");
        jPanelControllerSteps.add(jTextFieldStepRevMotorFieldR, new AbsoluteConstraints(280, 50, 80, -1));

        jTextFieldStepRevMotorFocus.setToolTipText("motor counts per full revolution of th focuser axis");
        jPanelControllerSteps.add(jTextFieldStepRevMotorFocus, new AbsoluteConstraints(360, 50, 80, -1));

        jTextFieldStepRevEncoderFocus.setToolTipText("encoder counts per full revolution of the focuser axis");
        jPanelControllerSteps.add(jTextFieldStepRevEncoderFocus, new AbsoluteConstraints(360, 70, 80, -1));

        jTextFieldStepRevEncoderFieldR.setToolTipText("encoder counts per full revolution of thefield rotation telescope axis");
        jPanelControllerSteps.add(jTextFieldStepRevEncoderFieldR, new AbsoluteConstraints(280, 70, 80, -1));

        jTextFieldStepRevEncoderAzRa.setToolTipText("encoder counts per full revolution of the azimuth or right ascension telescope axis");
        jPanelControllerSteps.add(jTextFieldStepRevEncoderAzRa, new AbsoluteConstraints(200, 70, 80, -1));

        jTextFieldStepRevEncoderAltDec.setToolTipText("encoder counts per full revolution of the altitude or declination telescope axis");
        jPanelControllerSteps.add(jTextFieldStepRevEncoderAltDec, new AbsoluteConstraints(120, 70, 80, -1));

        jToggleButtonStepRevOK.setText("adopt values");
        jToggleButtonStepRevOK.setToolTipText("save values entered");
        jToggleButtonStepRevOK.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonStepRevOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonStepRevOKActionPerformed(evt);
            }
        });

        jPanelControllerSteps.add(jToggleButtonStepRevOK, new AbsoluteConstraints(460, 70, -1, -1));

        jPanelController.add(jPanelControllerSteps, new AbsoluteConstraints(10, 110, 550, 100));

        jTabbedPaneMain.addTab("Controller", jPanelController);

        jPanelMainStartup.setLayout(new AbsoluteLayout());

        jPanelMainStartup.setName("Startup");
        jPanelStartupSite.setLayout(new AbsoluteLayout());

        jPanelStartupSite.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelStartupSiteLatitude.setText("latitude");
        jPanelStartupSite.add(jLabelStartupSiteLatitude, new AbsoluteConstraints(140, 40, -1, -1));

        jLabelMainStartupSiteName.setText("name");
        jPanelStartupSite.add(jLabelMainStartupSiteName, new AbsoluteConstraints(140, 10, -1, -1));

        jLabelStartupSiteLongitude.setText("longitude");
        jPanelStartupSite.add(jLabelStartupSiteLongitude, new AbsoluteConstraints(260, 40, -1, -1));

        jTextFieldStartupSiteName.setToolTipText("name of the site where the telescope is located");
        jPanelStartupSite.add(jTextFieldStartupSiteName, new AbsoluteConstraints(180, 10, 190, -1));

        jLabelStartupNum1.setText("1.");
        jPanelStartupSite.add(jLabelStartupNum1, new AbsoluteConstraints(10, 10, -1, -1));

        jTextFieldStartupSiteLatitude.setToolTipText("latitude in decimal degrees (negative values for southern hemisphere)");
        jPanelStartupSite.add(jTextFieldStartupSiteLatitude, new AbsoluteConstraints(190, 40, 50, -1));

        jTextFieldStartupSiteLongitude.setToolTipText("longitude in decimal degrees, as measured westward from Greenwich");
        jPanelStartupSite.add(jTextFieldStartupSiteLongitude, new AbsoluteConstraints(320, 40, 50, -1));

        jToggleButtonStartupSiteOK.setText("adopt values");
        jToggleButtonStartupSiteOK.setToolTipText("save site values");
        jToggleButtonStartupSiteOK.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonStartupSiteOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonStartupSiteOKActionPerformed(evt);
            }
        });

        jPanelStartupSite.add(jToggleButtonStartupSiteOK, new AbsoluteConstraints(460, 40, -1, -1));

        jLabelStartupSite.setText("Site");
        jLabelStartupSite.setToolTipText("");
        jPanelStartupSite.add(jLabelStartupSite, new AbsoluteConstraints(30, 10, -1, -1));

        jPanelMainStartup.add(jPanelStartupSite, new AbsoluteConstraints(10, 10, 550, 70));

        jPanelStartupTelescopeMount.setLayout(new AbsoluteLayout());

        jPanelStartupTelescopeMount.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelStartupMounting.setText("Telescope Mount");
        jPanelStartupTelescopeMount.add(jLabelStartupMounting, new AbsoluteConstraints(80, 10, -1, -1));

        jComboBoxStartupMountType.setToolTipText("type of telescope mounting: characteristics are predefined");
        jComboBoxStartupMountType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxStartupMountTypeItemStateChanged(evt);
            }
        });

        jPanelStartupTelescopeMount.add(jComboBoxStartupMountType, new AbsoluteConstraints(190, 10, 250, -1));

        jRadioButtonStartupCanMoveToPole.setText("canMoveToPole");
        jRadioButtonStartupCanMoveToPole.setToolTipText("the telescope can point to the primary axis' pole (ie, if equatorial mount in northern hemisphere, then scope can point to Polaris)");
        jRadioButtonStartupCanMoveToPole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonStartupCanMoveToPoleActionPerformed(evt);
            }
        });

        jPanelStartupTelescopeMount.add(jRadioButtonStartupCanMoveToPole, new AbsoluteConstraints(10, 40, 170, -1));

        jRadioButtonStartupCanMoveThruPole.setText("canMoveThruPole");
        jRadioButtonStartupCanMoveThruPole.setToolTipText("the telescope can swing past the pole");
        jRadioButtonStartupCanMoveThruPole.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonStartupCanMoveThruPoleActionPerformed(evt);
            }
        });

        jPanelStartupTelescopeMount.add(jRadioButtonStartupCanMoveThruPole, new AbsoluteConstraints(10, 60, 170, -1));

        jRadioButtonStartupPrimaryAxisFullyRotates.setText("primaryAxisFullyRotates");
        jRadioButtonStartupPrimaryAxisFullyRotates.setToolTipText("the primary axis can rotate a full 360 degree circle (primary axis is the Right Ascension or azimuth axis)");
        jRadioButtonStartupPrimaryAxisFullyRotates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonStartupPrimaryAxisFullyRotatesActionPerformed(evt);
            }
        });

        jPanelStartupTelescopeMount.add(jRadioButtonStartupPrimaryAxisFullyRotates, new AbsoluteConstraints(10, 80, 170, -1));

        jRadioButtonStartupMeridianFlipPossible.setText("meridianFlipPossible");
        jRadioButtonStartupMeridianFlipPossible.setToolTipText("it is possible for the mounting to be flipped across the meridian");
        jRadioButtonStartupMeridianFlipPossible.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonStartupMeridianFlipPossibleActionPerformed(evt);
            }
        });

        jPanelStartupTelescopeMount.add(jRadioButtonStartupMeridianFlipPossible, new AbsoluteConstraints(190, 40, 150, -1));

        jRadioButtonStartupMeridianFlipRequired.setText("meridianFlipRequired");
        jRadioButtonStartupMeridianFlipRequired.setToolTipText("it is required for the mounting to be flipped across the meridian");
        jRadioButtonStartupMeridianFlipRequired.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonStartupMeridianFlipRequiredActionPerformed(evt);
            }
        });

        jPanelStartupTelescopeMount.add(jRadioButtonStartupMeridianFlipRequired, new AbsoluteConstraints(190, 60, 150, -1));

        jRadioButtonStartupAutoMeridianFlip.setText("autoMeridianFlip");
        jRadioButtonStartupAutoMeridianFlip.setToolTipText("the mounting will be flipped across the meridian automatically, when necessary");
        jRadioButtonStartupAutoMeridianFlip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonStartupAutoMeridianFlipActionPerformed(evt);
            }
        });

        jPanelStartupTelescopeMount.add(jRadioButtonStartupAutoMeridianFlip, new AbsoluteConstraints(390, 40, 150, -1));

        jLabelStartupAutoMeridianFlipFuzzDeg.setText("autoMeridianFlipFuzzDeg");
        jPanelStartupTelescopeMount.add(jLabelStartupAutoMeridianFlipFuzzDeg, new AbsoluteConstraints(350, 65, -1, -1));

        jTextFieldStartupAutoMeridianFlipFuzzDeg.setToolTipText("amount in decimal degrees that the mounting can move past the meridian before a meridan flip occurs; this allows some latitude in positioning the scope close to the meridian");
        jPanelStartupTelescopeMount.add(jTextFieldStartupAutoMeridianFlipFuzzDeg, new AbsoluteConstraints(495, 65, 45, -1));

        jLabelStartupNum2.setText("2.");
        jPanelStartupTelescopeMount.add(jLabelStartupNum2, new AbsoluteConstraints(10, 10, -1, -1));

        jToggleButtonStartupTelescopeOK.setText("adopt value");
        jToggleButtonStartupTelescopeOK.setToolTipText("save telescope mount values");
        jToggleButtonStartupTelescopeOK.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonStartupTelescopeOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonStartupTelescopeOKActionPerformed(evt);
            }
        });

        jPanelStartupTelescopeMount.add(jToggleButtonStartupTelescopeOK, new AbsoluteConstraints(460, 100, -1, -1));

        jPanelMainStartup.add(jPanelStartupTelescopeMount, new AbsoluteConstraints(10, 110, 550, 130));

        jPanelStartupSetCoordinates.setLayout(new AbsoluteLayout());

        jPanelStartupSetCoordinates.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToggleButtonStartupResetCurrentCoordinates.setText("reset current coordinates");
        jToggleButtonStartupResetCurrentCoordinates.setToolTipText("reset current coordinates");
        jToggleButtonStartupResetCurrentCoordinates.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonStartupResetCurrentCoordinates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonStartupResetCurrentCoordinatesActionPerformed(evt);
            }
        });

        jPanelStartupSetCoordinates.add(jToggleButtonStartupResetCurrentCoordinates, new AbsoluteConstraints(50, 40, -1, 20));

        jLabelStartupNum3.setText("3.");
        jPanelStartupSetCoordinates.add(jLabelStartupNum3, new AbsoluteConstraints(10, 10, -1, -1));

        jToggleButtonStartupResetScopeToEncoders.setText("reset scope to external encoders position:");
        jToggleButtonStartupResetScopeToEncoders.setToolTipText("reset the telescope's position to the current encoder position");
        jToggleButtonStartupResetScopeToEncoders.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonStartupResetScopeToEncoders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonStartupResetScopeToEncodersActionPerformed(evt);
            }
        });

        jPanelStartupSetCoordinates.add(jToggleButtonStartupResetScopeToEncoders, new AbsoluteConstraints(90, 90, -1, -1));

        jToggleButtonStartupResetEncodersToScope.setText("reset external encoders to scope position:");
        jToggleButtonStartupResetEncodersToScope.setToolTipText("reset the encoders to the telescope's current position");
        jToggleButtonStartupResetEncodersToScope.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonStartupResetEncodersToScope.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonStartupResetEncodersToScopeActionPerformed(evt);
            }
        });

        jPanelStartupSetCoordinates.add(jToggleButtonStartupResetEncodersToScope, new AbsoluteConstraints(90, 70, -1, -1));

        jLabeStartuplSetCoordinates.setText("Set Coordinates");
        jPanelStartupSetCoordinates.add(jLabeStartuplSetCoordinates, new AbsoluteConstraints(30, 10, -1, -1));

        jLabelStartupScopePosition.setText("(scope position)");
        jLabelStartupScopePosition.setToolTipText("current telescope position");
        jPanelStartupSetCoordinates.add(jLabelStartupScopePosition, new AbsoluteConstraints(340, 70, 190, -1));

        jLabelStartupEncodersPosition.setText("(external encoders position)");
        jLabelStartupEncodersPosition.setToolTipText("current encoder position");
        jPanelStartupSetCoordinates.add(jLabelStartupEncodersPosition, new AbsoluteConstraints(340, 90, 190, -1));

        jLabelStartupResetThreshold.setText("auto reset scope to external encoders threshold deg");
        jPanelStartupSetCoordinates.add(jLabelStartupResetThreshold, new AbsoluteConstraints(90, 120, -1, -1));

        jTextFieldStartupResetThreshold.setToolTipText("if scope and encoder difference exceeds this threshold, then scope will be reset to encoder values");
        jPanelStartupSetCoordinates.add(jTextFieldStartupResetThreshold, new AbsoluteConstraints(400, 120, 50, -1));

        jToggleButtonStartupResetThresholdTelescopeOK.setText("adopt value");
        jToggleButtonStartupResetThresholdTelescopeOK.setToolTipText("save reset scope to encoder threshold value");
        jToggleButtonStartupResetThresholdTelescopeOK.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonStartupResetThresholdTelescopeOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonStartupResetThresholdTelescopeOKActionPerformed(evt);
            }
        });

        jPanelStartupSetCoordinates.add(jToggleButtonStartupResetThresholdTelescopeOK, new AbsoluteConstraints(460, 120, -1, -1));

        jRadioButtonSetCoordinatesSetMeridianFlipOn.setText("meridian flipped (west of pier facing east)");
        jRadioButtonSetCoordinatesSetMeridianFlipOn.setToolTipText("telescope is flipped across the meridian");
        jRadioButtonSetCoordinatesSetMeridianFlipOn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonSetCoordinatesSetMeridianFlipOnActionPerformed(evt);
            }
        });

        jPanelStartupSetCoordinates.add(jRadioButtonSetCoordinatesSetMeridianFlipOn, new AbsoluteConstraints(250, 30, -1, -1));

        jRadioButtonSetCoordinatesSetMeridianFlipOff.setText("meridian NOT flipped  (east of pier facing west)");
        jRadioButtonSetCoordinatesSetMeridianFlipOff.setToolTipText("telescope is NOT flipped across the meridian");
        jRadioButtonSetCoordinatesSetMeridianFlipOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonSetCoordinatesSetMeridianFlipOffActionPerformed(evt);
            }
        });

        jPanelStartupSetCoordinates.add(jRadioButtonSetCoordinatesSetMeridianFlipOff, new AbsoluteConstraints(250, 10, -1, -1));

        jPanelMainStartup.add(jPanelStartupSetCoordinates, new AbsoluteConstraints(10, 270, 550, 150));

        jTabbedPaneMain.addTab("Startup", jPanelMainStartup);

        jPanelMainInitialization.setLayout(new AbsoluteLayout());

        jPanelMainInitialization.setName("Initialization");
        jPanelInitializationInit12.setLayout(new AbsoluteLayout());

        jPanelInitializationInit12.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelInitializationInit1.setText("Initialization #1");
        jPanelInitializationInit12.add(jLabelInitializationInit1, new AbsoluteConstraints(30, 10, -1, -1));

        jLabelInitializationInit2.setText("Initialization #2");
        jPanelInitializationInit12.add(jLabelInitializationInit2, new AbsoluteConstraints(30, 50, -1, -1));

        jToggleButtonInitializationInit1.setText("change");
        jToggleButtonInitializationInit1.setToolTipText("change the auto generated initialization object");
        jToggleButtonInitializationInit1.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonInitializationInit1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonInitializationInit1ActionPerformed(evt);
            }
        });

        jPanelInitializationInit12.add(jToggleButtonInitializationInit1, new AbsoluteConstraints(490, 10, -1, 20));

        jToggleButtonInitializationInit2.setText("change");
        jToggleButtonInitializationInit2.setToolTipText("change the auto generated initialization object");
        jToggleButtonInitializationInit2.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonInitializationInit2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonInitializationInit2ActionPerformed(evt);
            }
        });

        jPanelInitializationInit12.add(jToggleButtonInitializationInit2, new AbsoluteConstraints(490, 50, -1, 20));

        jTextFieldInitializationInit1.setEditable(false);
        jTextFieldInitializationInit1.setText("(initialization #1 coordinates)");
        jTextFieldInitializationInit1.setToolTipText("equatorial coordinates to be used for the auto initialization object #1");
        jPanelInitializationInit12.add(jTextFieldInitializationInit1, new AbsoluteConstraints(120, 10, 360, -1));

        jTextFieldInitializationInit2.setEditable(false);
        jTextFieldInitializationInit2.setText("(initialization #2 coordinates)");
        jTextFieldInitializationInit2.setToolTipText("equatorial coordinates to be used for the auto initialization object #2");
        jPanelInitializationInit12.add(jTextFieldInitializationInit2, new AbsoluteConstraints(120, 50, 360, -1));

        jLabelInitializationNum2.setText("2.");
        jPanelInitializationInit12.add(jLabelInitializationNum2, new AbsoluteConstraints(10, 10, -1, -1));

        jPanelMainInitialization.add(jPanelInitializationInit12, new AbsoluteConstraints(10, 60, 550, 90));

        jPanelInitializationStartInitState.setLayout(new AbsoluteLayout());

        jPanelInitializationStartInitState.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelInitializationStartInitState.setText("Starting initialization state");
        jPanelInitializationStartInitState.add(jLabelInitializationStartInitState, new AbsoluteConstraints(30, 10, -1, -1));

        jComboBoxInitializationStartInitState.setToolTipText("initialization status");
        jComboBoxInitializationStartInitState.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxInitializationStartInitStateItemStateChanged(evt);
            }
        });

        jPanelInitializationStartInitState.add(jComboBoxInitializationStartInitState, new AbsoluteConstraints(200, 8, 210, -1));

        jLabelInitializationNum1.setText("1.");
        jPanelInitializationStartInitState.add(jLabelInitializationNum1, new AbsoluteConstraints(10, 10, -1, -1));

        jPanelMainInitialization.add(jPanelInitializationStartInitState, new AbsoluteConstraints(10, 10, 550, 40));

        jPanelInitializationInstructions.setLayout(new AbsoluteLayout());

        jPanelInitializationInstructions.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelInitializationNum3.setText("3.");
        jPanelInitializationInstructions.add(jLabelInitializationNum3, new AbsoluteConstraints(10, 10, -1, -1));

        jLabelInitializationInstructions.setText("Initialization instructions");
        jPanelInitializationInstructions.add(jLabelInitializationInstructions, new AbsoluteConstraints(30, 10, -1, -1));

        jTextAreaInitializationInstructions.setEditable(false);
        jTextAreaInitializationInstructions.setFont(new java.awt.Font("Dialog", 0, 10));
        jTextAreaInitializationInstructions.setText("(init instructions)");
        jTextAreaInitializationInstructions.setToolTipText("follow these instructions for auto initialization");
        jTextAreaInitializationInstructions.setWrapStyleWord(true);
        jPanelInitializationInstructions.add(jTextAreaInitializationInstructions, new AbsoluteConstraints(10, 30, 530, 60));

        jPanelMainInitialization.add(jPanelInitializationInstructions, new AbsoluteConstraints(10, 160, 550, 100));

        jPanelInitializationAdvanced.setLayout(new AbsoluteLayout());

        jPanelInitializationAdvanced.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jLabelInitializationNum4.setText("4.");
        jPanelInitializationAdvanced.add(jLabelInitializationNum4, new AbsoluteConstraints(10, 10, -1, -1));

        jLabelInitializationAdvanced.setText("Optional advanced initialization actions");
        jPanelInitializationAdvanced.add(jLabelInitializationAdvanced, new AbsoluteConstraints(30, 10, -1, -1));

        jLabelInitializationInit3.setText("Initialization #3");
        jPanelInitializationAdvanced.add(jLabelInitializationInit3, new AbsoluteConstraints(30, 40, -1, -1));

        jTextFieldInitializationInit3.setEditable(false);
        jTextFieldInitializationInit3.setText("(initialization #3 coordinates)");
        jTextFieldInitializationInit3.setToolTipText("equatorial coordinates to be used for the auto initialization object #2");
        jPanelInitializationAdvanced.add(jTextFieldInitializationInit3, new AbsoluteConstraints(120, 40, 360, -1));

        jToggleButtonInitializationInit3.setText("change");
        jToggleButtonInitializationInit3.setToolTipText("change initialization #3 object");
        jToggleButtonInitializationInit3.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonInitializationInit3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonInitializationInit3ActionPerformed(evt);
            }
        });

        jPanelInitializationAdvanced.add(jToggleButtonInitializationInit3, new AbsoluteConstraints(490, 40, -1, 20));

        jToggleButtonInitializationUseClosest.setText("re-init using closest initializations/analysis points");
        jToggleButtonInitializationUseClosest.setToolTipText("use the closest initializations to current position, as found in the initialization history and the analysis files");
        jToggleButtonInitializationUseClosest.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonInitializationUseClosest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonInitializationUseClosestActionPerformed(evt);
            }
        });

        jPanelInitializationAdvanced.add(jToggleButtonInitializationUseClosest, new AbsoluteConstraints(30, 70, -1, -1));

        jToggleButtonInitializationFixAltOffset.setText("fix altitude offset in degrees");
        jToggleButtonInitializationFixAltOffset.setToolTipText("fixup the altitude error as calculated from initialization");
        jToggleButtonInitializationFixAltOffset.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonInitializationFixAltOffset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonInitializationFixAltOffsetActionPerformed(evt);
            }
        });

        jPanelInitializationAdvanced.add(jToggleButtonInitializationFixAltOffset, new AbsoluteConstraints(30, 100, -1, -1));

        jTextFieldInitializationAltOffset.setText("(alt err)");
        jPanelInitializationAdvanced.add(jTextFieldInitializationAltOffset, new AbsoluteConstraints(200, 100, 50, -1));

        jLabelInitializationNewZ1Deg.setText("new Z1 deg");
        jPanelInitializationAdvanced.add(jLabelInitializationNewZ1Deg, new AbsoluteConstraints(30, 130, -1, -1));

        jLabelInitializationNewZ2Deg.setText("new Z2 deg");
        jPanelInitializationAdvanced.add(jLabelInitializationNewZ2Deg, new AbsoluteConstraints(160, 130, -1, -1));

        jLabelInitializationNewZ3Deg.setText("new Z3 deg");
        jPanelInitializationAdvanced.add(jLabelInitializationNewZ3Deg, new AbsoluteConstraints(290, 130, -1, -1));

        jTextFieldInitializationNewZ1Deg.setToolTipText("offset of altitude to perpendicular of azimuth, ie, one side of rocker box higher than the other");
        jPanelInitializationAdvanced.add(jTextFieldInitializationNewZ1Deg, new AbsoluteConstraints(100, 130, 50, -1));

        jTextFieldInitializationNewZ2Deg.setToolTipText("difference between mechanical and optical axes in azimuth or horizontal plane, from left to right");
        jPanelInitializationAdvanced.add(jTextFieldInitializationNewZ2Deg, new AbsoluteConstraints(230, 130, 50, -1));

        jTextFieldInitializationNewZ3Deg.setToolTipText("correction to zero setting of altitude, ie, difference between mechanical and optical axes in the vertical direction (same as altitude offset)");
        jPanelInitializationAdvanced.add(jTextFieldInitializationNewZ3Deg, new AbsoluteConstraints(360, 130, 50, -1));

        jToggleButtonInitializationReInit.setText("re-init");
        jToggleButtonInitializationReInit.setToolTipText("re-initialize, using displayed Z123 values");
        jToggleButtonInitializationReInit.setBorder(null);
        jToggleButtonInitializationReInit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonInitializationReInitActionPerformed(evt);
            }
        });

        jPanelInitializationAdvanced.add(jToggleButtonInitializationReInit, new AbsoluteConstraints(440, 130, -1, -1));

        jToggleButtonInitializationViewInitHistory.setText("init history");
        jToggleButtonInitializationViewInitHistory.setToolTipText("view history of all initializations");
        jToggleButtonInitializationViewInitHistory.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonInitializationViewInitHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonInitializationViewInitHistoryActionPerformed(evt);
            }
        });

        jPanelInitializationAdvanced.add(jToggleButtonInitializationViewInitHistory, new AbsoluteConstraints(430, 100, -1, -1));

        jPanelMainInitialization.add(jPanelInitializationAdvanced, new AbsoluteConstraints(10, 270, 550, 160));

        jTabbedPaneMain.addTab("Initialization", jPanelMainInitialization);

        jPanelMainGoto.setLayout(new AbsoluteLayout());

        jPanelMainGoto.setName("GotoCmds");
        jToggleButtonGotoNewTargetCoordinates.setText("new target coordinate");
        jToggleButtonGotoNewTargetCoordinates.setToolTipText("change target coordinate");
        jToggleButtonGotoNewTargetCoordinates.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGotoNewTargetCoordinates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGotoNewTargetCoordinatesActionPerformed(evt);
            }
        });

        jPanelMainGoto.add(jToggleButtonGotoNewTargetCoordinates, new AbsoluteConstraints(60, 20, -1, -1));

        jPanelGotoSpiralSearch.setLayout(new AbsoluteLayout());

        jPanelGotoSpiralSearch.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToggleButtonGotoSpiralSearchBegin.setText("begin");
        jToggleButtonGotoSpiralSearchBegin.setToolTipText("initiate a spiral search pattern");
        jToggleButtonGotoSpiralSearchBegin.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGotoSpiralSearchBegin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGotoSpiralSearchBeginActionPerformed(evt);
            }
        });

        jPanelGotoSpiralSearch.add(jToggleButtonGotoSpiralSearchBegin, new AbsoluteConstraints(110, 40, -1, 20));

        jLabelGotoSpiralSearchRad.setText("spiral radius deg");
        jLabelGotoSpiralSearchRad.setToolTipText("radius of the spiral search pattern in degrees of arc");
        jPanelGotoSpiralSearch.add(jLabelGotoSpiralSearchRad, new AbsoluteConstraints(10, 80, -1, 20));

        jTextFieldGotoSpiralSearchRad.setToolTipText("spiral search pattern radius in degrees");
        jPanelGotoSpiralSearch.add(jTextFieldGotoSpiralSearchRad, new AbsoluteConstraints(110, 80, 50, -1));

        jLabelGotoSpiralSearchSpeed.setText("speed deg/sec");
        jLabelGotoSpiralSearchSpeed.setToolTipText("speed of the spiral search pattern in degrees of arc per second");
        jPanelGotoSpiralSearch.add(jLabelGotoSpiralSearchSpeed, new AbsoluteConstraints(170, 80, -1, -1));

        jTextFieldGotoSpiralSearchSpeed.setToolTipText("speed of the spiral search pattern in degrees per second");
        jPanelGotoSpiralSearch.add(jTextFieldGotoSpiralSearchSpeed, new AbsoluteConstraints(260, 80, 50, -1));

        jLabelGotoSpiralSearch.setText("spiral search pattern");
        jPanelGotoSpiralSearch.add(jLabelGotoSpiralSearch, new AbsoluteConstraints(100, 10, -1, -1));

        jToggleButtonGotoSpiralSearchEnd.setText("end");
        jToggleButtonGotoSpiralSearchEnd.setToolTipText("stop the search pattern in progress");
        jToggleButtonGotoSpiralSearchEnd.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGotoSpiralSearchEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGotoSpiralSearchEndActionPerformed(evt);
            }
        });

        jPanelGotoSpiralSearch.add(jToggleButtonGotoSpiralSearchEnd, new AbsoluteConstraints(180, 40, -1, 20));

        jPanelMainGoto.add(jPanelGotoSpiralSearch, new AbsoluteConstraints(60, 290, 320, 110));

        jPanelGotoNearestObject.setLayout(new AbsoluteLayout());

        jPanelGotoNearestObject.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToggleButtonGotoNearestObject.setText("goto nearest object");
        jToggleButtonGotoNearestObject.setToolTipText("goto nearest object selecting from a datafile");
        jToggleButtonGotoNearestObject.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGotoNearestObject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGotoNearestObjectActionPerformed(evt);
            }
        });

        jPanelGotoNearestObject.add(jToggleButtonGotoNearestObject, new AbsoluteConstraints(30, 10, 220, 20));

        jToggleButtonGotoNextNearestObject.setText("goto next nearest object");
        jToggleButtonGotoNextNearestObject.setToolTipText("goto next nearest object, selecting from a datafile");
        jToggleButtonGotoNextNearestObject.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGotoNextNearestObject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGotoNextNearestObjectActionPerformed(evt);
            }
        });

        jPanelGotoNearestObject.add(jToggleButtonGotoNextNearestObject, new AbsoluteConstraints(30, 40, 220, 20));

        jToggleButtonGotoNearestObjectNotInput.setText("goto nearest object not input");
        jToggleButtonGotoNearestObjectNotInput.setToolTipText("goto nearest object not input, selecting from a datafile");
        jToggleButtonGotoNearestObjectNotInput.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGotoNearestObjectNotInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGotoNearestObjectNotInputActionPerformed(evt);
            }
        });

        jPanelGotoNearestObject.add(jToggleButtonGotoNearestObjectNotInput, new AbsoluteConstraints(30, 70, 220, 20));

        jToggleButtonGotoViewInputHistory.setText("input history");
        jToggleButtonGotoViewInputHistory.setToolTipText("view a history of all input positions");
        jToggleButtonGotoViewInputHistory.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGotoViewInputHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGotoViewInputHistoryActionPerformed(evt);
            }
        });

        jPanelGotoNearestObject.add(jToggleButtonGotoViewInputHistory, new AbsoluteConstraints(270, 30, -1, -1));

        jPanelMainGoto.add(jPanelGotoNearestObject, new AbsoluteConstraints(60, 150, 380, 100));

        jToggleButtonGotoHome.setText("go home");
        jToggleButtonGotoHome.setToolTipText("moves to home coordinates as defined in the configuration file");
        jToggleButtonGotoHome.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGotoHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGotoHomeActionPerformed(evt);
            }
        });

        jPanelMainGoto.add(jToggleButtonGotoHome, new AbsoluteConstraints(60, 60, -1, -1));

        jToggleButtonGotoMeridianFlip.setText("perform meridian flip");
        jToggleButtonGotoMeridianFlip.setToolTipText("perform a flip across the meridian");
        jToggleButtonGotoMeridianFlip.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGotoMeridianFlip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGotoMeridianFlipActionPerformed(evt);
            }
        });

        jPanelMainGoto.add(jToggleButtonGotoMeridianFlip, new AbsoluteConstraints(60, 100, -1, -1));

        jTabbedPaneMain.addTab("Goto", jPanelMainGoto);

        jPanelMainCmds.setLayout(new AbsoluteLayout());

        jPanelCmdsGrandTour.setLayout(new AbsoluteLayout());

        jPanelCmdsGrandTour.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToggleButtonCmdsGrandTour.setText("select grand tour");
        jToggleButtonCmdsGrandTour.setToolTipText("select a grand touring datafile");
        jToggleButtonCmdsGrandTour.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonCmdsGrandTour.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonCmdsGrandTourActionPerformed(evt);
            }
        });

        jPanelCmdsGrandTour.add(jToggleButtonCmdsGrandTour, new AbsoluteConstraints(10, 10, 150, 20));

        jTextFieldCmdsGrandTour.setEditable(false);
        jTextFieldCmdsGrandTour.setText("(grand tour filename)");
        jTextFieldCmdsGrandTour.setToolTipText("selected grand tour filename");
        jPanelCmdsGrandTour.add(jTextFieldCmdsGrandTour, new AbsoluteConstraints(10, 40, 420, -1));

        jToggleButtonCmdsGrandTourNearest.setText("jump to nearest grand tour object");
        jToggleButtonCmdsGrandTourNearest.setToolTipText("slew to the grand tour's object nearest to current position");
        jToggleButtonCmdsGrandTourNearest.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonCmdsGrandTourNearest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonCmdsGrandTourNearestActionPerformed(evt);
            }
        });

        jPanelCmdsGrandTour.add(jToggleButtonCmdsGrandTourNearest, new AbsoluteConstraints(210, 10, -1, 20));

        jPanelMainCmds.add(jPanelCmdsGrandTour, new AbsoluteConstraints(60, 20, 440, 70));

        jPanelCmdsCmdFile.setLayout(new AbsoluteLayout());

        jPanelCmdsCmdFile.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToggleButtonCmdsCmdFile.setText("select command file");
        jToggleButtonCmdsCmdFile.setToolTipText("select a file of scope commands to execute");
        jToggleButtonCmdsCmdFile.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonCmdsCmdFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonCmdsCmdFileActionPerformed(evt);
            }
        });

        jPanelCmdsCmdFile.add(jToggleButtonCmdsCmdFile, new AbsoluteConstraints(9, 10, 150, 20));

        jTextFieldCmdsCmdFileFilename.setEditable(false);
        jTextFieldCmdsCmdFileFilename.setText("(command file filename)");
        jTextFieldCmdsCmdFileFilename.setToolTipText("selected scope command file filename");
        jPanelCmdsCmdFile.add(jTextFieldCmdsCmdFileFilename, new AbsoluteConstraints(10, 40, 420, -1));

        jToggleButtonCmdsCmdFileExecute.setText("execute");
        jToggleButtonCmdsCmdFileExecute.setToolTipText("begin executing the loaded scope command file");
        jToggleButtonCmdsCmdFileExecute.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonCmdsCmdFileExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonCmdsCmdFileExecuteActionPerformed(evt);
            }
        });

        jPanelCmdsCmdFile.add(jToggleButtonCmdsCmdFileExecute, new AbsoluteConstraints(350, 70, -1, 20));

        jToggleButtonCmdsCmdFileCancel.setText("cancel");
        jToggleButtonCmdsCmdFileCancel.setToolTipText("cancel all command files in progress");
        jToggleButtonCmdsCmdFileCancel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonCmdsCmdFileCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonCmdsCmdFileCancelActionPerformed(evt);
            }
        });

        jPanelCmdsCmdFile.add(jToggleButtonCmdsCmdFileCancel, new AbsoluteConstraints(270, 70, -1, 20));

        jPanelMainCmds.add(jPanelCmdsCmdFile, new AbsoluteConstraints(60, 140, 440, 100));

        jPanelCmdsCmdScope.setLayout(new AbsoluteLayout());

        jPanelCmdsCmdScope.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTextFieldCmdsCmdScope.setText("(edit scope command)");
        jTextFieldCmdsCmdScope.setToolTipText("edit the command for particulars before executing");
        jPanelCmdsCmdScope.add(jTextFieldCmdsCmdScope, new AbsoluteConstraints(10, 60, 420, -1));

        jToggleButtonCmdsCmdScopeExecute.setText("execute");
        jToggleButtonCmdsCmdScopeExecute.setToolTipText("execute the selected and edited scope command");
        jToggleButtonCmdsCmdScopeExecute.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonCmdsCmdScopeExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonCmdsCmdScopeExecuteActionPerformed(evt);
            }
        });

        jPanelCmdsCmdScope.add(jToggleButtonCmdsCmdScopeExecute, new AbsoluteConstraints(350, 90, -1, 20));

        jComboBoxCmdsCmdScope.setToolTipText("choose one of the scope commands");
        jComboBoxCmdsCmdScope.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxCmdsCmdScopeItemStateChanged(evt);
            }
        });

        jPanelCmdsCmdScope.add(jComboBoxCmdsCmdScope, new AbsoluteConstraints(10, 30, 340, -1));

        jLabelCmdsCmdScope.setText("select telescope command");
        jPanelCmdsCmdScope.add(jLabelCmdsCmdScope, new AbsoluteConstraints(10, 10, 170, -1));

        jPanelMainCmds.add(jPanelCmdsCmdScope, new AbsoluteConstraints(60, 290, 440, 120));

        jTabbedPaneMain.addTab("CmdFiles", jPanelMainCmds);

        jPanelMain.add(jTabbedPaneMain, new AbsoluteConstraints(0, 140, 580, 460));

        jPanelStatus.setLayout(new AbsoluteLayout());

        jPanelStatus.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jToggleButtonStatusCoordinates.setText("all coordinates");
        jToggleButtonStatusCoordinates.setToolTipText("displays status of all coordinates including encoders");
        jToggleButtonStatusCoordinates.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonStatusCoordinates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonStatusCoordinatesActionPerformed(evt);
            }
        });

        jPanelStatus.add(jToggleButtonStatusCoordinates, new AbsoluteConstraints(20, 40, 160, 20));

        jToggleButtonStatusInits.setText("initializations");
        jToggleButtonStatusInits.setToolTipText("displays status of initializations");
        jToggleButtonStatusInits.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonStatusInits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonStatusInitsActionPerformed(evt);
            }
        });

        jPanelStatus.add(jToggleButtonStatusInits, new AbsoluteConstraints(20, 60, 160, 20));

        jToggleButtonStatusControllerComm.setText("controller com");
        jToggleButtonStatusControllerComm.setToolTipText("displays status of servo controller communications");
        jToggleButtonStatusControllerComm.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonStatusControllerComm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonStatusControllerCommActionPerformed(evt);
            }
        });

        jPanelStatus.add(jToggleButtonStatusControllerComm, new AbsoluteConstraints(20, 80, 160, 20));

        jToggleButtonStatusServoAltDec.setText("alt/dec motor");
        jToggleButtonStatusServoAltDec.setToolTipText("displays status of altitude/declination motor");
        jToggleButtonStatusServoAltDec.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonStatusServoAltDec.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonStatusServoAltDecActionPerformed(evt);
            }
        });

        jPanelStatus.add(jToggleButtonStatusServoAltDec, new AbsoluteConstraints(20, 120, 160, 20));

        jToggleButtonStatusServoAzRa.setText("az/Ra motor");
        jToggleButtonStatusServoAzRa.setToolTipText("displays status of azimuth/right ascension motor");
        jToggleButtonStatusServoAzRa.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonStatusServoAzRa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonStatusServoAzRaActionPerformed(evt);
            }
        });

        jPanelStatus.add(jToggleButtonStatusServoAzRa, new AbsoluteConstraints(20, 140, 160, 20));

        jToggleButtonStatusLX200.setText("LX200 commands");
        jToggleButtonStatusLX200.setToolTipText("displays status of LX200 commands");
        jToggleButtonStatusLX200.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonStatusLX200.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonStatusLX200ActionPerformed(evt);
            }
        });

        jPanelStatus.add(jToggleButtonStatusLX200, new AbsoluteConstraints(20, 200, 160, 20));

        jLabelStatus.setText("status displays");
        jPanelStatus.add(jLabelStatus, new AbsoluteConstraints(60, 10, -1, -1));

        jToggleButtonStatusFR.setText("field rotate motor");
        jToggleButtonStatusFR.setToolTipText("displays status of field rotation motor");
        jToggleButtonStatusFR.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonStatusFR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonStatusFRActionPerformed(evt);
            }
        });

        jPanelStatus.add(jToggleButtonStatusFR, new AbsoluteConstraints(20, 160, 160, 20));

        jToggleButtonStatusFocus.setText("focus motor");
        jToggleButtonStatusFocus.setToolTipText("displays status of focus motor");
        jToggleButtonStatusFocus.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonStatusFocus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonStatusFocusActionPerformed(evt);
            }
        });

        jPanelStatus.add(jToggleButtonStatusFocus, new AbsoluteConstraints(20, 180, 160, 20));

        jToggleButtonStatusCmdHistory.setText("cmd history");
        jToggleButtonStatusCmdHistory.setToolTipText("a history of commands from all possible command sources");
        jToggleButtonStatusCmdHistory.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonStatusCmdHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonStatusCmdHistoryActionPerformed(evt);
            }
        });

        jPanelStatus.add(jToggleButtonStatusCmdHistory, new AbsoluteConstraints(20, 220, 160, -1));

        jToggleButtonStatusHandpad.setText("handpad");
        jToggleButtonStatusHandpad.setToolTipText("displays status of handpad");
        jToggleButtonStatusHandpad.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonStatusHandpad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonStatusHandpadActionPerformed(evt);
            }
        });

        jPanelStatus.add(jToggleButtonStatusHandpad, new AbsoluteConstraints(20, 100, 160, 20));

        jPanelMain.add(jPanelStatus, new AbsoluteConstraints(590, 340, 200, 250));

        getContentPane().add(jPanelMain, new AbsoluteConstraints(0, 0, 800, 600));

        /*
         * copy over contents of JFrameMain.initComponents() starting after instantiation of controls at line:
         * getContentPane().setLayout(new...
         * and ending just before final pack() statement;
         * remove references to jPanelHandpadPlaceHolder;
         */

        jPanelMain.add(jPanelHandpad, new AbsoluteConstraints(590, 40, 200, 280));

        jLabelTitle.setText("Scope II, the Java version, by Mel Bartels, build " + eString.BUILD_DATE);

        pack();
    }

    private void jToggleButtonStatusHandpadActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonStatusHandpad.setSelected(false);
        handpadStatusTimer.startTimer();
    }

    private void jToggleButtonControllerSiTechCfgActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonControllerSiTechCfg.setSelected(false);
        if (t != null) {
            t.pauseSequencer = true;
            // wait for sequencer()to finish and stall on pauseSequencer before sending switch to ASCII
            // command, otherwise getStatus() will be sent in sequencer();
            common.threadSleep(1000);
            t.extNOPSwitchToAsciiMode();
            JFrameSiTech = new JFrameSiTech();
            JFrameSiTech.registerTrackReference(t);
            JFrameSiTech.show();
        }
    }

    private void jToggleButtonControllerOpenTerminalWindowEncodersActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonControllerOpenTerminalWindowEncoders.setSelected(false);
        if (t != null) {
            if (t.ef.E != null && t.ef.E.portOpened()) {
                JFrameTerminalEncoders = new JFrameTerminalEncoders(t);
                JFrameTerminalEncoders.start();
            }
        }
    }

    private void jToggleButtonControllerOpenTerminalWindowMotorsActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonControllerOpenTerminalWindowMotors.setSelected(false);
        if (t != null) {
            t.pauseSequencer = true;
            // wait for sequencer()to finish and stall on pauseSequencer before sending switch to ASCII
            // command, otherwise getStatus() will be sent in sequencer();
            common.threadSleep(1000);
            t.extNOPSwitchToAsciiMode();
            JFrameTerminalMotors = new JFrameTerminal("motor controller terminal window");
            JFrameTerminalMotors.registerTrackReference(t);
            JFrameTerminalMotors.registerIOReference(t.io);
            JFrameTerminalMotors.appendReturn(cfg.getInstance().controllerManufacturer == CONTROLLER_MANUFACTURER.SiTech);
            JFrameTerminalMotors.start();
        }
    }

    private void jToggleButtonGotoMeridianFlipActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGotoMeridianFlip.setSelected(false);
        if (t != null)
            t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_slew_meridian_flip.toString());
    }

    private void jToggleButtonGotoHomeActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGotoHome.setSelected(false);
        if (t != null)
            t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_slew_home.toString());
    }

    private void jToggleButtonGotoSpiralSearchBeginActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGotoSpiralSearchBegin.setSelected(false);
        try {
            cfg.getInstance().spiralSearchRadiusDeg = Double.parseDouble(jTextFieldGotoSpiralSearchRad.getText());
        }
        catch (NumberFormatException nfe) {
            console.errOut("jTextFieldGotoSpiralSearchRad bad deg of " + jTextFieldGotoSpiralSearchRad.getText());
        }
        try {
            cfg.getInstance().spiralSearchSpeedDegSec = Double.parseDouble(jTextFieldGotoSpiralSearchSpeed.getText());
        }
        catch (NumberFormatException nfe) {
            console.errOut("jTextFieldGotoSpiralSearchSpeed bad deg of " + jTextFieldGotoSpiralSearchSpeed.getText());
        }
        if (t != null)
            t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_spiral_search_start.toString());
    }

    private void jToggleButtonGotoSpiralSearchEndActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGotoSpiralSearchEnd.setSelected(false);
        if (t != null)
            t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_spiral_search_stop.toString());
    }

    private void jToggleButtonStartupResetThresholdTelescopeOKActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonStartupResetThresholdTelescopeOK.setSelected(false);
        try {
            cfg.getInstance().encoderErrorThresholdDeg = Double.parseDouble(jTextFieldStartupResetThreshold.getText());
        }
        catch (NumberFormatException nfe) {
            console.errOut("jTextFieldStartupResetThreshold bad deg of " + jTextFieldStartupResetThreshold.getText());
        }
    }

    private void jRadioButtonCheckDirectionFocusReverseEncoderActionPerformed(java.awt.event.ActionEvent evt) {
        if (jRadioButtonCheckDirectionFocusReverseEncoder.isSelected())
            cfg.getInstance().encoderFocusDir = ROTATION.CCW;
        else
            cfg.getInstance().encoderFocusDir = ROTATION.CW;
    }

    private void jRadioButtonCheckDirectionFieldRReverseEncoderActionPerformed(java.awt.event.ActionEvent evt) {
        if (jRadioButtonCheckDirectionFieldRReverseEncoder.isSelected())
            cfg.getInstance().encoderFieldRDir = ROTATION.CCW;
        else
            cfg.getInstance().encoderFieldRDir = ROTATION.CW;
    }

    private void jRadioButtonCheckDirectionAzRaReverseEncoderActionPerformed(java.awt.event.ActionEvent evt) {
        if (jRadioButtonCheckDirectionAzRaReverseEncoder.isSelected())
            cfg.getInstance().encoderAzRaDir = ROTATION.CCW;
        else
            cfg.getInstance().encoderAzRaDir = ROTATION.CW;
    }

    private void jRadioButtonCheckDirectionAltDecReverseEncoderActionPerformed(java.awt.event.ActionEvent evt) {
        if (jRadioButtonCheckDirectionAltDecReverseEncoder.isSelected())
            cfg.getInstance().encoderAltDecDir = ROTATION.CCW;
        else
            cfg.getInstance().encoderAltDecDir = ROTATION.CW;
    }

    private void jRadioButtonSetCoordinatesSetMeridianFlipOnActionPerformed(java.awt.event.ActionEvent evt) {
        if (cfg.getInstance().Mount.meridianFlipPossible()) {
            jRadioButtonSetCoordinatesSetMeridianFlipOff.setSelected(false);
            if (!cfg.getInstance().Mount.meridianFlip().flipped)
                if (t != null)
                    t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_reset_meridian_flip.toString());
        }
        else {
            jRadioButtonSetCoordinatesSetMeridianFlipOn.setSelected(false);
            jRadioButtonSetCoordinatesSetMeridianFlipOff.setSelected(false);
        }
    }

    private void jRadioButtonSetCoordinatesSetMeridianFlipOffActionPerformed(java.awt.event.ActionEvent evt) {
        if (cfg.getInstance().Mount.meridianFlipPossible()) {
            jRadioButtonSetCoordinatesSetMeridianFlipOn.setSelected(false);
            if (cfg.getInstance().Mount.meridianFlip().flipped)
                if (t != null)
                    t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_reset_meridian_flip.toString());
        }
        else {
            jRadioButtonSetCoordinatesSetMeridianFlipOn.setSelected(false);
            jRadioButtonSetCoordinatesSetMeridianFlipOff.setSelected(false);
        }
    }

    private void jToggleButtonStartupResetEncodersToScopeActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonStartupResetEncodersToScope.setSelected(false);
        if (t != null)
            t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_reset_encoders_to_scope.toString());
    }

    private void jToggleButtonStartupResetScopeToEncodersActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonStartupResetScopeToEncoders.setSelected(false);
        if (t != null)
            t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_reset_scope_to_encoders.toString());
    }

    private void jToggleButtonControllerEncodersResetActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonControllerEncodersReset.setSelected(false);
        if (t != null)
            t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_reset_encoders_type.toString());
    }

    private void jToggleButtonControllerMotorResetActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonControllerMotorReset.setSelected(false);
        if (t != null)
            t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_start_motor_controllers.toString());
    }

    private void jRadioButtonCheckDirectionFocusReverseItemStateChanged(java.awt.event.ItemEvent evt) {
        cfg.getInstance().spf.reverseMotor = jRadioButtonCheckDirectionFocusReverse.isSelected();
    }

    private void jRadioButtonCheckDirectionFieldRReverseItemStateChanged(java.awt.event.ItemEvent evt) {
        cfg.getInstance().spr.reverseMotor = jRadioButtonCheckDirectionFieldRReverse.isSelected();
    }

    private void jRadioButtonCheckDirectionAzRaReverseItemStateChanged(java.awt.event.ItemEvent evt) {
        cfg.getInstance().spz.reverseMotor = jRadioButtonCheckDirectionAzRaReverse.isSelected();
    }

    private void jRadioButtonCheckDirectionAltDecReverseItemStateChanged(java.awt.event.ItemEvent evt) {
        cfg.getInstance().spa.reverseMotor = jRadioButtonCheckDirectionAltDecReverse.isSelected();
    }

    private void jToggleButtonCheckDirectionFocusActionPerformed(java.awt.event.ActionEvent evt) {
        stopButtonsOffMediator.off();
        if (t != null) {
            if (cfg.getInstance().handpadMode != HANDPAD_MODE.handpadModeFRFocus)
                cfg.getInstance().handpadMode = HANDPAD_MODE.handpadModeFRFocus;
            t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_handpad_CW_fast.toString());
        }
    }

    private void jToggleButtonCheckDirectionFieldRActionPerformed(java.awt.event.ActionEvent evt) {
        stopButtonsOffMediator.off();
        if (t != null) {
            if (cfg.getInstance().handpadMode != HANDPAD_MODE.handpadModeFRFocus)
                cfg.getInstance().handpadMode = HANDPAD_MODE.handpadModeFRFocus;
            t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_handpad_up_fast.toString());
        }
    }

    private void jToggleButtonCheckDirectionAzRaActionPerformed(java.awt.event.ActionEvent evt) {
        stopButtonsOffMediator.off();
        if (t != null) {
            if (cfg.getInstance().handpadMode == HANDPAD_MODE.handpadModeFRFocus)
                cfg.getInstance().handpadMode = HANDPAD_MODE.handpadModeOff;
            t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_handpad_CW_fast.toString());
        }
    }

    private void jToggleButtonCheckDirectionAltDecActionPerformed(java.awt.event.ActionEvent evt) {
        stopButtonsOffMediator.off();
        if (t != null) {
            if (cfg.getInstance().handpadMode == HANDPAD_MODE.handpadModeFRFocus)
                cfg.getInstance().handpadMode = HANDPAD_MODE.handpadModeOff;
            t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_handpad_up_fast.toString());
        }
    }

    private void jToggleButtonCheckDirectionStopActionPerformed(java.awt.event.ActionEvent evt) {
        if (t != null && jToggleButtonCheckDirectionStop.isSelected())
            t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_stop.toString());
        motionButtonsOffMediator.off();
    }

    private void jToggleButtonStepRevOKActionPerformed(java.awt.event.ActionEvent evt) {
        double steps;
        azInt az = new azInt();

        jToggleButtonStepRevOK.setSelected(false);

        try {
            steps = Double.parseDouble(jTextFieldStepRevMotorAltDec.getText());
            if (steps != cfg.getInstance().spa.stepsPerRev) {
                cfg.getInstance().spa.stepsPerRev = steps;
                if (t != null)
                    t.cmdCol.UICmd.newCmd("UI",
                    CMD_SCOPE.cmd_scope_init_servo_vars.toString()
                    + " "
                    + SERVO_ID.altDec.KEY);
            }
        }
        catch (NumberFormatException nfe) {
            console.errOut("jTextFieldStepRevMotorAltDec bad number " + jTextFieldStepRevMotorAltDec.getText());
        }

        try {
            steps = Double.parseDouble(jTextFieldStepRevMotorAzRa.getText());
            if (steps != cfg.getInstance().spz.stepsPerRev) {
                cfg.getInstance().spz.stepsPerRev = steps;
                if (t != null)
                    t.cmdCol.UICmd.newCmd("UI",
                    CMD_SCOPE.cmd_scope_init_servo_vars.toString()
                    + " "
                    + SERVO_ID.azRa.KEY);
            }
        }
        catch (NumberFormatException nfe) {
            console.errOut("jTextFieldStepRevMotorAzRa bad number " + jTextFieldStepRevMotorAzRa.getText());
        }

        try {
            steps = Double.parseDouble(jTextFieldStepRevMotorFieldR.getText());
            if (steps != cfg.getInstance().spr.stepsPerRev) {
                cfg.getInstance().spr.stepsPerRev = steps;
                if (t != null)
                    t.cmdCol.UICmd.newCmd("UI",
                    CMD_SCOPE.cmd_scope_init_servo_vars.toString()
                    + " "
                    + SERVO_ID.fieldR.KEY);
            }
        }
        catch (NumberFormatException nfe) {
            console.errOut("jTextFieldStepRevMotorFieldR bad number " + jTextFieldStepRevMotorFieldR.getText());
        }

        try {
            steps = Double.parseDouble(jTextFieldStepRevMotorFocus.getText());
            if (steps != cfg.getInstance().spf.stepsPerRev) {
                cfg.getInstance().spf.stepsPerRev = steps;
                if (t != null)
                    t.cmdCol.UICmd.newCmd("UI",
                    CMD_SCOPE.cmd_scope_init_servo_vars.toString()
                    + " "
                    + SERVO_ID.focus.KEY);
            }
        }
        catch (NumberFormatException nfe) {
            console.errOut("jTextFieldStepRevMotorFocus bad number " + jTextFieldStepRevMotorFocus.getText());
        }

        try {
            cfg.getInstance().encoderAltDecCountsPerRev = Integer.parseInt(jTextFieldStepRevEncoderAltDec.getText());
            cfg.getInstance().encoderAzRaCountsPerRev= Integer.parseInt(jTextFieldStepRevEncoderAzRa.getText());
        }
        catch (NumberFormatException nfe) {
            console.errOut("encoderCountsRev bad number(s)");
        }
    }

    private void jComboBoxControllerMotorCommPortItemStateChanged(java.awt.event.ItemEvent evt) {
        // only fire once, and don't fire when jComboBox model loaded
        if (evt.getStateChange() == evt.DESELECTED)
            if (t != null) {
                t.cmdCol.UICmd.newCmd("UI",
                CMD_SCOPE.cmd_scope_cfg_parm.toString()
                + " "
                + CFG_PARM_NAME.servoSerialPortName
                + " "
                + jComboBoxControllerMotorCommPort.getSelectedItem());

                t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_open_motor_controller_ioport.toString());

                t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_start_motor_controllers.toString());
            }
    }

    private void jComboBoxControllerEncoderCommPortItemStateChanged(java.awt.event.ItemEvent evt) {
        // only fire once, and don't fire when jComboBox model loaded
        if (evt.getStateChange() == evt.DESELECTED)
            if (t != null) {
                t.cmdCol.UICmd.newCmd("UI",
                CMD_SCOPE.cmd_scope_cfg_parm.toString()
                + " "
                + CFG_PARM_NAME.encoderSerialPortName
                + " "
                + jComboBoxControllerEncoderCommPort.getSelectedItem());

                t.cmdCol.UICmd.newCmd("UI",
                CMD_SCOPE.cmd_scope_encoder_type.toString()
                + " "
                + jComboBoxEncodersControllerType.getSelectedItem());
            }
    }

    private void jComboBoxMotorControllerTypeItemStateChanged(java.awt.event.ItemEvent evt) {
        // only fire once, and don't fire when jComboBox model loaded
        if (evt.getStateChange() == evt.DESELECTED) {
            cfg.getInstance().controllerManufacturer = (CONTROLLER_MANUFACTURER) jComboBoxMotorControllerType.getSelectedItem();
            if (cfg.getInstance().handpadDesign != HANDPAD_DESIGN.handpadDesignSiTech) {
                cfg.getInstance().handpadDesign = HANDPAD_DESIGN.handpadDesignSiTech;
                cfg.getInstance().handpadPresent = true;
            }
        }
    }

    private void jComboBoxEncodersControllerTypeItemStateChanged(java.awt.event.ItemEvent evt) {
        // only fire once, and don't fire when jComboBox model loaded
        if (evt.getStateChange() == evt.DESELECTED)
            if (t != null)
                t.cmdCol.UICmd.newCmd("UI",
                CMD_SCOPE.cmd_scope_encoder_type.toString()
                + " "
                + jComboBoxEncodersControllerType.getSelectedItem());
    }

    private void jToggleButtonStatusFocusActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonStatusFocus.setSelected(false);
        statusFrameCollection.setVisible(STATUS_TYPE.StatusTypeServoFocus, true);
    }

    private void jToggleButtonStatusFRActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonStatusFR.setSelected(false);
        statusFrameCollection.setVisible(STATUS_TYPE.StatusTypeServoFieldR, true);
    }

    private void jToggleButtonAtAGlanceSetAnyCoordinateActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonAtAGlanceSetAnyCoordinate.setSelected(false);
        launchJFrameGetCoordinate(PANEL_GET_COORDINATE_TYPE.setInput);
    }

    private void jToggleButtonStartupSiteOKActionPerformed(java.awt.event.ActionEvent evt) {
        cfg.getInstance().siteName = jTextFieldStartupSiteName.getText();
        try {
            cfg.getInstance().latitudeDeg = Double.parseDouble(jTextFieldStartupSiteLatitude.getText());
        }
        catch (NumberFormatException nfe) {
            console.errOut("jTextFieldStartupSiteLatitude bad deg of " + jTextFieldStartupSiteLatitude.getText());
        }
        try {
            cfg.getInstance().longitudeDeg = Double.parseDouble(jTextFieldStartupSiteLongitude.getText());
        }
        catch (NumberFormatException nfe) {
            console.errOut("jTextFieldStartupSiteLongitude bad deg of " + jTextFieldStartupSiteLongitude.getText());
        }
    }

    private void jToggleButtonStartupTelescopeOKActionPerformed(java.awt.event.ActionEvent evt) {
        if (cfg.getInstance().Mount.meridianFlipPossible())
            try {
                cfg.getInstance().Mount.meridianFlip().autoFuzzDeg = Double.parseDouble(jTextFieldStartupAutoMeridianFlipFuzzDeg.getText());
                cfg.getInstance().Mount.meridianFlip().autoFuzzRad = cfg.getInstance().Mount.meridianFlip().autoFuzzDeg*units.DEG_TO_RAD;
            }
            catch (NumberFormatException nfe) {
                console.errOut("jTextFieldStartupAutoMeridianFlipFuzzDeg bad deg of " + jTextFieldStartupAutoMeridianFlipFuzzDeg.getText());
            }
    }

    private void jToggleButtonStatusServoAltDecActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonStatusServoAltDec.setSelected(false);
        statusFrameCollection.setVisible(STATUS_TYPE.StatusTypeServoAltDec, true);
    }

    private void jToggleButtonStatusControllerCommActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonStatusControllerComm.setSelected(false);
        statusFrameCollection.setVisible(STATUS_TYPE.StatusTypeServoComm, true);
    }

    private void jToggleButtonStatusLX200ActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonStatusLX200.setSelected(false);
        statusFrameCollection.setVisible(STATUS_TYPE.StatusTypeLX200, true);
    }

    private void jToggleButtonStatusCmdHistoryActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonStatusCmdHistory.setSelected(false);
        statusFrameCollection.setVisible(STATUS_TYPE.StatusTypeCmdHistory, true);
    }

    private void jToggleButtonStatusInitsActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonStatusInits.setSelected(false);
        statusFrameCollection.setVisible(STATUS_TYPE.StatusTypeInits, true);
    }

    private void jToggleButtonStatusCoordinatesActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonStatusCoordinates.setSelected(false);
        statusFrameCollection.setVisible(STATUS_TYPE.StatusTypeCoordinates, true);
    }

    private void jToggleButtonStatusServoAzRaActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonStatusServoAzRa.setSelected(false);
        statusFrameCollection.setVisible(STATUS_TYPE.StatusTypeServoAzRa, true);
    }

    private void jToggleButtonGotoNearestObjectActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGotoNearestObject.setSelected(false);
        if (t != null) {
            dtc = new dataFileChooser(new JFrame(), "open a datafile to search in");
            if (dtc.fileSelected) {
                t.cmdCol.UICmd.newCmd("UI",
                CMD_SCOPE.cmd_scope_slew_nearest_object.toString()
                + " "
                + dtc.file.getAbsoluteFile());
            }
        }
    }

    private void jToggleButtonGotoNextNearestObjectActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGotoNextNearestObject.setSelected(false);
        if (t != null) {
            dtc = new dataFileChooser(new JFrame(), "open a datafile to search in");
            if (dtc.fileSelected) {
                t.cmdCol.UICmd.newCmd("UI",
                CMD_SCOPE.cmd_scope_slew_nearest_object_avoid_input.toString()
                + " "
                + dtc.file.getAbsoluteFile());
            }
        }
    }

    private void jToggleButtonGotoNearestObjectNotInputActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGotoNearestObjectNotInput.setSelected(false);
        if (t != null) {
            dtc = new dataFileChooser(new JFrame(), "open a datafile to search in");
            if (dtc.fileSelected) {
                t.cmdCol.UICmd.newCmd("UI",
                CMD_SCOPE.cmd_scope_slew_nearest_object_not_in_input_file.toString()
                + " "
                + dtc.file.getAbsoluteFile());
            }
        }
    }

    private void jToggleButtonGotoViewInputHistoryActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGotoViewInputHistory.setSelected(false);
        JFrameFileViewer JFrameFileViewer = new JFrameFileViewer("input history", eString.INPUT_FILENAME);
    }

    private void jToggleButtonGotoNewTargetCoordinatesActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGotoNewTargetCoordinates.setSelected(false);
        launchJFrameGetCoordinate(PANEL_GET_COORDINATE_TYPE.newTarget);
    }

    private void jToggleButtonCmdsCmdFileCancelActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonCmdsCmdFileCancel.setSelected(false);
        if (cmdScopeListMethods.fileSelected())
            JFrameCmdScope.cancel();
    }

    private void jToggleButtonCmdsCmdFileExecuteActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonCmdsCmdFileExecute.setSelected(false);
        if (cmdScopeListMethods.fileSelected())
            JFrameCmdScope.execute();
    }

    /**
     * cmdScopeListMethods is registered with JFrameCmdScope;
     * call JFrameCmdScope.load() to ensure that form is initialized;
     * both this class JFrameMain and class JFrameCmdScope can now call cmdScopeListMethods methods;
     */
    private void jToggleButtonCmdsCmdFileActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonCmdsCmdFile.setSelected(false);
        if (JFrameCmdScope.load()) 
            jTextFieldCmdsCmdFileFilename.setText(cmdScopeListMethods.selectedFilename());
    }

    /**
     * grandTourListMethods is registered with JFrameGrandTour;
     * call JFrameGrandTour.load() to ensure that form is initialized;
     * both this class JFrameMain and class JFrameGrandTour can now call grandTourListMethods methods;
     */
    private void jToggleButtonCmdsGrandTourActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonCmdsGrandTour.setSelected(false);
        if (JFrameGrandTour.load()) 
            jTextFieldCmdsGrandTour.setText(grandTourListMethods.selectedFilename());
    }

    private void jToggleButtonCmdsGrandTourNearestActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonCmdsGrandTourNearest.setSelected(false);
        if (grandTourListMethods.fileSelected())
            JFrameGrandTour.nearest();
    }

    private void jToggleButtonCmdsCmdScopeExecuteActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonCmdsCmdScopeExecute.setSelected(false);
        if (t != null)
            t.cmdCol.UICmd.newCmd("UI", jTextFieldCmdsCmdScope.getText());
    }

    private void jComboBoxCmdsCmdScopeItemStateChanged(java.awt.event.ItemEvent evt) {
        // only fire once, and don't fire when jComboBox model loaded
        if (evt.getStateChange() == evt.DESELECTED)
            jTextFieldCmdsCmdScope.setText(((CMD_SCOPE) jComboBoxCmdsCmdScope.getSelectedItem()).description);
    }

    private void jToggleButtonInitializationViewInitHistoryActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonInitializationViewInitHistory.setSelected(false);
        JFrameFileViewer JFrameFileViewer = new JFrameFileViewer("initialization history", eString.PGM_NAME + eString.INITHIST_EXT);
    }

    private void jToggleButtonInitializationReInitActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonInitializationReInit.setSelected(false);
        if (t != null)
            t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_reinit.toString());
    }

    private void jToggleButtonInitializationFixAltOffsetActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonInitializationFixAltOffset.setSelected(false);
        if (t != null && cfg.getInstance().initialized())
            t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_alt_offset.toString());
    }

    private void jToggleButtonInitializationUseClosestActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonInitializationUseClosest.setSelected(false);
        if (t != null)
            t.cmdCol.UICmd.newCmd("UI",
            CMD_SCOPE.cmd_scope_init_using_closest_inithist_analysis.toString());
    }

    private void jToggleButtonInitializationInit3ActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonInitializationInit3.setSelected(false);
        if (t != null) {
            launchJFrameGetCoordinate(PANEL_GET_COORDINATE_TYPE.init3);
            cfg.getInstance().three.init = false;
            cfg.getInstance().handpadMode = HANDPAD_MODE.handpadModeInit3;
        }
    }

    private void jToggleButtonInitializationInit2ActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonInitializationInit2.setSelected(false);
        if (t != null) {
            launchJFrameGetCoordinate(PANEL_GET_COORDINATE_TYPE.autoInit2);
            cfg.getInstance().two.init = false;
        }
    }

    private void jToggleButtonInitializationInit1ActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonInitializationInit1.setSelected(false);
        if (t != null) {
            launchJFrameGetCoordinate(PANEL_GET_COORDINATE_TYPE.autoInit1);
            cfg.getInstance().one.init = false;
        }
    }

    private void jComboBoxInitializationStartInitStateItemStateChanged(java.awt.event.ItemEvent evt) {
        // only fire once, and don't fire when jComboBox model loaded
        if (evt.getStateChange() == evt.DESELECTED) {
            cfg.getInstance().initState = (INIT_STATE) jComboBoxInitializationStartInitState.getSelectedItem();
            if (t != null) {
                t.cmdCol.UICmd.newCmd("UI",
                CMD_SCOPE.cmd_scope_initState.toString()
                + " "
                + cfg.getInstance().initState);
            }
        }
    }

    private void jToggleButtonStartupResetCurrentCoordinatesActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonStartupResetCurrentCoordinates.setSelected(false);
        launchJFrameGetCoordinate(PANEL_GET_COORDINATE_TYPE.resetCurrent);
    }

    private void jRadioButtonStartupAutoMeridianFlipActionPerformed(java.awt.event.ActionEvent evt) {
        if (cfg.getInstance().Mount.meridianFlipPossible())
            cfg.getInstance().Mount.meridianFlip().auto = jRadioButtonStartupAutoMeridianFlip.isSelected();
    }

    private void jRadioButtonStartupMeridianFlipRequiredActionPerformed(java.awt.event.ActionEvent evt) {
        if (cfg.getInstance().Mount.meridianFlipPossible())
            cfg.getInstance().Mount.meridianFlip().required = jRadioButtonStartupMeridianFlipRequired.isSelected();
    }

    private void jRadioButtonStartupMeridianFlipPossibleActionPerformed(java.awt.event.ActionEvent evt) {
        cfg.getInstance().Mount.meridianFlipPossible(jRadioButtonStartupMeridianFlipPossible.isSelected());
        if (!cfg.getInstance().Mount.meridianFlipPossible()) {
            jRadioButtonStartupMeridianFlipRequired.setSelected(false);
            jRadioButtonStartupAutoMeridianFlip.setSelected(false);
        }
    }

    private void jRadioButtonStartupPrimaryAxisFullyRotatesActionPerformed(java.awt.event.ActionEvent evt) {
        cfg.getInstance().Mount.primaryAxisFullyRotates(jRadioButtonStartupPrimaryAxisFullyRotates.isSelected());
    }

    private void jRadioButtonStartupCanMoveThruPoleActionPerformed(java.awt.event.ActionEvent evt) {
        cfg.getInstance().Mount.canMoveThruPole(jRadioButtonStartupCanMoveThruPole.isSelected());
    }

    private void jRadioButtonStartupCanMoveToPoleActionPerformed(java.awt.event.ActionEvent evt) {
        cfg.getInstance().Mount.canMoveToPole(jRadioButtonStartupCanMoveToPole.isSelected());
    }

    private void jComboBoxStartupMountTypeItemStateChanged(java.awt.event.ItemEvent evt) {
        // only fire once, and don't fire when jComboBox model loaded
        if (evt.getStateChange() == evt.DESELECTED)
            if ( jComboBoxStartupMountType.getSelectedItem() != cfg.getInstance().Mount.mountType()) {
                cfg.getInstance().Mount = new mountFactory().build((MOUNT_TYPE) jComboBoxStartupMountType.getSelectedItem());
                mountTypeParmsMediator(cfg.getInstance().Mount);
            }
    }

    private void mountTypeParmsMediator(Mount Mount) {
        if (Mount.meridianFlipPossible()) {
            jRadioButtonStartupAutoMeridianFlip.setSelected(Mount.meridianFlip().auto);
            jRadioButtonStartupMeridianFlipRequired.setSelected(Mount.meridianFlip().required);
            jTextFieldStartupAutoMeridianFlipFuzzDeg.setText(eString.doubleToStringNoGrouping(Mount.meridianFlip().autoFuzzDeg, 2, 1));
        }
        else {
            jRadioButtonStartupAutoMeridianFlip.setSelected(false);
            jRadioButtonStartupMeridianFlipRequired.setSelected(false);
            jTextFieldStartupAutoMeridianFlipFuzzDeg.setText("");
        }
        jRadioButtonStartupMeridianFlipPossible.setSelected(Mount.meridianFlipPossible());
        jRadioButtonStartupPrimaryAxisFullyRotates.setSelected(Mount.primaryAxisFullyRotates());
        jRadioButtonStartupCanMoveThruPole.setSelected(Mount.canMoveThruPole());
        jRadioButtonStartupCanMoveToPole.setSelected(Mount.canMoveToPole());
    }

    private void jToggleButtonAtAGlanceNewTargetCoordActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonAtAGlanceResetCurrentCoord.setSelected(false);
        jToggleButtonAtAGlanceNewTargetCoord.setSelected(false);
        launchJFrameGetCoordinate(PANEL_GET_COORDINATE_TYPE.newTarget);

    }

    private void jToggleButtonAtAGlanceResetCurrentCoordActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonAtAGlanceResetCurrentCoord.setSelected(false);
        jToggleButtonAtAGlanceNewTargetCoord.setSelected(false);
        launchJFrameGetCoordinate(PANEL_GET_COORDINATE_TYPE.resetCurrent);
    }

    private void launchJFrameGetCoordinate(PANEL_GET_COORDINATE_TYPE getCoordinateType) {
        if (JFrameGetCoordinate == null)
            JFrameGetCoordinate = new JFrameGetCoordinate();
        else
            JFrameGetCoordinate.setVisible(true);
        JFrameGetCoordinate.registerTrackReference(t);
        JFrameGetCoordinate.updateGetCoordinateType(getCoordinateType);
    }

    private void jToggleButtonAtAGlanceStopActionPerformed(java.awt.event.ActionEvent evt) {
        if (t != null && jToggleButtonAtAGlanceStop.isSelected())
            t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_stop.toString());
        motionButtonsOffMediator.off();
    }

    private void jToggleButtonAtAGlanceTrackActionPerformed(java.awt.event.ActionEvent evt) {
        if (t != null)
            if (jToggleButtonAtAGlanceTrack.isSelected())  {
                stopButtonsOffMediator.off();
                t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_trackon.toString());
            }
            else
                t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_stop.toString());
    }

    private void jToggleButtonMainFileSaveCfgActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonMainFileSaveCfg.setSelected(false);
        cfg.getInstance().write();
    }

    private void jToggleButtonMainExitActionPerformed(java.awt.event.ActionEvent evt) {
        exit();
    }

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {
        exit();
    }

    private void exit() {
        jPanelHandpad.close();

        if (t != null)
            t.cmdCol.UICmd.newCmd("UI", CMD_SCOPE.cmd_scope_quit.toString());
        else
            System.exit(0);
    }
}

