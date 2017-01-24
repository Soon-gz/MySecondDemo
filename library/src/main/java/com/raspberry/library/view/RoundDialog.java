package com.raspberry.library.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.raspberry.library.R;

/**
 * Created by xiaohu on 2016/10/18.
 */

public class RoundDialog extends AlertDialog {

    public RoundDialog(@NonNull Context context) {
        this(context, R.style.roundDialog);
    }

    public RoundDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    public RoundDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }
}
