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
SiTech's ASCII control protocol for his controller, which emulates PICServo (note that the ASCII commands will not
work once the PICServo mode is entered):

ASCII COMMAND SET:
List of ASCII commands (all get terminated with an  <E> (13) ).  Be sure to use upper case.
For the Y servo, use 'Y' instead of 'X'  The # symbol means there is a number required.
The X servo is the Altitude or Declination.  The Y servo is the Azimuth or Right Ascension.

X#   Move Servo (-2147483648 to +2147483647)  You can tag a speed command at the end.  Example X-2345S1000000<E>
X    Returns the X position of the servo
XF#  Forces the X position to be equal to the number (-2147483648 to +2147483647) (This stops the controller if moving)
XS#  Velocity of X Servo (0-2147483647)
XR#  Ramping speed or Acceleration of X servo (0-2147483647)
XP#  Proportional Band of X servo (0-32767)
XP   Returns X proportional band
XI#  X Integral (0-32767)
XI   Returns X integral
XD#  X Derivative (0-32767)
XD   Returns X derivative
XE#  Maximum position error limit before servo turns off (0-32767)
XE   Returns the position error of the servo
XO#  X Output limit (0-255)
XO   Returns the PWM output of the servo (0-255)
XC#  X Current Limit (0-240 = 0-2.40 amps)
XC   Returns the X motor current * 100 (240 MAX)
XM#  X to manual mode, the number is the PWM value, 0-255.
XA   X to Auto mode.
XN   X Normal Stop     (ramps down, then stops.  Automatically clears when new position is given)
XNT  stops all motors, then resumes tracking when velocity drops below 524,287
XG   X Emergency stop   (stops immediately, may damage equipment if large inertial load on it)
XL#  Set Integral Limit (0-32767)
XL   Returns Integral Limit
XB   Returns servo bits like specific errors, modes, etc.
XB#  Number sets the servo bits like direction, etc.  (0-255)
XZ#  Forces the scope encoder position to be equal to the number (-2147483648 to +2147483647)
XZ   Returns the scope shaft encoder position
      The rest of the commands don't have a 'Y' command, only X.
      They affect both servo's
XK   Returns the keypad info in Decimal.
XH   Returns the temperature of the cpu chip (in degrees F)
XV   Returns the firmware version * 10.
XJ   Returns the motor power supply voltage * 10 (please divide returned number by 10)
XQ   Resets the servo system (both)
XU   Programs factory defaults into the flash rom.
XW   Writes the configuration of both the x and y parameters to flash rom.
XT   Reads the configuration from the Flash Rom.
UFN  uploads flash now

All of the following XX extended commands have a corresponding read command
as an example, if you type "XXL<E>" it responds with the latitude.

XXL# Store the latitude to the controller (4500 = 45 degrees north, -4500 = 45 degrees south)

XXZ# Store the azimuth encoder ticks per full circle.
XXT# Store the altitude encoder ticks per full circle.

XXU# Store the number of encoder ticks for the Altitude Motor Encoder to the controller
XXV# Store the number of encoder ticks for the Azimuth Motor Encoder to the controller

XXA# Stores the Altitude Slew Rate to the controller
XXB# Stores the Azimuth Slew Rate to the controller
XXC# Stores the Altitude Pan Rate to the controller
XXD# Stores the Azimuth Pan Rate to the controller
XXE# Stores the Platform tracking rate to the controller
XXF# Stores the Platform up/down adjuster to the controller
XXG# Stores the Platform Goal to the controller
XXH# Stores the Altitude Guide Rate to the controller
XXI# Stores the Azimuth Guide Rate to the controller
XXJ# pic servo timeout in seconds

AD#  Program the Address of module.  This can be 1,3 or 5 (No X or Y command)
      (Be sure to only have one module listening!!!)

YV   gets the serial number

0AAh Puts the servo into the PicServo Emulate mode (no CR required)

SC   Gets all parms per the config block
FC<E> followed by 130 bytes (128 bytes data, 2 bytes checksum) Sets all parms per config block

config block:
    // unsigned
    long XRampSpeed;
    long XVelocity;
    int XErrorLimit;
    int XPropBand;
    int XIntegral;
    int XDerivative;
    int XOutLimit;
    int XCurrentLimit;
    int XIntegralLimit;
    int XBits;
    long YRampSpeed;
    long YVelocity;
    int YErrorLimit;
    int YPropBand;
    int YIntegral;
    int YDerivative;
    int YOutLimit;
    int YCurrentLimit;
    int YIntegralLimit;
    int YBits;
    int ModuleAddress;
    //signed
    long PlatformTrackingRate;
    long PlatformUpDownAdjust;
    long PlatformGoal;
    int Latitude;
    long AzimuthEncoderTicksPerRev;
    long AltitudeEncoderTicksPerRev;
    long AzimuthMotorTicksPerRev;
    long AltitudeMotorTicksPerRev;
    long XSlewRate;
    long YSlewRate;
    long XPanRate;
    long YPanRate;
    long XGuideRate;
    long YGuideRate;
    // unsigned
    int PicServoCommTimeout;

Proportional, Integral, Derivative, Output limit, Current Limit, Maximum ServoError,
Ramp speed, Velocity, Integral Limit, Servo Bits and Address are stored from ram to the Flash
Rom when the XW command is received.
All of the extended commands (XXx) are saved too.

The flash ram values are loaded from the flash Rom to Ram on reset.
Both the X and Y parameters are stored to the flash rom.

Use X and Y for module address 1.
If the module address is 3, use 'T' and 'U', for address 5, it's V and W

Description of the bits for the X_BITS and Y_BITS:
0    if 1, the motor encoder is incremented the other direction
1    if 1, the motor polarity is reversed
2    if 1, the azimuth (or altitude) encoder is reversed
3    if 1, (x only) we're in the computerless drag and track mode
3    if 1, (y only) we're in the computerless slew and track mode  (no clutches, must use handpad to slew)
     (must be in drag and track too)
4    if 1, (x only) we're in the tracking platform mode
5    if 1, (x only) we enable the hand paddle
6    if 1, (x only) hand paddle is compatible with New Hand Paddle (allows slewing in two directions, and guiding)
7    if 1, (x only) we're in the guide mode. The pan rate is added or subtracted from
     the current tracking rate.

Here's the definition of the X and Y bits.
* N_BITS  00000000b*
           ||||||||
          |||||||Inverts the servo encoder
          ||||||Inverts the motor direction
          |||||Inverts the scope encoder
          ||||Drag and Track mode(X only) Slew and Track mode (Y only)
          |||Tracking Platform Mode (X only)
          ||Enable the Hand Paddle for slewing/panning (X only)
          |New Hand Paddle Type (allows slew in both alt and az at once (X only)
          Guide Mode (X only)


here's the document for the extended NOP functions.
NOP with length of 0: 0xAA, 0xAddress, 0x0E, Checksum Same as pic servo's NOP instruction

NOP with length of 1: 0xAA, 0xAddress, 0x1E, CommandByte, Checksum
[note: command byte here is actually control byte]
Command byte =  4: Reads the configuration from the flash rom.
Command byte =  8: Writes the configuration from the flash rom.
Command byte = 16: Forces the controller to use (and saves to flash) the factory defaults.
Command byte = 32: Forces the controller back to ascii mode. (lower addressed motor only,
     although it affects both motors)
Don't use any PicServo commands until you're ready to switch back to PicServo Mode.

NOP with length of 2: 0xAA, 0xAddress, 0x2E, 0x02, DataByte, Checksum
This command allows you to write the DataByte into X_BITS or Y_BITS (lower address goes to X_BITS).
See later in this email for definition of these bits.

NOP with length of 5: 0xAA, 0xAddress, 0x5E, 0x01, 4 data bytes (long int), Checksum
This command allows you to set the position of the telescope encoder (lower address is for the altitude)

NOP with length of 5: 0xAA, 0xAddress, 0x5E, 64d, 4 data bytes (long int), Checksum
This command allows you to set the guide add/subtract value. (lower address is for the altitude)

NOP with length of 9: 0xAA, 0xAddress, 0x9E, 32d, 4 data bytes (long int), 4 data bytes (long int), Checksum
This command allows you to set the slew rate and the pan rate.  Slew is the first long int, pan is the second.

More PicServo Differences:
Here's the extra ReadStatus commands

Read Scope Encoder  (this will be Altitude (or DEC) for odd addresses and Azimuth (or RA) for odd addresses.
   We do this whenever the Define Status returns the Home, instead it returns the scope encoder.

Read Motor Current.  This is done with the send A/D value bit in the Define Status.
 It reads motor current * 100. (bit 6 must be clear)

Read Motor Power Supply Voltage instead of A/D.  This is done by Setting Bit 6 of the Define Status.
Reads Volts * 10.  Only the odd address sends it.

Read CPU Chip Temperature instead of A/D.  This is done by Setting Bit 6 of the Define Status.
Reads in Deg's F.  Only the even address sends it.

When bit 7 of define status command is set, it turns off manual slewing.

Instead of sending the ID, the read version portion of the define status
command sends the status of the handpad.
;ID      00000000b
      ;  ||||||||
      ;  |||||||Left button pushed
      ;  ||||||Right button pushed
      ;  |||||Up button pushed
      ;  ||||Down button pushed
      ;  |||Switch status
      ;  ||Top Right button is pushed
      ;  |Top Left button is pushed
      ;  A direction button was pushed, and we may need another tracking
command.

;Definition of the bits for the X_BITS and Y_BITS:
;0    if 1, the motor encoder is incremented the other direction
;1    if 1, the motor polarity is reversed
;2    if 1, the azimuth (or altitude) encoder is reversed
;3    if 1, (x only) we're in the computerless drag and drop mode
;3    if 1, (y only) we're in the computerless slew and track mode  (no
clutches, must use handpad to slew)
       ;(must be in drag and track too) (not done yet)
;4    if 1, (x only) we're in the tracking platform mode
;5    if 1, (x only) we enable the hand paddle
;6    if 1, (x only) hand paddle is compatible with New Hand Paddle (allows
slewing in two directions, and guiding)
;7    if 1, (x only) we're in the guide mode. The pan rate is added or
subtracted from
      ;the current tracking rate.

Heres the definition of the X and Y bits.
;N_BITS  00000000b
      ;  ||||||||
      ;  |||||||Inverts the servo encoder
      ;  ||||||Inverts the motor direction
      ;  |||||Inverts the scope encoder
      ;  ||||Drag and Drop mode (DAD) (X only) Slew and drop mode (Y Only,
Not done yet)
      ;  |||Tracking Platform Mode (X only)
      ;  ||Enable the Hand Paddle for slewing/panning (X only)
      ;  |New Hand Paddle Type (allows slew/panning in both altitude and
azimuth at same time.
      ;  Guide Mode (X only)

disable SiTech controller operation of handpad when in PICServo mode by setting bit 7 in the define status command
for the first motor alt/Dec, that is, add 128 to the define status' control byte;

 * SiTech's controller box and wiring:
 *
 * serial Cygnal ribbon cable faces out towards power input;
 * power input: red to corner edge, black towards RJ48;
 * always make sure baud rate is 19.2k;
 *
 * handpad jumper closest to the max232 chip is 'mel' compatible, otherwise sends ground (pin compatible with encoder input RJ45)
 *         ___________________________________
 *Hand Pad|                                   | Scope Encoders
 *        |                                   |
 *  Serial|                                   | Motor address Even (Azimuth)
 *        |                                   |
 *   Power|                                   | Motor address Odd (Altitude)
 *        |___________________________________|
 *
 *Here's my ascii art to show you how the motor connectors on the board are wired:
 *They are a moduler female connector that a normal CAT5 cable fits in.  In this view,
 *you're looking at the edge of the board, into the female connector, and pin 1 is
 *at the left, pin 8 is at the right.  The plastic clip is at the bottom.
 *
 *      Altitude Motor              Azimuth Motor          Telescope Encoders
 *  __________________________     __________________________     ____________________
 * |                          |   |                          |   |                    |
 * |                          |   |                          |   |                    |
 * |  1  2  3  4  5  6  7  8  |   |  1  2  3  4  5  6  7  8  |   |  1  2  3  4  5  6  |
 * |                          |   |                          |   |                    |
 * |                          |   |                          |   |                    |
 * |                          |   |                          |   |                    |
 * |_________         ________|   |_________        _________|   |______        ______|
 *           |      |                       |      |                    |      |
 *           |      |                       |      |                    |      |
 *            ------                         ------                      ------
 * ____________________________________________________________________________
 *Circuit boad
 *
 *Motor Connectors:
 *Pin 1 is the encoder A signal
 *Pin 2 is the encoder B signal
 *Pin 3 is +5 volts for the encoder
 *pin 4 is Ground for the encoder
 *Pin 5 and 7 is one lead of the motor
 *Pin 6 and 8 is the other lead of the motor
 *
 * Pittman motor wiring to DB9:
 * motor color scheme: red=+vdc, black=ground, green=encoder signal channel a, yellow=encoder signal channel b
 * DB9#s: 1 = n.c. 2,3=motor black 4,5=motor red 6=encoder black 7=encoder red 8=encoder yellow 9=encoder blue/green
 *
 * DB9 to RJ45 adapter for motor:
 *    DB9 to RJ45:
 *    DB9 pinout:      RJ45 female showing DB9 pin#s: (face-on, clip down, cable to rear):
 *    --------------   -------------------
 *    \ 5 4 3 2 1 /    | 9 8 7 6 4 2 5 3 |  (blue,orange,black,red,yellow,white,green,brown)
 *     \ 9 8 7 6 /     -------     -------
 *      --------              |||||
 *
 *The Telescope encoder jack is a 6 pin modular connector, again, looking into it with the clip at the bottom,
 *1 is on the left, 6 is on the right.
 *
 *Pin 1 is Encoder Altitude, phase A
 *Pin 2 is Encoder Altitude, phase B
 *Pin 3 is Encoder Azimuth, phase A
 *Pin 4 is GND
 *Pin 5 is Encoder Azimuth, phase B
 *Pin 6 is +5Volts
 *
 *Here's the other End of Board:
 *
 *  Hand Paddle             Serial Connector
 *  _________________    _______________________
 * |                 |  |                       |     12-24 VDC Power
 * |                 |  |                       |    _________________
 * |  1  2  3  4  6  |  |  1  2  3  4  6  7  8  |   |                 |
 * |                 |  |                       |   |                 |
 * |                 |  |                       |   |  Minus    Plus  |
 * |                 |  |                       |   |    O       O    |
 * |_____        ____|  |_______        ________|   |                 |
 *       |      |               |      |            |                 |
 *       |      |               |      |            |_________________|
 *        ------                 ------
 *_____________________________________________________________________________
 *Board
 *
 *The serial connecter is also an 8 pin modular connector.  For the IBM compatible computer, you can simply
 *connect 1-1, 2-2, 3-3, etc, then skip 9 of the modular connector.  Really only pins 2, 3, and 5 are used.
 *
 *The apple is probably different, so here's the pinout of the female 8 pin modular connector:
 *Pin 2 is XMT data
 *Pin 3 is RCV data
 *Pin 5 is GND
 *
 *All handshaking is ignored.
 *FYI, it runs at 19.2 kbaud, 8 data 1 stop.
 *
 *RS232 serial connection is numbered same as RJ45: 2=2, 3=3, 5=5
 *    --------------   -------------------
 *    \ 5 4 3 2 1 /    | 8 7 6 5 4 3 2 1 | (white,brown,yellow,green,red,black,orange,blue)
 *     \ 9 8 7 6 /     -------     -------
 *      --------              |||||
 *pin 2 xmt = orange
 *pin 3 rec = black
 *pin 5 gnd = green
 * looking into RS232 to RJ45 adapter with clip down in back and leads coming out at you:
 *   x   x   2   x
 * x   5   3   x
 *
 *
 *The Handpad jack is a 6 pin modular connector, again, looking into it with the clip at the bottom,
 *1 is on the left, 6 is on the right.
 *
 *Pin 4 is GND or +5 volts, depending on the jumper setting of the jumber between the serial connector and hand pad connector.
 *If the jumper is toward the edge of the board, it is connected to ground, and it is not "mel bartels handpad compatible".
 *If the jumper is toward the MAX233 chip, pin 4 is connected to +5 volts, and is compatible with Mel Bartels handpad.
 *Pin 6 is +5Volts
 *
 *Here's how Mels handpad works:
 *Pin 1 is connected to the toggle switch, which selects between slew and pan.
 *Pan/slew right pushbutton provides +5 volts to pin 2
 *Pan/slew up pushbutton provides +5 volts to pin 3
 *Pan/slew down pushbutton provides +5 volts to pin 4
 *Pan/slew left pushbutton provides +5 volts to pin 3 and pin 4
 *The bottom left pushbutton provides +5 volts to pin 2 and pin 3
 *The bottom right pushbutton provides +5 volts to pin 2 and pin 4
 *
 *In my opinion, if you use Mel's hand paddle design, you could use pin 6 for both +5 volt leads, and don't use pin 4.
 *This will make it unnecessary to place the jumper between the two connectors.
 */

public final class SITECH_ASCII_CMDS {
    private String id;
    public final int KEY;
    private SITECH_ASCII_CMDS prev;
    private SITECH_ASCII_CMDS next;

    private static int itemCount;
    private static SITECH_ASCII_CMDS first;
    private static SITECH_ASCII_CMDS last;

    private SITECH_ASCII_CMDS(String id) {
        this.id = new String(id);
        this.KEY = itemCount++;
        if (first == null)
            first = this;
        if (last != null) {
            this.prev = last;
            last.next = this;
        }
        last = this;
    }

    public static Enumeration elements() {
        return new Enumeration() {
            private SITECH_ASCII_CMDS current = first;

            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                SITECH_ASCII_CMDS c = current;
                current = current.next();
                return c;
            }
        };
    }

    public String toString() {
        return this.id;
    }

    public static int size() {
        return itemCount;
    }

    public static SITECH_ASCII_CMDS first() {
        return first;
    }

    public static SITECH_ASCII_CMDS last() {
        return last;
    }

    public SITECH_ASCII_CMDS prev() {
        return this.prev;
    }

    public SITECH_ASCII_CMDS next() {
        return this.next;
    }

    public static void display() {
        console.stdOutLn("display of SITECH_ASCII_CMDS, which has "
        + itemCount
        + " elements:");
        SITECH_ASCII_CMDS current = first;
        while (current != null) {
            console.stdOutLn(current.id
            + ": "
            + current.KEY);
            current = current.next;
        }
        console.stdOutLn("end of display");
    }

    public static String returnItemListAsString() {
        String s = "";

        SITECH_ASCII_CMDS current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }

    public static SITECH_ASCII_CMDS matchKey(int i) {
        SITECH_ASCII_CMDS O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }

    public static SITECH_ASCII_CMDS matchStr(String s) {
        SITECH_ASCII_CMDS O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }

    public static final SITECH_ASCII_CMDS getDefaultResponse = new SITECH_ASCII_CMDS("");
    public static final SITECH_ASCII_CMDS setModuleAddress = new SITECH_ASCII_CMDS("AD");
    public static final SITECH_ASCII_CMDS setAll = new SITECH_ASCII_CMDS("FC");
    public static final SITECH_ASCII_CMDS getAll = new SITECH_ASCII_CMDS("SC");
    public static final SITECH_ASCII_CMDS queryEncoders = new SITECH_ASCII_CMDS("Q");
    public static final SITECH_ASCII_CMDS uploadFirmwareFile = new SITECH_ASCII_CMDS("UFN");
    public static final SITECH_ASCII_CMDS getCPUTemperature = new SITECH_ASCII_CMDS("XH");
    public static final SITECH_ASCII_CMDS getVoltage = new SITECH_ASCII_CMDS("XJ");
    public static final SITECH_ASCII_CMDS getKeypad = new SITECH_ASCII_CMDS("XK");
    public static final SITECH_ASCII_CMDS stopAllMotors = new SITECH_ASCII_CMDS("XNT");
    public static final SITECH_ASCII_CMDS resetController = new SITECH_ASCII_CMDS("XQ");
    public static final SITECH_ASCII_CMDS readCfgFromFlashROM = new SITECH_ASCII_CMDS("XT");
    public static final SITECH_ASCII_CMDS programFactoryDefaultsIntoFlashROM = new SITECH_ASCII_CMDS("XU");
    public static final SITECH_ASCII_CMDS getFirmwareVersion = new SITECH_ASCII_CMDS("XV");
    public static final SITECH_ASCII_CMDS writeCfgFromFlashROM = new SITECH_ASCII_CMDS("XW");
    public static final SITECH_ASCII_CMDS getSetPICServoTimeOut = new SITECH_ASCII_CMDS("XXJ");
    public static final SITECH_ASCII_CMDS getSetLatitude = new SITECH_ASCII_CMDS("XXL");
    public static final SITECH_ASCII_CMDS getSerialNum = new SITECH_ASCII_CMDS("YV");

    public static final SITECH_ASCII_CMDS XMove = new SITECH_ASCII_CMDS("X");
    public static final SITECH_ASCII_CMDS YMove = new SITECH_ASCII_CMDS("Y");

    public static final SITECH_ASCII_CMDS getXPosition = new SITECH_ASCII_CMDS("X");
    public static final SITECH_ASCII_CMDS getYPosition = new SITECH_ASCII_CMDS("Y");

    public static final SITECH_ASCII_CMDS setXAutoPWM = new SITECH_ASCII_CMDS("XA");
    public static final SITECH_ASCII_CMDS setYAutoPWM = new SITECH_ASCII_CMDS("YA");
    
    public static final SITECH_ASCII_CMDS getSetXbits = new SITECH_ASCII_CMDS("XB");
    public static final SITECH_ASCII_CMDS getSetYbits = new SITECH_ASCII_CMDS("YB");
    
    public static final SITECH_ASCII_CMDS setXCurrentLimit = new SITECH_ASCII_CMDS("XC");
    public static final SITECH_ASCII_CMDS setYCurrentLimit = new SITECH_ASCII_CMDS("YC");

    public static final SITECH_ASCII_CMDS getXMotorCurrent = new SITECH_ASCII_CMDS("XC");
    public static final SITECH_ASCII_CMDS getYMotorCurrent = new SITECH_ASCII_CMDS("YC");
    
    public static final SITECH_ASCII_CMDS getSetXDerivative = new SITECH_ASCII_CMDS("XD");
    public static final SITECH_ASCII_CMDS getSetYDerivative = new SITECH_ASCII_CMDS("YD");

    public static final SITECH_ASCII_CMDS setXMaxPositionError = new SITECH_ASCII_CMDS("XE");
    public static final SITECH_ASCII_CMDS setYMaxPositionError = new SITECH_ASCII_CMDS("YE");
   
    public static final SITECH_ASCII_CMDS getXPositionError = new SITECH_ASCII_CMDS("XE");
    public static final SITECH_ASCII_CMDS getYPositionError = new SITECH_ASCII_CMDS("YE");
   
    public static final SITECH_ASCII_CMDS setXPosition = new SITECH_ASCII_CMDS("XF");
    public static final SITECH_ASCII_CMDS setYPosition = new SITECH_ASCII_CMDS("YF");
   
    public static final SITECH_ASCII_CMDS XEmergencyStop = new SITECH_ASCII_CMDS("XG");
    public static final SITECH_ASCII_CMDS YEmergencyStop = new SITECH_ASCII_CMDS("YG");

    public static final SITECH_ASCII_CMDS getSetXIntegral = new SITECH_ASCII_CMDS("XI");
    public static final SITECH_ASCII_CMDS getSetYIntegral = new SITECH_ASCII_CMDS("YI");

    public static final SITECH_ASCII_CMDS getSetXIntegralLimit = new SITECH_ASCII_CMDS("XL");
    public static final SITECH_ASCII_CMDS getSetYIntegralLimit = new SITECH_ASCII_CMDS("YL");
    
    public static final SITECH_ASCII_CMDS setXPWM = new SITECH_ASCII_CMDS("XM");
    public static final SITECH_ASCII_CMDS setYPWM = new SITECH_ASCII_CMDS("YM");

    public static final SITECH_ASCII_CMDS XNormalStop = new SITECH_ASCII_CMDS("XN");
    public static final SITECH_ASCII_CMDS YNormalStop = new SITECH_ASCII_CMDS("YN");

    public static final SITECH_ASCII_CMDS getXPWM = new SITECH_ASCII_CMDS("XO");
    public static final SITECH_ASCII_CMDS getYPWM = new SITECH_ASCII_CMDS("YO");
    
    public static final SITECH_ASCII_CMDS setXOutputLimit = new SITECH_ASCII_CMDS("XO");
    public static final SITECH_ASCII_CMDS setYOutputLimit = new SITECH_ASCII_CMDS("YO");

    public static final SITECH_ASCII_CMDS getSetXProportional = new SITECH_ASCII_CMDS("XP");
    public static final SITECH_ASCII_CMDS getSetYProportional = new SITECH_ASCII_CMDS("YP");
    
    public static final SITECH_ASCII_CMDS getSetXRamp = new SITECH_ASCII_CMDS("XR");
    public static final SITECH_ASCII_CMDS getSetYRamp = new SITECH_ASCII_CMDS("YR");
    
    public static final SITECH_ASCII_CMDS getSetXVelocity = new SITECH_ASCII_CMDS("XS");
    public static final SITECH_ASCII_CMDS getSetYVelocity = new SITECH_ASCII_CMDS("YS");
    
    public static final SITECH_ASCII_CMDS getSetXEncoderPosition = new SITECH_ASCII_CMDS("XZ");
    public static final SITECH_ASCII_CMDS getSetYEncoderPosition = new SITECH_ASCII_CMDS("YZ");

    public static final SITECH_ASCII_CMDS getXMaxPositionError = new SITECH_ASCII_CMDS("XEL");
    public static final SITECH_ASCII_CMDS getYMaxPositionError = new SITECH_ASCII_CMDS("YEL");
    
    public static final SITECH_ASCII_CMDS getSetXMotorSlewRate = new SITECH_ASCII_CMDS("XXA");
    public static final SITECH_ASCII_CMDS getSetYMotorSlewRate = new SITECH_ASCII_CMDS("XXB");

    public static final SITECH_ASCII_CMDS getSetXMotorPanRate = new SITECH_ASCII_CMDS("XXC");
    public static final SITECH_ASCII_CMDS getSetYMotorPanRate = new SITECH_ASCII_CMDS("XXD");

    public static final SITECH_ASCII_CMDS getSetPlatformRate = new SITECH_ASCII_CMDS("XXE");
    public static final SITECH_ASCII_CMDS getSetPlatformUpDown = new SITECH_ASCII_CMDS("XXF");
    public static final SITECH_ASCII_CMDS getSetPlatformGoal = new SITECH_ASCII_CMDS("XXG");

    public static final SITECH_ASCII_CMDS getSetXMotorGuideRate = new SITECH_ASCII_CMDS("XXH");
    public static final SITECH_ASCII_CMDS getSetYMotorGuideRate = new SITECH_ASCII_CMDS("XXI");

    public static final SITECH_ASCII_CMDS getSetXEncoderCountsPerRevolution = new SITECH_ASCII_CMDS("XXT");
    public static final SITECH_ASCII_CMDS getSetYEncoderCountsPerRevolution = new SITECH_ASCII_CMDS("XXZ");

    public static final SITECH_ASCII_CMDS getSetXMotorCountsPerRevolution = new SITECH_ASCII_CMDS("XXU");
    public static final SITECH_ASCII_CMDS getSetYMotorCountsPerRevolution = new SITECH_ASCII_CMDS("XXV");
}
