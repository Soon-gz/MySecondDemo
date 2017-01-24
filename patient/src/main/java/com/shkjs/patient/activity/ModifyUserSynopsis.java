package com.shkjs.patient.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.raspberry.library.util.ToastUtils;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/9/25.
 */
public class ModifyUserSynopsis extends BaseActivity {

    @Bind(R.id.modify_user_synopsis_et)
    EditText editText;
    @Bind(R.id.modify_user_synopsis_tv)
    TextView textView;

    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_modify_user_synopsis);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.user_synopsis);

        initData();
        initListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
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
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.save:
                        ToastUtils.showToast("保存数据啦");
                        finish();
                        return true;
                    default:
                        return false;
                }

            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                textView.setText(editable.length() + "/100");
            }
        });

    }
}
