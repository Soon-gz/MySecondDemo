package com.shkjs.patient.bean;

import com.shkjs.patient.base.BaseModel;
import com.shkjs.patient.data.em.UseStatus;

import java.util.List;

public class Role extends BaseModel {
    /**
     *
     */
    private static final long serialVersionUID = -1050926956961974486L;

    private Long id;

    private String name;

    /**
     * 使用状态
     */
    private UseStatus status;

    /**
     * 权限列表
     */
    private List<Permission> permissions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public UseStatus getStatus() {
        return status;
    }

    public void setStatus(UseStatus status) {
        this.status = status;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Role) {
            Role role = (Role) obj;
            if (role.getId().equals(this.id) || role.getName().equals(this.getName())) {
                return true;
            }
            return false;
        }
        return super.equals(obj);
    }

    /**
     * 得到一个用户角色
     *
     * @return
     */
    public static Role newUserRole() {
        Role role = new Role();
        role.setId(1l);
        return role;
    }


    /**
     * 得到一个医生角色
     *
     * @return
     */
    public static Role newDoctorRole() {
        Role role = new Role();
        role.setId(2l);
        return role;
    }


    /**
     * 得到一个管理员角色
     *
     * @return
     */
    public static Role newAdminRole() {
        Role role = new Role();
        role.setId(3l);
        return role;
    }

    /**
     * @return
     * @author ZHANGYUKUN
     */
    public static Role newRole(String roleName) {
        Role role = new Role();

        switch (roleName) {
            case "admin":
                role.setId(3l);
                break;
            case "doctor":
                role.setId(2l);
                break;
            default:
                role.setId(1l);
                break;
        }

        return role;
    }

}