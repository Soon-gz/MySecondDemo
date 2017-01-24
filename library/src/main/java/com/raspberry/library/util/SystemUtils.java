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

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.telephony.TelephonyManager;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * 系统信息
 *
 * @author venshine
 */
public class SystemUtils {

    /**
     * ART
     *
     * @return
     */
    public static boolean isART() {
        final String vmVersion = System.getProperty("java.vm.version");
        return vmVersion != null && vmVersion.startsWith("2");
    }

    /**
     * DALVIK
     *
     * @return
     */
    public static boolean isDalvik() {
        final String vmVersion = System.getProperty("java.vm.version");
        return vmVersion != null && vmVersion.startsWith("1");
    }

    /**
     * The brand (e.g., Xiaomi) the software is customized for, if any.
     *
     * @return
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    /**
     * The name of the underlying board, like "MSM8660_SURF".
     *
     * @return
     */
    public static String getBoard() {
        return Build.BOARD;
    }

    /**
     * The end-user-visible name for the end product, like "MI-ONE Plus".
     *
     * @return
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * Either a changelist number, or a label like "JZO54K".
     *
     * @return
     */
    public static String getID() {
        return Build.ID;
    }

    /**
     * The user-visible version string, like "4.1.2".
     *
     * @return
     */
    public static String getVersionRelease() {
        return Build.VERSION.RELEASE;
    }

    /**
     * The user-visible SDK version of the framework.
     *
     * @return
     */
    public static int getVersionSDK() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * A string that uniquely identifies this build. Do not attempt to parse this value.
     *
     * @return
     */
    public static String getFingerPrint() {
        return Build.FINGERPRINT;
    }

    /**
     * The name of the overall product, like "mione_plus".
     *
     * @return
     */
    public static String getProduct() {
        return Build.PRODUCT;
    }

    /**
     * The manufacturer of the product/hardware, like "Xiaomi".
     *
     * @return
     */
    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    /**
     * The name of the industrial design, like "mione_plus".
     *
     * @return
     */
    public static String getDevice() {
        return Build.DEVICE;
    }

    /**
     * The name of the instruction set (CPU type + ABI convention) of native code, like "armeabi-v7a".
     *
     * @return
     */
    public static String getCpuAbi() {
        return Build.CPU_ABI;
    }

    /**
     * The name of the second instruction set (CPU type + ABI convention) of native code, like "armeabi".
     *
     * @return
     */
    public static String getCpuAbi2() {
        return Build.CPU_ABI2;
    }

    /**
     * A build ID string meant for displaying to the user, like "JZO54K".
     *
     * @return
     */
    public static String getDisplay() {
        return Build.DISPLAY;
    }


    /**
     * 获取系统当前语言
     *
     * @param context
     * @return
     */
    public static String getLanguage(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        return locale.getLanguage();
    }

    public static final String getOsInfo() {
        return android.os.Build.VERSION.RELEASE;
    }

    public static final String getPhoneModelWithManufacturer() {
        return Build.MANUFACTURER + " " + android.os.Build.MODEL;
    }

    public static final String getPhoneMode() {
        return Build.MODEL;
    }

    public static final boolean isAppOnForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getApplicationContext().getSystemService(Context
                .ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> list = manager.getRunningAppProcesses();
        if (list == null)
            return false;
        boolean ret = false;
        Iterator<ActivityManager.RunningAppProcessInfo> it = list.iterator();
        while (it.hasNext()) {
            ActivityManager.RunningAppProcessInfo appInfo = it.next();
            if (appInfo.processName.equals(packageName) && appInfo.importance == ActivityManager
                    .RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                ret = true;
                break;
            }
        }
        return ret;
    }

    public static final boolean isScreenOn(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return powerManager.isScreenOn();
    }

    public static boolean stackResumed(Context context) {
        ActivityManager manager = (ActivityManager) context.getApplicationContext().getSystemService(Context
                .ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();
        List<ActivityManager.RunningTaskInfo> recentTaskInfos = manager.getRunningTasks(1);
        if (recentTaskInfos != null && recentTaskInfos.size() > 0) {
            ActivityManager.RunningTaskInfo taskInfo = recentTaskInfos.get(0);
            if (taskInfo.baseActivity.getPackageName().equals(packageName) && taskInfo.numActivities > 1) {
                return true;
            }
        }

        return false;
    }

    public static final boolean mayOnEmulator(Context context) {
        if (mayOnEmulatorViaBuild()) {
            return true;
        }

        if (mayOnEmulatorViaTelephonyDeviceId(context)) {
            return true;
        }

        if (mayOnEmulatorViaQEMU(context)) {
            return true;
        }

        return false;
    }

    private static final boolean mayOnEmulatorViaBuild() {
        /**
         * ro.product.model likes sdk
         */
        if (!android.text.TextUtils.isEmpty(Build.MODEL) && Build.MODEL.toLowerCase().contains("sdk")) {
            return true;
        }

        /**
         * ro.product.manufacturer likes unknown
         */
        if (!android.text.TextUtils.isEmpty(Build.MANUFACTURER) && Build.MANUFACTURER.toLowerCase().contains
                ("unknown")) {
            return true;
        }

        /**
         * ro.product.device likes generic
         */
        if (!android.text.TextUtils.isEmpty(Build.DEVICE) && Build.DEVICE.toLowerCase().contains("generic")) {
            return true;
        }

        return false;
    }

    private static final boolean mayOnEmulatorViaTelephonyDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) {
            return false;
        }

        String deviceId = tm.getDeviceId();
        if (android.text.TextUtils.isEmpty(deviceId)) {
            return false;
        }

        /**
         * device id of telephony likes '0*'
         */
        for (int i = 0; i < deviceId.length(); i++) {
            if (deviceId.charAt(i) != '0') {
                return false;
            }
        }

        return true;
    }

    private static final boolean mayOnEmulatorViaQEMU(Context context) {
        String qemu = getProp(context, "ro.kernel.qemu");
        return "1".equals(qemu);
    }

    private static final String getProp(Context context, String property) {
        try {
            ClassLoader cl = context.getClassLoader();
            Class<?> SystemProperties = cl.loadClass("android.os.SystemProperties");
            Method method = SystemProperties.getMethod("get", String.class);
            Object[] params = new Object[1];
            params[0] = property;
            return (String) method.invoke(SystemProperties, params);
        } catch (Exception e) {
            return null;
        }
    }
}
