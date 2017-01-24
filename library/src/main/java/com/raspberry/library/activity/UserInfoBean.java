package com.raspberry.library.activity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/10/20.
 */

public class UserInfoBean implements Serializable{

    /**
     * birthday : 2016-11-15T01:54:07.276Z
     * bloodType : A
     * createDate : 2016-11-15T01:54:07.276Z
     * headPortrait : string
     * height : 0
     * id : 0
     * informationConfidential : OPEN
     * name : string
     * nickName : string
     * notificationSwitch : OPEN
     * password : string
     * phone : string
     * roles : [{"createDate":"2016-11-15T01:54:07.276Z","id":0,"name":"string","permissions":[{"createDate":"2016-11-15T01:54:07.276Z","id":0,"name":"string","status":"USE"}],"status":"USE"}]
     * sex : MAN
     * userGradeId : 0
     * userName : string
     * vip : string
     * weight : 0
     */

    private String birthday;
    private String bloodType;
    private String createDate;
    private String headPortrait;
    private int height;
    private int id;
    private String informationConfidential;
    private String name;
    private String nickName;
    private String notificationSwitch;
    private String password;
    private String phone;
    private String sex;
    private int userGradeId;
    private String userName;
    private String vip;
    private int weight;
    /**
     * createDate : 2016-11-15T01:54:07.276Z
     * id : 0
     * name : string
     * permissions : [{"createDate":"2016-11-15T01:54:07.276Z","id":0,"name":"string","status":"USE"}]
     * status : USE
     */

    private List<RolesBean> roles;

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getHeadPortrait() {
        return headPortrait;
    }

    public void setHeadPortrait(String headPortrait) {
        this.headPortrait = headPortrait;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInformationConfidential() {
        return informationConfidential;
    }

    public void setInformationConfidential(String informationConfidential) {
        this.informationConfidential = informationConfidential;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNotificationSwitch() {
        return notificationSwitch;
    }

    public void setNotificationSwitch(String notificationSwitch) {
        this.notificationSwitch = notificationSwitch;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getUserGradeId() {
        return userGradeId;
    }

    public void setUserGradeId(int userGradeId) {
        this.userGradeId = userGradeId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
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
         * createDate : 2016-11-15T01:54:07.276Z
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
