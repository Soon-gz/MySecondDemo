package com.shkjs.doctor.bean.push;


import com.raspberry.library.activity.UserInfoBean;
import com.shkjs.doctor.bean.DoctorBean;

public class GroupSitDiagnoseDetailDto extends GroupSitDiagnoseDetail {
	
	private static final long serialVersionUID = -3292738607260830746L;
	
	private GroupSitDiagnose groupSitDiagnose;
	private DoctorBean doctor;
	private UserInfoBean user;

	public GroupSitDiagnose getGroupSitDiagnose() {
		return groupSitDiagnose;
	}

	public void setGroupSitDiagnose(GroupSitDiagnose groupSitDiagnose) {
		this.groupSitDiagnose = groupSitDiagnose;
	}

	public UserInfoBean getUser() {
		return user;
	}

	public void setUser(UserInfoBean user) {
		this.user = user;
	}

	public DoctorBean getDoctor() {
		return doctor;
	}

	public void setDoctor(DoctorBean doctor) {
		this.doctor = doctor;
	}


}
