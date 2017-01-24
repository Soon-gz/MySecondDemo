package com.shkjs.doctor.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/11/25.
 */

public class EarliestVideoBean {

    /**
     * actuallyPaidMoney : 0
     * callingTimes : 0
     * code : string
     * createDate : 2016-11-25T04:59:15.887Z
     * diagnoseDate : 2016-11-25T04:59:15.887Z
     * doctor : {"askHospitalFee":0,"birthday":"2016-11-25T04:59:15.888Z","bloodType":"A","createDate":"2016-11-25T04:59:15.888Z","doctorPermit":"string","headPortrait":"string","height":0,"hospitalId":0,"id":0,"identityPermitFront":"string","identityPermitReverse":"string","informationConfidential":"OPEN","level":"RESIDENTDOCTOR","medicalCategoryId":0,"name":"string","nickName":"string","notificationSwitch":"OPEN","password":"string","phone":"string","platformLevel":"AUTHORITY","roles":[{"createDate":"2016-11-25T04:59:15.888Z","id":0,"name":"string","permissions":[{"createDate":"2016-11-25T04:59:15.888Z","id":0,"name":"string","status":"USE"}],"status":"USE"}],"sex":"MAN","userGradeId":0,"userName":"string","viewHospitalFee":0,"vip":"string","weight":0,"workPermit":"string"}
     * healthReport : {"createDate":"2016-11-25T04:59:15.889Z","id":0,"patientAge":0,"patientName":"string","patientSex":"MAN","seeDoctorTemplateId":0,"simpleIntroduction":"string","type":"PLATFORM","userId":0,"valid":"TRUE"}
     * id : 0
     * mark : string
     * money : 0
     * pId : 0
     * payDate : 2016-11-25T04:59:15.889Z
     * payOrigin : string
     * payType : BALANCE
     * source : INQUIRY_RESERVE
     * status : INITIAL
     * type : RECHARGE
     * userInfo : {"birthday":"2016-11-25T04:59:15.889Z","bloodType":"A","createDate":"2016-11-25T04:59:15.889Z","headPortrait":"string","height":0,"id":0,"informationConfidential":"OPEN","name":"string","nickName":"string","notificationSwitch":"OPEN","password":"string","phone":"string","roles":[{"createDate":"2016-11-25T04:59:15.890Z","id":0,"name":"string","permissions":[{"createDate":"2016-11-25T04:59:15.890Z","id":0,"name":"string","status":"USE"}],"status":"USE"}],"sex":"MAN","userGradeId":0,"userName":"string","vip":"string","weight":0}
     */

    private int actuallyPaidMoney;
    private int callingTimes;
    private String code;
    private String createDate;
    private String diagnoseDate;
    /**
     * askHospitalFee : 0
     * birthday : 2016-11-25T04:59:15.888Z
     * bloodType : A
     * createDate : 2016-11-25T04:59:15.888Z
     * doctorPermit : string
     * headPortrait : string
     * height : 0
     * hospitalId : 0
     * id : 0
     * identityPermitFront : string
     * identityPermitReverse : string
     * informationConfidential : OPEN
     * level : RESIDENTDOCTOR
     * medicalCategoryId : 0
     * name : string
     * nickName : string
     * notificationSwitch : OPEN
     * password : string
     * phone : string
     * platformLevel : AUTHORITY
     * roles : [{"createDate":"2016-11-25T04:59:15.888Z","id":0,"name":"string","permissions":[{"createDate":"2016-11-25T04:59:15.888Z","id":0,"name":"string","status":"USE"}],"status":"USE"}]
     * sex : MAN
     * userGradeId : 0
     * userName : string
     * viewHospitalFee : 0
     * vip : string
     * weight : 0
     * workPermit : string
     */

    private DoctorBean doctor;
    /**
     * createDate : 2016-11-25T04:59:15.889Z
     * id : 0
     * patientAge : 0
     * patientName : string
     * patientSex : MAN
     * seeDoctorTemplateId : 0
     * simpleIntroduction : string
     * type : PLATFORM
     * userId : 0
     * valid : TRUE
     */

    private HealthReportsWithBLOBs healthReport;
    private int id;
    private String mark;
    private int money;
    private int pId;
    private String payDate;
    private String payOrigin;
    private String payType;
    private String source;
    private String status;
    private String type;
    /**
     * birthday : 2016-11-25T04:59:15.889Z
     * bloodType : A
     * createDate : 2016-11-25T04:59:15.889Z
     * headPortrait : string
     * height : 0
     * id : 0
     * informationConfidential : OPEN
     * name : string
     * nickName : string
     * notificationSwitch : OPEN
     * password : string
     * phone : string
     * roles : [{"createDate":"2016-11-25T04:59:15.890Z","id":0,"name":"string","permissions":[{"createDate":"2016-11-25T04:59:15.890Z","id":0,"name":"string","status":"USE"}],"status":"USE"}]
     * sex : MAN
     * userGradeId : 0
     * userName : string
     * vip : string
     * weight : 0
     */

    private UserInfoBean userInfo;

    public int getActuallyPaidMoney() {
        return actuallyPaidMoney;
    }

    public void setActuallyPaidMoney(int actuallyPaidMoney) {
        this.actuallyPaidMoney = actuallyPaidMoney;
    }

    public int getCallingTimes() {
        return callingTimes;
    }

    public void setCallingTimes(int callingTimes) {
        this.callingTimes = callingTimes;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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

    public DoctorBean getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorBean doctor) {
        this.doctor = doctor;
    }

    public HealthReportsWithBLOBs getHealthReport() {
        return healthReport;
    }

    public void setHealthReport(HealthReportsWithBLOBs healthReport) {
        this.healthReport = healthReport;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getPId() {
        return pId;
    }

    public void setPId(int pId) {
        this.pId = pId;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getPayOrigin() {
        return payOrigin;
    }

    public void setPayOrigin(String payOrigin) {
        this.payOrigin = payOrigin;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }

    public static class DoctorBean {
        private int askHospitalFee;
        private String birthday;
        private String bloodType;
        private String createDate;
        private String doctorPermit;
        private String headPortrait;
        private int height;
        private int hospitalId;
        private int id;
        private String identityPermitFront;
        private String identityPermitReverse;
        private String informationConfidential;
        private String level;
        private int medicalCategoryId;
        private String name;
        private String nickName;
        private String notificationSwitch;
        private String password;
        private String phone;
        private String platformLevel;
        private String sex;
        private int userGradeId;
        private String userName;
        private int viewHospitalFee;
        private String vip;
        private int weight;
        private String workPermit;
        /**
         * createDate : 2016-11-25T04:59:15.888Z
         * id : 0
         * name : string
         * permissions : [{"createDate":"2016-11-25T04:59:15.888Z","id":0,"name":"string","status":"USE"}]
         * status : USE
         */

        private List<RolesBean> roles;

        public int getAskHospitalFee() {
            return askHospitalFee;
        }

        public void setAskHospitalFee(int askHospitalFee) {
            this.askHospitalFee = askHospitalFee;
        }

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

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getHospitalId() {
            return hospitalId;
        }

        public void setHospitalId(int hospitalId) {
            this.hospitalId = hospitalId;
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

        public String getInformationConfidential() {
            return informationConfidential;
        }

        public void setInformationConfidential(String informationConfidential) {
            this.informationConfidential = informationConfidential;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public int getMedicalCategoryId() {
            return medicalCategoryId;
        }

        public void setMedicalCategoryId(int medicalCategoryId) {
            this.medicalCategoryId = medicalCategoryId;
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

        public String getPlatformLevel() {
            return platformLevel;
        }

        public void setPlatformLevel(String platformLevel) {
            this.platformLevel = platformLevel;
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

        public int getViewHospitalFee() {
            return viewHospitalFee;
        }

        public void setViewHospitalFee(int viewHospitalFee) {
            this.viewHospitalFee = viewHospitalFee;
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
             * createDate : 2016-11-25T04:59:15.888Z
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

    public static class HealthReportBean {
        private String createDate;
        private int id;
        private int patientAge;
        private String patientName;
        private String patientSex;
        private int seeDoctorTemplateId;
        private String simpleIntroduction;
        private String type;
        private int userId;
        private String valid;

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

        public int getPatientAge() {
            return patientAge;
        }

        public void setPatientAge(int patientAge) {
            this.patientAge = patientAge;
        }

        public String getPatientName() {
            return patientName;
        }

        public void setPatientName(String patientName) {
            this.patientName = patientName;
        }

        public String getPatientSex() {
            return patientSex;
        }

        public void setPatientSex(String patientSex) {
            this.patientSex = patientSex;
        }

        public int getSeeDoctorTemplateId() {
            return seeDoctorTemplateId;
        }

        public void setSeeDoctorTemplateId(int seeDoctorTemplateId) {
            this.seeDoctorTemplateId = seeDoctorTemplateId;
        }

        public String getSimpleIntroduction() {
            return simpleIntroduction;
        }

        public void setSimpleIntroduction(String simpleIntroduction) {
            this.simpleIntroduction = simpleIntroduction;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getValid() {
            return valid;
        }

        public void setValid(String valid) {
            this.valid = valid;
        }
    }

    public static class UserInfoBean {
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
         * createDate : 2016-11-25T04:59:15.890Z
         * id : 0
         * name : string
         * permissions : [{"createDate":"2016-11-25T04:59:15.890Z","id":0,"name":"string","status":"USE"}]
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
             * createDate : 2016-11-25T04:59:15.890Z
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
}
