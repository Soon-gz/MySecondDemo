package com.shkjs.patient.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.raspberry.library.util.DisplayUtils;
import com.raspberry.library.util.InputMethodUtils;
import com.raspberry.library.util.SharedPreferencesUtils;
import com.shkjs.patient.LoginManager;
import com.shkjs.patient.Preference;
import com.shkjs.patient.R;
import com.shkjs.patient.application.MyApplication;
import com.shkjs.patient.cache.DataCache;
import com.shkjs.patient.util.ActivityManager;


/**
 * Created by xiaohu on 2016/9/11.
 * <p>
 * 基础activity
 */

public class BaseActivity extends AppCompatActivity {

    private IntentFilter intentFilter;
    protected boolean queryUserInfo = true;
    protected int statusBarHetght = 0;
    private TextView title;
    private ImageButton menuBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(this.getClass().getSimpleName() + " :onCreate");
        //将activity放入自定义管理栈中
        ActivityManager.getInstance().putActivity(this);
        //透明化状态栏
        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//Android 5.0及以上
        //            View decorView = getWindow().getDecorView();
        //            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        //            decorView.setSystemUiVisibility(option);
        //            getWindow().setStatusBarColor(Color.TRANSPARENT);
        //        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//Android 4.4到5.0
        //            WindowManager.LayoutParams params = getWindow().getAttributes();
        //            params.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | params.flags);
        //        }
        statusBarHetght = DisplayUtils.getStatusBarHeight2(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d(this.getClass().getSimpleName() + " :onResume");
        intentFilter = new IntentFilter();
        intentFilter.addAction(Preference.UPDATE_VIEW_ACTION);
        registerReceiver(receiver, intentFilter);
        if (queryUserInfo) {
            LoginManager.queryUserInfo(null);
            if (!DataCache.getInstance().isLoginNim() && SharedPreferencesUtils.getBoolean(MyApplication
                    .IS_AUTO_LOGIN)) {
                LoginManager.loginNim(this, null);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.d(this.getClass().getSimpleName() + " :onPause");
        unregisterReceiver(receiver);
        intentFilter = null;
        InputMethodUtils.hideSoftInput(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.d(this.getClass().getSimpleName() + " :onDestroy");
        //将activity从自定义管理栈中移除
        ActivityManager.getInstance().finishActivity(this);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Preference.UPDATE_VIEW_ACTION)) {
                updateView(intent);
            }
        }
    };

    /**
     * 每个需要自动更新界面的activity覆写该方法
     *
     * @param data 数据
     */
    protected void updateView(Intent data) {
    }

    /**
     * 初始化toolbar
     *
     * @param id          totlbar控件的ID
     * @param titleid     显示title的控件ID
     * @param titleString title内容
     * @return
     */
    protected Toolbar initToolbar(int id, int titleid, int titleString) {
        return initToolbar(id, titleid, titleString, -1);
    }

    /**
     * 初始化toolbar
     *
     * @param id          totlbar控件的ID
     * @param titleid     显示title的控件ID
     * @param titleString title内容
     * @param menu        菜单图标
     * @return
     */
    protected Toolbar initToolbar(int id, int titleid, int titleString, int menu) {
        Toolbar toolbar = (Toolbar) findViewById(id);
        title = (TextView) findViewById(titleid);
        title.setText(titleString);
        menuBtn = (ImageButton) findViewById(R.id.toolbar_menu);
        setSupportActionBar(toolbar);
        if (null != menuBtn) {
            if (menu > 0) {
                menuBtn.setVisibility(View.VISIBLE);
                menuBtn.setBackgroundResource(menu);
            } else {
                menuBtn.setVisibility(View.GONE);
            }
        }
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);//原始title不显示
        }
        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        //            toolbar.setPadding(toolbar.getPaddingLeft(), statusBarHetght, toolbar.getPaddingRight(), toolbar
        //                    .getPaddingBottom());
        //        }
        return toolbar;
    }

    /**
     * 设置标题
     *
     * @param title
     */
    protected void setToolbarTitle(String title) {
        if (null != this.title) {
            this.title.setText(title);
        }
    }

    /**
     * 设置标题
     *
     * @param id
     */
    protected void setToolbarTitle(int id) {
        if (null != this.title) {
            this.title.setText(id);
        }
    }

    /**
     * 右侧自定义菜单键点击事件
     *
     * @param listener
     */
    protected void setMenuBtnClickListener(View.OnClickListener listener) {
        if (null != menuBtn) {
            menuBtn.setOnClickListener(listener);
        }
    }
}
