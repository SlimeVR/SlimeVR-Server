### SlimeVR complete GUI translations
# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means

## Websocket (server) status
websocket-connecting = 正在连接到服务器
websocket-connection_lost = 与服务器的连接丢失，正在尝试重新连接...

## Tips
tips-find_tracker = 不确定哪个追踪器是哪个？在现实中摇动一个追踪器，对应的那个将在屏幕上高亮显示。
tips-do_not_move_heels = 确保你的脚跟在录制的时候不会发生移动!

## Body parts
body_part-NONE = 未分配
body_part-HEAD = 头部
body_part-NECK = 颈部
body_part-RIGHT_SHOULDER = 右肩
body_part-RIGHT_UPPER_ARM = 右大臂
body_part-RIGHT_LOWER_ARM = 右小臂
body_part-RIGHT_HAND = 右手
body_part-RIGHT_UPPER_LEG = 右大腿
body_part-RIGHT_LOWER_LEG = 右小腿
body_part-RIGHT_FOOT = 右脚
body_part-RIGHT_CONTROLLER = Right controller
body_part-CHEST = 胸部
body_part-WAIST = 腰部
body_part-HIP = 髋部
body_part-LEFT_SHOULDER = 左肩
body_part-LEFT_UPPER_ARM = 左大臂
body_part-LEFT_LOWER_ARM = 左小臂
body_part-LEFT_HAND = 左手
body_part-LEFT_UPPER_LEG = 左大腿
body_part-LEFT_LOWER_LEG = 左小腿
body_part-LEFT_FOOT = 左脚
body_part-LEFT_CONTROLLER = Left controller

## Skeleton stuff
skeleton_bone-NONE = 无
skeleton_bone-HEAD = 头部偏移
skeleton_bone-NECK = 颈部长度
skeleton_bone-TORSO = 躯干长度
skeleton_bone-CHEST = 胸部距离
skeleton_bone-WAIST = 腰部距离
skeleton_bone-HIP_OFFSET = 髋部偏移
skeleton_bone-HIPS_WIDTH = 髋部宽度
skeleton_bone-LEGS_LENGTH = 腿部长度
skeleton_bone-KNEE_HEIGHT = 膝盖高度
skeleton_bone-FOOT_LENGTH = 脚部长度
skeleton_bone-FOOT_SHIFT = 脚部偏移
skeleton_bone-SKELETON_OFFSET = 骨骼偏移
skeleton_bone-CONTROLLER_DISTANCE_Z = 控制器距离 Z
skeleton_bone-CONTROLLER_DISTANCE_Y = 控制器距离 Y
skeleton_bone-FOREARM_LENGTH = 前臂距离
skeleton_bone-SHOULDERS_DISTANCE = 肩膀距离
skeleton_bone-SHOULDERS_WIDTH = 肩膀宽度
skeleton_bone-UPPER_ARM_LENGTH = 上臂长度
skeleton_bone-ELBOW_OFFSET = 肘部偏移

## Tracker reset buttons
reset-reset_all = 重置所有比例
reset-full = 重置
reset-mounting = 重置佩戴
reset-quick = 快速重置

## Serial detection stuff
serial_detection-new_device-p0 = 检测到了新的串口设备!
serial_detection-new_device-p1 = 输入你的 WiFi 凭据!
serial_detection-new_device-p2 = 请选择你想对它做什么
serial_detection-open_wifi = 连接到 WiFi
serial_detection-open_serial = 打开串口控制器
serial_detection-submit = 提交!
serial_detection-close = 关闭

## Navigation bar
navbar-home = 主页
navbar-body_proportions = 身体比例
navbar-trackers_assign = 追踪器分配
navbar-mounting = 佩戴校准
navbar-onboarding = 向导
navbar-settings = 设置

## Bounding volume hierarchy recording
bvh-start_recording = 录制 BVH 文件
bvh-recording = 录制中...

## Overlay settings
overlay-is_visible_label = 在 SteamVR 中显示覆盖层
overlay-is_mirrored_label = 镜像显示覆盖层

## Tracker status
tracker-status-none = 无状态
tracker-status-busy = 繁忙
tracker-status-error = 错误
tracker-status-disconnected = 断开连接
tracker-status-occluded = 被遮挡
tracker-status-ok = 已连接

## Tracker status columns
tracker-table-column-name = 名字
tracker-table-column-type = 类型
tracker-table-column-battery = 电量
tracker-table-column-ping = 延迟
tracker-table-column-rotation = 旋转 X/Y/Z
tracker-table-column-position = 位置 X/Y/Z
tracker-table-column-url = 地址

## Tracker rotation
tracker-rotation-front = 前
tracker-rotation-left = 左
tracker-rotation-right = 右
tracker-rotation-back = 后

## Tracker information
tracker-infos-manufacturer = 制造商
tracker-infos-display_name = 显示名称
tracker-infos-custom_name = 自定义名称
tracker-infos-url = 追踪器地址

## Tracker settings
tracker-settings-back = 返回追踪器列表
tracker-settings-title = 追踪器设置
tracker-settings-assignment_section = 分配追踪器
tracker-settings-assignment_section-description = 该追踪器要被分配到身体的哪个部位？
tracker-settings-assignment_section-edit = 编辑分配
tracker-settings-mounting_section = 佩戴位置
tracker-settings-mounting_section-description = 该追踪器被佩戴在哪里？
tracker-settings-mounting_section-edit = 编辑佩戴
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = 追踪器名称
tracker-settings-name_section-placeholder = CC 封印着漆黑之力的漆黑左臂
tracker-settings-name_section-description = 给它起一个可爱的名字吧=w=~

## Tracker part card info
tracker-part_card-no_name = 未命名
tracker-part_card-unassigned = 未分配

## Body assignment menu
body_assignment_menu = 你想把追踪器放在哪里?
body_assignment_menu-description = 选择要将此追踪器分配到的位置，或者你也可以选择一次管理所有追踪器，而不是逐个管理。
body_assignment_menu-show_advanced_locations = 显示高级分配位置
body_assignment_menu-manage_trackers = 管理所有追踪器
body_assignment_menu-unassign_tracker = 取消分配追踪器

## Tracker assignment menu
# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
# 
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = Which tracker to assign to your
tracker_selection_menu-NONE = Which tracker do you want to be unassigned?
tracker_selection_menu-HEAD = { -tracker_selection-part } head?
tracker_selection_menu-NECK = { -tracker_selection-part } neck?
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part } right shoulder?
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part } right upper arm?
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part } right lower arm?
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part } right hand?
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part } right thigh?
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part } right ankle?
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part } right foot?
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part } right controller?
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

tracker_selection_menu-unassigned = 未分配的追踪器
tracker_selection_menu-assigned = 已分配的追踪器
tracker_selection_menu-dont_assign = 不分配

## Mounting menu
mounting_selection_menu = 你想把追踪器放在哪里？
mounting_selection_menu-close = 关闭

## Sidebar settings
settings-sidebar-title = 设置
settings-sidebar-general = 通用设置
settings-sidebar-tracker_mechanics = 追踪器设置
settings-sidebar-fk_settings = FK 设置
settings-sidebar-gesture_control = 手势控制
settings-sidebar-interface = 交互界面
settings-sidebar-osc_router = OSC 路由
settings-sidebar-utils = 工具
settings-sidebar-serial = 串行控制器

## SteamVR settings
settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = SteamVR 追踪器
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    启用或禁用特定的追踪器
    对于只支持特定追踪器的应用会很有用。
settings-general-steamvr-trackers-waist = 腰部
settings-general-steamvr-trackers-chest = 胸部
settings-general-steamvr-trackers-feet = 脚部
settings-general-steamvr-trackers-knees = 膝盖
settings-general-steamvr-trackers-elbows = 肘部
settings-general-steamvr-trackers-hands = 手部

## Tracker mechanics
settings-general-tracker_mechanics = 追踪器设置
settings-general-tracker_mechanics-subtitle = 滤波
# This also cares about multilines
settings-general-tracker_mechanics-description =
    选择追踪器的滤波类型。
    预测型将会对你的运动加以预测，而平滑型将会让你的运动更加平滑。
settings-general-tracker_mechanics-filtering_type = 滤波类型
settings-general-tracker_mechanics-filtering_type-none = 无滤波
settings-general-tracker_mechanics-filtering_type-none-description = 使用原始数据，不会进行任何过滤。
settings-general-tracker_mechanics-filtering_type-smoothing = 平滑型
settings-general-tracker_mechanics-filtering_type-smoothing-description = 让运动更加平滑，但会增加一些延迟。
settings-general-tracker_mechanics-filtering_type-prediction = 预测型
settings-general-tracker_mechanics-filtering_type-prediction-description = 减少延迟并使移动更敏捷，但可能会增加一些抖动。
settings-general-tracker_mechanics-amount = 滤波强度

## FK/Tracking settings
settings-general-fk_settings = FK 设置
settings-general-fk_settings-leg_tweak = 腿部调整
settings-general-fk_settings-leg_tweak-description = 本设置可以减少甚至消除脚部穿入地板的情况，但是当你跪在地上的时候可能产生一些问题. 脚滑矫正可以矫正一些脚滑溜冰的问题, 但是可能会降低某些动作的准确性。
# Floor clip: 
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = 地板限制
# Skating correction: 
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = 脚滑矫正
settings-general-fk_settings-leg_tweak-skating_correction-amount = 脚滑矫正数值
settings-general-fk_settings-arm_fk = 手臂 FK
settings-general-fk_settings-arm_fk-description = 更改手臂的追踪方式。
settings-general-fk_settings-arm_fk-force_arms = 强制从头显获得数据
settings-general-fk_settings-skeleton_settings = 骨架设置
settings-general-fk_settings-skeleton_settings-description = 打开或关闭骨架设置。建议保持这些设置不变。
settings-general-fk_settings-skeleton_settings-extended_spine = 脊柱延伸
settings-general-fk_settings-skeleton_settings-extended_pelvis = 骨盆延伸
settings-general-fk_settings-skeleton_settings-extended_knees = 膝盖延伸
settings-general-fk_settings-vive_emulation-title = Vive emulation
settings-general-fk_settings-vive_emulation-description = Emulate the waist tracker problems that Vive trackers have. This is a joke and makes tracking worse.
settings-general-fk_settings-vive_emulation-label = Enable Vive emulation

## Gesture control settings (tracker tapping)
settings-general-gesture_control = 手势控制
settings-general-gesture_control-subtitle = 双击快速重置
settings-general-gesture_control-description = 启用或禁用双击快速重置。启用时，双击身上佩戴的最高的追踪器上的任何位置将激活快速重置。延迟是指记录到敲击和重置之间的时间。
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps = { $amount ->
    [one] 1 tap
    *[other] { $amount } taps
}
settings-general-gesture_control-quickResetEnabled = Enable tap to quick reset
settings-general-gesture_control-quickResetDelay = Quick reset delay
settings-general-gesture_control-quickResetTaps = Taps for quick reset
settings-general-gesture_control-resetEnabled = Enable tap to reset
settings-general-gesture_control-resetDelay = Reset delay
settings-general-gesture_control-resetTaps = Taps for reset
settings-general-gesture_control-mountingResetEnabled = Enable tap to reset mounting
settings-general-gesture_control-mountingResetDelay = Mounting reset delay
settings-general-gesture_control-mountingResetTaps = Taps for mounting reset

## Interface settings
settings-general-interface = 交互界面
settings-general-interface-dev_mode = 开发者模式
settings-general-interface-dev_mode-description = 如果你需要深入的数据或更深入地与连接的追踪器进行交互，打开此模式将会非常有用。
settings-general-interface-dev_mode-label = 开发者模式
settings-general-interface-serial_detection = 串口设备检测
settings-general-interface-serial_detection-description = 每次插入可能是追踪器的新串口设备时，此选项都会显示一个弹出窗口。这有助于改进追踪器的设置过程。
settings-general-interface-serial_detection-label = 串口设备检测
settings-general-interface-lang = 选择语言
settings-general-interface-lang-description = 更改要使用的默认语言
settings-general-interface-lang-placeholder = 选择要使用的语言

## Serial settings
settings-serial = 串口控制台
# This cares about multilines
settings-serial-description =
    这是用于串口通信的实时信息馈送。
    如果你需要了解固件是否出现问题，这将会很有用。
settings-serial-connection_lost = 串口连接丢失，正在重新连接..
settings-serial-reboot = 重新启动
settings-serial-factory_reset = 恢复出厂设置
settings-serial-get_infos = 获取信息
settings-serial-serial_select = 选择串行端口
settings-serial-auto_dropdown_item = 自动

## OSC router settings
settings-osc-router = OSC 路由
# This cares about multilines
settings-osc-router-description =
    从另一个程序转发 OSC 信息。
    在使用另一个 VRChat 的 OSC 程序时会很有用。
settings-osc-router-enable = 启用
settings-osc-router-enable-description = 控制信息转发功能的开关。
settings-osc-router-enable-label = 启用
settings-osc-router-network = 网络端口
# This cares about multilines
settings-osc-router-network-description =
    设置用于监听和向 VRChat 发送数据的端口
    这些端口可以与 SlimeVR 服务器中使用的其他端口相同。
settings-osc-router-network-port_in =
    .label = 输入端口
    .placeholder = 输入端口（默认 9002）
settings-osc-router-network-port_out =
    .label = 输出端口
    .placeholder = 输出端口（默认 9000）
settings-osc-router-network-address = 网络地址
settings-osc-router-network-address-description = 设置用来发送数据的地址。
settings-osc-router-network-address-placeholder = IPV4 地址

## OSC VRChat settings
settings-osc-vrchat = VRChat OSC Trackers
# This cares about multilines
settings-osc-vrchat-description =
    改变 VRChat 的特定设置以接收和发送头显数据。
    用于全身追踪的追踪器数据（在 Quest 端生效）。
settings-osc-vrchat-enable = 启用
settings-osc-vrchat-enable-description = 切换数据的发送和接收
settings-osc-vrchat-enable-label = 启用
settings-osc-vrchat-network = 网络端口
settings-osc-vrchat-network-description = 设置用于监听和向 VRChat 发送数据的端口
settings-osc-vrchat-network-port_in =
    .label = 输入端口
    .placeholder = 输入端口（默认 9001）
settings-osc-vrchat-network-port_out =
    .label = 输出端口
    .placeholder = 输出端口（默认 9000）
settings-osc-vrchat-network-address = 网络地址
settings-osc-vrchat-network-address-description = 选择将数据发送到 VRChat 的地址（检查设备上的 WiFi 设置）
settings-osc-vrchat-network-address-placeholder = VRChat IP 地址
settings-osc-vrchat-network-trackers = 追踪器
settings-osc-vrchat-network-trackers-description = 切换数据的发送和接收
settings-osc-vrchat-network-trackers-chest = 胸部
settings-osc-vrchat-network-trackers-waist = 腰部
settings-osc-vrchat-network-trackers-knees = 膝盖
settings-osc-vrchat-network-trackers-feet = 脚部
settings-osc-vrchat-network-trackers-elbows = 肘部

## Setup/onboarding menu
onboarding-skip = 跳过设置
onboarding-continue = 继续
onboarding-wip = 仍在开发中

## WiFi setup
onboarding-wifi_creds-back = 返回简介
onboarding-wifi_creds = 输入 WiFi 凭据
# This cares about multilines
onboarding-wifi_creds-description =
    追踪器将使用这些凭据连接到 WiFi
    请使用当前连接到 WiFi 的凭据
onboarding-wifi_creds-skip = 跳过 WiFi 设置
onboarding-wifi_creds-submit = 提交！
onboarding-wifi_creds-ssid =
    .label = SSID
    .placeholder = Enter SSID
onboarding-wifi_creds-password =
    .label = Password
    .placeholder = Enter password

## Mounting setup
onboarding-reset_tutorial-back = 返回到佩戴校准
onboarding-reset_tutorial = 重置教程
onboarding-reset_tutorial-description = 此功能尚未开发完成，请继续就好

## Setup start
onboarding-home = 欢迎来到 SlimeVR
# This cares about multilines and it's centered!!
onboarding-home-description =
    将全身追踪
    带给每一个人
onboarding-home-start = 我准备好了！

## Enter VR part of setup
onboarding-enter_vr-back = 返回到追踪器分配
onboarding-enter_vr-title = VR 时间到！
onboarding-enter_vr-description = 穿戴好所有的追踪器，开始快乐 VR 吧！
onboarding-enter_vr-ready = 我准备好了

## Setup done
onboarding-done-title = 都搞定啦！
onboarding-done-description = 享受你的全身追踪体验吧
onboarding-done-close = 关闭向导

## Tracker connection setup
onboarding-connect_tracker-back = 返回到 WiFi 凭据设置
onboarding-connect_tracker-title = 连接追踪器
onboarding-connect_tracker-description-p0 = 来到了我第二喜欢的环节，连接所有的追踪器！
onboarding-connect_tracker-description-p1 = 只需通过 USB 连接所有尚未连接的设备即可。
onboarding-connect_tracker-issue-serial = QAQ 我在连接时遇到问题！
onboarding-connect_tracker-usb = USB 追踪器
onboarding-connect_tracker-connection_status-connecting = 正在发送 WiFi 凭据
onboarding-connect_tracker-connection_status-connected = WiFi 已连接
onboarding-connect_tracker-connection_status-error = 无法连接到 WiFi
onboarding-connect_tracker-connection_status-start_connecting = 寻找追踪器
onboarding-connect_tracker-connection_status-handshake = 已连接到服务器
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers = { $amount ->
    [0] No trackers
    [one] 1 tracker
    *[other] { $amount } trackers
} connected
onboarding-connect_tracker-next = 所有的追踪器都连接好了

## Tracker assignment setup
onboarding-assign_trackers-back = 返回 WiFi 凭据设置
onboarding-assign_trackers-title = 分配追踪器
onboarding-assign_trackers-description = 让我们选择哪个追踪器在哪里。单击要放置追踪器的部位
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned = { $assigned } of { $trackers ->
    [one] 1 tracker
    *[other] { $trackers } trackers
} assigned
onboarding-assign_trackers-advanced = 显示高级分配部位
onboarding-assign_trackers-next = 所有的追踪器都分配好了

## Tracker manual mounting setup
onboarding-manual_mounting-back = 返回到进入 VR
onboarding-manual_mounting = 手动佩戴
onboarding-manual_mounting-description = 单击每个追踪器并选择它们的配电方式
onboarding-manual_mounting-auto_mounting = 自动佩戴
onboarding-manual_mounting-next = 下一步

## Tracker automatic mounting setup
onboarding-automatic_mounting-back = 返回到进入 VR
onboarding-automatic_mounting-title = 佩戴校准
onboarding-automatic_mounting-description = 为了让 SlimeVR 追踪器正常工作，我们需要为你的追踪器分配一个佩戴方向，以使其与你的物理追踪器佩戴方式对齐。
onboarding-automatic_mounting-manual_mounting = 手动设置佩戴方向
onboarding-automatic_mounting-next = 下一步
onboarding-automatic_mounting-prev_step = 上一步
onboarding-automatic_mounting-done-title = 佩戴方向已校准。
onboarding-automatic_mounting-done-description = 你的佩戴方向校准完成！
onboarding-automatic_mounting-done-restart = 返回以开始
onboarding-automatic_mounting-mounting_reset-title = 佩戴重置
onboarding-automatic_mounting-mounting_reset-step-0 = 1. 双腿弯曲以滑雪的姿势蹲下，上身向前倾斜，手臂弯曲。
onboarding-automatic_mounting-mounting_reset-step-1 = 按下佩戴重置按钮并等待 3 秒钟，然后追踪器的佩戴方向将被重置。
onboarding-automatic_mounting-preparation-title = 准备
onboarding-automatic_mounting-preparation-step-0 = 1. 身体直立，双臂放在身体两侧。
onboarding-automatic_mounting-preparation-step-1 = 按下“复位”按钮，等待 3 秒钟，追踪器将复位。
onboarding-automatic_mounting-put_trackers_on-title = 穿戴好追踪器
onboarding-automatic_mounting-put_trackers_on-description = 为了校准佩戴方向，我们将使用你刚才分配的追踪器。戴上你所有的追踪器，你可以在右边的图中看到哪个追踪器对应哪个。
onboarding-automatic_mounting-put_trackers_on-next = 所有的追踪器都已开启！

## Tracker manual proportions setup
onboarding-manual_proportions-back = 返回重置教程
onboarding-manual_proportions-title = 手动调整身体比例
onboarding-manual_proportions-precision = 精确调整
onboarding-manual_proportions-auto = 自动校准

## Tracker automatic proportions setup
onboarding-automatic_proportions-back = 返回重置教程
onboarding-automatic_proportions-title = 测量你的身体比例
onboarding-automatic_proportions-description = 为了让 SlimeVR 追踪器正常使用，我们需要知道你的骨头的长度。这个简短的校准将为你测量它们。
onboarding-automatic_proportions-manual = 手动校准
onboarding-automatic_proportions-prev_step = 上一步
onboarding-automatic_proportions-put_trackers_on-title = 穿戴好追踪器
onboarding-automatic_proportions-put_trackers_on-description = 为了校准你的身体比例，我们将使用你刚才分配的追踪器。戴上你所有的追踪器，你可以在右边的图中看到哪个追踪器对应哪个。
onboarding-automatic_proportions-put_trackers_on-next = 所有的追踪器都已开启！
onboarding-automatic_proportions-preparation-title = 准备
onboarding-automatic_proportions-preparation-description = 在你的正后方放一把椅子，并准备好在接下来的设置过程中坐下。
onboarding-automatic_proportions-preparation-next = 我在椅子前面啦
onboarding-automatic_proportions-start_recording-title = 准备录制运动
onboarding-automatic_proportions-start_recording-description = 我们现在要记录一些特定的姿势和动作。这些将在下一个屏幕中提示。当按钮被按下时，准备好开始！
onboarding-automatic_proportions-start_recording-next = 开始录制
onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = 录制中...
onboarding-automatic_proportions-recording-description-p1 = 依次做出以下动作:
onboarding-automatic_proportions-recording-steps-0 = 弯曲几次膝盖。
onboarding-automatic_proportions-recording-steps-1 = 坐在椅子上，然后站起来。
onboarding-automatic_proportions-recording-steps-2 = 向左扭转上身，然后向右弯。
onboarding-automatic_proportions-recording-steps-3 = 向右扭转上身，然后向左弯。
onboarding-automatic_proportions-recording-steps-4 = 持续摆动身体，直到计时器结束。
onboarding-automatic_proportions-recording-processing = 正在处理结果
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 15)
onboarding-automatic_proportions-recording-timer = { $time ->
    [one] 1 second left
    *[other] { $time } seconds left
}
onboarding-automatic_proportions-verify_results-title = 校验结果
onboarding-automatic_proportions-verify_results-description = 检查下面的结果，它们看起来是正确的吗？
onboarding-automatic_proportions-verify_results-results = 录制结果
onboarding-automatic_proportions-verify_results-processing = 正在处理结果
onboarding-automatic_proportions-verify_results-redo = 重新录制
onboarding-automatic_proportions-verify_results-confirm = 他们是正确的！
onboarding-automatic_proportions-done-title = 身体数据已测量并保存。
onboarding-automatic_proportions-done-description = 你的身体比例校准已完成！

## Home
home-no_trackers = 未检测到或未分配追踪器
