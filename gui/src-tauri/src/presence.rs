use std::time::{Duration, SystemTime};

use color_eyre::{eyre::bail, Result};
use discord_sdk as ds;
use ds::wheel::{UserSpoke, UserState};
use tauri::{async_runtime::Mutex, AppHandle, Manager, Runtime, State};

const APP_ID: ds::AppId = 1237970689009647639;

pub struct DiscordClient {
	pub discord: ds::Discord,
	pub wheel: ds::wheel::Wheel,
}

pub struct DiscordTimestamp(SystemTime);

pub struct ExposedClient(Mutex<Option<DiscordClient>>);

async fn make_client(subs: ds::Subscriptions) -> Result<Option<DiscordClient>> {
	let (wheel, handler) = ds::wheel::Wheel::new(Box::new(|err| {
		log::error!(target: "discord_presence", "encountered a discord presence error: {err}");
	}));

	let mut user = wheel.user();

	let discord =
		ds::Discord::new(ds::DiscordApp::PlainId(APP_ID), subs, Box::new(handler))?;

	log::debug!(target: "discord_presence", "waiting for handshake...");
	let Ok(e) = tokio::time::timeout(Duration::from_secs(5), user.0.changed()).await
	else {
		return Ok(None);
	};
	e?;

	let _ = match &*user.0.borrow() {
		ds::wheel::UserState::Connected(user) => user.clone(),
		ds::wheel::UserState::Disconnected(err) => {
			bail!("failed to connect to Discord: {err}");
		}
	};

	log::info!(target: "discord_presence", "connected to Discord!");

	Ok(Some(DiscordClient { discord, wheel }))
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
pub async fn create_discord_client<R: Runtime>(
	app: AppHandle<R>,
	client: State<'_, ExposedClient>,
) -> Result<(), String> {
	if client_exists(&client).await {
		return Err("Trying to create a client when there is one already".to_owned());
	}

	let Some(discord_client) = make_client(ds::Subscriptions::ACTIVITY)
		.await
		.map_err(|e| e.to_string())?
	else {
		log::debug!(target: "discord_presence", "discord took too long to answer (probably not open)");
		return Ok(());
	};
	let user_wheel = discord_client.wheel.user();
	{
		let mut lock = client.0.lock().await;
		*lock = Some(discord_client);
	}

	tauri::async_runtime::spawn(async move {
		drop_client_on_loss(app, user_wheel).await;
	});
	Ok(())
}

async fn drop_client_on_loss<R: Runtime>(
	app: tauri::AppHandle<R>,
	mut user_wheel: UserSpoke,
) {
	while let Ok(_) = user_wheel.0.changed().await {
		if let UserState::Disconnected(e) = &*user_wheel.0.borrow() {
			match e {
				ds::Error::NoConnection
				| ds::Error::TimedOut
				| ds::Error::Close(_)
				| ds::Error::CorruptConnection => break,
				_ => {
					log::error!(target: "discord_presence", "unhandled discord error: {e}")
				}
			}
		}
	}
	log::info!(target: "discord_presence", "lost connection to discord, dropping client...");
	let mutex = app.state::<ExposedClient>();
	let opt = {
		let mut lock = mutex.0.lock().await;
		lock.take()
	};
	let Some(client) = opt else {
		return;
	};
	client.discord.disconnect().await;
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
			let Some(client) = client.unwrap() else {
				log::debug!(target: "discord_presence", "discord took too long to answer (probably not open)");
				return;
			};
			let user_wheel = client.wheel.user();
			{
				let mutex = app.state::<ExposedClient>();
				let mut lock = mutex.0.lock().await;
				*lock = Some(client)
			}
			tauri::async_runtime::spawn(async move {
				drop_client_on_loss(app, user_wheel).await;
			});
		});
	}

	Ok(())
}
