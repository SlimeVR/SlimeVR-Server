### SlimeVR complete GUI translations
# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means

## Websocket (server) status
websocket-connecting = Подключение к серверу
websocket-connection_lost = Потеряно подключение к серверу. Переподключение...

## Tips
tips-find_tracker = Не уверены, какой трекер какой? Встряхните трекер, и он выделит соответствующий элемент.
tips-do_not_move_heels = Убедитесь, что ваши пятки не двигаются во время записи!

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
body_part-RIGHT_CONTROLLER = Правый контроллер
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
body_part-LEFT_CONTROLLER = Левый контроллер

## Proportions
skeleton_bone-NONE = Пусто
skeleton_bone-HEAD = Голова
skeleton_bone-NECK = Длина шеи
skeleton_bone-CHEST = Длина груди
skeleton_bone-CHEST_OFFSET = Оффсет груди
skeleton_bone-WAIST = Длина талии
skeleton_bone-HIP = Длина таза
skeleton_bone-HIP_OFFSET = Оффсет таза
skeleton_bone-HIPS_WIDTH = Ширина бедер
skeleton_bone-UPPER_LEG = Длинна бедра
skeleton_bone-LOWER_LEG = Длинна лодышки
skeleton_bone-FOOT_LENGTH = Длинна стопы
skeleton_bone-FOOT_SHIFT = Смещение стопы
skeleton_bone-SKELETON_OFFSET = Оффсет скелета
skeleton_bone-SHOULDERS_DISTANCE = Дистанция плеч
skeleton_bone-SHOULDERS_WIDTH = Ширина плеч
skeleton_bone-UPPER_ARM = Длинна предплечья
skeleton_bone-LOWER_ARM = Длинна нижнего предплечья
skeleton_bone-CONTROLLER_Y = Дистанция контроллера Y
skeleton_bone-CONTROLLER_Z = Дистанция контроллера Z
skeleton_bone-ELBOW_OFFSET = Оффсет локтя

## Tracker reset buttons
reset-reset_all = Сбросить все пропорции
reset-full = Сброс
reset-mounting = Сбросить крепление
reset-quick = Быстрый сброс

## Serial detection stuff
serial_detection-new_device-p0 = Обнаружено новое устройство!
serial_detection-new_device-p1 = Заполните данные вашего Wi-Fi!
serial_detection-new_device-p2 = Пожалуйста, выберите, что вы хотите с ним сделать
serial_detection-open_wifi = Подключиться к Wi-Fi
serial_detection-open_serial = Открыть серийную консоль
serial_detection-submit = Отправить!
serial_detection-close = Закрыть

## Navigation bar
navbar-home = Дом
navbar-body_proportions = Пропроции тела
navbar-trackers_assign = Назначение трекера
navbar-mounting = Калибровка крепления
navbar-onboarding = Мастер настройки
navbar-settings = Настройки

## Bounding volume hierarchy recording
bvh-start_recording = Записать BVH
bvh-recording = Идет запись...

## Widget: Overlay settings
widget-overlay = Оверлей
widget-overlay-is_visible_label = Показывать оверлей в SteamVR
widget-overlay-is_mirrored_label = Показывать оверлей как зеркало

## Widget: Developer settings
widget-developer_mode = Режим разработчика
widget-developer_mode-high_contrast = Высокий контраст
widget-developer_mode-precise_rotation = Точное вращение
widget-developer_mode-fast_data_feed = Быстрый поток данных
widget-developer_mode-filter_slimes_and_hmd = Фильтровать SlimeVR и HMD
widget-developer_mode-sort_by_name = Сортировка по имени
widget-developer_mode-raw_slime_rotation = Сырое вращение
widget-developer_mode-more_info = Больше информации

## Widget: IMU Visualizer
widget-imu_visualizer = Вращение
widget-imu_visualizer-rotation_raw = Сырое
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
tracker-rotation-front = Перед
tracker-rotation-left = Лево
tracker-rotation-right = Право
tracker-rotation-back = Зад

## Tracker information
tracker-infos-manufacturer = Производитель
tracker-infos-display_name = Отображающееся имя
tracker-infos-custom_name = Свое имя
tracker-infos-url = URL трекера

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
tracker-settings-drift_compensation_section-description = Should this tracker compensate for its drift when drift compensation is enabled?
tracker-settings-drift_compensation_section-edit = Разрешить компенсацию дрифта
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Имя трекера
tracker-settings-name_section-description = Дай ему милое имя :)
tracker-settings-name_section-placeholder = Левая нога NightyBeast'а

## Tracker part card info
tracker-part_card-no_name = Безымянный
tracker-part_card-unassigned = Не привязан

## Body assignment menu
body_assignment_menu = Где ты хочешь чтобы этот трекер был?
body_assignment_menu-description = Выберите местоположение, куда вы хотите назначить этот трекер. В качестве альтернативы вы можете выбрать управление всеми трекерами сразу, а не по одному.
body_assignment_menu-show_advanced_locations = Показать дополнительные положения
body_assignment_menu-manage_trackers = Настроить все трекеры
body_assignment_menu-unassign_tracker = Отвязать трекер

## Tracker assignment menu
# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = Какой трекер ты хочешь привязать к
tracker_selection_menu-NONE = Which tracker do you want to be unassigned?
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
settings-sidebar-serial = Серийная консоль

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
settings-general-tracker_mechanics-filtering-type-none-description = Используйте вращения как есть. Не будет выполнять никакой фильтрации.
settings-general-tracker_mechanics-filtering-type-smoothing = Сглаживаение
settings-general-tracker_mechanics-filtering-type-smoothing-description = Сглаживает движения, но добавляет некоторую задержку.
settings-general-tracker_mechanics-filtering-type-prediction = Предсказывание
settings-general-tracker_mechanics-filtering-type-prediction-description = Уменьшает задержку и делает движения более быстрыми, но может увеличить дрожание.
settings-general-tracker_mechanics-filtering-amount = Количество
settings-general-tracker_mechanics-drift_compensation = Компенсация дрифта
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Компенсирует дрейф по рысканию IMU путем применения обратного вращения.
    Измените размер компенсации и до скольких сбросов учитывается.
settings-general-tracker_mechanics-drift_compensation-enabled-label = Компенсация дрифта
settings-general-tracker_mechanics-drift_compensation-amount-label = Кол-во компенсации
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Используйте до x последних сбросов

## FK/Tracking settings
settings-general-fk_settings = Настройки трекеров
settings-general-fk_settings-leg_tweak = Починка ног
settings-general-fk_settings-leg_tweak-description = Зажим для пола может уменьшить или даже устранить сцепление с полом, но может вызвать проблемы, когда вы стоите на коленях. Катание на коньках-коррекция корректирует катание на коньках, но может снизить точность в определенных моделях движений.
# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Клип через пол
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Коррекция катания на коньках
settings-general-fk_settings-leg_tweak-skating_correction-amount = Сила коррекция катания на коньках
settings-general-fk_settings-arm_fk = Трекинг руки
settings-general-fk_settings-arm_fk-description = Измените способ отслеживания рук.
settings-general-fk_settings-arm_fk-force_arms = Руки от HMD
settings-general-fk_settings-skeleton_settings = Настройки скелета
settings-general-fk_settings-skeleton_settings-description = Включите или выключите настройки скелета. Рекомендуется оставить их включенными.
settings-general-fk_settings-skeleton_settings-extended_spine = Удлененный позвоночник
settings-general-fk_settings-skeleton_settings-extended_pelvis = Расширенный таз
settings-general-fk_settings-skeleton_settings-extended_knees = Вытянутое колено
settings-general-fk_settings-vive_emulation-title = Эмуляция Vive
settings-general-fk_settings-vive_emulation-description = Имитируйте проблемы с отслеживанием талии, которые возникают у трекеров Vive. Это шутка, и она ухудшает отслеживание.
settings-general-fk_settings-vive_emulation-label = Включить эмуляцию Vive

## Gesture control settings (tracker tapping)
settings-general-gesture_control = Контроль жестами
settings-general-gesture_control-subtitle = Нажатие на ресет
settings-general-gesture_control-description = Позволяет запускать сброс настроек нажатием на трекер. Трекер, расположенный выше всего на вашем торсе, используется для быстрого сброса, трекер, расположенный выше всего на вашей левой ноге, используется для сброса, а трекер, расположенный выше всего на вашей правой ноге, используется для установки сброса. Следует отметить, что для регистрации нажатия должны происходить в течение 0,6 секунды.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps = { $amount ->
    [one] 1 tap
    *[other] { $amount } taps
}
settings-general-gesture_control-quickResetEnabled = Включить нажатие для быстрого сброса
settings-general-gesture_control-quickResetDelay = Задержка быстрого сброса
settings-general-gesture_control-quickResetTaps = Нажатия для быстрого сброса
settings-general-gesture_control-resetEnabled = Включить нажатие для сброса
settings-general-gesture_control-resetDelay = Сбросить задержку
settings-general-gesture_control-resetTaps = Нажатия для сброса
settings-general-gesture_control-mountingResetEnabled = Включите нажатия для сброса установки
settings-general-gesture_control-mountingResetDelay = Задержка сброса крепления
settings-general-gesture_control-mountingResetTaps = Нажатия для сброса крепления

## Interface settings
settings-general-interface = Interface
settings-general-interface-dev_mode = Режим разработчика
settings-general-interface-dev_mode-description = Этот режим может быть полезен, если вам нужны подробные данные или для взаимодействия с подключенными трекерами на более продвинутом уровне.
settings-general-interface-dev_mode-label = Режим разработчика
settings-general-interface-serial_detection = Обнаружение серийного устройства
settings-general-interface-serial_detection-description = Эта опция будет показывать всплывающее окно каждый раз, когда вы подключаете новое серийное устройство, которое может быть трекером. Это помогает улучшить процесс настройки трекера.
settings-general-interface-serial_detection-label = Обнаружение серийного устройства
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
settings-serial-factory_reset = Заводской сброс
settings-serial-get_infos = Получить иформацию
settings-serial-serial_select = Выбрать серийный порт
settings-serial-auto_dropdown_item = Авто

## OSC router settings
settings-osc-router = OSC роутер
# This cares about multilines
settings-osc-router-description =
    Пересылать OSC-сообщения из другой программы.
    Полезно для использования другой программы OSC, например, с VRChat.
settings-osc-router-enable = Включить
settings-osc-router-enable-description = Переключите переадресацию сообщений.
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
settings-osc-vrchat-network-trackers-waist = Талия
settings-osc-vrchat-network-trackers-knees = Колени
settings-osc-vrchat-network-trackers-feet = Ступни
settings-osc-vrchat-network-trackers-elbows = Локти

## Setup/onboarding menu
onboarding-skip = Пропустить установку
onboarding-continue = Продолжить
onboarding-wip = В работе W.I.P.

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
    .placeholder = Вставьте имя Wi-Fi
onboarding-wifi_creds-password =
    .label = Пароль
    .placeholder = Вставьте пароль Wi-Fi

## Mounting setup
onboarding-reset_tutorial-back = Вернитесь к калибровке крепления
onboarding-reset_tutorial = Сбросить туториал
onboarding-reset_tutorial-description = Эта функция не завершена, просто нажмите продолжить

## Setup start
onboarding-home = Добро пожаловать в SlimeVR!
# This cares about multilines and it's centered!!
onboarding-home-description =
    Обеспечение полного отслеживания тела
    для всех
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
onboarding-connect_tracker-back = Вернитесь к данным Wi-Fi
onboarding-connect_tracker-title = Подключить трекеры
onboarding-connect_tracker-description-p0 = Теперь самое интересное - подключение всех трекеров!
onboarding-connect_tracker-description-p1 = Просто подключите все, что еще не подключены, через USB-порт.
onboarding-connect_tracker-issue-serial = У меня проблемы с подключением!
onboarding-connect_tracker-usb = USB Трекер
onboarding-connect_tracker-connection_status-connecting = Отправить данные Wi_Fi
onboarding-connect_tracker-connection_status-connected = Подключен к Wi-Fi
onboarding-connect_tracker-connection_status-error = Невозможно подключиться к Wi-Fi
onboarding-connect_tracker-connection_status-start_connecting = Ищем трекеры...
onboarding-connect_tracker-connection_status-handshake = Подключен к трекеру
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers = { $amount ->
    [0] Нет трекеров
    [one] { $amount } трекер
    [few] { $amount } трекера
    *[other] { $amount } трекеров
} подключено
onboarding-connect_tracker-next = Я подключил все трекеры!

## Tracker assignment setup
onboarding-assign_trackers-back = Вернуться к данным Wi-Fi
onboarding-assign_trackers-title = Привязать трекеры
onboarding-assign_trackers-description = Давайте выберем, какой трекер куда идет. Нажмите на место, где вы хотите разместить трекер
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Трекеры, которым была назначена часть тела
# $trackers (Number) - Трекеры, подключенные к серверу
onboarding-assign_trackers-assigned = { $assigned } из { $trackers ->
    [one] 1 трекер
    [few] { $amount } трекера
    [many] { $amount} трекеров
    *[other] { $amount } трекеров
} привязано
onboarding-assign_trackers-advanced = Показать дополнительные места привязки
onboarding-assign_trackers-next = Я привязал все трекеры

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
onboarding-automatic_mounting-mounting_reset-step-0 = 1. Присядьте в позу "лыжи", согнув ноги, наклонив верхнюю часть тела вперед и согнув руки.
onboarding-automatic_mounting-mounting_reset-step-1 = 2. Нажмите кнопку "Сброс крепления" и подождите 3 секунды, прежде чем установочные повороты трекеров будут сброшены.
onboarding-automatic_mounting-preparation-title = Подготовка
onboarding-automatic_mounting-preparation-step-0 = 1. Встаньте прямо, руки по бокам.
onboarding-automatic_mounting-preparation-step-1 = 2. Нажмите кнопку "Сброс" и подождите 3 секунды, прежде чем трекеры сбросятся.
onboarding-automatic_mounting-put_trackers_on-title = Наденьте ваши трекеры
onboarding-automatic_mounting-put_trackers_on-description = Чтобы откалибровать повороты крепления, мы будем использовать трекеры, которые вы только что назначили. Включите все свои трекеры, вы можете увидеть, какие из них какие на рисунке справа.
onboarding-automatic_mounting-put_trackers_on-next = Я включил и надел все свои трекеры
## Tracker manual proportions setup
onboarding-manual_proportions-back = Вернитесь чтобы сбросить туториал
onboarding-manual_proportions-title = Ручные пропорции тела
onboarding-manual_proportions-precision = Регулеровка предсказывания
onboarding-manual_proportions-auto = Автоматическая калибровка

## Tracker automatic proportions setup
onboarding-automatic_proportions-back = Вернитесь чтобы сбросить туториал
onboarding-automatic_proportions-title = Измерьте свое тело
onboarding-automatic_proportions-description = Чтобы трекеры SlimeVR работали, нам нужно знать длину ваших костей. Эта короткая калибровка измерит его для вас.
onboarding-automatic_proportions-manual = Ручная калибровка
onboarding-automatic_proportions-prev_step = Предыдущий шаг
onboarding-automatic_proportions-put_trackers_on-title = Наденьте ваши трекеры
onboarding-automatic_proportions-put_trackers_on-description = Чтобы откалибровать ваши пропорции, мы собираемся использовать трекеры, которые вы только что назначили. Включите все свои трекеры, вы можете увидеть, какие из них какие на рисунке справа.
onboarding-automatic_proportions-put_trackers_on-next = Я надел все свои трекеры
onboarding-automatic_proportions-preparation-title = Подготовка
onboarding-automatic_proportions-preparation-description = Поставьте стул прямо позади себя в вашем игровом пространстве. Будьте готовы сесть во время настройки автобона.
onboarding-automatic_proportions-preparation-next = Я перед стулом
onboarding-automatic_proportions-start_recording-title = Будьте готовы к движению
onboarding-automatic_proportions-start_recording-description = Теперь мы собираемся записать некоторые конкретные позы и движения. Они будут запрошены на следующем экране. Будьте готовы начать, когда кнопка будет нажата!
onboarding-automatic_proportions-start_recording-next = Начать запись
onboarding-automatic_proportions-recording-title = Запись
onboarding-automatic_proportions-recording-description-p0 = Запись в процессе...
onboarding-automatic_proportions-recording-description-p1 = Сделайте эти движенияя:
onboarding-automatic_proportions-recording-steps-0 = Согните колени несколько раз.
onboarding-automatic_proportions-recording-steps-1 = Сядьте на стул, затем встаньте.
onboarding-automatic_proportions-recording-steps-2 = Поверните верхнюю часть тела влево, затем согните вправо.
onboarding-automatic_proportions-recording-steps-3 = Поверните верхнюю часть туловища вправо, затем согните левую.
onboarding-automatic_proportions-recording-steps-4 = Покачайтесь, пока таймер не закончится.
onboarding-automatic_proportions-recording-processing = Обрабатываем результаты...
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 15)
onboarding-automatic_proportions-recording-timer = { $time ->
    [one] секунда осталась
    [few] { $time } секунды осталась
    *[other] { $time } секунд осталось
}
onboarding-automatic_proportions-verify_results-title = Подтвердить результаты
onboarding-automatic_proportions-verify_results-description = Проверьте результаты ниже, правильно ли они выглядят?
onboarding-automatic_proportions-verify_results-results = Записываем результаты
onboarding-automatic_proportions-verify_results-processing = Обрабатываем результат
onboarding-automatic_proportions-verify_results-redo = Перезаписать
onboarding-automatic_proportions-verify_results-confirm = Они правильные
onboarding-automatic_proportions-done-title = Тело измерено и сохранено.
onboarding-automatic_proportions-done-description = Калибровка пропорций вашего тела завершена!

## Home
home-no_trackers = Трекеры не обнаружены и не привязаны
