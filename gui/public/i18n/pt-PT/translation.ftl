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

## Tracker reset buttons


## Serial detection stuff


## Navigation bar


## Biovision hierarchy recording


## Tracking pause


## Widget: Overlay settings


## Widget: Drift compensation


## Widget: Clear Mounting calibration


## Widget: Developer settings


## Widget: IMU Visualizer


## Widget: Skeleton Visualizer


## Tracker status


## Tracker status columns


## Tracker rotation


## Tracker information


## Tracker settings


## Tracker part card info


## Body assignment menu


## Tracker assignment menu


## Mounting menu


## Sidebar settings


## SteamVR settings


## Tracker mechanics


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

