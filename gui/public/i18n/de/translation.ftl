# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Verbindung zum Server wird hergestellt...
websocket-connection_lost = Verbindung zum Server verloren. Versuche Verbindung wiederherzustellen ...

## Update notification

version_update-title = Neue Version verfügbar: { $version }
version_update-description = Wenn Sie auf "Aktualisieren" klicken, wird der SlimeVR-Installationsassistent für dich heruntergeladen.
version_update-update = Aktualisieren
version_update-close = Schließen

## Tips

tips-find_tracker = Sie sind sich nicht sicher, welcher Tracker welcher ist? Schütteln Sie einen Tracker, um den zugehörigen Eintrag hervorzuheben.
tips-do_not_move_heels = Stellen Sie sicher, dass Sie Ihre Fersen während der Aufnahme nicht bewegen!
tips-file_select = Dateien per Drag & Drop verwenden oder <u>durchsuchen</u>
tips-tap_setup = Sie können langsam 2 Mal auf Ihren Tracker tippen, um ihn auszuwählen, anstatt ihn aus dem Menü auszuwählen.

## Body parts

body_part-NONE = Nicht zugewiesen
body_part-HEAD = Kopf
body_part-NECK = Hals
body_part-RIGHT_SHOULDER = Rechte Schulter
body_part-RIGHT_UPPER_ARM = Rechter Oberarm
body_part-RIGHT_LOWER_ARM = Rechter Unterarm
body_part-RIGHT_HAND = Rechte Hand
body_part-RIGHT_UPPER_LEG = Rechter Oberschenkel
body_part-RIGHT_LOWER_LEG = Rechter Unterschenkel
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
body_part-LEFT_LOWER_LEG = Linker Unterschenkel
body_part-LEFT_FOOT = Linker Fuß

## Proportions

skeleton_bone-NONE = Keine
skeleton_bone-HEAD = Kopfverschiebung
skeleton_bone-NECK = Halslänge
skeleton_bone-torso_group = Oberkörperhöhe
skeleton_bone-UPPER_CHEST = Obere Brustlänge
skeleton_bone-CHEST_OFFSET = Brustversatz
skeleton_bone-CHEST = Brustabstand
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
reset-full = Reset
reset-mounting = Befestigungs-Reset
reset-yaw = Horizontaler Reset

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
navbar-body_proportions = Körpermaße
navbar-trackers_assign = Tracker-Zuordnung
navbar-mounting = Tracker-Ausrichtung
navbar-onboarding = Einrichtungs-Assistent
navbar-settings = Einstellungen

## Biovision hierarchy recording

bvh-start_recording = BVH aufnehmen
bvh-recording = Aufnahme läuft...

## Tracking pause

tracking-unpaused = Tracking pausieren
tracking-paused = Tracking fortsetzen

## Widget: Overlay settings

widget-overlay = Visualisierung
widget-overlay-is_visible_label = Visualisierung in SteamVR anzeigen
widget-overlay-is_mirrored_label = Visualisierung spiegeln

## Widget: Drift compensation

widget-drift_compensation-clear = Driftkompensation zurücksetzen

## Widget: Clear Reset Mounting

widget-clear_mounting = Befestigungs-Reset zurücksetzen

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
widget-imu_visualizer-rotation_raw = Rohe Drehung
widget-imu_visualizer-rotation_preview = Vorschau
widget-imu_visualizer-rotation_hide = Ausblenden

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
tracker-rotation-overriden = (von Befestigungs-Reset überschrieben)

## Tracker information

tracker-infos-manufacturer = Hersteller
tracker-infos-display_name = Anzeigename
tracker-infos-custom_name = Benutzerdefinierter Name
tracker-infos-url = Tracker-Adresse
tracker-infos-version = Firmware-Version
tracker-infos-hardware_rev = Hardware-Version
tracker-infos-hardware_identifier = Hardware-ID
tracker-infos-imu = IMU-Sensor
tracker-infos-board_type = Platine

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
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Trackername
tracker-settings-name_section-description = Geben Sie ihm einen süßen Spitznamen :)
tracker-settings-name_section-placeholder = NightyBeast's linkes Bein

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
settings-sidebar-tracker_mechanics = Tracker-Mechanik
settings-sidebar-fk_settings = FK-Einstellungen
settings-sidebar-gesture_control = Gestensteuerung
settings-sidebar-interface = Bedienoberfläche
settings-sidebar-osc_router = OSC-Router
settings-sidebar-osc_trackers = VRChat OSC-Tracker
settings-sidebar-utils = Werkzeuge
settings-sidebar-serial = Serielle Konsole
settings-sidebar-appearance = Erscheinungsbild
settings-sidebar-notifications = Benachrichtigungen

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
settings-general-steamvr-trackers-feet = Füße
settings-general-steamvr-trackers-knees = Knie
settings-general-steamvr-trackers-elbows = Ellbogen
settings-general-steamvr-trackers-hands = Hände

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
settings-general-tracker_mechanics-drift_compensation = Drift-Kompensierung
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Kompensiert IMU Drift auf der Gier-Achse durch Anwenden einer invertierten Rotation.
    Ändern Sie die Menge der Kompensierung und die Anzahl der Resets, welche für die Berechnung genutzt werden.
settings-general-tracker_mechanics-drift_compensation-enabled-label = Drift-Kompensierung
settings-general-tracker_mechanics-drift_compensation-amount-label = Kompensierungsmenge
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Nutze die letzten x Resets

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
settings-general-fk_settings-leg_fk-reset_mounting_feet-description = Aktiviert das Zurücksetzen der Fußausrichtung, indem Sie auf die Zehenspitzen stehen.
settings-general-fk_settings-leg_fk-reset_mounting_feet = Fußausrichtung zurücksetzen
settings-general-fk_settings-arm_fk = Arm-Tracking
settings-general-fk_settings-arm_fk-description = Ändern Sie die Art und Weise, wie die Arme berechnet werden.
settings-general-fk_settings-arm_fk-force_arms = Arme vom VR-Headset erzwingen
settings-general-fk_settings-arm_fk-reset_mode-description = Ändern Sie, welche Armhaltung für den Befestigungs-Reset erwartet wird.
settings-general-fk_settings-arm_fk-back = nach Hinten
settings-general-fk_settings-arm_fk-back-description = Der Standardmodus, bei dem die Oberarme nach hinten und die Unterarme nach vorne gehen.
settings-general-fk_settings-arm_fk-tpose_up = T-Pose (oben)
settings-general-fk_settings-arm_fk-tpose_up-description = Erwartet, dass deine Arme während des vollständigen Zurücksetzens seitlich nach unten gerichtet sind und während des Befestigungs-Reset um 90 Grad nach außen gerichtet sind.
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
settings-general-fk_settings-vive_emulation-title = Vive-Simulierung
settings-general-fk_settings-vive_emulation-description = Simuliere die Tracking-Probleme, welche bei Vive-Trackern auftreten. Dies ist ein Scherz und verschlechtert das Tracking.
settings-general-fk_settings-vive_emulation-label = Vive-Simulierung

## Gesture control settings (tracker tapping)

settings-general-gesture_control = Gestensteuerung
settings-general-gesture_control-subtitle = Reset durch Antippen
settings-general-gesture_control-description = Erlaubt Reset durch das Antippen eines Trackers auszulösen. Der höchste Tracker auf dem Oberkörper wird für schnelle Resets genutzt, der höchste Tracker auf dem linken Bein wird für Reset genutzt und der höchste Tracker auf dem rechten Bein wird für Befestigungs-Reset genutzt. Das Antippen muss innerhalb von 0.5 Sekunden erfolgen, um erkannt zu werden.
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
settings-general-gesture_control-mountingResetEnabled = Antippen für Befestigungs-Reset
settings-general-gesture_control-mountingResetDelay = Befestigungs-Reset-Verzögerung
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
    Das bedeutet, dass Wi-Fi- und Kalibrierungseinstellungen <b>verloren gehen!</b>
settings-serial-factory_reset-warning-ok = Ich weiß, was ich tue
settings-serial-factory_reset-warning-cancel = Abbruch
settings-serial-get_infos = Informationen abrufen
settings-serial-serial_select = Wählen Sie einen seriellen Anschluss
settings-serial-auto_dropdown_item = Auto

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
settings-osc-vrchat-description = Ändern Sie VRChat-spezifische Einstellungen, um Headset- und Tracker-Daten für FBT zu empfangen und zu senden (funktioniert auch im Standalone-Modus auf der Meta Quest).
settings-osc-vrchat-enable = Aktivieren
settings-osc-vrchat-enable-description = Ein- und Ausschalten des Sendens und Empfangen von Daten
settings-osc-vrchat-enable-label = Aktivieren
settings-osc-vrchat-network = Netzwerk-Ports
settings-osc-vrchat-network-description = Festlegen der Ports zum Empfangen und Senden von Daten an VRChat
settings-osc-vrchat-network-port_in =
    .label = Eingangsport
    .placeholder = Eingangsport (Standard: 9001)
settings-osc-vrchat-network-port_out =
    .label = Ausgangsport
    .placeholder = Ausgangsport (Standard: 9000)
settings-osc-vrchat-network-address = Netzwerkadresse
settings-osc-vrchat-network-address-description = Wählen Sie, an welche Adresse die Daten an VRChat gesendet werden sollen (überprüfen Sie Ihre WLAN-Einstellungen auf Ihrem Gerät)
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
settings-osc-vmc-vrm-model_unloaded = Kein Modell geladen
settings-osc-vmc-vrm-model_loaded =
    { $titled ->
        [true] Modell geladen: { $name }
       *[other] Unbenanntes Modell geladen
    }
settings-osc-vmc-vrm-file_select = Modell per Drag & Drop laden oder <u>durchsuchen</u>
settings-osc-vmc-anchor_hip = Hüftenverankerung
settings-osc-vmc-anchor_hip-description = Die Hüften-Verankerung für das Tracking ist nützlich für VTubing im Sitzen. Beim Deaktivieren muss ein VRM-Model geladen werden.
settings-osc-vmc-anchor_hip-label = Hüftenverankerung

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
onboarding-wifi_creds = WLAN-Zugangsdaten eingeben
# This cares about multilines
onboarding-wifi_creds-description =
    Die Tracker nutzen diese Zugangsdaten, um sich mit dem WLAN zu verbinden.
    Bitte verwenden Sie die Zugangsdaten, mit denen ihr PC gerade verbunden sind.
    Dieses WLAN-Netzwerk muss ein 2.4 GHz-Netzwerk sein.
onboarding-wifi_creds-skip = WLAN-Zugangsdaten überspringen
onboarding-wifi_creds-submit = Weiter!
onboarding-wifi_creds-ssid =
    .label = WLAN-Name
    .placeholder = WLAN-Name eingeben
onboarding-wifi_creds-password =
    .label = Passwort
    .placeholder = Passwort eingeben

## Mounting setup

onboarding-reset_tutorial-back = Zurück zur Trackerausrichtung
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
    Tippen Sie { $taps } mal auf den markierten Tracker um einen Befestigungs-Reset auszulösen.
    
    Ein Befestigungs-Reset hilft dabei, die Tracker neu auszurichten, so wie diese tatsächlich an Ihnen angebracht sind. Zum Beispiel, wenn Sie ein Tracker versehentlich verschoben haben und dessen Orientierung sich stark verändert hat.
    
    Sie müssen sich in einer "Skifahren"-Pose, wie im Befestigungs-Assistenten gezeigt wird, befinden. Nach dem Auslösen wird der Reset nach 3 Sekunden (konfigurierbar) durchgeführt.

## Setup start

onboarding-home = Willkommen zu SlimeVR
onboarding-home-start = Los geht’s!

## Enter VR part of setup

onboarding-enter_vr-back = Zurück zur Trackerzuweisung
onboarding-enter_vr-title = Zeit für VR!
onboarding-enter_vr-description = Ziehen Sie alle Tracker an und betreten Sie dann VR!
onboarding-enter_vr-ready = Ich bin bereit!

## Setup done

onboarding-done-title = Alles eingerichtet!
onboarding-done-description = Genießen Sie die Fullbody-Erfahrung
onboarding-done-close = Einrichtung schließen

## Tracker connection setup

onboarding-connect_tracker-back = Zurück zu WLAN-Zugangsdaten
onboarding-connect_tracker-title = Verbinde Tracker
onboarding-connect_tracker-description-p0 = Nun zum unterhaltsamen Teil, verbinde alle Tracker!
onboarding-connect_tracker-description-p1 = Verbinden Sie einfach alle Tracker, die noch nicht verbunden sind, über einen USB-Anschluss.
onboarding-connect_tracker-issue-serial = Ich habe Schwierigkeiten die Tracker zu verbinden!
onboarding-connect_tracker-usb = USB Tracker
onboarding-connect_tracker-connection_status-none = Suche nach Trackern
onboarding-connect_tracker-connection_status-serial_init = Verbindung zum seriellen Gerät wird hergestellt
onboarding-connect_tracker-connection_status-provisioning = Sende WLAN-Zugangsdaten
onboarding-connect_tracker-connection_status-connecting = Sende WLAN-Zugangsdaten
onboarding-connect_tracker-connection_status-looking_for_server = Suche nach Server
onboarding-connect_tracker-connection_status-connection_error = Es kann keine WLAN-Verbindung hergestellt werden
onboarding-connect_tracker-connection_status-could_not_find_server = Server konnte nicht gefunden werden
onboarding-connect_tracker-connection_status-done = Verbindung zum Server hergestellt.
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers =
    { $amount ->
        [0] Kein Tracker
        [one] 1 Tracker
       *[other] { $amount } Tracker
    } verbunden
onboarding-connect_tracker-next = Ich habe alle meine Tracker verbunden.

## Tracker calibration tutorial

onboarding-calibration_tutorial = IMU-Kalibrierungs-Tutorial
onboarding-calibration_tutorial-subtitle = Dies wird dazu beitragen, das Driften der Tracker zu reduzieren!
onboarding-calibration_tutorial-description = Jedes Mal, wenn Sie Ihre Tracker einschalten, müssen diese für einen Moment auf einer ebenen Oberfläche ruhen, um sie zu kalibrieren. Lassen Sie uns dies nun tun, indem Sie auf die Schaltfläche "Kalibrieren" klicken. <b>Verschieben Sie die Tracker nicht!</b>
onboarding-calibration_tutorial-calibrate = Ich habe meine Tracker auf den Tisch gelegt
onboarding-calibration_tutorial-status-waiting = Wir warten auf Sie
onboarding-calibration_tutorial-status-calibrating = Kalibriere
onboarding-calibration_tutorial-status-success = Gut!
onboarding-calibration_tutorial-status-error = Der Tracker wurde bewegt

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
    { $assigned } von { $trackers ->
        [one] 1 Tracker
       *[other] { $trackers } Tracker
    } zugewiesen
onboarding-assign_trackers-advanced = Erweiterte Zuweisungspositionen anzeigen
onboarding-assign_trackers-next = Ich habe alle Tracker zugewiesen

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
# Italized text
onboarding-choose_mounting-auto_mounting-label = Experimentell
onboarding-choose_mounting-auto_mounting-description = Dadurch werden die Befestigungsausrichtungen für alle Ihrer Tracker automatisch aus 2 Posen erkannt
onboarding-choose_mounting-manual_mounting = Manuelle Befestigungsposition
# Italized text
onboarding-choose_mounting-manual_mounting-label = Empfohlen
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
onboarding-automatic_mounting-preparation-step-0 = 1. Stehen Sie aufrecht mit Ihren Armen an den Seiten.
onboarding-automatic_mounting-preparation-step-1 = 2. Drücken Sie die Schaltfläche "Reset" und warten Sie 3 Sekunden, bevor die Tracker zurückgesetzt werden.
onboarding-automatic_mounting-put_trackers_on-title = Legen Sie Ihre Tracker an
onboarding-automatic_mounting-put_trackers_on-description = Um die Drehung der Tracker zu kalibrieren, werden die Tracker verwendet, welche Sie gerade zugewiesen haben. Ziehen Sie alle Ihre Tracker an, in der Abbildung rechts können sie sehen um welchen Tracker es sich handelt.
onboarding-automatic_mounting-put_trackers_on-next = Ich habe alle meine Tracker angelegt

## Tracker proportions method choose

onboarding-choose_proportions = Welche Kalibrierungsmethode ist zu verwenden?
# Multiline string
onboarding-choose_proportions-description =
    Die Körperproportionen werden benutzt um die Maße Ihres Körpers zu kennen. Diese werden benötigt um die Positionen der Tracker zu berechnen.
    Wenn die Körperproportionen nicht mit denen Ihres Körpers übereinstimmen, wird die Tracking-Präzision schlechter sein und Sie werden ein rutschen/gleiten der Fuß-Tracker feststellen oder die Bewegungen des Avatars stimmen nicht gut mit denen von Ihnen überein.
onboarding-choose_proportions-auto_proportions = Automatische Proportionen
# Italized text
onboarding-choose_proportions-auto_proportions-subtitle = Empfohlen
onboarding-choose_proportions-auto_proportions-descriptionv3 =
    Dies wird versuchen, Ihre Proportionen mit Hilfe einer Bewegungsaufnahme zu bestimmen, welche von einem Algorithmus verarbeitet wird.
    
    <b>Dazu muss Ihr Headset (HMD) mit SlimeVR verbunden sein und Sie müssen es an haben!</b>
onboarding-choose_proportions-manual_proportions = Manuelle Körperproportionen
# Italized text
onboarding-choose_proportions-manual_proportions-subtitle = Für kleine Anpassungen
onboarding-choose_proportions-manual_proportions-description = Auf diese Weise können Sie Ihre Proportionen manuell anpassen, indem Sie diese direkt ändern
onboarding-choose_proportions-export = Proportionen exportieren
onboarding-choose_proportions-import = Proportionen importieren
onboarding-choose_proportions-import-success = Importiert
onboarding-choose_proportions-import-failed = Fehlgeschlagen
onboarding-choose_proportions-file_type = Körperproportions-Datei

## Tracker manual proportions setup

onboarding-manual_proportions-back = Gehen Sie zurück zum Reset-Tutorial
onboarding-manual_proportions-title = Manuelle Körperproportionen
onboarding-manual_proportions-precision = Feinanpassung
onboarding-manual_proportions-auto = Automatische Kalibrierung
onboarding-manual_proportions-ratio = Anpassung nach Proportionen

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
onboarding-automatic_proportions-check_height-title = Überprüfen Sie Ihre Körpergröße
onboarding-automatic_proportions-check_height-description = Wir benutzen die Höhe des Headsets (HMD) als eine Schätzung für Ihre tatsächliche Grösse, doch es ist besser dass Sie diese selbst überprüfen!
# All the text is in bold!
onboarding-automatic_proportions-check_height-calculation_warning = Bitte betätigen Sie auf den Knopf während Sie <u>aufrecht</u> stehen, um Ihre Körpergröße zu berechnen. Sie haben 3 Sekunden Zeit, nachdem Sie auf den Knopf gedrückt haben.
onboarding-automatic_proportions-check_height-fetch_height = Ich stehe!
# Context is that the height is unknown
onboarding-automatic_proportions-check_height-unknown = Unbekannt
# Shows an element below it
onboarding-automatic_proportions-check_height-hmd_height1 = Ihre Headset-Höhe ist
# Shows an element below it
onboarding-automatic_proportions-check_height-height1 = Ihre tatsächliche Körpergröße ist
onboarding-automatic_proportions-check_height-next_step = Werte sind korrekt
onboarding-automatic_proportions-start_recording-title = Bereiten Sie sich auf ein paar Bewegungen vor
onboarding-automatic_proportions-start_recording-description = Wir werden nun einige bestimmte Posen und Bewegungen aufnehmen. Diese werden im nächsten Bildschirm angezeigt. Bereiten Sie sicht darauf vor, wenn Sie den Knopf drücken!
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
onboarding-automatic_proportions-error_modal =
    <b>Warnung:</b> Beim Schätzen der Proportionen wurde ein Fehler festgestellt!
    Bitte <docs>prüfen Sie die Dokumentation</docs> oder treten sie unserem <discord>Discord Server</discord> bei, um Hilfe zu bekommen. ^_^
onboarding-automatic_proportions-error_modal-confirm = Verstanden!

## Home

home-no_trackers = Keine Tracker erkannt oder zugewiesen

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
