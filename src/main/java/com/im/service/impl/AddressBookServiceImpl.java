package com.im.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.im.entity.AddressBook;
import com.im.entity.HistoricalChat;
import com.im.entity.Message;
import com.im.entity.User;
import com.im.mapper.AddressBookMapper;
import com.im.mapper.HistoricalChatMapper;
import com.im.mapper.MessageMapper;
import com.im.mapper.UserMapper;
import com.im.service.AddressBookService;
import com.im.utils.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private HistoricalChatMapper historicalChatMapper;

    @Override
    public List<User> findById(Integer user_id) {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getApplicant, user_id).or().eq(AddressBook::getRespondent, user_id);
        queryWrapper.eq(AddressBook::getIsAgree, 1);
        List<AddressBook> addressBooks = addressBookMapper.selectList(queryWrapper);
        List<Integer> list;
        List<User> users = new ArrayList<>();
        list = addressBooks.stream().map((item)->{
            if(item.getApplicant().equals(user_id)){
                return item.getRespondent();
            }else {
                return item.getApplicant();
            }
        }).collect(Collectors.toList());
        if(list.size() < 1){
            return users;
        }
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.in(User::getId, list);
        users = userMapper.selectList(userLambdaQueryWrapper);
        return users;
    }

    @Override
    public Boolean add(Integer user_id, Integer userId) {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getApplicant, user_id);
        queryWrapper.eq(AddressBook::getRespondent, userId);
        AddressBook respondent = addressBookMapper.selectOne(queryWrapper);

        LambdaQueryWrapper<AddressBook> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(AddressBook::getApplicant, userId);
        queryWrapper1.eq(AddressBook::getRespondent, user_id);
        List<AddressBook> applicant = addressBookMapper.selectList(queryWrapper1);


        if(respondent == null && applicant.size() == 0){
            AddressBook addressBook = new AddressBook();
            addressBook.setApplicant(userId);
            addressBook.setRespondent(user_id);
            addressBook.setIsAgree(0);
            addressBookMapper.insert(addressBook);
            return true;
        }else if(applicant != null && respondent == null){
            return false;
        }else if(applicant == null && respondent != null) {
            respondent.setIsAgree(1);
            addressBookMapper.updateById(respondent);
            return true;
        }
        return false;
    }

    @Override
    public List<User> findNewFriendsById(Integer user_id) {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getRespondent, user_id);
        queryWrapper.eq(AddressBook::getIsAgree, 0);
        List<AddressBook> addressBooks = addressBookMapper.selectList(queryWrapper);
        List<Integer> newFriendsList = addressBooks.stream().map((item)->{
            return item.getApplicant();
        }).collect(Collectors.toList());
        List<User> users = new ArrayList<>();
        if(newFriendsList.size() < 1){
            return users;
        }
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.in(User::getId, newFriendsList);
        users = userMapper.selectList(userLambdaQueryWrapper);
        return users;
    }

    @Override
    public void deleteByUserId(Integer user_id) {
        Integer userId = UserThreadLocal.get();
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getApplicant, user_id).or().eq(AddressBook::getRespondent, user_id);
        addressBookMapper.delete(queryWrapper);

        LambdaQueryWrapper<Message> messageLambdaQueryWrapper = new LambdaQueryWrapper<>();
        messageLambdaQueryWrapper.eq(Message::getFromId, user_id).or().eq(Message::getToId, user_id);
        messageLambdaQueryWrapper.eq(Message::getFromId, userId).or().eq(Message::getToId, userId);
        messageMapper.delete(messageLambdaQueryWrapper);

        LambdaQueryWrapper<HistoricalChat> historicalChatLambdaQueryWrapper = new LambdaQueryWrapper<>();
        historicalChatLambdaQueryWrapper.eq(HistoricalChat::getUserId, userId);
        historicalChatMapper.delete(historicalChatLambdaQueryWrapper);
    }

    @Override
    public void agree(Integer user_id) {
        Integer userId = UserThreadLocal.get();
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getApplicant, user_id);
        queryWrapper.eq(AddressBook::getRespondent, userId);
        AddressBook addressBook = addressBookMapper.selectOne(queryWrapper);
        if(addressBook != null){
            addressBook.setIsAgree(1);
            addressBookMapper.updateById(addressBook);
        }
    }

    @Override
    public void refuse(Integer user_id) {
        Integer userId = UserThreadLocal.get();
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getApplicant, user_id);
        queryWrapper.eq(AddressBook::getRespondent, userId);
        AddressBook addressBook = addressBookMapper.selectOne(queryWrapper);
        if(addressBook != null){
            addressBookMapper.deleteById(addressBook.getId());
        }
    }

    @Override
    public Boolean isFriend(Integer user_id, Integer userId) {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getApplicant, user_id).or().eq(AddressBook::getRespondent, user_id);
        queryWrapper.eq(AddressBook::getApplicant, userId).or().eq(AddressBook::getRespondent, userId);
        queryWrapper.eq(AddressBook::getIsAgree, 1);
        Integer count = addressBookMapper.selectCount(queryWrapper);
        return count != null && count > 0;
    }
}
