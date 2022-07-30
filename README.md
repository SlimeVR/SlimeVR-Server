# SlimeVR UI

This is the GUI of SlimeVR, it uses the SolarXR protocol to communicate with the server and is completely isolated from the server logic.

This project is written in Typescript + React for the frontend and uses Tauri + Rust as a small backend. This makes the application more lightweight than electron.

## Compiling

### Prerequisites

- Node.js 16 (We recommend the use of `nvm` instead of installing Node.js directly)
- Windows Webview
- SlimeVR server installed

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

## Contributing

By contributing to this project you are placing all your code under MIT or less restricting licenses, and you certify that the code you have used is compatible with those licenses or is authored by you. If you're doing so on your work time, you certify that your employer is okay with this.

## License

All code in this repository is dual-licensed under either:

- MIT License ([LICENSE-MIT](docs/LICENSE-MIT))
- Apache License, Version 2.0 ([LICENSE-APACHE](docs/LICENSE-APACHE))

at your option. This means you can select the license you prefer!

Unless you explicitly state otherwise, any contribution intentionally submitted for inclusion in the work by you, as defined in the Apache-2.0 license, shall be dual licensed as above, without any additional terms or conditions.

### Complying with the license

Please note that these licenses are very permissive, but if you wish to distribute software based on this code, you need to be aware of the following limits of these licenses:

- When distributing any software that uses or is based on SlimeVR, you have to provide to the end-user the original, unmodified `LICENSE-MIT` or `LICENSE-APACHE` file (or both) from SlimeVR. This includes the `Copyright (c) 2022 SlimeVR Contributors` part of the license. It is not sufficient to use a generic MIT/Apache License, it must be the original license file.
- This applies even if you distribute software without the source code. In this case, one way to provide it to the end-user is to have a menu in your application that lists all the open source licenses used, including SlimeVR's.

Please refer to the original license files if you are at any point uncertain what the exact the requirements are.
