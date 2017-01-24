package com.shkjs.patient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;

/**
 * Created by xiaohu on 2016/12/27.
 * <p>
 * 预约须知
 */

public class OrderNoticeActivity extends BaseActivity {

    private Toolbar toolbar;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_notice);

        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.order_notice_text);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        textView = ((TextView) findViewById(R.id.text_tv));

        initString();
    }

    private void initString() {
        String str = "1、若医生在超过预约的时间段一个小时后没有对您进行视频呼叫或者没有回复您诊疗建议报告，则全额退款。\n2" +
                "、预约成功后，预约信息会保存在您的咨询消息中，请您注意您预约的时间段，并在该时间段登录医星汇等待医生的视频呼叫；预约时间段内医生会对您视频呼叫，若由于您的原因（包括您的网络原因、不在线、不接听等）造成无法视频，则系统将通过短信和系统消息的形式通知您，若超过三次未接通，则服务结束，且视为您主动取消订单，将按取消订单的扣款规则进行扣款，请您务必保持网络畅通。\n3、若您点击提交订单，则表示您已知晓以上条款并同意《医星汇用户服务协议》。";
        String key = "《医星汇用户服务协议》";
        int index = str.indexOf(key);
        SpannableString string = new SpannableString(str);
        string.setSpan(new OrderNoticeActivity.Clickable(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderNoticeActivity.this, UserAgreementActivity.class));
            }
        }), index, index + key.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        string.setSpan(new OrderNoticeActivity.NoUnderlineSpan(), index, index + key.length(), Spannable
                .SPAN_EXCLUSIVE_INCLUSIVE);
        textView.setText(string);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    class Clickable extends ClickableSpan implements View.OnClickListener {
        private final View.OnClickListener mListener;

        public Clickable(View.OnClickListener l) {
            mListener = l;
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v);
        }
    }

    class NoUnderlineSpan extends UnderlineSpan {

        @Override

        public void updateDrawState(TextPaint ds) {
            //设置可点击文本的字体颜色
            ds.setColor(ContextCompat.getColor(OrderNoticeActivity.this, R.color.blue_2bbbe6));
            //没有下划线
            ds.setUnderlineText(false);

        }
    }
}
