#![cfg_attr(
  all(not(debug_assertions), target_os = "windows"),
  windows_subsystem = "windows"
)]

use std::process::Command;
use std::path::Path;


// the payload type must implement `Serialize` and `Clone`.

fn main() {

  let child = if Path::new("run.bat").exists() {
    let child = Command::new("cmd")
      .args(["/C", "run.bat"])
      .spawn()
      .expect("sh command failed to start");
    Some(child)
  } else {
    println!("No run.bat found, SKIP");
    None
  };
    
  

  tauri::Builder::default()
    .on_window_event(|event| match event.event() {
      tauri::WindowEvent::Destroyed => {
        println!("DESTROYED");

      }
      _ => {}
    })
    .run(tauri::generate_context!())
    .expect("error while running tauri application");

  child.map(|mut child| {
    child.wait().unwrap();
    println!("STOP?");
  });
}
