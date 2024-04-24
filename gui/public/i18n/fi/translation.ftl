# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Yhdistetään palvelimeen
websocket-connection_lost = Yhteys epäonnistui. Yritetään uudelleen...

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

## Proportions

skeleton_bone-NONE = Ei mikään
skeleton_bone-HEAD = Pään säätö
skeleton_bone-NECK = Kaulan pituus
skeleton_bone-torso_group = Vartalon pituus
skeleton_bone-UPPER_CHEST = Ylärinnan pituus
skeleton_bone-CHEST_OFFSET = Rinnan keskitys
skeleton_bone-CHEST = Rinnan pituus
skeleton_bone-WAIST = Vyötärön pituus
skeleton_bone-HIP = Lonkan pituus
skeleton_bone-HIP_OFFSET = Lonkan keskitys
skeleton_bone-HIPS_WIDTH = Lonkan leveys
skeleton_bone-leg_group = Jalan pituus
skeleton_bone-UPPER_LEG = Yläjalan pituus
skeleton_bone-LOWER_LEG = Säären pituus
skeleton_bone-FOOT_LENGTH = Jalkaterän pituus
skeleton_bone-FOOT_SHIFT = Jalkaterän säätö
skeleton_bone-SKELETON_OFFSET = Luurangon keskitys
skeleton_bone-SHOULDERS_DISTANCE = Olkapäiden etäisyys
skeleton_bone-SHOULDERS_WIDTH = Olkapäiden leveys
skeleton_bone-arm_group = Käsivarren pituus
skeleton_bone-UPPER_ARM = Olkavarren pituus
skeleton_bone-LOWER_ARM = Kyynärvarren pituus
skeleton_bone-HAND_Y = Käden Etäisyys Y
skeleton_bone-HAND_Z = Käden Etäisyys Z
skeleton_bone-ELBOW_OFFSET = Kyynärpään keskitys

## Tracker reset buttons

reset-reset_all = Nollaa kaikki mittasuhteet
reset-full = Täysinollaus
reset-mounting = Nollaa Asennus
reset-yaw = Nollaa Kallistuma

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

## Biovision hierarchy recording

bvh-start_recording = Tallenna BVH
bvh-recording = Tallennetaan...

## Tracking pause

tracking-unpaused = Keskeytä jäljitys
tracking-paused = Jatka jäljitystä

## Widget: Overlay settings

widget-overlay = Overlay
widget-overlay-is_visible_label = Näytä Overlay SteamVR:ssä
widget-overlay-is_mirrored_label = Näytä Overlay Peilinä

## Widget: Drift compensation

widget-drift_compensation-clear = Tyhjennä ajautumakompensaatio

## Widget: Clear Reset Mounting

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
widget-imu_visualizer-rotation_raw = Käsittelemätön
widget-imu_visualizer-rotation_preview = Esikatselu
widget-imu_visualizer-rotation_hide = Piilota

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
tracker-table-column-tps = TPS
tracker-table-column-temperature = Lämpötila °C
tracker-table-column-linear-acceleration = Kiihtyvyys X/Y/Z
tracker-table-column-rotation = Kierto X/Y/Z
tracker-table-column-position = Sijainti X/Y/Z
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
tracker-infos-imu = IMU-Sensor
tracker-infos-board_type = Päälevy

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
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Jäljittimen nimi
tracker-settings-name_section-description = Anna sille söpö lempinimi :)
tracker-settings-name_section-placeholder = NightyBeast vasen jalka

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
settings-sidebar-tracker_mechanics = Jäljittimen mekaniikat
settings-sidebar-fk_settings = Jäljityksen asetukset
settings-sidebar-gesture_control = Eleohjaus
settings-sidebar-interface = Käyttöliittymä
settings-sidebar-osc_router = OSC-reititin
settings-sidebar-osc_trackers = VRChat OSC-jäljittimet
settings-sidebar-utils = Lisäohjelmat
settings-sidebar-serial = Sarjakonsoli
settings-sidebar-appearance = Ulkonäkö
settings-sidebar-notifications = Ilmoitukset

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
settings-general-steamvr-trackers-feet = Jalat
settings-general-steamvr-trackers-knees = Polvet
settings-general-steamvr-trackers-elbows = Kyynärpäät
settings-general-steamvr-trackers-hands = Kädet

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
settings-general-tracker_mechanics-drift_compensation = Ajautumakompensaatio
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Kompensoi IMU-kääntymistä käyttämällä käänteistä kiertoa.
    Muuta kompensaation määrää ja kuinka monta nollausta otetaan huomioon.
settings-general-tracker_mechanics-drift_compensation-enabled-label = Ajautumakompensaatio
settings-general-tracker_mechanics-drift_compensation-amount-label = Kompensaation määrä
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Käytä enintään x viimeistä nollausta

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
settings-general-fk_settings-leg_fk-reset_mounting_feet-description = Ota käyttöön jalkojen asennuksen nollaus varpaillaan.
settings-general-fk_settings-leg_fk-reset_mounting_feet = Jalkojen asennuksen nollaus
settings-general-fk_settings-arm_fk = Käsivarsien jäljitys
settings-general-fk_settings-arm_fk-description = Muuta tapaa, jolla käsivarsia jäljitetään.
settings-general-fk_settings-arm_fk-force_arms = Pakota kädet HMD:ltä
settings-general-fk_settings-arm_fk-reset_mode-description = Muuta, mikä käsivarren asentoa odotetaan asennuksen nollaukselle.
settings-general-fk_settings-arm_fk-back = Takaisin
settings-general-fk_settings-arm_fk-tpose_up = T-asento (ylös)
settings-general-fk_settings-arm_fk-tpose_down = T-asento (alas)
settings-general-fk_settings-arm_fk-forward = Eteenpäin
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
settings-general-fk_settings-self_localization-title = Mocap-tila
settings-general-fk_settings-self_localization-description = Mocap-tila sallii luurangon karkeasti seurata omaa sijaintiaan ilman laseja tai muita jäljittimiä. Huomioi, että tämä vaatii jalka- ja pääjäljittimien toimimista ja on vielä kokeellinen.
settings-general-fk_settings-vive_emulation-title = Vive-emulointi
settings-general-fk_settings-vive_emulation-description = Emuloi vyötäröjäljittimen ongelmia, joita Vive jäljittimillä on. Tämä on vitsi ja pahentaa jäljitystä.
settings-general-fk_settings-vive_emulation-label = Ota Vive-emulointi käyttöön

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
settings-general-gesture_control-fullResetEnabled = Ota käyttöön täysinollaus napautus
settings-general-gesture_control-fullResetDelay = Täysinollaus viive
settings-general-gesture_control-fullResetTaps = Napautuksia täysinollaukseen
settings-general-gesture_control-mountingResetEnabled = Ota käyttöön asennusnollaus napautus
settings-general-gesture_control-mountingResetDelay = Asennusnollaus viive
settings-general-gesture_control-mountingResetTaps = Napautuksia asennusnollaukseen
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = Jäljittimet yli kynnysarvon
settings-general-gesture_control-numberTrackersOverThreshold-description = Suurenna tätä arvoa jos napautustunnistus ei toimi. Älä suurenna yli sen, mikä on tarpeellista, koska se voi aiheuttaa enemmän virheitä.

## Appearance settings

settings-interface-appearance = Ulkonäkö
settings-general-interface-dev_mode = Kehittäjätila
settings-general-interface-dev_mode-description = Tämä tila voi olla hyödyllinen, jos tarvitset perusteellisia tietoja tai haluat olla tekemisissä yhdistettyjen jäljittimien kanssa edistyneemmällä tasolla.
settings-general-interface-dev_mode-label = Kehittäjätila
settings-general-interface-theme = Väri teema
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
settings-serial-get_infos = Hanki tietoja
settings-serial-serial_select = Valitse sarjaportti
settings-serial-auto_dropdown_item = Autom.

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
settings-osc-vrchat-description =
    Muuta VRChat-kohtaisia asetuksia vastaanottamaan HMD-dataa ja
    lähettämään jäljitindataa FBT:tä varten (toimii Quest-standalone:ssa).
settings-osc-vrchat-enable = Käytä
settings-osc-vrchat-enable-description = Vaihda tietojen lähettäminen ja vastaanottaminen.
settings-osc-vrchat-enable-label = Käytä
settings-osc-vrchat-network = Verkkoportit
settings-osc-vrchat-network-description = Aseta portit kuuntelua ja tietojen lähettämistä varten VRChatiin.
settings-osc-vrchat-network-port_in =
    .label = Portti sisään
    .placeholder = Portti sisään (oletus: 9001)
settings-osc-vrchat-network-port_out =
    .label = Portti ulos
    .placeholder = Portti ulos (oletus: 9000)
settings-osc-vrchat-network-address = Verkon osoite
settings-osc-vrchat-network-address-description = Valitse, mikä osoite lähettää tietoja VRChatiin (tarkista laitteesi Wi-Fi-asetukset).
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
settings-osc-vmc-vrm-model_unloaded = Mallia ei ole ladattu
settings-osc-vmc-vrm-model_loaded =
    { $titled ->
        [true] Malli ladattu: { $name }
       *[other] Nimetön malli ladattu
    }
settings-osc-vmc-vrm-file_select = Vedä ja pudota mallia käytettäväksi tai <u>selaa</u>
settings-osc-vmc-anchor_hip = Ankkuri lantiolla
settings-osc-vmc-anchor_hip-description = Ankkuroi jäljitin lonkalle, hyödyllinen istuvaan VTubing. Jos poistat käytöstä, lataa VRM-malli.
settings-osc-vmc-anchor_hip-label = Ankkuroi lonkalle

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

## Wi-Fi setup

onboarding-wifi_creds-back = Palaa esittelyyn
onboarding-wifi_creds = Syötä Wi-Fi-tunnistetiedot
# This cares about multilines
onboarding-wifi_creds-description =
    Jäljittimet käyttävät näitä tunnistetietoja langattomaan yhteyden muodostamiseen.
    Käytä tunnistetietoja, joihin olet tällä hetkellä yhteydessä.
onboarding-wifi_creds-skip = Ohita Wi-Fi-asetukset
onboarding-wifi_creds-submit = Lähetä!
onboarding-wifi_creds-ssid =
    .label = Wi-Fi nimi
    .placeholder = Syötä Wi-Fi nimi
onboarding-wifi_creds-password =
    .label = Salasana
    .placeholder = Syötä salasana

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

## Setup start

onboarding-home = Tervetuloa SlimeVR:ään
onboarding-home-start = Mennään asentamaan!

## Enter VR part of setup

onboarding-enter_vr-back = Palaa jäljittimien määritykseen
onboarding-enter_vr-title = Aika astua VR:ään!
onboarding-enter_vr-description = Laita kaikki jäljittimet päälle ja astu VR:ään!
onboarding-enter_vr-ready = Olen valmis

## Setup done

onboarding-done-title = Olet valmis!
onboarding-done-description = Nauti täysikehojäljityksestäsi
onboarding-done-close = Sulje opas

## Tracker connection setup

onboarding-connect_tracker-back = Palaa Wi-Fi-tunnistetietoihin
onboarding-connect_tracker-title = Yhdistä jäljittimet
onboarding-connect_tracker-description-p0 = Nyt hauskaan osaan, kaikkien jäljittimien yhdistämiseen!
onboarding-connect_tracker-description-p1 = Yhdistä vain kaikki, joita ei ole vielä yhdistetty, USB-portin kautta.
onboarding-connect_tracker-issue-serial = Minulla on ongelmia yhteyden muodostamisessa!
onboarding-connect_tracker-usb = USB-jäljitin
onboarding-connect_tracker-connection_status-none = Etsitään jäljittimiä
onboarding-connect_tracker-connection_status-serial_init = Yhdistetään sarjalaitteeseen
onboarding-connect_tracker-connection_status-provisioning = Lähetetään Wi-Fi-tunnistetietoja
onboarding-connect_tracker-connection_status-connecting = Yritetään muodostaa yhteys Wi-Fi-verkkoon
onboarding-connect_tracker-connection_status-looking_for_server = Etsitään palvelinta
onboarding-connect_tracker-connection_status-connection_error = Wi-Fi-yhteyden muodostaminen epäonnistui
onboarding-connect_tracker-connection_status-could_not_find_server = Palvelinta ei löytynyt
onboarding-connect_tracker-connection_status-done = Yhdistetty palvelimeen
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
onboarding-calibration_tutorial-description = Joka kerta, kun käynnistät jäljittimet, niiden täytyy levätä hetken tasaisella alustalla kalibroidakseen. Tehdään sama asia painamalla "{ onboarding-calibration_tutorial-calibrate }" nappia, <b>älä liikuta niitä!</b>
onboarding-calibration_tutorial-calibrate = Asetin jäljittimeni pöydälle
onboarding-calibration_tutorial-status-waiting = Odotetaan sinua
onboarding-calibration_tutorial-status-calibrating = Kalibroi
onboarding-calibration_tutorial-status-success = Kiva!
onboarding-calibration_tutorial-status-error = Jäljitintä liikutettiin

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
# Italized text
onboarding-choose_mounting-auto_mounting-label = Kokeellinen
onboarding-choose_mounting-auto_mounting-description = Tämä tunnistaa automaattisesti kaikkien jäljittimiesi asennussuunnat 2 asennosta
onboarding-choose_mounting-manual_mounting = Manuaalinen asennus
# Italized text
onboarding-choose_mounting-manual_mounting-label = Suositeltu
onboarding-choose_mounting-manual_mounting-description = Näin voit valita asennussuunnan manuaalisesti kullekin jäljittimelle
# Multiline text
onboarding-choose_mounting-manual_modal-title =
    Oletko varma, että haluat tehdä
    automaattisen asennuksen kalibroinnin?
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
onboarding-automatic_mounting-preparation-title = Valmistelu
onboarding-automatic_mounting-preparation-step-0 = 1. Seiso pystyssä kädet sivuilla.
onboarding-automatic_mounting-preparation-step-1 = 2. Paina "Täysinollaus" -painiketta ja odota 3 sekuntia, ennen kuin jäljittimet nollautuvat.
onboarding-automatic_mounting-put_trackers_on-title = Laita jäljittimet päällesi
onboarding-automatic_mounting-put_trackers_on-description = Kalibroidaksemme asennuskierrokset käytämme juuri määrittämiäsi jäljittimiä. Laita kaikki jäljittimet päällesi, näet mitkä ovat mitäkin oikealla olevassa kuvassa.
onboarding-automatic_mounting-put_trackers_on-next = Minulla on kaikki jäljittimet päällä

## Tracker proportions method choose

onboarding-choose_proportions = Mitä kalibrointimenetelmää käytetään?
onboarding-choose_proportions-auto_proportions = Automaattiset mittasuhteet
onboarding-choose_proportions-manual_proportions = Manuaaliset mittasuhteet
onboarding-choose_proportions-import-failed = Epäonnistui
onboarding-choose_proportions-file_type = Kehon mittasuhteet -tiedosto

## Tracker manual proportions setup

onboarding-manual_proportions-title = Manuaaliset kehon mittasuhteet
onboarding-manual_proportions-precision = Tarkka säätö
onboarding-manual_proportions-auto = Automaattiset mittasuhteet
onboarding-manual_proportions-ratio = Säädä suhderyhmien mukaan

## Tracker automatic proportions setup

onboarding-automatic_proportions-title = Mittaa kehosi
onboarding-automatic_proportions-manual = Manuaaliset mittasuhteet
onboarding-automatic_proportions-prev_step = Edellinen vaihe
onboarding-automatic_proportions-put_trackers_on-title = Laita jäljittimet päällesi
onboarding-automatic_proportions-put_trackers_on-next = Minulla on kaikki jäljittimet päällä
onboarding-automatic_proportions-requirements-title = Vaatimukset
onboarding-automatic_proportions-requirements-next = Olen lukenut vaatimukset
onboarding-automatic_proportions-check_height-title = Tarkista pituutesi
onboarding-automatic_proportions-check_height-description = Käytämme pituuttasi mittaustemme perustana käyttämällä HMD:n pituutta likiarvona todellisesta pituudestasi, mutta on parempi tarkistaa itse, ovatko ne oikein!
# All the text is in bold!
onboarding-automatic_proportions-check_height-calculation_warning = Paina painiketta <u>pystyasennossa</u> laskeaksesi pituutesi. Sinulla on 3 sekuntia painikkeen painamisen jälkeen!
onboarding-automatic_proportions-check_height-fetch_height = Seison
# Context is that the height is unknown
onboarding-automatic_proportions-check_height-unknown = Tuntematon
# Shows an element below it
onboarding-automatic_proportions-check_height-hmd_height1 = HMD-korkeus on
# Shows an element below it
onboarding-automatic_proportions-check_height-height1 = Joten todellinen pituutesi on
onboarding-automatic_proportions-check_height-next_step = Ne ovat hyvät
onboarding-automatic_proportions-start_recording-title = Valmistaudu liikkumaan
onboarding-automatic_proportions-start_recording-description = Aiomme nyt tallentaa joitain tiettyä asentoja ja liikkeitä. Näitä kysytään seuraavassa näytössä. Ole valmis aloittamaan, kun painat nappia!
onboarding-automatic_proportions-start_recording-next = Aloita tallennus
onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = Tallennus käynnissä...
onboarding-automatic_proportions-recording-description-p1 = Tee alla esitetyt liikkeet:
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
onboarding-automatic_proportions-error_modal =
    <b>Varoitus:</b> Mittasuhteita arvioitaessa havaittiin virhe!
     <docs>Tarkista dokumentit</docs> tai liity <discord>Discordiin</discord> saadaksesi apua ^_^
onboarding-automatic_proportions-error_modal-confirm = Ymmäretty!

## Home

home-no_trackers = Jäjittimiä ei havaittu tai määritetty

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
