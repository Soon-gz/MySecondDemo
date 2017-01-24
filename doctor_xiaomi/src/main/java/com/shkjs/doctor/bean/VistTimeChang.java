package com.shkjs.doctor.bean;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Shuwen on 2016/10/12.
 */

public class VistTimeChang implements Parcelable {
    private boolean isChanged = false;

    public VistTimeChang(boolean isChanged) {
        this.isChanged = isChanged;
    }

    protected VistTimeChang(Parcel in) {
        isChanged = in.readByte() != 0;
    }

    public static final Creator<VistTimeChang> CREATOR = new Creator<VistTimeChang>() {
        @Override
        public VistTimeChang createFromParcel(Parcel in) {
            return new VistTimeChang(in);
        }

        @Override
        public VistTimeChang[] newArray(int size) {
            return new VistTimeChang[size];
        }
    };

    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(boolean changed) {
        isChanged = changed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (isChanged ? 1 : 0));
    }
}
