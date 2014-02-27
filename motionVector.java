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
 * a motion vector and methods of manipulation
 *
 *
 * **********
 *
 * analysis of motion and trajectories with the goal of exploring the issues and deriving formulae to smoothly
 * move from one trajectory to another
 *
 * x=distance,
 * x1=accel distance,
 * x2=deccel distance,
 * z=total of accel+decel distances
 * a=acceleration,
 * b=beginning velocity,
 * f=final velocity,
 * m=maximum velocity,
 * v=average velocity,
 * t=time
 *
 * **********
 *
 * solving for m, assuming constant acceleration, shortest time for move, with no limit on maximum velocity---
 *
 * given z=vt   v=(b+f)/2   f=b+at
 * (sub for v) z=t(b+f)/2, (sub for f) z=t(b+b+at)/2, z=bt+at^2/2
 * using z=bt+at^2/2, (sub for t where t=(f-b)/a) x1=b(f-b)/a+a(f-b)^2/2a^2
 * z1 goes from b to m velocities: b(m-b)/a+(m-b)^2/2a, z1=(2b(m-b)+(m-b)^2)/2a
 * z2 goes from m to f velocities and has -a: z2=m(f-m)/-a+-a(f-m)^2/(2*-a^2), z2=(-2m(f-m)-(f-m)^2)/2a
 * z=z1+z2: z=(2b(m-b)+(m-b)^2)/2a + (-2m(f-m)-(f-m)^2)/2a,
 * 2az=2bm-2b^2+m^2-2mb+b^2 + -2mf+2m^2-f^2+2fm-m^2
 * 2az= -b^2 +2m^2 -f^2 +0bm +0bf +0mf
 * 2az=2m^2-b^2-f^2
 * solving for m: 2m^2=2az+b^2+f^2, m^2=az+b^2/2+f^2/2,
 * >>> m=+-sqr(az+b^2/2+f^2/2), where az should always be positive
 *
 * m always positive number, regardless of beg vel or final vel or accel, ie:
 * example: a=2, b=1, f=-1, m=5; a(ccel) portion: t=2, avg vel=3, z1=6; d(eccel) portion: t=3, avg vel=2, z2=6; z=12
 *         sqrt(2*12+1/2+1/2)=5;
 *          here because z1 and z2 are same, this shows that b and f can be + or -
 *
 * rearranging to solve for a---
 * >>> a=(2m^2-b^2-f^2)/2z
 *
 * taking into account the distance traveled while the move is in progress (constant final velocity,
 * no limit on max velocity)---
 *
 * t = t1 + t2 = (m-b)/a + (m-f)/a, t = (2m-b-f)/a; rearranging, m=(at+b+f)/2
 * recalling that m^2=az+b^2/2+f^2/2
 * noting that z increases by ft (final velocity * time)
 * m^2=a(z+ft)+b^2/2+f^2/2
 *
 * since time is unknown---
 * sub for t: m^2=a(z+f(2m-b-f)/a)+b^2/2+f^2/2
 * solve for m: m^2=az+2mf-bf-f^2+b^2/2+f^2/2, m^2-2mf=az-bf+b^2/2+f^2/2-f^2,
 * (m-f)^2=az-bf+b^2/2+f^2/2-f^2+f^2,
 * >>> m=+-sqrt(az-bf+b^2/2+f^2/2)+f
 *
 * example: a=2, b=1, f=-1, z=17 (net distance traveled or separation between two Positions)
 *          m=+-sqrt(34+1+1/2+1/2)-1=5
 *          first leg time=(5-1)/2=2, distance=2*(5+1)/2=6
 *          last leg time=(5--1)/2=3, distance=3*(5+-1)/2=6
 *          total distance=6(first leg)+6(second leg)--5(distance target moved during time)=17
 *          and
 *          a=2, b=1, f=1, z=8 (net distance traveled or separation between two Positions)
 *          m=+-sqrt(16-1+1/2+1/2)+1=5
 *          first leg time=(5-1)/2=2, distance=2*(5+1)/2=6
 *          last leg time=(5-1)/2=2, distance=2*(5+1)/2=6
 *          total distance=6(first leg)+6(second leg)-4(distance target moved during time)=8
 *
 * >>> and solving for a: a=((m-f)^2+bf-b^2/2-f^2/2)/z
 *
 * if max vel was capped---
 * scope will take longer than expected to reach position thus scope will move further thanks to this longer
 * period of time, so the scope should spend more time at max velocity by the amount:
 * b=begVel, t=time, z=initial distance, f=finalVel; bt=z+ft,
 * >>> t=z/(b-f)
 * b=begVel, t=time, z=final distance, f=finalVel; z=bt,
 * >>> t=z/b
 *
 * ex: if initial separation is 6, maxvel = 5, finalVel = 2: increase in time = 6/(5-2) = 2
 *     during time of 2, maxvel moves 10 and finalVel moves 4 + the original 6 = 10
 *     and
 *     if final separation is 10, maxvel = 5, finalVel = 2: increase in time = 10/5 = 2
 *
 * if t = time while max vel capped---
 * rearranging 2az=2m^2-b^2-f^2 to solve for z and adding distance while max vel capped:
 * z=(2m^2-b^2-f^2)/2a + mt,
 * >>> m=(sqrt(4az+2b^2+2f^2+a^2t^2)-at)/2
 *
 * **********
 *
 * solving for m, assuming constant acceleration, shortest time for move, no limit on maximum velocity
 * and first position/trajectory ends with zero velocity when coinciding or overlapping with second
 * position/trajectory that continues to move at a final velocity ---
 *
 * from above---
 * m=+-sqr(az+b^2/2+f^2/2), where az should always be positive
 * setting f=0
 * >>> m=+-sqr(az-b^2/2), where az should always be positive
 * example: a=2, b=1, z=12.25 (1st leg=6, 2nd leg=6.25):m=5
 * rearranging to solve for a---
 * >>> a=(2m^2-b^2)/2z
 *
 * taking into account the distance traveled while the move is in progress (constant final velocity,
 * no limit on max velocity)---
 *
 * t = t1 + t2 = (m-b)/a + m/a, t = (2m-b)/a; rearranging, m=(at+b)/2
 * recalling that m^2=az+b^2/2
 * noting that z increases by ft (final velocity * time)
 * m^2=a(z+ft)+b^2/2
 * example: a=2, b=1, f=-1, t=4.5, z=16.75 (1st leg=6, 2nd leg=6.25, z increase=4.5): m^2=2(16.75-4.5)+.5, m=5
 *
 * since time is unknown---
 * sub for t: m^2=a(z+f(2m-b)/a)+b^2/2
 * solve for m: m^2=az+2mf-bf+b^2/2, m^2-2mf=az-bf+b^2/2,
 * (m-f)^2=az-bf+b^2/2+f^2,
 * >>> m=+-sqrt(az-bf+b^2/2+f^2)+f
 * example: a=2, b=1, f=-1, z=16.75 (net distance traveled or separation between two Positions)
 *          m=+-sqrt(33.5+1+1/2+1)-1=5
 *          first leg time=(5-1)/2=2, distance=2*(5+1)/2=6
 *          last leg time=5/2=2.5, distance=2.5*2.5=6.25
 *          total distance=6(first leg)+6.25(second leg)--4.5(distance target moved during time)=16.75
 *
 * **********
 *
 * if time, distance known (maxvel, accel not known):
 * equations from above: x=bt+at^2/2, a=2x/t^2-2b/t and a=(f-b)/t
 *
 * break move into two parts so as to start with beg vel, reach a middle vel, and end with a final vel---
 *
 * assume constant accel over both parts---
 * (does NOT take into account the distance traveled while the move on)
 * using constant accel, therefore ratio of time for first part to time of last part:
 * t1/t2 = (m-b)/(m-f) and t2/t1=(m-f)/(m-b)
 * t=t1+t2, t1=t-t2, t1=t-t1(m-f)/(m-b), t1=((m-b)t-(m-f)t1)/(m-b), t1(m-b)+t1(m-f)=(m-b)t, t1=t(m-b)/(2m-f-b)
 * and t2=t-t1, t2=t-t2(m-b)/(m-f), t2=((m-f)t-(m-b)t2)/(m-f), t2(m-f)+t2(m-b)=(m-f)t, t2=t(m-f)/(2m-f-b)
 * z=z1+z1, z=(m+b)/2*t1+(m+f)/2*t2
 * sub for t1 and t2: z=t(m+b)(m-b)/2(2m-f-b) + t(m+f)(m-f)/2(2m-f-b),
 * z=t(2m^2-b^2-f^2)/(4m-2f-2b)
 * solving for m:
 * 2z(2m-f-b)=t(2m^2-b^2-f^2), 2tm^2-4mz=tb^2+tf^2-2zb-2zf, m^2-m(4z/2t)=b^2/2+f^2/2-(b+f)(z/t),
 * (m-z/t)^2=b^2/2+f^2/2-(b+f)(z/t)+(z/t)^2,
 * >>> m=+-sqrt(b^2/2+f^2/2-(b+f)(z/t)+(z/t)^2) + z/t
 *
 * example:b=2, f=4, m=6, a=1: t1=4, t2=2; z=4(2+6)/2 + 2(6+4)/2=26
 * m=sqrt(4/2 + 16/2 - 6*26/6 + (26/6)^2) + 26/6 = sqrt(10 - 26 + 18.78) + 4.33 = 1.67 + 4.33 = 6
 * example:b=1, f=-1, m=5, a=2: t1=2, t2=3;z=2(1+5)/2 + 3(5+-1)/2=12
 * m=sqrt(1/2 + 1/2 - 0 + (12/5)^2) + 12/5 = sqrt(1 - 0 + 5.76) + 2.4 = 2.6 + 2.4 = 5
 *
 * taking into account the distance traveled while the move is in progress---
 * noting that z increases by ft (final velocity * time)
 * net z = z+ft
 * >>> m=+-sqrt(b^2/2+f^2/2-(b+f)((z+ft)/t)+((z+ft)/t)^2) + (z+ft)/t
 *
 * computing acceleration from above:
 * >>> a = (m-b + m-f)/t = (2m-b-f)/t
 * example continuing last example from above: (2*5-1--1)/5 = 2, which agrees with above
 *
 * example:b=1, f=-1, m=5, a=2: t1=2, t2=3;z=2(1+5)/2 + 3(5+-1)/2 - -5 =17
 * m=sqrt(1/2 + 1/2 - 0 + ((17+-5)/5)^2) + (17+-5)/5 = sqrt(1 - 0 + 5.76) + 2.4 = 2.6 + 2.4 = 5
 *
 * if continuous acceleration between begVel and finalVel (z=t(b+f)/2), then maxvel will be max of begVel or finalVel
 * (does NOT take into account the distance traveled while the move on)
 * example:b=2, f=4, m=3 (pick a point on a line between begVel and finalVel), a=1: t1=1, t2=2; z=6
 * m=sqrt(2+8-6*3+9)+3=4 (which is max of b and f)
 *
 * if m < min of b or f, then change sqrt() value to a minus value
 * example:b=2, f=4, m=1, a=1: t1=1, t2=3;z=1.5+7.5=9
 * m=-sqrt(2+8-13.5+5.06)+2.25=-1.25+2.25=1
 *
 * assuming different accel over both parts---
 * (does NOT take into account the distance traveled while the move on)
 * (if excessive maxvel from attempt to move at constant accel over both parts, then different accel
 * over both parts may be sucessful)
 *
 * t1=(m-b)/a1; t2=(m-f)/a2; t1/t2=(m-b)/(m-f)*a2/a1:
 * t1=(m-b)/(m-f)*a2/a1*t2; t2=(m-f)/(m-b)*a2/a1*t1
 * t=t1+t2:
 * sub for t1:
 * t2=t-(m-b)/(m-f)*a2/a1*t2, t2+(m-b)/(m-f)*a2/a1*t2=t, (t2(m-f)a1+t2(m-b)a2)/((m-f)a1)=t, t2=(m-f)a1t/((m-f)a1+(m-b)a2)
 * sub for t2:
 * t1=t-(m-f)/(m-b)*a1/a2*t1, t1+(m-f)/(m-b)*a1/a2*t1=t, (t1(m-b)a2+t1(m-f)a1)/((m-b)a2)=t, t1=(m-b)a2t/((m-b)a2+(m-f)a1)
 * z+z1+z2; z=t1(m+b)/2 + t2(m+f)/2
 * sub for t1 and t2:
 * z=(m+b)/2 * (m-b)a2t/((m-b)a2+(m-f)a1) + (m+f)/2 * (m-f)a1t/((m-f)a1+(m-b)a2),
 * z=((m+b)(m-b)a2t+(m+f)(m-f)a1t)/(2((m-b)a2+(m-f)a1)),
 * 2z((m-b)a2+(m-f)a1))=(m+b)(m-b)a2t+(m+f)(m-f)a1t,
 * 2z(m-b)a2+2z(m-f)a1=(m^2-b^2)a2t+(m^2-f^2)a1t,
 * m^2ta2+m^2ta1-m2za2-m2za1=b^2a2t+f^2a1t-2zba2-2zfa1,
 * m^2t(a2+a1)-m2z(a2+a1)=b^2a2t+f^2a1t-2zba2-2zfa1,
 * m^2-m2z/t=(b^2a2t+f^2a1t-2zba2-2zfa1)/(t(a2+a1)),
 * m^2-m2z/t=(b^2a2+f^2a1)/(a2+a1) - (ba2+fa1)/(a2+a1)*2z/t,
 * (m-z/t)^2=(b^2a2+f^2a1)/(a2+a1) - (ba2+fa1)/(a2+a1)*2z/t + (z/t)^2,
 * >>> m=+-sqrt((b^2a2+f^2a1)/(a2+a1) - (ba2+fa1)/(a2+a1)*2z/t + (z/t)^2) + z/t
 *
 * let r be the ratio of the accels: r=a1/a2; a1=ra2; a2=a1/r:
 * >>> m=+-sqrt((b^2+f^2r)/(r+1) - (b+fr)/(r+1)*2z/t + (z/t)^2) + z/t
 *
 * notice if a2 and a1 set equal, this resolves to solving for m above: m=+-sqrt(b^2/2+f^2/2-(b+f)(z/t)+(z/t)^2) + z/t
 *
 * rearranging for a1/a2 if m is known:
 * a1/a2=(-tm^2+2zm+tb^2-2zb)/(tm^2-2zm-tf^2+2zf),
 * >>> a1/a2=(t(b^2-m^2)+2z(m-b))/(t(m^2-f^2)+2z(f-m))
 *
 * m located on line from m1 at start of move (a1 to infinity) to m2 at end of move (a2 to infinity):
 * >>> m1=2(z/t)-f and m2=2(z/t)-b; this line parallels the line from b to f
 *
 * example: b=3, f=6, t=9, z=58.5: if a1=a2=1, then m=9: m located 2/3 of way on line from m1=7 to m2=10;
 * if m=8, then r=5; if m=7.5, then r=15
 *
 * **********
 *
 * given accel, beg vel, final vel, calc distance needed to deccel from beg vel to final vel:
 * (from above) t=(f-b)/a; z=bt+at^2/2
 * ex: b=6, f=3, a=-.5: t=6 and z=27
 * ex: from above but changing f to -3: t=18 and z=27
 *
 * but during time to deccel, z changed by final vel * time; taking this into account:
 * z=bt+at^2/2+ft
 * ex: from above, z changed by 18 during deccel so z=45 (z=27+18=45)
 * ex: from above but changing f to -3: z changed by -54 during deccel so z=-27 (z=27-54=-27)
 *
 * delay deccel distance until d=bt+at^2/2-ft
 * ex: from above ex: b=6, f=3, a=-.5: t=6 and z=27; d=27-18=9; so when position difference closes to 9,
 * then deccel should commence, with the deccel taking a distance of 27 while the target position drifts
 * by 18 due to the final vel;
 * Positions synchronized at end of move which is 18 beyond initial target position, 27 beyond start of
 * deccel position, and 6 secs later
 * ex: from above but changing f to -3: d=27--54=81; so when position difference closes to 81, then deccel
 * should commence taking a distance of 27 while the target position drifts by -54 due to the final vel;
 * Positions synchronized at end of move which is -54 beyond initial target position, 27 beyond start of
 * deccel position, and 18 secs later
 *
 * can calculate time to start deccel:
 * t=d/b
 * ex: from above, t=1.5 sec: deccel should start 1.5 sec before target position would otherwise be
 * reached by running at beg vel, with move ending 6 seconds later (4.5 sec after target position would
 * otherwise have been reached by running at beg vel)
 * ex: from above but changing f to -3: deccel starts 13.5 sec before target position would otherwise be
 * reached by running at beg vel, with move ending 18 seconds later (4.5 sec after target position would
 * otherwise have been reached by running at beg vel)
 *
 * **********
 *
 * average velocity Vavg given initial time t1, ending time t2, initial position z1, final position z2:
 * given z=vt and v=z/t
 * Vavg = (z1-z2)/(t2-t1)
 *
 * final velocity given above 4 parameters plus accel a:
 * given f=b+at
 * f = Vavg+a(t2-t1)/2,
 * >>> f = (z1-z2)/(t2-t1) + a(t2-t1)/2
 *
 * **********
 */
public class motionVector {
    double time;
    double distance;
    double accel;
    double begVel;
    double endVel;
    String motionVectorDisplayString;

    String buildMotionVectorDisplayString() {
        motionVectorDisplayString = "time="
        + time
        + " distance="
        + distance
        + " accel="
        + accel
        + " begVel="
        + begVel
        + " endVel="
        + endVel;
        return motionVectorDisplayString;
    }

    void display() {
        console.stdOutLn(buildMotionVectorDisplayString());
    }
}

