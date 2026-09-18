# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = サーバー接続中
websocket-connection_lost = サーバーへの接続が失われました。再接続を試みています...
websocket-error-close = SlimeVRを終了する

## Update notification

version_update-title = 新しいバージョンが利用可能です：{ $version }
version_update-description = { version_update-update }をクリックすると、SlimeVRインストーラーがダウンロードされます。
version_update-update = アップデート
version_update-close = 閉じる

## Tips

tips-find_tracker = どのトラッカーがどれだかわからない？トラッカーを振ると、該当する項目がハイライトされます。
tips-do_not_move_heels = レコーディング中にかかとが動かないように注意しましょう！
tips-file_select = 使用するファイルをドラッグ&ドロップするか、 <u>参照</u>します。
tips-failed_webgl = WebGLの初期化に失敗しました。

## Units


## Dropdown


## Text input


## File input


## Window controls

titlebar-close = 閉じる

## Body parts

body_part-NONE = 未割り当て
body_part-HEAD = 頭
body_part-NECK = 首
body_part-RIGHT_SHOULDER = 右肩
body_part-RIGHT_UPPER_ARM = 右上腕
body_part-RIGHT_LOWER_ARM = 右前腕
body_part-RIGHT_HAND = 右手
body_part-RIGHT_UPPER_LEG = 右膝
body_part-RIGHT_LOWER_LEG = 右足
body_part-RIGHT_FOOT = 右足先
body_part-UPPER_CHEST = 上胸
body_part-HIP = 腰
body_part-LEFT_SHOULDER = 左肩
body_part-LEFT_UPPER_ARM = 左上腕
body_part-LEFT_LOWER_ARM = 左前腕
body_part-LEFT_HAND = 左手
body_part-LEFT_UPPER_LEG = 左膝
body_part-LEFT_LOWER_LEG = 左足
body_part-LEFT_FOOT = 左足先
body_part-LEFT_THUMB_PROXIMAL = 左母指近位部
body_part-LEFT_THUMB_DISTAL = 左母指遠位部
body_part-LEFT_INDEX_PROXIMAL = 左人差指近位部
body_part-LEFT_INDEX_INTERMEDIATE = 左人差指中間部
body_part-LEFT_INDEX_DISTAL = 左人差指遠位部
body_part-LEFT_MIDDLE_PROXIMAL = 左中指近位部
body_part-LEFT_MIDDLE_INTERMEDIATE = 左中指中間部
body_part-LEFT_MIDDLE_DISTAL = 左中指遠位部
body_part-LEFT_RING_PROXIMAL = 左薬指近位部
body_part-LEFT_RING_INTERMEDIATE = 左薬指中間部
body_part-LEFT_RING_DISTAL = 左薬指遠位部
body_part-LEFT_LITTLE_PROXIMAL = 左小指近位部
body_part-LEFT_LITTLE_INTERMEDIATE = 左小指中間部
body_part-LEFT_LITTLE_DISTAL = 左小指遠位部
body_part-RIGHT_THUMB_PROXIMAL = 右母指近位部
body_part-RIGHT_THUMB_DISTAL = 右母指遠位部
body_part-RIGHT_INDEX_PROXIMAL = 右人差指近位部
body_part-RIGHT_INDEX_INTERMEDIATE = 右人差指中間部
body_part-RIGHT_INDEX_DISTAL = 右人差指遠位部
body_part-RIGHT_MIDDLE_PROXIMAL = 右中指近位部
body_part-RIGHT_MIDDLE_INTERMEDIATE = 右中指中間部
body_part-RIGHT_MIDDLE_DISTAL = 右中指遠位部
body_part-RIGHT_RING_PROXIMAL = 右薬指近位部
body_part-RIGHT_RING_INTERMEDIATE = 右薬指中間部
body_part-RIGHT_RING_DISTAL = 右薬指遠位部
body_part-RIGHT_LITTLE_PROXIMAL = 右小指近位部
body_part-RIGHT_LITTLE_INTERMEDIATE = 右小指中間部
body_part-RIGHT_LITTLE_DISTAL = 右小指遠位部

## BoardType

board_type-UNKNOWN = 不明

## Proportions

skeleton_bone-NONE = 無し
skeleton_bone-HEAD = ヘッドシフト
skeleton_bone-NECK = 首長さ
skeleton_bone-torso_group = 胴体の長さ
skeleton_bone-UPPER_CHEST = 上胸の長さ
skeleton_bone-HIP = ヒップ長さ
skeleton_bone-HIPS_WIDTH = ヒップ幅
skeleton_bone-leg_group = 股下の長さ
skeleton_bone-UPPER_LEG = 膝長さ
skeleton_bone-LOWER_LEG = 足長さ
skeleton_bone-FOOT_LENGTH = 足先長さ
skeleton_bone-FOOT_SHIFT = 足先シフト
skeleton_bone-SHOULDERS_DISTANCE = 肩の距離
skeleton_bone-SHOULDERS_WIDTH = 肩幅
skeleton_bone-arm_group = 腕の長さ
skeleton_bone-UPPER_ARM = 上腕長さ
skeleton_bone-LOWER_ARM = 前腕長さ
skeleton_bone-HAND_Y = 手の距離 Y
skeleton_bone-HAND_Z = 手の距離Z

## Tracker reset buttons

reset-reset_all = すべてのプロポーションをリセット
reset-reset_all_warning-cancel = キャンセル
reset-full = リセット
reset-mounting = マウントリセット
reset-yaw = ヨーリセット

## Navigation bar

navbar-home = ホーム
navbar-body_proportions = ボディプロポーション
navbar-trackers_assign = トラッカー割り当て
navbar-mounting = マウントリセット
navbar-onboarding = セットアップ ウィザード
navbar-settings = 設定

## Biovision hierarchy recording

bvh-start_recording = BVHレコーディング
bvh-recording = レコーディング中...

## Tracking pause

tracking-unpaused = トラッキング停止
tracking-paused = トラッキング再開

## Widget: Developer settings

widget-developer_mode = 開発者モード
widget-developer_mode-high_contrast = ハイ コントラスト
widget-developer_mode-precise_rotation = 正確な回転角度を表示
widget-developer_mode-fast_data_feed = 高速表示モード
widget-developer_mode-raw_slime_rotation = 生

## Widget: IMU Visualizer

widget-imu_visualizer = 回転
widget-imu_visualizer-hide = 隠す
widget-imu_visualizer-rotation_raw = 生
widget-imu_visualizer-rotation_preview = 生

## Tracker status

tracker-status-none = ステータスなし
tracker-status-error = エラー
tracker-status-disconnected = 切断
tracker-status-ok = 接続中
tracker-status-timed_out = タイムアウト

## Tracker status columns

tracker-table-column-name = 名前
tracker-table-column-type = タイプ
tracker-table-column-battery = バッテリー
tracker-table-column-temperature = 温度. °C
tracker-table-column-linear-acceleration = 加速度. X/Y/Z
tracker-table-column-rotation = 回転 X/Y/Z
tracker-table-column-position = 位置 X/Y/Z

## Tracker rotation

tracker-rotation-front = 前
tracker-rotation-front_left = 左前
tracker-rotation-front_right = 右前
tracker-rotation-left = 左
tracker-rotation-right = 右
tracker-rotation-back = 後ろ
tracker-rotation-back_left = 左後
tracker-rotation-back_right = 右後
tracker-rotation-custom = カスタム

## Tracker information

tracker-infos-manufacturer = メーカ－
tracker-infos-display_name = 表示名
tracker-infos-custom_name = カスタム名称
tracker-infos-url = トラッカーURL
tracker-infos-hardware_identifier = ハードウェアID
tracker-infos-imu = 慣性計測センサー
tracker-infos-board_type = メインボード
tracker-infos-network_version = プロトコル・バージョン

## Tracker settings

tracker-settings-back = トラッカーリストへ戻る
tracker-settings-title = トラッカー設定
tracker-settings-assignment_section = 割り当て
tracker-settings-assignment_section-description = トラッカーが体のどの部位に装着されているか
tracker-settings-assignment_section-edit = 割り当ての編集
tracker-settings-mounting_section = 装着方向
tracker-settings-mounting_section-description = トラッカーをどの方向に装着していますか?
tracker-settings-mounting_section-edit = 装着向きの編集
# Multiline!
tracker-settings-use_mag-description =
    このトラッカーは、マグネトメーターの使用が許可されている場合、ドリフトを減らすためにマグネトメーターを使用すべきですか？ <b>設定を切り替える際は、トラッカーをシャットダウンしないでください！</b>
    
    まず、マグネトメーターの使用を許可する必要があります。<magSetting>設定に移動するにはここをクリックしてください</magSetting>。
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = トラッカー名称
tracker-settings-name_section-label = トラッカー名称
tracker-settings-forget = フォーゲット・トラッカー
tracker-settings-forget-description = スライムVRサーバーからトラッカーを削除し、サーバーが再起動するまで接続できないようにします。トラッカーの設定は失われません。
tracker-settings-forget-label = フォーゲット・トラッカー

## Dongle settings

dongle-infos-hardware_revision = ハードウエアのリビジョン
dongle-status-disconnected = 切断
dongle-settings-back = トラッカーリストへ戻る

## Tracker part card info

tracker-part_card-unassigned = 未割り当て

## Body assignment menu

body_assignment_menu = このトラッカーをどこに配置しますか？
body_assignment_menu-description = このトラッカーを割り当てる場所を選択します。また、トラッカーを一つずつ管理するのではなく、すべてのトラッカーを一括して管理することもできます。
body_assignment_menu-manage_trackers = すべてのトラッカーの管理
body_assignment_menu-unassign_tracker = トラッカーの割り当て解除

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning = <b>警告：</b>首のトラッカーを締め付けすぎると、頭部の血液循環に危険が生じる可能性があります！
tracker_selection_menu-neck_warning-done = リスクを理解しています
tracker_selection_menu-neck_warning-cancel = キャンセル

## Mounting menu

mounting_selection_menu-close = 閉じる

## Sidebar settings

settings-sidebar-title = 設定
settings-sidebar-general = 一般
settings-sidebar-trackers = トラッカー
settings-sidebar-interface = インターフェース
settings-sidebar-utils = ユーティリティ
settings-sidebar-appearance = 外観
settings-sidebar-notifications = 通知

## Bone routing settings

settings-routing-hands-warning-cancel = キャンセル

## SteamVR / Monado output settings

settings-driver-enable = 有効

## Tracker mechanics

settings-general-tracker_mechanics-filtering = フィルター機能
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    トラッカーのフィルタリングの種類を選択します。
    Predictionは動きを予測し、Smoothingは動きを滑らかにする。
settings-general-tracker_mechanics-filtering-type-none = フィルター無し
settings-general-tracker_mechanics-filtering-type-none-description = トラッカーの値をそのまま使用します。フィルタリングは行いません。
settings-general-tracker_mechanics-filtering-type-smoothing = スムージング
settings-general-tracker_mechanics-filtering-type-smoothing-description = 動きを滑らかにしますが、若干の遅れが発生します
settings-general-tracker_mechanics-filtering-type-prediction = プリディクション
settings-general-tracker_mechanics-filtering-type-prediction-description = レイテンシーを減らし、動きをよりキビキビさせますが、ジッターが増加する場合があります。
settings-stay_aligned-general-label = 一般
settings-stay_aligned-relaxed_poses-close = 閉じる

## Keybinds Page

settings-keybinds_full-reset = リセット
settings-keybinds_yaw-reset = ヨーリセット
settings-keybinds-recorder-modal-cancel-button = キャンセル

## FK/Tracking settings

# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = フロアクリップ
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = スケーティング補正
settings-general-fk_settings-leg_tweak-foot_plant = 足の着地
settings-general-fk_settings-leg_tweak-skating_correction-amount = スケーティング補正の強さ
settings-general-fk_settings-leg_tweak-skating_correction-description = スケート補正はアイススケートを補正しますが、特定の動きでは精度が低下する場合があります。これを有効にする際は、フルリセットし、ゲーム内で再校正してください。
settings-general-fk_settings-leg_tweak-floor_clip-description = フロアクリップを有効にすると、床を通り抜けることを減少させるか、完全に排除できます。これを有効にする際は、フルリセットし、ゲーム内で再校正してください。
settings-general-fk_settings-leg_tweak-toe_snap-description = 足指スナップは足トラッカーを使用していない場合、足の回転を推測しようとします。
settings-general-fk_settings-leg_tweak-foot_plant-description = 足の着地は足が地面に接触したときに足を地面に平行に回転させます。
settings-general-fk_settings-leg_fk = 足のトラッキング
settings-general-fk_settings-arm_fk-back = 後ろ
settings-general-fk_settings-arm_fk-back-description = デフォルトモードで、上腕を後ろに、下腕を前にします。
settings-general-fk_settings-arm_fk-tpose_up = Tポーズ(上げ)
settings-general-fk_settings-arm_fk-tpose_up-description = 完全リセット時は腕を下げて立っている姿勢、マウンティングリセット時は腕を体の両側に90度上げる。
settings-general-fk_settings-arm_fk-tpose_down = Tポーズ(下げ)
settings-general-fk_settings-arm_fk-tpose_down-description = 完全リセット時は腕を体の両側に90度上げ、マウンティングリセット時は腕を下げて立っている姿勢。
settings-general-fk_settings-arm_fk-forward = 前方ポーズ
settings-general-fk_settings-arm_fk-forward-description = リセット時に腕を前方に90度上げる。Vチューバーとして座っている時に便利。
settings-general-fk_settings-skeleton_settings-ratios = スケルトン比率
settings-general-fk_settings-skeleton_settings-ratios-description = スケルトン設定の値を変更する。これらを変更した後、体の比率を調整する必要があるかもしれません。
settings-general-fk_settings-self_localization-title = モーションキャプチャモード

## Gesture control settings (tracker tapping)

settings-general-gesture_control-subtitle = ダブルタップクイックリセット
settings-general-gesture_control-description = ダブルタップクイックリセットの有効・無効を設定します。有効にすると、最も高い胴体トラッカー上の任意の場所をダブルタップすると、クイックリセットが起動します。ディレイは、タップされてからリセットされるまでの時間です。
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
        [one] 1 tap
       *[other] { $amount } タップ
    }
# This is a unit: 3 trackers, 2 trackers, 1 tracker
# $amount (Number) - Amount of trackers
settings-general-gesture_control-trackers = { $amount } トラッカー
settings-general-gesture_control-yawResetEnabled = タップによるヨーリセットを有効にします
settings-general-gesture_control-yawResetDelay = ヨーリセット遅延
settings-general-gesture_control-yawResetTaps = ヨーリセット用のタップ
settings-general-gesture_control-fullResetEnabled = タップによるフルリセットを有効にします
settings-general-gesture_control-fullResetDelay = フルリセット遅延
settings-general-gesture_control-fullResetTaps = フルリセット用のタップ
settings-general-gesture_control-mountingResetEnabled = タップによるマウントリセットを有効にする
settings-general-gesture_control-mountingResetDelay = マウントリセットディレイ
settings-general-gesture_control-mountingResetTaps = タップによるマウントリセット

## Appearance settings

settings-interface-appearance = 外観
settings-general-interface-dev_mode = 開発者モード
settings-general-interface-dev_mode-description = このモードは、詳細なデータが必要な場合や、接続されたトラッカーをより高度なレベルで操作する場合に役立ちます。
settings-general-interface-dev_mode-label = 開発者モード
settings-general-interface-lang = 言語を選択
settings-general-interface-lang-description = 使用したいデフォルトの言語を変更する
settings-general-interface-lang-placeholder = 使用する言語を選択する
# Keep the font name untranslated
settings-interface-appearance-font = GUIフォント
settings-interface-appearance-font-placeholder = デフォルトフォント
settings-interface-appearance-font-os_font = OSフォント
settings-interface-appearance-font-slime_font = デフォルトフォント

## Notification settings

settings-interface-notifications = 通知
settings-general-interface-feedback_sound = フィードバック音
settings-general-interface-feedback_sound-label = フィードバック音
settings-general-interface-feedback_sound-volume = フィードバック音量
settings-general-interface-connected_trackers_warning = 接続されたトラッカー警告

## Behavior settings

settings-general-interface-dev_mode = 開発者モード
settings-general-interface-dev_mode-label = 開発者モード
settings-general-interface-use_tray = システムトレイに最小化する
settings-general-interface-use_tray-label = システムトレイに最小化する

## Serial settings

settings-serial-connection_lost = シリアルへの接続が失われました、再接続中...
settings-serial-reboot = リブート
settings-serial-factory_reset = ファクトリーリセット
settings-serial-factory_reset-warning-ok = 自分が何しているかを知っています。
settings-serial-factory_reset-warning-cancel = キャンセル
settings-serial-serial_select = シリアルポートを選択
settings-serial-auto_dropdown_item = 自動
settings-serial-send_command-warning-ok = 自分が何しているかを知っています。
settings-serial-send_command-warning-cancel = キャンセル

## OSC VRChat settings

settings-osc-vrchat-enable = 有効
settings-osc-vrchat-enable-description = データの送受信を切り替える。
settings-osc-vrchat-enable-label = 有効
settings-osc-vrchat-network = ネットワークポート
settings-osc-vrchat-network-port_in =
    .label = ポートイン
    .placeholder = ポートイン (デフォルト: 9001)
settings-osc-vrchat-network-port_out =
    .label = ポートアウト
    .placeholder = ポートアウト (デフォルト: 9000)
settings-osc-vrchat-network-address = ネットワークアドレス
settings-osc-vrchat-network-address-placeholder = VRChatのIPアドレス

## VRChat OSC status

settings-osc-vrchat-status-tracking = 回転
settings-osc-vrchat-status-badge-error = エラー
settings-osc-vrchat-status-badge-unknown = 不明

## VMC OSC settings

settings-osc-vmc = バーチャルモーションキャプチャ
settings-osc-vmc-enable = 有効
settings-osc-vmc-enable-description = データの送受信を切り替える。

## Common OSC settings


## Advanced settings


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

