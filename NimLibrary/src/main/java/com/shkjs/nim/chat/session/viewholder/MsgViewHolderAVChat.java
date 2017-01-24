package com.shkjs.nim.chat.session.viewholder;

import android.widget.TextView;

import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;
import com.netease.nimlib.sdk.avchat.model.AVChatAttachment;
import com.shkjs.nim.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by xiaohu on 2016/9/8.
 */
public class MsgViewHolderAVChat extends MsgViewHolderBase {

    private TextView timeTV;

    /**
     * @param time utc时间,单位毫秒
     * @return such as 1'21"
     */
    private static String getTime(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss", new Locale("zh", "CN"));
        String str = simpleDateFormat.format(time);
        String[] strs = str.split("\\:");
        str = "";
        if (!strs[0].equals("00")) {
            str = Integer.parseInt(strs[0]) + "'";
        }
        if (Integer.parseInt(strs[1]) > 9) {
            str = str + Integer.parseInt(strs[1]) + "\"";
        } else {
            str = str + "0" + Integer.parseInt(strs[1]) + "\"";
        }
        return str;
    }

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_av_video;
    }

    @Override
    protected void inflateContentView() {
        timeTV = findView(R.id.message_item_av_video_time);
    }

    @Override
    protected void bindContentView() {
        AVChatAttachment attachment = (AVChatAttachment) message.getAttachment();
        if (null != attachment) {
            timeTV.setText(getTime(attachment.getDuration()));
        }
    }
}
