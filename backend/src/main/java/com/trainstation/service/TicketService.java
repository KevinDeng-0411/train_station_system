package com.trainstation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trainstation.dto.TicketDetailDto;
import com.trainstation.entity.Ticket;
import com.trainstation.entity.RefundRecord;
import java.time.LocalDate;

public interface TicketService {
    Page<TicketDetailDto> getTicketPage(int pageNum, int pageSize, Long trainId, Long salespersonId);
    Ticket getTicketById(Long id);
    Ticket saleTicket(Ticket ticket);
    RefundRecord refundTicket(Long ticketId, Long operatorId, String reason);
    boolean isSeatAvailable(Long trainId, String seatNumber, LocalDate saleDate);
}