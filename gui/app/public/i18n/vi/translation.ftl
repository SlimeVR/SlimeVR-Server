# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Đang kết nối với máy chủ
websocket-connection_lost = Kết nối với máy chủ đã mất. Đang kết nối lại...
websocket-connection_lost-desc = Máy chủ SlimeVR bị dừng. Hãy kiểm tra nhật ký logs và khởi động lại chương trình.
websocket-timedout = Không thể tìm thấy máy chủ
websocket-timedout-desc = Có vẻ như máy chủ SlimeVR đã bị sập hoặc hết thời gian chờ. Vui lòng kiểm tra nhật ký logs và khởi động lại chương trình.
websocket-error-close = Thoát SlimeVR
websocket-error-logs = Mở thư mục nhật ký logs

## Update notification

version_update-title = Phiên bản mới có thể cập nhật:
version_update-description = Nhấp vào "{ version_update-update }" sẽ tải xuống trình cài đặt SlimeVR cho bạn.
version_update-update = Cập nhật
version_update-close = Đóng

## Tips

tips-find_tracker = Không rõ tracker nào đang được chọn? Di chuyển nó và trong menu sẽ sáng lên tracker đó
tips-do_not_move_heels = Không di chuyển gót chân trong khi đo
tips-file_select = Kéo và thả tệp để sử dụng hoặc <u>duyệt</u>.
tips-failed_webgl = Không thể khởi tạo WebGL.

## Units


## Body parts

body_part-NONE = Chưa được gán
body_part-HEAD = Đầu
body_part-NECK = Cổ
body_part-RIGHT_SHOULDER = Vai phải
body_part-RIGHT_UPPER_ARM = Bắp tay phải
body_part-RIGHT_LOWER_ARM = Cẳng tay phải
body_part-RIGHT_HAND = Tay phải
body_part-RIGHT_UPPER_LEG = Bắp chân phải
body_part-RIGHT_LOWER_LEG = Cẳng chân phải
body_part-RIGHT_FOOT = Bàn chân phải
body_part-UPPER_CHEST = Ngực trên
body_part-CHEST = Ngực
body_part-WAIST = Eo
body_part-HIP = Hông
body_part-LEFT_SHOULDER = Vai trái
body_part-LEFT_UPPER_ARM = Bắp tay trái
body_part-LEFT_LOWER_ARM = Cẳng tay trái
body_part-LEFT_HAND = Tay trái
body_part-LEFT_UPPER_LEG = Bắp chân trái
body_part-LEFT_LOWER_LEG = Cẳng chân trái
body_part-LEFT_FOOT = Bàn chân trái

## BoardType

board_type-MOCOPI = Sony Mocopi
board_type-WEMOSWROOM02 = Wemos Wroom-02 D1 Mini

## Proportions

skeleton_bone-NONE = Chưa được gán
skeleton_bone-HEAD = Sai số đầu
skeleton_bone-NECK = Chiều dài cổ
skeleton_bone-torso_group = Độ dài thân
skeleton_bone-UPPER_CHEST = Độ dài ngực trên
skeleton_bone-CHEST = Khoảng cách ngực
skeleton_bone-WAIST = Khoảng cách eo
skeleton_bone-HIP = Khoảng cách hông
skeleton_bone-HIPS_WIDTH = Chiều rộng hông
skeleton_bone-leg_group = Chiều dài chân
skeleton_bone-UPPER_LEG = Chiều dài bắp chân
skeleton_bone-LOWER_LEG = Chiều dài cẳng chân
skeleton_bone-FOOT_LENGTH = Chiều dài bàn chân
skeleton_bone-FOOT_SHIFT = Sai số bàn chân
skeleton_bone-SHOULDERS_DISTANCE = Khoảng cách vai
skeleton_bone-SHOULDERS_WIDTH = Chiều rộng vai
skeleton_bone-arm_group = Chiều dài cánh tay
skeleton_bone-UPPER_ARM = Chiều dài bắp tay
skeleton_bone-LOWER_ARM = Chiều dài cẳng tay
skeleton_bone-HAND_Y = Khoảng cách tay Y
skeleton_bone-HAND_Z = Khoảng cách tay Z

## Tracker reset buttons

reset-reset_all = Đặt lại tất cả bộ phận
reset-reset_all_warning-reset = Đặt lại tỷ lệ
reset-reset_all_warning-cancel = Hủy
reset-full = Đặt lại
reset-mounting = Đặt lại hướng gắn tracker
reset-yaw = Đặt lại chiều quay lệch

## Serial detection stuff

serial_detection-new_device-p0 = Tìm thấy thiết bị mới!
serial_detection-new_device-p1 = Nhập thông tin Wi-Fi
serial_detection-new_device-p2 = Chọn hành động cần thực hiện
serial_detection-open_wifi = Kết nối đến Wi-Fi
serial_detection-open_serial = Mở cổng Serial
serial_detection-submit = Gửi
serial_detection-close = Đóng

## Navigation bar

navbar-home = Trang chủ
navbar-body_proportions = Tỉ lệ cơ thể
navbar-trackers_assign = Phân bố tracker
navbar-mounting = Cân chỉnh hướng gắn tracker
navbar-onboarding = Trình thiết lập
navbar-settings = Cài đặt

## Biovision hierarchy recording

bvh-start_recording = Ghi BVH
bvh-recording = Đang ghi...

## Tracking pause

tracking-unpaused = Tạm dừng tracking
tracking-paused = Bỏ dừng theo dõi

## Widget: Developer settings

widget-developer_mode = Chế độ nhà phát triển
widget-developer_mode-high_contrast = Chế độ tương phản cao
widget-developer_mode-precise_rotation = Hiển thị góc quay chính xác
widget-developer_mode-fast_data_feed = Tăng tốc độ gửi dữ liệu
widget-developer_mode-sort_by_name = Sắp xếp theo tên
widget-developer_mode-raw_slime_rotation = Sử dụng giá trị góc quay thực cho tracker
widget-developer_mode-more_info = Thêm thông tin

## Widget: IMU Visualizer

widget-imu_visualizer = Góc quay
widget-imu_visualizer-preview = Xem trước
widget-imu_visualizer-hide = Ẩn
widget-imu_visualizer-rotation_raw = Gốc
widget-imu_visualizer-rotation_preview = Qua xử lí
widget-imu_visualizer-acceleration = Gia tốc
widget-imu_visualizer-position = Vị trí

## Tracker status

tracker-status-none = Không có trạng thái
tracker-status-busy = Bận
tracker-status-error = Lỗi
tracker-status-disconnected = Đã ngắt kết nối
tracker-status-occluded = Nghẽn
tracker-status-ok = Đã kết nối
tracker-status-timed_out = Hết thời gian chờ

## Tracker status columns

tracker-table-column-name = Tên
tracker-table-column-type = Loại
tracker-table-column-battery = Pin
tracker-table-column-ping = Ping
tracker-table-column-tps = TPS
tracker-table-column-temperature = Nhiệt độ (°C)
tracker-table-column-linear-acceleration = Tốc độ X/Y/Z
tracker-table-column-rotation = Góc quay X/Y/Z
tracker-table-column-position = Tọa độ X/Y/Z
tracker-table-column-url = Đường dẫn

## Tracker rotation

tracker-rotation-front = Trước
tracker-rotation-front_left = Phía trước-Bên trái
tracker-rotation-front_right = Phía trước-Bên phải
tracker-rotation-left = Trái
tracker-rotation-right = Phải
tracker-rotation-back = Sau
tracker-rotation-back_left = Phía sau-Bên trái
tracker-rotation-back_right = Phía sau-Bên phải
tracker-rotation-custom = Tùy chọn

## Tracker information

tracker-infos-manufacturer = Nhà sản xuất
tracker-infos-display_name = Tên hiển thị
tracker-infos-custom_name = Tên gọi
tracker-infos-url = Đường dẫn
tracker-infos-hardware_identifier = Hardware ID
tracker-infos-imu = Cảm biến IMU (IMU Sensor)
tracker-infos-board_type = Bảng mạch chính
tracker-infos-network_version = Phiên bản giao thức

## Tracker settings

tracker-settings-back = Quay lại danh sách tracker
tracker-settings-title = Cài đặt
tracker-settings-assignment_section = Vị trí
tracker-settings-assignment_section-description = Vị trí của tracker trên cơ thể
tracker-settings-assignment_section-edit = Thay đổi vị trí
tracker-settings-mounting_section = Vị trí đặt
tracker-settings-mounting_section-description = Tracker được đặt ở đâu?
tracker-settings-mounting_section-edit = Thay đổi chỗ đặt
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Tên tracker
tracker-settings-name_section-description = Đặt cho nó một cái tên đẹp :3
tracker-settings-name_section-placeholder = Chân trái của JINODK
tracker-settings-forget = Quên thiết bị
tracker-settings-forget-description = Xóa thiết bị khỏi phần mềm SlimeVR và ngăn nó kết nối với nó cho đến khi máy chủ được khởi động lại. Cấu hình của trình theo dõi sẽ không bị mất.
tracker-settings-forget-label = Quên thiết bị

## Tracker part card info

tracker-part_card-unassigned = Chưa gán vị trí

## Body assignment menu

body_assignment_menu = Bạn muốn gán tracker này cho bộ phận nào?
body_assignment_menu-description = Chọn vị trí bạn muốn gán tracker, ngoài ra bạn cũng có thể quản lí vị trí tất cả các tracker cùng một lúc
body_assignment_menu-manage_trackers = Quản lí tất cả tracker
body_assignment_menu-unassign_tracker = Bỏ gán tracker

## Tracker assignment menu

# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Cảnh báo:</b> Tracker ở cổ rất nguy hiểm và có thể gây tử vong nếu điều chỉnh quá chặt,
    Dây đeo có thể cắt lưu thông máu đến đầu của bạn!
tracker_selection_menu-neck_warning-done = Tôi hiểu những rủi ro
tracker_selection_menu-neck_warning-cancel = Hủy

## Mounting menu

mounting_selection_menu = Bạn muốn gắn tracker này cho bộ phận nào?
mounting_selection_menu-close = Đóng

## Sidebar settings

settings-sidebar-title = Cài đặt
settings-sidebar-general = Cài đặt chung
settings-sidebar-interface = Giao diện
settings-sidebar-osc_trackers = Trình theo dõi VRChat OSC
settings-sidebar-utils = Công cụ
settings-sidebar-serial = Cổng Serial
settings-sidebar-appearance = Giao diện
settings-sidebar-notifications = Thông báo
settings-sidebar-advanced = Cài đặt mở rộng

## Tracker mechanics

settings-general-tracker_mechanics-filtering = Lọc nhiễu
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Chọn kiểu lọc nhiễu cho tracker
    "Dự đoán" sẽ dự đoán chuyển động trong khi "Khử nhiễu" sẽ làm chuyển động mềm mại hơn
settings-general-tracker_mechanics-filtering-type = Kiểu lọc
settings-general-tracker_mechanics-filtering-type-none = Không lọc
settings-general-tracker_mechanics-filtering-type-none-description = Sử dụng giá trị thực, không áp dụng bất kì bộ lọc nào
settings-general-tracker_mechanics-filtering-type-smoothing = Khử nhiễu
settings-general-tracker_mechanics-filtering-type-smoothing-description = Làm chuyển động mềm mại hơn nhưng có thể tăng độ trễ
settings-general-tracker_mechanics-filtering-type-prediction = Dự đoán
settings-general-tracker_mechanics-filtering-type-prediction-description = Giảm độ trễ và làm chuyển động chân thật hơn, có thể khiến chuyển động không mượt mà
settings-general-tracker_mechanics-filtering-amount = Mức độ lọc
settings-general-tracker_mechanics-yaw-reset-smooth-time = Thời gian thiết lập lại chiều quay lệch (0s tắt làm mượt)
settings-general-tracker_mechanics-save_mounting_reset = Lưu thiết bị đã giao tự động
settings-general-tracker_mechanics-save_mounting_reset-description =
    Lưu thiết bị đã giao tự động cho các thiết bị giữa các lần khởi động lại. Có ích
    khi mặc một bộ đồ SlimeVR mà trình theo dõi không di chuyển giữa các phiên. <b>Không được khuyến khích cho người dùng bình thường!</b>
settings-general-tracker_mechanics-save_mounting_reset-enabled-label = Đặt lại hướng gắn thiết bị

## FK/Tracking settings

settings-general-fk_settings = Cài đặt nâng cao
# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Ngăn xuyên sàn
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Sửa trượt
settings-general-fk_settings-leg_tweak-toe_snap = Đoán hướng xoay chân
settings-general-fk_settings-leg_tweak-foot_plant = Cân bằng chân
settings-general-fk_settings-leg_tweak-skating_correction-amount = Mức độ sửa
settings-general-fk_settings-leg_tweak-skating_correction-description = Sửa trượt sẽ điều chỉnh nhằm giảm thiểu hiện tượng trượt băng nhưng có thể làm giảm độ chính xác trong một vài kiểu chuyển động nhất định. Khi bật tính năng này, vui lòng đảm bảo thực hiện thao tác đặt lại hoàn toàn và hiệu chỉnh lại trong trò chơi.
settings-general-fk_settings-leg_tweak-floor_clip-description = Ngăn xuyên sàn có thể giảm hoặc loại bỏ khả năng tracker của bạn đi xuyên sàn nhà. Khi bật tính năng này, vui lòng đảm bảo thực hiện thao tác đặt lại hoàn toàn và hiệu chỉnh lại trong trò chơi.
settings-general-fk_settings-leg_tweak-toe_snap-description = Đoán hướng xoay chân sẽ đoán hướng xoay của chân đồng thời khóa ngón chân của bạn vào mặt sàn bạn nếu bạn không sử dụng tracker cho chân.
settings-general-fk_settings-leg_tweak-foot_plant-description = Cân bằng chân sẽ xoay chân song song với mặt đất khi lại gần.
settings-general-fk_settings-leg_fk = Track chân
settings-general-fk_settings-arm_fk-reset_mode-description = Thay đổi tư thế cánh tay để đặt lại hướng gắn tracker.
settings-general-fk_settings-arm_fk-back = Khuỷu tay ra sau
settings-general-fk_settings-arm_fk-back-description = Chế độ mặc định, với cánh tay trên trỏ về phía sau và cánh tay dưới hướng về phía trước.
settings-general-fk_settings-arm_fk-tpose_up = T-pose
settings-general-fk_settings-arm_fk-tpose_up-description = Hai tay của bạn sẽ hướng xuống ở hai bên khi đặt lại hoàn toàn, và đưa lên 90 độ sang hai bên khi đặt lại hướng gắn tracker.
settings-general-fk_settings-arm_fk-tpose_down = T-pose (ngược lại)
settings-general-fk_settings-arm_fk-tpose_down-description = Hai tay của bạn sẽ đưa lên 90 độ sang hai bên khi đặt lại hoàn toàn, và hai tay hướng xuống hai bên khi đặt lại hướng gắn tracker.
settings-general-fk_settings-arm_fk-forward = Hai tay ra trước
settings-general-fk_settings-arm_fk-forward-description = Hai cánh tay của bạn nâng lên 90 độ về phía trước. Hữu dụng cho việc VTubing.
settings-general-fk_settings-skeleton_settings-ratios = Tỷ lệ khung xương
settings-general-fk_settings-skeleton_settings-ratios-description = Thay đổi các giá trị của cài đặt bộ xương. Bạn có thể cần phải điều chỉnh tỷ lệ của bạn sau khi thay đổi những điều này.
settings-general-fk_settings-self_localization-title = Chế độ Mocap
settings-general-fk_settings-self_localization-description = Chế độ Mocap cho phép bộ xương theo dõi đại khái vị trí của chính nó mà không cần kính VR hoặc các thiết bị theo dõi khác. Lưu ý rằng điều này yêu cầu bộ theo dõi chân và đầu để hoạt động và chức năng này vẫn đang trong quá trình thử nghiệm.

## Gesture control settings (tracker tapping)

settings-general-gesture_control-subtitle = Chạm để đặt lại
settings-general-gesture_control-description = Cho phép chạm vào tracker để đặt lại vị trí, chạm hai lần vào tracker cao nhất ở thân để đặt lại nhanh, chạm hai lần vào tracker cao nhất ở chân trái để đặt lại, chạm hai lần vào tracker cao nhất ở chân phải để đặt lại vị trí gắn tracker. Cử chỉ chỉ được tiếp nhận khi thời gian giữa hai lần chạm ngắn hơn 0.6 giây.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
        [one] 1 tap
       *[other] { $amount } lần
    }
# This is a unit: 3 trackers, 2 trackers, 1 tracker
# $amount (Number) - Amount of trackers
settings-general-gesture_control-trackers = { $amount } trackers
settings-general-gesture_control-yawResetEnabled = Bật chạm để đặt lại chiều quay
settings-general-gesture_control-yawResetDelay = Thời gian delay trước khi đặt lại
settings-general-gesture_control-yawResetTaps = Số lần chạm để đặt lại chiều quay
settings-general-gesture_control-fullResetEnabled = Bật chạm để đặt lại chiều quay
settings-general-gesture_control-fullResetDelay = Thời gian delay trước khi đặt lại full
settings-general-gesture_control-fullResetTaps = Số lần chạm để đặt lại full
settings-general-gesture_control-mountingResetEnabled = Chạm để đặt lại hướng gắn tracker
settings-general-gesture_control-mountingResetDelay = Thời gian delay trước khi đặt lại hướng gắn tracker
settings-general-gesture_control-mountingResetTaps = Số lần chạm cho đặt lại hướng gắn tracker
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = Số thiết bị có thể vượt phát hiện
settings-general-gesture_control-numberTrackersOverThreshold-description = Tăng số này nếu tính năng chạm thiết bị không hoạt động. Không tăng nó quá trên mức cần thiết để làm cho phát hiện chạm hoạt động vì nó sẽ gây ra nhiều kết quả sai hơn.

## Appearance settings

settings-interface-appearance = Giao diện
settings-general-interface-dev_mode = Chế độ nhà phát triển
settings-general-interface-dev_mode-description = Hữu dụng nếu cần thêm thông tin chi tiết của tracker hay can thiệp sâu hơn vào tracker
settings-general-interface-dev_mode-label = Chế độ nhà phát triển
settings-general-interface-theme = Màu giao diện
settings-general-interface-lang = Thay đổi ngôn ngữ (Change language)
settings-general-interface-lang-description = Đổi ngôn ngữ hiển thị (Change the default language you want to use)
settings-general-interface-lang-placeholder = Chọn ngôn ngữ để sử dụng (Select the language to use)
# Keep the font name untranslated
settings-interface-appearance-font = Phông chữ cho GUI
settings-interface-appearance-font-description = Cài đặt này thay đổi phông chữ được sử dụng bởi giao diện.
settings-interface-appearance-font-placeholder = Phông chữ mặc định
settings-interface-appearance-font-os_font = Phông chữ hệ điều hành
settings-interface-appearance-font-slime_font = Phông chữ mặc định
settings-interface-appearance-font_size = Tỷ lệ phông chữ cơ bản
settings-interface-appearance-font_size-description = Điều này ảnh hưởng đến kích thước phông chữ của toàn bộ giao diện ngoại trừ bảng cài đặt này.

## Notification settings

settings-interface-notifications = Thông báo
settings-general-interface-serial_detection = Nhận dạng thiết bị Serial mới
settings-general-interface-serial_detection-description = Hiển thị pop-up mỗi lần một thiết bị Serial mới được kết nối qua USB (có thể là tracker), giúp cải thiện quá trình thiết lập tracker
settings-general-interface-serial_detection-label = Nhận dạng thiết bị Serial mới
settings-general-interface-feedback_sound = Âm thanh phản hồi
settings-general-interface-feedback_sound-description = Tùy chọn này sẽ phát âm thanh khi thiết lập lại được kích hoạt.
settings-general-interface-feedback_sound-label = Âm thanh phản hồi
settings-general-interface-feedback_sound-volume = Âm lượng phản hồi
settings-general-interface-connected_trackers_warning = Cảnh báo với thiết bị đã kết nối
settings-general-interface-connected_trackers_warning-description = Tùy chọn này sẽ hiển thị cửa sổ bật lên mỗi khi bạn thử thoát khỏi SlimeVR trong khi có một hoặc nhiều thiết bị theo dõi được kết nối. Nó nhắc nhở bạn tắt trình theo dõi khi bạn hoàn tất để duy trì tuổi thọ pin.
settings-general-interface-connected_trackers_warning-label = Cảnh báo thiết bị đã kết nối khi thoát chương trình

## Behavior settings

settings-general-interface-use_tray = Thu nhỏ vào khay hệ thống
settings-general-interface-use_tray-description = Cho phép bạn đóng cửa sổ mà không cần đóng máy chủ SlimeVR để bạn có thể tiếp tục sử dụng nó mà không bị GUI làm phiền.
settings-general-interface-use_tray-label = Thu nhỏ vào khay hệ thống
settings-general-interface-discord_presence = Chia sẻ hoạt động trên Discord
settings-general-interface-discord_presence-description = Cho Discord của bạn biết rằng bạn đang sử dụng SlimeVR cùng với số lượng trình theo dõi IMU bạn đang sử dụng.
settings-general-interface-discord_presence-label = Chia sẻ hoạt động trên Discord
settings-general-interface-discord_presence-message =
    { $amount ->
        [0] Đang quay trên Slime
       *[other] Đang dùng { $amount } điểm full body
    }

## Serial settings

settings-serial = Cổng Serial
# This cares about multilines
settings-serial-description =
    Đây là cổng giao tiếp Serial trực tiếp với tracker
    Hữu dụng nếu cần kiểm tra tracker có hoạt động như mong muốn hay không
settings-serial-connection_lost = Kết nối đến Serial đã mất, đang kết nối lại...
settings-serial-reboot = Khởi động lại
settings-serial-factory_reset = Khôi phục cài đặt gốc
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>Lưu ý:</b> Thao tác này sẽ đặt lại trình theo dõi về cài đặt gốc.
    Đặt lại bao gồm tất cả các cài đặt Wi-Fi và hiệu chuẩn (Calibrate) <b>sẽ bị mất!</b>
settings-serial-factory_reset-warning-ok = Tôi biết mình đang làm gì
settings-serial-factory_reset-warning-cancel = Hủy
settings-serial-serial_select = Chọn cổng Serial
settings-serial-auto_dropdown_item = Tự động
settings-serial-get_wifi_scan = Quét WiFi
settings-serial-save_logs = Lưu vào tệp

## OSC VRChat settings

settings-osc-vrchat = VRChat OSC Trackers
# This cares about multilines
settings-osc-vrchat-description-v1 =
    Thay đổi cài đặt cụ thể cho OSC Trackers được sử dụng để gửi
    dữ liệu đo đến các ứng dụng không có SteamVR (ví dụ: Quest Standalone).
    Hãy đảm bảo bật OSC trong VRChat thông qua Menu hành động trong OSC > Enabled.
    Để cho phép nhận kính thực tế ảo HMD và dữ liệu bộ điều khiển từ VRChat, hãy vào menu chính của bạn
    cài đặt trong Tracking & IK > Allow Sending Head and Wrist VR Tracking OSC Data.
settings-osc-vrchat-enable = Dữ liệu OSC
settings-osc-vrchat-enable-description = Cho phép nhận và gửi dữ liệu OSC
settings-osc-vrchat-enable-label = Giao tiếp dữ liệu OSC
settings-osc-vrchat-network = Cổng mạng
settings-osc-vrchat-network-port_in =
    .label = Cổng vào
    .placeholder = Cổng vào (Mặc định: 9001)
settings-osc-vrchat-network-port_out =
    .label = Cổng ra
    .placeholder = Cổng ra (Mặc định: 9000)
settings-osc-vrchat-network-address = Địa chỉ mạng
settings-osc-vrchat-network-address-description-v1 = Chọn địa chỉ để gửi dữ liệu đến. Có thể giữ nguyên cho VRChat.
settings-osc-vrchat-network-address-placeholder = Địa chỉ IP của thiết bị chơi VRChat

## VMC OSC settings

settings-osc-vmc = Virtual Motion Capture
# This cares about multilines
settings-osc-vmc-description =
    Thay đổi cài đặt cụ thể cho giao thức VMC (Virtual Motion Capture)
        để gửi dữ liệu thân của SlimeVR và nhận dữ liệu thân từ các ứng dụng khác.
settings-osc-vmc-enable = Bật
settings-osc-vmc-enable-description = Cho phép nhận và gửi dữ liệu OSC
settings-osc-vmc-enable-label = Chuyển tiếp dữ liệu OSC
settings-osc-vmc-network = Cổng mạng (Network ports)
settings-osc-vmc-network-description = Đặt các cổng để nghe và gửi dữ liệu qua VMC.
settings-osc-vmc-network-port_in =
    .label = Cổng vào (Port In)
    .placeholder = Port in (default: 39540)
settings-osc-vmc-network-port_out =
    .label = Cổng ra (Port out)
    .placeholder = Port out (default: 39539)
settings-osc-vmc-network-address = Địa chỉ mạng (Network address)
settings-osc-vmc-network-address-description = Chọn địa chỉ để gửi dữ liệu qua VMC.
settings-osc-vmc-network-address-placeholder = Địa chỉ IPV4
settings-osc-vmc-vrm = Model VRM
settings-osc-vmc-vrm-description = Tải mô hình VRM để cho phép neo đầu và cho phép khả năng tương thích cao hơn với các ứng dụng khác.
settings-osc-vmc-vrm-file_select = Kéo và thả mô hình để sử dụng hoặc <u>duyệt file</u>
settings-osc-vmc-anchor_hip = Cố định ở hông
settings-osc-vmc-anchor_hip-description = Cố định theo dõi ở hông, hữu ích cho VTubing ngồi. Nếu tắt, hãy tải mô hình VRM.
settings-osc-vmc-anchor_hip-label = Cố định ở hông
settings-osc-vmc-mirror_tracking = Phản chiếu ngược theo dõi cơ thể
settings-osc-vmc-mirror_tracking-description = Phản chiếu theo dõi theo chiều ngang.
settings-osc-vmc-mirror_tracking-label = Phản chiếu ngược theo dõi cơ thể

## Common OSC settings


## Advanced settings

settings-utils-advanced = Cài đặt mở rộng
settings-utils-advanced-reset-gui = Đặt lại cài đặt GUI
settings-utils-advanced-reset-all-label = Đặt lại tất cả
settings-utils-advanced-reset_warning-reset = Đặt lại cài đặt
settings-utils-advanced-reset_warning-cancel = Hủy
settings-utils-advanced-open_data-label = Mở thư mục

## Home Screen


## Tracking Checlist


## Setup/onboarding menu

onboarding-skip = Bỏ qua cài đặt
onboarding-continue = Tiếp tục
onboarding-previous_step = Quay lại
onboarding-setup_warning =
    <b>Lưu ý:</b> Trình thiết lập ban đầu là cần thiết để theo dõi tốt,
    bước này cần thiết nếu đây là lần đầu tiên bạn sử dụng SlimeVR.
onboarding-setup_warning-skip = Bỏ qua cài đặt
onboarding-setup_warning-cancel = Tiếp tục thiết lập

## Quiz


## Wi-Fi setup

onboarding-wifi_creds-submit = Gửi
onboarding-wifi_creds-ssid =
    .label = Tên hiển thị
    .placeholder = Nhập tên Wi-Fi
onboarding-wifi_creds-password =
    .label = Mật khẩu
    .placeholder = và mật khẩu

## Install info


## Setup start

onboarding-home = Chào mừng bạn đến với SlimeVR!
onboarding-home-start = Bắt đầu thiết lập!

## Tracker connection setup

onboarding-connect_tracker-title = Kết nối tracker
onboarding-connect_tracker-issue-serial = Có vấn đề với việc kết nối? Kiểm tra thông tin qua cổng Serial
onboarding-connect_tracker-usb = USB Tracker
onboarding-connect_tracker-connection_status-serial_init = Kết nối với thiết bị nối tiếp serial
onboarding-connect_tracker-connection_status-provisioning = Đang gửi thông tin Wi-Fi
onboarding-connect_tracker-connection_status-connecting = Đang gửi thông tin Wi-Fi
onboarding-connect_tracker-connection_status-looking_for_server = Đang tìm máy chủ
onboarding-connect_tracker-connection_status-connection_error = Không thể kết nối đến Wi-Fi
onboarding-connect_tracker-connection_status-could_not_find_server = Không thể tìm thấy máy chủ
onboarding-connect_tracker-connection_status-done = Đã kết nối đến máy chủ SlimeVR
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers =
    { $amount ->
        [0] Không có tracker đã giao
        [one] 1 tracker đã giao
       *[other] { $amount } tracker đã giao
    }
onboarding-connect_tracker-next = Đã kết nối với tất cả tracker

## Tracker assignment setup

onboarding-assign_trackers-title = Gán tracker
onboarding-assign_trackers-description = Chọn vị trí bạn muốn gán tracker bằng cách nhấn vào tên bộ phận muốn gán và chọn tracker
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned =
    { $trackers ->
        [one] { $assigned } trên 1 tracker đã giao
       *[other] { $assigned } trên { $trackers } tracker đã giao
    }

## Tracker assignment warnings

# Note for devs, number is used for representing boolean states per bit.
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_FOOT =
    { $unassigned ->
        [6] Bàn chân trái được xác định nhưng bạn cần thêm chân dưới kèm theo để hoạt động!
        [5] Bàn chân trái được xác định nhưng bạn cần thêm chân trên kèm theo để hoạt động!
        [4] Bàn chân trái được xác định nhưng bạn cần thêm chân trên và chân dưới kèm theo để hoạt động!
        [3] Bàn chân trái được xác định nhưng bạn cần thêm ngực, eo hoặc bụng kèm theo để hoạt động!
        [2] Bàn chân trái được xác định nhưng bạn cần thêm chân trên, ngực, eo hoặc bụng kèm theo để hoạt động!
        [1] Bàn chân trái được xác định nhưng bạn cần thêm chân dưới, ngực, eo hoặc bụng kèm theo để hoạt động!
        [0] Bàn chân trái được xác định nhưng bạn cần thêm chân trên, chân dưới, ngực, eo hoặc bụng kèm theo để hoạt động!
       *[unknown] Bàn chân trái được xác định nhưng cần thêm bộ phận cơ thể thiếu!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_FOOT =
    { $unassigned ->
        [0] Bàn chân phải được xác định nhưng bạn cần thêm chân trên, chân dưới, ngực, eo hoặc bụng kèm theo để hoạt động!
        [1] Bàn chân phải được xác định nhưng bạn cần thêm chân dưới, ngực, eo hoặc bụng kèm theo để hoạt động!
        [2] Bàn chân phải được xác định nhưng bạn cần thêm chân trên, ngực, eo hoặc bụng kèm theo để hoạt động!
        [3] Bàn chân phải được xác định nhưng bạn cần thêm ngực, eo hoặc bụng kèm theo để hoạt động!
        [4] Bàn chân phải được xác định nhưng bạn cần thêm chân trên và chân dưới kèm theo để hoạt động!
        [5] Bàn chân phải được xác định nhưng bạn cần thêm chân trên kèm theo để hoạt động!
        [6] Bàn chân phải được xác định nhưng bạn cần thêm chân dưới kèm theo để hoạt động!
       *[unknown] Bàn chân phải được xác định nhưng cần thêm bộ phận cơ thể thiếu!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_LOWER_LEG =
    { $unassigned ->
        [0] Chân trên trái được xác định nhưng cần thêm chân trên!
        [1] Chân trên trái được xác định nhưng cần thêm ngực, eo và bụng được giao!
        [2] Chân trên trái được xác định nhưng cần thêm chân trên!
       *[unknown] Chân trên trái được xác định nhưng cần thêm bộ phận cơ thể thiếu!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_LOWER_LEG =
    { $unassigned ->
        [0] Chân trên phải được xác định nhưng cần thêm chân trên!
        [1] Chân trên phải được xác định nhưng cần thêm ngực, eo và bụng được giao!
        [2] Chân trên phải được xác định nhưng cần thêm chân trên!
       *[unknown] Chân trên phải được xác định nhưng cần thêm bộ phận cơ thể thiếu!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-LEFT_UPPER_LEG =
    { $unassigned ->
        [0] Chân trái trên được xác định nhưng cần thêm ngực, eo hoặc bụng!
       *[unknown] Chân trái trên được xác định nhưng cần thêm bộ phận cơ thể thiếu!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-RIGHT_UPPER_LEG =
    { $unassigned ->
        [0] Chân phải trên được xác định nhưng cần thêm ngực, eo hoặc bụng!
       *[unknown] Chân phải trên được xác định nhưng cần thêm bộ phận cơ thể thiếu!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-HIP =
    { $unassigned ->
        [0] Eo được xác định được xác định nhưng cần thêm ngực!
       *[unknown] Eo được xác định được xác định nhưng cần thêm bộ phận cơ thể thiếu!
    }
# $unassigned (Number) - Bits are based on BodyAssignment.ASSIGNMENT_RULES order
onboarding-assign_trackers-warning-WAIST =
    { $unassigned ->
        [0] Bụng được xác định nhưng cần thêm ngực!
       *[unknown] Bụng được xác định được xác định nhưng cần thêm bộ phận cơ thể thiếu!
    }

## Tracker mounting method choose

onboarding-choose_mounting = Bạn muốn dùng phương pháp hiểu chuần nào?
# Multiline text
onboarding-choose_mounting-description = Hướng lắp đặt chính xác cho vị trí của thiết bị theo dõi trên cơ thể của bạn.
onboarding-choose_mounting-auto_mounting = Cân chỉnh tự động
# Italicized text
onboarding-choose_mounting-auto_mounting-label-v2 = Khuyến khích dùng
onboarding-choose_mounting-auto_mounting-description = Điều này sẽ tự động phát hiện các hướng lắp đặt cho tất cả các thiết bị của bạn từ 2 tư thế
onboarding-choose_mounting-manual_mounting = Cân chỉnh thủ công
onboarding-choose_mounting-manual_mounting-description = Điều này sẽ cho phép bạn chọn hướng lắp theo cách thủ công cho từng thiết bị

## Tracker manual mounting setup

onboarding-manual_mounting = Cân chỉnh thủ công
onboarding-manual_mounting-description = Chọn từng tracker và chọn hướng nó được gắn
onboarding-manual_mounting-auto_mounting = Cân chỉnh tự động
onboarding-manual_mounting-next = Tiếp tục

## Tracker automatic mounting setup

onboarding-automatic_mounting-title = Cân chỉnh hướng gắn
onboarding-automatic_mounting-description = SlimeVR cần biết hướng gắn thực của tracker để hoạt động đúng, làm theo hướng dẫn để cân chỉnh hướng gắn tự động
onboarding-automatic_mounting-manual_mounting = Cân chỉnh thủ công
onboarding-automatic_mounting-next = Tiếp tục
onboarding-automatic_mounting-prev_step = Quay lại
onboarding-automatic_mounting-done-title = Đã cân chỉnh hướng gắn
onboarding-automatic_mounting-done-description = Cài đặt vị trí đã hoàn thành!
onboarding-automatic_mounting-done-restart = Thử lại
onboarding-automatic_mounting-mounting_reset-title = Đặt lại hướng gắn
onboarding-automatic_mounting-mounting_reset-step-0 = 1. Đứng khom người như tư thế trượt tuyết với đầu gối khom lại, thân trên hướng tới trước và hai tay co lại để giữ thăng bằng như hình bên
onboarding-automatic_mounting-mounting_reset-step-1 = 2. Nhấn nút đặt lại và chờ 3 giây trước khi hệ thống cân chỉnh hướng gắn tracker
onboarding-automatic_mounting-preparation-title = Chuẩn bị tư thế
onboarding-automatic_mounting-put_trackers_on-title = Đeo tracker lên người
onboarding-automatic_mounting-put_trackers_on-description = Để cân chỉnh hướng gắn của tracker, SlimeVR sẽ tiến hành đo góc nghiêng của tracker khi đang đeo để cân chỉnh hướng gắn, hãy đeo tracker theo đúng vị trí đã thiết lập
onboarding-automatic_mounting-put_trackers_on-next = Tiếp tục

## Tracker automatic proportions setup

onboarding-automatic_proportions-back = Quay lại cân chỉnh hướng gắn
onboarding-automatic_proportions-title = Đo kích thước cơ thể
onboarding-automatic_proportions-description = Để tracker có vị trí chính xác, SlimeVR cần biết các chỉ số kích thước cơ thể, làm theo hướng dẫn để đo kích thước cơ thể tự động
onboarding-automatic_proportions-prev_step = Quay lại
onboarding-automatic_proportions-put_trackers_on-title = Đeo tracker lên người
onboarding-automatic_proportions-put_trackers_on-description = Để đo kích thước cơ thể, SlimeVR sẽ sử dụng một thuật toán để dự đoán kích thước của các bộ phận, hãy đeo tracker theo đúng vị trí đã thiết lập như hình bên
onboarding-automatic_proportions-put_trackers_on-next = Tiếp tục
onboarding-automatic_proportions-requirements-title = Yêu cầu
# Each line of text is a different list item
onboarding-automatic_proportions-requirements-descriptionv2 =
    Bạn có ít nhất đủ tracker để theo dõi bàn chân của mình (thường là 5 tracker).
    Bạn đã bật tracker và headset và đang đeo chúng.
    Tracker và headset của bạn được kết nối với máy chủ SlimeVR và đang hoạt động bình thường (ví dụ: không bị lag, ngắt kết nối, v.v.).
    Headset của bạn đang báo cáo dữ liệu vị trí cho máy chủ SlimeVR (điều này thường có nghĩa là SteamVR đang chạy và kết nối với SlimeVR bằng driver SteamVR của SlimeVR).
    Tracking của bạn đang hoạt động và thể hiện chính xác các chuyển động của bạn (ví dụ: bạn đã thực hiện thiết đặt lại hoàn toàn và chúng di chuyển đúng hướng khi đá, cúi xuống, ngồi, v.v.).
onboarding-automatic_proportions-requirements-next = Tôi đã đọc các yêu cầu
onboarding-automatic_proportions-start_recording-title = Chuẩn bị đo
onboarding-automatic_proportions-start_recording-description = Phần mềm sẽ đo một số chuyển động, cử chỉ cụ thể, hãy chuẩn bị cho việc di chuyển theo yêu cầu trong phần tiếp theo
onboarding-automatic_proportions-start_recording-next = Bắt đầu
onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = Đang ghi...
onboarding-automatic_proportions-recording-description-p1 = Thực hiện các thao tác sau:
# Each line of text is a different list item
onboarding-automatic_proportions-recording-steps =
    Đứng thẳng, xoay đầu một vòng tròn.
    Cong lưng về phía trước và ngồi xổm. Trong khi ngồi xổm, nhìn sang trái, sau đó sang phải.
    Xoay phần thân trên của bạn sang trái (ngược chiều kim đồng hồ), sau đó đưa tay xuống đất.
    Xoay phần thân trên của bạn sang phải (theo chiều kim đồng hồ), sau đó với tay xuống đất.
    Xoay hông của bạn theo chuyển động tròn như thể bạn đang xoay vòng hula.
    Nếu còn thời gian, bạn có thể lặp lại các bước này cho đến khi kết thúc.
onboarding-automatic_proportions-recording-processing = Đang xử lí kết quả...
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 20)
onboarding-automatic_proportions-recording-timer =
    { $time ->
        [one] 1 second
       *[other] còn lại { $time } giây
    }
onboarding-automatic_proportions-verify_results-title = Kiểm tra kết quả
onboarding-automatic_proportions-verify_results-description = So sánh kết quả với chỉ số thực, Lưu ý: kết quả chỉ mang tính tương đối
onboarding-automatic_proportions-verify_results-results = Kết quả đo
onboarding-automatic_proportions-verify_results-processing = Đang xử lí kết quả...
onboarding-automatic_proportions-verify_results-redo = Thử lại
onboarding-automatic_proportions-verify_results-confirm = Kết quả tương đối chính xác
onboarding-automatic_proportions-done-title = Đã lưu chỉ số đo
onboarding-automatic_proportions-done-description = Quá trình đo đã hoàn tất
onboarding-automatic_proportions-error_modal-confirm = Đã hiểu!

## User height calibration


## Stay Aligned setup

## Trackers Still On notification

trackers_still_on-modal-title = Tracker vẫn còn bật
trackers_still_on-modal-description =
    Vẫn còn một hoặc nhiều tracker vẫn đang bật.
    Bạn vẫn muốn thoát khỏi SlimeVR?
trackers_still_on-modal-confirm = Thoát SlimeVR
trackers_still_on-modal-cancel = Vui lòng đợi...

## Firmware tool globals


## Firmware tool Steps


## firmware tool build status


## Firmware update status


## Dedicated Firmware Update Page


## Tray Menu

tray_menu-show = Xem
tray_menu-hide = Ẩn
tray_menu-quit = Thoát

## First exit modal

tray_or_exit_modal-title = Nút đóng nên làm gì?
# Multiline text
tray_or_exit_modal-description =
    Điều này cho phép bạn chọn xem bạn muốn thoát khỏi chương trình hoặc thu nhỏ nó vào khay khi nhấn nút đóng.
    
    Bạn có thể thay đổi điều này sau trong cài đặt giao diện.
tray_or_exit_modal-radio-exit = Thoát khi đóng
tray_or_exit_modal-radio-tray = Thu nhỏ vào khay hệ thống
tray_or_exit_modal-submit = Lưu
tray_or_exit_modal-cancel = Hủy

## Unknown device modal

unknown_device-modal-title = Thiết bị mới đã được tìm thấy!
unknown_device-modal-description =
    Có thiết bị mới với địa chỉ MAC <b>{ $deviceId }</b>.
    Bạn có muốn kết nối nó với SlimeVR không?
unknown_device-modal-confirm = Chắc!
unknown_device-modal-forget = Bỏ qua

## Error collection consent modal


## Tracking checklist section

