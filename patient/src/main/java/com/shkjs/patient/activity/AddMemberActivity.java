package com.shkjs.patient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.orhanobut.logger.Logger;
import com.raspberry.library.util.CheckUtils;
import com.raspberry.library.util.TextUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/10/10.
 * <p>
 * 添加家庭组成员
 */

public class AddMemberActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.member_submit_ll)
    LinearLayout addMemberLL;
    @Bind(R.id.member_result_ll)
    LinearLayout resultLL;
    @Bind(R.id.member_phone_et)
    EditText phoneNumET;
    @Bind(R.id.member_submit_btn)
    Button submitBtn;
    @Bind(R.id.member_result_return)
    Button returnBtn;
    @Bind(R.id.member_result_continue_add)
    Button continueBtn;

    private Toolbar toolbar;

    private String phoneStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_member);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.add_home_group_member);

        initData();
        initListener();

        setVisibility(true);
    }

    private void initData() {
    }

    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        submitBtn.setOnClickListener(this);
        returnBtn.setOnClickListener(this);
        continueBtn.setOnClickListener(this);

        phoneNumET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                phoneStr = TextUtils.getText(phoneNumET);
            }
        });
    }

    private void setVisibility(boolean isShow) {
        if (isShow) {
            addMemberLL.setVisibility(View.VISIBLE);
            resultLL.setVisibility(View.GONE);
        } else {
            addMemberLL.setVisibility(View.GONE);
            resultLL.setVisibility(View.VISIBLE);
        }
    }

    private void addMember() {
        if (TextUtils.isEmpty(phoneStr)) {
            ToastUtils.showToast(getResources().getString(R.string.username_hint));
            return;
        } else if (!CheckUtils.isPhoneNumberValid(phoneStr)) {
            ToastUtils.showToast(getResources().getString(R.string.username_right_hint));
            return;
        }

        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                Logger.e("HomeGroup", getString(R.string.add_home_group_member_failed) + e.getLocalizedMessage());
                ToastUtils.showToast(getString(R.string.add_home_group_member_failed));
            }

            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        //                        setVisibility(false);
                        setResult();
                        finish();
                    } else {
                        Logger.e("HomeGroup", getString(R.string.add_home_group_member_failed) + response.getMsg());
                        ToastUtils.showToast(getString(R.string.add_home_group_member_failed) + " " + response.getMsg
                                ());
                    }
                } else {
                    Logger.e("HomeGroup", getString(R.string.add_home_group_member_failed));
                    ToastUtils.showToast(getString(R.string.add_home_group_member_failed));
                }
            }
        };

        callback.setCancelable(false);
        callback.setContext(this);
        callback.setMainThread(true);

        //        HttpProtocol.addMember(DataCache.getInstance().getUserInfo().getId(), phoneStr, callback);
        HttpProtocol.addMemberByUserName(phoneStr, callback);
    }

    /**
     * 添加成功后，设置返回值
     */
    private void setResult() {
        Intent intent = new Intent();
        intent.putExtra(AddMemberActivity.class.getSimpleName(), true);
        setResult(RESULT_OK, intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.member_submit_btn:
                addMember();
                break;
            case R.id.member_result_return:
                finish();
                break;
            case R.id.member_result_continue_add:
                setVisibility(true);
                break;
            default:
                break;
        }
    }
}
