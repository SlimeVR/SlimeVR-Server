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


## Text input


## File input


## Window controls

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

## BoardType

board_type-UNKNOWN = 不明
board_type-CUSTOM = 自訂主板
board_type-SLIMEVR_DEV = SlimeVR 開發板
board_type-WRANGLER = Wrangler Joy-Con
board_type-MOCOPI = Sony mocopi
board_type-WEMOSWROOM02 = WeMos WROOM-02 D1 Mini
board_type-GLOVE_IMU_SLIMEVR_DEV = SlimeVR Dev IMU 手套
board_type-GESTURES = litten Yº by Gestures
board_type-GENERIC_NRF = 通用 nRF

## Proportions

skeleton_bone-NONE = 無
skeleton_bone-HEAD = 頭部偏移
skeleton_bone-HEAD-desc =
    這是從頭戴顯示器到頭中央的距離。
    若要調整，請左右搖頭（如不同意、否定般的樣子），檢查其他追蹤器的數值跳
    動並變更此參數，直到數值跳動小到可以忽略不計。
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
skeleton_bone-LOWER_CHEST-desc =
    這是從胸部中間到脊椎中間的距離。
    若要調整，請適當調整軀幹長度，檢查各種姿勢（坐下、彎腰、躺下等）並進行
    修改，直到虛擬的脊椎與實際脊椎對齊。
skeleton_bone-HIP = 臀部長度
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
    這是從脖子底部到肩膀的水平距離。
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
skeleton_bone-HAND_Y = 手部距離Y
skeleton_bone-HAND_Y-desc =
    這是從手腕到手中間的垂直距離。
    若要調整以進行動作捕捉，請適當調整手臂長度後修改此數值，直到虛擬的手部追蹤器
    與實際的手中間垂直對齊。
    若要調整以從控制器進行肘部跟蹤，請將手臂長度設為 0 後修改此數值，直到虛擬的肘
    部追蹤器與實際的手腕垂直對齊。
skeleton_bone-HAND_Z = 手部距離Z
skeleton_bone-HAND_Z-desc =
    這是從手腕到手中間的水平距離。
    若要調整以進行動作捕捉，請將此值設定為 0。
    若要調整以從控制器進行肘部跟蹤，請將手臂長度設為 0 後修改此數值，直到虛擬的肘
    部追蹤器與實際的手腕水平對齊。

## Tracker reset buttons

reset-reset_all = 重置軀幹比例
reset-reset_all_warning-reset = 重置軀幹比例
reset-reset_all_warning-cancel = 取消
reset-full = 完整重置
reset-mounting = 確認追蹤器的配戴狀態
reset-mounting-feet = 重置腳部配戴
reset-mounting-fingers = 重置手指配戴
reset-yaw = 左右偏擺重置
reset-error-mounting-need_full_reset = 配戴校正前需要完整重置
reset-error-yaw-need_full_reset = 左右偏擺重置前需要完整重置

## Navigation bar

navbar-home = 首頁
navbar-body_proportions = 軀幹比例
navbar-trackers_assign = 追蹤器分配
navbar-mounting = 確認追蹤器的配戴狀態
navbar-onboarding = 快速設定
navbar-settings = 設定
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

widget-developer_mode = 開發者模式
widget-developer_mode-high_contrast = 高對比 UI
widget-developer_mode-precise_rotation = 顯示精確旋轉
widget-developer_mode-fast_data_feed = 快速資料更新
widget-developer_mode-raw_slime_rotation = 原始旋轉

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

## Tracker status columns

tracker-table-column-name = 名稱
tracker-table-column-type = 類型
tracker-table-column-battery = 電量
tracker-table-column-temperature = 溫度 ℃
tracker-table-column-linear-acceleration = 加速度 X/Y/Z
tracker-table-column-rotation = 旋轉 X/Y/Z
tracker-table-column-position = 位置 X/Y/Z
tracker-table-column-stay_aligned = 持續校正

## Tracker rotation

tracker-rotation-front = 前
tracker-rotation-front_left = 左前方
tracker-rotation-front_right = 右前方
tracker-rotation-left = 左
tracker-rotation-right = 右
tracker-rotation-back = 向後彎折
tracker-rotation-back_left = 左後方
tracker-rotation-back_right = 右後方
tracker-rotation-custom = 自訂

## Tracker information

tracker-infos-manufacturer = 製造商
tracker-infos-display_name = 顯示名稱
tracker-infos-custom_name = 自訂名稱
tracker-infos-url = 追蹤器 URL
tracker-infos-hardware_identifier = 硬體 ID
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

dongle-infos-hardware_revision = 硬體版本
dongle-status-disconnected = 連線中斷
dongle-settings-back = 返回追蹤器列表
dongle-settings-update = 立即更新
dongle-settings-update-title = 韌體版本

## Tracker part card info

tracker-part_card-unassigned = 未分配

## Body assignment menu

body_assignment_menu = 你想將此追蹤器戴在哪裡？
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

mounting_selection_menu-close = 關閉

## Sidebar settings

settings-sidebar-title = 設定
settings-sidebar-general = 一般設定
settings-sidebar-stay_aligned = 持續校正
settings-sidebar-trackers = 追蹤器
settings-sidebar-interface = 使用者介面
settings-sidebar-utils = 工具
settings-sidebar-appearance = 外觀
settings-sidebar-home = 主畫面
settings-sidebar-checklist = 追蹤清單
settings-sidebar-notifications = 通知
settings-sidebar-firmware-tool = DIY 韌體工具
settings-sidebar-vrc_warnings = VRChat 配置警告
settings-sidebar-advanced = 進階

## Bone routing settings

settings-routing-output-badge-off = 關閉
settings-routing-group-fingers = 手指
settings-routing-hands-warning-cancel = 取消

## SteamVR / Monado output settings

settings-driver-enable = 啟用
settings-driver-status-badge-disabled = 關閉

## Tracker mechanics

settings-general-tracker_mechanics-filtering = 濾波
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    選擇追蹤器的濾波類型。
    預測型將會對你的運動加以預測，而平滑型將會讓你的運動更加平滑。
settings-general-tracker_mechanics-filtering-type-none = 不進行濾波
settings-general-tracker_mechanics-filtering-type-none-description = 使用原始資料，不進行濾波。
settings-general-tracker_mechanics-filtering-type-smoothing = 平滑型
settings-general-tracker_mechanics-filtering-type-smoothing-description = 讓運動更加平滑，但會增加一些延遲。
settings-general-tracker_mechanics-filtering-type-prediction = 預測型
settings-general-tracker_mechanics-filtering-type-prediction-description = 減少延遲並使移動更敏捷，但可能會增加一些抖動。
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

settings-keybinds_full-reset = 完整重置
settings-keybinds_yaw-reset = 左右偏擺重置
settings-keybinds_reset-all-button = 重置全部設定
settings-keybinds-recorder-modal-done-button = 完成
settings-keybinds-recorder-modal-cancel-button = 取消

## FK/Tracking settings

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
settings-general-fk_settings-self_localization-title = 動作捕捉模式

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
settings-general-gesture_control-fullResetEnabled = 敲擊以完整重置
settings-general-gesture_control-fullResetDelay = 完整重置延遲
settings-general-gesture_control-fullResetTaps = 完整重置敲擊次數
settings-general-gesture_control-mountingResetEnabled = 敲擊以配戴重置
settings-general-gesture_control-mountingResetDelay = 重置配戴延遲
settings-general-gesture_control-mountingResetTaps = 重置配戴敲擊次數
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = 觸發敲擊判定的最多追蹤器數量
settings-general-gesture_control-numberTrackersOverThreshold-description = 如果敲擊偵測無法作動，請嘗試增加此值以降低敲擊判定的門檻。為避免誤判，請勿設定超過所需要的數值。

## Appearance settings

settings-interface-appearance = 外觀
settings-general-interface-dev_mode = 開發者模式
settings-general-interface-dev_mode-description = 本功能會提供更深入的資料，也能與已連線的追蹤器進行更進一步的控制。
settings-general-interface-dev_mode-label = 開發者模式
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

settings-general-interface-dev_mode = 開發者模式
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

## Serial settings

settings-serial-connection_lost = 序列埠連線中斷，正在重新連線……
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
settings-serial-auto_dropdown_item = 自動
settings-serial-save_logs = 儲存到檔案
settings-serial-send_command = 傳送
settings-serial-send_command-placeholder = 輸入指令…
settings-serial-send_command-warning = <b>警告：</b>執行序列埠指令可能會導致資料遺失或追蹤器變磚。
settings-serial-send_command-warning-ok = 我已瞭解以上風險
settings-serial-send_command-warning-cancel = 取消

## OSC VRChat settings

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

settings-osc-vrchat-status-tracking = 旋轉
settings-osc-vrchat-status-badge-error = 錯誤
settings-osc-vrchat-status-badge-unknown = 不明

## VMC OSC settings

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
settings-osc-vmc-network-address = 網路位址
settings-osc-vmc-network-address-description = 設定用來發送 VMC 資料的位址。
settings-osc-vmc-network-address-placeholder = IPV4 地址
settings-osc-vmc-vrm = VRM 模型
settings-osc-vmc-vrm-untitled_model = 未命名模型
settings-osc-vmc-vrm-file_select = 拖曳檔案或 <u>瀏覽檔案</u> 以載入模型
settings-osc-vmc-anchor_hip = 臀部錨定
settings-osc-vmc-anchor_hip-label = 臀部錨定
settings-osc-vmc-mirror_tracking = 鏡像追蹤
settings-osc-vmc-mirror_tracking-description = 將追蹤的結果水平鏡像。
settings-osc-vmc-mirror_tracking-label = 鏡像追蹤
settings-osc-vmc-status-badge-error = 錯誤

## Common OSC settings

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

