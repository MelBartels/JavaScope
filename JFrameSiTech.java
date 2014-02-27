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
 * configuration and operation frame for Sidereal Technology controller
 */
public class JFrameSiTech extends javax.swing.JFrame {
    SiTechAsciiCmd SiTechAsciiCmd = new SiTechAsciiCmd();
    track t;

    // VALID_RETURN_BYTE_COUNT includes 128 data bytes + 2 checksum bytes; need 3 more for the SC + enter command
    final int B_ARRAY_SIZE = SiTechCfg.VALID_RETURN_BYTE_COUNT+3;
    byte[] bArray = new byte[B_ARRAY_SIZE];

    JFrameTerminal JFrameTerminalMotors;
    SiTechFirmwareUploadFileChooser SiTechFirmwareUploadFileChooser;

    // Variables declaration - do not modify
    private javax.swing.JComboBox jComboBoxCommPort;
    private javax.swing.JComboBox jComboBoxModuleSetAddress;
    private javax.swing.JLabel jLabelCPUTemperature;
    private javax.swing.JLabel jLabelFirmwareSerialNumber;
    private javax.swing.JLabel jLabelFirmwareVersion;
    private javax.swing.JLabel jLabelGeneral;
    private javax.swing.JLabel jLabelKeypad;
    private javax.swing.JLabel jLabelModuleAddress;
    private javax.swing.JLabel jLabelModuleLatitude;
    private javax.swing.JLabel jLabelModulePICServoTimeOut;
    private javax.swing.JLabel jLabelModulePlatformGoal;
    private javax.swing.JLabel jLabelModulePlatformRate;
    private javax.swing.JLabel jLabelModulePlatformUpDn;
    private javax.swing.JLabel jLabelModuleXCurrentLimit;
    private javax.swing.JLabel jLabelModuleXDerivative;
    private javax.swing.JLabel jLabelModuleXEncoderCountsPerRevolution;
    private javax.swing.JLabel jLabelModuleXEncoderPosition;
    private javax.swing.JLabel jLabelModuleXIntegral;
    private javax.swing.JLabel jLabelModuleXIntegralLimit;
    private javax.swing.JLabel jLabelModuleXMaxPositionError;
    private javax.swing.JLabel jLabelModuleXMotorCountsPerRevolution;
    private javax.swing.JLabel jLabelModuleXMotorCurrent;
    private javax.swing.JLabel jLabelModuleXMotorGuideRate;
    private javax.swing.JLabel jLabelModuleXMotorPanRate;
    private javax.swing.JLabel jLabelModuleXMotorPosition;
    private javax.swing.JLabel jLabelModuleXMotorSlewRate;
    private javax.swing.JLabel jLabelModuleXOutputLimit;
    private javax.swing.JLabel jLabelModuleXPWM;
    private javax.swing.JLabel jLabelModuleXPositionError;
    private javax.swing.JLabel jLabelModuleXProportional;
    private javax.swing.JLabel jLabelModuleXRamp;
    private javax.swing.JLabel jLabelModuleXVelocity;
    private javax.swing.JLabel jLabelModuleYCurrentLimit;
    private javax.swing.JLabel jLabelModuleYDerivative;
    private javax.swing.JLabel jLabelModuleYEncoderCountsPerRevolution;
    private javax.swing.JLabel jLabelModuleYEncoderPosition;
    private javax.swing.JLabel jLabelModuleYIntegral;
    private javax.swing.JLabel jLabelModuleYIntegralLimit;
    private javax.swing.JLabel jLabelModuleYMaxPositionError;
    private javax.swing.JLabel jLabelModuleYMotorCountsPerRevolution;
    private javax.swing.JLabel jLabelModuleYMotorCurrent;
    private javax.swing.JLabel jLabelModuleYMotorGuideRate;
    private javax.swing.JLabel jLabelModuleYMotorPanRate;
    private javax.swing.JLabel jLabelModuleYMotorPosition;
    private javax.swing.JLabel jLabelModuleYMotorSlewRate;
    private javax.swing.JLabel jLabelModuleYOutputLimit;
    private javax.swing.JLabel jLabelModuleYPWM;
    private javax.swing.JLabel jLabelModuleYPositionError;
    private javax.swing.JLabel jLabelModuleYProportional;
    private javax.swing.JLabel jLabelModuleYRamp;
    private javax.swing.JLabel jLabelModuleYVelocity;
    private javax.swing.JLabel jLabelQ;
    private javax.swing.JLabel jLabelReturn;
    private javax.swing.JLabel jLabelStatusComm;
    private javax.swing.JLabel jLabelStatusGetAllStatus;
    private javax.swing.JLabel jLabelStatusReturn;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelVoltage;
    private javax.swing.JLabel jLabelXEncoder;
    private javax.swing.JLabel jLabelXMotor;
    private javax.swing.JLabel jLabelYEncoder;
    private javax.swing.JLabel jLabelYMotor;
    private javax.swing.JPanel jPanelSiTech;
    private javax.swing.JRadioButton jRadioButtonDragAndTrack;
    private javax.swing.JRadioButton jRadioButtonEnableHandpad;
    private javax.swing.JRadioButton jRadioButtonEncodersActive;
    private javax.swing.JRadioButton jRadioButtonEquatorialPlatform;
    private javax.swing.JRadioButton jRadioButtonGuideMode;
    private javax.swing.JRadioButton jRadioButtonHandpadBiDir;
    private javax.swing.JRadioButton jRadioButtonSlewAndTrack;
    private javax.swing.JRadioButton jRadioButtonXInvertEncoderDirection;
    private javax.swing.JRadioButton jRadioButtonXInvertMotorDirection;
    private javax.swing.JRadioButton jRadioButtonXInvertServoEncoderDirection;
    private javax.swing.JRadioButton jRadioButtonYInvertEncoderDirection;
    private javax.swing.JRadioButton jRadioButtonYInvertMotorDirection;
    private javax.swing.JRadioButton jRadioButtonYInvertServoEncoderDirection;
    private javax.swing.JTextField jTextFieldCPUTemperature;
    private javax.swing.JTextField jTextFieldKeypad;
    private javax.swing.JTextField jTextFieldModuleAddress;
    private javax.swing.JTextField jTextFieldModuleLatitude;
    private javax.swing.JTextField jTextFieldModulePICServoTimeOut;
    private javax.swing.JTextField jTextFieldModulePlatformGoal;
    private javax.swing.JTextField jTextFieldModulePlatformRate;
    private javax.swing.JTextField jTextFieldModulePlatformUpDn;
    private javax.swing.JTextField jTextFieldModuleXCurrentLimit;
    private javax.swing.JTextField jTextFieldModuleXDerivative;
    private javax.swing.JTextField jTextFieldModuleXEncoderCountsPerRevolution;
    private javax.swing.JTextField jTextFieldModuleXEncoderPosition;
    private javax.swing.JTextField jTextFieldModuleXIntegral;
    private javax.swing.JTextField jTextFieldModuleXIntegralLimit;
    private javax.swing.JTextField jTextFieldModuleXMaxPositionError;
    private javax.swing.JTextField jTextFieldModuleXMotorCountsPerRevolution;
    private javax.swing.JTextField jTextFieldModuleXMotorCurrent;
    private javax.swing.JTextField jTextFieldModuleXMotorGuideRate;
    private javax.swing.JTextField jTextFieldModuleXMotorPanRate;
    private javax.swing.JTextField jTextFieldModuleXMotorSlewRate;
    private javax.swing.JTextField jTextFieldModuleXMoveToPosition;
    private javax.swing.JTextField jTextFieldModuleXOutputLimit;
    private javax.swing.JTextField jTextFieldModuleXPWM;
    private javax.swing.JTextField jTextFieldModuleXPosition;
    private javax.swing.JTextField jTextFieldModuleXPositionError;
    private javax.swing.JTextField jTextFieldModuleXProportional;
    private javax.swing.JTextField jTextFieldModuleXRamp;
    private javax.swing.JTextField jTextFieldModuleXVelocity;
    private javax.swing.JTextField jTextFieldModuleYCurrentLimit;
    private javax.swing.JTextField jTextFieldModuleYDerivative;
    private javax.swing.JTextField jTextFieldModuleYEncoderCountsPerRevolution;
    private javax.swing.JTextField jTextFieldModuleYEncoderPosition;
    private javax.swing.JTextField jTextFieldModuleYIntegral;
    private javax.swing.JTextField jTextFieldModuleYIntegralLimit;
    private javax.swing.JTextField jTextFieldModuleYMaxPositionError;
    private javax.swing.JTextField jTextFieldModuleYMotorCountsPerRevolution;
    private javax.swing.JTextField jTextFieldModuleYMotorCurrent;
    private javax.swing.JTextField jTextFieldModuleYMotorGuideRate;
    private javax.swing.JTextField jTextFieldModuleYMotorPanRate;
    private javax.swing.JTextField jTextFieldModuleYMotorSlewRate;
    private javax.swing.JTextField jTextFieldModuleYMoveToPosition;
    private javax.swing.JTextField jTextFieldModuleYOutputLimit;
    private javax.swing.JTextField jTextFieldModuleYPWM;
    private javax.swing.JTextField jTextFieldModuleYPosition;
    private javax.swing.JTextField jTextFieldModuleYPositionError;
    private javax.swing.JTextField jTextFieldModuleYProportional;
    private javax.swing.JTextField jTextFieldModuleYRamp;
    private javax.swing.JTextField jTextFieldModuleYVelocity;
    private javax.swing.JTextField jTextFieldQ;
    private javax.swing.JTextField jTextFieldReturn;
    private javax.swing.JTextField jTextFieldSerialNumber;
    private javax.swing.JTextField jTextFieldVersion;
    private javax.swing.JTextField jTextFieldVoltage;
    private javax.swing.JToggleButton jToggleButtonConnect;
    private javax.swing.JToggleButton jToggleButtonExit;
    private javax.swing.JToggleButton jToggleButtonGetAll;
    private javax.swing.JToggleButton jToggleButtonGetCPUTemperature;
    private javax.swing.JToggleButton jToggleButtonGetDragAndTrack;
    private javax.swing.JToggleButton jToggleButtonGetEnableHandpad;
    private javax.swing.JToggleButton jToggleButtonGetEquatorialPlatform;
    private javax.swing.JToggleButton jToggleButtonGetFirmwareVersion;
    private javax.swing.JToggleButton jToggleButtonGetGuideMode;
    private javax.swing.JToggleButton jToggleButtonGetHandpadBiDir;
    private javax.swing.JToggleButton jToggleButtonGetKeypad;
    private javax.swing.JToggleButton jToggleButtonGetPICServoTimeOut;
    private javax.swing.JToggleButton jToggleButtonGetPlatformGoal;
    private javax.swing.JToggleButton jToggleButtonGetPlatformRate;
    private javax.swing.JToggleButton jToggleButtonGetPlatformUpDn;
    private javax.swing.JToggleButton jToggleButtonGetQ;
    private javax.swing.JToggleButton jToggleButtonGetReturn;
    private javax.swing.JToggleButton jToggleButtonGetSerialNumber;
    private javax.swing.JToggleButton jToggleButtonGetSlewAndTrack;
    private javax.swing.JToggleButton jToggleButtonGetVoltage;
    private javax.swing.JToggleButton jToggleButtonGetXDerivative;
    private javax.swing.JToggleButton jToggleButtonGetXEncoderCountsPerRevolution;
    private javax.swing.JToggleButton jToggleButtonGetXEncoderPosition;
    private javax.swing.JToggleButton jToggleButtonGetXIntegral;
    private javax.swing.JToggleButton jToggleButtonGetXIntegralLimit;
    private javax.swing.JToggleButton jToggleButtonGetXInvertEncoderDirection;
    private javax.swing.JToggleButton jToggleButtonGetXInvertMotorDirection;
    private javax.swing.JToggleButton jToggleButtonGetXMaxPositionError;
    private javax.swing.JToggleButton jToggleButtonGetXMotorCountsPerRevolution;
    private javax.swing.JToggleButton jToggleButtonGetXMotorCurrent;
    private javax.swing.JToggleButton jToggleButtonGetXMotorGuideRate;
    private javax.swing.JToggleButton jToggleButtonGetXMotorPanRate;
    private javax.swing.JToggleButton jToggleButtonGetXMotorSlewRate;
    private javax.swing.JToggleButton jToggleButtonGetXPWM;
    private javax.swing.JToggleButton jToggleButtonGetXPosition;
    private javax.swing.JToggleButton jToggleButtonGetXPositionError;
    private javax.swing.JToggleButton jToggleButtonGetXProportional;
    private javax.swing.JToggleButton jToggleButtonGetXRamp;
    private javax.swing.JToggleButton jToggleButtonGetXServoEncoderDirection;
    private javax.swing.JToggleButton jToggleButtonGetXVelocity;
    private javax.swing.JToggleButton jToggleButtonGetYDerivative;
    private javax.swing.JToggleButton jToggleButtonGetYEncoderCountsPerRevolution;
    private javax.swing.JToggleButton jToggleButtonGetYEncoderPosition;
    private javax.swing.JToggleButton jToggleButtonGetYIntegral;
    private javax.swing.JToggleButton jToggleButtonGetYIntegralLimit;
    private javax.swing.JToggleButton jToggleButtonGetYInvertEncoderDirection;
    private javax.swing.JToggleButton jToggleButtonGetYInvertMotorDirection;
    private javax.swing.JToggleButton jToggleButtonGetYMaxPositionError;
    private javax.swing.JToggleButton jToggleButtonGetYMotorCountsPerRevolution;
    private javax.swing.JToggleButton jToggleButtonGetYMotorCurrent;
    private javax.swing.JToggleButton jToggleButtonGetYMotorGuideRate;
    private javax.swing.JToggleButton jToggleButtonGetYMotorPanRate;
    private javax.swing.JToggleButton jToggleButtonGetYMotorSlewRate;
    private javax.swing.JToggleButton jToggleButtonGetYPWM;
    private javax.swing.JToggleButton jToggleButtonGetYPosition;
    private javax.swing.JToggleButton jToggleButtonGetYPositionError;
    private javax.swing.JToggleButton jToggleButtonGetYProportional;
    private javax.swing.JToggleButton jToggleButtonGetYRamp;
    private javax.swing.JToggleButton jToggleButtonGetYServoEncoderDirection;
    private javax.swing.JToggleButton jToggleButtonGetYVelocity;
    private javax.swing.JToggleButton jToggleButtonMainSaveScopeIICfg;
    private javax.swing.JToggleButton jToggleButtonModuleGetLatitude;
    private javax.swing.JToggleButton jToggleButtonOpenTerminalWindow;
    private javax.swing.JToggleButton jToggleButtonProgramFactoryDefaultsIntoFlashROM;
    private javax.swing.JToggleButton jToggleButtonReadCfgFromFlashROM;
    private javax.swing.JToggleButton jToggleButtonResetController;
    private javax.swing.JToggleButton jToggleButtonSetAll;
    private javax.swing.JToggleButton jToggleButtonSetDragAndTrack;
    private javax.swing.JToggleButton jToggleButtonSetEnableHandpad;
    private javax.swing.JToggleButton jToggleButtonSetEquatorialPlatform;
    private javax.swing.JToggleButton jToggleButtonSetGuideMode;
    private javax.swing.JToggleButton jToggleButtonSetHandpadBiDir;
    private javax.swing.JToggleButton jToggleButtonSetLatitude;
    private javax.swing.JToggleButton jToggleButtonSetModuleAddress;
    private javax.swing.JToggleButton jToggleButtonSetPICServoTimeOut;
    private javax.swing.JToggleButton jToggleButtonSetPlatformGoal;
    private javax.swing.JToggleButton jToggleButtonSetPlatformRate;
    private javax.swing.JToggleButton jToggleButtonSetPlatformUpDn;
    private javax.swing.JToggleButton jToggleButtonSetSlewAndTrack;
    private javax.swing.JToggleButton jToggleButtonSetXCurrentLimit;
    private javax.swing.JToggleButton jToggleButtonSetXDerivative;
    private javax.swing.JToggleButton jToggleButtonSetXEncoderCountsPerRevolution;
    private javax.swing.JToggleButton jToggleButtonSetXEncoderPosition;
    private javax.swing.JToggleButton jToggleButtonSetXIntegral;
    private javax.swing.JToggleButton jToggleButtonSetXIntegralLimit;
    private javax.swing.JToggleButton jToggleButtonSetXInvertEncoderDirection;
    private javax.swing.JToggleButton jToggleButtonSetXInvertMotorDirection;
    private javax.swing.JToggleButton jToggleButtonSetXMaxPositionError;
    private javax.swing.JToggleButton jToggleButtonSetXMotorCountsPerRevolution;
    private javax.swing.JToggleButton jToggleButtonSetXMotorGuideRate;
    private javax.swing.JToggleButton jToggleButtonSetXMotorPanRate;
    private javax.swing.JToggleButton jToggleButtonSetXMotorSlewRate;
    private javax.swing.JToggleButton jToggleButtonSetXOutputLimit;
    private javax.swing.JToggleButton jToggleButtonSetXPWM;
    private javax.swing.JToggleButton jToggleButtonSetXPosition;
    private javax.swing.JToggleButton jToggleButtonSetXProportional;
    private javax.swing.JToggleButton jToggleButtonSetXRamp;
    private javax.swing.JToggleButton jToggleButtonSetXServoEncoderDirection;
    private javax.swing.JToggleButton jToggleButtonSetXVelocity;
    private javax.swing.JToggleButton jToggleButtonSetYCurrentLimit;
    private javax.swing.JToggleButton jToggleButtonSetYDerivative;
    private javax.swing.JToggleButton jToggleButtonSetYEncoderCountsPerRevolution;
    private javax.swing.JToggleButton jToggleButtonSetYEncoderPosition;
    private javax.swing.JToggleButton jToggleButtonSetYIntegral;
    private javax.swing.JToggleButton jToggleButtonSetYIntegralLimit;
    private javax.swing.JToggleButton jToggleButtonSetYInvertEncoderDirection;
    private javax.swing.JToggleButton jToggleButtonSetYInvertMotorDirection;
    private javax.swing.JToggleButton jToggleButtonSetYMaxPositionError;
    private javax.swing.JToggleButton jToggleButtonSetYMotorCountsPerRevolution;
    private javax.swing.JToggleButton jToggleButtonSetYMotorGuideRate;
    private javax.swing.JToggleButton jToggleButtonSetYMotorPanRate;
    private javax.swing.JToggleButton jToggleButtonSetYMotorSlewRate;
    private javax.swing.JToggleButton jToggleButtonSetYOutputLimit;
    private javax.swing.JToggleButton jToggleButtonSetYPWM;
    private javax.swing.JToggleButton jToggleButtonSetYPosition;
    private javax.swing.JToggleButton jToggleButtonSetYProportional;
    private javax.swing.JToggleButton jToggleButtonSetYRamp;
    private javax.swing.JToggleButton jToggleButtonSetYServoEncoderDirection;
    private javax.swing.JToggleButton jToggleButtonSetYVelocity;
    private javax.swing.JToggleButton jToggleButtonStopAllMotors;
    private javax.swing.JToggleButton jToggleButtonUpgradeFirmware;
    private javax.swing.JToggleButton jToggleButtonVelocityCalculator;
    private javax.swing.JToggleButton jToggleButtonWriteCfgToFlashROM;
    private javax.swing.JToggleButton jToggleButtonXAutoPWM;
    private javax.swing.JToggleButton jToggleButtonXEmergencyStop;
    private javax.swing.JToggleButton jToggleButtonXMove;
    private javax.swing.JToggleButton jToggleButtonXNormalStop;
    private javax.swing.JToggleButton jToggleButtonYAutoPWM;
    private javax.swing.JToggleButton jToggleButtonYEmergencyStop;
    private javax.swing.JToggleButton jToggleButtonYMove;
    private javax.swing.JToggleButton jToggleButtonYNormalStop;
    // End of variables declaration

    /** Creates new form JFrame */
    public JFrameSiTech() {
        int ix;
        boolean commPortFound = false;

        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
        initComponents();
        screenPlacement.getInstance().center(this);
        jLabelTitle.setText("Sidereal Technology controller configuration, build " + eString.BUILD_DATE);

        setCommStatus();
        setReturnStatus();
        setGetAllStatus();

        for (ix = 0; ix < ioSerial.commPortList.size(); ix++) {
            jComboBoxCommPort.addItem(ioSerial.commPortList.get(ix));
            if (ioSerial.commPortList.get(ix) == cfg.getInstance().servoSerialPortName)
                commPortFound = true;
        }
        if (commPortFound)
            jComboBoxCommPort.setSelectedItem(cfg.getInstance().servoSerialPortName);
        else {
            jComboBoxCommPort.setSelectedIndex(0);
            cfg.getInstance().servoSerialPortName = jComboBoxCommPort.getSelectedItem().toString();
        }
        
        jComboBoxModuleSetAddress.addItem("1");
        jComboBoxModuleSetAddress.addItem("3");
        jComboBoxModuleSetAddress.addItem("5");

        if (cfg.getInstance().encoderType == ENCODER_TYPE.encoderSiTech)
            jRadioButtonEncodersActive.setSelected(true);
        else
            jRadioButtonEncodersActive.setSelected(false);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {
        jPanelSiTech = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jComboBoxCommPort = new javax.swing.JComboBox();
        jToggleButtonResetController = new javax.swing.JToggleButton();
        jToggleButtonOpenTerminalWindow = new javax.swing.JToggleButton();
        jLabelFirmwareVersion = new javax.swing.JLabel();
        jToggleButtonGetFirmwareVersion = new javax.swing.JToggleButton();
        jLabelStatusReturn = new javax.swing.JLabel();
        jLabelVoltage = new javax.swing.JLabel();
        jToggleButtonGetVoltage = new javax.swing.JToggleButton();
        jLabelCPUTemperature = new javax.swing.JLabel();
        jToggleButtonGetCPUTemperature = new javax.swing.JToggleButton();
        jToggleButtonProgramFactoryDefaultsIntoFlashROM = new javax.swing.JToggleButton();
        jToggleButtonWriteCfgToFlashROM = new javax.swing.JToggleButton();
        jToggleButtonReadCfgFromFlashROM = new javax.swing.JToggleButton();
        jLabelModuleAddress = new javax.swing.JLabel();
        jToggleButtonSetModuleAddress = new javax.swing.JToggleButton();
        jLabelKeypad = new javax.swing.JLabel();
        jTextFieldKeypad = new javax.swing.JTextField();
        jToggleButtonGetKeypad = new javax.swing.JToggleButton();
        jToggleButtonXEmergencyStop = new javax.swing.JToggleButton();
        jToggleButtonXNormalStop = new javax.swing.JToggleButton();
        jToggleButtonYNormalStop = new javax.swing.JToggleButton();
        jToggleButtonYEmergencyStop = new javax.swing.JToggleButton();
        jLabelModuleXMotorPosition = new javax.swing.JLabel();
        jTextFieldModuleXPosition = new javax.swing.JTextField();
        jToggleButtonGetXPosition = new javax.swing.JToggleButton();
        jToggleButtonSetXPosition = new javax.swing.JToggleButton();
        jLabelModuleYMotorPosition = new javax.swing.JLabel();
        jTextFieldModuleYPosition = new javax.swing.JTextField();
        jToggleButtonGetYPosition = new javax.swing.JToggleButton();
        jToggleButtonSetYPosition = new javax.swing.JToggleButton();
        jToggleButtonXMove = new javax.swing.JToggleButton();
        jToggleButtonYMove = new javax.swing.JToggleButton();
        jLabelModuleXVelocity = new javax.swing.JLabel();
        jTextFieldModuleXVelocity = new javax.swing.JTextField();
        jToggleButtonSetXVelocity = new javax.swing.JToggleButton();
        jLabelModuleYVelocity = new javax.swing.JLabel();
        jTextFieldModuleYVelocity = new javax.swing.JTextField();
        jToggleButtonSetYVelocity = new javax.swing.JToggleButton();
        jLabelModuleXRamp = new javax.swing.JLabel();
        jTextFieldModuleXRamp = new javax.swing.JTextField();
        jToggleButtonSetXRamp = new javax.swing.JToggleButton();
        jLabelModuleYRamp = new javax.swing.JLabel();
        jTextFieldModuleYRamp = new javax.swing.JTextField();
        jToggleButtonSetYRamp = new javax.swing.JToggleButton();
        jLabelModuleXMotorCurrent = new javax.swing.JLabel();
        jTextFieldModuleXMotorCurrent = new javax.swing.JTextField();
        jToggleButtonGetXMotorCurrent = new javax.swing.JToggleButton();
        jLabelModuleYMotorCurrent = new javax.swing.JLabel();
        jTextFieldModuleYMotorCurrent = new javax.swing.JTextField();
        jToggleButtonGetYMotorCurrent = new javax.swing.JToggleButton();
        jLabelModuleXMaxPositionError = new javax.swing.JLabel();
        jTextFieldModuleXMaxPositionError = new javax.swing.JTextField();
        jToggleButtonSetXMaxPositionError = new javax.swing.JToggleButton();
        jLabelModuleYMaxPositionError = new javax.swing.JLabel();
        jTextFieldModuleYMaxPositionError = new javax.swing.JTextField();
        jToggleButtonSetYMaxPositionError = new javax.swing.JToggleButton();
        jTextFieldVersion = new javax.swing.JTextField();
        jTextFieldVoltage = new javax.swing.JTextField();
        jTextFieldCPUTemperature = new javax.swing.JTextField();
        jLabelModuleXProportional = new javax.swing.JLabel();
        jTextFieldModuleXProportional = new javax.swing.JTextField();
        jToggleButtonSetXProportional = new javax.swing.JToggleButton();
        jLabelModuleXDerivative = new javax.swing.JLabel();
        jTextFieldModuleXDerivative = new javax.swing.JTextField();
        jToggleButtonSetXDerivative = new javax.swing.JToggleButton();
        jLabelModuleXIntegral = new javax.swing.JLabel();
        jTextFieldModuleXIntegral = new javax.swing.JTextField();
        jToggleButtonSetXIntegral = new javax.swing.JToggleButton();
        jLabelModuleXIntegralLimit = new javax.swing.JLabel();
        jTextFieldModuleXIntegralLimit = new javax.swing.JTextField();
        jToggleButtonGetXIntegralLimit = new javax.swing.JToggleButton();
        jToggleButtonSetXIntegralLimit = new javax.swing.JToggleButton();
        jLabelModuleXCurrentLimit = new javax.swing.JLabel();
        jTextFieldModuleXCurrentLimit = new javax.swing.JTextField();
        jToggleButtonSetXCurrentLimit = new javax.swing.JToggleButton();
        jLabelModuleXOutputLimit = new javax.swing.JLabel();
        jTextFieldModuleXOutputLimit = new javax.swing.JTextField();
        jToggleButtonSetXOutputLimit = new javax.swing.JToggleButton();
        jLabelModuleXPWM = new javax.swing.JLabel();
        jTextFieldModuleXPWM = new javax.swing.JTextField();
        jToggleButtonGetXPWM = new javax.swing.JToggleButton();
        jToggleButtonSetXPWM = new javax.swing.JToggleButton();
        jLabelXMotor = new javax.swing.JLabel();
        jLabelYMotor = new javax.swing.JLabel();
        jToggleButtonXAutoPWM = new javax.swing.JToggleButton();
        jLabelModuleYProportional = new javax.swing.JLabel();
        jTextFieldModuleYProportional = new javax.swing.JTextField();
        jToggleButtonSetYProportional = new javax.swing.JToggleButton();
        jToggleButtonSetYDerivative = new javax.swing.JToggleButton();
        jToggleButtonSetYIntegral = new javax.swing.JToggleButton();
        jToggleButtonSetYIntegralLimit = new javax.swing.JToggleButton();
        jToggleButtonSetYCurrentLimit = new javax.swing.JToggleButton();
        jToggleButtonSetYOutputLimit = new javax.swing.JToggleButton();
        jToggleButtonSetYPWM = new javax.swing.JToggleButton();
        jToggleButtonGetYPWM = new javax.swing.JToggleButton();
        jToggleButtonGetYIntegralLimit = new javax.swing.JToggleButton();
        jTextFieldModuleYDerivative = new javax.swing.JTextField();
        jTextFieldModuleYIntegral = new javax.swing.JTextField();
        jTextFieldModuleYIntegralLimit = new javax.swing.JTextField();
        jTextFieldModuleYCurrentLimit = new javax.swing.JTextField();
        jTextFieldModuleYOutputLimit = new javax.swing.JTextField();
        jTextFieldModuleYPWM = new javax.swing.JTextField();
        jLabelModuleYDerivative = new javax.swing.JLabel();
        jLabelModuleYIntegral = new javax.swing.JLabel();
        jLabelModuleYIntegralLimit = new javax.swing.JLabel();
        jLabelModuleYCurrentLimit = new javax.swing.JLabel();
        jLabelModuleYOutputLimit = new javax.swing.JLabel();
        jLabelModuleYPWM = new javax.swing.JLabel();
        jToggleButtonYAutoPWM = new javax.swing.JToggleButton();
        jRadioButtonXInvertMotorDirection = new javax.swing.JRadioButton();
        jToggleButtonGetXInvertMotorDirection = new javax.swing.JToggleButton();
        jToggleButtonSetXInvertMotorDirection = new javax.swing.JToggleButton();
        jRadioButtonYInvertMotorDirection = new javax.swing.JRadioButton();
        jToggleButtonGetYInvertMotorDirection = new javax.swing.JToggleButton();
        jToggleButtonSetYInvertMotorDirection = new javax.swing.JToggleButton();
        jLabelXEncoder = new javax.swing.JLabel();
        jRadioButtonXInvertEncoderDirection = new javax.swing.JRadioButton();
        jToggleButtonGetXInvertEncoderDirection = new javax.swing.JToggleButton();
        jToggleButtonSetXInvertEncoderDirection = new javax.swing.JToggleButton();
        jLabelModuleXEncoderPosition = new javax.swing.JLabel();
        jTextFieldModuleXEncoderPosition = new javax.swing.JTextField();
        jToggleButtonGetXEncoderPosition = new javax.swing.JToggleButton();
        jToggleButtonSetXEncoderPosition = new javax.swing.JToggleButton();
        jLabelYEncoder = new javax.swing.JLabel();
        jRadioButtonYInvertEncoderDirection = new javax.swing.JRadioButton();
        jToggleButtonGetYInvertEncoderDirection = new javax.swing.JToggleButton();
        jToggleButtonSetYInvertEncoderDirection = new javax.swing.JToggleButton();
        jToggleButtonSetYEncoderPosition = new javax.swing.JToggleButton();
        jToggleButtonGetYEncoderPosition = new javax.swing.JToggleButton();
        jTextFieldModuleYEncoderPosition = new javax.swing.JTextField();
        jLabelModuleYEncoderPosition = new javax.swing.JLabel();
        jLabelModuleXMotorCountsPerRevolution = new javax.swing.JLabel();
        jTextFieldModuleXMotorCountsPerRevolution = new javax.swing.JTextField();
        jToggleButtonGetXMotorCountsPerRevolution = new javax.swing.JToggleButton();
        jToggleButtonSetXMotorCountsPerRevolution = new javax.swing.JToggleButton();
        jLabelModuleYMotorCountsPerRevolution = new javax.swing.JLabel();
        jTextFieldModuleYMotorCountsPerRevolution = new javax.swing.JTextField();
        jToggleButtonGetYMotorCountsPerRevolution = new javax.swing.JToggleButton();
        jToggleButtonSetYMotorCountsPerRevolution = new javax.swing.JToggleButton();
        jLabelModuleXEncoderCountsPerRevolution = new javax.swing.JLabel();
        jTextFieldModuleXEncoderCountsPerRevolution = new javax.swing.JTextField();
        jToggleButtonGetXEncoderCountsPerRevolution = new javax.swing.JToggleButton();
        jToggleButtonSetXEncoderCountsPerRevolution = new javax.swing.JToggleButton();
        jLabelModuleYEncoderCountsPerRevolution = new javax.swing.JLabel();
        jTextFieldModuleYEncoderCountsPerRevolution = new javax.swing.JTextField();
        jToggleButtonGetYEncoderCountsPerRevolution = new javax.swing.JToggleButton();
        jToggleButtonSetYEncoderCountsPerRevolution = new javax.swing.JToggleButton();
        jRadioButtonSlewAndTrack = new javax.swing.JRadioButton();
        jToggleButtonGetSlewAndTrack = new javax.swing.JToggleButton();
        jToggleButtonSetSlewAndTrack = new javax.swing.JToggleButton();
        jRadioButtonEquatorialPlatform = new javax.swing.JRadioButton();
        jToggleButtonGetEquatorialPlatform = new javax.swing.JToggleButton();
        jToggleButtonSetEquatorialPlatform = new javax.swing.JToggleButton();
        jRadioButtonEnableHandpad = new javax.swing.JRadioButton();
        jToggleButtonGetEnableHandpad = new javax.swing.JToggleButton();
        jToggleButtonSetEnableHandpad = new javax.swing.JToggleButton();
        jRadioButtonHandpadBiDir = new javax.swing.JRadioButton();
        jToggleButtonGetHandpadBiDir = new javax.swing.JToggleButton();
        jToggleButtonSetHandpadBiDir = new javax.swing.JToggleButton();
        jRadioButtonDragAndTrack = new javax.swing.JRadioButton();
        jToggleButtonGetDragAndTrack = new javax.swing.JToggleButton();
        jToggleButtonSetDragAndTrack = new javax.swing.JToggleButton();
        jToggleButtonExit = new javax.swing.JToggleButton();
        jLabelGeneral = new javax.swing.JLabel();
        jLabelStatusComm = new javax.swing.JLabel();
        jToggleButtonConnect = new javax.swing.JToggleButton();
        jComboBoxModuleSetAddress = new javax.swing.JComboBox();
        jRadioButtonGuideMode = new javax.swing.JRadioButton();
        jToggleButtonGetGuideMode = new javax.swing.JToggleButton();
        jToggleButtonSetGuideMode = new javax.swing.JToggleButton();
        jRadioButtonXInvertServoEncoderDirection = new javax.swing.JRadioButton();
        jToggleButtonGetXServoEncoderDirection = new javax.swing.JToggleButton();
        jToggleButtonSetXServoEncoderDirection = new javax.swing.JToggleButton();
        jRadioButtonYInvertServoEncoderDirection = new javax.swing.JRadioButton();
        jToggleButtonGetYServoEncoderDirection = new javax.swing.JToggleButton();
        jToggleButtonSetYServoEncoderDirection = new javax.swing.JToggleButton();
        jLabelModulePlatformUpDn = new javax.swing.JLabel();
        jTextFieldModulePlatformUpDn = new javax.swing.JTextField();
        jToggleButtonGetPlatformUpDn = new javax.swing.JToggleButton();
        jToggleButtonSetPlatformUpDn = new javax.swing.JToggleButton();
        jLabelModulePlatformRate = new javax.swing.JLabel();
        jTextFieldModulePlatformRate = new javax.swing.JTextField();
        jToggleButtonGetPlatformRate = new javax.swing.JToggleButton();
        jToggleButtonSetPlatformRate = new javax.swing.JToggleButton();
        jLabelModulePlatformGoal = new javax.swing.JLabel();
        jTextFieldModulePlatformGoal = new javax.swing.JTextField();
        jToggleButtonGetPlatformGoal = new javax.swing.JToggleButton();
        jToggleButtonSetPlatformGoal = new javax.swing.JToggleButton();
        jLabelModuleXMotorGuideRate = new javax.swing.JLabel();
        jTextFieldModuleXMotorGuideRate = new javax.swing.JTextField();
        jToggleButtonGetXMotorGuideRate = new javax.swing.JToggleButton();
        jToggleButtonSetXMotorGuideRate = new javax.swing.JToggleButton();
        jLabelModuleXMotorPanRate = new javax.swing.JLabel();
        jTextFieldModuleXMotorPanRate = new javax.swing.JTextField();
        jToggleButtonGetXMotorPanRate = new javax.swing.JToggleButton();
        jToggleButtonSetXMotorPanRate = new javax.swing.JToggleButton();
        jLabelModuleXMotorSlewRate = new javax.swing.JLabel();
        jTextFieldModuleXMotorSlewRate = new javax.swing.JTextField();
        jToggleButtonGetXMotorSlewRate = new javax.swing.JToggleButton();
        jToggleButtonSetXMotorSlewRate = new javax.swing.JToggleButton();
        jLabelModuleYMotorGuideRate = new javax.swing.JLabel();
        jTextFieldModuleYMotorGuideRate = new javax.swing.JTextField();
        jToggleButtonGetYMotorGuideRate = new javax.swing.JToggleButton();
        jToggleButtonSetYMotorGuideRate = new javax.swing.JToggleButton();
        jLabelModuleYMotorPanRate = new javax.swing.JLabel();
        jTextFieldModuleYMotorPanRate = new javax.swing.JTextField();
        jToggleButtonGetYMotorPanRate = new javax.swing.JToggleButton();
        jToggleButtonSetYMotorPanRate = new javax.swing.JToggleButton();
        jLabelModuleYMotorSlewRate = new javax.swing.JLabel();
        jTextFieldModuleYMotorSlewRate = new javax.swing.JTextField();
        jToggleButtonGetYMotorSlewRate = new javax.swing.JToggleButton();
        jToggleButtonSetYMotorSlewRate = new javax.swing.JToggleButton();
        jTextFieldModuleXMoveToPosition = new javax.swing.JTextField();
        jTextFieldModuleYMoveToPosition = new javax.swing.JTextField();
        jToggleButtonGetXProportional = new javax.swing.JToggleButton();
        jToggleButtonGetXDerivative = new javax.swing.JToggleButton();
        jToggleButtonGetXIntegral = new javax.swing.JToggleButton();
        jToggleButtonGetYProportional = new javax.swing.JToggleButton();
        jToggleButtonGetYDerivative = new javax.swing.JToggleButton();
        jToggleButtonGetYIntegral = new javax.swing.JToggleButton();
        jLabelFirmwareSerialNumber = new javax.swing.JLabel();
        jTextFieldSerialNumber = new javax.swing.JTextField();
        jToggleButtonGetSerialNumber = new javax.swing.JToggleButton();
        jLabelModuleLatitude = new javax.swing.JLabel();
        jTextFieldModuleLatitude = new javax.swing.JTextField();
        jToggleButtonModuleGetLatitude = new javax.swing.JToggleButton();
        jLabelModulePICServoTimeOut = new javax.swing.JLabel();
        jTextFieldModulePICServoTimeOut = new javax.swing.JTextField();
        jToggleButtonGetPICServoTimeOut = new javax.swing.JToggleButton();
        jToggleButtonSetPICServoTimeOut = new javax.swing.JToggleButton();
        jToggleButtonSetLatitude = new javax.swing.JToggleButton();
        jToggleButtonGetAll = new javax.swing.JToggleButton();
        jToggleButtonSetAll = new javax.swing.JToggleButton();
        jLabelStatusGetAllStatus = new javax.swing.JLabel();
        jLabelModuleXPositionError = new javax.swing.JLabel();
        jTextFieldModuleXPositionError = new javax.swing.JTextField();
        jToggleButtonGetXPositionError = new javax.swing.JToggleButton();
        jLabelModuleYPositionError = new javax.swing.JLabel();
        jTextFieldModuleYPositionError = new javax.swing.JTextField();
        jToggleButtonGetYPositionError = new javax.swing.JToggleButton();
        jTextFieldModuleAddress = new javax.swing.JTextField();
        jTextFieldQ = new javax.swing.JTextField();
        jLabelQ = new javax.swing.JLabel();
        jToggleButtonGetQ = new javax.swing.JToggleButton();
        jToggleButtonMainSaveScopeIICfg = new javax.swing.JToggleButton();
        jRadioButtonEncodersActive = new javax.swing.JRadioButton();
        jToggleButtonVelocityCalculator = new javax.swing.JToggleButton();
        jToggleButtonUpgradeFirmware = new javax.swing.JToggleButton();
        jToggleButtonGetXVelocity = new javax.swing.JToggleButton();
        jToggleButtonGetYVelocity = new javax.swing.JToggleButton();
        jToggleButtonGetXRamp = new javax.swing.JToggleButton();
        jToggleButtonGetYRamp = new javax.swing.JToggleButton();
        jToggleButtonGetYMaxPositionError = new javax.swing.JToggleButton();
        jToggleButtonGetXMaxPositionError = new javax.swing.JToggleButton();
        jToggleButtonStopAllMotors = new javax.swing.JToggleButton();
        jLabelReturn = new javax.swing.JLabel();
        jTextFieldReturn = new javax.swing.JTextField();
        jToggleButtonGetReturn = new javax.swing.JToggleButton();

        getContentPane().setLayout(new AbsoluteLayout());

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        jPanelSiTech.setLayout(new AbsoluteLayout());

        jPanelSiTech.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitle.setText("Sidereal Technology controller configuration");
        jLabelTitle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanelSiTech.add(jLabelTitle, new AbsoluteConstraints(202, 10, 420, -1));

        jComboBoxCommPort.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxCommPortItemStateChanged(evt);
            }
        });

        jPanelSiTech.add(jComboBoxCommPort, new AbsoluteConstraints(10, 50, 70, 20));

        jToggleButtonResetController.setText("reset controller");
        jToggleButtonResetController.setToolTipText("reset the controller");
        jToggleButtonResetController.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonResetController.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonResetControllerActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonResetController, new AbsoluteConstraints(40, 80, -1, -1));

        jToggleButtonOpenTerminalWindow.setText("open terminal window");
        jToggleButtonOpenTerminalWindow.setToolTipText("open ASCII terminal window to controller");
        jToggleButtonOpenTerminalWindow.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonOpenTerminalWindow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonOpenTerminalWindowActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonOpenTerminalWindow, new AbsoluteConstraints(130, 220, -1, -1));

        jLabelFirmwareVersion.setText("version");
        jPanelSiTech.add(jLabelFirmwareVersion, new AbsoluteConstraints(40, 480, -1, -1));

        jToggleButtonGetFirmwareVersion.setText("get");
        jToggleButtonGetFirmwareVersion.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetFirmwareVersion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetFirmwareVersionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetFirmwareVersion, new AbsoluteConstraints(190, 480, -1, -1));

        jLabelStatusReturn.setText("return status");
        jLabelStatusReturn.setToolTipText("communications return value status: green if valid return, red if not");
        jPanelSiTech.add(jLabelStatusReturn, new AbsoluteConstraints(140, 80, -1, -1));

        jLabelVoltage.setText("voltage");
        jPanelSiTech.add(jLabelVoltage, new AbsoluteConstraints(40, 520, -1, -1));

        jToggleButtonGetVoltage.setText("get");
        jToggleButtonGetVoltage.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetVoltage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetVoltageActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetVoltage, new AbsoluteConstraints(190, 520, -1, -1));

        jLabelCPUTemperature.setText("CPU temp.");
        jPanelSiTech.add(jLabelCPUTemperature, new AbsoluteConstraints(20, 540, -1, -1));

        jToggleButtonGetCPUTemperature.setText("get");
        jToggleButtonGetCPUTemperature.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetCPUTemperature.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetCPUTemperatureActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetCPUTemperature, new AbsoluteConstraints(190, 540, -1, -1));

        jToggleButtonProgramFactoryDefaultsIntoFlashROM.setText("program factory defaults to flash rom");
        jToggleButtonProgramFactoryDefaultsIntoFlashROM.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonProgramFactoryDefaultsIntoFlashROM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonProgramFactoryDefaultsIntoFlashROMActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonProgramFactoryDefaultsIntoFlashROM, new AbsoluteConstraints(10, 140, -1, -1));

        jToggleButtonWriteCfgToFlashROM.setText("write to flash rom");
        jToggleButtonWriteCfgToFlashROM.setToolTipText("do this to preserve values that have been ''set'");
        jToggleButtonWriteCfgToFlashROM.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonWriteCfgToFlashROM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonWriteCfgToFlashROMActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonWriteCfgToFlashROM, new AbsoluteConstraints(10, 160, -1, -1));

        jToggleButtonReadCfgFromFlashROM.setText("read from flash rom");
        jToggleButtonReadCfgFromFlashROM.setToolTipText("do this to read values that have been ''set'");
        jToggleButtonReadCfgFromFlashROM.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonReadCfgFromFlashROM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonReadCfgFromFlashROMActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonReadCfgFromFlashROM, new AbsoluteConstraints(140, 160, -1, -1));

        jLabelModuleAddress.setText("module address");
        jPanelSiTech.add(jLabelModuleAddress, new AbsoluteConstraints(110, 120, 100, -1));

        jToggleButtonSetModuleAddress.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetModuleAddress.setText("set");
        jToggleButtonSetModuleAddress.setToolTipText("set the module address based on the combo box value");
        jToggleButtonSetModuleAddress.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetModuleAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetModuleAddressActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetModuleAddress, new AbsoluteConstraints(220, 120, 20, -1));

        jLabelKeypad.setText("keypad");
        jPanelSiTech.add(jLabelKeypad, new AbsoluteConstraints(40, 560, -1, -1));

        jTextFieldKeypad.setToolTipText("current keypad or data input value");
        jPanelSiTech.add(jTextFieldKeypad, new AbsoluteConstraints(90, 560, 90, -1));

        jToggleButtonGetKeypad.setText("get");
        jToggleButtonGetKeypad.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetKeypad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetKeypadActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetKeypad, new AbsoluteConstraints(190, 560, -1, -1));

        jToggleButtonXEmergencyStop.setBackground(new java.awt.Color(255, 51, 0));
        jToggleButtonXEmergencyStop.setText("emergency stop");
        jToggleButtonXEmergencyStop.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonXEmergencyStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonXEmergencyStopActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonXEmergencyStop, new AbsoluteConstraints(400, 50, -1, -1));

        jToggleButtonXNormalStop.setBackground(new java.awt.Color(255, 51, 0));
        jToggleButtonXNormalStop.setText("normal stop");
        jToggleButtonXNormalStop.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonXNormalStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonXNormalStopActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonXNormalStop, new AbsoluteConstraints(310, 50, -1, -1));

        jToggleButtonYNormalStop.setBackground(new java.awt.Color(255, 51, 0));
        jToggleButtonYNormalStop.setText("normal stop");
        jToggleButtonYNormalStop.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonYNormalStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonYNormalStopActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonYNormalStop, new AbsoluteConstraints(590, 50, -1, -1));

        jToggleButtonYEmergencyStop.setBackground(new java.awt.Color(255, 51, 0));
        jToggleButtonYEmergencyStop.setText("emergency stop");
        jToggleButtonYEmergencyStop.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonYEmergencyStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonYEmergencyStopActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonYEmergencyStop, new AbsoluteConstraints(680, 50, -1, -1));

        jLabelModuleXMotorPosition.setText("position");
        jPanelSiTech.add(jLabelModuleXMotorPosition, new AbsoluteConstraints(310, 180, -1, -1));

        jTextFieldModuleXPosition.setToolTipText("motor position");
        jPanelSiTech.add(jTextFieldModuleXPosition, new AbsoluteConstraints(360, 180, 90, -1));

        jToggleButtonGetXPosition.setText("get");
        jToggleButtonGetXPosition.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetXPosition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetXPositionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetXPosition, new AbsoluteConstraints(460, 180, -1, -1));

        jToggleButtonSetXPosition.setText("set");
        jToggleButtonSetXPosition.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetXPosition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetXPositionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetXPosition, new AbsoluteConstraints(490, 180, -1, -1));

        jLabelModuleYMotorPosition.setText("position");
        jPanelSiTech.add(jLabelModuleYMotorPosition, new AbsoluteConstraints(590, 180, -1, -1));

        jTextFieldModuleYPosition.setToolTipText("motor position");
        jPanelSiTech.add(jTextFieldModuleYPosition, new AbsoluteConstraints(640, 180, 90, -1));

        jToggleButtonGetYPosition.setText("get");
        jToggleButtonGetYPosition.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetYPosition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetYPositionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetYPosition, new AbsoluteConstraints(740, 180, -1, -1));

        jToggleButtonSetYPosition.setText("set");
        jToggleButtonSetYPosition.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetYPosition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetYPositionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetYPosition, new AbsoluteConstraints(770, 180, -1, -1));

        jToggleButtonXMove.setBackground(new java.awt.Color(0, 204, 0));
        jToggleButtonXMove.setText("move to");
        jToggleButtonXMove.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonXMove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonXMoveActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonXMove, new AbsoluteConstraints(310, 80, -1, -1));

        jToggleButtonYMove.setBackground(new java.awt.Color(0, 204, 0));
        jToggleButtonYMove.setText("move to");
        jToggleButtonYMove.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonYMove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonYMoveActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonYMove, new AbsoluteConstraints(590, 80, -1, -1));

        jLabelModuleXVelocity.setText("velocity");
        jPanelSiTech.add(jLabelModuleXVelocity, new AbsoluteConstraints(310, 100, -1, -1));

        jTextFieldModuleXVelocity.setToolTipText("velocity to move at: must be non-zero for move to occur");
        jPanelSiTech.add(jTextFieldModuleXVelocity, new AbsoluteConstraints(360, 100, 90, -1));

        jToggleButtonSetXVelocity.setText("set");
        jToggleButtonSetXVelocity.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetXVelocity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetXVelocityActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetXVelocity, new AbsoluteConstraints(490, 100, -1, -1));

        jLabelModuleYVelocity.setText("velocity");
        jPanelSiTech.add(jLabelModuleYVelocity, new AbsoluteConstraints(590, 100, -1, -1));

        jTextFieldModuleYVelocity.setToolTipText("velocity to move at: must be non-zero for move to occur");
        jPanelSiTech.add(jTextFieldModuleYVelocity, new AbsoluteConstraints(640, 100, 90, -1));

        jToggleButtonSetYVelocity.setText("set");
        jToggleButtonSetYVelocity.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetYVelocity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetYVelocityActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetYVelocity, new AbsoluteConstraints(770, 100, -1, -1));

        jLabelModuleXRamp.setText("ramp");
        jPanelSiTech.add(jLabelModuleXRamp, new AbsoluteConstraints(320, 260, -1, -1));

        jTextFieldModuleXRamp.setToolTipText("ramping rate: smaller values take longer to ramp");
        jPanelSiTech.add(jTextFieldModuleXRamp, new AbsoluteConstraints(360, 260, 90, -1));

        jToggleButtonSetXRamp.setText("set");
        jToggleButtonSetXRamp.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetXRamp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetXRampActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetXRamp, new AbsoluteConstraints(490, 260, -1, -1));

        jLabelModuleYRamp.setText("ramp");
        jPanelSiTech.add(jLabelModuleYRamp, new AbsoluteConstraints(600, 260, -1, -1));

        jTextFieldModuleYRamp.setToolTipText("ramping rate: smaller values take longer to ramp");
        jPanelSiTech.add(jTextFieldModuleYRamp, new AbsoluteConstraints(640, 260, 90, -1));

        jToggleButtonSetYRamp.setText("set");
        jToggleButtonSetYRamp.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetYRamp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetYRampActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetYRamp, new AbsoluteConstraints(770, 260, -1, -1));

        jLabelModuleXMotorCurrent.setText("motor current");
        jPanelSiTech.add(jLabelModuleXMotorCurrent, new AbsoluteConstraints(270, 460, -1, -1));

        jTextFieldModuleXMotorCurrent.setToolTipText("motor current being consumed in amps");
        jPanelSiTech.add(jTextFieldModuleXMotorCurrent, new AbsoluteConstraints(360, 460, 90, -1));

        jToggleButtonGetXMotorCurrent.setText("get");
        jToggleButtonGetXMotorCurrent.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetXMotorCurrent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetXMotorCurrentActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetXMotorCurrent, new AbsoluteConstraints(460, 460, -1, -1));

        jLabelModuleYMotorCurrent.setText("motor current");
        jPanelSiTech.add(jLabelModuleYMotorCurrent, new AbsoluteConstraints(550, 460, -1, -1));

        jTextFieldModuleYMotorCurrent.setToolTipText("motor current being consumed in amps");
        jPanelSiTech.add(jTextFieldModuleYMotorCurrent, new AbsoluteConstraints(640, 460, 90, -1));

        jToggleButtonGetYMotorCurrent.setText("get");
        jToggleButtonGetYMotorCurrent.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetYMotorCurrent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetYMotorCurrentActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetYMotorCurrent, new AbsoluteConstraints(740, 460, -1, -1));

        jLabelModuleXMaxPositionError.setText("max pos. error");
        jPanelSiTech.add(jLabelModuleXMaxPositionError, new AbsoluteConstraints(270, 300, -1, -1));

        jTextFieldModuleXMaxPositionError.setToolTipText("maximum servo positioning error before motor is shutdown");
        jPanelSiTech.add(jTextFieldModuleXMaxPositionError, new AbsoluteConstraints(360, 300, 90, -1));

        jToggleButtonSetXMaxPositionError.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetXMaxPositionError.setText("set");
        jToggleButtonSetXMaxPositionError.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetXMaxPositionError.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetXMaxPositionErrorActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetXMaxPositionError, new AbsoluteConstraints(490, 300, -1, -1));

        jLabelModuleYMaxPositionError.setText("max pos. error");
        jPanelSiTech.add(jLabelModuleYMaxPositionError, new AbsoluteConstraints(550, 300, -1, -1));

        jTextFieldModuleYMaxPositionError.setToolTipText("maximum servo positioning error before motor is shutdown");
        jPanelSiTech.add(jTextFieldModuleYMaxPositionError, new AbsoluteConstraints(640, 300, 90, -1));

        jToggleButtonSetYMaxPositionError.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetYMaxPositionError.setText("set");
        jToggleButtonSetYMaxPositionError.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetYMaxPositionError.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetYMaxPositionErrorActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetYMaxPositionError, new AbsoluteConstraints(770, 300, -1, -1));

        jTextFieldVersion.setToolTipText("controller microcode version (divide by 10)");
        jPanelSiTech.add(jTextFieldVersion, new AbsoluteConstraints(90, 480, 90, -1));

        jTextFieldVoltage.setToolTipText("motor supply voltage as measured by the controller");
        jPanelSiTech.add(jTextFieldVoltage, new AbsoluteConstraints(90, 520, 90, -1));

        jTextFieldCPUTemperature.setToolTipText("controller's CPU temperature");
        jPanelSiTech.add(jTextFieldCPUTemperature, new AbsoluteConstraints(90, 540, 90, -1));

        jLabelModuleXProportional.setText("proportional");
        jPanelSiTech.add(jLabelModuleXProportional, new AbsoluteConstraints(280, 320, -1, -1));

        jTextFieldModuleXProportional.setToolTipText("PID proportional control value: sends control signal to motor proportional to position difference or error value");
        jPanelSiTech.add(jTextFieldModuleXProportional, new AbsoluteConstraints(360, 320, 90, -1));

        jToggleButtonSetXProportional.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetXProportional.setText("set");
        jToggleButtonSetXProportional.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetXProportional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetXProportionalActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetXProportional, new AbsoluteConstraints(490, 320, -1, -1));

        jLabelModuleXDerivative.setText("derivative");
        jPanelSiTech.add(jLabelModuleXDerivative, new AbsoluteConstraints(300, 340, -1, -1));

        jTextFieldModuleXDerivative.setToolTipText("PID derivative: acts as a lookahead or anticipatory brake to proportional control");
        jPanelSiTech.add(jTextFieldModuleXDerivative, new AbsoluteConstraints(360, 340, 90, -1));

        jToggleButtonSetXDerivative.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetXDerivative.setText("set");
        jToggleButtonSetXDerivative.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetXDerivative.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetXDerivativeActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetXDerivative, new AbsoluteConstraints(490, 340, -1, -1));

        jLabelModuleXIntegral.setText("integral");
        jPanelSiTech.add(jLabelModuleXIntegral, new AbsoluteConstraints(310, 360, -1, -1));

        jTextFieldModuleXIntegral.setToolTipText("PID integral: removes deadband or constant error");
        jPanelSiTech.add(jTextFieldModuleXIntegral, new AbsoluteConstraints(360, 360, 90, -1));

        jToggleButtonSetXIntegral.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetXIntegral.setText("set");
        jToggleButtonSetXIntegral.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetXIntegral.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetXIntegralActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetXIntegral, new AbsoluteConstraints(490, 360, -1, -1));

        jLabelModuleXIntegralLimit.setText("integral limit");
        jPanelSiTech.add(jLabelModuleXIntegralLimit, new AbsoluteConstraints(280, 380, -1, -1));

        jTextFieldModuleXIntegralLimit.setToolTipText("PID integral value cannot exceed this value");
        jPanelSiTech.add(jTextFieldModuleXIntegralLimit, new AbsoluteConstraints(360, 380, 90, -1));

        jToggleButtonGetXIntegralLimit.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetXIntegralLimit.setText("get");
        jToggleButtonGetXIntegralLimit.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetXIntegralLimit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetXIntegralLimitActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetXIntegralLimit, new AbsoluteConstraints(460, 380, -1, -1));

        jToggleButtonSetXIntegralLimit.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetXIntegralLimit.setText("set");
        jToggleButtonSetXIntegralLimit.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetXIntegralLimit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetXIntegralLimitActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetXIntegralLimit, new AbsoluteConstraints(490, 380, -1, -1));

        jLabelModuleXCurrentLimit.setText("current limit");
        jPanelSiTech.add(jLabelModuleXCurrentLimit, new AbsoluteConstraints(280, 400, -1, -1));

        jTextFieldModuleXCurrentLimit.setToolTipText("motor current limit in hundreds of an amp, ie, if desired limit is 2.00 amps, then enter 200");
        jPanelSiTech.add(jTextFieldModuleXCurrentLimit, new AbsoluteConstraints(360, 400, 90, -1));

        jToggleButtonSetXCurrentLimit.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetXCurrentLimit.setText("set");
        jToggleButtonSetXCurrentLimit.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetXCurrentLimit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetXCurrentLimitActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetXCurrentLimit, new AbsoluteConstraints(490, 400, -1, -1));

        jLabelModuleXOutputLimit.setText("output limit");
        jPanelSiTech.add(jLabelModuleXOutputLimit, new AbsoluteConstraints(290, 420, -1, -1));

        jTextFieldModuleXOutputLimit.setToolTipText("can range up to 255: leave this alone unless you know what you are doing");
        jPanelSiTech.add(jTextFieldModuleXOutputLimit, new AbsoluteConstraints(360, 420, 90, -1));

        jToggleButtonSetXOutputLimit.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetXOutputLimit.setText("set");
        jToggleButtonSetXOutputLimit.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetXOutputLimit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetXOutputLimitActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetXOutputLimit, new AbsoluteConstraints(490, 420, -1, -1));

        jLabelModuleXPWM.setText("PWM");
        jPanelSiTech.add(jLabelModuleXPWM, new AbsoluteConstraints(320, 440, -1, -1));

        jTextFieldModuleXPWM.setToolTipText("pulse width modulation value");
        jPanelSiTech.add(jTextFieldModuleXPWM, new AbsoluteConstraints(360, 440, 90, -1));

        jToggleButtonGetXPWM.setText("get");
        jToggleButtonGetXPWM.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetXPWM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetXPWMActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetXPWM, new AbsoluteConstraints(460, 440, -1, -1));

        jToggleButtonSetXPWM.setText("set");
        jToggleButtonSetXPWM.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetXPWM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetXPWMActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetXPWM, new AbsoluteConstraints(490, 440, -1, -1));

        jLabelXMotor.setForeground(new java.awt.Color(0, 0, 102));
        jLabelXMotor.setText("X-Altitude-Declination MOTOR");
        jPanelSiTech.add(jLabelXMotor, new AbsoluteConstraints(330, 30, -1, -1));

        jLabelYMotor.setForeground(new java.awt.Color(0, 0, 102));
        jLabelYMotor.setText("Y-Azimuth-RightAscension MOTOR");
        jPanelSiTech.add(jLabelYMotor, new AbsoluteConstraints(580, 30, -1, -1));

        jToggleButtonXAutoPWM.setText("auto");
        jToggleButtonXAutoPWM.setToolTipText("auto/manual mode: normal operation requires auto mode");
        jToggleButtonXAutoPWM.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonXAutoPWM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonXAutoPWMActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonXAutoPWM, new AbsoluteConstraints(280, 440, -1, -1));

        jLabelModuleYProportional.setText("proportional");
        jPanelSiTech.add(jLabelModuleYProportional, new AbsoluteConstraints(560, 320, -1, -1));

        jTextFieldModuleYProportional.setToolTipText("PID proportional control value: sends control signal to motor proportional to position difference or error value");
        jPanelSiTech.add(jTextFieldModuleYProportional, new AbsoluteConstraints(640, 320, 90, -1));

        jToggleButtonSetYProportional.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetYProportional.setText("set");
        jToggleButtonSetYProportional.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetYProportional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetYProportionalActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetYProportional, new AbsoluteConstraints(770, 320, -1, -1));

        jToggleButtonSetYDerivative.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetYDerivative.setText("set");
        jToggleButtonSetYDerivative.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetYDerivative.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetYDerivativeActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetYDerivative, new AbsoluteConstraints(770, 340, -1, -1));

        jToggleButtonSetYIntegral.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetYIntegral.setText("set");
        jToggleButtonSetYIntegral.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetYIntegral.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetYIntegralActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetYIntegral, new AbsoluteConstraints(770, 360, -1, -1));

        jToggleButtonSetYIntegralLimit.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetYIntegralLimit.setText("set");
        jToggleButtonSetYIntegralLimit.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetYIntegralLimit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetYIntegralLimitActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetYIntegralLimit, new AbsoluteConstraints(770, 380, -1, -1));

        jToggleButtonSetYCurrentLimit.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetYCurrentLimit.setText("set");
        jToggleButtonSetYCurrentLimit.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetYCurrentLimit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetYCurrentLimitActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetYCurrentLimit, new AbsoluteConstraints(770, 400, -1, -1));

        jToggleButtonSetYOutputLimit.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetYOutputLimit.setText("set");
        jToggleButtonSetYOutputLimit.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetYOutputLimit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetYOutputLimitActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetYOutputLimit, new AbsoluteConstraints(770, 420, -1, -1));

        jToggleButtonSetYPWM.setText("set");
        jToggleButtonSetYPWM.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetYPWM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetYPWMActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetYPWM, new AbsoluteConstraints(770, 440, -1, -1));

        jToggleButtonGetYPWM.setText("get");
        jToggleButtonGetYPWM.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetYPWM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetYPWMActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetYPWM, new AbsoluteConstraints(740, 440, -1, -1));

        jToggleButtonGetYIntegralLimit.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetYIntegralLimit.setText("get");
        jToggleButtonGetYIntegralLimit.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetYIntegralLimit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetYIntegralLimitActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetYIntegralLimit, new AbsoluteConstraints(740, 380, -1, -1));

        jTextFieldModuleYDerivative.setToolTipText("PID derivative: acts as a lookahead or anticipatory brake to proportional control");
        jPanelSiTech.add(jTextFieldModuleYDerivative, new AbsoluteConstraints(640, 340, 90, -1));

        jTextFieldModuleYIntegral.setToolTipText("PID integral: removes deadband or constant error");
        jPanelSiTech.add(jTextFieldModuleYIntegral, new AbsoluteConstraints(640, 360, 90, -1));

        jTextFieldModuleYIntegralLimit.setToolTipText("PID integral value cannot exceed this value");
        jPanelSiTech.add(jTextFieldModuleYIntegralLimit, new AbsoluteConstraints(640, 380, 90, -1));

        jTextFieldModuleYCurrentLimit.setToolTipText("motor current limit in hundreds of an amp, ie, if desired limit is 2.00 amps, then enter 200");
        jPanelSiTech.add(jTextFieldModuleYCurrentLimit, new AbsoluteConstraints(640, 400, 90, -1));

        jTextFieldModuleYOutputLimit.setToolTipText("can range up to 255: leave this alone unless you know what you are doing");
        jPanelSiTech.add(jTextFieldModuleYOutputLimit, new AbsoluteConstraints(640, 420, 90, -1));

        jTextFieldModuleYPWM.setToolTipText("pulse width modulation value");
        jPanelSiTech.add(jTextFieldModuleYPWM, new AbsoluteConstraints(640, 440, 90, -1));

        jLabelModuleYDerivative.setText("derivative");
        jPanelSiTech.add(jLabelModuleYDerivative, new AbsoluteConstraints(580, 340, -1, -1));

        jLabelModuleYIntegral.setText("integral");
        jPanelSiTech.add(jLabelModuleYIntegral, new AbsoluteConstraints(590, 360, -1, -1));

        jLabelModuleYIntegralLimit.setText("integral limit");
        jPanelSiTech.add(jLabelModuleYIntegralLimit, new AbsoluteConstraints(560, 380, -1, -1));

        jLabelModuleYCurrentLimit.setText("current limit");
        jPanelSiTech.add(jLabelModuleYCurrentLimit, new AbsoluteConstraints(560, 400, -1, -1));

        jLabelModuleYOutputLimit.setText("output limit");
        jPanelSiTech.add(jLabelModuleYOutputLimit, new AbsoluteConstraints(570, 420, -1, -1));

        jLabelModuleYPWM.setText("PWM");
        jPanelSiTech.add(jLabelModuleYPWM, new AbsoluteConstraints(600, 440, -1, -1));

        jToggleButtonYAutoPWM.setText("auto");
        jToggleButtonYAutoPWM.setToolTipText("auto/manual mode: normal operation requires auto mode");
        jToggleButtonYAutoPWM.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonYAutoPWM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonYAutoPWMActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonYAutoPWM, new AbsoluteConstraints(560, 440, -1, -1));

        jRadioButtonXInvertMotorDirection.setText("invert motor direction");
        jRadioButtonXInvertMotorDirection.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jPanelSiTech.add(jRadioButtonXInvertMotorDirection, new AbsoluteConstraints(310, 120, -1, -1));

        jToggleButtonGetXInvertMotorDirection.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetXInvertMotorDirection.setText("get");
        jToggleButtonGetXInvertMotorDirection.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetXInvertMotorDirection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetXInvertMotorDirectionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetXInvertMotorDirection, new AbsoluteConstraints(460, 120, -1, -1));

        jToggleButtonSetXInvertMotorDirection.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetXInvertMotorDirection.setText("set");
        jToggleButtonSetXInvertMotorDirection.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetXInvertMotorDirection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetXInvertMotorDirectionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetXInvertMotorDirection, new AbsoluteConstraints(490, 120, -1, -1));

        jRadioButtonYInvertMotorDirection.setText("invert motor direction");
        jRadioButtonYInvertMotorDirection.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jPanelSiTech.add(jRadioButtonYInvertMotorDirection, new AbsoluteConstraints(590, 120, -1, -1));

        jToggleButtonGetYInvertMotorDirection.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetYInvertMotorDirection.setText("get");
        jToggleButtonGetYInvertMotorDirection.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetYInvertMotorDirection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetYInvertMotorDirectionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetYInvertMotorDirection, new AbsoluteConstraints(740, 120, -1, -1));

        jToggleButtonSetYInvertMotorDirection.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetYInvertMotorDirection.setText("set");
        jToggleButtonSetYInvertMotorDirection.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetYInvertMotorDirection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetYInvertMotorDirectionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetYInvertMotorDirection, new AbsoluteConstraints(770, 120, -1, -1));

        jLabelXEncoder.setForeground(new java.awt.Color(0, 0, 102));
        jLabelXEncoder.setText("X-Altitude-Declination ENCODER");
        jPanelSiTech.add(jLabelXEncoder, new AbsoluteConstraints(320, 490, -1, -1));

        jRadioButtonXInvertEncoderDirection.setText("invert direction");
        jRadioButtonXInvertEncoderDirection.setToolTipText("reverses direction that external scope encoder counts in");
        jRadioButtonXInvertEncoderDirection.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jPanelSiTech.add(jRadioButtonXInvertEncoderDirection, new AbsoluteConstraints(340, 510, -1, 20));

        jToggleButtonGetXInvertEncoderDirection.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetXInvertEncoderDirection.setText("get");
        jToggleButtonGetXInvertEncoderDirection.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetXInvertEncoderDirection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetXInvertEncoderDirectionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetXInvertEncoderDirection, new AbsoluteConstraints(460, 510, -1, 20));

        jToggleButtonSetXInvertEncoderDirection.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetXInvertEncoderDirection.setText("set");
        jToggleButtonSetXInvertEncoderDirection.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetXInvertEncoderDirection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetXInvertEncoderDirectionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetXInvertEncoderDirection, new AbsoluteConstraints(490, 510, -1, 20));

        jLabelModuleXEncoderPosition.setText("position");
        jPanelSiTech.add(jLabelModuleXEncoderPosition, new AbsoluteConstraints(310, 550, -1, 20));

        jTextFieldModuleXEncoderPosition.setToolTipText("encoder position");
        jPanelSiTech.add(jTextFieldModuleXEncoderPosition, new AbsoluteConstraints(360, 550, 90, 20));

        jToggleButtonGetXEncoderPosition.setText("get");
        jToggleButtonGetXEncoderPosition.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetXEncoderPosition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetXEncoderPositionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetXEncoderPosition, new AbsoluteConstraints(460, 550, -1, 20));

        jToggleButtonSetXEncoderPosition.setText("set");
        jToggleButtonSetXEncoderPosition.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetXEncoderPosition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetXEncoderPositionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetXEncoderPosition, new AbsoluteConstraints(490, 550, -1, 20));

        jLabelYEncoder.setForeground(new java.awt.Color(0, 0, 102));
        jLabelYEncoder.setText("Y-Azimuth-RightAscension ENCODER");
        jPanelSiTech.add(jLabelYEncoder, new AbsoluteConstraints(570, 490, -1, -1));

        jRadioButtonYInvertEncoderDirection.setText("invert direction");
        jRadioButtonYInvertEncoderDirection.setToolTipText("reverses direction that external scope encoder counts in");
        jRadioButtonYInvertEncoderDirection.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jPanelSiTech.add(jRadioButtonYInvertEncoderDirection, new AbsoluteConstraints(620, 510, -1, 20));

        jToggleButtonGetYInvertEncoderDirection.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetYInvertEncoderDirection.setText("get");
        jToggleButtonGetYInvertEncoderDirection.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetYInvertEncoderDirection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetYInvertEncoderDirectionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetYInvertEncoderDirection, new AbsoluteConstraints(740, 510, -1, 20));

        jToggleButtonSetYInvertEncoderDirection.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetYInvertEncoderDirection.setText("set");
        jToggleButtonSetYInvertEncoderDirection.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetYInvertEncoderDirection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetYInvertEncoderDirectionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetYInvertEncoderDirection, new AbsoluteConstraints(770, 510, -1, 20));

        jToggleButtonSetYEncoderPosition.setText("set");
        jToggleButtonSetYEncoderPosition.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetYEncoderPosition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetYEncoderPositionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetYEncoderPosition, new AbsoluteConstraints(770, 550, -1, 20));

        jToggleButtonGetYEncoderPosition.setText("get");
        jToggleButtonGetYEncoderPosition.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetYEncoderPosition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetYEncoderPositionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetYEncoderPosition, new AbsoluteConstraints(740, 550, -1, 20));

        jTextFieldModuleYEncoderPosition.setToolTipText("encoder position");
        jPanelSiTech.add(jTextFieldModuleYEncoderPosition, new AbsoluteConstraints(640, 550, 90, 20));

        jLabelModuleYEncoderPosition.setText("position");
        jPanelSiTech.add(jLabelModuleYEncoderPosition, new AbsoluteConstraints(590, 550, -1, 20));

        jLabelModuleXMotorCountsPerRevolution.setText("counts per rev.");
        jPanelSiTech.add(jLabelModuleXMotorCountsPerRevolution, new AbsoluteConstraints(270, 160, -1, -1));

        jTextFieldModuleXMotorCountsPerRevolution.setToolTipText("counts or ticks of the motor that encompases a complete revolution of the telescope axis");
        jPanelSiTech.add(jTextFieldModuleXMotorCountsPerRevolution, new AbsoluteConstraints(360, 160, 90, -1));

        jToggleButtonGetXMotorCountsPerRevolution.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetXMotorCountsPerRevolution.setText("get");
        jToggleButtonGetXMotorCountsPerRevolution.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetXMotorCountsPerRevolution.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetXMotorCountsPerRevolutionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetXMotorCountsPerRevolution, new AbsoluteConstraints(460, 160, -1, -1));

        jToggleButtonSetXMotorCountsPerRevolution.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetXMotorCountsPerRevolution.setText("set");
        jToggleButtonSetXMotorCountsPerRevolution.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetXMotorCountsPerRevolution.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetXMotorCountsPerRevolutionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetXMotorCountsPerRevolution, new AbsoluteConstraints(490, 160, -1, -1));

        jLabelModuleYMotorCountsPerRevolution.setText("counts per rev.");
        jPanelSiTech.add(jLabelModuleYMotorCountsPerRevolution, new AbsoluteConstraints(550, 160, -1, -1));

        jTextFieldModuleYMotorCountsPerRevolution.setToolTipText("counts or ticks of the motor that encompases a complete revolution of the telescope axis");
        jPanelSiTech.add(jTextFieldModuleYMotorCountsPerRevolution, new AbsoluteConstraints(640, 160, 90, -1));

        jToggleButtonGetYMotorCountsPerRevolution.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetYMotorCountsPerRevolution.setText("get");
        jToggleButtonGetYMotorCountsPerRevolution.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetYMotorCountsPerRevolution.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetYMotorCountsPerRevolutionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetYMotorCountsPerRevolution, new AbsoluteConstraints(740, 160, -1, -1));

        jToggleButtonSetYMotorCountsPerRevolution.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetYMotorCountsPerRevolution.setText("set");
        jToggleButtonSetYMotorCountsPerRevolution.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetYMotorCountsPerRevolution.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetYMotorCountsPerRevolutionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetYMotorCountsPerRevolution, new AbsoluteConstraints(770, 160, -1, -1));

        jLabelModuleXEncoderCountsPerRevolution.setText("counts per rev.");
        jPanelSiTech.add(jLabelModuleXEncoderCountsPerRevolution, new AbsoluteConstraints(270, 530, -1, -1));

        jTextFieldModuleXEncoderCountsPerRevolution.setToolTipText("total number of encoder counts per telescope axis revolution");
        jPanelSiTech.add(jTextFieldModuleXEncoderCountsPerRevolution, new AbsoluteConstraints(360, 530, 90, -1));

        jToggleButtonGetXEncoderCountsPerRevolution.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetXEncoderCountsPerRevolution.setText("get");
        jToggleButtonGetXEncoderCountsPerRevolution.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetXEncoderCountsPerRevolution.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetXEncoderCountsPerRevolutionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetXEncoderCountsPerRevolution, new AbsoluteConstraints(460, 530, -1, -1));

        jToggleButtonSetXEncoderCountsPerRevolution.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetXEncoderCountsPerRevolution.setText("set");
        jToggleButtonSetXEncoderCountsPerRevolution.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetXEncoderCountsPerRevolution.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetXEncoderCountsPerRevolutionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetXEncoderCountsPerRevolution, new AbsoluteConstraints(490, 530, -1, -1));

        jLabelModuleYEncoderCountsPerRevolution.setText("counts per rev.");
        jPanelSiTech.add(jLabelModuleYEncoderCountsPerRevolution, new AbsoluteConstraints(550, 530, -1, -1));

        jTextFieldModuleYEncoderCountsPerRevolution.setToolTipText("total number of encoder counts per telescope axis revolution");
        jPanelSiTech.add(jTextFieldModuleYEncoderCountsPerRevolution, new AbsoluteConstraints(640, 530, 90, -1));

        jToggleButtonGetYEncoderCountsPerRevolution.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetYEncoderCountsPerRevolution.setText("get");
        jToggleButtonGetYEncoderCountsPerRevolution.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetYEncoderCountsPerRevolution.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetYEncoderCountsPerRevolutionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetYEncoderCountsPerRevolution, new AbsoluteConstraints(740, 530, -1, -1));

        jToggleButtonSetYEncoderCountsPerRevolution.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetYEncoderCountsPerRevolution.setText("set");
        jToggleButtonSetYEncoderCountsPerRevolution.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetYEncoderCountsPerRevolution.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetYEncoderCountsPerRevolutionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetYEncoderCountsPerRevolution, new AbsoluteConstraints(770, 530, -1, -1));

        jRadioButtonSlewAndTrack.setText("slewNtrack");
        jRadioButtonSlewAndTrack.setToolTipText("controller is in slew and track mode where the scope must only be moved by the motors: no clutches or hand pushes allowed");
        jRadioButtonSlewAndTrack.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jPanelSiTech.add(jRadioButtonSlewAndTrack, new AbsoluteConstraints(90, 280, -1, -1));

        jToggleButtonGetSlewAndTrack.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetSlewAndTrack.setText("get");
        jToggleButtonGetSlewAndTrack.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetSlewAndTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetSlewAndTrackActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetSlewAndTrack, new AbsoluteConstraints(190, 280, -1, -1));

        jToggleButtonSetSlewAndTrack.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetSlewAndTrack.setText("set");
        jToggleButtonSetSlewAndTrack.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetSlewAndTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetSlewAndTrackActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetSlewAndTrack, new AbsoluteConstraints(220, 280, -1, -1));

        jRadioButtonEquatorialPlatform.setText("EQplatform");
        jRadioButtonEquatorialPlatform.setToolTipText("controller is in equatorial or platform mode where the Right Ascension motor tracks at the sidereal rate");
        jRadioButtonEquatorialPlatform.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jPanelSiTech.add(jRadioButtonEquatorialPlatform, new AbsoluteConstraints(90, 360, -1, -1));

        jToggleButtonGetEquatorialPlatform.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetEquatorialPlatform.setText("get");
        jToggleButtonGetEquatorialPlatform.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetEquatorialPlatform.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetEquatorialPlatformActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetEquatorialPlatform, new AbsoluteConstraints(190, 360, -1, -1));

        jToggleButtonSetEquatorialPlatform.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetEquatorialPlatform.setText("set");
        jToggleButtonSetEquatorialPlatform.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetEquatorialPlatform.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetEquatorialPlatformActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetEquatorialPlatform, new AbsoluteConstraints(220, 360, -1, -1));

        jRadioButtonEnableHandpad.setText("handpadOn");
        jRadioButtonEnableHandpad.setToolTipText("handpad is enabled");
        jRadioButtonEnableHandpad.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jPanelSiTech.add(jRadioButtonEnableHandpad, new AbsoluteConstraints(90, 320, -1, -1));

        jToggleButtonGetEnableHandpad.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetEnableHandpad.setText("get");
        jToggleButtonGetEnableHandpad.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetEnableHandpad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetEnableHandpadActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetEnableHandpad, new AbsoluteConstraints(190, 320, -1, -1));

        jToggleButtonSetEnableHandpad.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetEnableHandpad.setText("set");
        jToggleButtonSetEnableHandpad.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetEnableHandpad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetEnableHandpadActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetEnableHandpad, new AbsoluteConstraints(220, 320, -1, -1));

        jRadioButtonHandpadBiDir.setText("handpadBiDir");
        jRadioButtonHandpadBiDir.setToolTipText("simultaneous directions are permitted: requires the new handpad design - handpads for Bartels' stepper unit not compatible with this option");
        jRadioButtonHandpadBiDir.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jPanelSiTech.add(jRadioButtonHandpadBiDir, new AbsoluteConstraints(90, 340, -1, -1));

        jToggleButtonGetHandpadBiDir.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetHandpadBiDir.setText("get");
        jToggleButtonGetHandpadBiDir.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetHandpadBiDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetHandpadBiDirActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetHandpadBiDir, new AbsoluteConstraints(190, 340, -1, -1));

        jToggleButtonSetHandpadBiDir.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetHandpadBiDir.setText("set");
        jToggleButtonSetHandpadBiDir.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetHandpadBiDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetHandpadBiDirActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetHandpadBiDir, new AbsoluteConstraints(220, 340, -1, -1));

        jRadioButtonDragAndTrack.setText("dragNtrack");
        jRadioButtonDragAndTrack.setToolTipText("controller is in drag and track mode where scope can be pushed by hand");
        jRadioButtonDragAndTrack.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jPanelSiTech.add(jRadioButtonDragAndTrack, new AbsoluteConstraints(90, 260, -1, -1));

        jToggleButtonGetDragAndTrack.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetDragAndTrack.setText("get");
        jToggleButtonGetDragAndTrack.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetDragAndTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetDragAndTrackActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetDragAndTrack, new AbsoluteConstraints(190, 260, -1, -1));

        jToggleButtonSetDragAndTrack.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetDragAndTrack.setText("set");
        jToggleButtonSetDragAndTrack.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetDragAndTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetDragAndTrackActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetDragAndTrack, new AbsoluteConstraints(220, 260, -1, -1));

        jToggleButtonExit.setText("exit");
        jToggleButtonExit.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonExitActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonExit, new AbsoluteConstraints(723, 10, 60, -1));

        jLabelGeneral.setForeground(new java.awt.Color(0, 0, 102));
        jLabelGeneral.setText("GENERAL");
        jPanelSiTech.add(jLabelGeneral, new AbsoluteConstraints(110, 30, -1, -1));

        jLabelStatusComm.setText("comm status");
        jLabelStatusComm.setToolTipText("status of comm port connection: green if connected, red if not");
        jPanelSiTech.add(jLabelStatusComm, new AbsoluteConstraints(140, 50, -1, -1));

        jToggleButtonConnect.setText("connect");
        jToggleButtonConnect.setToolTipText("connect to comm port designated by combo box");
        jToggleButtonConnect.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonConnectActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonConnect, new AbsoluteConstraints(90, 50, -1, -1));

        jPanelSiTech.add(jComboBoxModuleSetAddress, new AbsoluteConstraints(10, 120, 50, 20));

        jRadioButtonGuideMode.setText("guideMode");
        jRadioButtonGuideMode.setToolTipText("controller is in guide mode where handpad direction commands are interpreted as guiding +- tracking speed commands");
        jRadioButtonGuideMode.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jPanelSiTech.add(jRadioButtonGuideMode, new AbsoluteConstraints(90, 300, -1, -1));

        jToggleButtonGetGuideMode.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetGuideMode.setText("get");
        jToggleButtonGetGuideMode.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetGuideMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetGuideModeActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetGuideMode, new AbsoluteConstraints(190, 300, -1, -1));

        jToggleButtonSetGuideMode.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetGuideMode.setText("set");
        jToggleButtonSetGuideMode.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetGuideMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetGuideModeActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetGuideMode, new AbsoluteConstraints(220, 300, -1, -1));

        jRadioButtonXInvertServoEncoderDirection.setText("invert servo encoder");
        jRadioButtonXInvertServoEncoderDirection.setToolTipText("if motor 'runs away', can invert here, or swap encoder A and B channel leads");
        jRadioButtonXInvertServoEncoderDirection.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jPanelSiTech.add(jRadioButtonXInvertServoEncoderDirection, new AbsoluteConstraints(310, 140, -1, -1));

        jToggleButtonGetXServoEncoderDirection.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetXServoEncoderDirection.setText("get");
        jToggleButtonGetXServoEncoderDirection.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetXServoEncoderDirection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetXServoEncoderDirectionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetXServoEncoderDirection, new AbsoluteConstraints(460, 140, -1, -1));

        jToggleButtonSetXServoEncoderDirection.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetXServoEncoderDirection.setText("set");
        jToggleButtonSetXServoEncoderDirection.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetXServoEncoderDirection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetXServoEncoderDirectionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetXServoEncoderDirection, new AbsoluteConstraints(490, 140, -1, -1));

        jRadioButtonYInvertServoEncoderDirection.setText("invert servo encoder");
        jRadioButtonYInvertServoEncoderDirection.setToolTipText("if motor 'runs away', can invert here, or swap encoder A and B channel leads");
        jRadioButtonYInvertServoEncoderDirection.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jPanelSiTech.add(jRadioButtonYInvertServoEncoderDirection, new AbsoluteConstraints(590, 140, -1, -1));

        jToggleButtonGetYServoEncoderDirection.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetYServoEncoderDirection.setText("get");
        jToggleButtonGetYServoEncoderDirection.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetYServoEncoderDirection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetYServoEncoderDirectionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetYServoEncoderDirection, new AbsoluteConstraints(740, 140, -1, -1));

        jToggleButtonSetYServoEncoderDirection.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetYServoEncoderDirection.setText("set");
        jToggleButtonSetYServoEncoderDirection.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetYServoEncoderDirection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetYServoEncoderDirectionActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetYServoEncoderDirection, new AbsoluteConstraints(770, 140, -1, -1));

        jLabelModulePlatformUpDn.setText("platform up/dn");
        jPanelSiTech.add(jLabelModulePlatformUpDn, new AbsoluteConstraints(10, 420, -1, -1));

        jTextFieldModulePlatformUpDn.setToolTipText("plastform up/down motor velocity for simulated declination adjustment");
        jPanelSiTech.add(jTextFieldModulePlatformUpDn, new AbsoluteConstraints(90, 420, 90, -1));

        jToggleButtonGetPlatformUpDn.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetPlatformUpDn.setText("get");
        jToggleButtonGetPlatformUpDn.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetPlatformUpDn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetPlatformUpDnActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetPlatformUpDn, new AbsoluteConstraints(190, 420, -1, -1));

        jToggleButtonSetPlatformUpDn.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetPlatformUpDn.setText("set");
        jToggleButtonSetPlatformUpDn.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetPlatformUpDn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetPlatformUpDnActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetPlatformUpDn, new AbsoluteConstraints(220, 420, -1, -1));

        jLabelModulePlatformRate.setText("platform rate");
        jPanelSiTech.add(jLabelModulePlatformRate, new AbsoluteConstraints(10, 400, -1, -1));

        jTextFieldModulePlatformRate.setToolTipText("the tracking rate when in platform mode");
        jPanelSiTech.add(jTextFieldModulePlatformRate, new AbsoluteConstraints(90, 400, 90, -1));

        jToggleButtonGetPlatformRate.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetPlatformRate.setText("get");
        jToggleButtonGetPlatformRate.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetPlatformRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetPlatformRateActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetPlatformRate, new AbsoluteConstraints(190, 400, -1, -1));

        jToggleButtonSetPlatformRate.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetPlatformRate.setText("set");
        jToggleButtonSetPlatformRate.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetPlatformRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetPlatformRateActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetPlatformRate, new AbsoluteConstraints(220, 400, -1, -1));

        jLabelModulePlatformGoal.setText("platform goal");
        jPanelSiTech.add(jLabelModulePlatformGoal, new AbsoluteConstraints(10, 380, -1, -1));

        jTextFieldModulePlatformGoal.setToolTipText("in platform mode, controller will track the Right Ascension motor to this point, then auto-rewind to beginning");
        jPanelSiTech.add(jTextFieldModulePlatformGoal, new AbsoluteConstraints(90, 380, 90, -1));

        jToggleButtonGetPlatformGoal.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetPlatformGoal.setText("get");
        jToggleButtonGetPlatformGoal.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetPlatformGoal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetPlatformGoalActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetPlatformGoal, new AbsoluteConstraints(190, 380, -1, -1));

        jToggleButtonSetPlatformGoal.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetPlatformGoal.setText("set");
        jToggleButtonSetPlatformGoal.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetPlatformGoal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetPlatformGoalActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetPlatformGoal, new AbsoluteConstraints(220, 380, -1, -1));

        jLabelModuleXMotorGuideRate.setText("guide rate");
        jPanelSiTech.add(jLabelModuleXMotorGuideRate, new AbsoluteConstraints(300, 240, -1, -1));

        jTextFieldModuleXMotorGuideRate.setToolTipText("+- to tracking rate: finest velocity control");
        jPanelSiTech.add(jTextFieldModuleXMotorGuideRate, new AbsoluteConstraints(360, 240, 90, -1));

        jToggleButtonGetXMotorGuideRate.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetXMotorGuideRate.setText("get");
        jToggleButtonGetXMotorGuideRate.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetXMotorGuideRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetXMotorGuideRateActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetXMotorGuideRate, new AbsoluteConstraints(460, 240, -1, -1));

        jToggleButtonSetXMotorGuideRate.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetXMotorGuideRate.setText("set");
        jToggleButtonSetXMotorGuideRate.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetXMotorGuideRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetXMotorGuideRateActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetXMotorGuideRate, new AbsoluteConstraints(490, 240, -1, -1));

        jLabelModuleXMotorPanRate.setText("pan rate");
        jPanelSiTech.add(jLabelModuleXMotorPanRate, new AbsoluteConstraints(310, 220, -1, -1));

        jTextFieldModuleXMotorPanRate.setToolTipText("pan or centering rate: medium velocity");
        jPanelSiTech.add(jTextFieldModuleXMotorPanRate, new AbsoluteConstraints(360, 220, 90, -1));

        jToggleButtonGetXMotorPanRate.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetXMotorPanRate.setText("get");
        jToggleButtonGetXMotorPanRate.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetXMotorPanRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetXMotorPanRateActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetXMotorPanRate, new AbsoluteConstraints(460, 220, -1, -1));

        jToggleButtonSetXMotorPanRate.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetXMotorPanRate.setText("set");
        jToggleButtonSetXMotorPanRate.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetXMotorPanRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetXMotorPanRateActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetXMotorPanRate, new AbsoluteConstraints(490, 220, -1, -1));

        jLabelModuleXMotorSlewRate.setText("slew rate");
        jPanelSiTech.add(jLabelModuleXMotorSlewRate, new AbsoluteConstraints(300, 200, -1, -1));

        jTextFieldModuleXMotorSlewRate.setToolTipText("slew rate: fastest velocity");
        jPanelSiTech.add(jTextFieldModuleXMotorSlewRate, new AbsoluteConstraints(360, 200, 90, -1));

        jToggleButtonGetXMotorSlewRate.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetXMotorSlewRate.setText("get");
        jToggleButtonGetXMotorSlewRate.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetXMotorSlewRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetXMotorSlewRateActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetXMotorSlewRate, new AbsoluteConstraints(460, 200, -1, -1));

        jToggleButtonSetXMotorSlewRate.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetXMotorSlewRate.setText("set");
        jToggleButtonSetXMotorSlewRate.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetXMotorSlewRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetXMotorSlewRateActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetXMotorSlewRate, new AbsoluteConstraints(490, 200, -1, -1));

        jLabelModuleYMotorGuideRate.setText("guide rate");
        jPanelSiTech.add(jLabelModuleYMotorGuideRate, new AbsoluteConstraints(580, 240, -1, -1));

        jTextFieldModuleYMotorGuideRate.setToolTipText("+- to tracking rate: finest velocity control");
        jPanelSiTech.add(jTextFieldModuleYMotorGuideRate, new AbsoluteConstraints(640, 240, 90, -1));

        jToggleButtonGetYMotorGuideRate.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetYMotorGuideRate.setText("get");
        jToggleButtonGetYMotorGuideRate.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetYMotorGuideRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetYMotorGuideRateActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetYMotorGuideRate, new AbsoluteConstraints(740, 240, -1, -1));

        jToggleButtonSetYMotorGuideRate.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetYMotorGuideRate.setText("set");
        jToggleButtonSetYMotorGuideRate.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetYMotorGuideRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetYMotorGuideRateActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetYMotorGuideRate, new AbsoluteConstraints(770, 240, -1, -1));

        jLabelModuleYMotorPanRate.setText("pan rate");
        jPanelSiTech.add(jLabelModuleYMotorPanRate, new AbsoluteConstraints(590, 220, -1, -1));

        jTextFieldModuleYMotorPanRate.setToolTipText("pan or centering rate: medium velocity");
        jPanelSiTech.add(jTextFieldModuleYMotorPanRate, new AbsoluteConstraints(640, 220, 90, -1));

        jToggleButtonGetYMotorPanRate.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetYMotorPanRate.setText("get");
        jToggleButtonGetYMotorPanRate.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetYMotorPanRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetYMotorPanRateActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetYMotorPanRate, new AbsoluteConstraints(740, 220, -1, -1));

        jToggleButtonSetYMotorPanRate.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetYMotorPanRate.setText("set");
        jToggleButtonSetYMotorPanRate.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetYMotorPanRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetYMotorPanRateActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetYMotorPanRate, new AbsoluteConstraints(770, 220, -1, -1));

        jLabelModuleYMotorSlewRate.setText("slew rate");
        jPanelSiTech.add(jLabelModuleYMotorSlewRate, new AbsoluteConstraints(580, 200, -1, -1));

        jTextFieldModuleYMotorSlewRate.setToolTipText("slew rate: fastest velocity");
        jPanelSiTech.add(jTextFieldModuleYMotorSlewRate, new AbsoluteConstraints(640, 200, 90, -1));

        jToggleButtonGetYMotorSlewRate.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetYMotorSlewRate.setText("get");
        jToggleButtonGetYMotorSlewRate.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetYMotorSlewRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetYMotorSlewRateActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetYMotorSlewRate, new AbsoluteConstraints(740, 200, -1, -1));

        jToggleButtonSetYMotorSlewRate.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetYMotorSlewRate.setText("set");
        jToggleButtonSetYMotorSlewRate.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetYMotorSlewRate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetYMotorSlewRateActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetYMotorSlewRate, new AbsoluteConstraints(770, 200, -1, -1));

        jTextFieldModuleXMoveToPosition.setToolTipText("move to target: must be different than current position for the motor to move");
        jPanelSiTech.add(jTextFieldModuleXMoveToPosition, new AbsoluteConstraints(360, 80, 90, -1));

        jTextFieldModuleYMoveToPosition.setToolTipText("move to target: must be different than current position for the motor to move");
        jPanelSiTech.add(jTextFieldModuleYMoveToPosition, new AbsoluteConstraints(640, 80, 90, -1));

        jToggleButtonGetXProportional.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetXProportional.setText("get");
        jToggleButtonGetXProportional.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetXProportional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetXProportionalActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetXProportional, new AbsoluteConstraints(460, 320, -1, -1));

        jToggleButtonGetXDerivative.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetXDerivative.setText("get");
        jToggleButtonGetXDerivative.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetXDerivative.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetXDerivativeActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetXDerivative, new AbsoluteConstraints(460, 340, -1, -1));

        jToggleButtonGetXIntegral.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetXIntegral.setText("get");
        jToggleButtonGetXIntegral.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetXIntegral.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetXIntegralActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetXIntegral, new AbsoluteConstraints(460, 360, -1, -1));

        jToggleButtonGetYProportional.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetYProportional.setText("get");
        jToggleButtonGetYProportional.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetYProportional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetYProportionalActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetYProportional, new AbsoluteConstraints(740, 320, -1, -1));

        jToggleButtonGetYDerivative.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetYDerivative.setText("get");
        jToggleButtonGetYDerivative.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetYDerivative.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetYDerivativeActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetYDerivative, new AbsoluteConstraints(740, 340, -1, -1));

        jToggleButtonGetYIntegral.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetYIntegral.setText("get");
        jToggleButtonGetYIntegral.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetYIntegral.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetYIntegralActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetYIntegral, new AbsoluteConstraints(740, 360, -1, -1));

        jLabelFirmwareSerialNumber.setText("serial #");
        jPanelSiTech.add(jLabelFirmwareSerialNumber, new AbsoluteConstraints(40, 500, -1, -1));

        jTextFieldSerialNumber.setToolTipText("controller serial number");
        jPanelSiTech.add(jTextFieldSerialNumber, new AbsoluteConstraints(90, 500, 90, -1));

        jToggleButtonGetSerialNumber.setText("get");
        jToggleButtonGetSerialNumber.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetSerialNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetSerialNumberActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetSerialNumber, new AbsoluteConstraints(190, 500, -1, -1));

        jLabelModuleLatitude.setText("latitude deg");
        jPanelSiTech.add(jLabelModuleLatitude, new AbsoluteConstraints(20, 440, -1, -1));

        jTextFieldModuleLatitude.setToolTipText("latitude in degrees");
        jPanelSiTech.add(jTextFieldModuleLatitude, new AbsoluteConstraints(90, 440, 90, -1));

        jToggleButtonModuleGetLatitude.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonModuleGetLatitude.setText("get");
        jToggleButtonModuleGetLatitude.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonModuleGetLatitude.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonModuleGetLatitudeActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonModuleGetLatitude, new AbsoluteConstraints(190, 440, -1, -1));

        jLabelModulePICServoTimeOut.setText("PICTimeOut");
        jPanelSiTech.add(jLabelModulePICServoTimeOut, new AbsoluteConstraints(20, 460, -1, -1));

        jTextFieldModulePICServoTimeOut.setToolTipText("controller timeout in seconds when in PICServo mode: values of 0 or 255 mean no timeout");
        jPanelSiTech.add(jTextFieldModulePICServoTimeOut, new AbsoluteConstraints(90, 460, 90, -1));

        jToggleButtonGetPICServoTimeOut.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetPICServoTimeOut.setText("get");
        jToggleButtonGetPICServoTimeOut.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetPICServoTimeOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetPICServoTimeOutActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetPICServoTimeOut, new AbsoluteConstraints(190, 460, -1, -1));

        jToggleButtonSetPICServoTimeOut.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetPICServoTimeOut.setText("set");
        jToggleButtonSetPICServoTimeOut.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetPICServoTimeOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetPICServoTimeOutActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetPICServoTimeOut, new AbsoluteConstraints(220, 460, -1, -1));

        jToggleButtonSetLatitude.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetLatitude.setText("set");
        jToggleButtonSetLatitude.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetLatitude.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetLatitudeActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetLatitude, new AbsoluteConstraints(220, 440, -1, -1));

        jToggleButtonGetAll.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonGetAll.setText("get all blue");
        jToggleButtonGetAll.setToolTipText("get all variables with blue toggles");
        jToggleButtonGetAll.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetAllActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetAll, new AbsoluteConstraints(40, 100, -1, -1));

        jToggleButtonSetAll.setForeground(new java.awt.Color(0, 51, 255));
        jToggleButtonSetAll.setText("set all blue");
        jToggleButtonSetAll.setToolTipText("set all variables with blue toggles; necessary to get a 'get all' first");
        jToggleButtonSetAll.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSetAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSetAllActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonSetAll, new AbsoluteConstraints(190, 100, -1, -1));

        jLabelStatusGetAllStatus.setText("get all status");
        jLabelStatusGetAllStatus.setToolTipText("status of 'get all': green if 'get all' successful, red if not");
        jPanelSiTech.add(jLabelStatusGetAllStatus, new AbsoluteConstraints(110, 100, -1, -1));

        jLabelModuleXPositionError.setText("pos. error");
        jPanelSiTech.add(jLabelModuleXPositionError, new AbsoluteConstraints(300, 280, -1, -1));

        jTextFieldModuleXPositionError.setToolTipText("servo positioning error");
        jPanelSiTech.add(jTextFieldModuleXPositionError, new AbsoluteConstraints(360, 280, 90, -1));

        jToggleButtonGetXPositionError.setText("get");
        jToggleButtonGetXPositionError.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetXPositionError.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetXPositionErrorActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetXPositionError, new AbsoluteConstraints(460, 280, -1, -1));

        jLabelModuleYPositionError.setText("pos. error");
        jPanelSiTech.add(jLabelModuleYPositionError, new AbsoluteConstraints(580, 280, -1, -1));

        jTextFieldModuleYPositionError.setToolTipText("servo positioning error");
        jPanelSiTech.add(jTextFieldModuleYPositionError, new AbsoluteConstraints(640, 280, 90, -1));

        jToggleButtonGetYPositionError.setText("get");
        jToggleButtonGetYPositionError.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetYPositionError.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetYPositionErrorActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetYPositionError, new AbsoluteConstraints(740, 280, -1, -1));

        jTextFieldModuleAddress.setToolTipText("controller module address");
        jPanelSiTech.add(jTextFieldModuleAddress, new AbsoluteConstraints(70, 120, 30, -1));

        jTextFieldQ.setToolTipText("'Q'uery external scope encoders; divide by 50 to get positions in degrees; for compatibility with encoder boxes");
        jPanelSiTech.add(jTextFieldQ, new AbsoluteConstraints(40, 580, 320, -1));

        jLabelQ.setText("'Q'");
        jPanelSiTech.add(jLabelQ, new AbsoluteConstraints(20, 580, -1, -1));

        jToggleButtonGetQ.setText("get");
        jToggleButtonGetQ.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetQ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetQActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetQ, new AbsoluteConstraints(370, 580, -1, -1));

        jToggleButtonMainSaveScopeIICfg.setText("save to Scope II cfg");
        jToggleButtonMainSaveScopeIICfg.setToolTipText("save the configuration file");
        jToggleButtonMainSaveScopeIICfg.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonMainSaveScopeIICfg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonMainSaveScopeIICfgActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonMainSaveScopeIICfg, new AbsoluteConstraints(10, 200, -1, -1));

        jRadioButtonEncodersActive.setText("encoders active");
        jRadioButtonEncodersActive.setToolTipText("'on' if encoders attached to telescope");
        jRadioButtonEncodersActive.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jPanelSiTech.add(jRadioButtonEncodersActive, new AbsoluteConstraints(140, 200, -1, -1));

        jToggleButtonVelocityCalculator.setText("velocity calculator");
        jToggleButtonVelocityCalculator.setToolTipText("open the velocity calculator window");
        jToggleButtonVelocityCalculator.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonVelocityCalculator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonVelocityCalculatorActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonVelocityCalculator, new AbsoluteConstraints(10, 220, -1, -1));

        jToggleButtonUpgradeFirmware.setText("upgrade firmware");
        jToggleButtonUpgradeFirmware.setToolTipText("upgrade the controller's software: an upgrade file from the manufacturer is required");
        jToggleButtonUpgradeFirmware.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonUpgradeFirmware.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonUpgradeFirmwareActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonUpgradeFirmware, new AbsoluteConstraints(10, 180, -1, -1));

        jToggleButtonGetXVelocity.setText("get");
        jToggleButtonGetXVelocity.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetXVelocity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetXVelocityActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetXVelocity, new AbsoluteConstraints(460, 100, -1, -1));

        jToggleButtonGetYVelocity.setText("get");
        jToggleButtonGetYVelocity.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetYVelocity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetYVelocityActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetYVelocity, new AbsoluteConstraints(740, 100, -1, -1));

        jToggleButtonGetXRamp.setText("get");
        jToggleButtonGetXRamp.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetXRamp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetXRampActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetXRamp, new AbsoluteConstraints(460, 260, -1, -1));

        jToggleButtonGetYRamp.setText("get");
        jToggleButtonGetYRamp.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetYRamp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetYRampActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetYRamp, new AbsoluteConstraints(740, 260, -1, -1));

        jToggleButtonGetYMaxPositionError.setText("get");
        jToggleButtonGetYMaxPositionError.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetYMaxPositionError.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetYMaxPositionErrorActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetYMaxPositionError, new AbsoluteConstraints(740, 300, -1, -1));

        jToggleButtonGetXMaxPositionError.setText("get");
        jToggleButtonGetXMaxPositionError.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetXMaxPositionError.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetXMaxPositionErrorActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetXMaxPositionError, new AbsoluteConstraints(460, 300, -1, -1));

        jToggleButtonStopAllMotors.setBackground(new java.awt.Color(255, 51, 0));
        jToggleButtonStopAllMotors.setText("all stop then track");
        jToggleButtonStopAllMotors.setToolTipText("stop all motors smoothly then start tracking");
        jToggleButtonStopAllMotors.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonStopAllMotors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonStopAllMotorsActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonStopAllMotors, new AbsoluteConstraints(90, 240, -1, -1));

        jLabelReturn.setText("RTN");
        jPanelSiTech.add(jLabelReturn, new AbsoluteConstraints(410, 580, -1, -1));

        jTextFieldReturn.setToolTipText("send 'X' command and display the results: \nX,Y pos., X,Y encoder pos., X,Y amps*100, volt*10, cpuTempF, auto/man");
        jPanelSiTech.add(jTextFieldReturn, new AbsoluteConstraints(440, 580, 320, -1));

        jToggleButtonGetReturn.setText("get");
        jToggleButtonGetReturn.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonGetReturn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonGetReturnActionPerformed(evt);
            }
        });

        jPanelSiTech.add(jToggleButtonGetReturn, new AbsoluteConstraints(770, 580, -1, -1));

        getContentPane().add(jPanelSiTech, new AbsoluteConstraints(0, 0, 800, 602));

        pack();
    }

    private void jToggleButtonExitActionPerformed(java.awt.event.ActionEvent evt) {
        exitForm(null);
    }

    private void jToggleButtonStopAllMotorsActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonStopAllMotors.setSelected(false);
        SiTechAsciiCmd.xmtCmd(SITECH_ASCII_CMDS.stopAllMotors.toString());
        getResults();
        xmtReturnGetResults();
    }

    private void jToggleButtonGetReturnActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetReturn.setSelected(false);
        SiTechAsciiCmd.xmtCmd(SITECH_ASCII_CMDS.getDefaultResponse.toString());
        jTextFieldReturn.setText(getResults());
        xmtReturnGetResults();
    }

    private void jToggleButtonUpgradeFirmwareActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonUpgradeFirmware.setSelected(false);

        SiTechFirmwareUploadFileChooser = new SiTechFirmwareUploadFileChooser(new JFrame(), "Sidereal Technology firmware upload file chooser");
        if (SiTechFirmwareUploadFileChooser.fileSelected) {
            Thread longWork = new Thread() {
                public void run() {
                    new uploadFirmwareFile(SiTechAsciiCmd.io(), SiTechFirmwareUploadFileChooser.file).upload();
                }
            };
            longWork.start();
        }
    }

    private void jToggleButtonSetYVelocityActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetYVelocity.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetYVelocity.toString(), jTextFieldModuleYVelocity);
    }

    private void jToggleButtonGetYVelocityActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetYVelocity.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetYVelocity.toString(), jTextFieldModuleYVelocity);
    }

    private void jToggleButtonSetXVelocityActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetXVelocity.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetXVelocity.toString(), jTextFieldModuleXVelocity);
    }

    private void jToggleButtonGetXVelocityActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetXVelocity.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetXVelocity.toString(), jTextFieldModuleXVelocity);
    }

    private void jToggleButtonSetYRampActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetYRamp.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetYRamp.toString(), jTextFieldModuleYRamp);
    }

    private void jToggleButtonGetYRampActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetYRamp.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetYRamp.toString(), jTextFieldModuleYRamp);
    }

    private void jToggleButtonSetXRampActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetXRamp.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetXRamp.toString(), jTextFieldModuleXRamp);
    }

    private void jToggleButtonGetXRampActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetXRamp.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetXRamp.toString(), jTextFieldModuleXRamp);
    }

    private void jToggleButtonGetYMaxPositionErrorActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetYMaxPositionError.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getYMaxPositionError.toString(), jTextFieldModuleYMaxPositionError);
    }

    private void jToggleButtonGetXMaxPositionErrorActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetXMaxPositionError.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getXMaxPositionError.toString(), jTextFieldModuleXMaxPositionError);
    }

    private void jToggleButtonVelocityCalculatorActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonVelocityCalculator.setSelected(false);
        new JFrameSiTechVelCalc().show();
    }

    private void jToggleButtonSetYMotorGuideRateActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetYMotorGuideRate.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetYMotorGuideRate.toString(), jTextFieldModuleYMotorGuideRate);
    }

    private void jToggleButtonGetYMotorGuideRateActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetYMotorGuideRate.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetYMotorGuideRate.toString(), jTextFieldModuleYMotorGuideRate);
    }

    private void jToggleButtonSetYMotorPanRateActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetYMotorPanRate.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetYMotorPanRate.toString(), jTextFieldModuleYMotorPanRate);
    }

    private void jToggleButtonGetYMotorPanRateActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetYMotorPanRate.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetYMotorPanRate.toString(), jTextFieldModuleYMotorPanRate);
    }

    private void jToggleButtonSetYMotorSlewRateActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetYMotorSlewRate.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetYMotorSlewRate.toString(), jTextFieldModuleYMotorSlewRate);
    }

    private void jToggleButtonGetYMotorSlewRateActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetYMotorSlewRate.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetYMotorSlewRate.toString(), jTextFieldModuleYMotorSlewRate);
    }

    private void jToggleButtonSetXMotorGuideRateActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetXMotorGuideRate.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetXMotorGuideRate.toString(), jTextFieldModuleXMotorGuideRate);
    }

    private void jToggleButtonGetXMotorGuideRateActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetXMotorGuideRate.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetXMotorGuideRate.toString(), jTextFieldModuleXMotorGuideRate);
    }

    private void jToggleButtonSetXMotorPanRateActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetXMotorPanRate.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetXMotorPanRate.toString(), jTextFieldModuleXMotorPanRate);
    }

    private void jToggleButtonGetXMotorPanRateActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetXMotorPanRate.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetXMotorPanRate.toString(), jTextFieldModuleXMotorPanRate);
    }

    private void jToggleButtonSetXMotorSlewRateActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetXMotorSlewRate.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetXMotorSlewRate.toString(), jTextFieldModuleXMotorSlewRate);
    }

    private void jToggleButtonGetXMotorSlewRateActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetXMotorSlewRate.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetXMotorSlewRate.toString(), jTextFieldModuleXMotorSlewRate);
    }

    private void jToggleButtonSetPlatformUpDnActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetPlatformUpDn.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetPlatformUpDown.toString(), jTextFieldModulePlatformUpDn);
    }

    private void jToggleButtonGetPlatformUpDnActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetPlatformUpDn.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetPlatformUpDown.toString(), jTextFieldModulePlatformUpDn);
    }

    private void jToggleButtonSetPlatformRateActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetPlatformRate.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetPlatformRate.toString(), jTextFieldModulePlatformRate);
    }

    private void jToggleButtonGetPlatformRateActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetPlatformRate.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetPlatformRate.toString(), jTextFieldModulePlatformRate);
    }

    private void jToggleButtonSetPlatformGoalActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetPlatformGoal.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetPlatformGoal.toString(), jTextFieldModulePlatformGoal);
    }

    private void jToggleButtonGetPlatformGoalActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetPlatformGoal.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetPlatformGoal.toString(), jTextFieldModulePlatformGoal);
    }

    private void jToggleButtonGetGuideModeActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetGuideMode.setSelected(false);
        jRadioButtonGuideMode.setSelected((SiTechAsciiCmd.getGuideMode()));
    }

    private void jToggleButtonSetGuideModeActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetGuideMode.setSelected(false);
        SiTechAsciiCmd.setGuideMode(jRadioButtonGuideMode.isSelected());
    }

    private void jToggleButtonSetDragAndTrackActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetDragAndTrack.setSelected(false);
        SiTechAsciiCmd.setXBits(servo.DRAG_N_DROP, jRadioButtonDragAndTrack.isSelected());
    }

    private void jToggleButtonGetDragAndTrackActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetDragAndTrack.setSelected(false);
        if (SiTechAsciiCmd.getXBitsValue())
            jRadioButtonDragAndTrack.setSelected((SiTechAsciiCmd.XBits() & servo.DRAG_N_DROP)==servo.DRAG_N_DROP? true:false);
    }

    private void jToggleButtonGetHandpadBiDirActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetHandpadBiDir.setSelected(false);
        if (SiTechAsciiCmd.getXBitsValue())
            jRadioButtonHandpadBiDir.setSelected((SiTechAsciiCmd.XBits() & servo.SIMUL_DIR_HANDPAD_DESIGN)==servo.SIMUL_DIR_HANDPAD_DESIGN? true:false);
    }

    private void jToggleButtonSetHandpadBiDirActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetHandpadBiDir.setSelected(false);
        SiTechAsciiCmd.setXBits(servo.SIMUL_DIR_HANDPAD_DESIGN, jRadioButtonHandpadBiDir.isSelected());
    }

    private void jToggleButtonGetEnableHandpadActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetEnableHandpad.setSelected(false);
        if (SiTechAsciiCmd.getXBitsValue())
            jRadioButtonEnableHandpad.setSelected((SiTechAsciiCmd.XBits() & servo.ENABLE_HANDPAD)==servo.ENABLE_HANDPAD? true:false);
    }

    private void jToggleButtonSetEnableHandpadActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetEnableHandpad.setSelected(false);
        SiTechAsciiCmd.setXBits(servo.ENABLE_HANDPAD, jRadioButtonHandpadBiDir.isSelected());
    }

    private void jToggleButtonSetEquatorialPlatformActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetEquatorialPlatform.setSelected(false);
        SiTechAsciiCmd.setXBits(servo.PLATFORM_MODE, jRadioButtonEquatorialPlatform.isSelected());
    }

    private void jToggleButtonGetEquatorialPlatformActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetEquatorialPlatform.setSelected(false);
        if (SiTechAsciiCmd.getXBitsValue())
            jRadioButtonEquatorialPlatform.setSelected((SiTechAsciiCmd.XBits() & servo.PLATFORM_MODE)==servo.PLATFORM_MODE? true:false);
    }

    private void jToggleButtonSetSlewAndTrackActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetSlewAndTrack.setSelected(false);
        SiTechAsciiCmd.setYBits(servo.SLEW_N_DROP, jRadioButtonSlewAndTrack.isSelected());
    }

    private void jToggleButtonGetSlewAndTrackActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetSlewAndTrack.setSelected(false);
        if (SiTechAsciiCmd.getYBitsValue())
            jRadioButtonSlewAndTrack.setSelected((SiTechAsciiCmd.YBits() & servo.SLEW_N_DROP)==servo.SLEW_N_DROP? true:false);
    }

    private void jToggleButtonGetXServoEncoderDirectionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetXServoEncoderDirection.setSelected(false);
        if (SiTechAsciiCmd.getXBitsValue())
            jRadioButtonXInvertServoEncoderDirection.setSelected((SiTechAsciiCmd.XBits() & servo.INVERT_SERVO_ENCODER)==servo.INVERT_SERVO_ENCODER? true:false);
    }

    private void jToggleButtonSetXServoEncoderDirectionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetXServoEncoderDirection.setSelected(false);
        SiTechAsciiCmd.setXBits(servo.INVERT_SERVO_ENCODER, jRadioButtonXInvertServoEncoderDirection.isSelected());
    }

    private void jToggleButtonGetYServoEncoderDirectionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetYServoEncoderDirection.setSelected(false);
        if (SiTechAsciiCmd.getYBitsValue())
            jRadioButtonYInvertServoEncoderDirection.setSelected((SiTechAsciiCmd.YBits() & servo.INVERT_SERVO_ENCODER)==servo.INVERT_SERVO_ENCODER? true:false);
    }

    private void jToggleButtonSetYServoEncoderDirectionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetYServoEncoderDirection.setSelected(false);
        SiTechAsciiCmd.setYBits(servo.INVERT_SERVO_ENCODER, jRadioButtonYInvertServoEncoderDirection.isSelected());
    }

    private void jToggleButtonSetYEncoderPositionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetYEncoderPosition.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetYEncoderPosition.toString(), jTextFieldModuleYEncoderPosition);
    }

    private void jToggleButtonGetYEncoderPositionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetYEncoderPosition.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetYEncoderPosition.toString(), jTextFieldModuleYEncoderPosition);
    }

    private void jToggleButtonSetYEncoderCountsPerRevolutionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetYEncoderCountsPerRevolution.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetYEncoderCountsPerRevolution.toString(), jTextFieldModuleYEncoderCountsPerRevolution);
    }

    private void jToggleButtonGetYEncoderCountsPerRevolutionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetYEncoderCountsPerRevolution.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetYEncoderCountsPerRevolution.toString(), jTextFieldModuleYEncoderCountsPerRevolution);
    }

    private void jToggleButtonGetXEncoderCountsPerRevolutionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetXEncoderCountsPerRevolution.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetXEncoderCountsPerRevolution.toString(), jTextFieldModuleXEncoderCountsPerRevolution);
    }

    private void jToggleButtonSetXEncoderCountsPerRevolutionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetXEncoderCountsPerRevolution.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetXEncoderCountsPerRevolution.toString(), jTextFieldModuleXEncoderCountsPerRevolution);
    }

    private void jToggleButtonGetYMotorCountsPerRevolutionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetYMotorCountsPerRevolution.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetYMotorCountsPerRevolution.toString(), jTextFieldModuleYMotorCountsPerRevolution);
    }

    private void jToggleButtonSetYMotorCountsPerRevolutionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetYMotorCountsPerRevolution.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetYMotorCountsPerRevolution.toString(), jTextFieldModuleYMotorCountsPerRevolution);
    }

    private void jToggleButtonGetXMotorCountsPerRevolutionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetXMotorCountsPerRevolution.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetXMotorCountsPerRevolution.toString(), jTextFieldModuleXMotorCountsPerRevolution);
    }

    private void jToggleButtonSetXMotorCountsPerRevolutionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetXMotorCountsPerRevolution.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetXMotorCountsPerRevolution.toString(), jTextFieldModuleXMotorCountsPerRevolution);
    }

    private void jToggleButtonSetXEncoderPositionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetXEncoderPosition.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetXEncoderPosition.toString(), jTextFieldModuleXEncoderPosition);
    }

    private void jToggleButtonGetXEncoderPositionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetXEncoderPosition.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetXEncoderPosition.toString(), jTextFieldModuleXEncoderPosition);
    }

    private void jToggleButtonSetYInvertEncoderDirectionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetYInvertEncoderDirection.setSelected(false);
        SiTechAsciiCmd.setYBits(servo.INVERT_SCOPE_ENCODER, jRadioButtonYInvertEncoderDirection.isSelected());
    }

    private void jToggleButtonGetYInvertEncoderDirectionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetYInvertEncoderDirection.setSelected(false);
        if (SiTechAsciiCmd.getYBitsValue())
            jRadioButtonYInvertEncoderDirection.setSelected((SiTechAsciiCmd.YBits() & servo.INVERT_SCOPE_ENCODER)==servo.INVERT_SCOPE_ENCODER? true:false);
    }

    private void jToggleButtonSetXInvertEncoderDirectionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetXInvertEncoderDirection.setSelected(false);
        SiTechAsciiCmd.setXBits(servo.INVERT_SCOPE_ENCODER, jRadioButtonXInvertEncoderDirection.isSelected());
    }

    private void jToggleButtonGetXInvertEncoderDirectionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetXInvertEncoderDirection.setSelected(false);
        if (SiTechAsciiCmd.getXBitsValue())
            jRadioButtonXInvertEncoderDirection.setSelected((SiTechAsciiCmd.XBits() & servo.INVERT_SCOPE_ENCODER)==servo.INVERT_SCOPE_ENCODER? true:false);
    }

    private void jToggleButtonGetYInvertMotorDirectionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetYInvertMotorDirection.setSelected(false);
        if (SiTechAsciiCmd.getYBitsValue())
            jRadioButtonYInvertMotorDirection.setSelected((SiTechAsciiCmd.YBits() & servo.INVERT_MOTOR_DIR)==servo.INVERT_MOTOR_DIR? true:false);
    }

    private void jToggleButtonSetYInvertMotorDirectionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetYInvertMotorDirection.setSelected(false);
        SiTechAsciiCmd.setYBits(servo.INVERT_MOTOR_DIR, jRadioButtonYInvertMotorDirection.isSelected());
    }

    private void jToggleButtonSetXInvertMotorDirectionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetXInvertMotorDirection.setSelected(false);
        SiTechAsciiCmd.setXBits(servo.INVERT_MOTOR_DIR, jRadioButtonXInvertMotorDirection.isSelected());
    }

    private void jToggleButtonGetXInvertMotorDirectionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetXInvertMotorDirection.setSelected(false);
        if (SiTechAsciiCmd.getXBitsValue())
            jRadioButtonXInvertMotorDirection.setSelected((SiTechAsciiCmd.XBits() & servo.INVERT_MOTOR_DIR)==servo.INVERT_MOTOR_DIR? true:false);
    }

    private void jToggleButtonYAutoPWMActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonYAutoPWM.setSelected(false);
        SiTechAsciiCmd.xmtCmd(SITECH_ASCII_CMDS.setYAutoPWM.toString());
        getResults();
        xmtReturnGetResults();
    }

    private void jToggleButtonSetYPWMActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetYPWM.setSelected(false);
        setValue(SITECH_ASCII_CMDS.setYPWM.toString(), jTextFieldModuleYPWM);
    }

    private void jToggleButtonGetYPWMActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetYPWM.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getYPWM.toString(), jTextFieldModuleYPWM);
    }

    private void jToggleButtonSetYOutputLimitActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetYOutputLimit.setSelected(false);
        setValue(SITECH_ASCII_CMDS.setYOutputLimit.toString(), jTextFieldModuleYOutputLimit);
    }

    private void jToggleButtonSetYCurrentLimitActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetYCurrentLimit.setSelected(false);
        setValue(SITECH_ASCII_CMDS.setYCurrentLimit.toString(), jTextFieldModuleYCurrentLimit);
    }

    private void jToggleButtonGetYIntegralActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetYIntegral.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetYIntegral.toString(), jTextFieldModuleYIntegral);
    }

    private void jToggleButtonGetYDerivativeActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetYDerivative.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetYDerivative.toString(), jTextFieldModuleYDerivative);
    }

    private void jToggleButtonGetYProportionalActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetYProportional.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetYProportional.toString(), jTextFieldModuleYProportional);
    }

    private void jToggleButtonGetXIntegralActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetXIntegral.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetXIntegral.toString(), jTextFieldModuleXIntegral);
    }

    private void jToggleButtonGetXDerivativeActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetXDerivative.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetXDerivative.toString(), jTextFieldModuleXDerivative);
    }

    private void jToggleButtonGetXProportionalActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetXProportional.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetXProportional.toString(), jTextFieldModuleXProportional);
    }

    private void jToggleButtonSetYIntegralLimitActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetYIntegralLimit.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetYIntegralLimit.toString(), jTextFieldModuleYIntegralLimit);
    }

    private void jToggleButtonGetYIntegralLimitActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetYIntegralLimit.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetYIntegralLimit.toString(), jTextFieldModuleYIntegralLimit);
    }

    private void jToggleButtonSetYIntegralActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetYIntegral.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetYIntegral.toString(), jTextFieldModuleYIntegral);
    }

    private void jToggleButtonSetYDerivativeActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetYDerivative.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetYDerivative.toString(), jTextFieldModuleYDerivative);
    }

    private void jToggleButtonSetYProportionalActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetYProportional.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetYProportional.toString(), jTextFieldModuleYProportional);
    }

    private void jToggleButtonXAutoPWMActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonXAutoPWM.setSelected(false);
        SiTechAsciiCmd.xmtCmd(SITECH_ASCII_CMDS.setXAutoPWM.toString());
        getResults();
        xmtReturnGetResults();
    }

    private void jToggleButtonSetXOutputLimitActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetXOutputLimit.setSelected(false);
        setValue(SITECH_ASCII_CMDS.setXOutputLimit.toString(), jTextFieldModuleXOutputLimit);
    }

    private void jToggleButtonSetXPWMActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetXPWM.setSelected(false);
        setValue(SITECH_ASCII_CMDS.setXPWM.toString(), jTextFieldModuleXPWM);
    }

    private void jToggleButtonGetXPWMActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetXPWM.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getXPWM.toString(), jTextFieldModuleXPWM);
    }

    private void jToggleButtonSetXCurrentLimitActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetXCurrentLimit.setSelected(false);
        setValue(SITECH_ASCII_CMDS.setXCurrentLimit.toString(), jTextFieldModuleXCurrentLimit);
    }

    private void jToggleButtonSetXIntegralLimitActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetXIntegralLimit.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetXIntegralLimit.toString(), jTextFieldModuleXIntegralLimit);
    }

    private void jToggleButtonGetXIntegralLimitActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetXIntegralLimit.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetXIntegralLimit.toString(), jTextFieldModuleXIntegralLimit);
    }

    private void jToggleButtonSetXIntegralActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetXIntegral.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetXIntegral.toString(), jTextFieldModuleXIntegral);
    }

    private void jToggleButtonSetXDerivativeActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetXDerivative.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetXDerivative.toString(), jTextFieldModuleXDerivative);
    }

    private void jToggleButtonSetXProportionalActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetXProportional.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetXProportional.toString(), jTextFieldModuleXProportional);
    }

    private void jToggleButtonSetYMaxPositionErrorActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetYMaxPositionError.setSelected(false);
        setValue(SITECH_ASCII_CMDS.setYMaxPositionError.toString(), jTextFieldModuleYMaxPositionError);
    }

    private void jToggleButtonGetYPositionErrorActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetYPositionError.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getYPositionError.toString(), jTextFieldModuleYPositionError);
    }

    private void jToggleButtonSetXMaxPositionErrorActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetXMaxPositionError.setSelected(false);
        setValue(SITECH_ASCII_CMDS.setXMaxPositionError.toString(), jTextFieldModuleXMaxPositionError);
    }

    private void jToggleButtonGetXPositionErrorActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetXPositionError.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getXPositionError.toString(), jTextFieldModuleXPositionError);
    }

    private void jToggleButtonGetYMotorCurrentActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetYMotorCurrent.setSelected(false);
        if (SiTechAsciiCmd.getValue(SITECH_ASCII_CMDS.getYMotorCurrent.toString())) {
            SiTechAsciiCmd.value(SiTechAsciiCmd.value()/100.);
            jTextFieldModuleYMotorCurrent.setText(eString.doubleToStringNoGrouping(SiTechAsciiCmd.value(), 2, 2) + " A");
        }
        else
            jTextFieldModuleYMotorCurrent.setText("");
    }

    private void jToggleButtonGetXMotorCurrentActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetXMotorCurrent.setSelected(false);
        if (SiTechAsciiCmd.getValue(SITECH_ASCII_CMDS.getXMotorCurrent.toString())) {
            SiTechAsciiCmd.value(SiTechAsciiCmd.value()/100.);
            jTextFieldModuleXMotorCurrent.setText(eString.doubleToStringNoGrouping(SiTechAsciiCmd.value(), 2, 2) + " A");
        }
        else
            jTextFieldModuleXMotorCurrent.setText("");
    }

    private void jToggleButtonYMoveActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonYMove.setSelected(false);
        setValue(SITECH_ASCII_CMDS.YMove.toString(), jTextFieldModuleYMoveToPosition);
    }

    private void jToggleButtonXMoveActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonXMove.setSelected(false);
        setValue(SITECH_ASCII_CMDS.XMove.toString(), jTextFieldModuleXMoveToPosition);
    }

    private void jToggleButtonSetXPositionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetXPosition.setSelected(false);
        setValue(SITECH_ASCII_CMDS.setXPosition.toString(), jTextFieldModuleXPosition);
    }

    private void jToggleButtonGetXPositionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetXPosition.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getXPosition.toString(), jTextFieldModuleXPosition);
    }

    private void jToggleButtonSetYPositionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetYPosition.setSelected(false);
        setValue(SITECH_ASCII_CMDS.setYPosition.toString(), jTextFieldModuleYPosition);
    }

    private void jToggleButtonGetYPositionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetYPosition.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getYPosition.toString(), jTextFieldModuleYPosition);
    }

    private void jToggleButtonYNormalStopActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonYNormalStop.setSelected(false);
        SiTechAsciiCmd.xmtCmd(SITECH_ASCII_CMDS.YNormalStop.toString());
        getResults();
        xmtReturnGetResults();
    }

    private void jToggleButtonYEmergencyStopActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonYEmergencyStop.setSelected(false);
        SiTechAsciiCmd.xmtCmd(SITECH_ASCII_CMDS.YEmergencyStop.toString());
        getResults();
        xmtReturnGetResults();
    }

    private void jToggleButtonXNormalStopActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonXNormalStop.setSelected(false);
        SiTechAsciiCmd.xmtCmd(SITECH_ASCII_CMDS.XNormalStop.toString());
        getResults();
        xmtReturnGetResults();
    }

    private void jToggleButtonXEmergencyStopActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonXEmergencyStop.setSelected(false);
        SiTechAsciiCmd.xmtCmd(SITECH_ASCII_CMDS.XEmergencyStop.toString());
        getResults();
        xmtReturnGetResults();
    }

    private void jToggleButtonGetKeypadActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetKeypad.setSelected(false);
        SiTechAsciiCmd.xmtCmd(SITECH_ASCII_CMDS.getKeypad.toString());
        jTextFieldKeypad.setText(getResults().substring(1));
    }

    private void jToggleButtonSetModuleAddressActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetModuleAddress.setSelected(false);
        SiTechAsciiCmd.xmtCmd(SITECH_ASCII_CMDS.setModuleAddress.toString() + jComboBoxModuleSetAddress.getSelectedItem());
        getResults();
        xmtReturnGetResults();
    }

    // possibly serial number might contain non-numeric chars, so treat it as String
    private void jToggleButtonGetSerialNumberActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetSerialNumber.setSelected(false);
        SiTechAsciiCmd.xmtCmd(SITECH_ASCII_CMDS.getSerialNum.toString());
        jTextFieldSerialNumber.setText(getResults());
    }

    private void jToggleButtonSetPICServoTimeOutActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetPICServoTimeOut.setSelected(false);
        setValue(SITECH_ASCII_CMDS.getSetPICServoTimeOut.toString(), jTextFieldModulePICServoTimeOut);
    }

    private void jToggleButtonGetPICServoTimeOutActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetPICServoTimeOut.setSelected(false);
        getValueSetTextField(SITECH_ASCII_CMDS.getSetPICServoTimeOut.toString(), jTextFieldModulePICServoTimeOut);
    }

    private void jToggleButtonSetLatitudeActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSetLatitude.setSelected(false);
        SiTechAsciiCmd.value(0);
        try {
            SiTechAsciiCmd.value(Double.parseDouble(jTextFieldModuleLatitude.getText()));
            SiTechAsciiCmd.xmtCmd(SITECH_ASCII_CMDS.getSetLatitude.toString() + (int) (SiTechAsciiCmd.value()*100.));
            getResults();
            xmtReturnGetResults();
        }
        catch (NumberFormatException nfe) {
            console.errOut("invalid number in JFrameSiTech.jToggleButtonSetLatitudeActionPerformed(): " + SiTechAsciiCmd.value());
        }
    }

    private void jToggleButtonModuleGetLatitudeActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonModuleGetLatitude.setSelected(false);
        if (SiTechAsciiCmd.getValue(SITECH_ASCII_CMDS.getSetLatitude.toString())) {
            SiTechAsciiCmd.value(SiTechAsciiCmd.value()/100.);
            jTextFieldModuleLatitude.setText(eString.doubleToStringNoGrouping(SiTechAsciiCmd.value(), 2, 2));
        }
        else
            jTextFieldModuleLatitude.setText("");
    }

    private void jToggleButtonGetCPUTemperatureActionPerformed(java.awt.event.ActionEvent evt) {
        double centigrade;

        jToggleButtonGetCPUTemperature.setSelected(false);
        if (SiTechAsciiCmd.getValue(SITECH_ASCII_CMDS.getCPUTemperature.toString())) {
            centigrade = (SiTechAsciiCmd.value() - 32) * 5/9;
            jTextFieldCPUTemperature.setText(eString.doubleToStringNoGrouping(SiTechAsciiCmd.value(), 3, 0)
            + " F "
            + eString.doubleToStringNoGrouping(centigrade, 2, 0)
            + " C");
        }
        else
            jTextFieldCPUTemperature.setText("");
    }

    private void jToggleButtonReadCfgFromFlashROMActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonReadCfgFromFlashROM.setSelected(false);
        SiTechAsciiCmd.xmtCmd(SITECH_ASCII_CMDS.readCfgFromFlashROM.toString());
        getResults();
        xmtReturnGetResults();
    }

    private void jToggleButtonWriteCfgToFlashROMActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonWriteCfgToFlashROM.setSelected(false);
        SiTechAsciiCmd.xmtCmd(SITECH_ASCII_CMDS.writeCfgFromFlashROM.toString());
        getResults();
        xmtReturnGetResults();
    }

    private void jToggleButtonProgramFactoryDefaultsIntoFlashROMActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonProgramFactoryDefaultsIntoFlashROM.setSelected(false);
        SiTechAsciiCmd.xmtCmd(SITECH_ASCII_CMDS.programFactoryDefaultsIntoFlashROM.toString());
        getResults();
        xmtReturnGetResults();
    }

    private void jToggleButtonGetVoltageActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetVoltage.setSelected(false);
        if (SiTechAsciiCmd.getValue(SITECH_ASCII_CMDS.getVoltage.toString())) {
            SiTechAsciiCmd.value(SiTechAsciiCmd.value()/10.);
            jTextFieldVoltage.setText(eString.doubleToStringNoGrouping(SiTechAsciiCmd.value(), 2, 2) + " V");
        }
        else
            jTextFieldVoltage.setText("");
    }

    private void jToggleButtonOpenTerminalWindowActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonOpenTerminalWindow.setSelected(false);
        JFrameTerminalMotors = new JFrameTerminal("Sidereal Technology controller terminal window");
        JFrameTerminalMotors.registerIOReference(SiTechAsciiCmd.io());
        JFrameTerminalMotors.appendReturn(true);
        JFrameTerminalMotors.start();
    }

    private void jToggleButtonConnectActionPerformed(java.awt.event.ActionEvent evt) {
        if (jToggleButtonConnect.isSelected()) {
            SiTechAsciiCmd.closePort();
            SiTechAsciiCmd.openPort();
            xmtReturnGetResults();
        }
        else
            SiTechAsciiCmd.closePort();
        setCommStatus();
    }

    private void jToggleButtonResetControllerActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonResetController.setSelected(false); 
        SiTechAsciiCmd.xmtCmd(SITECH_ASCII_CMDS.resetController.toString());
        getResults();
        xmtReturnGetResults();
    }

    private void jToggleButtonGetFirmwareVersionActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetFirmwareVersion.setSelected(false);
        SiTechAsciiCmd.xmtCmd(SITECH_ASCII_CMDS.getFirmwareVersion.toString());
        jTextFieldVersion.setText(getResults());
    }

    private void jToggleButtonSetAllActionPerformed(java.awt.event.ActionEvent evt) {
        int bits;
        int i;
        int ix;
        int chkSum;
        byte[] setAllCmd;

        SiTechAsciiCmd.value(0);
        jToggleButtonSetAll.setSelected(false);
        if (SiTechAsciiCmd.portOpened() && SiTechAsciiCmd.SiTechCfg.getAllStatus) {
            SiTechAsciiCmd.SiTechCfg.getAllBytesIx = 0;

            SiTechAsciiCmd.SiTechCfg.extractLongFromTextField(jTextFieldModuleXRamp);
            SiTechAsciiCmd.SiTechCfg.extractLongFromTextField(jTextFieldModuleXVelocity);
            SiTechAsciiCmd.SiTechCfg.extractIntFromTextField(jTextFieldModuleXMaxPositionError);
            SiTechAsciiCmd.SiTechCfg.extractIntFromTextField(jTextFieldModuleXProportional);
            SiTechAsciiCmd.SiTechCfg.extractIntFromTextField(jTextFieldModuleXIntegral);
            SiTechAsciiCmd.SiTechCfg.extractIntFromTextField(jTextFieldModuleXDerivative);
            SiTechAsciiCmd.SiTechCfg.extractIntFromTextField(jTextFieldModuleXOutputLimit);
            SiTechAsciiCmd.SiTechCfg.extractIntFromTextField(jTextFieldModuleXCurrentLimit);
            SiTechAsciiCmd.SiTechCfg.extractIntFromTextField(jTextFieldModuleXIntegralLimit);
            bits = 0;
            if (jRadioButtonGuideMode.isSelected())
                bits += (int) servo.GUIDE_MODE;
            if (jRadioButtonDragAndTrack.isSelected())
                bits += (int) servo.DRAG_N_DROP;
            if (jRadioButtonHandpadBiDir.isSelected())
                bits += (int) servo.SIMUL_DIR_HANDPAD_DESIGN;
            if (jRadioButtonEnableHandpad.isSelected())
                bits += (int) servo.ENABLE_HANDPAD;
            if (jRadioButtonEquatorialPlatform.isSelected())
                bits += (int) servo.PLATFORM_MODE;
            if (jRadioButtonXInvertServoEncoderDirection.isSelected())
                bits += (int) servo.INVERT_SERVO_ENCODER;
            if (jRadioButtonXInvertEncoderDirection.isSelected())
                bits += (int) servo.INVERT_SCOPE_ENCODER;
            if (jRadioButtonXInvertMotorDirection.isSelected())
                bits += (int) servo.INVERT_MOTOR_DIR;
            SiTechAsciiCmd.SiTechCfg.intValueToGetAllBytes(bits);

            SiTechAsciiCmd.SiTechCfg.extractLongFromTextField(jTextFieldModuleYRamp);
            SiTechAsciiCmd.SiTechCfg.extractLongFromTextField(jTextFieldModuleYVelocity);
            SiTechAsciiCmd.SiTechCfg.extractIntFromTextField(jTextFieldModuleYMaxPositionError);
            SiTechAsciiCmd.SiTechCfg.extractIntFromTextField(jTextFieldModuleYProportional);
            SiTechAsciiCmd.SiTechCfg.extractIntFromTextField(jTextFieldModuleYIntegral);
            SiTechAsciiCmd.SiTechCfg.extractIntFromTextField(jTextFieldModuleYDerivative);
            SiTechAsciiCmd.SiTechCfg.extractIntFromTextField(jTextFieldModuleYOutputLimit);
            SiTechAsciiCmd.SiTechCfg.extractIntFromTextField(jTextFieldModuleYCurrentLimit);
            SiTechAsciiCmd.SiTechCfg.extractIntFromTextField(jTextFieldModuleYIntegralLimit);
            bits = 0;
            if (jRadioButtonSlewAndTrack.isSelected())
                bits += (int) servo.SLEW_N_DROP;
            if (jRadioButtonYInvertServoEncoderDirection.isSelected())
                bits += (int) servo.INVERT_SERVO_ENCODER;
            if (jRadioButtonYInvertEncoderDirection.isSelected())
                bits += (int) servo.INVERT_SCOPE_ENCODER;
            if (jRadioButtonYInvertMotorDirection.isSelected())
                bits += (int) servo.INVERT_MOTOR_DIR;
            SiTechAsciiCmd.SiTechCfg.intValueToGetAllBytes(bits);

            SiTechAsciiCmd.SiTechCfg.extractIntFromTextField(jTextFieldModuleAddress);

            SiTechAsciiCmd.SiTechCfg.extractSignedLongFromTextField(jTextFieldModulePlatformRate);
            SiTechAsciiCmd.SiTechCfg.extractSignedLongFromTextField(jTextFieldModulePlatformUpDn);
            SiTechAsciiCmd.SiTechCfg.extractSignedLongFromTextField(jTextFieldModulePlatformGoal);

            try {
                SiTechAsciiCmd.value(Double.parseDouble(jTextFieldModuleLatitude.getText()));
                SiTechAsciiCmd.value(SiTechAsciiCmd.value()*100.);
                if (SiTechAsciiCmd.value() < 0)
                    SiTechAsciiCmd.value(SiTechAsciiCmd.value()+65536);
                SiTechAsciiCmd.SiTechCfg.intValueToGetAllBytesReverseByteOrder((int) SiTechAsciiCmd.value());
            }
            catch (NumberFormatException nfe) {
                console.errOut("invalid number in JFrameSiTech.jToggleButtonSetAllActionPerformed(): " + SiTechAsciiCmd.value());
            }

            SiTechAsciiCmd.SiTechCfg.extractSignedLongFromTextFieldReverseByteOrder(jTextFieldModuleYEncoderCountsPerRevolution);
            SiTechAsciiCmd.SiTechCfg.extractSignedLongFromTextFieldReverseByteOrder(jTextFieldModuleXEncoderCountsPerRevolution);
            SiTechAsciiCmd.SiTechCfg.extractSignedLongFromTextFieldReverseByteOrder(jTextFieldModuleYMotorCountsPerRevolution);
            SiTechAsciiCmd.SiTechCfg.extractSignedLongFromTextFieldReverseByteOrder(jTextFieldModuleXMotorCountsPerRevolution);
            SiTechAsciiCmd.SiTechCfg.extractSignedLongFromTextField(jTextFieldModuleXMotorSlewRate);
            SiTechAsciiCmd.SiTechCfg.extractSignedLongFromTextField(jTextFieldModuleYMotorSlewRate);
            SiTechAsciiCmd.SiTechCfg.extractSignedLongFromTextField(jTextFieldModuleXMotorPanRate);
            SiTechAsciiCmd.SiTechCfg.extractSignedLongFromTextField(jTextFieldModuleYMotorPanRate);
            SiTechAsciiCmd.SiTechCfg.extractSignedLongFromTextField(jTextFieldModuleXMotorGuideRate);
            SiTechAsciiCmd.SiTechCfg.extractSignedLongFromTextField(jTextFieldModuleYMotorGuideRate);

            SiTechAsciiCmd.SiTechCfg.extractIntFromTextField(jTextFieldModulePICServoTimeOut);

            // zero out remaing bytes
            SiTechAsciiCmd.SiTechCfg.zeroOutRemainingBytes();

            // get checksum
            chkSum = SiTechAsciiCmd.SiTechCfg.getChkSum();

            //SiTechAsciiCmd.SiTechCfg.displayGetAllBytes();
            //console.stdOutLn("checksum: "
            //+ chkSum
            //+ " (bytes "
            //+ (chkSum - 256*(chkSum/256))
            //+ " "
            //+ (chkSum/256)
            //+ ")");

            // 1st 3 bytes are composed of the 2 char command and return
            setAllCmd = SITECH_ASCII_CMDS.setAll.toString().getBytes();
            bArray[0] = setAllCmd[0];
            bArray[1] = setAllCmd[1];
            bArray[2] = eString.RETURN;
            // next bytes are the values to be set as determined by the textFields above
            SiTechAsciiCmd.SiTechCfg.fillByteArray(bArray, 3);
            // end with the checksum
            bArray[B_ARRAY_SIZE-2] = (byte) (chkSum - 256*(chkSum/256));
            bArray[B_ARRAY_SIZE-1] = (byte) (chkSum/256);

            console.stdOutLn("display of bArray");
            for (ix = 0; ix < B_ARRAY_SIZE; ix++)
                console.stdOut(eString.intToString((int) bArray[ix], 3) + "   ");
            console.stdOutLn("\nend display of bArray");

            SiTechAsciiCmd.io().writeByteArrayPauseUntilXmtFinished(bArray, B_ARRAY_SIZE);

            // display response: either BD for bad or OK for ok
            common.threadSleep(cfg.getInstance().servoPortWaitTimeMilliSecs);
            console.stdOut("response to set all: ");
            while (SiTechAsciiCmd.io().readSerialBuffer())
                console.stdOutChar((char) SiTechAsciiCmd.io().returnByteRead());
            console.stdOutLn("");

            // take data just sent, and place it in ram for the controller's use
            jToggleButtonReadCfgFromFlashROMActionPerformed(null);
        }
    }

    private void jToggleButtonGetQActionPerformed(java.awt.event.ActionEvent evt) {
        String s;
        String sDeg = "";
        String value = "";
        StringTokenizer st;
        azDouble azRaw = new azDouble();
        azDouble azDeg = new azDouble();
        
        jToggleButtonGetQ.setSelected(false);
        SiTechAsciiCmd.xmtCmd(SITECH_ASCII_CMDS.queryEncoders.toString());
        s = getResults();
        st = new StringTokenizer(s);
        if (st.countTokens() == 2)
            try {
                value = st.nextToken();
                // must be double as string is +9999 or -9999: the +- not allowed with ints
                azRaw.z = Double.parseDouble(value);
                azDeg.z = azRaw.z / 50.;
                value = st.nextToken();
                azRaw.a = Double.parseDouble(value);
                azDeg.a = azRaw.a / 50.;
                sDeg = "          azDeg="
                + eString.doubleToString(azDeg.z, 3, 2)
                + "   altDeg="
                + eString.doubleToString(azDeg.a, 3, 2);
            }
            catch (NumberFormatException nfe) {
                console.errOut("bad number token in JFrameSiTech.jToggleButtonGetQActionPerformed(): " + value);
            }
      
        jTextFieldQ.setText(s + sDeg);
        xmtReturnGetResults();
    }

    private void jToggleButtonGetAllActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonGetAll.setSelected(false);

        if (SiTechAsciiCmd.getAll()) {
            jTextFieldModuleXRamp.setText(eString.longToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.XRampSpeed, 10));
            jTextFieldModuleXVelocity.setText(eString.longToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.XVelocity, 10));
            jTextFieldModuleXMaxPositionError.setText(eString.intToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.XErrorLimit, 10));
            jTextFieldModuleXProportional.setText(eString.intToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.XPropBand, 10));
            jTextFieldModuleXIntegral.setText(eString.intToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.XIntegral, 10));
            jTextFieldModuleXDerivative.setText(eString.intToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.XDerivative, 10));
            jTextFieldModuleXOutputLimit.setText(eString.intToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.XOutLimit, 10));
            jTextFieldModuleXCurrentLimit.setText(eString.intToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.XCurrentLimit, 10));
            jTextFieldModuleXIntegralLimit.setText(eString.intToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.XIntegralLimit, 10));

            jRadioButtonGuideMode.setSelected((SiTechAsciiCmd.SiTechCfg.XBits & servo.GUIDE_MODE)==servo.GUIDE_MODE+256? true:false);
            jRadioButtonDragAndTrack.setSelected((SiTechAsciiCmd.SiTechCfg.XBits & servo.DRAG_N_DROP)==servo.DRAG_N_DROP? true:false);
            jRadioButtonHandpadBiDir.setSelected((SiTechAsciiCmd.SiTechCfg.XBits & servo.SIMUL_DIR_HANDPAD_DESIGN)==servo.SIMUL_DIR_HANDPAD_DESIGN? true:false);
            jRadioButtonEnableHandpad.setSelected((SiTechAsciiCmd.SiTechCfg.XBits & servo.ENABLE_HANDPAD)==servo.ENABLE_HANDPAD? true:false);
            jRadioButtonEquatorialPlatform.setSelected((SiTechAsciiCmd.SiTechCfg.XBits & servo.PLATFORM_MODE)==servo.PLATFORM_MODE? true:false);
            jRadioButtonXInvertServoEncoderDirection.setSelected((SiTechAsciiCmd.SiTechCfg.XBits & servo.INVERT_SERVO_ENCODER)==servo.INVERT_SERVO_ENCODER? true:false);
            jRadioButtonXInvertEncoderDirection.setSelected((SiTechAsciiCmd.SiTechCfg.XBits & servo.INVERT_SCOPE_ENCODER)==servo.INVERT_SCOPE_ENCODER? true:false);
            jRadioButtonXInvertMotorDirection.setSelected((SiTechAsciiCmd.SiTechCfg.XBits & servo.INVERT_MOTOR_DIR)==servo.INVERT_MOTOR_DIR? true:false);

            jTextFieldModuleYRamp.setText(eString.longToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.YRampSpeed, 10));
            jTextFieldModuleYVelocity.setText(eString.longToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.YVelocity, 10));
            jTextFieldModuleYMaxPositionError.setText(eString.intToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.YErrorLimit, 10));
            jTextFieldModuleYProportional.setText(eString.intToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.YPropBand, 10));
            jTextFieldModuleYIntegral.setText(eString.intToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.YIntegral, 10));
            jTextFieldModuleYDerivative.setText(eString.intToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.YDerivative, 10));
            jTextFieldModuleYOutputLimit.setText(eString.intToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.YOutLimit, 10));
            jTextFieldModuleYCurrentLimit.setText(eString.intToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.YCurrentLimit, 10));
            jTextFieldModuleYIntegralLimit.setText(eString.intToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.YIntegralLimit, 10));

            jRadioButtonSlewAndTrack.setSelected((SiTechAsciiCmd.SiTechCfg.YBits & servo.SLEW_N_DROP)==servo.SLEW_N_DROP? true:false);
            jRadioButtonYInvertServoEncoderDirection.setSelected((SiTechAsciiCmd.SiTechCfg.YBits & servo.INVERT_SERVO_ENCODER)==servo.INVERT_SERVO_ENCODER? true:false);
            jRadioButtonYInvertEncoderDirection.setSelected((SiTechAsciiCmd.SiTechCfg.YBits & servo.INVERT_SCOPE_ENCODER)==servo.INVERT_SCOPE_ENCODER? true:false);
            jRadioButtonYInvertMotorDirection.setSelected((SiTechAsciiCmd.SiTechCfg.YBits & servo.INVERT_MOTOR_DIR)==servo.INVERT_MOTOR_DIR? true:false);

            jTextFieldModuleAddress.setText(eString.intToString(SiTechAsciiCmd.SiTechCfg.ModuleAddress, 1));

            jTextFieldModulePlatformRate.setText(eString.longToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.PlatformTrackingRate, 10));
            jTextFieldModulePlatformUpDn.setText(eString.longToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.PlatformUpDownAdjust, 10));
            jTextFieldModulePlatformGoal.setText(eString.longToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.PlatformGoal, 10));

            jTextFieldModuleLatitude.setText(eString.doubleToStringNoGrouping((double) SiTechAsciiCmd.SiTechCfg.Latitude/100., 2, 2));

            jTextFieldModuleYEncoderCountsPerRevolution.setText(eString.longToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.AzimuthEncoderTicksPerRev, 10));
            jTextFieldModuleXEncoderCountsPerRevolution.setText(eString.longToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.AltitudeEncoderTicksPerRev, 10));
            jTextFieldModuleYMotorCountsPerRevolution.setText(eString.longToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.AzimuthMotorTicksPerRev, 10));
            jTextFieldModuleXMotorCountsPerRevolution.setText(eString.longToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.AltitudeMotorTicksPerRev, 10));
            jTextFieldModuleXMotorSlewRate.setText(eString.longToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.XSlewRate, 10));
            jTextFieldModuleYMotorSlewRate.setText(eString.longToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.YSlewRate, 10));
            jTextFieldModuleXMotorPanRate.setText(eString.longToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.XPanRate, 10));
            jTextFieldModuleYMotorPanRate.setText(eString.longToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.YPanRate, 10));
            jTextFieldModuleXMotorGuideRate.setText(eString.longToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.XGuideRate, 10));
            jTextFieldModuleYMotorGuideRate.setText(eString.longToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.YGuideRate, 10));

            jTextFieldModulePICServoTimeOut.setText(eString.intToStringNoGroupingNoLeadingZeros(SiTechAsciiCmd.SiTechCfg.PicServoCommTimeout, 10));
        }
        setGetAllStatus();
        xmtReturnGetResults();
    }

    private void jComboBoxCommPortItemStateChanged(java.awt.event.ItemEvent evt) {
        // only fire once, and don't fire when jComboBox model loaded
        if (evt.getStateChange() == evt.DESELECTED)
            cfg.getInstance().servoSerialPortName = jComboBoxCommPort.getSelectedItem().toString();
    }

    private void jToggleButtonMainSaveScopeIICfgActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonMainSaveScopeIICfg.setSelected(false);

        // cfg.getInstance().servoSerialPortName saved in jComboBoxCommPortItemStateChanged()

        cfg.getInstance().controllerManufacturer = CONTROLLER_MANUFACTURER.SiTech;

        if (jRadioButtonEncodersActive.isSelected())
            cfg.getInstance().encoderType = ENCODER_TYPE.encoderSiTech;
        else
            cfg.getInstance().encoderType = ENCODER_TYPE.encoderNone;

        cfg.getInstance().handpadPresent = jRadioButtonEnableHandpad.isSelected();

        if (jTextFieldModuleLatitude.getText().length() > 0)
            try {
                SiTechAsciiCmd.value(Double.parseDouble(jTextFieldModuleLatitude.getText()));
                cfg.getInstance().latitudeDeg = SiTechAsciiCmd.value();
            }
            catch (NumberFormatException nfe) {
                console.errOut("invalid number in JFrameSiTech.jToggleButtonMainSaveScopeIICfgActionPerformed(): " + SiTechAsciiCmd.value());
            }

        if (jTextFieldModuleXMotorCountsPerRevolution.getText().length() > 0)
            try {
                cfg.getInstance().spa.stepsPerRev = Double.parseDouble(jTextFieldModuleXMotorCountsPerRevolution.getText());
            }
            catch (NumberFormatException nfe) {
                console.errOut("invalid number in JFrameSiTech.jToggleButtonMainSaveScopeIICfgActionPerformed(): " + SiTechAsciiCmd.value());
            }

        if (jTextFieldModuleYMotorCountsPerRevolution.getText().length() > 0)
            try {
                cfg.getInstance().spz.stepsPerRev = Double.parseDouble(jTextFieldModuleYMotorCountsPerRevolution.getText());
            }
            catch (NumberFormatException nfe) {
                console.errOut("invalid number in JFrameSiTech.jToggleButtonMainSaveScopeIICfgActionPerformed(): " + SiTechAsciiCmd.value());
            }

        if (jTextFieldModuleXMaxPositionError.getText().length() > 0)
            try {
                cfg.getInstance().spa.positionErrorLimitEL = Integer.parseInt(jTextFieldModuleXMaxPositionError.getText());
            }
            catch (NumberFormatException nfe) {
                console.errOut("invalid number in JFrameSiTech.jToggleButtonMainSaveScopeIICfgActionPerformed(): " + SiTechAsciiCmd.value());
            }

        if (jTextFieldModuleYMaxPositionError.getText().length() > 0)
            try {
                cfg.getInstance().spz.positionErrorLimitEL = Integer.parseInt(jTextFieldModuleYMaxPositionError.getText());
            }
            catch (NumberFormatException nfe) {
                console.errOut("invalid number in JFrameSiTech.jToggleButtonMainSaveScopeIICfgActionPerformed(): " + SiTechAsciiCmd.value());
            }

        if (jTextFieldModuleXProportional.getText().length() > 0)
            try {
                cfg.getInstance().spa.positionGainKp = Integer.parseInt(jTextFieldModuleXProportional.getText());
            }
            catch (NumberFormatException nfe) {
                console.errOut("invalid number in JFrameSiTech.jToggleButtonMainSaveScopeIICfgActionPerformed(): " + SiTechAsciiCmd.value());
            }

        if (jTextFieldModuleYProportional.getText().length() > 0)
            try {
                cfg.getInstance().spz.positionGainKp = Integer.parseInt(jTextFieldModuleYProportional.getText());
            }
            catch (NumberFormatException nfe) {
                console.errOut("invalid number in JFrameSiTech.jToggleButtonMainSaveScopeIICfgActionPerformed(): " + SiTechAsciiCmd.value());
            }

        if (jTextFieldModuleXDerivative.getText().length() > 0)
            try {
                cfg.getInstance().spa.velGainKd = Integer.parseInt(jTextFieldModuleXDerivative.getText());
            }
            catch (NumberFormatException nfe) {
                console.errOut("invalid number in JFrameSiTech.jToggleButtonMainSaveScopeIICfgActionPerformed(): " + SiTechAsciiCmd.value());
            }

        if (jTextFieldModuleYDerivative.getText().length() > 0)
            try {
                cfg.getInstance().spz.velGainKd = Integer.parseInt(jTextFieldModuleYDerivative.getText());
            }
            catch (NumberFormatException nfe) {
                console.errOut("invalid number in JFrameSiTech.jToggleButtonMainSaveScopeIICfgActionPerformed(): " + SiTechAsciiCmd.value());
            }

        if (jTextFieldModuleXIntegral.getText().length() > 0)
            try {
                cfg.getInstance().spa.positionGainKi = Integer.parseInt(jTextFieldModuleXIntegral.getText());
            }
            catch (NumberFormatException nfe) {
                console.errOut("invalid number in JFrameSiTech.jToggleButtonMainSaveScopeIICfgActionPerformed(): " + SiTechAsciiCmd.value());
            }

        if (jTextFieldModuleYIntegral.getText().length() > 0)
            try {
                cfg.getInstance().spz.positionGainKi = Integer.parseInt(jTextFieldModuleYIntegral.getText());
            }
            catch (NumberFormatException nfe) {
                console.errOut("invalid number in JFrameSiTech.jToggleButtonMainSaveScopeIICfgActionPerformed(): " + SiTechAsciiCmd.value());
            }

        if (jTextFieldModuleXIntegralLimit.getText().length() > 0)
            try {
                cfg.getInstance().spa.integrationLimitIL = Integer.parseInt(jTextFieldModuleXIntegralLimit.getText());
            }
            catch (NumberFormatException nfe) {
                console.errOut("invalid number in JFrameSiTech.jToggleButtonMainSaveScopeIICfgActionPerformed(): " + SiTechAsciiCmd.value());
            }

        if (jTextFieldModuleYIntegralLimit.getText().length() > 0)
            try {
                cfg.getInstance().spz.integrationLimitIL = Integer.parseInt(jTextFieldModuleYIntegralLimit.getText());
            }
            catch (NumberFormatException nfe) {
                console.errOut("invalid number in JFrameSiTech.jToggleButtonMainSaveScopeIICfgActionPerformed(): " + SiTechAsciiCmd.value());
            }

        if (jTextFieldModuleXCurrentLimit.getText().length() > 0)
            try {
                cfg.getInstance().spa.currentLimitCL = Integer.parseInt(jTextFieldModuleXCurrentLimit.getText());
            }
            catch (NumberFormatException nfe) {
                console.errOut("invalid number in JFrameSiTech.jToggleButtonMainSaveScopeIICfgActionPerformed(): " + SiTechAsciiCmd.value());
            }

        if (jTextFieldModuleYCurrentLimit.getText().length() > 0)
            try {
                cfg.getInstance().spz.currentLimitCL = Integer.parseInt(jTextFieldModuleYCurrentLimit.getText());
            }
            catch (NumberFormatException nfe) {
                console.errOut("invalid number in JFrameSiTech.jToggleButtonMainSaveScopeIICfgActionPerformed(): " + SiTechAsciiCmd.value());
            }

        if (jTextFieldModuleXOutputLimit.getText().length() > 0)
            try {
                cfg.getInstance().spa.outputLimitOL = Integer.parseInt(jTextFieldModuleXOutputLimit.getText());
            }
            catch (NumberFormatException nfe) {
                console.errOut("invalid number in JFrameSiTech.jToggleButtonMainSaveScopeIICfgActionPerformed(): " + SiTechAsciiCmd.value());
            }

        if (jTextFieldModuleYOutputLimit.getText().length() > 0)
            try {
                cfg.getInstance().spz.outputLimitOL = Integer.parseInt(jTextFieldModuleYOutputLimit.getText());
            }
            catch (NumberFormatException nfe) {
                console.errOut("invalid number in JFrameSiTech.jToggleButtonMainSaveScopeIICfgActionPerformed(): " + SiTechAsciiCmd.value());
            }

        if (jRadioButtonXInvertEncoderDirection.isSelected())
            cfg.getInstance().encoderAltDecDir = ROTATION.CW;
        else
            cfg.getInstance().encoderAltDecDir = ROTATION.CCW;

        if (jRadioButtonYInvertEncoderDirection.isSelected())
            cfg.getInstance().encoderAzRaDir = ROTATION.CW;
        else
            cfg.getInstance().encoderAzRaDir = ROTATION.CCW;

        if (jTextFieldModuleXEncoderCountsPerRevolution.getText().length() > 0)
            try {
                cfg.getInstance().encoderAltDecCountsPerRev = Integer.parseInt(jTextFieldModuleXEncoderCountsPerRevolution.getText());
            }
            catch (NumberFormatException nfe) {
                console.errOut("invalid number in JFrameSiTech.jToggleButtonMainSaveScopeIICfgActionPerformed(): " + SiTechAsciiCmd.value());
            }

        if (jTextFieldModuleYEncoderCountsPerRevolution.getText().length() > 0)
            try {
                cfg.getInstance().encoderAzRaCountsPerRev = Integer.parseInt(jTextFieldModuleYEncoderCountsPerRevolution.getText());
            }
            catch (NumberFormatException nfe) {
                console.errOut("invalid number in JFrameSiTech.jToggleButtonMainSaveScopeIICfgActionPerformed(): " + SiTechAsciiCmd.value());
            }

        cfg.getInstance().write();
    }

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {
        if (t==null) {
            SiTechAsciiCmd.closePort();
            System.exit(0);
        }
        else {
            t.pauseSequencer = false;
            setVisible(false);
        }
    }

    void registerTrackReference(track t) {
        this.t = t;
        SiTechAsciiCmd.registerIo(t.io);
        setCommStatus();
        xmtReturnGetResults();
    }

    private boolean getValueSetTextField(String cmd, javax.swing.JTextField jTextField) {
        if (SiTechAsciiCmd.getValue(cmd)) {
            jTextField.setText(eString.doubleToStringNoGrouping(SiTechAsciiCmd.value(), 10, 0));
            return true;
        }
        else
            jTextField.setText("");
        return false;
    }

    boolean setValue(String cmd, javax.swing.JTextField jTextField) {
        return SiTechAsciiCmd.setValue(cmd, jTextField.getText());
    }
    
    private String getResults() {
        String results = SiTechAsciiCmd.getResults(); 
        setReturnStatus();
        return results;
    }

    private void xmtReturnGetResults() {
        SiTechAsciiCmd.xmtReturnGetResults();
        setReturnStatus();
    }

    void setReturnStatus() {
        if (SiTechAsciiCmd.returnStatus())
            jLabelStatusReturn.setForeground(new java.awt.Color(0, 227, 0));
        else
            jLabelStatusReturn.setForeground(new java.awt.Color(255, 0, 0));
    }

    void setGetAllStatus() {
        if (SiTechAsciiCmd.SiTechCfg.getAllStatus)
            jLabelStatusGetAllStatus.setForeground(new java.awt.Color(0, 227, 0));
        else
            jLabelStatusGetAllStatus.setForeground(new java.awt.Color(255, 0, 0));
    }

    void setCommStatus() {
        if (SiTechAsciiCmd.portOpened())
            jLabelStatusComm.setForeground(new java.awt.Color(0, 227, 0));
        else
            jLabelStatusComm.setForeground(new java.awt.Color(255, 0, 0));

        jToggleButtonConnect.setSelected(SiTechAsciiCmd.portOpened());
    }
}
