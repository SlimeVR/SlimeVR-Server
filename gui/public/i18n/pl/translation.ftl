# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Łączenie z serwerem
websocket-connection_lost = Połączenie z serwerem zostało utracone. Próba ponownego połączenia...
websocket-connection_lost-desc = Wygląda na to, że serwer SlimeVR przestał działać. Sprawdź dzienniki i uruchom ponownie program
websocket-timedout = Nie można połączyć się z serwerem
websocket-timedout-desc = Wygląda na to, że serwer SlimeVR uległ awarii lub upłynął limit czasu. Sprawdź dzienniki i uruchom ponownie program
websocket-error-close = Wyjdź ze SlimeVR
websocket-error-logs = Otwórz folder dzienników

## Update notification

version_update-title = Dostępna jest nowa wersja: { $version }
version_update-description = Kliknięcie "{ version_update-update }" spowoduje pobranie instalatora SlimeVR.
version_update-update = Aktualizacja
version_update-close = Zamknij

## Tips

tips-find_tracker = Nie jesteś pewien, który tracker jest który? Potrząśnij trackerem, a podświetli on odpowiedni element.
tips-do_not_move_heels = Upewnij się, aby pięty nie ruszały się podczas nagrywania.
tips-file_select = Przeciągnij i upuść pliki, których chcesz użyć, lub <u>przeglądaj</u>.
tips-tap_setup = Możesz powoli stuknąć 2 razy tracker, aby go wybrać, zamiast wybierać go z menu.
tips-turn_on_tracker = Używasz oficjalnych trackerów SlimeVR? Pamiętaj, <b><em>aby włączyć tracker</em></b> po podłączeniu go do komputera!
tips-failed_webgl = Nie udało się zainicjalizować WebGL.

## Units

unit-meter = Metr
unit-foot = Stopa
unit-inch = Cal
unit-cm = cm

## Body parts

body_part-NONE = Nieprzypisany
body_part-HEAD = Głowa
body_part-NECK = Szyja
body_part-RIGHT_SHOULDER = Prawe ramię
body_part-RIGHT_UPPER_ARM = Prawy biceps
body_part-RIGHT_LOWER_ARM = Prawe przedramię
body_part-RIGHT_HAND = Prawa dłoń
body_part-RIGHT_UPPER_LEG = Prawe udo
body_part-RIGHT_LOWER_LEG = Prawa kostka
body_part-RIGHT_FOOT = Prawa stopa
body_part-UPPER_CHEST = Górna część klatki piersiowej
body_part-CHEST = Klatka piersiowa
body_part-WAIST = Talia
body_part-HIP = Biodra
body_part-LEFT_SHOULDER = Lewe ramię
body_part-LEFT_UPPER_ARM = Lewy biceps
body_part-LEFT_LOWER_ARM = Lewe przedramię
body_part-LEFT_HAND = Lewa dłoń
body_part-LEFT_UPPER_LEG = Lewe udo
body_part-LEFT_LOWER_LEG = Lewe podudzie
body_part-LEFT_FOOT = Lewa stopa
body_part-LEFT_THUMB_METACARPAL = Śródręcze lewego kciuka
body_part-LEFT_THUMB_PROXIMAL = Lewy kciuk proksymalnie
body_part-LEFT_THUMB_DISTAL = Lewy kciuk dystalnie
body_part-LEFT_INDEX_PROXIMAL = Lewy wskazujący proksymalny
body_part-LEFT_INDEX_INTERMEDIATE = Lewy wsakzujący pośredni
body_part-LEFT_INDEX_DISTAL = Lewy wsakzujący dystalnie
body_part-LEFT_MIDDLE_PROXIMAL = Lewy środkowy proksymalny
body_part-LEFT_MIDDLE_INTERMEDIATE = Lewy środkowy pośredni
body_part-LEFT_MIDDLE_DISTAL = Lewy środkowy dystalny
body_part-LEFT_RING_PROXIMAL = Lewy pierścień proksymalnie
body_part-LEFT_RING_INTERMEDIATE = Lewy pierścień pośredni
body_part-LEFT_RING_DISTAL = Lewy pierścień dystalnie
body_part-LEFT_LITTLE_PROXIMAL = Lewy mały proksymalnie
body_part-LEFT_LITTLE_INTERMEDIATE = Lewy mały pośredni
body_part-LEFT_LITTLE_DISTAL = Lewy mały dystalnie
body_part-RIGHT_THUMB_METACARPAL = Prawy kciuk śródręcza
body_part-RIGHT_THUMB_PROXIMAL = Prawy kciuk proksymalny
body_part-RIGHT_THUMB_DISTAL = Prawy kciuk dystalny
body_part-RIGHT_INDEX_PROXIMAL = Prawy indeks proksymalny
body_part-RIGHT_INDEX_INTERMEDIATE = Prawy indeks pośredni
body_part-RIGHT_INDEX_DISTAL = Prawy dystalny wskaźnik
body_part-RIGHT_MIDDLE_PROXIMAL = Prawy środkowy proksymalny
body_part-RIGHT_MIDDLE_INTERMEDIATE = Prawy środkowy pośredni
body_part-RIGHT_MIDDLE_DISTAL = Prawy środkowy dystalny
body_part-RIGHT_RING_PROXIMAL = Prawy pierścień proksymalny
body_part-RIGHT_RING_INTERMEDIATE = Prawy pierścień pośredni
body_part-RIGHT_RING_DISTAL = Prawy pierścień dystalny
body_part-RIGHT_LITTLE_PROXIMAL = Prawy mały proksymalny
body_part-RIGHT_LITTLE_INTERMEDIATE = Prawy mały pośredni
body_part-RIGHT_LITTLE_DISTAL = Prawy mały dystalny

## BoardType

board_type-UNKNOWN = Nieznany
board_type-NODEMCU = Kontroler NodeMCU
board_type-CUSTOM = Płytka niestandardowa
board_type-WROOM32 = WROOM32
board_type-WEMOSD1MINI = Wemos D1 Mini
board_type-TTGO_TBASE = Podstawa T TTGO
board_type-ESP01 = Zobacz materiał ESP-01
board_type-SLIMEVR = SlimeVR
board_type-SLIMEVR_DEV = SlimeVR Płytka Deweloperska
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
board_type-GLOVE_IMU_SLIMEVR_DEV = Rękawica SlimeVR Dev IMU

## Proportions

skeleton_bone-NONE = Brak
skeleton_bone-HEAD = Przesunięcie głowy
skeleton_bone-HEAD-desc =
    To jest odległość od headsetu do środka głowy.
    Aby ją dostosować, obracaj głową(lewo i prawo) i edytuj wartość do momentu kiedy ruch innych urządzeń będzie minimalny.
skeleton_bone-NECK = Długość szyi
skeleton_bone-NECK-desc =
    To jest odległość od środka głowy do podstawy szyi.
    Aby go dostosować, przesuń głowę w górę i w dół, jakbyś kiwnął głową lub przechylić głowę
    po lewej i prawej stronie i zmodyfikuj go, aż jakikolwiek ruch w innych śledzących nie będzie znikomy.
skeleton_bone-torso_group = Długość torsu
skeleton_bone-torso_group-desc =
    To jest odległość od podstawy szyi do bioder.
    Aby go dostosować, zmodyfikuj go w stojąco prosto, aż do wirtualnej linii bioder
    Zwykle z twoimi prawdziwymi.
skeleton_bone-UPPER_CHEST = Długość górnej części klatki piersiowej
skeleton_bone-UPPER_CHEST-desc =
    To jest odległość od podstawy szyi do środka klatki piersiowej.
    Aby go dostosować, odpowiednio dostosuj długość tułowia i zmodyfikuj ją w różnych pozycjach
    (Siedząc, pochylając się, leżąc itp.) Dopóki wirtualny kręgosłup nie pasuje do twojego prawdziwego.
skeleton_bone-CHEST_OFFSET = Przesunięcie klatki piersiowej
skeleton_bone-CHEST_OFFSET-desc =
    Można to dostosować, aby przesunąć wirtualny tracker klatki piersiowej w górę lub w dół, aby pomóc
    z kalibracją w niektórych grach lub aplikacjach, które mogą oczekiwać, że będzie wyższa lub niższa.
skeleton_bone-CHEST = Długość klatki piersiowej
skeleton_bone-CHEST-desc =
    To jest odległość od środka klatki piersiowej do środka kręgosłupa.
    Aby go dostosować, odpowiednio dostosuj długość tułowia i zmodyfikuj ją w różnych pozycjach
    (Siedząc, pochylając się, leżąc itp.) Dopóki wirtualny kręgosłup nie pasuje do twojego prawdziwego.
skeleton_bone-WAIST = Długość talii
skeleton_bone-WAIST-desc =
    To jest odległość od środka kręgosłupa do przycisku brzucha.
    Aby go dostosować, odpowiednio dostosuj długość tułowia i zmodyfikuj ją w różnych pozycjach
    (Siedząc, pochylając się, leżąc itp.) Dopóki wirtualny kręgosłup nie pasuje do twojego prawdziwego.
skeleton_bone-HIP = Długość bioder
skeleton_bone-HIP-desc =
    To jest odległość od przycisku brzucha do bioder
    Aby go dostosować, odpowiednio dostosuj długość tułowia i zmodyfikuj ją w różnych pozycjach
    (Siedząc, pochylając się, leżąc itp.) Dopóki wirtualny kręgosłup nie pasuje do twojego prawdziwego.
skeleton_bone-HIP_OFFSET = Przesunięcie bioder
skeleton_bone-HIP_OFFSET-desc =
    Można to dostosować, aby przesunąć wirtualny tracker bioder w górę lub w dół, aby pomóc
    z kalibracją w niektórych grach lub aplikacjach, które mogą oczekiwać, że będzie w talii.
skeleton_bone-HIPS_WIDTH = Szerokość bioder
skeleton_bone-HIPS_WIDTH-desc =
    To jest odległość między początkiem nóg.
    Aby go dostosować, wykonaj pełny reset z nogami prosto i zmodyfikuj go aż do POW
    Twoje wirtualne nogi pasują do twoich prawdziwych w poziomie.
skeleton_bone-leg_group = Długość nóg
skeleton_bone-leg_group-desc =
    To jest odległość od bioder do stóp.
    Aby go dostosować, odpowiednio dostosuj długość tułowia i zmodyfikuj ją
    Dopóki twoje wirtualne stopy nie znajdą się na tym samym poziomie co twoje prawdziwe.
skeleton_bone-UPPER_LEG = Długość górnej części nogi
skeleton_bone-UPPER_LEG-desc =
    To jest odległość od bioder do kolan.
    Aby go dostosować, odpowiednio wyregentuj długość nogi i zmodyfikuj ją
    Dopóki wirtualne kolana nie znajdą się na tym samym poziomie co twoje prawdziwe.
skeleton_bone-LOWER_LEG = Długość dolnej części nogi
skeleton_bone-LOWER_LEG-desc =
    To jest odległość od twoich kolan do kostek.
    Aby go dostosować, odpowiednio wyregentuj długość nogi i zmodyfikuj ją
    Dopóki wirtualne kolana nie znajdą się na tym samym poziomie co twoje prawdziwe.
skeleton_bone-FOOT_LENGTH = Długość stopy
skeleton_bone-FOOT_LENGTH-desc =
    To jest odległość od twoich kostek do twoich palców.
    Aby go dostosować, na palcach i zmodyfikuj, aż twoje wirtualne stopy pozostaną na miejscu.
skeleton_bone-FOOT_SHIFT = Przesunięcie stopy
skeleton_bone-FOOT_SHIFT-desc =
    Ta wartość jest poziomą odległością od kolana do kostki.
    Opowiada o dolnych nogach cofających się, gdy stoi prosto.
    Aby go dostosować, ustaw długość stopy na 0, wykonaj pełny reset i zmodyfikuj ją, aż do wirtualnegow
    Stopy ustawiają się w środku kostek.
skeleton_bone-SKELETON_OFFSET = Przesunięcie szkieletu
skeleton_bone-SKELETON_OFFSET-desc =
    Można to dostosować do przesunięcia wszystkich twoich trackerów do przodu lub do tyłu.
    Można go użyć, aby pomóc w kalibracji w niektórych grach lub aplikacjach
    To może oczekiwać, że twoi trackerzy będą bardziej naprzód.
skeleton_bone-SHOULDERS_DISTANCE = Odległość ramion
skeleton_bone-SHOULDERS_DISTANCE-desc =
    To jest pionowa odległość od podstawy szyi do ramion.
    Aby go dostosować, ustaw długość górnego ramienia na 0 i zmodyfikuj go, aż wirtualne śledzenie łokci
    Ustaw pionowo z prawdziwymi ramionami.
skeleton_bone-SHOULDERS_WIDTH = Szerokość ramion
skeleton_bone-SHOULDERS_WIDTH-desc =
    To jest pozioma odległość od podstawy szyi do ramion.
    Aby go dostosować, ustaw długość górnego ramienia na 0 i zmodyfikuj go, aż wirtualne śledzenie łokci
    Ustaw poziomo z prawdziwymi ramionami.
skeleton_bone-arm_group = Długość ramienia
skeleton_bone-arm_group-desc =
    To jest odległość od ramion do nadgarstków.
    Aby go dostosować, odpowiednio dostosuj odległość ramion, ustaw odległość dłoni y¶
    do 0 i zmodyfikuj go, aż śledzące rękę nie ustawiają się na nadgarstki.
skeleton_bone-UPPER_ARM = Długość bicepsa
skeleton_bone-UPPER_ARM-desc =
    To jest odległość od ramion do łokci.
    Aby go dostosować, odpowiednio wyregentuj długość ramienia i zmodyfikuj ją do ¶
    Twoje tropiniki łokciowe ustawiają się z twoimi prawdziwymi łokciami.
skeleton_bone-LOWER_ARM = Długość przedramienia
skeleton_bone-LOWER_ARM-desc =
    To jest odległość od twoich łokci do nadgarstków.
    Aby go dostosować, odpowiednio wyregentuj długość ramienia i zmodyfikuj ją do ¶
    Twoje tropiniki łokciowe ustawiają się z twoimi prawdziwymi łokciami.
skeleton_bone-HAND_Y = Odległość dłoni w osi Y
skeleton_bone-HAND_Y-desc =
    To jest pionowa odległość od nadgarstków do środka ręki.
    Aby dostosować go do przechwytywania ruchu, prawidłowo dostosuj długość ramienia i zmodyfikuj ją, aż będziesz
    Dręczniki ręczne ustawiają się pionowo z środkiem dłoni.
    Aby dostosować go do śledzenia łokcia z kontrolerów, ustaw długość ramienia na 0 i ¶
    Zmodyfikuj go, aż twoje urządzenia śledzące łokieć ustawiają się w pionie z nadgarstkami.
skeleton_bone-HAND_Z = Odległość dłoni w osi Z
skeleton_bone-HAND_Z-desc =
    To jest pozioma odległość od nadgarstków do środka ręki.
    Aby dostosować go do przechwytywania ruchu, ustaw go na 0.
    Aby dostosować go do śledzenia łokcia z kontrolerów, ustaw długość ramienia na 0 i ¶
    Zmodyfikuj go, aż twoich śledzących łokcia w sposób poziomo z nadgarstkami.
skeleton_bone-ELBOW_OFFSET = Przesunięcie łokcia
skeleton_bone-ELBOW_OFFSET-desc =
    Można to dostosować, aby przesunąć wirtualne urządzenia śledzące łokieć w górę lub w dół, aby pomóc
    z VRCHAT przypadkowo wiążącą tropiciel łokcia z klatką piersiową.

## Tracker reset buttons

reset-reset_all = Zresetuj wszystkie wymiary
reset-reset_all_warning-v2 =
    <b> Ostrzeżenie: </b> Twoje proporcje zostaną zresetowane do wartości domyślnych skalowanych do skonfigurowanego wysokości.
    Czy na pewno chcesz to zrobić?
reset-reset_all_warning-reset = Zresetuj proporcje
reset-reset_all_warning-cancel = Anuluj
reset-reset_all_warning_default-v2 =
    <b> Ostrzeżenie: </b> Twoja wysokość nie została skonfigurowana, twoje proporcje zostaną zresetowane do domyślnych z domyślnym wysokością.
    Czy na pewno chcesz to zrobić?
reset-full = Pełny Reset
reset-mounting = Zresetuj położenie
reset-mounting-feet = Zresetuj mocowanie stóp
reset-mounting-fingers = Zresetuj mocowanie palców
reset-yaw = Reset odchylenia
reset-error-no_feet_tracker = Nie przypisano urządzenia śledzenia stóp
reset-error-no_fingers_tracker = Nie przypisano urządzenia śledzenia palcy
reset-error-mounting-need_full_reset = Potrzebny jest pełny reset przed montażem
reset-error-yaw-need_full_reset = Potrzebny jest pełny reset przed resetem obrotu

## Serial detection stuff

serial_detection-new_device-p0 = Wykryto nowe urządzenie!
serial_detection-new_device-p1 = Wprowadź dane Wi-Fi!
serial_detection-new_device-p2 = Wybierz co chcesz z nim zrobić.
serial_detection-open_wifi = Połącz z Wi-Fi
serial_detection-open_serial = Otwórz Konsolę
serial_detection-submit = Potwierdź!
serial_detection-close = Zamknij

## Navigation bar

navbar-home = Strona Główna
navbar-body_proportions = Proporcje Ciała
navbar-trackers_assign = Przydzielenie Trackerów
navbar-mounting = Kalibracja Pozycji
navbar-onboarding = Wstępna konfiguracja
navbar-settings = Ustawienia
navbar-connect_trackers = Połącz Urządzenia

## Biovision hierarchy recording

bvh-start_recording = Nagraj BVH
bvh-stop_recording = Zapisz nagranie BVH
bvh-recording = Nagrywanie...
bvh-save_title = Zapisz nagranie BVH

## Tracking pause

tracking-unpaused = Wstrzymaj śledzenie
tracking-paused = Wznów śledzenie

## Widget: Overlay settings

widget-overlay = Nakładka
widget-overlay-is_visible_label = Wyświetlaj nakładkę w SteamVR
widget-overlay-is_mirrored_label = Wyświetlaj nakładkę w lustrzanym odbiciu

## Widget: Drift compensation

widget-drift_compensation-clear = Wyczyść kompensację dryfu

## Widget: Clear Mounting calibration

widget-clear_mounting = Wyczyść resetowanie montażu

## Widget: Developer settings

widget-developer_mode = Tryb deweloperski
widget-developer_mode-high_contrast = Wysoki kontrast
widget-developer_mode-precise_rotation = Wyświetlanie dokładniejszej rotacji
widget-developer_mode-fast_data_feed = Szybkie przesyłanie danych
widget-developer_mode-filter_slimes_and_hmd = Filtruj Slime i HMD
widget-developer_mode-sort_by_name = Sortuj według nazwy
widget-developer_mode-raw_slime_rotation = Surowa rotacja
widget-developer_mode-more_info = Więcej info

## Widget: IMU Visualizer

widget-imu_visualizer = Rotacja
widget-imu_visualizer-preview = Podgląd
widget-imu_visualizer-hide = Ukryj
widget-imu_visualizer-rotation_raw = Raw
widget-imu_visualizer-rotation_preview = Podgląd
widget-imu_visualizer-acceleration = Akceleracja
widget-imu_visualizer-position = Pozycja
widget-imu_visualizer-stay_aligned = Wyrównywanie

## Widget: Skeleton Visualizer

widget-skeleton_visualizer-preview = Podgląd szkieletu
widget-skeleton_visualizer-hide = Ukryj

## Tracker status

tracker-status-none = Brak Statusu
tracker-status-busy = Zajęty
tracker-status-error = Błąd
tracker-status-disconnected = Rozłączono
tracker-status-occluded = Zasłonięty
tracker-status-ok = OK
tracker-status-timed_out = Wygasły

## Tracker status columns

tracker-table-column-name = Nazwa
tracker-table-column-type = Typ
tracker-table-column-battery = Bateria
tracker-table-column-ping = Ping
tracker-table-column-tps = TPS
tracker-table-column-temperature = Temp. °C
tracker-table-column-linear-acceleration = Akceleracja X/Y/Z
tracker-table-column-rotation = Rotacja X/Y/Z
tracker-table-column-position = Pozycja X/Y/Z
tracker-table-column-stay_aligned = Wyrównywanie
tracker-table-column-url = URL

## Tracker rotation

tracker-rotation-front = Przód
tracker-rotation-front_left = Przedni lewy
tracker-rotation-front_right = Przedni prawy
tracker-rotation-left = Lewy
tracker-rotation-right = Prawy
tracker-rotation-back = Tył
tracker-rotation-back_left = Lewy tył
tracker-rotation-back_right = Prawy tył
tracker-rotation-custom = Własne
tracker-rotation-overriden = (nadpisany przez reset montażu)

## Tracker information

tracker-infos-manufacturer = Producent
tracker-infos-display_name = Wyświetlana Nazwa
tracker-infos-custom_name = Niestandardowa Nazwa
tracker-infos-url = Tracker URL
tracker-infos-version = Wersja oprogramowania
tracker-infos-hardware_rev = Rewizja sprzętu
tracker-infos-hardware_identifier = Identyfikator sprzętu
tracker-infos-data_support = Wsparcie danych
tracker-infos-imu = Czujnik IMU
tracker-infos-board_type = Płyta główna
tracker-infos-network_version = Wersja protokołu
tracker-infos-magnetometer = Magnetometer
tracker-infos-magnetometer-status-v1 =
    { $status ->
        [DISABLED] Wyłączony
        [ENABLED] Włączony
       *[NOT_SUPPORTED] Nieobsługiwane
    }

## Tracker settings

tracker-settings-back = Wróć do listy trackerów
tracker-settings-title = Ustawienia Trackerów
tracker-settings-assignment_section = Przydzielanie
tracker-settings-assignment_section-description = Do jakiej części ciała przydzielony jest tracker.
tracker-settings-assignment_section-edit = Edytuj przypisanie
tracker-settings-mounting_section = Położenie Trackera
tracker-settings-mounting_section-description = Gdzie zamontowany jest tracker?
tracker-settings-mounting_section-edit = Edytuj
tracker-settings-drift_compensation_section = Pozwalaj na kompensację dryfu
tracker-settings-drift_compensation_section-description = Czy ten tracker powinien być korygowany, gdy kompensacja dryfu jest włączona?
tracker-settings-drift_compensation_section-edit = Pozwalaj na kompensację dryfu
tracker-settings-use_mag = Zezwól na magnetometr na tym trackerze
# Multiline!
tracker-settings-use_mag-description =
    Czy ten tracker powinien używać magnetometru, aby zmniejszyć dryft, gdy użycie magnetometru jest dozwolone? <b>Proszę nie wyłączać trackera podczas przełączania tej opcji!</b> ¶
    ¶
    Najpierw musisz zezwolić na użycie magnetometru, <magSetting>kliknij tutaj, aby przejść do ustawienia</magSetting> .
tracker-settings-use_mag-label = Pozwól na magnetometr
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Nazwa Trackera
tracker-settings-name_section-description = Daj mu słodką nazwę :)
tracker-settings-name_section-placeholder = Lewa noga Yexo
tracker-settings-name_section-label = Nazwa Urządzenia
tracker-settings-forget = Zapomnij o trackerze
tracker-settings-forget-description = Usuwa moduł śledzący z serwera SlimeVR i uniemożliwia mu połączenie się z nim do czasu ponownego uruchomienia serwera. Konfiguracja modułu śledzącego nie zostanie utracona.
tracker-settings-forget-label = Zapomnij o trackerze
tracker-settings-update-unavailable-v2 = Nie znaleziono aktualizacji
tracker-settings-update-incompatible = Nie można zaktualizować. Niekompatybilne urządzenie lub wersja oprogramowania.
tracker-settings-update-low-battery = Nie można zaktualizować. Bateria poniżej 50%
tracker-settings-update-up_to_date = Aktualny
tracker-settings-update-blocked = Aktualizacja niedostępna. Brak innych wersji
tracker-settings-update = Zaktualizuj teraz
tracker-settings-update-title = Wersja oprogramowania
tracker-settings-current-version = Aktualny
tracker-settings-latest-version = Najnowszy

## Tracker part card info

tracker-part_card-no_name = Bez nazwy
tracker-part_card-unassigned = Nieprzydzielony

## Body assignment menu

body_assignment_menu = Gdzie chcesz przypisać ten tracker?
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
tracker_selection_menu-HEAD = { -tracker_selection-part } głowa?
tracker_selection_menu-NECK = { -tracker_selection-part } szyja?
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part } prawe ramię?
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part } prawe ramię?
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part }prawe przedramię?
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part } prawa ręka?
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part } prawe udo?
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part } prawa kostka?
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part } prawa stopa?
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part } prawy kontroler?
tracker_selection_menu-UPPER_CHEST = { -tracker_selection-part } górnej części klatki piersiowej?
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
settings-sidebar-steamvr = SteamVR
settings-sidebar-tracker_mechanics = Mechanika trackerów
settings-sidebar-stay_aligned = Wyrównywanie
settings-sidebar-fk_settings = Ustawienia śledzenia
settings-sidebar-gesture_control = Sterowanie gestami
settings-sidebar-interface = Interfejs
settings-sidebar-osc_router = OSC router
settings-sidebar-osc_trackers = Śledzenie VRChat OSC
settings-sidebar-osc_vmc = VMC
settings-sidebar-utils = Narzędzia
settings-sidebar-serial = Konsola szeregowa
settings-sidebar-appearance = Wygląd
settings-sidebar-home = Strona Główna
settings-sidebar-checklist = Lista kontrolna
settings-sidebar-notifications = Powiadomienia
settings-sidebar-behavior = Zachowanie
settings-sidebar-firmware-tool = Narzędzie do oprogramowania sprzętowego DIY
settings-sidebar-vrc_warnings = Ostrzeżenia dotyczące konfiguracji VRChat
settings-sidebar-advanced = Zaawansowany

## SteamVR settings

settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = Trackery SteamVR
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    Włącz lub Wyłącz specyficzne pozycje trackowania.
    Przydatne jeżeli chcesz więcej kontroli nad SlimeVR.
settings-general-steamvr-trackers-waist = Talia
settings-general-steamvr-trackers-chest = Klatka Piersiowa
settings-general-steamvr-trackers-left_foot = Lewa stopa
settings-general-steamvr-trackers-right_foot = Prawa stopa
settings-general-steamvr-trackers-left_knee = Lewe kolano
settings-general-steamvr-trackers-right_knee = Prawe kolano
settings-general-steamvr-trackers-left_elbow = Lewy łokieć
settings-general-steamvr-trackers-right_elbow = Prawy łokieć
settings-general-steamvr-trackers-left_hand = Lewa dłoń
settings-general-steamvr-trackers-right_hand = Prawa dłoń
settings-general-steamvr-trackers-tracker_toggling = Automatyczne przypisanie trackerów
settings-general-steamvr-trackers-tracker_toggling-description = Automatycznie obsługuje włączanie i wyłączanie wirtualnych trackerów SteamVR w zależności od bieżących przypisań fizycznych trackerów
settings-general-steamvr-trackers-tracker_toggling-label = Automatyczne przypisanie wirtualnych trackerów SteamVR
settings-general-steamvr-trackers-hands-warning =
    <b>Ostrzeżenie:</b> trackery na dłoniach zastąpią kontrolery.¶
    Nie jest to typowa konfiguracja. Czy kontynuować?
settings-general-steamvr-trackers-hands-warning-cancel = Anuluj
settings-general-steamvr-trackers-hands-warning-done = Tak

## Tracker mechanics

settings-general-tracker_mechanics = Mechanika trackerów
settings-general-tracker_mechanics-filtering = Filtrowanie
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Wybierz Filtry dla twoich trackerów.
    Przewidywanie przewiduje ruchy a Wygładzanie stara się wygładzić ruchy.
settings-general-tracker_mechanics-filtering-type = Filtry
settings-general-tracker_mechanics-filtering-type-none = Brak Filtrów
settings-general-tracker_mechanics-filtering-type-none-description = Używa rotacji w niezmienionej formie. Wszystkie filtry są wyłączone.
settings-general-tracker_mechanics-filtering-type-smoothing = Wygładzanie
settings-general-tracker_mechanics-filtering-type-smoothing-description = Wygładza ruchy lecz dodaje trochę opóźnienia.
settings-general-tracker_mechanics-filtering-type-prediction = Przewidywanie
settings-general-tracker_mechanics-filtering-type-prediction-description = Zmniejsza opóźnienie i zwiększa dynamikę ruchów, ale może dodać trochę drgań.
settings-general-tracker_mechanics-filtering-amount = Ilość
settings-general-tracker_mechanics-yaw-reset-smooth-time = Czas wygładzania resetu odchylenia (0s wyłącza wygładzanie)
settings-general-tracker_mechanics-drift_compensation = Kompensacja dryfu
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Kompensuje dryf odchylenia IMU poprzez zastosowanie odwrotnej rotacji.
    Zmień wysokość kompensacji i do ilu resetów jest branych pod uwagę.
settings-general-tracker_mechanics-drift_compensation-enabled-label = Kompensacja dryfu
settings-general-tracker_mechanics-drift_compensation-prediction = Przewidywanie kompensacji dryfu
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-prediction-description =
    Przewiduje kompensację dryfu odchylenia poza wcześniej zmierzony zakres.¶
    Włącz tę opcję, jeśli trackery stale obracają się wokół osi odchylenia.
settings-general-tracker_mechanics-drift_compensation-prediction-label = Przewidywanie kompensacji dryfu
settings-general-tracker_mechanics-drift_compensation_warning =
    <b>Ostrzeżenie:</b> Kompensacji dryftu używaj tylko wtedy, gdy chcesz zresetować¶
    bardzo często (co 5-10 minut).¶
    ¶
    Niektóre IMU podatne na częste resetowanie obejmują:¶
    Joy-Cons, owoTrack i MPU (bez najnowszego oprogramowania).
settings-general-tracker_mechanics-drift_compensation_warning-cancel = Anuluj
settings-general-tracker_mechanics-drift_compensation_warning-done = Rozumiem
settings-general-tracker_mechanics-drift_compensation-amount-label = Ilość kompensacji
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Użyj maksymalnie x ostatnich resetów
settings-general-tracker_mechanics-save_mounting_reset = Zapisz pozycję trackerów na ciele
settings-general-tracker_mechanics-save_mounting_reset-description =
    Zapisuje kalibrację pozycji trackerów na ciele pomiędzy ponownymi uruchomieniami. Użyteczne¶
    podczas noszenia stroju, w którym trackery przymocowane sa na stałe. <b>Niezalecane dla zwykłych użytkowników z odpinanymi trackerami!</b>
settings-general-tracker_mechanics-save_mounting_reset-enabled-label = Zapisz pozycję trackerów
settings-general-tracker_mechanics-use_mag_on_all_trackers = Użyj magnetometru na wszystkich trackerach IMU, które go obsługują
settings-general-tracker_mechanics-use_mag_on_all_trackers-description =
    Wykorzystuje magnetometr we wszystkich trackerach, które mają kompatybilne oprogramowanie sprzętowe, redukując dryf w stabilnych środowiskach magnetycznych.¶
    Można wyłączyć dla każdego modułu śledzącego w ustawieniach modułu śledzącego. <b>Proszę nie wyłączać żadnego modułu śledzącego podczas przełączania!</b>
settings-general-tracker_mechanics-use_mag_on_all_trackers-label = Użyj magnetometru na trackerach
settings-stay_aligned = Wyrównywanie
settings-stay_aligned-description = Wyrównywanie zmniejsza efekt driftu, stopniowo dostosowując trackery do twoich zrelaksowanych póz.
settings-stay_aligned-setup-label = Konfiguracja Opcji Wyrównywania
settings-stay_aligned-setup-description = Musisz ukończyć konfigurację, aby włączyć opcję Wyrównywania.
settings-stay_aligned-warnings-drift_compensation = ⚠ Wyłącz kompensację Driftu, będzie ona kolidować z opcją Wyrównywania!
settings-stay_aligned-enabled-label = Dostosuj trackery
settings-stay_aligned-hide_yaw_correction-label = Ukryj dopasowanie (do porównania bez opcji Wyrównywania)
settings-stay_aligned-general-label = Ogólne
settings-stay_aligned-relaxed_poses-label = Zrelaksowane pozy
settings-stay_aligned-relaxed_poses-description = Opcja Wyrównywania wykorzystuje Twoje zrelaksowane pozy, aby utrzymać trackery w jednej linii. Użyj opcji "Konfiguracja Opcji Wyrównywania", aby zaktualizować te pozy.
settings-stay_aligned-relaxed_poses-standing = Dostosuj trackery w pozycji stojącej
settings-stay_aligned-relaxed_poses-sitting = Dostosuj trackery, siedząc na krześle
settings-stay_aligned-relaxed_poses-flat = Dostosuj trackery, siedząc na podłodze lub leżąc na plecach
settings-stay_aligned-relaxed_poses-save_pose = Zapisz pozę
settings-stay_aligned-relaxed_poses-reset_pose = Zresetuj Pozycję
settings-stay_aligned-relaxed_poses-close = Zamknij
settings-stay_aligned-debug-label = Debugowanie
settings-stay_aligned-debug-description = Proszę dołączać ustawienia, podczas zgłaszania problemów z opcją Wyrównywania.
settings-stay_aligned-debug-copy-label = Skopiuj ustawienia do schowka

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
settings-general-fk_settings-leg_tweak-skating_correction-amount = Siła korekcji efektu jazdy na łyżwach
settings-general-fk_settings-leg_tweak-skating_correction-description = Korekta jazdy na łyżwach koryguje jazdę na łyżwach, ale może zmniejszyć dokładność niektórych wzorców ruchu. Włączając tę opcję, pamiętaj o pełnym zresetowaniu i ponownej kalibracji w grze.
settings-general-fk_settings-leg_tweak-floor_clip-description = Floor-clip może zmniejszyć lub nawet wyeliminować przecinanie podłogi. Włączając tę opcję, pamiętaj o pełnym zresetowaniu i ponownej kalibracji w grze.
settings-general-fk_settings-leg_tweak-toe_snap-description = Toe-snap próbuje odgadnąć obrót twoich stóp, jeśli trackery stóp nie są używane.
settings-general-fk_settings-leg_tweak-foot_plant-description = Foot-plant obraca stopy, aby były równoległe do podłoża podczas kontaktu.
settings-general-fk_settings-leg_fk = Śledzenie nóg
settings-general-fk_settings-leg_fk-reset_mounting_feet-description-v1 = Wymuś kalibracje montażu stóp podczas kalibracji pozycji.
settings-general-fk_settings-leg_fk-reset_mounting_feet-v1 = Wymuś kalibracje mocowania stóp
settings-general-fk_settings-enforce_joint_constraints = Limity szkieletowe
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints = Wymuszanie ograniczeń
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = Zapobiega obracaniu się stawów poza ich limit
settings-general-fk_settings-enforce_joint_constraints-correct_constraints = Korygowanie za pomocą ograniczeń
settings-general-fk_settings-enforce_joint_constraints-correct_constraints-description = Koryguj rotacje stawów, gdy przekraczają swój limit
settings-general-fk_settings-ik = Dane pozycji
settings-general-fk_settings-ik-use_position = Użyj danych o pozycji
settings-general-fk_settings-ik-use_position-description = Umożliwia wykorzystanie danych o pozycji z urządzeń, które je wspierają. Włączając to, upewnij się, że dokonałeś reset w aplikacji i skalibrowałeś położenie w grze.
settings-general-fk_settings-arm_fk = Śledzenie ramienia
settings-general-fk_settings-arm_fk-description = Zmień sposób śledzenia ramion.
settings-general-fk_settings-arm_fk-force_arms = Śledź ramiona z gogli VR
settings-general-fk_settings-reset_settings = Zresetuj ustawienia
settings-general-fk_settings-reset_settings-reset_hmd_pitch-description = Resetuj kąt nachylenia headsetu (rotacja pionowa) podczas pełnego resetu. Przydatne, jeśli nosisz headset na czole do VTubingu lub mocapu. Nie włączaj do VR.
settings-general-fk_settings-reset_settings-reset_hmd_pitch = Resetuj kąt nachylenia headsetu
settings-general-fk_settings-arm_fk-reset_mode-description = Zmień pozycję ramienia oczekiwaną przy resetowaniu montażu.
settings-general-fk_settings-arm_fk-back = Wstecz
settings-general-fk_settings-arm_fk-back-description = Tryb domyślny, w którym górne ramiona cofają się, a dolne ramiona przesuwają się do przodu.
settings-general-fk_settings-arm_fk-tpose_up = T-pose (w górę)
settings-general-fk_settings-arm_fk-tpose_up-description = Oczekuje, że twoje ręcę będą opuszczone podczas pelnego resetu i wystawione pod kątem 90 stopni na boki podczas resetu montażu.
settings-general-fk_settings-arm_fk-tpose_down = T-pose (w dół)
settings-general-fk_settings-arm_fk-tpose_down-description = Oczekuje, że Twoje ramiona będą ustawione pod kątem w 90 stopni na boki podczas Pełnego Resetu i w dół po bokach podczas Resetu Montażowego.
settings-general-fk_settings-arm_fk-forward = Do przodu
settings-general-fk_settings-arm_fk-forward-description = Oczekuje, że Twoje ramiona będą uniesione pod kątem 90 stopni do przodu. Przydatne w VTubingu.
settings-general-fk_settings-skeleton_settings-toggles = Przełączniki szkieletowe
settings-general-fk_settings-skeleton_settings-description = Włącz lub wyłącz ustawienia szkieletu. Zaleca się pozostawienie ich włączonych.
settings-general-fk_settings-skeleton_settings-extended_spine_model = Wydłużony model kręgosłupa
settings-general-fk_settings-skeleton_settings-extended_pelvis_model = Rozszerzony model miednicy
settings-general-fk_settings-skeleton_settings-extended_knees_model = Model z przedłużonym kolanem
settings-general-fk_settings-skeleton_settings-ratios = Proporcje szkieletu
settings-general-fk_settings-skeleton_settings-ratios-description = Zmień wartości ustawień szkieletu. Po zmianie może być konieczne dostosowanie proporcji.
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_hip = Przypisz talię od klatki piersiowej do bioder
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_legs = Przypisz talię od klatki piersiowej do nóg
settings-general-fk_settings-skeleton_settings-impute_hip_from_chest_legs = Przypisz biodro od klatki piersiowej do nóg
settings-general-fk_settings-skeleton_settings-impute_hip_from_waist_legs = Przypisz biodro od pasa do nóg
settings-general-fk_settings-skeleton_settings-interp_hip_legs = Średnie odchylenie biodra i przetoczenie nogami
settings-general-fk_settings-skeleton_settings-interp_knee_tracker_ankle = Uśrednij odchylenie i przechylenie nakolanników za pomocą kostek
settings-general-fk_settings-skeleton_settings-interp_knee_ankle = Średnie odchylenie kolan i przechylenie kostek
settings-general-fk_settings-self_localization-title = Tryb Mocap
settings-general-fk_settings-self_localization-description = Tryb Mocap pozwala szkieletowi z grubsza śledzić własną pozycję bez headsetu lub innych trackerów. Pamiętaj, że wymaga to śledzenia stóp i głowy do działania i nadal jest eksperymentalne.

## Gesture control settings (tracker tapping)

settings-general-gesture_control = Kontrola Gestami
settings-general-gesture_control-subtitle = Resetowanie na podstawie stuknięć
settings-general-gesture_control-description = Umożliwia wyzwalanie resetów przez stuknięcie modułu śledzącego. Układ śledzący znajdujący się najwyżej na tułowiu służy do resetowania odchylenia, układ śledzący znajdujący się najwyżej na lewej nodze służy do pełnego resetu, a układ śledzący znajdujący się najwyżej na prawej nodze służy do resetowania montażu. Należy wspomnieć, że stuknięcia muszą nastąpić w ciągu 0,6 sekundy, aby zostały zarejestrowane.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
        [one] 1 stuknięcie
        [few] { $amount } stuknięcia
        [many] { $amount } stuknięć
       *[other] { $amount } stuknięć
    }
# This is a unit: 3 trackers, 2 trackers, 1 tracker
# $amount (Number) - Amount of trackers
settings-general-gesture_control-trackers =
    { $amount ->
        [one] 1 tracker
        [few] 2 trackery
        [many] { $amount } trackery
       *[other] { $amount } trackery
    }
settings-general-gesture_control-yawResetEnabled = Włącz stuknięcie, aby zresetować odchylanie
settings-general-gesture_control-yawResetDelay = Opóźnienie resetowania odchylenia
settings-general-gesture_control-yawResetTaps = Stuknięć do zresetowania odchylenia
settings-general-gesture_control-fullResetEnabled = Włącz stuknięcie, aby całkowicie zresetować
settings-general-gesture_control-fullResetDelay = Pełne opóźnienie resetu
settings-general-gesture_control-fullResetTaps = Stuknięć do pełnego resetu
settings-general-gesture_control-mountingResetEnabled = Włącz stuknięcie, aby zresetować położenie
settings-general-gesture_control-mountingResetDelay = Opóźnienie resetowania położenia
settings-general-gesture_control-mountingResetTaps = Stuknięcie do resetowania położenia
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = Trackery ponad progiem
settings-general-gesture_control-numberTrackersOverThreshold-description = Zwiększ tę wartość, jeśli wykrywanie stukniecia nie działa. Nie zwiększaj go ponad to, co konieczne, ponieważ wygeneruje to fałszywe stuknięcia.

## Appearance settings

settings-interface-appearance = Wygląd
settings-general-interface-dev_mode = Tryb Dewelopera
settings-general-interface-dev_mode-description = Ten tryb przydaje się do sprawdzania większej ilości danych.
settings-general-interface-dev_mode-label = Tryb Dewelopera
settings-general-interface-theme = Motyw kolorystyczny
settings-general-interface-show-navbar-onboarding = Pokaż „{ navbar-onboarding }” na pasku nawigacyjnym
settings-general-interface-show-navbar-onboarding-description = Zmienia się to, jeśli na pasku nawigacyjnym pojawi się przycisk „{ navbar-onboarding }”.
settings-general-interface-show-navbar-onboarding-label = Pokaż „{ navbar-onboarding }”
settings-general-interface-lang = Wybierz Język
settings-general-interface-lang-description = Zmień podstawowy język jaki chcesz używać
settings-general-interface-lang-placeholder = Wybierz język, który będziesz używać
# Keep the font name untranslated
settings-interface-appearance-font = Czcionka interfejsu użytkownika
settings-interface-appearance-font-description = Spowoduje to zmianę czcionki używanej przez interfejs
settings-interface-appearance-font-placeholder = Domyślna czcionka
settings-interface-appearance-font-os_font = Czcionka systemu operacyjnego
settings-interface-appearance-font-slime_font = Domyślna czcionka
settings-interface-appearance-font_size = Skalowanie czcionki
settings-interface-appearance-font_size-description = Wpływa to na rozmiar czcionki całego interfejsu z wyjątkiem tego panelu ustawień
settings-interface-appearance-decorations = Skorzystaj z natywnych dekoracji systemowych
settings-interface-appearance-decorations-description = Nie spowoduje to wyrenderowania górnego paska interfejsu i zamiast tego użyje paska systemu operacyjnego.
settings-interface-appearance-decorations-label = Użyj domyślnych dekoracji

## Notification settings

settings-interface-notifications = Powiadomienia
settings-general-interface-serial_detection = Wykrywanie urządzeń
settings-general-interface-serial_detection-description = Ta opcja otworzy okienko, jeżeli serwer wykryje podłaczenie urządzenia, które może być trackerem.
settings-general-interface-serial_detection-label = Wykrywanie urządzeń
settings-general-interface-feedback_sound = Dźwięk zwrotny
settings-general-interface-feedback_sound-description = Ta opcja odtworzy dźwięk, gdy reset zostanie uruchomiony
settings-general-interface-feedback_sound-label = Dźwięk Informacji
settings-general-interface-feedback_sound-volume = Poziom głośności dzwięku zwrotnego
settings-general-interface-connected_trackers_warning = Ostrzeżenie o podłączonych trackerach
settings-general-interface-connected_trackers_warning-description = Ta opcja wyświetli wyskakujące okienko za każdym razem, gdy spróbujesz wyjść ze SlimeVR, mając jeden lub więcej podłączonych trackerów. Przypomina o wyłączeniu trackerów, gdy skończysz, aby wydłużyć żywotność baterii.
settings-general-interface-connected_trackers_warning-label = Ostrzeżenie o podłączonych trackerach przy wyjściu

## Behavior settings

settings-interface-behavior = Zachowanie
settings-general-interface-use_tray = Minimalizuj do zasobnika systemowego
settings-general-interface-use_tray-description = Pozwala zamknąć okno bez wyłączania serwera SlimeVR, aby używać trackerów bez interfejsu graficznego.
settings-general-interface-use_tray-label = Minimalizuj do zasobnika systemowego
settings-general-interface-discord_presence = Udostępniaj aktywność na Discordzie
settings-general-interface-discord_presence-description = Informuje Twojego klienta Discord o korzystaniu ze SlimeVR oraz o liczbie używanych trackerów IMU.
settings-general-interface-discord_presence-label = Udostępniaj aktywność na Discordzie
settings-general-interface-discord_presence-message =
    { $amount ->
        [0] Brak trackerów
        [one] Użwanie 1 trackera
        [few] Używanie { $amount } trackerów
       *[many] Używanie { $amount } trackerów
    }
settings-interface-behavior-error_tracking = Zbieranie błędów za pomocą Sentry.io
settings-interface-behavior-error_tracking-description_v2 =
    <h1>Czy wyrażasz zgodę na gromadzenie anonimowych danych o błędach?</h1>
    
    <b>Nie gromadzimy danych osobowych</b> , takich jak adres IP lub dane uwierzytelniające sieci bezprzewodowej. SlimeVR ceni Twoją prywatność!
    
    Aby zapewnić jak najlepsze wrażenia użytkownika, gromadzimy anonimowe raporty o błędach, wskaźniki wydajności i informacje o systemie operacyjnym. Pomaga nam to wykrywać błędy i problemy ze SlimeVR. Dane te są zbierane za pomocą Sentry.io.
settings-interface-behavior-error_tracking-label = Wysyłanie błędów do deweloperów
settings-interface-behavior-bvh_directory = Ścieżka do zapisywania nagrań BVH
settings-interface-behavior-bvh_directory-description = Wybierz ścieżkę domyślną, w której chcesz zapisywać nagrania BVH.
settings-interface-behavior-bvh_directory-label = Ścieżka do nagrań BVH

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
settings-serial-serial_select = Wybierz port szeregowy
settings-serial-auto_dropdown_item = Auto
settings-serial-get_wifi_scan = Skanuj sieci WiFi
settings-serial-file_type = Zwykły tekst
settings-serial-save_logs = Zapisz do pliku
settings-serial-send_command = Wyślij
settings-serial-send_command-placeholder = Polecenie...
settings-serial-send_command-warning = <b>Ostrzeżenie:</b> Wysyłanie poleceń szeregowych może prowadzić do utraty danych lub zablokowania urządzenia.
settings-serial-send_command-warning-ok = Wiem co robię
settings-serial-send_command-warning-cancel = Anuluj

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
    .label = Port wejściowy
    .placeholder = Port wejściowy (domyślnie: 9002)
settings-osc-router-network-port_out =
    .label = Port wyjściowy
    .placeholder = Port wyjściowy (domyślnie: 9000)
settings-osc-router-network-address = Adres sieciowy
settings-osc-router-network-address-description = Ustaw adres, na który mają być wysyłane dane.
settings-osc-router-network-address-placeholder = IPV4 address

## OSC VRChat settings

settings-osc-vrchat = VRChat OSC Trackers
# This cares about multilines
settings-osc-vrchat-description-v1 =
    Zmień ustawienia specyficzne dla standardu OSC Trackers używanego do wysyłania¶
    śledzenie danych do aplikacji bez SteamVR (np. samodzielny Quest).¶
    Upewnij się, że włączyłeś OSC w VRChat poprzez menu akcji w opcji OSC włączone.¶
    Aby zezwolić na odbieranie danych HMD i kontrolera z VRChat, przejdź do menu głównego¶
    ustawienia w obszarze Śledzenie i IK Zezwalaj na wysyłanie danych OSC śledzenia głowy i nadgarstka VR.
settings-osc-vrchat-enable = Zezwól
settings-osc-vrchat-enable-description = Zezwól na wysyłanie i odbieranie danych.
settings-osc-vrchat-enable-label = Zezwól
settings-osc-vrchat-oscqueryEnabled = Włącz OSCQuery
settings-osc-vrchat-oscqueryEnabled-description =
    OSCQuery automatycznie wykrywa uruchomione instancje VRChat i wysyła im dane.
    Może również reklamować się do nich w celu otrzymania danych HMD i administratora.
    Aby zezwolić na odbieranie danych HMD i kontrolera z VRChat, przejdź do ustawień menu głównego
    w sekcji "Tracking & IK" i włącz opcję "Zezwalaj na wysyłanie danych OSC śledzenia głowy i nadgarstka VR".
settings-osc-vrchat-oscqueryEnabled-label = Włącz OSCQuery
settings-osc-vrchat-network = Porty sieciowe
settings-osc-vrchat-network-description-v1 = Ustaw porty do odbierania i wysyłania danych. Nie trzeba tego zmieniać dla VRChata.
settings-osc-vrchat-network-port_in =
    .label = Port In
    .placeholder = Port in (default: 9001)
settings-osc-vrchat-network-port_out =
    .label = Port Out
    .placeholder = Port out (default: 9000)
settings-osc-vrchat-network-address = Adres sieciowy
settings-osc-vrchat-network-address-description-v1 = Wybierz adres, na który mają być wysyłane dane. Można pozostawić domyślnie dla VRChata.
settings-osc-vrchat-network-address-placeholder = Adres IP VRChata
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
settings-osc-vmc-vrm-untitled_model = Model bez nazwy
settings-osc-vmc-vrm-file_select = Przeciągnij i upuść model, którego chcesz użyć, lub <u>przeglądaj</u>
settings-osc-vmc-anchor_hip = Blokada na biodrach
settings-osc-vmc-anchor_hip-description = Zablokuj śledzenie na biodrach, przydatne podczas siedzenia VTubing. W przypadku wyłączenia załaduj model VRM.
settings-osc-vmc-anchor_hip-label = Blokada na biodrach
settings-osc-vmc-mirror_tracking = Odbicie lustrzane śledzenia
settings-osc-vmc-mirror_tracking-description = Odbij śledzenie w poziomie.
settings-osc-vmc-mirror_tracking-label = Odbicie lustrzane śledzenia

## Common OSC settings

settings-osc-common-network-ports_match_error = Porty wejściowe i wyjściowe routera OSC nie mogą być takie same!
settings-osc-common-network-port_banned_error = Port { $port } nie może zostać użyty!

## Advanced settings

settings-utils-advanced = Zaawansowany
settings-utils-advanced-reset-gui = Zresetuj ustawienia GUI
settings-utils-advanced-reset-gui-description = Przywróć domyślne ustawienia interfejsu.
settings-utils-advanced-reset-gui-label = Zresetuj GUI
settings-utils-advanced-reset-server = Zresetuj ustawienia śledzenia
settings-utils-advanced-reset-server-description = Przywróć domyślne ustawienia śledzenia.
settings-utils-advanced-reset-server-label = Zresetuj śledzenie
settings-utils-advanced-reset-all = Zresetuj wszystkie ustawienia
settings-utils-advanced-reset-all-description = Przywróć ustawienia domyślne interfejsu i śledzenia.
settings-utils-advanced-reset-all-label = Zresetuj wszystko
settings-utils-advanced-reset_warning =
    { $type ->
        [gui] <b>Ostrzeżenie:</b> Spowoduje to przywrócenie ustawień domyślnych interfejsu GUI.¶
        [server] <b>Ostrzeżenie:</b>Spowoduje to przywrócenie domyślnych ustawień śledzenia.¶
       *[all]
            <b>Ostrzeżenie:</b> Spowoduje to zresetowanie wszystkich ustawień do wartości domyślnych.¶
            Czy na pewno chcesz to zrobić?
    }
settings-utils-advanced-reset_warning-reset = Zresetuj ustawienia
settings-utils-advanced-reset_warning-cancel = Anuluj
settings-utils-advanced-open_data-v1 = Folder konfiguracyjny
settings-utils-advanced-open_data-description-v1 = Otwórz folder konfiguracyjny SlimeVR w eksploratorze plików, zawierający konfigurację
settings-utils-advanced-open_data-label = Otwórz folder
settings-utils-advanced-open_logs = Folder dzienników
settings-utils-advanced-open_logs-description = Otwórz folder dzienników SlimeVR w eksploratorze plików, zawierający dzienniki aplikacji
settings-utils-advanced-open_logs-label = Otwórz folder

## Home Screen

settings-home-list-layout = Układ listy urządzeń
settings-home-list-layout-desc = Wybierz jeden z możliwych układów ekranu głównego
settings-home-list-layout-grid = Siatka
settings-home-list-layout-table = Tabela

## Tracking Checlist

settings-tracking_checklist-active_steps = Aktywne Kroki
settings-tracking_checklist-active_steps-desc = Lista wszystkich kroków kontrolnych. Możesz wyłączyć konkretne punkty.

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
onboarding-wifi_creds-skip = Pomiń ustawienia Wi-Fi
onboarding-wifi_creds-submit = Potwierdź!
onboarding-wifi_creds-ssid =
    .label = SSID
    .placeholder = Enter SSID
onboarding-wifi_creds-ssid-required = Nazwa Wi-Fi jest wymagana
onboarding-wifi_creds-password =
    .label = Password
    .placeholder = Enter password

## Mounting setup

onboarding-reset_tutorial-back = Powrót do Konfiguracji Położenia trackerów
onboarding-reset_tutorial = Zresetuj poradnik
onboarding-reset_tutorial-explanation = Podczas korzystania z trackerów mogą się one rozregulować z powodu dryfu odchylenia IMU lub z powodu fizycznego przeniesienia ich. Możesz to naprawić na kilka sposobów.
onboarding-reset_tutorial-skip = Pomiń krok
# Cares about multiline
onboarding-reset_tutorial-0 =
    Stuknij { $taps } razy podświetlony tracker, aby uruchomić reset odchylenia.
    
    Spowoduje to, że trackery będą skierowane w tym samym kierunku, co Twój HMD.
# Cares about multiline
onboarding-reset_tutorial-1 =
    Stuknij { $taps } razy podświetlony tracker, aby wywołać pełny reset.
    
    Musisz stać, aby to zrobić (i-pozycja). Istnieje 3-sekundowe opóźnienie (konfigurowalne), zanim to nastąpi.
    Spowoduje to całkowite zresetowanie pozycji i rotacji wszystkich trackerów. Powinien rozwiązać większość problemów.
# Cares about multiline
onboarding-reset_tutorial-2 =
    Stuknij { $taps } razy podświetlony tracker, aby uruchomić reset montażu.
    
    Resetowanie montażu pomaga w tym, w jaki sposób trackery są faktycznie zakładane na ciebie, więc jeśli przypadkowo je przeniosłeś i zmieniłeś ich orientację o dużą ilość, to pomoże.
    
    Musisz być w pozie, jakbyś jechał na nartach, tak jak jest to pokazane w kreatorze automatycznego montażu, i masz 3-sekundowe opóźnienie (konfigurowalne), zanim zostanie uruchomione.

## Setup start

onboarding-home = Witamy w SlimeVR
onboarding-home-start = Zaczynajmy!

## Setup done

onboarding-done-title = Wszystko ustawione!
onboarding-done-description = Ciesz się Full-Body
onboarding-done-close = Zamknij Poradnik

## Tracker connection setup

onboarding-connect_tracker-back = Cofnij się do ustawień Wi-Fi
onboarding-connect_tracker-title = Połącz trackery
onboarding-connect_tracker-description-p0-v1 = A teraz czas na najfajniejszą część: łączenie trackerów!
onboarding-connect_tracker-description-p1-v1 = Podłącz każdy tracker pojedynczo przez port USB.
onboarding-connect_tracker-issue-serial = Mam problemy z połączeniem!
onboarding-connect_tracker-usb = USB Tracker
onboarding-connect_tracker-connection_status-none = Szukanie Trackerów
onboarding-connect_tracker-connection_status-serial_init = Łączenie z urządzeniem szeregowym
onboarding-connect_tracker-connection_status-obtaining_mac_address = Uzyskiwanie adresu MAC modułu śledzącego
onboarding-connect_tracker-connection_status-provisioning = Wysyłanie danych Wi-Fi
onboarding-connect_tracker-connection_status-connecting = Wysyłanie danych Wi-Fi
onboarding-connect_tracker-connection_status-looking_for_server = Szukanie serwera
onboarding-connect_tracker-connection_status-connection_error = Nie można połączyć się z Wi-Fi
onboarding-connect_tracker-connection_status-could_not_find_server = Nie można znaleźć serwera
onboarding-connect_tracker-connection_status-done = Połączono z serwerem
onboarding-connect_tracker-connection_status-no_serial_log = Nie można pobrać dzienników z modułu śledzącego
onboarding-connect_tracker-connection_status-no_serial_device_found = Nie można znaleźć lokalizatora z USB
onboarding-connect_serial-error-modal-no_serial_log = Czy tracker jest włączony?
onboarding-connect_serial-error-modal-no_serial_log-desc = Upewnij się, że tracker jest włączony i podłączony do komputera
onboarding-connect_serial-error-modal-no_serial_device_found = Nie wykryto trackerów
onboarding-connect_serial-error-modal-no_serial_device_found-desc =
    Podłącz tracker za pomocą dostarczonego USB do komputera i włącz tracker.
    Jeśli to nie zadziała:
      - Spróbuj użyć innego USB
      - Spróbuj użyć innego portu USB
      - spróbuj ponownie zainstalować serwer SlimeVR i wybierz "Sterowniki USB" w sekcji komponentów
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
onboarding-calibration_tutorial-description-v1 = Po włączeniu trackerów umieść je na chwilę na stabilnej powierzchni, aby umożliwić kalibrację. Kalibrację można przeprowadzić w dowolnym momencie po włączeniu trackerów - ta strona zawiera po prostu samouczek. Aby rozpocząć, kliknij przycisk "{ onboarding-calibration_tutorial-calibrate }", a następnie <b>nie ruszaj swoich trackerów!</b>
onboarding-calibration_tutorial-calibrate = Położyłem trackery na stole
onboarding-calibration_tutorial-status-waiting = Czekam na Ciebie
onboarding-calibration_tutorial-status-calibrating = Kalibracja
onboarding-calibration_tutorial-status-success = Nieźle!
onboarding-calibration_tutorial-status-error = Tracker został przeniesiony
onboarding-calibration_tutorial-skip = Pomiń samouczek

## Tracker assignment tutorial

onboarding-assignment_tutorial = Jak przygotować Slime Tracker przed założeniem
onboarding-assignment_tutorial-first_step = 1. Umieść naklejkę z częścią ciała (jeśli ją posiadasz) na trackerze według własnego uznania
# This text has a character limit of around 11 characters, so please keep it short
onboarding-assignment_tutorial-sticker = Naklejka
onboarding-assignment_tutorial-second_step-v2 = 2. Przymocuj pasek do trackera, tak aby rzep paska był skierowany w tę samą stronę, co slime face część trackera:
onboarding-assignment_tutorial-second_step-continuation-v2 = Strona z rzepem dla przedłużenia powinna być skierowana do góry, jak na poniższym obrazku:
onboarding-assignment_tutorial-done = Umieszczam naklejki i paski!

## Tracker assignment setup

onboarding-assign_trackers-back = Cofnij się do ustawień Wi-Fi
onboarding-assign_trackers-title = Przydziel Trackery
onboarding-assign_trackers-description = Wybierzmy gdzie idzie jaki tracker. Naciśnij gdzie chcesz go przydzielić
onboarding-assign_trackers-unassign_all = Usuń przydzielenie wszystkich urządzeń
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
onboarding-assign_trackers-mirror_view = Widok lustrzany
onboarding-assign_trackers-option-amount =
    { $trackersCount ->
        [one] x{ $trackersCount }
        [few] x{ $trackersCount }
       *[many] x{ $trackersCount }
    }
onboarding-assign_trackers-option-label =
    { $mode ->
        [lower-body] Zestaw na dolną część ciała
        [core] Zestaw podstawowy
        [enhanced-core] Rozszerzony zestaw podstawowy
        [full-body] Pełny zestaw
       *[all] Wszystkie trackery
    }
onboarding-assign_trackers-option-description =
    { $mode ->
        [lower-body] Minimum do śledzenia ciała w rzeczywistości wirtualnej
        [core] + Ulepszone śledzenie kręgosłupa
        [enhanced-core] + Śledzenie stóp
        [full-body] + Śledzenie łokci
       *[all] Wszystkie możliwe przypisania trackerów
    }

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
# Multiline text
onboarding-choose_mounting-description = Orientacja montażu koryguje umieszczenie trackerów na ciele.
onboarding-choose_mounting-auto_mounting = Automatyczne mocowanie
# Italicized text
onboarding-choose_mounting-auto_mounting-label-v2 = Zalecane
onboarding-choose_mounting-auto_mounting-description = To automatycznie wykryje kierunki montażu dla wszystkich twoich trackerów z 2 pozycji
onboarding-choose_mounting-manual_mounting = Montaż ręczny
# Italicized text
onboarding-choose_mounting-manual_mounting-label-v2 = Może nie być wystarczająco dokładne
onboarding-choose_mounting-manual_mounting-description = Umożliwi to ręczne wybranie kierunku montażu dla każdego trackera
# Multiline text
onboarding-choose_mounting-manual_modal-title =
    Czy na pewno chcesz to zrobić
    automatyczna kalibracja montażu?
onboarding-choose_mounting-manual_modal-description = <b> Ręczna kalibracja montażu jest zalecana dla nowych użytkowników </b> , ponieważ prawidłowe ustawienie pozycji automatycznej kalibracji montażu może być trudne i może wymagać pewnej praktyki.
onboarding-choose_mounting-manual_modal-confirm = Jestem pewien tego, co robię
onboarding-choose_mounting-manual_modal-cancel = Anuluj

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
onboarding-automatic_mounting-mounting_reset-step-0 = 1. Zrób pozycje "na Małysza" z wygiętymi nogami, tułowiem pochylonym do przodu z wygiętymi rękami.
onboarding-automatic_mounting-mounting_reset-step-1 = 2. Naciśnij "Zresetuj Położenie" i poczekaj 3 sekundy zanim trackery się zresetują.
onboarding-automatic_mounting-mounting_reset-feet-step-0 = 1. Stań na palcach z obiema stopami skierowanymi do przodu. Alternatywnie możesz to zrobić siedząc na krześle.
onboarding-automatic_mounting-mounting_reset-feet-step-1 = 2. Naciśnij "Kalibracja Stóp" i poczekaj 3 sekundy zanim zresetuje pozycje.
onboarding-automatic_mounting-preparation-title = Przygotowania
onboarding-automatic_mounting-preparation-v2-step-0 = 1. Naciśnij przycisk "Pełny reset".
onboarding-automatic_mounting-preparation-v2-step-1 = 2. Stań prosto z rękami po bokach. Upewnij się, że patrzysz przed siebie.
onboarding-automatic_mounting-preparation-v2-step-2 = 3. Utrzymaj pozycję, aż skończy się 3-sekundowy timer.
onboarding-automatic_mounting-put_trackers_on-title = Załóż trackery
onboarding-automatic_mounting-put_trackers_on-description = Aby skalibrować rotacje, użyjemy trackerów które przypisano przed chwilą. Załóż wszystkie trackery, możesz je odróznić na postaci po prawej.
onboarding-automatic_mounting-put_trackers_on-next = Wszystkie trackery założone
onboarding-automatic_mounting-return-home = Gotowe

## Tracker manual proportions setupa

onboarding-manual_proportions-back-scaled = Wróć do skalowania proporcji
onboarding-manual_proportions-title = Manualne Proporcje Ciała
onboarding-manual_proportions-fine_tuning_button = Automatyczne dostrajanie proporcji
onboarding-manual_proportions-fine_tuning_button-disabled-tooltip = Podłącz gogle VR, aby korzystać z automatycznego dostrajania
onboarding-manual_proportions-export = Eksportuj proporcje
onboarding-manual_proportions-import = Importuj proporcje
onboarding-manual_proportions-file_type = Proporcje ciała
onboarding-manual_proportions-normal_increment = Normalny przyrost
onboarding-manual_proportions-precise_increment = Precyzyjny przyrost
onboarding-manual_proportions-grouped_proportions = Zgrupowane proporcje
onboarding-manual_proportions-all_proportions = Wszystkie proporcje
onboarding-manual_proportions-estimated_height = Szacowany wzrost użytkownika

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
onboarding-automatic_proportions-requirements-descriptionv2 =
    Masz wystarczającą liczbę trackerów, aby śledzić swoje stopy (zwykle 5 trackerów).¶
    Masz włączone i założone trackery oraz headset.¶
    Twoje trackery i heaset są połączone z serwerem SlimeVR i działają poprawnie (np. nie ma przycięć, rozłączeń itp.).¶
    Twój headset raportuje dane o pozycji do serwera SlimeVR (zazwyczaj oznacza to, że SteamVR jest uruchomiony i podłączony do SlimeVR za pomocą sterownika SteamVR SlimeVR).¶
    Twoje śledzenie działa i dokładnie odzwierciedla Twoje ruchy (wykonałeś pełny reset i poruszają się we właściwym kierunku podczas kopania, schylania się, siedzenia itp.).
onboarding-automatic_proportions-requirements-next = Zapoznałem się z wymaganiami
onboarding-automatic_proportions-check_height-title-v3 = Zmierz wysokość headsetu
onboarding-automatic_proportions-check_height-description-v2 = Wysokość headsetu (HMD) powinna być nieco mniejsza niż pełny wzrost, ponieważ headset mierzy wysokość oczu. Pomiar ten zostanie wykorzystany jako punkt odniesienia dla proporcji Twojego ciała.
# All the text is in bold!
onboarding-automatic_proportions-check_height-calculation_warning-v3 = Rozpocznij pomiar, stojąc <u>prosto</u> , aby zmierzyć swój wzrost. Uważaj, aby nie podnosić rąk wyżej niż headset, ponieważ mogą one wpłynąć na pomiar!
onboarding-automatic_proportions-check_height-guardian_tip =
    Jeśli korzystasz z samodzielnego zestawu VR, upewnij się, że twoja granica¶
    bezpieczeństwa (guardian) jest włączona, aby poprawnie rozpoznać Twój wzrost!
# Context is that the height is unknown
onboarding-automatic_proportions-check_height-unknown = Nieznany
# Shows an element below it
onboarding-automatic_proportions-check_height-hmd_height2 = Wysokość headsetu wynosi:
onboarding-automatic_proportions-check_height-measure-start = Rozpocznij pomiar
onboarding-automatic_proportions-check_height-measure-stop = Przestań mierzyć
onboarding-automatic_proportions-check_height-measure-reset = Ponawianie próby pomiaru
onboarding-automatic_proportions-check_height-next_step = Są w porządku
onboarding-automatic_proportions-check_floor_height-title = Zmierz wysokość podłogi (opcjonalnie)
onboarding-automatic_proportions-check_floor_height-description = W niektórych przypadkach wysokość podłogi może nie być prawidłowo ustawiona przez headset, co powoduje, że wysokość headsetu jest mierzona jako wyższa niż powinna. Możesz zmierzyć "wysokość" podłogi, aby skorygować wysokość headsetu.
# All the text is in bold!
onboarding-automatic_proportions-check_floor_height-calculation_warning-v2 = Rozpocznij pomiar i przyłóż kontroler do podłogi, aby zmierzyć jej wysokość. Jeśli masz pewność, że wysokość podłogi jest prawidłowa, możesz pominąć ten krok.
# Shows an element below it
onboarding-automatic_proportions-check_floor_height-floor_height = Wysokość podłogi wynosi:
onboarding-automatic_proportions-check_floor_height-full_height = Twój szacowany pełny wzrost to:
onboarding-automatic_proportions-check_floor_height-measure-start = Rozpocznij pomiar
onboarding-automatic_proportions-check_floor_height-measure-stop = Przestań mierzyć
onboarding-automatic_proportions-check_floor_height-measure-reset = Ponawianie próby pomiaru
onboarding-automatic_proportions-check_floor_height-skip_step = Pomiń krok i zapisz
onboarding-automatic_proportions-check_floor_height-next_step = Wykorzystaj wysokość podłogi i zaoszczędź
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
onboarding-automatic_proportions-verify_results-title = Zweryfikuj wyniki
onboarding-automatic_proportions-verify_results-description = Sprawdź wyniki poniżej, czy są prawidłowe?
onboarding-automatic_proportions-verify_results-results = Wyniki nagrywania
onboarding-automatic_proportions-verify_results-processing = Przetwarzanie wyniku
onboarding-automatic_proportions-verify_results-redo = Powtórz nagranie
onboarding-automatic_proportions-verify_results-confirm = Wyniki prawidłowe
onboarding-automatic_proportions-done-title = Zmierzono oraz zapisano.
onboarding-automatic_proportions-done-description = Twoja kalibracja ciała została zakończona!
onboarding-automatic_proportions-error_modal-v2 =
    <b>Uwaga:</b> Wystąpił błąd podczas szacowania proporcji!¶
    Jest to prawdopodobnie problem z kalibracją montażu. Zanim spróbujesz ponownie, upewnij się, że śledzenie działa prawidłowo.¶
    Proszę, <docs>dokumentacja sprawdź dokumenty</docs> lub dołącz do naszego <discorda>Discord</discord>, aby uzyskać pomoc^_^
onboarding-automatic_proportions-error_modal-confirm = Zrozumiano!
onboarding-automatic_proportions-smol_warning =
    Skonfigurowana wysokość { $height } jest mniejsza niż minimalna akceptowana wysokość { $minHeight }.
    <b>Powtórz pomiary i upewnij się, że są prawidłowe.</b>
onboarding-automatic_proportions-smol_warning-cancel = Przejdź wstecz

## User height calibration

onboarding-user_height-title = Jaki masz wzrost?
onboarding-user_height-description = Potrzebujemy twojego wzrostu, aby obliczyć proporcje ciała i dokładnie oddać twoje ruchy. Możesz pozwolić SlimeVR to obliczyć albo wpisać swój wzrost ręcznie.
onboarding-user_height-need_head_tracker = Do kalibracji wymaganę są gogle vr z kontrolerami.
onboarding-user_height-calculate = Automatycznie oblicz mój wzrost
onboarding-user_height-next_step = Kontynuuj i zapisz
onboarding-user_height-manual-proportions = Manualne Proporcje Ciała
onboarding-user_height-calibration-title = Postęp kalibracji
onboarding-user_height-calibration-RECORDING_FLOOR = Dotknij podłogi górną częścią kontrolera
onboarding-user_height-calibration-WAITING_FOR_RISE = Wstań
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK = Wstań i spójrz przed siebie
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-ok = Upewnij się, że masz głowę poziomo
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-low = Nie patrz w podłogę
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-high = Nie patrz za wysoko
onboarding-user_height-calibration-WAITING_FOR_CONTROLLER_PITCH = Upewnij się, że kontroler jest skierowany w dół
onboarding-user_height-calibration-RECORDING_HEIGHT = Wstań i nie ruszaj się!
onboarding-user_height-calibration-DONE = Sukces!
onboarding-user_height-calibration-ERROR_TIMEOUT = Kalibracja zakończona niepomyślnie, spróbuj ponownie.
onboarding-user_height-calibration-ERROR_TOO_HIGH = Wykryty wzrost użytkownika jest zbyt wysoki, spróbuj ponownie.
onboarding-user_height-calibration-ERROR_TOO_SMALL = Wykryty wzrost użytkownika jest zbyt mały. Upewnij się, że stoisz prosto i patrzysz przed siebie pod koniec kalibracji.
onboarding-user_height-calibration-error = Kalibracja nieudana
onboarding-user_height-manual-tip = Podczas regulacji wzrostu wypróbuj różne pozy i zobacz, czy szkielet odzwierciedla twoje ruchy.
onboarding-user_height-reset-warning =
    <b>Ostrzeżenie:</b> Spowoduje to zresetowanie wszystkich ustawień proporcji do wartości domyślnych.
    Czy na pewno chcesz to zrobić?

## Stay Aligned setup

onboarding-stay_aligned-title = Wyrównywanie
onboarding-stay_aligned-description = Skonfiguruj opcję Wyrównywania, aby Twoje trackery były wyrównane.
onboarding-stay_aligned-put_trackers_on-title = Załóż trackery
onboarding-stay_aligned-put_trackers_on-description = Aby skalibrować proporcje, użyjemy trackerów które przed chwilą przypisałeś. Załóż wszystkie trackery, będziesz widział który to który na postaci po prawej.
onboarding-stay_aligned-put_trackers_on-trackers_warning = Masz mniej niż 5 trackerów aktualnie podłączonych i przypisanych! Jest to minimalna liczba elementów śledzących wymaganych do prawidłowego działania opcji Wyrównywania.
onboarding-stay_aligned-put_trackers_on-next = Mam wszystkie trackery założone
onboarding-stay_aligned-verify_mounting-title = Sprawdź swój montaż
onboarding-stay_aligned-verify_mounting-step-0 = Opcja Wyrównywania wymaga stabilnego mocowania trackera. W innym przypadku będziesz miał złe rezultaty.
onboarding-stay_aligned-verify_mounting-step-1 = 1. Poruszaj się podczas stania.
onboarding-stay_aligned-verify_mounting-step-2 = 2. Usiądź i poruszaj nogami i stopami.
onboarding-stay_aligned-verify_mounting-step-3 = 3. Jeśli Twoje trackery nie znajdują się we właściwym miejscu, naciśnij "Ponów kalibrację montażu".
onboarding-stay_aligned-verify_mounting-redo_mounting = Ponów kalibrację montażu
onboarding-stay_aligned-preparation-title = Przygotowania
onboarding-stay_aligned-preparation-tip = Upewnij się, że stoisz prosto. Patrz przed siebie z rękami opuszczonymi po bokach.
onboarding-stay_aligned-relaxed_poses-standing-title = Zrelaksowana pozycja stojąca
onboarding-stay_aligned-relaxed_poses-standing-step-0 = 1. Stań w wygodnej pozycji. Zrelaksuj się!
onboarding-stay_aligned-relaxed_poses-standing-step-1-v2 = 2. Naciśnij przycisk "Zapisz pozę".
onboarding-stay_aligned-relaxed_poses-sitting-title = Zrelaksowana pozycja siedząca na krześle
onboarding-stay_aligned-relaxed_poses-sitting-step-0 = 1. Usiądź w wygodnej pozycji. Zrelaksuj się!
onboarding-stay_aligned-relaxed_poses-sitting-step-1-v2 = 2. Naciśnij przycisk "Zapisz pozę".
onboarding-stay_aligned-relaxed_poses-flat-title = Zrelaksowana pozycja siedząca na podłodze
onboarding-stay_aligned-relaxed_poses-flat-step-0 = 1. Usiądź na podłodze z nogami wysuniętymi do przodu. Zrelaksuj się!
onboarding-stay_aligned-relaxed_poses-flat-step-1-v2 = 2. Naciśnij przycisk "Zapisz pozę".
onboarding-stay_aligned-relaxed_poses-skip_step = Pomiń
onboarding-stay_aligned-done-title = Wyrównywanie Włączone!
onboarding-stay_aligned-done-description = Konfiguracja Wyrównywania jest zakończona!
onboarding-stay_aligned-done-description-2 = Konfiguracja została zakończona! Możesz ponownie uruchomić proces, jeśli chcesz ponownie skalibrować pozy.
onboarding-stay_aligned-previous_step = Poprzednie
onboarding-stay_aligned-next_step = Następne
onboarding-stay_aligned-restart = Restart
onboarding-stay_aligned-done = Gotowy

## Home

home-no_trackers = Nie wykryto ani nie przypisano żadnych trackerów
home-settings = Ustawienia strony głównej
home-settings-close = Zamknij

## Trackers Still On notification

trackers_still_on-modal-title = Trackery nadal włączone
trackers_still_on-modal-description =
    Jeden lub więcej modułów śledzących jest nadal włączonych.
    Czy nadal chcesz wyjść ze SlimeVR?
trackers_still_on-modal-confirm = Wyjdź ze SlimeVR
trackers_still_on-modal-cancel = Poczekaj...

## Status system

status_system-StatusTrackerReset = Zaleca się wykonanie pełnego resetu, ponieważ jeden lub więcej trackerów nie jest wyregulowanych.
status_system-StatusSteamVRDisconnected =
    { $type ->
        [steamvr_feeder] Nie można połączyć się z aplikacją SlimeVR Feeder.
       *[other] Nie można połączyć się ze SteamVR przez sterownik SlimeVR.
    }
status_system-StatusTrackerError = Tracker { $trackerName } ma błąd.
status_system-StatusUnassignedHMD = Headset powinien być przypisany do śledzenia głowy.
status_system-StatusPublicNetwork =
    { $count ->
        [one] Twoja karta sieciowa jest ustawiona jako publiczna: { $adapters }. Nie jest to zalecane, aby SlimeVR działał poprawnie. <PublicFixLink>Zobacz, jak to naprawić tutaj.</PublicFixLink>
        [few] Niektóre karty sieciowe są ustawione jako publiczne: { $adapters }. Nie jest to zalecane, aby SlimeVR działał poprawnie. <PublicFixLink>Zobacz, jak to naprawić tutaj.</PublicFixLink>
       *[many] Dużo twoich karty sieciowych jest ustawionych jako publiczne: { $adapters }. Nie jest to zalecane, aby SlimeVR działał poprawnie. <PublicFixLink>Zobacz, jak to naprawić tutaj.</PublicFixLink>
    }

## Firmware tool globals

firmware_tool-next_step = Następny krok
firmware_tool-previous_step = Poprzedni krok
firmware_tool-ok = Wygląda dobrze
firmware_tool-retry = Powtórz
firmware_tool-loading = Ładowanie...

## Firmware tool Steps

firmware_tool = Narzędzie do oprogramowania sprzętowego DIY
firmware_tool-description = Umożliwia konfigurowanie i flashowanie trackerów DIY
firmware_tool-not_available = Ups, narzędzie do oprogramowania sprzętowego nie jest obecnie dostępne. Wróć później!
firmware_tool-not_compatible = Narzędzie oprogramowania układowego nie jest kompatybilne z tą wersją serwera. Proszę zaktualizować swój serwer!
firmware_tool-select_source = Wybierz oprogramowanie do wgrania
firmware_tool-select_source-description = Wybierz oprogramowanie, które chcesz wgrać na urządzenie
firmware_tool-select_source-error = Nie można załadować oprogramowania
firmware_tool-select_source-board_type = Typ urządzenia
firmware_tool-select_source-firmware = Źródło oprogramowania
firmware_tool-select_source-version = Wersja oprogramowania
firmware_tool-select_source-official = Oficjalny
firmware_tool-select_source-dev = Deweloperski
firmware_tool-board_defaults = Skonfiguruj swoje urządzenie
firmware_tool-board_defaults-description = Ustaw piny lub ustawienia do twojego urządzenia
firmware_tool-board_defaults-add = Dodaj
firmware_tool-board_defaults-reset = Zresetuj do domyślnych ustawień
firmware_tool-board_defaults-error-required = Wymagane pole
firmware_tool-board_defaults-error-format = Nieprawidłowy format
firmware_tool-board_defaults-error-format-number = To nie liczba
firmware_tool-flash_method_step = Metoda flashowania
firmware_tool-flash_method_step-description = Wybierz metodę flashowania, której chcesz użyć
firmware_tool-flash_method_step-ota-v2 =
    .label = Wi-Fi
    .description = Użyj metody bezprzewodowej. Twoje urządzenie będzie aktualizować się przez Wi-Fi. Działa tylko z skonfigurowanymi urządzeniami.
firmware_tool-flash_method_step-ota-info =
    Używamy Twoich danych wi-fi, aby wgrać tracker i potwierdzić, że wszystko działa poprawnie.
    <b>Nie przechowujemy Twoich danych wifi!</b>
firmware_tool-flash_method_step-serial-v2 =
    .label = USB
    .description = Użyj kabla usb, aby aktualizować urządzenie.
firmware_tool-flashbtn_step = Naciśnij przycisk zasilania
firmware_tool-flashbtn_step-description = Zanim przejdziesz do następnego kroku, musisz zrobić kilka rzeczy
firmware_tool-flashbtn_step-board_SLIMEVR =
    Naciśnij przycisk flash na płytce drukowanej przed włożeniem, aby włączyć tracker.¶
    Jeśli tracker był już włączony, po prostu go wyłącz i włącz ponownie, naciskając przycisk lub zwierając podkładki flash.¶
    Oto kilka zdjęć, jak to zrobić, zgodnie z różnymi wersjami trackera SlimeVR
firmware_tool-flashbtn_step-board_SLIMEVR-r11-v2 = Włącz tracker zwierając drugi prostokątny pad FLASH od krawędzi na górnej stronie płytki, a metalową osłonę mikrokontrolera
firmware_tool-flashbtn_step-board_SLIMEVR-r12-v2 = Włącz tracker zwierając drugi prostokątny pad FLASH od krawędzi na górnej stronie płytki, a metalową osłonę mikrokontrolera
firmware_tool-flashbtn_step-board_SLIMEVR-r14-v2 = Włącz tracker, naciskając przycisk FLASH na górnej stronie płytki. Dioda LED powinna krótko mrógnąć.
firmware_tool-flashbtn_step-board_OTHER =
    Przed flashowaniem prawdopodobnie będziesz musiał przełączyć moduł śledzący w tryb bootloadera.¶
    W większości przypadków oznacza to naciśnięcie przycisku rozruchu na płycie przed rozpoczęciem procesu flashowania.¶
    Jeśli na początku flashowania upłynie limit czasu procesu flashowania, prawdopodobnie oznacza to, że moduł śledzący nie był w trybie bootloadera¶
    Aby dowiedzieć się, jak włączyć tryb ładowarki łodzi, zapoznaj się z instrukcjami flashowania swojej tablicy
firmware_tool-flash_method_ota-title = Wgrywanie przez Wi-Fi
firmware_tool-flash_method_ota-devices = Wykryte urządzenia OTA:
firmware_tool-flash_method_ota-no_devices = Nie ma tablic, które można zaktualizować za pomocą OTA, upewnij się, że wybrałeś właściwy typ płyty
firmware_tool-flash_method_serial-title = Wgrywanie przez USB
firmware_tool-flash_method_serial-wifi = Dane uwierzytelniające Wi-Fi:
firmware_tool-flash_method_serial-devices-label = Wykryte urządzenia szeregowe:
firmware_tool-flash_method_serial-devices-placeholder = Wybierz urządzenie szeregowe
firmware_tool-flash_method_serial-no_devices = Nie wykryto kompatybilnych urządzeń szeregowych. Upewnij się, że tracker jest podłączony
firmware_tool-build_step = Building
firmware_tool-build_step-description = Trwa tworzenie oprogramowania sprzętowego. Proszę czekać
firmware_tool-flashing_step = Flashing
firmware_tool-flashing_step-description = Twoje trackery migają. Postępuj zgodnie z instrukcjami wyświetlanymi na ekranie
firmware_tool-flashing_step-warning-v2 = Nie odłączaj ani nie wyłączaj trackera podczas procesu przesyłania, chyba że zostaniesz o to poproszony, może to spowodować, że twoje urządzenie stanie się bezużyteczne.
firmware_tool-flashing_step-flash_more = Flashuj więcej trackerów
firmware_tool-flashing_step-exit = Wyjście

## firmware tool build status

firmware_tool-build-QUEUED = Budowanie....
firmware_tool-build-CREATING_BUILD_FOLDER = Tworzenie folderu kompilacji
firmware_tool-build-DOWNLOADING_SOURCE = Pobieranie kodu źródłowego
firmware_tool-build-EXTRACTING_SOURCE = Ekstrakcja kodu źródłowego
firmware_tool-build-BUILDING = Budowa oprogramowania sprzętowego
firmware_tool-build-SAVING = Zapisywanie kompilacji
firmware_tool-build-DONE = Budowa ukończona
firmware_tool-build-ERROR = Nie można zbudować oprogramowania sprzętowego

## Firmware update status

firmware_update-status-DOWNLOADING = Pobieranie oprogramowania sprzętowego
firmware_update-status-NEED_MANUAL_REBOOT-v2 = Wyłącz i ponownie włącz swój tracker
firmware_update-status-AUTHENTICATING = Uwierzytelnianie za pomocą MCU
firmware_update-status-UPLOADING = Przesyłanie oprogramowania sprzętowego
firmware_update-status-SYNCING_WITH_MCU = Synchronizacja z MCU
firmware_update-status-REBOOTING = Ponowne uruchomienie trackera
firmware_update-status-PROVISIONING = Ustawianie danych uwierzytelniających Wi-Fi
firmware_update-status-DONE = Aktualizacja zakończona!
firmware_update-status-ERROR_DEVICE_NOT_FOUND = Nie udało się znaleźć urządzenia
firmware_update-status-ERROR_TIMEOUT = Upłynął limit czasu procesu aktualizacji
firmware_update-status-ERROR_DOWNLOAD_FAILED = Nie można pobrać oprogramowania sprzętowego
firmware_update-status-ERROR_AUTHENTICATION_FAILED = Nie można uwierzytelnić za pomocą MCU
firmware_update-status-ERROR_UPLOAD_FAILED = Nie można załadować oprogramowania sprzętowego
firmware_update-status-ERROR_PROVISIONING_FAILED = Nie można ustawić danych uwierzytelniających Wi-Fi
firmware_update-status-ERROR_UNSUPPORTED_METHOD = Metoda aktualizacji nie jest obsługiwana
firmware_update-status-ERROR_UNKNOWN = Nieznany błąd

## Dedicated Firmware Update Page

firmware_update-title = Aktualizacja oprogramowania
firmware_update-devices = Dostępne urządzenia
firmware_update-devices-description = Wybierz trackery, które chcesz zaktualizować do najnowszej wersji oprogramowania SlimeVR
firmware_update-no_devices = Upewnij się, że trackery, które chcesz zaktualizować, są włączone i połączone z Wi-Fi!
firmware_update-changelog-title = Aktualizuję do wersji { $version }
firmware_update-looking_for_devices = Szukasz urządzeń do aktualizacji...
firmware_update-retry = Spróbować ponownie
firmware_update-update = Zaktualizuj wybrane moduły śledzące
firmware_update-exit = Wyjście

## Tray Menu

tray_menu-show = Pokaż
tray_menu-hide = Ukryj
tray_menu-quit = Zakończ

## First exit modal

tray_or_exit_modal-title = Co powinien robić przycisk zamykania?
# Multiline text
tray_or_exit_modal-description =
    Dzięki temu możesz wybrać, czy chcesz opuścić serwer, czy zminimalizować go do zasobnika po naciśnięciu przycisku zamykania.¶
    ¶
    Możesz to później zmienić w ustawieniach interfejsu!
tray_or_exit_modal-radio-exit = Wyjdź po zamknięciu
tray_or_exit_modal-radio-tray = Minimalizuj do zasobnika systemowego
tray_or_exit_modal-submit = Zachowaj
tray_or_exit_modal-cancel = Anuluj

## Unknown device modal

unknown_device-modal-title = Znaleziono nowy tracker!
unknown_device-modal-description =
    Dostępny jest nowy tracker z adresem MAC <b> { $deviceId } </b> .¶
    Czy chcesz podłączyć go do SlimeVR?
unknown_device-modal-confirm = Jasne!
unknown_device-modal-forget = Ignoruj
# VRChat config warnings
vrc_config-page-title = Ostrzeżenia dotyczące konfiguracji VRChat
vrc_config-page-desc = Ta strona pokazuje stan ustawień VRChat i pokazuje, jakie ustawienia są niekompatybilne ze SlimeVR. Zdecydowanie zaleca się naprawienie wszelkich pojawiających się tutaj ostrzeżeń, aby zapewnić najlepsze wrażenia użytkownika z SlimeVR.
vrc_config-page-help = Nie możesz znaleźć ustawień?
vrc_config-page-help-desc = Zapoznaj się <a>z naszą dokumentacją na ten temat!</a>
vrc_config-page-big_menu = Śledzenie i IK (duże menu)
vrc_config-page-big_menu-desc = Ustawienia związane z kinematyką odwrotną w dużym menu ustawień
vrc_config-page-wrist_menu = Śledzenie i IK (menu na nadgarstku)
vrc_config-page-wrist_menu-desc = Ustawienia związane z kinematyką odwrotną w małym menu ustawień (menu na nadgarstku)
vrc_config-on = Na
vrc_config-off = Od
vrc_config-invalid = Źle skonfigurowałeś ustawienia VRChat!
vrc_config-show_more = Pokaż więcej
vrc_config-setting_name = Nazwa ustawienia VRChat
vrc_config-recommended_value = Zalecana wartość
vrc_config-current_value = Bieżąca wartość
vrc_config-mute = Wycisz Ostrzeżenie
vrc_config-mute-btn = Wycisz
vrc_config-unmute-btn = Odcisz
vrc_config-legacy_mode = Korzystanie ze starszego rozwiązywania kinematyki odwrotnej
vrc_config-disable_shoulder_tracking = Wyłącz śledzenie ramienia
vrc_config-shoulder_width_compensation = Kompensacja szerokości barku
vrc_config-spine_mode = Tryb FBT Spine
vrc_config-tracker_model = Model śledzenia FBT
vrc_config-avatar_measurement_type = Pomiar awatara
vrc_config-calibration_range = Zakres kalibracji
vrc_config-calibration_visuals = Wyświetlanie wizualizacji kalibracji
vrc_config-user_height = Rzeczywista wysokość użytkownika
vrc_config-spine_mode-UNKNOWN = Nieznany
vrc_config-spine_mode-LOCK_BOTH = Zablokuj oba
vrc_config-spine_mode-LOCK_HEAD = Zablokuj głowicę
vrc_config-spine_mode-LOCK_HIP = Zablokuj biodro
vrc_config-tracker_model-UNKNOWN = Nieznany
vrc_config-tracker_model-AXIS = Oś
vrc_config-tracker_model-BOX = Pudełko
vrc_config-tracker_model-SPHERE = Sfera
vrc_config-tracker_model-SYSTEM = System
vrc_config-avatar_measurement_type-UNKNOWN = Nieznany
vrc_config-avatar_measurement_type-HEIGHT = Wysokość
vrc_config-avatar_measurement_type-ARM_SPAN = Rozpiętość ramion

## Error collection consent modal

error_collection_modal-title = Czy możemy zbierać błędy?
error_collection_modal-description_v2 =
    { settings-interface-behavior-error_tracking-description_v2 }
    
    To ustawienie można zmienić później w sekcji Zachowanie na stronie ustawień.
error_collection_modal-confirm = Zgadzam się
error_collection_modal-cancel = Nie chcę

## Tracking checklist section

tracking_checklist = Lista Kontrolna
tracking_checklist-settings = Ustawienia Listy Kontrolnej
tracking_checklist-settings-close = Zamknij
tracking_checklist-status-incomplete = Nie jesteś przygotowany aby korzystać ze SlimeVR!
tracking_checklist-status-partial =
    { $count ->
        [one] Masz { $count } ostrzeżenie!
        [few] Masz { $count } ostrzeżeń!
       *[many] Masz { $count } ostrzeżeń!
    }
tracking_checklist-status-complete = Jesteś gotowy korzystać ze SlimeVR!
tracking_checklist-MOUNTING_CALIBRATION = Wykonaj kalibrację montażu
tracking_checklist-FEET_MOUNTING_CALIBRATION = Wykonaj kalibrację montażu stóp
tracking_checklist-FULL_RESET = Wykonaj pełny reset
tracking_checklist-FULL_RESET-desc = Niektóre urządzenia wymagają resetu.
tracking_checklist-STEAMVR_DISCONNECTED = SteamVR nie jest uruchomiony
tracking_checklist-STEAMVR_DISCONNECTED-desc = SteamVR nie jest uruchomiony. Czy twoje gogle są podłączone?
tracking_checklist-STEAMVR_DISCONNECTED-open = Uruchom SteamVR
tracking_checklist-TRACKERS_REST_CALIBRATION = Skalibruj swoje urządzenia
tracking_checklist-TRACKERS_REST_CALIBRATION-desc = Nie wykonałeś kalibracji urządzenia. Proszę, pozwól swoim urządzeniom (podświetlonym na żółto) odpocząć na stabilnej powierzchni przez kilka sekund.
tracking_checklist-TRACKER_ERROR = Urządzenia z błędami
tracking_checklist-TRACKER_ERROR-desc = Niektóre z Twoich urządzeń mają błędy. Proszę ponownie uruchomić urządzenia podświetlone na żółto.
tracking_checklist-VRCHAT_SETTINGS = Konfiguruj ustawienia do VRChat'a
tracking_checklist-VRCHAT_SETTINGS-desc = Źle ustawiłeś ustawienia VRChat'a! Może to negatywnie wpłynąć na twoje śledzenie.
tracking_checklist-VRCHAT_SETTINGS-open = Przejdź do ostrzeżeń VRChat
tracking_checklist-UNASSIGNED_HMD = Zestaw VR nieprzypisany do Głowy
tracking_checklist-UNASSIGNED_HMD-desc = Zestaw VR powinien być przypisany jako śledzenie głowy.
tracking_checklist-NETWORK_PROFILE_PUBLIC = Zmień profil sieciowy
tracking_checklist-NETWORK_PROFILE_PUBLIC-desc =
    { $count ->
        [one]
            Jeden z Twoich adapterów sieciowych jest ustawiony na publiczny:
            { $adapters }
            Nie zaleca się tego, aby SlimeVR działał poprawnie.
            <PublicFixLink>Zobacz, jak to naprawić tutaj.</PublicFixLink>
        [few]
            Niektóre z Twoich adapterów sieciowych są ustawione na publiczne:
            { $adapters }
            Nie zaleca się tego, aby SlimeVR działał poprawnie.
            <PublicFixLink>Zobacz, jak to naprawić tutaj.</PublicFixLink>
       *[many]
            Niektóre z Twoich adapterów sieciowych są ustawione na publiczne:
            { $adapters }
            Nie zaleca się tego, aby SlimeVR działał poprawnie.
            <PublicFixLink>Zobacz, jak to naprawić tutaj.</PublicFixLink>
    }
tracking_checklist-NETWORK_PROFILE_PUBLIC-open = Otwórz panel sterowania
tracking_checklist-STAY_ALIGNED_CONFIGURED = Konfiguruj Opcje Wyrównywania
tracking_checklist-STAY_ALIGNED_CONFIGURED-desc = Zapisz pozycje wyrównywania, aby zmniejszyć poślizg
tracking_checklist-STAY_ALIGNED_CONFIGURED-open = Otwórz Konfiguracje Wyrównywania
tracking_checklist-ignore = Ignoruj
preview-mocap_mode_soon = Tryb mocap (wkrótce™)
preview-disable_render = Wyłącz renderowanie
preview-disabled_render = Renderowanie wyłączone
toolbar-mounting_calibration = Kalibracja Pozycji
toolbar-mounting_calibration-default = Ciało
toolbar-mounting_calibration-feet = Stopy
toolbar-mounting_calibration-fingers = Palce
toolbar-drift_reset = Reset Poślizgu
toolbar-assigned_trackers = { $count } Przydzielonych urządzeń
toolbar-unassigned_trackers = { $count } Nieprzydzielonych urządzeń
