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
tips-tap_setup = Вы можете медленно нажать 2 раза на свой трекер, чтобы выбрать его, вместо того чтобы выбирать его из меню.
tips-turn_on_tracker = Используете официальные трекеры SlimeVR? Не забудьте <b><em>включить трекер</em></b> после его подключения к ПК!
tips-failed_webgl = Не удалось инициализировать WebGL.

## Units


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
body_part-UPPER_CHEST = Верхняя часть груди
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
board_type-NODEMCU = NodeMCU
board_type-CUSTOM = Кастомная Плата
board_type-WROOM32 = WROOM32
board_type-WEMOSD1MINI = Wemos D1 Mini
board_type-TTGO_TBASE = TTGO T-Base
board_type-ESP01 = ESP-01
board_type-SLIMEVR = SlimeVR
board_type-LOLIN_C3_MINI = Lolin C3 Mini
board_type-BEETLE32C3 = Beetle ESP32-C3
board_type-ESP32C3DEVKITM1 = Espressif ESP32-C3 DevKitM-1
board_type-OWOTRACK = owoTrack
board_type-WRANGLER = Joycon через Wrangler
board_type-MOCOPI = Sony Mocopi
board_type-WEMOSWROOM02 = Wemos Wroom-02 D1 Mini
board_type-XIAO_ESP32C3 = Seeed Studio XIAO ESP32C3
board_type-HARITORA = Haritora
board_type-ESP32C6DEVKITC1 = Espressif ESP32-C6 DevKitC-1
board_type-GLOVE_IMU_SLIMEVR_DEV = SlimeVR Dev IMU Glove

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
skeleton_bone-CHEST_OFFSET = Смещение груди
skeleton_bone-CHEST_OFFSET-desc =
    Этот параметр позволяет сдвинуть ваш виртуальный трекер груди вверх или вниз, чтобы помочь
    с калибровкой в некоторых играх или приложениях, которые могут ожидать, что он будет выше или ниже.
skeleton_bone-CHEST = Длина груди
skeleton_bone-CHEST-desc =
    Это расстояние от центра вашей груди до центра вашего позвоночника.
    Чтобы откалибровать его,  убедитесь, что "Длина туловища" задана верно, и изменяйте значение в различных
    позициях (сидя, наклонившись, лёжа и т.д.), пока ваш виртуальный позвоночник не совпадёт с реальным.
skeleton_bone-WAIST = Длина талии
skeleton_bone-WAIST-desc =
    Это расстояние от центра вашего позвоночника до вашего пупка.
    Чтобы откалибровать его,  убедитесь, что "Длина туловища" задана верно, и изменяйте значение в различных
    позициях (сидя, наклонившись, лёжа и т.д.), пока ваш виртуальный позвоночник не совпадёт с реальным.
skeleton_bone-HIP = Длина таза
skeleton_bone-HIP-desc =
    Это расстояние от вашего пупка до ваших бедер.
    Чтобы откалибровать его,  убедитесь, что "Длина туловища" задана верно, и изменяйте значение в различных
    позициях (сидя, наклонившись, лёжа и т.д.), пока ваш виртуальный позвоночник не совпадет с реальным.
skeleton_bone-HIP_OFFSET = Смещение таза
skeleton_bone-HIP_OFFSET-desc =
    Этот параметр позволяет сдвинуть ваш виртуальный трекер бёдер вверх или вниз, чтобы помочь
    с калибровкой в некоторых играх или приложениях, которые могут ожидать, что он будет на вашей талии.
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
skeleton_bone-SKELETON_OFFSET = Смещение скелета
skeleton_bone-SKELETON_OFFSET-desc =
    Этот параметр позволяет сдвинуть все трекеры вперёд или назад.
    Его можно использовать для помощи с калибровкой в некоторых играх или приложениях,
    которые могут ожидать, что ваши трекеры будут вынесены дальше вперёд.
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
skeleton_bone-ELBOW_OFFSET = Смещение локтя
skeleton_bone-ELBOW_OFFSET-desc =
    Это параметр позволяет сдвинуть ваш виртуальный трекер локтей вверх или вниз, чтобы помочь
    со случайной привязкой трекеров локтей к груди при использовании с VRChat.

## Tracker reset buttons

reset-reset_all = Сбросить все пропорции
reset-reset_all_warning-v2 =
    <b>Внимание:<b> Ваши пропорции будут сброшены до дефолтных размеров в соответствии заданному росту.
    Вы уверены, что хотите это сделать?
reset-reset_all_warning-reset = Сброс пропорций
reset-reset_all_warning-cancel = Отмена
reset-reset_all_warning_default-v2 =
    <b>Внимание:</b> Вы не настроили свой рост в SlimeVR, ваши пропорции будут сброшены до дефолтных вместе с ростом.
    Вы уверены, что хотите это сделать?
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
navbar-onboarding = Установщик
navbar-settings = Настройки

## Biovision hierarchy recording

bvh-start_recording = Запись BVH
bvh-recording = Запись...

## Tracking pause

tracking-unpaused = Приостановить отслеживание
tracking-paused = Возобновить отслеживание

## Widget: Overlay settings

widget-overlay = Оверлей
widget-overlay-is_visible_label = Показывать оверлей в SteamVR
widget-overlay-is_mirrored_label = Показывать оверлей как зеркало

## Widget: Drift compensation

widget-drift_compensation-clear = Очистить компенсацию дрифта

## Widget: Clear Mounting calibration

widget-clear_mounting = Обнулить сброс выравнивания

## Widget: Developer settings

widget-developer_mode = Режим разработчика
widget-developer_mode-high_contrast = Высокая контрастность
widget-developer_mode-precise_rotation = Точное вращение
widget-developer_mode-fast_data_feed = Быстрый поток данных
widget-developer_mode-filter_slimes_and_hmd = Фильтровать трекеры SlimeVR и HMD
widget-developer_mode-sort_by_name = Сортировка по имени
widget-developer_mode-raw_slime_rotation = Вращение без обработки
widget-developer_mode-more_info = Дополнительная информация

## Widget: IMU Visualizer

widget-imu_visualizer = Вращение
widget-imu_visualizer-preview = Предпросмотр
widget-imu_visualizer-hide = Скрыть
widget-imu_visualizer-rotation_raw = RAW
widget-imu_visualizer-rotation_preview = Предпросмотр
widget-imu_visualizer-acceleration = Ускорение
widget-imu_visualizer-position = Позиция
widget-imu_visualizer-stay_aligned = Оставаться выровненным

## Widget: Skeleton Visualizer

widget-skeleton_visualizer-preview = Предпросмотр скелета
widget-skeleton_visualizer-hide = Скрыть

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
tracker-table-column-tps = TPS
tracker-table-column-temperature = Темп. °C
tracker-table-column-linear-acceleration = Ускорение X/Y/Z
tracker-table-column-rotation = Поворот X/Y/Z
tracker-table-column-position = Положение X/Y/Z
tracker-table-column-stay_aligned = Оставаться выровненным
tracker-table-column-url = URL

## Tracker rotation

tracker-rotation-front = Спереди
tracker-rotation-front_left = Левая сторона передней части
tracker-rotation-front_right = Правая сторона передней части
tracker-rotation-left = Слева
tracker-rotation-right = Справа
tracker-rotation-back = Сзади
tracker-rotation-back_left = Левая сторона задней части
tracker-rotation-back_right = Правая сторона задней части
tracker-rotation-custom = Пользовательское
tracker-rotation-overriden = (перезаписан крепёжным сбросом)

## Tracker information

tracker-infos-manufacturer = Производитель
tracker-infos-display_name = Отображаемое имя
tracker-infos-custom_name = Свое имя
tracker-infos-url = URL трекера
tracker-infos-version = Версия прошивки
tracker-infos-hardware_rev = Ревизия устройства
tracker-infos-hardware_identifier = ID оборудования
tracker-infos-data_support = Поддержка данных
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
tracker-settings-drift_compensation_section = Разрешить компенсацию дрейфа
tracker-settings-drift_compensation_section-description = Должен ли этот трекер компенсировать свой дрифт?
tracker-settings-drift_compensation_section-edit = Разрешить компенсацию дрейфа
tracker-settings-use_mag = Разрешить использование магнитометра для этого трекера
# Multiline!
tracker-settings-use_mag-description = Должен ли этот трекер использовать магнитометр для компенсации дрифта, когда использование магнитометра разрешено?<b>Пожалуйста, не выключайте трекер во время включения данной функции!</b> Вам сначала нужно разрешить использование магнитометра, <magSetting>нажмите здесь чтобы зайти в настройки</magSetting>.
tracker-settings-use_mag-label = Разрешить магнитометр
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Имя трекера
tracker-settings-name_section-description = Дайте ему милое имя :)
tracker-settings-name_section-placeholder = Левая нога NightyBeast'а
tracker-settings-name_section-label = Имя трекера
tracker-settings-forget = Забыть трекер
tracker-settings-forget-description = Убирает трекер с SlimeVR Сервер и запрещает ему подключаться к серверу до того как он будет перезапущен. Конфигурация трекера не будет потеряна.
tracker-settings-forget-label = Забыть трекер
tracker-settings-update-low-battery = Невозможно обновить. Заряд батареи менее 50%
tracker-settings-update-up_to_date = Обновлено
tracker-settings-update = Обновить сейчас
tracker-settings-update-title = Версия прошивки

## Tracker part card info

tracker-part_card-no_name = Нет имени
tracker-part_card-unassigned = Не привязан

## Body assignment menu

body_assignment_menu = Где вы хотите расположить этот трекер?
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
-tracker_selection-part = Какой трекер привязать к
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
tracker_selection_menu-UPPER_CHEST = { -tracker_selection-part } верхняя часть груди?
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
    <b>Внимание:</b> Трекер шеи может быть смертельно опасен, если его затянуть слишком туго,
    ремешок трекера может нарушить кровообращение к вашей голове!
tracker_selection_menu-neck_warning-done = Я понимаю риски
tracker_selection_menu-neck_warning-cancel = Отмена

## Mounting menu

mounting_selection_menu = Где вы хотите расположить этот трекер?
mounting_selection_menu-close = Закрыть

## Sidebar settings

settings-sidebar-title = Настройки
settings-sidebar-general = Общие
settings-sidebar-tracker_mechanics = Настройки трекеров
settings-sidebar-stay_aligned = Оставаться выровненным
settings-sidebar-fk_settings = Настройки отслеживания
settings-sidebar-gesture_control = Настройки жестов
settings-sidebar-interface = Интерфейс
settings-sidebar-osc_router = OSC роутер
settings-sidebar-osc_trackers = VRChat OSC Трекеры
settings-sidebar-utils = Утилиты
settings-sidebar-serial = Консоль
settings-sidebar-appearance = Внешний вид
settings-sidebar-notifications = Уведомления
settings-sidebar-behavior = Поведение
settings-sidebar-firmware-tool = Инструмент Прошивки DIY
settings-sidebar-vrc_warnings = Предупреждения конфигурации VRChat
settings-sidebar-advanced = Продвинутые

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
settings-general-steamvr-trackers-left_foot = Левая ступня
settings-general-steamvr-trackers-right_foot = Правая ступня
settings-general-steamvr-trackers-left_knee = Левое колено
settings-general-steamvr-trackers-right_knee = Правое колено
settings-general-steamvr-trackers-left_elbow = Левый локоть
settings-general-steamvr-trackers-right_elbow = Правый локоть
settings-general-steamvr-trackers-left_hand = Левая рука
settings-general-steamvr-trackers-right_hand = Правая рука
settings-general-steamvr-trackers-tracker_toggling = Автоматическое назначение трекеров
settings-general-steamvr-trackers-tracker_toggling-description = Автоматически включает и выключает трекеры SteamVR в зависимости от ваших текущих назначений трекеров
settings-general-steamvr-trackers-tracker_toggling-label = Автоматическое назначение трекеров
settings-general-steamvr-trackers-hands-warning =
    <b>Внимание:</b> трекеры рук переопределят ваши контроллеры.
    Вы уверены?
settings-general-steamvr-trackers-hands-warning-cancel = Отмена
settings-general-steamvr-trackers-hands-warning-done = Да

## Tracker mechanics

settings-general-tracker_mechanics = Настройки трекеров
settings-general-tracker_mechanics-filtering = Фильтрация
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Выберите тип фильтрации для ваших трекеров.
    Прогнозирование предсказывает движение, в то время как сглаживание сглаживает движение.
settings-general-tracker_mechanics-filtering-type = Тип фильтрации
settings-general-tracker_mechanics-filtering-type-none = Нет фильтрации
settings-general-tracker_mechanics-filtering-type-none-description = Используется вращение как есть. Не применяет никакой фильтрации.
settings-general-tracker_mechanics-filtering-type-smoothing = Сглаживание
settings-general-tracker_mechanics-filtering-type-smoothing-description = Сглаживает движения, но добавляет некоторую задержку.
settings-general-tracker_mechanics-filtering-type-prediction = Предсказывание
settings-general-tracker_mechanics-filtering-type-prediction-description = Уменьшает задержку и делает движения более быстрыми, но может увеличить дрожание.
settings-general-tracker_mechanics-filtering-amount = Количество
settings-general-tracker_mechanics-yaw-reset-smooth-time = Время сглаживания сброса отклонения (0 сек. отключает сглаживание)
settings-general-tracker_mechanics-drift_compensation = Компенсация дрейфа
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Компенсирует дрейф IMU по рысканию путем применения обратного вращения.
    Измените количество компенсации и до скольких сбросов учитывается.
settings-general-tracker_mechanics-drift_compensation-enabled-label = Компенсация дрейфа
settings-general-tracker_mechanics-drift_compensation-prediction = Прогноз компенсации дрифта
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-prediction-description =
    Прогнозирует компенсацию дрифта при повороте за пределы ранее измеренного диапазона.
    Включите эту опцию, если трекер постоянно вращается по оси поворота.
settings-general-tracker_mechanics-drift_compensation-prediction-label = Прогноз компенсации дрифта
settings-general-tracker_mechanics-drift_compensation_warning =
    <b>Предупреждение:</b> Используйте компенсацию дрифта только в том случае, если вам
    приходится сбрасывать настройки очень часто (каждые ~5-10 минут).
    
    IMU, склонные к частым сбросам, включают:
    Joy-Con, owoTrack, и MPU (без последней прошивки).
settings-general-tracker_mechanics-drift_compensation_warning-cancel = Отмена
settings-general-tracker_mechanics-drift_compensation_warning-done = Я понимаю
settings-general-tracker_mechanics-drift_compensation-amount-label = Кол-во компенсации
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Использовать до x последних сбросов
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
settings-stay_aligned = Оставаться выровненным
settings-stay_aligned-description = Функция "Оставаться выровненным" уменьшает дрифт, постепенно настраивая трекеры в соответствии с вашими расслабленными позами
settings-stay_aligned-setup-label = Настройки "Оставаться выровненным"
settings-stay_aligned-setup-description = Необходимо завершить "Настройки "Оставаться выровненным"", чтобы включить эту функцию.
settings-stay_aligned-warnings-drift_compensation = ⚠ Пожалуйста, выключите компенсацию дрифта! Компенсация дрифта будет конфликтовать с функцией "Оставаться выровненным".
settings-stay_aligned-enabled-label = Калибровка трекеров
settings-stay_aligned-hide_yaw_correction-label = Скрыть настройку (для сравнения без функции "Оставаться выровненным")
settings-stay_aligned-general-label = Общие
settings-stay_aligned-relaxed_poses-label = Расслабленные позы
settings-stay_aligned-relaxed_poses-description = Функция "Оставаться выровненным" использует ваши расслабленные позы, чтобы трекеры оставались выровнены. Используйте "Настройки "Оставаться выровненным"", чтобы обновить эти позы.
settings-stay_aligned-relaxed_poses-standing = Калибровка трекеров в положении стоя
settings-stay_aligned-relaxed_poses-sitting = Калибровка трекеров в положении сидя (на стуле)
settings-stay_aligned-relaxed_poses-flat = Калибровка трекеров в положении сидя (на полу) или лежа (на спине)
settings-stay_aligned-relaxed_poses-save_pose = Сохранить позу
settings-stay_aligned-relaxed_poses-reset_pose = Сбросить позу
settings-stay_aligned-debug-label = Отладка
settings-stay_aligned-debug-description = Пожалуйста, укажите ваши настройки при отправке сообщения о проблемах с функцией "Оставаться выровненным".
settings-stay_aligned-debug-copy-label = Копирование настроек в буфер обмена

## FK/Tracking settings

settings-general-fk_settings = Настройки трекинга
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
settings-general-fk_settings-enforce_joint_constraints-correct_constraints = Исправлять с учётом ограничений
settings-general-fk_settings-enforce_joint_constraints-correct_constraints-description = Исправлять вращение суставов, когда оно выходит за пределы их возможностей
settings-general-fk_settings-arm_fk = Отслеживание рук
settings-general-fk_settings-arm_fk-description = Принуждает к отслеживанию рук из гарнитуры (HMD), даже если доступны данные о положении рук.
settings-general-fk_settings-arm_fk-force_arms = Руки от HMD
settings-general-fk_settings-reset_settings = Сбросить настройки
settings-general-fk_settings-reset_settings-reset_hmd_pitch-description = Сбросить уклон HMD (вертикальное вращение) после полного сброса. Полезно при ношении шлема на лбу для VTubing-а или mocap-а. Не включайте для VR.
settings-general-fk_settings-reset_settings-reset_hmd_pitch = Сбросить уклон HMD
settings-general-fk_settings-arm_fk-reset_mode-description = Изменение ожидаемой позы руки для сброса крепления.
settings-general-fk_settings-arm_fk-back = Назад
settings-general-fk_settings-arm_fk-back-description = Режим по умолчанию, в котором плечи идут назад, а предплечья — вперед.
settings-general-fk_settings-arm_fk-tpose_up = Т-поза (вверх)
settings-general-fk_settings-arm_fk-tpose_up-description = Ожидает, что ваши руки будут опущены во время Полного Сброса, и на 90 градусов вверх в стороны во время сброса крепления.
settings-general-fk_settings-arm_fk-tpose_down = Т-поза (вниз)
settings-general-fk_settings-arm_fk-tpose_down-description = Ожидает, что ваши руки будут подняты на 90 градусов вверх во время Полного Сброса, и опущены во время сброса крепления.
settings-general-fk_settings-arm_fk-forward = Вперёд
settings-general-fk_settings-arm_fk-forward-description = Ожидает, что ваши руки будут подняты на 90 градусов вперед. Полезно для VTube'инга.
settings-general-fk_settings-skeleton_settings-toggles = Переключатели скелета
settings-general-fk_settings-skeleton_settings-description = Включите или выключите настройки скелета. Рекомендуется оставить их включенными.
settings-general-fk_settings-skeleton_settings-extended_spine_model = Удлинённая модель позвоночника
settings-general-fk_settings-skeleton_settings-extended_pelvis_model = Удлинённая модель таза
settings-general-fk_settings-skeleton_settings-extended_knees_model = Удлинённая модель колен
settings-general-fk_settings-skeleton_settings-ratios = Соотношения скелета
settings-general-fk_settings-skeleton_settings-ratios-description = Измените значения параметров скелета. Возможно, вам придется скорректировать пропорции после их изменения.
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_hip = Рассчитать талию от груди до бёдер
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_legs = Рассчитать талию от груди до ног
settings-general-fk_settings-skeleton_settings-impute_hip_from_chest_legs = Рассчитать бедро от груди до ног
settings-general-fk_settings-skeleton_settings-impute_hip_from_waist_legs = Рассчитать бедро от талии до ног
settings-general-fk_settings-skeleton_settings-interp_hip_legs = Усредните рыскание и перекат бедра c рысканьем и перекатом ног
settings-general-fk_settings-skeleton_settings-interp_knee_tracker_ankle = Усредните рыскание и крен коленных трекеров с рысканьем и креном трекеров лодыжек
settings-general-fk_settings-skeleton_settings-interp_knee_ankle = Усреднить угол поворота и переката для коленей и лодыжек
settings-general-fk_settings-self_localization-title = Режим Mocap
settings-general-fk_settings-self_localization-description = Режим Mocap позволяет скелету примерно отслеживать свое собственное положение без использования гарнитуры или других трекеров. Обратите внимание, что для работы этого требуются трекеры ног и головы, и это все еще экспериментальный метод.

## Gesture control settings (tracker tapping)

settings-general-gesture_control = Управление жестами
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
settings-general-interface-theme = Варианты оформления
settings-general-interface-show-navbar-onboarding = Показать "{ navbar-onboarding }" на панели навигации
settings-general-interface-show-navbar-onboarding-description = Это изменит, будет ли кнопка "{ navbar-onboarding }" показываться в панели задач.
settings-general-interface-show-navbar-onboarding-label = Показать "{ navbar-onboarding }"
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
settings-interface-appearance-decorations = Использовать стандартные системные украшения
settings-interface-appearance-decorations-description = Это не будет отображать верхнюю панель интерфейса, а вместо этого будет использоваться панель операционной системы.
settings-interface-appearance-decorations-label = Использовать стандартные украшения

## Notification settings

settings-interface-notifications = Уведомления
settings-general-interface-serial_detection = Обнаружение серийного устройства
settings-general-interface-serial_detection-description = Эта опция будет показывать всплывающее окно каждый раз, когда вы подключаете новое серийное устройство, которое может быть трекером. Это помогает улучшить процесс настройки трекера.
settings-general-interface-serial_detection-label = Обнаружение серийного устройства
settings-general-interface-feedback_sound = Звук уведомления
settings-general-interface-feedback_sound-description = Эта опция будет воспроизводить звук при срабатывании сброса.
settings-general-interface-feedback_sound-label = Звук уведомления
settings-general-interface-feedback_sound-volume = Громкость звука уведомления
settings-general-interface-connected_trackers_warning = Предупреждение о подключенных трекерах
settings-general-interface-connected_trackers_warning-description = Эта опция будет показывать предупреждение каждый раз, когда вы пытаетесь выйти из SlimeVR с одним или несколькими подключенными трекерами. Он напомнит вам о необходимости выключить трекеры, когда вы закончите, чтобы продлить срок службы батареи.
settings-general-interface-connected_trackers_warning-label = Предупреждение о подключенных трекеров при выходе

## Behavior settings

settings-interface-behavior = Поведение
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

settings-serial = Консоль
# This cares about multilines
settings-serial-description =
    Это информационный канал для серийной связи.
    Может быть полезным для отладки прошивки или оборудования.
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
settings-serial-get_wifi_scan = Получить сканирование Wi-Fi
settings-serial-file_type = Обычный текст
settings-serial-save_logs = Сохранить в файл

## OSC router settings

settings-osc-router = OSC роутер
# This cares about multilines
settings-osc-router-description =
    Пересылает OSC-сообщения из другой программы.
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
    .label = Порт Входа
    .placeholder = Порт Входа (по умолчанию: 9002)
settings-osc-router-network-port_out =
    .label = Порт Выхода
    .placeholder = Порт Выхода (по умолчанию: 9000)
settings-osc-router-network-address = Адрес сети
settings-osc-router-network-address-description = Задайте адрес для отправки данных.
settings-osc-router-network-address-placeholder = IPv4 адрес

## OSC VRChat settings

settings-osc-vrchat = VRChat OSC Трекеры
# This cares about multilines
settings-osc-vrchat-description-v1 =
    Изменение настроек, специфичных для стандарта OSC Трекеров, используемых для отправки
    данных о трекинге приложениям без SteamVR (например, Oculus Quest).
    Убедитесь, что вы включили OSC в VRChat через меню действия в OSC > Включено.
    Чтобы разрешить получение данных об HMD и контроллерах от VRChat, перейдите в настройки в главном меню,
    и далее перейдите в Tracking & IK > Allow Sending Head and Wrist VR Tracking OSC Data.
settings-osc-vrchat-enable = Включить
settings-osc-vrchat-enable-description = Переключить отправку и получение данных.
settings-osc-vrchat-enable-label = Включить
settings-osc-vrchat-oscqueryEnabled = Включить OSCQuery
settings-osc-vrchat-oscqueryEnabled-description =
    OSCQuery автоматически обнаруживает запущенные инстанции VRChat и отправляет им данные.
    Он также может объявлять о себе для них, чтобы получать данные от шлема и контроллеров.
    Чтобы разрешить получение данных шлема и контроллеров от VRChat, перейдите в настройки
    из главного меню, зайдите в раздел "Отслеживание и IK" и включите "Доступ к отслеживанию
    головы и запястий через OSC".
settings-osc-vrchat-oscqueryEnabled-label = Включить OSCQuery
settings-osc-vrchat-network = Порты сети
settings-osc-vrchat-network-description-v1 = Настройте порты для прослушивания и отправки данных. Можно оставить нетронутым для VRChat.
settings-osc-vrchat-network-port_in =
    .label = Порт Входа
    .placeholder = Порт Входа (по умолчанию: 9001)
settings-osc-vrchat-network-port_out =
    .label = Порт Выхода
    .placeholder = Порт Выхода (по умолчанию: 9000)
settings-osc-vrchat-network-address = Адрес сети
settings-osc-vrchat-network-address-description-v1 = Выберите, на какой адрес отправлять данные. Можно оставить нетронутым для VRChat.
settings-osc-vrchat-network-address-placeholder = VRChat IP адрес
settings-osc-vrchat-network-trackers = Трекеры
settings-osc-vrchat-network-trackers-description = Переключить отправку определённых трекеров через OSC.
settings-osc-vrchat-network-trackers-chest = Грудь
settings-osc-vrchat-network-trackers-hip = Таз
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
settings-osc-vmc-vrm-description = Загрузите модель VRM, чтобы предоставить возможность крепления на голове и обеспечить большую совместимость с другими приложениями.
settings-osc-vmc-vrm-untitled_model = Модель без названия
settings-osc-vmc-vrm-file_select = Перетащите модель для использования, или <u>выбрать</u>
settings-osc-vmc-anchor_hip = Привязка к бедрам
settings-osc-vmc-anchor_hip-description = Привязать трекинг к бедрам, полезно для сидячего VTubing'а. Если выключено, загрузите VRM модель.
settings-osc-vmc-anchor_hip-label = Привязать к бедрам
settings-osc-vmc-mirror_tracking = Отзеркалить отслеживание
settings-osc-vmc-mirror_tracking-description = Отзеркалить отслеживание горизонтально.
settings-osc-vmc-mirror_tracking-label = Отзеркалить отслеживание

## Common OSC settings


## Advanced settings

settings-utils-advanced = Продвинутые
settings-utils-advanced-reset-gui = Сброс настроек графического интерфейса
settings-utils-advanced-reset-gui-description = Восстановить стандартные настройки интерфейса.
settings-utils-advanced-reset-gui-label = Сбросить графический интерфейс
settings-utils-advanced-reset-server = Сброс настроек отслеживания
settings-utils-advanced-reset-server-description = Восстановить стандартные настройки для отслеживания.
settings-utils-advanced-reset-server-label = Сбросить отслеживание
settings-utils-advanced-reset-all = Сбросить все настройки
settings-utils-advanced-reset-all-description = Восстановить стандартные настройки как для интерфейса, так и для отслеживания.
settings-utils-advanced-reset-all-label = Сбросить всё
settings-utils-advanced-reset_warning =
    { $type ->
        [gui]
            <b>Предупреждение:</b> Это сбросит ваши настройки графического интерфейса до значений по умолчанию.
            Вы уверены, что хотите это сделать?
        [server]
            <b>Предупреждение:</b> Это сбросит ваши настройки отслеживания до значений по умолчанию.
            Вы уверены, что хотите это сделать?
       *[all]
            <b>Предупреждение:</b> Это сбросит все ваши настройки до значений по умолчанию.
            Вы уверены, что хотите это сделать?
    }
settings-utils-advanced-reset_warning-reset = Сброс настроек
settings-utils-advanced-reset_warning-cancel = Отмена
settings-utils-advanced-open_data-v1 = Папка конфигурации
settings-utils-advanced-open_data-description-v1 = Открыть в проводнике папку конфигурации SlimeVR, содержащую файлы конфигурации
settings-utils-advanced-open_data-label = Открыть папку
settings-utils-advanced-open_logs = Папка логов
settings-utils-advanced-open_logs-description = Открыть в проводнике папку логов SlimeVR, содержащую логи приложения
settings-utils-advanced-open_logs-label = Открыть папку

## Home Screen


## Tracking Checlist


## Setup/onboarding menu

onboarding-skip = Пропустить установку
onboarding-continue = Продолжить
onboarding-wip = В разработке
onboarding-previous_step = Предыдущий шаг
onboarding-setup_warning =
    <b>Предупреждение.</b> Для правильного отслеживания требуется первоначальная настройка,
    она необходима, если вы впервые используете SlimeVR.
onboarding-setup_warning-skip = Пропустить настройку
onboarding-setup_warning-cancel = Продолжить настройку

## Wi-Fi setup

onboarding-wifi_creds-back = Вернуться к введению
onboarding-wifi_creds-skip = Пропустить настройки Wi-Fi
onboarding-wifi_creds-submit = Отправить!
onboarding-wifi_creds-ssid =
    .label = Имя Wi-Fi
    .placeholder = Введите имя Wi-Fi
onboarding-wifi_creds-ssid-required = Необходимо указать имя сети Wi-Fi
onboarding-wifi_creds-password =
    .label = Пароль
    .placeholder = Введите пароль Wi-Fi

## Mounting setup

onboarding-reset_tutorial-back = Вернуться к калибровке крепления
onboarding-reset_tutorial = Сбросить туториал
onboarding-reset_tutorial-explanation = Пока вы пользуетесь своими трекерами, они могут не выравниваться из-за дрейфа IMU по рысканью или из-за того, что вы, возможно, переместили их физически. У вас есть несколько способов, исправить это.
onboarding-reset_tutorial-skip = Пропустить шаг
# Cares about multiline
onboarding-reset_tutorial-0 =
    Коснитесь { $taps } раз выделенного трекера, чтобы активировать сброс рыскания.
    
    Это заставит трекеры "смотреть" в том же направлении, что и ваша гарнитура (HMD).
# Cares about multiline
onboarding-reset_tutorial-1 =
    Нажмите { $taps } раз выделенный трекер, чтобы запустить полный сброс.
    
    Вы должны стоять для этого в (i-позе). Существует задержка в 3 секунды (настраиваемая), прежде чем сброс произойдет.
    Это полностью сбрасывает положение и вращение всех ваших трекеров. Это должно исправить большинство проблем.
# Cares about multiline
onboarding-reset_tutorial-2 =
    Нажмите { $taps } несколько раз на выделенный трекер, чтобы активировать сброс настроек.
    
    Сброс крепления помогает узнать, как на самом деле на вас надеты трекеры, поэтому, если вы случайно переместили их и сильно изменили их ориентацию, это поможет.
    
    Вы должны быть в позе, как будто вы катаетесь на лыжах, как показано в мастере автоматического монтажа крепления, и у вас есть 3-секундная задержка (настраиваемая) перед тем, как она сработает.

## Setup start

onboarding-home = Добро пожаловать в SlimeVR
onboarding-home-start = Давайте все настроим!

## Setup done

onboarding-done-title = Всё готово!
onboarding-done-description = Наслаждайтесь игрой с полным отслеживанием тела!
onboarding-done-close = Закрыть установку

## Tracker connection setup

onboarding-connect_tracker-back = Вернуться к данным Wi-Fi
onboarding-connect_tracker-title = Подключите трекеры
onboarding-connect_tracker-description-p0-v1 = Теперь самое интересное - подключение трекеров!
onboarding-connect_tracker-description-p1-v1 = Подключите каждый трекер по одному через USB-порт.
onboarding-connect_tracker-issue-serial = У меня проблемы с подключением!
onboarding-connect_tracker-usb = USB трекер
onboarding-connect_tracker-connection_status-none = Поиск трекеров
onboarding-connect_tracker-connection_status-serial_init = Подключение к устройству
onboarding-connect_tracker-connection_status-obtaining_mac_address = Получение MAC адреса трекера
onboarding-connect_tracker-connection_status-provisioning = Отправка данных Wi-Fi
onboarding-connect_tracker-connection_status-connecting = Попытка подключения к Wi-Fi
onboarding-connect_tracker-connection_status-looking_for_server = Поиск сервера
onboarding-connect_tracker-connection_status-connection_error = Не удается подключиться к Wi-Fi
onboarding-connect_tracker-connection_status-could_not_find_server = Не удалось найти сервер
onboarding-connect_tracker-connection_status-done = Подключен к серверу
onboarding-connect_tracker-connection_status-no_serial_log = Не удалось получить логи от трекера
onboarding-connect_tracker-connection_status-no_serial_device_found = Не удалось найти трекер через USB
onboarding-connect_serial-error-modal-no_serial_log = Включен ли трекер?
onboarding-connect_serial-error-modal-no_serial_log-desc = Убедитесь, что трекер включен и подключен к компьютеру
onboarding-connect_serial-error-modal-no_serial_device_found = Трекеры не обнаружены
onboarding-connect_serial-error-modal-no_serial_device_found-desc =
    Подключите трекер с помощью прилагаемого USB-кабеля к компьютеру и включите трекер.
    Если это не помогло:
      - Попробуйте использовать другой USB-кабель
      - Попробуйте использовать другой USB-порт
      - Попробуйте переустановить сервер SlimeVR и выберите «USB-драйверы» в разделе компонентов
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
onboarding-connect_tracker-next = Я подключил все трекеры

## Tracker calibration tutorial

onboarding-calibration_tutorial = Пособие по калибровке IMU
onboarding-calibration_tutorial-subtitle = Это поможет уменьшить дрейф трекера!
onboarding-calibration_tutorial-calibrate = Я положил свои трекеры на стол
onboarding-calibration_tutorial-status-waiting = Ждем вас
onboarding-calibration_tutorial-status-calibrating = Калибровка
onboarding-calibration_tutorial-status-success = Хорошо!
onboarding-calibration_tutorial-status-error = Трекер был перемещен
onboarding-calibration_tutorial-skip = Пропуск туториала

## Tracker assignment tutorial

onboarding-assignment_tutorial = Как подготовить Slime Трекер перед тем, как надеть его
onboarding-assignment_tutorial-first_step = 1. Наклейте стикер с частью тела (если он у вас есть) на трекер по вашему выбору.
# This text has a character limit of around 11 characters, so please keep it short
onboarding-assignment_tutorial-sticker = Стикер
onboarding-assignment_tutorial-second_step-v2 = 2. Прикрепите ремешок к трекеру, держа липучку ремешка в том же направлении, что и верхняя сторона трекера:
onboarding-assignment_tutorial-second_step-continuation-v2 = Липучка расширения должна смотреть вверх, как показано на следующей картинке:
onboarding-assignment_tutorial-done = Я наклеил стикеры и ремешки!

## Tracker assignment setup

onboarding-assign_trackers-back = Вернуться к вводу данных Wi-Fi
onboarding-assign_trackers-title = Привязать трекеры
onboarding-assign_trackers-description = Давайте выберем расположение ваших трекеров. Нажмите на место, где вы хотите разместить трекер
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
onboarding-assign_trackers-mirror_view = Зеркальный вид
onboarding-assign_trackers-option-amount =
    { $trackersCount ->
        [one] x{ $trackersCount }
        [few] x{ $trackersCount }
       *[many] x{ $trackersCount }
    }
onboarding-assign_trackers-option-label =
    { $mode ->
        [lower-body] Комплект нижней части тела
        [core] Базовый комплект
        [enhanced-core] Улучшенный базовый комплект
        [full-body] Комплект для всего тела
       *[all] Все трекеры
    }
onboarding-assign_trackers-option-description =
    { $mode ->
        [lower-body] Минимум для отслеживания тела в VR
        [core] + Улучшенное отслеживание позвоночника
        [enhanced-core] + Отслеживание поворота ступней
        [full-body] + Отслеживание локтей
       *[all] Все доступные привязки для трекеров
    }

## Tracker assignment warnings

# Note for devs, number is used for representing boolean states per bit.
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_FOOT =
    { $unassigned ->
        [0] Назначена левая ступня, но вам нужно также назначить левую голень!
        [1] Назначена левая ступня, но вам нужно также назначить левое бедро!
        [2] Назначена левая ступня, но вам нужно также назначить левую голень и левое бедро!
        [3] Назначена левая ступня, но вам нужно также назначить либо грудь, таз или талию!
        [4] Назначена левая ступня, но вам нужно также назначить левую голень, а также либо грудь, таз или талию!
        [5] Назначена левая ступня, но вам нужно также назначить левое бедро, а также либо грудь, таз или талию!
        [6] Назначена левая ступня, но вам нужно также назначить левую голень, а также либо грудь, таз или талию!
       *[unknown] Назначена левая ступня, но вам нужно также назначить неизвестную неназначенную часть тела!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_FOOT =
    { $unassigned ->
        [0] Назначена правая ступня, но вам также нужно назначить правую голень!
        [1] Назначена правая ступня, но вам также нужно назначить правое бедро!
        [2] Назначена правая ступня, но вам также нужно назначить правую голень и правое бедро!
        [3] Назначена правая ступня, но вам также нужно назначить либо грудь, таз или талию!
        [4] Назначена правая ступня, но вам также нужно назначить правую голень, а также либо грудь, таз или талию!
        [5] Назначена правая ступня, но вам также нужно назначить правое бедро, а также либо грудь, таз или талию!
        [6] Назначена правая ступня, но вам также нужно назначить правую голень и правое бедро, а также либо грудь, таз или талию!
       *[unknown] Назначена правая ступня, но вам также нужно назначить неизвестную неназначенную часть тела!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_LOWER_LEG =
    { $unassigned ->
        [0] Назначена левая голень, но вам также нужно назначить левое бедро!
        [1] Назначена левая голень, но вам также нужно назначить либо грудь, таз или талию!
        [2] Назначена левая голень, но вам также нужно назначить левое бедро, а также либо грудь, таз или талию!
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

onboarding-choose_mounting = Какой метод калибровки крепления использовать?
# Multiline text
onboarding-choose_mounting-description = Ориентация крепления корректирует размещение трекеров на вашем теле.
onboarding-choose_mounting-auto_mounting = Автоматическая привязка
# Italicized text
onboarding-choose_mounting-auto_mounting-label-v2 = Рекомендуется
onboarding-choose_mounting-auto_mounting-description = Это автоматически определит направления монтажа для всех ваших трекеров из 2 поз
onboarding-choose_mounting-manual_mounting = Ручная привязка
# Italicized text
onboarding-choose_mounting-manual_mounting-label-v2 = Может быть недостаточно точным
onboarding-choose_mounting-manual_mounting-description = Это позволит вам выбрать направление монтажа вручную для каждого трекера
# Multiline text
onboarding-choose_mounting-manual_modal-title =
    Вы уверены, что хотите сделать
    Автоматическую калибровку крепления?
onboarding-choose_mounting-manual_modal-description = <b>Ручная калибровка крепления рекомендуется для новых пользователей</b>, так как позы автоматической калибровки крепления могут быть трудными для повторения и могут потребовать некоторой практики.
onboarding-choose_mounting-manual_modal-confirm = Я уверен в том, что делаю
onboarding-choose_mounting-manual_modal-cancel = Отмена

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
onboarding-automatic_mounting-preparation-v2-step-0 = 1. Нажмите кнопку "Полный сброс"
onboarding-automatic_mounting-preparation-v2-step-1 = 2. Встаньте прямо, вытянув руки по бокам. Убедитесь, что смотрите прямо перед собой.
onboarding-automatic_mounting-preparation-v2-step-2 = 3. Удерживайте положение в течение 3 секунд, пока не истечет таймер.
onboarding-automatic_mounting-put_trackers_on-title = Наденьте ваши трекеры
onboarding-automatic_mounting-put_trackers_on-description = Чтобы откалибровать повороты крепления, мы будем использовать трекеры, которые вы только что назначили. Включите все свои трекеры, вы можете увидеть, какие из них какие на рисунке справа.
onboarding-automatic_mounting-put_trackers_on-next = Я включил и надел все свои трекеры

## Tracker manual proportions setupa

onboarding-manual_proportions-title = Ручные пропорции тела
onboarding-manual_proportions-fine_tuning_button = Автоматически точно настроить пропорции
onboarding-manual_proportions-fine_tuning_button-disabled-tooltip = Пожалуйста, подключите VR-гарнитуру для использования автоматической тонкой настройки
onboarding-manual_proportions-export = Экспорт пропорций
onboarding-manual_proportions-import = Импорт пропорций
onboarding-manual_proportions-file_type = Файл пропорций тела
onboarding-manual_proportions-normal_increment = Нормальный шаг
onboarding-manual_proportions-precise_increment = Точный шаг
onboarding-manual_proportions-grouped_proportions = Сгруппированные пропорции
onboarding-manual_proportions-all_proportions = Все пропорции
onboarding-manual_proportions-estimated_height = Расчётный рост пользователя

## Tracker automatic proportions setup

onboarding-automatic_proportions-back = Вернутся к началу обучения
onboarding-automatic_proportions-title = Измерьте свое тело
onboarding-automatic_proportions-description = Чтобы трекеры SlimeVR работали, нам нужно знать длину ваших костей. Эта короткая калибровка измерит его для вас.
onboarding-automatic_proportions-manual = Ручная калибровка
onboarding-automatic_proportions-prev_step = Предыдущий шаг
onboarding-automatic_proportions-put_trackers_on-title = Наденьте ваши трекеры
onboarding-automatic_proportions-put_trackers_on-description = Чтобы откалибровать ваши пропорции, мы собираемся использовать трекеры, которые вы только что назначили. Включите все свои трекеры, вы можете увидеть, какие из них какие на рисунке справа.
onboarding-automatic_proportions-put_trackers_on-next = Я надел все свои трекеры
onboarding-automatic_proportions-requirements-title = Требования
# Each line of text is a different list item
onboarding-automatic_proportions-requirements-descriptionv2 =
    У вас достаточно трекеров, чтобы отслеживать ваши ноги (обычно 5 трекеров).
    У вас есть трекеры и гарнитура, и они на вас надеты.
    Ваши трекеры и гарнитура подключены к серверу SlimeVR, и правильно функционируют (например, отсутствие лагов, отключений и т. д.)
    Ваша гарнитура передает данные о местоположении на сервер SlimeVR (обычно это означает, что SteamVR запущен и подключен к SlimeVR с помощью драйвера SteamVR).
    Ваше отслеживание работает и точно отражает ваши движения (например, вы выполнили полный сброс, и они движутся в правильном направлении при ударах ногами, наклонах, сидении и т. д.).
onboarding-automatic_proportions-requirements-next = Я прочитал требования
onboarding-automatic_proportions-check_height-title-v3 = Измерить высоту вашей VR-гарнитуры
onboarding-automatic_proportions-check_height-description-v2 = Высота вашей VR-гарнитуры (шлема) должна быть немного меньше вашего полного роста, поскольку высота вашей VR-гарнитуры отражает высоту ваших глаз. Это измерение будет использоваться в качестве основы для пропорций вашего тела.
# All the text is in bold!
onboarding-automatic_proportions-check_height-calculation_warning-v3 = Начните измерение, стоя <u>прямо</u>, чтобы измерить свой рост. Не поднимайте руки выше гарнитуры, это может повлиять на точность измерения!
onboarding-automatic_proportions-check_height-guardian_tip = Если вы используете автономную гарнитуру виртуальной реальности, убедитесь, что у вас включена Граница, чтобы ваш рост был верным!
# Context is that the height is unknown
onboarding-automatic_proportions-check_height-unknown = Неизвестно
# Shows an element below it
onboarding-automatic_proportions-check_height-hmd_height2 = Высота вашей VR-гарнитуры:
onboarding-automatic_proportions-check_height-measure-start = Начать измерение
onboarding-automatic_proportions-check_height-measure-stop = Остановить измерение
onboarding-automatic_proportions-check_height-measure-reset = Повторить измерение
onboarding-automatic_proportions-check_height-next_step = С ними всё хорошо
onboarding-automatic_proportions-check_floor_height-title = Измерить высоту пола (опционально)
onboarding-automatic_proportions-check_floor_height-description = В некоторых случаях высота вашего пола может быть неверно установлена VR-гарнитурой, в результате чего измеренное значение высоты VR-гарнитуры будет выше действительного. Вы можете измерить "высоту" вашего пола, чтобы скорректировать данные вашей гарнитуры.
# All the text is in bold!
onboarding-automatic_proportions-check_floor_height-calculation_warning-v2 = Начните измерение и приложите контроллер к полу, чтобы измерить высоту. Если вы уверены, что высота пола задана верно, вы можете пропустить этот шаг.
# Shows an element below it
onboarding-automatic_proportions-check_floor_height-floor_height = Высота вашего пола:
onboarding-automatic_proportions-check_floor_height-full_height = Ваш расчётный полный рост:
onboarding-automatic_proportions-check_floor_height-measure-start = Начать измерение
onboarding-automatic_proportions-check_floor_height-measure-stop = Остановить измерение
onboarding-automatic_proportions-check_floor_height-measure-reset = Повторить измерение
onboarding-automatic_proportions-check_floor_height-skip_step = Пропустить шаг и сохранить
onboarding-automatic_proportions-check_floor_height-next_step = Использовать высоту пола и сохранить
onboarding-automatic_proportions-start_recording-title = Будьте готовы к движению
onboarding-automatic_proportions-start_recording-description = Теперь мы собираемся записать некоторые конкретные позы и движения. Они будут запрошены на следующем экране. Будьте готовы начать, когда кнопка будет нажата!
onboarding-automatic_proportions-start_recording-next = Начать запись
onboarding-automatic_proportions-recording-title = Запись
onboarding-automatic_proportions-recording-description-p0 = Запись в процессе...
onboarding-automatic_proportions-recording-description-p1 = Сделайте эти движения:
# Each line of text is a different list item
onboarding-automatic_proportions-recording-steps =
    Стоя прямо, покрутите головой по кругу.
    Наклоните спину вперед и присядьте на корточки. Сидя на корточках, посмотрите налево, затем направо.
    Поверните верхнюю часть туловища влево (против часовой стрелки), затем наклонитесь к земле.
    Поверните верхнюю часть туловища вправо (по часовой стрелке), затем наклонитесь к земле.
    Вращайте бедрами круговыми движениями, как будто вы используете хула-хуп.
    Если на запись осталось время, вы можете повторять эти действия до тех пор, пока она не будет завершена.
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
onboarding-automatic_proportions-error_modal-v2 =
    <b>Внимание:</b> Произошла ошибка при оценке пропорций!
    Скорее всего, это проблема с калибровкой крепления. Прежде чем повторить попытку, убедитесь, что отслеживание работает правильно.
    Пожалуйста, <docs>проверьте документацию</docs> или присоединитесь к нашему <discord>Discord</discord> для получения помощи ^_^
onboarding-automatic_proportions-error_modal-confirm = Принято!
onboarding-automatic_proportions-smol_warning =
    Указанная высота { $height } меньше минимально допустимой высоты { $minHeight }.
    <b>Пожалуйста, повторите измерения и убедитесь, что они верны.</b>
onboarding-automatic_proportions-smol_warning-cancel = Вернуться

## User height calibration


## Stay Aligned setup

onboarding-stay_aligned-title = Оставаться выровненным
onboarding-stay_aligned-description = Настройте функцию "Оставаться выровненным", чтобы ваши трекеры продолжали оставаться выровненными.
onboarding-stay_aligned-put_trackers_on-title = Наденьте ваши трекеры
onboarding-stay_aligned-put_trackers_on-description = Чтобы сохранить ваши расслабленные позы, будут использованы трекеры, которые вы только что назначили. Наденьте все трекеры, вы можете увидеть, какие из них какие, на рисунке справа.
onboarding-stay_aligned-put_trackers_on-trackers_warning = Вы подключили и назначили менее 5 трекеров! Это минимально требуемое количество трекеров для корректной работы функции "Оставаться выровненным".
onboarding-stay_aligned-put_trackers_on-next = У меня включены все трекеры
onboarding-stay_aligned-verify_mounting-title = Проверьте ваше крепление
onboarding-stay_aligned-verify_mounting-step-0 = Функция "Оставаться выровненным" требует хорошего крепления. В противном случае вы не получите положительного опыта в использовании данной функции.
onboarding-stay_aligned-verify_mounting-step-1 = 1. Передвигайтесь, находясь в положении стоя.
onboarding-stay_aligned-verify_mounting-step-2 = 2. Сядьте и подвигайте вашими ногами и ступнями.
onboarding-stay_aligned-verify_mounting-step-3 = 3. Если ваши трекеры расположены в некорректных местах, нажмите "Повторить калиборвку крепления"
onboarding-stay_aligned-verify_mounting-redo_mounting = Повторить калибровку крепления
onboarding-stay_aligned-preparation-title = Подготовка
onboarding-stay_aligned-preparation-tip = Убедитесь, что стоите прямо. Вам необходимо смотреть вперед, а ваши руки должны быть опущены по бокам.
onboarding-stay_aligned-relaxed_poses-standing-title = Расслабленная поза (стоя)
onboarding-stay_aligned-relaxed_poses-standing-step-0 = 1. Встаньте в удобное положение. Расслабьтесь!
onboarding-stay_aligned-relaxed_poses-sitting-title = Расслабленная поза (сидя на стуле)
onboarding-stay_aligned-relaxed_poses-sitting-step-0 = 1. Сядьте в удобное положение. Расслабьтесь!
onboarding-stay_aligned-relaxed_poses-flat-title = Расслабленная поза (сидя на полу)
onboarding-stay_aligned-relaxed_poses-flat-step-0 = 1. Сядьте на пол, расположив ноги перед собой. Расслабьтесь!
onboarding-stay_aligned-relaxed_poses-skip_step = Пропустить
onboarding-stay_aligned-done-title = Функция "Оставаться выровненным" включена!
onboarding-stay_aligned-done-description = Настройка функции "Оставаться выровненным" завершена!
onboarding-stay_aligned-done-description-2 = Настройка завершена! Вы можете провести настройку повторно, если хотите заново откалибровать позы.
onboarding-stay_aligned-previous_step = Предыдущий
onboarding-stay_aligned-next_step = Следующий
onboarding-stay_aligned-restart = Перезапустить
onboarding-stay_aligned-done = Выполнено

## Home

home-no_trackers = Трекеры не обнаружены и не привязаны

## Trackers Still On notification

trackers_still_on-modal-title = Трекеры все еще включены
trackers_still_on-modal-description =
    Один или несколько трекеров все еще включены.
    Вы точно хотите выйти из SlimeVR?
trackers_still_on-modal-confirm = Выйти из SlimeVR
trackers_still_on-modal-cancel = Погоди...

## Status system

status_system-StatusTrackerReset = Рекомендуется выполнить полный сброс, так как один или несколько трекеров не настроены.
status_system-StatusSteamVRDisconnected =
    { $type ->
        [steamvr_feeder] В настоящее время не подключен к приложению SlimeVR Feeder.
       *[other] В настоящее время не подключен к SteamVR через драйвер SlimeVR.
    }
status_system-StatusTrackerError = В трекере { $trackerName } обнаружена ошибка.
status_system-StatusUnassignedHMD = VR гарнитура должна быть назначена как трекер головы.
status_system-StatusPublicNetwork = В настоящее время ваш сетевой профиль отмечен как "Общедоступный". Для корректной работы SlimeVR  не рекомендуется использовать эту настройку. <PublicFixLink>Узнать, как это исправить, здесь.</PublicFixLink>

## Firmware tool globals

firmware_tool-next_step = Следующий Шаг
firmware_tool-previous_step = Предыдущий Шаг
firmware_tool-ok = Похоже на правду
firmware_tool-retry = Повторить
firmware_tool-loading = Загрузка...

## Firmware tool Steps

firmware_tool = Инструмент Прошивки DIY
firmware_tool-description = Позволяет вам настроить и прошить ваши DIY трекеры
firmware_tool-not_available = Упс! В данный момент инструмент прошивки недоступен. Возвращайтесь позже!
firmware_tool-not_compatible = Средство прошивки несовместимо с этой версией сервера. Пожалуйста, обновите свой сервер!
firmware_tool-flash_method_step = Способ прошивки
firmware_tool-flash_method_step-description = Пожалуйста, выберите способ прошивки, который вы хотите использовать
firmware_tool-flashbtn_step = Нажмите кнопку загрузки
firmware_tool-flashbtn_step-description = Прежде чем перейти к следующему шагу, вам нужно сделать ещё несколько действий
firmware_tool-flashbtn_step-board_SLIMEVR = Отключите трекер, извлеките его из корпуса (если он есть), подключите USB кабель к компьютеру, затем выполните одно из следующих действий в соответствии с ревизией вашей платы от SlimeVR:
firmware_tool-flashbtn_step-board_OTHER =
    Перед перепрошивкой вам, возможно, потребуется перевести трекер в режим загрузчика.
    В большинстве случаев для этого необходимо нажатие кнопки загрузки на плате перед началом процесса прошивки.
    Если в начале процесса прошивки истекло время ожидания, это, вероятно, означает, что трекер не находился в режиме загрузчика
    Пожалуйста, обратитесь к инструкции по прошивке для вашей платы, чтобы узнать, как перевести трекер в режим загрузчика
firmware_tool-flash_method_ota-devices = Обнаруженные устройства OTA:
firmware_tool-flash_method_ota-no_devices = Нет плат, которые можно обновить с помощью OTA, убедитесь, что вы выбрали верный тип платы
firmware_tool-flash_method_serial-wifi = Учетные данные Wi-Fi:
firmware_tool-flash_method_serial-devices-label = Обнаруженные последовательные устройства:
firmware_tool-flash_method_serial-devices-placeholder = Выберите последовательное устройство
firmware_tool-flash_method_serial-no_devices = Не обнаружено совместимых последовательных устройств, убедитесь, что трекер подключен
firmware_tool-build_step = Сборка
firmware_tool-build_step-description = Прошивка собирается, пожалуйста, подождите
firmware_tool-flashing_step = Прошивка
firmware_tool-flashing_step-description = Ваши трекеры прошиваются, пожалуйста, следуйте инструкциям на экране
firmware_tool-flashing_step-warning-v2 = Не отключайте и не выключайте трекер во время процесса загрузки, если этого не требует инструкция, иначе это может сделать вашу плату непригодной к использованию
firmware_tool-flashing_step-flash_more = Прошить больше трекеров
firmware_tool-flashing_step-exit = Выйти

## firmware tool build status

firmware_tool-build-CREATING_BUILD_FOLDER = Создание папки сборки
firmware_tool-build-BUILDING = Сборка прошивки
firmware_tool-build-SAVING = Сохранение сборки
firmware_tool-build-DONE = Сборка завершена
firmware_tool-build-ERROR = Не удается собрать прошивку

## Firmware update status

firmware_update-status-DOWNLOADING = Скачивание прошивки
firmware_update-status-NEED_MANUAL_REBOOT-v2 = Пожалуйста, выключите и снова включите ваш трекер
firmware_update-status-AUTHENTICATING = Аутентификация с микроконтроллером
firmware_update-status-UPLOADING = Выгрузка прошивки
firmware_update-status-SYNCING_WITH_MCU = Синхронизация с микроконтроллером
firmware_update-status-REBOOTING = Применение обновления
firmware_update-status-PROVISIONING = Настройка учетных данных Wi-Fi
firmware_update-status-DONE = Обновление завершено!
firmware_update-status-ERROR_DEVICE_NOT_FOUND = Не удалось найти устройство
firmware_update-status-ERROR_TIMEOUT = Время ожидания процесса обновления истекло
firmware_update-status-ERROR_DOWNLOAD_FAILED = Не удалось загрузить прошивку
firmware_update-status-ERROR_AUTHENTICATION_FAILED = Не удалось пройти аутентификацию с микроконтроллером
firmware_update-status-ERROR_UPLOAD_FAILED = Не удалось выгрузить прошивку
firmware_update-status-ERROR_PROVISIONING_FAILED = Не удалось установить учетные данные Wi-Fi
firmware_update-status-ERROR_UNSUPPORTED_METHOD = Метод обновления не поддерживается
firmware_update-status-ERROR_UNKNOWN = Неизвестная ошибка

## Dedicated Firmware Update Page

firmware_update-title = Обновление прошивки
firmware_update-devices = Доступные устройства
firmware_update-devices-description = Пожалуйста, выберите трекеры, которые вы хотите обновить до последней версии прошивки SlimeVR
firmware_update-no_devices = Пожалуйста, убедитесь, что трекеры, которые вы хотите обновить, ВКЛЮЧЕНЫ и подключены к Wi-Fi!
firmware_update-changelog-title = Обновить до { $version }
firmware_update-looking_for_devices = Поиск устройств для обновления...
firmware_update-retry = Повторить
firmware_update-update = Обновить выбранные трекеры
firmware_update-exit = Выйти

## Tray Menu

tray_menu-show = Показать
tray_menu-hide = Скрыть
tray_menu-quit = Выйти

## First exit modal

tray_or_exit_modal-title = Что должна делать кнопка закрытия?
# Multiline text
tray_or_exit_modal-description =
    Это позволяет вам выбрать, хотите ли вы выйти из сервера или свернуть его в трей при нажатии кнопки закрытия.
    
    Вы можете изменить это позже в настройках интерфейса!
tray_or_exit_modal-radio-exit = Выход при закрытии
tray_or_exit_modal-radio-tray = Свернуть в системный трей
tray_or_exit_modal-submit = Сохранить
tray_or_exit_modal-cancel = Отмена

## Unknown device modal

unknown_device-modal-title = Новый трекер был найден!
unknown_device-modal-description =
    Появился новый трекер с MAC-адресом <b>{ $deviceId }</b>.
    Хотите подключить его к SlimeVR?
unknown_device-modal-confirm = Конечно!
unknown_device-modal-forget = Игнорировать
# VRChat config warnings
vrc_config-page-title = Предупреждение о настройках VRChat
vrc_config-page-desc = На этой странице показаны выбранные настройки VRChat и указано, какие настройки несовместимы со SlimeVR. Настоятельно рекомендуется исправить все предупреждения, отображаемые здесь, для получения наилучшего опыта с использованием SlimeVR.
vrc_config-page-help = Не можете найти настройки?
vrc_config-page-help-desc = Ознакомьтесь с нашей <a>документацией по этой теме!</a>
vrc_config-page-big_menu = Отслеживание и IK (Развёрнутое меню)
vrc_config-page-big_menu-desc = Настройки, относящиеся к IK в развёрнутом меню настроек
vrc_config-page-wrist_menu = Отслеживание и IK (Меню на Запястье)
vrc_config-page-wrist_menu-desc = Настройки, относящиеся к IK в маленьком меню настроек (Меню на Запястье)
vrc_config-on = Включено
vrc_config-off = Отключено
vrc_config-invalid = У вас есть некорректные настройки VRChat!
vrc_config-show_more = Развернуть
vrc_config-setting_name = Название настройки VRChat
vrc_config-recommended_value = Рекомендуемое значение
vrc_config-current_value = Текущее значение
vrc_config-mute = Предупреждение об отключении звука
vrc_config-mute-btn = Отключить звук
vrc_config-unmute-btn = Подключить звук
vrc_config-legacy_mode = Использовать старую версию IK
vrc_config-disable_shoulder_tracking = Выключить отслеживание плеч
vrc_config-shoulder_width_compensation = Компенсация ширины плеч
vrc_config-spine_mode = Фиксация позвоночника
vrc_config-tracker_model = Модель трекеров FBT
vrc_config-avatar_measurement_type = Измерение аватара
vrc_config-calibration_range = Диапазон калибровки
vrc_config-calibration_visuals = Подробные визуальные подсказки при калибровке
vrc_config-user_height = Реальный рост пользователя
vrc_config-spine_mode-UNKNOWN = Неизвестно
vrc_config-spine_mode-LOCK_BOTH = Фиксировать оба
vrc_config-spine_mode-LOCK_HEAD = Фиксировать голову
vrc_config-spine_mode-LOCK_HIP = Фиксировать бедра
vrc_config-tracker_model-UNKNOWN = Неизвестно
vrc_config-tracker_model-AXIS = Оси
vrc_config-tracker_model-BOX = Кубики
vrc_config-tracker_model-SPHERE = Сферы
vrc_config-tracker_model-SYSTEM = Системные
vrc_config-avatar_measurement_type-UNKNOWN = Неизвестно
vrc_config-avatar_measurement_type-HEIGHT = Высота
vrc_config-avatar_measurement_type-ARM_SPAN = Размах Рук

## Error collection consent modal

error_collection_modal-title = Можем ли мы собирать данные об ошибках?
error_collection_modal-description_v2 =
    { settings-interface-behavior-error_tracking-description_v2 }
    
    Вы можете изменить эту настройку позже на странице настроек в разделе Поведение.
error_collection_modal-confirm = Я согласен
error_collection_modal-cancel = Я не согласен

## Tracking checklist section

