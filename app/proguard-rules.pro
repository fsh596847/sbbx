# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /AndroidContent/AndroidSDK/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:

#==================================================================================================

## Proguard Official Website
# http://proguard.sourceforge.net/

## 名词定义

# 垃圾注释：
# 写和没写没区别，很简单的单词或语句没必要再翻译。
# 比如ignore warnings，我们作为开发人员不认识么？

# 非垃圾注释：
# 目标类、方法、变量或者命令语句是干嘛用的？（一眼看不明白的）
# 作用是什么？（推荐写这种）
# 或者一段代码的处理逻辑（多用于方法注释）

##---------------Begin: proguard configuration common for all Android apps ----------

# 忽略警告（垃圾注释）
# 如下为非垃圾注释
# 目的：忽略proguard优化代码时输出的警告，否则签名打包失败。
-ignorewarnings

# 不警告（垃圾注释，同上）
# 如下为非垃圾注释
# dontwarn意义见顶部官网Usage项
# “-dontwarn”后面是正则表达式，目的为忽略你指定的包或者类的警告。
# 此处为何不需要“dontwarn”？
# 由于上面“-ignorewarnings”语句忽略所有警告，故“dontwarn”不需要。
#-dontwarn org.**
#-dontwarn sean.transfer.**

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-dump class_files.txt
-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
# http://stackoverflow.com/a/35798565/3171537
-optimizations optimization_filter

-allowaccessmodification
-keepattributes *Annotation*
# adding this in to preserve line numbers so that the stack traces
# can be remapped
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-repackageclasses ''

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends java.lang.Throwable {*;}
-keep public class * extends java.lang.Exception {*;}
-dontnote com.android.vending.licensing.ILicensingService

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Preserve all native method names and the names of their classes.
-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Preserve static fields of inner classes of R classes that might be accessed
# through introspection.
-keepclassmembers class **.R$* {
  public static <fields>;
}

# Preserve the special static methods that are required in all enumeration classes.
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep public class * {
	public protected *;
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# --------------------------------------------------------------------
# REMOVE all Log messages except warnings and errors
# --------------------------------------------------------------------
-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** i(...);
    public static *** d(...);
}

#-assumenosideeffects class android.util.Log {
#   public static boolean isLoggable(java.lang.String,int);
#   public static int v(...);
#   public static int i(...);
#   public static int w(...);
#	  public static int d(...);
#	  public static int e(...);
#}

##---------------End: proguard configuration common for all Android apps ----------

# remove custom logging wrapper
#-assumenosideeffects class com.john.testlog.MyLogger {*;}

#Custom View
-keep class com.uh.rdsp.view.**{*;}

#Dependency Library
-keep class org.sufficientlysecure.htmltextview.**{*;}
-keep class cn.qqtheme.framework.**{*;}
-keep class net.simonvt.menudrawer.**{*;}
-keep class com.zhl.cbdialog.**{*;}
-keep class com.soundcloud.lightcycle.**{*;}

# WebView JS Interface
#-keepclassmembers class com.aft.fastrepair.presentation.view.fragment.BaseWebViewFragment_Refactor$BaseWebAppInterface {
#   public *;
#}
#-keepclassmembers class com.aft.fastrepair.presentation.view.fragment.OrderFragment$WebAppInterface {
#   public *;
#}
#-keepclassmembers class com.aft.fastrepair.presentation.view.fragment.DiscoverFragment_New$WebAppInterface {
#   public *;
#}

# dependency module
#-keep class com.john.testlog.** {*;}
-keep class org.joda.time.** {*;}
-keep class org.apache.commons.lang3.** {*;}
-keep class org.apache.http.entity.mime.** {*;}
#-keep class com.jwdev.library.** {*;}
#-dontwarn com.aft.fastrepair.data.repository.**
-dontwarn org.joda.time.**
-dontwarn com.jwdev.library.**
-dontwarn com.rey.material.**
-dontwarn com.ta.utdid2.**
-dontwarn org.apache.http.**

-keep class android.support.** {*;}
-keep class com.android.support.test.** {*;}

-keep class org.aspectj.** {*;}
-keep class de.hdodenhof.** {*;}
-keep class dagger.** {*;}
-keep class com.google.dexmaker.** {*;}
-keep class com.fourmob.datetimepicker.** {*;}
-keep class com.sleepbot.datetimepicker.** {*;}
-keep class com.wefika.flowlayout.** {*;}
-keep class org.hamcrest.** {*;}
-keep class javax.** {*;}
-keep class fr.tvbarthel.lib.blurdialogfragment.** {*;}
-keep class com.rengwuxian.materialedittext.** {*;}
-keep class com.nineoldandroids.** {*;}
-keep class com.rey.material.** {*;}
-keep class org.mockito.** {*;}
-keep class org.objenesis.** {*;}

-dontwarn okio.**
-dontwarn com.squareup.**
-keep class okio.** {*;}
-keep class com.squareup.okhttp.** {*;}
-keep interface com.squareup.okhttp.** {*;}
#-keep class com.squareup.okhttp.internal.** {*;}
-keep class com.squareup.otto.** {*;}
-keep class com.squareup.phrase.** {*;}

# Picasso
-dontwarn com.squareup.okhttp.**

-keep class timber.log.** {*;}

## ----------------------------------
##   ######## UIL ########
## ----------------------------------
-keep class com.nostra13.universalimageloader.** {*;}
-keepclassmembers class com.nostra13.universalimageloader.** {*;}

## ----------------------------------
##   ######## Gson ########
## ----------------------------------
#-keepattributes Signature
#-keep class sun.misc.Unsafe {*;}
#-keep class com.google.gson.** { *; }
#-keep class com.google.gson.stream.** { *; }
#-keep class com.google.gson.examples.android.model.** {*;}

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-keep class sun.misc.Unsafe {*;}
#-keep class com.google.gson.** {*;}
#-keep class com.google.gson.stream.** {*;}

# Application classes that will be serialized/deserialized over Gson
-keep class com.uh.rdsp.bean.** {*;}
-keep class com.uh.rdsp.home.pay.bean.** {*;}

##---------------End: proguard configuration for Gson  ----------

## ----------------------------------
##   ######## ButterKnife ########
## ----------------------------------
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

## ----------------------------------
##   ######## LeakCanary ########
## ----------------------------------
-keep class org.eclipse.mat.** { *; }
-keep class com.squareup.leakcanary.** { *; }

## ----------------------------------
##   ######## Parcel ########
## ----------------------------------
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keep class org.parceler.Parceler$$Parcels

## ----------------------------------
##   ######## Retrofit ########
## ----------------------------------
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions
# http://stackoverflow.com/questions/22881681/proguard-no-longer-works-with-retrofit
#-keepclasseswithmembers class * {
#    @retrofit.http.* <methods>;
#}

## ----------------------------------
##   ######## Retrofit2 ########
## ----------------------------------
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

## ----------------------------------
##   ######## LoganSquare ########
## ----------------------------------
-keep class com.bluelinelabs.logansquare.** { *; }
-keep @com.bluelinelabs.logansquare.annotation.JsonObject class *
-keep class **$$JsonObjectMapper { *; }

## ----------------------------------
##   ######## RxJava ########
## ----------------------------------
-keep class rx.** { *; }
-dontwarn rx.**
-dontwarn org.mockito.**
-dontwarn org.junit.**
-dontwarn org.robolectric.**

## ----------------------------------
##   ######## RxAndroid ########
## ----------------------------------
# https://github.com/ReactiveX/RxAndroid/issues/219
#-keep class rx.internal.util.unsafe.** { *; }

## ----------------------------------
##   ######## Otto ########
## ----------------------------------
-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

## ----------------------------------
##   ######## Alipay ########
## ----------------------------------

# java.io.IOException: The same input jar [/AndroidContent/AndroidStudio_Workspace/AFT_Company/AFanTiFastRepair_Branch_ZF/presentation/libs/alipaySDK-20150724.jar] is specified twice.
#-libraryjars libs/alipaySDK-20150724.jar

-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-dontwarn com.alipay.**

## ----------------------------------
##   ######## Wechat pay ########
## ----------------------------------
-keep class com.tencent.mm.sdk.** {
   *;
}

# PhotoPicker
-keep public class me.iwf.photopicker.**{*;}
# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
# nineoldandroids
-keep interface com.nineoldandroids.view.** { *; }
-dontwarn com.nineoldandroids.**
-keep class com.nineoldandroids.** { *; }
# support-v7-appcompat
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}
# support-design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

# GreenDao
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
    public static java.lang.String TABLENAME;
}
-keep class **$Properties
-keepattributes EnclosingMethod

# lombok
-dontwarn lombok.**

# 科大讯飞
-dontwarn com.iflytek.speech.**
-keep class com.iflytek.speech.**{*;}

# Bugly
-keep public class com.tencent.bugly.**{*;}

# 信鸽
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep class com.tencent.android.tpush.**  {* ;}
-keep class com.tencent.mid.**  {* ;}

# ShareSDK
-keep class android.net.http.SslError
-keep class android.webkit.**{*;}
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class com.alipay.share.sdk.**{*;}

# 微信支付
-dontwarn com.tencent.mm.**
-dontwarn com.tencent.wxop.stat.**
-keep class com.tencent.mm.** {*;}
-keep class com.tencent.wxop.stat.**{*;}