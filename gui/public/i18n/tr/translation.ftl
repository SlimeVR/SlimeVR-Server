# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Sunucuya bağlanılıyor...
websocket-connection_lost = Sunucuyla bağlantı kesildi. Tekrar bağlanılmaya çalışılıyor...
websocket-connection_lost-desc = SlimeVR sunucusu çöktü. Kayıtları kontrol edip programı yeniden başlat.
websocket-timedout = Sunucuya bağlanılamadı
websocket-timedout-desc = SlimeVR sunucusu çöktü veya zaman aşımına uğradı. Kayıtları kontrol et ve programı yeniden başlat.
websocket-error-close = SlimeVR'ı kapat
websocket-error-logs = Kayıtlar klasörünü aç

## Update notification

version_update-title = Yeni sürüm mevcut: { $version }
version_update-description = "{ version_update-update }" seçeneğine tıklamak SlimeVR kurulum uygulamasını indirir.
version_update-update = Güncelle
version_update-close = Kapat

## Tips

tips-find_tracker = Hangi takipçi hangisi emin değil misin? Takipçilerden birini hareket ettirerek belirleyebilirsin.
tips-do_not_move_heels = Kayıt sırasında ayaklarının hareket etmediğinden emin ol!
tips-file_select = Dosyaları sürükleyip bırak veya dosyalarından <u>seç</u>.
tips-tap_setup = Menüden seçmek yerine takipçine 2 kez yavaşça dokunarak da seçebilirsin.
tips-turn_on_tracker = Resmi SlimeVR takipçisi mi kullanıyorsun? Takipçilerini bilgisayara bağladıktan sonra onları <b><em>açmayı</em></b> unutma!
tips-failed_webgl = WebGL başlatılamadı.

## Units

unit-meter = Metre
unit-foot = Ayak
unit-inch = Inç
unit-cm = cm

## Body parts

body_part-NONE = Atanmamış
body_part-HEAD = Kafa
body_part-NECK = Boyun
body_part-RIGHT_SHOULDER = Sağ Omuz
body_part-RIGHT_UPPER_ARM = Sağ Üst Kol
body_part-RIGHT_LOWER_ARM = Sağ Alt Kol
body_part-RIGHT_HAND = Sağ El
body_part-RIGHT_UPPER_LEG = Sağ Uyluk
body_part-RIGHT_LOWER_LEG = Sağ Ayak Bileği
body_part-RIGHT_FOOT = Sağ Ayak
body_part-UPPER_CHEST = Üst Göğüs
body_part-CHEST = Göğüs
body_part-WAIST = Bel
body_part-HIP = Kalça
body_part-LEFT_SHOULDER = Sol Omuz
body_part-LEFT_UPPER_ARM = Sol Üst Kol
body_part-LEFT_LOWER_ARM = Sol Alt Kol
body_part-LEFT_HAND = Sol El
body_part-LEFT_UPPER_LEG = Sol Uyluk
body_part-LEFT_LOWER_LEG = Sol Ayak Bileği
body_part-LEFT_FOOT = Sol Ayak
body_part-LEFT_THUMB_METACARPAL = Sol başparmak metakarpal kemiği
body_part-LEFT_THUMB_PROXIMAL = Sol başparmak proksimal kemiği
body_part-LEFT_THUMB_DISTAL = Sol başparmak distal falanks kemiği
body_part-LEFT_INDEX_PROXIMAL = Sol işaret parmağının proksimal falanks kemiği
body_part-LEFT_INDEX_INTERMEDIATE = Sol işaret parmağının orta falanks kemiği
body_part-LEFT_INDEX_DISTAL = Sol işaret parmağının distal falanks kemiği
body_part-LEFT_MIDDLE_PROXIMAL = Sol orta parmağın proksimal falanks kemiği
body_part-LEFT_MIDDLE_INTERMEDIATE = Sol orta parmağın orta falanks kemiği
body_part-LEFT_MIDDLE_DISTAL = Sol orta parmağın distal falanks kemiği
body_part-LEFT_RING_PROXIMAL = Sol yüzük parmağının proksimal falanks kemiği
body_part-LEFT_RING_INTERMEDIATE = Sol yüzük parmağının orta falanks kemiği
body_part-LEFT_RING_DISTAL = Sol yüzük parmağının distal falanks kemiği
body_part-LEFT_LITTLE_PROXIMAL = Sol küçük parmağın proksimal falanks kemiği
body_part-LEFT_LITTLE_INTERMEDIATE = Sol küçük parmağın orta falanks kemiği
body_part-LEFT_LITTLE_DISTAL = Sol küçük parmağın distal falanks kemiği
body_part-RIGHT_THUMB_METACARPAL = Sağ başparmak metakarpal kemiği
body_part-RIGHT_THUMB_PROXIMAL = Sağ başparmağın proksimal falanks kemiği
body_part-RIGHT_THUMB_DISTAL = Sağ başparmağın distal falanks kemiği
body_part-RIGHT_INDEX_PROXIMAL = Sağ işaret parmağının proksimal falanks kemiği
body_part-RIGHT_INDEX_INTERMEDIATE = Sağ işaret parmağının orta falanks kemiği
body_part-RIGHT_INDEX_DISTAL = Sağ işaret parmağının distal falanks kemiği
body_part-RIGHT_MIDDLE_PROXIMAL = Sağ orta parmağın proksimal falanks kemiği
body_part-RIGHT_MIDDLE_INTERMEDIATE = Sağ orta parmağın orta falanks kemiği
body_part-RIGHT_MIDDLE_DISTAL = Sağ orta parmağın distal falanks kemiği
body_part-RIGHT_RING_PROXIMAL = Sağ yüzük parmağının proksimal falanks kemiği
body_part-RIGHT_RING_INTERMEDIATE = Sağ yüzük parmağının orta falanks kemiği
body_part-RIGHT_RING_DISTAL = Sağ yüzük parmağının distal falanks kemiği
body_part-RIGHT_LITTLE_PROXIMAL = Sağ küçük parmağın proksimal falanks kemiği
body_part-RIGHT_LITTLE_INTERMEDIATE = Sağ küçük parmağın orta falanks kemiği
body_part-RIGHT_LITTLE_DISTAL = Sağ küçük parmağın distal falanks kemiği

## BoardType

board_type-UNKNOWN = Bilinmeyen
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
board_type-GLOVE_IMU_SLIMEVR_DEV = SlimeVR Dev IMU Eldiveni
board_type-GESTURES = Hareketler
board_type-ESP32S3_SUPERMINI = ESP32-S3 Supermini
board_type-GENERIC_NRF = Jenerik nRF
board_type-SLIMEVR_BUTTERFLY_DEV = SlimeVR Dev Butterfly
board_type-SLIMEVR_BUTTERFLY = SlimeVR Butterfly

## Proportions

skeleton_bone-NONE = Yok
skeleton_bone-HEAD = Kafa Hizası
skeleton_bone-HEAD-desc =
    Bu, kulaklığın ile başının ortası arasındaki mesafedir.
    Ayarlamak için, 'hayır' der gibi başını soldan sağa ve sağdan sola salla,
    diğer takipçiler yerinden oynamayana kadar bunu tekrarlayıp ayarla.
skeleton_bone-NECK = Boyun Uzunluğu
skeleton_bone-NECK-desc =
    Bu, başının ortasından boynunun başlangıcına kadar olan mesafedir.
    Ayarlamak için başını 'evet' der gibi yukarı-aşağı hareket ettir veya başını hafifçe öne eğip, başını sola ve sağa kaydır,
    diğer takipçiler yerinden oynamayana kadar bunu tekrarlayıp ayarla.
skeleton_bone-torso_group = Gövde Uzunluğu
skeleton_bone-torso_group-desc =
    Bu, boynunun başlangıcından kalçalarına kadar olan mesafedir.
    Ayarlamak için dik dur ve sanal kalçaların, gerçek kalçalarınla hizalanana kadar değeri değiştir.
skeleton_bone-UPPER_CHEST = Üst Göğüs Uzunluğu
skeleton_bone-UPPER_CHEST-desc =
    Bu, boynunun başlangıcından göğsünün ortasına kadar olan mesafedir.
    Gövde uzunluğunu doğru şekilde ayarlayıp (otururken, eğilirken, uzanırken vb. duruşlarda) sanal omurgan,
    gerçek omurganla eşleşene kadar bu değeri değiştir.
skeleton_bone-CHEST_OFFSET = Göğüs Hizası
skeleton_bone-CHEST_OFFSET-desc =
    Bu, bazı oyun veya uygulamalarda kalibrasyona yardımcı olmak için,
    sanal göğüs takipçisini yukarı ya da aşağı hareket ettirmene izin verir.
skeleton_bone-CHEST = Göğüs Uzunluğu
skeleton_bone-CHEST-desc =
    Bu, göğsünün ortasından omurganın ortasına kadar olan mesafedir.
    Gövde uzunluğunu doğru şekilde ayarlayıp (otururken, eğilirken, uzanırken vb. duruşlarda) sanal omurgan,
    gerçek omurganla eşleşene kadar bu değeri değiştir.
skeleton_bone-WAIST = Bel Uzunluğu
skeleton_bone-WAIST-desc =
    Bu, omurganın ortasından göbek deliğine kadar olan mesafedir.
    Gövde uzunluğunu doğru şekilde ayarlayıp (otururken, eğilirken, uzanırken vb. duruşlarda) sanal omurgan,
    gerçek omurganla eşleşene kadar bu değeri değiştir.
skeleton_bone-HIP = Kalça Uzunluğu
skeleton_bone-HIP-desc =
    Bu, göbek deliğinden kalçalarına kadar olan mesafedir.
    Gövde uzunluğunu doğru şekilde ayarlayıp (otururken, eğilirken, uzanırken vb. duruşlarda) sanal omurgan,
    gerçek omurganla eşleşene kadar bu değeri değiştir.
skeleton_bone-HIP_OFFSET = Kalça Hizası
skeleton_bone-HIP_OFFSET-desc =
    Bu, bazı oyun veya uygulamalarda kalibrasyona yardımcı olmak için,
    sanal kalça takipçisini yukarı ya da aşağı hareket ettirmene izin verir.
skeleton_bone-HIPS_WIDTH = Kalça Genişliği
skeleton_bone-HIPS_WIDTH-desc =
    Bu, bacaklarının başladığı noktalar arasındaki mesafedir.
    Ayarlamak için bacaklarını düz tutarak tam bir sıfırlama yap ve,
    sanal bacaklarının aralığı, gerçek bacaklarının aralığıyla hizalanana kadar bu değeri değiştir.
skeleton_bone-leg_group = Bacak uzunluğu
skeleton_bone-leg_group-desc =
    Bu, kalçandan ayaklarına olan mesafedir.
    Ayarlamak için Gövde uzunluğunu doğru şekilde ayarla ve,
    sanal ayakların gerçek ayaklarınla aynı seviyeye gelene kadar bu değeri değiştir.
skeleton_bone-UPPER_LEG = Üst Bacak Uzunluğu
skeleton_bone-UPPER_LEG-desc =
    Bu, kalçandan dizlerine olan mesafedir.
    Ayarlamak için Bacak uzunluğunu doğru şekilde ayarla ve,
    sanal dizlerin gerçek dizlerinle aynı seviyeye gelene kadar bu değeri değiştir.
skeleton_bone-LOWER_LEG = Alt Bacak Uzunluğu
skeleton_bone-LOWER_LEG-desc =
    Bu, dizlerinden ayak bileklerine olan mesafedir.
    Ayarlamak için Bacak uzunluğunu doğru şekilde ayarla ve,
    sanal dizlerin gerçek dizlerinle aynı seviyeye gelene kadar bu değeri değiştir.
skeleton_bone-FOOT_LENGTH = Ayak Uzunluğu
skeleton_bone-FOOT_LENGTH-desc =
    Bu, ayak bileklerinden ayak parmaklarına olan mesafedir.
    Ayarlamak için parmak uçlarına çık ve sanal ayakların sabit kalana kadar bu değeri değiştir.
skeleton_bone-FOOT_SHIFT = Ayak hizası
skeleton_bone-FOOT_SHIFT-desc =
    Bu değer, dizinden ayak bileğine olan yatay mesafedir.
    Bu, dik dururken alt bacaklarının geriye doğru konumlanmasını telafi eder.
    Ayarlamak için Ayak uzunluğunu 0 olarak ayarla, tam sıfırlama yap ve,
    sanal ayakların ayak bileklerinin ortasıyla hizalanana kadar bu değeri değiştir.
skeleton_bone-SKELETON_OFFSET = İskelet hizası
skeleton_bone-SKELETON_OFFSET-desc =
    This can be adjusted to offset all your trackers forward or backward.
    It can be used to help with calibration in certain games or applications
    that may expect your trackers to be more forward.
skeleton_bone-SHOULDERS_DISTANCE = Omuz Mesafesi
skeleton_bone-SHOULDERS_DISTANCE-desc =
    This is the vertical distance from the base of your neck to your shoulders.
    To adjust it, set Upper Arm Length to 0 and modify it until your virtual elbow trackers
    line up vertically with your real shoulders.
skeleton_bone-SHOULDERS_WIDTH = Omuz Genişliği
skeleton_bone-SHOULDERS_WIDTH-desc =
    This is the horizontal distance from the base of your neck to your shoulders.
    To adjust it, set Upper Arm Length to 0 and modify it until your virtual elbow trackers
    line up horizontally with your real shoulders.
skeleton_bone-arm_group = Kol uzunluğu
skeleton_bone-arm_group-desc =
    This is the distance from your shoulders to your wrists.
    To adjust it, adjust Shoulders Distance properly, set Hand Distance Y
    to 0 and modify it until your hand trackers line up with your wrists.
skeleton_bone-UPPER_ARM = Üst Kol Uzunluğu
skeleton_bone-UPPER_ARM-desc =
    This is the distance from your shoulders to your elbows.
    To adjust it, adjust Arm Length properly and modify it until
    your elbow trackers line up with your real elbows.
skeleton_bone-LOWER_ARM = Alt Kol Uzunluğu
skeleton_bone-LOWER_ARM-desc =
    This is the distance from your elbows to your wrists.
    To adjust it, adjust Arm Length properly and modify it until
    your elbow trackers line up with your real elbows.
skeleton_bone-HAND_Y = Hand Distance Y
skeleton_bone-HAND_Y-desc =
    This is the vertical distance from your wrists to the middle of your hand.
    To adjust it for motion capture, adjust Arm Length properly and modify it until your
    hand trackers line up vertically with the middle of your hands.
    To adjust it for elbow tracking from your controllers, set Arm Length to 0 and
    modify it until your elbow trackers line up vertically with your wrists.
skeleton_bone-HAND_Z = Hand Distance Z
skeleton_bone-HAND_Z-desc =
    This is the horizontal distance from your wrists to the middle of your hand.
    To adjust it for motion capture, set it to 0.
    To adjust it for elbow tracking from your controllers, set Arm Length to 0 and
    modify it until your elbow trackers line up horizontally with your wrists.
skeleton_bone-ELBOW_OFFSET = Dirsek hizası
skeleton_bone-ELBOW_OFFSET-desc =
    This can be adjusted to move your virtual elbow trackers up or down in order to aid
    with VRChat accidentally binding an elbow tracker to the chest.

## Tracker reset buttons

reset-reset_all = Tüm oranları sıfırla
reset-reset_all_warning-v2 =
    <b>Warning:</b> Your proportions will be reset to defaults scaled to your configured height.
    Are you sure you want to do this?
reset-reset_all_warning-reset = Reset proportions
reset-reset_all_warning-cancel = İptal et
reset-reset_all_warning_default-v2 =
    <b>Warning:</b> Your height has not been configured, your proportions will be reset to defaults with the default height.
    Are you sure you want to do this?
reset-full = Sıfırlama
reset-mounting = Mounting Calibration
reset-mounting-feet = Ayak Kalibrasyonu
reset-mounting-fingers = Parmak Kalibrasyonu
reset-yaw = Yaw Reset
reset-error-no_feet_tracker = No feet tracker assigned
reset-error-no_fingers_tracker = No finger tracker assigned
reset-error-mounting-need_full_reset = Need a full reset before mounting
reset-error-yaw-need_full_reset = Need a full reset before yaw reset

## Serial detection stuff

serial_detection-new_device-p0 = Yeni seri cihaz algılandı!
serial_detection-new_device-p1 = Wi-Fi bilgilerinizi girin!
serial_detection-new_device-p2 = Lütfen onunla ne yapmak istediğinizi seçin
serial_detection-open_wifi = Wi-Fi'ye bağlan
serial_detection-open_serial = Seri Konsolu Aç
serial_detection-submit = Gönder!
serial_detection-close = Kapat

## Navigation bar

navbar-home = Ana Menü
navbar-body_proportions = Vücut Oranları
navbar-trackers_assign = Tracker Assignment
navbar-mounting = Mounting Calibration
navbar-onboarding = Kurulum Sihirbazı
navbar-settings = Ayarlar
navbar-connect_trackers = Connect Trackers

## Biovision hierarchy recording

bvh-start_recording = BVH Kaydet
bvh-stop_recording = Save BVH recording
bvh-recording = Kaydediliyor
bvh-save_title = Save BVH recording

## Tracking pause

tracking-unpaused = Takibi duraklat
tracking-paused = Unpause tracking

## Widget: Overlay settings

widget-overlay = Overlay
widget-overlay-is_visible_label = Show Overlay in SteamVR
widget-overlay-is_mirrored_label = Display Overlay as Mirror

## Widget: Drift compensation

widget-drift_compensation-clear = Clear drift compensation

## Widget: Clear Mounting calibration

widget-clear_mounting = Clear mounting calibration

## Widget: Developer settings

widget-developer_mode = Geliştirici Modu
widget-developer_mode-high_contrast = Yüksek kontrast
widget-developer_mode-precise_rotation = Hassas dönüş
widget-developer_mode-fast_data_feed = Fast data feed
widget-developer_mode-filter_slimes_and_hmd = Filter Slimes and HMD
widget-developer_mode-sort_by_name = Ada göre sırala
widget-developer_mode-raw_slime_rotation = Raw rotation
widget-developer_mode-more_info = Daha fazla bilgi

## Widget: IMU Visualizer

widget-imu_visualizer = Rotasyon
widget-imu_visualizer-preview = Önizle
widget-imu_visualizer-hide = Gizle
widget-imu_visualizer-rotation_raw = Raw rotation
widget-imu_visualizer-rotation_preview = Önizle
widget-imu_visualizer-acceleration = Acceleration
widget-imu_visualizer-position = Position
widget-imu_visualizer-stay_aligned = Stay Aligned

## Widget: Skeleton Visualizer

widget-skeleton_visualizer-preview = Skeleton preview
widget-skeleton_visualizer-hide = Gizle

## Tracker status

tracker-status-none = Durum Yok
tracker-status-busy = Meşgul
tracker-status-error = Hata
tracker-status-disconnected = Bağlantı kesildi
tracker-status-occluded = Occluded
tracker-status-ok = İYİ
tracker-status-timed_out = Zaman aşımı

## Tracker status columns

tracker-table-column-name = İsim
tracker-table-column-type = Tür
tracker-table-column-battery = Pil
tracker-table-column-ping = Ping
tracker-table-column-packet_loss = Packet Loss
tracker-table-column-tps = TPS
tracker-table-column-temperature = Sıcaklık °C
tracker-table-column-linear-acceleration = Accel. X/Y/Z
tracker-table-column-rotation = Rotasyon X/Y/Z
tracker-table-column-position = Pozisyon X/Y/Z
tracker-table-column-stay_aligned = Stay Aligned
tracker-table-column-url = URL

## Tracker rotation

tracker-rotation-front = Ön
tracker-rotation-front_left = Front-Left
tracker-rotation-front_right = Front-Right
tracker-rotation-left = Sol
tracker-rotation-right = Sağ
tracker-rotation-back = Arka
tracker-rotation-back_left = Back-Left
tracker-rotation-back_right = Back-Right
tracker-rotation-custom = Custom
tracker-rotation-overriden = (overridden by mounting calibration)

## Tracker information

tracker-infos-manufacturer = Üretici
tracker-infos-display_name = Görünen Ad
tracker-infos-custom_name = Özel Ad
tracker-infos-url = Takipçi URL'si
tracker-infos-version = Yazılım Sürümü
tracker-infos-hardware_rev = Donanım Revizyonu
tracker-infos-hardware_identifier = Donanım Kimliği
tracker-infos-data_support = Data support
tracker-infos-imu = IMU Sensör
tracker-infos-board_type = Ana kart
tracker-infos-network_version = Protocol Version
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

tracker-settings-back = Takipçi listesine geri dön
tracker-settings-title = Takipçi ayarları
tracker-settings-assignment_section = Assignment
tracker-settings-assignment_section-description = Tracker'in vücudun hangi kısmına atandığı.
tracker-settings-assignment_section-edit = Edit assignment
tracker-settings-mounting_section = Mounting orientation
tracker-settings-mounting_section-description = Where is the tracker mounted?
tracker-settings-mounting_section-edit = Edit mounting
tracker-settings-drift_compensation_section = Allow drift compensation
tracker-settings-drift_compensation_section-description = Should this tracker compensate for its drift when drift compensation is enabled?
tracker-settings-drift_compensation_section-edit = Allow drift compensation
tracker-settings-use_mag = Allow magnetometer on this tracker
# Multiline!
tracker-settings-use_mag-description =
    Should this tracker use magnetometer to reduce drift when magnetometer usage is allowed? <b>Please don't shutdown your tracker while toggling this!</b>
    
    You need to allow magnetometer usage first, <magSetting>click here to go to the setting</magSetting>.
tracker-settings-use_mag-label = Allow magnetometer
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Takipçi adı
tracker-settings-name_section-description = Give it a cute nickname :)
tracker-settings-name_section-placeholder = NightyBeast'in sol bacağı
tracker-settings-name_section-label = Tracker name
tracker-settings-forget = Forget tracker
tracker-settings-forget-description = Removes the tracker from the SlimeVR Server and prevents it from connecting until the server is restarted. The configuration of the tracker won't be lost.
tracker-settings-forget-label = Forget tracker
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

tracker-part_card-no_name = İsimsiz
tracker-part_card-unassigned = Atanmamış

## Body assignment menu

body_assignment_menu = Bu takipçinin nerede olmasını istiyorsunuz?
body_assignment_menu-description = Choose a location where you want this tracker to be assigned. Alternatively, you can choose to manage all trackers at once instead of one by one.
body_assignment_menu-show_advanced_locations = Show advanced assign locations
body_assignment_menu-manage_trackers = Tüm takipçileri yönet
body_assignment_menu-unassign_tracker = Unassign tracker

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = hangi takipçiyi atayacaksınız?
tracker_selection_menu-NONE = Which tracker do you want to be unassigned?
tracker_selection_menu-HEAD = Başınıza { -tracker_selection-part }
tracker_selection_menu-NECK = { -tracker_selection-part } neck?
tracker_selection_menu-RIGHT_SHOULDER = Sağ omuzunuza { -tracker_selection-part }
tracker_selection_menu-RIGHT_UPPER_ARM = Sağ üst kolunuza { -tracker_selection-part }
tracker_selection_menu-RIGHT_LOWER_ARM = Sağ alt kolunuza { -tracker_selection-part }
tracker_selection_menu-RIGHT_HAND = Sağ elinize { -tracker_selection-part }
tracker_selection_menu-RIGHT_UPPER_LEG = Say kalçanıza { -tracker_selection-part }
tracker_selection_menu-RIGHT_LOWER_LEG = Sağ ayak bileğinize { -tracker_selection-part }
tracker_selection_menu-RIGHT_FOOT = Sağ ayağınıza { -tracker_selection-part }
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part } right controller?
tracker_selection_menu-UPPER_CHEST = Üst göğüsünüze { -tracker_selection-part }
tracker_selection_menu-CHEST = { -tracker_selection-part } chest?
tracker_selection_menu-WAIST = { -tracker_selection-part } waist?
tracker_selection_menu-HIP = { -tracker_selection-part } hip?
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part } left shoulder?
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part } left upper arm?
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part } left lower arm?
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part } left hand?
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part } left thigh?
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part } left ankle?
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part } left foot?
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part } left controller?
tracker_selection_menu-unassigned = Atanmamış takipçiler
tracker_selection_menu-assigned = Atanan takipçiler
tracker_selection_menu-dont_assign = Unassign
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Warning:</b> A neck tracker can be deadly if adjusted too tightly;
    the strap could cut off circulation to your head!
tracker_selection_menu-neck_warning-done = Riskleri anlıyorum
tracker_selection_menu-neck_warning-cancel = İptal

## Mounting menu

mounting_selection_menu = Bu takipçinin nerede olmasını istiyorsunuz?
mounting_selection_menu-close = Kapat

## Sidebar settings

settings-sidebar-title = Ayarlar
settings-sidebar-general = Genel
settings-sidebar-steamvr = SteamVR
settings-sidebar-tracker_mechanics = Tracker mechanics
settings-sidebar-stay_aligned = Stay Aligned
settings-sidebar-fk_settings = Tracking settings
settings-sidebar-gesture_control = Gesture control
settings-sidebar-interface = Arayüz
settings-sidebar-osc_router = OSC yönlendirici
settings-sidebar-osc_trackers = VRChat OSC Takipçileri
settings-sidebar-osc_vmc = VMC
settings-sidebar-utils = Utilities
settings-sidebar-serial = Seri konsol
settings-sidebar-appearance = Appearance
settings-sidebar-home = Home Screen
settings-sidebar-checklist = Tracking checklist
settings-sidebar-notifications = Notifications
settings-sidebar-behavior = Behavior
settings-sidebar-firmware-tool = DIY Firmware Tool
settings-sidebar-vrc_warnings = VRChat Config Warnings
settings-sidebar-advanced = Advanced

## SteamVR settings

settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = SteamVR takipçileri
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    Enable or disable specific SteamVR trackers.
    Useful for games or apps that only support certain trackers.
settings-general-steamvr-trackers-waist = Bel
settings-general-steamvr-trackers-chest = Göğüs
settings-general-steamvr-trackers-left_foot = Left foot
settings-general-steamvr-trackers-right_foot = Right foot
settings-general-steamvr-trackers-left_knee = Left knee
settings-general-steamvr-trackers-right_knee = Right knee
settings-general-steamvr-trackers-left_elbow = Left elbow
settings-general-steamvr-trackers-right_elbow = Right elbow
settings-general-steamvr-trackers-left_hand = Left hand
settings-general-steamvr-trackers-right_hand = Right hand
settings-general-steamvr-trackers-tracker_toggling = Automatic tracker assignment
settings-general-steamvr-trackers-tracker_toggling-description = Automatically handles toggling SteamVR trackers on or off depending on your current tracker assignments.
settings-general-steamvr-trackers-tracker_toggling-label = Automatic tracker assignment
settings-general-steamvr-trackers-hands-warning =
    <b>Warning:</b> Enabling the SteamVR hand trackers will disable inputs from real controllers.
    This should only be enabled if you are using SlimeVR for hand tracking.
    
    Are you sure you want to do this?
settings-general-steamvr-trackers-hands-warning-cancel = Cancel
settings-general-steamvr-trackers-hands-warning-done = Yes

## Tracker mechanics

settings-general-tracker_mechanics = Tracker mechanics
settings-general-tracker_mechanics-filtering = Filtering
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Choose the filtering type for your trackers.
    Prediction predicts movement while smoothing smooths movement.
settings-general-tracker_mechanics-filtering-type = Filtering type
settings-general-tracker_mechanics-filtering-type-none = No filtering
settings-general-tracker_mechanics-filtering-type-none-description = Use rotations as is. Will not do any filtering.
settings-general-tracker_mechanics-filtering-type-smoothing = Smoothing
settings-general-tracker_mechanics-filtering-type-smoothing-description = Smooths movements but adds some latency.
settings-general-tracker_mechanics-filtering-type-prediction = Prediction
settings-general-tracker_mechanics-filtering-type-prediction-description = Reduces latency and makes movements more snappy, but may increase jitter.
settings-general-tracker_mechanics-filtering-amount = Miktar
settings-general-tracker_mechanics-yaw-reset-smooth-time = Yaw reset smooth time (0s disables smoothing)
settings-general-tracker_mechanics-drift_compensation = Drift compensation
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    Compensates for IMU yaw drift by applying an inverse rotation.
    Change the amount of compensation and the number of resets taken into account.
    This should only be used if you need to reset very often!
settings-general-tracker_mechanics-drift_compensation-enabled-label = Drift compensation
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
settings-general-tracker_mechanics-drift_compensation-amount-label = Compensation amount
settings-general-tracker_mechanics-drift_compensation-max_resets-label = Use up to x last resets
settings-general-tracker_mechanics-save_mounting_reset = Save automatic mounting calibration
settings-general-tracker_mechanics-save_mounting_reset-description =
    Saves the automatic mounting calibration for the trackers between restarts. Useful
    when wearing a suit where trackers don't move between sessions. <b>Not recommended for normal users!</b>
settings-general-tracker_mechanics-save_mounting_reset-enabled-label = Save mounting calibration
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

settings-general-fk_settings = Tracking settings
# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Floor clip
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Skating correction
settings-general-fk_settings-leg_tweak-toe_snap = Toe snap
settings-general-fk_settings-leg_tweak-foot_plant = Foot plant
settings-general-fk_settings-leg_tweak-skating_correction-amount = Skating correction strength
settings-general-fk_settings-leg_tweak-skating_correction-description = Skating-correction corrects for ice skating, but can decrease accuracy in certain movement patterns. When enabling this, make sure to perform a full reset and recalibrate in-game.
settings-general-fk_settings-leg_tweak-floor_clip-description = Floor-clip can reduce or eliminate clipping through the floor. When enabling this, make sure to perform a full reset and recalibrate in-game.
settings-general-fk_settings-leg_tweak-toe_snap-description = Toe-snap attempts to guess the rotation of your feet if foot trackers are not in use.
settings-general-fk_settings-leg_tweak-foot_plant-description = Foot-plant rotates your feet to be parallel to the ground when in contact.
settings-general-fk_settings-leg_fk = Bacak takibi
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
settings-general-fk_settings-arm_fk = Kol takibi
settings-general-fk_settings-arm_fk-description = Force arms to be tracked from the headset (HMD) even if positional hand data is available.
settings-general-fk_settings-arm_fk-force_arms = Force arms from HMD
settings-general-fk_settings-reset_settings = Reset settings
settings-general-fk_settings-reset_settings-reset_hmd_pitch-description = Reset the HMD's pitch (vertical rotation) upon doing a full reset. Useful if wearing an HMD on the forehead for VTubing or mocap. Do not enable for VR.
settings-general-fk_settings-reset_settings-reset_hmd_pitch = Reset HMD pitch
settings-general-fk_settings-arm_fk-reset_mode-description = Change which arm pose is expected for mounting calibration.
settings-general-fk_settings-arm_fk-back = Back
settings-general-fk_settings-arm_fk-back-description = The default mode, with the upper arms going back and lower arms going forward.
settings-general-fk_settings-arm_fk-tpose_up = T-pose (up)
settings-general-fk_settings-arm_fk-tpose_up-description = Expects your arms to be down at your sides during Full Reset, and 90 degrees up to the sides during Mounting Calibration.
settings-general-fk_settings-arm_fk-tpose_down = T-pose (down)
settings-general-fk_settings-arm_fk-tpose_down-description = Expects your arms to be 90 degrees up to the sides during Full Reset, and down at your sides during Mounting Calibration.
settings-general-fk_settings-arm_fk-forward = Forward
settings-general-fk_settings-arm_fk-forward-description = Expects your arms to be raised forward at 90 degrees. Useful for VTubing.
settings-general-fk_settings-skeleton_settings-toggles = Skeleton toggles
settings-general-fk_settings-skeleton_settings-description = İskelet ayarlarını açın veya kapatın. Bunları açık bırakmanız önerilir.
settings-general-fk_settings-skeleton_settings-extended_spine_model = Extended spine model
settings-general-fk_settings-skeleton_settings-extended_pelvis_model = Extended pelvis model
settings-general-fk_settings-skeleton_settings-extended_knees_model = Extended knee model
settings-general-fk_settings-skeleton_settings-ratios = Skeleton ratios
settings-general-fk_settings-skeleton_settings-ratios-description = Change the values of skeleton settings. You may need to adjust your proportions after changing these.
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_hip = Impute waist from chest to hip
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_legs = Impute waist from chest to legs
settings-general-fk_settings-skeleton_settings-impute_hip_from_chest_legs = Impute hip from chest to legs
settings-general-fk_settings-skeleton_settings-impute_hip_from_waist_legs = Impute hip from waist to legs
settings-general-fk_settings-skeleton_settings-interp_hip_legs = Average the hip's yaw and roll with the legs'
settings-general-fk_settings-skeleton_settings-interp_knee_tracker_ankle = Average the knee trackers' yaw and roll with the ankles'
settings-general-fk_settings-skeleton_settings-interp_knee_ankle = Average the knees' yaw and roll with the ankles'
settings-general-fk_settings-self_localization-title = Mocap mode
settings-general-fk_settings-self_localization-description = Mocap Mode allows the skeleton to roughly track its own position without a headset or other trackers. Note that this requires feet and head trackers to work and is still experimental.

## Gesture control settings (tracker tapping)

settings-general-gesture_control = Gesture control
settings-general-gesture_control-subtitle = Tap based resets
settings-general-gesture_control-description = Allows for resets to be triggered by tapping a tracker. Taps must occur within the time limit of 0.3 seconds times the number of taps to be recognized.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
        [one] 1 tap
       *[other] { $amount } taps
    }
# This is a unit: 3 trackers, 2 trackers, 1 tracker
# $amount (Number) - Amount of trackers
settings-general-gesture_control-trackers =
    { $amount ->
        [one] 1 tracker
       *[other] { $amount } trackers
    }
settings-general-gesture_control-yawResetEnabled = Enable tap to yaw reset
settings-general-gesture_control-yawResetDelay = Yaw reset delay
settings-general-gesture_control-yawResetTaps = Taps for yaw reset
settings-general-gesture_control-yawResetTracker = Yaw reset tracker
settings-general-gesture_control-fullResetEnabled = Enable tap to full reset
settings-general-gesture_control-fullResetDelay = Full reset delay
settings-general-gesture_control-fullResetTaps = Taps for full reset
settings-general-gesture_control-fullResetTracker = Full reset tracker
settings-general-gesture_control-mountingResetEnabled = Enable tap to perform mounting calibration
settings-general-gesture_control-mountingResetDelay = Mounting calibration delay
settings-general-gesture_control-mountingResetTaps = Taps for mounting calibration
settings-general-gesture_control-mountingResetTracker = Mounting reset tracker
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = Trackers over threshold
settings-general-gesture_control-numberTrackersOverThreshold-description = Increase this value if tap detection is not working. Do not increase it above what is needed to make tap detection work as it would cause more false positives.

## Appearance settings

settings-interface-appearance = Appearance
settings-general-interface-dev_mode = Developer Mode
settings-general-interface-dev_mode-description = This mode can be useful if you need in-depth data or need to interact with connected trackers on a more advanced level.
settings-general-interface-dev_mode-label = Developer Mode
settings-general-interface-theme = Color theme
settings-general-interface-show-navbar-onboarding = Show "{ navbar-onboarding }" on navigation bar
settings-general-interface-show-navbar-onboarding-description = This changes whether the "{ navbar-onboarding }" button shows on the navigation bar.
settings-general-interface-show-navbar-onboarding-label = Show "{ navbar-onboarding }"
settings-general-interface-lang = Select language
settings-general-interface-lang-description = Change the default language.
settings-general-interface-lang-placeholder = Select the language to use
# Keep the font name untranslated
settings-interface-appearance-font = GUI font
settings-interface-appearance-font-description = This changes the font used by the interface.
settings-interface-appearance-font-placeholder = Default font
settings-interface-appearance-font-os_font = OS font
settings-interface-appearance-font-slime_font = Default font
settings-interface-appearance-font_size = Base font scaling
settings-interface-appearance-font_size-description = This affects the font size of the whole interface except this settings panel.
settings-interface-appearance-decorations = Use the system native decorations
settings-interface-appearance-decorations-description = This will not render the top bar of the interface and will use the operating system's instead.
settings-interface-appearance-decorations-label = Use native decorations

## Notification settings

settings-interface-notifications = Notifications
settings-general-interface-serial_detection = Serial device detection
settings-general-interface-serial_detection-description = This option will show a pop-up every time you plug in a new serial device that could be a tracker. It helps to improve the setup process of a tracker.
settings-general-interface-serial_detection-label = Serial device detection
settings-general-interface-feedback_sound = Feedback sound
settings-general-interface-feedback_sound-description = This option plays a sound when a reset is triggered.
settings-general-interface-feedback_sound-label = Feedback sound
settings-general-interface-feedback_sound-volume = Feedback sound volume
settings-general-interface-connected_trackers_warning = Connected trackers warning
settings-general-interface-connected_trackers_warning-description = This option will show a pop-up every time you try exiting SlimeVR while having one or more connected trackers. It reminds you to turn off your trackers when you are done to preserve battery life.
settings-general-interface-connected_trackers_warning-label = Connected trackers warning on exit

## Behavior settings

settings-interface-behavior = Behavior
settings-general-interface-dev_mode = Developer Mode
settings-general-interface-dev_mode-description = This mode can be useful if you need in-depth data or need to interact with connected trackers on a more advanced level.
settings-general-interface-dev_mode-label = Developer Mode
settings-general-interface-use_tray = Minimize to system tray
settings-general-interface-use_tray-description = Lets you close the window without closing the SlimeVR Server so you can continue using it without having the GUI bother you.
settings-general-interface-use_tray-label = Minimize to system tray
settings-general-interface-discord_presence = Share activity on Discord
settings-general-interface-discord_presence-description = Tells your Discord client that you are using SlimeVR along with the number of IMU trackers you are using.
settings-general-interface-discord_presence-label = Share activity on Discord
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

settings-serial = Serial Console
# This cares about multilines
settings-serial-description =
    This is a live information feed for serial communication.
    May be useful to debug firmware or hardware issues.
settings-serial-connection_lost = Connection to serial lost, Reconnecting...
settings-serial-reboot = Yeniden Başlat
settings-serial-factory_reset = Factory Reset
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>Warning:</b> This will reset the tracker to factory settings.
    Which means Wi-Fi and calibration settings <b>will all be lost!</b>
settings-serial-factory_reset-warning-ok = Ben ne yaptığımı biliyorum
settings-serial-factory_reset-warning-cancel = İptal et
settings-serial-serial_select = Select a serial port
settings-serial-auto_dropdown_item = Otomatik
settings-serial-get_wifi_scan = Get WiFi Scan
settings-serial-enter_pairing = Enter Pairing
settings-serial-exit_pairing = Exit Pairing
settings-serial-calibrate = Calibrate
settings-serial-six_side_calibrate = 6-Side Calibrate
settings-serial-dfu = Enter DFU
settings-serial-meow = Meow!
settings-serial-file_type = Plain text
settings-serial-save_logs = Save To File
settings-serial-send_command = Send
settings-serial-send_command-placeholder = Command...
settings-serial-send_command-warning = <b>Warning:</b> Running serial commands can lead to data loss or brick the trackers.
settings-serial-send_command-warning-ok = I know what I'm doing
settings-serial-send_command-warning-cancel = Cancel

## OSC router settings

settings-osc-router = OSC router
# This cares about multilines
settings-osc-router-description =
    Forward OSC messages from another program.
    Useful for using another OSC program with VRChat, for example.
settings-osc-router-enable = Etkinleştir
settings-osc-router-enable-description = Toggle the forwarding of messages.
settings-osc-router-enable-label = Etkinleştir
settings-osc-router-network = Network ports
# This cares about multilines
settings-osc-router-network-description =
    Set the ports for listening and sending data.
    These can be the same as other ports used in the SlimeVR server.
settings-osc-router-network-port_in = 
    .label = Port In
    .placeholder = Port in (default: 9002)
settings-osc-router-network-port_out = 
    .label = Port Out
    .placeholder = Port out (default: 9000)
settings-osc-router-network-address = Ağ adresi
settings-osc-router-network-address-description = Set the address to send out data at.
settings-osc-router-network-address-placeholder = IPV4 address

## OSC VRChat settings

settings-osc-vrchat = VRChat OSC Trackers
# This cares about multilines
settings-osc-vrchat-description-v1 =
    Change settings specific to the OSC Trackers standard used for sending
    tracking data to applications without SteamVR (ex. Quest standalone).
    Make sure to enable OSC in VRChat via the Action Menu under OSC > Enabled.
settings-osc-vrchat-enable = Etkinleştir
settings-osc-vrchat-enable-description = Toggle the sending and receiving of data.
settings-osc-vrchat-enable-label = Etkinleştir
settings-osc-vrchat-oscqueryEnabled = Enable OSCQuery
settings-osc-vrchat-oscqueryEnabled-description =
    OSCQuery automatically detects running instances of VRChat and sends them data.
    It can also advertise itself to them in order to receive HMD and controller data.
    To allow receiving HMD and controller data from VRChat, go in your main menu's settings
    under "Tracking & IK" and enable "Allow Sending Head and Wrist VR Tracking OSC Data".
settings-osc-vrchat-oscqueryEnabled-label = Enable OSCQuery
settings-osc-vrchat-network = Network ports
settings-osc-vrchat-network-description-v1 = Set the ports for listening and sending data. Can be left untouched for VRChat.
settings-osc-vrchat-network-port_in = 
    .label = Port In
    .placeholder = Port in (default: 9001)
settings-osc-vrchat-network-port_out = 
    .label = Port Out
    .placeholder = Port out (default: 9000)
settings-osc-vrchat-network-address = Ağ adresi
settings-osc-vrchat-network-address-description-v1 = Choose which address to send out data to. Can be left untouched for VRChat.
settings-osc-vrchat-network-address-placeholder = VRChat ip address
settings-osc-vrchat-network-trackers = Trackers
settings-osc-vrchat-network-trackers-description = Toggle the sending of specific trackers via OSC.
settings-osc-vrchat-network-trackers-chest = Chest
settings-osc-vrchat-network-trackers-hip = Hip
settings-osc-vrchat-network-trackers-knees = Knees
settings-osc-vrchat-network-trackers-feet = Feet
settings-osc-vrchat-network-trackers-elbows = Elbows

## VMC OSC settings

settings-osc-vmc = Virtual Motion Capture
# This cares about multilines
settings-osc-vmc-description =
    Change settings specific to the VMC (Virtual Motion Capture) protocol
    to send SlimeVR's bone data and receive bone data from other apps.
settings-osc-vmc-enable = Enable
settings-osc-vmc-enable-description = Toggle the sending and receiving of data.
settings-osc-vmc-enable-label = Enable
settings-osc-vmc-network = Network ports
settings-osc-vmc-network-description = Set the ports for listening and sending data via VMC.
settings-osc-vmc-network-port_in = 
    .label = Port In
    .placeholder = Port in (default: 39540)
settings-osc-vmc-network-port_out = 
    .label = Port Out
    .placeholder = Port out (default: 39539)
settings-osc-vmc-network-address = Network address
settings-osc-vmc-network-address-description = Choose which address to send out data at via VMC.
settings-osc-vmc-network-address-placeholder = IPV4 address
settings-osc-vmc-vrm = VRM Model
settings-osc-vmc-vrm-description = Load a VRM model to allow head anchor and enable a higher compatibility with other applications.
settings-osc-vmc-vrm-untitled_model = Untitled model
settings-osc-vmc-vrm-file_select = Drag & drop a model to use, or <u>browse</u>
settings-osc-vmc-anchor_hip = Anchor at hips
settings-osc-vmc-anchor_hip-description = Anchor the tracking at the hips, useful for seated VTubing. If disabling, load a VRM model.
settings-osc-vmc-anchor_hip-label = Anchor at hips
settings-osc-vmc-mirror_tracking = Mirror tracking
settings-osc-vmc-mirror_tracking-description = Mirror the tracking horizontally.
settings-osc-vmc-mirror_tracking-label = Mirror tracking

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

onboarding-skip = Skip setup
onboarding-continue = Continue
onboarding-wip = Work in progress
onboarding-previous_step = Previous step
onboarding-setup_warning =
    <b>Warning:</b> The initial setup is required for good tracking,
    it is needed if this is your first time using SlimeVR.
onboarding-setup_warning-skip = Skip setup
onboarding-setup_warning-cancel = Continue setup

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
onboarding-wifi_creds-skip = Skip Wi-Fi settings
onboarding-wifi_creds-submit = Submit!
onboarding-wifi_creds-ssid = 
    .label = Wi-Fi name
    .placeholder = Enter Wi-Fi name
onboarding-wifi_creds-ssid-required = Wi-Fi name is required
onboarding-wifi_creds-password = 
    .label = Password
    .placeholder = Enter password
onboarding-wifi_creds-dongle-title = Trackers using a dongle
onboarding-wifi_creds-dongle-description = If your trackers came with a dongle, plug it into your device and you should be good to go!
onboarding-wifi_creds-dongle-wip = This section is a work in progress. A dedicated page to manage trackers that connect via a dongle will be made soon.
onboarding-wifi_creds-dongle-continue = Continue with a dongle

## Mounting setup

onboarding-reset_tutorial-back = Go back to Mounting calibration
onboarding-reset_tutorial = Reset tutorial
onboarding-reset_tutorial-explanation = While you use your trackers, they might get out of alignment because of IMU yaw drift, or because you might have moved them physically. You have several ways to fix this.
onboarding-reset_tutorial-skip = Skip step
# Cares about multiline
onboarding-reset_tutorial-0 =
    Tap the highlighted tracker { $taps } times to trigger a yaw reset.
    
    This will make the trackers face the same direction as your headset (HMD).
# Cares about multiline
onboarding-reset_tutorial-1 =
    Tap the highlighted tracker { $taps } times to trigger a full reset.
    
    You need to be standing for this (i-pose). There is a 3 seconds delay (configurable) before it actually happens.
    This fully resets the position and rotation of all your trackers. It should fix most issues.
# Cares about multiline
onboarding-reset_tutorial-2 =
    Tap the highlighted tracker { $taps } times to trigger mounting calibration.
    
    Mounting calibration adjusts for how trackers are placed on your body. If they've moved or rotated significantly, this helps recalibrate their orientation.
    
    You need to be in a pose like you are skiing as shown in the Automatic Mounting wizard, and you have a 3 second delay (configurable) before it gets triggered.

## Install info

install-info_udev-rules_modal_title = Hardware udev access rules not found
install-info_udev-rules_warning = Access rules via udev are required for serial console access & dongle connection. Paste the following command into your terminal to add the udev rules.
install-info_udev-rules_modal_button = Close
install-info_udev-rules_modal-dont-show-again_checkbox = Don't show again

## Setup start

onboarding-home = Welcome to SlimeVR
onboarding-home-start = Let's get set up!

## Setup done

onboarding-done-title = You're all set!
onboarding-done-description = Enjoy your full-body experience
onboarding-done-close = Close setup

## Tracker connection setup

onboarding-connect_tracker-back = Go back to Wi-Fi credentials
onboarding-connect_tracker-title = Connect trackers
onboarding-connect_tracker-description-p0-v1 = Now onto the fun part, connecting trackers!
onboarding-connect_tracker-description-p1-v1 = Connect each tracker one at a time through a USB port.
onboarding-connect_tracker-issue-serial = I'm having trouble connecting!
onboarding-connect_tracker-usb = USB Tracker
onboarding-connect_tracker-connection_status-none = Looking for trackers
onboarding-connect_tracker-connection_status-serial_init = Connecting to serial device
onboarding-connect_tracker-connection_status-obtaining_mac_address = Obtaining the tracker mac address
onboarding-connect_tracker-connection_status-provisioning = Sending Wi-Fi credentials
onboarding-connect_tracker-connection_status-connecting = Trying to connect to Wi-Fi
onboarding-connect_tracker-connection_status-looking_for_server = Looking for server
onboarding-connect_tracker-connection_status-connection_error = Unable to connect to Wi-Fi
onboarding-connect_tracker-connection_status-could_not_find_server = Could not find the server
onboarding-connect_tracker-connection_status-done = Connected to the Server
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
        [0] No trackers
        [one] 1 tracker
       *[other] { $amount } trackers
    } connected
onboarding-connect_tracker-next = I connected all my trackers

## Tracker calibration tutorial

onboarding-calibration_tutorial = IMU Calibration Tutorial
onboarding-calibration_tutorial-subtitle = This will help reduce tracker drifting!
onboarding-calibration_tutorial-description-v1 = After turning on your trackers, place them on a stable surface for a moment to allow for calibration. Calibration can be performed at any time after the trackers are powered on—this page simply provides a tutorial. To begin, click the "{ onboarding-calibration_tutorial-calibrate }" button, then <b>do not move your trackers!</b>
onboarding-calibration_tutorial-calibrate = I placed my trackers on a table
onboarding-calibration_tutorial-status-waiting = Waiting for you
onboarding-calibration_tutorial-status-calibrating = Calibrating
onboarding-calibration_tutorial-status-success = Nice!
onboarding-calibration_tutorial-status-error = The tracker was moved
onboarding-calibration_tutorial-skip = Skip tutorial

## Tracker assignment tutorial

onboarding-assignment_tutorial = How to prepare a Slime Tracker before putting it on
onboarding-assignment_tutorial-first_step = 1. Place a body part sticker (if you have one) on the tracker according to your choosing
# This text has a character limit of around 11 characters, so please keep it short
onboarding-assignment_tutorial-sticker = Sticker
onboarding-assignment_tutorial-second_step-v2 = 2. Attach the strap to your tracker, keeping the velcro side of the strap facing the same direction as the slime face of your tracker:
onboarding-assignment_tutorial-second_step-continuation-v2 = The velcro side for the extension should be facing up like the following image:
onboarding-assignment_tutorial-done = I put stickers and straps!

## Tracker assignment setup

onboarding-assign_trackers-back = Go back to Wi-Fi credentials
onboarding-assign_trackers-title = Assign trackers
onboarding-assign_trackers-description = Let's choose which tracker goes where. Click on a location where you want to place a tracker
onboarding-assign_trackers-unassign_all = Unassign all trackers
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned =
    { $assigned } of { $trackers ->
        [one] 1 tracker
       *[other] { $trackers } trackers
    } assigned
onboarding-assign_trackers-advanced = Show advanced assign locations
onboarding-assign_trackers-next = I assigned all the trackers
onboarding-assign_trackers-mirror_view = Mirror view
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
    Left foot is assigned but you need { $unassigned ->
        [0] the left ankle, left thigh and either the chest, hip or waist
        [1] the left thigh and either the chest, hip or waist
        [2] the left ankle and either the chest, hip or waist
        [3] either the chest, hip or waist
        [4] the left ankle and left thigh
        [5] the left thigh
        [6] the left ankle
       *[unknown] Unknown unassigned body part
    } to also be assigned!
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

onboarding-choose_mounting = What mounting calibration method to use?
# Multiline text
onboarding-choose_mounting-description = Mounting orientation corrects for the placement of trackers on your body.
onboarding-choose_mounting-auto_mounting = Automatic mounting
# Italicized text
onboarding-choose_mounting-auto_mounting-label-v2 = Recommended
onboarding-choose_mounting-auto_mounting-description = This will automatically detect the mounting orientations for all of your trackers from 2 poses
onboarding-choose_mounting-manual_mounting = Manual mounting
# Italicized text
onboarding-choose_mounting-manual_mounting-label-v2 = Might not be precise enough
onboarding-choose_mounting-manual_mounting-description = This will let you choose the mounting orientation manually for each tracker
# Multiline text
onboarding-choose_mounting-manual_modal-title =
    Are you sure you want to do
    the automatic mounting calibration?
onboarding-choose_mounting-manual_modal-description = <b>The manual mounting calibration is recommended for new users</b>, as the automatic mounting calibration's poses can be hard to get right first and may require some practice.
onboarding-choose_mounting-manual_modal-confirm = I'm sure of what I'm doing
onboarding-choose_mounting-manual_modal-cancel = Cancel

## Tracker manual mounting setup

onboarding-manual_mounting-back = Go back to Enter VR
onboarding-manual_mounting = Manual Mounting
onboarding-manual_mounting-description = Click on every tracker and select which way they are mounted
onboarding-manual_mounting-auto_mounting = Automatic mounting
onboarding-manual_mounting-next = Next step

## Tracker automatic mounting setup

onboarding-automatic_mounting-back = Go back to Enter VR
onboarding-automatic_mounting-title = Mounting Calibration
onboarding-automatic_mounting-description = For SlimeVR trackers to work, we need to assign a mounting orientation to your trackers to align them with your physical tracker mounting.
onboarding-automatic_mounting-manual_mounting = Manual mounting
onboarding-automatic_mounting-next = Next step
onboarding-automatic_mounting-prev_step = Previous step
onboarding-automatic_mounting-done-title = Mounting orientations calibrated.
onboarding-automatic_mounting-done-description = Your mounting calibration is complete!
onboarding-automatic_mounting-done-restart = Try again
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
onboarding-automatic_proportions-prev_step = Previous step
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
onboarding-automatic_proportions-requirements-next = I have read the requirements
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
onboarding-automatic_proportions-start_recording-title = Get ready to move
onboarding-automatic_proportions-start_recording-description = We're now going to record some specific poses and moves. These will be prompted in the next screen. Be ready to start when the button is pressed!
onboarding-automatic_proportions-start_recording-next = Start Recording
onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = Recording in progress...
onboarding-automatic_proportions-recording-description-p1 = Make the moves shown below:
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
        [one] 1 second left
       *[other] { $time } seconds left
    }
onboarding-automatic_proportions-verify_results-title = Verify results
onboarding-automatic_proportions-verify_results-description = Check the results below, do they look correct?
onboarding-automatic_proportions-verify_results-results = Recording results
onboarding-automatic_proportions-verify_results-processing = Processing the result
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
tray_or_exit_modal-radio-exit = Exit on close
tray_or_exit_modal-radio-tray = Minimize to system tray
tray_or_exit_modal-submit = Save
tray_or_exit_modal-cancel = Cancel

## Unknown device modal

unknown_device-modal-title = A new tracker was found!
unknown_device-modal-description =
    There is a new tracker with MAC address <b>{ $deviceId }</b>.
    Do you want to connect it to SlimeVR?
unknown_device-modal-confirm = Sure!
unknown_device-modal-forget = Ignore it
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
