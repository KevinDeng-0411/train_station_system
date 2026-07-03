package com.trainstation.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@TableName("trains")
public class Train {

    @TableId(type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "车次号不能为空")
    private String trainNumber;

    @NotBlank(message = "出发城市不能为空")
    private String departureCity;

    @NotBlank(message = "到达城市不能为空")
    private String arrivalCity;

    @NotNull(message = "总座位数不能为空")
    private Integer totalSeats;

    private Integer remainingSeats;

    @NotNull(message = "发车时间不能为空")
    private LocalTime departureTime;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // 关联查询字段（不在表中）
    @TableField(exist = false)
    private String departureStationName;

    @TableField(exist = false)
    private String arrivalStationName;
}