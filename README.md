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

Latest instructions are [on our site](https://docs.slimevr.dev/slimevr-setup.html).

## How to build

You need to execute these commands in the folder where you want this project.

```bash
# Clone repositories
git clone --recursive https://github.com/SlimeVR/SlimeVR-Server.git

# Enter the directory and build the runnable server JAR
cd SlimeVR-Server
gradlew shadowJar
```

Open Slime VR Server project in Eclipse or Intellij Idea

run gradle command `shadowJar` to build a runnable server JAR

## License Clarifications

**SlimeVR software** (including server, firmware, drivers, installator, documents, and others - see licence for each case specifically) **is distributed under MIT License and is copyright of Eiren Rain and SlimeVR.** MIT Licence is a permissive license giving you rights to modify and distribute the software with little strings attached.

**However, there are some limits, and if you wish to distribute software based on SlimeVR, you need to be aware of them:**

* When distributing any software based on SlimeVR, you have to clarify to the end user that your software is based on SlimeVR that is distributed under MIT License and is subject to copyright of Eiren Rain
* You must clarify either which parts of original software you're using, or what changes you did to the original software (i.e. clarify which parts of your software is covered by MIT License)
* You must provide a copy of the original license (see LICENSE file)
* You don't have to release your own software under MIT License or even open source at all, but you have to state that it's based on SlimeVR
* This applies even if you distribute software without the source code

## Contributions

By contributing to this project you are placing all your code under MIT or less restricting licenses, and you certify that the code you have used is compatible with those licenses or is authored by you. If you're doing so on your work time, you certify that your employer is okay with this.
