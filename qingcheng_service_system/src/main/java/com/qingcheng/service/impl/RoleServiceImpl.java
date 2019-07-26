package com.qingcheng.service.impl;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.ResourceMapper;
import com.qingcheng.dao.RoleMapper;
import com.qingcheng.dao.RoleResourceMapper;
import com.qingcheng.dao.RoleResourceResultMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.system.Resource;
import com.qingcheng.pojo.system.Role;
import com.qingcheng.pojo.system.RoleResource;
import com.qingcheng.pojo.system.RoleResourceResult;
import com.qingcheng.service.system.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired
    private  RoleResourceMapper roleResourceMapper;

    /**
     * 返回全部记录
     * @return
     */
    public List<Role> findAll() {
        return roleMapper.selectAll();
    }

    /**
     * 分页查询
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Role> findPage(int page, int size) {
        PageHelper.startPage(page,size);
        Page<Role> roles = (Page<Role>) roleMapper.selectAll();
        return new PageResult<Role>(roles.getTotal(),roles.getResult());
    }

    /**
     * 条件查询
     * @param searchMap 查询条件
     * @return
     */
    public List<Role> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return roleMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Role> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        Page<Role> roles = (Page<Role>) roleMapper.selectByExample(example);
        return new PageResult<Role>(roles.getTotal(),roles.getResult());
    }

    /**
     * 根据Id查询
     * @param
     * @return
     */
    public RoleResourceResult findById(Integer roleId) {
        // 创建对象
        RoleResourceResult roleResourceResult = new RoleResourceResult();
        Role role = roleMapper.selectByPrimaryKey(roleId);
        // 储存role对象
        roleResourceResult.setRole(role);
        roleResourceResult.setResourceList(resourceMapper.findRoleIdforResource(roleId));
        return roleResourceResult;
    }

    /**
     * 新增
     * @param role
     */
    public void add(Role role) {
        roleMapper.insert(role);
    }

    /**
     * 修改
     * @param
     */
    public void update(RoleResourceResult roleResourceResult) {
       // roleId提取
        Integer roleId = roleResourceResult.getRole().getId();
        // 保存role
        roleMapper.insert(roleResourceResult.getRole());
        // 删除roleResource关联,中间表,在曾中间表
        Example example =new Example(RoleResource.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("roleId",roleId);
        roleResourceMapper.deleteByExample(example);
        List<Resource> resourceList = roleResourceResult.getResourceList();
        for (Resource resource : resourceList) {

            RoleResource roleResource = new RoleResource();
            roleResource.setRoleId(roleId);
            roleResource.setResourceId(resource.getId());
            roleResourceMapper.insert(roleResource);
        }

    }

    /**
     *  删除
     * @param id
     */
    public void delete(Integer id) {
        roleMapper.deleteByPrimaryKey(id);
    }

    /**
     * 构建查询条件
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Role.class);
        Example.Criteria criteria = example.createCriteria();
        if(searchMap!=null){
            // 角色名称
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }

            // ID
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }

        }
        return example;
    }

}
