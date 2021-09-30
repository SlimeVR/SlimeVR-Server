OpenFullBody的第一个版本Fork自SlimeVR项目
# 易追服务端
# OpenFullBody Server
易追生态的服务端
Server app for OpenFullBody ecosystem
服务端作为中间纽带把追踪器与集成追踪服务连接起来，比如SteamVR。
Server orchestrates communication between multiple sensors and integrations, like SteamVR.

追踪器的制作：
Sensors implementations:
* [OpenFullBody Tracker for ESP](https://github.com/OpenFullBody/OpenFullBody-Tracker-ESP) - 支持使用ESP系列MCU和多种IMU搭建追踪器。请见露露sama的视频[一部手机实现VR身体腰部追踪！超低价高精度全身追踪！owotrack vr htc vive tracker](https://www.bilibili.com/video/BV1hQ4y1h7h6)
* [OpenFullBody Tracker for ESP](https://github.com/OpenFullBody/OpenFullBody-Tracker-ESP) - ESP microcontrollers and multiple IMUs are supported
* [owoTrack Mobile App](https://github.com/abb128/owoTrackVRSyncMobile) - 把手机作为一个追踪器（功能与兼容性受限）。同样是露露sama的视频[仅需580元！自制VR全身追踪器 全中文教程 高精度全身追踪 slimevr owotrack](https://www.bilibili.com/video/BV1ZR4y1H75i)
* [owoTrack Mobile App](https://github.com/abb128/owoTrackVRSyncMobile) - use phone as a tracker (limited functionality and compatibility)

集成追踪服务：
Integrations:
* 使用 [OpenFullBody OpenVR Driver](https://github.com/OpenFullBody/OpenFullBody-OpenVR-Driver) 作为SteamVR的驱动。
* Use [OpenFullBody OpenVR Driver](https://github.com/OpenFullBody/OpenFullBody-OpenVR-Driver) as a driver for SteamVR
* 对于其他集成追踪系统的支持将会在之后增加。
* Integrations with other systems will be added later

## 使用方法
## How to use

最新的使用说明放在了 [这里](https://gist.github.com/Eirenliel/8c0eefcdbda1076d5c2e1bf634831d20)。会定期更新此说明。
Latest instructions are currently [here](https://gist.github.com/Eirenliel/8c0eefcdbda1076d5c2e1bf634831d20). Will be updated and republished as time goes on.

## 编译方法
## How to build

你需要在你想放置这个工程的文件夹下面执行以下终端命令：
You need to execute these commands in the folder where you want this project.

```bash
# 克隆源目录
# Clone repositories
git clone https://github.com/OpenFullBody/OpenFullBody-Server.git
git clone https://github.com/Eirenliel/slime-java-commons.git

# 进入该目录并编译可执行的jar文件
# Enter the directory and build the runnable server JAR
cd OpenFullBody-Server
gradlew shadowJar
```

用Eclipse或者Intellij Idea打开这个工程文件
Open Open Full Body Server project in Eclipse or Intellij Idea

运行 [gradle](https://gradle.org/) 命令 `shadowJar` 来编译可运行的服务器jar文件
run gradle command `shadowJar` to build a runnable server JAR
