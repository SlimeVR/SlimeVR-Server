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

Latest instructions are currently [here](https://gist.github.com/Eirenliel/8c0eefcdbda1076d5c2e1bf634831d20). Will be updated and republished as time goes on.

## How to build

You need to execute these commands in the folder where you want this project.

```bash
# Clone repositories
git clone https://github.com/SlimeVR/SlimeVR-Server.git
git clone https://github.com/Eirenliel/slime-java-commons.git

# Enter the directory and build the runnable server JAR
cd SlimeVR-Server
gradlew serverJar
```

Open Slime VR Server project in Eclipse or Intellij Idea

run gradle command `serverJar` to build a runnable server JAR
