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
skeleton_bone-SHOULDERS_DISTANCE = Omuz Mesafesi
skeleton_bone-SHOULDERS_WIDTH = Omuz Genişliği
skeleton_bone-arm_group = Kol uzunluğu
skeleton_bone-UPPER_ARM = Üst Kol Uzunluğu
skeleton_bone-LOWER_ARM = Alt Kol Uzunluğu
skeleton_bone-ELBOW_OFFSET = Dirsek hizası

## Tracker reset buttons

reset-reset_all = Tüm oranları sıfırla
reset-full = Sıfırlama

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
navbar-onboarding = Kurulum Sihirbazı
navbar-settings = Ayarlar

## Biovision hierarchy recording

bvh-start_recording = BVH Kaydet
bvh-recording = Kaydediliyor

## Tracking pause

tracking-unpaused = Takibi duraklat

## Widget: Overlay settings


## Widget: Drift compensation


## Widget: Clear Mounting calibration


## Widget: Developer settings

widget-developer_mode = Geliştirici Modu
widget-developer_mode-high_contrast = Yüksek kontrast
widget-developer_mode-precise_rotation = Hassas dönüş
widget-developer_mode-sort_by_name = Ada göre sırala
widget-developer_mode-more_info = Daha fazla bilgi

## Widget: IMU Visualizer

widget-imu_visualizer = Rotasyon
widget-imu_visualizer-rotation_preview = Önizle

## Widget: Skeleton Visualizer


## Tracker status

tracker-status-none = Durum Yok
tracker-status-busy = Meşgul
tracker-status-error = Hata
tracker-status-disconnected = Bağlantı kesildi
tracker-status-ok = İYİ

## Tracker status columns

tracker-table-column-name = İsim
tracker-table-column-type = Tür
tracker-table-column-battery = Pil
tracker-table-column-tps = TPS
tracker-table-column-temperature = Sıcaklık °C
tracker-table-column-rotation = Rotasyon X/Y/Z
tracker-table-column-position = Pozisyon X/Y/Z
tracker-table-column-url = URL

## Tracker rotation

tracker-rotation-front = Ön
tracker-rotation-left = Sol
tracker-rotation-right = Sağ
tracker-rotation-back = Arka

## Tracker information

tracker-infos-manufacturer = Üretici
tracker-infos-display_name = Görünen Ad
tracker-infos-custom_name = Özel Ad
tracker-infos-url = Takipçi URL'si
tracker-infos-version = Yazılım Sürümü
tracker-infos-hardware_rev = Donanım Revizyonu
tracker-infos-hardware_identifier = Donanım Kimliği
tracker-infos-imu = IMU Sensör
tracker-infos-board_type = Ana kart

## Tracker settings

tracker-settings-back = Takipçi listesine geri dön
tracker-settings-title = Takipçi ayarları
tracker-settings-assignment_section-description = Tracker'in vücudun hangi kısmına atandığı.
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Takipçi adı
tracker-settings-name_section-placeholder = NightyBeast'in sol bacağı

## Tracker part card info

tracker-part_card-no_name = İsimsiz
tracker-part_card-unassigned = Atanmamış

## Body assignment menu

body_assignment_menu = Bu takipçinin nerede olmasını istiyorsunuz?
body_assignment_menu-manage_trackers = Tüm takipçileri yönet

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = hangi takipçiyi atayacaksınız?
tracker_selection_menu-HEAD = Başınıza { -tracker_selection-part }
tracker_selection_menu-RIGHT_SHOULDER = Sağ omuzunuza { -tracker_selection-part }
tracker_selection_menu-RIGHT_UPPER_ARM = Sağ üst kolunuza { -tracker_selection-part }
tracker_selection_menu-RIGHT_LOWER_ARM = Sağ alt kolunuza { -tracker_selection-part }
tracker_selection_menu-RIGHT_HAND = Sağ elinize { -tracker_selection-part }
tracker_selection_menu-RIGHT_UPPER_LEG = Say kalçanıza { -tracker_selection-part }
tracker_selection_menu-RIGHT_LOWER_LEG = Sağ ayak bileğinize { -tracker_selection-part }
tracker_selection_menu-RIGHT_FOOT = Sağ ayağınıza { -tracker_selection-part }
tracker_selection_menu-UPPER_CHEST = Üst göğüsünüze { -tracker_selection-part }
tracker_selection_menu-unassigned = Atanmamış takipçiler
tracker_selection_menu-assigned = Atanan takipçiler
tracker_selection_menu-neck_warning-done = Riskleri anlıyorum
tracker_selection_menu-neck_warning-cancel = İptal

## Mounting menu

mounting_selection_menu = Bu takipçinin nerede olmasını istiyorsunuz?
mounting_selection_menu-close = Kapat

## Sidebar settings

settings-sidebar-title = Ayarlar
settings-sidebar-general = Genel
settings-sidebar-interface = Arayüz
settings-sidebar-osc_router = OSC yönlendirici
settings-sidebar-osc_trackers = VRChat OSC Takipçileri
settings-sidebar-serial = Seri konsol

## SteamVR settings

settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = SteamVR takipçileri
settings-general-steamvr-trackers-waist = Bel
settings-general-steamvr-trackers-chest = Göğüs

## Tracker mechanics

settings-general-tracker_mechanics-filtering-amount = Miktar

## FK/Tracking settings

settings-general-fk_settings-leg_fk = Bacak takibi
settings-general-fk_settings-arm_fk = Kol takibi
settings-general-fk_settings-skeleton_settings-description = İskelet ayarlarını açın veya kapatın. Bunları açık bırakmanız önerilir.

## Gesture control settings (tracker tapping)


## Appearance settings


## Notification settings


## Behavior settings


## Serial settings

settings-serial-reboot = Yeniden Başlat
settings-serial-factory_reset-warning-ok = Ben ne yaptığımı biliyorum
settings-serial-factory_reset-warning-cancel = İptal et
settings-serial-auto_dropdown_item = Otomatik

## OSC router settings

settings-osc-router-enable = Etkinleştir
settings-osc-router-enable-label = Etkinleştir
settings-osc-router-network-address = Ağ adresi

## OSC VRChat settings

settings-osc-vrchat-enable = Etkinleştir
settings-osc-vrchat-enable-label = Etkinleştir
settings-osc-vrchat-network-address = Ağ adresi

## VMC OSC settings


## Common OSC settings


## Advanced settings


## Home Screen


## Tracking Checlist


## Setup/onboarding menu


## Wi-Fi setup


## Mounting setup


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

