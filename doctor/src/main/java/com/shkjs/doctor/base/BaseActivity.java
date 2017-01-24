package com.shkjs.doctor.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.util.ActivityManager;
import com.raspberry.library.util.ScreenUtils;
import com.raspberry.library.util.SystemStatusManager;

/**
 * Created by xiaohu on 2016/9/11.
 * <p>
 * 基础activity
 */

public class BaseActivity extends AppCompatActivity {

    private IntentFilter intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将activity放入自定义管理栈中
        ActivityManager.getInstance().putActivity(this);
    }


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        setTranslucentStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        intentFilter = new IntentFilter();
        intentFilter.addAction(Preference.UPDATE_VIEW_ACTION);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        intentFilter = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //将activity从自定义管理栈中移除
        ActivityManager.getInstance().finishACtivity(this);
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
        Toolbar toolbar = (Toolbar) findViewById(id);
        TextView title = (TextView) findViewById(titleid);
        title.setText(titleString);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);//原始title不显示
        }
        return toolbar;
    }


    private void setTranslucentStatus() {
        //判断版本是4.4以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= bits;
            win.setAttributes(winParams);

            SystemStatusManager tintManager = new SystemStatusManager(this);
            //打开系统状态栏控制
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(R.color.color_blue_0888ff);
            tintManager.setStatusBarTintResource(R.color.color_blue_0888ff);//设置背景
            View layoutAll = findViewById(R.id.layoutAll);
            if (layoutAll != null) {
                //设置系统栏需要的内偏移
                layoutAll.setPadding(0, ScreenUtils.getStatusHeight(this), 0, 0);
            }
        }
    }

}
