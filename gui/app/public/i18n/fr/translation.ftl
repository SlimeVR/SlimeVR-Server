# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Chargement...
websocket-connection_lost = Connexion avec le serveur perdue. Reconnexion...
websocket-connection_lost-desc = Il semble que le serveur SlimeVR ait planté. Vérifiez les logs et redémarrez le programme.
websocket-timedout = Impossible de se connecter au serveur
websocket-timedout-desc = Il semble que le serveur SlimeVR ait planté ou que le délai d'attente ait expiré. Vérifiez les logs et redémarrez le programme.
websocket-error-close = Quitter SlimeVR
websocket-error-logs = Ouvrir le dossier des logs

## Update notification

version_update-title = Nouvelle version disponible: { $version }
version_update-description = Cliquer sur « { version_update-update } » téléchargera l'installateur SlimeVR pour vous.
version_update-update = Mettre à jour
version_update-close = Fermer

## Tips

tips-find_tracker = Impossible de différencier vos capteurs ? Secouez-en un pour qu'il soit mis en évidence.
tips-do_not_move_heels = Assurez-vous de ne pas bouger vos pieds pendant l'enregistrement !
tips-file_select = Glissez et déposez des fichiers à utiliser, ou <u>parcourir</u>.
tips-failed_webgl = Échec de l'initialisation de WebGL.

## Units

unit-meter = Mètre
unit-foot = Pied
unit-inch = Pouce
unit-cm = cm

## Body parts

body_part-NONE = Non-attribué
body_part-HEAD = Tête
body_part-NECK = Cou
body_part-RIGHT_SHOULDER = Épaule droite
body_part-RIGHT_UPPER_ARM = Bras droit
body_part-RIGHT_LOWER_ARM = Avant-bras droit
body_part-RIGHT_HAND = Main droite
body_part-RIGHT_UPPER_LEG = Cuisse droite
body_part-RIGHT_LOWER_LEG = Cheville droite
body_part-RIGHT_FOOT = Pied droit
body_part-UPPER_CHEST = Poitrine supérieure
body_part-CHEST = Poitrine
body_part-WAIST = Taille
body_part-HIP = Hanche
body_part-LEFT_SHOULDER = Épaule gauche
body_part-LEFT_UPPER_ARM = Bras gauche
body_part-LEFT_LOWER_ARM = Avant-bras gauche
body_part-LEFT_HAND = Main gauche
body_part-LEFT_UPPER_LEG = Cuisse gauche
body_part-LEFT_LOWER_LEG = Cheville gauche
body_part-LEFT_FOOT = Pied gauche
body_part-LEFT_THUMB_METACARPAL = Métacarpien du pouce gauche
body_part-LEFT_THUMB_PROXIMAL = Pouce gauche proximal
body_part-LEFT_THUMB_DISTAL = Pouce gauche distal
body_part-LEFT_INDEX_PROXIMAL = Index gauche proximal
body_part-LEFT_INDEX_INTERMEDIATE = Index gauche intermédiaire
body_part-LEFT_INDEX_DISTAL = Index gauche distal
body_part-LEFT_MIDDLE_PROXIMAL = Majeur gauche proximal
body_part-LEFT_MIDDLE_INTERMEDIATE = Majeur gauche intermédiaire
body_part-LEFT_MIDDLE_DISTAL = Majeur gauche distal
body_part-LEFT_RING_PROXIMAL = Annulaire gauche proximal
body_part-LEFT_RING_INTERMEDIATE = Annulaire gauche intermédiaire
body_part-LEFT_RING_DISTAL = Annulaire gauche distal
body_part-LEFT_LITTLE_PROXIMAL = Auriculaire gauche proximal
body_part-LEFT_LITTLE_INTERMEDIATE = Auriculaire gauche intermédiaire
body_part-LEFT_LITTLE_DISTAL = Auriculaire gauche distal
body_part-RIGHT_THUMB_METACARPAL = Métacarpien du pouce droit
body_part-RIGHT_THUMB_PROXIMAL = Pouce droit proximal
body_part-RIGHT_THUMB_DISTAL = Pouce droit distal
body_part-RIGHT_INDEX_PROXIMAL = Index droit proximal
body_part-RIGHT_INDEX_INTERMEDIATE = Index droit intermédiaire
body_part-RIGHT_INDEX_DISTAL = Index droit distal
body_part-RIGHT_MIDDLE_PROXIMAL = Majeur droit proximal
body_part-RIGHT_MIDDLE_INTERMEDIATE = Majeur droit intermédiaire
body_part-RIGHT_MIDDLE_DISTAL = Majeur droit distal
body_part-RIGHT_RING_PROXIMAL = Annulaire droit proximal
body_part-RIGHT_RING_INTERMEDIATE = Annulaire droit intermédiaire
body_part-RIGHT_RING_DISTAL = Annulaire droit distal
body_part-RIGHT_LITTLE_PROXIMAL = Auriculaire droit proximal
body_part-RIGHT_LITTLE_INTERMEDIATE = Auriculaire droit intermédiaire
body_part-RIGHT_LITTLE_DISTAL = Auriculaire droit distal

## BoardType

board_type-UNKNOWN = Inconnu
board_type-NODEMCU = NodeMCU
board_type-CUSTOM = Carte personnalisée
board_type-WROOM32 = WROOM32
board_type-WEMOSD1MINI = Wemos D1 Mini
board_type-TTGO_TBASE = TTGO T-Base
board_type-ESP01 = ESP-01
board_type-SLIMEVR = SlimeVR
board_type-SLIMEVR_DEV = Carte de développement SlimeVR
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
board_type-GLOVE_IMU_SLIMEVR_DEV = SlimeVR Dev IMU Glove
board_type-GESTURES = Gestes
board_type-ESP32S3_SUPERMINI = ESP32-S3 Supermini
board_type-GENERIC_NRF = nRF Générique
board_type-SLIMEVR_BUTTERFLY_DEV = SlimeVR Dev Butterfly
board_type-SLIMEVR_BUTTERFLY = SlimeVR Butterfly

## Proportions

skeleton_bone-NONE = Aucun
skeleton_bone-HEAD = Décalage de la tête
skeleton_bone-HEAD-desc =
    Ceci est la distance entre votre casque et le milieu de votre tête.
    Pour l’ajuster, secouez votre tête de gauche à droite comme si vous disiez non et modifiez-la
    jusqu’à ce que vos capteurs bougent le moins possible.
skeleton_bone-NECK = Longueur du cou
skeleton_bone-NECK-desc =
    Ceci est la distance entre le milieu de votre tête et la base de votre cou.
    Pour l’ajuster, hochez votre tête de haut en bas ou inclinez votre tête de gauche à droite et modifiez-la
    jusqu’à ce que vos capteurs bougent le moins possible.
skeleton_bone-torso_group = Longueur du torse
skeleton_bone-torso_group-desc =
    Ceci est la distance entre la base de votre cou et vos hanches.
    Pour l’ajuster, tenez-vous debout et modifiez-la jusqu’à ce que
    vos hanches virtuelles soient alignées avec vos vraies hanches.
skeleton_bone-UPPER_CHEST = Longueur de la poitrine supérieure
skeleton_bone-UPPER_CHEST-desc =
    Ceci la distance entre la base de votre cou et le milieu de votre poitrine.
    Pour l’ajuster, ajustez correctement la longueur de votre torse et modifiez-la dans différentes positions
    (assis, penché, allongé, etc.) jusqu’à ce que votre colonne vertébrale virtuelle corresponde à votre colonne vertébrale réelle.
skeleton_bone-CHEST = Longueur de la poitrine
skeleton_bone-CHEST-desc =
    Ceci est la distance entre le milieu de votre poitrine et le milieu de votre colonne vertébrale.
    Pour l’ajuster, ajustez correctement la longueur de votre torse et modifiez-la dans différentes positions
    (assis, penché, allongé, etc.) jusqu’à ce que votre colonne vertébrale virtuelle corresponde à votre colonne vertébrale réelle.
skeleton_bone-WAIST = Longueur de la taille
skeleton_bone-WAIST-desc =
    Ceci est la distance entre le milieu de votre colonne vertébrale et votre nombril.
    Pour l’ajuster, ajustez correctement la longueur de votre torse et modifiez-la dans différentes positions
    (assis, penché, allongé, etc.) jusqu’à ce que votre colonne vertébrale virtuelle corresponde à votre colonne vertébrale réelle.
skeleton_bone-HIP = Longueur des hanches
skeleton_bone-HIP-desc =
    Ceci est la distance entre votre nombril et vos hanches
    Pour l’ajuster, ajustez correctement la longueur de votre torse et modifiez-la dans différentes positions
    (assis, penché, allongé, etc.) jusqu’à ce que votre colonne vertébrale virtuelle corresponde à votre colonne vertébrale réelle.
skeleton_bone-HIPS_WIDTH = Largeur des hanches
skeleton_bone-HIPS_WIDTH-desc =
    Ceci est la distance entre vos deux jambes.
    Pour l’ajuster, effectuez une réinitialisation complète avec vos jambes droites et modifiez-la jusqu’à ce que
    Vos jambes virtuelles soient au même niveau horizontalement que vos vraies jambes.
skeleton_bone-leg_group = Longueur des jambes
skeleton_bone-leg_group-desc =
    Ceci est la distance entre vos hanches et vos pieds.
    Pour l’ajuster, ajustez correctement la longueur du torse et modifiez-la
    jusqu’à ce que vos pieds virtuels soient au même niveau que vos pieds réels.
skeleton_bone-UPPER_LEG = Longueur des jambes supérieures
skeleton_bone-UPPER_LEG-desc =
    Ceci est la distance entre vos hanches et vos genoux.
    Pour l’ajuster, ajustez correctement la longueur des jambes et modifiez-la
    jusqu’à ce que vos genoux virtuels soient au même niveau que vos genoux réels.
skeleton_bone-LOWER_LEG = Longueur des jambes inférieures
skeleton_bone-LOWER_LEG-desc =
    Ceci est la distance entre vos genoux et vos chevilles.
    Pour l’ajuster, ajustez correctement la longueur des jambes et modifiez-la
    jusqu’à ce que vos genoux virtuels soient au même niveau que vos genoux réels.
skeleton_bone-FOOT_LENGTH = Longueur des pieds
skeleton_bone-FOOT_LENGTH-desc =
    Ceci est la distance entre vos chevilles et vos orteils.
    Pour l’ajuster, allez sur la pointe des pieds et modifiez-la jusqu’à ce que vos pieds virtuels restent en place.
skeleton_bone-FOOT_SHIFT = Décalage des pieds
skeleton_bone-FOOT_SHIFT-desc =
    Ceci est la distance horizontale entre votre genou et votre cheville.
    Il tient compte du fait que le bas de vos jambes recule lorsque vous vous tenez droit.
    Pour l’ajuster, réglez la longueur des pieds à 0, effectuez une réinitialisation complète et modifiez-la jusqu’à ce que vos
    pieds virtuels s’alignent avec le milieu de vos chevilles.
skeleton_bone-SHOULDERS_DISTANCE = Distance des épaules
skeleton_bone-SHOULDERS_DISTANCE-desc =
    Ceci est la distance verticale entre la base de votre cou et vos épaules.
    Pour l’ajuster, réglez la longueur des bras à 0 et modifiez-la jusqu’à ce que vos capteurs de coude virtuels
    soient alignés verticalement avec vos vraies épaules.
skeleton_bone-SHOULDERS_WIDTH = Largeur des épaules
skeleton_bone-SHOULDERS_WIDTH-desc =
    Ceci est la distance horizontale entre la base de votre cou et vos épaules.
    Pour l’ajuster, réglez la longueur des bras à 0 et modifiez-la jusqu’à ce que vos capteurs de coude virtuels
    soient alignés horizontalement avec vos vraies épaules.
skeleton_bone-arm_group = Longueur des bras
skeleton_bone-arm_group-desc =
    Ceci est la distance entre vos épaules et vos poignets.
    Pour l’ajuster, ajustez correctement la distance des épaules, réglez la distance Y des mains
    à 0 et modifiez-la jusqu’à ce que vos capteurs de main soient alignés avec vos poignets.
skeleton_bone-UPPER_ARM = Longueur des bras supérieurs
skeleton_bone-UPPER_ARM-desc =
    Ceci est la distance entre vos épaules et vos coudes.
    Pour l’ajuster, ajustez correctement la longueur des bras et modifiez-la jusqu’à ce que
    vos capteurs de coude soient alignés avec vos vrais coudes.
skeleton_bone-LOWER_ARM = Longueur des avant-bras
skeleton_bone-LOWER_ARM-desc =
    Ceci est la distance entre vos coudes et vos poignets.
    Pour l’ajuster, ajustez correctement la longueur des bras et modifiez-la jusqu’à ce que
    vos capteurs de coude soient alignés avec vos vrais coudes.
skeleton_bone-HAND_Y = Distance Y des mains
skeleton_bone-HAND_Y-desc =
    Ceci est la distance verticale entre vos poignets et le milieu de vos main.
    Pour l’ajuster pour la capture de mouvement, ajustez correctement la longueur des bras et modifiez-la jusqu’à ce que vos
    capteurs de main soient alignés verticalement avec le milieu de vos mains.
    Pour l’ajuster pour le suivi des coudes à partir de vos manettes, réglez la longueur des bras à 0 et
    modifiez-la jusqu’à ce que vos capteurs de coude soient alignés verticalement avec vos poignets.
skeleton_bone-HAND_Z = Distance Z des mains
skeleton_bone-HAND_Z-desc =
    Ceci est la distance horizontale entre vos poignets et le milieu de votre main.
    Pour l’ajuster pour la capture de mouvement, réglez-la à 0.
    Pour l’ajuster pour le suivi du coude à partir de vos manettes, réglez la longueur des bras à 0 et
    modifiez-la jusqu’à ce que vos capteurs de coude soient alignés horizontalement avec vos poignets.

## Tracker reset buttons

reset-reset_all = Réinitialiser toutes les proportions
reset-reset_all_warning-reset = Réinitialiser les proportions
reset-reset_all_warning-cancel = Annuler
reset-full = Réinitialisation complète
reset-mounting = Réinitialiser l'alignement
reset-mounting-feet = Réinitialiser l'alignement des pieds
reset-mounting-fingers = Réinitialiser l'alignement des doigts
reset-yaw = Réinitialisation horizontale
reset-error-mounting-need_full_reset = Nécessite une réinitialisation complète avant de le monter
reset-error-yaw-need_full_reset = Nécessite une réinitialisation complète avant une réinitialisation horizontale

## Serial detection stuff

serial_detection-new_device-p0 = Nouveau périphérique détecté !
serial_detection-new_device-p1 = Entrez vos identifiants Wi-Fi !
serial_detection-new_device-p2 = Veuillez sélectionner quoi en faire
serial_detection-open_wifi = Connecter au Wi-Fi
serial_detection-open_serial = Ouvrir la console série
serial_detection-submit = Soumettre !
serial_detection-close = Fermer

## Navigation bar

navbar-home = Accueil
navbar-body_proportions = Proportions du corps
navbar-trackers_assign = Attribution des capteurs
navbar-mounting = Alignement des capteurs
navbar-onboarding = Assistant de configuration
navbar-settings = Réglages
navbar-connect_trackers = Connecter les capteurs

## Biovision hierarchy recording

bvh-start_recording = Enregistrer BVH
bvh-stop_recording = Sauvegarder l’enregistrement BVH
bvh-recording = Enregistrement...
bvh-save_title = Sauvegarder l’enregistrement BVH

## Tracking pause

tracking-unpaused = Pause de la capture
tracking-paused = Arrêter la pause de la capture

## Widget: Overlay settings


## Widget: Drift compensation


## Widget: Clear Mounting calibration


## Widget: Developer settings

widget-developer_mode = Mode développeur
widget-developer_mode-high_contrast = Contraste élevé
widget-developer_mode-precise_rotation = Rotation précise
widget-developer_mode-fast_data_feed = Flux de données rapide
widget-developer_mode-sort_by_name = Trier par nom
widget-developer_mode-raw_slime_rotation = Rotation brute
widget-developer_mode-more_info = Plus d'informations

## Widget: IMU Visualizer

widget-imu_visualizer = Rotation
widget-imu_visualizer-preview = Aperçu
widget-imu_visualizer-hide = Masquer
widget-imu_visualizer-rotation_raw = Brute
widget-imu_visualizer-rotation_preview = Aperçu
widget-imu_visualizer-acceleration = Accélération
widget-imu_visualizer-position = Position
widget-imu_visualizer-stay_aligned = Garder Aligné

## Widget: Skeleton Visualizer


## Tracker status

tracker-status-none = Pas de statut
tracker-status-busy = Occupé
tracker-status-error = Erreur
tracker-status-disconnected = Déconnecté
tracker-status-occluded = Obstrué
tracker-status-ok = OK
tracker-status-timed_out = Délai expiré

## Tracker status columns

tracker-table-column-name = Nom
tracker-table-column-type = Type
tracker-table-column-battery = Batterie
tracker-table-column-ping = Ping
tracker-table-column-tps = TPS
tracker-table-column-temperature = Temp. °C
tracker-table-column-linear-acceleration = Accél. X/Y/Z
tracker-table-column-rotation = Rotation X/Y/Z
tracker-table-column-position = Position X/Y/Z
tracker-table-column-stay_aligned = Garder Aligné
tracker-table-column-url = URL

## Tracker rotation

tracker-rotation-front = Avant
tracker-rotation-front_left = Avant-Gauche
tracker-rotation-front_right = Avant-Droite
tracker-rotation-left = Gauche
tracker-rotation-right = Droite
tracker-rotation-back = Arrière
tracker-rotation-back_left = Arrière-Gauche
tracker-rotation-back_right = Arrière-Droite
tracker-rotation-custom = Personnalisé

## Tracker information

tracker-infos-manufacturer = Fabricant
tracker-infos-display_name = Nom
tracker-infos-custom_name = Nom personnalisé
tracker-infos-url = URL du capteur
tracker-infos-hardware_identifier = ID Matériel
tracker-infos-imu = Capteur IMU
tracker-infos-board_type = Carte principale
tracker-infos-network_version = Version du protocole
tracker-infos-magnetometer = Magnétomètre
tracker-infos-magnetometer-status-v1 =
    { $status ->
        [DISABLED] Désctivé
        [ENABLED] Activé
       *[NOT_SUPPORTED] Non pris en charge
    }
tracker-infos-packet_loss = Pertes de paquets
tracker-infos-packets_lost = Paquets perdus
tracker-infos-packets_received = Paquets reçus

## Tracker settings

tracker-settings-back = Retour à la liste des capteurs
tracker-settings-title = Paramètres du capteur
tracker-settings-assignment_section = Attribution du capteur
tracker-settings-assignment_section-description = Partie du corps à laquelle le capteur est attribué.
tracker-settings-assignment_section-edit = Changer
tracker-settings-mounting_section = Orientation du capteur
tracker-settings-mounting_section-description = Dans quelle direction pointe le capteur ?
tracker-settings-mounting_section-edit = Changer l'orientation
tracker-settings-use_mag = Autoriser l'utilisation du magnétomètre sur ce capteur
# Multiline!
tracker-settings-use_mag-description =
    Est-ce que ce capteur devrait utiliser son magnétomètre pour réduire la dérive lorsque l'utilisation du magnétomètre est autorisée ? <b>N'éteignez pas votre capteur pendant que vous changez cette option !</b>
    
    Vous devez d'abord autoriser l'utilisation du magnétomètre dans les paramètres. <magSetting>Cliquez ici pour y accéder</magSetting>.
tracker-settings-use_mag-label = Autoriser le magnétomètre
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Nom personalisé
tracker-settings-name_section-description = Donnez-lui un joli surnom :3
tracker-settings-name_section-placeholder = Patte gauche d'Erimel
tracker-settings-name_section-label = Nom personalisé
tracker-settings-forget = Oublier capteur
tracker-settings-forget-description = Supprime le capteur du serveur SlimeVR et l'empêche de s'y connecter jusqu'à ce que le serveur soit redémarré. La configuration du capteur ne sera pas perdue.
tracker-settings-forget-label = Oublier capteur
tracker-settings-update-unavailable-v2 = Aucune publication trouvée
tracker-settings-update-incompatible = Mise à jour impossible. Carte incompatible
tracker-settings-update-low-battery = Mise à jour impossible. Batterie inférieure à 50 %
tracker-settings-update-up_to_date = À jour
tracker-settings-update-blocked = Mise à jour non disponible. Aucune autre version disponible
tracker-settings-update = Mettre à jour maintenant
tracker-settings-update-title = Version du micrologiciel
tracker-settings-current-version = Actuel
tracker-settings-latest-version = Dernière version
tracker-settings-build-date = Date de build

## Tracker part card info

tracker-part_card-unassigned = Non-attribué

## Body assignment menu

body_assignment_menu = Où attribuer ce capteur ?
body_assignment_menu-description = Choisissez où attribuer ce capteur. Vous pouvez également gérer tous les capteurs à la fois au lieu d'un à la fois.
body_assignment_menu-manage_trackers = Gérer tous les capteurs
body_assignment_menu-unassign_tracker = Désattribuer

## Tracker assignment menu

# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Attention:</b> Un capteur au cou peut être mortel s'il est trop serré,
            la sangle pourrait couper la circulation à la tête !
tracker_selection_menu-neck_warning-done = Je suis conscient des risques
tracker_selection_menu-neck_warning-cancel = Annuler

## Mounting menu

mounting_selection_menu = Dans quelle direction pointe ce capteur ?
mounting_selection_menu-close = Fermer

## Sidebar settings

settings-sidebar-title = Réglages
settings-sidebar-general = Général
settings-sidebar-stay_aligned = Garder Aligné
settings-sidebar-interface = Interface
settings-sidebar-osc_trackers = Capteurs OSC VRChat
settings-sidebar-osc_vmc = VMC
settings-sidebar-utils = Utilitaires
settings-sidebar-serial = Console série
settings-sidebar-appearance = Apparence
settings-sidebar-home = Ecran d'accueil
settings-sidebar-checklist = Checklist de suivi
settings-sidebar-notifications = Notifications
settings-sidebar-behavior = Comportement
settings-sidebar-firmware-tool = Outil de micrologiciel DIY
settings-sidebar-vrc_warnings = Avertissements de configuration VRChat
settings-sidebar-advanced = Avancé

## SteamVR settings


## Tracker mechanics

settings-general-tracker_mechanics-filtering = Filtrage
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Choisissez le type de filtrage pour vos capteurs.
    La prédiction prédit les mouvements tandis que la fluidification rend les mouvements plus fluides.
settings-general-tracker_mechanics-filtering-type = Type de filtrage
settings-general-tracker_mechanics-filtering-type-none = Pas de filtrage
settings-general-tracker_mechanics-filtering-type-none-description = Utilisez les rotations telles quelles.
settings-general-tracker_mechanics-filtering-type-smoothing = Fluidification
settings-general-tracker_mechanics-filtering-type-smoothing-description = Fluidifie les mouvements mais ajoute un peu de latence.
settings-general-tracker_mechanics-filtering-type-prediction = Prédiction
settings-general-tracker_mechanics-filtering-type-prediction-description = Réduit la latence et rend les mouvements plus vifs, mais moins fluides.
settings-general-tracker_mechanics-filtering-amount = Intensité du filtrage
settings-general-tracker_mechanics-yaw-reset-smooth-time = Temps de fluidification de la réinitialisation horizontale (0s désactive la fluidification)
settings-general-tracker_mechanics-save_mounting_reset = Enregistrer la calibration de la réinitialisation automatique de l'alignement
settings-general-tracker_mechanics-save_mounting_reset-description =
    Enregistre les calibrations des réinitialisation automatiques d'alignement pour les capteurs entre les redémarrages.
    Utile lorsque vous portez une combinaison où les capteurs ne bougent pas entre les sessions. <b>Non recommandé pour les utilisateurs normaux !</b>
settings-general-tracker_mechanics-save_mounting_reset-enabled-label = Enregistrer la réinitialisation de l'alignement
settings-general-tracker_mechanics-use_mag_on_all_trackers = Utiliser le magnétomètre sur tous les capteurs IMU qui le prennent en charge
settings-general-tracker_mechanics-use_mag_on_all_trackers-description =
    Utilise le magnétomètre sur tous les capteurs dotés d'un micrologiciel compatible, réduisant ainsi la dérive dans des environnements magnétiques stables.
    Peut être désactivé par capteur dans les paramètres du capteur. <b>Ne fermez aucun des capteurs en changeant cette option !</b>
settings-general-tracker_mechanics-use_mag_on_all_trackers-label = Utiliser le magnétomètre sur les capteurs
settings-general-tracker_mechanics-trackers_over_usb = Capteurs via USB
settings-general-tracker_mechanics-trackers_over_usb-description = Permet de recevoir des données de suivi HID via USB. Assurez-vous que les capteurs connectés ont <b>la connexion via HID</b> activée !
settings-general-tracker_mechanics-trackers_over_usb-enabled-label = Permettre aux capteurs HID de se connecter directement via USB
settings-stay_aligned = Garder Aligné
settings-stay_aligned-description = Garder Aligné réduit la dérive en ajustant progressivement vos capteurs pour qu’ils correspondent à vos postures détendues.
settings-stay_aligned-setup-label = Configurer Garder Aligné
settings-stay_aligned-setup-description = Vous devez terminer « Configurer Garder Aligné » pour activer Garder Aligné.
settings-stay_aligned-enabled-label = Ajuster les capteurs
settings-stay_aligned-general-label = Général
settings-stay_aligned-relaxed_poses-label = Postures détendues
settings-stay_aligned-relaxed_poses-description = Garder Aligné utilise vos postures détendues pour garder vos capteurs alignés. Utilisez « Configurer Garder Aligné » pour mettre à jour ces poses.
settings-stay_aligned-relaxed_poses-standing = Ajuster les capteurs en position debout
settings-stay_aligned-relaxed_poses-sitting = Ajuster les capteurs en position assise sur une chaise
settings-stay_aligned-relaxed_poses-flat = Ajuster les capteurs en position assise sur le sol ou allongée sur le dos
settings-stay_aligned-relaxed_poses-save_pose = Enregistrer la posture
settings-stay_aligned-relaxed_poses-reset_pose = Réinitialiser la posture
settings-stay_aligned-relaxed_poses-close = Fermer
settings-stay_aligned-debug-label = Débogage
settings-stay_aligned-debug-description = Veuillez inclure vos paramètres lorsque vous signalez des problèmes concernant Garder Aligné.
settings-stay_aligned-debug-copy-label = Copier les paramètres dans le presse-papiers

## FK/Tracking settings

settings-general-fk_settings = Paramètres de la capture
# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Limitage au sol
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Correction du glissement
settings-general-fk_settings-leg_tweak-toe_snap = Correction des orteils
settings-general-fk_settings-leg_tweak-foot_plant = Correction des pieds
settings-general-fk_settings-leg_tweak-skating_correction-amount = Force de la correction du glissement
settings-general-fk_settings-leg_tweak-skating_correction-description = La correction de patinage corrige le patinage des pieds mais peut diminuer la précision de certains mouvements. Lorsque vous activez cette option, assurez-vous d'effectuer une réinitialisation complète et de le recalibrer en jeu.
settings-general-fk_settings-leg_tweak-floor_clip-description = Le limitage au sol tente de réduire ou même d'empêcher que vos pieds traversent le sol. Lorsque vous activez cette fonction, assurez-vous d'effectuer une réinitialisation complète et de recalibrer en jeu.
settings-general-fk_settings-leg_tweak-toe_snap-description = La correction des orteils estime l'orientation de vos pieds si vous ne portez pas de capteurs sur ses derniers.
settings-general-fk_settings-leg_tweak-foot_plant-description = La correction des pieds oriente vos pieds pour qu'ils soient parallèles au sol lorsqu'ils le touche.
settings-general-fk_settings-leg_fk = Capture des jambes
settings-general-fk_settings-leg_fk-reset_mounting_feet-description-v1 = Forcer la réinitialisation de l'alignement des pieds pendant la réinitialisation d'alignement générale.
settings-general-fk_settings-leg_fk-reset_mounting_feet-v1 = Forcer la réinitialisation de l'alignement des pieds
settings-general-fk_settings-enforce_joint_constraints = Limites squelettiques
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints = Appliquer les contraintes
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = Empêche les articulations de tourner au-delà de leur limite
settings-general-fk_settings-ik = Données de position
settings-general-fk_settings-ik-use_position = Utiliser les données de position
settings-general-fk_settings-ik-use_position-description = Permet d'utiliser les données de position des capteurs qui les fournissent. Assurez-vous de faire une réinitialisation complète et de recalibrer en jeu lorsque vous activez cette option.
settings-general-fk_settings-arm_fk-reset_mode-description = Changer la pose des bras attendue pour la réinitialisation de l'alignement.
settings-general-fk_settings-arm_fk-back = En arrière
settings-general-fk_settings-arm_fk-back-description = Le mode par défaut, avec les bras vers l'arrière et les avant-bras vers l'avant.
settings-general-fk_settings-arm_fk-tpose_up = T-pose (vers le haut)
settings-general-fk_settings-arm_fk-tpose_up-description = S'attend à ce que vos bras soient  vers le bas sur les côtés pendant la réinitialisation complète et à 90 degrés vers l'extérieur pendant la réinitialisation de l'alignement.
settings-general-fk_settings-arm_fk-tpose_down = T-pose (vers le bas)
settings-general-fk_settings-arm_fk-tpose_down-description = S'attend à ce que vos bras soient à 90 degrés vers l'extérieur pendant la réinitialisation complète et vers le bas sur les côtés pendant la réinitialisation de l'alignement.
settings-general-fk_settings-arm_fk-forward = En avant
settings-general-fk_settings-arm_fk-forward-description = S'attend à ce que vos bras soient levés 90 degrés vers l'avant. Utile pour le VTubing.
settings-general-fk_settings-skeleton_settings-ratios = Ratios du squelette
settings-general-fk_settings-skeleton_settings-ratios-description = Modifiez les valeurs des paramètres du squelette. Vous devrez peut-être ajuster vos proportions après les avoir modifiées.
settings-general-fk_settings-self_localization-title = Mode Mocap
settings-general-fk_settings-self_localization-description = Le mode Mocap permet au squelette de suivre grossièrement sa propre position sans casque ou autres capteurs. Ce mode nécessite des capteurs de pieds et de tête afin de fonctionner et est encore expérimental.

## Gesture control settings (tracker tapping)

settings-general-gesture_control-subtitle = Double tape pour réinitialisation rapide
settings-general-gesture_control-description = Permet de déclencher des réinitialisations en tapant un capteur. Le capteur le plus haut sur votre torse est utilisé pour la réinitialisation horizontale, le capteur le plus haut sur votre jambe gauche est utilisé pour la réinitialisation complète, et le capteur le plus haut sur votre jambe droite est utilisé pour la réinitialisation de l'alignement. Les tapes doivent être enchainées en moins de 0,6 seconde pour être pris en compte.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
        [one] 1 tap
       *[other] { $amount } taps
    }
# This is a unit: 3 trackers, 2 trackers, 1 tracker
# $amount (Number) - Amount of trackers
settings-general-gesture_control-trackers =
    { $amount ->
        [one] 1 capteur
       *[other] { $amount } capteurs
    }
settings-general-gesture_control-yawResetEnabled = Tapoter pour réinitialisation horizontale
settings-general-gesture_control-yawResetDelay = Délai de réinitialisation horizontale
settings-general-gesture_control-yawResetTaps = Nombre de tapes pour réinitialisation horizontale
settings-general-gesture_control-fullResetEnabled = Tapoter pour réinitialisation complète
settings-general-gesture_control-fullResetDelay = Délai de réinitialisation complète
settings-general-gesture_control-fullResetTaps = Nombre de tapes pour réinitialisation complète
settings-general-gesture_control-mountingResetEnabled = Tapoter pour réinitialisation de l'alignement
settings-general-gesture_control-mountingResetDelay = Délai de réinitialisation de l'alignement
settings-general-gesture_control-mountingResetTaps = Nombre de tapes pour la réinitialisation de l'alignement
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = Capteurs au-dessus du seuil
settings-general-gesture_control-numberTrackersOverThreshold-description = Augmentez cette valeur si la détection des tapotements ne fonctionne pas. N'augmentez pas cette valeur au-delà de ce qui est nécessaire pour que la détection des tapotements fonctionne, car cela pourrait entraîner des faux positifs.

## Appearance settings

settings-interface-appearance = Apparence
settings-general-interface-dev_mode = Mode développeur
settings-general-interface-dev_mode-description = Ce mode peut être utile pour avoir des données approfondies ou pour interagir avec des capteurs connectés à un niveau plus avancé.
settings-general-interface-dev_mode-label = Mode développeur
settings-general-interface-theme = Thème
settings-general-interface-lang = Langue
settings-general-interface-lang-description = Choisir la langue par défaut.
settings-general-interface-lang-placeholder = Langue
# Keep the font name untranslated
settings-interface-appearance-font = Police de l'interface
settings-interface-appearance-font-description = Cela change la police d'écriture utilisée par l'interface.
settings-interface-appearance-font-placeholder = Police par défaut
settings-interface-appearance-font-os_font = Police du système d'exploitation
settings-interface-appearance-font-slime_font = Police par défaut
settings-interface-appearance-font_size = Agrandissement du texte
settings-interface-appearance-font_size-description = Cela affecte la taille du texte de toute l'interface, sauf de ce menu.

## Notification settings

settings-interface-notifications = Notifications
settings-general-interface-serial_detection = Détection de périphérique série
settings-general-interface-serial_detection-description = Cette option affichera une fenêtre chaque fois qu'un nouveau périphérique série qui pourrait être un capteur est connecté.
settings-general-interface-serial_detection-label = Détection de périphérique série
settings-general-interface-feedback_sound = Son de retour
settings-general-interface-feedback_sound-description = Cette option va jouer un son lorsqu'une réanitilisation est enclenchée.
settings-general-interface-feedback_sound-label = Son de retour
settings-general-interface-feedback_sound-volume = Volume du son de retour
settings-general-interface-connected_trackers_warning = Avertissement de capteurs connectés
settings-general-interface-connected_trackers_warning-description = Cette option affichera une fenêtre contextuelle à chaque fois que vous essaierez de quitter SlimeVR en ayant un ou plusieurs capteurs connectés. Il vous rappelle d'éteindre vos capteurs lorsque vous avez terminé pour préserver la durée de vie de la batterie.
settings-general-interface-connected_trackers_warning-label = Avertissement de capteurs connectés en quittant

## Behavior settings

settings-interface-behavior = Comportement
settings-general-interface-use_tray = Minimiser dans la zone de notifications
settings-general-interface-use_tray-description = Vous permet de fermer la fenêtre sans fermer le serveur SlimeVR afin que vous puissiez continuer à l'utiliser sans l'interface graphique.
settings-general-interface-use_tray-label = Minimiser dans la zone de notifications
settings-general-interface-discord_presence = Partager l'activité sur Discord
settings-general-interface-discord_presence-description = Indique à votre client Discord que vous utilisez SlimeVR avec le nombre de capteurs IMU que vous utilisez.
settings-general-interface-discord_presence-label = Partager l'activité sur Discord
settings-general-interface-discord_presence-message =
    { $amount ->
        [0] Aucun capteur
        [one] Utilise { $amount } capteur
       *[other] Utilise { $amount } capteurs
    }
settings-interface-behavior-error_tracking = Collecte des erreurs via Sentry.io
settings-interface-behavior-error_tracking-description_v2 =
    <h1>Consentez-vous à la collecte de données d'erreur anonymisée ?</h1>
    
    <b>Nous ne collectons pas d'informations personnelles</b> telles que votre adresse IP ou vos identifiants Wi-Fi. SlimeVR accorde une grande importance à votre vie privée !
    
    Pour offrir la meilleure expérience utilisateur possible, nous collectons des rapports d'erreurs anonymisés, des mesures de performance et des informations sur le système d'exploitation. Cela nous aide à détecter les bugs et les problèmes liés à SlimeVR. Ces données sont collectées via Sentry.io.
settings-interface-behavior-error_tracking-label = Envoyer les erreurs aux développeurs
settings-interface-behavior-bvh_directory = Répertoire pour sauvegarder les enregistrements BVH
settings-interface-behavior-bvh_directory-description = Choisissez un répertoire où sauvegarder vos enregistrements BVH au lieu d’avoir à choisir où les sauvegarder à chaque fois.
settings-interface-behavior-bvh_directory-label = Répertoire où sauvegarder les enregistrements BVH

## Serial settings

settings-serial = Console série
# This cares about multilines
settings-serial-description =
    Il s'agit d'un flux d'informations en direct pour la communication en série.
    Peut être utile pour savoir si un capteur fonctionne correctement.
settings-serial-connection_lost = Connexion à l'appareil perdue, reconnexion...
settings-serial-reboot = Redémarrer
settings-serial-factory_reset = Remise à zéro
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>Attention:</b> Cela réinitialisera les paramètres du capteur à zéro.
            Ce qui signifie que les paramètres de Wi-Fi et de calibration <b>seront tous perdus !</b>
settings-serial-factory_reset-warning-ok = Je sais ce que je fais
settings-serial-factory_reset-warning-cancel = Annuler
settings-serial-serial_select = Sélectionnez un port série
settings-serial-auto_dropdown_item = Automatique
settings-serial-get_wifi_scan = Obtenir scan WiFi
settings-serial-save_logs = Enregistrer dans un fichier
settings-serial-send_command = Envoyer
settings-serial-send_command-placeholder = Commande...
settings-serial-send_command-warning = <b>Avertissement:</b> Exécuter des commandes en série peut entraîner une perte de données ou rendre les capteurs inutilisables.
settings-serial-send_command-warning-ok = Je sais ce que je fais
settings-serial-send_command-warning-cancel = Annuler

## OSC router settings


## OSC VRChat settings

settings-osc-vrchat = Capteurs OSC VRChat
# This cares about multilines
settings-osc-vrchat-description-v1 =
    Modifier les paramètres spécifiques à la norme « OSC Trackers » utilisée pour l'envoi
    des données de suivi vers des applications sans SteamVR (par exemple, sur Quest).
    Assurez-vous d'activer le protocole OSC dans VRChat via le menu d'action (rond) sous OSC > Enabled.
settings-osc-vrchat-enable = Activer
settings-osc-vrchat-enable-description = Activer/désactiver l'envoi et la réception de données.
settings-osc-vrchat-enable-label = Activer
settings-osc-vrchat-network = Ports réseau
settings-osc-vrchat-network-port_in =
    .label = Port d'entrée
    .placeholder = Port d'entrée (par défaut : 9001)
settings-osc-vrchat-network-port_out =
    .label = Port de sortie
    .placeholder = Port de sortie (par défaut : 9000)
settings-osc-vrchat-network-address = Adresse réseau
settings-osc-vrchat-network-address-description-v1 = Choisissez l'adresse à laquelle envoyer des données. Peut être laissé intact pour VRChat.
settings-osc-vrchat-network-address-placeholder = Adresse IP VRChat

## VMC OSC settings

settings-osc-vmc = Virtual Motion Capture
# This cares about multilines
settings-osc-vmc-description =
    Modifier les paramètres spécifique au protocole VMC (Virtual Motion Capture)
           pour envoyer les données de capture de SlimeVR et recevoir les données de capture d'autres applications.
settings-osc-vmc-enable = Activer
settings-osc-vmc-enable-description = Activer/désactiver l'envoi et la réception de données.
settings-osc-vmc-enable-label = Activer
settings-osc-vmc-network = Ports réseau
settings-osc-vmc-network-description = Définissez les ports pour écouter et envoyer des données par VMC.
settings-osc-vmc-network-port_in =
    .label = Port d'entrée
    .placeholder = Port d'entrée (par défaut : 39540)
settings-osc-vmc-network-port_out =
    .label = Port de sortie
    .placeholder = Port de sortie (par défaut : 39539)
settings-osc-vmc-network-address = Adresse réseau
settings-osc-vmc-network-address-description = Choisissez l'adresse vers laquelle envoyer des données VMC.
settings-osc-vmc-network-address-placeholder = Adresse IPv4
settings-osc-vmc-vrm = Modèle VRM
settings-osc-vmc-vrm-description = Chargez un modèle VRM pour permettre l'ancrage à la tête et permettre une plus grande compatibilité avec d'autres applications
settings-osc-vmc-vrm-untitled_model = Modèle sans nom
settings-osc-vmc-vrm-file_select = Glissez et déposez un modèle à utiliser, ou <u>parcourir</u>.
settings-osc-vmc-anchor_hip = Ancrage aux hanches
settings-osc-vmc-anchor_hip-description = Ancrer la capture des mouvements aux hanches, utile pour le VTubing assis.
settings-osc-vmc-anchor_hip-label = Ancrage aux hanches
settings-osc-vmc-mirror_tracking = Inverser les mouvements
settings-osc-vmc-mirror_tracking-description = Inverse les mouvements horizontalement
settings-osc-vmc-mirror_tracking-label = Inverser les mouvements

## Common OSC settings

settings-osc-common-network-port_banned_error = Le port { $port } ne peut pas être utilisé !

## Advanced settings

settings-utils-advanced = Avancé
settings-utils-advanced-reset-gui = Réinitialiser les paramètres de l'interface graphique
settings-utils-advanced-reset-gui-description = Restaurez les paramètres par défaut de l'interface.
settings-utils-advanced-reset-gui-label = Réinitialiser l'interface graphique
settings-utils-advanced-reset-server = Réinitialiser les paramètres de la capture
settings-utils-advanced-reset-server-description = Restaurez les paramètres par défaut de la capture
settings-utils-advanced-reset-server-label = Réinitialiser la capture
settings-utils-advanced-reset-all = Réinitialiser tous les paramètres
settings-utils-advanced-reset-all-description = Restaurez les paramètres de l'interface et de la capture.
settings-utils-advanced-reset-all-label = Tout réinitialiser
settings-utils-advanced-reset_warning =
    { $type ->
        [gui]
            <b>Avertissement:</b> Cela réinitialisera vos paramètres de l'interface.
            Êtes-vous sûr de vouloir faire cela ?
        [server]
            <b>Avertissement:</b> Cela réinitialisera vos paramètres de la capture.
            Êtes-vous sûr de vouloir faire cela ?
       *[all]
            <b>Avertissement:</b> Cela réinitialisera tous vos paramètres.
            Êtes-vous sûr de vouloir faire cela ?
    }
settings-utils-advanced-reset_warning-reset = Réinitialiser les paramètres
settings-utils-advanced-reset_warning-cancel = Annuler
settings-utils-advanced-open_data-v1 = Dossier de configuration
settings-utils-advanced-open_data-description-v1 = Ouvre le dossier de configuration de SlimeVR, contenant les fichiers de configuration, dans l'explorateur de fichier
settings-utils-advanced-open_data-label = Ouvrir le dossier
settings-utils-advanced-open_logs = Dossier des logs
settings-utils-advanced-open_logs-description = Ouvre le dossier des logs de SlimeVR, contenant ses logs, dans l'explorateur de fichier
settings-utils-advanced-open_logs-label = Ouvrir le dossier

## Home Screen

settings-home-list-layout = Disposition de la liste des capteurs
settings-home-list-layout-desc = Sélectionnez l'une des dispositions possibles de l'écran d'accueil
settings-home-list-layout-grid = Grille
settings-home-list-layout-table = Tableau

## Tracking Checlist

settings-tracking_checklist-active_steps = Etapes actives
settings-tracking_checklist-active_steps-desc = Liste de toutes les étapes de la checklist de suivi. Vous pouvez choisir de désactiver certaines étapes.

## Setup/onboarding menu

onboarding-skip = Passer
onboarding-continue = Continuer
onboarding-previous_step = Étape précédente
onboarding-setup_warning =
    <b>Avertissement:</b> La configuration est requise pour assurer une bonne capture des mouvements,
    elle est nécessaire si vous utilisez SlimeVR pour la première fois.
onboarding-setup_warning-skip = Passer la configuration
onboarding-setup_warning-cancel = Continuer la configuration

## Quiz


## Wi-Fi setup

onboarding-wifi_creds-v2 = Capteurs utilisant le Wi-Fi
# This cares about multilines
onboarding-wifi_creds-description-v2 =
    La plupart des capteurs (comme les capteurs officiels SlimeVR) utilisent le Wi-Fi pour se connecter au serveur.
    Veuillez utiliser les identifiants du réseau Wi-Fi auquel votre appareil est actuellement connecté.
    
    Assurez-vous d’utiliser une connexion Wi-Fi 2,4 GHz pour vos capteurs !
onboarding-wifi_creds-submit = Valider
onboarding-wifi_creds-ssid =
    .label = Nom du Wi-Fi
    .placeholder = Nom
onboarding-wifi_creds-ssid-required = Le nom du Wi-Fi est requis
onboarding-wifi_creds-password =
    .label = Mot de passe du Wi-Fi
    .placeholder = Mot de passe
onboarding-wifi_creds-dongle-title = Capteurs utilisant un dongle
onboarding-wifi_creds-dongle-description = Si vos capteurs ont été livrés avec un dongle, branchez-le à votre appareil et vous devriez être prêt !
onboarding-wifi_creds-dongle-wip = Cette section est en cours de développement. Une page dédiée à la gestion des capteurs connectés via un dongle sera bientôt créée.
onboarding-wifi_creds-dongle-continue = Continuer avec un dongle

## Mounting setup


## Install info


## Setup start

onboarding-home = Bienvenue sur SlimeVR
onboarding-home-start = Commencer

## Setup done


## Tracker connection setup

onboarding-connect_tracker-title = Connecter les capteurs
onboarding-connect_tracker-issue-serial = J'ai des problèmes de connexion !
onboarding-connect_tracker-usb = Capteur USB
onboarding-connect_tracker-connection_status-serial_init = Connexion au périphérique en série
onboarding-connect_tracker-connection_status-obtaining_mac_address = Obtention de l'adresse mac du capteur
onboarding-connect_tracker-connection_status-provisioning = Envoi des identifiants Wi-Fi
onboarding-connect_tracker-connection_status-connecting = Envoi d'identifiants Wi-Fi
onboarding-connect_tracker-connection_status-looking_for_server = Recherche du serveur
onboarding-connect_tracker-connection_status-connection_error = Impossible de se connecter au réseau
onboarding-connect_tracker-connection_status-could_not_find_server = Impossible de trouver le serveur
onboarding-connect_tracker-connection_status-done = Connecté au serveur
onboarding-connect_tracker-connection_status-no_serial_log = Erreur lors de l'obtention des journaux du capteur
onboarding-connect_tracker-connection_status-no_serial_device_found = Aucun capteur trouvé par USB
onboarding-connect_serial-error-modal-no_serial_log = Le capteur est-il allumé ?
onboarding-connect_serial-error-modal-no_serial_log-desc = Assurez-vous que le capteur est allumé et connecté à votre ordinateur
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers =
    { $amount ->
        [0] No trackers connected
        [one] 1 tracker connected
       *[other] { $amount } trackers connected
    }
onboarding-connect_tracker-next = J'ai connecté tous mes capteurs

## Tracker calibration tutorial


## Tracker assignment tutorial


## Tracker assignment setup

onboarding-assign_trackers-title = Attribuer des capteurs
onboarding-assign_trackers-description = Choisissons où mettre chaque capteur.
onboarding-assign_trackers-unassign_all = Désattribuer tout les capteurs
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned =
    { $trackers ->
        [one] { $assigned } sur 1 capteur attribué
       *[other] { $assigned } sur { $trackers } capteurs attribués
    }

## Tracker assignment warnings

# Note for devs, number is used for representing boolean states per bit.
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_FOOT =
    { $unassigned ->
        [0] Le pied gauche est attribué mais il faut que la cheville gauche, la cuisse gauche et soit la poitrine, la hanche ou la taille soient également attribuées !
        [1] Le pied gauche est attribué mais il faut que la cuisse gauche et soit la poitrine, la hanche ou la taille soient également attribuées !
        [2] Le pied gauche est attribué mais il faut que la cheville gauche et soit la poitrine, la hanche ou la taille soient également attribuées !
        [3] Le pied gauche est attribué mais il faut que la poitrine, la hanche ou la taille soient également attribuées !
        [4] Le pied gauche est attribué mais il faut que la cheville gauche et la cuisse gauche soient également attribuées !
        [5] Le pied gauche est attribué mais il faut que la cuisse gauche le soit également !
        [6] Le pied gauche est attribué mais il faut que la cheville gauche le soit également !
       *[unknown] Le pied gauche est attribué mais il faut qu'une autre partie du corps inconnue non assignée soit également attribuée !
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_FOOT =
    { $unassigned ->
        [0] Le pied droit est attribué mais il faut que la cheville droite, la cuisse droite et soit la poitrine, la hanche ou la taille soient également attribuées !
        [1] Le pied droit est assigné mais il faut que la cuisse droite et soit la poitrine, la hanche ou la taille soient également attribuées !
        [2] Le pied droit est assigné mais il faut que la cheville droite et soit la poitrine, la hanche ou la taille soient également attribuées !
        [3] Le pied droit est attribué mais il faut que la poitrine, la hanche ou la taille soient également attribuées !
        [4] Le pied droit est attribué mais il faut que la cheville droite et la cuisse droite soient également attribuées !
        [5] Le pied droit est attribué mais il faut que la cuisse droite le soit également !
        [6] Le pied droit est attribué mais il faut que la cheville droite le soit également !
       *[unknown] Le pied droit est assigné mais il faut qu'une autre partie du corps inconnue non assignée soit également attribuée !
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_LOWER_LEG =
    { $unassigned ->
        [0] La cheville gauche est attribuée mais il faut que la cuisse gauche et soit la poitrine, la hanche ou la taille soient également attribuées !
        [1] La cheville gauche est attribuée mais il faut que la poitrine, la hanche ou la taille soient également attribuées !
        [2] La cheville gauche est attribuée mais il faut que la cuisse gauche soit également attribuée !
       *[unknown] La cheville gauche est attribuée mais il faut qu'une partie du corps inconnue non assignée soit également attribuée !
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_LOWER_LEG =
    { $unassigned ->
        [0] La cheville droite est attribuée mais il faut que la cuisse droite et soit la poitrine, la hanche ou la taille soient également attribuées !
        [1] La cheville droite est attribuée mais il faut que la poitrine, la hanche ou la taille soient également attribuées !
        [2] La cheville droite est attribuée mais il faut que la cuisse droite soit également attribuée !
       *[unknown] La cheville droite est attribuée mais il faut qu'une partie du corps inconnue non assignée soit également attribuée !
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_UPPER_LEG =
    { $unassigned ->
        [0] La cuisse gauche est attribuée mais il faut que la poitrine, la hanche ou la taille soient également attribuées !
       *[unknown] La cuisse gauche est attribuée mais il faut qu'une partie du corps inconnue non assignée soit également attribuée !
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_UPPER_LEG =
    { $unassigned ->
        [0] La cuisse droite est attribuée mais il faut que la poitrine, la hanche ou la taille soient également attribuées !
       *[unknown] La cuisse droite est attribuée mais il faut qu'une partie du corps inconnue non assignée soit également attribuée !
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-HIP =
    { $unassigned ->
        [0] La hanche est attribuée mais il faut que la poitrine le soit aussi !
       *[unknown] La hanche est attribuée mais il faut qu'une partie du corps inconnue non assignée soit également attribuée !
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-WAIST =
    { $unassigned ->
        [0] La taille est attribuée mais il faut que la poitrine le soit aussi !
       *[unknown] La taille est attribuée mais il faut qu'une partie du corps inconnue non assignée soit également attribuée !
    }

## Tracker mounting method choose

onboarding-choose_mounting = Quelle méthode de calibration de l'alignement utiliser ?
# Multiline text
onboarding-choose_mounting-description = La calibration de l'alignement ajuste pour l'orientation des capteurs sur votre corps.
onboarding-choose_mounting-auto_mounting = Alignement automatique
# Italicized text
onboarding-choose_mounting-auto_mounting-label-v2 = Recommendée
onboarding-choose_mounting-auto_mounting-description = Ceci permettra de détecter automatiquement la direction de tous vos capteurs à partir de 2 poses
onboarding-choose_mounting-manual_mounting = Alignement manuel
onboarding-choose_mounting-manual_mounting-description = Ceci vous permettra de choisir la direction de chaque capteur manuellement

## Tracker manual mounting setup

onboarding-manual_mounting = Alignement manuel
onboarding-manual_mounting-description = Cliquez sur chaque capteur et sélectionnez la manière dont ils sont orientés
onboarding-manual_mounting-auto_mounting = Détection automatique
onboarding-manual_mounting-next = Prochaine étape

## Tracker automatic mounting setup

onboarding-automatic_mounting-title = Calibration de l'alignement des capteurs
onboarding-automatic_mounting-description = Pour que vos capteurs SlimeVR fonctionnent, nous devons attribuer une rotation à vos capteurs pour les aligner avec la rotation réelle de ces derniers.
onboarding-automatic_mounting-manual_mounting = Alignement manuel
onboarding-automatic_mounting-next = Prochaine étape
onboarding-automatic_mounting-prev_step = Étape précédente
onboarding-automatic_mounting-done-title = Alignements calibrés.
onboarding-automatic_mounting-done-description = La calibration de l'alignement de vos capteurs est terminée !
onboarding-automatic_mounting-done-restart = Retourner au début
onboarding-automatic_mounting-mounting_reset-title = Réinitialisation de l'alignement
onboarding-automatic_mounting-mounting_reset-step-0 = 1. Accroupissez-vous dans une pose de "ski" avec les jambes pliées, le haut du corps incliné vers l'avant et les bras pliés.
onboarding-automatic_mounting-mounting_reset-step-1 = 2. Appuyez sur le bouton "Réinitialiser l'alignement" et attendez 3 secondes avant que l'alignement des capteurs se calibre.
onboarding-automatic_mounting-mounting_reset-feet-step-0 = 1. Mettez-vous sur la pointe des pieds, les deux pieds pointés vers l’avant. Vous pouvez aussi le faire assis sur une chaise.
onboarding-automatic_mounting-mounting_reset-feet-step-1 = 2. Appuyez sur le bouton « Calibration des pieds » et attendez 3 secondes avant que l’orientation de l'alignement des capteurs ne se réinitialise.
onboarding-automatic_mounting-preparation-title = Préparation
onboarding-automatic_mounting-preparation-v2-step-0 = 1. Appuyez sur le bouton « Réinitialisation complète ».
onboarding-automatic_mounting-preparation-v2-step-1 = 2. Tenez-vous droit debout, les bras le long du corps. Assurez-vous de regarder vers l’avant.
onboarding-automatic_mounting-preparation-v2-step-2 = 3. Maintenez la position jusqu'à la fin du chronomètre de 3 secondes.
onboarding-automatic_mounting-put_trackers_on-title = Enfilez vos capteurs
onboarding-automatic_mounting-put_trackers_on-description = Pour calibrer l'alignement, nous allons utiliser les capteurs que vous venez d'attribuer.
onboarding-automatic_mounting-put_trackers_on-next = J'ai tous mes capteurs
onboarding-automatic_mounting-return-home = Terminé

## Tracker manual proportions setupa

onboarding-manual_proportions-back-scaled = Retour aux proportions mises à l'échelle
onboarding-manual_proportions-fine_tuning_button = Automatiquement ajuster les proportions
onboarding-manual_proportions-fine_tuning_button-disabled-tooltip = Veuillez connecter un casque VR pour utiliser l'ajustement automatique
onboarding-manual_proportions-export = Exporter les proportions
onboarding-manual_proportions-import = Importer les proportions
onboarding-manual_proportions-normal_increment = Incrément normal
onboarding-manual_proportions-precise_increment = Incrément précis
onboarding-manual_proportions-grouped_proportions = Proportions groupées
onboarding-manual_proportions-all_proportions = Toutes les proportions
onboarding-manual_proportions-estimated_height = Taille estimée de l'utilisateur

## Tracker automatic proportions setup

onboarding-automatic_proportions-back = Revenir au didacticiel de réinitialisation
onboarding-automatic_proportions-title = Calibration des proportions du corps
onboarding-automatic_proportions-description = Pour que les capteurs SlimeVR fonctionnent, nous devons connaître la longueur de vos os.
onboarding-automatic_proportions-prev_step = Étape précédente
onboarding-automatic_proportions-put_trackers_on-title = Enfilez vos capteurs
onboarding-automatic_proportions-put_trackers_on-description = Pour calibrer vos proportions, nous allons utiliser les capteurs que vous venez d'attribuer.
onboarding-automatic_proportions-put_trackers_on-next = J'ai tous mes capteurs
onboarding-automatic_proportions-requirements-title = Exigences
# Each line of text is a different list item
onboarding-automatic_proportions-requirements-descriptionv2 =
    Vous avez au moins assez de capteurs pour capturer vos pieds (généralement 5 capteurs).
    Vos capteurs et votre casque VR sont allumés et sur vous.
    Vos capteurs et votre casque VR sont connectés au serveur SlimeVR et fonctionnent correctement (ex. pas de lag, déconnexions, etc).
    Votre casque envoie sa position au serveur SlimeVR (cela signifie généralement que SteamVR est ouvert et connecté à SlimeVR en utilisant le pilote SteamVR de SlimeVR).
    La capture des mouvements fonctionne et représente correctement vos mouvements (ex. vous avez effectué une réinitialisation complète des capteurs et ils bougent dans le bon sens lorsque vous donnez des coups de pieds, vous penchez, vous assoyez, etc).
onboarding-automatic_proportions-requirements-next = J'ai lu les exigences
onboarding-automatic_proportions-start_recording-title = Préparez-vous à bouger
onboarding-automatic_proportions-start_recording-description = Nous allons maintenant enregistrer quelques positions et mouvements spécifiques. Ceux-ci seront inscris sur l'écran suivant. Soyez prêt à commencer dès que vous appuyez sur le bouton !
onboarding-automatic_proportions-start_recording-next = Commencer l'enregistrement
onboarding-automatic_proportions-recording-title = Enregistrement
onboarding-automatic_proportions-recording-description-p0 = Enregistrement en cours...
onboarding-automatic_proportions-recording-description-p1 = Effectuez les mouvements indiqués ci-dessous:
# Each line of text is a different list item
onboarding-automatic_proportions-recording-steps =
    Debout, bien droit, faites tourner votre tête en cercle.
    Inclinez le dos vers l'avant et accroupissez-vous. Accroupi, regardez vers la gauche, puis vers la droite.
    Tournez le haut de votre corps vers la gauche (dans le sens inverse des aiguilles d'une montre), puis penchez-vous vers le sol.
    Tournez le haut de votre corps vers la droite (dans le sens des aiguilles d'une montre), puis penchez-vous vers le sol.
    Faites rouler vos hanches dans un mouvement circulaire comme si vous utilisiez un cerceau.
    S'il reste du temps à l'enregistrement, répétez les étapes jusqu'à la fin de ce dernier.
onboarding-automatic_proportions-recording-processing = Traitement du résultat
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 20)
onboarding-automatic_proportions-recording-timer =
    { $time ->
        [one] 1 secondes restantes
       *[other] { $time } secondes restantes
    }
onboarding-automatic_proportions-verify_results-title = Vérifier les résultats
onboarding-automatic_proportions-verify_results-description = Les résultats ci-dessous vous semblent-ils corrects ?
onboarding-automatic_proportions-verify_results-results = Enregistrement des résultats
onboarding-automatic_proportions-verify_results-processing = Traitement du résultat
onboarding-automatic_proportions-verify_results-redo = Refaire l'enregistrement
onboarding-automatic_proportions-verify_results-confirm = Les résultats sont corrects
onboarding-automatic_proportions-done-title = Calibration terminée
onboarding-automatic_proportions-done-description = La calibration de vos proportions est terminée !
onboarding-automatic_proportions-error_modal-v2 =
    <b>Avertissement:</b> Il y a eu une erreur lors de l'estimation des proportions !
    Il s'agit probablement d'un problème avec la calibration de l'alignement. Assurez-vous que votre capture des mouvements fonctionne correctement avant de réessayer.
     Veuillez <docs>consulter la documentation</docs> ou rejoindre notre <discord>Discord</discord> pour obtenir de l'aide ^_^
onboarding-automatic_proportions-error_modal-confirm = Compris !
onboarding-automatic_proportions-smol_warning =
    Votre hauteur configurée de { $height } est inférieure à la hauteur minimale acceptée de { $minHeight }.
    <b>Veuillez refaire les mesures et vous assurer qu'elles sont correctes.</b>
onboarding-automatic_proportions-smol_warning-cancel = Retour

## User height calibration

onboarding-user_height-title = Quelle est votre taille ?
onboarding-user_height-need_head_tracker = Un casque VR (ou capteur de tête) et des manettes à position absolue sont nécessaires pour calculer votre taille.
onboarding-user_height-calculate = Calculer ma taille automatiquement
onboarding-user_height-next_step = Continuer et enregistrer
onboarding-user_height-manual-proportions = Proportions manuelles
onboarding-user_height-calibration-title = Progression de la calibration
onboarding-user_height-calibration-RECORDING_FLOOR = Touchez le sol avec l'extrémité de votre contrôleur
onboarding-user_height-calibration-WAITING_FOR_RISE = Relevez-vous
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK = Relevez-vous et regardez droit devant vous
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-ok = Assurez-vous que votre tête est bien droite
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-low = Ne regardez pas vers le sol
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-high = Ne regardez pas trop haut
onboarding-user_height-calibration-WAITING_FOR_CONTROLLER_PITCH = Assurez-vous que votre manette pointe vers le bas
onboarding-user_height-calibration-RECORDING_HEIGHT = Relevez-vous et restez immobile !
onboarding-user_height-calibration-DONE = Succès !
onboarding-user_height-calibration-ERROR_TIMEOUT = Délais de calibration expiré, veuillez réessayer.
onboarding-user_height-calibration-ERROR_TOO_HIGH = La taille détectée est trop grande, veuillez réessayez.
onboarding-user_height-calibration-ERROR_TOO_SMALL = La taille détectée est trop petite. Veuillez rester droit et regardez devant vous à la fin de la calibration.
onboarding-user_height-calibration-error = Calibration échouée
onboarding-user_height-manual-tip = En ajustant votre taille, essayez différentes poses et regardez comment le squelette suit vos mouvements.
onboarding-user_height-reset-warning =
    <b>Attention :</b> Cette action réinitialisera vos proportions pour être basées sur votre taille. 
    Êtes-vous sûr de vouloir continuer ?

## Stay Aligned setup

onboarding-stay_aligned-title = Garder Aligné
onboarding-stay_aligned-description = Configurer Garder Aligné pour garder vos capteurs alignés.
onboarding-stay_aligned-put_trackers_on-title = Mettez vos capteurs
onboarding-stay_aligned-put_trackers_on-description = Pour enregistrer vos postures de repos, nous utiliserons les capteurs que vous venez d’attribuer. Enfilez tous vos capteurs. Vous pouvez voir lesquels sont lesquels dans la figure de droite.
onboarding-stay_aligned-put_trackers_on-trackers_warning = Vous avez actuellement moins de 5 capteurs connectés et attribués ! Il s’agit du nombre minimum de capteurs requis pour que Garder Aligné fonctionne correctement.
onboarding-stay_aligned-put_trackers_on-next = J'ai tous mes capteurs sur moi
onboarding-stay_aligned-verify_mounting-title = Vérifiez votre alignement
onboarding-stay_aligned-preparation-title = Préparation
onboarding-stay_aligned-preparation-tip = Assurez-vous de vous tenir droit. Vous devez regarder vers l'avant et vos bras doivent être le long de votre corps.
onboarding-stay_aligned-relaxed_poses-standing-title = Posture debout détendu
onboarding-stay_aligned-relaxed_poses-standing-step-0 = 1. Tenez-vous dans une position confortable. Détendez-vous !
onboarding-stay_aligned-relaxed_poses-standing-step-1-v2 = 2. Appuyez sur le bouton « Enregistrer la posture ».
onboarding-stay_aligned-relaxed_poses-sitting-title = Posture assis détendu dans une chaise
onboarding-stay_aligned-relaxed_poses-sitting-step-0 = 1. Asseyez-vous dans une position confortable. Détendez-vous !
onboarding-stay_aligned-relaxed_poses-sitting-step-1-v2 = 2. Appuyez sur le bouton « Enregistrer la posture ».
onboarding-stay_aligned-relaxed_poses-flat-title = Posture assis détendu sur le sol
onboarding-stay_aligned-relaxed_poses-flat-step-0 = 1. Asseyez-vous sur le sol, les jambes devant. Détendez-vous !
onboarding-stay_aligned-relaxed_poses-flat-step-1-v2 = 2. Appuyez sur le bouton « Enregistrer la posture ».
onboarding-stay_aligned-relaxed_poses-skip_step = Sauter
onboarding-stay_aligned-done-title = Garder Aligné activé !
onboarding-stay_aligned-done-description = La configuration de Garder Aligné est terminée !
onboarding-stay_aligned-done-description-2 = La configuration est terminée ! Vous pouvez recommencer le processus si vous souhaitez recalibrer les postures.
onboarding-stay_aligned-previous_step = Précédent
onboarding-stay_aligned-next_step = Prochain
onboarding-stay_aligned-restart = Recommencer
onboarding-stay_aligned-done = Fait
onboarding-stay_aligned-manual_mounting-done = Terminé

## Home

home-settings = Paramètres de la page d'accueil
home-settings-close = Fermer

## Trackers Still On notification

trackers_still_on-modal-title = Capteurs encore allumés
trackers_still_on-modal-description =
    Un ou plusieurs capteurs sont encore allumés.
    Voulez-vous quand même quitter SlimeVR ?
trackers_still_on-modal-confirm = Quitter SlimeVR
trackers_still_on-modal-cancel = Annuler...

## Status system


## Firmware tool globals

firmware_tool-next_step = Prochaine étape
firmware_tool-previous_step = Étape précédente
firmware_tool-ok = Parfait
firmware_tool-retry = Réessayer
firmware_tool-loading = Chargement...

## Firmware tool Steps

firmware_tool = Outil de micrologiciel DIY
firmware_tool-description = Vous permet de configurer et de flash vos capteurs DIY
firmware_tool-not_available = Oups, l'outil de micrologiciel n'est pas disponible en ce moment. Revenez plus tard !
firmware_tool-not_compatible = L'outil de micrologiciel n'est pas compatible avec cette version de serveur. Veuillez mettre à jour votre serveur !
firmware_tool-select_source = Sélectionnez le micrologiciel à flasher
firmware_tool-select_source-description = Sélectionnez le micrologiciel que vous souhaitez flasher sur votre carte
firmware_tool-select_source-error = Impossible de charger les sources
firmware_tool-select_source-board_type = Type de carte
firmware_tool-select_source-firmware = Source du micrologiciel
firmware_tool-select_source-version = Version du micrologiciel
firmware_tool-select_source-official = Officiel
firmware_tool-select_source-dev = Dev
firmware_tool-select_source-not_selected = Aucune source sélectionnée
firmware_tool-select_source-no_boards = Aucune carte disponible pour cette source
firmware_tool-select_source-no_versions = Aucune version disponible pour cette source
firmware_tool-board_defaults = Configurez votre carte
firmware_tool-board_defaults-description = Réglez les broches ou réglages pour votre matériel
firmware_tool-board_defaults-add = Ajouter
firmware_tool-board_defaults-reset = Réinitialisation à la valeur par défaut
firmware_tool-board_defaults-error-required = Champ requis
firmware_tool-board_defaults-error-format = Format invalide
firmware_tool-board_defaults-error-format-number = Pas un nombre
firmware_tool-flash_method_step = Méthode de flash
firmware_tool-flash_method_step-description = Veuillez sélectionner la méthode de flash que vous souhaitez utiliser
firmware_tool-flash_method_step-ota-v2 =
    .label = Wi-Fi
    .description = Utilisez la méthode « over-the-air ». Votre capteur utilisera le Wi-Fi pour mettre à jour son microgiciel. Cette méthode ne fonctionne que pour les capteurs déjà configurés.
firmware_tool-flash_method_step-ota-info =
    Nous utilisons vos identifiants wifi pour flasher le capteur et confirmer que tout s'est déroulé correctement.
    <b>Nous ne stockons pas vos identifiants wifi !</b>
firmware_tool-flash_method_step-serial-v2 =
    .label = USB
    .description = Utiliser un cable USB pour mettre à jour votre capteur.
firmware_tool-flashbtn_step = Appuyez sur le bouton boot
firmware_tool-flashbtn_step-description = Avant de passer à l'étape suivante, il y a quelques choses que vous devez faire
firmware_tool-flashbtn_step-board_SLIMEVR = Éteignez le capteur, retirez le boîtier (s'il y en a un), connectez un câble USB à votre ordinateur, puis effectuez l'une des étapes suivantes en fonction de la révision de votre carte SlimeVR :
firmware_tool-flashbtn_step-board_SLIMEVR-r11-v2 = Allumez le capteur tout en court-circuitant le second pad FLASH rectangulaire à partir du bord en haut de la carte jusqu’à la protection métallique du microcontrôleur. La LED du capteur devrait faire un clignotement rapide.
firmware_tool-flashbtn_step-board_SLIMEVR-r12-v2 = Allumez le capteur tout en court-circuitant le pad FLASH circulaire sur le dessus de la carte à la protection métallique du microcontrôleur. La LED du capteur devrait faire un clignotement rapide.
firmware_tool-flashbtn_step-board_SLIMEVR-r14-v2 = Allumez le capteur tout en appuyant sur le bouton FLASH sur le dessus de la carte. La LED du capteur devrait faire un clignotement brièvement.
firmware_tool-flashbtn_step-board_OTHER =
    Avant de flash le capteur, vous devrez probablement le mettre en mode bootloader.
    La plupart du temps, il s'agit d'appuyer sur le bouton boot de la carte avant que le processus de flash ne commence.
    Si le processus de flash expire au début du flash, cela signifie probablement que le capteur n'était pas en mode bootloader
    Veuillez vous référer aux instructions de flash de votre carte pour savoir comment activer le mode boatloader
firmware_tool-flash_method_ota-title = Flasher via Wi-Fi
firmware_tool-flash_method_ota-devices = Appareils OTA détectés :
firmware_tool-flash_method_ota-no_devices = Il n'y a aucune carte pouvant être mise à jour à l'aide d'OTA, assurez-vous d'avoir sélectionné le bon type de carte
firmware_tool-flash_method_serial-title = Flasher via USB
firmware_tool-flash_method_serial-wifi = Identifiants Wi-Fi :
firmware_tool-flash_method_serial-devices-label = Appareils en série détectés :
firmware_tool-flash_method_serial-devices-placeholder = Sélectionnez un appareil en série
firmware_tool-flash_method_serial-no_devices = Aucun appareil en série compatible n'est détecté, assurez-vous que le capteur est branché
firmware_tool-build_step = Création
firmware_tool-build_step-description = Le micrologiciel se fait créer, veuillez patienter
firmware_tool-flashing_step = En train de flash
firmware_tool-flashing_step-description = Vos traceurs se font flash, veuillez suivre les instructions à l'écran
firmware_tool-flashing_step-warning-v2 = Ne débranchez pas ou n'éteignez pas le capteur pendant le processus d'envoi à moins qu'on ne vous le dise, cela pourrait rendre votre carte inutilisable
firmware_tool-flashing_step-flash_more = Flash plus de capteurs
firmware_tool-flashing_step-exit = Quitter

## firmware tool build status

firmware_tool-build-QUEUED = En attente de la création...
firmware_tool-build-CREATING_BUILD_FOLDER = Création du dossier de création
firmware_tool-build-DOWNLOADING_SOURCE = Téléchargement du code source
firmware_tool-build-EXTRACTING_SOURCE = Extraction du code source
firmware_tool-build-BUILDING = Création du micrologiciel
firmware_tool-build-SAVING = Enregistrement du micrologiciel
firmware_tool-build-DONE = Création terminée
firmware_tool-build-ERROR = Impossible de créer le micrologiciel

## Firmware update status

firmware_update-status-DOWNLOADING = Téléchargement du micrologiciel
firmware_update-status-NEED_MANUAL_REBOOT-v2 = Veuillez éteindre et rallumer votre capteur
firmware_update-status-AUTHENTICATING = Authentification avec le MCU
firmware_update-status-UPLOADING = Envoi du micrologiciel
firmware_update-status-SYNCING_WITH_MCU = Synchronisation avec le MCU
firmware_update-status-REBOOTING = Application de la mise à jour
firmware_update-status-PROVISIONING = Envoi des identifiants Wi-Fi
firmware_update-status-DONE = Mise à jour terminée !
firmware_update-status-ERROR_DEVICE_NOT_FOUND = Impossible de trouver l'appareil
firmware_update-status-ERROR_TIMEOUT = Le processus de mise à jour a dépassé le délai alloué
firmware_update-status-ERROR_DOWNLOAD_FAILED = Échec du téléchargement du micrologiciel
firmware_update-status-ERROR_AUTHENTICATION_FAILED = Échec de l'authentification avec le MCU
firmware_update-status-ERROR_UPLOAD_FAILED = Échec de l'envoi du micrologiciel
firmware_update-status-ERROR_PROVISIONING_FAILED = Impossible de définir les informations d'identification Wi-Fi
firmware_update-status-ERROR_UNSUPPORTED_METHOD = La méthode de mise à jour n'est pas prise en charge
firmware_update-status-ERROR_UNKNOWN = Erreur inconnue

## Dedicated Firmware Update Page

firmware_update-title = Mise à jour du micrologiciel
firmware_update-devices = Appareils disponibles
firmware_update-devices-description = Veuillez sélectionner les capteurs que vous souhaitez mettre à jour à la dernière version du micrologiciel SlimeVR
firmware_update-no_devices = Assurez-vous que les capteurs que vous souhaitez mettre à jour sont allumés et connectés au Wi-Fi !
firmware_update-changelog-title = Mise à jour vers { $version }
firmware_update-looking_for_devices = Recherche d'appareils à mettre à jour...
firmware_update-retry = Réessayer
firmware_update-update = Mettre à jour les capteurs sélectionnés
firmware_update-exit = Quitter

## Tray Menu

tray_menu-show = Afficher
tray_menu-hide = Masquer
tray_menu-quit = Quitter

## First exit modal

tray_or_exit_modal-title = Que devrait faire le bouton de fermeture ?
# Multiline text
tray_or_exit_modal-description =
    Cela permet de choisir entre quitter le serveur ou le réduire à la barre de notifications lorsque le bouton de fermeture est enfoncé.
    Vous pouvez modifier cela plus tard dans les paramètres d'interface !
tray_or_exit_modal-radio-exit = Quitter à la fermeture
tray_or_exit_modal-radio-tray = Minimiser dans la zone de notifications
tray_or_exit_modal-submit = Sauvegarder
tray_or_exit_modal-cancel = Annuler

## Unknown device modal

unknown_device-modal-title = Un nouveau capteur a été trouvé !
unknown_device-modal-description =
    Il y a un nouveau capteur avec l'adresse MAC <b>{ $deviceId }</b>.
    Voulez-vous le connecter à SlimeVR ?
unknown_device-modal-confirm = Oui!
unknown_device-modal-forget = Ignorer
# VRChat config warnings
vrc_config-page-title = Avertissements de configuration VRChat
vrc_config-page-desc = Cette page montre l’état de vos paramètres VRChat et montre quels paramètres sont incompatibles avec SlimeVR. Il est fortement recommandé de corriger tous les avertissements qui s’affichent ici pour la meilleure expérience utilisateur avec SlimeVR.
vrc_config-page-help = Vous ne trouvez pas les paramètres ?
vrc_config-page-help-desc = Consultez notre <a>documentation à ce sujet !</a>
vrc_config-page-big_menu = Suivi et CI (menu principal)
vrc_config-page-big_menu-desc = Paramètres liés au suivi dans le menu principal
vrc_config-page-wrist_menu = Suivi et CI (menu rapide)
vrc_config-page-wrist_menu-desc = Paramètres liés au suivi dans le petit menu des paramètres (menu rapide)
vrc_config-on = Activé
vrc_config-off = Désactivé
vrc_config-setting_name = Nom du paramètre VRChat
vrc_config-recommended_value = Valeur recommandée
vrc_config-current_value = Valeur actuelle
vrc_config-mute = Ignorer l'avertissement
vrc_config-mute-btn = Ignorer
vrc_config-unmute-btn = Ne plus ignorer
vrc_config-legacy_mode = Utiliser l'ancienne méthode de résolution de la CI
vrc_config-disable_shoulder_tracking = Désactiver le suivi des épaules
vrc_config-shoulder_width_compensation = Compensation de la largeur des épaules
vrc_config-spine_mode = Mode colonne vertébrale du suivi du corps
vrc_config-tracker_model = Apparence des traqueurs du suivi du corps
vrc_config-avatar_measurement_type = Mesure de l'avatar
vrc_config-calibration_range = Plage de calibration
vrc_config-calibration_visuals = Afficher les visuels de calibration
vrc_config-user_height = Taille réelle de l'utilisateur
vrc_config-spine_mode-UNKNOWN = Inconnu
vrc_config-spine_mode-LOCK_BOTH = Verrouiller les deux
vrc_config-spine_mode-LOCK_HEAD = Verrouiller la tête
vrc_config-spine_mode-LOCK_HIP = Verrouiller la hanche
vrc_config-tracker_model-UNKNOWN = Inconnu
vrc_config-tracker_model-AXIS = Axe
vrc_config-tracker_model-BOX = Cube
vrc_config-tracker_model-SPHERE = Sphère
vrc_config-tracker_model-SYSTEM = Système
vrc_config-avatar_measurement_type-UNKNOWN = Inconnu
vrc_config-avatar_measurement_type-HEIGHT = Taille
vrc_config-avatar_measurement_type-ARM_SPAN = Envergure des bras

## Error collection consent modal

error_collection_modal-title = Avons nous l'autorisation de collecter les erreurs ?
error_collection_modal-description_v2 =
    { settings-interface-behavior-error_tracking-description_v2 }
    
    Vous pouvez modifier ce paramètre ultérieurement dans la section "Comportement" des paramètres.
error_collection_modal-confirm = Je suis d'accord
error_collection_modal-cancel = Je ne veux pas

## Tracking checklist section

tracking_checklist = Checklist de suivi
tracking_checklist-settings = Paramètres de lachecklist de suivi
tracking_checklist-settings-close = Fermer
tracking_checklist-status-incomplete = Vous n’êtes pas prêt à utiliser SlimeVR !
tracking_checklist-status-partial =
    { $count ->
        [one] Vous avez 1 avertissement !
       *[other] Vous avez { $count } avertissements !
    }
tracking_checklist-status-complete = Vous êtes prêt à utiliser SlimeVR !
tracking_checklist-MOUNTING_CALIBRATION = Effectuer une calibration de l'alignement
tracking_checklist-FEET_MOUNTING_CALIBRATION = Effectuer une calibration de l'alignement des pieds
tracking_checklist-FULL_RESET = Faire une réinitialisation complète
tracking_checklist-FULL_RESET-desc = Certains capteurs nécessitent une réinitialisation.
tracking_checklist-STEAMVR_DISCONNECTED = SteamVR n'est pas lancé
tracking_checklist-STEAMVR_DISCONNECTED-desc = SteamVR n'est pas lancé. L’utilisez-vous pour la VR ?
tracking_checklist-STEAMVR_DISCONNECTED-open = Lancer SteamVR
tracking_checklist-TRACKERS_REST_CALIBRATION = Calibrer vos capteurs
tracking_checklist-TRACKERS_REST_CALIBRATION-desc = Vous n’avez pas fait de calibration de capteur. Veuillez laisser vos capteurs (surlignés en jaune) reposer sur une surface stable pendant quelques seconds.
tracking_checklist-TRACKER_ERROR = Capteurs avec erreur
tracking_checklist-TRACKER_ERROR-desc = Certains de vos capteurs ont une erreur. Veuillez redémarrer les capteurs surlignés en jaune.
tracking_checklist-VRCHAT_SETTINGS = Configurez les paramètres de VRChat
tracking_checklist-VRCHAT_SETTINGS-desc = Vous avez mal configuré les paramètres de VRChat ! Cela peut dégrader votre suivi.
tracking_checklist-VRCHAT_SETTINGS-open = Aller sur les avertissements de VRChat
tracking_checklist-UNASSIGNED_HMD = Casque VR non attribué à la tête
tracking_checklist-UNASSIGNED_HMD-desc = Le casque VR devrait être attribué en tant que capteur de la tête.
tracking_checklist-NETWORK_PROFILE_PUBLIC = Modifier votre profil de réseau
tracking_checklist-NETWORK_PROFILE_PUBLIC-desc =
    { $count ->
        [one] Votre profil de réseau est actuellement défini comme étant public. Ce n’est pas recommandé pour le fonctionnement correct de SlimeVR. <PublicFixLink>Voyez comment y remédier ici.</PublicFixLink>
       *[other]
            Certains de vos adaptateurs réseau sont réglés sur public :
            { $adapters }
            Ce n’est pas recommandé pour que SlimeVR fonctionne correctement.
            <PublicFixLink>Voyez comment y remédier ici.</PublicFixLink>
    }
tracking_checklist-NETWORK_PROFILE_PUBLIC-open = Ouvrir le panneau de configuration
tracking_checklist-STAY_ALIGNED_CONFIGURED = Configurer Garder Aligné
tracking_checklist-STAY_ALIGNED_CONFIGURED-desc = Enregistrez les poses Garder Aligné pour réduire la dérive
tracking_checklist-STAY_ALIGNED_CONFIGURED-open = Ouvrir l'assistant de Garder Aligné
tracking_checklist-ignore = Ignorer
preview-mocap_mode_soon = Mode Mocap (Bientôt™)
preview-disable_render = Désactiver le rendu
preview-disabled_render = Rendu désactivé
toolbar-mounting_calibration = Calibration de l'alignement
toolbar-mounting_calibration-default = Corps
toolbar-mounting_calibration-feet = Pieds
toolbar-mounting_calibration-fingers = Doigts
toolbar-drift_reset = Réinitialisation de la dérive
toolbar-assigned_trackers = { $count } capteurs assignés
toolbar-unassigned_trackers = { $count } capteurs non assignés
