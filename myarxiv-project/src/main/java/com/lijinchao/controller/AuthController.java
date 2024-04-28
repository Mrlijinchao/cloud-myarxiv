package com.lijinchao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lijinchao.constant.MessageConstant;
import com.lijinchao.entity.User;
import com.lijinchao.service.AuthService;
import com.lijinchao.service.LoginService;
import com.lijinchao.utils.BaseApiResult;
import com.lijinchao.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@Controller("authController")
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private LoginService loginService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private AuthService authService;

    /**
     * 登录
     * @param user
     * @return
     */
    @PostMapping("/doLogin")
    @ResponseBody
    public BaseApiResult doLogin(@RequestBody User user) {
        try {
            return BaseApiResult.success(loginService.doLogin(user));
        } catch (Exception e) {
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,e.getMessage());
        }
    }

     /**
     *@Description 用户退出
     *@Author huangyc
     *@Date 2022/7/10
     */
    @PostMapping("/exit")
    @ResponseBody
    public BaseApiResult exit(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        //清除用户信息
        redisUtil.delByToken(token);
        //返回登录地址
        return BaseApiResult.success("/login");
    }

    /**
     * 检查用户是否是登录状态
     */
    @GetMapping("/checkLoginState")
    @ResponseBody
    public BaseApiResult checkLoginState(HttpServletRequest request, HttpServletResponse response) {
        // 缓存 2s; 避免前端频繁刷新
        response.setHeader("Cache-Control", "max-age=2, public");
        //获取 header里的token
        final String token = request.getHeader("authorization");
        if (!StringUtils.hasText(token)) {
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, "未检查到携带的token");
        }
        if(!redisUtil.hasKeyByToken(token)){
            return BaseApiResult.error(MessageConstant.PARAMS_ERROR_CODE, "用户已下线");
        }

        return BaseApiResult.success("用户在线");
    }

    /**
     * 邮箱登录
     * @param emailMap
     * @return
     */
    @PostMapping("/emailLogin")
    @ResponseBody
    public Object emailLogin(@RequestBody Map<String,String> emailMap){
        try {
            String email = emailMap.get("email");
            String emailCode = emailMap.get("emailCode");
            return BaseApiResult.success(loginService.emailLogin(email,emailCode));
        } catch (Exception e) {
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,e.getMessage());
        }
    }

    /**
     * 背书
     * @param map
     * @return
     */
    @PostMapping("/endorsement")
    @ResponseBody
    public BaseApiResult endorsement(@RequestBody Map<String,String> map){
        String code  = map.get("code");
        String password = map.get("password");
        String endorsementCode = map.get("endorsementCode");
        String cipherText = map.get("cipherText");
        try {
            return authService.endorsement(code,password,endorsementCode,cipherText);
        }catch (Exception e){
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,MessageConstant.OPERATE_FAILED);
        }
    }





}
