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
websocket-report-question = Help us fix this by sending an error report.
websocket-report-send = Send report
websocket-report-sent = Report sent. Thank you!
websocket-error-close = Exit SlimeVR
websocket-error-logs = Open the logs folder

## Update notification
version_update-title = New version available: { $version }
version_update-description = Clicking "{ version_update-update }" will download the SlimeVR installer for you.
version_update-update = Update
version_update-close = Close

## Tips
tips-find_tracker = Not sure which tracker is which? Shake a tracker and it will highlight the corresponding item.
tips-do_not_move_heels = Ensure your heels do not move during recording!
tips-file_select = Drag & drop files to use, or <u>browse</u>.
tips-failed_webgl = Failed to initialize WebGL.

## Units
unit-meter = Meter
unit-foot = Foot
unit-inch = Inch
unit-cm = cm


## Dropdown
dropdown_select-all = Select All
dropdown_unselect-all = Unselect All


## Text input
# Accessible name for the eye button that reveals/hides a typed password
input-password-show = Show password
input-password-hide = Hide password


## File input
# Accessible name and tooltip for the icon-only button that clears a picked path
file_input-clear = Clear file
file_input-clear_folder = Clear folder


## Window controls
# Accessible names for the icon-only buttons in the title bar
titlebar-docs = Open documentation
titlebar-search = Search
titlebar-settings = Open settings
titlebar-update = Download update
titlebar-minimize = Minimize
titlebar-maximize = Maximize
titlebar-close = Close
titlebar-profiles = Profiles
titlebar-profiles-tooltip = Switch settings and user profiles
titlebar-profiles-settings = Settings
titlebar-profiles-user = User
titlebar-profiles-manage = Manage profiles


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
body_part-LOWER_CHEST = Lower chest
body_part-UPPER_WAIST = Upper waist
body_part-LOWER_WAIST = Lower waist
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
body_part-LEFT_BIG_TOE = Left Big Toe
body_part-LEFT_INDEX_TOE = Left Index Toe
body_part-LEFT_MIDDLE_TOE = Left Middle Toe
body_part-LEFT_RING_TOE = Left Ring Toe
body_part-LEFT_LITTLE_TOE = Left Little Toe
body_part-RIGHT_TOES = Right Toes
body_part-RIGHT_BIG_TOE = Right Big Toe
body_part-RIGHT_INDEX_TOE = Right Index Toe
body_part-RIGHT_MIDDLE_TOE = Right Middle Toe
body_part-RIGHT_RING_TOE = Right Ring Toe
body_part-RIGHT_LITTLE_TOE = Right Little Toe

## Proportions
skeleton_bone-NONE = None
skeleton_bone-NECK = Neck Length
skeleton_bone-NECK-desc =
    This is the distance from the middle of your head to the base of your neck.
    To adjust it, move your head up and down as if you're nodding or tilt your head
    to the left and right and modify it until any movement in other trackers is negligible.
skeleton_bone-torso_group = Torso Length
skeleton_bone-torso_group-desc =
    This is the distance from the base of your neck to your hips.
    To adjust it, modify it standing up straight until your virtual hips line
    up with your real ones.
skeleton_bone-UPPER_CHEST = Upper Chest Length
skeleton_bone-UPPER_CHEST-desc =
    This is the distance from the base of your neck to the middle of your chest.
    To adjust it, adjust "{ skeleton_bone-torso_group }" properly first, then modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches with your real one.
skeleton_bone-LOWER_CHEST = Lower Chest Length
skeleton_bone-LOWER_CHEST-desc =
    This is the distance from the middle of your chest to the middle of your spine.
    To adjust it, adjust "{ skeleton_bone-torso_group }" properly first, then modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches with your real one.
skeleton_bone-UPPER_WAIST = Upper Waist Length
skeleton_bone-UPPER_WAIST-desc =
    This is the distance from the middle of your spine to a bit above your belly button.
    To adjust it, adjust "{ skeleton_bone-torso_group }" properly first, then modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches with your real one.
skeleton_bone-LOWER_WAIST = Lower Waist Length
skeleton_bone-LOWER_WAIST-desc =
    This is the distance from a bit above your belly button to a bit below it.
    To adjust it, adjust "{ skeleton_bone-torso_group }" properly first, then modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches with your real one.
skeleton_bone-HIP = Hip Length
skeleton_bone-HIP-desc =
    This is the distance from a bit below your belly button to your hips.
    To adjust it, set "{ skeleton_bone-torso_group }" properly first, then modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches your real one.
skeleton_bone-HIPS_WIDTH = Hips Width
skeleton_bone-HIPS_WIDTH-desc =
    This is the distance between the start of your legs.
    To adjust it, perform a full reset with your legs straight and modify it until
    your virtual legs match up with your real ones horizontally.
skeleton_bone-leg_group = Leg Length
skeleton_bone-leg_group-desc =
    This is the distance from your hips to your feet.
    To adjust it, adjust "{ skeleton_bone-torso_group }" properly first, then modify it
    until your virtual feet are at the same level as your real ones.
skeleton_bone-UPPER_LEG = Upper Leg Length
skeleton_bone-UPPER_LEG-desc =
    This is the distance from your hips to your knees.
    To adjust it, adjust "{ skeleton_bone-leg_group }" properly first, then modify it
    until your virtual knees are at the same level as your real ones.
skeleton_bone-LOWER_LEG = Lower Leg Length
skeleton_bone-LOWER_LEG-desc =
    This is the distance from your knees to your ankles.
    To adjust it, adjust "{ skeleton_bone-leg_group }" properly first, then modify it
    until your virtual knees are at the same level as your real ones.
skeleton_bone-FOOT_LENGTH = Foot Length
skeleton_bone-FOOT_LENGTH-desc =
    This is the distance from your ankles to your toes.
    To adjust it, tiptoe and modify it until your virtual feet stay in place.
skeleton_bone-FOOT_SHIFT = Foot Shift
skeleton_bone-FOOT_SHIFT-desc =
    This value is the horizontal distance from your knee to your ankle.
    It moves the anchor of your feet to account for different armatures.
    To adjust it, tilt a foot down after calibrating in-game. If your leg goes down when you do,
    increase it; if your leg goes up, decrease it. Make sure to re-calibrate after changing it.
skeleton_bone-SHOULDERS_DISTANCE = Shoulders Distance
skeleton_bone-SHOULDERS_DISTANCE-desc =
    This is the vertical distance from the base of your neck to your shoulders.
    To adjust it, set "{ skeleton_bone-UPPER_ARM }" to 0 and modify it until your virtual elbow trackers
    line up vertically with your real shoulders.
skeleton_bone-SHOULDERS_WIDTH = Shoulders Width
skeleton_bone-SHOULDERS_WIDTH-desc =
    This is the horizontal distance between your shoulders.
    To adjust it, set "{ skeleton_bone-UPPER_ARM }" to 0 and modify it until your virtual elbow trackers
    line up horizontally with your real shoulders.
skeleton_bone-arm_group = Arm Length
skeleton_bone-arm_group-desc =
    This is the distance from your shoulders to your wrists.
    To adjust it, adjust "{ skeleton_bone-SHOULDERS_DISTANCE }" properly first, set "{ skeleton_bone-HAND }"
    to 0 then modify it until your hand trackers line up with your wrists.
skeleton_bone-UPPER_ARM = Upper Arm Length
skeleton_bone-UPPER_ARM-desc =
    This is the distance from your shoulders to your elbows.
    To adjust it, adjust "{ skeleton_bone-arm_group }" properly first, then modify it until
    your elbow trackers line up with your real elbows.
skeleton_bone-LOWER_ARM = Lower Arm Length
skeleton_bone-LOWER_ARM-desc =
    This is the distance from your elbows to your wrists.
    To adjust it, adjust "{ skeleton_bone-arm_group }" properly first, then modify it until
    your elbow trackers line up with your real elbows.
skeleton_bone-HAND = Hand Length
skeleton_bone-HAND-desc =
    This is the distance from your wrists to your knuckles.
    To adjust it, adjust "{ skeleton_bone-arm_group }" properly first, then modify it until your
    hand trackers line up vertically with your knuckles.

## Tracker reset buttons
reset-reset_all = Reset all proportions
reset-reset_all_warning-reset = Reset proportions
reset-reset_all_warning-cancel = Cancel

reset-full = Full Reset
reset-mounting = Mounting Calibration
reset-mounting-feet = Feet Calibration
reset-mounting-toes = Toes Calibration
reset-mounting-fingers = Fingers Calibration
reset-yaw = Yaw Reset
reset-error-mounting-need_full_reset = Need a full reset before mounting
reset-error-yaw-need_full_reset = Need a full reset before yaw reset
reset-error-need_positional_head = Step mounting needs a VR headset or a positional tracker
reset-error-no_trackers = No tracker available
reset-error-no_feet_tracker = No feet tracker assigned / available

## Navigation bar
navbar-home = Home
navbar-body_proportions = Body Proportions
navbar-trackers_assign = Tracker Assignment
navbar-mounting = Mounting Calibration
navbar-onboarding = Setup Wizard
navbar-settings = Settings
navbar-connect_trackers = Connect Trackers

## Biovision hierarchy recording
bvh-start_recording = Record BVH
bvh-stop_recording = Save BVH recording
bvh-recording = Recording...
bvh-save_title = Save BVH recording

## Tracking pause
tracking-unpaused = Pause tracking
tracking-paused = Unpause tracking

## Widget: Developer settings
widget-developer_mode = Developer Mode
widget-developer_mode-high_contrast = High contrast
widget-developer_mode-precise_rotation = Precise rotation
widget-developer_mode-fast_data_feed = Fast data feed
widget-developer_mode-raw_slime_rotation = Raw rotation

## Widget: IMU Visualizer
widget-imu_visualizer = Tracking data
widget-imu_visualizer-preview = Preview
widget-imu_visualizer-hide = Hide
widget-imu_visualizer-rotation_raw = Raw rotation
widget-imu_visualizer-rotation_preview = Preview rotation
widget-imu_visualizer-acceleration = Acceleration
widget-imu_visualizer-position = Position
widget-imu_visualizer-stay_aligned = Stay Aligned

## Tracker status
tracker-status-none = No Status
tracker-status-busy = Busy
tracker-status-error = Error
tracker-status-disconnected = Disconnected
tracker-status-occluded = Occluded
tracker-status-ok = OK
tracker-status-timed_out = Timed out
tracker-status-sleeping = Sleeping

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
tracker-rotation-mounting_reset = (using mounting reset orientation)

## Tracker information
tracker-infos-manufacturer = Manufacturer
tracker-infos-display_name = Display Name
tracker-infos-custom_name = Custom Name
tracker-infos-url = Tracker URL
tracker-infos-hardware_identifier = Hardware ID
tracker-infos-mac_address = MAC address
tracker-infos-data_type = Data type
tracker-infos-imu = IMU Sensor
tracker-infos-board_type = Main board
tracker-infos-network_version = Protocol Version
tracker-infos-magnetometer = Magnetometer
tracker-infos-magnetometer-status-v1 = { $status ->
    *[NOT_SUPPORTED] Not supported
    [DISABLED] Disabled
    [ENABLED] Enabled
}

tracker-infos-packet_loss = Packet Loss
tracker-infos-packets_lost = Packets Lost
tracker-infos-packets_received = Packets Received

## Tracker settings
tracker-settings-back = Go back to trackers list
tracker-settings-title = Tracker settings
tracker-settings-assignment_section = Assignment
tracker-settings-assignment_section-description = What part of the body the tracker is assigned to.
tracker-settings-assignment_section-edit = Edit assignment
tracker-settings-mounting_section = Mounting orientation
tracker-settings-mounting_section-description = Where is the tracker mounted?
tracker-settings-mounting_section-edit = Edit mounting
tracker-settings-use_mag = Allow magnetometer on this tracker
# Multiline!
tracker-settings-use_mag-description =
    Should this tracker use its magnetometer to reduce drift when magnetometer usage is allowed? <b>Do not shut down your tracker while toggling this!</b>

    You need to allow magnetometer usage first, <magSetting>click here to go to the setting</magSetting>.
tracker-settings-use_mag-label = Allow magnetometer
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-bone_offset_section = Bone offset
tracker-settings-bone_offset_section-description = Offset of the tracker from its bone, in meters.
tracker-settings-bone_offset_section-x = X
tracker-settings-bone_offset_section-y = Y
tracker-settings-bone_offset_section-z = Z
tracker-settings-name_section = Tracker name
tracker-settings-name_section-description = Give it a cute nickname :3
tracker-settings-name_section-placeholder = NightyBeast's left leg
tracker-settings-name_section-label = Tracker name
tracker-settings-forget = Forget tracker
tracker-settings-forget-description = Disconnects the tracker and removes it from SlimeVR, along with its saved settings (body part, mounting, name). If it's still on, it shows up under new trackers on the home page so you can add it back.
tracker-settings-forget-label = Forget tracker
tracker-settings-forget-modal-title = Forget this tracker?
tracker-settings-forget-modal-cancel = Cancel
tracker-settings-forget-modal-confirm = Forget tracker
tracker-settings-imu_graphs-title = Live tracker data
tracker-settings-imu_graphs-raw_accel = Raw acceleration
tracker-settings-imu_graphs-linear_accel = Linear acceleration
tracker-settings-imu_graphs-raw_rotation = Raw rotation
tracker-settings-imu_graphs-rotation = Rotation
tracker-settings-imu_graphs-live-pause = Pause live feed
tracker-settings-imu_graphs-live-resume = Resume live feed
tracker-settings-update-unavailable-v2 = No releases found
tracker-settings-update-incompatible = Cannot update. Incompatible board or firmware version
tracker-settings-update-low-battery = Cannot update. Battery lower than 50%
tracker-settings-update-up_to_date = Up to date
tracker-settings-update-blocked = Update not available; no other release available.
tracker-settings-update = Update now
tracker-settings-update-title = Firmware version
tracker-settings-current-version = Current
tracker-settings-latest-version = Latest
tracker-settings-build-date = Build Date

## Dongle settings
dongle-infos-model = Model
dongle-infos-hardware_revision = Hardware Revision
dongle-status-connected = Connected
dongle-status-disconnected = Disconnected
dongle-settings-back = Go back to trackers list
dongle-settings-title = Dongle settings
dongle-settings-name_section = Dongle name
dongle-settings-name_section-description = Give it a cute nickname :3
dongle-settings-name_section-placeholder = Futura's usb thingie
dongle-settings-update = Update now
dongle-settings-update-title = Firmware version
dongle-settings-paired_trackers = Paired Trackers
dongle-settings-paired_trackers-empty = No trackers paired to this dongle yet.
dongle-settings-pair = Pair Tracker
dongle-settings-forget_tracker = Forget
dongle-settings-telemetry-title = Live telemetry
dongle-settings-telemetry-select_trackers = Select trackers
dongle-settings-telemetry-select_trackers-summary = { $count } of { $total } trackers
dongle-settings-telemetry-show_min_max = Min/Max
dongle-settings-telemetry-chart_rssi = RSSI · dBm
dongle-settings-telemetry-chart_loss = Packet loss · %
dongle-settings-telemetry-chart_gaps = Loss gap events · TPS-relative packet gaps
dongle-settings-telemetry-footnote = Loss gap events mark regions where packet updates were delayed relative to the tracker's TPS rate. Darker and wider marks indicate longer drop durations.
# Accessible name and tooltip for the icon-only button that pauses/resumes the live telemetry feed
dongle-settings-telemetry-live-pause = Pause live feed
dongle-settings-telemetry-live-resume = Resume live feed


## Tracker part card info
tracker-part_card-unassigned = Unassigned

## Body assignment menu
body_assignment_menu = Where do you want this tracker to be?
body_assignment_menu-description = Choose a location where you want this tracker to be assigned. Alternatively, you can choose to manage all trackers at once instead of one by one.
body_assignment_menu-manage_trackers = Manage all trackers
body_assignment_menu-unassign_tracker = Unassign tracker

## Tracker assignment menu
# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Warning:</b> A neck tracker can be deadly if adjusted too tightly;
    the strap could cut off circulation to your head!
tracker_selection_menu-neck_warning-done = I understand the risks
tracker_selection_menu-neck_warning-cancel = Cancel

## Mounting menu
mounting_selection_menu = Which way is this tracker facing?
mounting_selection_menu-close = Close

## Sidebar settings
settings-advanced_section = Advanced settings
settings-sidebar-profiles = Profiles
settings-profiles-settings = Settings profiles
settings-profiles-settings-description = Your setup: trackers, outputs, resets, keybinds and every other setting. Switching applies it right away.
settings-profiles-user = User profiles
settings-profiles-user-description = The person wearing the trackers: height and body proportions.
settings-profiles-new_settings = New profile
settings-profiles-new_user = New profile
settings-profiles-default_name = Profile { $n }
settings-profiles-start_from = Start from
settings-profiles-start_from-defaults = Defaults
settings-profiles-start_from-copy = Copy of { $name }
settings-profiles-name = Profile name
settings-profiles-create = Create
settings-profiles-save = Save
settings-profiles-cancel = Cancel
settings-profiles-rename = Rename
settings-profiles-duplicate = Duplicate
settings-profiles-delete = Delete
settings-profiles-delete-active = Select another profile to delete this one
settings-profiles-delete_action = Delete
settings-profiles-delete_confirm = Delete "{ $name }"? This can't be undone.
settings-profiles-copy_name = { $name } copy
settings-profiles-error-invalid_kind = The profile type is missing.
settings-profiles-error-invalid_name = Names can only contain letters, numbers, spaces, - and _, up to 64 characters.
settings-profiles-error-already_exists = A profile with this name already exists.
settings-profiles-error-not_found = This profile doesn't exist anymore.
settings-profiles-error-is_active = The selected profile can't be deleted. Select another one first.
settings-profiles-error-failed = The server couldn't save the profile files.
settings-sidebar-search = Search settings and pages
settings-sidebar-search-empty = Nothing matches your search
settings-sidebar-search-hint = Type to search every setting and page
settings-sidebar-search-navigate = Navigate
settings-sidebar-search-open = Open
settings-sidebar-search-close = Close
settings-search-section-page = Page
settings-search-manual_proportions = Manual Proportions
settings-sidebar-general = General
settings-sidebar-outputs = Outputs
settings-sidebar-routing = Bone Routing
settings-sidebar-driver = SteamVR / Monado
settings-sidebar-resets = Resets
settings-sidebar-stay_aligned = Stay Aligned
settings-sidebar-tracking = Tracking
settings-sidebar-trackers = Trackers
settings-sidebar-interface = Interface
settings-sidebar-vrchat_osc = VRChat OSC
settings-sidebar-osc_vmc = VMC
settings-sidebar-utils = Utilities
settings-sidebar-serial = USB Serial Console
settings-sidebar-logs = Logs
settings-sidebar-appearance = Appearance
settings-sidebar-home = Home Screen
settings-sidebar-checklist = Tracking checklist
settings-sidebar-behavior = Behaviour
settings-sidebar-firmware-tool = DIY Firmware Tool
settings-sidebar-vrc_warnings = VRChat Config Warnings
settings-sidebar-advanced = Advanced

## Bone routing settings
settings-routing = Bone Routing
settings-routing-description =
    Choose what each output sends.
    SteamVR/Monado, VRChat OSC and VMC can each get a different set of body parts.
settings-routing-mode = Routing mode
settings-routing-automatic-label = Automatic routing
settings-routing-automatic-description = Sends each bone to the best output so nothing is sent twice. Turn it off to choose the outputs for every bone yourself.
settings-routing-bone = Bone
settings-routing-cell-required = Mandatory for this output
settings-routing-cell-unavailable = Output is unavailable on this platform
settings-routing-cell-duplicate = Bone is routed to multiple outputs, which may show duplicate trackers in applications
settings-routing-duplicate-warning =
    { $bones } { $boneCount ->
        [one] is
        *[many] are
    } routed to multiple outputs ({ $outputs }), which may show duplicate trackers in applications.
settings-routing-outputs = Outputs
settings-routing-bones = Bones
settings-routing-bones-description = Turn off Automatic to pick the bones for each output yourself.
settings-routing-legend-sent = Sent
settings-routing-legend-not_sent = Supported, not sent
settings-routing-legend-unsupported = Not supported
settings-routing-legend-editable = Can be toggled
settings-routing-output-driver = SteamVR / Monado
settings-routing-output-vrc_osc = VRChat OSC
settings-routing-output-vmc = VMC
settings-routing-output-bone-count = { $routed } of { $accepts } bones routed
settings-routing-output-badge-sending = Sending
settings-routing-output-sending-description = Connected and receiving bones.
settings-routing-output-badge-stopped = Not running
settings-routing-output-stopped-description = SteamVR or Monado driver not connected.
settings-routing-output-badge-idle = Idle
settings-routing-output-idle-description = Enabled, but a remote endpoint has not been found.
settings-routing-output-badge-off = Off
settings-routing-output-off-description = Disabled in its settings.
settings-routing-output-badge-empty = Nothing routed
settings-routing-output-empty-description = Enabled, but no bones have been routed to it.
settings-routing-output-badge-unavailable = Unavailable
settings-routing-output-unavailable-description = Output is unavailable on this platform.
settings-routing-group-spine = Spine & torso
settings-routing-group-legs = Legs
settings-routing-group-arms = Arms
settings-routing-group-fingers = Fingers
settings-routing-row-left_fingers = Left fingers
settings-routing-row-right_fingers = Right fingers
settings-routing-group-toes = Toes
settings-routing-row-left_toes = Left toes
settings-routing-row-right_toes = Right toes
settings-routing-hands-warning = <b>Warning:</b> Routing your hands to SteamVR/Monado replaces the input from your real controllers.
    Only do this if you want SlimeVR trackers to track your hands.

    Route hands there anyway?
settings-routing-hands-warning-cancel = Cancel
settings-routing-hands-warning-done = Route hands

## SteamVR / Monado output settings

settings-driver = SteamVR / Monado
settings-driver-description = Settings for the SlimeVR driver, used by SteamVR and Monado alike.
settings-driver-output_group = Output
settings-driver-data_group = Data sent
settings-driver-enable = Enable
settings-driver-enable-description = Send your trackers to and receive trackers from SteamVR or Monado through the SlimeVR driver. If this is off, they won't be able to connect to SlimeVR at all.
settings-driver-status-title = Status
settings-driver-status-connection = Driver connection
settings-driver-status-badge-connected = Connected
settings-driver-status-badge-waiting = Waiting
settings-driver-status-badge-disabled = Off
settings-driver-status-badge-unavailable = Unavailable
settings-driver-status-connection-connected = A driver is connected and receiving your trackers.
settings-driver-status-connection-waiting = Waiting for SteamVR or Monado to connect through the SlimeVR driver.
settings-driver-status-connection-disabled = Turned off, so nothing can connect.
settings-driver-status-connection-unavailable = This output does not exist on this platform.
settings-driver-bones = Bones sent
settings-driver-bones-description = Which bones this output sends is configured in Bone Routing.
settings-driver-bones-link = Open Bone Routing
settings-driver-velocity = Send velocity
settings-driver-velocity-description = Send linear and angular velocity data to the driver. Allows SteamVR/Monado tracker pose prediction and compatibility with applications that expect this data.

## Tracker mechanics
settings-general-mounting_method = Mounting calibration method
settings-general-mounting_method-pose_only = Only used by pose mounting calibration.
settings-general-mounting_method-description = What mounting calibration method to use to calibrate your trackers' mounting orientations.
settings-general-tracker_mechanics-filtering = Filtering
settings-general-tracker_mechanics-filtering-type = Filtering type
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Choose the filtering type for your trackers.
    "{ settings-general-tracker_mechanics-filtering-type-smoothing }" makes movement smoother while "{ settings-general-tracker_mechanics-filtering-type-prediction }" minimizes latency.
settings-general-tracker_mechanics-filtering-type-none = No filtering
settings-general-tracker_mechanics-filtering-type-none-description = Use rotations as is. Will not do any filtering.
settings-general-tracker_mechanics-filtering-type-smoothing = Smoothing
settings-general-tracker_mechanics-filtering-type-smoothing-description = Smooths movements but adds some latency.
settings-general-tracker_mechanics-filtering-type-prediction = Prediction
settings-general-tracker_mechanics-filtering-type-prediction-description = Reduces latency and makes movements snappier, but increases jitter.
settings-general-tracker_mechanics-filtering-amount = Filter strength
settings-general-tracker_mechanics-filtering-amount-description = How strong the selected filter is. Higher values increase smoothing or prediction while lower ones make the effect more subtle.
settings-general-tracker_mechanics-filtering-amount-no_filtering = Pick a filtering type to set its strength.
settings-general-fk_settings-reset_behaviour_group = Reset behaviour
settings-general-tracker_mechanics-yaw-reset-smooth-time = Yaw reset smooth time
settings-general-tracker_mechanics-yaw-reset-smooth-time-description = Smooths the trackers' rotations upon doing a yaw reset. 0s disables smoothing.
settings-general-tracker_mechanics-save_mounting_reset = Save automatic mounting calibration
settings-general-tracker_mechanics-save_mounting_reset-description =
    Saves the automatic mounting calibration for the trackers between restarts. Useful
    when wearing a suit where trackers don't move between sessions. <b>Not recommended for normal users!</b>
settings-general-tracker_mechanics-magnetometer_group = Magnetometer
settings-general-tracker_mechanics-connection_group = Connection
settings-general-tracker_mechanics-use_mag_on_all_trackers = Use magnetometer on all IMU trackers that support it
settings-general-tracker_mechanics-use_mag_on_all_trackers-description =
    Uses the magnetometer on all trackers that have a compatible firmware for it, reducing drift in stable magnetic environments.
    Can be disabled per tracker in the tracker's settings. <b>Please don't shut down any of the trackers while toggling this!</b>
settings-general-tracker_mechanics-trackers_over_usb = Trackers over USB
settings-general-tracker_mechanics-trackers_over_usb-description =
    Enables receiving HID tracker data over USB. Make sure connected trackers have <b>connection over HID</b> enabled!
settings-general-tracker_mechanics-timeout_delay = Timeout delay
settings-general-tracker_mechanics-timeout_delay-description = How long until a tracker is marked as '{ tracker-status-disconnected }' when the connection is lost.

settings-stay_aligned-description = Stay Aligned reduces trackers' IMU drift. Trackers that stay still are locked in place, with no setup. To also correct trackers while you move, record the poses you use with them.
settings-stay_aligned-enabled-label = Enable Stay Aligned
settings-stay_aligned-enabled-label-description = Turn Stay Aligned on or off for all your trackers.
settings-stay_aligned-status_group = Status
settings-stay_aligned-status-correction = Correction
settings-stay_aligned-status-position = Current detected position
settings-stay_aligned-position-unknown = Not recognized
settings-stay_aligned-position-standing = Standing
settings-stay_aligned-position-sitting = Sitting
settings-stay_aligned-position-flat = On the floor
settings-stay_aligned-position-kneeling = Kneeling
settings-stay_aligned-trackers-missing = No tracker for: { $parts }.
settings-stay_aligned-status-badge-disabled = Off
settings-stay_aligned-status-badge-correcting = Correcting
settings-stay_aligned-status-badge-not_correcting = Not correcting
settings-stay_aligned-status-text-disabled = Enable Stay Aligned to correct your trackers.
settings-stay_aligned-status-text-correcting = Moving trackers are corrected using the pose you are in.
settings-stay_aligned-status-text-pose_unusable = Moving trackers are not corrected in this pose.
settings-stay_aligned-status-pose-name = { $pose }.
settings-stay_aligned-status-badge-pose-ready = Ready
settings-stay_aligned-status-badge-pose-not_recorded = Not recorded
settings-stay_aligned-status-badge-pose-disabled = Turned off
settings-stay_aligned-status-badge-pose-unknown = Unrecognized
settings-stay_aligned-status-pose-ready = Moving trackers can be corrected with this pose.
settings-stay_aligned-status-pose-not_recorded = Record this pose so moving trackers can be corrected in it.
settings-stay_aligned-status-pose-disabled = This pose is turned off, so moving trackers are not corrected in it.
settings-stay_aligned-status-pose-unknown = Your position does not match any pose, so moving trackers are not corrected.
settings-stay_aligned-status-largest_correction = Largest correction
settings-stay_aligned-status-largest_correction-part = { $part }, since your last reset.
settings-stay_aligned-status-largest_correction-none = No tracker was corrected since your last reset.
settings-stay_aligned-status-largest_correction-off = Nothing is corrected while Stay Aligned is off.
settings-stay_aligned-poses_group = Relaxed poses
settings-stay_aligned-poses_group-description = Stay Aligned needs to know how your legs rest in each pose you play in. Record the ones you use. Only your legs and torso are used; your arms and hands can be anywhere.
settings-stay_aligned-pose-standing = Standing
settings-stay_aligned-pose-standing-description = Stand relaxed with your legs in their natural stance.
settings-stay_aligned-pose-sitting = Sitting in a chair
settings-stay_aligned-pose-sitting-description = Sit relaxed in a chair, feet touching the floor.
settings-stay_aligned-pose-flat = Sitting on the floor or lying on your back
settings-stay_aligned-pose-flat-description = Sit relaxed on the floor with your legs in front of you.
settings-stay_aligned-badge-recorded = Recorded
settings-stay_aligned-badge-not_recorded = Not recorded
settings-stay_aligned-badge-default = Default
settings-stay_aligned-record = Record
settings-stay_aligned-record_again = Record again
settings-stay_aligned-toggle-record_first = Record this pose first
settings-stay_aligned-debug-label = Debugging
settings-stay_aligned-debug-description = Please include your settings when reporting problems about Stay Aligned.
settings-stay_aligned-debug-copy-label = Copy settings to clipboard
settings-stay_aligned-debug-copy-short = Copy

## Stay Aligned pose recorder
settings-stay_aligned-recorder-close = Cancel
settings-stay_aligned-recorder-step-trackers = Trackers
settings-stay_aligned-recorder-step-full_reset = Full reset
settings-stay_aligned-recorder-step-mounting = Mounting calibration
settings-stay_aligned-recorder-step-feet_mounting = Feet calibration
settings-stay_aligned-recorder-step-record = Record pose
settings-stay_aligned-recorder-trackers-intro = Stay Aligned needs a chest, waist or hip tracker plus both thighs and both ankles.
settings-stay_aligned-recorder-trackers-assign = Assign trackers
settings-stay_aligned-recorder-full_reset-intro = Poses are measured from calibrated trackers, so your trackers need a full reset first.
settings-stay_aligned-recorder-mounting-intro = Your leg directions are measured from how the trackers are mounted, so they need calibrating first.
settings-stay_aligned-recorder-feet_mounting-intro = Your foot angles are measured from how the feet trackers are mounted, and your feet have a calibration of their own.
settings-stay_aligned-recorder-arms = Arms and hands can be anywhere.
settings-stay_aligned-recorder-record-step-0 = 1. Press "Start", then get into the pose.
settings-stay_aligned-recorder-record-step-1 = 2. Hold still. The pose is saved as soon as all your trackers are still.
settings-stay_aligned-recorder-start = Start
settings-stay_aligned-recorder-start_over = Start over
settings-stay_aligned-recorder-try_again = Try again
settings-stay_aligned-recorder-redo_preparation = Redo preparation
settings-stay_aligned-recorder-live-hold = Hold the pose and keep your trackers still.
settings-stay_aligned-recorder-live-wrong_pose = Wrong pose.
settings-stay_aligned-recorder-live-detected = Detected: { $position }.
settings-stay_aligned-recorder-live-not_still = Some trackers did not stay still.
settings-stay_aligned-recorder-live-missing = Some trackers are missing.
settings-stay_aligned-recorder-live-not_calibrated = Your trackers need a reset first.
settings-stay_aligned-recorder-part-upper_body = Upper body
settings-stay_aligned-recorder-state-still = Still
settings-stay_aligned-recorder-state-moving = Moving

## Keybinds Page
settings-keybinds-description = Change keybinds for various shortcuts
keybind_config-keybind_name = Keybind
keybind_config-keybind_value = Combination
keybind_config-keybind_delay = Delay (seconds)
settings-keybinds_full-reset = Full reset
settings-keybinds_yaw-reset = Yaw reset
settings-keybinds_mounting-reset = Mounting reset
settings-keybinds_feet-mounting-reset = Feet mounting reset
settings-keybinds_pause-tracking = Pause tracking
settings-keybinds_reset-all-button = Reset all
settings-keybinds-system-managed-description = Your desktop manages global shortcuts itself, so they have to be changed from your system settings.
settings-keybinds-open-system-settings-button = Open system settings
settings-keybinds-system-managed-hint = Set in system settings
settings-keybinds-unsupported-description = Global keybinds are not supported on this platform.
settings-sidebar-keybinds = Keybinds
settings-keybinds-recorder-modal-title = Assign keybind for
settings-keybinds-recorder-modal-unbind-button = Unbind
settings-keybinds-recorder-modal-done-button = Done
settings-keybinds-recorder-modal-cancel-button = Cancel
settings-keybinds-recorder-modal-key-enter = Enter
settings-keybinds-recorder-modal-key-backspace = Backspace
settings-keybinds-recorder-modal-key-escape = Esc
settings-keybinds-already-assigned = Already used by { $name }
settings-keybinds-click-to-record = Click to record shortcut
settings-keybinds-change-shortcut = Click to change shortcut
settings-keybinds-reset-single = Reset keybind to default
settings-reset-single = Reset this setting to its default
settings-keybinds-recorder-hint-recording = Hold the keys you want, then release them
settings-keybinds-error-unsupported-key = That key can't be used in a shortcut
settings-keybinds-error-add-modifier = Hold Ctrl, Alt, or Super with that key


## FK/Tracking settings

# Floor clip:
# Why the name: came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# Definition: prevents the foot trackers from going lower than they were when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Floor clip
# Skating correction:
# Why the name: without this enabled the feet will often slide across the ground as if you're skating across the ground.
#  Since this largely prevents this, it corrects for it hence skating correction.
# Definition: Guesses when each foot is in contact with the ground and uses that information to lock them in place.
settings-general-fk_settings-leg_tweak-skating_correction = Skating correction
settings-general-fk_settings-leg_tweak-toe_snap = Toe snap
settings-general-fk_settings-leg_tweak-foot_plant = Foot plant
settings-general-fk_settings-leg_tweak-skating_correction-amount = Skating correction strength
settings-general-fk_settings-leg_tweak-skating_correction-amount-description = A higher value will result in more aggressive correction at the cost of accuracy during certain movements.
settings-general-fk_settings-leg_tweak-skating_correction-description = Corrects for foot sliding, but can decrease accuracy during certain movements.
settings-general-fk_settings-leg_tweak-floor_clip-description = Prevents feet clipping through the floor by shifting them up when they do.
settings-general-fk_settings-leg_tweak-toe_snap-description = When not using feet trackers, guesses the rotation of your feet when going on your tippy toes.
settings-general-fk_settings-leg_tweak-foot_plant-description = When not using feet trackers, keeps your feet parallel to the ground when they touch it.
settings-general-fk_settings-leg_fk = Leg tracking
settings-general-fk_settings-leg_fk-reset_mounting_feet-v1 = Force feet mounting calibration
settings-general-fk_settings-leg_fk-reset_mounting_feet-description-v1 = Force feet mounting calibration during body mounting calibration.
settings-general-fk_settings-constraints_group = Constraints
settings-general-fk_settings-enforce_joint_constraints = Skeletal limits
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = Prevents joints from rotating past their limit
settings-general-fk_settings-position_group = Position tracking
settings-general-fk_settings-ik = Position data
settings-general-fk_settings-ik-use_position-description = Uses position data from trackers that provide it for IK. When enabling this, make sure to full reset and recalibrate in game.
settings-general-fk_settings-reset_settings-reset_reliable_reference_attitude = Reset HMD attitude
settings-general-fk_settings-reset_settings-reset_reliable_reference_attitude-description = Reset a VR headset's vertical rotation upon doing a full reset. Useful when wearing one on the forehead for VTubing or mocap. Do not enable for VR.
settings-general-fk_settings-arms_mounting_reset_mode = Arms mounting reset mode
settings-general-fk_settings-arms_mounting_reset_mode-description = Change which arm pose is expected for mounting reset.
settings-general-fk_settings-arms_mounting_reset_mode-back = Back
settings-general-fk_settings-arms_mounting_reset_mode-back-description = Expects your upper arms going back and your lower arms going forward. Recommended for VR.
settings-general-fk_settings-arms_mounting_reset_mode-t_pose = T-pose
settings-general-fk_settings-arms_mounting_reset_mode-t_pose-description = Expects your arms to be raised to the sides.
settings-general-fk_settings-arms_mounting_reset_mode-forward = Forward
settings-general-fk_settings-arms_mounting_reset_mode-forward-description = Expects your arms to be raised forward. Recommended for VTubing.
settings-general-fk_settings-skeleton_settings-ratios = Skeleton ratios
settings-general-fk_settings-skeleton_settings-ratios-description = Tweak the behaviour of the skeleton. You may need to adjust your proportions after changing these.
settings-general-fk_settings-skeleton_settings-spine_curvature = Spine curvature
settings-general-fk_settings-skeleton_settings-spine_curvature-description = How much the spine curves when bending instead of remaining straight. Use a lower value for stiffer movements or a higher one for more flexible movements.
settings-general-fk_settings-skeleton_settings-impute_spine_from_upper_to_lower = Estimate missing spine trackers from upper to lower
settings-general-fk_settings-skeleton_settings-impute_spine_from_upper_to_lower-description = For missing spine trackers, how much to rely on the lower trackers instead of the upper trackers. Use a lower value for more hip isolation or a higher one to increase stability lying down.
settings-general-fk_settings-skeleton_settings-interpolate_upper_legs_twist_with_lower_legs = Interpolate the upper legs' twists with the lower legs'
settings-general-fk_settings-skeleton_settings-interpolate_upper_legs_twist_with_lower_legs-description = How much the twist of your upper legs follows your lower legs. Use a lower value for motion capture or a higher one for VR.
settings-general-fk_settings-self_localization-title = Mocap mode
settings-general-fk_settings-self_localization-description = Mocap Mode allows the skeleton to roughly track its own position without any positional tracker.

## Gesture control settings (tracker tapping)
settings-general-gesture_control-subtitle = Tap-based resets
settings-general-gesture_control-description = Allows for resets to be triggered by tapping a tracker. Taps must occur within the time limit of 0.3 seconds times the number of taps to be recognized.
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
settings-general-gesture_control-column-reset = Reset
settings-general-gesture_control-column-tracker = Tracker
settings-general-gesture_control-column-taps = Taps
settings-general-gesture_control-column-delay = Delay
settings-general-gesture_control-yawReset-name = Yaw reset
settings-general-gesture_control-fullReset-name = Full reset
settings-general-gesture_control-mountingReset-name = Mounting calibration
settings-general-gesture_control-yawResetEnabled = Enable tap to yaw reset
settings-general-gesture_control-yawResetDelay = Yaw reset delay
settings-general-gesture_control-yawResetTaps = Taps for yaw reset
settings-general-gesture_control-yawResetTracker = Yaw reset tracker
settings-general-gesture_control-fullResetEnabled = Enable tap to full reset
settings-general-gesture_control-fullResetDelay = Full reset delay
settings-general-gesture_control-fullResetTaps = Taps for full reset
settings-general-gesture_control-fullResetTracker = Full reset tracker
settings-general-gesture_control-mountingResetEnabled = Enable tap to perform mounting calibration
settings-general-gesture_control-mountingResetDelay = Mounting calibration delay
settings-general-gesture_control-mountingResetTaps = Taps for mounting calibration
settings-general-gesture_control-mountingResetTracker = Mounting reset tracker
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = Trackers over threshold
settings-general-gesture_control-numberTrackersOverThreshold-description = Increase this value if tap detection is not working. Do not increase it above what is needed to make it work in order to prevent false positives.

## Appearance settings
settings-interface-appearance = Appearance
settings-interface-appearance-text_group = Text
settings-interface-appearance-language_group = Language
settings-utils-advanced-developer_group = Developer
settings-general-interface-dev_mode = Developer mode
settings-general-interface-dev_mode-description = This mode can be useful if you need in-depth data or to interact with connected trackers on a more advanced level.
settings-general-interface-theme = Colour theme
settings-general-interface-theme-description = The colours used across the whole interface.
settings-general-interface-lang = Select language
settings-general-interface-lang-description = Change the default language.
settings-general-interface-lang-placeholder = Select the language to use
settings-general-interface-crowdin_in_context = Help translate
settings-general-interface-crowdin_in_context-description = Switches the app to an editable view where every piece of text is a link to Crowdin's in-context editor. Requires a Crowdin account.
settings-general-interface-crowdin_in_context-description-active = In-context translation is on. Use the button below to leave it.
settings-general-interface-crowdin_in_context-failed = Couldn't reach Crowdin. Check your connection and try again.
settings-general-interface-crowdin_in_context-enable = Open in-context translation
settings-general-interface-crowdin_in_context-close = Close in-context translation
# Keep the font name untranslated
settings-interface-appearance-font = GUI font
settings-interface-appearance-font-description = This changes the font used by the interface.
settings-interface-appearance-font-placeholder = Default font
settings-interface-appearance-font-os_font = OS font
settings-interface-appearance-font-slime_font = Default font
settings-interface-appearance-font_size = Base font scaling
settings-interface-appearance-font_size-description = This affects the font size of the whole interface except this settings panel.

## Notification settings
settings-general-interface-feedback_sound = Feedback sound
settings-general-interface-feedback_sound-description = This option plays a sound when a reset is triggered.
settings-general-interface-feedback_sound-volume = Feedback sound volume
settings-general-interface-feedback_sound-volume-description = How loud the reset sound is.
settings-general-interface-connected_trackers_warning = Connected trackers warning
settings-general-interface-connected_trackers_warning-description = This option will show a pop-up every time you try exiting SlimeVR while having one or more connected trackers. It reminds you to turn off your trackers when you are done to preserve battery life.

## Behavior settings
settings-interface-behavior = Behaviour
settings-interface-notifications = Notifications
settings-interface-behavior-desktop_group = Desktop
settings-interface-behavior-input_group = Input
settings-interface-behavior-privacy_group = Privacy
settings-general-interface-use_tray = Minimize to system tray
settings-general-interface-use_tray-description = Lets you close the window without closing the SlimeVR Server so you can continue using it without having the GUI bother you.
settings-general-interface-discord_presence = Share activity on Discord
settings-general-interface-discord_presence-description = Tells your Discord client that you are using SlimeVR along with the number of IMU trackers you are using.
settings-general-interface-discord_presence-message = { $amount ->
    [0] Sliming around
    [one] Using 1 tracker
    *[other] Using { $amount } trackers
}
settings-interface-behavior-error_tracking = Error collection via Sentry.io
settings-interface-behavior-error_tracking-label = Send errors to developers
settings-interface-behavior-error_tracking-summary = Share error reports and diagnostic data so bugs can be found and fixed.
settings-interface-behavior-error_tracking-no_personal_data = <b>We do not collect personal information</b> such as your IP address or wireless credentials. SlimeVR values your privacy!
settings-interface-behavior-error_tracking-collects = To provide the best user experience, we collect:
settings-interface-behavior-error_tracking-collects-errors = Error reports and performance metrics
settings-interface-behavior-error_tracking-collects-system = Operating system and tracker hardware information
settings-interface-behavior-error_tracking-collects-recordings = Recordings of the SlimeVR interface (text fields are hidden)
settings-interface-behavior-error_tracking-collects-usage = Which features you use
settings-interface-behavior-error_tracking-collects-proportions = Your body proportions, to help us improve automatic proportions
settings-interface-behavior-error_tracking-footer = This data is linked to a random ID, not to your identity, and helps us detect bugs and issues with SlimeVR. It is collected via Sentry.io.

settings-interface-behavior-bvh_directory = Directory to save BVH recordings
settings-interface-behavior-bvh_directory-description = Choose a directory to save your BVH recordings instead of having to choose where to save them each time.
settings-interface-behavior-bvh_directory-label = Directory for BVH recordings
settings-interface-behavior-skeleton_mesh = Skeleton preview style
settings-interface-behavior-skeleton_mesh-description = Draw the body preview with solid 3D shapes instead of simple lines. Turn this off on low-end machines.
settings-interface-behavior-skeleton_mesh-label = Use 3D shapes
settings-interface-behavior-skeleton_mesh-lines = Lines
settings-interface-behavior-skeleton_mesh-mesh = 3D shapes
settings-interface-behavior-controller_nav = Game controller navigation
settings-interface-behavior-controller_nav-description = Move focus around the interface with a game controller: the D-pad or left stick move between controls, A activates, B goes back, only while a controller is connected. Holding Ctrl or Alt with the arrow keys always moves focus the same way.

## Serial settings
settings-serial = USB Serial Console
# This cares about multilines
settings-serial-description =
    This is a live information feed for serial communication via USB.
    May be useful to debug firmware or hardware issues.
settings-serial-connection_lost = Connection to serial lost. Reconnecting...
settings-serial-busy = The port is in use by a firmware update, the console will resume afterwards
settings-serial-opening = Opening the serial port...
settings-serial-open_failed = Could not open the serial port. Check that no other program is using it and that you have permission to access it.
settings-serial-no_port = No serial device selected
settings-serial-reboot = Reboot
settings-serial-factory_reset = Factory Reset
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>Warning:</b> This will reset the tracker to factory settings.
    Which means Wi-Fi and calibration settings <b>will all be lost!</b>
settings-serial-factory_reset-warning-ok = I know what I'm doing
settings-serial-factory_reset-warning-cancel = Cancel
settings-serial-serial_select = Select a serial port
settings-serial-get_wifi_scan = Get Wi-Fi Scan
settings-serial-enter_pairing = Enter Pairing
settings-serial-exit_pairing = Exit Pairing
settings-serial-calibrate = Calibrate
settings-serial-six_side_calibrate = 6-Side Calibrate
settings-serial-dfu = Enter DFU
settings-serial-meow = Meow!
settings-serial-save_logs = Save To File
settings-serial-send_command = Send
settings-serial-send_command-placeholder = Command...
settings-serial-send_command-warning =
    <b>Warning:</b> Running serial commands can lead to data loss or brick the trackers.
settings-serial-send_command-warning-ok = I know what I'm doing
settings-serial-send_command-warning-cancel = Cancel

## Logs settings
settings-logs = Logs
settings-logs-description = Follow the logs live. When you ask for help, export them and attach the .zip file to your message.
settings-logs-tab-live = Live
settings-logs-tab-files = Files ({ $count })
settings-logs-copy = Copy shown lines
settings-logs-export_all = Export all logs (.zip)
settings-logs-export-title = Export logs
settings-logs-export-all = All log files (.zip)
settings-logs-export-all-description = Every log file, for bug reports
settings-logs-export-source = { $source } files only (.zip)
settings-logs-export-selected =
    { $count ->
        [one] Export 1 selected file (.zip)
       *[other] Export { $count } selected files (.zip)
    }
settings-logs-sources = Sources
settings-logs-source-all = All
settings-logs-source-slimevr_server = Server
settings-logs-source-gui = GUI
settings-logs-source-openvr_driver = OpenVR driver
settings-logs-source-bindings_provider = Bindings provider
settings-logs-level = Level
settings-logs-level-debug = Debug
settings-logs-level-info = Info
settings-logs-level-warn = Warning
settings-logs-level-error = Error
settings-logs-logger = Logger
settings-logs-logger-all = All
settings-logs-search = Search messages
settings-logs-filters = Filters
settings-logs-details-show = Show details
settings-logs-details-hide = Hide details
settings-logs-new_lines =
    { $count ->
        [one] 1 new line
       *[other] { $count } new lines
    }
settings-logs-status-live = Live
settings-logs-status-paused = Paused
settings-logs-status-file = File
settings-logs-lines =
    { $total ->
        [one] 1 line
       *[other] { $total } lines
    }
settings-logs-lines-filtered = { $shown } of { $total } lines
settings-logs-pause = Pause
settings-logs-resume = Resume
settings-logs-clear = Clear view
settings-logs-open_folder = Open logs folder
settings-logs-back = Back to files
settings-logs-viewing = Viewing { $name }
settings-logs-files-empty = No log files yet.
settings-logs-files-current = current
settings-logs-files-part = Part { $part }
settings-logs-files-count =
    { $count ->
        [one] 1 file
       *[other] { $count } files
    }
settings-logs-files-view = View
settings-logs-files-save = Save

## OSC VRChat settings
settings-osc-vrchat = VRChat OSC
# This cares about multilines
settings-osc-vrchat-description-v1 =
    Change settings specific to VRChat OSC and the OSC Trackers standard used to send
    tracking data to applications without SteamVR (ex. Quest standalone).
    Make sure to enable OSC in VRChat via the Action Menu under OSC > Enabled.
settings-osc-vrchat-output_group = Output
settings-osc-vrchat-network_group = Network
settings-osc-vrchat-enable = Enable
settings-osc-vrchat-enable-description = Toggle the sending and receiving of data.
settings-osc-vrchat-network = Network ports
settings-osc-vrchat-network-port_in =
    .label = Port In
    .placeholder = Port in (default: 9001)
settings-osc-vrchat-network-port_out =
    .label = Port Out
    .placeholder = Port out (default: 9000)
settings-osc-vrchat-network-address = Network address
settings-osc-vrchat-network-address-description-v1 = Choose which address to send out data to. Can be left untouched for VRChat.
settings-osc-vrchat-network-address-placeholder = VRChat IP address

## VRChat OSC status
settings-osc-vrchat-status-title = Status
settings-osc-vrchat-status-input = Input
settings-osc-vrchat-status-tracking = Tracking data
settings-osc-vrchat-status-output = Output
settings-osc-vrchat-status-oscquery = OSCQuery
settings-osc-vrchat-status-input-idle = Not listening
settings-osc-vrchat-status-input-listening = Listening on port {$port}
settings-osc-vrchat-status-input-last-data = Last data from VRChat: {$elapsed}.
settings-osc-vrchat-status-input-no-data = No data received from VRChat yet.
settings-osc-vrchat-status-tracking-received = Last pose received: {$elapsed}.
settings-osc-vrchat-status-tracking-disabled = VRChat is connected, but isn't sending head/wrist tracking data. Turn on the OSC tracking data toggle in VRChat's settings. <OscTrackingLink>See how to enable it.</OscTrackingLink>
settings-osc-vrchat-status-tracking-unknown = Waiting for VRChat to connect.
settings-osc-vrchat-status-output-idle = No target
settings-osc-vrchat-status-output-waiting = Target {$address}:{$port} ({$source})
settings-osc-vrchat-status-output-sending = Sending to {$address}:{$port} ({$source})
settings-osc-vrchat-status-output-target = Target {$address}:{$port} ({$source})
settings-osc-vrchat-status-output-last-frame = Last frame sent: {$elapsed}
settings-osc-vrchat-status-output-no-frame = No frame sent yet.
settings-osc-vrchat-status-source-manual = manual
settings-osc-vrchat-status-source-auto = auto-detected
settings-osc-vrchat-status-oscquery-disabled = OSCQuery off (manual network mode)
settings-osc-vrchat-status-oscquery-advertising = Advertising on port {$port}
settings-osc-vrchat-status-oscquery-searching = No VRChat clients found yet.
settings-osc-vrchat-status-oscquery-discovered-title = Discovered VRChat clients:
settings-osc-vrchat-status-oscquery-switch = Switch
settings-osc-vrchat-status-network-mode = Network mode
settings-osc-vrchat-status-network-mode-auto = Automatic
settings-osc-vrchat-status-network-mode-manual = Manual
settings-osc-vrchat-status-network-mode-manual_only = Only used while the network mode is manual.
settings-osc-vrchat-status-network-mode-description = Automatic mode discovers VRChat through OSCQuery. Manual mode uses the configured address and ports.
settings-osc-vrchat-status-network-manual-description = Configure the local input port and the VRChat OSC target manually.

settings-osc-vrchat-status-badge-idle = Idle
settings-osc-vrchat-status-badge-listening = Listening
settings-osc-vrchat-status-badge-ready = Ready
settings-osc-vrchat-status-badge-found = Found
settings-osc-vrchat-status-badge-searching = Searching
settings-osc-vrchat-status-badge-disabled = Disabled
settings-osc-vrchat-status-badge-error = Error
settings-osc-vrchat-status-badge-received = Received
settings-osc-vrchat-status-badge-not-sent = Not sent
settings-osc-vrchat-status-badge-unknown = Unknown

## VMC OSC settings
settings-osc-vmc = Virtual Motion Capture
# This cares about multilines
settings-osc-vmc-description =
    Change settings specific to the VMC (Virtual Motion Capture) protocol
    to send SlimeVR's bone data and receive bone data from other apps.
settings-osc-vmc-output_group = Output
settings-osc-vmc-network_group = Network
settings-osc-vmc-enable = Enable
settings-osc-vmc-enable-description = Toggle the sending and receiving of data.
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
settings-osc-vmc-network-address-placeholder = IPv4 address
settings-osc-vmc-avatar_group = Avatar
settings-osc-vmc-vrm = VRM Model
settings-osc-vmc-vrm-description = Load a VRM model to use its own bone proportions and enable a higher compatibility with other applications.
settings-osc-vmc-vrm-untitled_model = Untitled model
settings-osc-vmc-vrm-file_select = Drag & drop a model to use, or <u>browse</u>
settings-osc-vmc-anchor_hip = Anchor at hips
settings-osc-vmc-anchor_hip-description = When enabled, the avatar stays pinned at its hips and only rotations are sent, useful for seated VTubing. When disabled, the avatar follows your head and moves around.
settings-osc-vmc-mirror_tracking = Mirror tracking
settings-osc-vmc-mirror_tracking-description = Mirror the tracking horizontally.
settings-osc-vmc-status-input = Input
settings-osc-vmc-status-output = Output
settings-osc-vmc-status-vrm = VRM model
settings-osc-vmc-status-input-idle = Not listening
settings-osc-vmc-status-input-listening = Listening on port {$port}
settings-osc-vmc-status-input-last-data = Last data received: {$elapsed}.
settings-osc-vmc-status-input-no-data = No data received yet.
settings-osc-vmc-status-output-idle = No target
settings-osc-vmc-status-output-waiting = Target {$address}:{$port}, no frame sent yet
settings-osc-vmc-status-output-sending = Sending to {$address}:{$port}
settings-osc-vmc-status-output-target = Target {$address}:{$port}
settings-osc-vmc-status-output-last-frame = Last frame sent: {$elapsed}.
settings-osc-vmc-status-output-no-frame = No frame sent yet.
settings-osc-vmc-status-vrm-none = No model loaded, using estimated bone offsets.
settings-osc-vmc-status-vrm-loaded = Model loaded, using its bone offsets.
settings-osc-vmc-status-title = Status
settings-osc-vmc-status-badge-idle = Idle
settings-osc-vmc-status-badge-listening = Listening
settings-osc-vmc-status-badge-ready = Ready
settings-osc-vmc-status-badge-disabled = Disabled
settings-osc-vmc-status-badge-error = Error

## Common OSC settings
settings-osc-common-network-ports_match_error-v2 = The in and out ports can't be the same!
settings-osc-common-network-port_banned_error = The port { $port } can't be used!

## Advanced settings
settings-utils-advanced = Advanced

settings-utils-advanced-danger_zone = Danger zone
settings-utils-advanced-danger_zone-description = These actions delete your settings and cannot be undone.

settings-utils-advanced-reset-gui = Reset GUI settings
settings-utils-advanced-reset-gui-description = Restore the default settings for the interface.
settings-utils-advanced-reset-gui-label = Reset GUI
settings-utils-advanced-reset-server = Reset tracking settings
settings-utils-advanced-reset-server-description = Switch back to the default profiles and restore their default tracking settings.
settings-utils-advanced-reset-server-label = Reset tracking
settings-utils-advanced-reset-all = Reset all settings
settings-utils-advanced-reset-all-description = Restore the default settings for the interface, and switch back to the default profiles with their default tracking settings.
settings-utils-advanced-reset-all-label = Reset all
settings-utils-advanced-reset_warning =
        <b>Warning:</b> This will reset { $type ->
            [gui] your GUI
            [server] your tracking
            *[all] all your
        } settings to the defaults.{ $type ->
            [gui] {""}
           *[other] {" "}You will be switched back to the default settings and user profiles.
        }
        Are you sure you want to do this?
settings-utils-advanced-reset_warning-reset = Reset settings
settings-utils-advanced-reset_warning-cancel = Cancel

settings-utils-advanced-folders_group = Folders
settings-utils-advanced-open_data-v1 = Config folder
settings-utils-advanced-open_data-description-v1 = Open SlimeVR's config folder in file explorer, containing the configuration
settings-utils-advanced-open_data-label = Open folder
settings-utils-advanced-open_logs = Logs folder
settings-utils-advanced-open_logs-description = Open SlimeVR's logs folder in file explorer, containing the logs of the app
settings-utils-advanced-open_logs-label = Open folder

## Home Screen
settings-home-list-layout = Trackers list layout
settings-home-list-layout-desc = Select one of the possible layouts of the home screen
settings-home-list-layout-grid = Grid
settings-home-list-layout-table = Table

settings-home-table_columns = Table columns
settings-home-table_columns-desc = Choose which columns are shown in the trackers table

settings-home-display_group = Display
settings-home-tracker_display = Tracker info
settings-home-tracker_display-desc = Choose which extra details are shown on trackers
settings-home-tracker_display-battery_voltage = Battery voltage
settings-home-tracker_display-numeric_signal = Numeric signal strength

## Tracking Checklist
settings-tracking_checklist-active_steps = Active steps
settings-tracking_checklist-active_steps-desc = Choose which steps appear in the tracking checklist. Turning a step off hides it until you turn it back on, unlike Ignore, which only lasts for the current session.

## Setup/onboarding menu
onboarding-skip = Skip setup
onboarding-continue = Continue
onboarding-previous_step = Previous step
onboarding-setup_warning =
    <b>Warning:</b> The initial setup is required for good tracking,
    it is needed if this is your first time using SlimeVR.
onboarding-setup_warning-skip = Skip setup
onboarding-setup_warning-cancel = Continue setup

## Quiz
onboarding-quiz_continue = Continue
onboarding-quiz_back = Back

onboarding-quiz-more_sets_modal-title = Have you connected all of your trackers?
onboarding-quiz-more_sets_modal-desc = If you have sets of different models, we can connect them right now!
onboarding-quiz-more_sets_modal-confirm = I have connected all my trackers
onboarding-quiz-more_sets_modal-cancel = I want to connect more trackers

onboarding-quiz-slimeset-title = What type of trackers are you connecting?
onboarding-quiz-slimeset-description = If you have multiple sets, you will be asked again later in the process
onboarding-quiz-slimeset-official-sets = Official SlimeVR Trackers
onboarding-quiz-slimeset-thirdparty-sets = Third-party or DIY Trackers
onboarding-quiz-slimeset-answer-regular = SlimeVR V1.0 & V1.2
onboarding-quiz-slimeset-answer-butterfly = Butterfly
onboarding-quiz-slimeset-answer-wifi = Wi-Fi-based Slime
onboarding-quiz-slimeset-answer-dongle = Dongle-based Slime

onboarding-quiz-usage-title = What are you using your trackers for?
onboarding-quiz-usage-description = If you plan on using SlimeVR for multiple purposes, you can change the affected settings later.
onboarding-quiz-usage-answer-VRC = VR Gaming (e.g. VRChat)
onboarding-quiz-usage-answer-mocap_vtubing = Mocap and VTubing

onboarding-quiz-runtime-title = Do you run games via SteamVR, or on the headset itself (standalone)?
onboarding-quiz-runtime-answer-steamvr = SteamVR
onboarding-quiz-runtime-answer-standalone = Standalone

onboarding-quiz-mocap_preferences-title = Mocap Preferences
onboarding-quiz-mocap_preferences-desc = Specify how you plan to use SlimeVR for mocap or VTubing
onboarding-quiz-mocap_preferences-playspace-title = What is your playspace?
onboarding-quiz-mocap_preferences-playspace-desc = If standing, SlimeVR will try to track walking movement instead of anchoring you in one spot.
onboarding-quiz-mocap_preferences-playspace-sitting = Sitting
onboarding-quiz-mocap_preferences-playspace-standing = Standing
onboarding-quiz-mocap_preferences-vrm_model-title = Do you have a VRM model? (Optional)
onboarding-quiz-mocap_preferences-vrm_model-desc = Loading a VRM model will improve tracking quality and compatibility with applications that use VMC.
onboarding-quiz-mocap_preferences-head_tracker-title = Are you wearing a tracker or VR headset on your head?
onboarding-quiz-mocap_preferences-head_tracker-yes = Yes
onboarding-quiz-mocap_preferences-head_tracker-no = No
onboarding-quiz-mocap_preferences-head_tracker_location-title = Where is your head tracker located?
onboarding-quiz-mocap_preferences-head_tracker_location-forehead = Forehead
onboarding-quiz-mocap_preferences-head_tracker_location-face = Face

## Wi-Fi setup
onboarding-wifi_creds-back-v2 = Go back
onboarding-wifi_creds-v2 = Trackers using Wi-Fi
# This cares about multilines
onboarding-wifi_creds-description-v2 =
    Most trackers (such as the official SlimeVR 1.0, 1.1 and 1.2 trackers) use Wi-Fi to connect to the server.
    Use the credentials of the Wi-Fi network your device is currently connected to.
onboarding-wifi_creds-continue = Continue with Wi-Fi
onboarding-wifi_creds-submit = Submit!
onboarding-wifi_creds-retry = Retry
onboarding-wifi_creds-ssid-label = Wi-Fi name
onboarding-wifi_creds-ssid =
    .placeholder = Enter Wi-Fi name
onboarding-wifi_creds-ssid-required = Wi-Fi name is required
onboarding-wifi_creds-ssid-scan =
    .placeholder = Select Wi-Fi network
onboarding-wifi_creds-network_band_tip = Only 2.4GHz Wi-Fi networks can be used by trackers. If your network doesn't appear in the list, make sure a 2.4GHz band is enabled on your router.
onboarding-wifi_creds-scan_idle = Plug in a tracker via USB to scan for nearby Wi-Fi networks.
onboarding-wifi_creds-scanning = Scanning for Wi-Fi networks...
onboarding-wifi_creds-scan_unsupported = Wi-Fi scanning isn't supported on this tracker's firmware yet. Enter your network name manually.
onboarding-wifi_creds-scan_failed = Wi-Fi scan failed on the tracker.
onboarding-wifi_creds-scan_error_no_device = Could not find a tracker over USB to scan for Wi-Fi networks.
onboarding-wifi_creds-scan_error_no_logs = Could not read serial logs from the tracker during Wi-Fi scan.
onboarding-wifi_creds-rescan = Rescan
onboarding-wifi_creds-enter_manually = Don't see your network? Enter it manually
onboarding-wifi_creds-use_scanned = Choose from scanned networks instead
onboarding-wifi_creds-password =
    .label = Password
    .placeholder = Enter your Wi-Fi password

onboarding-wifi_creds-dongle-title = Trackers using a dongle
onboarding-wifi_creds-dongle-description =
    If your trackers came with a dongle, plug it into your device and you should be good to go!
onboarding-wifi_creds-dongle-wip = This section is a work in progress. A dedicated page to manage trackers that connect via a dongle will be made soon.
onboarding-wifi_creds-dongle-continue = Continue with a dongle

## Install info
install-info_udev-rules_modal_title = Hardware udev access rules not found
install-info_udev-rules_warning = Access rules via udev are required for serial console access & dongle connection. Paste the following command into your terminal to add the udev rules.
install-info_udev-rules_modal_button = Close
install-info_udev-rules_modal-dont-show-again_checkbox = Don't show again
## Setup start
onboarding-home = Welcome to SlimeVR
onboarding-home-start = Let's get set up!


## Tracker connection setup
onboarding-connect_tracker-title = Let's connect your trackers
onboarding-connect_tracker-description = Enter your Wi-Fi network below and submit once. Every tracker you connect via USB, now or later, will automatically use those credentials.
onboarding-connect_tracker-learn_more = Learn more
onboarding-connect_tracker-issue-serial = I'm having trouble connecting!
onboarding-connect_tracker-usb = USB Tracker
onboarding-connect_tracker-tracker_mac_name = SlimeVR Tracker ({ $suffix })
onboarding-connect_tracker-tracker_port_name = USB Tracker ({ $port })
onboarding-connect_tracker-network_profile-ignore = I configured my firewall and know what I'm doing
onboarding-connect_tracker-waiting_first_title = Waiting for your first tracker
onboarding-connect_tracker-waiting_first_desc = Plug one in over USB to get started.
onboarding-connect_tracker-scan_results_title = Select network & enter password
onboarding-connect_tracker-scan_results_desc = Enter your Wi-Fi credentials on the left to connect your trackers.
onboarding-connect_tracker-all_caught_up = All caught up. Plug in more any time
onboarding-connect_tracker-shake_tip = Not sure which tracker is which? Shake a tracker to see it light up in the list.
onboarding-connect_tracker-close = Close
onboarding-connect_tracker-connection_status-serial_init = Connecting to serial device
onboarding-connect_tracker-connection_status-obtaining_mac_address = Obtaining the tracker's MAC address
onboarding-connect_tracker-connection_status-provisioning = Sending Wi-Fi credentials
onboarding-connect_tracker-connection_status-connecting = Trying to connect to Wi-Fi
onboarding-connect_tracker-connection_status-looking_for_server = Looking for server
onboarding-connect_tracker-connection_status-connection_error = Unable to connect to Wi-Fi
onboarding-connect_tracker-connection_status-could_not_find_server = Could not find the server
onboarding-connect_tracker-connection_status-done = Connected to the Server
onboarding-connect_tracker-connection_status-no_serial_log = Could not get logs from the tracker
onboarding-connect_tracker-connection_status-no_serial_device_found = Could not find a tracker from USB
onboarding-connect_tracker-connection_error-desc = Make sure your Wi-Fi SSID and password are entered correctly and that your 2.4GHz network is active.
onboarding-connect_tracker-could_not_find_server-desc = The tracker connected to Wi-Fi, but could not find the SlimeVR server on your local network. Check your firewall settings.
onboarding-connect_serial-error-modal-no_serial_log = Is the tracker turned on?
onboarding-connect_serial-error-modal-no_serial_log-desc = Make sure the tracker is turned on and connected to your computer.
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
onboarding-connect_tracker-setting_up = Setting up ({ $amount })
onboarding-connect_tracker-next = I connected all my trackers

## Tracker assignment setup
onboarding-assign_trackers-title = Assign trackers
onboarding-assign_trackers-description = Let's choose which tracker goes where. Click on a location where you want to place a tracker.
onboarding-assign_trackers-reset_assignments = Reset all assignments
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned = { $assigned } of { $trackers ->
    [one] 1 tracker
    *[other] { $trackers } trackers
} assigned
onboarding-assign_trackers-all_assigned-title = All trackers assigned!
onboarding-assign_trackers-all_assigned-description = You're ready to move on to the next step
onboarding-assign_trackers-no_trackers-title = No trackers connected
onboarding-assign_trackers-no_trackers-description = Connect a tracker to start assigning it to a body part
onboarding-assign_trackers-no_trackers-connect = Connect trackers
onboarding-assign_trackers-show_all = Show all body parts
onboarding-assign_trackers-show_all-short = Show all parts
onboarding-assign_trackers-tap_modal-title = Tap to assign
onboarding-assign_trackers-tap_modal-description = Tap the tracker you want to assign twice, or press its button once. You can also drag a tracker from the list instead.
onboarding-assign_trackers-tap_modal-cancel = Cancel
onboarding-assign_trackers-mobile-idle_title = { $remaining ->
    [one] 1 tracker left to assign
   *[other] { $remaining } trackers left to assign
}
onboarding-assign_trackers-mobile-idle_hint = Press a spot on the body, or open the list below
onboarding-assign_trackers-mobile-idle_hint-open = Pick a tracker, then press its spot on the body
onboarding-assign_trackers-mobile-choose_tracker = Choose a tracker for { $part }
onboarding-assign_trackers-mobile-choose_tracker-hint = Or double-tap it on your body
onboarding-assign_trackers-mobile-choose_tracker-current = Currently { $tracker }
onboarding-assign_trackers-mobile-choose_spot = Choose a spot for { $tracker }
onboarding-assign_trackers-mobile-choose_spot-hint = Press a spot on the body above
onboarding-assign_trackers-mobile-unassign = Unassign { $part }
onboarding-assign_trackers-tab-body = Body
onboarding-assign_trackers-tab-fingers = Fingers
onboarding-assign_trackers-tab-toes = Toes
onboarding-assign_trackers-side-right = Right
onboarding-assign_trackers-side-left = Left
# Accessible name for the left/right pill radiogroup in the assignment panel header
onboarding-assign_trackers-side = Body side
# Accessible name for the mirror-view pill toggle in the assignment panel header
onboarding-assign_trackers-mirror = Mirror view
onboarding-assign_trackers-finger-thumb = Thumb
onboarding-assign_trackers-finger-index = Index finger
onboarding-assign_trackers-finger-middle = Middle finger
onboarding-assign_trackers-finger-ring = Ring finger
onboarding-assign_trackers-finger-little = Pinkie finger
onboarding-assign_trackers-toes-middle = Middle toes
onboarding-assign_trackers-toe-big = Big toe
onboarding-assign_trackers-toe-index = Index toe
onboarding-assign_trackers-toe-middle = Middle toe
onboarding-assign_trackers-toe-ring = Ring toe
onboarding-assign_trackers-toe-little = Little toe
onboarding-assign_trackers-joint-distal = Distal
onboarding-assign_trackers-joint-intermediate = Medial
onboarding-assign_trackers-joint-proximal = Proximal
onboarding-assign_trackers-joint-metacarpal = Metacarpal

## Tracker assignment warnings
# Note for devs, number is used for representing boolean states per bit.
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
# $part (String) - the body part a tracker is assigned to
# $missing (String) - the body parts it still needs, already formatted as a list
onboarding-assign_trackers-warning = { $part } is assigned but you also need { $missing } to be assigned!
# $parts (String) - the body parts still missing a tracker, formatted as a list
# $count (Number) - how many of them there are
onboarding-assign_trackers-warning-required = { $count ->
    [one] { $parts } still needs a tracker
   *[other] { $parts } still need trackers
}

## Tracker mounting method choose
onboarding-choose_mounting = What mounting calibration method to use?
# Multiline text
onboarding-choose_mounting-description = Mounting orientation corrects for the placement of trackers on your body.

## Mounting method
mounting_method_modal-title = How do you want to calibrate your trackers?
mounting_method_modal-description = Mounting orientation corrects for the placement of trackers on your body. You can change this later in the settings.
# Italicized text
mounting_method-recommended = Recommended
mounting_method-select = Select
mounting_method-step = Step mounting
mounting_method-step-description = Take a step forward to calibrate your trackers.
mounting_method-step-needs_positional_head = Needs a headset or a tracker with position tracking.
mounting_method-pose = Pose mounting
mounting_method-pose-description = Hold a ski pose to calibrate your trackers.
mounting_method-manual = Manual mounting
mounting_method-manual-description = Choose the mounting orientation manually for each tracker.

## Tracker step mounting setup
onboarding-step_mounting-title = Step Mounting Calibration
onboarding-step_mounting-description = For SlimeVR trackers to work, we need to assign a mounting orientation to your trackers to align them with your physical tracker mounting.
onboarding-step_mounting-step-title = Step Mounting Calibration
onboarding-step_mounting-step-0 = 1. Press the "Mounting Calibration" button.
onboarding-step_mounting-step-1 = 2. Take one step forward, then stop and hold still until the calibration finishes.

## Step mounting calibration
step_mounting-title = Step Mounting Calibration
# $status (String) - StepMountingStatus enum member name
step_mounting-instructions-WAITING_FOR_MOVEMENT = Take a step forward, then stand still.
step_mounting-instructions-RECORDING = Keep still once you've stopped, we're still recording.
step_mounting-instructions-PROCESSING = Hold on, calculating your mounting offset...
step_mounting-status-WAITING_FOR_MOVEMENT = Waiting for movement...
step_mounting-status-RECORDING = Recording your step...
step_mounting-status-PROCESSING = Processing...
step_mounting-status-DONE = Done!
step_mounting-status-ERROR_NO_DATA = Couldn't detect any movement. Try again.
step_mounting-status-ERROR_THRESHOLD_EXCEEDED = Tracker acceleration was invalid. Try again.
step_mounting-status-ERROR_TIMEOUT = Timed out waiting for movement. Try again.
step_mounting-cancel = Cancel
step_mounting-close = Close
step_mounting-next = Next step
step_mounting-retry = Try again

## Tracker manual mounting setup
onboarding-manual_mounting = Manual Mounting
onboarding-manual_mounting-description = Click on every tracker and select which way they are mounted
onboarding-manual_mounting-next = Next step
onboarding-manual_mounting-automatic-title = Automatically mounted
onboarding-manual_mounting-automatic-description = A mounting calibration set these. Pick one to set its direction by hand.
onboarding-manual_mounting-assign_link-title = Tracker missing?
onboarding-manual_mounting-assign_link-description = Only trackers assigned to a body part show up here

## Tracker automatic mounting setup
onboarding-automatic_mounting-title = Mounting Calibration
onboarding-automatic_mounting-description = For SlimeVR trackers to work, we need to assign a mounting orientation to your trackers to align them with your physical tracker mounting.
onboarding-automatic_mounting-manual_mounting = Manual mounting
onboarding-automatic_mounting-next = Next step
onboarding-automatic_mounting-prev_step = Previous step
onboarding-automatic_mounting-done-title = Mounting orientations calibrated.
onboarding-automatic_mounting-done-description = Your mounting calibration is complete!
onboarding-automatic_mounting-done-restart = Try again
onboarding-automatic_mounting-mounting_reset-title = Mounting Calibration
onboarding-automatic_mounting-mounting_reset-step-0 =
    1. Squat in a "skiing" pose with your legs bent and your upper body tilted forwards{ $feet ->
        [tiptoe] , staying up on your toes
       *[flat] { "" }
    }. { $arms ->
        [forward] Stretch your arms straight out in front of you.
        [tpose_up] Stretch your arms out to your sides at 90 degrees.
        [tpose_down] Let your arms hang down at your sides.
       *[back] Keep your arms bent.
    }
onboarding-automatic_mounting-mounting_reset-step-1 = 2. Press the "Mounting calibration" button and wait for 3 seconds before the trackers' mounting orientations will reset.

onboarding-automatic_mounting-mounting_reset-feet-step-0 = 1. Stand on your toes with both feet pointing forward. Alternatively you can do it sitting on a chair.
onboarding-automatic_mounting-mounting_reset-feet-step-1 = 2. Press the "Feet calibration" button and wait for 3 seconds before the trackers' mounting orientations will reset.

onboarding-automatic_mounting-preparation-title = Preparation
onboarding-automatic_mounting-preparation-v2-step-0 = 1. Press the "Full Reset" button.
onboarding-automatic_mounting-preparation-v2-step-1 = 2. Stand upright with your arms to your sides. Make sure to look forward.
onboarding-automatic_mounting-preparation-v2-step-2 = 3. Hold the position until the 3s timer ends.
onboarding-automatic_mounting-preparation-v2-done = Looks like you've already performed a full reset recently!
onboarding-automatic_mounting-preparation-example-front = Stand upright, legs parallel, arms at your sides, looking forward.
onboarding-automatic_mounting-preparation-example-side = From the side, stay straight and don't lean.
onboarding-automatic_mounting-preparation-example-wrong = Don't spread your legs or arms.
onboarding-automatic_mounting-return-home = Done

## Tracker manual proportions setup
onboarding-manual_proportions-back-scaled = Go back to Scaled Proportions
onboarding-manual_proportions-fine_tuning_button = Automatically fine tune proportions
onboarding-manual_proportions-fine_tuning_button-disabled-tooltip = Please connect a VR headset to use automatic fine tuning
onboarding-manual_proportions-export = Export proportions
onboarding-manual_proportions-import = Import proportions
onboarding-manual_proportions-normal_increment = Normal increment
onboarding-manual_proportions-precise_increment = Precise increment
onboarding-manual_proportions-grouped_proportions = Grouped proportions
onboarding-manual_proportions-all_proportions = All proportions
onboarding-manual_proportions-estimated_height = Estimated user height

## Tracker automatic proportions setup
onboarding-automatic_proportions-back = Go back to Manual Proportions
onboarding-automatic_proportions-title = Measure your body
onboarding-automatic_proportions-description = For SlimeVR trackers to work, we need to know the length of your bones. This short calibration will measure it for you.
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


## User height calibration
onboarding-user_height-title = What is your height?
onboarding-user_height-need_head_tracker = A headset and controllers with positional tracking are required to perform the calibration.
onboarding-user_height-calculate = Calculate my height automatically
onboarding-user_height-error_bounds = Input too high or too low
onboarding-user_height-error_format = Wrong input format
onboarding-user_height-next_step = Continue and save
onboarding-user_height-manual-proportions = Manual Proportions
onboarding-user_height-calibration-title = Calibration Progress
onboarding-user_height-calibration-RECORDING_FLOOR = Touch the floor with the tip of your controller
onboarding-user_height-calibration-WAITING_FOR_RISE = Stand back up
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK = Stand back up and look forward
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-ok = Make sure your head is levelled
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-low = Do not look at the floor
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-high = Do not look too high up
onboarding-user_height-calibration-WAITING_FOR_CONTROLLER_PITCH = Make sure the controller is pointing down
onboarding-user_height-calibration-RECORDING_HEIGHT = Stand back up and stand still!
onboarding-user_height-calibration-DONE = Success!
onboarding-user_height-calibration-ERROR_TIMEOUT = Calibration timed out, try again.
onboarding-user_height-calibration-ERROR_TOO_HIGH = The detected user height is too high, try again.
onboarding-user_height-calibration-ERROR_TOO_SMALL = The detected user height is too small. Make sure to stand straight and look forward at the end of the calibration.
onboarding-user_height-calibration-error = Calibration Failed
onboarding-user_height-manual-tip = While adjusting your height, try different poses and see how the skeleton matches your body.
onboarding-user_height-reset-warning = <b>Warning:</b> This will reset your proportions to be based on your height.
    Are you sure you want to do this?

## Home
home-settings = Home Page Settings
home-settings-close = Close
home-connection_group-wifi = Wi-Fi Trackers
home-connection_group-driver = VR Devices
home-new_trackers-count = { $count ->
    [one] { $count } new tracker
    *[other] { $count } new trackers
}
home-new_trackers-pill = { $count } new
home-new_trackers-add = Add
home-new_trackers-add_all = Add all
home-new_trackers-adding = Connecting…
home-new_trackers-ignore = Ignore until SlimeVR restarts
home-new_trackers-official_board = SlimeVR Tracker
home-no_trackers-title = No trackers connected
home-no_trackers-description = Connect a tracker or turn on your existing trackers to get started
home-no_trackers-connect = Connect trackers
home-no_trackers-guide = View setup guide
# Accessible names and tooltips for the icon-only buttons in a connection group's toolbox
tracker-connection-metrics = Live telemetry
tracker-connection-dongle_settings = Dongle settings
tracker-connection-collapse = Collapse group
tracker-connection-expand = Expand group

## Trackers Still On notification
trackers_still_on-modal-title = Trackers still on
trackers_still_on-modal-description =
    One or more trackers are still on.
    Do you still want to exit SlimeVR?
trackers_still_on-modal-confirm = Exit SlimeVR
trackers_still_on-modal-cancel = Hold on...

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

firmware_tool-select_source = Select the firmware to flash
firmware_tool-select_source-description = Select the firmware you want to flash on your board
firmware_tool-select_source-error = Unable to load Sources
firmware_tool-select_source-board_type = Board Type
firmware_tool-select_source-firmware = Firmware Source
firmware_tool-select_source-version = Firmware Version
firmware_tool-select_source-official = Official
firmware_tool-select_source-dev = Dev
firmware_tool-select_source-latest = Latest
firmware_tool-select_source-prerelease = Pre-release
firmware_tool-select_source-not_selected = No source selected
firmware_tool-select_source-no_boards = No available boards for this source
firmware_tool-select_source-no_versions = No available versions for this source

firmware_tool-board_defaults = Configure your board
firmware_tool-board_defaults-description = Set the pins or settings relative to your hardware
firmware_tool-board_defaults-add = Add
# Accessible name for the trash icon that removes a component
firmware_tool-board_defaults-remove = Remove
firmware_tool-board_defaults-reset = Reset to Default
firmware_tool-board_defaults-error-required = Required field
firmware_tool-board_defaults-error-format = Invalid format
firmware_tool-board_defaults-error-format-number = Not a number

firmware_tool-flash_method_step = Flashing Method
firmware_tool-flash_method_step-description =
    Please select the flashing method you want to use

firmware_tool-flash_method_step-ota-v2 =
    .label = Wi-Fi
    .description = Use the over-the-air method. Your tracker will use Wi-Fi to update its firmware. Only works on trackers that have been set up.
firmware_tool-flash_method_step-ota-info =
    We use your Wi-Fi credentials to flash the tracker and confirm that everything worked correctly.
    <b>We do not store your Wi-Fi credentials!</b>
firmware_tool-flash_method_step-serial-v2 =
    .label = USB
    .description = Use a USB cable to update your tracker.


firmware_tool-flashbtn_step = Press the boot button
firmware_tool-flashbtn_step-description = Before going to the next step, there are a few things you need to do

firmware_tool-flashbtn_step-board_SLIMEVR = Turn off the tracker, remove the case (if any), connect the USB cable to your computer, then follow the appropriate steps for your SlimeVR board revision:
firmware_tool-flashbtn_step-board_SLIMEVR-r11-v2 = Turn on the tracker while shorting the second rectangular FLASH pad from the edge on the top side of the board to the metal shield of the microcontroller. The tracker LED should do a short blink.
firmware_tool-flashbtn_step-board_SLIMEVR-r12-v2 = Turn on the tracker while shorting the circular FLASH pad on the top side of the board to the metal shield of the microcontroller. The tracker LED should do a short blink.
firmware_tool-flashbtn_step-board_SLIMEVR-r14-v2 = Turn on the tracker while pushing in the FLASH button on the top side of the board. The tracker LED should do a short blink.

firmware_tool-flashbtn_step-board_OTHER = Before flashing, you will probably need to put the tracker into bootloader mode.
    Most of the time, this means pressing the boot button on the board before the flashing process starts.
    If the flashing process times out at the start, it probably means that the tracker was not in bootloader mode.
    Refer to your board's flashing instructions to learn how to enter bootloader mode.

firmware_tool-flash_method_ota-title = Flashing over Wi-Fi
firmware_tool-flash_method_ota-devices = Detected OTA Devices:
firmware_tool-flash_method_ota-no_devices = There are no boards that can be updated using OTA, make sure you selected the correct board type
firmware_tool-flash_method_serial-title = Flashing over USB
firmware_tool-flash_method_serial-wifi = Wi-Fi Credentials:
firmware_tool-flash_method_serial-devices-label = Detected Serial Devices:
firmware_tool-flash_method_serial-devices-placeholder = Select a serial device
firmware_tool-flash_method_serial-unknown_device = { $name } (unrecognized)
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
firmware_tool-build-QUEUED = Waiting to build...
firmware_tool-build-CREATING_BUILD_FOLDER = Creating the build folder
firmware_tool-build-DOWNLOADING_SOURCE = Downloading the source code
firmware_tool-build-EXTRACTING_SOURCE = Extracting the source code
firmware_tool-build-BUILDING = Building the firmware
firmware_tool-build-SAVING = Saving the build
firmware_tool-build-DONE = Build Complete
firmware_tool-build-ERROR = Unable to build the firmware

## Firmware update status
firmware_update-status-DOWNLOADING = Downloading the firmware
firmware_update-status-NEED_MANUAL_REBOOT-v2 = Please turn your tracker off and on again
firmware_update-status-AUTHENTICATING = Authenticating with the MCU
firmware_update-status-UPLOADING = Uploading the firmware
firmware_update-status-SYNCING_WITH_MCU = Syncing with the MCU
firmware_update-status-REBOOTING = Applying the update
firmware_update-status-PROVISIONING = Setting Wi-Fi credentials
firmware_update-status-DONE = Update complete!
firmware_update-status-ERROR_DEVICE_NOT_FOUND = Could not find the device
firmware_update-status-ERROR_TIMEOUT = The update process timed out
firmware_update-status-ERROR_DOWNLOAD_FAILED = Could not download the firmware
firmware_update-status-ERROR_AUTHENTICATION_FAILED = Could not authenticate with the MCU
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
vrc_config-setting_name = VRChat Setting name
vrc_config-recommended_value = Recommended Value
vrc_config-current_value = Current Value
vrc_config-mute = Mute Warning
vrc_config-mute-btn = Mute
vrc_config-unmute-btn = Unmute
vrc_config-legacy_mode = Use Legacy IK Solving
vrc_config-disable_shoulder_tracking = Disable Shoulder Tracking
vrc_config-tracker_model = FBT Tracker Model
vrc_config-avatar_measurement_type = Avatar Measurement
vrc_config-calibration_visuals = Display Calibration Visuals
vrc_config-user_height = User Real Height

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
error_collection_modal-subtitle = Sharing errors helps us find and fix bugs faster.
error_collection_modal-change_later = You can change this setting later in the Behaviour section of the settings page.
error_collection_modal-confirm = I agree
error_collection_modal-cancel = I don't want to

## Crash screen
crash_screen-title = Something went wrong
crash_screen-description = The interface crashed. Reloading usually fixes it. If you report this issue, include the error id below.
crash_screen-event_id = Error id: { $id }
crash_screen-copy = Copy error id
crash_screen-reload = Reload

## Tracking checklist section
tracking_checklist = Tracking Checklist
tracking_checklist-settings = Tracking Checklist Settings
tracking_checklist-settings-close = Close
# Accessible name and tooltip for the icon-only button that collapses/expands the checklist panel
tracking_checklist-collapse = Collapse checklist
tracking_checklist-expand = Expand checklist
tracking_checklist-status-incomplete = You are not prepared to use SlimeVR!
tracking_checklist-status-partial = {$count ->
    [one] You have 1 warning!
    *[many] You have {$count} warnings!
}
tracking_checklist-status-complete = You are prepared to use SlimeVR!
tracking_checklist-MOUNTING_METHOD = Pick a mounting calibration method
tracking_checklist-MOUNTING_METHOD-desc = Choose how your trackers are calibrated to the way they are mounted on your body.
tracking_checklist-MOUNTING_METHOD-settings_desc = Shown when you have not picked a mounting calibration method.
tracking_checklist-MOUNTING_CALIBRATION = Perform a mounting calibration
tracking_checklist-MOUNTING_CALIBRATION-settings_desc = Shown when your trackers need a mounting calibration.
tracking_checklist-FEET_MOUNTING_CALIBRATION = Perform a feet mounting calibration
tracking_checklist-FEET_MOUNTING_CALIBRATION-settings_desc = Shown when your feet trackers need a mounting calibration.
tracking_checklist-FULL_RESET = Perform a full reset
tracking_checklist-FULL_RESET-desc = Some trackers need a reset to be performed.
tracking_checklist-FULL_RESET-settings_desc = Shown when some of your trackers need a full reset.
tracking_checklist-STEAMVR_DISCONNECTED = Connect to SteamVR
tracking_checklist-STEAMVR_DISCONNECTED-desc = SteamVR is not running, or you have not yet connected your headset to SteamVR.
tracking_checklist-STEAMVR_DISCONNECTED-settings_desc = Shown when SteamVR is not running or the SlimeVR driver is not being loaded.
tracking_checklist-STEAMVR_DISCONNECTED-driver_blocked-desc = The driver has been blocked by SteamVR due to a previous SteamVR crash.
tracking_checklist-STEAMVR_DISCONNECTED-driver_disabled-desc = The driver is disabled in SteamVR settings.
tracking_checklist-STEAMVR_DISCONNECTED-driver_not_installed-desc = The driver is not installed.
tracking_checklist-STEAMVR_DISCONNECTED-open = Launch SteamVR
tracking_checklist-STEAMVR_DISCONNECTED-enable = Enable driver
tracking_checklist-STEAMVR_HANDS_ENABLED = Hand trackers toggled on
tracking_checklist-STEAMVR_HANDS_ENABLED-desc = You have enabled the SteamVR virtual hand trackers. This will cause button inputs to not work in SteamVR and in games.
tracking_checklist-STEAMVR_HANDS_ENABLED-settings_desc = Shown when your hands are routed to SteamVR/Monado in a way that replaces the input from your real controllers.
tracking_checklist-STEAMVR_HANDS_ENABLED-go = Disable them
tracking_checklist-STANDABLE_INSTALLED = Standable is installed
tracking_checklist-STANDABLE_INSTALLED-desc =
    Standable frequently causes tracking issues when used alongside SlimeVR. Standable should be fully uninstalled in Steam to ensure no issues arise.
    You must close SteamVR before uninstalling Standable in Steam.
tracking_checklist-STANDABLE_INSTALLED-settings_desc = Shown when Standable is installed, because it often causes tracking issues with SlimeVR.
tracking_checklist-TRACKERS_REST_CALIBRATION = Calibrate your trackers
tracking_checklist-TRACKERS_REST_CALIBRATION-desc = You didn't perform tracker calibration. Please let your trackers (highlighted in yellow) rest on a stable surface for a few seconds.
tracking_checklist-TRACKERS_REST_CALIBRATION-settings_desc = Shown when your trackers still need to rest on a stable surface to calibrate.
tracking_checklist-TRACKER_ERROR = Trackers with Errors
tracking_checklist-TRACKER_ERROR-desc = Some of your trackers have an error. Please restart the trackers highlighted in yellow.
tracking_checklist-TRACKER_ERROR-settings_desc = Shown when a tracker reports an error and needs to be restarted.
tracking_checklist-VRCHAT_SETTINGS = Configure VRChat settings
tracking_checklist-VRCHAT_SETTINGS-desc = You have misconfigured VRChat settings! This can negatively impact your tracking.
tracking_checklist-VRCHAT_SETTINGS-settings_desc = Shown when your VRChat settings could hurt your tracking.
tracking_checklist-VRCHAT_SETTINGS-open = Go to VRChat Warnings
tracking_checklist-UNASSIGNED_HMD = VR headset not assigned to Head
tracking_checklist-UNASSIGNED_HMD-desc = The VR headset should be assigned as a head tracker.
tracking_checklist-UNASSIGNED_HMD-settings_desc = Shown when your VR headset is not assigned to the head.
tracking_checklist-NETWORK_PROFILE_PUBLIC = Change your network profile
tracking_checklist-NETWORK_PROFILE_PUBLIC-desc = {$count ->
    [one] Your network profile is currently set to Public ({$adapters}).
          This is not recommended for SlimeVR to function properly.
          <PublicFixLink>See how to fix it here.</PublicFixLink>
    *[many] Some of your network adapters are set to public:
            {$adapters}
            This is not recommended for SlimeVR to function properly.
            <PublicFixLink>See how to fix it here.</PublicFixLink>
}
tracking_checklist-NETWORK_PROFILE_PUBLIC-open = Open Control Panel
tracking_checklist-NETWORK_PROFILE_PUBLIC-settings_desc = Shown when a network adapter is set to Public, which can stop trackers from connecting.
tracking_checklist-STAY_ALIGNED_CONFIGURED = Configure Stay Aligned
tracking_checklist-STAY_ALIGNED_CONFIGURED-desc = Record the Stay Aligned poses to reduce drift
tracking_checklist-STAY_ALIGNED_CONFIGURED-settings_desc = Shown when Stay Aligned is not enabled.
tracking_checklist-STAY_ALIGNED_CONFIGURED-open = Open Stay Aligned settings
tracking_checklist-VRCHAT_OSC_TRACKING_DISABLED = Enable VRChat OSC tracking data
tracking_checklist-VRCHAT_OSC_TRACKING_DISABLED-desc = VRChat is connected, but isn't sending back your head and hand tracking data. Turn on the OSC tracking-data toggle in VRChat's settings. <OscTrackingLink>See how to enable it.</OscTrackingLink>
tracking_checklist-VRCHAT_OSC_TRACKING_DISABLED-settings_desc = Shown when VRChat is connected but does not send back your head and hand tracking data.
tracking_checklist-VRCHAT_OSC_TRACKING_DISABLED-open = Go to VRChat OSC Settings

tracking_checklist-ignore = Ignore

preview-mocap_mode_soon = Mocap Mode (Soon™)
preview-disable_render = Disable rendering
preview-disabled_render = Rendering disabled
preview-render_mode = Change rendering mode
preview-reset_camera = Reset camera

toolbar-mounting_calibration = Mounting Calibration
toolbar-mounting_calibration-pick_method = Pick mounting method
toolbar-mounting_calibration-default = Body
toolbar-mounting_calibration-feet = Feet
toolbar-mounting_calibration-toes = Toes
toolbar-mounting_calibration-fingers = Fingers
toolbar-drift_reset = Drift Reset
toolbar-assigned_trackers = { $count ->
    [one] { $count } tracker assigned
    *[other] { $count } trackers assigned
}
toolbar-unassigned_trackers = { $count ->
    [one] { $count } tracker unassigned
    *[other] { $count } trackers unassigned
}
