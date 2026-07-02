package com.trainstation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@TableName("train_stations")
public class TrainStation {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long trainId;

    private Long stationId;

    private Integer stopOrder;

    private LocalTime arrivalTime;

    private LocalTime departureTime;

    private BigDecimal price;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // 扩展字段，用于关联查询
    @TableField(exist = false)
    private String stationName;

    @TableField(exist = false)
    private String city;
}
