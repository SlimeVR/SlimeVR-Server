# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Cargando...
websocket-connection_lost = Conexión con el servidor perdida. Intentando reconectar...
websocket-connection_lost-desc = Parece que el servidor de SlimeVR ha dejado de funcionar. Revise los registros y reinicie el programa
websocket-timedout = No se ha podido conectar al servidor
websocket-timedout-desc = Parece que el servidor de SlimeVR ha dejado de funcionar o se agotó el tiempo de espera de la conexión. Revise los registros y reinicie el programa
websocket-error-close = Salir de SlimeVR
websocket-error-logs = Abra la carpeta de registros

## Update notification

version_update-title = Nueva versión disponible: { $version }
version_update-description = Al hacer clic en "{ version_update-update }" se descargará el instalador de SlimeVR.
version_update-update = Actualizar
version_update-close = Cierra

## Tips

tips-find_tracker = ¿No estás seguro de qué tracker es el correcto? Agitalo y se resaltará el tracker correspondiente.
tips-do_not_move_heels = Asegúrate de no mover los talones durante el guardado de datos.
tips-file_select = Arrastra y suelta los archivos para usarlos o <u>navega</u>.
tips-failed_webgl = No se pudo iniciar WebGL.

## Units

unit-meter = Metro
unit-foot = Pie
unit-inch = Pulgada

## Dropdown


## Text input


## File input


## Window controls

titlebar-close = Cierra

## Body parts

body_part-NONE = Sin asignar
body_part-HEAD = Cabeza
body_part-NECK = Cuello
body_part-RIGHT_SHOULDER = Hombro derecho
body_part-RIGHT_UPPER_ARM = Brazo derecho
body_part-RIGHT_LOWER_ARM = Antebrazo derecho
body_part-RIGHT_HAND = Mano derecha
body_part-RIGHT_UPPER_LEG = Muslo derecho
body_part-RIGHT_LOWER_LEG = Tobillo derecho
body_part-RIGHT_FOOT = Pie derecho
body_part-UPPER_CHEST = Torso superior
body_part-HIP = Cadera
body_part-LEFT_SHOULDER = Hombro izquierdo
body_part-LEFT_UPPER_ARM = Brazo izquierdo
body_part-LEFT_LOWER_ARM = Antebrazo izquierdo
body_part-LEFT_HAND = Mano izquierda
body_part-LEFT_UPPER_LEG = Muslo izquierdo
body_part-LEFT_LOWER_LEG = Tobillo Izquierdo
body_part-LEFT_FOOT = Pie izquierdo
body_part-LEFT_THUMB_METACARPAL = Metacarpiano del pulgar izquierdo
body_part-LEFT_THUMB_PROXIMAL = Proximal del pulgar izquierdo
body_part-LEFT_THUMB_DISTAL = Distal del pulgar izquierdo
body_part-LEFT_INDEX_PROXIMAL = Proximal del índice izquierdo
body_part-LEFT_INDEX_INTERMEDIATE = Intermedio del índice izquierdo
body_part-LEFT_INDEX_DISTAL = Distal del indice izquierdo
body_part-LEFT_MIDDLE_PROXIMAL = Proximal del medio izquierdo
body_part-LEFT_MIDDLE_INTERMEDIATE = Intermedio del medio izquierdo
body_part-LEFT_MIDDLE_DISTAL = Distal del medio izquierdo
body_part-LEFT_RING_PROXIMAL = Proximal del anular izquierdo
body_part-LEFT_RING_INTERMEDIATE = Intermedio de anular izquierdo
body_part-LEFT_RING_DISTAL = Distal del anular izquierdo
body_part-LEFT_LITTLE_PROXIMAL = Proximal del meñique izquierdo
body_part-LEFT_LITTLE_INTERMEDIATE = Intermedio del meñique izquierdo
body_part-LEFT_LITTLE_DISTAL = Distal del meñique izquierdo
body_part-RIGHT_THUMB_METACARPAL = Metacarpiano del pulgar derecho
body_part-RIGHT_THUMB_PROXIMAL = Proximal del pulgar derecho
body_part-RIGHT_THUMB_DISTAL = Distal del pulgar derecho
body_part-RIGHT_INDEX_PROXIMAL = Proximal del indice derecho
body_part-RIGHT_INDEX_INTERMEDIATE = Indermedio del indice derecho
body_part-RIGHT_INDEX_DISTAL = Distal del indice derecho
body_part-RIGHT_MIDDLE_PROXIMAL = Proximal del medio derecho
body_part-RIGHT_MIDDLE_INTERMEDIATE = Intermedio del medio derecho
body_part-RIGHT_MIDDLE_DISTAL = Distal del medio derecho
body_part-RIGHT_RING_PROXIMAL = Proximal del anular derecho
body_part-RIGHT_RING_INTERMEDIATE = Intermedio del anular derecho
body_part-RIGHT_RING_DISTAL = Distal del anular derecho
body_part-RIGHT_LITTLE_PROXIMAL = Proximal del meñique derecho
body_part-RIGHT_LITTLE_INTERMEDIATE = Intermedio del meñique derecho
body_part-RIGHT_LITTLE_DISTAL = Distal del meñique derecho

## BoardType

board_type-UNKNOWN = Desconocido
board_type-CUSTOM = Placa Desconocida
board_type-GLOVE_IMU_SLIMEVR_DEV = Guante SlimeVR Dev IMU

## Proportions

skeleton_bone-NONE = Ninguno
skeleton_bone-HEAD = Desplazamiento de la cabeza
skeleton_bone-HEAD-desc =
    Esta es la distancia desde el visor hasta la mitad de la cabeza.
    Para ajustarlo, mueve la cabeza de izquierda a derecha como si no estuvieras de acuerdo y modifícalo
    hasta que el movimiento en otros trackers sea insignificante.
skeleton_bone-NECK = Longitud del cuello
skeleton_bone-NECK-desc =
    Esta es la distancia desde el medio de tu cabeza hasta la base de tu cuello.
    Para ajustarla, mueve tu cabeza para arriba y abajo como si estuvieras asintiendo
    o inclinalo a la izquierda y la derecha, continuando modificando
    hasta que el movimiento en otros rastreadores es insignificante.
skeleton_bone-torso_group = Longitud del torso
skeleton_bone-torso_group-desc =
    Esta es la distacia desde la base de tu cuello hasta tus caderas.
    Para ajustarla, modifícala mientras estes de pie derecho hasta que tus caderas virtuales
    estan en posicion con las verdaderas.
skeleton_bone-UPPER_CHEST = Longitud del torso superior
skeleton_bone-UPPER_CHEST-desc =
    Esta es la distancia desde la base del cuello hasta la mitad del pecho.
    Para ajustarlo, ajuste la longitud de su torso correctamente y modifíquelo en varias posiciones
    (sentado, inclinado, acostado, etc.) hasta que tu columna vertebral virtual coincida con la real.
skeleton_bone-LOWER_CHEST-desc =
    Esta es la distancia desde la mitad de su pecho hasta la mitad de su columna vertebral.
    Para ajustarlo, ajuste la longitud de su torso correctamente y modifíquelo en varias posiciones
    (sentado, inclinado, acostado, etc.) hasta que su columna vertebral virtual coincida con la real.
skeleton_bone-HIP = Longitud de cadera
skeleton_bone-HIPS_WIDTH = Ancho de la cadera
skeleton_bone-HIPS_WIDTH-desc =
    Esta es la distancia entre el inicio de las piernas.
    Para ajustarlo, realice un reinicio completo con las piernas rectas y modifíquelo hasta
    que tus piernas virtuales coinciden con las reales horizontalmente.
skeleton_bone-leg_group = Longitud de la espinilla
skeleton_bone-leg_group-desc =
    Esta es la distancia desde tus caderas hasta los pies.
    Para ajustarlo, ajuste la longitud de su torso correctamente y modifíquelo
    hasta que tus pies virtuales estén al mismo nivel que los reales.
skeleton_bone-UPPER_LEG = Longitud del muslo
skeleton_bone-UPPER_LEG-desc =
    Esta es la distancia desde las caderas hasta las rodillas.
    Para ajustarlo, ajuste la longitud de la pierna correctamente y modifíquelo
    hasta que tus rodillas virtuales estén al mismo nivel que las reales.
skeleton_bone-LOWER_LEG = Longitud de la espinilla
skeleton_bone-LOWER_LEG-desc =
    Esta es la distancia desde tus rodillas hasta tus tobillos.
    Para ajustarlo, ajuste la longitud de la pierna correctamente y modifíquelo
    hasta que tus rodillas virtuales estén al mismo nivel que las reales.
skeleton_bone-FOOT_LENGTH = Longitud del pie
skeleton_bone-FOOT_LENGTH-desc =
    Esta es la distancia desde tus tobillos hasta los dedos de tus pies.
    Para ajustarlo, camina de puntillas y modifícalo hasta que tus pies virtuales permanezcan en su lugar.
skeleton_bone-FOOT_SHIFT = Desplazamiento del pie
skeleton_bone-FOOT_SHIFT-desc =
    Este valor es la distancia horizontal desde tu rodilla hacia tu tobillo.
    Toma en cuenta la parte baja de tus piernas yendo hacia atrás cuando estes de pie.
    Para ajustarlo, pon el largo de los pies a 0, inicie un reinicio completo y modifícalo hasta que tus pies
    virtuales se alineen con el medio de tus tobillos.
skeleton_bone-SHOULDERS_DISTANCE = Distancia de hombros
skeleton_bone-SHOULDERS_DISTANCE-desc =
    Esta es la distancia vertical desde la base del cuello hasta tus hombros.
    Para ajustarlo, establezca la longitud de la parte superior del brazo en 0 y modifíquelo hasta que tus rastreadores virtuales de tus codos
    se alineen verticalmente con tus hombros reales.
skeleton_bone-SHOULDERS_WIDTH = Ancho de hombros
skeleton_bone-arm_group = Longitud del brazo
skeleton_bone-UPPER_ARM = Longitud del brazo
skeleton_bone-LOWER_ARM = Longitud del antebrazo
skeleton_bone-HAND_Y = Distancia Y de la mano
skeleton_bone-HAND_Z = Distancia Z de la mano

## Tracker reset buttons

reset-reset_all = Reiniciar todas las proporciones
reset-reset_all_warning-reset = Restablecer proporciones
reset-reset_all_warning-cancel = Cancelar
reset-full = Reinicio completo
reset-mounting = Verifique su montaje
reset-mounting-feet = Reiniciar montura de los pies
reset-mounting-fingers = Reiniciar montura de los dedos
reset-yaw = Restablecimiento horizontal

## Navigation bar

navbar-home = Inicio
navbar-body_proportions = Proporciones físicas
navbar-trackers_assign = Asignación de trackers
navbar-mounting = Verifique su montaje
navbar-onboarding = Asistente de Configuración
navbar-settings = Configuración
navbar-connect_trackers = Conectar Trackers

## Biovision hierarchy recording

bvh-start_recording = Grabar BVH
bvh-stop_recording = Guardar grabación BVH
bvh-recording = Grabando...
bvh-save_title = Guardar grabación BVH

## Tracking pause

tracking-unpaused = Pausar tracking
tracking-paused = Reanudar tracking

## Widget: Developer settings

widget-developer_mode = Modo de desarrollador
widget-developer_mode-high_contrast = Contraste alto
widget-developer_mode-precise_rotation = Rotación precisa
widget-developer_mode-fast_data_feed = Flujo de datos rápido
widget-developer_mode-raw_slime_rotation = Sin filtrar

## Widget: IMU Visualizer

widget-imu_visualizer = Rotación
widget-imu_visualizer-preview = Previsualización
widget-imu_visualizer-hide = Ocultar
widget-imu_visualizer-rotation_raw = Sin filtrar
widget-imu_visualizer-rotation_preview = Previsualización
widget-imu_visualizer-acceleration = Aceleración
widget-imu_visualizer-position = Posición
widget-imu_visualizer-stay_aligned = Mantener Alineado

## Tracker status

tracker-status-none = Sin estatus
tracker-status-busy = Ocupado
tracker-status-disconnected = Desconectado
tracker-status-occluded = Ocluída
tracker-status-ok = Ok
tracker-status-timed_out = Sin respuesta

## Tracker status columns

tracker-table-column-name = Nombre
tracker-table-column-type = Tipo
tracker-table-column-battery = Batería
tracker-table-column-temperature = Temperatura °C
tracker-table-column-linear-acceleration = Aceleración X/Y/Z
tracker-table-column-rotation = Rotación X/Y/Z
tracker-table-column-position = Posición X/Y/Z
tracker-table-column-stay_aligned = Mantener Alineado

## Tracker rotation

tracker-rotation-front = Frontal
tracker-rotation-front_left = Frontal-Izquierdo
tracker-rotation-front_right = Frontal-Derecho
tracker-rotation-left = Izquierda
tracker-rotation-right = Derecha
tracker-rotation-back = Parte posterior del brazo
tracker-rotation-back_left = Trasero-Izquierdo
tracker-rotation-back_right = Trasero-Derecho
tracker-rotation-custom = Personalizado

## Tracker information

tracker-infos-manufacturer = Fabricante
tracker-infos-display_name = Nombre visible
tracker-infos-custom_name = Nombre personalizado
tracker-infos-url = URL del Tracker
tracker-infos-hardware_identifier = ID de hardware
tracker-infos-imu = Sensor IMU
tracker-infos-board_type = Placa principal
tracker-infos-network_version = Versión de protocolo
tracker-infos-magnetometer = Magnetómetro
tracker-infos-magnetometer-status-v1 =
    { $status ->
        [DISABLED] Deshabilitado
        [ENABLED] Habilitado
       *[NOT_SUPPORTED] Incompatible
    }

## Tracker settings

tracker-settings-back = Volver a la lista de trackers
tracker-settings-title = Configuración de los trackers
tracker-settings-assignment_section = Asignación
tracker-settings-assignment_section-description = Parte del cuerpo que tiene el tracker asignado.
tracker-settings-assignment_section-edit = Editar asignación
tracker-settings-mounting_section = Posición de montura
tracker-settings-mounting_section-description = ¿Donde está montado el tracker?
tracker-settings-mounting_section-edit = Editar montura
tracker-settings-use_mag = Permitir magnetómetro en este tracker
# Multiline!
tracker-settings-use_mag-description =
    ¿Debería este tracker usar el magnetómetro para reducir el drift cuando se permita el uso de este? <b>¡Por favor, no apague su tracker mientras activas esto!</b>
    
    Primero debe permitir el uso del magnetómetro, <magSetting>haga clic aquí para ir a la configuración</magSetting>.
tracker-settings-use_mag-label = Permitir magnetómetro
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Nombre del tracker
tracker-settings-name_section-placeholder = Pata izquierda del Eevee
tracker-settings-name_section-label = Nombre del tracker
tracker-settings-forget = Olvidar tracker
tracker-settings-forget-description = Elimina el tracker del servidor SlimeVR y evita que se conecte a él hasta que se reinicie el servidor. La configuración del tracker no se perderá.
tracker-settings-forget-label = Olvidar tracker
tracker-settings-update-incompatible = No se puede actualizar. Versión de placa o firmware incompatible
tracker-settings-update-low-battery = No se puede actualizar. Batería inferior al 50%
tracker-settings-update-up_to_date = Actualizado
tracker-settings-update-blocked = Actualización no disponible. No hay otras versiones disponibles
tracker-settings-update = Actualizar ahora
tracker-settings-update-title = Versión del firmware

## Dongle settings

dongle-infos-hardware_revision = Revisión del hardware
dongle-status-disconnected = Desconectado
dongle-settings-back = Volver a la lista de trackers
dongle-settings-update = Actualizar ahora
dongle-settings-update-title = Versión del firmware

## Tracker part card info

tracker-part_card-unassigned = Sin asignar

## Body assignment menu

body_assignment_menu = ¿Dónde quieres colocar el tracker?
body_assignment_menu-description = Elige una ubicación donde desees asignar este tracker. También puedes administrar todos los trackers a la vez en lugar de uno por uno.
body_assignment_menu-manage_trackers = Administrar todos los trackers
body_assignment_menu-unassign_tracker = Desasignar tracker

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Advertencia:</b> Un tracker en el cuello puede ser mortal si está ajustado demasiado fuerte,
    la correa podría cortar la circulación a tu cabeza!
tracker_selection_menu-neck_warning-done = Entiendo los riesgos
tracker_selection_menu-neck_warning-cancel = Cancelar

## Mounting menu

mounting_selection_menu-close = Cierra

## Sidebar settings

settings-sidebar-title = Configuración
settings-sidebar-stay_aligned = Mantener Alineado
settings-sidebar-interface = Interfaz
settings-sidebar-utils = Utilidades
settings-sidebar-appearance = Apariencia
settings-sidebar-notifications = Notificaciones
settings-sidebar-firmware-tool = Herramienta de firmware DIY
settings-sidebar-vrc_warnings = Advertencias de la configuración de VRChat
settings-sidebar-advanced = Avanzado

## Bone routing settings

settings-routing-output-badge-off = Apagado
settings-routing-hands-warning-cancel = Cancelar

## SteamVR / Monado output settings

settings-driver-enable = Habilitar
settings-driver-status-badge-disabled = Apagado

## Tracker mechanics

settings-general-tracker_mechanics-filtering = Filtro
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Escoge el tipo de filtro para tus trackers.
    Predicción predice el movimiento mientras que Suavizado suaviza el movimiento.
settings-general-tracker_mechanics-filtering-type-none = Sin filtrar
settings-general-tracker_mechanics-filtering-type-none-description = Usa las rotaciones tal como son. No hará ningún filtrado.
settings-general-tracker_mechanics-filtering-type-smoothing = Suavizado
settings-general-tracker_mechanics-filtering-type-smoothing-description = Suaviza los movimientos pero añade algo de latencia.
settings-general-tracker_mechanics-filtering-type-prediction = Predicción
settings-general-tracker_mechanics-filtering-type-prediction-description = Reduce la latencia y hace que los movimientos sean mas inmediatos, pero puede aumentar la fluctuación.
settings-general-tracker_mechanics-save_mounting_reset = Guardar la calibración de reajuste de montaje automático
settings-general-tracker_mechanics-save_mounting_reset-description =
    Guarda las calibraciones de reajuste de montaje automático para los trackers entre reinicios. Útil
    cuando se lleva un traje en el que los trackers no se mueven entre sesiones. <b>No recomendado para usuarios normales!</b>
settings-general-tracker_mechanics-save_mounting_reset-enabled-label = Guardar restablecimiento de montaje
settings-general-tracker_mechanics-use_mag_on_all_trackers = Usar el magnetómetro en todos los trackers IMU que lo soporten
settings-general-tracker_mechanics-use_mag_on_all_trackers-description =
    Utiliza el magnetómetro en todos los trackers que tienen un firmware compatible con él, lo que reduce el drift en entornos magnéticos estables.
    Se puede desactivar por rastreador en la configuración de los trackers. <b>¡Por favor, no apagues ninguno de los trackers mientras activas esta opción!</b>
settings-general-tracker_mechanics-use_mag_on_all_trackers-label = Usar magnetómetro en los rastreadores
settings-stay_aligned-description = Mantener Alineado reduce el drift ajustando gradualmente tus trackers para que coincidan con tus poses relajadas
settings-stay_aligned-setup-label = Configurar Mantener Alineado
settings-stay_aligned-setup-description = Debe completar "Configurar Mantener Alineado" para habilitar Mantener Alineado.
settings-stay_aligned-enabled-label = Ajustar trackers
settings-stay_aligned-relaxed_poses-label = Posturas relajadas
settings-stay_aligned-relaxed_poses-description = Mantener Alineado utiliza tus posturas relajadas para mantener los trackers alineados. Usa "Configurar Mantener Alineado" para actualizar estas posturas.
settings-stay_aligned-relaxed_poses-standing = Ajustar los trackers mientras estás de pie
settings-stay_aligned-relaxed_poses-sitting = Ajustar los trackers mientras estás sentado en una silla
settings-stay_aligned-relaxed_poses-flat = Ajuste los trackers mientras estás sentado en el suelo o acostado boca arriba
settings-stay_aligned-relaxed_poses-save_pose = Guardar pose
settings-stay_aligned-relaxed_poses-reset_pose = Restablecer pose
settings-stay_aligned-relaxed_poses-close = Cierra
settings-stay_aligned-debug-label = Depuración
settings-stay_aligned-debug-description = Incluya su configuración cuando informe problemas sobre Mantener Alineado.
settings-stay_aligned-debug-copy-label = Copiar ajustes al portapapeles

## Keybinds Page

settings-keybinds_full-reset = Reinicio completo
settings-keybinds_yaw-reset = Restablecimiento horizontal
settings-keybinds_reset-all-button = Restablecer todo
settings-keybinds-recorder-modal-done-button = Hecho
settings-keybinds-recorder-modal-cancel-button = Cancelar

## FK/Tracking settings

# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Anclado al suelo
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Corrección del patinaje
settings-general-fk_settings-leg_tweak-toe_snap = Alineación de pies
settings-general-fk_settings-leg_tweak-foot_plant = Planta del pie
settings-general-fk_settings-leg_tweak-skating_correction-amount = Intensidad de la corrección del patinaje
settings-general-fk_settings-leg_tweak-skating_correction-description = Corrección del patinaje corrige el deslizamiento en el suelo, pero puede disminuir la precisión en ciertos movimientos. Activar esta opción requerirá realizar un reinicio general y recalibrar en el juego.
settings-general-fk_settings-leg_tweak-floor_clip-description = Anclado al suelo puede reducir o incluso eliminar el atravesar el piso con tu modelo. Al habilitar esto, asegúrese de hacer un "reinicio completo" y recalibrar en el juego.
settings-general-fk_settings-leg_tweak-toe_snap-description = "Acople de puntera" intenta adivinar la rotación de tus pies si los trackers de estos no están en uso.
settings-general-fk_settings-leg_tweak-foot_plant-description = El plantado de pie gira los pies para que queden paralelos al suelo en el momento del contacto.
settings-general-fk_settings-leg_fk = Tracking de piernas
settings-general-fk_settings-leg_fk-reset_mounting_feet-v1 = Forzar reinicio de la montura de los pies.
settings-general-fk_settings-leg_fk-reset_mounting_feet-description-v1 = Forzar el restablecimiento del montaje de los pies durante los reinicios generales del montaje.
settings-general-fk_settings-enforce_joint_constraints = Límites esqueléticos
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints = Imponer restricciones
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = Evita que las articulaciones giren más allá de su límite
settings-general-fk_settings-arm_fk-back = Parte posterior del brazo
settings-general-fk_settings-arm_fk-back-description = Modo predeterminado, con los brazos hacia atrás y los antebrazos hacia adelante.
settings-general-fk_settings-arm_fk-tpose_up = T-pose (arriba)
settings-general-fk_settings-arm_fk-tpose_up-description = Se espera que tus brazos esten relajados, perpendicular a tu cuerpo durante el reinicio completo y 90 grados respecto a tu cuerpo durante el reinicio de montaje.
settings-general-fk_settings-arm_fk-tpose_down = T-pose (abajo)
settings-general-fk_settings-arm_fk-tpose_down-description = Se espera que tus brazos estén a 90 grados respecto a tu cuerpo durante el reinicio completo y relajados, perpendicular a tu cuerpo durante el reinicio del montaje.
settings-general-fk_settings-arm_fk-forward = Siguiente
settings-general-fk_settings-arm_fk-forward-description = Espera que tus brazos estén 90 grados hacia adelante. Útil para VTubing.
settings-general-fk_settings-skeleton_settings-ratios = Proporciones del esqueleto
settings-general-fk_settings-skeleton_settings-ratios-description = Cambia los valores de la configuración del esqueleto. Es posible que debas ajustar tus proporciones de nuevo.
settings-general-fk_settings-self_localization-title = Modo Captura de movimiento

## Gesture control settings (tracker tapping)

settings-general-gesture_control-subtitle = Reinicio basado en toques
settings-general-gesture_control-description = Permite que los reinicios se activen tocando un tracker. El tracker situado más arriba en tu torso se utiliza para el restablecimiento horizontal, el tracker situado más arriba en tu pierna izquierda se utiliza para el reinicio completo y el tracker situado más arriba en tu pierna derecha se utiliza para el reinicio de montaje. Los toques deben producirse dentro del tiempo límite de 0,3 segundos multiplicado por el número de toques a reconocer.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
        [one] 1 toque
        [many] { $amount } toques
       *[other] { $amount } toques
    }
# This is a unit: 3 trackers, 2 trackers, 1 tracker
# $amount (Number) - Amount of trackers
settings-general-gesture_control-trackers =
    { $amount ->
        [one] tracker
        [many] trackers
       *[other] trackers
    }
settings-general-gesture_control-yawResetEnabled = Activa toque para restablecimiento horizontal
settings-general-gesture_control-yawResetDelay = Retraso de restablecimiento horizontal
settings-general-gesture_control-yawResetTaps = Toques para restablecimiento horizontal
settings-general-gesture_control-fullResetEnabled = Activa toque para reinicio completo
settings-general-gesture_control-fullResetDelay = Retraso de reinicio completo
settings-general-gesture_control-fullResetTaps = Da toquecitos para reinicio completo
settings-general-gesture_control-mountingResetEnabled = Activa toquecitos para reiniciar montaje
settings-general-gesture_control-mountingResetDelay = Reinicio de montaje retrasado
settings-general-gesture_control-mountingResetTaps = Da toquecitos para reinicio de montaje
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = Trackers por encima del limite
settings-general-gesture_control-numberTrackersOverThreshold-description = Aumenta este valor si la detección de toqueteo no funciona. No lo aumentes por encima de lo necesario para que la detección de toques funcione, ya que crearía más falsos positivos

## Appearance settings

settings-interface-appearance = Apariencia
settings-general-interface-dev_mode = Modo de desarrollador
settings-general-interface-dev_mode-description = Este modo puede ser útil si necesitas datos en profundidad o para interactuar con los trackers conectados a un nivel más avanzado
settings-general-interface-dev_mode-label = Modo de desarrollador
settings-general-interface-lang = Seleccionar idioma
settings-general-interface-lang-description = Cambiar el idioma predeterminado que deseas utilizar.
settings-general-interface-lang-placeholder = Seleccionar el idioma que desea utilizar
# Keep the font name untranslated
settings-interface-appearance-font = Fuente de GUI
settings-interface-appearance-font-description = Esto cambia la fuente utilizada por la interfaz
settings-interface-appearance-font-placeholder = Fuente predeterminada
settings-interface-appearance-font-os_font = Fuente SO
settings-interface-appearance-font-slime_font = Fuente predeterminada
settings-interface-appearance-font_size = Escala de la fuente base
settings-interface-appearance-font_size-description = Esto afecta al tamaño de la fuente de toda la interfaz excepto este panel de configuración

## Notification settings

settings-interface-notifications = Notificaciones
settings-general-interface-feedback_sound = Sonido de feedback
settings-general-interface-feedback_sound-description = Esta opción reproducirá un sonido cuando se activa un reinicio
settings-general-interface-feedback_sound-label = Sonido de feedback
settings-general-interface-feedback_sound-volume = Volumen del sonido de feedback
settings-general-interface-connected_trackers_warning = Advertencia de trackers conectados
settings-general-interface-connected_trackers_warning-description = Esta opción mostrará una ventana emergente cada vez que intentes salir de SlimeVR mientras tengas uno o más trackers conectados. Te recuerda que debes apagar los trackers cuando hayas terminado para ahorrar batería.
settings-general-interface-connected_trackers_warning-label = Aviso de trackers conectados al cerrar

## Behavior settings

settings-general-interface-dev_mode = Modo de desarrollador
settings-general-interface-dev_mode-label = Modo de desarrollador
settings-general-interface-use_tray = Minimizar en la bandeja del sistema
settings-general-interface-use_tray-description = Te permite cerrar la ventana sin cerrar SlimeVR para que pueda seguir usándolo sin que la interfaz te moleste.
settings-general-interface-use_tray-label = Minimizar en la bandeja del sistema
settings-general-interface-discord_presence = Compartir actividad en Discord
settings-general-interface-discord_presence-description = Le indica a tu cliente de Discord que estás usando SlimeVR junto con el número de rastreadores IMU que estás usando.
settings-general-interface-discord_presence-label = Compartir actividad en Discord
settings-general-interface-discord_presence-message =
    { $amount ->
        [0] Recolectando slimes
        [one] Usando 1 tracker
       *[other] Usando { $amount } trackers
    }
settings-interface-behavior-error_tracking = Recopilación de errores a través de Sentry.io
settings-interface-behavior-error_tracking-description_v2 =
    <h1>¿Da su consentimiento para la recopilación de datos de error anónimos?</h1>
    
    <b>No recopilamos información personal</b> , como su dirección IP o credenciales inalámbricas. ¡SlimeVR valora tu privacidad!
    
    Para proporcionar la mejor experiencia de usuario, recopilamos informes de errores anónimos, métricas de rendimiento e información del sistema operativo. Esto nos ayuda a detectar errores y problemas con SlimeVR. Estas métricas se recopilan a través de Sentry.io.
settings-interface-behavior-error_tracking-label = Enviar errores a los desarrolladores
settings-interface-behavior-bvh_directory = Directorio para guardar grabaciones BVH
settings-interface-behavior-bvh_directory-description = Elija un directorio para guardar sus grabaciones BVH en lugar de tener que elegir dónde guardarlas cada vez.
settings-interface-behavior-bvh_directory-label = Directorio de grabaciones BVH

## Serial settings

settings-serial-connection_lost = Conexión a puerto serial perdida, Reconectando...
settings-serial-reboot = Reiniciar
settings-serial-factory_reset = Restablecimiento de fábrica
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>Advertencia:</b> Esto restablecerá la configuración de fábrica del tracker.
    Esto significa que los ajustes de Wi-Fi y calibración <b>se perderán</b>.

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

