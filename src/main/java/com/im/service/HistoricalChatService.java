package com.im.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.im.entity.HistoricalChat;
import com.im.entity.User;

import java.util.List;

public interface HistoricalChatService extends IService<HistoricalChat> {
    List<User> findById(Integer user_id);

    Boolean add(Integer user_id, Integer userId);

    Boolean deleteByUserId(Integer user_id);
}
