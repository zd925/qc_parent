package com.qingcheng.service.system;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.system.Role;
import com.qingcheng.pojo.system.RoleResourceResult;

import java.util.*;

/**
 * role业务逻辑层
 */
public interface RoleService {


    public List<Role> findAll();


    public PageResult<Role> findPage(int page, int size);


    public List<Role> findList(Map<String,Object> searchMap);


    public PageResult<Role> findPage(Map<String,Object> searchMap,int page, int size);


    public RoleResourceResult findById(Integer RoleId);

    public void add(Role role);


    public void update(RoleResourceResult roleResourceResult);


    public void delete(Integer id);

}
