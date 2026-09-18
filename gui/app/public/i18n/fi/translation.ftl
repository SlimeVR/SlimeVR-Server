# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Yhdistetään palvelimeen
websocket-connection_lost = Yhteys epäonnistui. Yritetään uudelleen...
websocket-error-close = Poistu SlimeVR:stä

## Update notification

version_update-title = Uusi versio saatavilla: { $version }
version_update-description = Valitsemalla "{ version_update-update }" lataa SlimeVR-asennusohjelman.
version_update-update = Päivitys
version_update-close = Sulje

## Tips

tips-find_tracker = Epävarma, mikä jäljitin on mikä? Ravista jäljitintä ja se korostaa vastaavan kohdan.
tips-do_not_move_heels = Varmista, että kantapääsi ei liiku tallennuksen aikana!
tips-file_select = Vedä ja pudota käytettäviä tiedostoja tai <u>selaa</u>.

## Units


## Dropdown


## Text input


## File input


## Window controls

titlebar-close = Sulje

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
body_part-HIP = Lonkka
body_part-LEFT_SHOULDER = Vasen olkapää
body_part-LEFT_UPPER_ARM = Vasen olkavarsi
body_part-LEFT_LOWER_ARM = Vasen kyynärvarsi
body_part-LEFT_HAND = Vasen käsi
body_part-LEFT_UPPER_LEG = Vasen reisi
body_part-LEFT_LOWER_LEG = Vasen nilkka
body_part-LEFT_FOOT = Vasen jalkaterä

## BoardType

board_type-UNKNOWN = Tuntematon

## Proportions

skeleton_bone-NONE = Ei mikään
skeleton_bone-HEAD = Pään säätö
skeleton_bone-NECK = Kaulan pituus
skeleton_bone-torso_group = Vartalon pituus
skeleton_bone-UPPER_CHEST = Ylärinnan pituus
skeleton_bone-HIP = Lonkan pituus
skeleton_bone-HIPS_WIDTH = Lonkan leveys
skeleton_bone-leg_group = Jalan pituus
skeleton_bone-UPPER_LEG = Yläjalan pituus
skeleton_bone-LOWER_LEG = Säären pituus
skeleton_bone-FOOT_LENGTH = Jalkaterän pituus
skeleton_bone-FOOT_SHIFT = Jalkaterän säätö
skeleton_bone-SHOULDERS_DISTANCE = Olkapäiden etäisyys
skeleton_bone-SHOULDERS_WIDTH = Olkapäiden leveys
skeleton_bone-arm_group = Käsivarren pituus
skeleton_bone-UPPER_ARM = Olkavarren pituus
skeleton_bone-LOWER_ARM = Kyynärvarren pituus
skeleton_bone-HAND_Y = Käden Etäisyys Y
skeleton_bone-HAND_Z = Käden Etäisyys Z

## Tracker reset buttons

reset-reset_all = Nollaa kaikki mittasuhteet
reset-reset_all_warning-cancel = Peruuta
reset-full = Täysinollaus
reset-mounting = Asennuksen Nollaus
reset-yaw = Nollaa Kallistuma

## Navigation bar

navbar-home = Aloitus
navbar-body_proportions = Kehon Mittasuhteet
navbar-trackers_assign = Jäljittimien Määritys
navbar-mounting = Asennuksen Nollaus
navbar-onboarding = Asennustoiminto
navbar-settings = Asetukset

## Biovision hierarchy recording

bvh-start_recording = Tallenna BVH
bvh-recording = Tallennetaan...

## Tracking pause

tracking-unpaused = Keskeytä jäljitys
tracking-paused = Jatka jäljitystä

## Widget: Developer settings

widget-developer_mode = Kehittäjätila
widget-developer_mode-high_contrast = Suuri kontrasti
widget-developer_mode-precise_rotation = Tarkka kierto
widget-developer_mode-fast_data_feed = Nopea tietosyöte
widget-developer_mode-raw_slime_rotation = Käsittelemätön

## Widget: IMU Visualizer

widget-imu_visualizer = Kierto
widget-imu_visualizer-hide = Piilota
widget-imu_visualizer-rotation_raw = Käsittelemätön
widget-imu_visualizer-rotation_preview = Esikatselu

## Tracker status

tracker-status-none = Ei tilaa
tracker-status-busy = Varattu
tracker-status-error = Virhe
tracker-status-disconnected = Katkaistu
tracker-status-occluded = Peittynyt
tracker-status-timed_out = Aikakatkaistiin

## Tracker status columns

tracker-table-column-name = Nimi
tracker-table-column-type = Tyyppi
tracker-table-column-battery = Akkuvirta
tracker-table-column-temperature = Lämpötila °C
tracker-table-column-linear-acceleration = Kiihtyvyys X/Y/Z
tracker-table-column-rotation = Kierto X/Y/Z
tracker-table-column-position = Sijainti X/Y/Z

## Tracker rotation

tracker-rotation-front = Etu
tracker-rotation-front_left = Etu-vasen
tracker-rotation-front_right = Etu-oikea
tracker-rotation-left = Vasen
tracker-rotation-right = Oikea
tracker-rotation-back = Takaisin
tracker-rotation-back_left = Taka-vasen
tracker-rotation-back_right = Taka-oikea
tracker-rotation-custom = Mukautettu

## Tracker information

tracker-infos-manufacturer = Valmistaja
tracker-infos-display_name = Näyttönimi
tracker-infos-custom_name = Mukautettu Nimi
tracker-infos-url = Jäljittimen URL
tracker-infos-hardware_identifier = Laitteiston ID
tracker-infos-imu = IMU-Sensor
tracker-infos-board_type = Päälevy
tracker-infos-network_version = Protokollan versio

## Tracker settings

tracker-settings-back = Palaa jäljittimien luetteloon
tracker-settings-title = Jäljittimien asetukset
tracker-settings-assignment_section = Määritys
tracker-settings-assignment_section-description = Mihin kehon osaan jäljitin on määritetty.
tracker-settings-assignment_section-edit = Muokkaa määritystä
tracker-settings-mounting_section = Asennusasento
tracker-settings-mounting_section-description = Mihin jäljitin on asennettu?
tracker-settings-mounting_section-edit = Muokkaa asennusta
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Jäljittimen nimi
tracker-settings-name_section-placeholder = NightyBeast vasen jalka
tracker-settings-name_section-label = Jäljittimen nimi

## Dongle settings

dongle-infos-hardware_revision = Laitteston Tarkistus
dongle-status-disconnected = Katkaistu
dongle-settings-back = Palaa jäljittimien luetteloon

## Tracker part card info

tracker-part_card-unassigned = Ei määritetty

## Body assignment menu

body_assignment_menu = Missä haluat tämän jäljittimen olevan?
body_assignment_menu-description = Valitse sijainti, johon haluat määrittää tämän jäljittimen. Vaihtoehtoisesti voit valita, haluatko hallita kaikkia jäljittimiä kerralla yhden sijaan.
body_assignment_menu-manage_trackers = Hallitse kaikkia jäljittimiä
body_assignment_menu-unassign_tracker = Poista jäljittimen määritys

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Varoitus:</b> Kaulan jäljitin voi olla tappava jos säädetty liian tiukasti,
    hihna voi katkaista verenkierron päähän!
tracker_selection_menu-neck_warning-done = Ymmärrän riskit
tracker_selection_menu-neck_warning-cancel = Peruuta

## Mounting menu

mounting_selection_menu-close = Sulje

## Sidebar settings

settings-sidebar-title = Asetukset
settings-sidebar-general = Yleistä
settings-sidebar-trackers = Jäljittimet
settings-sidebar-interface = Käyttöliittymä
settings-sidebar-utils = Lisäohjelmat
settings-sidebar-appearance = Ulkonäkö
settings-sidebar-notifications = Ilmoitukset

## Bone routing settings

settings-routing-hands-warning-cancel = Peruuta

## SteamVR / Monado output settings

settings-driver-enable = Käytä

## Tracker mechanics

settings-general-tracker_mechanics-filtering = Suodatus
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Valitse suodatustyyppi jäljittimillesi.
    Ennustus ennustaa liikettä, kun taas tasoitus tasoittaa liikettä.
settings-general-tracker_mechanics-filtering-type-none = Ei suodatusta
settings-general-tracker_mechanics-filtering-type-none-description = Käytä kiertoja sellaisenaan. Ei tee mitään suodatusta.
settings-general-tracker_mechanics-filtering-type-smoothing = Tasoitus
settings-general-tracker_mechanics-filtering-type-smoothing-description = Tasoittaa liikettä, mutta lisää hieman viivettä.
settings-general-tracker_mechanics-filtering-type-prediction = Ennustus
settings-general-tracker_mechanics-filtering-type-prediction-description = Vähentää viivettä ja tekee liikeistä näppärämpiä, mutta voi lisätä värinää.
settings-stay_aligned-general-label = Yleistä
settings-stay_aligned-relaxed_poses-close = Sulje

## Keybinds Page

settings-keybinds_full-reset = Täysinollaus
settings-keybinds_yaw-reset = Nollaa Kallistuma
settings-keybinds-recorder-modal-cancel-button = Peruuta

## FK/Tracking settings

settings-general-fk_settings-leg_tweak-skating_correction-amount = Skating correction vahvuus
settings-general-fk_settings-leg_tweak-skating_correction-description = Skating correction helpottaa jalkojen luistelua, mutta voi heikentää tarkkuutta tietyissä liikekuvioissa. Kun otat käyttöön, muista tehdä täysi nollaus ja kalibroida uudelleen pelissä.
settings-general-fk_settings-leg_tweak-floor_clip-description = Floor clip voi vähentää tai korjata jalan kulun lattian läpi. Kun otat käyttöön, muista tehdä täysi nollaus ja kalibroida uudelleen pelissä.
settings-general-fk_settings-leg_tweak-toe_snap-description = Toe snap yrittää arvata varpaiden asennon jos jalkaterän jäljitintä ei ole käytössä.
settings-general-fk_settings-leg_tweak-foot_plant-description = Foot plant asettaa jalkateräsi yhdensuuntaisesti maan kanssa kosketuksessa.
settings-general-fk_settings-leg_fk = Jalkojen jäljitys
settings-general-fk_settings-arm_fk-back = Takaisin
settings-general-fk_settings-arm_fk-tpose_up = T-asento (ylös)
settings-general-fk_settings-arm_fk-tpose_down = T-asento (alas)
settings-general-fk_settings-arm_fk-forward = Eteenpäin
settings-general-fk_settings-skeleton_settings-ratios = Luurankosuhteet
settings-general-fk_settings-skeleton_settings-ratios-description = Muuta luurankoasetusten arvoja. Saatat joutua säätämään mittasuhteitasi muutosten jälkeen.
settings-general-fk_settings-self_localization-title = Mocap-tila

## Gesture control settings (tracker tapping)

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
settings-general-interface-feedback_sound = Palaute ääni
settings-general-interface-feedback_sound-description = Tämä asetus toistaa äänen nollauksen tapahtuessa.
settings-general-interface-feedback_sound-label = Palaute ääni
settings-general-interface-feedback_sound-volume = Palaute äänen voimakkuus
settings-general-interface-connected_trackers_warning = Yhdistettyjen jäljittimien varoitus
settings-general-interface-connected_trackers_warning-description = Tämä vaihtoehto näyttää ponnahdusikkunan aina, kun yrität poistua SlimeVR:stä, kun sinulla on yksi tai useampi yhdistetty jäljitin. Se muistuttaa sinua sammuttamaan jäljittimet, kun olet valmis, akun käyttöiän säästämiseksi.

## Behavior settings

settings-general-interface-dev_mode = Kehittäjätila
settings-general-interface-dev_mode-label = Kehittäjätila
settings-general-interface-use_tray = Pienennä ilmaisinalueelle
settings-general-interface-use_tray-description = Voit sulkea ikkunan sulkematta SlimeVR-palvelinta, jotta voit jatkaa sen käyttöä ilman, että graafinen käyttöliittymä häiritsee sinua.
settings-general-interface-use_tray-label = Pienennä ilmaisinalueelle
settings-general-interface-discord_presence = Jaa toiminta Discordissa
settings-general-interface-discord_presence-description = Kertoo Discordille, että käytät SlimeVR:ää, sekä käyttämiesi IMU-seurantalaitteiden määrän.
settings-general-interface-discord_presence-label = Jaa toiminta Discordissa

## Serial settings

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
settings-serial-save_logs = Tallenna tiedostoon
settings-serial-send_command-warning-ok = Tiedän mitä teen
settings-serial-send_command-warning-cancel = Peruuta

## OSC VRChat settings

settings-osc-vrchat-enable = Käytä
settings-osc-vrchat-enable-description = Vaihda tietojen lähettäminen ja vastaanottaminen.
settings-osc-vrchat-enable-label = Käytä
settings-osc-vrchat-network = Verkkoportit
settings-osc-vrchat-network-port_in =
    .label = Portti sisään
    .placeholder = Portti sisään (oletus: 9001)
settings-osc-vrchat-network-port_out =
    .label = Portti ulos
    .placeholder = Portti ulos (oletus: 9000)
settings-osc-vrchat-network-address = Verkon osoite
settings-osc-vrchat-network-address-description-v1 = Valitse, mihin osoitteeseen tiedot lähetetään. Voidaan jättää koskematta VRChatille.
settings-osc-vrchat-network-address-placeholder = VRChat IP-osoite

## VRChat OSC status


## VMC OSC settings


## Common OSC settings


## Advanced settings


## Home Screen


## Tracking Checklist


## Setup/onboarding menu


## Quiz


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

