# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Serveriga ühendamine
websocket-connection_lost = Ühendus serveriga on kaotatud. Ühenduse taastamine...

## Update notification

version_update-title = Uus versioon olemas: { $version }
version_update-description = Vajutades "{ version_update-update }" tõmbab programm alla SlimeVR installeri.
version_update-update = Värskenda
version_update-close = Sulge

## Tips

tips-find_tracker = Ei ole kindel milline jälgija on mis? Raputage jälgijat, ning õige jälgija tõstetakse esile.
tips-do_not_move_heels = Veenduge, et teie kannad ei liigu salvestamise ajal!
tips-file_select = Pukseerige failid kasutamiseks, või <u>sirvi</u>.

## Units


## Dropdown


## Text input


## File input


## Window controls

titlebar-close = Sulge

## Body parts

body_part-NONE = Määramata
body_part-HEAD = Pea
body_part-NECK = Kael
body_part-RIGHT_SHOULDER = Parem õlg
body_part-RIGHT_UPPER_ARM = Parem õlavars
body_part-RIGHT_LOWER_ARM = Parem küünarvars
body_part-RIGHT_HAND = Parem käsi
body_part-RIGHT_UPPER_LEG = Parem reis
body_part-RIGHT_LOWER_LEG = Parem säär
body_part-RIGHT_FOOT = Parem jalg
body_part-UPPER_CHEST = Rinna ülaosa
body_part-HIP = Puus
body_part-LEFT_SHOULDER = Vasak õlg
body_part-LEFT_UPPER_ARM = Vasak õlavars
body_part-LEFT_LOWER_ARM = Vasak küünarvars
body_part-LEFT_HAND = Vasak käsi
body_part-LEFT_UPPER_LEG = Vasak reis
body_part-LEFT_LOWER_LEG = Vasak säär
body_part-LEFT_FOOT = Vasak jalg

## BoardType

board_type-UNKNOWN = Tundmatu

## Proportions

skeleton_bone-NONE = Mitte midagi
skeleton_bone-HEAD = Pea Nihe
skeleton_bone-NECK = Kaela Pikkus
skeleton_bone-torso_group = Torso pikkus
skeleton_bone-UPPER_CHEST = Rinna ülaosa pikkus
skeleton_bone-HIP = Puusa pikkus
skeleton_bone-HIPS_WIDTH = Puusa Laius
skeleton_bone-leg_group = Jala pikkus
skeleton_bone-UPPER_LEG = Jala ülaosa pikkus
skeleton_bone-FOOT_LENGTH = Jala Pikkus
skeleton_bone-FOOT_SHIFT = Jala Nihe
skeleton_bone-SHOULDERS_DISTANCE = Õlgade Kaugus
skeleton_bone-SHOULDERS_WIDTH = Õlgade Laius
skeleton_bone-arm_group = Käe pikkus
skeleton_bone-UPPER_ARM = Õlavarre Pikkus
skeleton_bone-LOWER_ARM = Käsivarre Kaugus
skeleton_bone-HAND_Y = Käte kaugus Y
skeleton_bone-HAND_Z = Käte kaugus Z

## Tracker reset buttons

reset-reset_all = Lähtesta kõik proportsioonid
reset-reset_all_warning-cancel = Tühista
reset-full = Lähtesta
reset-mounting = Paigalduse lähtestamine
reset-yaw = Lähtesta lengerdus

## Navigation bar

navbar-home = Kodu
navbar-body_proportions = Keha Proportsioonid
navbar-trackers_assign = Jälgija Määramine
navbar-mounting = Paigalduse lähtestamine
navbar-onboarding = Häälestusviisard
navbar-settings = Seaded

## Biovision hierarchy recording

bvh-start_recording = Salvesta BVH
bvh-recording = Salvestamine...

## Tracking pause

tracking-unpaused = Peata jälgimine
tracking-paused = Jätka jälgimine

## Widget: Developer settings

widget-developer_mode = Arendaja režiim
widget-developer_mode-high_contrast = Kõrge kontrastsus
widget-developer_mode-precise_rotation = Täpne pööre
widget-developer_mode-fast_data_feed = Kiire andmevoog
widget-developer_mode-raw_slime_rotation = Toores

## Widget: IMU Visualizer

widget-imu_visualizer = Rotatsiooni
widget-imu_visualizer-rotation_raw = Toores
widget-imu_visualizer-rotation_preview = Eelvaade

## Tracker status

tracker-status-none = Staatuseta
tracker-status-busy = Hõivatud
tracker-status-error = Viga
tracker-status-disconnected = Ühendus katkestatud
tracker-status-occluded = Jälgija kadunud

## Tracker status columns

tracker-table-column-name = Nimi
tracker-table-column-type = Tüüp
tracker-table-column-battery = Patarei
tracker-table-column-rotation = Pööre X/Y/Z
tracker-table-column-position = Positsioon X/Y/Z

## Tracker rotation

tracker-rotation-front = Ees
tracker-rotation-front_left = Ees vasakul
tracker-rotation-front_right = Ees paremal
tracker-rotation-left = Vasak
tracker-rotation-right = Parem
tracker-rotation-back = Taga
tracker-rotation-back_left = Taga vasakul
tracker-rotation-back_right = Taga paremal
tracker-rotation-custom = Kohandatud

## Tracker information

tracker-infos-manufacturer = Tootja
tracker-infos-display_name = Kuvatav Nimi
tracker-infos-custom_name = Kohandatud Nimi
tracker-infos-url = Jälgija URL
tracker-infos-hardware_identifier = Riistvara ID
tracker-infos-imu = IMU sensor
tracker-infos-board_type = Põhiplaat

## Tracker settings

tracker-settings-back = Minge tagasi jälgija loendise
tracker-settings-title = Jälgija Seaded
tracker-settings-assignment_section = Jälgija asukoha määramine
tracker-settings-assignment_section-description = Mis kehaosale jälgija määratud on.
tracker-settings-assignment_section-edit = Muuda jälgija asukohta
tracker-settings-mounting_section = Paigaldusasend
tracker-settings-mounting_section-description = Kuhu on jälgija paigaldatud.
tracker-settings-mounting_section-edit = Muuda paigaldusasendit
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Jälgija Nimi
tracker-settings-name_section-placeholder = NightyBeast-i vasak käsi
tracker-settings-name_section-label = Jälgija Nimi

## Dongle settings

dongle-infos-hardware_revision = Riistvara revisjon
dongle-status-disconnected = Ühendus katkestatud
dongle-settings-back = Minge tagasi jälgija loendise

## Tracker part card info

tracker-part_card-unassigned = Määramata

## Body assignment menu

body_assignment_menu = Kus te soovite, et see jälgija paikneks?
body_assignment_menu-description = Vali asukoht kuhu te soovite, et jälgija määratakse. Alternatiivina saate te hallata kõiki jälgijaid korraga mitte ükshaaval.
body_assignment_menu-manage_trackers = Halda kõiki jälgijaid
body_assignment_menu-unassign_tracker = Tühista jälgija määramine

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Hoiatus:</b> Kaelajälgija võib olla surmav, kui seda liiga tihedalt reguleerida,
    rihm võib vere ringluse pähe lõpetada!
tracker_selection_menu-neck_warning-done = Ma mõistan riske
tracker_selection_menu-neck_warning-cancel = Tühista

## Mounting menu

mounting_selection_menu-close = Sulge

## Sidebar settings

settings-sidebar-title = Seaded
settings-sidebar-general = Tavaline
settings-sidebar-trackers = Jälgia
settings-sidebar-interface = Liides
settings-sidebar-utils = Olemus / Lisad
settings-sidebar-appearance = Välimus
settings-sidebar-notifications = Teavitused

## Bone routing settings

settings-routing-hands-warning-cancel = Tühista

## SteamVR / Monado output settings

settings-driver-enable = Luba

## Tracker mechanics

settings-general-tracker_mechanics-filtering = Filtreerimine
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Vali filtreerimis tüüp enda jälgijatele.
    Ennustus ennustab liikumist, aga silestamine silestab liikumist.
settings-general-tracker_mechanics-filtering-type-none = Ei mingit filtreerimist
settings-general-tracker_mechanics-filtering-type-none-description = Kasutage pöörlemist nii nagu on. Ei tee mingit filtreerimist.
settings-general-tracker_mechanics-filtering-type-smoothing = Silestamine
settings-general-tracker_mechanics-filtering-type-smoothing-description = Teeb liigutused siledaks, aga lisab aega jälgija ja programmi vahel.
settings-general-tracker_mechanics-filtering-type-prediction = Ennustamine
settings-general-tracker_mechanics-filtering-type-prediction-description = Vähendab aega jälgija ja programmi vahel ja tee liigutused kiiremaks, aga võib lisada värinat.
settings-stay_aligned-general-label = Tavaline
settings-stay_aligned-relaxed_poses-close = Sulge

## Keybinds Page

settings-keybinds_full-reset = Lähtesta
settings-keybinds_yaw-reset = Lähtesta lengerdus
settings-keybinds-recorder-modal-cancel-button = Tühista

## FK/Tracking settings

# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Põranda läbimine
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Libisemise korrigeerimine
settings-general-fk_settings-leg_tweak-toe_snap = Varba klõpsatus maha
settings-general-fk_settings-leg_tweak-foot_plant = Jalg maas
settings-general-fk_settings-leg_tweak-skating_correction-amount = Libisemise korrigeerimine jõud
settings-general-fk_settings-leg_tweak-skating_correction-description = Uisukorrektsioon korrigeerib uisutamist, kuid võib vähendada teatud liikumismustrite täpsust. Selle lubamisel veenduge, et mängus lähtestatakse jälgimine ja kalibreeritakse jälgimine uuesti.
settings-general-fk_settings-leg_tweak-floor_clip-description = Põrandaklõpsatus võib vähendada või isegi välistada jalgade läbi põranda minemise. Selle lubamisel veenduge, et mängus lähtestatakse jälgimine ja kalibreeritakse jälgimine uuesti.
settings-general-fk_settings-leg_tweak-toe_snap-description = Varvaste klõpsatus maha üritab ära arvata jalgade pöörlemist, kui jalgade jälgijaid ei kasutata.
settings-general-fk_settings-leg_tweak-foot_plant-description = Jalg-maas pöörab jalad kokkupuutel maapinnaga paralleelseks.
settings-general-fk_settings-leg_fk = Jalgade jälgimine
settings-general-fk_settings-arm_fk-back = Taga
settings-general-fk_settings-skeleton_settings-ratios = Skeleti suhted
settings-general-fk_settings-skeleton_settings-ratios-description = Muutke skeleti seadete väärtusi. Võimalik, et peate pärast nende muutmist oma proportsioone kohandama.
settings-general-fk_settings-self_localization-title = Mocapi režiim

## Gesture control settings (tracker tapping)

settings-general-gesture_control-subtitle = Puudutusepõhised lähtestused
settings-general-gesture_control-description = Lubab lähtestada jälgija puudutades jälgijat. Jälgija kere kõige kõrgemal osal kasutatakse kiireks lähtestamiseks, jälgija vasaku jala kõige kõrgemal osal kasutatakse lähtestamiseks ja jälgija parema jala kõige kõrgemal osal kasutatakse paigalduse lähtestamiseks. Vajutused peavad toimuma 0.3 sekundi jooksul, et need registreeritaks.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
        [one] 1 puudutus
       *[other] { $amount } puudutusi
    }
# This is a unit: 3 trackers, 2 trackers, 1 tracker
# $amount (Number) - Amount of trackers
settings-general-gesture_control-trackers =
    { $amount ->
        [one] 1 jälgija
       *[other] { $amount } jälgijat
    }
settings-general-gesture_control-yawResetEnabled = Luba puudutamine lengerduse lähtestamiseks
settings-general-gesture_control-yawResetDelay = Lengerduse lähtestamise viivitus
settings-general-gesture_control-yawResetTaps = Puudutust lengerduse lähtestamiseks
settings-general-gesture_control-fullResetEnabled = Luba puudutus täielikuks lähtestamiseks
settings-general-gesture_control-fullResetDelay = Tavalise lähtestamise viivitus
settings-general-gesture_control-fullResetTaps = Puudutust tavaliseks lähtestamiseks
settings-general-gesture_control-mountingResetEnabled = Luba, et vajutus lähtestab paigalduseasendi
settings-general-gesture_control-mountingResetDelay = Paigaldusasendi lähtestamise viivitus
settings-general-gesture_control-mountingResetTaps = Paigaldusasendi lähtestamise vajutus
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = Jälgijad üle läve
settings-general-gesture_control-numberTrackersOverThreshold-description = Suurendage seda väärtust, kui puudutuse tuvastamine ei tööta. Ärge suurendage seda üle selle, mis on vajalik puudutuse tuvastuse toimimiseks, kuna see põhjustaks rohkem valepositiivseid tulemusi.

## Appearance settings

settings-interface-appearance = Välimus
settings-general-interface-dev_mode = Arendaja režiim
settings-general-interface-dev_mode-description = See režiim on kasulik, kui on vaja põhjalike andmeid või või suhelda ühendatud jälgijatega kõrgemal tasemel.
settings-general-interface-dev_mode-label = Arendaja režiim
settings-general-interface-lang = Vaikekeel
settings-general-interface-lang-description = Muutke vaikekeelt, mida soovite kasutada.
settings-general-interface-lang-placeholder = Vali keel, mida kasutada
settings-interface-appearance-font-description = See muudab liidese kasutatavat fonti.
settings-interface-appearance-font-placeholder = Vaikimisi font
settings-interface-appearance-font-os_font = OS-i font
settings-interface-appearance-font-slime_font = Vaikimisi font
settings-interface-appearance-font_size = Fondi mastaapimise alus
settings-interface-appearance-font_size-description = See mõjutab kogu liidese fondi suurust, välja arvatud see seadete paneel.

## Notification settings

settings-interface-notifications = Teavitused
settings-general-interface-feedback_sound = Tagasiside heli
settings-general-interface-feedback_sound-description = See suvand esitab lähtestamise käivitamisel heli.
settings-general-interface-feedback_sound-label = Tagasiside heli
settings-general-interface-feedback_sound-volume = Tagasiside helitugevus

## Behavior settings

settings-general-interface-dev_mode = Arendaja režiim
settings-general-interface-dev_mode-label = Arendaja režiim

## Serial settings

settings-serial-connection_lost = Ühendus jadakonsooliga kaotatud. Ühenduse taastamine...
settings-serial-reboot = Taaskäivitage
settings-serial-factory_reset = Tehaseseadete taastamine
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>Hoiatus:</b> See lähtestab jälgija tehaseseadetele.
    Mis tähendab et WI-FI ja kalibreerimis sätted <b>kustutatakse!</b>
settings-serial-factory_reset-warning-ok = Ma tean mida ma teen
settings-serial-factory_reset-warning-cancel = Tühista
settings-serial-serial_select = Valige jadaport
settings-serial-send_command-warning-ok = Ma tean mida ma teen
settings-serial-send_command-warning-cancel = Tühista

## OSC VRChat settings

settings-osc-vrchat-enable = Luba
settings-osc-vrchat-enable-description = Lülitage andmete sisestamine sisse/välja.
settings-osc-vrchat-enable-label = Luba
settings-osc-vrchat-network = Pordid
settings-osc-vrchat-network-port_in =
    .label = Port sisse
    .placeholder = Võrguport sisse (vaikimisi: 9001)
settings-osc-vrchat-network-port_out =
    .label = Port välja
    .placeholder = Võrguport välja (vaikimisi: 9000)
settings-osc-vrchat-network-address = Võrgu aadress
settings-osc-vrchat-network-address-placeholder = VRChat ip aadress

## VRChat OSC status

settings-osc-vrchat-status-tracking = Rotatsiooni
settings-osc-vrchat-status-badge-error = Viga
settings-osc-vrchat-status-badge-unknown = Tundmatu

## VMC OSC settings

# This cares about multilines
settings-osc-vmc-description =
    VMC (Virtual Motion Capture) protokollile omaste sätete muutmine
        SlimeVR-i luuandmete saatmiseks ja luuandmete vastuvõtmiseks teistest rakendustest.
settings-osc-vmc-enable = Luba
settings-osc-vmc-enable-description = Lülitage andmete sisestamine sisse/välja.
settings-osc-vmc-enable-label = Luba
settings-osc-vmc-network = Pordid
settings-osc-vmc-network-description = Määrake pordid VMC kaudu andmete kuulamiseks ja saatmiseks.
settings-osc-vmc-network-port_in =
    .label = Port sisse
    .placeholder = Port sisse (vaikimisi: 39540)
settings-osc-vmc-network-port_out =
    .label = Port välja
    .placeholder = Port välja (vaikimisi 39539)
settings-osc-vmc-network-address = Võrgu aadress
settings-osc-vmc-network-address-description = Valige, millisel aadressil soovite VMC kaudu andmeid saata.
settings-osc-vmc-network-address-placeholder = IPV4 aadress
settings-osc-vmc-vrm = VRM-mudel
settings-osc-vmc-vrm-file_select = Kasutatava mudeli pukseerimine või <u>sirvimine</u>
settings-osc-vmc-anchor_hip = Ankurda puusadel
settings-osc-vmc-anchor_hip-label = Ankurda puusadel
settings-osc-vmc-status-badge-error = Viga

## Common OSC settings


## Advanced settings

settings-utils-advanced-reset_warning-cancel = Tühista

## Home Screen


## Tracking Checklist


## Setup/onboarding menu

onboarding-skip = Jäta seadistamine vahele
onboarding-continue = Jätka
onboarding-previous_step = Eelmine Samm
onboarding-setup_warning =
    <b>Hoiatus:</b> Hea jälgimise jaoks on vajalik esialgne seadistamine,
    see on vajalik, kui kasutate SlimeVR-i esimest korda.
onboarding-setup_warning-skip = Jäta seadistamine vahele
onboarding-setup_warning-cancel = Jätka seadistamist

## Quiz

onboarding-quiz_continue = Jätka
onboarding-quiz_back = Taga

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

