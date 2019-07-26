package com.qingcheng.dao;

import com.alibaba.dubbo.config.annotation.Service;
import com.qingcheng.pojo.system.Role;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface RoleMapper extends Mapper<Role> {
    @Select("SELECT * FROM tb_role WHERE id in (SELECT role_id FROM tb_admin_role WHERE admin_id=#{id} )")
    public List<Role> findAdminIdforSoleId(@Param("id")Integer id);

}
