/*
 * Copyright (C) 2016 venshine.cn@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.raspberry.library.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import static android.R.attr.targetSdkVersion;

/**
 * App 相关信息，包括版本名称、版本号、包名等等
 *
 * @author venshine
 */
public class AppUtils {

    private final static X500Principal DEBUG_DN = new X500Principal("CN=Android Debug,O=Android,C=US");

    /**
     * Get version name
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        PackageInfo info;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Get version code
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageInfo info;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get package name
     *
     * @param context
     * @return
     */
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    /**
     * Get icon
     *
     * @param context
     * @return
     */
    public static Drawable getIcon(Context context) {
        return getAppIcon(context, getPackageName(context));
    }

    /**
     * Get app icon
     *
     * @param context
     * @param packageName
     * @return
     */
    public static Drawable getAppIcon(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
            return info.loadIcon(pm);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get app version name
     *
     * @param context
     * @param packageName
     * @return
     */
    public static String getAppVersionName(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get app version code
     *
     * @param context
     * @param packageName
     * @return
     */
    public static int getAppVersionCode(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Get app name
     *
     * @param context
     * @param packageName
     * @return
     */
    public static String getAppName(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
            return info.loadLabel(pm).toString();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get app permission
     *
     * @param context
     * @param packageName
     * @return
     */
    public static String[] getAppPermission(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            return packageInfo.requestedPermissions;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get app signature
     *
     * @param context
     * @param packageName
     * @return
     */
    public static String getAppSignature(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            return packageInfo.signatures[0].toCharsString();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Judge whether an app is dubuggable
     *
     * @param ctx
     * @return
     */
    public static boolean isDebuggable(Context ctx) {
        boolean debuggable = false;
        try {
            PackageInfo pinfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager
                    .GET_SIGNATURES);
            Signature signatures[] = pinfo.signatures;
            for (int i = 0; i < signatures.length; i++) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                ByteArrayInputStream stream = new ByteArrayInputStream(signatures[i].toByteArray());
                X509Certificate cert = (X509Certificate) cf.generateCertificate(stream);
                debuggable = cert.getSubjectX500Principal().equals(DEBUG_DN);
                if (debuggable) {
                    break;
                }
            }

        } catch (NameNotFoundException e) {
        } catch (CertificateException e) {
        }
        return debuggable;
    }

    /**
     * Judge whether an app is in background
     *
     * @param context
     * @return
     */
    public static boolean isAppInBackground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(1);
        if (taskList != null && !taskList.isEmpty()) {
            ComponentName topActivity = taskList.get(0).topActivity;
            if (topActivity != null && !topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前进程名
     *
     * @param context
     * @return 进程名
     */
    public static final String getProcessName(Context context) {
        String processName = null;

        // ActivityManager
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));

        while (true) {
            for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
                if (info.pid == android.os.Process.myPid()) {
                    processName = info.processName;

                    break;
                }
            }

            // go home
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }

            // take a rest and again
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 判断当前应用是否是debug状态
     *
     * @param context
     * @return
     */
    public static boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检测是否有某项权限
     *
     * @param context
     * @param permission
     * @return
     */
    public boolean selfPermissionGranted(Context context, String permission) {
        // Android 6.0 以前，全部默认授权
        boolean result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (targetSdkVersion >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= 23, 使用Context#checkSelfPermission
                result = context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < 23, 需要使用 PermissionChecker
                result = PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker
                        .PERMISSION_GRANTED;
            }
        }
        return result;
    }


    /**
     * 安装App(支持6.0)
     *
     * @param context  上下文
     * @param filePath 文件路径
     */
    public static void installApp(Context context, String filePath) {
        if (StringUtils.isEmpty(filePath))
            return;
        installApp(context, new File(filePath));
    }

    /**
     * 安装App（支持6.0）
     *
     * @param context 上下文
     * @param file    文件
     */
    public static void installApp(Context context, File file) {
        if (null == file || !file.exists())
            return;
        context.startActivity(IntentUtils.getInstallAppIntent(file));
    }

    /**
     * 安装App（支持6.0）
     *
     * @param activity    activity
     * @param filePath    文件路径
     * @param requestCode 请求值
     */
    public static void installApp(Activity activity, String filePath, int requestCode) {
        if (StringUtils.isEmpty(filePath))
            return;
        installApp(activity, new File(filePath), requestCode);
    }

    /**
     * 安装App(支持6.0)
     *
     * @param activity    activity
     * @param file        文件
     * @param requestCode 请求值
     */
    public static void installApp(Activity activity, File file, int requestCode) {
        if (null == file || !file.exists())
            return;
        activity.startActivityForResult(IntentUtils.getInstallAppIntent(file), requestCode);
    }
}
