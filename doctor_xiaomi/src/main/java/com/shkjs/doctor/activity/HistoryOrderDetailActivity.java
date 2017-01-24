package com.shkjs.doctor.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.uikit.common.util.string.StringUtil;
import com.raspberry.library.util.CustomAlertDialog;
import com.raspberry.library.util.DateUtil;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.application.MyApplication;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.base.BaseResponse;
import com.shkjs.doctor.bean.DoctorBean;
import com.shkjs.doctor.bean.DoctorFeeBean;
import com.shkjs.doctor.bean.HistoryDetailUserNameBean;
import com.shkjs.doctor.bean.OrderbeanData;
import com.shkjs.doctor.cache.DataCache;
import com.shkjs.doctor.data.DoctorTag;
import com.shkjs.doctor.data.OrderSource;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ObjectResponse;
import com.shkjs.doctor.util.AudioHelper;
import com.shkjs.doctor.util.BalanceUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HistoryOrderDetailActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;
    @Bind(R.id.history_detail_status_tv)
    TextView history_detail_status_tv;
    @Bind(R.id.history_detail_number_edittext_tv)
    TextView history_detail_number_edittext_tv;
    @Bind(R.id.history_detail_time_edittext_tv)
    TextView history_detail_time_edittext_tv;
    @Bind(R.id.history_detail_money_tv)
    TextView history_detail_money_tv;
    @Bind(R.id.history_detail_name_tv)
    TextView history_detail_name_tv;
    @Bind(R.id.history_detail_time_tv)
    TextView history_detail_time_tv;
    @Bind(R.id.history_detail_allmoney_tv)
    TextView history_detail_allmoney_tv;
    @Bind(R.id.history_detail_litlemoney_tv)
    TextView history_detail_litlemoney_tv;
    @Bind(R.id.history_detail_incomemoney_tv)
    TextView history_detail_incomemoney_tv;
    @Bind(R.id.history_detail_cancel_tv)
    TextView history_detail_cancel_tv;
    @Bind(R.id.history_detail_title_tv)
    TextView history_detail_title_tv;
    @Bind(R.id.history_detail_time_text)
    TextView history_detail_time_text;
    @Bind(R.id.history_detail_cancel_ll)
    RelativeLayout history_detail_cancel_ll;
    @Bind(R.id.history_detail_photo_iv)
    ImageView history_detail_photo_iv;

    private OrderbeanData.OrderBean orderBean;
    private String time;
    private String date;
    private DoctorTag doctorTag;
    private RaspberryCallback<BaseResponse>callback;
    private RaspberryCallback<ObjectResponse<HistoryDetailUserNameBean>>getNameCallback;
    private RaspberryCallback<ObjectResponse<DoctorFeeBean>>doctorFeeCallback;
    private RaspberryCallback<ObjectResponse<DoctorBean>>doctorBeanCallback;

    private DoctorFeeBean doctorFeeBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_order_detail);
        ButterKnife.bind(this);
        orderBean = (OrderbeanData.OrderBean) getIntent().getSerializableExtra("orderbean");
        time = getIntent().getStringExtra("time");
        date = getIntent().getStringExtra("date");
        doctorTag = DoctorTag.valueOf(getIntent().getStringExtra(Preference.DOCTORTAG));
        Log.i("tag00","time:"+time+"用户ID："+orderBean.getPayerId()+"date:"+date);
        history_detail_time_tv.setText(DateUtil.getFormatTimeFromTimestamp(Long.parseLong(date),"yyyy-MM-dd")+time);

        toptitle_tv.setText("订单详情");
        initListener();
        initViews(orderBean);
    }

    private void initListener() {

        doctorBeanCallback = new RaspberryCallback<ObjectResponse<DoctorBean>>() {
            @Override
            public void onSuccess(ObjectResponse<DoctorBean> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response,code)){
                    initMoney(response.getData(),doctorFeeBean);
                }
            }
        };
        AudioHelper.initCallBack(doctorBeanCallback,this,false);

        doctorFeeCallback = new RaspberryCallback<ObjectResponse<DoctorFeeBean>>() {
            @Override
            public void onSuccess(ObjectResponse<DoctorFeeBean> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)){
                    doctorFeeBean = response.getData();
                    if (DataCache.getInstance().getUserInfo() != null){
                        initMoney(DataCache.getInstance().getUserInfo(),response.getData());
                    }else if (MyApplication.doctorBean != null){
                        initMoney(MyApplication.doctorBean,response.getData());
                    }else {
                        HttpProtocol.detail(doctorBeanCallback);
                    }
                }
            }
        };
        AudioHelper.initCallBack(doctorFeeCallback,this,true);

        if (doctorTag.equals(DoctorTag.NORMAL)){
            HttpProtocol.getMoneyDelay(doctorFeeCallback);
        }else {
            AudioHelper.setMoney(history_detail_incomemoney_tv,orderBean.getMoney());
        }
    }

    private void initMoney(DoctorBean doctorBean,DoctorFeeBean doctorFeeBean){
        if (doctorBean == null || doctorFeeBean == null){
            AudioHelper.setMoney(history_detail_litlemoney_tv,0);
            AudioHelper.setMoney(history_detail_incomemoney_tv,orderBean.getMoney());
            return;
        }
        switch (doctorBean.getPlatformLevel()){
            case Preference.CERTIFICATION:
                switch (OrderSource.valueOf(orderBean.getSource())){
                    case INQUIRY_RESERVE:
                        AudioHelper.setMoney(history_detail_litlemoney_tv,getDelayMoney(doctorFeeBean.getCertifictionTxt()));
                        AudioHelper.setMoney(history_detail_incomemoney_tv,getDoctorIncomeMoney(doctorFeeBean.getCertifictionTxt()));
                        break;
                    default:
                        AudioHelper.setMoney(history_detail_litlemoney_tv,getDelayMoney(doctorFeeBean.getCertifictionVideo()));
                        AudioHelper.setMoney(history_detail_incomemoney_tv,getDoctorIncomeMoney(doctorFeeBean.getCertifictionVideo()));
                        break;
                }
                break;
            case Preference.AUTHORITY:
                switch (OrderSource.valueOf(orderBean.getSource())){
                    case INQUIRY_RESERVE:
                        AudioHelper.setMoney(history_detail_litlemoney_tv,getDelayMoney(doctorFeeBean.getAuthorityTxt()));
                        AudioHelper.setMoney(history_detail_incomemoney_tv,getDoctorIncomeMoney(doctorFeeBean.getAuthorityTxt()));
                        break;
                    default:
                        AudioHelper.setMoney(history_detail_litlemoney_tv,getDelayMoney(doctorFeeBean.getAuthorityVideo()));
                        AudioHelper.setMoney(history_detail_incomemoney_tv,getDoctorIncomeMoney(doctorFeeBean.getAuthorityVideo()));
                        break;
                }
                break;
            default:
                AudioHelper.setMoney(history_detail_litlemoney_tv,0);
                AudioHelper.setMoney(history_detail_incomemoney_tv,orderBean.getMoney());
                break;
        }
    }

    private int getDelayMoney(int delayMoney){
        if (orderBean.getMoney() > delayMoney){
            return delayMoney;
        }
        return 0;
    }

    private int getDoctorIncomeMoney(int delayMoney){
        if (orderBean.getMoney() > delayMoney){
            return orderBean.getMoney() - delayMoney;
        }
        return 0;
    }

    private void initViews(OrderbeanData.OrderBean orderBean) {
        callback = new RaspberryCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response,code)){
                    ToastUtils.showToast("取消成功");
                    finish();
                }
            }
        };
        callback.setContext(this);
        callback.setShowDialog(true);
        callback.setCancelable(false);
        callback.setMainThread(true);

        getNameCallback = new RaspberryCallback<ObjectResponse<HistoryDetailUserNameBean>>() {
            @Override
            public void onSuccess(ObjectResponse<HistoryDetailUserNameBean> response, int code) {
                super.onSuccess(response, code);
                if(HttpProtocol.checkStatus(response,code)){
                    AudioHelper.setNameWithDefault(history_detail_name_tv,response.getData().getName(),response.getData().getNickName());
                }
            }
        };
        getNameCallback .setContext(this);
        getNameCallback.setShowDialog(false);
        getNameCallback.setCancelable(false);
        getNameCallback.setMainThread(true);

        HttpProtocol.getUserNameById(getNameCallback,orderBean.getPayerId()+"");

        if (!Preference.PAID.equals(orderBean.getStatus())){
            history_detail_cancel_ll.setVisibility(View.GONE);
        }
        history_detail_status_tv.setText(getOrderS(orderBean.getStatus()));
        history_detail_number_edittext_tv.setText(orderBean.getCode());
        history_detail_time_edittext_tv.setText(DateUtil.getFormatTimeFromTimestamp(Long.parseLong(orderBean.getCreateDate()),"yyyy-MM-dd HH:mm"));
        if ("多人会诊子项".equals(orderBean.getMark())){
            history_detail_title_tv.setText("视频会诊");
        }else {
            history_detail_title_tv.setText(orderBean.getMark());
        }
        if ("坐诊咨询".equals(orderBean.getMark())){
            history_detail_photo_iv.setImageResource(R.drawable.historicalorder_videoconsultation);
        }else if ("图文咨询".equals(orderBean.getMark())){
            history_detail_photo_iv.setImageResource(R.drawable.historicalorder_pictureconsulting);
        }else {
            history_detail_photo_iv.setImageResource(R.drawable.historicalorder_groupconsultation);
        }
        AudioHelper.setMoney(history_detail_money_tv,Long.parseLong(orderBean.getMoney()+""));

        if ("INQUIRY_RESERVE".equals(orderBean.getSource())){
            history_detail_time_tv.setVisibility(View.GONE);
            history_detail_time_text.setVisibility(View.GONE);
        }
        AudioHelper.setMoney(history_detail_allmoney_tv,Long.parseLong(orderBean.getMoney()+""));
    }

    @OnClick({R.id.back_iv,R.id.history_detail_cancel_ll})
    public void historydeatilOnClick(View view){
        switch (view.getId()){
            case R.id.back_iv:
                finish();
                break;
            case R.id.history_detail_cancel_ll:
                CustomAlertDialog.dialogExSureCancel("是否确定" + history_detail_cancel_tv.getText().toString(), this, new CustomAlertDialog.OnDialogClickListener() {
                    @Override
                    public void doSomeThings() {
                       HttpProtocol.postCancelOrder(callback,orderBean.getCode(),getSource(orderBean.getSource()),HistoryOrderDetailActivity.this);
                    }
                });
                break;
        }
    }


    public String getOrderS(String type){
        String status = "";
        switch (type){
            case "PAID":
                status = "未完成";
                break;
            case "COMPLETE":
                status = "已完成";
                break;
            default:
                status = "已取消";
                break;
        }
        return status;
    }

    public String getSource(String source){
        if ("GROUP_SIT_DIAGNOSE_DETAIL".equals(source)){
            return "GROUP_SIT_DIAGNOSE";
        }
        return source;
    }
}
