package com.shkjs.doctor.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.netease.nim.uikit.common.util.string.StringUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;
import com.raspberry.library.util.AppUtils;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.PhotoPopupWindow;
import com.shkjs.doctor.Preference;
import com.shkjs.doctor.R;
import com.shkjs.doctor.base.BaseActivity;
import com.shkjs.doctor.base.BaseResponse;
import com.shkjs.doctor.bean.CertificationBean;
import com.shkjs.doctor.bean.DoctorBean;
import com.shkjs.doctor.http.HttpBase;
import com.shkjs.doctor.http.HttpProtocol;
import com.shkjs.doctor.http.RaspberryCallback;
import com.shkjs.doctor.http.response.ObjectResponse;
import com.shkjs.doctor.util.ActivityManager;
import com.shkjs.doctor.util.UploadDemoPopup;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UploadAuthenDataActivity extends BaseActivity {

    @Bind(R.id.toptitle_tv)
    TextView toptitle_tvl;
    @Bind(R.id.text_ringht)
    TextView text_right;
    @Bind(R.id.upload_authen_head_iv)
    ImageView upload_authen_head_iv;
    @Bind(R.id.upload_authen_zheng_iv)
    ImageView upload_authen_zheng_iv;
    @Bind(R.id.upload_authen_fan_iv)
    ImageView upload_authen_fan_iv;
    @Bind(R.id.upload_authen_zhicheng_iv)
    ImageView upload_authen_zhicheng_iv;
    @Bind(R.id.upload_authen_zhiye_iv)
    ImageView upload_authen_zhiye_iv;
    @Bind(R.id.upload_authen_next_tv)
    Button upload_authen_next_tv;

    //用于判断当前正在选择哪个图片
    private final int AUTHEN_HEAD = 100;
    private final int AUTHEN_ZHENG = 200;
    private final int AUTHEN_FAN = 300;
    private final int AUTHEN_ZHIYE = 400;
    private final int AUTHEN_ZHICHENG = 500;

    //图片是否已选择
    private boolean AUTHEN_HEAD_FINISHED = false;
    private boolean AUTHEN_ZHENG_FINISHED = false;
    private boolean AUTHEN_FAN_FINISHED = false;
    private boolean AUTHEN_ZHIYE_FINISHED = false;
    private boolean AUTHEN_ZHICHENG_FINISHED = false;
    //图片地址
    private String HEADIMG_FILE = "";
    private String ZHENGIMG_FILE = "";
    private String FANIMG_FILE = "";
    private String ZHIYEIMG_FILE = "";
    private String ZHICHENGIMG_FILE = "";

    private static final int CAMERA_REQUEST_CODE = 123;//拍照
    private static final int GALLERY_REQUEST_CODE = 124;//选择图片
    private static final int PHOTO_RESOULT = 125;// 裁剪结果

    private static final String IMAGE_USPECIFIED = "image/*";


    //提交点击，可提交和不可提交的额点击事件
    private View.OnClickListener ONCLICK_LISTENER_CANNEXT;
    private View.OnClickListener ONCLICK_LISTENER_CANNOT_NEXT;
    private int CURRENT_AUTHEN;//判断当前是第几个截图

    private Uri photoUri;//图片的URI
    private String fileName;
    private String tempFileName;


    private CertificationBean certificationBean;
    private RaspberryCallback<ObjectResponse<String>> callback;
    private RaspberryCallback<BaseResponse> authenCallback;
    private DoctorBean doctorBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_authen_data);
        //初始化
        ButterKnife.bind(this);
        initPermission();
        certificationBean = (CertificationBean) getIntent().getSerializableExtra("personal");
        if (getIntent().getSerializableExtra("doctorbean") != null) {
            doctorBean = (DoctorBean) getIntent().getSerializableExtra("doctorbean");
            Log.i("tag00", "doctorbean不为空");
            initView(doctorBean);
        }
        toptitle_tvl.setText("上传认证资料");
        text_right.setText("示例");
        //设置监听
        initListener();
        initData();
    }


    public void initPermission(){
        //当Android版本大于等于23时，动态授权
        Acp.getInstance(this).request(new AcpOptions.Builder().setPermissions(Manifest.permission.CAMERA, Manifest.permission
                .READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE).build(), new AcpListener() {

            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(List<String> permissions) {
                ToastUtils.showToast(permissions.toString() + getResources().getString(R.string
                        .toast_permission_hint));
            }
        });
    }

    private void initData() {
        fileName = Environment.getExternalStorageDirectory().getPath() + "/" + AppUtils.getPackageName
                (UploadAuthenDataActivity.this) + "/cache/" + Preference.USER_ICON;//要保存照片的绝对路径
        tempFileName = Environment.getExternalStorageDirectory().getPath() + "/" + AppUtils.getPackageName
                (UploadAuthenDataActivity.this) + "/cache/temp.jpg";
    }

    private void initView(DoctorBean doctorBean) {
        if (!StringUtil.isEmpty(doctorBean.getHeadPortrait())) {
            Glide.with(this).load(HttpBase.IMGURL + doctorBean.getHeadPortrait()).placeholder(R.drawable
                    .default_image).dontAnimate().thumbnail(0.1f).into(upload_authen_head_iv);
            HEADIMG_FILE = doctorBean.getHeadPortrait();
            AUTHEN_HEAD_FINISHED = true;
        }
        if (!StringUtil.isEmpty(doctorBean.getIdentityPermitFront())) {
            Glide.with(this).load(HttpBase.IMGURL + doctorBean.getIdentityPermitFront()).placeholder(R.drawable
                    .default_image).dontAnimate().thumbnail(0.1f).into(upload_authen_zheng_iv);
            ZHENGIMG_FILE = doctorBean.getIdentityPermitFront();
            AUTHEN_ZHENG_FINISHED = true;
        }
        if (!StringUtil.isEmpty(doctorBean.getIdentityPermitReverse())) {
            Glide.with(this).load(HttpBase.IMGURL + doctorBean.getIdentityPermitReverse()).placeholder(R.drawable
                    .default_image).dontAnimate().thumbnail(0.1f).into(upload_authen_fan_iv);
            FANIMG_FILE = doctorBean.getIdentityPermitReverse();
            AUTHEN_FAN_FINISHED = true;
        }

        if (!StringUtil.isEmpty(doctorBean.getWorkPermit())) {
            Glide.with(this).load(HttpBase.IMGURL + doctorBean.getWorkPermit()).placeholder(R.drawable
                    .default_image).dontAnimate().thumbnail(0.1f).into(upload_authen_zhiye_iv);
            ZHIYEIMG_FILE = doctorBean.getWorkPermit();
            AUTHEN_ZHICHENG_FINISHED = true;
        }

        if (!StringUtil.isEmpty(doctorBean.getWorkPermit())) {
            Glide.with(this).load(HttpBase.IMGURL + doctorBean.getDoctorPermit()).placeholder(R.drawable
                    .default_image).dontAnimate().thumbnail(0.1f).into(upload_authen_zhicheng_iv);
            ZHICHENGIMG_FILE = doctorBean.getDoctorPermit();
            AUTHEN_ZHIYE_FINISHED = true;
        }

        checkCanNext();
    }

    //设置监听事件和初始化
    private void initListener() {

        callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onSuccess(final ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
                Log.i("tag00", "返回的图片地址：" + response.getData());
                if (HttpProtocol.checkStatus(response, code)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (CURRENT_AUTHEN) {
                                case AUTHEN_HEAD:
                                    HEADIMG_FILE = response.getData();
                                    Glide.with(UploadAuthenDataActivity.this).load(HttpBase.IMGURL + HEADIMG_FILE)
                                            .thumbnail(0.1F).dontAnimate().placeholder(R.drawable.default_image)
                                            .into(upload_authen_head_iv);
                                    AUTHEN_HEAD_FINISHED = true;
                                    break;
                                case AUTHEN_ZHENG:
                                    ZHENGIMG_FILE = response.getData();
                                    Glide.with(UploadAuthenDataActivity.this).load(HttpBase.IMGURL + ZHENGIMG_FILE)
                                            .thumbnail(0.1F).dontAnimate().placeholder(R.drawable.default_image)
                                            .into(upload_authen_zheng_iv);
                                    AUTHEN_ZHENG_FINISHED = true;
                                    break;
                                case AUTHEN_FAN:
                                    FANIMG_FILE = response.getData();
                                    Glide.with(UploadAuthenDataActivity.this).load(HttpBase.IMGURL + FANIMG_FILE)
                                            .thumbnail(0.1F).dontAnimate().placeholder(R.drawable.default_image)
                                            .into(upload_authen_fan_iv);
                                    AUTHEN_FAN_FINISHED = true;
                                    break;
                                case AUTHEN_ZHICHENG:
                                    ZHICHENGIMG_FILE = response.getData();
                                    Glide.with(UploadAuthenDataActivity.this).load(HttpBase.IMGURL +
                                            ZHICHENGIMG_FILE).thumbnail(0.1F).dontAnimate().placeholder(R.drawable
                                            .default_image).into(upload_authen_zhicheng_iv);
                                    AUTHEN_ZHICHENG_FINISHED = true;
                                    break;
                                case AUTHEN_ZHIYE:
                                    ZHIYEIMG_FILE = response.getData();
                                    Glide.with(UploadAuthenDataActivity.this).load(HttpBase.IMGURL + ZHIYEIMG_FILE)
                                            .thumbnail(0.1F).dontAnimate().placeholder(R.drawable.default_image)
                                            .into(upload_authen_zhiye_iv);
                                    AUTHEN_ZHIYE_FINISHED = true;
                                    break;
                                default:
                                    break;
                            }
                            checkCanNext();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
            }
        };
        authenCallback = new RaspberryCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse response, int code) {
                super.onSuccess(response, code);
                if (HttpProtocol.checkStatus(response, code)) {
                    startActivity(new Intent(UploadAuthenDataActivity.this, AuthenSuccessActivity.class));
                    ActivityManager.getInstance().finishACtivity(UploadAuthenDataActivity.this);
                    ActivityManager.getInstance().finishActivity(PersonalMessageActivity.class);
                }
            }
        };
        callback.setCancelable(false);
        callback.setContext(this);
        callback.setMainThread(false);

        ONCLICK_LISTENER_CANNOT_NEXT = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.showToast("请完善所有信息，才能提交审核。");
            }
        };
        ONCLICK_LISTENER_CANNEXT = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFinishedAll(true)) {
                    HttpProtocol.authentication(authenCallback, certificationBean, HEADIMG_FILE, ZHENGIMG_FILE,
                            FANIMG_FILE, ZHIYEIMG_FILE, ZHICHENGIMG_FILE,UploadAuthenDataActivity.this);
                    //修改网易昵称和头像
                    submitModifyNimUserName(certificationBean.getCertificateName());
                    submitModifyNimUserIcon(HttpBase.IMGURL + HEADIMG_FILE);
                }
            }
        };
        checkCanNext();
    }

    //点击事件
    @OnClick({R.id.back_iv, R.id.text_ringht, R.id.upload_authen_fan_rl, R.id.upload_authen_zheng_rl, R.id
            .upload_authen_head_rl, R.id.upload_authen_zhicheng_rl, R.id.upload_authen_zhiye_rl,})
    public void uploadOnClick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.text_ringht:
                UploadDemoPopup demoPopup = new UploadDemoPopup(this);
                demoPopup.showPopupWindow();
                break;
            case R.id.upload_authen_fan_rl:
                checkPermission(new CheckpermissionCallback() {
                    @Override
                    public void onGrantedSuccess() {
                        CURRENT_AUTHEN = AUTHEN_FAN;
                        modifyIcon();
                    }
                },AUTHEN_FAN);
                break;
            case R.id.upload_authen_zheng_rl:
                checkPermission(new CheckpermissionCallback() {
                    @Override
                    public void onGrantedSuccess() {
                        CURRENT_AUTHEN = AUTHEN_ZHENG;
                        modifyIcon();
                    }
                },AUTHEN_ZHENG);
                break;
            case R.id.upload_authen_head_rl:
                checkPermission(new CheckpermissionCallback() {
                    @Override
                    public void onGrantedSuccess() {
                        CURRENT_AUTHEN = AUTHEN_HEAD;
                        modifyIcon();
                    }
                },AUTHEN_HEAD);
                break;
            case R.id.upload_authen_zhicheng_rl:
                checkPermission(new CheckpermissionCallback() {
                    @Override
                    public void onGrantedSuccess() {
                        CURRENT_AUTHEN = AUTHEN_ZHICHENG;
                        modifyIcon();
                    }
                },AUTHEN_ZHICHENG);
                break;
            case R.id.upload_authen_zhiye_rl:
                checkPermission(new CheckpermissionCallback() {
                    @Override
                    public void onGrantedSuccess() {
                        CURRENT_AUTHEN = AUTHEN_ZHIYE;
                        modifyIcon();
                    }
                },AUTHEN_ZHIYE);
                break;
        }
    }

    private void modifyIcon() {

        final PhotoPopupWindow popupWindow = new PhotoPopupWindow(UploadAuthenDataActivity.this);

        //拍照
        popupWindow.setPhotographListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    photoUri = Uri.fromFile(new File(tempFileName));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                } else {
                    ToastUtils.showToast("没有相机");
                }
                popupWindow.dismiss();
            }
        });

        //选择图片
        popupWindow.setChooseListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(pickIntent, GALLERY_REQUEST_CODE);
                popupWindow.dismiss();
            }
        });

        popupWindow.showPopupWindow();
    }

    /**
     * 裁剪图片
     *
     * @param uri
     */
    private void startSmallPhotoZoom(Uri uri) {
        if (null == uri) {
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");// 调用Android系统自带的一个图片剪裁页面,
        intent.setDataAndType(uri, IMAGE_USPECIFIED);
        intent.putExtra("crop", "true");// 进行修剪
        // aspectX aspectY 是宽高的比例
        //        intent.putExtra("aspectX", 1);
        //        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_RESOULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE://拍照返回
                if (resultCode == RESULT_OK) {
                    Log.i("tag00", "拍照获得的uri:" + photoUri);
                    startSmallPhotoZoom(photoUri);
                } else {
                    ToastUtils.showToast("您没有拍照~请重新操作");
                }
                break;
            case GALLERY_REQUEST_CODE://选择文件返回
                if (resultCode == RESULT_OK) {
                    photoUri = result.getData();
                    Log.i("tag00", "返回图片的uri:" + photoUri);
                    startSmallPhotoZoom(photoUri);
                } else {
                    ToastUtils.showToast("您没有选择照片~请重新操作");
                }
                break;
            case PHOTO_RESOULT://裁剪返回
                if ((resultCode == RESULT_OK) && (null != result)) {
                    resultHandle(result);
                }
                break;
        }
    }

    //开始截图
    private void resultHandle(Intent data) {

        Bundle bundle = data.getExtras();
        Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
        FileOutputStream b = null;

        try {
            b = new FileOutputStream(fileName);
            // 强制压缩图片
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (b != null) {
                    b.flush();
                    b.close();
                }
                //上传选中的图片,必须等待文件写入完毕，否则容易出现ProtocolException
                HttpProtocol.uploadOneFile(callback, new File(fileName),this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void checkPermission(final CheckpermissionCallback checkpermissionCallback,int currentPosiotion){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Acp.getInstance(this).request(new AcpOptions.Builder().setPermissions(Manifest.permission
                    .READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission
                    .READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).build(), new AcpListener() {

                @Override
                public void onGranted() {
                    checkpermissionCallback.onGrantedSuccess();
                }

                @Override
                public void onDenied(List<String> permissions) {
                    ToastUtils.showToast(permissions.toString() + getResources().getString(R.string
                            .toast_permission_hint));
                }
            });
        } else {
            CURRENT_AUTHEN = currentPosiotion;
            modifyIcon();
        }
    }

    public interface CheckpermissionCallback{
        void onGrantedSuccess();
    }


    //实时更新提交按钮
    public void checkCanNext() {
        if (isFinishedAll()) {
            Log.i("tag00", "所有图片已完成。");
            upload_authen_next_tv.setBackground(getResources().getDrawable(R.drawable.shape_sure));
            upload_authen_next_tv.setOnClickListener(ONCLICK_LISTENER_CANNEXT);
        } else {
            upload_authen_next_tv.setBackground(getResources().getDrawable(R.drawable.shap_personal_next));
            upload_authen_next_tv.setOnClickListener(ONCLICK_LISTENER_CANNOT_NEXT);
        }
    }

    //检查图片是否完全设置
    private boolean isFinishedAll(boolean is) {
        if (StringUtil.isEmpty(HEADIMG_FILE)) {
            ToastUtils.showToast("头像未设置或设置失败,请设置头像");
            return false;
        }
        if (StringUtil.isEmpty(ZHENGIMG_FILE)) {
            ToastUtils.showToast("身份证正面未设置或设置失败,请设置");
            return false;
        }
        if (StringUtil.isEmpty(FANIMG_FILE)) {
            ToastUtils.showToast("身份证反面未设置或设置失败,请设置");
            return false;
        }
        if (StringUtil.isEmpty(ZHICHENGIMG_FILE)) {
            ToastUtils.showToast("职称证未设置或设置失败,请设置");
            return false;
        }
        if (StringUtil.isEmpty(ZHIYEIMG_FILE)) {
            ToastUtils.showToast("医师职业证未设置或设置失败,请设置");
            return false;
        }
        return true;
    }

    //检查图片是否完全设置
    private boolean isFinishedAll() {
        if (!AUTHEN_HEAD_FINISHED) {
            return false;
        }
        if (!AUTHEN_ZHENG_FINISHED) {
            return false;
        }
        if (!AUTHEN_FAN_FINISHED) {
            return false;
        }
        if (!AUTHEN_ZHICHENG_FINISHED) {
            return false;
        }
        if (!AUTHEN_ZHIYE_FINISHED) {
            return false;
        }
        return true;
    }


    /**
     * 修改用户头像（网易）
     *
     * @param headPortrait
     */
    private void submitModifyNimUserIcon(String headPortrait) {
        Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
        fields.put(UserInfoFieldEnum.AVATAR, headPortrait);
        NIMClient.getService(UserService.class).updateUserInfo(fields).setCallback(new RequestCallbackWrapper<Void>() {
            @Override
            public void onResult(int i, Void aVoid, Throwable throwable) {

            }
        });
    }

    /**
     * 修改用户昵称（网易）
     *
     * @param nickName
     */
    private void submitModifyNimUserName(String nickName) {
        Map<UserInfoFieldEnum, Object> fields = new HashMap<>(1);
        fields.put(UserInfoFieldEnum.Name, nickName);
        NIMClient.getService(UserService.class).updateUserInfo(fields).setCallback(new RequestCallbackWrapper<Void>() {
            @Override
            public void onResult(int i, Void aVoid, Throwable throwable) {

            }
        });
    }
}
