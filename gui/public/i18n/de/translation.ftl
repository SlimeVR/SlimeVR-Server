# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Verbindung zum Server wird hergestellt...
websocket-connection_lost = Verbindung zum Server verloren. Versuche Verbindung wiederherzustellen ...
websocket-connection_lost-desc = Es sieht so aus, als wäre der SlimeVR-Server abgestürzt. Überprüfen Sie die Protokolle und starten Sie das Programm neu.
websocket-timedout = Es konnte keine Verbindung zum Server hergestellt werden.
websocket-timedout-desc = Es sieht so aus, als ob der SlimeVR-Server abgestürzt ist oder nicht rechtzeitig geantwortet hat. Überprüfen Sie die Protokolle und starten Sie das Programm neu.
websocket-error-close = SlimeVR beenden
websocket-error-logs = Öffne den Logs-Ordner

## Update notification

version_update-title = Neue Version verfügbar: { $version }
version_update-description = Wenn Sie auf "{ version_update-update }" klicken, wird das SlimeVR-Installationsprogramm heruntergeladen.
version_update-update = Aktualisieren
version_update-close = Schließen

## Tips

tips-find_tracker = Sie sind sich nicht sicher, welcher Tracker welcher ist? Schütteln Sie einen Tracker, um den zugehörigen Eintrag hervorzuheben.
tips-do_not_move_heels = Stellen Sie sicher, dass Sie Ihre Fersen während der Aufnahme nicht bewegen!
tips-file_select = Dateien per Drag & Drop verwenden oder <u>durchsuchen</u>
tips-tap_setup = Sie können langsam 2 Mal auf Ihren Tracker tippen, um ihn auszuwählen, anstatt ihn aus dem Menü auszuwählen.
tips-turn_on_tracker = Verwenden Sie offizielle SlimeVR-Tracker? Vergessen Sie nicht den <b><em>Tracker einzuschalten</em></b>, nachdem Sie ihn an den PC angeschlossen haben!
tips-failed_webgl = Fehler beim Initialisieren von WebGL.

## Units

unit-meter = Meter
unit-foot = Fuß
unit-inch = Zoll
unit-cm = Zentimeter

## Body parts

body_part-NONE = Nicht zugewiesen
body_part-HEAD = Kopf
body_part-NECK = Hals
body_part-RIGHT_SHOULDER = Rechte Schulter
body_part-RIGHT_UPPER_ARM = Rechter Oberarm
body_part-RIGHT_LOWER_ARM = Rechter Unterarm
body_part-RIGHT_HAND = Rechte Hand
body_part-RIGHT_UPPER_LEG = Rechter Oberschenkel
body_part-RIGHT_LOWER_LEG = Rechter Knöchel
body_part-RIGHT_FOOT = Rechter Fuß
body_part-UPPER_CHEST = Obere Brust
body_part-CHEST = Brust
body_part-WAIST = Taille
body_part-HIP = Hüfte
body_part-LEFT_SHOULDER = Linke Schulter
body_part-LEFT_UPPER_ARM = Linker Oberarm
body_part-LEFT_LOWER_ARM = Linker Unterarm
body_part-LEFT_HAND = Linke Hand
body_part-LEFT_UPPER_LEG = Linker Oberschenkel
body_part-LEFT_LOWER_LEG = Linker Knöchel
body_part-LEFT_FOOT = Linker Fuß
body_part-LEFT_THUMB_METACARPAL = Linker Daumen-Mittelhandknochen
body_part-LEFT_THUMB_PROXIMAL = Linkes Daumengrundglied
body_part-LEFT_THUMB_DISTAL = Linkes Daumenendglied
body_part-LEFT_INDEX_PROXIMAL = Linkes Zeigefinger-Grundglied
body_part-LEFT_INDEX_INTERMEDIATE = Linkes Zeigefinger-Mittelglied
body_part-LEFT_INDEX_DISTAL = Linkes Zeigefinger-Endglied
body_part-LEFT_MIDDLE_PROXIMAL = Linkes Mittelfinger-Grundglied
body_part-LEFT_MIDDLE_INTERMEDIATE = Linkes Mittelfinger-Mittelglied
body_part-LEFT_MIDDLE_DISTAL = Linkes Mittelfinger-Endglied
body_part-LEFT_RING_PROXIMAL = Linkes Ringfinger-Grundglied
body_part-LEFT_RING_INTERMEDIATE = Linkes Ringfinger-Mittelglied
body_part-LEFT_RING_DISTAL = Linkes Ringfinger-Endglied
body_part-LEFT_LITTLE_PROXIMAL = Linkes Kleinfinger-Grundglied
body_part-LEFT_LITTLE_INTERMEDIATE = Linkes Kleinfinger-Mittelglied
body_part-LEFT_LITTLE_DISTAL = Linkes Kleinfinger-Endglied
body_part-RIGHT_THUMB_METACARPAL = Rechter Daumen-Mittelhandknochen
body_part-RIGHT_THUMB_PROXIMAL = Rechtes Daumengrundglied
body_part-RIGHT_THUMB_DISTAL = Rechtes Daumenendglied
body_part-RIGHT_INDEX_PROXIMAL = Rechtes Zeigefinger-Grundglied
body_part-RIGHT_INDEX_INTERMEDIATE = Rechtes Zeigefinger-Mittelglied
body_part-RIGHT_INDEX_DISTAL = Rechtes Zeigefinger-Endglied
body_part-RIGHT_MIDDLE_PROXIMAL = Rechtes Mittelfinger-Grundglied
body_part-RIGHT_MIDDLE_INTERMEDIATE = Rechtes Mittelfinger-Mittelglied
body_part-RIGHT_MIDDLE_DISTAL = Rechtes Mittelfinger-Endglied
body_part-RIGHT_RING_PROXIMAL = Rechtes Ringfinger-Grundglied
body_part-RIGHT_RING_INTERMEDIATE = Rechtes Ringfinger-Mittelglied
body_part-RIGHT_RING_DISTAL = Rechtes Ringfinger-Endglied
body_part-RIGHT_LITTLE_PROXIMAL = Rechtes Kleinfinger-Grundglied
body_part-RIGHT_LITTLE_INTERMEDIATE = Rechtes Kleinfinger-Mittelglied
body_part-RIGHT_LITTLE_DISTAL = Rechtes Kleinfinger-Endglied

## BoardType

board_type-UNKNOWN = Unbekannt
board_type-NODEMCU = NodeMCU
board_type-CUSTOM = Benutzerdefiniertes Board
board_type-WROOM32 = WROOM32
board_type-WEMOSD1MINI = Wemos D1 Mini
board_type-TTGO_TBASE = TTGO T-Base
board_type-ESP01 = ESP-01
board_type-SLIMEVR = SlimeVR
board_type-SLIMEVR_DEV = SlimeVR Dev Board
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
board_type-GLOVE_IMU_SLIMEVR_DEV = SlimeVR Dev-IMU-Handschuh

## Proportions

skeleton_bone-NONE = Keine
skeleton_bone-HEAD = Kopfverschiebung
skeleton_bone-HEAD-desc =
    Dies ist der Abstand von Ihrem Headset zur Mitte Ihres Kopfes.
    Um ihn anzupassen, bewegen Sie den Kopf von links nach rechts, als würden Sie „nein“ sagen. Ändern Sie den Wert so lange, bis sich die anderen Tracker nicht mehr mitbewegen.
skeleton_bone-NECK = Halslänge
skeleton_bone-NECK-desc =
    Dies ist der Abstand von der Mitte Ihres Kopfes bis zum Ansatz Ihres Nackens.
    Um diesen anzupassen, nicken Sie mit Ihren Kopf, als würden Sie "ja" sagen, oder neigen Sie Ihren Kopf nach links und rechts. und modifizieren Sie es, bis die Bewegung in anderen Trackern vernachlässigbar ist. Passen Sie den Wert so lange an, bis Bewegungen anderer Tracker kaum noch vorhanden sind.
skeleton_bone-torso_group = Oberkörperhöhe
skeleton_bone-torso_group-desc =
    Dies ist der Abstand vom Ansatz Ihres Nackens bis zu Ihren Hüften.
    Stehen Sie aufrecht und ändern Sie den Wert, bis Ihre virtuellen Hüften mit Ihren echten übereinstimmen.
skeleton_bone-UPPER_CHEST = Obere Brustlänge
skeleton_bone-UPPER_CHEST-desc =
    Dies ist der Abstand vom Ansatz Ihres Nackens bis zur Mitte Ihrer Brust.
    Passen Sie zunächst Ihre Rumpflänge korrekt an und verändern Sie dann diesen Wert in verschiedenen Positionen (z. B. im Sitzen, beim Bücken oder Liegen), bis Ihre virtuelle Wirbelsäule mit Ihrer echten übereinstimmt.
skeleton_bone-CHEST_OFFSET = Brustversatz
skeleton_bone-CHEST_OFFSET-desc =
    Dies kann angepasst werden, um Ihren virtuellen Brust-Tracker nach oben oder unten zu verschieben, um
    die Kalibrierung in bestimmten Spielen oder Anwendungen zu unterstützen, die möglicherweise einen höheren oder niedrigeren Wert erwarten.
skeleton_bone-CHEST = Brustabstand
skeleton_bone-CHEST-desc =
    Dies ist der Abstand vom Ansatz der Brust bis zur Mitte Ihrer Wirbelsäule.
    Passen Sie zunächst Ihre Rumpflänge korrekt an und verändern Sie dann diesen Wert in verschiedenen Positionen (z.B. im Sitzen, beim Bücken oder Liegen), bis Ihre virtuelle Wirbelsäule mit Ihrer echten übereinstimmt.
skeleton_bone-WAIST = Taillenabstand
skeleton_bone-HIP = Hüftlänge
skeleton_bone-HIP_OFFSET = Hüftversatz
skeleton_bone-HIPS_WIDTH = Hüftbreite
skeleton_bone-leg_group = Beinlänge
skeleton_bone-UPPER_LEG = Linker Oberschenkellänge
skeleton_bone-LOWER_LEG = Unterschenkellänge
skeleton_bone-FOOT_LENGTH = Fußlänge
skeleton_bone-FOOT_SHIFT = Fußverschiebung
skeleton_bone-SKELETON_OFFSET = Skelettversatz
skeleton_bone-SHOULDERS_DISTANCE = Schulterentfernung
skeleton_bone-SHOULDERS_WIDTH = Schulterbreite
skeleton_bone-arm_group = Armlänge
skeleton_bone-UPPER_ARM = Oberarmlänge
skeleton_bone-LOWER_ARM = Unterarmlänge
skeleton_bone-HAND_Y = Y-Abstand der Hände
skeleton_bone-HAND_Z = Z-Abstand der Hände
skeleton_bone-ELBOW_OFFSET = Ellbogenversatz

## Tracker reset buttons

reset-reset_all = Alle Proportionen zurücksetzen
reset-reset_all_warning-v2 =
    <b>Warnung:</b>Ihre Proportionen werden auf die Standardwerte zurückgesetzt und entsprechend Ihrer konfigurierten Körpergröße skaliert.
    Möchten Sie dies wirklich tun?
reset-reset_all_warning-reset = Proportionen zurücksetzen
reset-reset_all_warning-cancel = Abbrechen
reset-reset_all_warning_default-v2 =
    <b>Warnung:</b> Ihre Körpergröße wurde nicht konfiguriert. Ihre Proportionen werden auf die Standardwerte mit der Standardgröße zurückgesetzt.
    Möchten Sie dies wirklich tun?
reset-full = Reset
reset-mounting = Befestigungs-Reset
reset-mounting-feet = Fuß-Befestigungs-Reset
reset-mounting-fingers = Fingerkalibrierung
reset-yaw = Horizontaler Reset
reset-error-no_feet_tracker = Kein Fußtracker zugewiesen
reset-error-no_fingers_tracker = Kein Fingertracker zugewiesen

## Serial detection stuff

serial_detection-new_device-p0 = Neues serielles Gerät erkannt!
serial_detection-new_device-p1 = Geben Sie Ihre WLAN-Zugangsdaten ein!
serial_detection-new_device-p2 = Bitte wählen Sie, was Sie damit machen möchten
serial_detection-open_wifi = Mit WLAN verbinden
serial_detection-open_serial = Serielle Konsole öffnen
serial_detection-submit = Absenden!
serial_detection-close = Schließen

## Navigation bar

navbar-home = Start
navbar-body_proportions = Körperproportionen
navbar-trackers_assign = Tracker-Zuordnung
navbar-mounting = Tracker-Ausrichtung
navbar-onboarding = Einrichtungs-Assistent
navbar-settings = Einstellungen

## Biovision hierarchy recording

bvh-start_recording = BVH aufnehmen
bvh-stop_recording = BVH-Aufnahme speichern
bvh-recording = Aufnahme läuft...
bvh-save_title = BVH-Aufnahme speichern

## Tracking pause

tracking-unpaused = Tracking pausieren
tracking-paused = Tracking fortsetzen

## Widget: Overlay settings

widget-overlay = Visualisierung
widget-overlay-is_visible_label = Visualisierung in SteamVR anzeigen
widget-overlay-is_mirrored_label = Visualisierung spiegeln

## Widget: Drift compensation

widget-drift_compensation-clear = Driftkompensation zurücksetzen

## Widget: Clear Mounting calibration

widget-clear_mounting = Tracker-Ausrichtung zurücksetzen

## Widget: Developer settings

widget-developer_mode = Entwicklermodus
widget-developer_mode-high_contrast = Hoher Kontrast
widget-developer_mode-precise_rotation = Präzise Drehung
widget-developer_mode-fast_data_feed = Schnelleres Update-Intervall
widget-developer_mode-filter_slimes_and_hmd = Slime-Tracker und HMD filtern
widget-developer_mode-sort_by_name = Nach Namen sortieren
widget-developer_mode-raw_slime_rotation = Rohe Drehung
widget-developer_mode-more_info = Mehr Infos

## Widget: IMU Visualizer

widget-imu_visualizer = Drehung
widget-imu_visualizer-preview = Vorschau
widget-imu_visualizer-hide = Ausblenden
widget-imu_visualizer-rotation_raw = Rohe Drehung
widget-imu_visualizer-rotation_preview = Vorschau
widget-imu_visualizer-acceleration = Beschleunigung
widget-imu_visualizer-position = Position
widget-imu_visualizer-stay_aligned = Stay Aligned

## Widget: Skeleton Visualizer

widget-skeleton_visualizer-preview = Skelett Vorschau
widget-skeleton_visualizer-hide = Ausblenden

## Tracker status

tracker-status-none = Kein Status
tracker-status-busy = Beschäftigt
tracker-status-error = Fehler
tracker-status-disconnected = Getrennt
tracker-status-occluded = Verdeckt
tracker-status-ok = Verbunden
tracker-status-timed_out = Zeitüberschreitung

## Tracker status columns

tracker-table-column-name = Name
tracker-table-column-type = Typ
tracker-table-column-battery = Batterie
tracker-table-column-ping = Latenz
tracker-table-column-tps = TPS
tracker-table-column-temperature = Temp. °C
tracker-table-column-linear-acceleration = Beschleunigung X/Y/Z
tracker-table-column-rotation = Rotation X/Y/Z
tracker-table-column-position = Position X/Y/Z
tracker-table-column-stay_aligned = Stay Aligned
tracker-table-column-url = Adresse

## Tracker rotation

tracker-rotation-front = Vorne
tracker-rotation-front_left = Vorne-Links
tracker-rotation-front_right = Vorne-Rechts
tracker-rotation-left = Links
tracker-rotation-right = Rechts
tracker-rotation-back = Hinten
tracker-rotation-back_left = Hinten-Links
tracker-rotation-back_right = Hinten-Rechts
tracker-rotation-custom = Benutzerdefiniert
tracker-rotation-overriden = (von der Tracker-Ausrichtung überschrieben)

## Tracker information

tracker-infos-manufacturer = Hersteller
tracker-infos-display_name = Anzeigename
tracker-infos-custom_name = Benutzerdefinierter Name
tracker-infos-url = Tracker-Adresse
tracker-infos-version = Firmware-Version
tracker-infos-hardware_rev = Hardware-Version
tracker-infos-hardware_identifier = Hardware-ID
tracker-infos-data_support = Daten-Support
tracker-infos-imu = IMU-Sensor
tracker-infos-board_type = Platine
tracker-infos-network_version = Protokoll Version
tracker-infos-magnetometer = Magnetometer
tracker-infos-magnetometer-status-v1 =
    { $status ->
        [DISABLED] Ausgeschalten
        [ENABLED] Angeschalten
       *[NOT_SUPPORTED] Nicht unterstützt
    }

## Tracker settings

tracker-settings-back = Zurück zur Tracker-Liste
tracker-settings-title = Tracker-Einstellungen
tracker-settings-assignment_section = Zuweisung
tracker-settings-assignment_section-description = Welcher Körperteil dem Tracker zugewiesen ist.
tracker-settings-assignment_section-edit = Zuweisung bearbeiten
tracker-settings-mounting_section = Befestigungsposition
tracker-settings-mounting_section-description = Wo ist der Tracker befestigt?
tracker-settings-mounting_section-edit = Befestigung bearbeiten
tracker-settings-drift_compensation_section = Drift-Kompensierung
tracker-settings-drift_compensation_section-description = Soll dieser Tracker Drift kompensieren, wenn die Drift-Kompensierung allgemein aktiviert ist?
tracker-settings-drift_compensation_section-edit = Erlaube Drift Kompensierung
tracker-settings-use_mag = Magnetometer auf diesem Tracker zulassen
# Multiline!
tracker-settings-use_mag-description =
    Soll dieser Tracker das Magnetometer verwenden um Drift zu reduzieren, wenn die Verwendung von Magnetometer erlaubt ist? <b> Bitten schalten Sie den Tracker nicht aus, während Sie diese Einstellung umschalten!</b>
    
    Sie müssen zuerst die Verwendung des Magnetometers zulassen, <magSetting>klicken Sie hier, um zu den Einstellungen zu gelangen</magSetting>.
tracker-settings-use_mag-label = Magnetometer zulassen
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Trackername
tracker-settings-name_section-description = Geben Sie ihm einen süßen Spitznamen :)
tracker-settings-name_section-placeholder = NightyBeast's linkes Bein
tracker-settings-name_section-label = Trackername
tracker-settings-forget = Tracker Vergessen
tracker-settings-forget-description = Entfernt den Tracker vom SlimeVR Server und verhindert, dass er sich wieder verbindet, bis der Server neu gestartet wurde. Die Konfiguration des Trackers geht nicht verloren.
tracker-settings-forget-label = Tracker Vergessen
tracker-settings-update-unavailable-v2 = Keine Veröffentlichungen gefunden
tracker-settings-update-incompatible = Update nicht möglich. Board inkompatibel
tracker-settings-update-low-battery = Aktualisierung nicht möglich. Akku unter 50 %
tracker-settings-update-up_to_date = Auf dem neusten Stand
tracker-settings-update-blocked = Update nicht verfügbar. Weitere Veröffentlichungen sind nicht verfügbar.
tracker-settings-update = Jetzt aktualisieren
tracker-settings-update-title = Firmware-Version

## Tracker part card info

tracker-part_card-no_name = Kein Name
tracker-part_card-unassigned = Nicht zugewiesen

## Body assignment menu

body_assignment_menu = Wo tragen Sie diesen Tracker?
body_assignment_menu-description = Wählen Sie die Position aus, an dem Sie diesen Tracker befestigt haben. Alternativ können Sie auch alle Tracker auf einmal verwalten statt einzeln.
body_assignment_menu-show_advanced_locations = Zeige erweiterte Tracker-Positionen
body_assignment_menu-manage_trackers = Verwalte alle Tracker
body_assignment_menu-unassign_tracker = Zuweisung des Trackers aufheben

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = Welcher Tracker soll
tracker_selection_menu-NONE = Welchen Tracker möchten Sie zuweisen?
tracker_selection_menu-HEAD = { -tracker_selection-part } dem Kopf zugewiesen werden?
tracker_selection_menu-NECK = { -tracker_selection-part } dem Nacken zugewiesen werden?
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part } der rechten Schulter zugewiesen werden?
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part } dem rechten Oberarm zugewiesen werden?
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part } dem rechten Unterarm zugewiesen werden?
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part } der rechten Hand zugewiesen werden?
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part } dem rechten Oberschenkel zugewiesen werden?
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part } dem rechten Unterschenkel zugewiesen werden?
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part } dem rechten Fuß zugewiesen werden?
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part } dem rechten Controller zugewiesen werden?
tracker_selection_menu-UPPER_CHEST = { -tracker_selection-part } obere Brust?
tracker_selection_menu-CHEST = { -tracker_selection-part } der Brust zugewiesen werden?
tracker_selection_menu-WAIST = { -tracker_selection-part } der Taille zugewiesen werden?
tracker_selection_menu-HIP = { -tracker_selection-part } der Hüfte zugewiesen werden?
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part } der linken Schulter zugewiesen werden?
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part } dem linken Oberarm zugewiesen werden?
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part } dem linken Unterarm zugewiesen werden?
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part } der linken Hand zugewiesen werden?
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part } dem linken Oberschenkel zugewiesen werden?
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part } dem linken Unterschenkel zugewiesen werden?
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part } dem linken Fuß zugewiesen zugewiesen werden?
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part } dem linken Controller zugewiesen werden?
tracker_selection_menu-unassigned = Nicht zugewiesene Tracker
tracker_selection_menu-assigned = Zugewiesene Tracker
tracker_selection_menu-dont_assign = Nicht zuweisen
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Warnung:</b> Ein Hals-Tracker kann tödlich sein, wenn dieser zu fest angezogen ist.
    Der Riemen kann die Blutzirkulation zu Ihrem Kopf unterbrechen!
tracker_selection_menu-neck_warning-done = Ich verstehe die Risiken
tracker_selection_menu-neck_warning-cancel = Abbruch

## Mounting menu

mounting_selection_menu = Wo möchten Sie diesen Tracker platzieren?
mounting_selection_menu-close = Schließen

## Sidebar settings

settings-sidebar-title = Einstellungen
settings-sidebar-general = Allgemein
settings-sidebar-steamvr = SteamVR
settings-sidebar-tracker_mechanics = Tracker-Mechanik
settings-sidebar-stay_aligned = Stay Aligned
settings-sidebar-fk_settings = FK-Einstellungen
settings-sidebar-gesture_control = Gestensteuerung
settings-sidebar-interface = Bedienoberfläche
settings-sidebar-osc_router = OSC-Router
settings-sidebar-osc_trackers = VRChat OSC-Tracker
settings-sidebar-osc_vmc = VMC
settings-sidebar-utils = Werkzeuge
settings-sidebar-serial = Serielle Konsole
settings-sidebar-appearance = Erscheinungsbild
settings-sidebar-home = Startbildschirm
settings-sidebar-checklist = Tracking-Checkliste
settings-sidebar-notifications = Benachrichtigungen
settings-sidebar-behavior = Verhalten
settings-sidebar-firmware-tool = DIY Firmware-Tool
settings-sidebar-vrc_warnings = VRChat Konfigurations-Warnungen
settings-sidebar-advanced = Erweitert

## SteamVR settings

settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = SteamVR-Tracker
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    Aktivieren oder deaktivieren Sie bestimmte SteamVR-Tracker.
    Nützlich für Spiele oder Apps, die nur bestimmte Tracker unterstützen.
settings-general-steamvr-trackers-waist = Taille
settings-general-steamvr-trackers-chest = Brust
settings-general-steamvr-trackers-left_foot = Linker Fuß
settings-general-steamvr-trackers-right_foot = Rechter Fuß
settings-general-steamvr-trackers-left_knee = Linkes Knie
settings-general-steamvr-trackers-right_knee = Rechtes Knie
settings-general-steamvr-trackers-left_elbow = Linker Ellenbogen
settings-general-steamvr-trackers-right_elbow = Rechter Ellenbogen
settings-general-steamvr-trackers-left_hand = Linke Hand
settings-general-steamvr-trackers-right_hand = Rechte Hand
settings-general-steamvr-trackers-tracker_toggling = Automatische Tracker Zuweisung
settings-general-steamvr-trackers-tracker_toggling-description = Automatisches Aktivieren und Deaktivieren von SteamVR Trackern, in Abhängigkeit von der aktuellen Tracker-Zuordnung.
settings-general-steamvr-trackers-tracker_toggling-label = Automatische Tracker Zuweisung
settings-general-steamvr-trackers-hands-warning =
    <b>Warnung:</b> Handtracker übersteuern Ihre Controller.
    Sind Sie sich sicher?
settings-general-steamvr-trackers-hands-warning-cancel = Abbrechen
settings-general-steamvr-trackers-hands-warning-done = Ja

## Tracker mechanics

settings-general-tracker_mechanics = Tracker-Verhalten
settings-general-tracker_mechanics-filtering = Filtern
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Wählen Sie den Filter-Typ für Ihre Tracker aus.
    Vorhersage: prognostiziert Bewegung. Glättung: Bewegung werden geglättet.
settings-general-tracker_mechanics-filtering-type = Filter-Typ
settings-general-tracker_mechanics-filtering-type-none = Kein Filter
settings-general-tracker_mechanics-filtering-type-none-description = Verwendet die unveränderten Rotationsdaten der Tracker.
settings-general-tracker_mechanics-filtering-type-smoothing = Glättung
settings-general-tracker_mechanics-filtering-type-smoothing-description = Glättet Bewegungen, fügt aber etwas Verzögerung hinzu.
settings-general-tracker_mechanics-filtering-type-prediction = Vorhersage
settings-general-tracker_mechanics-filtering-type-prediction-description = Verringert die Latenz und macht die Bewegungen schneller, kann aber Zittern erhöhen.
settings-general-tracker_mechanics-filtering-amount = Stärke
settings-general-tracker_mechanics-yaw-reset-smooth-time = Horizontaler Reset Glättungszeit (0s deaktiviert die Glättung)
settings-general-tracker_mechanics-drift_compensation = Drift-Kompensierung
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Kompensiert IMU Drift auf der Gier-Achse durch Anwenden einer invertierten Rotation.
    Ändern Sie die Menge der Kompensierung und die Anzahl der Resets, welche für die Berechnung genutzt werden.
settings-general-tracker_mechanics-drift_compensation-enabled-label = Drift-Kompensierung
settings-general-tracker_mechanics-drift_compensation-prediction = Prognose der Driftkompensation
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-prediction-description =
    Prognostiziert die Driftkompensation basierend auf dem zuvor gemessenen Drift.
    Aktivieren Sie diese Funktion, wenn sich der Tracker kontinuierlich um die gier-Achse dreht.
settings-general-tracker_mechanics-drift_compensation-prediction-label = Prognose der Driftkompensation
settings-general-tracker_mechanics-drift_compensation_warning =
    <b>Warnung:</b> Verwenden Sie die Driftkompensation nur, wenn sie sehr oft
    reseten müssen (alle ~5-10 Minuten).
    
    Zu den IMUs, die häufig einen Reset benötigen, gehören:
    Joy-Cons, owoTrack und MPUs (ohne aktuelle Firmware).
settings-general-tracker_mechanics-drift_compensation_warning-cancel = Abbrechen
settings-general-tracker_mechanics-drift_compensation_warning-done = Ich verstehe
settings-general-tracker_mechanics-drift_compensation-amount-label = Kompensierungsmenge
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Nutze die letzten x Resets
settings-general-tracker_mechanics-save_mounting_reset = Automatische Tracker-Ausrichtung speichern
settings-general-tracker_mechanics-save_mounting_reset-description =
    Speichert die automatische Tracker-Ausrichtung für die Tracker zwischen den Neustarts. Nützlich
    wenn Sie einen Anzug tragen, bei dem sich die Tracker zwischen den Sitzungen nicht bewegen. <b>Für normale Benutzer nicht zu empfehlen!</b>
settings-general-tracker_mechanics-save_mounting_reset-enabled-label = Tracker-Ausrichtung speichern
settings-general-tracker_mechanics-use_mag_on_all_trackers = Verwende das Magnetometer auf allen IMU-Trackern, die dies unterstützen.
settings-general-tracker_mechanics-use_mag_on_all_trackers-description =
    Verwendet das Magnetometer auf allen Trackern, die über eine kompatible Firmware verfügen, um den Drift in stabilen magnetischen Umgebungen zu reduzieren.
    Kann pro Tracker in den Einstellungen des Trackers deaktiviert werden. <b>Bitte schalten Sie keinen der Tracker aus, während Sie dies umschalten!</b>
settings-general-tracker_mechanics-use_mag_on_all_trackers-label = Magnetometer auf Trackern verwenden
settings-stay_aligned = Stay Aligned
settings-stay_aligned-description = Stay Aligned reduziert Drift, indem es deine Tracker schrittweise an deine entspannten Posen anpasst.
settings-stay_aligned-setup-label = Stay Aligned einrichten
settings-stay_aligned-setup-description = Sie müssen Stay Aligned einrichten, um es zu aktivieren.
settings-stay_aligned-warnings-drift_compensation = ⚠ Bitte schalten Sie die Driftkompensation aus! Diese steht in Konflikt mit Stay Aligned.
settings-stay_aligned-enabled-label = Tracker anpassen
settings-stay_aligned-general-label = Allgemein
settings-stay_aligned-relaxed_poses-label = Entspannte Posen
settings-stay_aligned-relaxed_poses-save_pose = Pose speichern
settings-stay_aligned-relaxed_poses-reset_pose = Pose zurücksetzen
settings-stay_aligned-relaxed_poses-close = Schließen
settings-stay_aligned-debug-label = Debuggen
settings-stay_aligned-debug-description = Bitte geben Sie Ihre Einstellungen mit an, wenn Sie Probleme mit Stay Aligned melden.
settings-stay_aligned-debug-copy-label = Einstellungen in die Zwischenablage kopieren

## FK/Tracking settings

settings-general-fk_settings = FK-Einstellungen
# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Bodenclip
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Rutschkorrektur
settings-general-fk_settings-leg_tweak-toe_snap = Zehenausrichtung
settings-general-fk_settings-leg_tweak-foot_plant = Fußkorrektur
settings-general-fk_settings-leg_tweak-skating_correction-amount = Rutschkorrekturstärke
settings-general-fk_settings-leg_tweak-skating_correction-description = Die Rutschkorrektur korrigiert das Wegrutschen des Fußes, kann aber die Genauigkeit bestimmter Bewegungsmuster verringern. Wenn Sie dies aktivieren, stellen Sie sicher, dass Sie im Spiel Ihr Tracking vollständig zurücksetzten und neu kalibrieren.
settings-general-fk_settings-leg_tweak-floor_clip-description = Bodenclip kann das Clipping durch den Boden reduzieren oder sogar eliminieren. Wenn Sie dies aktivieren, stellen Sie sicher, dass Sie im Spiel Ihr Tracking vollständig zurücksetzten und neu kalibrieren.
settings-general-fk_settings-leg_tweak-toe_snap-description = Zehen-Ausrichtung versucht, die Rotation Ihrer Füße zu erraten, wenn keine Fuß-Tracker verwendet werden.
settings-general-fk_settings-leg_tweak-foot_plant-description = Fußkorrektur richtet Ihre Füße parallel zum Boden aus, wenn sie den Boden berühren.
settings-general-fk_settings-leg_fk = Beintracking
settings-general-fk_settings-enforce_joint_constraints = Gelenkgrenzen
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints = Grenzen erzwingen
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = Verhindert, dass sich Gelenke über ihre Grenzen hinaus drehen
settings-general-fk_settings-enforce_joint_constraints-correct_constraints = Mit Grenzen korrigieren
settings-general-fk_settings-enforce_joint_constraints-correct_constraints-description = Korrigiert Gelenkrotationen, wenn diese über ihre Grenzen hinausgehen
settings-general-fk_settings-ik = Positionsdaten
settings-general-fk_settings-ik-use_position = Positionsdaten verwenden
settings-general-fk_settings-arm_fk = Arm-Tracking
settings-general-fk_settings-arm_fk-description = Ändern Sie die Art und Weise, wie die Arme berechnet werden.
settings-general-fk_settings-arm_fk-force_arms = Arme vom VR-Headset erzwingen
settings-general-fk_settings-reset_settings = Einstellungen zurücksetzen
settings-general-fk_settings-reset_settings-reset_hmd_pitch-description = Setzen Sie die Neigung (vertikale Drehung) Ihres Headsets zurück, wenn Sie einen vollständigen Reset durchführen. Nützlich, wenn Sie ein Headset auf der Stirn für VTubing oder Mocap tragen. Nicht für VR aktivieren.
settings-general-fk_settings-reset_settings-reset_hmd_pitch = Headset-Nick (vertikale Drehung) zurücksetzen
settings-general-fk_settings-arm_fk-reset_mode-description = Ändern Sie, welche Armhaltung für den Reset der Tracker-Ausrichtung erwartet wird.
settings-general-fk_settings-arm_fk-back = nach Hinten
settings-general-fk_settings-arm_fk-back-description = Der Standardmodus, bei dem die Oberarme nach hinten und die Unterarme nach vorne gehen.
settings-general-fk_settings-arm_fk-tpose_up = T-Pose (oben)
settings-general-fk_settings-arm_fk-tpose_up-description = Erwartet, dass deine Arme während des vollständigen Zurücksetzens seitlich nach unten gerichtet sind und während des Reset der Tracker-Ausrichtung um 90 Grad nach außen gerichtet sind.
settings-general-fk_settings-arm_fk-tpose_down = T-Pose (unten)
settings-general-fk_settings-arm_fk-tpose_down-description = Erwartet, dass deine Arme während des vollständigen Zurücksetzens um 90 Grad nach außen gerichtet sind und während des Befestigungs-Reset seitlich nach unten.
settings-general-fk_settings-arm_fk-forward = Vorwärts
settings-general-fk_settings-arm_fk-forward-description = Erwartet, dass deine Arme um 90 Grad nach vorne gerichtet sind. Nützlich für VTubing.
settings-general-fk_settings-skeleton_settings-toggles = Skelett-Schalter
settings-general-fk_settings-skeleton_settings-description = Schalten Sie Skeletteinstellungen ein oder aus. Es wird empfohlen, diese eingeschaltet zu lassen.
settings-general-fk_settings-skeleton_settings-extended_spine_model = Erweitertes Wirbelsäulen-Modell
settings-general-fk_settings-skeleton_settings-extended_pelvis_model = Erweitertes Pelvis-Modell
settings-general-fk_settings-skeleton_settings-extended_knees_model = Erweitertes Knie-Modell
settings-general-fk_settings-skeleton_settings-ratios = Skelettverhältnisse
settings-general-fk_settings-skeleton_settings-ratios-description = Ändert die Werte der Skeletteinstellungen. Nachdem Sie diese geändert haben, müssen Sie möglicherweise Ihre Proportionen anpassen.
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_hip = Taille aus Brust zu Hüfte berechnen
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_legs = Taille von Brust zu Beine berechnen
settings-general-fk_settings-skeleton_settings-impute_hip_from_chest_legs = Hüfte von Brust zu Beine berechnen
settings-general-fk_settings-skeleton_settings-impute_hip_from_waist_legs = Hüfte von Taille zu Beine berechnen
settings-general-fk_settings-skeleton_settings-interp_hip_legs = Interpolieren der horizontalen und Torsionsrotation der Hüfte mit denen der Beine
settings-general-fk_settings-skeleton_settings-interp_knee_tracker_ankle = Interpolation der horizontalen und Torsionsrotationen der Knietracker mit denen der Fußgelenke
settings-general-fk_settings-skeleton_settings-interp_knee_ankle = Interpolation der horizontalen und Torsionsrotationen der Knie mit denen der Fußgelenke
settings-general-fk_settings-self_localization-title = Motion-Capture-Modus
settings-general-fk_settings-self_localization-description = Der Motion-Capture-Modus ermöglicht es dem Skelett, ungefähr die eigene Position ohne Headset oder Tracker zu verfolgen. Beachten Sie, dass diese Funktion Fuß- und Kopf-Tracker benötigt und noch experimentell ist.

## Gesture control settings (tracker tapping)

settings-general-gesture_control = Gestensteuerung
settings-general-gesture_control-subtitle = Reset durch Antippen
settings-general-gesture_control-description = Erlaubt Reset durch das Antippen eines Trackers auszulösen. Der höchste Tracker auf dem Oberkörper wird für schnelle Resets genutzt, der höchste Tracker auf dem linken Bein wird für Reset genutzt und der höchste Tracker auf dem rechten Bein wird für Reset der Tracker-Ausrichtung genutzt. Das Antippen muss innerhalb von 0.5 Sekunden erfolgen, um erkannt zu werden.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
        [one] 1-mal antippen
       *[other] { $amount }-mal antippen
    }
# This is a unit: 3 trackers, 2 trackers, 1 tracker
# $amount (Number) - Amount of trackers
settings-general-gesture_control-trackers =
    { $amount ->
        [one] 1 Tracker
       *[other] { $amount } Tracker
    }
settings-general-gesture_control-yawResetEnabled = Horizontaler Reset durch Antippen
settings-general-gesture_control-yawResetDelay = Verzögerung für einen horizontalen Reset
settings-general-gesture_control-yawResetTaps = Antipp-Anzahl für einen horizontalen Reset
settings-general-gesture_control-fullResetEnabled = Vollständiger Reset durch Antippen
settings-general-gesture_control-fullResetDelay = Verzögerung für einen vollständigen Reset
settings-general-gesture_control-fullResetTaps = Antipp-Anzahl für einen vollständigen Reset
settings-general-gesture_control-mountingResetEnabled = Aktivieren von Antippen für Reset der Tracker-Ausrichtung
settings-general-gesture_control-mountingResetDelay = Verzögerung von Reset der Tracker-Ausrichtung
settings-general-gesture_control-mountingResetTaps = Anzahl für Befestigungs-Reset
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = Tracker über Schwellwert
settings-general-gesture_control-numberTrackersOverThreshold-description = Erhöhen Sie diesen Wert wenn Tipp-Erkennung nicht funktioniert. Setzen Sie den Wert nicht höher als benötigt, da dies Fehlauslöser verursachen kann.

## Appearance settings

settings-interface-appearance = Erscheinungsbild
settings-general-interface-dev_mode = Entwicklermodus
settings-general-interface-dev_mode-description = Der Entwicklermodus stellt mehr Daten dar und erlaubt auch erweiterte Einstellungen, so wie erweiterte Optionen bei verbundenen Trackern.
settings-general-interface-dev_mode-label = Entwicklermodus
settings-general-interface-theme = Farbschema
settings-general-interface-show-navbar-onboarding = "{ navbar-onboarding }" in der Navigationsleiste anzeigen
settings-general-interface-show-navbar-onboarding-description = Dies ändert die Sichtbarkeit der Schaltfläche "{ navbar-onboarding }" in der Navigationsleiste
settings-general-interface-show-navbar-onboarding-label = Zeige "{ navbar-onboarding }"
settings-general-interface-lang = Sprachauswahl
settings-general-interface-lang-description = Ändern Sie die Standard-Sprache, die Sie verwenden möchten
settings-general-interface-lang-placeholder = Wählen Sie die zu verwendende Sprache aus
# Keep the font name untranslated
settings-interface-appearance-font = GUI-Schriftart
settings-interface-appearance-font-description = Verändert die Schriftart der Benutzeroberfläche.
settings-interface-appearance-font-placeholder = Standard-Schriftart
settings-interface-appearance-font-os_font = Betriebssystem-Schriftart
settings-interface-appearance-font-slime_font = Standard-Schriftart
settings-interface-appearance-font_size = Standard-Schriftgröße
settings-interface-appearance-font_size-description = Verändert die Schriftgröße der gesamten Oberfläche außer diesem Einstellungs-Panel.
settings-interface-appearance-decorations = Verwenden Sie die systemeigenen Fensterdekorationen
settings-interface-appearance-decorations-description = Dadurch wird die obere Leiste der Benutzeroberfläche nicht gerendert, sondern die des Betriebssystems verwendet.
settings-interface-appearance-decorations-label = Verwenden der native Fensterdekorationen

## Notification settings

settings-interface-notifications = Benachrichtigungen
settings-general-interface-serial_detection = Serielle Geräteerkennung
settings-general-interface-serial_detection-description = Diese Option zeigt jedes Mal ein Pop-up-Fenster an, wenn ein neues serielles Gerät angeschlossen wird, das ein Tracker sein könnte. Dies hilft beim Einrichtungsprozess des Trackers
settings-general-interface-serial_detection-label = Serielle Geräteerkennung
settings-general-interface-feedback_sound = Feedback-Geräusch
settings-general-interface-feedback_sound-description = Diese Option wird ein Geräusch abspielen, wenn ein Reset ausgeführt wurde.
settings-general-interface-feedback_sound-label = Feedback-Geräusch
settings-general-interface-feedback_sound-volume = Feedback-Sound-Lautstärke
settings-general-interface-connected_trackers_warning = Warnung zu verbundenen Trackern
settings-general-interface-connected_trackers_warning-description = Diese Option zeigt jedes Mal ein Pop-up-Fenster an, wenn Sie versuchen, SlimeVR zu beenden, während ein oder mehrere Tracker verbunden sind. Es erinnert Sie daran, die Tracker auszuschalten, um die Akkulaufzeit zu verlängern.
settings-general-interface-connected_trackers_warning-label = Warnung vor verbundenen Trackern beim Verlassen

## Behavior settings

settings-interface-behavior = Verhalten
settings-general-interface-use_tray = In den Infobereich minimieren
settings-general-interface-use_tray-description = Erlaubt Ihnen, das Fenster zu schließen, ohne den SlimeVR-Server zu beenden. Dies erlaubt Ihnen diesen weiterzuverwenden, ohne dass das Fenster stört.
settings-general-interface-use_tray-label = In den Infobereich minimieren
settings-general-interface-discord_presence = Aktivität auf Discord teilen
settings-general-interface-discord_presence-description = Teilt Ihrem Discord-Client mit, dass Sie SlimeVR verwenden, zusammen mit der Anzahl der IMU-Tracker, die Sie benutzen.
settings-general-interface-discord_presence-label = Aktivität auf Discord teilen
settings-general-interface-discord_presence-message =
    { $amount ->
        [0] Sliming around
        [one] nutzt 1 Tracker
       *[other] nutzt { $amount } Tracker
    }
settings-interface-behavior-error_tracking = Fehlererfassung über Sentry.io
settings-interface-behavior-error_tracking-description_v2 =
    <h1>Sind Sie mit der Erhebung anonymisierter Fehlerdaten einverstanden?</h1>
    
    <b>Wir erfassen keine personenbezogenen Daten,</b> wie Ihre IP-Adresse oder WLAN-Zugangsdaten. SlimeVR respektiert Ihre Privatsphäre!
    
    Um die bestmögliche Benutzererfahrung zu bieten, erfassen wir anonymisierte Fehlerberichte, Leistungsmetriken und Informationen zum Betriebssystem. Dies hilft uns, Fehler und Probleme mit SlimeVR zu erkennen. Diese Metriken werden über Sentry.io erfasst.
settings-interface-behavior-error_tracking-label = Fehler an Entwickler senden
settings-interface-behavior-bvh_directory = Verzeichnis zum Speichern von BVH-Aufnahmen
settings-interface-behavior-bvh_directory-label = Verzeichnis für BVH-Aufnahmen

## Serial settings

settings-serial = Serielle Konsole
# This cares about multilines
settings-serial-description =
    Dies ist ein Live-Ansicht der seriellen Kommunikation.
    Diese ist zur Unterstützung bei der Problemsuche mit Trackern.
settings-serial-connection_lost = Verbindung zur seriellen Schnittstelle verloren, Verbindung wird wiederhergestellt...
settings-serial-reboot = Neustart
settings-serial-factory_reset = Werkseinstellungen zurücksetzen
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>Warnung:</b> Dadurch wird der Tracker auf die Werkseinstellungen zurückgesetzt.
    Das bedeutet, dass die WLAN- und Kalibrierungseinstellungen <b>verloren gehen!</b>
settings-serial-factory_reset-warning-ok = Ich weiß, was ich tue
settings-serial-factory_reset-warning-cancel = Abbruch
settings-serial-serial_select = Wählen Sie einen seriellen Anschluss
settings-serial-auto_dropdown_item = Auto
settings-serial-get_wifi_scan = WLAN-Scan
settings-serial-file_type = Klartext
settings-serial-save_logs = In Datei speichern
settings-serial-send_command = Senden
settings-serial-send_command-placeholder = Befehl...
settings-serial-send_command-warning = <b>Warnung:</b> Das Ausführen serieller Befehle kann zu Datenverlust führen oder die Tracker unbrauchbar machen.
settings-serial-send_command-warning-ok = Ich weiß, was ich tue
settings-serial-send_command-warning-cancel = Abbruch

## OSC router settings

settings-osc-router = OSC Router
# This cares about multilines
settings-osc-router-description =
    Senden von OSC Daten an ein andere Programm.
    Nützlich wenn z.B. ein anderes OSC Programm zusammen mit VRChat verwendet wird.
settings-osc-router-enable = Aktivieren
settings-osc-router-enable-description = Ein- und Ausschalten des Sendens und Empfangen von Daten
settings-osc-router-enable-label = Aktivieren
settings-osc-router-network = Netzwerk-Ports
# This cares about multilines
settings-osc-router-network-description =
    Setzte die Ports zum Empfangen und Versenden von Daten.
    Diese können die selben Ports sein, welche vom SlimeVR Server verwendet werden.
settings-osc-router-network-port_in =
    .label = Eingangsport
    .placeholder = Eingangsport (Standard: 9002)
settings-osc-router-network-port_out =
    .label = Ausgangsport
    .placeholder = Ausgangsport (Standard: 9000)
settings-osc-router-network-address = Netzwerk-Adresse
settings-osc-router-network-address-description = Setze die Adresse, welche zum versenden von Daten genutzt wird.
settings-osc-router-network-address-placeholder = IPv4 Adresse

## OSC VRChat settings

settings-osc-vrchat = VRChat-OSC-Trackers
# This cares about multilines
settings-osc-vrchat-description-v1 =
    Ändern Sie die Einstellungen, die speziell für den OSC-Trackers-Standard verwendet werden, um Tracking-Daten an Anwendungen ohne SteamVR zu senden (z. B. für Quest Standalone).
    Stellen Sie sicher, dass Sie OSC in VRChat über das Aktionsmenü unter OSC > Aktiviert einschalten.
    Um das Empfangen von HMD- und Controller-Daten von VRChat zu ermöglichen, gehen Sie in Ihrem Hauptmenü
    zu den Einstellungen unter Tracking & IK > Erlaube das Senden von Kopf- und Handgelenk-VR-Tracking-OSC-Daten.
settings-osc-vrchat-enable = Aktivieren
settings-osc-vrchat-enable-description = Ein- und Ausschalten des Sendens und Empfangen von Daten
settings-osc-vrchat-enable-label = Aktivieren
settings-osc-vrchat-oscqueryEnabled = OSCQuery aktivieren
settings-osc-vrchat-oscqueryEnabled-description =
    OSCQuery erkennt laufende Instanzen von VRChat automatisch und sendet Daten an sie.
    Es kann sich auch selbst bei diesen Instanzen bekannt machen, um HMD- und Controller-Daten zu empfangen.
    Um den Empfang von HMD- und Controller-Daten aus VRChat zu ermöglichen, öffnen Sie das Hauptmenü,
    gehen Sie zu den Einstellungen unter "Tracking & IK" und aktivieren Sie "Erlaube das Senden von Kopf- und Handgelenk-VR-Tracking-OSC-Daten".
settings-osc-vrchat-oscqueryEnabled-label = OSCQuery aktivieren
settings-osc-vrchat-network = Netzwerk-Ports
settings-osc-vrchat-network-description-v1 = Legt die Ports für das Empfangen und Senden von Daten fest. Kann für VRChat unverändert bleiben.
settings-osc-vrchat-network-port_in =
    .label = Eingangsport
    .placeholder = Eingangsport (Standard: 9001)
settings-osc-vrchat-network-port_out =
    .label = Ausgangsport
    .placeholder = Ausgangsport (Standard: 9000)
settings-osc-vrchat-network-address = Netzwerkadresse
settings-osc-vrchat-network-address-description-v1 = Wählen Sie die IP-Adresse, an die die Daten gesendet werden sollen. Kann für VRChat unverändert bleiben.
settings-osc-vrchat-network-address-placeholder = VRChat-IP-Adresse
settings-osc-vrchat-network-trackers = Tracker
settings-osc-vrchat-network-trackers-description = Ein- und Ausschalten des Sendens und Empfangens von Daten
settings-osc-vrchat-network-trackers-chest = Brust
settings-osc-vrchat-network-trackers-hip = Hüfte
settings-osc-vrchat-network-trackers-knees = Knie
settings-osc-vrchat-network-trackers-feet = Füße
settings-osc-vrchat-network-trackers-elbows = Ellbogen

## VMC OSC settings

settings-osc-vmc = Virtual Motion Capture
# This cares about multilines
settings-osc-vmc-description = Einstellungsänderungen spezifisch für das VMC-Protokoll (Virtual Motion Capture Protocol), um die Skelett-Daten von SlimeVR zu senden und Skelett-Daten von anderen Applikationen zu empfangen.
settings-osc-vmc-enable = Aktivieren
settings-osc-vmc-enable-description = Ein- und Ausschalten des Sendens und Empfangen von Daten.
settings-osc-vmc-enable-label = Aktivieren
settings-osc-vmc-network = Netzwerk-Ports
settings-osc-vmc-network-description = Port Einstellungen zum Empfangen und Senden von Daten über VMC.
settings-osc-vmc-network-port_in =
    .label = Eingehender Port
    .placeholder = Eingehender Port (default: 39540)
settings-osc-vmc-network-port_out =
    .label = Ausgehender Port
    .placeholder = Ausgehender Port (default: 39539)
settings-osc-vmc-network-address = Netzwerkadresse
settings-osc-vmc-network-address-description = Setze die Adresse, wo die Daten hinversendet werden sollen.
settings-osc-vmc-network-address-placeholder = IPv4-Adresse
settings-osc-vmc-vrm = VRM-Model
settings-osc-vmc-vrm-description = Lade ein VRM-Modell um die Kopfverankerung anzuschalten und eine bessere Kompatibilität mit anderen Anwendungen zu bekommen.
settings-osc-vmc-vrm-untitled_model = Unbenanntes Modell
settings-osc-vmc-vrm-file_select = Modell per Drag & Drop laden oder <u>durchsuchen</u>
settings-osc-vmc-anchor_hip = Hüftenverankerung
settings-osc-vmc-anchor_hip-description = Die Hüften-Verankerung für das Tracking ist nützlich für VTubing im Sitzen. Beim Deaktivieren muss ein VRM-Model geladen werden.
settings-osc-vmc-anchor_hip-label = Hüftenverankerung
settings-osc-vmc-mirror_tracking = Tracking spiegeln
settings-osc-vmc-mirror_tracking-description = Tracking horizontal spiegeln
settings-osc-vmc-mirror_tracking-label = Tracking spiegeln

## Common OSC settings

settings-osc-common-network-ports_match_error = Die Ein- und Ausgänge des OSC-Routers können nicht gleich sein!
settings-osc-common-network-port_banned_error = Der Port { $port } kann nicht verwendet werden!

## Advanced settings

settings-utils-advanced = Erweitert
settings-utils-advanced-reset-gui = Einstellungen der Benutzeroberfläche zurücksetzen
settings-utils-advanced-reset-gui-description = Stellt die Standardeinstellungen für die Benutzeroberfläche wieder her.
settings-utils-advanced-reset-gui-label = Benutzeroberfläche zurücksetzen
settings-utils-advanced-reset-server = Tracking-Einstellungen zurücksetzen
settings-utils-advanced-reset-server-description = Stellen Sie die Standardeinstellungen für das Tracking wieder her.
settings-utils-advanced-reset-server-label = Tracking zurücksetzen
settings-utils-advanced-reset-all = Alle Einstellungen zurücksetzen
settings-utils-advanced-reset-all-description = Stellt die Standardeinstellungen für die Benutzeroberfläche und das Tracking wieder her.
settings-utils-advanced-reset-all-label = Alles zurücksetzen
settings-utils-advanced-reset_warning =
    { $type ->
        [gui]
            <b>Warnung:</b> Dadurch werden Ihre Benutzeroberfläche-Einstellungen auf die Standardeinstellungen zurückgesetzt.
            Möchten Sie das wirklich tun?
        [server]
            <b>Warnung:</b> Dadurch werden Ihre Tracking-Einstellungen auf die Standardeinstellungen zurückgesetzt.
            Möchten Sie das wirklich tun?
       *[all]
            <b>Warnung:</b> Dadurch werden alle Ihre Einstellungen auf die Standardeinstellungen zurückgesetzt.
            Möchten Sie das wirklich tun?
    }
settings-utils-advanced-reset_warning-reset = Einstellungen zurücksetzen
settings-utils-advanced-reset_warning-cancel = Abbrechen
settings-utils-advanced-open_data-v1 = Konfigurationsordner
settings-utils-advanced-open_data-description-v1 = Öffnet den Konfigurationsordner von SlimeVR im Explorer, der Konfigurationsdateien enthält
settings-utils-advanced-open_data-label = Ordner öffnen
settings-utils-advanced-open_logs = Logs-Ordner
settings-utils-advanced-open_logs-description = Öffnet den Logs-Ordner von SlimeVR im Explorer, der die Protokolle der App enthält.
settings-utils-advanced-open_logs-label = Ordner öffnen

## Home Screen

settings-home-list-layout = Layout der Tracker-Liste
settings-home-list-layout-desc = Wählen Sie eines der möglichen Startbildschirm-Layouts aus
settings-home-list-layout-table = Tabelle

## Tracking Checlist


## Setup/onboarding menu

onboarding-skip = Einrichtung überspringen
onboarding-continue = Fortsetzen
onboarding-wip = Noch in Bearbeitung
onboarding-previous_step = Vorheriger Schritt
onboarding-setup_warning = <b>Warnung:</b> Für gutes Tracking ist die Ersteinrichtung erforderlich! Bei der ersten Benutzung dies benötigt.
onboarding-setup_warning-skip = Einrichtung überspringen
onboarding-setup_warning-cancel = Einrichtung fortsetzen

## Wi-Fi setup

onboarding-wifi_creds-back = Zurück zur Einführung
onboarding-wifi_creds-skip = WLAN-Zugangsdaten überspringen
onboarding-wifi_creds-submit = Weiter!
onboarding-wifi_creds-ssid =
    .label = WLAN-Name
    .placeholder = WLAN-Name eingeben
onboarding-wifi_creds-ssid-required = WLAN-Name ist erforderlich
onboarding-wifi_creds-password =
    .label = Passwort
    .placeholder = Passwort eingeben

## Mounting setup

onboarding-reset_tutorial-back = Zurück zur Tracker-Ausrichtung
onboarding-reset_tutorial = Tutorial neustarten
onboarding-reset_tutorial-explanation = Während Sie Ihre Tracker verwenden, können sie aufgrund der IMU-Gierdrift oder weil Sie sie physisch bewegt haben, aus der Ausrichtung geraten. Sie haben mehrere Möglichkeiten, dies zu beheben.
onboarding-reset_tutorial-skip = Schritt überspringen
# Cares about multiline
onboarding-reset_tutorial-0 =
    Tippen Sie { $taps } mal auf den markierten Tracker, um den horizontalen Reset auszulösen.
    
    Dadurch zeigen die Tracker in die gleiche Richtung wie Ihre VR-Brille.
# Cares about multiline
onboarding-reset_tutorial-1 =
    Tippen Sie { $taps } mal auf den markierten Tracker, um einen vollständigen Reset auszulösen.
    
    Sie müssen dafür in einer I-Pose stehen. Es gibt eine Verzögerung von 3 Sekunden (konfigurierbar), bevor der Reset tatsächlich durchgeführt wird.
    Dadurch werden die Position und Rotation aller Ihrer Tracker vollständig zurückgesetzt. Dies sollte die meisten Probleme beheben.
# Cares about multiline
onboarding-reset_tutorial-2 =
    Tippen Sie { $taps } mal auf den markierten Tracker um einen Reset der Tracker-Ausrichtung auszulösen.
    
    Ein Reset der Tracker-Ausrichtung hilft dabei, die Tracker neu auszurichten, so wie diese tatsächlich an Ihnen angebracht sind. Zum Beispiel, wenn Sie ein Tracker versehentlich verschoben haben und dessen Orientierung sich stark verändert hat.
    
    Sie müssen sich in einer "Skifahren"-Pose, wie im Tracker-Ausrichtung-Assistenten gezeigt wird, befinden. Nach dem Auslösen wird der Reset nach 3 Sekunden (konfigurierbar) durchgeführt.

## Setup start

onboarding-home = Willkommen zu SlimeVR
onboarding-home-start = Los geht’s!

## Setup done

onboarding-done-title = Alles eingerichtet!
onboarding-done-description = Genießen Sie die Fullbody-Erfahrung
onboarding-done-close = Einrichtung schließen

## Tracker connection setup

onboarding-connect_tracker-back = Zurück zu WLAN-Zugangsdaten
onboarding-connect_tracker-title = Verbinde Tracker
onboarding-connect_tracker-description-p0-v1 = Kommen wir nun zum spaßigen Teil, dem Verbinden von Trackern!
onboarding-connect_tracker-description-p1-v1 = Schließen Sie jeden Tracker nacheinander und einzeln über einen USB-Anschluss an.
onboarding-connect_tracker-issue-serial = Ich habe Schwierigkeiten die Tracker zu verbinden!
onboarding-connect_tracker-usb = USB Tracker
onboarding-connect_tracker-connection_status-none = Suche nach Trackern
onboarding-connect_tracker-connection_status-serial_init = Verbindung zum seriellen Gerät wird hergestellt
onboarding-connect_tracker-connection_status-obtaining_mac_address = Ermittle die MAC-Adresse des Trackers
onboarding-connect_tracker-connection_status-provisioning = Sende WLAN-Zugangsdaten
onboarding-connect_tracker-connection_status-connecting = Sende WLAN-Zugangsdaten
onboarding-connect_tracker-connection_status-looking_for_server = Suche nach Server
onboarding-connect_tracker-connection_status-connection_error = Es kann keine WLAN-Verbindung hergestellt werden
onboarding-connect_tracker-connection_status-could_not_find_server = Server konnte nicht gefunden werden
onboarding-connect_tracker-connection_status-done = Verbindung zum Server hergestellt.
onboarding-connect_tracker-connection_status-no_serial_log = Konnte keine Logs vom Tracker abrufen
onboarding-connect_tracker-connection_status-no_serial_device_found = Konnte keinen Tracker über USB finden
onboarding-connect_serial-error-modal-no_serial_log = Ist der Tracker eingeschaltet?
onboarding-connect_serial-error-modal-no_serial_log-desc = Stellen Sie sicher, dass der Tracker eingeschaltet und mit Ihrem Computer verbunden ist.
onboarding-connect_serial-error-modal-no_serial_device_found = Keine Tracker erkannt
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers =
    { $amount ->
        [0] Kein Tracker verbunden
        [one] 1 Tracker verbunden
       *[other] { $amount } Tracker verbunden
    }
onboarding-connect_tracker-next = Ich habe alle meine Tracker verbunden.

## Tracker calibration tutorial

onboarding-calibration_tutorial = IMU-Kalibrierungs-Tutorial
onboarding-calibration_tutorial-subtitle = Dies wird dazu beitragen, das Driften der Tracker zu reduzieren!
onboarding-calibration_tutorial-calibrate = Ich habe meine Tracker auf den Tisch gelegt
onboarding-calibration_tutorial-status-waiting = Wir warten auf Sie
onboarding-calibration_tutorial-status-calibrating = Kalibriere
onboarding-calibration_tutorial-status-success = Gut!
onboarding-calibration_tutorial-status-error = Der Tracker wurde bewegt
onboarding-calibration_tutorial-skip = Tutorial überspringen

## Tracker assignment tutorial

onboarding-assignment_tutorial = So bereiten Sie einen SlimeVR-Tracker vor, bevor Sie diesen anlegen
onboarding-assignment_tutorial-first_step = 1. Platzieren Sie einen Körperteilaufkleber (falls vorhanden) auf dem Tracker Ihrer Wahl
# This text has a character limit of around 11 characters, so please keep it short
onboarding-assignment_tutorial-sticker = Aufkleber
onboarding-assignment_tutorial-second_step-v2 = 2. Befestigen Sie den Riemen an ihrem Tracker, wobei die Klettseite des Riemens in dieselbe Richtung zeigt wie das SlimeVR Logo Ihres Trackers:
onboarding-assignment_tutorial-second_step-continuation-v2 = Die Klettseite für den Erweiterungstracker sollte nach oben zeigen, wie in der folgenden Abbildung:
onboarding-assignment_tutorial-done = Ich habe Aufkleber und Bänder angebracht!

## Tracker assignment setup

onboarding-assign_trackers-back = Zurück zu den WLAN-Zugangsdaten
onboarding-assign_trackers-title = Tracker zuweisen
onboarding-assign_trackers-description = Wählen Sie nun aus, welcher Tracker wo befestigt ist. Klicken Sie auf einen Ort, an dem der Tracker platziert ist.
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned =
    { $trackers ->
        [one] { $assigned } von 1 Tracker zugewiesen
       *[other] { $assigned } von { $trackers } Tracker zugewiesen
    }
onboarding-assign_trackers-advanced = Erweiterte Zuweisungspositionen anzeigen
onboarding-assign_trackers-next = Ich habe alle Tracker zugewiesen
onboarding-assign_trackers-mirror_view = Ansicht spiegeln
onboarding-assign_trackers-option-amount =
    { $trackersCount ->
        [one] x{ $trackersCount }
       *[other] x{ $trackersCount }
    }
onboarding-assign_trackers-option-label =
    { $mode ->
        [lower-body] Lower-Body Set
        [core] Core Set
        [enhanced-core] Enhanced Core Set
        [full-body] Full-Body Set
       *[all] Alle Tracker
    }
onboarding-assign_trackers-option-description =
    { $mode ->
        [lower-body] Minimum für VR Full-Body Tracking
        [core] + Erweitertes Rücken-Tracking
        [enhanced-core] + Fuß-Rotation
        [full-body] + Ellbogen-Tracking
       *[all] Alle verfügbaren Tracker-Zuweisungen
    }

## Tracker assignment warnings

# Note for devs, number is used for representing boolean states per bit.
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_FOOT =
    { $unassigned ->
        [6] Der linke Fuß ist zugewiesen, aber der linke Unterschenkel muss ebenfalls zugewiesen sein!
        [5] Der linke Fuß ist zugewiesen, aber der linke Oberschenkel muss ebenfalls zugewiesen sein!
        [4] Der linke Fuß ist zugewiesen, aber der linke Unter- und Oberschenkel müssen ebenfalls zugewiesen sein!
        [3] Der linke Fuß ist zugewiesen, aber die Brust, oder Taille muss ebenfalls zugewiesen sein!
        [2] Der linke Fuß ist zugewiesen, aber der linke Unterschenkel und die Brust, Hüfte oder Taille müssen ebenfalls zugewiesen sein!
        [1] Der linke Fuß ist zugewiesen, aber der linke Oberschenkel und die Brust, die Hüfte oder die Taille müssen ebenfalls zugewiesen sein!
        [0] Der linke Fuß ist zugewiesen, aber der linke Unter- und Oberschenkel und die Brust, die Hüfte oder die Taille müssen ebenfalls zugewiesen sein!
       *[other] Der linke Fuß ist zugewiesen, aber "Unbekanntes nicht zugewiesenes Körperteil" muss ebenfalls zugewiesen sein!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_FOOT =
    { $unassigned ->
        [0] Der rechte Fuß ist zugewiesen, aber der rechte Unter- und Oberschenkel und entweder die Brust, die Hüfte oder die Taille müssen ebenfalls zugewiesen werden!
        [1] Der rechte Fuß ist zugewiesen, aber der rechte Oberschenkel und entweder die Brust, die Hüfte oder die Taille müssen ebenfalls zugewiesen werden!
        [2] Der rechte Fuß ist zugewiesen, aber der rechte Unterschenkel und entweder die Brust, die Hüfte oder die Taille müssen ebenfalls zugewiesen werden!
        [3] Der rechte Fuß ist zugewiesen, aber entweder die Brust, die Hüfte oder die Taille muss ebenfalls zugewiesen werden!
        [4] Der rechte Fuß ist zugewiesen, aber der rechte Unter- und Oberschenkel müssen ebenfalls zugewiesen werden!
        [5] Der rechte Fuß ist zugewiesen, aber der rechte Oberschenkel muss ebenfalls zugewiesen werden!
        [6] Der rechte Fuß ist zugewiesen, aber der rechte Unterschenkel muss ebenfalls zugewiesen werden!
       *[other] Der rechte Fuß ist zugewiesen, aber "Unbekanntes nicht zugewiesenes Körperteil" muss ebenfalls zugewiesen werden!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_LOWER_LEG =
    { $unassigned ->
        [0] Der linke Unterschenkel ist zugewiesen, aber der linke Oberschenkel und entweder die Brust, die Hüfte oder die Taille muss ebenfalls zugewiesen werden!
        [1] Der linke Unterschenkel ist zugewiesen, aber entweder die Brust, die Hüfte oder die Taille muss ebenfalls zugewiesen werden!
        [2] Der linke Unterschenkel ist zugewiesen, aber der linke Oberschenkel muss ebenfalls zugewiesen werden!
       *[other] Der linke Unterschenkel ist zugewiesen, aber "Unbekanntes nicht zugewiesenes Körperteil" muss ebenfalls zugewiesen werden!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_LOWER_LEG =
    { $unassigned ->
        [0] Der rechte Unterschenkel ist zugewiesen, aber der rechte Oberschenkel und entweder die Brust, die Hüfte oder die Taille muss ebenfalls zugewiesen werden!
        [1] Der rechte Unterschenkel ist zugewiesen, aber entweder die Brust, die Hüfte oder die Taille muss ebenfalls zugewiesen werden!
        [2] Der rechte Unterschenkel ist zugewiesen, aber der rechte Oberschenkel muss ebenfalls zugewiesen werden!
       *[other] Der rechte Unterschenkel ist zugewiesen, aber "Unbekanntes nicht zugewiesenes Körperteil" muss ebenfalls zugewiesen werden!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_UPPER_LEG =
    { $unassigned ->
        [0] Der linke Oberschenkel ist zugewiesen, aber entweder die Brust, die Hüfte oder die Taille muss ebenfalls zugewiesen werden!
       *[other] Der linke Oberschenkel ist zugewiesen, aber "Unbekanntes nicht zugewiesenes Körperteil" muss ebenfalls zugewiesen werden!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_UPPER_LEG =
    { $unassigned ->
        [0] Der rechte Oberschenkel ist zugewiesen, aber entweder die Brust, die Hüfte oder die Taille muss ebenfalls zugewiesen werden!
       *[other] Der rechte Oberschenkel ist zugewiesen, aber "Unbekanntes nicht zugewiesenes Körperteil" muss ebenfalls zugewiesen werden!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-HIP =
    { $unassigned ->
        [0] Die Hüfte ist zugewiesen, aber die Brust muss ebenfalls zugewiesen werden!
       *[other] Die Hüfte ist zugewiesen, aber "Unbekanntes nicht zugewiesenes Körperteil" muss ebenfalls zugewiesen werden!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-WAIST =
    { $unassigned ->
        [0] Taille ist zugewiesen, aber die Brust muss ebenfalls zugewiesen werden!
       *[unknown] Taille ist zugewiesen, aber "Unbekanntes nicht zugewiesenes Körperteil" muss ebenfalls zugewiesen werden!
    }

## Tracker mounting method choose

onboarding-choose_mounting = Welche Kalibrierungsmethode ist zu verwenden?
# Multiline text
onboarding-choose_mounting-description = Die Montageausrichtung korrigiert die Platzierung von Trackern am Körper.
onboarding-choose_mounting-auto_mounting = Befestigung automatisch ermitteln
# Italicized text
onboarding-choose_mounting-auto_mounting-label-v2 = Empfohlen
onboarding-choose_mounting-auto_mounting-description = Dadurch werden die Befestigungsausrichtungen für alle Ihrer Tracker automatisch aus 2 Posen erkannt
onboarding-choose_mounting-manual_mounting = Manuelle Befestigungsposition
# Italicized text
onboarding-choose_mounting-manual_mounting-label-v2 = Möglicherweise nicht präzise genug
onboarding-choose_mounting-manual_mounting-description = Auf diese Weise können Sie die Montagerichtung für jeden Tracker manuell auswählen
# Multiline text
onboarding-choose_mounting-manual_modal-title =
    Sind Sie sich sicher, dass Sie
    die automatische Tracker-Ausrichtung durchführen möchten?
onboarding-choose_mounting-manual_modal-description = <b>Die manuelle Tracker-Ausrichtung wird für neue Benutzer empfohlen</b>, da die Posen der automatischen Tracker-Ausrichtung anfangs schwer zu treffen sind und möglicherweise etwas Übung erfordern.
onboarding-choose_mounting-manual_modal-confirm = Ich bin mir sicher, was ich tue
onboarding-choose_mounting-manual_modal-cancel = Abbruch

## Tracker manual mounting setup

onboarding-manual_mounting-back = Zurück zum Eintritt in VR
onboarding-manual_mounting = Manuelle Definition der Befestigungsposition
onboarding-manual_mounting-description = Klicken Sie auf jeden Tracker und wählen Sie aus, in welche Richtung diese montiert sind
onboarding-manual_mounting-auto_mounting = Drehung automatisch ermitteln
onboarding-manual_mounting-next = Nächster Schritt

## Tracker automatic mounting setup

onboarding-automatic_mounting-back = Zurück zum Eintritt in VR
onboarding-automatic_mounting-title = Kalibrierung der Tracker-Befestigung/Rotation
onboarding-automatic_mounting-description = Damit die SlimeVR-Tracker korrekt funktionieren, müssen wir ihnen eine Drehung zuweisen, welche der Drehung entspricht wie diese befestigt sind.
onboarding-automatic_mounting-manual_mounting = Drehung manuell einstellen
onboarding-automatic_mounting-next = Nächster Schritt
onboarding-automatic_mounting-prev_step = Vorheriger Schritt
onboarding-automatic_mounting-done-title = Tracker Rotation kalibriert.
onboarding-automatic_mounting-done-description = Ihre Rotations-Kalibrierung ist abgeschlossen!
onboarding-automatic_mounting-done-restart = Zurück zum Start
onboarding-automatic_mounting-mounting_reset-title = Befestigungs-Reset
onboarding-automatic_mounting-mounting_reset-step-0 = 1. Beugen Sie sich in die "Skifahren"-Pose mit gebeugten Beinen, geneigtem Oberkörper und gebeugten Armen.
onboarding-automatic_mounting-mounting_reset-step-1 = 2. Drücken Sie die Schaltfläche "Befestigungs-Reset" und warten Sie 3 Sekunden, bevor die Drehungen der Tracker gesetzt werden.
onboarding-automatic_mounting-preparation-title = Vorbereitung
onboarding-automatic_mounting-put_trackers_on-title = Legen Sie Ihre Tracker an
onboarding-automatic_mounting-put_trackers_on-description = Um die Drehung der Tracker zu kalibrieren, werden die Tracker verwendet, welche Sie gerade zugewiesen haben. Ziehen Sie alle Ihre Tracker an, in der Abbildung rechts können sie sehen um welchen Tracker es sich handelt.
onboarding-automatic_mounting-put_trackers_on-next = Ich habe alle meine Tracker angelegt
onboarding-automatic_mounting-return-home = Fertig

## Tracker manual proportions setupa

onboarding-manual_proportions-title = Manuelle Körperproportionen
onboarding-manual_proportions-fine_tuning_button = Automatische Feinabstimmung der Proportionen
onboarding-manual_proportions-fine_tuning_button-disabled-tooltip = Bitte schließen Sie ein VR-Headset an, um die automatische Feinabstimmung zu nutzen
onboarding-manual_proportions-export = Proportionen exportieren
onboarding-manual_proportions-import = Proportionen importieren
onboarding-manual_proportions-file_type = Körperproportions-Datei
onboarding-manual_proportions-all_proportions = Alle Proportionen
onboarding-manual_proportions-estimated_height = Geschätzte Benutzergröße

## Tracker automatic proportions setup

onboarding-automatic_proportions-back = Gehen Sie zurück zum Reset-Tutorial
onboarding-automatic_proportions-title = Messen Sie Ihre Proportionen
onboarding-automatic_proportions-description = Damit die SlimeVR-Tracker funktionieren, müssen wir Ihre Proportionen kennen. Diese kurze Kalibrierung wird sie für Sie messen.
onboarding-automatic_proportions-manual = Manuelle Kalibrierung
onboarding-automatic_proportions-prev_step = Vorheriger Schritt
onboarding-automatic_proportions-put_trackers_on-title = Legen Sie Ihre Tracker an
onboarding-automatic_proportions-put_trackers_on-description = Um Ihre Proportionen zu kalibrieren, werden wir die Tracker verwenden, die Sie gerade zugewiesen haben. Legen Sie alle Ihre Tracker an. Sie können rechts in der Abbildung sehen, welche welche sind.
onboarding-automatic_proportions-put_trackers_on-next = Ich habe alle meine Tracker angelegt
onboarding-automatic_proportions-requirements-title = Anforderungen
# Each line of text is a different list item
onboarding-automatic_proportions-requirements-descriptionv2 =
    Sie haben genug Tracken, um Ihre Füße zu tracken (in der Regel 5 Tracker).
    Sie haben Ihre Tracker an und Ihr Headset auf.
    Ihre Tracker und Ihr Headset ist mit dem SlimeVR-Server verbunden und funktionieren ordnunsgemäß (z.B. kein Stottern, kein Trennen der Verbindung, usw.).
    Ihr Headset sendet Positionsdaten an den SlimeVR-Server (das bedeutet allgemein, dass SteamVR läuft und über den SlimeVR-SteamVR Treiber mit SteamVR verbunden ist).
    Ihr Tracking funktioniert und stellt Ihre Bewegungen akkurat dar (z.B.: Sie haben einen Reset durchgeführt und Ihre Tracker bewegen sich beim Treten, Bücken, Sitzen, usw. richtig).
onboarding-automatic_proportions-requirements-next = Ich habe die Anforderungen gelesen
onboarding-automatic_proportions-check_height-title-v3 = Messen der Höhe Ihres Headsets
onboarding-automatic_proportions-check_height-description-v2 = Die Höhe Ihres Headsets (HMD) sollte etwas geringer sein als Ihre vollständige Körpergröße, da Headsets die Höhe Ihrer Augen messen. Diese Messung wird als Grundlage für Ihre Körperproportionen verwendet.
# All the text is in bold!
onboarding-automatic_proportions-check_height-calculation_warning-v3 = Um Ihre Körpergröße zu messen, beginnen Sie mit der Messung, während Sie <u>aufrecht</u> stehen. Achten Sie darauf, dass Sie Ihre Hände nicht höher als Ihr Headset heben, da dies die Messung beeinflussen könnte!
onboarding-automatic_proportions-check_height-guardian_tip = Wenn Sie ein eigenständiges VR Headset verwenden, stellen Sie sicher, dass Ihr Guardian Begrenzung aktiviert ist und damit Ihre Größe korrekt ist!
# Context is that the height is unknown
onboarding-automatic_proportions-check_height-unknown = Unbekannt
# Shows an element below it
onboarding-automatic_proportions-check_height-hmd_height2 = Die Höhe Ihres Headsets beträgt:
onboarding-automatic_proportions-check_height-measure-start = Messung starten
onboarding-automatic_proportions-check_height-measure-stop = Messung stoppen
onboarding-automatic_proportions-check_height-measure-reset = Messung wiederholen
onboarding-automatic_proportions-check_height-next_step = Headset-Höhe verwenden
onboarding-automatic_proportions-check_floor_height-title = Messen der Bodenhöhe (optional)
onboarding-automatic_proportions-check_floor_height-description = In einigen Fällen wird die Bodenhöhe möglicherweise nicht korrekt von Ihrem Headset erfasst, wodurch die Headset-Höhe höher gemessen wird, als sie sein sollte. Sie können die „Höhe“ Ihres Bodens messen, um die Headset-Höhe zu korrigieren.
# All the text is in bold!
onboarding-automatic_proportions-check_floor_height-calculation_warning-v2 = Starten Sie die Messung und legen Sie einen Controller auf den Boden, um dessen Höhe zu messen. Wenn Sie sicher sind, dass die Bodenhöhe korrekt ist, können Sie diesen Schritt überspringen.
# Shows an element below it
onboarding-automatic_proportions-check_floor_height-floor_height = Die Bodenhöhe beträgt:
onboarding-automatic_proportions-check_floor_height-full_height = Die geschätzte Körpergröße ist:
onboarding-automatic_proportions-check_floor_height-measure-start = Messung starten
onboarding-automatic_proportions-check_floor_height-measure-stop = Messung stoppen
onboarding-automatic_proportions-check_floor_height-measure-reset = Messung wiederholen
onboarding-automatic_proportions-check_floor_height-skip_step = Schritt überspringen und speichern
onboarding-automatic_proportions-check_floor_height-next_step = Bodenhöhe verwenden und speichern
onboarding-automatic_proportions-start_recording-title = Bereiten Sie sich auf ein paar Bewegungen vor
onboarding-automatic_proportions-start_recording-description = Wir werden nun einige bestimmte Posen und Bewegungen aufnehmen. Diese werden im nächsten Schritt angezeigt. Sei bereit damit zu beginnen, wenn du auf den Knopf drückst!
onboarding-automatic_proportions-start_recording-next = Aufnahme starten
onboarding-automatic_proportions-recording-title = Aufnahme
onboarding-automatic_proportions-recording-description-p0 = Aufnahme läuft...
onboarding-automatic_proportions-recording-description-p1 = Machen Sie die unten beschriebenen Bewegungen:
# Each line of text is a different list item
onboarding-automatic_proportions-recording-steps =
    Stehen Sie aufrecht und drehen Sie den Kopf im Kreis.
    Beugen Sie den Rücken nach vorne und gehen Sie in die Hocke. Schauen Sie in der Hocke erst nach links, dann nach rechts.
    Drehen Sie Ihren Oberkörper nach links (gegen den Uhrzeigersinn), dann strecken Sie sich nach unten zum Boden.
    Drehen Sie Ihren Oberkörper nach rechts (im Uhrzeigersinn) und strecken Sie ihn dann nach unten zum Boden.
    Rollen Sie Ihre Hüften in einer kreisförmigen Bewegung, als ob Sie einen Hula-Hoop-Reifen benutzen würden.
    Wenn die Aufnahme noch nicht zu Ende ist, können Sie diese Schritte wiederholen, bis sie zu Ende ist.
onboarding-automatic_proportions-recording-processing = Aufnahme wird verarbeitet...
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 20)
onboarding-automatic_proportions-recording-timer =
    { $time ->
        [one] 1 Sekunde verbleibend
       *[other] { $time } Sekunden verbleibend
    }
onboarding-automatic_proportions-verify_results-title = Ergebnisse überprüfen
onboarding-automatic_proportions-verify_results-description = Sehen die untenstehenden Ergebnisse korrekt aus?
onboarding-automatic_proportions-verify_results-results = Aufnahme-Ergebnisse
onboarding-automatic_proportions-verify_results-processing = Ergebnis wird bearbeitet
onboarding-automatic_proportions-verify_results-redo = Aufnahme wiederholen
onboarding-automatic_proportions-verify_results-confirm = Ergebnisse sind korrekt
onboarding-automatic_proportions-done-title = Körper gemessen und gespeichert.
onboarding-automatic_proportions-done-description = Ihre Körperproportionen-Kalibrierung ist abgeschlossen!
onboarding-automatic_proportions-error_modal-v2 =
    <b>Warnung:</b> Bei der Schätzung der Proportionen ist ein Fehler aufgetreten!
    Dies ist wahrscheinlich ein Problem mit der Tracker-Ausrichtung. Vergewissern Sie sich, dass Ihre Tracker ordnungsgemäß funktioniert, bevor Sie es erneut versuchen.
    Bitte <docs>überprüfen Sie die Dokumentation</docs> oder treten Sie unserem <discord>Discord</discord> bei, um Hilfe zu erhalten ^_^
onboarding-automatic_proportions-error_modal-confirm = Verstanden!
onboarding-automatic_proportions-smol_warning =
    Ihre konfigurierte Höhe von { $height } ist kleiner als die minimale akzeptierte Höhe von { $minHeight }.
    <b>Bitte wiederholen Sie die Messungen und stellen Sie sicher, dass sie korrekt sind.</b>
onboarding-automatic_proportions-smol_warning-cancel = Zurück

## User height calibration

onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-low = Schauen sie nicht auf den Boden
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-high = Schauen sie nicht zu hoch nach oben
onboarding-user_height-calibration-WAITING_FOR_CONTROLLER_PITCH = Achten sie darauf, dass der Controller nach unten zeigt
onboarding-user_height-calibration-DONE = Erfolg!

## Stay Aligned setup

onboarding-stay_aligned-title = Stay Aligned
onboarding-stay_aligned-description = Konfigurieren Sie Stay Aligned, um Ihre Tracker ausgerichtet zu halten.
onboarding-stay_aligned-put_trackers_on-title = Legen Sie Ihre Tracker an
onboarding-stay_aligned-put_trackers_on-description = Um Ihre Ruheposen zu speichern, verwenden wir die Tracker, die Sie gerade zugewiesenen haben. Legen Sie all Ihre Tracker an. In der Abbildung rechts können Sie sehen, welcher welcher ist.
onboarding-stay_aligned-put_trackers_on-trackers_warning = Sie haben derzeit weniger als 5 Tracker verbunden und zugewiesen! Dies ist die Mindestanzahl an Trackern, die erforderlich sind, damit Stay Aligned richtig funktioniert.
onboarding-stay_aligned-put_trackers_on-next = Ich habe alle meine Tracker angelegt
onboarding-stay_aligned-verify_mounting-title = Tracker-Ausrichtung
onboarding-stay_aligned-verify_mounting-redo_mounting = Tracker-Ausrichtungskalibrierung wiederholen
onboarding-stay_aligned-preparation-title = Vorbereitung
onboarding-stay_aligned-preparation-tip = Achten Sie darauf, aufrecht zu stehen. Schauen Sie nach vorne und lassen Sie die Arme an den Seiten hängen.
onboarding-stay_aligned-relaxed_poses-standing-step-0 = 1. Nehmen Sie eine bequeme Haltung ein. Entspannen Sie sich!
onboarding-stay_aligned-relaxed_poses-standing-step-1-v2 = 2. Drücken Sie die Taste „Pose speichern“.
onboarding-stay_aligned-relaxed_poses-skip_step = Überspringen
onboarding-stay_aligned-previous_step = Zurück
onboarding-stay_aligned-next_step = Weiter
onboarding-stay_aligned-restart = Neu starten
onboarding-stay_aligned-done = Fertig

## Home

home-no_trackers = Keine Tracker erkannt oder zugewiesen
home-settings-close = Schließen

## Trackers Still On notification

trackers_still_on-modal-title = Es sind noch Tracker eingeschaltet
trackers_still_on-modal-description =
    Ein oder mehrere Tracker sind noch eingeschaltet.
    Möchten Sie SlimeVR trotzdem beenden?
trackers_still_on-modal-confirm = SlimeVR beenden
trackers_still_on-modal-cancel = Bitte warten...

## Status system

status_system-StatusTrackerReset = Es wird empfohlen, einen vollständigen Reset durchzuführen, da ein oder mehrere Tracker nicht kalibriert sind.
status_system-StatusSteamVRDisconnected =
    { $type ->
        [steamvr_feeder] Derzeit nicht mit der SlimeVR-Feeder-Appverbunden.
       *[other] Derzeit nicht über den SlimeVR-Treiber mit SteamVR verbunden.
    }
status_system-StatusTrackerError = Der Tracker "{ $trackerName }" weist einen Fehler auf.
status_system-StatusUnassignedHMD = Das VR-Headset sollte als Kopf-Tracker zugewiesen sein.

## Firmware tool globals

firmware_tool-next_step = Nächster Schritt
firmware_tool-previous_step = Vorheriger Schritt
firmware_tool-ok = Sieht gut aus
firmware_tool-retry = Erneut versuchen
firmware_tool-loading = Lade...

## Firmware tool Steps

firmware_tool = DIY Firmware-Tool
firmware_tool-description = Erlaubt ihnen das Konfigurieren und Flashen von DIY Trackern
firmware_tool-not_available = Das Firmware Tool ist im Moment nicht verfügbar. Versuche sie später erneut!
firmware_tool-not_compatible = Das Firmware Tool ist nicht mit dieser Version des Servers kompatibel. Bitte den Server aktualisieren!
firmware_tool-select_source-board_type = Boardtyp
firmware_tool-select_source-firmware = Firmware-Quelle
firmware_tool-select_source-version = Firmware-Version
firmware_tool-select_source-official = Offiziell
firmware_tool-select_source-dev = Dev
firmware_tool-board_defaults = Konfigurieren Sie Ihr Board
firmware_tool-board_defaults-add = Hinzufügen
firmware_tool-board_defaults-reset = Auf Standard zurücksetzen
firmware_tool-board_defaults-error-required = Erforderliches Feld
firmware_tool-board_defaults-error-format = Ungültiges Format
firmware_tool-board_defaults-error-format-number = Keine Zahl
firmware_tool-flash_method_step = Flash-Methode
firmware_tool-flash_method_step-description = Bitte wählen Sie die Flash-Methode aus, die Sie verwenden möchten.
firmware_tool-flash_method_step-ota-v2 =
    .label = WLAN
    .description = Verwenden Sie die Over-the-Air-Methode. Ihr Tracker wird seine Firmware über WLAN aktualisieren. Funktioniert nur bei Trackern, die bereits eingerichtet wurden.
firmware_tool-flash_method_step-ota-info =
    Wir nutzen Ihre WLAN-Zugangsdaten, um den Tracker zu flashen und zu bestätigen, dass alles korrekt funktioniert hat.
    <b>Wir speichern Ihre WLAN-Zugangsdaten nicht!</b>
firmware_tool-flash_method_step-serial-v2 =
    .label = USB
    .description = Verwenden Sie ein USB-Kabel, um Ihren Tracker zu aktualisieren.
firmware_tool-flashbtn_step = Drücken Sie den Boot-Button
firmware_tool-flashbtn_step-description = Bevor Sie mit dem nächsten Schritt fortfahren, gibt es ein paar Dinge, die Sie erledigen müssen.
firmware_tool-flashbtn_step-board_SLIMEVR = Schalten Sie den Tracker aus, entfernen Sie das Gehäuse (falls vorhanden), verbinden Sie ein USB-Kabel mit diesem Computer und führen Sie dann einen der folgenden Schritte entsprechend Ihrer SlimeVR-Board-Revision aus:
firmware_tool-flashbtn_step-board_OTHER =
    Bevor Sie den Tracker flashen, müssen Sie ihn wahrscheinlich in den Bootloader-Modus versetzen.
    In den meisten Fällen bedeutet das, dass Sie die Boot-Taste auf dem Board drücken müssen, bevor der Flash-Vorgang beginnt.
    Wenn der Flash-Vorgang zu Beginn aufgrund eines Timeouts fehlschlägt, bedeutet das wahrscheinlich, dass der Tracker nicht im Bootloader-Modus war.
    Bitte beziehen Sie sich auf die Flash-Anweisungen Ihres Boards, um zu erfahren, wie Sie den Bootloader-Modus aktivieren.
firmware_tool-flash_method_ota-title = Flashen über WLAN
firmware_tool-flash_method_ota-devices = Erkannte OTA-Geräte:
firmware_tool-flash_method_ota-no_devices = Es sind keine Boards vorhanden, die über OTA aktualisiert werden können. Stellen Sie sicher, dass Sie den richtigen Board-Typ ausgewählt haben.
firmware_tool-flash_method_serial-title = Über USB flashen
firmware_tool-flash_method_serial-wifi = WLAN-Zugangsdaten:
firmware_tool-flash_method_serial-devices-label = Erkannte serielle Geräte:
firmware_tool-flash_method_serial-devices-placeholder = Wählen Sie ein serielles Gerät aus
firmware_tool-flash_method_serial-no_devices = Es wurden keine kompatiblen seriellen Geräte erkannt. Stellen Sie sicher, dass der Tracker angeschlossen ist.
firmware_tool-build_step = Building
firmware_tool-build_step-description = Die Firmware wird erstellt, bitte warten.
firmware_tool-flashing_step = Flashen
firmware_tool-flashing_step-description = Ihre Tracker werden geflasht, bitte folgen Sie den Anweisungen auf dem Bildschirm.
firmware_tool-flashing_step-warning-v2 = Trennen oder schalten Sie den Tracker während des Upload-Vorgangs nicht aus, es sei denn, Sie werden dazu aufgefordert, da dies den Tracker unbrauchbar machen kann.
firmware_tool-flashing_step-flash_more = Weitere Tracker flashen
firmware_tool-flashing_step-exit = Schließen

## firmware tool build status

firmware_tool-build-QUEUED = Warte darauf, zu bauen....
firmware_tool-build-CREATING_BUILD_FOLDER = Erstelle den Build-Ordner
firmware_tool-build-DOWNLOADING_SOURCE = Lade den Quellcode herunter
firmware_tool-build-EXTRACTING_SOURCE = Entpacken des Quellcode
firmware_tool-build-BUILDING = Erstellen der Firmware
firmware_tool-build-SAVING = Speichern des Builds
firmware_tool-build-DONE = Erstellen abgeschlossen
firmware_tool-build-ERROR = Die Firmware konnte nicht erstellt werden

## Firmware update status

firmware_update-status-DOWNLOADING = Lade die Firmware herunter
firmware_update-status-NEED_MANUAL_REBOOT-v2 = Bitte schalten Sie Ihren Tracker aus und wieder ein.
firmware_update-status-AUTHENTICATING = Authentifizierung mit dem Mikrokontroller
firmware_update-status-UPLOADING = Lade die Firmware hoch
firmware_update-status-SYNCING_WITH_MCU = Synchronisieren mit dem Mikrokontroller
firmware_update-status-REBOOTING = Wende das Update an
firmware_update-status-PROVISIONING = WLAN-Zugangsdaten werden gesendet
firmware_update-status-DONE = Update abgeschlossen!
firmware_update-status-ERROR_DEVICE_NOT_FOUND = Gerät konnte nicht gefunden werden
firmware_update-status-ERROR_TIMEOUT = Während des Updates ist eine Zeitüberschreitung aufgetreten
firmware_update-status-ERROR_DOWNLOAD_FAILED = Die Firmware konnte nicht heruntergeladen werden
firmware_update-status-ERROR_AUTHENTICATION_FAILED = Authentifizierung mit dem Mikrokontroller fehlgeschlagen
firmware_update-status-ERROR_UPLOAD_FAILED = Die Firmware konnte nicht hochgeladen werden
firmware_update-status-ERROR_PROVISIONING_FAILED = Die WLAN-Zugangsdaten konnten nicht festgelegt werden
firmware_update-status-ERROR_UNSUPPORTED_METHOD = Die Update-Methode wird nicht unterstützt
firmware_update-status-ERROR_UNKNOWN = Unbekannter Fehler

## Dedicated Firmware Update Page

firmware_update-title = Firmware-Update
firmware_update-devices = Verfügbare Geräte
firmware_update-devices-description = Bitte wählen Sie die Tracker aus, die Sie auf die neueste Version der SlimeVR-Firmware aktualisieren möchten
firmware_update-no_devices = Bitte stellen Sie sicher, dass die Tracker, die Sie aktualisieren möchten, eingeschaltet und mit dem WLAN verbunden sind!
firmware_update-changelog-title = Aktualisieren auf { $version }
firmware_update-looking_for_devices = Suche nach Geräten zum Aktualisieren...
firmware_update-retry = Erneut versuchen
firmware_update-update = Aktualisiere ausgewählte Tracker
firmware_update-exit = Schließen

## Tray Menu

tray_menu-show = Anzeigen
tray_menu-hide = Ausblenden
tray_menu-quit = Beenden

## First exit modal

tray_or_exit_modal-title = Was soll der Schließen-Knopf tun?
# Multiline text
tray_or_exit_modal-description =
    Hier können Sie auswählen, ob sich der Server beim Schließen beenden oder in den Infobereich minimiert werden soll.
    
    Sie können dies später in den Einstellungen der Bedienoberfläche ändern!
tray_or_exit_modal-radio-exit = Beenden
tray_or_exit_modal-radio-tray = In den Infobereich minimieren
tray_or_exit_modal-submit = Speichern
tray_or_exit_modal-cancel = Abbruch

## Unknown device modal

unknown_device-modal-title = Ein neuer Tracker wurde gefunden!
unknown_device-modal-description =
    Es gibt einen neuen Tracker mit der MAC-Adresse <b>{ $deviceId }</b>.
    Möchten Sie diesen mit SlimeVR verbinden?
unknown_device-modal-confirm = Sicher!
unknown_device-modal-forget = Ignorieren
# VRChat config warnings
vrc_config-page-title = VRChat Konfigurations-Warnungen
vrc_config-page-desc = Diese Seite zeigt den Zustand deiner VRChat-Einstellungen und zeigt, welche Einstellungen mit SlimeVR inkompatibel sind. Es wird dringend empfohlen, alle hier angezeigten Warnungen zu beheben, um das beste Nutzererlebnis mit SlimeVR zu gewährleisten.
vrc_config-page-help-desc = Schauen Sie sich unsere <a>Dokumentation zu diesem Thema</a> an!
vrc_config-on = An
vrc_config-off = Aus
vrc_config-invalid = Sie haben falsch konfigurierte VRChat-Einstellungen!
vrc_config-show_more = Mehr anzeigen
vrc_config-setting_name = Name der VRChat-Einstellung
vrc_config-recommended_value = Empfohlener Wert
vrc_config-current_value = Aktueller Wert
vrc_config-mute = Warnung stummschalten
vrc_config-mute-btn = Stummschalten
vrc_config-unmute-btn = Stummschaltung aufheben
vrc_config-disable_shoulder_tracking = Schultertracking deaktivieren
vrc_config-user_height = Echte Benutzergröße
vrc_config-spine_mode-UNKNOWN = Unbekannt
vrc_config-spine_mode-LOCK_HEAD = Kopf sperren
vrc_config-spine_mode-LOCK_HIP = Hüfte sperren
vrc_config-tracker_model-UNKNOWN = Unbekannt
vrc_config-tracker_model-AXIS = Achse
vrc_config-tracker_model-SYSTEM = System
vrc_config-avatar_measurement_type-UNKNOWN = Unbekannt
vrc_config-avatar_measurement_type-HEIGHT = Höhe
vrc_config-avatar_measurement_type-ARM_SPAN = Armspannweite

## Error collection consent modal

error_collection_modal-title = Können wir Fehler sammeln?
error_collection_modal-description_v2 =
    { settings-interface-behavior-error_tracking-description_v2 }
    
    Sie können diese Einstellung später im Abschnitt Verhalten auf der Einstellungsseite ändern.
error_collection_modal-confirm = Ich stimme zu
error_collection_modal-cancel = Ich will nicht

## Tracking checklist section

tracking_checklist-settings-close = Schließen
tracking_checklist-status-partial =
    { $count ->
        [one] Sie haben 1 Warnung!
       *[other] Sie haben { $count } Warnungen!
    }
tracking_checklist-MOUNTING_CALIBRATION = Tracker-Ausrichtung durchführen
tracking_checklist-STEAMVR_DISCONNECTED = SteamVR läuft nicht
tracking_checklist-STEAMVR_DISCONNECTED-desc = SteamVR läuft nicht. Nutzen sie es für VR?
tracking_checklist-STEAMVR_DISCONNECTED-open = SteamVR starten
tracking_checklist-TRACKERS_REST_CALIBRATION-desc = Sie haben keine Tracker-Kalibrierung durchgeführt. Bitte lassen Sie Ihre Tracker (gelb markiert) für einige Sekunden auf einer stabilen Oberfläche ruhen.
tracking_checklist-TRACKER_ERROR = Tracker mit Fehlern
tracking_checklist-VRCHAT_SETTINGS = VRChat-Einstellungen konfigurieren
tracking_checklist-VRCHAT_SETTINGS-open = Gehen sie zu den VRChat-Warnungen
tracking_checklist-NETWORK_PROFILE_PUBLIC-open = Kontrollpanel öffnen
tracking_checklist-STAY_ALIGNED_CONFIGURED = Stay Aligned konfigurieren
tracking_checklist-STAY_ALIGNED_CONFIGURED-open = Öffne den Stay Aligned Assistent
tracking_checklist-ignore = Ignorieren
preview-mocap_mode_soon = Mocap-Modus (Bald™)
preview-disable_render = Vorschau deaktivieren
preview-disabled_render = Vorschau deaktiviert
toolbar-mounting_calibration = Tracker-Ausrichtung
toolbar-mounting_calibration-default = Körper
toolbar-mounting_calibration-feet = Füße
toolbar-mounting_calibration-fingers = Finger
toolbar-assigned_trackers = { $count } Tracker zugewiesen
toolbar-unassigned_trackers = { $count } Tracker nicht zugewiesen
