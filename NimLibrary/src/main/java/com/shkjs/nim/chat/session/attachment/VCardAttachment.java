package com.shkjs.nim.chat.session.attachment;

import com.alibaba.fastjson.JSONObject;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.shkjs.nim.chat.session.attachparser.VCardAttachParser;

/**
 * Created by xiaohu on 2016/9/8.
 */
public class VCardAttachment implements MsgAttachment {

    private static final String VCARD_TYPE = "vcard";
    private static final String MSG_TYPE = "type";
    private static final String MSG_DATA = "data";
    private static final String DOCTOR_ID = "id";
    private static final String DOCTOR_NAME = "name";
    private static final String DOCTOR_ICON = "icon";

    private long id;
    private String name;
    private String icon;

    public VCardAttachment() {
    }

    public VCardAttachment(long id, String name, String icon) {
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    @Override
    public String toJson(boolean send) {
        return VCardAttachParser.packData(packData());
    }

    /**
     * 解析附件内容。
     *
     * @param data 收到的数据
     */
    public void fromJson(JSONObject data) {
        if (data != null) {
            parseData(data);
        }
    }

    protected void parseData(JSONObject data) {
        String type = data.getString(MSG_TYPE);
        JSONObject object = data.getJSONObject(MSG_DATA);
        if (null != object) {
            id = object.getLong(DOCTOR_ID);
            name = object.getString(DOCTOR_NAME);
            icon = object.getString(DOCTOR_ICON);
        }
    }

    /**
     * 组装名片消息
     *
     * @return 返回组装后的数据
     */
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        data.put(DOCTOR_ID, id);
        data.put(DOCTOR_NAME, name);
        data.put(DOCTOR_ICON, icon);
        JSONObject object = new JSONObject();
        object.put(MSG_TYPE, VCARD_TYPE);
        object.put(MSG_DATA, data);
        return object;
    }

}
