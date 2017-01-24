package com.shkjs.patient.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.raspberry.library.util.TextUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.nim.recyclerview.BaseRecyclerViewHolder;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.base.BaseAdapter;
import com.shkjs.patient.bean.SecurityQuestion;
import com.shkjs.patient.cache.DataCache;
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
 * Created by xiaohu on 2016/10/27.
 * <p>
 * 设置密保问题
 */

public class SettingSecurityQuestionActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

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

    private Toolbar toolbar;

    private List<SecurityQuestion> datalist;
    private List<SecurityQuestion> questionOneList;
    private List<SecurityQuestion> questionTwoList;
    private List<SecurityQuestion> questionThreeList;

    private BaseAdapter<SecurityQuestion> questionAdapterOne;
    private BaseAdapter<SecurityQuestion> questionAdapterTwo;
    private BaseAdapter<SecurityQuestion> questionAdapterThree;

    private static final int ONE = 121;
    private static final int TWO = 122;
    private static final int THREE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting_security_question);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.setting_security_question);

        initData();
        initListener();

        querySecurityQuestions();
    }

    private void initData() {
        datalist = new ArrayList<>();
        SecurityQuestion question = new SecurityQuestion();
        question.setId(-1l);
        question.setQuestion("请选择");
        datalist.add(0, question);
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

        questionOne.setAdapter(questionAdapterOne);
        questionTwo.setAdapter(questionAdapterTwo);
        questionThree.setAdapter(questionAdapterThree);

        questionOne.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //                initList(ONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        questionTwo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //                initList(TWO);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        questionThree.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //                initList(THREE);
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
     * 选择后，数据互斥
     */
    private void initList(int type) {
        switch (type) {
            case ONE:
                if (questionOne.getSelectedItemPosition() != 0) {
                    SecurityQuestion question2 = (SecurityQuestion) questionTwo.getSelectedItem();
                    SecurityQuestion question3 = (SecurityQuestion) questionThree.getSelectedItem();
                    questionTwoList.remove(questionOne.getSelectedItem());
                    questionThreeList.remove(questionOne.getSelectedItem());
                    for (int i = 0; i < questionTwoList.size(); i++) {
                        if (question2.equals(questionTwoList.get(i))) {
                            questionTwo.setSelection(i);
                            break;
                        }
                    }
                    for (int i = 0; i < questionThreeList.size(); i++) {
                        if (question3.equals(questionThreeList.get(i))) {
                            questionThree.setSelection(i);
                            break;
                        }
                    }
                }
                break;
            case TWO:
                if (questionTwo.getSelectedItemPosition() != 0) {
                    SecurityQuestion question1 = (SecurityQuestion) questionOne.getSelectedItem();
                    SecurityQuestion question3 = (SecurityQuestion) questionThree.getSelectedItem();
                    questionOneList.remove(questionTwo.getSelectedItem());
                    questionThreeList.remove(questionTwo.getSelectedItem());
                    for (int i = 0; i < questionOneList.size(); i++) {
                        if (question1.equals(questionOneList.get(i))) {
                            questionOne.setSelection(i);
                            break;
                        }
                    }
                    for (int i = 0; i < questionThreeList.size(); i++) {
                        if (question3.equals(questionThreeList.get(i))) {
                            questionThree.setSelection(i);
                            break;
                        }
                    }
                }
                break;
            case THREE:
                if (questionThree.getSelectedItemPosition() != 0) {
                    SecurityQuestion question1 = (SecurityQuestion) questionOne.getSelectedItem();
                    SecurityQuestion question2 = (SecurityQuestion) questionTwo.getSelectedItem();
                    questionOneList.remove(questionThree.getSelectedItem());
                    questionTwoList.remove(questionThree.getSelectedItem());
                    for (int i = 0; i < questionOneList.size(); i++) {
                        if (question1.equals(questionOneList.get(i))) {
                            questionOne.setSelection(i);
                            break;
                        }
                    }
                    for (int i = 0; i < questionTwoList.size(); i++) {
                        if (question2.equals(questionTwoList.get(i))) {
                            questionTwo.setSelection(i);
                            break;
                        }
                    }
                }
                break;
            default:
                break;
        }

        questionAdapterOne.notifyDataSetChanged();
        questionAdapterTwo.notifyDataSetChanged();
        questionAdapterThree.notifyDataSetChanged();

        enableSubmit();
    }

    /**
     * 创建设置密保body数据
     *
     * @return
     */
    private String createBodyStr() {
        String bodyStr = null;
        bodyStr = "list[0].question=" + ((SecurityQuestion) questionOne.getSelectedItem()).getQuestion();
        bodyStr = bodyStr + "&list[0].answer=" + TextUtils.getText(answerOne);
        bodyStr = bodyStr + "&list[0].accountId=" + DataCache.getInstance().getUserInfo().getId();
        bodyStr = bodyStr + "&list[1].question=" + ((SecurityQuestion) questionTwo.getSelectedItem()).getQuestion();
        bodyStr = bodyStr + "&list[1].answer=" + TextUtils.getText(answerTwo);
        bodyStr = bodyStr + "&list[1].accountId=" + DataCache.getInstance().getUserInfo().getId();
        bodyStr = bodyStr + "&list[2].question=" + ((SecurityQuestion) questionThree.getSelectedItem()).getQuestion();
        bodyStr = bodyStr + "&list[2].answer=" + TextUtils.getText(answerThree);
        bodyStr = bodyStr + "&list[2].accountId=" + DataCache.getInstance().getUserInfo().getId();
        return bodyStr;
    }

    /**
     * 校验是否选择密保和填写密保答案
     *
     * @return
     */
    private boolean checkSecurity() {
        if (questionOne.getSelectedItemPosition() == 0 || questionTwo.getSelectedItemPosition() == 0 || questionThree
                .getSelectedItemPosition() == 0) {
            //            ToastUtils.showToast("请选择密保问题");
            return false;
        } else if (TextUtils.isEmpty(answerOne) || TextUtils.isEmpty(answerTwo) || TextUtils.isEmpty(answerThree)) {
            //            ToastUtils.showToast("请输入密保答案");
            return false;
        }
        return true;
    }

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

                        questionOneList.addAll(datalist);
                        questionTwoList.addAll(datalist);
                        questionThreeList.addAll(datalist);

                        questionAdapterOne.notifyDataSetChanged();
                        questionAdapterTwo.notifyDataSetChanged();
                        questionAdapterThree.notifyDataSetChanged();
                        return;
                    }
                }
                ToastUtils.showToast("查询密保问题失败");
                finish();
            }
        };

        callback.setContext(this);
        callback.setCancelable(false);
        callback.setMainThread(true);

        HttpProtocol.querySecurityQuestions(callback);
    }

    /**
     * 添加密保问题与答案
     */
    private void addSecurityQuestions() {
        RaspberryCallback<ObjectResponse<Boolean>> callback = new RaspberryCallback<ObjectResponse<Boolean>>() {

            @Override
            public void onSuccess(ObjectResponse<Boolean> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        ToastUtils.showToast("设置密保成功");
                        finish();
                        return;
                    }
                }
                ToastUtils.showToast("设置密保失败" + response.getMsg());
            }
        };

        callback.setContext(this);
        callback.setCancelable(false);
        callback.setMainThread(true);

        HttpProtocol.addSecurityQuestion(createBodyStr(), callback);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit_btn:
                if (checkSecurity()) {
                    if (questionOne.getSelectedItem().equals(questionTwo.getSelectedItem()) || questionTwo
                            .getSelectedItem().equals(questionThree.getSelectedItem()) || questionOne.getSelectedItem
                            ().equals(questionThree.getSelectedItem())) {
                        ToastUtils.showToast("请选择三个不同的密保问题");
                    } else {
                        addSecurityQuestions();
                    }
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
