### SlimeVR complete GUI translations
# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means

## Websocket (server) status
websocket-connecting = 正在連接到伺服器
websocket-connection_lost = 與伺服器的連接丟失，正在嘗試重新連接...

## Tips
tips-find_tracker = 不確定哪個追蹤器是哪個？搖動一個追蹤器，對應的那個將在螢幕上高亮顯示。
tips-do_not_move_heels = 確保你的腳跟在錄製的時候不會發生移動!

## Body parts
body_part-NONE = 未分配
body_part-HEAD = 頭部
body_part-NECK = 頸部
body_part-RIGHT_SHOULDER = 右肩
body_part-RIGHT_UPPER_ARM = 右大臂
body_part-RIGHT_LOWER_ARM = 右小臂
body_part-RIGHT_HAND = 右手
body_part-RIGHT_UPPER_LEG = 右大腿
body_part-RIGHT_LOWER_LEG = 右小腿
body_part-RIGHT_FOOT = 右腳
body_part-RIGHT_CONTROLLER = 右控制器
body_part-CHEST = 胸部
body_part-WAIST = 腰部
body_part-HIP = 髖部
body_part-LEFT_SHOULDER = 左肩
body_part-LEFT_UPPER_ARM = 左大臂
body_part-LEFT_LOWER_ARM = 左小臂
body_part-LEFT_HAND = 左手
body_part-LEFT_UPPER_LEG = 左大腿
body_part-LEFT_LOWER_LEG = 左小腿
body_part-LEFT_FOOT = 左腳
body_part-LEFT_CONTROLLER = 左控制器

## Proportions
skeleton_bone-NONE = 無
skeleton_bone-HEAD = 頭部偏移
skeleton_bone-NECK = 頸部長度
skeleton_bone-CHEST = 胸部長度
skeleton_bone-CHEST_OFFSET = 胸部偏移
skeleton_bone-WAIST = 腰部長度
skeleton_bone-HIP = 髖部長度
skeleton_bone-HIP_OFFSET = 髖部偏移
skeleton_bone-HIPS_WIDTH = 髖部寬度
skeleton_bone-UPPER_LEG = 大腿長度
skeleton_bone-LOWER_LEG = 小腿長度
skeleton_bone-FOOT_LENGTH = 腳部長度
skeleton_bone-FOOT_SHIFT = 腳部偏移
skeleton_bone-SKELETON_OFFSET = 骨骼偏移
skeleton_bone-SHOULDERS_DISTANCE = 肩膀距離
skeleton_bone-SHOULDERS_WIDTH = 肩膀寬度
skeleton_bone-UPPER_ARM = 上臂長度
skeleton_bone-LOWER_ARM = 前臂距離
skeleton_bone-CONTROLLER_Y = 控制器距離 Y
skeleton_bone-CONTROLLER_Z = 控制器距離 Z
skeleton_bone-ELBOW_OFFSET = 肘部偏移

## Tracker reset buttons
reset-reset_all = 重置身體比例
reset-full = 重置
reset-mounting = 重置佩戴
reset-quick = 快速重置

## Serial detection stuff
serial_detection-new_device-p0 = 檢測到了新的串口設備!
serial_detection-new_device-p1 = 輸入你的 Wi-Fi 憑據!
serial_detection-new_device-p2 = 請選擇你想對它做什麼
serial_detection-open_wifi = 連接到 Wi-Fi
serial_detection-open_serial = 打開串口控制台
serial_detection-submit = 提交!
serial_detection-close = 關閉

## Navigation bar
navbar-home = 主頁
navbar-body_proportions = 身體比例
navbar-trackers_assign = 追蹤器分配
navbar-mounting = 佩戴校準
navbar-onboarding = 嚮導
navbar-settings = 設置

## Bounding volume hierarchy recording
bvh-start_recording = 錄製 BVH 檔
bvh-recording = 錄製中...

## Widget: Overlay settings
widget-overlay = 覆蓋層
widget-overlay-is_visible_label = 在 SteamVR 中顯示覆蓋層
widget-overlay-is_mirrored_label = 鏡像顯示覆蓋層

## Widget: Developer settings
widget-developer_mode = 開發者選項
widget-developer_mode-high_contrast = 高對比
widget-developer_mode-precise_rotation = 顯示精確旋轉
widget-developer_mode-fast_data_feed = 快速資料更新
widget-developer_mode-filter_slimes_and_hmd = 對追蹤器和HMD應用濾波
widget-developer_mode-sort_by_name = 根據名稱排序
widget-developer_mode-raw_slime_rotation = 顯示原始旋轉
widget-developer_mode-more_info = 顯示更多資訊

## Widget: IMU Visualizer
widget-imu_visualizer = 旋轉
widget-imu_visualizer-rotation_raw = 原始旋轉
widget-imu_visualizer-rotation_preview = 預覽

## Tracker status
tracker-status-none = 無狀態
tracker-status-busy = 繁忙
tracker-status-error = 錯誤
tracker-status-disconnected = 斷開連接
tracker-status-occluded = 被遮擋
tracker-status-ok = 已連接

## Tracker status columns
tracker-table-column-name = 名字
tracker-table-column-type = 類型
tracker-table-column-battery = 電量
tracker-table-column-ping = 延遲
tracker-table-column-tps = TPS
tracker-table-column-temperature = 溫度 °C
tracker-table-column-linear-acceleration = 加速度 X/Y/Z
tracker-table-column-rotation = 旋轉 X/Y/Z
tracker-table-column-position = 位置 X/Y/Z
tracker-table-column-url = 地址

## Tracker rotation
tracker-rotation-front = 前
tracker-rotation-left = 左
tracker-rotation-right = 右
tracker-rotation-back = 後

## Tracker information
tracker-infos-manufacturer = 製造商
tracker-infos-display_name = 顯示名稱
tracker-infos-custom_name = 自訂名稱
tracker-infos-url = 追蹤器地址

## Tracker settings
tracker-settings-back = 返回追蹤器列表
tracker-settings-title = 追蹤器設置
tracker-settings-assignment_section = 分配追蹤器
tracker-settings-assignment_section-description = 該追蹤器要被分配到身體的哪個部位？
tracker-settings-assignment_section-edit = 編輯分配
tracker-settings-mounting_section = 佩戴位置
tracker-settings-mounting_section-description = 該追蹤器被佩戴在哪裡？
tracker-settings-mounting_section-edit = 編輯佩戴
tracker-settings-drift_compensation_section = 允許漂移補償
tracker-settings-drift_compensation_section-description = 是否在此追蹤器上應用漂移補償？
tracker-settings-drift_compensation_section-edit = 允許漂移補償
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = 追蹤器名稱
tracker-settings-name_section-description = 給它起一個可愛的名字吧 :)
tracker-settings-name_section-placeholder = 白白貓的左腳

## Tracker part card info
tracker-part_card-no_name = 未命名
tracker-part_card-unassigned = 未分配

## Body assignment menu
body_assignment_menu = 你想將此追蹤器戴在哪裡？
body_assignment_menu-description = 選擇要將此追蹤器分配到的位置，或者你也可以選擇一次管理所有追蹤器，而不是逐一管理。
body_assignment_menu-show_advanced_locations = 顯示高級分配位置
body_assignment_menu-manage_trackers = 管理所有追蹤器
body_assignment_menu-unassign_tracker = 取消分配追蹤器

## Tracker assignment menu
# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = 哪個追蹤器將被分配到你的
tracker_selection_menu-NONE = 你想將哪個追蹤器解除配置？
tracker_selection_menu-HEAD = { -tracker_selection-part }頭部？
tracker_selection_menu-NECK = { -tracker_selection-part }頸部？
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part }右肩？
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part }右大臂？
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part }右小臂？
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part }右手？
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part }右大腿？
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part }右小腿？
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part }右腳？
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part }右控制器？
tracker_selection_menu-CHEST = { -tracker_selection-part }胸部？
tracker_selection_menu-WAIST = { -tracker_selection-part }腰部？
tracker_selection_menu-HIP = { -tracker_selection-part }髖部？
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part }左肩？
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part }左大臂？
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part }左小臂？
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part }左手？
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part }左大腿？
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part }左小腿？
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part }左腳？
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part }左控制器？

tracker_selection_menu-unassigned = 未分配的追蹤器
tracker_selection_menu-assigned = 已分配的追蹤器
tracker_selection_menu-dont_assign = 不分配

## Mounting menu
mounting_selection_menu = 你想將此追蹤器戴在哪裡？
mounting_selection_menu-close = 關閉

## Sidebar settings
settings-sidebar-title = 設置
settings-sidebar-general = 通用設置
settings-sidebar-tracker_mechanics = 追蹤器設置
settings-sidebar-fk_settings = FK 設置
settings-sidebar-gesture_control = 手勢控制
settings-sidebar-interface = 交互介面
settings-sidebar-osc_router = OSC 路由
settings-sidebar-utils = 工具
settings-sidebar-serial = 串口控制台

## SteamVR settings
settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = SteamVR 追蹤器
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    啟用或禁用特定的追蹤器
    對於只支援特定追蹤器的應用會很有用。
settings-general-steamvr-trackers-waist = 腰部
settings-general-steamvr-trackers-chest = 胸部
settings-general-steamvr-trackers-feet = 腳部
settings-general-steamvr-trackers-knees = 膝蓋
settings-general-steamvr-trackers-elbows = 肘部
settings-general-steamvr-trackers-hands = 手部

## Tracker mechanics
settings-general-tracker_mechanics = 追蹤器設置
settings-general-tracker_mechanics-filtering = 濾波
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    選擇追蹤器的濾波類型。
    預測型將會對你的運動加以預測，而平滑型將會讓你的運動更加平滑。
settings-general-tracker_mechanics-filtering-type = 濾波類型
settings-general-tracker_mechanics-filtering-type-none = 無濾波
settings-general-tracker_mechanics-filtering-type-none-description = 使用原始資料，不進行濾波。
settings-general-tracker_mechanics-filtering-type-smoothing = 平滑型
settings-general-tracker_mechanics-filtering-type-smoothing-description = 讓運動更加平滑，但會增加一些延遲。
settings-general-tracker_mechanics-filtering-type-prediction = 預測型
settings-general-tracker_mechanics-filtering-type-prediction-description = 減少延遲並使移動更敏捷，但可能會增加一些抖動。
settings-general-tracker_mechanics-filtering-amount = 濾波強度
settings-general-tracker_mechanics-drift_compensation = 漂移補償
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    應用反向旋轉以補償IMU的偏航角漂移。
    更改補償量和使用多少次的重置結果用於計算補償量。
settings-general-tracker_mechanics-drift_compensation-enabled-label = 漂移補償
settings-general-tracker_mechanics-drift_compensation-amount-label = 補償量
settings-general-tracker_mechanics-drift_compensation-max_resets-label = 使用幾次的重置結果？

## FK/Tracking settings
settings-general-fk_settings = FK 設置
settings-general-fk_settings-leg_tweak = 腿部調整
settings-general-fk_settings-leg_tweak-description = 本設置可以減少甚至消除腳部穿入地板的情況，但是當你跪在地上的時候可能產生一些問題. 腳滑矯正可以矯正一些腳滑溜冰的問題, 但是可能會降低某些動作的準確性。
# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = 地板限制
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = 腳滑矯正
settings-general-fk_settings-leg_tweak-skating_correction-amount = 腳滑矯正量
settings-general-fk_settings-arm_fk = 手臂 FK
settings-general-fk_settings-arm_fk-description = 更改手臂的追蹤方式。
settings-general-fk_settings-arm_fk-force_arms = 強制從頭顯獲得資料
settings-general-fk_settings-skeleton_settings = 骨架設置
settings-general-fk_settings-skeleton_settings-description = 打開或關閉骨架設置。建議保持這些設置不變。
settings-general-fk_settings-skeleton_settings-extended_spine = 脊柱延伸
settings-general-fk_settings-skeleton_settings-extended_pelvis = 骨盆延伸
settings-general-fk_settings-skeleton_settings-extended_knees = 膝蓋延伸
settings-general-fk_settings-vive_emulation-title = Vive 模擬
settings-general-fk_settings-vive_emulation-description = 模擬Vive追蹤器的腰部丟追問題。（ 注：這是一個玩笑功能，會劣化追蹤品質。）
settings-general-fk_settings-vive_emulation-label = 開啟 Vive 模擬

## Gesture control settings (tracker tapping)
settings-general-gesture_control = 手勢控制
settings-general-gesture_control-subtitle = 敲擊重置
settings-general-gesture_control-description = 啟用或禁用敲擊重置。啟用時，敲擊身上佩戴的最高的追蹤器上的任何位置將啟動快速重置。延遲是指記錄到敲擊和重置之間的時間。
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps = { $amount ->
    *[other] { $amount }次敲擊
}
settings-general-gesture_control-quickResetEnabled = 開啟敲擊快速重置
settings-general-gesture_control-quickResetDelay = 敲擊快速重置延遲
settings-general-gesture_control-quickResetTaps = 快速重置敲擊次數
settings-general-gesture_control-resetEnabled = 開啟敲擊重置
settings-general-gesture_control-resetDelay = 敲擊重置延遲
settings-general-gesture_control-resetTaps = 重置敲擊次數
settings-general-gesture_control-mountingResetEnabled = 開啟敲擊重置佩戴
settings-general-gesture_control-mountingResetDelay = 敲擊重置佩戴延遲
settings-general-gesture_control-mountingResetTaps = 重置佩戴敲擊次數

## Interface settings
settings-general-interface = 交互介面
settings-general-interface-dev_mode = 開發者模式
settings-general-interface-dev_mode-description = 如果你需要深入的資料或更深入地與連接的追蹤器進行交互，打開此模式將會非常有用。
settings-general-interface-dev_mode-label = 開發者模式
settings-general-interface-serial_detection = 串口設備檢測
settings-general-interface-serial_detection-description = 每次插入可能是追蹤器的新串口設備時，此選項都會顯示一個快顯視窗。這有助於改進追蹤器的設置過程。
settings-general-interface-serial_detection-label = 串口設備檢測
settings-general-interface-lang = 選擇語言
settings-general-interface-lang-description = 更改要使用的預設語言
settings-general-interface-lang-placeholder = 選擇要使用的語言

## Serial settings
settings-serial = 串口控制台
# This cares about multilines
settings-serial-description =
    這裡用於顯示串口的即時資訊流。
    如果你需要瞭解固件是否出現問題，這將會很有用。
settings-serial-connection_lost = 串口連接丟失，正在重新連接..
settings-serial-reboot = 重新啟動
settings-serial-factory_reset = 恢復出廠設置
settings-serial-get_infos = 獲取資訊
settings-serial-serial_select = 選擇序列埠
settings-serial-auto_dropdown_item = 自動

## OSC router settings
settings-osc-router = OSC 路由
# This cares about multilines
settings-osc-router-description =
    從另一個程式轉發 OSC 資訊。
    在使用另一個 VRChat 的 OSC 程式時會很有用。
settings-osc-router-enable = 啟用
settings-osc-router-enable-description = 控制資訊轉發功能的開關。
settings-osc-router-enable-label = 啟用
settings-osc-router-network = 網路埠
# This cares about multilines
settings-osc-router-network-description =
    設置用於監聽和向 VRChat 發送資料的埠
    這些埠可以與 SlimeVR 伺服器中使用的其他埠相同。
settings-osc-router-network-port_in =
    .label = 輸入埠
    .placeholder = 輸入埠（默認 9002）
settings-osc-router-network-port_out =
    .label = 輸出埠
    .placeholder = 輸出埠（默認 9000）
settings-osc-router-network-address = 網路位址
settings-osc-router-network-address-description = 設置用來發送資料的位址。
settings-osc-router-network-address-placeholder = IPV4 地址

## OSC VRChat settings
settings-osc-vrchat = VRChat OSC Trackers
# This cares about multilines
settings-osc-vrchat-description =
    改變 VRChat 的特定設置以接收和發送頭顯資料。
    用於全身追蹤的追蹤器資料（在 Quest 端生效）。
settings-osc-vrchat-enable = 啟用
settings-osc-vrchat-enable-description = 切換資料的發送和接收
settings-osc-vrchat-enable-label = 啟用
settings-osc-vrchat-network = 網路埠
settings-osc-vrchat-network-description = 設置用於監聽和向 VRChat 發送資料的埠
settings-osc-vrchat-network-port_in =
    .label = 輸入埠
    .placeholder = 輸入埠（默認 9001）
settings-osc-vrchat-network-port_out =
    .label = 輸出埠
    .placeholder = 輸出埠（默認 9000）
settings-osc-vrchat-network-address = 網路位址
settings-osc-vrchat-network-address-description = 選擇將資料發送到 VRChat 的位址（檢查設備上的 Wi-Fi 設置）
settings-osc-vrchat-network-address-placeholder = VRChat IP 地址
settings-osc-vrchat-network-trackers = 追蹤器
settings-osc-vrchat-network-trackers-description = 切換資料的發送和接收
settings-osc-vrchat-network-trackers-chest = 胸部
settings-osc-vrchat-network-trackers-waist = 腰部
settings-osc-vrchat-network-trackers-knees = 膝蓋
settings-osc-vrchat-network-trackers-feet = 腳部
settings-osc-vrchat-network-trackers-elbows = 肘部

## Setup/onboarding menu
onboarding-skip = 跳過設置
onboarding-continue = 繼續
onboarding-wip = 仍在開發中

## Wi-Fi setup
onboarding-wifi_creds-back = 返回簡介
onboarding-wifi_creds = 輸入 Wi-Fi 憑據
# This cares about multilines
onboarding-wifi_creds-description =
    追蹤器將使用這些憑據連接到 Wi-Fi
    請使用當前連接到 Wi-Fi 的憑據
onboarding-wifi_creds-skip = 跳過 Wi-Fi 設置
onboarding-wifi_creds-submit = 提交！
onboarding-wifi_creds-ssid =
    .label = SSID
    .placeholder = 輸入 SSID
onboarding-wifi_creds-password =
    .label = 密碼
    .placeholder = 輸入密碼

## Mounting setup
onboarding-reset_tutorial-back = 返回到佩戴校準
onboarding-reset_tutorial = 重置教程
onboarding-reset_tutorial-description = 此功能尚未開發完成，請繼續就好

## Setup start
onboarding-home = 歡迎來到 SlimeVR
# This cares about multilines and it's centered!!
onboarding-home-description =
    將全身追蹤
    帶給每一個人
onboarding-home-start = 我準備好了！

## Enter VR part of setup
onboarding-enter_vr-back = 返回到追蹤器分配
onboarding-enter_vr-title = VR 時間到！
onboarding-enter_vr-description = 穿戴好所有的追蹤器，開始快樂 VR 吧！
onboarding-enter_vr-ready = 我準備好了

## Setup done
onboarding-done-title = 都搞定啦！
onboarding-done-description = 享受你的全身追蹤體驗吧
onboarding-done-close = 關閉嚮導

## Tracker connection setup
onboarding-connect_tracker-back = 返回到 Wi-Fi 憑據設置
onboarding-connect_tracker-title = 連接追蹤器
onboarding-connect_tracker-description-p0 = 來到了我第二喜歡的環節，連接所有的追蹤器！
onboarding-connect_tracker-description-p1 = 只需通過 USB 連接所有尚未連接的設備即可。
onboarding-connect_tracker-issue-serial = QAQ 我在連接時遇到問題！
onboarding-connect_tracker-usb = USB 追蹤器
onboarding-connect_tracker-connection_status-connecting = 正在發送 Wi-Fi 憑據
onboarding-connect_tracker-connection_status-connected = Wi-Fi 已連接
onboarding-connect_tracker-connection_status-error = 無法連接到 Wi-Fi
onboarding-connect_tracker-connection_status-start_connecting = 尋找追蹤器
onboarding-connect_tracker-connection_status-handshake = 已連接到伺服器
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers = { $amount ->
    [0] 沒有已連接的追蹤器
    *[other] { $amount } 個追蹤器已連接
}
onboarding-connect_tracker-next = 所有的追蹤器都連接好了

## Tracker assignment setup
onboarding-assign_trackers-back = 返回 Wi-Fi 憑據設置
onboarding-assign_trackers-title = 分配追蹤器
onboarding-assign_trackers-description = 讓我們選擇哪個追蹤器在哪裡。按一下要放置追蹤器的部位
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned = { $assigned }/{ $trackers } 個追蹤器已分配
onboarding-assign_trackers-advanced = 顯示高級分配部位
onboarding-assign_trackers-next = 所有的追蹤器都分配好了

## Tracker manual mounting setup
onboarding-manual_mounting-back = 返回到進入 VR
onboarding-manual_mounting = 手動佩戴
onboarding-manual_mounting-description = 按一下每個追蹤器並選擇它們的配電方式
onboarding-manual_mounting-auto_mounting = 自動佩戴
onboarding-manual_mounting-next = 下一步

## Tracker automatic mounting setup
onboarding-automatic_mounting-back = 返回到進入 VR
onboarding-automatic_mounting-title = 佩戴校準
onboarding-automatic_mounting-description = 為了讓 SlimeVR 追蹤器正常工作，我們需要為你的追蹤器分配一個佩戴方向，以使其與你的物理追蹤器佩戴方式對齊。
onboarding-automatic_mounting-manual_mounting = 手動設置佩戴方向
onboarding-automatic_mounting-next = 下一步
onboarding-automatic_mounting-prev_step = 上一步
onboarding-automatic_mounting-done-title = 佩戴方向已校準。
onboarding-automatic_mounting-done-description = 你的佩戴方向校準完成！
onboarding-automatic_mounting-done-restart = 返回以開始
onboarding-automatic_mounting-mounting_reset-title = 佩戴重置
onboarding-automatic_mounting-mounting_reset-step-0 = 1. 雙腿彎曲以滑雪的姿勢蹲下，上身向前傾斜，手臂彎曲。
onboarding-automatic_mounting-mounting_reset-step-1 = 按下佩戴重新開機按鈕並等待 3 秒鐘，然後追蹤器的佩戴方向將被重置。
onboarding-automatic_mounting-preparation-title = 準備
onboarding-automatic_mounting-preparation-step-0 = 1. 身體直立，雙臂放在身體兩側。
onboarding-automatic_mounting-preparation-step-1 = 按下“重定”按鈕，等待 3 秒鐘，追蹤器將復位。
onboarding-automatic_mounting-put_trackers_on-title = 穿戴好追蹤器
onboarding-automatic_mounting-put_trackers_on-description = 為了校準佩戴方向，我們將使用你剛才分配的追蹤器。戴上你所有的追蹤器，你可以在右邊的圖中看到哪個追蹤器對應哪個。
onboarding-automatic_mounting-put_trackers_on-next = 所有的追蹤器都已開啟！

## Tracker manual proportions setup
onboarding-manual_proportions-back = 返回重置教程
onboarding-manual_proportions-title = 手動調整身體比例
onboarding-manual_proportions-precision = 精確調整
onboarding-manual_proportions-auto = 自動校準

## Tracker automatic proportions setup
onboarding-automatic_proportions-back = 返回重置教程
onboarding-automatic_proportions-title = 測量你的身體比例
onboarding-automatic_proportions-description = 為了讓 SlimeVR 追蹤器正常使用，我們需要知道你的骨頭的長度。這個簡短的校準將為你測量它們。
onboarding-automatic_proportions-manual = 手動校準
onboarding-automatic_proportions-prev_step = 上一步
onboarding-automatic_proportions-put_trackers_on-title = 穿戴好追蹤器
onboarding-automatic_proportions-put_trackers_on-description = 為了校準你的身體比例，我們將使用你剛才分配的追蹤器。戴上你所有的追蹤器，你可以在右邊的圖中看到哪個追蹤器對應哪個。
onboarding-automatic_proportions-put_trackers_on-next = 所有的追蹤器都已開啟！
onboarding-automatic_proportions-preparation-title = 準備
onboarding-automatic_proportions-preparation-description = 在你的正後方放一把椅子，並準備好在接下來的設置過程中坐下。
onboarding-automatic_proportions-preparation-next = 我在椅子前面啦
onboarding-automatic_proportions-start_recording-title = 準備錄製運動
onboarding-automatic_proportions-start_recording-description = 我們現在要記錄一些特定的姿勢和動作。這些將在下一個螢幕中提示。當按鈕被按下時，準備好開始！
onboarding-automatic_proportions-start_recording-next = 開始錄製
onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = 錄製中...
onboarding-automatic_proportions-recording-description-p1 = 依次做出以下動作:
onboarding-automatic_proportions-recording-steps-0 = 彎曲幾次膝蓋。
onboarding-automatic_proportions-recording-steps-1 = 坐在椅子上，然後站起來。
onboarding-automatic_proportions-recording-steps-2 = 向左扭轉上身，然後向右彎。
onboarding-automatic_proportions-recording-steps-3 = 向右扭轉上身，然後向左彎。
onboarding-automatic_proportions-recording-steps-4 = 持續擺動身體，直到計時器結束。
onboarding-automatic_proportions-recording-processing = 正在處理結果
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 15)
onboarding-automatic_proportions-recording-timer =  剩餘{ $time }秒
onboarding-automatic_proportions-verify_results-title = 校驗結果
onboarding-automatic_proportions-verify_results-description = 檢查下面的結果，它們看起來是正確的嗎？
onboarding-automatic_proportions-verify_results-results = 錄製結果
onboarding-automatic_proportions-verify_results-processing = 正在處理結果
onboarding-automatic_proportions-verify_results-redo = 重新錄製
onboarding-automatic_proportions-verify_results-confirm = 他們是正確的！
onboarding-automatic_proportions-done-title = 身體資料已測量並保存。
onboarding-automatic_proportions-done-description = 你的身體比例校準已完成！

## Home
home-no_trackers = 未檢測到或未分配追蹤器