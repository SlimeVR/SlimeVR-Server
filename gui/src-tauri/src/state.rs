use std::{fs, path::PathBuf};

use color_eyre::Result;
use serde::{Deserialize, Serialize};
use tauri::{LogicalSize, Monitor, PhysicalPosition, PhysicalSize, Window};

use crate::util;

static STATE_FILENAME: &str = ".window-state.json";

#[derive(Serialize, Deserialize, Debug, Default)]
pub struct WindowState {
	maximized: bool,
	width: f64,
	height: f64,
	x: i32,
	y: i32,
	decorated: bool,
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
		if !path.exists() {
			fs::create_dir(&path)?
		}
		let file = fs::File::create(path.join(STATE_FILENAME))?;
		serde_json::to_writer(file, self)?;
		Ok(())
	}

	pub fn update_state(
		&mut self,
		window: &Window,
		ignore_maximized: bool,
	) -> Result<()> {
		self.decorated = window.is_decorated()?;

		let maximized = window.is_maximized()?;
		self.maximized = maximized || (self.maximized && ignore_maximized);
		// We early return when it's maximized because we dont have to save the state
		// of the rest of the window when it's maximized.
		if maximized {
			return Ok(());
		}
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
		window.set_decorations(self.decorated)?;

		let maximized = !ignore_maximized && window.is_maximized()?;
		if maximized && !self.maximized {
			window.unmaximize()?;
		}

		let size = if self.width >= util::MIN_WINDOW_SIZE_WIDTH
			&& self.height >= util::MIN_WINDOW_SIZE_HEIGHT
		{
			Some(LogicalSize::new(self.width, self.height))
		} else {
			None
		};

		let pos = PhysicalPosition::new(self.x, self.y);
		let monitor = window
			.available_monitors()?
			.into_iter()
			.find(|x| x.contains(pos))
			.map(|m| (m, true))
			.or(window.current_monitor()?.map(|m| (m, false)));

		// Don't surpass the monitor's size
		if let Some((monitor, is_old)) = monitor {
			let monitor_size = *monitor.size();
			let window_size = size
				.map(|s| s.to_physical(monitor.scale_factor()))
				.unwrap_or(window.outer_size()?);
			window.set_size(PhysicalSize::new(
				u32::min(monitor_size.width, window_size.width),
				u32::min(monitor_size.height, window_size.height),
			))?;

			// If the position of the window was previously in the config
			if is_old {
				window.set_position(pos)?;
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
const ALLOWED_OVERFLOW: i32 = 16;
impl MonitorExt for Monitor {
	fn contains(&self, position: PhysicalPosition<i32>) -> bool {
		let PhysicalPosition { x, y } = *self.position();
		let PhysicalSize { width, height } = *self.size();

		(x < position.x + ALLOWED_OVERFLOW) as _
			&& (position.x - ALLOWED_OVERFLOW) < (x + width as i32)
			&& (y - ALLOWED_OVERFLOW) < position.y as _
			&& (position.y + ALLOWED_OVERFLOW) < (y + height as i32)
	}
}
