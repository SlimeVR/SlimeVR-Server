# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Загрузка...
websocket-connection_lost = Произошел сбой сервера!
websocket-connection_lost-desc = Похоже, что произошел сбой сервера SlimeVR. Проверьте логи и перезапустите программу
websocket-timedout = Не удалось подключиться к серверу
websocket-timedout-desc = Похоже, что произошел сбой сервера SlimeVR или превышено время ожидания. Проверьте логи и перезапустите программу
websocket-error-close = Выйти из SlimeVR
websocket-error-logs = Открыть папку логов

## Update notification

version_update-title = Доступна новая версия: { $version }
version_update-description = Нажав «{ version_update-update }», вы загрузите установщик SlimeVR.
version_update-update = Обновить
version_update-close = Закрыть

## Tips

tips-find_tracker = Не уверены, какой трекер какой? Встряхните его, и трекер выделится в списке.
tips-do_not_move_heels = Убедитесь, что ваши пятки не двигаются во время записи!
tips-file_select = Выберите и перетащите файлы, чтобы использовать, или нажмите <u>выбрать</u>.
tips-failed_webgl = Не удалось инициализировать WebGL.

## Units


## Dropdown


## Text input


## File input


## Window controls

titlebar-close = Закрыть

## Body parts

body_part-NONE = Не привязан
body_part-HEAD = Голова
body_part-NECK = Шея
body_part-RIGHT_SHOULDER = Правое плечо
body_part-RIGHT_UPPER_ARM = Правое предплечье
body_part-RIGHT_LOWER_ARM = Правое нижнее предплечье
body_part-RIGHT_HAND = Правая рука
body_part-RIGHT_UPPER_LEG = Правое бедро
body_part-RIGHT_LOWER_LEG = Правая голень
body_part-RIGHT_FOOT = Правая ступня
body_part-UPPER_CHEST = Верхняя часть груди
body_part-HIP = Таз
body_part-LEFT_SHOULDER = Левое плечо
body_part-LEFT_UPPER_ARM = Левое предплечье
body_part-LEFT_LOWER_ARM = Левое нижнее предплечье
body_part-LEFT_HAND = Левая рука
body_part-LEFT_UPPER_LEG = Левое бедро
body_part-LEFT_LOWER_LEG = Левая голень
body_part-LEFT_FOOT = Левая ступня
body_part-LEFT_THUMB_METACARPAL = Левый большой палец (пястная кость)
body_part-LEFT_THUMB_PROXIMAL = Левый большой палец (проксимальная фаланга)
body_part-LEFT_THUMB_DISTAL = Левый большой палец (дистальная фаланга)
body_part-LEFT_INDEX_PROXIMAL = Левый указательный палец (проксимальная фаланга)
body_part-LEFT_INDEX_INTERMEDIATE = Левый указательный палец (промежуточная фаланга)
body_part-LEFT_INDEX_DISTAL = Левый указательный палец (дистальная фаланга)
body_part-LEFT_MIDDLE_PROXIMAL = Левый средний палец (проксимальная фаланга)
body_part-LEFT_MIDDLE_INTERMEDIATE = Левый средний палец (промежуточная фаланга)
body_part-LEFT_MIDDLE_DISTAL = Левый средний палец (дистальная фаланга)
body_part-LEFT_RING_PROXIMAL = Левый безымянный палец (проксимальная фаланга)
body_part-LEFT_RING_INTERMEDIATE = Левый безымянный палец (промежуточная фаланга)
body_part-LEFT_RING_DISTAL = Левый безымянный палец (дистальная фаланга)
body_part-LEFT_LITTLE_PROXIMAL = Левый мизинец (проксимальная фаланга)
body_part-LEFT_LITTLE_INTERMEDIATE = Левый мизинец (промежуточная фаланга)
body_part-LEFT_LITTLE_DISTAL = Левый мизинец (дистальная фаланга)
body_part-RIGHT_THUMB_METACARPAL = Правый большой палец (пястная кость)
body_part-RIGHT_THUMB_PROXIMAL = Правый большой палец (проксимальная фаланга)
body_part-RIGHT_THUMB_DISTAL = Правый большой палец (дистальная фаланга)
body_part-RIGHT_INDEX_PROXIMAL = Правый указательный палец (проксимальная фаланга)
body_part-RIGHT_INDEX_INTERMEDIATE = Правый указательный палец (промежуточная фаланга)
body_part-RIGHT_INDEX_DISTAL = Правый указательный палец (дистальная фаланга)
body_part-RIGHT_MIDDLE_PROXIMAL = Правый средний палец (проксимальная фаланга)
body_part-RIGHT_MIDDLE_INTERMEDIATE = Правый средний палец (промежуточная фаланга)
body_part-RIGHT_MIDDLE_DISTAL = Правый средний палец (дистальная фаланга)
body_part-RIGHT_RING_PROXIMAL = Правый безымянный палец (проксимальная фаланга)
body_part-RIGHT_RING_INTERMEDIATE = Правый безымянный палец (промежуточная фаланга)
body_part-RIGHT_RING_DISTAL = Правый безымянный палец (дистальная фаланга)
body_part-RIGHT_LITTLE_PROXIMAL = Правый мизинец (проксимальная фаланга)
body_part-RIGHT_LITTLE_INTERMEDIATE = Правый мизинец (промежуточная фаланга)
body_part-RIGHT_LITTLE_DISTAL = Правый мизинец (дистальная фаланга)

## BoardType

board_type-UNKNOWN = Неизвестно
board_type-CUSTOM = Кастомная Плата
board_type-WRANGLER = Joycon через Wrangler

## Proportions

skeleton_bone-NONE = Пусто
skeleton_bone-HEAD = Смещение Головы
skeleton_bone-HEAD-desc =
    Это расстояние от вашего шлема до центра вашей головы.
    Чтобы откалибровать его, покрутите головой из стороны в сторону, будто несогласны с чем-то, и изменяйте
    значение, пока движение других трекеров не станет незначительным.
skeleton_bone-NECK = Длина Шеи
skeleton_bone-NECK-desc =
    Это расстояние от центра вашей головы до основания вашей шеи.
    Чтобы откалибровать его, двигайте головой вверх-вниз, будто вы киваете, или отклоняйте голову
    влево-вправо и изменяйте значение, пока движение других трекеров не станет незначительным.
skeleton_bone-torso_group = Длина Туловища
skeleton_bone-torso_group-desc =
    Это расстояние от основания вашей шеи до ваших бёдер.
    Чтобы откалибровать его, встаньте прямо и изменяйте значение, пока виртуальная линия бёдер
    не совпадёт с реальной.
skeleton_bone-UPPER_CHEST = Длина верхней части груди
skeleton_bone-UPPER_CHEST-desc =
    Это расстояние от основания вашей шеи до середины вашей груди.
    Чтобы откалибровать его,  убедитесь, что "Длина туловища" задана верно, и изменяйте её в различных
    позициях (сидя, наклонившсь, лёжа и т.д.), пока ваш виртуальный позвоночник не совпадёт с реальным.
skeleton_bone-LOWER_CHEST-desc =
    Это расстояние от центра вашей груди до центра вашего позвоночника.
    Чтобы откалибровать его,  убедитесь, что "Длина туловища" задана верно, и изменяйте значение в различных
    позициях (сидя, наклонившись, лёжа и т.д.), пока ваш виртуальный позвоночник не совпадёт с реальным.
skeleton_bone-HIP = Длина таза
skeleton_bone-HIPS_WIDTH = Ширина бедер
skeleton_bone-HIPS_WIDTH-desc =
    Это расстояние между начала ваших ног.
    Чтобы откалибровать его, выполните полный сброс, поставив ноги прямо, и изменяйте значение,
    пока ваши виртуальные ноги не совпадут с реальными.
skeleton_bone-leg_group = Длина ноги
skeleton_bone-leg_group-desc =
    Это расстояние от ваших бёдер до ваших ступней.
    Чтобы откалибровать его, убедитесь, что "Длина туловища" задана верно,
    и изменяйте значение, пока ваши виртуальные ступни не совпадут с реальными.
skeleton_bone-UPPER_LEG = Длина бедра
skeleton_bone-UPPER_LEG-desc =
    Это расстояние от ваших бёдер до ваших колен.
    Чтобы откалибровать его, убедитесь, что "Длина ноги" задана верно,
    и изменяйте значение, пока ваши виртуальные колени не совпадут с реальными.
skeleton_bone-LOWER_LEG = Длина голени
skeleton_bone-LOWER_LEG-desc =
    Это расстояние от ваших колен до ваших лодыжек.
    Чтобы откалибровать его, убедитесь, что "Длина ноги" задана верно,
    и изменяйте значение, пока ваши виртуальные колени не совпадут с реальными.
skeleton_bone-FOOT_LENGTH = Длинна стопы
skeleton_bone-FOOT_LENGTH-desc =
    Это расстояние от ваших лодыжек до пальцев ног.
    Чтобы откалибровать его, поднимитесь на цыпочки и изменяйте
    значение, пока ваши виртуальные стопы не встанут на место.
skeleton_bone-FOOT_SHIFT = Смещение стопы
skeleton_bone-FOOT_SHIFT-desc =
    Это горизонтальное расстояние от ваших колен до ваших лодыжек.
    Оно отвечает за смещение голеней назад, когда вы стоите прямо.
    Чтобы откалибровать его, установите "Длину ноги" равной 0, выполните полный сброс, и
    изменяйте значение, пока ваши виртуальные ступни не сравняются с центром лодыжек.
skeleton_bone-SHOULDERS_DISTANCE = Расстояние до Плеч
skeleton_bone-SHOULDERS_DISTANCE-desc =
    Это вертикальное расстояние от основания вашей шеи до ваших плеч.
    Чтобы откалибровать его, установите "Длину предплечья" равной 0, и изменяйте значение, пока
    ваши виртуальные трекеры локтей не сравняются вертикально с вашими реальными плечами.
skeleton_bone-SHOULDERS_WIDTH = Ширина плеч
skeleton_bone-SHOULDERS_WIDTH-desc =
    Это горизонтальное расстояние от основания вашей шеи до ваших плеч.
    Чтобы откалибровать его, установите "Длину предплечья" равной 0, и изменяйте значение, пока
    ваши виртуальные трекеры локтей не сравняются горизонтально с вашими реальными плечами.
skeleton_bone-arm_group = Длина руки
skeleton_bone-arm_group-desc =
    Это расстояние от ваших плеч до ваших запястий.
    Чтобы откалибровать его, убедитесь, что "Расстояние до плеч" задано верно, установите "Расстояние
    до руки Y" равным 0, и изменяйте значение, пока ваши трекеры рук не совпадут с вашими запястьями.
skeleton_bone-UPPER_ARM = Длинна предплечья
skeleton_bone-UPPER_ARM-desc =
    Это расстояние от ваших плеч до ваших локтей.
    Чтобы откалибровать его, убедитесь, что "Длина руки" задана верно, и изменяйте
    значение, пока ваши трекеры локтей не совпадут с вашими реальными локтями.
skeleton_bone-LOWER_ARM = Длинна нижнего предплечья
skeleton_bone-LOWER_ARM-desc =
    Это расстояние от ваших локтей до ваших запястий.
    Чтобы откалибровать его, убедитесь, что "Длина руки" задана верно, и изменяйте
    значение, пока ваши трекеры локтей не совпадут с вашими реальными локтями.
skeleton_bone-HAND_Y = Расстояние от руки Y
skeleton_bone-HAND_Y-desc =
    Это вертикальное расстояние от ваших запястий до центра вашей руки.
    Чтобы откалибровать его для захвата движений, убедитесь, что "Длина руки" задана верно, и изменяйте
    значение, пока ваши трекеры рук не совпадут вертикально с центром ваших рук.
    Чтобы откалибровать его для отслеживания локтей от ваших контроллеров, установите "Длину руки" равной 0,
    и изменяйте значение, пока ваши трекеры локтей не совпадут вертикально с вашими запястьями.
skeleton_bone-HAND_Z = Расстояние от руки Z
skeleton_bone-HAND_Z-desc =
    Это горизонтальное расстояние от ваших запястий до центра ваших ладоней.
    Чтобы откалибровать его для захвата движений, установите значение равным 0.
    Чтобы откалибровать его для отслеживания локтей от ваших контроллеров, установите
    "Длину руки" равной 0, и изменяйте значение, пока ваши трекеры локтей не совпадут
    горизонтально с вашими запястьями.

## Tracker reset buttons

reset-reset_all = Сбросить все пропорции
reset-reset_all_warning-reset = Сброс пропорций
reset-reset_all_warning-cancel = Отмена
reset-full = Полный сброс
reset-mounting = Проверьте ваше крепление
reset-yaw = Горизонтальный сброс

## Navigation bar

navbar-home = Дом
navbar-body_proportions = Пропорции тела
navbar-trackers_assign = Назначение трекера
navbar-mounting = Проверьте ваше крепление
navbar-onboarding = Установщик
navbar-settings = Настройки

## Biovision hierarchy recording

bvh-start_recording = Запись BVH
bvh-recording = Запись...

## Tracking pause

tracking-unpaused = Приостановить отслеживание
tracking-paused = Возобновить отслеживание

## Widget: Developer settings

widget-developer_mode = Режим разработчика
widget-developer_mode-high_contrast = Высокая контрастность
widget-developer_mode-precise_rotation = Точное вращение
widget-developer_mode-fast_data_feed = Быстрый поток данных
widget-developer_mode-raw_slime_rotation = RAW

## Widget: IMU Visualizer

widget-imu_visualizer = Вращение
widget-imu_visualizer-preview = Предпросмотр
widget-imu_visualizer-hide = Скрыть
widget-imu_visualizer-rotation_raw = RAW
widget-imu_visualizer-rotation_preview = Предпросмотр
widget-imu_visualizer-acceleration = Ускорение
widget-imu_visualizer-position = Позиция
widget-imu_visualizer-stay_aligned = Оставаться выровненным

## Tracker status

tracker-status-none = Без Статуса
tracker-status-busy = Занят
tracker-status-error = Ошибка
tracker-status-disconnected = Отключен
tracker-status-occluded = Перекрыт
tracker-status-ok = ОК
tracker-status-timed_out = Истекло время ожидания

## Tracker status columns

tracker-table-column-name = Имя
tracker-table-column-type = Тип
tracker-table-column-battery = Батарея
tracker-table-column-ping = Пинг
tracker-table-column-temperature = Темп. °C
tracker-table-column-linear-acceleration = Ускорение X/Y/Z
tracker-table-column-rotation = Поворот X/Y/Z
tracker-table-column-position = Положение X/Y/Z
tracker-table-column-stay_aligned = Оставаться выровненным

## Tracker rotation

tracker-rotation-front = Спереди
tracker-rotation-front_left = Левая сторона передней части
tracker-rotation-front_right = Правая сторона передней части
tracker-rotation-left = Слева
tracker-rotation-right = Справа
tracker-rotation-back = Назад
tracker-rotation-back_left = Левая сторона задней части
tracker-rotation-back_right = Правая сторона задней части
tracker-rotation-custom = Пользовательское

## Tracker information

tracker-infos-manufacturer = Производитель
tracker-infos-display_name = Отображаемое имя
tracker-infos-custom_name = Свое имя
tracker-infos-url = URL трекера
tracker-infos-hardware_identifier = ID оборудования
tracker-infos-imu = Датчик IMU
tracker-infos-board_type = Основная плата
tracker-infos-network_version = Версия протокола
tracker-infos-magnetometer = Магнитометр
tracker-infos-magnetometer-status-v1 =
    { $status ->
        [DISABLED] Выключено
        [ENABLED] Включено
       *[NOT_SUPPORTED] Не Поддерживается
    }

## Tracker settings

tracker-settings-back = Вернуться к списку трекеров
tracker-settings-title = Настройки трекера
tracker-settings-assignment_section = Привязка
tracker-settings-assignment_section-description = К какой части тела привязан трекер.
tracker-settings-assignment_section-edit = Изменить привязку
tracker-settings-mounting_section = Положение крепления
tracker-settings-mounting_section-description = Где прикреплен трекер?
tracker-settings-mounting_section-edit = Изменить прикрепление
tracker-settings-use_mag = Разрешить использование магнитометра для этого трекера
# Multiline!
tracker-settings-use_mag-description = Должен ли этот трекер использовать магнитометр для компенсации дрифта, когда использование магнитометра разрешено?<b>Пожалуйста, не выключайте трекер во время включения данной функции!</b> Вам сначала нужно разрешить использование магнитометра, <magSetting>нажмите здесь чтобы зайти в настройки</magSetting>.
tracker-settings-use_mag-label = Разрешить магнитометр
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Имя трекера
tracker-settings-name_section-placeholder = Левая нога NightyBeast'а
tracker-settings-name_section-label = Имя трекера
tracker-settings-forget = Забыть трекер
tracker-settings-forget-description = Убирает трекер с SlimeVR Сервер и запрещает ему подключаться к серверу до того как он будет перезапущен. Конфигурация трекера не будет потеряна.
tracker-settings-forget-label = Забыть трекер
tracker-settings-update-low-battery = Невозможно обновить. Заряд батареи менее 50%
tracker-settings-update-up_to_date = Обновлено
tracker-settings-update = Обновить сейчас
tracker-settings-update-title = Версия прошивки

## Dongle settings

dongle-infos-hardware_revision = Ревизия устройства
dongle-status-disconnected = Отключен
dongle-settings-back = Вернуться к списку трекеров
dongle-settings-update = Обновить сейчас
dongle-settings-update-title = Версия прошивки

## Tracker part card info

tracker-part_card-unassigned = Не привязан

## Body assignment menu

body_assignment_menu = Где вы хотите расположить этот трекер?
body_assignment_menu-description = Выберите местоположение, куда вы хотите назначить этот трекер. В качестве альтернативы вы можете выбрать управление всеми трекерами сразу, а не по одному.
body_assignment_menu-manage_trackers = Настроить все трекеры
body_assignment_menu-unassign_tracker = Отвязать трекер

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Внимание:</b> Трекер шеи может быть смертельно опасен, если его затянуть слишком туго,
    ремешок трекера может нарушить кровообращение к вашей голове!
tracker_selection_menu-neck_warning-done = Я понимаю риски
tracker_selection_menu-neck_warning-cancel = Отмена

## Mounting menu

mounting_selection_menu-close = Закрыть

## Sidebar settings

settings-sidebar-title = Настройки
settings-sidebar-general = Общие
settings-sidebar-stay_aligned = Оставаться выровненным
settings-sidebar-trackers = Трекеры
settings-sidebar-interface = Интерфейс
settings-sidebar-utils = Утилиты
settings-sidebar-appearance = Внешний вид
settings-sidebar-notifications = Уведомления
settings-sidebar-firmware-tool = Инструмент Прошивки DIY
settings-sidebar-vrc_warnings = Предупреждения конфигурации VRChat
settings-sidebar-advanced = Продвинутые

## Bone routing settings

settings-routing-output-badge-off = Отключено
settings-routing-hands-warning-cancel = Отмена

## SteamVR / Monado output settings

settings-driver-enable = Включить
settings-driver-status-badge-disabled = Отключено

## Tracker mechanics

settings-general-tracker_mechanics-filtering = Фильтрация
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Выберите тип фильтрации для ваших трекеров.
    Прогнозирование предсказывает движение, в то время как сглаживание сглаживает движение.
settings-general-tracker_mechanics-filtering-type-none = Нет фильтрации
settings-general-tracker_mechanics-filtering-type-none-description = Используется вращение как есть. Не применяет никакой фильтрации.
settings-general-tracker_mechanics-filtering-type-smoothing = Сглаживание
settings-general-tracker_mechanics-filtering-type-smoothing-description = Сглаживает движения, но добавляет некоторую задержку.
settings-general-tracker_mechanics-filtering-type-prediction = Предсказывание
settings-general-tracker_mechanics-filtering-type-prediction-description = Уменьшает задержку и делает движения более быстрыми, но может увеличить дрожание.
settings-general-tracker_mechanics-save_mounting_reset = Сохранить калибровку автоматического сброса крепления
settings-general-tracker_mechanics-save_mounting_reset-description =
    Сохраняет автоматические калибровки сброса крепления для трекеров между перезапусками. Полезно
    при ношении костюма, в котором трекеры не перемещаются между сессиями. <b>Не рекомендуется для обычных пользователей!</b>
settings-general-tracker_mechanics-save_mounting_reset-enabled-label = Сохранить сброс крепления
settings-general-tracker_mechanics-use_mag_on_all_trackers = Использовать магнитометр на всех IMU трекерах, которые его поддерживают
settings-general-tracker_mechanics-use_mag_on_all_trackers-description =
    Использует магнитометр на всех трекерах, которые имеют совместимую с ним прошивку, уменьшая дрифт в стабильных магнитных средах.
    Может быть отключен для каждого трекера в настройках трекера. <b>Пожалуйста, не выключайте ни один из трекеров во время переключения!</b>
settings-general-tracker_mechanics-use_mag_on_all_trackers-label = Использовать магнитометр трекеров
settings-stay_aligned-description = Функция "Оставаться выровненным" уменьшает дрифт, постепенно настраивая трекеры в соответствии с вашими расслабленными позами
settings-stay_aligned-setup-label = Настройки "Оставаться выровненным"
settings-stay_aligned-setup-description = Необходимо завершить "Настройки "Оставаться выровненным"", чтобы включить эту функцию.
settings-stay_aligned-enabled-label = Калибровка трекеров
settings-stay_aligned-general-label = Общие
settings-stay_aligned-relaxed_poses-label = Расслабленные позы
settings-stay_aligned-relaxed_poses-description = Функция "Оставаться выровненным" использует ваши расслабленные позы, чтобы трекеры оставались выровнены. Используйте "Настройки "Оставаться выровненным"", чтобы обновить эти позы.
settings-stay_aligned-relaxed_poses-standing = Калибровка трекеров в положении стоя
settings-stay_aligned-relaxed_poses-sitting = Калибровка трекеров в положении сидя (на стуле)
settings-stay_aligned-relaxed_poses-flat = Калибровка трекеров в положении сидя (на полу) или лежа (на спине)
settings-stay_aligned-relaxed_poses-save_pose = Сохранить позу
settings-stay_aligned-relaxed_poses-reset_pose = Сбросить позу
settings-stay_aligned-relaxed_poses-close = Закрыть
settings-stay_aligned-debug-label = Отладка
settings-stay_aligned-debug-description = Пожалуйста, укажите ваши настройки при отправке сообщения о проблемах с функцией "Оставаться выровненным".
settings-stay_aligned-debug-copy-label = Копирование настроек в буфер обмена

## Keybinds Page

settings-keybinds_full-reset = Полный сброс
settings-keybinds_yaw-reset = Горизонтальный сброс
settings-keybinds_reset-all-button = Сбросить всё
settings-keybinds-recorder-modal-done-button = Выполнено
settings-keybinds-recorder-modal-cancel-button = Отмена

## FK/Tracking settings

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
settings-general-fk_settings-leg_tweak-skating_correction-description = Коррекция скольжения корректирует скольжение по полу, но может снизить точность некоторых форм движений. При включении обязательно выполните полный сброс и повторную калибровку в игре.
settings-general-fk_settings-leg_tweak-floor_clip-description = Привязка к полу может уменьшить или даже полностью исключить прохождение через пол. При включении обязательно выполните полный сброс и повторную калибровку в игре.
settings-general-fk_settings-leg_tweak-toe_snap-description = Toe-snap пытается угадать вращение ваших ступней, если трекеры для них не используются.
settings-general-fk_settings-leg_tweak-foot_plant-description = Foot-Plant поворачивает ваши ступни так, чтобы они были параллельны земле при контакте.
settings-general-fk_settings-leg_fk = Отслеживание ног
settings-general-fk_settings-enforce_joint_constraints = Ограничения Скелета
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints = Применять ограничения
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = Предотвращает вращение суставов за пределы их возможностей
settings-general-fk_settings-arm_fk-back = Назад
settings-general-fk_settings-arm_fk-back-description = Режим по умолчанию, в котором плечи идут назад, а предплечья — вперед.
settings-general-fk_settings-arm_fk-tpose_up = Т-поза (вверх)
settings-general-fk_settings-arm_fk-tpose_up-description = Ожидает, что ваши руки будут опущены во время Полного Сброса, и на 90 градусов вверх в стороны во время сброса крепления.
settings-general-fk_settings-arm_fk-tpose_down = Т-поза (вниз)
settings-general-fk_settings-arm_fk-tpose_down-description = Ожидает, что ваши руки будут подняты на 90 градусов вверх во время Полного Сброса, и опущены во время сброса крепления.
settings-general-fk_settings-arm_fk-forward = Вперёд
settings-general-fk_settings-arm_fk-forward-description = Ожидает, что ваши руки будут подняты на 90 градусов вперед. Полезно для VTube'инга.
settings-general-fk_settings-skeleton_settings-ratios = Соотношения скелета
settings-general-fk_settings-skeleton_settings-ratios-description = Измените значения параметров скелета. Возможно, вам придется скорректировать пропорции после их изменения.
settings-general-fk_settings-self_localization-title = Режим Mocap

## Gesture control settings (tracker tapping)

settings-general-gesture_control-subtitle = Сброс после нажатия
settings-general-gesture_control-description = Позволяет запускать сброс настроек нажатием на трекер. Трекер, расположенный выше всего на вашем торсе, используется для быстрого сброса, трекер, расположенный выше всего на вашей левой ноге, используется для сброса, а трекер, расположенный выше всего на вашей правой ноге, используется для сброса установок. Нажатия должны происходить в течение 0.3 секунд, умноженное на количество нажатий для регистрации нажатия.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
        [one] { $amount } нажатие
        [few] { $amount } нажатия
        [many] { $amount } нажатий
       *[other] { $amount } нажатий
    }
# This is a unit: 3 trackers, 2 trackers, 1 tracker
# $amount (Number) - Amount of trackers
settings-general-gesture_control-trackers =
    { $amount ->
        [one] 1 трекер
        [few] { $amount } трекера
        [many] { $amount } трекеров
       *[other] { $amount } трекеров
    }
settings-general-gesture_control-yawResetEnabled = Включить нажатие для сброса
settings-general-gesture_control-yawResetDelay = Задержка сброса по рысканью
settings-general-gesture_control-yawResetTaps = Нажатия для сброса
settings-general-gesture_control-fullResetEnabled = Включить нажатия для полного сброса
settings-general-gesture_control-fullResetDelay = Задержка полного сброса
settings-general-gesture_control-fullResetTaps = Нажатия для полного сброса
settings-general-gesture_control-mountingResetEnabled = Включить нажатия для сброса крепления
settings-general-gesture_control-mountingResetDelay = Задержка сброса крепления
settings-general-gesture_control-mountingResetTaps = Нажатия для сброса крепления
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = Трекеры, превышающие порог
settings-general-gesture_control-numberTrackersOverThreshold-description = Увеличьте это значение, если обнаружение нажатий не работает. Не увеличивайте его выше того, чем необходимо для работы обнаружения касания, иначе это приведет к большему количеству ложных срабатываний.

## Appearance settings

settings-interface-appearance = Внешний вид
settings-general-interface-dev_mode = Режим разработчика
settings-general-interface-dev_mode-description = Этот режим может быть полезен, если вам нужны подробные данные или для взаимодействия с подключенными трекерами на более продвинутом уровне.
settings-general-interface-dev_mode-label = Режим разработчика
settings-general-interface-lang = Выбрать язык
settings-general-interface-lang-description = Измените язык по умолчанию, который вы хотите использовать.
settings-general-interface-lang-placeholder = Выберите язык для использования
# Keep the font name untranslated
settings-interface-appearance-font = Шрифт GUI
settings-interface-appearance-font-description = Это изменяет шрифт, используемый интерфейсом.
settings-interface-appearance-font-placeholder = Шрифт по умолчанию
settings-interface-appearance-font-os_font = Шрифт ОС
settings-interface-appearance-font-slime_font = Шрифт по умолчанию
settings-interface-appearance-font_size = Масштабирование шрифта
settings-interface-appearance-font_size-description = Это влияет на размер шрифта всего интерфейса, за исключением этой панели настроек.

## Notification settings

settings-interface-notifications = Уведомления
settings-general-interface-feedback_sound = Звук уведомления
settings-general-interface-feedback_sound-description = Эта опция будет воспроизводить звук при срабатывании сброса.
settings-general-interface-feedback_sound-label = Звук уведомления
settings-general-interface-feedback_sound-volume = Громкость звука уведомления
settings-general-interface-connected_trackers_warning = Предупреждение о подключенных трекерах
settings-general-interface-connected_trackers_warning-description = Эта опция будет показывать предупреждение каждый раз, когда вы пытаетесь выйти из SlimeVR с одним или несколькими подключенными трекерами. Он напомнит вам о необходимости выключить трекеры, когда вы закончите, чтобы продлить срок службы батареи.
settings-general-interface-connected_trackers_warning-label = Предупреждение о подключенных трекеров при выходе

## Behavior settings

settings-general-interface-dev_mode = Режим разработчика
settings-general-interface-dev_mode-label = Режим разработчика
settings-general-interface-use_tray = Свернуть в системный трей
settings-general-interface-use_tray-description = Позволяет закрыть окно, не закрывая сервер SlimeVR, так что вы можете продолжать использовать его, не беспокоясь о графическом интерфейсе.
settings-general-interface-use_tray-label = Свернуть в системный трей
settings-general-interface-discord_presence = Поделиться активностью в Discord
settings-general-interface-discord_presence-description = Сообщает вашему приложению Discord, что вы используете SlimeVR, вместе с количеством IMU трекеров, которые вы используете.
settings-general-interface-discord_presence-label = Поделиться активностью в Discord
settings-general-interface-discord_presence-message =
    { $amount ->
        [0] Чиллим со Слаймами
        [one] Используется { $amount } трекер
        [few] Используется { $amount } трекера
       *[many] Используется { $amount } трекеров
    }
settings-interface-behavior-error_tracking = Сбор ошибок через Sentry.io
settings-interface-behavior-error_tracking-description_v2 =
    <h1>Даете ли вы согласие на сбор анонимных данных об ошибках?</h1>
    <b>Мы не собираем личную информацию</b>, такую как ваш IP адрес или учётные данные беспроводной сети. SlimeVR ценит вашу конфиденциальность!
    Чтобы обеспечить наилучший опыт для пользователей, мы собираем анонимные отчёты об ошибках, показатели производительности и информацию об операционной системе. Это помогает нам обнаруживать ошибки и проблемы со SlimeVR. Эти данные собираются с помощью Sentry.io.
settings-interface-behavior-error_tracking-label = Отправлять ошибки разработчикам

## Serial settings

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
settings-serial-serial_select = Выбрать серийный порт
settings-serial-auto_dropdown_item = Авто
settings-serial-save_logs = Сохранить в файл
settings-serial-send_command-warning-ok = Я знаю, что я делаю
settings-serial-send_command-warning-cancel = Отмена

## OSC VRChat settings

settings-osc-vrchat-enable = Включить
settings-osc-vrchat-enable-description = Переключить отправку и получение данных.
settings-osc-vrchat-enable-label = Включить
settings-osc-vrchat-network = Сетевые порты
settings-osc-vrchat-network-port_in =
    .label = Порт Вход
    .placeholder = Порт Входа (по умолчанию: 9001)
settings-osc-vrchat-network-port_out =
    .label = Порт Выход
    .placeholder = Порт Выхода (по умолчанию: 9000)
settings-osc-vrchat-network-address = Адрес сети
settings-osc-vrchat-network-address-description-v1 = Выберите, на какой адрес отправлять данные. Можно оставить нетронутым для VRChat.
settings-osc-vrchat-network-address-placeholder = VRChat IP адрес

## VRChat OSC status

settings-osc-vrchat-status-tracking = Вращение
settings-osc-vrchat-status-badge-error = Ошибка
settings-osc-vrchat-status-badge-unknown = Неизвестно

## VMC OSC settings

settings-osc-vmc = Виртуальный захват движения
# This cares about multilines
settings-osc-vmc-description =
    Измените настройки, специфичные для протокола VMC (Virtual Motion Capture)
    , чтобы отправлять данные о костях SlimeVR и получать данные о костях из других приложений.
settings-osc-vmc-enable = Включить
settings-osc-vmc-enable-description = Переключить отправку и получение данных.
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
settings-osc-vmc-network-address-placeholder = IPv4 адрес
settings-osc-vmc-vrm = VRM Модель
settings-osc-vmc-vrm-untitled_model = Модель без названия
settings-osc-vmc-vrm-file_select = Перетащите модель для использования, или <u>выбрать</u>

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

