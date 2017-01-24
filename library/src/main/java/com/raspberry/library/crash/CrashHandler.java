package com.raspberry.library.crash;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;

import com.orhanobut.logger.Logger;
import com.raspberry.library.util.ToastUtils;

/**
 * Created by xiaohu on 2016/12/7.
 * <p>
 * 全局异常捕获
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static CrashHandler instance;
    private Context context;
    private ExceptionHandlerListener listener;

    private CrashHandler() {
    }

    /**
     * 单例
     *
     * @return
     */
    public static CrashHandler getInstance() {
        if (null == instance) {
            synchronized (CrashHandler.class) {
                if (null == instance) {
                    instance = new CrashHandler();
                }
            }
        }
        return instance;
    }

    /**
     * 设置上下文和异常处理监听
     *
     * @param context  上下文
     * @param listener 异常处理监听
     */
    public void setCustomCrashHanler(Context context, ExceptionHandlerListener listener) {
        this.context = context;
        this.listener = listener;
        //崩溃时将catch住异常
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    //崩溃时触发
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (isHandler(ex)) {
            handlerException(ex);
        } else {
            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(thread, ex);
        }
    }

    /**
     * 是否需要自己处理异常
     *
     * @param ex
     * @return
     */
    private boolean isHandler(Throwable ex) {
        // 排序不需要捕获的情况
        if (ex == null) {
            return false;
        }
        return true;

    }

    /**
     * 自定义异常处理
     *
     * @param ex
     */
    private void handlerException(final Throwable ex) {
        if (null != listener) {
            listener.handler(ex);
        }
    }

    public interface ExceptionHandlerListener {
        void handler(Throwable ex);
    }
}
