# Contributing to SlimeVR

This document describes essential knowledge required to contribute to the SlimeVR Server.

### Prerequisites

- [Git](https://git-scm.com/downloads)
- [Java v17+](https://adoptium.net/temurin/releases/)
- [Node.js v16.9+](https://nodejs.org) (We recommend the use of `nvm` instead of installing Node.js directly)
- [Microsoft Edge WebView2](https://developer.microsoft.com/en-us/microsoft-edge/webview2/#download-section) or `webkit2gtk` for Linux
- [Rust](https://rustup.rs)

## Cloning the code
First, clone the codebase using git in a terminal in the folder you want.

```bash
git clone --recursive https://github.com/SlimeVR/SlimeVR-Server.git
```

Now you can open the codebase in [IDEA](https://www.jetbrains.com/idea/download/) (Recommended; VSCode and Eclipse also work but have limited Kotlin support).


## Building the code

### Java (server)

The Java code is built with `gradle`, a CLI tool that manages java projects and their
dependencies.
- You can run the server by running `./gradlew run` in your IDE's terminal.
- To compile the code, run `./gradlew shadowJar`. The result will
be at `server/build/libs/slimevr.jar` (you can ignore `server.jar`).

(Note: Your IDE may be able to do all of the above for you.)

### Tauri (gui)

- Activate corepack (included with Node.JS) via `corepack enable` (might require administrator permissions)
- Run `pnpm i` in your IDE's terminal to download and install dependencies.
- To launch the GUI in dev mode, run `pnpm gui`.
- Finally, to compile for production, run `pnpm run tauri build`. The result
will be at `target/release/slimevr.exe`.

## Code style

### Java (server)

The Java code is auto-formatted with [spotless](https://github.com/diffplug/spotless/tree/main/plugin-gradle).
Code is checked for autoformatting whenever you build, but you can also run
`./gradlew spotlessCheck` if you prefer.

To auto-format your Java and Kotlin code from the command line, you can run `./gradlew spotlessApply`.
We recommend installing support for spotless in your IDE, and formatting
whenever you save a file to make things easy.

If you need to prevent autoformatting for a select region of code, use
`// @formatter:off` and `// @formatter:on`

#### Setting up spotless for IntelliJ IDEA
* Install https://plugins.jetbrains.com/plugin/18321-spotless-gradle
* Add a keyboard shortcut for `Code` > `Reformat Code with Spotless`
* They are working on support to do this on save without a keybind
  [here](https://github.com/ragurney/spotless-intellij-gradle/issues/8)

#### Setting up spotless for VSCode
* Install the `richardwillis.vscode-spotless-gradle` extension
* Add the following to your workspace settings, at `.vscode/settings.json`:
```json
"spotlessGradle.format.enable": true,
"editor.formatOnSave": true,
"[java]": {
	"editor.defaultFormatter": "richardwillis.vscode-spotless-gradle"
}
```

#### Setting up Eclipse autoformatting
Import the formatting settings defined in `spotless.xml`, like this:
* Go to `File > Properties`, then `Java Code Style > Formatter`
* Check `Enable project specific settings`
* Click `Import`, then open `spotless.xml`, then `Apply`
* Go to `Java Editor > Save Actions`
* Select `Enable project specific settings`, `Perform the selected actions on save`,
`Format source code`, `Format all lines`

Eclipse will only do a subset of the checks in `spotless`, so you may still want to do
`./gradlew spotlessApply` if you ever see an error from spotless.

### Tauri (gui)

We use ESLint and Prettier to format GUI code.
- First, go into the GUI's directory with your terminal by running `cd gui`.
- To check code formatting, run `pnpm run lint`.
- To fix code formatting, run `pnpm run lint:fix` and `pnpm run format`

Don't forget to run `cd ..` to return to the root directory.

## SolarXR Protocol

SolarXR is used to communicate between the server (backend) and GUI (frontend).
It can also be used to communicate to third party applications.

When touching SolarXR:
- You will need `flatc`. To know which version to get, refer to
[SolarXR's README](https://github.com/SlimeVR/SolarXR-Protocol/blob/main/README.md#flatc)
- The only files you should edit are in the `schema` directory.
- After editing files, you should run `cd solarxr-protocol`, then either run
`./generate-flatbuffer.ps1` (Windows) or `./generate-flatbuffer.sh` (Linux/OSX)
- Make sure to commit your changes inside the submodule.

## Code Licensing
SlimeVR uses dual MIT and Apache-2.0 license. Be sure that any code that you reference,
or dependencies you add, are compatible with these licenses. For example, `GPL-v3` is
not compatible because it requires any and all code that depends on it to *also* be
licensed under `GPL-v3`.

## Discord
We use discord *a lot* to coordinate and discuss development. Come join us at
https://discord.gg/SlimeVR!
