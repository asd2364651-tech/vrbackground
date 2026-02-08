-keep class com.pico.** { *; }
-keep interface com.pico.** { *; }
-keep enum com.pico.** { *; }

-dontwarn com.pico.**

-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers class * {
    *** *_handler(...);
}
