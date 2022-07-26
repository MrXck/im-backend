package com.im.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

@Data
public class HistoricalChat implements Serializable {
    @TableField("id")
    private Integer id;
    private Integer userId;
    private Integer withId;
}
