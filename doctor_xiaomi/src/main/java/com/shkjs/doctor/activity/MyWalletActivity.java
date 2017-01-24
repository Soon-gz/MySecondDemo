package com.shkjs.doctor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.uikit.common.util.string.StringUtil;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.doctor.R;
import com.shkjs.doctor.application.MyApplication;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.base.BaseResponse;
import com.shkjs.doctor.bean.BandCardBean;
import com.shkjs.doctor.bean.MyWalletDetailBean;
import com.shkjs.doctor.cache.DataCache;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ListResponse;
import com.shkjs.doctor.http.response.ObjectResponse;
import com.shkjs.doctor.util.AudioHelper;
import com.shkjs.doctor.util.BalanceUtils;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyWalletActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;
    @Bind(R.id.text_ringht)
    TextView text_ringht;
    @Bind(R.id.mywallet_apply_bading_fl)
    FrameLayout mywallet_apply_bading_fl;
    @Bind(R.id.mywallet_balance_tv)
    TextView mywallet_balance_tv;
    @Bind(R.id.mywallet_collection_money_tv)
    TextView mywallet_collection_money_tv;
    @Bind(R.id.mywallet_out_money_tv)
    TextView mywallet_out_money_tv;
    @Bind(R.id.mywallet_apply_bankcard_tv)
    TextView mywallet_apply_bankcard_tv;


    private String ISBINGED = "1"; //是否已绑定支付宝   0=已绑定  ，1=未绑定
    private static final String HASBINGED = "0";
    private static final String DONOTBING = "1";
    private String accountId = "";
    private boolean isFirstIn = true;
    private String parthen = "1\\d{10}";
    private RaspberryCallback<ObjectResponse<Long>>monthMoneyCallback;
    private RaspberryCallback<ObjectResponse<MyWalletDetailBean>>callback;
    private RaspberryCallback<ListResponse<BandCardBean>>getBandCardcallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);

        ButterKnife.bind(this);
        toptitle_tv.setText("我的钱包");
        text_ringht.setText("收入明细");

        loadNetData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstIn){
            isFirstIn = false;
        }
        initListner();
        loadNetData();
    }

    private void initListner() {
        callback = new RaspberryCallback<ObjectResponse<MyWalletDetailBean>>() {

            @Override
            public void onSuccess(ObjectResponse<MyWalletDetailBean> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response,code)){
                    AudioHelper.setMoney(mywallet_balance_tv,response.getData().getBalance());
                    AudioHelper.setMoney(mywallet_collection_money_tv,response.getData().getTotalIncome());
                    accountId = response.getData().getId()+"";
                }
            }
        };
        AudioHelper.initCallBack(callback,this,true);

        getBandCardcallback = new RaspberryCallback<ListResponse<BandCardBean>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
            }

            @Override
            public void onSuccess(ListResponse<BandCardBean> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response,code)){
                    if (response.getData()!= null &&  response.getData().size() == 0){
                        ISBINGED = DONOTBING;
                    }else {
                        Log.i("tag00","钱包绑卡数据："+response.getData().size());
                        ISBINGED = HASBINGED;
                    }
                    initEvents(response);
                }
            }
        };
        AudioHelper.initCallBack(getBandCardcallback,this,false);

        monthMoneyCallback = new RaspberryCallback<ObjectResponse<Long>>() {
            @Override
            public void onSuccess(ObjectResponse<Long> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response,code)){
                    if (response.getData() != null){
                        AudioHelper.setMoney(mywallet_out_money_tv,response.getData());
                    }
                }
            }
        };
        AudioHelper.initCallBack(monthMoneyCallback,this,false);

    }

    private void loadNetData() {
        HttpProtocol.getAccountDeatil(callback);
        HttpProtocol.getBandCard(getBandCardcallback);
        HttpProtocol.getMonthClosingMoney(monthMoneyCallback);
    }

    private void initEvents(ListResponse<BandCardBean> response) {
        switch (ISBINGED){
            case HASBINGED:
                mywallet_apply_bading_fl.getChildAt(0).setVisibility(View.GONE);
                mywallet_apply_bading_fl.getChildAt(1).setVisibility(View.VISIBLE);
                for (BandCardBean bandCardBean:response.getData()) {
                    if ("USE".equals(bandCardBean.getDefaultUse())){
                        String phoneNum = bandCardBean.getBankCard();
                        if (isPhoneNumber(phoneNum)){
                            if (DataCache.getInstance().getUserInfo() != null){
                                mywallet_apply_bankcard_tv.setText(DataCache.getInstance().getUserInfo().getName()+"（"+phoneNum.substring(0,3)+"****"+phoneNum.substring(7,11)+"）");
                            }else {
                                mywallet_apply_bankcard_tv.setText("用户（"+phoneNum.substring(0,3)+"****"+phoneNum.substring(7,11)+"）");
                            }
                        }else {
                            int atNum = phoneNum.indexOf("@");
                            if (DataCache.getInstance().getUserInfo() != null){
                                mywallet_apply_bankcard_tv.setText(DataCache.getInstance().getUserInfo().getName()+"（"+phoneNum.substring(0,(int)Math.ceil(atNum/2.0)) + getStar((int)Math.floor(atNum/2.0)) +phoneNum.substring(atNum,phoneNum.length())+"）");
                            }else {
                                mywallet_apply_bankcard_tv.setText("用户（"+phoneNum.substring(0,(int)Math.ceil(atNum/2.0)) + getStar((int)Math.floor(atNum/2.0)) +phoneNum.substring(atNum,phoneNum.length())+"）");
                            }
                        }
                        break;
                    }
                }
                break;
            case DONOTBING:
                mywallet_apply_bading_fl.getChildAt(0).setVisibility(View.VISIBLE);
                mywallet_apply_bading_fl.getChildAt(1).setVisibility(View.GONE);
                break;
        }
    }

    @OnClick({R.id.text_ringht,R.id.back_iv,R.id.mywallet_apply_bading_fl,R.id.month_closing_money_rl})
    public void onMyWalletClick(View view){
        switch (view.getId()){
            case R.id.text_ringht:
                startActivity(new Intent(MyWalletActivity.this,IncomeDetailsActivity.class).putExtra("accountId",accountId));
                break;
            case R.id.back_iv:
                finish();
                break;
            case R.id.mywallet_apply_bading_fl:
                startActivity(new Intent(MyWalletActivity.this,BindingAlipayActivity.class));
                break;
            case R.id.month_closing_money_rl:
                startActivity(new Intent(this,MonthClosingActivity.class));
                break;
        }
    }

    private boolean isPhoneNumber(String number){
        Pattern pattern = Pattern.compile(parthen);
        Matcher match = pattern.matcher(number);
        Boolean flag = match.matches();
        return flag;
    }

    private String getStar(int num){
        String stars = "";
        for (int index = 0; index < num; index++) {
            stars += "*";
        }
        return  stars;
    }
}
