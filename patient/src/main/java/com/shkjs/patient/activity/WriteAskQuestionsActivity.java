package com.shkjs.patient.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.internal.LinkedTreeMap;
import com.netease.nimlib.sdk.media.player.AudioPlayer;
import com.netease.nimlib.sdk.media.player.OnPlayListener;
import com.netease.nimlib.sdk.media.record.AudioRecorder;
import com.netease.nimlib.sdk.media.record.IAudioRecordCallback;
import com.netease.nimlib.sdk.media.record.RecordType;
import com.orhanobut.logger.Logger;
import com.raspberry.library.activity.ImagePagerActivity;
import com.raspberry.library.multiselect.MultiImageSelector;
import com.raspberry.library.multiselect.MultiImageSelectorActivity;
import com.raspberry.library.util.BasePopupWindow;
import com.raspberry.library.util.DisplayUtils;
import com.raspberry.library.util.PopupWindowUtils;
import com.raspberry.library.util.TextUtils;
import com.raspberry.library.util.TimeFormatUtils;
import com.raspberry.library.util.ToastUtils;
import com.shkjs.nim.recyclerview.BaseRecyclerAdapter;
import com.shkjs.nim.recyclerview.BaseRecyclerViewHolder;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.bean.Doctor;
import com.shkjs.patient.bean.Order;
import com.shkjs.patient.cache.DataCache;
import com.shkjs.patient.data.em.OrderInfoType;
import com.shkjs.patient.data.em.OrderSource;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.em.Sex;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.shkjs.patient.util.ActivityManager;
import com.shkjs.patient.view.AudioButton;
import com.shkjs.patient.view.AudioRecorderPopup;
import com.shkjs.patient.view.SexChoosePopup;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.qiujuer.common.okhttp.Http;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import top.zibin.luban.Luban;

/**
 * Created by xiaohu on 2016/10/20.
 * <p>
 * 填写问诊表
 */

public class WriteAskQuestionsActivity extends BaseActivity implements View.OnClickListener, View
        .OnLongClickListener, View.OnFocusChangeListener, IAudioRecordCallback {

    public static final int REQUEST_IMAGE = 123;//图片选择
    public static final int MAX_NUMBER = 20;//选择图片的最多数量
    public static final String AUDIO_TAG = "file";//语音文件上传tag
    public static final String IMAGE_TAG = "image";//图片文件上传tag
    public static final String ORDER = "order_info";//订单信息
    public static final String DOCTOR = "doctor";//咨询医生

    @Bind(R.id.user_name_et)
    EditText userNameET;
    @Bind(R.id.user_sex_tv)
    TextView userSexTV;
    @Bind(R.id.user_age_et)
    EditText userAgeET;
    @Bind(R.id.recording_btn)
    AudioButton recordBtn;
    @Bind(R.id.describe_et)
    EditText describeET;
    @Bind(R.id.describe_tv)
    TextView describeTV;
    @Bind(R.id.multi_recyclerview)
    RecyclerView recyclerView;
    @Bind(R.id.submit_btn)
    Button submitBtn;
    @Bind(R.id.progress_ll)
    LinearLayout progressLL;
    @Bind(R.id.progress_tv)
    TextView progressTV;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.audio_progress_bar)
    ProgressBar audioProgressBar;

    private Toolbar toolbar;

    private int imageSize;
    private ArrayList<String> datalist;
    private ArrayList<String> tempList;
    private BaseRecyclerAdapter<String> adapter;

    // 语音
    private AudioRecorder audioRecorder;
    private AudioRecorderPopup audioRecorderPopup;
    private AudioPlayer audioPlayer;
    private File audioFile;
    private String audioUrl;
    private long audioTime;
    private PopupWindow popupWindow;
    String hint = null;

    //图片
    private List<File> multiFiles;
    private Map<String, String> multiUrls;
    private Map<String, File> multiMap;
    private boolean isCompressing = false;

    //性别
    private SexChoosePopup sexChoosePopup;

    private boolean isUploadAudio = false;//是否上传过语音文件
    private boolean isUploadPicture = false;//是否上传过图片文件
    private boolean isUploadAudioing = false;//是否正在上传过语音文件
    private boolean isUploadPictureing = false;//是否正在上传过图片文件

    private Order order;
    private Doctor doctor;
    private long reportId = -1;

    //上传进度
    private String str = "未选择图片...";
    private int progress = 0;
    private int secondProgress = 0;
    private static final int UPDATE_VIEW = 121;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_VIEW:
                    if (str.contains("未") || str.contains("成功") || str.contains("失败")) {
                        progressLL.setVisibility(View.GONE);
                    } else {
                        progressLL.setVisibility(View.VISIBLE);
                        progressTV.setText(str);
                        progressBar.setProgress(progress);
                        progressBar.setSecondaryProgress(secondProgress);
                    }
                    break;
                default:
                    break;
            }
        }
    };


    public static void start(Context context, Order order, Doctor doctor) {
        Intent intent = new Intent(context, WriteAskQuestionsActivity.class);
        intent.putExtra(ORDER, order);
        intent.putExtra(DOCTOR, doctor);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_write_ask_questions);

        //初始化控件
        ButterKnife.bind(this);
        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.write_ask_questins);

        //初始化数据
        initData();
        //初始化
        initListener();

        if (null == order || null == doctor) {
            ToastUtils.showToast(getString(R.string.data_error));
            finish();
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != adapter) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != audioRecorder) {
            audioRecorder.completeRecord(true);
            audioRecorder = null;
        }
        if (null != audioPlayer) {
            audioPlayer.stop();
            audioPlayer = null;
        }

        uploadReport(false);
        Http.cancel(AUDIO_TAG);
        Http.cancel(IMAGE_TAG);
    }

    private void initData() {
        order = (Order) getIntent().getSerializableExtra(ORDER);
        doctor = (Doctor) getIntent().getSerializableExtra(DOCTOR);

        imageSize = (DisplayUtils.getScreenWidth(this) - 2 * DisplayUtils.dip2px(this, 14) - 4 * DisplayUtils.dip2px
                (this, 4)) / 4;

        audioRecorder = new AudioRecorder(this, RecordType.AAC, AudioRecorder.DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND,
                this);
        audioPlayer = new AudioPlayer(this);

        datalist = new ArrayList<>();
        tempList = new ArrayList<>();
        multiFiles = new ArrayList<>();
        multiUrls = new HashMap<>();
        multiMap = new HashMap<>();
        datalist.add("");

        adapter = new BaseRecyclerAdapter<String>(this, datalist) {
            @Override
            public int getItemLayoutId(int viewType) {
                return R.layout.multi_select_item;
            }

            @Override
            public void bindData(BaseRecyclerViewHolder holder, final int position, String item) {
                tempList.clear();
                tempList.addAll(datalist.subList(0, datalist.size() - 1));
                ImageView imageView = holder.getImageView(R.id.multi_select_item_icon);
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                params.height = imageSize;
                params.width = imageSize;
                imageView.setLayoutParams(params);
                if (position == datalist.size() - 1) {
                    Glide.with(WriteAskQuestionsActivity.this).load(R.drawable.main_healthreport_upload_addphoto)
                            .into(imageView);
                    holder.getImageView(R.id.multi_select_item_cancel).setVisibility(View.INVISIBLE);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (datalist.size() > MAX_NUMBER) {
                                ToastUtils.showToast(getString(R.string.mis_msg_amount_limit));
                            } else {
                                MultiImageSelector.create().showCamera(true).count(MAX_NUMBER).multi().origin
                                        (tempList).start(WriteAskQuestionsActivity.this, REQUEST_IMAGE);
                            }
                        }
                    });
                } else {
                    Glide.with(WriteAskQuestionsActivity.this).load(datalist.get(position)).centerCrop().into
                            (imageView);
                    holder.getImageView(R.id.multi_select_item_cancel).setVisibility(View.VISIBLE);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ImagePagerActivity.startImagePagerActivity(WriteAskQuestionsActivity.this, tempList,
                                    position, new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view
                                            .getMeasuredHeight()));
                        }
                    });
                    holder.setClickListener(R.id.multi_select_item_cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isCompressing) {
                                ToastUtils.showToast("正在压缩文件，请稍后操作");
                            } else if (isUploadPictureing) {
                                ToastUtils.showToast("正在上传文件，请稍后操作");
                            } else {
                                multiUrls.remove(multiMap.get(datalist.get(position)).getName());
                                multiMap.remove(datalist.get(position));
                                datalist.remove(position);
                                adapter.notifyDataSetChanged();
                                if (datalist.size() <= 1) {
                                    isUploadPicture = false;
                                }
                            }
                        }
                    });
                }

            }
        };
        //默认用户姓名
        if (!TextUtils.isEmpty(DataCache.getInstance().getUserInfo().getNickName())) {
            userNameET.setText(DataCache.getInstance().getUserInfo().getNickName());
        }
        //默认用户性别
        if (!DataCache.getInstance().getUserInfo().getSex().equals(Sex.SECRECY)) {
            userSexTV.setText(DataCache.getInstance().getUserInfo().getSex().getMark());
        }
        //默认用户年龄
        if (!TextUtils.isEmpty(DataCache.getInstance().getUserInfo().getBirthday())) {
            userAgeET.setText("" + TimeFormatUtils.getAge(new Date(Long.parseLong(DataCache.getInstance().getUserInfo
                    ().getBirthday()))));
        }

        handler.sendEmptyMessage(UPDATE_VIEW);
    }

    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishActivity();
                finish();
            }
        });
        recordBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        userSexTV.setOnClickListener(this);

        describeET.setOnFocusChangeListener(this);
        userAgeET.setOnFocusChangeListener(this);
        userNameET.setOnFocusChangeListener(this);

        //用户姓名、年龄、性别不允许修改
        userNameET.setEnabled(false);
        userAgeET.setEnabled(false);
        userSexTV.setEnabled(false);

        recordBtn.setOnLongClickListener(this);

        describeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                describeTV.setText(s.length() + "/100");
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));// 布局管理器。
        recyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        recyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。

        recyclerView.setAdapter(adapter);

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
                long time = audioPlayer.getDuration() - l;
                if (time < 500) {
                    recordBtn.setTextStr(TimeFormatUtils.getTime(audioPlayer.getDuration()));
                    recordBtn.setButtonDrawable(R.drawable.main_healthreport_upload_icon_voice_icon);
                } else {
                    recordBtn.setTextStr(TimeFormatUtils.getTime(time));
                    recordBtn.setButtonDrawable(R.drawable.main_healthreport_upload_icon_voice_icon);
                }
            }
        });
    }

    /**
     * 创建录音界面
     */
    private void createRecordPopup() {
        if (null == audioRecorderPopup) {
            audioRecorderPopup = new AudioRecorderPopup(this);

            audioRecorderPopup.setRecordListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (audioRecorderPopup.getStatus()) {
                        case AudioRecorderPopup.INITIAL:
                            onStartAudioRecord();
                            break;
                        case AudioRecorderPopup.RECORDING:
                            onEndAudioRecord(false);
                            break;
                        case AudioRecorderPopup.COMPLETE:
                            startPalyAudio();
                            break;
                    }

                }
            });

            audioRecorderPopup.setSurelListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recordBtn.setTextStr(TimeFormatUtils.getTime(audioTime));
                    recordBtn.setButtonDrawable(R.drawable.main_healthreport_upload_icon_voice_icon);
                    audioRecorderPopup.setStatus(AudioRecorderPopup.SURE);
                    uploadAudioFile();//确定后就上传语音文件
                }
            });
            audioRecorderPopup.setCancelListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != audioFile && audioFile.exists()) {
                        audioFile.delete();
                    }
                    audioRecorderPopup.setStatus(AudioRecorderPopup.CANCEL);
                }
            });
            audioRecorderPopup.setOnDismissListener(new BasePopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if (null != audioRecorder && audioRecorder.isRecording()) {
                        onEndAudioRecord(true);
                        audioRecorderPopup.setStatus(AudioRecorderPopup.CANCEL);
                    }
                }
            });
        }
        audioRecorderPopup.showPopupWindow();
    }

    /**
     * 创建性别选择界面
     */
    private void createSexPopup() {
        if (null == sexChoosePopup) {
            sexChoosePopup = new SexChoosePopup(this);

            sexChoosePopup.chooseManListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userSexTV.setText(getString(R.string.sex_man));
                    sexChoosePopup.dismiss();
                }
            });

            sexChoosePopup.chooseWomanListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userSexTV.setText(getString(R.string.sex_woman));
                    sexChoosePopup.dismiss();
                }
            });

        }
        sexChoosePopup.showPopupWindow();
    }

    /**
     * 创建删除音频文件界面
     */
    private void createDeletePopup() {
        View view = getLayoutInflater().inflate(R.layout.popup_delete, null);
        if (null == popupWindow) {
            popupWindow = new PopupWindow(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setContentView(view);
            popupWindow.setClippingEnabled(false);
            popupWindow.setBackgroundDrawable(new ColorDrawable());//不设置，点击外部popup不消失
            popupWindow.setOutsideTouchable(true);

            view.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != audioFile && audioFile.exists()) {
                        if (null != audioPlayer && audioPlayer.isPlaying()) {
                            audioPlayer.stop();
                        }
                        audioFile.delete();
                        audioUrl = "";
                        isUploadAudio = false;
                        if (null != audioRecorderPopup) {
                            audioRecorderPopup.setStatus(AudioRecorderPopup.INITIAL);
                        }
                        recordBtn.setTextStr("录音");
                        recordBtn.setButtonDrawable(R.drawable.main_healthreport_upload_icon_record);
                    }
                    popupWindow.dismiss();
                }
            });
        }
        int position[] = PopupWindowUtils.calculatePopWindowPos(recordBtn, view);
        popupWindow.showAtLocation(recordBtn, Gravity.TOP | Gravity.START, position[0] + recordBtn.getWidth() / 2 -
                view.getMeasuredWidth() / 2, position[1] - recordBtn.getHeight() - view.getMeasuredHeight());
    }

    /**
     * 开始语音录制
     */
    private void onStartAudioRecord() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams
                .FLAG_KEEP_SCREEN_ON);

        boolean started = audioRecorder.startRecord();
        if (started == false) {
            ToastUtils.showToast(getString(R.string.recording_init_failed));
            audioRecorderPopup.dismiss();
            return;
        }

        playAudioRecordAnim();
    }

    /**
     * 结束语音录制
     *
     * @param cancel
     */
    private void onEndAudioRecord(boolean cancel) {
        getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        audioRecorder.completeRecord(cancel);
        stopAudioRecordAnim();
    }


    /**
     * 开始语音录制动画
     */
    private void playAudioRecordAnim() {
        audioRecorderPopup.getTime().setBase(SystemClock.elapsedRealtime());
        audioRecorderPopup.getTime().start();
        audioRecorderPopup.setStatus(AudioRecorderPopup.RECORDING);
    }

    /**
     * 结束语音录制动画
     */
    private void stopAudioRecordAnim() {
        audioRecorderPopup.getTime().stop();
        //        audioRecorderPopup.getTime().setBase(SystemClock.elapsedRealtime());
        audioRecorderPopup.setStatus(AudioRecorderPopup.COMPLETE);
    }

    /**
     * 开始播放语音
     */
    private void startPalyAudio() {
        if (null != audioPlayer && !audioPlayer.isPlaying()) {
            if (null == audioFile || !audioFile.exists()) {
                ToastUtils.showToast("录音失败，请重新录制");
            } else {
                audioPlayer.setDataSource(audioFile.getPath());
                audioPlayer.start(AudioManager.STREAM_MUSIC);
            }
        }
    }

    /**
     * 停止播放语音
     */
    private void stopPalyAudio() {
        if (null != audioPlayer) {
            recordBtn.setTextStr(TimeFormatUtils.getTime(audioPlayer.getDuration()));
            recordBtn.setButtonDrawable(R.drawable.main_healthreport_upload_icon_voice_icon);
            audioPlayer.stop();
        }
    }

    /**
     * 创建报告body
     *
     * @return
     */
    private String cerateBodyStr() {
        String bodrStr = null;
        bodrStr = "id=" + reportId;
        bodrStr = bodrStr + "&content=" + (TextUtils.isEmpty(describeET) ? "未填写" : TextUtils.getText(describeET));
        bodrStr = bodrStr + "&patientName=" + (TextUtils.isEmpty(userNameET) ? "未填写" : TextUtils.getText(userNameET));
        bodrStr = bodrStr + "&patientAge=" + (TextUtils.isEmpty(userAgeET) ? "-1" : TextUtils.getText(userAgeET));
        bodrStr = bodrStr + "&patientSex=" + (TextUtils.isEmpty(userSexTV) ? Sex.SECRECY : Sex.getSex(TextUtils
                .getText(userSexTV)));
        //        bodrStr = bodrStr + "&simpleIntroduction=" + "";
        //        bodrStr = bodrStr + "&attachment=" + "";
        //        bodrStr = bodrStr + "&diseaseDescription=" + "";
        if (null != audioUrl && !TextUtils.isEmpty(audioUrl)) {
            bodrStr = bodrStr + "&map['voice'].type=VOICE";
            bodrStr = bodrStr + "&map['voice'].content[0].address=" + audioUrl;
            bodrStr = bodrStr + "&map['voice'].content[0].duration=" + audioTime;
        }
        if (null != multiUrls && multiUrls.size() > 0) {
            bodrStr = bodrStr + "&map['picture'].type=PICTURE";
            int i = 0;
            for (String key : multiUrls.keySet()) {
                bodrStr = bodrStr + "&map['picture'].content[" + i + "].address=" + multiUrls.get(key);
                i++;
            }
        }
        return bodrStr;
    }


    /**
     * 校验输入内容
     *
     * @return
     */
    private boolean checkInput() {
        //        if (TextUtils.isEmpty(userNameET) && TextUtils.isEmpty(userAgeET) && TextUtils.isEmpty(userSexTV) &&
        //                TextUtils.isEmpty(describeET)) {
        //            ToastUtils.showToast("请输入有效内容");
        //            return false;
        //        }
        //        if (TextUtils.isEmpty(userNameET)) {
        //            ToastUtils.showToast("请输入用户姓名");
        //            return false;
        //        }
        //        if (TextUtils.isEmpty(userSexTV)) {
        //            ToastUtils.showToast("请选择用户性别");
        //            return false;
        //        }
        //        if (TextUtils.isEmpty(userAgeET)) {
        //            ToastUtils.showToast("请输入用户年龄");
        //            return false;
        //        }
        //        if (TextUtils.isEmpty(describeET)) {
        //            ToastUtils.showToast("请输入详情描述");
        //            return false;
        //        }
        if (TextUtils.isEmpty(audioUrl) && isUploadAudio) {
            ToastUtils.showToast("请等待语音文件上传成功");
            uploadAudioFile();
            return false;
        }
        if (((null == multiUrls || multiUrls.size() <= 0) && isUploadPicture) || isCompressing) {
            ToastUtils.showToast("请等待图片文件上传成功");
            if (!isCompressing) {
                uploadMultiPictureFile(true);
            }
            return false;
        }
        return true;
    }

    /**
     * 上传语音文件
     */
    private void uploadAudioFile() {
        if (isUploadAudioing) {
            return;
        }

        if (null == audioFile) {
            ToastUtils.showToast("录音失败，请重新录制");
            return;
        }

        audioUrl = "";

        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onStart(Request request) {
                super.onStart(request);
                audioProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                audioProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onProgress(long current, long count) {
                super.onProgress(current, count);
                audioProgressBar.setProgress((int) (current * 100 / count));
            }

            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                isUploadAudioing = false;
            }

            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        audioUrl = response.getData();
                    } else {
                        Logger.e("File", getString(R.string.upload_file_failed) + response.getMsg());
                    }
                } else {
                    Logger.e("File", getString(R.string.upload_file_failed));
                }
                isUploadAudioing = false;
            }
        };

        callback.setMainThread(false);

        Http.cancel(AUDIO_TAG);
        isUploadAudio = true;
        HttpProtocol.fileUpload(audioFile, AUDIO_TAG, callback);
        isUploadAudioing = true;
    }

    /**
     * 上传图片文件（多个）
     */
    private void uploadMultiPictureFile(boolean isUpload) {
        if (isUploadPictureing && isUpload) {
            return;
        }
        multiFiles.clear();
        multiUrls.clear();
        for (String key : multiMap.keySet()) {
            multiFiles.add(multiMap.get(key));
        }
        if (multiFiles.size() <= 0) {
            Logger.e("文件被谁吃啦？？？？");
            return;
        }
        RaspberryCallback<ObjectResponse<Object>> callback = new RaspberryCallback<ObjectResponse<Object>>() {

            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                isUploadPictureing = false;
                str = "上传失败...";
                progress = 0;
                handler.sendEmptyMessage(UPDATE_VIEW);
            }

            @Override
            public void onSuccess(ObjectResponse<Object> response, int code) {
                super.onSuccess(response, code);
                str = "上传失败...";
                progress = 0;
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        LinkedTreeMap<String, String> map = (LinkedTreeMap<String, String>) response.getData();
                        for (int i = 0; i < map.size(); i++) {
                            multiUrls.put(multiFiles.get(i).getName(), map.get(multiFiles.get(i).getName()));
                        }
                        str = "上传成功...";
                        progress = 100;
                    } else {
                        Logger.e("File", getString(R.string.upload_file_failed) + response.getMsg());
                    }
                } else {
                    Logger.e("File", getString(R.string.upload_file_failed));
                }
                isUploadPictureing = false;
                handler.sendEmptyMessage(UPDATE_VIEW);
            }

            @Override
            public void onProgress(long current, long count) {
                super.onProgress(current, count);
                str = "正在上传...";
                progress = (int) (current * 100 / count);
                handler.sendEmptyMessage(UPDATE_VIEW);
            }
        };

        callback.setMainThread(false);

        Http.cancel(IMAGE_TAG);
        isUploadPicture = true;
        HttpProtocol.multiFileUpload(multiFiles, IMAGE_TAG, callback);
        isUploadPictureing = true;
    }

    /**
     * 更新健康报告
     */
    private void uploadReport(boolean isShowDialog) {

        RaspberryCallback<ObjectResponse<Object>> callback = new RaspberryCallback<ObjectResponse<Object>>() {

            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
            }

            @Override
            public void onSuccess(ObjectResponse<Object> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        ToastUtils.showToast("请在咨询消息界面中咨询医生");
                        finish();
                    } else {
                        Logger.e("File", getString(R.string.upload_file_failed) + response.getMsg());
                    }
                } else {
                    Logger.e("File", getString(R.string.upload_file_failed));
                }
            }
        };

        if (isShowDialog) {
            callback.setContext(this);
        }
        callback.setMainThread(false);
        callback.setCancelable(false);

        if (reportId < 0) {
            getReportId(isShowDialog);
            return;
        }
        HttpProtocol.updateReport(cerateBodyStr(), callback);

    }

    /**
     * 获取健康报告ID
     */
    private void getReportId(final boolean isShowDialog) {

        RaspberryCallback<ObjectResponse<Long>> callback = new RaspberryCallback<ObjectResponse<Long>>() {

            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
            }

            @Override
            public void onSuccess(ObjectResponse<Long> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        reportId = response.getData();
                        uploadReport(isShowDialog);
                    }
                }
            }
        };

        if (isShowDialog) {
            callback.setContext(this);
        }
        callback.setMainThread(false);

        HttpProtocol.queryReportId(order.getCode(), callback);
    }

    /**
     * 压缩图片文件
     */
    private void compress() {
        new Thread() {
            @Override
            public void run() {
                str = "正在压缩...";
                progress = 0;
                secondProgress = 0;
                handler.sendEmptyMessage(UPDATE_VIEW);
                isUploadPicture = true;
                isCompressing = true;
                List<String> list = new ArrayList<>();
                for (final String str : datalist) {
                    if (!TextUtils.isEmpty(str)) {
                        if (!multiMap.containsKey(str)) {
                            list.add(str);
                        }
                    }
                }
                CountDownLatch countDownLatch = new CountDownLatch(list.size());
                compress(list, countDownLatch);
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                isCompressing = false;
                secondProgress = 100;
                handler.sendEmptyMessage(UPDATE_VIEW);
                uploadMultiPictureFile(false);
            }
        }.start();
        //        new Thread() {
        //            @Override
        //            public void run() {
        //                str = "正在压缩...";
        //                progress = 0;
        //                Object lock = new Object();
        //                isUploadPicture = true;
        //                isCompressing = true;
        //                synchronized (datalist) {
        //                    for (final String str : datalist) {
        //                        if (!TextUtils.isEmpty(str)) {
        //                            if (!multiMap.containsKey(str)) {
        //                                File file = new File(str);
        //                                Logger.d("压缩前: " + file.length());
        //                                Luban.get(UploadHealthReportActivity.this).load(file)//传入要压缩的图片
        //                                        .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
        //                                        .setCompressListener(new OnCompressListener() { //设置回调
        //
        //                                            @Override
        //                                            public void onStart() {
        //                                                compressMap.put(str, false);
        //                                            }
        //
        //                                            @Override
        //                                            public void onSuccess(File file) {
        //                                                Logger.d("压缩后: " + file.length());
        //                                                multiMap.put(str, file);
        //                                                compressMap.put(str, true);
        //                                            }
        //
        //                                            @Override
        //                                            public void onError(Throwable e) {
        //                                                Logger.e("压缩失败！");
        //                                                compressMap.put(str, true);
        //                                            }
        //                                        }).launch();    //启动压缩
        //                                synchronized (lock) {
        //                                    while (true) {
        //                                        try {
        //                                            if (compressMap.get(str)) {
        //                                                break;
        //                                            }
        //                                            secondProgress = multiMap.size() * 100 / datalist.size();
        //                                            handler.sendEmptyMessage(UPDATE_VIEW);
        //                                            lock.wait(100);
        //                                        } catch (InterruptedException e) {
        //                                            e.printStackTrace();
        //                                        }
        //                                    }
        //
        //                                }
        //                            }
        //                        }
        //                    }
        //                }
        //                isCompressing = false;
        //                uploadMultiPictureFile(false);
        //            }
        //        }.start();
    }

    /**
     * 压缩图片文件
     *
     * @param list 待压缩图片url
     */
    private void compress(final List<String> list, final CountDownLatch countDownLatch) {

        final int size = list.size();

        Subscriber<File> subscriber = new Subscriber<File>() {
            @Override
            public void onCompleted() {
                handler.sendEmptyMessage(UPDATE_VIEW);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                handler.sendEmptyMessage(UPDATE_VIEW);
            }

            @Override
            public void onNext(File file) {
                compress(file, file.getPath(), countDownLatch);
                int count = (int) countDownLatch.getCount();
                Logger.d(count);
                secondProgress = (size - count) * 100 / size;
                file = null;
            }
        };
        Observable.from(list).map(new Func1<String, File>() {
            @Override
            public File call(String s) {
                return new File(s);
            }
        }).subscribe(subscriber);
    }

    /**
     * 压缩图片文件
     *
     * @param file 待压缩文件
     */
    private void compress(final File file, final String url, final CountDownLatch countDownLatch) {
        //        Luban.get(UploadHealthReportActivity.this).load(file)//传入要压缩的图片
        //                .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
        //                .setCompressListener(new OnCompressListener() { //设置回调
        //
        //                    @Override
        //                    public void onStart() {
        //                        handler.sendEmptyMessage(UPDATE_VIEW);
        //                    }
        //
        //                    @Override
        //                    public void onSuccess(File file) {
        //                        Logger.d("压缩成功");
        //                        multiMap.put(url, file);
        //                        countDownLatch.countDown();
        //                        handler.sendEmptyMessage(UPDATE_VIEW);
        //                    }
        //
        //                    @Override
        //                    public void onError(Throwable e) {
        //                        Logger.d("压缩失败");
        //                        countDownLatch.countDown();
        //                        handler.sendEmptyMessage(UPDATE_VIEW);
        //                    }
        //                }).launch();    //启动压缩

        Luban.get(WriteAskQuestionsActivity.this)//
                .load(file)//传入要压缩的图片
                .putGear(Luban.THIRD_GEAR)      //设定压缩档次，默认三挡
                .asObservable()//
                .observeOn(Schedulers.io())//IO线程
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        Logger.d("压缩失败");
                        handler.sendEmptyMessage(UPDATE_VIEW);
                    }
                })//
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        Logger.d("压缩结束");
                        handler.sendEmptyMessage(UPDATE_VIEW);
                    }
                })//
                .subscribe(new Action1<File>() {
                    @Override
                    public void call(File file) {
                        Logger.d("压缩成功");
                        multiMap.put(url, file);
                        countDownLatch.countDown();
                        handler.sendEmptyMessage(UPDATE_VIEW);
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_sex_tv:
                userSexTV.setFocusable(true);
                userSexTV.setFocusableInTouchMode(true);
                userSexTV.requestFocus();
                createSexPopup();
                break;
            case R.id.recording_btn:
                if (null == audioRecorderPopup || audioRecorderPopup.getStatus() == AudioRecorderPopup.INITIAL ||
                        audioRecorderPopup.getStatus() == AudioRecorderPopup.CANCEL) {
                    createRecordPopup();
                } else if (audioRecorderPopup.getStatus() == AudioRecorderPopup.SURE) {
                    if (null != audioPlayer) {
                        if (audioPlayer.isPlaying()) {
                            stopPalyAudio();
                        } else {
                            startPalyAudio();
                        }
                    }
                }
                break;
            case R.id.submit_btn:
                if (checkInput()) {
                    uploadReport(true);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.recording_btn) {
            createDeletePopup();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE) {
                if (null != data) {
                    ArrayList<String> list = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                    if (null != list) {
                        //                        for (String str : list) {
                        //                            if (!datalist.contains(str)) {
                        //                                datalist.add(datalist.size() - 1, str);
                        //                            }
                        //                        }
                        datalist.clear();
                        datalist.addAll(list);
                        datalist.add("");
                        adapter.notifyDataSetChanged();
                        compress();//压缩图片
                        //                        uploadMultiPictureFile(false);//选择完成则上传图片
                    }
                }
            }
        }
    }

    private void finishActivity() {
        ActivityManager.getInstance().finishActivity(SearchDoctorActivity.class);
        ActivityManager.getInstance().finishActivity(DoctorDetailActivity.class);
        ActivityManager.getInstance().finishActivity(OrderTimeActivity.class);
        ActivityManager.getInstance().finishActivity(PayActivity.class);
    }

    /**
     * 获取订单type
     *
     * @param source
     * @return
     */
    private OrderInfoType getOrderInfoType(OrderSource source) {
        OrderInfoType type = null;
        switch (source) {
            case INQUIRY_RESERVE:
                type = OrderInfoType.PICTURE;
                break;
            case SIT_DIAGNOSE_RESERVE:
                type = OrderInfoType.VIDEO;
                break;
            case GROUP_SIT_DIAGNOSE:
                type = OrderInfoType.MULTIVIDEO;
                break;
            case GROUP_SIT_DIAGNOSE_DETAIL:
                type = OrderInfoType.MULTIVIDEO;
                break;
            case RECHARGE:
                break;
            default:
                break;
        }
        return type;
    }

    @Override
    public void onBackPressed() {
        if (null != audioRecorderPopup && audioRecorderPopup.isShowing()) {
            audioRecorderPopup.setStatus(AudioRecorderPopup.CANCEL);
        }
        if (null != popupWindow && popupWindow.isShowing()) {
            popupWindow.dismiss();
            return;
        }
        finishActivity();
        super.onBackPressed();
    }

    /* ************************************************语音录制回调************************************************** */
    @Override
    public void onRecordReady() {
        Logger.d("ready");
    }

    @Override
    public void onRecordStart(File file, RecordType recordType) {
        Logger.d("start");
    }

    @Override
    public void onRecordSuccess(File file, long l, RecordType recordType) {
        audioFile = file;
        audioTime = l;
        Logger.d("success");
    }

    @Override
    public void onRecordFail() {
        Logger.d("fail");
    }

    @Override
    public void onRecordCancel() {
        Logger.d("cancel");
    }

    @Override
    public void onRecordReachedMaxTime(int i) {
        onEndAudioRecord(false);
        ToastUtils.showToast(getString(R.string.recording_max_time));
    }


    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {
            case R.id.user_name_et:
                if (b) {
                    userNameET.setGravity(Gravity.LEFT);
                    userNameET.setSelection(userNameET.getText().toString().length() / 2);
                    hint = userNameET.getHint().toString();
                    userNameET.setTag(hint);
                    userNameET.setHint("");
                } else {
                    userNameET.setGravity(Gravity.RIGHT);
                    hint = userNameET.getTag().toString();
                    userNameET.setHint(hint);
                }
                break;
            case R.id.user_age_et:
                if (b) {
                    userAgeET.setGravity(Gravity.LEFT);
                    userAgeET.setSelection(userAgeET.getText().toString().length() / 2);
                    hint = userAgeET.getHint().toString();
                    userAgeET.setTag(hint);
                    userAgeET.setHint("");
                } else {
                    userAgeET.setGravity(Gravity.RIGHT);
                    hint = userAgeET.getTag().toString();
                    userAgeET.setHint(hint);
                }
                break;
            case R.id.user_sex_tv:
                if (b) {
                    hint = userSexTV.getHint().toString();
                    userSexTV.setTag(hint);
                    userSexTV.setHint("");
                } else {
                    hint = userSexTV.getTag().toString();
                    userSexTV.setHint(hint);
                }
                break;
            case R.id.describe_et:
                if (b) {
                    hint = describeET.getHint().toString();
                    describeET.setTag(hint);
                    describeET.setHint("");
                } else {
                    hint = describeET.getTag().toString();
                    describeET.setHint(hint);
                }
                break;
        }
    }
}
