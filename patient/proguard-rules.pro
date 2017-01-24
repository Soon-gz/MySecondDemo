	#指定代码的压缩级别
    -optimizationpasses 5

    #包明不混合大小写
    -dontusemixedcaseclassnames

    #不去忽略非公共的库类
    -dontskipnonpubliclibraryclasses

     #优化  不优化输入的类文件
    -dontoptimize

     #预校验
    -dontpreverify

     #混淆时是否记录日志
    -verbose

     # 混淆时所采用的算法
    -optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

    #保护注解
    -keepattributes *Annotation*

    # 保持内部类
    -keepattributes InnerClasses,EnclosingMethod
    -optimizations optimization_filter

    # 保持哪些类不被混淆
    -keep public class * extends android.app.Fragment
    -keep public class * extends android.app.Activity
    -keep public class * extends android.app.Application
    -keep public class * extends android.app.Service
    -keep public class * extends android.content.BroadcastReceiver
    -keep public class * extends android.content.ContentProvider
    -keep public class * extends android.app.backup.BackupAgentHelper
    -keep public class * extends android.preference.Preference
    -keep public class com.android.vending.licensing.ILicensingService
    #如果有引用v4包可以添加下面这行
    -keep public class * extends android.support.v4.app.Fragment

    #忽略警告
    -ignorewarning

    #抛出异常时保留代码行号，在异常分析中可以方便定位
    -keepattributes SourceFile,LineNumberTable

    ########记录生成的日志数据,gradle build时在本项目根目录输出########

    #apk 包内所有 class 的内部结构
    -dump class_files.txt
    #未混淆的类和成员
    -printseeds seeds.txt
    #列出从 apk 中删除的代码
    -printusage unused.txt
    #混淆前后的映射
    -printmapping mapping.txt

    ########记录生成的日志数据，gradle build时 在本项目根目录输出-end######


    #####混淆保护自己项目的部分代码以及引用的第三方jar包library#######

    ## ########## 网易云信 ##########
    -dontwarn com.netease.**
    -dontwarn io.netty.**
    -keep class com.netease.** {*;}
    #如果 netty 使用的官方版本，它中间用到了反射，因此需要 keep。如果使用的是我们提供的版本，则不需要 keep
    -keep class io.netty.** {*;}

    #如果你使用全文检索插件，需要加入
    -dontwarn java.nio.channels.SeekableByteChannel
    -dontwarn org.apache.lucene.**
    -keep class org.apache.lucene.** {*;}
    -keep class org.lukhnos.** {*;}
    -keep class org.tartarus.** {*;}

    #如果引用了v4或者v7包
    -dontwarn android.support.**

    ## ########## 个推 ##########
    -dontwarn com.igexin.**
    -keep class com.igexin.** {*;}

    ## ########## 腾讯Bugly ##########
    -dontwarn com.tencent.bugly.**
    -keep public class com.tencent.bugly.**{*;}

    ## ########## butterknife ##########
    -keep class butterknife.** { *; }
    -dontwarn butterknife.internal.**
    -keep class **$$ViewBinder { *; }

    -keepclasseswithmembernames class * {
        @butterknife.* <fields>;
    }

    -keepclasseswithmembernames class * {
        @butterknife.* <methods>;
    }

    ## ########## TakePhoto ##########
    -keep class com.jph.takephoto.** { *; }
    -dontwarn com.jph.takephoto.**

    -keep class com.darsh.multipleimageselect.** { *; }
    -dontwarn com.darsh.multipleimageselect.**

    -keep class com.soundcloud.android.crop.** { *; }
    -dontwarn com.soundcloud.android.crop.**

    ## ########## OkHttp ##########
    -dontwarn com.squareup.okhttp.**
    -keep class com.squareup.okhttp.** {*;}
    -keep interface com.squareup.okhttp.** {*;}
    -dontwarn okio.**

    ## ########## 支付宝 ##########
    -keep class com.alipay.android.app.IAlixPay{*;}
    -keep class com.alipay.android.app.IAlixPay$Stub{*;}
    -keep class com.alipay.android.app.IRemoteServiceCallback{*;}
    -keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
    -keep class com.alipay.sdk.app.PayTask{
        public *;
    }
    -keep class com.alipay.sdk.app.AuthTask{
        public *;
    }

    ## ########## luban压缩 ##########
    -keep public class top.zibin.luban.** {*;}

    ## ########## RxJava RxAndroid ##########
    -dontwarn sun.misc.**
    -keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
       long producerIndex;
       long consumerIndex;
    }
    -keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
        rx.internal.util.atomic.LinkedQueueNode producerNode;
    }
    -keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
        rx.internal.util.atomic.LinkedQueueNode consumerNode;
    }

    ## ########## Mob##########
    -keep class cn.smssdk.**{*;}
    -keep class com.mob.**{*;}

    ## ########## androidannotations##########
    -dontwarn org.springframework.**

    #如果用用到Gson解析包的，直接添加下面这几行就能成功混淆，不然会报错。
    #gson
    #-libraryjars libs/gson-2.2.2.jar
    -keepattributes Signature
    # Gson specific classes
    -keep class sun.misc.Unsafe { *; }
    # Application classes that will be serialized/deserialized over Gson
    -keep class com.google.gson.examples.android.model.** { *; }

    #Glide
    -keep public class * implements com.bumptech.glide.module.GlideModule
    -keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
              **[] $VALUES;
              public *;
            }
    #-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

    ####混淆保护自己项目的部分代码以及引用的第三方jar包library-end####

    #保持自定义控件类不被混淆
    -keep public class * extends android.view.View {
        public <init>(android.content.Context);
        public <init>(android.content.Context, android.util.AttributeSet);
        public <init>(android.content.Context, android.util.AttributeSet, int);
        public void set*(...);
    }

    -keepclasseswithmembers class * {
        public <init>(android.content.Context, android.util.AttributeSet);
    }

    #保持自定义控件类不被混淆
    -keepclassmembers class * extends android.app.Activity {
        public void *(android.view.View);
    }

    #保持 native 方法不被混淆
    -keepclasseswithmembernames class * {
        native <methods>;
    }

    #保持 Parcelable 不被混淆
    -keep class * implements android.os.Parcelable {
        public static final android.os.Parcelable$Creator *;
    }

    #保持 Serializable 不被混淆并且enum 类也不被混淆
    -keepclassmembers class * implements java.io.Serializable {
        static final long serialVersionUID;
        private static final java.io.ObjectStreamField[] serialPersistentFields;
        !static !transient <fields>;
        !private <fields>;
        !private <methods>;
        private void writeObject(java.io.ObjectOutputStream);
        private void readObject(java.io.ObjectInputStream);
        java.lang.Object writeReplace();
        java.lang.Object readResolve();
    }

    # 枚举类不能被混淆
    -keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    }

    -keepclassmembers class * {
        public void *ButtonClicked(android.view.View);
    }

    #不混淆资源类
    -keepclassmembers class **.R$* {
        public static <fields>;
    }

    #避免混淆泛型 如果混淆报错建议关掉
    #–keepattributes Signature



    ################  APP独有  ###############
    # 保留实体类和成员不被混淆
    -keep public class com.shkjs.patient.bean.** {
        public void set*(***);
        public *** get*();
        public *** is*();
    }
    -keep public class com.shkjs.patient.data.** {
        public void set*(***);
        public *** get*();
        public *** is*();
    }
    -keep public class com.shkjs.patient.data.push.** {
        public void set*(***);
        public *** get*();
        public *** is*();
    }
    -keep public class com.shkjs.patient.base.** {*;}


    # 对WebView的处理
    -keepclassmembers class * extends android.webkit.webViewClient {
        public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
         public boolean *(android.webkit.WebView, java.lang.String);
    }
    -keepclassmembers class * extends android.webkit.webViewClient {
        public void *(android.webkit.webView, java.lang.String);
    }