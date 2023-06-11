# SlimeVR Server
Server app for SlimeVR ecosystem

Server orchestrates communication between multiple sensors and integrations, like SteamVR.

Sensors implementations:
* [SlimeVR Tracker for ESP](https://github.com/SlimeVR/SlimeVR-Tracker-ESP) - ESP microcontrollers and multiple IMUs are supported
* [owoTrack Mobile App](https://github.com/abb128/owoTrackVRSyncMobile) - use phones as trackers (limited functionality and compatibility)
* [SlimeVR Wrangler](https://github.com/carl-anders/slimevr-wrangler) - use Nintendo Switch Joycon controllers as trackers

Integrations:
* Use [SlimeVR OpenVR Driver](https://github.com/SlimeVR/SlimeVR-OpenVR-Driver) as a driver for SteamVR.
* Use built-in OSC Trackers support for FBT integration with VRChat, PCVR or Standalone.
* Use built-in VMC support for sending and receiving tracking data to and from other apps such as VSeeFace.
* Export recordings as .BVH files to integrate motion capture data into 3d applications such as Blender.

## Installing
It's highly recommended to install using the installer downloadable here: https://github.com/SlimeVR/SlimeVR-Installer/releases/latest/download/slimevr_web_installer.exe

Latest setup instructions are [in our docs](https://docs.slimevr.dev/server/index.html).

## Building & Contributing
For information on building and contributing to the codebase, see [CONTRIBUTING.md](CONTRIBUTING.md).

## Translating

Translation is done via Pontoon at [i18n.slimevr.dev](https://i18n.slimevr.dev/). Please join our [Discord translation forum](https://discord.com/channels/817184208525983775/1050413434249949235) to coordinate.

## License clarification
**SlimeVR software** (including server, firmware, drivers, installer, documents, and others - see
licence for each case specifically) **is distributed under a dual MIT/Apache 2.0 License
([LICENSE-MIT] and [LICENSE-APACHE]). The software is the copyright of the SlimeVR
contributors.**

**However, these licenses have some limits, and if you wish to distribute software based
on SlimeVR, you need to be aware of them:**

* When distributing any software that uses or is based on SlimeVR, you have to provide
  to the end-user at least one of the original, unmodified [LICENSE-MIT] or
  [LICENSE-APACHE] files from SlimeVR. This includes the `Copyright (c) 2020 Eiren Rain
  and SlimeVR Contributors` part of the license. It is insufficient to use a generic MIT
  or Apache-2.0 License, **it must be the original license file**.
* This applies even if you distribute software without the source code. In this case,
  one way to provide it to the end-user is to have a menu in your application that lists
  all the open source licenses used, including SlimeVR's.

Please refer to the [LICENSE-MIT] and [LICENSE-APACHE] files if you are at any point
uncertain what the exact requirements are.

## Contributions
Any contributions submitted for inclusion in this repository will be dual-licensed under
either:

- MIT License ([LICENSE-MIT])
- Apache License, Version 2.0 ([LICENSE-APACHE])

Unless you explicitly state otherwise, any contribution intentionally submitted for
inclusion in the work by you, as defined in the Apache-2.0 license, shall be dual
licensed as above, without any additional terms or conditions.

You also certify that the code you have used is compatible with those licenses or is
authored by you. If you're doing so on your work time, you certify that your employer is
okay with this and that you are authorized to provide the above licenses.

[LICENSE-MIT]: LICENSE-MIT
[LICENSE-APACHE]: LICENSE-APACHE
