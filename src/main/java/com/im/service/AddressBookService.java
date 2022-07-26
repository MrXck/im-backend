package com.im.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.im.entity.AddressBook;
import com.im.entity.User;

import java.util.List;

public interface AddressBookService extends IService<AddressBook> {
    List<User> findById(Integer user_id);

    Boolean add(Integer user_id, Integer userId);

    List<User> findNewFriendsById(Integer user_id);

    void deleteByUserId(Integer user_id);

    void agree(Integer user_id);

    void refuse(Integer user_id);

    Boolean isFriend(Integer user_id, Integer userId);
}
