package com.qingcheng.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.qingcheng.dao.AdminMapper;
import com.qingcheng.dao.AdminRoleMapper;
import com.qingcheng.dao.RoleMapper;
import com.qingcheng.entity.PageResult;
import com.qingcheng.pojo.system.Admin;
import com.qingcheng.pojo.system.AdminRole;
import com.qingcheng.pojo.system.AdminRoleResult;
import com.qingcheng.pojo.system.Role;
import com.qingcheng.service.system.AdminService;
import com.qingcheng.util.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service(interfaceClass = AdminService.class)
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private AdminRoleMapper adminRoleMapper;
    @Autowired
    private RoleMapper roleMapper;

    /**
     * 返回全部记录
     *
     * @return
     */
    public List<Admin> findAll() {
        return adminMapper.selectAll();
    }

    /**
     * 分页查询
     *
     * @param page 页码
     * @param size 每页记录数
     * @return 分页结果
     */
    public PageResult<Admin> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        Page<Admin> admins = (Page<Admin>) adminMapper.selectAll();
        return new PageResult<Admin>(admins.getTotal(), admins.getResult());
    }

    /**
     * 条件查询
     *
     * @param searchMap 查询条件
     * @return
     */
    public List<Admin> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return adminMapper.selectByExample(example);
    }

    /**
     * 分页+条件查询
     *
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    public PageResult<Admin> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        Page<Admin> admins = (Page<Admin>) adminMapper.selectByExample(example);
        return new PageResult<Admin>(admins.getTotal(), admins.getResult());
    }

    /**
     * 根据Id查询
     *
     * @param id
     * @return
     */
    public AdminRoleResult findById(Integer id) {
        // new 一个对象用于存放数据
        AdminRoleResult adminRoleResult = new AdminRoleResult();
        Admin admin = adminMapper.selectByPrimaryKey(id);
        // 复杂逻辑能用数据库查询直接查询出来方便
        List<Role> roleList = roleMapper.findAdminIdforSoleId(id);
        // 把得到数据直接存入adminRoleResult对象中
        adminRoleResult.setAdmin(admin);
        adminRoleResult.setRoles(roleList);
        return adminRoleResult;
    }

    /**
     * 新增
     *
     * @param
     */
    public void add(AdminRoleResult adminRoleResult) {

        Admin admin = adminRoleResult.getAdmin();
        admin.setPassword(BCrypt.hashpw(admin.getPassword(), BCrypt.gensalt()));
        adminMapper.insert(admin);
        List<Role> roles = adminRoleResult.getRoles();
        insertAdminRoleMapper(roles, admin.getId());


    }

    private void insertAdminRoleMapper(List<Role> roles, Integer id) {
        for (Role role : roles) {
            AdminRole adminRole = new AdminRole();
            adminRole.setAdminId(id);
            adminRole.setRoleId(role.getId());
            adminRoleMapper.insert(adminRole);

        }
    }

    /**
     * 修改
     *
     * @param
     */
    @Transactional
    public void update(AdminRoleResult adminRoleResult) {
        Admin admin = adminRoleResult.getAdmin();
        adminMapper.updateByPrimaryKeySelective(admin);
        Example example = new Example(AdminRole.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("AdminId", admin.getId());
        adminRoleMapper.deleteByExample(example);

        List<Role> roles = adminRoleResult.getRoles();
        insertAdminRoleMapper(roles, admin.getId());
    }

    /**
     * 删除
     *
     * @param id
     */
    public void delete(Integer id) {
        adminMapper.deleteByPrimaryKey(id);
    }

    /**
     * 修改密码
     *
     * @param username    用户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    @Override
    @Transactional
    public void updatePassword(String username, String oldPassword, String newPassword) {
        Example example = new Example(Admin.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", username);
        List<Admin> list = adminMapper.selectByExample(example);
        boolean checkpw = BCrypt.checkpw(oldPassword, list.get(0).getPassword());

        if (!checkpw) {
            throw new RuntimeException("旧密码错误");
        }
        Admin admin = new Admin();
        admin.setLoginName(username);
        admin.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        adminMapper.updateByPrimaryKeySelective(admin);
    }


    /**
     * 构建查询条件
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Admin.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 用户名
            if (searchMap.get("loginName") != null && !"".equals(searchMap.get("loginName"))) {
                criteria.andEqualTo("loginName", searchMap.get("loginName"));
            }
            // 密码
            if (searchMap.get("password") != null && !"".equals(searchMap.get("password"))) {
                criteria.andEqualTo("password", searchMap.get("password"));
            }
            // 状态
            if (searchMap.get("status") != null && !"".equals(searchMap.get("status"))) {
                criteria.andEqualTo("status", searchMap.get("status"));
            }

            // id
            if (searchMap.get("id") != null) {
                criteria.andEqualTo("id", searchMap.get("id"));
            }

        }
        return example;
    }

}
