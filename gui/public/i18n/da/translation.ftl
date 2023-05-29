### SlimeVR complete GUI translations


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
tips-tap_setup = Du kan trykke langsomt 2 gange på din tracker for at vælge den i stedet for at vælge den i menuen.

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

## Proportions

skeleton_bone-NONE = Ingen
skeleton_bone-HEAD = Hoved skift
skeleton_bone-NECK = Hals længde
skeleton_bone-torso_group = Torso Længde
skeleton_bone-CHEST = Bryst Længde
skeleton_bone-CHEST_OFFSET = Bryst Juster
skeleton_bone-WAIST = Taljelængde
skeleton_bone-HIP = Hoftelængde
skeleton_bone-HIP_OFFSET = Hofte Juster
skeleton_bone-HIPS_WIDTH = Hoftebredde
skeleton_bone-leg_group = Benlængde
skeleton_bone-UPPER_LEG = Øvre benlængde
skeleton_bone-LOWER_LEG = Underbenslængde
skeleton_bone-FOOT_LENGTH = Fodlængde
skeleton_bone-FOOT_SHIFT = Fodskift
skeleton_bone-SKELETON_OFFSET = Skelet Juster
skeleton_bone-SHOULDERS_DISTANCE = Skulder Afstand
skeleton_bone-SHOULDERS_WIDTH = Skulder Bredde
skeleton_bone-arm_group = Armlængde
skeleton_bone-UPPER_ARM = Overarmslængde
skeleton_bone-LOWER_ARM = Nedre armlængde
skeleton_bone-HAND_Y = Håndafstand Y
skeleton_bone-HAND_Z = Håndafstand Z
skeleton_bone-ELBOW_OFFSET = Albuer Juster

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

## Bounding volume hierarchy recording

bvh-start_recording = Optag BVH
bvh-recording = Optager...

## Widget: Overlay settings

widget-overlay = Overlejring
widget-overlay-is_visible_label = Vis Overlejring i SteamVR
widget-overlay-is_mirrored_label = Vis Overlejring som Spejl

## Widget: Drift compensation

widget-drift_compensation-clear = Klar afdriftskompensation

## Widget: Developer settings

widget-developer_mode = Udviklertilstand
widget-developer_mode-high_contrast = Høj kontrast
widget-developer_mode-precise_rotation = Præcis rotation
widget-developer_mode-fast_data_feed = Hurtig datatilførsel
widget-developer_mode-filter_slimes_and_hmd = Filter slimes og HMD
widget-developer_mode-sort_by_name = Sorter efter navn
widget-developer_mode-raw_slime_rotation = Rå rotation
widget-developer_mode-more_info = Mere info

## Widget: IMU Visualizer

widget-imu_visualizer = Rotation
widget-imu_visualizer-rotation_raw = Rå
widget-imu_visualizer-rotation_preview = Forhåndsvisning
widget-imu_visualizer-rotation_hide = Skjul

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
tracker-infos-version = Firmware Version
tracker-infos-hardware_rev = Hardware Revision
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
tracker-settings-drift_compensation_section = Tillad afdriftskompensation
tracker-settings-drift_compensation_section-description = Skal denne tracker kompensere for dens drift, når driftkompensation er aktiveret?
tracker-settings-drift_compensation_section-edit = Tillad afdriftskompensation
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Tracker navn
tracker-settings-name_section-description = Giv den et sødt kælenavn :)
tracker-settings-name_section-placeholder = NightyBeast's venstre ben

## Tracker part card info

tracker-part_card-no_name = Intet navn
tracker-part_card-unassigned = Ikke tildelt

## Body assignment menu

body_assignment_menu = Hvor vil du have denne tracker til at være?
body_assignment_menu-description = Vælg en placering, hvor du ønsker, at denne tracker skal tildeles. Alternativt kan du vælge at administrere alle trackere på én gang i stedet for én efter én.
body_assignment_menu-show_advanced_locations = Vis avancerede placeringer
body_assignment_menu-manage_trackers = Administrer alle trackere
body_assignment_menu-unassign_tracker = Fjern tildeling af tracker

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = Hvilken tracker skal tildeles til din
tracker_selection_menu-NONE = Hvilken tracker vil du fjerne tildelingen af?
tracker_selection_menu-HEAD = { -tracker_selection-part } hoved?
tracker_selection_menu-NECK = { -tracker_selection-part } hals?
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part } højre skulder?
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part } højre overarm?
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part } højre underarm?
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part } højre hånd?
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part } højre lår?
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part } højre ankel?
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part } højre fod?
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part } højre controller?
tracker_selection_menu-CHEST = { -tracker_selection-part } brystet?
tracker_selection_menu-WAIST = { -tracker_selection-part } talje?
tracker_selection_menu-HIP = { -tracker_selection-part } hofte?
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part } venstre skulder?
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part } venstre overarm?
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part } venstre underarm?
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part } venstre hånd?
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part } venstre lår?
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part } venstre ankel?
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part } venstre fod?
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part } venstre controller?
tracker_selection_menu-unassigned = Ikke-tildelte trackere
tracker_selection_menu-assigned = Tildelte trackere
tracker_selection_menu-dont_assign = Tildel ikke
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
settings-sidebar-tracker_mechanics = Tracker mekanik
settings-sidebar-fk_settings = Tracking indstillinger
settings-sidebar-interface = Brugergrænseflade
settings-sidebar-osc_router = OSC-router
settings-sidebar-osc_trackers = VRChat OSC trackere
settings-sidebar-utils = Hjælpeprogrammer
settings-sidebar-serial = Seriel konsol

## SteamVR settings

settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = SteamVR-trackere
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    Aktivér eller deaktiver specifikke SteamVR-trackere.
    Nyttig til spil eller apps, der kun understøtter bestemte trackere.
settings-general-steamvr-trackers-waist = Talje
settings-general-steamvr-trackers-chest = Bryst
settings-general-steamvr-trackers-feet = Fødder
settings-general-steamvr-trackers-knees = Knæ
settings-general-steamvr-trackers-elbows = Albuer
settings-general-steamvr-trackers-hands = Hænder

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
settings-general-tracker_mechanics-drift_compensation = Drift kompensation
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Kompenserer IMU yaw drift ved at anvende en omvendt rotation.
    Skift kompensationsbeløb og hvor mange nulstillinger der skal tages i betragtning.
settings-general-tracker_mechanics-drift_compensation-enabled-label = Drift kompensation
settings-general-tracker_mechanics-drift_compensation-amount-label = Kompensationsmængde
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Brug op til x seneste nulstillinger

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
settings-general-fk_settings-arm_fk = Arm sporing
settings-general-fk_settings-arm_fk-description = Tving arme til spore fra HMD, selvom positionshånddata er tilgængelige.
settings-general-fk_settings-arm_fk-force_arms = Tving arme fra HMD
settings-general-fk_settings-skeleton_settings = Indstillinger for skelet
settings-general-fk_settings-skeleton_settings-description = Slå skeletindstillinger til eller fra. Det anbefales at lade disse være på.
settings-general-fk_settings-skeleton_settings-extended_spine = Udvidet rygsøjle
settings-general-fk_settings-skeleton_settings-extended_pelvis = Forlænget pelvis
settings-general-fk_settings-skeleton_settings-extended_knees = Forlænget knæ
settings-general-fk_settings-vive_emulation-title = Vive emulering
settings-general-fk_settings-vive_emulation-description = Emuler de taljetrackerproblemer, som Vive-trackere har. Dette er en joke og gør sporing værre.
settings-general-fk_settings-vive_emulation-label = Aktivér Vive-emulering

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

## Interface settings

settings-general-interface = Brugergrænseflade
settings-general-interface-dev_mode = Udvikler-tilstand
settings-general-interface-dev_mode-description = Denne tilstand kan være nyttig, hvis du har brug for dybdegående data eller for at interagere med tilsluttede trackere på et mere avanceret niveau.
settings-general-interface-dev_mode-label = Udvikler-tilstand
settings-general-interface-serial_detection = Seriel enhedsregistrering
settings-general-interface-serial_detection-description = Denne mulighed viser en pop-up, hver gang du tilslutter en ny seriel enhed, der kan være en tracker. Det hjælper med at forbedre opsætningsprocessen for en tracker.
settings-general-interface-serial_detection-label = Seriel enhedsregistrering
settings-general-interface-feedback_sound = Feedback lyd
settings-general-interface-feedback_sound-description = Denne indstilling afspiller en lyd, når du nulstiller
settings-general-interface-feedback_sound-label = Feedback lyd
settings-general-interface-feedback_sound-volume = Feedback lydstyrke
settings-general-interface-theme = Farvetema
settings-general-interface-lang = Vælg sprog
settings-general-interface-lang-description = Skift det standardsprog, du vil bruge.
settings-general-interface-lang-placeholder = Vælg det sprog, der skal bruges

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
settings-serial-get_infos = Hent oplysninger
settings-serial-serial_select = Vælg en seriel port
settings-serial-auto_dropdown_item = Auto

## OSC router settings

settings-osc-router = OSC-router
# This cares about multilines
settings-osc-router-description =
    Videresend OSC-meddelelser fra et andet program.
    Nyttig til brug af et andet OSC-program med VRChat, for eksempel.
settings-osc-router-enable = Aktiver
settings-osc-router-enable-label = Aktiver
settings-osc-router-network = Netværksporte
# This cares about multilines
settings-osc-router-network-description =
    Vælg de porte der skal bruges til at lytte og sende data.
    Disse kan være de samme som de andre porte der bruges på SlimeVR-serveren.
settings-osc-router-network-port_in =
    .label = Port ind
    .placeholder = Port ind (standard: 9002)
settings-osc-router-network-port_out =
    .label = Port ud
    .placeholder = Port ud (standard: 9000)
settings-osc-router-network-address = Netværksadresse
settings-osc-router-network-address-description = Indstil den adresse, der skal sendes data på.
settings-osc-router-network-address-placeholder = IPV4-adresse

## OSC VRChat settings

settings-osc-vrchat = VRChat OSC trackere
# This cares about multilines
settings-osc-vrchat-description =
    Skift VRChat-specifikke indstillinger for at modtage HMD-data og sende
    trackerdata til FBT uden SteamVR (f.eks. Quest standalone).
settings-osc-vrchat-enable = Aktiver
settings-osc-vrchat-enable-label = Aktiver
settings-osc-vrchat-network = Netværksporte
settings-osc-vrchat-network-description = Indstil portene til at lytte og sende data til VRChat.
settings-osc-vrchat-network-port_in =
    .label = Port ind
    .placeholder = Port ind (standard: 9001)
settings-osc-vrchat-network-port_out =
    .label = Port ud
    .placeholder = Port ud (standard: 9000)
settings-osc-vrchat-network-address = Netværksadresse
settings-osc-vrchat-network-address-description = Vælg hvilken adresse der skal sende data til VRChat (tjek dine Wi-Fi-indstillinger på din enhed).
settings-osc-vrchat-network-address-placeholder = VRChat ip-adresse
settings-osc-vrchat-network-trackers = Trackere
settings-osc-vrchat-network-trackers-description = Skift afsendelse af specifikke trackere via OSC.
settings-osc-vrchat-network-trackers-chest = Bryst
settings-osc-vrchat-network-trackers-hip = Hofte
settings-osc-vrchat-network-trackers-knees = Knæ
settings-osc-vrchat-network-trackers-feet = Fødder
settings-osc-vrchat-network-trackers-elbows = Albuer

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
settings-osc-vmc-vrm-model_unloaded = Ingen model indlæst
settings-osc-vmc-vrm-file_select = Træk og slip en model, du vil bruge, eller <u>gennemse</u>

## Setup/onboarding menu

onboarding-skip = Spring opsætning over
onboarding-continue = Fortsæt
onboarding-previous_step = Forrige trin
onboarding-setup_warning =
    <b>Advarsel:</b> Den indledende opsætning er nødvendig for god tracking,
    det er nødvendigt, hvis det er første gang, du bruger SlimeVR.
onboarding-setup_warning-skip = Spring opsætning over
onboarding-setup_warning-cancel = Fortsæt konfigurationen

## Wi-Fi setup

onboarding-wifi_creds-back = Gå tilbage til introduktion
onboarding-wifi_creds = Indtast Wi-Fi-oplysninger
# This cares about multilines
onboarding-wifi_creds-description =
    Trackerne bruger disse oplysninger til at oprette forbindelse trådløst.
    Brug de oplysninger, du i øjeblikket har forbindelse til.
onboarding-wifi_creds-skip = Spring Wi-Fi-indstillinger over
onboarding-wifi_creds-submit = Færdig!
onboarding-wifi_creds-ssid =
    .label = Wi-Fi-navn
    .placeholder = Indtast Wi-Fi-navn
onboarding-wifi_creds-password =
    .label = Kodeord
    .placeholder = Indtast Wi-Fi-kodeord

## Mounting setup

onboarding-reset_tutorial-back = Gå tilbage til monteringskalibrering
onboarding-reset_tutorial = Start forfra
onboarding-reset_tutorial-description = Denne funktion er ikke færdig, bare tryk på fortsæt

## Setup start

onboarding-home = Velkommen til SlimeVR
onboarding-home-start = Lad os komme i gang!

## Enter VR part of setup

onboarding-enter_vr-back = Gå tilbage til Tracker-tildeler
onboarding-enter_vr-title = Tid til at gå ind i VR!
onboarding-enter_vr-description = Tag alle dine trackere på, og gå derefter på VR!
onboarding-enter_vr-ready = Jeg er klar

## Setup done

onboarding-done-title = Du er klar!
onboarding-done-description = Nyd din full-body oplevelse
onboarding-done-close = Luk opsætning

## Tracker connection setup

onboarding-connect_tracker-back = Gå tilbage til Wi-Fi-oplysninger
onboarding-connect_tracker-title = Tilslut trackere
onboarding-connect_tracker-description-p0 = Nu til den sjove del, forbind alle trackere!
onboarding-connect_tracker-description-p1 = Du skal blot tilslutte alle, der ikke er tilsluttet endnu, via en USB-port.
onboarding-connect_tracker-issue-serial = Jeg har problemer med at oprette forbindelse!
onboarding-connect_tracker-usb = USB-tracker
onboarding-connect_tracker-connection_status-none = Leder efter trackere
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


## Tracker assignment setup

onboarding-assign_trackers-back = Gå tilbage til Wi-Fi-oplysninger
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
onboarding-assign_trackers-advanced = Vis avancerede trackerplaceringer
onboarding-assign_trackers-next = Jeg har tildelt alle trackerene

## Tracker assignment warnings


## Tracker mounting method choose

onboarding-choose_mounting = Hvilken monteringskalibreringsmetode vil du bruge?
onboarding-choose_mounting-auto_mounting = Automatisk montering
# Italized text
onboarding-choose_mounting-auto_mounting-subtitle = Anbefalet
onboarding-choose_mounting-auto_mounting-description = Dette registrerer automatisk monteringsretningerne til alle dine trackere fra 2 stillinger
onboarding-choose_mounting-manual_mounting = Manuel montering
# Italized text
onboarding-choose_mounting-manual_mounting-subtitle = Hvis du ved hvad du laver
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

## Tracker proportions method choose

# Italized text
onboarding-choose_proportions-auto_proportions-subtitle = Anbefalet

## Tracker manual proportions setup


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

## Home

home-no_trackers = Ingen trackere registreret eller tildelt
