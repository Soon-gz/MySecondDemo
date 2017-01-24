package com.shkjs.patient.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaohu on 2016/11/11.
 */

public class GroupSitDiagnoseDoctorsInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 7137363841748336634L;

    private List<GroupSitDiagnoseDoctors> doctors;
    private String roomName;

    public void setDoctor(GroupSitDiagnoseDoctors doctor) {
        if (null == this.doctors) {
            this.doctors = new ArrayList<>();
        }
        this.doctors.add(doctor);
    }

    public List<GroupSitDiagnoseDoctors> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<GroupSitDiagnoseDoctors> doctors) {
        this.doctors = doctors;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    @Override
    public String toString() {
        return "GroupSitDiagnoseDoctorsInfo{" +
                "doctors=" + doctors +
                ", roomName='" + roomName + '\'' +
                '}';
    }
}
