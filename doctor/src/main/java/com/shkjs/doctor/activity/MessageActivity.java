package com.shkjs.doctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.raspberry.library.util.CustomAlertDialog;
import com.raspberry.library.util.DateUtil;
import com.raspberry.library.util.DividerItemDecoration;
import com.raspberry.library.util.JsonUtils;
import com.raspberry.library.util.SwipRefreshLayoutListener;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.application.MyApplication;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.base.BaseRecyclerAdapter;
import com.shkjs.doctor.base.BaseRecyclerViewHolder;
import com.shkjs.doctor.base.BaseResponse;
import com.shkjs.doctor.bean.DoctorBean;
import com.shkjs.doctor.bean.SystemHelpBean;
import com.shkjs.doctor.bean.SystemMessageBean;
import com.shkjs.doctor.cache.DataCache;
import com.shkjs.doctor.data.PushMessageTypeEm;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ListResponse;
import com.shkjs.doctor.http.response.ObjectResponse;
import com.shkjs.doctor.util.AudioHelper;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends BaseActivity {

    @Bind(R.id.refreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;              //下拉刷新系统消息
    @Bind(R.id.no_message_layout_textview)
    TextView no_message_layout_textview;               //没有系统消息时显示的提示信息
    @Bind(R.id.no_message_layout_relative)
    RelativeLayout no_message_layout_relative;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerView;                          //recyclerview作数据展示

    //recycler适配器
    private BaseRecyclerAdapter<SystemMessageBean> adapter;
    private RaspberryCallback<ListResponse<SystemMessageBean>> callback;
    private RaspberryCallback<BaseResponse> agreeCallback;
    private RaspberryCallback<BaseResponse> disagreeCallback;
    private RaspberryCallback<BaseResponse> markReadCallback;

    //数据集合
    private List<SystemMessageBean> datalist;
    private int pageNum = 1;
    private boolean isBottom = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        //初始化控件
        ButterKnife.bind(this);
        //设置下拉刷新监听器
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                datalist.clear();
                pageNum = 1;
                adapter.notifyDataSetChanged();
                callback.setShowDialog(false);
                HttpProtocol.queryHelp(callback, pageNum);
            }
        });
        //初始化数据
        initdata();
        //初始化recyclerview，设置item默认动画
        initListener();
        //加载数据
        loadNetData();
    }


    private void loadNetData() {
        callback = new RaspberryCallback<ListResponse<SystemMessageBean>>() {

            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                initView();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onSuccess(ListResponse<SystemMessageBean> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    if (response.getData() != null && response.getData().size() > 0) {
                        datalist.addAll(response.getData());
                    }
                    initView();
                }
            }
        };
        callback.setContext(this);
        callback.setMainThread(true);
        callback.setCancelable(false);
        callback.setShowDialog(true);
        HttpProtocol.queryHelp(callback, pageNum);

        agreeCallback = new RaspberryCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    ToastUtils.showToast("同意会诊成功。");
                }
            }
        };
        AudioHelper.initCallBack(agreeCallback,this,true);

        disagreeCallback = new RaspberryCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    ToastUtils.showToast("已拒绝视频会诊邀请。");
                }
            }
        };
        AudioHelper.initCallBack(disagreeCallback,this,true);

        markReadCallback = new RaspberryCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                }
            }
        };
        markReadCallback.setContext(this);
        markReadCallback.setMainThread(true);
        markReadCallback.setCancelable(false);
        markReadCallback.setShowDialog(false);
    }

    public void initView() {
        if (datalist.size() > 0) {
            adapter.notifyDataSetChanged();
            no_message_layout_relative.setVisibility(View.GONE);
        } else {
            adapter.notifyDataSetChanged();
            no_message_layout_textview.setText(R.string.no_message);
            no_message_layout_relative.setVisibility(View.VISIBLE);
        }
    }

    private void loadDoctorData() {
        //主页获取医生详情 未测试  2016/10/8
        RaspberryCallback<ObjectResponse<DoctorBean>> callback = new RaspberryCallback<ObjectResponse<DoctorBean>>() {
            @Override
            public void onSuccess(ObjectResponse<DoctorBean> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response,code)){
                    DataCache.getInstance().setUserInfo(response.getData());
                    MyApplication.doctorBean = response.getData();
                    startActivity(new Intent(MessageActivity.this, PersonalMessageActivity.class).putExtra("doctorBean", DataCache.getInstance().getUserInfo()));
                }
            }
        };
        callback.setCancelable(false);
        callback.setContext(this);
        callback.setMainThread(true);
        HttpProtocol.detail(callback);
    }

    //初始化数据
    private void initdata() {
        datalist = new ArrayList<>();
        adapter = new BaseRecyclerAdapter<SystemMessageBean>(this, datalist) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_system_message_rv;
            }

            @Override
            public void bindData(final BaseRecyclerViewHolder holder, int position, final SystemMessageBean item) {
                Log.i("tag00", "对应的json数据：" + JsonUtils.toJson(item) + "\n");
                AudioHelper.setTextWithDefault(holder.getTextView(R.id.item_sys_msg_title), AudioHelper.getTitle(item.getAction()));
                AudioHelper.setDateWithDefaultHHmm(holder.getTextView(R.id.item_sys_msg_time), item.getCreateDate());
                AudioHelper.setTextWithDefault(holder.getTextView(R.id.item_sys_msg_content), AudioHelper.getContent(item.getContent(), item.getAction(),item.getCreateDate()));
                HttpProtocol.markMessageReded(markReadCallback, item.getId() + "");
//                try {
//                    final JSONObject jsonObject = new JSONObject(JsonUtils.toJson(item));
//                    final String status = jsonObject.getString("status");
//                    switch (jsonObject.getString("status")) {
//                        case "0":
//                            holder.getImageView(R.id.item_sys_msg_red_dot).setVisibility(View.VISIBLE);
//                            if ("RAW_DATA".equals(item.getAction())){
//                                holder.getImageView(R.id.item_sys_msg_red_dot).setVisibility(View.GONE);
//                                HttpProtocol.markMessageReded(markReadCallback, item.getId() + "");
//                            }
//                            break;
//                        case "1":
//                            holder.getImageView(R.id.item_sys_msg_red_dot).setVisibility(View.GONE);
//                            break;
//                    }
//                    holder.getLinearLayout(R.id.item_sys_msg_ll).setOnClickListener(new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View view) {
//                            switch (PushMessageTypeEm.valueOf(item.getAction())) {
//                                case AUTHENTICATION:
//                                    if ("0".equals(status)) {
//                                        holder.getImageView(R.id.item_sys_msg_red_dot).setVisibility(View.GONE);
//                                        holder.getLinearLayout(R.id.item_sys_msg_ll).setEnabled(false);
//                                        HttpProtocol.markMessageReded(markReadCallback, item.getId() + "");
//                                        try {
//                                            JSONObject content = new JSONObject(item.getContent());
//                                            String doctorPlatformLevel = content.getString("level");
//                                            if (doctorPlatformLevel.equals(DataCache.getInstance().getUserInfo().getPlatformLevel())){
//                                                startActivity(new Intent(MessageActivity.this, PersonalMessageActivity.class).putExtra("doctorBean", DataCache.getInstance().getUserInfo()));
//                                            }else {
//                                                loadDoctorData();
//                                            }
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                    break;
//                                case ORDER_CANCEL:
//                                    if ("0".equals(status)) {
//                                        holder.getLinearLayout(R.id.item_sys_msg_ll).setEnabled(false);
//                                        holder.getImageView(R.id.item_sys_msg_red_dot).setVisibility(View.GONE);
//                                        HttpProtocol.markMessageReded(markReadCallback, item.getId() + "");
//                                        startActivity(new Intent(MessageActivity.this, HistoryOrderActivity.class));
//                                    }
//                                    break;
//                                case GROUP_DIAGNOSE:
//                                    if ("0".equals(status)) {
//                                        holder.getImageView(R.id.item_sys_msg_red_dot).setVisibility(View.GONE);
//                                        HttpProtocol.markMessageReded(markReadCallback, item.getId() + "");
//                                    }
//                                    try {
//                                        JSONObject content = new JSONObject(item.getContent());
//                                        final int id = content.getInt("subGroupDiagnoseId");
//                                        String createTime = DateUtil.getFormatTimeFromTimestamp(Long.parseLong(content.getString("startDate")), "yyyy年MM月dd日HH:mm");
//                                        String contentStr = "尊敬的医生，您好，" + content.getString("createName") + "医生邀请您在" + createTime + "参加" + content.getString("name") + "患者的视频会诊，请在收到消息后一小时内确认。";
//                                        CustomAlertDialog.dialogGroupCheck("视频会诊邀请", contentStr, MessageActivity.this, new CustomAlertDialog.onDialogSureCancelListener() {
//                                            @Override
//                                            public void agreeGroup() {
//                                                HttpProtocol.agreeGroupDiagnose(agreeCallback, id + "",MessageActivity.this);
//                                            }
//
//                                            @Override
//                                            public void disAgree() {
//                                                HttpProtocol.disAgreeGroupDiagnose(disagreeCallback, id + "",MessageActivity.this);
//                                            }
//                                        });
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                    break;
//                                case ORDER_FINISH:
//                                    if ("0".equals(status)) {
//                                        holder.getLinearLayout(R.id.item_sys_msg_ll).setEnabled(false);
//                                        holder.getImageView(R.id.item_sys_msg_red_dot).setVisibility(View.GONE);
//                                        HttpProtocol.markMessageReded(markReadCallback, item.getId() + "");
//                                        startActivity(new Intent(MessageActivity.this,MyWalletActivity.class));
//                                    }
//                                    break;
//                                case MONTH_CLOSING:
//                                    if ("0".equals(status)) {
//                                        holder.getLinearLayout(R.id.item_sys_msg_ll).setEnabled(false);
//                                        holder.getImageView(R.id.item_sys_msg_red_dot).setVisibility(View.GONE);
//                                        HttpProtocol.markMessageReded(markReadCallback, item.getId() + "");
//                                        startActivity(new Intent(MessageActivity.this,MyWalletActivity.class));
//                                    }
//                                    break;
//                            }
//                        }
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        };
    }

    private void initListener() {
        //设置item动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isBottom && newState == RecyclerView.SCROLL_STATE_IDLE && canNextAdd()) {
                    pageNum++;
                    callback.setShowDialog(false);
                    HttpProtocol.queryHelp(callback, pageNum);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                isBottom = ((layoutManager.findLastVisibleItemPosition() + 1) == adapter.getItemCount());
            }
        });
    }

    public boolean canNextAdd() {
        //默认加载10个，如果不满10个就不加载更多了
        if (datalist.size() < 20 * pageNum) {
            return false;
        }
        return true;
    }


    @OnClick(R.id.back_iv)
    public void sysMsgBack(View view) {
        finish();
    }

}
