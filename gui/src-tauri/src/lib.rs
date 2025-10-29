use tauri::Manager;

mod cross;

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
	log_panics::init();

	tauri::Builder::default()
		.plugin(
			tauri_plugin_log::Builder::new()
				.target(tauri_plugin_log::Target::new(
					tauri_plugin_log::TargetKind::LogDir {
						file_name: Some("slimevr".to_string()),
					},
				))
				.max_file_size(30_000 /* bytes */)
				.rotation_strategy(tauri_plugin_log::RotationStrategy::KeepSome(3))
				.build(),
		)
		.plugin(tauri_plugin_opener::init())
		.plugin(tauri_plugin_dialog::init())
		.plugin(tauri_plugin_fs::init())
		.plugin(tauri_plugin_os::init())
		.plugin(tauri_plugin_shell::init())
		.plugin(tauri_plugin_store::Builder::default().build())
		.plugin(tauri_plugin_http::init())
		.invoke_handler(tauri::generate_handler![cross::is_tray_available,])
		.setup(move |app| {
			log::info!("SlimeVR started!");

			let _ = tauri::WebviewWindowBuilder::new(
				app,
				"main",
				tauri::WebviewUrl::App("index.html".into()),
			)
			.build()?;

			app.manage(cross::TrayAvailable(false));

			Ok(())
		})
		.run(tauri::generate_context!())
		.expect("error while running tauri application");
}
