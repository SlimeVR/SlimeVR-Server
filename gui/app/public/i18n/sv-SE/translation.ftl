# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Ansluter till server
websocket-connection_lost = Anslutning förlorad till server. Försöker återansluta
websocket-connection_lost-desc = Det verkar som att SlimeVR servern kraschade. Kolla loggen och starta om programmet.
websocket-timedout = Kunde ej koppla till servern.
websocket-timedout-desc = Det verkar som att SlimeVR servern kraschade eller löpte ur tidsgränsen. Kolla loggen och starta om programmet.
websocket-error-close = Stäng SlimeVR
websocket-error-logs = Öppna logg-mappen

## Update notification

version_update-title = Ny version tillgänglig: { $version }
version_update-description = Att trycka på "{ version_update-update }" kommer ladda ner SlimeVR installatorn för dig
version_update-update = Updatera
version_update-close = Stäng

## Tips

tips-find_tracker = Osäker vilken spårare är vilken? Dela en spårare och den kommer markera motsvarande spårare.
tips-do_not_move_heels = Se till att dina hälar inte rör sig under inspelningen!
tips-file_select = Dra och släpp filer som du vill använda, eller <u>bläddra</u>.
tips-failed_webgl = Misslyckades att initiera WebGL.

## Units


## Dropdown


## Text input


## File input


## Window controls

titlebar-close = Stäng

## Body parts

body_part-NONE = Ej tilldelad
body_part-HEAD = Huvud
body_part-NECK = Nacke
body_part-RIGHT_SHOULDER = Höger axel
body_part-RIGHT_UPPER_ARM = Höger överarm
body_part-RIGHT_LOWER_ARM = Höger underarm
body_part-RIGHT_HAND = Höger hand
body_part-RIGHT_UPPER_LEG = Höger lår
body_part-RIGHT_LOWER_LEG = Höger vrist
body_part-RIGHT_FOOT = Höger fot
body_part-UPPER_CHEST = Över bröst
body_part-HIP = Höft
body_part-LEFT_SHOULDER = Vänster axel
body_part-LEFT_UPPER_ARM = Vänster överarm
body_part-LEFT_LOWER_ARM = Väster underarm
body_part-LEFT_HAND = Vänster hand
body_part-LEFT_UPPER_LEG = Vänster lår
body_part-LEFT_LOWER_LEG = Vänster vrist
body_part-LEFT_FOOT = Vänster fot
body_part-LEFT_THUMB_METACARPAL = Vänster tumme metacarpalben
body_part-LEFT_THUMB_PROXIMAL = Vänster tumme proximal falang
body_part-LEFT_THUMB_DISTAL = Vänster tumme distal falang
body_part-LEFT_INDEX_PROXIMAL = Vänster pekfinger proximal falang
body_part-LEFT_INDEX_INTERMEDIATE = Vänster pekfinger mellanfalang
body_part-LEFT_INDEX_DISTAL = Vänster pekfinger distal falang
body_part-LEFT_MIDDLE_PROXIMAL = Vänster långfinger proximal falang
body_part-LEFT_MIDDLE_INTERMEDIATE = Vänster långfinger mellanfalang
body_part-LEFT_MIDDLE_DISTAL = Vänster långfinger distal falang
body_part-LEFT_RING_PROXIMAL = Vänster ringfinger proximal falang
body_part-LEFT_RING_INTERMEDIATE = Vänster ringfinger mellanfalang
body_part-LEFT_RING_DISTAL = Vänster ringfinger distal falang
body_part-LEFT_LITTLE_PROXIMAL = Vänster ringfinger proximal falang
body_part-LEFT_LITTLE_INTERMEDIATE = Vänster lillfinger mellanfalang
body_part-LEFT_LITTLE_DISTAL = Vänster lillfinger distal falang
body_part-RIGHT_THUMB_METACARPAL = Höger tumme metacarpalben
body_part-RIGHT_THUMB_PROXIMAL = Höger tumme proximal falang
body_part-RIGHT_THUMB_DISTAL = Höger tumme distal falang
body_part-RIGHT_INDEX_PROXIMAL = Höger pekfinger proximal falang
body_part-RIGHT_INDEX_INTERMEDIATE = Höger pekfinger mellanfalang
body_part-RIGHT_INDEX_DISTAL = Höger pekfinger distal falang
body_part-RIGHT_MIDDLE_PROXIMAL = Höger långfinger proximal falang
body_part-RIGHT_MIDDLE_INTERMEDIATE = Höger långfinger mellanfalang
body_part-RIGHT_MIDDLE_DISTAL = Höger långfinger distal falang
body_part-RIGHT_RING_PROXIMAL = Höger ringfinger proximal falang
body_part-RIGHT_RING_INTERMEDIATE = Höger ringfinger mellanfalang
body_part-RIGHT_RING_DISTAL = Höger ringfinger distal falang
body_part-RIGHT_LITTLE_PROXIMAL = Höger ringfinger proximal falang
body_part-RIGHT_LITTLE_INTERMEDIATE = Höger lillfinger mellanfalang
body_part-RIGHT_LITTLE_DISTAL = Höger lillfinger distal falang

## BoardType

board_type-UNKNOWN = Okänd
board_type-CUSTOM = Anpassat kretskort

## Proportions

skeleton_bone-NONE = Ingen
skeleton_bone-HEAD = Huvudskift
skeleton_bone-HEAD-desc =
    Detta är distansen från ditt headset till mittpunkten av ditt huvud.
    För att justera det, skaka ditt huvud höger eller vänster, som om att du säger nej och modifiera
    det tills någon rörelse i andra rörelsesensorer är obetydlig.
skeleton_bone-NECK = Halsens längd
skeleton_bone-NECK-desc =
    Detta är distansen från mittpunkten av ditt huvud till din nackes bas.
    För att justera det, skaka ditt huvud upp och ner, som om att du säger ja eller luta ditt
    huvud höger eller vänster och modifiera det tills någon rörelse i andra rörelsesensorer är obetydlig.
skeleton_bone-torso_group = Halsens längd
skeleton_bone-torso_group-desc =
    Detta är distansen från din nackes bas till dina höfter.
    För att justera det, modifiera det stående rakt upp tills dina virtuella höfter
    matchar dina riktiga.
skeleton_bone-UPPER_CHEST = Övre bröstkorgens längd
skeleton_bone-UPPER_CHEST-desc =
    Detta är distansen från din nackes bas till mitten av din bröstkorg.
    För att justera det, justera din torso-längd ordentligt och modifiera den i olika olika positioner.
    (sittande, böjd, liggande, osv.) Tills din virtuella ryggrad matchar med din riktiga.
skeleton_bone-LOWER_CHEST-desc =
    Detta är distansen från mitten av din bröstkorg till mitten av din ryggrad.
    För att justera det, justera din torso-längd ordentligt och modifiera den i olika olika positioner.
    (sittande, böjd, liggande, osv.) Tills din virtuella ryggrad matchar med din riktiga.
skeleton_bone-HIP = Höftlängd
skeleton_bone-HIPS_WIDTH = Höftbredd
skeleton_bone-HIPS_WIDTH-desc =
    Detta är distansen mellan början på dina ben.
    För att justera det, utför en full återställning med dina ben raka och modifiera den
    tills dina virtuella ben matchar med dina riktiga horisontellt.
skeleton_bone-leg_group = Benlängd
skeleton_bone-leg_group-desc =
    Detta är distansen från dina höfter till dina fötter.
    För att justera det, justera din Torso-längd ordentligt och modifiera
    den tills dina virtuella fötter är på samma nivå som dina riktiga.
skeleton_bone-UPPER_LEG = Längd på övre delen av benet
skeleton_bone-UPPER_LEG-desc =
    Detta är distansen från dina fötter till dina knän.
    För att justera det, justera din ben-längd ordentligt och modifiera
    den tills dina virtuella knän är på samma nivå som dina riktiga.
skeleton_bone-LOWER_LEG = Längd på underben
skeleton_bone-LOWER_LEG-desc =
    Detta är avståndet från dina knän till dina fotleder.
    För att justera det, justera din ben-längd ordentligt och modifiera
    den tills dina virtuella knän är på samma nivå som dina riktiga.
skeleton_bone-FOOT_LENGTH = Fot Längd
skeleton_bone-FOOT_LENGTH-desc =
    Detta är distansen från dina höftleder till dina tår.
    För att justera det, gå på tå och modifiera det tills dina virtuella fötter stannar på plats.
skeleton_bone-FOOT_SHIFT = Fotförskjutning
skeleton_bone-FOOT_SHIFT-desc =
    Detta värde är den horisontella distansen från dina ditt knä till din fotled.
    den tar hänsyn till att dina underben går baklänges när du står rakt upp.
    För att justera det, ställ fotens längd till 0, utför en full återställning och modifiera den tills
    dina virtuella fötter matchar mitten av dina fotleder.
skeleton_bone-SHOULDERS_DISTANCE = Avstånd mellan axlar
skeleton_bone-SHOULDERS_DISTANCE-desc =
    Detta är den vertikala distansen från din nackes bas till dina axlar.
    För att justera det, Längd på överarm till 0 och modifiera det tills dina virituella
    axel-sensorer är i linje med dina riktiga axlar.
skeleton_bone-SHOULDERS_WIDTH = Axlarnas bredd
skeleton_bone-SHOULDERS_WIDTH-desc =
    Detta är den horisontella distansen från din nackes bas till dina axlar.
    För att justera det, ändra längd på överarm till 0 och modifiera det tills dina virtuella
    axel-sensorer är i linje med dina riktiga axlar.
skeleton_bone-arm_group = Armlängd
skeleton_bone-arm_group-desc =
    Detta är avståndet från dina axlar till dina handleder.
    För att justera det, justera Avståndet mellan axlar ordentligt, ändra Handavstånd Y
    till 0 och modifiera tills dina hand-sensorer är i linje med dina handleder.
skeleton_bone-UPPER_ARM = Längd på överarm
skeleton_bone-UPPER_ARM-desc =
    Detta är distansen från dina axlar till dina armbågar.
    För att justera det, justera Armlängd ordentligt och modifiera det
    tills dina armbågs-sensorer är i linje med dina riktiga armbågar.
skeleton_bone-LOWER_ARM = Längd på underarm
skeleton_bone-LOWER_ARM-desc =
    Detta är avståndet från dina armbågar till dina handleder.
    För att justera det, justera Armlängd ordentligt och modifiera det
    tills dina armbågs-spårare matchar med dina riktiga armbågar.
skeleton_bone-HAND_Y = Handavstånd Y
skeleton_bone-HAND_Y-desc =
    Detta är den vertikala distansen från dina handleder till mitten av din hand.
    Föra att justera den till Motion Capture, justera Armlängd ordentligt och modifiera
    den tills dina hand-sensorer är i linje vertikalt med mitten av dina händer.
    För att justera den till Armbågs-spårning från dina dina kontroller, sätt Armlängd till 0 och
    modifiera tills dina armbågs-sensorer är i linje vertikalt med dina handleder.
skeleton_bone-HAND_Z = Handavstånd Z
skeleton_bone-HAND_Z-desc =
    Detta är den horisontella distansen från dina handleder till mitten av din hand.
    För att justera det för Motion Capture, sätt det till 0.
    För att justera det för armbågs-spårning från dina kontroller, sätt Armlängd till 0 och
    modifiera tills dina armbågs-spårare är i linje horisontellt med dina handleder.

## Tracker reset buttons

reset-reset_all = Återställ alla proportioner
reset-reset_all_warning-reset = Återställ proportioner
reset-reset_all_warning-cancel = Avbryt
reset-full = Fullständig återställning
reset-mounting = Monterings-återställning
reset-yaw = Återställning av gir

## Navigation bar

navbar-home = Hem
navbar-body_proportions = Kroppsproportioner
navbar-trackers_assign = Tracker Uppgift
navbar-mounting = Monterings-återställning
navbar-onboarding = Inställningsguide
navbar-settings = Inställningar

## Biovision hierarchy recording

bvh-start_recording = Spela in BVH-rekord
bvh-recording = Inspelning...

## Tracking pause

tracking-unpaused = Pausa spårning
tracking-paused = Avbryt spårning

## Widget: Developer settings

widget-developer_mode = Utvecklarläge
widget-developer_mode-high_contrast = Hög kontrast
widget-developer_mode-precise_rotation = Exakt rotation
widget-developer_mode-fast_data_feed = Snabb dataflöde
widget-developer_mode-raw_slime_rotation = Rå rotation

## Widget: IMU Visualizer

widget-imu_visualizer = Rotation
widget-imu_visualizer-preview = Förhandsvisa
widget-imu_visualizer-hide = Göm
widget-imu_visualizer-rotation_raw = Rå rotation
widget-imu_visualizer-rotation_preview = Förhandsgranska rotation
widget-imu_visualizer-stay_aligned = Behåll inriktning

## Tracker status

tracker-status-none = Ingen status
tracker-status-busy = Upptagen
tracker-status-error = Fel
tracker-status-disconnected = Frånkopplad
tracker-status-occluded = Ocklusivt
tracker-status-ok = Okej
tracker-status-timed_out = Tid ute

## Tracker status columns

tracker-table-column-name = Namn
tracker-table-column-type = Typ
tracker-table-column-battery = Batteri
tracker-table-column-temperature = Temperatur i Celsius:
tracker-table-column-stay_aligned = Behåll inriktning
tracker-table-column-url = WEBBADRESS

## Tracker rotation

tracker-rotation-front = Fram
tracker-rotation-front_left = Fram-vänster
tracker-rotation-front_right = Fram-höger
tracker-rotation-left = Vänster
tracker-rotation-right = Höger
tracker-rotation-back = Tillbaka
tracker-rotation-back_left = Bak-väster
tracker-rotation-back_right = Bak-höger
tracker-rotation-custom = Egenanpassad

## Tracker information

tracker-infos-manufacturer = Tillverkare
tracker-infos-display_name = Visa namn
tracker-infos-custom_name = Anpassat namn
tracker-infos-url = URL för spårare
tracker-infos-hardware_identifier = Hårdvaru-ID
tracker-infos-imu = IMU-sensor
tracker-infos-board_type = Huvudkrets
tracker-infos-network_version = Protokollsversion
tracker-infos-magnetometer-status-v1 =
    { $status ->
        [DISABLED] Avaktiverad
        [ENABLED] Aktiverad
       *[NOT_SUPPORTED] Ej stödd
    }

## Tracker settings

tracker-settings-back = Gå tillbaka till trackerslistan
tracker-settings-title = Inställningar för spårare
tracker-settings-assignment_section = Uppdrag
tracker-settings-assignment_section-description = Vilken del av kroppen som spåraren är kopplad till.
tracker-settings-assignment_section-edit = Redigera uppdrag
tracker-settings-mounting_section = Monteringsriktning
tracker-settings-mounting_section-description = Var är spårningsenheten monterad?
tracker-settings-mounting_section-edit = Redigera montering
tracker-settings-use_mag = Aktivera magnetometer på denna sensorn.
# Multiline!
tracker-settings-use_mag-description =
    Ska denna sensorn använda magnetometern för att minska drift när magnetometer-användning är tillåten? <b> Var vänlig och stäng inte av sensorn när du växlar av och på denna inställningen! <b>
    
    Du behöver tillåta magnetometer-användning först <magSetting> klicka här för att gå till inställningen </magSetting>.
tracker-settings-use_mag-label = Tillåt magnetometer
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Sensorns namn
tracker-settings-name_section-placeholder = Bokstensmannens vänstra ben
tracker-settings-name_section-label = Sensorns namn
tracker-settings-forget = Glöm spårning
tracker-settings-forget-description = Tar bort trackern från SlimeVR-servern och förhindrar den från att ansluta till den tills servern startas om. Konfigurationen av trackern kommer inte att gå förlorad.
tracker-settings-forget-label = Glöm spårning
tracker-settings-update-low-battery = Kan ej uppdatera. Batteriet är under 50%
tracker-settings-update-up_to_date = Uppdaterad
tracker-settings-update = Uppdatera nu
tracker-settings-update-title = Mjukvaroversion

## Dongle settings

dongle-infos-hardware_revision = Revision av hårdvara
dongle-status-disconnected = Frånkopplad
dongle-settings-back = Gå tillbaka till trackerslistan
dongle-settings-update = Uppdatera nu
dongle-settings-update-title = Mjukvaroversion

## Tracker part card info

tracker-part_card-unassigned = Ej tilldelad

## Body assignment menu

body_assignment_menu = Var vill du att den här trackern ska vara?
body_assignment_menu-description = Välj en plats där du vill att denna tracker ska tilldelas. Alternativt kan du välja att hantera alla trackers på en gång istället för en och en.
body_assignment_menu-manage_trackers = Hantera alla spårare
body_assignment_menu-unassign_tracker = Ta bort tilldelning av spårare

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Varning:</b> En halsboja kan vara livsfarlig om den sitter för hårt,
    då kan remmen skära av blodcirkulationen till huvudet!
tracker_selection_menu-neck_warning-done = Jag förstår riskerna
tracker_selection_menu-neck_warning-cancel = Avbryt

## Mounting menu

mounting_selection_menu-close = Stäng

## Sidebar settings

settings-sidebar-title = Inställningar
settings-sidebar-general = Allmän
settings-sidebar-stay_aligned = Behåll inriktning
settings-sidebar-trackers = Spårare
settings-sidebar-interface = Gränssnitt
settings-sidebar-utils = Verktyg
settings-sidebar-appearance = Utseende
settings-sidebar-notifications = Meddelanden
settings-sidebar-firmware-tool = DIY Mjukvaroverktyg
settings-sidebar-vrc_warnings = VRChat Config varningar
settings-sidebar-advanced = Avancerad

## Bone routing settings

settings-routing-output-badge-off = Av
settings-routing-hands-warning-cancel = Avbryt

## SteamVR / Monado output settings

settings-driver-enable = Aktivera
settings-driver-status-badge-disabled = Av

## Tracker mechanics

settings-general-tracker_mechanics-filtering = Filtrering
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Välj filtreringstyp för dina trackers.
    Prediction förutspår rörelser medan smoothing jämnar ut rörelser.
settings-general-tracker_mechanics-filtering-type-none = Ingen filtrering
settings-general-tracker_mechanics-filtering-type-none-description = Använd rotationer som de är. Kommer inte att göra någon filtrering.
settings-general-tracker_mechanics-filtering-type-smoothing = Utjämning
settings-general-tracker_mechanics-filtering-type-smoothing-description = Utjämnar rörelser men ger viss fördröjning.
settings-general-tracker_mechanics-filtering-type-prediction = Förutsägelse
settings-general-tracker_mechanics-filtering-type-prediction-description = Minskar latensen och gör rörelserna mer snabba, men kan öka jittern.
settings-general-tracker_mechanics-save_mounting_reset = Spara automatisk montering återställning kalibrering
settings-general-tracker_mechanics-save_mounting_reset-description = Sparar de automatiska kalibreringarna för återställning av montering för trackers mellan omstarter. Användbart när du bär en dräkt där trackers inte flyttas mellan sessionerna. <b>Rekommenderas inte för normala användare!</b>
settings-general-tracker_mechanics-save_mounting_reset-enabled-label = Spara återställning av montering
settings-general-tracker_mechanics-use_mag_on_all_trackers = Använd magnetometern på alla IMU-sensorer som stödjer det
settings-general-tracker_mechanics-use_mag_on_all_trackers-description =
    Använder magnetometers på alla sensorer som har en kompatibel mjukvara till det, minskar drift i magnetiskt stabila miljöer.
    Kan stängas av för individuella spårare i dess inställningar. <b> Var vänlig och stäng inte av någon av sensorerna när du växlar av och på denna funktionen.
settings-general-tracker_mechanics-use_mag_on_all_trackers-label = använd magnetometer på sensorer.
settings-stay_aligned-description = Behåll inriktning minskar drift genom att gradvis justera dina sensorer för att matcha dina avslappnade positioner.
settings-stay_aligned-setup-label = Ställ in Behåll inriktning
settings-stay_aligned-setup-description = Du måste kompletera "Ställ in Behåll inriktnining" för att kunna aktivera Behåll inriktning.
settings-stay_aligned-enabled-label = Justera sensorer
settings-stay_aligned-general-label = Allmän
settings-stay_aligned-relaxed_poses-label = Avslappnade positioner
settings-stay_aligned-relaxed_poses-description = Behåll inriktning använder dina avslappnade positioner till att hålla dina sensorer inriktade. Använd "ställ in Behåll inriktning" för att uppdatera dessa positionerna.
settings-stay_aligned-relaxed_poses-standing = Justera sensorer medans du står
settings-stay_aligned-relaxed_poses-sitting = Justera sensorer medans du sitter i en stol
settings-stay_aligned-relaxed_poses-flat = Justera sensorer medans du sitter på golvet, eller ligger på ryggen.
settings-stay_aligned-relaxed_poses-save_pose = Spara position
settings-stay_aligned-relaxed_poses-reset_pose = Återställ position
settings-stay_aligned-relaxed_poses-close = Stäng
settings-stay_aligned-debug-label = Avbuggning
settings-stay_aligned-debug-description = Var vänlig inkludera dina inställningar när du rapporterar problem om Behåll inriktning.
settings-stay_aligned-debug-copy-label = Kopiera inställningar till urklipp

## Keybinds Page

settings-keybinds_full-reset = Fullständig återställning
settings-keybinds_yaw-reset = Återställning av gir
settings-keybinds_reset-all-button = Återställ allt
settings-keybinds-recorder-modal-cancel-button = Avbryt

## FK/Tracking settings

# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Golvklämma
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Korrigering av skridskoåkning
settings-general-fk_settings-leg_tweak-toe_snap = Snäppning av tå
settings-general-fk_settings-leg_tweak-foot_plant = Fotväxt
settings-general-fk_settings-leg_tweak-skating_correction-amount = Styrka vid korrigering av skridskoåkning
settings-general-fk_settings-leg_tweak-skating_correction-description = Skating-correction korrigerar för skridskoåkning men kan försämra precisionen i vissa rörelsemönster. När du aktiverar detta, se till att återställa och kalibrera om i spelet.
settings-general-fk_settings-leg_tweak-floor_clip-description = Floor-clip kan minska eller till och med eliminera klippning genom golvet. När du aktiverar detta, se till att återställa och kalibrera om i spelet.
settings-general-fk_settings-leg_tweak-toe_snap-description = Toe-snap försöker gissa rotationen på dina fötter om fotspårare inte används.
settings-general-fk_settings-leg_tweak-foot_plant-description = Fotplatta roterar fötterna så att de är parallella med marken vid kontakt.
settings-general-fk_settings-leg_fk = Spårning av ben
settings-general-fk_settings-enforce_joint_constraints = Skelett-gränser
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints = Upprätthåll begränsningar
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = Förhindra leder från att rotera förbi dess gränser
settings-general-fk_settings-arm_fk-back = Tillbaka
settings-general-fk_settings-arm_fk-back-description = Standardläget, där överarmarna går bakåt och underarmarna framåt.
settings-general-fk_settings-arm_fk-tpose_up = T-pose (upp)
settings-general-fk_settings-arm_fk-tpose_up-description = Förväntar sig att armarna ska vara nere på sidorna under Full Reset och 90 grader upp på sidorna under Mounting Reset.
settings-general-fk_settings-arm_fk-tpose_down = T-pose (nedåt)
settings-general-fk_settings-arm_fk-tpose_down-description = Förväntar sig att armarna ska vara 90 grader upp åt sidorna under Full Reset och nedåt på sidorna under Mounting Reset.
settings-general-fk_settings-arm_fk-forward = Framåt
settings-general-fk_settings-arm_fk-forward-description = Förväntar sig att dina armar är upp 90 grader framåt. Användbart för VTubing.
settings-general-fk_settings-skeleton_settings-ratios = Skelettets proportioner
settings-general-fk_settings-skeleton_settings-ratios-description = Ändra värdena för skelettinställningarna. Du kan behöva justera dina proportioner efter att du har ändrat dessa.
settings-general-fk_settings-self_localization-title = Mocap-läge

## Gesture control settings (tracker tapping)

settings-general-gesture_control-subtitle = Tap-baserade återställningar
settings-general-gesture_control-description = Gör att återställningar kan utlösas genom att trycka på en tracker. Den tracker som sitter högst upp på din bål används för Yaw Reset, den tracker som sitter högst upp på ditt vänstra ben används för Full Reset och den tracker som sitter högst upp på ditt högra ben används för Mounting Reset. Taps måste ske inom tidsgränsen 0,3 sekunder gånger antalet taps som ska erkännas.
settings-general-gesture_control-yawResetEnabled = Aktivera återställning av tap till yaw
settings-general-gesture_control-yawResetDelay = Fördröjning av återställning av yaw
settings-general-gesture_control-yawResetTaps = Tryck för återställning av yaw
settings-general-gesture_control-fullResetEnabled = Aktivera tryck för fullständig återställning
settings-general-gesture_control-fullResetDelay = Fördröjning vid fullständig återställning
settings-general-gesture_control-fullResetTaps = Kranar för fullständig återställning
settings-general-gesture_control-mountingResetEnabled = Aktivera tryck för att återställa montering
settings-general-gesture_control-mountingResetDelay = Fördröjning av återställning av montering
settings-general-gesture_control-mountingResetTaps = Gängtappar för montering av återställning
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = Trackers över tröskelvärdet
settings-general-gesture_control-numberTrackersOverThreshold-description = Öka detta värde om avkänningen inte fungerar. Öka inte värdet mer än vad som krävs för att detekteringen ska fungera, eftersom det kan leda till fler falska positiva resultat.

## Appearance settings

settings-interface-appearance = Utseende
settings-general-interface-dev_mode = Utvecklarläge
settings-general-interface-dev_mode-description = Det här läget kan vara användbart om du behöver djupgående data eller vill interagera med anslutna trackers på en mer avancerad nivå.
settings-general-interface-dev_mode-label = Utvecklarläge
settings-general-interface-lang = Välj språk
settings-general-interface-lang-description = Ändra det standardspråk som du vill använda.
settings-general-interface-lang-placeholder = Välj det språk du vill använda
# Keep the font name untranslated
settings-interface-appearance-font = GUI-teckensnitt
settings-interface-appearance-font-description = Detta ändrar det teckensnitt som används av gränssnittet.
settings-interface-appearance-font-placeholder = Standardteckensnitt
settings-interface-appearance-font-os_font = OS-teckensnitt
settings-interface-appearance-font-slime_font = Standardteckensnitt
settings-interface-appearance-font_size = Skalning av basteckensnitt
settings-interface-appearance-font_size-description = Detta påverkar teckenstorleken i hela gränssnittet utom i denna inställningspanel.

## Notification settings

settings-interface-notifications = Meddelanden
settings-general-interface-feedback_sound = Feedback-ljud
settings-general-interface-feedback_sound-description = Detta alternativ spelar upp ett ljud när en återställning utlöses.
settings-general-interface-feedback_sound-label = Feedback-ljud
settings-general-interface-feedback_sound-volume = Volym för återkopplingsljud
settings-general-interface-connected_trackers_warning = Varning för uppkopplade spårare
settings-general-interface-connected_trackers_warning-description = Detta alternativ kommer att visa ett popup-fönster varje gång du försöker lämna SlimeVR medan du har en eller flera anslutna trackers. Det påminner dig om att stänga av dina trackers när du är klar för att spara batteritid.
settings-general-interface-connected_trackers_warning-label = Varning för uppkopplade trackers vid utresa

## Behavior settings

settings-general-interface-dev_mode = Utvecklarläge
settings-general-interface-dev_mode-label = Utvecklarläge
settings-general-interface-use_tray = Minimera till systemfältet
settings-general-interface-use_tray-description = Låter dig stänga fönstret utan att stänga SlimeVR-servern så att du kan fortsätta använda den utan att GUI stör dig.
settings-general-interface-use_tray-label = Minimera till systemfältet
settings-general-interface-discord_presence = Dela aktivitet på Discord
settings-general-interface-discord_presence-description = Berättar för din Discord-klient att du använder SlimeVR tillsammans med antalet IMU-trackers du använder.
settings-general-interface-discord_presence-label = Dela aktivitet på Discord
settings-interface-behavior-error_tracking = Error samling via Sentry.io
settings-interface-behavior-error_tracking-description_v2 =
    <h1>Tillåter du samlingen av anonym error-data?</h1>
    
    <b>Vi samlar inte personlig information</b> så som din IP adress eller trådlösa referenser. Slimevr värdesätter din integritet!
    
    För att tillhandhålla den bästa användarupplevelsen, så samlar vi anonyma error-raporter, prestandamått och operativsystems-info. Detta hjälper oss upptäcka buggar och problem med Slimevr. Dessa rapporterna samlas via Sentry.io.
settings-interface-behavior-error_tracking-label = Skicka errors till utväcklare

## Serial settings

settings-serial-connection_lost = Anslutning till seriell enhet förlorad, återanslutning...
settings-serial-reboot = Omstart
settings-serial-factory_reset = Fabriksåterställning
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>Warning:</b> Detta återställer trackern till fabriksinställningarna.<b>
    Det innebär att Wi-Fi- och kalibreringsinställningar <b>kommer att gå förlorade!</b>
settings-serial-factory_reset-warning-ok = Jag vet vad jag gör
settings-serial-factory_reset-warning-cancel = Avbryt
settings-serial-serial_select = Välj en serieport
settings-serial-auto_dropdown_item = Automatiskt
settings-serial-save_logs = Spara till fil
settings-serial-send_command-warning-ok = Jag vet vad jag gör
settings-serial-send_command-warning-cancel = Avbryt

## OSC VRChat settings

settings-osc-vrchat-enable = Aktivera
settings-osc-vrchat-enable-description = Växla mellan sändning och mottagning av data.
settings-osc-vrchat-enable-label = Aktivera
settings-osc-vrchat-network = Nätverksportar
settings-osc-vrchat-network-port_in =
    .label = Port in
    .placeholder = Port in (default: 9001)
settings-osc-vrchat-network-port_out =
    .label = Port ut
    .placeholder = Port ut (default: 9000)
settings-osc-vrchat-network-address = Nätverksadress
settings-osc-vrchat-network-address-description-v1 = Välj vilken adress du vill skicka data till. Kan lämnas orörd för VRChat.
settings-osc-vrchat-network-address-placeholder = VRChat ip-adress

## VRChat OSC status

settings-osc-vrchat-status-tracking = Rotation
settings-osc-vrchat-status-badge-error = Fel
settings-osc-vrchat-status-badge-unknown = Okänd

## VMC OSC settings

settings-osc-vmc = Virtuell rörelseinspelning
settings-osc-vmc-enable = Aktivera
settings-osc-vmc-enable-description = Växla mellan sändning och mottagning av data.
settings-osc-vmc-enable-label = Aktivera
settings-osc-vmc-network = Nätverksportar
settings-osc-vmc-network-description = Ställ in portarna för att lyssna på och skicka data via VMC.
settings-osc-vmc-network-port_in =
    .label = Port in
    .placeholder = Port in (standard: 39540)
settings-osc-vmc-network-port_out =
    .label = Port ut
    .placeholder = Port ut (standard: 39539)
settings-osc-vmc-network-address = Nätverksadress
settings-osc-vmc-network-address-description = Välj vilken adress du vill skicka ut data till via VMC.
settings-osc-vmc-network-address-placeholder = IPV4-adress
settings-osc-vmc-vrm = VRM-modell
settings-osc-vmc-vrm-untitled_model = Namnlös modell
settings-osc-vmc-vrm-file_select = Dra och släpp en modell att använda, eller <u>bläddra</u>
settings-osc-vmc-anchor_hip = Förankring vid höfterna
settings-osc-vmc-anchor_hip-label = Förankring vid höfterna
settings-osc-vmc-mirror_tracking = Spegla spårning
settings-osc-vmc-mirror_tracking-description = Spegla spårning horisontellt.

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

