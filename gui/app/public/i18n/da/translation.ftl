# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Tilslutter til serveren
websocket-connection_lost = Forbindelse mistet til serveren. Forsøger at oprette forbindelse igen...

## Update notification

version_update-title = Ny version tilgængelig: { $version }
version_update-update = Opdater
version_update-close = Tæt

## Tips

tips-find_tracker = Ikke sikker på, hvilken tracker er hvilken? Ryst trackeren, og den vil fremhæve det tilsvarende element.
tips-do_not_move_heels = Sørg for, at dine hæle ikke bevæger sig under optagelsen!
tips-file_select = Træk og slip filer for at bruge, eller <u>gennemse</u>.

## Units


## Dropdown


## Text input


## File input


## Window controls

titlebar-close = Tæt

## Body parts

body_part-NONE = Ikke tildelt
body_part-HEAD = Hoved
body_part-NECK = Nakke
body_part-RIGHT_SHOULDER = Højre skulder
body_part-RIGHT_UPPER_ARM = Højre overarm
body_part-RIGHT_LOWER_ARM = Højre underarm
body_part-RIGHT_HAND = Højre hånd
body_part-RIGHT_UPPER_LEG = Højre lår
body_part-RIGHT_LOWER_LEG = Højre ankel
body_part-RIGHT_FOOT = Højre fod
body_part-HIP = Hofte
body_part-LEFT_SHOULDER = Venstre skulder
body_part-LEFT_UPPER_ARM = Venstre overarm
body_part-LEFT_LOWER_ARM = Venstre underarm
body_part-LEFT_HAND = Venstre hånd
body_part-LEFT_UPPER_LEG = Venstre lår
body_part-LEFT_LOWER_LEG = Venstre ankel
body_part-LEFT_FOOT = Venstre fod

## BoardType


## Proportions

skeleton_bone-NONE = Ingen
skeleton_bone-HEAD = Hoved skift
skeleton_bone-NECK = Hals længde
skeleton_bone-torso_group = Torso Længde
skeleton_bone-HIP = Hoftelængde
skeleton_bone-HIPS_WIDTH = Hoftebredde
skeleton_bone-leg_group = Benlængde
skeleton_bone-UPPER_LEG = Øvre benlængde
skeleton_bone-LOWER_LEG = Underbenslængde
skeleton_bone-FOOT_LENGTH = Fodlængde
skeleton_bone-FOOT_SHIFT = Fodskift
skeleton_bone-SHOULDERS_DISTANCE = Skulder Afstand
skeleton_bone-SHOULDERS_WIDTH = Skulder Bredde
skeleton_bone-arm_group = Armlængde
skeleton_bone-UPPER_ARM = Overarmslængde
skeleton_bone-LOWER_ARM = Nedre armlængde
skeleton_bone-HAND_Y = Håndafstand Y
skeleton_bone-HAND_Z = Håndafstand Z

## Tracker reset buttons

reset-reset_all = Nulstil alle proportioner
reset-reset_all_warning-cancel = Annuller
reset-full = Fuld nulstilling
reset-mounting = Montage Kalibrering
reset-yaw = Yaw Nulstil

## Navigation bar

navbar-home = Hjem
navbar-body_proportions = Kropsforhold
navbar-trackers_assign = Tracker opgave
navbar-mounting = Montage Kalibrering
navbar-onboarding = Opsætningsguide
navbar-settings = Indstillinger

## Biovision hierarchy recording

bvh-start_recording = Optag BVH
bvh-recording = Optager...

## Tracking pause


## Widget: Developer settings

widget-developer_mode = Udvikler-tilstand
widget-developer_mode-high_contrast = Høj kontrast
widget-developer_mode-precise_rotation = Præcis rotation
widget-developer_mode-fast_data_feed = Hurtig datatilførsel
widget-developer_mode-raw_slime_rotation = Rå

## Widget: IMU Visualizer

widget-imu_visualizer = Rotation
widget-imu_visualizer-rotation_raw = Rå
widget-imu_visualizer-rotation_preview = Forhåndsvisning

## Tracker status

tracker-status-none = Ingen status
tracker-status-busy = Travl
tracker-status-error = Fejl
tracker-status-disconnected = Afbrudt
tracker-status-occluded = Okkluderet
tracker-status-ok = Okay

## Tracker status columns

tracker-table-column-name = Navn
tracker-table-column-battery = Batteri

## Tracker rotation

tracker-rotation-front = Forrest
tracker-rotation-left = Venstre
tracker-rotation-right = Højre
tracker-rotation-back = Tilbage

## Tracker information

tracker-infos-manufacturer = Fabrikant
tracker-infos-display_name = Display navn
tracker-infos-custom_name = Brugerdefineret navn
tracker-infos-imu = IMU-sensor

## Tracker settings

tracker-settings-back = Gå tilbage til trackerlisten
tracker-settings-title = Tracker indstillinger
tracker-settings-assignment_section = Opgave
tracker-settings-assignment_section-description = Hvilken del af kroppen trackeren er tildelt.
tracker-settings-assignment_section-edit = Rediger opgave
tracker-settings-mounting_section = Monteringsposition
tracker-settings-mounting_section-description = Hvor er trackeren monteret?
tracker-settings-mounting_section-edit = Rediger montering
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Tracker navn
tracker-settings-name_section-placeholder = NightyBeast's venstre ben
tracker-settings-name_section-label = Tracker navn

## Dongle settings

dongle-status-disconnected = Afbrudt
dongle-settings-back = Gå tilbage til trackerlisten

## Tracker part card info

tracker-part_card-unassigned = Ikke tildelt

## Body assignment menu

body_assignment_menu = Hvor vil du have denne tracker til at være?
body_assignment_menu-description = Vælg en placering, hvor du ønsker, at denne tracker skal tildeles. Alternativt kan du vælge at administrere alle trackere på én gang i stedet for én efter én.
body_assignment_menu-manage_trackers = Administrer alle trackere
body_assignment_menu-unassign_tracker = Fjern tildeling af tracker

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Advarsel:</b> En halstracker kan være dødbringende, hvis den justeres for stramt,
    Remmen kunne fjerne blodcirkulationen til dit hoved!
tracker_selection_menu-neck_warning-done = Jeg forstår risiciene
tracker_selection_menu-neck_warning-cancel = Annuller

## Mounting menu

mounting_selection_menu-close = Tæt

## Sidebar settings

settings-sidebar-title = Indstillinger
settings-sidebar-general = Generel
settings-sidebar-trackers = Trackere
settings-sidebar-interface = Brugergrænseflade
settings-sidebar-utils = Hjælpeprogrammer

## Bone routing settings

settings-routing-hands-warning-cancel = Annuller

## SteamVR / Monado output settings

settings-driver-enable = Aktiver

## Tracker mechanics

settings-general-tracker_mechanics-filtering = Filtrering
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Vælg filtreringstypen for dine trackere.
    Forudsigelse forudsiger bevægelse, mens udjævning udjævner bevægelse.
settings-general-tracker_mechanics-filtering-type-none = Ingen filtrering
settings-general-tracker_mechanics-filtering-type-none-description = Brug rotationer, som de er. Vil ikke foretage nogen filtrering.
settings-general-tracker_mechanics-filtering-type-smoothing = Udjævning
settings-general-tracker_mechanics-filtering-type-smoothing-description = Udjævner bevægelser, men tilføjer en smule latens.
settings-general-tracker_mechanics-filtering-type-prediction = Forudsigelse
settings-general-tracker_mechanics-filtering-type-prediction-description = Reducerer latens og gør bevægelser hutigere, men kan forårsage rystelser.
settings-stay_aligned-general-label = Generel
settings-stay_aligned-relaxed_poses-close = Tæt

## Keybinds Page

settings-keybinds_full-reset = Fuld nulstilling
settings-keybinds_yaw-reset = Yaw Nulstil
settings-keybinds-recorder-modal-cancel-button = Annuller

## FK/Tracking settings

# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Skate korrektion
settings-general-fk_settings-leg_tweak-toe_snap = Tå snap
settings-general-fk_settings-leg_tweak-skating_correction-amount = Skate korrektionsstyrke
settings-general-fk_settings-leg_tweak-skating_correction-description = Skate-korrektion korrigerer for skate, men kan reducere nøjagtigheden i visse bevægelsesmønstre. Når du aktiverer dette, skal du sørge for at nulstille og kalibrere i spillet.
settings-general-fk_settings-leg_tweak-floor_clip-description = Floor-clip kan reducere eller endda eliminere klipning gennem gulvet. Når du aktiverer dette, skal du sørge for at nulstille og kalibrere i spillet.
settings-general-fk_settings-leg_tweak-toe_snap-description = Tå-snap forsøger at gætte rotationen af dine fødder, hvis fodtrackere ikke er i brug.
settings-general-fk_settings-leg_tweak-foot_plant-description = Foot-plant roterer dine fødder så de er parallelle med jorden, når de er i kontakt med jorden.
settings-general-fk_settings-leg_fk = Bensporing
settings-general-fk_settings-arm_fk-back = Tilbage

## Gesture control settings (tracker tapping)

settings-general-gesture_control-subtitle = Tryk baseret nulstilling
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
        [one] tryk
       *[other] tryk
    }
settings-general-gesture_control-yawResetEnabled = Aktivér tryk for at yaw resette

## Appearance settings

settings-general-interface-dev_mode = Udvikler-tilstand
settings-general-interface-dev_mode-description = Denne tilstand kan være nyttig, hvis du har brug for dybdegående data eller for at interagere med tilsluttede trackere på et mere avanceret niveau.
settings-general-interface-dev_mode-label = Udvikler-tilstand
settings-general-interface-lang = Vælg sprog
settings-general-interface-lang-description = Skift det standardsprog, du vil bruge.
settings-general-interface-lang-placeholder = Vælg det sprog, der skal bruges

## Notification settings

settings-general-interface-feedback_sound = Feedback lyd
settings-general-interface-feedback_sound-description = Denne indstilling afspiller en lyd, når du nulstiller
settings-general-interface-feedback_sound-label = Feedback lyd
settings-general-interface-feedback_sound-volume = Feedback lydstyrke

## Behavior settings

settings-general-interface-dev_mode = Udvikler-tilstand
settings-general-interface-dev_mode-label = Udvikler-tilstand

## Serial settings

settings-serial-connection_lost = Forbindelse til seriel mistet, Genopretter forbindelse...
settings-serial-reboot = Genstart
settings-serial-factory_reset = Nulstil til fabriksindstillinger
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>Advarsel:</b> Dette nulstiller trackeren til fabriksindstillingerne.
    Hvilket betyder, at alle Wi-Fi- og kalibreringsindstillinger <b>går tabt!</b>
settings-serial-factory_reset-warning-ok = Jeg ved hvad jeg laver
settings-serial-factory_reset-warning-cancel = Annuller
settings-serial-serial_select = Vælg en seriel port
settings-serial-send_command-warning-ok = Jeg ved hvad jeg laver
settings-serial-send_command-warning-cancel = Annuller

## OSC VRChat settings

settings-osc-vrchat-enable = Aktiver

## VRChat OSC status


## VMC OSC settings


## Common OSC settings


## Advanced settings


## Home Screen


## Tracking Checklist


## Setup/onboarding menu


## Quiz


## Wi-Fi setup


## Install info


## Setup start


## Tracker connection setup


## Tracker assignment setup


## Tracker assignment warnings


## Tracker mounting method choose


## Tracker manual mounting setup


## Tracker automatic mounting setup


## Tracker manual proportions setupa


## Tracker automatic proportions setup


## User height calibration


## Stay Aligned setup


## Home


## Trackers Still On notification


## Firmware tool globals


## Firmware tool Steps


## firmware tool build status


## Firmware update status


## Dedicated Firmware Update Page


## Tray Menu


## First exit modal


## Unknown device modal


## Error collection consent modal


## Tracking checklist section

