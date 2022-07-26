package com.im.controller;

import com.im.common.R;
import com.im.service.AddressBookService;
import com.im.utils.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @PostMapping("/add/{user_id}")
    public R add(@PathVariable("user_id") Integer user_id){
        Integer userId = UserThreadLocal.get();
        if(user_id.equals(userId)){
            return R.error("不可以添加自己");
        }
        Boolean result = addressBookService.add(user_id, userId);
        if(result){
            return R.success("已经发送请求");
        }
        return R.error("请求发送失败");
    }

    @GetMapping("/list")
    public R list(){
        Integer user_id = UserThreadLocal.get();
        if(user_id == null){
            return R.error("获取失败");
        }
        return R.success(addressBookService.findById(user_id));
    }

    @PostMapping("/newFriends")
    public R getNewFriends(){
        Integer user_id = UserThreadLocal.get();
        if(user_id == null){
            return R.error("获取失败");
        }
        return R.success(addressBookService.findNewFriendsById(user_id));
    }

    @PostMapping("/remove/{user_id}")
    public R removeByUserId(@PathVariable("user_id") Integer user_id){
        addressBookService.deleteByUserId(user_id);
        return R.success("删除成功");
    }

    @PostMapping("/agree/{user_id}")
    public R agree(@PathVariable("user_id")Integer user_id){
        addressBookService.agree(user_id);
        return R.success("");
    }

    @PostMapping("/refuse/{user_id}")
    public R refuse(@PathVariable("user_id")Integer user_id){
        addressBookService.refuse(user_id);
        return R.success("");
    }
}
