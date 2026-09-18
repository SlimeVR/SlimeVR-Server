# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Ładowanie...
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
tips-failed_webgl = Nie udało się zainicjalizować WebGL.

## Units

unit-meter = Metr
unit-foot = Stopa
unit-inch = Cal

## Dropdown


## Text input


## File input


## Window controls

titlebar-close = Zamknij

## Body parts

body_part-NONE = Nieprzydzielony
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
body_part-HIP = Biodro
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
board_type-TTGO_TBASE = Podstawa T TTGO
board_type-ESP01 = Zobacz materiał ESP-01
board_type-SLIMEVR_DEV = SlimeVR Płytka Deweloperska
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
skeleton_bone-LOWER_CHEST-desc =
    To jest odległość od środka klatki piersiowej do środka kręgosłupa.
    Aby go dostosować, odpowiednio dostosuj długość tułowia i zmodyfikuj ją w różnych pozycjach
    (Siedząc, pochylając się, leżąc itp.) Dopóki wirtualny kręgosłup nie pasuje do twojego prawdziwego.
skeleton_bone-HIP = Długość bioder
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

## Tracker reset buttons

reset-reset_all = Zresetuj wszystkie wymiary
reset-reset_all_warning-reset = Zresetuj proporcje
reset-reset_all_warning-cancel = Anuluj
reset-full = Pełny Reset
reset-mounting = Sprawdź swój montaż
reset-mounting-feet = Zresetuj mocowanie stóp
reset-mounting-fingers = Zresetuj mocowanie palców
reset-yaw = Reset odchylenia
reset-error-mounting-need_full_reset = Potrzebny jest pełny reset przed montażem
reset-error-yaw-need_full_reset = Potrzebny jest pełny reset przed resetem obrotu

## Navigation bar

navbar-home = Strona Główna
navbar-body_proportions = Proporcje Ciała
navbar-trackers_assign = Przydzielenie Trackerów
navbar-mounting = Sprawdź swój montaż
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

## Widget: Developer settings

widget-developer_mode = Tryb Dewelopera
widget-developer_mode-high_contrast = Wysoki kontrast
widget-developer_mode-precise_rotation = Wyświetlanie dokładniejszej rotacji
widget-developer_mode-fast_data_feed = Szybkie przesyłanie danych
widget-developer_mode-raw_slime_rotation = Raw

## Widget: IMU Visualizer

widget-imu_visualizer = Rotacja
widget-imu_visualizer-preview = Podgląd
widget-imu_visualizer-hide = Ukryj
widget-imu_visualizer-rotation_raw = Raw
widget-imu_visualizer-rotation_preview = Podgląd
widget-imu_visualizer-acceleration = Akceleracja
widget-imu_visualizer-position = Pozycja
widget-imu_visualizer-stay_aligned = Wyrównywanie

## Tracker status

tracker-status-none = Brak Statusu
tracker-status-busy = Zajęty
tracker-status-error = Błąd
tracker-status-disconnected = Rozłączono
tracker-status-occluded = Zasłonięty
tracker-status-timed_out = Wygasły

## Tracker status columns

tracker-table-column-name = Nazwa
tracker-table-column-type = Typ
tracker-table-column-battery = Bateria
tracker-table-column-linear-acceleration = Akceleracja X/Y/Z
tracker-table-column-rotation = Rotacja X/Y/Z
tracker-table-column-position = Pozycja X/Y/Z
tracker-table-column-stay_aligned = Wyrównywanie

## Tracker rotation

tracker-rotation-front = Przód
tracker-rotation-front_left = Przedni lewy
tracker-rotation-front_right = Przedni prawy
tracker-rotation-left = Lewy
tracker-rotation-right = Prawy
tracker-rotation-back = Wstecz
tracker-rotation-back_left = Lewy tył
tracker-rotation-back_right = Prawy tył
tracker-rotation-custom = Własne

## Tracker information

tracker-infos-manufacturer = Producent
tracker-infos-display_name = Wyświetlana Nazwa
tracker-infos-custom_name = Niestandardowa Nazwa
tracker-infos-hardware_identifier = Identyfikator sprzętu
tracker-infos-imu = Czujnik IMU
tracker-infos-board_type = Płyta główna
tracker-infos-network_version = Wersja protokołu
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

## Dongle settings

dongle-infos-hardware_revision = Rewizja sprzętu
dongle-status-disconnected = Rozłączono
dongle-settings-back = Wróć do listy trackerów
dongle-settings-update = Zaktualizuj teraz
dongle-settings-update-title = Wersja oprogramowania

## Tracker part card info

tracker-part_card-unassigned = Nieprzydzielony

## Body assignment menu

body_assignment_menu = Gdzie chciałbyś ten tracker?
body_assignment_menu-description = Wybierz miejsce gdzie tracker będzie przydzielony. Alternatywnie możesz ustawić wszystkie na raz.
body_assignment_menu-manage_trackers = Zarządzaj wszystkimi trackerami
body_assignment_menu-unassign_tracker = Usuń przydzielenie

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Ostrzeżenie:</b> Opaska na szyję może być śmiertelna, jeśli zostanie dopasowana zbyt ciasno,
    pasek może odciąć krążenie do głowy!
tracker_selection_menu-neck_warning-done = Rozumiem ryzyko
tracker_selection_menu-neck_warning-cancel = Anuluj

## Mounting menu

mounting_selection_menu-close = Zamknij

## Sidebar settings

settings-sidebar-title = Ustawienia
settings-sidebar-general = Ogólne
settings-sidebar-stay_aligned = Wyrównywanie
settings-sidebar-interface = Interfejs
settings-sidebar-utils = Narzędzia
settings-sidebar-appearance = Wygląd
settings-sidebar-home = Strona Główna
settings-sidebar-checklist = Lista kontrolna
settings-sidebar-notifications = Powiadomienia
settings-sidebar-firmware-tool = Narzędzie do oprogramowania sprzętowego DIY
settings-sidebar-vrc_warnings = Ostrzeżenia dotyczące konfiguracji VRChat
settings-sidebar-advanced = Zaawansowany

## Bone routing settings

settings-routing-output-badge-off = Od
settings-routing-group-fingers = Palce
settings-routing-hands-warning-cancel = Anuluj

## SteamVR / Monado output settings

settings-driver-enable = Umożliwiać
settings-driver-status-badge-disabled = Od

## Tracker mechanics

settings-general-tracker_mechanics-filtering = Filtrowanie
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Wybierz Filtry dla twoich trackerów.
    Przewidywanie przewiduje ruchy a Wygładzanie stara się wygładzić ruchy.
settings-general-tracker_mechanics-filtering-type-none = Brak Filtrów
settings-general-tracker_mechanics-filtering-type-none-description = Używa rotacji w niezmienionej formie. Wszystkie filtry są wyłączone.
settings-general-tracker_mechanics-filtering-type-smoothing = Wygładzanie
settings-general-tracker_mechanics-filtering-type-smoothing-description = Wygładza ruchy lecz dodaje trochę opóźnienia.
settings-general-tracker_mechanics-filtering-type-prediction = Przewidywanie
settings-general-tracker_mechanics-filtering-type-prediction-description = Zmniejsza opóźnienie i zwiększa dynamikę ruchów, ale może dodać trochę drgań.
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
settings-stay_aligned-description = Wyrównywanie zmniejsza efekt driftu, stopniowo dostosowując trackery do twoich zrelaksowanych póz.
settings-stay_aligned-setup-label = Konfiguracja Opcji Wyrównywania
settings-stay_aligned-setup-description = Musisz ukończyć konfigurację, aby włączyć opcję Wyrównywania.
settings-stay_aligned-enabled-label = Dostosuj trackery
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

## Keybinds Page

settings-keybinds_full-reset = Pełny Reset
settings-keybinds_yaw-reset = Reset odchylenia
settings-keybinds_reset-all-button = Zresetuj wszystko
settings-keybinds-recorder-modal-done-button = Gotowy
settings-keybinds-recorder-modal-cancel-button = Anuluj

## FK/Tracking settings

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
settings-general-fk_settings-leg_fk-reset_mounting_feet-v1 = Wymuś kalibracje mocowania stóp
settings-general-fk_settings-leg_fk-reset_mounting_feet-description-v1 = Wymuś kalibracje montażu stóp podczas kalibracji pozycji.
settings-general-fk_settings-enforce_joint_constraints = Limity szkieletowe
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints = Wymuszanie ograniczeń
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = Zapobiega obracaniu się stawów poza ich limit
settings-general-fk_settings-ik = Dane pozycji
settings-general-fk_settings-ik-use_position = Użyj danych o pozycji
settings-general-fk_settings-ik-use_position-description = Umożliwia wykorzystanie danych o pozycji z urządzeń, które je wspierają. Włączając to, upewnij się, że dokonałeś reset w aplikacji i skalibrowałeś położenie w grze.
settings-general-fk_settings-arm_fk-back = Wstecz
settings-general-fk_settings-arm_fk-back-description = Tryb domyślny, w którym górne ramiona cofają się, a dolne ramiona przesuwają się do przodu.
settings-general-fk_settings-arm_fk-tpose_up = T-pose (w górę)
settings-general-fk_settings-arm_fk-tpose_up-description = Oczekuje, że twoje ręcę będą opuszczone podczas pelnego resetu i wystawione pod kątem 90 stopni na boki podczas resetu montażu.
settings-general-fk_settings-arm_fk-tpose_down = T-pose (w dół)
settings-general-fk_settings-arm_fk-tpose_down-description = Oczekuje, że Twoje ramiona będą ustawione pod kątem w 90 stopni na boki podczas Pełnego Resetu i w dół po bokach podczas Resetu Montażowego.
settings-general-fk_settings-arm_fk-forward = Do przodu
settings-general-fk_settings-arm_fk-forward-description = Oczekuje, że Twoje ramiona będą uniesione pod kątem 90 stopni do przodu. Przydatne w VTubingu.
settings-general-fk_settings-skeleton_settings-ratios = Proporcje szkieletu
settings-general-fk_settings-skeleton_settings-ratios-description = Zmień wartości ustawień szkieletu. Po zmianie może być konieczne dostosowanie proporcji.
settings-general-fk_settings-self_localization-title = Tryb Mocap

## Gesture control settings (tracker tapping)

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

## Notification settings

settings-interface-notifications = Powiadomienia
settings-general-interface-feedback_sound = Dźwięk Informacji
settings-general-interface-feedback_sound-description = Ta opcja odtworzy dźwięk, gdy reset zostanie uruchomiony
settings-general-interface-feedback_sound-label = Dźwięk Informacji
settings-general-interface-feedback_sound-volume = Poziom głośności dzwięku zwrotnego
settings-general-interface-connected_trackers_warning = Ostrzeżenie o podłączonych trackerach
settings-general-interface-connected_trackers_warning-description = Ta opcja wyświetli wyskakujące okienko za każdym razem, gdy spróbujesz wyjść ze SlimeVR, mając jeden lub więcej podłączonych trackerów. Przypomina o wyłączeniu trackerów, gdy skończysz, aby wydłużyć żywotność baterii.
settings-general-interface-connected_trackers_warning-label = Ostrzeżenie o podłączonych trackerach przy wyjściu

## Behavior settings

settings-general-interface-dev_mode = Tryb Dewelopera
settings-general-interface-dev_mode-label = Tryb Dewelopera
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
settings-serial-save_logs = Zapisz do pliku
settings-serial-send_command = Wyślij
settings-serial-send_command-placeholder = Polecenie...
settings-serial-send_command-warning = <b>Ostrzeżenie:</b> Wysyłanie poleceń szeregowych może prowadzić do utraty danych lub zablokowania urządzenia.
settings-serial-send_command-warning-ok = Wiem co robię
settings-serial-send_command-warning-cancel = Anuluj

## OSC VRChat settings

settings-osc-vrchat-enable = Umożliwiać
settings-osc-vrchat-enable-description = Przełącz wysyłanie i odbieranie danych.
settings-osc-vrchat-enable-label = Umożliwiać
settings-osc-vrchat-network = Porty sieciowe
settings-osc-vrchat-network-port_in =
    .label = Port Wejścia
    .placeholder = Port in (default: 9001)
settings-osc-vrchat-network-port_out =
    .label = Port Wyjścia
    .placeholder = Port wyjściowy (domyślnie: 9000)
settings-osc-vrchat-network-address = Adres sieciowy
settings-osc-vrchat-network-address-description-v1 = Wybierz adres, na który mają być wysyłane dane. Można pozostawić domyślnie dla VRChata.
settings-osc-vrchat-network-address-placeholder = Adres IP VRChata

## VRChat OSC status

settings-osc-vrchat-status-tracking = Rotacja
settings-osc-vrchat-status-badge-error = Błąd
settings-osc-vrchat-status-badge-unknown = Nieznany

## VMC OSC settings

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

