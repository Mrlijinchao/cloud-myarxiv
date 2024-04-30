package com.lijinchao.permission;

import com.alibaba.fastjson.JSONObject;
import com.lijinchao.constant.MessageConstant;
import com.lijinchao.entity.User;
import com.lijinchao.feign.feignclient.auth.AuthClient;
import com.lijinchao.uitls.EnumUtil;
import com.lijinchao.uitls.RedisUtil;
import com.lijinchao.utils.BaseApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @ClassName AuthenticationInterceptor
 * @Description 权限校验，拦截器
 * 参考文章： 注解式权限校验 https://blog.csdn.net/LitongZero/article/details/103628706
 * @Author lijinchao
 * @Date 2022/12/7 20:24
 * @Version 1.0
 **/
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    /**
     * 拦截器中无法注入bean，因此使用构造器
     */
    private final RedisUtil redisUtil;
    private final AuthClient authClient;

    public AuthenticationInterceptor(RedisUtil redisUtil, AuthClient authClient) {
        this.redisUtil = redisUtil;
        this.authClient = authClient;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        //获取 header里的token
        final String token = request.getHeader("authorization");
        if (token == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            String jsonObj = JSONObject.toJSONString(BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,"未携带token！"));
            returnJson(response,jsonObj);
            return false;
        }

        User user = (User)redisUtil.getByToken(token);
//        User user = redisUtil.getByToken(token);
//        User user = (User)redisUtil.getByTokenNew(token);

        if (ObjectUtils.isEmpty(user)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            String jsonObj = JSONObject.toJSONString(BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,"您的token已失效，请重新登录！！"));
            returnJson(response,jsonObj);
            return false;
        }

//        // 发请求到project模块验证
//        Boolean isAuth = authClient.authRequest(null, token);
//
//        if(!isAuth){
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            String jsonObj = JSONObject.toJSONString(BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,"验证不通过，请先登录账号"));
//            returnJson(response,jsonObj);
//            return false;
//        }

        log.info("token验证通过！");

        // 获取方法中的注解
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        // 省略判断是否需要登录的方法.....

        // 获取类注解
        Permission permissionClass = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), Permission.class);
        // 获取方法注解
        Permission permissionMethod = AnnotationUtils.findAnnotation(method, Permission.class);

        // 判断是否需要权限校验
        if (permissionClass == null && permissionMethod == null) {
            // 不需要校验权限，直接放行
            return true;
        }

        // 省略Token解析的方法.....

//        // 此处根据自己的系统架构，通过Token或Cookie等获取用户信息。
//        User userInfo = userService.queryById(userData.get("id").asString());
//        if (userInfo == null || userInfo.getPermissionEnum() == null) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            return false;
//        }

        // 获取该方法注解，优先级:方法注解>类注解
        List<String> permissions;
        if (permissionClass != null && permissionMethod == null) {
            // 类注解不为空，方法注解为空，使用类注解
            permissions = EnumUtil.getValue(permissionClass.value(), permissionClass.roleValue());
        } else if (permissionClass == null) {
            // 类注解为空，使用方法注解
            permissions = EnumUtil.getValue(permissionMethod.value(), permissionMethod.roleValue());
        } else {
            // 都不为空，使用方法注解
            permissions = EnumUtil.getValue(permissionMethod.value(), permissionMethod.roleValue());
        }

        // 校验该用户是否有改权限
        // 校验方法可自行实现，拿到permissionEnums中的参数进行比较
        if (authClient.authPermission(null,token,permissions)) {
            // 拥有权限
            return true;
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            String jsonObj = JSONObject.toJSONString(BaseApiResult.error(MessageConstant.PROCESS_ERROR_CODE,"您没有此接口的访问权限！"));
            returnJson(response,jsonObj);
            return false;
        }
    }

    /**
     * @Author lijinchao
     * @Description 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     * @Date 20:26 2022/12/7
     * @Param [request, response, handler, modelAndView]
     **/
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) {
        // empty
    }

    /**
     * @Author lijinchao
     * @Description 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
     * @Date 20:27 2022/12/7
     * @Param [request, response, handler, ex]
     **/
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // empty
    }

    /**
     * 设置返回格式为json
     * @param response
     * @param json
     * @throws Exception
     */
    private void returnJson(HttpServletResponse response, String json) throws Exception{
        PrintWriter writer = null;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("json/html; charset=utf-8");
        try {
            writer = response.getWriter();
            writer.print(json);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

}
