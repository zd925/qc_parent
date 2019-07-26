package com.qingcheng.pojo.system;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

/**
 * @author Super_DH
 * 没有单枪匹马的成功,因为有人替你负重前行
 * 浮世三千 我爱有三 日月为卿 日为朝 月为暮 卿为朝朝暮暮
 * @version java.11
 * @date: 2019-07-01    19:45
 */
@Table(name = "tb_role_resource")
public class RoleResourceResult implements Serializable {

    @Id
    private Role role;
    @Id
    private List<Resource> resourceList;

    @Override
    public String toString() {
        return "RoleResourceResult{" +
                "role=" + role +
                ", resourceList=" + resourceList +
                '}';
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Resource> getResourceList() {
        return resourceList;
    }

    public void setResourceList(List<Resource> resourceList) {
        this.resourceList = resourceList;
    }
}
