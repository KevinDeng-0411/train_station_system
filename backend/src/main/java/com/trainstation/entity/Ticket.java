package com.trainstation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("tickets")
public class Ticket {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long trainId;

    private String passengerName;

    private String passengerIdCard;

    private Long departureStationId;

    private Long arrivalStationId;

    private String seatNumber;

    private BigDecimal price;

    private LocalDate saleDate;

    private LocalDateTime saleTime;

    private Long salespersonId;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // 扩展字段
    @TableField(exist = false)
    private String trainNumber;

    @TableField(exist = false)
    private String departureStationName;

    @TableField(exist = false)
    private String arrivalStationName;

    @TableField(exist = false)
    private String salespersonName;
}
