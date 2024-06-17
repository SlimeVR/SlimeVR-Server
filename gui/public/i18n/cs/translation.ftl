# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Připojování k serveru
websocket-connection_lost = Ztraceno spojení se serverem. Pokouším se znovu připojit...

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
tracking-paused = Pokračovat se sledováním

## Widget: Overlay settings

widget-overlay = Překrytí
widget-overlay-is_visible_label = Zobrazit překrytí v SteamVR
widget-overlay-is_mirrored_label = Zobrazit překrytí jako zrcadlo

## Widget: Drift compensation

widget-drift_compensation-clear = Vymazat kompenzaci driftu

## Widget: Clear Reset Mounting

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
widget-imu_visualizer-rotation_raw = Nezpracované
widget-imu_visualizer-rotation_preview = Náhled
widget-imu_visualizer-rotation_hide = Skrýt

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
tracker-infos-imu = Senzor IMU
tracker-infos-board_type = Základní deska
tracker-infos-network_version = Verze protokolu

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
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Název trackeru
tracker-settings-name_section-description = Třeba nějakou roztomilou přezdívku :)
tracker-settings-name_section-placeholder = Erimelova levá tlapka
tracker-settings-forget = Zapomenout tracker
tracker-settings-forget-label = Zapomenout tracker

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
tracker_selection_menu-NECK = Který tracker přiřadit ke krku?
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
tracker_selection_menu-HIP = Který tracker přiřadit ke kyčli?
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
settings-sidebar-fk_settings = Nastavení trackování
settings-sidebar-gesture_control = Ovládání gesty
settings-sidebar-interface = Rozhraní
settings-sidebar-osc_router = OSC router
settings-sidebar-osc_trackers = VRChat OSC tracker
settings-sidebar-utils = Nástroje
settings-sidebar-serial = Sériová konzole
settings-sidebar-appearance = Vzhled
settings-sidebar-notifications = Notifikace

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
settings-general-steamvr-trackers-tracker_toggling = Automatické přiřazení trackeru
settings-general-steamvr-trackers-tracker_toggling-description = Automaticky zapne trackery ve SteamVR v závislosti na aktuálním přiřazením trackerů.
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
settings-general-tracker_mechanics-drift_compensation = Kompenzace driftu
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Kompenzuje vychýlení IMU použitím inverzní rotace.
    Změňte velikost kompenzace a počet resetů, které jsou brány v úvahu.
settings-general-tracker_mechanics-drift_compensation-enabled-label = Kompenzace driftu
settings-general-tracker_mechanics-drift_compensation-amount-label = Množství kompenzace
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Použít až x posledních obnovení
settings-general-tracker_mechanics-save_mounting_reset-enabled-label = Uložit "Kalibraci nasazení"

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
settings-general-fk_settings-leg_fk-reset_mounting_feet-description = Aktivovat reset nasazení nohou stoupnutím na špičky.
settings-general-fk_settings-leg_fk-reset_mounting_feet = Reset nasazení nohou
settings-general-fk_settings-arm_fk = Trackování ramen
settings-general-fk_settings-arm_fk-description = Vynutit sledování rukou z VR headsetu, i když jsou k dispozici údaje o poloze rukou z trackerů.
settings-general-fk_settings-arm_fk-force_arms = Vynutit ruce z VR Headsetu
settings-general-fk_settings-arm_fk-reset_mode-description = Nastavte pózu rukou použitá pro reset nasazení.
settings-general-fk_settings-arm_fk-back = Paže dozadu
settings-general-fk_settings-arm_fk-back-description = Výchozí režim: paže směřují dozadu, předloktí dopředu.
settings-general-fk_settings-arm_fk-tpose_up = T-póza (ruce nahoru)
settings-general-fk_settings-arm_fk-tpose_up-description = Před zahájením plného resetu, očekává že stojíte vzpřímeně a máte paže volně spuštěné podél těla. A pro reset umístění zaujměte uvolněný postoj a pomalu zvedněte paže do pozice Téčka (90 stupňů jako písmeno T).
settings-general-fk_settings-arm_fk-tpose_down = T-póza (ruce dolů)
settings-general-fk_settings-arm_fk-tpose_down-description = Před zahájením plného resetu, očekává že zaujmete uvolněný postoj a pomalu zvednete paže do pozice Téčka (90 stupňů jako písmeno T). A pro reset umístění, že stojíte vzpřímeně a máte paže volně spuštěné podél těla.
settings-general-fk_settings-arm_fk-forward = Vpřed
settings-general-fk_settings-arm_fk-forward-description = Ideální pozice pro Vtubing: zvedněte paže do 90 stupňového úhlu. (90 stupňů jako písmeno T).
settings-general-fk_settings-skeleton_settings-description = Zapnutí nebo vypnutí nastavení kostry. Je doporučeno je ponechat zapnuté.
settings-general-fk_settings-self_localization-description = Režim Mocap je experimentální funkce, která dokáže přibližně určit polohu vašeho těla bez VR Headsetu a dalších trackerů. Pro správnou funkci je však nutné mít trackery pro nohy a hlavu.
settings-general-fk_settings-vive_emulation-title = Vive emulace
settings-general-fk_settings-vive_emulation-description = Napodobit problémy Vive trackerů. Tohle je jenom pro vtip a zhoršuje trackování.
settings-general-fk_settings-vive_emulation-label = Povolení emulace Vive

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
settings-general-gesture_control-mountingResetEnabled = Povolit klepnutí pro resetování montáže
settings-general-gesture_control-mountingResetDelay = Zpoždění resetování montáže
settings-general-gesture_control-mountingResetTaps = Klepnutí pro resetování montáže
settings-general-gesture_control-numberTrackersOverThreshold-description = Zvyšte tuto hodnotu, pokud detekce klepnutí nefunguje. Nepřekračujte ji nad hodnotu, která je potřebná k tomu, aby detekce klepnutí fungovala, protože by to mohlo způsobit více falešně pozitivních klepnutí.

## Appearance settings

settings-interface-appearance = Vzhled
settings-general-interface-dev_mode = Vývojářský režim
settings-general-interface-dev_mode-description = Tento režim může být užitečný, pokud potřebujete podrobné údaje nebo omunikovat s trackerama na pokročilejší úrovni.
settings-general-interface-dev_mode-label = Vývojářský režim
settings-general-interface-theme = Barva tématu
settings-general-interface-lang = Zvolte jazyk
settings-general-interface-lang-description = Změňte výchozí jazyk, který chcete používat.
settings-general-interface-lang-placeholder = Zvolte jazyk, který chcete používat.
# Keep the font name untranslated
settings-interface-appearance-font = Font rozhraní
settings-interface-appearance-font-placeholder = Toto změní písmo používané v rozhraní.
settings-interface-appearance-font-os_font = Systémový font
settings-interface-appearance-font-slime_font = Výchozí font
settings-interface-appearance-font_size = Výchozí velikost písma
settings-interface-appearance-font_size-description = Toto ovlivňuje velikost písma celého rozhraní, s výjimkou panelu nastavení.

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
settings-general-interface-use_tray = Minimalizovat do oznamovací oblasti
settings-general-interface-use_tray-description = Umožňuje vám zavřít okno, aniž byste zavřeli SlimeVR Server, takže ho můžete nadále používat bez rozhraní.
settings-general-interface-use_tray-label = Minimalizovat do oznamovací oblasti

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
settings-serial-get_infos = Získat informace
settings-serial-serial_select = Vyberte sériový port
settings-serial-auto_dropdown_item = Auto
settings-serial-get_wifi_scan = Skenovat WiFi

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
settings-osc-vrchat-description =
    Úprava nastavení specifických pro VRChat pro příjem dat HMD a odesílání
    data trackerů pro FBT (funguje na samostatném Questu).
settings-osc-vrchat-enable = Zapnout
settings-osc-vrchat-enable-description = Vypnutí a zapnutí odesílání a přijímání dat.
settings-osc-vrchat-enable-label = Zapnout
settings-osc-vrchat-network = Síťové porty
settings-osc-vrchat-network-description = Nastavení portů pro naslouchání a odesílání dat do VRChat.
settings-osc-vrchat-network-port_in =
    .label = Vstup portu
    .placeholder = Vstup portu (výchozí: 9001)
settings-osc-vrchat-network-port_out =
    .label = Výstup portu
    .placeholder = Výstup portu (výchozí: 9000)
settings-osc-vrchat-network-address = Síťová adresa
settings-osc-vrchat-network-address-description = Zvolte adresu, ze které se mají odesílat data na VRChat (zkontrolujte nastavení Wi-Fi v zařízení).
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
settings-osc-vmc-vrm-model_unloaded = Není načten žádný model
settings-osc-vmc-vrm-model_loaded =
    { $titled ->
        [true] Model načten: { $name }
       *[false] Načten neznámý model
    }
settings-osc-vmc-vrm-file_select = Přetáhněte zde model, který chcete použít, nebo <u>procházejte</u>
settings-osc-vmc-anchor_hip = Zakotvit v bocích
settings-osc-vmc-anchor_hip-description = Zakotvit sledování u boků, užitečné pro VTubing kde sedíte. Pokud je deaktivováno, načíst VRM model.
settings-osc-vmc-anchor_hip-label = Zakotvit v bocích

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
onboarding-wifi_creds = Zadání přihlašovacích údajů k Wi-Fi
# This cares about multilines
onboarding-wifi_creds-description =
    Sledovací zařízení budou tato přihlašovací údaje používat k připojení.
    Použijte prosím přihlašovací údaje, ke kterým jste aktuálně připojeni.
onboarding-wifi_creds-skip = Přeskočit nastavení Wi-Fi
onboarding-wifi_creds-submit = Odeslat!
onboarding-wifi_creds-ssid =
    .label = Název Wi-Fi
    .placeholder = Zadejte název Wi-Fi
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

## Enter VR part of setup

onboarding-enter_vr-back = Zpět na přiřazení trackerů
onboarding-enter_vr-title = Čas vstoupit do VR!
onboarding-enter_vr-description = Nasaďte si všechny trackery a pak vstupte do VR!
onboarding-enter_vr-ready = Jsem připraven

## Setup done

onboarding-done-title = Vše je připraveno!
onboarding-done-description = Užijte si zážitek s full body tracking
onboarding-done-close = Zavřít průvodce

## Tracker connection setup

onboarding-connect_tracker-back = Zpět na přihlašovací údaje Wi-Fi
onboarding-connect_tracker-title = Připojení trackerů
onboarding-connect_tracker-issue-serial = Mám potíže s připojením!
onboarding-connect_tracker-usb = USB Tracker
onboarding-connect_tracker-connection_status-none = Hledám trackery
onboarding-connect_tracker-connection_status-serial_init = Připojuji se k sériovému zařízení
onboarding-connect_tracker-connection_status-provisioning = Odesílám přihlašovací údaje WiFi
onboarding-connect_tracker-connection_status-connecting = Pokouším se připojit k WiFi
onboarding-connect_tracker-connection_status-looking_for_server = Hledám server
onboarding-connect_tracker-connection_status-connection_error = Nelze se připojit k síti Wi-Fi
onboarding-connect_tracker-connection_status-could_not_find_server = Nelze najít server
onboarding-connect_tracker-connection_status-done = Připojeno k serveru
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
onboarding-calibration_tutorial-description = Po každém zapnutí trackerů je potřeba je na chvíli položit na rovný povrch, aby se zkalibrovaly. Stejný postup provedeme teď kliknutím na tlačítko "{ onboarding-calibration_tutorial-calibrate }". <b>Během kalibrace jimi prosím nehýbejte!</b>
onboarding-calibration_tutorial-calibrate = Položil jsem trackery na stůl
onboarding-calibration_tutorial-status-waiting = Čekám na tebe
onboarding-calibration_tutorial-status-calibrating = Kalibruji
onboarding-calibration_tutorial-status-success = Super!
onboarding-calibration_tutorial-status-error = Trackerem bylo pohnuto.

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
# Italized text
onboarding-choose_mounting-auto_mounting-label = Experimentální
onboarding-choose_mounting-auto_mounting-description = Orientace nasazení všech trackerů bude automaticky rozpoznána ze 2 pozic.
onboarding-choose_mounting-manual_mounting = Manuální nastavení
# Italized text
onboarding-choose_mounting-manual_mounting-label = Doporučeno
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
onboarding-automatic_mounting-preparation-step-0 = 1. Postavte se vzpřímeně s rukama na bocích.
onboarding-automatic_mounting-preparation-step-1 = 2. Stiskněte tlačítko "Reset" a počkejte 3 sekundy, než se trackery resetují.
onboarding-automatic_mounting-put_trackers_on-title = Nasaďte si trackery
onboarding-automatic_mounting-put_trackers_on-description = Pro kalibraci směru nasazení použijeme právě přiřazené trackery. Nasaďte si prosím všechny trackery. Můžete zkontrolovat jejich umístění na obrázku vpravo.
onboarding-automatic_mounting-put_trackers_on-next = Mám nasazené všechny trackery

## Tracker proportions method choose

onboarding-choose_proportions = Jakou metodu kalibrace chcete použít?
# Multiline string
onboarding-choose_proportions-description-v1 =
    Proporce těla potřebujeme, abychom dobře změřili tvoje pohyby. Trackerům totiž pomáhají zjistit, kde přesně se na těle nachází.
    Když jsou proporce nastaveny špatně, sledování nebude fungovat dobře. Může to způsobit efekt kde tvůj avatar divně klouže, nebo že se divně hýbe.
    <b>Proporce stačí změřit jednou a máš to vyřešené!</b> Pokud nezměníš výrazně postavu nebo to napoprvé nedopadne dobře, nemusíš už nic měřit.
onboarding-choose_proportions-auto_proportions = Automatická detekce proporcí
# Italized text
onboarding-choose_proportions-auto_proportions-subtitle = Doporučeno
onboarding-choose_proportions-auto_proportions-descriptionv3 =
    Toto odhadne vaše proporce z krátkého záznamu vašich pohybů pomocí speciálního algoritmu.
    
    <b>K tomu JE NUTNÉ mít VR headset připojený k SlimeVR a nasazený na hlavě!</b>
onboarding-choose_proportions-manual_proportions = Manuální nastavení proporcí
# Italized text
onboarding-choose_proportions-manual_proportions-subtitle = Pro drobné úpravy
onboarding-choose_proportions-manual_proportions-description = Nastavte si vaše proporce ručně dle vašich potřeb.
onboarding-choose_proportions-export = Exportovat proporce
onboarding-choose_proportions-import = Importovat proporce
onboarding-choose_proportions-import-success = Importováno!
onboarding-choose_proportions-import-failed = Import byl neúspěšný
onboarding-choose_proportions-file_type = Soubor tělesných proporcí

## Tracker manual proportions setup

onboarding-manual_proportions-back = Zpět na tutoriál
onboarding-manual_proportions-title = Manuální proporce těla
onboarding-manual_proportions-precision = Přesná úprava
onboarding-manual_proportions-auto = Automatická kalibrace proporcí
onboarding-manual_proportions-ratio = Upravit podle poměrů končetin

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
onboarding-automatic_proportions-check_height-title = Ujistěte se, že máte správnou výšku
onboarding-automatic_proportions-check_height-description = Vaši výšku využíváme jako základ pro měření pohybů. Pro přibližné zjištění výšky používáme výšku vašeho VR headsetu, ale je přesnější, když si svoji skutečnou výšku zadáte sami!
# All the text is in bold!
onboarding-automatic_proportions-check_height-calculation_warning = Pro změření výšky stiskněte tlačítko <u>ve vzpřímeném postoji.</u> Máte na to 3 sekundy od stisknutí!
onboarding-automatic_proportions-check_height-fetch_height = Stojím vzpřímeně!
# Context is that the height is unknown
onboarding-automatic_proportions-check_height-unknown = Neznámá
# Shows an element below it
onboarding-automatic_proportions-check_height-hmd_height1 = Výška vašeho headsetu je
# Shows an element below it
onboarding-automatic_proportions-check_height-height1 = takže vaše výška by měla být
onboarding-automatic_proportions-check_height-next_step = Je to v pořádku!
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
onboarding-automatic_proportions-error_modal = <b>Chyba:</b> Při odhadování proporcí došlo k chybě! Prosím, přečtěte si <docs>dokumentaci</docs> nebo se připojte k našemu serveru <discord>Discord</discord> a požádejte o pomoc ^_^
onboarding-automatic_proportions-error_modal-confirm = Rozumím!

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

unknown_device-modal-confirm = Jasně!
unknown_device-modal-forget = Ignoruj
