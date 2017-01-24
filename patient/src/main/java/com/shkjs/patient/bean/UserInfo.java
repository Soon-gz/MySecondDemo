package com.shkjs.patient.bean;

import com.raspberry.library.util.TextUtils;
import com.shkjs.patient.data.em.BloodType;
import com.shkjs.patient.data.em.InformationConfidential;
import com.shkjs.patient.data.em.NotificationSwitch;
import com.shkjs.patient.data.em.Sex;

public class UserInfo extends User {

    private static final long serialVersionUID = -6377342820571811880L;

    private InformationConfidential informationConfidential;

    private Sex sex;

    private String birthday;

    private String name;

    private String nickName;

    private String headPortrait;

    private Long userGradeId;

    private Integer height;

    private Integer weight;

    private BloodType bloodType;

    private String vip;

    //前端处理
    @Deprecated
    private NotificationSwitch notificationSwitch;

    public InformationConfidential getInformationConfidential() {
        return informationConfidential;
    }

    public void setInformationConfidential(InformationConfidential informationConfidential) {
        this.informationConfidential = informationConfidential;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getNickName() {
        return TextUtils.isEmpty(nickName) ? "" : nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait == null ? null : headPortrait.trim();
    }

    public Long getUserGradeId() {
        return userGradeId;
    }

    public void setUserGradeId(Long userGradeId) {
        this.userGradeId = userGradeId;
    }

    public NotificationSwitch getNotificationSwitch() {
        return notificationSwitch;
    }

    public void setNotificationSwitch(NotificationSwitch notificationSwitch) {
        this.notificationSwitch = notificationSwitch;
    }


    /**
     * 得到一个普通用户
     *
     * @return
     */
    public static UserInfo newDefaultUser() {
        UserInfo userInfo = new UserInfo();
        userInfo.setInformationConfidential(InformationConfidential.SPECIFICOPEN);
        userInfo.setUserGradeId(1l);
        userInfo.setNotificationSwitch(NotificationSwitch.OPEN);
        return userInfo;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Sex getSex() {
        return sex == null ? Sex.SECRECY : sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public BloodType getBloodType() {
        return bloodType == null ? BloodType.UNKONW : bloodType;
    }

    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (((UserInfo) obj).getId().equals(this.getId())) {
            return true;
        }
        return super.equals(obj);
    }
}