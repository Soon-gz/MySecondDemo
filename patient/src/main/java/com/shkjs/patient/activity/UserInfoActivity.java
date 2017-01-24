package com.shkjs.patient.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;
import com.orhanobut.logger.Logger;
import com.raspberry.library.glide.CircleTransform;
import com.raspberry.library.util.AppUtils;
import com.raspberry.library.util.BasePopupWindow;
import com.raspberry.library.util.TextUtils;
import com.raspberry.library.util.TimeFormatUtils;
import com.raspberry.library.util.ToastUtils;
import com.raspberry.library.view.PhotoPopupWindow;
import com.raspberry.library.view.TimePopupWindow;
import com.shkjs.patient.Preference;
import com.shkjs.patient.R;
import com.shkjs.patient.base.BaseActivity;
import com.shkjs.patient.bean.UserInfo;
import com.shkjs.patient.cache.DataCache;
import com.shkjs.patient.data.em.BloodType;
import com.shkjs.patient.data.em.ResultStatus;
import com.shkjs.patient.data.em.Sex;
import com.shkjs.patient.data.response.ObjectResponse;
import com.shkjs.patient.http.HttpBase;
import com.shkjs.patient.http.HttpProtocol;
import com.shkjs.patient.http.RaspberryCallback;
import com.shkjs.patient.view.BloodChoosePopup;
import com.shkjs.patient.view.QRNormalPopup;
import com.shkjs.patient.view.SexChoosePopup;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import net.qiujuer.common.okhttp.Http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xiaohu on 2016/9/23.
 * <p>
 * 用户信息
 */
public class UserInfoActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener,
        TakePhoto.TakeResultListener, InvokeListener {

    private static final int TYPE_EDIT = 121;//浏览界面
    private static final int TYPE_SAVE = 122;//编辑界面
    private static final int CAMERA_REQUEST_CODE = 123;//拍照
    private static final int GALLERY_REQUEST_CODE = 124;//选择图片
    private static final int PHOTO_RESOULT = 125;// 裁剪结果
    private static final int USER_NICK_NAME = 126;// 昵称
    private static final int USER_SEX = 127;// 性别
    private static final int USER_AGE = 128;// 年龄
    private static final int USER_HEIGHT = 129;// 身高
    private static final int USER_WEIGHT = 130;// 体重
    private static final int USER_BLOOD = 131;// 血型
    private static final String IMAGE_USPECIFIED = "image/*";

    private Uri photoUri;
    private String fileName;
    private String tempFileName;
    private UserInfo userInfo;
    private String nickNameStr;// 昵称
    private String sexStr;// 性别
    private String birthdayStr;// 出生年月
    private String heightStr;// 身高
    private String weightStr;// 体重
    private String bloodStr;// 血型

    private int selectType = TYPE_EDIT;//默认浏览界面

    private Toolbar toolbar;

    @Bind(R.id.user_icon_ll)
    LinearLayout userIconLL;
    @Bind(R.id.user_icon_iv)
    ImageView userIconIV;// 头像
    @Bind(R.id.user_nick_name_et)
    EditText userNickNameET;// 昵称
    @Bind(R.id.user_sex_tv)
    TextView userSexTV;// 性别
    @Bind(R.id.user_sex_ll)
    LinearLayout userSexLL;
    @Bind(R.id.user_age_tv)
    TextView userBirthdayTV;// 出生年月
    @Bind(R.id.user_age_ll)
    LinearLayout userAgeLL;
    @Bind(R.id.user_height_et)
    EditText userHeightET;// 身高
    @Bind(R.id.user_weight_et)
    EditText userWeightET;// 体重
    @Bind(R.id.user_blood_tv)
    TextView userBloodTV;// 血型
    @Bind(R.id.user_blood_ll)
    LinearLayout userBloodLL;
    @Bind(R.id.user_synopsis_tv)
    TextView userSynopsisTV;
    @Bind(R.id.user_synopsis_ll)
    LinearLayout userSynopsisLL;

    private SexChoosePopup sexChoosePopup;
    private TimePopupWindow timePopupWindow;
    private BloodChoosePopup bloodChoosePopup;

    private InvokeParam invokeParam;
    private TakePhoto takePhoto;

    private RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
        @Override
        public void onFailure(Request request, Response response, Exception e) {
            Logger.d("File", e.getLocalizedMessage());
        }

        @Override
        public void onSuccess(ObjectResponse<String> response, int code) {
            super.onSuccess(response, code);
            if (code == HttpBase.SUCCESS) {
                if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                    Logger.d("File", response);
                    submitModifyUserIcon(response.getData());
                } else {
                    Logger.e("File", getString(R.string.upload_file_failed) + response.getMsg());
                }
            } else {
                Logger.e("File", getString(R.string.upload_file_failed));
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_info);

        //初始化控件
        ButterKnife.bind(this);
        //        initToolbar(selectType);

        toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.user_info);

        initData();
        initListener();
        getUserInfo();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.clear(userIconIV);
        Http.cancel(UserInfoActivity.class.getSimpleName());
        String userInfoStr = createUserInfo();
        if (null != userInfoStr) {
            submitModifyUserInfo(userInfoStr);
        }
        if (null != this.userInfo) {
            DataCache.getInstance().setUserInfo(this.userInfo);
        }
    }

    private void initData() {
        fileName = Environment.getExternalStorageDirectory().getPath() + "/" + AppUtils.getPackageName
                (UserInfoActivity.this) + "/cache/" + Preference.USER_ICON;//要保存照片的绝对路径
        tempFileName = Environment.getExternalStorageDirectory().getPath() + "/" + AppUtils.getPackageName
                (UserInfoActivity.this) + "/cache/temp.jpg";
    }

    private void initListener() {

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //        userSynopsisLL.setOnClickListener(this);//屏蔽个人简介
        userBloodLL.setOnClickListener(this);
        userSexLL.setOnClickListener(this);
        userAgeLL.setOnClickListener(this);
        userIconLL.setOnClickListener(this);
        userIconIV.setOnClickListener(this);

        userNickNameET.setOnFocusChangeListener(this);
        userHeightET.setOnFocusChangeListener(this);
        userWeightET.setOnFocusChangeListener(this);

    }

    private void initToolbar(final int type) {
        switch (type) {
            case TYPE_EDIT:
                initToolBarMenu(R.menu.edit_menu);
                setEnabled(false);//不可编辑
                break;
            case TYPE_SAVE:
                initToolBarMenu(R.menu.save_menu);
                setEnabled(true);//可编辑
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

    }

    private void initToolBarMenu(int id) {
        if (id == R.menu.save_menu) {
            toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.edit_user_info);
        } else {
            toolbar = initToolbar(R.id.toolbar, R.id.toolbar_title, R.string.user_info);
        }
        setSupportActionBar(null);//不这样设置，下面加载布局无效
        toolbar.getMenu().clear();
        toolbar.inflateMenu(id);
    }

    private void setEnabled(boolean enabled) {
        userIconLL.setEnabled(enabled);
        userNickNameET.setEnabled(enabled);
        userSexLL.setEnabled(enabled);
        userAgeLL.setEnabled(enabled);
        userHeightET.setEnabled(enabled);
        userWeightET.setEnabled(enabled);
        userBloodLL.setEnabled(enabled);
    }

    private void getUserInfo() {

        RaspberryCallback<ObjectResponse<UserInfo>> callback = new RaspberryCallback<ObjectResponse<UserInfo>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                Logger.e("UserInfo", getString(R.string.get_userinfo_fail_text) + e.getLocalizedMessage());
                ToastUtils.showToast(getString(R.string.get_userinfo_fail_text));
                finish();
            }

            @Override
            public void onSuccess(ObjectResponse<UserInfo> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED) && null != response.getData()) {
                        userInfo = response.getData();
                        if (null != userInfo.getNickName()) {
                            userNickNameET.setText(userInfo.getNickName());
                        }
                        if (null != userInfo.getSex()) {
                            userSexTV.setText(userInfo.getSex().getMark());
                        }
                        if (!TextUtils.isEmpty(userInfo.getBirthday())) {
                            userBirthdayTV.setText(TimeFormatUtils.getLocalTime(Long.parseLong(userInfo.getBirthday()
                            )));
                        }
                        if (null != userInfo.getHeight()) {
                            userHeightET.setText("" + userInfo.getHeight());
                        }
                        if (null != userInfo.getWeight()) {
                            userWeightET.setText("" + userInfo.getWeight());
                        }
                        if (null != userInfo.getBloodType()) {
                            userBloodTV.setText(userInfo.getBloodType() == null ? "" : userInfo.getBloodType()
                                    .getMark());
                        }
                        if (null != userInfo.getHeadPortrait()) {
                            Glide.with(UserInfoActivity.this).load(HttpBase.BASE_OSS_URL + userInfo.getHeadPortrait()
                            ).error(R.drawable.main_headportrait_xlarge).placeholder(R.drawable
                                    .main_headportrait_xlarge).transform(new CircleTransform(UserInfoActivity.this))
                                    .into(userIconIV);
                        }
                    } else {
                        Logger.e("UserInfo", getString(R.string.get_userinfo_fail_text) + response.getMsg());
                        ToastUtils.showToast(getString(R.string.get_userinfo_fail_text) + response.getMsg());
                        finish();
                    }
                } else {
                    Logger.e("UserInfo", getString(R.string.get_userinfo_fail_text));
                    ToastUtils.showToast(getString(R.string.get_userinfo_fail_text));
                    finish();
                }
            }
        };
        callback.setContext(this);
        callback.setMainThread(true);
        callback.setCancelable(false);
        HttpProtocol.getUserDetail(UserInfoActivity.class.getSimpleName(), callback);
    }

    private void submitModifyUserIcon(final String headPortrait) {
        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                Logger.e("UserInfo", getString(R.string.modify_userinfo_fail_text) + e.getLocalizedMessage());
                ToastUtils.showToast(getString(R.string.modify_userinfo_fail_text));
            }

            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        //                        Glide.with(UserInfoActivity.this).load(HttpBase.BASE_OSS_URL +
                        // headPortrait).transform(new
                        //                                CircleTransform(UserInfoActivity
                        //                                .this)).into(userIconIV);
                        userInfo.setHeadPortrait(headPortrait);
                        DataCache.getInstance().getUserInfo().setHeadPortrait(headPortrait);
                        Intent intent = new Intent(Preference.UPDATE_VIEW_ACTION);
                        sendBroadcast(intent);
                    } else {
                        Logger.e("UserInfo", getString(R.string.modify_userinfo_fail_text) + response.getMsg());
                        ToastUtils.showToast(getString(R.string.modify_userinfo_fail_text) + response.getMsg());
                    }
                } else {
                    Logger.e("UserInfo", getString(R.string.modify_userinfo_fail_text));
                    ToastUtils.showToast(getString(R.string.modify_userinfo_fail_text));
                }
            }
        };
        callback.setContext(this);
        callback.setMainThread(false);
        callback.setCancelable(false);
        HttpProtocol.modifyUserIcon(headPortrait, callback);
        submitModifyNimUserIcon(HttpBase.BASE_OSS_URL + headPortrait);
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

    private void submitModifyUserInfo(String userInfo) {
        RaspberryCallback<ObjectResponse<String>> callback = new RaspberryCallback<ObjectResponse<String>>() {
            @Override
            public void onFailure(Request request, Response response, Exception e) {
                super.onFailure(request, response, e);
                Logger.e("UserInfo", getString(R.string.modify_userinfo_fail_text) + e.getLocalizedMessage());
            }

            @Override
            public void onSuccess(ObjectResponse<String> response, int code) {
                super.onSuccess(response, code);
                if (code == HttpBase.SUCCESS) {
                    if (response.getStatus().equals(ResultStatus.SUCCEED)) {
                        Intent intent = new Intent(Preference.UPDATE_VIEW_ACTION);
                        sendBroadcast(intent);
                    } else {
                        Logger.e("UserInfo", getString(R.string.modify_userinfo_fail_text) + response.getMsg());
                    }
                } else {
                    Logger.e("UserInfo", getString(R.string.modify_userinfo_fail_text));
                }
            }
        };
        callback.setMainThread(false);
        callback.setCancelable(false);
        HttpProtocol.modifyUserInfo(userInfo, callback);
    }

    private String createUserInfo() {
        String bodyStr = null;
        if (null == userInfo) {
            return bodyStr;
        }
        nickNameStr = TextUtils.getText(userNickNameET);
        sexStr = TextUtils.getText(userSexTV);
        birthdayStr = TextUtils.getText(userBirthdayTV);
        heightStr = TextUtils.getText(userHeightET);
        weightStr = TextUtils.getText(userWeightET);
        bloodStr = TextUtils.getText(userBloodTV);

        if (!TextUtils.isEmpty(nickNameStr) && (!nickNameStr.equals(userInfo.getNickName()))) {
            bodyStr = createBodyStr(bodyStr, USER_NICK_NAME);
            userInfo.setNickName(nickNameStr);
            submitModifyNimUserName(nickNameStr);
        }
        if (!TextUtils.isEmpty(sexStr) && ((null == userInfo.getSex() || !sexStr.equals(userInfo.getSex().getMark()))
        )) {
            bodyStr = createBodyStr(bodyStr, USER_SEX);
            userInfo.setSex(Sex.getSex(sexStr));
        }
        if (!TextUtils.isEmpty(birthdayStr) && (!TextUtils.isEmpty(userInfo.getBirthday()) || !birthdayStr.equals
                (userInfo.getBirthday()))) {
            bodyStr = createBodyStr(bodyStr, USER_AGE);
            userInfo.setBirthday(TimeFormatUtils.getDate(birthdayStr).getTime() + "");
        }
        if (!TextUtils.isEmpty(heightStr) && (null == userInfo.getHeight() || Integer.valueOf(heightStr) != userInfo
                .getHeight())) {
            bodyStr = createBodyStr(bodyStr, USER_HEIGHT);
            userInfo.setHeight(Integer.valueOf(heightStr));
        }
        if (!TextUtils.isEmpty(weightStr) && (null == userInfo.getWeight() || Integer.valueOf(weightStr) != userInfo
                .getWeight())) {
            bodyStr = createBodyStr(bodyStr, USER_WEIGHT);
            userInfo.setWeight(Integer.valueOf(weightStr));
        }
        if (!TextUtils.isEmpty(bloodStr) && (null == userInfo.getBloodType() || !bloodStr.equals(userInfo
                .getBloodType().getMark()))) {
            bodyStr = createBodyStr(bodyStr, USER_BLOOD);
            userInfo.setBloodType(BloodType.getBloodType(bloodStr));
        }

        return bodyStr;
    }

    private String createBodyStr(String bodyStr, int bodyType) {
        if (null != bodyStr) {
            bodyStr = bodyStr + "&";
        } else {
            bodyStr = "";
        }
        switch (bodyType) {
            case USER_NICK_NAME:
                bodyStr = bodyStr + "nickName=" + nickNameStr;
                break;
            case USER_SEX:
                bodyStr = bodyStr + "sex=" + Sex.getSex(sexStr).name();
                break;
            case USER_AGE:
                bodyStr = bodyStr + "birthday=" + birthdayStr;
                break;
            case USER_HEIGHT:
                bodyStr = bodyStr + "height=" + heightStr;
                break;
            case USER_WEIGHT:
                bodyStr = bodyStr + "weight=" + weightStr;
                break;
            case USER_BLOOD:
                bodyStr = bodyStr + "bloodType=" + BloodType.getBloodType(bloodStr).name();
                break;
            default:
                break;
        }
        return bodyStr;
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
                    userSexTV.setText(Sex.getSex(getString(R.string.sex_man)).getMark());
                    sexChoosePopup.dismiss();
                }
            });

            sexChoosePopup.chooseWomanListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userSexTV.setText(Sex.getSex(getString(R.string.sex_woman)).getMark());
                    sexChoosePopup.dismiss();
                }
            });

        }
        sexChoosePopup.showPopupWindow();
    }

    /**
     * 创建出生年日选择界面
     */
    private void createTimePopup() {
        if (null == timePopupWindow) {
            timePopupWindow = new TimePopupWindow(this);
            timePopupWindow.setSureListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userBirthdayTV.setText(timePopupWindow.getTime());
                    timePopupWindow.dismiss();
                }
            });

        }
        timePopupWindow.showPopupWindow();
    }

    /**
     * 创建血型选择框
     */
    private void cerateBloodPopup() {
        if (null == bloodChoosePopup) {
            bloodChoosePopup = new BloodChoosePopup(this);

            bloodChoosePopup.setChooseAListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userBloodTV.setText(bloodChoosePopup.getText((TextView) v));
                    bloodChoosePopup.dismiss();
                }
            });
            bloodChoosePopup.setChooseBListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userBloodTV.setText(bloodChoosePopup.getText((TextView) v));
                    bloodChoosePopup.dismiss();
                }
            });
            bloodChoosePopup.setChooseOListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userBloodTV.setText(bloodChoosePopup.getText((TextView) v));
                    bloodChoosePopup.dismiss();
                }
            });
            bloodChoosePopup.setChooseABListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userBloodTV.setText(bloodChoosePopup.getText((TextView) v));
                    bloodChoosePopup.dismiss();
                }
            });

        }
        bloodChoosePopup.showPopupWindow();
    }

    private void modifyUserIcon() {

        final PhotoPopupWindow popupWindow = new PhotoPopupWindow(UserInfoActivity.this);

        //拍照
        popupWindow.setPhotographListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    photoUri = Uri.fromFile(new File(tempFileName));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, CAMERA_REQUEST_CODE);
                    //                    CropOptions options = new CropOptions.Builder().setAspectX(1).setAspectY(1)
                    // .setOutputX(320)
                    //                            .setOutputY(320).setWithOwnCrop(false).create();
                    //                    getTakePhoto().onPickFromCaptureWithCrop(photoUri, options);
                } else {
                    ToastUtils.showToast(getString(R.string.not_camera));
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
                //                photoUri = Uri.fromFile(new File(tempFileName));
                //                CropOptions options = new CropOptions.Builder().setAspectX(1).setAspectY(1)
                // .setOutputX(320)
                //                        .setOutputY(320).setWithOwnCrop(false).create();
                //                getTakePhoto().onPickFromGalleryWithCrop(photoUri, options);
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
    private void startPhotoZoom(Uri uri) {
        if (null == uri) {
            ToastUtils.showToast(getString(R.string.data_error));
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");// 调用Android系统自带的一个图片剪裁页面,
        intent.setDataAndType(uri, IMAGE_USPECIFIED);
        intent.putExtra("crop", "true");// 进行修剪
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_RESOULT);
    }

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

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Glide.with(UserInfoActivity.this).load(new File(fileName)).transform(new CircleTransform(UserInfoActivity
                .this)).skipMemoryCache(true) //跳过内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 跳过硬盘缓存
                .into(userIconIV);

        callback.setContext(this);
        callback.setMainThread(false);
        callback.setCancelable(false);
        HttpProtocol.fileUpload(new File(fileName), callback);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_synopsis_ll:
                startActivityForResult(new Intent(UserInfoActivity.this, ModifyUserSynopsis.class), 121);
                break;
            case R.id.user_icon_iv:
                final QRNormalPopup normalPopup = new QRNormalPopup(UserInfoActivity.this);
                normalPopup.getTextView().setVisibility(View.GONE);
                Glide.with(this).load(HttpBase.BASE_OSS_URL + userInfo.getHeadPortrait()).error(R.drawable
                        .main_headportrait_xlarge).placeholder(R.drawable.main_headportrait_xlarge).transform(new
                        CircleTransform(UserInfoActivity.this)).into(normalPopup.getImageView());
                normalPopup.showPopupWindow();
                normalPopup.setOnDismissListener(new BasePopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        Glide.clear(normalPopup.getImageView());
                    }
                });
                break;
            case R.id.user_icon_ll:
                modifyUserIcon();
                break;
            case R.id.user_sex_ll://弹出性别选择框
                createSexPopup();
                break;
            case R.id.user_blood_ll://弹出血型选择框
                cerateBloodPopup();
                break;
            case R.id.user_age_ll://弹出出生年月日选择框
                createTimePopup();
                break;
            default:
                break;
        }
        if (view instanceof ViewGroup) {
            getWindow().getDecorView().setFocusable(true);
            getWindow().getDecorView().setFocusableInTouchMode(true);
            getWindow().getDecorView().requestFocus();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE://拍照返回
                if (resultCode == RESULT_OK) {
                    startPhotoZoom(photoUri);
                } else {
                    ToastUtils.showToast("您没有拍照~请重新操作");
                }
                break;
            case GALLERY_REQUEST_CODE://选择文件返回
                if (resultCode == RESULT_OK) {
                    photoUri = data.getData();
                    startPhotoZoom(photoUri);
                } else {
                    ToastUtils.showToast("您没有选择照片~请重新操作");
                }
                break;
            case PHOTO_RESOULT://裁剪返回
                if ((resultCode == RESULT_OK) && (null != data)) {
                    resultHandle(data);
                }
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.user_nick_name_et:
                if (hasFocus) {
                    userNickNameET.setGravity(Gravity.LEFT);
                } else {
                    userNickNameET.setGravity(Gravity.RIGHT);
                }
                break;
            case R.id.user_height_et:
                if (hasFocus) {
                    userHeightET.setGravity(Gravity.LEFT);
                } else {
                    userHeightET.setGravity(Gravity.RIGHT);
                }
                break;
            case R.id.user_weight_et:
                if (hasFocus) {
                    userWeightET.setGravity(Gravity.LEFT);
                } else {
                    userWeightET.setGravity(Gravity.RIGHT);
                }
                break;
            default:
                break;
        }
    }

    /**
     * *****************************TakePhoto**********************************
     **/
    @Override
    public void takeSuccess(TResult result) {
        Logger.d(result);

        //        fileName = result.getImage().getOriginalPath();
        //
        //        Glide.with(UserInfoActivity.this).load(new File(fileName)).transform(new CircleTransform
        // (UserInfoActivity
        //                .this)).skipMemoryCache(true) //跳过内存缓存
        //                .diskCacheStrategy(DiskCacheStrategy.NONE) // 跳过硬盘缓存
        //                .into(userIconIV);
        //
        //        callback.setContext(this);
        //        callback.setMainThread(true);
        //        callback.setCancelable(false);
        //        HttpProtocol.fileUpload(new File(fileName), callback);
    }

    @Override
    public void takeFail(TResult result, String msg) {
        Logger.e("takeFail : " + msg);
    }

    @Override
    public void takeCancel() {
        Logger.d("takeCancel : ");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode,
                permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam
                .getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    /**
     * 获取TakePhoto实例
     *
     * @return
     */
    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }
}
