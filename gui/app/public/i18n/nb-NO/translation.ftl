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
tips-failed_webgl = Feil ved initialisering av WebGL.

## Units


## Dropdown


## Text input


## File input


## Window controls

titlebar-close = Lukk

## Body parts

body_part-NONE = Utilordnet
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
board_type-CUSTOM = Egendefinert brett

## Proportions

skeleton_bone-NONE = Ingen
skeleton_bone-HEAD = Hode bytte
skeleton_bone-NECK = Halslengde
skeleton_bone-torso_group = Torsolengde
skeleton_bone-HIP = Hofte-lengde
skeleton_bone-HIPS_WIDTH = Hofte-bredde
skeleton_bone-UPPER_LEG = Lår-lengde
skeleton_bone-LOWER_LEG = Leg-lengde
skeleton_bone-FOOT_LENGTH = Fot-lengde
skeleton_bone-FOOT_SHIFT = Fot-bytte
skeleton_bone-SHOULDERS_DISTANCE = Skulder-distanse
skeleton_bone-SHOULDERS_WIDTH = Skulder-bredde
skeleton_bone-UPPER_ARM = Overarms-lengde
skeleton_bone-LOWER_ARM = Nedre arm-lengde

## Tracker reset buttons

reset-reset_all = Nullstill alle proporsjoner
reset-reset_all_warning-cancel = Avslutt
reset-full = Nullstill
reset-mounting = Monterings nullstilling

## Navigation bar

navbar-home = Hjem
navbar-body_proportions = Kropps proporsjoner
navbar-trackers_assign = Tracker tildeling
navbar-mounting = Monterings nullstilling
navbar-onboarding = Oppsetts veiviser
navbar-settings = Innstillinger

## Biovision hierarchy recording

bvh-start_recording = BVH-innspilling
bvh-recording = Spiller inn...

## Tracking pause


## Widget: Developer settings

widget-developer_mode = Utvikler modus
widget-developer_mode-high_contrast = Høy kontrast
widget-developer_mode-precise_rotation = Nøyaktig rotering
widget-developer_mode-fast_data_feed = Rask data-feed
widget-developer_mode-raw_slime_rotation = Rå

## Widget: IMU Visualizer

widget-imu_visualizer = Rotasjon
widget-imu_visualizer-rotation_raw = Rå
widget-imu_visualizer-rotation_preview = Forhåndsvisning

## Tracker status

tracker-status-none = Ingen status
tracker-status-busy = Opptatt
tracker-status-error = Feilmelding
tracker-status-disconnected = Frakoblet
tracker-status-occluded = Okkludert

## Tracker status columns

tracker-table-column-name = Navn
tracker-table-column-battery = Batteri
tracker-table-column-linear-acceleration = Aksel. X/Y/Z
tracker-table-column-rotation = Rotasjon X/Y/Z
tracker-table-column-position = Posisjon X/Y/Z

## Tracker rotation

tracker-rotation-left = Venstre
tracker-rotation-right = Høyre
tracker-rotation-back = Bak
tracker-rotation-custom = Egendefinert

## Tracker information

tracker-infos-manufacturer = Produsent
tracker-infos-display_name = Vis navn
tracker-infos-custom_name = Tilpasset navn

## Tracker settings

tracker-settings-back = Gå tilbake til tracker-liste
tracker-settings-title = Tracker innstillinger
tracker-settings-assignment_section = Tildeling
tracker-settings-assignment_section-description = Kroppsdelen trackeren er blitt tildelt.
tracker-settings-assignment_section-edit = Endre tildeling
tracker-settings-mounting_section = Monterings posisjon
tracker-settings-mounting_section-description = Hvor er trackeren montert?
tracker-settings-mounting_section-edit = Endre montering
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Tracker navn
tracker-settings-name_section-placeholder = ~Thͭiͪaͥsͣˢ~ venstre ben
tracker-settings-name_section-label = Tracker navn

## Dongle settings

dongle-status-disconnected = Frakoblet
dongle-settings-back = Gå tilbake til tracker-liste

## Tracker part card info

tracker-part_card-unassigned = Utilordnet

## Body assignment menu

body_assignment_menu = Hvor vil du at denne trackeren skal være?
body_assignment_menu-description = Velg den plasseringen du vil tildele denne trackeren. Alternativt kan du velge å ordne alle trackere samtidig istedenfor å ordne dem én etter én.
body_assignment_menu-manage_trackers = Ordne alle trackere
body_assignment_menu-unassign_tracker = Fjern tracker-tildeling

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>NB:</b> Hals-trackeren kan være helseskadelig dersom den sitter for stramt,
    festet kan blokkere blodtilførselen til hodet ditt!
tracker_selection_menu-neck_warning-done = Jeg forstår risikoene
tracker_selection_menu-neck_warning-cancel = Avslutt

## Mounting menu

mounting_selection_menu-close = Lukk

## Sidebar settings

settings-sidebar-title = Innstillinger
settings-sidebar-general = Generelle
settings-sidebar-trackers = Trackere
settings-sidebar-interface = Grensesnitt
settings-sidebar-utils = Verktøy

## Bone routing settings

settings-routing-hands-warning-cancel = Avslutt

## SteamVR / Monado output settings

settings-driver-enable = Aktiver

## Tracker mechanics

settings-general-tracker_mechanics-filtering = Filtrering
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Velg filtrerings-type for dine trackere.
    Prediksjon forutser bevegelser mens Utjevning jevner ut bevegelser.
settings-general-tracker_mechanics-filtering-type-none = Ingen filtrering
settings-general-tracker_mechanics-filtering-type-none-description = Bruk rotasjoner som før. Altså ingen filtrering.
settings-general-tracker_mechanics-filtering-type-smoothing = Utjevning
settings-general-tracker_mechanics-filtering-type-smoothing-description = Jevner ut bevegelser men øker forsinkelsen litt.
settings-general-tracker_mechanics-filtering-type-prediction = Forutsigelse
settings-general-tracker_mechanics-filtering-type-prediction-description = Reduserer forsinkelsen og gjør bevegelser skarpere, men kan påvirke stabiliteten.
settings-general-tracker_mechanics-use_mag_on_all_trackers = Bruk magnetometer på alle IMU-trackere som støtter det
settings-general-tracker_mechanics-use_mag_on_all_trackers-description =
    Bruker magnetometer på alle trackere som har en kompatibel firmware for det, noe som reduserer drift i stabile magnetiske områder.
    Kan deaktiveres per tracker i trackerens innstillinger. <b>Ikke slå av noen av trackerne mens du endrer denne innstillingen !</b>
settings-general-tracker_mechanics-use_mag_on_all_trackers-label = Bruk magnetometer på trackere
settings-stay_aligned-general-label = Generelle
settings-stay_aligned-relaxed_poses-close = Lukk

## Keybinds Page

settings-keybinds_full-reset = Nullstill
settings-keybinds-recorder-modal-cancel-button = Avslutt

## FK/Tracking settings

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
settings-general-fk_settings-arm_fk-back = Bak

## Gesture control settings (tracker tapping)

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


## Behavior settings

settings-general-interface-dev_mode = Utvikler modus
settings-general-interface-dev_mode-label = Utvikler modus
settings-interface-behavior-error_tracking-description_v2 =
    <h1>Samtykker du til innsamling av anonymiserte feildata?</h1>
    
    <b>Vi samler ikke inn personlig informasjon</b> slik som din IP-adresse eller trådløs-legitimasjon. SlimeVR verdsetter ditt personvern!
    
    For å gi deg den beste brukeropplevelsen, samler vi inn anonymiserte feilrapporter, ytelsesmålinger og informasjon om operativsystemet. Dette hjelper oss med å oppdage feil og problemer med SlimeVR. Disse beregningene samles inn via Sentry.io.
settings-interface-behavior-error_tracking-label = Send feilmeldinger til utviklere

## Serial settings

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
settings-serial-send_command-warning-ok = Jeg vet hva jeg driver med
settings-serial-send_command-warning-cancel = Avslutt

## OSC VRChat settings

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

## VRChat OSC status

settings-osc-vrchat-status-tracking = Rotasjon
settings-osc-vrchat-status-badge-error = Feilmelding
settings-osc-vrchat-status-badge-unknown = Ukjent

## VMC OSC settings

settings-osc-vmc-enable = Aktiver
settings-osc-vmc-enable-description = Skru av/på utsending og mottakelse av data.
settings-osc-vmc-enable-label = Aktiver
settings-osc-vmc-network = Nettverks-porter
settings-osc-vmc-network-port_in =
    .label = Port Inn
    .placeholder = Port in (default: 39540)
settings-osc-vmc-network-port_out =
    .label = Port Ut
    .placeholder = Port out (default: 39539)
settings-osc-vmc-network-address = Nettverksadresse
settings-osc-vmc-network-address-placeholder = IPV4 adresse
settings-osc-vmc-status-badge-error = Feilmelding

## Common OSC settings


## Advanced settings

settings-utils-advanced-reset_warning-cancel = Avslutt

## Home Screen


## Tracking Checklist


## Setup/onboarding menu

onboarding-skip = Hopp over oppsett
onboarding-continue = Fortsett
onboarding-previous_step = Forrige steg
onboarding-setup_warning-skip = Hopp over oppsett

## Quiz

onboarding-quiz_continue = Fortsett
onboarding-quiz_back = Bak

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

