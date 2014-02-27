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
 * class that simulates a PICServo microcontroller's motion action;
 * inherits the trajectory's class methods through composition;
 */
public class PICServoSim {
    PICSERVO_SIM_CMD PICServoSimCmd;
    String PICServoSimString;
    double startJD;
    double startPosRad;
    double startVelRadSec;
    double accelRadSecSec;
    double targetDistanceRad;
    double targetVelRadSec;
    double maxVelRadSec;
    double currentPosRad;
    double currentVelRadSec;
    double timeDiffSec;
    TRAJECTORY_STYLE trajectoryStyle;
    trajectory traj = new trajectory();

    PICServoSim() {}

    void buildResetPos(double startJD, double startPosRad) {
        PICServoSimCmd = PICSERVO_SIM_CMD.PICServoSimCmdResetPos;
        this.startJD = startJD;
        this.startPosRad = startPosRad;
        trajectory.trajFromAccelDistanceMaxVel(traj, 0., 0., 0.);
    }

    void buildPos(double startJD, double accelRadSecSec, double targetDistanceRad, double maxVelRadSec) {
        PICServoSimCmd = PICSERVO_SIM_CMD.PICServoSimCmdPos;
        this.startJD = startJD;
        this.accelRadSecSec = accelRadSecSec;
        this.targetDistanceRad = targetDistanceRad;
        this.maxVelRadSec = maxVelRadSec;
        trajectory.trajFromAccelDistanceMaxVel(traj, accelRadSecSec, targetDistanceRad, maxVelRadSec);
        trajectoryStyle = TRAJECTORY_STYLE.position;
    }

    void buildVel(double startJD, double accelRadSecSec, double startVelRadSec, double targetVelRadSec) {
        PICServoSimCmd = PICSERVO_SIM_CMD.PICServoSimCmdVel;
        this.startJD = startJD;
        this.accelRadSecSec = accelRadSecSec;
        this.startVelRadSec = startVelRadSec;
        this.targetVelRadSec = targetVelRadSec;
        trajectory.trajFromAccelBegVelTargetVel(traj, accelRadSecSec, startVelRadSec, targetVelRadSec);
        trajectoryStyle = TRAJECTORY_STYLE.velocity;
    }

    double currentPosRad(double JD) {
        if (PICServoSimCmd == PICSERVO_SIM_CMD.PICServoSimCmdResetPos)
            currentPosRad = startPosRad;
        else if (PICServoSimCmd == PICSERVO_SIM_CMD.PICServoSimCmdPos)
            currentPosRad = startPosRad +
            trajectory.distanceFromTrajFromAccelDistanceMaxVel(traj, timeDiffSec(JD), accelRadSecSec, maxVelRadSec);
        else if (PICServoSimCmd == PICSERVO_SIM_CMD.PICServoSimCmdVel)
            currentPosRad = startPosRad +
            trajectory.distanceFromTrajFromTimeAccelBegVelTargetVel(traj, timeDiffSec(JD));

        return currentPosRad;
    }

    double currentVelRadSec(double JD) {
        if (PICServoSimCmd == PICSERVO_SIM_CMD.PICServoSimCmdResetPos)
            currentVelRadSec = 0.;
        else if (PICServoSimCmd == PICSERVO_SIM_CMD.PICServoSimCmdPos)
            currentVelRadSec = trajectory.velFromTrajFromAccelDistanceMaxVel(traj, timeDiffSec(JD));
        else if (PICServoSimCmd == PICSERVO_SIM_CMD.PICServoSimCmdVel)
            currentVelRadSec = trajectory.VelFromTrajFromTimeAccelBegVelTargetVel(traj, timeDiffSec(JD));

        return currentVelRadSec;
    }

    String buildPICServoSimString() {
        PICServoSimString = PICServoSimCmd
        + " startJD "
        + startJD
        + " startPosRad "
        + startPosRad
        + " startVelRadSec "
        + startVelRadSec
        + " "
        + traj.buildTrajDisplayString();
        return PICServoSimString;
    }

    double timeDiffSec(double JD) {
        return (JD - startJD) * units.DAY_TO_SEC;
    }

    void test() {
        boolean quit = false;
        int select;

        System.out.println("test of PICServoSim");
        while (!quit) {
            System.out.println("select from the following, or any other number to quit");

            Enumeration ePICSERVO_SIM_CMD = PICSERVO_SIM_CMD.elements();
            while (ePICSERVO_SIM_CMD.hasMoreElements()) {
                PICSERVO_SIM_CMD psc = (PICSERVO_SIM_CMD) ePICSERVO_SIM_CMD.nextElement();
                System.out.println("      "
                + (psc.KEY+1)
                + ". "
                + psc);
            }
            console.getInt();
            select = console.i;
            if (select < 1 || select > PICSERVO_SIM_CMD.size()) {
                quit = true;
                break;
            }

            System.out.print("enter starting time in JD(days) ");
            console.getDouble();
            startJD = console.d;

            System.out.print("enter accelRadSecSec ");
            console.getDouble();
            accelRadSecSec = console.d;

            if (select == PICSERVO_SIM_CMD.PICServoSimCmdResetPos.KEY+1)
                buildResetPos(startJD, 0);
            else if (select == PICSERVO_SIM_CMD.PICServoSimCmdPos.KEY+1) {
                System.out.print("enter targetDistanceRad ");
                console.getDouble();
                targetDistanceRad = console.d;
                System.out.print("enter maxVelRadSec ");
                console.getDouble();
                maxVelRadSec = console.d;
                buildPos(startJD, accelRadSecSec, targetDistanceRad, maxVelRadSec);
            }
            else if (select == PICSERVO_SIM_CMD.PICServoSimCmdVel.KEY+1) {
                System.out.print("enter targetVelRadSec ");
                console.getDouble();
                targetVelRadSec = console.d;
                buildVel(startJD, accelRadSecSec, 0, targetVelRadSec);
            }
            System.out.println(buildPICServoSimString());
        }
    }
}

