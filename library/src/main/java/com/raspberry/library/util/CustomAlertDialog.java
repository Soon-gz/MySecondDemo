package com.raspberry.library.util;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.raspberry.library.R;


/**
 * Created by Shuwen on 2016/9/25.
 */
public class CustomAlertDialog {
    private static AlertDialog myDialog = null;

    private static final int RESIDENT_DOCTOR = 0;//住院医师
    private static final int VISITING_STAFF = 1;//主治医师
    private static final int ASSOCIATE_CHIEF_PHYSICIAN = 2;//副主任医师
    private static final int CHIEF_PHYSICIAN = 3;//主任医师
    private static final int CHOOSE_DOCTOR_CANCEL= 4;//取消选择

    private static final int DIALOG_CANCEL = 5;//进入视频弹出的dialog取消键
    private static final int DIALOG_SURE = 6;//进入视频弹窗的dialog确定键

    private static final int DIALOG_MAN = 7;//性别选择的男
    private static final int DIALOG_WOMAN = 8;//性别选择的女

    private static final int DIALOG_SURE_GROUP = 9;//会诊接受
    private static final int DIALOG_CANCEL_GROUP = 10;//拒绝会诊

    public static void dialogExSureCancel(String mes, final Context context, final OnDialogClickListener onDialogClickListener) {
        createDialog(context,R.layout.alert_dialog_layout);
        TextView textView = (TextView) myDialog.getWindow().findViewById(R.id.dialog_msg);
        textView.setText(mes);
        setItemOnClickListener(R.id.dialog_cancel,onDialogClickListener,DIALOG_CANCEL);
        setItemOnClickListener(R.id.dialog_sure,onDialogClickListener,DIALOG_SURE);
    }

    public static void dialogExSureIgnore(String mes, final Context context, final OnDialogClickListener onDialogClickListener) {
        createDialog(context,R.layout.alert_dialog_ignore);
        TextView textView = (TextView) myDialog.getWindow().findViewById(R.id.dialog_msg);
        textView.setText(mes);
        setItemOnClickListener(R.id.dialog_cancel,onDialogClickListener,DIALOG_CANCEL);
        setItemOnClickListener(R.id.dialog_sure,onDialogClickListener,DIALOG_SURE);
    }


    /**
     * 创建dialog实例
     * @param context 传入的上下，在其上显示弹窗
     * @param layoutId 自定义布局id
     */
    private static void createDialog(Context context,int layoutId){
        myDialog = new AlertDialog.Builder(context).create();
        myDialog.show();
        Window window = myDialog.getWindow();
        window.setWindowAnimations(R.style.dialog_anim);
        myDialog.setCanceledOnTouchOutside(false);
        myDialog.getWindow().setContentView(layoutId);
    }

    /**
     * 选择性别的弹窗提示
     * @param context
     * @param onDialogItemClickListener
     */
    public static void dialogChooseSex(Context context,onItemClickListener onDialogItemClickListener){
        createDialog(context,R.layout.alert_dialog_choose_sex);
        setItemOnClickListener(R.id.alert_dialog_woman_tv,onDialogItemClickListener,DIALOG_WOMAN);
        setItemOnClickListener(R.id.alert_dialog_man_tv,onDialogItemClickListener,DIALOG_MAN);
        setItemOnClickListener(R.id.alert_dialog_sex_cancel_tv,onDialogItemClickListener,DIALOG_CANCEL);
    }

    /**
     * 会诊邀请确认
     * @param mes
     * @param context
     * @param onDialogItemClickListener
     */
    public static void dialogGroupCheck(String title,String mes,Context context,onItemClickListener onDialogItemClickListener){
        createDialog(context,R.layout.alert_dialog_group_layout);
        TextView textView = (TextView) myDialog.getWindow().findViewById(R.id.dialog_msg_group);
        textView.setText(mes);
        TextView textView1 = (TextView) myDialog.getWindow().findViewById(R.id.dialog_msg_group_title);
        textView1.setText(title);
        setItemOnClickListener(R.id.dialog_sure_group,onDialogItemClickListener,DIALOG_SURE_GROUP);
        setItemOnClickListener(R.id.dialog_cancel_group,onDialogItemClickListener,DIALOG_CANCEL_GROUP);
    }

    /**
     * 选择医师职称弹出的dialog
     * @param context
     * @param onDialogItemClickListener
     */
    public static void dialogChooseDoctor(Context context,onItemClickListener onDialogItemClickListener){
        createDialog(context,R.layout.alert_dialog_choose_doctor);
        setItemOnClickListener(R.id.alert_dialog_zhuyuan_tv,onDialogItemClickListener,RESIDENT_DOCTOR);
        setItemOnClickListener(R.id.alert_dialog_zhuzhi_tv,onDialogItemClickListener,VISITING_STAFF);
        setItemOnClickListener(R.id.alert_dialog_fuzhuren_tv,onDialogItemClickListener,ASSOCIATE_CHIEF_PHYSICIAN);
        setItemOnClickListener(R.id.alert_dialog_zhuren_tv,onDialogItemClickListener,CHIEF_PHYSICIAN);
        setItemOnClickListener(R.id.alert_dialog_cancel_tv,onDialogItemClickListener,CHOOSE_DOCTOR_CANCEL);
    }

    /**
     * 设置指定项点击事件
     * @param id
     * @param onDialogItemClickListener
     * @param position
     */
    private static void setItemOnClickListener(final int id, final onItemClickListener onDialogItemClickListener, final int position){
        myDialog.getWindow()
                .findViewById(id)
                .setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (onDialogItemClickListener == null){
                            myDialog.dismiss();
                            return;
                        }
                        switch (position){
                            case RESIDENT_DOCTOR:
                                ((onDialogItemClickListener)onDialogItemClickListener).residentDoctorOnClick();
                                break;
                            case VISITING_STAFF:
                                ((onDialogItemClickListener)onDialogItemClickListener).visitingStaffOnClick();
                                break;
                            case ASSOCIATE_CHIEF_PHYSICIAN:
                                ((onDialogItemClickListener)onDialogItemClickListener).associateChiefPhysicianOnClick();
                                break;
                            case CHIEF_PHYSICIAN:
                                ((onDialogItemClickListener)onDialogItemClickListener).chiefPhysicianOnClick();
                                break;
                            case CHOOSE_DOCTOR_CANCEL:
                                break;
                            case DIALOG_CANCEL:
                                break;
                            case DIALOG_SURE:
                                ((OnDialogClickListener)onDialogItemClickListener).doSomeThings();
                                break;
                            case DIALOG_MAN:
                                ((onDialogManClickListener)onDialogItemClickListener).chooseMan();
                                break;
                            case DIALOG_WOMAN:
                                ((onDialogManClickListener)onDialogItemClickListener).chooseWoman();
                                break;
                            case DIALOG_SURE_GROUP:
                                ((onDialogSureCancelListener)onDialogItemClickListener).agreeGroup();
                                break;
                            case DIALOG_CANCEL_GROUP:
                                ((onDialogSureCancelListener)onDialogItemClickListener).disAgree();
                                break;
                        }
                        myDialog.dismiss();
                        myDialog = null;
                    }

                });
    }


    private interface onItemClickListener{

    }

    /**
     * 创建只带确定按钮的提示弹窗
     * @param context
     */
    public static void dialogWithSure(Context context,String msg,OnDialogClickListener onDialogClickListener){
        createDialog(context,R.layout.alert_dialog_sure);
        TextView textView = (TextView) myDialog.getWindow().findViewById(R.id.dialog_sure_msg);
        textView.setText(msg);
        setItemOnClickListener(R.id.dialog_sure,onDialogClickListener,DIALOG_SURE);
    }
    /**
     * 创建设置坐诊时间只带确定按钮的提示弹窗
     * @param context
     */
    public static void dialogVisitTimeWithSure(Context context,String mes,int drawableId){
        createDialog(context,R.layout.alert_dialog_visitime);
        ImageView imageView = (ImageView) myDialog.getWindow().findViewById(R.id.dialog_sure_img);
        TextView textView = (TextView) myDialog.getWindow().findViewById(R.id.dialog_sure_msg);
        imageView.setBackgroundResource(drawableId);
        textView.setText(mes);
        setItemOnClickListener(R.id.dialog_sure,null,0);
    }



    public interface onDialogItemClickListener extends onItemClickListener{
        //住院医师被点击出发事件
        void residentDoctorOnClick();
        //主治医师
        void visitingStaffOnClick();
        //副主任医师
        void associateChiefPhysicianOnClick();
        //主任医师
        void chiefPhysicianOnClick();
    }

    public interface onDialogSureCancelListener extends onItemClickListener{
        //同意会诊邀请
        void agreeGroup();
        //拒绝会诊邀请
        void disAgree();
    }

    public interface onDialogManClickListener extends onItemClickListener{
        //选择男
        void chooseMan();
        //选择女
        void chooseWoman();
    }

    public interface OnDialogClickListener extends onItemClickListener{
        void doSomeThings();
    }
}
