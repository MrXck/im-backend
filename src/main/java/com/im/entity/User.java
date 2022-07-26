package com.im.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    @TableField("id")
    private Integer id;

    private String username;

    private String password;

    private String avatar;

    private String phone;

    private String synopsis;
}
