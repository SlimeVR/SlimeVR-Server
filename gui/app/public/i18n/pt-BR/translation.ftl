# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Carregando...
websocket-connection_lost = O servidor parou de funcionar!
websocket-connection_lost-desc = Parece que o servidor do SlimeVR parou de funcionar. Verifique os logs e reinicie o programa.
websocket-timedout = Não foi possível conectar-se ao servidor
websocket-timedout-desc = Parece que o servidor do SlimeVR parou de funcionar ou atingiu o tempo limite. Verifique os logs e reinicie o programa.
websocket-error-close = Sair do SlimeVR
websocket-error-logs = Abrir a pasta de logs

## Update notification

version_update-title = Nova versão disponível: { $version }
version_update-description = Ao clicar em "{ version_update-update }" irá baixar o instalador do SlimeVR para você.
version_update-update = Atualizar
version_update-close = Fechar

## Tips

tips-find_tracker = Não tem certeza de qual tracker é qual? Sacuda um tracker e o item correspondente será destacado.
tips-do_not_move_heels = Certifique-se de manter os calcanhares imóveis durante a gravação!
tips-file_select = Arraste e solte arquivos para usar, ou <u>pesquise</u>.
tips-failed_webgl = Falha ao inicializar o WebGL.

## Units

unit-meter = Metros
unit-foot = Pés
unit-inch = Polegadas

## Dropdown


## Text input


## File input


## Window controls

titlebar-close = Fechar

## Body parts

body_part-NONE = Não atribuído
body_part-HEAD = Cabeça
body_part-NECK = Pescoço
body_part-RIGHT_SHOULDER = Ombro direito
body_part-RIGHT_UPPER_ARM = Braço superior direito
body_part-RIGHT_LOWER_ARM = Antebraço direito
body_part-RIGHT_HAND = Mão direita
body_part-RIGHT_UPPER_LEG = Coxa direita
body_part-RIGHT_LOWER_LEG = Tornozelo direito
body_part-RIGHT_FOOT = Pé direito
body_part-UPPER_CHEST = Peito superior
body_part-HIP = Quadril
body_part-LEFT_SHOULDER = Ombro esquerdo
body_part-LEFT_UPPER_ARM = Braço superior esquerdo
body_part-LEFT_LOWER_ARM = Antebraço esquerdo
body_part-LEFT_HAND = Mão esquerda
body_part-LEFT_UPPER_LEG = Coxa esquerda
body_part-LEFT_LOWER_LEG = Tornozelo esquerdo
body_part-LEFT_FOOT = Pé esquerdo
body_part-LEFT_THUMB_METACARPAL = Metacarpo do polegar esquerdo
body_part-LEFT_THUMB_PROXIMAL = Proximal do polegar esquerdo
body_part-LEFT_THUMB_DISTAL = Distal do polegar esquerdo
body_part-LEFT_INDEX_PROXIMAL = Indicador esquerdo proximal
body_part-LEFT_INDEX_INTERMEDIATE = Indicador esquerdo intermediário
body_part-LEFT_INDEX_DISTAL = Indicador esquerdo distal
body_part-LEFT_MIDDLE_PROXIMAL = Médio esquerdo proximal
body_part-LEFT_MIDDLE_INTERMEDIATE = Médio esquerdo intermediário
body_part-LEFT_MIDDLE_DISTAL = Médio distal esquerdo
body_part-LEFT_RING_PROXIMAL = Anelar esquerdo proximal
body_part-LEFT_RING_INTERMEDIATE = Anelar esquerdo intermediário
body_part-LEFT_RING_DISTAL = Anelar esquerdo distal
body_part-LEFT_LITTLE_PROXIMAL = Mindinho esquerdo proximal
body_part-LEFT_LITTLE_INTERMEDIATE = Mindinho esquerdo intermediário
body_part-LEFT_LITTLE_DISTAL = Mindinho esquerdo distal
body_part-RIGHT_THUMB_METACARPAL = Metacarpo do polegar direito
body_part-RIGHT_THUMB_PROXIMAL = Polegar direito proximal
body_part-RIGHT_THUMB_DISTAL = Distal do polegar direito
body_part-RIGHT_INDEX_PROXIMAL = Porção proximal do dedo indicador direito
body_part-RIGHT_INDEX_INTERMEDIATE = Porção intermediária do dedo indicador direito
body_part-RIGHT_INDEX_DISTAL = Porção distal do dedo indicador direito
body_part-RIGHT_MIDDLE_PROXIMAL = Porção proximal do dedo médio direito
body_part-RIGHT_MIDDLE_INTERMEDIATE = Porção intermediária do dedo médio direito
body_part-RIGHT_MIDDLE_DISTAL = Porção distal do dedo médio direito
body_part-RIGHT_RING_PROXIMAL = Porção proximal do dedo anelar direito
body_part-RIGHT_RING_INTERMEDIATE = Porção intermediária do dedo anelar direito
body_part-RIGHT_RING_DISTAL = Porção distal do dedo anelar direito
body_part-RIGHT_LITTLE_PROXIMAL = Porção proximal do dedo mínimo direito
body_part-RIGHT_LITTLE_INTERMEDIATE = Porção intermediária do dedo mínimo direito
body_part-RIGHT_LITTLE_DISTAL = Porção distal do dedo mínimo direito

## BoardType

board_type-UNKNOWN = Desconhecido
board_type-CUSTOM = Placa Customizada
board_type-SLIMEVR_DEV = Placa do SlimeVR Dev
board_type-WRANGLER = Joycons
board_type-GESTURES = Gestos
board_type-GENERIC_NRF = nRF genérico

## Proportions

skeleton_bone-NONE = Nada
skeleton_bone-HEAD = Deslocamento da Cabeça
skeleton_bone-HEAD-desc =
    Esta é a distância do seu headset até o centro da sua cabeça.
    Para ajustá-la, balance a cabeça da esquerda para a direita, como se estivesse discordando, 
    e modifique o valor até que qualquer movimento nos outros trackers seja insignificante.
skeleton_bone-NECK = Comprimento do Pescoço
skeleton_bone-NECK-desc =
    Esta é a distância do centro da sua cabeça até a base do seu pescoço.
    Para ajustá-la, mova a cabeça para cima e para baixo, como se estivesse assentindo, 
    ou incline a cabeça para a esquerda e para a direita, ajustando o valor até que qualquer movimento nos outros trackers seja insignificante.
skeleton_bone-torso_group = Comprimento do Tronco
skeleton_bone-torso_group-desc =
    Esta é a distância da base do seu pescoço até os seus quadris.
    Para ajustá-la, fique em pé com a postura ereta e modifique o valor até 
    que seus quadris virtuais se alinhem com os reais.
skeleton_bone-UPPER_CHEST = Comprimento do Peito Superior
skeleton_bone-UPPER_CHEST-desc =
    Esta é a distância da base do seu pescoço até o meio do seu peito.
    Para ajustá-la, configure corretamente o Comprimento do Tronco e faça ajustes em várias posições
    (sentado, curvado, deitado, etc.) até que sua coluna virtual coincida com a real.
skeleton_bone-LOWER_CHEST-desc =
    Esta é a distância do meio do seu peito até o meio da sua coluna.
    Para ajustá-la, ajuste corretamente o Comprimento do Tronco e modifique o valor em várias posições 
    (sentado, inclinado para frente, deitado, etc.) até que sua coluna virtual corresponda à sua coluna real.
skeleton_bone-HIP = Comprimento do Quadril
skeleton_bone-HIPS_WIDTH = Largura do Quadril
skeleton_bone-HIPS_WIDTH-desc =
    Esta é a distância entre o início das suas pernas.
    Para ajustá-la, execute "Redefinir Tudo" com as pernas retas e modifique-a até que suas pernas virtuais se alinhem horizontalmente com as pernas reais.
skeleton_bone-leg_group = Comprimento da Perna
skeleton_bone-leg_group-desc =
    Esta é a distância dos seus quadris até os seus pés.
    Para ajustá-la, configure corretamente o Comprimento do Tronco e faça ajustes
    até que seus pés virtuais fiquem no mesmo nível dos reais.
skeleton_bone-UPPER_LEG = Comprimento da Coxa
skeleton_bone-UPPER_LEG-desc =
    Esta é a distância dos seus quadris até os seus joelhos.
    Para ajustá-la, configure corretamente o Comprimento das Pernas e faça ajustes
    até que seus joelhos virtuais fiquem no mesmo nível dos reais.
skeleton_bone-LOWER_LEG = Comprimento da Parte Inferior da Perna
skeleton_bone-LOWER_LEG-desc =
    Esta é a distância dos seus joelhos até os seus tornozelos.
    Para ajustá-la, configure corretamente o Comprimento das Pernas e faça ajustes
    até que seus joelhos virtuais fiquem no mesmo nível dos reais.
skeleton_bone-FOOT_LENGTH = Comprimento do Pé
skeleton_bone-FOOT_LENGTH-desc =
    Esta é a distância dos seus tornozelos até os seus dedos dos pés.
    Para ajustá-la, fique na ponta dos pés e faça ajustes até que seus pés virtuais permaneçam no lugar.
skeleton_bone-FOOT_SHIFT = Deslocamento do Pé
skeleton_bone-FOOT_SHIFT-desc =
    Este valor é a distância horizontal do seu joelho até o seu tornozelo.
    Ele leva em consideração que a parte inferior das pernas se projeta para trás quando você fica em pé.
    Para ajustá-lo, defina o Comprimento do Pé como 0, execute "Redefinir Tudo" e ajuste até 
    que seus pés virtuais se alinhem com o meio dos seus tornozelos.
skeleton_bone-SHOULDERS_DISTANCE = Distância dos Ombros
skeleton_bone-SHOULDERS_DISTANCE-desc =
    Esta é a distância vertical da base do seu pescoço até os seus ombros.
    Para ajustá-la, defina o Comprimento do Braço Superior como 0 e faça ajustes até que
    seus trackers virtuais de cotovelo se alinhem verticalmente com os seus ombros reais.
skeleton_bone-SHOULDERS_WIDTH = Largura dos Ombros
skeleton_bone-SHOULDERS_WIDTH-desc =
    Esta é a distância horizontal da base do seu pescoço até os seus ombros.
    Para ajustá-la, defina o Comprimento do Braço Superior como 0 e faça ajustes até que
    seus trackers virtuais de cotovelo se alinhem horizontalmente com os seus ombros reais.
skeleton_bone-arm_group = Comprimento do Braço
skeleton_bone-arm_group-desc =
    Esta é a distância entre seus ombros e seus pulsos.
    Para ajustá-la, ajuste corretamente a Distância dos ombros, defina Distância das Mãos Y como 0 e modifique-a até que os trackers das mãos se alinhem com seus pulsos.
skeleton_bone-UPPER_ARM = Comprimento do Braço Superior
skeleton_bone-UPPER_ARM-desc =
    Esta é a distância dos seus ombros até os seus cotovelos.
    Para ajustá-la, configure corretamente o Comprimento do Braço e faça ajustes até que
    seus trackers de cotovelo se alinhem com os seus cotovelos reais.
skeleton_bone-LOWER_ARM = Comprimento do Antebraço
skeleton_bone-LOWER_ARM-desc =
    Esta é a distância entre seus cotovelos e seus pulsos.
    Para ajustá-la, ajuste corretamente o Comprimento do Braço e modifique-o até 
    que os trackers de cotovelo se alinhem com seus cotovelos reais.
skeleton_bone-HAND_Y = Distância da Mão Y
skeleton_bone-HAND_Y-desc =
    Esta é a distância vertical dos seus punhos até o meio da sua mão.
    Para ajustá-la para captura de movimento, configure corretamente o Comprimento do Braço e faça ajustes
    até que seus trackers de mão se alinhem verticalmente com o meio das suas mãos.
    Para ajustá-la para rastreamento de cotovelo a partir dos controladores, defina o Comprimento do Braço como 0
    e faça ajustes até que seus trackers de cotovelo se alinhem verticalmente com os seus punhos.
skeleton_bone-HAND_Z = Distância da Mão Z
skeleton_bone-HAND_Z-desc =
    Esta é a distância horizontal dos seus punhos até o meio da sua mão.
    Para ajustá-la para captura de movimento, defina este valor como 0.
    Para ajustá-la para rastreamento de cotovelo a partir dos controladores, defina o Comprimento do Braço como 0 e
    faça ajustes até que seus trackers de cotovelo se alinhem horizontalmente com os seus punhos.

## Tracker reset buttons

reset-reset_all = Redefinir todas as proporções
reset-reset_all_warning-reset = Redefinir proporções
reset-reset_all_warning-cancel = Cancelar
reset-full = Redefinir Tudo
reset-mounting = Calibração de Montagem
reset-mounting-feet = Calibrar Pés
reset-mounting-fingers = Calibrar Dedos
reset-yaw = Redefinir Rápido (Guinada)
reset-error-mounting-need_full_reset = É necessário fazer uma redefinição completa antes da montagem
reset-error-yaw-need_full_reset = É necessário executar "Redefinir Tudo" antes de "Redefinir Rápido (Guinada)"

## Navigation bar

navbar-home = Início
navbar-body_proportions = Proporções do Corpo
navbar-trackers_assign = Atribuição de Tracker
navbar-mounting = Calibração de Montagem
navbar-onboarding = Assistente de Configuração
navbar-settings = Opções
navbar-connect_trackers = Conectar Trackers

## Biovision hierarchy recording

bvh-start_recording = Gravar BVH
bvh-stop_recording = Salvar gravação BVH
bvh-recording = Gravando...
bvh-save_title = Salvar gravação BVH

## Tracking pause

tracking-unpaused = Pausar rastreamento
tracking-paused = Retomar rastreamento

## Widget: Developer settings

widget-developer_mode = Modo de desenvolvedor
widget-developer_mode-high_contrast = Alto contraste
widget-developer_mode-precise_rotation = Rotação precisa
widget-developer_mode-fast_data_feed = Fluxo de dados rápido
widget-developer_mode-raw_slime_rotation = Rotação bruta

## Widget: IMU Visualizer

widget-imu_visualizer = Dados de rastreamento
widget-imu_visualizer-preview = Pré-visualização
widget-imu_visualizer-hide = Esconder
widget-imu_visualizer-rotation_raw = Rotação bruta
widget-imu_visualizer-rotation_preview = Pré-visualizar rotação
widget-imu_visualizer-acceleration = Aceleração
widget-imu_visualizer-position = Posição
widget-imu_visualizer-stay_aligned = Manter Alinhado

## Tracker status

tracker-status-none = Sem Status
tracker-status-busy = Ocupado
tracker-status-error = Erro
tracker-status-disconnected = Desconectado
tracker-status-occluded = Ocluído
tracker-status-timed_out = Tempo limite atingido

## Tracker status columns

tracker-table-column-name = Nome
tracker-table-column-type = Tipo
tracker-table-column-battery = Bateria
tracker-table-column-linear-acceleration = Aceleração. X/Y/Z
tracker-table-column-rotation = Rotação X/Y/Z
tracker-table-column-position = Posição X/Y/Z
tracker-table-column-stay_aligned = Manter Alinhado

## Tracker rotation

tracker-rotation-front = Frente
tracker-rotation-front_left = Frente-Esquerda
tracker-rotation-front_right = Frente-Direita
tracker-rotation-left = Esquerda
tracker-rotation-right = Direita
tracker-rotation-back = Voltar
tracker-rotation-back_left = Trás-Esquerda
tracker-rotation-back_right = Trás-Direita
tracker-rotation-custom = Personalizado

## Tracker information

tracker-infos-manufacturer = Fabricante
tracker-infos-display_name = Nome de Exibição
tracker-infos-custom_name = Nome Personalizado
tracker-infos-url = URL do Tracker
tracker-infos-hardware_identifier = ID do Hardware
tracker-infos-imu = Sensor IMU
tracker-infos-board_type = Placa principal
tracker-infos-network_version = Versão do protocolo
tracker-infos-magnetometer = Magnetômetro
tracker-infos-magnetometer-status-v1 =
    { $status ->
        [DISABLED] Desativado
        [ENABLED] Ativado
       *[NOT_SUPPORTED] Não Suportado
    }
tracker-infos-packet_loss = Perda de Pacotes
tracker-infos-packets_lost = Perda de Pacotes
tracker-infos-packets_received = Pacotes Recebidos

## Tracker settings

tracker-settings-back = Voltar para a lista de trackers
tracker-settings-title = Opções dos Trackers
tracker-settings-assignment_section = Atribuição
tracker-settings-assignment_section-description = A qual parte do corpo o tracker está atribuído.
tracker-settings-assignment_section-edit = Editar atribuição
tracker-settings-mounting_section = Orientação de montagem
tracker-settings-mounting_section-description = Onde o tracker está posicionado?
tracker-settings-mounting_section-edit = Editar montagem
tracker-settings-use_mag = Permitir o uso do magnetômetro neste tracker
# Multiline!
tracker-settings-use_mag-description =
    Esse tracker deve usar o magnetômetro para reduzir o drift quando o uso de magnetômetro estiver permitido? <b>Não desligue seu tracker enquanto altera esta opção!</b>
    
    Você precisa permitir o uso de magnetômetro primeiro, <magSetting>clique aqui para ir para as configurações</magSetting>.
tracker-settings-use_mag-label = Permitir o uso do magnetômetro
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Nome do tracker
tracker-settings-name_section-placeholder = Coxa esquerda de NightyBeast
tracker-settings-name_section-label = Nome do tracker
tracker-settings-forget = Esquecer o tracker
tracker-settings-forget-description = Remove o tracker do servidor SlimeVR e impede que ele se conecte a ele até que o servidor seja reiniciado. A configuração do tracker não será perdida.
tracker-settings-forget-label = Esquecer o tracker
tracker-settings-update-unavailable-v2 = Nenhuma versão nova encontrada
tracker-settings-update-incompatible = Não é possível atualizar. Placa ou versão de firmware incompatível
tracker-settings-update-low-battery = Não é possível atualizar. Bateria abaixo de 50%
tracker-settings-update-up_to_date = Atualizado
tracker-settings-update-blocked = Atualização não disponível. Não há outras novas versões disponíveis
tracker-settings-update = Atualizar agora
tracker-settings-update-title = Versão do firmware
tracker-settings-current-version = Atual
tracker-settings-latest-version = Último
tracker-settings-build-date = Data da Compilação

## Dongle settings

dongle-infos-hardware_revision = Revisão do hardware
dongle-status-disconnected = Desconectado
dongle-settings-back = Voltar para a lista de trackers
dongle-settings-update = Atualizar agora
dongle-settings-update-title = Versão do firmware

## Tracker part card info

tracker-part_card-unassigned = Não atribuído

## Body assignment menu

body_assignment_menu = Onde você quer que esse tracker fique?
body_assignment_menu-description = Escolha a parte do corpo à qual deseja atribuir este tracker. Você também pode gerenciar todos os trackers de uma vez, em vez de configurá-los individualmente.
body_assignment_menu-manage_trackers = Arrumar todos os trackers
body_assignment_menu-unassign_tracker = Desatribuir tracker

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Atenção:</b> O tracker de pescoço pode ser mortal se ajustado muito apertado,
    o strap pode cortar a circulação para sua cabeça!
tracker_selection_menu-neck_warning-done = Eu entendo os riscos
tracker_selection_menu-neck_warning-cancel = Cancelar

## Mounting menu

mounting_selection_menu-close = Fechar

## Sidebar settings

settings-sidebar-title = Opções
settings-sidebar-general = Geral
settings-sidebar-stay_aligned = Manter Alinhado
settings-sidebar-utils = Utilidades
settings-sidebar-appearance = Aparência
settings-sidebar-home = Tela Inicial
settings-sidebar-checklist = Checklist do Rastreamento
settings-sidebar-notifications = Notificações
settings-sidebar-firmware-tool = Ferramenta de firmware DIY
settings-sidebar-vrc_warnings = Alerta nas Configurações do VRChat
settings-sidebar-advanced = Avançado

## Bone routing settings

settings-routing-output-badge-off = Desativado
settings-routing-group-fingers = Dedos
settings-routing-hands-warning-cancel = Cancelar

## SteamVR / Monado output settings

settings-driver-enable = Ativar
settings-driver-status-badge-disabled = Desativado

## Tracker mechanics

settings-general-tracker_mechanics-filtering = Filtros
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Escolha o tipo de filtragem para seus trackers.
    A predição antecipa o movimento, enquanto a suavização reduz as variações do movimento.
settings-general-tracker_mechanics-filtering-type-none = Sem filtro
settings-general-tracker_mechanics-filtering-type-none-description = Usa as rotações como estão. Não aplicará nenhuma filtragem.
settings-general-tracker_mechanics-filtering-type-smoothing = Suavização
settings-general-tracker_mechanics-filtering-type-smoothing-description = Suaviza os movimentos, mas adiciona um pouco de latência.
settings-general-tracker_mechanics-filtering-type-prediction = Predição
settings-general-tracker_mechanics-filtering-type-prediction-description = Reduz a latência e torna os movimentos mais responsivos, mas pode aumentar a oscilação.
settings-general-tracker_mechanics-save_mounting_reset = Salvar calibração automática de montagem
settings-general-tracker_mechanics-save_mounting_reset-description = Salva a calibração automática de montagem dos trackers entre reinicializações. Útil ao usar um traje em que os trackers não se movem entre as sessões. <b>Não recomendado para usuários normais!</b>
settings-general-tracker_mechanics-save_mounting_reset-enabled-label = Salvar calibração de montagem
settings-general-tracker_mechanics-use_mag_on_all_trackers = Usar o magnetômetro em todos os trackers IMUs compatíveis
settings-general-tracker_mechanics-use_mag_on_all_trackers-description =
    Usa o magnetômetro em todos os trackers com firmware compatível, reduzindo o drift em ambientes magneticamente estáveis.
    Essa opção pode ser desativada individualmente nas configurações de cada tracker. <b>Não desligue nenhum dos trackers enquanto altera esta opção!</b>
settings-general-tracker_mechanics-use_mag_on_all_trackers-label = Usar o magnetômetro nos trackers
settings-general-tracker_mechanics-trackers_over_usb = Trackers via USB
settings-general-tracker_mechanics-trackers_over_usb-description = Ativa o recebimento de dados de trackers HID via USB. Certifique-se de que os trackers conectados tenham a opção <b>conexão via HID</b> ativada!
settings-general-tracker_mechanics-trackers_over_usb-enabled-label = Permitir que trackers HID se conectem diretamente via USB
settings-stay_aligned-description = O Manter Alinhado reduz o drift ao ajustar gradualmente seus trackers para corresponder às suas poses relaxadas
settings-stay_aligned-setup-label = Configurar o Manter Alinhado
settings-stay_aligned-setup-description = É necessário concluir “Configurar o Manter Alinhado” para ativar o Manter Alinhado.
settings-stay_aligned-enabled-label = Ajustar trackers
settings-stay_aligned-general-label = Geral
settings-stay_aligned-relaxed_poses-label = Poses Relaxadas
settings-stay_aligned-relaxed_poses-description = O Manter Alinhado usa suas poses relaxadas para manter os trackers alinhados. Use “Configurar o Manter Alinhado” para atualizar essas poses.
settings-stay_aligned-relaxed_poses-standing = Ajustar trackers enquanto estiver em pé
settings-stay_aligned-relaxed_poses-sitting = Ajustar trackers enquanto estiver sentado em uma cadeira
settings-stay_aligned-relaxed_poses-flat = Ajustar trackers sentado no chão ou deitado de costas
settings-stay_aligned-relaxed_poses-save_pose = Salvar Pose
settings-stay_aligned-relaxed_poses-reset_pose = Redefinir Pose
settings-stay_aligned-relaxed_poses-close = Fechar
settings-stay_aligned-debug-label = Depuração
settings-stay_aligned-debug-description = Inclua suas configurações ao relatar problemas relacionados ao Manter Alinhado.
settings-stay_aligned-debug-copy-label = Copiar configurações para a área de transferência

## Keybinds Page

settings-keybinds_full-reset = Redefinir Tudo
settings-keybinds_yaw-reset = Redefinir Rápido (Guinada)
settings-keybinds_reset-all-button = Redefinir todas as configurações
settings-keybinds-recorder-modal-done-button = Concluído
settings-keybinds-recorder-modal-cancel-button = Cancelar

## FK/Tracking settings

# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Limite de chão
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Correção de deslize
settings-general-fk_settings-leg_tweak-toe_snap = Encaixar os dedos do pé
settings-general-fk_settings-leg_tweak-foot_plant = Pé plantado
settings-general-fk_settings-leg_tweak-skating_correction-amount = Força da correção de deslize
settings-general-fk_settings-leg_tweak-skating_correction-description = A correção de deslizamento corrige o efeito de patinação, mas pode reduzir a precisão em certos padrões de movimento. Ao ativá-la, certifique-se de executar "Redefinir Tudo" e recalibrar no jogo.
settings-general-fk_settings-leg_tweak-floor_clip-description = Limite de chão pode reduzir ou eliminar o atravessamento do chão. Ao ativá-lo, certifique-se de executar "Redefinir Tudo" e recalibrar no jogo.
settings-general-fk_settings-leg_tweak-toe_snap-description = Encaixar os dedos do pé, tenta adivinhar a rotação dos seus pés se os trackers dos pés não estiverem em uso.
settings-general-fk_settings-leg_tweak-foot_plant-description = Pé plantado gira os pés para ficarem paralelos ao chão quando em contato.
settings-general-fk_settings-leg_fk = Tracking de pernas
settings-general-fk_settings-leg_fk-reset_mounting_feet-v1 = Forçar a calibração de montagem dos pés
settings-general-fk_settings-leg_fk-reset_mounting_feet-description-v1 = Forçar a calibração de montagem dos pés durante a calibração de montagem do corpo.
settings-general-fk_settings-enforce_joint_constraints = Limites esqueléticos
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints = Impor limites
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = Evita que as articulações rotacionem além de seu limite
settings-general-fk_settings-ik = Dados de posição
settings-general-fk_settings-ik-use_position = Usar dados de posição
settings-general-fk_settings-ik-use_position-description = Ativa o uso de dados de posição dos trackers que os fornecem. Ao ativar isso, certifique-se de executar "Redefinir Tudo" e recalibrar no jogo.
settings-general-fk_settings-arm_fk-back = Voltar
settings-general-fk_settings-arm_fk-back-description = O modo padrão, com os braços voltados para trás e os antebraços para frente.
settings-general-fk_settings-arm_fk-tpose_up = T-pose (cima)
settings-general-fk_settings-arm_fk-tpose_up-description = Espera que seus braços estejam abaixados ao lado do corpo durante "Redefinir Tudo" e levantados a 90 graus para os lados durante a "Calibração de Montagem".
settings-general-fk_settings-arm_fk-tpose_down = T-pose (baixo)
settings-general-fk_settings-arm_fk-tpose_down-description = Mantenha os braços levantados a 90 graus para os lados durante "Redefinir Tudo" e abaixados ao lado do corpo durante a "Calibração de Montagem".
settings-general-fk_settings-arm_fk-forward = Para frente
settings-general-fk_settings-arm_fk-forward-description = Espera que seus braços estejam levantados para frente a 90 graus. Útil para VTubing.
settings-general-fk_settings-skeleton_settings-ratios = Proporções do esqueleto
settings-general-fk_settings-skeleton_settings-ratios-description = Mude os valores das configurações do esqueleto. Pode ser necessário ajustar suas proporções depois de alterá-las.
settings-general-fk_settings-self_localization-title = Modo mocap

## Gesture control settings (tracker tapping)

settings-general-gesture_control-subtitle = Redefinições baseadas em toque
settings-general-gesture_control-description = Permite que redefinições sejam acionadas ao tocar em um tracker. O tracker mais alto no tronco é usado para "Redefinir direção", o tracker mais alto na perna esquerda é usado para executar "Redefinir Tudo", e o tracker mais alto na perna direita é usado para a "Calibração de Montagem". Os toques devem ocorrer dentro do limite de 0,3 segundos multiplicado pelo número de toques para serem reconhecidos.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
        [one] 1 toque
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
settings-general-gesture_control-yawResetEnabled = Ativar toque para executar "Redefinir Rápido (Guinada)"
settings-general-gesture_control-yawResetDelay = Atraso para executar "Redefinir Rápido (Guinada)"
settings-general-gesture_control-yawResetTaps = Toques para executar "Redefinir Rápido (Guinada)"
settings-general-gesture_control-fullResetEnabled = Ativar toque para executar "Redefinir Tudo"
settings-general-gesture_control-fullResetDelay = Atraso do "Redefinir Tudo"
settings-general-gesture_control-fullResetTaps = Toques para executar "Redefinir Tudo"
settings-general-gesture_control-mountingResetEnabled = Ativar toque para realizar a "Calibração de Montagem"
settings-general-gesture_control-mountingResetDelay = Atraso da calibração da montagem
settings-general-gesture_control-mountingResetTaps = Toques para calibração de montagem
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = Trackers acima do limite
settings-general-gesture_control-numberTrackersOverThreshold-description = Aumente esse valor se a detecção de toque não estiver funcionando. Não aumente o valor acima do necessário para que a detecção de toque funcione, pois isso causaria mais falsos positivos.

## Appearance settings

settings-interface-appearance = Aparência
settings-general-interface-dev_mode = Modo de desenvolvedor
settings-general-interface-dev_mode-description = Este modo pode ser útil se precisar de dados específicos ou para interagir com trackers conectados a um nível mais avançado
settings-general-interface-dev_mode-label = Modo de desenvolvedor
settings-general-interface-lang = Selecione o idioma
settings-general-interface-lang-description = Alterar o idioma padrão que pretende utilizar
settings-general-interface-lang-placeholder = Selecione o idioma que vai usar
# Keep the font name untranslated
settings-interface-appearance-font = Fonte da interface
settings-interface-appearance-font-description = Isso altera a fonte usada pela interface.
settings-interface-appearance-font-placeholder = Fonte padrão
settings-interface-appearance-font-os_font = Fonte do sistema
settings-interface-appearance-font-slime_font = Fonte padrão
settings-interface-appearance-font_size = Escala da fonte
settings-interface-appearance-font_size-description = Isso afeta o tamanho da fonte de toda a interface, exceto neste painel de configurações.

## Notification settings

settings-interface-notifications = Notificações
settings-general-interface-feedback_sound = Som de feedback
settings-general-interface-feedback_sound-description = Essa opção reproduzirá um som quando um reset for acionado.
settings-general-interface-feedback_sound-label = Som de feedback
settings-general-interface-feedback_sound-volume = Volume do som de feedback
settings-general-interface-connected_trackers_warning = Aviso de trackers conectados
settings-general-interface-connected_trackers_warning-description = Essa opção exibirá um pop-up toda vez que você tentar fechar o SlimeVR enquanto tiver um ou mais trackers conectados. Ela o lembrará de desligar os trackers quando você terminar para preservar a vida útil da bateria.
settings-general-interface-connected_trackers_warning-label = Aviso de trackers conectados ao fechar

## Behavior settings

settings-general-interface-dev_mode = Modo de desenvolvedor
settings-general-interface-dev_mode-label = Modo de desenvolvedor
settings-general-interface-use_tray = Minimizar para a bandeja do sistema
settings-general-interface-use_tray-description = Permite que você feche a janela sem fechar o servidor do SlimeVR, para que possa continuar usando-o sem que a interface gráfica o incomode.
settings-general-interface-use_tray-label = Minimizar para a bandeja do sistema
settings-general-interface-discord_presence = Compartilhar atividade no Discord
settings-general-interface-discord_presence-description = Informa ao seu Discord que o SlimeVR está aberto, juntamente com o número de trackers IMU que você está utilizando.
settings-general-interface-discord_presence-label = Compartilhar atividade no Discord
settings-general-interface-discord_presence-message =
    { $amount ->
        [0] Dando uma voltinha
        [one] Usando 1 tracker
       *[other] Usando { $amount } trackers
    }
settings-interface-behavior-error_tracking = Coleta de erros via Sentry.io
settings-interface-behavior-error_tracking-description_v2 =
    <h1>Você consente com a coleta de dados de erro anonimizados?</h1>
    
    <b>Não coletamos informações pessoais</b>, como seu endereço IP ou credenciais de rede sem fio. O SlimeVR valoriza sua privacidade!
    
    Para oferecer a melhor experiência possível, coletamos relatórios de erro anonimizados, métricas de desempenho e informações sobre o sistema operacional. Isso nos ajuda a identificar bugs e problemas no SlimeVR. Essas métricas são coletadas por meio do Sentry.io.
settings-interface-behavior-error_tracking-label = Enviar erros para os desenvolvedores
settings-interface-behavior-bvh_directory = Diretório para salvar gravações BVH
settings-interface-behavior-bvh_directory-description = Escolha um diretório para salvar suas gravações BVH, em vez de precisar escolher onde salvá-las a cada vez.
settings-interface-behavior-bvh_directory-label = Diretório para gravações BVH

## Serial settings

settings-serial-connection_lost = Conexão com o serial perdida, reconectando...
settings-serial-reboot = Reiniciar
settings-serial-factory_reset = Redefinição de Fábrica
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>Aviso:</b> Isso redefinirá o tracker para as configurações de fábrica.
    Isso significa que as configurações de Wi-Fi e de calibração <b>serão todas perdidas!</b>
settings-serial-factory_reset-warning-ok = Eu sei o que estou fazendo
settings-serial-factory_reset-warning-cancel = Cancelar
settings-serial-serial_select = Selecione uma porta serial
settings-serial-save_logs = Salvar em arquivo
settings-serial-send_command = Enviar
settings-serial-send_command-placeholder = Comando...
settings-serial-send_command-warning = <b>Aviso:</b> executar comandos seriais pode causar perda de dados ou inutilizar os trackers.
settings-serial-send_command-warning-ok = Eu sei o que estou fazendo
settings-serial-send_command-warning-cancel = Cancelar

## OSC VRChat settings

settings-osc-vrchat-enable = Ativar
settings-osc-vrchat-enable-description = Ative o envio e o recebimento de dados.
settings-osc-vrchat-enable-label = Ativar
settings-osc-vrchat-network = Portas de rede
settings-osc-vrchat-network-port_in =
    .label = Porta de Entrada
    .placeholder = Porta de entrada (padrão: 9001)
settings-osc-vrchat-network-port_out =
    .label = Porta de Saída
    .placeholder = Porta de saída (padrão: 9000)
settings-osc-vrchat-network-address = Endereço de rede
settings-osc-vrchat-network-address-description-v1 = Escolha o endereço para enviar os dados. Pode ser deixado como está para o VRChat.
settings-osc-vrchat-network-address-placeholder = Endereço de ip do VRChat

## VRChat OSC status

settings-osc-vrchat-status-tracking = Dados de rastreamento
settings-osc-vrchat-status-badge-error = Erro
settings-osc-vrchat-status-badge-unknown = Desconhecido

## VMC OSC settings

settings-osc-vmc = Captura virtual de movimentos
# This cares about multilines
settings-osc-vmc-description =
    Altere as configurações específicas do protocolo VMC (Virtual Motion Capture)
    para enviar dados de esqueleto do SlimeVR e receber dados de esqueleto de outros aplicativos.
settings-osc-vmc-enable = Ativar
settings-osc-vmc-enable-description = Ative o envio e o recebimento de dados.
settings-osc-vmc-enable-label = Ativar
settings-osc-vmc-network = Portas de rede
settings-osc-vmc-network-description = Defina as portas para escutar e enviar dados via VMC (Virtual Motion Capture).
settings-osc-vmc-network-port_in =
    .label = Porta de Entrada
    .placeholder = Porta de entrada (padrão: 39540)
settings-osc-vmc-network-port_out =
    .label = Porta de Saída
    .placeholder = Porta de saída (padrão: 39539)
settings-osc-vmc-network-address = Endereço de rede
settings-osc-vmc-network-address-description = Escolha o endereço para envio de dados via VMC (Virtual Motion Capture).
settings-osc-vmc-network-address-placeholder = Endereço IPV4
settings-osc-vmc-vrm = Modelo VRM
settings-osc-vmc-vrm-untitled_model = Modelo sem título
settings-osc-vmc-vrm-file_select = Arraste e solte um modelo para usar, ou <u>navegue</u>
settings-osc-vmc-anchor_hip = Ancorar no quadril
settings-osc-vmc-anchor_hip-label = Ancorar no quadril
settings-osc-vmc-mirror_tracking = Espelhar rastreamento
settings-osc-vmc-mirror_tracking-description = Espelhar o rastreamento horizontalmente.
settings-osc-vmc-mirror_tracking-label = Espelhar rastreamento
settings-osc-vmc-status-badge-error = Erro

## Common OSC settings

settings-osc-common-network-port_banned_error = A porta { $port } não pode ser usada!

## Advanced settings

settings-utils-advanced = Avançado
settings-utils-advanced-reset-gui = Redefinir configurações da interface
settings-utils-advanced-reset-gui-description = Restaurar as configurações padrão da interface.
settings-utils-advanced-reset-gui-label = Redefinir interface
settings-utils-advanced-reset-server = Redefinir configurações de rastreamento
settings-utils-advanced-reset-server-description = Restaura as configurações padrão do rastreamento.
settings-utils-advanced-reset-server-label = Redefinir rastreamento
settings-utils-advanced-reset-all = Resetar todas as configurações
settings-utils-advanced-reset-all-description = Restaura a configuração da interface e de tracking para as configurações iniciais.
settings-utils-advanced-reset-all-label = Redefinir todas as configurações
settings-utils-advanced-reset_warning =
    { $type ->
        [gui]
            <b>Aviso:</b> Isso irá restaurar as configurações da interface gráfica para as configurações iniciais.
            Tem certeza de que deseja fazer isso?
        [server]
            <b>Aviso:</b> Isso irá restaurar as configurações de tracking para as configurações iniciais.
            Tem certeza de que deseja fazer isso?
       *[all]
            <b>Aviso:</b> Isso irá restaurar todas as suas configurações para as configurações iniciais.
            Tem certeza de que deseja fazer isso?
    }
settings-utils-advanced-reset_warning-reset = Resetar configurações
settings-utils-advanced-reset_warning-cancel = Cancelar
settings-utils-advanced-open_data-v1 = Pasta de configuração
settings-utils-advanced-open_data-description-v1 = Abre a pasta de configuração do SlimeVR no explorador de arquivos, contendo as configurações
settings-utils-advanced-open_data-label = Abrir pasta
settings-utils-advanced-open_logs = Pasta de logs
settings-utils-advanced-open_logs-description = Abre a pasta de logs do SlimeVR no explorador de arquivos, contendo os logs do aplicativo
settings-utils-advanced-open_logs-label = Abrir pasta

## Home Screen

settings-home-list-layout = Layout da lista de trackers
settings-home-list-layout-desc = Selecione um dos layouts possíveis da tela inicial.
settings-home-list-layout-grid = Grade
settings-home-list-layout-table = Tabela

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

