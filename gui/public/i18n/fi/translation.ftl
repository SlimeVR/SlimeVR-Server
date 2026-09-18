# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Yhdistetään palvelimeen
websocket-connection_lost = Yhteys epäonnistui. Yritetään uudelleen...
websocket-connection_lost-desc = It looks like the SlimeVR server crashed. Check the logs and restart the program.
websocket-timedout = Could not connect to the server
websocket-timedout-desc = It looks like the SlimeVR server crashed or timed out. Check the logs and restart the program.
websocket-error-close = Exit SlimeVR
websocket-error-logs = Open the logs Folder

## Update notification

version_update-title = Uusi versio saatavilla: { $version }
version_update-description = Valitsemalla "{ version_update-update }" lataa SlimeVR-asennusohjelman.
version_update-update = Päivitys
version_update-close = Sulje

## Tips

tips-find_tracker = Epävarma, mikä jäljitin on mikä? Ravista jäljitintä ja se korostaa vastaavan kohdan.
tips-do_not_move_heels = Varmista, että kantapääsi ei liiku tallennuksen aikana!
tips-file_select = Vedä ja pudota käytettäviä tiedostoja tai <u>selaa</u>.
tips-tap_setup = Voit hitaasti napauttaa 2 kertaa jäljitintä valitaksesi sen, sen sijaan, että valitsisit sen valikosta.
tips-turn_on_tracker = Using official SlimeVR trackers? Don't forget to <b><em>turn on your tracker</em></b> after connecting it to the PC!
tips-failed_webgl = Failed to initialize WebGL.

## Units

unit-meter = Meter
unit-foot = Foot
unit-inch = Inch
unit-cm = cm

## Body parts

body_part-NONE = Ei määritetty
body_part-HEAD = Pää
body_part-NECK = Kaula
body_part-RIGHT_SHOULDER = Oikea olkapää
body_part-RIGHT_UPPER_ARM = Oikea olkavarsi
body_part-RIGHT_LOWER_ARM = Oikea kyynärvarsi
body_part-RIGHT_HAND = Oikea käsi
body_part-RIGHT_UPPER_LEG = Oikea reisi
body_part-RIGHT_LOWER_LEG = Oikea nilkka
body_part-RIGHT_FOOT = Oikea jalkaterä
body_part-UPPER_CHEST = Ylärinta
body_part-CHEST = Rinta
body_part-WAIST = Vyötärö
body_part-HIP = Lonkka
body_part-LEFT_SHOULDER = Vasen olkapää
body_part-LEFT_UPPER_ARM = Vasen olkavarsi
body_part-LEFT_LOWER_ARM = Vasen kyynärvarsi
body_part-LEFT_HAND = Vasen käsi
body_part-LEFT_UPPER_LEG = Vasen reisi
body_part-LEFT_LOWER_LEG = Vasen nilkka
body_part-LEFT_FOOT = Vasen jalkaterä
body_part-LEFT_THUMB_METACARPAL = Left thumb metacarpal
body_part-LEFT_THUMB_PROXIMAL = Left thumb proximal
body_part-LEFT_THUMB_DISTAL = Left thumb distal
body_part-LEFT_INDEX_PROXIMAL = Left index proximal
body_part-LEFT_INDEX_INTERMEDIATE = Left index intermediate
body_part-LEFT_INDEX_DISTAL = Left index distal
body_part-LEFT_MIDDLE_PROXIMAL = Left middle proximal
body_part-LEFT_MIDDLE_INTERMEDIATE = Left middle intermediate
body_part-LEFT_MIDDLE_DISTAL = Left middle distal
body_part-LEFT_RING_PROXIMAL = Left ring proximal
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

board_type-UNKNOWN = Unknown
board_type-NODEMCU = NodeMCU
board_type-CUSTOM = Custom Board
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

skeleton_bone-NONE = Ei mikään
skeleton_bone-HEAD = Pään säätö
skeleton_bone-HEAD-desc =
    This is the distance from your headset to the middle of your head.
    To adjust it, shake your head left to right as if you're disagreeing and modify
    it until any movement in other trackers is negligible.
skeleton_bone-NECK = Kaulan pituus
skeleton_bone-NECK-desc =
    This is the distance from the middle of your head to the base of your neck.
    To adjust it, move your head up and down as if you're nodding or tilt your head
    to the left and right and modify it until any movement in other trackers is negligible.
skeleton_bone-torso_group = Vartalon pituus
skeleton_bone-torso_group-desc =
    This is the distance from the base of your neck to your hips.
    To adjust it, modify it standing up straight until your virtual hips line
    up with your real ones.
skeleton_bone-UPPER_CHEST = Ylärinnan pituus
skeleton_bone-UPPER_CHEST-desc =
    This is the distance from the base of your neck to the middle of your chest.
    To adjust it, adjust your Torso Length properly and modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches with your real one.
skeleton_bone-CHEST_OFFSET = Rinnan keskitys
skeleton_bone-CHEST_OFFSET-desc =
    This can be adjusted to move your virtual chest tracker up or down in order to aid
    with calibration in certain games or applications that may expect it to be higher or lower.
skeleton_bone-CHEST = Rinnan pituus
skeleton_bone-CHEST-desc =
    This is the distance from the middle of your chest to the middle of your spine.
    To adjust it, adjust your Torso Length properly and modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches with your real one.
skeleton_bone-WAIST = Vyötärön pituus
skeleton_bone-WAIST-desc =
    This is the distance from the middle of your spine to your belly button.
    To adjust it, adjust your Torso Length properly and modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches with your real one.
skeleton_bone-HIP = Lonkan pituus
skeleton_bone-HIP-desc =
    This is the distance from your belly button to your hips.
    To adjust it, set your Torso Length properly and modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches your real one.
skeleton_bone-HIP_OFFSET = Lonkan keskitys
skeleton_bone-HIP_OFFSET-desc =
    This can be adjusted to move your virtual hip tracker up or down in order to aid
    with calibration in certain games or applications that may expect it to be on your waist.
skeleton_bone-HIPS_WIDTH = Lonkan leveys
skeleton_bone-HIPS_WIDTH-desc =
    This is the distance between the start of your legs.
    To adjust it, perform a full reset with your legs straight and modify it until
    your virtual legs match up with your real ones horizontally.
skeleton_bone-leg_group = Jalan pituus
skeleton_bone-leg_group-desc =
    This is the distance from your hips to your feet.
    To adjust it, adjust your Torso Length properly and modify it
    until your virtual feet are at the same level as your real ones.
skeleton_bone-UPPER_LEG = Yläjalan pituus
skeleton_bone-UPPER_LEG-desc =
    This is the distance from your hips to your knees.
    To adjust it, adjust your Leg Length properly and modify it
    until your virtual knees are at the same level as your real ones.
skeleton_bone-LOWER_LEG = Säären pituus
skeleton_bone-LOWER_LEG-desc =
    This is the distance from your knees to your ankles.
    To adjust it, adjust your Leg Length properly and modify it
    until your virtual knees are at the same level as your real ones.
skeleton_bone-FOOT_LENGTH = Jalkaterän pituus
skeleton_bone-FOOT_LENGTH-desc =
    This is the distance from your ankles to your toes.
    To adjust it, tiptoe and modify it until your virtual feet stay in place.
skeleton_bone-FOOT_SHIFT = Jalkaterän säätö
skeleton_bone-FOOT_SHIFT-desc =
    This value is the horizontal distance from your knee to your ankle.
    It accounts for your lower legs going backwards when standing up straight.
    To adjust it, set Foot Length to 0, perform a full reset and modify it until your virtual
    feet line up with the middle of your ankles.
skeleton_bone-SKELETON_OFFSET = Luurangon keskitys
skeleton_bone-SKELETON_OFFSET-desc =
    This can be adjusted to offset all your trackers forward or backward.
    It can be used to help with calibration in certain games or applications
    that may expect your trackers to be more forward.
skeleton_bone-SHOULDERS_DISTANCE = Olkapäiden etäisyys
skeleton_bone-SHOULDERS_DISTANCE-desc =
    This is the vertical distance from the base of your neck to your shoulders.
    To adjust it, set Upper Arm Length to 0 and modify it until your virtual elbow trackers
    line up vertically with your real shoulders.
skeleton_bone-SHOULDERS_WIDTH = Olkapäiden leveys
skeleton_bone-SHOULDERS_WIDTH-desc =
    This is the horizontal distance from the base of your neck to your shoulders.
    To adjust it, set Upper Arm Length to 0 and modify it until your virtual elbow trackers
    line up horizontally with your real shoulders.
skeleton_bone-arm_group = Käsivarren pituus
skeleton_bone-arm_group-desc =
    This is the distance from your shoulders to your wrists.
    To adjust it, adjust Shoulders Distance properly, set Hand Distance Y
    to 0 and modify it until your hand trackers line up with your wrists.
skeleton_bone-UPPER_ARM = Olkavarren pituus
skeleton_bone-UPPER_ARM-desc =
    This is the distance from your shoulders to your elbows.
    To adjust it, adjust Arm Length properly and modify it until
    your elbow trackers line up with your real elbows.
skeleton_bone-LOWER_ARM = Kyynärvarren pituus
skeleton_bone-LOWER_ARM-desc =
    This is the distance from your elbows to your wrists.
    To adjust it, adjust Arm Length properly and modify it until
    your elbow trackers line up with your real elbows.
skeleton_bone-HAND_Y = Käden Etäisyys Y
skeleton_bone-HAND_Y-desc =
    This is the vertical distance from your wrists to the middle of your hand.
    To adjust it for motion capture, adjust Arm Length properly and modify it until your
    hand trackers line up vertically with the middle of your hands.
    To adjust it for elbow tracking from your controllers, set Arm Length to 0 and
    modify it until your elbow trackers line up vertically with your wrists.
skeleton_bone-HAND_Z = Käden Etäisyys Z
skeleton_bone-HAND_Z-desc =
    This is the horizontal distance from your wrists to the middle of your hand.
    To adjust it for motion capture, set it to 0.
    To adjust it for elbow tracking from your controllers, set Arm Length to 0 and
    modify it until your elbow trackers line up horizontally with your wrists.
skeleton_bone-ELBOW_OFFSET = Kyynärpään keskitys
skeleton_bone-ELBOW_OFFSET-desc =
    This can be adjusted to move your virtual elbow trackers up or down in order to aid
    with VRChat accidentally binding an elbow tracker to the chest.

## Tracker reset buttons

reset-reset_all = Nollaa kaikki mittasuhteet
reset-reset_all_warning-v2 =
    <b>Warning:</b> Your proportions will be reset to defaults scaled to your configured height.
    Are you sure you want to do this?
reset-reset_all_warning-reset = Reset proportions
reset-reset_all_warning-cancel = Cancel
reset-reset_all_warning_default-v2 =
    <b>Warning:</b> Your height has not been configured, your proportions will be reset to defaults with the default height.
    Are you sure you want to do this?
reset-full = Täysinollaus
reset-mounting = Nollaa Asennus
reset-mounting-feet = Feet Calibration
reset-mounting-fingers = Fingers Calibration
reset-yaw = Nollaa Kallistuma
reset-error-no_feet_tracker = No feet tracker assigned
reset-error-no_fingers_tracker = No finger tracker assigned
reset-error-mounting-need_full_reset = Need a full reset before mounting
reset-error-yaw-need_full_reset = Need a full reset before yaw reset

## Serial detection stuff

serial_detection-new_device-p0 = Uusi sarjalaite havaittu!
serial_detection-new_device-p1 = Anna Wi-Fi-kirjautumistietosi!
serial_detection-new_device-p2 = Valitse, mitä haluat tehdä sillä
serial_detection-open_wifi = Yhdistä Wi-Fi-verkkoon
serial_detection-open_serial = Avaa sarjakonsoli
serial_detection-submit = Lähetä!
serial_detection-close = Sulje

## Navigation bar

navbar-home = Aloitus
navbar-body_proportions = Kehon Mittasuhteet
navbar-trackers_assign = Jäljittimien Määritys
navbar-mounting = Asennuksen Kalibrointi
navbar-onboarding = Asennustoiminto
navbar-settings = Asetukset
navbar-connect_trackers = Connect Trackers

## Biovision hierarchy recording

bvh-start_recording = Tallenna BVH
bvh-stop_recording = Save BVH recording
bvh-recording = Tallennetaan...
bvh-save_title = Save BVH recording

## Tracking pause

tracking-unpaused = Keskeytä jäljitys
tracking-paused = Jatka jäljitystä

## Widget: Overlay settings

widget-overlay = Overlay
widget-overlay-is_visible_label = Näytä Overlay SteamVR:ssä
widget-overlay-is_mirrored_label = Näytä Overlay Peilinä

## Widget: Drift compensation

widget-drift_compensation-clear = Tyhjennä ajautumakompensaatio

## Widget: Clear Mounting calibration

widget-clear_mounting = Tyhjennä asennuksen nollaus

## Widget: Developer settings

widget-developer_mode = Kehittäjätila
widget-developer_mode-high_contrast = Suuri kontrasti
widget-developer_mode-precise_rotation = Tarkka kierto
widget-developer_mode-fast_data_feed = Nopea tietosyöte
widget-developer_mode-filter_slimes_and_hmd = Suodata slimesit ja HMD
widget-developer_mode-sort_by_name = Lajittele nimen mukaan
widget-developer_mode-raw_slime_rotation = Käsittelemätön kierto
widget-developer_mode-more_info = Lisätietoja

## Widget: IMU Visualizer

widget-imu_visualizer = Kierto
widget-imu_visualizer-preview = Preview
widget-imu_visualizer-hide = Hide
widget-imu_visualizer-rotation_raw = Käsittelemätön
widget-imu_visualizer-rotation_preview = Esikatselu
widget-imu_visualizer-acceleration = Acceleration
widget-imu_visualizer-position = Position
widget-imu_visualizer-stay_aligned = Stay Aligned

## Widget: Skeleton Visualizer

widget-skeleton_visualizer-preview = Luurangon esikatselu
widget-skeleton_visualizer-hide = Piilota

## Tracker status

tracker-status-none = Ei tilaa
tracker-status-busy = Varattu
tracker-status-error = Virhe
tracker-status-disconnected = Katkaistu
tracker-status-occluded = Peittynyt
tracker-status-ok = OK
tracker-status-timed_out = Aikakatkaistiin

## Tracker status columns

tracker-table-column-name = Nimi
tracker-table-column-type = Tyyppi
tracker-table-column-battery = Akkuvirta
tracker-table-column-ping = Ping
tracker-table-column-packet_loss = Packet Loss
tracker-table-column-tps = TPS
tracker-table-column-temperature = Lämpötila °C
tracker-table-column-linear-acceleration = Kiihtyvyys X/Y/Z
tracker-table-column-rotation = Kierto X/Y/Z
tracker-table-column-position = Sijainti X/Y/Z
tracker-table-column-stay_aligned = Stay Aligned
tracker-table-column-url = URL

## Tracker rotation

tracker-rotation-front = Etu
tracker-rotation-front_left = Etu-vasen
tracker-rotation-front_right = Etu-oikea
tracker-rotation-left = Vasen
tracker-rotation-right = Oikea
tracker-rotation-back = Taka
tracker-rotation-back_left = Taka-vasen
tracker-rotation-back_right = Taka-oikea
tracker-rotation-custom = Mukautettu
tracker-rotation-overriden = (asennusnollauksen ohittama)

## Tracker information

tracker-infos-manufacturer = Valmistaja
tracker-infos-display_name = Näyttönimi
tracker-infos-custom_name = Mukautettu Nimi
tracker-infos-url = Jäljittimen URL
tracker-infos-version = Laiteohjelmiston Versio
tracker-infos-hardware_rev = Laitteston Tarkistus
tracker-infos-hardware_identifier = Laitteiston ID
tracker-infos-data_support = Data support
tracker-infos-imu = IMU-Sensor
tracker-infos-board_type = Päälevy
tracker-infos-network_version = Protokollan versio
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

tracker-settings-back = Palaa jäljittimien luetteloon
tracker-settings-title = Jäljittimien asetukset
tracker-settings-assignment_section = Määritys
tracker-settings-assignment_section-description = Mihin kehon osaan jäljitin on määritetty.
tracker-settings-assignment_section-edit = Muokkaa määritystä
tracker-settings-mounting_section = Asennusasento
tracker-settings-mounting_section-description = Mihin jäljitin on asennettu?
tracker-settings-mounting_section-edit = Muokkaa asennusta
tracker-settings-drift_compensation_section = Salli ajautumakompensaatio
tracker-settings-drift_compensation_section-description = Pitäisikö tämän jäljittimen kompensoida ajautumaa, jos ajautumakompensaatio on päällä?
tracker-settings-drift_compensation_section-edit = Salli ajautumakompensaatio
tracker-settings-use_mag = Allow magnetometer on this tracker
# Multiline!
tracker-settings-use_mag-description =
    Should this tracker use magnetometer to reduce drift when magnetometer usage is allowed? <b>Please don't shutdown your tracker while toggling this!</b>
    
    You need to allow magnetometer usage first, <magSetting>click here to go to the setting</magSetting>.
tracker-settings-use_mag-label = Allow magnetometer
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Jäljittimen nimi
tracker-settings-name_section-description = Anna sille söpö lempinimi :)
tracker-settings-name_section-placeholder = NightyBeast vasen jalka
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

tracker-part_card-no_name = Ei nimeä
tracker-part_card-unassigned = Ei määritetty

## Body assignment menu

body_assignment_menu = Missä haluat tämän jäljittimen olevan?
body_assignment_menu-description = Valitse sijainti, johon haluat määrittää tämän jäljittimen. Vaihtoehtoisesti voit valita, haluatko hallita kaikkia jäljittimiä kerralla yhden sijaan.
body_assignment_menu-show_advanced_locations = Näytä tarkempia määrityssijainteja
body_assignment_menu-manage_trackers = Hallitse kaikkia jäljittimiä
body_assignment_menu-unassign_tracker = Poista jäljittimen määritys

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = Minkä jäljittimen valitset
tracker_selection_menu-NONE = Minkä jäljittimen määrityksen haluat poistaa?
tracker_selection_menu-HEAD = { -tracker_selection-part } pää?
tracker_selection_menu-NECK = { -tracker_selection-part } kaula?
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part } oikea olkapää?
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part } oikea olkavarsi?
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part } oikea kyynärvarsi?
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part } oikea käsi?
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part } oikea reisi?
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part } oikea nilkka?
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part } oikea jalkaterä?
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part } oikea ohjain?
tracker_selection_menu-UPPER_CHEST = { -tracker_selection-part } ylärinta?
tracker_selection_menu-CHEST = { -tracker_selection-part } rinta?
tracker_selection_menu-WAIST = { -tracker_selection-part } vyötärö?
tracker_selection_menu-HIP = { -tracker_selection-part } lonkka?
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part } vasen olkapää?
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part } vasen olkavarsi?
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part } vasen kyynärvarsi?
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part } vasen käsi?
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part } vasen reisi?
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part } vasen nilkka?
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part } vasen jalkaterä?
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part } vasen ohjain?
tracker_selection_menu-unassigned = Määrittämättömät jäljittimet
tracker_selection_menu-assigned = Määritetyt jäljittimet
tracker_selection_menu-dont_assign = Älä määritä
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Varoitus:</b> Kaulan jäljitin voi olla tappava jos säädetty liian tiukasti,
    hihna voi katkaista verenkierron päähän!
tracker_selection_menu-neck_warning-done = Ymmärrän riskit
tracker_selection_menu-neck_warning-cancel = Peruuta

## Mounting menu

mounting_selection_menu = Missä haluat tämän jäljittimen olevan?
mounting_selection_menu-close = Sulje

## Sidebar settings

settings-sidebar-title = Asetukset
settings-sidebar-general = Yleistä
settings-sidebar-steamvr = SteamVR
settings-sidebar-tracker_mechanics = Jäljittimen mekaniikat
settings-sidebar-stay_aligned = Stay Aligned
settings-sidebar-fk_settings = Jäljityksen asetukset
settings-sidebar-gesture_control = Eleohjaus
settings-sidebar-interface = Käyttöliittymä
settings-sidebar-osc_router = OSC-reititin
settings-sidebar-osc_trackers = VRChat OSC-jäljittimet
settings-sidebar-osc_vmc = VMC
settings-sidebar-utils = Lisäohjelmat
settings-sidebar-serial = Sarjakonsoli
settings-sidebar-appearance = Ulkonäkö
settings-sidebar-home = Home Screen
settings-sidebar-checklist = Tracking checklist
settings-sidebar-notifications = Ilmoitukset
settings-sidebar-behavior = Behavior
settings-sidebar-firmware-tool = DIY Firmware Tool
settings-sidebar-vrc_warnings = VRChat Config Warnings
settings-sidebar-advanced = Advanced

## SteamVR settings

settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = SteamVR jäljittimet
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    Ota tai poista käytöstä tietyt SteamVR jäljittimet.
    Hyödyllinen peleille tai sovelluksille, jotka tukevat vain tiettyjä jäljittimiä.
settings-general-steamvr-trackers-waist = Vyötärö
settings-general-steamvr-trackers-chest = Rinta
settings-general-steamvr-trackers-left_foot = Vasen jalkaterä
settings-general-steamvr-trackers-right_foot = Oikea jalkaterä
settings-general-steamvr-trackers-left_knee = Vasen polvi
settings-general-steamvr-trackers-right_knee = Oikea polvi
settings-general-steamvr-trackers-left_elbow = Vasen kyynärpää
settings-general-steamvr-trackers-right_elbow = Oikea kyynärpää
settings-general-steamvr-trackers-left_hand = Vasen käsi
settings-general-steamvr-trackers-right_hand = Oikea käsi
settings-general-steamvr-trackers-tracker_toggling = Automatic tracker assignment
settings-general-steamvr-trackers-tracker_toggling-description = Automatically handles toggling SteamVR trackers on or off depending on your current tracker assignments.
settings-general-steamvr-trackers-tracker_toggling-label = Automatic tracker assignment
settings-general-steamvr-trackers-hands-warning =
    <b>Warning:</b> Enabling the SteamVR hand trackers will disable inputs from real controllers.
    This should only be enabled if you are using SlimeVR for hand tracking.
    
    Are you sure you want to do this?
settings-general-steamvr-trackers-hands-warning-cancel = Peruuta
settings-general-steamvr-trackers-hands-warning-done = Kyllä

## Tracker mechanics

settings-general-tracker_mechanics = Jäljittimen mekaniikat
settings-general-tracker_mechanics-filtering = Suodatus
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Valitse suodatustyyppi jäljittimillesi.
    Ennustus ennustaa liikettä, kun taas tasoitus tasoittaa liikettä.
settings-general-tracker_mechanics-filtering-type = Suodatustyyppi
settings-general-tracker_mechanics-filtering-type-none = Ei suodatusta
settings-general-tracker_mechanics-filtering-type-none-description = Käytä kiertoja sellaisenaan. Ei tee mitään suodatusta.
settings-general-tracker_mechanics-filtering-type-smoothing = Tasoitus
settings-general-tracker_mechanics-filtering-type-smoothing-description = Tasoittaa liikettä, mutta lisää hieman viivettä.
settings-general-tracker_mechanics-filtering-type-prediction = Ennustus
settings-general-tracker_mechanics-filtering-type-prediction-description = Vähentää viivettä ja tekee liikeistä näppärämpiä, mutta voi lisätä värinää.
settings-general-tracker_mechanics-filtering-amount = Määrä
settings-general-tracker_mechanics-yaw-reset-smooth-time = Yaw reset smooth time (0s disables smoothing)
settings-general-tracker_mechanics-drift_compensation = Ajautumakompensaatio
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Kompensoi IMU-kääntymistä käyttämällä käänteistä kiertoa.
    Muuta kompensaation määrää ja kuinka monta nollausta otetaan huomioon.
settings-general-tracker_mechanics-drift_compensation-enabled-label = Ajautumakompensaatio
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
settings-general-tracker_mechanics-drift_compensation_warning-cancel = Cancel
settings-general-tracker_mechanics-drift_compensation_warning-done = I understand
settings-general-tracker_mechanics-drift_compensation-amount-label = Kompensaation määrä
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Käytä enintään x viimeistä nollausta
settings-general-tracker_mechanics-save_mounting_reset = Save automatic mounting calibration
settings-general-tracker_mechanics-save_mounting_reset-description =
    Saves the automatic mounting calibration for the trackers between restarts. Useful
    when wearing a suit where trackers don't move between sessions. <b>Not recommended for normal users!</b>
settings-general-tracker_mechanics-save_mounting_reset-enabled-label = Save mounting calibration
settings-general-tracker_mechanics-use_mag_on_all_trackers = Use magnetometer on all IMU trackers that support it
settings-general-tracker_mechanics-use_mag_on_all_trackers-description =
    Uses magnetometer on all trackers that have a compatible firmware for it, reducing drift in stable magnetic environments.
    Can be disabled per tracker in the tracker's settings. <b>Please don't shutdown any of the trackers while toggling this!</b>
settings-general-tracker_mechanics-use_mag_on_all_trackers-label = Use magnetometer on trackers
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

settings-general-fk_settings = Jäljityksen asetukset
# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Floor clip
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Skating correction
settings-general-fk_settings-leg_tweak-toe_snap = Toe snap
settings-general-fk_settings-leg_tweak-foot_plant = Foot plant
settings-general-fk_settings-leg_tweak-skating_correction-amount = Skating correction vahvuus
settings-general-fk_settings-leg_tweak-skating_correction-description = Skating correction helpottaa jalkojen luistelua, mutta voi heikentää tarkkuutta tietyissä liikekuvioissa. Kun otat käyttöön, muista tehdä täysi nollaus ja kalibroida uudelleen pelissä.
settings-general-fk_settings-leg_tweak-floor_clip-description = Floor clip voi vähentää tai korjata jalan kulun lattian läpi. Kun otat käyttöön, muista tehdä täysi nollaus ja kalibroida uudelleen pelissä.
settings-general-fk_settings-leg_tweak-toe_snap-description = Toe snap yrittää arvata varpaiden asennon jos jalkaterän jäljitintä ei ole käytössä.
settings-general-fk_settings-leg_tweak-foot_plant-description = Foot plant asettaa jalkateräsi yhdensuuntaisesti maan kanssa kosketuksessa.
settings-general-fk_settings-leg_fk = Jalkojen jäljitys
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
settings-general-fk_settings-arm_fk = Käsivarsien jäljitys
settings-general-fk_settings-arm_fk-description = Muuta tapaa, jolla käsivarsia jäljitetään.
settings-general-fk_settings-arm_fk-force_arms = Pakota kädet HMD:ltä
settings-general-fk_settings-reset_settings = Palauta asetukset
settings-general-fk_settings-reset_settings-reset_hmd_pitch-description = Reset the HMD's pitch (vertical rotation) upon doing a full reset. Useful if wearing an HMD on the forehead for VTubing or mocap. Do not enable for VR.
settings-general-fk_settings-reset_settings-reset_hmd_pitch = Nollaa HMD-sävelkorkeus
settings-general-fk_settings-arm_fk-reset_mode-description = Muuta, mikä käsivarren asentoa odotetaan asennuksen nollaukselle.
settings-general-fk_settings-arm_fk-back = Takaisin
settings-general-fk_settings-arm_fk-back-description = The default mode, with the upper arms going back and lower arms going forward.
settings-general-fk_settings-arm_fk-tpose_up = T-asento (ylös)
settings-general-fk_settings-arm_fk-tpose_up-description = Expects your arms to be down at your sides during Full Reset, and 90 degrees up to the sides during Mounting Calibration.
settings-general-fk_settings-arm_fk-tpose_down = T-asento (alas)
settings-general-fk_settings-arm_fk-tpose_down-description = Expects your arms to be 90 degrees up to the sides during Full Reset, and down at your sides during Mounting Calibration.
settings-general-fk_settings-arm_fk-forward = Eteenpäin
settings-general-fk_settings-arm_fk-forward-description = Expects your arms to be raised forward at 90 degrees. Useful for VTubing.
settings-general-fk_settings-skeleton_settings-toggles = Luurankoasetuksia
settings-general-fk_settings-skeleton_settings-description = Ota tai poista käytöstä luurankoasetukset. On suositeltavaa jättää nämä päälle.
settings-general-fk_settings-skeleton_settings-extended_spine_model = Laajennettu selkärankamalli
settings-general-fk_settings-skeleton_settings-extended_pelvis_model = Laajennettu lantiomalli
settings-general-fk_settings-skeleton_settings-extended_knees_model = Laajennettu polvimalli
settings-general-fk_settings-skeleton_settings-ratios = Luurankosuhteet
settings-general-fk_settings-skeleton_settings-ratios-description = Muuta luurankoasetusten arvoja. Saatat joutua säätämään mittasuhteitasi muutosten jälkeen.
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_hip = Laskelmoi vyötärö rinnasta lantioon
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_legs = Laskelmoi vyötärö rinnasta jalkoihin
settings-general-fk_settings-skeleton_settings-impute_hip_from_chest_legs = Laskelmoi lonkka rinnasta jalkoihin
settings-general-fk_settings-skeleton_settings-impute_hip_from_waist_legs = Laskelmoi lantio vyötäröstä jalkoihin
settings-general-fk_settings-skeleton_settings-interp_hip_legs = Keskimääritä lonkan kallistus jaloilla'
settings-general-fk_settings-skeleton_settings-interp_knee_tracker_ankle = Keskimääritä polvijäljittimen kallistus nilkoilla'
settings-general-fk_settings-skeleton_settings-interp_knee_ankle = Average the knees' yaw and roll with the ankles'
settings-general-fk_settings-self_localization-title = Mocap-tila
settings-general-fk_settings-self_localization-description = Mocap-tila sallii luurangon karkeasti seurata omaa sijaintiaan ilman laseja tai muita jäljittimiä. Huomioi, että tämä vaatii jalka- ja pääjäljittimien toimimista ja on vielä kokeellinen.

## Gesture control settings (tracker tapping)

settings-general-gesture_control = Eleohjaus
settings-general-gesture_control-subtitle = Napautuspohjaiset nollaukset
settings-general-gesture_control-description = Mahdollistaa nollauksen napauttamalla jäljitintä. Ylävartalon korkeinta jäljitintä käytetään Pikanollaukseen. Vasemman jalan korkeinta jäljitintä käytetään Nollaukseen, vastaavaisesti oikean jalan korkeinta jäljitintä käytetään Asennusnollaukseen. On syytä mainita, että napautusten on tapahduttava 0.6 sekunnin sisällä, jotta ne rekisteröityvät.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
        [one] 1 napautus
       *[other] { $amount } napautusta
    }
# This is a unit: 3 trackers, 2 trackers, 1 tracker
# $amount (Number) - Amount of trackers
settings-general-gesture_control-trackers =
    { $amount ->
        [one] 1 jäljitin
       *[other] { $amount } jäljitintä
    }
settings-general-gesture_control-yawResetEnabled = Ota käyttöön kallistumanollaus napautus
settings-general-gesture_control-yawResetDelay = Kallistumanollaus viive
settings-general-gesture_control-yawResetTaps = Napautuksia kallistumanollaukseen.
settings-general-gesture_control-yawResetTracker = Yaw reset tracker
settings-general-gesture_control-fullResetEnabled = Ota käyttöön täysinollaus napautus
settings-general-gesture_control-fullResetDelay = Täysinollaus viive
settings-general-gesture_control-fullResetTaps = Napautuksia täysinollaukseen
settings-general-gesture_control-fullResetTracker = Full reset tracker
settings-general-gesture_control-mountingResetEnabled = Ota käyttöön asennusnollaus napautus
settings-general-gesture_control-mountingResetDelay = Asennusnollaus viive
settings-general-gesture_control-mountingResetTaps = Napautuksia asennusnollaukseen
settings-general-gesture_control-mountingResetTracker = Mounting reset tracker
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = Jäljittimet yli kynnysarvon
settings-general-gesture_control-numberTrackersOverThreshold-description = Suurenna tätä arvoa jos napautustunnistus ei toimi. Älä suurenna yli sen, mikä on tarpeellista, koska se voi aiheuttaa enemmän virheitä.

## Appearance settings

settings-interface-appearance = Ulkonäkö
settings-general-interface-dev_mode = Kehittäjätila
settings-general-interface-dev_mode-description = Tämä tila voi olla hyödyllinen, jos tarvitset perusteellisia tietoja tai haluat olla tekemisissä yhdistettyjen jäljittimien kanssa edistyneemmällä tasolla.
settings-general-interface-dev_mode-label = Kehittäjätila
settings-general-interface-theme = Väri teema
settings-general-interface-show-navbar-onboarding = Show "{ navbar-onboarding }" on navigation bar
settings-general-interface-show-navbar-onboarding-description = This changes whether the "{ navbar-onboarding }" button shows on the navigation bar.
settings-general-interface-show-navbar-onboarding-label = Show "{ navbar-onboarding }"
settings-general-interface-lang = Valitse kieli
settings-general-interface-lang-description = Vaihda oletuskieli, jota haluat käyttää.
settings-general-interface-lang-placeholder = Valitse käytettävä kieli
# Keep the font name untranslated
settings-interface-appearance-font = GUI-fontti
settings-interface-appearance-font-description = Tämä muuttaa käyttöliittymän käyttämää fonttia.
settings-interface-appearance-font-placeholder = Oletusfontti
settings-interface-appearance-font-os_font = OS-fontti
settings-interface-appearance-font-slime_font = Oletusfontti
settings-interface-appearance-font_size = Perusfontin skaalaus
settings-interface-appearance-font_size-description = Tämä vaikuttaa koko käyttöliittymän fonttikokoon tätä asetuspaneelia lukuun ottamatta.
settings-interface-appearance-decorations = Use the system native decorations
settings-interface-appearance-decorations-description = This will not render the top bar of the interface and will use the operating system's instead.
settings-interface-appearance-decorations-label = Use native decorations

## Notification settings

settings-interface-notifications = Ilmoitukset
settings-general-interface-serial_detection = Sarjalaitteen tunnistus
settings-general-interface-serial_detection-description = Tämä vaihtoehto näyttää ponnahdusikkunan aina, kun liität uuden sarjalaitteen, joka voi olla jäljitin. Se auttaa parantamaan jäljittimen asennusprosessia.
settings-general-interface-serial_detection-label = Sarjalaitteen tunnistus
settings-general-interface-feedback_sound = Palaute ääni
settings-general-interface-feedback_sound-description = Tämä asetus toistaa äänen nollauksen tapahtuessa.
settings-general-interface-feedback_sound-label = Palaute ääni
settings-general-interface-feedback_sound-volume = Palaute äänen voimakkuus
settings-general-interface-connected_trackers_warning = Yhdistettyjen jäljittimien varoitus
settings-general-interface-connected_trackers_warning-description = Tämä vaihtoehto näyttää ponnahdusikkunan aina, kun yrität poistua SlimeVR:stä, kun sinulla on yksi tai useampi yhdistetty jäljitin. Se muistuttaa sinua sammuttamaan jäljittimet, kun olet valmis, akun käyttöiän säästämiseksi.
settings-general-interface-connected_trackers_warning-label = Connected trackers warning on exit

## Behavior settings

settings-interface-behavior = Behavior
settings-general-interface-dev_mode = Developer Mode
settings-general-interface-dev_mode-description = This mode can be useful if you need in-depth data or need to interact with connected trackers on a more advanced level.
settings-general-interface-dev_mode-label = Developer Mode
settings-general-interface-use_tray = Pienennä ilmaisinalueelle
settings-general-interface-use_tray-description = Voit sulkea ikkunan sulkematta SlimeVR-palvelinta, jotta voit jatkaa sen käyttöä ilman, että graafinen käyttöliittymä häiritsee sinua.
settings-general-interface-use_tray-label = Pienennä ilmaisinalueelle
settings-general-interface-discord_presence = Jaa toiminta Discordissa
settings-general-interface-discord_presence-description = Kertoo Discordille, että käytät SlimeVR:ää, sekä käyttämiesi IMU-seurantalaitteiden määrän.
settings-general-interface-discord_presence-label = Jaa toiminta Discordissa
settings-general-interface-discord_presence-message =
    { $amount ->
        [0] Sliming around
        [one] Using 1 tracker
       *[other] Using { $amount } trackers
    }
settings-interface-behavior-error_tracking = Error collection via Sentry.io
settings-interface-behavior-error_tracking-description_v2 =
    <h1>Do you consent to the collection of anonymized error data?</h1>
    
    <b>We do not collect personal information</b> such as your IP address or wireless credentials. SlimeVR values your privacy!
    
    To provide the best user experience, we collect anonymized error reports, performance metrics, and operating system information. This helps us detect bugs and issues with SlimeVR. These metrics are collected via Sentry.io.
settings-interface-behavior-error_tracking-label = Send errors to developers
settings-interface-behavior-bvh_directory = Directory to save BVH recordings
settings-interface-behavior-bvh_directory-description = Choose a directory to save your BVH recordings instead of having to choose where to save them each time.
settings-interface-behavior-bvh_directory-label = Directory for BVH recordings

## Serial settings

settings-serial = Sarjakonsoli
# This cares about multilines
settings-serial-description =
    Tämä on reaaliaikainen tietosyöte sarjaviestintää varten.
    Voi olla hyödyllistä, jos sinun on tiedettävä, että laiteohjelmisto toimii.
settings-serial-connection_lost = Yhteys sarjaan kadonnut, yhdistetään uudelleen...
settings-serial-reboot = Käynnistä uudelleen
settings-serial-factory_reset = Tehdasasetusten palautus
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>Varoitus:</b> Tämä palauttaa jäljittimen tehdasasetuksille.
    Tämä tarkoittaa, että Wi-Fi- ja kalibrointiasetukset <b>menetetään kokonaan!</b>
settings-serial-factory_reset-warning-ok = Tiedän mitä teen
settings-serial-factory_reset-warning-cancel = Peruuta
settings-serial-serial_select = Valitse sarjaportti
settings-serial-auto_dropdown_item = Autom.
settings-serial-get_wifi_scan = Get WiFi Scan
settings-serial-enter_pairing = Enter Pairing
settings-serial-exit_pairing = Exit Pairing
settings-serial-calibrate = Calibrate
settings-serial-six_side_calibrate = 6-Side Calibrate
settings-serial-dfu = Enter DFU
settings-serial-meow = Meow!
settings-serial-file_type = Teksti
settings-serial-save_logs = Tallenna tiedostoon
settings-serial-send_command = Send
settings-serial-send_command-placeholder = Command...
settings-serial-send_command-warning = <b>Warning:</b> Running serial commands can lead to data loss or brick the trackers.
settings-serial-send_command-warning-ok = I know what I'm doing
settings-serial-send_command-warning-cancel = Cancel

## OSC router settings

settings-osc-router = OSC-reititin
# This cares about multilines
settings-osc-router-description =
    Välitä OSC-viestit toisesta ohjelmasta.
    Hyödyllinen toisen OSC-ohjelman käyttämiseen esimerkiksi VRChatin kanssa.
settings-osc-router-enable = Käytä
settings-osc-router-enable-description = Vaihda viestien edelleenlähetystä.
settings-osc-router-enable-label = Käytä
settings-osc-router-network = Verkkoportit
# This cares about multilines
settings-osc-router-network-description =
    Aseta portit tietojen kuuntelua ja lähettämistä varten.
    Nämä voivat olla samat kuin muut SlimeVR-palvelimessa käytetyt portit.
settings-osc-router-network-port_in = 
    .label = Portti sisään
    .placeholder = Portti sisään (oletus: 9002)
settings-osc-router-network-port_out = 
    .label = Portti ulos
    .placeholder = Portti ulos (oletus: 9000)
settings-osc-router-network-address = Verkon osoite
settings-osc-router-network-address-description = Määritä osoite, johon tiedot lähetetään.
settings-osc-router-network-address-placeholder = IPV4-osoite

## OSC VRChat settings

settings-osc-vrchat = VRChat OSC -jäljittimet
# This cares about multilines
settings-osc-vrchat-description-v1 =
    Change settings specific to the OSC Trackers standard used for sending
    tracking data to applications without SteamVR (ex. Quest standalone).
    Make sure to enable OSC in VRChat via the Action Menu under OSC > Enabled.
settings-osc-vrchat-enable = Käytä
settings-osc-vrchat-enable-description = Vaihda tietojen lähettäminen ja vastaanottaminen.
settings-osc-vrchat-enable-label = Käytä
settings-osc-vrchat-oscqueryEnabled = Enable OSCQuery
settings-osc-vrchat-oscqueryEnabled-description =
    OSCQuery automatically detects running instances of VRChat and sends them data.
    It can also advertise itself to them in order to receive HMD and controller data.
    To allow receiving HMD and controller data from VRChat, go in your main menu's settings
    under "Tracking & IK" and enable "Allow Sending Head and Wrist VR Tracking OSC Data".
settings-osc-vrchat-oscqueryEnabled-label = Enable OSCQuery
settings-osc-vrchat-network = Verkkoportit
settings-osc-vrchat-network-description-v1 = Aseta portit tietojen kuuntelua ja lähettämistä varten. Voidaan jättää koskematta VRChatille.
settings-osc-vrchat-network-port_in = 
    .label = Portti sisään
    .placeholder = Portti sisään (oletus: 9001)
settings-osc-vrchat-network-port_out = 
    .label = Portti ulos
    .placeholder = Portti ulos (oletus: 9000)
settings-osc-vrchat-network-address = Verkon osoite
settings-osc-vrchat-network-address-description-v1 = Valitse, mihin osoitteeseen tiedot lähetetään. Voidaan jättää koskematta VRChatille.
settings-osc-vrchat-network-address-placeholder = VRChat IP-osoite
settings-osc-vrchat-network-trackers = Jäljittimet
settings-osc-vrchat-network-trackers-description = Vaihda tiettyjen jäljittimien lähettäminen OSC:n kautta.
settings-osc-vrchat-network-trackers-chest = Rinta
settings-osc-vrchat-network-trackers-hip = Lonkka
settings-osc-vrchat-network-trackers-knees = Polvet
settings-osc-vrchat-network-trackers-feet = Jalat
settings-osc-vrchat-network-trackers-elbows = Kyynärpäät

## VMC OSC settings

settings-osc-vmc = Virtual Motion Capture
# This cares about multilines
settings-osc-vmc-description =
    Muuta VMC (Virtual Motion Capture) -protokollan asetuksia
        lähettääksesi ja vastaanottaaksesi SlimeVR:n luutietoja muihin sovelluksiin.
settings-osc-vmc-enable = Käytä
settings-osc-vmc-enable-description = Vaihda tietojen lähettäminen ja vastaanottaminen.
settings-osc-vmc-enable-label = Käytä
settings-osc-vmc-network = Verkkoportit
settings-osc-vmc-network-description = Aseta portit tietojen kuunteluun ja lähettämiseen VMC:n kautta
settings-osc-vmc-network-port_in = 
    .label = Portti sisään
    .placeholder = Portti sisään (oletus: 39540)
settings-osc-vmc-network-port_out = 
    .label = Portti ulos
    .placeholder = Portti ulos (oletus: 39539)
settings-osc-vmc-network-address = Verkon osoite
settings-osc-vmc-network-address-description = Määritä osoite, johon tietoja lähetetään VMC:n kautta
settings-osc-vmc-network-address-placeholder = IPV4-osoite
settings-osc-vmc-vrm = VRM-malli
settings-osc-vmc-vrm-description = Lataa VRM-malli salliaksesi pääankkurin ja mahdollistaaksesi paremman yhteensopivuuden muiden sovellusten kanssa
settings-osc-vmc-vrm-untitled_model = Untitled model
settings-osc-vmc-vrm-file_select = Vedä ja pudota mallia käytettäväksi tai <u>selaa</u>
settings-osc-vmc-anchor_hip = Ankkuri lantiolla
settings-osc-vmc-anchor_hip-description = Ankkuroi jäljitin lonkalle, hyödyllinen istuvaan VTubing. Jos poistat käytöstä, lataa VRM-malli.
settings-osc-vmc-anchor_hip-label = Ankkuroi lonkalle
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

onboarding-skip = Ohita asennus
onboarding-continue = Jatka
onboarding-wip = Keskeneräinen
onboarding-previous_step = Edellinen vaihe
onboarding-setup_warning =
    <b>Varoitus:</b> Alkuasennus vaaditaan hyvään jäljitykseen,
    sitä tarvitaan, jos käytät SlimeVR:ää ensimmäistä kertaa.
onboarding-setup_warning-skip = Ohita asennus
onboarding-setup_warning-cancel = Jatka asennusta

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
onboarding-wifi_creds-skip = Ohita Wi-Fi-asetukset
onboarding-wifi_creds-submit = Lähetä!
onboarding-wifi_creds-ssid = 
    .label = Wi-Fi nimi
    .placeholder = Syötä Wi-Fi nimi
onboarding-wifi_creds-ssid-required = Wi-Fi name is required
onboarding-wifi_creds-password = 
    .label = Salasana
    .placeholder = Syötä salasana
onboarding-wifi_creds-dongle-title = Trackers using a dongle
onboarding-wifi_creds-dongle-description = If your trackers came with a dongle, plug it into your device and you should be good to go!
onboarding-wifi_creds-dongle-wip = This section is a work in progress. A dedicated page to manage trackers that connect via a dongle will be made soon.
onboarding-wifi_creds-dongle-continue = Continue with a dongle

## Mounting setup

onboarding-reset_tutorial-back = Palaa asennuksen kalibrointiin
onboarding-reset_tutorial = Nollaa tutoriaali
onboarding-reset_tutorial-explanation = Kun käytät jäljittimiä, ne saattavat epälinjoittua IMU:n ajautumisen tai fyysisen siirron takia. Sinulla on useita tapoja korjata tämä.
onboarding-reset_tutorial-skip = Ohita vaihe
# Cares about multiline
onboarding-reset_tutorial-0 =
    Napauta { $taps } kertaa korostettua jäljitintä käynnistääksesi kallistuman nollauksen.
    
    Tämä osoittaa jäljittimet samaan suuntaan kuin HMD.
# Cares about multiline
onboarding-reset_tutorial-1 =
    Napauta { $taps } kertaa korostettua jäljitintä käynnistääksesi täysinollauksen.
    
    Sinun on seisottava paikallasi (i-pose) tätä varten. On 3 sekunnin viive (muokattavissa), ennen kuin se tapahtuu.
    Tämä täysin nollaa kaikkien jäljittimien sijainnin ja kierron. Sen pitäisi korjata useimmat ongelmat.
# Cares about multiline
onboarding-reset_tutorial-2 =
    Napauta { $taps } kertaa korostettua jäljitintä käynnistääksesi asennuksen nollaus.
    
    Asennuksen nollaus auttaa siinä, kuinka jäljittimet on todella asetettu sinulla, joten jos vahingossa siirsit ja muutit suuresti niiden suuntaa, tämä auttaa.
    
    Sinun on oltava hiihto-asennossa, kuten on näytettynä Automaattisessa Asennuksessa ja sinulla on 3 sekunnin viive (muokattavissa), ennen kuin se käynnistyy.

## Install info

install-info_udev-rules_modal_title = Hardware udev access rules not found
install-info_udev-rules_warning = Access rules via udev are required for serial console access & dongle connection. Paste the following command into your terminal to add the udev rules.
install-info_udev-rules_modal_button = Close
install-info_udev-rules_modal-dont-show-again_checkbox = Don't show again

## Setup start

onboarding-home = Tervetuloa SlimeVR:ään
onboarding-home-start = Mennään asentamaan!

## Setup done

onboarding-done-title = Olet valmis!
onboarding-done-description = Nauti täysikehojäljityksestäsi
onboarding-done-close = Sulje opas

## Tracker connection setup

onboarding-connect_tracker-back = Palaa Wi-Fi-tunnistetietoihin
onboarding-connect_tracker-title = Yhdistä jäljittimet
onboarding-connect_tracker-description-p0-v1 = Now onto the fun part, connecting trackers!
onboarding-connect_tracker-description-p1-v1 = Connect each tracker one at a time through a USB port.
onboarding-connect_tracker-issue-serial = Minulla on ongelmia yhteyden muodostamisessa!
onboarding-connect_tracker-usb = USB-jäljitin
onboarding-connect_tracker-connection_status-none = Etsitään jäljittimiä
onboarding-connect_tracker-connection_status-serial_init = Yhdistetään sarjalaitteeseen
onboarding-connect_tracker-connection_status-obtaining_mac_address = Obtaining the tracker mac address
onboarding-connect_tracker-connection_status-provisioning = Lähetetään Wi-Fi-tunnistetietoja
onboarding-connect_tracker-connection_status-connecting = Yritetään muodostaa yhteys Wi-Fi-verkkoon
onboarding-connect_tracker-connection_status-looking_for_server = Etsitään palvelinta
onboarding-connect_tracker-connection_status-connection_error = Wi-Fi-yhteyden muodostaminen epäonnistui
onboarding-connect_tracker-connection_status-could_not_find_server = Palvelinta ei löytynyt
onboarding-connect_tracker-connection_status-done = Yhdistetty palvelimeen
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
        [0] Jäljittimiä ei ole yhdistetty
        [one] 1 jäljitin yhdistetty
       *[other] { $amount } jäljitintä yhdistetty
    }
onboarding-connect_tracker-next = Yhdistin kaikki jäljittimeni

## Tracker calibration tutorial

onboarding-calibration_tutorial = IMU-kalibrointi tutoriaali
onboarding-calibration_tutorial-subtitle = Tämä auttaa vähentämään jäljittimen ajautumaa!
onboarding-calibration_tutorial-description-v1 = After turning on your trackers, place them on a stable surface for a moment to allow for calibration. Calibration can be performed at any time after the trackers are powered on—this page simply provides a tutorial. To begin, click the "{ onboarding-calibration_tutorial-calibrate }" button, then <b>do not move your trackers!</b>
onboarding-calibration_tutorial-calibrate = Asetin jäljittimeni pöydälle
onboarding-calibration_tutorial-status-waiting = Odotetaan sinua
onboarding-calibration_tutorial-status-calibrating = Kalibroi
onboarding-calibration_tutorial-status-success = Kiva!
onboarding-calibration_tutorial-status-error = Jäljitintä liikutettiin
onboarding-calibration_tutorial-skip = Skip tutorial

## Tracker assignment tutorial

onboarding-assignment_tutorial = Kuinka valmistellaan Slime-jäljitin ennen käyttöä
onboarding-assignment_tutorial-first_step = 1. Aseta kehonosatarra (jos sinulla on) jäljittimeen valintasi mukaan
# This text has a character limit of around 11 characters, so please keep it short
onboarding-assignment_tutorial-sticker = Tarra
onboarding-assignment_tutorial-second_step-v2 = 2. Kiinnitä hihna jäljittimeen pitäen hihnan tarranauhaa samaan suuntaan kuin jäljittimen etupuoli:
onboarding-assignment_tutorial-second_step-continuation-v2 = Jatkeen tarranauhapuolen tulee olla ylöspäin seuraavan kuvan mukaisesti:
onboarding-assignment_tutorial-done = Laitoin tarrat ja hihnat!

## Tracker assignment setup

onboarding-assign_trackers-back = Palaa Wi-Fi-tunnistetietoihin
onboarding-assign_trackers-title = Määritä jäljittimet
onboarding-assign_trackers-description = Valitaan, mikä jäljitin menee minne. Napsauta paikkaa, johon haluat sijoittaa jäljittimen
onboarding-assign_trackers-unassign_all = Unassign all trackers
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned =
    { $trackers ->
        [one] { $assigned } / 1 jäljitintä määritetty
       *[other] { $assigned } / { $trackers } jäljittimiä määritetty
    }
onboarding-assign_trackers-advanced = Näytä tarkempia määrityssijainteja
onboarding-assign_trackers-next = Määritin kaikki jäljittimet
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
    { $unassigned ->
        [6] Vasen jalkaterä on määritetty, mutta myös vasen nilkka on määritettävä!
        [5] Vasen jalkaterä on määritetty, mutta myös vasen reisi on määritettävä!
        [4] Vasen jalkaterä on määritetty, mutta myös vasen nilkka ja vasen reisi on määritettävä!
        [3] Vasen jalkaterä on määritetty, mutta joko rinta, lantio tai vyötärö on myös määritettävä!
        [2] Vasen jalkaterä on määritetty, mutta myös vasen nilkka ja joko rinta, lantio tai vyötärö on määritettävä!
        [1] Vasen jalkaterä on määritetty, mutta myös vasen reisi ja joko rinta, lantio tai vyötärö on määritettävä!
        [0] Vasen jalkaterä on määritetty, mutta myös vasen nilkka, vasen reisi ja joko rinta, lantio tai vyötärö on määritettävä!
       *[other] Vasen jalkaterä on määritetty, mutta myös tuntematon määrittelemätön osa on määritettävä!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_FOOT =
    { $unassigned ->
        [6] Oikea jalkaterä on määritetty, mutta myös oikea nilkka on määritettävä!
        [5] Oikea jalkaterä on määritetty, mutta myös oikea reisi on määritettävä!
        [4] Oikea jalkaterä on määritetty, mutta myös oikea nilkka ja oikea reisi on määritettävä!
        [3] Oikea jalkaterä on määritetty, mutta joko rinta, lantio tai vyötärö on myös määritettävä!
        [2] Oikea jalkaterä on määritetty, mutta myös oikea nilkka ja joko rinta, lantio tai vyötärö on määritettävä!
        [1] Oikea jalkaterä on määritetty, mutta myös oikea reisi ja joko rinta, lantio tai vyötärö on määritettävä!
        [0] Oikea jalkaterä on määritetty, mutta myös oikea nilkka, oikea reisi ja joko rinta, lantio tai vyötärö on määritettävä!
       *[other] Oikea jalkaterä on määritetty, mutta myös tuntematon määrittelemätön osa on määritettävä!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_LOWER_LEG =
    { $unassigned ->
        [0] Vasen nilkka on määritetty, mutta myös vasen reisi ja joko rinta, lantio tai vyötärö on määritettävä!
        [1] Vasen nilkka on määritetty, mutta joko rinta, lantio tai vyötärö on myös määritettävä!
        [2] Vasen nilkka on määritetty, mutta myös vasen reisi on määritettävä!
       *[other] Vasen nilkka on määritetty, mutta myös tuntematon määrittelemätön osa on määritettävä!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_LOWER_LEG =
    { $unassigned ->
        [0] Oikea nilkka on määritetty, mutta myös oikea reisi ja joko rinta, lantio tai vyötärö on määritettävä!
        [1] Oikea nilkka on määritetty, mutta joko rinta, lantio tai vyötärö on myös määritettävä!
        [2] Oikea nilkka on määritetty, mutta myös oikea reisi on määritettävä!
       *[other] Oikea nilkka on määritetty, mutta myös tuntematon määrittelemätön osa on määritettävä!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_UPPER_LEG =
    { $unassigned ->
        [0] Vasen reisi on määritetty, mutta myös rinta, lantio tai vyötärö on määritettävä!
       *[other] Vasen reisi on määritetty, mutta myös tuntematon määrittelemätön osa on määritettävä!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_UPPER_LEG =
    { $unassigned ->
        [0] Oikea reisi on määritetty, mutta myös rinta, lantio tai vyötärö on määritettävä!
       *[other] Oikea reisi on määritetty, mutta myös tuntematon määrittelemätön osa on määritettävä!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-HIP =
    { $unassigned ->
        [0] Lonkka on määritetty, mutta myös rinta on määritettävä!
       *[other] Lonkka on määritetty, mutta myös tuntematon määrittelemätön osa on määritettävä!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-WAIST =
    { $unassigned ->
        [0] Vyötärö on määritetty, mutta myös rinta on määritettävä!
       *[other] Vyötärö on määritetty, mutta myös tuntematon määrittelemätön osa on määritettävä!
    }

## Tracker mounting method choose

onboarding-choose_mounting = Mitä asennuskalibrointimenetelmää käytetään?
# Multiline text
onboarding-choose_mounting-description = Asennussuuntaus korjaa jäljittimien sijoittelun kehossasi.
onboarding-choose_mounting-auto_mounting = Automaattinen asennus
# Italicized text
onboarding-choose_mounting-auto_mounting-label-v2 = Recommended
onboarding-choose_mounting-auto_mounting-description = Tämä tunnistaa automaattisesti kaikkien jäljittimiesi asennussuunnat 2 asennosta
onboarding-choose_mounting-manual_mounting = Manuaalinen asennus
# Italicized text
onboarding-choose_mounting-manual_mounting-label-v2 = Might not be precise enough
onboarding-choose_mounting-manual_mounting-description = Näin voit valita asennussuunnan manuaalisesti kullekin jäljittimelle
# Multiline text
onboarding-choose_mounting-manual_modal-title =
    Oletko varma, että haluat tehdä
    automaattisen asennuksen kalibroinnin?
onboarding-choose_mounting-manual_modal-description = <b>The manual mounting calibration is recommended for new users</b>, as the automatic mounting calibration's poses can be hard to get right first and may require some practice.
onboarding-choose_mounting-manual_modal-confirm = Tiedän mitä teen
onboarding-choose_mounting-manual_modal-cancel = Peruuta

## Tracker manual mounting setup

onboarding-manual_mounting-back = Palaa takaisin VR:ään
onboarding-manual_mounting = Manuaalinen Asennus
onboarding-manual_mounting-description = Napsauta jokaista jäljitintä ja valitse, mihin suuntaan ne on asennettu
onboarding-manual_mounting-auto_mounting = Automaattinen asennus
onboarding-manual_mounting-next = Seuraava vaihe

## Tracker automatic mounting setup

onboarding-automatic_mounting-back = Palaa takaisin VR:ään
onboarding-automatic_mounting-title = Asennuksen Kalibrointi
onboarding-automatic_mounting-description = Jotta SlimeVR jäljittimet toimisivat, meidän on määritettävä jäljittimille asennuksen kierto, jotta ne voidaan kohdistaa fyysisen jäljittimen asennuksen kanssa.
onboarding-automatic_mounting-manual_mounting = Manuaalinen asennus
onboarding-automatic_mounting-next = Seuraava vaihe
onboarding-automatic_mounting-prev_step = Edellinen vaihe
onboarding-automatic_mounting-done-title = Asennuskierrokset kalibroitu.
onboarding-automatic_mounting-done-description = Asennuskalibrointi on valmis!
onboarding-automatic_mounting-done-restart = Yritä uudelleen
onboarding-automatic_mounting-mounting_reset-title = Asennuksen Nollaus
onboarding-automatic_mounting-mounting_reset-step-0 = 1. Kyykisty "hiihtoasentoon" siten, että jalat ovat koukussa, ylävartalo kallistettuna eteenpäin ja kädet koukussa.
onboarding-automatic_mounting-mounting_reset-step-1 = 2. Paina "Nollaa Asennus" -painiketta ja odota 3 sekuntia, ennen kuin jäljittimien asennuskierrot nollautuvat.
onboarding-automatic_mounting-mounting_reset-feet-step-0 = 1. Stand on your toes with both feet pointing forward. Alternatively you can do it sitting on a chair.
onboarding-automatic_mounting-mounting_reset-feet-step-1 = 2. Press the "Feet calibration" button and wait for 3 seconds before the trackers' mounting orientations will reset.
onboarding-automatic_mounting-preparation-title = Valmistelu
onboarding-automatic_mounting-preparation-v2-step-0 = 1. Press the "Full Reset" button.
onboarding-automatic_mounting-preparation-v2-step-1 = 2. Stand upright with your arms to your sides. Make sure to look forward.
onboarding-automatic_mounting-preparation-v2-step-2 = 3. Hold the position until the 3s timer ends.
onboarding-automatic_mounting-put_trackers_on-title = Laita jäljittimet päällesi
onboarding-automatic_mounting-put_trackers_on-description = Kalibroidaksemme asennuskierrokset käytämme juuri määrittämiäsi jäljittimiä. Laita kaikki jäljittimet päällesi, näet mitkä ovat mitäkin oikealla olevassa kuvassa.
onboarding-automatic_mounting-put_trackers_on-next = Minulla on kaikki jäljittimet päällä
onboarding-automatic_mounting-return-home = Done

## Tracker manual proportions setupa

onboarding-manual_proportions-back-scaled = Go back to Scaled Proportions
onboarding-manual_proportions-title = Manuaaliset kehon mittasuhteet
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

onboarding-automatic_proportions-back = Go back to Manual Proportions
onboarding-automatic_proportions-title = Mittaa kehosi
onboarding-automatic_proportions-description = For SlimeVR trackers to work, we need to know the length of your bones. This short calibration will measure it for you.
onboarding-automatic_proportions-manual = Manuaaliset mittasuhteet
onboarding-automatic_proportions-prev_step = Edellinen vaihe
onboarding-automatic_proportions-put_trackers_on-title = Laita jäljittimet päällesi
onboarding-automatic_proportions-put_trackers_on-description = To calibrate your proportions, we're gonna use the trackers you just assigned. Put on all your trackers, you can see which are which in the figure to the right.
onboarding-automatic_proportions-put_trackers_on-next = Minulla on kaikki jäljittimet päällä
onboarding-automatic_proportions-requirements-title = Vaatimukset
# Each line of text is a different list item
onboarding-automatic_proportions-requirements-descriptionv2 =
    You have at least enough trackers to track your feet (generally 5 trackers).
    You have your trackers and headset on and are wearing them.
    Your trackers and headset are connected to the SlimeVR server and are working properly (ex. no stuttering, disconnecting, etc).
    Your headset is reporting positional data to the SlimeVR server (this generally means having SteamVR running and connected to SlimeVR using SlimeVR's SteamVR driver).
    Your tracking is working and is accurately representing your movements (ex. you have performed a full reset and they move the right direction when kicking, bending over, sitting, etc).
onboarding-automatic_proportions-requirements-next = Olen lukenut vaatimukset
onboarding-automatic_proportions-check_height-title-v3 = Measure your headset height
onboarding-automatic_proportions-check_height-description-v2 = Your headset (HMD) height should be slightly less than your full height because headsets measure your eye height. This measurement will be used as a baseline for your body proportions.
# All the text is in bold!
onboarding-automatic_proportions-check_height-calculation_warning-v3 = Start measuring while standing <u>upright</u> to measure your height. Be careful not to raise your hands higher than your headset, as they may affect the measurement!
onboarding-automatic_proportions-check_height-guardian_tip =
    If you are using a standalone VR headset, make sure to have your guardian /
    boundary turned on so that your height is correct!
# Context is that the height is unknown
onboarding-automatic_proportions-check_height-unknown = Tuntematon
# Shows an element below it
onboarding-automatic_proportions-check_height-hmd_height2 = Your headset height is:
onboarding-automatic_proportions-check_height-measure-start = Start measuring
onboarding-automatic_proportions-check_height-measure-stop = Stop measuring
onboarding-automatic_proportions-check_height-measure-reset = Retry measuring
onboarding-automatic_proportions-check_height-next_step = Ne ovat hyvät
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
onboarding-automatic_proportions-start_recording-title = Valmistaudu liikkumaan
onboarding-automatic_proportions-start_recording-description = Aiomme nyt tallentaa joitain tiettyä asentoja ja liikkeitä. Näitä kysytään seuraavassa näytössä. Ole valmis aloittamaan, kun painat nappia!
onboarding-automatic_proportions-start_recording-next = Aloita tallennus
onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = Tallennus käynnissä...
onboarding-automatic_proportions-recording-description-p1 = Tee alla esitetyt liikkeet:
# Each line of text is a different list item
onboarding-automatic_proportions-recording-steps =
    Standing up straight, roll your head in a circle.
    Bend your back forward and squat. While squatting, look to your left, then to your right.
    Twist your upper body to the left (counter-clockwise), then reach down toward the ground.
    Twist your upper body to the right (clockwise), then reach down toward the ground.
    Roll your hips in a circular motion as if you're using a hula hoop.
    If there is time left on the recording, you can repeat these steps until it's finished.
onboarding-automatic_proportions-recording-processing = Käsitellään tuloksia
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 20)
onboarding-automatic_proportions-recording-timer =
    { $time ->
        [one] 1 sekuntti jäljellä
       *[other] { $time } sekuntia jäljellä
    }
onboarding-automatic_proportions-verify_results-title = Vahvista tulokset
onboarding-automatic_proportions-verify_results-description = Tarkista alla olevat tulokset, näyttävätkö ne oikeilta?
onboarding-automatic_proportions-verify_results-results = Tallennuksen tulokset
onboarding-automatic_proportions-verify_results-processing = Käsitellään tuloksia
onboarding-automatic_proportions-verify_results-redo = Tee tallennus uudelleen
onboarding-automatic_proportions-verify_results-confirm = Nämä ovat oikein
onboarding-automatic_proportions-done-title = Keho mitattu ja tallennettu.
onboarding-automatic_proportions-done-description = Kehosi mittasuhteiden kalibrointi on valmis!
onboarding-automatic_proportions-error_modal-v2 =
    <b>Warning:</b> There was an error while estimating proportions!
    This is likely a mounting calibration issue. Make sure your tracking works properly before trying again.
    Please <docs>check the docs</docs> or join our <discord>Discord</discord> for help ^_^
onboarding-automatic_proportions-error_modal-confirm = Ymmäretty!
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

home-no_trackers = Jäjittimiä ei havaittu tai määritetty
home-settings = Home Page Settings
home-settings-close = Close

## Trackers Still On notification

trackers_still_on-modal-title = Jäljittimet ovat vielä päällä
trackers_still_on-modal-description =
    Yksi tai useampi jäljitin on edelleen päällä.
    Haluatko silti poistua SlimeVR:stä?
trackers_still_on-modal-confirm = Poistu SlimeVR:stä
trackers_still_on-modal-cancel = Odota...

## Status system

status_system-StatusTrackerReset = On suositeltavaa suorittaa täydellinen nollaus, koska yhtä tai useampaa jäljitintä ei ole säädetty.
status_system-StatusSteamVRDisconnected =
    { $type ->
        [steamvr_feeder] Tällä hetkellä ei ole yhdistetty SlimeVR Feeder -sovellukseen.
       *[other] Tällä hetkellä ei ole yhdistetty SteamVR:ään SlimeVR-ajurin kautta.
    }
status_system-StatusTrackerError = { $trackerName } jäljittimessä on virhe
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
tray_or_exit_modal-cancel = Cancel

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
