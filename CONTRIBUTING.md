# Contributing to SlimeVR

This document describes essential knowledge for contributors to SlimeVR.

## How to get started

### Getting the code
First, clone the codebase using `git`. If you don't have `git` installed, go install it.

```bash
# Clone repositories
git clone --recursive https://github.com/SlimeVR/SlimeVR-Server.git

# Enter the directory of the codebase
cd SlimeVR-Server
```

Now you can open the codebase in your favorite IDE or text editor.

### Building the code
The code is built with `gradle`, a cli tool that manages java projects and their
dependencies. You can build the code with `./gradlew build` and run it with
`./gradlew run`.


## Code Style
Code is autoformatted with [spotless](https://github.com/diffplug/spotless/tree/main/plugin-gradle).
Code is checked for autoformatting whenever you build, but you can also run
`./gradlew spotlessCheck` if you prefer.

To autoformat your code from the command line, you can run `./gradlew spotlessApply`.
We recommend installing support for spotless in your IDE of choice, and formatting
whenever you save a file, to make things easy.

If you need to prevent autoformatting for a particular region of code, use
`// @formatter:off` and `// @formatter:on`

### Setting up spotless in VSCode
* Install the `richardwillis.vscode-spotless-gradle` extension
* Add the following to your workspace settings, at `.vscode/settings.json`:
```json
"spotlessGradle.format.enable": true,
"editor.formatOnSave": true,
"[java]": {
	"editor.defaultFormatter": "richardwillis.vscode-spotless-gradle"
}
```

### Setting up spotless for IntelliJ
* Install https://plugins.jetbrains.com/plugin/18321-spotless-gradle.
* Add a keyboard shortcut for `Code` > `Reformat Code with Spotless`
* They are working on support to do this on save without a keybind
[here](https://github.com/ragurney/spotless-intellij-gradle/issues/8)

### Setting up Eclipse autoformatting
Import the formatting settings defined in `spotless.xml`, like this:
* Go to `File > Properties`, then `Java Code Style > Formatter`
* Check `Enable project specific settings`
* Click `Import`, then open `spotless.xml`, then `Apply`
* Go to `Java Editor > Save Actions`
* Select `Enable project specific settings`, `Perform the selected actions on save`,
`Format source code`, `Format all lines`

Eclipse will only do a subset of the checks in `spotless`, so you may still want to do
`./gradlew spotlessApply` if you ever see an error from spotless.

## Code Licensing
SlimeVR uses an MIT license, and some parts of the project use a dual MIT/Apache 2.0
license. Be sure that any code that you reference, or dependencies you add, are
compatible with these licenses. `GPL-v3` for example is not compatible because it
requires any and all code that depends on it to *also* be licensed under `GPL-v3`.

## Discord
We use discord *a lot* to coordinate and discuss development. Come join us at
https://discord.gg/SlimeVR!
