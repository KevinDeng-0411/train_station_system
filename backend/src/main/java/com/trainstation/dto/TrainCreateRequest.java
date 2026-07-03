package com.trainstation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalTime;

@Data
public class TrainCreateRequest {
    @NotBlank(message = "车次号不能为空")
    private String trainNumber;

    @NotNull(message = "请选择出发站")
    private Long departureStationId;

    @NotNull(message = "请选择到达站")
    private Long arrivalStationId;

    @NotNull(message = "总座位数不能为空")
    private Integer totalSeats;

    @NotNull(message = "发车时间不能为空")
    private LocalTime departureTime;

    private Integer status = 1;
}