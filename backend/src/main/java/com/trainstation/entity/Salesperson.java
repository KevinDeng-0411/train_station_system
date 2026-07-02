package com.trainstation.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("salespeople")
public class Salesperson {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "工号不能为空")
    private String employeeCode;

    @NotBlank(message = "姓名不能为空")
    private String name;

    private String phone;

    private String idCard;

    @NotNull(message = "状态不能为空")
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}