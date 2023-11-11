use tauri::{
	menu::{Menu, MenuItem},
	tray::{ClickType, TrayIconBuilder},
	Icon, Manager, Runtime,
};

pub fn create_tray<R: Runtime>(app: &tauri::AppHandle<R>) -> tauri::Result<()> {
	let toggle_i = MenuItem::with_id(app, "toggle", "Toggle", true, None);
	let quit_i = MenuItem::with_id(app, "quit", "Quit", true, None);
	let menu1 = Menu::with_items(app, &[&toggle_i, &quit_i])?;

	let _ = TrayIconBuilder::with_id("tray-1")
		.menu(&menu1)
		.title("SlimeVR")
		.tooltip("SlimeVR")
		.icon_as_template(true)
		.menu_on_left_click((false))
		.icon(if cfg!(target_os = "macos") {
			Icon::Raw(include_bytes!("../icons/appleTrayIcon.png").to_vec())
		} else {
			Icon::Raw(include_bytes!("../icons/128x128.png").to_vec())
		})
		.on_menu_event(move |app, event| match event.id.as_ref() {
			"quit" => app.exit(0),
			"toggle" => {
				if let Some(window) = app.get_window("main") {
					let new_title = if window.is_visible().unwrap_or_default() {
						let _ = window.hide();
						"Show"
					} else {
						let _ = window.show();
						let _ = window.set_focus();
						"Hide"
					};
					toggle_i.set_text(new_title).unwrap();
				}
			}
			_ => {}
		})
		.on_tray_event(|tray, event| {
			if event.click_type == ClickType::Left {
				let app = tray.app_handle();
				if let Some(window) = app.get_window("main") {
					let _ = window.show();
					let _ = window.set_focus();
				}
			}
		})
		.build(app)?;

	// If not, menu won't work on Linux
	app.manage(menu1);

	Ok(())
}
