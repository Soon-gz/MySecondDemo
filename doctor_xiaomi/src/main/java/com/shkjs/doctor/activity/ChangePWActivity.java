package com.shkjs.doctor.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.raspberry.library.util.MD5Utils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.base.BaseResponse;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePWActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;
    @Bind(R.id.change_pwd_old_edittext_tv)
    EditText change_pwd_old_edittext_tv;
    @Bind(R.id.change_pwd_new_edittext_tv)
    EditText change_pwd_new_edittext_tv;
    @Bind(R.id.change_pwd_new_edittext_again_tv)
    EditText change_pwd_new_edittext_again_tv;

    private RaspberryCallback<BaseResponse>callback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pw);
        ButterKnife.bind(this);
        toptitle_tv.setText("修改登录密码");

        initListener();
    }

    private void initListener() {
        callback =  new RaspberryCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse response, int code) {
                super.onSuccess(response, code);
                ToastUtils.showToast("修改密码成功!");
                finish();
            }
        };
        callback.setContext(this);
        callback.setMainThread(true);
        callback.setShowDialog(true);
        callback.setCancelable(false);
    }

    @OnClick({R.id.back_iv,R.id.change_pwd_sure_tv})
    public void changeOnClick(View view){
        switch (view.getId()){
            case R.id.back_iv:
                finish();
                break;
            case R.id.change_pwd_sure_tv:
                if (isFinished()){
                    //发起网络请求，提交参数
                    HttpProtocol.modifyUserPW(callback, MD5Utils.encodeMD52(change_pwd_old_edittext_tv.getText().toString().trim()),MD5Utils.encodeMD52(change_pwd_new_edittext_tv.getText().toString().trim()));
                }
                break;

        }
    }

    /**
     * 检查是否填写完全
     * @return
     */
    public boolean isFinished(){
        if(change_pwd_old_edittext_tv.getText().toString().trim().equals("")){
            ToastUtils.showToast("请填写旧密码！");
            return false;
        }
        if(change_pwd_new_edittext_tv.getText().toString().trim().equals("")){
            ToastUtils.showToast("请填写新密码！");
            return false;
        }
        if(change_pwd_new_edittext_again_tv.getText().toString().trim().equals("")){
            ToastUtils.showToast("请再次输入新密码！");
            return false;
        }
        if (!change_pwd_new_edittext_again_tv.getText().toString().trim().equals(change_pwd_new_edittext_tv.getText().toString().trim())){
            ToastUtils.showToast("请确保两次输入的新密码一致！");
            return false;
        }
        return true;
    }
}
