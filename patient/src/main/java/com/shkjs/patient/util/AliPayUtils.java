package com.shkjs.patient.util;

import android.app.Activity;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.orhanobut.logger.Logger;
import com.shkjs.patient.http.HttpProtocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.qiujuer.common.okhttp.core.HttpCallback;

import java.util.Map;

/**
 * Created by xiaohu on 2016/10/26.
 * <p>
 * 支付宝支付工具类
 */

public class AliPayUtils {

    public static void alipay(final Activity activity, final String orderInfo, final PayResultListener listener) {

        final PayTask task = new PayTask(activity);

        new Thread(new Runnable() {
            @Override
            public void run() {

                String resultStatus = null;
                String result = null;
                String memo = null;

                Map<String, String> rawResult = task.payV2(orderInfo, true);

                for (String key : rawResult.keySet()) {
                    if (TextUtils.equals(key, "resultStatus")) {
                        resultStatus = rawResult.get(key);
                    } else if (TextUtils.equals(key, "result")) {
                        result = rawResult.get(key);
                    } else if (TextUtils.equals(key, "memo")) {
                        memo = rawResult.get(key);
                    }
                }

                // 判断resultStatus 为9000则代表支付成功
                if (TextUtils.equals(resultStatus, "9000")) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSuccess();
                        }
                    });
                    uploadPayResult(result);
                } else {
                    final String finalMemo = memo;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFailed(finalMemo);
                        }
                    });
                }

            }
        }).start();

    }

    /**
     * 支付成功，上传支付结果
     *
     * @param result
     */
    private static void uploadPayResult(String result) {

        HttpCallback<String> callback = new HttpCallback<String>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {

            }

            @Override
            public void onSuccess(String response, int code) {
                Logger.d("upload success alipay ::" + response);
            }
        };

        HttpProtocol.aliPaySyncCall(result, callback);
    }

    public interface PayResultListener {
        public void onSuccess();

        public void onFailed(String result);
    }
}
