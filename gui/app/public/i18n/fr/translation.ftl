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

## Dropdown


## Text input


## File input


## Window controls

titlebar-close = Fermer

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
board_type-CUSTOM = Carte personnalisée
board_type-WEMOSD1MINI = Wemos D1 Mini
board_type-SLIMEVR_DEV = Carte de développement SlimeVR
board_type-GESTURES = Gestes
board_type-GENERIC_NRF = nRF Générique

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
skeleton_bone-LOWER_CHEST-desc =
    Ceci est la distance entre le milieu de votre poitrine et le milieu de votre colonne vertébrale.
    Pour l’ajuster, ajustez correctement la longueur de votre torse et modifiez-la dans différentes positions
    (assis, penché, allongé, etc.) jusqu’à ce que votre colonne vertébrale virtuelle corresponde à votre colonne vertébrale réelle.
skeleton_bone-HIP = Longueur des hanches
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
reset-mounting = Calibration de l'alignement
reset-mounting-feet = Réinitialiser l'alignement des pieds
reset-mounting-fingers = Réinitialiser l'alignement des doigts
reset-yaw = Réinitialisation horizontale
reset-error-mounting-need_full_reset = Nécessite une réinitialisation complète avant de le monter
reset-error-yaw-need_full_reset = Nécessite une réinitialisation complète avant une réinitialisation horizontale

## Navigation bar

navbar-home = Accueil
navbar-body_proportions = Proportions du corps
navbar-trackers_assign = Attribution des capteurs
navbar-mounting = Calibration de l'alignement
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

## Widget: Developer settings

widget-developer_mode = Mode développeur
widget-developer_mode-high_contrast = Contraste élevé
widget-developer_mode-precise_rotation = Rotation précise
widget-developer_mode-fast_data_feed = Flux de données rapide
widget-developer_mode-raw_slime_rotation = Brute

## Widget: IMU Visualizer

widget-imu_visualizer = Rotation
widget-imu_visualizer-preview = Aperçu
widget-imu_visualizer-hide = Masquer
widget-imu_visualizer-rotation_raw = Brute
widget-imu_visualizer-rotation_preview = Aperçu
widget-imu_visualizer-acceleration = Accélération
widget-imu_visualizer-stay_aligned = Garder Aligné

## Tracker status

tracker-status-none = Pas de statut
tracker-status-busy = Occupé
tracker-status-error = Erreur
tracker-status-disconnected = Déconnecté
tracker-status-occluded = Obstrué
tracker-status-timed_out = Délai expiré

## Tracker status columns

tracker-table-column-name = Nom
tracker-table-column-battery = Batterie
tracker-table-column-linear-acceleration = Accél. X/Y/Z
tracker-table-column-stay_aligned = Garder Aligné

## Tracker rotation

tracker-rotation-front = Avant
tracker-rotation-front_left = Avant-Gauche
tracker-rotation-front_right = Avant-Droite
tracker-rotation-left = Gauche
tracker-rotation-right = Droite
tracker-rotation-back = En arrière
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

## Dongle settings

dongle-infos-hardware_revision = Révision du hardware
dongle-status-disconnected = Déconnecté
dongle-settings-back = Retour à la liste des capteurs
dongle-settings-update = Mettre à jour maintenant
dongle-settings-update-title = Version du micrologiciel

## Tracker part card info

tracker-part_card-unassigned = Non-attribué

## Body assignment menu

body_assignment_menu = Dans quelle direction pointe ce capteur ?
body_assignment_menu-description = Choisissez où attribuer ce capteur. Vous pouvez également gérer tous les capteurs à la fois au lieu d'un à la fois.
body_assignment_menu-manage_trackers = Gérer tous les capteurs
body_assignment_menu-unassign_tracker = Désattribuer

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Attention:</b> Un capteur au cou peut être mortel s'il est trop serré,
            la sangle pourrait couper la circulation à la tête !
tracker_selection_menu-neck_warning-done = Je suis conscient des risques
tracker_selection_menu-neck_warning-cancel = Annuler

## Mounting menu

mounting_selection_menu-close = Fermer

## Sidebar settings

settings-sidebar-title = Réglages
settings-sidebar-general = Général
settings-sidebar-stay_aligned = Garder Aligné
settings-sidebar-trackers = Capteurs
settings-sidebar-utils = Utilitaires
settings-sidebar-appearance = Apparence
settings-sidebar-home = Ecran d'accueil
settings-sidebar-checklist = Checklist de suivi
settings-sidebar-firmware-tool = Outil de micrologiciel DIY
settings-sidebar-vrc_warnings = Avertissements de configuration VRChat
settings-sidebar-advanced = Avancé

## Bone routing settings

settings-routing-output-badge-off = Désactivé
settings-routing-group-fingers = Doigts
settings-routing-hands-warning-cancel = Annuler

## SteamVR / Monado output settings

settings-driver-enable = Activer
settings-driver-status-badge-disabled = Désactivé

## Tracker mechanics

settings-general-tracker_mechanics-filtering = Filtrage
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Choisissez le type de filtrage pour vos capteurs.
    La prédiction prédit les mouvements tandis que la fluidification rend les mouvements plus fluides.
settings-general-tracker_mechanics-filtering-type-none = Pas de filtrage
settings-general-tracker_mechanics-filtering-type-none-description = Utilisez les rotations telles quelles.
settings-general-tracker_mechanics-filtering-type-smoothing = Fluidification
settings-general-tracker_mechanics-filtering-type-smoothing-description = Fluidifie les mouvements mais ajoute un peu de latence.
settings-general-tracker_mechanics-filtering-type-prediction = Prédiction
settings-general-tracker_mechanics-filtering-type-prediction-description = Réduit la latence et rend les mouvements plus vifs, mais moins fluides.
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

## Keybinds Page

settings-keybinds_full-reset = Réinitialisation complète
settings-keybinds_yaw-reset = Réinitialisation horizontale
settings-keybinds_reset-all-button = Tout réinitialiser
settings-keybinds-recorder-modal-done-button = Fait
settings-keybinds-recorder-modal-cancel-button = Annuler

## FK/Tracking settings

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
settings-general-fk_settings-leg_fk-reset_mounting_feet-v1 = Forcer la réinitialisation de l'alignement des pieds
settings-general-fk_settings-leg_fk-reset_mounting_feet-description-v1 = Forcer la réinitialisation de l'alignement des pieds pendant la réinitialisation d'alignement générale.
settings-general-fk_settings-enforce_joint_constraints = Limites squelettiques
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints = Appliquer les contraintes
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = Empêche les articulations de tourner au-delà de leur limite
settings-general-fk_settings-ik = Données de position
settings-general-fk_settings-ik-use_position = Utiliser les données de position
settings-general-fk_settings-ik-use_position-description = Permet d'utiliser les données de position des capteurs qui les fournissent. Assurez-vous de faire une réinitialisation complète et de recalibrer en jeu lorsque vous activez cette option.
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

## Gesture control settings (tracker tapping)

settings-general-gesture_control-subtitle = Double tape pour réinitialisation rapide
settings-general-gesture_control-description = Permet de déclencher des réinitialisations en tapant un capteur. Le capteur le plus haut sur votre torse est utilisé pour la réinitialisation horizontale, le capteur le plus haut sur votre jambe gauche est utilisé pour la réinitialisation complète, et le capteur le plus haut sur votre jambe droite est utilisé pour la réinitialisation de l'alignement. Les tapes doivent être enchainées en moins de 0,6 seconde pour être pris en compte.
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

settings-general-interface-feedback_sound = Son de retour
settings-general-interface-feedback_sound-description = Cette option va jouer un son lorsqu'une réanitilisation est enclenchée.
settings-general-interface-feedback_sound-label = Son de retour
settings-general-interface-feedback_sound-volume = Volume du son de retour
settings-general-interface-connected_trackers_warning = Avertissement de capteurs connectés
settings-general-interface-connected_trackers_warning-description = Cette option affichera une fenêtre contextuelle à chaque fois que vous essaierez de quitter SlimeVR en ayant un ou plusieurs capteurs connectés. Il vous rappelle d'éteindre vos capteurs lorsque vous avez terminé pour préserver la durée de vie de la batterie.
settings-general-interface-connected_trackers_warning-label = Avertissement de capteurs connectés en quittant

## Behavior settings

settings-general-interface-dev_mode = Mode développeur
settings-general-interface-dev_mode-label = Mode développeur
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

