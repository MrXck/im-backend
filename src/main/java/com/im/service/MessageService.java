package com.im.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.im.entity.Message;

import java.util.List;

public interface MessageService extends IService<Message> {
    List<Message> findMessageById(Integer user_id, Integer userId);
}
