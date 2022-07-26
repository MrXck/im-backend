package com.im.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.entity.HistoricalChat;
import com.im.entity.User;
import com.im.mapper.HistoricalChatMapper;
import com.im.mapper.UserMapper;
import com.im.service.HistoricalChatService;
import com.im.utils.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoricalChatServiceImpl extends ServiceImpl<HistoricalChatMapper, HistoricalChat> implements HistoricalChatService {

    @Autowired
    private HistoricalChatMapper historicalChatMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> findById(Integer user_id) {
        LambdaQueryWrapper<HistoricalChat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HistoricalChat::getUserId, user_id);
        List<HistoricalChat> historicalChats = historicalChatMapper.selectList(queryWrapper);
        List<Integer> withIdList = historicalChats.stream().map((item)->{
            return item.getWithId();
        }).collect(Collectors.toList());
        List<User> list = new ArrayList<>();
        if(withIdList.size() < 1){
            return list;
        }
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.in(User::getId, withIdList);

        list = userMapper.selectList(userLambdaQueryWrapper);
        return list.stream().map((item)->{
            item.setPassword("");
            return item;
        }).collect(Collectors.toList());
    }

    @Override
    public Boolean add(Integer user_id, Integer userId) {
        LambdaQueryWrapper<HistoricalChat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HistoricalChat::getUserId, userId);
        queryWrapper.eq(HistoricalChat::getWithId, user_id);
        HistoricalChat historicalChat = historicalChatMapper.selectOne(queryWrapper);
        if(historicalChat == null){
            HistoricalChat historicalChat1 = new HistoricalChat();
            historicalChat1.setUserId(userId);
            historicalChat1.setWithId(user_id);
            historicalChatMapper.insert(historicalChat1);
            return true;
        }
        return false;
    }

    @Override
    public Boolean deleteByUserId(Integer user_id) {
        Integer userId = UserThreadLocal.get();
        LambdaQueryWrapper<HistoricalChat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HistoricalChat::getUserId, userId);
        queryWrapper.eq(HistoricalChat::getWithId, user_id);
        int delete = historicalChatMapper.delete(queryWrapper);
        return delete != 0;
    }
}
