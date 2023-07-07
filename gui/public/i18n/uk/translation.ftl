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
tips-tap_setup = Ви можете повільно постукати 2 рази по трекеру, щоб вибрати його, замість того, щоб вибирати його з меню.

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

## Proportions

skeleton_bone-NONE = Нічого
skeleton_bone-HEAD = Зсув голови
skeleton_bone-NECK = Довжина шиї
skeleton_bone-torso_group = Довжина тулуба
skeleton_bone-UPPER_CHEST = Довжина верхньої частини грудей
skeleton_bone-CHEST_OFFSET = Зміщення грудної клітини
skeleton_bone-CHEST = Довжина грудей
skeleton_bone-WAIST = Довжина талії
skeleton_bone-HIP = Довжина стегна
skeleton_bone-HIP_OFFSET = Зміщення стегна
skeleton_bone-HIPS_WIDTH = Ширина стегон
skeleton_bone-leg_group = Довжина ніг
skeleton_bone-UPPER_LEG = Довжина верхньої частини ноги
skeleton_bone-LOWER_LEG = Довжина гомілки
skeleton_bone-FOOT_LENGTH = Довжина стопи
skeleton_bone-FOOT_SHIFT = Зміщення стопи
skeleton_bone-SKELETON_OFFSET = Зміщення скелета
skeleton_bone-SHOULDERS_DISTANCE = Відстань між плечима
skeleton_bone-SHOULDERS_WIDTH = Ширина плечей
skeleton_bone-arm_group = Довжина руки
skeleton_bone-UPPER_ARM = Довжина верхньої частини руки
skeleton_bone-LOWER_ARM = Довжина нижньої частини руки
skeleton_bone-HAND_Y = Відстань рук Y
skeleton_bone-HAND_Z = Відстань руки Z
skeleton_bone-ELBOW_OFFSET = Зміщення ліктя

## Tracker reset buttons

reset-reset_all = Скинути всі пропорції
reset-full = Повне скидання
reset-mounting = Скинути закріплення
reset-yaw = Скинути нахил

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
navbar-mounting = Калібрування закріплення
navbar-onboarding = Майстер налаштування
navbar-settings = Параметри

## Biovision hierarchy recording

bvh-start_recording = Запис BVH
bvh-recording = Запис...

## Tracking pause

tracking-unpaused = Призупинити трекінг
tracking-paused = Продовжити трекінг

## Widget: Overlay settings

widget-overlay = Накладання
widget-overlay-is_visible_label = Показати накладання у SteamVR
widget-overlay-is_mirrored_label = Відображення накладання як дзеркала

## Widget: Drift compensation

widget-drift_compensation-clear = Очистити компенсацію дрейфу

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
widget-imu_visualizer-rotation_raw = Необроблене
widget-imu_visualizer-rotation_preview = Попередній перегляд
widget-imu_visualizer-rotation_hide = Приховати

## Tracker status

tracker-status-none = Немає статусу
tracker-status-busy = Зайнятий
tracker-status-error = Помилка
tracker-status-disconnected = Відключено
tracker-status-occluded = Закрито
tracker-status-ok = OK

## Tracker status columns

tracker-table-column-name = Ім'я
tracker-table-column-type = Тип
tracker-table-column-battery = Батарея
tracker-table-column-ping = Пінг
tracker-table-column-tps = TPS
tracker-table-column-temperature = Темп. °C
tracker-table-column-linear-acceleration = Прискорення X/Y/Z
tracker-table-column-rotation = Обертання X/Y/Z
tracker-table-column-position = Позиція X/Y/Z
tracker-table-column-url = URL

## Tracker rotation

tracker-rotation-front = Спереду
tracker-rotation-left = Зліва
tracker-rotation-right = Справа
tracker-rotation-back = Ззаду

## Tracker information

tracker-infos-manufacturer = Виробник
tracker-infos-display_name = Відображуване ім'я
tracker-infos-custom_name = Персональне ім'я
tracker-infos-url = URL трекера
tracker-infos-version = Версія прошивки
tracker-infos-hardware_rev = Ревізія обладнання
tracker-infos-hardware_identifier = Ідентифікатор обладнання
tracker-infos-imu = IMU Сенсор
tracker-infos-board_type = Основна плата

## Tracker settings

tracker-settings-back = Повернутися до списку трекерів
tracker-settings-title = Налаштування трекеру
tracker-settings-assignment_section = Призначення
tracker-settings-assignment_section-description = До якої частини тіла призначенний трекер.
tracker-settings-assignment_section-edit = Редагування призначення
tracker-settings-mounting_section = Позиція закріпу
tracker-settings-mounting_section-description = Де закріплено трекер?
tracker-settings-mounting_section-edit = Редагувати закріплення
tracker-settings-drift_compensation_section = Дозволити компенсацію дрейфу
tracker-settings-drift_compensation_section-description = Чи повинен цей трекер компенсувати свій дрейф, коли включена компенсація дрейфу?
tracker-settings-drift_compensation_section-edit = Дозволити компенсацію дрейфу
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Ім'я трекера
tracker-settings-name_section-description = Дайте йому миле прізвисько °^°
tracker-settings-name_section-placeholder = Ліва нога NightyBeast

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

mounting_selection_menu = Де ви хочете, щоб був цей трекер?
mounting_selection_menu-close = Закрити

## Sidebar settings

settings-sidebar-title = Параметри
settings-sidebar-general = Загальні
settings-sidebar-tracker_mechanics = Механіки трекера
settings-sidebar-fk_settings = Налаштування відстеження
settings-sidebar-gesture_control = Управління жестами
settings-sidebar-interface = Інтерфейс
settings-sidebar-osc_router = OSC роутер
settings-sidebar-osc_trackers = VRChat OSC трекери
settings-sidebar-utils = Утиліти
settings-sidebar-serial = Послідовна консоль

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
settings-general-steamvr-trackers-feet = Ступні
settings-general-steamvr-trackers-knees = Коліна
settings-general-steamvr-trackers-elbows = Лікті
settings-general-steamvr-trackers-hands = Руки

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
settings-general-tracker_mechanics-drift_compensation = Компенсація дрейфу
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Компенсує дрейф нахилу IMU, застосовуючи зворотне обертання.
    Змініть суму компенсації та до того, скільки скидань враховано.
settings-general-tracker_mechanics-drift_compensation-enabled-label = Компенсація дрейфу
settings-general-tracker_mechanics-drift_compensation-amount-label = Сума компенсації
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Використання до x останніх скидань

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
settings-general-fk_settings-arm_fk = Трекінг руки
settings-general-fk_settings-arm_fk-description = Намагатися відстежувати руки за допомогою шолома, навіть якщо є інформація о позиції руки
settings-general-fk_settings-arm_fk-force_arms = Відстеження рук з шолома
settings-general-fk_settings-skeleton_settings = Налаштування скелета
settings-general-fk_settings-skeleton_settings-description = Увімкніть або вимкніть налаштування скелета. Рекомендується залишити їх увімкненими.
settings-general-fk_settings-skeleton_settings-extended_spine = Подовжений хребет
settings-general-fk_settings-skeleton_settings-extended_pelvis = Розширений таз
settings-general-fk_settings-skeleton_settings-extended_knees = Подовжене коліно
settings-general-fk_settings-vive_emulation-title = Емуляція Vive
settings-general-fk_settings-vive_emulation-description = Емуляція проблем з трекером талії, які є у трекерів Vive. Це жарт і погіршує відстеження.
settings-general-fk_settings-vive_emulation-label = Увімкнути емуляцію Vive

## Gesture control settings (tracker tapping)

settings-general-gesture_control = Управління жестами
settings-general-gesture_control-subtitle = Скидання на основі дотику
settings-general-gesture_control-description = Дозволяє запускати скидання, торкнувшись трекера. Трекер найвищий на вашому тулубі використовується для скидання нахилу, трекер найвищий на лівій нозі використовується для повного скидання, а трекер найвищий на правій нозі використовується для скидання закріплення. Слід зазначити, що дотики повинні відбутися протягом 0,6 секунди для реєстрації.
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
settings-general-gesture_control-mountingResetEnabled = Увімкнути дотик для скидання прив'язки
settings-general-gesture_control-mountingResetDelay = Затримка скидання прив'язки
settings-general-gesture_control-mountingResetTaps = Дотики для скидання прив'язки

## Interface settings

settings-general-interface = Інтерфейс
settings-general-interface-dev_mode = Режим розробника
settings-general-interface-dev_mode-description = Цей режим може бути корисним, якщо вам потрібні поглиблені дані або для взаємодії з підключеними трекерами на більш просунутому рівні.
settings-general-interface-dev_mode-label = Режим розробника
settings-general-interface-serial_detection = Виявлення послідовного пристрою
settings-general-interface-serial_detection-description = Цей параметр відображатиме спливаюче вікно кожного разу, коли ви підключаєте новий послідовний пристрій, який може бути трекером. Це допомагає покращити процес налаштування трекера.
settings-general-interface-serial_detection-label = Виявлення послідовного пристрою
settings-general-interface-feedback_sound = Звук зворотного зв'язку
settings-general-interface-feedback_sound-description = Ця опція відтворюватиме звуковий сигнал при спрацьовуванні скидання
settings-general-interface-feedback_sound-label = Звук зворотного зв'язку
settings-general-interface-feedback_sound-volume = Гучність звуку зворотного зв'язку
settings-general-interface-theme = Варіація оформлення
settings-general-interface-lang = Виберіть мову
settings-general-interface-lang-description = Змініть мову за замовчуванням, яку ви хочете використовувати.
settings-general-interface-lang-placeholder = Виберіть мову для використання

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
settings-serial-get_infos = Отримати інформацію
settings-serial-serial_select = Вибір послідовного порту
settings-serial-auto_dropdown_item = Автоматично

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
settings-osc-vrchat-description =
    Змініть специфічні для VRChat налаштування для отримання даних шолому та надсилання
    даних трекерів для FBT без SteamVR (наприклад, автономний Quest).
settings-osc-vrchat-enable = Увімкнути
settings-osc-vrchat-enable-description = Перемикайте відправку та отримання даних.
settings-osc-vrchat-enable-label = Увімкнути
settings-osc-vrchat-network = Мережеві порти
settings-osc-vrchat-network-description = Встановіть порти для прослуховування і відправки даних в VRChat.
settings-osc-vrchat-network-port_in =
    .label = Вхідний Порт
    .placeholder = Вхідний Порт (зазвичай: 9001)
settings-osc-vrchat-network-port_out =
    .label = Вихідний Порт
    .placeholder = Вихідний Порт (зазвичай: 9000)
settings-osc-vrchat-network-address = Мережева адреса
settings-osc-vrchat-network-address-description = Виберіть, за якою адресою надсилати дані до VRChat (перевірте налаштування Wi-Fi на своєму пристрої).
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
settings-osc-vmc-vrm-model_unloaded = Модель не завантажена
settings-osc-vmc-vrm-model_loaded =
    { $titled ->
        [true] Модель завантажена: { $name }
       *[other] Завантажена модель без назви
    }
settings-osc-vmc-vrm-file_select = Перетягніть модель для використання або <u>знайдіть</u>
settings-osc-vmc-anchor_hip = Якір у стегон
settings-osc-vmc-anchor_hip-description = Закріпіть стеження на стегнах, корисно для сидячих VTubing. Якщо вимкнено, завантажте модель VRM.
settings-osc-vmc-anchor_hip-label = Якір у стегон

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

## Wi-Fi setup

onboarding-wifi_creds-back = Повернутися до вступу
onboarding-wifi_creds = Введіть дані Wi-Fi
# This cares about multilines
onboarding-wifi_creds-description =
    Трекери використовуватимуть ці дані для бездротового підключення.
    Будь ласка, використовуйте дані, до яких ви зараз підключені.
onboarding-wifi_creds-skip = Пропустити налаштування Wi-Fi
onboarding-wifi_creds-submit = Підтвердити!
onboarding-wifi_creds-ssid =
    .label = Назва Wi-Fi
    .placeholder = Введіть назву Wi-Fi
onboarding-wifi_creds-password =
    .label = Пароль
    .placeholder = Введіть Пароль

## Mounting setup

onboarding-reset_tutorial-back = Повернутися до розділу Калібрування прив'язки
onboarding-reset_tutorial = Інструкція по скиданню
onboarding-reset_tutorial-explanation = Коли ви використовуєте свої трекери, вони можуть вийти з вирівнювання через дрейф нахилу IMU або тому, що ви могли їх фізично перемістити. Це можна виправити кількома способами.
onboarding-reset_tutorial-skip = Пропустити крок
# Cares about multiline
onboarding-reset_tutorial-0 =
    Торкніться { $taps } виділеного трекера, щоб запустити скидання нахилу.
    
    Це змусить трекери дивитися в тому ж напрямку, що і ваш шолом.
# Cares about multiline
onboarding-reset_tutorial-1 =
    Торкніться { $taps } виділеного трекера, щоб ініціювати повне скидання.
    
    Для цього потрібно стояти (i-поза). Існує затримка 3 секунди (налаштовується), перш ніж це дійсно станеться.
    Це повністю скидає положення та обертання всіх ваших трекерів. Це має вирішити більшість проблем.
# Cares about multiline
onboarding-reset_tutorial-2 =
    Торкніться { $taps } виділеного елемента стеження, щоб активувати скидання прив'язки.
    
    Скидання прив'язки допомагає в тому, як трекери насправді надягнені на вас, тому, якщо ви випадково перемістили їх і змінили місце прикріплення на велику кількість, це допоможе.
    
    Вам потрібно бути в позі, ніби ви катаєтеся на лижах, як показано на майстрі автоматичної прив'язки, і у вас є 3-секундна затримка (налаштовується), перш ніж вона спрацює.

## Setup start

onboarding-home = Ласкаво просимо до SlimeVR
onboarding-home-start = Давайте налаштуємося!

## Enter VR part of setup

onboarding-enter_vr-back = Повернутися до Прив'язки трекерів
onboarding-enter_vr-title = Час вступати у VR!
onboarding-enter_vr-description = Увімкніть усі свої трекери, а потім вступіть у VR!
onboarding-enter_vr-ready = Я готовий

## Setup done

onboarding-done-title = Все готово!
onboarding-done-description = Насолоджуйтесь досвідом трекінгу всього тіла
onboarding-done-close = Закрити налаштування

## Tracker connection setup

onboarding-connect_tracker-back = Повернутися до даних Wi-Fi
onboarding-connect_tracker-title = Підключіть трекери
onboarding-connect_tracker-description-p0 = Тепер перейдемо до найцікавішого, з'єднання усіх трекерів!
onboarding-connect_tracker-description-p1 = Просто підключіть все, що ще не підключено, через USB-порт.
onboarding-connect_tracker-issue-serial = У мене виникли проблеми з підключенням!
onboarding-connect_tracker-usb = USB-трекер
onboarding-connect_tracker-connection_status-none = Шукаємо трекери
onboarding-connect_tracker-connection_status-serial_init = Підключення до послідовного пристрою
onboarding-connect_tracker-connection_status-provisioning = Надсилання даних Wi-Fi
onboarding-connect_tracker-connection_status-connecting = Спроба підключення до Wi-Fi
onboarding-connect_tracker-connection_status-looking_for_server = Шукаю сервер
onboarding-connect_tracker-connection_status-connection_error = Не вдається підключитися до мережі Wi-Fi
onboarding-connect_tracker-connection_status-could_not_find_server = Не вдалося знайти сервер
onboarding-connect_tracker-connection_status-done = Підключено до сервера
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
onboarding-calibration_tutorial-description = Кожен раз, коли ви вмикаєте трекери, їм потрібно на мить відпочити на рівній поверхні для калібрування. Давайте зробимо те ж саме, натиснувши кнопку "{ onboarding-calibration_tutorial-calibrate }", <b>не переміщайте їх!</b>
onboarding-calibration_tutorial-calibrate = Я поклав свої трекери на стіл
onboarding-calibration_tutorial-status-waiting = Чекаємо на Вас
onboarding-calibration_tutorial-status-calibrating = Калібрування
onboarding-calibration_tutorial-status-success = Добре!
onboarding-calibration_tutorial-status-error = Трекер переміщено

## Tracker assignment tutorial

onboarding-assignment_tutorial = Як підготувати Slime трекер перед його надяганням
onboarding-assignment_tutorial-first_step = 1. Розмістіть наліпку з частиною тіла (якщо вона у вас є) на трекері відповідно до вашого вибору
# This text has a character limit of around 11 characters, so please keep it short
onboarding-assignment_tutorial-sticker = Наліпка
onboarding-assignment_tutorial-second_step = 2. Прикріпіть ремінь до трекера, зберігаючи гачок і петльову сторону лицьової сторони ремінця в такій орієнтації:
onboarding-assignment_tutorial-second_step-continuation = Гачок і петльова сторона для подовжувача повинні бути в такій орієнтації:
onboarding-assignment_tutorial-done = Я наклеїв наліпки і закріпив ремінці!

## Tracker assignment setup

onboarding-assign_trackers-back = Повернутися до даних Wi-Fi
onboarding-assign_trackers-title = Призначити трекери
onboarding-assign_trackers-description = Давайте виберемо, який трекер куди йде. Натисніть на місце, де ви хочете розмістити трекер
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

## Tracker mounting method choose

onboarding-choose_mounting = Який метод калібрування закріплення використовувати?
# Multiline text
onboarding-choose_mounting-description = Орієнтація кріплення коригується для розміщення трекерів на вашому тілі.
onboarding-choose_mounting-auto_mounting = Автоматична прив'язка
# Italized text
onboarding-choose_mounting-auto_mounting-label = Експериментальний
onboarding-choose_mounting-auto_mounting-description = Це автоматично визначить напрямки прив'язки для всіх ваших трекерів з 2 поз
onboarding-choose_mounting-manual_mounting = Ручна прив'язка
# Italized text
onboarding-choose_mounting-manual_mounting-label = Рекомендується
onboarding-choose_mounting-manual_mounting-description = Це дозволить вибрати напрямок прив'язки вручну для кожного трекера

## Tracker manual mounting setup

onboarding-manual_mounting-back = Повернутися до VR
onboarding-manual_mounting = Ручне закріплення
onboarding-manual_mounting-description = Натисніть на кожен трекер і виберіть, в який бік вони прив'язані
onboarding-manual_mounting-auto_mounting = Автоматична прив'язка
onboarding-manual_mounting-next = Наступний крок

## Tracker automatic mounting setup

onboarding-automatic_mounting-back = Повернутися до VR
onboarding-automatic_mounting-next = Наступний крок
onboarding-automatic_mounting-prev_step = Попередній крок
onboarding-automatic_mounting-done-restart = Спробуйте знову

## Tracker proportions method choose


## Tracker manual proportions setup


## Tracker automatic proportions setup

onboarding-automatic_proportions-prev_step = Попередній крок
onboarding-automatic_proportions-requirements-next = Я ознайомився з вимогами
onboarding-automatic_proportions-start_recording-title = Приготуйтеся рухатися
onboarding-automatic_proportions-start_recording-next = Почати запис
onboarding-automatic_proportions-recording-title = ЗАПИС
onboarding-automatic_proportions-recording-description-p1 = Повторюйте рухи, показані нижче:
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 20)
onboarding-automatic_proportions-recording-timer =
    { $time ->
        [one] залишилась { $time }  секунда
        [few] залишилось { $time } секунди
        [many] залишилось { $time } секунд
       *[other] залишилось { $time } секунд
    }
onboarding-automatic_proportions-verify_results-title = Перевірити результати
onboarding-automatic_proportions-verify_results-processing = Обробка результату

## Home


## Status system

