### SlimeVR complete GUI translations


# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = 正在連接伺服器
websocket-connection_lost = 與伺服器的連線已中斷，正在嘗試重新連線……

## Update notification

version_update-title = 有可用的新版本：{ $version }
version_update-description = 按下「更新」將為您下載 SlimeVR 安裝程式。
version_update-update = 更新
version_update-close = 關閉

## Tips

tips-find_tracker = 若你不確定手上的追蹤器是哪一個，搖一搖它，對應的項目就會顯示出來
tips-do_not_move_heels = 確保你的腳跟在測量過程時不會發生移動！
tips-file_select = 拖曳檔案或 <u>瀏覽檔案</u> 以使用
tips-tap_setup = 除了從列表挑選追蹤器以外，您也可以慢慢敲擊 2 次追蹤器來選擇它。

## Body parts

body_part-NONE = 未分配
body_part-HEAD = 頭部
body_part-NECK = 頸部
body_part-RIGHT_SHOULDER = 右肩
body_part-RIGHT_UPPER_ARM = 右上臂
body_part-RIGHT_LOWER_ARM = 右前臂
body_part-RIGHT_HAND = 右手
body_part-RIGHT_UPPER_LEG = 右大腿
body_part-RIGHT_LOWER_LEG = 右小腿
body_part-RIGHT_FOOT = 右腳
body_part-CHEST = 胸部
body_part-WAIST = 腰部
body_part-HIP = 臀部
body_part-LEFT_SHOULDER = 左肩
body_part-LEFT_UPPER_ARM = 左上臂
body_part-LEFT_LOWER_ARM = 左前臂
body_part-LEFT_HAND = 左手
body_part-LEFT_UPPER_LEG = 左大腿
body_part-LEFT_LOWER_LEG = 左小腿
body_part-LEFT_FOOT = 左腳

## Proportions

skeleton_bone-NONE = 無
skeleton_bone-HEAD = 頭部偏移
skeleton_bone-NECK = 頸部長度
skeleton_bone-torso_group = 軀幹長度
skeleton_bone-CHEST = 胸部長度
skeleton_bone-CHEST_OFFSET = 胸部偏移
skeleton_bone-WAIST = 腰部長度
skeleton_bone-HIP = 臀部長度
skeleton_bone-HIP_OFFSET = 臀部偏移
skeleton_bone-HIPS_WIDTH = 臀部寬度
skeleton_bone-leg_group = 腿部長度
skeleton_bone-UPPER_LEG = 大腿長度
skeleton_bone-LOWER_LEG = 小腿長度
skeleton_bone-FOOT_LENGTH = 腳部長度
skeleton_bone-FOOT_SHIFT = 腳部偏移
skeleton_bone-SKELETON_OFFSET = 骨骼偏移
skeleton_bone-SHOULDERS_DISTANCE = 肩膀距離
skeleton_bone-SHOULDERS_WIDTH = 肩膀寬度
skeleton_bone-arm_group = 手臂長度
skeleton_bone-UPPER_ARM = 上臂長度
skeleton_bone-LOWER_ARM = 前臂長度
skeleton_bone-HAND_Y = 手部距離Y
skeleton_bone-HAND_Z = 手部距離Z
skeleton_bone-ELBOW_OFFSET = 肘部偏移

## Tracker reset buttons

reset-reset_all = 重置軀幹比例
reset-full = 完整重置
reset-mounting = 配戴重置
reset-yaw = 左右偏擺重置

## Serial detection stuff

serial_detection-new_device-p0 = 偵測到了新的串列埠裝置！
serial_detection-new_device-p1 = 輸入你的 Wi-Fi 認證資訊！
serial_detection-new_device-p2 = 請選擇你想對它做什麼
serial_detection-open_wifi = 連線到 Wi-Fi
serial_detection-open_serial = 開啟串列埠終端
serial_detection-submit = 送出！
serial_detection-close = 關閉

## Navigation bar

navbar-home = 首頁
navbar-body_proportions = 軀幹比例
navbar-trackers_assign = 追蹤器分配
navbar-mounting = 配戴校正
navbar-onboarding = 快速設定
navbar-settings = 詳細設定

## Bounding volume hierarchy recording

bvh-start_recording = 錄製 BVH 檔案
bvh-recording = 錄製中…

## Widget: Overlay settings

widget-overlay = 內嵌介面
widget-overlay-is_visible_label = 在 SteamVR 中顯示內嵌介面
widget-overlay-is_mirrored_label = 鏡像顯示內嵌介面

## Widget: Drift compensation

widget-drift_compensation-clear = 清除偏移補償數據

## Widget: Developer settings

widget-developer_mode = 開發者選項
widget-developer_mode-high_contrast = 高對比 UI
widget-developer_mode-precise_rotation = 顯示精確旋轉
widget-developer_mode-fast_data_feed = 快速資料更新
widget-developer_mode-filter_slimes_and_hmd = 只顯示 Slime 追蹤器與頭戴顯示器
widget-developer_mode-sort_by_name = 依名稱排序
widget-developer_mode-raw_slime_rotation = 顯示原始旋轉
widget-developer_mode-more_info = 更多資訊

## Widget: IMU Visualizer

widget-imu_visualizer = 旋轉
widget-imu_visualizer-rotation_raw = 原始旋轉
widget-imu_visualizer-rotation_preview = 預覽
widget-imu_visualizer-rotation_hide = 隱藏

## Tracker status

tracker-status-none = 無
tracker-status-busy = 忙碌
tracker-status-error = 錯誤
tracker-status-disconnected = 連線中斷
tracker-status-occluded = 被遮擋
tracker-status-ok = 已連線

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
tracker-table-column-url = URL

## Tracker rotation

tracker-rotation-front = 前
tracker-rotation-left = 左
tracker-rotation-right = 右
tracker-rotation-back = 後

## Tracker information

tracker-infos-manufacturer = 製造商
tracker-infos-display_name = 顯示名稱
tracker-infos-custom_name = 自訂名稱
tracker-infos-url = 追蹤器 URL
tracker-infos-version = 韌體版本
tracker-infos-hardware_rev = 硬體版本
tracker-infos-hardware_identifier = 硬體 ID
tracker-infos-imu = 慣性測量單元 (IMU)
tracker-infos-board_type = 主板

## Tracker settings

tracker-settings-back = 返回追蹤器列表
tracker-settings-title = 追蹤器設定
tracker-settings-assignment_section = 分配追蹤器
tracker-settings-assignment_section-description = 這個追蹤器分配到的身體部位
tracker-settings-assignment_section-edit = 編輯分配
tracker-settings-mounting_section = 配戴方式
tracker-settings-mounting_section-description = 這個追蹤器要配戴在哪裡？
tracker-settings-mounting_section-edit = 編輯配戴方式
tracker-settings-drift_compensation_section = 偏移補償
tracker-settings-drift_compensation_section-description = 是否在此追蹤器上套用偏移補償？
tracker-settings-drift_compensation_section-edit = 允許偏移補償
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = 追蹤器名稱
tracker-settings-name_section-description = 給它起一個可愛的名字吧 ^^
tracker-settings-name_section-placeholder = ↖★煞氣a黑貓☆↘的美味右腿

## Tracker part card info

tracker-part_card-no_name = 未命名
tracker-part_card-unassigned = 未分配

## Body assignment menu

body_assignment_menu = 將這個追蹤器配戴在哪裡？
body_assignment_menu-description = 選擇要將此追蹤器分配到的身體部位。除了逐個設定外，你也可以一次設定所有追蹤器。
body_assignment_menu-show_advanced_locations = 顯示進階分配部位
body_assignment_menu-manage_trackers = 管理所有追蹤器
body_assignment_menu-unassign_tracker = 解除分配

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = 哪個追蹤器要分配到你的
tracker_selection_menu-NONE = 你想將哪個追蹤器解除分配？
tracker_selection_menu-HEAD = { -tracker_selection-part }頭部？
tracker_selection_menu-NECK = { -tracker_selection-part }頸部？
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part }右肩？
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part }右上臂？
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part }右前臂？
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part }右手？
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part }右大腿？
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part }右小腿？
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part }右腳？
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part }右控制器？
tracker_selection_menu-CHEST = { -tracker_selection-part }胸部？
tracker_selection_menu-WAIST = { -tracker_selection-part }腰部？
tracker_selection_menu-HIP = { -tracker_selection-part }臀部？
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part }左肩？
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part }左上臂？
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part }左前臂？
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part }左手？
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part }左大腿？
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part }左小腿？
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part }左腳？
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part }左控制器？
tracker_selection_menu-unassigned = 尚未分配的追蹤器
tracker_selection_menu-assigned = 已分配的追蹤器
tracker_selection_menu-dont_assign = 不要分配
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
settings-sidebar-tracker_mechanics = 追蹤機制
settings-sidebar-fk_settings = 追蹤設定
settings-sidebar-gesture_control = 手勢控制
settings-sidebar-interface = 使用者介面
settings-sidebar-osc_router = OSC 路由
settings-sidebar-osc_trackers = VRChat OSC 追蹤器
settings-sidebar-utils = 工具
settings-sidebar-serial = 串列埠終端

## SteamVR settings

settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = SteamVR 追蹤器
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    開啟或關閉特定的 SteamVR 追蹤器。
    對於只支援特定追蹤器的遊戲或應用程式會很有用。
settings-general-steamvr-trackers-waist = 腰部
settings-general-steamvr-trackers-chest = 胸部
settings-general-steamvr-trackers-feet = 腳部
settings-general-steamvr-trackers-knees = 膝蓋
settings-general-steamvr-trackers-elbows = 肘部
settings-general-steamvr-trackers-hands = 手部

## Tracker mechanics

settings-general-tracker_mechanics = 追蹤機制
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
settings-general-tracker_mechanics-drift_compensation = 偏移補償
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    套用逆向旋轉以補償陀螺儀的左右偏擺位移。
    你可以更改補償的強度，以及使用幾次以內的重置結果來進行補償。
settings-general-tracker_mechanics-drift_compensation-enabled-label = 偏移補償
settings-general-tracker_mechanics-drift_compensation-amount-label = 補償量
settings-general-tracker_mechanics-drift_compensation-max_resets-label = 使用幾次的重置結果？

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
settings-general-fk_settings-leg_tweak-skating_correction = 腳滑修正
settings-general-fk_settings-leg_tweak-toe_snap = 腳趾跟地
settings-general-fk_settings-leg_tweak-foot_plant = 腳底貼地
settings-general-fk_settings-leg_tweak-skating_correction-amount = 腳滑修正量
settings-general-fk_settings-leg_tweak-skating_correction-description = 腳滑修正功能可以矯正腳滑溜冰的問題，但會降低某些動作的準確度。啟用本功能前請進行完整重置，並在遊戲內進行校正。
settings-general-fk_settings-leg_tweak-floor_clip-description = 地板限制功能可以減輕甚至解決腳部穿入地板的情況。啟用本功能前請進行完整重置，並在遊戲內進行校正。
settings-general-fk_settings-leg_tweak-toe_snap-description = 腳趾跟地功能在沒有腳部的追蹤器時，會嘗試猜測腳掌的旋轉角度。
settings-general-fk_settings-leg_tweak-foot_plant-description = 腳底貼地功能會在腳底與地面接觸時，將腳部旋轉成與地板平行。
settings-general-fk_settings-leg_fk = 腿部追蹤
settings-general-fk_settings-arm_fk = 手臂追蹤
settings-general-fk_settings-arm_fk-description = 強制透過頭戴顯示器來追蹤手臂，即使有手部的定位資料。
settings-general-fk_settings-arm_fk-force_arms = 強制從頭戴顯示器進行手臂追蹤
settings-general-fk_settings-skeleton_settings = 骨架設定
settings-general-fk_settings-skeleton_settings-description = 開啟或關閉骨架設定。建議保持這些設定開啟。
settings-general-fk_settings-skeleton_settings-extended_spine = 脊椎延伸
settings-general-fk_settings-skeleton_settings-extended_pelvis = 骨盆延伸
settings-general-fk_settings-skeleton_settings-extended_knees = 膝蓋延伸
settings-general-fk_settings-vive_emulation-title = Vive 模擬
settings-general-fk_settings-vive_emulation-description = 模擬 Vive 追蹤器的腰部追蹤失效問題。（ 註：這是一個玩笑功能，會降低追蹤品質。）
settings-general-fk_settings-vive_emulation-label = 開啟 Vive 模擬

## Gesture control settings (tracker tapping)

settings-general-gesture_control = 手勢控制
settings-general-gesture_control-subtitle = 敲擊重置
settings-general-gesture_control-description = 使用敲擊追蹤器的方法觸發重置。敲擊軀幹所配戴的最高的追蹤器會啟用左右偏擺重置，敲擊左腳配戴最高的追蹤器會觸發完整重置，敲擊右腳配戴最高的追蹤器會觸發配戴重置。請注意，需要在 0.6 秒內滿足敲擊次數才會觸發。
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
       *[other] { $amount } 次敲擊
    }
settings-general-gesture_control-yawResetEnabled = 敲擊以左右偏擺重置
settings-general-gesture_control-yawResetDelay = 左右偏擺重置延遲
settings-general-gesture_control-yawResetTaps = 左右偏擺重置敲擊次數
settings-general-gesture_control-fullResetEnabled = 敲擊以完整重置
settings-general-gesture_control-fullResetDelay = 完整重置延遲
settings-general-gesture_control-fullResetTaps = 完整重置敲擊次數
settings-general-gesture_control-mountingResetEnabled = 敲擊以配戴重置
settings-general-gesture_control-mountingResetDelay = 重置佩戴延遲
settings-general-gesture_control-mountingResetTaps = 重置佩戴敲擊次數

## Interface settings

settings-general-interface = 使用者介面
settings-general-interface-dev_mode = 開發者模式
settings-general-interface-dev_mode-description = 如果你需要深入的資料或與連線的追蹤器進行進階互動，開啟此模式將會非常有用。
settings-general-interface-dev_mode-label = 開發者模式
settings-general-interface-serial_detection = 串列埠裝置檢測
settings-general-interface-serial_detection-description = 每次插入新串列埠的裝置（可能是追蹤器）時，此選項會顯示一個彈出視窗。這有助於改進追蹤器的設定流程。
settings-general-interface-serial_detection-label = 串列埠裝置檢測
settings-general-interface-feedback_sound = 聲音回饋
settings-general-interface-feedback_sound-description = 啟用本選項後，觸發重置時會發出提示音
settings-general-interface-feedback_sound-label = 聲音回饋
settings-general-interface-feedback_sound-volume = 聲音回饋音量
settings-general-interface-theme = 佈景主題色彩
settings-general-interface-lang = 選擇語言
settings-general-interface-lang-description = 更改要使用的預設語言
settings-general-interface-lang-placeholder = 選擇要使用的語言

## Serial settings

settings-serial = 串列埠終端
# This cares about multilines
settings-serial-description = 這裡用於顯示串列埠的即時資訊，有助於瞭解韌體是否發生問題。
settings-serial-connection_lost = 串列埠連線中斷，正在重新連線……
settings-serial-reboot = 重新啟動
settings-serial-factory_reset = 恢復出廠設定
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>警告：</b>本選項會將該追蹤器恢復出廠設定，
    亦即其 Wi-Fi 與追蹤器校正的設定<b>將會全部刪除</b>。
settings-serial-factory_reset-warning-ok = 我確實要執行出廠設定
settings-serial-factory_reset-warning-cancel = 取消
settings-serial-get_infos = 取得資訊
settings-serial-serial_select = 選擇串列埠
settings-serial-auto_dropdown_item = 自動

## OSC router settings

settings-osc-router = OSC 路由
# This cares about multilines
settings-osc-router-description =
    從另一個程式轉發 OSC 訊息。
    例如在 VRChat 同時使用另一個 OSC 程式時，此功能會有幫助。
settings-osc-router-enable = 啟用
settings-osc-router-enable-description = 切換轉發 OSC 訊息。
settings-osc-router-enable-label = 啟用
settings-osc-router-network = 連接埠
# This cares about multilines
settings-osc-router-network-description =
    設定用於監聽和傳送資料的連接埠，
    可以與 SlimeVR 伺服器中使用的其他埠號相同。
settings-osc-router-network-port_in =
    .label = 輸入埠
    .placeholder = 輸入埠（預設：9002）
settings-osc-router-network-port_out =
    .label = 輸出埠
    .placeholder = 輸出埠（預設：9000）
settings-osc-router-network-address = 網路地址
settings-osc-router-network-address-description = 設置用來發送資料的位址。
settings-osc-router-network-address-placeholder = IPV4 地址

## OSC VRChat settings

settings-osc-vrchat = VRChat OSC 追蹤器
# This cares about multilines
settings-osc-vrchat-description =
    此處可更改 VRChat 專用的設定以取得頭戴顯示器的資料，並傳送追蹤器
    資料以進行全身追蹤，不須透過 SteamVR（例如 Quest 單機版本）。
settings-osc-vrchat-enable = 啟用
settings-osc-vrchat-enable-description = 切換資料的傳送和接收。
settings-osc-vrchat-enable-label = 啟用
settings-osc-vrchat-network = 連接埠
settings-osc-vrchat-network-description = 設定與 VRChat 監聽和傳送資料的連接埠。
settings-osc-vrchat-network-port_in =
    .label = 輸入埠
    .placeholder = 輸入埠（預設：9001）
settings-osc-vrchat-network-port_out =
    .label = 輸出埠
    .placeholder = 輸出埠（預設：9000）
settings-osc-vrchat-network-address = 網路位址
settings-osc-vrchat-network-address-description = 設定用來發送資料到 VRChat 的位址（請檢查裝置的 Wi-Fi 設定）。
settings-osc-vrchat-network-address-placeholder = VRChat IP 位址
settings-osc-vrchat-network-trackers = 追蹤器
settings-osc-vrchat-network-trackers-description = 切換傳送指定追蹤器的資料。
settings-osc-vrchat-network-trackers-chest = 胸部
settings-osc-vrchat-network-trackers-hip = 臀部
settings-osc-vrchat-network-trackers-knees = 膝蓋
settings-osc-vrchat-network-trackers-feet = 腳部
settings-osc-vrchat-network-trackers-elbows = 肘部

## VMC OSC settings

settings-osc-vmc = Virtual Motion Capture
# This cares about multilines
settings-osc-vmc-description =
    修改 VMC (Virtual Motion Capture) 協定的相關設定
    以傳送 SlimeVR 的骨骼資料，並接收來自其他應用程式的骨骼資料
settings-osc-vmc-enable = 啟用
settings-osc-vmc-enable-description = 切換資料的傳送和接收。
settings-osc-vmc-enable-label = 啟用
settings-osc-vmc-network = 連接埠
settings-osc-vmc-network-description = 設定用於監聽和傳送 VMC 資料的連接埠
settings-osc-vmc-network-port_in =
    .label = 輸入埠
    .placeholder = 輸入埠（預設：39540）
settings-osc-vmc-network-port_out =
    .label = 輸出埠
    .placeholder = 輸出埠（預設：39539）
settings-osc-vmc-network-address = 網路地址
settings-osc-vmc-network-address-description = 設定用來發送 VMC 資料的位址
settings-osc-vmc-network-address-placeholder = IPV4 地址
settings-osc-vmc-vrm = VRM 模型
settings-osc-vmc-vrm-description = 載入 VRM 模型以允許頭部錨定，並增進與其他應用程式的相容性
settings-osc-vmc-vrm-model_unloaded = 未載入模型
settings-osc-vmc-vrm-model_loaded =
    { $titled ->
        [true] 已載入模型：{ $name }
       *[other] 已載入未命名的模型
    }
settings-osc-vmc-vrm-file_select = 拖曳檔案或 <u>瀏覽檔案</u> 以載入模型
settings-osc-vmc-anchor_hip = 臀部錨定
settings-osc-vmc-anchor_hip-description = 將追蹤錨定在臀部，有利於坐姿進行虛擬直播。若本選項無法切換，請載入 VRM 模型。
settings-osc-vmc-anchor_hip-label = 臀部錨定

## Setup/onboarding menu

onboarding-skip = 跳過設定
onboarding-continue = 繼續
onboarding-wip = 施工中
onboarding-previous_step = 上一步
onboarding-setup_warning =
    <b>警告：</b>若要有良好的追蹤效果，必須進行初始設定，
    如果這是您是第一次使用 SlimeVR，請繼續進行設定。
onboarding-setup_warning-skip = 跳過設定
onboarding-setup_warning-cancel = 繼續設定

## Wi-Fi setup

onboarding-wifi_creds-back = 返回簡介
onboarding-wifi_creds = 輸入 Wi-Fi 認證資訊
# This cares about multilines
onboarding-wifi_creds-description =
    追蹤器將使用該認證資訊以進行無線連接，
    請使用目前連接中的認證資訊。
onboarding-wifi_creds-skip = 跳過 Wi-Fi 設定
onboarding-wifi_creds-submit = 送出！
onboarding-wifi_creds-ssid =
    .label = Wi-Fi 名稱
    .placeholder = 請輸入 Wi-Fi 名稱
onboarding-wifi_creds-password =
    .label = 密碼
    .placeholder = 輸入密碼

## Mounting setup

onboarding-reset_tutorial-back = 返回到配戴校正
onboarding-reset_tutorial = 重置教學
onboarding-reset_tutorial-description = 此功能尚未開發完成，請點選繼續即可。

## Setup start

onboarding-home = 歡迎來到 SlimeVR
onboarding-home-start = 來開始設定吧！

## Enter VR part of setup

onboarding-enter_vr-back = 返回到追蹤器分配
onboarding-enter_vr-title = 該是進入 VR 的時候了！
onboarding-enter_vr-description = 穿戴好所有的追蹤器，開始快樂 VR 吧！
onboarding-enter_vr-ready = 我準備好了

## Setup done

onboarding-done-title = 都搞定啦！
onboarding-done-description = 享受你的全身追蹤體驗吧
onboarding-done-close = 關閉設定

## Tracker connection setup

onboarding-connect_tracker-back = 返回到 Wi-Fi 認證資訊設定
onboarding-connect_tracker-title = 連接追蹤器
onboarding-connect_tracker-description-p0 = 來到了我第二喜歡的環節，連線所有的追蹤器！
onboarding-connect_tracker-description-p1 = 只需透過 USB 連線所有尚未連線的裝置即可。
onboarding-connect_tracker-issue-serial = 我在連接時碰到問題了！
onboarding-connect_tracker-usb = USB 追蹤器
onboarding-connect_tracker-connection_status-none = 正在尋找追蹤器
onboarding-connect_tracker-connection_status-serial_init = 正在連線到序列裝置
onboarding-connect_tracker-connection_status-provisioning = 正在傳送 Wi-Fi 認證資訊
onboarding-connect_tracker-connection_status-connecting = 正在傳送 Wi-Fi 資訊
onboarding-connect_tracker-connection_status-looking_for_server = 正在尋找伺服器
onboarding-connect_tracker-connection_status-connection_error = 無法連線到 Wi-Fi
onboarding-connect_tracker-connection_status-could_not_find_server = 未尋找到可用的伺服器
onboarding-connect_tracker-connection_status-done = 已連線到伺服器
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
onboarding-connect_tracker-next = 所有的追蹤器都連接好了

## Tracker calibration tutorial

onboarding-calibration_tutorial = IMU 校正教學
onboarding-calibration_tutorial-subtitle = 進行這項操作可以有效減少追蹤器發生飄移的機會
onboarding-calibration_tutorial-description = 每次在打開追蹤器的開關時，需要將追蹤器放置在平面一會兒來進行自動校正。您也可以透過按下“校正”按鈕來進行手動校正，<b>校正過程中請勿移動追蹤器</b>。
onboarding-calibration_tutorial-calibrate = 追蹤器已經放置在桌上了
onboarding-calibration_tutorial-status-waiting = 正在等待您完成動作
onboarding-calibration_tutorial-status-calibrating = 校正中
onboarding-calibration_tutorial-status-success = 很好，校正完成了！
onboarding-calibration_tutorial-status-error = 追蹤器移動了

## Tracker assignment setup

onboarding-assign_trackers-back = 返回到 Wi-Fi 認證資訊設定
onboarding-assign_trackers-title = 分配追蹤器
onboarding-assign_trackers-description = 這些追蹤器要放在身上的哪個部位呢？請點選要放置追蹤器的部位
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned = { $assigned }/{ $trackers } 個追蹤器已分配
onboarding-assign_trackers-advanced = 顯示進階分配部位
onboarding-assign_trackers-next = 所有的追蹤器都分配好了

## Tracker assignment warnings

# Note for devs, number is used for representing boolean states per bit.
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_FOOT =
    { $unassigned ->
        [0] 左腳已分配，但您還需要分配左腳踝、左大腿和胸部、臀部或腰部！
        [1] 左腳已分配，但您還需要分配左大腿和胸部、臀部或腰部！
        [2] 左腳已分配，但您還需要分配左腳踝和胸部、臀部或腰部！
        [3] 左腳已分配，但您還需要分配胸部、臀部或腰部！
        [4] 左腳已分配，但您還需要分配左腳踝和左大腿！
        [5] 左腳已分配，但您還需要分配左大腿！
        [6] 左腳已分配，但您還需要分配左腳踝！
       *[unknown] 左腳已分配，但您還需要分配其它未分配的身體部位！
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_FOOT =
    { $unassigned ->
        [0] 右腳已分配，但您還需要分配右腳踝、右大腿以及胸部、臀部或腰部！
        [1] 右腳已分配，但您還需要分配右大腿和胸部、臀部或腰部！
        [2] 右腳已分配，但您還需要分配右腳踝和胸部、臀部或腰部！
        [3] 右腳已分配，但您還需要分配胸部、臀部或腰部！
        [4] 右腳已分配，但您還需要分配右腳踝和右大腿！
        [5] 右腳已分配，但您還需要分配右大腿！
        [6] 右腳已分配，但您還需要分配右腳踝！
       *[unknown] 右腳已分配，但您還需要分配其它未分配的身體部位！
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_LOWER_LEG =
    { $unassigned ->
        [0] 左腳踝已分配，但您還需要分配左大腿和胸部、臀部或腰部！
        [1] 左腳踝已分配，但您還需要分配胸部、臀部或腰部！
        [2] 左腳踝已分配，但您還需要分配左大腿！
       *[unknown] 左腳踝已分配，但您還需要分配其它未分配的身體部位！
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_LOWER_LEG =
    { $unassigned ->
        [0] 右腳踝已分配，但您還需要分配右大腿和胸部、臀部或腰部！
        [1] 右腳踝已分配，但您還需要分配胸部、臀部或腰部！
        [2] 右腳踝已分配，但您還需要分配右大腿！
       *[unknown] 右腳踝已分配，但您還需要分配其它未分配的身體部位！
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_UPPER_LEG =
    { $unassigned ->
        [0] 左大腿已分配，但您還需要分配胸部、臀部或腰部！
       *[unknown] 左大腿已分配，但您還需要分配其它未分配的身體部位！
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_UPPER_LEG =
    { $unassigned ->
        [0] 右大腿已分配，但您還需要分配胸部、臀部或腰部！
       *[unknown] 右大腿已分配，但您還需要分配其它未分配的身體部位！
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-HIP =
    { $unassigned ->
        [0] 臀部已分配，但您還需要分配胸部！
       *[unknown] 臀部已分配，但您還需要分配其它未分配的身體部位！
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-WAIST =
    { $unassigned ->
        [0] 腰部已分配，但您還需要分配胸部！
       *[unknown] 腰部已分配，但您還需要分配其它未分配的身體部位！
    }

## Tracker mounting method choose

onboarding-choose_mounting = 要使用哪一種配戴校正方式？
onboarding-choose_mounting-auto_mounting = 自動配戴校正
# Italized text
onboarding-choose_mounting-auto_mounting-subtitle = 推薦使用
onboarding-choose_mounting-auto_mounting-description = 本選項會透過兩個身體姿勢，判斷所有追蹤器的配戴方位
onboarding-choose_mounting-manual_mounting = 手動配戴校正
# Italized text
onboarding-choose_mounting-manual_mounting-subtitle = 如果你清楚你要做什麼的話
onboarding-choose_mounting-manual_mounting-description = 本選項可以讓你選擇每個追蹤器的配戴方位

## Tracker manual mounting setup

onboarding-manual_mounting-back = 返回到進入 VR
onboarding-manual_mounting = 手動配戴
onboarding-manual_mounting-description = 點選每個追蹤器並選擇它們的配戴方式
onboarding-manual_mounting-auto_mounting = 進行自動設定
onboarding-manual_mounting-next = 下一步

## Tracker automatic mounting setup

onboarding-automatic_mounting-back = 返回到進入 VR
onboarding-automatic_mounting-title = 配戴校正
onboarding-automatic_mounting-description = 為了讓 SlimeVR 追蹤器正常運作，我們需要為你的追蹤器設定一個配戴方向，以使其與你的物理追蹤器配戴方式對齊。
onboarding-automatic_mounting-manual_mounting = 進行手動設定
onboarding-automatic_mounting-next = 下一步
onboarding-automatic_mounting-prev_step = 上一步
onboarding-automatic_mounting-done-title = 配戴方向已校正。
onboarding-automatic_mounting-done-description = 你的配戴方向校準完成！
onboarding-automatic_mounting-done-restart = 返回以開始
onboarding-automatic_mounting-mounting_reset-title = 配戴重置
onboarding-automatic_mounting-mounting_reset-step-0 = 1. 雙腿彎曲以滑雪的姿勢蹲下，上身向前傾斜，手臂彎曲。
onboarding-automatic_mounting-mounting_reset-step-1 = 2. 按下「配戴重置」按鈕並等待 3 秒鐘，追蹤器的配戴方向將被重置。
onboarding-automatic_mounting-preparation-title = 準備
onboarding-automatic_mounting-preparation-step-0 = 1. 身體直立，雙臂放在身體兩側。
onboarding-automatic_mounting-preparation-step-1 = 2. 按下「完整重置」按鈕，等待 3 秒鐘，追蹤器將會重置。
onboarding-automatic_mounting-put_trackers_on-title = 請戴好追蹤器
onboarding-automatic_mounting-put_trackers_on-description = 為了校準配戴方向，我們將使用剛才分配的追蹤器。戴上你所有的追蹤器，你可以在右邊的圖中看到追蹤器的對應部位。
onboarding-automatic_mounting-put_trackers_on-next = 我所有的追蹤器都戴好了！

## Tracker proportions method choose

onboarding-choose_proportions = 要使用哪一種軀幹比例的校正方式？
onboarding-choose_proportions-auto_proportions = 自動軀幹比例校正
# Italized text
onboarding-choose_proportions-auto_proportions-subtitle = 推薦使用
onboarding-choose_proportions-auto_proportions-description = 本選項會透過演算法，分析身體的移動來推算軀幹比例
onboarding-choose_proportions-manual_proportions = 手動軀幹比例校正
# Italized text
onboarding-choose_proportions-manual_proportions-subtitle = 適合進行微調
onboarding-choose_proportions-manual_proportions-description = 本選項可以讓你直接修改軀幹比例的設定值

## Tracker manual proportions setup

onboarding-manual_proportions-back = 返回重置教學
onboarding-manual_proportions-title = 手動調整軀幹比例
onboarding-manual_proportions-precision = 精確調整
onboarding-manual_proportions-auto = 進行自動校正
onboarding-manual_proportions-ratio = 依比例分組調整

## Tracker automatic proportions setup

onboarding-automatic_proportions-back = 返回重置教學
onboarding-automatic_proportions-title = 測量你的身體比例
onboarding-automatic_proportions-description = 為了讓 SlimeVR 追蹤器正常使用，我們需要知道你的骨頭長度。這個簡短的流程將會進行這方面的測量。
onboarding-automatic_proportions-manual = 進行手動校正
onboarding-automatic_proportions-prev_step = 上一步
onboarding-automatic_proportions-put_trackers_on-title = 請戴好追蹤器
onboarding-automatic_proportions-put_trackers_on-description = 為了校準你的軀幹比例，我們將使用你剛才分配的追蹤器。戴上你所有的追蹤器，你可以在右邊的圖中看到追蹤器的對應部位。
onboarding-automatic_proportions-put_trackers_on-next = 我所有的追蹤器都戴好了！
onboarding-automatic_proportions-requirements-title = 使用需求
# Each line of text is a different list item
onboarding-automatic_proportions-requirements-description =
    你需要足夠的追蹤器以進行足部追蹤（通常為 5 個）。
    你已經打開追蹤器與頭戴顯示器的電源。
    你需要穿戴上追蹤器與頭戴顯示器。
    你的追蹤器與頭戴顯示器都已經連接到 SlimeVR 伺服器。
    你的追蹤器與頭戴顯示器在 SlimeVR 伺服器中運作正常。
    你的頭戴顯示器會回報定位資料給 SlimeVR 伺服器（通常為執行 SteamVR 並透過 SlimeVR 的 SteamVR 附加元件來連接 SlimeVR）。
onboarding-automatic_proportions-requirements-next = 我已閱讀使用需求
onboarding-automatic_proportions-start_recording-title = 準備擺動作囉
onboarding-automatic_proportions-start_recording-description = 我們現在要記錄一些特定的姿勢和動作，將會在下一個畫面中提示。當按鈕被按下時，準備好開始！
onboarding-automatic_proportions-start_recording-next = 開始錄製
onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = 正在錄製中……
onboarding-automatic_proportions-recording-description-p1 = 請做出以下動作:
# Each line of text is a different list item
onboarding-automatic_proportions-recording-steps =
    站直，頭部繞圈轉動。
    腰部向前伸，腿部彎曲，呈半蹲姿勢後，頭部轉動向左看，再向右看。
    上身向左（逆時針）扭動後，向前頃，使右半身朝向地面。
    上身向右（順時針）扭動後，向前頃，使左半身朝向地面。
    以圓形軌跡扭動臀部，如同使用呼拉圈的姿勢。
    若還有剩餘時間，可以重複進行以上動作。
onboarding-automatic_proportions-recording-processing = 正在處理結果
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 20)
onboarding-automatic_proportions-recording-timer = 剩餘 { $time } 秒
onboarding-automatic_proportions-verify_results-title = 檢查結果
onboarding-automatic_proportions-verify_results-description = 檢查下面的結果，它們看起來是正確的嗎？
onboarding-automatic_proportions-verify_results-results = 錄製結果
onboarding-automatic_proportions-verify_results-processing = 正在處理結果
onboarding-automatic_proportions-verify_results-redo = 重新錄製
onboarding-automatic_proportions-verify_results-confirm = 他們是正確的！
onboarding-automatic_proportions-done-title = 身體資料已測量並儲存。
onboarding-automatic_proportions-done-description = 你的身體比例校正已完成！

## Home

home-no_trackers = 未偵測到或未分配追蹤器
