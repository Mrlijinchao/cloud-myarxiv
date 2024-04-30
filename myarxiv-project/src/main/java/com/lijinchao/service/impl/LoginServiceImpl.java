package com.lijinchao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lijinchao.constant.MessageConstant;
import com.lijinchao.entity.Category;
import com.lijinchao.entity.Subject;
import com.lijinchao.entity.User;
import com.lijinchao.entity.dto.UserDTO;
import com.lijinchao.enums.GlobalEnum;
import com.lijinchao.mapper.UserMapper;
import com.lijinchao.service.CategoryService;
import com.lijinchao.service.LoginService;
import com.lijinchao.service.SubjectService;
import com.lijinchao.service.UserService;
import com.lijinchao.utils.BaseApiResult;
import com.lijinchao.utils.BeanUtilCopy;
import com.lijinchao.utils.RedisUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    UserService userService;

    @Resource
    UserMapper userMapper;

    @Resource
    RedisUtil redisUtil;

    @Resource
    SubjectService subjectService;

    @Resource
    CategoryService categoryService;

    @Transactional
    @Override
    public Object doLogin(User user) throws Exception {

        if(StringUtils.isEmpty(user.getCode())){
            throw new Exception("用户名不能为空");
        }

        User one = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getCode, user.getCode()));
        if(ObjectUtils.isEmpty(one)){
            throw new Exception("账号不存在，请联系系统管理员注册之后才能登录");
        }

        if(one.getStatusCd().equals(GlobalEnum.INVALID.getCode())){
            throw new Exception("账号已注销，请联系系统管理员恢复账号才能登录");
        }

        if(!user.getPassword().equals(one.getPassword())){
            throw new Exception("密码错误！");
        }

        // 把密码置空，防止密码泄露
        one.setPassword("");
        String token = "Bearer " + UUID.randomUUID().toString().replaceAll("-", "");
        // 以token和用户Id作为key，目的是后面可以根据token或者Id对数据进行操作
        redisUtil.set(token + ":" + one.getId(),one,12, TimeUnit.HOURS);

        return BaseApiResult.returnToken(one,token);
    }

    @Override
    public Object emailLogin(String email, String code) throws Exception {
        if(!StringUtils.hasText(email)
        || !StringUtils.hasText(code)){
            throw new Exception("参数为空");
        }
        String emailCode = (String) redisUtil.get(email);
        if(!code.equals(emailCode)){
            throw new Exception("验证码错误");
        }
        User one = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
        // 把密码置空，防止密码泄露
        one.setPassword("");
        String token = "Bearer " + UUID.randomUUID().toString().replaceAll("-", "");
        // 以token和用户Id作为key，目的是后面可以根据token或者Id对数据进行操作
        redisUtil.set(token + ":" + one.getId(),one,12, TimeUnit.HOURS);

        return BaseApiResult.returnToken(one,token);
    }

    @Override
    public Object backgroundLogin(User user) throws Exception {
        List<User> userList = userService.list(new QueryWrapper<User>()
                .eq("code", user.getCode()).eq("password", user.getPassword()));
        if(CollectionUtils.isEmpty(userList)){
            throw new Exception("账号或者密码错误");
        }
        User one = userList.get(0);
        // 检查是否是管理员或者审核角色
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("audit");
        Boolean aBoolean = userService.checkPermissionForUser(one, permissions);
        if(!aBoolean){
            throw new Exception("仅限管理员和审核员登录");
        }
        String isAdmin = "0";
        Boolean aBoolean1 = userService.isAdmin(one.getId());
        if(aBoolean1){
            isAdmin = "1";
        }
        one.setIsAdmin(isAdmin);

        // 封装subject和category
        Subject subject = subjectService.getById(one.getSubjectId());
        Category category = categoryService.getById(one.getCategoryId());
        one.setSubjectObj(subject);
        one.setCategoryObj(category);

        // 把密码置空，防止密码泄露
        one.setPassword("");
        String token = "Bearer " + UUID.randomUUID().toString().replaceAll("-", "");
        // 以token和用户Id作为key，目的是后面可以根据token或者Id对数据进行操作
        redisUtil.set(token + ":" + one.getId(),one,12, TimeUnit.HOURS);

        return BaseApiResult.returnToken(one,token);

    }
}
