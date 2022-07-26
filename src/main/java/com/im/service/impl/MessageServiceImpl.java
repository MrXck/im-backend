package com.im.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.entity.Message;
import com.im.mapper.MessageMapper;
import com.im.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public List<Message> findMessageById(Integer user_id, Integer userId) {
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Message::getFromId, user_id).or().eq(Message::getToId, user_id);
        queryWrapper.eq(Message::getFromId, userId).or().eq(Message::getToId, userId);
        queryWrapper.orderByAsc(Message::getTime);
        return messageMapper.selectList(queryWrapper);
    }
}
