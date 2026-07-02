package com.trainstation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("refund_records")
public class RefundRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long ticketId;

    private BigDecimal refundAmount;

    private LocalDateTime refundTime;

    private Long operatorId;

    private String reason;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // 扩展字段
    @TableField(exist = false)
    private String operatorName;
}
