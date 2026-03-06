-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean
-dontwarn reactor.blockhound.integration.BlockHoundIntegration

-dontwarn java.beans.**

-dontwarn io.netty.**
-keep class io.netty.** { *; }

# Jackson 2.x
# https://github.com/FasterXML/jackson-docs/wiki/JacksonOnAndroid
-keepattributes *Annotation*,Signature,EnclosingMethod

-keep class com.fasterxml.jackson.**
-keepclassmembers class * {
    @com.fasterxml.jackson.annotation.* *;
}

# Jackson 2.x Model Versioning Module
-keep class com.github.jonpeterson.jackson.**

# SnakeYAML 2.X
-keep class org.yaml.snakeyaml.**

# Don't mess with SlimeVR config, the class structure is essential for serialization
-keep class dev.slimevr.config.** { *; }

# Obfuscation is fine but it makes crash logs unreadable, we don't really need it for our app
-dontobfuscate
