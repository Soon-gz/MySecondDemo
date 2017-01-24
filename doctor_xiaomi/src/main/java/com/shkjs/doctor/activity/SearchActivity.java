package com.shkjs.doctor.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import com.netease.nim.uikit.common.util.string.StringUtil;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.bean.EventsBus;
import com.shkjs.doctor.bean.MedicalCategoryBean;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ListResponse;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

public class SearchActivity extends BaseActivity {

    @Bind(R.id.choose_list)
    ListView listView;
    @Bind(R.id.search_edit)
    EditText search_edit;

    private List<String>searchList;
    private List<String>realutList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        initListener();
    }

    private void initListener() {
        searchList = new ArrayList<>();
        realutList = new ArrayList<>();
        RaspberryCallback<ListResponse<MedicalCategoryBean>>callback = new RaspberryCallback<ListResponse<MedicalCategoryBean>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
            }

            @Override
            public void onSuccess(final ListResponse<MedicalCategoryBean> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response,code)){
                    if (response.getData() != null){
                        runOnUiThread(new TimerTask() {
                            @Override
                            public void run() {
                                final String[] mStrings = new String[response.getData().size()];
                                for (int i = 0; i < response.getData().size(); i++) {
                                    mStrings[i] = response.getData().get(i).getName();
                                    searchList.add(mStrings[i]);
                                }
                                listView.setAdapter(new ArrayAdapter<>(SearchActivity.this,android.R.layout.simple_list_item_1, mStrings));
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        EventBus.getDefault().post(new EventsBus(mStrings[i],"2"));
                                        finish();
                                    }
                                });
                            }
                        });
                    }else {
                        searchList.add("外科");
                        searchList.add("儿科");
                        searchList.add("其他");
                        listView.setAdapter(new ArrayAdapter<>(SearchActivity.this,android.R.layout.simple_list_item_1, searchList));
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                EventBus.getDefault().post(new EventsBus(searchList.get(i),"2"));
                                finish();
                            }
                        });
                    }
                }
            }
        };
        callback.setMainThread(true);
        callback.setContext(this);
        callback.setCancelable(false);

        HttpProtocol.getMedical(callback);
    }


    @OnClick({R.id.search_doctor_return_ibtn,R.id.search_searchbtn})
    public void searchOnClick(View view){
        switch (view.getId()){
            case R.id.search_searchbtn:
                if (!StringUtil.isEmpty(search_edit.getText().toString())){
                    realutList.clear();
                    for (int index = 0; index < searchList.size(); index++) {
                        if (searchList.get(index).contains(search_edit.getText().toString())){
                            realutList.add(searchList.get(index));
                        }
                    }
                    listView.setAdapter(new ArrayAdapter<>(SearchActivity.this,android.R.layout.simple_list_item_1, realutList));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            EventBus.getDefault().post(new EventsBus(realutList.get(i),"2"));
                            finish();
                        }
                    });
                }else {
                    ToastUtils.showToast("请输入需要搜索的科室");
                }
                break;
            case R.id.search_doctor_return_ibtn:
                finish();
                break;
        }
    }
}
