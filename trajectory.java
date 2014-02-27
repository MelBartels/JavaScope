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
 * trapezoidal trajectory and methods of manipulation
 */
public class trajectory {
    motionVector rampUp = new motionVector();
    motionVector maxVel = new motionVector();
    motionVector rampDown = new motionVector();
    String trajDisplayString;
    double totalTime;
    double totalDistance;
    double startSidT;
    double startRampDownSidT;
    double endSidT;
    TRAJ_MOVE_STATUS moveStatus;
    double holdPosDeg;
    double deltaPosDeg;
    double errorPosDeg;
    static timeDistance td = new timeDistance();

    trajectory() {
        endSidT = astroTime.getInstance().sidT.rad;
        moveStatus = TRAJ_MOVE_STATUS.waitToBuildTraj;
    }

    String buildTrajDisplayString() {
        trajDisplayString = "trajectory totalTime="
        + totalTime
        + " totalDistance="
        + totalDistance
        + "\n";

        trajDisplayString += "rampUp: ";
        rampUp.buildMotionVectorDisplayString();
        trajDisplayString += rampUp.motionVectorDisplayString + "\n";

        trajDisplayString += "MaxVel: ";
        maxVel.buildMotionVectorDisplayString();
        trajDisplayString += maxVel.motionVectorDisplayString + "\n";

        trajDisplayString += "rampDown: ";
        rampDown.buildMotionVectorDisplayString();
        trajDisplayString += rampDown.motionVectorDisplayString + "\n";

        return trajDisplayString;
    }

    void display() {
        console.stdOutLn(buildTrajDisplayString());
    }

    /**
     * make accel fit direction implied with begVel and finalVel
     */
    static double signAccel(double accel, double begVel, double finalVel) {
        if (finalVel > begVel)
            return Math.abs(accel);
        else
            return -Math.abs(accel);
    }

    static void timeDistanceFromAccelBegVelEndVel(timeDistance td, double accel, double begVel, double finalVel) {
        accel = signAccel(accel, begVel, finalVel);
        td.time = Math.abs((begVel - finalVel) / accel);
        td.distance = begVel * td.time + accel * td.time * td.time / 2.;
    }

    /**
     * include check for time
     */
    static void timeDistanceFromTimeAccelBegVelEndVel(timeDistance td, double time, double accel, double begVel, double finalVel) {
        accel = signAccel(accel, begVel, finalVel);
        td.time = Math.abs((begVel - finalVel) / accel);
        if (time < td.time)
            td.time = time;
        td.distance = begVel * td.time + accel * td.time * td.time / 2.;
    }

    /**
     * time+distance for two trajectories to close within so that when change between begVel and finalVel
     * is finished the two trajectories coincide;
     * distance is (final vel's start position) - start position;
     * takes into account position change based on final vel and time that occurs during move;
     */
    static void triggerTimeDistanceFromAccelBegVelEndVel(timeDistance td, double accel, double begVel, double finalVel) {
        double FinalPosDistance;

        // time, distance for initial position to complete change from begVel to finalVel
        timeDistanceFromAccelBegVelEndVel(td, accel, begVel, finalVel);
        // distance of final position travel while initial position is completing its move
        FinalPosDistance = finalVel * td.time;
        td.distance -= FinalPosDistance;
    }

    /**
     * does NOT take into account the distance traveled while move in progress;
     */
    static double maxVelFromAccel_UnCompDistance_BegVelEndVel(double accel, double distance, double begVel, double finalVel) {
        boolean negativeMV;
        double mv;

        if (accel < 0.)
            accel = -accel;
        if (distance < 0.) {
            negativeMV = true;
            distance = -distance;
            begVel = -begVel;
            finalVel = -finalVel;
        }
        else
            negativeMV = false;

        mv = Math.sqrt(accel*distance + begVel*begVel/2. + finalVel*finalVel/2.);
        if (negativeMV)
            return -mv;
        else
            return mv;
    }

    /**
     * takes into account the distance traveled while move in progress: distance parameter is starting separation
     * between Positions or net distance to cover; if finalVel != 0, distance moved by by first position in order to
     * reach target != net distance since target has moved by finalVel*time_of_first_Position_move;
     * since time is not known, net distance cannot be calculated from finalVel*time, so must use this equation
     */
    static double maxVelFromAccelDistanceBegVelEndVel(double accel, double distance, double begVel, double finalVel) {
        boolean negativeMV;
        double mv;

        if (accel < 0.)
            accel = -accel;
        if (distance < 0.) {
            negativeMV = true;
            distance = -distance;
            begVel = -begVel;
            finalVel = -finalVel;
        }
        else
            negativeMV = false;

        mv = Math.sqrt(accel*distance - begVel*finalVel + begVel*begVel/2. + finalVel*finalVel/2.) + finalVel;
        if (negativeMV)
            return -mv;
        else
            return mv;
    }

    /**
     * takes into account the distance traveled while move in progress: distance parameter is starting separation
     * between Positions or net distance to cover; if finalVel != 0, distance moved by by first position in order to
     * reach target != net distance since target has moved by finalVel*time_of_first_Position_move;
     * since time is not known, net distance cannot be calculated from finalVel*time, so must use this equation;
     * first trajectory ends with zero final velocity matching position of second trajectory which continues at final velocity
     */
    static double maxVelFromAccelDistanceBegVelEndVelFirstTrajZeroEndVel(double accel, double distance, double begVel, double finalVel) {
        boolean negativeMV;
        double mv;

        if (accel < 0.)
            accel = -accel;
        if (distance < 0.) {
            negativeMV = true;
            distance = -distance;
            begVel = -begVel;
            finalVel = -finalVel;
        }
        else
            negativeMV = false;

        mv = Math.sqrt(accel*distance - begVel*finalVel + begVel*begVel/2. + finalVel*finalVel) + finalVel;
        if (negativeMV)
            return -mv;
        else
            return mv;
    }

    /**
     * does NOT take into account the distance traveled while move in progress;
     */
    static double maxVelFromTime_UnCompDistance_BegVelEndVel(double time, double distance, double begVel, double finalVel) {
        boolean negativeMV;
        double s;
        double mv;

        if (time <= 0.)
            time = 1/Double.MAX_VALUE;

        if (distance < 0.) {
            negativeMV = true;
            distance = -distance;
            begVel = -begVel;
            finalVel = -finalVel;
        }
        else
            negativeMV = false;

        // after 's' is checked, rest of equation is completed
        s = Math.sqrt(begVel*begVel/2. + finalVel*finalVel/2. - (begVel+finalVel)*distance/time + Math.pow(distance/time, 2));
        if (distance < time * (begVel + finalVel)/2.)
            s = -s;
        mv = s + distance/time;
        if (negativeMV)
            return -mv;
        else
            return mv;
    }

    /**
     * takes into account the distance traveled while move in progress: distance parameter is starting separation
     * between Positions or net distance to cover; if finalVel != 0, distance moved by by first position in order to
     * reach target != net distance since target has moved by finalVel*time_of_first_Position_move;
     */
    static double maxVelFromTimeDistanceBegVelEndVel(double time, double distance, double begVel, double finalVel) {
        double newDistance;

        newDistance = distance + finalVel*time;
        return maxVelFromTime_UnCompDistance_BegVelEndVel(time, newDistance, begVel, finalVel);
    }

    static void trajFromTimeDistanceBegVelEndVel(trajectory traj, double time, double distance, double begVel, double finalVel) {
        double maxVel;
        double accel;

        maxVel = maxVelFromTimeDistanceBegVelEndVel(time, distance, begVel, finalVel);
        accel = Math.abs(2. * maxVel - begVel - finalVel) / time;
        trajFromAccelDistanceMaxVelBegVelEndVel(traj, accel, distance, maxVel, begVel, finalVel);
    }

    /**
     * if Positions in radians and time in seconds, return final vel will be radians/sec;
     * if Positions in radians and time in radians, return final vel will be radians/radian;
     */
    static double finalVelFromAccelDistanceTimeDiff(double accel, double distance, double timediff) {
        double avgvel;
        double finalVel;

        avgvel = distance/timediff;
        finalVel = avgvel + accel*timediff/2;
        return finalVel;
    }

    /**
     * builds a Positional trapezoidal trajectory;
     * assumes starting and ending velocities of zero
     */
    static void trajFromAccelDistanceMaxVel(trajectory traj, double accel, double distance, double maxvel) {
        ROTATION dir;
        double maxVel;
        double maxVelToUse;
        double remainDistance;

        if (distance >= 0.)
            dir = ROTATION.CW;
        else {
            distance = -distance;
            dir = ROTATION.CCW;
        }
        // maxvel should always be passed in as a positive value: if not, fix it
        maxvel = Math.abs(maxvel);
        // find theoretical max velocity of trajectory
        maxVel = maxVelFromAccel_UnCompDistance_BegVelEndVel(accel, distance, 0., 0.);
        if (maxVel > maxvel)
            maxVelToUse = maxvel;
        else
            maxVelToUse = maxVel;

        timeDistanceFromAccelBegVelEndVel(td, accel, 0., maxVelToUse);
        traj.rampUp.begVel = 0.;
        traj.rampUp.endVel = maxVelToUse;
        traj.rampUp.accel = accel;
        traj.rampUp.time = td.time;
        traj.rampUp.distance = td.distance;

        timeDistanceFromAccelBegVelEndVel(td, accel, maxVelToUse, 0);
        traj.rampDown.begVel = maxVelToUse;
        traj.rampDown.endVel = 0;
        traj.rampDown.accel = -accel;
        traj.rampDown.time = td.time;
        traj.rampDown.distance = td.distance;

        traj.maxVel.begVel = maxVelToUse;
        traj.maxVel.endVel = maxVelToUse;
        traj.maxVel.accel = 0.;

        // remaining distance
        remainDistance = distance - (traj.rampUp.distance + traj.rampDown.distance);

        // if remainDistance>0, then equivalent to maxVel>maxvel where move is a trapezoid,
        // else move is a sawtooth (possibly truncated by reduced max vel)
        traj.maxVel.time = Math.abs(remainDistance/maxVelToUse);
        traj.maxVel.distance = traj.maxVel.time * maxVelToUse;
        traj.totalTime = traj.rampUp.time + traj.rampDown.time + traj.maxVel.time;
        traj.totalDistance = traj.rampUp.distance + traj.rampDown.distance + traj.maxVel.distance;

        if (dir == ROTATION.CCW) {
            traj.rampUp.endVel = -traj.rampUp.endVel;
            traj.rampUp.accel = -traj.rampUp.accel;
            traj.rampUp.distance = -traj.rampUp.distance;

            traj.rampDown.begVel = -traj.rampDown.begVel;
            traj.rampDown.accel = -traj.rampDown.accel;
            traj.rampDown.distance = -traj.rampDown.distance;

            traj.maxVel.begVel = -traj.maxVel.begVel;
            traj.maxVel.endVel = -traj.maxVel.endVel;
            traj.maxVel.distance = -traj.maxVel.distance;

            traj.totalDistance = -traj.totalDistance;
        }
    }

    /**
     * return distance traveled in a Positional trapezoidal trajectory based on time elapsed where begVel and endVel are zero
     */
    static double distanceFromTimeAccelDistanceMaxVel(trajectory traj, double time, double accel, double distance, double maxvel) {
        trajFromAccelDistanceMaxVel(traj, accel, distance, maxvel);
        return distanceFromTrajFromAccelDistanceMaxVel(traj, time, accel, maxvel);
    }

    static double distanceFromTrajFromAccelDistanceMaxVel(trajectory traj, double time, double accel, double maxvel) {
        double distance;

        if (time >= traj.rampUp.time) {
            distance = traj.rampUp.distance;
            if (time >= traj.rampUp.time + traj.maxVel.time) {
                distance += traj.maxVel.distance;
                if (time >= traj.totalTime)
                    distance = traj.totalDistance;
                else {
                    timeDistanceFromTimeAccelBegVelEndVel(td, time-(traj.rampUp.time+traj.maxVel.time), traj.rampDown.accel, traj.rampDown.begVel, 0.);
                    distance += td.distance;
                }
            }
            else
                distance += (time - traj.rampUp.time) * traj.maxVel.begVel;
        }
        else {
            timeDistanceFromTimeAccelBegVelEndVel(td, time, accel, 0., maxvel);
            distance = td.distance;
        }

        return distance;
    }

    /**
     * return velocity at time 'time' in a Positional trapezoidal trajectory distance where begVel and endVel are zero
     */
    static double velFromTimeAccelDistanceMaxVel(trajectory traj, double time, double accel, double distance, double maxvel) {
        trajFromAccelDistanceMaxVel(traj, accel, distance, maxvel);
        return velFromTrajFromAccelDistanceMaxVel(traj, time);
    }

    static double velFromTrajFromAccelDistanceMaxVel(trajectory traj, double time) {
        double timediff;

        if (time >= traj.totalTime)
            return 0.;

        timediff = time - (traj.rampUp.time + traj.maxVel.time);
        if (timediff >= 0.)
            return (traj.rampDown.time-timediff)/traj.rampDown.time * (traj.rampDown.begVel-traj.rampDown.endVel);

        timediff = time - traj.rampUp.time;
        if (timediff >= 0.)
            return traj.maxVel.begVel;

        return time/traj.rampUp.time * (traj.rampUp.endVel-traj.rampUp.begVel);
    }

    /**
     * takes into account the distance traveled while move in progress: distance parameter is starting separation
     * between Positions or net distance to cover; if finalVel != 0, distance moved by by first position in order to
     * reach target != net distance since target has moved by finalVel*time_of_first_Position_move;
     */
    static void trajFromAccelDistanceMaxVelBegVelEndVel(
    trajectory traj, double accel, double distance, double maxvel, double begVel, double finalVel) {
        double maxVel;
        double maxVelToUse;
        double timeRamping;
        double targetPos;
        double remainDistance;

        maxVel = maxVelFromAccelDistanceBegVelEndVel(accel, distance, begVel, finalVel);
        // point maxvel in same direction as maxVel
        if (maxVel > 0.)
            maxvel = Math.abs(maxvel);
        else
            maxvel = -Math.abs(maxvel);
        // adjust maxVelToUse to not exceed maxvel
        if (maxVel > 0. && maxVel > maxvel || maxVel < 0. && maxVel < maxvel)
            maxVelToUse = maxvel;
        else
            maxVelToUse = maxVel;

        timeDistanceFromAccelBegVelEndVel(td, accel, begVel, maxVelToUse);
        traj.rampUp.begVel = begVel;
        traj.rampUp.endVel = maxVelToUse;
        traj.rampUp.accel = accel;
        traj.rampUp.time = td.time;
        traj.rampUp.distance = td.distance;

        timeDistanceFromAccelBegVelEndVel(td, accel, maxVelToUse, finalVel);
        traj.rampDown.begVel = maxVelToUse;
        traj.rampDown.endVel = finalVel;
        traj.rampDown.accel = -accel;
        traj.rampDown.time = td.time;
        traj.rampDown.distance = td.distance;

        traj.maxVel.begVel = maxVelToUse;
        traj.maxVel.endVel = maxVelToUse;
        traj.maxVel.accel = 0.;

        timeRamping = traj.rampUp.time+traj.rampDown.time;
        // target position moves finalVel*timeRamping distance during timeRamping time
        targetPos = finalVel * timeRamping;
        // remaining distance
        remainDistance = distance - (traj.rampUp.distance + traj.rampDown.distance - targetPos);

        // if remainDistance!=0, then equivalent to maxVel>maxvel where move is a trapezoid,
        // else move is a sawtooth (possibly truncated by reduced max vel)
        traj.maxVel.time = Math.abs(remainDistance/(maxVelToUse-finalVel));
        traj.maxVel.distance = traj.maxVel.time * maxVelToUse;
        if (remainDistance < 0.)
            traj.maxVel.distance = -Math.abs(traj.maxVel.distance);
        traj.totalTime = traj.rampUp.time + traj.rampDown.time + traj.maxVel.time;
        traj.totalDistance = traj.rampUp.distance + traj.rampDown.distance + traj.maxVel.distance;
    }

    /**
     * takes into account the distance traveled while move in progress: distance parameter is starting separation
     * between Positions or net distance to cover; if finalVel != 0, distance moved by by first position in order to
     * reach target != net distance since target has moved by finalVel*time_of_first_Position_move;
     * first trajectory ends with zero final velocity matching position of second trajectory which continues at final velocity
     */
    static void trajFromAccelDistanceMaxVelBegVelEndVelFirstTrajZeroEndVel(
    trajectory traj, double accel, double distance, double maxvel, double begVel, double finalVel) {
        double maxVel;
        double maxVelToUse;
        double timeRamping;
        double targetPos;
        double remainDistance;

        maxVel = maxVelFromAccelDistanceBegVelEndVelFirstTrajZeroEndVel(accel, distance, begVel, finalVel);
        // point maxvel in same direction as maxVel
        if (maxVel > 0.)
            maxvel = Math.abs(maxvel);
        else
            maxvel = -Math.abs(maxvel);
        // adjust maxVelToUse to not exceed maxvel
        if (maxVel > 0. && maxVel > maxvel || maxVel < 0. && maxVel < maxvel)
            maxVelToUse = maxvel;
        else
            maxVelToUse = maxVel;

        timeDistanceFromAccelBegVelEndVel(td, accel, begVel, maxVelToUse);
        traj.rampUp.begVel = begVel;
        traj.rampUp.endVel = maxVelToUse;
        traj.rampUp.accel = accel;
        traj.rampUp.time = td.time;
        traj.rampUp.distance = td.distance;

        timeDistanceFromAccelBegVelEndVel(td, accel, maxVelToUse, 0);
        traj.rampDown.begVel = maxVelToUse;
        traj.rampDown.endVel = 0;
        traj.rampDown.accel = -accel;
        traj.rampDown.time = td.time;
        traj.rampDown.distance = td.distance;

        traj.maxVel.begVel = maxVelToUse;
        traj.maxVel.endVel = maxVelToUse;
        traj.maxVel.accel = 0.;

        timeRamping = traj.rampUp.time+traj.rampDown.time;
        // target position moves finalVel*timeRamping distance during timeRamping time
        targetPos = finalVel * timeRamping;
        // remaining distance
        remainDistance = distance - (traj.rampUp.distance + traj.rampDown.distance - targetPos);

        // if remainDistance!=0, then equivalent to maxVel>maxvel where move is a trapezoid,
        // else move is a sawtooth (possibly truncated by reduced max vel)
        traj.maxVel.time = Math.abs(remainDistance/(maxVelToUse-finalVel));
        traj.maxVel.distance = traj.maxVel.time * maxVelToUse;
        if (remainDistance < 0.)
            traj.maxVel.distance = -Math.abs(traj.maxVel.distance);
        traj.totalTime = traj.rampUp.time + traj.rampDown.time + traj.maxVel.time;
        traj.totalDistance = traj.rampUp.distance + traj.rampDown.distance + traj.maxVel.distance;
    }

    /**
     * does NOT take into account the distance traveled while move in progress;
     * velocity trajectory in form of line of constant slope from begVel to endVel;
     */
    static void trajFromAccelBegVelTargetVel(trajectory traj, double accel, double begVel, double endVel) {
        timeDistanceFromAccelBegVelEndVel(td, accel, begVel, endVel);
        traj.rampUp.begVel = begVel;
        traj.rampUp.accel = signAccel(accel, begVel, endVel);
        traj.rampUp.time = td.time;
        traj.rampUp.endVel = traj.rampUp.time * traj.rampUp.accel + begVel;
        traj.rampUp.distance = td.distance;

        traj.maxVel.time = traj.maxVel.distance = 0.;
        traj.maxVel.begVel = traj.maxVel.endVel = traj.rampUp.endVel;

        traj.rampDown.begVel = traj.rampDown.endVel = traj.rampUp.endVel;
        traj.rampDown.accel = traj.rampDown.time = traj.rampDown.distance = 0.;

        traj.totalTime = traj.rampUp.time;
        traj.totalDistance = traj.rampUp.distance;
    }

    /**
     * does NOT take into account the distance traveled while move in progress;
     * velocity trajectory in form of line of constant slope followed by horizontal line, ie, a deccel situation: \__ ;
     * returns distance at time 'time' in the trajectory
     */
    static double distanceFromTimeAccelBegVelTargetVel(trajectory traj, double time, double accel, double begVel, double endVel) {
        trajFromAccelBegVelTargetVel(traj, accel, begVel, endVel);
        return distanceFromTrajFromTimeAccelBegVelTargetVel(traj, time);
    }

    static double distanceFromTrajFromTimeAccelBegVelTargetVel(trajectory traj, double time) {
        double timediff;

        timediff = time - traj.rampUp.time;
        if (timediff >= 0.)
            return traj.rampUp.distance + timediff * traj.maxVel.begVel;

        timeDistanceFromTimeAccelBegVelEndVel(td, time, traj.rampUp.accel, traj.rampUp.begVel, traj.rampUp.endVel);
        return td.distance;

    }

    /**
     * does NOT take into account the distance traveled while move in progress;
     * velocity trajectory in form of line of constant slope followed by horizontal line, ie, a deccel situation: \__ ;
     * returns velocity at time 'time' in the trajectory
     */
    static double velFromTimeAccelBegVelTargetVel(trajectory traj, double time, double accel, double begVel, double endVel) {
        trajFromAccelBegVelTargetVel(traj, accel, begVel, endVel);
        return VelFromTrajFromTimeAccelBegVelTargetVel(traj, time);
    }

    static double VelFromTrajFromTimeAccelBegVelTargetVel(trajectory traj, double time) {
        if (time - traj.rampUp.time >= 0.)
            return traj.maxVel.begVel;
        return time/traj.rampUp.time * (traj.rampUp.endVel-traj.rampUp.begVel) + traj.rampUp.begVel;
    }

    static void test() {
        boolean quit = false;
        int mainSelect;
        double maxVel;
        double finalVel;
        int ix;
        double dx;
        timeDistance td = new timeDistance();
        trajectory traj = new trajectory();

        while (!quit) {
            do {
                System.out.println("\nTrajectory class tests");
                System.out.println("   select from:");
                Enumeration eTEST_TRAJECTORY = TEST_TRAJECTORY.elements();
                while (eTEST_TRAJECTORY.hasMoreElements()) {
                    TEST_TRAJECTORY tt = (TEST_TRAJECTORY) eTEST_TRAJECTORY.nextElement();
                    System.out.println("      "
                    + (tt.KEY+1)
                    + ". "
                    + tt);
                }
                console.getInt();
                mainSelect = console.i;
            }
            while (mainSelect < 1 || mainSelect > TEST_TRAJECTORY.size());

            if (mainSelect == TEST_TRAJECTORY.timeDistanceFromAccelBegVelEndVel.KEY+1) {
                System.out.println("void timeDistanceFromAccelBegVelEndVel(timeDistance td, double accel, double begVel, double finalVel)");

                System.out.println("void timeDistanceFromAccelBegVelEndVel(td, 2, 3, 6)");
                timeDistanceFromAccelBegVelEndVel(td, 2, 3, 6);
                System.out.println("td.time 1.5 = "
                + td.time
                + " td.distance 6.75 = "
                + td.distance);

                System.out.println("\nvoid timeDistanceFromAccelBegVelEndVel(td, 2, 3, -6)");
                timeDistanceFromAccelBegVelEndVel(td, 2, 3, -6);
                System.out.println("td.time 4.5 = "
                + td.time
                + " td.distance -6.75 = "
                + td.distance);

                System.out.println("\nvoid timeDistanceFromAccelBegVelEndVel(td, 2, -3, -6)");
                timeDistanceFromAccelBegVelEndVel(td, 2, -3, -6);
                System.out.println("td.time 4.5 = "
                + td.time
                + " td.distance -6.75 = "
                + td.distance);

                System.out.println("\nvoid timeDistanceFromAccelBegVelEndVel(td, 2, -3, 6)");
                timeDistanceFromAccelBegVelEndVel(td, 2, -3, 6);
                System.out.println("td.time 4.5 = "
                + td.time
                + " td.distance 6.75 = "
                + td.distance);
            }
            else if (mainSelect == TEST_TRAJECTORY.timeDistanceFromTimeAccelBegVelEndVel.KEY+1) {
                System.out.println("void timeDistanceFromTimeAccelBegVelEndVel(timeDistance td, double time, double accel, double begVel, double finalVel)");

                System.out.println("void timeDistanceFromTimeAccelBegVelEndVel(td, 1.5, 2, 3, 6)");
                timeDistanceFromTimeAccelBegVelEndVel(td, 1.5, 2, 3, 6);
                System.out.println("td.distance 6.75 = " + td.distance);

                System.out.println("void timeDistanceFromTimeAccelBegVelEndVel(td, 2.5, 2, 3, 6)");
                timeDistanceFromTimeAccelBegVelEndVel(td, 2.5, 2, 3, 6);
                System.out.println("td.distance 6.75 = " + td.distance);

                System.out.println("void timeDistanceFromTimeAccelBegVelEndVel(td, .5, 2, 3, 6)");
                timeDistanceFromTimeAccelBegVelEndVel(td, .5, 2, 3, 6);
                System.out.println("td.distance 1.75 = " + td.distance);
            }
            else if (mainSelect == TEST_TRAJECTORY.finalVelFromAccelDistanceTimeDiff.KEY+1) {
                System.out.println("double finalVelFromAccelDistanceTimeDiff(double accel, double distance, double timediff)");

                System.out.println("finalVel = finalVelFromAccelDistanceTimeDiff(2, 50, 10)");
                finalVel = finalVelFromAccelDistanceTimeDiff(2, 50, 10);
                System.out.println("finalVel 15 = " + finalVel);
                System.out.println("avgvel = 50/10=5; 5sec of accel increases vel by 10, so finalVel=5+10=15");

                System.out.println("\nFinalVel = finalVelFromAccelDistanceTimeDiff(-2, -50, 10)");
                finalVel = finalVelFromAccelDistanceTimeDiff(-2, -50, 10);
                System.out.println("finalVel -15 = " + finalVel);
            }
            else if (mainSelect == TEST_TRAJECTORY.triggerTimeDistanceFromAccelBegVelEndVel.KEY+1) {
                System.out.println("distance is separation of Positions at start of move;");
                System.out.println("   NOT distance moved by first position if finalVel != 0");
                System.out.println("void triggerTimeDistanceFromAccelBegVelEndVel(timeDistance td, double accel, double begVel, double finalVel)");

                System.out.println("void triggerTimeDistanceFromAccelBegVelEndVel(td, 2, 6, 3)");
                triggerTimeDistanceFromAccelBegVelEndVel(td, 2, 6, 3);
                System.out.println("td.time 1.5 = "
                + td.time
                + " td.distance 2.25 = "
                + td.distance);
                System.out.println("ie, final position has head start of 2.25 and moves 4.5 while initial position moves 6.75");

                System.out.println("\nvoid triggerTimeDistanceFromAccelBegVelEndVel(td, 2, 3, -6)");
                triggerTimeDistanceFromAccelBegVelEndVel(td, 2, 3, -6);
                System.out.println("td.time 4.5 = "
                + td.time
                + " td.distance 20.25 = "
                + td.distance);
                System.out.print("ie, final position starts at 20.25 and moves -27 while initial position starts at 0 and moves -6.75");
                System.out.print(" (initial position moves forward 2.25 before stopping to reverse direction;");
                System.out.print(" at this point: triggerTimeDistanceFromAccelBegVelEndVel(td, 2, 0, -6)");
                triggerTimeDistanceFromAccelBegVelEndVel(td, 2, 0, -6);
                System.out.println(" separation has closed to 9 = " + td.distance);

                System.out.println("\nvoid triggerTimeDistanceFromAccelBegVelEndVel(td, 2, -6, -3)");
                triggerTimeDistanceFromAccelBegVelEndVel(td, 2, -6, -3);
                System.out.println("td.time 1.5 = "
                + td.time
                + " td.distance -2.25 = "
                + td.distance);

                System.out.println("\nvoid triggerTimeDistanceFromAccelBegVelEndVel(td, 2, -3, 6)");
                triggerTimeDistanceFromAccelBegVelEndVel(td, 2, -3, 6);
                System.out.println("td.time 4.5 = "
                + td.time
                + " td.distance -20.25 = "
                + td.distance);
            }
            else if (mainSelect == TEST_TRAJECTORY.maxVelFromAccel_UnCompDistance_BegVelEndVel.KEY+1) {
                System.out.println("MaxVel = maxVelFromAccel_UnCompDistance_BegVelEndVel(double accel, double distance, double begVel, double finalVel)");

                System.out.println("MaxVel = maxVelFromAccel_UnCompDistance_BegVelEndVel(2, 12, 1, -1)");
                maxVel = maxVelFromAccel_UnCompDistance_BegVelEndVel(2, 12, 1, -1);
                System.out.println("MaxVel 5 = " + maxVel);
                System.out.println("first leg time=(5-1)/2=2, distance=2*(5+1)/2=6");
                System.out.println("last leg time=(5--1)/2=3, distance=3*(5+-1)/2=6");
                System.out.println("total distance=12");

                System.out.println("\nMaxVel = maxVelFromAccel_UnCompDistance_BegVelEndVel(2, -12, -1, 1)");
                maxVel = maxVelFromAccel_UnCompDistance_BegVelEndVel(2, -12, -1, 1);
                System.out.println("MaxVel -5 = " + maxVel);
            }
            else if (mainSelect == TEST_TRAJECTORY.maxVelFromAccelDistanceBegVelEndVel.KEY+1) {
                System.out.println("distance is separation of Positions at start of move;");
                System.out.println("   NOT distance moved by first position if finalVel != 0");
                System.out.println("double maxVelFromAccelDistanceBegVelEndVel(double accel, double distance, double begVel, double finalVel)");

                System.out.println("MaxVel = maxVelFromAccelDistanceBegVelEndVel(2, 17, 1, -1)");
                maxVel = maxVelFromAccelDistanceBegVelEndVel(2, 17, 1, -1);
                System.out.println("MaxVel 5 = " + maxVel);
                System.out.println("first leg time=(5-1)/2=2, distance=2*(5+1)/2=6");
                System.out.println("last leg time=(5--1)/2=3, distance=3*(5+-1)/2=6");
                System.out.println("change in position over total time of 5 = -5;");
                System.out.println("total distance=6(first leg)+6(second leg)--5(distance target moved during time)=17");

                System.out.println("\nMaxVel = maxVelFromAccelDistanceBegVelEndVel(2, 8, 1, 1)");
                maxVel = maxVelFromAccelDistanceBegVelEndVel(2, 8, 1, 1);
                System.out.println("MaxVel 5 = " + maxVel);
                System.out.println("first leg time=(5-1)/2=2, distance=2*(5+1)/2=6");
                System.out.println("last leg time=(5-1)/2=2, distance=2*(5+1)/2=6");
                System.out.println("change in position over total time of 4 = 4;");
                System.out.println("total distance=6(first leg)+6(second leg)-4(distance target moved during time)=8");

                System.out.println("\ndouble maxVelFromAccelDistanceBegVelEndVel(double accel, double distance, double begVel, "
                + "double finalVel)");
                System.out.println("MaxVel = maxVelFromAccelDistanceBegVelEndVel(2, -17, -1, 1)");
                maxVel = maxVelFromAccelDistanceBegVelEndVel(2, -17, -1, 1);
                System.out.println("MaxVel -5 = " + maxVel);

                System.out.println("\nMaxVel = maxVelFromAccelDistanceBegVelEndVel(2, -8, -1, -1)");
                maxVel = maxVelFromAccelDistanceBegVelEndVel(2, -8, -1, -1);
                System.out.println("MaxVel -5 = " + maxVel);
            }
            else if (mainSelect == TEST_TRAJECTORY.maxVelFromAccelDistanceBegVelEndVelFirstTrajZeroEndVel.KEY+1) {
                System.out.println("distance is separation of Positions at start of move;");
                System.out.println("   NOT distance moved by first position if finalVel != 0");
                System.out.println("1st trajectory ends with zero velocity matching 2nd trajectory that continues with finalVel");
                System.out.println("double maxVelFromAccelDistanceBegVelEndVelFirstTrajZeroEndVel(double accel, double distance, "
                + "double begVel, double finalVel)");

                System.out.println("MaxVel = maxVelFromAccelDistanceBegVelEndVelFirstTrajZeroEndVel(2, 16.75, 1, -1)");
                maxVel = maxVelFromAccelDistanceBegVelEndVelFirstTrajZeroEndVel(2, 16.75, 1, -1);
                System.out.println("MaxVel 5 = " + maxVel);
                System.out.println("first leg time=(5-1)/2=2, distance=2*(5+1)/2=6");
                System.out.println("last leg time=(5-0)/2=2.5, distance=2.5*(5+0)/2=6.26");
                System.out.println("change in position over total time of 4.5 = -4.5");
                System.out.println("total distance=6(first leg)+6.25(second leg)--4.5(distance target moved during time)=16.75");

                System.out.println("\ndouble maxVelFromAccelDistanceBegVelEndVelFirstTrajZeroEndVel(double accel, double distance, "
                + "double begVel, double finalVel)");
                System.out.println("MaxVel = maxVelFromAccelDistanceBegVelEndVelFirstTrajZeroEndVel(2, -16.75, -1, 1)");
                maxVel = maxVelFromAccelDistanceBegVelEndVelFirstTrajZeroEndVel(2, -16.75, -1, 1);
                System.out.println("MaxVel -5 = " + maxVel);
            }
            else if (mainSelect == TEST_TRAJECTORY.maxVelFromTime_UnCompDistance_BegVelEndVel.KEY+1) {
                System.out.println("double maxVelFromTime_UnCompDistance_BegVelEndVel(double time, double distance, double begVel, "
                + "double finalVel)");

                System.out.println("MaxVel = maxVelFromTime_UnCompDistance_BegVelEndVel(5, 12, 1, -1)");
                maxVel = maxVelFromTime_UnCompDistance_BegVelEndVel(5, 12, 1, -1);
                System.out.println("MaxVel 5 = " + maxVel);
                System.out.println("first leg time=(5-1)/2=2, distance=2*(5+1)/2=6");
                System.out.println("last leg time=(5--1)/2=3, distance=3*(5+-1)/2=6");

                System.out.println("\nMaxVel = maxVelFromTime_UnCompDistance_BegVelEndVel(5, -12, -1, 1)");
                maxVel = maxVelFromTime_UnCompDistance_BegVelEndVel(5, -12, -1, 1);
                System.out.println("MaxVel -5 = " + maxVel);
            }
            else if (mainSelect == TEST_TRAJECTORY.maxVelFromTimeDistanceBegVelEndVel.KEY+1) {
                System.out.println("distance is separation of Positions at start of move;");
                System.out.println("   NOT distance moved by first position if finalVel != 0");
                System.out.println("double maxVelFromTimeDistanceBegVelEndVel(double time, double distance, double begVel, double finalVel)");

                System.out.println("MaxVel = maxVelFromTimeDistanceBegVelEndVel(5, 17, 1, -1)");
                maxVel = maxVelFromTimeDistanceBegVelEndVel(5, 17, 1, -1);
                System.out.println("MaxVel 5 = " + maxVel);
                System.out.println("first leg time=(5-1)/2=2, distance=2*(5+1)/2=6");
                System.out.println("last leg time=(5--1)/2=3, distance=3*(5+-1)/2=6");
                System.out.println("change in position over total time of 5 = -5;");
                System.out.println("total distance=6(first leg)+6(second leg)--5(distance target moved during time)=17");

                System.out.println("\ndouble maxVelFromTimeDistanceBegVelEndVel(double time, double distance, double begVel, "
                + "double finalVel)");
                System.out.println("MaxVel = maxVelFromTimeDistanceBegVelEndVel(5, -17, -1, 1)");
                maxVel = maxVelFromTimeDistanceBegVelEndVel(5, -17, -1, 1);
                System.out.println("MaxVel -5 = " + maxVel);
            }
            else if (mainSelect == TEST_TRAJECTORY.trajFromTimeDistanceBegVelEndVel.KEY+1) {
                System.out.println("distance is separation of Positions at start of move;");
                System.out.println("   NOT distance moved by first position if finalVel != 0");
                System.out.println("void trajFromTimeDistanceBegVelEndVel(trajectory traj, double time, double distance, double begVel, double finalVel)");

                System.out.println("void trajFromTimeDistanceBegVelEndVel(traj, 5, 17, 1, -1)");
                trajFromTimeDistanceBegVelEndVel(traj, 5, 17, 1, -1);
                System.out.println("maxVel should be 5, rampUp accel should be 2");
                traj.display();
            }
            else if (mainSelect == TEST_TRAJECTORY.trajFromAccelDistanceMaxVel.KEY+1) {
                System.out.println("void trajFromAccelDistanceMaxVel(trajectory traj, double accel, double distance, double maxvel)");

                System.out.println("trajFromAccelDistanceMaxVel(traj, 2, 24, 4)");
                trajFromAccelDistanceMaxVel(traj, 2, 24, 4);
                System.out.println("total distance should be 24");
                traj.display();

                System.out.println("\nTrajFromAccelDistanceMaxVel(traj, 2, 8, 8)");
                trajFromAccelDistanceMaxVel(traj, 2, 8, 8);
                System.out.println("total distance should be 8");
                traj.display();

                System.out.println("\nTrajFromAccelDistanceMaxVel(traj, 2, -8, 8)");
                trajFromAccelDistanceMaxVel(traj, 2, -8, 8);
                System.out.println("total distance should be -8");
                traj.display();
            }
            else if (mainSelect == TEST_TRAJECTORY.distanceFromTimeAccelDistanceMaxVel.KEY+1) {
                System.out.println("double distanceFromTimeAccelDistanceMaxVel(trajectory traj, double time, double accel, "
                + "double distance, double maxvel)");

                trajFromAccelDistanceMaxVel(traj, 2, 24, 4);
                traj.display();
                for (ix = 0; ix < 10; ix++)
                    System.out.println("time of "
                    + ix
                    + ": distance is "
                    + distanceFromTimeAccelDistanceMaxVel(traj, ix, 2, 24, 4));

                trajFromAccelDistanceMaxVel(traj, 2, -24, 4);
                traj.display();
                for (ix = 0; ix < 10; ix++)
                    System.out.println("time of "
                    + ix
                    + ": distance is "
                    + distanceFromTimeAccelDistanceMaxVel(traj, ix, 2, -24, 4));
            }
            else if (mainSelect == TEST_TRAJECTORY.velFromTimeAccelDistanceMaxVel.KEY+1) {
                System.out.println("double velFromTimeAccelDistanceMaxVel(trajectory traj, double time, double accel, "
                + "double distance, double maxvel)");

                trajFromAccelDistanceMaxVel(traj, 2, 24, 4);
                traj.display();
                for (dx = 0.; dx < 9.; dx+=.5)
                    System.out.println("time of "
                    + dx
                    + ": velocity is "
                    + velFromTimeAccelDistanceMaxVel(traj, dx, 2, 24, 4));

                trajFromAccelDistanceMaxVel(traj, 2, -24, 4);
                traj.display();
                for (dx = 0.; dx < 9.; dx+=.5)
                    System.out.println("time of "
                    + dx
                    + ": velocity is "
                    + velFromTimeAccelDistanceMaxVel(traj, dx, 2, -24, 4));
            }
            else if (mainSelect == TEST_TRAJECTORY.trajFromAccelDistanceMaxVelBegVelEndVel.KEY+1) {
                System.out.println("distance is separation of Positions at start of move;");
                System.out.println("   NOT distance moved by first position if finalVel != 0");
                System.out.println("void trajFromAccelDistanceMaxVelBegVelEndVel(trajectory traj, "
                + "double accel, double distance, double maxvel, double begVel, double finalVel)");

                System.out.println("trajFromAccelDistanceMaxVelBegVelEndVel(traj, 2, 17, 5, 1, -1)");
                trajFromAccelDistanceMaxVelBegVelEndVel(traj, 2, 17, 5, 1, -1);
                System.out.println("totalTime should be 5 totalDistance should be 12");
                traj.display();

                System.out.println("trajFromAccelDistanceMaxVelBegVelEndVel(traj, 2, 17, 9999, 1, -1)");
                trajFromAccelDistanceMaxVelBegVelEndVel(traj, 2, 17, 9999, 1, -1);
                System.out.println("totalTime should be 5 totalDistance should be 12");
                traj.display();

                System.out.println("\ntrajFromAccelDistanceMaxVelBegVelEndVel(traj, 2, 29, 5, 1, -1)");
                trajFromAccelDistanceMaxVelBegVelEndVel(traj, 2, 29, 5, 1, -1);
                System.out.println("totalTime should be 7 totalDistance should be 22");
                traj.display();

                System.out.println("\ntrajFromAccelDistanceMaxVelBegVelEndVel(traj, 2, 68, 10, 6, 2)");
                trajFromAccelDistanceMaxVelBegVelEndVel(traj, 2, 68, 10, 6, 2);
                System.out.println("totalTime should be 11 totalDistance should be 90");
                traj.display();

                System.out.println("\ntrajFromAccelDistanceMaxVelBegVelEndVel(traj, 2, 14, 10, 6, 2)");
                trajFromAccelDistanceMaxVelBegVelEndVel(traj, 2, 14, 10, 6, 2);
                System.out.println("totalTime should be 4 totalDistance should be 22");
                traj.display();

                System.out.println("\ntrajFromAccelDistanceMaxVelBegVelEndVel(traj, 2, -17, 5, -1, 1)");
                trajFromAccelDistanceMaxVelBegVelEndVel(traj, 2, -17, 5, -1, 1);
                System.out.println("totalTime should be 5 totalDistance should be -12");
                traj.display();

                System.out.println("\ntrajFromAccelDistanceMaxVelBegVelEndVel(traj, 2, -29, 5, -1, 1)");
                trajFromAccelDistanceMaxVelBegVelEndVel(traj, 2, -29, 5, -1, 1);
                System.out.println("totalTime should be 7 totalDistance should be -22");
                traj.display();

                System.out.println("\ntrajFromAccelDistanceMaxVelBegVelEndVel(traj, 2, -68, 10, -6, -2)");
                trajFromAccelDistanceMaxVelBegVelEndVel(traj, 2, -68, 10, -6, -2);
                System.out.println("totalTime should be 11 totalDistance should be -90");
                traj.display();

                System.out.println("\ntrajFromAccelDistanceMaxVelBegVelEndVel(traj, 2, -14, 10, -6, -2)");
                trajFromAccelDistanceMaxVelBegVelEndVel(traj, 2, -14, 10, -6, -2);
                System.out.println("totalTime should be 4 totalDistance should be -22");
                traj.display();
            }
            else if (mainSelect == TEST_TRAJECTORY.trajFromAccelDistanceMaxVelBegVelEndVelFirstTrajZeroEndVel.KEY+1) {
                System.out.println("distance is separation of Positions at start of move;");
                System.out.println("   NOT distance moved by first position if finalVel != 0");
                System.out.println("void trajFromAccelDistanceMaxVelBegVelEndVelFirstTrajZeroEndVel(trajectory traj, "
                + "double accel, double distance, double maxvel, double begVel, double finalVel)");

                System.out.println("trajFromAccelDistanceMaxVelBegVelEndVelFirstTrajZeroEndVel(traj, 2, 67, 10, 6, 2)");
                trajFromAccelDistanceMaxVelBegVelEndVelFirstTrajZeroEndVel(traj, 2, 67, 10, 6, 2);
                System.out.println("totalTime should be 12 totalDistance should be 91");
                traj.display();

                System.out.println("\ntrajFromAccelDistanceMaxVelBegVelEndVelFirstTrajZeroEndVel(traj, 2, -67, 10, -6, -2)");
                trajFromAccelDistanceMaxVelBegVelEndVelFirstTrajZeroEndVel(traj, 2, -67, 10, -6, -2);
                System.out.println("totalTime should be 12 totalDistance should be -91");
                traj.display();
            }
            else if (mainSelect == TEST_TRAJECTORY.trajFromAccelBegVelTargetVel.KEY+1) {
                System.out.println("void trajFromAccelBegVelTargetVel(trajectory traj, double accel, double begVel, double finalVel)");

                System.out.println("void trajFromAccelBegVelTargetVel(traj, 2, 3, 6)");
                trajFromAccelBegVelTargetVel(traj, 2, 3, 6);
                System.out.println("totalTime should be 1.5, totalDistance should be 6.75");
                traj.display();

                System.out.println("\nvoid trajFromAccelBegVelTargetVel(traj, 2, 3, -6)");
                trajFromAccelBegVelTargetVel(traj, 2, 3, -6);
                System.out.println("totalTime should be 4.5, totalDistance should be -6.75");
                traj.display();
            }
            else if (mainSelect == TEST_TRAJECTORY.distanceFromTimeAccelBegVelTargetVel.KEY+1) {
                System.out.println("double distanceFromTimeAccelBegVelTargetVel(trajectory traj, double time, double accel, "
                + "double begVel, double endVel)");

                System.out.println("\nvoid trajFromAccelBegVelTargetVel(traj, 2, 3, -6)");
                trajFromAccelBegVelTargetVel(traj, 2, 3, -6);
                traj.display();
                for (dx = 0.; dx < 7.; dx+=.5)
                    System.out.println("time of: "
                    + dx
                    + " position is "
                    + distanceFromTimeAccelBegVelTargetVel(traj, dx, 2, 3, -6));
            }
            else if (mainSelect == TEST_TRAJECTORY.velFromTimeAccelBegVelTargetVel.KEY+1) {
                System.out.println("double velFromTimeAccelBegVelTargetVel(trajectory traj, double time, double accel, "
                + "double begVel, double endVel)");

                System.out.println("\nbuild trajectory to compare with using void trajFromAccelBegVelTargetVel(traj, 2, 3, -6)");
                trajFromAccelBegVelTargetVel(traj, 2, 3, -6);
                traj.display();
                System.out.println("\nresults should match");
                for (dx = 0.; dx < 7.; dx+=.5)
                    System.out.println("time of: "
                    + dx
                    + " velocity is "
                    + velFromTimeAccelBegVelTargetVel(traj, dx, 2, 3, -6));
            }
            else if (mainSelect == TEST_TRAJECTORY.quit.KEY+1) {
                quit = true;
            }
        }
    }
}

