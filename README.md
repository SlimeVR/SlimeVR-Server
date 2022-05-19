# SlimeVR Server
Server app for SlimeVR ecosystem

Server orchestrates communication between multiple sensors and integrations, like SteamVR.

Sensors implementations:
* [SlimeVR Tracker for ESP](https://github.com/SlimeVR/SlimeVR-Tracker-ESP) - ESP microcontrollers and multiple IMUs are supported
* [owoTrack Mobile App](https://github.com/abb128/owoTrackVRSyncMobile) - use phone as a tracker (limited functionality and compatibility)

Integrations:
* Use [SlimeVR OpenVR Driver](https://github.com/SlimeVR/SlimeVR-OpenVR-Driver) as a driver for SteamVR
* Integrations with other systems will be added later

## How to use

It's recommended to download installer from here: https://github.com/SlimeVR/SlimeVR-Installer/releases/latest/download/slimevr_web_installer.exe

Latest instructions are [on our site](https://docs.slimevr.dev/server-setup/slimevr-setup.html).

## License Clarification

**SlimeVR software** (including server, firmware, drivers, installator, documents, and others - see licence for each case specifically) **is distributed under the [MIT License](https://github.com/SlimeVR/SlimeVR-Server/blob/main/LICENSE) and is copyright of Eiren Rain and SlimeVR.** The MIT Licence is a permissive license giving you rights to modify and distribute the software with little strings attached.

**However, the MIT License has some limits, and if you wish to distribute software based on SlimeVR, you need to be aware of them:**

* When distributing any software that uses or is based on SlimeVR, you have to provide to the end-user the original, unmodified `LICENSE` file from SlimeVR. This file is located [here](https://github.com/SlimeVR/SlimeVR-Server/blob/main/LICENSE). This includes the `Copyright (c) 2021 Eiren Rain, SlimeVR` part of the license. It is not sufficient to use a generic MIT License, it must be the original license file.
* This applies even if you distribute software without the source code. In this case, one way to provide it to the end-user is to have a menu in your application that lists all the open source licenses used, including SlimeVR's.

Please refer to the [LICENSE](https://github.com/SlimeVR/SlimeVR-Server/blob/main/LICENSE) file if you are at any point uncertain what the exact the requirements are.

## Contributions
By contributing to this project you are placing all your code under MIT or less restricting licenses, and you certify that the code you have used is compatible with those licenses or is authored by you. If you're doing so on your work time, you certify that your employer is okay with this.

For a how-to on contributing, see [CONTRIBUTING.md](CONTRIBUTING.md).
