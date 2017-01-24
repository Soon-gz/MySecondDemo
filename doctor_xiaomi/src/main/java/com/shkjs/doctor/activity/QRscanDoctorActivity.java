package com.shkjs.doctor.activity;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.zxing.Result;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.raspberry.library.qrcode.QRCode2ScanActivity;
import com.raspberry.library.qrcode.decode.Utils;
import com.raspberry.library.util.DESUtils;
import com.raspberry.library.util.ScreenUtils;
import com.raspberry.library.util.SystemStatusManager;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.util.ActivityManager;

import java.util.List;

/**
 * Created by Administrator on 2016/10/12.
 */

public class QRscanDoctorActivity extends QRCode2ScanActivity {
    private static final int REQUEST_CODE = 234;
    private String photo_path;
    private String INTENT_TYPE;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setTranslucentStatus();
        INTENT_TYPE = getIntent().getStringExtra(Preference.QRSCAN_INTENT_TYPE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //当Android版本大于等于23时，动态授权
        Acp.getInstance(this).request(new AcpOptions.Builder().setPermissions(Manifest.permission
                        .READ_PHONE_STATE,Manifest.permission.CAMERA, Manifest.permission
                        .READ_EXTERNAL_STORAGE).build(), new AcpListener() {

            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(List<String> permissions) {
                ToastUtils.showToast(permissions.toString() + getResources().getString(R.string
                        .toast_permission_hint));
                //不给权限就不给你用
                ActivityManager.getInstance().finishAllActivity();
                finish();
            }
        });
    }

    @Override
    public void handleDecode(Result rawResult, Bundle bundle) {
        super.handleDecode(rawResult, bundle);
        inactivityTimer.onActivity();
        beepManager.playBeepSoundAndVibrate();

        Log.i("tag00","获取的字符串："+rawResult.getText());
        String resultStr = "";
        try {
            resultStr  = DESUtils.decodeUrl(rawResult.getText());
            bundle.putString("result", resultStr);
            bundle.putString(Preference.QRSCAN_INTENT_TYPE,INTENT_TYPE);
            Log.i("tag00","解析的字符串："+DESUtils.decodeUrl(rawResult.getText()));
            switch (INTENT_TYPE){
                case Preference.QRSCAN_INTENT_ADDPATIENT:
                    if (!DESUtils.decodeUrl(rawResult.getText()).contains("doctor")){
                        startActivity(new Intent(QRscanDoctorActivity.this, ResultActivity.class).putExtras(bundle));
                        ActivityManager.getInstance().finishActivity(MyPatientsActivity.class);
                    }else {
                        ToastUtils.showToast("此处无法添加医生！");
                    }
                    break;
                case Preference.QRSCAN_INTENT_CREATEAV_ADDPATIENT:
                    if (!DESUtils.decodeUrl(rawResult.getText()).contains("doctor")){
                        startActivity(new Intent(QRscanDoctorActivity.this, ResultActivity.class).putExtras(bundle));
                    }else {
                        ToastUtils.showToast("此处无法添加医生！");
                    }
                    break;
                case Preference.QRSCAN_INTENT_CREATEAV_ADDDOCTOR:
                    if (DESUtils.decodeUrl(rawResult.getText()).contains("doctor")){
                        startActivity(new Intent(QRscanDoctorActivity.this, ResultActivity.class).putExtras(bundle));
                    }else {
                        ToastUtils.showToast("此处无法添加患者！");
                    }
                    break;
            }
            finish();
        }catch (Exception e){
            finish();
            ToastUtils.showToast("二维码不是本平台生成！");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case REQUEST_CODE:

                    String[] proj = { MediaStore.Images.Media.DATA };
                    // 获取选中图片的路径
                    Cursor cursor = getContentResolver().query(data.getData(),
                            proj, null, null, null);

                    if (cursor.moveToFirst()) {

                        int column_index = cursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        photo_path = cursor.getString(column_index);
                        if (photo_path == null) {
                            photo_path = Utils.getPath(getApplicationContext(),
                                    data.getData());
                            Log.i("123path  Utils", photo_path);
                        }
                        Log.i("123path", photo_path);
                    }

                    cursor.close();

                    new Thread(new Runnable() {

                        @Override
                        public void run() {

                            Result result = scanningImage(photo_path);
                            // String result = decode(photo_path);
                            if (result == null) {
                                Log.i("123", "   -----------");
                                Looper.prepare();
                                Toast.makeText(getApplicationContext(), "图片格式有误",Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            } else {
                                Log.i("tag00", result.toString());
                                // 数据返回
                                String resultStr = "";
                                try {
                                    resultStr  = DESUtils.decodeUrl(result.getText());
                                    String recode = resultStr;
                                    Bundle bundle = new Bundle();
                                    bundle.putString("result", recode);
                                    Log.i("tag00",DESUtils.decodeUrl(result.getText()));
                                    bundle.putString(Preference.QRSCAN_INTENT_TYPE,INTENT_TYPE);
                                    switch (INTENT_TYPE){
                                        case Preference.QRSCAN_INTENT_ADDPATIENT:
                                            if (!DESUtils.decodeUrl(result.getText()).contains("doctor")){
                                                startActivity(new Intent(QRscanDoctorActivity.this, ResultActivity.class).putExtras(bundle));
                                                ActivityManager.getInstance().finishActivity(MyPatientsActivity.class);
                                            }else {
                                                ToastUtils.showToast("此处无法添加医生！");
                                            }
                                            break;
                                        case Preference.QRSCAN_INTENT_CREATEAV_ADDPATIENT:
                                            if (!DESUtils.decodeUrl(result.getText()).contains("doctor")){
                                                startActivity(new Intent(QRscanDoctorActivity.this, ResultActivity.class).putExtras(bundle));
                                            }else {
                                                ToastUtils.showToast("此处无法添加医生！");
                                            }
                                            break;
                                        case Preference.QRSCAN_INTENT_CREATEAV_ADDDOCTOR:
                                            if (DESUtils.decodeUrl(result.getText()).contains("doctor")){
                                                startActivity(new Intent(QRscanDoctorActivity.this, ResultActivity.class).putExtras(bundle));
                                            }else {
                                                ToastUtils.showToast("此处无法添加患者！");
                                            }
                                            break;
                                    }
                                    finish();
                                }catch (Exception e){
                                    finish();
                                    ToastUtils.showToast("二维码不是本平台生成！");
                                }
                            }
                        }
                    }).start();
                    break;

            }

        }
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
