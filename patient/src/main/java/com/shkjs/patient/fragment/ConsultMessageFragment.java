package com.shkjs.patient.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.netease.nim.uikit.cache.FriendDataCache;
import com.netease.nim.uikit.recent.RecentContactsCallback;
import com.netease.nim.uikit.recent.viewholder.RecentViewHolder;
import com.netease.nim.uikit.session.helper.MessageHelper;
import com.netease.nim.uikit.uinfo.UserInfoHelper;
import com.netease.nim.uikit.uinfo.UserInfoObservable;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.orhanobut.logger.Logger;
import com.raspberry.library.util.RecyclerViewUtlis;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.nim.chat.session.SessionHelper;
import com.shkjs.nim.recyclerview.BaseRecyclerAdapter;
import com.shkjs.nim.recyclerview.BaseRecyclerViewHolder;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by LENOVO on 2016/8/17.
 * <p/>
 * 咨询消息Fragment，展示咨询消息记录，包括视频和图文
 */

public class ConsultMessageFragment extends BaseFragment {
    /**
     * 预加载标志，默认值为false，设置为true，表示已经预加载完成，可以加载数据
     */
    private boolean isPrepared;

    private Context context;

    @Bind(R.id.no_message_layout_textview)
    TextView noMessageTV;
    @Bind(R.id.recyclerview_layout_refreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recyclerview_layout_recyclerview)
    RecyclerView recyclerView;

    private BaseRecyclerAdapter<RecentContact> adapter;

    // 置顶功能可直接使用，也可作为思路，供开发者充分利用RecentContact的tag字段
    public static final long RECENT_TAG_STICKY = 1; // 联系人置顶tag

    // data
    private List<RecentContact> dataList;

    private boolean msgLoaded = false;

    private UserInfoObservable.UserInfoObserver userInfoObserver;
    private List<RecentContact> loadedRecents;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {

        context = getActivity();

        View view = inflater.inflate(R.layout.fragment_consult_message, null);//使用container会异常

        //绑定控件
        ButterKnife.bind(this, view);
        initData();
        initListener();
        requestMessages(true);
        registerObservers(true);

        isPrepared = true;
        setlazyLoad();//加载数据
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        registerObservers(false);
    }

    private void initData() {

        dataList = new ArrayList<>();

        adapter = new BaseRecyclerAdapter<RecentContact>(context, dataList) {

            @Override
            public int getItemViewType(int position) {
                return super.getItemViewType(position);
            }

            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.consult_video_sitting_message_item;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, final RecentContact item) {
                holder.getTextView(R.id.consult_message_type_tv).setText(item.getFromNick());
                holder.getTextView(R.id.consult_message_date_time_tv).setText(item.getTime() + "");
                holder.setClickListener(R.id.consult_message_join_tv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SessionHelper.startP2PSession(context, item.getContactId());
                    }
                });

                //由于没有设置头像，加载出来是空白，故暂时不加载
                //                Glide.with(context).load(NimUIKit.getUserInfoProvider().getUserInfo(item
                // .getContactId()).getAvatar())
                //                        .transform(new CircleTransform(context)).into(holder.getImageView(R.id
                //                        .consult_message_doctor_icon_iv));
            }
        };

    }

    private void initListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                msgLoaded = false;
                requestMessages(true);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(context));// 布局管理器。
        recyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        recyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        //        recyclerView.addItemDecoration(new ListViewDecoration());// 添加分割线。
        // 添加滚动监听。
        recyclerView.addOnScrollListener(mOnScrollListener);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 加载数据的方法，只要保证isPrepared和isVisible都为true的时候才往下执行开始加载数据
     */
    @Override
    protected void setlazyLoad() {
        super.setlazyLoad();
        if (!isPrepared || !isVisible) {
            return;
        }

        notifyDataSetChanged();
    }

    private void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
        boolean empty = dataList.isEmpty() && msgLoaded;
        noMessageTV.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(!empty ? View.VISIBLE : View.GONE);
    }


    /**
     * 加载更多
     */
    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!recyclerView.canScrollVertically(1)) {// 手指不能向上滑动了
                // TODO 这里有个注意的地方，如果你刚进来时没有数据，但是设置了适配器，这个时候就会触发加载更多，需要开发者判断下是否有数据，如果有数据才去加载更多。
                ToastUtils.showToast("加载更多");
            }
        }
    };

    private void requestMessages(boolean delay) {
        if (msgLoaded) {
            return;
        }
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (msgLoaded) {
                    return;
                }
                // 查询最近联系人列表数据
                NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(new RequestCallbackWrapper<List<RecentContact>>() {

                    @Override
                    public void onResult(int code, List<RecentContact> recents, Throwable exception) {
                        if (code != ResponseCode.RES_SUCCESS || recents == null) {
                            return;
                        }
                        loadedRecents = recents;

                        // 此处如果是界面刚初始化，为了防止界面卡顿，可先在后台把需要显示的用户资料和群组资料在后台加载好，然后再刷新界面
                        //
                        msgLoaded = true;
                        if (isAdded()) {
                            onRecentContactsLoaded();
                        }
                    }
                });
            }
        }, delay ? 250 : 0);
    }

    private void onRecentContactsLoaded() {
        dataList.clear();
        if (loadedRecents != null) {
            dataList.addAll(loadedRecents);
            loadedRecents = null;
        }
        refreshMessages(true);

        swipeRefreshLayout.setRefreshing(false);

        if (callback != null) {
            callback.onRecentContactsLoaded();
        }
    }

    private void refreshMessages(boolean unreadChanged) {
        sortRecentContacts(dataList);
        notifyDataSetChanged();

        if (unreadChanged) {

            // 方式一：累加每个最近联系人的未读（快）
            /*
            int unreadNum = 0;
            for (RecentContact r : dataList) {
                unreadNum += r.getUnreadCount();
            }
            */

            // 方式二：直接从SDK读取（相对慢）
            int unreadNum = NIMClient.getService(MsgService.class).getTotalUnreadCount();

            if (callback != null) {
                callback.onUnreadCountChange(unreadNum);
            }
        }
    }

    /**
     * **************************** 排序 ***********************************
     */
    private void sortRecentContacts(List<RecentContact> list) {
        if (list.size() == 0) {
            return;
        }
        Collections.sort(list, comp);
    }

    private static Comparator<RecentContact> comp = new Comparator<RecentContact>() {

        @Override
        public int compare(RecentContact o1, RecentContact o2) {
            // 先比较置顶tag
            long sticky = (o1.getTag() & RECENT_TAG_STICKY) - (o2.getTag() & RECENT_TAG_STICKY);
            if (sticky != 0) {
                return sticky > 0 ? -1 : 1;
            } else {
                long time = o1.getTime() - o2.getTime();
                return time == 0 ? 0 : (time > 0 ? -1 : 1);
            }
        }
    };

    /**
     * ********************** 收消息，处理状态变化 ************************
     */
    private void registerObservers(boolean register) {
        MsgServiceObserve service = NIMClient.getService(MsgServiceObserve.class);
        service.observeRecentContact(messageObserver, register);
        service.observeMsgStatus(statusObserver, register);
        service.observeRecentContactDeleted(deleteObserver, register);
        service.observeRevokeMessage(revokeMessageObserver, register);
        FriendDataCache.getInstance().registerFriendDataChangedObserver(friendDataChangedObserver, register);
        if (register) {
            registerUserInfoObserver();
        } else {
            unregisterUserInfoObserver();
        }
    }

    Observer<List<RecentContact>> messageObserver = new Observer<List<RecentContact>>() {
        @Override
        public void onEvent(List<RecentContact> messages) {
            int index;
            for (RecentContact msg : messages) {
                index = -1;
                for (int i = 0; i < dataList.size(); i++) {
                    if (msg.getContactId().equals(dataList.get(i).getContactId()) && msg.getSessionType() ==
                            (dataList.get(i).getSessionType())) {
                        index = i;
                        break;
                    }
                }

                if (index >= 0) {
                    dataList.remove(index);
                }

                dataList.add(msg);
            }

            refreshMessages(true);
        }
    };

    Observer<IMMessage> statusObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage message) {
            int index = getItemIndex(message.getUuid());
            if (index >= 0 && index < dataList.size()) {
                RecentContact item = dataList.get(index);
                item.setMsgStatus(message.getStatus());
                refreshViewHolderByIndex(index);
            }
        }
    };

    Observer<RecentContact> deleteObserver = new Observer<RecentContact>() {
        @Override
        public void onEvent(RecentContact recentContact) {
            if (recentContact != null) {
                for (RecentContact item : dataList) {
                    if (TextUtils.equals(item.getContactId(), recentContact.getContactId()) && item.getSessionType()
                            == recentContact.getSessionType()) {
                        dataList.remove(item);
                        refreshMessages(true);
                        break;
                    }
                }
            } else {
                dataList.clear();
                refreshMessages(true);
            }
        }
    };

    Observer<IMMessage> revokeMessageObserver = new Observer<IMMessage>() {
        @Override
        public void onEvent(IMMessage message) {
            if (message == null) {
                return;
            }

            MessageHelper.getInstance().onRevokeMessage(message);
        }
    };

    private int getItemIndex(String uuid) {
        for (int i = 0; i < dataList.size(); i++) {
            RecentContact item = dataList.get(i);
            if (TextUtils.equals(item.getRecentMessageId(), uuid)) {
                return i;
            }
        }

        return -1;
    }

    protected void refreshViewHolderByIndex(final int index) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //                Object tag = ListViewUtil.getViewHolderByIndex(listView, index);
                Object tag = RecyclerViewUtlis.getViewHolderByIndex(recyclerView, index);
                if (tag instanceof RecentViewHolder) {
                    RecentViewHolder viewHolder = (RecentViewHolder) tag;
                    viewHolder.refreshCurrentItem();
                }
            }
        });
    }

    private void registerUserInfoObserver() {
        if (userInfoObserver == null) {
            userInfoObserver = new UserInfoObservable.UserInfoObserver() {
                @Override
                public void onUserInfoChanged(List<String> accounts) {
                    refreshMessages(false);
                }
            };
        }

        UserInfoHelper.registerObserver(userInfoObserver);
    }

    private void unregisterUserInfoObserver() {
        if (userInfoObserver != null) {
            UserInfoHelper.unregisterObserver(userInfoObserver);
        }
    }

    FriendDataCache.FriendDataChangedObserver friendDataChangedObserver = new FriendDataCache
            .FriendDataChangedObserver() {
        @Override
        public void onAddedOrUpdatedFriends(List<String> accounts) {
            refreshMessages(false);
        }

        @Override
        public void onDeletedFriends(List<String> accounts) {
            refreshMessages(false);
        }

        @Override
        public void onAddUserToBlackList(List<String> account) {
            refreshMessages(false);
        }

        @Override
        public void onRemoveUserFromBlackList(List<String> account) {
            refreshMessages(false);
        }
    };

    private RecentContactsCallback callback = new RecentContactsCallback() {
        @Override
        public void onRecentContactsLoaded() {
            Logger.e("查询成功");
        }

        @Override
        public void onUnreadCountChange(int unreadCount) {
            Logger.e("新增新消息：" + unreadCount);
        }

        @Override
        public void onItemClick(final RecentContact recent) {

        }

        @Override
        public String getDigestOfAttachment(MsgAttachment attachment) {
            return null;
        }

        @Override
        public String getDigestOfTipMsg(RecentContact recent) {
            return null;
        }
    };
}
