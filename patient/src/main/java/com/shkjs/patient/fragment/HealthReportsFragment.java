package com.shkjs.patient.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.netease.nimlib.sdk.media.player.AudioPlayer;
import com.netease.nimlib.sdk.media.player.OnPlayListener;
import com.orhanobut.logger.Logger;
import com.raspberry.library.activity.ImagePagerActivity;
import com.raspberry.library.glide.CircleTransform;
import com.raspberry.library.util.CustomAlertDialog;
import com.raspberry.library.util.RecyclerViewUtlis;
import com.raspberry.library.util.TextUtils;
import com.raspberry.library.util.TimeFormatUtils;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.MultiImageView;
import com.raspberry.library.view.RecycleViewDecoration;
import com.shkjs.nim.recyclerview.BaseRecyclerAdapter;
import com.shkjs.nim.recyclerview.BaseRecyclerViewHolder;
import com.shkjs.patient.Preference;
import com.shkjs.patient.R;
import com.shkjs.patient.activity.HealthReportDetailActivity;
import com.shkjs.patient.base.BaseFragment;
import com.shkjs.patient.bean.BodyContent;
import com.shkjs.patient.bean.DiagnoseCase;
import com.shkjs.patient.bean.Page;
import com.shkjs.patient.bean.UserHealthReports;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ListPageResponse;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.qiujuer.common.okhttp.Http;
import net.qiujuer.common.okhttp.core.HttpCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by LENOVO on 2016/8/17.
 * <p/>
 * 健康档案
 */

public class HealthReportsFragment extends BaseFragment {

    /**
     * 预加载标志，默认值为false，设置为true，表示已经预加载完成，可以加载数据
     */
    private boolean isPrepared;

    private static final int UPDATE_VIEW = 121;//刷新界面
    private static final String TIME_FORMAT = "yyyy-MM-dd HH:mm";//时间展示格式

    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;              //下拉刷新系统消息
    private TextView noMessageTV;
    private RecyclerView recyclerView;

    private List<UserHealthReports> datalist;
    private BaseRecyclerAdapter<UserHealthReports> adapter;
    private BaseRecyclerAdapter<DiagnoseCase> diagnoseAdapter;

    private Page page;
    private int pageNumber = 1;
    private boolean isFirst = true;//是否为第一次下拉刷新
    private boolean isPullDown = true;//是否为下拉刷新
    private boolean isRefreshing = false;//是否正在刷新
    private boolean isAllowPullUp = true;//是否允许上拉加载更多

    private static final int DELETE = 121;

    //语音播放
    private AudioPlayer audioPlayer;
    private String audioUrl = "";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_VIEW:
                    adapter.notifyDataSetChanged();
                    completeQuery();
                    break;
                default:
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {

        context = getActivity();

        View view = inflater.inflate(R.layout.fragment_health_report, null);//使用container会异常

        findViews(view);
        initData();
        initListener();

        isPrepared = true;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setlazyLoad();//加载数据
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != audioPlayer) {
            audioPlayer.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != audioPlayer) {
            audioPlayer.stop();
            audioPlayer = null;
        }
    }

    private void findViews(View view) {
        noMessageTV = (TextView) view.findViewById(R.id.no_message_layout_textview);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_layout_recyclerview);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.recyclerview_layout_refreshlayout);
    }

    private void initData() {

        noMessageTV.setText("请点击右上角“+”上传相关报告信息~");
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.no_data);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        noMessageTV.setCompoundDrawables(null, drawable, null, null);

        page = new Page();
        audioPlayer = new AudioPlayer(context);
        datalist = new ArrayList<>();

        adapter = new BaseRecyclerAdapter<UserHealthReports>(context, datalist) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.health_report_item;
            }

            @Override
            public void bindData(final BaseRecyclerViewHolder holder, final int position, final UserHealthReports
                    item) {

                holder.getTextView(R.id.health_report_name_tv).setText(item.getPatientName());
                holder.getTextView(R.id.health_report_time_tv).setText(TimeFormatUtils.getLocalTime(TIME_FORMAT, Long
                        .parseLong(item.getCreateDate())));
                holder.getTextView(R.id.health_report_symptom_tv).setText("详情描述：" + item.getContent());

                MultiImageView multiImageView = (MultiImageView) holder.getView(R.id
                        .health_report_pictures_multiimageview);
                initMuitlPicture(multiImageView, item);

                initAudio(holder, item);

                RecyclerView recyclerView = (RecyclerView) holder.getView(R.id
                        .health_report_disease_information_recyclerview);
                initRecyclerView(recyclerView, item);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, HealthReportDetailActivity.class);
                        intent.putExtra(HealthReportDetailActivity.class.getSimpleName(), item);
                        startActivityForResult(intent, DELETE);
                    }
                });

                switch (item.getType()) {
                    case PLATFORM://平台报告
                        holder.getView(R.id.health_report_type_ll).setBackgroundResource(R.color.green_85db84);
                        holder.getTextView(R.id.health_report_type_tv).setText("诊疗报告");
                        holder.itemView.setOnClickListener(null);
                        break;
                    case CASEHISTORY://病历报告
                        holder.getView(R.id.health_report_type_ll).setBackgroundResource(R.color.blue_7fc8ff);
                        holder.getTextView(R.id.health_report_type_tv).setText("病历报告");
                        break;
                    case PHYSICALEXAM://体检报告
                        holder.getView(R.id.health_report_type_ll).setBackgroundResource(R.color.blue_7fc8ff);
                        holder.getTextView(R.id.health_report_type_tv).setText("体检报告");
                        break;
                    default:
                        break;
                }
                holder.setClickListener(R.id.health_report_delete_tv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CustomAlertDialog.dialogExSureCancel("确认删除？", context, new CustomAlertDialog
                                .OnDialogClickListener() {
                            @Override
                            public void doSomeThings() {
                                deleteHealthReport(position);
                            }
                        });
                    }
                });
            }
        };
    }

    /**
     * 初始化医生回复界面
     *
     * @param recyclerView
     * @param report
     */
    private void initRecyclerView(RecyclerView recyclerView, final UserHealthReports report) {
        diagnoseAdapter = new BaseRecyclerAdapter<DiagnoseCase>(context, report.getDiagnoseCases()) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.health_report_doctor_item;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, final int position, final DiagnoseCase item) {

                holder.getTextView(R.id.health_report_disease_information_tv).setText(item.getContent());
                holder.getTextView(R.id.health_report_disease_information_time_tv).setText(TimeFormatUtils
                        .getLocalTime(TIME_FORMAT, Long.parseLong(item.getCreateDate())));
                holder.getTextView(R.id.health_report_doctor_name_tv).setText(item.getDoctorInfo().getName());
                Glide.with(context).load(HttpBase.BASE_OSS_URL + item.getDoctorInfo().getHeadPortrait()).transform
                        (new CircleTransform(context)).placeholder(R.drawable.actionbar_headportrait_small).error(R
                        .drawable.actionbar_headportrait_small).into(holder.getImageView(R.id
                        .health_report_doctor_icon_iv));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, HealthReportDetailActivity.class);
                        intent.putExtra(HealthReportDetailActivity.class.getSimpleName(), report);
                        intent.putExtra("position", position);
                        startActivityForResult(intent, DELETE);
                    }
                });

            }
        };
        //设置item动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new RecycleViewDecoration(context));//分割线
        recyclerView.setAdapter(diagnoseAdapter);
    }

    /**
     * 初始化报告图片显示
     *
     * @param imageView
     * @param item
     */
    private void initMuitlPicture(MultiImageView imageView, UserHealthReports item) {
        if (null != item.getAttachment()) {
            if (null != item.getAttachment().getPicture()) {
                final List<String> list = new ArrayList<>();
                for (BodyContent content : item.getAttachment().getPicture().getContent()) {
                    if (!TextUtils.isEmpty(content.getAddress())) {
                        list.add(HttpBase.BASE_OSS_URL + content.getAddress());
                    }
                }
                imageView.setList(list);
                imageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ImagePagerActivity.startImagePagerActivity(context, list, position, new ImagePagerActivity
                                .ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight()));
                    }
                });
                return;
            }
        }
        imageView.setList(new ArrayList<String>());
    }

    /**
     * 初始化报告语音
     *
     * @param holder
     * @param item
     */
    private void initAudio(final BaseRecyclerViewHolder holder, final UserHealthReports item) {
        if (null != item.getAttachment()) {
            if (null != item.getAttachment().getVoice()) {
                final List<String> list = new ArrayList<>();
                for (BodyContent content : item.getAttachment().getVoice().getContent()) {
                    if (!TextUtils.isEmpty(content.getAddress())) {
                        list.add(HttpBase.BASE_OSS_URL + content.getAddress());
                    }
                }
                if (list.size() > 0) {
                    holder.getView(R.id.health_report_voice_ll).setVisibility(View.VISIBLE);
                    final long totalTime = item.getAttachment().getVoice().getContent().get(0).getDuration();
                    holder.getTextView(R.id.health_report_voice_time_tv).setText(TimeFormatUtils.getTime(totalTime));
                    holder.getView(R.id.health_report_voice_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != audioPlayer) {
                                File file = new File(Preference.REPORT_AUDIO_CACHE_PATH + item.getId());
                                playAudio(list.get(0), holder, file, totalTime);
                                audioPlayer.setOnPlayListener(new OnPlayListener() {
                                    @Override
                                    public void onPrepared() {
                                        Logger.d(audioPlayer.getDuration());
                                    }

                                    @Override
                                    public void onCompletion() {

                                    }

                                    @Override
                                    public void onInterrupt() {

                                    }

                                    @Override
                                    public void onError(String s) {

                                    }

                                    @Override
                                    public void onPlaying(long l) {
                                        long time = totalTime - l;
                                        if (time < 500) {
                                            holder.getTextView(R.id.health_report_voice_time_tv).setText
                                                    (TimeFormatUtils.getTime(totalTime));
                                        } else {
                                            holder.getTextView(R.id.health_report_voice_time_tv).setText
                                                    (TimeFormatUtils.getTime(time));
                                        }
                                    }
                                });
                            }
                        }
                    });
                    return;
                }
            }
            //                if (list.size() > 0) {
            //                    holder.getView(R.id.health_report_voice_ll).setVisibility(View.VISIBLE);
            //                    final long totalTime = item.getAttachment().getVoice().getContent().get(0)
            // .getDuration();
            //                    holder.getTextView(R.id.health_report_voice_time_tv).setText(TimeFormatUtils
            // .getTime(totalTime));
            //                    holder.getView(R.id.health_report_voice_btn).setOnClickListener(new View
            // .OnClickListener() {
            //                        @Override
            //                        public void onClick(View v) {
            //                            if (null != audioPlayer) {
            //                                if (audioUrl.equals(list.get(0)) && audioPlayer.isPlaying()) {
            //                                    audioPlayer.stop();
            //                                    holder.getTextView(R.id.health_report_voice_time_tv).setText
            // (TimeFormatUtils
            //                                            .getTime(totalTime));
            //                                } else {
            //                                    new Thread() {
            //                                        @Override
            //                                        public void run() {
            //                                            playAudio(list.get(0), holder, totalTime);
            //                                        }
            //                                    }.start();
            //                                    audioPlayer.setOnPlayListener(new OnPlayListener() {
            //                                        @Override
            //                                        public void onPrepared() {
            //                                        }
            //
            //                                        @Override
            //                                        public void onCompletion() {
            //                                        }
            //
            //                                        @Override
            //                                        public void onInterrupt() {
            //                                        }
            //
            //                                        @Override
            //                                        public void onError(String s) {
            //                                        }
            //
            //                                        @Override
            //                                        public void onPlaying(long l) {
            //                                            long time = totalTime - l;
            //                                            if (time < 500) {
            //                                                holder.getTextView(R.id.health_report_voice_time_tv)
            // .setText
            //                                                        (TimeFormatUtils.getTime(totalTime));
            //                                            } else {
            //                                                holder.getTextView(R.id.health_report_voice_time_tv)
            // .setText
            //                                                        (TimeFormatUtils.getTime(time));
            //                                            }
            //                                        }
            //                                    });
            //                                }
            //                            }
            //                        }
            //                    });
            //                }
            //                return;
            //        }
        }
        holder.getView(R.id.health_report_voice_ll).setVisibility(View.GONE);
    }

    private void playAudio(final String url, BaseRecyclerViewHolder holder, long totalTime) {
        //        if (audioUrl.equals(url) && audioPlayer.isPlaying()) {
        //            audioPlayer.stop();
        //            holder.getTextView(R.id.health_report_voice_time_tv).setText(TimeFormatUtils.getTime(totalTime));
        //            return;
        //        }
        audioPlayer.stop();
        Logger.d(url);
        audioPlayer.setDataSource(url);
        audioPlayer.start(AudioManager.STREAM_MUSIC);
        audioUrl = url;
    }

    private void playAudio(final String url, BaseRecyclerViewHolder holder, final File file, long totalTime) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (audioUrl.equals(url)) {
            if (audioPlayer.isPlaying()) {
                audioPlayer.stop();
                holder.getTextView(R.id.health_report_voice_time_tv).setText(TimeFormatUtils.getTime(totalTime));
                audioUrl = "";
                return;
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Http.downloadAsync(url, file.getPath(), new HttpCallback<File>() {
                    @Override
                    public void onFailure(Request request, Response response, Exception e) {

                    }

                    @Override
                    public void onSuccess(File response, int code) {
                        audioPlayer.stop();
                        audioPlayer.setDataSource(response.getPath());
                        audioPlayer.start(AudioManager.STREAM_MUSIC);
                        audioUrl = url;
                    }
                });
            }
        }).start();
    }

    private void initListener() {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isPullDown = true;
                queryUserHealthReport(isPullDown);
            }
        });

        //        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
        //            @Override
        //            public void onItemClick(View itemView, int pos) {
        //                Intent intent = new Intent(context, HealthReportDetailActivity.class);
        //                intent.putExtra(HealthReportDetailActivity.class.getSimpleName(), datalist.get(pos));
        //                startActivity(intent);
        //            }
        //        });

        //设置item动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new RecycleViewDecoration(context));//分割线
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && RecyclerViewUtlis.canPullUp(recyclerView)) {
                    if (isAllowPullUp) {
                        isPullDown = false;
                        queryUserHealthReport(isPullDown);
                    } else {
                        ToastUtils.showToast("暂无更多数据~");
                    }
                }
            }
        });

    }

    /**
     * 查询用户健康报告
     */
    private void queryUserHealthReport(final boolean isPullDown) {

        if (isRefreshing) {
            ToastUtils.showToast("正在刷新，请稍后重试");
            return;
        }

        swipeRefreshLayout.setRefreshing(true);

        RaspberryCallback<ListPageResponse<UserHealthReports>> callback = new
                RaspberryCallback<ListPageResponse<UserHealthReports>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                handler.sendEmptyMessage(UPDATE_VIEW);
            }

            @Override
            public void onSuccess(ListPageResponse<UserHealthReports> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        if (isPullDown) {
                            //                            int position;
                            //                            for (UserHealthReports report : response.getData()) {
                            //                                if (datalist.contains(report)) {
                            //                                    position = datalist.indexOf(report);
                            //                                    datalist.remove(position);
                            //                                    datalist.add(position, report);
                            //                                } else {
                            //                                    if (isFirst) {
                            //                                        datalist.add(report);
                            //                                    } else {
                            //                                        datalist.add(0, report);
                            //                                    }
                            //                                }
                            //                            }
                            //                            isFirst = false;
                            datalist.clear();
                            datalist.addAll(response.getData());
                        } else {
                            pageNumber = response.getPageNum();
                            for (UserHealthReports report : response.getData()) {
                                if (!datalist.contains(report)) {
                                    datalist.add(report);
                                }
                            }
                        }
                        page.setPageNum(response.getPageNum());
                        page.setPageSize(response.getPageSize());
                        page.setTotalCount(response.getTotalCount());
                        if (page.getPageNum() * page.getPageSize() >= page.getTotalCount()) {
                            isAllowPullUp = false;
                        }
                    }
                }

                handler.sendEmptyMessage(UPDATE_VIEW);
            }
        };

        callback.setMainThread(false);

        if (isPullDown) {
            page.setPageNum(1);
        } else {
            page.setPageNum(pageNumber + 1);
        }

        HttpProtocol.getReportList(page, callback);
        isRefreshing = true;
    }

    /**
     * 删除报告
     *
     * @param position
     */

    private void deleteHealthReport(final int position) {
        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                handler.sendEmptyMessage(UPDATE_VIEW);
            }

            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        File file = new File(Preference.REPORT_AUDIO_CACHE_PATH + datalist.get(position).getId());
                        if (file.exists()) {
                            file.delete();
                        }
                        datalist.remove(position);
                    } else {
                        ToastUtils.showToast("删除失败，请稍后重试");
                    }
                } else {
                    ToastUtils.showToast("删除失败，请稍后重试");
                }
                handler.sendEmptyMessage(UPDATE_VIEW);
            }
        };

        callback.setMainThread(false);
        callback.setContext(context);
        callback.setCancelable(false);

        HttpProtocol.deleteReport(datalist.get(position).getId(), callback);
    }

    private void completeQuery() {
        isRefreshing = false;
        swipeRefreshLayout.setRefreshing(false);
        if (datalist.size() == 0) {
            noMessageTV.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noMessageTV.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
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
        queryUserHealthReport(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == DELETE) {
                if (null != data) {
                    UserHealthReports reports = (UserHealthReports) data.getSerializableExtra
                            (HealthReportDetailActivity.class.getSimpleName());
                    if (null != reports) {
                        datalist.remove(reports);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }
}

