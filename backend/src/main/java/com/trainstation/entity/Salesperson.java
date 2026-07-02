package com.trainstation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("salespeople")
public class Salesperson {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String employeeCode;

    private String name;

    private String phone;

    private String idCard;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
