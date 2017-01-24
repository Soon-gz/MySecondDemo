package com.shkjs.patient.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.widget.Toast;

import com.google.zxing.Result;
import com.orhanobut.logger.Logger;
import com.raspberry.library.qrcode.QRCode2ScanActivity;
import com.raspberry.library.qrcode.decode.Utils;

/**
 * Created by xiaohu on 2016/10/12.
 */

public class QRCodeActivity extends QRCode2ScanActivity {

    private static final int REQUEST_CODE = 234;
    private String photo_path;

    @Override
    public void handleDecode(Result rawResult, Bundle bundle) {
        super.handleDecode(rawResult, bundle);
        inactivityTimer.onActivity();
        beepManager.playBeepSoundAndVibrate();

        Intent intent = new Intent(QRCodeActivity.this, ResultActivity.class);
        String recode = recode(rawResult.getText());
        Logger.d(recode);
        if (recode.contains("?recode=")) {
            recode = recode.substring(recode.indexOf("?recode=") + "?recode=".length());
        }
        intent.putExtra(ResultActivity.class.getSimpleName(), recode);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        startActivity(intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case REQUEST_CODE:

                    String[] proj = {MediaStore.Images.Media.DATA};
                    // 获取选中图片的路径
                    Cursor cursor = getContentResolver().query(data.getData(), proj, null, null, null);

                    if (cursor.moveToFirst()) {

                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        photo_path = cursor.getString(column_index);
                        if (photo_path == null) {
                            photo_path = Utils.getPath(getApplicationContext(), data.getData());
                            Logger.i("123path  Utils", photo_path);
                        }
                        Logger.i("123path", photo_path);
                    }

                    cursor.close();

                    new Thread(new Runnable() {

                        @Override
                        public void run() {

                            Result result = scanningImage(photo_path);
                            // String result = decode(photo_path);
                            if (result == null) {
                                Logger.i("123", "   -----------");
                                Looper.prepare();
                                Toast.makeText(getApplicationContext(), "图片格式有误", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            } else {
                                Logger.i("tag00", result.toString());
                                String recode = recode(result.toString());
                                if (recode.contains("?recode=")) {
                                    recode = recode.substring(recode.indexOf("?recode=") + "?recode=".length());
                                }
                                Intent intent = new Intent(QRCodeActivity.this, ResultActivity.class);
                                intent.putExtra(ResultActivity.class.getSimpleName(), recode);
                                intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }).start();
                    break;

            }

        }
    }
}
