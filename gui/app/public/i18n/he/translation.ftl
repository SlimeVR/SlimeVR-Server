# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = מתחבר לשרת
websocket-connection_lost = החיבור לשרת אבד. מנסה להתחבר מחדש

## Update notification

version_update-close = סגור

## Tips

tips-find_tracker = לא בטוח איזה חיישן אתה מחזיק? נער את החיישן והתוכנה תסמן לך אותו.
tips-do_not_move_heels = אנא וודא שהעקבים שלך לא זזות בזמן הקלטה

## Units


## Dropdown


## Text input


## File input


## Window controls

titlebar-close = סגור

## Body parts

body_part-NONE = לא שויך
body_part-HEAD = ראש
body_part-NECK = צוואר
body_part-RIGHT_SHOULDER = כתף ימין
body_part-RIGHT_UPPER_ARM = זרוע עליונה ימנית
body_part-RIGHT_LOWER_ARM = זרוע תחתונה ימנית
body_part-RIGHT_HAND = יד ימין
body_part-RIGHT_UPPER_LEG = ירך ימין
body_part-RIGHT_LOWER_LEG = קרסול ימין
body_part-RIGHT_FOOT = רגל ימין
body_part-HIP = ירך
body_part-LEFT_SHOULDER = כתף שמאל
body_part-LEFT_UPPER_ARM = זרוע עליונה שמאלית
body_part-LEFT_LOWER_ARM = זרוע תחתונה שמאלית
body_part-LEFT_HAND = יד שמאל
body_part-LEFT_UPPER_LEG = ירך שמאל
body_part-LEFT_LOWER_LEG = קרסול שמאל
body_part-LEFT_FOOT = רגל שמאל

## BoardType


## Proportions

skeleton_bone-NONE = לא נבחר
skeleton_bone-NECK = אורך צוואר
skeleton_bone-HIP = אורך הירך
skeleton_bone-HIPS_WIDTH = רוחב הירכיים
skeleton_bone-UPPER_LEG = אורך הרגל העליונה
skeleton_bone-LOWER_LEG = אורך רגל תחתון
skeleton_bone-FOOT_LENGTH = אורך כף הרגל
skeleton_bone-SHOULDERS_DISTANCE = מרחק כתפיים
skeleton_bone-SHOULDERS_WIDTH = רוחב כתפיים
skeleton_bone-UPPER_ARM = אורך זרוע עליונה
skeleton_bone-LOWER_ARM = אורך זרוע תחתונה

## Tracker reset buttons

reset-reset_all = איפוס כל הפרופורציות
reset-reset_all_warning-cancel = ביטול
reset-full = איפוס
reset-mounting = איפוס הרכבה

## Navigation bar

navbar-home = בית
navbar-body_proportions = פרופורציות גוף
navbar-trackers_assign = שיוך חיישנים
navbar-mounting = כיול ההרכבה
navbar-onboarding = אשף ההגדרה
navbar-settings = הגדרות

## Biovision hierarchy recording

bvh-start_recording = הקלטת BVH
bvh-recording = מקליט...

## Tracking pause


## Widget: Developer settings

widget-developer_mode = מצב מפתח
widget-developer_mode-high_contrast = ניגודיות גבוהה
widget-developer_mode-fast_data_feed = פיד נתונים מהיר

## Widget: IMU Visualizer

widget-imu_visualizer = סיבוב
widget-imu_visualizer-rotation_preview = תצוגה מקדימה

## Tracker status

tracker-status-none = אין סטטוס
tracker-status-busy = עסוק
tracker-status-error = שגיאה
tracker-status-disconnected = מנותק
tracker-status-occluded = מוסתר
tracker-status-ok = אוקיי

## Tracker status columns

tracker-table-column-name = שם
tracker-table-column-type = סוג
tracker-table-column-battery = סוללה
tracker-table-column-ping = פינג
tracker-table-column-temperature = טמפ' °C
tracker-table-column-linear-acceleration = תאוצה X/Y/Z
tracker-table-column-rotation = סיבוב X/Y/Z
tracker-table-column-position = מיקום X/Y/Z
tracker-table-column-url = כתובת URL

## Tracker rotation

tracker-rotation-front = קדימה
tracker-rotation-left = שמאל
tracker-rotation-right = ימין
tracker-rotation-back = אחורה

## Tracker information

tracker-infos-manufacturer = יצרן
tracker-infos-display_name = שם תצוגה
tracker-infos-custom_name = שם מותאם אישית

## Tracker settings

tracker-settings-assignment_section = שיוך
tracker-settings-assignment_section-description = לאיזה חלק בגוף החיישן משויך.
tracker-settings-assignment_section-edit = ערוך שיוך
tracker-settings-mounting_section = מיקום הרכבה
tracker-settings-mounting_section-edit = עריכת הרכבה
tracker-settings-name_section-description = תן לו כינוי חמוד :)
tracker-settings-name_section-placeholder = רגל שמאלית של NightyBeast

## Dongle settings

dongle-status-disconnected = מנותק

## Tracker part card info

tracker-part_card-unassigned = לא הוקצה

## Body assignment menu


## Tracker assignment menu

tracker_selection_menu-neck_warning-done = אני מבין את הסיכונים
tracker_selection_menu-neck_warning-cancel = ביטול

## Mounting menu

mounting_selection_menu-close = סגור

## Sidebar settings

settings-sidebar-title = הגדרות
settings-sidebar-general = כללי
settings-sidebar-interface = ממשק
settings-sidebar-utils = כלי שירות
settings-sidebar-serial = טרמינל סידרתי

## Bone routing settings

settings-routing-hands-warning-cancel = ביטול

## SteamVR / Monado output settings


## Tracker mechanics

settings-general-tracker_mechanics-filtering = סינון
settings-general-tracker_mechanics-filtering-type = סוג סינון
settings-general-tracker_mechanics-filtering-type-none = ללא סינון
settings-general-tracker_mechanics-filtering-type-none-description = השתמש בערכי סיבוב כפי שהם. לא יעשה שום סינון.
settings-general-tracker_mechanics-filtering-type-smoothing = החלקה
settings-general-tracker_mechanics-filtering-type-smoothing-description = מחליק את התנועות אך מוסיף השהיה מסויימת.
settings-general-tracker_mechanics-filtering-type-prediction = חיזוי
settings-general-tracker_mechanics-filtering-type-prediction-description = מפחית את ההשהיה ומפיק תנועות הדוקות , אך עלול להגביר את הריצוד.
settings-general-tracker_mechanics-filtering-amount = כמות
settings-stay_aligned-general-label = כללי
settings-stay_aligned-relaxed_poses-close = סגור

## Keybinds Page

settings-keybinds_full-reset = איפוס
settings-keybinds-recorder-modal-cancel-button = ביטול

## FK/Tracking settings

settings-general-fk_settings-arm_fk-back = אחורה

## Gesture control settings (tracker tapping)


## Appearance settings

settings-general-interface-dev_mode = מצב מפתח
settings-general-interface-dev_mode-description = This mode can be useful if you need in-depth data or need to interact with connected trackers on a more advanced level.
settings-general-interface-dev_mode-label = מצב מפתח

## Notification settings


## Behavior settings

settings-general-interface-dev_mode = מצב מפתח
settings-general-interface-dev_mode-label = מצב מפתח

## Serial settings

settings-serial = טרמינל סידרתי
settings-serial-factory_reset-warning-cancel = ביטול
settings-serial-send_command-warning-cancel = ביטול

## OSC VRChat settings


## VRChat OSC status

settings-osc-vrchat-status-tracking = סיבוב
settings-osc-vrchat-status-badge-error = שגיאה

## VMC OSC settings

settings-osc-vmc-status-badge-error = שגיאה

## Common OSC settings


## Advanced settings

settings-utils-advanced-reset_warning-cancel = ביטול

## Home Screen


## Tracking Checklist


## Setup/onboarding menu


## Quiz

onboarding-quiz_back = אחורה

## Wi-Fi setup

onboarding-wifi_creds-submit = שלח!

## Install info

install-info_udev-rules_modal_button = סגור

## Setup start


## Tracker connection setup

onboarding-connect_tracker-close = סגור

## Tracker assignment setup

onboarding-assign_trackers-tap_modal-cancel = ביטול
onboarding-assign_trackers-side-right = ימין
onboarding-assign_trackers-side-left = שמאל

## Tracker assignment warnings


## Tracker mounting method choose


## Tracker manual mounting setup


## Tracker automatic mounting setup

onboarding-automatic_mounting-title = כיול ההרכבה
onboarding-automatic_mounting-mounting_reset-title = כיול ההרכבה

## Tracker manual proportions setupa


## Tracker automatic proportions setup

onboarding-automatic_proportions-recording-processing = מעבד את התוצאה
onboarding-automatic_proportions-verify_results-title = אמת את התוצאות
onboarding-automatic_proportions-verify_results-description = אנא בדוק את התוצאות, האם התוצאות נראות נכון?
onboarding-automatic_proportions-verify_results-results = תוצאות הקלטה
onboarding-automatic_proportions-verify_results-processing = מעבד את התוצאה
onboarding-automatic_proportions-verify_results-redo = הקלט מחדש
onboarding-automatic_proportions-verify_results-confirm = הם נכונים
onboarding-automatic_proportions-done-title = הגוף שלך נמדד ונשמר
onboarding-automatic_proportions-done-description = תהליך כיול פרופורציות הגוף שלך הושלם!

## User height calibration


## Stay Aligned setup

onboarding-stay_aligned-verify_mounting-title = כיול ההרכבה

## Home

home-settings-close = סגור

## Trackers Still On notification


## Firmware tool globals

firmware_tool-loading = מתחבר לשרת

## Firmware tool Steps


## firmware tool build status


## Firmware update status


## Dedicated Firmware Update Page


## Tray Menu


## First exit modal

tray_or_exit_modal-cancel = ביטול

## Unknown device modal


## Error collection consent modal


## Tracking checklist section

tracking_checklist-settings-close = סגור
toolbar-mounting_calibration = כיול ההרכבה
toolbar-mounting_calibration-feet = רגל
