package com.trainstation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@TableName("trains")
public class Train {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String trainNumber;

    private String departureCity;

    private String arrivalCity;

    private Integer totalSeats;

    private Integer remainingSeats;

    private LocalTime departureTime;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
