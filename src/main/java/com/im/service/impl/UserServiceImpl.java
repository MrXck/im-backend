package com.im.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.config.RSAConfig;
import com.im.entity.User;
import com.im.mapper.UserMapper;
import com.im.service.UserService;
import com.im.utils.JwtUtils;
import com.im.utils.MD5Utils;
import com.im.utils.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RSAConfig rsaConfig;

    @Override
    public User login(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        String username = user.getUsername();
        String password = user.getPassword();
        queryWrapper.eq(username != null, User::getUsername, username);
        queryWrapper.eq(password != null, User::getPassword, MD5Utils.md5(password));
        System.out.println(MD5Utils.md5(password));
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public String register(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        String username = user.getUsername();
        String avatar = user.getAvatar();
        String password = user.getPassword();
        String phone = user.getPhone();
        if(username == null || "".equals(username)){
            return "请输入用户名";
        }
        queryWrapper.eq(User::getUsername, username);
        User user1 = userMapper.selectOne(queryWrapper);
        if(user1 == null){
            if(password == null || "".equals(password)){
                return "请输入密码";
            }
            if(phone == null || "".equals(phone)){
                return "请输入手机号";
            }
            if("".equals(avatar)){
                user.setAvatar("/img/user.jpg");
            }
            user.setPassword(MD5Utils.md5(password));
            userMapper.insert(user);
            return "注册成功";
        }else {
            return "用户名已存在";
        }
    }

    @Override
    public List<User> findByPhone(User user) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        String phone = user.getPhone();
        queryWrapper.eq(phone != null, User::getPhone, phone);
        List<User> users = userMapper.selectList(queryWrapper);
        return users.stream().map((item)->{
            item.setPassword("");
            return item;
        }).collect(Collectors.toList());
    }

    public Integer checkToken(String token) {
        Map<String, Object> map = JwtUtils.checkToken(token, this.rsaConfig.getPublishStr());
        if (CollUtil.isNotEmpty(map)) {
            return Convert.toInt(map.get("userId"));
        }
        return null;
    }

    @Override
    public User updateByUserId(User user) {
        Integer userId = UserThreadLocal.get();
        User user1 = userMapper.selectById(userId);
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        String username = user.getUsername();
        String password = user.getPassword();
        String avatar = user.getAvatar();
        String phone = user.getPhone();
        String synopsis = user.getSynopsis();
        updateWrapper.eq(User::getId, userId);
        updateWrapper.set(username != null && !"".equals(username), User::getUsername, username);
        updateWrapper.set(password != null && !"".equals(password), User::getPassword, MD5Utils.md5(password));
        updateWrapper.set(avatar != null && !"".equals(avatar), User::getAvatar, avatar);
        updateWrapper.set(phone != null && !"".equals(phone), User::getPhone, phone);
        updateWrapper.set(synopsis != null && !"".equals(synopsis), User::getSynopsis, synopsis);
        userMapper.update(user1, updateWrapper);
        user1 = userMapper.selectById(userId);
        return user1;
    }
}
