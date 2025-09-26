# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means

## Websocket (server) status
websocket-connecting = Loading...
websocket-connection_lost = The server crashed!
websocket-connection_lost-desc = It looks like the SlimeVR server crashed. Check the logs and restart the program.
websocket-timedout = Could not connect to the server
websocket-timedout-desc = It looks like the SlimeVR server crashed or timed out. Check the logs and restart the program.
websocket-error-close = Exit SlimeVR
websocket-error-logs = Open the logs Folder

## Update notification
version_update-title = New version available: { $version }
version_update-description = Clicking "{ version_update-update }" will download the SlimeVR installer for you.
version_update-update = Update
version_update-close = Close

## Tips
tips-find_tracker = Not sure which tracker is which? Shake a tracker and it will highlight the corresponding item.
tips-do_not_move_heels = Ensure your heels do not move during recording!
tips-file_select = Drag & drop files to use, or <u>browse</u>.
tips-tap_setup = You can slowly tap your tracker 2 times to choose it instead of selecting it from the menu.
tips-turn_on_tracker = Using official SlimeVR trackers? Don't forget to <b><em>turn on your tracker</em></b> after connecting it to the PC!
tips-failed_webgl = Failed to initialize WebGL.

## Body parts
body_part-NONE = Unassigned
body_part-HEAD = Head
body_part-NECK = Neck
body_part-RIGHT_SHOULDER = Right shoulder
body_part-RIGHT_UPPER_ARM = Right upper arm
body_part-RIGHT_LOWER_ARM = Right lower arm
body_part-RIGHT_HAND = Right hand
body_part-RIGHT_UPPER_LEG = Right thigh
body_part-RIGHT_LOWER_LEG = Right ankle
body_part-RIGHT_FOOT = Right foot
body_part-UPPER_CHEST = Upper chest
body_part-CHEST = Chest
body_part-WAIST = Waist
body_part-HIP = Hip
body_part-LEFT_SHOULDER = Left shoulder
body_part-LEFT_UPPER_ARM = Left upper arm
body_part-LEFT_LOWER_ARM = Left lower arm
body_part-LEFT_HAND = Left hand
body_part-LEFT_UPPER_LEG = Left thigh
body_part-LEFT_LOWER_LEG = Left ankle
body_part-LEFT_FOOT = Left foot
body_part-LEFT_THUMB_METACARPAL = Left thumb metacarpal
body_part-LEFT_THUMB_PROXIMAL = Left thumb proximal
body_part-LEFT_THUMB_DISTAL = Left thumb distal
body_part-LEFT_INDEX_PROXIMAL = Left index proximal
body_part-LEFT_INDEX_INTERMEDIATE = Left index intermediate
body_part-LEFT_INDEX_DISTAL = Left index distal
body_part-LEFT_MIDDLE_PROXIMAL = Left middle proximal
body_part-LEFT_MIDDLE_INTERMEDIATE = Left middle intermediate
body_part-LEFT_MIDDLE_DISTAL = Left middle distal
body_part-LEFT_RING_PROXIMAL = Left ring proximal
body_part-LEFT_RING_INTERMEDIATE = Left ring intermediate
body_part-LEFT_RING_DISTAL = Left ring distal
body_part-LEFT_LITTLE_PROXIMAL = Left little proximal
body_part-LEFT_LITTLE_INTERMEDIATE = Left little intermediate
body_part-LEFT_LITTLE_DISTAL = Left little distal
body_part-RIGHT_THUMB_METACARPAL = Right thumb metacarpal
body_part-RIGHT_THUMB_PROXIMAL = Right thumb proximal
body_part-RIGHT_THUMB_DISTAL = Right thumb distal
body_part-RIGHT_INDEX_PROXIMAL = Right index proximal
body_part-RIGHT_INDEX_INTERMEDIATE = Right index intermediate
body_part-RIGHT_INDEX_DISTAL = Right index distal
body_part-RIGHT_MIDDLE_PROXIMAL = Right middle proximal
body_part-RIGHT_MIDDLE_INTERMEDIATE = Right middle intermediate
body_part-RIGHT_MIDDLE_DISTAL = Right middle distal
body_part-RIGHT_RING_PROXIMAL = Right ring proximal
body_part-RIGHT_RING_INTERMEDIATE = Right ring intermediate
body_part-RIGHT_RING_DISTAL = Right ring distal
body_part-RIGHT_LITTLE_PROXIMAL = Right little proximal
body_part-RIGHT_LITTLE_INTERMEDIATE = Right little intermediate
body_part-RIGHT_LITTLE_DISTAL = Right little distal

## BoardType
board_type-UNKNOWN = Unknown
board_type-NODEMCU = NodeMCU
board_type-CUSTOM = Custom Board
board_type-WROOM32 = WROOM32
board_type-WEMOSD1MINI = Wemos D1 Mini
board_type-TTGO_TBASE = TTGO T-Base
board_type-ESP01 = ESP-01
board_type-SLIMEVR = SlimeVR
board_type-LOLIN_C3_MINI = Lolin C3 Mini
board_type-BEETLE32C3 = Beetle ESP32-C3
board_type-ESP32C3DEVKITM1 = Espressif ESP32-C3 DevKitM-1
board_type-OWOTRACK = owoTrack
board_type-WRANGLER = Wrangler Joycons
board_type-MOCOPI = Sony Mocopi
board_type-WEMOSWROOM02 = Wemos Wroom-02 D1 Mini
board_type-XIAO_ESP32C3 = Seeed Studio XIAO ESP32C3
board_type-HARITORA = Haritora
board_type-ESP32C6DEVKITC1 = Espressif ESP32-C6 DevKitC-1
board_type-GLOVE_IMU_SLIMEVR_DEV = SlimeVR Dev IMU Glove

## Proportions
skeleton_bone-NONE = None
skeleton_bone-HEAD = Head Shift
skeleton_bone-HEAD-desc =
    This is the distance from your headset to the middle of your head.
    To adjust it, shake your head left to right as if you're disagreeing and modify
    it until any movement in other trackers is negligible.
skeleton_bone-NECK = Neck Length
skeleton_bone-NECK-desc =
    This is the distance from the middle of your head to the base of your neck.
    To adjust it, move your head up and down as if you're nodding or tilt your head
    to the left and right and modify it until any movement in other trackers is negligible.
skeleton_bone-torso_group = Torso length
skeleton_bone-torso_group-desc =
    This is the distance from the base of your neck to your hips.
    To adjust it, modify it standing up straight until your virtual hips line
    up with your real ones.
skeleton_bone-UPPER_CHEST = Upper Chest Length
skeleton_bone-UPPER_CHEST-desc =
    This is the distance from the base of your neck to the middle of your chest.
    To adjust it, adjust your Torso Length properly and modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches with your real one.
skeleton_bone-CHEST_OFFSET = Chest Offset
skeleton_bone-CHEST_OFFSET-desc =
    This can be adjusted to move your virtual chest tracker up or down in order to aid
    with calibration in certain games or applications that may expect it to be higher or lower.
skeleton_bone-CHEST = Chest Length
skeleton_bone-CHEST-desc =
    This is the distance from the middle of your chest to the middle of your spine.
    To adjust it, adjust your Torso Length properly and modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches with your real one.
skeleton_bone-WAIST = Waist Length
skeleton_bone-WAIST-desc =
    This is the distance from the middle of your spine to your belly button.
    To adjust it, adjust your Torso Length properly and modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches with your real one.
skeleton_bone-HIP = Hip Length
skeleton_bone-HIP-desc =
    This is the distance from your belly button to your hips.
    To adjust it, set your Torso Length properly and modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches your real one.
skeleton_bone-HIP_OFFSET = Hip Offset
skeleton_bone-HIP_OFFSET-desc =
    This can be adjusted to move your virtual hip tracker up or down in order to aid
    with calibration in certain games or applications that may expect it to be on your waist.
skeleton_bone-HIPS_WIDTH = Hips Width
skeleton_bone-HIPS_WIDTH-desc =
    This is the distance between the start of your legs.
    To adjust it, perform a full reset with your legs straight and modify it until
    your virtual legs match up with your real ones horizontally.
skeleton_bone-leg_group = Leg length
skeleton_bone-leg_group-desc =
    This is the distance from your hips to your feet.
    To adjust it, adjust your Torso Length properly and modify it
    until your virtual feet are at the same level as your real ones.
skeleton_bone-UPPER_LEG = Upper Leg Length
skeleton_bone-UPPER_LEG-desc =
    This is the distance from your hips to your knees.
    To adjust it, adjust your Leg Length properly and modify it
    until your virtual knees are at the same level as your real ones.
skeleton_bone-LOWER_LEG = Lower Leg Length
skeleton_bone-LOWER_LEG-desc =
    This is the distance from your knees to your ankles.
    To adjust it, adjust your Leg Length properly and modify it
    until your virtual knees are at the same level as your real ones.
skeleton_bone-FOOT_LENGTH = Foot Length
skeleton_bone-FOOT_LENGTH-desc =
    This is the distance from your ankles to your toes.
    To adjust it, tiptoe and modify it until your virtual feet stay in place.
skeleton_bone-FOOT_SHIFT = Foot Shift
skeleton_bone-FOOT_SHIFT-desc =
    This value is the horizontal distance from your knee to your ankle.
    It accounts for your lower legs going backwards when standing up straight.
    To adjust it, set Foot Length to 0, perform a full reset and modify it until your virtual
    feet line up with the middle of your ankles.
skeleton_bone-SKELETON_OFFSET = Skeleton Offset
skeleton_bone-SKELETON_OFFSET-desc =
    This can be adjusted to offset all your trackers forward or backward.
    It can be used to help with calibration in certain games or applications
    that may expect your trackers to be more forward.
skeleton_bone-SHOULDERS_DISTANCE = Shoulders Distance
skeleton_bone-SHOULDERS_DISTANCE-desc =
    This is the vertical distance from the base of your neck to your shoulders.
    To adjust it, set Upper Arm Length to 0 and modify it until your virtual elbow trackers
    line up vertically with your real shoulders.
skeleton_bone-SHOULDERS_WIDTH = Shoulders Width
skeleton_bone-SHOULDERS_WIDTH-desc =
    This is the horizontal distance from the base of your neck to your shoulders.
    To adjust it, set Upper Arm Length to 0 and modify it until your virtual elbow trackers
    line up horizontally with your real shoulders.
skeleton_bone-arm_group = Arm length
skeleton_bone-arm_group-desc =
    This is the distance from your shoulders to your wrists.
    To adjust it, adjust Shoulders Distance properly, set Hand Distance Y
    to 0 and modify it until your hand trackers line up with your wrists.
skeleton_bone-UPPER_ARM = Upper Arm Length
skeleton_bone-UPPER_ARM-desc =
    This is the distance from your shoulders to your elbows.
    To adjust it, adjust Arm Length properly and modify it until
    your elbow trackers line up with your real elbows.
skeleton_bone-LOWER_ARM = Lower Arm Length
skeleton_bone-LOWER_ARM-desc =
    This is the distance from your elbows to your wrists.
    To adjust it, adjust Arm Length properly and modify it until
    your elbow trackers line up with your real elbows.
skeleton_bone-HAND_Y = Hand Distance Y
skeleton_bone-HAND_Y-desc =
    This is the vertical distance from your wrists to the middle of your hand.
    To adjust it for motion capture, adjust Arm Length properly and modify it until your
    hand trackers line up vertically with the middle of your hands.
    To adjust it for elbow tracking from your controllers, set Arm Length to 0 and
    modify it until your elbow trackers line up vertically with your wrists.
skeleton_bone-HAND_Z = Hand Distance Z
skeleton_bone-HAND_Z-desc =
    This is the horizontal distance from your wrists to the middle of your hand.
    To adjust it for motion capture, set it to 0.
    To adjust it for elbow tracking from your controllers, set Arm Length to 0 and
    modify it until your elbow trackers line up horizontally with your wrists.
skeleton_bone-ELBOW_OFFSET = Elbow Offset
skeleton_bone-ELBOW_OFFSET-desc =
    This can be adjusted to move your virtual elbow trackers up or down in order to aid
    with VRChat accidentally binding an elbow tracker to the chest.

## Tracker reset buttons
reset-reset_all = Reset all proportions
reset-reset_all_warning-v2 =
    <b>Warning:</b> Your proportions will be reset to defaults scaled to your configured height.
    Are you sure you want to do this?
reset-reset_all_warning-reset = Reset proportions
reset-reset_all_warning-cancel = Cancel
reset-reset_all_warning_default-v2 =
    <b>Warning:</b> Your height has not been configured, your proportions will be reset to defaults with the default height.
    Are you sure you want to do this?

reset-full = Full Reset
reset-mounting = Reset Mounting
reset-mounting-feet = Reset Feet Mounting
reset-mounting-fingers = Reset Fingers Mounting
reset-yaw = Yaw Reset

## Serial detection stuff
serial_detection-new_device-p0 = New serial device detected!
serial_detection-new_device-p1 = Enter your Wi-Fi credentials!
serial_detection-new_device-p2 = Please select what you want to do with it
serial_detection-open_wifi = Connect to Wi-Fi
serial_detection-open_serial = Open Serial Console
serial_detection-submit = Submit!
serial_detection-close = Close

## Navigation bar
navbar-home = Home
navbar-body_proportions = Body Proportions
navbar-trackers_assign = Tracker Assignment
navbar-mounting = Mounting Calibration
navbar-onboarding = Setup Wizard
navbar-settings = Settings

## Biovision hierarchy recording
bvh-start_recording = Record BVH
bvh-recording = Recording...
bvh-save_title = Save BVH recording

## Tracking pause
tracking-unpaused = Pause tracking
tracking-paused = Unpause tracking

## Widget: Overlay settings
widget-overlay = Overlay
widget-overlay-is_visible_label = Show Overlay in SteamVR
widget-overlay-is_mirrored_label = Display Overlay as Mirror

## Widget: Drift compensation
widget-drift_compensation-clear = Clear drift compensation

## Widget: Clear Reset Mounting
widget-clear_mounting = Clear Reset Mounting

## Widget: Developer settings
widget-developer_mode = Developer Mode
widget-developer_mode-high_contrast = High contrast
widget-developer_mode-precise_rotation = Precise rotation
widget-developer_mode-fast_data_feed = Fast data feed
widget-developer_mode-filter_slimes_and_hmd = Filter Slimes and HMD
widget-developer_mode-sort_by_name = Sort by name
widget-developer_mode-raw_slime_rotation = Raw rotation
widget-developer_mode-more_info = More info

## Widget: IMU Visualizer
widget-imu_visualizer = Tracking data
widget-imu_visualizer-preview = Preview
widget-imu_visualizer-hide = Hide
widget-imu_visualizer-rotation_raw = Raw rotation
widget-imu_visualizer-rotation_preview = Preview rotation
widget-imu_visualizer-acceleration = Acceleration
widget-imu_visualizer-position = Position
widget-imu_visualizer-stay_aligned = Stay Aligned

## Widget: Skeleton Visualizer
widget-skeleton_visualizer-preview = Skeleton preview
widget-skeleton_visualizer-hide = Hide

## Tracker status
tracker-status-none = No Status
tracker-status-busy = Busy
tracker-status-error = Error
tracker-status-disconnected = Disconnected
tracker-status-occluded = Occluded
tracker-status-ok = OK
tracker-status-timed_out = Timed out

## Tracker status columns
tracker-table-column-name = Name
tracker-table-column-type = Type
tracker-table-column-battery = Battery
tracker-table-column-ping = Ping
tracker-table-column-tps = TPS
tracker-table-column-temperature = Temp. °C
tracker-table-column-linear-acceleration = Accel. X/Y/Z
tracker-table-column-rotation = Rotation X/Y/Z
tracker-table-column-position = Position X/Y/Z
tracker-table-column-stay_aligned = Stay Aligned
tracker-table-column-url = URL

## Tracker rotation
tracker-rotation-front = Front
tracker-rotation-front_left = Front-Left
tracker-rotation-front_right = Front-Right
tracker-rotation-left = Left
tracker-rotation-right = Right
tracker-rotation-back = Back
tracker-rotation-back_left = Back-Left
tracker-rotation-back_right = Back-Right
tracker-rotation-custom = Custom
tracker-rotation-overriden = (overridden by mounting reset)

## Tracker information
tracker-infos-manufacturer = Manufacturer
tracker-infos-display_name = Display Name
tracker-infos-custom_name = Custom Name
tracker-infos-url = Tracker URL
tracker-infos-version = Firmware Version
tracker-infos-hardware_rev = Hardware Revision
tracker-infos-hardware_identifier = Hardware ID
tracker-infos-data_support = Data support
tracker-infos-imu = IMU Sensor
tracker-infos-board_type = Main board
tracker-infos-network_version = Protocol Version
tracker-infos-magnetometer = Magnetometer
tracker-infos-magnetometer-status-v1 = { $status ->
    *[NOT_SUPPORTED] Not supported
    [DISABLED] Disabled
    [ENABLED] Enabled
}

## Tracker settings
tracker-settings-back = Go back to trackers list
tracker-settings-title = Tracker settings
tracker-settings-assignment_section = Assignment
tracker-settings-assignment_section-description = What part of the body the tracker is assigned to.
tracker-settings-assignment_section-edit = Edit assignment
tracker-settings-mounting_section = Mounting orientation
tracker-settings-mounting_section-description = Where is the tracker mounted?
tracker-settings-mounting_section-edit = Edit mounting
tracker-settings-drift_compensation_section = Allow drift compensation
tracker-settings-drift_compensation_section-description = Should this tracker compensate for its drift when drift compensation is enabled?
tracker-settings-drift_compensation_section-edit = Allow drift compensation
tracker-settings-use_mag = Allow magnetometer on this tracker
# Multiline!
tracker-settings-use_mag-description =
    Should this tracker use magnetometer to reduce drift when magnetometer usage is allowed? <b>Please don't shutdown your tracker while toggling this!</b>

    You need to allow magnetometer usage first, <magSetting>click here to go to the setting</magSetting>.
tracker-settings-use_mag-label = Allow magnetometer
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Tracker name
tracker-settings-name_section-description = Give it a cute nickname :)
tracker-settings-name_section-placeholder = NightyBeast's left leg
tracker-settings-name_section-label = Tracker name
tracker-settings-forget = Forget tracker
tracker-settings-forget-description = Removes the tracker from the SlimeVR Server and prevents it from connecting until the server is restarted. The configuration of the tracker won't be lost.
tracker-settings-forget-label = Forget tracker
tracker-settings-update-unavailable = Cannot be updated (DIY)
tracker-settings-update-low-battery = Cannot update. Battery lower than 50%
tracker-settings-update-up_to_date = Up to date
tracker-settings-update-blocked = Update not available. No other releases available
tracker-settings-update-available = { $versionName } is now available
tracker-settings-update = Update now
tracker-settings-update-title = Firmware version

## Tracker part card info
tracker-part_card-no_name = No name
tracker-part_card-unassigned = Unassigned

## Body assignment menu
body_assignment_menu = Where do you want this tracker to be?
body_assignment_menu-description = Choose a location where you want this tracker to be assigned. Alternatively, you can choose to manage all trackers at once instead of one by one.
body_assignment_menu-show_advanced_locations = Show advanced assign locations
body_assignment_menu-manage_trackers = Manage all trackers
body_assignment_menu-unassign_tracker = Unassign tracker

## Tracker assignment menu
# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = Which tracker to assign to your
tracker_selection_menu-NONE = Which tracker do you want to be unassigned?
tracker_selection_menu-HEAD = { -tracker_selection-part } head?
tracker_selection_menu-NECK = { -tracker_selection-part } neck?
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part } right shoulder?
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part } right upper arm?
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part } right lower arm?
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part } right hand?
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part } right thigh?
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part } right ankle?
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part } right foot?
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part } right controller?
tracker_selection_menu-UPPER_CHEST = { -tracker_selection-part } upper chest?
tracker_selection_menu-CHEST = { -tracker_selection-part } chest?
tracker_selection_menu-WAIST = { -tracker_selection-part } waist?
tracker_selection_menu-HIP = { -tracker_selection-part } hip?
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part } left shoulder?
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part } left upper arm?
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part } left lower arm?
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part } left hand?
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part } left thigh?
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part } left ankle?
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part } left foot?
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part } left controller?

tracker_selection_menu-unassigned = Unassigned Trackers
tracker_selection_menu-assigned = Assigned Trackers
tracker_selection_menu-dont_assign = Unassign

# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Warning:</b> A neck tracker can be deadly if adjusted too tightly;
    the strap could cut off circulation to your head!
tracker_selection_menu-neck_warning-done = I understand the risks
tracker_selection_menu-neck_warning-cancel = Cancel

## Mounting menu
mounting_selection_menu = Where do you want this tracker to be?
mounting_selection_menu-close = Close

## Sidebar settings
settings-sidebar-title = Settings
settings-sidebar-general = General
settings-sidebar-tracker_mechanics = Tracker mechanics
settings-sidebar-stay_aligned = Stay Aligned
settings-sidebar-fk_settings = Tracking settings
settings-sidebar-gesture_control = Gesture control
settings-sidebar-interface = Interface
settings-sidebar-osc_router = OSC router
settings-sidebar-osc_trackers = VRChat OSC Trackers
settings-sidebar-utils = Utilities
settings-sidebar-serial = Serial console
settings-sidebar-appearance = Appearance
settings-sidebar-notifications = Notifications
settings-sidebar-behavior = Behavior
settings-sidebar-firmware-tool = DIY Firmware Tool
settings-sidebar-vrc_warnings = VRChat Config Warnings
settings-sidebar-advanced = Advanced

## SteamVR settings
settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = SteamVR trackers
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    Enable or disable specific SteamVR trackers.
    Useful for games or apps that only support certain trackers.
settings-general-steamvr-trackers-waist = Waist
settings-general-steamvr-trackers-chest = Chest
settings-general-steamvr-trackers-left_foot = Left foot
settings-general-steamvr-trackers-right_foot = Right foot
settings-general-steamvr-trackers-left_knee = Left knee
settings-general-steamvr-trackers-right_knee = Right knee
settings-general-steamvr-trackers-left_elbow = Left elbow
settings-general-steamvr-trackers-right_elbow = Right elbow
settings-general-steamvr-trackers-left_hand = Left hand
settings-general-steamvr-trackers-right_hand = Right hand
settings-general-steamvr-trackers-tracker_toggling = Automatic tracker assignment
settings-general-steamvr-trackers-tracker_toggling-description = Automatically handles toggling SteamVR trackers on or off depending on your current tracker assignments.
settings-general-steamvr-trackers-tracker_toggling-label = Automatic tracker assignment
settings-general-steamvr-trackers-hands-warning = <b>Warning:</b> hand trackers will override your controllers.
    Are you sure?
settings-general-steamvr-trackers-hands-warning-cancel = Cancel
settings-general-steamvr-trackers-hands-warning-done = Yes

## Tracker mechanics
settings-general-tracker_mechanics = Tracker mechanics
settings-general-tracker_mechanics-filtering = Filtering
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Choose the filtering type for your trackers.
    Prediction predicts movement while smoothing smooths movement.
settings-general-tracker_mechanics-filtering-type = Filtering type
settings-general-tracker_mechanics-filtering-type-none = No filtering
settings-general-tracker_mechanics-filtering-type-none-description = Use rotations as is. Will not do any filtering.
settings-general-tracker_mechanics-filtering-type-smoothing = Smoothing
settings-general-tracker_mechanics-filtering-type-smoothing-description = Smooths movements but adds some latency.
settings-general-tracker_mechanics-filtering-type-prediction = Prediction
settings-general-tracker_mechanics-filtering-type-prediction-description = Reduces latency and makes movements more snappy, but may increase jitter.
settings-general-tracker_mechanics-filtering-amount = Amount
settings-general-tracker_mechanics-yaw-reset-smooth-time = Yaw reset smooth time (0s disables smoothing)
settings-general-tracker_mechanics-drift_compensation = Drift compensation
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Compensates for IMU yaw drift by applying an inverse rotation.
    Change the amount of compensation and the number of resets taken into account.
    This should only be used if you need to reset very often!
settings-general-tracker_mechanics-drift_compensation-enabled-label = Drift compensation
settings-general-tracker_mechanics-drift_compensation-prediction = Drift compensation prediction
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-prediction-description =
    Predicts yaw drift compensation beyond previously measured range.
    Enable this if your trackers are continuously spinning on the yaw axis.
settings-general-tracker_mechanics-drift_compensation-prediction-label = Drift compensation prediction
settings-general-tracker_mechanics-drift_compensation_warning =
    <b>Warning:</b> Only use drift compensation if you need to reset
    very often (every ~5-10 minutes).

    Some IMUs prone to frequent resets include:
    Joy-Cons, owoTrack, and MPUs (without recent firmware).
settings-general-tracker_mechanics-drift_compensation_warning-cancel = Cancel
settings-general-tracker_mechanics-drift_compensation_warning-done = I understand
settings-general-tracker_mechanics-drift_compensation-amount-label = Compensation amount
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Use up to x last resets
settings-general-tracker_mechanics-save_mounting_reset = Save automatic mounting reset calibration
settings-general-tracker_mechanics-save_mounting_reset-description =
    Saves the automatic mounting reset calibrations for the trackers between restarts. Useful
    when wearing a suit where trackers don't move between sessions. <b>Not recommended for normal users!</b>
settings-general-tracker_mechanics-save_mounting_reset-enabled-label = Save mounting reset
settings-general-tracker_mechanics-use_mag_on_all_trackers = Use magnetometer on all IMU trackers that support it
settings-general-tracker_mechanics-use_mag_on_all_trackers-description =
    Uses magnetometer on all trackers that have a compatible firmware for it, reducing drift in stable magnetic environments.
    Can be disabled per tracker in the tracker's settings. <b>Please don't shutdown any of the trackers while toggling this!</b>
settings-general-tracker_mechanics-use_mag_on_all_trackers-label = Use magnetometer on trackers

settings-stay_aligned = Stay Aligned
settings-stay_aligned-description = Stay Aligned reduces drift by gradually adjusting your trackers to match your relaxed poses.
settings-stay_aligned-setup-label = Setup Stay Aligned
settings-stay_aligned-setup-description = You must complete "Setup Stay Aligned" to enable Stay Aligned.
settings-stay_aligned-warnings-drift_compensation = ⚠ Please turn off Drift Compensation! Drift Compensation will conflict with Stay Aligned.
settings-stay_aligned-enabled-label = Adjust trackers
settings-stay_aligned-hide_yaw_correction-label = Hide adjustment (to compare with no Stay Aligned)
settings-stay_aligned-general-label = General
settings-stay_aligned-relaxed_poses-label = Relaxed Poses
settings-stay_aligned-relaxed_poses-description = Stay Aligned uses your relaxed poses to keep the trackers aligned. Use "Setup Stay Aligned" to update these poses.
settings-stay_aligned-relaxed_poses-standing = Adjust trackers while standing
settings-stay_aligned-relaxed_poses-sitting = Adjust trackers while sitting in a chair
settings-stay_aligned-relaxed_poses-flat = Adjust trackers while sitting on the floor, or lying on your back
settings-stay_aligned-relaxed_poses-save_pose = Save pose
settings-stay_aligned-relaxed_poses-reset_pose = Reset pose
settings-stay_aligned-relaxed_poses-close = Close
settings-stay_aligned-debug-label = Debugging
settings-stay_aligned-debug-description = Please include your settings when reporting problems about Stay Aligned.
settings-stay_aligned-debug-copy-label = Copy settings to clipboard

## FK/Tracking settings
settings-general-fk_settings = Tracking settings

# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Floor clip
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Skating correction
settings-general-fk_settings-leg_tweak-toe_snap = Toe snap
settings-general-fk_settings-leg_tweak-foot_plant = Foot plant
settings-general-fk_settings-leg_tweak-skating_correction-amount = Skating correction strength
settings-general-fk_settings-leg_tweak-skating_correction-description = Skating-correction corrects for ice skating, but can decrease accuracy in certain movement patterns. When enabling this, make sure to perform a full reset and recalibrate in-game.
settings-general-fk_settings-leg_tweak-floor_clip-description = Floor-clip can reduce or eliminate clipping through the floor. When enabling this, make sure to perform a full reset and recalibrate in-game.
settings-general-fk_settings-leg_tweak-toe_snap-description = Toe-snap attempts to guess the rotation of your feet if foot trackers are not in use.
settings-general-fk_settings-leg_tweak-foot_plant-description = Foot-plant rotates your feet to be parallel to the ground when in contact.
settings-general-fk_settings-leg_fk = Leg tracking
settings-general-fk_settings-leg_fk-reset_mounting_feet-description-v1 = Force feet mounting reset during general mounting resets.
settings-general-fk_settings-leg_fk-reset_mounting_feet-v1 = Force feet mounting reset
settings-general-fk_settings-enforce_joint_constraints = Skeletal Limits
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints = Enforce constraints
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = Prevents joints from rotating past their limit
settings-general-fk_settings-enforce_joint_constraints-correct_constraints = Correct with constraints
settings-general-fk_settings-enforce_joint_constraints-correct_constraints-description = Correct joint rotations when they push past their limit
settings-general-fk_settings-arm_fk = Arm tracking
settings-general-fk_settings-arm_fk-description = Force arms to be tracked from the headset (HMD) even if positional hand data is available.
settings-general-fk_settings-arm_fk-force_arms = Force arms from HMD
settings-general-fk_settings-reset_settings = Reset settings
settings-general-fk_settings-reset_settings-reset_hmd_pitch-description = Reset the HMD's pitch (vertical rotation) upon doing a full reset. Useful if wearing an HMD on the forehead for VTubing or mocap. Do not enable for VR.
settings-general-fk_settings-reset_settings-reset_hmd_pitch = Reset HMD pitch
settings-general-fk_settings-arm_fk-reset_mode-description = Change which arm pose is expected for mounting reset.
settings-general-fk_settings-arm_fk-back = Back
settings-general-fk_settings-arm_fk-back-description = The default mode, with the upper arms going back and lower arms going forward.
settings-general-fk_settings-arm_fk-tpose_up = T-pose (up)
settings-general-fk_settings-arm_fk-tpose_up-description = Expects your arms to be down at your sides during Full Reset, and 90 degrees up to the sides during Mounting Reset.
settings-general-fk_settings-arm_fk-tpose_down = T-pose (down)
settings-general-fk_settings-arm_fk-tpose_down-description = Expects your arms to be 90 degrees up to the sides during Full Reset, and down at your sides during Mounting Reset.
settings-general-fk_settings-arm_fk-forward = Forward
settings-general-fk_settings-arm_fk-forward-description = Expects your arms to be raised forward at 90 degrees. Useful for VTubing.
settings-general-fk_settings-skeleton_settings-toggles = Skeleton toggles
settings-general-fk_settings-skeleton_settings-description = Toggle skeleton settings on or off. It is recommended to leave these on.
settings-general-fk_settings-skeleton_settings-extended_spine_model = Extended spine model
settings-general-fk_settings-skeleton_settings-extended_pelvis_model = Extended pelvis model
settings-general-fk_settings-skeleton_settings-extended_knees_model = Extended knee model
settings-general-fk_settings-skeleton_settings-ratios = Skeleton ratios
settings-general-fk_settings-skeleton_settings-ratios-description = Change the values of skeleton settings. You may need to adjust your proportions after changing these.
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_hip = Impute waist from chest to hip
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_legs = Impute waist from chest to legs
settings-general-fk_settings-skeleton_settings-impute_hip_from_chest_legs = Impute hip from chest to legs
settings-general-fk_settings-skeleton_settings-impute_hip_from_waist_legs = Impute hip from waist to legs
settings-general-fk_settings-skeleton_settings-interp_hip_legs = Average the hip's yaw and roll with the legs'
settings-general-fk_settings-skeleton_settings-interp_knee_tracker_ankle = Average the knee trackers' yaw and roll with the ankles'
settings-general-fk_settings-skeleton_settings-interp_knee_ankle = Average the knees' yaw and roll with the ankles'

settings-general-fk_settings-self_localization-title = Mocap mode
settings-general-fk_settings-self_localization-description = Mocap Mode allows the skeleton to roughly track its own position without a headset or other trackers. Note that this requires feet and head trackers to work and is still experimental.

## Gesture control settings (tracker tapping)
settings-general-gesture_control = Gesture control
settings-general-gesture_control-subtitle = Tap based resets
settings-general-gesture_control-description = Allows for resets to be triggered by tapping a tracker. The tracker highest up on your torso is used for Yaw Reset, the tracker highest up on your left leg is used for Full Reset, and the tracker highest up on your right leg is used for Mounting Reset. Taps must occur within the time limit of 0.3 seconds times the number of taps to be recognized.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps = { $amount ->
    [one] 1 tap
    *[other] { $amount } taps
}
# This is a unit: 3 trackers, 2 trackers, 1 tracker
# $amount (Number) - Amount of trackers
settings-general-gesture_control-trackers = { $amount ->
    [one] 1 tracker
    *[other] { $amount } trackers
}
settings-general-gesture_control-yawResetEnabled = Enable tap to yaw reset
settings-general-gesture_control-yawResetDelay = Yaw reset delay
settings-general-gesture_control-yawResetTaps = Taps for yaw reset
settings-general-gesture_control-fullResetEnabled = Enable tap to full reset
settings-general-gesture_control-fullResetDelay = Full reset delay
settings-general-gesture_control-fullResetTaps = Taps for full reset
settings-general-gesture_control-mountingResetEnabled = Enable tap to reset mounting
settings-general-gesture_control-mountingResetDelay = Mounting reset delay
settings-general-gesture_control-mountingResetTaps = Taps for mounting reset
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = Trackers over threshold
settings-general-gesture_control-numberTrackersOverThreshold-description = Increase this value if tap detection is not working. Do not increase it above what is needed to make tap detection work as it would cause more false positives.

## Appearance settings
settings-interface-appearance = Appearance
settings-general-interface-dev_mode = Developer Mode
settings-general-interface-dev_mode-description = This mode can be useful if you need in-depth data or to interact with connected trackers on a more advanced level.
settings-general-interface-dev_mode-label = Developer Mode
settings-general-interface-theme = Color theme
settings-general-interface-show-navbar-onboarding = Show "{ navbar-onboarding }" on navigation bar
settings-general-interface-show-navbar-onboarding-description = This changes whether the "{ navbar-onboarding }" button shows on the navigation bar.
settings-general-interface-show-navbar-onboarding-label = Show "{ navbar-onboarding }"
settings-general-interface-lang = Select language
settings-general-interface-lang-description = Change the default language.
settings-general-interface-lang-placeholder = Select the language to use
# Keep the font name untranslated
settings-interface-appearance-font = GUI font
settings-interface-appearance-font-description = This changes the font used by the interface.
settings-interface-appearance-font-placeholder = Default font
settings-interface-appearance-font-os_font = OS font
settings-interface-appearance-font-slime_font = Default font
settings-interface-appearance-font_size = Base font scaling
settings-interface-appearance-font_size-description = This affects the font size of the whole interface except this settings panel.
settings-interface-appearance-decorations = Use the system native decorations
settings-interface-appearance-decorations-description = This will not render the top bar of the interface and will use the operating system's instead.
settings-interface-appearance-decorations-label = Use native decorations

## Notification settings
settings-interface-notifications = Notifications
settings-general-interface-serial_detection = Serial device detection
settings-general-interface-serial_detection-description = This option will show a pop-up every time you plug in a new serial device that could be a tracker. It helps to improve the setup process of a tracker.
settings-general-interface-serial_detection-label = Serial device detection
settings-general-interface-feedback_sound = Feedback sound
settings-general-interface-feedback_sound-description = This option plays a sound when a reset is triggered.
settings-general-interface-feedback_sound-label = Feedback sound
settings-general-interface-feedback_sound-volume = Feedback sound volume
settings-general-interface-connected_trackers_warning = Connected trackers warning
settings-general-interface-connected_trackers_warning-description = This option will show a pop-up every time you try exiting SlimeVR while having one or more connected trackers. It reminds you to turn off your trackers when you are done to preserve battery life.
settings-general-interface-connected_trackers_warning-label = Connected trackers warning on exit

## Behavior settings
settings-interface-behavior = Behavior
settings-general-interface-dev_mode = Developer Mode
settings-general-interface-dev_mode-description = This mode can be useful if you need in-depth data or need to interact with connected trackers on a more advanced level.
settings-general-interface-dev_mode-label = Developer Mode
settings-general-interface-use_tray = Minimize to system tray
settings-general-interface-use_tray-description = Lets you close the window without closing the SlimeVR Server so you can continue using it without having the GUI bother you.
settings-general-interface-use_tray-label = Minimize to system tray
settings-general-interface-discord_presence = Share activity on Discord
settings-general-interface-discord_presence-description = Tells your Discord client that you are using SlimeVR along with the number of IMU trackers you are using.
settings-general-interface-discord_presence-label = Share activity on Discord
settings-general-interface-discord_presence-message = { $amount ->
    [0] Sliming around
    [one] Using 1 tracker
    *[other] Using { $amount } trackers
}
settings-interface-behavior-error_tracking = Error collection via Sentry.io
settings-interface-behavior-error_tracking-description_v2 =
    <h1>Do you consent to the collection of anonymized error data?</h1>

    <b>We do not collect personal information</b> such as your IP address or wireless credentials. SlimeVR values your privacy!

    To provide the best user experience, we collect anonymized error reports, performance metrics, and operating system information. This helps us detect bugs and issues with SlimeVR. These metrics are collected via Sentry.io.

settings-interface-behavior-error_tracking-label = Send errors to developers
settings-interface-behavior-bvh_directory = Directory to save BVH recordings
settings-interface-behavior-bvh_directory-description = Choose a directory to save your BVH recordings instead of having to choose where to save them each time.
settings-interface-behavior-bvh_directory-label = Directory for BVH recordings

## Serial settings
settings-serial = Serial Console
# This cares about multilines
settings-serial-description =
    This is a live information feed for serial communication.
    May be useful to debug firmware or hardware issues.
settings-serial-connection_lost = Connection to serial lost, Reconnecting...
settings-serial-reboot = Reboot
settings-serial-factory_reset = Factory Reset
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>Warning:</b> This will reset the tracker to factory settings.
    Which means Wi-Fi and calibration settings <b>will all be lost!</b>
settings-serial-factory_reset-warning-ok = I know what I'm doing
settings-serial-factory_reset-warning-cancel = Cancel
settings-serial-get_infos = Get Infos
settings-serial-serial_select = Select a serial port
settings-serial-auto_dropdown_item = Auto
settings-serial-get_wifi_scan = Get WiFi Scan
settings-serial-file_type = Plain text
settings-serial-save_logs = Save To File

## OSC router settings
settings-osc-router = OSC router
# This cares about multilines
settings-osc-router-description =
    Forward OSC messages from another program.
    Useful for using another OSC program with VRChat, for example.
settings-osc-router-enable = Enable
settings-osc-router-enable-description = Toggle the forwarding of messages.
settings-osc-router-enable-label = Enable
settings-osc-router-network = Network ports
# This cares about multilines
settings-osc-router-network-description =
    Set the ports for listening and sending data.
    These can be the same as other ports used in the SlimeVR server.
settings-osc-router-network-port_in =
    .label = Port In
    .placeholder = Port in (default: 9002)
settings-osc-router-network-port_out =
    .label = Port Out
    .placeholder = Port out (default: 9000)
settings-osc-router-network-address = Network address
settings-osc-router-network-address-description = Set the address to send out data at.
settings-osc-router-network-address-placeholder = IPV4 address

## OSC VRChat settings
settings-osc-vrchat = VRChat OSC Trackers
# This cares about multilines
settings-osc-vrchat-description-v1 =
    Change settings specific to the OSC Trackers standard used for sending
    tracking data to applications without SteamVR (ex. Quest standalone).
    Make sure to enable OSC in VRChat via the Action Menu under OSC > Enabled.
settings-osc-vrchat-enable = Enable
settings-osc-vrchat-enable-description = Toggle the sending and receiving of data.
settings-osc-vrchat-enable-label = Enable
settings-osc-vrchat-oscqueryEnabled = Enable OSCQuery
settings-osc-vrchat-oscqueryEnabled-description =
    OSCQuery automatically detects running instances of VRChat and sends them data.
    It can also advertise itself to them in order to receive HMD and controller data.
    To allow receiving HMD and controller data from VRChat, go in your main menu's settings
    under "Tracking & IK" and enable "Allow Sending Head and Wrist VR Tracking OSC Data".
settings-osc-vrchat-oscqueryEnabled-label = Enable OSCQuery
settings-osc-vrchat-network = Network ports
settings-osc-vrchat-network-description-v1 = Set the ports for listening and sending data. Can be left untouched for VRChat.
settings-osc-vrchat-network-port_in =
    .label = Port In
    .placeholder = Port in (default: 9001)
settings-osc-vrchat-network-port_out =
    .label = Port Out
    .placeholder = Port out (default: 9000)
settings-osc-vrchat-network-address = Network address
settings-osc-vrchat-network-address-description-v1 = Choose which address to send out data to. Can be left untouched for VRChat.
settings-osc-vrchat-network-address-placeholder = VRChat ip address
settings-osc-vrchat-network-trackers = Trackers
settings-osc-vrchat-network-trackers-description = Toggle the sending of specific trackers via OSC.
settings-osc-vrchat-network-trackers-chest = Chest
settings-osc-vrchat-network-trackers-hip = Hip
settings-osc-vrchat-network-trackers-knees = Knees
settings-osc-vrchat-network-trackers-feet = Feet
settings-osc-vrchat-network-trackers-elbows = Elbows

## VMC OSC settings
settings-osc-vmc = Virtual Motion Capture
# This cares about multilines
settings-osc-vmc-description =
    Change settings specific to the VMC (Virtual Motion Capture) protocol
    to send SlimeVR's bone data and receive bone data from other apps.
settings-osc-vmc-enable = Enable
settings-osc-vmc-enable-description = Toggle the sending and receiving of data.
settings-osc-vmc-enable-label = Enable
settings-osc-vmc-network = Network ports
settings-osc-vmc-network-description = Set the ports for listening and sending data via VMC.
settings-osc-vmc-network-port_in =
    .label = Port In
    .placeholder = Port in (default: 39540)
settings-osc-vmc-network-port_out =
    .label = Port Out
    .placeholder = Port out (default: 39539)
settings-osc-vmc-network-address = Network address
settings-osc-vmc-network-address-description = Choose which address to send out data at via VMC.
settings-osc-vmc-network-address-placeholder = IPV4 address
settings-osc-vmc-vrm = VRM Model
settings-osc-vmc-vrm-description = Load a VRM model to allow head anchor and enable a higher compatibility with other applications.
settings-osc-vmc-vrm-untitled_model = Untitled model
settings-osc-vmc-vrm-file_select = Drag & drop a model to use, or <u>browse</u>
settings-osc-vmc-anchor_hip = Anchor at hips
settings-osc-vmc-anchor_hip-description = Anchor the tracking at the hips, useful for seated VTubing. If disabling, load a VRM model.
settings-osc-vmc-anchor_hip-label = Anchor at hips
settings-osc-vmc-mirror_tracking = Mirror tracking
settings-osc-vmc-mirror_tracking-description = Mirror the tracking horizontally.
settings-osc-vmc-mirror_tracking-label = Mirror tracking

## Advanced settings
settings-utils-advanced = Advanced

settings-utils-advanced-reset-gui = Reset GUI settings
settings-utils-advanced-reset-gui-description = Restore the default settings for the interface.
settings-utils-advanced-reset-gui-label = Reset GUI
settings-utils-advanced-reset-server = Reset tracking settings
settings-utils-advanced-reset-server-description = Restore the default settings for the tracking.
settings-utils-advanced-reset-server-label = Reset tracking
settings-utils-advanced-reset-all = Reset all settings
settings-utils-advanced-reset-all-description = Restore the default settings for both the interface and tracking.
settings-utils-advanced-reset-all-label = Reset all
settings-utils-advanced-reset_warning =
        <b>Warning:</b> This will reset { $type ->
            [gui] your GUI
            [server] your tracking
            *[all] all your
        } settings to the defaults.
        Are you sure you want to do this?
settings-utils-advanced-reset_warning-reset = Reset settings
settings-utils-advanced-reset_warning-cancel = Cancel

settings-utils-advanced-open_data-v1 = Config folder
settings-utils-advanced-open_data-description-v1 = Open SlimeVR's config folder in file explorer, containing the configuration
settings-utils-advanced-open_data-label = Open folder
settings-utils-advanced-open_logs = Logs folder
settings-utils-advanced-open_logs-description = Open SlimeVR's logs folder in file explorer, containing the logs of the app
settings-utils-advanced-open_logs-label = Open folder

## Setup/onboarding menu
onboarding-skip = Skip setup
onboarding-continue = Continue
onboarding-wip = Work in progress
onboarding-previous_step = Previous step
onboarding-setup_warning =
    <b>Warning:</b> The initial setup is required for good tracking,
    it is needed if this is your first time using SlimeVR.
onboarding-setup_warning-skip = Skip setup
onboarding-setup_warning-cancel = Continue setup

## Wi-Fi setup
onboarding-wifi_creds-back = Go Back to introduction
onboarding-wifi_creds = Input Wi-Fi credentials
# This cares about multilines
onboarding-wifi_creds-description =
    The Trackers will use these credentials to connect wirelessly.
    Please use the credentials that you are currently connected to.
onboarding-wifi_creds-skip = Skip Wi-Fi settings
onboarding-wifi_creds-submit = Submit!
onboarding-wifi_creds-ssid =
    .label = Wi-Fi name
    .placeholder = Enter Wi-Fi name
onboarding-wifi_creds-ssid-required = Wi-Fi name is required
onboarding-wifi_creds-password =
    .label = Password
    .placeholder = Enter password

## Mounting setup
onboarding-reset_tutorial-back = Go Back to Mounting calibration
onboarding-reset_tutorial = Reset tutorial
onboarding-reset_tutorial-explanation = While you use your trackers, they might get out of alignment because of IMU yaw drift, or because you might have moved them physically. You have several ways to fix this.
onboarding-reset_tutorial-skip = Skip step
# Cares about multiline
onboarding-reset_tutorial-0 = Tap the highlighted tracker { $taps } times to trigger a yaw reset.

    This will make the trackers face the same direction as your headset (HMD).
# Cares about multiline
onboarding-reset_tutorial-1 = Tap the highlighted tracker { $taps } times to trigger a full reset.

    You need to be standing for this (i-pose). There is a 3 seconds delay (configurable) before it actually happens.
    This fully resets the position and rotation of all your trackers. It should fix most issues.
# Cares about multiline
onboarding-reset_tutorial-2 = Tap the highlighted tracker { $taps } times to trigger a mounting reset.

    Mounting reset adjusts for how trackers are placed on your body. If they've moved or rotated significantly, this helps recalibrate their orientation.

    You need to be in a pose like you are skiing as shown in the Automatic Mounting wizard, and you have a 3 second delay (configurable) before it gets triggered.

## Setup start
onboarding-home = Welcome to SlimeVR
onboarding-home-start = Let's get set up!

## Enter VR part of setup
onboarding-enter_vr-back = Go Back to Tracker assignment
onboarding-enter_vr-title = Time to enter VR!
onboarding-enter_vr-description = Put on all your trackers and then enter VR!
onboarding-enter_vr-ready = I'm ready

## Setup done
onboarding-done-title = You're all set!
onboarding-done-description = Enjoy your full-body experience
onboarding-done-close = Close setup

## Tracker connection setup
onboarding-connect_tracker-back = Go Back to Wi-Fi credentials
onboarding-connect_tracker-title = Connect trackers
onboarding-connect_tracker-description-p0-v1 = Now onto the fun part, connecting trackers!
onboarding-connect_tracker-description-p1-v1 = Connect each tracker one at a time through a USB port.
onboarding-connect_tracker-issue-serial = I'm having trouble connecting!
onboarding-connect_tracker-usb = USB Tracker
onboarding-connect_tracker-connection_status-none = Looking for trackers
onboarding-connect_tracker-connection_status-serial_init = Connecting to serial device
onboarding-connect_tracker-connection_status-obtaining_mac_address = Obtaining the tracker mac address
onboarding-connect_tracker-connection_status-provisioning = Sending Wi-Fi credentials
onboarding-connect_tracker-connection_status-connecting = Trying to connect to Wi-Fi
onboarding-connect_tracker-connection_status-looking_for_server = Looking for server
onboarding-connect_tracker-connection_status-connection_error = Unable to connect to Wi-Fi
onboarding-connect_tracker-connection_status-could_not_find_server = Could not find the server
onboarding-connect_tracker-connection_status-done = Connected to the Server
onboarding-connect_tracker-connection_status-no_serial_log = Could not get logs from the tracker
onboarding-connect_tracker-connection_status-no_serial_device_found = Could not find a tracker from USB
onboarding-connect_serial-error-modal-no_serial_log = Is the tracker turned on?
onboarding-connect_serial-error-modal-no_serial_log-desc = Make sure the tracker is turned on and connected to your computer.
onboarding-connect_serial-error-modal-no_serial_device_found = No trackers detected
onboarding-connect_serial-error-modal-no_serial_device_found-desc =
    Please connect a tracker with the provided USB cable to your computer and turn the tracker on.
    If this does not work:
      - try using a different USB cable
      - try using a different USB port
      - try reinstalling the SlimeVR server and select "USB Drivers" in the components section
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers = { $amount ->
    [0] No trackers
    [one] 1 tracker
    *[other] { $amount } trackers
} connected
onboarding-connect_tracker-next = I connected all my trackers

## Tracker calibration tutorial
onboarding-calibration_tutorial = IMU Calibration Tutorial
onboarding-calibration_tutorial-subtitle = This will help reduce tracker drifting!
onboarding-calibration_tutorial-description-v1 = After turning on your trackers, place them on a stable surface for a moment to allow for calibration. Calibration can be performed at any time after the trackers are powered on—this page simply provides a tutorial. To begin, click the "{ onboarding-calibration_tutorial-calibrate }" button, then <b>do not move your trackers!</b>
onboarding-calibration_tutorial-calibrate = I placed my trackers on a table
onboarding-calibration_tutorial-status-waiting = Waiting for you
onboarding-calibration_tutorial-status-calibrating = Calibrating
onboarding-calibration_tutorial-status-success = Nice!
onboarding-calibration_tutorial-status-error = The tracker was moved
onboarding-calibration_tutorial-skip = Skip tutorial

## Tracker assignment tutorial
onboarding-assignment_tutorial = How to prepare a Slime Tracker before putting it on
onboarding-assignment_tutorial-first_step = 1. Place a body part sticker (if you have one) on the tracker according to your choosing
# This text has a character limit of around 11 characters, so please keep it short
onboarding-assignment_tutorial-sticker = Sticker
onboarding-assignment_tutorial-second_step-v2 = 2. Attach the strap to your tracker, keeping the velcro side of the strap facing the same direction as the slime face of your tracker:
onboarding-assignment_tutorial-second_step-continuation-v2 = The velcro side for the extension should be facing up like the following image:
onboarding-assignment_tutorial-done = I put stickers and straps!

## Tracker assignment setup
onboarding-assign_trackers-back = Go Back to Wi-Fi Credentials
onboarding-assign_trackers-title = Assign trackers
onboarding-assign_trackers-description = Let's choose which tracker goes where. Click on a location where you want to place a tracker
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned = { $assigned } of { $trackers ->
    [one] 1 tracker
    *[other] { $trackers } trackers
} assigned
onboarding-assign_trackers-advanced = Show advanced assign locations
onboarding-assign_trackers-next = I assigned all the trackers
onboarding-assign_trackers-mirror_view = Mirror view
onboarding-assign_trackers-option-amount = { $trackersCount ->
    [one] x{ $trackersCount }
    *[other] x{ $trackersCount }
}
onboarding-assign_trackers-option-label = { $mode ->
    [lower-body] Lower-Body Set
    [core] Core Set
    [enhanced-core] Enhanced Core Set
    [full-body] Full-Body Set
    *[all] All Trackers
}
onboarding-assign_trackers-option-description = { $mode ->
    [lower-body] Minimum for VR full-body tracking
    [core] + Enhanced spine tracking
    [enhanced-core] + Foot rotation
    [full-body] + Elbow tracking
    *[all] All available tracker assignments
}

## Tracker assignment warnings
# Note for devs, number is used for representing boolean states per bit.
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_FOOT = Left foot is assigned but you need { $unassigned ->
    [0] the left ankle, left thigh and either the chest, hip or waist
    [1] the left thigh and either the chest, hip or waist
    [2] the left ankle and either the chest, hip or waist
    [3] either the chest, hip or waist
    [4] the left ankle and left thigh
    [5] the left thigh
    [6] the left ankle
    *[unknown] Unknown unassigned body part
} to also be assigned!
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_FOOT = Right foot is assigned but you need { $unassigned ->
    [0] the right ankle, right thigh and either the chest, hip or waist
    [1] the right thigh and either the chest, hip or waist
    [2] the right ankle and either the chest, hip or waist
    [3] either the chest, hip or waist
    [4] the right ankle and right thigh
    [5] the right thigh
    [6] the right ankle
    *[unknown] Unknown unassigned body part
} to also be assigned!
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_LOWER_LEG = Left ankle is assigned but you need { $unassigned ->
    [0] the left thigh and either the chest, hip or waist
    [1] either the chest, hip or waist
    [2] the left thigh
    *[unknown] Unknown unassigned body part
} to also be assigned!
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_LOWER_LEG = Right ankle is assigned but you need { $unassigned ->
    [0] the right thigh and either the chest, hip or waist
    [1] either the chest, hip or waist
    [2] the right thigh
    *[unknown] Unknown unassigned body part
} to also be assigned!
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_UPPER_LEG = Left thigh is assigned but you need { $unassigned ->
    [0] either the chest, hip or waist
    *[unknown] Unknown unassigned body part
} to also be assigned!
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_UPPER_LEG = Right thigh is assigned but you need { $unassigned ->
    [0] either the chest, hip or waist
    *[unknown] Unknown unassigned body part
} to also be assigned!
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-HIP = Hip is assigned but you need { $unassigned ->
    [0] the chest
    *[unknown] Unknown unassigned body part
} to also be assigned!
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-WAIST = Waist is assigned but you need { $unassigned ->
    [0] the chest
    *[unknown] Unknown unassigned body part
} to also be assigned!

## Tracker mounting method choose
onboarding-choose_mounting = What mounting calibration method to use?
# Multiline text
onboarding-choose_mounting-description = Mounting orientation corrects for the placement of trackers on your body.
onboarding-choose_mounting-auto_mounting = Automatic mounting
# Italicized text
onboarding-choose_mounting-auto_mounting-label-v2 = Recommended
onboarding-choose_mounting-auto_mounting-description = This will automatically detect the mounting orientations for all of your trackers from 2 poses
onboarding-choose_mounting-manual_mounting = Manual mounting
# Italicized text
onboarding-choose_mounting-manual_mounting-label-v2 = Might not be precise enough
onboarding-choose_mounting-manual_mounting-description = This will let you choose the mounting orientation manually for each tracker
# Multiline text
onboarding-choose_mounting-manual_modal-title = Are you sure you want to do
    the automatic mounting calibration?
onboarding-choose_mounting-manual_modal-description = <b>The manual mounting calibration is recommended for new users</b>, as the automatic mounting calibration's poses can be hard to get right first and may require some practice.
onboarding-choose_mounting-manual_modal-confirm = I'm sure of what I'm doing
onboarding-choose_mounting-manual_modal-cancel = Cancel

## Tracker manual mounting setup
onboarding-manual_mounting-back = Go Back to Enter VR
onboarding-manual_mounting = Manual Mounting
onboarding-manual_mounting-description = Click on every tracker and select which way they are mounted
onboarding-manual_mounting-auto_mounting = Automatic mounting
onboarding-manual_mounting-next = Next step

## Tracker automatic mounting setup
onboarding-automatic_mounting-back = Go Back to Enter VR
onboarding-automatic_mounting-title = Mounting Calibration
onboarding-automatic_mounting-description = For SlimeVR trackers to work, we need to assign a mounting orientation to your trackers to align them with your physical tracker mounting.
onboarding-automatic_mounting-manual_mounting = Manual mounting
onboarding-automatic_mounting-next = Next step
onboarding-automatic_mounting-prev_step = Previous step
onboarding-automatic_mounting-done-title = Mounting orientations calibrated.
onboarding-automatic_mounting-done-description = Your mounting calibration is complete!
onboarding-automatic_mounting-done-restart = Try again
onboarding-automatic_mounting-mounting_reset-title = Mounting Reset
onboarding-automatic_mounting-mounting_reset-step-0 = 1. Squat in a "skiing" pose with your legs bent, your upper body tilted forwards, and your arms bent.
onboarding-automatic_mounting-mounting_reset-step-1 = 2. Press the "Reset Mounting" button and wait for 3 seconds before the trackers' mounting orientations will reset.
onboarding-automatic_mounting-preparation-title = Preparation
onboarding-automatic_mounting-preparation-v2-step-0 = 1. Press the "Full Reset" button.
onboarding-automatic_mounting-preparation-v2-step-1 = 2. Stand upright with your arms to your sides. Make sure to look forward.
onboarding-automatic_mounting-preparation-v2-step-2 = 3. Hold the position until the 3s timer ends.
onboarding-automatic_mounting-put_trackers_on-title = Put on your trackers
onboarding-automatic_mounting-put_trackers_on-description = To calibrate mounting orientations, we're gonna use the trackers you just assigned. Put on all your trackers, you can see which are which in the figure to the right.
onboarding-automatic_mounting-put_trackers_on-next = I have all my trackers on
onboarding-automatic_mounting-return-home = Done

## Tracker manual proportions setupa
onboarding-manual_proportions-back = Go Back to Reset tutorial
onboarding-manual_proportions-title = Manual Body Proportions
onboarding-manual_proportions-fine_tuning_button = Automatically fine tune proportions
onboarding-manual_proportions-fine_tuning_button-disabled-tooltip = Please connect a VR headset to use automatic fine tuning
onboarding-manual_proportions-export = Export proportions
onboarding-manual_proportions-import = Import proportions
onboarding-manual_proportions-file_type = Body proportions file
onboarding-manual_proportions-normal_increment = Normal increment
onboarding-manual_proportions-precise_increment = Precise increment
onboarding-manual_proportions-grouped_proportions = Grouped proportions
onboarding-manual_proportions-all_proportions = All proportions
onboarding-manual_proportions-estimated_height = Estimated user height

## Tracker automatic proportions setup
onboarding-automatic_proportions-back = Go Back to Manual Proportions
onboarding-automatic_proportions-title = Measure your body
onboarding-automatic_proportions-description = For SlimeVR trackers to work, we need to know the length of your bones. This short calibration will measure it for you.
onboarding-automatic_proportions-manual = Manual proportions
onboarding-automatic_proportions-prev_step = Previous step

onboarding-automatic_proportions-put_trackers_on-title = Put on your trackers
onboarding-automatic_proportions-put_trackers_on-description = To calibrate your proportions, we're gonna use the trackers you just assigned. Put on all your trackers, you can see which are which in the figure to the right.
onboarding-automatic_proportions-put_trackers_on-next = I have all my trackers on

onboarding-automatic_proportions-requirements-title = Requirements
# Each line of text is a different list item
onboarding-automatic_proportions-requirements-descriptionv2 =
    You have at least enough trackers to track your feet (generally 5 trackers).
    You have your trackers and headset on and are wearing them.
    Your trackers and headset are connected to the SlimeVR server and are working properly (ex. no stuttering, disconnecting, etc).
    Your headset is reporting positional data to the SlimeVR server (this generally means having SteamVR running and connected to SlimeVR using SlimeVR's SteamVR driver).
    Your tracking is working and is accurately representing your movements (ex. you have performed a full reset and they move the right direction when kicking, bending over, sitting, etc).
onboarding-automatic_proportions-requirements-next = I have read the requirements

onboarding-automatic_proportions-check_height-title-v3 = Measure your headset height
onboarding-automatic_proportions-check_height-description-v2 = Your headset (HMD) height should be slightly less than your full height because headsets measure your eye height. This measurement will be used as a baseline for your body proportions.
# All the text is in bold!
onboarding-automatic_proportions-check_height-calculation_warning-v3 = Start measuring while standing <u>upright</u> to measure your height. Be careful not to raise your hands higher than your headset, as they may affect the measurement!
onboarding-automatic_proportions-check_height-guardian_tip = If you are using a standalone VR headset, make sure to have your guardian /
    boundary turned on so that your height is correct!
# Context is that the height is unknown
onboarding-automatic_proportions-check_height-unknown = Unknown
# Shows an element below it
onboarding-automatic_proportions-check_height-hmd_height2 = Your headset height is:
onboarding-automatic_proportions-check_height-measure-start = Start measuring
onboarding-automatic_proportions-check_height-measure-stop = Stop measuring
onboarding-automatic_proportions-check_height-measure-reset = Retry measuring
onboarding-automatic_proportions-check_height-next_step = Use headset height

onboarding-automatic_proportions-check_floor_height-title = Measure your floor height (optional)
onboarding-automatic_proportions-check_floor_height-description = In some cases, your floor height may not be set correctly by your headset, causing the headset height to be measured as higher than it should be. You can measure the "height" of your floor to correct your headset height.
# All the text is in bold!
onboarding-automatic_proportions-check_floor_height-calculation_warning-v2 = Start measuring and put a controller against your floor to measure its height. If you are sure that your floor height is correct, you can skip this step.
# Shows an element below it
onboarding-automatic_proportions-check_floor_height-floor_height = Your floor height is:
onboarding-automatic_proportions-check_floor_height-full_height = Your estimated full height is:
onboarding-automatic_proportions-check_floor_height-measure-start = Start measuring
onboarding-automatic_proportions-check_floor_height-measure-stop = Stop measuring
onboarding-automatic_proportions-check_floor_height-measure-reset = Retry measuring
onboarding-automatic_proportions-check_floor_height-skip_step = Skip step and save
onboarding-automatic_proportions-check_floor_height-next_step = Use floor height and save

onboarding-automatic_proportions-start_recording-title = Get ready to move
onboarding-automatic_proportions-start_recording-description = We're now going to record some specific poses and moves. These will be prompted in the next screen. Be ready to start when the button is pressed!
onboarding-automatic_proportions-start_recording-next = Start Recording

onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = Recording in progress...
onboarding-automatic_proportions-recording-description-p1 = Make the moves shown below:
# Each line of text is a different list item
onboarding-automatic_proportions-recording-steps =
    Standing up straight, roll your head in a circle.
    Bend your back forward and squat. While squatting, look to your left, then to your right.
    Twist your upper body to the left (counter-clockwise), then reach down toward the ground.
    Twist your upper body to the right (clockwise), then reach down toward the ground.
    Roll your hips in a circular motion as if you're using a hula hoop.
    If there is time left on the recording, you can repeat these steps until it's finished.
onboarding-automatic_proportions-recording-processing = Processing the result
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 20)
onboarding-automatic_proportions-recording-timer = { $time ->
    [one] 1 second left
    *[other] { $time } seconds left
}

onboarding-automatic_proportions-verify_results-title = Verify results
onboarding-automatic_proportions-verify_results-description = Check the results below, do they look correct?
onboarding-automatic_proportions-verify_results-results = Recording results
onboarding-automatic_proportions-verify_results-processing = Processing the result
onboarding-automatic_proportions-verify_results-redo = Redo recording
onboarding-automatic_proportions-verify_results-confirm = They're correct

onboarding-automatic_proportions-done-title = Body measured and saved.
onboarding-automatic_proportions-done-description = Your body proportions' calibration is complete!
onboarding-automatic_proportions-error_modal-v2 =
    <b>Warning:</b> There was an error while estimating proportions!
    This is likely a mounting calibration issue. Make sure your tracking works properly before trying again.
    Please <docs>check the docs</docs> or join our <discord>Discord</discord> for help ^_^
onboarding-automatic_proportions-error_modal-confirm = Understood!

onboarding-automatic_proportions-smol_warning =
    Your configured height of { $height } is smaller than the minimum accepted height of { $minHeight }.
    <b>Please redo the measurements and ensure they are correct.</b>
onboarding-automatic_proportions-smol_warning-cancel = Go back

## Tracker scaled proportions setup
onboarding-scaled_proportions-title = Scaled proportions
onboarding-scaled_proportions-description = For SlimeVR trackers to work, we need to know the length of your bones. This will use an average proportion and scale it based on your height.
onboarding-scaled_proportions-manual_height-title = Configure your height
onboarding-scaled_proportions-manual_height-description-v2 = This height will be used as a baseline for your body proportions.
onboarding-scaled_proportions-manual_height-missing_steamvr = SteamVR is not currently connected to SlimeVR, so measurements can't be based on your headset. <b>Proceed at your own risk or check the docs!</b>
onboarding-scaled_proportions-manual_height-height-v2 = Your full height is
onboarding-scaled_proportions-manual_height-estimated_height = Your estimated headset height is:
onboarding-scaled_proportions-manual_height-next_step = Continue and save
onboarding-scaled_proportions-manual_height-warning =
    You are currently using the manual way of setting up scaled proportions!
    <b>This mode is recommended only if you do not use an HMD with SlimeVR.</b>

    To be able to use the automatic scaled proportions please:
onboarding-scaled_proportions-manual_height-warning-no_hmd = Connect a VR Headset
onboarding-scaled_proportions-manual_height-warning-no_controllers = Make sure your controllers are connected and correctly assigned to your hands

## Tracker scaled proportions reset
onboarding-scaled_proportions-reset_proportion-title = Reset your body proportions
onboarding-scaled_proportions-reset_proportion-description = To set your body proportions based on your height, you need to now reset all of your proportions. This will clear any proportions you have configured and provide a baseline configuration.
onboarding-scaled_proportions-done-title = Body proportions set
onboarding-scaled_proportions-done-description = Your body proportions should now be configured based on your height.

## Stay Aligned setup
onboarding-stay_aligned-title = Stay Aligned
onboarding-stay_aligned-description = Configure Stay Aligned to keep your trackers aligned.
onboarding-stay_aligned-put_trackers_on-title = Put on your trackers
onboarding-stay_aligned-put_trackers_on-description = To save your resting poses, we'll use the trackers you just assigned. Put on all your trackers, you can see which are which in the figure to the right.
onboarding-stay_aligned-put_trackers_on-trackers_warning = You have fewer than 5 trackers currently connected and assigned! This is the minimum amount of trackers required for Stay Aligned to function properly.
onboarding-stay_aligned-put_trackers_on-next = I have all my trackers on
onboarding-stay_aligned-verify_mounting-title = Check your Mounting
onboarding-stay_aligned-verify_mounting-step-0 = Stay Aligned requires good mounting. Otherwise, you won't get a good experience with Stay Aligned.
onboarding-stay_aligned-verify_mounting-step-1 = 1. Move around while standing.
onboarding-stay_aligned-verify_mounting-step-2 = 2. Sit down and move your legs and feet.
onboarding-stay_aligned-verify_mounting-step-3 = 3. If your trackers aren't in the right place, press "Redo Mounting Calibration".
onboarding-stay_aligned-verify_mounting-redo_mounting = Redo Mounting calibration
onboarding-stay_aligned-preparation-title = Preparation
onboarding-stay_aligned-preparation-tip = Make sure to stand upright. Keep looking forward with your arms down at your sides.
onboarding-stay_aligned-relaxed_poses-standing-title = Relaxed Standing Pose
onboarding-stay_aligned-relaxed_poses-standing-step-0 = 1. Stand in a comfortable position. Relax!
onboarding-stay_aligned-relaxed_poses-standing-step-1-v2 = 2. Press the "Save pose" button.
onboarding-stay_aligned-relaxed_poses-sitting-title = Relaxed Sitting in Chair Pose
onboarding-stay_aligned-relaxed_poses-sitting-step-0 = 1. Sit in a comfortable position. Relax!
onboarding-stay_aligned-relaxed_poses-sitting-step-1-v2 = 2. Press the "Save pose" button.
onboarding-stay_aligned-relaxed_poses-flat-title = Relaxed Sitting on Floor Pose
onboarding-stay_aligned-relaxed_poses-flat-step-0 = 1. Sit on the floor with your legs in front. Relax!
onboarding-stay_aligned-relaxed_poses-flat-step-1-v2 = 2. Press the "Save pose" button.
onboarding-stay_aligned-relaxed_poses-skip_step = Skip
onboarding-stay_aligned-done-title = Stay Aligned enabled!
onboarding-stay_aligned-done-description = Your Stay Aligned setup is complete!
onboarding-stay_aligned-done-description-2 = Setup is complete! You may restart the process if you want to recalibrate the poses.
onboarding-stay_aligned-previous_step = Previous
onboarding-stay_aligned-next_step = Next
onboarding-stay_aligned-restart = Restart
onboarding-stay_aligned-done = Done

## Home
home-no_trackers = No trackers detected or assigned

## Trackers Still On notification
trackers_still_on-modal-title = Trackers still on
trackers_still_on-modal-description =
    One or more trackers are still on.
    Do you still want to exit SlimeVR?
trackers_still_on-modal-confirm = Exit SlimeVR
trackers_still_on-modal-cancel = Hold on...

## Status system
status_system-StatusTrackerReset = It is recommended to perform a full reset as one or more trackers are unadjusted.
status_system-StatusSteamVRDisconnected = { $type ->
    *[steamvr] Currently not connected to SteamVR via the SlimeVR driver.
    [steamvr_feeder] Currently not connected to the SlimeVR Feeder App.
}
status_system-StatusTrackerError = The { $trackerName } tracker has an error.
status_system-StatusUnassignedHMD = The VR headset should be assigned as a head tracker.
status_system-StatusPublicNetwork = {$count ->
    [one] Your network profile is currently set to Public ({$adapters}). This is not recommended for SlimeVR to function properly. <PublicFixLink>See how to fix it here.</PublicFixLink>
    *[many] Some of your network adapters are set to public: {$adapters}. This is not recommended for SlimeVR to function properly. <PublicFixLink>See how to fix it here.</PublicFixLink>
}


## Firmware tool globals
firmware_tool-next_step = Next Step
firmware_tool-previous_step = Previous Step
firmware_tool-ok = Looks good
firmware_tool-retry = Retry

firmware_tool-loading = Loading...

## Firmware tool Steps
firmware_tool = DIY Firmware tool
firmware_tool-description =
    Allows you to configure and flash your DIY trackers
firmware_tool-not_available = Oops, the firmware tool is not available at the moment. Come back later!
firmware_tool-not_compatible = The firmware tool is not compatible with this version of the server. Please update your server!

firmware_tool-board_step = Select your Board
firmware_tool-board_step-description = Select one of the boards listed below.

firmware_tool-board_pins_step = Check the pins
firmware_tool-board_pins_step-description =
    Please verify that the selected pins are correct.
    If you followed the SlimeVR documentation, the default values should be correct.
firmware_tool-board_pins_step-enable_led = Enable LED
firmware_tool-board_pins_step-led_pin =
    .label = LED Pin
    .placeholder = Enter the pin address of the LED

firmware_tool-board_pins_step-battery_type = Select the battery type
firmware_tool-board_pins_step-battery_type-BAT_EXTERNAL = External battery
firmware_tool-board_pins_step-battery_type-BAT_INTERNAL = Internal battery
firmware_tool-board_pins_step-battery_type-BAT_INTERNAL_MCP3021 = Internal MCP3021
firmware_tool-board_pins_step-battery_type-BAT_MCP3021 = MCP3021


firmware_tool-board_pins_step-battery_sensor_pin =
    .label = Battery sensor Pin
    .placeholder = Enter the pin address of battery sensor
firmware_tool-board_pins_step-battery_resistor =
    .label = Battery Resistor (Ohms)
    .placeholder = Enter the value of battery resistor
firmware_tool-board_pins_step-battery_shield_resistor-0 =
    .label = Battery Shield R1 (Ohms)
    .placeholder = Enter the value of Battery Shield R1
firmware_tool-board_pins_step-battery_shield_resistor-1 =
    .label = Battery Shield R2 (Ohms)
    .placeholder = Enter the value of Battery Shield R2

firmware_tool-add_imus_step = Declare your IMUs
firmware_tool-add_imus_step-description =
    Please add the IMUs that your tracker has.
    If you followed the SlimeVR documentation, the default values should be correct.
firmware_tool-add_imus_step-imu_type-label = IMU type
firmware_tool-add_imus_step-imu_type-placeholder = Select the type of IMU

firmware_tool-add_imus_step-imu_rotation-tooltip = Click to open documentation
firmware_tool-add_imus_step-imu_rotation-tooltip-label = IMU Rotation (Degree)
firmware_tool-add_imus_step-imu_rotation-tooltip-placeholder = IMU Rotation (Degree)
    .label = IMU Rotation (deg)
    .placeholder = Rotation angle of the IMU
firmware_tool-add_imus_step-scl_pin =
    .label = SCL Pin
    .placeholder = Pin address of SCL
firmware_tool-add_imus_step-sda_pin =
    .label = SDA Pin
    .placeholder = Pin address of SDA
firmware_tool-add_imus_step-int_pin =
    .label = INT Pin
    .placeholder = Pin address of INT
firmware_tool-add_imus_step-optional_tracker =
    .label = Optional tracker
firmware_tool-add_imus_step-show_less = Show Less
firmware_tool-add_imus_step-show_more = Show More
firmware_tool-add_imus_step-add_more = Add more IMUs

firmware_tool-select_firmware_step = Select the firmware version
firmware_tool-select_firmware_step-description =
    Please choose what version of the firmware you want to use
firmware_tool-select_firmware_step-show-third-party =
    .label = Show third party firmwares

firmware_tool-flash_method_step = Flashing Method
firmware_tool-flash_method_step-description =
    Please select the flashing method you want to use
firmware_tool-flash_method_step-ota =
    .label = OTA
    .description = Use the over-the-air method. Your tracker will use Wi-Fi to update its firmware. Only works on trackers that have been set up.
firmware_tool-flash_method_step-serial =
    .label = Serial
    .description = Use a USB cable to update your tracker.

firmware_tool-flashbtn_step = Press the boot btn
firmware_tool-flashbtn_step-description = Before going to the next step, there are a few things you need to do

firmware_tool-flashbtn_step-board_SLIMEVR = Turn off the tracker, remove the case (if any), connect the USB cable to your computer, then follow the appropriate steps for your SlimeVR board revision:
firmware_tool-flashbtn_step-board_SLIMEVR-r11 = Turn on the tracker while shorting the second rectangular FLASH pad from the edge on the top side of the board to the metal shield of the microcontroller.
firmware_tool-flashbtn_step-board_SLIMEVR-r12 = Turn on the tracker while shorting the circular FLASH pad on the top side of the board to the metal shield of the microcontroller.
firmware_tool-flashbtn_step-board_SLIMEVR-r14 = Turn on the tracker while pushing in the FLASH button on the top side of the board.

firmware_tool-flashbtn_step-board_OTHER = Before flashing, you will probably need to put the tracker into bootloader mode.
    Most of the time, this means pressing the boot button on the board before the flashing process starts.
    If the flashing process times out at the start, it probably means that the tracker was not in bootloader mode.
    Refer to your board's flashing instructions to learn how to enter bootloader mode.



firmware_tool-flash_method_ota-devices = Detected OTA Devices:
firmware_tool-flash_method_ota-no_devices = There are no boards that can be updated using OTA, make sure you selected the correct board type
firmware_tool-flash_method_serial-wifi = Wi-Fi Credentials:
firmware_tool-flash_method_serial-devices-label = Detected Serial Devices:
firmware_tool-flash_method_serial-devices-placeholder = Select a serial device
firmware_tool-flash_method_serial-no_devices = There are no compatible serial devices detected, make sure the tracker is plugged in

firmware_tool-build_step = Building
firmware_tool-build_step-description =
    The firmware is building, please wait

firmware_tool-flashing_step = Flashing
firmware_tool-flashing_step-description =
    Your trackers are flashing, please follow the instructions on the screen
firmware_tool-flashing_step-warning-v2 = Do not unplug or turn off the tracker during the upload process unless told to, it may make your board unusable
firmware_tool-flashing_step-flash_more = Flash more trackers
firmware_tool-flashing_step-exit = Exit

## firmware tool build status
firmware_tool-build-CREATING_BUILD_FOLDER = Creating the build folder
firmware_tool-build-DOWNLOADING_FIRMWARE = Downloading the firmware
firmware_tool-build-EXTRACTING_FIRMWARE = Extracting the firmware
firmware_tool-build-SETTING_UP_DEFINES = Configuring the defines
firmware_tool-build-BUILDING = Building the firmware
firmware_tool-build-SAVING = Saving the build
firmware_tool-build-DONE = Build Complete
firmware_tool-build-ERROR = Unable to build the firmware

## Firmware update status
firmware_update-status-DOWNLOADING = Downloading the firmware
firmware_update-status-NEED_MANUAL_REBOOT-v2 = Please turn your tracker off and on again
firmware_update-status-AUTHENTICATING = Authenticating with the mcu
firmware_update-status-UPLOADING = Uploading the firmware
firmware_update-status-SYNCING_WITH_MCU = Syncing with the mcu
firmware_update-status-REBOOTING = Applying the update
firmware_update-status-PROVISIONING = Setting Wi-Fi credentials
firmware_update-status-DONE = Update complete!
firmware_update-status-ERROR_DEVICE_NOT_FOUND = Could not find the device
firmware_update-status-ERROR_TIMEOUT = The update process timed out
firmware_update-status-ERROR_DOWNLOAD_FAILED = Could not download the firmware
firmware_update-status-ERROR_AUTHENTICATION_FAILED = Could not authenticate with the mcu
firmware_update-status-ERROR_UPLOAD_FAILED = Could not upload the firmware
firmware_update-status-ERROR_PROVISIONING_FAILED = Could not set the Wi-Fi credentials
firmware_update-status-ERROR_UNSUPPORTED_METHOD = The update method is not supported
firmware_update-status-ERROR_UNKNOWN = Unknown error

## Dedicated Firmware Update Page
firmware_update-title = Firmware update
firmware_update-devices = Available Devices
firmware_update-devices-description = Please select the trackers you want to update to the latest version of SlimeVR firmware.
firmware_update-no_devices = Please make sure that the trackers you want to update are ON and connected to the Wi-Fi!
firmware_update-changelog-title = Updating to {$version}
firmware_update-looking_for_devices = Looking for devices to update...
firmware_update-retry = Retry
firmware_update-update = Update Selected Trackers
firmware_update-exit = Exit

## Tray Menu
tray_menu-show = Show
tray_menu-hide = Hide
tray_menu-quit = Quit

## First exit modal
tray_or_exit_modal-title = What should the close button do?
# Multiline text
tray_or_exit_modal-description = Choose whether to exit the server or minimize it to the tray when clicking the close button.

    You can change this later in the interface settings!
tray_or_exit_modal-radio-exit = Exit on close
tray_or_exit_modal-radio-tray = Minimize to system tray
tray_or_exit_modal-submit = Save
tray_or_exit_modal-cancel = Cancel

## Unknown device modal
unknown_device-modal-title = A new tracker was found!
unknown_device-modal-description = There is a new tracker with MAC address <b>{$deviceId}</b>.
    Do you want to connect it to SlimeVR?
unknown_device-modal-confirm = Sure!
unknown_device-modal-forget = Ignore it


# VRChat config warnings
vrc_config-page-title = VRChat configuration warnings
vrc_config-page-desc = This page shows the state of your VRChat settings and shows what settings are incompatible with SlimeVR. It is highly recommended that you fix any warnings showing up here for the best user experience with SlimeVR.
vrc_config-page-help = Can't find the settings?
vrc_config-page-help-desc = Check out our <a>documentation on this topic!</a>
vrc_config-page-big_menu = Tracking & IK (Big Menu)
vrc_config-page-big_menu-desc = Settings related to IK in the big settings menu
vrc_config-page-wrist_menu = Tracking & IK (Wrist Menu)
vrc_config-page-wrist_menu-desc = Settings related to IK in small settings menu (wrist menu)
vrc_config-on = On
vrc_config-off = Off
vrc_config-invalid = You have misconfigured VRChat settings!
vrc_config-show_more = Show more
vrc_config-setting_name = VRChat Setting name
vrc_config-recommended_value = Recommended Value
vrc_config-current_value = Current Value
vrc_config-mute = Mute Warning
vrc_config-mute-btn = Mute
vrc_config-unmute-btn = Unmute
vrc_config-legacy_mode = Use Legacy IK Solving
vrc_config-disable_shoulder_tracking = Disable Shoulder Tracking
vrc_config-shoulder_width_compensation = Shoulder Width Compensation
vrc_config-spine_mode = FBT Spine Mode
vrc_config-tracker_model = FBT Tracker Model
vrc_config-avatar_measurement_type = Avatar Measurement
vrc_config-calibration_range = Calibration Range
vrc_config-calibration_visuals = Display Calibration Visuals
vrc_config-user_height = User Real Height

vrc_config-spine_mode-UNKNOWN = Unknown
vrc_config-spine_mode-LOCK_BOTH = Lock Both
vrc_config-spine_mode-LOCK_HEAD = Lock Head
vrc_config-spine_mode-LOCK_HIP = Lock Hip

vrc_config-tracker_model-UNKNOWN = Unknown
vrc_config-tracker_model-AXIS = Axis
vrc_config-tracker_model-BOX = Box
vrc_config-tracker_model-SPHERE = Sphere
vrc_config-tracker_model-SYSTEM = System

vrc_config-avatar_measurement_type-UNKNOWN = Unknown
vrc_config-avatar_measurement_type-HEIGHT = Height
vrc_config-avatar_measurement_type-ARM_SPAN = Arm Span

## Error collection consent modal
error_collection_modal-title = Can we collect errors?
error_collection_modal-description_v2 = { settings-interface-behavior-error_tracking-description_v2 }

    You can change this setting later in the Behavior section of the settings page.
error_collection_modal-confirm = I agree
error_collection_modal-cancel = I don't want to
