# SlimeVR-Server
Server app for SlimeVR ecosystem

Server orchestrates communication between VR driver running in SteamVR and multiple sensors.

Sensors implementations:
* [SlimeVR Tracker for ESP](https://github.com/SlimeVR/SlimeVR-Tracker-ESP) - EPS8266 microcontroller and multiple IMUs are supported. Future
* [owoTrack Mobile App](https://github.com/abb128/owoTrackVRSyncMobile) - use phone as a tracker (might not work with most recent version due to changes in protocol)

Driver implementation:
* Uses [AptrilTags FBT driver](https://github.com/SlimeVR/Simple-OpenVR-Driver-Tutorial) as a driver (will be replaced soon)