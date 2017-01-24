package com.shkjs.nim.chat.session.attachparser;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;
import com.shkjs.nim.chat.session.attachment.VCardAttachment;

/**
 * Created by xiaohu on 2016/9/8.
 * <p/>
 * 名片消息解析类
 */
public class VCardAttachParser implements MsgAttachmentParser {

    @Override
    public MsgAttachment parse(String json) {
        VCardAttachment attachment = new VCardAttachment();
        JSONObject object = JSON.parseObject(json);
        attachment.fromJson(object);
        return attachment;
    }

    public static String packData(JSONObject data) {
        if (data != null) {
            return data.toJSONString();
        }

        return null;
    }
}
