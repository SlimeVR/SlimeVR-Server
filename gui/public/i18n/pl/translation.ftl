### SlimeVR complete GUI translations


# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Łączenie z serwerem
websocket-connection_lost = Połączenie z serwerem zostało utracone. Próba ponownego połączenia...

## Update notification

version_update-title = Dostępna jest nowa wersja: { $version }
version_update-description = Kliknięcie „Aktualizuj” spowoduje pobranie instalatora SlimeVR.
version_update-update = Aktualizacja
version_update-close = Zamknij

## Tips

tips-find_tracker = Nie wiesz który tracker to który? Obracaj Trackerem , podczas obracania będzie sie on podświetlał w serwerze.
tips-do_not_move_heels = Upewnij się aby pięty pozostały w bezruchu podczas nagrywania.
tips-file_select = Przeciągnij i upuść pliki, których chcesz użyć, lub <u>przeglądaj</u>.
tips-tap_setup = Możesz powoli stuknąć 2 razy tracker, aby go wybrać, zamiast wybierać go z menu.

## Body parts

body_part-NONE = Nie Przypisano
body_part-HEAD = Głowa
body_part-NECK = Szyja
body_part-RIGHT_SHOULDER = Prawe Ramie
body_part-RIGHT_UPPER_ARM = Prawy Biceps
body_part-RIGHT_LOWER_ARM = Prawe PrzedRamie
body_part-RIGHT_HAND = Prawa Dłoń
body_part-RIGHT_UPPER_LEG = Prawe Udo
body_part-RIGHT_LOWER_LEG = Prawe Podudzie
body_part-RIGHT_FOOT = Prawa Stopa
body_part-CHEST = Klatka Piersiowa
body_part-WAIST = Talia
body_part-HIP = Biodra
body_part-LEFT_SHOULDER = Lewe Ramie
body_part-LEFT_UPPER_ARM = Lewy Biceps
body_part-LEFT_LOWER_ARM = Lewe PrzedRamie
body_part-LEFT_HAND = Lewa Dłoń
body_part-LEFT_UPPER_LEG = Lewe Udo
body_part-LEFT_LOWER_LEG = Lewe Podudzie
body_part-LEFT_FOOT = Lewa Stopa

## Proportions

skeleton_bone-NONE = Brak
skeleton_bone-HEAD = Przesunięcie Głowy
skeleton_bone-NECK = Długość Szyi
skeleton_bone-torso_group = Długość torsu
skeleton_bone-CHEST = Długość Klatki Piersiowej
skeleton_bone-CHEST_OFFSET = Przesunięcie Klatki Piersiowej
skeleton_bone-WAIST = Długość Talii
skeleton_bone-HIP = Długość Bioder
skeleton_bone-HIP_OFFSET = Przesunięcie Bioder
skeleton_bone-HIPS_WIDTH = Szerokość Bioder
skeleton_bone-leg_group = Długość nóg
skeleton_bone-UPPER_LEG = Długość Górnej Części Nogi
skeleton_bone-LOWER_LEG = Długość Dolnej Części Nogi
skeleton_bone-FOOT_LENGTH = Długość Stopy
skeleton_bone-FOOT_SHIFT = Przesunięcie Stopy
skeleton_bone-SKELETON_OFFSET = Przesunięcie Szkieletu
skeleton_bone-SHOULDERS_DISTANCE = Odległość Ramion
skeleton_bone-SHOULDERS_WIDTH = Szerokość Ramion
skeleton_bone-arm_group = Długość ramienia
skeleton_bone-UPPER_ARM = Długość Bicepsa
skeleton_bone-LOWER_ARM = Długość PrzedRamienia
skeleton_bone-HAND_Y = Odległość ręki Y
skeleton_bone-HAND_Z = Odległość ręki Z
skeleton_bone-ELBOW_OFFSET = Przesunięcie Łokcia

## Tracker reset buttons

reset-reset_all = Zresetuj wszystkie wymiary
reset-full = Reset
reset-mounting = Zresetuj Położenie
reset-yaw = Reset Odchylenia

## Serial detection stuff

serial_detection-new_device-p0 = Wykryto Nowe Urządzenie.
serial_detection-new_device-p1 = Wprowadź dane Wi-Fi!
serial_detection-new_device-p2 = Wybierz co chcesz z nim zrobić.
serial_detection-open_wifi = Połącz z Wi-Fi
serial_detection-open_serial = Otwórz Konsole
serial_detection-submit = Potwierdź!
serial_detection-close = Zamknij

## Navigation bar

navbar-home = Strona Główna
navbar-body_proportions = Proporcje Ciała
navbar-trackers_assign = Przydzielenie Trackerów
navbar-mounting = Kalibracja Pozycji
navbar-onboarding = Wstępna Konfiguracja
navbar-settings = Ustawienia

## Bounding volume hierarchy recording

bvh-start_recording = Nagraj BVH
bvh-recording = Nagrywam...

## Widget: Overlay settings

widget-overlay = Overlay
widget-overlay-is_visible_label = Pokaż Overlay w SteamVR
widget-overlay-is_mirrored_label = Pokaż Overlay jako Lustro

## Widget: Drift compensation

widget-drift_compensation-clear = Wyczyść kompensację dryfu

## Widget: Developer settings

widget-developer_mode = Tryb Dewelopera
widget-developer_mode-high_contrast = Wysoki kontrast
widget-developer_mode-precise_rotation = Precise rotation
widget-developer_mode-fast_data_feed = Szybkie przesyłanie danych
widget-developer_mode-filter_slimes_and_hmd = Filtruj slimy i HMD
widget-developer_mode-sort_by_name = Sortuj według nazwy
widget-developer_mode-raw_slime_rotation = Surowa rotacja
widget-developer_mode-more_info = Więcej info

## Widget: IMU Visualizer

widget-imu_visualizer = Obrót
widget-imu_visualizer-rotation_raw = Raw
widget-imu_visualizer-rotation_preview = Podgląd
widget-imu_visualizer-rotation_hide = Ukryj

## Tracker status

tracker-status-none = Brak Statusu
tracker-status-busy = Zajęty
tracker-status-error = Error
tracker-status-disconnected = Rozłączono
tracker-status-occluded = Zablokowany
tracker-status-ok = Połączono

## Tracker status columns

tracker-table-column-name = Nazwa
tracker-table-column-type = Typ
tracker-table-column-battery = Bateria
tracker-table-column-ping = Ping
tracker-table-column-tps = TPS
tracker-table-column-temperature = Temp. °C
tracker-table-column-linear-acceleration = Accel. X/Y/Z
tracker-table-column-rotation = Rotacja X/Y/Z
tracker-table-column-position = Pozycja X/Y/Z
tracker-table-column-url = URL

## Tracker rotation

tracker-rotation-front = Przód
tracker-rotation-left = Lewa
tracker-rotation-right = Prawa
tracker-rotation-back = Tył

## Tracker information

tracker-infos-manufacturer = Producent
tracker-infos-display_name = Wyświetlana Nazwa
tracker-infos-custom_name = Niestandardowa Nazwa
tracker-infos-url = Tracker URL
tracker-infos-version = Wersja Oprogramowania
tracker-infos-hardware_rev = Wersja Sprzętu
tracker-infos-hardware_identifier = Identyfikator sprzętu
tracker-infos-imu = Czujnik IMU
tracker-infos-board_type = Płyta główna

## Tracker settings

tracker-settings-back = Wróć do listy trackerów
tracker-settings-title = Ustawienia Trackerów
tracker-settings-assignment_section = Przydzielanie
tracker-settings-assignment_section-description = Do jakiej części ciała jest przydzielony tracker.
tracker-settings-assignment_section-edit = Edytuj
tracker-settings-mounting_section = Położenie Trackera
tracker-settings-mounting_section-description = Gdzie jest Tracker zamontowany?
tracker-settings-mounting_section-edit = Edytuj
tracker-settings-drift_compensation_section = Allow drift compensation
tracker-settings-drift_compensation_section-description = Czy ten tracker powinien kompensować dryf, gdy kompensacja dryfu jest włączona?
tracker-settings-drift_compensation_section-edit = Allow drift compensation
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Nazwa Trackera
tracker-settings-name_section-description = Daj mu słodką nazwę :)
tracker-settings-name_section-placeholder = Lewa noga Yexo

## Tracker part card info

tracker-part_card-no_name = Brak Nazwy
tracker-part_card-unassigned = Nieprzydzielony

## Body assignment menu

body_assignment_menu = Gdzie chcesz żeby ten tracker był?
body_assignment_menu-description = Wybierz miejsce gdzie tracker będzie przydzielony. Alternatywnie możesz ustawić wszystkie na raz.
body_assignment_menu-show_advanced_locations = Pokaż zaawansowane położenia
body_assignment_menu-manage_trackers = Zarządzaj wszystkimi trackerami
body_assignment_menu-unassign_tracker = Usuń przydzielenie

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = Which tracker to assign to your
tracker_selection_menu-NONE = Któremu trackerowi chcesz cofnąć przypisanie?
tracker_selection_menu-HEAD = { -tracker_selection-part } head?
tracker_selection_menu-NECK = { -tracker_selection-part } szyja?
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part } prawe ramię?
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part } right upper arm?
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part } right lower arm?
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part } prawa ręka?
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part } prawe udo?
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part } prawa kostka?
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part } prawa stopa?
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part } prawy kontroler?
tracker_selection_menu-CHEST = { -tracker_selection-part } klatka piersiowa?
tracker_selection_menu-WAIST = { -tracker_selection-part } talia?
tracker_selection_menu-HIP = { -tracker_selection-part } biodro?
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part } lewe ramię?
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part } left upper arm?
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part } left lower arm?
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part } lewa ręka?
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part } lewe udo?
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part } lewa kostka?
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part } lewa stopa?
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part } lewy kontroler?
tracker_selection_menu-unassigned = Nieprzydzielone trackery
tracker_selection_menu-assigned = Przydzielone trackery
tracker_selection_menu-dont_assign = Nie przydzielaj
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Ostrzeżenie:</b> Opaska na szyję może być śmiertelna, jeśli zostanie dopasowana zbyt ciasno,
    pasek może odciąć krążenie do głowy!
tracker_selection_menu-neck_warning-done = Rozumiem ryzyko
tracker_selection_menu-neck_warning-cancel = Anuluj

## Mounting menu

mounting_selection_menu = Gdzie chciałbyś ten tracker?
mounting_selection_menu-close = Zamknij

## Sidebar settings

settings-sidebar-title = Ustawienia
settings-sidebar-general = Ogólne
settings-sidebar-tracker_mechanics = Mechanika trackerów
settings-sidebar-fk_settings = FK settings
settings-sidebar-gesture_control = Sterowanie gestami
settings-sidebar-interface = Interfejs
settings-sidebar-osc_router = OSC router
settings-sidebar-osc_trackers = Śledzenie VRChat OSC
settings-sidebar-utils = Narzędzia
settings-sidebar-serial = Konsola Seryjna

## SteamVR settings

settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = SteamVR trackers
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    Włącz lub Wyłącz specyficzne pozycje trackowania.
    Przydatne jeżeli chcesz więcej kontroli nad SlimeVR.
settings-general-steamvr-trackers-waist = Talia
settings-general-steamvr-trackers-chest = Klatka Piersiowa
settings-general-steamvr-trackers-feet = Stopy
settings-general-steamvr-trackers-knees = Kolana
settings-general-steamvr-trackers-elbows = Łokcie
settings-general-steamvr-trackers-hands = Ręce

## Tracker mechanics

settings-general-tracker_mechanics = Mechanika trackerów
settings-general-tracker_mechanics-filtering = Filtrowanie
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Wybierz Filtry dla twoich trackerów.
    Przewidywanie przewiduje ruchy a Wygładzanie stara się wygładzić ruchy.
settings-general-tracker_mechanics-filtering-type = Filtry
settings-general-tracker_mechanics-filtering-type-none = Brak Filtrów
settings-general-tracker_mechanics-filtering-type-none-description = Używa rotacji takimi jakimi są.
settings-general-tracker_mechanics-filtering-type-smoothing = Wygładzanie
settings-general-tracker_mechanics-filtering-type-smoothing-description = Wygładza ruchy lecz dodaje trochę opóźnienia.
settings-general-tracker_mechanics-filtering-type-prediction = Przewidywanie
settings-general-tracker_mechanics-filtering-type-prediction-description = Zmniejsza opóźnienie i robi ruchy trochę ostrzejszymi, ale może dodać trochę drgań.
settings-general-tracker_mechanics-filtering-amount = Ilość
settings-general-tracker_mechanics-drift_compensation = Kompensacja dryfu
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Kompensuje dryf odchylenia IMU poprzez zastosowanie odwrotnej rotacji.
    Zmień wysokość kompensacji i do ilu resetów jest branych pod uwagę.
settings-general-tracker_mechanics-drift_compensation-enabled-label = Kompensacja dryfu
settings-general-tracker_mechanics-drift_compensation-amount-label = Compensation amount
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Użyj maksymalnie x ostatnich resetów

## FK/Tracking settings

settings-general-fk_settings = Ustawienia śledzenia
# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Klip podłogowy
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Korekta jazdy na łyżwach
settings-general-fk_settings-leg_tweak-toe_snap = Pstryknięcie palcem
settings-general-fk_settings-leg_tweak-foot_plant = Korekta stopy
settings-general-fk_settings-leg_tweak-skating_correction-amount = Skating correction strength
settings-general-fk_settings-leg_tweak-skating_correction-description = Korekta jazdy na łyżwach koryguje jazdę na łyżwach, ale może zmniejszyć dokładność niektórych wzorców ruchu. Włączając tę opcję, pamiętaj o pełnym zresetowaniu i ponownej kalibracji w grze.
settings-general-fk_settings-leg_tweak-floor_clip-description = Floor-clip może zmniejszyć lub nawet wyeliminować przecinanie podłogi. Włączając tę opcję, pamiętaj o pełnym zresetowaniu i ponownej kalibracji w grze.
settings-general-fk_settings-leg_tweak-toe_snap-description = Toe-snap próbuje odgadnąć obrót twoich stóp, jeśli trackery stóp nie są używane.
settings-general-fk_settings-leg_tweak-foot_plant-description = Foot-plant obraca stopy, aby były równoległe do podłoża podczas kontaktu.
settings-general-fk_settings-leg_fk = Śledzenie nóg
settings-general-fk_settings-arm_fk = Śledzenie ramienia
settings-general-fk_settings-arm_fk-description = Zmień sposób śledzenia ramion.
settings-general-fk_settings-arm_fk-force_arms = Śledź ramiona z gogli VR
settings-general-fk_settings-skeleton_settings = Ustawienia szkieletu
settings-general-fk_settings-skeleton_settings-description = Włącz lub wyłącz ustawienia szkieletu. Zaleca się pozostawienie ich włączonych.
settings-general-fk_settings-skeleton_settings-extended_spine = Wydłużony kręgosłup
settings-general-fk_settings-skeleton_settings-extended_pelvis = Rozszerzona miednica
settings-general-fk_settings-skeleton_settings-extended_knees = Wydłużone kolano
settings-general-fk_settings-vive_emulation-title = Emulacja Vive
settings-general-fk_settings-vive_emulation-description = Naśladuj problemy z trackerem talii, które mają trackery Vive. To żart i pogarsza śledzenie.
settings-general-fk_settings-vive_emulation-label = Włącz emulację Vive

## Gesture control settings (tracker tapping)

settings-general-gesture_control = Kontrola Gestami
settings-general-gesture_control-subtitle = Dotknij 2 razy by wykonać szybki reset
settings-general-gesture_control-description = Umożliwia wyzwalanie resetów przez stuknięcie modułu śledzącego. Układ śledzący znajdujący się najwyżej na tułowiu służy do resetowania odchylenia, układ śledzący znajdujący się najwyżej na lewej nodze służy do pełnego resetu, a układ śledzący znajdujący się najwyżej na prawej nodze służy do resetowania montażu. Należy wspomnieć, że stuknięcia muszą nastąpić w ciągu 0,6 sekundy, aby zostały zarejestrowane.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
        [one] 1 dotknięcie
        [few] { $amount } dotknięcia
        [many] { $amount } dotknięć
       *[other] { $amount } dotknięć
    }
settings-general-gesture_control-yawResetEnabled = Włącz stuknięcie, aby zresetować odchylanie
settings-general-gesture_control-yawResetDelay = Opóźnienie resetowania odchylenia
settings-general-gesture_control-yawResetTaps = Stuknięcie do resetowania odchylenia
settings-general-gesture_control-fullResetEnabled = Włącz stuknięcie, aby całkowicie zresetować
settings-general-gesture_control-fullResetDelay = Pełne opóźnienie resetu
settings-general-gesture_control-fullResetTaps = Stuknij do pełnego resetu
settings-general-gesture_control-mountingResetEnabled = Włącz stuknięcie, aby zresetować położenie
settings-general-gesture_control-mountingResetDelay = Opóźnienie resetowania położenia
settings-general-gesture_control-mountingResetTaps = Stuknięcie do resetowania położenia

## Interface settings

settings-general-interface = Interfejs
settings-general-interface-dev_mode = Tryb Dewelopera
settings-general-interface-dev_mode-description = Ten tryb przydaje się do sprawdzania większej ilości danych.
settings-general-interface-dev_mode-label = Tryb Dewelopera
settings-general-interface-serial_detection = Wykrywanie urządzeń
settings-general-interface-serial_detection-description = Ta opcja daje powiadomienia jeżeli serwer wykryje urządzenie które może być trackerem
settings-general-interface-serial_detection-label = Wykrywanie urządzeń
settings-general-interface-feedback_sound = Dźwięk Informacji
settings-general-interface-feedback_sound-description = Ta opcja odtworzy dźwięk, gdy reset zostanie uruchomiony
settings-general-interface-feedback_sound-label = Dźwięk Informacji
settings-general-interface-feedback_sound-volume = Poziom głośności sprzężenia zwrotnego
settings-general-interface-theme = Motyw kolorystyczny
settings-general-interface-lang = Wybierz Język
settings-general-interface-lang-description = Zmień podstawowy język jaki chcesz używać
settings-general-interface-lang-placeholder = Wybierz Język który będziesz używać

## Serial settings

settings-serial = Serial Console
# This cares about multilines
settings-serial-description =
    To jest źródło informacji na żywo dla komunikacji szeregowej.
    Może być przydatny, jeśli chcesz wiedzieć, czy oprogramowanie układowe działa.
settings-serial-connection_lost = Utracono połączenie z portem szeregowym, ponowne łączenie...
settings-serial-reboot = Ponowne uruchomienie
settings-serial-factory_reset = Przywrócenie ustawień fabrycznych
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>Ostrzeżenie:</b> Spowoduje to zresetowanie trackera do ustawień fabrycznych.
    Co oznacza, że ustawienia Wi-Fi i kalibracji <b>zostaną utracone!</b>
settings-serial-factory_reset-warning-ok = Wiem co robię
settings-serial-factory_reset-warning-cancel = Anuluj
settings-serial-get_infos = Uzyskaj informacje
settings-serial-serial_select = Wybierz port szeregowy
settings-serial-auto_dropdown_item = Auto

## OSC router settings

settings-osc-router = OSC router
# This cares about multilines
settings-osc-router-description =
    Przekaż wiadomości OSC z innego programu.
    Przydatne na przykład do używania innego programu OSC z VRChat.
settings-osc-router-enable = Zezwól
settings-osc-router-enable-description = Zezwól na przekazywanie wiadomości.
settings-osc-router-enable-label = Zezwól
settings-osc-router-network = Porty sieciowe
# This cares about multilines
settings-osc-router-network-description =
    Ustaw porty do odbierania i wysyłania danych.
    Mogą to być takie same porty, jak inne porty używane na serwerze SlimeVR.
settings-osc-router-network-port_in =
    .label = Port In
    .placeholder = Port in (default: 9002)
settings-osc-router-network-port_out =
    .label = Port Out
    .placeholder = Port out (default: 9000)
settings-osc-router-network-address = Adres sieciowy
settings-osc-router-network-address-description = Ustaw adres, na który mają być wysyłane dane.
settings-osc-router-network-address-placeholder = IPV4 address

## OSC VRChat settings

settings-osc-vrchat = VRChat OSC Trackers
# This cares about multilines
settings-osc-vrchat-description =
    Zmień ustawienia specyficzne dla VRChat, aby odbierać i wysyłać dane HMD
    dane trackerów dla FBT (działa na samodzielnym Quest).
settings-osc-vrchat-enable = Zezwól
settings-osc-vrchat-enable-description = Zezwól na wysyłanie i odbieranie danych.
settings-osc-vrchat-enable-label = Zezwól
settings-osc-vrchat-network = Porty sieciowe
settings-osc-vrchat-network-description = Ustaw porty do odbierania i wysyłania danych do VRChat.
settings-osc-vrchat-network-port_in =
    .label = Port In
    .placeholder = Port in (default: 9001)
settings-osc-vrchat-network-port_out =
    .label = Port Out
    .placeholder = Port out (default: 9000)
settings-osc-vrchat-network-address = Adres sieciowy
settings-osc-vrchat-network-address-description = Wybierz adres, na który chcesz wysłać dane do VRChat (sprawdź ustawienia Wi-Fi na swoim urządzeniu).
settings-osc-vrchat-network-address-placeholder = VRChat ip address
settings-osc-vrchat-network-trackers = Trackers
settings-osc-vrchat-network-trackers-description = Przełącz wysyłanie określonych trackerów przez OSC.
settings-osc-vrchat-network-trackers-chest = Klatka piersiowa
settings-osc-vrchat-network-trackers-hip = Biodro
settings-osc-vrchat-network-trackers-knees = Kolana
settings-osc-vrchat-network-trackers-feet = Stopy
settings-osc-vrchat-network-trackers-elbows = Łokcie

## VMC OSC settings

settings-osc-vmc = Virtual Motion Capture
# This cares about multilines
settings-osc-vmc-description =
    Zmień ustawienia specyficzne dla protokołu VMC (Virtual Motion Capture).
    aby wysyłać dane z kości SlimeVR i odbierać dane kości z innych aplikacji.
settings-osc-vmc-enable = Umożliwiać
settings-osc-vmc-enable-description = Przełącz wysyłanie i odbieranie danych.
settings-osc-vmc-enable-label = Umożliwiać
settings-osc-vmc-network = Porty sieciowe
settings-osc-vmc-network-description = Ustaw porty do odbierania i wysyłania danych przez VMC
settings-osc-vmc-network-port_in =
    .label = Port Wejścia
    .placeholder = Port Wejścia (domyślnie: 39540)
settings-osc-vmc-network-port_out =
    .label = Port Wyjścia
    .placeholder = Port Wyjścia (domyślnie: 39539)
settings-osc-vmc-network-address = Adres sieciowy
settings-osc-vmc-network-address-description = Wybierz adres, na który chcesz wysłać dane przez WRR
settings-osc-vmc-network-address-placeholder = IPV4 adres
settings-osc-vmc-vrm = Model VRM
settings-osc-vmc-vrm-description = Załaduj model VRM, aby umożliwić zablokowanie głowy i zapewnić większą kompatybilność z innymi aplikacjami
settings-osc-vmc-vrm-model_unloaded = Nie załadowano modelu
settings-osc-vmc-vrm-model_loaded =
    { $titled ->
        [true] Model załadowany: { $name }
       *[other] Załadowano model bez nazwy
    }
settings-osc-vmc-vrm-file_select = Przeciągnij i upuść model, którego chcesz użyć, lub <u>przeglądaj</u>
settings-osc-vmc-anchor_hip = Blokada na biodrach
settings-osc-vmc-anchor_hip-description = Zablokuj śledzenie na biodrach, przydatne podczas siedzenia VTubing. W przypadku wyłączenia załaduj model VRM.
settings-osc-vmc-anchor_hip-label = Blokada na biodrach

## Setup/onboarding menu

onboarding-skip = Pomiń wstępną konfiguracje
onboarding-continue = Kontynuuj
onboarding-wip = W trakcie prac
onboarding-previous_step = Poprzedni krok
onboarding-setup_warning =
    <b>Ostrzeżenie:</b> konfiguracja jest konieczna do dobrego śledzenia,
    i jest to wymagane, jeśli używasz SlimeVR po raz pierwszy.
onboarding-setup_warning-skip = Pomiń konfigurację
onboarding-setup_warning-cancel = Kontynuuj konfigurację

## Wi-Fi setup

onboarding-wifi_creds-back = Cofnij się do początku
onboarding-wifi_creds = Wpisz dane Wi-Fi
# This cares about multilines
onboarding-wifi_creds-description =
    Trackery będą używać tej sieci do łączenia się z serwerem
    proszę używać sieci do której jest się połączonym
onboarding-wifi_creds-skip = Pomiń ustawienia Wi-Fi
onboarding-wifi_creds-submit = Potwierdź!
onboarding-wifi_creds-ssid =
    .label = SSID
    .placeholder = Enter SSID
onboarding-wifi_creds-password =
    .label = Password
    .placeholder = Enter password

## Mounting setup

onboarding-reset_tutorial-back = Powrót do Konfiguracji Położenia trackerów
onboarding-reset_tutorial = Zresetuj poradnik
onboarding-reset_tutorial-description = Ta funkcja jeszcze nie jest skończona.

## Setup start

onboarding-home = Witamy w SlimeVR
onboarding-home-start = Zaczynajmny!

## Enter VR part of setup

onboarding-enter_vr-back = Cofnij do Przydzielania Trackerów
onboarding-enter_vr-title = Czas na wejście do VR!
onboarding-enter_vr-description = Załóż wszystkie trackery a potem wejdź do VR!
onboarding-enter_vr-ready = Jestem gotów

## Setup done

onboarding-done-title = Wszystko ustawione!
onboarding-done-description = Ciesz się Full-Body
onboarding-done-close = Zamknij Poradnik

## Tracker connection setup

onboarding-connect_tracker-back = Cofnij się do ustawień Wi-Fi
onboarding-connect_tracker-title = Połącz trackery
onboarding-connect_tracker-description-p0 = Teraz czas na zabawę, połączenie wszystkich trackerów!
onboarding-connect_tracker-description-p1 = Po prostu połącz wszystkie dotychczas nie połączone trackery za pomocą USB
onboarding-connect_tracker-issue-serial = Mam problemy z połączeniem!
onboarding-connect_tracker-usb = USB Tracker
onboarding-connect_tracker-connection_status-none = Szukanie Trackerów
onboarding-connect_tracker-connection_status-serial_init = Łączenie z urządzeniem szeregowym
onboarding-connect_tracker-connection_status-provisioning = Wysyłanie danych Wi-Fi
onboarding-connect_tracker-connection_status-connecting = Wysyłanie danych Wi-Fi
onboarding-connect_tracker-connection_status-looking_for_server = Szukanie serwera
onboarding-connect_tracker-connection_status-connection_error = Nie można połączyć się z Wi-Fi
onboarding-connect_tracker-connection_status-could_not_find_server = Nie można znaleźć serwera
onboarding-connect_tracker-connection_status-done = Połączono z serwerem
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers =
    { $amount ->
        [0] Brak podłączonych trackerów
        [one] 1 podłączony tracker
        [few] { $amount } podłączone trackery
        [many] { $amount } połączonych trackerów
       *[other] { $amount } połączonych trackerów
    }
onboarding-connect_tracker-next = Połączyłem już wszystkie trackery

## Tracker calibration tutorial

onboarding-calibration_tutorial = Samouczek kalibracji IMU
onboarding-calibration_tutorial-subtitle = Pomoże to ograniczyć dryf trackera!
onboarding-calibration_tutorial-description = Za każdym razem, gdy włączasz trackery, muszą one chwilę polerzeć na płaskiej powierzchni, aby się skalibrować. Zróbmy to samo, klikając przycisk „Kalibruj”, <b>nie ruszaj ich!</b>
onboarding-calibration_tutorial-calibrate = Położyłem trackery na stole
onboarding-calibration_tutorial-status-waiting = Czekam na Ciebie
onboarding-calibration_tutorial-status-calibrating = Kalibracja
onboarding-calibration_tutorial-status-success = Nieźle!
onboarding-calibration_tutorial-status-error = Tracker został przeniesiony

## Tracker assignment setup

onboarding-assign_trackers-back = Cofnij się do ustawień Wi-Fi
onboarding-assign_trackers-title = Przydziel Trackery
onboarding-assign_trackers-description = Wybierzmy gdzie idzie jaki tracker. Naciśnij gdzie chcesz go przydzielić
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned =
    { $trackers ->
        [one] { $assigned } z 1 przypisanego trackera
        [few] { $assigned } z { $trackers } przypisanych trackerów
        [many] Przypisano { $assigned } z { $trackers } trackerów
       *[other] Przypisano { $assigned } z { $trackers } trackerów
    }
onboarding-assign_trackers-advanced = Pokaż zaawansowane ustawienia pozycji
onboarding-assign_trackers-next = Przydzieliłem już wszystkie trackery

## Tracker assignment warnings

# Note for devs, number is used for representing boolean states per bit.
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_FOOT =
    { $unassigned ->
        [0] Lewa stopa jest przypisana, ale musisz przypisać również lewą kostkę, lewe udo i klatkę piersiową, biodro lub talię!
        [1] Lewa stopa jest przypisana, ale musisz przypisać również lewe udo i klatkę piersiową, biodro lub talię!
        [2] Lewa stopa jest przypisana, ale musisz przypisać również lewą kostkę i klatkę piersiową, biodro lub talię!
        [3] Lewa stopa jest przypisana, ale musisz też przypisać klatkę piersiową, biodro lub talię!
        [4] Lewa stopa jest przypisana, ale musisz przypisać również lewą kostkę i lewe udo!
        [5] Lewa stopa jest przypisana, ale musisz przypisać również lewe udo!
        [6] Lewa stopa jest przypisana, ale musisz przypisać również lewą kostkę!
       *[unknown] Lewa stopa jest przypisana, ale potrzebujesz również nieznanej nieprzypisanej części ciała!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_FOOT =
    { $unassigned ->
        [0] Prawa stopa jest przypisana, ale potrzebujesz również przypisać prawą kostkę, prawe udo i klatkę piersiową, biodro lub talię!
        [1] Prawa stopa jest przypisana, ale musisz przypisać również prawe udo i klatkę piersiową, biodro lub talię!
        [2] Prawa stopa jest przypisana, ale potrzebujesz również przypisać prawą kostkę i klatkę piersiową, biodro lub talię!
        [3] Prawa stopa jest przypisana, ale musisz też przypisać klatkę piersiową, biodro lub talię!
        [4] Prawa stopa jest przypisana, ale potrzebujesz również przypisać prawą kostkę i prawe udo!
        [5] Prawa stopa jest przypisana, ale potrzebne jest również przypisanie prawego uda!
        [6] Prawa stopa jest przypisana, ale potrzebujesz również przypisać prawą kostkę!
       *[unknown] Prawa stopa jest przypisana, ale potrzebujesz również nieznanej nieprzypisanej części ciała!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_LOWER_LEG =
    { $unassigned ->
        [0] Lewa kostka jest przypisana, ale musisz przypisać również lewe udo i klatkę piersiową, biodro lub talię!
        [1] Lewa kostka jest przypisana, ale musisz też przypisać klatkę piersiową, biodro lub talię!
        [2] Lewa kostka jest przypisana, ale musisz przypisać również lewe udo!
       *[unknown] Lewa kostka jest przypisana, ale potrzebujesz również przypisać nieznaną nieprzypisaną część ciała!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_LOWER_LEG =
    { $unassigned ->
        [0] Prawa kostka jest przypisana, ale musisz przypisać również prawe udo i klatkę piersiową, biodro lub talię!
        [1] Prawa kostka jest przypisana, ale musisz też przypisać klatkę piersiową, biodro lub talię!
        [2] Prawa kostka jest przypisana, ale potrzebne jest również przypisanie prawego uda!
       *[unknown] Prawa kostka jest przypisana, ale potrzebujesz również przypisać nieznaną nieprzypisaną część ciała!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_UPPER_LEG =
    { $unassigned ->
        [0] Lewe udo jest przypisane, ale musisz też przypisać klatkę piersiową, biodro lub talię!
       *[unknown] Lewe udo jest przypisane, ale potrzebujesz również przypisać nieznaną nieprzypisaną część ciała!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_UPPER_LEG =
    { $unassigned ->
        [0] Prawe udo jest przypisane, ale musisz też przypisać klatkę piersiową, biodro lub talię!
       *[unknown] Prawe udo jest przypisane, ale potrzebujesz również przypisać nieznaną nieprzypisaną część ciała!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-HIP =
    { $unassigned ->
        [0] Biodro jest przypisane, ale musisz też przypisać klatkę piersiową!
       *[unknown] Biodro jest przypisane, ale potrzebujesz również nieznanej nieprzypisanej części ciała!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-WAIST =
    { $unassigned ->
        [0] Talia jest przypisana, ale klatka piersiowa również musi być przypisana!
       *[unknown] Talia jest przypisana, ale potrzebujesz również nieznanej nieprzypisanej części ciała!
    }

## Tracker mounting method choose

onboarding-choose_mounting = Jakiej metody kalibracji montażu użyć?
onboarding-choose_mounting-auto_mounting = Automatyczne mocowanie
# Italized text
onboarding-choose_mounting-auto_mounting-subtitle = Zalecana
onboarding-choose_mounting-auto_mounting-description = To automatycznie wykryje kierunki montażu dla wszystkich twoich trackerów z 2 pozycji
onboarding-choose_mounting-manual_mounting = Montaż ręczny
# Italized text
onboarding-choose_mounting-manual_mounting-subtitle = Jeśli wiesz, co robisz
onboarding-choose_mounting-manual_mounting-description = Umożliwi to ręczne wybranie kierunku montażu dla każdego trackera

## Tracker manual mounting setup

onboarding-manual_mounting-back = Cofnij się żeby wejść do VR
onboarding-manual_mounting = Pozycjonowanie Manualne
onboarding-manual_mounting-description = Kliknij na każdy tracker i wybierz w jaki sposób są zamontowane
onboarding-manual_mounting-auto_mounting = Automatyczne połączenie
onboarding-manual_mounting-next = Następny krok

## Tracker automatic mounting setup

onboarding-automatic_mounting-back = Cofnij się żeby wejść do VR
onboarding-automatic_mounting-title = Kalibracja Pozycji
onboarding-automatic_mounting-description = Aby SlimeVR działało prawidłowo, musimy przypisać rotacje trackera aby zgadzała się ona z tą w prawdziwym życiu.
onboarding-automatic_mounting-manual_mounting = Pozycjonowanie Manualne
onboarding-automatic_mounting-next = Następny krok
onboarding-automatic_mounting-prev_step = Poprzedni krok
onboarding-automatic_mounting-done-title = Rotacja trackerów została skalibrowana.
onboarding-automatic_mounting-done-description = Kalibracja skończona!
onboarding-automatic_mounting-done-restart = Cofnij się na początek
onboarding-automatic_mounting-mounting_reset-title = Kalibracja Pozycji
onboarding-automatic_mounting-mounting_reset-step-0 = 1. Zrób pozycje "na Małysza" z wygiętymi nogami, tułów pochylony do przodu z wygiętymi rękami.
onboarding-automatic_mounting-mounting_reset-step-1 = 2. Naciśnij "Zresetuj Położenie" i poczekaj 3 sekundy zanim trackery się zresetują.
onboarding-automatic_mounting-preparation-title = Przygotowania
onboarding-automatic_mounting-preparation-step-0 = 1. Stań prosto z rękami wyciągniętymi na bok.
onboarding-automatic_mounting-preparation-step-1 = 2. Naciśnij "Reset" i poczekaj 3 sekundy zanim trackery się zresetują.
onboarding-automatic_mounting-put_trackers_on-title = Załóż trackery
onboarding-automatic_mounting-put_trackers_on-description = Aby skalibrować rotacje, użyjemy trackerów które przed chwilą przypisałeś. Załóż wszystkie trackery, będziesz widział który to który na postaci po prawej.
onboarding-automatic_mounting-put_trackers_on-next = Mam wszystkie trackery założone

## Tracker proportions method choose

onboarding-choose_proportions = Jakiej metody kalibracji proporcji użyć?
onboarding-choose_proportions-auto_proportions = Proporcje automatyczne
# Italized text
onboarding-choose_proportions-auto_proportions-subtitle = Zalecana
onboarding-choose_proportions-auto_proportions-description = To odgadnie twoje proporcje, rejestrując próbkę twoich ruchów i przepuszczając ją przez sztuczną inteligencję
onboarding-choose_proportions-manual_proportions = Ręczne proporcje
# Italized text
onboarding-choose_proportions-manual_proportions-subtitle = Drobne detale
onboarding-choose_proportions-manual_proportions-description = Umożliwi to ręczne dostosowanie proporcji poprzez ich bezpośrednią modyfikację

## Tracker manual proportions setup

onboarding-manual_proportions-back = Wróć do samouczka resetowania
onboarding-manual_proportions-title = Manualne Proporcje Ciała
onboarding-manual_proportions-precision = Precyzyjna Regulacja
onboarding-manual_proportions-auto = Automatyczna Kalibracja
onboarding-manual_proportions-ratio = Dostosuj według grup proporcji

## Tracker automatic proportions setup

onboarding-automatic_proportions-back = Wróć do samouczka resetowania
onboarding-automatic_proportions-title = Zmierz swoje ciało
onboarding-automatic_proportions-description = Aby SlimeVR działało poprawnie, musimy znać długość twoich kości. Ta kalibracja zrobi to za ciebie.
onboarding-automatic_proportions-manual = Kalibracja Manualna
onboarding-automatic_proportions-prev_step = Poprzedni krok
onboarding-automatic_proportions-put_trackers_on-title = Załóż trackery
onboarding-automatic_proportions-put_trackers_on-description = Aby skalibrować proporcje, użyjemy trackerów które przed chwilą przypisałeś. Załóż wszystkie trackery, będziesz widział który to który na postaci po prawej.
onboarding-automatic_proportions-put_trackers_on-next = Mam wszystkie trackery założone
onboarding-automatic_proportions-requirements-title = Wymagania
# Each line of text is a different list item
onboarding-automatic_proportions-requirements-description =
    Masz co najmniej wystarczającą liczbę trackerów do śledzenia twoich stóp (zazwyczaj 5 trackerów).
    Masz włączone trackery i zestaw VR.
    Masz na sobie trackery i zestaw VR.
    Twoje urządzenia śledzące i zestaw VR są połączone z serwerem SlimeVR.
    Twoje urządzenia śledzące i zestaw VR działają poprawnie na serwerze SlimeVR.
    Twój zestaw VR przesyła dane pozycyjne do serwera SlimeVR (oznacza to ogólnie, że SteamVR działa i jest połączony ze SlimeVR za pomocą sterownika SlimeVR dla SteamVR).
onboarding-automatic_proportions-requirements-next = Zapoznałem się z wymaganiami
onboarding-automatic_proportions-start_recording-title = Bądź gotowy żeby się ruszać
onboarding-automatic_proportions-start_recording-description = Będziemy teraz nagrywać specyficzne pozycje i ruchy. Będą one pokazane w następnym okienku. Bądź gotowy po naciśnięciu przycisku!
onboarding-automatic_proportions-start_recording-next = Uruchom nagrywanie
onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = Nagrywanie w toku...
onboarding-automatic_proportions-recording-description-p1 = Wykonuj ruchy pokazane niżej:
# Each line of text is a different list item
onboarding-automatic_proportions-recording-steps =
    Stojąc prosto, obracaj głowę w kółko.
    Pochyl plecy do przodu i zrób przysiad. Podczas kucania spójrz w lewo, a następnie w prawo.
    Obróć górną część ciała w lewo (przeciwnie do ruchu wskazówek zegara), a następnie sięgnij w dół w kierunku ziemi.
    Obróć górną część ciała w prawo (zgodnie z ruchem wskazówek zegara), a następnie sięgnij w dół w kierunku podłoża.
    Obracaj biodrami okrężnymi ruchami, jakbyś używał hula-hoop.
    Jeśli na nagraniu pozostało trochę czasu, możesz powtarzać te czynności, aż do zakończenia.
onboarding-automatic_proportions-recording-processing = Przetwarzanie wyników
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 20)
onboarding-automatic_proportions-recording-timer =
    { $time ->
        [one] Pozostała 1 sekunda
        [few] Pozostały { $time } sekundy
        [many] Pozostało { $time } sekund
       *[other] Pozostało { $time } sekund
    }
onboarding-automatic_proportions-verify_results-title = Zweryfikuj Wyniki
onboarding-automatic_proportions-verify_results-description = Sprawdź wyniki poniżej, czy są prawidłowe?
onboarding-automatic_proportions-verify_results-results = Wyniki Nagrywania
onboarding-automatic_proportions-verify_results-processing = Przetwarzanie wyniku
onboarding-automatic_proportions-verify_results-redo = Powtórz Nagrywanie
onboarding-automatic_proportions-verify_results-confirm = Są Prawidłowe
onboarding-automatic_proportions-done-title = Zmierzono oraz Zapisano.
onboarding-automatic_proportions-done-description = Twoja kalibracja ciała została zakończona!

## Home

home-no_trackers = Nie wykryto trackerów
