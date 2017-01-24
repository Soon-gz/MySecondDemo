package com.shkjs.nim.chat.session.viewholder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.TextView;

import com.netease.nim.uikit.common.ui.imageview.HeadImageView;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.shkjs.nim.R;
import com.shkjs.nim.chat.session.attachment.VCardAttachment;

/**
 * Created by xiaohu on 2016/9/8.
 * <p>
 * 名片消息界面展示
 */
public class MsgViewHolderVCard extends MsgViewHolderBase {

    private TextView doctorName;
    private HeadImageView doctorIcon;

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_vcard;
    }

    @Override
    protected void inflateContentView() {
        doctorName = findView(R.id.doctor_name);
        doctorIcon = findView(R.id.doctor_icon);
    }

    @Override
    protected void bindContentView() {

        VCardAttachment attachment = (VCardAttachment) message.getAttachment();
        if (null != attachment) {
            doctorName.setText(attachment.getName());

            // 异步从cache or NOS加载图片
            ImageLoader.getInstance().displayImage(attachment.getIcon(), doctorIcon, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (loadedImage != null){
                        doctorIcon.setImageBitmap(loadedImage);
                    }else {
                        doctorIcon.setImageResource(R.drawable.avatar_def);
                    }
                }
            });
        }
    }

    @Override
    protected void onItemClick() {
        super.onItemClick();

        Intent intent = new Intent("com.shkjs.patient.doctor_detail");
        intent.putExtra("doctorId", ((VCardAttachment) message.getAttachment()).getId());
        context.sendBroadcast(intent);
    }
}
