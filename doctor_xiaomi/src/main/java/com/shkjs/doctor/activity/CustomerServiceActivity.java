package com.shkjs.doctor.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.raspberry.library.util.CustomAlertDialog;
import com.raspberry.library.util.DividerItemDecoration;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.base.BaseRecyclerAdapter;
import com.shkjs.doctor.base.BaseRecyclerViewHolder;
import com.shkjs.doctor.bean.QueryHelpBean;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ListResponse;
import com.shkjs.doctor.util.AudioHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomerServiceActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;
    @Bind(R.id.custem_help_recyclerview)
    RecyclerView custem_help_recyclerview;

    private BaseRecyclerAdapter<QueryHelpBean>adapter;
    private RaspberryCallback<ListResponse<QueryHelpBean>>callback;
    private List<QueryHelpBean>dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service);
        ButterKnife.bind(this);
        toptitle_tv.setText("使用帮助");
        initListener();
    }

    private void initListener() {
        dataList = new ArrayList<>();
        callback = new RaspberryCallback<ListResponse<QueryHelpBean>>() {
            @Override
            public void onSuccess(ListResponse<QueryHelpBean> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response,code)){
                    dataList.addAll(response.getData());
                    adapter.notifyDataSetChanged();
                }
            }
        };
        AudioHelper.initCallBack(callback,this,true);

        adapter = new BaseRecyclerAdapter<QueryHelpBean>(this,dataList) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_help;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, QueryHelpBean item) {
                holder.getTextView(R.id.item_help_title).setText((position+1)+"."+item.getTitile());
                holder.getTextView(R.id.item_help_content).setText(item.getContent());
                if (item.getContent().contains("医星汇客服")){
                    holder.getTextView(R.id.item_help_content).setTextColor(getResources().getColor(R.color.red_e84618));
                    holder.getTextView(R.id.item_help_content).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CustomAlertDialog.dialogExSureCancel("是否给医星汇客服拨打电话?",CustomerServiceActivity.this, new CustomAlertDialog.OnDialogClickListener() {
                                @Override
                                public void doSomeThings() {
                                    Intent intent = new Intent();
                                    intent.setAction("android.intent.action.DIAL");
                                    intent.setData(Uri.parse("tel:"+"4008859120"));
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                }else {
                    holder.getTextView(R.id.item_help_content).setTextColor(getResources().getColor(R.color.gray_888888));
                }
            }
        };
        custem_help_recyclerview.setAdapter(adapter);
        custem_help_recyclerview.setItemAnimator(new DefaultItemAnimator());
        custem_help_recyclerview.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        custem_help_recyclerview.setHasFixedSize(true);
        custem_help_recyclerview.setLayoutManager(new LinearLayoutManager(this));

        HttpProtocol.getHelp(callback);
    }

    @OnClick(R.id.back_iv)
    public void customerOnClick(View view){
        switch (view.getId()){
            case R.id.back_iv:
                finish();
                break;
//            case R.id.customer_service_phone_call:
//                Intent intent = new Intent();
//                intent.setAction("android.intent.action.DIAL");
//                intent.setData(Uri.parse("tel:"+customer_service_phone_call.getText().toString().trim()));
//                startActivity(intent);
//                break;
        }
    }
}
