package com.shkjs.doctor.http;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.netease.nim.uikit.common.util.string.StringUtil;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.DialogMaker;
import com.shkjs.doctor.activity.LoginActivity;
import com.shkjs.doctor.base.BaseResponse;
import com.shkjs.doctor.data.ResponseStatusEnum;
import com.shkjs.doctor.util.ActivityManager;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.qiujuer.common.okhttp.core.HttpCallback;
import net.qiujuer.genius.kit.util.UiKit;

/**
 * Created by xiaohu on 2016/9/28.
 * <p>
 * 便于统一处理未登录等情况
 */

public abstract class RaspberryCallback<T extends BaseResponse> extends HttpCallback<T> {

    private static final String MSG_STR_ZH = "需要登录";
    private static final String MSG_STR_EN = "needLogin";

    private boolean isMainThread = false;//是否在主线程运行，默认不是
    private boolean cancelable = false;//是否能取消，默认不能
    private boolean showDialog = true;//默认转圈

    public boolean isShowDialog() {
        return showDialog;
    }

    public void setShowDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }

    private Context context;

    @Override
    protected void dispatchFailure(final Request request, final Response response, final Exception e) {
        if (isMainThread) {
            UiKit.runOnMainThreadSync(new Runnable() {
                @Override
                public void run() {
                    onFailure(request, response, e);
                }
            });
        } else {
            onFailure(request, response, e);
        }
    }

    @Override
    protected void dispatchSuccess(final T response, final int code) {
        if (isMainThread) {
            UiKit.runOnMainThreadSync(new Runnable() {
                @Override
                public void run() {
                    onSuccess(response, code);
                }
            });
        } else {
            onSuccess(response, code);
        }
    }

    @Override
    public void dispatchProgress(final long current, final long count) {
        if (isMainThread) {
            UiKit.runOnMainThreadSync(new Runnable() {
                @Override
                public void run() {
                    onProgress(current, count);
                }
            });
        } else {
            onProgress(current, count);
        }
    }

    public void onStart(Request request) {
        if (null != context && showDialog) {
            DialogMaker.showProgressDialog(context, "", cancelable);
        }
    }

    public void onFinish() {
        DialogMaker.dismissProgressDialog();
    }

    @Override
    public void onFailure(Request request, Response response, Exception e) {
        DialogMaker.dismissProgressDialog();
        ToastUtils.showToast("网络访问失败，请检查网络是否连接！");
        Log.i("tag00","错误信息："+e.toString());
    }

    @Override
    public void onSuccess(T response, int code) {
        String msg = "服务器返回提示为空";
        if (response != null){
            msg = !StringUtil.isEmpty(response.getMsg()) ?response.getMsg():"服务器返回提示为空";
        }
        if (code == HttpBase.SUCCESS && response!= null) {
            //审核从认证或权威到未通过
//            HttpProtocol.loginBackgroudNotpass();
            Log.i("tag00","=================================新的网络请求================================");
            switch (ResponseStatusEnum.valueOf(response.getStatus())){
                case FAIL:
                    ToastUtils.showToast(msg);
                    Log.i("tag00",msg);
                    break;
                case EXCEPTION:
                    ToastUtils.showToast(msg);
                    Log.i("tag00",msg);
                    break;
                case SUCCEED:
                    break;
                case UNLOGIN:
                    ActivityManager.getInstance().finishActivity();
                    ActivityManager.getInstance().finishAllActivity();//未finish栈顶activity
                    if (!"LoginActivity".equals(context.getClass().getSimpleName())){
                        context.startActivity(new Intent(context,LoginActivity.class));
                    }
                    ToastUtils.showToast("请重新登录。");
                    Log.i("tag00",msg);
                    break;
                case NOTPERMISSION:
                    //审核通过时立刻生效
                    HttpProtocol.loginAutoBackgroud();
//                    ToastUtils.showToast(msg);
                    Log.i("tag00",msg);
                    break;
            }
        }else {
            Log.i("tag00","服务器访问异常，错误码："+code+"\n"+msg);
            ToastUtils.showToast(msg+"\n"+"服务器访问异常，错误码："+code);
            context.startActivity(new Intent(context,LoginActivity.class));
        }
        if (showDialog){
            DialogMaker.dismissProgressDialog();
        }
    }

    public boolean isMainThread() {
        return isMainThread;
    }

    public void setMainThread(boolean mainThread) {
        isMainThread = mainThread;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

}
