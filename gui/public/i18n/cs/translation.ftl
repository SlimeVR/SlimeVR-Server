### SlimeVR complete GUI translations


# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Připojování k serveru
websocket-connection_lost = Ztráta spojení se serverem. Pokus o obnovení připojení...

## Tips

tips-find_tracker = Nejste si jisti, který tracker je který? Zatřeste tracker a zvýrazní se odpovídající položka.
tips-do_not_move_heels = Ujistěte se, že se vaše paty během nahrávání nepohybují!

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
body_part-RIGHT_CONTROLLER = Pravý ovladač
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
body_part-LEFT_CONTROLLER = Levý ovladač

## Proportions

skeleton_bone-NONE = Žádný
skeleton_bone-HEAD = Posun hlavy
skeleton_bone-NECK = Délka krku
skeleton_bone-CHEST = Délka hrudníku
skeleton_bone-CHEST_OFFSET = Odsazení hrudníku
skeleton_bone-WAIST = Délka pasu
skeleton_bone-HIP = Délka kyčlí
skeleton_bone-HIP_OFFSET = Odsazení hrudníku
skeleton_bone-HIPS_WIDTH = Šířka kyčlí
skeleton_bone-UPPER_LEG = Délka horní části nohy
skeleton_bone-LOWER_LEG = Délka dolní části nohy
skeleton_bone-FOOT_LENGTH = Délka chodidla
skeleton_bone-FOOT_SHIFT = Odsazení chodidla
skeleton_bone-SKELETON_OFFSET = Odsazení kostry
skeleton_bone-SHOULDERS_DISTANCE = Vzdálenost ramen
skeleton_bone-SHOULDERS_WIDTH = Šířka ramen
skeleton_bone-UPPER_ARM = Délka nadloktí
skeleton_bone-LOWER_ARM = Délka podloktí
skeleton_bone-CONTROLLER_Y = Vzdálenost ovladače Y
skeleton_bone-CONTROLLER_Z = Vzdálenost ovladače Z
skeleton_bone-ELBOW_OFFSET = Odsazení loktů

## Tracker reset buttons

reset-reset_all = Obnovení všech proporcí
reset-full = Resetovat
reset-mounting = Obnovit montáž
reset-quick = Rychlý reset

## Serial detection stuff

serial_detection-new_device-p0 = Nové sériové zařízení detekováno!
serial_detection-new_device-p1 = Zadejte přihlašovací údaje Wi-Fi!
serial_detection-new_device-p2 = Vyberte prosím, co s tím chcete udělat
serial_detection-open_wifi = Připojit se k Wi-Fi
serial_detection-open_serial = Otevřít sériovou konzolu
serial_detection-submit = Odeslat!
serial_detection-close = Zavřít

## Navigation bar

navbar-home = Domů
navbar-body_proportions = Tělesné proporce
navbar-trackers_assign = Přiřazení trackerů
navbar-mounting = Montážní kalibrace
navbar-onboarding = Průvodce nastavením
navbar-settings = Nastavení

## Bounding volume hierarchy recording

bvh-start_recording = Nahrávat BVH
bvh-recording = Nahrávání...

## Widget: Overlay settings

widget-overlay = Překrytí
widget-overlay-is_visible_label = Zobrazit překrytí v SteamVR
widget-overlay-is_mirrored_label = Zobrazit překrytí jako zrcadlo

## Widget: Drift compensation

widget-drift_compensation-clear = Vymazat kompenzaci driftu

## Widget: Developer settings

widget-developer_mode = Vývojářský režim
widget-developer_mode-high_contrast = Vysoký kontrast
widget-developer_mode-precise_rotation = Přesná rotace
widget-developer_mode-fast_data_feed = Rychlý přenos dat
widget-developer_mode-filter_slimes_and_hmd = Filtrovat slimy a HMD
widget-developer_mode-sort_by_name = Seřadit podle názvu
widget-developer_mode-raw_slime_rotation = Nezpracovaná rotace
widget-developer_mode-more_info = Více informací

## Widget: IMU Visualizer

widget-imu_visualizer = Rotace
widget-imu_visualizer-rotation_raw = Nezpracované
widget-imu_visualizer-rotation_preview = Náhled

## Tracker status

tracker-status-none = Žádný stav
tracker-status-busy = Zaneprázdněný
tracker-status-error = Chyba
tracker-status-disconnected = Odpojeno
tracker-status-occluded = Zakrytý
tracker-status-ok = OK

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
tracker-rotation-left = Levá
tracker-rotation-right = Pravá
tracker-rotation-back = Zadní

## Tracker information

tracker-infos-manufacturer = Výrobce
tracker-infos-display_name = Zobrazovaný název
tracker-infos-custom_name = Vlastní název
tracker-infos-url = URL Trackeru

## Tracker settings

tracker-settings-back = Zpět na seznam trackerů
tracker-settings-title = Nastavení trackeru
tracker-settings-assignment_section = Přiřazení
tracker-settings-assignment_section-description = K jaké části těla je tracker přiřazen.
tracker-settings-assignment_section-edit = Upravit přiřazení
tracker-settings-mounting_section = Montážní poloha
tracker-settings-mounting_section-description = Kde je tracker namontován?
tracker-settings-mounting_section-edit = Upravit montáž
tracker-settings-drift_compensation_section = Povolit kompenzaci driftu
tracker-settings-drift_compensation_section-description = Měl by tento tracker kompenzovat svůj drift, když je zapnuta kompenzace driftu?
tracker-settings-drift_compensation_section-edit = Povolit kompenzaci driftu
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Název trackeru
tracker-settings-name_section-description = Dejte tomu nějakou roztomilou přezdívku :)
tracker-settings-name_section-placeholder = Levá noha NightyBeast

## Tracker part card info

tracker-part_card-no_name = Bez jména
tracker-part_card-unassigned = Nepřiřazeno

## Body assignment menu

body_assignment_menu = Kde chcete, aby tento tracker byl?
body_assignment_menu-description = Zvolte umístění, kam má být tento tracker přiřazen. Případně můžete spravovat všechny trackery najednou, místo jednoho po druhém.
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
    <b>Varování:</b> Příliš pevně nastavený tracker krku může být smrtelně nebezpečný,
    popruh může přerušit krevní oběh v hlavě!
tracker_selection_menu-neck_warning-done = Chápu rizika
tracker_selection_menu-neck_warning-cancel = Zrušit

## Mounting menu

mounting_selection_menu = Kde chcete, aby byl tento tracker umístěn?
mounting_selection_menu-close = Zavřít

## Sidebar settings

settings-sidebar-title = Nastavení
settings-sidebar-general = Obecné
settings-sidebar-tracker_mechanics = Mechanika trackerů
settings-sidebar-fk_settings = Nastavení sledování
settings-sidebar-gesture_control = Ovládání gesty
settings-sidebar-interface = Rozhraní
settings-sidebar-osc_router = OSC router
settings-sidebar-utils = Nástroje
settings-sidebar-serial = Sériová konzole

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
settings-general-steamvr-trackers-feet = Chodidla
settings-general-steamvr-trackers-knees = Kolena
settings-general-steamvr-trackers-elbows = Lokty
settings-general-steamvr-trackers-hands = Ruce

## Tracker mechanics

settings-general-tracker_mechanics = Mechanika trackerů
settings-general-tracker_mechanics-filtering = Filtrování
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Vyberte typ filtrování pro své trackery.
    Predikce předpovídá pohyb, zatímco vyhlazování pohyb vyhlazuje.
settings-general-tracker_mechanics-filtering-type = Typ filtrování
settings-general-tracker_mechanics-filtering-type-none = Žádné filtrování
settings-general-tracker_mechanics-filtering-type-none-description = Použít rotaci tak, jak e. Nebude se provádět žádné filtrování.
settings-general-tracker_mechanics-filtering-type-smoothing = Vyhlazování
settings-general-tracker_mechanics-filtering-type-smoothing-description = Vyhlazuje pohyby, ale přidává určité zpoždění.
settings-general-tracker_mechanics-filtering-type-prediction = Predikce
settings-general-tracker_mechanics-filtering-type-prediction-description = Snižuje zpoždění a zrychluje pohyby, ale může zvýšit jitter.
settings-general-tracker_mechanics-filtering-amount = Množství
settings-general-tracker_mechanics-drift_compensation = Kompenzace driftu
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Kompenzuje vychýlení IMU použitím inverzní rotace.
    Změňte velikost kompenzace a počet resetů, které jsou brány v úvahu.
settings-general-tracker_mechanics-drift_compensation-enabled-label = Kompenzace driftu

## FK/Tracking settings


## Gesture control settings (tracker tapping)


## Interface settings


## Serial settings


## OSC router settings


## OSC VRChat settings


## Setup/onboarding menu


## Wi-Fi setup


## Mounting setup


## Setup start


## Enter VR part of setup


## Setup done


## Tracker connection setup


## Tracker assignment setup


## Tracker manual mounting setup


## Tracker automatic mounting setup


## Tracker manual proportions setup


## Tracker automatic proportions setup


## Home

