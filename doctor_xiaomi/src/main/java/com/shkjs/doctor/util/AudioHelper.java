package com.shkjs.doctor.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nimlib.sdk.media.player.AudioPlayer;
import com.netease.nimlib.sdk.media.player.OnPlayListener;
import com.orhanobut.logger.Logger;
import com.raspberry.library.activity.ImagePagerActivity;
import com.raspberry.library.util.DateUtil;
import com.raspberry.library.util.DividerItemDecoration;
import com.raspberry.library.util.TimeFormatUtils;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.MultiImageView;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.activity.MyPatientDetailActivity;
import com.shkjs.doctor.activity.MyPatientsDetailActivity;
import com.shkjs.doctor.adapter.HeadRecyclerviewAdapter;
import com.shkjs.doctor.base.BaseRecyclerAdapter;
import com.shkjs.doctor.base.BaseRecyclerViewHolder;
import com.shkjs.doctor.base.BaseResponse;
import com.shkjs.doctor.bean.BodyContent;
import com.shkjs.doctor.bean.PictureListBean;
import com.shkjs.doctor.bean.VideoConsultBean;
import com.shkjs.doctor.data.OrderSource;
import com.shkjs.doctor.data.PushMessageTypeEm;
import com.shkjs.doctor.data.ReportType;
import com.shkjs.doctor.data.Sex;
import com.shkjs.doctor.bean.UserHealthReports;
import com.raspberry.library.activity.UserInfoBean;
import com.shkjs.doctor.http.HttpBase;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ListResponse;
import com.shkjs.doctor.view.AudioButton;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.qiujuer.common.okhttp.Http;
import net.qiujuer.common.okhttp.core.HttpCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/11/4.
 */

public class AudioHelper {

    public static String audioUrl;

    /**
     * 初始化报告语音
     *
     * @param holder
     * @param item
     */
    public static void initAudio(final BaseRecyclerViewHolder holder, final UserHealthReports item, final AudioPlayer audioPlayer, final Context context) {
        if (null != item.getAttachment()) {
            if (null != item.getAttachment().getVoice()) {
                final List<String> list = new ArrayList<>();
                for (BodyContent str : item.getAttachment().getVoice().getContent()) {
                    list.add(HttpBase.IMGURL + str.getAddress());
                }
                if (list.size() > 0) {
                    Log.i("tag00", "语音地址：" + list.get(0));
                    holder.getView(R.id.health_report_voice_ll).setVisibility(View.VISIBLE);
                    Log.i("tag00","语音时间："+item.getAttachment().getVoice().getContent().get(0).getDuration());
                    final long totalTime = item.getAttachment().getVoice().getContent().get(0).getDuration();
                    holder.getTextView(R.id.item_patient_detail_voice_time_tv).setText(TimeFormatUtils.getTime(totalTime));
                    holder.getView(R.id.health_report_voice_btn).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != audioPlayer) {
                                File file = new File(Preference.REPORT_AUDIO_CACHE_PATH + item.getId());
                                playAudio(list.get(0), file, context, audioPlayer);
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
                                        long time = audioPlayer.getDuration() - l;
                                        if (time < 500) {
                                            holder.getTextView(R.id.item_patient_detail_voice_time_tv).setText
                                                    (TimeFormatUtils.getTime(audioPlayer.getDuration()));
                                        } else {
                                            holder.getTextView(R.id.item_patient_detail_voice_time_tv).setText
                                                    (TimeFormatUtils.getTime(time));
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        } else {
            holder.getView(R.id.health_report_voice_ll).setVisibility(View.GONE);
        }
    }

    public static UserInfoBean createUserInfo(PictureListBean.UserInfoBean userInfo) {
        UserInfoBean userInfoBean = new UserInfoBean();
        userInfoBean.setUserName(userInfo.getUserName());
        userInfoBean.setName(userInfo.getName());
        userInfoBean.setBirthday(userInfo.getBirthday());
        userInfoBean.setHeadPortrait(userInfo.getHeadPortrait());
        userInfoBean.setNickName(userInfo.getNickName());
        userInfoBean.setId(userInfo.getId());
        userInfoBean.setBloodType(userInfo.getBloodType());
        userInfoBean.setHeight(userInfo.getHeight());
        userInfoBean.setCreateDate(userInfo.getCreateDate());
        userInfoBean.setInformationConfidential(userInfo.getInformationConfidential());
        userInfoBean.setNotificationSwitch(userInfo.getNotificationSwitch());
        userInfoBean.setPassword(userInfo.getPassword());
        userInfoBean.setSex(userInfo.getSex());
        userInfoBean.setVip(userInfo.getVip());
        userInfoBean.setWeight(userInfo.getWeight());
        return userInfoBean;
    }
    public static UserInfoBean createUserInfo(VideoConsultBean.UserInfoBean userInfo) {
        UserInfoBean userInfoBean = new UserInfoBean();
        userInfoBean.setUserName(userInfo.getUserName());
        userInfoBean.setName(userInfo.getName());
        userInfoBean.setBirthday(userInfo.getBirthday());
        userInfoBean.setHeadPortrait(userInfo.getHeadPortrait());
        userInfoBean.setNickName(userInfo.getNickName());
        userInfoBean.setId(userInfo.getId());
        userInfoBean.setBloodType(userInfo.getBloodType());
        userInfoBean.setHeight(userInfo.getHeight());
        userInfoBean.setCreateDate(userInfo.getCreateDate());
        userInfoBean.setInformationConfidential(userInfo.getInformationConfidential());
        userInfoBean.setNotificationSwitch(userInfo.getNotificationSwitch());
        userInfoBean.setPassword(userInfo.getPassword());
        userInfoBean.setSex(userInfo.getSex());
        userInfoBean.setVip(userInfo.getVip());
        userInfoBean.setWeight(userInfo.getWeight());
        return userInfoBean;
    }

    /**
     * 初始化报告语音
     *
     * @param item
     */
    public static void initAudio(LinearLayout linearLayout, AudioButton audioButton, final TextView audioTime, final UserHealthReports item, final AudioPlayer audioPlayer, final Context context) {
        if (null != item.getAttachment()) {
            if (null != item.getAttachment().getVoice()) {
                final List<String> list = new ArrayList<>();
                for (BodyContent str : item.getAttachment().getVoice().getContent()) {
                    list.add(HttpBase.IMGURL + str.getAddress());
                }
                if (list.size() > 0) {
                    Log.i("tag00", "语音地址：" + list.get(0));
                    linearLayout.setVisibility(View.VISIBLE);
                    Log.i("tag00","语音时间："+item.getAttachment().getVoice().getContent().get(0).getDuration());
                    final long totalTime = item.getAttachment().getVoice().getContent().get(0).getDuration();
                    audioTime.setText(TimeFormatUtils.getTime(totalTime));
                    audioButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != audioPlayer) {
                                File file = new File(Preference.REPORT_AUDIO_CACHE_PATH + item.getId());
                                playAudio(list.get(0), file, context, audioPlayer);
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
                                        long time = audioPlayer.getDuration() - l;
                                        if (time < 500) {
                                            audioTime.setText
                                                    (TimeFormatUtils.getTime(audioPlayer.getDuration()));
                                        } else {
                                            audioTime.setText
                                                    (TimeFormatUtils.getTime(time));
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        } else {
            linearLayout.setVisibility(View.GONE);
        }
    }


    public static void playAudio(final String url, final File file, Context context, final AudioPlayer audioPlayer) {
        if (!file.exists()) {
            try {
                file.createNewFile();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Http.downloadAsync(url, file.getPath(), new HttpCallback<File>() {
                            @Override
                            public void onFailure(Request request, Response response, Exception e) {
                                DialogMaker.dismissProgressDialog();
                            }

                            @Override
                            public void onFinish() {
                                super.onFinish();
                                DialogMaker.dismissProgressDialog();
                            }

                            @Override
                            public void onSuccess(File response, int code) {
                                DialogMaker.dismissProgressDialog();
                                audioPlayer.stop();
                                audioPlayer.setDataSource(response.getPath());
                                audioPlayer.start(AudioManager.STREAM_MUSIC);
                                audioUrl = url;
                            }
                        });
                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (audioPlayer.isPlaying()) {
                audioPlayer.stop();
                audioUrl = "";
                return;
            } else {
                audioPlayer.stop();
                audioPlayer.setDataSource(file.getPath());
                audioPlayer.start(AudioManager.STREAM_MUSIC);
                audioUrl = url;
            }
        }
    }


    /**
     * 展示指定的FrameLayout子控价
     *
     * @param index
     * @param frameLayout
     */
    public static void showFrame(int index, FrameLayout frameLayout) {
        for (int dex = 0; dex < frameLayout.getChildCount(); dex++) {
            if (dex == index) {
                frameLayout.getChildAt(index).setVisibility(View.VISIBLE);
            } else {
                frameLayout.getChildAt(dex).setVisibility(View.GONE);
            }
        }
    }

    /**
     * 初始化recyclerView
     **/
    public static void initRecyclerView(RecyclerView recyclerView, HeadRecyclerviewAdapter<UserHealthReports> adapter, Context context) {
        //设置item动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
    }

    /**
     * 初始化视频的recyclerView
     **/
    public static void initBaseRecyclerView(RecyclerView recyclerView, BaseRecyclerAdapter<UserHealthReports> adapter, Context context) {
        //设置item动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
    }

    /**
     * 设置圆形头像
     * @param activity
     * @param circleImageView
     */
    public static void setCircleImage(Activity activity, CircleImageView circleImageView,String headUrl,int defaultId){
        if (!StringUtil.isEmpty(headUrl)){
            Glide.with(activity).load(HttpBase.IMGURL + headUrl).placeholder(defaultId).thumbnail(0.1f).dontAnimate().into(circleImageView);
        }else {
            circleImageView.setImageResource(defaultId);
        }
    }

    /**
     * 设置性别
     * @param textView
     * @param sex
     */
    public static void setSexText(TextView textView,String sex){
        if (!StringUtil.isEmpty(sex)){
            setTextWithDefault(textView,Sex.getSexCalue(sex));
        }else {
            setTextWithDefault(textView,"保密");
        }
    }

    /**
     * 初始化适配器
     **/
    public static HeadRecyclerviewAdapter<UserHealthReports> initRecyclerAdapter(List<UserHealthReports> list, final UserInfoBean userInfo, final Context context, final AudioPlayer audioPlayer, final boolean isLeft) {
        HeadRecyclerviewAdapter<UserHealthReports> adapter = new HeadRecyclerviewAdapter<UserHealthReports>(context, list) {
            @Override
            public int getItemLayoutId(int viewType) {
                if (viewType == -1) {
                    return R.layout.item_patient_detail_headview;
                } else {
                    return R.layout.item_patient_detail_healthdc;
                }
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, UserHealthReports item) {
                if (position == 0) {
                    Glide.with(context).load(HttpBase.IMGURL + userInfo.getHeadPortrait()).dontAnimate().thumbnail(0.1f).placeholder(R.drawable.default_head_rect).into(holder.getCircleImageView(R.id.mypatient_detail_headimg_iv));
                    holder.getImageView(R.id.main_mypatients_vip).setVisibility(userInfo.getVip() !=null && "1".equals(userInfo.getVip())?View.VISIBLE:View.GONE);
                    setTextWithDefault(holder.getTextView(R.id.item_patient_detail_sex),Sex.getSexCalue(userInfo.getSex() != null ? userInfo.getSex():"SECRECY"));
                    setNameWithDefault(holder.getTextView(R.id.item_patient_detail_username),userInfo.getName(),userInfo.getNickName());
                    setAgeWithDefault(holder.getTextView(R.id.item_patient_detail_age),userInfo.getBirthday());
                    setTextWithDefault(holder.getTextView(R.id.item_patient_detail_blood),userInfo.getBloodType(),"型");
                    setNumWithDefault(holder.getTextView(R.id.item_patient_detail_height),userInfo.getHeight(),"cm");
                    setNumWithDefault(holder.getTextView(R.id.item_patient_detail_weight),userInfo.getWeight(),"kg");
                } else {
                    MultiImageView multiImageView = (MultiImageView) holder.getView(R.id.item_patient_detail_pictures_multiimageview);
                    setNameWithDefault(holder.getTextView(R.id.item_patient_detail_name_tv),item.getPatientName(),item.getPatientName());
                    setDateWithDefaultHHmm(holder.getTextView(R.id.item_patient_detail_time_tv),item.getCreateDate());
                    holder.getTextView(R.id.item_patient_detail_symptom_tv).setText(item.getContent());
                    switch (item.getType()){
                        case CASEHISTORY:
                            holder.getRelativeLayout(R.id.item_patient_detail_platform_rl).setBackgroundResource(R.color.palegreen_85bd84);
                            holder.getTextView(R.id.item_patient_detail_platform_tv).setText("病历报告");
                            break;
                        case PHYSICALEXAM:
                            holder.getRelativeLayout(R.id.item_patient_detail_platform_rl).setBackgroundResource(R.color.palegreen_7fc8ff);
                            holder.getTextView(R.id.item_patient_detail_platform_tv).setText("体检报告");
                            break;
                    }
                    AudioHelper.initAudio(holder, item, audioPlayer, context);
                    initMuitlPicture(multiImageView, item, context);
                }
            }
        };
        return adapter;
    }
    /**
     * 初始化适配器
     **/
    public static BaseRecyclerAdapter<UserHealthReports> initBaseRecyAdapter(List<UserHealthReports> list, final Context context, final AudioPlayer audioPlayer) {
        HeadRecyclerviewAdapter<UserHealthReports> adapter = new HeadRecyclerviewAdapter<UserHealthReports>(context, list) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_patient_detail_healthdc;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, UserHealthReports item) {
                    MultiImageView multiImageView = (MultiImageView) holder.getView(R.id.item_patient_detail_pictures_multiimageview);
                    setNameWithDefault(holder.getTextView(R.id.item_patient_detail_name_tv),item.getPatientName(),item.getPatientName());
                    setDateWithDefaultHHmm(holder.getTextView(R.id.item_patient_detail_time_tv),item.getCreateDate());
                    holder.getTextView(R.id.item_patient_detail_symptom_tv).setText(item.getContent());
                    switch (item.getType()){
                        case CASEHISTORY:
                            holder.getRelativeLayout(R.id.item_patient_detail_platform_rl).setBackgroundResource(R.color.palegreen_85bd84);
                            holder.getTextView(R.id.item_patient_detail_platform_tv).setText("病历报告");
                            break;
                        case PHYSICALEXAM:
                            holder.getRelativeLayout(R.id.item_patient_detail_platform_rl).setBackgroundResource(R.color.palegreen_7fc8ff);
                            holder.getTextView(R.id.item_patient_detail_platform_tv).setText("体检报告");
                            break;
                    }
                    AudioHelper.initAudio(holder, item, audioPlayer, context);
                    initMuitlPicture(multiImageView, item, context);
            }
        };
        return adapter;
    }

    public static void setAgeWithDefault(TextView textView,String birthDay){
        Log.i("tag00","出生年月日毫秒值："+birthDay);
        if (!StringUtil.isEmpty(birthDay)){
            if (birthDay.contains("-") && Long.parseLong(birthDay) > 0){
                textView.setText(DateUtil.getAgeByDate(birthDay));
            }else {
                textView.setText(DateUtil.getAgeByLong(Long.parseLong(birthDay)));
            }
        }else {
            textView.setText("保密");
        }
    }

    public static void setNameWithDefault(TextView textView,String name,String nickName){
        if (!StringUtil.isEmpty(name)){
            textView.setText(name);
        }else if (!StringUtil.isEmpty(nickName)){
            textView.setText(nickName);
        }else {
            textView.setText("用户");
        }
    }

    public static void setTextWithDefault(TextView textView,String s){
        if (!StringUtil.isEmpty(s)){
            textView.setText(s);
        }else {
            textView.setText("保密");
        }
    }
    public static void setWeekDayWithDefault(TextView textView,String dialogdate,String createdate){
        if (!StringUtil.isEmpty(dialogdate)){
            textView.setText(DateUtil.DateToWeek(new Date(Long.parseLong(dialogdate))));
        }else {
            textView.setText(DateUtil.DateToWeek(new Date(Long.parseLong(createdate))));
        }
    }

    public static void setDateWithDefault(TextView textView,String s){
        if (!StringUtil.isEmpty(s)){
            if (s.contains("-")){
                textView.setText(s.substring(0,10));
            }else {
                textView.setText(DateUtil.getFormatTimeFromTimestamp(Long.parseLong(s),"yyyy-MM-dd"));
            }
        }
    }
    public static void setDateWithDefaultHHmm(TextView textView,String s){
        if (!StringUtil.isEmpty(s)){
            if (s.contains("-")){
                textView.setText(s.substring(0,10));
            }else {
                textView.setText(DateUtil.getFormatTimeFromTimestamp(Long.parseLong(s),"yyyy-MM-dd HH:mm"));
            }
        }
    }

    public static void setTextWithDefaultBySelf(TextView textView,String s,String defaultStr){
        if (!StringUtil.isEmpty(s)){
            textView.setText(s);
        }else {
            textView.setText(defaultStr);
        }
    }
    public static void setTextWithDefault(TextView textView,String s,String other){
        if (!StringUtil.isEmpty(s)){
            textView.setText(s+other);
        }else {
            textView.setText("保密");
        }
    }
    public static void setNumWithDefault(TextView textView,int s,String other){
        if ( s != 0){
            textView.setText(s+other);
        }else {
            textView.setText("保密");
        }
    }

    /**
     * 初始化视频咨询适配器
     **/
    public static BaseRecyclerAdapter<UserHealthReports> initBaseRecyclerAdapter(List<UserHealthReports> list, final UserInfoBean userInfo, final Context context, final AudioPlayer audioPlayer, final boolean isLeft) {
        BaseRecyclerAdapter<UserHealthReports> adapter = new HeadRecyclerviewAdapter<UserHealthReports>(context, list) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.item_video_detail_healthrp;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, int position, UserHealthReports item) {
                MultiImageView multiImageView = (MultiImageView) holder.getView(R.id.item_patient_detail_pictures_multiimageview);
                if (isLeft) {
                    holder.getTextView(R.id.item_patient_detail_name_tv).setVisibility(View.GONE);
                    holder.getTextView(R.id.item_patient_detail_time_tv).setVisibility(View.GONE);
                } else {
                    holder.getTextView(R.id.item_patient_detail_name_tv).setVisibility(View.VISIBLE);
                    holder.getTextView(R.id.item_patient_detail_time_tv).setVisibility(View.VISIBLE);
                    holder.getTextView(R.id.item_patient_detail_name_tv).setText(item.getPatientName());
                    setDateWithDefault(holder.getTextView(R.id.item_patient_detail_time_tv),item.getCreateDate());
                }
                holder.getTextView(R.id.item_patient_detail_symptom_tv).setText(item.getContent());
                AudioHelper.initAudio(holder, item, audioPlayer, context);
                initMuitlPicture(multiImageView, item, context);
            }
        };
        return adapter;
    }

    /**
     * 初始化报告图片显示
     *
     * @param imageView
     * @param item
     */
    public static void initMuitlPicture(MultiImageView imageView, UserHealthReports item, final Context context) {
        if (null != item.getAttachment()) {
            if (null != item.getAttachment().getPicture()) {
                final List<String> list = new ArrayList<>();
                for (BodyContent str : item.getAttachment().getPicture().getContent()) {
                    list.add(HttpBase.IMGURL + str.getAddress());
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
     * 初始化接口回调
     **/
    public static RaspberryCallback<ListResponse<UserHealthReports>> initCallback(final List<UserHealthReports> list, final HeadRecyclerviewAdapter<UserHealthReports> adapter, Context context, final SwipeRefreshLayout swipeRefreshLayout, final LinearLayout linearLayout) {
        RaspberryCallback<ListResponse<UserHealthReports>> callback = new RaspberryCallback<ListResponse<UserHealthReports>>() {
            @Override
            public void onSuccess(ListResponse<UserHealthReports> response, int code) {
                super.onSuccess(response, code);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (HttpProtocol.checkStatus(response, code)) {
                    if (response.getData() != null && response.getData().size() > 0){
                        list.addAll(response.getData());
                        linearLayout.setVisibility(View.GONE);
                    }else {
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        };
        callback.setContext(context);
        callback.setMainThread(true);
        callback.setCancelable(false);
        callback.setShowDialog(true);
        return callback;
    }

    /**
     * 初始化接口回调
     **/
    public static RaspberryCallback<ListResponse<UserHealthReports>> initCallback(final List<UserHealthReports> list, final HeadRecyclerviewAdapter<UserHealthReports> adapter, Context context) {
        RaspberryCallback<ListResponse<UserHealthReports>> callback = new RaspberryCallback<ListResponse<UserHealthReports>>() {
            @Override
            public void onSuccess(ListResponse<UserHealthReports> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    Log.i("tag00", "数据量：" + response.getData().size());
                    list.addAll(response.getData());
                    adapter.notifyDataSetChanged();
                }
            }

        };
        callback.setContext(context);
        callback.setMainThread(true);
        callback.setCancelable(false);
        callback.setShowDialog(true);
        return callback;
    }

    /**
     * 初始化视频接口回调
     **/
    public static RaspberryCallback<ListResponse<UserHealthReports>> initCallback(final List<UserHealthReports> list, final BaseRecyclerAdapter<UserHealthReports> adapter, final Activity context, final LinearLayout linearLayout) {
        RaspberryCallback<ListResponse<UserHealthReports>> callback = new RaspberryCallback<ListResponse<UserHealthReports>>() {
            @Override
            public void onSuccess(ListResponse<UserHealthReports> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    if (response.getData() != null && response.getData().size() > 0){
                        list.addAll(response.getData());
                        context.runOnUiThread(new TimerTask() {
                            @Override
                            public void run() {
                                linearLayout.setVisibility(View.GONE);
                            }
                        });
                    }else {
                        context.runOnUiThread(new TimerTask() {
                            @Override
                            public void run() {
                                linearLayout.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    adapter.notifyDataSetChanged();
                }
            }

        };
        callback.setContext(context);
        callback.setMainThread(true);
        callback.setCancelable(false);
        callback.setShowDialog(true);
        return callback;
    }


    public static RaspberryCallback<BaseResponse> initDeleteCallback(final Activity context) {
        RaspberryCallback<BaseResponse> callback = new RaspberryCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    ToastUtils.showToast("删除患者成功。");
                    ActivityManager.getInstance().finishActivity(MyPatientsDetailActivity.class);
                    Intent intent = context.getIntent();
                    context.setResult(Preference.MYPATIENT_RESULT_CODE,intent);
                }
            }
        };
        callback.setContext(context);
        callback.setMainThread(true);
        callback.setCancelable(false);
        callback.setShowDialog(true);
        return callback;
    }

    public static String getTitle(String titleEm){
        String titleStr = "";
        switch (PushMessageTypeEm.valueOf(titleEm)){
            case ORDER_CANCEL:
                titleStr = "订单取消";
                break;
            case AUTHENTICATION:
                titleStr = "医生认证";
                break;
            case GROUP_DIAGNOSE:
                titleStr = "视频会诊邀请";
                break;
            case RAW_DATA:
                titleStr = "订单消息";
                break;
            case ORDER_FINISH:
                titleStr = "订单完成";
                break;
            case MONTH_CLOSING:
                titleStr = "结算";
                break;
        }
        return titleStr;
    }

    public static String getContent(String contentEm,String action,String createDate){
        String contentStr = "";
        try {
            switch (PushMessageTypeEm.valueOf(action)){
                case ORDER_CANCEL:
                    JSONObject jsonObject = new JSONObject(contentEm);
                    String typeName = "";
                    switch (jsonObject.getString("cancelType")){
                        case "0":
                            typeName = "管理员";
                            break;
                        case "1":
                            typeName = "医生";
                            break;
                        case "2":
                            typeName = "患者";
                            break;
                        case "3":
                            typeName = "系统";
                            break;
                    }
                    contentStr = "尊敬的医生,您好,"+jsonObject.getString("name")+typeName+" 于"+jsonObject.getString("dateTime")+"取消了"+jsonObject.getString("subscribeTime")
                            +"时间段的“"+OrderSource.values()[(jsonObject.getInt("orderSource"))].getMark()+"”的订单，请合理安排该时间段的订单。";
                    break;
                case AUTHENTICATION:
                    JSONObject jsonObject1 = new JSONObject(contentEm);
                    switch (jsonObject1.getString("level")){
                        case Preference.NOTPASS:
                            contentStr = "尊敬的医生,您好,您在医星汇的医师认证审核未通过，具体原因是："+"\n"+jsonObject1.getString("content");
                            break;
                        case Preference.AUTHORITY:
                            contentStr = "尊敬的医生,您好,恭喜您已成功通过医星汇的 “权威医生”认证。";
                            break;
                        case Preference.CERTIFICATION:
                            contentStr = "尊敬的医生,您好,恭喜您已成功通过医星汇的“普通医生”认证。";
                            break;
                    }
                    break;
                case GROUP_DIAGNOSE:
                    JSONObject jsonObject2 = new JSONObject(contentEm);
                    contentStr = "尊敬的医生,您好,"+jsonObject2.getString("createName")+"医生邀请您加入"+jsonObject2.getString("name")+"患者的视频会诊，请尽快确认。";
                    break;
                case RAW_DATA:
                    contentStr = "尊敬的医生,您好,"+contentEm.replaceAll("\"","");
                    break;
                case ORDER_FINISH:
                    JSONObject jsonObject3 = new JSONObject(contentEm);
                    String type = "";
                    OrderSource source = OrderSource.values()[jsonObject3.getInt("orderSource")];
                    switch (source) {
                        case INQUIRY_RESERVE:
                            type = "图文咨询";
                            break;
                        case SIT_DIAGNOSE_RESERVE:
                            type = "视频咨询";
                            break;
                        case GROUP_SIT_DIAGNOSE:
                            type = "视频会诊";
                            break;
                        default:
                            break;
                    }
                    //尊敬的医生，您于XXX时间已完成XXX患者的XXX，账户入账诊金XXX元。createDate
                    String money = BalanceUtils.formatBalance2(jsonObject3.getLong("money"));
                    String moneyStr = !StringUtil.isEmpty(money)?money:"0.00";
                    contentStr = "尊敬的医生,您于"+DateUtil.getFormatTimeFromTimestamp(Long.parseLong(createDate),"yyyy-MM-dd HH:mm")+"已完成"+jsonObject3.getString("userName")+"的"+type+",账户入账诊金"+moneyStr+"元。";
                    break;
                case MONTH_CLOSING:
//                    尊敬的%s医生，医星汇已于%s将您%s的诊金%s结算至您绑定的支付宝账号，请注意查看。
                    JSONObject jsonObject4 = new JSONObject(contentEm);
                    contentStr = "尊敬的医生，医星汇已于"+jsonObject4.getString("dateTime")+"将您"+jsonObject4.getString("yearAndMonth")+"的诊金"+BalanceUtils.formatBalance2(jsonObject4.getLong("money"))+"元结算至您绑定的支付宝账号，请注意查看。";
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contentStr;
    }

    public static void setMoney(TextView textView,long money){
        if (money == 0){
            textView.setText("0.00");
        }else {
            textView.setText(BalanceUtils.formatBalance2(Math.abs(money)));
        }
    }

    public static void initCallBack(RaspberryCallback callback,Activity activity,boolean showDialog){
        callback.setContext(activity);
        callback.setCancelable(false);
        callback.setShowDialog(showDialog);
        callback.setMainThread(true);
    }



}
