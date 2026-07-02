package com.trainstation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("stations")
public class Station {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String stationName;

    private String city;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
