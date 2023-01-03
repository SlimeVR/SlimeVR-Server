### SlimeVR complete GUI translations
# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropiate 
# features like variables and selectors in each appropiate case!
# And also comment the string if it's something not easy to translate so you help
# translators on what it means

## Websocket (server) status
websocket-connecting = Łączenie z serwerem
websocket-connection_lost = Połączenie z serwerem zostało utracone. Próba ponownego połączenia...

## Tips
tips-find_tracker = Nie wiesz który tracker to który? Obracaj Trackerem , podczas obracania będzie sie on podświetlał w serwerze.
tips-do_not_move_heels = Upewnij się aby pięty pozostały w bezruchu podczas nagrywania.

## Body parts
body_part-NONE = Nie Przypisano
body_part-HEAD = Głowa
body_part-NECK = Szyja
body_part-RIGHT_SHOULDER = Prawe Ramie
body_part-RIGHT_UPPER_ARM = Prawy Biceps
body_part-RIGHT_LOWER_ARM = Prawe PrzedRamie
body_part-RIGHT_HAND = Prawa Dłoń
body_part-RIGHT_UPPER_LEG = Prawe Udo
body_part-RIGHT_LOWER_LEG = Prawy Podudzie
body_part-RIGHT_FOOT = Prawa Stopa
body_part-RIGHT_CONTROLLER = Right controller
body_part-CHEST = Klatka Piersiowa
body_part-WAIST = Pas
body_part-HIP = Biodra
body_part-LEFT_SHOULDER = Lewe Ramie
body_part-LEFT_UPPER_ARM = Lewy Biceps
body_part-LEFT_LOWER_ARM = Lewe PrzedRamie
body_part-LEFT_HAND = Lewa Dłoń
body_part-LEFT_UPPER_LEG = Lewe Udo
body_part-LEFT_LOWER_LEG = Lewe Podudzie
body_part-LEFT_FOOT = Lewa Stopa
body_part-LEFT_CONTROLLER = Left controller

## Skeleton stuff
skeleton_bone-NONE = Brak
skeleton_bone-HEAD = Head Shift
skeleton_bone-NECK = Długość Szyi
skeleton_bone-TORSO = Długość Tułowia
skeleton_bone-CHEST = Długość Klatki Piersiowej
skeleton_bone-WAIST = Waist Distance
skeleton_bone-HIP_OFFSET = Offset Bioder
skeleton_bone-HIPS_WIDTH = Szerokość Bioder
skeleton_bone-LEGS_LENGTH = Długość Nóg
skeleton_bone-KNEE_HEIGHT = Wysokość Kolana
skeleton_bone-FOOT_LENGTH = Długość Stopy
skeleton_bone-FOOT_SHIFT = Foot Shift
skeleton_bone-SKELETON_OFFSET = Skeleton Offset
skeleton_bone-CONTROLLER_DISTANCE_Z = Controller Distance Z
skeleton_bone-CONTROLLER_DISTANCE_Y = Controller Distance Y
skeleton_bone-FOREARM_LENGTH = Długość PrzedRamienia
skeleton_bone-SHOULDERS_DISTANCE = Shoulders Distance
skeleton_bone-SHOULDERS_WIDTH = Szerokość Ramion
skeleton_bone-UPPER_ARM_LENGTH = Długość Bicepsa
skeleton_bone-ELBOW_OFFSET = Offset Łokcia

## Tracker reset buttons
reset-reset_all = Zresetuj wszystkie wymiary
reset-full = Reset
reset-mounting = Zresetuj Położenie
reset-quick = Szybki Reset

## Serial detection stuff
serial_detection-new_device-p0 = Wykryto Nowe Urządzenie.
serial_detection-new_device-p1 = Wprowadź dane WiFi!
serial_detection-new_device-p2 = Wybierz co chcesz z nim zrobić.
serial_detection-open_wifi = Połącz z WiFi
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

## Overlay settings
overlay-is_visible_label = Pokaż Overlay w SteamVR
overlay-is_mirrored_label = Pokaż Overlay jako Lustro

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
tracker-settings-drift_compensation_section-description = Should this tracker compensate for its drift when drift compensation is enabled?
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
tracker_selection_menu-NONE = Which tracker do you want to be unassigned?
tracker_selection_menu-HEAD = { -tracker_selection-part } head?
tracker_selection_menu-NECK = { -tracker_selection-part } neck?
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part } right shoulder?
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part } right upper arm?
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part } right lower arm?
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part } right hand?
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part } right thigh?
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part } right ankle?
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part } right foot?
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part } right controller?
tracker_selection_menu-CHEST = { -tracker_selection-part } chest?
tracker_selection_menu-WAIST = { -tracker_selection-part } waist?
tracker_selection_menu-HIP = { -tracker_selection-part } hip?
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part } left shoulder?
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part } left upper arm?
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part } left lower arm?
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part } left hand?
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part } left thigh?
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part } left ankle?
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part } left foot?
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part } left controller?

tracker_selection_menu-unassigned = Nieprzydzielone trackery
tracker_selection_menu-assigned = Przydzielone trackery
tracker_selection_menu-dont_assign = Nie przydzielaj

## Mounting menu
mounting_selection_menu = Gdzie chciałbyś ten tracker?
mounting_selection_menu-close = Zamknij

## Sidebar settings
settings-sidebar-title = Ustawienia
settings-sidebar-general = Ogólne
settings-sidebar-tracker_mechanics = Tracker mechanics
settings-sidebar-fk_settings = FK settings
settings-sidebar-gesture_control = Gesture control
settings-sidebar-interface = Interfejs
settings-sidebar-osc_router = OSC router
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
settings-general-steamvr-trackers-waist = Bruch
settings-general-steamvr-trackers-chest = Klatka Piersiowa
settings-general-steamvr-trackers-feet = Stopy
settings-general-steamvr-trackers-knees = Kolana
settings-general-steamvr-trackers-elbows = Łokcie
settings-general-steamvr-trackers-hands = Ręce

## Tracker mechanics
settings-general-tracker_mechanics = Tracker mechanics
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
settings-general-tracker_mechanics-drift_compensation = Drift compensation
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Compensates IMU yaw drift by applying an inverse rotation.
    Change amount of compensation and up to how many resets are taken into account.
settings-general-tracker_mechanics-drift_compensation-enabled-label = Drift compensation
settings-general-tracker_mechanics-drift_compensation-amount-label = Compensation amount
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Use up to x last resets

## FK settings
settings-general-fk_settings = Tracking settings
settings-general-fk_settings-leg_tweak = Leg tweaks
settings-general-fk_settings-leg_tweak-description = Floor-clip can Reduce or even eliminates clipping with the floor but may cause problems when on your knees. Skating-correction corrects for ice skating, but can decrease accuracy in certain movement patterns.
# Floor clip: 
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Floor clip
# Skating correction: 
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Skating correction
settings-general-fk_settings-leg_tweak-skating_correction-amount = Skating correction strength
settings-general-fk_settings-arm_fk = Arm tracking
settings-general-fk_settings-arm_fk-description = Change the way the arms are tracked.
settings-general-fk_settings-arm_fk-force_arms = Force arms from HMD
settings-general-fk_settings-skeleton_settings = Skeleton settings
settings-general-fk_settings-skeleton_settings-description = Toggle skeleton settings on or off. It is recommended to leave these on.
settings-general-fk_settings-skeleton_settings-extended_spine = Extended spine
settings-general-fk_settings-skeleton_settings-extended_pelvis = Extended pelvis
settings-general-fk_settings-skeleton_settings-extended_knees = Extended knee
settings-general-fk_settings-vive_emulation-title = Vive emulation
settings-general-fk_settings-vive_emulation-description = Emulate the waist tracker problems that Vive trackers have. This is a joke and makes tracking worse.
settings-general-fk_settings-vive_emulation-label = Enable Vive emulation

## Gesture control settings (tracker tapping)
settings-general-gesture_control = Kontrola Gestami
settings-general-gesture_control-subtitle = Dotknij 2 razy by wykonać szybki reset
settings-general-gesture_control-description = Włącz lub wyłącz opcje szybkiego resetowanie podwójnym dotknięciem. Stuknij 2 razy w jakąkolwiek część trackera na klatce piersiowej aby wykonać szybki reset. Opóźnienie jest czasem pomiędzy stuknięciem a wykonaniem szybkiego resetu.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps = { $amount ->
    [one] 1 tap
    *[other] { $amount } taps
}
settings-general-gesture_control-quickResetEnabled = Enable tap to quick reset
settings-general-gesture_control-quickResetDelay = Quick reset delay
settings-general-gesture_control-quickResetTaps = Taps for quick reset
settings-general-gesture_control-resetEnabled = Enable tap to reset
settings-general-gesture_control-resetDelay = Reset delay
settings-general-gesture_control-resetTaps = Taps for reset
settings-general-gesture_control-mountingResetEnabled = Enable tap to reset mounting
settings-general-gesture_control-mountingResetDelay = Mounting reset delay
settings-general-gesture_control-mountingResetTaps = Taps for mounting reset

## Interface settings
settings-general-interface = Interfejs
settings-general-interface-dev_mode = Tryb Dewelopera
settings-general-interface-dev_mode-description = Ten tryb przydaje się do sprawdzania większej ilości danych.
settings-general-interface-dev_mode-label = Tryb Dewelopera
settings-general-interface-serial_detection = Wykrywanie urządzeń
settings-general-interface-serial_detection-description = Ta opcja daje powiadomienia jeżeli serwer wykryje urządzenie które może być trackerem
settings-general-interface-serial_detection-label = Wykrywanie urządzeń
settings-general-interface-lang = Wybierz Język
settings-general-interface-lang-description = Zmień podstawowy język jaki chcesz używać
settings-general-interface-lang-placeholder = Wybierz Język który będziesz używać

## Serial settings
settings-serial = Serial Console
# This cares about multilines
settings-serial-description =
    This is a live information feed for serial communication.
    May be useful if you need to know the firmware is acting up.
settings-serial-connection_lost = Connection to serial lost, Reconnecting...
settings-serial-reboot = Reboot
settings-serial-factory_reset = Factory Reset
settings-serial-get_infos = Get Infos
settings-serial-serial_select = Select a serial port
settings-serial-auto_dropdown_item = Auto

## OSC router settings
settings-osc-router = OSC router
# This cares about multilines
settings-osc-router-description =
    Forward OSC messages from another program.
    Useful for using another OSC program with VRChat for example.
settings-osc-router-enable = Enable
settings-osc-router-enable-description = Toggle the forwarding of messages.
settings-osc-router-enable-label = Enable
settings-osc-router-network = Network ports
# This cares about multilines
settings-osc-router-network-description =
    Set the ports for listening and sending data.
    These can be the same as other ports used in the SlimeVR server.
settings-osc-router-network-port_in =
    .label = Port In
    .placeholder = Port in (default: 9002)
settings-osc-router-network-port_out =
    .label = Port Out
    .placeholder = Port out (default: 9000)
settings-osc-router-network-address = Network address
settings-osc-router-network-address-description = Set the address to send out data at.
settings-osc-router-network-address-placeholder = IPV4 address

## OSC VRChat settings
settings-osc-vrchat = VRChat OSC Trackers
# This cares about multilines
settings-osc-vrchat-description =
    Change VRChat-specific settings to receive HMD data and send
    trackers data for FBT (works on Quest standalone).
settings-osc-vrchat-enable = Enable
settings-osc-vrchat-enable-description = Toggle the sending and receiving of data.
settings-osc-vrchat-enable-label = Enable
settings-osc-vrchat-network = Network ports
settings-osc-vrchat-network-description = Set the ports for listening and sending data to VRChat.
settings-osc-vrchat-network-port_in =
    .label = Port In
    .placeholder = Port in (default: 9001)
settings-osc-vrchat-network-port_out =
    .label = Port Out
    .placeholder = Port out (default: 9000)
settings-osc-vrchat-network-address = Network address
settings-osc-vrchat-network-address-description = Choose which address to send out data to VRChat (check your wifi settings on your device).
settings-osc-vrchat-network-address-placeholder = VRChat ip address
settings-osc-vrchat-network-trackers = Trackers
settings-osc-vrchat-network-trackers-description = Toggle the sending of specific trackers via OSC.
settings-osc-vrchat-network-trackers-chest = Chest
settings-osc-vrchat-network-trackers-waist = Waist
settings-osc-vrchat-network-trackers-knees = Knees
settings-osc-vrchat-network-trackers-feet = Feet
settings-osc-vrchat-network-trackers-elbows = Elbows

## Setup/onboarding menu
onboarding-skip = Pomiń wstępną konfiguracje
onboarding-continue = Kontynuuj
onboarding-wip = W trakcie prac

## WiFi setup
onboarding-wifi_creds-back = Cofnij się do początku
onboarding-wifi_creds = Wpisz dane WiFi
# This cares about multilines
onboarding-wifi_creds-description =
    Trackery będą używać tej sieci do łączenia się z serwerem
    proszę używać sieci do której jest się połączonym
onboarding-wifi_creds-skip = Pomiń ustawienia WiFi
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
# This cares about multilines and it's centered!!
onboarding-home-description =
    Full-body tracking
    dla każdego
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
onboarding-connect_tracker-back = Cofnij się do ustawień WiFi
onboarding-connect_tracker-title = Połącz trackery
onboarding-connect_tracker-description-p0 = Teraz czas na zabawe, połączenie wszystkich trackerów!
onboarding-connect_tracker-description-p1 = Po prostu połącz wszystkie dotychczas nie połączone trackery za pomocą USB
onboarding-connect_tracker-issue-serial = Mam problemy z połączeniem!
onboarding-connect_tracker-usb = USB Tracker
onboarding-connect_tracker-connection_status-connecting = Wysyłanie danych WiFi
onboarding-connect_tracker-connection_status-connected = Połączono z WiFi
onboarding-connect_tracker-connection_status-error = Nie można połączyć z Wifi
onboarding-connect_tracker-connection_status-start_connecting = Szukanie Trackerów
onboarding-connect_tracker-connection_status-handshake = Połączono z serwerem
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers = { $amount ->
    [0] No trackers
    [one] 1 tracker
    *[other] { $amount } trackers
} connected
onboarding-connect_tracker-next = Połączyłem już wszystkie trackery

## Tracker assignment setup
onboarding-assign_trackers-back = Cofnij się do ustawień WiFi
onboarding-assign_trackers-title = Przydziel Trackery
onboarding-assign_trackers-description = Wybierzmy gdzie idzie jaki tracker. Naciśnij gdzie chcesz go przydzielić
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned = { $assigned } of { $trackers ->
    [one] 1 tracker
    *[other] { $trackers } trackers
} assigned
onboarding-assign_trackers-advanced = Pokaż zaawansowane ustawienia pozycji
onboarding-assign_trackers-next = Przydzieliłem już wszystkie trackery

## Tracker manual mounting setup
onboarding-manual_mounting-back = Cofnij się żeby wejść do VR
onboarding-manual_mounting = Pozycjonowanie Manualne
onboarding-manual_mounting-description = Kliknij na każdy tracker i wybierz w jaki sposób są zamontowane
onboarding-manual_mounting-auto_mounting = Automatic mounting
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

## Tracker manual proportions setup
onboarding-manual_proportions-back = Go Back to Reset tutorial
onboarding-manual_proportions-title = Manualne Proporcje Ciała
onboarding-manual_proportions-precision = Precyzyjna Regulacja
onboarding-manual_proportions-auto = Automatyczna Kalibracja

## Tracker automatic proportions setup
onboarding-automatic_proportions-back = Go Back to Reset tutorial
onboarding-automatic_proportions-title = Zmierz swoje ciało
onboarding-automatic_proportions-description = Aby SlimeVR działało poprawnie, musimy znać długość twoich kości. Ta kalibracja zrobi to za ciebie.
onboarding-automatic_proportions-manual = Kalibracja Manualna
onboarding-automatic_proportions-prev_step = Poprzedni krok
onboarding-automatic_proportions-put_trackers_on-title = Załóż trackery
onboarding-automatic_proportions-put_trackers_on-description = Aby skalibrować proporcje, użyjemy trackerów które przed chwilą przypisałeś. Załóż wszystkie trackery, będziesz widział który to który na postaci po prawej.
onboarding-automatic_proportions-put_trackers_on-next = Mam wszystkie trackery założone
onboarding-automatic_proportions-preparation-title = Przygotowania
onboarding-automatic_proportions-preparation-description = Połóż krzesło za sobą w twojej przeszczeni grania. Bądź gotowy do siadania podczas automatycznej kalibracji kości.
onboarding-automatic_proportions-preparation-next = Jestem przed krzesłem
onboarding-automatic_proportions-start_recording-title = Bądź gotowy żeby się ruszać
onboarding-automatic_proportions-start_recording-description = Będziemy teraz nagrywać specyficzne pozycje i ruchy. Będą one pokazane w następnym okienku. Bądź gotowy po naciśnięciu przycisku!
onboarding-automatic_proportions-start_recording-next = Uruchom nagrywanie
onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = Nagrywanie w toku...
onboarding-automatic_proportions-recording-description-p1 = Wykonuj ruchy pokazane niżej:
onboarding-automatic_proportions-recording-steps-0 = Zegnij kolana kilka razy.
onboarding-automatic_proportions-recording-steps-1 = Usiądź na krześle ,po czym wstań.
onboarding-automatic_proportions-recording-steps-2 = Przekręć ciało w lewo ,po czym przechyl się w prawo.
onboarding-automatic_proportions-recording-steps-3 = Przekręć ciało w prawo ,po czym przechyl się w lewo.
onboarding-automatic_proportions-recording-steps-4 = Poruszaj się dopuki czas się nie skończy
onboarding-automatic_proportions-recording-processing = Przetwarzanie wyników
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 15)
onboarding-automatic_proportions-recording-timer = { $time ->
    [one] 1 second left
    *[other] { $time } seconds left
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
