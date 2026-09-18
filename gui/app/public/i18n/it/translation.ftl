# Please developers (not translators) don't reuse a key inside another key
# or concat text with a translation string in the code, use the appropriate
# features like variables and selectors in each appropriate case!
# And also comment the string if it's something not easy to translate, so you help
# translators on what it means


## Websocket (server) status

websocket-connecting = Caricamento...
websocket-connection_lost = Connessione con il server persa. Tentativo di riconnessione in corso...
websocket-connection_lost-desc = Sembra che il server SlimeVR si sia crashato. Controlla i log e riavvia il programma
websocket-timedout = Impossibile connettersi al server
websocket-timedout-desc = Sembra che il server SlimeVR si sia crashato o bloccato. Controlla i log e riavvia il programma
websocket-error-close = Chiudi SlimeVR
websocket-error-logs = Apri la cartella dei log

## Update notification

version_update-title = Nuova versione disponibile: { $version }
version_update-description = Cliccando su "{ version_update-update }" si scaricherà il programma di installazione di SlimeVR.
version_update-update = Aggiorna
version_update-close = Chiudi

## Tips

tips-find_tracker = Non sei sicurə quale tracker è quale? Agitalo e l'oggetto corrispondente sarà evidenziato.
tips-do_not_move_heels = Assicurati di non muovere i tuoi talloni durante la registrazione!
tips-file_select = Trascina qui i file da usare, o <u>sfoglia</u>.
tips-failed_webgl = Inizializzazione WebGL fallita.

## Units


## Dropdown


## Text input


## File input


## Window controls

titlebar-close = Chiudi

## Body parts

body_part-NONE = Non assegnato
body_part-HEAD = Testa
body_part-NECK = Collo
body_part-RIGHT_SHOULDER = Spalla destra
body_part-RIGHT_UPPER_ARM = Parte superiore del braccio destro
body_part-RIGHT_LOWER_ARM = Avambraccio destro
body_part-RIGHT_HAND = Mano destra
body_part-RIGHT_UPPER_LEG = Coscia destra
body_part-RIGHT_LOWER_LEG = Caviglia destra
body_part-RIGHT_FOOT = Piede destro
body_part-UPPER_CHEST = Parte superiore del petto
body_part-HIP = Bacino
body_part-LEFT_SHOULDER = Spalla sinistra
body_part-LEFT_UPPER_ARM = Parte superiore del braccio sinistro
body_part-LEFT_LOWER_ARM = Avambraccio sinistro
body_part-LEFT_HAND = Mano sinistra
body_part-LEFT_UPPER_LEG = Coscia sinistra
body_part-LEFT_LOWER_LEG = Caviglia sinistra
body_part-LEFT_FOOT = Piede sinistro
body_part-LEFT_THUMB_METACARPAL = Metacarpo del pollice sinistro
body_part-LEFT_THUMB_PROXIMAL = Falange prossimale del pollice sinistro
body_part-LEFT_THUMB_DISTAL = Falange distale del pollice sinistro
body_part-LEFT_INDEX_PROXIMAL = Falange prossimale dell'indice sinistro
body_part-LEFT_INDEX_INTERMEDIATE = Falange intermedia dell'indice sinistro
body_part-LEFT_INDEX_DISTAL = Falange distale dell'indice sinistro
body_part-LEFT_MIDDLE_PROXIMAL = Falange prossimale del medio sinistro
body_part-LEFT_MIDDLE_INTERMEDIATE = Falange intermedia del medio sinistro
body_part-LEFT_MIDDLE_DISTAL = Falange distale del medio sinistro
body_part-LEFT_RING_PROXIMAL = Falange prossimale dell'anulare sinistro
body_part-LEFT_RING_INTERMEDIATE = Falange intermedia dell'anulare sinistro
body_part-LEFT_RING_DISTAL = Falange distale dell'anulare sinistro
body_part-LEFT_LITTLE_PROXIMAL = Falange prossimale del mignolo sinistro
body_part-LEFT_LITTLE_INTERMEDIATE = Falange intermedia del mignolo sinistro
body_part-LEFT_LITTLE_DISTAL = Falange distale del mignolo sinistro
body_part-RIGHT_THUMB_METACARPAL = Metacarpo del pollice destro
body_part-RIGHT_THUMB_PROXIMAL = Falange prossimale del pollice destro
body_part-RIGHT_THUMB_DISTAL = Falange distale del pollice destro
body_part-RIGHT_INDEX_PROXIMAL = Falange prossimale dell'indice destro
body_part-RIGHT_INDEX_INTERMEDIATE = Falange intermedia dell'indice destro
body_part-RIGHT_INDEX_DISTAL = Falange distale dell'indice destro
body_part-RIGHT_MIDDLE_PROXIMAL = Falange prossimale del medio destro
body_part-RIGHT_MIDDLE_INTERMEDIATE = Falange intermedia del medio destro
body_part-RIGHT_MIDDLE_DISTAL = Falange distale del medio destro
body_part-RIGHT_RING_PROXIMAL = Falange prossimale dell'anulare destro
body_part-RIGHT_RING_INTERMEDIATE = Falange intermedia dell'anulare destro
body_part-RIGHT_RING_DISTAL = Falange distale dell'anulare destro
body_part-RIGHT_LITTLE_PROXIMAL = Falange prossimale del mignolo destro
body_part-RIGHT_LITTLE_INTERMEDIATE = Falange intermedia del mignolo destro
body_part-RIGHT_LITTLE_DISTAL = Falange distale del mignolo destro

## BoardType

board_type-UNKNOWN = Sconosciuto
board_type-CUSTOM = Scheda Personalizzata
board_type-GLOVE_IMU_SLIMEVR_DEV = Guanto SlimeVR Dev IMU

## Proportions

skeleton_bone-NONE = Nessuna selezione
skeleton_bone-HEAD = Correzione per la testa
skeleton_bone-HEAD-desc =
    La distanza tra il visore e il centro della testa.
    Scuoti la testa da sinistra a destra come se si fosse in disaccordo
    e modificala fino a quando il movimento degli altri tracker è trascurabile.
skeleton_bone-NECK = Lunghezza del collo
skeleton_bone-NECK-desc =
    La distanza tra il centro della testa e la base del collo.
    Muovere la testa verso l'alto e verso il basso come se si stesse annuendo o inclinare la testa a sinistra e a destra, modificala fino a quando il movimento degli altri tracker è trascurabile.
skeleton_bone-torso_group = Lunghezza del torso
skeleton_bone-torso_group-desc =
    La distanza dalla base del collo ai fianchi.
    Modificala stando in piedi finché i fianchi virtuali non si allineano a quelli reali.
skeleton_bone-UPPER_CHEST = Lunghezza della parte superiore del petto
skeleton_bone-UPPER_CHEST-desc =
    La distanza tra la base del collo e il centro del torace.
    Regola correttamente la lunghezza del busto e modificala in varie posizioni
    (seduti, piegati, sdraiati, ecc.) finché la colonna vertebrale virtuale non corrisponde a quella reale.
skeleton_bone-LOWER_CHEST-desc =
    La distanza tra il centro del petto e il centro della colonna vertebrale.
    Regola correttamente la lunghezza del busto e modificala in varie posizioni
    (seduti, piegati, sdraiati, ecc.) finché la colonna vertebrale virtuale non corrisponde a quella reale.
skeleton_bone-HIP = Lunghezza del bacino
skeleton_bone-HIPS_WIDTH = Larghezza del bacino
skeleton_bone-HIPS_WIDTH-desc =
    La distanza tra l'inizio delle gambe.
    Esegui un reset completo con le gambe dritte e modificala finché
    le gambe virtuali non coincidono con quelle reali in orizzontale.
skeleton_bone-leg_group = Lunghezza della gamba
skeleton_bone-leg_group-desc =
    La distanza tra i fianchi e i piedi.
    Regola correttamente la lunghezza del busto e modificala
    finché i piedi virtuali non si trovano allo stesso livello di quelli reali.
skeleton_bone-UPPER_LEG = Lunghezza della parte superiore della gamba
skeleton_bone-UPPER_LEG-desc =
    La distanza tra i fianchi e le ginocchia.
    Regola adeguatamente la lunghezza delle gambe e modificala
    finché le ginocchia virtuali non sono allo stesso livello di quelle reali.
skeleton_bone-LOWER_LEG = Lunghezza della parte inferiore della gamba
skeleton_bone-LOWER_LEG-desc =
    La distanza tra le ginocchia e le caviglie.
    Regola correttamente la lunghezza delle gambe e modificala
    fino a quando le ginocchia virtuali sono allo stesso livello di quelle reali.
skeleton_bone-FOOT_LENGTH = Lunghezza dei piedi
skeleton_bone-FOOT_LENGTH-desc =
    La distanza tra le caviglie e le dita dei piedi.
    Cammina in punta di piedi e modificala finché i piedi virtuali non rimangono in posizione.
skeleton_bone-FOOT_SHIFT = Correzione per i piedi
skeleton_bone-FOOT_SHIFT-desc =
    Questo valore è la distanza orizzontale dal ginocchio alla caviglia.
    Tiene conto del fatto che la parte inferiore delle gambe va all'indietro quando si sta in piedi.
    Per regolarla, impostare la lunghezza dei piedi su 0, eseguire un reset completo e modificarla
    finché i piedi virtuali di non si allineano al centro delle caviglie.
skeleton_bone-SHOULDERS_DISTANCE = Distanza delle spalle
skeleton_bone-SHOULDERS_DISTANCE-desc =
    La distanza verticale dalla base del collo alle spalle.
    Imposta Lunghezza braccio superiore su 0 e modificala finché i tracker dei gomiti virtuali
    non si allineano verticalmente con le spalle reali.
skeleton_bone-SHOULDERS_WIDTH = Larghezza delle spalle
skeleton_bone-SHOULDERS_WIDTH-desc =
    La distanza orizzontale dalla base del collo alle spalle.
    Imposta Lunghezza braccio superiore su 0 e modificala finché i tracker dei gomiti virtuali
    non si allineano orizzontalmente con le spalle reali.
skeleton_bone-arm_group = Lunghezza delle braccia
skeleton_bone-arm_group-desc =
    La distanza tra le spalle e i polsi.
    Regola correttamente la distanza delle spalle, imposta la distanza delle mani Y
    su 0 e modificala finché i tracker delle mani non si allineano ai polsi.
skeleton_bone-UPPER_ARM = Lunghezza braccio superiore
skeleton_bone-UPPER_ARM-desc =
    La distanza tra le spalle e i gomiti.
    Regola correttamente la Lunghezza delle braccia e modificala finché
    i tracker dei gomiti non si allineano con i gomiti reali.
skeleton_bone-LOWER_ARM = Lunghezza degli avambracci
skeleton_bone-LOWER_ARM-desc =
    La distanza tra i gomiti e i polsi.
    Regola correttamente la Lunghezza del braccio e modificala finché
    i tracker dei gomiti non si allineano con i gomiti reali.
skeleton_bone-HAND_Y = Distanza della mano sull'asse Y
skeleton_bone-HAND_Y-desc =
    La distanza verticale tra i polsi e il centro della mano.
    Per regolarla per il Motion Capture, regolare correttamente la lunghezza del braccio e modificarla finché i tracker delle mani di non si allineano verticalmente con il centro delle mani.
    Per regolarla per il tracciamento dei gomiti dai controller, impostare Lunghezza delle Braccia su 0 e
    modificarla finché i tracker dei gomiti non si allineano verticalmente con i polsi.
skeleton_bone-HAND_Z = Distanza della mano sull'asse Z
skeleton_bone-HAND_Z-desc =
    La distanza orizzontale tra i polsi e il centro della mano.
    Per regolarla per il Motion Capture, impostatela su 0.
    Per regolarla per il tracciamento dei gomiti dai controller, impostate Lunghezza delle braccia su 0 e
    modificarla finché i tracker dei gomiti non si allineano orizzontalmente con i polsi.

## Tracker reset buttons

reset-reset_all = Ripristina tutte le proporzioni
reset-reset_all_warning-reset = Ripristino delle proporzioni
reset-reset_all_warning-cancel = Annulla
reset-full = Ripristino completo
reset-mounting = Controlla il posizionamento dei tuoi tracker
reset-yaw = Ripristino dell'orientamento

## Navigation bar

navbar-body_proportions = Proporzioni del corpo
navbar-trackers_assign = Assegnazione dei tracker
navbar-mounting = Controlla il posizionamento dei tuoi tracker
navbar-onboarding = Installazione guidata
navbar-settings = Impostazioni

## Biovision hierarchy recording

bvh-start_recording = Registra BVH
bvh-recording = Registrazione in corso...

## Tracking pause

tracking-unpaused = Pausa il Tracciamento
tracking-paused = Riprendi il tracciamento

## Widget: Developer settings

widget-developer_mode = Modalità sviluppatore
widget-developer_mode-high_contrast = Contrasto alto
widget-developer_mode-precise_rotation = Rotazione precisa
widget-developer_mode-fast_data_feed = Trasmissione veloce dei dati
widget-developer_mode-raw_slime_rotation = Non processato

## Widget: IMU Visualizer

widget-imu_visualizer = Dati di tracciamento
widget-imu_visualizer-preview = Anteprima
widget-imu_visualizer-hide = Nascondi
widget-imu_visualizer-rotation_raw = Non processato
widget-imu_visualizer-rotation_preview = Anteprima
widget-imu_visualizer-acceleration = Accelerazione
widget-imu_visualizer-position = Posizione
widget-imu_visualizer-stay_aligned = Rimani Allineato

## Tracker status

tracker-status-none = Nessuno stato
tracker-status-busy = Occupato
tracker-status-error = Errore
tracker-status-disconnected = Disconnesso
tracker-status-occluded = Ostruito
tracker-status-timed_out = Tempo scaduto

## Tracker status columns

tracker-table-column-name = Nome
tracker-table-column-type = Tipologia
tracker-table-column-battery = Batteria
tracker-table-column-temperature = Temperatura °C
tracker-table-column-linear-acceleration = Accellerazione X/Y/Z
tracker-table-column-rotation = Rotazione X/Y/Z
tracker-table-column-position = Rotazione X/Y/Z
tracker-table-column-stay_aligned = Rimani Allineato

## Tracker rotation

tracker-rotation-front = Davanti
tracker-rotation-front_left = Anteriore sinistra
tracker-rotation-front_right = Anteriore destra
tracker-rotation-left = Sinistra
tracker-rotation-right = Destra
tracker-rotation-back = Indietro
tracker-rotation-back_left = Posteriore sinistra
tracker-rotation-back_right = Posteriore destra
tracker-rotation-custom = Personalizzata

## Tracker information

tracker-infos-manufacturer = Produttore
tracker-infos-display_name = Nome da visualizzare
tracker-infos-custom_name = Nome personalizzato
tracker-infos-url = URL del tracker
tracker-infos-imu = Sensore IMU
tracker-infos-board_type = Scheda principale
tracker-infos-network_version = Versione del protocollo
tracker-infos-magnetometer = Magnetometro
tracker-infos-magnetometer-status-v1 =
    { $status ->
        [DISABLED] Disattivato
        [ENABLED] Attivato
       *[NOT_SUPPORTED] Non supportato
    }

## Tracker settings

tracker-settings-back = Torna alla lista dei tracker
tracker-settings-title = Impostazioni del tracker
tracker-settings-assignment_section = Assegnazione
tracker-settings-assignment_section-description = Definisce a che parte del corpo è assegnato il tracker.
tracker-settings-assignment_section-edit = Modifica assegnazione
tracker-settings-mounting_section = Orientamento del posizionamento
tracker-settings-mounting_section-description = Dove è posizionato il tracker?
tracker-settings-mounting_section-edit = Modifica posizionamento
tracker-settings-use_mag = Consenti il magnetometro su questo tracker
# Multiline!
tracker-settings-use_mag-description =
    Vuoi consentire al tracker l'utilizzo del magnetometro per ridurre il drift quando l'uso del magnetometro è consentito? <b>Per favore non spegnere il tracker durante l'attivazione!</b>
    
    È necessario prima consentire l'utilizzo del magnetometro, <magSetting>fare clic qui per accedere alle impostazioni</magSetting>.
tracker-settings-use_mag-label = Consenti magnetometro
# The .<name> means it's an attribute and it's related to the top key.
# In this case that is the settings for the assignment section.
tracker-settings-name_section = Nome del tracker
tracker-settings-name_section-placeholder = Gamba destra di NightyQueer
tracker-settings-name_section-label = Nome del tracker
tracker-settings-forget = Dimentica il tracker
tracker-settings-forget-description = Rimuove il tracker dal SlimeVR server e impedisce che si riconnetta ad fino al riavvio del server. Le impostazioni del tracker non andranno perse.
tracker-settings-forget-label = Dimentica il tracker
tracker-settings-update-low-battery = Non è possibile aggiornare. Batteria inferiore al 50%
tracker-settings-update-up_to_date = Aggiornata
tracker-settings-update = Aggiorna
tracker-settings-update-title = Versione firmware

## Dongle settings

dongle-infos-hardware_revision = Versione hardware
dongle-status-disconnected = Disconnesso
dongle-settings-back = Torna alla lista dei tracker
dongle-settings-update = Aggiorna
dongle-settings-update-title = Versione firmware

## Tracker part card info

tracker-part_card-unassigned = Non assegnato

## Body assignment menu

body_assignment_menu = Dove vuoi che sia posizionato questo tracker?
body_assignment_menu-description = Scegli una parte del corpo a cui assegnare questo tracker. Alternativamente puoi scegliere di gestire tutti i tracker in una schermata unica invece che singolarmente.
body_assignment_menu-manage_trackers = Gestisci tutti i tracker
body_assignment_menu-unassign_tracker = Rimuovi assegnazione del tracker

## Tracker assignment menu

# A -translation_key (with a dash in the front) means that it's a label.
# It can only be used in the translation file, it's nice for reusing names and that kind of stuff.
#
# We are using it here because english doesn't require changing the text in each case but
# maybe your language does.
# This line cares about multilines.
# <b>text</b> means that the text should be bold.
tracker_selection_menu-neck_warning =
    <b>Attenzione:</b> Un tracker per il collo può essere mortale se indossato troppo stretto,
    Il cinturino potrebbe bloccare la circolazione alla tua testa!
tracker_selection_menu-neck_warning-done = Comprendo i rischi
tracker_selection_menu-neck_warning-cancel = Annulla

## Mounting menu

mounting_selection_menu-close = Chiudi

## Sidebar settings

settings-sidebar-title = Impostazioni
settings-sidebar-general = Generale
settings-sidebar-stay_aligned = Rimani Allineato
settings-sidebar-trackers = Tracker
settings-sidebar-interface = Interfaccia
settings-sidebar-utils = Strumenti
settings-sidebar-appearance = Aspetto
settings-sidebar-notifications = Notifiche
settings-sidebar-firmware-tool = Strumento firmware fai-da-te
settings-sidebar-vrc_warnings = Avvertimenti per le impostazioni di VRChat
settings-sidebar-advanced = Avanzato

## Bone routing settings

settings-routing-output-badge-off = Spento
settings-routing-hands-warning-cancel = Annulla

## SteamVR / Monado output settings

settings-driver-enable = Attiva
settings-driver-status-badge-disabled = Spento

## Tracker mechanics

settings-general-tracker_mechanics-filtering = Filtro movimenti
# This also cares about multilines
settings-general-tracker_mechanics-filtering-description =
    Scegli la tipologia di filtro per il tuo tracker.
    Predizione anticipa i movimenti, mentre Attenuazione attenua movimenti eccessivi.
settings-general-tracker_mechanics-filtering-type-none = Non usare alcun filtro
settings-general-tracker_mechanics-filtering-type-none-description = Usa le rotazioni così come sono. Non eseguirà alcun filtro.
settings-general-tracker_mechanics-filtering-type-smoothing = Attenuazione
settings-general-tracker_mechanics-filtering-type-smoothing-description = Attenua movimenti eccessivi ma aggiunge ritardo.
settings-general-tracker_mechanics-filtering-type-prediction = Predizione
settings-general-tracker_mechanics-filtering-type-prediction-description = Riduce ritardo e rende movimenti più istantanei, ma può introdurre tremolio.
settings-general-tracker_mechanics-save_mounting_reset = Salva automaticamente la calibrazione del ripristino del posizionamento
settings-general-tracker_mechanics-save_mounting_reset-description =
    Salva in automatico la calibrazione del ripristino del posizionamento per i tracker tra un riavvio e l'altro. Utile
    quando si indossa una tuta in cui la posizione e orientamento dei tracker non cambia tra una sessione e l'altra. <b>Non consigliato per gli utenti normali!</b>
settings-general-tracker_mechanics-save_mounting_reset-enabled-label = Salva il ripristino del posizionamento
settings-general-tracker_mechanics-use_mag_on_all_trackers = Utilizza il magnetometro su tutti i tracker IMU che lo supportano
settings-general-tracker_mechanics-use_mag_on_all_trackers-description =
    Utilizza il magnetometro su tutti i tracker che dispongono di un firmware compatibile, riducendo il drift in ambienti magnetici stabili.
    Può essere disabilitato per ogni tracker nelle impostazioni del tracker. <b>Per favore non spegnere nessuno dei tracker durante l'attivazione!</b>
settings-general-tracker_mechanics-use_mag_on_all_trackers-label = Usa il magnetometro sui tracker
settings-stay_aligned-description = Rimani Allineato riduce la deriva regolando gradualmente i tracker per allinearsi alle tue pose rilassate.
settings-stay_aligned-setup-label = Configura Rimani Allineato
settings-stay_aligned-setup-description = Devi completare "Configura Rimani Allineato" per attivare Rimani Allineato.
settings-stay_aligned-enabled-label = Regola i tracker
settings-stay_aligned-general-label = Generale
settings-stay_aligned-relaxed_poses-label = Pose Rilassate
settings-stay_aligned-relaxed_poses-description = Rimani Allineato utilizza le tue pose rilassate per mantenere allineati i tracker. Usa "Configura Rimani Allineato" per aggiornare queste pose.
settings-stay_aligned-relaxed_poses-standing = Regola i tracker stando in piedi
settings-stay_aligned-relaxed_poses-sitting = Regola i tracker mentre sei seduto su una sedia
settings-stay_aligned-relaxed_poses-flat = Regola i tracker mentre sei seduto a terra o sdraiato sulla schiena
settings-stay_aligned-relaxed_poses-save_pose = Salva posa
settings-stay_aligned-relaxed_poses-reset_pose = Ripristina posa
settings-stay_aligned-relaxed_poses-close = Chiudi
settings-stay_aligned-debug-label = Debug
settings-stay_aligned-debug-description = Includi le tue impostazioni quando segnali problemi relativi a Rimani Allineato.
settings-stay_aligned-debug-copy-label = Copia le impostazioni negli appunti

## Keybinds Page

settings-keybinds_full-reset = Ripristino completo
settings-keybinds_yaw-reset = Ripristino dell'orientamento
settings-keybinds_reset-all-button = Ripristina tutto
settings-keybinds-recorder-modal-done-button = Fatto
settings-keybinds-recorder-modal-cancel-button = Annulla

## FK/Tracking settings

# Floor clip:
# why the name - came from the idea of noclip in video games, but is the opposite where clipping to the floor is a desired feature
# definition - Prevents the foot trackers from going lower than they where when a reset was performed
settings-general-fk_settings-leg_tweak-floor_clip = Compenetrazione pavimento
# Skating correction:
# why the name - without this enabled the feet will often slide across the ground as if your skating across the ground,
# since this largely prevents this it corrects for it hence skating correction (note this may be renamed to sliding correction)
# definition - Guesses when each foot is in contact with the ground and uses that information to improve tracking
settings-general-fk_settings-leg_tweak-skating_correction = Correzione slittamento
settings-general-fk_settings-leg_tweak-toe_snap = Dita dei piedi piantate
settings-general-fk_settings-leg_tweak-foot_plant = Piedi piantati
settings-general-fk_settings-leg_tweak-skating_correction-amount = Forza fattore di correzione slittamento
settings-general-fk_settings-leg_tweak-skating_correction-description = "Correzione slittamento" corregge l'effetto che fa sembrare che pattini sul ghiaccio, ma può peggiorare la precisione di certi movimenti. Quando abiliti questa funzione é necessario eseguire un ripristino completo e ri-calibrazione in gioco per il corretto funzionamento.
settings-general-fk_settings-leg_tweak-floor_clip-description = "Compenetrazione pavimento" può ridurre o anche eliminare completamente la compenetrazione con il pavimento. Quando abiliti questa funzione é necessario eseguire un ripristino completo e ri-calibrazione in gioco per il corretto funzionamento.
settings-general-fk_settings-leg_tweak-toe_snap-description = "Dita dei piedi piantate" prova ad indovinare la rotazione dei tuoi piedi quando non stai usando dei tracker per i piedi.
settings-general-fk_settings-leg_tweak-foot_plant-description = "Piedi piantati" ruota i piedi in modo tale che siano paralleli al terreno quando in contatto con esso.
settings-general-fk_settings-leg_fk = Tracciamento delle gambe
settings-general-fk_settings-enforce_joint_constraints = Limiti dello scheletro
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints = Rispetta i vincoli
settings-general-fk_settings-enforce_joint_constraints-enforce_constraints-description = Impedisci ai legamenti di ruotare oltre il loro limite
settings-general-fk_settings-arm_fk-back = Indietro
settings-general-fk_settings-arm_fk-back-description = La modalità predefinita, con la parte superiori delle braccia che vanno indietro e le parte inferiori delle braccia che vanno avanti.
settings-general-fk_settings-arm_fk-tpose_up = Posa a T (in alto)
settings-general-fk_settings-arm_fk-tpose_up-description = Si aspetta che le braccia siano abbassate sui lati durante il Ripristino Completo e a 90 gradi con il busto ai lati per il Ripristino Posizionamento.
settings-general-fk_settings-arm_fk-tpose_down = Posa a T (in basso)
settings-general-fk_settings-arm_fk-tpose_down-description = Si aspetta che le braccia siano a 90 gradi con il busto ai lati durante il Ripristino Completo e abbassate sui lati per il Ripristino Posizionamento.
settings-general-fk_settings-arm_fk-forward = Avanti
settings-general-fk_settings-arm_fk-forward-description = Si aspetta che le tue braccia siano alzate di 90 gradi in avanti. Utile per VTubing.
settings-general-fk_settings-skeleton_settings-ratios = Proporzioni dello scheletro
settings-general-fk_settings-skeleton_settings-ratios-description = Modifica i valori delle impostazioni dello scheletro. Potrebbe essere necessario regolare le proporzioni dopo aver modificato queste impostazioni.
settings-general-fk_settings-self_localization-title = Modalità Mocap

## Gesture control settings (tracker tapping)

settings-general-gesture_control-subtitle = Ripristino toccando un tracker
settings-general-gesture_control-description = Abilita o disabilita il ripristino toccando un tracker. Il tracker più in alto sul torso viene utilizzato per il ripristino dell'orientamento, il tracker più in alto sulla gamba sinistra viene utilizzato per il ripristino completo e il tracker più in alto sulla gamba destra viene utilizzato per il ripristino del posizionamento. Si deve tener presente che i tocchi devono avvenire entro 0,6 secondi per essere registrati.
# This is a unit: 3 taps, 2 taps, 1 tap
# $amount (Number) - Amount of taps (touches to the tracker's case)
settings-general-gesture_control-taps =
    { $amount ->
        [one] 1 tocco
       *[other] { $amount } tocchi
    }
# This is a unit: 3 trackers, 2 trackers, 1 tracker
# $amount (Number) - Amount of trackers
settings-general-gesture_control-trackers =
    { $amount ->
        [one] 1 tracker
        [many] { $amount } tracker
       *[other] { $amount } tracker
    }
settings-general-gesture_control-yawResetEnabled = Abilita tocco per il ripristino dell'orientamento
settings-general-gesture_control-yawResetDelay = Ritardo per il ripristino dell'orientamento
settings-general-gesture_control-yawResetTaps = Numero di tocchi per il ripristino dell'orientamento
settings-general-gesture_control-fullResetEnabled = Abilita tocco per il ripristino completo
settings-general-gesture_control-fullResetDelay = Ritardo per il ripristino completo
settings-general-gesture_control-fullResetTaps = Numero di tocchi per il ripristino completo
settings-general-gesture_control-mountingResetEnabled = Abilita tocco per ripristino del posizionamento
settings-general-gesture_control-mountingResetDelay = Ritardo per il ripristino del posizionamento
settings-general-gesture_control-mountingResetTaps = Numero di tocchi per il ripristino del posizionamento
# The number of trackers that can have higher acceleration before a tap is rejected
settings-general-gesture_control-numberTrackersOverThreshold = Tracker sopra il limite
settings-general-gesture_control-numberTrackersOverThreshold-description = Aumenta questo valore se il rilevamento del tocco non funziona. Non aumentarlo al di sopra di quanto necessario per far funzionare il rilevamento del tocco in quanto causerebbe più falsi positivi.

## Appearance settings

settings-interface-appearance = Aspetto
settings-general-interface-dev_mode = Modalità sviluppatore
settings-general-interface-dev_mode-description = Questa modalità è utile se hai bisogno di dati approfonditi o devi interagire in maniera più avanzata con i tracker connessi.
settings-general-interface-dev_mode-label = Modalità sviluppatore
settings-general-interface-lang = Seleziona la lingua
settings-general-interface-lang-description = Seleziona la lingua che vuoi utilizzare
settings-general-interface-lang-placeholder = Seleziona la lingua da utilizzare
# Keep the font name untranslated
settings-interface-appearance-font = Font della GUI
settings-interface-appearance-font-description = Questo cambia il font utilizzato dall'interfaccia.
settings-interface-appearance-font-placeholder = Font predefinito
settings-interface-appearance-font-os_font = Font di sistema
settings-interface-appearance-font-slime_font = Font predefinito
settings-interface-appearance-font_size = Ridimensionamento del font di base
settings-interface-appearance-font_size-description = Questo influisce sulla dimensione del font dell'intera interfaccia ad eccezione di questo pannello delle impostazioni.

## Notification settings

settings-interface-notifications = Notifiche
settings-general-interface-feedback_sound = Suono di feedback
settings-general-interface-feedback_sound-description = Questa opzione riprodurrà un suono quando viene effettuato un ripristino
settings-general-interface-feedback_sound-label = Suono di feedback
settings-general-interface-feedback_sound-volume = Volume del suono di feedback
settings-general-interface-connected_trackers_warning = Avviso di tracker connessi
settings-general-interface-connected_trackers_warning-description = Questa opzione mostrerà un pop-up ogni volta che proverai ad uscire da SmileVR mentre uno o più tracker sono connessi. Ció ti permetterà di ricordarti di spegnere i tuoi tracker per preservarne la durata delle batterie.
settings-general-interface-connected_trackers_warning-label = Avviso di tracker connessi alla chiusura dell'applicazione

## Behavior settings

settings-general-interface-dev_mode = Modalità sviluppatore
settings-general-interface-dev_mode-label = Modalità sviluppatore
settings-general-interface-use_tray = Riduci a icona nella barra delle applicazioni
settings-general-interface-use_tray-description = Ti consente di chiudere la finestra senza chiudere il server SlimeVR in modo da poter continuare a usarlo senza che la GUI ti infastidisca.
settings-general-interface-use_tray-label = Riduci a icona nella barra delle applicazioni
settings-general-interface-discord_presence = Condividi attività su Discord
settings-general-interface-discord_presence-description = Dice al tuo client di Discord che stai utilizzando SlimeVR insieme al numero di tracker IMU che stai utilizzando.
settings-general-interface-discord_presence-label = Condividi attività su Discord
settings-general-interface-discord_presence-message =
    { $amount ->
        [0] Slimeggiando qua e là
        [one] Sta usando 1 tracker
       *[other] Sta usando { $amount } tracker
    }
settings-interface-behavior-error_tracking = Raccolta degli errori tramite Sentry.io
settings-interface-behavior-error_tracking-description_v2 =
    <h1>Acconsenti alla raccolta di dati di errore anonimizzati?</h1>
    
    <b>Non raccogliamo informazioni personali</b> come l'indirizzo IP o le credenziali wireless. SlimeVR ha a cuore la tua privacy!
    
    Per offrire la migliore esperienza utente, raccogliamo segnalazioni di errori anonime, metriche delle prestazioni e informazioni sul sistema operativo. Questo ci aiuta a rilevare bug e problemi con SlimeVR. Queste metriche vengono raccolte tramite Sentry.io.
settings-interface-behavior-error_tracking-label = Invia errori agli sviluppatori

## Serial settings

settings-serial-connection_lost = Connessione seriale persa. Riconnessione in corso...
settings-serial-reboot = Riavvia
settings-serial-factory_reset = Ripristino delle impostazioni di fabbrica
# This cares about multilines
# <b>text</b> means that the text should be bold
settings-serial-factory_reset-warning =
    <b>Attenzione:</b> Questa azione ripristinerà il tracker alle impostazioni di fabbrica.
    Ciò significa che le impostazioni Wi-Fi e di calibrazione <b>andranno tutte perse!</b>
settings-serial-factory_reset-warning-ok = Capisco cosa sto facendo
settings-serial-factory_reset-warning-cancel = Annulla
settings-serial-serial_select = Seleziona una porta seriale
settings-serial-auto_dropdown_item = Automatico
settings-serial-save_logs = Salva su file
settings-serial-send_command-warning-ok = Capisco cosa sto facendo
settings-serial-send_command-warning-cancel = Annulla

## OSC VRChat settings

settings-osc-vrchat-enable = Attiva
settings-osc-vrchat-enable-description = Attiva o disattiva l'invio e la ricezione dei dati
settings-osc-vrchat-enable-label = Attiva
settings-osc-vrchat-network = Porte di rete
settings-osc-vrchat-network-port_in =
    .label = Porta in ingresso
    .placeholder = Porta in ingresso (predefinito: 9002)
settings-osc-vrchat-network-port_out =
    .label = Porta in uscita
    .placeholder = Porta in uscita (predefinito: 9000)
settings-osc-vrchat-network-address = Indirizzo di rete
settings-osc-vrchat-network-address-description-v1 = Scegli a quale indirizzo inviare i dati. Può essere lasciato come predefinito per utilizzo con VRChat.
settings-osc-vrchat-network-address-placeholder = Indirizzo IP di VRChat

## VRChat OSC status

settings-osc-vrchat-status-tracking = Dati di tracciamento
settings-osc-vrchat-status-badge-error = Errore
settings-osc-vrchat-status-badge-unknown = Sconosciuto

## VMC OSC settings


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

