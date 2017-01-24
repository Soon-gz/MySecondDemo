package com.shkjs.doctor.bean;

import com.shkjs.doctor.data.DoctorTag;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Shuwen on 2016/10/9.
 */

public class DoctorBean implements Serializable{

    /**
     * askHospitalFee : 0
     * categoryName : string
     * createDate : 2016-11-10T07:11:13.672Z
     * doctorPermit : string
     * headPortrait : string
     * hospitalName : string
     * id : 0
     * identityPermitFront : string
     * identityPermitReverse : string
     * introduce : string
     * introduceVideo : string
     * level : RESIDENTDOCTOR
     * name : string
     * otherPermit : string
     * password : string
     * platformLevel : AUTHORITY
     * roles : [{"createDate":"2016-11-10T07:11:13.672Z","id":0,"name":"string","permissions":[{"createDate":"2016-11-10T07:11:13.672Z","id":0,"name":"string","status":"USE"}],"status":"USE"}]
     * skilled : string
     * userName : string
     * viewHospitalFee : 0
     * workPermit : string
     */

    private int askHospitalFee;
    private String categoryName;
    private String createDate;
    private String doctorPermit;
    private String headPortrait;
    private String hospitalName;
    private DoctorTag tag;
    private int id;
    private String identityPermitFront;
    private String identityPermitReverse;
    private String introduce;
    private String introduceVideo;
    private String level;
    private String name;
    private String otherPermit;
    private String password;
    private String platformLevel;
    private String skilled;
    private String userName;
    private int viewHospitalFee;
    private String workPermit;
    /**
     * createDate : 2016-11-10T07:11:13.672Z
     * id : 0
     * name : string
     * permissions : [{"createDate":"2016-11-10T07:11:13.672Z","id":0,"name":"string","status":"USE"}]
     * status : USE
     */

    private List<RolesBean> roles;

    public DoctorTag getTag() {
        return tag;
    }

    public void setTag(DoctorTag tag) {
        this.tag = tag;
    }

    public int getAskHospitalFee() {
        return askHospitalFee;
    }

    public void setAskHospitalFee(int askHospitalFee) {
        this.askHospitalFee = askHospitalFee;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getDoctorPermit() {
        return doctorPermit;
    }

    public void setDoctorPermit(String doctorPermit) {
        this.doctorPermit = doctorPermit;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdentityPermitFront() {
        return identityPermitFront;
    }

    public void setIdentityPermitFront(String identityPermitFront) {
        this.identityPermitFront = identityPermitFront;
    }

    public String getIdentityPermitReverse() {
        return identityPermitReverse;
    }

    public void setIdentityPermitReverse(String identityPermitReverse) {
        this.identityPermitReverse = identityPermitReverse;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getIntroduceVideo() {
        return introduceVideo;
    }

    public void setIntroduceVideo(String introduceVideo) {
        this.introduceVideo = introduceVideo;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOtherPermit() {
        return otherPermit;
    }

    public void setOtherPermit(String otherPermit) {
        this.otherPermit = otherPermit;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPlatformLevel() {
        return platformLevel;
    }

    public void setPlatformLevel(String platformLevel) {
        this.platformLevel = platformLevel;
    }

    public String getSkilled() {
        return skilled;
    }

    public void setSkilled(String skilled) {
        this.skilled = skilled;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getViewHospitalFee() {
        return viewHospitalFee;
    }

    public void setViewHospitalFee(int viewHospitalFee) {
        this.viewHospitalFee = viewHospitalFee;
    }

    public String getWorkPermit() {
        return workPermit;
    }

    public void setWorkPermit(String workPermit) {
        this.workPermit = workPermit;
    }

    public List<RolesBean> getRoles() {
        return roles;
    }

    public void setRoles(List<RolesBean> roles) {
        this.roles = roles;
    }

    public static class RolesBean {
        private String createDate;
        private int id;
        private String name;
        private String status;
        /**
         * createDate : 2016-11-10T07:11:13.672Z
         * id : 0
         * name : string
         * status : USE
         */

        private List<PermissionsBean> permissions;

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<PermissionsBean> getPermissions() {
            return permissions;
        }

        public void setPermissions(List<PermissionsBean> permissions) {
            this.permissions = permissions;
        }

        public static class PermissionsBean {
            private String createDate;
            private int id;
            private String name;
            private String status;

            public String getCreateDate() {
                return createDate;
            }

            public void setCreateDate(String createDate) {
                this.createDate = createDate;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }
        }
    }
}
