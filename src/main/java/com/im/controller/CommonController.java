package com.im.controller;

import com.im.common.R;
import com.im.entity.Message;
import com.im.mapper.MessageMapper;
import com.im.service.AddressBookService;
import com.im.utils.NoAuthorization;
import com.im.utils.UserThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class CommonController {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private AddressBookService addressBookService;

    private final static String PATH = System.getProperty("user.dir") + "/files/";

    @NoAuthorization
    @PostMapping("/upload")
    public R upload(MultipartFile file, Message message, HttpServletRequest request){
        Integer userId = UserThreadLocal.get();
        Integer user_id = message.getToId();
        if(!addressBookService.isFriend(user_id, userId)){
            return R.error("你们不是好友无法发送消息");
        }
        File file1 = new File(PATH);
        if (!file1.exists()) {
            file1.mkdirs();
        }

        String[] pics = new String[]{
                ".png",
                ".jpg",
        };
        System.out.println(message);
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + suffix;
        try {
            file.transferTo(new File(PATH + filename));
            message.setType(4);
            for (String pic : pics) {
                if(suffix.toLowerCase().equals(pic)){
                    message.setType(3);
                }
            }
            message.setMessage("/file/download/" + filename);
            message.setFromId(userId);
            message.setTime(LocalDateTime.now());
            messageMapper.insert(message);
        } catch (IOException e) {
        }
        return R.success(message);
    }

    @NoAuthorization
    @PostMapping("/uploadAvatar")
    public R upload(MultipartFile file, HttpServletRequest request){
        File file1 = new File(PATH);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + suffix;
        try {
            file.transferTo(new File(PATH + filename));
        } catch (IOException e) {
        }
        return R.success("/file/download/" + filename);
    }


    @NoAuthorization
    @GetMapping("/download/{filename}")
    public void download(@PathVariable("filename") String filename, HttpServletResponse response){
        File file1 = new File(PATH);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(PATH + filename));
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("application/octet-stream");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
        }
    }
}
