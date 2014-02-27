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

public final class CMD_SCOPE {
    private String id;
    public final String description;
    public final int KEY;
    private CMD_SCOPE prev;
    private CMD_SCOPE next;
    
    private static int itemCount;
    private static CMD_SCOPE first;
    private static CMD_SCOPE last;
    
    private CMD_SCOPE(String id) {
        this(id, "");
    }
    
    private CMD_SCOPE(String id, String description) {
        this.id = new String(id);
        this.description = description;
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
            private CMD_SCOPE current = first;
            
            public boolean hasMoreElements() {
                return current != null;
            }
            public Object nextElement() {
                CMD_SCOPE c = current;
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
    
    public static CMD_SCOPE first() {
        return first;
    }
    
    public static CMD_SCOPE last() {
        return last;
    }
    
    public CMD_SCOPE prev() {
        return this.prev;
    }
    
    public CMD_SCOPE next() {
        return this.next;
    }
    
    public static void display() {
        console.stdOutLn("display of CMD_SCOPE, which has "
                + itemCount
                + " elements:");
        CMD_SCOPE current = first;
        while (current != null) {
            console.stdOutLn(current.id
                    + ": "
                    + current.KEY);
            current = current.next;
        }
        console.stdOutLn("end of display");
    }
    
    public static void displayCmdScope() {
        console.stdOut("display of CMD_SCOPE, which has "
                + itemCount
                + " elements:"
                + "\n\n");
        CMD_SCOPE current = first;
        while (current != null) {
            console.stdOut(current.id
                    + ": "
                    + current.description
                    + "\n\n");
            current = current.next;
        }
        console.stdOutLn("end of display");
    }
    
    public static String returnItemListAsString() {
        String s = "";
        
        CMD_SCOPE current = first;
        while (current != null) {
            s += current.id + "\n";
            current = current.next;
        }
        return s;
    }
    
    public static CMD_SCOPE matchKey(int i) {
        CMD_SCOPE O = first();
        while (O != null && O.KEY != i)
            O = O.next();
        return O;
    }
    
    public static CMD_SCOPE matchStr(String s) {
        CMD_SCOPE O = first();
        while (O != null && !s.equalsIgnoreCase(O.toString()))
            O = O.next();
        return O;
    }
    
    public static CMD_SCOPE matchDesc(String s) {
        CMD_SCOPE O = first();
        while (O != null && !s.equalsIgnoreCase(O.description))
            O = O.next();
        return O;
    }
    
    /**
     * pauseSec is the time in seconds that the cmd list will pause for before executing the next command;
     * the pause time starts counting as soon as the command begins execution, ie, if cmd is 'stop 5', then
     * the motors will be commanded to stop and 5 secs later the next command will execute: if the motors
     * take 3 seconds to ramp down, then there will be a 2 sec pause, if the motors take 8 seconds to ramp
     * down, then there will no pause since commands are allowed to finish before next command is executed;
     * cmd_scope_quit has no pause time since any subsequent commands will not be executed;
     */
    
    // commands that work with lists of commands
    public static final CMD_SCOPE cmd_scope_auto_scroll_on =
            new CMD_SCOPE("cmd_scope_auto_scroll_on", "auto_scroll_on [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_auto_scroll_off =
            new CMD_SCOPE("cmd_scope_auto_scroll_off", "auto_scroll_off [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_reload_scroll =
            new CMD_SCOPE("cmd_scope_reload_scroll", "reload_scroll [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_pause =
            new CMD_SCOPE("cmd_scope_pause", "pause [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_prompt =
            new CMD_SCOPE("cmd_scope_prompt", "prompt [pauseSecs] [promptStringToDisplay]");
    public static final CMD_SCOPE cmd_scope_cancel_all_cmds =
            new CMD_SCOPE("cmd_scope_cancel_all_cmds", "cancel_all_cmds [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_exec_cmd_file =
            new CMD_SCOPE("cmd_scope_exec_cmd_file", "exec_cmd_file [filename] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_scroll_previous_object =
            new CMD_SCOPE("cmd_scope_scroll_previous_object", "grandtour_scroll_object [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_scroll_next_object =
            new CMD_SCOPE("cmd_scope_scroll_next_object", "grandtour_scroll_object [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_grandtour_file =
            new CMD_SCOPE("cmd_scope_grandtour_file", "grandtour_file [filename] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_grandtour_nearest_object =
            new CMD_SCOPE("cmd_scope_grandtour_nearest_object", "grandtour_nearest_object [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_grandtour_previous_object =
            new CMD_SCOPE("cmd_scope_grandtour_previous_object", "grandtour_previous_object [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_grandtour_next_object =
            new CMD_SCOPE("cmd_scope_grandtour_next_object", "grandtour_next_object [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_spiral_search_start =
            new CMD_SCOPE("cmd_scope_spiral_search_start", "spiral_search_start [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_spiral_search_stop =
            new CMD_SCOPE("cmd_scope_spiral_search_stop", "spiral_search_stop [pauseSecs] [comment]");
    // other command protocols
    public static final CMD_SCOPE cmd_scope_LX200 =
            new CMD_SCOPE("cmd_scope_LX200", ":[LX200ProtocolCommand]# [pauseSecs] [comment]");
    // commands that work with config values and actions consequent to changing values
    public static final CMD_SCOPE cmd_scope_cfg_parm =
            new CMD_SCOPE("cmd_scope_cfg_parm", "cfg_parm [parameterName] [parameterValue(s)]");
    // commands that work with the controllers
    public static final CMD_SCOPE cmd_scope_open_motor_controller_ioport =
            new CMD_SCOPE("cmd_scope_open_motor_controller_ioport", "open_motor_controller_ioport [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_start_motor_controllers =
            new CMD_SCOPE("cmd_scope_start_motor_controllers", "start_motor_controllers [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_init_servo_vars =
            new CMD_SCOPE("cmd_scope_init_servo_vars", "init_servo_vars [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_define_status =
            new CMD_SCOPE("cmd_scope_define_status", "define_status [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_encoder_type =
            new CMD_SCOPE("cmd_scope_encoder_type", "encoder_type [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_reset_encoders_type =
            new CMD_SCOPE("cmd_scope_reset_encoders_type", "reset_encoders [pauseSecs] [comment]");
    // commands that work with files
    public static final CMD_SCOPE cmd_scope_quit =
            new CMD_SCOPE("cmd_scope_quit", "quit [comment]");
    public static final CMD_SCOPE cmd_scope_save_cfg =
            new CMD_SCOPE("cmd_scope_save_cfg", "save_cfg [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_write_logfile =
            new CMD_SCOPE("cmd_scope_write_logfile", "write_logfile [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_write_input_comment =
            new CMD_SCOPE("cmd_scope_write_input_comment", "write_input_comment [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_html_update_freq =
            new CMD_SCOPE("cmd_scope_html_update_freq", "html_update_freq [updateFrequencyInSecs] [pauseSecs] [comment]");
    // commands that work with the handpad
    public static final CMD_SCOPE cmd_scope_handpad_left_mode_key =
            new CMD_SCOPE("cmd_scope_handpad_left_mode_key", "handpad_left_mode_key [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_handpad_right_mode_key =
            new CMD_SCOPE("cmd_scope_handpad_right_mode_key", "handpad_right_mode_key [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_handpad_up_fast =
            new CMD_SCOPE("cmd_scope_handpad_up_fast", "handpad_up_fast [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_handpad_up_slow =
            new CMD_SCOPE("cmd_scope_handpad_up_slow", "handpad_up_slow [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_handpad_up_stop =
            new CMD_SCOPE("cmd_scope_handpad_up_stop", "handpad_up_stop [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_handpad_down_fast =
            new CMD_SCOPE("cmd_scope_handpad_down_fast", "handpad_down_fast [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_handpad_down_slow =
            new CMD_SCOPE("cmd_scope_handpad_down_slow", "handpad_down_slow [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_handpad_down_stop =
            new CMD_SCOPE("cmd_scope_handpad_down_stop", "handpad_down_stop [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_handpad_CW_fast =
            new CMD_SCOPE("cmd_scope_handpad_CW_fast", "handpad_CW_fast [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_handpad_CW_slow =
            new CMD_SCOPE("cmd_scope_handpad_CW_slow", "handpad_CW_slow [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_handpad_CW_stop =
            new CMD_SCOPE("cmd_scope_handpad_CW_stop", "handpad_CW_stop [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_handpad_CCW_fast =
            new CMD_SCOPE("cmd_scope_handpad_CCW_fast", "handpad_CCW_fast [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_handpad_CCW_slow =
            new CMD_SCOPE("cmd_scope_handpad_CCW_slow", "handpad_CCW_slow [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_handpad_CCW_stop =
            new CMD_SCOPE("cmd_scope_handpad_CCW_stop", "handpad_CCW_stop [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_handpad_allow_SiTech_control_on =
            new CMD_SCOPE("cmd_scope_handpad_allow_SiTech_control_on", "handpad_allow_SiTech_control_on [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_handpad_allow_SiTech_control_off =
            new CMD_SCOPE("cmd_scope_handpad_allow_SiTech_control_off", "handpad_allow_SiTech_control_off [pauseSecs] [comment]");
    // commands that work with initializations and alignment
    public static final CMD_SCOPE cmd_scope_initState =
            new CMD_SCOPE("cmd_scope_initState", "initState [INIT_STATE] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_init1 =
            new CMD_SCOPE("cmd_scope_init1", "init1 [optEpoch] [RaHr] [RaMin] [RaSec] [decDeg] [DecMin] [DecSec] [pauseSecs] [positionName]");
    public static final CMD_SCOPE cmd_scope_init2 =
            new CMD_SCOPE("cmd_scope_init2", "init2 [optEpoch] [RaHr] [RaMin] [RaSec] [decDeg] [DecMin] [DecSec] [pauseSecs] [positionName]");
    public static final CMD_SCOPE cmd_scope_init3 =
            new CMD_SCOPE("cmd_scope_init3", "init3 [optEpoch] [RaHr] [RaMin] [RaSec] [decDeg] [DecMin] [DecSec] [pauseSecs] [positionName]");
    public static final CMD_SCOPE cmd_scope_init1input =
            new CMD_SCOPE("cmd_scope_init1input", "init1input [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_init2input =
            new CMD_SCOPE("cmd_scope_init2input", "init2input [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_init3input =
            new CMD_SCOPE("cmd_scope_init3input", "init3input [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_autoInit1 =
            new CMD_SCOPE("cmd_scope_autoInit1", "autoInit1 [optEpoch] [RaHr] [RaMin] [RaSec] [decDeg] [DecMin] [DecSec] [pauseSecs] [positionName]");
    public static final CMD_SCOPE cmd_scope_autoInit2 =
            new CMD_SCOPE("cmd_scope_autoInit2", "autoInit2 [optEpoch] [RaHr] [RaMin] [RaSec] [decDeg] [DecMin] [DecSec] [pauseSecs] [positionName]");
    public static final CMD_SCOPE cmd_scope_init_using_closest_inithist_analysis =
            new CMD_SCOPE("cmd_scope_init_using_closest_inithist_analysis", "init_using_closest_inithist_analysis [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_kill_inits =
            new CMD_SCOPE("cmd_scope_kill_inits", "kill_inits [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_alt_offset =
            new CMD_SCOPE("cmd_scope_alt_offset", "alt_offset [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_reinit =
            new CMD_SCOPE("cmd_scope_reinit", "reinit [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_new_z123_reinit =
            new CMD_SCOPE("cmd_scope_new_z123_reinit", "new_z123_reinit [z1Deg] [z2Deg] z3Deg] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_new_z12_from_inEquat_currAltaz =
            new CMD_SCOPE("cmd_scope_new_z12_from_inEquat_currAltaz", "new_z12_from_inEquat_currAltaz [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_new_z123_from_inEquat_currAltaz =
            new CMD_SCOPE("cmd_scope_new_z123_from_inEquat_currAltaz", "new_z123_from_inEquat_currAltaz [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_new_z12_from_analysis_file =
            new CMD_SCOPE("cmd_scope_new_z12_from_analysis_file", "new_z12_from_analysis_file [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_new_z123_from_analysis_file =
            new CMD_SCOPE("cmd_scope_new_z123_from_analysis_file", "new_z123_from_analysis_file [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_analyze =
            new CMD_SCOPE("cmd_scope_analyze", "analyze [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_polarAlign1 =
            new CMD_SCOPE("cmd_scope_polarAlign1", "polarAlign1 [optEpoch] [RaHr] [RaMin] [RaSec] [decDeg] [DecMin] [DecSec] [pauseSecs] [positionName]");
    public static final CMD_SCOPE cmd_scope_polarAlign2 =
            new CMD_SCOPE("cmd_scope_polarAlign2", "polarAlign2 [optEpoch] [RaHr] [RaMin] [RaSec] [decDeg] [DecMin] [DecSec] [pauseSecs] [positionName]");
    public static final CMD_SCOPE cmd_scope_polarAlign3 =
            new CMD_SCOPE("cmd_scope_polarAlign3", "polarAlign3 [optEpoch] [RaHr] [RaMin] [RaSec] [decDeg] [DecMin] [DecSec] [pauseSecs] [positionName]");
    // commands that work with error corrections
    public static final CMD_SCOPE cmd_scope_backlash_on =
            new CMD_SCOPE("cmd_scope_backlash_on", "backlash_on [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_backlash_off =
            new CMD_SCOPE("cmd_scope_backlash_off", "backlash_off [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_pec_on =
            new CMD_SCOPE("cmd_scope_pec_on", "pec_on [servoID] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_pec_off =
            new CMD_SCOPE("cmd_scope_pec_off", "pec_off [servoID] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_pec_synch =
            new CMD_SCOPE("cmd_scope_pec_synch", "pec_synch [servoID] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_guide_on =
            new CMD_SCOPE("cmd_scope_guide_on", "guide_on [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_guide_off =
            new CMD_SCOPE("cmd_scope_guide_off", "guide_off [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_save_pec =
            new CMD_SCOPE("cmd_scope_save_pec", "save_pec [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_guide_save_pec_start =
            new CMD_SCOPE("cmd_scope_guide_save_pec_start", "guide_save_pec_start [servoID] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_guide_save_pec_end =
            new CMD_SCOPE("cmd_scope_guide_save_pec_end", "guide_save_pec_end [servoID] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_build_guide_analysis_files =
            new CMD_SCOPE("cmd_scope_build_guide_analysis_files", "build_guide_analysis_files [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_create_pec_from_guide_analysis =
            new CMD_SCOPE("cmd_scope_create_pec_from_guide_analysis", "create_pec_from_guide_analysis [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_create_pmc_from_analysis =
            new CMD_SCOPE("cmd_scope_create_pmc_from_analysis", "create_pmc_from_analysis [pauseSecs] [comment]");
    // commands that work with coordinates
    public static final CMD_SCOPE cmd_scope_reset_abs_equat =
            new CMD_SCOPE("cmd_scope_reset_abs_equat", "reset_abs_equat [optEpoch] [RaHr] [RaMin] [RaSec] [decDeg] [DecMin] [DecSec] [pauseSecs] [positionName]");
    public static final CMD_SCOPE cmd_scope_reset_off_equat =
            new CMD_SCOPE("cmd_scope_reset_off_equat", "reset_off_equat [RaHr] [RaMin] [RaSec] [decDeg] [DecMin] [DecSec] [pauseSecs] [positionName]");
    public static final CMD_SCOPE cmd_scope_set_abs_input_equat =
            new CMD_SCOPE("cmd_scope_set_abs_input_equat", "set_abs_input_equat [optEpoch] [RaHr] [RaMin] [RaSec] [decDeg] [DecMin] [DecSec] [pauseSecs] [positionName]");
    public static final CMD_SCOPE cmd_scope_set_off_input_equat =
            new CMD_SCOPE("cmd_scope_set_abs_input_equat", "set_off_input_equat [RaHr] [RaMin] [RaSec] [decDeg] [DecMin] [DecSec] [pauseSecs] [positionName]");
    public static final CMD_SCOPE cmd_scope_set_abs_input_altaz =
            new CMD_SCOPE("cmd_scope_set_abs_input_altaz", "set_abs_input_altaz [AltitudeInDecimalDegrees] [AzimuthInDecimalDegrees] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_set_off_input_altaz =
            new CMD_SCOPE("cmd_scope_set_off_input_altaz", "set_off_input_altaz [AltitudeInDecimalDegrees] [AzimuthInDecimalDegrees] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_reset_abs_altaz =
            new CMD_SCOPE("cmd_scope_reset_abs_altaz", "reset_abs_altaz [AltitudeInDecimalDegrees] [AzimuthInDecimalDegrees] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_reset_off_altaz =
            new CMD_SCOPE("cmd_scope_reset_off_altaz", "reset_off_altaz [AltitudeInDecimalDegrees] [AzimuthInDecimalDegrees] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_reset_FR =
            new CMD_SCOPE("cmd_scope_reset_FR", "cmd_scope_reset_FR [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_reset_meridian_flip =
            new CMD_SCOPE("cmd_scope_reset_meridian_flip", "reset_meridian_flip [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_reset_home =
            new CMD_SCOPE("cmd_scope_reset_home", "reset_home [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_reset_scope_to_encoders =
            new CMD_SCOPE("cmd_scope_reset_scope_to_encoders", "reset_scope_to_encoders [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_reset_encoders_to_scope =
            new CMD_SCOPE("cmd_scope_reset_encoders_to_scope", "reset_encoders_to_scope [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_save1 =
            new CMD_SCOPE("cmd_scope_save1", "save1 [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_restore1 =
            new CMD_SCOPE("cmd_scope_restore1", "restore1 [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_drift_equat =
            new CMD_SCOPE("cmd_scope_drift_equat", "drift_equat [RaHr] [RaMin] [RaSec] [decDeg] [DecMin] [DecSec] [timeForDriftInSeconds] [positionName]");
    public static final CMD_SCOPE cmd_scope_drift_altaz =
            new CMD_SCOPE("cmd_scope_drift_altaz", "drift_altaz [DriftAltitudeInDecimalDegrees] [DriftAzimuthInDecimalDegrees] [timeForDriftInSeconds] [comment]");
    // commands that track and slew
    public static final CMD_SCOPE cmd_scope_trackon =
            new CMD_SCOPE("cmd_scope_trackon", "trackon [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_trackoff =
            new CMD_SCOPE("cmd_scope_trackoff", "trackoff [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_stop =
            new CMD_SCOPE("cmd_scope_stop", "stop [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_motors_on =
            new CMD_SCOPE("cmd_scope_motors_on", "motors_on [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_motors_off =
            new CMD_SCOPE("cmd_scope_motors_off", "motors_off [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_fast_speed =
            new CMD_SCOPE("cmd_scope_fast_speed", "fast_speed [speedInDegPerSec] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_slow_speed =
            new CMD_SCOPE("cmd_scope_slow_speed", "slow_speed  [speedInDegPerSec] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_slew_abs_altaz =
            new CMD_SCOPE("cmd_scope_slew_abs_altaz", "slew_abs_altaz [AltitudeInDecimalDegrees] [AzimuthInDecimalDegrees] [timeForMoveInSecs] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_slew_off_altaz =
            new CMD_SCOPE("cmd_scope_slew_off_altaz", "slew_off_altaz [AltitudeInDecimalDegrees] [AzimuthInDecimalDegrees] [timeForMoveInSecs] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_slew_abs_equat =
            new CMD_SCOPE("cmd_scope_slew_abs_equat", "slew_abs_equat [optEpoch] [RaHr] [RaMin] [RaSec] [decDeg] [DecMin] [DecSec] [timeForMoveInSecs] [pauseSecs] [positionName]");
    public static final CMD_SCOPE cmd_scope_slew_off_equat =
            new CMD_SCOPE("cmd_scope_slew_off_equat", "slew_off_equat [RaHr] [RaMin] [RaSec] [decDeg] [DecMin] [DecSec] [timeForMoveInSecs] [pauseSecs] [positionName]");
    public static final CMD_SCOPE cmd_scope_slew_off_equat_arcmin =
            new CMD_SCOPE("cmd_scope_slew_off_equat_arcmin", "slew_off_equat_arcmin [RaArcmin] [DecArcmin] [timeForMoveInSecs] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_slew_meridian_flip =
            new CMD_SCOPE("cmd_scope_slew_meridian_flip", "slew_meridian_flip [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_slew_home =
            new CMD_SCOPE("cmd_scope_slew_home", "slew_home [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_slew_nearest_object =
            new CMD_SCOPE("cmd_scope_slew_nearest_object", "slew_nearest_object [filename] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_slew_nearest_object_avoid_input =
            new CMD_SCOPE("cmd_scope_slew_nearest_object_avoid_input", "slew_nearest_object_avoid_input [filename] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_slew_nearest_object_not_in_input_file =
            new CMD_SCOPE("cmd_scope_slew_nearest_object_not_in_input_file", "slew_nearest_object_not_in_input_file [filename] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_slew_match_object_name =
            new CMD_SCOPE("cmd_scope_slew_match_object_name", "slew_match_object_name [dir] [object] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_slew_match_comet_name =
            new CMD_SCOPE("cmd_scope_slew_match_comet_name", "slew_match_comet_name [dir] [object] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_read_equat_slew =
            new CMD_SCOPE("cmd_scope_read_equat_slew", "read_equat_slew [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_write_equat_slew =
            new CMD_SCOPE("cmd_scope_write_equat_slew", "write_equat_slew [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_write_altaz_slew =
            new CMD_SCOPE("cmd_scope_write_altaz_slew", "write_altaz_slew [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_Project_Pluto_Guide =
            new CMD_SCOPE("cmd_scope_Project_Pluto_Guide", "Project_Pluto_Guide [pauseSecs] [comment]");
    // focusing commands
    public static final CMD_SCOPE cmd_scope_focus_fast_speed =
            new CMD_SCOPE("cmd_scope_focus_fast_speed", "focus_fast_speed [speedInStepsPerSec] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_focus_slow_speed =
            new CMD_SCOPE("cmd_scope_slow_speed", "focus_slow_speed [speedInStepsPerSec] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_reset_focus =
            new CMD_SCOPE("cmd_scope_reset_focus", "reset_focus [focusPosition] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_focus_abs =
            new CMD_SCOPE("cmd_scope_focus_abs", "focus_abs [focusPosition] [timeForMoveInSecs] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_focus_rel =
            new CMD_SCOPE("cmd_scope_focus_rel", "focus_rel [focusPosition] [timeForMoveInSecs] [pauseSecs] [comment]");
    public static final CMD_SCOPE cmd_scope_focus_eyepiece =
            new CMD_SCOPE("cmd_scope_focus_eyepiece", "focus_eyepiece [eyepieceName] [timeForMoveInSecs] [pauseSecs] [comment]");
}

