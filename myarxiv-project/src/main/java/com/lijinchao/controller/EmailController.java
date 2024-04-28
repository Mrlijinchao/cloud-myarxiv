package com.lijinchao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lijinchao.constant.MessageConstant;
import com.lijinchao.entity.User;
import com.lijinchao.service.UserService;
import com.lijinchao.utils.BaseApiResult;
import com.lijinchao.utils.RedisUtil;
import com.lijinchao.utils.mail.SendMail;
import com.lijinchao.utils.mail.Tools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@CrossOrigin
@Slf4j
@RestController
@RequestMapping("/email")
public class EmailController {

    @Resource
    RedisUtil redisUtil;

    @Resource
    UserService userService;

    /**
     * 发送邮件验证码
     * @param emailMap
     * @return
     */
    @PostMapping("")
    public Object userLoginEmailVerify(@RequestBody Map<String,String> emailMap){
        String email = emailMap.get("email");
        String emailTitle = emailMap.get("emailTitle");
        String emailSubject = emailMap.get("emailSubject");
        log.info("email: {}",email);
        String code = Tools.randomCode();
        try {
//            List<User> userList = userService.list(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
//            if(CollectionUtils.isEmpty(userList)){
//                return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,"发送验证码失败，此邮箱未注册账号");
//            }
//            SendMail.sendMail(email, Tools.getEmailContent(code,"登录验证码邮件"),"登录验证码");
            SendMail.sendMail(email, Tools.getEmailContent(code,emailTitle),emailSubject);
            redisUtil.set(email,code,5, TimeUnit.MINUTES);
            return BaseApiResult.success("我们已经向您所填的邮箱发送了验证码，请及时查收！验证码在5分钟内有效！");
        } catch (MessagingException e) {
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,"发送验证码失败");
        }
    }

    @PostMapping("/sendEmail")
    public BaseApiResult sendEmail(@RequestBody Map<String,String> emailMap){
        String email = emailMap.get("email");
        String emailContent = emailMap.get("emailContent");
        String emailSubject = emailMap.get("emailSubject");
        try {
            SendMail.sendMail(email, emailContent,emailSubject);
            return BaseApiResult.success("邮件发送成功，请及时查收！");
        } catch (MessagingException e) {
            e.printStackTrace();
            return BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,"邮件发送失败！");
        }

    }



}
