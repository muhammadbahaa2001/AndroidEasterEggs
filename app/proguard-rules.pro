# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontwarn org.bouncycastle.jsse.**
-dontwarn org.conscrypt.**
-dontwarn org.openjsse.**

-keepnames class * extends com.dede.android_eggs.ui.adapter.VHolder
-keepclassmembers class * extends com.dede.android_eggs.ui.adapter.VHolder {
    public <init>(android.view.View);
}

# temp fix
-keep class androidx.activity.OnBackPressedDispatcher {
    private android.window.OnBackInvokedCallback onBackInvokedCallback;
}