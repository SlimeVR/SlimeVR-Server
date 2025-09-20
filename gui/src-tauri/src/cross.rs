pub struct TrayAvailable(pub bool);

#[tauri::command]
pub fn is_tray_available(tray_available: tauri::State<TrayAvailable>) -> bool {
	tray_available.0
}
