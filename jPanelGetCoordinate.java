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
 * the get coordinate panel
 */
public class jPanelGetCoordinate extends JPanelColor {
    private track t;
    private CMD_SCOPE cs;
    private findAllObjectsInDataDir faoidd;
    private dataFileChooserLoadListPos dfc;
    private IJListSelectPosition ijlsp;
    private listPosition lp;

    private PANEL_GET_COORDINATE_TYPE getCoordinateType;
    private closeJFrameMediator closeJFrameMediator;

    private updateLabelMediator jLabelSearchLibrarySelectedObjectMediator;
    private updateLabelMediator jLabelSearchDirectorySelectedObjectMediator;
    private updateLabelMediator jLabelSearchDataFileSelectedObjectMediator;

    private JPanelColor jPanelGetCoordinateViaDirectorySearch;
    private JPanelColor jPanelGetCoordinateViaFileSearch;
    private JPanelColor jPanelGetCoordinateViaInputAltaz;
    private JPanelColor jPanelGetCoordinateViaInputEquat;
    private JPanelColor jPanelGetCoordinateViaLibrarySearch;

    private JRadioButtonColor jRadioButtonAltazAbsolute;
    private JRadioButtonColor jRadioButtonAltazRelative;
    private JRadioButtonColor jRadioButtonEquatAbsolute;
    private JRadioButtonColor jRadioButtonEquatRelative;

    private JToggleButtonColor jToggleButtonAltazOK;
    private JToggleButtonColor jToggleButtonEquatOK;
    private JToggleButtonColor jToggleButtonSearchDataFileNearestObjects;
    private JToggleButtonColor jToggleButtonSearchDataFileOK;
    private JToggleButtonColor jToggleButtonSearchDataFileSelect;
    private JToggleButtonColor jToggleButtonSearchDirectoryNearestObjects;
    private JToggleButtonColor jToggleButtonSearchDirectoryOK;
    private JToggleButtonColor jToggleButtonSearchDirectorySelect;
    private JToggleButtonColor jToggleButtonSearchLibraryNearestObjects;
    private JToggleButtonColor jToggleButtonSearchLibraryOK;
    private JToggleButtonColor jToggleButtonSearchLibrarySearch;

    private javax.swing.ButtonGroup buttonGroupAltazAbsoluteRelative;
    private javax.swing.ButtonGroup buttonGroupEquatAbsoluteRelative;
    private JComboBoxColor jComboBoxGetCoordinateType;
    private javax.swing.JLabel jLabelGetCoordinate;
    private javax.swing.JLabel jLabelAlt;
    private javax.swing.JLabel jLabelAz;
    private javax.swing.JLabel jLabelAltazMoveTime;
    private javax.swing.JLabel jLabelDec;
    private javax.swing.JLabel jLabelDecDeg;
    private javax.swing.JLabel jLabelDecMin;
    private javax.swing.JLabel jLabelDecSec;
    private javax.swing.JLabel jLabelEpoch;
    private javax.swing.JLabel jLabelEquatMoveTime;
    private javax.swing.JLabel jLabelRA;
    private javax.swing.JLabel jLabelRaHr;
    private javax.swing.JLabel jLabelRaMin;
    private javax.swing.JLabel jLabelRaSec;
    private javax.swing.JLabel jLabelSearchDataFileMoveTime;
    private javax.swing.JLabel jLabelSearchDataFileSelectedFilename;
    private javax.swing.JLabel jLabelSearchDataFileSelectedObject;
    private javax.swing.JLabel jLabelSearchDirectoryMoveTime;
    private javax.swing.JLabel jLabelSearchDirectorySearchString;
    private javax.swing.JLabel jLabelSearchDirectorySelectedObject;
    private javax.swing.JLabel jLabelSearchLibraryMoveTime;
    private javax.swing.JLabel jLabelSearchLibrarySearchString;
    private javax.swing.JLabel jLabelSearchLibrarySelectedObject;
    private javax.swing.JTabbedPane jTabbedPaneGetCoordinate;
    private javax.swing.JTextField jTextFieldAlt;
    private javax.swing.JTextField jTextFieldAz;
    private javax.swing.JTextField jTextFieldAltazMoveTimeSec;
    private javax.swing.JTextField jTextFieldDecDeg;
    private javax.swing.JTextField jTextFieldDecMin;
    private javax.swing.JTextField jTextFieldDecSec;
    private javax.swing.JTextField jTextFieldEpoch;
    private javax.swing.JTextField jTextFieldEquatMoveTimeSec;
    private javax.swing.JTextField jTextFieldRaHr;
    private javax.swing.JTextField jTextFieldRaMin;
    private javax.swing.JTextField jTextFieldRaSec;
    private javax.swing.JTextField jTextFieldSearchDataFileMoveTimeSec;
    private javax.swing.JTextField jTextFieldSearchDirectoryMoveTimeSec;
    private javax.swing.JTextField jTextFieldSearchDirectorySearchString;
    private javax.swing.JTextField jTextFieldSearchLibraryMoveTimeSec;
    private javax.swing.JTextField jTextFieldSearchLibrarySearchString;

    public jPanelGetCoordinate() {
        mediumColor();

        lp = new listPosition();
        initComponents();

        updateGetCoordinateType(PANEL_GET_COORDINATE_TYPE.newTarget);

        jLabelSearchLibrarySelectedObjectMediator = new updateLabelMediator(jLabelSearchLibrarySelectedObject);
        jLabelSearchDirectorySelectedObjectMediator = new updateLabelMediator(jLabelSearchDirectorySelectedObject);
        jLabelSearchDataFileSelectedObjectMediator = new updateLabelMediator(jLabelSearchDataFileSelectedObject);

        jRadioButtonAltazAbsolute.setSelected(true);
        jRadioButtonEquatAbsolute.setSelected(true);

        Enumeration ePANEL_GET_COORDINATE_TYPE = PANEL_GET_COORDINATE_TYPE.elements();
        while (ePANEL_GET_COORDINATE_TYPE.hasMoreElements()) {
            PANEL_GET_COORDINATE_TYPE pct = (PANEL_GET_COORDINATE_TYPE) ePANEL_GET_COORDINATE_TYPE.nextElement();
            jComboBoxGetCoordinateType.addItem(pct.description);
        }
        jComboBoxGetCoordinateType.setSelectedItem(PANEL_GET_COORDINATE_TYPE.newTarget.description);
        getCoordinateType = PANEL_GET_COORDINATE_TYPE.newTarget;
    }

    void registerTrackReference(track t) {
        this.t = t;
    }

    void registerCloseJFrameMediator(closeJFrameMediator closeJFrameMediator) {
        this.closeJFrameMediator = closeJFrameMediator;
    }

    void updateGetCoordinateType(PANEL_GET_COORDINATE_TYPE getCoordinateType) {
        jComboBoxGetCoordinateType.setSelectedItem(getCoordinateType.description);
    }

    private void initComponents() {
        (jPanelGetCoordinateViaDirectorySearch = new JPanelColor()).lightColor();
        (jPanelGetCoordinateViaFileSearch = new JPanelColor()).lightColor();
        (jPanelGetCoordinateViaInputEquat = new JPanelColor()).lightColor();
        (jPanelGetCoordinateViaInputAltaz = new JPanelColor()).lightColor();
        (jPanelGetCoordinateViaLibrarySearch = new JPanelColor()).lightColor();

        jRadioButtonAltazAbsolute = new JRadioButtonColor();
        jRadioButtonAltazRelative = new JRadioButtonColor();
        jRadioButtonEquatAbsolute = new JRadioButtonColor();
        jRadioButtonEquatRelative = new JRadioButtonColor();

        jToggleButtonAltazOK = new JToggleButtonColor();
        jToggleButtonEquatOK = new JToggleButtonColor();

        jToggleButtonSearchDataFileNearestObjects = new JToggleButtonColor();
        jToggleButtonSearchDataFileOK = new JToggleButtonColor();
        jToggleButtonSearchDataFileSelect = new JToggleButtonColor();
        jToggleButtonSearchDirectoryNearestObjects = new JToggleButtonColor();
        jToggleButtonSearchDirectoryOK = new JToggleButtonColor();
        jToggleButtonSearchDirectorySelect = new JToggleButtonColor();
        jToggleButtonSearchLibraryNearestObjects = new JToggleButtonColor();
        jToggleButtonSearchLibraryOK = new JToggleButtonColor();
        jToggleButtonSearchLibrarySearch = new JToggleButtonColor();

        buttonGroupAltazAbsoluteRelative = new javax.swing.ButtonGroup();
        buttonGroupEquatAbsoluteRelative = new javax.swing.ButtonGroup();
        jComboBoxGetCoordinateType = new JComboBoxColor();
        jLabelGetCoordinate = new javax.swing.JLabel();
        jLabelAlt = new javax.swing.JLabel();
        jLabelAz = new javax.swing.JLabel();
        jLabelAltazMoveTime = new javax.swing.JLabel();
        jLabelDec = new javax.swing.JLabel();
        jLabelDecDeg = new javax.swing.JLabel();
        jLabelDecMin = new javax.swing.JLabel();
        jLabelDecSec = new javax.swing.JLabel();
        jLabelEpoch = new javax.swing.JLabel();
        jLabelEquatMoveTime = new javax.swing.JLabel();
        jLabelRA = new javax.swing.JLabel();
        jLabelRaHr = new javax.swing.JLabel();
        jLabelRaMin = new javax.swing.JLabel();
        jLabelRaSec = new javax.swing.JLabel();
        jLabelSearchDataFileMoveTime = new javax.swing.JLabel();
        jLabelSearchDataFileSelectedFilename = new javax.swing.JLabel();
        jLabelSearchDataFileSelectedObject = new javax.swing.JLabel();
        jLabelSearchDirectoryMoveTime = new javax.swing.JLabel();
        jLabelSearchDirectorySearchString = new javax.swing.JLabel();
        jLabelSearchDirectorySelectedObject = new javax.swing.JLabel();
        jLabelSearchLibraryMoveTime = new javax.swing.JLabel();
        jLabelSearchLibrarySearchString = new javax.swing.JLabel();
        jLabelSearchLibrarySelectedObject = new javax.swing.JLabel();
        jTabbedPaneGetCoordinate = new javax.swing.JTabbedPane();
        jTextFieldAlt = new javax.swing.JTextField();
        jTextFieldAltazMoveTimeSec = new javax.swing.JTextField();
        jTextFieldAz = new javax.swing.JTextField();
        jTextFieldDecDeg = new javax.swing.JTextField();
        jTextFieldDecMin = new javax.swing.JTextField();
        jTextFieldDecSec = new javax.swing.JTextField();
        jTextFieldEpoch = new javax.swing.JTextField();
        jTextFieldEquatMoveTimeSec = new javax.swing.JTextField();
        jTextFieldRaHr = new javax.swing.JTextField();
        jTextFieldRaMin = new javax.swing.JTextField();
        jTextFieldRaSec = new javax.swing.JTextField();
        jTextFieldSearchDataFileMoveTimeSec = new javax.swing.JTextField();
        jTextFieldSearchDirectoryMoveTimeSec = new javax.swing.JTextField();
        jTextFieldSearchDirectorySearchString = new javax.swing.JTextField();
        jTextFieldSearchLibrarySearchString = new javax.swing.JTextField();
        jTextFieldSearchLibraryMoveTimeSec = new javax.swing.JTextField();

        setLayout(new AbsoluteLayout());

        setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTabbedPaneGetCoordinate.setName("");
        jPanelGetCoordinateViaLibrarySearch.setLayout(new AbsoluteLayout());

        jLabelSearchLibrarySearchString.setText("object search string");
        jPanelGetCoordinateViaLibrarySearch.add(jLabelSearchLibrarySearchString, new AbsoluteConstraints(20, 40, -1, -1));

        jTextFieldSearchLibrarySearchString.setToolTipText("enter object name, ie, M1");
        jPanelGetCoordinateViaLibrarySearch.add(jTextFieldSearchLibrarySearchString, new AbsoluteConstraints(140, 40, 70, -1));

        jLabelSearchLibrarySelectedObject.setText("(selected object)");
        jLabelSearchLibrarySelectedObject.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        jPanelGetCoordinateViaLibrarySearch.add(jLabelSearchLibrarySelectedObject, new AbsoluteConstraints(20, 80, 250, -1));

        jTextFieldSearchLibraryMoveTimeSec.setToolTipText("if empty, then move will proceed at preset 'fast' speed");
        jPanelGetCoordinateViaLibrarySearch.add(jTextFieldSearchLibraryMoveTimeSec, new AbsoluteConstraints(220, 110, 50, -1));

        jToggleButtonSearchLibraryOK.setText("OK");
        jToggleButtonSearchLibraryOK.setToolTipText("object selection complete");
        jToggleButtonSearchLibraryOK.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSearchLibraryOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSearchLibraryOKActionPerformed(evt);
            }
        });

        jPanelGetCoordinateViaLibrarySearch.add(jToggleButtonSearchLibraryOK, new AbsoluteConstraints(320, 100, -1, -1));

        jLabelSearchLibraryMoveTime.setText("time for move in seconds");
        jPanelGetCoordinateViaLibrarySearch.add(jLabelSearchLibraryMoveTime, new AbsoluteConstraints(50, 110, -1, -1));

        jToggleButtonSearchLibrarySearch.setText("search");
        jToggleButtonSearchLibrarySearch.setToolTipText("search object database");
        jToggleButtonSearchLibrarySearch.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSearchLibrarySearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSearchLibrarySearchActionPerformed(evt);
            }
        });

        jPanelGetCoordinateViaLibrarySearch.add(jToggleButtonSearchLibrarySearch, new AbsoluteConstraints(230, 40, -1, -1));

        jToggleButtonSearchLibraryNearestObjects.setText("nearest objects");
        jToggleButtonSearchLibraryNearestObjects.setToolTipText("search for nearest objects to current equatorial coordinate");
        jToggleButtonSearchLibraryNearestObjects.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSearchLibraryNearestObjects.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSearchLibraryNearestObjectsActionPerformed(evt);
            }
        });

        jPanelGetCoordinateViaLibrarySearch.add(jToggleButtonSearchLibraryNearestObjects, new AbsoluteConstraints(20, 10, -1, -1));

        jTabbedPaneGetCoordinate.addTab("library", jPanelGetCoordinateViaLibrarySearch);

        jPanelGetCoordinateViaFileSearch.setLayout(new AbsoluteLayout());

        jToggleButtonSearchDataFileSelect.setText("select data file");
        jToggleButtonSearchDataFileSelect.setToolTipText("locate the datafile to search for an object");
        jToggleButtonSearchDataFileSelect.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSearchDataFileSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSearchDataFileSelectActionPerformed(evt);
            }
        });

        jPanelGetCoordinateViaFileSearch.add(jToggleButtonSearchDataFileSelect, new AbsoluteConstraints(20, 40, -1, -1));

        jLabelSearchDataFileSelectedObject.setText("(selected object)");
        jLabelSearchDataFileSelectedObject.setToolTipText("selected object's coordinate");
        jLabelSearchDataFileSelectedObject.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        jPanelGetCoordinateViaFileSearch.add(jLabelSearchDataFileSelectedObject, new AbsoluteConstraints(20, 80, 250, -1));

        jToggleButtonSearchDataFileOK.setText("OK");
        jToggleButtonSearchDataFileOK.setToolTipText("object selection complete");
        jToggleButtonSearchDataFileOK.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSearchDataFileOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSearchDataFileOKActionPerformed(evt);
            }
        });

        jPanelGetCoordinateViaFileSearch.add(jToggleButtonSearchDataFileOK, new AbsoluteConstraints(320, 100, -1, -1));

        jTextFieldSearchDataFileMoveTimeSec.setToolTipText("if empty, then move will proceed at preset 'fast' speed");
        jPanelGetCoordinateViaFileSearch.add(jTextFieldSearchDataFileMoveTimeSec, new AbsoluteConstraints(220, 110, 50, -1));

        jLabelSearchDataFileMoveTime.setText("time for move in seconds");
        jPanelGetCoordinateViaFileSearch.add(jLabelSearchDataFileMoveTime, new AbsoluteConstraints(50, 110, -1, -1));

        jToggleButtonSearchDataFileNearestObjects.setText("nearest objects");
        jToggleButtonSearchDataFileNearestObjects.setToolTipText("search for nearest objects to current equatorial coordinate");
        jToggleButtonSearchDataFileNearestObjects.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSearchDataFileNearestObjects.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSearchDataFileNearestObjectsActionPerformed(evt);
            }
        });

        jPanelGetCoordinateViaFileSearch.add(jToggleButtonSearchDataFileNearestObjects, new AbsoluteConstraints(20, 10, -1, -1));

        jLabelSearchDataFileSelectedFilename.setText("(selected file)");
        jLabelSearchDataFileSelectedFilename.setToolTipText("selected data file name");
        jPanelGetCoordinateViaFileSearch.add(jLabelSearchDataFileSelectedFilename, new AbsoluteConstraints(110, 40, 230, -1));

        jTabbedPaneGetCoordinate.addTab("datafile", jPanelGetCoordinateViaFileSearch);

        jPanelGetCoordinateViaInputEquat.setLayout(new AbsoluteLayout());

        jRadioButtonEquatAbsolute.setText("absolute");
        jRadioButtonEquatAbsolute.setToolTipText("coordinate entered will be in absolute coordinate");
        buttonGroupEquatAbsoluteRelative.add(jRadioButtonEquatAbsolute);
        jPanelGetCoordinateViaInputEquat.add(jRadioButtonEquatAbsolute, new AbsoluteConstraints(260, 30, -1, -1));

        jRadioButtonEquatRelative.setText("relative");
        jRadioButtonEquatRelative.setToolTipText("coordinate entered will be relative to current position");
        buttonGroupEquatAbsoluteRelative.add(jRadioButtonEquatRelative);
        jPanelGetCoordinateViaInputEquat.add(jRadioButtonEquatRelative, new AbsoluteConstraints(260, 50, -1, -1));

        jToggleButtonEquatOK.setText("OK");
        jToggleButtonEquatOK.setToolTipText("coordinate data entry complete");
        jToggleButtonEquatOK.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonEquatOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonEquatOKActionPerformed(evt);
            }
        });

        jPanelGetCoordinateViaInputEquat.add(jToggleButtonEquatOK, new AbsoluteConstraints(320, 100, -1, -1));

        jLabelEquatMoveTime.setText("time for move in seconds");
        jPanelGetCoordinateViaInputEquat.add(jLabelEquatMoveTime, new AbsoluteConstraints(50, 110, -1, -1));

        jTextFieldEquatMoveTimeSec.setToolTipText("if empty, then move will proceed at preset 'fast' speed");
        jPanelGetCoordinateViaInputEquat.add(jTextFieldEquatMoveTimeSec, new AbsoluteConstraints(220, 110, 50, -1));

        jLabelRA.setText("R.A.");
        jPanelGetCoordinateViaInputEquat.add(jLabelRA, new AbsoluteConstraints(30, 30, -1, -1));

        jTextFieldRaHr.setToolTipText("if empty, then zero assumed");
        jPanelGetCoordinateViaInputEquat.add(jTextFieldRaHr, new AbsoluteConstraints(60, 30, 30, -1));

        jLabelRaHr.setText("hr");
        jPanelGetCoordinateViaInputEquat.add(jLabelRaHr, new AbsoluteConstraints(90, 30, -1, -1));

        jLabelRaMin.setText("min");
        jPanelGetCoordinateViaInputEquat.add(jLabelRaMin, new AbsoluteConstraints(150, 30, -1, -1));

        jLabelRaSec.setText("sec");
        jPanelGetCoordinateViaInputEquat.add(jLabelRaSec, new AbsoluteConstraints(210, 30, -1, -1));

        jTextFieldRaMin.setToolTipText("if empty, then zero assumed");
        jPanelGetCoordinateViaInputEquat.add(jTextFieldRaMin, new AbsoluteConstraints(120, 30, 30, -1));

        jTextFieldRaSec.setToolTipText("if empty, then zero assumed");
        jPanelGetCoordinateViaInputEquat.add(jTextFieldRaSec, new AbsoluteConstraints(180, 30, 30, -1));

        jLabelDec.setText("Dec");
        jPanelGetCoordinateViaInputEquat.add(jLabelDec, new AbsoluteConstraints(30, 50, -1, -1));

        jTextFieldDecDeg.setToolTipText("if empty, then zero assumed");
        jPanelGetCoordinateViaInputEquat.add(jTextFieldDecDeg, new AbsoluteConstraints(60, 50, 30, -1));

        jLabelDecDeg.setText("deg");
        jPanelGetCoordinateViaInputEquat.add(jLabelDecDeg, new AbsoluteConstraints(90, 50, -1, -1));

        jTextFieldDecMin.setToolTipText("if empty, then zero assumed");
        jPanelGetCoordinateViaInputEquat.add(jTextFieldDecMin, new AbsoluteConstraints(120, 50, 30, -1));

        jLabelDecMin.setText("min");
        jPanelGetCoordinateViaInputEquat.add(jLabelDecMin, new AbsoluteConstraints(150, 50, -1, -1));

        jTextFieldDecSec.setToolTipText("if empty, then zero assumed");
        jPanelGetCoordinateViaInputEquat.add(jTextFieldDecSec, new AbsoluteConstraints(180, 50, 30, -1));

        jLabelDecSec.setText("sec");
        jPanelGetCoordinateViaInputEquat.add(jLabelDecSec, new AbsoluteConstraints(210, 50, -1, -1));

        jLabelEpoch.setText("epoch");
        jPanelGetCoordinateViaInputEquat.add(jLabelEpoch, new AbsoluteConstraints(30, 80, -1, -1));

        jTextFieldEpoch.setToolTipText("leave blank for current epoch");
        jPanelGetCoordinateViaInputEquat.add(jTextFieldEpoch, new AbsoluteConstraints(70, 80, 40, -1));

        jTabbedPaneGetCoordinate.addTab("equatorial", jPanelGetCoordinateViaInputEquat);

        jPanelGetCoordinateViaInputAltaz.setLayout(new AbsoluteLayout());

        jRadioButtonAltazAbsolute.setText("absolute");
        jRadioButtonAltazAbsolute.setToolTipText("coordinate entered will be in absolute coordinate");
        buttonGroupAltazAbsoluteRelative.add(jRadioButtonAltazAbsolute);
        jPanelGetCoordinateViaInputAltaz.add(jRadioButtonAltazAbsolute, new AbsoluteConstraints(250, 30, -1, -1));

        jRadioButtonAltazRelative.setText("relative");
        jRadioButtonAltazRelative.setToolTipText("coordinate entered will be relative to current position");
        buttonGroupAltazAbsoluteRelative.add(jRadioButtonAltazRelative);
        jPanelGetCoordinateViaInputAltaz.add(jRadioButtonAltazRelative, new AbsoluteConstraints(250, 50, -1, -1));

        jLabelAlt.setText("altitude degrees");
        jPanelGetCoordinateViaInputAltaz.add(jLabelAlt, new AbsoluteConstraints(50, 30, -1, -1));

        jLabelAz.setText("azimuth degrees");
        jPanelGetCoordinateViaInputAltaz.add(jLabelAz, new AbsoluteConstraints(50, 50, -1, -1));

        jTextFieldAlt.setToolTipText("if empty then current value assumed");
        jPanelGetCoordinateViaInputAltaz.add(jTextFieldAlt, new AbsoluteConstraints(150, 30, 60, 20));

        jTextFieldAz.setToolTipText("if empty then current value assumed");
        jPanelGetCoordinateViaInputAltaz.add(jTextFieldAz, new AbsoluteConstraints(150, 50, 60, -1));

        jToggleButtonAltazOK.setText("OK");
        jToggleButtonAltazOK.setToolTipText("coordinate data entry complete");
        jToggleButtonAltazOK.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonAltazOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonAltazOKActionPerformed(evt);
            }
        });

        jPanelGetCoordinateViaInputAltaz.add(jToggleButtonAltazOK, new AbsoluteConstraints(320, 100, -1, -1));

        jLabelAltazMoveTime.setText("time for move in seconds");
        jPanelGetCoordinateViaInputAltaz.add(jLabelAltazMoveTime, new AbsoluteConstraints(50, 110, -1, -1));

        jTextFieldAltazMoveTimeSec.setToolTipText("if empty, then move will proceed at preset 'fast' speed");
        jPanelGetCoordinateViaInputAltaz.add(jTextFieldAltazMoveTimeSec, new AbsoluteConstraints(220, 110, 50, -1));

        jTabbedPaneGetCoordinate.addTab("altazimuth", jPanelGetCoordinateViaInputAltaz);

        jPanelGetCoordinateViaDirectorySearch.setLayout(new AbsoluteLayout());

        jPanelGetCoordinateViaDirectorySearch.setName("");
        jToggleButtonSearchDirectorySelect.setText("select directory");
        jToggleButtonSearchDirectorySelect.setToolTipText("select the directory where the datafiles are located");
        jToggleButtonSearchDirectorySelect.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSearchDirectorySelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSearchDirectorySelectActionPerformed(evt);
            }
        });

        jPanelGetCoordinateViaDirectorySearch.add(jToggleButtonSearchDirectorySelect, new AbsoluteConstraints(230, 40, -1, -1));

        jLabelSearchDirectorySearchString.setText("object search string");
        jPanelGetCoordinateViaDirectorySearch.add(jLabelSearchDirectorySearchString, new AbsoluteConstraints(20, 40, -1, -1));

        jTextFieldSearchDirectorySearchString.setToolTipText("enter object name, ie, M1");
        jPanelGetCoordinateViaDirectorySearch.add(jTextFieldSearchDirectorySearchString, new AbsoluteConstraints(140, 40, 70, -1));

        jToggleButtonSearchDirectoryOK.setText("OK");
        jToggleButtonSearchDirectoryOK.setToolTipText("object selection complete");
        jToggleButtonSearchDirectoryOK.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        jToggleButtonSearchDirectoryOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSearchDirectoryOKActionPerformed(evt);
            }
        });

        jPanelGetCoordinateViaDirectorySearch.add(jToggleButtonSearchDirectoryOK, new AbsoluteConstraints(320, 100, -1, -1));

        jLabelSearchDirectorySelectedObject.setText("(selected object)");
        jLabelSearchDirectorySelectedObject.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        jPanelGetCoordinateViaDirectorySearch.add(jLabelSearchDirectorySelectedObject, new AbsoluteConstraints(20, 80, 250, -1));

        jTextFieldSearchDirectoryMoveTimeSec.setToolTipText("if empty, then move will proceed at preset 'fast' speed");
        jPanelGetCoordinateViaDirectorySearch.add(jTextFieldSearchDirectoryMoveTimeSec, new AbsoluteConstraints(220, 110, 50, -1));

        jLabelSearchDirectoryMoveTime.setText("time for move in seconds");
        jPanelGetCoordinateViaDirectorySearch.add(jLabelSearchDirectoryMoveTime, new AbsoluteConstraints(50, 110, -1, -1));

        jTabbedPaneGetCoordinate.addTab("directory", jPanelGetCoordinateViaDirectorySearch);

        add(jTabbedPaneGetCoordinate, new AbsoluteConstraints(0, 40, 360, 170));

        jLabelGetCoordinate.setText("Select Coordinate");
        add(jLabelGetCoordinate, new AbsoluteConstraints(40, 10, 120, -1));

        jComboBoxGetCoordinateType.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxGetCoordinateTypeItemStateChanged(evt);
            }
        });

        add(jComboBoxGetCoordinateType, new AbsoluteConstraints(180, 10, 130, -1));
    }

    private void jComboBoxGetCoordinateTypeItemStateChanged(java.awt.event.ItemEvent evt) {
        // only fire once, and don't fire when jComboBox model loaded
        if (evt.getStateChange() == evt.DESELECTED) {

            // update data model behind combo box
            getCoordinateType = PANEL_GET_COORDINATE_TYPE.matchDesc(jComboBoxGetCoordinateType.getSelectedItem().toString());

            if (getCoordinateType == PANEL_GET_COORDINATE_TYPE.newTarget) {

                jTextFieldSearchLibraryMoveTimeSec.setToolTipText("if empty, then move will proceed at preset 'fast' speed");
                jPanelGetCoordinateViaLibrarySearch.add(jTextFieldSearchLibraryMoveTimeSec, new AbsoluteConstraints(220, 110, 50, -1));

                jLabelSearchLibraryMoveTime.setText("time for move in seconds");
                jPanelGetCoordinateViaLibrarySearch.add(jLabelSearchLibraryMoveTime, new AbsoluteConstraints(50, 110, -1, -1));

                jPanelGetCoordinateViaLibrarySearch.repaint();

                jTextFieldSearchDirectoryMoveTimeSec.setToolTipText("if empty, then move will proceed at preset 'fast' speed");
                jPanelGetCoordinateViaDirectorySearch.add(jTextFieldSearchDirectoryMoveTimeSec, new AbsoluteConstraints(220, 110, 50, -1));

                jLabelSearchDirectoryMoveTime.setText("time for move in seconds");
                jPanelGetCoordinateViaDirectorySearch.add(jLabelSearchDirectoryMoveTime, new AbsoluteConstraints(50, 110, -1, -1));

                jPanelGetCoordinateViaDirectorySearch.repaint();

                jTextFieldSearchDataFileMoveTimeSec.setToolTipText("if empty, then move will proceed at preset 'fast' speed");
                jPanelGetCoordinateViaFileSearch.add(jTextFieldSearchDataFileMoveTimeSec, new AbsoluteConstraints(220, 110, 50, -1));

                jLabelSearchDataFileMoveTime.setText("time for move in seconds");
                jPanelGetCoordinateViaFileSearch.add(jLabelSearchDataFileMoveTime, new AbsoluteConstraints(50, 110, -1, -1));

                jPanelGetCoordinateViaFileSearch.repaint();

                jLabelEquatMoveTime.setText("time for move in seconds");
                jPanelGetCoordinateViaInputEquat.add(jLabelEquatMoveTime, new AbsoluteConstraints(50, 110, -1, -1));

                jTextFieldEquatMoveTimeSec.setToolTipText("if empty, then move will proceed at preset 'fast' speed");
                jPanelGetCoordinateViaInputEquat.add(jTextFieldEquatMoveTimeSec, new AbsoluteConstraints(220, 110, 50, -1));

                jPanelGetCoordinateViaInputEquat.repaint();

                jLabelAltazMoveTime.setText("time for move in seconds");
                jPanelGetCoordinateViaInputAltaz.add(jLabelAltazMoveTime, new AbsoluteConstraints(50, 110, -1, -1));

                jTextFieldAltazMoveTimeSec.setToolTipText("if empty, then move will proceed at preset 'fast' speed");
                jPanelGetCoordinateViaInputAltaz.add(jTextFieldAltazMoveTimeSec, new AbsoluteConstraints(220, 110, 50, -1));

                jPanelGetCoordinateViaInputAltaz.repaint();
            }
            else {
                jPanelGetCoordinateViaLibrarySearch.remove(jLabelSearchLibraryMoveTime);
                jPanelGetCoordinateViaLibrarySearch.remove(jTextFieldSearchLibraryMoveTimeSec);
                jPanelGetCoordinateViaFileSearch.remove(jLabelSearchDataFileMoveTime);
                jPanelGetCoordinateViaFileSearch.remove(jTextFieldSearchDataFileMoveTimeSec);
                jPanelGetCoordinateViaDirectorySearch.remove(jLabelSearchDirectoryMoveTime);
                jPanelGetCoordinateViaDirectorySearch.remove(jTextFieldSearchDirectoryMoveTimeSec);
                jPanelGetCoordinateViaInputEquat.remove(jLabelEquatMoveTime);
                jPanelGetCoordinateViaInputEquat.remove(jTextFieldEquatMoveTimeSec);
                jPanelGetCoordinateViaInputAltaz.remove(jLabelAltazMoveTime);
                jPanelGetCoordinateViaInputAltaz.remove(jTextFieldAltazMoveTimeSec);

                jPanelGetCoordinateViaDirectorySearch.repaint();
                jPanelGetCoordinateViaFileSearch.repaint();
                jPanelGetCoordinateViaInputEquat.repaint();
                jPanelGetCoordinateViaInputAltaz.repaint();
                jPanelGetCoordinateViaLibrarySearch.repaint();
            }
        }
    }

    private void jToggleButtonEquatOKActionPerformed(java.awt.event.ActionEvent evt) {
        position p;
        double year;
        boolean okToContinue;

        jToggleButtonEquatOK.setSelected(false);
        if (t != null) {
            if (jTextFieldRaHr.getText().length() == 0)
                jTextFieldRaHr.setText("0");
            if (jTextFieldRaMin.getText().length() == 0)
                jTextFieldRaMin.setText("0");
            if (jTextFieldRaSec.getText().length() == 0)
                jTextFieldRaSec.setText("0");
            if (jTextFieldDecDeg.getText().length() == 0)
                jTextFieldDecDeg.setText("0");
            if (jTextFieldDecMin.getText().length() == 0)
                jTextFieldDecMin.setText("0");
            if (jTextFieldDecSec.getText().length() == 0)
                jTextFieldDecSec.setText("0");

            p = new position("jToggleButtonEquatOK");
            okToContinue = true;

            if (okToContinue)
                try {
                    p.ra.hr = Integer.parseInt(jTextFieldRaHr.getText());
                }
                catch (NumberFormatException nfe) {
                    console.errOut("jToggleButtonEquatOKActionPerformed bad ra hr of " + jTextFieldRaHr.getText());
                    okToContinue = false;
                }
            if (okToContinue)
                try {
                    p.ra.min = Integer.parseInt(jTextFieldRaMin.getText());
                }
                catch (NumberFormatException nfe) {
                    console.errOut("jToggleButtonEquatOKActionPerformed bad ra min of " + jTextFieldRaMin.getText());
                    okToContinue = false;
                }
            if (okToContinue)
                try {
                    p.ra.sec = Integer.parseInt(jTextFieldRaSec.getText());
                }
                catch (NumberFormatException nfe) {
                    console.errOut("jToggleButtonEquatOKActionPerformed bad ra sec of " + jTextFieldRaSec.getText());
                    okToContinue = false;
                }
            if (okToContinue)
                try {
                    p.dec.deg = Integer.parseInt(jTextFieldDecDeg.getText());
                }
                catch (NumberFormatException nfe) {
                    console.errOut("jToggleButtonEquatOKActionPerformed bad dec deg of " + jTextFieldDecDeg.getText());
                    okToContinue = false;
                }
            if (okToContinue)
                try {
                    p.dec.min = Integer.parseInt(jTextFieldDecMin.getText());
                }
                catch (NumberFormatException nfe) {
                    console.errOut("jToggleButtonEquatOKActionPerformed bad dec min of " + jTextFieldDecMin.getText());
                    okToContinue = false;
                }
            if (okToContinue)
                try {
                    p.dec.sec = Integer.parseInt(jTextFieldDecSec.getText());
                }
                catch (NumberFormatException nfe) {
                    console.errOut("jToggleButtonEquatOKActionPerformed bad dec sec of " + jTextFieldDecSec.getText());
                    okToContinue = false;
                }
            if (okToContinue)
                if (jTextFieldEpoch.getText().length() != 0.) {
                    year = 0.;
                    try {
                        year = Double.parseDouble(jTextFieldEpoch.getText());
                    }
                    catch (NumberFormatException nfe) {
                        console.errOut("jToggleButtonEquatOKActionPerformed bad epoch of " + jTextFieldEpoch.getText());
                        okToContinue = false;
                    }
                    if (okToContinue) {
                        p.ra.calcRad();
                        p.dec.calcRad();
                        if (year != 0.)
                            p.applyPrecessionCorrectionFromEpochYear(year);
                        p.ra.getHMSM();
                        p.dec.getDMS();
                    }
                }
            if (okToContinue) {
                cs = setCmdScopeMediator(COORDINATE_TYPE.equat, jRadioButtonEquatAbsolute.isSelected());
                if (cs != null && p != null)
                    t.cmdCol.UICmd.newCmd("UI",
                    cs.toString()
                    + " "
                    + p.ra.hr
                    + " "
                    + p.ra.min
                    + " "
                    + p.ra.sec
                    + " "
                    + (p.dec.sign==units.MINUS?"-":"")
                    + p.dec.deg
                    + " "
                    + p.dec.min
                    + " "
                    + p.dec.sec
                    + " "
                    + jTextFieldEquatMoveTimeSec.getText()
                    + " "
                    + p.objName);
            }
        }
        if (closeJFrameMediator != null)
            closeJFrameMediator.close();
    }

    private void jToggleButtonSearchDataFileNearestObjectsActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSearchDataFileNearestObjects.setSelected(false);
        if (dfc.listPos.size() > 0) {
            lp.free();
            // get from already selected datafile's listPos
            dataFileListPositions.getInstance().findNearestObjectsFromListPos(20, lp, dfc.listPos);
            ijlsp = new JListSelectPositionFactory().buildWithAngsep(lp, jLabelSearchLibrarySelectedObjectMediator);
        }
    }

    private void jToggleButtonSearchLibraryNearestObjectsActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSearchLibraryNearestObjects.setSelected(false);
        lp.free();
        dataFileListPositions.getInstance().findNearestObjects(20, lp);
        ijlsp = new JListSelectPositionFactory().buildWithAngsep(lp, jLabelSearchLibrarySelectedObjectMediator);
    }

    private void jRadioButtonEquatRelativeActionPerformed(java.awt.event.ActionEvent evt) {}

    private void jRadioButtonEquatAbsoluteActionPerformed(java.awt.event.ActionEvent evt) {}

    private void jRadioButtonAltazRelativeActionPerformed(java.awt.event.ActionEvent evt) {}

    private void jRadioButtonAltazAbsoluteActionPerformed(java.awt.event.ActionEvent evt) {}

    private void jToggleButtonAltazOKActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonAltazOK.setSelected(false);
        if (t != null) {
            if (jTextFieldAlt.getText().length() == 0)
                jTextFieldAlt.setText("9999");
            if (jTextFieldAz.getText().length() == 0)
                jTextFieldAz.setText("9999");

            cs = setCmdScopeMediator(COORDINATE_TYPE.altaz, jRadioButtonAltazAbsolute.isSelected());
            if (cs != null)
                t.cmdCol.UICmd.newCmd("UI",
                cs.toString()
                + " "
                + jTextFieldAlt.getText()
                + " "
                + jTextFieldAz.getText()
                + " "
                + jTextFieldAltazMoveTimeSec.getText());
        }
        if (closeJFrameMediator != null)
            closeJFrameMediator.close();
    }

    private void jToggleButtonSearchDirectorySelectActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSearchDirectorySelect.setSelected(false);
        /**
         * create separate thread to be executed from the main application thread, include a .invokeLater Runnable()
         * to be executed from the event-dispatching thread queue to process component work;
         */
        Thread longWork = new Thread() {
            public void run() {
                faoidd = new findAllObjectsInDataDir(jTextFieldSearchDirectorySearchString.getText(), false);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (faoidd.dirSelected()) {
                            //faoidd.listPos.showDataFileFormat();
                            ijlsp = new JListSelectPositionFactory().build(faoidd.listPos, jLabelSearchDirectorySelectedObjectMediator);
                            // thread now stops as there is nothing more to execute
                        }
                    }
                });
            }
        };
        longWork.start();
    }

    private void jToggleButtonSearchDirectoryOKActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSearchDirectoryOK.setSelected(false);
        launchCmdScopeMediator();
    }

    private void launchCmdScopeMediator() {
        if (t != null) {
            cs = setCmdScopeMediator(COORDINATE_TYPE.equat, true);
            if (cs != null && ijlsp != null && ijlsp.selectedPosition() != null)
                t.cmdCol.UICmd.newCmd("UI",
                cs.toString()
                + " "
                + ijlsp.selectedPosition().ra.getStringHMS(eString.SPACE)
                + " "
                + ijlsp.selectedPosition().dec.getStringDMS(eString.SPACE)
                + " 0 "
                + ijlsp.selectedPosition().objName);
        }
        if (closeJFrameMediator != null)
            closeJFrameMediator.close();
    }

    private void jToggleButtonSearchLibrarySearchActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSearchLibrarySearch.setSelected(false);
        lp.free();
        dataFileListPositions.getInstance().findAllObjects(jTextFieldSearchLibrarySearchString.getText(), lp);
        ijlsp = new JListSelectPositionFactory().build(lp, jLabelSearchLibrarySelectedObjectMediator);
    }

    private void jToggleButtonSearchLibraryOKActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSearchLibraryOK.setSelected(false);
        launchCmdScopeMediator();
    }

    private void jToggleButtonSearchDataFileSelectActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSearchDataFileSelect.setSelected(false);
        dfc = new dataFileChooserLoadListPos(new JFrame());
        if (dfc.fileSelected) {
            //dfc.listPos.showDataFileFormat();
            ijlsp = new JListSelectPositionFactory().build(dfc.listPos, jLabelSearchDataFileSelectedObjectMediator);
            jLabelSearchDataFileSelectedFilename.setText(dfc.file.getAbsoluteFile().toString());
        }
    }

    private void jToggleButtonSearchDataFileOKActionPerformed(java.awt.event.ActionEvent evt) {
        jToggleButtonSearchDataFileOK.setSelected(false);
        launchCmdScopeMediator();
    }

    private CMD_SCOPE setCmdScopeMediator(COORDINATE_TYPE coordinateType, boolean absolute) {
        if (getCoordinateType == PANEL_GET_COORDINATE_TYPE.newTarget)
            if (coordinateType == COORDINATE_TYPE.equat)
                if (absolute)
                    return CMD_SCOPE.cmd_scope_slew_abs_equat;
                else
                    return CMD_SCOPE.cmd_scope_slew_off_equat;
            else
                if (absolute)
                    return CMD_SCOPE.cmd_scope_slew_abs_altaz;
                else
                    return CMD_SCOPE.cmd_scope_slew_off_altaz;

        if (getCoordinateType == PANEL_GET_COORDINATE_TYPE.resetCurrent)
            if (coordinateType == COORDINATE_TYPE.equat)
                if (absolute)
                    return CMD_SCOPE.cmd_scope_reset_abs_equat;
                else
                    return CMD_SCOPE.cmd_scope_reset_off_equat;
            else
                if (absolute)
                    return CMD_SCOPE.cmd_scope_reset_abs_altaz;
                else
                    return CMD_SCOPE.cmd_scope_reset_off_altaz;

        if (getCoordinateType == PANEL_GET_COORDINATE_TYPE.setInput)
            if (coordinateType == COORDINATE_TYPE.equat)
                if (absolute)
                    return CMD_SCOPE.cmd_scope_set_abs_input_equat;
                else
                    return CMD_SCOPE.cmd_scope_set_off_input_equat;
            else
                if (absolute)
                    return CMD_SCOPE.cmd_scope_set_abs_input_altaz;
                else
                    return CMD_SCOPE.cmd_scope_set_off_input_altaz;

        if (getCoordinateType == PANEL_GET_COORDINATE_TYPE.autoInit1)
            return CMD_SCOPE.cmd_scope_autoInit1;

        if (getCoordinateType == PANEL_GET_COORDINATE_TYPE.autoInit2)
            return CMD_SCOPE.cmd_scope_autoInit2;

        if (getCoordinateType == PANEL_GET_COORDINATE_TYPE.init1)
            return CMD_SCOPE.cmd_scope_init1;

        if (getCoordinateType == PANEL_GET_COORDINATE_TYPE.init2)
            return CMD_SCOPE.cmd_scope_init2;

        if (getCoordinateType == PANEL_GET_COORDINATE_TYPE.init3)
            return CMD_SCOPE.cmd_scope_init3;

        if (getCoordinateType == PANEL_GET_COORDINATE_TYPE.polarAlign1)
            return CMD_SCOPE.cmd_scope_polarAlign1;

        if (getCoordinateType == PANEL_GET_COORDINATE_TYPE.polarAlign2)
            return CMD_SCOPE.cmd_scope_polarAlign2;

        if (getCoordinateType == PANEL_GET_COORDINATE_TYPE.polarAlign3)
            return CMD_SCOPE.cmd_scope_polarAlign3;

        console.errOut("jPanelGetCoordinate.setCmdScopeMediator() could not return a value, "
        + "getCoordinateType="
        + (getCoordinateType==null?"null":getCoordinateType.toString())
        + " coordinateType="
        + (coordinateType==null?"null":coordinateType.toString())
        + " absolute="
        + absolute);

        return null;
    }

    position selectedPosition() {
        if (ijlsp != null)
            return ijlsp.selectedPosition();
        return null;
    }
}

