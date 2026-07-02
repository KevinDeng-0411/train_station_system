package com.trainstation.dto;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TicketDetailDto {
    private Long id;
    private Long trainId;
    private String trainNumber;
    private String passengerName;
    private String passengerIdCard;
    private Long departureStationId;
    private String departureStationName;
    private Long arrivalStationId;
    private String arrivalStationName;
    private String seatNumber;
    private BigDecimal price;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate saleDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime saleTime;
    private Long salespersonId;
    private String salespersonName;
    private Integer status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}