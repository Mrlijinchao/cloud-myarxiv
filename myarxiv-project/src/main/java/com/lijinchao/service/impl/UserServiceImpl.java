package com.lijinchao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lijinchao.constant.MessageConstant;
import com.lijinchao.entity.*;
import com.lijinchao.entity.dto.UserDTO;
import com.lijinchao.enums.GlobalEnum;
import com.lijinchao.mapper.*;
import com.lijinchao.permission.PermissionRoleEnum;
import com.lijinchao.service.CategoryService;
import com.lijinchao.service.SubjectService;
import com.lijinchao.service.UserService;
import com.lijinchao.utils.BaseApiResult;
import com.lijinchao.utils.BeanUtilCopy;
import com.lijinchao.utils.RedisUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* @author 时之始
* @description 针对表【user】的数据库操作Service实现
* @createDate 2023-12-27 15:26:43
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    UserRoleMapper userRoleMapper;

    @Resource
    RolePrivilegeMapper rolePrivilegeMapper;

    @Resource
    PrivilegeMapper privilegeMapper;

    @Resource
    UserPrivilegeMapper userPrivilegeMapper;

    @Resource
    RoleMapper roleMapper;

    @Resource
    RedisUtil redisUtil;

    @Resource
    UserMapper userMapper;

    @Resource
    SubjectService subjectService;

    @Resource
    CategoryService categoryService;


    @Override
    public BaseApiResult userRegister(User user) throws Exception {
        //根据code和email查询用户是否存在，存在则抛出异常
        List<User> userList = this.queryUserByCode(user.getCode());
        verifyWhetherItExists(userList);
        userList = this.list(new LambdaQueryWrapper<User>().eq(User::getEmail,user.getEmail()));
        if(!CollectionUtils.isEmpty(userList)){
            throw new Exception("邮箱已存在,请更换后再次尝试");
        }

        user.setStatusCd(GlobalEnum.EFFECT.getCode());
        this.save(user);

        //往用户角色表新增一条记录(普通用户)
        UserRole userRole = new UserRole();
        userRole.setRoleId(Long.parseLong(GlobalEnum.ORDINARY_USER.getCode()));
        userRole.setUserId(user.getId());
        userRole.setStatusCd(GlobalEnum.EFFECT.getCode());
        userRoleMapper.insert(userRole);

        return BaseApiResult.success("注册成功！");
    }

    /**
     * 校验是否已存在用户
     *
     * @param users
     * @return
     */
    private void verifyWhetherItExists(List<User> users) throws Exception {
        if (!CollectionUtils.isEmpty(users)) {
            User user = users.get(0);
            //曾经注册过，且生效，那么抛出异常
            if (GlobalEnum.EFFECT.getCode().equals(user.getStatusCd())) {
                throw new Exception("用户已经存在");
            } else {
                //曾经注册过，且无效则修改
                user.setStatusCd(GlobalEnum.EFFECT.getCode());
                //修改
                this.updateById(user);
            }
        }
    }

    /**
     * 根据账号查询用户
     *
     * @param userCode
     * @return
     */
    private List<User> queryUserByCode(String userCode) {
        return this.list(new LambdaQueryWrapper<User>().eq(User::getCode, userCode));
    }

    /**
     * 根据用户Id查询用户所有的角色信息
     * @param userId
     * @return
     */
    @Override
    public List<Role> queryRole(Long userId) {
        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<UserRole> userRoles=userRoleMapper.selectList(queryWrapper);
        List<Long> roleIds=userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        if(roleIds.isEmpty()) {
            return new ArrayList<>();
        }
        return roleMapper.selectBatchIds(roleIds);
    }

    /**
     * 查找用户权限
     * @param user
     * @param permissions
     * @return
     */
    @Override
    public Boolean checkPermissionForUser(User user, List<String> permissions) {
        if(user.getId() ==null){
            return false;
        }

        // 如果是管理员直接放行
        if(isAdmin(user.getId())){
            return true;
        }

        List<Privilege> privileges= getUserPrivilegesByIds(Collections.singletonList(user.getId()));
        Set<String> privilegeCodes = privileges.stream().map(Privilege::getCode).collect(Collectors.toSet());
        if(CollectionUtils.isEmpty(privilegeCodes)){
            return false;
        }
        //只要用户拥有某一个权限就放行
        for(String code : permissions){
            if (privilegeCodes.contains(code)){
                return true;
            }
        }

        return false;
    }

    /**
     * 检查是否是管理员
     * @param userId
     * @return
     */
    @Override
    public Boolean isAdmin(Long userId){
        if(userId == null){
            return false;
        }

        List<UserRole> userRoles = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId));
        if(CollectionUtils.isEmpty(userRoles)){
            return false;
        }
        Set<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        List<Role> roles = roleMapper.selectBatchIds(roleIds);
        Set<String> roleCodes = roles.stream().map(Role::getCode).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(roleCodes)){
            return false;
        }

        if(roleCodes.contains(PermissionRoleEnum.ADMIN.getCode())
                || roleCodes.contains(PermissionRoleEnum.SUPERADMIN.getCode())){
            return true;
        }

        return false;
    }


    /**
     * 根据用户Id查询所有用户权限
     * @param userIds
     * @return
     */
    @Override
    public List<Privilege> getUserPrivilegesByIds(List<Long> userIds){
        if(CollectionUtils.isEmpty(userIds)){
            return new ArrayList<>();
        }
        Set<Long> privilegeIds = new HashSet<>();
        Set<Long> roleIds = new HashSet<>();
        // 先查询与用户关联的角色权限
        List<UserRole> userRoles = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().in(UserRole::getUserId, userIds).ne(UserRole::getStatusCd, GlobalEnum.INVALID));
        if(!CollectionUtils.isEmpty(userRoles)){
            Set<Long> ids = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
            roleIds.addAll(ids);
        }
        if(!CollectionUtils.isEmpty(roleIds)){
            List<RolePrivilege> rolePrivileges = rolePrivilegeMapper.selectList(new LambdaQueryWrapper<RolePrivilege>()
                    .in(RolePrivilege::getRoleId, roleIds));
            if (!CollectionUtils.isEmpty(rolePrivileges)){
                Set<Long> ids = rolePrivileges.stream().map(RolePrivilege::getPrivilegeId).collect(Collectors.toSet());
                privilegeIds.addAll(ids);
            }
        }

        // 再查询与用户自己直接关联的权限
        List<UserPrivilege> userPrivileges = userPrivilegeMapper.selectList(new LambdaQueryWrapper<UserPrivilege>()
                .in(UserPrivilege::getUserId, userIds));
        if(!CollectionUtils.isEmpty(userPrivileges)){
            Set<Long> ids = userPrivileges.stream().map(UserPrivilege::getPrivilegeId).collect(Collectors.toSet());
            privilegeIds.addAll(ids);
        }

        if(!CollectionUtils.isEmpty(privilegeIds)){
            List<Privilege> privileges = privilegeMapper.selectBatchIds(privilegeIds);
            return privileges;
        }
        return new ArrayList<>();
    }

    /**
     * 删除用户及其关联信息
     * @param userIds
     * @return
     */
    @Transactional
    @Override
    public BaseApiResult delUsersById(List<Long> userIds) {
        if(CollectionUtils.isEmpty(userIds)){
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE,MessageConstant.DATA_IS_NULL);
        }

        // 删除用户
        this.removeByIds(userIds);

        // 删除用户角色关联信息
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().in(UserRole::getUserId,userIds));

        // 删除用户权限关联信息
        userPrivilegeMapper.delete(new LambdaQueryWrapper<UserPrivilege>().in(UserPrivilege::getUserId,userIds));

        // 清除token
        for(Long id:userIds){
            redisUtil.delById(String.valueOf(id));
        }

        return BaseApiResult.success("删除用户成功");
    }

    @Transactional
    @Override
    public BaseApiResult closeAccount(String token) {
        User user = (User)redisUtil.getByToken(token);
        if (ObjectUtils.isEmpty(user)){
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
        user.setStatusCd(GlobalEnum.INVALID.getCode());
        this.updateById(user);
        return BaseApiResult.success("注销成功！");
    }

    @Transactional
    @Override
    public BaseApiResult modifyUserInfo(User user,String token) {
        // 从redis里面获取用户信息
        User cacheUser = (User)redisUtil.getByToken(token);
        if(ObjectUtils.isEmpty(user) || ObjectUtils.isEmpty(user)){
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.DATA_IS_NULL);
        }
        // 如果用户没有管理员权限或者用户修改的数据不是自己的就返回错误
        if(!isAdmin(cacheUser.getId())
                && !cacheUser.getId().equals(user.getId())){
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.NO_PRIVILEGE);
        }

        this.updateById(user);

        // 更新redis里面的数据
        User newUser = this.getById(user.getId());
        newUser.setPassword("");
        redisUtil.updateById(String.valueOf(newUser.getId()),newUser,12, TimeUnit.HOURS);

        return BaseApiResult.success("用户信息修改成功！");
    }

    @Override
    public BaseApiResult modifyPassword(Long userId, String oldPassword, String newPassword) {
        User user1 = this.getById(userId);

//        //MD5加密
//        String md5Password = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        if (!oldPassword.equals(user1.getPassword())) {
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,"原密码错误");
        }
//        String s = DigestUtils.md5DigestAsHex(newPassword.getBytes());
        LambdaUpdateWrapper<User> userLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userLambdaUpdateWrapper.eq(User::getId,userId).set(User::getPassword,newPassword);
        boolean update = this.update(userLambdaUpdateWrapper);
        if(update){
            return BaseApiResult.success("修改密码成功");
        }

        return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,"修改密码失败");
    }

    @Override
    public Page<User> multiConditionalPageQuery(UserDTO userDTO) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = packageQueryWrapper(userDTO);
        // 根据创建时间降序排序
        userLambdaQueryWrapper.orderByDesc(User::getCreateDate);
        Page<User> page = new Page<>(1,10);
        if(!ObjectUtils.isEmpty(userDTO.getPageNum())){
            page.setCurrent(userDTO.getPageNum());
        }

        if(!ObjectUtils.isEmpty(userDTO.getPageSize())){
            page.setSize(userDTO.getPageSize());
        }

        Page<User> userPage = userMapper.selectPage(page, userLambdaQueryWrapper);
        return userPage;
    }

    /**
     * 构建查询条件
     * @param userDTO
     * @return
     */
    public LambdaQueryWrapper<User> packageQueryWrapper(UserDTO userDTO){
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(ObjectUtils.isEmpty(userDTO)){
            return userLambdaQueryWrapper;
        }

        if(!ObjectUtils.isEmpty(userDTO.getId())){
            userLambdaQueryWrapper.eq(User::getId,userDTO.getId());
        }

        if(!ObjectUtils.isEmpty(userDTO.getEmail())){
            userLambdaQueryWrapper.eq(User::getEmail,userDTO.getEmail());
        }

        // 模糊匹配用户账号
        if(!ObjectUtils.isEmpty(userDTO.getCode())){
            userLambdaQueryWrapper.like(User::getCode,userDTO.getCode());
        }
        // 模糊匹配用户名
        if(!ObjectUtils.isEmpty(userDTO.getName())){
            userLambdaQueryWrapper.like(User::getName,userDTO.getName());
        }

        if(!ObjectUtils.isEmpty(userDTO.getEducation())){
            userLambdaQueryWrapper.eq(User::getEducation,userDTO.getEducation());
        }

        if(!ObjectUtils.isEmpty(userDTO.getIdentityCode())){
            userLambdaQueryWrapper.eq(User::getIdentityCode,userDTO.getIdentityCode());
        }

        if(!ObjectUtils.isEmpty(userDTO.getPhone())){
            userLambdaQueryWrapper.like(User::getPhone,userDTO.getPhone());
        }

        if(!ObjectUtils.isEmpty(userDTO.getSchool())){
            userLambdaQueryWrapper.like(User::getSchool,userDTO.getSchool());
        }

        if(!ObjectUtils.isEmpty(userDTO.getSubjectId())){
            userLambdaQueryWrapper.like(User::getSubjectId,userDTO.getSubjectId());
        }

        if(!ObjectUtils.isEmpty(userDTO.getCreateDate())){
            userLambdaQueryWrapper.eq(User::getCreateDate,userDTO.getCreateDate());
        }

        // 查询在某个时间点以前注册的用户
        if(!ObjectUtils.isEmpty(userDTO.getMaxDate())){
            userLambdaQueryWrapper.le(User::getCreateDate,userDTO.getMaxDate());
        }
        // 查询在某个时间点之后注册的用户
        if(!ObjectUtils.isEmpty(userDTO.getMinDate())){
            userLambdaQueryWrapper.ge(User::getCreateDate,userDTO.getMinDate());
        }
        userLambdaQueryWrapper.ne(User::getStatusCd,GlobalEnum.INVALID.getCode());
        return userLambdaQueryWrapper;
    }

    /**
     * 根据用户Id和权限Id，为用户添加权限
     * @param userId
     * @param privilegeId
     */
    @Override
    public void addPrivilege(Long userId, Long privilegeId) {
        if(ObjectUtils.isEmpty(userId) || ObjectUtils.isEmpty(privilegeId)){
            return;
        }
        LambdaQueryWrapper<UserPrivilege> lambdaQueryWrapper = new LambdaQueryWrapper<UserPrivilege>()
                .eq(UserPrivilege::getUserId, userId)
                .eq(UserPrivilege::getPrivilegeId, privilegeId);

        List<UserPrivilege> userPrivilegeList = userPrivilegeMapper.selectList(lambdaQueryWrapper);

        if(!CollectionUtils.isEmpty(userPrivilegeList)){
            return;
        }

        UserPrivilege userPrivilege=new UserPrivilege();
        userPrivilege.setUserId(userId);
        userPrivilege.setPrivilegeId(privilegeId);
        userPrivilegeMapper.insert(userPrivilege);
    }

    /**
     * 根据用户Id和角色Id，为用户添加角色
     * @param userId
     * @param roleId
     */
    @Override
    public void addRole(Long userId, Long roleId) {
        if(ObjectUtils.isEmpty(userId) || ObjectUtils.isEmpty(roleId)){
            return;
        }
        LambdaQueryWrapper<UserRole> lambdaQueryWrapper = new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getUserId, userId)
                .eq(UserRole::getRoleId, roleId);

        List<UserRole> userRoleList = userRoleMapper.selectList(lambdaQueryWrapper);
        if(!CollectionUtils.isEmpty(userRoleList)){
            return;
        }

        UserRole userRole=new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRoleMapper.insert(userRole);
    }

    @Override
    public void deletePrivilege(Long userId, Long privilegeId) {
        if(ObjectUtils.isEmpty(userId) || ObjectUtils.isEmpty(privilegeId)){
            return;
        }
        LambdaQueryWrapper<UserPrivilege> lambdaQueryWrapper = new LambdaQueryWrapper<UserPrivilege>()
                .eq(UserPrivilege::getPrivilegeId, privilegeId)
                .eq(UserPrivilege::getUserId, userId);
        userPrivilegeMapper.delete(lambdaQueryWrapper);
    }

    @Override
    public void deleteRole(Long userId, Long roleId) {
        if(ObjectUtils.isEmpty(userId) || ObjectUtils.isEmpty(roleId)){
            return;
        }
        LambdaQueryWrapper<UserRole> lambdaQueryWrapper = new LambdaQueryWrapper<UserRole>()
                .eq(UserRole::getRoleId, roleId)
                .eq(UserRole::getUserId, userId);
        userRoleMapper.delete(lambdaQueryWrapper);
    }

    /**
     * 获取完整的用户信息
     * @param user
     * @return
     */
    @Override
    public UserDTO getCompletionUser(User user) {
        if(ObjectUtils.isEmpty(user)){
            return new UserDTO();
        }
        Subject subject = subjectService.getById(user.getSubjectId());
        Category category = categoryService.getById(user.getCategoryId());
        UserDTO userDTO = new UserDTO();
        BeanUtilCopy.copyProperties(user,userDTO);
        userDTO.setSubject(subject.getName());
        userDTO.setCategory(category.getName());
        return userDTO;
    }

    @Override
    public List<User> getAllUser() {
        List<User> userList = this.list();
        List<Category> categoryList = categoryService.list();
        List<Subject> subjectList = subjectService.list();
        HashMap<Long, Category> categoryHashMap = new HashMap<>();
        HashMap<Long, Subject> subjectHashMap = new HashMap<>();

        if(!CollectionUtils.isEmpty(categoryList)){
            Map<Long, Category> categoryMap = categoryList.stream()
                    .collect(Collectors.toMap(Category::getId, Function.identity()));
            categoryHashMap.putAll(categoryMap);
        }

        if(!CollectionUtils.isEmpty(subjectList)){
            Map<Long, Subject> subjectMap = subjectList.stream()
                    .collect(Collectors.toMap(Subject::getId, Function.identity()));
            subjectHashMap.putAll(subjectMap);
        }

        for(User user : userList){
            Category category = categoryHashMap.get(user.getCategoryId());
            Subject subject = subjectHashMap.get(user.getSubjectId());
            user.setCategoryObj(category);
            user.setSubjectObj(subject);
        }

        return userList;
    }


}




