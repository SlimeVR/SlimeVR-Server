# SlimeVR-Server
Server app for SlimeVR ecosystem

Server orchestrates communication between VR driver running in SteamVR and multiple sensors.

Sensors implementations:
* [owoTrack Mobile App](https://github.com/abb128/owoTrackVRSyncMobile) - use phone as a tracker
* [SlimeVR Tracker for ESP](https://github.com/Eirenliel/SlimeVR-Tracker-ESP) - use EPS8266 and MPU9250 as tracker (supports multiple trackers)

Driver implementation:
* Uses [AptrilTags FBT driver](https://github.com/Eirenliel/Simple-OpenVR-Driver-Tutorial) as a driver