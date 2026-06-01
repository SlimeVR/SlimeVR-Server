-dontobfuscate

-dontwarn io.ktor.**
-dontwarn net.java.dev.jna.**
-dontwarn com.github.ajalt.mordant.**
-dontwarn androidx.compose.**
-dontwarn org.jetbrains.compose.**

-keepattributes SourceFile,LineNumberTable,Signature,InnerClasses,EnclosingMethod,AnnotationDefault,*Annotation*

-keep class dev.slimevr.updater.MainKt { public static void main(java.lang.String[]); }

-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.** { *; }
-keep class net.java.dev.jna.** { *; }
-keep class com.sun.jna.platform.** { *; }
-dontwarn net.java.dev.jna.**

-keepattributes Signature, InnerClasses, EnclosingMethod, Deprecated, Annotation, SourceFile, LineNumberT

-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

-keep class com.github.ajalt.mordant.** { *; }
