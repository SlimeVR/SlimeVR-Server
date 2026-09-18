# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Načítání...
websocket-connection_lost = Ztraceno spojení se serverem. Pokouším se znovu připojit...
websocket-connection_lost-desc = Vypadá to že SlimeVR server spadl. Zkontrolujte záznamy protokolů a restartuje aplikaci
websocket-timedout = Nepodařilo se připojit k serveru
websocket-timedout-desc = Vypadá to že buď vypršel časový limit SlimeVR serveru, a nebo došlo k zhroucení. Zkontrolujte záznamy protokolů a restartuje aplikaci
websocket-error-close = Zavřít SlimeVR
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
tips-failed_webgl = Načtení WebGL selhalo.

## Units

unit-meter = Metr
unit-inch = Palec

## Dropdown


## Text input


## File input


## Window controls

titlebar-close = Zavřít

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

board_type-UNKNOWN = Neznýmý
board_type-CUSTOM = Vlastní deska
board_type-WRANGLER = Wrangler Joycony
board_type-GLOVE_IMU_SLIMEVR_DEV = SlimeVR vývojářská IMU rukavice
board_type-GESTURES = Gesta
board_type-GENERIC_NRF = Obecné nRF

## Proportions

skeleton_bone-NONE = Žádný
skeleton_bone-HEAD = Posun hlavy
skeleton_bone-NECK = Délka krku
skeleton_bone-torso_group = Délka trupu
skeleton_bone-UPPER_CHEST = Horní délka hrudníku
skeleton_bone-HIP = Délka kyčlí
skeleton_bone-HIPS_WIDTH = Šířka kyčlí
skeleton_bone-leg_group = Délka nohy
skeleton_bone-UPPER_LEG = Délka horní části nohy
skeleton_bone-LOWER_LEG = Délka dolní části nohy
skeleton_bone-FOOT_LENGTH = Délka chodidla
skeleton_bone-FOOT_LENGTH-desc =
    Toto je vzdálenost mezi vaši kotníky a prsty na nohou.
    Pro upravení, Choďte po špičkách dokud vaše virtuální nohy nezůstanou na místě.
skeleton_bone-FOOT_SHIFT = Odsazení chodidla
skeleton_bone-SHOULDERS_DISTANCE = Vzdálenost ramen
skeleton_bone-SHOULDERS_WIDTH = Šířka ramen
skeleton_bone-arm_group = Délka paže
skeleton_bone-UPPER_ARM = Délka nadloktí
skeleton_bone-LOWER_ARM = Délka podloktí
skeleton_bone-HAND_Y = Vzdálenost ruky na ose Y
skeleton_bone-HAND_Z = Vzdálenost ruky na ose Z

## Tracker reset buttons

reset-reset_all = Obnovit nastavení proporcí
reset-reset_all_warning-reset = Obnovit proporce
reset-reset_all_warning-cancel = Zrušit
reset-full = Plný Reset
reset-mounting = Zkotrolujte nasazení
reset-mounting-feet = Obnovit pozice nasazení nohou
reset-mounting-fingers = Obnovit pozice nasazení prstů
reset-yaw = Rychlý reset

## Navigation bar

navbar-home = Domů
navbar-body_proportions = Tělesné proporce
navbar-trackers_assign = Přiřazení trackerů
navbar-mounting = Zkotrolujte nasazení
navbar-onboarding = Průvodce nastavením
navbar-settings = Nastavení
navbar-connect_trackers = Připojte Trackery

## Biovision hierarchy recording

bvh-start_recording = Nahrát BVH
bvh-stop_recording = Uložit BVH záznam
bvh-recording = Nahrávání...
bvh-save_title = Uložit BVH záznam

## Tracking pause

tracking-unpaused = Pozastavit sledování
tracking-paused = Pokračovat v sledování

## Widget: Developer settings

widget-developer_mode = Vývojářský režim
widget-developer_mode-high_contrast = Vysoký kontrast
widget-developer_mode-precise_rotation = Přesná rotace
widget-developer_mode-fast_data_feed = Rychlý přenos dat
widget-developer_mode-raw_slime_rotation = Nezpracované

## Widget: IMU Visualizer

widget-imu_visualizer = Rotace
widget-imu_visualizer-preview = Náhled
widget-imu_visualizer-hide = Skrýt
widget-imu_visualizer-rotation_raw = Nezpracované
widget-imu_visualizer-rotation_preview = Náhled
widget-imu_visualizer-acceleration = Akcelerace
widget-imu_visualizer-position = Pozice
widget-imu_visualizer-stay_aligned = Zůstaň Srovnaný!

## Tracker status

tracker-status-none = Žádný stav
tracker-status-busy = Zaneprázdněný
tracker-status-error = Chyba
tracker-status-disconnected = Odpojeno
tracker-status-occluded = Zakrytý
tracker-status-timed_out = Spojení přerušeno

## Tracker status columns

tracker-table-column-name = Název
tracker-table-column-type = Typ
tracker-table-column-battery = Baterie
tracker-table-column-temperature = Teplota °C
tracker-table-column-linear-acceleration = Akcel. X/Y/Z
tracker-table-column-rotation = Rotace X/Y/Z
tracker-table-column-position = Pozice X/Y/Z
tracker-table-column-stay_aligned = Zůstaň Srovnaný!

## Tracker rotation

tracker-rotation-front = Přední
tracker-rotation-front_left = Vpředu vlevo
tracker-rotation-front_right = Vpředu vpravo
tracker-rotation-left = Levá
tracker-rotation-right = Pravá
tracker-rotation-back = Paže dozadu
tracker-rotation-back_left = Vzadu vlevo
tracker-rotation-back_right = Vzadu vpravo
tracker-rotation-custom = Vlastní nastavení

## Tracker information

tracker-infos-manufacturer = Výrobce
tracker-infos-display_name = Zobrazený název
tracker-infos-custom_name = Vlastní název
tracker-infos-url = URL Trackeru
tracker-infos-hardware_identifier = ID hardwaru
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
tracker-infos-packet_loss = Ztráta Paketů
tracker-infos-packets_lost = Pakety Ztraceny
tracker-infos-packets_received = Pakety Přijaty

## Tracker settings

tracker-settings-back = Zpět na seznam trackerů
tracker-settings-title = Nastavení trackeru
tracker-settings-assignment_section = Přiřazení
tracker-settings-assignment_section-description = Na kterou část těla je tracker přiřazen?
tracker-settings-assignment_section-edit = Upravit přiřazení
tracker-settings-mounting_section = Poloha nasazení
tracker-settings-mounting_section-description = Na jakou stranu je tracker nasazený?
tracker-settings-mounting_section-edit = Upravit nasazení
tracker-settings-use_mag = Povolit magnetometr na tomto trackeru
# Multiline!
tracker-settings-use_mag-description =
    Měl by tento tracker používat magnetometer k redukci driftu když je použití magnetometru povoleno? <b> Prosím nevypínejte váš tracker při přepínání tohoto nastavení!</b>
    
    Nejprve musíte povolit používání magnetometru, <magSetting>Kliknutím zde přejdete k nastavená magnetometru</magSetting>.
tracker-settings-use_mag-label = Povolit magnetometr
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Název trackeru
tracker-settings-name_section-placeholder = Erimelova levá tlapka
tracker-settings-name_section-label = Název trackeru
tracker-settings-forget = Zapomenout tracker
tracker-settings-forget-description = Odebere tracker z SlimeVR Serveru a zabrání jeho opětovnému připojení do té doby, dokud nebude server restarován. Konfigurace trackeru nebude ztracena.
tracker-settings-forget-label = Zapomenout tracker
tracker-settings-update-unavailable-v2 = Žádné vydání nebyla nalezena
tracker-settings-update-incompatible = Nelze aktualizovat. Nekompatibilní deska nebo verze firmwaru
tracker-settings-update-low-battery = Nelze provést aktualizaci. Baterie má méně než 50%
tracker-settings-update-up_to_date = Aktuální
tracker-settings-update-blocked = Není dostupná aktualizace. Žádná jiná verze není k dispozici
tracker-settings-update = Aktualizovat nyní
tracker-settings-update-title = Verze Firmwareu
tracker-settings-current-version = Současný
tracker-settings-latest-version = Nejnovější
tracker-settings-build-date = Datum sestavení

## Dongle settings

dongle-infos-hardware_revision = Revize hardwaru
dongle-status-disconnected = Odpojeno
dongle-settings-back = Zpět na seznam trackerů
dongle-settings-update = Aktualizovat nyní
dongle-settings-update-title = Verze Firmwareu

## Tracker part card info

tracker-part_card-unassigned = Nepřiřazeno

## Body assignment menu

body_assignment_menu = Kde chcete, aby byl tento tracker umístěn?
body_assignment_menu-description = Vyberte, kam chcete tento tracker umístit. Nebo můžete spravovat všechny trackery najednou, místo jednoho po druhém.
body_assignment_menu-manage_trackers = Spravovat všechny trackery
body_assignment_menu-unassign_tracker = Zrušit přiřazení trackeru

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Varování:</b> Krční tracker může být smrtelný, pokud je popruh
    utažen příliš těsně. Popruh by mohl přerušit krevní oběh do hlavy!
tracker_selection_menu-neck_warning-done = Chápu riziko
tracker_selection_menu-neck_warning-cancel = Zrušit

## Mounting menu

mounting_selection_menu-close = Zavřít

## Sidebar settings

settings-sidebar-title = Nastavení
settings-sidebar-general = Obecné
settings-sidebar-stay_aligned = Zůstaň Srovnaný!
settings-sidebar-trackers = Trackery
settings-sidebar-interface = Rozhraní
settings-sidebar-utils = Nástroje
settings-sidebar-appearance = Vzhled
settings-sidebar-home = Domovská obrazovka
settings-sidebar-checklist = Přehled trackování
settings-sidebar-notifications = Notifikace
settings-sidebar-firmware-tool = Nástroj pro DIY firmware
settings-sidebar-vrc_warnings = Varovaní VRChat konfigurace
settings-sidebar-advanced = Pokročilé

## Bone routing settings

settings-routing-output-badge-off = Vypnuto
settings-routing-group-fingers = Prsty
settings-routing-hands-warning-cancel = Zrušit

## SteamVR / Monado output settings

settings-driver-enable = Zapnout
settings-driver-status-badge-disabled = Vypnuto

## Tracker mechanics

settings-general-tracker_mechanics-filtering = Filtrování
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Vyberte typ filtrování pro své trackery.
    Predikce předpovídá pohyb, zatímco vyhlazování pohyb vyhlazuje.
settings-general-tracker_mechanics-filtering-type-none = Žádné filtrování
settings-general-tracker_mechanics-filtering-type-none-description = Použít rotace tak, jak jsou. Nebude provedeno žádné filtrování.
settings-general-tracker_mechanics-filtering-type-smoothing = Vyhlazování
settings-general-tracker_mechanics-filtering-type-smoothing-description = Vyhlazuje pohyby, ale přidává mírné zpoždění.
settings-general-tracker_mechanics-filtering-type-prediction = Predikce
settings-general-tracker_mechanics-filtering-type-prediction-description = Zkracuje prodlevu a zrychluje pohyby, ale může způsobit třesení trackerů.
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
settings-general-tracker_mechanics-trackers_over_usb = Trackery přes USB
settings-stay_aligned-description = Zůstaň Srovnaný (Stay Aligned) redukuje drift pomocí postupného upravování vašich trackerů do vaší relaxůjící pózy.
settings-stay_aligned-setup-label = Nastavte Zůstaň Srovnaný (Stay Aligned)
settings-stay_aligned-setup-description = Musíte dokončit "Nastavení Zůstaň Srovnaný" pro zapnutí Zůstaň Srovnaný.
settings-stay_aligned-enabled-label = Upravit trackery
settings-stay_aligned-general-label = Obecné
settings-stay_aligned-relaxed_poses-label = Relaxovací Póza
settings-stay_aligned-relaxed_poses-description = Zůstaň Srovnaný používá vaše uvolněné pózy k udržení srovnání trackerů. K aktualizaci těchto póz použijte "Nastavte Zůstaň Srovnaný".
settings-stay_aligned-relaxed_poses-standing = Upravit trackery při stoje
settings-stay_aligned-relaxed_poses-sitting = Upravit pozici trackerů při sezení na židli
settings-stay_aligned-relaxed_poses-flat = Upravte pozici trackerů při sezení na zemi, nebo ležení na zádech
settings-stay_aligned-relaxed_poses-save_pose = Uložit pózu
settings-stay_aligned-relaxed_poses-reset_pose = Obnovit pózu
settings-stay_aligned-relaxed_poses-close = Zavřít
settings-stay_aligned-debug-label = Ladění
settings-stay_aligned-debug-description = Při nahlašování problémů s Zůstaň Srovnaný, prosím zahrňte vaše nastavení.
settings-stay_aligned-debug-copy-label = Zkopírovat nastavení do schránky

## Keybinds Page

settings-keybinds_full-reset = Plný Reset
settings-keybinds_yaw-reset = Rychlý reset
settings-keybinds_reset-all-button = Obnovit vše
settings-keybinds-recorder-modal-done-button = Hotovo
settings-keybinds-recorder-modal-cancel-button = Zrušit

## FK/Tracking settings

# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Clip podlahy
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
settings-general-fk_settings-leg_fk-reset_mounting_feet-v1 = Vynutit kalibraci nasazení pro trackery nohou
settings-general-fk_settings-enforce_joint_constraints = Limity kostry
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints = Prosazování omezení
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = Zabránit rotaci kloubům za jejich limit
settings-general-fk_settings-ik = Data pozice
settings-general-fk_settings-ik-use_position = Použít Data pozice
settings-general-fk_settings-arm_fk-back = Paže dozadu
settings-general-fk_settings-arm_fk-back-description = Výchozí režim: paže směřují dozadu, předloktí dopředu.
settings-general-fk_settings-arm_fk-tpose_up = T-póza (ruce nahoru)
settings-general-fk_settings-arm_fk-tpose_up-description = Před zahájením plného resetu, očekává že stojíte vzpřímeně a máte paže volně spuštěné podél těla. A pro reset umístění zaujměte uvolněný postoj a pomalu zvedněte paže do pozice Téčka (90 stupňů jako písmeno T).
settings-general-fk_settings-arm_fk-tpose_down = T-póza (ruce dolů)
settings-general-fk_settings-arm_fk-tpose_down-description = Před zahájením plného resetu, očekává že zaujmete uvolněný postoj a pomalu zvednete paže do pozice Téčka (90 stupňů jako písmeno T). A pro reset umístění, že stojíte vzpřímeně a máte paže volně spuštěné podél těla.
settings-general-fk_settings-arm_fk-forward = Vpřed
settings-general-fk_settings-arm_fk-forward-description = Ideální pozice pro Vtubing: zvedněte paže do 90 stupňového úhlu. (90 stupňů jako písmeno T).
settings-general-fk_settings-skeleton_settings-ratios = Poměry kostry
settings-general-fk_settings-skeleton_settings-ratios-description = Změňte hodnoty nastavení kostry, Po změně budete možná muset poupravit vaše proporce.
settings-general-fk_settings-self_localization-title = Režim Mocap

## Gesture control settings (tracker tapping)

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
settings-general-interface-lang = Zvolte jazyk
settings-general-interface-lang-description = Změňte výchozí jazyk, který chcete používat.
settings-general-interface-lang-placeholder = Zvolte jazyk, který chcete používat.
# Keep the font name untranslated
settings-interface-appearance-font = Font rozhraní
settings-interface-appearance-font-description = Tohle mění font který používá rozhraní
settings-interface-appearance-font-placeholder = Výchozí font
settings-interface-appearance-font-os_font = Systémový font
settings-interface-appearance-font-slime_font = Výchozí font
settings-interface-appearance-font_size = Výchozí velikost písma
settings-interface-appearance-font_size-description = Toto ovlivňuje velikost písma celého rozhraní, s výjimkou panelu nastavení.

## Notification settings

settings-interface-notifications = Notifikace
settings-general-interface-feedback_sound = Zvuk zpětné vazby
settings-general-interface-feedback_sound-description = Tato možnost spustí zvuk, když je aktivován reset.
settings-general-interface-feedback_sound-label = Zvuk zpětné vazby
settings-general-interface-feedback_sound-volume = Hlasitost zvuku zpětné vazby
settings-general-interface-connected_trackers_warning = Upozornění o připojených trackerů
settings-general-interface-connected_trackers_warning-description = Tato možnost zobrazí vyskakovací okno pokaždé, když se pokusíte opustit SlimeVR, když máte připojen jeden nebo více trackerů. Připomene vám, abyste vypnuli své trackery, až budete hotovi, abyste prodloužili životnost baterie.
settings-general-interface-connected_trackers_warning-label = Upozornění o připojených trackerech při ukončení

## Behavior settings

settings-general-interface-dev_mode = Vývojářský režim
settings-general-interface-dev_mode-label = Vývojářský režim
settings-general-interface-use_tray = Minimalizovat
settings-general-interface-use_tray-description = Umožňuje vám zavřít okno, aniž byste zavřeli SlimeVR Server, takže ho můžete nadále používat bez rozhraní.
settings-general-interface-use_tray-label = Minimalizovat
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
settings-interface-behavior-bvh_directory = Cesta pro uložení BVH záznamů
settings-interface-behavior-bvh_directory-description = Vyberte cestu k uložení záznamů BHV. namísto toho, abyste pokaždé vybírali, kam je uložit.
settings-interface-behavior-bvh_directory-label = Lokace pro BVH nahrávky

## Serial settings

settings-serial-connection_lost = Ztráta připojení k seriálu, Připojení se obnovuje...
settings-serial-reboot = Restartovat
settings-serial-factory_reset = Obnovení do továrního nastavení
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>Varování</b> Toto obnoví tovární nastavení trackeru.
    To znamená, že nastavení Wi-Fi a kalibrace <b>budou ztracena!</b>
settings-serial-factory_reset-warning-ok = Vím, co dělám!
settings-serial-factory_reset-warning-cancel = Zrušit
settings-serial-serial_select = Vyberte sériový port
settings-serial-save_logs = Uložit jako soubor
settings-serial-send_command = Odeslat
settings-serial-send_command-placeholder = Příkaz...

## OSC VRChat settings


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

