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
version_update-description = Ved at klikke på "Opdater" downloades SlimeVR-installationsprogrammet for dig.
version_update-update = Opdater
version_update-close = Luk

## Tips

tips-find_tracker = Ikke sikker på, hvilken tracker er hvilken? Ryst trackeren, og den vil fremhæve det tilsvarende element.
tips-do_not_move_heels = Sørg for, at dine hæle ikke bevæger sig under optagelsen!
tips-file_select = Træk og slip filer for at bruge, eller <u>gennemse</u>.

## Units


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
body_part-CHEST = Bryst
body_part-WAIST = Talje
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
skeleton_bone-CHEST = Bryst Længde
skeleton_bone-WAIST = Taljelængde
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
reset-full = Fuld nulstilling
reset-mounting = Nulstil Montage
reset-yaw = Yaw Nulstil

## Serial detection stuff

serial_detection-new_device-p0 = Ny seriel enhed fundet!
serial_detection-new_device-p1 = Indtast dine Wi-Fi-legitimationsoplysninger!
serial_detection-new_device-p2 = Vælg venligst hvad du vil gøre med det
serial_detection-open_wifi = Opret forbindelse til Wi-Fi
serial_detection-open_serial = Åbn seriel konsol
serial_detection-submit = Indsend!
serial_detection-close = Tæt

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

## Widget: Clear Mounting calibration


## Widget: Developer settings

widget-developer_mode = Udviklertilstand
widget-developer_mode-high_contrast = Høj kontrast
widget-developer_mode-precise_rotation = Præcis rotation
widget-developer_mode-fast_data_feed = Hurtig datatilførsel
widget-developer_mode-sort_by_name = Sorter efter navn
widget-developer_mode-raw_slime_rotation = Rå rotation
widget-developer_mode-more_info = Mere info

## Widget: IMU Visualizer

widget-imu_visualizer = Rotation
widget-imu_visualizer-rotation_raw = Rå
widget-imu_visualizer-rotation_preview = Forhåndsvisning

## Widget: Skeleton Visualizer


## Tracker status

tracker-status-none = Ingen status
tracker-status-busy = Travl
tracker-status-error = Fejl
tracker-status-disconnected = Afbrudt
tracker-status-occluded = Okkluderet
tracker-status-ok = Okay

## Tracker status columns

tracker-table-column-name = Navn
tracker-table-column-type = Type
tracker-table-column-battery = Batteri
tracker-table-column-ping = Ping
tracker-table-column-tps = TPS
tracker-table-column-temperature = Temp. °C
tracker-table-column-linear-acceleration = Accel. X/Y/Z
tracker-table-column-rotation = Rotation X/Y/Z
tracker-table-column-position = Position X/Y/Z
tracker-table-column-url = URL

## Tracker rotation

tracker-rotation-front = Forrest
tracker-rotation-left = Venstre
tracker-rotation-right = Højre
tracker-rotation-back = Tilbage

## Tracker information

tracker-infos-manufacturer = Fabrikant
tracker-infos-display_name = Display navn
tracker-infos-custom_name = Brugerdefineret navn
tracker-infos-url = Tracker URL
tracker-infos-hardware_identifier = Hardware ID
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
tracker-settings-name_section-description = Giv den et sødt kælenavn :)
tracker-settings-name_section-placeholder = NightyBeast's venstre ben

## Tracker part card info

tracker-part_card-unassigned = Ikke tildelt

## Body assignment menu

body_assignment_menu = Hvor vil du have denne tracker til at være?
body_assignment_menu-description = Vælg en placering, hvor du ønsker, at denne tracker skal tildeles. Alternativt kan du vælge at administrere alle trackere på én gang i stedet for én efter én.
body_assignment_menu-manage_trackers = Administrer alle trackere
body_assignment_menu-unassign_tracker = Fjern tildeling af tracker

## Tracker assignment menu

# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Advarsel:</b> En halstracker kan være dødbringende, hvis den justeres for stramt,
    Remmen kunne fjerne blodcirkulationen til dit hoved!
tracker_selection_menu-neck_warning-done = Jeg forstår risiciene
tracker_selection_menu-neck_warning-cancel = Annuller

## Mounting menu

mounting_selection_menu = Hvor vil du have denne tracker til at være?
mounting_selection_menu-close = Luk

## Sidebar settings

settings-sidebar-title = Indstillinger
settings-sidebar-general = Generel
settings-sidebar-interface = Brugergrænseflade
settings-sidebar-osc_trackers = VRChat OSC trackere
settings-sidebar-utils = Hjælpeprogrammer
settings-sidebar-serial = Seriel konsol

## Tracker mechanics

settings-general-tracker_mechanics-filtering = Filtrering
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Vælg filtreringstypen for dine trackere.
    Forudsigelse forudsiger bevægelse, mens udjævning udjævner bevægelse.
settings-general-tracker_mechanics-filtering-type = Filtrerings type
settings-general-tracker_mechanics-filtering-type-none = Ingen filtrering
settings-general-tracker_mechanics-filtering-type-none-description = Brug rotationer, som de er. Vil ikke foretage nogen filtrering.
settings-general-tracker_mechanics-filtering-type-smoothing = Udjævning
settings-general-tracker_mechanics-filtering-type-smoothing-description = Udjævner bevægelser, men tilføjer en smule latens.
settings-general-tracker_mechanics-filtering-type-prediction = Forudsigelse
settings-general-tracker_mechanics-filtering-type-prediction-description = Reducerer latens og gør bevægelser hutigere, men kan forårsage rystelser.
settings-general-tracker_mechanics-filtering-amount = Mængde

## FK/Tracking settings

settings-general-fk_settings = Trackingsindstillinger
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
settings-general-interface-theme = Farvetema
settings-general-interface-lang = Vælg sprog
settings-general-interface-lang-description = Skift det standardsprog, du vil bruge.
settings-general-interface-lang-placeholder = Vælg det sprog, der skal bruges

## Notification settings

settings-general-interface-serial_detection = Seriel enhedsregistrering
settings-general-interface-serial_detection-description = Denne mulighed viser en pop-up, hver gang du tilslutter en ny seriel enhed, der kan være en tracker. Det hjælper med at forbedre opsætningsprocessen for en tracker.
settings-general-interface-serial_detection-label = Seriel enhedsregistrering
settings-general-interface-feedback_sound = Feedback lyd
settings-general-interface-feedback_sound-description = Denne indstilling afspiller en lyd, når du nulstiller
settings-general-interface-feedback_sound-label = Feedback lyd
settings-general-interface-feedback_sound-volume = Feedback lydstyrke

## Behavior settings


## Serial settings

settings-serial = Seriel konsol
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
settings-serial-auto_dropdown_item = Auto

## OSC VRChat settings

settings-osc-vrchat = VRChat OSC trackere
settings-osc-vrchat-enable = Aktiver
settings-osc-vrchat-enable-label = Aktiver
settings-osc-vrchat-network = Netværksporte
settings-osc-vrchat-network-port_in =
    .label = Port ind
    .placeholder = Port ind (standard: 9001)
settings-osc-vrchat-network-port_out =
    .label = Port ud
    .placeholder = Port ud (standard: 9000)
settings-osc-vrchat-network-address = Netværksadresse
settings-osc-vrchat-network-address-placeholder = VRChat ip-adresse

## VMC OSC settings

settings-osc-vmc = Virtual Motion Capture
# This cares about multilines
settings-osc-vmc-description =
    Skift indstillinger, der er specifikke for VMC-protokollen (Virtual Motion Capture)
        for at sende SlimeVRs data og modtage data fra andre apps.
settings-osc-vmc-enable = Aktiver
settings-osc-vmc-enable-description = Skift afsendelse og modtagelse af data.
settings-osc-vmc-enable-label = Aktiver
settings-osc-vmc-network = Netværksporte
settings-osc-vmc-network-description = Vælg portene til at lytte og sende data via VMC
settings-osc-vmc-network-port_in =
    .label = Port ind
    .placeholder = Port ind (standard: 39540)
settings-osc-vmc-network-port_out =
    .label = Port ud
    .placeholder = Port ud (standard: 39539)
settings-osc-vmc-network-address = Netværksadresse
settings-osc-vmc-network-address-description = Vælg hvilken adresse du vil sende data på via VMC
settings-osc-vmc-network-address-placeholder = IPV4-adresse
settings-osc-vmc-vrm = VRM-model
settings-osc-vmc-vrm-description = Indlæs en VRM-model for at tillade hovedanker og muliggøre en højere kompatibilitet med andre applikationer
settings-osc-vmc-vrm-file_select = Træk og slip en model, du vil bruge, eller <u>gennemse</u>

## Common OSC settings


## Advanced settings


## Home Screen


## Tracking Checlist


## Setup/onboarding menu

onboarding-skip = Spring opsætning over
onboarding-continue = Fortsæt
onboarding-previous_step = Forrige trin
onboarding-setup_warning =
    <b>Advarsel:</b> Den indledende opsætning er nødvendig for god tracking,
    det er nødvendigt, hvis det er første gang, du bruger SlimeVR.
onboarding-setup_warning-skip = Spring opsætning over
onboarding-setup_warning-cancel = Fortsæt konfigurationen

## Quiz


## Wi-Fi setup

onboarding-wifi_creds-submit = Færdig!
onboarding-wifi_creds-ssid =
    .label = Wi-Fi-navn
    .placeholder = Indtast Wi-Fi-navn
onboarding-wifi_creds-password =
    .label = Kodeord
    .placeholder = Indtast Wi-Fi-kodeord

## Install info


## Setup start

onboarding-home = Velkommen til SlimeVR
onboarding-home-start = Lad os komme i gang!

## Tracker connection setup

onboarding-connect_tracker-title = Tilslut trackere
onboarding-connect_tracker-issue-serial = Jeg har problemer med at oprette forbindelse!
onboarding-connect_tracker-usb = USB-tracker
onboarding-connect_tracker-connection_status-serial_init = Tilslutter seriel enhed
onboarding-connect_tracker-connection_status-provisioning = Sender Wi-Fi-oplysninger
onboarding-connect_tracker-connection_status-connecting = Forsøger at oprette forbindelse til Wi-Fi
onboarding-connect_tracker-connection_status-looking_for_server = Leder efter server
onboarding-connect_tracker-connection_status-connection_error = Kan ikke oprette forbindelse til Wi-Fi
onboarding-connect_tracker-connection_status-could_not_find_server = Kunne ikke finde serveren
onboarding-connect_tracker-connection_status-done = Tilsluttet serveren
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers =
    { $amount ->
        [0] Ingen trackere tilsluttet
        [one] En tracker tilsluttet
       *[other] { $amount } trackere tilsluttet
    }
onboarding-connect_tracker-next = Jeg har tilsluttet alle mine trackere

## Tracker calibration tutorial


## Tracker assignment tutorial


## Tracker assignment setup

onboarding-assign_trackers-title = Tildel trackere
onboarding-assign_trackers-description = Lad os vælge, hvilken tracker der skal hvorhen. Klik på et sted, hvor du vil placere en tracker
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned =
    { $trackers ->
        [one] { $assigned } af en tracker tildelt
       *[other] { $assigned } af { $trackers } trackere tildelt
    }

## Tracker assignment warnings


## Tracker mounting method choose

onboarding-choose_mounting = Hvilken monteringskalibreringsmetode vil du bruge?
onboarding-choose_mounting-auto_mounting = Automatisk montering
onboarding-choose_mounting-auto_mounting-description = Dette registrerer automatisk monteringsretningerne til alle dine trackere fra 2 stillinger
onboarding-choose_mounting-manual_mounting = Manuel montering
onboarding-choose_mounting-manual_mounting-description = Dette giver dig mulighed for manuelt at vælge monteringsretningen for hver tracker

## Tracker manual mounting setup

onboarding-manual_mounting = Manuel montering
onboarding-manual_mounting-description = Klik på hver tracker og vælg hvilken vej de er monteret
onboarding-manual_mounting-auto_mounting = Automatisk montering
onboarding-manual_mounting-next = Næste trin

## Tracker automatic mounting setup

onboarding-automatic_mounting-next = Næste trin
onboarding-automatic_mounting-prev_step = Forrige trin
onboarding-automatic_mounting-done-restart = Prøv igen
onboarding-automatic_mounting-mounting_reset-step-0 = 1. Sæt dig på hug i en "skiløb" -stilling med bøjede ben, din overkrop vippet fremad og dine arme bøjet.
onboarding-automatic_mounting-preparation-title = Forberedelse
onboarding-automatic_mounting-put_trackers_on-title = Tag dine trackere på
onboarding-automatic_mounting-put_trackers_on-description = For at kalibrere rotationer bruger vi de trackere, du lige har tildelt. Tag alle dine trackere på du kan se hvilke der er hvilke i figuren til højre.
onboarding-automatic_mounting-put_trackers_on-next = Jeg har alle mine trackere på

## Tracker manual proportions setupa


## Tracker automatic proportions setup

onboarding-automatic_proportions-requirements-next = Jeg har læst kravene
onboarding-automatic_proportions-start_recording-next = Start optagelse
onboarding-automatic_proportions-recording-description-p0 = Optagelse i gang...
onboarding-automatic_proportions-recording-description-p1 = Foretag de bevægelser, der er vist nedenfor:
onboarding-automatic_proportions-recording-processing = Behandler resultatet
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 20)
onboarding-automatic_proportions-recording-timer =
    { $time ->
        [one] { $time } sekund tilbage
       *[other] { $time } sekunder tilbage
    }
onboarding-automatic_proportions-verify_results-title = Bekræft resultater
onboarding-automatic_proportions-verify_results-description = Tjek resultaterne nedenfor, ser de korrekte ud?
onboarding-automatic_proportions-verify_results-processing = Behandler resultatet
onboarding-automatic_proportions-verify_results-redo = prøv igen
onboarding-automatic_proportions-done-title = Krop målt og gemt.
onboarding-automatic_proportions-done-description = Kalibreringen af dine kropsproportioner er fuldført!

## User height calibration


## Stay Aligned setup

## Trackers Still On notification


## Status system


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

