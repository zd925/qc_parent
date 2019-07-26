package com.qingcheng.pojo.system;

import com.sun.javafx.beans.IDProperty;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Super_DH
 * 没有单枪匹马的成功,因为有人替你负重前行
 * 浮世三千 我爱有三 日月为卿 日为朝 月为暮 卿为朝朝暮暮
 * @version java.11
 * @date: 2019-07-01    14:43
 */
@Table(name = "tb_role_resource")
public class RoleResource implements Serializable {
    @Id
    private Integer roleId;
    @Id
    private Integer resourceId;

    @Override
    public String toString() {
        return "RoleResource{" +
                "roleId=" + roleId +
                ", resourceId=" + resourceId +
                '}';
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }
}
