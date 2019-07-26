package com.qingcheng.controller.system;

import com.alibaba.dubbo.config.annotation.Reference;
import com.qingcheng.entity.PageResult;
import com.qingcheng.entity.Result;
import com.qingcheng.pojo.system.Admin;
import com.qingcheng.pojo.system.AdminRoleResult;
import com.qingcheng.pojo.system.Role;
import com.qingcheng.service.system.AdminService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Reference
    private AdminService adminService;

    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestBody Map<String,String> searchMap){
       adminService.updatePassword(searchMap.get("loginName"),searchMap.get("oldPassword"),searchMap.get("newPassword"));
        return new Result(0,"修改密码成功");
    }

    @GetMapping("/findAll")
    public List<Admin> findAll(){
        return adminService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult<Admin> findPage(int page, int size){
        return adminService.findPage(page, size);
    }

    @PostMapping("/findList")
    public List<Admin> findList(@RequestBody Map<String,Object> searchMap){
        return adminService.findList(searchMap);
    }

    @PostMapping("/findPage")
    public PageResult<Admin> findPage(@RequestBody Map<String,Object> searchMap,int page, int size){
        return  adminService.findPage(searchMap,page,size);
    }

    @GetMapping("/findById")
    public AdminRoleResult findById(Integer id){
        return adminService.findById(id);
    }


    @PostMapping("/add")
    public Result add(@RequestBody AdminRoleResult adminRoleResult){
        adminService.add(adminRoleResult);
        return new Result();
    }

    @PostMapping("/update")
    public Result  update(@RequestBody AdminRoleResult  adminRoleResult){
        adminService.update(adminRoleResult);
        return new Result();
    }

    @GetMapping("/delete")
    public Result delete(Integer id){
        adminService.delete(id);
        return new Result();
    }

}
