use std::{collections::HashMap, sync::Mutex};

use tauri::{
	menu::{Menu, MenuItem, MenuItemKind},
	tray::{ClickType, TrayIconBuilder},
	AppHandle, Icon, Manager, Runtime, State,
};

pub struct TrayMenu<R: Runtime>(Menu<R>);

pub struct TrayTranslations {
	store: Mutex<HashMap<String, String>>,
}

impl TrayTranslations {
	fn get(&self, key: &str) -> String {
		let lock = self.store.lock().unwrap();
		lock.get(key)
			.map_or_else(|| key.to_string(), |v| v.to_string())
	}
}

#[tauri::command]
pub fn update_translations<R: Runtime>(
	app: AppHandle<R>,
	i18n: State<TrayTranslations>,
	menu: State<TrayMenu<R>>,
	new_i18n: HashMap<String, String>,
) -> color_eyre::Result<(), String> {
	{
		let mut lock = i18n.store.lock().map_err(|e| e.to_string())?;
		for (new_key, new_value) in new_i18n {
			lock.insert(new_key, new_value);
		}
	}

	update_tray_text(app, i18n, menu)?;

	Ok(())
}

#[tauri::command]
pub fn update_tray_text<R: Runtime>(
	app: AppHandle<R>,
	i18n: State<TrayTranslations>,
	menu: State<TrayMenu<R>>,
) -> color_eyre::Result<(), String> {
	if let Some((window, MenuItemKind::MenuItem(toggle_i))) =
		app.get_window("main").zip(menu.0.get("toggle"))
	{
		let new_title = if window.is_visible().unwrap_or_default() {
			i18n.get("tray_menu-hide")
		} else {
			i18n.get("tray_menu-show")
		};
		toggle_i.set_text(new_title).map_err(|e| e.to_string())?;
	}

	if let Some(MenuItemKind::MenuItem(quit_i)) = menu.0.get("quit") {
		quit_i
			.set_text(i18n.get("tray_menu-quit"))
			.map_err(|e| e.to_string())?;
	}

	Ok(())
}

pub fn create_tray<R: Runtime>(app: &tauri::AppHandle<R>) -> tauri::Result<()> {
	let toggle_i = MenuItem::with_id(app, "toggle", "Hide", true, None);
	let quit_i = MenuItem::with_id(app, "quit", "Quit", true, None);
	let menu1 = Menu::with_items(app, &[&toggle_i, &quit_i])?;

	let _ = TrayIconBuilder::with_id("tray-1")
		.menu(&menu1)
		.title("SlimeVR")
		.tooltip("SlimeVR")
		.icon_as_template(true)
		.menu_on_left_click(false)
		.icon(if cfg!(target_os = "macos") {
			Icon::Raw(include_bytes!("../icons/appleTrayIcon.png").to_vec())
		} else {
			Icon::Raw(include_bytes!("../icons/128x128.png").to_vec())
		})
		.on_menu_event(move |app, event| match event.id.as_ref() {
			"quit" => app.emit_all("try-close", "tray").unwrap(),
			"toggle" => {
				let i18n = app.state::<TrayTranslations>();
				if let Some(window) = app.get_window("main") {
					let new_title = if window.is_visible().unwrap_or_default() {
						let _ = window.hide();
						i18n.get("tray_menu-show")
					} else {
						let _ = window.show();
						let _ = window.set_focus();
						i18n.get("tray_menu-hide")
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

	app.manage(TrayMenu(menu1));
	app.manage(TrayTranslations {
		store: Default::default(),
	});

	Ok(())
}
