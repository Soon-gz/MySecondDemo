package com.shkjs.doctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.raspberry.library.util.DividerItemDecoration;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.SwipeItemLayout;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.base.BaseRecyclerAdapter;
import com.shkjs.doctor.base.BaseRecyclerViewHolder;
import com.shkjs.doctor.bean.CreateAVDoctorBean;
import com.shkjs.doctor.util.HealthPopup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyFriendsActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;
    @Bind(R.id.imageview_right)
    ImageView imageView;
    @Bind(R.id.menurecyclerview)
    RecyclerView recyclerView;
    @Bind(R.id.no_message_layout_textview)
    TextView no_message_layout_textview;

    //医生好友数据
    private List<CreateAVDoctorBean>dataList;
    private BaseRecyclerAdapter<CreateAVDoctorBean>adapter;
    private HealthPopup healthPopup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);
        ButterKnife.bind(this);
        toptitle_tv.setText("医生好友");
        no_message_layout_textview.setText("您还没有医生好友，快去添加吧~");
        imageView.setImageResource(R.drawable.add);

        initViews();

        loadNetData();
    }

    //加载网络数据
    private void loadNetData() {
        for (int i = 0; i < 5; i++) {
            CreateAVDoctorBean bean = new CreateAVDoctorBean("钢铁侠","钢铁科","复仇者联盟","复仇者","http://image.tianjimedia.com/uploadImages/2012/304/YLPU5Z5FHT0Q_1000x500.jpg");
            dataList.add(bean);
        }
        adapter.notifyDataSetChanged();
    }


    public void adapterNotifyDataSetChanged(){
        adapter.notifyDataSetChanged();
        if (dataList.size() == 0){
            no_message_layout_textview.setVisibility(View.VISIBLE);
        }else {
            no_message_layout_textview.setVisibility(View.GONE);
        }
    }

    //初始化控件
    private void initViews() {

        dataList = new ArrayList<>();
        healthPopup = new HealthPopup(this);
        adapter = new BaseRecyclerAdapter<CreateAVDoctorBean>(this,dataList) {


            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_myfriends_doctor;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, final int position, CreateAVDoctorBean item) {
                final SwipeItemLayout itemLayout = holder.getSwipeItemLayout(R.id.item_myfriends_sil);
                Glide.with(MyFriendsActivity.this).load(item.getHead()).dontAnimate().thumbnail(0.1f).into(holder.getCircleImageView(R.id.item_myfriends_iv));
                holder.getTextView(R.id.item_myfriends_name_tv).setText(item.getName());
                holder.getTextView(R.id.item_myfriends_department_tv).setText(item.getDepartment());
                holder.getTextView(R.id.item_myfriends_hospital_tv).setText(item.getHospital());
                holder.getTextView(R.id.item_myfriends_doctortitle_tv).setText(item.getDoctorTitle());
                holder.getTextView(R.id.item_myfriends_delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemLayout.close();
                        ToastUtils.showToast("删除了"+position);
                        dataList.remove(position);
                        adapterNotifyDataSetChanged();
                    }
                });
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。

        recyclerView.setAdapter(adapter);

    }

    @OnClick({R.id.back_iv,R.id.imageview_right})
    public void myfriendsOnClick(View view){
        switch (view.getId()){
            case R.id.back_iv:
                finish();
                break;
            case R.id.imageview_right:
                healthPopup.addFriendsByPhone(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(MyFriendsActivity.this,AddFrientsActivity.class));
                        healthPopup.dismiss();
                    }
                });

                healthPopup.addFriendsByScan(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        healthPopup.dismiss();
                    }
                });
                healthPopup.showPopupWindow(imageView);
                break;
        }
    }
}
