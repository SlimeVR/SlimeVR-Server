#![cfg_attr(
    all(not(debug_assertions), target_os = "windows"),
    windows_subsystem = "windows"
)]

use clap::Parser;
use clap_verbosity_flag::{InfoLevel, Verbosity};
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
    let runfile_path = cli
        .launch_from_path
        .unwrap_or_else(|| PathBuf::from(env!("CARGO_MANIFEST_DIR")).join("run.bat"));
    let stdout_recv = if !runfile_path.exists() {
        log::warn!("runfile doesn't exist. We will not start the server.");
        None
    } else {
        let (recv, _child) = Command::new(runfile_path.to_str().unwrap())
            .spawn()
            .expect("sh command failed to start");
        Some(recv)
    };

    tauri::Builder::default()
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
                            _ => continue,
                        };
                        app_handle
                            .emit_all("server-stdio", emit_me)
                            .expect("Failed to emit");
                    }
                });
            }
            Ok(())
        })
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
