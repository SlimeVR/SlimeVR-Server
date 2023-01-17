### SlimeVR complete GUI translations
# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropiate
# features like variables and selectors in each appropiate case!
# And also comment the string if it's something not easy to translate so you help
# translators on what it means

## Websocket (server) status
websocket-connecting = Verbinden met de server
websocket-connection_lost = Verbinding met de server verbroken. Opniew verbinding maken...

## Tips
tips-find_tracker = Weet je niet welke tracker welke is? Schud een tracker en het corresponderende item zal worden gemarkeerd.
tips-do_not_move_heels = Zorg ervoor dat je hielen tijdens het opnemen niet bewegen!

## Body parts
body_part-NONE = Niet toegewezen
body_part-HEAD = Hoofd
body_part-NECK = Neck
body_part-RIGHT_SHOULDER = Rechterschouder
body_part-RIGHT_UPPER_ARM = Rechterbovenarm
body_part-RIGHT_LOWER_ARM = Rechteronderarm
body_part-RIGHT_HAND = Rechterhand
body_part-RIGHT_UPPER_LEG = Rechterdij
body_part-RIGHT_LOWER_LEG = Rechterenkel
body_part-RIGHT_FOOT = Rechtervoet
body_part-RIGHT_CONTROLLER = Rechtercontroller
body_part-CHEST = Borst
body_part-WAIST = Taille
body_part-HIP = Heup
body_part-LEFT_SHOULDER = Linkerschouder
body_part-LEFT_UPPER_ARM = Linkerbovenarm
body_part-LEFT_LOWER_ARM = Linkeronderarm
body_part-LEFT_HAND = Linkerhand
body_part-LEFT_UPPER_LEG = Linkerdij
body_part-LEFT_LOWER_LEG = Linkerenkel
body_part-LEFT_FOOT = Linkervoet
body_part-LEFT_CONTROLLER = Linkercontroller

## Proportions
skeleton_bone-NONE = Geen
skeleton_bone-HEAD = Hoofdverschuiving
skeleton_bone-NECK = Necklengte
skeleton_bone-CHEST = Borstafstand
skeleton_bone-CHEST_OFFSET = Borstoffset
skeleton_bone-WAIST = Tailleafstand
skeleton_bone-HIP = Heuplengte
skeleton_bone-HIP_OFFSET = Heupoffset
skeleton_bone-HIPS_WIDTH = Heupbreedte
skeleton_bone-UPPER_LEG = Bovenbeenlengte
skeleton_bone-LOWER_LEG = Onderbeenlengte
skeleton_bone-FOOT_LENGTH = Voetlengte
skeleton_bone-FOOT_SHIFT = Voetverschuiving
skeleton_bone-SKELETON_OFFSET = Skelettenoffset
skeleton_bone-SHOULDERS_DISTANCE = Schoudersafstand
skeleton_bone-SHOULDERS_WIDTH = Schouderbreedte
skeleton_bone-UPPER_ARM = Bovenarmlengte
skeleton_bone-LOWER_ARM = Onderarmlengte
skeleton_bone-CONTROLLER_Y = Controllerafstand Y
skeleton_bone-CONTROLLER_Z = Controllerafstand Z
skeleton_bone-ELBOW_OFFSET = Elleboogoffset

## Tracker reset buttons
reset-reset_all = Alle afmetingen resetten
reset-full = Resetten
reset-mounting = Bevestiging resetten
reset-quick = Snel resetten

## Serial detection stuff
serial_detection-new_device-p0 = Nieuw serieel apparaat gedetecteerd!
serial_detection-new_device-p1 = Voer je WiFi-inloggegevens in!
serial_detection-new_device-p2 = Selecteer wat je wil doen
serial_detection-open_wifi = Verbinden met WiFi
serial_detection-open_serial = Open serieel console
serial_detection-submit = Verzenden!
serial_detection-close = Sluiten

## Navigation bar
navbar-home = Startpagina
navbar-body_proportions = Lichaams- verhoudingen
navbar-trackers_assign = Tracker- toewijzing
navbar-mounting = Bevestigings- kalibratie
navbar-onboarding = Setupgids
navbar-settings = Instellingen

## Bounding volume hierarchy recording
bvh-start_recording = BVH opnemen
bvh-recording = Opname bezig...

## Widget: Overlay settings
widget-overlay = Overlay
widget-overlay-is_visible_label = Overlay in SteamVR weergeven
widget-overlay-is_mirrored_label = Overlay weergeven als spiegel

## Widget: Developer settings
widget-developer_mode = Developer Mode
widget-developer_mode-high_contrast = Hoog contrast
widget-developer_mode-precise_rotation = Precieze rotatie
widget-developer_mode-fast_data_feed = Snelle data feed
widget-developer_mode-filter_slimes_and_hmd = Filter slimes en HMD
widget-developer_mode-sort_by_name = Op naam sorteren
widget-developer_mode-raw_slime_rotation = Ruwe rotatie
widget-developer_mode-more_info = Meer informatie

## Widget: IMU Visualizer
widget-imu_visualizer = Rotatie
widget-imu_visualizer-rotation_raw = Rauw
widget-imu_visualizer-rotation_preview = Preview

## Tracker status
tracker-status-none = Geen status
tracker-status-busy = Bezig
tracker-status-error = Fout
tracker-status-disconnected = Verbinding verbroken
tracker-status-occluded = Verborgen
tracker-status-ok = OK

## Tracker status columns
tracker-table-column-name = Naam
tracker-table-column-type = Type
tracker-table-column-battery = Batterij
tracker-table-column-ping = Ping
tracker-table-column-tps = TPS
tracker-table-column-temperature = Temp. °C
tracker-table-column-linear-acceleration = Accel. X/Y/Z
tracker-table-column-rotation = Rotatie X/Y/Z
tracker-table-column-position = Positie X/Y/Z
tracker-table-column-url = URL

## Tracker rotation
tracker-rotation-front = Voorzijde
tracker-rotation-left = Links
tracker-rotation-right = Rechts
tracker-rotation-back = Achterzijde

## Tracker information
tracker-infos-manufacturer = Fabrikant
tracker-infos-display_name = Weergavenaam
tracker-infos-custom_name = Aangepaste naam
tracker-infos-url = Tracker URL

## Tracker settings
tracker-settings-back = Terug naar trackerslijst
tracker-settings-title = Trackersinstellingen
tracker-settings-assignment_section = Toewijzing
tracker-settings-assignment_section-description = Aan welk lichaamsdeel de tracker is toegewezen.
tracker-settings-assignment_section-edit = Toewijzing bewerken
tracker-settings-mounting_section = Bevestigingsorientatie
tracker-settings-mounting_section-description = Hoe is de tracker georiënteerd?
tracker-settings-mounting_section-edit = Bevestiging bewerken
tracker-settings-drift_compensation_section = Laat drift compensatie toe
tracker-settings-drift_compensation_section-description = Moet deze tracker compenseren voor drift wanneer drift compensatie is ingeschakeld?
tracker-settings-drift_compensation_section-edit = Laat drift compensatie toe
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Trackernaam
tracker-settings-name_section-placeholder = NightyBeast's linkerbeen
tracker-settings-name_section-description = Geef een schattige bijnaam :)

## Tracker part card info
tracker-part_card-no_name = Geen naam
tracker-part_card-unassigned = Niet toegewezen

## Body assignment menu
body_assignment_menu = Waar wil je deze tracker bevestigen?
body_assignment_menu-description = Kies een locatie waar je deze tracker wilt toewijzen. Alternatief kun je kiezen om alle trackers tegelijk te beheren in plaats van één voor één.
body_assignment_menu-show_advanced_locations = Geavanceerde bevestigingslocaties weergeven
body_assignment_menu-manage_trackers = Beheer alle trackers
body_assignment_menu-unassign_tracker = Tracker niet toewijzen

## Tracker assignment menu
# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = Welke tracker wil je toewijzen aan je
tracker_selection_menu-NONE = Van welke tracker will je de toewijzing ongedaan maken?
tracker_selection_menu-HEAD = { -tracker_selection-part } hoofd?
tracker_selection_menu-NECK = { -tracker_selection-part } nek?
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part } rechterschouder?
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part } rechterbovenarm?
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part } rechteronderarm?
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part } rechterhand?
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part } rechterdij?
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part } rechterenkel?
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part } rechtervoet?
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part } rechtercontroller?
tracker_selection_menu-CHEST = { -tracker_selection-part } borst?
tracker_selection_menu-WAIST = { -tracker_selection-part } taille?
tracker_selection_menu-HIP = { -tracker_selection-part } heup?
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part } linkerschouder?
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part } linkerbovenarm?
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part } linkeronderarm?
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part } linkerhand?
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part } linkerdij?
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part } linkerenkel?
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part } linkervoet?
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part } linkercontroller?

tracker_selection_menu-unassigned = Niet toegewezen trackers
tracker_selection_menu-assigned = Toegewezen trackers
tracker_selection_menu-dont_assign = Niet toewijzen

## Mounting menu
mounting_selection_menu = Waar wil je deze tracker hebben bevestigd?
mounting_selection_menu-close = Sluiten

## Sidebar settings
settings-sidebar-title = Instellingen
settings-sidebar-general = Algemeen
settings-sidebar-tracker_mechanics = Trackersinstellingen
settings-sidebar-fk_settings = FK-instellingen
settings-sidebar-gesture_control = Tikbediening
settings-sidebar-interface = Interface
settings-sidebar-osc_router = OSC-router
settings-sidebar-utils = Hulpmiddelen
settings-sidebar-serial = Serieel console

## SteamVR settings
settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = SteamVR trackers
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    Schakel specifieke SteamVR trackers in of uit.
    Handig voor games of apps die alleen bepaalde trackers ondersteunen.
settings-general-steamvr-trackers-waist = Taille
settings-general-steamvr-trackers-chest = Borst
settings-general-steamvr-trackers-feet = Voeten
settings-general-steamvr-trackers-knees = Knieën
settings-general-steamvr-trackers-elbows = Ellebogen
settings-general-steamvr-trackers-hands = Handen

## Tracker mechanics
settings-general-tracker_mechanics = Tracker aanpassingen
settings-general-tracker_mechanics-filtering = Filtering
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Kies het type filter voor uw trackers.
    Voorspelling voorspelt beweging terwijl smoothing bewegingen vloeiender maakt.
settings-general-tracker_mechanics-filtering-type = Filtering type
settings-general-tracker_mechanics-filtering-type-none = Geen filtering
settings-general-tracker_mechanics-filtering-type-none-description = Gebruik rotaties zoals ze zijn. Zal geen filtering uitvoeren.
settings-general-tracker_mechanics-filtering-type-smoothing = Smoothing
settings-general-tracker_mechanics-filtering-type-smoothing-description = Maakt bewegingen vloeiender, maar voegt enige latentie toe.
settings-general-tracker_mechanics-filtering-type-prediction = Voorspelling
settings-general-tracker_mechanics-filtering-type-prediction-description = Verlaagt latentie en maakt bewegingen snappier, maar kan jitter verhogen.
settings-general-tracker_mechanics-filtering-amount = Hoeveelheid
settings-general-tracker_mechanics-drift_compensation = Drift compensatie
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Compenseert voor IMU yaw drift door de toevoeging van een omgekeerde rotatie.
    Veranderd de sterkte van de compensatie en hoeveel resets worden gebruikt.
settings-general-tracker_mechanics-drift_compensation-enabled-label = Drift compensate
settings-general-tracker_mechanics-drift_compensation-amount-label = Compensatiesterkte
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Gebruik de laatste x resets

## FK/Tracking settings
settings-general-fk_settings = Tracking instellingen
settings-general-fk_settings-leg_tweak = Aanpassingen van tracking gedrag voor benen
settings-general-fk_settings-leg_tweak-description = Floor-clip verminderd de kans dat je voeten door de grond gaan, maar kan problemen veroorzaken als je op je knieën bent. Skating-correctie corrigeert ongewenst glijden van je voeten, maar kan de nauwkeurigheid in bepaalde bewegingspatronen verminderen.
# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Floor-clip
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Skating-correctie
settings-general-fk_settings-leg_tweak-skating_correction-amount = Skating-correctie sterkte
settings-general-fk_settings-arm_fk = Arm tracking
settings-general-fk_settings-arm_fk-description = Verander de manier waarop de armen worden getrackt.
settings-general-fk_settings-arm_fk-force_arms = Dwing armen vanuit HMD
settings-general-fk_settings-arm_fk-i_pose = Use i-pose instead of t-pose
settings-general-fk_settings-skeleton_settings = Skeleton instellingen
settings-general-fk_settings-skeleton_settings-description = Schakel skeleton instellingen in of uit. Het is aanbevolen om deze aan te laten.
settings-general-fk_settings-skeleton_settings-extended_spine = Uitgebreide rug
settings-general-fk_settings-skeleton_settings-extended_pelvis = Uitgebreide bekken
settings-general-fk_settings-skeleton_settings-extended_knees = Uitgebreide knieën
settings-general-fk_settings-vive_emulation-title = Vive-emulatie
settings-general-fk_settings-vive_emulation-description = Emuleer de problemen met de taille van Vive trackers. Dit is een mop en maakt tracking slechter.
settings-general-fk_settings-vive_emulation-label = Vive-emulatie inschakelen

## Gesture control settings (tracker tapping)
settings-general-gesture_control = Gesture control
settings-general-gesture_control-subtitle = Op tik gebaseerde resets
settings-general-gesture_control-description = Maakt het mogelijk om resets te activeren door op een tracker te tikken. De tracker het hoogst op je bovenlichaam wordt gebruikt voor Quick Reset, de tracker het hoogst op je linkerbeen voor Reset en de tracker het hoogst op je rechterbeen voor Mounting Reset. Het moet worden vermeld dat tikken binnen 0,6 seconden moeten gebeuren om geregistreerd te worden.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps = { $amount ->
    [one] 1 tik
    *[other] { $amount } tikken
}
settings-general-gesture_control-quickResetEnabled = Activeer tikken voor snelle reset
settings-general-gesture_control-quickResetDelay = Vertraging snelle reset
settings-general-gesture_control-quickResetTaps = Hoeveelheid tikken voor snelle reset
settings-general-gesture_control-resetEnabled = Activeer tikken voor reset
settings-general-gesture_control-resetDelay = Vertraging reset
settings-general-gesture_control-resetTaps = Hoeveelheid tikken voor reset
settings-general-gesture_control-mountingResetEnabled = Activeer tikken voor bevestigingskalibratie
settings-general-gesture_control-mountingResetDelay = Vertraging bevestigingskalibratie
settings-general-gesture_control-mountingResetTaps = Hoeveelheid tikken voor bevestigingskalibratie

## Interface settings
settings-general-interface = Interface
settings-general-interface-dev_mode = Ontwikkelaarsmodus
settings-general-interface-dev_mode-description = Deze modus kan nuttig zijn als u diepgaande gegevens nodig hebt of op een geavanceerd niveau wilt communiceren met aangesloten trackers.
settings-general-interface-dev_mode-label = Ontwikkelaarsmodus
settings-general-interface-serial_detection = Detectie van seriële apparaten
settings-general-interface-serial_detection-description = Met deze optie verschijnt er elke keer dat u een nieuw serieel apparaat aansluit dat mogelijk een tracker is, een pop-up. Dit helpt bij het verbeteren van het instelproces van een tracker.
settings-general-interface-serial_detection-label = Detectie van seriële apparaten
settings-general-interface-lang = Selecteer taal
settings-general-interface-lang-description = Verander de standaardtaal die u wilt gebruiken.
settings-general-interface-lang-placeholder = Selecteer de te gebruiken taal

## Serial settings
settings-serial = Seriele console
# This cares about multilines
settings-serial-description =
    Dit is een live-informatiefeed voor seriële communicatie.
    Kan nuttig zijn voor het debuggen van trackers.
settings-serial-connection_lost = Verbinding met seriële poort verloren, opnieuw verbinden...
settings-serial-reboot = Opnieuw opstarten
settings-serial-factory_reset = Fabrieksinstellingen herstellen
settings-serial-get_infos = Informatie ophalen
settings-serial-serial_select = Selecteer een seriële poort
settings-serial-auto_dropdown_item = Automatisch

## OSC router settings
settings-osc-router = OSC-router
# This cares about multilines
settings-osc-router-description =
    Stuur OSC-berichten door vanuit een ander programma.
    Nuttig om bijvoorbeeld een ander OSC-programma te gebruiken met VRChat.
settings-osc-router-enable = Inschakelen
settings-osc-router-enable-description = Schakel het doorsturen van berichten in of uit.
settings-osc-router-enable-label = Inschakelen
settings-osc-router-network = Netwerkpoorten
# This cares about multilines
settings-osc-router-network-description =
    Stel de poorten in voor het verzenden en ontvangen van gegevens.
    Dit kunnen dezelfde poorten zijn als andere poorten die worden gebruikt in de SlimeVR-server.
settings-osc-router-network-port_in =
    .label = Poort in
    .placeholder = Poort in (standaard: 9002)
settings-osc-router-network-port_out =
    .label = Poort uit
    .placeholder = Poort uit (standaard: 9000)
settings-osc-router-network-address = Netwerkadres
settings-osc-router-network-address-description = Stel het adres in waarnaar gegevens moeten worden verzonden.
settings-osc-router-network-address-placeholder = IPV4-adres

## OSC VRChat settings
settings-osc-vrchat = VRChat OSC Trackers
# This cares about multilines
settings-osc-vrchat-description =
    Wijzig VRChat-specifieke instellingen om HMD-data te ontvangen en te verzenden
    trackergegevens voor FBT (werkt op Quest standalone).
settings-osc-vrchat-enable = Inschakelen
settings-osc-vrchat-enable-description = Schakel het verzenden en ontvangen van gegevens in en uit.
settings-osc-vrchat-enable-label = Inschakelen
settings-osc-vrchat-network = Netwerkpoorten
settings-osc-vrchat-network-description = Stel de poorten in voor het zenden en ontvangen van OSC-gegevens naar VRChat.
settings-osc-vrchat-network-port_in =
    .label = Poort In
    .placeholder = Poort in (standaard: 9001)
settings-osc-vrchat-network-port_out =
    .label = Poort Out
    .placeholder = Poort uit (standaard: 9000)
settings-osc-vrchat-network-address = Netwerkadres
settings-osc-vrchat-network-address-description = Kies naar welk adres u gegevens naar VRChat wilt verzenden (controleer de wifi-instellingen op je apparaat).
settings-osc-vrchat-network-address-placeholder = VRChat IP-adres
settings-osc-vrchat-network-trackers = Trackers
settings-osc-vrchat-network-trackers-description = Schakel het verzenden van specifieke trackers via OSC in en uit.
settings-osc-vrchat-network-trackers-chest = Borst
settings-osc-vrchat-network-trackers-waist = Taille
settings-osc-vrchat-network-trackers-knees = Knieën
settings-osc-vrchat-network-trackers-feet = Voeten
settings-osc-vrchat-network-trackers-elbows = Ellebogen

## Setup/onboarding menu
onboarding-skip = Setupgids overslaan
onboarding-continue = Doorgaan
onboarding-wip = WIP

## WiFi setup
onboarding-wifi_creds-back = Ga terug naar de introductie
onboarding-wifi_creds = Voer de WiFi-inloggegevens in
# This cares about multilines
onboarding-wifi_creds-description =
    Deze gegevens worden gebruikt om de trackers draadloos te verbinden met de server.
    Gelieve de gegevens te gebruiken van het netwerk waarmee je momenteel bent verbonden.
onboarding-wifi_creds-skip = WiFi-instellingen overslaan
onboarding-wifi_creds-submit = Verzenden!
onboarding-wifi_creds-ssid =
    .label = WiFi naam
    .placeholder = Vul WiFi naam in
onboarding-wifi_creds-password =
    .label = Paswoord
    .placeholder = Vul paswoord in

## Mounting setup
onboarding-reset_tutorial-back = Ga terug naar de bevestigingskalibratie
onboarding-reset_tutorial = Reset tutorial
onboarding-reset_tutorial-description = Deze stap is nog niet afgewerkt, druk gewoon op doorgaan.

## Setup start
onboarding-home = Welkom bij SlimeVR
# This cares about multilines and it's centered!!
onboarding-home-description =
    Maakt full-body tracking beschikbaar
    voor iedereen!
onboarding-home-start = Laten we beginnen!

## Enter VR part of setup
onboarding-enter_vr-back = Ga terug naar de sectie voor toewijzing van trackers
onboarding-enter_vr-title = Tijd om VR in te gaan!
onboarding-enter_vr-description = Doe al je trackers aan en ga dan in VR!
onboarding-enter_vr-ready = Gereed!

## Setup done
onboarding-done-title = Je bent klaar!
onboarding-done-description = Geniet van je full-body ervaring
onboarding-done-close = Sluit de gids

## Tracker connection setup
onboarding-connect_tracker-back = Ga terug naar de instellingen voor WiFi-configuratie
onboarding-connect_tracker-title = Trackers verbinden
onboarding-connect_tracker-description-p0 = Nu het leuke gedeelte, verbind al je trackers!
onboarding-connect_tracker-description-p1 = Gebruik een USB-kabel om alle trackers te verbinden die nog niet verbonden zijn.
onboarding-connect_tracker-issue-serial = Ik heb problemen met verbinden!
onboarding-connect_tracker-usb = USB Tracker
onboarding-connect_tracker-connection_status-connecting = Wifi-inloggegevens verzenden
onboarding-connect_tracker-connection_status-connected = Verbonden via WiFi
onboarding-connect_tracker-connection_status-error = Kan geen verbinding maken via WiFi
onboarding-connect_tracker-connection_status-start_connecting = Zoeken naar trackers
onboarding-connect_tracker-connection_status-handshake = Verbonden met de server
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers = { $amount ->
    [0] Geen trackers
    [one] 1 tracker
    *[other] { $amount } trackers
} verbonden
onboarding-connect_tracker-next = Ik heb al mijn trackers verbonden

## Tracker assignment setup
onboarding-assign_trackers-back = Ga terug naar de instellingen voor WiFi-configuratie
onboarding-assign_trackers-title = Trackers toewijzen
onboarding-assign_trackers-description = Laten we de bevesteging van je trackers bepalen. Klik op de lichaamslocatie waar je een tracker wilt toewijzen.
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned = { $assigned } van { $trackers ->
    [one] 1 tracker
    *[other] { $trackers } trackers
} toegewezen
onboarding-assign_trackers-advanced = Geavanceerde toewijzingslocaties weergeven
onboarding-assign_trackers-next = Ik heb alle trackers toegewezen

## Tracker manual mounting setup
onboarding-manual_mounting-back = Ga terug naar de VR sectie
onboarding-manual_mounting = Handmatige bevestiging
onboarding-manual_mounting-description = Klik op elke tracker en selecteer op welke manier ze zijn bevestigd
onboarding-manual_mounting-auto_mounting = Automatische bevestiging
onboarding-manual_mounting-next = Volgende stap

## Tracker automatic mounting setup
onboarding-automatic_mounting-back = Ga terug naar de VR sectie
onboarding-automatic_mounting-title = Bevestigingskalibratie
onboarding-automatic_mounting-description = Om je trackers te laten werken, moet de rotatie worden ingesteld hoe deze zijn bevestigd op je lichaam.
onboarding-automatic_mounting-manual_mounting = Bevestiging handmatig instellen
onboarding-automatic_mounting-next = Volgende stap
onboarding-automatic_mounting-prev_step = Vorige stap
onboarding-automatic_mounting-done-title = Bevestigingsrotaties gekalibreerd.
onboarding-automatic_mounting-done-description = Uw bevestigingskalibratie is compleet!
onboarding-automatic_mounting-done-restart = Terug naar start
onboarding-automatic_mounting-mounting_reset-title = Bevestiging kalibreren
onboarding-automatic_mounting-mounting_reset-step-0 = 1. Ga staan in een "skie"-houding met gebogen benen, uw bovenlichaam naar voren gekanteld en armen gebogen.
onboarding-automatic_mounting-mounting_reset-step-1 = 2. Druk op de knop "Bevestiging resetten" en wacht 3 seconden voordat de bevestigingsrotaties van de trackers opnieuw worden ingesteld.
onboarding-automatic_mounting-preparation-title = Voorbereiding
onboarding-automatic_mounting-preparation-step-0 = 1. Sta rechtop met uw armen langs uw zij.
onboarding-automatic_mounting-preparation-step-1 = 2. Druk op de knop "Resetten" en wacht 3 seconden voordat de trackers opnieuw worden ingesteld.
onboarding-automatic_mounting-put_trackers_on-title = Doe je trackers aan
onboarding-automatic_mounting-put_trackers_on-description = Om bevestigingsrotaties te kalibreren, gaan we gebruik maken van de trackers die je net hebt toegewezen. Doe al je trackers aan, je kunt zien welke trackers welke zijn in de figuur rechts.
onboarding-automatic_mounting-put_trackers_on-next = Ik heb al mijn trackers aan

## Tracker manual proportions setup
onboarding-manual_proportions-back = Ga terug naar de reset tutorial
onboarding-manual_proportions-title = Handmatige lichaamsverhoudingen
onboarding-manual_proportions-precision = Precisie-aanpassing
onboarding-manual_proportions-auto = Automatische kalibratie

## Tracker automatic proportions setup
onboarding-automatic_proportions-back = Ga terug naar de reset tutorial
onboarding-automatic_proportions-title = Meet je lichaam
onboarding-automatic_proportions-description = Om SlimeVR-trackers te laten werken, moeten we de lengte van je botten weten. Deze korte kalibratie meet het voor je.
onboarding-automatic_proportions-manual = Handmatige kalibratie
onboarding-automatic_proportions-prev_step = Vorige stap
onboarding-automatic_proportions-put_trackers_on-title = Doe je trackers aan
onboarding-automatic_proportions-put_trackers_on-description = Om je verhoudingen te kalibreren, gaan we gebruik maken van de trackers die je net hebt toegewezen. Doe al je trackers aan, je kunt zien welke trackers welke zijn in de figuur rechts.
onboarding-automatic_proportions-put_trackers_on-next = Ik heb al mijn trackers aan
onboarding-automatic_proportions-preparation-title = Voorbereiding
onboarding-automatic_proportions-preparation-description = Plaats een stoel recht achter je binnen je speelruimte. Zorg dat je klaar bent om te gaan zitten tijdens de autobone-configuratie.
onboarding-automatic_proportions-preparation-next = Ik sta voor een stoel
onboarding-automatic_proportions-start_recording-title = Zorg dat je klaar bent om te bewegen
onboarding-automatic_proportions-start_recording-description = We gaan nu enkele specifieke houdingen en bewegingen opnemen. Deze worden in het volgende scherm geprompt. Zorg dat je klaar bent om te beginnen als de knop wordt ingedrukt!
onboarding-automatic_proportions-start_recording-next = Start opname
onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = Opname bezig...
onboarding-automatic_proportions-recording-description-p1 = Voer de onderstaande bewegingen uit:
onboarding-automatic_proportions-recording-steps-0 = Buig een paar keer op je knieën.
onboarding-automatic_proportions-recording-steps-1 = Ga zitten op een stoel en sta weer op.
onboarding-automatic_proportions-recording-steps-2 = Draai je bovenlichaam naar links, buig dan naar rechts.
onboarding-automatic_proportions-recording-steps-3 = Draai je bovenlichaam naar rechts, buig dan naar links.
onboarding-automatic_proportions-recording-steps-4 = Wiebel rond tot de timer is afgelopen.
onboarding-automatic_proportions-recording-processing = Resultaat verwerken
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 15)
onboarding-automatic_proportions-recording-timer = { $time ->
    [one] 1 seconde resterend
    *[other] { $time } seconden resterend
}
onboarding-automatic_proportions-verify_results-title = Resultaten controleren
onboarding-automatic_proportions-verify_results-description = Controleer de resultaten hieronder, zien ze er correct uit?
onboarding-automatic_proportions-verify_results-results = Opnameresultaten
onboarding-automatic_proportions-verify_results-processing = Resultaat verwerken
onboarding-automatic_proportions-verify_results-redo = Opname opnieuw doen
onboarding-automatic_proportions-verify_results-confirm = Ze zijn correct
onboarding-automatic_proportions-done-title = Lichaam gemeten en opgeslagen.
onboarding-automatic_proportions-done-description = Je kalibratie voor lichaamsverhoudingen is voltooid!

## Home
home-no_trackers = Geen trackers gedetecteerd of toegewezen
