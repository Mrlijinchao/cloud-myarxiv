package com.lijinchao.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lijinchao.constant.MessageConstant;
import com.lijinchao.entity.Privilege;
import com.lijinchao.entity.Role;
import com.lijinchao.entity.User;
import com.lijinchao.entity.dto.RoleDTO;
import com.lijinchao.service.RolePrivilegeService;
import com.lijinchao.service.RoleService;
import com.lijinchao.service.UserRoleService;
import com.lijinchao.utils.BaseApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/role")
public class RoleController {

    @Resource
    RoleService roleService;

    @Resource
    UserRoleService userRoleService;

    @Resource
    RolePrivilegeService rolePrivilegeService;

    /**
     * 新增角色
     * @param roleDTO
     * @return
     */
    @PostMapping("")
    public BaseApiResult addRole(@RequestBody RoleDTO roleDTO) {
        try {
            roleService.addRole(roleDTO);
        } catch (Exception e) {
            log.error("saveRole fail", e);
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
        return BaseApiResult.success("新增角色成功");
    }

    /**
     * 查询角色
     * @param roleDTO
     * @return
     */
    @GetMapping("")
    public BaseApiResult queryRoles(RoleDTO roleDTO) {
        try {
            List<Role> roles = roleService.queryRoles(roleDTO);
            return BaseApiResult.success(roles);
        } catch (Exception e) {
            log.error("queryRoles fail", e);
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }

    /**
     * 删除角色
     * @param
     * @return
     */
    @DeleteMapping("")
    public BaseApiResult deleteRole(@RequestBody List<Long> ids) {
        try {
            roleService.deleteRole(ids);
        } catch (Exception e) {
            log.error("deleteRole fail", e);
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
        return BaseApiResult.success("删除成功！");
    }

    /**
     * 修改角色
     * @param role
     * @return
     */
    @PutMapping("")
    public BaseApiResult updateRole(@RequestBody Role role) {
        try {
            roleService.updateRole(role);
        } catch (Exception e) {
            log.error("updateRole fail", e);
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
        return BaseApiResult.success("修改成功！");
    }


    /**
     * 根据角色Id查询角色权限
     * @param roleId
     * @return
     */
    @GetMapping("/getPrivilegeByRoleId")
    public BaseApiResult getPrivilegeByRoleId(Long roleId) {
        try {
            List<Privilege> privileges = rolePrivilegeService.getPrivilegeByRoleId(roleId);
            return BaseApiResult.success(privileges);
        } catch (Exception e) {
            log.error("queryRolePrivilege fail", e);
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }

    /**
     * 删除角色用户
     * @param
     * @return
     */
    @DeleteMapping("/action/deleteUser")
    public BaseApiResult deleteRoleUser(@RequestParam Long roleId, @RequestParam Long userId) {
        try {
            userRoleService.deleteByUserIdAndRoleId(userId, roleId);
        } catch (Exception e) {
            log.error("deleteRole fail", e);
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
        return BaseApiResult.success("删除成功！");
    }

    /**
     * 删除角色权限
     * @param
     * @return
     */
    @DeleteMapping("/action/deletePrivilege")
    public BaseApiResult deleteRolePrivilege(@RequestParam Long roleId, @RequestParam Long privilegeId) {
        try {
            rolePrivilegeService.deleteByRoleIdAndPrivilegeId(roleId, privilegeId);
        } catch (Exception e) {
            log.error("deleteRole fail", e);
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
        return BaseApiResult.success("删除成功！");
    }

    /**
     * 分页多条件查询角色
     * @param roleName
     * @param roleCode
     * @param roleType
     * @param userName
     * @param userCode
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/conditional/query")
    public BaseApiResult pagePrivilege(
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) String roleCode,
            @RequestParam(required = false) String roleType,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String userCode,
            @RequestParam(required = false,defaultValue = "1") Integer pageNum,
            @RequestParam(required = false,defaultValue = "10") Integer pageSize
    ){
        try {
            Page<Role> rolePage = roleService.multiConditionalPageQuery(roleName, roleCode, roleType, userName,
                    userCode, pageNum, pageSize);
            return BaseApiResult.success(rolePage);
        } catch (Exception e) {
            log.error("query fail", e);
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }

    /**
     * 角色查询用户
     * @param roleId
     * @return
     */
    @GetMapping("/queryUser")
    public BaseApiResult queryUser(Long roleId){
        try{
            List<User> users=roleService.queryUser(roleId);
            return BaseApiResult.success(users);
        }catch(Exception e){
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }

    /**
     * 角色添加权限
     * @param roleId
     * @param privilegeId
     * @return
     */
    @PostMapping("/addPrivilege")
    public BaseApiResult addPrivilege(Long roleId,Long privilegeId){
        try{
            roleService.addPrivilege(roleId,privilegeId);
            return BaseApiResult.success("添加成功！");
        }catch(Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
    }
}
