package com.shkjs.patient.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.raspberry.library.util.TextUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.nim.recyclerview.BaseRecyclerAdapter;
import com.shkjs.nim.recyclerview.BaseRecyclerViewHolder;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.bean.HelpMesage;
import com.shkjs.patient.bean.Page;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ListPageResponse;
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
 * Created by xiaohu on 2016/9/23.
 * <p/>
 * 客服中心
 */
public class CallCenterActivity extends BaseActivity {

    //    @Bind(R.id.call_center_webview)
    //    WebView webView;
    @Bind(R.id.no_message_layout_textview)
    TextView noMessageTV;
    @Bind(R.id.recyclerview_layout_refreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recyclerview_layout_recyclerview)
    RecyclerView recyclerView;

    private Toolbar toolbar;

    private List<HelpMesage> datalist;
    private BaseRecyclerAdapter<HelpMesage> adapter;
    private Page page;
    private String phone = "400-8859-120";

    private static final int UPDATE_VIEW = 121;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_VIEW:
                    adapter.notifyDataSetChanged();
                    complete();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_call_center);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.help_text);

        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //        webView.destroy();
    }

    private void initData() {

        //        webView.loadUrl("http://www.baidu.com");
        //        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        //        webView.setWebViewClient(new WebViewClient() {
        //            @Override
        //            public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //                // TODO Auto-generated method stub
        //                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
        //                view.loadUrl(url);
        //                return true;
        //            }
        //        });
        //
        //        //启用支持javascript
        //        WebSettings settings = webView.getSettings();
        //        settings.setJavaScriptEnabled(true);
        //        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//优先使用缓存

        page = new Page();
        page.setPageSize(Integer.MAX_VALUE);
        datalist = new ArrayList<>();

        adapter = new BaseRecyclerAdapter<HelpMesage>(this, datalist) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.call_center_item;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, final int position, HelpMesage item) {
                holder.itemView.setBackgroundResource(R.drawable.selector_bar_select_style);
                holder.getTextView(R.id.call_center_question).setText((position + 1) + ". " + item.getTitile());
                if (!TextUtils.isEmpty(item.getContent()) && item.getContent().contains(phone)) {
                    int index = item.getContent().indexOf(phone);
                    int length = phone.length();
                    SpannableString string = new SpannableString(item.getContent());
                    ForegroundColorSpan span = new ForegroundColorSpan(ContextCompat.getColor(CallCenterActivity
                            .this, R.color.red_e84618));
                    string.setSpan(span, index, index + length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.getTextView(R.id.call_center_answer).setText(string);
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            callPhone();
                        }
                    });
                } else {
                    holder.getTextView(R.id.call_center_answer).setText(item.getContent());
                    holder.itemView.setOnClickListener(null);
                }
            }
        };

        noMessageTV.setText("暂时没有任何消息~");
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.no_data);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        noMessageTV.setCompoundDrawables(null, drawable, null, null);
    }

    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getHelpMessage();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        recyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        recyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        recyclerView.setAdapter(adapter);

        //主动获取数据
        getHelpMessage();
    }

    /**
     * 拨打电话
     */
    private void callPhone() {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            Uri data = Uri.parse("tel:" + phone);
            intent.setData(data);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            ToastUtils.showToast("您的设备没有拨号功能");
        }
    }

    @Override
    public void onBackPressed() {
        //        if (webView.canGoBack()) {
        //            webView.goBack();//返回上一页面
        //            return;
        //        }
        super.onBackPressed();
    }

    private void getHelpMessage() {

        swipeRefreshLayout.setRefreshing(true);

        RaspberryCallback<ListPageResponse<HelpMesage>> callback = new
                RaspberryCallback<ListPageResponse<HelpMesage>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                Logger.e("Help", getString(R.string.get_help_msg_fail_text) + e.getLocalizedMessage());
                ToastUtils.showToast(getString(R.string.get_help_msg_fail_text));
                handler.sendEmptyMessage(UPDATE_VIEW);
            }

            @Override
            public void onSuccess(ListPageResponse<HelpMesage> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        datalist.clear();
                        datalist.addAll(response.getData());
                    } else {
                        Logger.e("Help", getString(R.string.get_help_msg_fail_text) + response.getMsg());
                        ToastUtils.showToast(getString(R.string.get_help_msg_fail_text) + response.getMsg());
                    }
                } else {
                    Logger.e("Doctor", getString(R.string.search_doctor_fail_text));
                    ToastUtils.showToast(getString(R.string.search_doctor_fail_text));
                }
                handler.sendEmptyMessage(UPDATE_VIEW);
            }
        };

        callback.setMainThread(false);

        HttpProtocol.queryHelp(page, callback);
    }

    private void complete() {
        swipeRefreshLayout.setRefreshing(false);
        if (datalist.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            noMessageTV.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            noMessageTV.setVisibility(View.VISIBLE);
        }
    }
}
