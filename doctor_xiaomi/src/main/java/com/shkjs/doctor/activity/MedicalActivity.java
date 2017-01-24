package com.shkjs.doctor.activity;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nimlib.sdk.media.player.AudioPlayer;
import com.netease.nimlib.sdk.media.player.OnPlayListener;
import com.netease.nimlib.sdk.media.record.AudioRecorder;
import com.netease.nimlib.sdk.media.record.IAudioRecordCallback;
import com.netease.nimlib.sdk.media.record.RecordType;
import com.raspberry.library.activity.ImagePagerActivity;
import com.raspberry.library.util.CustomAlertDialog;
import com.raspberry.library.util.TimeFormatUtils;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.MultiImageView;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.base.BaseResponse;
import com.shkjs.doctor.data.Sex;
import com.shkjs.doctor.bean.UserHealthReports;
import com.shkjs.doctor.http.HttpBase;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ObjectResponse;
import com.shkjs.doctor.util.AudioHelper;
import com.shkjs.doctor.util.SoftKeyboardUtil;
import com.shkjs.doctor.view.AudioButton;
import com.shkjs.doctor.view.AudioRecorderPopup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MedicalActivity extends BaseActivity implements IAudioRecordCallback, SoftKeyboardUtil.OnSoftKeyboardChangeListener {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tv;
    @Bind(R.id.text_ringht)
    TextView text_ringht;
    @Bind(R.id.medical_photos_miv)
    MultiImageView multiImageView;
    @Bind(R.id.medical_name_edittext_tv)
    EditText medical_name_edittext_tv;
    @Bind(R.id.medical_sex_edittext_tv)
    TextView medical_sex_edittext_tv;
    @Bind(R.id.medical_age_edittext_tv)
    EditText medical_age_edittext_tv;
    @Bind(R.id.medical_bingqing_edittext_et)
    EditText medical_bingqing_edittext_et;
    @Bind(R.id.medical_yizhu_edittext_et)
    EditText medical_yizhu_edittext_et;
    @Bind(R.id.medical_bingqing_textnum_tv)
    TextView medical_bingqing_textnum_tv;
    @Bind(R.id.medical_yizhu_textnum_tv)
    TextView medical_yizhu_textnum_tv;
    @Bind(R.id.age_text)
    TextView age_text;
    @Bind(R.id.no_medical_photos_miv)
    TextView no_medical_photos_miv;
    @Bind(R.id.medical_photos_miv_ll)
    LinearLayout medical_photos_miv_ll;
    @Bind(R.id.recording_btn)
    AudioButton recordBtn;

    private List<String> photos;
    private String name;
    private String sex;
    private String age;
    private String exhort;
    private String content;
    private RaspberryCallback<BaseResponse> completeCallback;
    private RaspberryCallback<ObjectResponse<UserHealthReports>> reportCallback;
    private RaspberryCallback<BaseResponse> setCanfinishVideo;
    private String orderId;
    private String orderCode;
    private String completeType;
    private int resultCode;
    //录音
    private AudioRecorderPopup audioRecorderPopup;
    private AudioPlayer audioPlayer;
    private String audioUrl;
    private long audioTime;
    private File audioFile;
    private AudioRecorder audioRecorder;
    //键盘
    private int height1 = 0;
    private int height2 = 0;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical);
        ButterKnife.bind(this);
        toptitle_tv.setText("填写病历表");
        text_ringht.setText("完成");
        orderId = getIntent().getStringExtra("orderId");
        orderCode = getIntent().getStringExtra("orderCode");
        completeType = getIntent().getStringExtra(Preference.COMPLETE_TYPE);
        Log.i("tag00", "orderId:" + orderId + "  orderCode=" + orderCode);
        initData();
        initListener();
        loadNetData();
    }

    private void initData() {
        audioRecorder = new AudioRecorder(this, RecordType.AAC, AudioRecorder.DEFAULT_MAX_AUDIO_RECORD_TIME_SECOND,
                this);
        audioPlayer = new AudioPlayer(this);
    }

    private void loadNetData() {
        HttpProtocol.medicalReport(reportCallback, orderCode);
    }

    private void initListener() {
        SoftKeyboardUtil.observeSoftKeyboard(this, this);

        photos = new ArrayList<>();
        medical_bingqing_edittext_et.addTextChangedListener(TextWhtcherListener(medical_bingqing_textnum_tv));
        medical_yizhu_edittext_et.addTextChangedListener(TextWhtcherListener(medical_yizhu_textnum_tv));
        completeCallback = new RaspberryCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    ToastUtils.showToast("提交完成。");
                    Intent intent = getIntent();
                    setResult(resultCode, intent);
                    finish();
                }
            }
        };
        completeCallback.setCancelable(false);
        completeCallback.setContext(this);
        completeCallback.setMainThread(true);
        completeCallback.setShowDialog(true);

        reportCallback = new RaspberryCallback<ObjectResponse<UserHealthReports>>() {
            @Override
            public void onSuccess(ObjectResponse<UserHealthReports> response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    if (response.getData() != null) {
                        if (!StringUtil.isEmpty(response.getData().getPatientName())) {
                            medical_name_edittext_tv.setText(response.getData().getPatientName());
                        }else {
                            medical_name_edittext_tv.setText("保密");
                        }
                        if (response.getData().getPatientSex() != null) {
                            medical_sex_edittext_tv.setText(response.getData().getPatientSex().getMark());
                            sex = response.getData().getPatientSex().getMark();
                        }else {
                            medical_sex_edittext_tv.setText("保密");
                            sex = "SECRECY";
                        }
                        if (response.getData().getPatientAge() != 0) {
                            medical_age_edittext_tv.setText(response.getData().getPatientAge() + "");
                            age_text.setVisibility(View.VISIBLE);
                        }else {
                            medical_age_edittext_tv.setText("保密");
                            age_text.setVisibility(View.GONE);
                        }

                        if (response.getData().getAttachment() != null && response.getData().getAttachment()
                                .getPicture() != null) {
                            medical_photos_miv_ll.setVisibility(View.VISIBLE);
                            for (int index = 0; index < response.getData().getAttachment().getPicture().getContent()
                                    .size(); index++) {
                                photos.add(HttpBase.IMGURL + response.getData().getAttachment().getPicture()
                                        .getContent().get(index).getAddress());
                                multiImageView.setList(photos);
                                multiImageView.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        ImagePagerActivity.startImagePagerActivity(MedicalActivity.this, photos,
                                                position, new ImagePagerActivity.ImageSize(view.getMeasuredWidth(),
                                                        view.getMeasuredHeight()));
                                    }
                                });
                            }
                        } else {
                            medical_photos_miv_ll.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        medical_photos_miv_ll.setVisibility(View.INVISIBLE);
                    }
                }
            }
        };
        reportCallback.setCancelable(false);
        reportCallback.setContext(this);
        reportCallback.setMainThread(true);
        reportCallback.setShowDialog(true);

        setCanfinishVideo = new RaspberryCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    Log.i("tag00", "设置视频可完成成功");
                }
            }
        };
        AudioHelper.initCallBack(setCanfinishVideo, this, false);

        if (!completeType.equals(Preference.COMPLETE_PICTURE)) {
            HttpProtocol.setCanfinishVideo(setCanfinishVideo, orderId);
        }
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


    public TextWatcher TextWhtcherListener(final TextView textView) {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textView.setText(String.valueOf(charSequence.length()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
        return textWatcher;
    }

    private boolean isFinishedAll() {
//        if (StringUtil.isEmpty(medical_name_edittext_tv.getText().toString().trim())) {
//            ToastUtils.showToast("请填写患者姓名");
//            return false;
//        }
//        if (StringUtil.isEmpty(medical_sex_edittext_tv.getText().toString().trim())) {
//            ToastUtils.showToast("请填写患者性别");
//            return false;
//        }
//        if (StringUtil.isEmpty(medical_age_edittext_tv.getText().toString().trim())) {
//            ToastUtils.showToast("请填写患者年龄");
//            return false;
//        }
        if (StringUtil.isEmpty(medical_bingqing_edittext_et.getText().toString().trim())) {
            ToastUtils.showToast("请填写患者病情");
            return false;
        }
        if (StringUtil.isEmpty(medical_yizhu_edittext_et.getText().toString().trim())) {
            ToastUtils.showToast("请填写医嘱");
            return false;
        }
        return true;
    }

    @OnClick({R.id.back_iv, R.id.text_ringht, R.id.medical_sex_edittext_tv, R.id.recording_btn})
    public void medicalOnClick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                CustomAlertDialog.dialogExSureCancel("还未完成患者的病历表，确定退出吗？", this, new CustomAlertDialog
                        .OnDialogClickListener() {
                    @Override
                    public void doSomeThings() {
                        Intent intent = getIntent();
                        setResult(resultCode, intent);
                        finish();
                    }
                });
                break;
            case R.id.text_ringht:
                if (isFinishedAll()) {
                    //提交病历表信息
                    name = medical_name_edittext_tv.getText().toString();
                    age = medical_age_edittext_tv.getText().toString();
                    exhort = medical_bingqing_edittext_et.getText().toString();
                    content = medical_yizhu_edittext_et.getText().toString();
                    try {
                        int ageNum = Integer.parseInt(age);
                        age = ageNum+"";
                    }catch (Exception e){
                        age = "-1";
                    }finally {
                        switch (completeType) {
                            case Preference.COMPLETE_PICTURE:
                                resultCode = Preference.PICTURE_COMPLETE_RESULT;
                                HttpProtocol.completePictureConsult(completeCallback, orderId,  content, exhort,Sex
                                        .getSexEm(sex), age, name,this);
                                break;
                            case Preference.COMPLETE_VIDEO_GROUP_CONSULT:
                                resultCode = Preference.VIDEO_GROUP_COMPLETE_RESULT;
                                HttpProtocol.completeVideoGroupConsult(completeCallback, orderId,  content, exhort,Sex
                                        .getSexEm(sex), age, name,this);
                                break;
                            case Preference.COMPLETE_SIT_VIDEO_CONSULT:
                                resultCode = Preference.VIDEO_COMPLETE_RESULT;
                                HttpProtocol.completeSitVideoConsult(completeCallback, orderId, content,exhort, Sex
                                        .getSexEm(sex), age, name,this);
                                break;
                        }
                    }

                }
                break;
            case R.id.medical_sex_edittext_tv:
                CustomAlertDialog.dialogChooseSex(this, new CustomAlertDialog.onDialogManClickListener() {
                    @Override
                    public void chooseMan() {
                        medical_sex_edittext_tv.setText("男");
                        sex = Sex.MAN.getMark();
                    }

                    @Override
                    public void chooseWoman() {
                        medical_sex_edittext_tv.setText("女");
                        sex = Sex.WOMAN.getMark();
                    }
                });
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
                    //                    uploadAudioFile();//确定后就上传语音文件
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
        }
        audioRecorderPopup.showPopupWindow();

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

    @Override
    public void onBackPressed() {
        if (null != audioRecorderPopup && audioRecorderPopup.isShowing()) {
            audioRecorderPopup.setStatus(AudioRecorderPopup.CANCEL);
        }
        super.onBackPressed();
    }

    @Override
    public void onRecordReady() {

    }

    @Override
    public void onRecordStart(File file, RecordType recordType) {

    }

    @Override
    public void onRecordSuccess(File file, long l, RecordType recordType) {
        audioFile = file;
        audioTime = l;
    }

    @Override
    public void onRecordFail() {

    }

    @Override
    public void onRecordCancel() {

    }

    @Override
    public void onRecordReachedMaxTime(int i) {
        onEndAudioRecord(false);
        ToastUtils.showToast(getString(R.string.recording_max_time));
    }

    @Override
    public void onSoftKeyBoardChange(int softKeybardHeight, boolean visible) {
        if(!visible){
            height1=softKeybardHeight;
            no_medical_photos_miv.setVisibility(View.GONE);
        }else {
            height2=softKeybardHeight;
            no_medical_photos_miv.setVisibility(View.VISIBLE);
        }
    }
}
