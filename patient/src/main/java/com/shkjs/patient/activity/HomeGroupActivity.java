package com.shkjs.patient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.google.gson.internal.LinkedTreeMap;
import com.orhanobut.logger.Logger;
import com.raspberry.library.glide.CircleTransform;
import com.raspberry.library.util.JsonUtils;
import com.raspberry.library.util.TextUtils;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.RecycleViewDecoration;
import com.raspberry.library.view.SwipeItemLayout;
import com.shkjs.nim.recyclerview.BaseRecyclerAdapter;
import com.shkjs.nim.recyclerview.BaseRecyclerViewHolder;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.bean.UserGroup;
import com.shkjs.patient.bean.UserInfo;
import com.shkjs.patient.cache.DataCache;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/10/10.
 * <p>
 * 我的家庭组
 */

public class HomeGroupActivity extends BaseActivity implements View.OnClickListener {

    private static final int INITIAL = 121;//初始状态
    private static final int MANAGER = 122;//户主
    private static final int MEMBER = 123;//成员
    private static final int ADD_MEMBER = 124;//添加成员

    @Bind(R.id.home_group_initial_ll)
    LinearLayout initialLL;
    @Bind(R.id.home_group_not_initial_ll)
    LinearLayout notInitialLL;
    @Bind(R.id.home_group_initial_add_btn)
    Button addMemberBtn;
    @Bind(R.id.recyclerview_layout_refreshlayout)
    SwipeRefreshLayout refreshLayout;
    @Bind(R.id.recyclerview_layout_recyclerview)
    RecyclerView recyclerView;

    private Toolbar toolbar;

    private List<UserInfo> datalist;
    private BaseRecyclerAdapter<UserInfo> adapter;

    private int DEFAULT = INITIAL;//默认为初始状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_group);

        //初始化控件
        ButterKnife.bind(this);

        initToolbar(INITIAL);//默认初始状态

        initData();
        initListener();

        //        queryUserGroup();
        getGroupMembers();
    }

    private void initData() {

        datalist = new ArrayList<>();
        adapter = new BaseRecyclerAdapter<UserInfo>(this, datalist) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.home_group_member_item;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, final int position, UserInfo item) {
                Glide.with(HomeGroupActivity.this).load(HttpBase.BASE_OSS_URL + item.getHeadPortrait()).transform(new
                        CircleTransform(HomeGroupActivity.this)).into(holder.getImageView(R.id.member_icon));

                holder.setText(R.id.member_name, item.getNickName());
                holder.setText(R.id.member_number, item.getUserName());

                final SwipeItemLayout layout = (SwipeItemLayout) holder.getView(R.id.swipe_item_layout);
                layout.setSwipeAble(true);
                if (DEFAULT == MEMBER) {
                    holder.getTextView(R.id.swipe_left_tv).setText("退出");
                    holder.setClickListener(R.id.swipe_left_tv, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            layout.close();
                            quitGroup(position);
                        }
                    });
                } else {
                    if (item.getId().equals(DataCache.getInstance().getUserInfo().getId())) {
                        if (datalist.size() > 1) {
                            layout.setSwipeAble(false);
                        } else {
                            holder.getTextView(R.id.swipe_left_tv).setText("删除");
                            holder.setClickListener(R.id.swipe_left_tv, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    layout.close();
                                    deleteUserGroup();
                                }
                            });
                        }
                    } else {
                        holder.getTextView(R.id.swipe_left_tv).setText("删除");
                        holder.setClickListener(R.id.swipe_left_tv, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                layout.close();
                                deleteMember(position);
                            }
                        });
                    }
                }
            }
        };
    }

    private void initListener() {
        addMemberBtn.setOnClickListener(this);
        refreshLayout.setEnabled(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));// 布局管理器。
        recyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        recyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        recyclerView.addItemDecoration(new RecycleViewDecoration(HomeGroupActivity.this));//分割线
        recyclerView.setAdapter(adapter);
    }

    private void initToolbar(final int type) {
        DEFAULT = type;
        switch (type) {
            case INITIAL:
                toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.mine_home_group, R.drawable
                        .add_select_style);
                setVisibility(true);
                break;
            case MANAGER:
                //                toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.home_group_member);
                toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.mine_home_group, R.drawable
                        .add_select_style);
                setVisibility(false);
                break;
            case MEMBER:
                //                toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string
                // .mine_home_group_master);
                toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.mine_home_group);
                setVisibility(false);
                break;
            default:
                break;
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setMenuBtnClickListener(this);

    }

    private void setVisibility(boolean isShow) {
        if (isShow) {
            initialLL.setVisibility(View.VISIBLE);
            notInitialLL.setVisibility(View.GONE);
        } else {
            initialLL.setVisibility(View.GONE);
            notInitialLL.setVisibility(View.VISIBLE);
        }
    }

    private void addGroupMember() {
        Intent intent = new Intent(this, AddMemberActivity.class);
        startActivityForResult(intent, ADD_MEMBER);
    }

    private void queryUserGroup() {
        RaspberryCallback<ObjectResponse<UserGroup>> callback = new RaspberryCallback<ObjectResponse<UserGroup>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
            }

            @Override
            public void onSuccess(ObjectResponse<UserGroup> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        if (null == response.getData()) {
                            initToolbar(INITIAL);
                        } else {
                            if (response.getData().getUser().getId() == DataCache.getInstance().getUserInfo().getId()) {
                                initToolbar(MANAGER);
                            } else {
                                initToolbar(MEMBER);
                            }
                        }
                    } else {
                        Logger.e("HomeGroup", getString(R.string.get_home_group_failed) + response.getMsg());
                        ToastUtils.showToast(getString(R.string.get_home_group_failed));
                    }
                } else {
                    Logger.e("HomeGroup", getString(R.string.get_home_group_failed));
                    ToastUtils.showToast(getString(R.string.get_home_group_failed));
                }
            }
        };

        callback.setMainThread(true);
        callback.setContext(this);
        callback.setCancelable(false);

        HttpProtocol.queryUserGroup(callback);
    }

    private void getGroupMembers() {
        RaspberryCallback<ObjectResponse<Map<String, Object>>> callback = new
                RaspberryCallback<ObjectResponse<Map<String, Object>>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
            }

            @Override
            public void onSuccess(ObjectResponse<Map<String, Object>> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        if (null == response.getData()) {
                            initToolbar(INITIAL);
                        } else {
                            if (Boolean.parseBoolean(response.getData().get("isHz").toString())) {
                                initToolbar(MANAGER);
                            } else {
                                initToolbar(MEMBER);
                            }
                            List<LinkedTreeMap<String, Object>> list = (List<LinkedTreeMap<String, Object>>) response
                                    .getData().get("list");

                            datalist.clear();
                            if (list.size() <= 0) {
                                initToolbar(INITIAL);
                            } else {
                                for (int i = 0; i < list.size(); i++) {
                                    String json = JsonUtils.mapToJson(list.get(i));
                                    if (!TextUtils.isEmpty(json)) {
                                        UserInfo userInfo = JsonUtils.jsonToObject(json, UserInfo.class);
                                        if (userInfo.getId() != DataCache.getInstance().getUserInfo().getId()) {
                                            if (DEFAULT == MEMBER) {
                                                userInfo.setNickName(userInfo.getNickName() + " (创建人)");
                                            }
                                            datalist.add(userInfo);
                                        } else {
                                            userInfo.setNickName(userInfo.getNickName() + " (创建人)");
                                            datalist.add(0, userInfo);
                                        }
                                    } else {
                                        initToolbar(INITIAL);
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Logger.e("HomeGroup", getString(R.string.get_home_group_failed) + response.getMsg());
                        ToastUtils.showToast(getString(R.string.get_home_group_failed));
                    }
                } else {
                    Logger.e("HomeGroup", getString(R.string.get_home_group_failed));
                    ToastUtils.showToast(getString(R.string.get_home_group_failed));
                }
            }
        };

        callback.setMainThread(true);
        callback.setContext(this);
        callback.setCancelable(false);

        //        HttpProtocol.queryUserGroup(callback);
        HttpProtocol.queryGroupInfo(callback);
    }

    private void deleteMember(final int position) {
        RaspberryCallback<ObjectResponse<Integer>> callback = new RaspberryCallback<ObjectResponse<Integer>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
            }

            @Override
            public void onSuccess(ObjectResponse<Integer> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response
                            .getData()) {
                        datalist.remove(position);
                        adapter.notifyDataSetChanged();
                        if (response.getData() <= 1) {//家庭组只有自己时，删除该家庭组
                            deleteUserGroup();
                        }
                        return;
                    }
                }
                ToastUtils.showToast("删除成员失败");
            }
        };

        callback.setMainThread(true);
        callback.setContext(this);
        callback.setCancelable(false);

        HttpProtocol.removeMember(datalist.get(position).getId(), callback);
    }

    private void deleteUserGroup() {
        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
            }

            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        datalist.clear();
                        adapter.notifyDataSetChanged();
                        initToolbar(INITIAL);
                    }
                }
            }
        };

        callback.setMainThread(true);
        callback.setCancelable(false);

        HttpProtocol.removeUserGroup(callback);
    }

    private void quitGroup(int position) {
        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
            }

            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        finish();
                    } else {
                        Logger.e("HomeGroup", getString(R.string.get_home_group_failed) + response.getMsg());
                        ToastUtils.showToast(getString(R.string.get_home_group_failed));
                    }
                } else {
                    Logger.e("HomeGroup", getString(R.string.get_home_group_failed));
                    ToastUtils.showToast(getString(R.string.get_home_group_failed));
                }
            }
        };

        callback.setMainThread(true);
        callback.setContext(this);
        callback.setCancelable(false);

        HttpProtocol.quitUserGroup(callback);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_group_initial_add_btn:
            case R.id.toolbar_menu:
                addGroupMember();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_MEMBER) {
                if (null != data) {//添加成功，重新查询
                    if (data.getBooleanExtra(AddMemberActivity.class.getSimpleName(), false)) {
                        getGroupMembers();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
