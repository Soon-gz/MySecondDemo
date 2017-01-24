package com.shkjs.nim.chat.session;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.session.SessionCustomization;
import com.netease.nim.uikit.session.SessionEventListener;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nim.uikit.session.activity.P2PMessageActivity;
import com.netease.nim.uikit.session.module.MsgForwardFilter;
import com.netease.nim.uikit.session.module.MsgRevokeFilter;
import com.netease.nim.uikit.team.model.TeamExtras;
import com.netease.nim.uikit.team.model.TeamRequestCode;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.avchat.model.AVChatAttachment;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.shkjs.nim.R;
import com.shkjs.nim.chat.session.action.VCardAction;
import com.shkjs.nim.chat.session.attachment.VCardAttachment;
import com.shkjs.nim.chat.session.attachparser.VCardAttachParser;
import com.shkjs.nim.chat.session.viewholder.MsgViewHolderAVChat;
import com.shkjs.nim.chat.session.viewholder.MsgViewHolderVCard;
import com.shkjs.nim.em.ClientType;

import java.util.ArrayList;

/**
 * Created by xiaohu on 2016/9/8.
 */
public class SessionHelper {

    private static SessionCustomization p2pCustomization;
    private static SessionCustomization teamCustomization;

    private static ClientType type = ClientType.PATIENT;

    public static void init(ClientType type) {
        SessionHelper.type = type;
        // 注册自定义消息附件解析器
        NIMClient.getService(MsgService.class).registerCustomAttachmentParser(new VCardAttachParser());

        // 注册各种扩展消息类型的显示ViewHolder
        registerViewHolders();

        // 设置会话中点击事件响应处理
        setSessionListener();

        // 注册消息转发过滤器
        registerMsgForwardFilter();
        // 注册消息撤回过滤器
        registerMsgRevokeFilter();

    }

    public static void startP2PSession(Context context, String account) {
        NimUIKit.startChatting(context, account, SessionTypeEnum.P2P, getP2pCustomization(), null);
    }

    public static void startP2PSession(Context context, String account, P2PMessageActivity.Status status) {
        NimUIKit.startChatting(context, account, SessionTypeEnum.P2P, getP2pCustomization(), null, status);
    }

    public static void startP2PSession(Context context, String account, P2PMessageActivity.Status status, String name) {
        NimUIKit.startChatting(context, account, SessionTypeEnum.P2P, getP2pCustomization(status), null, status, name);
    }

    public static void startTeamSession(Context context, String tid) {
        NimUIKit.startChatting(context, tid, SessionTypeEnum.Team, getTeamCustomization(), null);
    }

    private static void registerViewHolders() {
        //TODO  lxh 20161115 需要屏蔽音视频消息展示
        NimUIKit.registerMsgItemViewHolder(AVChatAttachment.class, MsgViewHolderAVChat.class);
        NimUIKit.registerMsgItemViewHolder(VCardAttachment.class, MsgViewHolderVCard.class);
    }

    private static void setSessionListener() {
        SessionEventListener listener = new SessionEventListener() {
            @Override
            public void onAvatarClicked(Context context, IMMessage message) {
                // 一般用于打开用户资料页面
            }

            @Override
            public void onAvatarLongClicked(Context context, IMMessage message) {
                // 一般用于群组@功能，或者弹出菜单，做拉黑，加好友等功能
            }
        };

        NimUIKit.setSessionListener(listener);
    }


    /**
     * 消息转发过滤器
     * //TODO lxh 屏蔽消息转发 20161115
     */
    private static void registerMsgForwardFilter() {
        NimUIKit.setMsgForwardFilter(new MsgForwardFilter() {
            @Override
            public boolean shouldIgnore(IMMessage message) {
                //                if (message.getDirect() == MsgDirectionEnum.In && (message.getAttachStatus() ==
                // AttachStatusEnum
                //                        .transferring || message.getAttachStatus() == AttachStatusEnum.fail)) {
                //                    // 接收到的消息，附件没有下载成功，不允许转发
                //                    return true;
                //                }
                //                if (message.getAttachment() instanceof VCardAttachment) {//名片消息不允许转发
                //                    return true;
                //                }
                return true;
            }
        });
    }

    /**
     * 消息撤回过滤器
     * //TODO lxh 屏蔽消息撤回 20161115
     */
    private static void registerMsgRevokeFilter() {
        NimUIKit.setMsgRevokeFilter(new MsgRevokeFilter() {
            @Override
            public boolean shouldIgnore(IMMessage message) {
                return true;
            }
        });
    }

    // 定制化单聊界面。如果使用默认界面，返回null即可
    private static SessionCustomization getP2pCustomization() {
        if (p2pCustomization == null) {
            p2pCustomization = new SessionCustomization() {
                // 由于需要Activity Result， 所以重载该函数。
                @Override
                public void onActivityResult(final Activity activity, int requestCode, int resultCode, Intent data) {
                    super.onActivityResult(activity, requestCode, resultCode, data);

                }

                @Override
                public MsgAttachment createStickerAttachment(String category, String item) {
                    return null;
                }
            };

            // 背景
            //            p2pCustomization.backgroundColor = Color.BLUE;
            //            p2pCustomization.backgroundUri = "file:///android_asset/xx/bk.jpg";
            //            p2pCustomization.backgroundUri = "file:///sdcard/Pictures/bk.png";
            //            p2pCustomization.backgroundUri = "android.resource://com.netease.nim.demo/drawable/bk"

            // 定制加号点开后可以包含的操作， 默认已经有图片，视频等消息了
            ArrayList<BaseAction> actions = new ArrayList<>();
            // TODO SW 自定义聊天界面更多，不需要语音聊天和视频聊天   20161009
            //            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            //                actions.add(new AVChatAction(AVChatType.AUDIO));
            //                actions.add(new AVChatAction(AVChatType.VIDEO));
            //            }

            if (type == ClientType.DOCTOR) {
                actions.add(new VCardAction());
                // 定制ActionBar右边的按钮，可以加多个
                ArrayList<SessionCustomization.OptionsButton> buttons = new ArrayList<>();
                SessionCustomization.OptionsButton completeMsgButton = new SessionCustomization.OptionsButton() {
                    @Override
                    public void onClick(Context context, View view, String sessionId) {
                        Intent mIntent = new Intent("ACTION_NAME");
                        mIntent.putExtra("yaner", "发送广播，相当于在这里传送数据");
                        //发送广播
                        context.sendBroadcast(mIntent);
                        ((Activity) context).finish();
                    }
                };

                completeMsgButton.iconId = R.drawable.actionbar_finish_select;
                buttons.add(completeMsgButton);

                p2pCustomization.buttons = buttons;
            }

            p2pCustomization.actions = actions;
            p2pCustomization.withSticker = true;

            // 定制ActionBar右边的按钮，可以加多个
            //            ArrayList<SessionCustomization.OptionsButton> buttons = new ArrayList<>();
            //            SessionCustomization.OptionsButton cloudMsgButton = new SessionCustomization.OptionsButton() {
            //                @Override
            //                public void onClick(Context context, View view, String sessionId) {
            //                    initPopuptWindow(context, view, sessionId, SessionTypeEnum.P2P);
            //                }
            //            };
            //            cloudMsgButton.iconId = R.drawable.nim_ic_messge_history;
            //
            //            SessionCustomization.OptionsButton infoButton = new SessionCustomization.OptionsButton() {
            //                @Override
            //                public void onClick(Context context, View view, String sessionId) {
            //                    MessageInfoActivity.startActivity(context, sessionId); //打开聊天信息
            //                }
            //            };
            //
            //            infoButton.iconId = R.drawable.nim_ic_message_actionbar_p2p_add;
            //
            //            buttons.add(cloudMsgButton);
            //            buttons.add(infoButton);
            //            p2pCustomization.buttons = buttons;
        }

        return p2pCustomization;
    }

    // 定制化单聊界面。如果使用默认界面，返回null即可
    private static SessionCustomization getP2pCustomization(P2PMessageActivity.Status status) {
        //        if (p2pCustomization == null) {
        p2pCustomization = new SessionCustomization() {
            // 由于需要Activity Result， 所以重载该函数。
            @Override
            public void onActivityResult(final Activity activity, int requestCode, int resultCode, Intent data) {
                super.onActivityResult(activity, requestCode, resultCode, data);

            }

            @Override
            public MsgAttachment createStickerAttachment(String category, String item) {
                return null;
            }
        };

        // 背景
        //            p2pCustomization.backgroundColor = Color.BLUE;
        //            p2pCustomization.backgroundUri = "file:///android_asset/xx/bk.jpg";
        //            p2pCustomization.backgroundUri = "file:///sdcard/Pictures/bk.png";
        //            p2pCustomization.backgroundUri = "android.resource://com.netease.nim.demo/drawable/bk"

        // 定制加号点开后可以包含的操作， 默认已经有图片，视频等消息了
        ArrayList<BaseAction> actions = new ArrayList<>();
        // TODO SW 自定义聊天界面更多，不需要语音聊天和视频聊天   20161009
        //            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        //                actions.add(new AVChatAction(AVChatType.AUDIO));
        //                actions.add(new AVChatAction(AVChatType.VIDEO));
        //            }

        if (type.equals(ClientType.DOCTOR)) {
            actions.add(new VCardAction());
            // 定制ActionBar右边的按钮，可以加多个
            ArrayList<SessionCustomization.OptionsButton> buttons = new ArrayList<>();
            Log.i("tag00", status.getMark());
            if (!status.equals(P2PMessageActivity.Status.COMPLETEDOCTOR)) {
                SessionCustomization.OptionsButton completeMsgButton = new SessionCustomization.OptionsButton() {
                    @Override
                    public void onClick(Context context, View view, String sessionId) {
                        Intent mIntent = new Intent("ACTION_NAME");
                        mIntent.putExtra("yaner", "发送广播，相当于在这里传送数据");
                        //发送广播
                        context.sendBroadcast(mIntent);
                        ((Activity) context).finish();
                    }
                };
                completeMsgButton.iconId = R.drawable.actionbar_finish_select;
                buttons.add(completeMsgButton);
            }

            p2pCustomization.buttons = buttons;
        }

        p2pCustomization.actions = actions;
        p2pCustomization.withSticker = true;

        // 定制ActionBar右边的按钮，可以加多个
        //            ArrayList<SessionCustomization.OptionsButton> buttons = new ArrayList<>();
        //            SessionCustomization.OptionsButton cloudMsgButton = new SessionCustomization.OptionsButton() {
        //                @Override
        //                public void onClick(Context context, View view, String sessionId) {
        //                    initPopuptWindow(context, view, sessionId, SessionTypeEnum.P2P);
        //                }
        //            };
        //            cloudMsgButton.iconId = R.drawable.nim_ic_messge_history;
        //
        //            SessionCustomization.OptionsButton infoButton = new SessionCustomization.OptionsButton() {
        //                @Override
        //                public void onClick(Context context, View view, String sessionId) {
        //                    MessageInfoActivity.startActivity(context, sessionId); //打开聊天信息
        //                }
        //            };
        //
        //            infoButton.iconId = R.drawable.nim_ic_message_actionbar_p2p_add;
        //
        //            buttons.add(cloudMsgButton);
        //            buttons.add(infoButton);
        //            p2pCustomization.buttons = buttons;
        //        }

        return p2pCustomization;
    }

    // 定制化群聊界面。如果使用默认界面，返回null即可
    private static SessionCustomization getTeamCustomization() {
        if (teamCustomization == null) {
            teamCustomization = new SessionCustomization() {
                @Override
                public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
                    if (requestCode == TeamRequestCode.REQUEST_CODE) {
                        if (resultCode == Activity.RESULT_OK) {
                            String reason = data.getStringExtra(TeamExtras.RESULT_EXTRA_REASON);
                            boolean finish = reason != null && (reason.equals(TeamExtras.RESULT_EXTRA_REASON_DISMISS)
                                    || reason.equals(TeamExtras.RESULT_EXTRA_REASON_QUIT));
                            if (finish) {
                                activity.finish(); // 退出or解散群直接退出多人会话
                            }
                        }
                    }
                }

                @Override
                public MsgAttachment createStickerAttachment(String category, String item) {
                    return null;
                }
            };

            // 定制加号点开后可以包含的操作， 默认已经有图片，视频等消息了
            ArrayList<BaseAction> actions = new ArrayList<>();
            teamCustomization.actions = actions;

            // 定制ActionBar右边的按钮，可以加多个
            //            ArrayList<SessionCustomization.OptionsButton> buttons = new ArrayList<>();
            //            SessionCustomization.OptionsButton cloudMsgButton = new SessionCustomization.OptionsButton() {
            //                @Override
            //                public void onClick(Context context, View view, String sessionId) {
            //                    initPopuptWindow(context, view, sessionId, SessionTypeEnum.Team);
            //                }
            //            };
            //            cloudMsgButton.iconId = R.drawable.nim_ic_messge_history;
            //
            //            SessionCustomization.OptionsButton infoButton = new SessionCustomization.OptionsButton() {
            //                @Override
            //                public void onClick(Context context, View view, String sessionId) {
            //                    Team team = TeamDataCache.getInstance().getTeamById(sessionId);
            //                    if (team != null && team.isMyTeam()) {
            //                        NimUIKit.startTeamInfo(context, sessionId);
            //                    } else {
            //                        Toast.makeText(context, R.string.team_invalid_tip, Toast.LENGTH_SHORT).show();
            //                    }
            //                }
            //            };
            //            infoButton.iconId = R.drawable.nim_ic_message_actionbar_team;
            //            buttons.add(cloudMsgButton);
            //            buttons.add(infoButton);
            //            teamCustomization.buttons = buttons;

            teamCustomization.withSticker = true;
        }

        return teamCustomization;
    }

}