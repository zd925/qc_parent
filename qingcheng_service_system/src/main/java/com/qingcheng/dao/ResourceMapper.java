package com.qingcheng.dao;

import com.qingcheng.pojo.system.Resource;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ResourceMapper extends Mapper<Resource> {

    // 根据中间表查询role=>Resource的数据
    @Select("SELECT id , res_key as resKey ,res_name as resName , parent_id as parentId FROM tb_resource WHERE id in (SELECT resource_id as id FROM tb_role_resource WHERE role_id=#{id} )")
    public List<Resource> findRoleIdforResource(@Param("id") Integer id);


    /**
     * 根据用户名查询资源权限列表
     */
    @Select("SELECT res_key resKey FROM tb_resource WHERE id IN ( " +
            "SELECT resource_id FROM tb_role_resource WHERE role_id IN ( " +
            "SELECT role_id FROM tb_admin_role WHERE admin_id IN ( " +
            "SELECT id FROM tb_admin WHERE login_name=#{loginName} " +
            ") " +
            ") " +
            ")")
    List<String> findResKeyByLoginName(@Param("loginName") String loginName);

}
