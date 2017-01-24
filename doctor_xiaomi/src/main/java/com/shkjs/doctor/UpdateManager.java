package com.shkjs.doctor;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.raspberry.library.util.AppUtils;
import com.raspberry.library.util.DisplayUtils;
import com.raspberry.library.util.IntentUtils;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.raspberry.library.view.RoundDialog;
import com.shkjs.doctor.bean.DownloadAddress;
import com.shkjs.doctor.bean.Page;
import com.shkjs.doctor.bean.Version;
import com.shkjs.doctor.data.PhoneTerminalType;
import com.shkjs.doctor.data.VersionType;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ListPageResponse;
import com.shkjs.doctor.util.ActivityManager;

/**
 * Created by xiaohu on 2017/1/5.
 * <p>
 * APP更新管理
 */

public class UpdateManager {

    public static void updateDialog(final Activity activity, final Version version) {
        if (null != activity && null != version && isNewVersion(version.getCode(), AppUtils.getVersionName(activity))) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("tag00","更新类型："+version.getVersionType().getMark());
                    if (version.getVersionType().equals(VersionType.FORCE)) {
                        createDownloadDialog(activity, version, true);
                    } else {
                        createDownloadDialog(activity, version, false);
                    }
                }
            });
        }
    }

    private static void createDownloadDialog(final Activity activity, final Version version, boolean isForce) {
        if (!isForce && !SharedPreferencesUtils.getBoolean(version.getCode())  ) {
            return;
        }
        final AlertDialog dialog = new RoundDialog(activity);
        dialog.setCancelable(false);

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_download, null);
        TextView contentTV = (TextView) view.findViewById(R.id.content_tv);
        Button cancelBtn = (Button) view.findViewById(R.id.cancel_btn);
        Button downloadBtn = (Button) view.findViewById(R.id.download_btn);

        contentTV.setText(version.getDescribe());

        if (isForce) {
            cancelBtn.setVisibility(View.GONE);
            downloadBtn.setBackgroundResource(R.drawable.shape_btn_blue_bottom_style);
        } else {
            cancelBtn.setVisibility(View.VISIBLE);
            cancelBtn.setBackgroundResource(R.drawable.shape_btn_gray_left_style);
            downloadBtn.setBackgroundResource(R.drawable.shape_btn_blue_right_style);
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferencesUtils.put(version.getCode(), false);
                    dialog.dismiss();
                }
            });
        }

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryDownloadUrls(activity);
                dialog.dismiss();
            }
        });

        dialog.setView(view);
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = DisplayUtils.getScreenWidth(activity) - 2 * DisplayUtils.dip2px(activity, 20);
        dialog.getWindow().setAttributes(params);
    }

    private static void queryDownloadUrls(final Activity activity) {
        RaspberryCallback<ListPageResponse<DownloadAddress>> callback = new
                RaspberryCallback<ListPageResponse<DownloadAddress>>() {

            @Override
            public void onSuccess(ListPageResponse<DownloadAddress> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response,code)) {
                    if (null != response.getData()) {
                        for (DownloadAddress address : response.getData()) {
                            if (address.getType().equals(PhoneTerminalType.ANDROID_DOCTOR)) {
                                IntentUtils.openUrl(activity, address.getAddress());
                                ActivityManager.getInstance().finishAllActivity();
                                System.exit(0);
                            }
                        }
                    }
                }
            }
        };
        callback.setContext(activity);
        callback.setMainThread(false);
        Page page = new Page();
        page.setPageSize(Integer.MAX_VALUE);
        HttpProtocol.queryDownloadUrls(page, callback);
    }

    /**
     * 判断是否为新版本
     *
     * @param code    版本号  例如 1.0.1
     * @param nowCode 当前版本号  例如 1.0.1
     * @return false
     */
    private static boolean isNewVersion(String code, String nowCode) {
        Log.i("tag00","服务器版本："+code + "   当前手机版本："+nowCode);
        String[] codes = code.split("\\.");
        String[] nowCodes = nowCode.split("\\.");
        if (null != codes && null != nowCodes) {
            if (Integer.parseInt(codes[0]) > Integer.parseInt(nowCodes[0])) {
                return true;
            } else if (Integer.parseInt(codes[0]) < Integer.parseInt(nowCodes[0])) {
                return false;
            } else {
                if (Integer.parseInt(codes[1]) > Integer.parseInt(nowCodes[1])) {
                    return true;
                } else if (Integer.parseInt(codes[1]) < Integer.parseInt(nowCodes[1])) {
                    return false;
                } else {
                    if (Integer.parseInt(codes[2]) > Integer.parseInt(nowCodes[2])) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }
}
