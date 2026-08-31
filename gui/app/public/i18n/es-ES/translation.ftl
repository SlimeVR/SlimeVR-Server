# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Conectándose al servidor
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
version_update-close = Cerrar

## Tips

tips-find_tracker = ¿No estás seguro de qué tracker es el correcto? Agitalo y se resaltará el tracker correspondiente.
tips-do_not_move_heels = Asegúrate de no mover los talones durante el guardado de datos.
tips-file_select = Arrastra y suelta los archivos para usarlos o <u>navega</u>.
tips-failed_webgl = No se pudo iniciar WebGL.

## Units

unit-meter = Metro
unit-foot = Pie
unit-inch = Pulgada
unit-cm = cm

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
body_part-CHEST = Pecho
body_part-WAIST = Cintura
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
board_type-NODEMCU = NodeMCU
board_type-CUSTOM = Placa Desconocida
board_type-WROOM32 = WROOM32
board_type-WEMOSD1MINI = Wemos D1 Mini
board_type-TTGO_TBASE = TTGO T-Base
board_type-ESP01 = ESP-01
board_type-SLIMEVR = SlimeVR
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
skeleton_bone-CHEST = Longitud del pecho
skeleton_bone-CHEST-desc =
    Esta es la distancia desde la mitad de su pecho hasta la mitad de su columna vertebral.
    Para ajustarlo, ajuste la longitud de su torso correctamente y modifíquelo en varias posiciones
    (sentado, inclinado, acostado, etc.) hasta que su columna vertebral virtual coincida con la real.
skeleton_bone-WAIST = Longitud de cintura
skeleton_bone-WAIST-desc =
    Esta es la distancia desde la mitad de la columna vertebral hasta el ombligo.
    Para ajustarlo, ajuste la longitud de su torso correctamente y modifíquelo en varias posiciones
    (sentado, inclinado, acostado, etc.) hasta que tu columna vertebral virtual coincida con la real.
skeleton_bone-HIP = Longitud de cadera
skeleton_bone-HIP-desc =
    Esta es la distancia desde el ombligo hasta tus caderas.
    Para ajustarlo, configure la longitud de su torso correctamente y modifíquelo en varias posiciones
    (sentado, inclinado, acostado, etc.) hasta que tu columna virtual coincida con la real.
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
reset-mounting = Reiniciar montura
reset-mounting-feet = Reiniciar montura de los pies
reset-mounting-fingers = Reiniciar montura de los dedos
reset-yaw = Restablecimiento horizontal

## Serial detection stuff

serial_detection-new_device-p0 = ¡Nuevo dispositivo serial detectado!
serial_detection-new_device-p1 = ¡Introduce las credenciales de tu red Wi-Fi!
serial_detection-new_device-p2 = Por favor, selecciona lo que desea hacer con él
serial_detection-open_wifi = Conectar a una red Wi-Fi
serial_detection-open_serial = Abrir la consola serial
serial_detection-submit = ¡Enviar!
serial_detection-close = Cerrar

## Navigation bar

navbar-home = Inicio
navbar-body_proportions = Proporciones físicas
navbar-trackers_assign = Asignación de trackers
navbar-mounting = Calibración de montura
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
widget-developer_mode-sort_by_name = Ordenar por nombre
widget-developer_mode-raw_slime_rotation = Rotación sin filtrar
widget-developer_mode-more_info = Más información

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
tracker-status-error = Error
tracker-status-disconnected = Desconectado
tracker-status-occluded = Ocluída
tracker-status-ok = Ok
tracker-status-timed_out = Sin respuesta

## Tracker status columns

tracker-table-column-name = Nombre
tracker-table-column-type = Tipo
tracker-table-column-battery = Batería
tracker-table-column-ping = Ping
tracker-table-column-tps = TPS
tracker-table-column-temperature = Temperatura °C
tracker-table-column-linear-acceleration = Aceleración X/Y/Z
tracker-table-column-rotation = Rotación X/Y/Z
tracker-table-column-position = Posición X/Y/Z
tracker-table-column-stay_aligned = Mantener Alineado
tracker-table-column-url = URL

## Tracker rotation

tracker-rotation-front = Frontal
tracker-rotation-front_left = Frontal-Izquierdo
tracker-rotation-front_right = Frontal-Derecho
tracker-rotation-left = Izquierda
tracker-rotation-right = Derecha
tracker-rotation-back = Trasero
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
tracker-settings-name_section-description = Dale un sobrenombre lindo :)
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

## Tracker part card info

tracker-part_card-unassigned = Sin asignar

## Body assignment menu

body_assignment_menu = ¿Donde quieres que esté este tracker?
body_assignment_menu-description = Elige una ubicación donde desees asignar este tracker. También puedes administrar todos los trackers a la vez en lugar de uno por uno.
body_assignment_menu-manage_trackers = Administrar todos los trackers
body_assignment_menu-unassign_tracker = Desasignar tracker

## Tracker assignment menu

# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Advertencia:</b> Un tracker en el cuello puede ser mortal si está ajustado demasiado fuerte,
    la correa podría cortar la circulación a tu cabeza!
tracker_selection_menu-neck_warning-done = Entiendo los riesgos
tracker_selection_menu-neck_warning-cancel = Cancelar

## Mounting menu

mounting_selection_menu = ¿Dónde quieres colocar el tracker?
mounting_selection_menu-close = Cerrar

## Sidebar settings

settings-sidebar-title = Configuración
settings-sidebar-general = General
settings-sidebar-stay_aligned = Mantener Alineado
settings-sidebar-interface = Interfaz
settings-sidebar-osc_trackers = VRChat OSC Trackers
settings-sidebar-utils = Utilidades
settings-sidebar-serial = Consola serial
settings-sidebar-appearance = Apariencia
settings-sidebar-notifications = Notificaciones
settings-sidebar-behavior = Comportamiento
settings-sidebar-firmware-tool = Herramienta de firmware DIY
settings-sidebar-vrc_warnings = Advertencias de la configuración de VRChat
settings-sidebar-advanced = Avanzado

## Tracker mechanics

settings-general-tracker_mechanics-filtering = Filtro
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Escoge el tipo de filtro para tus trackers.
    Predicción predice el movimiento mientras que Suavizado suaviza el movimiento.
settings-general-tracker_mechanics-filtering-type = Tipo de filtro
settings-general-tracker_mechanics-filtering-type-none = Sin filtrar
settings-general-tracker_mechanics-filtering-type-none-description = Usa las rotaciones tal como son. No hará ningún filtrado.
settings-general-tracker_mechanics-filtering-type-smoothing = Suavizado
settings-general-tracker_mechanics-filtering-type-smoothing-description = Suaviza los movimientos pero añade algo de latencia.
settings-general-tracker_mechanics-filtering-type-prediction = Predicción
settings-general-tracker_mechanics-filtering-type-prediction-description = Reduce la latencia y hace que los movimientos sean mas inmediatos, pero puede aumentar la fluctuación.
settings-general-tracker_mechanics-filtering-amount = Cantidad
settings-general-tracker_mechanics-yaw-reset-smooth-time = Tiempo de suavizado al restablecer el eje horizontal (0s deshabilita el suavizado)
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
settings-stay_aligned = Mantener Alineado
settings-stay_aligned-description = Mantener Alineado reduce el drift ajustando gradualmente tus trackers para que coincidan con tus poses relajadas
settings-stay_aligned-setup-label = Configurar Mantener Alineado
settings-stay_aligned-setup-description = Debe completar "Configurar Mantener Alineado" para habilitar Mantener Alineado.
settings-stay_aligned-enabled-label = Ajustar trackers
settings-stay_aligned-general-label = General
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

## FK/Tracking settings

settings-general-fk_settings = Configuración de FK Tracking
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
settings-general-fk_settings-leg_fk-reset_mounting_feet-description-v1 = Forzar el restablecimiento del montaje de los pies durante los reinicios generales del montaje.
settings-general-fk_settings-leg_fk-reset_mounting_feet-v1 = Forzar reinicio de la montura de los pies.
settings-general-fk_settings-enforce_joint_constraints = Límites esqueléticos
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints = Imponer restricciones
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = Evita que las articulaciones giren más allá de su límite
settings-general-fk_settings-arm_fk-reset_mode-description = Cambia la posición por defecto para el restablecimiento de montura
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
settings-general-fk_settings-self_localization-description = El modo captura de movimiento permite al esqueleto seguir aproximadamente tu posición sin auriculares ni otros trackers. Ten en cuenta que esto requiere trrackers de pies y cabeza para funcionar y que aún está en fase experimental.

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
settings-general-interface-theme = Temas
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
settings-general-interface-serial_detection = Detección de dispositivos seriales
settings-general-interface-serial_detection-description = Esta opción mostrará una ventana emergente cada vez que conectes un nuevo dispositivo serie que podría ser un tracker. Ayuda a mejorar el proceso de configuración de un tracker.
settings-general-interface-serial_detection-label = Detección de dispositivos seriales
settings-general-interface-feedback_sound = Sonido de feedback
settings-general-interface-feedback_sound-description = Esta opción reproducirá un sonido cuando se activa un reinicio
settings-general-interface-feedback_sound-label = Sonido de feedback
settings-general-interface-feedback_sound-volume = Volumen del sonido de feedback
settings-general-interface-connected_trackers_warning = Advertencia de trackers conectados
settings-general-interface-connected_trackers_warning-description = Esta opción mostrará una ventana emergente cada vez que intentes salir de SlimeVR mientras tengas uno o más trackers conectados. Te recuerda que debes apagar los trackers cuando hayas terminado para ahorrar batería.
settings-general-interface-connected_trackers_warning-label = Aviso de trackers conectados al cerrar

## Behavior settings

settings-interface-behavior = Comportamiento
settings-general-interface-use_tray = Minimizar a la bandeja
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

settings-serial = Consola serial
# This cares about multilines
settings-serial-description =
    Esta es una fuente de información en vivo para la comunicación serial.
    Puede ser útil si necesitas saber si el firmware está fallando.
settings-serial-connection_lost = Conexión a puerto serial perdida, Reconectando...
settings-serial-reboot = Reiniciar
settings-serial-factory_reset = Restablecimiento de fábrica
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>Advertencia:</b> Esto restablecerá la configuración de fábrica del tracker.
    Esto significa que los ajustes de Wi-Fi y calibración <b>se perderán</b>.
settings-serial-factory_reset-warning-ok = Sé lo que estoy haciendo
settings-serial-factory_reset-warning-cancel = Cancelar
settings-serial-serial_select = Selecciona un puerto serial
settings-serial-auto_dropdown_item = Automático
settings-serial-get_wifi_scan = Obtener escaneo WiFi
settings-serial-save_logs = Guardar en archivo
settings-serial-send_command = Enviar
settings-serial-send_command-warning-cancel = Cancelar

## OSC VRChat settings

settings-osc-vrchat = VRChat OSC Trackers
# This cares about multilines
settings-osc-vrchat-description-v1 =
    Change settings specific to the OSC Trackers standard used for sending
    tracking data to applications without SteamVR (ex. Quest standalone).
    Make sure to enable OSC in VRChat via the Action Menu under OSC > Enabled.
    To allow receiving HMD and controller data from VRChat, go in your main menu's
    settings under Tracking & IK > Allow Sending Head and Wrist VR Tracking OSC Data.
settings-osc-vrchat-enable = Habilitar
settings-osc-vrchat-enable-description = Alternar el envío y la recepción de datos
settings-osc-vrchat-enable-label = Habilitar
settings-osc-vrchat-network = Puertos de red
settings-osc-vrchat-network-port_in =
    .label = Puerto de entrada
    .placeholder = Puerto de entrada (Por defecto: 9001)
settings-osc-vrchat-network-port_out =
    .label = Puerto de salida
    .placeholder = Puerto de salida (Por defecto: 9000)
settings-osc-vrchat-network-address = Dirección de red
settings-osc-vrchat-network-address-description-v1 = Elige a qué dirección enviar los datos. Se puede dejar sin modificar para VRChat.
settings-osc-vrchat-network-address-placeholder = Dirección IP de VRChat

## VMC OSC settings

settings-osc-vmc = Captura de movimiento virtual
# This cares about multilines
settings-osc-vmc-description = Cambiar la configuración al protocolo VMC (Virtual Motion Capture) para enviar datos de SlimeVR y recibir información de otras apps.
settings-osc-vmc-enable = Habilitar
settings-osc-vmc-enable-description = Alterna el envío y recepción de datos.
settings-osc-vmc-enable-label = Habilitar
settings-osc-vmc-network = Puertos de red
settings-osc-vmc-network-description = Establece los puertos para escuchar y enviar datos via VMC.
settings-osc-vmc-network-port_in =
    .label = Puerto de entrada
    .placeholder = Puerto de entrada (Por defecto: 39540)
settings-osc-vmc-network-port_out =
    .label = Puerto de salida
    .placeholder = Puerto de salida (Por defecto: 39539)
settings-osc-vmc-network-address = Dirección de red
settings-osc-vmc-network-address-description = Elige la dirección a la que se enviarán los datos vía VMC.
settings-osc-vmc-network-address-placeholder = Dirección IPV4
settings-osc-vmc-vrm = Modelo VRM
settings-osc-vmc-vrm-description = Cargar un modelo VRM para permitir el anclaje de la cabeza y posibilitar una mayor compatibilidad con otras aplicaciones.
settings-osc-vmc-vrm-untitled_model = Modelo sin título
settings-osc-vmc-vrm-file_select = Arrastre y suelte un modelo para utilizarlo, o <u>busquelo</u>
settings-osc-vmc-anchor_hip = Anclar a la cadera
settings-osc-vmc-anchor_hip-description = Ancla el tracking a la cadera, útil para VTubing sentado. Si se deshabilita, carga un modelo VRM.
settings-osc-vmc-anchor_hip-label = Anclar a la cadera
settings-osc-vmc-mirror_tracking = Invertir el tracking
settings-osc-vmc-mirror_tracking-description = Invierte el tracking horizontalmente.
settings-osc-vmc-mirror_tracking-label = Invertir el tracking

## Common OSC settings


## Advanced settings

settings-utils-advanced = Avanzado
settings-utils-advanced-reset-gui = Restablecer configucación del GUI
settings-utils-advanced-reset-gui-description = Restaurar la configuración predeterminado para el interfaz.
settings-utils-advanced-reset-gui-label = Restablecer el GUI
settings-utils-advanced-reset-server = Restablecer la configuración del tracking
settings-utils-advanced-reset-server-description = Restaurar la configuración predeterminado para el tracking,
settings-utils-advanced-reset-server-label = Restablecer el tracking,
settings-utils-advanced-reset-all = Restablecer todas las configuraciónes
settings-utils-advanced-reset-all-description = Restaurar la configuración predeterminada para el interfaz y el tracking.
settings-utils-advanced-reset-all-label = Restablecer todo
settings-utils-advanced-reset_warning =
    { $type ->
        [gui]
            <b>Advertencia:</b> Esto restablecerá la configuración de la interfaz a los valores predeterminados
            ¿Esta seguro de que quiere hacer esto?
        [server]
            <b>Advertencia:</b> Esto restablecerá la configuración de los trackers a los valores predeterminados
            ¿Esta seguro de que quiere hacer esto?
       *[all]
            <b>Advertencia:</b> Esto restablecerá todas las configuraciones a los valores predeterminados
            ¿Esta seguro de que quiere hacer esto?
    }
settings-utils-advanced-reset_warning-reset = Restablecer la configuración
settings-utils-advanced-reset_warning-cancel = Cancelar
settings-utils-advanced-open_data-v1 = Carpeta de configuración
settings-utils-advanced-open_data-description-v1 = Abrirá la carpeta de configuración de SlimeVR en el explorador de archivos, que contiene la configuración
settings-utils-advanced-open_data-label = Abrir carpeta
settings-utils-advanced-open_logs = Carpeta de registros
settings-utils-advanced-open_logs-description = Abra la carpeta de registros de SlimeVR en el explorador de archivos, que contiene los registros de la aplicación
settings-utils-advanced-open_logs-label = Abrir carpeta

## Home Screen


## Tracking Checlist


## Setup/onboarding menu

onboarding-skip = Omitir configuración
onboarding-continue = Continuar
onboarding-previous_step = Paso anterior
onboarding-setup_warning =
    <b>Advertencia:</b> La configuración inicial es necesaria para un buen tracking,
    es necesaria si es la primera vez que usas SlimeVR
onboarding-setup_warning-skip = Omitir configuración
onboarding-setup_warning-cancel = Continuar con la configuración

## Quiz


## Wi-Fi setup

onboarding-wifi_creds-submit = ¡Enviar!
onboarding-wifi_creds-ssid =
    .label = Nombre Wi-Fi
    .placeholder = Introduce nombre de Wi-Fi
onboarding-wifi_creds-ssid-required = Es necesario el nombre del Wi-Fi
onboarding-wifi_creds-password =
    .label = Contraseña
    .placeholder = Introduce contraseña

## Install info


## Setup start

onboarding-home = Bienvenido a SlimeVR
onboarding-home-start = ¡Vamos a prepararnos!

## Tracker connection setup

onboarding-connect_tracker-title = Conectar trackers
onboarding-connect_tracker-issue-serial = ¡Tengo problemas para conectarme!
onboarding-connect_tracker-usb = Tracker USB
onboarding-connect_tracker-connection_status-serial_init = Conectándose al dispositivo serial
onboarding-connect_tracker-connection_status-obtaining_mac_address = Obteniendo la dirección MAC del tracker
onboarding-connect_tracker-connection_status-provisioning = Enviando credenciales Wi-Fi
onboarding-connect_tracker-connection_status-connecting = Intentando conectarse a una red Wi-Fi
onboarding-connect_tracker-connection_status-looking_for_server = Buscando servidor
onboarding-connect_tracker-connection_status-connection_error = No se puede conectar al Wi-Fi
onboarding-connect_tracker-connection_status-could_not_find_server = No se pudo encontrar el servidor
onboarding-connect_tracker-connection_status-done = Conectado al Server
onboarding-connect_tracker-connection_status-no_serial_log = No se pudieron obtener los registros del tracker
onboarding-connect_tracker-connection_status-no_serial_device_found = No se pudo encontrar un tracker conectado por USB
onboarding-connect_serial-error-modal-no_serial_log = ¿Está encendido el tracker?
onboarding-connect_serial-error-modal-no_serial_log-desc = Asegúrate de que el tracker esté encendido y conectado a tu ordenador.
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers =
    { $amount ->
        [0] No hay tracker conectados
        [one] 1 tracker conectado
       *[other] { $amount } trackers conectados
    }
onboarding-connect_tracker-next = He conectado todos mis trackers

## Tracker assignment setup

onboarding-assign_trackers-title = Asignar trackers
onboarding-assign_trackers-description = Elije qué tracker va a dónde. Haz clic en la ubicación donde deseas colocar un tracker
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned =
    { $trackers ->
        [one] { $assigned } de 1 tracker asignado
       *[other] { $assigned } de { $trackers } trackers asignados
    }

## Tracker assignment warnings

# Note for devs, number is used for representing boolean states per bit.
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_FOOT =
    { $unassigned ->
        [0] El pie izquierdo está asignado, pero es necesario que también se asignen el tobillo izquierdo, el muslo izquierdo y el pecho, la cadera o la cintura!
        [1] El pie izquierdo está asignado, pero también es necesario que también se asignen el muslo izquierdo y el pecho, la cadera o la cintura!
        [2] El pie izquierdo está asignado, pero también es necesario que también se asignen el tobillo izquierdo y el pecho, la cadera o la cintura!
        [3] El pie izquierdo está asignado, pero también es necesario que también se asignen el pecho, la cadera o la cintura!
        [4] El pie izquierdo está asignado, pero también es necesario que también se asignen el tobillo izquierdo y el muslo izquierdo!
        [5] El pie izquierdo está asignado, pero también es necesario que también se asigne el muslo izquierdo!
        [6] El pie izquierdo está asignado, pero también es necesario que también se asigne el tobillo izquierdo!
       *[other] El pie izquierdo está asignado, pero también es necesario que también se asigne el Parte del cuerpo asignada desconocida.
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_FOOT =
    { $unassigned ->
        [0] El pie derecho está asignado, pero es necesario que también se asignen el tobillo derecho, el muslo derecho y el pecho, la cadera o la cintura!
        [1] El pie derecho está asignado, pero es necesario que también se asignen el muslo derecho y el pecho, la cadera o la cintura!
        [2] El pie derecho está asignado, pero es necesario que también se asignen el tobillo derecho y el pecho, la cadera o la cintura!
        [3] El pie derecho está asignado, pero es necesario que también se asignen el pecho, la cadera o la cintura!
        [4] El pie derecho está asignado, pero es necesario que también se asignen tobillo derecho y el muslo derecho!
        [5] El pie derecho está asignado, pero es necesario que también se asigne el muslo derecho!
        [6] El pie derecho está asignado, pero es necesario que también se asigne el tobillo derecho!
       *[other] Pie derecho asignado, pero necesitas asignar Desconocido
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_LOWER_LEG =
    { $unassigned ->
        [2] El tobillo izquierdo está asignado, ¡pero necesitas que el muslo izquierdo también lo esté!
        [1] El tobillo izquierdo está asignado, ¡pero necesitas que el pecho, la cadera o la cintura también lo estén!
        [0] El tobillo izquierdo está asignado, ¡pero necesitas que el muslo izquierdo y el pecho, la cadera o la cintura también lo estén!
       *[unknown] El tobillo izquierdo está asignado, ¡pero necesitas que la parte del cuerpo desconocida no asignada también lo esté!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_LOWER_LEG =
    { $unassigned ->
        [2] El tobillo derecho está asignado, ¡pero necesitas que el muslo derecho también lo esté!
        [1] El tobillo derecho está asignado, ¡pero necesitas que el pecho, la cadera o la cintura también lo estén!
        [0] El tobillo derecho está asignado, ¡pero necesitas que el muslo derecho y el pecho, la cadera o la cintura también lo estén!
       *[unknown] El tobillo derecho está asignado, ¡pero necesitas que la parte del cuerpo desconocida no asignada también lo esté!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_UPPER_LEG =
    { $unassigned ->
        [0] El muslo izquierdo está asignado, ¡pero necesitas que el pecho, la cadera o la cintura también lo estén!
       *[unknown] El muslo izquierdo está asignado, ¡pero necesitas que la parte del cuerpo desconocida no asignada también lo esté!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_UPPER_LEG =
    { $unassigned ->
        [0] El muslo derecho está asignado, ¡pero necesitas que el pecho, la cadera o la cintura también lo estén!
       *[unknown] El muslo derecho está asignado, ¡pero necesitas que la parte del cuerpo desconocida no asignada también lo esté!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-HIP =
    { $unassigned ->
        [0] La cadera está asignada, ¡pero necesitas que el pecho también lo esté!
       *[unknown] La cadera está asignada, ¡pero necesitas que la parte del cuerpo desconocida no asignada también lo esté!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-WAIST =
    { $unassigned ->
        [0] La cintura está asignada, ¡pero necesitas que el pecho también lo esté!
       *[unknown] La cintura está asignada, ¡pero necesitas que la parte del cuerpo desconocida no asignada también lo esté!
    }

## Tracker mounting method choose

onboarding-choose_mounting = ¿Qué método de calibración de montura usara?
# Multiline text
onboarding-choose_mounting-description = La posición de montura corrige la colocación de los trackers en el cuerpo.
onboarding-choose_mounting-auto_mounting = Calibración de montura automatica
# Italicized text
onboarding-choose_mounting-auto_mounting-label-v2 = Recomendada
onboarding-choose_mounting-auto_mounting-description = Esto detectará automáticamente la posición de montura para todos sus trackers a partir de 2 poses
onboarding-choose_mounting-manual_mounting = Calibración de montura manual
onboarding-choose_mounting-manual_mounting-description = Esto te permitirá elegir la posición de montura para cada tracker de manera manual

## Tracker manual mounting setup

onboarding-manual_mounting = Calibración de montura manual
onboarding-manual_mounting-description = Haz clic en cada tracker y selecciona la forma en la que están montados
onboarding-manual_mounting-auto_mounting = Calibración de montura automatica
onboarding-manual_mounting-next = Siguiente paso

## Tracker automatic mounting setup

onboarding-automatic_mounting-title = Calibración de montura
onboarding-automatic_mounting-description = Para que los trackers de SlimeVR funcionen, necesitamos asignar una posición de montura a tus trackers para alinearlos con la posicion física del tracker.
onboarding-automatic_mounting-manual_mounting = Calibración de montura manual
onboarding-automatic_mounting-next = Siguiente paso
onboarding-automatic_mounting-prev_step = Paso anterior
onboarding-automatic_mounting-done-title = Posiciones de monturas calibradas.
onboarding-automatic_mounting-done-description = ¡Su calibración de montura está completa!
onboarding-automatic_mounting-done-restart = Volver a intentarlo
onboarding-automatic_mounting-mounting_reset-title = Reinicio de montura
onboarding-automatic_mounting-mounting_reset-step-0 = 1. Ponte en cuclillas en postura de "esquí" con las piernas dobladas, la parte superior del cuerpo inclinada hacia adelante y los brazos doblados.
onboarding-automatic_mounting-mounting_reset-step-1 = 2. Presiona el botón "Restablecer montaje" y espera 3 segundos antes de que se restablezcan las orientaciones de montaje de los trackers.
onboarding-automatic_mounting-preparation-title = Preparación
onboarding-automatic_mounting-preparation-v2-step-0 = 1. Presione el botón de "Reinicio completo".
onboarding-automatic_mounting-preparation-v2-step-1 = 2. Ponte de pie con los brazos a los lados. Asegúrate de mirar hacia adelante.
onboarding-automatic_mounting-preparation-v2-step-2 = 3. Mantenga la posición hasta que finalice el temporizador de 3 segundos.
onboarding-automatic_mounting-put_trackers_on-title = Ponte los trackers
onboarding-automatic_mounting-put_trackers_on-description = Para calibrar la posiciones de montura, vamos a utilizar los trackers que acabas de asignar. Colocate todos tus trackers, puedes ver cuales son cuales en la figura de la derecha.
onboarding-automatic_mounting-put_trackers_on-next = Tengo todos mis trackers en posicion
onboarding-automatic_mounting-return-home = Hecho

## Tracker manual proportions setupa

onboarding-manual_proportions-fine_tuning_button = Ajuste automático de las proporciones
onboarding-manual_proportions-fine_tuning_button-disabled-tooltip = Conecte el visor RV para usar el ajuste automatico
onboarding-manual_proportions-export = Exportar proporciones
onboarding-manual_proportions-import = Importar proporciones
onboarding-manual_proportions-normal_increment = Incremento normal
onboarding-manual_proportions-precise_increment = Incremento preciso
onboarding-manual_proportions-grouped_proportions = Proporciones agrupadas
onboarding-manual_proportions-all_proportions = Todas las proporciones
onboarding-manual_proportions-estimated_height = Altura estimada del usuario

## Tracker automatic proportions setup

onboarding-automatic_proportions-back = Volver al tutorial de reinicio
onboarding-automatic_proportions-title = Mide tu cuerpo
onboarding-automatic_proportions-description = Para que los trackers de SlimeVR funcionen, necesitamos saber tus proporciones corporales. Esta breve calibración lo medirá por ti.
onboarding-automatic_proportions-prev_step = Paso anterior
onboarding-automatic_proportions-put_trackers_on-title = Ponte los trackers
onboarding-automatic_proportions-put_trackers_on-description = Para calibrar tus proporciones, vamos a utilizar los trackers que acabas de asignar. Ponte todos tus trackers, puedes ver cuáles son cuáles en la figura de la derecha.
onboarding-automatic_proportions-put_trackers_on-next = Tengo todos mis trackers puestos
onboarding-automatic_proportions-requirements-title = Requisitos
# Each line of text is a different list item
onboarding-automatic_proportions-requirements-descriptionv2 =
    Tienes al menos suficientes trackers para trackear tus pies (generalmente 5 trackers).
    Los trackers y tu visor están encendidos y los llevas puestos.
    Tus trackers y visor está conectados al servidor de SlimeVR y funcionan correctamente (ej. sin cortes, sin desconexiones, etc).
    Tu visor están reportando datos de posición al servidor de SlimeVR (esto generalmente significa tener SteamVR ejecutándose y conectado a SlimeVR usando el driver para SteamVR de SlimeVR).
    Su tracking está funcionando y está representando con precisión sus movimientos (ej. usted ha realizado un reinicio completo y se mueven en la dirección correcta al patear, agacharse, sentarse, etc).
onboarding-automatic_proportions-requirements-next = He leído los requisitos
onboarding-automatic_proportions-start_recording-title = Prepárate para moverte
onboarding-automatic_proportions-start_recording-description = Ahora vamos a grabar algunas poses y movimientos específicos. Se le indicarán en la siguiente pantalla. ¡Prepárate para empezar cuando pulse el botón!
onboarding-automatic_proportions-start_recording-next = Iniciar grabación
onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = Grabación en curso...
onboarding-automatic_proportions-recording-description-p1 = Realiza los movimientos que se muestran a continuación:
# Each line of text is a different list item
onboarding-automatic_proportions-recording-steps =
    Parado derecho, gira tu cabeza en un circulo.
    Dobla la espalda hacia delante y ponte en cuclillas. En cuclillas, mira a la izquierda y luego a la derecha.
    Gira la parte superior de tu cuerpo hacia la izquierda (en el sentido contrario a las agujas del reloj) y luego estira los brazos hacia el suelo.
    Gira la parte superior de tu cuerpo hacia la derecha (en el sentido de las agujas del reloj) y luego estira los brazos hacia el suelo.
    Gira las caderas en un movimiento circular como si estuvieras usando un aro redondo.
    Si queda tiempo de grabación, puedes repetir estos pasos hasta que termine.
onboarding-automatic_proportions-recording-processing = Procesando los resultados
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 20)
onboarding-automatic_proportions-recording-timer =
    { $time ->
        [one] Queda 1 segundo
       *[other] Quedan { $time } segundos
    }
onboarding-automatic_proportions-verify_results-title = Verificar resultados
onboarding-automatic_proportions-verify_results-description = Revisa los resultados a continuación, ¿Son correctos?
onboarding-automatic_proportions-verify_results-results = Resultados de la grabación
onboarding-automatic_proportions-verify_results-processing = Procesando los resultados
onboarding-automatic_proportions-verify_results-redo = Rehacer la grabación
onboarding-automatic_proportions-verify_results-confirm = Estan correctas
onboarding-automatic_proportions-done-title = Proporciones medidas y guardadas.
onboarding-automatic_proportions-done-description = ¡Calibración de las proporciones físicas completada!
onboarding-automatic_proportions-error_modal-v2 =
    <b>Advertencia:</b> ¡Hubo un error al estimar las proporciones!
    Es probable que se trate de un problema de calibración de montura. Asegúrate de que el tracking funciona correctamente antes de volver a intentarlo.
     <docs>Consulta la documentación</docs> o únete a nuestro <discord>Discord</discord> para obtener ayuda ^_^
onboarding-automatic_proportions-error_modal-confirm = ¡Entendido!
onboarding-automatic_proportions-smol_warning =
    La altura configurada de { $height } es menor que la altura mínima aceptada de { $minHeight }.
    <b>Vuelva a hacer las mediciones y asegúrese de que sean correctas.</b>
onboarding-automatic_proportions-smol_warning-cancel = Volver

## User height calibration


## Stay Aligned setup

onboarding-stay_aligned-title = Mantener Alineado
onboarding-stay_aligned-description = Configure Mantener Alineado para mantener sus trackers alineados.
onboarding-stay_aligned-put_trackers_on-title = Ponte los trackers
onboarding-stay_aligned-put_trackers_on-description = Para guardar tus posturas de descanso, vamos a utilizar los trackers que acabas de asignar. Ponte todos tus trackers, puedes ver cuál es cuál en la figura de la derecha.
onboarding-stay_aligned-put_trackers_on-trackers_warning = ¡Tiene menos de 5 trackers actualmente conectados y asignados! Esta es la cantidad mínima de trackers necesarios para que Mantener Alineado funcione correctamente.
onboarding-stay_aligned-put_trackers_on-next = Tengo todos mis trackers puestos
onboarding-stay_aligned-verify_mounting-title = Verifique su montaje
onboarding-stay_aligned-preparation-title = Preparación
onboarding-stay_aligned-preparation-tip = Asegúrate de estar de pie. Sigue mirando hacia adelante con los brazos hacia abajo a los lados.
onboarding-stay_aligned-relaxed_poses-standing-title = Postura de pie relajada
onboarding-stay_aligned-relaxed_poses-standing-step-0 = 1. Párese en una posición cómoda. ¡Relájate!
onboarding-stay_aligned-relaxed_poses-standing-step-1-v2 = 2. Presione el botón "Guardar pose".
onboarding-stay_aligned-relaxed_poses-sitting-title = Postura relajada sentado en silla
onboarding-stay_aligned-relaxed_poses-sitting-step-0 = 1. Siéntese en una posición cómoda. ¡Relájate!
onboarding-stay_aligned-relaxed_poses-sitting-step-1-v2 = 2. Presione el botón "Guardar pose".
onboarding-stay_aligned-relaxed_poses-flat-title = Postura relajada sentado en el suelo
onboarding-stay_aligned-relaxed_poses-flat-step-0 = 1. Siéntate en el suelo con las piernas al frente. ¡Relájate!
onboarding-stay_aligned-relaxed_poses-flat-step-1-v2 = 2. Presione el botón "Guardar pose".
onboarding-stay_aligned-relaxed_poses-skip_step = Omitir
onboarding-stay_aligned-done-title = ¡Mantener Alineado habilitado!
onboarding-stay_aligned-done-description = ¡Su configuración de Mantener Alineado está completa!
onboarding-stay_aligned-done-description-2 = ¡La configuración está completa! Puede reiniciar el proceso si desea recalibrar las poses.
onboarding-stay_aligned-previous_step = Atrás
onboarding-stay_aligned-next_step = Siguiente
onboarding-stay_aligned-restart = Reiniciar
onboarding-stay_aligned-done = Hecho

## Trackers Still On notification

trackers_still_on-modal-title = Los trackers siguen encendidos
trackers_still_on-modal-description =
    Uno o más trackers siguen encendidos.
    Aún quieres salir de SlimeVR?
trackers_still_on-modal-confirm = Salir de SlimeVR
trackers_still_on-modal-cancel = Un momento...

## Firmware tool globals

firmware_tool-next_step = Siguiente paso
firmware_tool-previous_step = Paso anterior
firmware_tool-ok = Se ve bien
firmware_tool-retry = Reintentar
firmware_tool-loading = Cargando...

## Firmware tool Steps

firmware_tool = Herramienta de firmware DIY
firmware_tool-description = Le permite configurar y actualizar sus trackers DIY
firmware_tool-not_available = Vaya, la herramienta de firmware no está disponible en este momento. ¡Vuelve más tarde!
firmware_tool-not_compatible = La herramienta de firmware no es compatible con esta versión del servidor. ¡Por favor, actualice su servidor!
firmware_tool-flash_method_step = Método de flasheado
firmware_tool-flash_method_step-description = Seleccione el método de flasheado que desea utilizar
firmware_tool-flashbtn_step = Pulse el botón de boot
firmware_tool-flashbtn_step-description = Antes de pasar al siguiente paso, hay algunas cosas que debe hacer
firmware_tool-flashbtn_step-board_SLIMEVR = Apague el tracker, retire la carcasa (si la hay), conecte un cable USB a este ordenador y, a continuación, realice uno de los siguientes pasos de acuerdo con la revisión de la placa SlimeVR:
firmware_tool-flashbtn_step-board_OTHER =
    Antes de flashear, probablemente tendrá que poner el tracker en modo bootloader.
    La mayoría de las veces significa presionar el botón de boot en la placa antes de que comience el proceso de flasheo.
    Si el tiempo de espera del proceso de flasheo se agota al comienzo, probablemente signifique que el tracker no estaba en modo bootloader
    Consulte las instrucciones de flasheo de su placa para saber cómo activar el modo bootloader
firmware_tool-flash_method_ota-devices = Dispositivos OTA detectados:
firmware_tool-flash_method_ota-no_devices = No hay placas que se puedan actualizar mediante OTA, asegúrese de seleccionar el tipo de placa correcto
firmware_tool-flash_method_serial-wifi = Credenciales del Wi-Fi:
firmware_tool-flash_method_serial-devices-label = Dispositivos seriales detectados:
firmware_tool-flash_method_serial-devices-placeholder = Seleccione un dispositivo serial
firmware_tool-flash_method_serial-no_devices = No se han detectado dispositivos serial compatibles, asegúrese de que el tracker esté enchufado
firmware_tool-build_step = Compilando
firmware_tool-build_step-description = El firmware se está compilando, espere por favor
firmware_tool-flashing_step = Flasheando
firmware_tool-flashing_step-description = Sus trackers estan siendo flasheados, por favor siga las instrucciones en pantalla
firmware_tool-flashing_step-warning-v2 = No desconectes ni apagues el tracker durante el proceso de carga a menos que se le indique, ya que puede hacer que tu placa quede inutilizable
firmware_tool-flashing_step-flash_more = Flashear más trackers
firmware_tool-flashing_step-exit = Salir

## firmware tool build status

firmware_tool-build-CREATING_BUILD_FOLDER = Creando la carpeta de compilación
firmware_tool-build-BUILDING = Compilando el firmware
firmware_tool-build-SAVING = Guardando la compilación
firmware_tool-build-DONE = Compilación completa
firmware_tool-build-ERROR = No se ha podido compilar el firmware

## Firmware update status

firmware_update-status-DOWNLOADING = Descargando el firmware
firmware_update-status-NEED_MANUAL_REBOOT-v2 = Por favor, apague y vuelva a encender su rastreador
firmware_update-status-AUTHENTICATING = Autenticando con el microcontrolador
firmware_update-status-UPLOADING = Cargando el firmware
firmware_update-status-SYNCING_WITH_MCU = Sincronizando con el microcontrolador
firmware_update-status-REBOOTING = Aplicando la actualización
firmware_update-status-PROVISIONING = Aplicando credenciales Wi-Fi
firmware_update-status-DONE = ¡Actualización completa!
firmware_update-status-ERROR_DEVICE_NOT_FOUND = No se ha podido encontrar el dispositivo
firmware_update-status-ERROR_TIMEOUT = Se agotó el tiempo de espera del proceso de actualización
firmware_update-status-ERROR_DOWNLOAD_FAILED = No se pudo descargar el firmware
firmware_update-status-ERROR_AUTHENTICATION_FAILED = No se pudo autenticar con el microcontrolador
firmware_update-status-ERROR_UPLOAD_FAILED = No se pudo cargar el firmware
firmware_update-status-ERROR_PROVISIONING_FAILED = No se pudieron configurar las credenciales de Wi-Fi
firmware_update-status-ERROR_UNSUPPORTED_METHOD = El metodo de actualización no es compatible
firmware_update-status-ERROR_UNKNOWN = Error desconocido

## Dedicated Firmware Update Page

firmware_update-title = Actualización de firmware
firmware_update-devices = Dispositivos disponibles
firmware_update-devices-description = Seleccione los trackers que desea actualizar a la última versión del firmware de SlimeVR
firmware_update-no_devices = ¡Asegúrese de que los trackers que desea actualizar estén encendidos y conectados al Wi-Fi!
firmware_update-changelog-title = Actualizando a { $version }
firmware_update-looking_for_devices = Buscando dispositivos para actualizar...
firmware_update-retry = Reintentar
firmware_update-update = Actualizar rastreadores seleccionados
firmware_update-exit = Salir

## Tray Menu

tray_menu-show = Mostrar
tray_menu-hide = Ocultar
tray_menu-quit = Salir

## First exit modal

tray_or_exit_modal-title = ¿Qué debe hacer el botón de cierre?
# Multiline text
tray_or_exit_modal-description =
    Esto te permite elegir si quieres salir de la aplicación o minimizarlo a la bandeja al pulsar el botón de cerrar.
    
    ¡Puedes cambiar esto más tarde en la configuración de la interfaz!
tray_or_exit_modal-radio-exit = Salir al cerrar
tray_or_exit_modal-radio-tray = Minimizar a la bandeja
tray_or_exit_modal-submit = Guardar
tray_or_exit_modal-cancel = Cancelar

## Unknown device modal

unknown_device-modal-title = ¡Se ha encontrado un nuevo tracker!
unknown_device-modal-description =
    Hay un nuevo tracker con dirección MAC <b>{ $deviceId }</b>.
    ¿Quieres conectarlo a SlimeVR?
unknown_device-modal-confirm = ¡Claro!
unknown_device-modal-forget = Ignóralo
# VRChat config warnings
vrc_config-page-title = Advertencias sobre la configuración de VRChat
vrc_config-page-desc = Esta página muestra el estado de la configuración de VRChat y dice qué configuración es incompatible con SlimeVR. Se recomienda encarecidamente que corrijas las advertencias que aparecen aquí para obtener la mejor experiencia con SlimeVR.
vrc_config-page-help = ¿No encuentras los ajustes?
vrc_config-page-help-desc = ¡Consulte nuestra <a>documentación sobre este tema!</a>
vrc_config-page-big_menu = Seguimiento e IK (Menú grande)
vrc_config-page-big_menu-desc = Configuración relacionada al IK en el menú grande de configuración
vrc_config-page-wrist_menu = Seguimiento e IK (Menú de muñeca)
vrc_config-page-wrist_menu-desc = Ajustes relacionados al IK en el pequeño menú de ajustes (menú de muñeca)
vrc_config-on = Encendido
vrc_config-off = Apagado
vrc_config-setting_name = Nombre del ajuste de VRChat
vrc_config-recommended_value = Valor recomendado
vrc_config-current_value = Valor actual
vrc_config-mute = Silenciar advertencia
vrc_config-mute-btn = Silenciar
vrc_config-unmute-btn = Desilenciar
vrc_config-legacy_mode = Utilizar la resolución de IK antigua
vrc_config-disable_shoulder_tracking = Desactivar el seguimiento de hombros
vrc_config-shoulder_width_compensation = Compensación de la anchura del hombro
vrc_config-spine_mode = Modo columna FBT
vrc_config-tracker_model = Modelo de rastreador para "FBT"
vrc_config-avatar_measurement_type = Medida del avatar
vrc_config-calibration_range = Rango de calibración
vrc_config-calibration_visuals = Mostrar elementos visuales de la calibración
vrc_config-user_height = Altura real del usuario
vrc_config-spine_mode-UNKNOWN = Desconocido
vrc_config-spine_mode-LOCK_BOTH = Bloquear ambos
vrc_config-spine_mode-LOCK_HEAD = Bloquear cabeza
vrc_config-spine_mode-LOCK_HIP = Bloquear cadera
vrc_config-tracker_model-UNKNOWN = Desconocido
vrc_config-tracker_model-AXIS = Eje
vrc_config-tracker_model-BOX = Caja
vrc_config-tracker_model-SPHERE = Esfera
vrc_config-tracker_model-SYSTEM = Sistema
vrc_config-avatar_measurement_type-UNKNOWN = Desconocido
vrc_config-avatar_measurement_type-HEIGHT = Altura
vrc_config-avatar_measurement_type-ARM_SPAN = Amplitud de los brazos

## Error collection consent modal

error_collection_modal-title = ¿Podemos recopilar errores?
error_collection_modal-description_v2 =
    { settings-interface-behavior-error_tracking-description_v2 }
    
    Puede cambiar esta configuración más adelante en la sección Comportamiento de la página de configuración.
error_collection_modal-confirm = Acepto
error_collection_modal-cancel = No quiero

## Tracking checklist section

