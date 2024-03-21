# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = サーバー接続中
websocket-connection_lost = サーバーへの接続が失われました。再接続を試みています...

## Update notification

version_update-title = 新しいバージョンが利用可能です：{ $version }
version_update-description = { version_update-update }をクリックすると、SlimeVRインストーラーがダウンロードされます。
version_update-update = アップデート
version_update-close = 閉じる

## Tips

tips-find_tracker = どのトラッカーがどれだかわからない？トラッカーを振ると、該当する項目がハイライトされます。
tips-do_not_move_heels = レコーディング中にかかとが動かないように注意しましょう！
tips-file_select = 使用するファイルをドラッグ&ドロップするか、 <u>参照</u>します。
tips-tap_setup = 追跡装置をゆっくり2回軽くタップして選択することができます、メニューから選ぶ必要はありません

## Body parts

body_part-NONE = 未設定
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
body_part-CHEST = 胸
body_part-WAIST = 腰
body_part-HIP = ヒップ
body_part-LEFT_SHOULDER = 左肩
body_part-LEFT_UPPER_ARM = 左上腕
body_part-LEFT_LOWER_ARM = 左前腕
body_part-LEFT_HAND = 左手
body_part-LEFT_UPPER_LEG = 左膝
body_part-LEFT_LOWER_LEG = 左足
body_part-LEFT_FOOT = 左足先

## Proportions

skeleton_bone-NONE = 無し
skeleton_bone-HEAD = ヘッドシフト
skeleton_bone-NECK = 首長さ
skeleton_bone-torso_group = 胴体の長さ
skeleton_bone-UPPER_CHEST = 上胸の長さ
skeleton_bone-CHEST_OFFSET = 胸オフセット
skeleton_bone-CHEST = 胸囲
skeleton_bone-WAIST = ウエスト長さ
skeleton_bone-HIP = ヒップ長さ
skeleton_bone-HIP_OFFSET = ヒップオフセット
skeleton_bone-HIPS_WIDTH = ヒップ幅
skeleton_bone-leg_group = 股下の長さ
skeleton_bone-UPPER_LEG = 膝長さ
skeleton_bone-LOWER_LEG = 足長さ
skeleton_bone-FOOT_LENGTH = 足先長さ
skeleton_bone-FOOT_SHIFT = 足先シフト
skeleton_bone-SKELETON_OFFSET = スケルトンオフセット
skeleton_bone-SHOULDERS_DISTANCE = 肩の距離
skeleton_bone-SHOULDERS_WIDTH = 肩幅
skeleton_bone-arm_group = 腕の長さ
skeleton_bone-UPPER_ARM = 上腕長さ
skeleton_bone-LOWER_ARM = 前腕長さ
skeleton_bone-HAND_Y = 手の距離 Y
skeleton_bone-HAND_Z = 手の距離Z
skeleton_bone-ELBOW_OFFSET = 肘オフセット

## Tracker reset buttons

reset-reset_all = すべてのプロポーションをリセット
reset-full = リセット
reset-mounting = リセットマウンティング
reset-yaw = ヨーリセット

## Serial detection stuff

serial_detection-new_device-p0 = 新しいシリアルデバイスを検出しました！
serial_detection-new_device-p1 = Wi-Fiの認証情報を入力してください！
serial_detection-new_device-p2 = 何をするか選択してください
serial_detection-open_wifi = Wi-Fiに接続
serial_detection-open_serial = シリアルコンソールを開く
serial_detection-submit = 実行！
serial_detection-close = 閉じる

## Navigation bar

navbar-home = ホーム
navbar-body_proportions = ボディプロポーション
navbar-trackers_assign = トラッカー割り当て
navbar-mounting = マウントキャリブレーション
navbar-onboarding = セットアップ ウィザード
navbar-settings = 設定

## Biovision hierarchy recording

bvh-start_recording = BVHレコーディング
bvh-recording = レコーディング中...

## Tracking pause

tracking-unpaused = トラッキング停止
tracking-paused = トラッキング再開

## Widget: Overlay settings

widget-overlay = オーバーレイ設定
widget-overlay-is_visible_label = SteamVRでオーバーレイを表示する
widget-overlay-is_mirrored_label = オーバーレイをミラーとして表示する

## Widget: Drift compensation

widget-drift_compensation-clear = ドリフト補正をクリアする

## Widget: Clear Reset Mounting

widget-clear_mounting = リセットマウンティングをクリア

## Widget: Developer settings

widget-developer_mode = 開発者モード
widget-developer_mode-high_contrast = ハイ コントラスト
widget-developer_mode-precise_rotation = 正確な回転角度を表示
widget-developer_mode-fast_data_feed = 高速表示モード
widget-developer_mode-filter_slimes_and_hmd = SlimeVRとHMDのみを表示
widget-developer_mode-sort_by_name = 表示名順
widget-developer_mode-raw_slime_rotation = 元の回転角度
widget-developer_mode-more_info = 他情報

## Widget: IMU Visualizer

widget-imu_visualizer = 回転
widget-imu_visualizer-rotation_raw = 生
widget-imu_visualizer-rotation_preview = 生
widget-imu_visualizer-rotation_hide = 隠す

## Widget: Skeleton Visualizer

widget-skeleton_visualizer-preview = スケルトンプレビュー
widget-skeleton_visualizer-hide = 非表示

## Tracker status

tracker-status-none = ステータスなし
tracker-status-busy = Busy
tracker-status-error = エラー
tracker-status-disconnected = 切断
tracker-status-occluded = Occluded
tracker-status-ok = 接続中
tracker-status-timed_out = タイムアウト

## Tracker status columns

tracker-table-column-name = Name
tracker-table-column-type = Type
tracker-table-column-battery = バッテリー
tracker-table-column-ping = Ping
tracker-table-column-tps = TPS
tracker-table-column-temperature = 温度. °C
tracker-table-column-linear-acceleration = 加速度. X/Y/Z
tracker-table-column-rotation = 回転 X/Y/Z
tracker-table-column-position = 位置 X/Y/Z
tracker-table-column-url = URL

## Tracker rotation

tracker-rotation-front = 前
tracker-rotation-front_left = 左前
tracker-rotation-front_right = 右前
tracker-rotation-left = 左
tracker-rotation-right = 右
tracker-rotation-back = 後
tracker-rotation-back_left = 左後
tracker-rotation-back_right = 右後
tracker-rotation-custom = カスタム
tracker-rotation-overriden = (マウンティングリセットによる上書き)

## Tracker information

tracker-infos-manufacturer = メーカ－
tracker-infos-display_name = 表示名
tracker-infos-custom_name = カスタム名称
tracker-infos-url = トラッカーURL
tracker-infos-version = ファームウェアバージョン
tracker-infos-hardware_rev = ハードウエアのリビジョン
tracker-infos-hardware_identifier = ハードウェアID
tracker-infos-imu = 慣性計測センサー
tracker-infos-board_type = メインボード

## Tracker settings

tracker-settings-back = トラッカーリストへ戻る
tracker-settings-title = トラッカー設定
tracker-settings-assignment_section = 割り当て
tracker-settings-assignment_section-description = トラッカーが体のどの部位に装着されているか
tracker-settings-assignment_section-edit = 割り当ての編集
tracker-settings-mounting_section = 装着方向
tracker-settings-mounting_section-description = トラッカーをどの方向に装着していますか?
tracker-settings-mounting_section-edit = 装着向きの編集
tracker-settings-drift_compensation_section = ドリフト補正を行う
tracker-settings-drift_compensation_section-description = ドリフト補正が有効になっている場合、このトラッカーはドリフトを補正する必要がありますか?
tracker-settings-drift_compensation_section-edit = ドリフト補正を行う
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = トラッカー名称
tracker-settings-name_section-description = 自由に名称をつけてください
tracker-settings-name_section-placeholder = NightyBeast's left leg

## Tracker part card info

tracker-part_card-no_name = 名称無し
tracker-part_card-unassigned = 未割り当て

## Body assignment menu

body_assignment_menu = このトラッカーをどこに配置しますか？
body_assignment_menu-description = このトラッカーを割り当てる場所を選択します。また、トラッカーを一つずつ管理するのではなく、すべてのトラッカーを一括して管理することもできます。
body_assignment_menu-show_advanced_locations = 高度な割り当て場所の表示
body_assignment_menu-manage_trackers = すべてのトラッカーの管理
body_assignment_menu-unassign_tracker = トラッカーの割り当て解除

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
-tracker_selection-part = どのトラッカーを{ $body-part }に割り当てますか？
tracker_selection_menu-NONE = どのトラッカーに割り当てないか？
tracker_selection_menu-HEAD = { -tracker_selection-part(body-part: "頭") }
tracker_selection_menu-NECK = { -tracker_selection-part(body-part: "首") }
tracker_selection_menu-RIGHT_SHOULDER = { -tracker_selection-part(body-part: "右肩") }
tracker_selection_menu-RIGHT_UPPER_ARM = { -tracker_selection-part(body-part: "右上腕") }
tracker_selection_menu-RIGHT_LOWER_ARM = { -tracker_selection-part(body-part: "右前腕") }
tracker_selection_menu-RIGHT_HAND = { -tracker_selection-part(body-part: "右手") }
tracker_selection_menu-RIGHT_UPPER_LEG = { -tracker_selection-part(body-part: "右太もも") }
tracker_selection_menu-RIGHT_LOWER_LEG = { -tracker_selection-part(body-part: "右足首") }
tracker_selection_menu-RIGHT_FOOT = { -tracker_selection-part(body-part: "右足先") }
tracker_selection_menu-RIGHT_CONTROLLER = { -tracker_selection-part(body-part: "右コントローラ") }
tracker_selection_menu-UPPER_CHEST = { -tracker_selection-part } 上胸？
tracker_selection_menu-CHEST = { -tracker_selection-part(body-part: "胸") }
tracker_selection_menu-WAIST = { -tracker_selection-part(body-part: "腰") }
tracker_selection_menu-HIP = { -tracker_selection-part(body-part: "ヒップ") }
tracker_selection_menu-LEFT_SHOULDER = { -tracker_selection-part(body-part: "左肩") }
tracker_selection_menu-LEFT_UPPER_ARM = { -tracker_selection-part(body-part: "左上腕") }
tracker_selection_menu-LEFT_LOWER_ARM = { -tracker_selection-part(body-part: "左前腕") }
tracker_selection_menu-LEFT_HAND = { -tracker_selection-part(body-part: "左手") }
tracker_selection_menu-LEFT_UPPER_LEG = { -tracker_selection-part(body-part: "左太もも") }
tracker_selection_menu-LEFT_LOWER_LEG = { -tracker_selection-part(body-part: "左足首") }
tracker_selection_menu-LEFT_FOOT = { -tracker_selection-part(body-part: "左足先") }
tracker_selection_menu-LEFT_CONTROLLER = { -tracker_selection-part(body-part: "左コントローラ") }
tracker_selection_menu-unassigned = 未割り当てのトラッカー
tracker_selection_menu-assigned = 割り当て済みのトラッカー
tracker_selection_menu-dont_assign = 割り当てない
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning = <b>警告：</b>首のトラッカーを締め付けすぎると、頭部の血液循環に危険が生じる可能性があります！
tracker_selection_menu-neck_warning-done = リスクを理解しています
tracker_selection_menu-neck_warning-cancel = キャンセル

## Mounting menu

mounting_selection_menu = このトラッカーをどこに配置しますか？
mounting_selection_menu-close = 閉じる

## Sidebar settings

settings-sidebar-title = 設定
settings-sidebar-general = 一般
settings-sidebar-tracker_mechanics = トラッカーメカニズム
settings-sidebar-fk_settings = FK設定
settings-sidebar-gesture_control = ジェスチャーコントロール
settings-sidebar-interface = インターフェース
settings-sidebar-osc_router = OSCルーター
settings-sidebar-osc_trackers = VRChatOSCトラッカー
settings-sidebar-utils = ユーティリティ
settings-sidebar-serial = シリアルコンソール
settings-sidebar-appearance = 外観
settings-sidebar-notifications = 通知

## SteamVR settings

settings-general-steamvr = SteamVR
settings-general-steamvr-subtitle = SteamVRのトラッカー
# Not all translation keys support multiline, only the ones that specify it will actually
# split it in lines (that also means you can split in lines however you want in those).
# The first spaces (not tabs) for indentation will be ignored, just to make the file look nice when writing.
# This one is one of this cases that cares about multilines
settings-general-steamvr-description =
    有効化したい部位にチャックを入れてください。
    SlimeVRが行うことをよりコントロールしたい場合に便利です。
settings-general-steamvr-trackers-waist = 腰
settings-general-steamvr-trackers-chest = 胸
settings-general-steamvr-trackers-feet = 足
settings-general-steamvr-trackers-knees = 膝
settings-general-steamvr-trackers-elbows = 肘
settings-general-steamvr-trackers-hands = 手

## Tracker mechanics

settings-general-tracker_mechanics = トラッカーメカニズム
settings-general-tracker_mechanics-filtering = フィルター機能
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    トラッカーのフィルタリングの種類を選択します。
    Predictionは動きを予測し、Smoothingは動きを滑らかにする。
settings-general-tracker_mechanics-filtering-type = フィルタータイプ
settings-general-tracker_mechanics-filtering-type-none = フィルター無し
settings-general-tracker_mechanics-filtering-type-none-description = トラッカーの値をそのまま使用します。フィルタリングは行いません。
settings-general-tracker_mechanics-filtering-type-smoothing = スムージング
settings-general-tracker_mechanics-filtering-type-smoothing-description = 動きを滑らかにしますが、若干の遅れが発生します
settings-general-tracker_mechanics-filtering-type-prediction = プリディクション
settings-general-tracker_mechanics-filtering-type-prediction-description = レイテンシーを減らし、動きをよりキビキビさせますが、ジッターが増加する場合があります。
settings-general-tracker_mechanics-filtering-amount = 数値
settings-general-tracker_mechanics-drift_compensation = ドリフト補正
# This cares about multilines
settings-general-tracker_mechanics-drift_compensation-description =
    逆回転をかけることで IMU のヨー軸ドリフトを補正します。
    補正量とリセット回数を変更します。
settings-general-tracker_mechanics-drift_compensation-enabled-label = ドリフト補正
settings-general-tracker_mechanics-drift_compensation-amount-label = 補正量
settings-general-tracker_mechanics-drift_compensation-max_resets-label = 最大リセット回数

## FK/Tracking settings

settings-general-fk_settings = FK設定
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
settings-general-fk_settings-leg_fk-reset_mounting_feet-description = つま先立ちで足のマウンティングリセットを有効にします。
settings-general-fk_settings-leg_fk-reset_mounting_feet = 足のマウンティングリセット
settings-general-fk_settings-arm_fk = アームトラッキング
settings-general-fk_settings-arm_fk-description = 腕の追従方法を変更する。
settings-general-fk_settings-arm_fk-force_arms = Force arms from HMD
settings-general-fk_settings-arm_fk-reset_mode-description = マウンティングリセットのために期待される腕のポーズを変更します。
settings-general-fk_settings-arm_fk-back = 後ろ
settings-general-fk_settings-arm_fk-back-description = デフォルトモードで、上腕を後ろに、下腕を前にします。
settings-general-fk_settings-arm_fk-tpose_up = Tポーズ(上げ)
settings-general-fk_settings-arm_fk-tpose_up-description = 完全リセット時は腕を下げて立っている姿勢、マウンティングリセット時は腕を体の両側に90度上げる。
settings-general-fk_settings-arm_fk-tpose_down = Tポーズ(下げ)
settings-general-fk_settings-arm_fk-tpose_down-description = 完全リセット時は腕を体の両側に90度上げ、マウンティングリセット時は腕を下げて立っている姿勢。
settings-general-fk_settings-arm_fk-forward = 前方ポーズ
settings-general-fk_settings-arm_fk-forward-description = リセット時に腕を前方に90度上げる。Vチューバーとして座っている時に便利。
settings-general-fk_settings-skeleton_settings-toggles = スケルトン設定
settings-general-fk_settings-skeleton_settings-description = スケルトン設定のオン/オフを切り替えます。これらはオンのままにしておくことをお勧めします。
settings-general-fk_settings-skeleton_settings-extended_spine_model = 拡張脊椎モデル
settings-general-fk_settings-skeleton_settings-extended_pelvis_model = 拡張骨盤モデル
settings-general-fk_settings-skeleton_settings-extended_knees_model = 拡張膝モデル
settings-general-fk_settings-skeleton_settings-ratios = スケルトン比率
settings-general-fk_settings-skeleton_settings-ratios-description = スケルトン設定の値を変更する。これらを変更した後、体の比率を調整する必要があるかもしれません。
settings-general-fk_settings-skeleton_settings-impute_waist_from_chest_hip = 胸から腰への推定
settings-general-fk_settings-self_localization-title = モーションキャプチャモード
settings-general-fk_settings-vive_emulation-title = Viveエミュレーション
settings-general-fk_settings-vive_emulation-description = Viveトラッカーが抱える腰トラッカーの問題をエミュレートします。
settings-general-fk_settings-vive_emulation-label = Viveエミュレーションの有効化

## Gesture control settings (tracker tapping)

settings-general-gesture_control = ジェスチャーコントロール
settings-general-gesture_control-subtitle = ダブルタップクイックリセット
settings-general-gesture_control-description = ダブルタップクイックリセットの有効・無効を設定します。有効にすると、最も高い胴体トラッカー上の任意の場所をダブルタップすると、クイックリセットが起動します。ディレイは、タップされてからリセットされるまでの時間です。
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
        [one] 1 tap
       *[other] { $amount } タップ
    }
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

settings-general-interface-dev_mode = 開発者モード
settings-general-interface-dev_mode-description = このモードは、詳細なデータが必要な場合や、接続されたトラッカーをより高度なレベルで操作する場合に役立ちます。
settings-general-interface-dev_mode-label = 開発者モード
settings-general-interface-theme = カラーテーマ
settings-general-interface-lang = 言語を選択
settings-general-interface-lang-description = 使用したいデフォルトの言語を変更する
settings-general-interface-lang-placeholder = 使用する言語を選択する
# Keep the font name untranslated
settings-interface-appearance-font = GUIフォント
settings-interface-appearance-font-os_font = OSフォント

## Notification settings

settings-interface-notifications = 通知
settings-general-interface-serial_detection = シリアルデバイスの検出
settings-general-interface-serial_detection-description = このオプションは、トラッカーとなり得る新しいシリアルデバイスを接続するたびにポップアップを表示します。これはトラッカーの設定プロセスを改善するのに役立ちます。
settings-general-interface-serial_detection-label = シリアルデバイスの検出
settings-general-interface-feedback_sound = フィードバック音
settings-general-interface-feedback_sound-label = フィードバック音
settings-general-interface-feedback_sound-volume = フィードバック音量

## Serial settings

settings-serial = シリアルコンソール
# This cares about multilines
settings-serial-description =
    シリアル通信のライブ情報フィードです。
    ファームウェアの動作を知る必要がある場合に有用かもしれません。
settings-serial-connection_lost = シリアルへの接続が失われました、再接続中...
settings-serial-reboot = リブート
settings-serial-factory_reset = ファクトリーリセット
settings-serial-factory_reset-warning-ok = 自分が何しているかを知っています。
settings-serial-factory_reset-warning-cancel = キャンセル
settings-serial-get_infos = 情報取得
settings-serial-serial_select = シリアルポートを選択
settings-serial-auto_dropdown_item = 自動

## OSC router settings

settings-osc-router = OSCルーター
# This cares about multilines
settings-osc-router-description =
    他のプログラムからの OSC メッセージを転送します。
    例えば、VRChat で他の OSC プログラムを使用する場合に便利です。
settings-osc-router-enable = 有効
settings-osc-router-enable-description = メッセージの転送を切り替えます。
settings-osc-router-enable-label = 有効
settings-osc-router-network = ネットワークポート
# This cares about multilines
settings-osc-router-network-description =
    データのリスニングと送信のためのポートを設定します。
    これらはSlimeVRサーバーで使用する他のポートと同じでかまいません。
settings-osc-router-network-port_in =
    .label = ポートイン
    .placeholder = ポートイン (デフォルト: 9002)
settings-osc-router-network-port_out =
    .label = ポートアウト
    .placeholder = ポートアウト (デフォルト: 9000)
settings-osc-router-network-address = ネットワークアドレス
settings-osc-router-network-address-description = データを送り出すアドレスを設定します。
settings-osc-router-network-address-placeholder = IPV4アドレス

## OSC VRChat settings

settings-osc-vrchat = VRChat OSCトラッカー
# This cares about multilines
settings-osc-vrchat-description =
    HMDのデータを受信して送信するためにVRChat固有の設定を変更する。
    FBT用のトラッカーデータ（Questスタンドアロンで動作します）
settings-osc-vrchat-enable = 有効
settings-osc-vrchat-enable-description = データの送受信を切り替える。
settings-osc-vrchat-enable-label = 有効
settings-osc-vrchat-network = ネットワークポート
settings-osc-vrchat-network-description = VRChatへのデータを送受信するためのポートを設定します。
settings-osc-vrchat-network-port_in =
    .label = ポートイン
    .placeholder = ポートイン (デフォルト: 9001)
settings-osc-vrchat-network-port_out =
    .label = ポートアウト
    .placeholder = ポートアウト (デフォルト: 9000)
settings-osc-vrchat-network-address = ネットワークアドレス
settings-osc-vrchat-network-address-description = VRChatにデータを送信するアドレスを選択してください（デバイスのWi-Fi設定を確認してください）
settings-osc-vrchat-network-address-placeholder = VRChatのIPアドレス
settings-osc-vrchat-network-trackers = トラッカー
settings-osc-vrchat-network-trackers-description = データの送受信を切り替える。
settings-osc-vrchat-network-trackers-chest = 胸
settings-osc-vrchat-network-trackers-hip = 腰
settings-osc-vrchat-network-trackers-knees = 膝
settings-osc-vrchat-network-trackers-feet = 足
settings-osc-vrchat-network-trackers-elbows = 肘

## VMC OSC settings

settings-osc-vmc = バーチャルモーションキャプチャ
settings-osc-vmc-enable = 有効
settings-osc-vmc-enable-label = 有効
settings-osc-vmc-network = ネットワークポート
settings-osc-vmc-network-port_in =
    .label = ポートイン
    .placeholder = ポートイン（デフォルト：３９５４０）
settings-osc-vmc-network-port_out =
    .label = ポートアウト
    .placeholder = ポートアウト（デフォルト：３９５３９）
settings-osc-vmc-network-address = ネットワークアドレス
settings-osc-vmc-network-address-placeholder = IPV4アドレス
settings-osc-vmc-vrm = VRMモデル

## Setup/onboarding menu

onboarding-skip = 設定をスキップする
onboarding-continue = 続ける
onboarding-wip = 実行中
onboarding-setup_warning-skip = セットアップをスキップする
onboarding-setup_warning-cancel = セットアップを続行する

## Wi-Fi setup

onboarding-wifi_creds-back = 戻る
onboarding-wifi_creds = Wi-Fi
# This cares about multilines
onboarding-wifi_creds-description =
    トラッカーはこれらの認証情報を使ってWi-Fiに接続します。
    現在接続している認証情報を使用してください。
onboarding-wifi_creds-skip = Wi-Fi設定をスキップする
onboarding-wifi_creds-submit = 実行！
onboarding-wifi_creds-ssid =
    .label = Wi-Fi名
    .placeholder = Enter Wi-Fi名
onboarding-wifi_creds-password =
    .label = Password
    .placeholder = Enter password

## Mounting setup

onboarding-reset_tutorial-back = マウントキャリブレーションに戻る
onboarding-reset_tutorial = リセットチュートリアル
onboarding-reset_tutorial-skip = ステップをスキップする

## Setup start

onboarding-home = SlimeVRへようこそ
onboarding-home-start = セットアップ開始！

## Enter VR part of setup

onboarding-enter_vr-back = トラッカー割り当てに戻る
onboarding-enter_vr-title = VRに入る時間だ！
onboarding-enter_vr-description = トラッカーを全部つけて、VRに突入せよ！
onboarding-enter_vr-ready = 準備完了

## Setup done

onboarding-done-title = 準備完了です！
onboarding-done-description = フルトラをお楽しみください
onboarding-done-close = ガイドを閉じる

## Tracker connection setup

onboarding-connect_tracker-back = Wi-Fi認証に戻る
onboarding-connect_tracker-title = 接続中のトラッカー
onboarding-connect_tracker-description-p0 = さあ、楽しい部分に移りましょう。すべてのトラッカーを接続します！
onboarding-connect_tracker-description-p1 = まだ接続されていないトラッカーたちをUSBポートを通して接続するだけです。
onboarding-connect_tracker-issue-serial = 接続に問題があります！
onboarding-connect_tracker-usb = USBトラッカー
onboarding-connect_tracker-connection_status-none = トラッカーを探しています
onboarding-connect_tracker-connection_status-connecting = Wi-Fiの認証情報を送信中
onboarding-connect_tracker-connection_status-looking_for_server = サーバーを探しています
onboarding-connect_tracker-connection_status-connection_error = Wi-Fiに接続できません
onboarding-connect_tracker-connection_status-could_not_find_server = サーバーが見つかりません
onboarding-connect_tracker-connection_status-done = サーバーに接続されました
# $amount (Number) - Amount of trackers connected (this is a number, but you can use CLDR plural rules for your language)
# More info on https://www.unicode.org/cldr/cldr-aux/charts/22/supplemental/language_plural_rules.html
# English in this case only has 2 plural rules, which are "one" and "other",
# we use 0 in an explicit way because there is no plural rule in english for 0, so we directly say
# if $amount is 0 then we say "No trackers connected"
onboarding-connect_tracker-connected_trackers =
    { $amount ->
        [0] No trackers
        [one] 1 tracker
       *[other] { $amount } trackers
    } connected
onboarding-connect_tracker-next = すべてのトラッカーを接続しました

## Tracker calibration tutorial

onboarding-calibration_tutorial = IMU校正チュートリアル
onboarding-calibration_tutorial-subtitle = これにより、センサーのドリフトを減らすことが役立ちます
onboarding-calibration_tutorial-status-calibrating = 校正中

## Tracker assignment tutorial

# This text has a character limit of around 11 characters, so please keep it short
onboarding-assignment_tutorial-sticker = ステッカー

## Tracker assignment setup

onboarding-assign_trackers-back = Wi-Fi認証に戻る
onboarding-assign_trackers-title = トラッカーを割り当てる
onboarding-assign_trackers-description = どのトラッカーをどこに置くか選んでみましょう。トラッカーを配置したい場所をクリックしてください。
# Look at translation of onboarding-connect_tracker-connected_trackers on how to use plurals
# $assigned (Number) - Trackers that have been assigned a body part
# $trackers (Number) - Trackers connected to the server
onboarding-assign_trackers-assigned =
    { $assigned } of { $trackers ->
        [one] 1 tracker
       *[other] { $trackers } trackers
    } assigned
onboarding-assign_trackers-advanced = 高度な割り当て場所の表示
onboarding-assign_trackers-next = すべてのトラッカーを割り当てました

## Tracker assignment warnings


## Tracker mounting method choose

# Italized text
onboarding-choose_mounting-auto_mounting-label = 実験的な

## Tracker manual mounting setup

onboarding-manual_mounting-back = VRの入力に戻る
onboarding-manual_mounting = マニュアルマウント
onboarding-manual_mounting-description = すべてのトラッカーをクリックし、どの方向にマウントするかを選択
onboarding-manual_mounting-auto_mounting = 自動マウント
onboarding-manual_mounting-next = 次のステップ

## Tracker automatic mounting setup

onboarding-automatic_mounting-back = VRの入力に戻る
onboarding-automatic_mounting-title = マウントキャリブレーション
onboarding-automatic_mounting-description = SlimeVRのトラッカーを動作させるためには、物理的なトラッカーの取り付け位置と合わせるために、トラッカーの取り付け方向を合わせる必要があります。
onboarding-automatic_mounting-manual_mounting = マニュアルマウント
onboarding-automatic_mounting-next = 次のステップ
onboarding-automatic_mounting-prev_step = 前のステップ
onboarding-automatic_mounting-done-title = 取り付け方向の較正を行いました。
onboarding-automatic_mounting-done-description = マウントのキャリブレーションが完了しました！
onboarding-automatic_mounting-done-restart = 最初に戻る
onboarding-automatic_mounting-mounting_reset-title = マウントリセット
onboarding-automatic_mounting-mounting_reset-step-0 = 1. 足を曲げ、上体を前に倒し、腕を曲げた状態で、スキーのポーズでしゃがむ。
onboarding-automatic_mounting-mounting_reset-step-1 = 2. リセットマウンティングボタンを押し、3秒待つと装着方向がリセットされます。
onboarding-automatic_mounting-preparation-title = 準備
onboarding-automatic_mounting-preparation-step-0 = 1. 両手を横に広げて直立します。
onboarding-automatic_mounting-preparation-step-1 = 2. リセットボタンを押し、3秒待つとリセットされます。
onboarding-automatic_mounting-put_trackers_on-title = トラッカーを装着する
onboarding-automatic_mounting-put_trackers_on-description = マウントの方向を較正するために、先ほど割り当てたトラッカーを使用します。右の図でどれがどれだかわかると思います。
onboarding-automatic_mounting-put_trackers_on-next = すべてのトラッカーを装着しました

## Tracker proportions method choose

# Italized text
onboarding-choose_proportions-auto_proportions-subtitle = おすすめされた

## Tracker manual proportions setup

onboarding-manual_proportions-back = チュートリアルをリセットする
onboarding-manual_proportions-title = マニュアルボディプロポーション
onboarding-manual_proportions-precision = 精度を調整する
onboarding-manual_proportions-auto = 自動キャリブレーション

## Tracker automatic proportions setup

onboarding-automatic_proportions-back = チュートリアルをリセットする
onboarding-automatic_proportions-title = 自分の体の測定
onboarding-automatic_proportions-description = SlimeVRのトラッカーが動作するためには、あなたの骨の長さを知る必要があります。この短いキャリブレーションでそれを測定します。
onboarding-automatic_proportions-manual = 手動調整
onboarding-automatic_proportions-prev_step = 前のステップ
onboarding-automatic_proportions-put_trackers_on-title = トラッカーを装着する
onboarding-automatic_proportions-put_trackers_on-description = プロポーションを調整するために、先ほど割り当てたトラッカーを使用します。右の図で、どれがどのトラッカーかわかると思います。
onboarding-automatic_proportions-put_trackers_on-next = すべてのトラッカーを装着しました
onboarding-automatic_proportions-requirements-title = 要件
onboarding-automatic_proportions-requirements-next = 要件を読みました
onboarding-automatic_proportions-start_recording-title = 測定の準備をする
onboarding-automatic_proportions-start_recording-description = これから具体的なポーズや動きを記録します。これらは次の画面に表示されます。ボタンが押されたらすぐに始められるように準備しておいてください！
onboarding-automatic_proportions-start_recording-next = レコーディングスタート
onboarding-automatic_proportions-recording-title = REC
onboarding-automatic_proportions-recording-description-p0 = レコーディング中...
onboarding-automatic_proportions-recording-description-p1 = 以下に示すような動きをします。
onboarding-automatic_proportions-recording-processing = 結果を処理中
# $time (Number) - Seconds left for the automatic calibration recording to finish (max 20)
onboarding-automatic_proportions-recording-timer =
    { $time ->
        [one] 1 second left
       *[other] { $time } seconds left
    }
onboarding-automatic_proportions-verify_results-title = 結果を確認
onboarding-automatic_proportions-verify_results-description = 以下の結果を確認してください。正しく表示されていますか？
onboarding-automatic_proportions-verify_results-results = 記録結果
onboarding-automatic_proportions-verify_results-processing = 結果の処理
onboarding-automatic_proportions-verify_results-redo = レコーディングやり直し
onboarding-automatic_proportions-verify_results-confirm = 正確です
onboarding-automatic_proportions-done-title = 体を測定して保存
onboarding-automatic_proportions-done-description = ボディプロポーションのキャリブレーションが完了しました！

## Home

home-no_trackers = トラッカーを検出できません。もしくは割り当てられていません。

## Trackers Still On notification


## Status system

status_system-StatusTrackerReset = 一つ以上のトラッカーが調整されていないため、完全なリセットを実行することをお勧めします
status_system-StatusSteamVRDisconnected =
    { $type ->
        [steamvr_feeder] SlimeVR Feederアプリに接続されていません
       *[other] SlimeVRドライバ経由でSteamVRに接続されていません
    }
status_system-StatusTrackerError = { $trackerName } トラッカーにエラーが発生しています
