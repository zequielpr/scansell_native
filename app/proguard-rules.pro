# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
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
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class  com.kunano.scansell.model.*{*;}
-keep class com.kunano.scansell.ui.sales.admin.back_up.*{*;}

# Google Play Services
-keep class com.google.api.services.drive.** { *; }
-keep class com.google.auth.oauth2.** { *; }
-keep class com.google.auth.http.** { *; }
-keep class com.google.auth.oauth2.*$TypeAdapter
-keep class com.google.api.client.googleapis.services.* { *; }
-keep class com.google.api.client.googleapis.json.* { *; }
-keep class com.google.api.client.extensions.android.** { *; }
-keep class com.google.api.client.extensions.android.http.** { *; }
-keep class com.google.api.client.googleapis.extensions.android.gms.auth.** { *; }
-keep class com.google.api.services.drive.model.** { *; }
-keepclassmembers class * {
    @com.google.api.client.util.Key <fields>;
}
-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault
