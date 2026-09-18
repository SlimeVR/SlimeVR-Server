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

## Tracker reset buttons


## Navigation bar


## Biovision hierarchy recording


## Tracking pause


## Widget: Developer settings


## Widget: IMU Visualizer


## Tracker status


## Tracker status columns


## Tracker rotation


## Tracker information


## Tracker settings


## Dongle settings


## Tracker part card info


## Body assignment menu


## Tracker assignment menu


## Mounting menu


## Sidebar settings


## Bone routing settings


## SteamVR / Monado output settings


## Tracker mechanics


## Keybinds Page


## FK/Tracking settings


## Gesture control settings (tracker tapping)


## Appearance settings


## Notification settings


## Behavior settings


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

