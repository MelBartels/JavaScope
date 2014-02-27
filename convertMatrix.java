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
 * coordinate translation routines using Taki's matrix method;
 */
public class convertMatrix {
    private String convertName;
    String initHistoryFile;
    String analysisFile;

    private final String init1Str = "init1";
    private final String init2Str = "init2";
    private final String init3Str = "init3";

    INIT_STATE initState;

    private WHY_INIT lastWhyInit;
    private boolean writeInitHistoryFile;

    // storage arrays for matrix multiplication
    private double qq[][] = new double[4][4];
    private double vv[][] = new double[4][4];
    private double rr[][] = new double[4][4];
    private double xx[][] = new double[4][4];
    private double yy[][] = new double[4][4];

    // working vars
    private double f;
    private double h;
    private double w;

    // fabrication errors (in radians):
    // offset of elevation to perpendicular of horizon, ie, one side of rocker box higher than the other
    private double z1;
    // optical axis pointing error in same plane, ie, tube horiz.: optical axis error left to right (horiz)
    private double z2;
    // correction to zero setting of elevation, ie, vertical offset error (same as altitude offset)
    private double z3;
    // if z1 or z2 is non-zero
    private boolean z12NonZero;

    // used to compute z1, z2, z3
    private double bestZ1;
    private double bestZ2;
    private double bestZ3;
    // same as z3
    double altOffset;
    private double altOffsetFromCalcPostInitVars;
    private double pointErrRMS;
    private double bestPointErrRMS;
    private int z12Count;
    private int Z3Count;
    private double lastDeltaAx3;

    private double holdCurrentDec;
    private double holdCosCurrentDec;
    private double holdSinCurrentDec;

    private boolean updatePostInitVars;
    private boolean updatePostInitVarsForFieldRotationOnly;

    // following values in radians
    private double latScopeZenith;
    private double latEquatPole;
    // used by field rotation function
    private double sinLatEquatPole;
    private double cosLatEquatPole;
    private double sinLatEquatPoleDividedByCosLatEquatPole;
    private double longitudeRad;
    // Hour Angle offset = Local Sidereal time - scope's meridian, or, haOff = LST - ha - ra, or,
    // ha = LST - haOff - ra; (+) offset = scope tilted to West, (-) offset = scope tilted to East;
    // haOff varies from - offset to + offset (should only be a few deg.)
    private double haOff;
    // azimuth offset of init'ed position from true azimuth
    private double azOff;
    private azDouble zenithOffset = new azDouble();
    private azDouble polarAlignEquatPole = new azDouble();
    private azDouble polarAlignAltazPole = new azDouble();

    // field rotation angle
    double fieldRotation;
    // rate of field rotation in radians per minute
    private double fieldRotationRateRadMin;

    private refract refract = new refract();
    double RefractDec;
    double RefractRa;
    private double airMass;
    private boolean validAirMass;

    private CONVERT_SUBR_SELECT subrSelect;
    // count of iterations needed in subrT()
    private int subrTCount;
    // count of iterations needed in subrL()
    private int subrLCount;

    // declare file objects
    private PrintStream output;
    private BufferedReader input;

    private static double prevFieldRotation;
    private static double frPrevSidT;

    private String z123String;
    String meridianFlipStatusString;
    String postInitVarsString;
    String additionalConvertVarsString;
    String initString;

    // maintain a collection of position objects rather than allowing each function to 'new' a position every time function is called
    private positionCollection posCol = new positionCollection();

    convertTrig site = new convertTrig("site");

    convertMatrix(String convertName) {
        this.convertName = new String(convertName);

        initHistoryFile = eString.PGM_NAME + eString.INITHIST_EXT;
        analysisFile = eString.PGM_NAME + eString.ANALYSIS_EXT;

        subrSelect = CONVERT_SUBR_SELECT.BellTaki;

        // empty initHistoryFile
        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(initHistoryFile)));
            output.close();
        }
        catch (IOException ioe) {
            console.errOut(ioe.toString());
            ioe.printStackTrace();
            System.err.println("Could not create " + initHistoryFile);
        }
        writeInitHistoryFile = true;

        // set hold, hold sin, and hold cos values of current.dec
        holdCurrentDec = -9999.;
        checkHoldSinCosCurrentDec();

        // set mount errors from the cfg.getInstance().dat file
        setMountErrorsDeg(cfg.getInstance().z1Deg, cfg.getInstance().z2Deg, cfg.getInstance().z3Deg);
    }

    /**
     * altitude always positive when above site horizon;
     * az always increases clockwise;
     * in northern hemisphere at pole, sky appears to rotate counter-clockwise when looking up at the pole
     *    and clockwise when looking down at the ground, in southern hemisphere rotations are reversed;
     * hence, in northern hemisphere, tracking causes az to increase, in southern hemisphere, az decreases;
     * for equatorial mounts, az of 0 means hour angle of 0;
     * for altazimuth mounts, pointing to intersection of celestial equator and meridian:
     *    northern hemisphere, az of 180 means meridian to the south,
     *    southern hemisphere, az of 0 means meridian to the north;
     */
    boolean init(INIT_STATE initState) {
        this.initState = initState;

        console.stdOutLn("initializing coordinate conversion using " + initState);

        if (initState == INIT_STATE.equatAlign)
            initConvertEquat();
        else if (initState == INIT_STATE.altazAlign)
            initConvertAltaz();
        else if (initState == INIT_STATE.altAltAlign)
            initConvertAltAlt(0., WHY_INIT.altAlt);
        else if (initState == INIT_STATE.configFileAlign || initState == INIT_STATE.userAlign)
            initUsingCfgBkup();
        else if (initState == INIT_STATE.noAlign)
            cfg.getInstance().killInits();
        else
            return false;
        return true;
    }

    /**
     * first init point alt/az of 0/0 associated with hour angle/dec of 0/0;
     * second init point west on celestial equator (az=90 in north, az=270 in south);
     */
    void initConvertEquat() {
        double ra;
        position tempP = posCol.nextAvail();

        cfg.getInstance().one.setCoordDeg(cfg.getInstance().current.sidT.rad*units.RAD_TO_DEG, 0., 0., 0., cfg.getInstance().current.sidT.rad*units.RAD_TO_DEG);
        cfg.getInstance().one.objName = "";

        ra = cfg.getInstance().one.ra.rad - units.QTR_REV;
        if (ra < 0.)
            ra += units.ONE_REV;
        if (cfg.getInstance().latitudeDeg >= 0.)
            cfg.getInstance().two.setCoordDeg(ra*units.RAD_TO_DEG, 45., 45., 90., cfg.getInstance().one.sidT.rad*units.RAD_TO_DEG);
        else
            cfg.getInstance().two.setCoordDeg(ra*units.RAD_TO_DEG, -45., 45., 270., cfg.getInstance().one.sidT.rad*units.RAD_TO_DEG);
        cfg.getInstance().two.objName = "";

        cfg.getInstance().one.init = cfg.getInstance().two.init = true;
        cfg.getInstance().three.init = false;
        tempP.copy(cfg.getInstance().current);

        cfg.getInstance().current.copy(cfg.getInstance().one);
        initMatrix(1, WHY_INIT.equat);
        cfg.getInstance().current.copy(tempP);

        detectMeridianFlipFromCurrentAltaz();

        console.stdOutLn("current altitude coordinate indicate that meridian flip state is "
        + ((cfg.getInstance().Mount.meridianFlipPossible() && cfg.getInstance().Mount.meridianFlip().flipped)?"ON":"off"));

        tempP.available = true;
    }

    /**
     * first init points at celestial pole;
     * second init points at celestial equator on meridian;
     */
    void initConvertAltaz() {
        position tempP = posCol.nextAvail();

        cfg.getInstance().one.setCoordDeg(0., units.QTR_REV*units.RAD_TO_DEG, cfg.getInstance().latitudeDeg, 0., cfg.getInstance().current.sidT.rad*units.RAD_TO_DEG);
        cfg.getInstance().one.objName = "";
        cfg.getInstance().two.setCoordDeg(cfg.getInstance().current.sidT.rad*units.RAD_TO_DEG, 0., 90.-cfg.getInstance().latitudeDeg, units.HALF_REV*units.RAD_TO_DEG,
        cfg.getInstance().current.sidT.rad*units.RAD_TO_DEG);
        cfg.getInstance().two.objName = "";

        cfg.getInstance().one.init = cfg.getInstance().two.init = true;
        cfg.getInstance().three.init = false;
        tempP.copy(cfg.getInstance().current);
        cfg.getInstance().current.copy(cfg.getInstance().one);
        initMatrix(1, WHY_INIT.altaz);
        cfg.getInstance().current.copy(tempP);

        tempP.available = true;
    }

    /**
     * called in initConvertEquat(),
     * setCurrentAltazGetEquatCopyTarget(),
     * resetToCurrentAltazCoord(),
     * sequencer()
     */
    boolean detectMeridianFlipFromCurrentAltaz() {
        if (cfg.getInstance().Mount.meridianFlipPossible()) {
            cfg.getInstance().Mount.meridianFlip().flipped = false;
            if (cfg.getInstance().Mount.meridianFlip().required)
                // use true, not indicated altitude
                if (cfg.getInstance().current.alt.rad + z3 > units.QTR_REV) {
                    cfg.getInstance().Mount.meridianFlip().flipped = true;
                    return true;
                }
        }
        return false;
    }

    void close() {
        setCfgZ123Deg();

        if (initState == INIT_STATE.userAlign)
            // so as to instruct program at next startup to accept the already saved config file one, two positions
            cfg.getInstance().initState = INIT_STATE.configFileAlign;
        else
            cfg.getInstance().initState = initState;
    }

    void setMountErrorsDeg(double z1Deg, double z2Deg, double z3Deg) {
        z1 = z1Deg*units.DEG_TO_RAD;
        z2 = z2Deg*units.DEG_TO_RAD;
        z3 = z3Deg*units.DEG_TO_RAD;
        if (z1 != 0. || z2 != 0.)
            z12NonZero = true;
        else
            z12NonZero = false;
    }

    private void setCfgZ123Deg() {
        cfg.getInstance().z1Deg = z1*units.RAD_TO_DEG;
        cfg.getInstance().z2Deg = z2*units.RAD_TO_DEG;
        cfg.getInstance().z3Deg = z3*units.RAD_TO_DEG;
    }

    private void zeroArrays() {
        int i, j;

        for (i = 0; i < 4; i++)
            for (j = 0; j < 4; j++)
                qq[i][j] = vv[i][j] = rr[i][j] = xx[i][j] = yy[i][j] = 0.;
    }

    private void checkHoldSinCosCurrentDec() {
        if (holdCurrentDec != cfg.getInstance().current.dec.rad) {
            holdCosCurrentDec = Math.cos(cfg.getInstance().current.dec.rad);
            holdSinCurrentDec = Math.sin(cfg.getInstance().current.dec.rad);
            holdCurrentDec = cfg.getInstance().current.dec.rad;
        }
    }

    /**
     * only write WHY_INIT.z123 once to file if WHY_INIT.z123 or WHY_INIT.altAltAz sequentially repeats
     */
    private void saveInits(WHY_INIT whyInit) {
        if (writeInitHistoryFile && !(lastWhyInit == whyInit && (whyInit == WHY_INIT.z123 || whyInit == WHY_INIT.altAltAz))) {
            try {
                // append to initHistoryFile
                output = new PrintStream(new BufferedOutputStream(new FileOutputStream(initHistoryFile, true)));
            }
            catch (IOException ioe) {
                console.errOut(ioe.toString());
                ioe.printStackTrace();
                System.err.println("could not open "
                + initHistoryFile
                + " for append");
            }

            astroTime.getInstance().buildStringCurrentDateTime();

            if (whyInit == WHY_INIT.z123 || whyInit == WHY_INIT.altAltAz)
                output.println(whyInit);
            else {
                if (cfg.getInstance().one.init) {
                    output.println(cfg.getInstance().one.buildStringDataFileWithAltazFormat()
                    + " "
                    + astroTime.getInstance().stringCurrentDateTime
                    + " "
                    + init1Str
                    + " "
                    + whyInit);
                }
                if (cfg.getInstance().two.init) {
                    output.println(cfg.getInstance().two.buildStringDataFileWithAltazFormat()
                    + " "
                    + astroTime.getInstance().stringCurrentDateTime
                    + " "
                    + init2Str
                    + " "
                    + whyInit);
                }
                if (cfg.getInstance().three.init) {
                    output.println(cfg.getInstance().three.buildStringDataFileWithAltazFormat()
                    + " "
                    + astroTime.getInstance().stringCurrentDateTime
                    + " "
                    + init3Str
                    + " "
                    + whyInit);
                }
            }
            output.println("");
            output.close();
        }
        lastWhyInit = whyInit;
    }

    private boolean initUsingCfgBkup() {
        position tempP = posCol.nextAvail();

        cfg.getInstance().one.copy(cfg.getInstance().bkupOne);
        cfg.getInstance().two.copy(cfg.getInstance().bkupTwo);
        cfg.getInstance().three.copy(cfg.getInstance().bkupThree);

        if (cfg.getInstance().one.init) {
            tempP.copy(cfg.getInstance().current);
            cfg.getInstance().current.copy(cfg.getInstance().one);
            // if two.init, then initializes both positions
            initMatrix(1, WHY_INIT.cfgFile);
            cfg.getInstance().current.copy(tempP);
        }
        tempP.available = true;
        return cfg.getInstance().initialized();
    }

    private void arrayAssignInit(int init) {
        double b;

        if (init == 1)
            zeroArrays();

        checkHoldSinCosCurrentDec();

        // b is CCW so ha formula backwards
        b = cfg.getInstance().current.ra.rad - cfg.getInstance().current.sidT.rad;
        // xx is telescope matrix; convert parameters into rectangular (cartesian) coordinates
        xx[1][init] = holdCosCurrentDec * Math.cos(b);
        xx[2][init] = holdCosCurrentDec * Math.sin(b);
        xx[3][init] = holdSinCurrentDec;
        // f is CCW
        f = units.ONE_REV - cfg.getInstance().current.az.rad;
        h = cfg.getInstance().current.alt.rad + z3;
        subrA();
        // yy is celestial matrix; convert parameters into rectangular (cartesian) coordinates
        yy[1][init] = yy[1][0];
        yy[2][init] = yy[2][0];
        yy[3][init] = yy[3][0];
    }

    private void generateThirdInit() {
        int i;
        double a;

        // generate 3rd initialization point from the first two using vector product formula
        xx[1][3] = xx[2][1]*xx[3][2] - xx[3][1]*xx[2][2];
        xx[2][3] = xx[3][1]*xx[1][2] - xx[1][1]*xx[3][2];
        xx[3][3] = xx[1][1]*xx[2][2] - xx[2][1]*xx[1][2];
        a = Math.sqrt(xx[1][3]*xx[1][3] + xx[2][3]*xx[2][3] + xx[3][3]*xx[3][3]);
        for (i = 1; i <= 3; i++)
            if (a == 0.)
                xx[i][3] = Double.MAX_VALUE;
            else
                xx[i][3] /= a;
        yy[1][3] = yy[2][1]*yy[3][2] - yy[3][1]*yy[2][2];
        yy[2][3] = yy[3][1]*yy[1][2] - yy[1][1]*yy[3][2];
        yy[3][3] = yy[1][1]*yy[2][2] - yy[2][1]*yy[1][2];
        a = Math.sqrt(yy[1][3]*yy[1][3] + yy[2][3]*yy[2][3] + yy[3][3]*yy[3][3]);
        for (i = 1; i <= 3; i++)
            if (a == 0.)
                yy[i][3] = Double.MAX_VALUE;
            else
                yy[i][3] /= a;
    }

    private void transformMatrix() {
        int i, j, l, m, n;
        double e;

        for (i = 1; i <= 3; i++)
            for (j = 1; j <= 3; j++)
                vv[i][j] = xx[i][j];
        // get determinate from copied into array vv
        determinateSubr();
        // save it
        e = w;
        for (m = 1; m <= 3; m++) {
            for (i = 1; i <= 3; i++)
                for (j = 1; j <= 3; j++)
                    vv[i][j] = xx[i][j];
            for (n = 1; n <= 3; n++) {
                vv[1][m] = 0.;
                vv[2][m] = 0.;
                vv[3][m] = 0.;
                vv[n][m] = 1.;
                determinateSubr();
                if (e == 0)
                    qq[m][n] = Double.MAX_VALUE;
                else
                    qq[m][n] = w/e;
            }
        }
        for (i = 1; i <= 3; i++)
            for (j = 1; j <= 3; j++)
                rr[i][j] = 0.;
        for (i = 1; i <= 3; i++)
            for (j = 1; j <= 3; j++)
                for (l = 1; l <= 3; l++)
                    rr[i][j] += (yy[i][l] * qq[l][j]);
        for (m = 1; m <= 3; m++) {
            for (i = 1; i <= 3; i++)
                for (j = 1; j <= 3; j++)
                    vv[i][j] = rr[i][j];
            determinateSubr();
            e = w;
            for (n = 1; n <= 3; n++) {
                vv[1][m] = 0.;
                vv[2][m] = 0.;
                vv[3][m] = 0.;
                vv[n][m] = 1.;
                determinateSubr();
                if (e == 0)
                    qq[m][n] = Double.MAX_VALUE;
                else
                    qq[m][n] = w/e;
            }
        }
    }

    /**
     * to use, put values to init into current., then call initMatrix(x) with x = desired init;
     * function performs all possible inits from the beginning: for example, need only call initMatrix(1) once to also init two and three;
     * only function from which saveInits() is called
     */
    void initMatrix(int init, WHY_INIT whyInit) {
        position tempP = posCol.nextAvail();

        if (init == 3 && cfg.getInstance().one.init && cfg.getInstance().two.init) {
            tempP.copy(cfg.getInstance().current);
            cfg.getInstance().current.copy(cfg.getInstance().one);
            arrayAssignInit(1);
            cfg.getInstance().current.copy(cfg.getInstance().two);
            arrayAssignInit(2);
            cfg.getInstance().current.copy(tempP);
            arrayAssignInit(3);
            transformMatrix();
            cfg.getInstance().three.copy(cfg.getInstance().current);
            cfg.getInstance().three.init = true;
            updatePostInitVars = true;
            saveInits(whyInit);
        }
        else if (init == 2 && cfg.getInstance().one.init && cfg.getInstance().two.init && cfg.getInstance().three.init) {
            tempP.copy(cfg.getInstance().current);
            cfg.getInstance().current.copy(cfg.getInstance().one);
            arrayAssignInit(1);
            cfg.getInstance().current.copy(tempP);
            arrayAssignInit(2);
            tempP.copy(cfg.getInstance().current);
            cfg.getInstance().current.copy(cfg.getInstance().three);
            arrayAssignInit(3);
            cfg.getInstance().current.copy(tempP);
            transformMatrix();
            updatePostInitVars = true;
            cfg.getInstance().two.copy(cfg.getInstance().current);
            cfg.getInstance().two.init = true;
            saveInits(whyInit);
        }
        else if (init == 2 && cfg.getInstance().one.init && !cfg.getInstance().three.init) {
            tempP.copy(cfg.getInstance().current);
            cfg.getInstance().current.copy(cfg.getInstance().one);
            arrayAssignInit(1);
            cfg.getInstance().current.copy(tempP);
            arrayAssignInit(2);
            generateThirdInit();
            transformMatrix();
            updatePostInitVars = true;
            cfg.getInstance().two.copy(cfg.getInstance().current);
            cfg.getInstance().two.init = true;
            saveInits(whyInit);
        }
        else if (init == 1 && cfg.getInstance().one.init && cfg.getInstance().two.init && cfg.getInstance().three.init) {
            arrayAssignInit(1);
            tempP.copy(cfg.getInstance().current);
            cfg.getInstance().current.copy(cfg.getInstance().two);
            arrayAssignInit(2);
            cfg.getInstance().current.copy(cfg.getInstance().three);
            arrayAssignInit(3);
            cfg.getInstance().current.copy(tempP);
            transformMatrix();
            updatePostInitVars = true;
            cfg.getInstance().one.copy(cfg.getInstance().current);
            cfg.getInstance().one.init = true;
            saveInits(whyInit);
        }
        else if (init == 1 && cfg.getInstance().two.init && !cfg.getInstance().three.init) {
            arrayAssignInit(1);
            tempP.copy(cfg.getInstance().current);
            cfg.getInstance().current.copy(cfg.getInstance().two);
            arrayAssignInit(2);
            cfg.getInstance().current.copy(tempP);
            generateThirdInit();
            transformMatrix();
            updatePostInitVars = true;
            cfg.getInstance().one.copy(cfg.getInstance().current);
            cfg.getInstance().one.init = true;
            saveInits(whyInit);
        }
        else if (init == 1 && !cfg.getInstance().two.init) {
            arrayAssignInit(1);
            cfg.getInstance().one.copy(cfg.getInstance().current);
            cfg.getInstance().one.init = true;
            saveInits(whyInit);
        }
        else {
            common.badExit("initMatrix() failure: init="
            + init
            + ", cfg.getInstance().one.init="
            + cfg.getInstance().one.init
            + ", cfg.getInstance().two.init="
            + cfg.getInstance().two.init
            + ", cfg.getInstance().three.init="
            + cfg.getInstance().three.init);
        }
        tempP.available = true;
        
        // update initState if not a preset alignment, ie, equatAlign, configFileAlign, et al
        if (cfg.getInstance().initialized() && initState == INIT_STATE.noAlign)
            initState = INIT_STATE.userAlign;
    }

    private void subrA() {
        double cosF, cosH, cosZ1, cosZ2, sinF, sinH, sinZ1, sinZ2;

        cosF = Math.cos(f);
        cosH = Math.cos(h);
        sinF = Math.sin(f);
        sinH = Math.sin(h);

        if (z12NonZero) {
            cosZ1 = Math.cos(z1);
            cosZ2 = Math.cos(z2);
            sinZ1 = Math.sin(z1);
            sinZ2 = Math.sin(z2);
            yy[1][0] = cosF*cosH*cosZ2 - sinF*cosZ1*sinZ2 + sinF*sinH*sinZ1*cosZ2;
            yy[2][0] = sinF*cosH*cosZ2 + cosF*sinZ2*cosZ1 - cosF*sinH*sinZ1*cosZ2;
            yy[3][0] = sinH*cosZ1*cosZ2 + sinZ1*sinZ2;
        }
        else {
            yy[1][0] = cosF*cosH;
            yy[2][0] = sinF*cosH;
            yy[3][0] = sinH;
        }
    }

    /**
     * 'h' is alt, 'F' is az
     */
    private void subrSwitcher() {
        double cosF, cosH, sinF, sinH;
        double cosZ1, cosZ2, sinZ1, sinZ2;

        cosF = Math.cos(f);
        cosH = Math.cos(h);
        sinF = Math.sin(f);
        sinH = Math.sin(h);

        if (z12NonZero) {
            cosZ1 = Math.cos(z1);
            cosZ2 = Math.cos(z2);
            sinZ1 = Math.sin(z1);
            sinZ2 = Math.sin(z2);
            if (subrSelect == CONVERT_SUBR_SELECT.TakiSimple)
                subrS(cosF, cosH, sinF, sinH);
            else if (subrSelect == CONVERT_SUBR_SELECT.TakiSmallAngle)
                subrB(cosF, cosH, sinF, sinH, cosZ1, cosZ2, sinZ1, sinZ2);
            else if (subrSelect == CONVERT_SUBR_SELECT.BellIterative)
                subrL(cosF, cosH, sinF, sinH, cosZ1, cosZ2, sinZ1, sinZ2);
            else if (subrSelect == CONVERT_SUBR_SELECT.TakiIterative)
                subrT(cosF, cosH, sinF, sinH, cosZ1, cosZ2, sinZ1, sinZ2);
            else if (subrSelect == CONVERT_SUBR_SELECT.BellTaki)
                subrU(cosF, cosH, sinF, sinH, cosZ1, cosZ2, sinZ1, sinZ2);
        }
        else {
            yy[1][1] = cosF*cosH;
            yy[2][1] = sinF*cosH;
            yy[3][1] = sinH;
        }
    }

    /**
     * per Taki's eq 5.3-4
     */
    private void subrS(double cosF, double cosH, double sinF, double sinH) {
        yy[1][1] = cosH*cosF + z2*sinF - z1*sinH*sinF;
        yy[2][1] = cosH*sinF - z2*cosF - z1*sinH*cosF;
        yy[3][1] = sinH;
    }

    /**
     * per Taki's eq 5.3-2
     */
    private void subrB(double cosF, double cosH, double sinF, double sinH, double cosZ1, double cosZ2, double sinZ1, double sinZ2) {
        yy[1][1] = (cosH*cosF + sinF*cosZ1*sinZ2 - sinH*sinF*sinZ1*cosZ2)/cosZ2;
        yy[2][1] = (cosH*sinF - cosF*cosZ1*sinZ2 + sinH*cosF*sinZ1*cosZ2)/cosZ2;
        yy[3][1] = (sinH - sinZ1*sinZ2)/(cosZ1*cosZ2);
    }

    /**
     * per Taki's eq 5.3-5/6 (Taki says 2 loops sufficient for z errors of 1 deg),
     * z1=1, z2=-1, z3=1, alt/az=88/100 loops needed 6; z1=2, z2=-2, z3=0, alt/az=90/100 loops needed 22;
     * will not converge if .dec or .alt = 90 deg and z12 non-zero and equat init adopted (could be because of poor initial guess by subrB())
     */
    private void subrT(double cosF, double cosH, double sinF, double sinH, double cosZ1, double cosZ2, double sinZ1, double sinZ2) {
        double cosF1, sinF1;
        int MAX_LOOP_COUNT = 25;
        azDouble last = new azDouble();
        azDouble err = new azDouble();
        double holdF = f;
        double holdH = h;

        // so as to not make the err. = invalid later
        last.a = last.z = Double.MAX_VALUE/2.;
        subrTCount = 0;

        // start with best guess using Taki's 'subroutine b'
        subrB(cosF, cosH, sinF, sinH, cosZ1, cosZ2, sinZ1, sinZ2);
        do {
            angleSubr();

            err.a = Math.abs(last.a - h);
            err.z = Math.abs(last.z - f);

            /**
            console.stdOutLn(h*units.RAD_TO_DEG
            + "   "
            + f*units.RAD_TO_DEG
            + "   "
            + err.a*units.RAD_TO_ARCMIN
            + "   "
            + err.z*units.RAD_TO_ARCMIN);
             */

            last.a = h;
            last.z = f;

            cosF1 = Math.cos(f);
            sinF1 = Math.sin(f);

            yy[1][1] = (cosH*cosF + sinF1*cosZ1*sinZ2 - (sinH-sinZ1*sinZ2)*sinF1*sinZ1/cosZ1)/cosZ2;
            yy[2][1] = (cosH*sinF - cosF1*cosZ1*sinZ2 + (sinH-sinZ1*sinZ2)*cosF1*sinZ1/cosZ1)/cosZ2;
            yy[3][1] = (sinH-sinZ1*sinZ2)/(cosZ1*cosZ2);

            subrTCount++;
            if (subrTCount > MAX_LOOP_COUNT) {
                // console.stdOutLn("switching from subrT() to subrL()...");
                f = holdF;
                h = holdH;
                subrL(cosF, cosH, sinF, sinH, cosZ1, cosZ2, sinZ1, sinZ2);
            }
        }while (err.a > units.TENTHS_ARCSEC_TO_RAD || err.z > units.TENTHS_ARCSEC_TO_RAD);
    }

    /**
     * use apparent alt derivation from Larry Bell, apparent az from Taki's iterative solution
     */
    private void subrU(double cosF, double cosH, double sinF, double sinH, double cosZ1, double cosZ2, double sinZ1, double sinZ2) {
        double apparentAlt;

        apparentAlt = getApparentAlt(cosZ1, cosZ2, sinZ1, sinZ2);

        subrT(cosF, cosH, sinF, sinH, cosZ1, cosZ2, sinZ1, sinZ2);
        angleSubr();

        cosH = Math.cos(apparentAlt);
        sinH = Math.sin(apparentAlt);
        cosF = Math.cos(f);
        sinF = Math.sin(f);

        yy[1][1] = cosF*cosH;
        yy[2][1] = sinF*cosH;
        yy[3][1] = sinH;
    }

    /**
     * per Larry Bell's derivation;
     * z1 rotation done between alt and az rotations so no closed algebraic solution, instead, search iteratively;
     * 'h' is alt, 'F' is az;
     * apparent coordinates are what the encoders see, and are our goal;
     */
    private double getApparentAlt(double cosZ1, double cosZ2, double sinZ1, double sinZ2) {
        double v1;

        v1 = (Math.sin(h)-sinZ1*sinZ2)*cosZ1*(cosZ2/((sinZ1*sinZ1-1)*(sinZ2*sinZ2-1)));
        v1 = eMath.boundsSinCos(v1);
        return Math.asin(v1);
    }

    /**
     * from Larry Bell's derivation of iterative solution to Z1Z2
     */
    private void subrL(double cosF, double cosH, double sinF, double sinH, double cosZ1, double cosZ2, double sinZ1, double sinZ2) {
        double trueAz, tanTrueAz;
        double apparentAlt, apparentAz, bestApparentAz;
        double ee, ff, gg, hh;
        double goalSeek, holdGoalSeek;
        double incr, minIncr;
        boolean dir;

        trueAz = f;
        tanTrueAz = Math.tan(trueAz);

        apparentAlt = getApparentAlt(cosZ1, cosZ2, sinZ1, sinZ2);

        ee = Math.cos(apparentAlt);
        ff = Math.sin(apparentAlt);
        gg = cosZ2*sinZ1*ff*tanTrueAz - tanTrueAz*sinZ2*cosZ1 - cosZ2*ee;
        hh = sinZ2*cosZ1 - cosZ2*sinZ1*ff - tanTrueAz*cosZ2*ee;

        // start with best guess using Taki's 'subroutine b' for apparentAz
        subrB(cosF, cosH, sinF, sinH, cosZ1, cosZ2, sinZ1, sinZ2);
        angleSubr();
        apparentAz = f;

        // iteratively solve for best apparent azimuth by searching for a goal of 0 for goalSeek
        bestApparentAz = apparentAz;
        holdGoalSeek = Double.MAX_VALUE;
        incr = units.ARCSEC_TO_RAD*2.;
        minIncr = units.ARCSEC_TO_RAD;
        dir = true;
        subrLCount = 0;
        do {
            if (dir)
                apparentAz += incr;
            else
                apparentAz -= incr;

            goalSeek = gg*Math.sin(apparentAz)-hh*Math.cos(apparentAz);
            /**
             console.stdOutLn("goalSeek "
             + goalSeek*1000000.
             + " dir "
             + dir);
             */

            if (Math.abs(goalSeek) <= Math.abs(holdGoalSeek)) {
                bestApparentAz = apparentAz;
                // console.stdOutLn("bestApparentAz " + bestApparentAz*units.RAD_TO_DEG);
            }
            else {
                // GoakSeek getting worse, so reverse direction and cut increment by half
                incr /= 2.;
                dir = !dir;
            }
            holdGoalSeek = goalSeek;
            subrLCount++;
        }while (incr >= minIncr);

        cosF = Math.cos(bestApparentAz);
        sinF = Math.sin(bestApparentAz);
        cosH = Math.cos(apparentAlt);
        sinH = Math.sin(apparentAlt);

        yy[1][1] = cosF*cosH;
        yy[2][1] = sinF*cosH;
        yy[3][1] = sinH;
    }

    private void determinateSubr() {
        w = vv[1][1]*vv[2][2]*vv[3][3] + vv[1][2]*vv[2][3]*vv[3][1]
        + vv[1][3]*vv[3][2]*vv[2][1] - vv[1][3]*vv[2][2]*vv[3][1]
        - vv[1][1]*vv[3][2]*vv[2][3] - vv[1][2]*vv[2][1]*vv[3][3];
    }

    private void angleSubr() {
        double c;

        c = Math.sqrt(yy[1][1]*yy[1][1] + yy[2][1]*yy[2][1]);

        if (c == 0. && yy[3][1] > 0.)
            h = units.QTR_REV;
        else if (c == 0. && yy[3][1] < 0.)
            h = -units.QTR_REV;
        else if (c != 0.)
            h = Math.atan(yy[3][1]/c);
        else
                console.errOut("undetermined h in convertMatrix.angleSubr()");

        if (c == 0.)
            // f should be indeterminate: Taki program listing is f = 1000 degrees (maybe to note this situation?)
            f = 0.;
        else if (c != 0. && yy[1][1] == 0. && yy[2][1] > 0.)
            f = units.QTR_REV;
        else if (c != 0. && yy[1][1] == 0. && yy[2][1] < 0.)
            f = units.ONE_REV - units.QTR_REV;
        else if (yy[1][1] > 0.)
            f = Math.atan(yy[2][1]/yy[1][1]);
        else if (yy[1][1] < 0.)
            f = Math.atan(yy[2][1]/yy[1][1]) + units.HALF_REV;
        else
            console.errOut("undetermined f in convertMatrix.angleSubr()");

        f = eMath.validRad(f);
    }

    void getAltaz() {
        int i, j;
        double b;

        checkHoldSinCosCurrentDec();

        // b is CCW so ha formula backwards
        b = cfg.getInstance().current.ra.rad - cfg.getInstance().current.sidT.rad;
        // convert to rectangular coordinates and put in xx
        xx[1][1] = holdCosCurrentDec * Math.cos(b);
        xx[2][1] = holdCosCurrentDec * Math.sin(b);
        xx[3][1] = holdSinCurrentDec;
        yy[1][1] = 0.;
        yy[2][1] = 0.;
        yy[3][1] = 0.;
        // mutiply xx by transform matrix rr to get equatorial rectangular coordinates
        for (i = 1; i <= 3; i++)
            for (j = 1; j <= 3; j++)
                yy[i][1] += (rr[i][j] * xx[j][1]);
        // convert to celestial coordinates
        angleSubr();
        // modify for non-zero Z1Z2Z3 mount error values
        subrSwitcher();
        angleSubr();
        cfg.getInstance().current.alt.rad = h;
        // convert azimuth from CCW to CW
        cfg.getInstance().current.az.rad = units.ONE_REV - f;
        // if flipped, then restore 'true' or actual altaz coordinates since input equat and the subsequent
        // coordinate translation is not aware of meridian flip and always results in not flipped coordinate values
        if (cfg.getInstance().Mount.meridianFlipPossible() && cfg.getInstance().Mount.meridianFlip().flipped)
            translateAltazAcrossPole();
        // adjust altitude: this should occur after meridian flip adjustment - see meridian flip note below
        cfg.getInstance().current.alt.rad -= z3;
    }

    void translateAltazAcrossPole() {
        cfg.getInstance().current.alt.rad = units.HALF_REV - cfg.getInstance().current.alt.rad;
        cfg.getInstance().current.az.rad = eMath.validRad(cfg.getInstance().current.az.rad + units.HALF_REV);
    }

    /**
     * meridian flip:
     *
     * if mount is flipped across meridian, then flipped ra differs from original setting circle
     * ra by 12 hrs; altitude reading is mirrored across the pole, that is, an alt of 80 is actually 100
     * (mirrored across 90) as read from the original setting circle orientation;
     * ie,
     * northern hemisphere (az increases as scope tracks, az=0 when on meridian):
     * not flipped:
     * 	1 hr west of meridian (ra < sidT), coord are ra:(sidT-1hr), dec:45, alt:45, az:15;
     * 	1 hr east of meridian (ra > sidT), coord are ra:(sidT+1hr), dec:45, alt:45, az:345 (should be flipped);
     * same coord flipped (scope assumed to have moved the flip, but aimed back at original equat coord):
     * 	1 hr west of meridian (ra < sidT), coord are ra:(sidT-1hr), dec:45, alt:135, az:195 (should be un-flipped);
     * 	1 hr east of meridian (ra > sidT), coord are ra:(sidT+1hr), dec:45, alt:135, az:165;
     * southern hemisphere (az decreases as scope tracks, az=0 when on meridian):
     * not flipped:
     * 	1 hr west of meridian (ra < sidT), coord are ra:(sidT-1hr), dec:-45, alt:45, az:345;
     * 	1 hr east of meridian (ra > sidT), coord are ra:(sidT+1hr), dec:-45, alt:45, az:15 (should be flipped);
     * same coord flipped (scope assumed to have moved the flip, but aimed back at original equat coord):
     * 	1 hr west of meridian (ra < sidT), coord are ra:(sidT-1hr), dec:-45, alt:135, az:165 (should be un-flipped);
     * 	1 hr east of meridian (ra > sidT), coord are ra:(sidT+1hr), dec:-45, alt:135, az:195;
     *
     * z3 or altitude offset error:
     * if scope aimed at 70 but setting circles say 60, then z3 = 10;
     * meridian flipped position: scope aimed at 110 with setting circles indicate 100;
     *
     * also see meridianNeedsFlipping() notes
     */
    void getEquat() {
        int i, j;
        double holdAlt, holdAz;

        holdAlt = cfg.getInstance().current.alt.rad;
        holdAz = cfg.getInstance().current.az.rad;
        cfg.getInstance().current.alt.rad += z3;
        // return to equivalent not flipped altaz values for purposes of coordinate translation
        if (cfg.getInstance().Mount.meridianFlipPossible() && cfg.getInstance().Mount.meridianFlip().flipped)
            translateAltazAcrossPole();
        h = cfg.getInstance().current.alt.rad;
        // convert from CW to CCW az
        f = units.ONE_REV - cfg.getInstance().current.az.rad;
        cfg.getInstance().current.alt.rad = holdAlt;
        cfg.getInstance().current.az.rad = holdAz;

        subrA();
        xx[1][1] = yy[1][0];
        xx[2][1] = yy[2][0];
        xx[3][1] = yy[3][0];
        yy[1][1] = 0.;
        yy[2][1] = 0.;
        yy[3][1] = 0.;
        for (i = 1; i <= 3; i++)
            for (j = 1; j <= 3; j++)
                yy[i][1] += (qq[i][j] * xx[j][1]);
        angleSubr();
        f += cfg.getInstance().current.sidT.rad;
        cfg.getInstance().current.ra.rad = f;
        cfg.getInstance().current.ra.rad = eMath.validRad(cfg.getInstance().current.ra.rad);
        cfg.getInstance().current.dec.rad = h;
    }

    private void checkForPostInitVars() {
        if (updatePostInitVars)
            if (updatePostInitVarsForFieldRotationOnly)
                calcPostInitVarsForFieldRotationOnly();
            else
                calcPostInitVars();
    }

    /**
     * can be called after init 2: calculates apparent scope latitude from two perspectives, longitude, offset hour angle, offset azimuth,
     * zenith and polar offsets
     */
    private void calcPostInitVars() {
        // temporary coordinates
        position tempP = posCol.nextAvail();
        tempP.copy(cfg.getInstance().current);

        // use current time
        cfg.getInstance().current.sidT.rad = astroTime.getInstance().calcSidT();

        // aim at equatorial pole to get azimuth offset and polar offset and 2nd latitude value
        // latitude obtained here by getting scope altitude at equatorial zenith and latitude obtained below by getting scope declination
        // at scope zenith
        cfg.getInstance().current.dec.rad = units.QTR_REV;
        cfg.getInstance().current.ra.rad = 0.;
        getAltaz();
        azOff = cfg.getInstance().current.az.rad;
        latEquatPole = cfg.getInstance().current.alt.rad;
        sinLatEquatPole = Math.sin(latEquatPole);
        cosLatEquatPole = Math.cos(latEquatPole);
        sinLatEquatPoleDividedByCosLatEquatPole = sinLatEquatPole/cosLatEquatPole;
        polarAlignEquatPole.a = units.QTR_REV - cfg.getInstance().current.alt.rad;
        if (polarAlignEquatPole.a == 0.)
            polarAlignEquatPole.z = 0.;
        else
            polarAlignEquatPole.z = cfg.getInstance().current.az.rad;

        // aim at site zenith (meridian is sidereal time, declination of zenith = site latitude) to get scope offset
        cfg.getInstance().current.ra.rad = cfg.getInstance().current.sidT.rad;
        cfg.getInstance().current.dec.rad = cfg.getInstance().latitudeDeg*units.DEG_TO_RAD;
        getAltaz();
        zenithOffset.a = units.QTR_REV-cfg.getInstance().current.alt.rad;
        if (zenithOffset.a == 0.)
            zenithOffset.z = 0.;
        else
            zenithOffset.z = cfg.getInstance().current.az.rad;

        // aim at scope zenith to get latitude, longitude, and haOff
        if (initState == INIT_STATE.equatAlign || latEquatPole == units.QTR_REV || latEquatPole == -units.QTR_REV)
            cfg.getInstance().current.alt.rad = cfg.getInstance().latitudeDeg*units.DEG_TO_RAD;
        else
            cfg.getInstance().current.alt.rad = units.QTR_REV;
        cfg.getInstance().current.az.rad = 0.;
        getEquat();

        latScopeZenith = cfg.getInstance().current.dec.rad;

        // longitudeDeg*units.DEG_TO_RAD + current.sidT.rad = Greenwich Sidereal time;
        // difference between GST and current.ra.rad (== zenith) will be scope longitude
        longitudeRad = cfg.getInstance().longitudeDeg*units.DEG_TO_RAD + cfg.getInstance().current.sidT.rad - cfg.getInstance().current.ra.rad;
        longitudeRad = eMath.validRad(longitudeRad);

        // find hour angle offset = LST(current.sidT.rad) - scope's meridian,
        // ha = LST - haOff - ra, or, haOff = LST - ra, by setting for zenith (ha = 0);
        // + offset = scope tilted to West, - offset = scope tilted to East;
        // haOff varies from - offset to + offset(should be a small amount)
        haOff = cfg.getInstance().current.sidT.rad - cfg.getInstance().current.ra.rad;
        haOff = eMath.validRadPi(haOff);
        // for equatorial alignments, make azimuth offset same as hour angle offset
        if (initState == INIT_STATE.equatAlign)
            azOff = haOff;

        // aim at scope pole to get polar offset from another perspective
        cfg.getInstance().current.alt.rad = units.QTR_REV;
        cfg.getInstance().current.az.rad = 0.;
        getEquat();
        polarAlignAltazPole.a = units.QTR_REV - cfg.getInstance().current.dec.rad;
        if (polarAlignAltazPole.a == 0.)
            polarAlignAltazPole.z = 0.;
        else
            polarAlignAltazPole.z = eMath.validRadPi(cfg.getInstance().current.sidT.rad - cfg.getInstance().current.ra.rad);

        calcAltOffsetIteratively(cfg.getInstance().one, cfg.getInstance().two);
        altOffsetFromCalcPostInitVars = altOffset;

        // restore current coordinates
        cfg.getInstance().current.copy(tempP);
        tempP.available = true;

        updatePostInitVars = false;
    }

    /**
     * see calcPostInitVars() for comments;
     * shortened version of calcPostInitVars() that runs must faster;
     * use when only variables for field rotation calculation are needed;
     */
    private void calcPostInitVarsForFieldRotationOnly() {
        position tempP = posCol.nextAvail();
        tempP.copy(cfg.getInstance().current);

        cfg.getInstance().current.sidT.rad = astroTime.getInstance().calcSidT();

        cfg.getInstance().current.dec.rad = units.QTR_REV;
        cfg.getInstance().current.ra.rad = 0.;
        getAltaz();
        azOff = cfg.getInstance().current.az.rad;
        latEquatPole = cfg.getInstance().current.alt.rad;
        sinLatEquatPole = Math.sin(latEquatPole);
        cosLatEquatPole = Math.cos(latEquatPole);
        sinLatEquatPoleDividedByCosLatEquatPole = sinLatEquatPole/cosLatEquatPole;

        if (initState == INIT_STATE.equatAlign || latEquatPole == units.QTR_REV || latEquatPole == -units.QTR_REV)
            cfg.getInstance().current.alt.rad = cfg.getInstance().latitudeDeg*units.DEG_TO_RAD;
        else
            cfg.getInstance().current.alt.rad = units.QTR_REV;
        cfg.getInstance().current.az.rad = 0.;
        getEquat();
        haOff = cfg.getInstance().current.sidT.rad - cfg.getInstance().current.ra.rad;
        haOff = eMath.validRadPi(haOff);

        cfg.getInstance().current.copy(tempP);
        tempP.available = true;
        updatePostInitVars = false;
    }

    void calcFieldRotation() {
        double a;
        double sinHA;

        checkForPostInitVars();

        cfg.getInstance().current.ha.rad = cfg.getInstance().current.sidT.rad - haOff - cfg.getInstance().current.ra.rad;
        sinHA = Math.sin(cfg.getInstance().current.ha.rad);
        checkHoldSinCosCurrentDec();
        a = sinLatEquatPoleDividedByCosLatEquatPole * holdCosCurrentDec - holdSinCurrentDec * Math.cos(cfg.getInstance().current.ha.rad);
        if (a < 0.)
            fieldRotation = Math.atan(sinHA/a) + units.HALF_REV;
        else if (a == 0.)
            if (sinHA < 0.)
                fieldRotation = -units.HALF_REV;
            else if (sinHA == 0.)
                fieldRotation = 0.;
            else
                fieldRotation = units.HALF_REV;
        else
            fieldRotation = Math.atan(sinHA/a);
        fieldRotation = eMath.validRad(fieldRotation);
    }

    /**
     * rate of field rotation in radians per minute: calculates rate based on last two calls to calcFieldRotation(),
     * assuming that sidT has changed between calls to calcFieldRotation();
     * rate can exceed 360deg/min, though it cannot be sustained for the minute
     */
    private boolean getFieldRotationRate() {
        if (cfg.getInstance().current.sidT.rad > frPrevSidT && cfg.getInstance().current.sidT.rad != frPrevSidT) {
            fieldRotationRateRadMin = (fieldRotation-prevFieldRotation)/((cfg.getInstance().current.sidT.rad-frPrevSidT)*units.RAD_TO_MIN);
            fieldRotationRateRadMin = eMath.validRadPi(fieldRotationRateRadMin);
            prevFieldRotation = fieldRotation;
            frPrevSidT = cfg.getInstance().current.sidT.rad;
            return true;
        }
        return false;
    }

    /**
     * this function calculates the current field rotation rate
     */
    private void calcFieldRotationRateForSidTrackViaDeltaFR() {
        // start 1/2 min before desired midpoint
        double holdSidT = cfg.getInstance().current.sidT.rad - units.MIN_TO_RAD/2.;
        double holdFRPrevSidT = frPrevSidT;

        calcFieldRotation();
        /**
         console.stdOutLn("1st field rotation angle is "
         + eString.doubleToStringNoGrouping(fieldRotation*units.RAD_TO_DEG, 3, 3)
         + " deg");
         */
        // set previous values to values that they can never reach
        frPrevSidT = -units.ONE_REV;
        prevFieldRotation = 2.*units.ONE_REV;
        getFieldRotationRate();
        // 2nd time 1 min after first and 1/2 min after desired midpoint
        cfg.getInstance().current.sidT.rad += units.MIN_TO_RAD;
        calcFieldRotation();
        /**
         console.stdOutLn("2nd field rotation angle is "
         + eString.doubleToStringNoGrouping(fieldRotation*units.RAD_TO_DEG, 3, 3)
         + " deg");
         */
        getFieldRotationRate();

        cfg.getInstance().current.sidT.rad = holdSidT;
        frPrevSidT = holdFRPrevSidT;
    }

    /**
     * note: these two CalcFieldRotationRate functions match with large hour angle offsets of the scope's zenith and very high altitude
     * angles if latitude is found by pointing at scope's zenith as opposed to pointing at scope's equatorial pole
     *
     * From: "MLThiebaux" <mlt@ns.sympatico.ca>
     * We can think of this system as an equatorial mounting with the polar axis grossly misoriented.
     * Say the polar axis is pointing at a fixed point t in the sky with declination  t. (If Dobsonian, read azimuth axis instead of polar
     * axis).  Suppose we are tracking a star with declination  s.  Let  h  be the hour angle of the star minus the fixed hour angle of t.
     * h is increasing at the constant rate  w = 15 deg/hr.
     * Let a = cos(s)tan(t) and b = sin(s).
     * Then the apparent rotation rate in the field of view of the telescope is w(b-AcosH)/[(sinH)^2 +(a-BcosH)^2)].
     * Note that the rate is time-varying; a positive rate corresponds to a counter-clockwise rotation in a 2-mirror telescope.
     */
    private void calcFieldRotationRateForSidTrackViaFormula() {
        double t, s, h, a, b;
        position tempP = posCol.nextAvail();

        checkForPostInitVars();
        tempP.copy(cfg.getInstance().current);

        // get dec of scope's zenith point (hour angle of scope's zenith point already calculated and in haOff)
        cfg.getInstance().current.alt.rad = units.QTR_REV;
        cfg.getInstance().current.az.rad = 0.;
        getEquat();
        t = cfg.getInstance().current.dec.rad;

        // get hour angle of target
        cfg.getInstance().current.alt.rad = tempP.alt.rad;
        cfg.getInstance().current.az.rad = tempP.az.rad;
        getEquat();
        s = cfg.getInstance().current.dec.rad;
        h = cfg.getInstance().current.sidT.rad - cfg.getInstance().current.ra.rad;
        h -= haOff;
        h = eMath.validRadPi(h);

        a = Math.cos(s) * Math.tan(t);
        b = Math.sin(s);

        fieldRotationRateRadMin = (b - a*Math.cos(h)) / ((Math.sin(h) * Math.sin(h) + (a - b*Math.cos(h)) * (a - b*Math.cos(h))));
        // sidereal tracking rate is 1/4 deg/min
        fieldRotationRateRadMin = -fieldRotationRateRadMin * .25*units.DEG_TO_RAD;

        cfg.getInstance().current.copy(tempP);
        tempP.available = true;
    }

    String buildInitString() {
        checkForPostInitVars();

        initString = initState + "\n";

        if (cfg.getInstance().one.init)
            initString += cfg.getInstance().one.stringObjName()
            + ": "
            + cfg.getInstance().one.buildString()
            + "\n";
        else
            initString += "(position #1 not initialized)\n";
        if (cfg.getInstance().two.init)
            initString += cfg.getInstance().two.stringObjName()
            + ": "
            + cfg.getInstance().two.buildString()
            + "\n";
        else
            initString += "(position #2 not initialized)\n";
        if (cfg.getInstance().three.init)
            initString += cfg.getInstance().three.stringObjName()
            + ": "
            + cfg.getInstance().three.buildString()
            + "\n";
        else
            initString += "(position #3 not initialized)\n";

        initString += buildZ123String();

        if (cfg.getInstance().initialized()) {
            initString += "altitude offset from init one, two = "
            + eString.doubleToStringNoGrouping(altOffsetFromCalcPostInitVars*units.RAD_TO_DEG, 3, 2)
            + " deg\n";

            buildPostInitVarsString();
            initString += postInitVarsString;
        }

        return initString;
    }

    void displayInitString() {
        console.stdOut(buildInitString());
    }

    String buildMeridianFlipStatusString() {
        meridianFlipStatusString = "\nmeridian flip is ";
        if (cfg.getInstance().Mount.meridianFlipPossible() && cfg.getInstance().Mount.meridianFlip().flipped)
            meridianFlipStatusString = meridianFlipStatusString + "ON, scope is on west side of mount facing east.";
        else
            meridianFlipStatusString = meridianFlipStatusString + "off, scope is on east side of mount facing west.";

        meridianFlipStatusString += " Auto meridian flip is "
        + ((cfg.getInstance().Mount.meridianFlipPossible() && cfg.getInstance().Mount.meridianFlip().auto)?"ON":"off")
        + "\n\n";

        return meridianFlipStatusString;
    }

    String buildZ123String() {
        z123String = "\nz1 (axes perpendicularity) = "
        + eString.doubleToStringNoGrouping(z1*units.RAD_TO_DEG, 2, 2)
        + " deg,   "
        + "z2 (azimuth offset) = "
        + eString.doubleToStringNoGrouping(z2*units.RAD_TO_DEG, 2, 2)
        + " deg,   "
        + "z3 (altitude offset) = "
        + eString.doubleToStringNoGrouping(z3*units.RAD_TO_DEG, 2, 2)
        + " deg\n";

        return z123String;
    }

    void displayZ123String() {
        console.stdOut(buildZ123String());
    }

    String buildPostInitVarsString() {
        checkForPostInitVars();

        postInitVarsString = "\nfrom telescope's perspective:\n"
        + "latitude calculated by aiming at zenith = "
        + eString.doubleToStringNoGrouping(latScopeZenith*units.RAD_TO_DEG, 3, 2)
        + " deg,"
        + "\nlatitude calculated by aiming at celestial pole = "
        + eString.doubleToStringNoGrouping(latEquatPole*units.RAD_TO_DEG, 3, 2)
        + " deg\n"
        + "longitude = "
        + eString.doubleToStringNoGrouping(longitudeRad*units.RAD_TO_DEG, 3, 2)
        + " deg\n"
        + "hour angle offset = "
        + eString.doubleToStringNoGrouping(haOff*units.RAD_TO_DEG, 3, 2)
        + " deg,"
        + "azimuth offset = "
        + eString.doubleToStringNoGrouping(azOff*units.RAD_TO_DEG, 3, 2)
        + " deg\n"
        + "zenith pointing error = "
        + eString.doubleToStringNoGrouping(zenithOffset.a*units.RAD_TO_DEG, 3, 2)
        + " deg,"
        + "      direction (east=90,south=180) = "
        + eString.doubleToStringNoGrouping(zenithOffset.z*units.RAD_TO_DEG, 3, 2)
        + " deg\n"
        + "polar alignment error (aiming at equatorial pole) = "
        + eString.doubleToStringNoGrouping(polarAlignEquatPole.a*units.RAD_TO_DEG, 3, 2)
        + " deg,"
        + "      direction (east=90,south=180) = "
        + eString.doubleToStringNoGrouping(polarAlignEquatPole.z*units.RAD_TO_DEG, 3, 2)
        + " deg\n"
        + "polar alignment error (aiming at altazimuth pole) = "
        + eString.doubleToStringNoGrouping(polarAlignAltazPole.a*units.RAD_TO_DEG, 3, 2)
        + " deg,"
        + "      direction (east=90,south=180) = "
        + eString.doubleToStringNoGrouping(polarAlignAltazPole.z*units.RAD_TO_DEG, 3, 2)
        + " deg\n";

        return postInitVarsString;
    }

    void displayPostInitVars() {
        console.stdOut(buildPostInitVarsString());
    }

    String buildAdditionalConvertVarsString() {
        calcFieldRotation();
        getFieldRotationRate();
        site.p.ra.rad = cfg.getInstance().current.ra.rad;
        site.p.dec.rad = cfg.getInstance().current.dec.rad;
        site.p.sidT.rad = cfg.getInstance().current.sidT.rad;
        site.getAltaz();
        calcSiteRefractScopeToSky();
        calcSiteAirMass();

        // buildStringCurrentDateTime() returns String built upon astroTime.getInstance().getCurrentDateTime() which is called in astroTime.getInstance().calcSidT()
        additionalConvertVarsString = "field rotation = "
        + eString.doubleToStringNoGrouping(fieldRotation*units.RAD_TO_DEG, 3, 2)
        + " deg,"
        + " changing at rate of "
        + eString.doubleToStringNoGrouping(fieldRotationRateRadMin*units.RAD_TO_DEG, 3, 2)
        + " deg/min\n\n"
        + "refraction = "
        + eString.doubleToStringNoGrouping(refract.refract*units.RAD_TO_ARCSEC, 3, 2)
        + " arcsec, correction style is "
        + cfg.getInstance().refractAlign
        + (cfg.getInstance().refractAlign==ALIGNMENT.equat?"\n   declination correction = "
        + eString.doubleToStringNoGrouping(RefractDec*units.RAD_TO_ARCSEC, 3, 1)
        + " arcsec, "
        + " right ascension correction = "
        + eString.doubleToStringNoGrouping(RefractRa*units.RAD_TO_ARCSEC, 3, 1)
        + " arcsec":"")
        + "\nairMass = "
        + (validAirMass?eString.doubleToStringNoGrouping(airMass, 1, 2):"invalid")
        + "\n\n"
        + "telescope's coordinates based on site lat/long ("
        + eString.doubleToStringNoGrouping(cfg.getInstance().latitudeDeg, 3, 2)
        + "/"
        + eString.doubleToStringNoGrouping(cfg.getInstance().longitudeDeg, 3, 2)
        + "deg) and computer date/time ("
        + astroTime.getInstance().buildStringCurrentDateTime()
        + ") TZ/dst ("
        +  astroTime.getInstance().tz
        + "/"
        + astroTime.getInstance().dst
        + "):\n"
        + "altitude = "
        + eString.doubleToStringNoGrouping(site.p.alt.rad*units.RAD_TO_DEG, 3, 1)
        + " deg,"
        + "   azimuth(dome) = "
        + eString.doubleToStringNoGrouping(site.p.az.rad*units.RAD_TO_DEG, 3, 1)
        + " deg\n\n";

        return additionalConvertVarsString;
    }

    void displayAdditionalConvertVars() {
        console.stdOut(buildAdditionalConvertVarsString());
    }

    /**
     * if scope polar aligned and cfg site vars and computer date/time accurate, then calculate refraction by:
     * 1. before calling one of these functions get altitude from site.getAltaz()
     * 2. call one of these functions, depending on which direction the conversion is headed
     *    a. functions calculate two equatorial coordinates, one without and one with refracted altitude
     *    b. since scope polar aligned, differences in equat coordinates are differences in encoder Positions to correct for
     */
    void calcSiteRefractScopeToSky() {
        double holdSiteRa, holdSiteDec, holdSiteAlt;

        site.getEquat();
        holdSiteDec = site.p.dec.rad;
        holdSiteRa = site.p.ra.rad;
        holdSiteAlt = site.p.alt.rad;

        refract.calcRefractScopeToSky(site.p.alt.rad);
        site.p.alt.rad -= refract.refract;
        site.getEquat();
        RefractDec = holdSiteDec - site.p.dec.rad;
        RefractRa = holdSiteRa - site.p.ra.rad;

        site.p.dec.rad = holdSiteDec;
        site.p.ra.rad = holdSiteRa;
        site.p.alt.rad = holdSiteAlt;
    }

    void calcSiteRefractSkyToScope() {
        double holdSiteRa, holdSiteDec, holdSiteAlt;

        site.getEquat();
        holdSiteDec = site.p.dec.rad;
        holdSiteRa = site.p.ra.rad;
        holdSiteAlt = site.p.alt.rad;

        refract.calcRefractSkyToScope(site.p.alt.rad);
        site.p.alt.rad += refract.refract;
        site.getEquat();
        RefractDec = site.p.dec.rad - holdSiteDec;
        RefractRa = site.p.ra.rad - holdSiteRa;

        site.p.dec.rad = holdSiteDec;
        site.p.ra.rad = holdSiteRa;
        site.p.alt.rad = holdSiteAlt;
    }

    // must call site.getAltaz() first; in doing so, will also set the site.p.az value which represents the dome azimuth
    void calcSiteAirMass() {
        double zenithDistance, secZenithDistance;

        zenithDistance = units.QTR_REV - site.p.alt.rad;
        if (zenithDistance > units.QTR_REV)
            zenithDistance = units.QTR_REV;
        else
            if (zenithDistance < 0.)
                zenithDistance = 0.;

        secZenithDistance = 1./Math.cos(zenithDistance);
        airMass = secZenithDistance - .0018161*(secZenithDistance-1.) -
        .002875*Math.pow(secZenithDistance-1., 2.) - .0008083*Math.pow(secZenithDistance-1., 3.);
        if (airMass > 9.9 || airMass < 1.)
            validAirMass = false;
        else
            validAirMass = true;
    }

    /**
     * analysisFile contains input equatorial coordinates in deg, actual altazimuth coordinates in deg, actual sidereal time in deg,
     * optional altaz errors in arcmin, and optional objName;
     * errors in altazimuth coordinates calculated from:
     * input equatorial coordinates converted to altazimuth coordinates minus the actual altazimuth coordinates
     */
    boolean loadAnalysisFileIntoMemory(listPosition lp) {
        position p = new position("loadAnalysisFileIntoMemory");
        try {
            input = new BufferedReader(new FileReader(analysisFile));
            // start with empty listPosition
            lp.init();
            while (console.fReadLinePositionDegWithAltazErrArcmin(input, p))
                lp.add(p);
            input.close();
            // for testing
            lp.display();
            return true;
        }
        catch (IOException ioe) {
            return false;
        }
    }

    boolean emptyAnalysisFile(listPosition lp) {
        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(analysisFile)));
            output.close();
            return true;
        }
        catch (IOException ioe) {
            console.errOut(ioe.toString());
            ioe.printStackTrace();
            System.err.println("Could not create " + analysisFile);
            return false;
        }
    }

    void writeAnalysisFile(listPosition lp) {
        Iterator it;
        position p;

        try {
            output = new PrintStream(new BufferedOutputStream(new FileOutputStream(analysisFile)));
            it = lp.iterator();
            while (it.hasNext()) {
                p = (position) it.next();
                output.println(p.buildCoordDegRaw()
                + "   "
                + eString.doubleToStringNoGrouping(p.azErr.a*units.RAD_TO_ARCMIN, 3, 3)
                + "   "
                + eString.doubleToStringNoGrouping(p.azErr.z*units.RAD_TO_ARCMIN, 3, 3));
            }
            output.close();
        }
        catch (IOException ioe) {
            console.errOut(ioe.toString());
            ioe.printStackTrace();
            System.err.println("could not open " + analysisFile);
        }
    }

    void calcAnalysisErrorAddToAnalysisFile(listPosition lp, position p) {
        loadAnalysisFileIntoMemory(lp);
        lp.add(p);
        calcAnalysisErrors(lp);
        writeAnalysisFile(lp);
    }

    void calcAnalysisErrors(listPosition lp) {
        Iterator it;
        position p;
        double pointErr = 0.;
        double pointErrTot = 0.;
        double maxPointErr;
        String s1, s2;
        position tempP = posCol.nextAvail();

        tempP.copy(cfg.getInstance().current);

        // calculate errors
        maxPointErr = 0.;
        it = lp.iterator();
        while (it.hasNext()) {
            p = (position) it.next();
            p.buildStringDeg();
            cfg.getInstance().current.copy(p);
            getAltaz();
            // if scope aimed higher or more CW than what it should be, then it is defined as a positive error
            p.azErr.a = p.alt.rad - cfg.getInstance().current.alt.rad;
            // azimuth errors in terms of true field decrease towards the zenith
            p.azErr.z = (p.az.rad - cfg.getInstance().current.az.rad) * Math.cos(p.alt.rad);
            pointErr = Math.sqrt(p.azErr.a * p.azErr.a + p.azErr.z * p.azErr.z);
            pointErrTot += pointErr;
            if (pointErr > maxPointErr)
                maxPointErr = pointErr;
        }
        pointErrRMS = pointErrTot / lp.size();
        cfg.getInstance().current.copy(tempP);
        tempP.available = true;
    }

    boolean computeBestZ12FromPosition(position p) {
        boolean rtn;

        listPosition lp = new listPosition();

        lp.add(p);
        rtn = computeBestZ12FromListPosition(lp);
        lp.free();
        return rtn;
    }

    boolean computeBestZ123FromPosition(position p) {
        boolean rtn;

        listPosition lp = new listPosition();

        lp.add(p);
        rtn = computeBestZ123FromListPosition(lp);
        lp.free();
        return rtn;
    }

    boolean computeBestZ12FromAnalysisFile(listPosition lp) {
        boolean rtn;

        if (cfg.getInstance().initialized() && loadAnalysisFileIntoMemory(lp) && lp.size() > 0) {
            rtn = computeBestZ12FromListPosition(lp);
            return rtn;
        }
        return false;
    }

    boolean computeBestZ123FromAnalysisFile(listPosition lp) {
        boolean rtn;

        if (cfg.getInstance().initialized() && loadAnalysisFileIntoMemory(lp) && lp.size() > 0) {
            rtn = computeBestZ123FromListPosition(lp);
            return rtn;
        }
        return false;
    }

    /**
     * computeBestZ123FromAnalysisFile() with re-init
     */
    boolean computeBestZ123FromAnalysisFileReInit(listPosition lp) {
        if (computeBestZ123FromAnalysisFile(lp)) {
            position tempP = posCol.nextAvail();
            tempP.copy(cfg.getInstance().current);
            setMountErrorsDeg(bestZ1*units.RAD_TO_DEG, bestZ2*units.RAD_TO_DEG, bestZ3*units.RAD_TO_DEG);
            cfg.getInstance().current.copy(cfg.getInstance().one);
            initMatrix(1, WHY_INIT.reInitConversionMatrix);
            cfg.getInstance().current.copy(tempP);
            tempP.available = true;
            return true;
        }
        return false;
    }

    /**
     * compute mount misalignment errors z1, z2, z3 using iterative search;
     * best values picked by mean square of resulting altitude and azimuth errors: that is, find best values of z123 that,
     * after plugging in z123 values, minimize altitude and azimuth errors (azimuth error corrected for cos of altitude);
     * search for z3 outside of search for z12 because including z3 creates multiple local minima that can only be found with brute force
     * or optimization such as annealing;
     * z12 range is +- 7 deg;
     * critically important to separate z1 z2 cleanly by determining accurate azimuths for series of altitudes between 10 and 80 deg;
     * routine run time is 1 sec on 1.5ghz machine, 12 sec on 166mhz machine running Java from DOS prompt (similar code compiled in c and
     * run in DOS completes 10x faster);
     */
    boolean computeBestZ12FromListPosition(listPosition lp) {
        double startZ1 = 0.;
        double startZ2 = 0.;
        double z12Range = 25200.*units.ARCSEC_TO_RAD;
        double z12Interval = 1800.*units.ARCSEC_TO_RAD;
        double minInterval = 3.6*units.ARCSEC_TO_RAD;

        return computeBestZ12FromListPositionSubr(lp, startZ1, startZ2, z12Range, z12Interval, minInterval, bestZ3);
    }

    boolean computeBestZ123FromListPosition(listPosition lp) {
        double startZ1 = 0.;
        double startZ2 = 0.;
        double z12Range = 25200.*units.ARCSEC_TO_RAD;
        double z12Interval = 1800.*units.ARCSEC_TO_RAD;
        double minInterval = 3.6*units.ARCSEC_TO_RAD;

        if (!computeBestZ3FromListPosition(lp)) {
            calcAltOffsetIteratively(cfg.getInstance().one, cfg.getInstance().two);
            bestZ3 = altOffset;
            console.stdOutLn("setting bestZ3 to iterative altitude offset of "
            + altOffset*units.RAD_TO_DEG
            + " calculated from Positions "
            + "one and two");
        }
        return computeBestZ12FromListPositionSubr(lp, startZ1, startZ2, z12Range, z12Interval, minInterval, bestZ3);
    }

    private boolean computeBestZ12FromListPositionSubr(listPosition lp, double startZ1, double startZ2,
    double z12Range, double z12Interval, double minInterval, double workZ3) {
        double holdZ1, holdZ2;
        position tempP = posCol.nextAvail();

        cfg.getInstance().one.showCoordDeg();
        cfg.getInstance().two.showCoordDeg();
        console.stdOutLn("computing best Z1 Z2 from provided positions, please wait...");

        if (cfg.getInstance().initialized() && lp.size()>0) {
            tempP.copy(cfg.getInstance().current);
            holdZ1 = z1;
            holdZ2 = z2;

            bestPointErrRMS = Double.MAX_VALUE;
            bestZ1 = bestZ2 = Double.MAX_VALUE;
            z12Count = 0;
            do {
                for (z1 = startZ1 - z12Range; z1 <= startZ1 + z12Range; z1 += z12Interval)
                    for (z2 = startZ2 - z12Range; z2 <= startZ2 + z12Range; z2 += z12Interval) {
                        setMountErrorsDeg(z1*units.RAD_TO_DEG, z2*units.RAD_TO_DEG, workZ3*units.RAD_TO_DEG);
                        cfg.getInstance().current.copy(cfg.getInstance().one);
                        initMatrix(1, WHY_INIT.z123);
                        calcAnalysisErrors(lp);
                        if (pointErrRMS < bestPointErrRMS) {
                            bestPointErrRMS = pointErrRMS;
                            bestZ1 = z1;
                            bestZ2 = z2;
                            //displayBestZ12RMS();
                        }
                        z12Count++;
                    }
                displayBestZ12RMS();
                startZ1 = bestZ1;
                startZ2 = bestZ2;
                z12Range /= 10.;
                z12Interval /= 10.;
            }while (z12Interval >= minInterval);

            // write out calculated errors
            writeAnalysisFile(lp);
            displayBestZ12RMS();

            z1 = holdZ1;
            z2 = holdZ2;
            setMountErrorsDeg(z1*units.RAD_TO_DEG, z2*units.RAD_TO_DEG, z3*units.RAD_TO_DEG);
            cfg.getInstance().current.copy(cfg.getInstance().one);
            initMatrix(1, WHY_INIT.reInitConversionMatrix);
            cfg.getInstance().current.copy(tempP);
            tempP.available = true;
            return true;
        }
        return false;
    }

    void displayBestZ12RMS() {
        console.stdOutLn("BestZ1Deg "
        + eString.doubleToStringNoGrouping(bestZ1*units.RAD_TO_DEG, 3, 3)
        + " BestZ2Deg "
        + eString.doubleToStringNoGrouping(bestZ2*units.RAD_TO_DEG, 3, 3)
        + ", RMS "
        + eString.doubleToStringNoGrouping(bestPointErrRMS*units.RAD_TO_ARCMIN, 3, 3)
        + " arcmin,"
        + " iterations = "
        + z12Count);
    }

    boolean setZ123RtnErrFromAnalysisFile(listPosition lp) {
        double holdZ1, holdZ2, holdZ3;
        double z1Deg, z2Deg, z3Deg;
        position tempP = posCol.nextAvail();

        if (cfg.getInstance().initialized() && loadAnalysisFileIntoMemory(lp) && lp.size()>0) {
            tempP.copy(cfg.getInstance().current);
            holdZ1 = z1;
            holdZ2 = z2;
            holdZ3 = z3;

            System.out.println("test of Z1Z2Z3 RMS error from analysisFile");
            System.out.print("please enter z1Deg error ");
            console.getDouble();
            z1Deg = console.d;
            System.out.print("please enter z2Deg error ");
            console.getDouble();
            z2Deg = console.d;
            System.out.print("please enter z3Deg error ");
            console.getDouble();
            z3Deg = console.d;
            setMountErrorsDeg(z1Deg, z2Deg, z3Deg);
            cfg.getInstance().current.copy(cfg.getInstance().one);
            initMatrix(1, WHY_INIT.z123);
            calcAnalysisErrors(lp);
            writeAnalysisFile(lp);
            System.out.println("RMS error is " + eString.doubleToStringNoGrouping(pointErrRMS*units.RAD_TO_ARCMIN, 3, 3) + " arcmin");

            z1 = holdZ1;
            z2 = holdZ2;
            z3 = holdZ3;
            initMatrix(1, WHY_INIT.reInitConversionMatrix);
            cfg.getInstance().current.copy(tempP);
            tempP.available = true;
            return true;
        }
        return false;
    }

    int findClosestInit() {
        double init1Distance, init2Distance, init3Distance;

        init1Distance = celeCoordCalcs.calcEquatAngularSepViaHrAngle(cfg.getInstance().current, cfg.getInstance().one);
        init2Distance = celeCoordCalcs.calcEquatAngularSepViaHrAngle(cfg.getInstance().current, cfg.getInstance().two);
        init3Distance = celeCoordCalcs.calcEquatAngularSepViaHrAngle(cfg.getInstance().current, cfg.getInstance().three);
        if (init1Distance < init2Distance && init1Distance < init3Distance)
            return 1;
        else if (init2Distance < init3Distance)
            return 2;
        else
            return 3;
    }

    /**
     * puts initHistoryFile into a listPosition
     */
    void listPositionLoadInitHistory(listPosition lp) {
        position p;
        String s, s1, s2;
        StringTokenizer st;

        try {
            input = new BufferedReader(new FileReader(initHistoryFile));
            s = input.readLine();
            while (s != null) {
                // remove coordinate tags
                st = new StringTokenizer(s, " :\t\n\r\f", true);
                s2 = "";
                while (st.countTokens() > 0) {
                    s1 = st.nextToken();
                    if (s1.equalsIgnoreCase("RA")
                    || s1.equalsIgnoreCase("DEC")
                    || s1.equalsIgnoreCase("ALT")
                    || s1.equalsIgnoreCase("AZ")
                    || s1.equalsIgnoreCase(":"))
                        ;
                    else
                        s2 += s1 + " ";
                }
                // initHistoryFile writes blank line at end of each append, so skip blank line
                if (s2.length() > 0) {
                    st = new StringTokenizer(s2);
                    p = new position();
                    common.fReadEquatAltazCoord(p, st);
                    p.objName(common.tokensToString(st));
                    if (cfg.getInstance().precessionNutationAberration)
                        p.applyPrecessionCorrection();
                    lp.add(p);
                }
                s = input.readLine();
            }
            input.close();
        }
        catch (IOException ioe) {
            console.errOut(ioe.toString());
            ioe.printStackTrace();
            System.err.println("could not open " + initHistoryFile);
        }
    }

    /**
     * sorts listPosition built from initHistoryFile and analysisFile by closest to current altaz coord, then re-inits
     * 2 inits, 3 inits if possible;
     */
    boolean initClosestFromInitHistoryAndAnalysisFiles() {
        listPosition lp = new listPosition();
        listPosition polarAlign2 = new listPosition();
        position temp = new position();
        int ix;

        listPositionLoadInitHistory(lp);
        loadAnalysisFileIntoMemory(polarAlign2);
        // append polarAlign2 analysisFile contents to lp initHistoryFile contents
        for (ix = 0; ix < polarAlign2.size(); ix++)
            lp.add(polarAlign2.get(ix));

        lp.sortClosestAltaz(cfg.getInstance().current);
        //lp.display();
        if (lp.size() >= 2) {
            cfg.getInstance().killInits();
            temp.copy(cfg.getInstance().current);
            cfg.getInstance().current.copy(lp.get(0));
            initMatrix(1, WHY_INIT.closestInitHistory);
            cfg.getInstance().current.copy(lp.get(1));
            initMatrix(2, WHY_INIT.closestInitHistory);
            if (lp.size() >= 3) {
                cfg.getInstance().current.copy(lp.get(2));
                initMatrix(3, WHY_INIT.closestInitHistory);
            }
            cfg.getInstance().current.copy(temp);
            return true;
        }
        return false;
    }

    /**
     * angular separation of two equatorial coordinates should = the angular separation of the corresponding altazimuth coordinates;
     * for target altitudes that cross the equator of their coordinate system, there are two solutions
     *
     * formula from Dave Ek <ekdave@earthlink.net>
     */
    private void calcAltOffset(position a, position z) {
        String s;
        double a1, a2;
        double n = Math.cos(a.az.rad - z.az.rad);
        double m = Math.cos(celeCoordCalcs.calcEquatAngularSepViaHrAngle(a, z));
        double x = (2.*m - (n+1.) * Math.cos(a.alt.rad - z.alt.rad)) / (n-1.);

        // likely causes: azimuths not separate enough resulting in n-1 term being too small, or, variation from ideal numbers in other
        // variables
        if (x > 1. || x < -1.) {
            s = "Bad x in convertMatrix.calcAltOffset(), x="
            + eString.doubleToStringNoGrouping(x, 3, 3)
            + " a.a.rad="
            + eString.doubleToStringNoGrouping(a.alt.rad*units.RAD_TO_DEG, 3, 3)
            + " z.a.rad="
            + eString.doubleToStringNoGrouping(z.alt.rad*units.RAD_TO_DEG, 3, 3)
            + " a.z.rad="
            + eString.doubleToStringNoGrouping(a.az.rad*units.RAD_TO_DEG, 3, 3)
            + " z.z.rad="
            + eString.doubleToStringNoGrouping(z.az.rad*units.RAD_TO_DEG, 3, 3)
            + " sep="
            + eString.doubleToStringNoGrouping(celeCoordCalcs.calcEquatAngularSepViaHrAngle(a, z) * units.RAD_TO_DEG, 3, 3);
            common.badExit(s);
        }
        else {
            a1 = .5 * (+Math.acos(x) - a.alt.rad - z.alt.rad);
            a2 = .5 * (-Math.acos(x) - a.alt.rad - z.alt.rad);
            if (Math.abs(a1) < Math.abs(a2))
                altOffset = a1;
            else
                altOffset = a2;
        }
    }

    /**
     * when angular separation of altaz values closest to that of equat values, best altitude offset found;
     * work with copy of Positions as .alt values changed;
     * +- 45 deg range
     */
    void calcAltOffsetIteratively(position a, position z) {
        position aa = posCol.nextAvail();
        position zz = posCol.nextAvail();
        aa.copy(a);
        zz.copy(z);
        double bestAltOff = Double.MAX_VALUE;
        double diff, lastDiff, bestDiff;
        double incr = units.ARCSEC_TO_RAD;
        int iter;
        // +- 45 deg search range
        int maxIter = (int) (45.*units.DEG_TO_RAD/incr);
        double begA = aa.alt.rad;
        double begZ = zz.alt.rad;

        bestDiff = Double.MAX_VALUE;

        // start from zero offset and increment offset until difference starts to get worse
        lastDiff = Double.MAX_VALUE;
        for (iter = 0; iter < maxIter; iter++, aa.alt.rad += incr, zz.alt.rad += incr) {
            diff = celeCoordCalcs.angSepDiffViaHrAngle(aa, zz);
            if (diff < bestDiff) {
                bestDiff = diff;
                bestAltOff = aa.alt.rad - begA;
            }
            if (diff > lastDiff)
                break;
            else
                lastDiff = diff;
        }
        // again, start from zero offset, but this time decrement offset
        aa.alt.rad = begA;
        zz.alt.rad = begZ;
        diff = lastDiff = Double.MAX_VALUE;
        for (iter = 0; iter < maxIter; iter++, aa.alt.rad -= incr, zz.alt.rad -= incr) {
            diff = celeCoordCalcs.angSepDiffViaHrAngle(aa, zz);
            if (diff < bestDiff) {
                bestDiff = diff;
                bestAltOff = aa.alt.rad - begA;
            }
            if (diff > lastDiff)
                break;
            else
                lastDiff = diff;
        }
        if (bestAltOff == Double.MAX_VALUE)
            altOffset = 0.;
        else
            altOffset = bestAltOff;

        //System.out.println("calcAltOffsetIteratively() calculated altOffset from:");
        //a.showCoordDeg();
        //z.showCoordDeg();
        System.out.println("   altOffset (deg) = " + altOffset*units.RAD_TO_DEG);

        aa.available = zz.available = true;
    }

    private boolean computeBestZ3FromPosition(position p) {
        boolean rtn;

        listPosition lp = new listPosition();

        lp.add(p);
        rtn = computeBestZ3FromListPosition(lp);
        lp.free();
        return rtn;
    }

    private boolean computeBestZ3FromAnalysisFile(listPosition lp) {
        boolean rtn;

        if (cfg.getInstance().initialized() && loadAnalysisFileIntoMemory(lp) && lp.size()>0) {
            rtn = computeBestZ3FromListPosition(lp);
            return rtn;
        }
        return false;
    }

    /**
     * compare all Positions against each other in position list, calculating altitude offset for each pairing, then take the average
     * computing z12 errors critically depends on accurately pegging z3 error;
     * It appears that the most accurate z3 value is calculated from averaging all the altitude offsets as found by comparing all the
     * Positions with each other as found in the analysis file. It is important that the analysis file contains a wide range of Positions
     * all around the sky so that effects of z1 z2 errors are minimized while calculating the averaged altitude offset. Obtaining accurate
     * z2 z3 error values depend critically on a very accurate z3 or altitude offset value. Since z1 z2 depends on very slight azimuth
     * differences as the altitude is varied from equator to pole, an inaccurate z3 value will obscure the untangling of z1 z2.
     */
    private boolean computeBestZ3FromListPosition(listPosition lp) {
        Iterator it;
        Iterator jt;
        int ix, aIx;
        position a;
        position b;
        double tot = 0.;
        int count = 0;

        if (cfg.getInstance().initialized() && lp.size() > 0) {

            calcAltOffsetIteratively(cfg.getInstance().one, cfg.getInstance().two);
            tot += altOffset;
            count++;
            if (cfg.getInstance().three.init) {
                calcAltOffsetIteratively(cfg.getInstance().one, cfg.getInstance().three);
                tot += altOffset;
                count++;
                calcAltOffsetIteratively(cfg.getInstance().two, cfg.getInstance().three);
                tot += altOffset;
                count++;
            }

            it = lp.iterator();
            aIx = 0;
            while (it.hasNext()) {
                a = (position) it.next();

                calcAltOffsetIteratively(a, cfg.getInstance().one);
                tot += altOffset;
                count++;
                calcAltOffsetIteratively(a, cfg.getInstance().two);
                tot += altOffset;
                count++;
                if (cfg.getInstance().three.init) {
                    calcAltOffsetIteratively(a, cfg.getInstance().three);
                    tot += altOffset;
                    count++;
                }

                aIx++;
                jt = lp.iterator();
                for (ix = 0; ix < aIx; ix++)
                    jt.next();
                while (jt.hasNext()) {
                    b = (position) jt.next();
                    calcAltOffsetIteratively(a, b);
                    //console.stdOutLn("Iterative altOffset " + eString.doubleToStringNoGrouping(altOffset * units.RAD_TO_DEG, 3, 3));
                    tot += altOffset;
                    count++;
                }
            }
            if (count > 0)
                bestZ3 = tot/(double) count;
            else {
                console.stdOutLn("Z3 could not be calculated in convertMatrix.computeBestZ3FromListPosition(), setting to 0");
                bestZ3 = 0;
                return false;
            }
            console.stdOutLn("BestZ3Deg " + eString.doubleToStringNoGrouping(bestZ3*units.RAD_TO_DEG, 3, 3));
            return true;
        }
        return false;
    }

    void addAltOffsetReInit() {
        position tempP = posCol.nextAvail();

        cfg.getInstance().current.alt.rad += altOffset;
        cfg.getInstance().three.alt.rad += altOffset;
        cfg.getInstance().two.alt.rad += altOffset;
        cfg.getInstance().one.alt.rad += altOffset;
        altOffset = 0.;
        // reset z3 error which is altitude offset
        z3 = 0.;
        tempP.copy(cfg.getInstance().current);
        cfg.getInstance().current.copy(cfg.getInstance().one);
        initMatrix(1, WHY_INIT.altOffset);
        cfg.getInstance().current.copy(tempP);
        tempP.available = true;
    }

    void test() {
        double s;

        System.out.println("class convertMatrix test");
        cfg.getInstance().killInits();
        System.out.println("from Toshimi Taki's test data, Sky and Telescope magazine, February 1989");
        System.out.println("using z1=-.04, z2=.4, z3=-1.63");
        setMountErrorsDeg(-.04, .4, -1.63);
        cfg.getInstance().current.setCoordDeg(79.172, 45.998, 39.9, 360.-39.9, 39.2*units.SID_RATE/4.);
        initMatrix(1, WHY_INIT.test);
        cfg.getInstance().one.showCoordDeg();
        cfg.getInstance().current.setCoordDeg(37.96, 89.264, 36.2, 360.-94.6, 40.3*units.SID_RATE/4.);
        initMatrix(2, WHY_INIT.test);
        cfg.getInstance().two.showCoordDeg();

        cfg.getInstance().current.setCoordDeg(326.05, 9.88, 0., 0., 47.*units.SID_RATE/4.);
        getAltaz();
        cfg.getInstance().current.showCoordDeg();
        s = 360.-202.54;
        System.out.println("Should be (accurate to 0.01) alt: 42.16   az: " + s);

        cfg.getInstance().current.setCoordDeg(71.53, 17.07, 0., 0., 62.*units.SID_RATE/4.);
        getAltaz();
        cfg.getInstance().current.showCoordDeg();
        s = 360.-359.98;
        System.out.println("Should be (accurate to 0.01) alt: 40.31   az: " + s);

        cfg.getInstance().current.setCoordDeg(0., 0., 35.5, 360.-24.1, 71.9*units.SID_RATE/4.);
        getEquat();
        cfg.getInstance().current.showCoordDeg();
        System.out.println("Should be (accurate to 0.01) ra: 87.99   dec: 32.51");

      /**
      // generate some extra Positions using Taki's initalization numbers
      cfg.getInstance().current.setCoordDeg(79.172, 45.998, 10., 60., 39.2*units.SID_RATE/4.);
      getEquat();
      cfg.getInstance().current.showCoordDeg();
      cfg.getInstance().current.setCoordDeg(79.172, 45.998, 80., 120., 45*units.SID_RATE/4.);
      getEquat();
      cfg.getInstance().current.showCoordDeg();
      cfg.getInstance().current.setCoordDeg(79.172, 45.998, 20., 210., 39.2*units.SID_RATE/4.);
      getEquat();
      cfg.getInstance().current.showCoordDeg();
       */
    }

    /**
     * coordinate hysteresis is a slow creep of the coordinate values as repetitive getEquat() and getAltaz() are called: there
     * should be no meaningful difference as the coordinates are translated back and forth
     */
    void testHysteresis() {
        position tempP = posCol.nextAvail();
        double InputAltDeg, InputAzDeg;
        CONVERT_SUBR_SELECT holdSubrSelect;

        System.out.println("class convert hysteresis test: difference between initial altaz coord and altaz");
        System.out.println("coord after converting to equat coord then converting back to altaz");
        cfg.getInstance().killInits();
        System.out.print("z1=");
        console.getDouble();
        cfg.getInstance().z1Deg = console.d;
        System.out.print("z2=");
        console.getDouble();
        cfg.getInstance().z2Deg = console.d;
        System.out.print("z3=");
        console.getDouble();
        cfg.getInstance().z3Deg = console.d;
        setMountErrorsDeg(cfg.getInstance().z1Deg, cfg.getInstance().z2Deg, cfg.getInstance().z3Deg);
        cfg.getInstance().current.setCoordDeg(79.172, 45.998, 39.9, 360.-39.9, 39.2*units.SID_RATE/4.);
        initMatrix(1, WHY_INIT.test);
        cfg.getInstance().current.setCoordDeg(37.96, 89.264, 36.2, 360.-94.6, 40.3*units.SID_RATE/4.);
        initMatrix(2, WHY_INIT.test);

        System.out.print("enter altitude (deg) ");
        console.getDouble();
        InputAltDeg = console.d;
        System.out.print("enter azimuth (deg) ");
        console.getDouble();
        InputAzDeg = console.d;
        cfg.getInstance().current.setCoordDeg(0., 0., InputAltDeg, InputAzDeg, 47.*units.SID_RATE/4.);
        getEquat();
        tempP.copy(cfg.getInstance().current);
        System.out.println("initial coordinates");
        cfg.getInstance().current.showCoordDeg();

        holdSubrSelect = subrSelect;
        subrSelect = CONVERT_SUBR_SELECT.TakiSimple;
        testHysteresisSubr(tempP);
        subrSelect = CONVERT_SUBR_SELECT.TakiSmallAngle;
        testHysteresisSubr(tempP);
        subrSelect = CONVERT_SUBR_SELECT.BellIterative;
        testHysteresisSubr(tempP);
        subrSelect = CONVERT_SUBR_SELECT.TakiIterative;
        testHysteresisSubr(tempP);
        subrSelect = CONVERT_SUBR_SELECT.BellTaki;
        testHysteresisSubr(tempP);
        subrSelect = holdSubrSelect;

        tempP.available = true;
    }

    private void testHysteresisSubr(position hold) {
        azDouble deltaArcsec = new azDouble();

        getAltaz();

        System.out.println("using routine " + subrSelect);

        if (subrSelect == CONVERT_SUBR_SELECT.BellIterative)
            System.out.println("SubrLCount " + subrLCount);
        if (subrSelect == CONVERT_SUBR_SELECT.TakiIterative)
            System.out.println("SubrTCount " + subrTCount);

        System.out.println("final coordinates");
        cfg.getInstance().current.showCoordDeg();
        deltaArcsec.a = (hold.alt.rad - cfg.getInstance().current.alt.rad)*units.RAD_TO_ARCSEC;
        deltaArcsec.z = (hold.az.rad - cfg.getInstance().current.az.rad)*units.RAD_TO_ARCSEC;
        System.out.println("differences in arcsec are alt="
        + deltaArcsec.a
        + " az="
        + deltaArcsec.z);
    }

    void testFieldRotation() {
        azDouble userDeg = new azDouble();

        System.out.println("class convert field rotation rate test");
        // get user input altaz coord
        System.out.print("please enter altitude deg ");
        console.getDouble();
        userDeg.a = console.d;
        System.out.print("please enter azimuth deg ");
        console.getDouble();
        userDeg.z = console.d;
        cfg.getInstance().current.alt.rad = userDeg.a*units.DEG_TO_RAD;
        cfg.getInstance().current.az.rad = userDeg.z*units.DEG_TO_RAD;
        getEquat();
        calcFieldRotation();
        System.out.println("field rotation angle is  "
        + eString.doubleToStringNoGrouping(fieldRotation*units.RAD_TO_DEG, 3, 3)
        + " deg");

        calcFieldRotationRateForSidTrackViaDeltaFR();
        System.out.println("field rotation rate is "
        + eString.doubleToStringNoGrouping(fieldRotationRateRadMin*units.RAD_TO_DEG, 3, 3)
        + " deg/min");

        cfg.getInstance().current.alt.rad = userDeg.a*units.DEG_TO_RAD;
        cfg.getInstance().current.az.rad = userDeg.z*units.DEG_TO_RAD;
        calcFieldRotationRateForSidTrackViaFormula();
        System.out.println("field rotation rate via formula is "
        + eString.doubleToStringNoGrouping(fieldRotationRateRadMin*units.RAD_TO_DEG, 3, 3)
        + " deg/min");
    }

    void testEquatRefract() {
        azDouble userDeg = new azDouble();
        azDouble holdCfgAltaz = new azDouble();

        // get user input altaz coord
        System.out.println("test of equatorial refraction correction");
        cfg.getInstance().current.sidT.rad = astroTime.getInstance().calcSidT();
        System.out.println("initializing to equatorial alignment");
        setMountErrorsDeg(0., 0., 0.);
        initConvertEquat();
        System.out.print("please enter altitude deg ");
        console.getDouble();
        userDeg.a = console.d;
        site.p.alt.rad = userDeg.a * units.DEG_TO_RAD;
        System.out.print("please enter azimuth deg ");
        console.getDouble();
        userDeg.z = console.d;
        site.p.az.rad = userDeg.z * units.DEG_TO_RAD;

        // first test:
        System.out.println("first test: get scope->sky equatorial refraction, subtract equatorial refraction, get sky's refracted altazimuth coordinates, " +
        "get sky->scope equatorial refraction, compare to starting altazimuth values");
        // SiteAltaz -> SiteEquat -> cfg.getInstance().current equat
        site.p.sidT.rad = cfg.getInstance().current.sidT.rad = astroTime.getInstance().calcSidT();
        // get scope->sky refraction: needs site altaz which were input by user
        calcSiteRefractScopeToSky();
        System.out.println("scope->sky refraction in arcseconds: "
        + refract.refract*units.RAD_TO_ARCSEC
        + "\n   delta in arcseconds: dec/alt="
        + RefractDec*units.RAD_TO_ARCSEC
        + " ra/az="
        + RefractRa*units.RAD_TO_ARCSEC);
        // calcSiteRefractScopeToSky() does a site.getEquat()
        cfg.getInstance().current.ra.rad = site.p.ra.rad;
        cfg.getInstance().current.dec.rad = site.p.dec.rad;
        // add correction making cfg.getInstance().current 'sky' equat
        cfg.getInstance().current.ra.rad -= RefractRa;
        cfg.getInstance().current.dec.rad -= RefractDec;
        // back it out...
        // get 'sky' site altaz from cfg.getInstance().current 'sky' equat
        site.p.ra.rad = cfg.getInstance().current.ra.rad;
        site.p.dec.rad = cfg.getInstance().current.dec.rad;
        site.getAltaz();
        // get sky->scope refraction: needs site altaz set by above function site.getAltaz()
        calcSiteRefractSkyToScope();
        System.out.println("sky->scope refraction in arcseconds: "
        + refract.refract*units.RAD_TO_ARCSEC
        + "\n   delta in arcseconds: dec/alt="
        + RefractDec*units.RAD_TO_ARCSEC
        + " ra/az="
        + RefractRa*units.RAD_TO_ARCSEC);
        // remove sky->scope refraction (site.p.az doesn't change with refraction)
        site.p.alt.rad += refract.refract;
        System.out.println("doing scope->sky translation, then doing sky->scope translation via equat coord gives"
        + " altitude deg = "
        + site.p.alt.rad*units.RAD_TO_DEG
        + " azimuth deg = "
        + site.p.az.rad*units.RAD_TO_DEG);

        // second test:
        System.out.println("\nsecond test, first part: after coordinate translation get translate scope->sky refraction, "
        + "adjust cfg.getInstance().current alt & az coords using equatorial refraction values "
        + "(in equatorial alignment so current alt/az equivalent to dec/Ra), "
        + "then translate coordinates (equat->altaz), and compare equatorial coordinates");
        site.p.alt.rad = userDeg.a * units.DEG_TO_RAD;
        site.p.az.rad = userDeg.z * units.DEG_TO_RAD;
        // aim scope at site altaz
        site.getEquat();
        cfg.getInstance().current.ra.rad = site.p.ra.rad;
        cfg.getInstance().current.dec.rad = site.p.dec.rad;
        getAltaz();
        holdCfgAltaz.a = cfg.getInstance().current.alt.rad;
        holdCfgAltaz.z = cfg.getInstance().current.az.rad;
        // get scope->sky refraction
        calcSiteRefractScopeToSky();
        System.out.println("scope->sky refraction in arcseconds: "
        + refract.refract*units.RAD_TO_ARCSEC
        + "\n   delta in arcseconds: dec/alt="
        + RefractDec*units.RAD_TO_ARCSEC
        + " ra/az="
        + RefractRa*units.RAD_TO_ARCSEC);
        // scope/encoder/servo of 0 deg points to -34.5 arcmin sky coord
        cfg.getInstance().current.az.rad += RefractRa;
        cfg.getInstance().current.alt.rad -= RefractDec;
        // sky new equat coord
        getEquat();
        // compare to site's equat obtained by compensating for scope->sky refraction to obtain site's version of sky equat coord
        site.p.alt.rad -= refract.refract;
        site.getEquat();
        System.out.println("after scope->sky refraction translation, errors in equatorial coordinates in arcseconds are"
        + " ra "
        + (cfg.getInstance().current.ra.rad-site.p.ra.rad)*units.RAD_TO_ARCSEC
        + " dec "
        + (cfg.getInstance().current.dec.rad-site.p.dec.rad)*units.RAD_TO_ARCSEC);
        // now return to scope coordinates by getting sky->scope translation correction:
        // start by getting cfg.getInstance().current equat coord from newly adjusted cfg.getInstance().current altaz coord
        System.out.println("\nsecond test, second part: starting with just obtained equatorial coordinates, "
        + "get sky->scope equatorial refraction, change cfg.getInstance().current alt and az, and compare to starting input altaz values");
        getEquat();
        // and get sky->scope refraction
        site.p.ra.rad = cfg.getInstance().current.ra.rad;
        site.p.dec.rad = cfg.getInstance().current.dec.rad;
        site.getAltaz();
        calcSiteRefractSkyToScope();
        System.out.println("sky->scope refraction in arcseconds: "
        + refract.refract*units.RAD_TO_ARCSEC
        + "\n   delta in arcseconds: dec/alt="
        + RefractDec*units.RAD_TO_ARCSEC
        + " ra/az="
        + RefractRa*units.RAD_TO_ARCSEC);
        // translate coordinates from sky->scope
        site.p.alt.rad += refract.refract;
        cfg.getInstance().current.az.rad -= RefractRa;
        cfg.getInstance().current.alt.rad += RefractDec;
        // should agree with starting values: nonzero z12 values will cause sub-arcsec hysteresis
        System.out.println("after sky->scope translation, differences from user input altaz values in arcseconds are "
        + " alt "
        + (holdCfgAltaz.a-cfg.getInstance().current.alt.rad)*units.RAD_TO_ARCSEC
        + " az "
        + (holdCfgAltaz.z-cfg.getInstance().current.az.rad)*units.RAD_TO_ARCSEC);
    }

    void testAltOffset() {
        System.out.println("Test of altitude offset code...");
        cfg.getInstance().one.init = cfg.getInstance().two.init = true;
        cfg.getInstance().three.init = false;

        System.out.println("               RA      dec     alt      az    sidT");
        System.out.println("example: 1:    72.1, 20.4333, 63.785, 210.287, 0");
        System.out.println("         2: 359.138,  2.5833,  4.164,  269.58, 0");
        System.out.println("");
        System.out.println("please select from:");
        System.out.println("   1. entering ra, dec, alt, az, sidT, and +- equal amount from both altitudes");
        System.out.println("   2. using above values but entering only an altitude offset");
        console.getInt();
        if (console.i == 1) {
            System.out.print("1st (deg) ra?");
            console.getDouble();
            cfg.getInstance().one.ra.rad = console.d*units.DEG_TO_RAD;
            System.out.print("dec? ");
            console.getDouble();
            cfg.getInstance().one.dec.rad = console.d*units.DEG_TO_RAD;
            System.out.print("alt? ");
            console.getDouble();
            cfg.getInstance().one.alt.rad = console.d*units.DEG_TO_RAD;
            System.out.print("az? " );
            console.getDouble();
            cfg.getInstance().one.az.rad = console.d*units.DEG_TO_RAD;
            System.out.print("sidT? ");
            console.getDouble();
            cfg.getInstance().one.sidT.rad = console.d*units.DEG_TO_RAD;

            System.out.print("2nd (deg) ra? ");
            console.getDouble();
            cfg.getInstance().two.ra.rad = console.d*units.DEG_TO_RAD;
            System.out.print("dec? ");
            console.getDouble();
            cfg.getInstance().two.dec.rad = console.d*units.DEG_TO_RAD;
            System.out.print("alt? ");
            console.getDouble();
            cfg.getInstance().two.alt.rad = console.d*units.DEG_TO_RAD;
            System.out.print("az? ");
            console.getDouble();
            cfg.getInstance().two.az.rad = console.d*units.DEG_TO_RAD;
            System.out.print("sidT? ");
            console.getDouble();
            cfg.getInstance().two.sidT.rad = console.d*units.DEG_TO_RAD;
        }
        else {
            System.out.print("altitude offset (deg)? ");
            console.getDouble();
            cfg.getInstance().one.setCoordDeg(72.1, 20.4333, 63.785+console.d, 210.287, 0);
            cfg.getInstance().two.setCoordDeg(359.138,  2.5833,  4.164+console.d,  269.58, 0);
        }

        calcAltOffsetIteratively(cfg.getInstance().one, cfg.getInstance().two);
        System.out.println("Iterative altOffset "
        + eString.doubleToStringNoGrouping(altOffset * units.RAD_TO_DEG, 3, 3)
        + " one.alt "
        + eString.doubleToStringNoGrouping((cfg.getInstance().one.alt.rad + altOffset) * units.RAD_TO_DEG, 3, 3)
        + " two.alt "
        + eString.doubleToStringNoGrouping((cfg.getInstance().two.alt.rad + altOffset) * units.RAD_TO_DEG, 3, 3));

        calcAltOffset(cfg.getInstance().one, cfg.getInstance().two);
        System.out.println("Formula altOffset "
        + eString.doubleToStringNoGrouping(altOffset * units.RAD_TO_DEG, 3, 3)
        + " one.alt "
        + eString.doubleToStringNoGrouping((cfg.getInstance().one.alt.rad + altOffset) * units.RAD_TO_DEG, 3, 3)
        + " two.alt "
        + eString.doubleToStringNoGrouping((cfg.getInstance().two.alt.rad + altOffset) * units.RAD_TO_DEG, 3, 3));
    }

    void testBestZ123FromAlteredPosition() {
        double z1Deg, z2Deg, z3Deg;

        position test = new position("testBestZ123FromAlteredPosition");
        azDouble userErr = new azDouble();
        CONVERT_SUBR_SELECT holdSubrSelect = subrSelect;

        System.out.println("Test of computation of z1, z2, z3:");
        System.out.println("Given two initialization Positions and a third pointing position, find best Z1Z2Z3 "
        + "that minimizes difference between calculated altaz and pointing position.");
        // .TakiIterative does not converge to solution
        subrSelect = CONVERT_SUBR_SELECT.BellIterative;
        holdCurrentDec = -9999.;
        checkHoldSinCosCurrentDec();
        z1Deg = z2Deg = z3Deg = 0.;
        setMountErrorsDeg(z1Deg, z2Deg, z3Deg);
        cfg.getInstance().one.setCoordDeg(140.734112, 0., 0., 0., 140.734112);
        cfg.getInstance().two.setCoordDeg(120.75, 20., 20., 20., 140.75);
        cfg.getInstance().one.init = cfg.getInstance().two.init = true;
        cfg.getInstance().three.init = false;
        cfg.getInstance().current.copy(cfg.getInstance().one);
        initMatrix(1, WHY_INIT.test);
        System.out.println("adopted an equatorial alignment:");
        cfg.getInstance().one.showCoordDeg();
        cfg.getInstance().two.showCoordDeg();
        System.out.println("aiming towards west horizon...");
        System.out.print("enter test altitude error deg: ");
        console.getDouble();
        userErr.a = console.d;
        System.out.print("enter test azimuth error deg: ");
        console.getDouble();
        userErr.z = console.d;
        test.setCoordDeg(50.734112, 45., 45.+userErr.a, 90.+userErr.z, 140.734112);
        computeBestZ123FromPosition(test);
        System.out.println("arrived at z1 "
        + eString.doubleToStringNoGrouping(bestZ1*units.RAD_TO_DEG, 3, 3)
        + " z2 "
        + eString.doubleToStringNoGrouping(bestZ2*units.RAD_TO_DEG, 3, 3)
        + " z3 "
        + eString.doubleToStringNoGrouping(bestZ3*units.RAD_TO_DEG, 3, 3));
        subrSelect = holdSubrSelect;
    }

    void testComputeBestZ123FromAnalysisFile(listPosition lp) {
        System.out.println("Before running, copy scope.analysis.taki to "
        + eString.PGM_NAME
        + ".analysis. Routine should return "
        + "z1=-.04, z2=.4, z3=-1.63.");
        System.out.println("from Toshimi Taki's test data, Sky and Telescope magazine, February 1989");

        setMountErrorsDeg(0., 0. ,0.);
        displayZ123String();

        cfg.getInstance().current.setCoordDeg(79.172, 45.998, 39.9, 360.-39.9, 39.2*units.SID_RATE/4.);
        initMatrix(1, WHY_INIT.test);
        cfg.getInstance().current.setCoordDeg(37.96, 89.264, 36.2, 360.-94.6, 40.3*units.SID_RATE/4.);
        initMatrix(2, WHY_INIT.test);
        if (computeBestZ123FromAnalysisFile(lp))
            ;
        else
            System.out.println("no init1 or init2, or, could not find "
            + analysisFile
            + ", or file empty, or unable to optimize results");
    }

    void testZ12Compare() {

        class zData {
            position p;
            double z1Deg;
            double z2Deg;
            azDouble z1Pos;
            azDouble z1Err;
            azDouble z2Pos;
            azDouble z2Err; {
                p = new position("testZ12Compare");
                z1Pos = new azDouble();
                z1Err = new azDouble();
                z2Pos = new azDouble();
                z2Err = new azDouble();
            }
        }

        int ix;
        double z1Deg;
        double z2Deg;

        System.out.println("initializing to altazimuth alignment with no Z123 errors");
        setMountErrorsDeg(0., 0., 0.);
        initConvertAltaz();
        zData[] zData = new zData[90];
        for (ix = 0; ix < 90; ix++)
            zData[ix] = new zData();

        cfg.getInstance().current.sidT.rad = astroTime.getInstance().calcSidT();

        System.out.println("comparing altazimuth Positions resulting from z1 and z2 errors");
        System.out.print("enter z1 error in degrees ");
        console.getDouble();
        z1Deg = console.d;
        System.out.print("enter z2 error in degrees ");
        console.getDouble();
        z2Deg = console.d;
        // az is immaterial - can be any value without affecting Z12 errors and their difference
        cfg.getInstance().current.az.rad = 0.;

        // build array with no z12 errors
        for (ix = 0; ix < 90; ix++) {
            cfg.getInstance().current.alt.rad = (double) ix*units.DEG_TO_RAD;
            getEquat();
            zData[ix].p.copy(cfg.getInstance().current);
            zData[ix].z1Deg = z1Deg;
            zData[ix].z2Deg = z2Deg;
        }
        // add values for z1 error
        setMountErrorsDeg(z1Deg, 0., 0.);
        initConvertAltaz();
        for (ix = 0; ix < 90; ix++) {
            cfg.getInstance().current.ra.rad = zData[ix].p.ra.rad;
            cfg.getInstance().current.dec.rad = zData[ix].p.dec.rad;
            getAltaz();
            zData[ix].z1Pos.a = cfg.getInstance().current.alt.rad;
            zData[ix].z1Pos.z = cfg.getInstance().current.az.rad;
            zData[ix].z1Err.a = zData[ix].p.alt.rad - zData[ix].z1Pos.a;
            zData[ix].z1Err.z = zData[ix].p.az.rad - zData[ix].z1Pos.z;
        }
        // add values for z2 error
        setMountErrorsDeg(0., z2Deg, 0.);
        initConvertAltaz();
        for (ix = 0; ix < 90; ix++) {
            cfg.getInstance().current.ra.rad = zData[ix].p.ra.rad;
            cfg.getInstance().current.dec.rad = zData[ix].p.dec.rad;
            getAltaz();
            zData[ix].z2Pos.a = cfg.getInstance().current.alt.rad;
            zData[ix].z2Pos.z = cfg.getInstance().current.az.rad;
            zData[ix].z2Err.a = zData[ix].p.alt.rad - zData[ix].z2Pos.a;
            zData[ix].z2Err.z = zData[ix].p.az.rad - zData[ix].z2Pos.z;
        }

        // write out data
        System.out.println("az(deg)   Z1azErr(armin)   Z1azErrComp(armin)   Z2azErr(armin)   Z2azErrComp(armin)   DiffInZ12azErrComp's");
        System.out.println("ErrComp compensated for shrinking of az as zenith is approached;");
        System.out.println("positive error means that Z12az is less than ideal");
        System.out.println("select Z1 and Z2 so that the difference between Z1, Z2 compensated errors are matched at the "
        + "start and end of the desired altitude range (ie, try Z1=.5, Z2=-.707 and compare alt range of 10 and 80)");
        for (ix = 0; ix < 90; ix++)
            System.out.println(ix
            + ",   "
            + eString.doubleToStringNoGrouping(zData[ix].z1Err.z*units.RAD_TO_ARCMIN, 4, 2)
            + ",   "
            + eString.doubleToStringNoGrouping(Math.cos(ix*units.DEG_TO_RAD) * zData[ix].z1Err.z*units.RAD_TO_ARCMIN, 4, 2)
            + ",   "
            + eString.doubleToStringNoGrouping(zData[ix].z2Err.z*units.RAD_TO_ARCMIN, 4, 2)
            + ",   "
            + eString.doubleToStringNoGrouping(Math.cos(ix*units.DEG_TO_RAD) * zData[ix].z2Err.z*units.RAD_TO_ARCMIN, 4, 2)
            + ",   "
            + eString.doubleToStringNoGrouping((Math.cos(ix*units.DEG_TO_RAD) * zData[ix].z1Err.z
            - Math.cos(ix*units.DEG_TO_RAD) * zData[ix].z2Err.z)*units.RAD_TO_ARCMIN, 4, 2));
    }

    void testAltAltAzTrack() {
        System.out.println("Test of altAltAz tracking has been deprecated");
    }
}

