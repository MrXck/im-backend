package com.im.controller;

import com.im.common.R;
import com.im.config.RSAConfig;
import com.im.entity.User;
import com.im.service.UserService;
import com.im.utils.JwtUtils;
import com.im.utils.NoAuthorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RSAConfig rsaConfig;

    @NoAuthorization
    @PostMapping("/login")
    public R login(@RequestBody User user){
        System.out.println(user);
        User user1 = userService.login(user);
        System.out.println(user1);
        if(user1 != null){
            user1.setPassword("");
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user1.getId()); //将用户id存入到token中
            //生成token，算法采用RSA非对称加密
            String token = JwtUtils.createToken(claims, this.rsaConfig.getPrivateStr(), 720);
            Map<String, Object> map = new HashMap<>();
            map.put("token", token);
            map.put("user", user1);
            return R.success(map);
        }
        return R.error("用户名或密码错误");
    }

    @NoAuthorization
    @PostMapping("/register")
    public R register(@RequestBody User user){
        String result = userService.register(user);
        if(result.equals("注册成功")){
            return R.success("注册成功");
        }
        return R.error(result);
    }

    @PostMapping("/find")
    public R findByPhone(@RequestBody User user){
        List<User> users = userService.findByPhone(user);
        return R.success(users);
    }

    @PostMapping("/update")
    public R update(@RequestBody User user){
        User user1 = userService.updateByUserId(user);
        return R.success(user1);
    }


}
