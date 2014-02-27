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

public class runTests {
    int mainSelect;
    boolean quit;
    convertMatrix c;
    guideParms gp;
    position in;

    runTests() {
        c = new convertMatrix("Test");
        c.init(cfg.getInstance().initState);
        gp = new guideParms();
        in = new position("runTests");
    }
    void runTestsFromConsole() {
        int maxColWidth;
        int ix;
        int row;
        int maxRow;
        TEST_SELECT tscol1;
        TEST_SELECT tscol2;

        while (!quit) {
            do {
                System.out.println("\nrunning tests: select from");

                maxColWidth = 0;
                for (ix = 0; ix < TEST_SELECT.size(); ix++)
                    if (TEST_SELECT.matchKey(ix).toString().length() > maxColWidth)
                        maxColWidth = TEST_SELECT.matchKey(ix).toString().length();

                maxRow = TEST_SELECT.size()/2 + TEST_SELECT.size()%2;
                for (row = 0; row < maxRow; row++) {
                    tscol1 = TEST_SELECT.matchKey(row);
                    tscol2 = TEST_SELECT.matchKey(row + maxRow);
                    System.out.print("   "
                    + (row+1)
                    + ". "
                    + eString.padString(tscol1.toString(), maxColWidth+2));
                    if (row < 9)
                        System.out.print(" ");
                    if (tscol2 == null)
                        System.out.println("");
                    else
                        System.out.println((row+1+maxRow)
                        + ". "
                        + tscol2);
                }
                console.getInt();
                mainSelect = console.i;
            }
            while (mainSelect < 1 || mainSelect > TEST_SELECT.size());

            runATest();
        }
    }

    void runATest() {
        int id;
        listPosition lp = new listPosition();
        PMC PMC;
        servo s;
        track t;
        trackBuilder tb;
        Iterator it;

        if (mainSelect == TEST_SELECT.SERVO_ID.KEY+1) {
            System.out.println("   display of SERVO_ID using display():");
            SERVO_ID.display();
            System.out.println("");

            System.out.println("   display of SERVO_ID using returnItemListAsString():");
            System.out.println(SERVO_ID.returnItemListAsString());

            System.out.println("   display of SERVO_ID using Enumeration:");
            // SERVO_ID already substantiated, not necessary to create an Enumeration object
            Enumeration eSERVO_ID = SERVO_ID.elements();
            while (eSERVO_ID.hasMoreElements()) {
                SERVO_ID si = (SERVO_ID) eSERVO_ID.nextElement();
                System.out.println(si
                + ": "
                + si.KEY);
            }
            System.out.println("   displaying KEY=1 via matchKey(): " + SERVO_ID.matchKey(1));

            System.out.println("   displaying 'focus' via matchStr(): " + SERVO_ID.matchStr("focus"));
        }
        else if (mainSelect == TEST_SELECT.eString.KEY+1)
            eString.test();
        else if (mainSelect == TEST_SELECT.cfg.KEY+1)
            cfg.getInstance().askAndWrite();
        else if (mainSelect == TEST_SELECT.aTimes.KEY+1)
            astroTime.getInstance().test();
        else if (mainSelect == TEST_SELECT.beep.KEY+1)
            common.testBeep();
        else if (mainSelect == TEST_SELECT.refract.KEY+1) {
            refract r = new refract();
            r.test();
        }
        else if (mainSelect == TEST_SELECT.equatRefract.KEY+1)
            c.testEquatRefract();
        else if (mainSelect == TEST_SELECT.precession.KEY+1) {
            precession p = new precession();
            p.test();
        }
        else if (mainSelect == TEST_SELECT.nutation.KEY+1) {
            nutation n = new nutation();
            n.test();
        }
        else if (mainSelect == TEST_SELECT.annualAberration.KEY+1) {
            annualAberration aa = new annualAberration();
            aa.test();
        }
        else if (mainSelect == TEST_SELECT.coordinateCorrections.KEY+1) {
            testCoordinateCorrections tcc = new testCoordinateCorrections();
        }
        else if (mainSelect == TEST_SELECT.llPos.KEY+1)
            if (c.loadAnalysisFileIntoMemory(lp))
                lp.test();
            else
                System.out.println("could not find " + c.analysisFile);
        else if (mainSelect == TEST_SELECT.reInitConversionMatrix.KEY+1) {
            System.out.print("please enter new z1 error in degrees ");
            console.getDouble();
            cfg.getInstance().z1Deg = console.d;
            System.out.print("please enter new z2 error in degrees ");
            console.getDouble();
            cfg.getInstance().z2Deg = console.d;
            System.out.print("please enter new z3 error in degrees ");
            console.getDouble();
            cfg.getInstance().z3Deg = console.d;

            c.setMountErrorsDeg(cfg.getInstance().z1Deg, cfg.getInstance().z2Deg, cfg.getInstance().z3Deg);
            cfg.getInstance().setInitStateFromCmdLine();
            c.init(cfg.getInstance().initState);
            if (cfg.getInstance().one.init)
                cfg.getInstance().one.showCoordDeg();
            if (cfg.getInstance().two.init)
                cfg.getInstance().two.showCoordDeg();
        }
        else if (mainSelect == TEST_SELECT.trackRates.KEY+1) {
            trackRates tr = new trackRates();
            tr.test();
        }
        else if (mainSelect == TEST_SELECT.convertTrig.KEY+1) {
            convertTrig ct = new convertTrig("test");
            ct.test();
        }
        else if (mainSelect == TEST_SELECT.convertMatrix.KEY+1)
            c.test();
        else if (mainSelect == TEST_SELECT.coordHysteresis.KEY+1)
            c.testHysteresis();
        else if (mainSelect == TEST_SELECT.fieldRotation.KEY+1)
            c.testFieldRotation();
        else if (mainSelect == TEST_SELECT.altAltAzTrack.KEY+1)
            c.testAltAltAzTrack();
        else if (mainSelect == TEST_SELECT.bestZ123FromAlteredPosition.KEY+1)
            c.testBestZ123FromAlteredPosition();
        else if (mainSelect == TEST_SELECT.setZ123RtnErrFromAnalysisFile.KEY+1)
            if (c.setZ123RtnErrFromAnalysisFile(lp))
                ;
            else
                System.out.println("no init1 or init2, or, could not find "
                + c.analysisFile
                + ", or file empty");
        else if (mainSelect == TEST_SELECT.bestZ123FromAnalysisFile.KEY+1)
            c.testComputeBestZ123FromAnalysisFile(lp);
        else if (mainSelect == TEST_SELECT.z12Comparison.KEY+1)
            c.testZ12Compare();
        else if (mainSelect == TEST_SELECT.altOffset.KEY+1)
            c.testAltOffset();
        else if (mainSelect == TEST_SELECT.altOffsetFromCfgOneTwoPos.KEY+1) {
            c.calcAltOffsetIteratively(cfg.getInstance().one, cfg.getInstance().two);
            System.out.println("Iterative altOffset "
            + eString.doubleToStringNoGrouping(c.altOffset * units.RAD_TO_DEG, 3, 3)
            + " one.alt "
            + eString.doubleToStringNoGrouping((cfg.getInstance().one.alt.rad + c.altOffset) * units.RAD_TO_DEG, 3, 3)
            + " two.alt "
            + eString.doubleToStringNoGrouping((cfg.getInstance().two.alt.rad + c.altOffset) * units.RAD_TO_DEG, 3, 3));
        }
        else if (mainSelect == TEST_SELECT.linkGuide.KEY+1) {
            linkGuideTest lgt = new linkGuideTest();
        }
        else if (mainSelect == TEST_SELECT.guide.KEY+1) {
            gp.servoIDStr = SERVO_ID.altDec.toString();
            gp.descriptStr = "Main";
            gp.PECSize = 200;
            gp.PECRotation = ROTATION.biDir;
            gp.guidingCycles = 10;
            gp.PECIxOffset = 0.;
            System.out.println("a PEC table should be created");
            guide g = new guide(gp);
        }
        else if (mainSelect == TEST_SELECT.PEC.KEY+1) {
            gp.servoIDStr = SERVO_ID.altDec.toString();
            gp.descriptStr = "Main";
            gp.motorStepsPerPECArray = 2048;
            gp.PECSize = 200;
            gp.PECRotation = ROTATION.biDir;
            gp.guidingCycles = 10;
            gp.PECIxOffset = 0.;
            System.out.println("a PEC table should be created");
            PEC p = new PEC(gp.servoIDStr, gp.descriptStr, gp.motorStepsPerPECArray, gp.PECSize, gp.PECRotation, gp.PECIxOffset);
        }
        else if (mainSelect == TEST_SELECT.axisToAxisEC.KEY+1) {
            axisToAxisEC aa = new axisToAxisEC(AXIS_TO_AXIS_EC.altAz);
            if (c.loadAnalysisFileIntoMemory(lp)) {
                c.calcAnalysisErrors(lp);
                c.writeAnalysisFile(lp);
                aa.build_EC_FromAnalysisErrorsInMemory(lp);
                aa.saveToFile();
            }
            else
                System.out.println("could not find " + c.analysisFile);
        }
        else if (mainSelect == TEST_SELECT.listAxisToAxisEC.KEY+1) {
            listAxisToAxisECTest laaect = new listAxisToAxisECTest();
        }
        else if (mainSelect == TEST_SELECT.PMC.KEY+1) {
            PMC = new PMC();
            if (PMC.loadPMCSuccessful)
                PMC.displayListPositionErr();
            else
                if (c.loadAnalysisFileIntoMemory(lp)) {
                    c.calcAnalysisErrors(lp);
                    c.writeAnalysisFile(lp);
                    PMC.appendListPositionToPMCFile(lp);
                    lp.init();
                    if (PMC.loadListPositionErrFromPMCFile())
                        PMC.displayListPositionErr();
                    else
                        System.out.println("could not find "
                        + PMC.PMCFile
                        + " in test PMC");
                }
                else
                    System.out.println("could not find " + c.analysisFile);
        }
        else if (mainSelect == TEST_SELECT.verifyLimitMotion.KEY+1) {
            System.out.println("verifying existence of Limit motion type objects...");
            Enumeration eLIMIT_MOTION_TYPE = LIMIT_MOTION_TYPE.elements();
            while (eLIMIT_MOTION_TYPE.hasMoreElements()) {
                LIMIT_MOTION_TYPE lmt = (LIMIT_MOTION_TYPE) eLIMIT_MOTION_TYPE.nextElement();
                System.out.println("building " + lmt);
                LimitMotion LimitMotion = new limitMotionFactory().build(lmt);
            }
        }
        else if (mainSelect == TEST_SELECT.limitWindow.KEY+1) {
            listLimitWindowTest llwt = new listLimitWindowTest();
        }
        else if (mainSelect == TEST_SELECT.limitMotionBase.KEY+1) {
            limitMotionBase lmb = new limitMotionBase();
            lmb.test();
        }
        else if (mainSelect == TEST_SELECT.limitMotionCollection.KEY+1) {
            limitMotionCollection lmc = new limitMotionCollection();
            lmc.test();
        }
        else if (mainSelect == TEST_SELECT.ioFile.KEY+1) {
            ioFile iof = new ioFile();
            iof.test();
        }
        else if (mainSelect == TEST_SELECT.serialPort.KEY+1) {
            ioSerial servoPort = new ioSerial();
            servoPort.test();
        }
        else if (mainSelect == TEST_SELECT.UDP.KEY+1) {
            ioUDP ioUDP = new ioUDP();
            ioUDP.test();
        }
        else if (mainSelect == TEST_SELECT.TCPserver.KEY+1) {
            ioTCPserver ioTCPserver = new ioTCPserver();
            ioTCPserver.test();
        }
        else if (mainSelect == TEST_SELECT.TCPclient.KEY+1) {
            ioTCPclient ioTCPclient = new ioTCPclient();
            ioTCPclient.test();
        }
        else if (mainSelect == TEST_SELECT.TCP.KEY+1) {
            testTCP testTCP = new testTCP();
        }
        else if (mainSelect == TEST_SELECT.IOFactory.KEY+1) {
            ioFactory iof = new ioFactory();
            iof.test();
        }
        else if (mainSelect == TEST_SELECT.serveHtml.KEY+1)
            new serveHtml().test();
        else if (mainSelect == TEST_SELECT.ioRelay.KEY+1) {
            testIORelay ior = new testIORelay();
            ior.test();
        }
        else if (mainSelect == TEST_SELECT.verifyEncoders.KEY+1) {
            System.out.println("verifying existence of encoder type objects...");
            Enumeration eENCODER_TYPE = ENCODER_TYPE.elements();
            while (eENCODER_TYPE.hasMoreElements()) {
                ENCODER_TYPE et = (ENCODER_TYPE) eENCODER_TYPE.nextElement();
                System.out.println("building " + et);
                Encoders Encoders = new encoderFactory().build(et);
            }
        }
        else if (mainSelect == TEST_SELECT.encoders.KEY+1) {
            testEncoders te = new testEncoders();
            te.test();
        }
        else if (mainSelect == TEST_SELECT.setDate.KEY+1) {
            System.out.print("enter new date (mm-dd-yy): ");
            console.getString();
            common.writeFileExecFile(eString.SETDATE_BAT_FILENAME, "date " + console.s, true);
            astroTime.getInstance().getCurrentDateTime();
            System.out.println("new " + astroTime.getInstance().buildStringCurrentDateTime());
        }
        else if (mainSelect == TEST_SELECT.servo.KEY+1) {
            s = new servo();
            s.test();
            s.close();
        }
        else if (mainSelect == TEST_SELECT.trajectory.KEY+1)
            trajectory.test();
        else if (mainSelect == TEST_SELECT.PICServoSim.KEY+1) {
            PICServoSim pss = new PICServoSim();
            pss.test();
        }
        else if (mainSelect == TEST_SELECT.PICServoMotorsSimulator.KEY+1)
            new PICServoMotorsSimulator().run();
        else if (mainSelect == TEST_SELECT.handpad.KEY+1) {
            handpadControl hc = new handpadControl();
            hc.testHandpad();
            hc.close();
        }
        else if (mainSelect == TEST_SELECT.verifyHandpadDesigns.KEY+1) {
            System.out.println("verifying existence of handpad design objects...");
            Enumeration eHANDPAD_DESIGN = HANDPAD_DESIGN.elements();
            while (eHANDPAD_DESIGN.hasMoreElements()) {
                HANDPAD_DESIGN hd = (HANDPAD_DESIGN) eHANDPAD_DESIGN.nextElement();
                System.out.println("building " + hd);
                HandpadDesigns HandpadDesigns = new handpadDesignFactory().build(hd);
            }
        }
        else if (mainSelect == TEST_SELECT.verifyHandpadModes.KEY+1) {
            t = null;
            System.out.println("verifying existence of handpad mode objects...");
            Enumeration eHANDPAD_MODE = HANDPAD_MODE.elements();
            while (eHANDPAD_MODE.hasMoreElements()) {
                HANDPAD_MODE hm = (HANDPAD_MODE) eHANDPAD_MODE.nextElement();
                System.out.println("building " + hm);
                HandpadModes HandpadModes = new handpadModeFactory().build(hm, t);
            }
        }
        else if (mainSelect == TEST_SELECT.verifyMountTypes.KEY+1) {
            System.out.println("verifying existence of mount type objects...");
            Enumeration eMOUNT_TYPE = MOUNT_TYPE.elements();
            while (eMOUNT_TYPE.hasMoreElements()) {
                MOUNT_TYPE mt = (MOUNT_TYPE) eMOUNT_TYPE.nextElement();
                System.out.println("building " + mt);
                Mount Mount = new mountFactory().build(mt);
            }
        }
        else if (mainSelect == TEST_SELECT.verifyTrackStyle.KEY+1) {
            System.out.println("verifying existence of track style type objects...");
            Enumeration eTRACK_STYLE_ID = TRACK_STYLE_ID.elements();
            while (eTRACK_STYLE_ID.hasMoreElements()) {
                TRACK_STYLE_ID ts = (TRACK_STYLE_ID) eTRACK_STYLE_ID.nextElement();
                System.out.println("building " + ts);
                TrackStyle TrackStyle = new trackStyleFactory().build(ts);
            }
        }
        else if (mainSelect == TEST_SELECT.trackStyle.KEY+1) {
            tb = new trackBuilder(null);
            tb.build();
            // TrackStyle set in testTracking()
            tb.t.testTracking(true);
            tb.close();
        }
        else if (mainSelect == TEST_SELECT.LX200.KEY+1) {
            // display all LX200 commands recognized
            System.out.println(CMD_LX200.returnItemListAsString());
            // set LX200control to true and turn off all motors
            cfg.getInstance().LX200Control = true;
            for (id = 0; id < SERVO_ID.size(); id++)
                cfg.getInstance().servoParm[id].controllerActive = false;
            cfg.getInstance().current.ra.rad = 5.*units.HR_TO_RAD;
            cfg.getInstance().current.dec.rad = 25.*units.DEG_TO_RAD;

            tb = new trackBuilder(null);
            tb.build();
            tb.t.cmdCol.LX200.io.displayReceivedChar(true);
            // not necessary to set TrackStyle
            while (true)
                tb.t.cmdCol.LX200.readLX200Input();
        }
        else if (mainSelect == TEST_SELECT.dataFile.KEY+1) {
            dataFile df = new dataFile();
            df.test();
        }
        else if (mainSelect == TEST_SELECT.inputFileAvoidObject.KEY+1) {
            inputFile inf = new inputFile();
            inf.test();
        }
        else if (mainSelect == TEST_SELECT.cometFile.KEY+1) {
            cometFile cf = new cometFile();
            cf.test();
        }
        else if (mainSelect == TEST_SELECT.fileFilter.KEY+1) {
            fileFilterTest fft = new fileFilterTest();
            fft.test();
        }
        else if (mainSelect == TEST_SELECT.findObjectInFileSet.KEY+1) {
            findObjectInFileSetTest fo = new findObjectInFileSetTest();
            fo.test();
        }
        else if (mainSelect == TEST_SELECT.findAllObjectsInFileSet.KEY+1) {
            findAllObjectsInFileSetTest fao = new findAllObjectsInFileSetTest();
            fao.test();
        }
        else if (mainSelect == TEST_SELECT.ProjectPlutoGuide.KEY+1) {
            externalSlewFiles esf = new externalSlewFiles();
            esf.testProjectPlutoGuide();
        }
        else if (mainSelect == TEST_SELECT.spiralSearch.KEY+1) {
            new spiralSearchCmdScopeList(c).test();
        }
        else if (mainSelect == TEST_SELECT.parseTestCmdFile.KEY+1) {
            System.out.print("enter name of command file to test ");
            console.getString();
            cmdScopeList cl = new cmdScopeList(console.s);
            // pass a null for class track pointer
            cl.init(null);
            cl.parseCmdFromFile(console.s);
        }
        else if (mainSelect == TEST_SELECT.listAllCmdScope.KEY+1)
            CMD_SCOPE.displayCmdScope();
        else if (mainSelect == TEST_SELECT.verifyCmdScope.KEY+1) {
            StringTokenizer st = new StringTokenizer(" ");
            System.out.println("verifying existence of command scope type objects...");
            Enumeration eCMD_SCOPE = CMD_SCOPE.elements();
            while (eCMD_SCOPE.hasMoreElements()) {
                CMD_SCOPE cs = (CMD_SCOPE) eCMD_SCOPE.nextElement();
                System.out.println("building " + cs);
                cmdScope cmd = new CmdScopeFactory().build(cs, st);
            }
        }
        else if (mainSelect == TEST_SELECT.execStringCmdScope.KEY+1) {
            execStringCmdScopeTest esct = new execStringCmdScopeTest();
        }
        else if (mainSelect == TEST_SELECT.cmdScopeInHtmlFormat.KEY+1)
            html.writeHTMLFile(eString.CMD_SCOPE_HTML_PAGE, eString.STATUS_STYLE_SHEET, 0, "cmdScope", html.buildCmdScopeHtmlString());
        else if (mainSelect == TEST_SELECT.parmsInHtmlFormat.KEY+1)
            html.writeHTMLFile(eString.PARMS_HTML_PAGE, eString.STATUS_STYLE_SHEET, 0, "Parms", html.buildCfgServoParmsHtmlString());
        else if (mainSelect == TEST_SELECT.filesUsedInHtmlFormat.KEY+1)
            html.writeHTMLFile(eString.FILES_USED_HTML_PAGE, eString.STATUS_STYLE_SHEET, 0, "filesUsed", html.buildFilesUsedHtmlString());
        else if (mainSelect == TEST_SELECT.cmdLineArgsInHtmlFormat.KEY+1)
            html.writeHTMLFile(eString.CMD_LINE_HTML_PAGE, eString.STATUS_STYLE_SHEET, 0, "CommandLineHelp", html.buildCmdLineArgsHtmlString());
        else if (mainSelect == TEST_SELECT.testDataFileChooserLoadListPos.KEY+1)
            new testDataFileChooserLoadListPos();
        else if (mainSelect == TEST_SELECT.testCmdFileChooser.KEY+1)
            new testCmdFileChooser();
        else if (mainSelect == TEST_SELECT.testFindAllObjectsInDataDir.KEY+1)
            new testFindAllObjectsInDataDir();
        else if (mainSelect == TEST_SELECT.testDataFileListPositions.KEY+1)
            new dataFileListPositionsTest();
        else if (mainSelect == TEST_SELECT.precessDataFiles.KEY+1)
            new precessDataFiles();
        else if (mainSelect == TEST_SELECT.verifyJFrameStatusTextArea.KEY+1) {
            System.out.println("verifying existence of verifyJFrameStatusTextArea type objects...");
            Enumeration eSTATUS_TYPE = STATUS_TYPE.elements();
            while (eSTATUS_TYPE.hasMoreElements()) {
                STATUS_TYPE st = (STATUS_TYPE) eSTATUS_TYPE.nextElement();
                System.out.println("building " + st);
                IJFrameStatusTextArea IJFrameStatusTextArea = new JFrameStatusTextAreaFactory().build(st);
            }
        }
        else if (mainSelect == TEST_SELECT.temp.KEY+1) {
        }
        else if (mainSelect == TEST_SELECT.quit.KEY+1)
            quit = true;
    }
}

