# SlimeVR UI

This is the GUI of SlimeVR, it uses the SolarXR protocol to communicate with the server and is completely isolated from the server logic.

This project is written in Typescript + React for the frontend and uses Tauri + Rust as a small backend. This makes the application more lightweight than electron.

## Compiling

### Prerequisites

- [Node.js](https://nodejs.org) 16 (We recommend the use of `nvm` instead of installing Node.js directly)
- Windows Webview
- SlimeVR server installed
- [Rust](https://rustup.rs)

```
npm install
```

Build for production

```
npm run tauri build
```

Launch in dev mode

```
npm run tauri dev
```
