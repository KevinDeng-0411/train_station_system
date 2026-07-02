package com.trainstation.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TicketSaleRequest {
    private Long trainId;
    private String passengerName;
    private String passengerIdCard;
    private Long departureStationId;
    private Long arrivalStationId;
    private String seatNumber;
    private BigDecimal price;
    private LocalDate saleDate;
    private Long salespersonId;
}
