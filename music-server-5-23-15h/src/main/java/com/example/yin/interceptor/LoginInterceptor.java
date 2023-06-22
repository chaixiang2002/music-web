package com.example.yin.interceptor;

import com.example.yin.common.R;
import com.example.yin.config.CustomObjectMapper;
import com.example.yin.config.RedisTemplate;
import com.example.yin.common.Constants;
import com.example.yin.model.dto.UserRedisDto;
import com.example.yin.util.AuthUtil;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private CustomObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //放行OPTIONS请求
        String method = request.getMethod();
        if ("OPTIONS".equals(method)) {
            return true;
        }

        //1、从请求头中拿到token
        String token = AuthUtil.getToken();


        //2、如果token是空的，先看一下请求是否在白名单里面
        String requestURI = request.getRequestURI();
        if(!ObjectUtils.isEmpty(requestURI)&&requestURI.equals("/error")){
            R r = R.error(40004, "当前资源未找到");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(40004);
            response.getWriter().write(objectMapper.writeValueAsString(r));
            return false;
        }

        if (ObjectUtils.isEmpty(token)) {

            //白名单匹配，即访问的接口不需要登录权限
            if (AuthUtil.agreeRequest(requestURI)) {
                return true;
            }

            //否则，让用户去登录
            R r = R.error(40001, "当前用户未登录");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(40001);
            response.getWriter().write(objectMapper.writeValueAsString(r));
            return false;
        }

        //如果有token，但是现在是去注册/登录，则不拦截
        if (AuthUtil.mediaRequest(requestURI)) {
            return true;
        }

        //3、先去看一下有没有token->user这个键值对
        Optional<UserRedisDto> userDto = redisTemplate.getObject(Constants.TOKEN_PREFIX + token, UserRedisDto.class);

        //4、若为空，说明用户太长时间没操作，请重新登录,或者
        if (!userDto.isPresent()) {
            R r = R.error(40002, "用户登录已超时");
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(40002);
            response.getWriter().write(objectMapper.writeValueAsString(r));
            return false;
        }

        //5、不为空，看一下是不是已被挤退
        UserRedisDto loginUser = userDto.get();
        String lastTaken = redisTemplate.get(Constants.ID_PREFIX + loginUser.getType() + ":" + loginUser.getId());

        //当前的token不是最新的token，已被挤退
        if (!token.equals(lastTaken)) {
            //被挤退的话，我们直接删掉redis多于的key-value
            redisTemplate.remove(Constants.TOKEN_PREFIX + token);

            R r = R.error(40003, "当前用户已异地登录");

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(40003);
            response.getWriter().write(objectMapper.writeValueAsString(r));
            return false;
        }

        //6。认证成功了，重设一下超时时间
        redisTemplate.expire(Constants.ID_PREFIX + loginUser.getType() + ":" + loginUser.getId(), Constants.TOKEN_TIME);
        redisTemplate.expire(Constants.TOKEN_PREFIX + token, Constants.TOKEN_TIME);
        return true;
    }
}
