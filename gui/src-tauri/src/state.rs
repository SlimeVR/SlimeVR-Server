use std::{fs, path::PathBuf};

use anyhow::Result;
use serde::{Deserialize, Serialize};
use tauri::{LogicalSize, Monitor, PhysicalPosition, PhysicalSize, Window};

static STATE_FILENAME: &str = ".window-state.json";

#[derive(Serialize, Deserialize, Debug, Default)]
pub struct WindowState {
	maximized: bool,
	width: f64,
	height: f64,
	x: i32,
	y: i32,
	#[serde(skip)]
	old: bool,
}

impl WindowState {
	pub fn open_state(path: PathBuf) -> Option<Self> {
		if let Ok(file) = fs::File::open(path.join(STATE_FILENAME)) {
			return serde_json::from_reader(file)
				.map(|mut s: WindowState| {
					s.old = true;
					s
				})
				.ok();
		}
		None
	}

	pub fn is_old(&self) -> bool {
		self.old
	}

	pub fn save_state(&self, path: PathBuf) -> Result<()> {
		let file = fs::File::create(path.join(STATE_FILENAME))?;
		serde_json::to_writer(file, self)?;
		Ok(())
	}

	pub fn update_state(&mut self, window: &Window) -> Result<()> {
		self.maximized = window.is_maximized()?;
		let scale_factor = window.scale_factor()?;
		let size = window.outer_size()?.to_logical::<f64>(scale_factor);
		let pos = window.outer_position()?;

		self.width = size.width;
		self.height = size.height;

		self.x = pos.x;
		self.y = pos.y;
		Ok(())
	}

	pub fn update_window(&self, window: &Window, ignore_maximized: bool) -> Result<()> {
		let maximized = !ignore_maximized && window.is_maximized()?;
		if maximized && !self.maximized {
			window.unmaximize()?;
		}

		window.set_size(LogicalSize::new(self.width, self.height))?;

		let pos = PhysicalPosition::new(self.x, self.y);
		for monitor in window.available_monitors()? {
			if monitor.contains(pos) {
				window.set_position(pos)?;
				break;
			}
		}

		if !ignore_maximized && !maximized && self.maximized {
			window.maximize()?;
		}

		Ok(())
	}
}

pub trait MonitorExt {
	fn contains(&self, position: PhysicalPosition<i32>) -> bool;
}

/// Allowed amount to overflow out of the screen
const ABSOLUTE_ERROR: i32 = 32;
impl MonitorExt for Monitor {
	fn contains(&self, position: PhysicalPosition<i32>) -> bool {
		let PhysicalPosition { x, y } = *self.position();
		let PhysicalSize { width, height } = *self.size();

		(x < position.x + ABSOLUTE_ERROR) as _
			&& (position.x - ABSOLUTE_ERROR) < (x + width as i32)
			&& (y - ABSOLUTE_ERROR) < position.y as _
			&& (position.y + ABSOLUTE_ERROR) < (y + height as i32)
	}
}
