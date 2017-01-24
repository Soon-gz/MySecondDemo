package com.shkjs.patient.http;

import android.content.Context;

import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.DialogMaker;
import com.shkjs.patient.activity.LoginActivity;
import com.shkjs.patient.application.MyApplication;
import com.shkjs.patient.base.BaseResponse;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.util.ActivityManager;
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

    private boolean isMainThread = false;//是否在主线程运行，默认不是
    private boolean cancelable = false;//是否能取消，默认不能
    private boolean isShow = true;//是否显示进度条

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
        UiKit.runOnMainThreadSync(new Runnable() {
            @Override
            public void run() {
                onProgress(current, count);
            }
        });
    }

    @Override
    public void onStart(Request request) {
        if (null != context && isShow) {
            DialogMaker.showProgressDialog(context, "", cancelable);
        }
    }

    @Override
    public void onFinish() {
        setShow(false);
        UiKit.dispose();
        DialogMaker.dismissProgressDialog();
    }

    @Override
    public void onFailure(Request request, Response response, Exception e) {
        setShow(false);
        UiKit.dispose();
        DialogMaker.dismissProgressDialog();
        ToastUtils.showToast("网络异常，请检查网络或重试!");
    }

    @Override
    public void onSuccess(T response, int code) {
        if (code == HttpBase.SUCCESS) {
            if (response.getStatus().equals(ResultStatus.UNLOGIN)) {
                //                //TODO 退出到登录界面,或者自动重新登录
                ActivityManager.getInstance().finishActivity();
                ActivityManager.getInstance().finishAllActivity();//未finish栈顶activity
                LoginActivity.start(MyApplication.getInstance().getApplicationContext());
                ToastUtils.showToast("请重新登录!");
                //                if (!DataCache.getInstance().isLogining()) {
                //                    final String userName = SharedPreferencesUtils.getString(MyApplication.USER_NAME);
                //                    final String userPwd = SharedPreferencesUtils.getString(MyApplication.USER_PWD);
                //                    LoginManager.loginServer(userName, userPwd);
                //                }
                //                ToastUtils.showToast("连接异常，请重试!");
            }
        }
        setShow(false);
        UiKit.dispose();
        DialogMaker.dismissProgressDialog();
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

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
