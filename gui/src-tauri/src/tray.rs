use std::{collections::HashMap, sync::Mutex};

use tauri::{
	include_image,
	menu::{Menu, MenuBuilder, MenuItemBuilder, MenuItemKind},
	tray::{MouseButton, MouseButtonState, TrayIconBuilder, TrayIconEvent},
	AppHandle, Emitter, Manager, Runtime, State,
};

pub struct TrayMenu<R: Runtime>(Menu<R>);
pub struct TrayAvailable(pub bool);

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
pub fn is_tray_available(tray_available: State<TrayAvailable>) -> bool {
	tray_available.0
}

#[tauri::command]
pub fn update_translations<R: Runtime>(
	app: AppHandle<R>,
	i18n: State<TrayTranslations>,
	menu: State<Option<TrayMenu<R>>>,
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
	menu: State<Option<TrayMenu<R>>>,
) -> color_eyre::Result<(), String> {
	let Some(menu) = menu.as_ref() else {
		return Ok(());
	};
	if let Some((window, MenuItemKind::MenuItem(toggle_i))) =
		app.get_webview_window("main").zip(menu.0.get("toggle"))
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
	#[cfg(target_os = "linux")]
	unsafe {
		const LIBS_TO_CHECK: &[&str] = &[
			"libayatana-appindicator3.so.1",
			"libappindicator3.so.1",
			"libayatana-appindicator3.so",
			"libappindicator3.so",
		];
		let found = LIBS_TO_CHECK
			.iter()
			.any(|lib| libloading::Library::new(lib).is_ok());
		if !found {
			log::warn!(
				"libappindicator couldn't be found so tray support has been disabled!"
			);
			app.manage(TrayAvailable(false));
			return Ok(());
		}
	}

	let toggle_i = MenuItemBuilder::with_id("toggle", "Hide").build(app)?;
	let quit_i = MenuItemBuilder::with_id("quit", "Quit").build(app)?;
	let menu1 = MenuBuilder::new(app).items(&[&toggle_i, &quit_i]).build()?;

	let _ = TrayIconBuilder::with_id("tray-1")
		.menu(&menu1)
		.title("SlimeVR")
		.tooltip("SlimeVR")
		.icon_as_template(true)
		.menu_on_left_click(false)
		.icon(if cfg!(target_os = "macos") {
			include_image!("icons/appleTrayIcon.png")
		} else {
			include_image!("icons/128x128.png")
		})
		.on_menu_event(move |app, event| match event.id.as_ref() {
			"quit" => app.emit("try-close", "tray").unwrap(),
			"toggle" => {
				let i18n = app.state::<TrayTranslations>();
				if let Some(window) = app.get_webview_window("main") {
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
		.on_tray_icon_event(|tray, event| match event {
			TrayIconEvent::Click {
				button,
				button_state,
				..
			} if button == MouseButton::Left
				&& button_state == MouseButtonState::Up =>
			{
				let app = tray.app_handle();
				if let Some(window) = app.get_webview_window("main") {
					let _ = window.show();
					let _ = window.set_focus();
				}
			}
			_ => {}
		})
		// We don't want this as we open the window on left click
		.menu_on_left_click(false)
		.build(app)?;

	app.manage(TrayAvailable(true));
	app.manage(Some(TrayMenu(menu1)));
	app.manage(TrayTranslations {
		store: Default::default(),
	});

	Ok(())
}
