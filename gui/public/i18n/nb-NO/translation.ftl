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

## BoardType

board_type-UNKNOWN = Ukjent
board_type-NODEMCU = NodeMCU
board_type-CUSTOM = Egendefinert brett
board_type-WROOM32 = WROOM32
board_type-WEMOSD1MINI = Wemos D1 Mini
board_type-SLIMEVR = SlimeVR
board_type-OWOTRACK = owoTrack
board_type-XIAO_ESP32C3 = Seeed Studio XIAO ESP32C3
board_type-HARITORA = Haritora
board_type-ESP32C6DEVKITC1 = Espressif ESP32-C6 DevKitC-1

## Proportions

skeleton_bone-NONE = Ingen
skeleton_bone-HEAD = Hode bytte
skeleton_bone-NECK = Halslengde
skeleton_bone-torso_group = Torsolengde
skeleton_bone-CHEST_OFFSET = Bryst-forskyvning
skeleton_bone-CHEST = Bryst-lengde
skeleton_bone-WAIST = Midje-lengde
skeleton_bone-HIP = Hofte-lengde
skeleton_bone-HIP_OFFSET = Hofte-forskyvning
skeleton_bone-HIPS_WIDTH = Hofte-bredde
skeleton_bone-UPPER_LEG = Lår-lengde
skeleton_bone-LOWER_LEG = Leg-lengde
skeleton_bone-FOOT_LENGTH = Fot-lengde
skeleton_bone-FOOT_SHIFT = Fot-bytte
skeleton_bone-SKELETON_OFFSET = Skjellet-forskyvning
skeleton_bone-SHOULDERS_DISTANCE = Skulder-distanse
skeleton_bone-SHOULDERS_WIDTH = Skulder-bredde
skeleton_bone-UPPER_ARM = Overarms-lengde
skeleton_bone-LOWER_ARM = Nedre arm-lengde
skeleton_bone-ELBOW_OFFSET = Albue-forskyvning

## Tracker reset buttons

reset-reset_all = Nullstill alle proporsjoner
reset-reset_all_warning-cancel = Avbryt
reset-reset_all_warning_default-v2 =
    <b>Advarsel:</b> Høyden din har ikke blitt konfigurert, proporsjonene dine vil bli tilbakestilt til standardinnstillingene med standardhøyden.
    Er du sikker på at du vil gjøre dette?
reset-full = Nullstill
reset-mounting = Nullstill montering

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

## Biovision hierarchy recording

bvh-start_recording = BVH-innspilling
bvh-recording = Spiller inn...

## Tracking pause


## Widget: Overlay settings

widget-overlay = Overlegg
widget-overlay-is_visible_label = Vis overlegg i SteamVR
widget-overlay-is_mirrored_label = Vis overlegg som speil

## Widget: Drift compensation


## Widget: Clear Mounting calibration


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
widget-imu_visualizer-rotation_raw = Rå
widget-imu_visualizer-rotation_preview = Forhåndsvisning

## Widget: Skeleton Visualizer


## Tracker status

tracker-status-none = Ingen status
tracker-status-busy = Opptatt
tracker-status-error = Feilmelding
tracker-status-disconnected = Frakoblet
tracker-status-occluded = Okkludert
tracker-status-ok = OK

## Tracker status columns

tracker-table-column-name = Navn
tracker-table-column-type = Type
tracker-table-column-battery = Batteri
tracker-table-column-ping = Ping
tracker-table-column-tps = TPS
tracker-table-column-temperature = Temp. °C
tracker-table-column-linear-acceleration = Aksel. X/Y/Z
tracker-table-column-rotation = Rotasjon X/Y/Z
tracker-table-column-position = Posisjon X/Y/Z
tracker-table-column-url = URL

## Tracker rotation

tracker-rotation-front = Front
tracker-rotation-left = Venstre
tracker-rotation-right = Høyre
tracker-rotation-back = Bak
tracker-rotation-custom = Egendefinert

## Tracker information

tracker-infos-manufacturer = Produsent
tracker-infos-display_name = Vis navn
tracker-infos-custom_name = Tilpasset navn
tracker-infos-url = Tracker URL

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
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Tracker navn
tracker-settings-name_section-description = Gi den et søtt kallenavn :)
tracker-settings-name_section-placeholder = ~Thͭiͪaͥsͣˢ~ venstre ben

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
settings-sidebar-tracker_mechanics = Tracker-mekanisme
settings-sidebar-fk_settings = Tracker-innstillinger
settings-sidebar-gesture_control = Bevegelses-kontroll
settings-sidebar-interface = Grensesnitt
settings-sidebar-osc_router = OSC ruter
settings-sidebar-utils = Verktøy
settings-sidebar-serial = Seriell konsoll

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
settings-general-steamvr-trackers-hands-warning-cancel = Avbryt

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
settings-general-tracker_mechanics-drift_compensation = Avdrifts-kompansering
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Kompenserer for IMU yaw avdrift ved å legge til en rotasjon av omvendt verdi.
    Endre mengde kompensasjon og opp til hvor mange nullstillinger som skal bli gjort rede for.
settings-general-tracker_mechanics-drift_compensation-enabled-label = Avdrifts kompansering
settings-general-tracker_mechanics-drift_compensation_warning-cancel = Avbryt
settings-general-tracker_mechanics-drift_compensation-amount-label = Kompanserings mengde
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Bruk opp til x siste nullstillinger
settings-general-tracker_mechanics-use_mag_on_all_trackers = Bruk magnetometer på alle IMU-trackere som støtter det
settings-general-tracker_mechanics-use_mag_on_all_trackers-description =
    Bruker magnetometer på alle trackere som har en kompatibel firmware for det, noe som reduserer drift i stabile magnetiske områder.
    Kan deaktiveres per tracker i trackerens innstillinger. <b>Ikke slå av noen av trackerne mens du endrer denne innstillingen !</b>
settings-general-tracker_mechanics-use_mag_on_all_trackers-label = Bruk magnetometer på trackere

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
settings-general-fk_settings-leg_tweak-skating_correction-amount = Skating-korreksjon styrke
settings-general-fk_settings-arm_fk = Arm sporing
settings-general-fk_settings-arm_fk-description = Endre metoden armene spores på.
settings-general-fk_settings-arm_fk-force_arms = Tving armer fra HMD
settings-general-fk_settings-skeleton_settings-description = Skru skjellet innstillinger av eller på. Det anbefales å la disse stå på.

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
settings-general-gesture_control-mountingResetEnabled = Aktiver monterings-nullstilling ved trykking
settings-general-gesture_control-mountingResetDelay = Monterings-nullstilling utsettelse
settings-general-gesture_control-mountingResetTaps = Trykking for monterings-nullstilling

## Appearance settings

settings-general-interface-dev_mode = Utvikler modus
settings-general-interface-dev_mode-description = Denne modusen kan være hjelpsom dersom du trenger data som gir mer innsyn eller for å samhandle med tilkoblede sporere på et mer avansert nivå.
settings-general-interface-dev_mode-label = Utvikler modus
settings-general-interface-lang = Velg språk
settings-general-interface-lang-description = Endre hovedspråket du vil bruke.
settings-general-interface-lang-placeholder = Velg språket du vil bruke

## Notification settings

settings-general-interface-serial_detection = Seriell enhets gjenkjenning
settings-general-interface-serial_detection-description = Dette valget viser en pop-up for hver gang du kobler til en ny seriell-enhet som kunne vært en sporer. Dette forbedrer sporerens konfigureringsprosess.
settings-general-interface-serial_detection-label = Seriell enhets gjenkjenning

## Behavior settings

settings-interface-behavior-error_tracking-description_v2 =
    <h1>Samtykker du til innsamling av anonymiserte feildata?</h1>
    
    <b>Vi samler ikke inn personlig informasjon</b> slik som din IP-adresse eller trådløs-legitimasjon. SlimeVR verdsetter ditt personvern!
    
    For å gi deg den beste brukeropplevelsen, samler vi inn anonymiserte feilrapporter, ytelsesmålinger og informasjon om operativsystemet. Dette hjelper oss med å oppdage feil og problemer med SlimeVR. Disse beregningene samles inn via Sentry.io.
settings-interface-behavior-error_tracking-label = Send feilmeldinger til utviklere

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
settings-osc-vrchat-enable = Aktiver
settings-osc-vrchat-enable-description = Skru av/på utsending og mottakelse av data.
settings-osc-vrchat-enable-label = Aktiver
settings-osc-vrchat-network = Nettverks-porter
settings-osc-vrchat-network-port_in =
    .label = Port Inn
    .placeholder = Port inn (normalverdi: 9001)
settings-osc-vrchat-network-port_out =
    .label = Port Ut
    .placeholder = Port ut (standardisering: 9000)
settings-osc-vrchat-network-address = Nettverksadresse
settings-osc-vrchat-network-address-placeholder = VRChat ip adresse
settings-osc-vrchat-network-trackers = Trackere
settings-osc-vrchat-network-trackers-description = Skru av/på sendingen av spesifikke sporere via OSC.
settings-osc-vrchat-network-trackers-chest = Bryst
settings-osc-vrchat-network-trackers-hip = Hofte
settings-osc-vrchat-network-trackers-knees = Knær
settings-osc-vrchat-network-trackers-feet = Føtter
settings-osc-vrchat-network-trackers-elbows = Albuer

## VMC OSC settings


## Common OSC settings


## Advanced settings


## Home Screen


## Tracking Checlist


## Setup/onboarding menu

onboarding-skip = Hopp over oppsett
onboarding-continue = Fortsett
onboarding-wip = Arbeid pågår

## Wi-Fi setup

onboarding-wifi_creds-back = Gå tilbake til introduksjonen
onboarding-wifi_creds-skip = Hopp over Wi-Fi innstillinger
onboarding-wifi_creds-submit = Send inn!
onboarding-wifi_creds-ssid =
    .label = Wi-Fi navn
    .placeholder = Tast inn navnet på ditt Wi-Fi nettverk
onboarding-wifi_creds-password =
    .label = Passord
    .placeholder = Tast inn passord

## Mounting setup

onboarding-reset_tutorial-back = Gå tilbake til monterings kalibrering
onboarding-reset_tutorial = Nullstill opplæringen
onboarding-reset_tutorial-skip = Hopp over trinn

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
onboarding-connect_tracker-issue-serial = Jeg sliter med å koble til!
onboarding-connect_tracker-usb = USB Tracker
onboarding-connect_tracker-connection_status-connecting = Sender Wi-Fi legitimasjon
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


## Tracker assignment tutorial


## Tracker assignment setup

onboarding-assign_trackers-back = Gå tilbake til Wi-Fi legitimasjon
onboarding-assign_trackers-title = Tildel trackerne
onboarding-assign_trackers-description = La oss velge hvilke trackere som skal hvor. Trykk på stedet der du vil plassere en tracker
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

## Tracker assignment warnings


## Tracker mounting method choose

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
onboarding-automatic_mounting-preparation-title = Forberedning
onboarding-automatic_mounting-put_trackers_on-title = Ta på deg dine trackere
onboarding-automatic_mounting-put_trackers_on-description = For å kalibrere monterings-rotasjonene, må vi bruke trackerne du akkurat tildelte. Ta på deg alle dine trackere, du kan se hvem som er hvem i figuren til høyre.
onboarding-automatic_mounting-put_trackers_on-next = Jeg har alle mine trackere på

## Tracker manual proportions setupa

onboarding-manual_proportions-title = Manuelle kropps-proporsjoner

## Tracker automatic proportions setup

onboarding-automatic_proportions-back = Gå tilbake til Nullstillings opplæring
onboarding-automatic_proportions-title = Mål opp kroppen din
onboarding-automatic_proportions-description = For at SlimeVR trackere skal funke, må vi vite lengden på beinene dine. Denne korte kalibreringen kan måle det opp for deg.
onboarding-automatic_proportions-manual = Manuell kalibrering
onboarding-automatic_proportions-prev_step = Forrige steg
onboarding-automatic_proportions-put_trackers_on-title = Ta på deg alle dine trackere
onboarding-automatic_proportions-put_trackers_on-description = For å kalibrere dine proporsjoner, bruker vi trackerne du akkurat har tildelt. Ta på deg alle dine trackere, du kan se hvem som går hvor i figuren til høyre.
onboarding-automatic_proportions-put_trackers_on-next = Jeg har alle mine trackere på
onboarding-automatic_proportions-start_recording-title = Gjør deg klar til å bevege deg
onboarding-automatic_proportions-start_recording-description = Vi kommer nå til å spille inn noen spesifikke poseringer og bevegelser. Disse vil bli vist i den neste skjermen. Gjør deg klar til å starte når du trykker på knappen!
onboarding-automatic_proportions-start_recording-next = Start Innspilling
onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = Innspilling pågår...
onboarding-automatic_proportions-recording-description-p1 = Utfør bevegelsene vist nedenfor:
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

## User height calibration


## Stay Aligned setup


## Home

home-no_trackers = Ingen trackere oppdaget eller tildelt

## Trackers Still On notification


## Status system


## Firmware tool globals


## Firmware tool Steps


## firmware tool build status


## Firmware update status


## Dedicated Firmware Update Page


## Tray Menu


## First exit modal

tray_or_exit_modal-cancel = Avbryt

## Unknown device modal


## Error collection consent modal


## Tracking checklist section

