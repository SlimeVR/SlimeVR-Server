# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Підключення до сервера
websocket-connection_lost = З'єднання з сервером втрачено. Повторне підключення...

## Update notification

version_update-title = Доступна нова версія: { $version }
version_update-description = Натискання "{ version_update-update }" почнеться завантаження SlimeVR installer.
version_update-update = Оновлення
version_update-close = Закрити

## Tips

tips-find_tracker = Не знаєте який трекер вибирати? Потрясіть трекер і він підсвітить відповідний пункт.
tips-do_not_move_heels = Переконайтеся, що не рухаєте п'ятами під час запису!
tips-file_select = Перетягніть файли для використання або <u>знайдіть</u>.
tips-failed_webgl = Не вдалося ініціалізувати WebGL.

## Units


## Dropdown


## Text input


## File input


## Window controls

titlebar-close = Закрити

## Body parts

body_part-NONE = Непризначений
body_part-HEAD = Голова
body_part-NECK = Шия
body_part-RIGHT_SHOULDER = Праве плече
body_part-RIGHT_UPPER_ARM = Права верхня частина руки
body_part-RIGHT_LOWER_ARM = Права нижня частина руки
body_part-RIGHT_HAND = Права рука
body_part-RIGHT_UPPER_LEG = Праве стегно
body_part-RIGHT_LOWER_LEG = Права щиколотка
body_part-RIGHT_FOOT = Права ступня
body_part-UPPER_CHEST = Верхня частина грудей
body_part-HIP = Бедро
body_part-LEFT_SHOULDER = Ліве плече
body_part-LEFT_UPPER_ARM = Ліва верхня частина руки
body_part-LEFT_LOWER_ARM = Ліва нижня частина руки
body_part-LEFT_HAND = Ліва рука
body_part-LEFT_UPPER_LEG = Ліве стегно
body_part-LEFT_LOWER_LEG = Ліва щиколотка
body_part-LEFT_FOOT = Ліва ступня

## BoardType


## Proportions

skeleton_bone-NONE = Нічого
skeleton_bone-HEAD = Зсув голови
skeleton_bone-NECK = Довжина шиї
skeleton_bone-torso_group = Довжина тулуба
skeleton_bone-UPPER_CHEST = Довжина верхньої частини грудей
skeleton_bone-HIP = Довжина стегна
skeleton_bone-HIPS_WIDTH = Ширина стегон
skeleton_bone-leg_group = Довжина ніг
skeleton_bone-UPPER_LEG = Довжина верхньої частини ноги
skeleton_bone-LOWER_LEG = Довжина гомілки
skeleton_bone-FOOT_LENGTH = Довжина стопи
skeleton_bone-FOOT_SHIFT = Зміщення стопи
skeleton_bone-SHOULDERS_DISTANCE = Відстань між плечима
skeleton_bone-SHOULDERS_WIDTH = Ширина плечей
skeleton_bone-arm_group = Довжина руки
skeleton_bone-UPPER_ARM = Довжина верхньої частини руки
skeleton_bone-LOWER_ARM = Довжина нижньої частини руки
skeleton_bone-HAND_Y = Відстань рук Y
skeleton_bone-HAND_Z = Відстань руки Z

## Tracker reset buttons

reset-reset_all = Скинути всі пропорції
reset-reset_all_warning-cancel = Скасувати
reset-full = Повне скидання
reset-mounting = Калібрування положення
reset-yaw = Скинути нахил

## Navigation bar

navbar-home = Домашня сторінка
navbar-body_proportions = Пропорції тіла
navbar-trackers_assign = Призначення трекера
navbar-mounting = Калібрування положення
navbar-onboarding = Майстер налаштування
navbar-settings = Параметри

## Biovision hierarchy recording

bvh-start_recording = Запис BVH
bvh-recording = Запис...

## Tracking pause

tracking-unpaused = Призупинити трекінг
tracking-paused = Продовжити трекінг

## Widget: Developer settings

widget-developer_mode = Режим розробника
widget-developer_mode-high_contrast = Висока контрастність
widget-developer_mode-precise_rotation = Точне обертання
widget-developer_mode-fast_data_feed = Швидка подача даних
widget-developer_mode-raw_slime_rotation = Необроблене

## Widget: IMU Visualizer

widget-imu_visualizer = Обертання
widget-imu_visualizer-hide = Сховати
widget-imu_visualizer-rotation_raw = Необроблене
widget-imu_visualizer-rotation_preview = Попередній перегляд

## Tracker status

tracker-status-none = Немає статусу
tracker-status-busy = Зайнятий
tracker-status-error = Помилка
tracker-status-disconnected = Відключено
tracker-status-occluded = Закрито
tracker-status-timed_out = Минув час очікування

## Tracker status columns

tracker-table-column-name = Ім'я
tracker-table-column-type = Тип
tracker-table-column-battery = Батарея
tracker-table-column-ping = Пінг
tracker-table-column-temperature = Темп. °C
tracker-table-column-linear-acceleration = Прискорення X/Y/Z
tracker-table-column-rotation = Обертання X/Y/Z
tracker-table-column-position = Позиція X/Y/Z

## Tracker rotation

tracker-rotation-front = Спереду
tracker-rotation-front_left = Ліва сторона передньої частини
tracker-rotation-front_right = Права сторона передньої частини
tracker-rotation-left = Зліва
tracker-rotation-right = Справа
tracker-rotation-back = Назад
tracker-rotation-back_left = Ліва сторона задньої частини
tracker-rotation-back_right = Права сторона задньої частини
tracker-rotation-custom = Персональне

## Tracker information

tracker-infos-manufacturer = Виробник
tracker-infos-display_name = Відображуване ім'я
tracker-infos-custom_name = Персональне ім'я
tracker-infos-url = URL трекера
tracker-infos-hardware_identifier = Ідентифікатор обладнання
tracker-infos-imu = IMU Сенсор
tracker-infos-board_type = Основна плата
tracker-infos-network_version = Версія протоколу

## Tracker settings

tracker-settings-back = Повернутися до списку трекерів
tracker-settings-title = Налаштування трекеру
tracker-settings-assignment_section = Призначення
tracker-settings-assignment_section-description = До якої частини тіла призначенний трекер.
tracker-settings-assignment_section-edit = Редагування призначення
tracker-settings-mounting_section = Позиція трекера
tracker-settings-mounting_section-description = Де закріплено трекер?
tracker-settings-mounting_section-edit = Змінити місце розташування
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Ім'я трекера
tracker-settings-name_section-placeholder = Ліва нога NightyBeast
tracker-settings-name_section-label = Ім'я трекера
tracker-settings-forget = Забути трекери
tracker-settings-forget-description = Прибирає трекер із SlimeVR і забороняє йому підключатися до сервера до того, як він буде перезапущений. Конфігурацію трекера не буде втрачено.
tracker-settings-forget-label = Забути трекери

## Dongle settings

dongle-infos-hardware_revision = Ревізія обладнання
dongle-status-disconnected = Відключено
dongle-settings-back = Повернутися до списку трекерів

## Tracker part card info

tracker-part_card-unassigned = Непризначений

## Body assignment menu

body_assignment_menu = Де ви хочете розташувати цей трекер?
body_assignment_menu-description = Виберіть місце, куди потрібно призначити цей трекер. Крім того, ви можете керувати всіма трекерами одночасно, а не по одному.
body_assignment_menu-manage_trackers = Керування всіма трекерами
body_assignment_menu-unassign_tracker = Відв'язати трекер

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Попередження:</b> Трекер шиї може бути смертельно небезпечним, якщо його регулювати занадто щільно,
    Ремінь може скоротити кровообіг до вашої голови!
tracker_selection_menu-neck_warning-done = Я розумію ризики
tracker_selection_menu-neck_warning-cancel = Скасувати

## Mounting menu

mounting_selection_menu-close = Закрити

## Sidebar settings

settings-sidebar-title = Параметри
settings-sidebar-general = Загальні
settings-sidebar-trackers = Трекери
settings-sidebar-interface = Інтерфейс
settings-sidebar-utils = Утиліти
settings-sidebar-appearance = Зовнішність
settings-sidebar-notifications = Повідомлення

## Bone routing settings

settings-routing-hands-warning-cancel = Скасувати

## SteamVR / Monado output settings

settings-driver-enable = Увімкнути

## Tracker mechanics

settings-general-tracker_mechanics-filtering = Фільтрація
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Виберіть тип фільтрації для своїх трекерів.
    Передбачення передбачає рух, а згладжування згладжує рух.
settings-general-tracker_mechanics-filtering-type-none = Без фільтрації
settings-general-tracker_mechanics-filtering-type-none-description = Використовуйте обертання як є. Ніякої фільтрації не зробить.
settings-general-tracker_mechanics-filtering-type-smoothing = Згладжування
settings-general-tracker_mechanics-filtering-type-smoothing-description = Згладжує рухи, але додає деяку затримку.
settings-general-tracker_mechanics-filtering-type-prediction = Передбачення
settings-general-tracker_mechanics-filtering-type-prediction-description = Зменшує затримку і робить рухи більш швидкими, але може посилити тремтіння.
settings-general-tracker_mechanics-save_mounting_reset = Зберегти автоматичне калібрування положення трекерів
settings-general-tracker_mechanics-save_mounting_reset-description =
    Зберігає калібрування положення трекерів на тілі між перезавантаженнями. Корисний
    при носінні костюма, в якому трекери не переміщаються між сесіями. <b>Не рекомендується для звичайних користувачів!</b>
settings-general-tracker_mechanics-save_mounting_reset-enabled-label = Зберегти калібрування положення
settings-stay_aligned-general-label = Загальні
settings-stay_aligned-relaxed_poses-close = Закрити

## Keybinds Page

settings-keybinds_full-reset = Повне скидання
settings-keybinds_yaw-reset = Скинути нахил
settings-keybinds-recorder-modal-cancel-button = Скасувати

## FK/Tracking settings

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
settings-general-fk_settings-arm_fk-back = Назад
settings-general-fk_settings-arm_fk-back-description = Стандартний режим: плечі ззаду, а передпліччя спереду.
settings-general-fk_settings-arm_fk-tpose_up = Т-поза (вгору)
settings-general-fk_settings-arm_fk-tpose_up-description = Очікується, що ваші руки будуть опущені з боків під час повного скидання, та на 90 градусів у сторони під час скидання положення.
settings-general-fk_settings-arm_fk-tpose_down = Т-поза (вниз)
settings-general-fk_settings-arm_fk-tpose_down-description = Очікується, що ваші руки будуть піднятими в сторони на 90 градусів під час повного скидання, та опущені з боків під час скидання положення.
settings-general-fk_settings-arm_fk-forward = Вперед
settings-general-fk_settings-arm_fk-forward-description = Очікується, що ваші руки будуть підняті вперед на 90 градусів. Корисно для вітюбінга.
settings-general-fk_settings-skeleton_settings-ratios = Співвідношення скелета
settings-general-fk_settings-skeleton_settings-ratios-description = Змініть параметри скелета. Можливо, вам доведеться скоригувати пропорції після їхньої зміни.
settings-general-fk_settings-self_localization-title = Режим Мокап

## Gesture control settings (tracker tapping)

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
settings-general-gesture_control-yawResetEnabled = Увімкнути дотик, щоб скинути нахил
settings-general-gesture_control-yawResetDelay = Затримка скидання нахилу
settings-general-gesture_control-yawResetTaps = Дотики для скидання нахилу
settings-general-gesture_control-fullResetEnabled = Увімкнути дотик для повного скидання
settings-general-gesture_control-fullResetDelay = Затримка повного скидання
settings-general-gesture_control-fullResetTaps = Дотики для повного скидання
settings-general-gesture_control-mountingResetEnabled = Увімкнути дотик для скидання положення
settings-general-gesture_control-mountingResetDelay = Затримка скидання положення
settings-general-gesture_control-mountingResetTaps = Дотики для скидання положення
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = Трекери перевищують поріг
settings-general-gesture_control-numberTrackersOverThreshold-description = Збільште це значення, якщо не працює виявлення дотика. Не збільшуйте його вище ніж необхідно для виявлення дотика, оскільки це призведе до більшої кількості помилкових спрацьовувань.

## Appearance settings

settings-interface-appearance = Зовнішність
settings-general-interface-dev_mode = Режим розробника
settings-general-interface-dev_mode-description = Цей режим може бути корисним, якщо вам потрібні поглиблені дані або для взаємодії з підключеними трекерами на більш просунутому рівні.
settings-general-interface-dev_mode-label = Режим розробника
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

## Notification settings

settings-interface-notifications = Повідомлення
settings-general-interface-feedback_sound = Звук зворотного зв'язку
settings-general-interface-feedback_sound-description = Ця опція відтворюватиме звуковий сигнал при спрацьовуванні скидання
settings-general-interface-feedback_sound-label = Звук зворотного зв'язку
settings-general-interface-feedback_sound-volume = Гучність звуку зворотного зв'язку
settings-general-interface-connected_trackers_warning = Попередження про підключені трекери
settings-general-interface-connected_trackers_warning-description = Ця опція відображатиме спливаюче вікно кожного разу, коли ви намагатиметеся вийти зі SlimeVR, якщо підключено один або декілька трекерів. Він нагадує вам вимкнути ваші трекери, коли ви закінчите, щоб зберегти заряд батареї.
settings-general-interface-connected_trackers_warning-label = Попередження про підключені трекери під час виходу

## Behavior settings

settings-general-interface-dev_mode = Режим розробника
settings-general-interface-dev_mode-label = Режим розробника
settings-general-interface-use_tray = Згорнути в системний трей
settings-general-interface-use_tray-description = Дозволяє закрити вікно, не закриваючи сервер SlimeVR, так що ви можете продовжувати використати його, не турбуючись про інтерфейс.
settings-general-interface-use_tray-label = Згорнути в системний трей
settings-general-interface-discord_presence = Ділитися активністю в Discord
settings-general-interface-discord_presence-description = Повідомляє вашому клієнту Discord, що ви використовуєте SlimeVR, а також передає кількість трекерів IMU, які ви використовуєте.
settings-general-interface-discord_presence-label = Ділитися активністю в Discord

## Serial settings

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
settings-serial-save_logs = Зберегти у файл
settings-serial-send_command-warning-ok = Я знаю, що роблю
settings-serial-send_command-warning-cancel = Скасувати

## OSC VRChat settings

settings-osc-vrchat-enable = Увімкнути
settings-osc-vrchat-enable-description = Перемикайте відправку та отримання даних.
settings-osc-vrchat-enable-label = Увімкнути
settings-osc-vrchat-network = Мережеві порти
settings-osc-vrchat-network-port_in =
    .label = Вхідний Порт
    .placeholder = Вхідний Порт (зазвичай: 9001)
settings-osc-vrchat-network-port_out =
    .label = Вихідний Порт
    .placeholder = Вихідний Порт (зазвичай: 9000)
settings-osc-vrchat-network-address = Мережева адреса
settings-osc-vrchat-network-address-description-v1 = Виберіть, за якою адресою надсилати дані. Можна залишити без змін для VRChat.
settings-osc-vrchat-network-address-placeholder = IP-адреса VRChat

## VRChat OSC status

settings-osc-vrchat-status-tracking = Обертання
settings-osc-vrchat-status-badge-error = Помилка

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
settings-osc-vmc-vrm-file_select = Перетягніть модель для використання або <u>знайдіть</u>
settings-osc-vmc-anchor_hip = Якір у стегон
settings-osc-vmc-anchor_hip-label = Якір у стегон
settings-osc-vmc-mirror_tracking = Дзеркальний трекінг
settings-osc-vmc-mirror_tracking-description = Віддзеркалити трекери горизонтально.
settings-osc-vmc-mirror_tracking-label = Дзеркальний трекінг
settings-osc-vmc-status-badge-error = Помилка

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

