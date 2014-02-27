pause 5

;cmd_scope_spiral_search_start

;quit
 
stop   
;trackon
;pause 5
;cmd_scope_handpad_CCW_slow

;slew_off_equat 0 0 0 1 0 0 20
 
;slew_off_altaz 1 1 5

;slew_off_altaz .01 .01 0 5
;slew_off_altaz .01 .01 0 5
;slew_off_altaz .01 .01 0 5
;slew_off_altaz -.01 -.01 0 5
;slew_off_altaz -.01 -.01 0 5
;slew_off_altaz -.01 -.01 0 5

;reset_altaz 40 200

;backlash_off

;pause 5
;trackoff
;trackon                           
;cfg_parm handpadMode handpadModeGuideOn



;reset_equat    20 0 0 30   0 0    10
;slew_off_equat  0 0 0  0  5 0   0 10
;slew_off_equat  0 0 0  0 -5 0   0 10
;pause 1

;pec_on azRa
;cfg_parm handpadMode handpadModeGuideOn

;html_update_freq 2

;reset_equat 20  45  0    30  0 0
;slew_abs_equat 20  45  42    30  42  56    10
;pause 30

;quit
;reset_encoders_to_scope
;reset_scope_to_encoders
;reset_altaz 135 187.49
;slew_abs_equat 23 0 0 45 0 0 
;trackon
;trackoff
;slew_off_equat 1 0 0 10 0 0 
;pause 5
;html_update_freq 2
;cfg_parm autoGEMFlip true
;slew_match_object_name /mel/cot/dat M42
;reset_equat 0 0 0 0 0 0
;slew_match_object_name /mel/cot test
;pause 20
;:Sr 5:12:34.127#
;pause 10
;quit
;trackoff
;slew_off_equat 0 0 0 2 0 0 4
;slew_off_altaz 1 1 5
;slew_off_altaz -1 -1 5
;slew_off_altaz 1 1
;slew_off_altaz -1 -1
;reset_altaz 40 100 
;reset_altaz 45 105 5
;pause 1
;trackon
;pause 5
;slew_off_equat 0 0 0 -1 0 0 5 10

;reset_equat 20  0  0    30  0 0
;reset_equat 20  45  0    30  0 0 5
;backlash_off
;trackon
;slew_abs_equat 20 45 0 30 0 0 
;pause 5
;trackoff 2
;slew_abs_equat 20  45  42    30  42  56    10 15
;slew_abs_equat 20  46  33    30  21  40    10 15
;slew_abs_equat 20  47  46    30   1  39    10 15
;slew_abs_equat 20  49  51    29  45  20    10 15
;slew_abs_equat 20  53  16    29  39  33    10 15
;slew_abs_equat 20  55  12    30  14  30    10 15
;slew_abs_equat 20  56  20    30  53  14    10 15
;slew_abs_equat 20  57  13    31  12  36    10 15
;slew_abs_equat 20  56  42    31  38  14    10 15
;slew_abs_equat 20  55  13    31  55  42    10 15
;slew_abs_equat 20  53  43    32  11  55    10 15
;slew_abs_equat 20  52  44    31  45  39    10 15
;slew_abs_equat 20  51   5    31  57  29    10 15
;slew_abs_equat 20  48  30    31  59  53    10 15
;slew_abs_equat 20  48  41    31  21  43    10 15
;slew_abs_equat 20  48   5    30  54  50    10 15
;slew_abs_equat 20  45  46    31   6  37    10 15
;slew_abs_equat 20  45  43    30  43  30    10 15
;slew_abs_equat 20  45  43    30  43  30    10 15
;prompt 10 finished
;quit