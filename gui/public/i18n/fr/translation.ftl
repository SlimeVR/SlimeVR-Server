### SlimeVR complete GUI translations


# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Connexion au serveur
websocket-connection_lost = Connexion avec le serveur perdue. Reconnexion...

## Tips

tips-find_tracker = Impossible de différencier vos capteurs? Secouez-en un pour qu'il soit mis en évidence.
tips-do_not_move_heels = Assurez-vous de ne pas bouger vos pieds pendant la calibration!
tips-file_select = Glissez et déposez des fichiers à utiliser, ou <u>parcourir</u>.

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

## Proportions

skeleton_bone-NONE = Aucun
skeleton_bone-HEAD = Décalage de la tête
skeleton_bone-NECK = Longueur du cou
skeleton_bone-torso_group = Longueur du torse
skeleton_bone-CHEST = Longueur de la poitrine
skeleton_bone-CHEST_OFFSET = Écart de la poitrine
skeleton_bone-WAIST = Longueur de la taille
skeleton_bone-HIP = Longueur des hanches
skeleton_bone-HIP_OFFSET = Écart de la hanche
skeleton_bone-HIPS_WIDTH = Largeur des hanches
skeleton_bone-leg_group = Longueur des jambes
skeleton_bone-UPPER_LEG = Longueur des jambes supérieures
skeleton_bone-LOWER_LEG = Longueur des jambes inférieures
skeleton_bone-FOOT_LENGTH = Longueur des pieds
skeleton_bone-FOOT_SHIFT = Décalage des pieds
skeleton_bone-SKELETON_OFFSET = Écart du squelette
skeleton_bone-SHOULDERS_DISTANCE = Distance des épaules
skeleton_bone-SHOULDERS_WIDTH = Largeur des épaules
skeleton_bone-arm_group = Longueur des bras
skeleton_bone-UPPER_ARM = Longueur des bras supérieurs
skeleton_bone-LOWER_ARM = Longueur des avant-bras
skeleton_bone-HAND_Y = Distance Y des mains
skeleton_bone-HAND_Z = Distance Z des mains
skeleton_bone-ELBOW_OFFSET = Écart des coudes

## Tracker reset buttons

reset-reset_all = Réinitialiser toutes les proportions
reset-full = Réinitialisation complète
reset-mounting = Réinitialiser l'alignement
reset-yaw = Réinitialisation horizontale

## Serial detection stuff

serial_detection-new_device-p0 = Nouveau périphérique détecté!
serial_detection-new_device-p1 = Entrez vos identifiants Wi-Fi!
serial_detection-new_device-p2 = Veuillez sélectionner quoi en faire
serial_detection-open_wifi = Connecter au Wi-Fi
serial_detection-open_serial = Ouvrir la console série
serial_detection-submit = Soumettre!
serial_detection-close = Fermer

## Navigation bar

navbar-home = Accueil
navbar-body_proportions = Proportions du corps
navbar-trackers_assign = Attribution des capteurs
navbar-mounting = Alignement des capteurs
navbar-onboarding = Assistant de configuration
navbar-settings = Réglages

## Bounding volume hierarchy recording

bvh-start_recording = Enregistrer BVH
bvh-recording = Enregistrement...

## Widget: Overlay settings

widget-overlay = Squelette
widget-overlay-is_visible_label = Superposer le squelette dans SteamVR
widget-overlay-is_mirrored_label = Afficher le squelette en tant que miroir

## Widget: Drift compensation

widget-drift_compensation-clear = Remise à zéro de la compensation de la dérive

## Widget: Developer settings

widget-developer_mode = Mode développeur
widget-developer_mode-high_contrast = Contraste élevé
widget-developer_mode-precise_rotation = Rotation précise
widget-developer_mode-fast_data_feed = Flux de données rapide
widget-developer_mode-filter_slimes_and_hmd = Filtrer les capteurs SlimeVR et le casque VR
widget-developer_mode-sort_by_name = Trier par nom
widget-developer_mode-raw_slime_rotation = Rotation brute
widget-developer_mode-more_info = Plus d'informations

## Widget: IMU Visualizer

widget-imu_visualizer = Rotation
widget-imu_visualizer-rotation_raw = Brute
widget-imu_visualizer-rotation_preview = Aperçu

## Tracker status

tracker-status-none = Pas de statut
tracker-status-busy = Occupé
tracker-status-error = Erreur
tracker-status-disconnected = Déconnecté
tracker-status-occluded = Obstrué
tracker-status-ok = OK

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
tracker-table-column-url = URL

## Tracker rotation

tracker-rotation-front = Avant
tracker-rotation-left = Gauche
tracker-rotation-right = Droite
tracker-rotation-back = Arrière

## Tracker information

tracker-infos-manufacturer = Fabricant
tracker-infos-display_name = Nom
tracker-infos-custom_name = Nom personnalisé
tracker-infos-url = URL du capteur
tracker-infos-version = Version du firmware
tracker-infos-hardware_rev = Révision du hardware

## Tracker settings

tracker-settings-back = Retour à la liste des capteurs
tracker-settings-title = Paramètres du capteur
tracker-settings-assignment_section = Attribution du capteur
tracker-settings-assignment_section-description = À quelle partie du corps le capteur est attribué.
tracker-settings-assignment_section-edit = Ré-attribuer
tracker-settings-mounting_section = Orientation du capteur
tracker-settings-mounting_section-description = Dans quelle direction pointe le capteur?
tracker-settings-mounting_section-edit = Changer l'orientation
tracker-settings-drift_compensation_section = Permettre la compensation de la dérive
tracker-settings-drift_compensation_section-description = Ce capteur devrait-il compenser pour sa dérive si l'option est activée?
tracker-settings-drift_compensation_section-edit = Permettre la compensation de la dérive
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Nom personalisé
tracker-settings-name_section-description = Donnez-lui un joli surnom :3
tracker-settings-name_section-placeholder = Patte gauche d'Erimel

## Tracker part card info

tracker-part_card-no_name = Sans nom
tracker-part_card-unassigned = Non-attribué

## Body assignment menu

body_assignment_menu = Où attribuer ce capteur?
body_assignment_menu-description = Choisissez où attribuer ce capteur. Vous pouvez également gérer tous les capteurs à la fois au lieu d'un à la fois.
body_assignment_menu-show_advanced_locations = Afficher les emplacements d'attribution avancés
body_assignment_menu-manage_trackers = Gérer tous les capteurs
body_assignment_menu-unassign_tracker = Désattribuer

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = Quel capteur à attribuer à votre
tracker_selection_menu-NONE = Quel capteur voulez-vous désattribuer?
tracker_selection_menu-HEAD = { -tracker_selection-part } tête?
tracker_selection_menu-NECK = { -tracker_selection-part } cou?
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part } épaule droite?
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part } bras droit?
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part } avant-bras droit?
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part } main droite?
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part } cuisse droite?
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part } cheville droite?
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part } pied droit?
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part } manette droite?
tracker_selection_menu-CHEST = { -tracker_selection-part } poitrine?
tracker_selection_menu-WAIST = { -tracker_selection-part } taille?
tracker_selection_menu-HIP = { -tracker_selection-part } hanche?
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part } épaule gauche?
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part } bras gauche?
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part } avant-bras gauche?
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part } main gauche?
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part } cuisse gauche?
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part } cheville gauche?
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part } pied gauche?
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part } manette gauche?
tracker_selection_menu-unassigned = Capteurs non-attribués
tracker_selection_menu-assigned = Capteurs attribués
tracker_selection_menu-dont_assign = Ne pas attribuer
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Attention:</b> Un capteur au cou peut être mortel s'il est trop serré,
            la sangle pourrait couper la circulation à la tête!
tracker_selection_menu-neck_warning-done = Je suis conscient des risques
tracker_selection_menu-neck_warning-cancel = Annuler

## Mounting menu

mounting_selection_menu = Dans quelle direction pointe ce capteur?
mounting_selection_menu-close = Fermer

## Sidebar settings

settings-sidebar-title = Réglages
settings-sidebar-general = Général
settings-sidebar-tracker_mechanics = Paramètres des capteurs
settings-sidebar-fk_settings = Paramètres de la capture
settings-sidebar-gesture_control = Contrôle gestuel
settings-sidebar-interface = Interface
settings-sidebar-osc_router = Routeur OSC
settings-sidebar-osc_trackers = Capteurs OSC VRChat
settings-sidebar-utils = Utilitaires
settings-sidebar-serial = Console série

## SteamVR settings

settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = Capteurs SteamVR
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    Activez ou désactivez des capteurs SteamVR.
    Utile pour les jeux ou applications qui ne supportent que certains capteurs.
settings-general-steamvr-trackers-waist = Taille
settings-general-steamvr-trackers-chest = Poitrine
settings-general-steamvr-trackers-feet = Pieds
settings-general-steamvr-trackers-knees = Genoux
settings-general-steamvr-trackers-elbows = Coudes
settings-general-steamvr-trackers-hands = Mains

## Tracker mechanics

settings-general-tracker_mechanics = Paramètres des capteurs
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
settings-general-tracker_mechanics-drift_compensation = Compensation de la dérive
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Compense la dérive des gyroscopes en appliquant une rotation inverse.
    Modifier la force de la compensation et le nombre de réinitialisations prises en compte.
settings-general-tracker_mechanics-drift_compensation-enabled-label = Compensation de la dérive
settings-general-tracker_mechanics-drift_compensation-amount-label = Force de la compensation
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Nombre de réinitialisations prises en compte

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
settings-general-fk_settings-arm_fk = Capture des bras
settings-general-fk_settings-arm_fk-description = Changez la façon dont les bras sont captés.
settings-general-fk_settings-arm_fk-force_arms = Forcer les bras en provenance du casque VR
settings-general-fk_settings-skeleton_settings = Paramètres du squelette
settings-general-fk_settings-skeleton_settings-description = Activez ou désactivez des paramètres avancés de capture.
settings-general-fk_settings-skeleton_settings-extended_spine = Colone vertébrale avancée
settings-general-fk_settings-skeleton_settings-extended_pelvis = Bassin avancé
settings-general-fk_settings-skeleton_settings-extended_knees = Genoux avancés
settings-general-fk_settings-vive_emulation-title = Émulation Vive
settings-general-fk_settings-vive_emulation-description = Simule les problèmes des capteurs de taille que capteurs Vive ont. Cette optionest une blague et rend la capture des mouvements pire.
settings-general-fk_settings-vive_emulation-label = Activer l'émulation Vive

## Gesture control settings (tracker tapping)

settings-general-gesture_control = Contrôle gestuel
settings-general-gesture_control-subtitle = Double tape pour réinitialisation rapide
settings-general-gesture_control-description = Permet de déclencher des réinitialisations en tapant un capteur. Le capteur le plus haut sur votre torse est utilisé pour la réinitialisation horizontale, le capteur le plus haut sur votre jambe gauche est utilisé pour la réinitialisation complète, et le capteur le plus haut sur votre jambe droite est utilisé pour la réinitialisation de l'alignement. Les tapes doivent être enchainées en moins de 0,6 seconde pour être pris en compte.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
        [one] 1 tap
       *[other] { $amount } taps
    }
settings-general-gesture_control-yawResetEnabled = Taper pour réinitialisation horizontale
settings-general-gesture_control-yawResetDelay = Délai de réinitialisation horizontale
settings-general-gesture_control-yawResetTaps = Tapes pour réinitialisation horizontale
settings-general-gesture_control-fullResetEnabled = Taper pour réinitialisation complète
settings-general-gesture_control-fullResetDelay = Délai de réinitialisation complète
settings-general-gesture_control-fullResetTaps = Tapes pour réinitialisation complète
settings-general-gesture_control-mountingResetEnabled = Taper pour réinitialisation de l'alignement
settings-general-gesture_control-mountingResetDelay = Délai de réinitialisation de l'alignement
settings-general-gesture_control-mountingResetTaps = Tapes pour la réinitialisation de l'alignement

## Interface settings

settings-general-interface = Interface
settings-general-interface-dev_mode = Mode développeur
settings-general-interface-dev_mode-description = Ce mode peut être utile pour avoir des données approfondies ou pour interagir avec des capteurs connectés à un niveau plus avancé.
settings-general-interface-dev_mode-label = Mode développeur
settings-general-interface-serial_detection = Détection de périphérique série
settings-general-interface-serial_detection-description = Cette option affichera une fenêtre chaque fois qu'un nouveau périphérique série qui pourrait être un capteur est connecté.
settings-general-interface-serial_detection-label = Détection de périphérique série
settings-general-interface-feedback_sound = Son de retour
settings-general-interface-feedback_sound-description = Cette option va jouer un son lorsqu'une réanitilisation est enclenchée
settings-general-interface-feedback_sound-label = Son de retour
settings-general-interface-feedback_sound-volume = Volume du son de retour
settings-general-interface-theme = Thème de couleurs
settings-general-interface-lang = Sélectionner la langue
settings-general-interface-lang-description = Choisir la langue par défaut.
settings-general-interface-lang-placeholder = Choisissez la langue

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
            Ce qui signifie que les paramètres de Wi-Fi et de calibration <b>seront tous perdus!</b>
settings-serial-factory_reset-warning-ok = Je sais ce que je fais
settings-serial-factory_reset-warning-cancel = Annuler
settings-serial-get_infos = Obtenir des informations
settings-serial-serial_select = Sélectionnez un port série
settings-serial-auto_dropdown_item = Automatique

## OSC router settings

settings-osc-router = Routeur OSC
# This cares about multilines
settings-osc-router-description =
    Transférez les messages OSC provenant d'un autre programme
    Utile pour utiliser un autre programme OSC avec VRChat par exemple.
settings-osc-router-enable = Activer
settings-osc-router-enable-description = Activer/désactiver le transfert de messages.
settings-osc-router-enable-label = Activer
settings-osc-router-network = Ports réseau
# This cares about multilines
settings-osc-router-network-description =
    Définissez les ports pour écouter et envoyer des données.
    Ces ports peuvent être les mêmes que les autres utilisés dans le serveur SlimeVR.
settings-osc-router-network-port_in =
    .label = Port d'entrée
    .placeholder = Port d'entrée (par défaut: 9002)
settings-osc-router-network-port_out =
    .label = Port de sortie
    .placeholder = Port de sortie (par défaut: 9000)
settings-osc-router-network-address = Adresse réseau
settings-osc-router-network-address-description = Choisissez l'adresse vers laquelle envoyer les données.
settings-osc-router-network-address-placeholder = Adresse IPv4

## OSC VRChat settings

settings-osc-vrchat = Capteurs OSC VRChat
# This cares about multilines
settings-osc-vrchat-description =
    Modifiez les paramètres spécifiques à VRChat pour recevoir et envoyer
    des capteurs par OSC (fonctionne sur Quest sans PC).
settings-osc-vrchat-enable = Activer
settings-osc-vrchat-enable-description = Activer/désactiver l'envoi et la réception de données.
settings-osc-vrchat-enable-label = Activer
settings-osc-vrchat-network = Ports réseau
settings-osc-vrchat-network-description = Définissez les ports pour écouter et envoyer des données à VRChat.
settings-osc-vrchat-network-port_in =
    .label = Port d'entrée
    .placeholder = Port d'entrée (par défaut : 9001)
settings-osc-vrchat-network-port_out =
    .label = Port de sortie
    .placeholder = Port de sortie (par défaut : 9000)
settings-osc-vrchat-network-address = Adresse réseau
settings-osc-vrchat-network-address-description = Choisissez l'adresse à laquelle envoyer les données à VRChat (vérifiez les réseaux Wi-Fi de votre appareil).
settings-osc-vrchat-network-address-placeholder = Adresse IP VRChat
settings-osc-vrchat-network-trackers = capteurs
settings-osc-vrchat-network-trackers-description = Sélectionner quels capteurs envoyer via OSC.
settings-osc-vrchat-network-trackers-chest = Poitrine
settings-osc-vrchat-network-trackers-hip = Hanche
settings-osc-vrchat-network-trackers-knees = Genoux
settings-osc-vrchat-network-trackers-feet = Pieds
settings-osc-vrchat-network-trackers-elbows = Coudes

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
settings-osc-vmc-vrm-model_unloaded = Aucun modèle chargé
settings-osc-vmc-vrm-model_loaded =
    { $titled ->
        [true] Modèle chargé : { $name }
       *[other] Modèle sans nom chargé
    }
settings-osc-vmc-vrm-file_select = Glissez et déposez un modèle à utiliser, ou <u>parcourir</u>.
settings-osc-vmc-anchor_hip = Ancrage aux hanches
settings-osc-vmc-anchor_hip-description = Ancrer la capture des mouvements aux hanches, utile pour le VTubing assis.
settings-osc-vmc-anchor_hip-label = Ancrage aux hanches

## Setup/onboarding menu

onboarding-skip = Passer
onboarding-continue = Continuer
onboarding-wip = Pas encore implémenté
onboarding-previous_step = Étape précédente
onboarding-setup_warning =
    <b>Avertissement:</b> La configuration est requise pour assurer une bonne capture des mouvements,
    elle est nécessaire si vous utilisez SlimeVR pour la première fois.
onboarding-setup_warning-skip = Passer la configuration
onboarding-setup_warning-cancel = Continuer la configuration

## Wi-Fi setup

onboarding-wifi_creds-back = Retour à l'introduction
onboarding-wifi_creds = Saisir les identifiants Wi-Fi
# This cares about multilines
onboarding-wifi_creds-description =
    Les capteurs utiliseront ces informations d'identification pour se connecter au réseau.
    Veuillez utiliser les identifiants avec lesquels vous êtes actuellement connecté.
onboarding-wifi_creds-skip = Passer la configuration Wi-Fi
onboarding-wifi_creds-submit = Valider
onboarding-wifi_creds-ssid =
    .label = Nom du Wi-Fi
    .placeholder = Nom
onboarding-wifi_creds-password =
    .label = Mot de passe du Wi-Fi
    .placeholder = Mot de passe

## Mounting setup

onboarding-reset_tutorial-back = Retourner à l'alignement des capteurs
onboarding-reset_tutorial = Didacticiel de réinitialisation
onboarding-reset_tutorial-description = Cette fonctionnalité n'est pas encore terminée, appuyez simplement sur continuer

## Setup start

onboarding-home = Bienvenue sur SlimeVR
onboarding-home-start = Commencer!

## Enter VR part of setup

onboarding-enter_vr-back = Revenir à l'attribution des capteurs
onboarding-enter_vr-title = Il est temps d'entrer en réalité virtuelle!
onboarding-enter_vr-description = Enfilez tous vos capteurs puis entrez en réalité virtuelle!
onboarding-enter_vr-ready = je suis prêt!

## Setup done

onboarding-done-title = Vous êtes prêt!
onboarding-done-description = Amusez-vous bien :)
onboarding-done-close = Fermer le guide

## Tracker connection setup

onboarding-connect_tracker-back = Revenir aux informations d'identification Wi-Fi
onboarding-connect_tracker-title = Connecter les capteurs
onboarding-connect_tracker-description-p0 = Passons maintenant à la partie amusante, en connectant tous les capteurs!
onboarding-connect_tracker-description-p1 = Connectez chaque capteur qui n'est pas encore connecté via un port USB.
onboarding-connect_tracker-issue-serial = J'ai du mal à me connecter!
onboarding-connect_tracker-usb = Capteur USB
onboarding-connect_tracker-connection_status-none = Recherche de capteurs
onboarding-connect_tracker-connection_status-serial_init = Connexion au périphérique en série
onboarding-connect_tracker-connection_status-provisioning = Envoi des identifiants Wi-Fi
onboarding-connect_tracker-connection_status-connecting = Envoi d'identifiants Wi-Fi
onboarding-connect_tracker-connection_status-looking_for_server = Recherche du serveur
onboarding-connect_tracker-connection_status-connection_error = Impossible de se connecter au réseau
onboarding-connect_tracker-connection_status-could_not_find_server = Impossible de trouver le serveur
onboarding-connect_tracker-connection_status-done = Connecté au serveur
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers =
    { $amount ->
        [0] No trackers
        [one] 1 tracker
       *[other] { $amount } trackers
    } connected
onboarding-connect_tracker-next = J'ai connecté tous mes capteurs

## Tracker assignment setup

onboarding-assign_trackers-back = Revenir aux identifiants Wi-Fi
onboarding-assign_trackers-title = Attribuer des capteurs
onboarding-assign_trackers-description = Choisissons où mettre chaque capteur.
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned =
    { $trackers ->
        [one] { $assigned } sur 1 capteur attribué
       *[other] { $assigned } sur { $trackers } capteurs attribués
    }
onboarding-assign_trackers-advanced = Afficher les emplacements d'attribution avancés
onboarding-assign_trackers-next = J'ai attribué tous mes capteurs

## Tracker assignment warnings

# Note for devs, number is used for representing boolean states per bit.
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_FOOT =
    { $unassigned ->
        [0] Le pied gauche est attribué mais il faut que la cheville gauche, la cuisse gauche et soit la poitrine, la hanche ou la taille soient également attribuées!
        [1] Le pied gauche est attribué mais il faut que la cuisse gauche et soit la poitrine, la hanche ou la taille soient également attribuées!
        [2] Le pied gauche est attribué mais il faut que la cheville gauche et soit la poitrine, la hanche ou la taille soient également attribuées!
        [3] Le pied gauche est attribué mais il faut que la poitrine, la hanche ou la taille soient également attribuées!
        [4] Le pied gauche est attribué mais il faut que la cheville gauche et la cuisse gauche soient également attribuées!
        [5] Le pied gauche est attribué mais il faut que la cuisse gauche le soit également!
        [6] Le pied gauche est attribué mais il faut que la cheville gauche le soit également!
       *[unknown] Le pied gauche est attribué mais il faut qu'une autre partie du corps inconnue non assignée soit également attribuée!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_FOOT =
    { $unassigned ->
        [0] Le pied droit est attribué mais il faut que la cheville droite, la cuisse droite et soit la poitrine, la hanche ou la taille soient également attribuées!
        [1] Le pied droit est assigné mais il faut que la cuisse droite et soit la poitrine, la hanche ou la taille soient également attribuées!
        [2] Le pied droit est assigné mais il faut que la cheville droite et soit la poitrine, la hanche ou la taille soient également attribuées!
        [3] Le pied droit est attribué mais il faut que la poitrine, la hanche ou la taille soient également attribuées!
        [4] Le pied droit est attribué mais il faut que la cheville droite et la cuisse droite soient également attribuées!
        [5] Le pied droit est attribué mais il faut que la cuisse droite le soit également!
        [6] Le pied droit est attribué mais il faut que la cheville droite le soit également!
       *[unknown] Le pied droit est assigné mais il faut qu'une autre partie du corps inconnue non assignée soit également attribuée!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_LOWER_LEG =
    { $unassigned ->
        [0] La cheville gauche est attribuée mais il faut que la cuisse gauche et soit la poitrine, la hanche ou la taille soient également attribuées!
        [1] La cheville gauche est attribuée mais il faut que la poitrine, la hanche ou la taille soient également attribuées!
        [2] La cheville gauche est attribuée mais il faut que la cuisse gauche soit également attribuée!
       *[unknown] La cheville gauche est attribuée mais il faut qu'une partie du corps inconnue non assignée soit également attribuée!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_LOWER_LEG =
    { $unassigned ->
        [0] La cheville droite est attribuée mais il faut que la cuisse droite et soit la poitrine, la hanche ou la taille soient également attribuées!
        [1] La cheville droite est attribuée mais il faut que la poitrine, la hanche ou la taille soient également attribuées!
        [2] La cheville droite est attribuée mais il faut que la cuisse droite soit également attribuée !
       *[unknown] La cheville droite est attribuée mais il faut qu'une partie du corps inconnue non assignée soit également attribuée!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_UPPER_LEG =
    { $unassigned ->
        [0] La cuisse gauche est attribuée mais il faut que la poitrine, la hanche ou la taille soient également attribuées!
       *[unknown] La cuisse gauche est attribuée mais il faut qu'une partie du corps inconnue non assignée soit également attribuée!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_UPPER_LEG =
    { $unassigned ->
        [0] La cuisse droite est attribuée mais il faut que la poitrine, la hanche ou la taille soient également attribuées!
       *[unknown] La cuisse droite est attribuée mais il faut qu'une partie du corps inconnue non assignée soit également attribuée!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-HIP =
    { $unassigned ->
        [0] La hanche est attribuée mais il faut que la poitrine le soit aussi!
       *[unknown] La hanche est attribuée mais il faut qu'une partie du corps inconnue non assignée soit également attribuée!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-WAIST =
    { $unassigned ->
        [0] La taille est attribuée mais il faut que la poitrine le soit aussi!
       *[unknown] La taille est attribuée mais il faut qu'une partie du corps inconnue non assignée soit également attribuée!
    }

## Tracker mounting method choose

onboarding-choose_mounting = Quelle méthode de calibration de l’alignement utiliser?
onboarding-choose_mounting-auto_mounting = Alignement automatique
# Italized text
onboarding-choose_mounting-auto_mounting-subtitle = Recommendée
onboarding-choose_mounting-auto_mounting-description = Ceci permettra de détecter automatiquement la direction de tous vos capteurs à partir de 2 poses
onboarding-choose_mounting-manual_mounting = Alignement manuel
# Italized text
onboarding-choose_mounting-manual_mounting-subtitle = Si vous savez ce que vous faites
onboarding-choose_mounting-manual_mounting-description = Ceci vous permettra de choisir la direction de chaque capteur manuellement

## Tracker manual mounting setup

onboarding-manual_mounting-back = Retournez à entrer dans la réalité virtuelle
onboarding-manual_mounting = Alignement manuel
onboarding-manual_mounting-description = Cliquez sur chaque capteur et sélectionnez la manière dont ils sont orientés
onboarding-manual_mounting-auto_mounting = Détection automatique
onboarding-manual_mounting-next = Prochaine étape

## Tracker automatic mounting setup

onboarding-automatic_mounting-back = Retournez à entrer dans la réalité virtuelle
onboarding-automatic_mounting-title = Calibration de l'alignement des capteurs
onboarding-automatic_mounting-description = Pour que vos capteurs SlimeVR fonctionnent, nous devons attribuer une rotation à vos capteurs pour les aligner avec la rotation réelle de ces derniers.
onboarding-automatic_mounting-manual_mounting = Alignement manuel
onboarding-automatic_mounting-next = Prochaine étape
onboarding-automatic_mounting-prev_step = Étape précédente
onboarding-automatic_mounting-done-title = C'est terminé!
onboarding-automatic_mounting-done-description = L'alignement des capteurs est calibré!
onboarding-automatic_mounting-done-restart = Retourner au début
onboarding-automatic_mounting-mounting_reset-title = Réinitialisation de l'alignement
onboarding-automatic_mounting-mounting_reset-step-0 = 1. Accroupissez-vous dans une pose de "ski" avec les jambes pliées, le haut du corps incliné vers l'avant et les bras pliés.
onboarding-automatic_mounting-mounting_reset-step-1 = 2. Appuyez sur le bouton "Réinitialiser l'alignement" et attendez 3 secondes avant que l'alignement des capteurs se calibre.
onboarding-automatic_mounting-preparation-title = Préparation
onboarding-automatic_mounting-preparation-step-0 = 1. Tenez-vous debout avec vos bras à vos côtés.
onboarding-automatic_mounting-preparation-step-1 = 2. Appuyez sur le bouton "Réinitialisation complète" et attendez 3 secondes avant que les capteurs ne se réinitialisent.
onboarding-automatic_mounting-put_trackers_on-title = Enfilez vos capteurs
onboarding-automatic_mounting-put_trackers_on-description = Pour calibrer l'alignement, nous allons utiliser les capteurs que vous venez d'attribuer.
onboarding-automatic_mounting-put_trackers_on-next = J'ai tous mes capteurs

## Tracker proportions method choose

onboarding-choose_proportions = Quelle méthode de calibration des proportions utiliser?
onboarding-choose_proportions-auto_proportions = Proportions automatiques
# Italized text
onboarding-choose_proportions-auto_proportions-subtitle = Recommendée
onboarding-choose_proportions-auto_proportions-description = Ceci estimera vos proportions en enregistrant un extrait de vos mouvements et en le faisant traiter par un algorithme
onboarding-choose_proportions-manual_proportions = Proportions manuelles
# Italized text
onboarding-choose_proportions-manual_proportions-subtitle = Pour les retouches
onboarding-choose_proportions-manual_proportions-description = Ceci vous permettra d'ajuster vos proportions manuellement en les modifiant directement

## Tracker manual proportions setup

onboarding-manual_proportions-back = Revenir au didacticiel de réinitialisation
onboarding-manual_proportions-title = Proportions manuelles du corps
onboarding-manual_proportions-precision = Ajustement de précision
onboarding-manual_proportions-auto = Calibration automatique
onboarding-manual_proportions-ratio = Ajuster par groupes de ratios

## Tracker automatic proportions setup

onboarding-automatic_proportions-back = Revenir au didacticiel de réinitialisation
onboarding-automatic_proportions-title = Calibration des proportions du corps
onboarding-automatic_proportions-description = Pour que les capteurs SlimeVR fonctionnent, nous devons connaître la longueur de vos os.
onboarding-automatic_proportions-manual = Calibration manuelle
onboarding-automatic_proportions-prev_step = Étape précédente
onboarding-automatic_proportions-put_trackers_on-title = Enfilez vos capteurs
onboarding-automatic_proportions-put_trackers_on-description = Pour calibrer vos proportions, nous allons utiliser les capteurs que vous venez d'attribuer.
onboarding-automatic_proportions-put_trackers_on-next = J'ai tous mes capteurs
onboarding-automatic_proportions-requirements-title = Exigences
# Each line of text is a different list item
onboarding-automatic_proportions-requirements-description =
    Vous avez au moins assez de capteurs pour capturer vos pieds (généralement 5 capteurs).
    Vos capteurs et votre casque VR sont allumés.
    Vous portez vos capteurs et votre casque VR.
    Vos capteurs et votre casque VR sont connectés au serveur SlimeVR.
    Vos capteurs et votre casque VR fonctionnent correctement au sein du serveur SlimeVR.
    Votre casque envoie sa position au serveur SlimeVR (cela signifie généralement que SteamVR est ouvert et connecté à SlimeVR en utilisant le driver SteamVR de SlimeVR).
onboarding-automatic_proportions-requirements-next = J'ai lu les exigences
onboarding-automatic_proportions-start_recording-title = Préparez-vous à bouger
onboarding-automatic_proportions-start_recording-description = Nous allons maintenant enregistrer des poses et des mouvements spécifiques.
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
onboarding-automatic_proportions-verify_results-description = Les résultats ci-dessous vous semblent-ils corrects?
onboarding-automatic_proportions-verify_results-results = Enregistrement des résultats
onboarding-automatic_proportions-verify_results-processing = Traitement du résultat
onboarding-automatic_proportions-verify_results-redo = Refaire l'enregistrement
onboarding-automatic_proportions-verify_results-confirm = Les résultats sont corrects
onboarding-automatic_proportions-done-title = Calibration terminée
onboarding-automatic_proportions-done-description = Votre calibration est terminée!

## Home

home-no_trackers = Aucun capteur détecté ou attribué
