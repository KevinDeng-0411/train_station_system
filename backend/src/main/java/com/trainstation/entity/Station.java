package com.trainstation.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("stations")
public class Station {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "站点名称不能为空")
    private String stationName;

    @NotBlank(message = "所在城市不能为空")
    private String city;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}