package com.shkjs.doctor.bean;

import java.util.List;

/**
 * Created by Shuwen on 2016/10/9.
 */

public class InComeDetailBean {

    /**
     * balance : 0
     * createDate : 2016-11-11T08:15:42.125Z
     * freeze : 0
     * id : 0
     * password : string
     * totalExpend : 0
     * totalIncome : 0
     * user : {"createDate":"2016-11-11T08:15:42.125Z","id":0,"password":"string","roles":[{"createDate":"2016-11-11T08:15:42.125Z","id":0,"name":"string","permissions":[{"createDate":"2016-11-11T08:15:42.125Z","id":0,"name":"string","status":"USE"}],"status":"USE"}],"userName":"string"}
     * version : 0
     */

    private AccountBean account;
    /**
     * account : {"balance":0,"createDate":"2016-11-11T08:15:42.125Z","freeze":0,"id":0,"password":"string","totalExpend":0,"totalIncome":0,"user":{"createDate":"2016-11-11T08:15:42.125Z","id":0,"password":"string","roles":[{"createDate":"2016-11-11T08:15:42.125Z","id":0,"name":"string","permissions":[{"createDate":"2016-11-11T08:15:42.125Z","id":0,"name":"string","status":"USE"}],"status":"USE"}],"userName":"string"},"version":0}
     * createDate : 2016-11-11T08:15:42.125Z
     * id : 0
     * mark : string
     * money : 0
     * orderId : 0
     * type : RECHARGE
     */

    private String createDate;
    private int id;
    private String mark;
    private int money;
    private int orderId;
    private String type;

    public AccountBean getAccount() {
        return account;
    }

    public void setAccount(AccountBean account) {
        this.account = account;
    }

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

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static class AccountBean {
        private int balance;
        private String createDate;
        private int freeze;
        private int id;
        private String password;
        private int totalExpend;
        private int totalIncome;
        /**
         * createDate : 2016-11-11T08:15:42.125Z
         * id : 0
         * password : string
         * roles : [{"createDate":"2016-11-11T08:15:42.125Z","id":0,"name":"string","permissions":[{"createDate":"2016-11-11T08:15:42.125Z","id":0,"name":"string","status":"USE"}],"status":"USE"}]
         * userName : string
         */

        private UserBean user;
        private int version;

        public int getBalance() {
            return balance;
        }

        public void setBalance(int balance) {
            this.balance = balance;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
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

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
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
            private int id;
            private String password;
            private String userName;
            /**
             * createDate : 2016-11-11T08:15:42.125Z
             * id : 0
             * name : string
             * permissions : [{"createDate":"2016-11-11T08:15:42.125Z","id":0,"name":"string","status":"USE"}]
             * status : USE
             */

            private List<RolesBean> roles;

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

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
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
                private int id;
                private String name;
                private String status;
                /**
                 * createDate : 2016-11-11T08:15:42.125Z
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
}
