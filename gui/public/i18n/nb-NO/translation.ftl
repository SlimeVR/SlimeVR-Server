# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Laster...
websocket-connection_lost = Mistet server-tilkobling. Vennligst vent mens koblingen gjenopprettes...
websocket-connection_lost-desc = Det ser ut som at SlimeVR serveren har krasjet. Sjekk loggene og restart programmet.
websocket-timedout = Klarte ikke å koble til serveren.
websocket-timedout-desc = Det ser ut som at SlimeVR serveren har krasjet eller har brukt for lang til på å koble til. Sjekk loggene og restart programmet.
websocket-error-close = Avslutt SlimeVR
websocket-error-logs = Åpne opp "logs" mappen.

## Update notification

version_update-title = Ny versjon tilgjengelig: { $version }
version_update-description = Å klikke "{ version_update-update }" laster ned SlimeVR installatøren for deg.
version_update-update = Oppdater
version_update-close = Lukk

## Tips

tips-find_tracker = Usikker på hvilken tracker som er hvilken? Rist en av dem! Den ristede trackeren vil bli fremhevet.
tips-do_not_move_heels = Sørg for at dine heler ikke beveger under opptaket!
tips-file_select = Dra og slipp filene for å enten bruke eller <u>gå igjennom</u>.
tips-tap_setup = Du kan forsiktig trykke på din tracker 2 ganger sammenhengende istedenfor å velge den fra menyen.
tips-turn_on_tracker = Bruker du offisielle SlimeVR trackere? Husk å <b><em>skru dem på</em></b> etter å ha koblet dem til PCen din!
tips-failed_webgl = Feil ved initialisering av WebGL.

## Units

unit-meter = Meter
unit-foot = Foot
unit-inch = Inch
unit-cm = cm

## Body parts

body_part-NONE = Ikke tildelt
body_part-HEAD = Hode
body_part-NECK = Hals
body_part-RIGHT_SHOULDER = Høyre skulder
body_part-RIGHT_UPPER_ARM = Høyre overarm
body_part-RIGHT_LOWER_ARM = Høyre underarm
body_part-RIGHT_HAND = Høyre hånd
body_part-RIGHT_UPPER_LEG = Høyre lår
body_part-RIGHT_LOWER_LEG = Høyre ankel
body_part-RIGHT_FOOT = Høyre fot
body_part-UPPER_CHEST = Øvre bryst
body_part-CHEST = Bryst
body_part-WAIST = Midje
body_part-HIP = Hofte
body_part-LEFT_SHOULDER = Venstre skulder
body_part-LEFT_UPPER_ARM = Venstre overarm
body_part-LEFT_LOWER_ARM = Venstre underarm
body_part-LEFT_HAND = Venstre hånd
body_part-LEFT_UPPER_LEG = Venstre lår
body_part-LEFT_LOWER_LEG = Venstre ankel
body_part-LEFT_FOOT = Venstre fot
body_part-LEFT_THUMB_METACARPAL = Venstre tommel mellomhånden
body_part-LEFT_THUMB_PROXIMAL = Venstre tommel proksimal
body_part-LEFT_THUMB_DISTAL = Venstre tommel lengst unna
body_part-LEFT_INDEX_PROXIMAL = Venstre innerste pekefinger
body_part-LEFT_INDEX_INTERMEDIATE = Venstre middels pekefinger
body_part-LEFT_INDEX_DISTAL = Venstre yterste pekefinger
body_part-LEFT_MIDDLE_PROXIMAL = Venstre innerste langfinger
body_part-LEFT_MIDDLE_INTERMEDIATE = Venstre middels langfinger
body_part-LEFT_MIDDLE_DISTAL = Venstre yterste langfinger
body_part-LEFT_RING_PROXIMAL = Venstre innerste ring
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

board_type-UNKNOWN = Ukjent
board_type-NODEMCU = NodeMCU
board_type-CUSTOM = Egendefinert brett
board_type-WROOM32 = WROOM32
board_type-WEMOSD1MINI = Wemos D1 Mini
board_type-TTGO_TBASE = TTGO T-Base
board_type-ESP01 = ESP-01
board_type-SLIMEVR = SlimeVR
board_type-SLIMEVR_DEV = SlimeVR Dev Board
board_type-SLIMEVR_V1_2 = SlimeVR v1.2
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
board_type-GESTURES = Gestures
board_type-ESP32S3_SUPERMINI = ESP32-S3 Supermini
board_type-GENERIC_NRF = Generic nRF
board_type-SLIMEVR_BUTTERFLY_DEV = SlimeVR Dev Butterfly
board_type-SLIMEVR_BUTTERFLY = SlimeVR Butterfly

## Proportions

skeleton_bone-NONE = Ingen
skeleton_bone-HEAD = Hode bytte
skeleton_bone-HEAD-desc =
    This is the distance from your headset to the middle of your head.
    To adjust it, shake your head left to right as if you're disagreeing and modify
    it until any movement in other trackers is negligible.
skeleton_bone-NECK = Halslengde
skeleton_bone-NECK-desc =
    This is the distance from the middle of your head to the base of your neck.
    To adjust it, move your head up and down as if you're nodding or tilt your head
    to the left and right and modify it until any movement in other trackers is negligible.
skeleton_bone-torso_group = Torsolengde
skeleton_bone-torso_group-desc =
    This is the distance from the base of your neck to your hips.
    To adjust it, modify it standing up straight until your virtual hips line
    up with your real ones.
skeleton_bone-UPPER_CHEST = Upper Chest Length
skeleton_bone-UPPER_CHEST-desc =
    This is the distance from the base of your neck to the middle of your chest.
    To adjust it, adjust your Torso Length properly and modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches with your real one.
skeleton_bone-CHEST_OFFSET = Bryst-forskyvning
skeleton_bone-CHEST_OFFSET-desc =
    This can be adjusted to move your virtual chest tracker up or down in order to aid
    with calibration in certain games or applications that may expect it to be higher or lower.
skeleton_bone-CHEST = Bryst-lengde
skeleton_bone-CHEST-desc =
    This is the distance from the middle of your chest to the middle of your spine.
    To adjust it, adjust your Torso Length properly and modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches with your real one.
skeleton_bone-WAIST = Midje-lengde
skeleton_bone-WAIST-desc =
    This is the distance from the middle of your spine to your belly button.
    To adjust it, adjust your Torso Length properly and modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches with your real one.
skeleton_bone-HIP = Hofte-lengde
skeleton_bone-HIP-desc =
    This is the distance from your belly button to your hips.
    To adjust it, set your Torso Length properly and modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches your real one.
skeleton_bone-HIP_OFFSET = Hofte-forskyvning
skeleton_bone-HIP_OFFSET-desc =
    This can be adjusted to move your virtual hip tracker up or down in order to aid
    with calibration in certain games or applications that may expect it to be on your waist.
skeleton_bone-HIPS_WIDTH = Hofte-bredde
skeleton_bone-HIPS_WIDTH-desc =
    This is the distance between the start of your legs.
    To adjust it, perform a full reset with your legs straight and modify it until
    your virtual legs match up with your real ones horizontally.
skeleton_bone-leg_group = Leg length
skeleton_bone-leg_group-desc =
    This is the distance from your hips to your feet.
    To adjust it, adjust your Torso Length properly and modify it
    until your virtual feet are at the same level as your real ones.
skeleton_bone-UPPER_LEG = Lår-lengde
skeleton_bone-UPPER_LEG-desc =
    This is the distance from your hips to your knees.
    To adjust it, adjust your Leg Length properly and modify it
    until your virtual knees are at the same level as your real ones.
skeleton_bone-LOWER_LEG = Leg-lengde
skeleton_bone-LOWER_LEG-desc =
    This is the distance from your knees to your ankles.
    To adjust it, adjust your Leg Length properly and modify it
    until your virtual knees are at the same level as your real ones.
skeleton_bone-FOOT_LENGTH = Fot-lengde
skeleton_bone-FOOT_LENGTH-desc =
    This is the distance from your ankles to your toes.
    To adjust it, tiptoe and modify it until your virtual feet stay in place.
skeleton_bone-FOOT_SHIFT = Fot-bytte
skeleton_bone-FOOT_SHIFT-desc =
    This value is the horizontal distance from your knee to your ankle.
    It accounts for your lower legs going backwards when standing up straight.
    To adjust it, set Foot Length to 0, perform a full reset and modify it until your virtual
    feet line up with the middle of your ankles.
skeleton_bone-SKELETON_OFFSET = Skjellet-forskyvning
skeleton_bone-SKELETON_OFFSET-desc =
    This can be adjusted to offset all your trackers forward or backward.
    It can be used to help with calibration in certain games or applications
    that may expect your trackers to be more forward.
skeleton_bone-SHOULDERS_DISTANCE = Skulder-distanse
skeleton_bone-SHOULDERS_DISTANCE-desc =
    This is the vertical distance from the base of your neck to your shoulders.
    To adjust it, set Upper Arm Length to 0 and modify it until your virtual elbow trackers
    line up vertically with your real shoulders.
skeleton_bone-SHOULDERS_WIDTH = Skulder-bredde
skeleton_bone-SHOULDERS_WIDTH-desc =
    This is the horizontal distance from the base of your neck to your shoulders.
    To adjust it, set Upper Arm Length to 0 and modify it until your virtual elbow trackers
    line up horizontally with your real shoulders.
skeleton_bone-arm_group = Arm length
skeleton_bone-arm_group-desc =
    This is the distance from your shoulders to your wrists.
    To adjust it, adjust Shoulders Distance properly, set Hand Distance Y
    to 0 and modify it until your hand trackers line up with your wrists.
skeleton_bone-UPPER_ARM = Overarms-lengde
skeleton_bone-UPPER_ARM-desc =
    This is the distance from your shoulders to your elbows.
    To adjust it, adjust Arm Length properly and modify it until
    your elbow trackers line up with your real elbows.
skeleton_bone-LOWER_ARM = Nedre arm-lengde
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
skeleton_bone-ELBOW_OFFSET = Albue-forskyvning
skeleton_bone-ELBOW_OFFSET-desc =
    This can be adjusted to move your virtual elbow trackers up or down in order to aid
    with VRChat accidentally binding an elbow tracker to the chest.

## Tracker reset buttons

reset-reset_all = Nullstill alle proporsjoner
reset-reset_all_warning-v2 =
    <b>Warning:</b> Your proportions will be reset to defaults scaled to your configured height.
    Are you sure you want to do this?
reset-reset_all_warning-reset = Reset proportions
reset-reset_all_warning-cancel = Avbryt
reset-reset_all_warning_default-v2 =
    <b>Advarsel:</b> Høyden din har ikke blitt konfigurert, proporsjonene dine vil bli tilbakestilt til standardinnstillingene med standardhøyden.
    Er du sikker på at du vil gjøre dette?
reset-full = Nullstill
reset-mounting = Nullstill montering
reset-mounting-feet = Feet Calibration
reset-mounting-fingers = Fingers Calibration
reset-yaw = Yaw Reset
reset-error-no_feet_tracker = No feet tracker assigned
reset-error-no_fingers_tracker = No finger tracker assigned
reset-error-mounting-need_full_reset = Need a full reset before mounting
reset-error-yaw-need_full_reset = Need a full reset before yaw reset

## Serial detection stuff

serial_detection-new_device-p0 = Ny seriell enhet oppdaget!
serial_detection-new_device-p1 = Tast inn din WiFi legitimasjon!
serial_detection-new_device-p2 = Vennligst velg hva du vil gjøre med den
serial_detection-open_wifi = Koble til Wi-Fi
serial_detection-open_serial = Åpne seriell konsoll
serial_detection-submit = Send inn!
serial_detection-close = Lukk

## Navigation bar

navbar-home = Hjem
navbar-body_proportions = Kropps proporsjoner
navbar-trackers_assign = Tracker tildeling
navbar-mounting = Monterings kalibrering
navbar-onboarding = Oppsetts veiviser
navbar-settings = Innstillinger
navbar-connect_trackers = Connect Trackers

## Biovision hierarchy recording

bvh-start_recording = BVH-innspilling
bvh-stop_recording = Save BVH recording
bvh-recording = Spiller inn...
bvh-save_title = Save BVH recording

## Tracking pause

tracking-unpaused = Pause tracking
tracking-paused = Unpause tracking

## Widget: Overlay settings

widget-overlay = Overlegg
widget-overlay-is_visible_label = Vis overlegg i SteamVR
widget-overlay-is_mirrored_label = Vis overlegg som speil

## Widget: Drift compensation

widget-drift_compensation-clear = Clear drift compensation

## Widget: Clear Mounting calibration

widget-clear_mounting = Clear mounting calibration

## Widget: Developer settings

widget-developer_mode = Utvikler modus
widget-developer_mode-high_contrast = Høy kontrast
widget-developer_mode-precise_rotation = Nøyaktig rotering
widget-developer_mode-fast_data_feed = Rask data-feed
widget-developer_mode-filter_slimes_and_hmd = Filtrer slimes og HMD
widget-developer_mode-sort_by_name = Sorter etter navn
widget-developer_mode-raw_slime_rotation = Rå rotering
widget-developer_mode-more_info = Mer info

## Widget: IMU Visualizer

widget-imu_visualizer = Rotasjon
widget-imu_visualizer-preview = Preview
widget-imu_visualizer-hide = Hide
widget-imu_visualizer-rotation_raw = Rå
widget-imu_visualizer-rotation_preview = Forhåndsvisning
widget-imu_visualizer-acceleration = Acceleration
widget-imu_visualizer-position = Position
widget-imu_visualizer-stay_aligned = Stay Aligned

## Widget: Skeleton Visualizer

widget-skeleton_visualizer-preview = Skeleton preview
widget-skeleton_visualizer-hide = Hide

## Tracker status

tracker-status-none = Ingen status
tracker-status-busy = Opptatt
tracker-status-error = Feilmelding
tracker-status-disconnected = Frakoblet
tracker-status-occluded = Okkludert
tracker-status-ok = OK
tracker-status-timed_out = Timed out

## Tracker status columns

tracker-table-column-name = Navn
tracker-table-column-type = Type
tracker-table-column-battery = Batteri
tracker-table-column-ping = Ping
tracker-table-column-packet_loss = Packet Loss
tracker-table-column-tps = TPS
tracker-table-column-temperature = Temp. °C
tracker-table-column-linear-acceleration = Aksel. X/Y/Z
tracker-table-column-rotation = Rotasjon X/Y/Z
tracker-table-column-position = Posisjon X/Y/Z
tracker-table-column-stay_aligned = Stay Aligned
tracker-table-column-url = URL

## Tracker rotation

tracker-rotation-front = Front
tracker-rotation-front_left = Front-Left
tracker-rotation-front_right = Front-Right
tracker-rotation-left = Venstre
tracker-rotation-right = Høyre
tracker-rotation-back = Bak
tracker-rotation-back_left = Back-Left
tracker-rotation-back_right = Back-Right
tracker-rotation-custom = Egendefinert
tracker-rotation-overriden = (overridden by mounting calibration)

## Tracker information

tracker-infos-manufacturer = Produsent
tracker-infos-display_name = Vis navn
tracker-infos-custom_name = Tilpasset navn
tracker-infos-url = Tracker URL
tracker-infos-version = Firmware Version
tracker-infos-hardware_rev = Hardware Revision
tracker-infos-hardware_identifier = Hardware ID
tracker-infos-data_support = Data support
tracker-infos-imu = IMU Sensor
tracker-infos-board_type = Main board
tracker-infos-network_version = Protocol Version
tracker-infos-magnetometer = Magnetometer
tracker-infos-magnetometer-status-v1 =
    { $status ->
       *[NOT_SUPPORTED] Not supported
        [DISABLED] Disabled
        [ENABLED] Enabled
    }
tracker-infos-packet_loss = Packet Loss
tracker-infos-packets_lost = Packets Lost
tracker-infos-packets_received = Packets Received

## Tracker settings

tracker-settings-back = Gå tilbake til tracker-liste
tracker-settings-title = Tracker innstillinger
tracker-settings-assignment_section = Tildeling
tracker-settings-assignment_section-description = Kroppsdelen trackeren er blitt tildelt.
tracker-settings-assignment_section-edit = Endre tildeling
tracker-settings-mounting_section = Monterings posisjon
tracker-settings-mounting_section-description = Hvor er trackeren montert?
tracker-settings-mounting_section-edit = Endre montering
tracker-settings-drift_compensation_section = Tillat avdrifts-kompensasjon
tracker-settings-drift_compensation_section-description = Skal denne trackeren kompensere for egen avdrift når avsdrifts-kompansasjon er aktivert?
tracker-settings-drift_compensation_section-edit = Tillat avdrifts-kompensasjon
tracker-settings-use_mag = Allow magnetometer on this tracker
# Multiline!
tracker-settings-use_mag-description =
    Should this tracker use magnetometer to reduce drift when magnetometer usage is allowed? <b>Please don't shutdown your tracker while toggling this!</b>
    
    You need to allow magnetometer usage first, <magSetting>click here to go to the setting</magSetting>.
tracker-settings-use_mag-label = Allow magnetometer
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Tracker navn
tracker-settings-name_section-description = Gi den et søtt kallenavn :)
tracker-settings-name_section-placeholder = ~Thͭiͪaͥsͣˢ~ venstre ben
tracker-settings-name_section-label = Tracker name
tracker-settings-forget = Forget tracker
tracker-settings-forget-description = Removes the tracker from the SlimeVR Server and prevents it from connecting until the server is restarted. The configuration of the tracker won't be lost.
tracker-settings-forget-label = Forget tracker
tracker-settings-update-unavailable-v2 = No releases found
tracker-settings-update-incompatible = Cannot update. Incompatible board or firmware version
tracker-settings-update-low-battery = Cannot update. Battery lower than 50%
tracker-settings-update-up_to_date = Up to date
tracker-settings-update-blocked = Update not available. No other releases available
tracker-settings-update = Update now
tracker-settings-update-title = Firmware version
tracker-settings-current-version = Current
tracker-settings-latest-version = Latest
tracker-settings-build-date = Build Date

## Tracker part card info

tracker-part_card-no_name = Ingen navn
tracker-part_card-unassigned = Utilordnet

## Body assignment menu

body_assignment_menu = Hvor vil du plassere denne trackeren?
body_assignment_menu-description = Velg den plasseringen du vil tildele denne trackeren. Alternativt kan du velge å ordne alle trackere samtidig istedenfor å ordne dem én etter én.
body_assignment_menu-show_advanced_locations = Vis avanserte tildelings-plasseringer
body_assignment_menu-manage_trackers = Ordne alle trackere
body_assignment_menu-unassign_tracker = Fjern tracker-tildeling

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = { "Hvilken tracker skal tildeles til " }
tracker_selection_menu-NONE = Hvilken tracker vil du fjerne tildelingen på?
tracker_selection_menu-HEAD = { -tracker_selection-part }hodet?
tracker_selection_menu-NECK = { -tracker_selection-part }halsen?
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part }høyre skulder?
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part }høyre overarm?
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part }høyre nedre arm?
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part }høyre hånd?
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part }høyre lår?
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part }høre ankel?
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part }høyre fot?
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part }høyre kontroller?
tracker_selection_menu-UPPER_CHEST = { -tracker_selection-part } upper chest?
tracker_selection_menu-CHEST = { -tracker_selection-part }brystet?
tracker_selection_menu-WAIST = { -tracker_selection-part }midjen?
tracker_selection_menu-HIP = { -tracker_selection-part }hoften?
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part }venstre skulder?
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part }venstre overarm?
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part }venstre nedre arm?
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part }venstre hånd?
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part }venstre lår?
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part }venstre ankel?
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part }venstre fot?
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part }venstre kontroller?
tracker_selection_menu-unassigned = Utilordnede trackere
tracker_selection_menu-assigned = Tildelte trackere
tracker_selection_menu-dont_assign = Ikke tildel
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>NB:</b> Hals-trackeren kan være helseskadelig dersom den sitter for stramt,
    festet kan blokkere blodtilførselen til hodet ditt!
tracker_selection_menu-neck_warning-done = Jeg forstår risikoene
tracker_selection_menu-neck_warning-cancel = Avbryt

## Mounting menu

mounting_selection_menu = Hvor vil du at denne trackeren skal være?
mounting_selection_menu-close = Lukk

## Sidebar settings

settings-sidebar-title = Innstillinger
settings-sidebar-general = Generelle
settings-sidebar-steamvr = SteamVR
settings-sidebar-tracker_mechanics = Tracker-mekanisme
settings-sidebar-stay_aligned = Stay Aligned
settings-sidebar-fk_settings = Tracker-innstillinger
settings-sidebar-gesture_control = Bevegelses-kontroll
settings-sidebar-interface = Grensesnitt
settings-sidebar-osc_router = OSC ruter
settings-sidebar-osc_trackers = VRChat OSC Trackers
settings-sidebar-osc_vmc = VMC
settings-sidebar-utils = Verktøy
settings-sidebar-serial = Seriell konsoll
settings-sidebar-appearance = Appearance
settings-sidebar-home = Home Screen
settings-sidebar-checklist = Tracking checklist
settings-sidebar-notifications = Notifications
settings-sidebar-behavior = Behavior
settings-sidebar-firmware-tool = DIY Firmware Tool
settings-sidebar-vrc_warnings = VRChat Config Warnings
settings-sidebar-advanced = Advanced

## SteamVR settings

settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = SteamVR trackere
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    Skru av/på spesifikke SteamVR trackere.
    Nyttig for spill eller programmer som bare støtter visse trackere.
settings-general-steamvr-trackers-waist = Midje
settings-general-steamvr-trackers-chest = Bryst
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
settings-general-steamvr-trackers-hands-warning =
    <b>Warning:</b> Enabling the SteamVR hand trackers will disable inputs from real controllers.
    This should only be enabled if you are using SlimeVR for hand tracking.
    
    Are you sure you want to do this?
settings-general-steamvr-trackers-hands-warning-cancel = Avbryt
settings-general-steamvr-trackers-hands-warning-done = Yes

## Tracker mechanics

settings-general-tracker_mechanics = Tracker-mekanismer
settings-general-tracker_mechanics-filtering = Filtrering
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Velg filtrerings-type for dine trackere.
    Prediksjon forutser bevegelser mens Utjevning jevner ut bevegelser.
settings-general-tracker_mechanics-filtering-type = Filtrerings type
settings-general-tracker_mechanics-filtering-type-none = Ingen filtrering
settings-general-tracker_mechanics-filtering-type-none-description = Bruk rotasjoner som før. Altså ingen filtrering.
settings-general-tracker_mechanics-filtering-type-smoothing = Utjevning
settings-general-tracker_mechanics-filtering-type-smoothing-description = Jevner ut bevegelser men øker forsinkelsen litt.
settings-general-tracker_mechanics-filtering-type-prediction = Forutsigelse
settings-general-tracker_mechanics-filtering-type-prediction-description = Reduserer forsinkelsen og gjør bevegelser skarpere, men kan påvirke stabiliteten.
settings-general-tracker_mechanics-filtering-amount = Mengde
settings-general-tracker_mechanics-yaw-reset-smooth-time = Yaw reset smooth time (0s disables smoothing)
settings-general-tracker_mechanics-drift_compensation = Avdrifts-kompansering
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Kompenserer for IMU yaw avdrift ved å legge til en rotasjon av omvendt verdi.
    Endre mengde kompensasjon og opp til hvor mange nullstillinger som skal bli gjort rede for.
settings-general-tracker_mechanics-drift_compensation-enabled-label = Avdrifts kompansering
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
settings-general-tracker_mechanics-drift_compensation_warning-cancel = Avbryt
settings-general-tracker_mechanics-drift_compensation_warning-done = I understand
settings-general-tracker_mechanics-drift_compensation-amount-label = Kompanserings mengde
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Bruk opp til x siste nullstillinger
settings-general-tracker_mechanics-save_mounting_reset = Save automatic mounting calibration
settings-general-tracker_mechanics-save_mounting_reset-description =
    Saves the automatic mounting calibration for the trackers between restarts. Useful
    when wearing a suit where trackers don't move between sessions. <b>Not recommended for normal users!</b>
settings-general-tracker_mechanics-save_mounting_reset-enabled-label = Save mounting calibration
settings-general-tracker_mechanics-use_mag_on_all_trackers = Bruk magnetometer på alle IMU-trackere som støtter det
settings-general-tracker_mechanics-use_mag_on_all_trackers-description =
    Bruker magnetometer på alle trackere som har en kompatibel firmware for det, noe som reduserer drift i stabile magnetiske områder.
    Kan deaktiveres per tracker i trackerens innstillinger. <b>Ikke slå av noen av trackerne mens du endrer denne innstillingen !</b>
settings-general-tracker_mechanics-use_mag_on_all_trackers-label = Bruk magnetometer på trackere
settings-general-tracker_mechanics-trackers_over_usb = Trackers over USB
settings-general-tracker_mechanics-trackers_over_usb-description = Enables receiving HID tracker data over USB. Make sure connected trackers have <b>connection over HID</b> enabled!
settings-general-tracker_mechanics-trackers_over_usb-enabled-label = Allow HID trackers to connect directly over USB
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

settings-general-fk_settings = Tracking innstillinger
# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Gulv-clip
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Skating korreksjon
settings-general-fk_settings-leg_tweak-toe_snap = Toe snap
settings-general-fk_settings-leg_tweak-foot_plant = Foot plant
settings-general-fk_settings-leg_tweak-skating_correction-amount = Skating-korreksjon styrke
settings-general-fk_settings-leg_tweak-skating_correction-description = Skating-correction corrects for ice skating, but can decrease accuracy in certain movement patterns. When enabling this, make sure to perform a full reset and recalibrate in-game.
settings-general-fk_settings-leg_tweak-floor_clip-description = Floor-clip can reduce or eliminate clipping through the floor. When enabling this, make sure to perform a full reset and recalibrate in-game.
settings-general-fk_settings-leg_tweak-toe_snap-description = Toe-snap attempts to guess the rotation of your feet if foot trackers are not in use.
settings-general-fk_settings-leg_tweak-foot_plant-description = Foot-plant rotates your feet to be parallel to the ground when in contact.
settings-general-fk_settings-leg_fk = Leg tracking
settings-general-fk_settings-leg_fk-reset_mounting_feet-description-v1 = Force feet mounting calibration during body mounting calibration.
settings-general-fk_settings-leg_fk-reset_mounting_feet-v1 = Force feet mounting calibration
settings-general-fk_settings-enforce_joint_constraints = Skeletal Limits
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints = Enforce constraints
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = Prevents joints from rotating past their limit
settings-general-fk_settings-enforce_joint_constraints-correct_constraints = Correct with constraints
settings-general-fk_settings-enforce_joint_constraints-correct_constraints-description = Correct joint rotations when they push past their limit
settings-general-fk_settings-ik = Position data
settings-general-fk_settings-ik-use_position = Use Position data
settings-general-fk_settings-ik-use_position-description = Enables the use of position data from trackers that provide it. When enabling this make sure to full reset and recalibrate in game.
settings-general-fk_settings-velocity_settings = Velocity Settings
settings-general-fk_settings-velocity_settings-description = Send derived velocity data to SteamVR. Required for Natural Locomotion support. May cause jitter in FBT.
settings-general-fk_settings-velocity_settings-send_derived_velocity = Send derived velocity to driver
settings-general-fk_settings-arm_fk = Arm sporing
settings-general-fk_settings-arm_fk-description = Endre metoden armene spores på.
settings-general-fk_settings-arm_fk-force_arms = Tving armer fra HMD
settings-general-fk_settings-reset_settings = Reset settings
settings-general-fk_settings-reset_settings-reset_hmd_pitch-description = Reset the HMD's pitch (vertical rotation) upon doing a full reset. Useful if wearing an HMD on the forehead for VTubing or mocap. Do not enable for VR.
settings-general-fk_settings-reset_settings-reset_hmd_pitch = Reset HMD pitch
settings-general-fk_settings-arm_fk-reset_mode-description = Change which arm pose is expected for mounting calibration.
settings-general-fk_settings-arm_fk-back = Back
settings-general-fk_settings-arm_fk-back-description = The default mode, with the upper arms going back and lower arms going forward.
settings-general-fk_settings-arm_fk-tpose_up = T-pose (up)
settings-general-fk_settings-arm_fk-tpose_up-description = Expects your arms to be down at your sides during Full Reset, and 90 degrees up to the sides during Mounting Calibration.
settings-general-fk_settings-arm_fk-tpose_down = T-pose (down)
settings-general-fk_settings-arm_fk-tpose_down-description = Expects your arms to be 90 degrees up to the sides during Full Reset, and down at your sides during Mounting Calibration.
settings-general-fk_settings-arm_fk-forward = Forward
settings-general-fk_settings-arm_fk-forward-description = Expects your arms to be raised forward at 90 degrees. Useful for VTubing.
settings-general-fk_settings-skeleton_settings-toggles = Skeleton toggles
settings-general-fk_settings-skeleton_settings-description = Skru skjellet innstillinger av eller på. Det anbefales å la disse stå på.
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

settings-general-gesture_control = Bevegelses-kontroll
settings-general-gesture_control-subtitle = Trykk baserte nullstillinger
settings-general-gesture_control-description = Tillat at nullstillinger aktiveres ved å trykke på en sporer. Den høyeste sporeren på overkroppen blir da brukt til Rask Nullstilling, den høyeste sporeren på det venstre beinet blir brukt til Nullstilling og den høyeste sporeren på det høyre beinet blir brukt til Monterings Nullstilling. Vær obs på at trykking bør skje to ganger innen 0.6 sekunder for å bli registrert.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
        [one] 1 trykk
       *[other] { $amount } trykk
    }
# This is a unit: 3 trackers, 2 trackers, 1 tracker
# $amount (Number) - Amount of trackers
settings-general-gesture_control-trackers =
    { $amount ->
        [one] 1 tracker
       *[other] { $amount } trackers
    }
settings-general-gesture_control-yawResetEnabled = Enable tap to yaw reset
settings-general-gesture_control-yawResetDelay = Yaw reset delay
settings-general-gesture_control-yawResetTaps = Taps for yaw reset
settings-general-gesture_control-yawResetTracker = Yaw reset tracker
settings-general-gesture_control-fullResetEnabled = Enable tap to full reset
settings-general-gesture_control-fullResetDelay = Full reset delay
settings-general-gesture_control-fullResetTaps = Taps for full reset
settings-general-gesture_control-fullResetTracker = Full reset tracker
settings-general-gesture_control-mountingResetEnabled = Aktiver monterings-nullstilling ved trykking
settings-general-gesture_control-mountingResetDelay = Monterings-nullstilling utsettelse
settings-general-gesture_control-mountingResetTaps = Trykking for monterings-nullstilling
settings-general-gesture_control-mountingResetTracker = Mounting reset tracker
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = Trackers over threshold
settings-general-gesture_control-numberTrackersOverThreshold-description = Increase this value if tap detection is not working. Do not increase it above what is needed to make tap detection work as it would cause more false positives.

## Appearance settings

settings-interface-appearance = Appearance
settings-general-interface-dev_mode = Utvikler modus
settings-general-interface-dev_mode-description = Denne modusen kan være hjelpsom dersom du trenger data som gir mer innsyn eller for å samhandle med tilkoblede sporere på et mer avansert nivå.
settings-general-interface-dev_mode-label = Utvikler modus
settings-general-interface-theme = Color theme
settings-general-interface-show-navbar-onboarding = Show "{ navbar-onboarding }" on navigation bar
settings-general-interface-show-navbar-onboarding-description = This changes whether the "{ navbar-onboarding }" button shows on the navigation bar.
settings-general-interface-show-navbar-onboarding-label = Show "{ navbar-onboarding }"
settings-general-interface-lang = Velg språk
settings-general-interface-lang-description = Endre hovedspråket du vil bruke.
settings-general-interface-lang-placeholder = Velg språket du vil bruke
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
settings-general-interface-serial_detection = Seriell enhets gjenkjenning
settings-general-interface-serial_detection-description = Dette valget viser en pop-up for hver gang du kobler til en ny seriell-enhet som kunne vært en sporer. Dette forbedrer sporerens konfigureringsprosess.
settings-general-interface-serial_detection-label = Seriell enhets gjenkjenning
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
settings-general-interface-discord_presence-message =
    { $amount ->
        [0] Sliming around
        [one] Using 1 tracker
       *[other] Using { $amount } trackers
    }
settings-interface-behavior-error_tracking = Error collection via Sentry.io
settings-interface-behavior-error_tracking-description_v2 =
    <h1>Samtykker du til innsamling av anonymiserte feildata?</h1>
    
    <b>Vi samler ikke inn personlig informasjon</b> slik som din IP-adresse eller trådløs-legitimasjon. SlimeVR verdsetter ditt personvern!
    
    For å gi deg den beste brukeropplevelsen, samler vi inn anonymiserte feilrapporter, ytelsesmålinger og informasjon om operativsystemet. Dette hjelper oss med å oppdage feil og problemer med SlimeVR. Disse beregningene samles inn via Sentry.io.
settings-interface-behavior-error_tracking-label = Send feilmeldinger til utviklere
settings-interface-behavior-bvh_directory = Directory to save BVH recordings
settings-interface-behavior-bvh_directory-description = Choose a directory to save your BVH recordings instead of having to choose where to save them each time.
settings-interface-behavior-bvh_directory-label = Directory for BVH recordings

## Serial settings

settings-serial = Seriell konsoll
# This cares about multilines
settings-serial-description =
    Dette er en live informasjons-feed for seriell kommunikasjon.
    Kan være hjelpsomt hvis du lurer på om det er problemer med fastvaren.
settings-serial-connection_lost = Tilkobling til serie tapt, gjenopptar tilkobling...
settings-serial-reboot = Omstart
settings-serial-factory_reset = Fabrikktilbakestilling
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>NB:</b> Dette nullstiller trackeren tilbake til fabrikkinstillinger.
    Som betyr at Wi-Fi og kalibrerings innstillingene dine </b>vil bli tapt!</b>
settings-serial-factory_reset-warning-ok = Jeg vet hva jeg driver med
settings-serial-factory_reset-warning-cancel = Avslutt
settings-serial-serial_select = Velg en serieport
settings-serial-auto_dropdown_item = Auto
settings-serial-get_wifi_scan = Get WiFi Scan
settings-serial-enter_pairing = Enter Pairing
settings-serial-exit_pairing = Exit Pairing
settings-serial-calibrate = Calibrate
settings-serial-six_side_calibrate = 6-Side Calibrate
settings-serial-dfu = Enter DFU
settings-serial-meow = Meow!
settings-serial-file_type = Plain text
settings-serial-save_logs = Save To File
settings-serial-send_command = Send
settings-serial-send_command-placeholder = Command...
settings-serial-send_command-warning = <b>Warning:</b> Running serial commands can lead to data loss or brick the trackers.
settings-serial-send_command-warning-ok = I know what I'm doing
settings-serial-send_command-warning-cancel = Cancel

## OSC router settings

settings-osc-router = OSC ruter
# This cares about multilines
settings-osc-router-description =
    Videresend OSC beskjeder fra andre programmer.
    Nyttig når du bruker andre OSC programmer med VRChat for eksempel.
settings-osc-router-enable = Aktiver
settings-osc-router-enable-description = Skru av/på videresending av beskjeder.
settings-osc-router-enable-label = Aktiver
settings-osc-router-network = Nettverks-porter
# This cares about multilines
settings-osc-router-network-description =
    Still inn portene som skal motta eller sende data.
    Disse kan være lik som andre porter brukt i SlimeVR serveren.
settings-osc-router-network-port_in = 
    .label = Port inn
    .placeholder = Port inn (normalverdi:9002)
settings-osc-router-network-port_out = 
    .label = Port Ut
    .placeholder = Port ut (normalverdi: 9000)
settings-osc-router-network-address = Nettverksadresse
settings-osc-router-network-address-description = Tast inn adressen som skal motta data.
settings-osc-router-network-address-placeholder = IPV4 adresse

## OSC VRChat settings

settings-osc-vrchat = VRChat OSC Trackere
# This cares about multilines
settings-osc-vrchat-description-v1 =
    Change settings specific to the OSC Trackers standard used for sending
    tracking data to applications without SteamVR (ex. Quest standalone).
    Make sure to enable OSC in VRChat via the Action Menu under OSC > Enabled.
settings-osc-vrchat-enable = Aktiver
settings-osc-vrchat-enable-description = Skru av/på utsending og mottakelse av data.
settings-osc-vrchat-enable-label = Aktiver
settings-osc-vrchat-oscqueryEnabled = Enable OSCQuery
settings-osc-vrchat-oscqueryEnabled-description =
    OSCQuery automatically detects running instances of VRChat and sends them data.
    It can also advertise itself to them in order to receive HMD and controller data.
    To allow receiving HMD and controller data from VRChat, go in your main menu's settings
    under "Tracking & IK" and enable "Allow Sending Head and Wrist VR Tracking OSC Data".
settings-osc-vrchat-oscqueryEnabled-label = Enable OSCQuery
settings-osc-vrchat-network = Nettverks-porter
settings-osc-vrchat-network-description-v1 = Set the ports for listening and sending data. Can be left untouched for VRChat.
settings-osc-vrchat-network-port_in = 
    .label = Port Inn
    .placeholder = Port inn (normalverdi: 9001)
settings-osc-vrchat-network-port_out = 
    .label = Port Ut
    .placeholder = Port ut (standardisering: 9000)
settings-osc-vrchat-network-address = Nettverksadresse
settings-osc-vrchat-network-address-description-v1 = Choose which address to send out data to. Can be left untouched for VRChat.
settings-osc-vrchat-network-address-placeholder = VRChat ip adresse
settings-osc-vrchat-network-trackers = Trackere
settings-osc-vrchat-network-trackers-description = Skru av/på sendingen av spesifikke sporere via OSC.
settings-osc-vrchat-network-trackers-chest = Bryst
settings-osc-vrchat-network-trackers-hip = Hofte
settings-osc-vrchat-network-trackers-knees = Knær
settings-osc-vrchat-network-trackers-feet = Føtter
settings-osc-vrchat-network-trackers-elbows = Albuer

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

## Common OSC settings

settings-osc-common-network-ports_match_error = The OSC Router in and out ports can't be the same!
settings-osc-common-network-port_banned_error = The port { $port } can't be used!

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

## Home Screen

settings-home-list-layout = Trackers list layout
settings-home-list-layout-desc = Select one of the possible layouts of the home screen
settings-home-list-layout-grid = Grid
settings-home-list-layout-table = Table

## Tracking Checlist

settings-tracking_checklist-active_steps = Active Steps
settings-tracking_checklist-active_steps-desc = List of all the steps in the tracking checklist. You can choose to disable specific steps.

## Setup/onboarding menu

onboarding-skip = Hopp over oppsett
onboarding-continue = Fortsett
onboarding-wip = Arbeid pågår
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
onboarding-quiz-slimeset-answer-wifi = WiFi-based Slime
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
    Most trackers (such as official SlimeVR trackers) use Wi-Fi to connect to the server.
    Please use the credentials of the Wi-Fi network your device is currently connected to.
    
    Make sure to use a 2.4GHz Wi-Fi connection for your trackers!
onboarding-wifi_creds-skip = Hopp over Wi-Fi innstillinger
onboarding-wifi_creds-submit = Send inn!
onboarding-wifi_creds-ssid = 
    .label = Wi-Fi navn
    .placeholder = Tast inn navnet på ditt Wi-Fi nettverk
onboarding-wifi_creds-ssid-required = Wi-Fi name is required
onboarding-wifi_creds-password = 
    .label = Passord
    .placeholder = Tast inn passord
onboarding-wifi_creds-dongle-title = Trackers using a dongle
onboarding-wifi_creds-dongle-description = If your trackers came with a dongle, plug it into your device and you should be good to go!
onboarding-wifi_creds-dongle-wip = This section is a work in progress. A dedicated page to manage trackers that connect via a dongle will be made soon.
onboarding-wifi_creds-dongle-continue = Continue with a dongle

## Mounting setup

onboarding-reset_tutorial-back = Gå tilbake til monterings kalibrering
onboarding-reset_tutorial = Nullstill opplæringen
onboarding-reset_tutorial-explanation = While you use your trackers, they might get out of alignment because of IMU yaw drift, or because you might have moved them physically. You have several ways to fix this.
onboarding-reset_tutorial-skip = Hopp over trinn
# Cares about multiline
onboarding-reset_tutorial-0 =
    Tap the highlighted tracker { $taps } times to trigger a yaw reset.
    
    This will make the trackers face the same direction as your headset (HMD).
# Cares about multiline
onboarding-reset_tutorial-1 =
    Tap the highlighted tracker { $taps } times to trigger a full reset.
    
    You need to be standing for this (i-pose). There is a 3 seconds delay (configurable) before it actually happens.
    This fully resets the position and rotation of all your trackers. It should fix most issues.
# Cares about multiline
onboarding-reset_tutorial-2 =
    Tap the highlighted tracker { $taps } times to trigger mounting calibration.
    
    Mounting calibration adjusts for how trackers are placed on your body. If they've moved or rotated significantly, this helps recalibrate their orientation.
    
    You need to be in a pose like you are skiing as shown in the Automatic Mounting wizard, and you have a 3 second delay (configurable) before it gets triggered.

## Install info

install-info_udev-rules_modal_title = Hardware udev access rules not found
install-info_udev-rules_warning = Access rules via udev are required for serial console access & dongle connection. Paste the following command into your terminal to add the udev rules.
install-info_udev-rules_modal_button = Close
install-info_udev-rules_modal-dont-show-again_checkbox = Don't show again

## Setup start

onboarding-home = Velkommen til SlimeVR
onboarding-home-start = La oss sette i gang!

## Setup done

onboarding-done-title = Nå er alt klart!
onboarding-done-description = Nyt din hel-kropps opplevelse
onboarding-done-close = Lukk guiden

## Tracker connection setup

onboarding-connect_tracker-back = Gå tilbake til Wi-Fi legitimasjon
onboarding-connect_tracker-title = Koble til trackere
onboarding-connect_tracker-description-p0-v1 = Now onto the fun part, connecting trackers!
onboarding-connect_tracker-description-p1-v1 = Connect each tracker one at a time through a USB port.
onboarding-connect_tracker-issue-serial = Jeg sliter med å koble til!
onboarding-connect_tracker-usb = USB Tracker
onboarding-connect_tracker-connection_status-none = Looking for trackers
onboarding-connect_tracker-connection_status-serial_init = Connecting to serial device
onboarding-connect_tracker-connection_status-obtaining_mac_address = Obtaining the tracker mac address
onboarding-connect_tracker-connection_status-provisioning = Sending Wi-Fi credentials
onboarding-connect_tracker-connection_status-connecting = Sender Wi-Fi legitimasjon
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
onboarding-connect_tracker-connected_trackers =
    { $amount ->
        [0] Ingen trackere tilkoblet
        [one] 1 tracker tilkoblet
       *[other] { $amount } trackere tilkoblet
    }
onboarding-connect_tracker-next = Jeg har tilkoblet alle mine trackere

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

onboarding-assign_trackers-back = Gå tilbake til Wi-Fi legitimasjon
onboarding-assign_trackers-title = Tildel trackerne
onboarding-assign_trackers-description = La oss velge hvilke trackere som skal hvor. Trykk på stedet der du vil plassere en tracker
onboarding-assign_trackers-unassign_all = Unassign all trackers
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned =
    { $trackers ->
        [one] { $assigned } av 1 tracker tildelt
       *[other] { $assigned } av { $trackers } trackere tildelt
    }
onboarding-assign_trackers-advanced = Vis avanserte tildelings-plasseringer
onboarding-assign_trackers-next = Jeg har tildelt alle trackerne
onboarding-assign_trackers-mirror_view = Mirror view
onboarding-assign_trackers-option-amount =
    { $trackersCount ->
        [one] x{ $trackersCount }
       *[other] x{ $trackersCount }
    }
onboarding-assign_trackers-option-label =
    { $mode ->
        [lower-body] Lower-Body Set
        [core] Core Set
        [enhanced-core] Enhanced Core Set
        [full-body] Full-Body Set
       *[all] All Trackers
    }
onboarding-assign_trackers-option-description =
    { $mode ->
        [lower-body] Minimum for VR full-body tracking
        [core] + Enhanced spine tracking
        [enhanced-core] + Foot rotation
        [full-body] + Elbow tracking
       *[all] All available tracker assignments
    }

## Tracker assignment warnings

# Note for devs, number is used for representing boolean states per bit.
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_FOOT =
    Left foot is assigned but you need { $unassigned ->
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
onboarding-assign_trackers-warning-RIGHT_FOOT =
    Right foot is assigned but you need { $unassigned ->
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
onboarding-assign_trackers-warning-LEFT_LOWER_LEG =
    Left ankle is assigned but you need { $unassigned ->
        [0] the left thigh and either the chest, hip or waist
        [1] either the chest, hip or waist
        [2] the left thigh
       *[unknown] Unknown unassigned body part
    } to also be assigned!
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_LOWER_LEG =
    Right ankle is assigned but you need { $unassigned ->
        [0] the right thigh and either the chest, hip or waist
        [1] either the chest, hip or waist
        [2] the right thigh
       *[unknown] Unknown unassigned body part
    } to also be assigned!
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_UPPER_LEG =
    Left thigh is assigned but you need { $unassigned ->
        [0] either the chest, hip or waist
       *[unknown] Unknown unassigned body part
    } to also be assigned!
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_UPPER_LEG =
    Right thigh is assigned but you need { $unassigned ->
        [0] either the chest, hip or waist
       *[unknown] Unknown unassigned body part
    } to also be assigned!
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-HIP =
    Hip is assigned but you need { $unassigned ->
        [0] the chest
       *[unknown] Unknown unassigned body part
    } to also be assigned!
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-WAIST =
    Waist is assigned but you need { $unassigned ->
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
onboarding-choose_mounting-manual_modal-title =
    Are you sure you want to do
    the automatic mounting calibration?
onboarding-choose_mounting-manual_modal-description = <b>The manual mounting calibration is recommended for new users</b>, as the automatic mounting calibration's poses can be hard to get right first and may require some practice.
onboarding-choose_mounting-manual_modal-confirm = I'm sure of what I'm doing
onboarding-choose_mounting-manual_modal-cancel = Avbryt

## Tracker manual mounting setup

onboarding-manual_mounting-back = Gå tilbake for å tre inn i VR
onboarding-manual_mounting = Manuell montering
onboarding-manual_mounting-description = Trykk på en hver tracker og velg hvilken vei de er montert
onboarding-manual_mounting-auto_mounting = Automatisk montering
onboarding-manual_mounting-next = Neste steg

## Tracker automatic mounting setup

onboarding-automatic_mounting-back = Gå tilbake for å tre inn i VR
onboarding-automatic_mounting-title = Monterings Kalibrering
onboarding-automatic_mounting-description = For at SlimeVR trackere skal funke, må vi tildele en monterings-rotasjon til dine trackere for at de skal samstemme med din fysiske tracker-montering.
onboarding-automatic_mounting-manual_mounting = Sett opp montering manuelt
onboarding-automatic_mounting-next = Neste steg
onboarding-automatic_mounting-prev_step = Forrige steg
onboarding-automatic_mounting-done-title = Monterings-rotasjoner kalibrert.
onboarding-automatic_mounting-done-description = Din monterings-kalibrasjon er fullført!
onboarding-automatic_mounting-done-restart = Tilbake til start
onboarding-automatic_mounting-mounting_reset-title = Monterings nullstilling
onboarding-automatic_mounting-mounting_reset-step-0 = 1. Gjør knebøy som om du står på ski, bøyde knær, overkroppen rettet forover og armer bøyd.
onboarding-automatic_mounting-mounting_reset-step-1 = 2. Trykk på "Nullstill montering" knappen og vent 3 sekunder før trackernes monterings-rotasjon nullstilles.
onboarding-automatic_mounting-mounting_reset-feet-step-0 = 1. Stand on your toes with both feet pointing forward. Alternatively you can do it sitting on a chair.
onboarding-automatic_mounting-mounting_reset-feet-step-1 = 2. Press the "Feet calibration" button and wait for 3 seconds before the trackers' mounting orientations will reset.
onboarding-automatic_mounting-preparation-title = Forberedning
onboarding-automatic_mounting-preparation-v2-step-0 = 1. Press the "Full Reset" button.
onboarding-automatic_mounting-preparation-v2-step-1 = 2. Stand upright with your arms to your sides. Make sure to look forward.
onboarding-automatic_mounting-preparation-v2-step-2 = 3. Hold the position until the 3s timer ends.
onboarding-automatic_mounting-put_trackers_on-title = Ta på deg dine trackere
onboarding-automatic_mounting-put_trackers_on-description = For å kalibrere monterings-rotasjonene, må vi bruke trackerne du akkurat tildelte. Ta på deg alle dine trackere, du kan se hvem som er hvem i figuren til høyre.
onboarding-automatic_mounting-put_trackers_on-next = Jeg har alle mine trackere på
onboarding-automatic_mounting-return-home = Done

## Tracker manual proportions setupa

onboarding-manual_proportions-back-scaled = Go back to Scaled Proportions
onboarding-manual_proportions-title = Manuelle kropps-proporsjoner
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

onboarding-automatic_proportions-back = Gå tilbake til Nullstillings opplæring
onboarding-automatic_proportions-title = Mål opp kroppen din
onboarding-automatic_proportions-description = For at SlimeVR trackere skal funke, må vi vite lengden på beinene dine. Denne korte kalibreringen kan måle det opp for deg.
onboarding-automatic_proportions-manual = Manuell kalibrering
onboarding-automatic_proportions-prev_step = Forrige steg
onboarding-automatic_proportions-put_trackers_on-title = Ta på deg alle dine trackere
onboarding-automatic_proportions-put_trackers_on-description = For å kalibrere dine proporsjoner, bruker vi trackerne du akkurat har tildelt. Ta på deg alle dine trackere, du kan se hvem som går hvor i figuren til høyre.
onboarding-automatic_proportions-put_trackers_on-next = Jeg har alle mine trackere på
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
onboarding-automatic_proportions-check_height-guardian_tip =
    If you are using a standalone VR headset, make sure to have your guardian /
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
onboarding-automatic_proportions-start_recording-title = Gjør deg klar til å bevege deg
onboarding-automatic_proportions-start_recording-description = Vi kommer nå til å spille inn noen spesifikke poseringer og bevegelser. Disse vil bli vist i den neste skjermen. Gjør deg klar til å starte når du trykker på knappen!
onboarding-automatic_proportions-start_recording-next = Start Innspilling
onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = Innspilling pågår...
onboarding-automatic_proportions-recording-description-p1 = Utfør bevegelsene vist nedenfor:
# Each line of text is a different list item
onboarding-automatic_proportions-recording-steps =
    Standing up straight, roll your head in a circle.
    Bend your back forward and squat. While squatting, look to your left, then to your right.
    Twist your upper body to the left (counter-clockwise), then reach down toward the ground.
    Twist your upper body to the right (clockwise), then reach down toward the ground.
    Roll your hips in a circular motion as if you're using a hula hoop.
    If there is time left on the recording, you can repeat these steps until it's finished.
onboarding-automatic_proportions-recording-processing = Jobber med resultatet
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 20)
onboarding-automatic_proportions-recording-timer =
    { $time ->
        [one] 1 sekund igjen
       *[other] { $time } sekunder igjen
    }
onboarding-automatic_proportions-verify_results-title = Bekreft resultatene
onboarding-automatic_proportions-verify_results-description = Sjekk resultatene under, ser de riktige ut?
onboarding-automatic_proportions-verify_results-results = Spiller inn resultater
onboarding-automatic_proportions-verify_results-processing = Jobber med resultatet
onboarding-automatic_proportions-verify_results-redo = Gjør om innspillingen
onboarding-automatic_proportions-verify_results-confirm = De er riktige
onboarding-automatic_proportions-done-title = Kropp målt og lagret.
onboarding-automatic_proportions-done-description = Din kropps-proposisjons kalibrering er fullført!
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
onboarding-user_height-description = We need your height to calculate your body proportions and accurately represent your movements. You can either let SlimeVR calculate it, or input your height manually.
onboarding-user_height-need_head_tracker = A headset and controllers with positional tracking are required to perform the calibration.
onboarding-user_height-calculate = Calculate my height automatically
onboarding-user_height-next_step = Continue and save
onboarding-user_height-prev_step = Back
onboarding-user_height-manual-proportions = Manual Proportions
onboarding-user_height-calibration-title = Calibration Progress
onboarding-user_height-calibration-RECORDING_FLOOR = Touch the floor with the tip of your controller
onboarding-user_height-calibration-WAITING_FOR_RISE = Stand back up
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK = Stand back up and look forward
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-ok = Make sure your head is leveled
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
onboarding-user_height-reset-warning =
    <b>Warning:</b> This will reset your proportions to be based on your height.
    Are you sure you want to do this?

## Stay Aligned setup

onboarding-stay_aligned-title = Stay Aligned
onboarding-stay_aligned-description = Configure Stay Aligned to keep your trackers aligned.
onboarding-stay_aligned-put_trackers_on-title = Put on your trackers
onboarding-stay_aligned-put_trackers_on-description = To save your resting poses, we'll use the trackers you just assigned. Put on all your trackers, you can see which are which in the figure to the right.
onboarding-stay_aligned-put_trackers_on-trackers_warning = You have fewer than 5 trackers currently connected and assigned! This is the minimum amount of trackers required for Stay Aligned to function properly.
onboarding-stay_aligned-put_trackers_on-next = I have all my trackers on
onboarding-stay_aligned-verify_mounting-title = Mounting Calibration
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
onboarding-stay_aligned-manual_mounting-done = Done

## Home

home-no_trackers = Ingen trackere oppdaget eller tildelt
home-settings = Home Page Settings
home-settings-close = Close

## Trackers Still On notification

trackers_still_on-modal-title = Trackers still on
trackers_still_on-modal-description =
    One or more trackers are still on.
    Do you still want to exit SlimeVR?
trackers_still_on-modal-confirm = Exit SlimeVR
trackers_still_on-modal-cancel = Hold on...

## Status system

status_system-StatusTrackerReset = It is recommended to perform a full reset as one or more trackers are unadjusted.
status_system-StatusSteamVRDisconnected =
    { $type ->
       *[steamvr] Currently not connected to SteamVR via the SlimeVR driver.
        [steamvr_feeder] Currently not connected to the SlimeVR Feeder App.
    }
status_system-StatusTrackerError = The { $trackerName } tracker has an error.
status_system-StatusUnassignedHMD = The VR headset should be assigned as a head tracker.
status_system-StatusPublicNetwork =
    { $count ->
        [one] Your network profile is currently set to Public ({ $adapters }). This is not recommended for SlimeVR to function properly. <PublicFixLink>See how to fix it here.</PublicFixLink>
       *[many] Some of your network adapters are set to public: { $adapters }. This is not recommended for SlimeVR to function properly. <PublicFixLink>See how to fix it here.</PublicFixLink>
    }

## Firmware tool globals

firmware_tool-next_step = Next Step
firmware_tool-previous_step = Previous Step
firmware_tool-ok = Looks good
firmware_tool-retry = Retry
firmware_tool-loading = Loading...

## Firmware tool Steps

firmware_tool = DIY Firmware tool
firmware_tool-description = Allows you to configure and flash your DIY trackers
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
firmware_tool-select_source-not_selected = No source selected
firmware_tool-select_source-no_boards = No available boards for this source
firmware_tool-select_source-no_versions = No available versions for this source
firmware_tool-board_defaults = Configure your board
firmware_tool-board_defaults-description = Set the pins or settings relative to your hardware
firmware_tool-board_defaults-add = Add
firmware_tool-board_defaults-reset = Reset to Default
firmware_tool-board_defaults-error-required = Required field
firmware_tool-board_defaults-error-format = Invalid format
firmware_tool-board_defaults-error-format-number = Not a number
firmware_tool-flash_method_step = Flashing Method
firmware_tool-flash_method_step-description = Please select the flashing method you want to use
firmware_tool-flash_method_step-ota-v2 = 
    .label = Wi-Fi
    .description = Use the over-the-air method. Your tracker will use Wi-Fi to update its firmware. Only works on trackers that have been set up.
firmware_tool-flash_method_step-ota-info =
    We use your wifi credentials to flash the tracker and confirm that everything worked correctly.
    <b>We do not store your wifi credentials!</b>
firmware_tool-flash_method_step-serial-v2 = 
    .label = USB
    .description = Use a USB cable to update your tracker.
firmware_tool-flashbtn_step = Press the boot button
firmware_tool-flashbtn_step-description = Before going to the next step, there are a few things you need to do
firmware_tool-flashbtn_step-board_SLIMEVR = Turn off the tracker, remove the case (if any), connect the USB cable to your computer, then follow the appropriate steps for your SlimeVR board revision:
firmware_tool-flashbtn_step-board_SLIMEVR-r11-v2 = Turn on the tracker while shorting the second rectangular FLASH pad from the edge on the top side of the board to the metal shield of the microcontroller. The tracker LED should do a short blink.
firmware_tool-flashbtn_step-board_SLIMEVR-r12-v2 = Turn on the tracker while shorting the circular FLASH pad on the top side of the board to the metal shield of the microcontroller. The tracker LED should do a short blink.
firmware_tool-flashbtn_step-board_SLIMEVR-r14-v2 = Turn on the tracker while pushing in the FLASH button on the top side of the board. The tracker LED should do a short blink.
firmware_tool-flashbtn_step-board_OTHER =
    Before flashing, you will probably need to put the tracker into bootloader mode.
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
firmware_tool-flash_method_serial-no_devices = There are no compatible serial devices detected, make sure the tracker is plugged in
firmware_tool-build_step = Building
firmware_tool-build_step-description = The firmware is building, please wait
firmware_tool-flashing_step = Flashing
firmware_tool-flashing_step-description = Your trackers are flashing, please follow the instructions on the screen
firmware_tool-flashing_step-warning-v2 = Do not unplug or turn off the tracker during the upload process unless told to, it may make your board unusable
firmware_tool-flashing_step-flash_more = Flash more trackers
firmware_tool-flashing_step-exit = Exit
firmware_tool-flashing_step-onboarding_continue = Continue

## firmware tool build status

firmware_tool-build-QUEUED = Waiting to build....
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
firmware_update-changelog-title = Updating to { $version }
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
tray_or_exit_modal-description =
    Choose whether to exit the server or minimize it to the tray when clicking the close button.
    
    You can change this later in the interface settings!
tray_or_exit_modal-radio-exit = Exit on close
tray_or_exit_modal-radio-tray = Minimize to system tray
tray_or_exit_modal-submit = Save
tray_or_exit_modal-cancel = Avbryt

## Unknown device modal

unknown_device-modal-title = A new tracker was found!
unknown_device-modal-description =
    There is a new tracker with MAC address <b>{ $deviceId }</b>.
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
error_collection_modal-description_v2 =
    { settings-interface-behavior-error_tracking-description_v2 }
    
    You can change this setting later in the Behavior section of the settings page.
error_collection_modal-confirm = I agree
error_collection_modal-cancel = I don't want to

## Tracking checklist section

tracking_checklist = Tracking Checklist
tracking_checklist-settings = Tracking Checklist Settings
tracking_checklist-settings-close = Close
tracking_checklist-status-incomplete = You are not prepared to use SlimeVR!
tracking_checklist-status-partial =
    { $count ->
        [one] You have 1 warning!
       *[many] You have { $count } warnings!
    }
tracking_checklist-status-complete = You are prepared to use SlimeVR!
tracking_checklist-MOUNTING_CALIBRATION = Perform a mounting calibration
tracking_checklist-FEET_MOUNTING_CALIBRATION = Perform a feet mounting calibration
tracking_checklist-FULL_RESET = Perform a full reset
tracking_checklist-FULL_RESET-desc = Some trackers need a reset to be performed.
tracking_checklist-STEAMVR_DISCONNECTED = SteamVR disconnected
tracking_checklist-STEAMVR_DISCONNECTED-desc = SteamVR is not running. Are you using it for VR?
tracking_checklist-STEAMVR_DISCONNECTED-driver_blocked-desc = The driver has been blocked by SteamVR due to a previous SteamVR crash.
tracking_checklist-STEAMVR_DISCONNECTED-driver_disabled-desc = The driver is disabled in SteamVR settings.
tracking_checklist-STEAMVR_DISCONNECTED-driver_not_installed-desc = The driver is not installed.
tracking_checklist-STEAMVR_DISCONNECTED-open = Launch SteamVR
tracking_checklist-STEAMVR_DISCONNECTED-enable = Enable driver
tracking_checklist-STEAMVR_HANDS_ENABLED = Hand trackers toggled on
tracking_checklist-STEAMVR_HANDS_ENABLED-desc = You have enabled the SteamVR virtual hand trackers. This will cause button inputs to not work in SteamVR and in games.
tracking_checklist-STEAMVR_HANDS_ENABLED-go = Disable them
tracking_checklist-STANDABLE_INSTALLED = Standable is installed
tracking_checklist-STANDABLE_INSTALLED-desc =
    Standable frequently causes tracking issues when used alongside SlimeVR. Standable should be fully uninstalled in Steam to ensure no issues arise.
    You must close SteamVR before uninstalling Standable in Steam.
tracking_checklist-TRACKERS_REST_CALIBRATION = Calibrate your trackers
tracking_checklist-TRACKERS_REST_CALIBRATION-desc = You didn't perform tracker calibration. Please let your trackers (highlighted in yellow) rest on a stable surface for a few seconds.
tracking_checklist-TRACKER_ERROR = Trackers with Errors
tracking_checklist-TRACKER_ERROR-desc = Some of your trackers have an error. Please restart the trackers highlighted in yellow.
tracking_checklist-VRCHAT_SETTINGS = Configure VRChat settings
tracking_checklist-VRCHAT_SETTINGS-desc = You have misconfigured VRChat settings! This can negatively impact your tracking.
tracking_checklist-VRCHAT_SETTINGS-open = Go to VRChat Warnings
tracking_checklist-UNASSIGNED_HMD = VR headset not assigned to Head
tracking_checklist-UNASSIGNED_HMD-desc = The VR headset should be assigned as a head tracker.
tracking_checklist-NETWORK_PROFILE_PUBLIC = Change your network profile
tracking_checklist-NETWORK_PROFILE_PUBLIC-desc =
    { $count ->
        [one]
            Your network profile is currently set to Public ({ $adapters }).
            This is not recommended for SlimeVR to function properly.
            <PublicFixLink>See how to fix it here.</PublicFixLink>
       *[many]
            Some of your network adapters are set to public:
            { $adapters }
            This is not recommended for SlimeVR to function properly.
            <PublicFixLink>See how to fix it here.</PublicFixLink>
    }
tracking_checklist-NETWORK_PROFILE_PUBLIC-open = Open Control Panel
tracking_checklist-STAY_ALIGNED_CONFIGURED = Configure Stay Aligned
tracking_checklist-STAY_ALIGNED_CONFIGURED-desc = Record the Stay Aligned poses to reduce drift
tracking_checklist-STAY_ALIGNED_CONFIGURED-open = Open Stay Aligned Wizard
tracking_checklist-ignore = Ignore
preview-mocap_mode_soon = Mocap Mode (Soon™)
preview-disable_render = Disable rendering
preview-disabled_render = Rendering disabled
toolbar-mounting_calibration = Mounting Calibration
toolbar-mounting_calibration-default = Body
toolbar-mounting_calibration-feet = Feet
toolbar-mounting_calibration-fingers = Fingers
toolbar-drift_reset = Drift Reset
toolbar-assigned_trackers = { $count } trackers assigned
toolbar-unassigned_trackers = { $count } trackers unassigned
