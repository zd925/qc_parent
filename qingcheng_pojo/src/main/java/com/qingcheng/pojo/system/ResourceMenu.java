package com.qingcheng.pojo.system;

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
@Table(name = "tb_resource_menu")
public class ResourceMenu implements Serializable {
    @Id
    private Integer resourceId;
    @Id
    private Integer menuId;

    @Override
    public String toString() {
        return "ResourceMenu{" +
                "resourceId=" + resourceId +
                ", menuId=" + menuId +
                '}';
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }
}
