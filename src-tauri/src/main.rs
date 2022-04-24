#![cfg_attr(
    all(not(debug_assertions), target_os = "windows"),
    windows_subsystem = "windows"
)]

use std::path::PathBuf;
use std::process::Command;

// the payload type must implement `Serialize` and `Clone`.

fn main() {
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
    let runfile_path = PathBuf::from(env!("CARGO_MANIFEST_DIR")).join("run.bat");
    if runfile_path.exists() {
        Command::new("cmd")
            .args(["/C", runfile_path.to_str().unwrap()])
            .spawn()
            .expect("sh command failed to start");
    } else {
        log::warn!("No run.bat found, SKIP");
    }

    tauri::Builder::default()
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
