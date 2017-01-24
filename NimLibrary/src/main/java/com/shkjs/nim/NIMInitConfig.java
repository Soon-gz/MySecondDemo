package com.shkjs.nim;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;

import com.netease.nim.uikit.ImageLoaderKit;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nim.uikit.cache.FriendDataCache;
import com.netease.nim.uikit.cache.NimUserInfoCache;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nim.uikit.contact.ContactProvider;
import com.netease.nim.uikit.contact.core.query.PinYin;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderThumbBase;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.avchat.AVChatManager;
import com.netease.nimlib.sdk.avchat.model.AVChatData;
import com.netease.nimlib.sdk.msg.MessageNotifierCustomization;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.team.constant.TeamFieldEnum;
import com.netease.nimlib.sdk.team.model.IMMessageFilter;
import com.netease.nimlib.sdk.team.model.UpdateTeamAttachment;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.netease.nimlib.sdk.uinfo.model.NimUserInfo;
import com.orhanobut.logger.Logger;
import com.shkjs.nim.cache.AppCache;
import com.shkjs.nim.chat.AVChatProfile;
import com.shkjs.nim.chat.activity.AVChatActivity;
import com.shkjs.nim.chat.config.Preferences;
import com.shkjs.nim.chat.config.UserPreferences;
import com.shkjs.nim.chat.session.SessionHelper;
import com.shkjs.nim.em.ClientType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xiaohu on 2016/9/9.
 * <p/>
 * 初始化IM相关设置
 */
public class NIMInitConfig {

    public static void init(Builder builder, ClientType type) {

        NIMClient.init(builder.context, builder.loginInfo, builder.options);

        if (inMainProcess(builder.context)) {
            // init pinyin
            PinYin.init(builder.context);
            PinYin.validate();

            // 初始化UIKit模块
            initUIKit(builder.context, builder.infoProvider, builder.contactProvider, type);

            // 注册通知消息过滤器
            NIMClient.getService(MsgService.class).registerIMMessageFilter(builder.messageFilter);

            // 初始化消息提醒
            //            NIMClient.toggleNotification(UserPreferences.getNotificationToggle());
            NIMClient.toggleNotification(false);

            // 注册网络通话来电
            AVChatManager.getInstance().observeIncomingCall(builder.avChatIncomingCallObserver, true);

        }
    }

    private static void initUIKit(Context context, UserInfoProvider infoProvider, ContactProvider contactProvider,
                                  ClientType type) {
        // 初始化，需要传入用户信息提供者
        NimUIKit.init(context, infoProvider, contactProvider);

        // 设置地理位置提供者。如果需要发送地理位置消息，该参数必须提供。如果不需要，可以忽略。
        //        NimUIKit.setLocationProvider(new NimDemoLocationProvider());

        // 会话窗口的定制初始化。
        SessionHelper.init(type);

        // 通讯录列表定制初始化
        //        NimUIKit.setContactEventListener(new ContactEventListener() {...});
    }

    /**
     * 是否在主进程
     *
     * @param context
     * @return
     */
    public static boolean inMainProcess(Context context) {
        String packageName = context.getPackageName();
        String processName = getProcessName(context);
        return packageName.equals(processName);
    }

    /**
     * 获取当前进程名
     *
     * @param context
     * @return 进程名
     */
    public static final String getProcessName(Context context) {
        String processName = null;

        // ActivityManager
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));

        while (true) {
            for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
                if (info.pid == android.os.Process.myPid()) {
                    processName = info.processName;

                    break;
                }
            }

            // go home
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }

            // take a rest and again
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static class Builder {

        private Context context = null;
        private LoginInfo loginInfo = null;
        private SDKOptions options = null;

        private UserInfoProvider infoProvider;
        private ContactProvider contactProvider;

        private MessageNotifierCustomization messageNotifierCustomization;

        private IMMessageFilter messageFilter;

        private Observer<AVChatData> avChatIncomingCallObserver;


        public Builder(Context context) {
            this.context = context;
            AppCache.setContext(context);
            initData();
        }

        public Builder create() {
            return this;
        }

        public Builder setLoginInfo(LoginInfo loginInfo) {
            this.loginInfo = loginInfo;
            return this;
        }

        public Builder setSDKOptions(SDKOptions options) {
            this.options = options;
            return this;
        }

        public Builder setUserInfoProvider(UserInfoProvider infoProvider) {
            this.infoProvider = infoProvider;
            return this;
        }

        public Builder setContactProvider(ContactProvider contactProvider) {
            this.contactProvider = contactProvider;
            return this;
        }

        public Builder setMessageNotifierCustomization(MessageNotifierCustomization messageNotifierCustomization) {
            this.messageNotifierCustomization = messageNotifierCustomization;
            return this;
        }

        public Builder setIMMessageFilter(IMMessageFilter messageFilter) {
            this.messageFilter = messageFilter;
            return this;
        }

        public Builder setAVChatIncomingCallObserver(Observer<AVChatData> avChatIncomingCallObserver) {
            this.avChatIncomingCallObserver = avChatIncomingCallObserver;
            return this;
        }

        /**
         * 默认设置
         */
        private void initData() {
            loginInfo = getLoginInfo();
            infoProvider = getInfoProvider();
            contactProvider = getContactProvider();
            messageFilter = getMessageFilter();
            messageNotifierCustomization = getMessageNotifierCustomization();
            avChatIncomingCallObserver = getAVChatIncomingCallObserver();
            options = getOptions();
        }

        /**
         * 读取本地缓存用户登录信息
         *
         * @return 用户登录信息
         */
        private LoginInfo getLoginInfo() {
            String account = Preferences.getUserAccount();
            String token = Preferences.getUserToken();

            if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
                //                AppCache.setAccount(account.toLowerCase());
                AppCache.setAccount(account);
                return new LoginInfo(account, token);
            } else {
                return null;
            }
        }

        /**
         * 网易sdk配置
         *
         * @return 配置项
         */
        public SDKOptions getOptions() {
            SDKOptions options = new SDKOptions();

            // 如果将新消息通知提醒托管给SDK完成，需要添加以下配置。
            StatusBarNotificationConfig config = UserPreferences.getStatusConfig();
            if (config == null) {
                config = new StatusBarNotificationConfig();
            }
            // 点击通知需要跳转到的界面
            //            config.notificationEntrance = WelcomeActivity.class;
            //            config.notificationSmallIconId = R.drawable.ic_stat_notify_msg;

            // 通知铃声的uri字符串
            config.notificationSound = "android.resource://com.shkjs.im/raw/msg";

            // 呼吸灯配置
            config.ledARGB = Color.GREEN;
            config.ledOnMs = 1000;
            config.ledOffMs = 1500;

            options.statusBarNotificationConfig = config;
            AppCache.setNotificationConfig(config);
            UserPreferences.setStatusConfig(config);

            // 配置保存图片，文件，log等数据的目录
            String sdkPath = Environment.getExternalStorageDirectory() + "/" + context.getPackageName() + "/nim";
            options.sdkStorageRootPath = sdkPath;

            // 配置数据库加密秘钥
            options.databaseEncryptKey = "SHKJS";

            // 配置是否需要预下载附件缩略图
            options.preloadAttach = true;

            // 配置附件缩略图的尺寸大小，
            options.thumbnailSize = MsgViewHolderThumbBase.getImageMaxEdge();

            // 用户信息提供者
            options.userInfoProvider = infoProvider;

            // 定制通知栏提醒文案（可选，如果不定制将采用SDK默认文案）
            options.messageNotifierCustomization = messageNotifierCustomization;

            return options;
        }

        /**
         * 获取用户信息提供者
         *
         * @return 用户信息提供者
         */
        public UserInfoProvider getInfoProvider() {
            UserInfoProvider infoProvider = new UserInfoProvider() {
                @Override
                public UserInfo getUserInfo(String account) {
                    UserInfo user = NimUserInfoCache.getInstance().getUserInfo(account);
                    if (user == null || com.raspberry.library.util.TextUtils.isEmpty(user.getName()) || com.raspberry
                            .library.util.TextUtils.isEmpty(user.getAvatar())) {
                        NimUserInfoCache.getInstance().getUserInfoFromRemote(account, null);
                    }

                    return user;
                }

                @Override
                public int getDefaultIconResId() {
                    return R.drawable.avatar_def;
                }

                @Override
                public Bitmap getTeamIcon(String teamId) {
                    Drawable drawable = context.getResources().getDrawable(R.drawable.nim_avatar_group);
                    if (drawable instanceof BitmapDrawable) {
                        return ((BitmapDrawable) drawable).getBitmap();
                    }

                    return null;
                }

                @Override
                public Bitmap getAvatarForMessageNotifier(String account) {
                    /**
                     * 注意：这里最好从缓存里拿，如果读取本地头像可能导致UI进程阻塞，导致通知栏提醒延时弹出。
                     */
                    UserInfo user = getUserInfo(account);
                    return (user != null) ? ImageLoaderKit.getNotificationBitmapFromCache(user) : null;
                }

                @Override
                public String getDisplayNameForMessageNotifier(String account, String sessionId, SessionTypeEnum
                        sessionType) {
                    String nick = null;
                    if (sessionType == SessionTypeEnum.P2P) {
                        nick = NimUserInfoCache.getInstance().getAlias(account);
                    } else if (sessionType == SessionTypeEnum.Team) {
                        nick = TeamDataCache.getInstance().getTeamNick(sessionId, account);
                        if (TextUtils.isEmpty(nick)) {
                            nick = NimUserInfoCache.getInstance().getAlias(account);
                        }
                    }
                    // 返回null，交给sdk处理。如果对方有设置nick，sdk会显示nick
                    if (TextUtils.isEmpty(nick)) {
                        return null;
                    }

                    return nick;
                }
            };
            return infoProvider;
        }

        /**
         * 获取联系人提供者
         *
         * @return 联系人提供者
         */
        public ContactProvider getContactProvider() {
            ContactProvider contactProvider = new ContactProvider() {
                @Override
                public List<UserInfoProvider.UserInfo> getUserInfoOfMyFriends() {
                    List<NimUserInfo> nimUsers = NimUserInfoCache.getInstance().getAllUsersOfMyFriend();
                    List<UserInfoProvider.UserInfo> users = new ArrayList<>(nimUsers.size());
                    if (!nimUsers.isEmpty()) {
                        users.addAll(nimUsers);
                    }

                    return users;
                }

                @Override
                public int getMyFriendsCount() {
                    return FriendDataCache.getInstance().getMyFriendCounts();
                }

                @Override
                public String getUserDisplayName(String account) {
                    return NimUserInfoCache.getInstance().getUserDisplayName(account);
                }
            };
            return contactProvider;
        }

        /**
         * 获取通知栏提醒文案
         *
         * @return 通知栏提醒文案
         */
        public MessageNotifierCustomization getMessageNotifierCustomization() {
            MessageNotifierCustomization messageNotifierCustomization = new MessageNotifierCustomization() {
                @Override
                public String makeNotifyContent(String nick, IMMessage message) {
                    return null; // 采用SDK默认文案
                }

                @Override
                public String makeTicker(String nick, IMMessage message) {
                    return null; // 采用SDK默认文案
                }

            };
            return messageNotifierCustomization;
        }

        /**
         * 获取通知消息过滤器（如果过滤则该消息不存储不上报）
         *
         * @return 通知消息过滤器
         */
        public IMMessageFilter getMessageFilter() {
            IMMessageFilter messageFilter = new IMMessageFilter() {
                @Override
                public boolean shouldIgnore(IMMessage message) {
                    if (UserPreferences.getMsgIgnore() && message.getAttachment() != null) {
                        if (message.getAttachment() instanceof UpdateTeamAttachment) {
                            UpdateTeamAttachment attachment = (UpdateTeamAttachment) message.getAttachment();
                            for (Map.Entry<TeamFieldEnum, Object> field : attachment.getUpdatedFields().entrySet()) {
                                if (field.getKey() == TeamFieldEnum.ICON) {
                                    return true;
                                }
                            }
                            //                        } else if (message.getAttachment() instanceof AVChatAttachment) {
                            //                            return true;
                        }
                    }
                    return false;
                }
            };
            return messageFilter;
        }

        public Observer<AVChatData> getAVChatIncomingCallObserver() {
            Observer<AVChatData> avChatDataObserver = new Observer<AVChatData>() {
                @Override
                public void onEvent(AVChatData data) {
                    String extra = data.getExtra();
                    Logger.e("Extra", "Extra Message->" + extra);
                    // 有网络来电打开AVChatActivity
                    AVChatProfile.getInstance().setAVChatting(true);
                    //接听只有患者端
                    AVChatActivity.launch(AppCache.getContext(), data, AVChatActivity.FROM_BROADCASTRECEIVER,
                            ClientType.PATIENT);
                    //                    AVChatCallActivity.launch(AppCache.getContext(), data, AVChatActivity
                    // .FROM_BROADCASTRECEIVER);
                }
            };
            return avChatDataObserver;
        }
    }
}