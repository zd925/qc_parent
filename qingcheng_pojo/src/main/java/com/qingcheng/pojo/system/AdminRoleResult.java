package com.qingcheng.pojo.system;

import java.io.Serializable;
import java.util.List;

/**
 * @author Super_DH
 * 没有单枪匹马的成功,因为有人替你负重前行
 * 浮世三千 我爱有三 日月为卿 日为朝 月为暮 卿为朝朝暮暮
 * @version java.11
 * @date: 2019-07-01    15:04
 */
public class AdminRoleResult implements Serializable {

    private List<Role> roles;
    private Admin admin;

    @Override
    public String toString() {
        return "AdminRoleResult{" +
                "roles=" + roles +
                ", admin=" + admin +
                '}';
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }
}
