# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = 正在连接到服务器
websocket-connection_lost = 与服务器的连接丢失，正在尝试重新连接...
websocket-connection_lost-desc = 看起来 SlimeVR 服务器崩溃了。检查日志并重新启动程序
websocket-timedout = 无法连接到服务器
websocket-timedout-desc = 看起来 SlimeVR 服务器崩溃或超时了。检查日志并重新启动程序
websocket-error-close = 退出 SlimeVR
websocket-error-logs = 打开日志文件夹

## Update notification

version_update-title = 新版本可用：{ $version }
version_update-description = 点击“{ version_update-update }”将为您下载 SlimeVR 安装程序。
version_update-update = 更新
version_update-close = 关闭

## Tips

tips-find_tracker = 不确定哪个追踪器是哪个？在现实中摇动一个追踪器，对应的那个将在屏幕上高亮显示。
tips-do_not_move_heels = 确保你的脚跟在录制的时候不会发生移动!
tips-file_select = 拖放文档或 <u>浏览文档</u> 以使用
tips-tap_setup = 你可以缓慢地敲击2次追踪器来选中它，而不是从菜单中选取。
tips-turn_on_tracker = 如果使用的是 SlimeVR 官方的追踪器，请在将追踪器连接到电脑后再<b><em>打开追踪器的电源</em></b>！
tips-failed_webgl = WebGL初始化失败

## Units

unit-meter = 米
unit-foot = 英尺
unit-inch = 英寸
unit-cm = 厘米

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
body_part-UPPER_CHEST = 上胸
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
body_part-LEFT_THUMB_METACARPAL = 左拇指掌骨
body_part-LEFT_THUMB_PROXIMAL = 左拇指近端
body_part-LEFT_THUMB_DISTAL = 左拇指远端
body_part-LEFT_INDEX_PROXIMAL = 左食指近端
body_part-LEFT_INDEX_INTERMEDIATE = 左食指中端
body_part-LEFT_INDEX_DISTAL = 左食指远端
body_part-LEFT_MIDDLE_PROXIMAL = 左中指近端
body_part-LEFT_MIDDLE_INTERMEDIATE = 左中指中端
body_part-LEFT_MIDDLE_DISTAL = 左中指远端
body_part-LEFT_RING_PROXIMAL = 左环指近端
body_part-LEFT_RING_INTERMEDIATE = 左环指中端
body_part-LEFT_RING_DISTAL = 左环指远端
body_part-LEFT_LITTLE_PROXIMAL = 左小指近端
body_part-LEFT_LITTLE_INTERMEDIATE = 左小指中端
body_part-LEFT_LITTLE_DISTAL = 左小指远端
body_part-RIGHT_THUMB_METACARPAL = 右拇指掌骨
body_part-RIGHT_THUMB_PROXIMAL = 右拇指近端
body_part-RIGHT_THUMB_DISTAL = 右拇指远端
body_part-RIGHT_INDEX_PROXIMAL = 右食指近端
body_part-RIGHT_INDEX_INTERMEDIATE = 右食指中端
body_part-RIGHT_INDEX_DISTAL = 右食指远端
body_part-RIGHT_MIDDLE_PROXIMAL = 右中指近端
body_part-RIGHT_MIDDLE_INTERMEDIATE = 右中指中端
body_part-RIGHT_MIDDLE_DISTAL = 右中指远端
body_part-RIGHT_RING_PROXIMAL = 右环指近端
body_part-RIGHT_RING_INTERMEDIATE = 右环指中端
body_part-RIGHT_RING_DISTAL = 右环指远端
body_part-RIGHT_LITTLE_PROXIMAL = 右小指近端
body_part-RIGHT_LITTLE_INTERMEDIATE = 右小指中端
body_part-RIGHT_LITTLE_DISTAL = 右小指远端

## BoardType

board_type-UNKNOWN = 未知
board_type-NODEMCU = NodeMCU
board_type-CUSTOM = 自定义开发板
board_type-WROOM32 = WROOM32
board_type-WEMOSD1MINI = Wemos D1 Mini
board_type-TTGO_TBASE = TTGO T-Base
board_type-ESP01 = ESP-01
board_type-SLIMEVR = SlimeVR
board_type-SLIMEVR_DEV = SlimeVR 开发板
board_type-SLIMEVR_V1_2 = SlimeVR v1.2
board_type-LOLIN_C3_MINI = Lolin C3 Mini
board_type-BEETLE32C3 = Beetle ESP32-C3
board_type-ESP32C3DEVKITM1 = Espressif ESP32-C3 DevKitM-1
board_type-OWOTRACK = owoTrack
board_type-WRANGLER = Wrangler Joycons
board_type-MOCOPI = 索尼 Mocopi
board_type-WEMOSWROOM02 = Wemos Wroom-02 D1 Mini
board_type-XIAO_ESP32C3 = Seeed Studio XIAO ESP32C3
board_type-HARITORA = Haritora
board_type-ESP32C6DEVKITC1 = Espressif ESP32-C6 DevKitC-1
board_type-GLOVE_IMU_SLIMEVR_DEV = SlimeVR开发版IMU手套

## Proportions

skeleton_bone-NONE = 无
skeleton_bone-HEAD = 头部偏移
skeleton_bone-HEAD-desc =
    从头戴显示器到头部中心的距离。
    调节时，左右摇头并修改此参数，使头部移动时其他追踪器位置保持不变。
skeleton_bone-NECK = 颈部长度
skeleton_bone-NECK-desc =
    从头部中心到脖子根部的距离。
    调节时，上下点头或左右倾斜头部，并修改此参数，
    使头部移动时其他追踪器位置保持不变。
skeleton_bone-torso_group = 躯干长度
skeleton_bone-torso_group-desc =
    从脖子根部到臀部的距离。
    调节时，直立并修改此参数，使虚拟臀部与真实的臀部对齐。
skeleton_bone-UPPER_CHEST = 上胸长度
skeleton_bone-UPPER_CHEST-desc =
    从脖子根部到胸部中部的距离。
    调节时，先完成躯干长度的设置，再修改此参数，
    并进行包括坐下、弯腰、平躺等动作，使虚拟脊椎与真实的脊椎对齐。
skeleton_bone-CHEST_OFFSET = 胸部偏移
skeleton_bone-CHEST_OFFSET-desc = 向上或向下移动臀部胸部追踪器，用于在某些游戏或应用中移动胸部追踪器位置。
skeleton_bone-CHEST = 胸部长度
skeleton_bone-CHEST-desc =
    从胸部中部到脊椎中部的距离。
    调节时，先完成躯干长度的设置，再修改此参数，
    并进行包括坐下、弯腰、平躺等动作，使虚拟脊椎与真实的脊椎对齐。
skeleton_bone-WAIST = 腰部长度
skeleton_bone-WAIST-desc =
    从脊椎中部到肚脐的距离。
    调节时，先完成躯干长度的设置，再修改此参数，
    并进行包括坐下、弯腰、平躺等动作，使虚拟脊椎与真实的脊椎对齐。
skeleton_bone-HIP = 髋部长度
skeleton_bone-HIP-desc =
    从肚脐到臀部的距离。
    调节时，先完成躯干长度的设置，再修改此参数，
    并进行包括坐下、弯腰、平躺等动作，使虚拟脊椎与真实的脊椎对齐。
skeleton_bone-HIP_OFFSET = 髋部偏移
skeleton_bone-HIP_OFFSET-desc = 向上或向下移动臀部虚拟追踪器，用于在某些游戏或应用中将虚拟臀部追踪器移动至腰部。
skeleton_bone-HIPS_WIDTH = 髋部宽度
skeleton_bone-HIPS_WIDTH-desc =
    两腿根部之间的距离。
    调节时，先在站直时进行一次完全重置，再修改此参数，
    使虚拟腿部与真实的腿部位置在水平方向上对齐。
skeleton_bone-leg_group = 全腿长度
skeleton_bone-leg_group-desc =
    从臀部到脚踝的距离。
    调节时，先完成躯干长度的设置，再修改此参数，
    使虚拟脚部与真实的脚部位置对齐。
skeleton_bone-UPPER_LEG = 大腿长度
skeleton_bone-UPPER_LEG-desc =
    从臀部到膝盖的距离。
    调节时，先完成腿部长度的设置，再修改此参数，
    使虚拟膝盖与真实的膝盖位置对齐。
skeleton_bone-LOWER_LEG = 小腿长度
skeleton_bone-LOWER_LEG-desc =
    从膝盖到脚踝的距离。
    调节时，先完成腿部长度的设置，再修改此参数，
    使虚拟膝盖与真实的膝盖位置对齐。
skeleton_bone-FOOT_LENGTH = 脚部长度
skeleton_bone-FOOT_LENGTH-desc =
    从脚踝到脚趾的距离。
    调节时，踮起脚尖并修改此参数，
    使虚拟脚部位置与真实脚部位置对齐。
skeleton_bone-FOOT_SHIFT = 脚部偏移
skeleton_bone-FOOT_SHIFT-desc =
    从膝盖到脚踝的水平距离。可用于修正直立式小腿向后弯曲的问题。
    调节时，先将脚部长度设置为0，进行完全复位，
    再修改此参数，使虚拟脚部对齐脚踝中心。
skeleton_bone-SKELETON_OFFSET = 骨架偏移
skeleton_bone-SKELETON_OFFSET-desc = 向前或向后移动所有虚拟追踪器，用于在某些游戏或应用中调节追踪器位置。
skeleton_bone-SHOULDERS_DISTANCE = 肩膀距离
skeleton_bone-SHOULDERS_DISTANCE-desc =
    从脖子中心到肩膀的垂直距离。
    调节时，先将上臂长度设置为0，再修改此参数，
    使虚拟肘部追踪器与真实的肩膀位置在垂直方向上对齐。
skeleton_bone-SHOULDERS_WIDTH = 肩膀宽度
skeleton_bone-SHOULDERS_WIDTH-desc =
    从脖子中心到肩膀的水平距离。
    调节时，先将上臂长度设置为0，再修改此参数，
    使虚拟肘部追踪器与真实的肩膀位置在水平方向上对齐。
skeleton_bone-arm_group = 手臂长度
skeleton_bone-arm_group-desc =
    从肩膀到手腕的距离。
    调整时，先完成肩膀距离的配置，将手部距离Y设置为0，
    再修改此参数，使手部追踪器与手腕对齐。
skeleton_bone-UPPER_ARM = 上臂长度
skeleton_bone-UPPER_ARM-desc =
    从肩膀到手肘的距离。
    调整时，先完成手臂长度的设置，再修改此参数，
    使肘部追踪器与真实的手肘位置对齐。
skeleton_bone-LOWER_ARM = 前臂距离
skeleton_bone-LOWER_ARM-desc =
    从手肘到手腕的距离。
    调整时，先完成手臂长度的设置，再修改此参数，
    使肘部追踪器与真实的手肘位置对齐。
skeleton_bone-HAND_Y = 手部距离Y
skeleton_bone-HAND_Y-desc =
    从手腕到手中心的c垂直距离。
    在动捕模式下，先完成手臂长度的设置，
    再修改此参数，使手部追踪器与手的中心在垂直方向上对齐。
    在使用控制器进行肘部追踪的情况下，将手臂长度设置为0，
    再修改此参数，使肘部追踪器与手的中心在垂直方向上对齐。
skeleton_bone-HAND_Z = 手部距离Z
skeleton_bone-HAND_Z-desc =
    从手腕到手中心的水平距离。
    在动捕模式下，将此数值设置为0。
    在使用控制器进行肘部追踪的情况下，将手臂长度设置为0，
    并修改此参数，使肘部追踪器与手腕在水平方向对齐。
skeleton_bone-ELBOW_OFFSET = 肘部偏移
skeleton_bone-ELBOW_OFFSET-desc = 向上或向下移动肘部虚拟追踪器，避免VRChat将肘部追踪器绑定到胸部。

## Tracker reset buttons

reset-reset_all = 重置身体比例
reset-reset_all_warning-v2 =
    <b>警告：</b> 您的骨骼比例将重置为您配置的身高下的默认值。
    您确定要执行此操作吗？
reset-reset_all_warning-reset = 重置身体比例
reset-reset_all_warning-cancel = 取消
reset-reset_all_warning_default-v2 =
    <b>警告：</b> 您的身高尚未配置，您的骨骼比例将重置为默认身高下的默认值。
    您确定要执行此操作吗？
reset-full = 完整重置
reset-mounting = 重置佩戴
reset-mounting-feet = 重置脚部佩戴
reset-mounting-fingers = 重置手指佩戴
reset-yaw = 重置航向轴
reset-error-no_feet_tracker = 未分配脚部追踪器
reset-error-no_fingers_tracker = 未分配手指追踪器
reset-error-mounting-need_full_reset = 佩戴校准前需要先执行完整重置
reset-error-yaw-need_full_reset = 航向轴重置前需要先执行完整重置

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
navbar-connect_trackers = 连接追踪器

## Biovision hierarchy recording

bvh-start_recording = 录制 BVH 文件
bvh-stop_recording = 保存 BVH 记录
bvh-recording = 录制中...
bvh-save_title = 保存BVH记录

## Tracking pause

tracking-unpaused = 暂停追踪
tracking-paused = 解除暂停追踪

## Widget: Overlay settings

widget-overlay = 覆盖层
widget-overlay-is_visible_label = 在 SteamVR 中显示覆盖层
widget-overlay-is_mirrored_label = 镜像显示覆盖层

## Widget: Drift compensation

widget-drift_compensation-clear = 清除漂移补偿数据

## Widget: Clear Mounting calibration

widget-clear_mounting = 清除重置佩戴

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
widget-imu_visualizer-preview = 预览
widget-imu_visualizer-hide = 隐藏
widget-imu_visualizer-rotation_raw = 原始旋转
widget-imu_visualizer-rotation_preview = 预览
widget-imu_visualizer-acceleration = 加速度
widget-imu_visualizer-position = 位置
widget-imu_visualizer-stay_aligned = 持续校准

## Widget: Skeleton Visualizer

widget-skeleton_visualizer-preview = 骨架预览
widget-skeleton_visualizer-hide = 隐藏

## Tracker status

tracker-status-none = 无状态
tracker-status-busy = 繁忙
tracker-status-error = 错误
tracker-status-disconnected = 断开连接
tracker-status-occluded = 被遮挡
tracker-status-ok = 已连接
tracker-status-timed_out = 连接超时

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
tracker-table-column-stay_aligned = 持续校准
tracker-table-column-url = 地址

## Tracker rotation

tracker-rotation-front = 前
tracker-rotation-front_left = 左前
tracker-rotation-front_right = 右前
tracker-rotation-left = 左
tracker-rotation-right = 右
tracker-rotation-back = 后
tracker-rotation-back_left = 左后
tracker-rotation-back_right = 右后
tracker-rotation-custom = 自定义
tracker-rotation-overriden = （被重置佩戴覆盖）

## Tracker information

tracker-infos-manufacturer = 制造商
tracker-infos-display_name = 显示名称
tracker-infos-custom_name = 自定义名称
tracker-infos-url = 追踪器地址
tracker-infos-version = 固件版本
tracker-infos-hardware_rev = 硬件版本
tracker-infos-hardware_identifier = 硬件ID
tracker-infos-data_support = 数据类型
tracker-infos-imu = IMU型号
tracker-infos-board_type = 主板型号
tracker-infos-network_version = 协议版本
tracker-infos-magnetometer = 磁力计
tracker-infos-magnetometer-status-v1 =
    { $status ->
        [DISABLED] 已禁用
        [ENABLED] 已启用
       *[NOT_SUPPORTED] 不支持
    }

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
tracker-settings-use_mag = 允许使用这个追踪器的磁力计
# Multiline!
tracker-settings-use_mag-description =
    如果「在追踪器上启用磁力计」已启用，是否要在这个追踪器上启用它来减轻飘移？<b>切换本选项时请勿关闭追踪器的电源！</b>
    
    请先启用「在追踪器上启用磁力计」功能，<magSetting>点选此处以移动至该设定</magSetting>。
tracker-settings-use_mag-label = 允许使用这个追踪器的磁力计
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = 追踪器名称
tracker-settings-name_section-description = 给它起一个可爱的名字吧=w=~
tracker-settings-name_section-placeholder = CC 封印着漆黑之力的漆黑左臂
tracker-settings-name_section-label = 追踪器名称
tracker-settings-forget = 忘记追踪器
tracker-settings-forget-description = 从 SlimeVR 服务器中移除该追踪器，并在服务器重启前不再连接这一追踪器。追踪器的配置信息不会被清除。
tracker-settings-forget-label = 忘记追踪器
tracker-settings-update-unavailable-v2 = 未找到可用版本
tracker-settings-update-incompatible = 电路板不兼容，无法升级。
tracker-settings-update-low-battery = 无法更新。当前电池电量低于 50%
tracker-settings-update-up_to_date = 已是最新
tracker-settings-update-blocked = 更新不可用。没有其他可用版本
tracker-settings-update = 立即更新
tracker-settings-update-title = 固件版本
tracker-settings-current-version = 当前版本
tracker-settings-latest-version = 最新版本

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
tracker_selection_menu-UPPER_CHEST = { -tracker_selection-part }上胸？
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
settings-sidebar-steamvr = SteamVR
settings-sidebar-tracker_mechanics = 追踪器设置
settings-sidebar-stay_aligned = 持续校准
settings-sidebar-fk_settings = FK 设置
settings-sidebar-gesture_control = 手势控制
settings-sidebar-interface = 交互界面
settings-sidebar-osc_router = OSC 路由
settings-sidebar-osc_trackers = VRChat OSC 追踪器
settings-sidebar-osc_vmc = VMC
settings-sidebar-utils = 工具
settings-sidebar-serial = 串口控制台
settings-sidebar-appearance = 外观
settings-sidebar-home = 主界面
settings-sidebar-checklist = 追踪检查清单
settings-sidebar-notifications = 通知
settings-sidebar-behavior = 行为
settings-sidebar-firmware-tool = DIY固件工具
settings-sidebar-vrc_warnings = VRChat设置警告
settings-sidebar-advanced = 高级选项

## SteamVR settings

settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = SteamVR 追踪器
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    启用或禁用特定的 SteamVR 追踪器
    对于只支持特定追踪器的游戏或应用会很有用。
settings-general-steamvr-trackers-waist = 腰部
settings-general-steamvr-trackers-chest = 胸部
settings-general-steamvr-trackers-left_foot = 左脚
settings-general-steamvr-trackers-right_foot = 右脚
settings-general-steamvr-trackers-left_knee = 左膝
settings-general-steamvr-trackers-right_knee = 右膝
settings-general-steamvr-trackers-left_elbow = 左手肘
settings-general-steamvr-trackers-right_elbow = 右手肘
settings-general-steamvr-trackers-left_hand = 左手
settings-general-steamvr-trackers-right_hand = 右手
settings-general-steamvr-trackers-tracker_toggling = 自动开关追踪器
settings-general-steamvr-trackers-tracker_toggling-description = 根据当前已分配的追踪器，自动选择可用的SteamVR虚拟追踪器
settings-general-steamvr-trackers-tracker_toggling-label = 自动开关追踪器
settings-general-steamvr-trackers-hands-warning =
    <b>警告：</b>开启手部虚拟追踪器将覆盖手柄的追踪信息。
    是否确定？
settings-general-steamvr-trackers-hands-warning-cancel = 取消
settings-general-steamvr-trackers-hands-warning-done = 是

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
settings-general-tracker_mechanics-yaw-reset-smooth-time = 重置航向轴平滑过渡时长（0s时关闭平滑瞬移到位）
settings-general-tracker_mechanics-drift_compensation = 漂移补偿
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    应用反向旋转以补偿IMU的偏航角漂移。
    更改补偿量和使用多少次的重置结果用于计算补偿量。
settings-general-tracker_mechanics-drift_compensation-enabled-label = 漂移补偿
settings-general-tracker_mechanics-drift_compensation-prediction = 预测式漂移补偿
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-prediction-description =
    预测超过先前测量范围的偏航角漂移，并进行补偿。
    适用于追踪器在偏航轴上持续旋转的场景。
settings-general-tracker_mechanics-drift_compensation-prediction-label = 预测式漂移补偿
settings-general-tracker_mechanics-drift_compensation_warning =
    <b>警告：</b> 仅在需要经常重置偏航角
    (大概5~10分钟左右需要重置一次) 时使用漂移补偿。
    
    一些可能需要此补偿的 IMU 包括：
    Joy-Cons、owoTrack 和 MPU（使用旧DMP固件）。
settings-general-tracker_mechanics-drift_compensation_warning-cancel = 取消
settings-general-tracker_mechanics-drift_compensation_warning-done = 了解
settings-general-tracker_mechanics-drift_compensation-amount-label = 补偿量
settings-general-tracker_mechanics-drift_compensation-max_resets-label = 使用几次的重置结果？
settings-general-tracker_mechanics-save_mounting_reset = 保存佩戴重置结果
settings-general-tracker_mechanics-save_mounting_reset-description =
    在SlimeVR服务器关闭时保留追踪器自动佩戴重置结果。适用于一体式动捕服等
    追踪器佩戴位置保持不变的场景。<b>不建议普通用户使用！</b>
settings-general-tracker_mechanics-save_mounting_reset-enabled-label = 保存佩戴重置
settings-general-tracker_mechanics-use_mag_on_all_trackers = 在支持的 IMU 追踪器上启用磁力计
settings-general-tracker_mechanics-use_mag_on_all_trackers-description =
    在所有有固件支持的追踪器上启用磁力计，在磁场稳定的环境中可以减轻飘移。
    可以在个别追踪器上禁用本功能。<b>切换此选项时请勿关闭任何一个追踪器的电源！</b>
settings-general-tracker_mechanics-use_mag_on_all_trackers-label = 在追踪器上启用磁力计
settings-stay_aligned = 持续校准
settings-stay_aligned-description = 持续校准会逐渐将追踪器对齐到设置的放松姿势，减少追踪器漂移的影响
settings-stay_aligned-setup-label = 配置持续校准
settings-stay_aligned-setup-description = 完成“配置持续校准”后才可启动持续校准。
settings-stay_aligned-warnings-drift_compensation = ⚠ 使用持续校准时请关闭漂移补偿功能，避免发生干扰
settings-stay_aligned-enabled-label = 调整追踪器
settings-stay_aligned-hide_yaw_correction-label = 隐藏调整效果（用于与持续校准关闭时进行比较）
settings-stay_aligned-general-label = 通用设置
settings-stay_aligned-relaxed_poses-label = 放松姿势
settings-stay_aligned-relaxed_poses-description = 持续校准使用您设定的放松姿势保持追踪器校准。使用“设置持续校准”来更新放松姿势。
settings-stay_aligned-relaxed_poses-standing = 站立放松姿势
settings-stay_aligned-relaxed_poses-sitting = 椅子上放松姿势
settings-stay_aligned-relaxed_poses-flat = 地面/平躺放松姿势
settings-stay_aligned-relaxed_poses-save_pose = 保存姿势
settings-stay_aligned-relaxed_poses-reset_pose = 重置姿势
settings-stay_aligned-relaxed_poses-close = 关闭
settings-stay_aligned-debug-label = 调试
settings-stay_aligned-debug-description = 在报告持续校准相关问题时，请包含您的以下设置信息
settings-stay_aligned-debug-copy-label = 复制设置信息到剪贴板

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
settings-general-fk_settings-leg_fk-reset_mounting_feet-description-v1 = 在进行普通佩戴重置时强制进行脚部佩戴重置。
settings-general-fk_settings-leg_fk-reset_mounting_feet-v1 = 强制脚部佩戴重置
settings-general-fk_settings-enforce_joint_constraints = 骨骼限制
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints = 强制约束
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = 避免关节旋转超过人体骨骼角度限制
settings-general-fk_settings-enforce_joint_constraints-correct_constraints = 使用约束修正
settings-general-fk_settings-enforce_joint_constraints-correct_constraints-description = 当关节旋转超过人体骨骼角度限制时进行修正
settings-general-fk_settings-ik = 位置数据
settings-general-fk_settings-ik-use_position = 使用位置数据
settings-general-fk_settings-ik-use_position-description = 若追踪器支持，使用来自追踪器的位置数据。启用后，请再次进行完全重置并在游戏中重新校准追踪器。
settings-general-fk_settings-arm_fk = 手臂追踪
settings-general-fk_settings-arm_fk-description = 即使有手臂位置数据可用，也强制使用头显的数据追踪手臂。
settings-general-fk_settings-arm_fk-force_arms = 强制使用头显数据追踪手臂
settings-general-fk_settings-reset_settings = 重置设置
settings-general-fk_settings-reset_settings-reset_hmd_pitch-description = 在进行完整重置时重置头显的俯仰角（垂直旋转）。适合将头显斜戴在头上进行直播或动捕的场景。作为VR使用时不要开启此选项。
settings-general-fk_settings-reset_settings-reset_hmd_pitch = 重置头显俯仰
settings-general-fk_settings-arm_fk-reset_mode-description = 更改佩戴重置时使用的手臂姿势。
settings-general-fk_settings-arm_fk-back = 向后弯折
settings-general-fk_settings-arm_fk-back-description = 默认，重置时大臂向后，小臂向前，类似滑雪。
settings-general-fk_settings-arm_fk-tpose_up = T-pose（抬起）
settings-general-fk_settings-arm_fk-tpose_up-description = 完整重置时手臂垂下，呈立正姿势；佩戴重置时手臂向两侧伸平。
settings-general-fk_settings-arm_fk-tpose_down = T-pose（放下）
settings-general-fk_settings-arm_fk-tpose_down-description = 完整重置时手臂向两侧伸平；佩戴重置时手臂垂下，呈立正姿势。
settings-general-fk_settings-arm_fk-forward = 向前伸平
settings-general-fk_settings-arm_fk-forward-description = 重置时手臂向前伸平，有利于坐姿进行虚拟直播。
settings-general-fk_settings-skeleton_settings-toggles = 骨架设置
settings-general-fk_settings-skeleton_settings-description = 打开或关闭骨架设置。建议保持这些设置不变。
settings-general-fk_settings-skeleton_settings-extended_spine_model = 延伸脊柱模型
settings-general-fk_settings-skeleton_settings-extended_pelvis_model = 延伸骨盆模型
settings-general-fk_settings-skeleton_settings-extended_knees_model = 伸展膝盖模型
settings-general-fk_settings-skeleton_settings-ratios = 骨架比例
settings-general-fk_settings-skeleton_settings-ratios-description = 更改骨架设置的参数。您可能需要在更改后调整身体比例。
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_hip = 用胸部到髋部的数据推算腰部
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_legs = 用胸部到腿部的数据推算腰部
settings-general-fk_settings-skeleton_settings-impute_hip_from_chest_legs = 用胸部到腿部的数据推算髋部
settings-general-fk_settings-skeleton_settings-impute_hip_from_waist_legs = 用腰部到腿部的数据推算髋部
settings-general-fk_settings-skeleton_settings-interp_hip_legs = 平均髋部与腿部间航向轴和横滚轴的数值
settings-general-fk_settings-skeleton_settings-interp_knee_tracker_ankle = 平均膝盖追踪器与小腿间航向轴和横滚轴的数值
settings-general-fk_settings-skeleton_settings-interp_knee_ankle = 平均膝盖与小腿间航向轴和横滚轴的数值
settings-general-fk_settings-self_localization-title = 动作捕捉模式
settings-general-fk_settings-self_localization-description = 动作捕捉模式允许在没有头戴设备或其他追踪器的情况下粗略地跟踪骨架姿态。请注意，本功能需要脚部和头部追踪器，且现阶段依然是实验性的。

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
# This is a unit: 3 trackers, 2 trackers, 1 tracker
# $amount (Number) - Amount of trackers
settings-general-gesture_control-trackers = { $amount } 个追踪器
settings-general-gesture_control-yawResetEnabled = 开启敲击重置航向轴
settings-general-gesture_control-yawResetDelay = 敲击重置航向轴延迟
settings-general-gesture_control-yawResetTaps = 重置航向轴敲击次数
settings-general-gesture_control-fullResetEnabled = 开启敲击完整重置
settings-general-gesture_control-fullResetDelay = 敲击完整重置延迟
settings-general-gesture_control-fullResetTaps = 完整重置敲击次数
settings-general-gesture_control-mountingResetEnabled = 开启敲击重置佩戴
settings-general-gesture_control-mountingResetDelay = 敲击重置佩戴延迟
settings-general-gesture_control-mountingResetTaps = 重置佩戴敲击次数
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = 可触发敲击重置的追踪器数量
settings-general-gesture_control-numberTrackersOverThreshold-description = 如敲击重置不起作用请增加此值。请在保证敲击重置可用的情况下将此值设置的尽可能低，否则会导致误报。

## Appearance settings

settings-interface-appearance = 外观
settings-general-interface-dev_mode = 开发者模式
settings-general-interface-dev_mode-description = 如果你需要深入的资料或对连接的追踪器进行进阶调整，开启此模式将会非常有用。
settings-general-interface-dev_mode-label = 开发者模式
settings-general-interface-theme = 主题颜色
settings-general-interface-show-navbar-onboarding = 在导航栏上显示 “{ navbar-onboarding }”
settings-general-interface-show-navbar-onboarding-description = 本选项设置是否将 "{ navbar-onboarding }" 按钮显示在导航栏上。
settings-general-interface-show-navbar-onboarding-label = 显示 “{ navbar-onboarding }”
settings-general-interface-lang = 选择语言
settings-general-interface-lang-description = 更改要使用的默认语言
settings-general-interface-lang-placeholder = 选择要使用的语言
# Keep the font name untranslated
settings-interface-appearance-font = 字体
settings-interface-appearance-font-description = 修改使用的字体。
settings-interface-appearance-font-placeholder = 默认字体
settings-interface-appearance-font-os_font = 系统字体
settings-interface-appearance-font-slime_font = 默认字体
settings-interface-appearance-font_size = 字体缩放
settings-interface-appearance-font_size-description = 这会影响除此设置面板外所有界面的字体大小。
settings-interface-appearance-decorations = 使用系统原生窗口标题栏
settings-interface-appearance-decorations-description = 这个选项开启后，将不会显示SlimeVR的标题栏，而是显示使用系统原生标题栏。
settings-interface-appearance-decorations-label = 使用系统原生窗口标题栏

## Notification settings

settings-interface-notifications = 通知
settings-general-interface-serial_detection = 串口设备检测
settings-general-interface-serial_detection-description = 每次插入可能是追踪器的新串口设备时，此选项都会显示一个弹出窗口。这有助于改进追踪器的设置过程。
settings-general-interface-serial_detection-label = 串口设备检测
settings-general-interface-feedback_sound = 声音反馈
settings-general-interface-feedback_sound-description = 开启此选项会在触发重置时发出提示音
settings-general-interface-feedback_sound-label = 声音反馈
settings-general-interface-feedback_sound-volume = 提示音音量
settings-general-interface-connected_trackers_warning = 已连接追踪器警告
settings-general-interface-connected_trackers_warning-description = 启用本选项后，每次当退出 SlimeVR 时仍有追踪器连接着会显示通知，提醒你在使用完毕时关闭追踪器电源来节省电池电量。
settings-general-interface-connected_trackers_warning-label = 退出时，有追踪器连接中则显示警告

## Behavior settings

settings-interface-behavior = 行为
settings-general-interface-use_tray = 最小化至任务栏
settings-general-interface-use_tray-description = 关闭 SlimeVR 窗口时，SlimeVR 服务器将会隐藏至任务栏图标而不会直接退出，可以继续使用。
settings-general-interface-use_tray-label = 最小化至任务栏
settings-general-interface-discord_presence = 向Discord发送状态
settings-general-interface-discord_presence-description = 告诉你的Discord客户端你正在使用SlimeVR，同时显示你正在使用的IMU追踪器数量。
settings-general-interface-discord_presence-label = 向Discord发送状态
settings-general-interface-discord_presence-message =
    { $amount ->
        [0] { "" }
       *[other] 正在使用 { $amount } 个追踪器
    }
settings-interface-behavior-error_tracking = 通过 Sentry.io 收集错误信息
settings-interface-behavior-error_tracking-description_v2 =
    <h1>您是否同意收集匿名的错误信息？</h1>
    
    <b>我们不会收集您的个人信息</b> ，例如您的 IP 地址或 WiFi 信息。SlimeVR 重视您的隐私！
    
    为了提供最佳用户体验，我们会收集匿名错误报告、性能指标和操作系统信息。这有助于我们检测 SlimeVR 的错误和问题。这些指标将通过 Sentry.io 收集。
settings-interface-behavior-error_tracking-label = 向开发人员发送错误信息
settings-interface-behavior-bvh_directory = BVH记录保存目录
settings-interface-behavior-bvh_directory-description = 选择保存BVH记录文件的目录
settings-interface-behavior-bvh_directory-label = BVH记录保存目录

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
    <b>警告：</b> 这会将追踪器重置为出厂设置。
    这意味着 Wi-Fi 凭据和校准数据 <b>都将丢失！</b>
settings-serial-factory_reset-warning-ok = 我已知晓
settings-serial-factory_reset-warning-cancel = 取消
settings-serial-serial_select = 选择串行端口
settings-serial-auto_dropdown_item = 自动
settings-serial-get_wifi_scan = 扫描可用WiFi
settings-serial-file_type = 纯文本
settings-serial-save_logs = 保存到文件
settings-serial-send_command = 发送
settings-serial-send_command-placeholder = 输入指令...
settings-serial-send_command-warning = <b>警告:</b>运行串口命令可能导致数据丢失或使追踪器无法正常工作。
settings-serial-send_command-warning-ok = 我已知晓
settings-serial-send_command-warning-cancel = 取消

## OSC router settings

settings-osc-router = OSC 路由
# This cares about multilines
settings-osc-router-description =
    从另一个程序转发 OSC 信息。
    例如需要在 VRChat 同时使用另一个 OSC 程序。
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
settings-osc-vrchat-description-v1 =
    修改与OSC追踪器相关的设置，可用于在不使用SteamVR时传输追踪数据（如一体机模式）。
    在使用OSC追踪器时，请确保在VRChat的圆盘菜单中开启OSC功能（选项 > OSC > 开启）。
    为了从VRChat中接收到头显和控制器的数据，还需要在VRChat主菜单中设置的“动捕与IK”下，
    打开“通过OSC共享头显和手柄信息”。
settings-osc-vrchat-enable = 启用
settings-osc-vrchat-enable-description = 切换数据的发送和接收
settings-osc-vrchat-enable-label = 启用
settings-osc-vrchat-oscqueryEnabled = 启用 OSCQuery
settings-osc-vrchat-oscqueryEnabled-description =
    OSCQuery 会自动检测正在运行的 VRChat 并向其发送数据。
    它还可以向 VRChat 通告自身，以便接收头戴显示器和控制器数据。
    要允许从 VRChat 接收头戴显示器和控制器数据，请进入主菜单的设置
    并启用“允许发送头部和手腕VR追踪OSC数据”。
settings-osc-vrchat-oscqueryEnabled-label = 启用 OSCQuery
settings-osc-vrchat-network = 网络端口
settings-osc-vrchat-network-description-v1 = 设置传输OSC数据的端口。用于VRChat时无需修改。
settings-osc-vrchat-network-port_in =
    .label = 输入端口
    .placeholder = 输入端口（默认 9001）
settings-osc-vrchat-network-port_out =
    .label = 输出端口
    .placeholder = 输出端口（默认 9000）
settings-osc-vrchat-network-address = 网络地址
settings-osc-vrchat-network-address-description-v1 = 选择传输OSC数据的IP地址。用于VRChat时无需修改。
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
settings-osc-vmc-vrm-untitled_model = 无标题模型
settings-osc-vmc-vrm-file_select = 拖曳文件或 <u>浏览文件</u> 以加载模型
settings-osc-vmc-anchor_hip = 髋部锚定
settings-osc-vmc-anchor_hip-description = 将追踪锚定在髋部，有利于坐姿进行虚拟直播。若本选项无法切换，请加载 VRM 模型。
settings-osc-vmc-anchor_hip-label = 髋部锚定
settings-osc-vmc-mirror_tracking = 镜像追踪
settings-osc-vmc-mirror_tracking-description = 水平镜像追踪结果
settings-osc-vmc-mirror_tracking-label = 镜像追踪

## Common OSC settings

settings-osc-common-network-ports_match_error = OSC路由的输入和输出端口不能相同！
settings-osc-common-network-port_banned_error = 无法使用端口{ $port } !

## Advanced settings

settings-utils-advanced = 高级选项
settings-utils-advanced-reset-gui = 重置GUI设置
settings-utils-advanced-reset-gui-description = 恢复界面设置的初始配置。
settings-utils-advanced-reset-gui-label = 重置GUI设置
settings-utils-advanced-reset-server = 重置追踪设置
settings-utils-advanced-reset-server-description = 恢复追踪设置的初始配置。
settings-utils-advanced-reset-server-label = 重置追踪设置
settings-utils-advanced-reset-all = 重置所有设置
settings-utils-advanced-reset-all-description = 恢复界面设置与追踪设置的初始配置。
settings-utils-advanced-reset-all-label = 重置所有设置
settings-utils-advanced-reset_warning =
    { $type ->
        [gui]
            <b>警告：</b> 将要恢复界面设置为初始配置。
            是否确认进行此操作？
        [server]
            <b>警告：</b> 将要恢复追踪设置为初始配置。
            是否确认进行此操作？
       *[all]
            <b>警告：</b> 将要恢复所有设置为初始配置。
            是否确认进行此操作？
    }
settings-utils-advanced-reset_warning-reset = 重置设置
settings-utils-advanced-reset_warning-cancel = 取消
settings-utils-advanced-open_data-v1 = 配置文件夹
settings-utils-advanced-open_data-description-v1 = 在文件管理器中打开SlimeVR的配置文件夹，查看配置文件。
settings-utils-advanced-open_data-label = 打开文件夹
settings-utils-advanced-open_logs = 日志文件夹
settings-utils-advanced-open_logs-description = 在文件管理器中打开SlimeVR的日志文件夹，查看SlimeVR的日志文件。
settings-utils-advanced-open_logs-label = 打开文件夹

## Home Screen

settings-home-list-layout = 追踪器列表布局
settings-home-list-layout-desc = 选择主界面的显示布局
settings-home-list-layout-grid = 网格
settings-home-list-layout-table = 列表

## Tracking Checlist

settings-tracking_checklist-active_steps = 启用的检查项
settings-tracking_checklist-active_steps-desc = 追踪检查清单中所有项目的列表。您可以禁用不需要的步骤。

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
onboarding-wifi_creds-v2 = 通过 Wi-Fi 连接
# This cares about multilines
onboarding-wifi_creds-description-v2 =
    大多数追踪器（例如官方的 SlimeVR 追踪器）都通过 Wi-Fi 连接服务器。
    请输入当前设备连接的网络的 Wi-Fi 凭证。
    
    请确保输入的是 2.4GHz 频段的 Wi-Fi 凭证！
onboarding-wifi_creds-skip = 跳过 Wi-Fi 设置
onboarding-wifi_creds-submit = 提交！
onboarding-wifi_creds-ssid =
    .label = SSID
    .placeholder = 输入 SSID
onboarding-wifi_creds-ssid-required = Wi-Fi 名称为必填项
onboarding-wifi_creds-password =
    .label = 密码
    .placeholder = 输入密码
onboarding-wifi_creds-dongle-title = 通过接收器连接
onboarding-wifi_creds-dongle-description = 如果你的追踪器附带接收器，将其插入电脑即可直接开始使用！
onboarding-wifi_creds-dongle-wip = 此部分仍在开发中。将来会推出用于管理接收器连接追踪器的专属页面。
onboarding-wifi_creds-dongle-continue = 继续，使用接收器

## Mounting setup

onboarding-reset_tutorial-back = 返回到佩戴校准
onboarding-reset_tutorial = 重置教程
onboarding-reset_tutorial-explanation = 追踪器在使用时可能会由于IMU的航向角漂移或是因为您移动了它们而失准。您有几种方法来解决这个问题。
onboarding-reset_tutorial-skip = 跳过步骤
# Cares about multiline
onboarding-reset_tutorial-0 =
    敲击 { $taps } 次高亮显示的追踪器以触发航向轴重置。
    
    这将使追踪器朝向与您的头显相同的方向。
# Cares about multiline
onboarding-reset_tutorial-1 =
    敲击 { $taps } 次高亮显示的追踪器以触发完整重置。
    
    此功能需要你站直（i-pose）后使用。 在重置实际发生前有 3 秒延迟（可配置）。
    这将完全重置所有追踪器的位置和旋转，应该能解决大多数问题。
# Cares about multiline
onboarding-reset_tutorial-2 =
    敲击 { $taps } 次高亮显示的追踪器以触发佩戴重置。
    
    佩戴重置能对追踪器实际的配戴方式进行调整，所以如果你不小心移动了追踪器并将它们的佩戴方向改变了很多，这个功能将有所帮助。
    
    你需要摆出一个像滑雪那样的姿势，就像在运行自动设置佩戴向导时做的那样，在重置实际发生前有 3 秒延迟（可配置）。

## Setup start

onboarding-home = 欢迎来到 SlimeVR
onboarding-home-start = 我准备好了！

## Setup done

onboarding-done-title = 都搞定啦！
onboarding-done-description = 享受你的全身追踪体验吧
onboarding-done-close = 关闭向导

## Tracker connection setup

onboarding-connect_tracker-back = 返回到 Wi-Fi 凭据设置
onboarding-connect_tracker-title = 连接追踪器
onboarding-connect_tracker-description-p0-v1 = 来到了我第二喜欢的环节，连接追踪器！
onboarding-connect_tracker-description-p1-v1 = 一次一个，将所有追踪器依次通过USB口连接至电脑。
onboarding-connect_tracker-issue-serial = QAQ 我在连接时遇到问题！
onboarding-connect_tracker-usb = USB 追踪器
onboarding-connect_tracker-connection_status-none = 正在寻找追踪器
onboarding-connect_tracker-connection_status-serial_init = 正在连接到串口设备
onboarding-connect_tracker-connection_status-obtaining_mac_address = 获取追踪器的mac地址
onboarding-connect_tracker-connection_status-provisioning = 正在发送 Wi-Fi 凭据
onboarding-connect_tracker-connection_status-connecting = 正在发送 Wi-Fi 凭据
onboarding-connect_tracker-connection_status-looking_for_server = 正在寻找服务器
onboarding-connect_tracker-connection_status-connection_error = 无法连接到 Wi-Fi
onboarding-connect_tracker-connection_status-could_not_find_server = 未找到到服务器
onboarding-connect_tracker-connection_status-done = 已连接到服务器
onboarding-connect_tracker-connection_status-no_serial_log = 无法从跟踪器获取日志
onboarding-connect_tracker-connection_status-no_serial_device_found = 无法通过USB找到追踪器
onboarding-connect_serial-error-modal-no_serial_log = 追踪器开关是否打开？
onboarding-connect_serial-error-modal-no_serial_log-desc = 请确保追踪器已经开启并连接到电脑
onboarding-connect_serial-error-modal-no_serial_device_found = 未检测到追踪器
onboarding-connect_serial-error-modal-no_serial_device_found-desc =
    请使用附带的USB线将追踪器连接到电脑并打开追踪器开关。
    若仍无法工作：
     - 尝试其他的USB线
     - 尝试其他的USB接口
     - 尝试重新安装SlimeVR服务端并在安装时勾选“USB驱动”
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
onboarding-calibration_tutorial-description-v1 = 打开追踪器后，将它们放在稳定的平面上一段时间，以便进行校准。追踪器开机后可随时进行校准 - 本页仅是一个校准教程。首先，点击 “{ onboarding-calibration_tutorial-calibrate }” 按钮，然后 <b>不要移动您的追踪器！</b>
onboarding-calibration_tutorial-calibrate = 我已经把追踪器放在桌子上了
onboarding-calibration_tutorial-status-waiting = 等待你的操作
onboarding-calibration_tutorial-status-calibrating = 校准中
onboarding-calibration_tutorial-status-success = 很好！
onboarding-calibration_tutorial-status-error = 追踪器被移动！
onboarding-calibration_tutorial-skip = 跳过教程

## Tracker assignment tutorial

onboarding-assignment_tutorial = 在佩戴 Slime 追踪器之前的准备工作
onboarding-assignment_tutorial-first_step = 1. 根据您分配的情况在追踪器上粘贴标识身体部位的贴纸（如果有）
# This text has a character limit of around 11 characters, so please keep it short
onboarding-assignment_tutorial-sticker = 贴纸
onboarding-assignment_tutorial-second_step-v2 = 2. 将绑带穿过追踪器，确保绑带有粘扣面的朝向与追踪器上的笑脸标志同向：
onboarding-assignment_tutorial-second_step-continuation-v2 = 扩展追踪器的粘扣朝向应如下图所示：
onboarding-assignment_tutorial-done = 我把贴纸和绑带都弄好了！

## Tracker assignment setup

onboarding-assign_trackers-back = 返回 Wi-Fi 凭据设置
onboarding-assign_trackers-title = 分配追踪器
onboarding-assign_trackers-description = 让我们选择追踪器的佩戴位置。点击对应部位即可分配。
onboarding-assign_trackers-unassign_all = 取消分配所有追踪器
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned = { $assigned }/{ $trackers } 个追踪器已分配
onboarding-assign_trackers-advanced = 显示高级分配部位
onboarding-assign_trackers-next = 所有的追踪器都分配好了
onboarding-assign_trackers-mirror_view = 镜像显示
onboarding-assign_trackers-option-amount = { $trackersCount } 点
onboarding-assign_trackers-option-label =
    { $mode ->
        [lower-body] 下半身追踪
        [core] 身体追踪
        [enhanced-core] 拓展身体追踪
        [full-body] 全身追踪
       *[all] 所有可选追踪
    }
onboarding-assign_trackers-option-description =
    { $mode ->
        [lower-body] VR全身追踪最少需要的点数
        [core] + 更准确的躯干追踪
        [enhanced-core] + 脚部转动
        [full-body] + 上臂追踪
       *[all] 所有可用的追踪器分配
    }

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
# Multiline text
onboarding-choose_mounting-description = 佩戴方向校准用于确定您身上的追踪器的朝向。
onboarding-choose_mounting-auto_mounting = 自动设置佩戴方向
# Italicized text
onboarding-choose_mounting-auto_mounting-label-v2 = 推荐使用
onboarding-choose_mounting-auto_mounting-description = 这将需要你做2个动作以自动检测所有追踪器的佩戴方向
onboarding-choose_mounting-manual_mounting = 手动设置佩戴方向
# Italicized text
onboarding-choose_mounting-manual_mounting-label-v2 = 可能不够精确
onboarding-choose_mounting-manual_mounting-description = 这将需要你手动选择每个追踪器的佩戴方向
# Multiline text
onboarding-choose_mounting-manual_modal-title = 确定要进行自动佩戴校准？
onboarding-choose_mounting-manual_modal-description = <b>我们建议新手使用手动佩戴校准</b>，因为自动佩戴校准的姿势要一次做正确比较困难，可能需要一些练习。
onboarding-choose_mounting-manual_modal-confirm = 我已知晓
onboarding-choose_mounting-manual_modal-cancel = 取消

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
onboarding-automatic_mounting-done-restart = 再试一次
onboarding-automatic_mounting-mounting_reset-title = 佩戴重置
onboarding-automatic_mounting-mounting_reset-step-0 = 1. 双腿弯曲以滑雪的姿势蹲下，上身向前倾斜，手臂弯曲。
onboarding-automatic_mounting-mounting_reset-step-1 = 按下佩戴重置按钮并等待 3 秒钟，然后追踪器的佩戴方向将被重置。
onboarding-automatic_mounting-mounting_reset-feet-step-0 = 1. 双脚朝前，踮起脚尖站立。或者，您也可以坐在椅子上完成这个动作。
onboarding-automatic_mounting-mounting_reset-feet-step-1 = 2. 点击“脚部校准”按钮并等待 3 秒，追踪器的佩戴方向将会重置。
onboarding-automatic_mounting-preparation-title = 准备
onboarding-automatic_mounting-preparation-v2-step-0 = 1. 按下“完全重置”按钮。
onboarding-automatic_mounting-preparation-v2-step-1 = 2. 站直并向前看，双臂放在身体两侧。
onboarding-automatic_mounting-preparation-v2-step-2 = 3. 保持姿势，直到 3 秒计时结束。
onboarding-automatic_mounting-put_trackers_on-title = 穿戴好追踪器
onboarding-automatic_mounting-put_trackers_on-description = 为了校准佩戴方向，我们将使用你刚才分配的追踪器。戴上你所有的追踪器，你可以在右边的图中看到哪个追踪器对应哪个。
onboarding-automatic_mounting-put_trackers_on-next = 所有的追踪器都已开启！
onboarding-automatic_mounting-return-home = 完成

## Tracker manual proportions setupa

onboarding-manual_proportions-back-scaled = 返回使用缩放比例
onboarding-manual_proportions-title = 手动调整身体比例
onboarding-manual_proportions-fine_tuning_button = 自动微调身体比例
onboarding-manual_proportions-fine_tuning_button-disabled-tooltip = 请连接 VR头戴显示器 以使用自动微调
onboarding-manual_proportions-export = 导出身体比例
onboarding-manual_proportions-import = 导入身体比例
onboarding-manual_proportions-file_type = 身体比例文件
onboarding-manual_proportions-normal_increment = 正常调整
onboarding-manual_proportions-precise_increment = 精细调整
onboarding-manual_proportions-grouped_proportions = 分组身体比例
onboarding-manual_proportions-all_proportions = 所有身体比例
onboarding-manual_proportions-estimated_height = 估测用户身高

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
onboarding-automatic_proportions-requirements-descriptionv2 =
    你需要足够的追踪器以追踪脚部（通常至少为 5 个）。
    你已经穿戴好并打开追踪器和头戴设备的电源。
    你的追踪器和头戴设备都已经连接到 SlimeVR 服务器并正常工作（没有卡顿、断联等问题）。
    你的头戴设备正在向 SlimeVR 服务器回报位置信息（通常需要 SteamVR 正在运行且通过 SlimeVR 的 SteamVR 驱动连接到 SlimeVR）。
    你的追踪器正常工作并能反应实际运动（进行过完整重置和佩戴重置，踢腿、弯腰、坐下等动作时虚拟骨骼向正确的方向弯曲）。
onboarding-automatic_proportions-requirements-next = 我已阅读
onboarding-automatic_proportions-check_height-title-v3 = 测量你的头戴显示器的高度
onboarding-automatic_proportions-check_height-description-v2 = 您的头戴显示器 （HMD） 高度应略小于您的身高，因为头戴显示器会测量您眼睛的高度。此测量值将用作计算您身体比例的基准。
# All the text is in bold!
onboarding-automatic_proportions-check_height-calculation_warning-v3 = 在 <u>站直</u> 时开始测量以计算您的身高。注意不要将手举过头显，不然可能会影响测量结果！
onboarding-automatic_proportions-check_height-guardian_tip =
    如果你正在使用一体机进行串流，请确保开启并设置了安全边界，
    以获取正确的高度信息。
# Context is that the height is unknown
onboarding-automatic_proportions-check_height-unknown = 未知
# Shows an element below it
onboarding-automatic_proportions-check_height-hmd_height2 = 您的头戴显示器的高度是：
onboarding-automatic_proportions-check_height-measure-start = 开始测量
onboarding-automatic_proportions-check_height-measure-stop = 停止测量
onboarding-automatic_proportions-check_height-measure-reset = 重试测量
onboarding-automatic_proportions-check_height-next_step = 数值没问题
onboarding-automatic_proportions-check_floor_height-title = 测量您的地板高度（可选）
onboarding-automatic_proportions-check_floor_height-description = 在某些情况下，头戴显示器可能无法正确设置地板高度，从而导致头显测得的身高高于实际身高。您可以测量地板的“高度”以校正头显的高度。
# All the text is in bold!
onboarding-automatic_proportions-check_floor_height-calculation_warning-v2 = 开始测量并将控制器放在地板上以测量地板高度。如果您确定您的地板高度是正确的，则可以跳过此步骤。
# Shows an element below it
onboarding-automatic_proportions-check_floor_height-floor_height = 您的地板高度为：
onboarding-automatic_proportions-check_floor_height-full_height = 估计您的身高为：
onboarding-automatic_proportions-check_floor_height-measure-start = 开始测量
onboarding-automatic_proportions-check_floor_height-measure-stop = 停止测量
onboarding-automatic_proportions-check_floor_height-measure-reset = 重试测量
onboarding-automatic_proportions-check_floor_height-skip_step = 跳过这一步并保存
onboarding-automatic_proportions-check_floor_height-next_step = 使用测量的地板高度并保存
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
onboarding-automatic_proportions-error_modal-v2 =
    <b>警告：</b> 估算身体比例时发生错误！
    这可能是因为佩戴校准存在问题，请确保追踪器工作正常后再试一次。
     <docs>请查看文档</docs> 或加入我们的 <discord>Discord</discord> 频道寻求帮助 ^_^
onboarding-automatic_proportions-error_modal-confirm = 了解！
onboarding-automatic_proportions-smol_warning =
    您配置的身高 { $height } 小于可接受的最小身高 { $minHeight }。
    <b>请重新进行测量并确保测量结果是正确的。</b>
onboarding-automatic_proportions-smol_warning-cancel = 返回

## User height calibration

onboarding-user_height-title = 你的身高是多少？
onboarding-user_height-description = 我们需要你的身高来计算躯干比例，以准确呈现你的动作。你可以让 SlimeVR 自动计算身高，也可以手动输入。
onboarding-user_height-need_head_tracker = 进行校准需要具备定位功能的头戴显示器与控制器。
onboarding-user_height-calculate = 自动计算我的身高
onboarding-user_height-next_step = 保存并继续
onboarding-user_height-manual-proportions = 手动调整躯干比例
onboarding-user_height-calibration-title = 校准进度
onboarding-user_height-calibration-RECORDING_FLOOR = 用控制器的前端触碰地面
onboarding-user_height-calibration-WAITING_FOR_RISE = 回到站姿
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK = 回到站姿并向前看
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-ok = 确保你的头部水平
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-low = 不要往地面看
onboarding-user_height-calibration-WAITING_FOR_FW_LOOK-high = 不要往高处看
onboarding-user_height-calibration-WAITING_FOR_CONTROLLER_PITCH = 确保控制器方向朝下
onboarding-user_height-calibration-RECORDING_HEIGHT = 重新站直并保持姿势不动！
onboarding-user_height-calibration-DONE = 完成！
onboarding-user_height-calibration-ERROR_TIMEOUT = 校准超时，请重试。
onboarding-user_height-calibration-ERROR_TOO_HIGH = 检测到的用户身高数值过大，请重试。
onboarding-user_height-calibration-ERROR_TOO_SMALL = 检测到的用户身高数值过小。请确保在校准结束时身体站直并平视前方。
onboarding-user_height-calibration-error = 校准失败
onboarding-user_height-manual-tip = 在调整身高时，尝试不同姿势，看看骨架是否与你的身体动作匹配。
onboarding-user_height-reset-warning =
    <b>警告：</b> 这会将您的身体比例重置为仅基于身高的默认比例。
    您确定要执行此操作吗？

## Stay Aligned setup

onboarding-stay_aligned-title = 持续校准
onboarding-stay_aligned-description = 配置持续校准以保持追踪器校准
onboarding-stay_aligned-put_trackers_on-title = 穿戴好追踪器
onboarding-stay_aligned-put_trackers_on-description = 为了保存放松姿势，我们将使用你刚才分配的追踪器。戴上你所有的追踪器，你可以在右边的图中看到哪个追踪器对应哪个。
onboarding-stay_aligned-put_trackers_on-trackers_warning = 您当前已经连接和分配的追踪器数量少于 5 个！这是持续校准正常运行所需的最小追踪器数量。
onboarding-stay_aligned-put_trackers_on-next = 所有的追踪器都已开启！
onboarding-stay_aligned-verify_mounting-title = 检查追踪器佩戴
onboarding-stay_aligned-verify_mounting-step-0 = 持续校准需要良好的追踪器佩戴固定。否则，持续校准的效果可能会不太理想。
onboarding-stay_aligned-verify_mounting-step-1 = 1. 请在站立时四处移动。
onboarding-stay_aligned-verify_mounting-step-2 = 2. 请坐下并移动腿和脚
onboarding-stay_aligned-verify_mounting-step-3 = 3. 如果追踪器不在正确的位置上，请重新进行这个流程。
onboarding-stay_aligned-verify_mounting-redo_mounting = 重做佩戴校准
onboarding-stay_aligned-preparation-title = 准备
onboarding-stay_aligned-preparation-tip = 站直并向前看，双臂放在身体两侧。
onboarding-stay_aligned-relaxed_poses-standing-title = 站立放松姿势
onboarding-stay_aligned-relaxed_poses-standing-step-0 = 1. 以舒适的姿势站立并放松。
onboarding-stay_aligned-relaxed_poses-standing-step-1-v2 = 2. 按下“保存姿势”按钮。
onboarding-stay_aligned-relaxed_poses-sitting-title = 椅子上放松姿势
onboarding-stay_aligned-relaxed_poses-sitting-step-0 = 1. 以舒适的姿势坐下并放松。
onboarding-stay_aligned-relaxed_poses-sitting-step-1-v2 = 2. 按下“保存姿势”按钮。
onboarding-stay_aligned-relaxed_poses-flat-title = 地面/平躺放松姿势
onboarding-stay_aligned-relaxed_poses-flat-step-0 = 1. 以舒适的姿势坐或躺在地面上，保持腿在前方并放松。
onboarding-stay_aligned-relaxed_poses-flat-step-1-v2 = 2. 按下“保存姿势”按钮。
onboarding-stay_aligned-relaxed_poses-skip_step = 跳过
onboarding-stay_aligned-done-title = 持续校准已开启！
onboarding-stay_aligned-done-description = 持续校准已设定完成！
onboarding-stay_aligned-done-description-2 = 设置完成！如果要重新校准放松姿势，可以重新进行该过程
onboarding-stay_aligned-previous_step = 上一步
onboarding-stay_aligned-next_step = 下一步
onboarding-stay_aligned-restart = 重新开始
onboarding-stay_aligned-done = 完成

## Home

home-no_trackers = 未检测到或未分配追踪器
home-settings = 主界面设置
home-settings-close = 关闭

## Trackers Still On notification

trackers_still_on-modal-title = 有追踪器的电源还开着
trackers_still_on-modal-description =
    至少有一个追踪器的电源还开着。
    确定要退出 SlimeVR 吗？
trackers_still_on-modal-confirm = 退出 SlimeVR
trackers_still_on-modal-cancel = 等会…

## Status system

status_system-StatusTrackerReset = 建议执行完整重置，因为有至少一个追踪器未被调整。
status_system-StatusSteamVRDisconnected =
    { $type ->
        [steamvr_feeder] 尚未连接到 SlimeVR Feeder App
       *[other] 尚未通过 SlimeVR 驱动程序连接到 SteamVR
    }
status_system-StatusTrackerError = { $trackerName } 追踪器发生错误
status_system-StatusUnassignedHMD = VR头显应被分配为头部追踪器。
status_system-StatusPublicNetwork = 您当前的网络类型设置为“公开”。 这可能会导致SlimeVR功能出现异常。<PublicFixLink>点击这里查看如何进行设置。</PublicFixLink>

## Firmware tool globals

firmware_tool-next_step = 下一步
firmware_tool-previous_step = 上一步
firmware_tool-ok = 看起来不错
firmware_tool-retry = 重试
firmware_tool-loading = 加载中...

## Firmware tool Steps

firmware_tool = DIY固件工具
firmware_tool-description = 允许您配置和烧录 DIY 追踪器固件
firmware_tool-not_available = 哦不，固件工具目前不可用。稍后再来！
firmware_tool-not_compatible = 固件工具与此版本的服务端不兼容。请更新您的服务端！
firmware_tool-select_source = 选择要刷写的固件
firmware_tool-select_source-description = 选择要在电路板上刷写的固件
firmware_tool-select_source-error = 无法加载固件源代码
firmware_tool-select_source-board_type = 电路板类型
firmware_tool-select_source-firmware = 固件来源
firmware_tool-select_source-version = 固件版本
firmware_tool-select_source-official = 官方
firmware_tool-select_source-dev = 开发版
firmware_tool-board_defaults = 配置电路板
firmware_tool-board_defaults-description = 设置引脚与其他和硬件相关的配置
firmware_tool-board_defaults-add = 新增
firmware_tool-board_defaults-reset = 恢复默认设置
firmware_tool-board_defaults-error-required = 必填字段
firmware_tool-board_defaults-error-format = 格式无效
firmware_tool-board_defaults-error-format-number = 不是数字
firmware_tool-flash_method_step = 固件烧录方式
firmware_tool-flash_method_step-description = 请选择您要使用的固件烧录方式
firmware_tool-flash_method_step-ota-v2 =
    .label = Wi-Fi
    .description = 选择无线OTA更新方式。你的追踪器将会使用Wi-Fi来更新固件。只在已设置完成的追踪器上生效。
firmware_tool-flash_method_step-ota-info = 将会使用你的Wi-Fi凭证来刷写追踪器的固件并确保一切正常。<b>我们不会存储你的Wi-Fi凭证!</b>
firmware_tool-flash_method_step-serial-v2 =
    .label = USB
    .description = 使用USB线连接来更新你的追踪器。
firmware_tool-flashbtn_step = 按下启动/Boot按钮
firmware_tool-flashbtn_step-description = 在进入下一步之前，您需要做几件事情。
firmware_tool-flashbtn_step-board_SLIMEVR = 关闭追踪器，拆下外壳（如果有的话），使用 USB 数据线连接到计算机，然后根据您的 SlimeVR 电路板版本执行以下步骤之一：
firmware_tool-flashbtn_step-board_SLIMEVR-r11-v2 = 保持短接电路板正面边缘第二个矩形 FLASH 焊盘和单片机模块的金属屏蔽罩，同时打开追踪器电源。追踪器的指示灯将会短暂闪烁。
firmware_tool-flashbtn_step-board_SLIMEVR-r12-v2 = 保持短接电路板正面圆形 FLASH 焊盘和单片机模块的金属屏蔽罩，同时打开追踪器电源。追踪器的指示灯将会短暂闪烁。
firmware_tool-flashbtn_step-board_SLIMEVR-r14-v2 = 按住电路板正面的 FLASH 按钮的同时打开追踪器电源。追踪器的指示灯将会短暂闪烁。
firmware_tool-flashbtn_step-board_OTHER =
    在烧录固件之前，您可能需要将追踪器置于bootloader模式。
    通常这意味着在开始固件烧录过程之前，按下板上的引导/boot按钮。
    如果固件烧录过程在开始时超时，这通常表示追踪器没有处于bootloader模式。
    请参考您的追踪器电路板的固件烧录说明，了解如何进入bootloader模式。
firmware_tool-flash_method_ota-title = 通过Wi-Fi刷写
firmware_tool-flash_method_ota-devices = 检测到的 OTA 设备：
firmware_tool-flash_method_ota-no_devices = 没有可以使用 OTA 更新的电路板，请确保选择了正确的电路板类型
firmware_tool-flash_method_serial-title = 通过USB刷写
firmware_tool-flash_method_serial-wifi = Wi-Fi 凭证：
firmware_tool-flash_method_serial-devices-label = 检测到的串口设备：
firmware_tool-flash_method_serial-devices-placeholder = 选择串口设备
firmware_tool-flash_method_serial-no_devices = 未检测到兼容的串口设备，请确保追踪器已插入
firmware_tool-build_step = 构建中
firmware_tool-build_step-description = 固件正在构建中，请稍候
firmware_tool-flashing_step = 固件烧录中
firmware_tool-flashing_step-description = 正在向追踪器烧录固件，请按照屏幕上的指示操作
firmware_tool-flashing_step-warning-v2 = 除非特别指示，在固件传输过程中请勿断开或关闭追踪器，否则可能会导致您的电路板无法使用
firmware_tool-flashing_step-flash_more = 烧录更多的追踪器
firmware_tool-flashing_step-exit = 退出

## firmware tool build status

firmware_tool-build-QUEUED = 等待构建中....
firmware_tool-build-CREATING_BUILD_FOLDER = 正在创建 build 文件夹
firmware_tool-build-DOWNLOADING_SOURCE = 正在下载源代码
firmware_tool-build-EXTRACTING_SOURCE = 正在解压源代码
firmware_tool-build-BUILDING = 正在构建固件
firmware_tool-build-SAVING = 正在保存构建结果
firmware_tool-build-DONE = 构建完成
firmware_tool-build-ERROR = 无法构建固件

## Firmware update status

firmware_update-status-DOWNLOADING = 正在下载固件
firmware_update-status-NEED_MANUAL_REBOOT-v2 = 请关闭您的追踪器，然后再重新打开
firmware_update-status-AUTHENTICATING = 正在确认追踪器连接
firmware_update-status-UPLOADING = 正在传输固件
firmware_update-status-SYNCING_WITH_MCU = 正在与追踪器同步
firmware_update-status-REBOOTING = 正在重启追踪器
firmware_update-status-PROVISIONING = 正在设置 Wi-Fi 凭据
firmware_update-status-DONE = 更新完成！
firmware_update-status-ERROR_DEVICE_NOT_FOUND = 未找到设备
firmware_update-status-ERROR_TIMEOUT = 更新过程超时
firmware_update-status-ERROR_DOWNLOAD_FAILED = 无法下载固件
firmware_update-status-ERROR_AUTHENTICATION_FAILED = 无法确认追踪器连接
firmware_update-status-ERROR_UPLOAD_FAILED = 无法传输固件
firmware_update-status-ERROR_PROVISIONING_FAILED = 无法设置 Wi-Fi 凭据
firmware_update-status-ERROR_UNSUPPORTED_METHOD = 更新方式不被支持
firmware_update-status-ERROR_UNKNOWN = 未知错误

## Dedicated Firmware Update Page

firmware_update-title = 固件更新
firmware_update-devices = 可用设备
firmware_update-devices-description = 请选择要更新到最新版本 SlimeVR 固件的跟踪器
firmware_update-no_devices = 请确保您要更新的追踪器已打开并连接到 Wi-Fi！
firmware_update-changelog-title = 更新至{ $version }
firmware_update-looking_for_devices = 正在寻找要更新的设备...
firmware_update-retry = 重试
firmware_update-update = 更新选定的追踪器
firmware_update-exit = 退出

## Tray Menu

tray_menu-show = 显示窗口
tray_menu-hide = 隐藏窗口
tray_menu-quit = 退出 SlimeVR

## First exit modal

tray_or_exit_modal-title = 选择关闭按钮的功能
# Multiline text
tray_or_exit_modal-description =
    你可以选择在按下关闭按钮时，是退出 SlimeVR 服务器，还是仅将窗口最小化至任务栏图标。
    你也可以在设置-交互界面中修改这个选项
tray_or_exit_modal-radio-exit = 退出 SlimeVR
tray_or_exit_modal-radio-tray = 最小化至任务栏
tray_or_exit_modal-submit = 保存
tray_or_exit_modal-cancel = 取消

## Unknown device modal

unknown_device-modal-title = 发现了一个新的追踪器！
unknown_device-modal-description =
    发现一个MAC地址为 <b>{ $deviceId }</b> 的新追踪器。
    要将它连接到 SlimeVR 吗？
unknown_device-modal-confirm = 是的！
unknown_device-modal-forget = 忽略它
# VRChat config warnings
vrc_config-page-title = VRChat设置警告
vrc_config-page-desc = 本页会显示当前VRChat设置的状态，并显示哪些设置与SlimeVR存在冲突。为获得最好的体验，建议修复此处显示的所有警告后再进行使用。
vrc_config-page-help = 找不到设置信息？
vrc_config-page-help-desc = 请查阅 <a>此处的说明文档</a>
vrc_config-page-big_menu = 追踪与IK（大菜单）
vrc_config-page-big_menu-desc = 大菜单中与IK相关的设置
vrc_config-page-wrist_menu = 追踪与IK（手上菜单）
vrc_config-page-wrist_menu-desc = 手上菜单（小菜单）中与IK相关的配置
vrc_config-on = 开
vrc_config-off = 关
vrc_config-invalid = 存在错误的VRChat设置！
vrc_config-show_more = 显示更多
vrc_config-setting_name = VRChat设置名称
vrc_config-recommended_value = 推荐值
vrc_config-current_value = 当前值
vrc_config-mute = 消除警告
vrc_config-mute-btn = 消除
vrc_config-unmute-btn = 取消消除
vrc_config-legacy_mode = 使用旧版IK解算
vrc_config-disable_shoulder_tracking = 禁用肩膀追踪
vrc_config-shoulder_width_compensation = 肩宽补偿
vrc_config-spine_mode = 全追脊柱模式
vrc_config-tracker_model = 全追追踪器模型
vrc_config-avatar_measurement_type = 虚拟形象测量
vrc_config-calibration_range = 校准范围
vrc_config-calibration_visuals = 显示全追校准范围
vrc_config-user_height = 用户真实身高
vrc_config-spine_mode-UNKNOWN = 未知
vrc_config-spine_mode-LOCK_BOTH = 同时锁定
vrc_config-spine_mode-LOCK_HEAD = 锁定头部
vrc_config-spine_mode-LOCK_HIP = 锁定臀部
vrc_config-tracker_model-UNKNOWN = 未知
vrc_config-tracker_model-AXIS = 十字轴
vrc_config-tracker_model-BOX = 方盒
vrc_config-tracker_model-SPHERE = 球体
vrc_config-tracker_model-SYSTEM = 系统模型
vrc_config-avatar_measurement_type-UNKNOWN = 未知
vrc_config-avatar_measurement_type-HEIGHT = 身高
vrc_config-avatar_measurement_type-ARM_SPAN = 臂展

## Error collection consent modal

error_collection_modal-title = 我们可以收集错误信息吗？
error_collection_modal-description_v2 =
    { settings-interface-behavior-error_tracking-description_v2 }
    
    您可以稍后在设置页面的行为部分中更改此设置。
error_collection_modal-confirm = 我同意
error_collection_modal-cancel = 还是算了

## Tracking checklist section

tracking_checklist = 追踪检查清单
tracking_checklist-settings = 追踪检查清单设置
tracking_checklist-settings-close = 关闭
tracking_checklist-status-incomplete = 使用 SlimeVR 前的准备工作尚未完成！
tracking_checklist-status-partial = 你有 { $count } 个警告！
tracking_checklist-status-complete = 已经准备好使用 SlimeVR！
tracking_checklist-MOUNTING_CALIBRATION = 进行佩戴校准
tracking_checklist-FEET_MOUNTING_CALIBRATION = 进行脚部佩戴校准
tracking_checklist-FULL_RESET = 进行完整重置
tracking_checklist-FULL_RESET-desc = 有些追踪器需要进行重置
tracking_checklist-STEAMVR_DISCONNECTED = SteamVR 未在运行
tracking_checklist-STEAMVR_DISCONNECTED-desc = SteamVR 未在运行。你要将追踪器用于 VR 吗？
tracking_checklist-STEAMVR_DISCONNECTED-open = 启动 SteamVR
tracking_checklist-TRACKERS_REST_CALIBRATION = 校准追踪器
tracking_checklist-TRACKERS_REST_CALIBRATION-desc = 您尚未执行追踪器校准。请将（黄色高亮显示的）追踪器放置在平稳表面上，并静置数秒。
tracking_checklist-TRACKER_ERROR = 追踪器出现错误
tracking_checklist-TRACKER_ERROR-desc = 有追踪器发生错误，请重启黄色高亮标记的追踪器。
tracking_checklist-VRCHAT_SETTINGS = 调整 VRChat 设置
tracking_checklist-VRCHAT_SETTINGS-desc = VRChat 的设置有问题！这会影响到在 VRChat 中使用 SlimeVR 的体验。
tracking_checklist-VRCHAT_SETTINGS-open = 前往 VRChat 警告页面
tracking_checklist-UNASSIGNED_HMD = VR 头戴显示器未分配给头部
tracking_checklist-UNASSIGNED_HMD-desc = VR 头戴显示器应该被分配为头部追踪器。
tracking_checklist-NETWORK_PROFILE_PUBLIC = 更改网络配置文件类型
tracking_checklist-NETWORK_PROFILE_PUBLIC-desc =
    检测到您的部分网卡被设为“公用网络”：
    { $adapters }
    这可能会影响 SlimeVR 的正常运行。
    <PublicFixLink>点击此处查看如何更改设置。</PublicFixLink>
tracking_checklist-NETWORK_PROFILE_PUBLIC-open = 打开控制面板
tracking_checklist-STAY_ALIGNED_CONFIGURED = 调整持续校准设置
tracking_checklist-STAY_ALIGNED_CONFIGURED-desc = 记录持续校准所使用的姿势以减缓漂移现象
tracking_checklist-STAY_ALIGNED_CONFIGURED-open = 打开持续校准设置
tracking_checklist-ignore = 忽略
preview-mocap_mode_soon = 动作捕捉模式（即将推出™）
preview-disable_render = 禁用预览
preview-disabled_render = 预览已禁用
toolbar-mounting_calibration = 佩戴校准
toolbar-mounting_calibration-default = 身体
toolbar-mounting_calibration-feet = 脚部
toolbar-mounting_calibration-fingers = 手指
toolbar-drift_reset = 漂移重置
toolbar-assigned_trackers = { $count } 个已分配的追踪器
toolbar-unassigned_trackers = { $count } 个未分配的追踪器
