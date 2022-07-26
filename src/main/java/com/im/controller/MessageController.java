package com.im.controller;

import com.im.common.R;
import com.im.service.MessageService;
import com.im.utils.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/{user_id}")
    public R findById(@PathVariable("user_id") Integer user_id){
        Integer userId = UserThreadLocal.get();
        if(userId == null){
            return R.error("获取失败");
        }
        return R.success(messageService.findMessageById(user_id, userId));
    }
}
