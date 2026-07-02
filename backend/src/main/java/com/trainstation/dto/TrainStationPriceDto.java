package com.trainstation.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TrainStationPriceDto {
    private Long stationId;
    private String stationName;
    private String city;
    private Integer stopOrder;
    private String arrivalTime;
    private String departureTime;
    private BigDecimal price;
}
