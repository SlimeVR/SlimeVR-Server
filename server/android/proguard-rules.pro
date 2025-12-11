-dontwarn java.awt.Color
-dontwarn java.beans.BeanInfo
-dontwarn java.beans.IntrospectionException
-dontwarn java.beans.Introspector
-dontwarn java.beans.PropertyDescriptor
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean
-dontwarn org.apache.logging.log4j.Level
-dontwarn org.apache.logging.log4j.LogManager
-dontwarn org.apache.logging.log4j.Logger
-dontwarn org.apache.logging.log4j.message.MessageFactory
-dontwarn org.apache.logging.log4j.spi.ExtendedLogger
-dontwarn org.apache.logging.log4j.spi.ExtendedLoggerWrapper
-dontwarn org.apache.log4j.Level
-dontwarn org.apache.log4j.Logger
-dontwarn org.apache.log4j.Priority
-dontwarn org.conscrypt.BufferAllocator
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.HandshakeListener
-dontwarn org.eclipse.jetty.npn.NextProtoNego$ClientProvider
-dontwarn org.eclipse.jetty.npn.NextProtoNego$Provider
-dontwarn org.eclipse.jetty.npn.NextProtoNego$ServerProvider
-dontwarn org.eclipse.jetty.npn.NextProtoNego
-dontwarn org.jetbrains.annotations.Async$Execute
-dontwarn org.jetbrains.annotations.Async$Schedule
-dontwarn reactor.blockhound.integration.BlockHoundIntegration

-keep class io.ktor.** { *; }
-keep class io.netty.** { *; }
-keep class kotlin.reflect.jvm.internal.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.atomicfu.**
-dontwarn io.netty.**
-dontwarn com.typesafe.**
-dontwarn org.slf4j.**

# Proguard configuration for Jackson 2.x
# https://github.com/FasterXML/jackson-docs/wiki/JacksonOnAndroid
#-keep class java.beans.** { *; }
#-dontwarn java.beans.**
#
#-keep class com.fasterxml.jackson.** { *; }
#-dontwarn com.fasterxml.jackson.databind.**
#
#-keep class com.github.jonpeterson.jackson.** { *; }
#
#-keepclassmembers class * {
#     @com.fasterxml.jackson.annotation.* *;
#}

# Proguard configuration for SnakeYAML 2.X
#-keep class org.yaml.snakeyaml.** { *; }
#-dontwarn org.yaml.snakeyaml.**

# Don't mess with SlimeVR config, the class structure is essential for serialization
-keep class dev.slimevr.config.** { *; }

# Obfuscation is fine but it makes crash logs unreadable, we don't really need it for our app
-dontobfuscate

# Temporary measure to keep config functional, beware Jackson issues if removing!!
-dontoptimize
