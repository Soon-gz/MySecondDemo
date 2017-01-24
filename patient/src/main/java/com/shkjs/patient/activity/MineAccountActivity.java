package com.shkjs.patient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.bean.Account;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.shkjs.patient.util.SpliceUtils;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/9/23.
 * <p>
 * 我的账户
 */
public class MineAccountActivity extends BaseActivity implements View.OnClickListener {

    private static final int RECHARGE = 121;//充值
    private static final int UPDATE_VIEW = 122;

    @Bind(R.id.mine_account_balance_rl)
    RelativeLayout abalanceLL;
    @Bind(R.id.mine_account_balance_tv)
    TextView balanceTV;
    @Bind(R.id.mine_account_add_alipay_account_ll)
    LinearLayout alipayLL;
    @Bind(R.id.mine_account_add_alipay_account_tv)
    TextView alipayTV;
    @Bind(R.id.mine_account_add_homegroup_member_ll)
    LinearLayout homeGroupLL;
    @Bind(R.id.mine_account_homegroup_ll)
    LinearLayout mineHomeGroupLL;
    @Bind(R.id.mine_account_add_homegroup_member_tv)
    TextView homeGroupTV;
    @Bind(R.id.mine_account_recharge_btn)
    Button rechargeBtn;

    private Toolbar toolbar;

    private Account account;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_VIEW:
                    showView();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mine_account);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.mine_account_text);

        initData();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAccountDetail();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    private void initData() {
        alipayLL.setVisibility(View.GONE);
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
                    case R.id.detail:
                        balanceDetail();
                        return true;
                    default:
                        return false;
                }

            }
        });

        mineHomeGroupLL.setOnClickListener(this);
        rechargeBtn.setOnClickListener(this);
    }

    private void getAccountDetail() {
        RaspberryCallback<ObjectResponse<Account>> callback = new RaspberryCallback<ObjectResponse<Account>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                Logger.e(getString(R.string.get_account_detail_failed) + e.getLocalizedMessage());
                ToastUtils.showToast(getString(R.string.get_account_detail_failed));
                finish();
            }

            @Override
            public void onSuccess(ObjectResponse<Account> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        account = response.getData();
                        handler.sendEmptyMessage(UPDATE_VIEW);
                    } else {
                        Logger.e(getString(R.string.get_account_detail_failed) + response.getMsg());
                        ToastUtils.showToast(getString(R.string.get_account_detail_failed));
                        finish();
                    }
                } else {
                    Logger.e(getString(R.string.get_account_detail_failed));
                    ToastUtils.showToast(getString(R.string.get_account_detail_failed));
                    finish();
                }
            }
        };

        callback.setContext(this);
        callback.setMainThread(false);
        callback.setCancelable(false);

        HttpProtocol.getAccountDetail(callback);
    }

    private void showView() {
        if (null != account) {
            balanceTV.setText(SpliceUtils.formatBalance(account.getBalance()));
        } else {
            balanceTV.setText(SpliceUtils.formatBalance(0));
        }
    }

    /**
     * 家庭组
     */
    private void homeGroup() {
        Intent intent = new Intent(this, HomeGroupActivity.class);
        startActivity(intent);
    }

    /**
     * 充值
     */
    private void recharge() {
        Intent intent = new Intent(this, RechargeActivity.class);
        intent.putExtra(RechargeActivity.class.getSimpleName(), account.getBalance());
        startActivityForResult(intent, RECHARGE);
    }

    /**
     * 收支明细
     */
    private void balanceDetail() {
        Intent intent = new Intent(this, BalanceDetailActivity.class);
        intent.putExtra(BalanceDetailActivity.class.getSimpleName(), account.getId());
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mine_account_homegroup_ll:
                homeGroup();
                break;
            case R.id.mine_account_recharge_btn:
                recharge();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == RECHARGE) {//充值成功后，更新余额
                if (data != null) {
                    long balance = data.getLongExtra(RechargeActivity.class.getSimpleName(), 0);
                    if (balance != 0) {
                        account.setBalance(balance);
                        balanceTV.setText(SpliceUtils.formatBalance(balance));
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
