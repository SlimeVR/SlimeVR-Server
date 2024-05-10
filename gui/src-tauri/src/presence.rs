use std::time::SystemTime;

use color_eyre::{eyre::bail, Result};
use discord_sdk as ds;
use tauri::{async_runtime::Mutex, Manager, Runtime, State};

const APP_ID: ds::AppId = 1237970689009647639;

pub struct DiscordClient {
	pub discord: ds::Discord,
	pub user: ds::user::User,
	pub wheel: ds::wheel::Wheel,
}

pub struct DiscordTimestamp(SystemTime);

pub struct ExposedClient(Mutex<Option<DiscordClient>>);

async fn make_client(subs: ds::Subscriptions) -> Result<DiscordClient> {
	let (wheel, handler) = ds::wheel::Wheel::new(Box::new(|err| {
		log::error!(target: "discord_presence", "encountered a discord presence error: {err}");
	}));

	let mut user = wheel.user();

	let discord =
		ds::Discord::new(ds::DiscordApp::PlainId(APP_ID), subs, Box::new(handler))?;

	log::info!(target: "discord_presence", "waiting for handshake...");
	user.0.changed().await?;

	let user = match &*user.0.borrow() {
		ds::wheel::UserState::Connected(user) => user.clone(),
		ds::wheel::UserState::Disconnected(err) => {
			bail!("failed to connect to Discord: {err}");
		}
	};

	log::info!(target: "discord_presence", "connected to Discord, local user name is {}", user.username);

	Ok(DiscordClient {
		discord,
		user,
		wheel,
	})
}

async fn client_exists(client: &State<'_, ExposedClient>) -> bool {
	let lock = client.0.lock().await;
	lock.is_some()
}

#[tauri::command]
pub async fn discord_client_exists(
	client: State<'_, ExposedClient>,
) -> Result<bool, ()> {
	Ok(client_exists(&client).await)
}

#[tauri::command]
pub async fn update_presence(
	client: State<'_, ExposedClient>,
	timestamp: State<'_, DiscordTimestamp>,
	details: String,
	state: Option<String>,
	small_icon: Option<(String, String)>,
	button: Option<ds::activity::Button>,
) -> Result<(), ()> {
	if !client_exists(&client).await {
		return Err(());
	}

	let rp = ds::activity::ActivityBuilder::default()
		.details(details)
		.start_timestamp(timestamp.0);
	let rp = if let Some(state) = state {
		rp.state(state)
	} else {
		rp
	};
	let rp = if let Some((id, desc)) = small_icon {
		rp.assets(
			ds::activity::Assets::default()
				.large("icon".to_owned(), Some("SlimeVR".to_owned()))
				.small(id, Some(desc)),
		)
	} else {
		rp.assets(
			ds::activity::Assets::default()
				.large("icon".to_owned(), Some("SlimeVR".to_owned())),
		)
	};
	let rp = if let Some(button) = button {
		rp.button(button)
	} else {
		rp
	};

	let lock = client.0.lock().await;
	lock.as_ref()
		.unwrap()
		.discord
		.update_activity(rp)
		.await
		.map_err(|_e| ())?;

	Ok(())
}

#[tauri::command]
pub async fn clear_presence(client: State<'_, ExposedClient>) -> Result<(), String> {
	if !client_exists(&client).await {
		return Err("Missing discord client".to_owned());
	}

	let lock = client.0.lock().await;
	lock.as_ref()
		.unwrap()
		.discord
		.clear_activity()
		.await
		.map_err(|e| e.to_string())?;

	Ok(())
}

#[tauri::command]
pub async fn create_discord_client(
	client: State<'_, ExposedClient>,
) -> Result<(), String> {
	if client_exists(&client).await {
		return Err("Trying to create a client when there is one already".to_owned());
	}

	let discord_client = make_client(ds::Subscriptions::ACTIVITY)
		.await
		.map_err(|e| e.to_string())?;
	let mut lock = client.0.lock().await;
	*lock = Some(discord_client);
	Ok(())
}

pub fn create_presence<R: Runtime>(app: &tauri::AppHandle<R>) -> tauri::Result<()> {
	app.manage(ExposedClient(Mutex::new(None)));
	app.manage(DiscordTimestamp(SystemTime::now()));
	{
		let app = app.clone();
		tauri::async_runtime::spawn(async move {
			let client = make_client(ds::Subscriptions::ACTIVITY).await;
			if let Err(e) = client {
				log::error!(target: "discord_presence", "couldn't initialize discord client: {e}");
				return;
			}
			let client = client.unwrap();
			let mutex = app.state::<ExposedClient>();
			{
				let mut lock = mutex.0.lock().await;
				*lock = Some(client)
			}
		});
	}

	Ok(())
}
