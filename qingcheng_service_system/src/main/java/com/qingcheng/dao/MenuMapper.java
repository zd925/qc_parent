package com.qingcheng.dao;

import com.qingcheng.pojo.system.Menu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface MenuMapper extends Mapper<Menu> {

    /**
     * 根据当前登录的用户 查询菜单列表
     * @param
     * @return
     */
    @Select("SELECT id,name,icon,url as url,parent_id as parentId FROM tb_menu WHERE id IN( " +
            "SELECT menu_id FROM tb_resource_menu WHERE resource_id IN ( " +
            "SELECT resource_id FROM tb_role_resource WHERE role_id IN ( " +
            "SELECT role_id FROM tb_admin_role WHERE admin_id IN ( " +
            "SELECT id FROM tb_admin WHERE login_name=#{loginName} " +
            ") " +
            ") " +
            ") " +
            ") UNION " +
            "SELECT id,name,icon,url as url,parent_id as parentId FROM tb_menu WHERE id IN( " +
            "SELECT parent_id FROM tb_menu WHERE id IN( " +
            "SELECT menu_id FROM tb_resource_menu WHERE resource_id IN ( " +
            "SELECT resource_id FROM tb_role_resource WHERE role_id IN ( " +
            "SELECT role_id FROM tb_admin_role WHERE admin_id IN ( " +
            "SELECT id FROM tb_admin WHERE login_name=#{loginName} " +
            ") " +
            ") " +
            ") " +
            ") " +
            ") UNION " +
            "SELECT id,name,icon,url as url,parent_id as parentId FROM tb_menu WHERE id IN ( " +
            "SELECT parent_id FROM tb_menu WHERE id IN( " +
            "SELECT parent_id FROM tb_menu WHERE id IN( " +
            "SELECT menu_id FROM tb_resource_menu WHERE resource_id IN ( " +
            "SELECT resource_id FROM tb_role_resource WHERE role_id IN ( " +
            "SELECT role_id FROM tb_admin_role WHERE admin_id IN ( " +
            "SELECT id FROM tb_admin WHERE login_name=#{loginName} " +
            ") " +
            ") " +
            ") " +
            ") " +
            ") " +
            ")")
    List<Menu> findMenusByLoginName(@Param("loginName") String loginName);

}




