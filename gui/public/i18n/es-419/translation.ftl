### SlimeVR complete GUI translations
# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropiate 
# features like variables and selectors in each appropiate case!
# And also comment the string if it's something not easy to translate so you help
# translators on what it means

## Websocket (server) status
websocket-connecting = Conectando al servidor
websocket-connection_lost = Conexión al servidor perdida. Intentando reconectar...

## Tips
tips-find_tracker = ¿No estás seguro de cuál sensor es cuál? Agita un sensor y se resaltará donde está asignado.
tips-do_not_move_heels = ¡Asegúrate de no mover los talones en la grabación!

## Body parts
body_part-NONE = Sin asignar
body_part-HEAD = Cabeza
body_part-NECK = Cuello
body_part-RIGHT_SHOULDER = Hombro derecho
body_part-RIGHT_UPPER_ARM = Brazo superior derecho
body_part-RIGHT_LOWER_ARM = Antebrazo derecho
body_part-RIGHT_HAND = Mano derecha
body_part-RIGHT_UPPER_LEG = Muslo derecho
body_part-RIGHT_LOWER_LEG = Tobillo derecho
body_part-RIGHT_FOOT = Pie derecho
body_part-RIGHT_CONTROLLER = Control derecho
body_part-CHEST = Pecho
body_part-WAIST = Cintura
body_part-HIP = Cadera
body_part-LEFT_SHOULDER = Hombro izquierdo
body_part-LEFT_UPPER_ARM = Brazo superior izquierdo
body_part-LEFT_LOWER_ARM = Antebrazo izquierdo
body_part-LEFT_HAND = Mano izquierda
body_part-LEFT_UPPER_LEG = Muslo izquierdo
body_part-LEFT_LOWER_LEG = Tobillo izquierdo
body_part-LEFT_FOOT = Pie izquierdo
body_part-LEFT_CONTROLLER = Control izquierdo

## Skeleton stuff
skeleton_bone-NONE = Nada
skeleton_bone-HEAD = Inclinación de cabeza
skeleton_bone-NECK = Largo del cuello
skeleton_bone-TORSO = Largo del torso
skeleton_bone-CHEST = Distancia del pecho
skeleton_bone-WAIST = Distancia de la cintura
skeleton_bone-HIP_OFFSET = Desplazamiento de la cadera
skeleton_bone-HIPS_WIDTH = Ancho de la cadera
skeleton_bone-LEGS_LENGTH = Largo de las piernas
skeleton_bone-KNEE_HEIGHT = Altura de las rodillas
skeleton_bone-FOOT_LENGTH = Largo de pies
skeleton_bone-FOOT_SHIFT = Desplazamiento de pies
skeleton_bone-SKELETON_OFFSET = Desplazamiento del esqueleto
skeleton_bone-CONTROLLER_DISTANCE_Z = Distancia Z del mando
skeleton_bone-CONTROLLER_DISTANCE_Y = Distancia Y del mando
skeleton_bone-FOREARM_LENGTH = Distancia del antebrazo
skeleton_bone-SHOULDERS_DISTANCE = Distancia de los hombros
skeleton_bone-SHOULDERS_WIDTH = Ancho de los hombros
skeleton_bone-UPPER_ARM_LENGTH = Largo del brazo superior
skeleton_bone-ELBOW_OFFSET = Desplazamiento del codo

## Tracker reset buttons
reset-reset_all = Reiniciar todas las proporciones
reset-full = Reiniciar
reset-mounting = Reiniciar montura
reset-quick = Reinicio rápido

## Serial detection stuff
serial_detection-new_device-p0 = ¡Nuevo dispositivo serial detectado!
serial_detection-new_device-p1 = ¡Ingresa tus credenciales del WiFi!
serial_detection-new_device-p2 = Por favor selecciona que quieres hacer con el
serial_detection-open_wifi = Conectarse al WiFi
serial_detection-open_serial = Abrir consola serial
serial_detection-submit = ¡Enviar!
serial_detection-close = Cerrar

## Navigation bar
navbar-home = Inicio
navbar-body_proportions = Proporciones corporales
navbar-trackers_assign = Asignación de sensores
navbar-mounting = Calibración de montura
navbar-onboarding = Asistente de configuración
navbar-settings = Ajustes

## Bounding volume hierarchy recording
bvh-start_recording = Grabar BVH
bvh-recording = Grabando...

## Overlay settings
overlay-is_visible_label = Mostrar interfaz en SteamVR
overlay-is_mirrored_label = Mostrar interfaz reflejada

## Tracker status
tracker-status-none = Sin estado
tracker-status-busy = Ocupado
tracker-status-error = Error
tracker-status-disconnected = Desconectado
tracker-status-occluded = Ocluido
tracker-status-ok = Conectado

## Tracker status columns
tracker-table-column-name = Nombre
tracker-table-column-type = Tipo
tracker-table-column-battery = Batería
tracker-table-column-ping = Latencia
tracker-table-column-rotation = Rotación X/Y/Z
tracker-table-column-position = Posición X/Y/Z
tracker-table-column-url = URL

## Tracker rotation
tracker-rotation-front = Frente
tracker-rotation-left = Izquierda
tracker-rotation-right = Derecha
tracker-rotation-back = Atrás

## Tracker information
tracker-infos-manufacturer = Fabricante
tracker-infos-display_name = Nombre
tracker-infos-custom_name = Nombre personalizado
tracker-infos-url = URL del sensor

## Tracker settings
tracker-settings-back = Volver a la lista de sensores
tracker-settings-title = Ajustes de los sensores
tracker-settings-assignment_section = Asignación
tracker-settings-assignment_section-description = Parte del cuerpo asignado al sensor.
tracker-settings-assignment_section-edit = Editar asignación
tracker-settings-mounting_section = Posición de montura
tracker-settings-mounting_section-description = ¿Dónde está montado el sensor?
tracker-settings-mounting_section-edit = Editar montura
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Nombre del sensor
tracker-settings-name_section-description = Dale un apodo bonito :)
tracker-settings-name_section-placeholder = Pierna izquierda de NightyBeast

## Tracker part card info
tracker-part_card-no_name = Sin nombre
tracker-part_card-unassigned = Sin asignar

## Body assignment menu
body_assignment_menu = ¿Dónde quieres que esté este sensor?
body_assignment_menu-description = Selecciona la posición donde quieres asignar este sensor. También puedes escoger administrar todos los sensores al mismo tiempo en vez de uno por uno.
body_assignment_menu-show_advanced_locations = Mostrar posiciones de asignación avanzadas
body_assignment_menu-manage_trackers = Administrar todos los sensores
body_assignment_menu-unassign_tracker = Desasignar sensor

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

tracker_selection_menu-unassigned = Sensores sin asignar
tracker_selection_menu-assigned = Sensores asignados
tracker_selection_menu-dont_assign = No asignar

## Mounting menu
mounting_selection_menu = ¿Dónde quieres colocar el sensor?
mounting_selection_menu-close = Cerrar

## Sidebar settings
settings-sidebar-title = Ajustes
settings-sidebar-general = General
settings-sidebar-tracker_mechanics = Mecánicas del sensor
settings-sidebar-fk_settings = Ajustes de FK
settings-sidebar-gesture_control = Control de gestos
settings-sidebar-interface = Interfaz
settings-sidebar-osc_router = Router OSC
settings-sidebar-utils = Utilidades
settings-sidebar-serial = Consola serial

## SteamVR settings
settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = Sensores en SteamVR
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    Habilita o deshabilita sensores de SteamVR en específico.
    Útil para juegos y aplicaciones que solo soportan ciertos sensores..
settings-general-steamvr-trackers-waist = Cintura
settings-general-steamvr-trackers-chest = Pecho
settings-general-steamvr-trackers-feet = Pies
settings-general-steamvr-trackers-knees = Rodillas
settings-general-steamvr-trackers-elbows = Codos
settings-general-steamvr-trackers-hands = Manos

## Tracker mechanics
settings-general-tracker_mechanics = Mecánicas del sensor
settings-general-tracker_mechanics-filtering = Filtrado
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Selecciona un tipo de filtro para tus sensores.
    Predicción predice el movimiento mientras que el suavizado suaviza el movimiento.
settings-general-tracker_mechanics-filtering-type = Tipos de filtro
settings-general-tracker_mechanics-filtering-type-none = Sin filtro
settings-general-tracker_mechanics-filtering-type-none-description = Usa las rotaciones como son. No se realizará ningún filtrado.
settings-general-tracker_mechanics-filtering-type-smoothing = Suavizado
settings-general-tracker_mechanics-filtering-type-smoothing-description = Suaviza los movimientos pero añade algo de latencia.
settings-general-tracker_mechanics-filtering-type-prediction = Predicción
settings-general-tracker_mechanics-filtering-type-prediction-description = Reduce la latencia y los movimientos serán más inmediatos, pero puede incrementar la inestabilidad.
settings-general-tracker_mechanics-filtering-amount = Cantidad

## FK/Tracking settings
settings-general-fk_settings = Ajustes de sensores
settings-general-fk_settings-leg_tweak = Corrección de piernas
settings-general-fk_settings-leg_tweak-description = El clip del suelo puede reducir o incluso eliminar el traspaso del piso pero puede causar problemas cuando te arrodilles. Corrección del patinaje corrige el patinaje, pero puede disminuir la precisión de ciertos movimientos.
# Floor clip: 
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Clip del suelo
# Skating correction: 
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Corrección del patinaje
settings-general-fk_settings-leg_tweak-skating_correction-amount = Agresividad de la corrección del patinaje
settings-general-fk_settings-arm_fk = Trackeo de brazos
settings-general-fk_settings-arm_fk-description = Cambia cómo el movimiento de los brazos es detectado.
settings-general-fk_settings-arm_fk-force_arms = Forzar brazos desde el HMD
settings-general-fk_settings-skeleton_settings = Ajustes de esqueleto
settings-general-fk_settings-skeleton_settings-description = Habilita o deshabilita los ajustes de esqueleto. Es recomendado dejar estos ajustes habilitados.
settings-general-fk_settings-skeleton_settings-extended_spine = Extención de columna
settings-general-fk_settings-skeleton_settings-extended_pelvis = Extención de pelvis
settings-general-fk_settings-skeleton_settings-extended_knees = Extención de rodillas
settings-general-fk_settings-vive_emulation-title = Vive emulation
settings-general-fk_settings-vive_emulation-description = Emulate the waist tracker problems that Vive trackers have. This is a joke and makes tracking worse.
settings-general-fk_settings-vive_emulation-label = Enable Vive emulation

## Gesture control settings (tracker tapping)
settings-general-gesture_control = Control de gestos
settings-general-gesture_control-subtitle = Reinicio basado en toques
settings-general-gesture_control-description = Permite la ejecución de un reinicio al tocar un sensor. El sensor más alto en el torso es utilizado para el reinicio rápido, el sensor más alto en tu pierna izquierda es utilizado para el reinicio, y el sensor más alto en tu pierna derecha es utilizado para reiniciar la montura. Cabe destacar que los toques deben suceder dentro de 0.6 segundos para ser registrados.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps = { $amount ->
    [one] 1 tap
    *[other] { $amount } taps
}
settings-general-gesture_control-quickResetEnabled = Activar toques para reinicio rápido
settings-general-gesture_control-quickResetDelay = Retraso de reinicio rápido
settings-general-gesture_control-quickResetTaps = Toques para reinicio rápido
settings-general-gesture_control-resetEnabled = Activar toques para reinicio
settings-general-gesture_control-resetDelay = Retraso de reinicio
settings-general-gesture_control-resetTaps = Toques para reinicio
settings-general-gesture_control-mountingResetEnabled = Activar toques para reinicio de montura
settings-general-gesture_control-mountingResetDelay = Retraso de reinicio de montura
settings-general-gesture_control-mountingResetTaps = Toques para reinicio de montura

## Interface settings
settings-general-interface = Interfaz
settings-general-interface-dev_mode = Modo desarrollador
settings-general-interface-dev_mode-description = Este modo puede ser útil si es que necesitas información a fondo o para un nivel de interacción más avanzado con los sensores conectados.
settings-general-interface-dev_mode-label = Modo desarrollador
settings-general-interface-serial_detection = Detección de dispositivo serial
settings-general-interface-serial_detection-description = Esta opción mostrará un notificación cada vez que conectes un nuevo dispositivo serial que pueda ser un sensor. Ayuda a mejorar el proceso de configuración de un sensor.
settings-general-interface-serial_detection-label = Detección de dispositivo serial
settings-general-interface-lang = Selecciona un idioma
settings-general-interface-lang-description = Cambia el idioma que quieras usar.
settings-general-interface-lang-placeholder = Selecciona el idioma a utilizar

## Serial settings
settings-serial = Consola serial
# This cares about multilines
settings-serial-description =
    Esta es la comunicación serial actualizada en vivo.
    Puede ser util para saber si el firmware tiene problemas.
settings-serial-connection_lost = Conexión serial perdida, reconectando...
settings-serial-reboot = Reinciar
settings-serial-factory_reset = Restauración de fábrica
settings-serial-get_infos = Obtener información
settings-serial-serial_select = Selecciona un puerto serial
settings-serial-auto_dropdown_item = Auto

## OSC router settings
settings-osc-router = Router OSC
# This cares about multilines
settings-osc-router-description =
    Redirecciona mensajes OSC recibidos de otro programa.
    Útil para usar otro programa OSC con VRChat por ejemplo.
settings-osc-router-enable = Habilitar
settings-osc-router-enable-description = Habilita el reenvío de mensajes.
settings-osc-router-enable-label = Habilitar
settings-osc-router-network = Puertos de conexión
# This cares about multilines
settings-osc-router-network-description =
    Establece los puertos de entrada y salida de datos
    Estos pueden ser lo mismos puertos usados en el servidor de SlimeVR.
settings-osc-router-network-port_in =
    .label = Puerto de entrada
    .placeholder = Puerto de entrada (por defecto: 9002)
settings-osc-router-network-port_out =
    .label = Puerto de salida
    .placeholder = Puerto de salida (por defecto: 9000)
settings-osc-router-network-address = Dirección de red
settings-osc-router-network-address-description = Establece la direción a la cuál se enviarán los datos.
settings-osc-router-network-address-placeholder = Dirección IPv4

## OSC VRChat settings
settings-osc-vrchat = VRChat OSC Trackers
# This cares about multilines
settings-osc-vrchat-description =
    Cambiar ajustes específicos de VRChat para recibir datos del HMD y enviar
    datos de los sensores para seguimiento de cuerpo completo (funciona en Quest nativo).
settings-osc-vrchat-enable = Habilitar
settings-osc-vrchat-enable-description = Habilita el envio y recibo de datos.
settings-osc-vrchat-enable-label = Habilitar
settings-osc-vrchat-network = Puertos de conexión
settings-osc-vrchat-network-description = Establece los puertos de entrada y salida de datos a VRChat.
settings-osc-vrchat-network-port_in =
    .label = Puerto de entrada
    .placeholder = Puerto de entrada (por defecto: 9001)
settings-osc-vrchat-network-port_out =
    .label = Puerto de salida
    .placeholder = Puerto de salida (por defecto: 9000)
settings-osc-vrchat-network-address = Dirección de red
settings-osc-vrchat-network-address-description = Establece la dirección donde se enviarán los datos de VRChat (revisa los ajustes de WiFi de tu dispositivo que tenga el juego).
settings-osc-vrchat-network-address-placeholder = Dirección IP de VRChat
settings-osc-vrchat-network-trackers = Sensores
settings-osc-vrchat-network-trackers-description = Habilita el envío de sensores específicos mediante OSC.
settings-osc-vrchat-network-trackers-chest = Pecho
settings-osc-vrchat-network-trackers-waist = Cintura
settings-osc-vrchat-network-trackers-knees = Rodillas
settings-osc-vrchat-network-trackers-feet = Pies
settings-osc-vrchat-network-trackers-elbows = Codos

## Setup/onboarding menu
onboarding-skip = Saltar configuración
onboarding-continue = Continuar
onboarding-wip = Trabajo en progreso

## WiFi setup
onboarding-wifi_creds-back = Volver a la introducción
onboarding-wifi_creds = Ingresar credenciales del WiFi
# This cares about multilines
onboarding-wifi_creds-description =
    Los sensores utilizarán estas credenciales para conectarse inalámbricamente. 
    Por favor usa las credenciales del WiFi al cuál estás conectado actualmente.
onboarding-wifi_creds-skip = Saltar ajustes de WiFi
onboarding-wifi_creds-submit = ¡Enviar!
onboarding-wifi_creds-ssid =
    .label = SSID
    .placeholder = Enter SSID
onboarding-wifi_creds-password =
    .label = Password
    .placeholder = Enter password

## Mounting setup
onboarding-reset_tutorial-back = Volver a la calibración de montura
onboarding-reset_tutorial = Reiniciar tutorial
onboarding-reset_tutorial-description = Esta aún parte no está finalizada, por favor presione continuar

## Setup start
onboarding-home = Bienvenido a SlimeVR
# This cares about multilines and it's centered!!
onboarding-home-description =
    ¡Acercando el seguimiento
    de cuerpo completo a todos!
onboarding-home-start = ¡Comencemos!

## Enter VR part of setup
onboarding-enter_vr-back = Volver a la asignación de sensores
onboarding-enter_vr-title = ¡Es hora de entrar a la RV!
onboarding-enter_vr-description = ¡Ponte todos tus sensores y luego entra a la RV!
onboarding-enter_vr-ready = Estoy listo

## Setup done
onboarding-done-title = ¡Estás listo!
onboarding-done-description = Disfruta moverte en la realidad virtual
onboarding-done-close = Cerrar la guía

## Tracker connection setup
onboarding-connect_tracker-back = Volver a las credenciales WiFi
onboarding-connect_tracker-title = Conecta tus sensores
onboarding-connect_tracker-description-p0 = Ahora la parte divertida, ¡Conectar todos tus sensores!
onboarding-connect_tracker-description-p1 = Simplemente conecta todos los sensores que aún no están conectados, por medio de un puerto USB.
onboarding-connect_tracker-issue-serial = ¡Tengo problemas conectándolos!
onboarding-connect_tracker-usb = Sensor USB
onboarding-connect_tracker-connection_status-connecting = Enviando credenciales WiFi
onboarding-connect_tracker-connection_status-connected = Conectado al WiFi
onboarding-connect_tracker-connection_status-error = Incapaz de conectar al WiFi
onboarding-connect_tracker-connection_status-start_connecting = Buscando sensores
onboarding-connect_tracker-connection_status-handshake = Conectado con el servidor
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
onboarding-connect_tracker-next = He conectado todos mis sensores

## Tracker assignment setup
onboarding-assign_trackers-back = Volver a las credenciales WiFi
onboarding-assign_trackers-title = Asignación de sensores
onboarding-assign_trackers-description = Debes escoger dónde van los sensores. Has clic en la ubicación donde quieras colocar un sensor
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned = { $assigned } of { $trackers ->
    [one] 1 tracker
    *[other] { $trackers } trackers
} assigned
onboarding-assign_trackers-advanced = Mostrar ubicación de asignaciones avanzados.
onboarding-assign_trackers-next = He asignado todos los sensores

## Tracker manual mounting setup
onboarding-manual_mounting-back = Volver para entrar a la RV
onboarding-manual_mounting = Montura manual
onboarding-manual_mounting-description = Has clic en todos los sensores y selecciona en que dirección están montados
onboarding-manual_mounting-auto_mounting = Montura automática
onboarding-manual_mounting-next = Siguiente paso

## Tracker automatic mounting setup
onboarding-automatic_mounting-back = Volver para entrar a la RV
onboarding-automatic_mounting-title = Calibración de montura
onboarding-automatic_mounting-description = Para que los sensores SlimeVR funcionen, necesitamos asignar una posición de montura a tus sensores para que se alinien con la montura física de tu sensor.
onboarding-automatic_mounting-manual_mounting = Establecer montura manualmente
onboarding-automatic_mounting-next = Siguiente paso
onboarding-automatic_mounting-prev_step = Paso anterior
onboarding-automatic_mounting-done-title = Ubicación de monturas calibradas.
onboarding-automatic_mounting-done-description = ¡Tu calibración de monturas está completa!
onboarding-automatic_mounting-done-restart = Volver al inicio
onboarding-automatic_mounting-mounting_reset-title = Reinicio de montura
onboarding-automatic_mounting-mounting_reset-step-0 = 1. Arrodíllate en una posición de «esquiar» con tus piernas dobladas, la parte superior de tu cuerpo inclinada hacia adelante, y tus brazos doblados.
onboarding-automatic_mounting-mounting_reset-step-1 = 2. Presiona el botón «Reinicio de montura» y espera 3 segundos hasta que se reinicie la montura.
onboarding-automatic_mounting-preparation-title = Preparación
onboarding-automatic_mounting-preparation-step-0 = 1. Párate derecho con tus brazos a los costados.
onboarding-automatic_mounting-preparation-step-1 = 2. Presiona el botón «Reiniciar» y espera 3 segundos hasta que se reinicien los sensores.
onboarding-automatic_mounting-put_trackers_on-title = Ponte tus sensores
onboarding-automatic_mounting-put_trackers_on-description = Para calibrar la ubicación de tus monturas, usaremos los sensores que has asignado. Ponte todos tus sensores, puedes ver cuál es cual en la figura de la derecha.
onboarding-automatic_mounting-put_trackers_on-next = Tengo puestos todos mis sensores

## Tracker manual proportions setup
onboarding-manual_proportions-back = Volver al tutorial de reinicio
onboarding-manual_proportions-title = Proporciones de cuerpo manuales
onboarding-manual_proportions-precision = Ajuste con precisión
onboarding-manual_proportions-auto = Calibración automática

## Tracker automatic proportions setup
onboarding-automatic_proportions-back = Volver al tutorial de reinicio
onboarding-automatic_proportions-title = Mide tu cuerpo
onboarding-automatic_proportions-description = Para que los sensores de SlimeVR funcionen, necesitamos saber el largo de tus huesos. Esta breve calibración los medirá por ti.
onboarding-automatic_proportions-manual = Calibración manual
onboarding-automatic_proportions-prev_step = Paso anterior
onboarding-automatic_proportions-put_trackers_on-title = Ponte tus sensores
onboarding-automatic_proportions-put_trackers_on-description = Para calibrar tus proporciones, usaremos los sensores que acabas de asignar. Ponte todos tus sensores, puedes ver cuál es cual en la figura de la derecha.
onboarding-automatic_proportions-put_trackers_on-next = Tengo puestos todos mis sensores
onboarding-automatic_proportions-preparation-title = Preparación
onboarding-automatic_proportions-preparation-description = Coloca una silla directamente detrás de ti en tu area de juego. Prepárate para sentarte durante la configuración del autobone.
onboarding-automatic_proportions-preparation-next = Estoy al frente de una silla
onboarding-automatic_proportions-start_recording-title = Prepárate para moverte
onboarding-automatic_proportions-start_recording-description = Ahora vamos a grabar poses y movimientos en específico. Estas serán mostradas en la siguiente ventana. ¡Prepárate para empezar cuando presiones el botón!
onboarding-automatic_proportions-start_recording-next = Empezar grabación
onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = Grabación en proceso...
onboarding-automatic_proportions-recording-description-p1 = Realiza los siguientes movimientos:
onboarding-automatic_proportions-recording-steps-0 = Dobla tus rodillas unas cuantas veces.
onboarding-automatic_proportions-recording-steps-1 = Sientate en una silla y párate.
onboarding-automatic_proportions-recording-steps-2 = Gira tu torso hacia la izquierda, luego inclínate hacia la derecha.
onboarding-automatic_proportions-recording-steps-3 = Gira tu torso hacia la derecha, luego inclínate hacia la izquierda.
onboarding-automatic_proportions-recording-steps-4 = Menea tu cuerpo hasta que el tiempo se acabe.
onboarding-automatic_proportions-recording-processing = Procesando el resultado
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 15)
onboarding-automatic_proportions-recording-timer = { $time ->
    [one] 1 second left
    *[other] { $time } seconds left
}
onboarding-automatic_proportions-verify_results-title = Verificando resultados
onboarding-automatic_proportions-verify_results-description = Comprueba abajo los resultados, ¿Parecen correctos?
onboarding-automatic_proportions-verify_results-results = Grabando resultados
onboarding-automatic_proportions-verify_results-processing = Procesando resultados
onboarding-automatic_proportions-verify_results-redo = Rehacer grabación
onboarding-automatic_proportions-verify_results-confirm = Son correctos
onboarding-automatic_proportions-done-title = Cuerpo medido y guardado.
onboarding-automatic_proportions-done-description = ¡La calibración de tus proporciones corporales fue completada!

## Home
home-no_trackers = No hay sensores detectados o asignados
