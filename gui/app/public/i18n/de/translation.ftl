# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Lade...
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
tips-failed_webgl = Fehler beim Initialisieren von WebGL.

## Units

unit-foot = Fuß
unit-inch = Zoll
unit-cm = Zentimeter

## Dropdown


## Text input


## File input


## Window controls

titlebar-close = Schließen

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
board_type-CUSTOM = Benutzerdefiniertes Board
board_type-GLOVE_IMU_SLIMEVR_DEV = SlimeVR Dev-IMU-Handschuh
board_type-GESTURES = Gesten
board_type-GENERIC_NRF = Generisches nRF

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
skeleton_bone-LOWER_CHEST-desc =
    Dies ist der Abstand vom Ansatz der Brust bis zur Mitte Ihrer Wirbelsäule.
    Passen Sie zunächst Ihre Rumpflänge korrekt an und verändern Sie dann diesen Wert in verschiedenen Positionen (z.B. im Sitzen, beim Bücken oder Liegen), bis Ihre virtuelle Wirbelsäule mit Ihrer echten übereinstimmt.
skeleton_bone-HIP = Hüftlänge
skeleton_bone-HIPS_WIDTH = Hüftbreite
skeleton_bone-leg_group = Beinlänge
skeleton_bone-UPPER_LEG = Linker Oberschenkellänge
skeleton_bone-LOWER_LEG = Unterschenkellänge
skeleton_bone-FOOT_LENGTH = Fußlänge
skeleton_bone-FOOT_SHIFT = Fußverschiebung
skeleton_bone-SHOULDERS_DISTANCE = Schulterentfernung
skeleton_bone-SHOULDERS_WIDTH = Schulterbreite
skeleton_bone-arm_group = Armlänge
skeleton_bone-UPPER_ARM = Oberarmlänge
skeleton_bone-LOWER_ARM = Unterarmlänge
skeleton_bone-HAND_Y = Y-Abstand der Hände
skeleton_bone-HAND_Z = Z-Abstand der Hände

## Tracker reset buttons

reset-reset_all = Alle Proportionen zurücksetzen
reset-reset_all_warning-reset = Proportionen zurücksetzen
reset-reset_all_warning-cancel = Abbruch
reset-full = Reset
reset-mounting = Kalibrierung der Tracker-Befestigung/Rotation
reset-mounting-feet = Fuß-Befestigungs-Reset
reset-mounting-fingers = Fingerkalibrierung
reset-yaw = Horizontaler Reset
reset-error-mounting-need_full_reset = Ein vollständiger Reset ist vor der Tracker-Ausrichtung erforderlich.
reset-error-yaw-need_full_reset = Für den Yaw-Reset ist ein vollständiger Reset erforderlich.

## Navigation bar

navbar-home = Start
navbar-body_proportions = Körperproportionen
navbar-trackers_assign = Tracker-Zuordnung
navbar-mounting = Kalibrierung der Tracker-Befestigung/Rotation
navbar-onboarding = Einrichtungs-Assistent
navbar-settings = Einstellungen
navbar-connect_trackers = Tracker verbinden

## Biovision hierarchy recording

bvh-start_recording = BVH aufnehmen
bvh-stop_recording = BVH-Aufnahme speichern
bvh-recording = Aufnahme läuft...
bvh-save_title = BVH-Aufnahme speichern

## Tracking pause

tracking-unpaused = Tracking pausieren
tracking-paused = Tracking fortsetzen

## Widget: Developer settings

widget-developer_mode = Entwicklermodus
widget-developer_mode-high_contrast = Hoher Kontrast
widget-developer_mode-precise_rotation = Präzise Drehung
widget-developer_mode-fast_data_feed = Schnelleres Update-Intervall
widget-developer_mode-raw_slime_rotation = Rohe Drehung

## Widget: IMU Visualizer

widget-imu_visualizer = Drehung
widget-imu_visualizer-preview = Vorschau
widget-imu_visualizer-hide = Ausblenden
widget-imu_visualizer-rotation_raw = Rohe Drehung
widget-imu_visualizer-rotation_preview = Vorschau
widget-imu_visualizer-acceleration = Beschleunigung

## Tracker status

tracker-status-none = Kein Status
tracker-status-busy = Beschäftigt
tracker-status-error = Fehler
tracker-status-disconnected = Getrennt
tracker-status-occluded = Verdeckt
tracker-status-ok = Verbunden
tracker-status-timed_out = Zeitüberschreitung

## Tracker status columns

tracker-table-column-type = Typ
tracker-table-column-battery = Batterie
tracker-table-column-ping = Latenz
tracker-table-column-linear-acceleration = Beschleunigung X/Y/Z
tracker-table-column-url = Adresse

## Tracker rotation

tracker-rotation-front = Vorne
tracker-rotation-front_left = Vorne-Links
tracker-rotation-front_right = Vorne-Rechts
tracker-rotation-left = Links
tracker-rotation-right = Rechts
tracker-rotation-back = nach Hinten
tracker-rotation-back_left = Hinten-Links
tracker-rotation-back_right = Hinten-Rechts
tracker-rotation-custom = Benutzerdefiniert

## Tracker information

tracker-infos-manufacturer = Hersteller
tracker-infos-display_name = Anzeigename
tracker-infos-custom_name = Benutzerdefinierter Name
tracker-infos-url = Tracker-Adresse
tracker-infos-hardware_identifier = Hardware-ID
tracker-infos-imu = IMU-Sensor
tracker-infos-board_type = Platine
tracker-infos-network_version = Protokoll Version
tracker-infos-magnetometer-status-v1 =
    { $status ->
        [DISABLED] Ausgeschalten
        [ENABLED] Angeschalten
       *[NOT_SUPPORTED] Nicht unterstützt
    }
tracker-infos-packet_loss = Paketverlust
tracker-infos-packets_lost = Pakete verloren
tracker-infos-packets_received = Pakete empfangen

## Tracker settings

tracker-settings-back = Zurück zur Tracker-Liste
tracker-settings-title = Tracker-Einstellungen
tracker-settings-assignment_section = Zuweisung
tracker-settings-assignment_section-description = Welcher Körperteil dem Tracker zugewiesen ist.
tracker-settings-assignment_section-edit = Zuweisung bearbeiten
tracker-settings-mounting_section = Befestigungsposition
tracker-settings-mounting_section-description = Wo ist der Tracker befestigt?
tracker-settings-mounting_section-edit = Befestigung bearbeiten
tracker-settings-use_mag = Magnetometer auf diesem Tracker zulassen
# Multiline!
tracker-settings-use_mag-description =
    Soll dieser Tracker das Magnetometer verwenden um Drift zu reduzieren, wenn die Verwendung von Magnetometer erlaubt ist? <b> Bitten schalten Sie den Tracker nicht aus, während Sie diese Einstellung umschalten!</b>
    
    Sie müssen zuerst die Verwendung des Magnetometers zulassen, <magSetting>klicken Sie hier, um zu den Einstellungen zu gelangen</magSetting>.
tracker-settings-use_mag-label = Magnetometer zulassen
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Trackername
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
tracker-settings-current-version = Aktuelle
tracker-settings-latest-version = Aktuelleste
tracker-settings-build-date = Herstellungsdatum

## Dongle settings

dongle-infos-hardware_revision = Hardware-Version
dongle-status-disconnected = Getrennt
dongle-settings-back = Zurück zur Tracker-Liste
dongle-settings-update = Jetzt aktualisieren
dongle-settings-update-title = Firmware-Version

## Tracker part card info

tracker-part_card-unassigned = Nicht zugewiesen

## Body assignment menu

body_assignment_menu = Wo möchten Sie diesen Tracker platzieren?
body_assignment_menu-description = Wählen Sie die Position aus, an dem Sie diesen Tracker befestigt haben. Alternativ können Sie auch alle Tracker auf einmal verwalten statt einzeln.
body_assignment_menu-manage_trackers = Verwalte alle Tracker
body_assignment_menu-unassign_tracker = Zuweisung des Trackers aufheben

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Warnung:</b> Ein Hals-Tracker kann tödlich sein, wenn dieser zu fest angezogen ist.
    Der Riemen kann die Blutzirkulation zu Ihrem Kopf unterbrechen!
tracker_selection_menu-neck_warning-done = Ich verstehe die Risiken
tracker_selection_menu-neck_warning-cancel = Abbruch

## Mounting menu

mounting_selection_menu-close = Schließen

## Sidebar settings

settings-sidebar-title = Einstellungen
settings-sidebar-general = Allgemein
settings-sidebar-trackers = Tracker
settings-sidebar-interface = Bedienoberfläche
settings-sidebar-utils = Werkzeuge
settings-sidebar-appearance = Erscheinungsbild
settings-sidebar-home = Startbildschirm
settings-sidebar-checklist = Tracking-Checkliste
settings-sidebar-notifications = Benachrichtigungen
settings-sidebar-firmware-tool = DIY Firmware-Tool
settings-sidebar-vrc_warnings = VRChat Konfigurations-Warnungen
settings-sidebar-advanced = Erweitert

## Bone routing settings

settings-routing-output-badge-off = Aus
settings-routing-group-fingers = Finger
settings-routing-hands-warning-cancel = Abbruch

## SteamVR / Monado output settings

settings-driver-enable = Aktivieren
settings-driver-status-badge-disabled = Aus

## Tracker mechanics

settings-general-tracker_mechanics-filtering = Filtern
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Wählen Sie den Filter-Typ für Ihre Tracker aus.
    Vorhersage: prognostiziert Bewegung. Glättung: Bewegung werden geglättet.
settings-general-tracker_mechanics-filtering-type-none = Kein Filter
settings-general-tracker_mechanics-filtering-type-none-description = Verwendet die unveränderten Rotationsdaten der Tracker.
settings-general-tracker_mechanics-filtering-type-smoothing = Glättung
settings-general-tracker_mechanics-filtering-type-smoothing-description = Glättet Bewegungen, fügt aber etwas Verzögerung hinzu.
settings-general-tracker_mechanics-filtering-type-prediction = Vorhersage
settings-general-tracker_mechanics-filtering-type-prediction-description = Verringert die Latenz und macht die Bewegungen schneller, kann aber Zittern erhöhen.
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
settings-general-tracker_mechanics-trackers_over_usb = Tracker über USB
settings-general-tracker_mechanics-trackers_over_usb-enabled-label = Erlaube HID-Tracker eine USB-Direktverbindung
settings-stay_aligned-description = Stay Aligned reduziert Drift, indem es deine Tracker schrittweise an deine entspannten Posen anpasst.
settings-stay_aligned-setup-label = Stay Aligned einrichten
settings-stay_aligned-setup-description = Sie müssen Stay Aligned einrichten, um es zu aktivieren.
settings-stay_aligned-enabled-label = Tracker anpassen
settings-stay_aligned-general-label = Allgemein
settings-stay_aligned-relaxed_poses-label = Entspannte Posen
settings-stay_aligned-relaxed_poses-standing = Tracker im Stehen anpassen
settings-stay_aligned-relaxed_poses-sitting = Tracker anpassen, während du auf einem Stuhl sitzt
settings-stay_aligned-relaxed_poses-save_pose = Pose speichern
settings-stay_aligned-relaxed_poses-reset_pose = Pose zurücksetzen
settings-stay_aligned-relaxed_poses-close = Schließen
settings-stay_aligned-debug-label = Debuggen
settings-stay_aligned-debug-description = Bitte geben Sie Ihre Einstellungen mit an, wenn Sie Probleme mit Stay Aligned melden.
settings-stay_aligned-debug-copy-label = Einstellungen in die Zwischenablage kopieren

## Keybinds Page

settings-keybinds_full-reset = Reset
settings-keybinds_yaw-reset = Horizontaler Reset
settings-keybinds_reset-all-button = Alles zurücksetzen
settings-keybinds-recorder-modal-done-button = Fertig
settings-keybinds-recorder-modal-cancel-button = Abbruch

## FK/Tracking settings

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
settings-general-fk_settings-leg_fk-reset_mounting_feet-v1 = Fuß-Ausrichtung kalibrieren
settings-general-fk_settings-leg_fk-reset_mounting_feet-description-v1 = Erzwinge Fußausrichtungs-Kalibrierung während der Körperausrichtungs-Kalibrierung.
settings-general-fk_settings-enforce_joint_constraints = Gelenkgrenzen
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints = Grenzen erzwingen
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = Verhindert, dass sich Gelenke über ihre Grenzen hinaus drehen
settings-general-fk_settings-ik = Positionsdaten
settings-general-fk_settings-ik-use_position = Positionsdaten verwenden
settings-general-fk_settings-arm_fk-back = nach Hinten
settings-general-fk_settings-arm_fk-back-description = Der Standardmodus, bei dem die Oberarme nach hinten und die Unterarme nach vorne gehen.
settings-general-fk_settings-arm_fk-tpose_up = T-Pose (oben)
settings-general-fk_settings-arm_fk-tpose_up-description = Erwartet, dass deine Arme während des vollständigen Zurücksetzens seitlich nach unten gerichtet sind und während des Reset der Tracker-Ausrichtung um 90 Grad nach außen gerichtet sind.
settings-general-fk_settings-arm_fk-tpose_down = T-Pose (unten)
settings-general-fk_settings-arm_fk-tpose_down-description = Erwartet, dass deine Arme während des vollständigen Zurücksetzens um 90 Grad nach außen gerichtet sind und während des Befestigungs-Reset seitlich nach unten.
settings-general-fk_settings-arm_fk-forward = Vorwärts
settings-general-fk_settings-arm_fk-forward-description = Erwartet, dass deine Arme um 90 Grad nach vorne gerichtet sind. Nützlich für VTubing.
settings-general-fk_settings-skeleton_settings-ratios = Skelettverhältnisse
settings-general-fk_settings-skeleton_settings-ratios-description = Ändert die Werte der Skeletteinstellungen. Nachdem Sie diese geändert haben, müssen Sie möglicherweise Ihre Proportionen anpassen.
settings-general-fk_settings-self_localization-title = Motion-Capture-Modus

## Gesture control settings (tracker tapping)

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

## Notification settings

settings-interface-notifications = Benachrichtigungen
settings-general-interface-feedback_sound = Feedback-Geräusch
settings-general-interface-feedback_sound-description = Diese Option wird ein Geräusch abspielen, wenn ein Reset ausgeführt wurde.
settings-general-interface-feedback_sound-label = Feedback-Geräusch
settings-general-interface-feedback_sound-volume = Feedback-Sound-Lautstärke
settings-general-interface-connected_trackers_warning = Warnung zu verbundenen Trackern
settings-general-interface-connected_trackers_warning-description = Diese Option zeigt jedes Mal ein Pop-up-Fenster an, wenn Sie versuchen, SlimeVR zu beenden, während ein oder mehrere Tracker verbunden sind. Es erinnert Sie daran, die Tracker auszuschalten, um die Akkulaufzeit zu verlängern.
settings-general-interface-connected_trackers_warning-label = Warnung vor verbundenen Trackern beim Verlassen

## Behavior settings

settings-general-interface-dev_mode = Entwicklermodus
settings-general-interface-dev_mode-label = Entwicklermodus
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
settings-serial-save_logs = In Datei speichern
settings-serial-send_command = Senden
settings-serial-send_command-placeholder = Befehl...
settings-serial-send_command-warning = <b>Warnung:</b> Das Ausführen serieller Befehle kann zu Datenverlust führen oder die Tracker unbrauchbar machen.
settings-serial-send_command-warning-ok = Ich weiß, was ich tue
settings-serial-send_command-warning-cancel = Abbruch

## OSC VRChat settings

settings-osc-vrchat-enable = Aktivieren
settings-osc-vrchat-enable-description = Ein- und Ausschalten des Sendens und Empfangen von Daten.
settings-osc-vrchat-enable-label = Aktivieren
settings-osc-vrchat-network = Netzwerk-Ports

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

