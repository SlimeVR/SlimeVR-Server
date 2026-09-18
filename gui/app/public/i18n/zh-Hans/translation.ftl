# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = 加载中...
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
tips-failed_webgl = WebGL初始化失败

## Units

unit-meter = 米
unit-foot = 英尺
unit-inch = 英寸
unit-cm = 厘米

## Dropdown


## Text input


## File input


## Window controls

titlebar-close = 关闭

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
board_type-CUSTOM = 自定义开发板
board_type-SLIMEVR_DEV = SlimeVR 开发板
board_type-MOCOPI = 索尼 Mocopi
board_type-GLOVE_IMU_SLIMEVR_DEV = SlimeVR开发版IMU手套
board_type-GESTURES = 手势
board_type-GENERIC_NRF = nRF系列
board_type-SLIMEVR_BUTTERFLY_DEV = SlimeVR蝴蝶 开发版
board_type-SLIMEVR_BUTTERFLY = SlimeVR蝴蝶

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
skeleton_bone-LOWER_CHEST-desc =
    从胸部中部到脊椎中部的距离。
    调节时，先完成躯干长度的设置，再修改此参数，
    并进行包括坐下、弯腰、平躺等动作，使虚拟脊椎与真实的脊椎对齐。
skeleton_bone-HIP = 髋部长度
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

## Tracker reset buttons

reset-reset_all = 重置身体比例
reset-reset_all_warning-reset = 重置身体比例
reset-reset_all_warning-cancel = 取消
reset-full = 完整重置
reset-mounting = 检查追踪器佩戴
reset-mounting-feet = 重置脚部佩戴
reset-mounting-fingers = 重置手指佩戴
reset-yaw = 重置航向轴
reset-error-mounting-need_full_reset = 佩戴校准前需要先执行完整重置
reset-error-yaw-need_full_reset = 航向轴重置前需要先执行完整重置

## Navigation bar

navbar-home = 主界面
navbar-body_proportions = 身体比例
navbar-trackers_assign = 追踪器分配
navbar-mounting = 检查追踪器佩戴
navbar-onboarding = 向导
navbar-settings = 设置
navbar-connect_trackers = 连接追踪器

## Biovision hierarchy recording

bvh-start_recording = 录制 BVH 文件
bvh-stop_recording = 保存 BVH 记录
bvh-recording = 录制中...
bvh-save_title = 保存 BVH 记录

## Tracking pause

tracking-unpaused = 暂停追踪
tracking-paused = 解除暂停追踪

## Widget: Developer settings

widget-developer_mode = 开发者模式
widget-developer_mode-high_contrast = 高对比度
widget-developer_mode-precise_rotation = 显示精确旋转
widget-developer_mode-fast_data_feed = 快速数据更新
widget-developer_mode-raw_slime_rotation = 原始旋转

## Widget: IMU Visualizer

widget-imu_visualizer = 旋转
widget-imu_visualizer-preview = 预览
widget-imu_visualizer-hide = 隐藏窗口
widget-imu_visualizer-rotation_raw = 原始旋转
widget-imu_visualizer-rotation_preview = 预览
widget-imu_visualizer-acceleration = 加速度
widget-imu_visualizer-position = 位置
widget-imu_visualizer-stay_aligned = 持续校准

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
tracker-rotation-back = 返回
tracker-rotation-back_left = 左后
tracker-rotation-back_right = 右后
tracker-rotation-custom = 自定义

## Tracker information

tracker-infos-manufacturer = 制造商
tracker-infos-display_name = 显示名称
tracker-infos-custom_name = 自定义名称
tracker-infos-url = 追踪器地址
tracker-infos-hardware_identifier = 硬件ID
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
tracker-infos-packet_loss = 丢包
tracker-infos-packets_lost = 包丢失
tracker-infos-packets_received = 包已接收

## Tracker settings

tracker-settings-back = 返回追踪器列表
tracker-settings-title = 追踪器设置
tracker-settings-assignment_section = 分配追踪器
tracker-settings-assignment_section-description = 该追踪器要被分配到身体的哪个部位？
tracker-settings-assignment_section-edit = 编辑分配
tracker-settings-mounting_section = 佩戴位置
tracker-settings-mounting_section-description = 该追踪器被佩戴在哪里？
tracker-settings-mounting_section-edit = 编辑佩戴
tracker-settings-use_mag = 允许使用这个追踪器的磁力计
# Multiline!
tracker-settings-use_mag-description =
    如果「在追踪器上启用磁力计」已启用，是否要在这个追踪器上启用它来减轻飘移？<b>切换本选项时请勿关闭追踪器的电源！</b>
    
    请先启用「在追踪器上启用磁力计」功能，<magSetting>点选此处以移动至该设定</magSetting>。
tracker-settings-use_mag-label = 允许使用这个追踪器的磁力计
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = 追踪器名称
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
tracker-settings-build-date = 生成日期

## Dongle settings

dongle-infos-hardware_revision = 硬件版本
dongle-status-disconnected = 断开连接
dongle-settings-back = 返回追踪器列表
dongle-settings-update = 立即更新
dongle-settings-update-title = 固件版本

## Tracker part card info

tracker-part_card-unassigned = 未分配

## Body assignment menu

body_assignment_menu = 你想将此追踪器戴在哪里？
body_assignment_menu-description = 选择要将此追踪器分配到的位置，或者你也可以选择一次管理所有追踪器，而不是逐个管理。
body_assignment_menu-manage_trackers = 管理所有追踪器
body_assignment_menu-unassign_tracker = 取消分配追踪器

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>警告：</b> 颈部追踪器在调整得过紧时有致命风险
    绑带可能会阻断你头部的血液循环！
tracker_selection_menu-neck_warning-done = 我已知晓风险
tracker_selection_menu-neck_warning-cancel = 取消

## Mounting menu

mounting_selection_menu-close = 关闭

## Sidebar settings

settings-sidebar-title = 设置
settings-sidebar-general = 通用设置
settings-sidebar-stay_aligned = 持续校准
settings-sidebar-trackers = 追踪器
settings-sidebar-interface = 交互界面
settings-sidebar-utils = 工具
settings-sidebar-appearance = 外观
settings-sidebar-home = 主界面
settings-sidebar-checklist = 追踪检查清单
settings-sidebar-notifications = 通知
settings-sidebar-firmware-tool = DIY固件工具
settings-sidebar-vrc_warnings = VRChat设置警告
settings-sidebar-advanced = 高级选项

## Bone routing settings

settings-routing-output-badge-off = 关
settings-routing-group-fingers = 手指
settings-routing-hands-warning-cancel = 取消

## SteamVR / Monado output settings

settings-driver-enable = 启用
settings-driver-status-badge-disabled = 关

## Tracker mechanics

settings-general-tracker_mechanics-filtering = 滤波
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    选择追踪器的滤波类型。
    预测型将会对你的运动加以预测，而平滑型将会让你的运动更加平滑。
settings-general-tracker_mechanics-filtering-type-none = 无滤波
settings-general-tracker_mechanics-filtering-type-none-description = 使用原始数据，不进行滤波。
settings-general-tracker_mechanics-filtering-type-smoothing = 平滑型
settings-general-tracker_mechanics-filtering-type-smoothing-description = 让运动更加平滑，但会增加一些延迟。
settings-general-tracker_mechanics-filtering-type-prediction = 预测型
settings-general-tracker_mechanics-filtering-type-prediction-description = 减少延迟并使移动更敏捷，但可能会增加一些抖动。
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
settings-general-tracker_mechanics-trackers_over_usb = 通过USB连接的追踪器
settings-general-tracker_mechanics-trackers_over_usb-description = 通过USB接收HID追踪器数据。清确保连接的追踪器启用了 <b>通过HID连接</b> 功能！
settings-general-tracker_mechanics-trackers_over_usb-enabled-label = 允许HID追踪器通过USB直接连接
settings-stay_aligned-description = 持续校准会逐渐将追踪器对齐到设置的放松姿势，减少追踪器漂移的影响
settings-stay_aligned-setup-label = 配置持续校准
settings-stay_aligned-setup-description = 完成“配置持续校准”后才可启动持续校准。
settings-stay_aligned-enabled-label = 调整追踪器
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

## Keybinds Page

settings-keybinds_full-reset = 完整重置
settings-keybinds_yaw-reset = 重置航向轴
settings-keybinds_reset-all-button = 重置所有设置
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
settings-general-fk_settings-leg_tweak-skating_correction = 脚滑矫正
settings-general-fk_settings-leg_tweak-toe_snap = 脚趾着地
settings-general-fk_settings-leg_tweak-foot_plant = 脚掌着地
settings-general-fk_settings-leg_tweak-skating_correction-amount = 脚滑矫正量
settings-general-fk_settings-leg_tweak-skating_correction-description = 脚滑矫正可以矫正一些脚滑溜冰的问题, 但是可能会降低某些动作的准确性。启用前请先进行完整重置，启用后在游戏内重新校准。
settings-general-fk_settings-leg_tweak-floor_clip-description = 地板限制可以减轻甚至消除脚部穿入地板的情况。启用前请先进行完整重置，启用后在游戏内重新校准。
settings-general-fk_settings-leg_tweak-toe_snap-description = 脚趾着地可以在没有脚部追踪器的情况下尝试猜测脚部的俯仰。
settings-general-fk_settings-leg_tweak-foot_plant-description = 脚掌着地会在脚与地面接触时保持脚掌与地板平行。
settings-general-fk_settings-leg_fk = 腿部追踪
settings-general-fk_settings-leg_fk-reset_mounting_feet-v1 = 强制脚部佩戴重置
settings-general-fk_settings-leg_fk-reset_mounting_feet-description-v1 = 在进行普通佩戴重置时强制进行脚部佩戴重置。
settings-general-fk_settings-enforce_joint_constraints = 骨骼限制
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints = 强制约束
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = 避免关节旋转超过人体骨骼角度限制
settings-general-fk_settings-ik = 位置数据
settings-general-fk_settings-ik-use_position = 使用位置数据
settings-general-fk_settings-ik-use_position-description = 若追踪器支持，使用来自追踪器的位置数据。启用后，请再次进行完全重置并在游戏中重新校准追踪器。
settings-general-fk_settings-arm_fk-back = 返回
settings-general-fk_settings-arm_fk-back-description = 默认，重置时大臂向后，小臂向前，类似滑雪。
settings-general-fk_settings-arm_fk-tpose_up = T-pose（抬起）
settings-general-fk_settings-arm_fk-tpose_up-description = 完整重置时手臂垂下，呈立正姿势；佩戴重置时手臂向两侧伸平。
settings-general-fk_settings-arm_fk-tpose_down = T-pose（放下）
settings-general-fk_settings-arm_fk-tpose_down-description = 完整重置时手臂向两侧伸平；佩戴重置时手臂垂下，呈立正姿势。
settings-general-fk_settings-arm_fk-forward = 向前伸平
settings-general-fk_settings-arm_fk-forward-description = 重置时手臂向前伸平，有利于坐姿进行虚拟直播。
settings-general-fk_settings-skeleton_settings-ratios = 骨架比例
settings-general-fk_settings-skeleton_settings-ratios-description = 更改骨架设置的参数。您可能需要在更改后调整身体比例。
settings-general-fk_settings-self_localization-title = 动作捕捉模式

## Gesture control settings (tracker tapping)

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

## Notification settings

settings-interface-notifications = 通知
settings-general-interface-feedback_sound = 声音反馈
settings-general-interface-feedback_sound-description = 开启此选项会在触发重置时发出提示音
settings-general-interface-feedback_sound-label = 声音反馈
settings-general-interface-feedback_sound-volume = 提示音音量
settings-general-interface-connected_trackers_warning = 已连接追踪器警告
settings-general-interface-connected_trackers_warning-description = 启用本选项后，每次当退出 SlimeVR 时仍有追踪器连接着会显示通知，提醒你在使用完毕时关闭追踪器电源来节省电池电量。
settings-general-interface-connected_trackers_warning-label = 退出时，有追踪器连接中则显示警告

## Behavior settings

settings-general-interface-dev_mode = 开发者模式
settings-general-interface-dev_mode-label = 开发者模式
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
settings-interface-behavior-bvh_directory = BVH 记录保存目录
settings-interface-behavior-bvh_directory-description = 选择保存 BVH 记录文件的目录
settings-interface-behavior-bvh_directory-label = BVH 记录保存目录

## Serial settings

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
settings-serial-save_logs = 保存到文件
settings-serial-send_command = 发送
settings-serial-send_command-placeholder = 输入指令...
settings-serial-send_command-warning = <b>警告:</b>运行串口命令可能导致数据丢失或使追踪器无法正常工作。
settings-serial-send_command-warning-ok = 我已知晓
settings-serial-send_command-warning-cancel = 取消

## OSC VRChat settings

settings-osc-vrchat-enable = 启用
settings-osc-vrchat-enable-description = 切换数据的发送和接收
settings-osc-vrchat-enable-label = 启用
settings-osc-vrchat-network = 网络端口
settings-osc-vrchat-network-port_in =
    .label = 输入端口
    .placeholder = 输入端口（默认 9001）
settings-osc-vrchat-network-port_out =
    .label = 输出端口
    .placeholder = 输出端口（默认 9000）
settings-osc-vrchat-network-address = 网络地址
settings-osc-vrchat-network-address-description-v1 = 选择传输OSC数据的IP地址。用于VRChat时无需修改。
settings-osc-vrchat-network-address-placeholder = VRChat IP 地址

## VRChat OSC status

settings-osc-vrchat-status-tracking = 旋转
settings-osc-vrchat-status-badge-error = 错误
settings-osc-vrchat-status-badge-unknown = 未知

## VMC OSC settings

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
settings-osc-vmc-vrm-untitled_model = 无标题模型
settings-osc-vmc-vrm-file_select = 拖曳文件或 <u>浏览文件</u> 以加载模型
settings-osc-vmc-anchor_hip = 髋部锚定
settings-osc-vmc-anchor_hip-label = 髋部锚定
settings-osc-vmc-mirror_tracking = 镜像追踪
settings-osc-vmc-mirror_tracking-description = 水平镜像追踪结果
settings-osc-vmc-mirror_tracking-label = 镜像追踪
settings-osc-vmc-status-badge-error = 错误

## Common OSC settings

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

