package com.shkjs.doctor.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.raspberry.library.util.CustomAlertDialog;
import com.raspberry.library.util.InputMethodUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.bean.DoctorMessageBean;
import com.shkjs.doctor.bean.EventsBus;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class IntroductGoodActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;
    @Bind(R.id.text_ringht)
    TextView text_right;
    @Bind(R.id.introduct_good_edittext_et)
    EditText introduct_good_edittext_et;        //编辑个人简介以及擅长领域
    @Bind(R.id.introduct_good_textnum_tv)
    TextView introduct_good_textnum_tv;         //输入字数的限制

    private static final String INTRODCUTION = "0";//填写个人简介
    private static final String GOODATSOMETHING = "1";//填写擅长领域
    private static final String TEXTRIGHT = "保存";
    private static final String INTRODUCTIONG_TITLE = "简介";
    private static final String GOODATSOMETHING_TITLE = "擅长";
    private static final String INTRODUCTION_HINT = "请填写个人简介,以便于患者更了解您";
    private static final String GOODSOMETHING_HINT = "请简要描述您所擅长的疾病领域";
    private String msg = "";
    private String type = INTRODCUTION;

    private boolean canChange = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduct_good);
        //初始化
        ButterKnife.bind(this);
        type = getIntent().getStringExtra("type");
        msg = getIntent().getStringExtra("msg");
        canChange = getIntent().getBooleanExtra("canchange",true);
        setTitleText(type);
        //设置监听
        initListener();
    }

    private void initListener() {
        if (canChange){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    InputMethodManager inputManager = (InputMethodManager) introduct_good_edittext_et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            },400);
        }
        introduct_good_edittext_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                introduct_good_textnum_tv.setText(String.valueOf(charSequence.length()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @OnClick({R.id.back_iv,R.id.text_ringht})
    public void introductOnClick(View view){
        switch (view.getId()){
            case R.id.back_iv:
                if (!isBackCheckContentChange()){
                    finish();
                }else {
                    CustomAlertDialog.dialogExSureCancel("填写内容未保存，确定退出吗？", this, new CustomAlertDialog.OnDialogClickListener() {
                        @Override
                        public void doSomeThings() {
                            finish();
                        }
                    });
                }
                break;
            case R.id.text_ringht:
                if (isBackCheckContentChange()){
                    ToastUtils.showToast("保存成功");
                    EventBus.getDefault().post(new EventsBus(introduct_good_edittext_et.getText().toString(),type));
                    finish();
                }else{
                    ToastUtils.showToast("请输入内容或修改之后再保存");
                }

                break;
        }
    }

    //返回时对输入进行检查
    public boolean isBackCheckContentChange(){
        if ("".equals(introduct_good_edittext_et.getText().toString()) || msg.equals(introduct_good_edittext_et.getText().toString())){
            return false;
        }else{
            return true;
        }
    }

    //进入时对携带数据进行检查
    public boolean checkInChange(){
        if (INTRODUCTION_HINT.equals(msg) || GOODSOMETHING_HINT.equals(msg)){
            return false;
        }else{
            return true;
        }
    }

    //设置标题
    public void setTitleText(String type){
        switch (type){
            case INTRODCUTION:
                toptitle_tv.setText(INTRODUCTIONG_TITLE);
                if (!checkInChange()){
                    introduct_good_edittext_et.setHint(INTRODUCTION_HINT);
                }else {
                    introduct_good_edittext_et.setText(msg);
                    introduct_good_textnum_tv.setText(String.valueOf(msg.length()));
                }
                break;
            case GOODATSOMETHING:
                toptitle_tv.setText(GOODATSOMETHING_TITLE);
                if (!checkInChange()){
                    introduct_good_edittext_et.setHint(GOODSOMETHING_HINT);
                }else {
                    introduct_good_edittext_et.setText(msg);
                    introduct_good_textnum_tv.setText(String.valueOf(msg.length()));
                }
                break;
        }
        if (canChange){
            InputMethodUtils.showSoftInput(introduct_good_edittext_et);
            text_right.setText(TEXTRIGHT);
        }else {
            introduct_good_edittext_et.setEnabled(false);
        }

    }
}
