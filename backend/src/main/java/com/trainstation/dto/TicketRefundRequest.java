package com.trainstation.dto;

import lombok.Data;

@Data
public class TicketRefundRequest {
    private Long ticketId;
    private Long operatorId;
    private String reason;
}
