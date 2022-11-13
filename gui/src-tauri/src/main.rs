#![cfg_attr(
    all(not(debug_assertions), target_os = "windows"),
    windows_subsystem = "windows"
)]

use clap::Parser;
use clap_verbosity_flag::{InfoLevel, Verbosity};
use std::env;
use std::path::PathBuf;
use tauri::api::clap;
use tauri::api::process::Command;
use tauri::Manager;

#[derive(Parser)]
#[clap(version, about)]
struct Cli {
    #[clap(short, long)]
    display_console: bool,
    #[clap(long)]
    launch_from_path: Option<PathBuf>,
    #[clap(flatten)]
    verbosity: Verbosity<InfoLevel>,
}

fn is_valid_path(path: &PathBuf) -> bool {

    let java_folder = path.join("jre");
    let server_path = path.join("slimevr.jar");

    return java_folder.exists() && server_path.exists();
}

fn get_launch_path(cli: Cli) -> Option<PathBuf> {
    let mut path = cli.launch_from_path.unwrap_or_default();
    if path.exists() && is_valid_path(&path) {
        return Some(path);
    }

    path = env::current_dir().unwrap();
    if path.exists() && is_valid_path(&path) {
        return Some(path);
    }
    
    path = PathBuf::from(env!("CARGO_MANIFEST_DIR"));
    if path.exists() && is_valid_path(&path) {
        return Some(path);
    }
   
    None
}

fn main() {
    let cli = Cli::parse();

    // Set up loggers and global handlers
    {
        if std::env::var_os("RUST_LOG").is_none() {
            std::env::set_var("RUST_LOG", "info")
        }
        pretty_env_logger::init();
    }

    // Ensure child processes die when spawned on windows
    #[cfg(target_os = "windows")]
    {
        use win32job::{ExtendedLimitInfo, Job};

        let mut info = ExtendedLimitInfo::new();
        info.limit_kill_on_job_close();
        let job = Job::create_with_limit_info(&mut info).expect("Failed to create Job");
        job.assign_current_process()
            .expect("Failed to assign current process to Job");

        // We don't do anything with the job anymore, but we shouldn't drop it because that would
        // terminate our process tree. So we intentionally leak it instead.
        std::mem::forget(job);
    }

    // Spawn server process
    let run_path = get_launch_path(cli);

    let stdout_recv = if let Some(p) = run_path {
        log::info!("Server found on path: {}", p.to_str().unwrap());
        
        let java_folder = p.join("jre");
        let (recv, _child) = Command::new(java_folder.join("bin/java").to_str().unwrap())
            .current_dir(p)
            .args(["-Xmx512M", "-jar", "slimevr.jar", "--no-gui"])
            .spawn()
            .expect("Unable to start the server jar");
        Some(recv)
    } else {
        log::warn!("No server found. We will not start the server.");
        None
    };

    tauri::Builder::default()
        .plugin(tauri_plugin_window_state::Builder::default().build())
        .setup(|app| {
            if let Some(mut recv) = stdout_recv {
                let app_handle = app.app_handle();
                tauri::async_runtime::spawn(async move {
                    use tauri::api::process::CommandEvent;

                    while let Some(cmd_event) = recv.recv().await {
                        let emit_me = match cmd_event {
                            CommandEvent::Stderr(s) => ("stderr", s),
                            CommandEvent::Stdout(s) => ("stdout", s),
                            CommandEvent::Error(s) => ("error", s),
                            CommandEvent::Terminated(s) => ("terminated", format!("{s:?}")),
                            _ => ("other", "".to_string()),
                        };
                        app_handle
                            .emit_all("server-status", emit_me)
                            .expect("Failed to emit");
                    }
                    app_handle
                        .emit_all("server-status", ("other", "receiver cancelled"))
                        .expect("Failed to emit");
                });
            }
            Ok(())
        })
        //
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
