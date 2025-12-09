# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Připojování k serveru
websocket-connection_lost = Ztraceno spojení se serverem. Pokouším se znovu připojit...
websocket-connection_lost-desc = Vypadá to že SlimeVR server spadl. Zkontrolujte záznamy protokolů a restartuje aplikaci
websocket-timedout = Nelze se připojit k serveru
websocket-timedout-desc = Vypadá to že buď vypršel časový limit SlimeVR serveru, a nebo došlo k zhroucení. Zkontrolujte záznamy protokolů a restartuje aplikaci
websocket-error-close = Ukončit SlimeVR
websocket-error-logs = Otevření složku s záznamy protokolů

## Update notification

version_update-title = K dispozici je nová verze: { $version }
version_update-description = Kliknutím na "{ version_update-update }", stáhnete instalační program SlimeVR.
version_update-update = Aktualizace
version_update-close = Zavřít

## Tips

tips-find_tracker = Nejste si jisti, který tracker je který? Zatřeste tracker a zvýrazní se odpovídající položka.
tips-do_not_move_heels = Během nahrávání se ujistěte, že se vaše paty nepohybují!
tips-file_select = Nahrajte soubory přetažením zde, nebo tlačítkem <u>procházet</u>
tips-tap_setup = Pro výběr trackeru na něj můžete dvakrát pomalu poklepat, místo výběru z nabídky.
tips-turn_on_tracker = Máte oficiální SlimeVR trackery? <b><em>Po připojení k PC je nezapomeňte zapnout!</em></b>
tips-failed_webgl = Načtení WebGL selhalo.

## Units


## Body parts

body_part-NONE = Nepřiřazeno
body_part-HEAD = Hlava
body_part-NECK = Krk
body_part-RIGHT_SHOULDER = Pravé rameno
body_part-RIGHT_UPPER_ARM = Pravé nadloktí
body_part-RIGHT_LOWER_ARM = Pravé podloktí
body_part-RIGHT_HAND = Pravá ruka
body_part-RIGHT_UPPER_LEG = Pravé stehno
body_part-RIGHT_LOWER_LEG = Pravý kotník
body_part-RIGHT_FOOT = Pravá noha
body_part-UPPER_CHEST = Horní část hrudníku
body_part-CHEST = Hrudník
body_part-WAIST = Pás
body_part-HIP = Kyčel
body_part-LEFT_SHOULDER = Levé rameno
body_part-LEFT_UPPER_ARM = Levé nadloktí
body_part-LEFT_LOWER_ARM = Levé podloktí
body_part-LEFT_HAND = Levá ruka
body_part-LEFT_UPPER_LEG = Levé stehno
body_part-LEFT_LOWER_LEG = Levý kotník
body_part-LEFT_FOOT = Levá noha
body_part-LEFT_THUMB_DISTAL = Vzdálená falanga levého palce
body_part-LEFT_INDEX_DISTAL = Vzálená kůstka levého ukazováku
body_part-LEFT_MIDDLE_DISTAL = Vzálená kůstka levého prostředníku
body_part-LEFT_RING_DISTAL = Vzálená kůstka levého prsteníku
body_part-RIGHT_THUMB_DISTAL = Vzálená falanga pravého pacle

## BoardType

board_type-UNKNOWN = Neznámý
board_type-NODEMCU = NodeMCU
board_type-CUSTOM = Vlastní deska
board_type-WROOM32 = WROOM32
board_type-WEMOSD1MINI = Wemos D1 Mini
board_type-TTGO_TBASE = TTGO T-Base
board_type-ESP01 = ESP-01
board_type-SLIMEVR = SlimeVR
board_type-LOLIN_C3_MINI = Lolin C3 Mini
board_type-BEETLE32C3 = Beetle ESP32-C3
board_type-ESP32C3DEVKITM1 = Espressif ESP32-C3 DevKitM-1
board_type-OWOTRACK = owoTrack
board_type-WRANGLER = Wrangler Joycony
board_type-MOCOPI = Sony Mocopi
board_type-WEMOSWROOM02 = Wemos Wroom-02 D1 Mini
board_type-XIAO_ESP32C3 = Seeed Studio XIAO ESP32C3
board_type-HARITORA = Haritora
board_type-ESP32C6DEVKITC1 = Espressif ESP32-C6 DevKitC-1
board_type-GLOVE_IMU_SLIMEVR_DEV = SlimeVR vývojářská IMU rukavice

## Proportions

skeleton_bone-NONE = Žádný
skeleton_bone-HEAD = Posun hlavy
skeleton_bone-NECK = Délka krku
skeleton_bone-torso_group = Délka trupu
skeleton_bone-UPPER_CHEST = Horní délka hrudníku
skeleton_bone-CHEST_OFFSET = Odsazení hrudníku
skeleton_bone-CHEST = Délka hrudníku
skeleton_bone-WAIST = Délka pasu
skeleton_bone-HIP = Délka kyčlí
skeleton_bone-HIP_OFFSET = Odsazení hrudníku
skeleton_bone-HIPS_WIDTH = Šířka kyčlí
skeleton_bone-leg_group = Délka nohy
skeleton_bone-UPPER_LEG = Délka horní části nohy
skeleton_bone-LOWER_LEG = Délka dolní části nohy
skeleton_bone-FOOT_LENGTH = Délka chodidla
skeleton_bone-FOOT_LENGTH-desc =
    Toto je vzdálenost mezi vaši kotníky a prsty na nohou.
    Pro upravení, Chodtě po špičkách dokud vaše virtuální nohy nezůstanou na místě.
skeleton_bone-FOOT_SHIFT = Odsazení chodidla
skeleton_bone-SKELETON_OFFSET = Odsazení kostry
skeleton_bone-SHOULDERS_DISTANCE = Vzdálenost ramen
skeleton_bone-SHOULDERS_WIDTH = Šířka ramen
skeleton_bone-arm_group = Délka paže
skeleton_bone-UPPER_ARM = Délka nadloktí
skeleton_bone-LOWER_ARM = Délka podloktí
skeleton_bone-HAND_Y = Vzdálenost ruky na ose Y
skeleton_bone-HAND_Z = Vzdálenost ruky na ose Z
skeleton_bone-ELBOW_OFFSET = Odsazení loktů

## Tracker reset buttons

reset-reset_all = Obnovit nastavení proporcí
reset-reset_all_warning-v2 =
    <b>Varování:</b> vyše proporce budou obnoveny do výchozích hodnot založených na vaší nakonfigurované výšce.
    Jste si jistí že chcete udělat?
reset-reset_all_warning-reset = Obnovit proporce
reset-reset_all_warning-cancel = Zrušit
reset-reset_all_warning_default-v2 =
    <b>Varování:</b> Vaše výška ještě nebyla nakonfigurována, vaše proporce se obnoví do výchozích hodnot založených na výchozí výšce.
    Jste si jistí že to chcete udělat?
reset-full = Plný Reset
reset-mounting = Znovu nastavit nasazení
reset-yaw = Rychlý reset

## Serial detection stuff

serial_detection-new_device-p0 = Bylo detekováno nové sériové zařízení!
serial_detection-new_device-p1 = Zadejte přihlašovací údaje Wi-Fi!
serial_detection-new_device-p2 = Vyberte akci kterou chcete vykonat.
serial_detection-open_wifi = Připojit se k Wi-Fi
serial_detection-open_serial = Otevřít sériovou konzoly
serial_detection-submit = Odeslat!
serial_detection-close = Zavřít

## Navigation bar

navbar-home = Domů
navbar-body_proportions = Tělesné proporce
navbar-trackers_assign = Přiřazení trackerů
navbar-mounting = Kalibrace nasazení
navbar-onboarding = Průvodce nastavením
navbar-settings = Nastavení

## Biovision hierarchy recording

bvh-start_recording = Nahrát BVH
bvh-recording = Nahrávání...

## Tracking pause

tracking-unpaused = Pozastavit sledování
tracking-paused = Pokračovat v sledování

## Widget: Overlay settings

widget-overlay = Překrytí
widget-overlay-is_visible_label = Zobrazit překrytí v SteamVR
widget-overlay-is_mirrored_label = Zobrazit překrytí jako zrcadlo

## Widget: Drift compensation

widget-drift_compensation-clear = Vymazat kompenzaci driftu

## Widget: Clear Mounting calibration

widget-clear_mounting = Vymazat reset nasazení

## Widget: Developer settings

widget-developer_mode = Vývojářský režim
widget-developer_mode-high_contrast = Vysoký kontrast
widget-developer_mode-precise_rotation = Přesná rotace
widget-developer_mode-fast_data_feed = Rychlý přenos dat
widget-developer_mode-filter_slimes_and_hmd = Filtrovat trackery a HMD
widget-developer_mode-sort_by_name = Seřadit podle názvu
widget-developer_mode-raw_slime_rotation = Nezpracovaná rotace
widget-developer_mode-more_info = Více informací

## Widget: IMU Visualizer

widget-imu_visualizer = Rotace
widget-imu_visualizer-preview = Náhled
widget-imu_visualizer-hide = Skrýt
widget-imu_visualizer-rotation_raw = Nezpracované
widget-imu_visualizer-rotation_preview = Náhled
widget-imu_visualizer-acceleration = Akcelerace
widget-imu_visualizer-position = Pozice
widget-imu_visualizer-stay_aligned = Zůstaň Srovaný (Stay Aligned)

## Widget: Skeleton Visualizer

widget-skeleton_visualizer-preview = Náhled kostry
widget-skeleton_visualizer-hide = Skrýt

## Tracker status

tracker-status-none = Žádný stav
tracker-status-busy = Zaneprázdněný
tracker-status-error = Chyba
tracker-status-disconnected = Odpojeno
tracker-status-occluded = Zakrytý
tracker-status-ok = OK
tracker-status-timed_out = Spojení přerušeno

## Tracker status columns

tracker-table-column-name = Název
tracker-table-column-type = Typ
tracker-table-column-battery = Baterie
tracker-table-column-ping = Ping
tracker-table-column-tps = TPS
tracker-table-column-temperature = Teplota °C
tracker-table-column-linear-acceleration = Akcel. X/Y/Z
tracker-table-column-rotation = Rotace X/Y/Z
tracker-table-column-position = Pozice X/Y/Z
tracker-table-column-stay_aligned = Zůstaň Srovaný (Stay Aligned)
tracker-table-column-url = URL

## Tracker rotation

tracker-rotation-front = Přední
tracker-rotation-front_left = Vpředu vlevo
tracker-rotation-front_right = Vpředu vpravo
tracker-rotation-left = Levá
tracker-rotation-right = Pravá
tracker-rotation-back = Zadní
tracker-rotation-back_left = Vzadu vlevo
tracker-rotation-back_right = Vzadu vpravo
tracker-rotation-custom = Vlastní nastavení
tracker-rotation-overriden = (přepsáno kalibrací nasazení)

## Tracker information

tracker-infos-manufacturer = Výrobce
tracker-infos-display_name = Zobrazený název
tracker-infos-custom_name = Vlastní název
tracker-infos-url = URL Trackeru
tracker-infos-version = Verze firmwaru
tracker-infos-hardware_rev = Revize hardwaru
tracker-infos-hardware_identifier = ID hardwaru
tracker-infos-data_support = Datový typ
tracker-infos-imu = Senzor IMU
tracker-infos-board_type = Základní deska
tracker-infos-network_version = Verze protokolu
tracker-infos-magnetometer = Magnetometr
tracker-infos-magnetometer-status-v1 =
    { $status ->
        [DISABLED] Zakázáno
        [ENABLED] Povoleno
       *[NOT_SUPPORTED] Není podporováno
    }

## Tracker settings

tracker-settings-back = Zpět na seznam trackerů
tracker-settings-title = Nastavení trackeru
tracker-settings-assignment_section = Přiřazení
tracker-settings-assignment_section-description = Na kterou část těla je tracker přiřazen?
tracker-settings-assignment_section-edit = Upravit přiřazení
tracker-settings-mounting_section = Poloha nasazení
tracker-settings-mounting_section-description = Na jakou stranu je tracker nasazený?
tracker-settings-mounting_section-edit = Upravit nasazení
tracker-settings-drift_compensation_section = Povolit kompenzaci driftu
tracker-settings-drift_compensation_section-description = Měl by tento tracker kompenzovat svůj drift, když je zapnuta kompenzace driftu?
tracker-settings-drift_compensation_section-edit = Povolit kompenzaci driftu
tracker-settings-use_mag = Povolit magnetometr na tomto trackeru
# Multiline!
tracker-settings-use_mag-description =
    Měl by tento tracker používat magnetometer k redukci driftu když je použití magnetometru povoleno? <b> Prosím nevypínejte váš tracker při přepínání tohoto nastavení!</b>
    
    Nejprve musíte povolit používání magnetometru, <magSetting>Kliknutím zde přejdete k nastavená magnetometru</magSetting>.
tracker-settings-use_mag-label = Povolit magnetometr
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Název trackeru
tracker-settings-name_section-description = Třeba nějakou roztomilou přezdívku :)
tracker-settings-name_section-placeholder = Erimelova levá tlapka
tracker-settings-name_section-label = Název trackeru
tracker-settings-forget = Zapomenout tracker
tracker-settings-forget-description = Odebere tracker z SlimeVR Serveru a zabrání jeho opětovnému připojení do té doby, dokud nebude server restarován. Konfigurace trackeru nebude ztracena.
tracker-settings-forget-label = Zapomenout tracker
tracker-settings-update-low-battery = Nelze provést aktualizaci. Baterie má méně než 50%
tracker-settings-update-up_to_date = Aktuální
tracker-settings-update = Aktualizovat nyní
tracker-settings-update-title = Verze Firmwareu

## Tracker part card info

tracker-part_card-no_name = Bez jména
tracker-part_card-unassigned = Nepřiřazeno

## Body assignment menu

body_assignment_menu = Kde chcete, aby tento tracker byl?
body_assignment_menu-description = Vyberte, kam chcete tento tracker umístit. Nebo můžete spravovat všechny trackery najednou, místo jednoho po druhém.
body_assignment_menu-show_advanced_locations = Zobrazit pokročilá místa přiřazení
body_assignment_menu-manage_trackers = Spravovat všechny trackery
body_assignment_menu-unassign_tracker = Zrušit přiřazení trackeru

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = { "Který tracker přiřadit k" }
tracker_selection_menu-NONE = Který tracker chcete aby byl nezařazený?
tracker_selection_menu-HEAD = { -tracker_selection-part } hlavě?
tracker_selection_menu-NECK = { -tracker_selection-part } krku?
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part } pravému rameni?
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part } pravýmu nadloktí?
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part } pravýmu podloktí?
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part } pravé ruce?
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part } pravému stehnu?
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part } pravému kotníku?
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part } pravému chodidlu?
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part } pravému ovladači?
tracker_selection_menu-UPPER_CHEST = { -tracker_selection-part } k horní část hrudníku?
tracker_selection_menu-CHEST = { -tracker_selection-part } hrudníku?
tracker_selection_menu-WAIST = { -tracker_selection-part } pasu?
tracker_selection_menu-HIP = { -tracker_selection-part } kyčle?
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part } levému rameni?
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part } levýmu nadloktí?
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part } levýmu podloktí?
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part } levé ruce?
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part } levému stehnu?
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part } levému kotníku?
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part } levému chodidlu?
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part } levému ovladači?
tracker_selection_menu-unassigned = Nepřiřazené Trackery
tracker_selection_menu-assigned = Přiřazené Trackery
tracker_selection_menu-dont_assign = Nepřiřazovat
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Varování:</b> Krční tracker může být smrtelný, pokud je popruh
    utažen příliš těsně. Popruh by mohl přerušit krevní oběh do hlavy!
tracker_selection_menu-neck_warning-done = Chápu riziko
tracker_selection_menu-neck_warning-cancel = Zrušit

## Mounting menu

mounting_selection_menu = Kde chcete, aby byl tento tracker umístěn?
mounting_selection_menu-close = Zavřít

## Sidebar settings

settings-sidebar-title = Nastavení
settings-sidebar-general = Obecné
settings-sidebar-tracker_mechanics = Mechanika trackerů
settings-sidebar-stay_aligned = Zůstaň Srovaný (Stay Aligned)
settings-sidebar-fk_settings = Nastavení trackování
settings-sidebar-gesture_control = Ovládání gesty
settings-sidebar-interface = Rozhraní
settings-sidebar-osc_router = OSC router
settings-sidebar-osc_trackers = VRChat OSC tracker
settings-sidebar-utils = Nástroje
settings-sidebar-serial = Sériová konzole
settings-sidebar-appearance = Vzhled
settings-sidebar-notifications = Notifikace
settings-sidebar-behavior = Chování
settings-sidebar-firmware-tool = Nástroj pro DIY firmware
settings-sidebar-vrc_warnings = Varovaní VRChat konfigurace
settings-sidebar-advanced = Pokročilé

## SteamVR settings

settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = Trackery SteamVR
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    Povolit nebo zakázat určité trackery SteamVR.
    Užitečné pro hry nebo aplikace, které podporují pouze určité trackery.
settings-general-steamvr-trackers-waist = Pás
settings-general-steamvr-trackers-chest = Hrudník
settings-general-steamvr-trackers-left_foot = Levá noha
settings-general-steamvr-trackers-right_foot = Pravá noha
settings-general-steamvr-trackers-left_knee = Levé koleno
settings-general-steamvr-trackers-right_knee = Pravé koleno
settings-general-steamvr-trackers-left_elbow = Levý loket
settings-general-steamvr-trackers-right_elbow = Pravý loket
settings-general-steamvr-trackers-left_hand = Levá ruka
settings-general-steamvr-trackers-right_hand = Pravá ruka
settings-general-steamvr-trackers-tracker_toggling = Automatické přiřazení trackeru
settings-general-steamvr-trackers-tracker_toggling-description = Automaticky zapne trackery ve SteamVR v závislosti na aktuálním přiřazením trackerů.
settings-general-steamvr-trackers-tracker_toggling-label = Automatické přiřazení trackeru
settings-general-steamvr-trackers-hands-warning =
    <b>Varování:</b> trackery rukou přepíší vaše ovladače.
    Jste si jistí?
settings-general-steamvr-trackers-hands-warning-cancel = Zrušit
settings-general-steamvr-trackers-hands-warning-done = Ano

## Tracker mechanics

settings-general-tracker_mechanics = Mechanika trackerů
settings-general-tracker_mechanics-filtering = Filtrování
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Vyberte typ filtrování pro své trackery.
    Predikce předpovídá pohyb, zatímco vyhlazování pohyb vyhlazuje.
settings-general-tracker_mechanics-filtering-type = Typ filtrování
settings-general-tracker_mechanics-filtering-type-none = Žádné filtrování
settings-general-tracker_mechanics-filtering-type-none-description = Použít rotace tak, jak jsou. Nebude provedeno žádné filtrování.
settings-general-tracker_mechanics-filtering-type-smoothing = Vyhlazování
settings-general-tracker_mechanics-filtering-type-smoothing-description = Vyhlazuje pohyby, ale přidává mírné zpoždění.
settings-general-tracker_mechanics-filtering-type-prediction = Predikce
settings-general-tracker_mechanics-filtering-type-prediction-description = Zkracuje prodlevu a zrychluje pohyby, ale může způsobit třesení trackerů.
settings-general-tracker_mechanics-filtering-amount = Množství
settings-general-tracker_mechanics-yaw-reset-smooth-time = Čas vyhlazení resetu svislé osy (0s pro vypnutí vyhlazení)
settings-general-tracker_mechanics-drift_compensation = Kompenzace driftu
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Kompenzuje vychýlení IMU použitím inverzní rotace.
    Změňte velikost kompenzace a počet resetů, které jsou brány v úvahu.
settings-general-tracker_mechanics-drift_compensation-enabled-label = Kompenzace driftu
settings-general-tracker_mechanics-drift_compensation-prediction = Predikce kompenzace driftu
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-prediction-description =
    Předpovídá kompenzaci driftu svislé osy přes předešle změřený rozsah.
    Povolte toto pokud se vaše trackery neustále otáčejí na vertikální ose.
settings-general-tracker_mechanics-drift_compensation-prediction-label = Predikce kompenzace driftu
settings-general-tracker_mechanics-drift_compensation_warning =
    <b>Varování:</b> Kompenzaci driftu používejte pouze tehdy kdy musíte resetovat
    Neobvykle často (každých ~5-10 minut).
    
    Některé IMU které náchylné k častým resetům zahrnují:
    Joy-Cons, owoTrack a MPU (bez aktuálního firmwaru).
settings-general-tracker_mechanics-drift_compensation_warning-cancel = Zrušit
settings-general-tracker_mechanics-drift_compensation_warning-done = Rozumím
settings-general-tracker_mechanics-drift_compensation-amount-label = Množství kompenzace
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Použít až x posledních obnovení
settings-general-tracker_mechanics-save_mounting_reset = Uložit automatickou kalibraci obnovení připevnění
settings-general-tracker_mechanics-save_mounting_reset-description =
    Uloží automatické kalibrování resetování umístění pro trackery mezi restarty. Užitečné
    pokud máte oblek, na kterém se umístění trackeru nemění mezi relacemi. <b>Nedoporučováno pro uživatele s běžnou sestavou</b>
settings-general-tracker_mechanics-save_mounting_reset-enabled-label = Uložit "Kalibraci nasazení"
settings-general-tracker_mechanics-use_mag_on_all_trackers = Použít magnetometr na všech IMU trackerech, které jej podporují
settings-general-tracker_mechanics-use_mag_on_all_trackers-description =
    Použití magnetometr na všech trackerech které pro to mají kompatibilní firmware, snížení drifutu v stailních magnetických prostředích.
    Může být vypnuto pro jednotivé trackery v jejich nastaveních. <b> Prosíme nevypínejte žádný z trackerů při přepínání tohoto nastavení! </b>
settings-general-tracker_mechanics-use_mag_on_all_trackers-label = Použít magnetometru na trackerech
settings-stay_aligned = Zůstaň Srovaný (Stay Aligned)
settings-stay_aligned-description = Zůstaň Srovaný redukuje drift pomocí postupného upravování vašich trackerů do vaší relaxůjící pózy.
settings-stay_aligned-setup-label = Nastavte Zůstaň Sronaný
settings-stay_aligned-setup-description = Musíte dokončit "Nastvení Zůstaň Srovaný" pro zapnutí Zůstaň Srovnaný.
settings-stay_aligned-warnings-drift_compensation = ⚠ Prosím vypněte Kompenzaci Driftu! Kompenzace driftu bude narušovat funkčnost Zůstaň Srovnaný.
settings-stay_aligned-enabled-label = Upravit trackery
settings-stay_aligned-general-label = Obecné
settings-stay_aligned-relaxed_poses-label = Relaxovací Póza
settings-stay_aligned-relaxed_poses-standing = Upravit trackery při stoje
settings-stay_aligned-relaxed_poses-reset_pose = Obnovit pózu
settings-stay_aligned-relaxed_poses-close = Zavřít
settings-stay_aligned-debug-label = Ladění
settings-stay_aligned-debug-copy-label = Zkopírovat nastavení do schránky

## FK/Tracking settings

settings-general-fk_settings = Nastavení trackování
# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Podlahovej clip
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Korekce bruslení
settings-general-fk_settings-leg_tweak-toe_snap = Přichycení špiček
settings-general-fk_settings-leg_tweak-foot_plant = Narovnání chodidla
settings-general-fk_settings-leg_tweak-skating_correction-amount = Síla korekce "bruslení"
settings-general-fk_settings-leg_tweak-skating_correction-description = Korekce bruslení snižuje effect "bruslení", ale může snížit přesnost u některých pohybů. Pokud tuto funkci povolíte, nezapomeňte provést úplný reset a zkalibrovat se ve hře.
settings-general-fk_settings-leg_tweak-floor_clip-description = Připnutí k podlaze může zlepšit nebo dokonce zabránit propadání trackerů podlahou. Při zapnutí této funkce nezapomeňte provést úplný reset a zkalibrovat se ve hře.
settings-general-fk_settings-leg_tweak-toe_snap-description = Přichycení špiček se pokouší odhadnout rotaci vašich chodidel v případě, že nepoužíváte trackery chodidel.
settings-general-fk_settings-leg_tweak-foot_plant-description = Narovnání chodidla při dotyku narovnává chodidla tak, aby byla rovnoběžně se zemí.
settings-general-fk_settings-leg_fk = Sledování nohou
settings-general-fk_settings-enforce_joint_constraints = Limity kostry
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints = Prosazování omezení
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = Zabránit rotaci kloubům za jejich limit
settings-general-fk_settings-enforce_joint_constraints-correct_constraints = Opravit pomocí omezení
settings-general-fk_settings-enforce_joint_constraints-correct_constraints-description = Opravit rotaci kloubů, když překročí svůj limit
settings-general-fk_settings-arm_fk = Trackování ramen
settings-general-fk_settings-arm_fk-description = Vynutit sledování rukou z VR headsetu, i když jsou k dispozici údaje o poloze rukou z trackerů.
settings-general-fk_settings-arm_fk-force_arms = Vynutit ruce z VR Headsetu
settings-general-fk_settings-reset_settings = Obnovit nastavení
settings-general-fk_settings-reset_settings-reset_hmd_pitch = Obnovit HMD pitch
settings-general-fk_settings-arm_fk-reset_mode-description = Nastavte pózu rukou použitá pro reset nasazení.
settings-general-fk_settings-arm_fk-back = Paže dozadu
settings-general-fk_settings-arm_fk-back-description = Výchozí režim: paže směřují dozadu, předloktí dopředu.
settings-general-fk_settings-arm_fk-tpose_up = T-póza (ruce nahoru)
settings-general-fk_settings-arm_fk-tpose_up-description = Před zahájením plného resetu, očekává že stojíte vzpřímeně a máte paže volně spuštěné podél těla. A pro reset umístění zaujměte uvolněný postoj a pomalu zvedněte paže do pozice Téčka (90 stupňů jako písmeno T).
settings-general-fk_settings-arm_fk-tpose_down = T-póza (ruce dolů)
settings-general-fk_settings-arm_fk-tpose_down-description = Před zahájením plného resetu, očekává že zaujmete uvolněný postoj a pomalu zvednete paže do pozice Téčka (90 stupňů jako písmeno T). A pro reset umístění, že stojíte vzpřímeně a máte paže volně spuštěné podél těla.
settings-general-fk_settings-arm_fk-forward = Vpřed
settings-general-fk_settings-arm_fk-forward-description = Ideální pozice pro Vtubing: zvedněte paže do 90 stupňového úhlu. (90 stupňů jako písmeno T).
settings-general-fk_settings-skeleton_settings-toggles = Přepínače kostry
settings-general-fk_settings-skeleton_settings-description = Zapnutí nebo vypnutí nastavení kostry. Je doporučeno je ponechat zapnuté.
settings-general-fk_settings-skeleton_settings-extended_spine_model = Prodloužení modelu páteře
settings-general-fk_settings-skeleton_settings-extended_pelvis_model = Model s prodloužením pánve
settings-general-fk_settings-skeleton_settings-extended_knees_model = Model s prodloužením kolene
settings-general-fk_settings-skeleton_settings-ratios = Poměry kostry
settings-general-fk_settings-skeleton_settings-ratios-description = Změňte hodnoty nastavení kostry, Po změně budete možná muset poupravit vaše proporce.
settings-general-fk_settings-self_localization-title = Režim Mocap
settings-general-fk_settings-self_localization-description = Režim Mocap je experimentální funkce, která dokáže přibližně určit polohu vašeho těla bez VR Headsetu a dalších trackerů. Pro správnou funkci je však nutné mít trackery pro nohy a hlavu.

## Gesture control settings (tracker tapping)

settings-general-gesture_control = Ovládání gesty
settings-general-gesture_control-subtitle = Resetování na základě klepnutí
settings-general-gesture_control-description = Umožňuje spouštět resetování klepnutím na tracker. Sledovací zařízení umístěné nejvýše na vašem hrudníku slouží k Rychlému-Resetování, tracker umístěný nejvýše na levé noze se používá pro Resetování, a tracker umístěný nejvýše na pravé noze se používá pro Resetování Montáže. Je třeba zmínit, že aby bylo klepnutí zaregistrováno, klepnutí musí být provedena do 0.6 vteřin.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
        [one] { $amount } klepnutí
        [few] { $amount } klepnutí
       *[other] { $amount } klepnutí
    }
# This is a unit: 3 trackers, 2 trackers, 1 tracker
# $amount (Number) - Amount of trackers
settings-general-gesture_control-trackers =
    { $amount ->
        [one] 1 tracker
        [few] { $amount } trackerů
        [many] { $amount } trackerů
       *[other] { $amount } trackerů
    }
settings-general-gesture_control-yawResetEnabled = Povolit klepnutí pro reset odklonu
settings-general-gesture_control-yawResetDelay = Zpoždění resetu vybočení
settings-general-gesture_control-yawResetTaps = Klepnutí pro resetování rotace
settings-general-gesture_control-fullResetEnabled = Povolit klepnutí pro úplné restartování
settings-general-gesture_control-fullResetDelay = Zpoždění úplného obnovení
settings-general-gesture_control-fullResetTaps = Klepnutí pro úplný reset
settings-general-gesture_control-mountingResetEnabled = Povolit klepnutí pro resetování montáže
settings-general-gesture_control-mountingResetDelay = Zpoždění resetování montáže
settings-general-gesture_control-mountingResetTaps = Klepnutí pro resetování montáže
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = Trackery překročily práh
settings-general-gesture_control-numberTrackersOverThreshold-description = Zvyšte tuto hodnotu, pokud detekce klepnutí nefunguje. Nepřekračujte ji nad hodnotu, která je potřebná k tomu, aby detekce klepnutí fungovala, protože by to mohlo způsobit více falešně pozitivních klepnutí.

## Appearance settings

settings-interface-appearance = Vzhled
settings-general-interface-dev_mode = Vývojářský režim
settings-general-interface-dev_mode-description = Tento režim může být užitečný, pokud potřebujete podrobné údaje nebo omunikovat s trackerama na pokročilejší úrovni.
settings-general-interface-dev_mode-label = Vývojářský režim
settings-general-interface-theme = Barva tématu
settings-general-interface-show-navbar-onboarding = Zobrazit "{ navbar-onboarding }" na navigačním panelu
settings-general-interface-show-navbar-onboarding-description = Tohle mění jestli bude tlačítko pro "{ navbar-onboarding }" zobrazeno na navigačním panelu
settings-general-interface-show-navbar-onboarding-label = Zobrazit "{ navbar-onboarding }"
settings-general-interface-lang = Zvolte jazyk
settings-general-interface-lang-description = Změňte výchozí jazyk, který chcete používat.
settings-general-interface-lang-placeholder = Zvolte jazyk, který chcete používat.
# Keep the font name untranslated
settings-interface-appearance-font = Font rozhraní
settings-interface-appearance-font-description = Tohle mění font který používá rozhraní
settings-interface-appearance-font-placeholder = Toto změní písmo používané v rozhraní.
settings-interface-appearance-font-os_font = Systémový font
settings-interface-appearance-font-slime_font = Výchozí font
settings-interface-appearance-font_size = Výchozí velikost písma
settings-interface-appearance-font_size-description = Toto ovlivňuje velikost písma celého rozhraní, s výjimkou panelu nastavení.
settings-interface-appearance-decorations = Použít nativní dekorace systému
settings-interface-appearance-decorations-description = Tímto se nebude vykreslovat horní lišta rozhraní, a místo toho se použije vlastní barva lišty zvolená v operačním systému.
settings-interface-appearance-decorations-label = Použít nativní dekorace

## Notification settings

settings-interface-notifications = Notifikace
settings-general-interface-serial_detection = Detekce sériových zařízení
settings-general-interface-serial_detection-description = Tato možnost zobrazí pop-up pokaždé, když připojíte nové sériové zařízení, které by mohlo být trackerem. Pomáhá zlepšit proces nastavení trackeru.
settings-general-interface-serial_detection-label = Detekce sériových zařízení
settings-general-interface-feedback_sound = Zvuk zpětné vazby
settings-general-interface-feedback_sound-description = Tato možnost spustí zvuk, když je aktivován reset.
settings-general-interface-feedback_sound-label = Zvuk zpětné vazby
settings-general-interface-feedback_sound-volume = Hlasitost zvuku zpětné vazby
settings-general-interface-connected_trackers_warning = Upozornění o připojených trackerů
settings-general-interface-connected_trackers_warning-description = Tato možnost zobrazí vyskakovací okno pokaždé, když se pokusíte opustit SlimeVR, když máte připojen jeden nebo více trackerů. Připomene vám, abyste vypnuli své trackery, až budete hotovi, abyste prodloužili životnost baterie.
settings-general-interface-connected_trackers_warning-label = Upozornění o připojených trackerech při ukončení

## Behavior settings

settings-interface-behavior = Chování
settings-general-interface-use_tray = Minimalizovat do oznamovací oblasti
settings-general-interface-use_tray-description = Umožňuje vám zavřít okno, aniž byste zavřeli SlimeVR Server, takže ho můžete nadále používat bez rozhraní.
settings-general-interface-use_tray-label = Minimalizovat do oznamovací oblasti
settings-general-interface-discord_presence = Sdílet aktivitu na Discordu
settings-general-interface-discord_presence-description = Sdělí Discord klientu, že používáte SlimeVR společně s počtem trackerů IMU, které používáte.
settings-general-interface-discord_presence-label = Sdílet aktivitu na Discordu
settings-general-interface-discord_presence-message =
    { $amount ->
        [0] Sliming around
        [one] Používá 1 tracker
        [few] Používá { $amount } trackery
        [many] Používá { $amount } trackerů
       *[other] Používá { $amount } trackerů
    }
settings-interface-behavior-error_tracking = Sběr chyb prostřednictvím Sentry.io
settings-interface-behavior-error_tracking-description_v2 =
    <h1>Souhlasíte se shromažďováním anonymizovaých údajů o chybých?</h1>
    
    <b>Neschrožďujeme osobní udaje!</b> pro příklad IP adresy nebo přihlašovací údaje k sítím Wi-Fi. SlimeVR respektuje vaše soukromí!
    
    Aby jsme mohli poskytnout nejlepší zážitek uživatelům, schromažďujeme proto anonymizované zprávy o chybých, metriky výkon a informace o operačním systém. To nám pomáhá zjištovat chyby a problémy s SlimeVR. Tyto matriky jsou schromažďovány prostřednictvím Sentry.io.
settings-interface-behavior-error_tracking-label = Odeslat chyby vývojářům

## Serial settings

settings-serial = Sériová Konzole
# This cares about multilines
settings-serial-description =
    Jedná se o přímý informační kanál pro sériovou komunikaci.
    Může být užitečné, pokud potřebujete zjistit, zda se firmware chová špatně.
settings-serial-connection_lost = Ztráta připojení k seriálu, Připojení se obnovuje...
settings-serial-reboot = Restartovat
settings-serial-factory_reset = Obnovení továrního nastavení
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>Varování</b> Toto obnoví tovární nastavení trackeru.
    To znamená, že nastavení Wi-Fi a kalibrace <b>budou ztracena!</b>
settings-serial-factory_reset-warning-ok = Vím, co dělám
settings-serial-factory_reset-warning-cancel = Zrušit
settings-serial-serial_select = Vyberte sériový port
settings-serial-auto_dropdown_item = Auto
settings-serial-get_wifi_scan = Skenovat WiFi
settings-serial-file_type = Prostý text
settings-serial-save_logs = Uložit jako soubor

## OSC router settings

settings-osc-router = OSC router
# This cares about multilines
settings-osc-router-description =
    Přeposlat zprávy OSC z jiného programu.
    Užitečné například pro použití jiného OSC programu s VRChat.
settings-osc-router-enable = Zapnout
settings-osc-router-enable-description = Vypnutí nebo zapnutí přeposílání zpráv.
settings-osc-router-enable-label = Zapnout
settings-osc-router-network = Síťové porty
# This cares about multilines
settings-osc-router-network-description =
    Nastavení portů pro naslouchání a odesílání dat.
    Tyto porty mohou být stejné jako ostatní porty používané v serveru SlimeVR.
settings-osc-router-network-port_in =
    .label = Vstup portu
    .placeholder = Vstup portu (výchozí: 9002)
settings-osc-router-network-port_out =
    .label = Výstup z portu
    .placeholder = Výstup z portu (výchozí: 9000)
settings-osc-router-network-address = Síťová adresa
settings-osc-router-network-address-description = Nastavte adresu pro odesílání dat.
settings-osc-router-network-address-placeholder = Adresa IPV4

## OSC VRChat settings

settings-osc-vrchat = Trackery VRChat OSC
# This cares about multilines
settings-osc-vrchat-description-v1 =
    Pro změnu nastavení specifických pro standart OSC pro odesílání
    sledovacích dat aplikacím bez SteamVR (např. Quest standalone).
    Ujistěte se že jste povolili OSC ve VRChat přes nabídku v menu Akcí pod OSC > Povoleno.
settings-osc-vrchat-enable = Zapnout
settings-osc-vrchat-enable-description = Vypnutí a zapnutí odesílání a přijímání dat.
settings-osc-vrchat-enable-label = Zapnout
settings-osc-vrchat-oscqueryEnabled = Povolit OSCQuery
settings-osc-vrchat-oscqueryEnabled-label = Povolit OSCQuery
settings-osc-vrchat-network = Síťové porty
settings-osc-vrchat-network-description-v1 = Nastavte port pro komunikaci. Může zůstat tak jak je pro VRChat.
settings-osc-vrchat-network-port_in =
    .label = Vstup portu
    .placeholder = Vstup portu (výchozí: 9001)
settings-osc-vrchat-network-port_out =
    .label = Výstup portu
    .placeholder = Výstup portu (výchozí: 9000)
settings-osc-vrchat-network-address = Síťová adresa
settings-osc-vrchat-network-address-description-v1 = Zvolte na jakou adresu zasílat data, Může zůstat nezměneno pro Vrchat.
settings-osc-vrchat-network-address-placeholder = VRChat ip adresa
settings-osc-vrchat-network-trackers = Trackery
settings-osc-vrchat-network-trackers-description = Vypnuti a zapnutí odesílání konkrétních trackerů přes OSC.
settings-osc-vrchat-network-trackers-chest = Hrudník
settings-osc-vrchat-network-trackers-hip = Kyčel
settings-osc-vrchat-network-trackers-knees = Kolena
settings-osc-vrchat-network-trackers-feet = Chodidla
settings-osc-vrchat-network-trackers-elbows = Lokty

## VMC OSC settings

settings-osc-vmc = Virtuální snímání pohybu (Také známo jako Virtual Motion Capture)
# This cares about multilines
settings-osc-vmc-description =
    Změna nastavení specificky pro VCM (Virtual Motion Capture) protokol
        odesílat data o kostech SlimeVR a přijímat data o kostech z jiných aplikací.
settings-osc-vmc-enable = Zapnout
settings-osc-vmc-enable-description = Vypnutí a zapnutí odesílání a přijímání dat.
settings-osc-vmc-enable-label = Zapnout
settings-osc-vmc-network = Síťové porty
settings-osc-vmc-network-description = Nastavte porty pro poslech a odesílání dat pomocí VMC.
settings-osc-vmc-network-port_in =
    .label = Port pro příjem
    .placeholder = Port pro příjem (výchozí: 39540)
settings-osc-vmc-network-port_out =
    .label = Port pro odesílání
    .placeholder = Port pro odesílání (výchozí: 39539)
settings-osc-vmc-network-address = Síťová adresa
settings-osc-vmc-network-address-description = Vyberte, na kterou adresu odesílat data pomocí VMC.
settings-osc-vmc-network-address-placeholder = Adresa IPV4
settings-osc-vmc-vrm = VRM Model
settings-osc-vmc-vrm-description = Načtěte VRM model, k umožnení lepšímu sledování hlavy a zlepšení kompatibility s dalšími aplikacemi.
settings-osc-vmc-vrm-untitled_model = Nepojmenovaný model
settings-osc-vmc-vrm-file_select = Přetáhněte zde model, který chcete použít, nebo <u>procházejte</u>
settings-osc-vmc-anchor_hip = Zakotvit v bocích
settings-osc-vmc-anchor_hip-description = Zakotvit sledování u boků, užitečné pro VTubing kde sedíte. Pokud je deaktivováno, načíst VRM model.
settings-osc-vmc-anchor_hip-label = Zakotvit v bocích
settings-osc-vmc-mirror_tracking = Zrcadlení sledování
settings-osc-vmc-mirror_tracking-description = Zrcadlit trakování horizontálně.
settings-osc-vmc-mirror_tracking-label = Zrcadlení trackování

## Common OSC settings


## Advanced settings

settings-utils-advanced = Pokročilé
settings-utils-advanced-reset-gui = Obnovení GUI nastavení
settings-utils-advanced-reset-gui-description = Obnovení výchozího nastavení pro rozhraní.
settings-utils-advanced-reset-gui-label = Obnovit GUI
settings-utils-advanced-reset-server = Obnovení nastavení sledování
settings-utils-advanced-reset-server-description = Obnovit výchozí nastavení pro sledovaní.
settings-utils-advanced-reset-server-label = Obnovení sledování
settings-utils-advanced-reset-all = Obnovit všechna nastavení
settings-utils-advanced-reset-all-description = Obnoví výchozí nastavení pro rozhraní a sledování.
settings-utils-advanced-reset-all-label = Obnovit vše
settings-utils-advanced-reset_warning =
    { $type ->
        [gui]
            <b>Varování:</b> Tímhle se všechna vaše nastavení GUI obnoví na výchozí hodnoty.
            Jste si jisti že to chcete udělat?
        [server]
            <b>Varování:</b> Tímhle se všechna vaše nastavení Sledování obnoví na výchozí hodnoty.
            Jste si jisti že to chcete udělat?
       *[all]
            <b>Varování:</b> Tímhle se všechna vaše nastavení obnoví na výchozí hodnoty.
            Jste si jisti že to chcete udělat?
    }
settings-utils-advanced-reset_warning-reset = Obnovit nastavení
settings-utils-advanced-reset_warning-cancel = Zrušit
settings-utils-advanced-open_data-v1 = Složka s konfigurací
settings-utils-advanced-open_data-description-v1 = Otevřít složku s konfiguračními soubory pro SlimeVR v průzkumníku souborů?
settings-utils-advanced-open_data-label = Otevřít složku
settings-utils-advanced-open_logs = Složka s záznamy protokolů
settings-utils-advanced-open_logs-description = Otevřít složku s konfiguračními soubory pro SlimeVR v průzkumníku souborů?
settings-utils-advanced-open_logs-label = Otevřít složku

## Home Screen


## Tracking Checlist


## Setup/onboarding menu

onboarding-skip = Přeskočit nastavení
onboarding-continue = Pokračovat
onboarding-wip = Probíhající práce
onboarding-previous_step = Předchozí krok
onboarding-setup_warning =
    <b>Varování:</b> Pro dobré trackování je vyžadována počáteční kalibrace a nastavení,
    Je nutné, pokud používáte SlimeVR poprvé.
onboarding-setup_warning-skip = Přeskočit nastavení
onboarding-setup_warning-cancel = Pokračovat v nastavení

## Wi-Fi setup

onboarding-wifi_creds-back = Zpět na úvod
onboarding-wifi_creds-skip = Přeskočit nastavení Wi-Fi
onboarding-wifi_creds-submit = Odeslat!
onboarding-wifi_creds-ssid =
    .label = Název Wi-Fi
    .placeholder = Zadejte název Wi-Fi
onboarding-wifi_creds-ssid-required = Je vyžadován název sítě Wi-Fi
onboarding-wifi_creds-password =
    .label = Heslo
    .placeholder = Zadejte heslo

## Mounting setup

onboarding-reset_tutorial-back = Zpět na kalibraci montáže
onboarding-reset_tutorial = Obnovit tutoriál
onboarding-reset_tutorial-explanation = Během používání trackerů může dojít k jejich vychýlení, ať už kvůli nepřesnostem gyroskopu nebo jejich fyzickému posunutí. Existuje ale několik způsobů, jak to napravit.
onboarding-reset_tutorial-skip = Přeskočit krok
# Cares about multiline
onboarding-reset_tutorial-0 =
    Klepněte na zvýrazněný snímač { $taps } krát pro resetování rotace.
    
    Tím nastavíte snímače tak, aby směřovaly stejným směrem jako váš headset (HMD).
# Cares about multiline
onboarding-reset_tutorial-1 =
    Pro plný reset poklepejte { $taps } krát na zvýrazněný tracker.
    
    Pro tuto funkci musíte stát v základní pozici (ruce podél těla). Před provedením resetu je zde 3 sekundová prodleva (lze nastavit).
    Tímto se kompletně resetuje pozice a rotace všech vašich trackerů, což by mělo vyřešit většinu problémů.

## Setup start

onboarding-home = Vítejte k SlimeVR
onboarding-home-start = Pusťme se do toho!

## Setup done

onboarding-done-title = Vše je připraveno!
onboarding-done-description = Užijte si zážitek s full body tracking
onboarding-done-close = Zavřít průvodce

## Tracker connection setup

onboarding-connect_tracker-back = Zpět na přihlašovací údaje Wi-Fi
onboarding-connect_tracker-title = Připojení trackerů
onboarding-connect_tracker-description-p0-v1 = A nyní k té zábavné části, Připojte trackery
onboarding-connect_tracker-description-p1-v1 = Připojte po jednom postupně všechny tracker přes USB port.
onboarding-connect_tracker-issue-serial = Mám potíže s připojením!
onboarding-connect_tracker-usb = USB Tracker
onboarding-connect_tracker-connection_status-none = Hledám trackery
onboarding-connect_tracker-connection_status-serial_init = Připojuji se k sériovému zařízení
onboarding-connect_tracker-connection_status-obtaining_mac_address = Získávání MAC adresy trackeru
onboarding-connect_tracker-connection_status-provisioning = Odesílám přihlašovací údaje WiFi
onboarding-connect_tracker-connection_status-connecting = Pokouším se připojit k WiFi
onboarding-connect_tracker-connection_status-looking_for_server = Hledám server
onboarding-connect_tracker-connection_status-connection_error = Nelze se připojit k síti Wi-Fi
onboarding-connect_tracker-connection_status-could_not_find_server = Nelze najít server
onboarding-connect_tracker-connection_status-done = Připojeno k serveru
onboarding-connect_tracker-connection_status-no_serial_log = Nepodařilo se získat protokoly z trackeru
onboarding-connect_tracker-connection_status-no_serial_device_found = Nepodařilo se nalézt tracker přes USB
onboarding-connect_serial-error-modal-no_serial_log = Je tracker zapnutý?
onboarding-connect_serial-error-modal-no_serial_log-desc = Ujistěte se, že je tracker zapnutý a připojený k vašemu počátači
onboarding-connect_serial-error-modal-no_serial_device_found = Nebyly nalezeny žádné trackery
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers =
    { $amount ->
        [0] Žádné připojené trackery
        [one] 1 připojený tracker
        [few] { $amount } připojené trackery
       *[other] { $amount } připojených trackerů
    }
onboarding-connect_tracker-next = Připojil jsem všechny své trackery

## Tracker calibration tutorial

onboarding-calibration_tutorial = Kalibrační návod pro IMU
onboarding-calibration_tutorial-subtitle = Tohle pomůže snížit drift trackerů!
onboarding-calibration_tutorial-calibrate = Položil jsem trackery na stůl
onboarding-calibration_tutorial-status-waiting = Čekám na tebe
onboarding-calibration_tutorial-status-calibrating = Kalibruji
onboarding-calibration_tutorial-status-success = Super!
onboarding-calibration_tutorial-status-error = Trackerem bylo pohnuto.
onboarding-calibration_tutorial-skip = Přeskočit návod

## Tracker assignment tutorial

onboarding-assignment_tutorial = Jak připravit Slime Tracker před nasazením
onboarding-assignment_tutorial-first_step = 1. Umístěte na tracker samolepku s částí těla (pokud je máte) dle vlastního výběru.
# This text has a character limit of around 11 characters, so please keep it short
onboarding-assignment_tutorial-sticker = Samolepka
onboarding-assignment_tutorial-second_step-v2 = 2. Připevněte pásek k trackeru. Strana pásku se suchým zipem musí směřovat stejným směrem jako obličej na trackeru:
onboarding-assignment_tutorial-second_step-continuation-v2 = Suchý zip pro menší trackery by měla směřovat nahoru, jako na následujícím obrázku:
onboarding-assignment_tutorial-done = Nachystal jsem samolepky a pásky!

## Tracker assignment setup

onboarding-assign_trackers-back = Zpět na přihlašovací údaje Wi-Fi
onboarding-assign_trackers-title = Přiřazení trackerů
onboarding-assign_trackers-description = Vyberte, na jakou končetinu každý tracker patří. Klikněte na místo, kam chcete umístit tracker
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned =
    { $trackers ->
        [one] { $assigned } z { $trackers } trackerů bylo přiřazeno
        [few] { $assigned } z { $trackers } trackerů bylo přiřazeno
       *[other] { $assigned } z { $trackers } trackerů bylo přiřazeno
    }
onboarding-assign_trackers-advanced = Zobrazit pokročilá místa na přiřazení trackerů
onboarding-assign_trackers-next = Přiřadil jsem všechny trackery
onboarding-assign_trackers-mirror_view = Zrcadlit náhled
onboarding-assign_trackers-option-label =
    { $mode ->
        [lower-body] Spodní část těla
        [core] Základ
        [enhanced-core] Vylepšený základ
        [full-body] Celé tělo
       *[all] Všechny trackery
    }
onboarding-assign_trackers-option-description =
    { $mode ->
        [lower-body] Minimum pro sledování celého těla ve VR
        [core] + Vylepšené sledování páteře
        [enhanced-core] + Rotace chodidel
        [full-body] + Sledování loktů
       *[all] Přrazení všech dostupný trackerů
    }

## Tracker assignment warnings

# Note for devs, number is used for representing boolean states per bit.
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_FOOT =
    { $unassigned ->
        [0] Levá noha je přiřazena, ale pro správné fungování musí být také přiřazen levý kotník, levé stehno a jedna z těchto oblastí: hrudník, bok nebo pas.
        [1] Levá noha je přiřazena, ale pro správné fungování musí být také přiřazeno levé stehno a jedna z těchto oblastí: hrudník, bok nebo pas.
        [2] Levá noha je přiřazena, ale pro správné fungování musí být také přiřazen levý kotník a jedna z těchto oblastí: hrudník, bok nebo pas.
        [3] Levá noha je přiřazena, ale pro správné fungování musí být přiřazena jedna z těchto oblastí: hrudník, bok nebo pas.
        [4] Levá noha je přiřazena, ale pro správné fungování musí být také přiřazen levý kotník a levé stehno.
        [5] Levá noha je přiřazena, ale pro správné fungování musí být také přiřazeno levé stehno.
        [6] Levá noha je přiřazena, ale pro správné fungování musí být také přiřazen levý kotník.
       *[unknown] Levá noha je přiřazena, ale pro správné fungování musí být také přiřazen Neznámá Nepřiřazena část těla.
    }

## Tracker mounting method choose

onboarding-choose_mounting = Jakou metodu nasazení trackerů použít?
# Multiline text
onboarding-choose_mounting-description = Správná orientace nasazení zajistí přesné sledování trackerů na těle.
onboarding-choose_mounting-auto_mounting = Automatická detekce nasazení
# Italicized text
onboarding-choose_mounting-auto_mounting-label-v2 = Doporučeno
onboarding-choose_mounting-auto_mounting-description = Orientace nasazení všech trackerů bude automaticky rozpoznána ze 2 pozic.
onboarding-choose_mounting-manual_mounting = Manuální nastavení
# Italicized text
onboarding-choose_mounting-manual_mounting-label-v2 = Nemusí být dostatečně přesné
onboarding-choose_mounting-manual_mounting-description = Ručně zadejte orientaci nasazení každého trackeru.
# Multiline text
onboarding-choose_mounting-manual_modal-title =
    Jste si jistí, že chcete spustit
    automatickou kalibraci nasazení?
onboarding-choose_mounting-manual_modal-description = <b>Pro nové uživatele doporučujeme ruční nastavení nasazení.</b> Automatická detekce nasazení sice nabízí pohodlí, ale zvládnout potřebné pozice napoprvé může být náročné a vyžadovat trochu cviku.
onboarding-choose_mounting-manual_modal-confirm = Vím co dělám!
onboarding-choose_mounting-manual_modal-cancel = Zrušit

## Tracker manual mounting setup

onboarding-manual_mounting-back = Zpět do VR
onboarding-manual_mounting = Manuální nasazení trackerů
onboarding-manual_mounting-description = Klikněte na každý tracker a vyberte, jakým směrem jsou nasazeny
onboarding-manual_mounting-auto_mounting = Automatická detekce nasazení
onboarding-manual_mounting-next = Další krok

## Tracker automatic mounting setup

onboarding-automatic_mounting-back = Zpět do VR
onboarding-automatic_mounting-title = Kalibrace nasazení
onboarding-automatic_mounting-description = Pro správnou funkci trackerů SlimeVR jim musíme přiřadit orientaci.  Ta musí odpovídat tomu, jak jsou fyzicky nasměrovány na vašem těle.
onboarding-automatic_mounting-manual_mounting = Manuální nasazení
onboarding-automatic_mounting-next = Další krok
onboarding-automatic_mounting-prev_step = Předchozí krok
onboarding-automatic_mounting-done-title = Směr nasazení trackerů zkalibrován.
onboarding-automatic_mounting-done-description = Kalibrace nasazení trackerů je dokončena!
onboarding-automatic_mounting-done-restart = Začít znovu
onboarding-automatic_mounting-mounting_reset-title = Reset nasazení trackerů
onboarding-automatic_mounting-mounting_reset-step-0 = 1. Dřepněte si, jako při lyžování: nohy pokrčte v kolenou, trup nakloňte mírně dopředu a paže pokrčte.
onboarding-automatic_mounting-mounting_reset-step-1 = 2. Stiskněte tlačítko "Resetovat nasazení trackerů" a  vyčkejte 3 sekundy. Orientace nasazení trackerů se nastaví na základní hodnoty.
onboarding-automatic_mounting-preparation-title = Příprava
onboarding-automatic_mounting-preparation-v2-step-0 = 1. Stiskněte tlačítko pro "Plný Reset"
onboarding-automatic_mounting-put_trackers_on-title = Nasaďte si trackery
onboarding-automatic_mounting-put_trackers_on-description = Pro kalibraci směru nasazení použijeme právě přiřazené trackery. Nasaďte si prosím všechny trackery. Můžete zkontrolovat jejich umístění na obrázku vpravo.
onboarding-automatic_mounting-put_trackers_on-next = Mám nasazené všechny trackery

## Tracker manual proportions setupa

onboarding-manual_proportions-title = Manuální proporce těla
onboarding-manual_proportions-fine_tuning_button = Automatické jemné doladění proporcí
onboarding-manual_proportions-fine_tuning_button-disabled-tooltip = Pro použití automatického jemného lazení, prosím připojte VR headset
onboarding-manual_proportions-export = Exportovat proporce
onboarding-manual_proportions-import = Importovat proporce
onboarding-manual_proportions-file_type = Soubor tělesných proporcí
onboarding-manual_proportions-normal_increment = Normální škála
onboarding-manual_proportions-precise_increment = Přesná škála
onboarding-manual_proportions-grouped_proportions = Skupinové proporce
onboarding-manual_proportions-all_proportions = Všechny proporce
onboarding-manual_proportions-estimated_height = Odhadovaná výška uživatele

## Tracker automatic proportions setup

onboarding-automatic_proportions-back = Zpět na tutoriál
onboarding-automatic_proportions-title = Změřte své tělo
onboarding-automatic_proportions-description = Aby trackery SlimeVR fungovaly, potřebujeme znát délku vašich kostí. Tato krátká kalibrace vám to změří.
onboarding-automatic_proportions-manual = Manuální kalibrace proporcí
onboarding-automatic_proportions-prev_step = Předchozí krok
onboarding-automatic_proportions-put_trackers_on-title = Nasaďte si trackery
onboarding-automatic_proportions-put_trackers_on-description = Pro kalibraci proporcí použijeme trackery, které jste právě přiřadili. Nasaďte si všechny trackery a na obrázku vpravo zkontrolujte, jak je máte nasazené.
onboarding-automatic_proportions-put_trackers_on-next = Mám nasazené všechny trackery
onboarding-automatic_proportions-requirements-title = Požadavky
# Each line of text is a different list item
onboarding-automatic_proportions-requirements-descriptionv2 =
    Minimálně 5 trackerů: Máte dostatečný počet trackerů pro sledování nohou.
    Nasazené vybavení: Máte nasazené trackery a headset.
    Připojení a funkčnost: Trackery a headset jsou připojeny k serveru SlimeVR a fungují správně (bez záseků, odpojování apod.).
    SteamVR a SlimeVR: Headset odesílá pozici do serveru SlimeVR (obvykle je potřeba mít spuštěný SteamVR a připojený k SlimeVR pomocí ovladače SlimeVR pro SteamVR).
    Přesné sledování: Sledování funguje a přesně zaznamenává vaše pohyby (například jste provedli kompletní reset a trackery se správně pohybují při kopání, předklonu, sezení apod.).
onboarding-automatic_proportions-requirements-next = Přečetl jsem si požadavky
onboarding-automatic_proportions-check_height-title-v3 = Měření výšky vašeho headsetu
onboarding-automatic_proportions-check_height-description-v2 = Váš headset HMD výška by měla být o trochu menší než vaše celá výška, protože headset měří výšku úrovně vašich očí. Toto měření bude použito pro výchozí hodnoty vaších tělesných proporcí.
# All the text is in bold!
onboarding-automatic_proportions-check_height-calculation_warning-v3 = Započněte měření při tom když stojíte <u>vzpřímeně</u> aby jste změřili vaší výšku. Dávejte pozor aby jste nezvedly vaše ruce nad váš headset, mohlo by to ovlivnit výsledky měření!
onboarding-automatic_proportions-check_height-guardian_tip =
    Pokud používáte Samostatný VR headset, ujistěte se že váš opatrovník /
    Hranice zapnutá aby byla vaše výška správně!
# Context is that the height is unknown
onboarding-automatic_proportions-check_height-unknown = Neznámá
# Shows an element below it
onboarding-automatic_proportions-check_height-hmd_height2 = Výška vašeho headsetu je:
onboarding-automatic_proportions-check_height-measure-start = Zahájit měření
onboarding-automatic_proportions-check_height-measure-stop = Ukončit měření
onboarding-automatic_proportions-check_height-measure-reset = Opakovat měření
onboarding-automatic_proportions-check_height-next_step = Je to v pořádku!
onboarding-automatic_proportions-check_floor_height-title = Změřte vaší výšku podlahy (dobrovolné)
onboarding-automatic_proportions-check_floor_height-description = V některých případech, výška vaší podlahy nemusí být správně nastavena vaším headsetem, což způsobí že výška headsetu bude zaznamenána víš než by měla být. Můžete změřit "výšku" vaší podlahy aby jste opravily výšku vašeho headsetu
# All the text is in bold!
onboarding-automatic_proportions-check_floor_height-calculation_warning-v2 = Začněte s měřením a přiložte ovladač proti vaší podlaze pro změření její výšky. pokud jste si jistí že výška podlahy je správně nastavená, můžete tento krok přeskočit
# Shows an element below it
onboarding-automatic_proportions-check_floor_height-floor_height = Výška vaší podlahy je:
onboarding-automatic_proportions-check_floor_height-full_height = Vaše odhadnutá celá výška je:
onboarding-automatic_proportions-check_floor_height-measure-start = Zahájit měření
onboarding-automatic_proportions-check_floor_height-measure-stop = Ukončit měření
onboarding-automatic_proportions-check_floor_height-measure-reset = Opakovat měření
onboarding-automatic_proportions-check_floor_height-skip_step = Přeskočit krok a uložit
onboarding-automatic_proportions-check_floor_height-next_step = Použít výšku podlahy a uložit
onboarding-automatic_proportions-start_recording-title = Připravte se hýbat
onboarding-automatic_proportions-start_recording-description = Připravte se na nahrání několika póz a pohybů. Dostanete přesné instrukce na další obrazovce. Až budete připraveni, stiskněte tlačítko a začněte!
onboarding-automatic_proportions-start_recording-next = Spustit nahrávání
onboarding-automatic_proportions-recording-title = Nahrát
onboarding-automatic_proportions-recording-description-p0 = Probíhá nahrávání...
onboarding-automatic_proportions-recording-description-p1 = Proveďte níže uvedené pohyby:
# Each line of text is a different list item
onboarding-automatic_proportions-recording-steps =
    Stůj rovně: Postavte se rovně a vzpřímeně.
    Kroužení hlavou: Udělejte hlavou kruh, jednou kolem dokola.
    Dřep s pohledem do stran: Předkloňte se a dřepněte. V dřepu se otočte pohled doleva a doprava.
    Otáčení horní části těla: S rovnými zády se otočte horní částí těla doleva (proti směru hodinových ručiček), jako byste chtěli rukou sáhnout k zemi. Pak se otočte doprava (po směru hodinových ručiček).
    Kroužení boky: Krouživým pohybem otáčejte boky, jako byste točili hula hoop kruhem.
    Pokud zbývá čas, můžete tyto pohyby opakovat až do konce nahrávání.
onboarding-automatic_proportions-recording-processing = Zpracovávám výsledek
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 20)
onboarding-automatic_proportions-recording-timer =
    { $time ->
        [one] Zbývá 1 sekunda
        [few] Zbývají { $time } sekundy
       *[other] Zbývá { $time } sekund
    }
onboarding-automatic_proportions-verify_results-title = Ověření výsledků
onboarding-automatic_proportions-verify_results-description = Zkontrolujte výsledky níže, vypadají správně?
onboarding-automatic_proportions-verify_results-results = Zaznamenávání výsledky
onboarding-automatic_proportions-verify_results-processing = Zpracovávám výsledek
onboarding-automatic_proportions-verify_results-redo = Znovu provést záznam
onboarding-automatic_proportions-verify_results-confirm = Jsou správné
onboarding-automatic_proportions-done-title = Tělo změřeno a uloženo.
onboarding-automatic_proportions-done-description = Kalibrace proporcí vašeho těla je dokončena!
onboarding-automatic_proportions-error_modal-v2 =
    <b>Varování:</b> Nastala chyba při odhadu proporcí!
    Pravděpodobně jde o nesprávnou kalibraci umístění trackerů. Ujistěte se že vaše sledování funguje správě než to zkusíte znovu.
    Prosím <docs>Zkontrolujte dokumentaci</docs> nebo se připojte na náš <discord>Discord server</discord> a požádejte o pomoc ^_^
onboarding-automatic_proportions-error_modal-confirm = Rozumím!
onboarding-automatic_proportions-smol_warning =
    Vaše nakonfigurovaná výška { $height } je menší než minimální přijatelná výška { $minHeight }.
    <b>Proveďte prosím přeměření a ujistěte se, že jsou hodnoty správné.</b>
onboarding-automatic_proportions-smol_warning-cancel = Jít zpět

## User height calibration


## Stay Aligned setup

onboarding-stay_aligned-title = Zůstaň Srovaný!
onboarding-stay_aligned-description = Nakonfigurujte Zustaň Srovnaný, aby byly vaše trackery srovnáný.
onboarding-stay_aligned-put_trackers_on-title = Nasaďte si trackery
onboarding-stay_aligned-put_trackers_on-next = Mám nasazené všechny trackery
onboarding-stay_aligned-verify_mounting-title = Zkotrolujte nasazení
onboarding-stay_aligned-verify_mounting-step-1 = 1. Pohybujte se ve stoje.
onboarding-stay_aligned-verify_mounting-step-2 = 2. Posaďte se a pohybujte nohama a chodidly.
onboarding-stay_aligned-relaxed_poses-standing-step-0 = 1. Stůjte v pohodlné pozici. Relaxujte!
onboarding-stay_aligned-relaxed_poses-skip_step = Přeskočit
onboarding-stay_aligned-done-title = Zustaň Srovnaný zapnuto!
onboarding-stay_aligned-previous_step = Předchozí
onboarding-stay_aligned-next_step = Další
onboarding-stay_aligned-restart = Restart
onboarding-stay_aligned-done = Hotovo

## Home

home-no_trackers = Nebyly zjištěny ani přiřazeny žádné trackery

## Trackers Still On notification

trackers_still_on-modal-title = Máte trackery stále zapnuté
trackers_still_on-modal-description =
    Jeden nebo více trackerů jsou stále zapnuty.
    Opravdu chcete ukončit SlimeVR?
trackers_still_on-modal-confirm = Zavřít SlimeVR
trackers_still_on-modal-cancel = Dejte my chvilku!

## Status system

status_system-StatusTrackerReset = Pro dosažení nejlepších výsledků proveďte kompletní reset. Alespoň jeden tracker není správně nastaven.
status_system-StatusSteamVRDisconnected =
    { $type ->
        [steamvr_feeder] Nejste připojeni k SlimeVR "Feeder" aplikaci.
       *[steamvr] Nejste připojeni ke SteamVR zapomocí ovladače SlimeVR.
    }
status_system-StatusTrackerError = Tracker { $trackerName } není v pořádku.
status_system-StatusUnassignedHMD = Váš VR Headset by měl být přiřazen jako tracker hlavy.

## Firmware tool globals

firmware_tool-next_step = Další krok
firmware_tool-previous_step = Předchozí krok
firmware_tool-ok = Vypadá to dobře
firmware_tool-retry = Zkusit znovu
firmware_tool-loading = Načítání...

## Firmware tool Steps

firmware_tool = Nástroj pro DIY firmwere
firmware_tool-description = Umožní vám konfigurovat a flashovat vaše DIY trackery
firmware_tool-not_available = Jejda, nástroj pro firmware není v momentální chvíli k dispozici, Vraťte se později!
firmware_tool-not_compatible = Nástroj pro firmware není kompatibilní s touhle verzí serveru. Aktualizujte prosím svůj server.
firmware_tool-flash_method_step = Metoda flashování
firmware_tool-flash_method_step-description = Prosím zvolte metodu flashování, kterou chcete použít
firmware_tool-flashbtn_step = Stiskněte tlačítko bootu btn
firmware_tool-flashbtn_step-description = Než přejdeme na další krok, je tady pár věcí které musíte udělat
firmware_tool-flashbtn_step-board_SLIMEVR = Vypněte tracker, vyndejte z obalu (jestli v nějakém je), Připojte USB kabel k tomuto počítači a poté následujte jeden z kroků revize odpovídající k vaší verzi desky trackeru SlimeVR:
firmware_tool-flashbtn_step-board_OTHER =
    Před flashováním, pravděpodobně budete muset přepnout tracker do bootloader režimu.
    Ve většině případů to znamená stisknutí boot tlačítka na desce trakeru před tím než začne proces flashování.
    Pokud procesu flashování vyprší čas hned na začátku flashování, to nejspíš znamená že tracker nebyl v řežimu bootloaderu
    Podívejte se prosím na instrukce procesu flashování pro desku vašeho zařízení, aby jste zjistili jak se dostat do režimu bootloaderu
firmware_tool-flash_method_ota-devices = Byla detekována zařízení s OTA:
firmware_tool-flash_method_ota-no_devices = Nebyly nalezeny žádné zákadní desky které by mohly být aktualizované pomocí OTA, prosím ujistěte se že jste zvolily správný typ základní desky
firmware_tool-flash_method_serial-wifi = Přihlašovací údaje Wi-Fi:
firmware_tool-flash_method_serial-devices-label = Detekována Sériová Zařízení:
firmware_tool-flash_method_serial-devices-placeholder = Vyberte sériové zařízení
firmware_tool-flash_method_serial-no_devices = Nebyla nalezena žádná kompatibilní seriová zařízení, prosím ujistěte se že trackery jsou připojeny
firmware_tool-build_step = Sestavování
firmware_tool-build_step-description = Firmwere se sestavuje, čekejte prosím
firmware_tool-flashing_step = Flashování
firmware_tool-flashing_step-description = Probíhá flashování vašich trackerů, prosím postupujte dle instrukcí na obrazovce
firmware_tool-flashing_step-flash_more = Flashnout více trackerů
firmware_tool-flashing_step-exit = Odejít

## firmware tool build status

firmware_tool-build-CREATING_BUILD_FOLDER = Vytváření složky pro sestavení
firmware_tool-build-BUILDING = Sestavování firmweru
firmware_tool-build-SAVING = Ukládání sestavení
firmware_tool-build-DONE = Sestavení dokončeno
firmware_tool-build-ERROR = Nepodařilo se sestavit firmwere

## Firmware update status

firmware_update-status-DOWNLOADING = Stahování firmwaru
firmware_update-status-AUTHENTICATING = Autentifikování s mcu
firmware_update-status-UPLOADING = Nahrávání firmwaru
firmware_update-status-SYNCING_WITH_MCU = Synchronizace s MCU
firmware_update-status-REBOOTING = Restartování trackeru
firmware_update-status-PROVISIONING = Nastavování přihlašovacích údajů pro síť Wi-Fi
firmware_update-status-DONE = Aktualizace byla dokončena!
firmware_update-status-ERROR_DEVICE_NOT_FOUND = Zařízení se nepodařilo nalézt
firmware_update-status-ERROR_TIMEOUT = Vypršel časový limit pro proces aktualizace
firmware_update-status-ERROR_DOWNLOAD_FAILED = Nepodařilo se stáhnout firmware
firmware_update-status-ERROR_AUTHENTICATION_FAILED = Ověření MCU se nezdařilo
firmware_update-status-ERROR_UPLOAD_FAILED = Nepodařilo se nahrát firmware
firmware_update-status-ERROR_PROVISIONING_FAILED = Nepodařilo se nastavit přihlašovací údaje pro Wi-Fi
firmware_update-status-ERROR_UNSUPPORTED_METHOD = Metoda aktualizace není podporována
firmware_update-status-ERROR_UNKNOWN = Neznámá chyba

## Dedicated Firmware Update Page

firmware_update-title = Aktualizace firmwaru
firmware_update-devices = Dostupná zařízení
firmware_update-devices-description = Prosím zvolte tracker, který chcete aktualizovat na nejnovější verzi SlimeVR firmwaru
firmware_update-no_devices = Prosím ujistěte se, že tracker který chcete aktualizovat je ZAPNUTO a připojeno k Wi-Fi!
firmware_update-changelog-title = Aktualizování na { $version }
firmware_update-looking_for_devices = Hledání zařízení pro aktualizaci
firmware_update-retry = Opakovat
firmware_update-update = Aktualizovat Zvolený/é Tracker/y
firmware_update-exit = Odejít

## Tray Menu

tray_menu-show = Zobrazit
tray_menu-hide = Skrýt
tray_menu-quit = Ukončit

## First exit modal

tray_or_exit_modal-title = Co chcete aby "křížek" udělal?
# Multiline text
tray_or_exit_modal-description =
    Tímto si zvolíte, zda chcete při stisknutí tlačítka pro zavření ukončit server, nebo jej pouze minimalizovat do systémové lišty.
    
    Toto nastavení můžete později změnit v nastavení aplikace.
tray_or_exit_modal-radio-exit = Ukončit při zavření
tray_or_exit_modal-radio-tray = Minimalizovat
tray_or_exit_modal-submit = Uložit
tray_or_exit_modal-cancel = Zrušit

## Unknown device modal

unknown_device-modal-title = Byl nalezen nový tracker!
unknown_device-modal-description =
    Byl objeven nový tracker s MAC adresou <b>{ $deviceId }</b>.
    Chcete jej připojit k SlimeVR?
unknown_device-modal-confirm = Jasně!
unknown_device-modal-forget = Ignoruj
vrc_config-page-help = Nemůžete najít specifické nastavení?
vrc_config-page-big_menu = Sledování & IK (Velké Menu)
vrc_config-page-big_menu-desc = Nastavení souvicející s IK ve velké nabídce nastavení
vrc_config-page-wrist_menu = Sledování & IK (Zápěstní menu)
vrc_config-on = Zapnuto
vrc_config-off = Vypnuto
vrc_config-invalid = Máte špatně nakonfigurované VRChat nastavení!
vrc_config-show_more = Ukázat více
vrc_config-setting_name = Jméno nastavení v VRChat
vrc_config-recommended_value = Doporučená hodnota
vrc_config-current_value = Aktuální hodnota
vrc_config-mute = Upozornění na ztlumení
vrc_config-mute-btn = Ztlumení
vrc_config-unmute-btn = Zrušit ztlumení
vrc_config-legacy_mode = Použít starší řešení IK
vrc_config-disable_shoulder_tracking = Vypnout sledování ramen
vrc_config-spine_mode = Režim páteře FTB
vrc_config-tracker_model = Model FBT trackeru
vrc_config-avatar_measurement_type = Meření avataru
vrc_config-calibration_range = Kalibrační rozsah
vrc_config-calibration_visuals = Zobrazit vizualizaci kalibrace
vrc_config-user_height = Reálná výška uživatele
vrc_config-spine_mode-UNKNOWN = Neznámý
vrc_config-spine_mode-LOCK_BOTH = Uzamknout obojí
vrc_config-spine_mode-LOCK_HEAD = Uzamknout hlavu
vrc_config-spine_mode-LOCK_HIP = Uzamknout boky
vrc_config-tracker_model-UNKNOWN = Neznýmý
vrc_config-tracker_model-AXIS = Osy
vrc_config-tracker_model-BOX = Box
vrc_config-tracker_model-SPHERE = Sféra
vrc_config-tracker_model-SYSTEM = Systém
vrc_config-avatar_measurement_type-UNKNOWN = Neznámý
vrc_config-avatar_measurement_type-HEIGHT = Výška
vrc_config-avatar_measurement_type-ARM_SPAN = Rozpětí paží

## Error collection consent modal

error_collection_modal-title = Můžeme sbírat chyby?
error_collection_modal-description_v2 =
    { settings-interface-behavior-error_tracking-description_v2 }
    
    Tohle lze později změnit v sekci Chování v nastavení.
error_collection_modal-confirm = Souhlasím
error_collection_modal-cancel = Nesouhlasím

## Tracking checklist section

