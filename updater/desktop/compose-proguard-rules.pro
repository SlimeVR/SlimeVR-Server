-optimizationpasses 5
-allowaccessmodification
-mergeinterfacesaggressively

-assumenosideeffects class java.io.PrintStream {
    public void println(java.lang.String);
    public void print(java.lang.String);
}

-repackageclasses ''
-allowaccessmodification

-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

 -keep class dev.slimevr.updater.MainKt { public static void main(java.lang.String[]); }
 -keep class org.jetbrains.skia.** { *; }
 -keep class org.jetbrains.skiko.** { *; }
 -keep class androidx.compose.ui.window.** { *; }
 -keep class androidx.compose.ui.res.** { *; }
 -keep class org.jetbrains.compose.resources.** { *; }
 -keep class *.Res { *; }
 -keep class *.Res$* { *; }
 -keepclassmembers class * { @org.jetbrains.compose.resources.InternalResourceApi *; }
 -keepclassmembers class * { @kotlinx.serialization.Serializable *; @kotlinx.serialization.SerialName *; }
 -keep class kotlinx.serialization.json.** { *; }
 -keep class com.sun.jna.** { *; }
 -keep class net.java.dev.jna.** { *; }
 -keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
 -keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
 -dontwarn **
