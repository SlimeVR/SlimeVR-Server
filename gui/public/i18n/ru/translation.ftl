### SlimeVR complete GUI translations


# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Подключение к серверу
websocket-connection_lost = Потеряно соединение с сервером.  Переподключение...

## Tips

tips-find_tracker = Не уверены, какой это трекер? Встряхните его, и трекер выделится в списке.
tips-do_not_move_heels = Убедитесь, что ваши пятки не двигаются во время записи!
tips-file_select = Выберите или перетащите файлы для использования <u>выбрать</u>.

## Body parts

body_part-NONE = Не привязано
body_part-HEAD = Голова
body_part-NECK = Шея
body_part-RIGHT_SHOULDER = Правое плечо
body_part-RIGHT_UPPER_ARM = Правое предплечье
body_part-RIGHT_LOWER_ARM = Правое нижнее предплечье
body_part-RIGHT_HAND = Правая рука
body_part-RIGHT_UPPER_LEG = Правое бедро
body_part-RIGHT_LOWER_LEG = Правая голень
body_part-RIGHT_FOOT = Правая ступня
body_part-CHEST = Грудь
body_part-WAIST = Талия
body_part-HIP = Таз
body_part-LEFT_SHOULDER = Левое плечо
body_part-LEFT_UPPER_ARM = Левое предплечье
body_part-LEFT_LOWER_ARM = Левое нижнее предплечье
body_part-LEFT_HAND = Левая рука
body_part-LEFT_UPPER_LEG = Левое бедро
body_part-LEFT_LOWER_LEG = Левая голень
body_part-LEFT_FOOT = Левая ступня

## Proportions

skeleton_bone-NONE = Пусто
skeleton_bone-HEAD = Голова
skeleton_bone-NECK = Длина шеи
skeleton_bone-torso_group = Длина туловища
skeleton_bone-CHEST = Длина груди
skeleton_bone-CHEST_OFFSET = Смещение груди
skeleton_bone-WAIST = Длина талии
skeleton_bone-HIP = Длина таза
skeleton_bone-HIP_OFFSET = Смещение таза
skeleton_bone-HIPS_WIDTH = Ширина бедер
skeleton_bone-leg_group = Длина ноги
skeleton_bone-UPPER_LEG = Длина бедра
skeleton_bone-LOWER_LEG = Длина голени
skeleton_bone-FOOT_LENGTH = Длинна стопы
skeleton_bone-FOOT_SHIFT = Смещение стопы
skeleton_bone-SKELETON_OFFSET = Смещение скелета
skeleton_bone-SHOULDERS_DISTANCE = Дистанция плеч
skeleton_bone-SHOULDERS_WIDTH = Ширина плеч
skeleton_bone-arm_group = Длина руки
skeleton_bone-UPPER_ARM = Длинна предплечья
skeleton_bone-LOWER_ARM = Длинна нижнего предплечья
skeleton_bone-HAND_Y = Расстояние от руки Y
skeleton_bone-HAND_Z = Расстояние от руки Z
skeleton_bone-ELBOW_OFFSET = Смещение локтя

## Tracker reset buttons

reset-reset_all = Сбросить все пропорции
reset-full = Полный сброс
reset-mounting = Сбросить крепление
reset-yaw = Горизонтальный сброс

## Serial detection stuff

serial_detection-new_device-p0 = Обнаружено новое устройство!
serial_detection-new_device-p1 = Заполните данные вашего Wi-Fi!
serial_detection-new_device-p2 = Пожалуйста, выберите, что вы хотите с ним сделать
serial_detection-open_wifi = Подключиться к Wi-Fi
serial_detection-open_serial = Открыть консоль
serial_detection-submit = Отправить!
serial_detection-close = Закрыть

## Navigation bar

navbar-home = Дом
navbar-body_proportions = Пропорции тела
navbar-trackers_assign = Назначение трекера
navbar-mounting = Калибровка крепления
navbar-onboarding = Мастер настройки
navbar-settings = Настройки

## Bounding volume hierarchy recording

bvh-start_recording = Запись BVH
bvh-recording = Запись...

## Widget: Overlay settings

widget-overlay = Оверлей
widget-overlay-is_visible_label = Показывать оверлей в SteamVR
widget-overlay-is_mirrored_label = Показывать оверлей как зеркало

## Widget: Drift compensation

widget-drift_compensation-clear = Очистить компенсацию дрифта

## Widget: Developer settings

widget-developer_mode = Режим разработчика
widget-developer_mode-high_contrast = Высокая контрастность
widget-developer_mode-precise_rotation = Точное вращение
widget-developer_mode-fast_data_feed = Быстрый поток данных
widget-developer_mode-filter_slimes_and_hmd = Фильтровать SlimeVR и HMD
widget-developer_mode-sort_by_name = Сортировка по имени
widget-developer_mode-raw_slime_rotation = Вращение без обработки
widget-developer_mode-more_info = Дополнительная информация

## Widget: IMU Visualizer

widget-imu_visualizer = Вращение
widget-imu_visualizer-rotation_raw = RAW
widget-imu_visualizer-rotation_preview = Предпросмотр

## Tracker status

tracker-status-none = Без Статуса
tracker-status-busy = Занят
tracker-status-error = Ошибка
tracker-status-disconnected = Отключен
tracker-status-occluded = Закрыт
tracker-status-ok = ОК

## Tracker status columns

tracker-table-column-name = Имя
tracker-table-column-type = Тип
tracker-table-column-battery = Батарея
tracker-table-column-ping = Пинг
tracker-table-column-tps = TPS
tracker-table-column-temperature = Темп. °C
tracker-table-column-linear-acceleration = Ускорение. X/Y/Z
tracker-table-column-rotation = Поворот X/Y/Z
tracker-table-column-position = Положение X/Y/Z
tracker-table-column-url = URL

## Tracker rotation

tracker-rotation-front = Спереди
tracker-rotation-left = Слева
tracker-rotation-right = Справа
tracker-rotation-back = Сзади

## Tracker information

tracker-infos-manufacturer = Производитель
tracker-infos-display_name = Отображаемое имя
tracker-infos-custom_name = Свое имя
tracker-infos-url = URL трекера
tracker-infos-version = Версия прошивки
tracker-infos-hardware_rev = Ревизия устройства

## Tracker settings

tracker-settings-back = Вернуться к списку трекеров
tracker-settings-title = Настройки трекера
tracker-settings-assignment_section = Привязка
tracker-settings-assignment_section-description = К какой части тела привязан трекер.
tracker-settings-assignment_section-edit = Изменить привязку
tracker-settings-mounting_section = Положение крепления
tracker-settings-mounting_section-description = Где прикреплен трекер?
tracker-settings-mounting_section-edit = Изменить прикрепление
tracker-settings-drift_compensation_section = Разрешить компенсацию дрифта
tracker-settings-drift_compensation_section-description = Должен ли этот трекер компенсировать свой дрифт, когда включена компенсация дрифта?
tracker-settings-drift_compensation_section-edit = Разрешить компенсацию дрифта
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Имя трекера
tracker-settings-name_section-description = Дай ему милое имя :)
tracker-settings-name_section-placeholder = Левая нога NightyBeast'а

## Tracker part card info

tracker-part_card-no_name = Нет имени
tracker-part_card-unassigned = Не привязан

## Body assignment menu

body_assignment_menu = Где вы хотите, чтобы был этот трекер?
body_assignment_menu-description = Выберите местоположение, куда вы хотите назначить этот трекер. В качестве альтернативы вы можете выбрать управление всеми трекерами сразу, а не по одному.
body_assignment_menu-show_advanced_locations = Показать дополнительные места привязки
body_assignment_menu-manage_trackers = Настроить все трекеры
body_assignment_menu-unassign_tracker = Отвязать трекер

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = Какой трекер ты хочешь привязать к
tracker_selection_menu-NONE = Какой трекер вы хотите оставить неназначенным?
tracker_selection_menu-HEAD = { -tracker_selection-part } голове?
tracker_selection_menu-NECK = { -tracker_selection-part } шее?
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part } правому плечу?
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part } правому предплечью?
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part } правому нижнему предплечью?
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part } провой руке?
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part } правому бедру?
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part } правой лодышке?
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part } правой ступне?
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part } правому контроллеру?
tracker_selection_menu-CHEST = { -tracker_selection-part } груди?
tracker_selection_menu-WAIST = { -tracker_selection-part } талии?
tracker_selection_menu-HIP = { -tracker_selection-part } тазу?
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part } левому плечу?
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part } левому предплечью?
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part } левому нижнему предплечью?
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part } левой руке?
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part } левому бедру?
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part } левой лодышке?
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part } левой ступне?
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part } левому контроллеру?
tracker_selection_menu-unassigned = Не привязанные трекеры
tracker_selection_menu-assigned = Привязанные трекеры
tracker_selection_menu-dont_assign = Не привязывать
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Внимание:</b> Трекер шеи может быть смертельно опасен, если его отрегулировать слишком туго,
    ремешок может нарушить кровообращение в вашей голове!
tracker_selection_menu-neck_warning-done = Я понимаю риски
tracker_selection_menu-neck_warning-cancel = Отмена

## Mounting menu

mounting_selection_menu = Где ты хочешь чтобы этот трекер был?
mounting_selection_menu-close = Закрыть

## Sidebar settings

settings-sidebar-title = Настройки
settings-sidebar-general = Общие
settings-sidebar-tracker_mechanics = Механики трекеров
settings-sidebar-fk_settings = Настройки трекеров
settings-sidebar-gesture_control = Настройки жестов
settings-sidebar-interface = Интерфейс
settings-sidebar-osc_router = OSC роутер
settings-sidebar-utils = Утилиты
settings-sidebar-serial = Консоль

## SteamVR settings

settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = SteamVR трекеры
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    Включите или отключите определенные трекеры SteamVR.
    Полезно для игр или приложений, которые поддерживают только определенные трекеры.
settings-general-steamvr-trackers-waist = Талия
settings-general-steamvr-trackers-chest = Грудь
settings-general-steamvr-trackers-feet = Ступни
settings-general-steamvr-trackers-knees = Колени
settings-general-steamvr-trackers-elbows = Локти
settings-general-steamvr-trackers-hands = Руки

## Tracker mechanics

settings-general-tracker_mechanics = Механики трекеров
settings-general-tracker_mechanics-filtering = Фильтр
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Выберите тип фильтрации для ваших трекеров.
    Прогнозирование предсказывает движение, в то время как сглаживание сглаживает движение.
settings-general-tracker_mechanics-filtering-type = Тип фильтра
settings-general-tracker_mechanics-filtering-type-none = Нет фильтра
settings-general-tracker_mechanics-filtering-type-none-description = Используется вращение как есть. Нет никакой фильтрации.
settings-general-tracker_mechanics-filtering-type-smoothing = Сглаживание
settings-general-tracker_mechanics-filtering-type-smoothing-description = Сглаживает движения, но добавляет некоторую задержку.
settings-general-tracker_mechanics-filtering-type-prediction = Предсказывание
settings-general-tracker_mechanics-filtering-type-prediction-description = Уменьшает задержку и делает движения более быстрыми, но может увеличить дрожание.
settings-general-tracker_mechanics-filtering-amount = Количество
settings-general-tracker_mechanics-drift_compensation = Компенсация дрифта
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Компенсирует дрифт по рысканию IMU путем применения обратного вращения.
    Измените размер компенсации и до скольких сбросов учитывается.
settings-general-tracker_mechanics-drift_compensation-enabled-label = Компенсация дрифта
settings-general-tracker_mechanics-drift_compensation-amount-label = Кол-во компенсации
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Используйте до x последних сбросов

## FK/Tracking settings

settings-general-fk_settings = Настройки трекеров
# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Привязка к полу
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Коррекция скольжения
settings-general-fk_settings-leg_tweak-toe_snap = Коррекция пальцев ног
settings-general-fk_settings-leg_tweak-foot_plant = Коррекция стоп
settings-general-fk_settings-leg_tweak-skating_correction-amount = Сила коррекции скольжения
settings-general-fk_settings-leg_tweak-skating_correction-description = Сила коррекции скольжения корректирует катание на коньках, но может снизить точность определенных моделей движения. При включении обязательно выполните полный сброс и повторную калибровку в игре.
settings-general-fk_settings-leg_tweak-floor_clip-description = Привязка к полу может уменьшить или даже полностью исключить прохождение через пол. При включении обязательно выполните полный сброс и повторную калибровку в игре.
settings-general-fk_settings-leg_tweak-toe_snap-description = Toe-snap пытается угадать вращение ваших ступней, если трекеры для них не используются.
settings-general-fk_settings-leg_tweak-foot_plant-description = Foot-Plant поворачивает ваши ступни так, чтобы они были параллельны земле при контакте.
settings-general-fk_settings-leg_fk = Трекинг ноги
settings-general-fk_settings-arm_fk = Трекинг руки
settings-general-fk_settings-arm_fk-description = Измените способ отслеживания рук.
settings-general-fk_settings-arm_fk-force_arms = Руки от HMD
settings-general-fk_settings-skeleton_settings = Настройки скелета
settings-general-fk_settings-skeleton_settings-description = Включите или выключите настройки скелета. Рекомендуется оставить их включенными.
settings-general-fk_settings-skeleton_settings-extended_spine = Удлиненный позвоночник
settings-general-fk_settings-skeleton_settings-extended_pelvis = Расширенный таз
settings-general-fk_settings-skeleton_settings-extended_knees = Вытянутое колено
settings-general-fk_settings-vive_emulation-title = Эмуляция Vive
settings-general-fk_settings-vive_emulation-description = Имитируйте проблемы с отслеживанием талии, которые возникают у трекеров Vive. Это шутка, и она ухудшает отслеживание.
settings-general-fk_settings-vive_emulation-label = Включить эмуляцию Vive

## Gesture control settings (tracker tapping)

settings-general-gesture_control = Контроль жестами
settings-general-gesture_control-subtitle = Сброс после нажатия
settings-general-gesture_control-description = Позволяет запускать сброс настроек нажатием на трекер. Трекер, расположенный выше всего на вашем торсе, используется для быстрого сброса, трекер, расположенный выше всего на вашей левой ноге, используется для сброса, а трекер, расположенный выше всего на вашей правой ноге, используется для сброса установок. Следует отметить, что для регистрации нажатия должны происходить в течение 0,6 секунды.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
        [one] { $amount } нажатие
        [few] { $amount } нажатия
        [many] { $amount } нажатий
       *[other] { $amount } нажатий
    }
settings-general-gesture_control-yawResetEnabled = Включить нажатие для сброса
settings-general-gesture_control-yawResetDelay = Задержка сброса
settings-general-gesture_control-yawResetTaps = Нажатия для сброса
settings-general-gesture_control-fullResetEnabled = Включить нажатия для полного сброса
settings-general-gesture_control-fullResetDelay = Задержка полного сброса
settings-general-gesture_control-fullResetTaps = Нажатия для полного сброса
settings-general-gesture_control-mountingResetEnabled = Включить нажатия для сброса крепления
settings-general-gesture_control-mountingResetDelay = Задержка сброса крепления
settings-general-gesture_control-mountingResetTaps = Нажатия для сброса крепления

## Interface settings

settings-general-interface = Интерфейс
settings-general-interface-dev_mode = Режим разработчика
settings-general-interface-dev_mode-description = Этот режим может быть полезен, если вам нужны подробные данные или для взаимодействия с подключенными трекерами на более продвинутом уровне.
settings-general-interface-dev_mode-label = Режим разработчика
settings-general-interface-serial_detection = Обнаружение серийного устройства
settings-general-interface-serial_detection-description = Эта опция будет показывать всплывающее окно каждый раз, когда вы подключаете новое серийное устройство, которое может быть трекером. Это помогает улучшить процесс настройки трекера.
settings-general-interface-serial_detection-label = Обнаружение серийного устройства
settings-general-interface-feedback_sound = Звук обратной связи
settings-general-interface-feedback_sound-description = Эта опция будет воспроизводить звук при срабатывании сброса.
settings-general-interface-feedback_sound-label = Звук обратной связи
settings-general-interface-feedback_sound-volume = Громкость реакции интерфейса на косание треккера
settings-general-interface-theme = Варианты оформления
settings-general-interface-lang = Выбрать язык
settings-general-interface-lang-description = Измените язык по умолчанию, который вы хотите использовать.
settings-general-interface-lang-placeholder = Выберите язык для использования

## Serial settings

settings-serial = Серийная консоль
# This cares about multilines
settings-serial-description =
    Это оперативный информационный канал для серийной связи.
     Может быть полезно, если вам нужно знать, что прошивка работает неправильно.
settings-serial-connection_lost = Соединение с серийным портом потеряно, повторное подключение...
settings-serial-reboot = Перезагрузить
settings-serial-factory_reset = Полный сброс
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    </b>Предупреждение:</b> Это приведет к полному сбросу настроек трекера.
    Это означает, что Wi-Fi и настройки калибровки <b>будут потеряны!</b>
settings-serial-factory_reset-warning-ok = Я знаю, что я делаю
settings-serial-factory_reset-warning-cancel = Отмена
settings-serial-get_infos = Получить информацию
settings-serial-serial_select = Выбрать серийный порт
settings-serial-auto_dropdown_item = Авто

## OSC router settings

settings-osc-router = OSC роутер
# This cares about multilines
settings-osc-router-description =
    Пересылать OSC-сообщения из другой программы.
    Полезно для использования другой программы OSC, например, с VRChat.
settings-osc-router-enable = Включить
settings-osc-router-enable-description = Включить/отключить переадресацию сообщений.
settings-osc-router-enable-label = Включить
settings-osc-router-network = Порты сети
# This cares about multilines
settings-osc-router-network-description =
    Установите порты для прослушивания и отправки данных.
    Они могут быть такими же, как и другие порты, используемые на сервере SlimeVR.
settings-osc-router-network-port_in =
    .label = Порт Вход
    .placeholder = Порт Вход (default: 9002)
settings-osc-router-network-port_out =
    .label = Порт выход
    .placeholder = Порт выход (default: 9000)
settings-osc-router-network-address = Адрес сети
settings-osc-router-network-address-description = Задайте адрес для отправки данных.
settings-osc-router-network-address-placeholder = IPV4 адрес

## OSC VRChat settings

settings-osc-vrchat = VRChat OSC Трекеры
# This cares about multilines
settings-osc-vrchat-description =
    Измените настройки, специфичные для VRChat, чтобы получать данные HMD и отправлять
    данные трекеров для FBT (работает с Quest).
settings-osc-vrchat-enable = Включить
settings-osc-vrchat-enable-description = Переключайте отправку и получение данных.
settings-osc-vrchat-enable-label = Включить
settings-osc-vrchat-network = Порты сети
settings-osc-vrchat-network-description = Установите порты для прослушивания и отправки данных в VRChat.
settings-osc-vrchat-network-port_in =
    .label = Порт вход
    .placeholder = Порт вход (default: 9001)
settings-osc-vrchat-network-port_out =
    .label = Порт выход
    .placeholder = Порт выход (default: 9000)
settings-osc-vrchat-network-address = Адрес сети
settings-osc-vrchat-network-address-description = Выберите, на какой адрес отправлять данные в VRChat (проверьте настройки Wi-Fi на вашем устройстве).
settings-osc-vrchat-network-address-placeholder = VRChat ip адрес
settings-osc-vrchat-network-trackers = Трекеры
settings-osc-vrchat-network-trackers-description = Переключите отправку определенных трекеров через OSC.
settings-osc-vrchat-network-trackers-chest = Грудь
settings-osc-vrchat-network-trackers-knees = Колени
settings-osc-vrchat-network-trackers-feet = Ступни
settings-osc-vrchat-network-trackers-elbows = Локти

## VMC OSC settings

settings-osc-vmc = Виртуальный захват движения
# This cares about multilines
settings-osc-vmc-description =
    Измените настройки, специфичные для протокола VMC (Virtual Motion Capture)
    , чтобы отправлять данные о костях SlimeVR и получать данные о костях из других приложений.
settings-osc-vmc-enable = Включить
settings-osc-vmc-enable-description = Включить/отключить отправку и получение данных.
settings-osc-vmc-enable-label = Включить
settings-osc-vmc-network = Сетевые порты
settings-osc-vmc-network-description = Установите порты для прослушивания и отправки данных через VMC
settings-osc-vmc-network-port_in =
    .label = Порт Вход
    .placeholder = Порт Вход (по умолчанию: 39540)
settings-osc-vmc-network-port_out =
    .label = Порт Выход
    .placeholder = Порт Выход (по умолчанию 39539)
settings-osc-vmc-network-address = Адрес сети
settings-osc-vmc-network-address-description = Выберите, на какой адрес отправлять данные через VMC
settings-osc-vmc-network-address-placeholder = IPV4 адрес
settings-osc-vmc-vrm = VRM Модель
settings-osc-vmc-vrm-description = Загрузите модель VRM, чтобы обеспечить возможность крепления на голове и обеспечить большую совместимость с другими приложениями
settings-osc-vmc-vrm-model_unloaded = Модель не загружена
settings-osc-vmc-vrm-model_loaded =
    { $titled ->
        [true] Загруженная модель: { $name }
       *[other] Загружена модель без названия
    }
settings-osc-vmc-vrm-file_select = Перетащите модель для использования, или <u>выбрать</u>
settings-osc-vmc-anchor_hip = Привязать к бедрам
settings-osc-vmc-anchor_hip-description = Привязать трекинг к бедрам, полезно для сидячего VTubing'а. Если выключено, загрузите VRM модель.
settings-osc-vmc-anchor_hip-label = Привязать к бедрам

## Setup/onboarding menu

onboarding-skip = Пропустить установку
onboarding-continue = Продолжить
onboarding-wip = В разработке
onboarding-previous_step = Предыдущий шаг
onboarding-setup_warning =
    <b>Предупреждение.</b> Для правильного отслеживания требуется первоначальная настройка,
    это необходимо, если вы впервые используете SlimeVR.
onboarding-setup_warning-skip = Пропустить настройку

## Wi-Fi setup

onboarding-wifi_creds-back = Вернуться к введению
onboarding-wifi_creds = Вставьте данные Wi-Fi
# This cares about multilines
onboarding-wifi_creds-description =
    Трекеры будут использовать эти учетные данные для беспроводного подключения.
    Пожалуйста, используйте данные Wi_Fi, к которому вы в данный момент подключены.
onboarding-wifi_creds-skip = Пропустить настройки Wi-Fi
onboarding-wifi_creds-submit = Отправить!
onboarding-wifi_creds-ssid =
    .label = Имя Wi-Fi
    .placeholder = Введите имя Wi-Fi
onboarding-wifi_creds-password =
    .label = Пароль
    .placeholder = Введите пароль Wi-Fi

## Mounting setup

onboarding-reset_tutorial-back = Вернуться к калибровке крепления
onboarding-reset_tutorial = Сбросить туториал
onboarding-reset_tutorial-description = Эта функция не завершена, просто нажмите продолжить

## Setup start

onboarding-home = Добро пожаловать в SlimeVR!
onboarding-home-start = Давайте все настроим!

## Enter VR part of setup

onboarding-enter_vr-back = Вернуться к привязке трекеров
onboarding-enter_vr-title = Время зайти в VR!
onboarding-enter_vr-description = Наденьте все ваши трекеры и зайдите в VR!
onboarding-enter_vr-ready = Я готов

## Setup done

onboarding-done-title = Вы готовы!
onboarding-done-description = Наслаждайтесь игре!
onboarding-done-close = Закрыть гид

## Tracker connection setup

onboarding-connect_tracker-back = Вернуться к учетным данным Wi-Fi
onboarding-connect_tracker-title = Подключить трекеры
onboarding-connect_tracker-description-p0 = Теперь самое интересное - подключение всех трекеров!
onboarding-connect_tracker-description-p1 = Просто подключите все, что еще не подключены, через USB-порт.
onboarding-connect_tracker-issue-serial = У меня проблемы с подключением!
onboarding-connect_tracker-usb = USB Трекер
onboarding-connect_tracker-connection_status-none = Поиск трекеров
onboarding-connect_tracker-connection_status-serial_init = Подключение к устройству
onboarding-connect_tracker-connection_status-provisioning = Отправка данных Wi-Fi
onboarding-connect_tracker-connection_status-connecting = Отправить данные Wi_Fi
onboarding-connect_tracker-connection_status-looking_for_server = Поиск сервера
onboarding-connect_tracker-connection_status-connection_error = Не удается подключиться к Wi-Fi
onboarding-connect_tracker-connection_status-could_not_find_server = Не удалось найти сервер
onboarding-connect_tracker-connection_status-done = Подключен к серверу
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers =
    { $amount ->
        [0] Нет подключенных трекеров
        [one] { $amount } подключенный трекер
        [many] { $amount } подключённых трекеров
        [few] { $amount } подключённых трекеров
       *[other] { $amount } подключённых трекеров
    }
onboarding-connect_tracker-next = Я подключил все трекеры!

## Tracker assignment setup

onboarding-assign_trackers-back = Вернуться к вводу данных Wi-Fi
onboarding-assign_trackers-title = Привязать трекеры
onboarding-assign_trackers-description = Давайте выберем, расположение ваших трекеров. Нажмите на место, где вы хотите разместить трекер
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned =
    { $trackers ->
        [one] { $assigned } из 1 трекер привязано
        [few] { $assigned } из { $trackers } трекера привязано
        [many] { $assigned } из { $trackers } трекеров привязано
       *[other] { $assigned } из { $trackers } трекеров привязано
    }
onboarding-assign_trackers-advanced = Показать дополнительные места привязки
onboarding-assign_trackers-next = Я привязал все трекеры

## Tracker assignment warnings

# Note for devs, number is used for representing boolean states per bit.
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_FOOT =
    { $unassigned ->
        [0] Назначена левая ступня, также вам нужно назначить левую голень и левое бедро, и либо грудь, либо талию!
        [1] Назначена левая ступня, но вам также нужно назначить левое бедро и грудь, а также либо таз, либо талию!
        [2] Назначена левая ступня, но вам также нужно назначить левую голень и грудь, а также либо таз, либо талию!
        [3] Назначена левая ступня, но вам нужно, назначить грудь, таз или талия также должны быть назначены!
        [4] Назначена левая ступня, но вам нужно, назначить левую голень и левое бедро!
        [5] Назначена левая ступня, но вам нужно, назначить левое бедро!
        [6] Назначена левая ступня, но вам нужно, назначить левую голень!
       *[unknown] Назначена левая ступня, но вам также нужно назначить неизвестную неназначенную часть тела!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_FOOT =
    { $unassigned ->
        [0] Назначена правая ступня, также вам нужно назначить правую голень и правое бедро, и либо грудь, либо талию!
        [1] Назначена правая ступня, но вам также нужно назначить правое бедро и грудь, а также либо таз, либо талию!
        [2] Назначена правая ступня, но вам также нужно назначить правую голень и грудь, а также либо таз, либо талию!
        [3] Назначена правая ступня, но вам нужно, назначить грудь, таз или талия также должны быть назначены!
        [4] Назначена правая ступня, но вам нужно, назначить правую голень и правое бедро!
        [5] Назначена правая ступня, но вам нужно, назначить правое бедро!
        [6] Назначена правая ступня, но вам нужно, назначить правую голень!
       *[unknown] Назначена правая ступня, но вам также нужно назначить неизвестную неназначенную часть тела!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_LOWER_LEG =
    { $unassigned ->
        [0] Назначена левая голень, но вам нужно, чтобы также было назначено левое бедро!
        [1] Назначена левая голень, но вам также нужно назначить грудь, таз или талию!
        [2] Назначена левая голень, но вам также нужно назначить левое бедро и либо грудь, либо таз, либо талию!
       *[other] Назначена левая голень, но вам также нужно назначить неизвестную неназначенную часть тела!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_LOWER_LEG =
    { $unassigned ->
        [0] Назначена правая голень, но вам также нужно назначить правое бедро и либо грудь, либо таз, либо талию!
        [1] Назначена правая голень, но вам также нужно назначить грудь, таз или талию!
        [2] Назначена правая голень, но вам нужно, чтобы также было назначено правое бедро!
       *[other] Назначена правая голень, но вам также нужно назначить неизвестную неназначенную часть тела!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_UPPER_LEG =
    { $unassigned ->
        [0] Назначено левое бедро, вам также нужно назначить грудь, таз или талию!
       *[unknown] Назначено левое бедро, но вам также нужно назначить неизвестную неназначенную часть тела!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_UPPER_LEG =
    { $unassigned ->
        [0] Назначено правое бедро, вам также нужно назначить грудь, таз или талию!
       *[unknown] Назначено правое бедро, но вам также нужно назначить неизвестную неназначенную часть тела!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-HIP =
    { $unassigned ->
        [0] Таз назначен, но вам нужно, чтобы грудь также была назначена!
       *[unknown] Таз назначен, но вам нужно, чтобы неизвестная неназначенная часть тела также была назначена!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-WAIST =
    { $unassigned ->
        [0] Талия назначена, но вам нужно, чтобы грудь также была назначена!
       *[unknown] Талия назначена, но вам нужно, чтобы неизвестная неназначенная часть тела также была назначена!
    }

## Tracker mounting method choose


## Tracker manual mounting setup

onboarding-manual_mounting-back = Вернуться чтобы войти в VR
onboarding-manual_mounting = Ручная привязка
onboarding-manual_mounting-description = Нажмите на каждый трекер и выберите, каким способом они будут привязаны
onboarding-manual_mounting-auto_mounting = Автоматическая привязка
onboarding-manual_mounting-next = Следующий шаг

## Tracker automatic mounting setup

onboarding-automatic_mounting-back = Вернуться чтобы войти в VR
onboarding-automatic_mounting-title = Калибровка привязки
onboarding-automatic_mounting-description = Чтобы трекеры SlimeVR работали, нам необходимо назначить поворот крепления для ваших трекеров, чтобы выровнять их с вашим физическим креплением трекера.
onboarding-automatic_mounting-manual_mounting = Установка вручную
onboarding-automatic_mounting-next = Следующий шаг
onboarding-automatic_mounting-prev_step = Предыдущий щаг
onboarding-automatic_mounting-done-title = Привязка поворотов калибрована.
onboarding-automatic_mounting-done-description = Калибровка вашей привязки завершена!
onboarding-automatic_mounting-done-restart = Вернуться к началу
onboarding-automatic_mounting-mounting_reset-title = Сброс крепления
onboarding-automatic_mounting-mounting_reset-step-0 = 1. Присядьте в позу "лыжника", согнув ноги, наклонив верхнюю часть тела вперед и согнув руки.
onboarding-automatic_mounting-mounting_reset-step-1 = 2. Нажмите кнопку "Сброс крепления" и подождите 3 секунды, прежде чем установочные повороты трекеров будут сброшены.
onboarding-automatic_mounting-preparation-title = Подготовка
onboarding-automatic_mounting-preparation-step-0 = 1. Встаньте прямо, руки по бокам.
onboarding-automatic_mounting-preparation-step-1 = 2. Нажмите кнопку "Сброс" и подождите 3 секунды, прежде чем трекеры сбросятся.
onboarding-automatic_mounting-put_trackers_on-title = Наденьте ваши трекеры
onboarding-automatic_mounting-put_trackers_on-description = Чтобы откалибровать повороты крепления, мы будем использовать трекеры, которые вы только что назначили. Включите все свои трекеры, вы можете увидеть, какие из них какие на рисунке справа.
onboarding-automatic_mounting-put_trackers_on-next = Я включил и надел все свои трекеры

## Tracker proportions method choose


## Tracker manual proportions setup

onboarding-manual_proportions-back = Вернутся к началу обучения
onboarding-manual_proportions-title = Ручные пропорции тела
onboarding-manual_proportions-precision = Регулировка предсказывания
onboarding-manual_proportions-auto = Автоматическая калибровка
onboarding-manual_proportions-ratio = Настроить по группам пропорций

## Tracker automatic proportions setup

onboarding-automatic_proportions-back = Вернутся к началу обучения
onboarding-automatic_proportions-title = Измерьте свое тело
onboarding-automatic_proportions-description = Чтобы трекеры SlimeVR работали, нам нужно знать длину ваших костей. Эта короткая калибровка измерит его для вас.
onboarding-automatic_proportions-manual = Ручная калибровка
onboarding-automatic_proportions-prev_step = Предыдущий шаг
onboarding-automatic_proportions-put_trackers_on-title = Наденьте ваши трекеры
onboarding-automatic_proportions-put_trackers_on-description = Чтобы откалибровать ваши пропорции, мы собираемся использовать трекеры, которые вы только что назначили. Включите все свои трекеры, вы можете увидеть, какие из них какие на рисунке справа.
onboarding-automatic_proportions-put_trackers_on-next = Я надел все свои трекеры
onboarding-automatic_proportions-start_recording-title = Будьте готовы к движению
onboarding-automatic_proportions-start_recording-description = Теперь мы собираемся записать некоторые конкретные позы и движения. Они будут запрошены на следующем экране. Будьте готовы начать, когда кнопка будет нажата!
onboarding-automatic_proportions-start_recording-next = Начать запись
onboarding-automatic_proportions-recording-title = Запись
onboarding-automatic_proportions-recording-description-p0 = Запись в процессе...
onboarding-automatic_proportions-recording-description-p1 = Сделайте эти движения:
onboarding-automatic_proportions-recording-processing = Обработка результата...
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 20)
onboarding-automatic_proportions-recording-timer =
    { $time ->
        [one] { $time } секунда
        [few] { $time } секунды
        [many] { $time } секунд
       *[other] { $time } секунд
    }
onboarding-automatic_proportions-verify_results-title = Подтвердить результаты
onboarding-automatic_proportions-verify_results-description = Проверьте результаты ниже, правильно ли они выглядят?
onboarding-automatic_proportions-verify_results-results = Запись результатов
onboarding-automatic_proportions-verify_results-processing = Обработка результатов
onboarding-automatic_proportions-verify_results-redo = Перезаписать
onboarding-automatic_proportions-verify_results-confirm = Они правильные
onboarding-automatic_proportions-done-title = Тело измерено и сохранено.
onboarding-automatic_proportions-done-description = Калибровка пропорций вашего тела завершена!

## Home

home-no_trackers = Трекеры не обнаружены и не привязаны
