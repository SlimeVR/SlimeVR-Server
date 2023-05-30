### SlimeVR complete GUI translations


# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = 正在连接到服务器
websocket-connection_lost = 与服务器的连接丢失，正在尝试重新连接...

## Update notification

version_update-title = 新版本可用：{ $version }
version_update-description = 点击“更新”将为您下载SlimeVR安装程序。
version_update-update = 更新
version_update-close = 关闭

## Tips

tips-find_tracker = 不确定哪个追踪器是哪个？在现实中摇动一个追踪器，对应的那个将在屏幕上高亮显示。
tips-do_not_move_heels = 确保你的脚跟在录制的时候不会发生移动!
tips-file_select = 拖放文档或 <u>浏览文档</u> 以使用
tips-tap_setup = 你可以缓慢地敲击2次追踪器来选中它，而不是从菜单中选取。

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

## Proportions

skeleton_bone-NONE = 无
skeleton_bone-HEAD = 头部偏移
skeleton_bone-NECK = 颈部长度
skeleton_bone-torso_group = 躯干长度
skeleton_bone-CHEST = 胸部长度
skeleton_bone-CHEST_OFFSET = 胸部偏移
skeleton_bone-WAIST = 腰部长度
skeleton_bone-HIP = 髋部长度
skeleton_bone-HIP_OFFSET = 髋部偏移
skeleton_bone-HIPS_WIDTH = 髋部宽度
skeleton_bone-leg_group = 全腿长度
skeleton_bone-UPPER_LEG = 大腿长度
skeleton_bone-LOWER_LEG = 小腿长度
skeleton_bone-FOOT_LENGTH = 脚部长度
skeleton_bone-FOOT_SHIFT = 脚部偏移
skeleton_bone-SKELETON_OFFSET = 骨骼偏移
skeleton_bone-SHOULDERS_DISTANCE = 肩膀距离
skeleton_bone-SHOULDERS_WIDTH = 肩膀宽度
skeleton_bone-arm_group = 手臂长度
skeleton_bone-UPPER_ARM = 上臂长度
skeleton_bone-LOWER_ARM = 前臂距离
skeleton_bone-HAND_Y = 手部距离Y
skeleton_bone-HAND_Z = 手部距离Z
skeleton_bone-ELBOW_OFFSET = 肘部偏移

## Tracker reset buttons

reset-reset_all = 重置身体比例
reset-full = 完整重置
reset-mounting = 重置佩戴
reset-yaw = 重置航向轴

## Serial detection stuff

serial_detection-new_device-p0 = 检测到了新的串口设备!
serial_detection-new_device-p1 = 输入你的 Wi-Fi 凭据!
serial_detection-new_device-p2 = 请选择你想对它做什么
serial_detection-open_wifi = 连接到 Wi-Fi
serial_detection-open_serial = 打开串口控制台
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

## Widget: Overlay settings

widget-overlay = 覆盖层
widget-overlay-is_visible_label = 在 SteamVR 中显示覆盖层
widget-overlay-is_mirrored_label = 镜像显示覆盖层

## Widget: Drift compensation

widget-drift_compensation-clear = 清除漂移补偿数据

## Widget: Developer settings

widget-developer_mode = 开发者选项
widget-developer_mode-high_contrast = 高对比度
widget-developer_mode-precise_rotation = 显示精确旋转
widget-developer_mode-fast_data_feed = 快速数据更新
widget-developer_mode-filter_slimes_and_hmd = 只显示 Slime 追踪器与 HMD
widget-developer_mode-sort_by_name = 根据名称排序
widget-developer_mode-raw_slime_rotation = 显示原始旋转
widget-developer_mode-more_info = 显示更多信息

## Widget: IMU Visualizer

widget-imu_visualizer = 旋转
widget-imu_visualizer-rotation_raw = 原始旋转
widget-imu_visualizer-rotation_preview = 预览
widget-imu_visualizer-rotation_hide = 隐藏

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
tracker-table-column-tps = TPS
tracker-table-column-temperature = 温度 °C
tracker-table-column-linear-acceleration = 加速度 X/Y/Z
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
tracker-infos-version = 固件版本
tracker-infos-hardware_rev = 硬件版本
tracker-infos-hardware_identifier = 硬件ID
tracker-infos-imu = IMU型号
tracker-infos-board_type = 主板型号

## Tracker settings

tracker-settings-back = 返回追踪器列表
tracker-settings-title = 追踪器设置
tracker-settings-assignment_section = 分配追踪器
tracker-settings-assignment_section-description = 该追踪器要被分配到身体的哪个部位？
tracker-settings-assignment_section-edit = 编辑分配
tracker-settings-mounting_section = 佩戴位置
tracker-settings-mounting_section-description = 该追踪器被佩戴在哪里？
tracker-settings-mounting_section-edit = 编辑佩戴
tracker-settings-drift_compensation_section = 允许漂移补偿
tracker-settings-drift_compensation_section-description = 是否在此追踪器上应用漂移补偿？
tracker-settings-drift_compensation_section-edit = 允许漂移补偿
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = 追踪器名称
tracker-settings-name_section-description = 给它起一个可爱的名字吧=w=~
tracker-settings-name_section-placeholder = CC 封印着漆黑之力的漆黑左臂

## Tracker part card info

tracker-part_card-no_name = 未命名
tracker-part_card-unassigned = 未分配

## Body assignment menu

body_assignment_menu = 你想将此追踪器戴在哪里？
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
-tracker_selection-part = 哪个追踪器将被分配到你的
tracker_selection_menu-NONE = 你想将哪个追踪器解除分配？
tracker_selection_menu-HEAD = { -tracker_selection-part }头部？
tracker_selection_menu-NECK = { -tracker_selection-part }颈部？
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part }右肩？
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part }右大臂？
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part }右小臂？
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part }右手？
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part }右大腿？
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part }右小腿？
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part }右脚？
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part }右控制器？
tracker_selection_menu-CHEST = { -tracker_selection-part }胸部？
tracker_selection_menu-WAIST = { -tracker_selection-part }腰部？
tracker_selection_menu-HIP = { -tracker_selection-part }髋部？
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part }左肩？
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part }左大臂？
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part }左小臂？
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part }左手？
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part }左大腿？
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part }左小腿？
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part }左脚？
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part }左控制器？
tracker_selection_menu-unassigned = 未分配的追踪器
tracker_selection_menu-assigned = 已分配的追踪器
tracker_selection_menu-dont_assign = 不分配
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>警告：</b> 颈部追踪器在调整得过紧时有致命风险
    绑带可能会阻断你头部的血液循环！
tracker_selection_menu-neck_warning-done = 我已知晓风险
tracker_selection_menu-neck_warning-cancel = 取消

## Mounting menu

mounting_selection_menu = 你想将此追踪器戴在哪里？
mounting_selection_menu-close = 关闭

## Sidebar settings

settings-sidebar-title = 设置
settings-sidebar-general = 通用设置
settings-sidebar-tracker_mechanics = 追踪器设置
settings-sidebar-fk_settings = FK 设置
settings-sidebar-gesture_control = 手势控制
settings-sidebar-interface = 交互界面
settings-sidebar-osc_router = OSC 路由
settings-sidebar-osc_trackers = VRChat OSC 追踪器
settings-sidebar-utils = 工具
settings-sidebar-serial = 串口控制台

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
settings-general-tracker_mechanics-filtering = 滤波
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    选择追踪器的滤波类型。
    预测型将会对你的运动加以预测，而平滑型将会让你的运动更加平滑。
settings-general-tracker_mechanics-filtering-type = 滤波类型
settings-general-tracker_mechanics-filtering-type-none = 无滤波
settings-general-tracker_mechanics-filtering-type-none-description = 使用原始数据，不进行滤波。
settings-general-tracker_mechanics-filtering-type-smoothing = 平滑型
settings-general-tracker_mechanics-filtering-type-smoothing-description = 让运动更加平滑，但会增加一些延迟。
settings-general-tracker_mechanics-filtering-type-prediction = 预测型
settings-general-tracker_mechanics-filtering-type-prediction-description = 减少延迟并使移动更敏捷，但可能会增加一些抖动。
settings-general-tracker_mechanics-filtering-amount = 滤波强度
settings-general-tracker_mechanics-drift_compensation = 漂移补偿
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    应用反向旋转以补偿IMU的偏航角漂移。
    更改补偿量和使用多少次的重置结果用于计算补偿量。
settings-general-tracker_mechanics-drift_compensation-enabled-label = 漂移补偿
settings-general-tracker_mechanics-drift_compensation-amount-label = 补偿量
settings-general-tracker_mechanics-drift_compensation-max_resets-label = 使用几次的重置结果？

## FK/Tracking settings

settings-general-fk_settings = FK 设置
# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = 地板限制
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = 脚滑矫正
settings-general-fk_settings-leg_tweak-toe_snap = 脚趾着地
settings-general-fk_settings-leg_tweak-foot_plant = 脚掌着地
settings-general-fk_settings-leg_tweak-skating_correction-amount = 脚滑矫正量
settings-general-fk_settings-leg_tweak-skating_correction-description = 脚滑矫正可以矫正一些脚滑溜冰的问题, 但是可能会降低某些动作的准确性。启用前请先进行完整重置，启用后在游戏内重新校准。
settings-general-fk_settings-leg_tweak-floor_clip-description = 地板限制可以减轻甚至消除脚部穿入地板的情况。启用前请先进行完整重置，启用后在游戏内重新校准。
settings-general-fk_settings-leg_tweak-toe_snap-description = 脚趾着地可以在没有脚部追踪器的情况下尝试猜测脚部的俯仰。
settings-general-fk_settings-leg_tweak-foot_plant-description = 脚掌着地会在脚与地面接触时保持脚掌与地板平行。
settings-general-fk_settings-leg_fk = 腿部追踪
settings-general-fk_settings-arm_fk = 手臂追踪
settings-general-fk_settings-arm_fk-description = 即使有手臂位置数据可用，也强制使用头显的数据追踪手臂。
settings-general-fk_settings-arm_fk-force_arms = 强制使用头显数据追踪手臂
settings-general-fk_settings-skeleton_settings = 骨架设置
settings-general-fk_settings-skeleton_settings-description = 打开或关闭骨架设置。建议保持这些设置不变。
settings-general-fk_settings-skeleton_settings-extended_spine = 脊柱延伸
settings-general-fk_settings-skeleton_settings-extended_pelvis = 骨盆延伸
settings-general-fk_settings-skeleton_settings-extended_knees = 膝盖延伸
settings-general-fk_settings-vive_emulation-title = Vive 模拟
settings-general-fk_settings-vive_emulation-description = 模拟Vive追踪器的腰部丢追问题。（ 注：这是一个玩笑功能，会劣化追踪质量。）
settings-general-fk_settings-vive_emulation-label = 开启 Vive 模拟

## Gesture control settings (tracker tapping)

settings-general-gesture_control = 手势控制
settings-general-gesture_control-subtitle = 敲击重置
settings-general-gesture_control-description = 启用敲击追踪器触发重置。敲击躯干配戴最高的追踪器会触发重置航向轴，敲击左腿配戴最高的追踪器会触发完整重置，敲击右腿配戴最高的追踪器会触发重置佩戴。请注意，需要在 0.6 秒内满足敲击次数才会触发。
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
       *[other] { $amount }次敲击
    }
settings-general-gesture_control-yawResetEnabled = 开启敲击重置航向轴
settings-general-gesture_control-yawResetDelay = 敲击重置航向轴延迟
settings-general-gesture_control-yawResetTaps = 重置航向轴敲击次数
settings-general-gesture_control-fullResetEnabled = 开启敲击完整重置
settings-general-gesture_control-fullResetDelay = 敲击完整重置延迟
settings-general-gesture_control-fullResetTaps = 完整重置敲击次数
settings-general-gesture_control-mountingResetEnabled = 开启敲击重置佩戴
settings-general-gesture_control-mountingResetDelay = 敲击重置佩戴延迟
settings-general-gesture_control-mountingResetTaps = 重置佩戴敲击次数

## Interface settings

settings-general-interface = 交互界面
settings-general-interface-dev_mode = 开发者模式
settings-general-interface-dev_mode-description = 如果你需要深入的数据或更深入地与连接的追踪器进行交互，打开此模式将会非常有用。
settings-general-interface-dev_mode-label = 开发者模式
settings-general-interface-serial_detection = 串口设备检测
settings-general-interface-serial_detection-description = 每次插入可能是追踪器的新串口设备时，此选项都会显示一个弹出窗口。这有助于改进追踪器的设置过程。
settings-general-interface-serial_detection-label = 串口设备检测
settings-general-interface-feedback_sound = 声音反馈
settings-general-interface-feedback_sound-description = 开启此选项会在触发重置时发出提示音
settings-general-interface-feedback_sound-label = 声音反馈
settings-general-interface-feedback_sound-volume = 提示音音量
settings-general-interface-theme = 主题颜色
settings-general-interface-lang = 选择语言
settings-general-interface-lang-description = 更改要使用的默认语言
settings-general-interface-lang-placeholder = 选择要使用的语言

## Serial settings

settings-serial = 串口控制台
# This cares about multilines
settings-serial-description =
    这里用于显示串口的实时信息流。
    如果你需要了解固件是否出现问题，这将会很有用。
settings-serial-connection_lost = 串口连接丢失，正在重新连接..
settings-serial-reboot = 重新启动
settings-serial-factory_reset = 恢复出厂设置
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>警告：</b> 这会将跟踪器重置为出厂设置。
    这意味着Wi-Fi凭据和校准数据 <b>都将丢失！</b>
settings-serial-factory_reset-warning-ok = 我已知晓
settings-serial-factory_reset-warning-cancel = 取消
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
settings-osc-vrchat-network-address-description = 选择将数据发送到 VRChat 的地址（检查设备上的 Wi-Fi 设置）
settings-osc-vrchat-network-address-placeholder = VRChat IP 地址
settings-osc-vrchat-network-trackers = 追踪器
settings-osc-vrchat-network-trackers-description = 切换数据的发送和接收
settings-osc-vrchat-network-trackers-chest = 胸部
settings-osc-vrchat-network-trackers-hip = 髋部
settings-osc-vrchat-network-trackers-knees = 膝盖
settings-osc-vrchat-network-trackers-feet = 脚部
settings-osc-vrchat-network-trackers-elbows = 肘部

## VMC OSC settings

settings-osc-vmc = Virtual Motion Capture
# This cares about multilines
settings-osc-vmc-description =
    修改 VMC (Virtual Motion Capture) 协定的相关设置
    以发送 SlimeVR 的骨骼数据，并接收来自其他应用程序的骨骼数据
settings-osc-vmc-enable = 启用
settings-osc-vmc-enable-description = 切换数据的发送和接收
settings-osc-vmc-enable-label = 启用
settings-osc-vmc-network = 网络端口
settings-osc-vmc-network-description = 设置用于监听和发送 VMC 数据的连接端口
settings-osc-vmc-network-port_in =
    .label = 输入端口
    .placeholder = 输入端口 (默认: 39540)
settings-osc-vmc-network-port_out =
    .label = 输出端口
    .placeholder = 输出端口 (默认: 39539)
settings-osc-vmc-network-address = 网络地址
settings-osc-vmc-network-address-description = 设置用来发送 VMC 数据的地址
settings-osc-vmc-network-address-placeholder = IPV4 地址
settings-osc-vmc-vrm = VRM 模型
settings-osc-vmc-vrm-description = 加载 VRM 模型以允许头部锚定，并增进与其他程序的兼容
settings-osc-vmc-vrm-model_unloaded = 未加载模型
settings-osc-vmc-vrm-model_loaded =
    { $titled ->
        [true] 已加载模型：{ $name }
       *[other] 已加载未命名的模型
    }
settings-osc-vmc-vrm-file_select = 拖曳文件或 <u>浏览文件</u> 以加载模型
settings-osc-vmc-anchor_hip = 髋部锚定
settings-osc-vmc-anchor_hip-description = 将追踪锚定在髋部，有利于坐姿进行虚拟直播。若本选项无法切换，请加载 VRM 模型。
settings-osc-vmc-anchor_hip-label = 髋部锚定

## Setup/onboarding menu

onboarding-skip = 跳过设置
onboarding-continue = 继续
onboarding-wip = 仍在开发中
onboarding-previous_step = 上一步
onboarding-setup_warning =
    <b>警告：</b> 需要进行设置以得到良好的追踪质量，
    如果这是您第一次使用 SlimeVR 则需要先进行设置。
onboarding-setup_warning-skip = 跳过设置
onboarding-setup_warning-cancel = 继续设置

## Wi-Fi setup

onboarding-wifi_creds-back = 返回简介
onboarding-wifi_creds = 输入 Wi-Fi 凭据
# This cares about multilines
onboarding-wifi_creds-description =
    追踪器将使用这些凭据连接到 Wi-Fi
    请使用当前连接到 Wi-Fi 的凭据
onboarding-wifi_creds-skip = 跳过 Wi-Fi 设置
onboarding-wifi_creds-submit = 提交！
onboarding-wifi_creds-ssid =
    .label = SSID
    .placeholder = 输入 SSID
onboarding-wifi_creds-password =
    .label = 密码
    .placeholder = 输入密码

## Mounting setup

onboarding-reset_tutorial-back = 返回到佩戴校准
onboarding-reset_tutorial = 重置教程
onboarding-reset_tutorial-description = 此功能尚未开发完成，请继续就好

## Setup start

onboarding-home = 欢迎来到 SlimeVR
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

onboarding-connect_tracker-back = 返回到 Wi-Fi 凭据设置
onboarding-connect_tracker-title = 连接追踪器
onboarding-connect_tracker-description-p0 = 来到了我第二喜欢的环节，连接所有的追踪器！
onboarding-connect_tracker-description-p1 = 只需通过 USB 连接所有尚未连接的设备即可。
onboarding-connect_tracker-issue-serial = QAQ 我在连接时遇到问题！
onboarding-connect_tracker-usb = USB 追踪器
onboarding-connect_tracker-connection_status-none = 正在寻找追踪器
onboarding-connect_tracker-connection_status-serial_init = 正在连接到串口设备
onboarding-connect_tracker-connection_status-provisioning = 正在发送 Wi-Fi 凭据
onboarding-connect_tracker-connection_status-connecting = 正在发送 Wi-Fi 凭据
onboarding-connect_tracker-connection_status-looking_for_server = 正在寻找服务器
onboarding-connect_tracker-connection_status-connection_error = 无法连接到 Wi-Fi
onboarding-connect_tracker-connection_status-could_not_find_server = 未找到到服务器
onboarding-connect_tracker-connection_status-done = 已连接到服务器
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers =
    { $amount ->
        [0] 没有已连接的追踪器
       *[other] { $amount } 个追踪器已连接
    }
onboarding-connect_tracker-next = 所有的追踪器都连接好了

## Tracker calibration tutorial

onboarding-calibration_tutorial = IMU校准教程
onboarding-calibration_tutorial-subtitle = 这将有助于减少追踪器漂移！
onboarding-calibration_tutorial-description = 每次开启追踪器时，它们都需要在平坦的表面上放置片刻以进行自校准。你可以通过点击“校准”按钮来手动校准， <b>校准过程中不要移动它们！</b>
onboarding-calibration_tutorial-calibrate = 我已经把追踪器放在桌子上了
onboarding-calibration_tutorial-status-waiting = 等待你的操作
onboarding-calibration_tutorial-status-calibrating = 校准中
onboarding-calibration_tutorial-status-success = 很好！
onboarding-calibration_tutorial-status-error = 追踪器被移动！

## Tracker assignment setup

onboarding-assign_trackers-back = 返回 Wi-Fi 凭据设置
onboarding-assign_trackers-title = 分配追踪器
onboarding-assign_trackers-description = 让我们选择哪个追踪器在哪里。单击要放置追踪器的部位
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned = { $assigned }/{ $trackers } 个追踪器已分配
onboarding-assign_trackers-advanced = 显示高级分配部位
onboarding-assign_trackers-next = 所有的追踪器都分配好了

## Tracker assignment warnings

# Note for devs, number is used for representing boolean states per bit.
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_FOOT =
    { $unassigned ->
        [0] 左脚 已分配，但您还需要分配 左小腿 左大腿 胸部 髋部/腰部！
        [1] 左脚 已分配，但您还需要分配 左大腿 胸部 髋部/腰部！
        [2] 左脚 已分配，但您还需要分配 左小腿 胸部 髋部/腰部！
        [3] 左脚 已分配，但您还需要分配 胸部 髋部/腰部！
        [4] 左脚 已分配，但您还需要分配 左小腿 左大腿 ！
        [5] 左脚 已分配，但您还需要分配 左大腿！
        [6] 左脚 已分配，但您还需要分配 左小腿！
       *[unknown] 左脚 已分配，但您还需要分配 未知未分配身体部位！
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_FOOT =
    { $unassigned ->
        [0] 右脚 已分配，但您还需要分配 右小腿 右大腿 胸部 髋部/腰部！
        [1] 右脚 已分配，但您还需要分配 右大腿 胸部 髋部/腰部！
        [2] 右脚 已分配，但您还需要分配 右小腿 胸部 髋部/腰部！
        [3] 右脚 已分配，但您还需要分配 胸部 髋部/腰部！
        [4] 右脚 已分配，但您还需要分配 右小腿 右大腿 ！
        [5] 右脚 已分配，但您还需要分配 右大腿！
        [6] 右脚 已分配，但您还需要分配 右小腿！
       *[unknown] 右脚 已分配，但您还需要分配 未知未分配身体部位！
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_LOWER_LEG =
    { $unassigned ->
        [0] 左小腿 已分配，但您还需要分配 左大腿 胸部 髋部/腰部！
        [1] 左小腿 已分配，但您还需要分配 胸部 髋部/腰部！
        [2] 左小腿 已分配，但您还需要分配 左大腿！
       *[unknown] 左小腿 已分配，但您还需要分配 未知未分配身体部位！
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_LOWER_LEG =
    { $unassigned ->
        [0] 右小腿 已分配，但您还需要分配 右大腿 胸部 髋部/腰部！
        [1] 右小腿 已分配，但您还需要分配 胸部 髋部/腰部！
        [2] 右小腿 已分配，但您还需要分配 右大腿！
       *[unknown] 右小腿 已分配，但您还需要分配 未知未分配身体部位！
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_UPPER_LEG =
    { $unassigned ->
        [0] 左大腿 已分配，但您还需要分配 胸部 髋部/腰部！
       *[unknown] 左大腿 已分配，但您还需要分配 未知未分配身体部位！
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_UPPER_LEG =
    { $unassigned ->
        [0] 右大腿 已分配，但您还需要分配 胸部 髋部/腰部！
       *[unknown] 右大腿 已分配，但您还需要分配 未知未分配身体部位！
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-HIP =
    { $unassigned ->
        [0] 髋部 已分配，但您还需要分配 胸部！
       *[unknown] 髋部 已分配，但您还需要分配 未知未分配身体部位！
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-WAIST =
    { $unassigned ->
        [0] 腰部 已分配，但您还需要分配 胸部！
       *[unknown] 腰部 已分配，但您还需要分配 未知未分配身体部位！
    }

## Tracker mounting method choose

onboarding-choose_mounting = 使用哪种方法校准佩戴朝向？
onboarding-choose_mounting-auto_mounting = 自动设置佩戴方向
# Italized text
onboarding-choose_mounting-auto_mounting-subtitle = 推荐
onboarding-choose_mounting-auto_mounting-description = 这将需要你做2个动作以自动检测所有追踪器的佩戴方向
onboarding-choose_mounting-manual_mounting = 手动设置佩戴方向
# Italized text
onboarding-choose_mounting-manual_mounting-subtitle = 如果你清楚自己在做什么
onboarding-choose_mounting-manual_mounting-description = 这将需要你手动选择每个追踪器的佩戴方向

## Tracker manual mounting setup

onboarding-manual_mounting-back = 返回到进入 VR
onboarding-manual_mounting = 手动佩戴
onboarding-manual_mounting-description = 单击每个追踪器并选择它们的佩戴方式
onboarding-manual_mounting-auto_mounting = 自动设置佩戴方向
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

## Tracker proportions method choose

onboarding-choose_proportions = 使用哪种方法校准身体比例？
onboarding-choose_proportions-auto_proportions = 自动调整身体比例
# Italized text
onboarding-choose_proportions-auto_proportions-subtitle = 推荐
onboarding-choose_proportions-auto_proportions-description = 这将录制你的运动样本并通过AI来猜测你的身体比例
onboarding-choose_proportions-manual_proportions = 手动调整身体比例
# Italized text
onboarding-choose_proportions-manual_proportions-subtitle = 用于精细调整
onboarding-choose_proportions-manual_proportions-description = 这将需要你手动修改以调整你的身体比例

## Tracker manual proportions setup

onboarding-manual_proportions-back = 返回重置教程
onboarding-manual_proportions-title = 手动调整身体比例
onboarding-manual_proportions-precision = 精确调整
onboarding-manual_proportions-auto = 自动校准
onboarding-manual_proportions-ratio = 按比例分组调整

## Tracker automatic proportions setup

onboarding-automatic_proportions-back = 返回重置教程
onboarding-automatic_proportions-title = 测量你的身体比例
onboarding-automatic_proportions-description = 为了让 SlimeVR 追踪器正常使用，我们需要知道你的骨头的长度。这个简短的校准将为你测量它们。
onboarding-automatic_proportions-manual = 手动校准
onboarding-automatic_proportions-prev_step = 上一步
onboarding-automatic_proportions-put_trackers_on-title = 穿戴好追踪器
onboarding-automatic_proportions-put_trackers_on-description = 为了校准你的身体比例，我们将使用你刚才分配的追踪器。戴上你所有的追踪器，你可以在右边的图中看到哪个追踪器对应哪个。
onboarding-automatic_proportions-put_trackers_on-next = 所有的追踪器都已开启！
onboarding-automatic_proportions-requirements-title = 准备工作
# Each line of text is a different list item
onboarding-automatic_proportions-requirements-description =
    你需要足够的追踪器以追踪脚部（通常为 5 个）。
    你已经打开追踪器和头戴设备的电源。
    你已经穿戴好追踪器和头戴设备。
    你的追踪器和头戴设备都已经连接到 SlimeVR 服务器。
    你的追踪器和头戴设备在 SlimeVR 服务器中工作正常。
    你的头戴设备在回报位置信息给 SlimeVR 服务器（通常需要 SteamVR 正在运行且通过 SlimeVR 的 SteamVR 驱动连接到 SlimeVR）。
onboarding-automatic_proportions-requirements-next = 我已阅读
onboarding-automatic_proportions-start_recording-title = 准备录制运动
onboarding-automatic_proportions-start_recording-description = 我们现在要记录一些特定的姿势和动作。这些将在下一个屏幕中提示。当按钮被按下时，准备好开始！
onboarding-automatic_proportions-start_recording-next = 开始录制
onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = 录制中...
onboarding-automatic_proportions-recording-description-p1 = 依次做出以下动作:
# Each line of text is a different list item
onboarding-automatic_proportions-recording-steps =
    挺直身子站好，然后活动脖子，使头部沿逆时针或顺时针方向绕一圈；
    上半身前倾然后屈膝半蹲，保持住这个姿势不动，然后转头看向左边，再转头看向右边；
    站直身体，沿逆时针方向扭腰，使你的上半身朝向左前方，然后弯下腰，使上半身倾向左前方的地面；
    站直身体，沿顺时针方向扭腰，使你的上半身朝向右前方，然后弯下腰，使上半身倾向右前方的地面；
    扭扭腰转圈圈，就如同你在转呼啦圈一样!
    如果进度条还没走完，可以重复以上动作直到录制结束。
onboarding-automatic_proportions-recording-processing = 正在处理结果
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 20)
onboarding-automatic_proportions-recording-timer = 剩余{ $time }秒
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
