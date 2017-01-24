package com.shkjs.patient.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.raspberry.library.util.MD5Utils;
import com.raspberry.library.util.TextUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.nim.recyclerview.BaseRecyclerViewHolder;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.base.BaseAdapter;
import com.shkjs.patient.bean.SecurityQuestion;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ListResponse;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/10/21.
 * <p>
 * 找回支付密码
 */

public class RetrievePayPwdActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    @Bind(R.id.security_ll)
    LinearLayout securityLL;
    @Bind(R.id.password_ll)
    LinearLayout passwordLL;
    @Bind(R.id.question_one_spinner)
    Spinner questionOne;//问题一
    @Bind(R.id.answer_one_tv)
    EditText answerOne;//答案一
    @Bind(R.id.question_two_spinner)
    Spinner questionTwo;//问题二
    @Bind(R.id.answer_two_tv)
    EditText answerTwo;//答案二
    @Bind(R.id.question_three_spinner)
    Spinner questionThree;//问题三
    @Bind(R.id.answer_three_tv)
    EditText answerThree;//答案三
    @Bind(R.id.submit_btn)
    Button submitBtn;
    @Bind(R.id.modify_pwd_newpwd_et)
    EditText newPasswordET;//
    @Bind(R.id.modify_pwd_newpwd_again_et)
    EditText newPasswordAgainET;//
    @Bind(R.id.modify_pwd_submit_btn)
    Button pwdSubmitBtn;

    private Toolbar toolbar;

    private List<SecurityQuestion> datalist;
    private List<SecurityQuestion> questionOneList;
    private List<SecurityQuestion> questionTwoList;
    private List<SecurityQuestion> questionThreeList;

    private BaseAdapter<SecurityQuestion> questionAdapterOne;
    private BaseAdapter<SecurityQuestion> questionAdapterTwo;
    private BaseAdapter<SecurityQuestion> questionAdapterThree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting_security_question);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.retrieve_pay_password);

        initData();
        initListener();

        querySecurityQuestions();

        showView(true);
    }

    private void initData() {
        datalist = new ArrayList<>();
        questionOneList = new ArrayList<>();
        questionTwoList = new ArrayList<>();
        questionThreeList = new ArrayList<>();

        questionAdapterOne = new BaseAdapter<SecurityQuestion>(this, questionOneList) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.spinner_item;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, final int position, final SecurityQuestion item) {
                holder.getTextView(R.id.content_tv).setText(item.getQuestion());
                holder.getView(R.id.content_divider).setVisibility(View.GONE);
            }
        };

        questionAdapterTwo = new BaseAdapter<SecurityQuestion>(this, questionTwoList) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.spinner_item;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, final int position, final SecurityQuestion item) {
                holder.getTextView(R.id.content_tv).setText(item.getQuestion());
                holder.getView(R.id.content_divider).setVisibility(View.GONE);
            }
        };

        questionAdapterThree = new BaseAdapter<SecurityQuestion>(this, questionThreeList) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.spinner_item;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, final int position, final SecurityQuestion item) {
                holder.getTextView(R.id.content_tv).setText(item.getQuestion());
                holder.getView(R.id.content_divider).setVisibility(View.GONE);
            }
        };
    }

    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        submitBtn.setOnClickListener(this);
        pwdSubmitBtn.setOnClickListener(this);

        questionOne.setAdapter(questionAdapterOne);
        questionTwo.setAdapter(questionAdapterTwo);
        questionThree.setAdapter(questionAdapterThree);

        questionOne.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                enableSubmit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        questionTwo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                enableSubmit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        questionThree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                enableSubmit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        answerOne.addTextChangedListener(this);
        answerTwo.addTextChangedListener(this);
        answerThree.addTextChangedListener(this);

    }

    /**
     * 创建设置密保body数据
     *
     * @return
     */
    private String createBodyStr() {
        String bodyStr = null;
        bodyStr = "list[0].id=" + ((SecurityQuestion) questionOne.getSelectedItem()).getId();
        bodyStr = bodyStr + "&list[0].answer=" + TextUtils.getText(answerOne);
        bodyStr = bodyStr + "&list[1].id=" + ((SecurityQuestion) questionTwo.getSelectedItem()).getId();
        bodyStr = bodyStr + "&list[1].answer=" + TextUtils.getText(answerTwo);
        bodyStr = bodyStr + "&list[2].id=" + ((SecurityQuestion) questionThree.getSelectedItem()).getId();
        bodyStr = bodyStr + "&list[2].answer=" + TextUtils.getText(answerThree);
        return bodyStr;
    }

    /**
     * 校验是否选择密保和填写密保答案
     *
     * @return
     */
    private boolean checkSecurity() {
        if (TextUtils.isEmpty(answerOne) || TextUtils.isEmpty(answerTwo) || TextUtils.isEmpty(answerThree)) {
            //            ToastUtils.showToast("请输入密保答案");
            return false;
        }
        return true;
    }

    /**
     * 校验是支付密码
     *
     * @return
     */
    private boolean checkPassword() {
        if (TextUtils.isEmpty(newPasswordET) || TextUtils.isEmpty(newPasswordAgainET)) {
            ToastUtils.showToast("请输入支付密码");
            return false;
        } else if (!TextUtils.equals(newPasswordET, newPasswordAgainET)) {
            ToastUtils.showToast("两次输入密码不同");
            return false;
        }
        return true;
    }

    /**
     * 按钮是否可点击
     */
    private void enableSubmit() {
        submitBtn.setEnabled(checkSecurity());
    }

    /**
     * 查询密保问题列表
     */
    private void querySecurityQuestions() {
        RaspberryCallback<ListResponse<SecurityQuestion>> callback = new
                RaspberryCallback<ListResponse<SecurityQuestion>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                finish();
            }

            @Override
            public void onSuccess(ListResponse<SecurityQuestion> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {

                        datalist.addAll(response.getData());

                        questionOneList.add(datalist.get(0));
                        questionTwoList.add(datalist.get(1));
                        questionThreeList.add(datalist.get(2));

                        questionAdapterOne.notifyDataSetChanged();
                        questionAdapterTwo.notifyDataSetChanged();
                        questionAdapterThree.notifyDataSetChanged();
                        return;
                    }
                }
                ToastUtils.showToast("查询密保问题失败，请重试！");
                finish();
            }
        };

        callback.setContext(this);
        callback.setCancelable(false);
        callback.setMainThread(true);

        HttpProtocol.querySecurityQuestion(callback);
    }

    /**
     * 校验密保
     */
    private void checkSecurityQuestions() {
        RaspberryCallback<ObjectResponse<Boolean>> callback = new RaspberryCallback<ObjectResponse<Boolean>>() {

            @Override
            public void onSuccess(ObjectResponse<Boolean> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        showView(false);
                        return;
                    }
                }
                ToastUtils.showToast("密保验证未通过，请核对问题和答案");
            }
        };

        callback.setContext(this);
        callback.setCancelable(false);
        callback.setMainThread(true);

        HttpProtocol.checkSecurityQuestion(createBodyStr(), callback);
    }

    /**
     * 找回支付密码
     */
    private void modifyPayPwd() {
        RaspberryCallback<ObjectResponse<Boolean>> callback = new RaspberryCallback<ObjectResponse<Boolean>>() {

            @Override
            public void onSuccess(ObjectResponse<Boolean> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        ToastUtils.showToast("找回密码成功");
                        finish();
                        return;
                    }
                }
                ToastUtils.showToast("找回密码失败");
            }
        };

        callback.setContext(this);
        callback.setCancelable(false);
        callback.setMainThread(true);

        HttpProtocol.retrievePayPwd(MD5Utils.encodeMD52(TextUtils.getText(newPasswordET)), callback);
    }

    private void showView(boolean isShow) {
        if (isShow) {
            securityLL.setVisibility(View.VISIBLE);
            passwordLL.setVisibility(View.GONE);
        } else {
            securityLL.setVisibility(View.GONE);
            passwordLL.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit_btn:
                if (checkSecurity()) {
                    checkSecurityQuestions();
                }
                break;
            case R.id.modify_pwd_submit_btn:
                if (checkPassword()) {
                    modifyPayPwd();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        enableSubmit();
    }
}