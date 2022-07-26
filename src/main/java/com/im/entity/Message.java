package com.im.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Message implements Serializable {

    @TableField("id")
    private Integer id;
    private Integer fromId;
    private Integer toId;
    private String message;
    private LocalDateTime time;

    // 1 登录  2 消息  3 图片  4 文件  5 聊天室消息
    private Integer type;
}
