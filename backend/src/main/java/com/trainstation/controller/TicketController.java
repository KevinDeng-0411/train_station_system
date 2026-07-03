package com.trainstation.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trainstation.dto.TicketDetailDto;
import com.trainstation.entity.Ticket;
import com.trainstation.entity.RefundRecord;
import com.trainstation.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping
    public Map<String, Object> getTickets(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long trainId,
            @RequestParam(required = false) Long salespersonId) {
        Map<String, Object> result = new HashMap<>();
        Page<TicketDetailDto> page = ticketService.getTicketPage(pageNum, pageSize, trainId, salespersonId);
        result.put("success", true);
        result.put("data", page.getRecords());
        result.put("total", page.getTotal());
        return result;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getTicketById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        Ticket ticket = ticketService.getTicketById(id);
        result.put("success", ticket != null);
        result.put("data", ticket);
        return result;
    }

    @PostMapping("/sale")
    public Map<String, Object> saleTicket(@RequestBody Ticket ticket) {
        Map<String, Object> result = new HashMap<>();
        try {
            Ticket saved = ticketService.saleTicket(ticket);
            result.put("success", true);
            result.put("data", saved);
            result.put("message", "售票成功");
        } catch (RuntimeException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @PostMapping("/{id}/refund")
    public Map<String, Object> refundTicket(
            @PathVariable Long id,
            @RequestParam Long operatorId,
            @RequestParam(required = false) String reason) {
        Map<String, Object> result = new HashMap<>();
        try {
            RefundRecord record = ticketService.refundTicket(id, operatorId, reason);
            result.put("success", true);
            result.put("data", record);
            result.put("message", "退票成功");
        } catch (RuntimeException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    @GetMapping("/sold-seats")
    public Map<String, Object> getSoldSeats(
            @RequestParam Long trainId,
            @RequestParam String saleDate) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", ticketService.getSoldSeats(trainId, java.time.LocalDate.parse(saleDate)));
        return result;
    }

    @GetMapping("/check-seat")
    public Map<String, Object> checkSeatAvailable(
            @RequestParam Long trainId,
            @RequestParam String seatNumber,
            @RequestParam String saleDate) {
        Map<String, Object> result = new HashMap<>();
        boolean available = ticketService.isSeatAvailable(
            trainId, seatNumber, java.time.LocalDate.parse(saleDate));
        result.put("success", true);
        result.put("data", available);
        return result;
    }
}
