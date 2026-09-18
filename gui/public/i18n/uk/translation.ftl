# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Підключення до сервера
websocket-connection_lost = З'єднання з сервером втрачено. Повторне підключення...
websocket-connection_lost-desc = It looks like the SlimeVR server crashed. Check the logs and restart the program.
websocket-timedout = Could not connect to the server
websocket-timedout-desc = It looks like the SlimeVR server crashed or timed out. Check the logs and restart the program.
websocket-error-close = Exit SlimeVR
websocket-error-logs = Open the logs Folder

## Update notification

version_update-title = Доступна нова версія: { $version }
version_update-description = Натискання "{ version_update-update }" почнеться завантаження SlimeVR installer.
version_update-update = Оновлення
version_update-close = Закрити

## Tips

tips-find_tracker = Не знаєте який трекер вибирати? Потрясіть трекер і він підсвітить відповідний пункт.
tips-do_not_move_heels = Переконайтеся, що не рухаєте п'ятами під час запису!
tips-file_select = Перетягніть файли для використання або <u>знайдіть</u>.
tips-tap_setup = Ви можете повільно постукати 2 рази по трекеру, щоб вибрати його, замість того, щоб вибирати його з меню.
tips-turn_on_tracker = Використовуєте офіційні трекери SlimeVR? Не забудьте <b><em>увімкнути трекер</em></b> після підключення до ПК!
tips-failed_webgl = Не вдалося ініціалізувати WebGL.

## Units

unit-meter = Meter
unit-foot = Foot
unit-inch = Inch
unit-cm = cm

## Body parts

body_part-NONE = Не призначено
body_part-HEAD = Голова
body_part-NECK = Шия
body_part-RIGHT_SHOULDER = Праве плече
body_part-RIGHT_UPPER_ARM = Права верхня частина руки
body_part-RIGHT_LOWER_ARM = Права нижня частина руки
body_part-RIGHT_HAND = Права рука
body_part-RIGHT_UPPER_LEG = Праве стегно
body_part-RIGHT_LOWER_LEG = Права щиколотка
body_part-RIGHT_FOOT = Права нога
body_part-UPPER_CHEST = Верхня частина грудей
body_part-CHEST = Груди
body_part-WAIST = Талія
body_part-HIP = Стегно
body_part-LEFT_SHOULDER = Ліве плече
body_part-LEFT_UPPER_ARM = Ліва верхня частина руки
body_part-LEFT_LOWER_ARM = Ліва нижня частина руки
body_part-LEFT_HAND = Ліва рука
body_part-LEFT_UPPER_LEG = Ліве стегно
body_part-LEFT_LOWER_LEG = Ліва щиколотка
body_part-LEFT_FOOT = Ліва нога
body_part-LEFT_THUMB_METACARPAL = Left thumb metacarpal
body_part-LEFT_THUMB_PROXIMAL = Left thumb proximal
body_part-LEFT_THUMB_DISTAL = Left thumb distal
body_part-LEFT_INDEX_PROXIMAL = Left index proximal
body_part-LEFT_INDEX_INTERMEDIATE = Left index intermediate
body_part-LEFT_INDEX_DISTAL = Left index distal
body_part-LEFT_MIDDLE_PROXIMAL = Left middle proximal
body_part-LEFT_MIDDLE_INTERMEDIATE = Left middle intermediate
body_part-LEFT_MIDDLE_DISTAL = Left middle distal
body_part-LEFT_RING_PROXIMAL = Left ring proximal
body_part-LEFT_RING_INTERMEDIATE = Left ring intermediate
body_part-LEFT_RING_DISTAL = Left ring distal
body_part-LEFT_LITTLE_PROXIMAL = Left little proximal
body_part-LEFT_LITTLE_INTERMEDIATE = Left little intermediate
body_part-LEFT_LITTLE_DISTAL = Left little distal
body_part-RIGHT_THUMB_METACARPAL = Right thumb metacarpal
body_part-RIGHT_THUMB_PROXIMAL = Right thumb proximal
body_part-RIGHT_THUMB_DISTAL = Right thumb distal
body_part-RIGHT_INDEX_PROXIMAL = Right index proximal
body_part-RIGHT_INDEX_INTERMEDIATE = Right index intermediate
body_part-RIGHT_INDEX_DISTAL = Right index distal
body_part-RIGHT_MIDDLE_PROXIMAL = Right middle proximal
body_part-RIGHT_MIDDLE_INTERMEDIATE = Right middle intermediate
body_part-RIGHT_MIDDLE_DISTAL = Right middle distal
body_part-RIGHT_RING_PROXIMAL = Right ring proximal
body_part-RIGHT_RING_INTERMEDIATE = Right ring intermediate
body_part-RIGHT_RING_DISTAL = Right ring distal
body_part-RIGHT_LITTLE_PROXIMAL = Right little proximal
body_part-RIGHT_LITTLE_INTERMEDIATE = Right little intermediate
body_part-RIGHT_LITTLE_DISTAL = Right little distal

## BoardType

board_type-UNKNOWN = Unknown
board_type-NODEMCU = NodeMCU
board_type-CUSTOM = Custom Board
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
board_type-GLOVE_IMU_SLIMEVR_DEV = SlimeVR Dev IMU Glove
board_type-GESTURES = Gestures
board_type-ESP32S3_SUPERMINI = ESP32-S3 Supermini
board_type-GENERIC_NRF = Generic nRF
board_type-SLIMEVR_BUTTERFLY_DEV = SlimeVR Dev Butterfly
board_type-SLIMEVR_BUTTERFLY = SlimeVR Butterfly

## Proportions

skeleton_bone-NONE = Нічого
skeleton_bone-HEAD = Зсув голови
skeleton_bone-HEAD-desc =
    This is the distance from your headset to the middle of your head.
    To adjust it, shake your head left to right as if you're disagreeing and modify
    it until any movement in other trackers is negligible.
skeleton_bone-NECK = Довжина шиї
skeleton_bone-NECK-desc =
    This is the distance from the middle of your head to the base of your neck.
    To adjust it, move your head up and down as if you're nodding or tilt your head
    to the left and right and modify it until any movement in other trackers is negligible.
skeleton_bone-torso_group = Довжина тулуба
skeleton_bone-torso_group-desc =
    This is the distance from the base of your neck to your hips.
    To adjust it, modify it standing up straight until your virtual hips line
    up with your real ones.
skeleton_bone-UPPER_CHEST = Довжина верхньої частини грудей
skeleton_bone-UPPER_CHEST-desc =
    This is the distance from the base of your neck to the middle of your chest.
    To adjust it, adjust your Torso Length properly and modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches with your real one.
skeleton_bone-CHEST_OFFSET = Зміщення грудної клітини
skeleton_bone-CHEST_OFFSET-desc =
    This can be adjusted to move your virtual chest tracker up or down in order to aid
    with calibration in certain games or applications that may expect it to be higher or lower.
skeleton_bone-CHEST = Довжина грудей
skeleton_bone-CHEST-desc =
    This is the distance from the middle of your chest to the middle of your spine.
    To adjust it, adjust your Torso Length properly and modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches with your real one.
skeleton_bone-WAIST = Довжина талії
skeleton_bone-WAIST-desc =
    This is the distance from the middle of your spine to your belly button.
    To adjust it, adjust your Torso Length properly and modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches with your real one.
skeleton_bone-HIP = Довжина стегна
skeleton_bone-HIP-desc =
    This is the distance from your belly button to your hips.
    To adjust it, set your Torso Length properly and modify it in various positions
    (sitting down, bending over, lying down, etc.) until your virtual spine matches your real one.
skeleton_bone-HIP_OFFSET = Зміщення стегна
skeleton_bone-HIP_OFFSET-desc =
    This can be adjusted to move your virtual hip tracker up or down in order to aid
    with calibration in certain games or applications that may expect it to be on your waist.
skeleton_bone-HIPS_WIDTH = Ширина стегон
skeleton_bone-HIPS_WIDTH-desc =
    This is the distance between the start of your legs.
    To adjust it, perform a full reset with your legs straight and modify it until
    your virtual legs match up with your real ones horizontally.
skeleton_bone-leg_group = Довжина ніг
skeleton_bone-leg_group-desc =
    This is the distance from your hips to your feet.
    To adjust it, adjust your Torso Length properly and modify it
    until your virtual feet are at the same level as your real ones.
skeleton_bone-UPPER_LEG = Довжина верхньої частини ноги
skeleton_bone-UPPER_LEG-desc =
    This is the distance from your hips to your knees.
    To adjust it, adjust your Leg Length properly and modify it
    until your virtual knees are at the same level as your real ones.
skeleton_bone-LOWER_LEG = Довжина гомілки
skeleton_bone-LOWER_LEG-desc =
    This is the distance from your knees to your ankles.
    To adjust it, adjust your Leg Length properly and modify it
    until your virtual knees are at the same level as your real ones.
skeleton_bone-FOOT_LENGTH = Довжина стопи
skeleton_bone-FOOT_LENGTH-desc =
    This is the distance from your ankles to your toes.
    To adjust it, tiptoe and modify it until your virtual feet stay in place.
skeleton_bone-FOOT_SHIFT = Зміщення стопи
skeleton_bone-FOOT_SHIFT-desc =
    This value is the horizontal distance from your knee to your ankle.
    It accounts for your lower legs going backwards when standing up straight.
    To adjust it, set Foot Length to 0, perform a full reset and modify it until your virtual
    feet line up with the middle of your ankles.
skeleton_bone-SKELETON_OFFSET = Зміщення скелета
skeleton_bone-SKELETON_OFFSET-desc =
    This can be adjusted to offset all your trackers forward or backward.
    It can be used to help with calibration in certain games or applications
    that may expect your trackers to be more forward.
skeleton_bone-SHOULDERS_DISTANCE = Відстань між плечима
skeleton_bone-SHOULDERS_DISTANCE-desc =
    This is the vertical distance from the base of your neck to your shoulders.
    To adjust it, set Upper Arm Length to 0 and modify it until your virtual elbow trackers
    line up vertically with your real shoulders.
skeleton_bone-SHOULDERS_WIDTH = Ширина плечей
skeleton_bone-SHOULDERS_WIDTH-desc =
    This is the horizontal distance from the base of your neck to your shoulders.
    To adjust it, set Upper Arm Length to 0 and modify it until your virtual elbow trackers
    line up horizontally with your real shoulders.
skeleton_bone-arm_group = Довжина руки
skeleton_bone-arm_group-desc =
    This is the distance from your shoulders to your wrists.
    To adjust it, adjust Shoulders Distance properly, set Hand Distance Y
    to 0 and modify it until your hand trackers line up with your wrists.
skeleton_bone-UPPER_ARM = Довжина верхньої частини руки
skeleton_bone-UPPER_ARM-desc =
    This is the distance from your shoulders to your elbows.
    To adjust it, adjust Arm Length properly and modify it until
    your elbow trackers line up with your real elbows.
skeleton_bone-LOWER_ARM = Довжина нижньої частини руки
skeleton_bone-LOWER_ARM-desc =
    This is the distance from your elbows to your wrists.
    To adjust it, adjust Arm Length properly and modify it until
    your elbow trackers line up with your real elbows.
skeleton_bone-HAND_Y = Відстань рук Y
skeleton_bone-HAND_Y-desc =
    This is the vertical distance from your wrists to the middle of your hand.
    To adjust it for motion capture, adjust Arm Length properly and modify it until your
    hand trackers line up vertically with the middle of your hands.
    To adjust it for elbow tracking from your controllers, set Arm Length to 0 and
    modify it until your elbow trackers line up vertically with your wrists.
skeleton_bone-HAND_Z = Відстань руки Z
skeleton_bone-HAND_Z-desc =
    This is the horizontal distance from your wrists to the middle of your hand.
    To adjust it for motion capture, set it to 0.
    To adjust it for elbow tracking from your controllers, set Arm Length to 0 and
    modify it until your elbow trackers line up horizontally with your wrists.
skeleton_bone-ELBOW_OFFSET = Зміщення ліктя
skeleton_bone-ELBOW_OFFSET-desc =
    This can be adjusted to move your virtual elbow trackers up or down in order to aid
    with VRChat accidentally binding an elbow tracker to the chest.

## Tracker reset buttons

reset-reset_all = Скинути всі пропорції
reset-reset_all_warning-v2 =
    <b>Warning:</b> Your proportions will be reset to defaults scaled to your configured height.
    Are you sure you want to do this?
reset-reset_all_warning-reset = Reset proportions
reset-reset_all_warning-cancel = Cancel
reset-reset_all_warning_default-v2 =
    <b>Warning:</b> Your height has not been configured, your proportions will be reset to defaults with the default height.
    Are you sure you want to do this?
reset-full = Повне скидання
reset-mounting = Скинути положення
reset-mounting-feet = Feet Calibration
reset-mounting-fingers = Fingers Calibration
reset-yaw = Скинути нахил
reset-error-no_feet_tracker = No feet tracker assigned
reset-error-no_fingers_tracker = No finger tracker assigned
reset-error-mounting-need_full_reset = Need a full reset before mounting
reset-error-yaw-need_full_reset = Need a full reset before yaw reset

## Serial detection stuff

serial_detection-new_device-p0 = Виявлено новий послідовний пристрій!
serial_detection-new_device-p1 = Введіть дані вашого Wi-Fi!
serial_detection-new_device-p2 = Будь ласка, виберіть, що ви хочете з ним зробити
serial_detection-open_wifi = Підключити до Wi-Fi
serial_detection-open_serial = Відкрити послідовну консоль
serial_detection-submit = Підтвердити!
serial_detection-close = Закрити

## Navigation bar

navbar-home = Домашня сторінка
navbar-body_proportions = Пропорції тіла
navbar-trackers_assign = Призначення трекера
navbar-mounting = Калібрування положення
navbar-onboarding = Майстер налаштування
navbar-settings = Параметри
navbar-connect_trackers = Connect Trackers

## Biovision hierarchy recording

bvh-start_recording = Запис BVH
bvh-stop_recording = Save BVH recording
bvh-recording = Запис...
bvh-save_title = Save BVH recording

## Tracking pause

tracking-unpaused = Призупинити трекінг
tracking-paused = Продовжити трекінг

## Widget: Overlay settings

widget-overlay = Накладання
widget-overlay-is_visible_label = Показати накладання у SteamVR
widget-overlay-is_mirrored_label = Відображення накладання як дзеркала

## Widget: Drift compensation

widget-drift_compensation-clear = Очистити компенсацію дрейфу

## Widget: Clear Mounting calibration

widget-clear_mounting = Очистити скидання положення

## Widget: Developer settings

widget-developer_mode = Режим розробника
widget-developer_mode-high_contrast = Висока контрастність
widget-developer_mode-precise_rotation = Точне обертання
widget-developer_mode-fast_data_feed = Швидка подача даних
widget-developer_mode-filter_slimes_and_hmd = Фільтрація слаймів і шолому
widget-developer_mode-sort_by_name = Сортування за назвою
widget-developer_mode-raw_slime_rotation = Необроблене обертання
widget-developer_mode-more_info = Детальніше

## Widget: IMU Visualizer

widget-imu_visualizer = Обертання
widget-imu_visualizer-preview = Preview
widget-imu_visualizer-hide = Hide
widget-imu_visualizer-rotation_raw = Необроблене
widget-imu_visualizer-rotation_preview = Попередній перегляд
widget-imu_visualizer-acceleration = Acceleration
widget-imu_visualizer-position = Position
widget-imu_visualizer-stay_aligned = Stay Aligned

## Widget: Skeleton Visualizer

widget-skeleton_visualizer-preview = Перегляд скелета
widget-skeleton_visualizer-hide = Сховати

## Tracker status

tracker-status-none = Немає статусу
tracker-status-busy = Зайнятий
tracker-status-error = Помилка
tracker-status-disconnected = Відключено
tracker-status-occluded = Закрито
tracker-status-ok = OK
tracker-status-timed_out = Минув час очікування

## Tracker status columns

tracker-table-column-name = Ім'я
tracker-table-column-type = Тип
tracker-table-column-battery = Батарея
tracker-table-column-ping = Пінг
tracker-table-column-packet_loss = Packet Loss
tracker-table-column-tps = TPS
tracker-table-column-temperature = Темп. °C
tracker-table-column-linear-acceleration = Прискорення X/Y/Z
tracker-table-column-rotation = Обертання X/Y/Z
tracker-table-column-position = Позиція X/Y/Z
tracker-table-column-stay_aligned = Stay Aligned
tracker-table-column-url = URL

## Tracker rotation

tracker-rotation-front = Спереду
tracker-rotation-front_left = Ліва сторона передньої частини
tracker-rotation-front_right = Права сторона передньої частини
tracker-rotation-left = Зліва
tracker-rotation-right = Справа
tracker-rotation-back = Ззаду
tracker-rotation-back_left = Ліва сторона задньої частини
tracker-rotation-back_right = Права сторона задньої частини
tracker-rotation-custom = Персональне
tracker-rotation-overriden = (замінено скиданням положення)

## Tracker information

tracker-infos-manufacturer = Виробник
tracker-infos-display_name = Відображуване ім'я
tracker-infos-custom_name = Персональне ім'я
tracker-infos-url = URL трекера
tracker-infos-version = Версія прошивки
tracker-infos-hardware_rev = Ревізія обладнання
tracker-infos-hardware_identifier = Ідентифікатор обладнання
tracker-infos-data_support = Data support
tracker-infos-imu = IMU Сенсор
tracker-infos-board_type = Основна плата
tracker-infos-network_version = Версія протоколу
tracker-infos-magnetometer = Magnetometer
tracker-infos-magnetometer-status-v1 =
    { $status ->
       *[NOT_SUPPORTED] Not supported
        [DISABLED] Disabled
        [ENABLED] Enabled
    }
tracker-infos-packet_loss = Packet Loss
tracker-infos-packets_lost = Packets Lost
tracker-infos-packets_received = Packets Received

## Tracker settings

tracker-settings-back = Повернутися до списку трекерів
tracker-settings-title = Налаштування трекеру
tracker-settings-assignment_section = Призначення
tracker-settings-assignment_section-description = До якої частини тіла призначенний трекер.
tracker-settings-assignment_section-edit = Редагування призначення
tracker-settings-mounting_section = Позиція трекера
tracker-settings-mounting_section-description = Де закріплено трекер?
tracker-settings-mounting_section-edit = Змінити місце розташування
tracker-settings-drift_compensation_section = Дозволити компенсацію дрейфу
tracker-settings-drift_compensation_section-description = Чи повинен цей трекер компенсувати свій дрейф, коли включена компенсація дрейфу?
tracker-settings-drift_compensation_section-edit = Дозволити компенсацію дрейфу
tracker-settings-use_mag = Allow magnetometer on this tracker
# Multiline!
tracker-settings-use_mag-description =
    Should this tracker use magnetometer to reduce drift when magnetometer usage is allowed? <b>Please don't shutdown your tracker while toggling this!</b>
    
    You need to allow magnetometer usage first, <magSetting>click here to go to the setting</magSetting>.
tracker-settings-use_mag-label = Allow magnetometer
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Ім'я трекера
tracker-settings-name_section-description = Дайте йому миле прізвисько °^°
tracker-settings-name_section-placeholder = Ліва нога NightyBeast
tracker-settings-name_section-label = Tracker name
tracker-settings-forget = Забути трекери
tracker-settings-forget-description = Прибирає трекер із SlimeVR і забороняє йому підключатися до сервера до того, як він буде перезапущений. Конфігурацію трекера не буде втрачено.
tracker-settings-forget-label = Забути трекери
tracker-settings-update-unavailable-v2 = No releases found
tracker-settings-update-incompatible = Cannot update. Incompatible board or firmware version
tracker-settings-update-low-battery = Cannot update. Battery lower than 50%
tracker-settings-update-up_to_date = Up to date
tracker-settings-update-blocked = Update not available. No other releases available
tracker-settings-update = Update now
tracker-settings-update-title = Firmware version
tracker-settings-current-version = Current
tracker-settings-latest-version = Latest
tracker-settings-build-date = Build Date

## Tracker part card info

tracker-part_card-no_name = Немає імені
tracker-part_card-unassigned = Непризначений

## Body assignment menu

body_assignment_menu = Де ви хочете, щоб був цей трекер?
body_assignment_menu-description = Виберіть місце, куди потрібно призначити цей трекер. Крім того, ви можете керувати всіма трекерами одночасно, а не по одному.
body_assignment_menu-show_advanced_locations = Відображення розширених точок розташувань
body_assignment_menu-manage_trackers = Керування всіма трекерами
body_assignment_menu-unassign_tracker = Відв'язати трекер

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = Який трекер призначити к
tracker_selection_menu-NONE = Який трекер ви хочете відв'язати?
tracker_selection_menu-HEAD = { -tracker_selection-part } голові?
tracker_selection_menu-NECK = { -tracker_selection-part } шиї?
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part } правому плечу?
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part } правій верхній частині руці?
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part } правій нижній частині руці?
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part } правій руці?
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part } правому стегну?
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part } правій щиколотці?
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part } правій ступні?
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part } правому контролеру?
tracker_selection_menu-UPPER_CHEST = { -tracker_selection-part } верхня частина грудей?
tracker_selection_menu-CHEST = { -tracker_selection-part } грудям?
tracker_selection_menu-WAIST = { -tracker_selection-part } талії?
tracker_selection_menu-HIP = { -tracker_selection-part } стегну?
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part } Лівому плечу?
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part } лівій верхній частині руки?
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part } лівій нижній частині руці?
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part } лівій руці?
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part } лівому стегну?
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part } лівій щиколотці
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part } лівій ступні?
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part } лівому контролеру?
tracker_selection_menu-unassigned = Непризначені трекери
tracker_selection_menu-assigned = Призначені трекери
tracker_selection_menu-dont_assign = Відв'язати
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Попередження:</b> Трекер шиї може бути смертельно небезпечним, якщо його регулювати занадто щільно,
    Ремінь може скоротити кровообіг до вашої голови!
tracker_selection_menu-neck_warning-done = Я розумію ризики
tracker_selection_menu-neck_warning-cancel = Скасувати

## Mounting menu

mounting_selection_menu = Де ви хочете розташувати цей трекер?
mounting_selection_menu-close = Закрити

## Sidebar settings

settings-sidebar-title = Параметри
settings-sidebar-general = Загальні
settings-sidebar-steamvr = SteamVR
settings-sidebar-tracker_mechanics = Механіки трекера
settings-sidebar-stay_aligned = Stay Aligned
settings-sidebar-fk_settings = Налаштування відстеження
settings-sidebar-gesture_control = Управління жестами
settings-sidebar-interface = Інтерфейс
settings-sidebar-osc_router = OSC роутер
settings-sidebar-osc_trackers = VRChat OSC трекери
settings-sidebar-osc_vmc = VMC
settings-sidebar-utils = Утиліти
settings-sidebar-serial = Послідовна консоль
settings-sidebar-appearance = Зовнішність
settings-sidebar-home = Home Screen
settings-sidebar-checklist = Tracking checklist
settings-sidebar-notifications = Сповіщення
settings-sidebar-behavior = Behavior
settings-sidebar-firmware-tool = DIY Firmware Tool
settings-sidebar-vrc_warnings = VRChat Config Warnings
settings-sidebar-advanced = Advanced

## SteamVR settings

settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = SteamVR трекери
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    Увімкніть або вимкніть певні SteamVR трекери.
    Корисно для ігор або програм, які підтримують лише певні трекери.
settings-general-steamvr-trackers-waist = Талія
settings-general-steamvr-trackers-chest = Груди
settings-general-steamvr-trackers-left_foot = Ліва ступня
settings-general-steamvr-trackers-right_foot = Права ступня
settings-general-steamvr-trackers-left_knee = Ліве коліно
settings-general-steamvr-trackers-right_knee = Праве коліно
settings-general-steamvr-trackers-left_elbow = Лівий лікоть
settings-general-steamvr-trackers-right_elbow = Правий лікоть
settings-general-steamvr-trackers-left_hand = Ліва рука
settings-general-steamvr-trackers-right_hand = Права рука
settings-general-steamvr-trackers-tracker_toggling = Автоматичне призначення трекерів
settings-general-steamvr-trackers-tracker_toggling-description = Автоматично займається увімкненням та вимкненням трекерів SlimeVR залежно від поточних призначень ваших трекерів
settings-general-steamvr-trackers-tracker_toggling-label = Автоматичне призначення трекерів
settings-general-steamvr-trackers-hands-warning =
    <b>Увага:</b> трекери рук перевизначать ваші контролери.
    Ви впевнені?
settings-general-steamvr-trackers-hands-warning-cancel = Скасувати
settings-general-steamvr-trackers-hands-warning-done = Так

## Tracker mechanics

settings-general-tracker_mechanics = Механіки трекера
settings-general-tracker_mechanics-filtering = Фільтрація
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Виберіть тип фільтрації для своїх трекерів.
    Передбачення передбачає рух, а згладжування згладжує рух.
settings-general-tracker_mechanics-filtering-type = Тип фільтрації
settings-general-tracker_mechanics-filtering-type-none = Без фільтрації
settings-general-tracker_mechanics-filtering-type-none-description = Використовуйте обертання як є. Ніякої фільтрації не зробить.
settings-general-tracker_mechanics-filtering-type-smoothing = Згладжування
settings-general-tracker_mechanics-filtering-type-smoothing-description = Згладжує рухи, але додає деяку затримку.
settings-general-tracker_mechanics-filtering-type-prediction = Передбачення
settings-general-tracker_mechanics-filtering-type-prediction-description = Зменшує затримку і робить рухи більш швидкими, але може посилити тремтіння.
settings-general-tracker_mechanics-filtering-amount = Кількість
settings-general-tracker_mechanics-yaw-reset-smooth-time = Час згладжування скидання рискання (0 сек. відключає згладжування)
settings-general-tracker_mechanics-drift_compensation = Компенсація дрейфу
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Компенсує дрейф нахилу IMU, застосовуючи зворотне обертання.
    Змініть суму компенсації та до того, скільки скидань враховано.
settings-general-tracker_mechanics-drift_compensation-enabled-label = Компенсація дрейфу
settings-general-tracker_mechanics-drift_compensation-prediction = Drift compensation prediction
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-prediction-description =
    Predicts yaw drift compensation beyond previously measured range.
    Enable this if your trackers are continuously spinning on the yaw axis.
settings-general-tracker_mechanics-drift_compensation-prediction-label = Drift compensation prediction
settings-general-tracker_mechanics-drift_compensation_warning =
    <b>Warning:</b> Only use drift compensation if you need to reset
    very often (every ~5-10 minutes).
    
    Some IMUs prone to frequent resets include:
    Joy-Cons, owoTrack, and MPUs (without recent firmware).
settings-general-tracker_mechanics-drift_compensation_warning-cancel = Cancel
settings-general-tracker_mechanics-drift_compensation_warning-done = I understand
settings-general-tracker_mechanics-drift_compensation-amount-label = Сума компенсації
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Використання до x останніх скидань
settings-general-tracker_mechanics-save_mounting_reset = Зберегти автоматичне калібрування положення трекерів
settings-general-tracker_mechanics-save_mounting_reset-description =
    Зберігає калібрування положення трекерів на тілі між перезавантаженнями. Корисний
    при носінні костюма, в якому трекери не переміщаються між сесіями. <b>Не рекомендується для звичайних користувачів!</b>
settings-general-tracker_mechanics-save_mounting_reset-enabled-label = Зберегти калібрування положення
settings-general-tracker_mechanics-use_mag_on_all_trackers = Use magnetometer on all IMU trackers that support it
settings-general-tracker_mechanics-use_mag_on_all_trackers-description =
    Uses magnetometer on all trackers that have a compatible firmware for it, reducing drift in stable magnetic environments.
    Can be disabled per tracker in the tracker's settings. <b>Please don't shutdown any of the trackers while toggling this!</b>
settings-general-tracker_mechanics-use_mag_on_all_trackers-label = Use magnetometer on trackers
settings-general-tracker_mechanics-trackers_over_usb = Trackers over USB
settings-general-tracker_mechanics-trackers_over_usb-description = Enables receiving HID tracker data over USB. Make sure connected trackers have <b>connection over HID</b> enabled!
settings-general-tracker_mechanics-trackers_over_usb-enabled-label = Allow HID trackers to connect directly over USB
settings-stay_aligned = Stay Aligned
settings-stay_aligned-description = Stay Aligned reduces drift by gradually adjusting your trackers to match your relaxed poses.
settings-stay_aligned-setup-label = Setup Stay Aligned
settings-stay_aligned-setup-description = You must complete "Setup Stay Aligned" to enable Stay Aligned.
settings-stay_aligned-warnings-drift_compensation = ⚠ Please turn off Drift Compensation! Drift Compensation will conflict with Stay Aligned.
settings-stay_aligned-enabled-label = Adjust trackers
settings-stay_aligned-hide_yaw_correction-label = Hide adjustment (to compare with no Stay Aligned)
settings-stay_aligned-general-label = General
settings-stay_aligned-relaxed_poses-label = Relaxed Poses
settings-stay_aligned-relaxed_poses-description = Stay Aligned uses your relaxed poses to keep the trackers aligned. Use "Setup Stay Aligned" to update these poses.
settings-stay_aligned-relaxed_poses-standing = Adjust trackers while standing
settings-stay_aligned-relaxed_poses-sitting = Adjust trackers while sitting in a chair
settings-stay_aligned-relaxed_poses-flat = Adjust trackers while sitting on the floor, or lying on your back
settings-stay_aligned-relaxed_poses-save_pose = Save pose
settings-stay_aligned-relaxed_poses-reset_pose = Reset pose
settings-stay_aligned-relaxed_poses-close = Close
settings-stay_aligned-debug-label = Debugging
settings-stay_aligned-debug-description = Please include your settings when reporting problems about Stay Aligned.
settings-stay_aligned-debug-copy-label = Copy settings to clipboard

## FK/Tracking settings

settings-general-fk_settings = Налаштування відстеження
# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Прив'язка до підлоги
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = корекція ковзання
settings-general-fk_settings-leg_tweak-toe_snap = корекція пальців ноги
settings-general-fk_settings-leg_tweak-foot_plant = корекція ступні
settings-general-fk_settings-leg_tweak-skating_correction-amount = Сила корекції ковзання
settings-general-fk_settings-leg_tweak-skating_correction-description = Корекція ковзання коригує катання на ковзанах, але може знизити точність певних моделей руху. Увімкнувши це, обов'язково повністю скиньте та відкалібруйте у грі.
settings-general-fk_settings-leg_tweak-floor_clip-description = Прив'язка до підлоги може зменшити або навіть прибрати проходження через підлогу. Коли вмикаєте, обов'язково зробіть повне скидання і перекалібровку у грі
settings-general-fk_settings-leg_tweak-toe_snap-description = Корекція пальців ноги намагається вгадати обертання ваших ступень, якщо трекери для них не використовуються
settings-general-fk_settings-leg_tweak-foot_plant-description = Корекція ступні повертає ваші ступні так, щоб вони були паралельні землі при контакті
settings-general-fk_settings-leg_fk = Трекінг ноги
settings-general-fk_settings-leg_fk-reset_mounting_feet-description-v1 = Force feet mounting calibration during body mounting calibration.
settings-general-fk_settings-leg_fk-reset_mounting_feet-v1 = Force feet mounting calibration
settings-general-fk_settings-enforce_joint_constraints = Skeletal Limits
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints = Enforce constraints
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = Prevents joints from rotating past their limit
settings-general-fk_settings-enforce_joint_constraints-correct_constraints = Correct with constraints
settings-general-fk_settings-enforce_joint_constraints-correct_constraints-description = Correct joint rotations when they push past their limit
settings-general-fk_settings-ik = Position data
settings-general-fk_settings-ik-use_position = Use Position data
settings-general-fk_settings-ik-use_position-description = Enables the use of position data from trackers that provide it. When enabling this make sure to full reset and recalibrate in game.
settings-general-fk_settings-velocity_settings = Velocity Settings
settings-general-fk_settings-velocity_settings-description = Send derived velocity data to SteamVR. Required for Natural Locomotion support. May cause jitter in FBT.
settings-general-fk_settings-velocity_settings-send_derived_velocity = Send derived velocity to driver
settings-general-fk_settings-arm_fk = Трекінг руки
settings-general-fk_settings-arm_fk-description = Намагатися відстежувати руки за допомогою шолома, навіть якщо є інформація о позиції руки
settings-general-fk_settings-arm_fk-force_arms = Відстеження рук з шолома
settings-general-fk_settings-reset_settings = Скинути налаштування
settings-general-fk_settings-reset_settings-reset_hmd_pitch-description = Скидає тангаж шолому (вертикальне обертання). Корисно, якщо носити шолом на лобі для вітюбінга або мокап. Не вмикати для VR.
settings-general-fk_settings-reset_settings-reset_hmd_pitch = Скинути тангаж шолому
settings-general-fk_settings-arm_fk-reset_mode-description = Змініть позу руки, яка очікується для скидання налаштувань положення.
settings-general-fk_settings-arm_fk-back = Назад
settings-general-fk_settings-arm_fk-back-description = Стандартний режим: плечі ззаду, а передпліччя спереду.
settings-general-fk_settings-arm_fk-tpose_up = Т-поза (вгору)
settings-general-fk_settings-arm_fk-tpose_up-description = Очікується, що ваші руки будуть опущені з боків під час повного скидання, та на 90 градусів у сторони під час скидання положення.
settings-general-fk_settings-arm_fk-tpose_down = Т-поза (вниз)
settings-general-fk_settings-arm_fk-tpose_down-description = Очікується, що ваші руки будуть піднятими в сторони на 90 градусів під час повного скидання, та опущені з боків під час скидання положення.
settings-general-fk_settings-arm_fk-forward = Вперед
settings-general-fk_settings-arm_fk-forward-description = Очікується, що ваші руки будуть підняті вперед на 90 градусів. Корисно для вітюбінга.
settings-general-fk_settings-skeleton_settings-toggles = Перемикачі скелета
settings-general-fk_settings-skeleton_settings-description = Увімкніть або вимкніть налаштування скелета. Рекомендується залишити їх увімкненими.
settings-general-fk_settings-skeleton_settings-extended_spine_model = Подовжена модель хребта
settings-general-fk_settings-skeleton_settings-extended_pelvis_model = Розширена модель тазу
settings-general-fk_settings-skeleton_settings-extended_knees_model = Подовжена модель коліна
settings-general-fk_settings-skeleton_settings-ratios = Співвідношення скелета
settings-general-fk_settings-skeleton_settings-ratios-description = Змініть параметри скелета. Можливо, вам доведеться скоригувати пропорції після їхньої зміни.
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_hip = Розрахувати талію від грудей до стегон
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_legs = Розрахувати талію від грудей до ніг
settings-general-fk_settings-skeleton_settings-impute_hip_from_chest_legs = Розрахувати стегно від грудей до ніг
settings-general-fk_settings-skeleton_settings-impute_hip_from_waist_legs = Розрахувати стегно від талії до ніг
settings-general-fk_settings-skeleton_settings-interp_hip_legs = Інтерполяція рискання та крену стегн з ногами
settings-general-fk_settings-skeleton_settings-interp_knee_tracker_ankle = Інтерполяція рискання та крену трекерів на колінах та щиколотках
settings-general-fk_settings-skeleton_settings-interp_knee_ankle = Інтерполяція рискання та крену колін з щиколотками
settings-general-fk_settings-self_localization-title = Режим Мокап
settings-general-fk_settings-self_localization-description = Режим Мокап дозволяє скелету приблизно відстежувати власне положення без використання шолому або інших трекерів. Зверніть увагу, що для цього потрібні трекери ніг і голови, і це все ще експериментальний метод.

## Gesture control settings (tracker tapping)

settings-general-gesture_control = Управління жестами
settings-general-gesture_control-subtitle = Скидання на основі дотику
settings-general-gesture_control-description = Дозволяє запускати скидання, торкнувшись трекера. Трекер найвищий на вашому тулубі використовується для скидання рискання, трекер найвищий на лівій нозі використовується для повного скидання, а трекер найвищий на правій нозі використовується для скидання положення. Слід зазначити, що для реєстрації дотики мають тривати протягом 0,3 секунди.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
        [one] 1 дотик
        [few] 2 дотика
        [many] { $amount } дотиків
       *[other] { $amount } дотиків
    }
# This is a unit: 3 trackers, 2 trackers, 1 tracker
# $amount (Number) - Amount of trackers
settings-general-gesture_control-trackers =
    { $amount ->
        [one] 1 tracker
       *[other] { $amount } trackers
    }
settings-general-gesture_control-yawResetEnabled = Увімкнути дотик, щоб скинути нахил
settings-general-gesture_control-yawResetDelay = Затримка скидання нахилу
settings-general-gesture_control-yawResetTaps = Дотики для скидання нахилу
settings-general-gesture_control-yawResetTracker = Yaw reset tracker
settings-general-gesture_control-fullResetEnabled = Увімкнути дотик для повного скидання
settings-general-gesture_control-fullResetDelay = Затримка повного скидання
settings-general-gesture_control-fullResetTaps = Дотики для повного скидання
settings-general-gesture_control-fullResetTracker = Full reset tracker
settings-general-gesture_control-mountingResetEnabled = Увімкнути дотик для скидання положення
settings-general-gesture_control-mountingResetDelay = Затримка скидання положення
settings-general-gesture_control-mountingResetTaps = Дотики для скидання положення
settings-general-gesture_control-mountingResetTracker = Mounting reset tracker
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = Трекери перевищують поріг
settings-general-gesture_control-numberTrackersOverThreshold-description = Збільште це значення, якщо не працює виявлення дотика. Не збільшуйте його вище ніж необхідно для виявлення дотика, оскільки це призведе до більшої кількості помилкових спрацьовувань.

## Appearance settings

settings-interface-appearance = Зовнішність
settings-general-interface-dev_mode = Режим розробника
settings-general-interface-dev_mode-description = Цей режим може бути корисним, якщо вам потрібні поглиблені дані або для взаємодії з підключеними трекерами на більш просунутому рівні.
settings-general-interface-dev_mode-label = Режим розробника
settings-general-interface-theme = Варіація оформлення
settings-general-interface-show-navbar-onboarding = Show "{ navbar-onboarding }" on navigation bar
settings-general-interface-show-navbar-onboarding-description = This changes whether the "{ navbar-onboarding }" button shows on the navigation bar.
settings-general-interface-show-navbar-onboarding-label = Show "{ navbar-onboarding }"
settings-general-interface-lang = Виберіть мову
settings-general-interface-lang-description = Змініть мову за замовчуванням, яку ви хочете використовувати.
settings-general-interface-lang-placeholder = Виберіть мову для використання
# Keep the font name untranslated
settings-interface-appearance-font = Шрифт GUI
settings-interface-appearance-font-description = Це змінює шрифт, який використовується інтерфейсом.
settings-interface-appearance-font-placeholder = Шрифт за замовчуванням
settings-interface-appearance-font-os_font = Шрифт операційної системи
settings-interface-appearance-font-slime_font = Шрифт за замовчуванням
settings-interface-appearance-font_size = Базове масштабування шрифту
settings-interface-appearance-font_size-description = Це впливає на розмір шрифту всього інтерфейсу, крім цієї панелі налаштувань.
settings-interface-appearance-decorations = Use the system native decorations
settings-interface-appearance-decorations-description = This will not render the top bar of the interface and will use the operating system's instead.
settings-interface-appearance-decorations-label = Use native decorations

## Notification settings

settings-interface-notifications = Повідомлення
settings-general-interface-serial_detection = Виявлення послідовного пристрою
settings-general-interface-serial_detection-description = Цей параметр відображатиме спливаюче вікно кожного разу, коли ви підключаєте новий послідовний пристрій, який може бути трекером. Це допомагає покращити процес налаштування трекера.
settings-general-interface-serial_detection-label = Виявлення послідовного пристрою
settings-general-interface-feedback_sound = Звук зворотного зв'язку
settings-general-interface-feedback_sound-description = Ця опція відтворюватиме звуковий сигнал при спрацьовуванні скидання
settings-general-interface-feedback_sound-label = Звук зворотного зв'язку
settings-general-interface-feedback_sound-volume = Гучність звуку зворотного зв'язку
settings-general-interface-connected_trackers_warning = Попередження про підключені трекери
settings-general-interface-connected_trackers_warning-description = Ця опція відображатиме спливаюче вікно кожного разу, коли ви намагатиметеся вийти зі SlimeVR, якщо підключено один або декілька трекерів. Він нагадує вам вимкнути ваші трекери, коли ви закінчите, щоб зберегти заряд батареї.
settings-general-interface-connected_trackers_warning-label = Попередження про підключені трекери під час виходу

## Behavior settings

settings-interface-behavior = Behavior
settings-general-interface-dev_mode = Developer Mode
settings-general-interface-dev_mode-description = This mode can be useful if you need in-depth data or need to interact with connected trackers on a more advanced level.
settings-general-interface-dev_mode-label = Developer Mode
settings-general-interface-use_tray = Згорнути в системний трей
settings-general-interface-use_tray-description = Дозволяє закрити вікно, не закриваючи сервер SlimeVR, так що ви можете продовжувати використати його, не турбуючись про інтерфейс.
settings-general-interface-use_tray-label = Згорнути в системний трей
settings-general-interface-discord_presence = Ділитися активністю в Discord
settings-general-interface-discord_presence-description = Повідомляє вашому клієнту Discord, що ви використовуєте SlimeVR, а також передає кількість трекерів IMU, які ви використовуєте.
settings-general-interface-discord_presence-label = Ділитися активністю в Discord
settings-general-interface-discord_presence-message =
    { $amount ->
        [0] Sliming around
        [one] Using 1 tracker
       *[other] Using { $amount } trackers
    }
settings-interface-behavior-error_tracking = Error collection via Sentry.io
settings-interface-behavior-error_tracking-description_v2 =
    <h1>Do you consent to the collection of anonymized error data?</h1>
    
    <b>We do not collect personal information</b> such as your IP address or wireless credentials. SlimeVR values your privacy!
    
    To provide the best user experience, we collect anonymized error reports, performance metrics, and operating system information. This helps us detect bugs and issues with SlimeVR. These metrics are collected via Sentry.io.
settings-interface-behavior-error_tracking-label = Send errors to developers
settings-interface-behavior-bvh_directory = Directory to save BVH recordings
settings-interface-behavior-bvh_directory-description = Choose a directory to save your BVH recordings instead of having to choose where to save them each time.
settings-interface-behavior-bvh_directory-label = Directory for BVH recordings

## Serial settings

settings-serial = Послідовна консоль
# This cares about multilines
settings-serial-description =
    Це інформаційна стрічка для послідовного зв'язку.
    Може бути корисним, якщо вам потрібно знати, що прошивка не працює.
settings-serial-connection_lost = Підключення до послідовного пристрою втрачене, повторне підключення...
settings-serial-reboot = Перезавантажити
settings-serial-factory_reset = Скидання до заводських налаштувань
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>Попередження:</b> Це скине трекер до заводських налаштувань.
    Це означає, що Wi-Fi та налаштування калібрування <b>будуть втрачені!</b>
settings-serial-factory_reset-warning-ok = Я знаю, що роблю
settings-serial-factory_reset-warning-cancel = Скасувати
settings-serial-serial_select = Вибір послідовного порту
settings-serial-auto_dropdown_item = Автоматично
settings-serial-get_wifi_scan = Сканувати мережу Wi-Fi
settings-serial-enter_pairing = Enter Pairing
settings-serial-exit_pairing = Exit Pairing
settings-serial-calibrate = Calibrate
settings-serial-six_side_calibrate = 6-Side Calibrate
settings-serial-dfu = Enter DFU
settings-serial-meow = Meow!
settings-serial-file_type = Простий текст
settings-serial-save_logs = Зберегти у файл
settings-serial-send_command = Send
settings-serial-send_command-placeholder = Command...
settings-serial-send_command-warning = <b>Warning:</b> Running serial commands can lead to data loss or brick the trackers.
settings-serial-send_command-warning-ok = I know what I'm doing
settings-serial-send_command-warning-cancel = Cancel

## OSC router settings

settings-osc-router = OSC роутер
# This cares about multilines
settings-osc-router-description =
    Пересилання повідомлень OSC з іншої програми.
    Корисно для використання іншої програми OSC з VRChat, наприклад.
settings-osc-router-enable = Увімкнути
settings-osc-router-enable-description = Увімкнути пересилання повідомлень.
settings-osc-router-enable-label = Увімкнути
settings-osc-router-network = Мережеві порти
# This cares about multilines
settings-osc-router-network-description =
    Встановіть порти для прослуховування і відправки даних.
    Вони можуть бути такими ж, як і інші порти, що використовуються на сервері SlimeVR.
settings-osc-router-network-port_in = 
    .label = Вхідний Порт
    .placeholder = Вхідний Порт (зазвичай: 9002)
settings-osc-router-network-port_out = 
    .label = Вихідний Порт
    .placeholder = Вихідний Порт (зазвичай: 9000)
settings-osc-router-network-address = Мережева адреса
settings-osc-router-network-address-description = Укажіть адресу для надсилання даних за адресою.
settings-osc-router-network-address-placeholder = IPV4-адреса

## OSC VRChat settings

settings-osc-vrchat = VRChat OSC трекери
# This cares about multilines
settings-osc-vrchat-description-v1 =
    Change settings specific to the OSC Trackers standard used for sending
    tracking data to applications without SteamVR (ex. Quest standalone).
    Make sure to enable OSC in VRChat via the Action Menu under OSC > Enabled.
settings-osc-vrchat-enable = Увімкнути
settings-osc-vrchat-enable-description = Перемикайте відправку та отримання даних.
settings-osc-vrchat-enable-label = Увімкнути
settings-osc-vrchat-oscqueryEnabled = Enable OSCQuery
settings-osc-vrchat-oscqueryEnabled-description =
    OSCQuery automatically detects running instances of VRChat and sends them data.
    It can also advertise itself to them in order to receive HMD and controller data.
    To allow receiving HMD and controller data from VRChat, go in your main menu's settings
    under "Tracking & IK" and enable "Allow Sending Head and Wrist VR Tracking OSC Data".
settings-osc-vrchat-oscqueryEnabled-label = Enable OSCQuery
settings-osc-vrchat-network = Мережеві порти
settings-osc-vrchat-network-description-v1 = Встановіть порти для прослуховування і відправки даних. Можна залишити без змін для VRChat.
settings-osc-vrchat-network-port_in = 
    .label = Вхідний Порт
    .placeholder = Вхідний Порт (зазвичай: 9001)
settings-osc-vrchat-network-port_out = 
    .label = Вихідний Порт
    .placeholder = Вихідний Порт (зазвичай: 9000)
settings-osc-vrchat-network-address = Мережева адреса
settings-osc-vrchat-network-address-description-v1 = Виберіть, за якою адресою надсилати дані. Можна залишити без змін для VRChat.
settings-osc-vrchat-network-address-placeholder = IP-адреса VRChat
settings-osc-vrchat-network-trackers = Трекери
settings-osc-vrchat-network-trackers-description = Перемикання відправку конкретних трекерів через OSC.
settings-osc-vrchat-network-trackers-chest = Груди
settings-osc-vrchat-network-trackers-hip = Бедро
settings-osc-vrchat-network-trackers-knees = Коліна
settings-osc-vrchat-network-trackers-feet = Ступні
settings-osc-vrchat-network-trackers-elbows = Лікті

## VMC OSC settings

settings-osc-vmc = Віртуальне захоплення руху
# This cares about multilines
settings-osc-vmc-description =
    Змінення настройок протоколу VMC (Virtual Motion Capture)
        щоб надсилати дані про кістки SlimeVR та отримувати дані про кістки з інших програм.
settings-osc-vmc-enable = Увімкнути
settings-osc-vmc-enable-description = Перемикайте відправку та отримання даних.
settings-osc-vmc-enable-label = Увімкнути
settings-osc-vmc-network = Мережеві порти
settings-osc-vmc-network-description = Встановіть порти для прослуховування і відправки даних по VMC
settings-osc-vmc-network-port_in = 
    .label = Вхідний Порт
    .placeholder = Вхідний Порт (зазвичай: 39540)
settings-osc-vmc-network-port_out = 
    .label = Вихідний Порт
    .placeholder = Вихідний Порт (зазвичай: 39539)
settings-osc-vmc-network-address = Мережева адреса
settings-osc-vmc-network-address-description = Виберіть, за якою адресою надсилати дані через VMC
settings-osc-vmc-network-address-placeholder = IPV4-адреса
settings-osc-vmc-vrm = Модель VRM
settings-osc-vmc-vrm-description = Завантажте модель VRM, щоб дозволити головний якір і забезпечити більш високу сумісність з іншими програмами
settings-osc-vmc-vrm-untitled_model = Untitled model
settings-osc-vmc-vrm-file_select = Перетягніть модель для використання або <u>знайдіть</u>
settings-osc-vmc-anchor_hip = Якір у стегон
settings-osc-vmc-anchor_hip-description = Закріпіть стеження на стегнах, корисно для сидячих VTubing. Якщо вимкнено, завантажте модель VRM.
settings-osc-vmc-anchor_hip-label = Якір у стегон
settings-osc-vmc-mirror_tracking = Дзеркальний трекінг
settings-osc-vmc-mirror_tracking-description = Віддзеркалити трекери горизонтально.
settings-osc-vmc-mirror_tracking-label = Дзеркальний трекінг

## Common OSC settings

settings-osc-common-network-ports_match_error = The OSC Router in and out ports can't be the same!
settings-osc-common-network-port_banned_error = The port { $port } can't be used!

## Advanced settings

settings-utils-advanced = Advanced
settings-utils-advanced-reset-gui = Reset GUI settings
settings-utils-advanced-reset-gui-description = Restore the default settings for the interface.
settings-utils-advanced-reset-gui-label = Reset GUI
settings-utils-advanced-reset-server = Reset tracking settings
settings-utils-advanced-reset-server-description = Restore the default settings for the tracking.
settings-utils-advanced-reset-server-label = Reset tracking
settings-utils-advanced-reset-all = Reset all settings
settings-utils-advanced-reset-all-description = Restore the default settings for both the interface and tracking.
settings-utils-advanced-reset-all-label = Reset all
settings-utils-advanced-reset_warning =
    <b>Warning:</b> This will reset { $type ->
        [gui] your GUI
        [server] your tracking
       *[all] all your
    } settings to the defaults.
    Are you sure you want to do this?
settings-utils-advanced-reset_warning-reset = Reset settings
settings-utils-advanced-reset_warning-cancel = Cancel
settings-utils-advanced-open_data-v1 = Config folder
settings-utils-advanced-open_data-description-v1 = Open SlimeVR's config folder in file explorer, containing the configuration
settings-utils-advanced-open_data-label = Open folder
settings-utils-advanced-open_logs = Logs folder
settings-utils-advanced-open_logs-description = Open SlimeVR's logs folder in file explorer, containing the logs of the app
settings-utils-advanced-open_logs-label = Open folder

## Home Screen

settings-home-list-layout = Trackers list layout
settings-home-list-layout-desc = Select one of the possible layouts of the home screen
settings-home-list-layout-grid = Grid
settings-home-list-layout-table = Table

## Tracking Checlist

settings-tracking_checklist-active_steps = Active Steps
settings-tracking_checklist-active_steps-desc = List of all the steps in the tracking checklist. You can choose to disable specific steps.

## Setup/onboarding menu

onboarding-skip = Пропустити налаштування
onboarding-continue = Продовжити
onboarding-wip = В роботі
onboarding-previous_step = Попередній крок
onboarding-setup_warning =
    <b>Попередження:</b> Початкова настройка потрібна для хорошого відстеження,
    це потрібно, якщо ви вперше використовуєте SlimeVR.
onboarding-setup_warning-skip = Пропустити налаштування
onboarding-setup_warning-cancel = Продовжити налаштування

## Quiz

onboarding-quiz_continue = Continue
onboarding-quiz_back = Back
onboarding-quiz-more_sets_modal-title = Have you connected all of your trackers?
onboarding-quiz-more_sets_modal-desc = If you have sets of different models, we can connect them right now!
onboarding-quiz-more_sets_modal-confirm = I have connected all my trackers
onboarding-quiz-more_sets_modal-cancel = I want to connect more trackers
onboarding-quiz-slimeset-title = What type of trackers are you connecting?
onboarding-quiz-slimeset-description = If you have multiple sets, you will be asked again later in the process
onboarding-quiz-slimeset-official-sets = Official SlimeVR Trackers
onboarding-quiz-slimeset-thirdparty-sets = Third-party or DIY Trackers
onboarding-quiz-slimeset-answer-regular = SlimeVR V1.0 & V1.2
onboarding-quiz-slimeset-answer-butterfly = Butterfly
onboarding-quiz-slimeset-answer-wifi = WiFi-based Slime
onboarding-quiz-slimeset-answer-dongle = Dongle-based Slime
onboarding-quiz-usage-title = What are you using your trackers for?
onboarding-quiz-usage-description = If you plan on using SlimeVR for multiple purposes, you can change the affected settings later.
onboarding-quiz-usage-answer-VRC = VR Gaming (e.g. VRChat)
onboarding-quiz-usage-answer-mocap_vtubing = Mocap and VTubing
onboarding-quiz-runtime-title = Do you run games via SteamVR, or on the headset itself (standalone)?
onboarding-quiz-runtime-answer-steamvr = SteamVR
onboarding-quiz-runtime-answer-standalone = Standalone
onboarding-quiz-mocap_preferences-title = Mocap Preferences
onboarding-quiz-mocap_preferences-desc = Specify how you plan to use SlimeVR for mocap or VTubing
onboarding-quiz-mocap_preferences-playspace-title = What is your playspace?
onboarding-quiz-mocap_preferences-playspace-desc = If standing, SlimeVR will try to track walking movement instead of anchoring you in one spot.
onboarding-quiz-mocap_preferences-playspace-sitting = Sitting
onboarding-quiz-mocap_preferences-playspace-standing = Standing
onboarding-quiz-mocap_preferences-vrm_model-title = Do you have a VRM model? (Optional)
onboarding-quiz-mocap_preferences-vrm_model-desc = Loading a VRM model will improve tracking quality and compatibility with applications that use VMC.
onboarding-quiz-mocap_preferences-head_tracker-title = Are you wearing a tracker or VR headset on your head?
onboarding-quiz-mocap_preferences-head_tracker-yes = Yes
onboarding-quiz-mocap_preferences-head_tracker-no = No
onboarding-quiz-mocap_preferences-head_tracker_location-title = Where is your head tracker located?
onboarding-quiz-mocap_preferences-head_tracker_location-forehead = Forehead
onboarding-quiz-mocap_preferences-head_tracker_location-face = Face

## Wi-Fi setup

onboarding-wifi_creds-back-v2 = Go back
onboarding-wifi_creds-v2 = Trackers using Wi-Fi
# This cares about multilines
onboarding-wifi_creds-description-v2 =
    Most trackers (such as official SlimeVR trackers) use Wi-Fi to connect to the server.
    Please use the credentials of the Wi-Fi network your device is currently connected to.
    
    Make sure to use a 2.4GHz Wi-Fi connection for your trackers!
onboarding-wifi_creds-skip = Пропустити налаштування Wi-Fi
onboarding-wifi_creds-submit = Підтвердити!
onboarding-wifi_creds-ssid = 
    .label = Назва Wi-Fi
    .placeholder = Введіть назву Wi-Fi
onboarding-wifi_creds-ssid-required = Wi-Fi name is required
onboarding-wifi_creds-password = 
    .label = Пароль
    .placeholder = Введіть Пароль
onboarding-wifi_creds-dongle-title = Trackers using a dongle
onboarding-wifi_creds-dongle-description = If your trackers came with a dongle, plug it into your device and you should be good to go!
onboarding-wifi_creds-dongle-wip = This section is a work in progress. A dedicated page to manage trackers that connect via a dongle will be made soon.
onboarding-wifi_creds-dongle-continue = Continue with a dongle

## Mounting setup

onboarding-reset_tutorial-back = Повернутися до калібрування положення
onboarding-reset_tutorial = Інструкція по скиданню
onboarding-reset_tutorial-explanation = Коли ви використовуєте свої трекери, вони можуть вийти з вирівнювання через дрейф нахилу IMU або тому, що ви могли їх фізично перемістити. Це можна виправити кількома способами.
onboarding-reset_tutorial-skip = Пропустити крок
# Cares about multiline
onboarding-reset_tutorial-0 =
    Торкніться { $taps } виділеного трекера, щоб запустити скидання нахилу.
    
    Це змусить трекери дивитися в тому ж напрямку, що і ваш шолом.
# Cares about multiline
onboarding-reset_tutorial-1 =
    Торкніться { $taps } разів виділеного трекера, щоб ініціювати повне скидання.
    
    Для цього потрібно стояти (i-поза). Існує затримка 3 секунди (можна налаштувати), перш ніж воно спрацює.
    Це повністю скидає розташування всіх ваших трекерів, та має вирішити більшість проблем.
# Cares about multiline
onboarding-reset_tutorial-2 =
    Торкніться { $taps } разів виділеного трекеру, щоб активувати скидання положення.
    
    Скидання положення допомагає в тому, як трекери насправді надягнені на вас, тому, якщо ви випадково перемістили їх та змінили місце розташування, це допоможе.
    
    Вам потрібно бути в позі, ніби ви катаєтеся на лижах, як показано на майстрі автоматичної прив'язки положення. У вас є затримка у 3 секунди (можна налаштувати), перш ніж спрацює скидання.

## Install info

install-info_udev-rules_modal_title = Hardware udev access rules not found
install-info_udev-rules_warning = Access rules via udev are required for serial console access & dongle connection. Paste the following command into your terminal to add the udev rules.
install-info_udev-rules_modal_button = Close
install-info_udev-rules_modal-dont-show-again_checkbox = Don't show again

## Setup start

onboarding-home = Ласкаво просимо до SlimeVR
onboarding-home-start = Давайте налаштуємося!

## Setup done

onboarding-done-title = Все готово!
onboarding-done-description = Насолоджуйтесь досвідом трекінгу всього тіла
onboarding-done-close = Закрити налаштування

## Tracker connection setup

onboarding-connect_tracker-back = Повернутися до даних Wi-Fi
onboarding-connect_tracker-title = Підключіть трекери
onboarding-connect_tracker-description-p0-v1 = Тепер найцікавіше – підключення трекерів!
onboarding-connect_tracker-description-p1-v1 = Підключіть кожен трекер по одному через USB-порт.
onboarding-connect_tracker-issue-serial = У мене виникли проблеми з підключенням!
onboarding-connect_tracker-usb = USB-трекер
onboarding-connect_tracker-connection_status-none = Шукаємо трекери
onboarding-connect_tracker-connection_status-serial_init = Підключення до послідовного пристрою
onboarding-connect_tracker-connection_status-obtaining_mac_address = Obtaining the tracker mac address
onboarding-connect_tracker-connection_status-provisioning = Надсилання даних Wi-Fi
onboarding-connect_tracker-connection_status-connecting = Спроба підключення до Wi-Fi
onboarding-connect_tracker-connection_status-looking_for_server = Шукаю сервер
onboarding-connect_tracker-connection_status-connection_error = Не вдається підключитися до мережі Wi-Fi
onboarding-connect_tracker-connection_status-could_not_find_server = Не вдалося знайти сервер
onboarding-connect_tracker-connection_status-done = Підключено до сервера
onboarding-connect_tracker-connection_status-no_serial_log = Could not get logs from the tracker
onboarding-connect_tracker-connection_status-no_serial_device_found = Could not find a tracker from USB
onboarding-connect_serial-error-modal-no_serial_log = Is the tracker turned on?
onboarding-connect_serial-error-modal-no_serial_log-desc = Make sure the tracker is turned on and connected to your computer.
onboarding-connect_serial-error-modal-no_serial_device_found = No trackers detected
onboarding-connect_serial-error-modal-no_serial_device_found-desc =
    Please connect a tracker with the provided USB cable to your computer and turn the tracker on.
    If this does not work:
      - try using a different USB cable
      - try using a different USB port
      - try reinstalling the SlimeVR server and select "USB Drivers" in the components section
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers =
    { $amount ->
        [0] Трекери не підключенно
        [one] 1 трекер підключенний
        [few] { $amount } трекерів підключенно
        [many] { $amount } трекерів підключенно
       *[other] { $amount } трекерів підключенно
    }
onboarding-connect_tracker-next = Я підключив усі свої трекери

## Tracker calibration tutorial

onboarding-calibration_tutorial = Інструкція з калібрування IMU
onboarding-calibration_tutorial-subtitle = Це допоможе зменшити дрейф трекера!
onboarding-calibration_tutorial-description-v1 = After turning on your trackers, place them on a stable surface for a moment to allow for calibration. Calibration can be performed at any time after the trackers are powered on—this page simply provides a tutorial. To begin, click the "{ onboarding-calibration_tutorial-calibrate }" button, then <b>do not move your trackers!</b>
onboarding-calibration_tutorial-calibrate = Я поклав свої трекери на стіл
onboarding-calibration_tutorial-status-waiting = Чекаємо на Вас
onboarding-calibration_tutorial-status-calibrating = Калібрування
onboarding-calibration_tutorial-status-success = Добре!
onboarding-calibration_tutorial-status-error = Трекер переміщено
onboarding-calibration_tutorial-skip = Skip tutorial

## Tracker assignment tutorial

onboarding-assignment_tutorial = Як підготувати Slime трекер перед його надяганням
onboarding-assignment_tutorial-first_step = 1. Розмістіть наліпку з частиною тіла (якщо вона у вас є) на трекері відповідно до вашого вибору
# This text has a character limit of around 11 characters, so please keep it short
onboarding-assignment_tutorial-sticker = Наліпка
onboarding-assignment_tutorial-second_step-v2 = 2. Прикріпіть ремінець до трекера, тримаючи липучку в тому ж напрямку, що й лицьова сторона вашого трекера:
onboarding-assignment_tutorial-second_step-continuation-v2 = Сторона з липучкою додаткового модуля повинна дивитися вгору, як на зображенні нижче:
onboarding-assignment_tutorial-done = Я наклеїв наліпки і закріпив ремінці!

## Tracker assignment setup

onboarding-assign_trackers-back = Повернутися до даних Wi-Fi
onboarding-assign_trackers-title = Призначити трекери
onboarding-assign_trackers-description = Давайте виберемо, який трекер куди йде. Натисніть на місце, де ви хочете розмістити трекер
onboarding-assign_trackers-unassign_all = Unassign all trackers
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned =
    { $trackers ->
        [one] { $assigned } з 1 трекеру призначенно
        [few] { $assigned } з { $trackers } трекерів призначенно
        [many] { $assigned } з { $trackers } трекерів призначенно
       *[other] { $assigned } з { $trackers } трекерів призначенно
    }
onboarding-assign_trackers-advanced = Відобразити розширені розташування призначень
onboarding-assign_trackers-next = Я призначив усі трекери
onboarding-assign_trackers-mirror_view = Дзеркальний вигляд
onboarding-assign_trackers-option-amount =
    { $trackersCount ->
        [one] x{ $trackersCount }
       *[other] x{ $trackersCount }
    }
onboarding-assign_trackers-option-label =
    { $mode ->
        [lower-body] Lower-Body Set
        [core] Core Set
        [enhanced-core] Enhanced Core Set
        [full-body] Full-Body Set
       *[all] All Trackers
    }
onboarding-assign_trackers-option-description =
    { $mode ->
        [lower-body] Minimum for VR full-body tracking
        [core] + Enhanced spine tracking
        [enhanced-core] + Foot rotation
        [full-body] + Elbow tracking
       *[all] All available tracker assignments
    }

## Tracker assignment warnings

# Note for devs, number is used for representing boolean states per bit.
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_FOOT =
    { $unassigned ->
        [0] Ліва нога призначенна, але треба ще ліва щиколотка, ліве стегно та на вибір груди, бедро або талія повинні бути призначенні
        [1] Ліва ступня призначенна, але в тебе повинно бути ліве стегно і на вибір груди, бедро або талія теж призначенні
        [2] Ліва ступня призначенна, але в тебе повинно бути ще ліва щиколотка та на вибір груди, бедро або талія теж призначенні
        [3] Ліва ступня призначенна, але тобі ще треба на вибір груди, бедро або талія теж призначенні
        [4] Ліва ступня призначенна, але тобі ще потрібно ліва щиколотка і ліве стегно теж призначенні
        [5] Ліва ступня призначенна, але тобі ще потрібно ліве стегно теж призначити
        [6] Ліва ступня призначенна, але тобі ще треба ліву щиколотку теж призначити
       *[other] Ліва ступня призначенна, але тобі ще треба Невідома кількість непризначенних частин тіла теж призначенні
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_FOOT =
    Right foot is assigned but you need { $unassigned ->
        [0] the right ankle, right thigh and either the chest, hip or waist
        [1] the right thigh and either the chest, hip or waist
        [2] the right ankle and either the chest, hip or waist
        [3] either the chest, hip or waist
        [4] the right ankle and right thigh
        [5] the right thigh
        [6] the right ankle
       *[unknown] Unknown unassigned body part
    } to also be assigned!
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_LOWER_LEG =
    Left ankle is assigned but you need { $unassigned ->
        [0] the left thigh and either the chest, hip or waist
        [1] either the chest, hip or waist
        [2] the left thigh
       *[unknown] Unknown unassigned body part
    } to also be assigned!
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_LOWER_LEG =
    Right ankle is assigned but you need { $unassigned ->
        [0] the right thigh and either the chest, hip or waist
        [1] either the chest, hip or waist
        [2] the right thigh
       *[unknown] Unknown unassigned body part
    } to also be assigned!
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_UPPER_LEG =
    Left thigh is assigned but you need { $unassigned ->
        [0] either the chest, hip or waist
       *[unknown] Unknown unassigned body part
    } to also be assigned!
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_UPPER_LEG =
    Right thigh is assigned but you need { $unassigned ->
        [0] either the chest, hip or waist
       *[unknown] Unknown unassigned body part
    } to also be assigned!
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-HIP =
    Hip is assigned but you need { $unassigned ->
        [0] the chest
       *[unknown] Unknown unassigned body part
    } to also be assigned!
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-WAIST =
    Waist is assigned but you need { $unassigned ->
        [0] the chest
       *[unknown] Unknown unassigned body part
    } to also be assigned!

## Tracker mounting method choose

onboarding-choose_mounting = Який спосіб калібрування положення використовувати?
# Multiline text
onboarding-choose_mounting-description = Орієнтація кріплення коригується для розміщення трекерів на вашому тілі.
onboarding-choose_mounting-auto_mounting = Автоматична прив'язка положення
# Italicized text
onboarding-choose_mounting-auto_mounting-label-v2 = Recommended
onboarding-choose_mounting-auto_mounting-description = Це автоматично визначить орієнтацію всіх ваших трекерів з 2 поз
onboarding-choose_mounting-manual_mounting = Самостійна прив'язка
# Italicized text
onboarding-choose_mounting-manual_mounting-label-v2 = Might not be precise enough
onboarding-choose_mounting-manual_mounting-description = Це дозволить обрати орієнтацію кожного трекера самостійно
# Multiline text
onboarding-choose_mounting-manual_modal-title =
    Ви впевнені, що хочете зробити
    автоматичне калібрування положення?
onboarding-choose_mounting-manual_modal-description = <b>Самостійне калібрування положення рекомендується для нових користувачів</b>, оскільки пози автоматичного калібрування положення можуть вимагати певної практики.
onboarding-choose_mounting-manual_modal-confirm = Я знаю, що роблю
onboarding-choose_mounting-manual_modal-cancel = Скасувати

## Tracker manual mounting setup

onboarding-manual_mounting-back = Повернутися до VR
onboarding-manual_mounting = Самостійна прив'язка
onboarding-manual_mounting-description = Натисніть на кожен трекер і виберіть, як вони розташовані
onboarding-manual_mounting-auto_mounting = Автоматична прив'язка
onboarding-manual_mounting-next = Наступний крок

## Tracker automatic mounting setup

onboarding-automatic_mounting-back = Повернутися до VR
onboarding-automatic_mounting-title = Mounting Calibration
onboarding-automatic_mounting-description = For SlimeVR trackers to work, we need to assign a mounting orientation to your trackers to align them with your physical tracker mounting.
onboarding-automatic_mounting-manual_mounting = Manual mounting
onboarding-automatic_mounting-next = Наступний крок
onboarding-automatic_mounting-prev_step = Попередній крок
onboarding-automatic_mounting-done-title = Mounting orientations calibrated.
onboarding-automatic_mounting-done-description = Your mounting calibration is complete!
onboarding-automatic_mounting-done-restart = Спробуйте знову
onboarding-automatic_mounting-mounting_reset-title = Mounting Calibration
onboarding-automatic_mounting-mounting_reset-step-0 = 1. Squat in a "skiing" pose with your legs bent, your upper body tilted forwards, and your arms bent.
onboarding-automatic_mounting-mounting_reset-step-1 = 2. Press the "Mounting calibration" button and wait for 3 seconds before the trackers' mounting orientations will reset.
onboarding-automatic_mounting-mounting_reset-feet-step-0 = 1. Stand on your toes with both feet pointing forward. Alternatively you can do it sitting on a chair.
onboarding-automatic_mounting-mounting_reset-feet-step-1 = 2. Press the "Feet calibration" button and wait for 3 seconds before the trackers' mounting orientations will reset.
onboarding-automatic_mounting-preparation-title = Preparation
onboarding-automatic_mounting-preparation-v2-step-0 = 1. Press the "Full Reset" button.
onboarding-automatic_mounting-preparation-v2-step-1 = 2. Stand upright with your arms to your sides. Make sure to look forward.
onboarding-automatic_mounting-preparation-v2-step-2 = 3. Hold the position until the 3s timer ends.
onboarding-automatic_mounting-put_trackers_on-title = Put on your trackers
onboarding-automatic_mounting-put_trackers_on-description = To calibrate mounting orientations, we're gonna use the trackers you just assigned. Put on all your trackers, you can see which are which in the figure to the right.
onboarding-automatic_mounting-put_trackers_on-next = I have all my trackers on
onboarding-automatic_mounting-return-home = Done

## Tracker manual proportions setupa

onboarding-manual_proportions-back-scaled = Go back to Scaled Proportions
onboarding-manual_proportions-title = Manual Body Proportions
onboarding-manual_proportions-fine_tuning_button = Automatically fine tune proportions
onboarding-manual_proportions-fine_tuning_button-disabled-tooltip = Please connect a VR headset to use automatic fine tuning
onboarding-manual_proportions-export = Export proportions
onboarding-manual_proportions-import = Import proportions
onboarding-manual_proportions-file_type = Body proportions file
onboarding-manual_proportions-normal_increment = Normal increment
onboarding-manual_proportions-precise_increment = Precise increment
onboarding-manual_proportions-grouped_proportions = Grouped proportions
onboarding-manual_proportions-all_proportions = All proportions
onboarding-manual_proportions-estimated_height = Estimated user height

## Tracker automatic proportions setup

onboarding-automatic_proportions-back = Go back to Manual Proportions
onboarding-automatic_proportions-title = Measure your body
onboarding-automatic_proportions-description = For SlimeVR trackers to work, we need to know the length of your bones. This short calibration will measure it for you.
onboarding-automatic_proportions-manual = Manual proportions
onboarding-automatic_proportions-prev_step = Попередній крок
onboarding-automatic_proportions-put_trackers_on-title = Put on your trackers
onboarding-automatic_proportions-put_trackers_on-description = To calibrate your proportions, we're gonna use the trackers you just assigned. Put on all your trackers, you can see which are which in the figure to the right.
onboarding-automatic_proportions-put_trackers_on-next = I have all my trackers on
onboarding-automatic_proportions-requirements-title = Requirements
# Each line of text is a different list item
onboarding-automatic_proportions-requirements-descriptionv2 =
    You have at least enough trackers to track your feet (generally 5 trackers).
    You have your trackers and headset on and are wearing them.
    Your trackers and headset are connected to the SlimeVR server and are working properly (ex. no stuttering, disconnecting, etc).
    Your headset is reporting positional data to the SlimeVR server (this generally means having SteamVR running and connected to SlimeVR using SlimeVR's SteamVR driver).
    Your tracking is working and is accurately representing your movements (ex. you have performed a full reset and they move the right direction when kicking, bending over, sitting, etc).
onboarding-automatic_proportions-requirements-next = Я ознайомився з вимогами
onboarding-automatic_proportions-check_height-title-v3 = Measure your headset height
onboarding-automatic_proportions-check_height-description-v2 = Your headset (HMD) height should be slightly less than your full height because headsets measure your eye height. This measurement will be used as a baseline for your body proportions.
# All the text is in bold!
onboarding-automatic_proportions-check_height-calculation_warning-v3 = Start measuring while standing <u>upright</u> to measure your height. Be careful not to raise your hands higher than your headset, as they may affect the measurement!
onboarding-automatic_proportions-check_height-guardian_tip =
    If you are using a standalone VR headset, make sure to have your guardian /
    boundary turned on so that your height is correct!
# Context is that the height is unknown
onboarding-automatic_proportions-check_height-unknown = Unknown
# Shows an element below it
onboarding-automatic_proportions-check_height-hmd_height2 = Your headset height is:
onboarding-automatic_proportions-check_height-measure-start = Start measuring
onboarding-automatic_proportions-check_height-measure-stop = Stop measuring
onboarding-automatic_proportions-check_height-measure-reset = Retry measuring
onboarding-automatic_proportions-check_height-next_step = Use headset height
onboarding-automatic_proportions-check_floor_height-title = Measure your floor height (optional)
onboarding-automatic_proportions-check_floor_height-description = In some cases, your floor height may not be set correctly by your headset, causing the headset height to be measured as higher than it should be. You can measure the "height" of your floor to correct your headset height.
# All the text is in bold!
onboarding-automatic_proportions-check_floor_height-calculation_warning-v2 = Start measuring and put a controller against your floor to measure its height. If you are sure that your floor height is correct, you can skip this step.
# Shows an element below it
onboarding-automatic_proportions-check_floor_height-floor_height = Your floor height is:
onboarding-automatic_proportions-check_floor_height-full_height = Your estimated full height is:
onboarding-automatic_proportions-check_floor_height-measure-start = Start measuring
onboarding-automatic_proportions-check_floor_height-measure-stop = Stop measuring
onboarding-automatic_proportions-check_floor_height-measure-reset = Retry measuring
onboarding-automatic_proportions-check_floor_height-skip_step = Skip step and save
onboarding-automatic_proportions-check_floor_height-next_step = Use floor height and save
onboarding-automatic_proportions-start_recording-title = Приготуйтеся рухатися
onboarding-automatic_proportions-start_recording-description = We're now going to record some specific poses and moves. These will be prompted in the next screen. Be ready to start when the button is pressed!
onboarding-automatic_proportions-start_recording-next = Почати запис
onboarding-automatic_proportions-recording-title = ЗАПИС
onboarding-automatic_proportions-recording-description-p0 = Recording in progress...
onboarding-automatic_proportions-recording-description-p1 = Повторюйте рухи, показані нижче:
# Each line of text is a different list item
onboarding-automatic_proportions-recording-steps =
    Standing up straight, roll your head in a circle.
    Bend your back forward and squat. While squatting, look to your left, then to your right.
    Twist your upper body to the left (counter-clockwise), then reach down toward the ground.
    Twist your upper body to the right (clockwise), then reach down toward the ground.
    Roll your hips in a circular motion as if you're using a hula hoop.
    If there is time left on the recording, you can repeat these steps until it's finished.
onboarding-automatic_proportions-recording-processing = Processing the result
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 20)
onboarding-automatic_proportions-recording-timer =
    { $time ->
        [one] залишилась { $time }  секунда
        [few] залишилось { $time } секунди
        [many] залишилось { $time } секунд
       *[other] залишилось { $time } секунд
    }
onboarding-automatic_proportions-verify_results-title = Перевірити результати
onboarding-automatic_proportions-verify_results-description = Check the results below, do they look correct?
onboarding-automatic_proportions-verify_results-results = Recording results
onboarding-automatic_proportions-verify_results-processing = Обробка результату
onboarding-automatic_proportions-verify_results-redo = Redo recording
onboarding-automatic_proportions-verify_results-confirm = They're correct
onboarding-automatic_proportions-done-title = Body measured and saved.
onboarding-automatic_proportions-done-description = Your body proportions' calibration is complete!
onboarding-automatic_proportions-error_modal-v2 =
    <b>Warning:</b> There was an error while estimating proportions!
    This is likely a mounting calibration issue. Make sure your tracking works properly before trying again.
    Please <docs>check the docs</docs> or join our <discord>Discord</discord> for help ^_^
onboarding-automatic_proportions-error_modal-confirm = Understood!
onboarding-automatic_proportions-smol_warning =
    Your configured height of { $height } is smaller than the minimum accepted height of { $minHeight }.
    <b>Please redo the measurements and ensure they are correct.</b>
onboarding-automatic_proportions-smol_warning-cancel = Go back

## User height calibration

onboarding-user_height-title = What is your height?
onboarding-user_height-description = We need your height to calculate your body proportions and accurately represent your movements. You can either let SlimeVR calculate it, or input your height manually.
onboarding-user_height-need_head_tracker = A headset and controllers with positional tracking are required to perform the calibration.
onboarding-user_height-calculate = Calculate my height automatically
onboarding-user_height-next_step = Continue and save
onboarding-user_height-prev_step = Back
onboarding-user_height-manual-proportions = Manual Proportions
onboarding-user_height-calibration-title = Calibration Progress
onboarding-user_height-calibration-RECORDING_FLOOR = Touch the floor with the tip of your controller
onboarding-user_height-calibration-WAITING_FOR_RISE = Stand back up
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK = Stand back up and look forward
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-ok = Make sure your head is leveled
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-low = Do not look at the floor
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-high = Do not look too high up
onboarding-user_height-calibration-WAITING_FOR_CONTROLLER_PITCH = Make sure the controller is pointing down
onboarding-user_height-calibration-RECORDING_HEIGHT = Stand back up and stand still!
onboarding-user_height-calibration-DONE = Success!
onboarding-user_height-calibration-ERROR_TIMEOUT = Calibration timed out, try again.
onboarding-user_height-calibration-ERROR_TOO_HIGH = The detected user height is too high, try again.
onboarding-user_height-calibration-ERROR_TOO_SMALL = The detected user height is too small. Make sure to stand straight and look forward at the end of the calibration.
onboarding-user_height-calibration-error = Calibration Failed
onboarding-user_height-manual-tip = While adjusting your height, try different poses and see how the skeleton matches your body.
onboarding-user_height-reset-warning =
    <b>Warning:</b> This will reset your proportions to be based on your height.
    Are you sure you want to do this?

## Stay Aligned setup

onboarding-stay_aligned-title = Stay Aligned
onboarding-stay_aligned-description = Configure Stay Aligned to keep your trackers aligned.
onboarding-stay_aligned-put_trackers_on-title = Put on your trackers
onboarding-stay_aligned-put_trackers_on-description = To save your resting poses, we'll use the trackers you just assigned. Put on all your trackers, you can see which are which in the figure to the right.
onboarding-stay_aligned-put_trackers_on-trackers_warning = You have fewer than 5 trackers currently connected and assigned! This is the minimum amount of trackers required for Stay Aligned to function properly.
onboarding-stay_aligned-put_trackers_on-next = I have all my trackers on
onboarding-stay_aligned-verify_mounting-title = Mounting Calibration
onboarding-stay_aligned-verify_mounting-step-0 = Stay Aligned requires good mounting. Otherwise, you won't get a good experience with Stay Aligned.
onboarding-stay_aligned-verify_mounting-step-1 = 1. Move around while standing.
onboarding-stay_aligned-verify_mounting-step-2 = 2. Sit down and move your legs and feet.
onboarding-stay_aligned-verify_mounting-step-3 = 3. If your trackers aren't in the right place, press "Redo Mounting Calibration".
onboarding-stay_aligned-verify_mounting-redo_mounting = Redo Mounting calibration
onboarding-stay_aligned-preparation-title = Preparation
onboarding-stay_aligned-preparation-tip = Make sure to stand upright. Keep looking forward with your arms down at your sides.
onboarding-stay_aligned-relaxed_poses-standing-title = Relaxed Standing Pose
onboarding-stay_aligned-relaxed_poses-standing-step-0 = 1. Stand in a comfortable position. Relax!
onboarding-stay_aligned-relaxed_poses-standing-step-1-v2 = 2. Press the "Save pose" button.
onboarding-stay_aligned-relaxed_poses-sitting-title = Relaxed Sitting in Chair Pose
onboarding-stay_aligned-relaxed_poses-sitting-step-0 = 1. Sit in a comfortable position. Relax!
onboarding-stay_aligned-relaxed_poses-sitting-step-1-v2 = 2. Press the "Save pose" button.
onboarding-stay_aligned-relaxed_poses-flat-title = Relaxed Sitting on Floor Pose
onboarding-stay_aligned-relaxed_poses-flat-step-0 = 1. Sit on the floor with your legs in front. Relax!
onboarding-stay_aligned-relaxed_poses-flat-step-1-v2 = 2. Press the "Save pose" button.
onboarding-stay_aligned-relaxed_poses-skip_step = Skip
onboarding-stay_aligned-done-title = Stay Aligned enabled!
onboarding-stay_aligned-done-description = Your Stay Aligned setup is complete!
onboarding-stay_aligned-done-description-2 = Setup is complete! You may restart the process if you want to recalibrate the poses.
onboarding-stay_aligned-previous_step = Previous
onboarding-stay_aligned-next_step = Next
onboarding-stay_aligned-restart = Restart
onboarding-stay_aligned-done = Done
onboarding-stay_aligned-manual_mounting-done = Done

## Home

home-no_trackers = No trackers detected or assigned
home-settings = Home Page Settings
home-settings-close = Close

## Trackers Still On notification

trackers_still_on-modal-title = Trackers still on
trackers_still_on-modal-description =
    One or more trackers are still on.
    Do you still want to exit SlimeVR?
trackers_still_on-modal-confirm = Exit SlimeVR
trackers_still_on-modal-cancel = Hold on...

## Status system

status_system-StatusTrackerReset = It is recommended to perform a full reset as one or more trackers are unadjusted.
status_system-StatusSteamVRDisconnected =
    { $type ->
       *[steamvr] Currently not connected to SteamVR via the SlimeVR driver.
        [steamvr_feeder] Currently not connected to the SlimeVR Feeder App.
    }
status_system-StatusTrackerError = The { $trackerName } tracker has an error.
status_system-StatusUnassignedHMD = The VR headset should be assigned as a head tracker.
status_system-StatusPublicNetwork =
    { $count ->
        [one] Your network profile is currently set to Public ({ $adapters }). This is not recommended for SlimeVR to function properly. <PublicFixLink>See how to fix it here.</PublicFixLink>
       *[many] Some of your network adapters are set to public: { $adapters }. This is not recommended for SlimeVR to function properly. <PublicFixLink>See how to fix it here.</PublicFixLink>
    }

## Firmware tool globals

firmware_tool-next_step = Next Step
firmware_tool-previous_step = Previous Step
firmware_tool-ok = Looks good
firmware_tool-retry = Retry
firmware_tool-loading = Loading...

## Firmware tool Steps

firmware_tool = DIY Firmware tool
firmware_tool-description = Allows you to configure and flash your DIY trackers
firmware_tool-not_available = Oops, the firmware tool is not available at the moment. Come back later!
firmware_tool-not_compatible = The firmware tool is not compatible with this version of the server. Please update your server!
firmware_tool-select_source = Select the firmware to flash
firmware_tool-select_source-description = Select the firmware you want to flash on your board
firmware_tool-select_source-error = Unable to load Sources
firmware_tool-select_source-board_type = Board Type
firmware_tool-select_source-firmware = Firmware Source
firmware_tool-select_source-version = Firmware Version
firmware_tool-select_source-official = Official
firmware_tool-select_source-dev = Dev
firmware_tool-select_source-not_selected = No source selected
firmware_tool-select_source-no_boards = No available boards for this source
firmware_tool-select_source-no_versions = No available versions for this source
firmware_tool-board_defaults = Configure your board
firmware_tool-board_defaults-description = Set the pins or settings relative to your hardware
firmware_tool-board_defaults-add = Add
firmware_tool-board_defaults-reset = Reset to Default
firmware_tool-board_defaults-error-required = Required field
firmware_tool-board_defaults-error-format = Invalid format
firmware_tool-board_defaults-error-format-number = Not a number
firmware_tool-flash_method_step = Flashing Method
firmware_tool-flash_method_step-description = Please select the flashing method you want to use
firmware_tool-flash_method_step-ota-v2 = 
    .label = Wi-Fi
    .description = Use the over-the-air method. Your tracker will use Wi-Fi to update its firmware. Only works on trackers that have been set up.
firmware_tool-flash_method_step-ota-info =
    We use your wifi credentials to flash the tracker and confirm that everything worked correctly.
    <b>We do not store your wifi credentials!</b>
firmware_tool-flash_method_step-serial-v2 = 
    .label = USB
    .description = Use a USB cable to update your tracker.
firmware_tool-flashbtn_step = Press the boot button
firmware_tool-flashbtn_step-description = Before going to the next step, there are a few things you need to do
firmware_tool-flashbtn_step-board_SLIMEVR = Turn off the tracker, remove the case (if any), connect the USB cable to your computer, then follow the appropriate steps for your SlimeVR board revision:
firmware_tool-flashbtn_step-board_SLIMEVR-r11-v2 = Turn on the tracker while shorting the second rectangular FLASH pad from the edge on the top side of the board to the metal shield of the microcontroller. The tracker LED should do a short blink.
firmware_tool-flashbtn_step-board_SLIMEVR-r12-v2 = Turn on the tracker while shorting the circular FLASH pad on the top side of the board to the metal shield of the microcontroller. The tracker LED should do a short blink.
firmware_tool-flashbtn_step-board_SLIMEVR-r14-v2 = Turn on the tracker while pushing in the FLASH button on the top side of the board. The tracker LED should do a short blink.
firmware_tool-flashbtn_step-board_OTHER =
    Before flashing, you will probably need to put the tracker into bootloader mode.
    Most of the time, this means pressing the boot button on the board before the flashing process starts.
    If the flashing process times out at the start, it probably means that the tracker was not in bootloader mode.
    Refer to your board's flashing instructions to learn how to enter bootloader mode.
firmware_tool-flash_method_ota-title = Flashing over Wi-Fi
firmware_tool-flash_method_ota-devices = Detected OTA Devices:
firmware_tool-flash_method_ota-no_devices = There are no boards that can be updated using OTA, make sure you selected the correct board type
firmware_tool-flash_method_serial-title = Flashing over USB
firmware_tool-flash_method_serial-wifi = Wi-Fi Credentials:
firmware_tool-flash_method_serial-devices-label = Detected Serial Devices:
firmware_tool-flash_method_serial-devices-placeholder = Select a serial device
firmware_tool-flash_method_serial-no_devices = There are no compatible serial devices detected, make sure the tracker is plugged in
firmware_tool-build_step = Building
firmware_tool-build_step-description = The firmware is building, please wait
firmware_tool-flashing_step = Flashing
firmware_tool-flashing_step-description = Your trackers are flashing, please follow the instructions on the screen
firmware_tool-flashing_step-warning-v2 = Do not unplug or turn off the tracker during the upload process unless told to, it may make your board unusable
firmware_tool-flashing_step-flash_more = Flash more trackers
firmware_tool-flashing_step-exit = Exit
firmware_tool-flashing_step-onboarding_continue = Continue

## firmware tool build status

firmware_tool-build-QUEUED = Waiting to build....
firmware_tool-build-CREATING_BUILD_FOLDER = Creating the build folder
firmware_tool-build-DOWNLOADING_SOURCE = Downloading the source code
firmware_tool-build-EXTRACTING_SOURCE = Extracting the source code
firmware_tool-build-BUILDING = Building the firmware
firmware_tool-build-SAVING = Saving the build
firmware_tool-build-DONE = Build Complete
firmware_tool-build-ERROR = Unable to build the firmware

## Firmware update status

firmware_update-status-DOWNLOADING = Downloading the firmware
firmware_update-status-NEED_MANUAL_REBOOT-v2 = Please turn your tracker off and on again
firmware_update-status-AUTHENTICATING = Authenticating with the mcu
firmware_update-status-UPLOADING = Uploading the firmware
firmware_update-status-SYNCING_WITH_MCU = Syncing with the mcu
firmware_update-status-REBOOTING = Applying the update
firmware_update-status-PROVISIONING = Setting Wi-Fi credentials
firmware_update-status-DONE = Update complete!
firmware_update-status-ERROR_DEVICE_NOT_FOUND = Could not find the device
firmware_update-status-ERROR_TIMEOUT = The update process timed out
firmware_update-status-ERROR_DOWNLOAD_FAILED = Could not download the firmware
firmware_update-status-ERROR_AUTHENTICATION_FAILED = Could not authenticate with the mcu
firmware_update-status-ERROR_UPLOAD_FAILED = Could not upload the firmware
firmware_update-status-ERROR_PROVISIONING_FAILED = Could not set the Wi-Fi credentials
firmware_update-status-ERROR_UNSUPPORTED_METHOD = The update method is not supported
firmware_update-status-ERROR_UNKNOWN = Unknown error

## Dedicated Firmware Update Page

firmware_update-title = Firmware update
firmware_update-devices = Available Devices
firmware_update-devices-description = Please select the trackers you want to update to the latest version of SlimeVR firmware.
firmware_update-no_devices = Please make sure that the trackers you want to update are ON and connected to the Wi-Fi!
firmware_update-changelog-title = Updating to { $version }
firmware_update-looking_for_devices = Looking for devices to update...
firmware_update-retry = Retry
firmware_update-update = Update Selected Trackers
firmware_update-exit = Exit

## Tray Menu

tray_menu-show = Show
tray_menu-hide = Hide
tray_menu-quit = Quit

## First exit modal

tray_or_exit_modal-title = What should the close button do?
# Multiline text
tray_or_exit_modal-description =
    Choose whether to exit the server or minimize it to the tray when clicking the close button.
    
    You can change this later in the interface settings!
tray_or_exit_modal-radio-exit = Вийти після закриття
tray_or_exit_modal-radio-tray = Згорнути в системний трей
tray_or_exit_modal-submit = Зберегти
tray_or_exit_modal-cancel = Скасувати

## Unknown device modal

unknown_device-modal-title = Знайдено новий трекер!
unknown_device-modal-description =
    З'явився новий трекер із MAC-адресою <b>{ $deviceId }</b>.
    Бажаєте підключити його до SlimeVR?
unknown_device-modal-confirm = Звісно!
unknown_device-modal-forget = Ігнорувати
# VRChat config warnings
vrc_config-page-title = VRChat configuration warnings
vrc_config-page-desc = This page shows the state of your VRChat settings and shows what settings are incompatible with SlimeVR. It is highly recommended that you fix any warnings showing up here for the best user experience with SlimeVR.
vrc_config-page-help = Can't find the settings?
vrc_config-page-help-desc = Check out our <a>documentation on this topic!</a>
vrc_config-page-big_menu = Tracking & IK (Big Menu)
vrc_config-page-big_menu-desc = Settings related to IK in the big settings menu
vrc_config-page-wrist_menu = Tracking & IK (Wrist Menu)
vrc_config-page-wrist_menu-desc = Settings related to IK in small settings menu (wrist menu)
vrc_config-on = On
vrc_config-off = Off
vrc_config-invalid = You have misconfigured VRChat settings!
vrc_config-show_more = Show more
vrc_config-setting_name = VRChat Setting name
vrc_config-recommended_value = Recommended Value
vrc_config-current_value = Current Value
vrc_config-mute = Mute Warning
vrc_config-mute-btn = Mute
vrc_config-unmute-btn = Unmute
vrc_config-legacy_mode = Use Legacy IK Solving
vrc_config-disable_shoulder_tracking = Disable Shoulder Tracking
vrc_config-shoulder_width_compensation = Shoulder Width Compensation
vrc_config-spine_mode = FBT Spine Mode
vrc_config-tracker_model = FBT Tracker Model
vrc_config-avatar_measurement_type = Avatar Measurement
vrc_config-calibration_range = Calibration Range
vrc_config-calibration_visuals = Display Calibration Visuals
vrc_config-user_height = User Real Height
vrc_config-spine_mode-UNKNOWN = Unknown
vrc_config-spine_mode-LOCK_BOTH = Lock Both
vrc_config-spine_mode-LOCK_HEAD = Lock Head
vrc_config-spine_mode-LOCK_HIP = Lock Hip
vrc_config-tracker_model-UNKNOWN = Unknown
vrc_config-tracker_model-AXIS = Axis
vrc_config-tracker_model-BOX = Box
vrc_config-tracker_model-SPHERE = Sphere
vrc_config-tracker_model-SYSTEM = System
vrc_config-avatar_measurement_type-UNKNOWN = Unknown
vrc_config-avatar_measurement_type-HEIGHT = Height
vrc_config-avatar_measurement_type-ARM_SPAN = Arm Span

## Error collection consent modal

error_collection_modal-title = Can we collect errors?
error_collection_modal-description_v2 =
    { settings-interface-behavior-error_tracking-description_v2 }
    
    You can change this setting later in the Behavior section of the settings page.
error_collection_modal-confirm = I agree
error_collection_modal-cancel = I don't want to

## Tracking checklist section

tracking_checklist = Tracking Checklist
tracking_checklist-settings = Tracking Checklist Settings
tracking_checklist-settings-close = Close
tracking_checklist-status-incomplete = You are not prepared to use SlimeVR!
tracking_checklist-status-partial =
    { $count ->
        [one] You have 1 warning!
       *[many] You have { $count } warnings!
    }
tracking_checklist-status-complete = You are prepared to use SlimeVR!
tracking_checklist-MOUNTING_CALIBRATION = Perform a mounting calibration
tracking_checklist-FEET_MOUNTING_CALIBRATION = Perform a feet mounting calibration
tracking_checklist-FULL_RESET = Perform a full reset
tracking_checklist-FULL_RESET-desc = Some trackers need a reset to be performed.
tracking_checklist-STEAMVR_DISCONNECTED = SteamVR disconnected
tracking_checklist-STEAMVR_DISCONNECTED-desc = SteamVR is not running. Are you using it for VR?
tracking_checklist-STEAMVR_DISCONNECTED-driver_blocked-desc = The driver has been blocked by SteamVR due to a previous SteamVR crash.
tracking_checklist-STEAMVR_DISCONNECTED-driver_disabled-desc = The driver is disabled in SteamVR settings.
tracking_checklist-STEAMVR_DISCONNECTED-driver_not_installed-desc = The driver is not installed.
tracking_checklist-STEAMVR_DISCONNECTED-open = Launch SteamVR
tracking_checklist-STEAMVR_DISCONNECTED-enable = Enable driver
tracking_checklist-STEAMVR_HANDS_ENABLED = Hand trackers toggled on
tracking_checklist-STEAMVR_HANDS_ENABLED-desc = You have enabled the SteamVR virtual hand trackers. This will cause button inputs to not work in SteamVR and in games.
tracking_checklist-STEAMVR_HANDS_ENABLED-go = Disable them
tracking_checklist-STANDABLE_INSTALLED = Standable is installed
tracking_checklist-STANDABLE_INSTALLED-desc =
    Standable frequently causes tracking issues when used alongside SlimeVR. Standable should be fully uninstalled in Steam to ensure no issues arise.
    You must close SteamVR before uninstalling Standable in Steam.
tracking_checklist-TRACKERS_REST_CALIBRATION = Calibrate your trackers
tracking_checklist-TRACKERS_REST_CALIBRATION-desc = You didn't perform tracker calibration. Please let your trackers (highlighted in yellow) rest on a stable surface for a few seconds.
tracking_checklist-TRACKER_ERROR = Trackers with Errors
tracking_checklist-TRACKER_ERROR-desc = Some of your trackers have an error. Please restart the trackers highlighted in yellow.
tracking_checklist-VRCHAT_SETTINGS = Configure VRChat settings
tracking_checklist-VRCHAT_SETTINGS-desc = You have misconfigured VRChat settings! This can negatively impact your tracking.
tracking_checklist-VRCHAT_SETTINGS-open = Go to VRChat Warnings
tracking_checklist-UNASSIGNED_HMD = VR headset not assigned to Head
tracking_checklist-UNASSIGNED_HMD-desc = The VR headset should be assigned as a head tracker.
tracking_checklist-NETWORK_PROFILE_PUBLIC = Change your network profile
tracking_checklist-NETWORK_PROFILE_PUBLIC-desc =
    { $count ->
        [one]
            Your network profile is currently set to Public ({ $adapters }).
            This is not recommended for SlimeVR to function properly.
            <PublicFixLink>See how to fix it here.</PublicFixLink>
       *[many]
            Some of your network adapters are set to public:
            { $adapters }
            This is not recommended for SlimeVR to function properly.
            <PublicFixLink>See how to fix it here.</PublicFixLink>
    }
tracking_checklist-NETWORK_PROFILE_PUBLIC-open = Open Control Panel
tracking_checklist-STAY_ALIGNED_CONFIGURED = Configure Stay Aligned
tracking_checklist-STAY_ALIGNED_CONFIGURED-desc = Record the Stay Aligned poses to reduce drift
tracking_checklist-STAY_ALIGNED_CONFIGURED-open = Open Stay Aligned Wizard
tracking_checklist-ignore = Ignore
preview-mocap_mode_soon = Mocap Mode (Soon™)
preview-disable_render = Disable rendering
preview-disabled_render = Rendering disabled
toolbar-mounting_calibration = Mounting Calibration
toolbar-mounting_calibration-default = Body
toolbar-mounting_calibration-feet = Feet
toolbar-mounting_calibration-fingers = Fingers
toolbar-drift_reset = Drift Reset
toolbar-assigned_trackers = { $count } trackers assigned
toolbar-unassigned_trackers = { $count } trackers unassigned
