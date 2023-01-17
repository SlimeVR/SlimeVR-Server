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
skeleton_bone-TORSO = Torso Lengte
skeleton_bone-CHEST = Borstafstand
skeleton_bone-WAIST = Tailleafstand
skeleton_bone-HIP_OFFSET = Heupoffset
skeleton_bone-HIPS_WIDTH = Heupbreedte
skeleton_bone-LEGS_LENGTH = Beenlengte
skeleton_bone-KNEE_HEIGHT = Kniehoogte
skeleton_bone-FOOT_LENGTH = Voetlengte
skeleton_bone-FOOT_SHIFT = Voetverschuiving
skeleton_bone-SKELETON_OFFSET = Skelettenoffset
skeleton_bone-CONTROLLER_DISTANCE_Z = Controllerafstand Z
skeleton_bone-CONTROLLER_DISTANCE_Y = Controllerafstand Y
skeleton_bone-FOREARM_LENGTH = Onderarmlengte
skeleton_bone-SHOULDERS_DISTANCE = Schoudersafstand
skeleton_bone-SHOULDERS_WIDTH = Schouderbreedte
skeleton_bone-UPPER_ARM_LENGTH = Bovenarmlengte
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
navbar-home = Home
navbar-body_proportions = Lichaamsverhoudingen
navbar-trackers_assign = Tracker toewijzing
navbar-mounting = Bevestigingskalibratie
navbar-onboarding = Setup Wizard
navbar-settings = Instellingen

## Bounding volume hierarchy recording
bvh-start_recording = Opnemen BVH
bvh-recording = Opnemen...

## Overlay settings
overlay-is_visible_label = Overlay in SteamVR weergeven
overlay-is_mirrored_label = Overlay weergeven als spiegel

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
tracker-settings-mounting_section = Bevestigingspositie
tracker-settings-mounting_section-description = Waar bevindt de tracker zich?
tracker-settings-mounting_section-edit = Bevestiging bewerken
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Trackernaam
tracker-settings-name_section-placeholder = NightyBeast's linkerbeen
tracker-settings-name_section-description = Geef een schattige bijnaam :)

## Tracker part card info
tracker-part_card-no_name = Geen naam
tracker-part_card-unassigned = Niet toegewezen

## Body assignment menu
body_assignment_menu = Waar wil je deze tracker hebben?
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
tracker_selection_menu-NONE = Which tracker do you want to be unassigned?
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
settings-general-tracker_mechanics-subtitle = Filtering
# This also cares about multilines
settings-general-tracker_mechanics-description =
    Kies het type filter voor uw trackers.
    Prediction voorspelt beweging terwijl smoothing bewegingen vloeiender maakt.
settings-general-tracker_mechanics-filtering_type = Filtering type
settings-general-tracker_mechanics-filtering_type-none = Geen filtering
settings-general-tracker_mechanics-filtering_type-none-description = Gebruik rotaties zoals ze zijn. Zal geen filtering uitvoeren.
settings-general-tracker_mechanics-filtering_type-smoothing = Smoothing
settings-general-tracker_mechanics-filtering_type-smoothing-description = Maakt bewegingen vloeiender, maar voegt enige latentie toe.
settings-general-tracker_mechanics-filtering_type-prediction = Voorspelling
settings-general-tracker_mechanics-filtering_type-prediction-description = Verlaagt latentie en maakt bewegingen snappier, maar kan jitter verhogen.
settings-general-tracker_mechanics-amount = Hoeveelheid

## FK/Tracking settings
settings-general-fk_settings = Tracking instellingen
settings-general-fk_settings-leg_tweak = Been tracking aanpassingen
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
    [one] 1 tap
    *[other] { $amount } taps
}
settings-general-gesture_control-quickResetEnabled = Schakel tikken in om snel opnieuw te starten
settings-general-gesture_control-quickResetDelay = Vertraging snel opnieuw starten
settings-general-gesture_control-quickResetTaps = Tikken voor snel opnieuw starten
settings-general-gesture_control-resetEnabled = Schakel tikken in om opnieuw te starten
settings-general-gesture_control-resetDelay = Vertraging opnieuw starten
settings-general-gesture_control-resetTaps = Tikken voor opnieuw starten
settings-general-gesture_control-mountingResetEnabled = Schakel tikken in om opnieuw te starten bij montage
settings-general-gesture_control-mountingResetDelay = Vertraging opnieuw starten bij montage
settings-general-gesture_control-mountingResetTaps = Tikken voor opnieuw starten bij montage

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
    Stel de poorten in voor het luisteren en verzenden van gegevens.
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
settings-osc-vrchat-network-description = Stel de poorten in om naar en te verzenden naar VRChat te luisteren.
settings-osc-vrchat-network-port_in =
    .label = Poort In
    .placeholder = Poort in (standaard: 9001)
settings-osc-vrchat-network-port_out =
    .label = Poort Out
    .placeholder = Poort uit (standaard: 9000)
settings-osc-vrchat-network-address = Netwerkadres
settings-osc-vrchat-network-address-description = Kies naar welk adres u gegevens naar VRChat wilt verzenden (controleer uw wifi-instellingen op uw apparaat).
settings-osc-vrchat-network-address-placeholder = VRChat IP-adres
settings-osc-vrchat-network-trackers = Trackers
settings-osc-vrchat-network-trackers-description = Schakel het verzenden van specifieke trackers via OSC in en uit.
settings-osc-vrchat-network-trackers-chest = Borst
settings-osc-vrchat-network-trackers-waist = Taille
settings-osc-vrchat-network-trackers-knees = Knieën
settings-osc-vrchat-network-trackers-feet = Voeten
settings-osc-vrchat-network-trackers-elbows = Ellebogen

## Setup/onboarding menu
onboarding-skip = Stelopstelling overslaan
onboarding-continue = Doorgaan
onboarding-wip = Werk in uitvoering

## WiFi setup
onboarding-wifi_creds-back = Ga terug naar introductie
onboarding-wifi_creds = Voer WiFi-credentials in
# This cares about multilines
onboarding-wifi_creds-description =
    De trackers gebruiken deze gegevens om draadloos te verbinden
    gebruik alstublieft de gegevens waarmee u momenteel verbonden bent
onboarding-wifi_creds-skip = WiFi-instellingen overslaan
onboarding-wifi_creds-submit = Verzenden!
onboarding-wifi_creds-ssid =
    .label = SSID
    .placeholder = Enter SSID
onboarding-wifi_creds-password =
    .label = Password
    .placeholder = Enter password

## Mounting setup
onboarding-reset_tutorial-back = Ga terug naar bevestigingscalibratie
onboarding-reset_tutorial = Tutorial opnieuw instellen
onboarding-reset_tutorial-description = Deze functie is nog niet klaar, druk gewoon op doorgaan

## Setup start
onboarding-home = Welkom bij SlimeVR
# This cares about multilines and it's centered!!
onboarding-home-description =
    Volledig lichaamstracking voor iedereen
    aanbieden
onboarding-home-start = Laten we opstellen!

## Enter VR part of setup
onboarding-enter_vr-back = Ga terug naar trackeropdracht
onboarding-enter_vr-title = Tijd om VR in te gaan!
onboarding-enter_vr-description = Doe al je trackers aan en ga dan in VR!
onboarding-enter_vr-ready = Ik ben klaar

## Setup done
onboarding-done-title = Je bent klaar!
onboarding-done-description = Geniet van je full body ervaring
onboarding-done-close = Sluit de gids

## Tracker connection setup
onboarding-connect_tracker-back = Ga terug naar WiFi-credentials
onboarding-connect_tracker-title = Trackers verbinden
onboarding-connect_tracker-description-p0 = Nu de leuke partij, verbind alle trackers!
onboarding-connect_tracker-description-p1 = Verbind gewoon alle nog niet verbonden trackers via een USB-poort.
onboarding-connect_tracker-issue-serial = Ik heb problemen met verbinden!
onboarding-connect_tracker-usb = USB Tracker
onboarding-connect_tracker-connection_status-connecting = Wifi-credentials verzenden
onboarding-connect_tracker-connection_status-connected = Verbonden met WiFi
onboarding-connect_tracker-connection_status-error = Kan niet verbinden met WiFi
onboarding-connect_tracker-connection_status-start_connecting = Zoeken naar trackers
onboarding-connect_tracker-connection_status-handshake = Verbonden met de server
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
onboarding-connect_tracker-next = Ik heb alle mijn trackers verbonden

## Tracker assignment setup
onboarding-assign_trackers-back = Ga terug naar WiFi-credentials
onboarding-assign_trackers-title = Trackers toewijzen
onboarding-assign_trackers-description = Laten we kiezen welke tracker waar gaat. Klik op een locatie waar je een tracker wilt plaatsen
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned = { $assigned } of { $trackers ->
    [one] 1 tracker
    *[other] { $trackers } trackers
} assigned
onboarding-assign_trackers-advanced = Geavanceerde toewijzingslocaties weergeven
onboarding-assign_trackers-next = Ik heb alle trackers toegewezen

## Tracker manual mounting setup
onboarding-manual_mounting-back = Ga terug naar VR instappen
onboarding-manual_mounting = Handmatige bevestiging
onboarding-manual_mounting-description = Klik op elke tracker en selecteer op welke manier ze zijn bevestigd
onboarding-manual_mounting-auto_mounting = Automatische bevestiging
onboarding-manual_mounting-next = Volgende stap

## Tracker automatic mounting setup
onboarding-automatic_mounting-back = Ga terug naar VR instappen
onboarding-automatic_mounting-title = Bevestigingscalibratie
onboarding-automatic_mounting-description = Om SlimeVR-trackers te laten werken, moeten we een bevestigingsrotatie toewijzen aan uw trackers om deze te aligneren met uw fysieke trackerbevestiging.
onboarding-automatic_mounting-manual_mounting = Bevestiging handmatig instellen
onboarding-automatic_mounting-next = Volgende stap
onboarding-automatic_mounting-prev_step = Vorige stap
onboarding-automatic_mounting-done-title = Bevestigingsrotaties gecalibreerd.
onboarding-automatic_mounting-done-description = Uw bevestigingscalibratie is compleet!
onboarding-automatic_mounting-done-restart = Terug naar start
onboarding-automatic_mounting-mounting_reset-title = Bevestiging opnieuw instellen
onboarding-automatic_mounting-mounting_reset-step-0 = 1. Ga in een "skiën"-houding met gebogen benen, uw bovenlichaam naar voren gekanteld en gebogen armen.
onboarding-automatic_mounting-mounting_reset-step-1 = 2. Druk op de knop "Bevestiging opnieuw instellen" en wacht 3 seconden voordat de bevestigingsrotaties van de trackers opnieuw worden ingesteld.
onboarding-automatic_mounting-preparation-title = Voorbereiding
onboarding-automatic_mounting-preparation-step-0 = 1. Sta rechtop met uw armen langs uw zij.
onboarding-automatic_mounting-preparation-step-1 = 2. Druk op de knop "Opnieuw instellen" en wacht 3 seconden voordat de trackers opnieuw worden ingesteld.
onboarding-automatic_mounting-put_trackers_on-title = Doe je trackers aan
onboarding-automatic_mounting-put_trackers_on-description = Om bevestigingsrotaties te calibreren, gaan we gebruik maken van de trackers die je net hebt toegewezen. Doe al je trackers aan, je kunt zien welke trackers welke zijn in de figuur rechts.
onboarding-automatic_mounting-put_trackers_on-next = Ik heb alle mijn trackers aan

## Tracker manual proportions setup
onboarding-manual_proportions-back = Ga terug naar tutorial opnieuw instellen
onboarding-manual_proportions-title = Handmatige lichaamsverhoudingen
onboarding-manual_proportions-precision = Precisie-aanpassing
onboarding-manual_proportions-auto = Automatische calibratie

## Tracker automatic proportions setup
onboarding-automatic_proportions-back = Ga terug naar tutorial opnieuw instellen
onboarding-automatic_proportions-title = Meet je lichaam
onboarding-automatic_proportions-description = Om SlimeVR-trackers te laten werken, moeten we de lengte van je botten weten. Deze korte calibratie meet het voor je.
onboarding-automatic_proportions-manual = Handmatige calibratie
onboarding-automatic_proportions-prev_step = Vorige stap
onboarding-automatic_proportions-put_trackers_on-title = Doe je trackers aan
onboarding-automatic_proportions-put_trackers_on-description = Om je verhoudingen te calibreren, gaan we gebruik maken van de trackers die je net hebt toegewezen. Doe al je trackers aan, je kunt zien welke trackers welke zijn in de figuur rechts.
onboarding-automatic_proportions-put_trackers_on-next = Ik heb alle mijn trackers aan
onboarding-automatic_proportions-preparation-title = Voorbereiding
onboarding-automatic_proportions-preparation-description = Plaats een stoel recht achter je binnen je speelruimte. Zorg dat je klaar bent om te gaan zitten tijdens de autobone-installatie.
onboarding-automatic_proportions-preparation-next = Ik ben voor een stoel
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
    [one] 1 second left
    *[other] { $time } seconds left
}
onboarding-automatic_proportions-verify_results-title = Resultaten controleren
onboarding-automatic_proportions-verify_results-description = Controleer de resultaten hieronder, zien ze er correct uit?
onboarding-automatic_proportions-verify_results-results = Opnameresultaten
onboarding-automatic_proportions-verify_results-processing = Resultaat verwerken
onboarding-automatic_proportions-verify_results-redo = Opname opnieuw doen
onboarding-automatic_proportions-verify_results-confirm = Ze zijn correct
onboarding-automatic_proportions-done-title = Lichaam gemeten en opgeslagen.
onboarding-automatic_proportions-done-description = Je calibratie voor lichaamsverhoudingen is voltooid!

## Home
home-no_trackers = Geen trackers gedetecteerd of toegewezen
