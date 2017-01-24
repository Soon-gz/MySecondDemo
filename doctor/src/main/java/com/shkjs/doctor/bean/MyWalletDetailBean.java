package com.shkjs.doctor.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/10/14.
 */

public class MyWalletDetailBean {

    /**
     * balance : 0
     * createDate : 2016-10-14T07:14:17.045Z
     * dr : string
     * freeze : 0
     * id : 0
     * totalExpend : 0
     * totalIncome : 0
     * updateDate : 2016-10-14T07:14:17.045Z
     * user : {"createDate":"2016-10-14T07:14:17.045Z","dr":"string","id":0,"password":"string","roles":[{"createDate":"2016-10-14T07:14:17.045Z","dr":"string","id":0,"name":"string","permissions":[{"createDate":"2016-10-14T07:14:17.045Z","dr":"string","id":0,"name":"string","status":"USE","updateDate":"2016-10-14T07:14:17.045Z"}],"status":"USE","updateDate":"2016-10-14T07:14:17.045Z"}],"updateDate":"2016-10-14T07:14:17.045Z","userName":"string"}
     * version : 0
     */

    private long balance;
    private String createDate;
    private String dr;
    private int freeze;
    private int id;
    private int totalExpend;
    private int totalIncome;
    private String updateDate;
    /**
     * createDate : 2016-10-14T07:14:17.045Z
     * dr : string
     * id : 0
     * password : string
     * roles : [{"createDate":"2016-10-14T07:14:17.045Z","dr":"string","id":0,"name":"string","permissions":[{"createDate":"2016-10-14T07:14:17.045Z","dr":"string","id":0,"name":"string","status":"USE","updateDate":"2016-10-14T07:14:17.045Z"}],"status":"USE","updateDate":"2016-10-14T07:14:17.045Z"}]
     * updateDate : 2016-10-14T07:14:17.045Z
     * userName : string
     */

    private UserBean user;
    private int version;

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getDr() {
        return dr;
    }

    public void setDr(String dr) {
        this.dr = dr;
    }

    public int getFreeze() {
        return freeze;
    }

    public void setFreeze(int freeze) {
        this.freeze = freeze;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTotalExpend() {
        return totalExpend;
    }

    public void setTotalExpend(int totalExpend) {
        this.totalExpend = totalExpend;
    }

    public int getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(int totalIncome) {
        this.totalIncome = totalIncome;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public static class UserBean {
        private String createDate;
        private String dr;
        private int id;
        private String password;
        private String updateDate;
        private String userName;
        /**
         * createDate : 2016-10-14T07:14:17.045Z
         * dr : string
         * id : 0
         * name : string
         * permissions : [{"createDate":"2016-10-14T07:14:17.045Z","dr":"string","id":0,"name":"string","status":"USE","updateDate":"2016-10-14T07:14:17.045Z"}]
         * status : USE
         * updateDate : 2016-10-14T07:14:17.045Z
         */

        private List<RolesBean> roles;

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getDr() {
            return dr;
        }

        public void setDr(String dr) {
            this.dr = dr;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUpdateDate() {
            return updateDate;
        }

        public void setUpdateDate(String updateDate) {
            this.updateDate = updateDate;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public List<RolesBean> getRoles() {
            return roles;
        }

        public void setRoles(List<RolesBean> roles) {
            this.roles = roles;
        }

        public static class RolesBean {
            private String createDate;
            private String dr;
            private int id;
            private String name;
            private String status;
            private String updateDate;
            /**
             * createDate : 2016-10-14T07:14:17.045Z
             * dr : string
             * id : 0
             * name : string
             * status : USE
             * updateDate : 2016-10-14T07:14:17.045Z
             */

            private List<PermissionsBean> permissions;

            public String getCreateDate() {
                return createDate;
            }

            public void setCreateDate(String createDate) {
                this.createDate = createDate;
            }

            public String getDr() {
                return dr;
            }

            public void setDr(String dr) {
                this.dr = dr;
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

            public String getUpdateDate() {
                return updateDate;
            }

            public void setUpdateDate(String updateDate) {
                this.updateDate = updateDate;
            }

            public List<PermissionsBean> getPermissions() {
                return permissions;
            }

            public void setPermissions(List<PermissionsBean> permissions) {
                this.permissions = permissions;
            }

            public static class PermissionsBean {
                private String createDate;
                private String dr;
                private int id;
                private String name;
                private String status;
                private String updateDate;

                public String getCreateDate() {
                    return createDate;
                }

                public void setCreateDate(String createDate) {
                    this.createDate = createDate;
                }

                public String getDr() {
                    return dr;
                }

                public void setDr(String dr) {
                    this.dr = dr;
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

                public String getUpdateDate() {
                    return updateDate;
                }

                public void setUpdateDate(String updateDate) {
                    this.updateDate = updateDate;
                }
            }
        }
    }

    @Override
    public String toString() {
        return "MyWalletDetailBean{" +
                "balance=" + balance +
                ", createDate='" + createDate + '\'' +
                ", dr='" + dr + '\'' +
                ", freeze=" + freeze +
                ", id=" + id +
                ", totalExpend=" + totalExpend +
                ", totalIncome=" + totalIncome +
                ", updateDate='" + updateDate + '\'' +
                ", user=" + user +
                ", version=" + version +
                '}';
    }
}
