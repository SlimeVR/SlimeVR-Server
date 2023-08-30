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
tips-tap_setup = Saate jälgija valimiseks menüüst valimise asemel aeglaselt oma jälgijat 2 korda puudutada.

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
body_part-CHEST = Rind
body_part-WAIST = Vöökoht
body_part-HIP = Puus
body_part-LEFT_SHOULDER = Vasak õlg
body_part-LEFT_UPPER_ARM = Vasak õlavars
body_part-LEFT_LOWER_ARM = Vasak küünarvars
body_part-LEFT_HAND = Vasak käsi
body_part-LEFT_UPPER_LEG = Vasak reis
body_part-LEFT_LOWER_LEG = Vasak säär
body_part-LEFT_FOOT = Vasak jalg

## Proportions

skeleton_bone-NONE = Mitte midagi
skeleton_bone-HEAD = Pea Nihe
skeleton_bone-NECK = Kaela Pikkus
skeleton_bone-torso_group = Torso pikkus
skeleton_bone-UPPER_CHEST = Rinna ülaosa pikkus
skeleton_bone-CHEST_OFFSET = Rindkere nihe
skeleton_bone-CHEST = Rinna pikkus
skeleton_bone-WAIST = Vööümbermõõt
skeleton_bone-HIP = Puusa pikkus
skeleton_bone-HIP_OFFSET = Puusa Nihe
skeleton_bone-HIPS_WIDTH = Puusa Laius
skeleton_bone-leg_group = Jala pikkus
skeleton_bone-UPPER_LEG = Jala ülaosa pikkus
skeleton_bone-LOWER_LEG = Lower Leg Length
skeleton_bone-FOOT_LENGTH = Jala Pikkus
skeleton_bone-FOOT_SHIFT = Jala Nihe
skeleton_bone-SKELETON_OFFSET = Skeletti Nihe
skeleton_bone-SHOULDERS_DISTANCE = Õlgade Kaugus
skeleton_bone-SHOULDERS_WIDTH = Õlgade Laius
skeleton_bone-arm_group = Käe pikkus
skeleton_bone-UPPER_ARM = Õlavarre Pikkus
skeleton_bone-LOWER_ARM = Käsivarre Kaugus
skeleton_bone-HAND_Y = Käte kaugus Y
skeleton_bone-HAND_Z = Käte kaugus Z
skeleton_bone-ELBOW_OFFSET = Küünarnuki Nihe

## Tracker reset buttons

reset-reset_all = Lähtesta kõik proportsioonid
reset-full = Lähtesta
reset-mounting = Lähtesta Paigaldusasend
reset-yaw = Lähtesta lengerdus

## Serial detection stuff

serial_detection-new_device-p0 = Uus jadaseade tuvastatud!
serial_detection-new_device-p1 = Sisestage enda Wi-Fi andmed!
serial_detection-new_device-p2 = Palun valige, mida te soovite sellega teha
serial_detection-open_wifi = Ühendage Wi-Fi-ga
serial_detection-open_serial = Avage Jadakonsool
serial_detection-submit = Jätka!
serial_detection-close = Sulge

## Navigation bar

navbar-home = Kodu
navbar-body_proportions = Keha Proportsioonid
navbar-trackers_assign = Jälgija Määramine
navbar-mounting = Jälgijate Paigalduse Kalibreerimine
navbar-onboarding = Häälestusviisard
navbar-settings = Seaded

## Biovision hierarchy recording

bvh-start_recording = Salvesta BVH
bvh-recording = Salvestamine...

## Tracking pause

tracking-unpaused = Peata jälgimine
tracking-paused = Jätka jälgimine

## Widget: Overlay settings

widget-overlay = Ülekate
widget-overlay-is_visible_label = Näita Ülekatet SteamVR-is
widget-overlay-is_mirrored_label = Näita Ülekatet Peeglina

## Widget: Drift compensation

widget-drift_compensation-clear = Selgem triivi kompenseerimine

## Widget: Clear Reset Mounting

widget-clear_mounting = Lähtesta paigaldusasend

## Widget: Developer settings

widget-developer_mode = Arendaja režiim
widget-developer_mode-high_contrast = Kõrge kontrastsus
widget-developer_mode-precise_rotation = Täpne pööre
widget-developer_mode-fast_data_feed = Kiire andmevoog
widget-developer_mode-filter_slimes_and_hmd = Filtreerige Slimed ja HMD-d
widget-developer_mode-sort_by_name = Sorteeri nime järgi
widget-developer_mode-raw_slime_rotation = Toores pööre
widget-developer_mode-more_info = Rohkem infot

## Widget: IMU Visualizer

widget-imu_visualizer = Rotatsiooni
widget-imu_visualizer-rotation_raw = Toores
widget-imu_visualizer-rotation_preview = Eelvaade
widget-imu_visualizer-rotation_hide = Peida

## Tracker status

tracker-status-none = Staatuseta
tracker-status-busy = Hõivatud
tracker-status-error = Viga
tracker-status-disconnected = Ühendus katkestatud
tracker-status-occluded = Jälgija kadunud
tracker-status-ok = OK

## Tracker status columns

tracker-table-column-name = Nimi
tracker-table-column-type = Tüüp
tracker-table-column-battery = Patarei
tracker-table-column-ping = Ping
tracker-table-column-tps = TPS
tracker-table-column-temperature = Temp. °C
tracker-table-column-linear-acceleration = Accel. X/Y/Z
tracker-table-column-rotation = Pööre X/Y/Z
tracker-table-column-position = Positsioon X/Y/Z
tracker-table-column-url = URL

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
tracker-rotation-overriden = (tühistatud paigalduse lähtestamine tõttu)

## Tracker information

tracker-infos-manufacturer = Tootja
tracker-infos-display_name = Kuvatav Nimi
tracker-infos-custom_name = Kohandatud Nimi
tracker-infos-url = Jälgija URL
tracker-infos-version = Püsivara versioon
tracker-infos-hardware_rev = Riistvara revisjon
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
tracker-settings-drift_compensation_section = Allow drift compensation
tracker-settings-drift_compensation_section-description = Should this tracker compensate for its drift when drift compensation is enabled?
tracker-settings-drift_compensation_section-edit = Allow drift compensation
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Jälgija Nimi
tracker-settings-name_section-description = Anna talle armas hüüdnimi :)
tracker-settings-name_section-placeholder = NightyBeast-i vasak käsi

## Tracker part card info

tracker-part_card-no_name = Nimi puudub
tracker-part_card-unassigned = Määramata

## Body assignment menu

body_assignment_menu = Kus te soovite, et jälgija oleks?
body_assignment_menu-description = Vali asukoht kuhu te soovite, et jälgija määratakse. Alternatiivina saate te hallata kõiki jälgijaid korraga mitte ükshaaval.
body_assignment_menu-show_advanced_locations = Kuva täpsem määramise asukoht
body_assignment_menu-manage_trackers = Halda kõiki jälgijaid
body_assignment_menu-unassign_tracker = Tühista jälgija määramine

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = Millist jälgijat määrata teie
tracker_selection_menu-NONE = Millise jälgija määramist soovite tühistada?
tracker_selection_menu-HEAD = { -tracker_selection-part } peale?
tracker_selection_menu-NECK = { -tracker_selection-part } kaelale?
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part } paremale õlale?
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part } paremale õlavarrele?
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part } paremale küünarvarrele?
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part } paremale käele?
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part } paremale reiele?
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part } paremale säärele?
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part } paremale jalale?
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part } paremale kontrollerile?
tracker_selection_menu-UPPER_CHEST = { -tracker_selection-part } rinnale?
tracker_selection_menu-CHEST = { -tracker_selection-part } rind?
tracker_selection_menu-WAIST = { -tracker_selection-part } vöökoht?
tracker_selection_menu-HIP = { -tracker_selection-part } puus?
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part } vasakule õlale?
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part } vasakule õlavarrele?
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part } vasakule küünarvarrele_
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part } vasakule käele?
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part } vasakule reiele?
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part } vasakule säärele?
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part } vasakule jalale?
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part } vasakule kontrollerile?
tracker_selection_menu-unassigned = Määramata Jälgijad
tracker_selection_menu-assigned = Määratud Jälgijad
tracker_selection_menu-dont_assign = Ära määra jälgijat
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Hoiatus:</b> Kaelajälgija võib olla surmav, kui seda liiga tihedalt reguleerida,
    rihm võib vere ringluse pähe lõpetada!
tracker_selection_menu-neck_warning-done = Ma mõistan riske
tracker_selection_menu-neck_warning-cancel = Tühista

## Mounting menu

mounting_selection_menu = Kus te soovite, et see jälgija paikneks?
mounting_selection_menu-close = Sulge

## Sidebar settings

settings-sidebar-title = Seaded
settings-sidebar-general = Tavaline
settings-sidebar-tracker_mechanics = Jälgija mehaanika
settings-sidebar-fk_settings = FK seaded
settings-sidebar-gesture_control = Žesti juhtimine
settings-sidebar-interface = Liides
settings-sidebar-osc_router = OSC ruuter
settings-sidebar-osc_trackers = VRChati OSC Jälgija
settings-sidebar-utils = Olemus / Lisad
settings-sidebar-serial = Jadakonsool
settings-sidebar-appearance = Välimus
settings-sidebar-notifications = Teavitused

## SteamVR settings

settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = SteamVR jälgijad
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    Luba või keela spetsiifilised SteamVR-i jälgijad.
    Kasulik teatud mängudele või äppidele, mis toetavad ainult teatuid jälgijaid.
settings-general-steamvr-trackers-waist = Vöökoht
settings-general-steamvr-trackers-chest = Rind
settings-general-steamvr-trackers-feet = Jalad
settings-general-steamvr-trackers-knees = Põlved
settings-general-steamvr-trackers-elbows = Küünarnukid
settings-general-steamvr-trackers-hands = Käed

## Tracker mechanics

settings-general-tracker_mechanics = Jälgija mehaanika
settings-general-tracker_mechanics-filtering = Filtreerimine
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Vali filtreerimis tüüp enda jälgijatele.
    Ennustus ennustab liikumist, aga silestamine silestab liikumist.
settings-general-tracker_mechanics-filtering-type = Filtreerimise tüüp
settings-general-tracker_mechanics-filtering-type-none = Ei mingit filtreerimist
settings-general-tracker_mechanics-filtering-type-none-description = Kasutage pöörlemist nii nagu on. Ei tee mingit filtreerimist.
settings-general-tracker_mechanics-filtering-type-smoothing = Silestamine
settings-general-tracker_mechanics-filtering-type-smoothing-description = Teeb liigutused siledaks, aga lisab aega jälgija ja programmi vahel.
settings-general-tracker_mechanics-filtering-type-prediction = Ennustamine
settings-general-tracker_mechanics-filtering-type-prediction-description = Vähendab aega jälgija ja programmi vahel ja tee liigutused kiiremaks, aga võib lisada värinat.
settings-general-tracker_mechanics-filtering-amount = Amount
settings-general-tracker_mechanics-drift_compensation = Drift compensation
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Compensates IMU yaw drift by applying an inverse rotation.
    Change amount of compensation and up to how many resets are taken into account.
settings-general-tracker_mechanics-drift_compensation-enabled-label = Drift compensation
settings-general-tracker_mechanics-drift_compensation-amount-label = Compensation amount
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Use up to x last resets

## FK/Tracking settings

settings-general-fk_settings = Jälgija seaded
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
settings-general-fk_settings-arm_fk = Käe jälgimine
settings-general-fk_settings-arm_fk-description = Muuda viisi kuidas käsi jälgitakse.
settings-general-fk_settings-arm_fk-force_arms = Sunni käed HMD-st
settings-general-fk_settings-skeleton_settings-toggles = Skeleti lülitid
settings-general-fk_settings-skeleton_settings-description = Lülita skeletti seaded sisse või välja. Soovitatud on see sisse jätta.
settings-general-fk_settings-skeleton_settings-extended_spine_model = Laiendatud selgroo mudel
settings-general-fk_settings-skeleton_settings-extended_pelvis_model = Laiendatud vaagna mudel
settings-general-fk_settings-skeleton_settings-extended_knees_model = Laiendatud põlve mudel
settings-general-fk_settings-skeleton_settings-ratios = Skeleti suhted
settings-general-fk_settings-skeleton_settings-ratios-description = Muutke skeleti seadete väärtusi. Võimalik, et peate pärast nende muutmist oma proportsioone kohandama.
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_hip = Imputeeri vöökoht rinnast ja puusast
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_legs = Imputeeri vöökoht rinnast ja jalgadest
settings-general-fk_settings-skeleton_settings-impute_hip_from_chest_legs = Imputeeri puusakoht rinnast ja jalgadest
settings-general-fk_settings-skeleton_settings-impute_hip_from_waist_legs = Imputeeri puusakoht vöökohast ja jalgadest
settings-general-fk_settings-skeleton_settings-interp_hip_legs = Leia keskmine puusa lengerdus ja pöörlemine jalgade abiga
settings-general-fk_settings-skeleton_settings-interp_knee_tracker_ankle = Leia keskmine põlvede lengerdus ja pöörlemine säärte abiga
settings-general-fk_settings-self_localization-title = Mocapi režiim
settings-general-fk_settings-self_localization-description = Mocap-režiim võimaldab skeletil ligikaudselt jälgida oma asukohta ilma peakomplekti või muude jälgijateta. Pange tähele, et see nõuab jalgade ja peajälgijate olemasolu ning on endiselt eksperimentaalne.
settings-general-fk_settings-vive_emulation-title = Vive-i emulatsioon
settings-general-fk_settings-vive_emulation-description = Emuleeri vöökoha jälgija probleeme mis Vive jälgijatel on. See on nali ja teeb jälgijate täpsuse halvaks.
settings-general-fk_settings-vive_emulation-label = Luba Vive-i emulatsioon

## Gesture control settings (tracker tapping)

settings-general-gesture_control = Žesti juhtimine
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
settings-general-interface-theme = Värviteema
settings-general-interface-lang = Vaikekeel
settings-general-interface-lang-description = Muutke vaikekeelt, mida soovite kasutada.
settings-general-interface-lang-placeholder = Vali keel, mida kasutada
# Keep the font name untranslated
settings-interface-appearance-font = GUI font
settings-interface-appearance-font-description = See muudab liidese kasutatavat fonti.
settings-interface-appearance-font-placeholder = Vaikimisi font
settings-interface-appearance-font-os_font = OS-i font
settings-interface-appearance-font-slime_font = Vaikimisi font
settings-interface-appearance-font_size = Fondi mastaapimise alus
settings-interface-appearance-font_size-description = See mõjutab kogu liidese fondi suurust, välja arvatud see seadete paneel.

## Notification settings

settings-interface-notifications = Teavitused
settings-general-interface-serial_detection = Jadaseadme märkamine
settings-general-interface-serial_detection-description = See valik näitab hüpikakent iga kord kui panna sisse uus jada seade, mis võib olla jälgija. See aitab jälgija seadistusprotsessi parandada.
settings-general-interface-serial_detection-label = Jadaseadme märkamine
settings-general-interface-feedback_sound = Tagasiside heli
settings-general-interface-feedback_sound-description = See suvand esitab lähtestamise käivitamisel heli.
settings-general-interface-feedback_sound-label = Tagasiside heli
settings-general-interface-feedback_sound-volume = Tagasiside helitugevus

## Serial settings

settings-serial = Jadakonsool
# This cares about multilines
settings-serial-description =
    See on reaalajas teabevoog jadaside jaoks.
    Võib olla kasulik, kui on vaja teada, kas püsivara töötab.
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
settings-serial-get_infos = Saa infot
settings-serial-serial_select = Valige jadaport
settings-serial-auto_dropdown_item = Auto

## OSC router settings

settings-osc-router = OSC ruuter
# This cares about multilines
settings-osc-router-description =
    Edasta OSC-teated teisest programmidest.
    Kasulik, kui kasutada teist OSC programmi näiteks VRChat-iga.
settings-osc-router-enable = Luba
settings-osc-router-enable-description = Lülitage andmete sisestamine sisse/välja.
settings-osc-router-enable-label = Luba
settings-osc-router-network = Võrgupordid
# This cares about multilines
settings-osc-router-network-description =
    Lisage võrgupordid, mille pealt saata ja kuulata andmeid.
    Need võivad olla samad võrgupordid mida kasutab SlimeVR server.
settings-osc-router-network-port_in =
    .label = Võrguport sisse
    .placeholder = Võrguport sisse (vaikimisi: 9002)
settings-osc-router-network-port_out =
    .label = Võrguport välja
    .placeholder = Võrguport välja (vaikimisi: 9000)
settings-osc-router-network-address = Võrgu aadress
settings-osc-router-network-address-description = Lisage võrgu aadress kuhu saata andmeid.
settings-osc-router-network-address-placeholder = IPV4 aadress

## OSC VRChat settings

settings-osc-vrchat = VRChat OSC Jälgija
# This cares about multilines
settings-osc-vrchat-description =
    Muuda VRChat-i spetsiifiliseid seadeid, et saada ja saata HMD andmeid.
    Jälgijate andmed FBT jaoks (töötab Questi peal ilma arvuti ühenduseta).
settings-osc-vrchat-enable = Luba
settings-osc-vrchat-enable-description = Lülitage andmete sisestamine sisse/välja.
settings-osc-vrchat-enable-label = Luba
settings-osc-vrchat-network = Võrgupordid
settings-osc-vrchat-network-description = Lisage võrgupordid andmete saamiseks ja saatmiseks VRChat-i.
settings-osc-vrchat-network-port_in =
    .label = Võrguport sisse
    .placeholder = Võrguport sisse (vaikimisi: 9001)
settings-osc-vrchat-network-port_out =
    .label = Võrguport välja
    .placeholder = Võrguport välja (vaikimisi: 9000)
settings-osc-vrchat-network-address = Võrgu aadress
settings-osc-vrchat-network-address-description = Vali, mis aadressile saata andmeid VRChat-i jaoks (kontrolli enda Wi-Fi seadeid seadmest).
settings-osc-vrchat-network-address-placeholder = VRChat ip aadress
settings-osc-vrchat-network-trackers = Jälgia
settings-osc-vrchat-network-trackers-description = Lülita sisse/välja teatud jälgijate andmete saatmise OSC kaudu.
settings-osc-vrchat-network-trackers-chest = Rind
settings-osc-vrchat-network-trackers-hip = Puus
settings-osc-vrchat-network-trackers-knees = Põlved
settings-osc-vrchat-network-trackers-feet = Jalad
settings-osc-vrchat-network-trackers-elbows = Küünarnukid

## VMC OSC settings

settings-osc-vmc = Virtual Motion Capture
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
settings-osc-vmc-vrm-description = Laadige VRM-mudel, et võimaldada peaankurdamist ja suuremat ühilduvust teiste rakendustega.
settings-osc-vmc-vrm-model_unloaded = Mudelit pole laaditud
settings-osc-vmc-vrm-model_loaded =
    { $titled ->
        [true] Mudel laaditud: { $name }
       *[other] Pealkirjata mudel on laaditud
    }
settings-osc-vmc-vrm-file_select = Kasutatava mudeli pukseerimine või <u>sirvimine</u>
settings-osc-vmc-anchor_hip = Ankurda puusadel
settings-osc-vmc-anchor_hip-description = Ankurdage jälgimine puusadele, mis on kasulik istuva VTubingu jaoks. Keelamise korral laadige VRM-mudel.
settings-osc-vmc-anchor_hip-label = Ankurda puusadel

## Setup/onboarding menu

onboarding-skip = Jäta seadistamine vahele
onboarding-continue = Jätka
onboarding-wip = Töö käib
onboarding-previous_step = Eelmine samm
onboarding-setup_warning =
    <b>Hoiatus:</b> Hea jälgimise jaoks on vajalik esialgne seadistamine,
    see on vajalik, kui kasutate SlimeVR-i esimest korda.
onboarding-setup_warning-skip = Jäta seadistamine vahele
onboarding-setup_warning-cancel = Jätka seadistamist

## Wi-Fi setup

onboarding-wifi_creds-back = Mine tagasi juhistele
onboarding-wifi_creds = Sisestage enda Wi-Fi andmed!
# This cares about multilines
onboarding-wifi_creds-description =
    Jälgijad kasutavad neid andmeid, et ühendada juhtmevabalt.
    Palun kasutage neid Wi-Fi andmeid, millega te praegu olete ühendatud.
onboarding-wifi_creds-skip = Jätke Wi-Fi seaded vahele.
onboarding-wifi_creds-submit = Jätka!
onboarding-wifi_creds-ssid =
    .label = SSID
    .placeholder = Sisesta SSID
onboarding-wifi_creds-password =
    .label = Parool
    .placeholder = Sisesta parool

## Mounting setup

onboarding-reset_tutorial-back = Minge tagasi paigaldus kalibreerimisse
onboarding-reset_tutorial = Lähtesta õpetus
onboarding-reset_tutorial-explanation = Kui kasutate oma jälgijaid, võivad need IMU lengerdamise tõttu joondusest välja tulla või kuna olete neid füüsiliselt liigutanud. Teil on selle parandamiseks mitu võimalust.
onboarding-reset_tutorial-skip = Jäta samm vahele
# Cares about multiline
onboarding-reset_tutorial-0 =
    Puudutage { $taps } korda esiletõstetud jälgijat, et käivitada lengerduse lähtestamine.
    
    See setib jälgijad teie HMD-ga samas suunas.
# Cares about multiline
onboarding-reset_tutorial-1 =
    Täieliku lähtestamise käivitamiseks puudutage esiletõstetud jälgijat { $taps } korda.
    
    Sa pead seisma (i-poosis). Enne kui see juhtub, on 3-sekundiline viivitus (konfigureeritav).
    See lähtestab täielikult kõigi teie jälgijate asukoha ja pöörlemise. See peaks lahendama enamiku probleeme.
# Cares about multiline
onboarding-reset_tutorial-2 =
    Puudutage { $taps } korda esiletõstetud jälgijat, et lähtestada paigaldus.
    
    Paigaldamise lähtestamine aitab kaasa sellele, kuidas jälgijad teile tegelikult pannakse, nii et kui te neid kogemata liigutasite ja muutsite nende orientatsiooni suure summa võrra, aitab see.
    
    Peate olema poosis, nagu suusatate, nagu see on näidatud automaatse paigaldamise viisardil, ja teil on 3-sekundiline viivitus (konfigureeritav), enne kui see käivitub.

## Setup start

onboarding-home = Tere tulemast SlimeVR-i
onboarding-home-start = Hakkame sättima!

## Enter VR part of setup

onboarding-enter_vr-back = Minge tagasi jälgijate määramisse
onboarding-enter_vr-title = Aeg minna VR-i!
onboarding-enter_vr-description = Pange selga kõik jälgijad ja VR prillid.
onboarding-enter_vr-ready = Olen valmis

## Setup done

onboarding-done-title = Kõik on valmis!
onboarding-done-description = Nautige enda kogu keha jälgimis kogemust
onboarding-done-close = Sulgege juhend

## Tracker connection setup

onboarding-connect_tracker-back = Minge tagasi Wi-Fi andmetesse
onboarding-connect_tracker-title = Ühendage jälgijad
onboarding-connect_tracker-description-p0 = Nüüd lähme lõbusa osa juurde, ühendame kõik jälgijad-
onboarding-connect_tracker-description-p1 = Lihtsalt ühendage kõik jälgijad, mis ei ole ühendatud läbi USB enda arvutisse.
onboarding-connect_tracker-issue-serial = Mul on probleeme ühenduse loomisega!
onboarding-connect_tracker-usb = USB Jälgija
onboarding-connect_tracker-connection_status-none = Jälgijate otsimine
onboarding-connect_tracker-connection_status-serial_init = Ühenduse loomine jadaseadmega
onboarding-connect_tracker-connection_status-provisioning = Saadame Wi-Fi andmeid
onboarding-connect_tracker-connection_status-connecting = Saadame Wi-Fi andmeid
onboarding-connect_tracker-connection_status-looking_for_server = Serveri otsimine
onboarding-connect_tracker-connection_status-connection_error = Wi-Fi-ga ei saa ühendust luua!
onboarding-connect_tracker-connection_status-could_not_find_server = Serverit ei leitud
onboarding-connect_tracker-connection_status-done = Ühendatud serveriga
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers =
    { $amount ->
        [0] Mitte ühtegi jälgijat ühendatud
        [one] 1 jälgija
       *[other] { $amount } jälgijat
    } connected
onboarding-connect_tracker-next = Olen ühendanud kõik oma jälgijad

## Tracker calibration tutorial

onboarding-calibration_tutorial = IMU kalibreerimise õpetus
onboarding-calibration_tutorial-subtitle = See aitab vähendada jälgija driftimist!
onboarding-calibration_tutorial-description = Iga kord, kui lülitate oma jälgijad sisse, peavad nad kalibreerimiseks hetkeks tasasel pinnal olema. Teeme sama, klõpsates nuppu "{ onboarding-calibration_tutorial-calibrate }", <b>ärge liigutage neid!</b>
onboarding-calibration_tutorial-calibrate = Panin oma jälgijad lauale
onboarding-calibration_tutorial-status-waiting = Ootan sind
onboarding-calibration_tutorial-status-calibrating = Kalibreerimine
onboarding-calibration_tutorial-status-success = Võimas!
onboarding-calibration_tutorial-status-error = Jälgija liigutati

## Tracker assignment tutorial

onboarding-assignment_tutorial = Kuidas valmistada Slime Trackerit enne selle külge panemist
onboarding-assignment_tutorial-first_step = 1. Asetage kehaosa kleebis (kui teil see on) jälgijale vastavalt oma valikule
# This text has a character limit of around 11 characters, so please keep it short
onboarding-assignment_tutorial-sticker = Kleebis
onboarding-assignment_tutorial-second_step-v2 = 2. Kinnitage rihm oma jälgija külge, hoides rihma takjakülge jälgimisseadme pealmise poolega samas suunas:
onboarding-assignment_tutorial-second_step-continuation-v2 = Extensioni takjapaela pool peaks olema ülespoole suunatud nagu järgmine pilt:
onboarding-assignment_tutorial-done = Panin kleepsud ja rihmad külge!

## Tracker assignment setup

onboarding-assign_trackers-back = Minge tagasi Wi-Fi andmetesse
onboarding-assign_trackers-title = Määrake jälgijad asukoht
onboarding-assign_trackers-description = Valime mis jälgijad lähevad kuhu. Vajutage asukohale kuhu te tahate, et jälgija läheks.
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned =
    { $assigned } of { $trackers ->
        [one] 1 jälgija
       *[other] { $trackers } jälgijat
    } assigned
onboarding-assign_trackers-advanced = Kuva täpsemad määramiskohad
onboarding-assign_trackers-next = Määrasin kõikide jälgijate asukohad

## Tracker assignment warnings


## Tracker mounting method choose

onboarding-choose_mounting-auto_mounting = Automaatne paigaldamine
# Italized text
onboarding-choose_mounting-auto_mounting-label = Eksperimentaalne
onboarding-choose_mounting-auto_mounting-description = See tuvastab automaatselt kõigi teie jälgijate paigaldussuuna 2 poosist
onboarding-choose_mounting-manual_mounting = Käsitsi paigaldamine
# Italized text
onboarding-choose_mounting-manual_mounting-label = Soovitatud
onboarding-choose_mounting-manual_mounting-description = See võimaldab teil valida iga jälgija paigaldussuuna käsitsi

## Tracker manual mounting setup

onboarding-manual_mounting-back = Minge tagasi, et siseneda VR-i
onboarding-manual_mounting = Käsitsi paigaldamine
onboarding-manual_mounting-description = Vajutage iga jälgija peale ja valige, kuidas see on paigaldatud
onboarding-manual_mounting-auto_mounting = Automaatne paigaldamine
onboarding-manual_mounting-next = Järgmine Samm

## Tracker automatic mounting setup

onboarding-automatic_mounting-back = Minge tagasi, et siseneda VR-i
onboarding-automatic_mounting-title = Paigaldamis kalibreerimine
onboarding-automatic_mounting-description = Et SlimeVR jälgijad töötaksid peame me nendele seadistama paigaldamise pöörde, et need joondada teie füüsilise jälgijate paigaldusega.
onboarding-automatic_mounting-manual_mounting = Manuaalselt seadistamine
onboarding-automatic_mounting-next = Järgmine Samm
onboarding-automatic_mounting-prev_step = Eelmine Samm
onboarding-automatic_mounting-done-title = Paigalduse pööre kalibreeritud.
onboarding-automatic_mounting-done-description = Teie paigalduse kalibreerimine on valmis!
onboarding-automatic_mounting-done-restart = Minge algusese
onboarding-automatic_mounting-mounting_reset-title = Paigalduse lähtestamine
onboarding-automatic_mounting-mounting_reset-step-0 = 1. Kükita suusaasendis, jalad kõverad, ülakeha kallutatud ettepoole ja käed kõverad.
onboarding-automatic_mounting-mounting_reset-step-1 = 2. Vajutage "Lähtesta Paigaldusasend" nuppu ja oodage 3 sekuntit ja jälgijate paigaldusasend lähtestatakse.
onboarding-automatic_mounting-preparation-title = Ettevalmistus
onboarding-automatic_mounting-preparation-step-0 = 1. Seiske püsti, käed kõrval.
onboarding-automatic_mounting-preparation-step-1 = 2. Vajutage "Lähtesta" nuppu ja oodage 3 sekundit ja jälgijad lähtestatakse.
onboarding-automatic_mounting-put_trackers_on-title = Pange kõik jälgijad peale
onboarding-automatic_mounting-put_trackers_on-description = Et kalibreerida jälgijate paigaldus asendi pööret pange kõik jälgijad peale ja nüüd te näete mis on mis jälgijad paremal pool ekraani.
onboarding-automatic_mounting-put_trackers_on-next = Mul on kõik jälgijad küljes

## Tracker proportions method choose

onboarding-choose_proportions = Millist proportsiooni kalibreerimismeetodit kasutada?
# Multiline string
onboarding-choose_proportions-description =
    Keha proportsioone kasutatakse teie keha mõõtude tundmiseks. Neid on vaja, et arvutada jälgijate asukohad.
    Kui teie keha proportsioonid ei vasta salvestatud proportsioonidele, on teie jälgimistäpsus halvem ja märkate selliseid asju nagu jalgade uisutamine või libistamine või keha ei sobi teie avatariga hästi.
onboarding-choose_proportions-auto_proportions = Automaatsed proportsioonid
# Italized text
onboarding-choose_proportions-auto_proportions-subtitle = Soovitatud
# Italized text
onboarding-choose_proportions-manual_proportions-subtitle = Väikeste puudutuste jaoks
onboarding-choose_proportions-manual_proportions-description = See võimaldab teil proportsioone käsitsi reguleerida, muutes neid otseselt
onboarding-choose_proportions-export = Ekspordi proportsioonid
onboarding-choose_proportions-file_type = Keha proportsioonide fail

## Tracker manual proportions setup

onboarding-manual_proportions-back = Mine tagasi lähtestamise õppetusse
onboarding-manual_proportions-title = Käsitsi keha proportsioonid
onboarding-manual_proportions-precision = Täpne reguleerimine
onboarding-manual_proportions-auto = Automaatne kalibreerimine
onboarding-manual_proportions-ratio = Kohandamine suhtarvugruppide järgi

## Tracker automatic proportions setup

onboarding-automatic_proportions-back = Mine tagasi lähtestamise õppetusse
onboarding-automatic_proportions-title = Mõõtke oma keha
onboarding-automatic_proportions-description = Et SlimeVR jälgijad töötaks peame me teadma teie kontide pikkust. See lühike kalibreerimine mõõdab selle teie jaoks.
onboarding-automatic_proportions-manual = Manuaalne Kalibreerimine
onboarding-automatic_proportions-prev_step = Eelmine Samm
onboarding-automatic_proportions-put_trackers_on-title = Pange kõik jälgijad peale
onboarding-automatic_proportions-put_trackers_on-description = Et kalibreerida teie proportsioone pange kõik jälgijad peale ja te näete mis on mis jälgijad paremal pool ekraani.
onboarding-automatic_proportions-put_trackers_on-next = Mul on kõik jälgijad küljes
onboarding-automatic_proportions-requirements-title = Nõuded
onboarding-automatic_proportions-requirements-next = Olen lugenud nõudeid
onboarding-automatic_proportions-check_height-title = Kontrollige oma pikkust
onboarding-automatic_proportions-check_height-description = Me kasutame teie pikkust oma mõõtmiste alusena, kasutades HMD kõrgust teie tegeliku kõrguse ligikaudseks arvutamiseks, kuid parem on ise kontrollida, kas need on õiged!
onboarding-automatic_proportions-check_height-fetch_height = Ma seisan!
# Context is that the height is unknown
onboarding-automatic_proportions-check_height-unknown = Tundmatu
# Shows an element below it
onboarding-automatic_proportions-check_height-hmd_height1 = Teie HMD kõrgus on
# Shows an element below it
onboarding-automatic_proportions-check_height-height1 = nii et teie tegelik kõrgus on
onboarding-automatic_proportions-check_height-next_step = Nendega on kõik korras
onboarding-automatic_proportions-start_recording-title = Olge valmis liikuma
onboarding-automatic_proportions-start_recording-description = Me nüüd salvestame teatud poose ja liigutusi neid näete järgmisel ekraanil. Olge valmis, kui te vajutate nuppu!
onboarding-automatic_proportions-start_recording-next = Alusta salvestamist
onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = Salvestamine on pooleli...
onboarding-automatic_proportions-recording-description-p1 = Tehke allpool näidatud liigutusi:
# Each line of text is a different list item
onboarding-automatic_proportions-recording-steps =
    Sirgelt püsti seistes pöörage pea ringi igas suunas.
    Painutage selg ettepoole ja kükitage. Kükitades vaadake vasakule, seejärel paremale.
    Keerake ülakeha vasakule (vastupäeva), seejärel sirutage alla maapinna poole.
    Keerake ülakeha paremale (päripäeva), seejärel sirutage alla maapinna poole.
    Pöörage puusi ringiratast, nagu kasutaksite hularõngast.
    Kui salvestusel on veel aega, korrake juhiseid, kuni aeg on läbi.
onboarding-automatic_proportions-recording-processing = Tulemuse töötlemine
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 20)
onboarding-automatic_proportions-recording-timer =
    { $time ->
        [one] 1 sekund
       *[other] { $time } sekundit
    }
onboarding-automatic_proportions-verify_results-title = Kontrollige tulemust
onboarding-automatic_proportions-verify_results-description = Kontrollige tulemust all, kas kõik näeb välja korrektne?
onboarding-automatic_proportions-verify_results-results = Salvestame tulemused
onboarding-automatic_proportions-verify_results-processing = Tulemuse töötlemine
onboarding-automatic_proportions-verify_results-redo = Tee salvestus uuesti
onboarding-automatic_proportions-verify_results-confirm = Nad on õiged
onboarding-automatic_proportions-done-title = Kere mõõdetud ja salvestatud.
onboarding-automatic_proportions-done-description = Teie keha proportsioonid kalibreerimine on valmis!
onboarding-automatic_proportions-error_modal-confirm = Sain aru!

## Home

home-no_trackers = Jälgijaid ei tuvastatud ega määratud

## Status system

status_system-StatusSteamVRDisconnected =
    { $type ->
        [steamvr_feeder] Praegu ei ole SlimeVR-feeder äpiga ühendatud.
       *[other] Praegu ei ole SlimeVR-draiveri kaudu SteamVR-iga ühendatud.
    }
status_system-StatusTrackerError = Jälgijal { $trackerName } on tõrge.
