package com.im.controller;

import com.im.common.R;
import com.im.entity.User;
import com.im.service.HistoricalChatService;
import com.im.utils.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/history")
public class HistoricalChatController {

    @Autowired
    private HistoricalChatService historicalChatService;

    @GetMapping("/list")
    public R list(){
        Integer user_id = UserThreadLocal.get();
        if(user_id == null){
            return R.error("获取失败");
        }
        List<User> list = historicalChatService.findById(user_id);
        return R.success(list);
    }

    @PostMapping("/add/{user_id}")
    public R add(@PathVariable("user_id") Integer user_id){
        Integer userId = UserThreadLocal.get();
        Boolean result = historicalChatService.add(user_id, userId);
        if(result){
            return R.success("");
        }
        return R.error("");
    }

    @PostMapping("/delete/{user_id}")
    public R delete(@PathVariable("user_id") Integer user_id){
        Boolean result = historicalChatService.deleteByUserId(user_id);
        if(result){
            return R.success("操作成功");
        }
        return R.error("操作失败");
    }

}
