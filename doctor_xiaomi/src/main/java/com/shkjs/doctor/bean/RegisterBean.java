package com.shkjs.doctor.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/11/8.
 */

public class RegisterBean {

    /**
     * createDate : 2016-11-08T01:42:56.076Z
     * dr : string
     * id : 0
     * password : string
     * roles : [{"createDate":"2016-11-08T01:42:56.076Z","dr":"string","id":0,"name":"string","permissions":[{"createDate":"2016-11-08T01:42:56.076Z","dr":"string","id":0,"name":"string","status":"USE","updateDate":"2016-11-08T01:42:56.076Z"}],"status":"USE","updateDate":"2016-11-08T01:42:56.076Z"}]
     * updateDate : 2016-11-08T01:42:56.076Z
     * userName : string
     */

    private String createDate;
    private String dr;
    private int id;
    private String password;
    private String updateDate;
    private String userName;
    /**
     * createDate : 2016-11-08T01:42:56.076Z
     * dr : string
     * id : 0
     * name : string
     * permissions : [{"createDate":"2016-11-08T01:42:56.076Z","dr":"string","id":0,"name":"string","status":"USE","updateDate":"2016-11-08T01:42:56.076Z"}]
     * status : USE
     * updateDate : 2016-11-08T01:42:56.076Z
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
         * createDate : 2016-11-08T01:42:56.076Z
         * dr : string
         * id : 0
         * name : string
         * status : USE
         * updateDate : 2016-11-08T01:42:56.076Z
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
