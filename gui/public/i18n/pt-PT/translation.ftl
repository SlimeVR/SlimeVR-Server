# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = A carregar...
websocket-connection_lost = O servidor fechou inesperadamente!
websocket-connection_lost-desc = Parece que o servidor do SlimeVR parou de funcionar. Verifique os registos e reinicie a aplicação.
websocket-timedout = Não foi possível conectar ao servidor
websocket-timedout-desc = Parece que o servidor do SlimeVR parou de funcionar ou atingiu o tempo limite. Verifique os registos e reinicie a aplicação.
websocket-error-close = Sair do SlimeVR
websocket-error-logs = Abrir a pasta de registos

## Update notification

version_update-title = Nova versão disponível: { $version }
version_update-description = Clicar em { version_update-update } irá transferir o instalador do SlimeVR para você.
version_update-update = Atualizar
version_update-close = Fechar

## Tips

tips-find_tracker = Não sabe qual é qual? Abane um tracker e o dispositivo correspondente irá ficar destacado.
tips-do_not_move_heels = Tenha cuidado para não mover os calcanhares durante a gravação!
tips-file_select = Arraste para aqui os ficheiros, ou <u>pesquise</u>
tips-tap_setup = Você pode tocar lentamente 2 vezes no tracker para o escolher em vez de o selecionar pelo menu.
tips-turn_on_tracker = A usar os trackers oficiais do SlimeVR? Não se esqueça de <b><em>ligar o tracker</em></b> após o conectar ao computador!
tips-failed_webgl = Não foi possível inicializar o WebGL.

## Units

unit-meter = Metro
unit-foot = Pé
unit-inch = Polegada
unit-cm = cm

## Body parts

body_part-NONE = Não atribuído
body_part-HEAD = Cabeça
body_part-NECK = Pescoço
body_part-RIGHT_SHOULDER = Ombro direito
body_part-RIGHT_UPPER_ARM = Braço direito
body_part-RIGHT_LOWER_ARM = Antebraço direito
body_part-RIGHT_HAND = Mão direita
body_part-RIGHT_UPPER_LEG = Coxa direita
body_part-RIGHT_LOWER_LEG = Tornozelo direito
body_part-RIGHT_FOOT = Pé direito
body_part-UPPER_CHEST = Peitoral superior
body_part-CHEST = Peito
body_part-WAIST = Cintura
body_part-HIP = Anca
body_part-LEFT_SHOULDER = Ombro esquerdo
body_part-LEFT_UPPER_ARM = Braço esquerdo
body_part-LEFT_LOWER_ARM = Antebraço esquerdo
body_part-LEFT_HAND = Mão esquerda
body_part-LEFT_UPPER_LEG = Coxa esquerda
body_part-LEFT_LOWER_LEG = Tornozelo esquerdo
body_part-LEFT_FOOT = Pé esquerdo
body_part-LEFT_THUMB_METACARPAL = Metacarpo do polegar esquerdo
body_part-LEFT_THUMB_PROXIMAL = Polegar esquerdo proximal
body_part-LEFT_THUMB_DISTAL = Polegar esquerdo distal
body_part-LEFT_INDEX_PROXIMAL = Indicador esquerdo proximal
body_part-LEFT_INDEX_INTERMEDIATE = Indicador esquerdo intermédio
body_part-LEFT_INDEX_DISTAL = Indicador esquerdo distal
body_part-LEFT_MIDDLE_PROXIMAL = Meio esquerdo proximal
body_part-LEFT_MIDDLE_INTERMEDIATE = Meio esquerdo intermédio
body_part-LEFT_MIDDLE_DISTAL = Meio distal esquerdo
body_part-LEFT_RING_PROXIMAL = Anelar esquerdo proximal
body_part-LEFT_RING_INTERMEDIATE = Anelar esquerdo intermédio
body_part-LEFT_RING_DISTAL = Anelar esquerdo distal
body_part-LEFT_LITTLE_PROXIMAL = Mindinho esquerdo proximal
body_part-LEFT_LITTLE_INTERMEDIATE = Mindinho esquerdo intermédio
body_part-LEFT_LITTLE_DISTAL = Mindinho esquerdo distal
body_part-RIGHT_THUMB_METACARPAL = Metacarpo do polegar direto
body_part-RIGHT_THUMB_PROXIMAL = Polegar direito proximal
body_part-RIGHT_THUMB_DISTAL = Polegar direito distal
body_part-RIGHT_INDEX_PROXIMAL = Indicador direito proximal
body_part-RIGHT_INDEX_INTERMEDIATE = Indicador direito intermédio
body_part-RIGHT_INDEX_DISTAL = Indicador direito distal
body_part-RIGHT_MIDDLE_PROXIMAL = Meio direito proximal
body_part-RIGHT_MIDDLE_INTERMEDIATE = Meio direito intermédio
body_part-RIGHT_MIDDLE_DISTAL = Meio direito distal
body_part-RIGHT_RING_PROXIMAL = Anelar direito proximal
body_part-RIGHT_RING_INTERMEDIATE = Anelar direito intermédio
body_part-RIGHT_RING_DISTAL = Anelar direito distal
body_part-RIGHT_LITTLE_PROXIMAL = Mindinho direito proximal
body_part-RIGHT_LITTLE_INTERMEDIATE = Mindinho direito intermédio
body_part-RIGHT_LITTLE_DISTAL = Mindinho direito distal

## BoardType

board_type-UNKNOWN = Desconhecido
board_type-NODEMCU = NodeMCU
board_type-CUSTOM = Placa Personalizada
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
board_type-GLOVE_IMU_SLIMEVR_DEV = Luva SlimeVR Dev IMU
board_type-GESTURES = Gestos
board_type-ESP32S3_SUPERMINI = ESP32-S3 Supermini
board_type-GENERIC_NRF = nRF Genérico
board_type-SLIMEVR_BUTTERFLY_DEV = SlimeVR Dev Butterfly
board_type-SLIMEVR_BUTTERFLY = SlimeVR Butterfly

## Proportions

skeleton_bone-NONE = Nenhum
skeleton_bone-HEAD = Movimento da cabeça
skeleton_bone-HEAD-desc =
    Esta é a distância do seus óculos até ao meio da sua cabeça.
    Para a ajustar, abane a cabeça da esquerda para a direita como se estivesse a discordar e modifique
    até que qualquer movimento nos outros trackers seja insignificante.
skeleton_bone-NECK = Comprimento do pescoço
skeleton_bone-NECK-desc =
    Esta é a distância do meio da sua cabeça até à base do pescoço.
    Para a ajustar, mova a cabeça para cima e para baixo como se estivesse a concordar ou incline-a
    para a esquerda e para a direita e modifique-a até que qualquer movimento nos outros trackers seja insignificante.
skeleton_bone-torso_group = Comprimento do tronco
skeleton_bone-torso_group-desc =
    Esta é a distância da base do seu pescoço até às ancas.
    Para a ajustar, fique de pé, direito, até que as suas ancas virtuais se alinhem
    com as verdadeiras.
skeleton_bone-UPPER_CHEST = Comprimento do peitoral superior
skeleton_bone-UPPER_CHEST-desc =
    Esta é a distância da base do seu pescoço até ao meio do peito.
    Para a ajustar, ajuste o comprimento do seu tronco corretamente e modifique-o em várias posições
    (sentado, curvado, deitado, etc.) até que a sua coluna virtual corresponda à verdadeira.
skeleton_bone-CHEST_OFFSET = Compensação do peito
skeleton_bone-CHEST_OFFSET-desc =
    Isto pode ser ajustado para mover o tracker do peito virtual para cima ou para baixo, de forma a auxiliar
    na calibração em determinados jogos ou aplicações que podem exigir que ele fique mais alto ou mais baixo.
skeleton_bone-CHEST = Comprimento do peito
skeleton_bone-CHEST-desc =
    Esta é a distância do meio do seu peito ao meio da sua coluna.
    Para a ajustar, ajuste o comprimento do seu tronco corretamente e modifique-o em várias posições
    (sentado, curvado, deitado, etc.) até que a sua coluna virtual corresponda à verdadeira.
skeleton_bone-WAIST = Comprimento da cintura
skeleton_bone-WAIST-desc =
    Esta é a distância do meio da sua coluna até ao umbigo.
    Para a ajustar, ajuste o comprimento do seu tronco corretamente e modifique-o em várias posições
    (sentado, curvado, deitado, etc.) até que a sua coluna virtual corresponda à verdadeira.
skeleton_bone-HIP = Comprimento da anca
skeleton_bone-HIP-desc =
    Esta é a distância do seu umbigo até às ancas.
    Para a ajustar, defina o comprimento do tronco corretamente e modifique-o em várias posições
    (sentado, curvado, deitado, etc.) até que a sua coluna virtual corresponda à verdadeira.
skeleton_bone-HIP_OFFSET = Compensação da anca
skeleton_bone-HIP_OFFSET-desc =
    Isto pode ser ajustado para mover o tracker virtual da anca para cima ou para baixo, de forma a auxiliar
    na calibração em determinados jogos ou aplicações que exigem que fique à cintura.
skeleton_bone-HIPS_WIDTH = Largura da anca
skeleton_bone-HIPS_WIDTH-desc =
    Esta é a distância entre o início das suas pernas.
    Para a ajustar, faça uma reposição completa com as pernas esticadas e ajuste-a até que
    as suas pernas virtuais coincidam com as verdadeiras horizontalmente.
skeleton_bone-leg_group = Comprimento da perna
skeleton_bone-leg_group-desc =
    Esta é a distância da sua anca aos seus pés.
    Para a ajustar, ajuste o Comprimento do Tronco corretamente e modifique-o
    até que os seus pés virtuais estejam ao mesmo nível dos seus pés reais.
skeleton_bone-UPPER_LEG = Comprimento da coxa
skeleton_bone-UPPER_LEG-desc =
    Esta é a distância das suas ancas aos joelhos.
    Para a ajustar, ajuste o Comprimento das Pernas corretamente e modifique-o
    até que os seus joelhos virtuais estejam ao mesmo nível dos joelhos verdadeiros.
skeleton_bone-LOWER_LEG = Comprimento da perna
skeleton_bone-LOWER_LEG-desc =
    Esta é a distância dos seus joelhos aos tornozelos.
    Para a ajustar, ajuste o Comprimento da Perna corretamente e modifique-o
    até que os seus joelhos virtuais estejam ao mesmo nível dos seus joelhos verdadeiros.
skeleton_bone-FOOT_LENGTH = Comprimento do pé
skeleton_bone-FOOT_LENGTH-desc =
    Esta é a distância dos seus tornozelos aos dedos dos pés.
    Para a ajustar, coloque-se na ponta dos pés e ajuste-a até que os seus pés virtuais permaneçam no lugar.
skeleton_bone-FOOT_SHIFT = Deslocamento do pé
skeleton_bone-FOOT_SHIFT-desc =
    Este valor é a distância horizontal do seu joelho até o seu tornozelo.
    Ele leva em consideração que a parte inferior das pernas se projeta para trás quando você fica de pé.
    Para o ajustar, defina o Comprimento do Pé como 0, execute "Redefinir Tudo" e ajuste até 
    que seus pés virtuais se alinhem com o meio dos seus tornozelos.
skeleton_bone-SKELETON_OFFSET = Compensação do esqueleto
skeleton_bone-SKELETON_OFFSET-desc =
    Isto pode ser ajustado para deslocar todos os seus trackers para a frente ou para trás.
    Pode ser utilizado para auxiliar na calibração em determinados jogos ou aplicações
    que podem exigir que os seus trackers estejam mais à frente.
skeleton_bone-SHOULDERS_DISTANCE = Distância dos ombros
skeleton_bone-SHOULDERS_DISTANCE-desc =
    Esta é a distância vertical da base do seu pescoço até aos seus ombros.
    Para a ajustar, defina o Comprimento do Braço para 0 e modifique-o até que os trackers virtuais do seu cotovelo
    alinhem verticalmente com os seus ombros verdadeiros.
skeleton_bone-SHOULDERS_WIDTH = Largura dos ombros
skeleton_bone-SHOULDERS_WIDTH-desc =
    Esta é a distância horizontal da base do seu pescoço até aos seus ombros.
    Para a ajustar, defina o Comprimento do Braço para 0 e modifique-o até que os trackers virtuais do seu cotovelo
    alinhem horizontalmente com os seus ombros verdadeiros.
skeleton_bone-arm_group = Comprimento do braço
skeleton_bone-arm_group-desc =
    Esta é a distância dos seus ombros aos seus pulsos.
    Para a ajustar, ajuste a Distância dos Ombros corretamente, defina a Distância das Mãos Y
    como 0 e modifique-a até que os trackers das mãos se alinhem com os seus pulsos.
skeleton_bone-UPPER_ARM = Comprimento do braço
skeleton_bone-UPPER_ARM-desc =
    Esta é a distância dos seus ombros aos cotovelos.
    Para a ajustar, ajuste o Comprimento do Braço corretamente e modifique-o até que
    os trackers dos seus cotovelos alinhem-se com os seus cotovelos verdadeiros.
skeleton_bone-LOWER_ARM = Comprimento do braço inferior
skeleton_bone-LOWER_ARM-desc =
    Esta é a distância dos seus cotovelos aos pulsos.
    Para a ajustar, ajuste o Comprimento do Braço corretamente e modifique-o até que
    os trackers dos seus cotovelos alinhem-se com os seus cotovelos verdadeiros.
skeleton_bone-HAND_Y = Distância da mão Y
skeleton_bone-HAND_Y-desc =
    Esta é a distância vertical dos seus pulsos até ao meio da sua mão.
    Para o ajustar para captura de movimento, ajuste o Comprimento do Braço corretamente e modifique-o até que os trackers da mão se alinhem verticalmente com o meio das suas mãos.
    Para o ajustar para o rastreio do cotovelo a partir dos seus controlos, defina o Comprimento do Braço para 0 e modifique-o até que os trackers do cotovelo se alinhem verticalmente com os seus pulsos.
skeleton_bone-HAND_Z = Distância da mão Z
skeleton_bone-HAND_Z-desc =
    Esta é a distância horizontal dos seus pulsos até o meio da sua mão.
    Para a ajustar para captura de movimento, defina este valor como 0.
    Para a ajustar para rastreamento de cotovelo a partir dos comandos, defina o Comprimento do Braço como 0 e
    faça ajustes até que seus trackers dos cotovelos se alinhem horizontalmente com os seus pulsos.
skeleton_bone-ELBOW_OFFSET = Compensação do cotovelo
skeleton_bone-ELBOW_OFFSET-desc =
    Isso pode ser ajustado para mover seus trackers de cotovelo virtuais para cima ou para baixo, 
    a fim de ajudar a evitar que o VRChat vincule acidentalmente um tracker de cotovelo ao peito.

## Tracker reset buttons

reset-reset_all = Repor todas a proporções
reset-reset_all_warning-v2 =
    <b>Aviso:</b> As suas proporções serão repostas para os padrões de acordo com a altura configurada.
    Tem a certeza de que quer fazer isto?
reset-reset_all_warning-reset = Repor proporções
reset-reset_all_warning-cancel = Cancelar
reset-reset_all_warning_default-v2 =
    <b>Aviso:</b> A sua altura não foi configurada, as suas proporções serão repostas para a altura predefinida.
    Tem a certeza de que quer fazer isto?
reset-full = Repor tudo
reset-mounting = Calibração de montagem
reset-mounting-feet = Calibração dos pés
reset-mounting-fingers = Calibração dos dedos
reset-yaw = Reposição horizontal
reset-error-no_feet_tracker = Nenhum tracker de pés atribuído
reset-error-no_fingers_tracker = Nenhum tracker de dedos atribuído
reset-error-mounting-need_full_reset = É necessário fazer uma redefinição completa antes da montagem
reset-error-yaw-need_full_reset = É necessário fazer uma reposição completa antes de repor   horizontalmente

## Serial detection stuff

serial_detection-new_device-p0 = Novo dispositivo serial detetado!
serial_detection-new_device-p1 = Insira as credenciais do seu Wi-Fi!
serial_detection-new_device-p2 = Selecione o que pretende fazer com ele
serial_detection-open_wifi = Ligar ao Wi-Fi
serial_detection-open_serial = Abrir a Consola Serial
serial_detection-submit = Submeter!
serial_detection-close = Fechar

## Navigation bar

navbar-home = Início
navbar-body_proportions = Proporções do corpo
navbar-trackers_assign = Atribuição do tracker
navbar-mounting = Calibração de montagem
navbar-onboarding = Assistente de configuração
navbar-settings = Definições

## Biovision hierarchy recording

bvh-start_recording = Gravar BVH
bvh-recording = A gravar...
bvh-save_title = Guardar a gravação BVH

## Tracking pause

tracking-unpaused = Pausar rastreamento
tracking-paused = Retomar rastreamento

## Widget: Overlay settings

widget-overlay = Sobreposição
widget-overlay-is_visible_label = Mostrar a sobreposição no SteamVR
widget-overlay-is_mirrored_label = Exibir sobreposição como espelho

## Widget: Drift compensation

widget-drift_compensation-clear = Remover a compensação de desvio

## Widget: Clear Mounting calibration

widget-clear_mounting = Limpar a calibração de montagem

## Widget: Developer settings

widget-developer_mode = Modo de desenvolvedor
widget-developer_mode-high_contrast = Alto contraste
widget-developer_mode-precise_rotation = Rotação precisa
widget-developer_mode-fast_data_feed = Alimentação rápida de dados
widget-developer_mode-filter_slimes_and_hmd = Filtrar os Slimes e HMD
widget-developer_mode-sort_by_name = Ordenar por nome
widget-developer_mode-raw_slime_rotation = Rotação bruta
widget-developer_mode-more_info = Mais informação

## Widget: IMU Visualizer

widget-imu_visualizer = Dados de rastreamento
widget-imu_visualizer-preview = Pré-visualização
widget-imu_visualizer-hide = Esconder
widget-imu_visualizer-rotation_raw = Rotação bruta
widget-imu_visualizer-rotation_preview = Prévisualizar rotação
widget-imu_visualizer-acceleration = Aceleração
widget-imu_visualizer-position = Posição
widget-imu_visualizer-stay_aligned = Continue alinhado

## Widget: Skeleton Visualizer

widget-skeleton_visualizer-preview = Visualização do esqueleto
widget-skeleton_visualizer-hide = Esconder

## Tracker status

tracker-status-none = Nenhum estado
tracker-status-busy = Ocupado
tracker-status-error = Erro
tracker-status-disconnected = Desconectado
tracker-status-occluded = Obstruído
tracker-status-ok = OK
tracker-status-timed_out = Tempo limite esgotado

## Tracker status columns

tracker-table-column-name = Nome
tracker-table-column-type = Tipo
tracker-table-column-battery = Bateria
tracker-table-column-ping = Latência
tracker-table-column-tps = TPS
tracker-table-column-temperature = Temp. °C
tracker-table-column-linear-acceleration = Acel. X/Y/Z
tracker-table-column-rotation = Rotação X/Y/Z
tracker-table-column-position = Posição X/Y/Z
tracker-table-column-stay_aligned = Continue alinhado
tracker-table-column-url = URL

## Tracker rotation

tracker-rotation-front = Frente
tracker-rotation-front_left = Frente-Esquerda
tracker-rotation-front_right = Frente-Direita
tracker-rotation-left = Esquerda
tracker-rotation-right = Direita
tracker-rotation-back = Atrás
tracker-rotation-back_left = Atrás-Esquerda
tracker-rotation-back_right = Atrás-Direita
tracker-rotation-custom = Personalizado
tracker-rotation-overriden = (substituído pela calibração de montagem)

## Tracker information

tracker-infos-manufacturer = Fabricante
tracker-infos-display_name = Nome de exibição
tracker-infos-custom_name = Nome personalizado
tracker-infos-url = URL do tracker
tracker-infos-version = Versão do firmware
tracker-infos-hardware_rev = Revisão do hardware
tracker-infos-hardware_identifier = ID do hardware
tracker-infos-data_support = Suporte de dados
tracker-infos-imu = Sensor IMU
tracker-infos-board_type = Placa principal
tracker-infos-network_version = Versão do protocolo
tracker-infos-magnetometer = Magnetómetro
tracker-infos-magnetometer-status-v1 =
    { $status ->
        [DISABLED] Inativo
        [ENABLED] Ativo
       *[NOT_SUPPORTED] Não suportado
    }

## Tracker settings

tracker-settings-back = Voltar à lista de trackers
tracker-settings-title = Definições dos tracker
tracker-settings-assignment_section = Atribuição
tracker-settings-assignment_section-description = A que parte do corpo está atribuído o tracker.
tracker-settings-assignment_section-edit = Editar atribuição
tracker-settings-mounting_section = Orientação de montagem
tracker-settings-mounting_section-description = Onde é que o tracker está montado?
tracker-settings-mounting_section-edit = Editar posição
tracker-settings-drift_compensation_section = Permitir compensação de desvio
tracker-settings-drift_compensation_section-description = Este tracker deve compensar pelo o seu desvio quando a compensação de desvio está ativada?
tracker-settings-drift_compensation_section-edit = Permitir compensação de desvio
tracker-settings-use_mag = Permitir magnetómetro neste tracker
# Multiline!
tracker-settings-use_mag-description =
    Este tracker deve utilizar o magnetómetro para reduzir o desvio quando o uso do magnetómetro é permitido? <b>Por favor não desligue o tracker enquanto estiver a ativar esta opção!</b>
    Precisa de permitir o uso do magnetómetro primeiro, <magSetting>clique aqui para aceder à configuração</magSetting>.
tracker-settings-use_mag-label = Permitir magnetómetro
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Nome do tracker
tracker-settings-name_section-description = Dê-lhe um apelido fofo :)
tracker-settings-name_section-placeholder = Perna esquerda de NightyBeast
tracker-settings-name_section-label = Nome do tracker
tracker-settings-forget = Esquecer tracker
tracker-settings-forget-description = Remove o tracker do servidor do SlimeVR e impede a ligação até que o servidor seja reiniciado. A configuração do tracker não será apagada.
tracker-settings-forget-label = Esquecer tracker
tracker-settings-update-low-battery = Não é possível atualizar. Bateria com menos de 50%
tracker-settings-update-up_to_date = Atualizado
tracker-settings-update = Atualizar agora
tracker-settings-update-title = Versão do firmware

## Tracker part card info

tracker-part_card-no_name = Nenhum nome
tracker-part_card-unassigned = Não atribuído

## Body assignment menu

body_assignment_menu = Onde quer que este tracker esteja?
body_assignment_menu-description = Selecione o local onde pretende que este tracker seja atribuído. Em alternativa, pode optar por gerir todos os trackers de uma só vez, em vez de um a um.
body_assignment_menu-show_advanced_locations = Mostrar locais de atribuição avançados
body_assignment_menu-manage_trackers = Gerir todos os trackers
body_assignment_menu-unassign_tracker = Desatribuir tracker

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = Qual o tracker a atribuir ao seu
tracker_selection_menu-NONE = Qual é o tracker que pretende que seja desatribuído?
tracker_selection_menu-HEAD = { -tracker_selection-part } cabeça?
tracker_selection_menu-NECK = { -tracker_selection-part } pescoço?
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part } ombro direito?
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part } braço direito?
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part } braço inferior direito?
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part } mão direita?
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part } coxa direita?
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part } tornozelo direito?
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part } pé direito?
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part } comando direito?
tracker_selection_menu-UPPER_CHEST = { -tracker_selection-part } peitoral superior?
tracker_selection_menu-CHEST = { -tracker_selection-part } peito?
tracker_selection_menu-WAIST = { -tracker_selection-part } cintura?
tracker_selection_menu-HIP = { -tracker_selection-part } anca?
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part } ombro esquerdo?
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part } braço esquerdo?
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part } braço inferior esquerdo?
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part } mão esquerda?
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part } coxa esquerda?
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part } tornozelo esquerdo?
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part } pé esquerdo?
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part } comando esquerdo?
tracker_selection_menu-unassigned = Trackers não atribuídos
tracker_selection_menu-assigned = Trackers atribuídos
tracker_selection_menu-dont_assign = Desatribuir
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Aviso:</b> Um tracker de pescoço pode ser fatal se estiver muito justo;
    a tira pode cortar a circulação para a sua cabeça!
tracker_selection_menu-neck_warning-done = Eu compreendo os riscos
tracker_selection_menu-neck_warning-cancel = Cancelar

## Mounting menu

mounting_selection_menu = Onde quer que este tracker esteja?
mounting_selection_menu-close = Fechar

## Sidebar settings

settings-sidebar-title = Definições
settings-sidebar-general = Geral
settings-sidebar-steamvr = SteamVR
settings-sidebar-tracker_mechanics = Mecânicas do tracker
settings-sidebar-stay_aligned = Continue alinhado
settings-sidebar-fk_settings = Definições do rastreio
settings-sidebar-gesture_control = Controlo de gestos
settings-sidebar-interface = Interface
settings-sidebar-osc_router = Router OSC
settings-sidebar-osc_trackers = Trackers OSC do VRChat
settings-sidebar-osc_vmc = VMC
settings-sidebar-utils = Utilitários
settings-sidebar-serial = Consola Serial
settings-sidebar-appearance = Aparência
settings-sidebar-home = Ecrã Inicial
settings-sidebar-checklist = Lista de Tarefas
settings-sidebar-notifications = Notificações
settings-sidebar-behavior = Comportamento
settings-sidebar-firmware-tool = Ferramenta de firmware DIY
settings-sidebar-vrc_warnings = Avisos de configuração do VRChat
settings-sidebar-advanced = Avançado

## SteamVR settings

settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = Trackers do SteamVR
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    Ative ou desative trackers específicos do SteamVR.
    Útil para jogos ou aplicações que suportam apenas determinados trackers.
settings-general-steamvr-trackers-waist = Cintura
settings-general-steamvr-trackers-chest = Peito
settings-general-steamvr-trackers-left_foot = Pé esquerdo
settings-general-steamvr-trackers-right_foot = Pé direito
settings-general-steamvr-trackers-left_knee = Joelho esquerdo
settings-general-steamvr-trackers-right_knee = Joelho direito
settings-general-steamvr-trackers-left_elbow = Cotovelo esquerdo
settings-general-steamvr-trackers-right_elbow = Cotovelo direito
settings-general-steamvr-trackers-left_hand = Mão esquerda
settings-general-steamvr-trackers-right_hand = Mão Direita
settings-general-steamvr-trackers-tracker_toggling = Atribuição automática do tracker
settings-general-steamvr-trackers-tracker_toggling-description = Lida automaticamente com a ativação ou desativação dos trackers no SteamVR, dependendo das suas atribuições atuais de trackers.
settings-general-steamvr-trackers-tracker_toggling-label = Atribuição automática de trackers
settings-general-steamvr-trackers-hands-warning =
    <b>Aviso:</b> Ativar os trackers de mão no SteamVR desativará as entradas de comandos reais.
    Isto só deve ser ativado se você estiver a utilizar o SlimeVR para tracking de mãos
    Tem a certeza de que quer fazer isto?
settings-general-steamvr-trackers-hands-warning-cancel = Cancelar
settings-general-steamvr-trackers-hands-warning-done = Sim

## Tracker mechanics

settings-general-tracker_mechanics = Mecânicas do tracker
settings-general-tracker_mechanics-filtering = Filtramento

## FK/Tracking settings


## Gesture control settings (tracker tapping)


## Appearance settings


## Notification settings


## Behavior settings


## Serial settings


## OSC router settings


## OSC VRChat settings


## VMC OSC settings


## Common OSC settings


## Advanced settings


## Home Screen


## Tracking Checlist


## Setup/onboarding menu


## Quiz


## Wi-Fi setup


## Mounting setup


## Install info


## Setup start


## Setup done


## Tracker connection setup


## Tracker calibration tutorial


## Tracker assignment tutorial


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


## Status system


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

