# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = 載入中…
websocket-connection_lost = 伺服器當機了！
websocket-connection_lost-desc = SlimeVR 伺服器程式似乎當機了。請檢查日誌並重新啟動程式
websocket-timedout = 無法連接到伺服器
websocket-timedout-desc = SlimeVR 伺服器程式似乎已當機或逾時。請檢查日誌並重新啟動程式
websocket-error-close = 退出 SlimeVR
websocket-error-logs = 開啟日誌資料夾

## Update notification

version_update-title = 有可用的新版本：{ $version }
version_update-description = 按下「{ version_update-update }」將為你下載 SlimeVR 安裝程式。
version_update-update = 更新
version_update-close = 關閉

## Tips

tips-find_tracker = 若你不確定手上的追蹤器是哪一個，搖一搖它，對應的項目會被高亮顯示出來。
tips-do_not_move_heels = 測量過程中，請勿移動腳跟！
tips-file_select = 拖曳檔案或 <u>瀏覽檔案</u> 以使用。
tips-failed_webgl = 初始化 WebGL 失敗。

## Units

unit-meter = 公尺
unit-foot = 英尺
unit-inch = 英吋
unit-cm = 公分

## Dropdown

dropdown_select-all = 全部選擇
dropdown_unselect-all = 全部取消選擇

## Text input

# Accessible name for the eye button that reveals/hides a typed password
input-password-show = 顯示密碼
input-password-hide = 隱藏密碼

## File input

# Accessible name and tooltip for the icon-only button that clears a picked path
file_input-clear = 取消選取檔案
file_input-clear_folder = 取消選取資料夾

## Window controls

# Accessible names for the icon-only buttons in the title bar
titlebar-docs = 開啟文件
titlebar-settings = 開啟設定
titlebar-update = 下載更新
titlebar-minimize = 最小化
titlebar-maximize = 最大化
titlebar-close = 關閉

## Body parts

body_part-NONE = 未分配
body_part-HEAD = 頭部
body_part-NECK = 頸部
body_part-RIGHT_SHOULDER = 右肩
body_part-RIGHT_UPPER_ARM = 右上臂
body_part-RIGHT_LOWER_ARM = 右前臂
body_part-RIGHT_HAND = 右手
body_part-RIGHT_UPPER_LEG = 右大腿
body_part-RIGHT_LOWER_LEG = 右腳踝
body_part-RIGHT_FOOT = 右腳
body_part-UPPER_CHEST = 上胸
body_part-LOWER_CHEST = 下胸
body_part-UPPER_WAIST = 上腰
body_part-LOWER_WAIST = 下腰
body_part-HIP = 臀部
body_part-LEFT_SHOULDER = 左肩
body_part-LEFT_UPPER_ARM = 左上臂
body_part-LEFT_LOWER_ARM = 左前臂
body_part-LEFT_HAND = 左手
body_part-LEFT_UPPER_LEG = 左大腿
body_part-LEFT_LOWER_LEG = 左腳踝
body_part-LEFT_FOOT = 左腳
body_part-LEFT_THUMB_METACARPAL = 左拇指掌骨
body_part-LEFT_THUMB_PROXIMAL = 左拇指近端
body_part-LEFT_THUMB_DISTAL = 左拇指遠端
body_part-LEFT_INDEX_PROXIMAL = 左食指近端
body_part-LEFT_INDEX_INTERMEDIATE = 左食指中端
body_part-LEFT_INDEX_DISTAL = 左食指遠端
body_part-LEFT_MIDDLE_PROXIMAL = 左中指近端
body_part-LEFT_MIDDLE_INTERMEDIATE = 左中指中端
body_part-LEFT_MIDDLE_DISTAL = 左中指遠端
body_part-LEFT_RING_PROXIMAL = 左無名指近端
body_part-LEFT_RING_INTERMEDIATE = 左無名指中端
body_part-LEFT_RING_DISTAL = 左無名指遠端
body_part-LEFT_LITTLE_PROXIMAL = 左小指近端
body_part-LEFT_LITTLE_INTERMEDIATE = 左小指中端
body_part-LEFT_LITTLE_DISTAL = 左小指遠端
body_part-RIGHT_THUMB_METACARPAL = 右拇指掌骨
body_part-RIGHT_THUMB_PROXIMAL = 右拇指近端
body_part-RIGHT_THUMB_DISTAL = 右拇指遠端
body_part-RIGHT_INDEX_PROXIMAL = 右食指近端
body_part-RIGHT_INDEX_INTERMEDIATE = 右食指中端
body_part-RIGHT_INDEX_DISTAL = 右食指遠端
body_part-RIGHT_MIDDLE_PROXIMAL = 右中指近端
body_part-RIGHT_MIDDLE_INTERMEDIATE = 右中指中端
body_part-RIGHT_MIDDLE_DISTAL = 右中指遠端
body_part-RIGHT_RING_PROXIMAL = 右無名指近端
body_part-RIGHT_RING_INTERMEDIATE = 右無名指中端
body_part-RIGHT_RING_DISTAL = 右無名指遠端
body_part-RIGHT_LITTLE_PROXIMAL = 右小指近端
body_part-RIGHT_LITTLE_INTERMEDIATE = 右小指中端
body_part-RIGHT_LITTLE_DISTAL = 右小指遠端
body_part-LEFT_BIG_TOE = 左腳大腳趾
body_part-LEFT_INDEX_TOE = 左腳第二腳趾
body_part-LEFT_MIDDLE_TOE = 左腳第三腳趾
body_part-LEFT_RING_TOE = 左腳第四腳趾
body_part-LEFT_LITTLE_TOE = 左腳小腳趾
body_part-RIGHT_TOES = 右腳趾
body_part-RIGHT_BIG_TOE = 右腳大腳趾
body_part-RIGHT_INDEX_TOE = 右腳第二腳趾
body_part-RIGHT_MIDDLE_TOE = 右腳第三腳趾
body_part-RIGHT_RING_TOE = 右腳第四腳趾
body_part-RIGHT_LITTLE_TOE = 右腳小腳趾

## BoardType

board_type-UNKNOWN = 不明
board_type-NODEMCU = NodeMCU
board_type-CUSTOM = 自訂主板
board_type-WROOM32 = WROOM32
board_type-WEMOSD1MINI = Wemos D1 Mini
board_type-TTGO_TBASE = TTGO T-Base
board_type-ESP01 = ESP-01
board_type-SLIMEVR = SlimeVR
board_type-SLIMEVR_DEV = SlimeVR 開發板
board_type-SLIMEVR_V1_2 = SlimeVR v1.2
board_type-LOLIN_C3_MINI = Lolin C3 Mini
board_type-BEETLE32C3 = Beetle ESP32-C3
board_type-ESP32C3DEVKITM1 = Espressif ESP32-C3 DevKitM-1
board_type-OWOTRACK = owoTrack
board_type-WRANGLER = Wrangler Joy-Con
board_type-MOCOPI = Sony mocopi
board_type-WEMOSWROOM02 = WeMos WROOM-02 D1 Mini
board_type-XIAO_ESP32C3 = Seeed Studio XIAO ESP32C3
board_type-HARITORA = Haritora
board_type-ESP32C6DEVKITC1 = Espressif ESP32-C6 DevKitC-1
board_type-GLOVE_IMU_SLIMEVR_DEV = SlimeVR Dev IMU 手套
board_type-GESTURES = litten Yº by Gestures
board_type-ESP32S3_SUPERMINI = ESP32-S3 Supermini
board_type-GENERIC_NRF = 通用 nRF
board_type-SLIMEVR_BUTTERFLY_DEV = SlimeVR Dev Butterfly
board_type-SLIMEVR_BUTTERFLY = SlimeVR Butterfly

## Proportions

skeleton_bone-NONE = 無
skeleton_bone-NECK = 頸部長度
skeleton_bone-NECK-desc =
    這是從頭中央到脖子底部的距離。
    若要調整，請上下點頭（如同意、肯定般的樣子），或將頭倒向兩側肩膀，檢查
    其他追蹤器的數值跳動並變更此參數，直到數值跳動小到可以忽略不計。
skeleton_bone-torso_group = 軀幹長度
skeleton_bone-torso_group-desc =
    這是從脖子底部到臀部的距離。
    若要調整，請站立並調整此參數，直到虛擬的臀部與實際臀部對齊。
skeleton_bone-UPPER_CHEST = 上胸長度
skeleton_bone-UPPER_CHEST-desc =
    這是從脖子底部到胸部中間的距離。
    若要調整，請適當調整軀幹長度，檢查各種姿勢（坐下、彎腰、躺下等）並進行
    修改，直到虛擬的脊椎與實際脊椎對齊。
skeleton_bone-LOWER_CHEST = 下胸長度
skeleton_bone-LOWER_CHEST-desc =
    這是從胸部中間到脊椎中間的距離。
    若要調整，請適當調整軀幹長度，檢查各種姿勢（坐下、彎腰、躺下等）並進行
    修改，直到虛擬的脊椎與實際脊椎對齊。
skeleton_bone-UPPER_WAIST = 上腰長度
skeleton_bone-UPPER_WAIST-desc =
    這是從脊椎中間到肚臍些微上面的距離。
    若要調整，請適當調整軀幹長度，檢查各種姿勢（坐下、彎腰、躺下等）並進行
    修改，直到虛擬的脊椎與實際脊椎對齊。
skeleton_bone-LOWER_WAIST = 下腰長度
skeleton_bone-LOWER_WAIST-desc =
    這是從肚臍些微上面到肚臍些微下面的距離。
    若要調整，請適當調整軀幹長度，檢查各種姿勢（坐下、彎腰、躺下等）並進行
    修改，直到虛擬的脊椎與實際脊椎對齊。
skeleton_bone-HIP = 臀部長度
skeleton_bone-HIP-desc =
    這是從肚臍到臀部的距離。
    若要調整，請適當調整軀幹長度，檢查各種姿勢（坐下、彎腰、躺下等）並進行
    修改，直到虛擬的脊椎與實際脊椎對齊。
skeleton_bone-HIPS_WIDTH = 臀部寬度
skeleton_bone-HIPS_WIDTH-desc =
    這是兩腿之間的起始距離。
    若要調整，請站直進行完整重置後，修改到虛擬的腿部與實際腿部對齊。
skeleton_bone-leg_group = 腿部長度
skeleton_bone-leg_group-desc =
    這是從臀部到腳部的距離。
    若要調整，請適當調整軀幹長度後，修改此數值，直到虛擬的腳部
    與實際腳部的高度一致。
skeleton_bone-UPPER_LEG = 大腿長度
skeleton_bone-UPPER_LEG-desc =
    這是從臀部到膝蓋的距離。
    若要調整，請適當調整腿部長度後，修改此數值，直到虛擬的膝蓋
    與實際膝蓋的高度一致。
skeleton_bone-LOWER_LEG = 小腿長度
skeleton_bone-LOWER_LEG-desc =
    這是從膝蓋到腳踝的距離。
    若要調整，請適當調整腿部長度後，修改此數值，直到虛擬的膝蓋
    與實際膝蓋的高度一致。
skeleton_bone-FOOT_LENGTH = 腳部長度
skeleton_bone-FOOT_LENGTH-desc =
    這是從腳踝到腳趾的距離。
    若要調整，請踮起腳尖，修改此數值，直到虛擬的腳部與實際腳部對齊。
skeleton_bone-FOOT_SHIFT = 腳部偏移
skeleton_bone-FOOT_SHIFT-desc =
    這是從膝蓋到腳踝的水平距離。
    這個數值可以校正站直時小腿會向後移動的問題。
    若要調整，請將腳部長度設為 0，進行完整重置後再修改，直到虛擬腳部與
    腳踝中間對齊。
skeleton_bone-SHOULDERS_DISTANCE = 肩膀距離
skeleton_bone-SHOULDERS_DISTANCE-desc =
    這是從脖子底部到肩膀的垂直距離。
    若要調整，請將上臂長度設為 0 再修改，直到虛擬的肘部追蹤器與
    實際的肩膀垂直對齊。
skeleton_bone-SHOULDERS_WIDTH = 肩膀寬度
skeleton_bone-SHOULDERS_WIDTH-desc =
    這是兩側肩膀的水平距離。
    若要調整，請將上臂長度設為 0 再修改，直到虛擬的肘部追蹤器與
    實際的肩膀水平對齊。
skeleton_bone-arm_group = 手臂長度
skeleton_bone-arm_group-desc =
    這是從肩膀到手腕的距離。
    若要調整，請適當調整肩膀距離，將手部距離 Y 設定為 0 再修改，
    直到虛擬的手部追蹤器與手腕對齊。
skeleton_bone-UPPER_ARM = 上臂長度
skeleton_bone-UPPER_ARM-desc =
    這是從肩膀到手肘的距離。
    若要調整，請適當調整手臂長度後，修改此數值，直到虛擬的
    手肘追蹤器與實際手肘的位置一致。
skeleton_bone-LOWER_ARM = 前臂長度
skeleton_bone-LOWER_ARM-desc =
    這是從手肘到手腕的距離。
    若要調整，請適當調整手臂長度後，修改此數值，直到虛擬的
    手肘追蹤器與實際手肘的位置一致。
skeleton_bone-HAND = 手部長度
skeleton_bone-HAND-desc =
    這是從手腕到指關節的距離。
    若要調整，請適當調整手臂長度後，修改此數值，直到虛擬的
    手部追蹤器與實際手指的位置一致。

## Tracker reset buttons

reset-reset_all = 重置軀幹比例
reset-reset_all_warning-reset = 重置軀幹比例
reset-reset_all_warning-cancel = 取消
reset-full = 完整重置
reset-mounting = 配戴重置
reset-mounting-feet = 重置腳部配戴
reset-mounting-toes = 腳趾校正
reset-mounting-fingers = 重置手指配戴
reset-yaw = 左右偏擺重置
reset-error-mounting-need_full_reset = 配戴校正前需要完整重置
reset-error-yaw-need_full_reset = 左右偏擺重置前需要完整重置
reset-error-no_feet_tracker = 未指定/沒有腳部追蹤器

## Navigation bar

navbar-home = 首頁
navbar-body_proportions = 軀幹比例
navbar-trackers_assign = 追蹤器分配
navbar-mounting = 配戴校正
navbar-onboarding = 快速設定
navbar-settings = 詳細設定
navbar-connect_trackers = 連接追蹤器

## Biovision hierarchy recording

bvh-start_recording = 錄製 BVH 檔案
bvh-stop_recording = 儲存 BVH 紀錄
bvh-recording = 錄製中…
bvh-save_title = 儲存 BVH 紀錄

## Tracking pause

tracking-unpaused = 暫停追蹤
tracking-paused = 解除暫停追蹤

## Widget: Developer settings

widget-developer_mode = 開發者選項
widget-developer_mode-high_contrast = 高對比 UI
widget-developer_mode-precise_rotation = 顯示精確旋轉
widget-developer_mode-fast_data_feed = 快速資料更新
widget-developer_mode-raw_slime_rotation = 顯示原始旋轉

## Widget: IMU Visualizer

widget-imu_visualizer = 旋轉
widget-imu_visualizer-preview = 預覽
widget-imu_visualizer-hide = 隱藏
widget-imu_visualizer-rotation_raw = 原始旋轉
widget-imu_visualizer-rotation_preview = 預覽
widget-imu_visualizer-acceleration = 加速度
widget-imu_visualizer-position = 位置
widget-imu_visualizer-stay_aligned = 持續校正

## Tracker status

tracker-status-none = 無
tracker-status-busy = 忙碌
tracker-status-error = 錯誤
tracker-status-disconnected = 連線中斷
tracker-status-occluded = 被遮擋
tracker-status-ok = 已連線
tracker-status-timed_out = 已逾時
tracker-status-sleeping = 睡眠中

## Tracker status columns

tracker-table-column-name = 名稱
tracker-table-column-type = 類型
tracker-table-column-battery = 電量
tracker-table-column-ping = Ping
tracker-table-column-tps = TPS
tracker-table-column-temperature = 溫度 ℃
tracker-table-column-linear-acceleration = 加速度 X/Y/Z
tracker-table-column-rotation = 旋轉 X/Y/Z
tracker-table-column-position = 位置 X/Y/Z
tracker-table-column-stay_aligned = 持續校正
tracker-table-column-url = URL

## Tracker rotation

tracker-rotation-front = 前
tracker-rotation-front_left = 左前方
tracker-rotation-front_right = 右前方
tracker-rotation-left = 左
tracker-rotation-right = 右
tracker-rotation-back = 後
tracker-rotation-back_left = 左後方
tracker-rotation-back_right = 右後方
tracker-rotation-custom = 自訂
tracker-rotation-mounting_reset = （使用配戴校正的方位）

## Tracker information

tracker-infos-manufacturer = 製造商
tracker-infos-display_name = 顯示名稱
tracker-infos-custom_name = 自訂名稱
tracker-infos-url = 追蹤器 URL
tracker-infos-hardware_identifier = 硬體 ID
tracker-infos-data_type = 資料型態
tracker-infos-imu = 慣性測量單元 (IMU)
tracker-infos-board_type = 主板
tracker-infos-network_version = 通訊協定版本
tracker-infos-magnetometer = 磁力計
tracker-infos-magnetometer-status-v1 =
    { $status ->
        [DISABLED] 已停用
        [ENABLED] 已啟用
       *[NOT_SUPPORTED] 不支援
    }
tracker-infos-packet_loss = 封包遺失
tracker-infos-packets_lost = 已遺失封包
tracker-infos-packets_received = 已接收封包

## Tracker settings

tracker-settings-back = 返回追蹤器列表
tracker-settings-title = 追蹤器設定
tracker-settings-assignment_section = 分配追蹤器
tracker-settings-assignment_section-description = 這個追蹤器分配到的身體部位
tracker-settings-assignment_section-edit = 編輯分配
tracker-settings-mounting_section = 配戴方式
tracker-settings-mounting_section-description = 這個追蹤器要配戴在哪裡？
tracker-settings-mounting_section-edit = 編輯配戴方式
tracker-settings-use_mag = 允許使用這個追蹤器的磁力計
# Multiline!
tracker-settings-use_mag-description =
    如果「在追蹤器上啟用磁力計」功能已開啟，是否要在這個追蹤器上啟用它來減緩偏移？<b>切換本選項時請勿關閉追蹤器的電源！</b>
    
    請先開啟「在追蹤器上啟用磁力計」功能，<magSetting>點選此處以移動至該設定</magSetting>。
tracker-settings-use_mag-label = 允許使用這個追蹤器的磁力計
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = 追蹤器名稱
tracker-settings-name_section-description = 給它起一個可愛的名字吧 ^^
tracker-settings-name_section-placeholder = ↖★煞氣a黑貓☆↘的美味右腿
tracker-settings-name_section-label = 追蹤器名稱
tracker-settings-forget = 忘記追蹤器
tracker-settings-forget-description = 從 SlimeVR 伺服器程式中移除該追蹤器，且直到重新啟動伺服器前不會再次連接。該追蹤器的設定不會遺失。
tracker-settings-forget-label = 忘記追蹤器
tracker-settings-update-unavailable-v2 = 未找到可用版本
tracker-settings-update-incompatible = 電路板不相容，無法更新。
tracker-settings-update-low-battery = 無法更新，電池電量低於 50%
tracker-settings-update-up_to_date = 已為最新版本
tracker-settings-update-blocked = 無法更新，沒有其他可用版本。
tracker-settings-update = 立即更新
tracker-settings-update-title = 韌體版本
tracker-settings-current-version = 目前版本
tracker-settings-latest-version = 最新版本
tracker-settings-build-date = 建置日期

## Dongle settings

dongle-infos-model = 型號
dongle-infos-hardware_revision = 硬體版本
dongle-status-connected = 已連接
dongle-status-disconnected = 連線中斷
dongle-settings-back = 返回追蹤器列表
dongle-settings-title = 接收器設定
dongle-settings-name_section = 接收器名稱
dongle-settings-name_section-description = 給它起一個可愛的名字吧 owo
dongle-settings-name_section-placeholder = 阿喵的那隻 usb
dongle-settings-update = 立即更新
dongle-settings-update-title = 韌體版本
dongle-settings-paired_trackers = 已配對的追蹤器
dongle-settings-paired_trackers-empty = 追蹤器尚未配對至此接收器。
dongle-settings-pair = 配對追蹤器
dongle-settings-forget_tracker = 忘記
dongle-settings-telemetry-title = 即時遙測
dongle-settings-telemetry-select_trackers = 選擇追蹤器
dongle-settings-telemetry-select_trackers-summary = { $count } / { $total } 個追蹤器
dongle-settings-telemetry-show_min_max = 最小/最大
dongle-settings-telemetry-chart_rssi = RSSI · dBm
dongle-settings-telemetry-chart_loss = 封包遺失率 · %
dongle-settings-telemetry-chart_gaps = 封包遺失間隔 · 相對於 TPS 的封包間隔
dongle-settings-telemetry-footnote = 封包遺失間隔會顯示封包更新頻率晚於追蹤器的 TPS 的區間，越寬越深的標記表示更長的封包遺失間隔。
# Accessible name and tooltip for the icon-only button that pauses/resumes the live telemetry feed
dongle-settings-telemetry-live-pause = 暫停即時遙測
dongle-settings-telemetry-live-resume = 恢復即時遙測

## Tracker part card info

tracker-part_card-unassigned = 未分配

## Body assignment menu

body_assignment_menu = 將這個追蹤器配戴在哪裡？
body_assignment_menu-description = 選擇要將此追蹤器分配到的身體部位。除了逐個設定外，你也可以一次設定所有追蹤器。
body_assignment_menu-manage_trackers = 管理所有追蹤器
body_assignment_menu-unassign_tracker = 解除分配

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>警告：</b>將追蹤器戴在頸部有致命危險，綁太緊可能會阻礙頭部
    血液循環，進而導致窒息。
tracker_selection_menu-neck_warning-done = 我已瞭解其風險
tracker_selection_menu-neck_warning-cancel = 取消

## Mounting menu

mounting_selection_menu = 你想將此追蹤器戴在哪裡？
mounting_selection_menu-close = 關閉

## Sidebar settings

settings-sidebar-title = 設定
settings-sidebar-general = 一般設定
settings-sidebar-outputs = 輸出
settings-sidebar-routing = 骨骼路由
settings-sidebar-driver = SteamVR / Monado
settings-sidebar-resets = 重置
settings-sidebar-stay_aligned = 持續校正
settings-sidebar-tracking = 追蹤
settings-sidebar-trackers = 追蹤器
settings-sidebar-interface = 使用者介面
settings-sidebar-vrchat_osc = VRChat OSC
settings-sidebar-osc_vmc = VMC
settings-sidebar-utils = 工具
settings-sidebar-serial = 序列埠終端
settings-sidebar-appearance = 外觀
settings-sidebar-home = 主畫面
settings-sidebar-checklist = 追蹤清單
settings-sidebar-notifications = 通知
settings-sidebar-behavior = 行為
settings-sidebar-firmware-tool = DIY 韌體工具
settings-sidebar-vrc_warnings = VRChat 配置警告
settings-sidebar-advanced = 進階

## Bone routing settings

settings-routing = 骨骼路由
settings-routing-description =
    選擇每個輸出方法要傳送哪些資料。
    SteamVR/Monado、VRChat OSC、VMC 可以個別接收不同組合的身體部位。
settings-routing-mode = 路由模式
settings-routing-automatic-label = 自動調整路由
settings-routing-mode-description =
    開啟自動調整路由時，SlimeVR 會挑選每個骨骼最佳的輸出方法，避免同時同一個骨骼以兩個方法輸出：優先使用 SteamVR/Monado，VRChat OSC 會接收其無法使用的部位，VMC 會取得全身骨骼。
    關閉此選項以手動調整每個骨骼的輸出位置，也可以將同一個骨骼同時以不同方法輸出。
settings-routing-bone = 骨骼
settings-routing-cell-required = 此輸出方法為必要
settings-routing-cell-unavailable = 在此平台無法使用此輸出方法
settings-routing-cell-duplicate = 骨骼已被路由到多個輸出方法，可能導致應用程式出現重複的追蹤器
settings-routing-duplicate-warning =
    { $bones } { $boneCount ->
        *[other] 已
    }被路由到多個輸出方法（{ $outputs }），可能導致應用程式出現重複的追蹤器。
settings-routing-outputs = 輸出方法
settings-routing-bones = 骨骼
settings-routing-bones-description = 勾選表示骨骼會以此方法輸出。橫線表示此輸出不支援這個骨骼。關閉自動調整路由以手動選擇。
settings-routing-output-driver = SteamVR / Monado
settings-routing-output-vrc_osc = VRChat OSC
settings-routing-output-vmc = VMC
settings-routing-output-bone-count = { $routed } / { $accepts } 骨骼已路由
settings-routing-output-badge-sending = 發送中
settings-routing-output-sending-description = 已連接，正在透過此輸出方法傳送骨骼資料。
settings-routing-output-badge-stopped = 未執行
settings-routing-output-stopped-description = SteamVR 或 Monado 驅動程式未連接。
settings-routing-output-badge-idle = 待機中
settings-routing-output-idle-description = 已啟用，但尚未找到遠端節點。
settings-routing-output-badge-off = 關閉
settings-routing-output-off-description = 在設定中已被關閉。
settings-routing-output-badge-empty = 沒有路由
settings-routing-output-empty-description = 已啟用，但沒有骨骼路由到這個輸出方法。
settings-routing-output-badge-unavailable = 無法使用
settings-routing-output-unavailable-description = 輸出方法不支援這個平台。
settings-routing-group-spine = 脊椎與軀幹
settings-routing-group-legs = 腿部
settings-routing-group-arms = 手臂
settings-routing-group-fingers = 手指
settings-routing-row-left_fingers = 左手手指
settings-routing-row-right_fingers = 右手手指
settings-routing-group-toes = 腳趾
settings-routing-row-left_toes = 左腳腳趾
settings-routing-row-right_toes = 右腳腳趾
settings-routing-hands-warning =
    <b>警告：</b>將手部追蹤器路由到 SteamVR/Monado 將會取代原本的控制器輸入。
    僅在需要用 SlimeVR 追蹤器追蹤手部時使用。
    
    仍要路由手部追蹤器到 SteamVR/Monado？
settings-routing-hands-warning-cancel = 取消
settings-routing-hands-warning-done = 路由手部追蹤器

## SteamVR / Monado output settings

settings-driver = SteamVR / Monado
settings-driver-description = SlimeVR 驅動程式的設定，用於 SteamVR 與 Monado 等。
settings-driver-enable = 啟用
settings-driver-enable-description = 透過 SlimeVR 驅動程式傳送追蹤器資訊到 SteamVR 或 Monado。關閉時上述環境無法接收 SlimeVR 的追蹤資訊。
settings-driver-enable-label = 啟用 SteamVR / Monado
settings-driver-status-title = 狀態
settings-driver-status-connection = 驅動程式連接狀態
settings-driver-status-badge-connected = 已連接
settings-driver-status-badge-waiting = 等待中
settings-driver-status-badge-disabled = 關閉
settings-driver-status-badge-unavailable = 無法使用
settings-driver-status-connection-connected = 驅動程式已連接並接收追蹤器資訊。
settings-driver-status-connection-waiting = 等待 SteamVR/Monado 透過 SlimeVR 驅動程式連接追蹤器。
settings-driver-status-connection-disabled = 已關閉，不使用此方法連接。
settings-driver-status-connection-unavailable = 輸出方法在此平台上無法使用。
settings-driver-bones = 傳送的骨骼
settings-driver-bones-description = 在骨骼路由中，哪些骨骼會以此方法輸出。
settings-driver-bones-link = 開啟骨骼路由
settings-driver-velocity = 傳送速度
settings-driver-velocity-description = 傳送直線速度與角速度到驅動程式，以允許 SteamVR/Monado 預測姿勢，與保持支援此資料類型的應用程式的相容性。

## Tracker mechanics

settings-general-trackers_settings = 追蹤器設定
settings-general-tracker_mechanics-filtering = 濾波
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    選擇追蹤器的濾波類型。
    預測型將會對你的運動加以預測，而平滑型將會讓你的運動更加平滑。
settings-general-tracker_mechanics-filtering-type = 濾波類型
settings-general-tracker_mechanics-filtering-type-none = 不進行濾波
settings-general-tracker_mechanics-filtering-type-none-description = 使用原始資料，不進行濾波。
settings-general-tracker_mechanics-filtering-type-smoothing = 平滑型
settings-general-tracker_mechanics-filtering-type-smoothing-description = 讓運動更加平滑，但會增加一些延遲。
settings-general-tracker_mechanics-filtering-type-prediction = 預測型
settings-general-tracker_mechanics-filtering-type-prediction-description = 減少延遲並使移動更敏捷，但可能會增加一些抖動。
settings-general-tracker_mechanics-filtering-amount = 濾波強度
settings-general-tracker_mechanics-yaw-reset-smooth-time = 左右偏擺重置平滑過渡時間
settings-general-tracker_mechanics-yaw-reset-smooth-time-description = 進行左右偏移重置時，平滑化追蹤器的旋轉角度。0 秒會關閉此選項。
settings-general-tracker_mechanics-save_mounting_reset = 儲存自動配戴重置的校正
settings-general-tracker_mechanics-save_mounting_reset-description =
    儲存自動配戴重置的校正，重新啟動 SlimeVR 後不需要再進行校正。
    本設定適用於動捕服，因為多次穿戴後追蹤器的位置不會變化。<b>不建議一般使用者使用！</b>
settings-general-tracker_mechanics-save_mounting_reset-enabled-label = 儲存自動配戴重置的校正
settings-general-tracker_mechanics-use_mag_on_all_trackers = 在有磁力計支援的 IMU 追蹤器上啟用磁力計
settings-general-tracker_mechanics-use_mag_on_all_trackers-description =
    在所有有韌體支援的追蹤器上使用磁力計，在磁場穩定的環境中可以減緩偏移。
    開啟此選項後，可以個別在追蹤器選項內停用磁力計。<b>切換此選項時請勿關閉任何一個追蹤器的電源！</b>
settings-general-tracker_mechanics-use_mag_on_all_trackers-label = 在追蹤器上啟用磁力計
settings-general-tracker_mechanics-trackers_over_usb = 透過 USB 連接的追蹤器
settings-general-tracker_mechanics-trackers_over_usb-description = 透過 USB 接收 HID 追蹤器的資料，請確保連接的追蹤器已啟用<b>「透過 HID 連接」</b>的功能。
settings-general-tracker_mechanics-trackers_over_usb-enabled-label = 允許 HID 追蹤器透過 USB 直接連接
settings-general-tracker_mechanics-timeout_delay = 逾時延遲
settings-general-tracker_mechanics-timeout_delay-description = 追蹤器離線時，標記成「{ tracker-status-disconnected }」的時間。
settings-stay_aligned = 持續校正
settings-stay_aligned-description = 持續校正功能會逐漸調整追蹤器以對齊到設定的放鬆姿態，進而減少追蹤器偏移的影響。
settings-stay_aligned-setup-label = 設定持續校正
settings-stay_aligned-setup-description = 完成「設定持續校正」後，才可啟用持續校正功能。
settings-stay_aligned-enabled-label = 調整追蹤器
settings-stay_aligned-general-label = 一般設定
settings-stay_aligned-relaxed_poses-label = 放鬆的姿態
settings-stay_aligned-relaxed_poses-description = 持續校正功能會使用各種放鬆的姿態保持追蹤器的定位。請使用「設定持續校正」來更新設定的姿態。
settings-stay_aligned-relaxed_poses-standing = 使用站立姿勢調整追蹤器
settings-stay_aligned-relaxed_poses-sitting = 使用坐在椅子上的姿勢調整追蹤器
settings-stay_aligned-relaxed_poses-flat = 使用坐在地板上或躺下的姿勢調整追蹤器
settings-stay_aligned-relaxed_poses-save_pose = 儲存姿勢
settings-stay_aligned-relaxed_poses-reset_pose = 重置姿勢
settings-stay_aligned-relaxed_poses-close = 關閉
settings-stay_aligned-debug-label = 除錯資訊
settings-stay_aligned-debug-description = 在回報與持續校正相關的問題時，請附上以下設定參數。
settings-stay_aligned-debug-copy-label = 複製設定參數進剪貼簿

## Keybinds Page

settings-keybinds = 快捷鍵設定
settings-keybinds-description = 變更不同捷徑的快捷鍵
keybind_config-keybind_name = 快捷鍵
keybind_config-keybind_value = 組合鍵
keybind_config-keybind_delay = 觸發延遲秒數
settings-keybinds_full-reset = 完整重置
settings-keybinds_yaw-reset = 左右偏擺重置
settings-keybinds_mounting-reset = 配戴重置
settings-keybinds_feet-mounting-reset = 腳部配戴重置
settings-keybinds_pause-tracking = 暫停追蹤
settings-keybinds_reset-all-button = 重置全部設定
settings-keybinds-system-managed-description = 你的桌面環境會管理全域快捷鍵，因此需要從系統設定中更改。
settings-keybinds-open-system-settings-button = 開啟系統設定
settings-keybinds-system-managed-hint = 在系統設定中設定
settings-keybinds-unsupported-description = 此平台不支援全域快捷鍵。
settings-sidebar-keybinds = 快捷鍵
settings-keybinds-recorder-modal-title = 指定快捷鍵給
settings-keybinds-recorder-modal-unbind-button = 取消設定
settings-keybinds-recorder-modal-done-button = 完成
settings-keybinds-recorder-modal-cancel-button = 取消
settings-keybinds-recorder-modal-key-enter = Enter
settings-keybinds-recorder-modal-key-backspace = Backspace
settings-keybinds-recorder-modal-key-escape = Esc
settings-keybinds-already-assigned = 已被 { $name } 使用
settings-keybinds-click-to-record = 點擊以錄製快捷鍵
settings-keybinds-change-shortcut = 點擊以變更快捷鍵
settings-keybinds-reset-single = 重置快捷鍵回預設設定
settings-keybinds-recorder-hint-recording = 請按住要設定的鍵盤按鍵後放開
settings-keybinds-error-unsupported-key = 該按鍵無法設定為快捷鍵
settings-keybinds-error-add-modifier = 設定時請同時按住 Ctrl、Alt 或 Super

## FK/Tracking settings

settings-general-fk_settings = 追蹤設定
# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = 地板限制
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = 腳滑補正
settings-general-fk_settings-leg_tweak-toe_snap = 腳趾跟地
settings-general-fk_settings-leg_tweak-foot_plant = 腳底貼地
settings-general-fk_settings-leg_tweak-skating_correction-amount = 腳滑補正量
settings-general-fk_settings-leg_tweak-skating_correction-description = 腳滑補正功能可以矯正腳滑溜冰的問題，但會降低某些動作的準確度。啟用本功能前請進行完整重置，並在遊戲內進行校正。
settings-general-fk_settings-leg_tweak-floor_clip-description = 地板限制功能可以減輕甚至解決腳部穿入地板的情況。啟用本功能前請進行完整重置，並在遊戲內進行校正。
settings-general-fk_settings-leg_tweak-toe_snap-description = 腳趾跟地功能在沒有腳部的追蹤器時，會嘗試猜測腳掌的旋轉角度。
settings-general-fk_settings-leg_tweak-foot_plant-description = 腳底貼地功能會在腳底與地面接觸時，將腳部旋轉成與地板平行。
settings-general-fk_settings-leg_fk = 腿部追蹤
settings-general-fk_settings-leg_fk-reset_mounting_feet-v1 = 強制重置腳部配戴
settings-general-fk_settings-leg_fk-reset_mounting_feet-description-v1 = 使用普通的配戴重置時，強制重置腳部配戴。
settings-general-fk_settings-enforce_joint_constraints = 骨架限制
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints = 約束關節旋轉
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = 避免關節旋轉超出極限
settings-general-fk_settings-ik = 定位資料
settings-general-fk_settings-ik-use_position = 使用定位資料
settings-general-fk_settings-ik-use_position-description = 若追蹤器支援定位，使用來自追蹤器的定位資料。啟用後請再次進行完整重置並在遊戲中重新校正追蹤器。
settings-general-fk_settings-resets_settings = 重置設定
settings-general-fk_settings-reset_settings-reset_reliable_reference_attitude = 重置頭戴顯示器上下旋轉
settings-general-fk_settings-reset_settings-reset_reliable_reference_attitude-description = 進行重置時將頭戴顯示器的上下旋轉一併重置，若將頭戴顯示器戴在額頭以進行虛擬直播或動作捕捉時可以使用，進行 VR 時請勿使用。
settings-general-fk_settings-arm_fk-reset_mode = 手臂重置模式
settings-general-fk_settings-arm_fk-reset_mode-description = 更改配戴重置時，手臂需要做出的姿勢。
settings-general-fk_settings-arm_fk-back = 向後彎折
settings-general-fk_settings-arm_fk-back-description = 預設模式，重置時手肘朝後，前臂向前，類似滑雪。
settings-general-fk_settings-arm_fk-tpose_up = T-pose（抬起）
settings-general-fk_settings-arm_fk-tpose_up-description = 完整重置時手臂向下，呈立正姿勢；配戴重置時手臂向兩側伸平。
settings-general-fk_settings-arm_fk-tpose_down = T-pose（放下）
settings-general-fk_settings-arm_fk-tpose_down-description = 完整重置時手臂向兩側伸平；配戴重置時手臂向下，呈立正姿勢。
settings-general-fk_settings-arm_fk-forward = 向前伸平
settings-general-fk_settings-arm_fk-forward-description = 重置時手臂向前伸平，有利於坐姿進行虛擬直播。
settings-general-fk_settings-skeleton_settings-ratios = 骨架比例
settings-general-fk_settings-skeleton_settings-ratios-description = 修改骨架設定的參數，你可能需要在修改後調整軀幹比例。
settings-general-fk_settings-skeleton_settings-impute_spine_from_upper_to_lower = 下脊椎無追蹤器時，從上脊椎推測其餘的脊椎追蹤器位置
settings-general-fk_settings-skeleton_settings-impute_spine_curvature = 脊椎曲度
settings-general-fk_settings-skeleton_settings-interpolate_hip_with_upper_legs = 從上腿部推測臀部追蹤器的位置
settings-general-fk_settings-skeleton_settings-interpolate_upper_legs_twist_with_lower_legs = 從上腿部的旋轉推測下腿部的旋轉
settings-general-fk_settings-self_localization-title = 動作捕捉模式
settings-general-fk_settings-self_localization-description = 動作捕捉模式允許在沒有頭戴顯示器或其他追蹤器時，粗略的追蹤身體骨架的定位。請注意，本功能需要腳部與頭部的追蹤器，並且本功能仍在實驗階段。

## Gesture control settings (tracker tapping)

settings-general-gesture_control-subtitle = 敲擊重置
settings-general-gesture_control-description = 使用敲擊追蹤器的方法觸發重置。敲擊軀幹所配戴的最高的追蹤器會啟用左右偏擺重置，敲擊左腳配戴最高的追蹤器會觸發完整重置，敲擊右腳配戴最高的追蹤器會觸發配戴重置。請注意，需要在 0.3 秒內滿足敲擊次數才會觸發。
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
       *[other] { $amount } 次敲擊
    }
# This is a unit: 3 trackers, 2 trackers, 1 tracker
# $amount (Number) - Amount of trackers
settings-general-gesture_control-trackers = { $amount } 個追蹤器
settings-general-gesture_control-yawResetEnabled = 敲擊以左右偏擺重置
settings-general-gesture_control-yawResetDelay = 左右偏擺重置延遲
settings-general-gesture_control-yawResetTaps = 左右偏擺重置敲擊次數
settings-general-gesture_control-yawResetTracker = 左右偏擺重置的追蹤器
settings-general-gesture_control-fullResetEnabled = 敲擊以完整重置
settings-general-gesture_control-fullResetDelay = 完整重置延遲
settings-general-gesture_control-fullResetTaps = 完整重置敲擊次數
settings-general-gesture_control-fullResetTracker = 完整重置的追蹤器
settings-general-gesture_control-mountingResetEnabled = 敲擊以配戴重置
settings-general-gesture_control-mountingResetDelay = 重置配戴延遲
settings-general-gesture_control-mountingResetTaps = 重置配戴敲擊次數
settings-general-gesture_control-mountingResetTracker = 配戴重置的追蹤器
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = 觸發敲擊判定的最多追蹤器數量
settings-general-gesture_control-numberTrackersOverThreshold-description = 如果敲擊偵測無法作動，請嘗試增加此值以降低敲擊判定的門檻。為避免誤判，請勿設定超過所需要的數值。

## Appearance settings

settings-interface-appearance = 外觀
settings-general-interface-dev_mode = 開發者模式
settings-general-interface-dev_mode-description = 本功能會提供更深入的資料，也能與已連線的追蹤器進行更進一步的控制。
settings-general-interface-dev_mode-label = 開發者模式
settings-general-interface-theme = 佈景主題色彩
settings-general-interface-lang = 選擇語言
settings-general-interface-lang-description = 更改所使用的介面語言。
settings-general-interface-lang-placeholder = 選擇要使用的語言
# Keep the font name untranslated
settings-interface-appearance-font = UI 字體
settings-interface-appearance-font-description = 本設定會修改 UI 所使用的字體。
settings-interface-appearance-font-placeholder = 預設字體
settings-interface-appearance-font-os_font = 作業系統字體
settings-interface-appearance-font-slime_font = 預設字體
settings-interface-appearance-font_size = 文字縮放
settings-interface-appearance-font_size-description = 本設定會影響整個 UI 的文字大小，除了本設定面板以外。

## Notification settings

settings-interface-notifications = 通知
settings-general-interface-feedback_sound = 聲音回饋
settings-general-interface-feedback_sound-description = 啟用本選項後，觸發重置時會發出提示音。
settings-general-interface-feedback_sound-label = 聲音回饋
settings-general-interface-feedback_sound-volume = 聲音回饋音量
settings-general-interface-connected_trackers_warning = 已連接追蹤器警告
settings-general-interface-connected_trackers_warning-description = 啟用本選項後，每次當退出 SlimeVR 時仍有追蹤器連接著會顯示通知，提醒你在使用完畢時關閉追蹤器電源來節省電池電量。
settings-general-interface-connected_trackers_warning-label = 當退出程式時，有追蹤器連接中則顯示警告

## Behavior settings

settings-interface-behavior = 行為
settings-general-interface-dev_mode = 開發者模式
settings-general-interface-dev_mode-description = 本功能會提供更深入的資料，也能在需要與連接中的追蹤器有進階的操作時使用。
settings-general-interface-dev_mode-label = 開發者模式
settings-general-interface-use_tray = 最小化到系統列
settings-general-interface-use_tray-description = 本選項可以讓你在關閉視窗時不會關閉 SlimeVR 的伺服器程式，讓你在不受圖形介面的打擾下繼續使用追蹤器。
settings-general-interface-use_tray-label = 最小化到系統列
settings-general-interface-discord_presence = 在 Discord 上分享活動
settings-general-interface-discord_presence-description = 在 Discord 上顯示你正在使用 SlimeVR，以及使用中的追蹤器的數量。
settings-general-interface-discord_presence-label = 在 Discord 上分享活動
settings-general-interface-discord_presence-message =
    { $amount ->
        [0] 正在捕捉史萊姆
       *[other] 正在使用 { $amount } 個追蹤器
    }
settings-interface-behavior-error_tracking = 透過 Sentry.io 收集錯誤資訊
settings-interface-behavior-error_tracking-description_v2 =
    <h1>你是否同意我們蒐集匿名化的錯誤資料？</h1>
    
    <b>我們不會收集個人資訊</b> ，例如你的 IP 位址或無線網路認證資訊。SlimeVR 重視你的隱私！
    
    為了提供最佳的使用者體驗，我們會蒐集匿名化的錯誤報告、性能指標和作業系統資訊，這會對我們檢測 SlimeVR 的錯誤和問題有所幫助。我們會透過 Sentry.io 來蒐集這些指標。
settings-interface-behavior-error_tracking-label = 向開發者傳送錯誤資訊
settings-interface-behavior-bvh_directory = BVH 紀錄儲存目錄
settings-interface-behavior-bvh_directory-description = 選擇儲存 BVH 紀錄文件的目錄，如此每次錄製 BVH 時不需要選擇儲存位置。
settings-interface-behavior-bvh_directory-label = 存放 BVH 紀錄的目錄
settings-interface-behavior-skeleton_mesh = 骨骼預覽樣式
settings-interface-behavior-skeleton_mesh-description = 繪製預覽圖時以 3D 造形取代線條，低階機器請關閉此選項。
settings-interface-behavior-skeleton_mesh-label = 使用 3D 造形
settings-interface-behavior-controller_nav = 遊戲控制器操作
settings-interface-behavior-controller_nav-description = 使用遊戲控制器選擇項目操作，十字鍵或左搖桿移動游標，A 鍵選擇，B 鍵返回，只有在連接控制器時才會使用。按住 Ctrl 或是 Alt 加上鍵盤方向鍵會以相同方式移動游標。
settings-interface-behavior-controller_nav-label = 使用遊戲控制器操作

## Serial settings

settings-serial = 序列埠終端
# This cares about multilines
settings-serial-description = 這裡用於顯示序列埠的即時資訊，可能有助於瞭解韌體是否發生問題。
settings-serial-connection_lost = 序列埠連線中斷，正在重新連線……
settings-serial-busy = 該埠目前被韌體更新占用，終端機將會在韌體更新完成後恢復連線
settings-serial-opening = 開啟序列埠中……
settings-serial-open_failed = 無法開啟序列埠，請檢查沒有其他應用程式正在占用，並確認你持有存取序列埠裝置的權限。
settings-serial-no_port = 偵測不到序列埠裝置
settings-serial-reboot = 重新啟動
settings-serial-factory_reset = 恢復出廠設定
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>警告：</b>本選項會將該追蹤器恢復出廠設定，
    亦即其 Wi-Fi 與追蹤器校正的設定<b>將會全部刪除</b>。
settings-serial-factory_reset-warning-ok = 我已瞭解以上風險
settings-serial-factory_reset-warning-cancel = 取消
settings-serial-serial_select = 選擇序列埠
settings-serial-get_wifi_scan = 取得 Wi-Fi 掃描
settings-serial-enter_pairing = 進入配對
settings-serial-exit_pairing = 離開配對
settings-serial-calibrate = 校正
settings-serial-six_side_calibrate = 六面校正
settings-serial-dfu = 進入 DFU 模式
settings-serial-meow = 喵～！
settings-serial-save_logs = 儲存到檔案
settings-serial-send_command = 傳送
settings-serial-send_command-placeholder = 輸入指令…
settings-serial-send_command-warning = <b>警告：</b>執行序列埠指令可能會導致資料遺失或追蹤器變磚。
settings-serial-send_command-warning-ok = 我已瞭解以上風險
settings-serial-send_command-warning-cancel = 取消

## OSC VRChat settings

settings-osc-vrchat = VRChat OSC 追蹤器
# This cares about multilines
settings-osc-vrchat-description-v1 =
    變更 OSC 追蹤器標準的設定，該標準可用於傳送追蹤器資料到不使用 SteamVR 的應用程式（例如 Quest 單機版）。
    請確保 VRChat 中的動作選單內，OSC 設定「選項→OSC→已啟用」已經開啟。
settings-osc-vrchat-enable = 啟用
settings-osc-vrchat-enable-description = 切換資料的傳送和接收。
settings-osc-vrchat-enable-label = 啟用
settings-osc-vrchat-network = 連接埠
settings-osc-vrchat-network-port_in =
    .label = 輸入埠
    .placeholder = 輸入埠（預設：9001）
settings-osc-vrchat-network-port_out =
    .label = 輸出埠
    .placeholder = 輸出埠（預設：9000）
settings-osc-vrchat-network-address = 網路位址
settings-osc-vrchat-network-address-description-v1 = 設定收發追蹤器資料的 IP 位址，使用 VRChat 不須更改。
settings-osc-vrchat-network-address-placeholder = VRChat IP 位址

## VRChat OSC status

settings-osc-vrchat-status-title = 狀態
settings-osc-vrchat-status-input = 輸入
settings-osc-vrchat-status-tracking = 旋轉
settings-osc-vrchat-status-output = 輸出
settings-osc-vrchat-status-oscquery = OSCQuery
settings-osc-vrchat-status-input-idle = 尚未監聽
settings-osc-vrchat-status-input-listening = 在 { $port } 埠上監聽
settings-osc-vrchat-status-input-last-data = 最後從 VRChat 接收資料：{ $elapsed }。
settings-osc-vrchat-status-input-no-data = 尚未從 VRChat 接收資料。
settings-osc-vrchat-status-tracking-received = 最後接收姿勢：{ $elapsed }。
settings-osc-vrchat-status-tracking-disabled =
    VRChat 已連接，但沒有傳送頭部與手腕追蹤資料。請在 VRChat 設定中開啟 OSC 追蹤資料。
    <OscTrackingLink>開啟方法看這裡。</OscTrackingLink>
settings-osc-vrchat-status-tracking-unknown = 正在等待 VRChat 連接。
settings-osc-vrchat-status-output-idle = 無目標
settings-osc-vrchat-status-output-waiting = 目標 { $address }:{ $port } ({ $source })，未傳送幀資料
settings-osc-vrchat-status-output-sending = 正在傳送資料到 { $address }:{ $port } ({ $source })
settings-osc-vrchat-status-output-target = 目標 { $address }:{ $port } ({ $source })
settings-osc-vrchat-status-output-last-frame = 最後傳送幀資料：{ $elapsed }
settings-osc-vrchat-status-output-no-frame = 尚未傳送幀資料
settings-osc-vrchat-status-source-manual = 手動
settings-osc-vrchat-status-source-auto = 自動偵測
settings-osc-vrchat-status-oscquery-disabled = OSCQuery 已關閉（手動網路模式）
settings-osc-vrchat-status-oscquery-advertising = 在 { $port } 埠廣播
settings-osc-vrchat-status-oscquery-searching = 尚未找到 VRChat 客戶端。
settings-osc-vrchat-status-oscquery-discovered-title = 已發現的 VRChat 客戶端：
settings-osc-vrchat-status-oscquery-switch = 切換
settings-osc-vrchat-status-network-mode = 網路模式
settings-osc-vrchat-status-network-mode-description = 自動模式會透過 OSCQuery 尋找 VRChat 客戶端，手動模式會使用已設定的位址與埠號。
settings-osc-vrchat-status-network-mode-toggle = 手動網路設定
settings-osc-vrchat-status-network-manual-description = 手動設定本地端的輸入埠號與 VRChat OSC 目標。
settings-osc-vrchat-status-badge-idle = 閒置
settings-osc-vrchat-status-badge-listening = 監聽中
settings-osc-vrchat-status-badge-ready = 就緒
settings-osc-vrchat-status-badge-found = 已找到
settings-osc-vrchat-status-badge-searching = 搜尋中
settings-osc-vrchat-status-badge-disabled = 停用
settings-osc-vrchat-status-badge-error = 錯誤
settings-osc-vrchat-status-badge-received = 已接收
settings-osc-vrchat-status-badge-not-sent = 未傳送
settings-osc-vrchat-status-badge-unknown = 不明

## VMC OSC settings

settings-osc-vmc = Virtual Motion Capture
# This cares about multilines
settings-osc-vmc-description =
    修改 VMC (Virtual Motion Capture) 協定的相關設定，
    以傳送 SlimeVR 的骨骼資料，並接收來自其他應用程式的骨骼資料。
settings-osc-vmc-enable = 啟用
settings-osc-vmc-enable-description = 切換資料的傳送和接收。
settings-osc-vmc-enable-label = 啟用
settings-osc-vmc-network = 連接埠
settings-osc-vmc-network-description = 設定用於監聽和傳送 VMC 資料的連接埠。
settings-osc-vmc-network-port_in =
    .label = 輸入埠
    .placeholder = 輸入埠（預設：39540）
settings-osc-vmc-network-port_out =
    .label = 輸出埠
    .placeholder = 輸出埠（預設：39539）
settings-osc-vmc-network-address = 網路地址
settings-osc-vmc-network-address-description = 設定用來發送 VMC 資料的位址。
settings-osc-vmc-network-address-placeholder = IPV4 地址
settings-osc-vmc-vrm = VRM 模型
settings-osc-vmc-vrm-description = 載入 VRM 模型以允許頭部錨定，並與其他應用程式有更高的相容性。
settings-osc-vmc-vrm-untitled_model = 未命名模型
settings-osc-vmc-vrm-file_select = 拖曳檔案或 <u>瀏覽檔案</u> 以載入模型
settings-osc-vmc-anchor_hip = 臀部錨定
settings-osc-vmc-anchor_hip-description = 將追蹤錨定在臀部，有利於坐姿進行虛擬直播。若本選項無法切換，請載入 VRM 模型。
settings-osc-vmc-anchor_hip-label = 臀部錨定
settings-osc-vmc-mirror_tracking = 鏡像追蹤
settings-osc-vmc-mirror_tracking-description = 將追蹤的結果水平鏡像。
settings-osc-vmc-mirror_tracking-label = 鏡像追蹤
settings-osc-vmc-status-title = 狀態
settings-osc-vmc-status-input = 輸入
settings-osc-vmc-status-output = 輸出
settings-osc-vmc-status-vrm = VRM 模型
settings-osc-vmc-status-input-idle = 未監聽
settings-osc-vmc-status-input-listening = 在 { $port } 埠監聽中
settings-osc-vmc-status-input-last-data = 最後接收資料：{ $elapsed }。
settings-osc-vmc-status-input-no-data = 尚未收到資料。
settings-osc-vmc-status-output-idle = 無目標
settings-osc-vmc-status-output-waiting = 目標 { $address }:{ $port }，未傳送幀資料
settings-osc-vmc-status-output-sending = 正在傳送資料到 { $address }:{ $port }
settings-osc-vmc-status-output-target = 目標 { $address }:{ $port }
settings-osc-vmc-status-output-last-frame = 最後傳送幀資料：{ $elapsed }。
settings-osc-vmc-status-output-no-frame = 尚未傳送幀資料。
settings-osc-vmc-status-vrm-none = 沒有載入模型，使用推測的骨骼位移。
settings-osc-vmc-status-vrm-loaded = 已載入模型，使用模型的骨骼位移。
settings-osc-vmc-status-badge-idle = 閒置
settings-osc-vmc-status-badge-listening = 監聽中
settings-osc-vmc-status-badge-ready = 就緒
settings-osc-vmc-status-badge-disabled = 停用
settings-osc-vmc-status-badge-error = 錯誤

## Common OSC settings

settings-osc-common-network-ports_match_error-v2 = 輸入埠與輸出埠不能相同！
settings-osc-common-network-port_banned_error = 無法使用 { $port } 連接埠！

## Advanced settings

settings-utils-advanced = 進階
settings-utils-advanced-reset-gui = 重置 UI 設定
settings-utils-advanced-reset-gui-description = 將 UI 設定恢復成預設值。
settings-utils-advanced-reset-gui-label = 重置 UI 設定
settings-utils-advanced-reset-server = 重置追蹤設定
settings-utils-advanced-reset-server-description = 將追蹤設定恢復成預設值。
settings-utils-advanced-reset-server-label = 重置追蹤設定
settings-utils-advanced-reset-all = 重置全部設定
settings-utils-advanced-reset-all-description = 將 UI 與追蹤設定恢復成預設值。
settings-utils-advanced-reset-all-label = 重置全部設定
settings-utils-advanced-reset_warning =
    { $type ->
        [gui]
            <b>警告：</b> 這會將 UI 設定重置為預設值。
            你確定要執行此操作嗎？
        [server]
            <b>警告：</b> 這會將追蹤設定重置為預設值。
            你確定要執行此操作嗎？
       *[all]
            <b>警告：</b> 這會將所有設定重置為預設值。
            你確定要執行此操作嗎？
    }
settings-utils-advanced-reset_warning-reset = 重置設定
settings-utils-advanced-reset_warning-cancel = 取消
settings-utils-advanced-open_data-v1 = 設定資料夾
settings-utils-advanced-open_data-description-v1 = 在檔案管理器中開啟 SlimeVR 的設定資料夾，該資料夾包含程式的設定。
settings-utils-advanced-open_data-label = 打開資料夾
settings-utils-advanced-open_logs = 紀錄檔資料夾
settings-utils-advanced-open_logs-description = 在檔案管理器中開啟 SlimeVR 的紀錄檔資料夾，該資料夾包含程式的紀錄檔。
settings-utils-advanced-open_logs-label = 打開資料夾

## Home Screen

settings-home-list-layout = 追蹤器清單檢視方式
settings-home-list-layout-desc = 請從以下選項選擇一個主畫面的檢視方式
settings-home-list-layout-grid = 格狀
settings-home-list-layout-table = 表格
settings-home-table_columns = 表格欄位
settings-home-table_columns-desc = 選擇哪些欄位要顯示在追蹤器表格上
settings-home-tracker_display = 追蹤器資訊
settings-home-tracker_display-desc = 選擇哪些額外資訊要顯示在追蹤器上
settings-home-tracker_display-battery_voltage = 電池電壓
settings-home-tracker_display-numeric_signal = 訊號強度數值

## Tracking Checklist

settings-tracking_checklist-active_steps = 列出的追蹤清單項目
settings-tracking_checklist-active_steps-desc = 列出所有會在追蹤清單中顯示的步驟，你可以停用或啟用可忽略的步驟。

## Setup/onboarding menu

onboarding-skip = 跳過設定
onboarding-continue = 繼續
onboarding-previous_step = 上一步
onboarding-setup_warning =
    <b>警告：</b>若要有良好的追蹤效果，必須進行初始設定，
    若是第一次使用 SlimeVR，請繼續進行設定。
onboarding-setup_warning-skip = 跳過設定
onboarding-setup_warning-cancel = 繼續設定

## Quiz

onboarding-quiz_continue = 繼續
onboarding-quiz_back = 向後彎折
onboarding-quiz-more_sets_modal-title = 你連接完所有的追蹤器了嗎？
onboarding-quiz-more_sets_modal-desc = 如果你有別的型號的追蹤器套件，此時可以一併設定！
onboarding-quiz-more_sets_modal-confirm = 所有追蹤器皆已連接
onboarding-quiz-more_sets_modal-cancel = 我有其他追蹤器要連接
onboarding-quiz-slimeset-title = 你要連接的追蹤器為哪種形式？
onboarding-quiz-slimeset-description = 如果你有多個追蹤器套件，本次流程後會再次詢問
onboarding-quiz-slimeset-official-sets = 官方的 SlimeVR 追蹤器
onboarding-quiz-slimeset-thirdparty-sets = 第三方或是 DIY 追蹤器
onboarding-quiz-slimeset-answer-regular = SlimeVR V1.0 & V1.2
onboarding-quiz-slimeset-answer-butterfly = Butterfly
onboarding-quiz-slimeset-answer-wifi = 使用 Wi-Fi 的 Slime 追蹤器
onboarding-quiz-slimeset-answer-dongle = 使用接收器的 Slime 追蹤器
onboarding-quiz-usage-title = 你使用追蹤器的用途為何？
onboarding-quiz-usage-description = 如果你計畫將 SlimeVR 用於多個用途，稍後可以在相關設定中調整。
onboarding-quiz-usage-answer-VRC = VR 遊戲（如 VRChat）
onboarding-quiz-usage-answer-mocap_vtubing = 動作捕捉與虛擬直播
onboarding-quiz-runtime-title = 遊玩遊戲時會透過 SteamVR 或是在頭戴顯示器執行（單機）？
onboarding-quiz-runtime-answer-steamvr = SteamVR
onboarding-quiz-runtime-answer-standalone = 單機
onboarding-quiz-mocap_preferences-title = 動作捕捉偏好
onboarding-quiz-mocap_preferences-desc = 你將會怎麼使用 SlimeVR 於動作捕捉或虛擬直播上
onboarding-quiz-mocap_preferences-playspace-title = 你的活動空間為何？
onboarding-quiz-mocap_preferences-playspace-desc = 若為站姿，SlimeVR 會嘗試追蹤行走姿勢，不會將你維持在同一個位置。
onboarding-quiz-mocap_preferences-playspace-sitting = 坐姿
onboarding-quiz-mocap_preferences-playspace-standing = 站姿
onboarding-quiz-mocap_preferences-vrm_model-title = 你有 VRM 模型嗎？（可選用）
onboarding-quiz-mocap_preferences-vrm_model-desc = 載入 VRM 模型可以提昇追蹤品質，以及使用 VMC 協定的應用程式的相容性。
onboarding-quiz-mocap_preferences-head_tracker-title = 你的頭上有配戴追蹤器或是 VR 頭戴顯示器嗎？
onboarding-quiz-mocap_preferences-head_tracker-yes = 確定
onboarding-quiz-mocap_preferences-head_tracker-no = 沒有
onboarding-quiz-mocap_preferences-head_tracker_location-title = 頭部的追蹤器配戴在哪裡？
onboarding-quiz-mocap_preferences-head_tracker_location-forehead = 前額
onboarding-quiz-mocap_preferences-head_tracker_location-face = 臉上

## Wi-Fi setup

onboarding-wifi_creds-back-v2 = 返回
onboarding-wifi_creds-v2 = 透過 Wi-Fi 連接
# This cares about multilines
onboarding-wifi_creds-description-v2 =
    大多數的追蹤器（例如官方的 SlimeVR 追蹤器）使用 Wi-Fi 連接伺服器程式。
    請輸入目前設備連接的網路的 Wi-Fi 憑證。
    
    請確保輸入的是 2.4 GHz 頻道的 Wi-Fi 憑證。
onboarding-wifi_creds-continue = 以 Wi-Fi 繼續
onboarding-wifi_creds-submit = 送出！
onboarding-wifi_creds-retry = 重試
onboarding-wifi_creds-ssid-label = Wi-Fi 名稱
onboarding-wifi_creds-ssid =
    .placeholder = 請輸入 Wi-Fi 名稱
onboarding-wifi_creds-ssid-required = 必須填寫 Wi-Fi 名稱
onboarding-wifi_creds-ssid-scan =
    .placeholder = 選擇 Wi-Fi 網路
onboarding-wifi_creds-network_band_tip = 追蹤器只能使用 2.4GHz 的 Wi-Fi 網路。如果你的網路不在列表當中，請確保你的 Wi-Fi AP 有開啟 2.4GHz 頻段。
onboarding-wifi_creds-scan_idle = 為了掃描附近的 Wi-Fi 網路，請使用 USB 連接一個追蹤器。
onboarding-wifi_creds-scanning = 正在掃描 Wi-Fi 網路……
onboarding-wifi_creds-scan_unsupported = 此追蹤器的韌體尚未支援掃描 Wi-Fi 網路，請手動輸入網路名稱。
onboarding-wifi_creds-scan_failed = 追蹤器上進行 Wi-Fi 掃描失敗。
onboarding-wifi_creds-scan_error_no_device = USB 上找不到追蹤器以進行 Wi-Fi 網路掃描。
onboarding-wifi_creds-scan_error_no_logs = 無法在進行 Wi-Fi 掃描時，從追蹤器讀取序列埠輸出。
onboarding-wifi_creds-rescan = 重新掃描
onboarding-wifi_creds-enter_manually = 看不到你的網路嗎？進行手動輸入
onboarding-wifi_creds-use_scanned = 從已掃描的網路中選擇
onboarding-wifi_creds-password =
    .label = 密碼
    .placeholder = 輸入密碼
onboarding-wifi_creds-dongle-title = 透過接收器連接
onboarding-wifi_creds-dongle-description = 如果你的追蹤器有接收器，將其插入你的裝置即可開始使用。
onboarding-wifi_creds-dongle-wip = 本部分目前仍在開發階段，將來會推出管理接收器連接追蹤器的專屬頁面。
onboarding-wifi_creds-dongle-continue = 使用接收器繼續

## Install info

install-info_udev-rules_modal_title = 找不到硬體的 udev 存取規則
install-info_udev-rules_warning = 需要加入 udev 的存取規則才能存取追蹤器的序列埠以及連接接收器。請將以下指令複製貼上進終端機以新增 udev 存取規則。
install-info_udev-rules_modal_button = 關閉
install-info_udev-rules_modal-dont-show-again_checkbox = 不要再顯示

## Setup start

onboarding-home = 歡迎來到 SlimeVR
onboarding-home-start = 來開始設定吧！

## Tracker connection setup

onboarding-connect_tracker-title = 連接追蹤器
onboarding-connect_tracker-description = 請將 Wi-Fi 連接資訊輸入以下欄位，現在與之後透過 USB 連接的追蹤器將會自動使用以下憑證來連接 Wi-Fi。
onboarding-connect_tracker-learn_more = 了解更多資訊
onboarding-connect_tracker-issue-serial = 我在連接時碰到問題了！
onboarding-connect_tracker-usb = USB 追蹤器
onboarding-connect_tracker-tracker_mac_name = SlimeVR 追蹤器 ({ $suffix })
onboarding-connect_tracker-tracker_port_name = USB 追蹤器 ({ $port })
onboarding-connect_tracker-network_profile-ignore = 我已知曉，而且防火牆已配置好了
onboarding-connect_tracker-waiting_first_title = 正在等待第一個追蹤器
onboarding-connect_tracker-waiting_first_desc = 使用 USB 插入一個追蹤器以開始。
onboarding-connect_tracker-scan_results_title = 選擇網路並輸入密碼
onboarding-connect_tracker-scan_results_desc = 在左側輸入 Wi-Fi 憑證資訊以連接追蹤器。
onboarding-connect_tracker-all_caught_up = 設定完成，你可隨時連接更多追蹤器
onboarding-connect_tracker-shake_tip = 不知道手上的追蹤器是哪一個嗎？搖一搖它就會在清單中顯示出來。
onboarding-connect_tracker-close = 關閉
onboarding-connect_tracker-connection_status-serial_init = 正在連線到序列埠裝置
onboarding-connect_tracker-connection_status-obtaining_mac_address = 正在取得追蹤器的 MAC 位址
onboarding-connect_tracker-connection_status-provisioning = 正在傳送 Wi-Fi 認證資訊
onboarding-connect_tracker-connection_status-connecting = 正在傳送 Wi-Fi 資訊
onboarding-connect_tracker-connection_status-looking_for_server = 正在尋找伺服器
onboarding-connect_tracker-connection_status-connection_error = 無法連線到 Wi-Fi
onboarding-connect_tracker-connection_status-could_not_find_server = 未尋找到可用的伺服器
onboarding-connect_tracker-connection_status-done = 已連線到伺服器
onboarding-connect_tracker-connection_status-no_serial_log = 無法從追蹤器取得紀錄訊息
onboarding-connect_tracker-connection_status-no_serial_device_found = 無法透過 USB 找到追蹤器
onboarding-connect_tracker-connection_error-desc = 請確保 Wi-Fi 的 SSID 與密碼輸入正確，且 2.4GHz 網路已啟用。
onboarding-connect_tracker-could_not_find_server-desc = 追蹤器已連接到 Wi-Fi，但找不到本地網路的 SlimeVR 伺服器，請檢查防火牆設定。
onboarding-connect_serial-error-modal-no_serial_log = 追蹤器電源開了嗎？
onboarding-connect_serial-error-modal-no_serial_log-desc = 請確認追蹤器電源已開啟，並連接到這台電腦上
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers =
    { $amount ->
        [0] 沒有已連線的追蹤器
       *[other] { $amount } 個追蹤器已連線
    }
onboarding-connect_tracker-setting_up = 設定 ({ $amount })
onboarding-connect_tracker-next = 所有的追蹤器都連接好了

## Tracker assignment setup

onboarding-assign_trackers-title = 分配追蹤器
onboarding-assign_trackers-description = 這些追蹤器要放在身上的哪個部位呢？請點選要放置追蹤器的部位
onboarding-assign_trackers-reset_assignments = 重置所有分配
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned = { $assigned }/{ $trackers } 個追蹤器已分配
onboarding-assign_trackers-all_assigned-title = 全部的追蹤器已經分配！
onboarding-assign_trackers-all_assigned-description = 你已經準備好進行下一個步驟了
onboarding-assign_trackers-no_trackers-title = 沒有連接的追蹤器
onboarding-assign_trackers-no_trackers-description = 連接追蹤器以分配到身體部位
onboarding-assign_trackers-show_all = 顯示所有身體部位
onboarding-assign_trackers-show_all-short = 顯示所有部位
onboarding-assign_trackers-tap_modal-title = 點選以分配
onboarding-assign_trackers-tap_modal-description = 敲擊要分配的追蹤器兩下，或是點選對應的按鈕，此外也可以從清單拖曳追蹤器到身體部位上。
onboarding-assign_trackers-tap_modal-cancel = 取消
onboarding-assign_trackers-mobile-idle_title = 剩下 { $remaining } 個追蹤器
onboarding-assign_trackers-mobile-idle_hint = 選擇身體上的位置，或是開啟以下清單
onboarding-assign_trackers-mobile-idle_hint-open = 選擇一個追蹤器，然後選擇要指定身體位置
onboarding-assign_trackers-mobile-choose_tracker = 選擇 { $part } 的追蹤器
onboarding-assign_trackers-mobile-choose_tracker-hint = 或是在追蹤器上敲兩下
onboarding-assign_trackers-mobile-choose_tracker-current = 目前為 { $tracker }
onboarding-assign_trackers-mobile-choose_spot = 選擇 { $tracker } 的配戴位置
onboarding-assign_trackers-mobile-choose_spot-hint = 請在上方的身體選擇一個位置
onboarding-assign_trackers-mobile-unassign = 取消分配 { $part }
onboarding-assign_trackers-tab-body = 身體
onboarding-assign_trackers-tab-fingers = 手指
onboarding-assign_trackers-tab-toes = 腳趾
onboarding-assign_trackers-side-right = 右
onboarding-assign_trackers-side-left = 左
# Accessible name for the left/right pill radiogroup in the assignment panel header
onboarding-assign_trackers-side = 身體側邊
# Accessible name for the mirror-view pill toggle in the assignment panel header
onboarding-assign_trackers-mirror = 鏡像顯示
onboarding-assign_trackers-finger-thumb = 大拇指
onboarding-assign_trackers-finger-index = 食指
onboarding-assign_trackers-finger-middle = 中指
onboarding-assign_trackers-finger-ring = 無名指
onboarding-assign_trackers-finger-little = 小指
onboarding-assign_trackers-toes-middle = 第三腳趾
onboarding-assign_trackers-toe-big = 大腳趾
onboarding-assign_trackers-toe-index = 第二腳趾
onboarding-assign_trackers-toe-middle = 第三腳趾
onboarding-assign_trackers-toe-ring = 第四腳趾
onboarding-assign_trackers-toe-little = 小腳趾
onboarding-assign_trackers-joint-distal = 遠端指骨
onboarding-assign_trackers-joint-intermediate = 中端指骨
onboarding-assign_trackers-joint-proximal = 近端指骨
onboarding-assign_trackers-joint-metacarpal = 掌骨

## Tracker assignment warnings

# Note for devs, number is used for representing boolean states per bit.
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_FOOT =
    { $unassigned ->
        [0] 左腳已分配，但你還需要分配左腳踝、左大腿和胸部、臀部或腰部！
        [1] 左腳已分配，但你還需要分配左大腿和胸部、臀部或腰部！
        [2] 左腳已分配，但你還需要分配左腳踝和胸部、臀部或腰部！
        [3] 左腳已分配，但你還需要分配胸部、臀部或腰部！
        [4] 左腳已分配，但你還需要分配左腳踝和左大腿！
        [5] 左腳已分配，但你還需要分配左大腿！
        [6] 左腳已分配，但你還需要分配左腳踝！
       *[unknown] 左腳已分配，但你還需要分配其它未分配的身體部位！
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_FOOT =
    { $unassigned ->
        [0] 右腳已分配，但你還需要分配右腳踝、右大腿以及胸部、臀部或腰部！
        [1] 右腳已分配，但你還需要分配右大腿和胸部、臀部或腰部！
        [2] 右腳已分配，但你還需要分配右腳踝和胸部、臀部或腰部！
        [3] 右腳已分配，但你還需要分配胸部、臀部或腰部！
        [4] 右腳已分配，但你還需要分配右腳踝和右大腿！
        [5] 右腳已分配，但你還需要分配右大腿！
        [6] 右腳已分配，但你還需要分配右腳踝！
       *[unknown] 右腳已分配，但你還需要分配其它未分配的身體部位！
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_LOWER_LEG =
    { $unassigned ->
        [0] 左腳踝已分配，但你還需要分配左大腿和胸部、臀部或腰部！
        [1] 左腳踝已分配，但你還需要分配胸部、臀部或腰部！
        [2] 左腳踝已分配，但你還需要分配左大腿！
       *[unknown] 左腳踝已分配，但你還需要分配其它未分配的身體部位！
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_LOWER_LEG =
    { $unassigned ->
        [0] 右腳踝已分配，但你還需要分配右大腿和胸部、臀部或腰部！
        [1] 右腳踝已分配，但你還需要分配胸部、臀部或腰部！
        [2] 右腳踝已分配，但你還需要分配右大腿！
       *[unknown] 右腳踝已分配，但你還需要分配其它未分配的身體部位！
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_UPPER_LEG =
    { $unassigned ->
        [0] 左大腿已分配，但你還需要分配胸部、臀部或腰部！
       *[unknown] 左大腿已分配，但你還需要分配其它未分配的身體部位！
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_UPPER_LEG =
    { $unassigned ->
        [0] 右大腿已分配，但你還需要分配胸部、臀部或腰部！
       *[unknown] 右大腿已分配，但你還需要分配其它未分配的身體部位！
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-HIP =
    { $unassigned ->
        [0] 臀部已分配，但你還需要分配胸部！
       *[unknown] 臀部已分配，但你還需要分配其它未分配的身體部位！
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-WAIST =
    { $unassigned ->
        [0] 腰部已分配，但你還需要分配胸部！
       *[unknown] 腰部已分配，但你還需要分配其它未分配的身體部位！
    }

## Tracker mounting method choose

onboarding-choose_mounting = 要使用哪一種配戴校正方式？
# Multiline text
onboarding-choose_mounting-description = 配戴校正可以校正追蹤器放在身上的位置。
onboarding-choose_mounting-auto_mounting = 自動配戴校正
# Italicized text
onboarding-choose_mounting-auto_mounting-label-v2 = 推薦使用
onboarding-choose_mounting-auto_mounting-description = 本選項會透過兩個身體姿勢，判斷所有追蹤器的配戴方位
onboarding-choose_mounting-manual_mounting = 手動配戴校正
onboarding-choose_mounting-manual_mounting-description = 本選項可以讓你選擇每個追蹤器的配戴方位

## Tracker manual mounting setup

onboarding-manual_mounting = 手動配戴
onboarding-manual_mounting-description = 點選每個追蹤器並選擇它們的配戴方式
onboarding-manual_mounting-auto_mounting = 進行自動設定
onboarding-manual_mounting-next = 下一步

## Tracker automatic mounting setup

onboarding-automatic_mounting-title = 配戴校正
onboarding-automatic_mounting-description = 為了讓 SlimeVR 追蹤器正常運作，我們需要為每個追蹤器設定配戴方向，以符合你實際的追蹤器配戴方式。
onboarding-automatic_mounting-manual_mounting = 進行手動設定
onboarding-automatic_mounting-next = 下一步
onboarding-automatic_mounting-prev_step = 上一步
onboarding-automatic_mounting-done-title = 配戴方向已校正。
onboarding-automatic_mounting-done-description = 你的配戴方向校準完成！
onboarding-automatic_mounting-done-restart = 再試一次
onboarding-automatic_mounting-mounting_reset-title = 配戴重置
onboarding-automatic_mounting-mounting_reset-step-0 = 1. 雙腿彎曲以滑雪的姿勢蹲下，上身向前傾斜，手臂彎曲。
onboarding-automatic_mounting-mounting_reset-step-1 = 2. 按下「配戴重置」按鈕並等待 3 秒鐘，追蹤器的配戴方向將被重置。
onboarding-automatic_mounting-mounting_reset-feet-step-0 = 1. 以腳尖站立，雙腳朝前。你也能坐在椅子上進行。
onboarding-automatic_mounting-mounting_reset-feet-step-1 = 2. 按下「腳部校正」按鈕並等待 3 秒鐘，追蹤器的配戴方向將被重置。
onboarding-automatic_mounting-preparation-title = 準備
onboarding-automatic_mounting-preparation-v2-step-0 = 1. 請按下「完整重置」按鈕。
onboarding-automatic_mounting-preparation-v2-step-1 = 2. 站直，雙臂放在身體兩側，確保向前直視。
onboarding-automatic_mounting-preparation-v2-step-2 = 3. 保持姿勢直到 3 秒倒數結束。
onboarding-automatic_mounting-preparation-v2-done = 你似乎最近進行過一次完整重置了！
onboarding-automatic_mounting-put_trackers_on-title = 請戴好追蹤器
onboarding-automatic_mounting-put_trackers_on-description = 為了校準配戴方向，我們將使用剛才分配的追蹤器。戴上你所有的追蹤器，你可以在右邊的圖中看到追蹤器的對應部位。
onboarding-automatic_mounting-put_trackers_on-next = 我所有的追蹤器都戴好了！
onboarding-automatic_mounting-return-home = 完成

## Tracker manual proportions setupa

onboarding-manual_proportions-back-scaled = 返回使用縮放比例
onboarding-manual_proportions-fine_tuning_button = 自動微調軀幹比例
onboarding-manual_proportions-fine_tuning_button-disabled-tooltip = 請連接 VR 頭戴顯示器以使用此功能
onboarding-manual_proportions-export = 匯出軀幹比例
onboarding-manual_proportions-import = 匯入軀幹比例
onboarding-manual_proportions-normal_increment = 正常調整
onboarding-manual_proportions-precise_increment = 精確調整
onboarding-manual_proportions-grouped_proportions = 分組調整軀幹比例
onboarding-manual_proportions-all_proportions = 全部軀幹比例
onboarding-manual_proportions-estimated_height = 預估的使用者身高

## Tracker automatic proportions setup

onboarding-automatic_proportions-back = 返回重置教學
onboarding-automatic_proportions-title = 測量你的身體比例
onboarding-automatic_proportions-description = 為了讓 SlimeVR 追蹤器正常使用，我們需要知道你的骨頭長度。這個簡短的流程將會進行這方面的測量。
onboarding-automatic_proportions-prev_step = 上一步
onboarding-automatic_proportions-put_trackers_on-title = 請戴好追蹤器
onboarding-automatic_proportions-put_trackers_on-description = 為了校準你的軀幹比例，我們將使用你剛才分配的追蹤器。戴上你所有的追蹤器，你可以在右邊的圖中看到追蹤器的對應部位。
onboarding-automatic_proportions-put_trackers_on-next = 我所有的追蹤器都戴好了！
onboarding-automatic_proportions-requirements-title = 使用需求
# Each line of text is a different list item
onboarding-automatic_proportions-requirements-descriptionv2 =
    你需要足夠的追蹤器以進行足部追蹤（通常為 5 個）。
    追蹤器的電源已經打開並已經穿著在身上。
    你需要穿戴上追蹤器與頭戴顯示器。
    追蹤器與頭戴顯示器都已經連接到 SlimeVR 伺服器，並且正常運作（亦即沒有卡頓或斷線等狀況）。
    追蹤器與頭戴顯示器在 SlimeVR 伺服器中運作正常。
    頭戴顯示器會回報定位資料給 SlimeVR 伺服器（通常為執行 SteamVR 並透過 SlimeVR 的 SteamVR 驅動程式來連接 SlimeVR）。
    追蹤狀態正常且能反映你的移動姿態（例如，進行完全重置後，踢腿、彎曲、坐下時的肢體方向是正確的）。
onboarding-automatic_proportions-requirements-next = 我已閱讀使用需求
onboarding-automatic_proportions-start_recording-title = 準備擺動作囉
onboarding-automatic_proportions-start_recording-description = 我們現在要記錄一些特定的姿勢和動作，將會在下一個畫面中提示。當按鈕被按下時，準備好開始！
onboarding-automatic_proportions-start_recording-next = 開始錄製
onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = 正在錄製中……
onboarding-automatic_proportions-recording-description-p1 = 請做出以下動作:
# Each line of text is a different list item
onboarding-automatic_proportions-recording-steps =
    挺直身子站好，然後活動脖子，使頭部沿逆時針或順時針方向繞一圈；
    上半身前傾然後屈膝半蹲，保持住這個姿勢不動，然後轉頭看向左邊，再轉頭看向右邊；
    站直身體，沿逆時針方向扭腰，使你的上半身朝向左前方，然後彎下腰，使上半身傾向左前方的地面；
    站直身體，沿順時針方向扭腰，使你的上半身朝向右前方，然後彎下腰，使上半身傾向右前方的地面；
    扭扭腰轉圈圈，就如同你在轉呼啦圈一樣！
    如果進度條還沒走完，可以重複以上動作直到錄製結束。
onboarding-automatic_proportions-recording-processing = 正在處理結果
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 20)
onboarding-automatic_proportions-recording-timer = 倒數 { $time } 秒
onboarding-automatic_proportions-verify_results-title = 檢查結果
onboarding-automatic_proportions-verify_results-description = 請檢查以下測量結果，看起來是正確的嗎？
onboarding-automatic_proportions-verify_results-results = 錄製結果
onboarding-automatic_proportions-verify_results-processing = 正在處理結果
onboarding-automatic_proportions-verify_results-redo = 重新錄製
onboarding-automatic_proportions-verify_results-confirm = 看起來沒問題
onboarding-automatic_proportions-done-title = 身體資料已測量並儲存。
onboarding-automatic_proportions-done-description = 你的身體比例校正已完成！
onboarding-automatic_proportions-error_modal-v2 =
    <b>警告：</b>推算軀幹比例時發生錯誤！
    這有可能是配戴校正的問題，請確保追蹤功能運作正常之後再試一次。
    <docs>請檢閱文件</docs>或加入我們的 <discord>Discord</discord> 以尋求幫助 ^_^
onboarding-automatic_proportions-error_modal-confirm = 瞭解！
onboarding-automatic_proportions-smol_warning =
    你設定的身高 { $height } 小於可接受的最小身高 { $minHeight }。
    <b>請重新進行測量，並確保數值是正確的。</b>
onboarding-automatic_proportions-smol_warning-cancel = 返回

## User height calibration

onboarding-user_height-title = 你的身高是多少？
onboarding-user_height-need_head_tracker = 進行校正需要具備定位功能的頭戴顯示器與控制器。
onboarding-user_height-calculate = 自動計算我的身高
onboarding-user_height-error_bounds = 輸入數值太高或太低
onboarding-user_height-error_format = 輸入格式錯誤
onboarding-user_height-next_step = 繼續並儲存
onboarding-user_height-manual-proportions = 手動調整軀幹比例
onboarding-user_height-calibration-title = 校正進度
onboarding-user_height-calibration-RECORDING_FLOOR = 以控制器前端碰觸地面
onboarding-user_height-calibration-WAITING_FOR_RISE = 回到站姿
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK = 回到站姿並向前看
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-ok = 確保你的頭部保持水平
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-low = 不要朝地板看
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-high = 不要朝高處看
onboarding-user_height-calibration-WAITING_FOR_CONTROLLER_PITCH = 確保控制器朝下
onboarding-user_height-calibration-RECORDING_HEIGHT = 維持站姿並站直！
onboarding-user_height-calibration-DONE = 完成！
onboarding-user_height-calibration-ERROR_TIMEOUT = 校正逾時，請再試一次。
onboarding-user_height-calibration-ERROR_TOO_HIGH = 偵測到的使用者身高太高，請再試一次。
onboarding-user_height-calibration-ERROR_TOO_SMALL = 偵測到的使用者身高太低，請確保在校正結尾時維持站直並向前看的姿勢。
onboarding-user_height-calibration-error = 校正失敗
onboarding-user_height-manual-tip = 調整身高時，請嘗試使用不同的姿勢來確保骨架與你的身體吻合。
onboarding-user_height-reset-warning =
    <b>警告：</b> 這會將軀幹比例重置為僅基於身高的比例。
    你確定要執行此操作嗎？

## Stay Aligned setup

onboarding-stay_aligned-title = 持續校正
onboarding-stay_aligned-description = 設定持續校正功能讓追蹤器保持對齊狀態。
onboarding-stay_aligned-put_trackers_on-title = 請戴好追蹤器
onboarding-stay_aligned-put_trackers_on-description = 為了保存放鬆中的姿態，我們將使用你剛才分配的追蹤器。戴上你所有的追蹤器，你可以在右邊的圖中看到追蹤器的對應部位。
onboarding-stay_aligned-put_trackers_on-trackers_warning = 你目前已連接與分配的追蹤器少於 5 個，持續校正功能需要 5 個以上的追蹤器才能正常運作。
onboarding-stay_aligned-put_trackers_on-next = 我所有的追蹤器都戴好了
onboarding-stay_aligned-verify_mounting-title = 確認追蹤器的配戴狀態
onboarding-stay_aligned-preparation-title = 準備
onboarding-stay_aligned-preparation-tip = 請確保站直。你必須向前直視，並且兩臂垂到身體兩側。
onboarding-stay_aligned-relaxed_poses-standing-title = 放鬆的站立姿勢
onboarding-stay_aligned-relaxed_poses-standing-step-0 = 1. 請以舒適的姿態站著，保持放鬆。
onboarding-stay_aligned-relaxed_poses-standing-step-1-v2 = 3. 請按下「儲存姿勢」按鈕。
onboarding-stay_aligned-relaxed_poses-sitting-title = 放鬆的坐在椅子上的姿勢
onboarding-stay_aligned-relaxed_poses-sitting-step-0 = 1. 請以舒適的姿態坐著，保持放鬆。
onboarding-stay_aligned-relaxed_poses-sitting-step-1-v2 = 3. 請按下「儲存姿勢」按鈕。
onboarding-stay_aligned-relaxed_poses-flat-title = 放鬆的坐在地板上的姿勢
onboarding-stay_aligned-relaxed_poses-flat-step-0 = 1. 請以舒適的姿態坐在地板上，腿部朝前，保持放鬆。
onboarding-stay_aligned-relaxed_poses-flat-step-1-v2 = 3. 請按下「儲存姿勢」按鈕。
onboarding-stay_aligned-relaxed_poses-skip_step = 跳過
onboarding-stay_aligned-done-title = 持續校正已啟用！
onboarding-stay_aligned-done-description = 持續校正功能設定完成。
onboarding-stay_aligned-done-description-2 = 設定完成。若需要重新校正姿勢，可以重新進行這個流程。
onboarding-stay_aligned-previous_step = 上一步
onboarding-stay_aligned-next_step = 下一步
onboarding-stay_aligned-restart = 重新開始
onboarding-stay_aligned-done = 完成
onboarding-stay_aligned-manual_mounting-done = 完成

## Home

home-settings = 主畫面設定
home-settings-close = 關閉
home-connection_group-wifi = Wi-Fi 追蹤器
home-connection_group-driver = VR 裝置
home-no_trackers-title = 追蹤器尚未連接
home-no_trackers-description = 連接追蹤器或是打開既有的追蹤器來開始使用
home-no_trackers-connect = 連接追蹤器
home-no_trackers-guide = 閱讀設定指南
# Accessible names and tooltips for the icon-only buttons in a connection group's toolbox
tracker-connection-metrics = 即時遙測
tracker-connection-dongle_settings = 接收器設定
tracker-connection-collapse = 收合群組
tracker-connection-expand = 展開群組

## Trackers Still On notification

trackers_still_on-modal-title = 有追蹤器的電源還開著
trackers_still_on-modal-description =
    至少有一個追蹤器的電源還開著。
    確定要退出 SlimeVR 嗎？
trackers_still_on-modal-confirm = 退出 SlimeVR
trackers_still_on-modal-cancel = 先不要…

## Firmware tool globals

firmware_tool-next_step = 下一步
firmware_tool-previous_step = 上一步
firmware_tool-ok = 看起來 OK
firmware_tool-retry = 重試
firmware_tool-loading = 載入中…

## Firmware tool Steps

firmware_tool = DIY 韌體工具
firmware_tool-description = 本工具可以配置與燒錄 DIY 追蹤器
firmware_tool-not_available = 唉呀，現在韌體工具無法使用。請稍後再來！
firmware_tool-not_compatible = 韌體工具與這個版本的伺服器不相容。請更新伺服器！
firmware_tool-select_source = 選擇要燒錄的韌體
firmware_tool-select_source-description = 選擇要在電路板上燒錄的韌體
firmware_tool-select_source-error = 無法載入韌體來源
firmware_tool-select_source-board_type = 電路板類型
firmware_tool-select_source-firmware = 韌體來源
firmware_tool-select_source-version = 韌體版本
firmware_tool-select_source-official = 正式版
firmware_tool-select_source-dev = 開發版
firmware_tool-select_source-latest = 最新版本
firmware_tool-select_source-not_selected = 未選擇來源
firmware_tool-select_source-no_boards = 此來源沒有可用的開發板
firmware_tool-select_source-no_versions = 此來源沒有可用的版本
firmware_tool-board_defaults = 設定電路板
firmware_tool-board_defaults-description = 設定與硬體相關的腳位或配置
firmware_tool-board_defaults-add = 新增
# Accessible name for the trash icon that removes a component
firmware_tool-board_defaults-remove = 移除
firmware_tool-board_defaults-reset = 恢復預設值
firmware_tool-board_defaults-error-required = 必填欄位
firmware_tool-board_defaults-error-format = 格式無效
firmware_tool-board_defaults-error-format-number = 不是數字
firmware_tool-flash_method_step = 燒錄方法
firmware_tool-flash_method_step-description = 選擇要使用的燒錄方法
firmware_tool-flash_method_step-ota-v2 =
    .label = Wi-Fi
    .description = 使用 OTA 線上更新。你的追蹤器會透過 Wi-Fi 來更新韌體，只支援已經設定好的追蹤器。
firmware_tool-flash_method_step-ota-info =
    即將使用你的 Wi-Fi 憑證來燒錄韌體，並確保一切正常。
    <b>我們不會儲存你的 Wi-Fi 憑證！</b>
firmware_tool-flash_method_step-serial-v2 =
    .label = USB
    .description = 透過 USB 連線來更新追蹤器。
firmware_tool-flashbtn_step = 進入燒錄模式
firmware_tool-flashbtn_step-description = 在進入下一步前，請先進行以下操作
firmware_tool-flashbtn_step-board_SLIMEVR = 關閉追蹤器電源，移除外殼（若有的話），並用 USB 線連接到這台電腦上，然後根據你持有的 SlimeVR 追蹤器主板的版本，進行下述操作：
firmware_tool-flashbtn_step-board_SLIMEVR-r11-v2 = 將追蹤器上方第二個 FLASH 方形接點與微控制器的金屬遮罩短路，同時開啟追蹤器開關。追蹤器指示燈應該會短暫閃爍並熄滅。
firmware_tool-flashbtn_step-board_SLIMEVR-r12-v2 = 將追蹤器上方的 FLASH 圓形接點與微控制器的金屬遮罩短路，同時開啟追蹤器開關。追蹤器指示燈應該會短暫閃爍並熄滅。
firmware_tool-flashbtn_step-board_SLIMEVR-r14-v2 = 按住追蹤器上方的 FLASH 按鈕，同時開啟追蹤器開關。追蹤器指示燈應該會短暫閃爍並熄滅。
firmware_tool-flashbtn_step-board_OTHER =
    在燒錄前，你可能需要將追蹤器切換進 Bootloader（開機載入程式）。
    多數狀況下，在燒錄開始前按下 BOOT 按鈕即可開始燒錄。
    如果燒錄進度開始時就已逾時，表示追蹤器未能進入 Bootloader 模式，
    請參考追蹤器主板燒錄韌體的說明文件，以得知進入 Bootloader 模式的方法。
firmware_tool-flash_method_ota-title = 透過 Wi-Fi 燒錄
firmware_tool-flash_method_ota-devices = 偵測到的 OTA 裝置：
firmware_tool-flash_method_ota-no_devices = 找不到可以使用 OTA 更新的主板，請確認所選擇的主板類型
firmware_tool-flash_method_serial-title = 透過 USB 燒錄
firmware_tool-flash_method_serial-wifi = Wi-Fi 認證資訊：
firmware_tool-flash_method_serial-devices-label = 偵測到的序列埠裝置：
firmware_tool-flash_method_serial-devices-placeholder = 選擇一個序列埠裝置
firmware_tool-flash_method_serial-unknown_device = { $name }（不明）
firmware_tool-flash_method_serial-no_devices = 偵測不到相容的序列埠裝置，請確認追蹤器已連接
firmware_tool-build_step = 建置中
firmware_tool-build_step-description = 韌體正在建置中，請稍後
firmware_tool-flashing_step = 燒錄中
firmware_tool-flashing_step-description = 追蹤器燒錄中，請遵循畫面上的指示
firmware_tool-flashing_step-warning-v2 = 除非特別指示，燒錄中請勿移除或是關閉追蹤器，否則可能導致主板無法使用
firmware_tool-flashing_step-flash_more = 燒錄更多追蹤器
firmware_tool-flashing_step-exit = 離開

## firmware tool build status

firmware_tool-build-QUEUED = 正在等待建置…
firmware_tool-build-CREATING_BUILD_FOLDER = 正在建立建置資料夾
firmware_tool-build-DOWNLOADING_SOURCE = 正在下載原始碼
firmware_tool-build-EXTRACTING_SOURCE = 正在解壓縮原始碼
firmware_tool-build-BUILDING = 正在建置韌體
firmware_tool-build-SAVING = 正在儲存建置
firmware_tool-build-DONE = 建置完成
firmware_tool-build-ERROR = 無法建置韌體

## Firmware update status

firmware_update-status-DOWNLOADING = 正在下載韌體
firmware_update-status-NEED_MANUAL_REBOOT-v2 = 請關閉追蹤器的電源再打開
firmware_update-status-AUTHENTICATING = 正在與 MCU 認證
firmware_update-status-UPLOADING = 正在上傳韌體
firmware_update-status-SYNCING_WITH_MCU = 正在與 MCU 同步
firmware_update-status-REBOOTING = 正在套用更新
firmware_update-status-PROVISIONING = 正在設定 Wi-Fi 認證資訊
firmware_update-status-DONE = 更新完成！
firmware_update-status-ERROR_DEVICE_NOT_FOUND = 找不到裝置
firmware_update-status-ERROR_TIMEOUT = 更新流程已逾時
firmware_update-status-ERROR_DOWNLOAD_FAILED = 無法下載韌體
firmware_update-status-ERROR_AUTHENTICATION_FAILED = 無法與 MCU 進行認證
firmware_update-status-ERROR_UPLOAD_FAILED = 無法上傳韌體
firmware_update-status-ERROR_PROVISIONING_FAILED = 無法設定 Wi-Fi 認證資訊
firmware_update-status-ERROR_UNSUPPORTED_METHOD = 更新方法不支援
firmware_update-status-ERROR_UNKNOWN = 發生不明錯誤

## Dedicated Firmware Update Page

firmware_update-title = 韌體更新
firmware_update-devices = 可用裝置
firmware_update-devices-description = 請選擇要更新到最新版本 SlimeVR 韌體的追蹤器
firmware_update-no_devices = 請確認要更新的追蹤器電源已開啟並連接到 Wi-Fi
firmware_update-changelog-title = 更新到 { $version }
firmware_update-looking_for_devices = 正在尋找要更新的裝置…
firmware_update-retry = 重試
firmware_update-update = 更新所選的追蹤器
firmware_update-exit = 離開

## Tray Menu

tray_menu-show = 顯示
tray_menu-hide = 隱藏
tray_menu-quit = 離開

## First exit modal

tray_or_exit_modal-title = 關閉視窗的動作是什麼？
# Multiline text
tray_or_exit_modal-description =
    你可以選擇在關閉視窗時，一併退出伺服器程式，或是將視窗最小化到系統列圖示中。
    
    本設定之後也可以在使用者介面設定中更改。
tray_or_exit_modal-radio-exit = 退出 SlimeVR
tray_or_exit_modal-radio-tray = 最小化到系統列
tray_or_exit_modal-submit = 儲存
tray_or_exit_modal-cancel = 取消

## Unknown device modal

unknown_device-modal-title = 找到了新的追蹤器！
unknown_device-modal-description =
    偵測到新的追蹤器，其 MAC 位址為 <b>{ $deviceId }</b>。
    要將它連接到 SlimeVR 嗎？
unknown_device-modal-confirm = 好喔！
unknown_device-modal-forget = 忽略它
# VRChat config warnings
vrc_config-page-title = VRChat 設定警告
vrc_config-page-desc = 本頁會顯示 VRChat 的設定並顯示哪些設定與 SlimeVR 不相容。非常建議您根據這裡顯示的警告來修改設定，以獲得最佳的 SlimeVR 使用體驗。
vrc_config-page-help = 找不到設定在哪裡嗎？
vrc_config-page-help-desc = 請查閱<a>本主題的相關文件</a>
vrc_config-page-big_menu = 追蹤 & IK（主選單）
vrc_config-page-big_menu-desc = 主選單中與 IK （逆向運動學）相關的設定
vrc_config-page-wrist_menu = 追蹤 & IK（快速選單）
vrc_config-page-wrist_menu-desc = 快速選單中與 IK （逆向運動學）相關的設定
vrc_config-on = 開啟
vrc_config-off = 關閉
vrc_config-setting_name = VRChat 設定名稱
vrc_config-recommended_value = 建議設定
vrc_config-current_value = 目前設定
vrc_config-mute = 消除警告
vrc_config-mute-btn = 消除
vrc_config-unmute-btn = 復歸
vrc_config-legacy_mode = 使用傳統 IK 解決方案
vrc_config-disable_shoulder_tracking = 停用肩膀追蹤
vrc_config-shoulder_width_compensation = 肩寬補償
vrc_config-spine_mode = FBT 脊椎模式
vrc_config-tracker_model = FBT 追蹤器形狀
vrc_config-avatar_measurement_type = 角色測量
vrc_config-calibration_range = 校正範圍
vrc_config-calibration_visuals = 顯示 FBT 校正範圍
vrc_config-user_height = 用戶真實身高
vrc_config-spine_mode-UNKNOWN = 不明
vrc_config-spine_mode-LOCK_BOTH = 同時鎖定
vrc_config-spine_mode-LOCK_HEAD = 鎖定頭部
vrc_config-spine_mode-LOCK_HIP = 鎖定臀部
vrc_config-tracker_model-UNKNOWN = 不明
vrc_config-tracker_model-AXIS = 軸
vrc_config-tracker_model-BOX = 箱型
vrc_config-tracker_model-SPHERE = 球型
vrc_config-tracker_model-SYSTEM = 系統
vrc_config-avatar_measurement_type-UNKNOWN = 不明
vrc_config-avatar_measurement_type-HEIGHT = 身高
vrc_config-avatar_measurement_type-ARM_SPAN = 臂展

## Error collection consent modal

error_collection_modal-title = 我們可以蒐集錯誤資訊嗎？
error_collection_modal-description_v2 =
    { settings-interface-behavior-error_tracking-description_v2 }
    
    若之後要變更此設定，可以在「詳細設定」頁面中的「行為」來變更。
error_collection_modal-confirm = 我同意
error_collection_modal-cancel = 我不想要

## Tracking checklist section

tracking_checklist = 追蹤清單
tracking_checklist-settings = 追蹤清單設定
tracking_checklist-settings-close = 關閉
# Accessible name and tooltip for the icon-only button that collapses/expands the checklist panel
tracking_checklist-collapse = 收合檢查清單
tracking_checklist-expand = 展開檢查清單
tracking_checklist-status-incomplete = 還沒做完 SlimeVR 使用前的準備！
tracking_checklist-status-partial = 你有 { $count } 項警告！
tracking_checklist-status-complete = 已經準備好使用 SlimeVR 了！
tracking_checklist-MOUNTING_CALIBRATION = 進行配戴校正
tracking_checklist-FEET_MOUNTING_CALIBRATION = 進行腳部的配戴校正
tracking_checklist-FULL_RESET = 進行完整重置
tracking_checklist-FULL_RESET-desc = 有追蹤器需要進行重置
tracking_checklist-STEAMVR_DISCONNECTED = SteamVR 已離線
tracking_checklist-STEAMVR_DISCONNECTED-desc = SteamVR 未執行，你要把追蹤器用在 VR 上嗎？
tracking_checklist-STEAMVR_DISCONNECTED-driver_blocked-desc = 由於前次的 SteamVR 崩潰，SlimeVR 驅動程式已被 SteamVR 停用。
tracking_checklist-STEAMVR_DISCONNECTED-driver_disabled-desc = SteamVR 設定中已停用 SlimeVR 驅動程式。
tracking_checklist-STEAMVR_DISCONNECTED-driver_not_installed-desc = 未安裝驅動程式。
tracking_checklist-STEAMVR_DISCONNECTED-open = 啟動 SteamVR
tracking_checklist-STEAMVR_DISCONNECTED-enable = 啟用驅動程式
tracking_checklist-STEAMVR_HANDS_ENABLED = 手部追蹤器已開啟
tracking_checklist-STEAMVR_HANDS_ENABLED-desc = 已開啟 SteamVR 的手部追蹤器，這會導致 VR 控制器按鍵的輸入無法在 SteamVR 與遊戲中使用。
tracking_checklist-STEAMVR_HANDS_ENABLED-go = 關閉手部追蹤器
tracking_checklist-STANDABLE_INSTALLED = 已安裝 Standable
tracking_checklist-STANDABLE_INSTALLED-desc = Standable 與 SlimeVR 同時使用時會導致追蹤問題。為了確保沒有相關問題發生，請從 Steam 完整移除 Standable。必須先關閉 SteamVR 才能從 Steam 移除 Standable。
tracking_checklist-TRACKERS_REST_CALIBRATION = 校正追蹤器
tracking_checklist-TRACKERS_REST_CALIBRATION-desc = 追蹤器尚未進行校正。請將以黃色標記的追蹤器放置在平面上幾秒鐘。
tracking_checklist-TRACKER_ERROR = 追蹤器出現錯誤
tracking_checklist-TRACKER_ERROR-desc = 有追蹤器發生錯誤，請重啟黃色標記的追蹤器。
tracking_checklist-VRCHAT_SETTINGS = 調整 VRChat 設定
tracking_checklist-VRCHAT_SETTINGS-desc = VRChat 的設定有問題，這會影響到在 VRChat 使用 SlimeVR 的體驗。
tracking_checklist-VRCHAT_SETTINGS-open = 前往 VRChat 警告
tracking_checklist-UNASSIGNED_HMD = VR 頭戴裝置尚未分配給頭部
tracking_checklist-UNASSIGNED_HMD-desc = VR 頭戴顯示器應被分配為頭部追蹤器。
tracking_checklist-NETWORK_PROFILE_PUBLIC = 變更網路設定檔
tracking_checklist-NETWORK_PROFILE_PUBLIC-desc =
    { $count ->
        [one]
            網路設定檔目前設定為「公開」({ $adapters })。
            為了確保 SlimeVR 運作正常，不建議使用此設定。
            <PublicFixLink>修正方法看這裡。</PublicFixLink>
       *[many]
            有多個網路卡的網路設定檔目前設定為「公開」：
            { $adapters }
            為了確保 SlimeVR 運作正常，不建議使用此設定。
            <PublicFixLink>修正方法看這裡。</PublicFixLink>
    
    }
tracking_checklist-NETWORK_PROFILE_PUBLIC-open = 開啟控制台
tracking_checklist-STAY_ALIGNED_CONFIGURED = 調整持續校正設定
tracking_checklist-STAY_ALIGNED_CONFIGURED-desc = 記錄持續校正所使用的姿勢以減緩飄移現象
tracking_checklist-STAY_ALIGNED_CONFIGURED-open = 開啟持續校正設定
tracking_checklist-VRCHAT_OSC_TRACKING_DISABLED = 啟用 VRChat OSC 追蹤資料
tracking_checklist-VRCHAT_OSC_TRACKING_DISABLED-desc =
    已經連接 VRChat，但 VRChat 沒有傳送頭部與手部的追蹤資料進 SlimeVR。請在 VRChat 開啟 OSC 追蹤資料設定。
    <OscTrackingLink>開啟方法看這裡。</OscTrackingLink>
tracking_checklist-VRCHAT_OSC_TRACKING_DISABLED-open = 開啟 VRChat OSC 設定
tracking_checklist-ignore = 忽略
preview-mocap_mode_soon = 動作捕捉模式（即將推出™）
preview-disable_render = 停用預覽
preview-disabled_render = 預覽已停用
preview-render_mode = 變更渲染模式
preview-reset_camera = 重設相機
toolbar-mounting_calibration = 配戴校正
toolbar-mounting_calibration-default = 身體
toolbar-mounting_calibration-feet = 腳部
toolbar-mounting_calibration-toes = 腳趾
toolbar-mounting_calibration-fingers = 手指
toolbar-drift_reset = 漂移重置
toolbar-assigned_trackers = { $count } 個追蹤器已分配
toolbar-unassigned_trackers = { $count } 個追蹤器尚未分配
