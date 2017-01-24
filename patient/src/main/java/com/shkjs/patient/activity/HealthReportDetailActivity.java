package com.shkjs.patient.activity;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.netease.nimlib.sdk.media.player.AudioPlayer;
import com.netease.nimlib.sdk.media.player.OnPlayListener;
import com.raspberry.library.activity.ImagePagerActivity;
import com.raspberry.library.glide.CircleTransform;
import com.raspberry.library.util.DisplayUtils;
import com.raspberry.library.util.TextUtils;
import com.raspberry.library.util.TimeFormatUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.nim.recyclerview.BaseRecyclerAdapter;
import com.shkjs.nim.recyclerview.BaseRecyclerViewHolder;
import com.shkjs.patient.Preference;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.bean.BodyContent;
import com.shkjs.patient.bean.UserHealthReports;
import com.shkjs.patient.cache.DataCache;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.shkjs.patient.view.AudioButton;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.qiujuer.common.okhttp.Http;
import net.qiujuer.common.okhttp.core.HttpCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/10/17.
 * <p>
 * 健康报告详情
 */

public class HealthReportDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final String TIME_FROMAT_ONE = "yyyy-MM-dd HH:mm";//时间展示格式
    private static final String TIME_FROMAT_TWO = "HH:mm";//时间展示格式

    @Bind(R.id.health_report_own)
    LinearLayout ownLL;
    @Bind(R.id.health_report_system)
    LinearLayout systemLL;
    @Bind(R.id.health_report_doctor_icon_iv)
    ImageView iconIV;                                    //头像
    @Bind(R.id.health_report_icon_iv)
    ImageView userIconIV;                               //用户头像
    @Bind(R.id.health_report_name_tv)
    TextView patientNameTV;                             //用户名字
    @Bind(R.id.health_report_patient_sex_iv)
    TextView patientSexTV;                              //用户性别
    @Bind(R.id.health_report_patient_age_iv)
    TextView patientAgeTV;                              //用户年龄
    @Bind(R.id.health_report_doctor_name_tv)
    TextView doctorNameTV;                              //医生名字
    @Bind(R.id.health_report_patient_name_iv)
    TextView patientName;                               //就诊人
    @Bind(R.id.health_report_time_tv)
    TextView reportTimeTV;                              //报告时间
    @Bind(R.id.health_system_report_time_tv)
    TextView reportSystemTimeTV;                        //报告时间
    @Bind(R.id.health_report_symptom_tv)
    TextView patientSymptomTV;                          //症状
    @Bind(R.id.health_report_content_iv)
    TextView reportContentTV;                           //病情信息
    @Bind(R.id.health_report_prescription_iv)
    TextView prescriptionTV;                            //药方
    @Bind(R.id.health_report_advice_iv)
    TextView adviceTV;                                  //建议
    @Bind(R.id.health_report_voice_ll)
    LinearLayout voiceLL;                                    //播放语音
    @Bind(R.id.health_report_voice_btn)
    AudioButton voiceBtn;                                    //播放语音按钮
    @Bind(R.id.health_report_voice_time_tv)
    TextView voiceTimeTV;                               //语音时间
    @Bind(R.id.health_report_pictures)
    RecyclerView recyclerView;
    @Bind(R.id.health_report_delete_btn)
    Button deleteBtn;                                    //删除报告

    private Toolbar toolbar;

    private List<String> datalist;
    private BaseRecyclerAdapter<String> adapter;

    private UserHealthReports report;

    // 语音
    private AudioPlayer audioPlayer;
    private String audioUrl;

    private int imageSize;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        report = (UserHealthReports) getIntent().getSerializableExtra(HealthReportDetailActivity.class.getSimpleName());
        position = getIntent().getIntExtra("position", -1);

        if (null == report) {
            ToastUtils.showToast(getString(R.string.data_error));
            finish();
            return;
        }

        setContentView(R.layout.activity_health_report_detail);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.health_report_detail);

        initView();
        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != audioPlayer) {
            audioPlayer.stop();
            audioPlayer = null;
        }
        Glide.clear(userIconIV);
        Glide.clear(iconIV);
    }

    /**
     * 初始化界面
     */
    private void initView() {
        switch (report.getType()) {
            case PLATFORM://平台报告
                ownLL.setVisibility(View.GONE);
                systemLL.setVisibility(View.VISIBLE);
                reportSystemTimeTV.setText(TimeFormatUtils.getLocalTime(TIME_FROMAT_ONE, Long.parseLong(report
                        .getCreateDate()))
                        //                        + "-" + TimeFormatUtils.getLocalTime(TIME_FROMAT_TWO, Long.parseLong
                        //                        (report.getCreateDate()))
                );
                //                SpliceUtils.getTime(report.getCreateDate())
                break;
            case CASEHISTORY://病历报告
            case PHYSICALEXAM://体检报告
                ownLL.setVisibility(View.VISIBLE);
                systemLL.setVisibility(View.GONE);
                reportTimeTV.setText(TimeFormatUtils.getLocalTime(TIME_FROMAT_ONE, Long.parseLong(report
                        .getCreateDate())));
                //没有头像地址
                Glide.with(this).load(HttpBase.BASE_OSS_URL + DataCache.getInstance().getUserInfo().getHeadPortrait()
                ).transform(new CircleTransform(this)).placeholder(R.drawable.actionbar_headportrait_small).error(R
                        .drawable.actionbar_headportrait_small).into(userIconIV);
                break;
            default:
                break;
        }

        patientName.setText(report.getPatientName());
        patientNameTV.setText(report.getPatientName());
        patientSexTV.setText(report.getPatientSex().getMark());
        patientAgeTV.setText(report.getPatientAge() <= 0 ? "未填写" : report.getPatientAge() + "");

        patientSymptomTV.setText(report.getContent());
        if (report.getDiagnoseCases().size() > 0) {
            Glide.with(this).load(HttpBase.BASE_OSS_URL + report.getDiagnoseCases().get(position).getDoctorInfo()
                    .getHeadPortrait()).transform(new CircleTransform(this)).placeholder(R.drawable
                    .actionbar_headportrait_small).error(R.drawable.actionbar_headportrait_small).into(iconIV);
            doctorNameTV.setText(report.getDiagnoseCases().get(position).getDoctorInfo().getName());
            reportContentTV.setText(report.getDiagnoseCases().get(position).getContent());
            prescriptionTV.setText(report.getDiagnoseCases().get(position).getExhort());
            adviceTV.setText(report.getDiagnoseCases().get(position).getAttachment());
        } else {
            //没有头像地址
            Glide.with(this).load(R.drawable.actionbar_headportrait_small).placeholder(R.drawable
                    .actionbar_headportrait_small).error(R.drawable.actionbar_headportrait_small).into(iconIV);
            reportContentTV.setText(report.getDiseaseDescription());
            prescriptionTV.setText("");
            adviceTV.setText("");
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {

        imageSize = (DisplayUtils.getScreenWidth(this) - DisplayUtils.dip2px(this, 92)) / 3;

        datalist = new ArrayList<>();
        voiceLL.setVisibility(View.GONE);

        if (null != report.getAttachment()) {
            if (null != report.getAttachment().getPicture()) {
                for (BodyContent content : report.getAttachment().getPicture().getContent()) {
                    datalist.add(HttpBase.BASE_OSS_URL + content.getAddress());
                }
            }
            if (null != report.getAttachment().getVoice()) {
                voiceLL.setVisibility(View.VISIBLE);
                audioPlayer = new AudioPlayer(this);
                audioUrl = report.getAttachment().getVoice().getContent().get(0).getAddress();
                if (!TextUtils.isEmpty(audioUrl)) {
                    final File file = new File(Preference.REPORT_AUDIO_CACHE_PATH + report.getId());
                    if (file.exists()) {
                        audioPlayer.setDataSource(file.getPath());
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Http.downloadAsync(HttpBase.BASE_OSS_URL + audioUrl, file.getPath(), new
                                        HttpCallback<File>() {
                                    @Override
                                    public void onFailure(Request request, Response response, Exception e) {

                                    }

                                    @Override
                                    public void onSuccess(File response, int code) {
                                        audioPlayer.setDataSource(response.getPath());
                                        audioUrl = HttpBase.BASE_OSS_URL + audioUrl;
                                    }
                                });
                            }
                        }).start();
                    }
                    //                    audioPlayer.setDataSource(HttpBase.BASE_OSS_URL + audioUrl);
                    voiceTimeTV.setText(TimeFormatUtils.getTime(report.getAttachment().getVoice().getContent().get(0)
                            .getDuration()));
                }
            }
        }

        adapter = new BaseRecyclerAdapter<String>(this, datalist) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.multi_select_item;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, final int position, String item) {
                holder.getImageView(R.id.multi_select_item_cancel).setVisibility(View.GONE);
                ImageView imageView = holder.getImageView(R.id.multi_select_item_icon);
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                params.height = imageSize;
                params.width = imageSize;
                imageView.setLayoutParams(params);
                Glide.with(HealthReportDetailActivity.this).load(datalist.get(position)).placeholder(R.drawable
                        .mis_default_error).error(R.drawable.mis_default_error).centerCrop().into(imageView);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ImagePagerActivity.startImagePagerActivity(HealthReportDetailActivity.this, datalist,
                                position, new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view
                                        .getMeasuredHeight()));
                    }
                });
            }
        };

    }

    /**
     * 初始化事件监听
     */
    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        voiceBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));// 布局管理器。
        recyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        recyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。

        recyclerView.setAdapter(adapter);

        if (null != audioPlayer) {
            audioPlayer.setOnPlayListener(new OnPlayListener() {
                @Override
                public void onPrepared() {

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
                    long time = report.getAttachment().getVoice().getContent().get(0).getDuration() - l;
                    if (time < 500) {
                        voiceTimeTV.setText(TimeFormatUtils.getTime(report.getAttachment().getVoice().getContent()
                                .get(0).getDuration()));
                    } else {
                        voiceTimeTV.setText(TimeFormatUtils.getTime(time));
                    }
                }
            });
        }
    }

    /**
     * 开始播放语音
     */
    private void startPalyAudio() {
        if (null != audioPlayer && !audioPlayer.isPlaying()) {
            audioPlayer.start(AudioManager.STREAM_MUSIC);
        }
    }

    /**
     * 停止播放语音
     */
    private void stopPalyAudio() {
        if (null != audioPlayer) {
            voiceTimeTV.setText(TimeFormatUtils.getTime(report.getAttachment().getVoice().getContent().get(0)
                    .getDuration()));
            audioPlayer.stop();
        }
    }

    /**
     * 删除报告
     */
    private void deleteHealthReport() {
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
                        File file = new File(Preference.REPORT_AUDIO_CACHE_PATH + report.getId());
                        if (file.exists()) {
                            file.delete();
                        }
                        setResult();
                        finish();
                        return;
                    }
                }
                ToastUtils.showToast("删除失败，请重试");
            }
        };

        callback.setMainThread(true);
        callback.setContext(this);
        callback.setCancelable(false);

        HttpProtocol.deleteReport(report.getId(), callback);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.health_report_voice_btn:
                if (null != audioPlayer && audioPlayer.isPlaying()) {
                    stopPalyAudio();
                } else {
                    startPalyAudio();
                }
                break;
            case R.id.health_report_delete_btn:
                deleteHealthReport();
                break;
            default:
                break;
        }
    }

    private void setResult() {
        Intent intent = new Intent();
        intent.putExtra(HealthReportDetailActivity.class.getSimpleName(), report);
        setResult(RESULT_OK, intent);
    }
}
