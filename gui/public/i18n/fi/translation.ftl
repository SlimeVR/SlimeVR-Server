### SlimeVR complete GUI translations


# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Yhdistetään palvelimeen
websocket-connection_lost = Yhteys epäonnistui. Yritetään uudelleen...

## Tips

tips-find_tracker = Epävarma, mikä jäljitin on mikä? Ravista jäljitintä ja se korostaa vastaavan kohdan.
tips-do_not_move_heels = Varmista, että kantapääsi ei liiku tallennuksen aikana!

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
skeleton_bone-CHEST = Rinnan pituus
skeleton_bone-CHEST_OFFSET = Rinnan keskitys
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

## Bounding volume hierarchy recording

bvh-start_recording = Tallenna BVH
bvh-recording = Tallennetaan...

## Widget: Overlay settings

widget-overlay = Overlay
widget-overlay-is_visible_label = Näytä Overlay SteamVR:ssä
widget-overlay-is_mirrored_label = Näytä Overlay Peilinä

## Widget: Drift compensation

widget-drift_compensation-clear = Tyhjennä ajautumakompensaatio

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

## Tracker status

tracker-status-none = Ei tilaa
tracker-status-busy = Varattu
tracker-status-error = Virhe
tracker-status-disconnected = Katkaistu
tracker-status-occluded = Peittynyt
tracker-status-ok = OK

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
tracker-rotation-left = Vasen
tracker-rotation-right = Oikea
tracker-rotation-back = Taka

## Tracker information

tracker-infos-manufacturer = Valmistaja
tracker-infos-display_name = Näyttönimi
tracker-infos-custom_name = Mukautettu Nimi
tracker-infos-url = Jäljittimen URL
tracker-infos-version = Laiteohjelmiston Versio
tracker-infos-hardware_rev = Laitteston Tarkistus

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
settings-sidebar-utils = Lisäohjelmat
settings-sidebar-serial = Sarjakonsoli

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
settings-general-fk_settings-arm_fk = Käsivarsien jäljitys
settings-general-fk_settings-arm_fk-description = Muuta tapaa, jolla käsivarsia jäljitetään.
settings-general-fk_settings-arm_fk-force_arms = Pakota kädet HMD:ltä
settings-general-fk_settings-skeleton_settings = Luurangon asetukset
settings-general-fk_settings-skeleton_settings-description = Ota tai poista käytöstä luurankoasetukset. On suositeltavaa jättää nämä päälle.
settings-general-fk_settings-skeleton_settings-extended_spine = Laajennettu selkäranka
settings-general-fk_settings-skeleton_settings-extended_pelvis = Laajennettu lantioluu
settings-general-fk_settings-skeleton_settings-extended_knees = Laajennettu polvi
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
settings-general-gesture_control-yawResetEnabled = Ota käyttöön kallistumanollaus napautus
settings-general-gesture_control-yawResetDelay = Kallistumanollaus viive
settings-general-gesture_control-yawResetTaps = Napautuksia kallistumanollaukseen.
settings-general-gesture_control-fullResetEnabled = Ota käyttöön täysinollaus napautus
settings-general-gesture_control-fullResetDelay = Täysinollaus viive
settings-general-gesture_control-fullResetTaps = Napautuksia täysinollaukseen
settings-general-gesture_control-mountingResetEnabled = Ota käyttöön asennusnollaus napautus
settings-general-gesture_control-mountingResetDelay = Asennusnollaus viive
settings-general-gesture_control-mountingResetTaps = Napautuksia asennusnollaukseen

## Interface settings

settings-general-interface = Käyttöliittymä
settings-general-interface-dev_mode = Kehittäjätila
settings-general-interface-dev_mode-description = Tämä tila voi olla hyödyllinen, jos tarvitset perusteellisia tietoja tai haluat olla tekemisissä yhdistettyjen jäljittimien kanssa edistyneemmällä tasolla.
settings-general-interface-dev_mode-label = Kehittäjätila
settings-general-interface-serial_detection = Sarjalaitteen tunnistus
settings-general-interface-serial_detection-description = Tämä vaihtoehto näyttää ponnahdusikkunan aina, kun liität uuden sarjalaitteen, joka voi olla jäljitin. Se auttaa parantamaan jäljittimen asennusprosessia.
settings-general-interface-serial_detection-label = Sarjalaitteen tunnistus
settings-general-interface-feedback_sound = Palaute ääni
settings-general-interface-feedback_sound-description = Tämä asetus toistaa äänen nollauksen tapahtuessa.
settings-general-interface-feedback_sound-label = Palaute ääni
settings-general-interface-feedback_sound-volume = Palaute äänen voimakkuus
settings-general-interface-theme = Väri teema
settings-general-interface-lang = Valitse kieli
settings-general-interface-lang-description = Vaihda oletuskieli, jota haluat käyttää.
settings-general-interface-lang-placeholder = Valitse käytettävä kieli

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
settings-osc-vmc-anchor_hip = Ankkuri lantiolla

## Setup/onboarding menu

onboarding-skip = Ohita asennus
onboarding-continue = Jatka
onboarding-wip = Keskeneräinen

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
onboarding-reset_tutorial-description = Tämä ominaisuus ei ole valmis, paina vain Jatka

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

## Tracker assignment setup


## Tracker assignment warnings


## Tracker mounting method choose


## Tracker manual mounting setup


## Tracker automatic mounting setup


## Tracker proportions method choose


## Tracker manual proportions setup


## Tracker automatic proportions setup


## Home

