package com.shkjs.doctor.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Shuwen on 2016/10/10.
 */

public class VisitTimeDataBean implements Parcelable {

    /**
     * createDate : 2016-10-10T01:40:04.209Z
     * diagnoseDate : 2016-10-10
     * diagnoseNum : 0
     * diagnoseNumSubscribed : 0
     * doctorId : 0
     * dr : string
     * endTime : 0
     * id : 0
     * segmentType : 0
     * startTime : 0
     * updateDate : 2016-10-10T01:40:04.224Z
     * version : 0
     */

    private String createDate;
    private String diagnoseDate;
    private int diagnoseNum;
    private int diagnoseNumSubscribed;
    private int doctorId;
    private String dr;
    private int endTime;
    private int id;
    private int segmentType;
    private int startTime;
    private String updateDate;
    private int version;

    protected VisitTimeDataBean(Parcel in) {
        createDate = in.readString();
        diagnoseDate = in.readString();
        diagnoseNum = in.readInt();
        diagnoseNumSubscribed = in.readInt();
        doctorId = in.readInt();
        dr = in.readString();
        endTime = in.readInt();
        id = in.readInt();
        segmentType = in.readInt();
        startTime = in.readInt();
        updateDate = in.readString();
        version = in.readInt();
    }
    public VisitTimeDataBean(){}

    public static final Creator<VisitTimeDataBean> CREATOR = new Creator<VisitTimeDataBean>() {
        @Override
        public VisitTimeDataBean createFromParcel(Parcel in) {
            return new VisitTimeDataBean(in);
        }

        @Override
        public VisitTimeDataBean[] newArray(int size) {
            return new VisitTimeDataBean[size];
        }
    };

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getDiagnoseDate() {
        return diagnoseDate;
    }

    public void setDiagnoseDate(String diagnoseDate) {
        this.diagnoseDate = diagnoseDate;
    }

    public int getDiagnoseNum() {
        return diagnoseNum;
    }

    public void setDiagnoseNum(int diagnoseNum) {
        this.diagnoseNum = diagnoseNum;
    }

    public int getDiagnoseNumSubscribed() {
        return diagnoseNumSubscribed;
    }

    public void setDiagnoseNumSubscribed(int diagnoseNumSubscribed) {
        this.diagnoseNumSubscribed = diagnoseNumSubscribed;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getDr() {
        return dr;
    }

    public void setDr(String dr) {
        this.dr = dr;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSegmentType() {
        return segmentType;
    }

    public void setSegmentType(int segmentType) {
        this.segmentType = segmentType;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "VisitTimeDataBean{" +
                "createDate='" + createDate + '\'' +
                ", diagnoseDate='" + diagnoseDate + '\'' +
                ", diagnoseNum=" + diagnoseNum +
                ", diagnoseNumSubscribed=" + diagnoseNumSubscribed +
                ", doctorId=" + doctorId +
                ", dr='" + dr + '\'' +
                ", endTime=" + endTime +
                ", id=" + id +
                ", segmentType=" + segmentType +
                ", startTime=" + startTime +
                ", updateDate='" + updateDate + '\'' +
                ", version=" + version +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(createDate);
        parcel.writeString(diagnoseDate);
        parcel.writeInt(diagnoseNum);
        parcel.writeInt(diagnoseNumSubscribed);
        parcel.writeInt(doctorId);
        parcel.writeString(dr);
        parcel.writeInt(endTime);
        parcel.writeInt(id);
        parcel.writeInt(segmentType);
        parcel.writeInt(startTime);
        parcel.writeString(updateDate);
        parcel.writeInt(version);
    }
}
