package com.lijinchao.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lijinchao.constant.MessageConstant;
import com.lijinchao.entity.Privilege;
import com.lijinchao.entity.Role;
import com.lijinchao.entity.User;
import com.lijinchao.entity.UserPrivilege;
import com.lijinchao.entity.UserRole;
import com.lijinchao.entity.dto.RegisterDTO;
import com.lijinchao.entity.dto.UserDTO;
import com.lijinchao.enums.GlobalEnum;
import com.lijinchao.permission.Permission;
import com.lijinchao.permission.PermissionRoleEnum;
import com.lijinchao.service.LoginService;
import com.lijinchao.service.UserService;
import com.lijinchao.utils.BaseApiResult;
import com.lijinchao.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 用户模块
 * 关于用户的一些操作
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    UserService userService;

    @Resource
    RedisUtil redisUtil;

    @Autowired
    private LoginService loginService;

    /**
     * 获取当前用户信息
     *
     * @param request
     * @return
     */
    @GetMapping("/queryCurrentUser")
    public BaseApiResult queryCurrentUser(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        return BaseApiResult.success(redisUtil.getByToken(token));
    }

    /**
     * 获取当前用户的完整信息
     * @param request
     * @return
     */
    @GetMapping("/queryCompletionUser")
    public BaseApiResult queryCurrentUserCompletionInfo(HttpServletRequest request){
        String token = request.getHeader("authorization");
        User user = (User)redisUtil.getByToken(token);
        UserDTO completionUser = userService.getCompletionUser(user);
        return BaseApiResult.success(completionUser);
    }

//    /**
//     * 新增用户
//     *
//     * @param users
//     * @return
//     */
//    @PostMapping("")
//    public BaseApiResult addUser(@RequestBody List<User> users) {
//        try {
//            return userService.batchSaveUsers(users);
//        } catch (Exception e) {
//            log.error("addUser fail", e);
//            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, e.getMessage());
//        }
//    }

    /**
     * 用户注册
     * @param registerDTO
     * @return
     */
    @PostMapping("/register")
    public BaseApiResult userRegister(@RequestBody RegisterDTO registerDTO){
        try {
            User user = registerDTO.getUser();
            String code = (String)redisUtil.get(user.getEmail());
            if(ObjectUtils.isEmpty(code) || !code.equals(registerDTO.getVerificationCode())){
                return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE,"验证码错误，注册失败！请填写正确的验证码");
            }
            return userService.userRegister(user);
        } catch (Exception e) {
            log.error("userRegister fail", e);
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, e.getMessage());
        }
    }

    /**
     * 管理员删除用户
     *
     * @return
     */
    @Transactional
    @Permission(roleValue = {PermissionRoleEnum.ADMIN, PermissionRoleEnum.SUPERADMIN})
    @DeleteMapping("")
    public BaseApiResult delUser(@RequestParam List<Long> userIds) {
        try {
            return userService.delUsersById(userIds);
        } catch (Exception e) {
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
    }

    /**
     * 用户注销自己的账号
     *
     * @param request
     * @return
     */
    @DeleteMapping("/closeAccount")
    public BaseApiResult closeAccount(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        return userService.closeAccount(token);
    }

    @Permission(roleValue = {PermissionRoleEnum.ADMIN, PermissionRoleEnum.SUPERADMIN})
    @PostMapping("/frozenAccount")
    public BaseApiResult frozenAccount(@RequestBody User user){
        try {
            // statusCd为1000表示正常，为1100表示账号冻结
            boolean update = userService.update(new LambdaUpdateWrapper<User>()
                    .eq(User::getId, user.getId())
                    .set(User::getStatusCd, "1100"));
            if(update){
                return BaseApiResult.success();
            }else{
                return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
            }
        }catch(Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
    }

    /**
     * 恢复用户账号
     *
     * @param user
     * @return
     */
    @Transactional
    @Permission(roleValue = {PermissionRoleEnum.ADMIN, PermissionRoleEnum.SUPERADMIN})
    @PostMapping("/recoverAccount")
    public BaseApiResult recoverAccount(@RequestBody User user) {
        if (ObjectUtils.isEmpty(user.getId())) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.DATA_IS_NULL);
        }
        boolean b = userService.update(new LambdaUpdateWrapper<User>()
                        .eq(User::getId,user.getId())
                .set(User::getStatusCd, GlobalEnum.EFFECT.getCode()));
        if (b) {
            return BaseApiResult.success("用户账号已恢复");
        }
        return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
    }

    /**
     * 修改用户信息，只有管理员和本人才有权限修改
     *
     * @param user
     * @param request
     * @return
     */
    @PutMapping("/modify")
    public BaseApiResult modifyUser(@RequestBody User user, HttpServletRequest request) {
        String token = request.getHeader("authorization");
        return userService.modifyUserInfo(user, token);
    }

    /**
     * 修改用户密码
     *
     * @param userPasswordInfo
     * @return
     */
    @PutMapping("/modifyPassword")
    public BaseApiResult modifyPassword(@RequestBody Map<String, String> userPasswordInfo) {
        Long userId = Long.parseLong(userPasswordInfo.get("userId"));
        String oldPassword = userPasswordInfo.get("oldPassword");
        String newPassword = userPasswordInfo.get("newPassword");
        if (ObjectUtils.isEmpty(userId) || ObjectUtils.isEmpty(oldPassword)
                || ObjectUtils.isEmpty(newPassword)) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.DATA_IS_NULL);
        }
        return userService.modifyPassword(userId, oldPassword, newPassword);
    }


    /**
     * 多条件分页查询用户信息
     *
     * @param userDTO
     * @return
     */
    @GetMapping("/query")
    public BaseApiResult pageQuery(UserDTO userDTO) {
        try {
            Page<User> userPage = userService.multiConditionalPageQuery(userDTO);
            return BaseApiResult.success(userPage);
        } catch (Exception e) {
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
    }

    /**
     * 用户查询角色
     *
     * @param userId
     * @return
     */
    @GetMapping("/queryRole")
    public BaseApiResult queryRole(Long userId) {
        try {
            List<Role> roles = userService.queryRole(userId);
            return BaseApiResult.success(roles);
        } catch (Exception e) {
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
    }

    /**
     * 根据用户Id查询用户权限
     *
     * @param userId
     * @return
     */
    @GetMapping("/queryPrivilege")
    public BaseApiResult queryPrivilege(Long userId) {
        try {
            List<Privilege> privileges = userService.getUserPrivilegesByIds(Collections.singletonList(userId));
            return BaseApiResult.success(privileges);
        } catch (Exception e) {
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
    }

    /**
     * 检查用户账号是否存在
     *
     * @param code
     * @return
     */
    @GetMapping("/checkUserCodeIsExist")
    public BaseApiResult checkUserCodeIsExist(@RequestParam String code) {
        if (ObjectUtils.isEmpty(code)) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.DATA_IS_NULL);
        }
        List<User> list = userService.list(new LambdaQueryWrapper<User>().eq(User::getCode, code));
        if (!CollectionUtils.isEmpty(list)) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, "此账号已存在！");
        }
        return BaseApiResult.success("此账号可用");
    }

    /**
     * 检查用户邮箱是否已经存在
     *
     * @param email
     * @return
     */
    @GetMapping("/checkUserEmailIsExist")
    public BaseApiResult checkUserEmailIsExist(@RequestParam String email) {
        if (ObjectUtils.isEmpty(email)) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.DATA_IS_NULL);
        }
        List<User> list = userService.list(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
        if (!CollectionUtils.isEmpty(list)) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, "此邮箱已存在！");
        }
        return BaseApiResult.success("此邮箱可用");
    }

    /**
     * 检查手机号是否已经存在
     *
     * @param phone
     * @return
     */
    @GetMapping("/checkUserPhoneIsExist")
    public BaseApiResult checkUserPhoneIsExist(@RequestParam String phone) {
        if (ObjectUtils.isEmpty(phone)) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, MessageConstant.DATA_IS_NULL);
        }
        List<User> list = userService.list(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if (!CollectionUtils.isEmpty(list)) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, "此手机号已存在！");
        }
        return BaseApiResult.success("此手机号可用");
    }

    /**
     * 用户添加权限
     * @param userPrivilege
     * @return
     */
    @PostMapping("/addPrivilege")
    public BaseApiResult addPrivilege(@RequestBody UserPrivilege userPrivilege){
        try{
            userService.addPrivilege(userPrivilege.getUserId(),userPrivilege.getPrivilegeId());
            return BaseApiResult.success("添加成功！");
        }catch(Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
    }

    /**
     * 用户添加角色
     * @param userRole
     * @return
     */
    @PostMapping("/addRole")
    public BaseApiResult addRole(@RequestBody UserRole userRole){
        try{
            userService.addRole(userRole.getUserId(),userRole.getRoleId());
            return BaseApiResult.success("添加成功！");
        }catch(Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
    }

    /**
     * 用户移除限
     * @param userId
     * @param privilegeId
     * @return
     */
    @DeleteMapping("/removePrivilege")
    public BaseApiResult removePrivilege(Long userId,Long privilegeId){
        try{
            userService.deletePrivilege(userId,privilegeId);
            return BaseApiResult.success("移除成功！");
        }catch(Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
    }

    /**
     * 用户移除角色
     * @param userId
     * @param roleId
     * @return
     */
    @DeleteMapping("/removeRole")
    public BaseApiResult removeRole(Long userId,Long roleId){
        try{
            userService.deleteRole(userId,roleId);
            return BaseApiResult.success("移除成功！");
        }catch(Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
    }

    @GetMapping("/queryAllUser")
    public BaseApiResult queryAllUser(){
        try {
            List<User> allUser = userService.getAllUser();
            return BaseApiResult.success(allUser);
        }catch(Exception e){
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE, MessageConstant.OPERATE_FAILED);
        }
    }



}
