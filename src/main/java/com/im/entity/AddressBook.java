package com.im.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

@Data
public class AddressBook implements Serializable {
    @TableField("id")
    private Integer id;
    private Integer applicant;
    private Integer respondent;
    private String reason;
    private Integer isAgree;
}
