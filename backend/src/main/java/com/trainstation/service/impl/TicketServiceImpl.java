package com.trainstation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trainstation.entity.Ticket;
import com.trainstation.entity.Train;
import com.trainstation.entity.RefundRecord;
import com.trainstation.mapper.TicketMapper;
import com.trainstation.mapper.RefundRecordMapper;
import com.trainstation.mapper.TrainMapper;
import com.trainstation.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketMapper ticketMapper;

    @Autowired
    private TrainMapper trainMapper;

    @Autowired
    private RefundRecordMapper refundRecordMapper;

    @Override
    public Page<Ticket> getTicketPage(int pageNum, int pageSize, Long trainId, Long salespersonId) {
        Page<Ticket> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>();
        if (trainId != null) {
            wrapper.eq(Ticket::getTrainId, trainId);
        }
        if (salespersonId != null) {
            wrapper.eq(Ticket::getSalespersonId, salespersonId);
        }
        wrapper.orderByDesc(Ticket::getSaleTime);
        return ticketMapper.selectPage(page, wrapper);
    }

    @Override
    public Ticket getTicketById(Long id) {
        return ticketMapper.selectById(id);
    }

    @Override
    @Transactional
    public Ticket saleTicket(Ticket ticket) {
        // 1. 检查余票是否充足
        Train train = trainMapper.selectById(ticket.getTrainId());
        if (train == null) {
            throw new RuntimeException("车次不存在");
        }
        if (train.getRemainingSeats() <= 0) {
            throw new RuntimeException("余票不足，无法售票");
        }

        // 2. 检查座位是否已被占用
        LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Ticket::getTrainId, ticket.getTrainId())
               .eq(Ticket::getSaleDate, ticket.getSaleDate())
               .eq(Ticket::getSeatNumber, ticket.getSeatNumber())
               .eq(Ticket::getStatus, 1);
        long count = ticketMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("该座位已被售出");
        }

        // 3. 设置状态为有效
        ticket.setStatus(1);
        ticket.setSaleTime(LocalDateTime.now());

        // 4. 插入售票记录
        ticketMapper.insert(ticket);

        // 5. 减少余票（通过触发器也会自动减少，这里应用层也做一次确保一致）
        train.setRemainingSeats(train.getRemainingSeats() - 1);
        trainMapper.updateById(train);

        return ticket;
    }

    @Override
    @Transactional
    public RefundRecord refundTicket(Long ticketId, Long operatorId, String reason) {
        // 1. 检查车票是否存在
        Ticket ticket = ticketMapper.selectById(ticketId);
        if (ticket == null) {
            throw new RuntimeException("车票不存在");
        }
        if (ticket.getStatus() == 0) {
            throw new RuntimeException("该车票已退票");
        }

        // 2. 更新车票状态
        ticket.setStatus(0);
        ticketMapper.updateById(ticket);

        // 3. 恢复余票
        Train train = trainMapper.selectById(ticket.getTrainId());
        if (train != null && train.getRemainingSeats() < train.getTotalSeats()) {
            train.setRemainingSeats(train.getRemainingSeats() + 1);
            trainMapper.updateById(train);
        }

        // 4. 创建退票记录
        RefundRecord refundRecord = new RefundRecord();
        refundRecord.setTicketId(ticketId);
        refundRecord.setRefundAmount(ticket.getPrice());
        refundRecord.setRefundTime(LocalDateTime.now());
        refundRecord.setOperatorId(operatorId);
        refundRecord.setReason(reason);
        refundRecordMapper.insert(refundRecord);

        return refundRecord;
    }

    @Override
    public boolean isSeatAvailable(Long trainId, String seatNumber, java.time.LocalDate saleDate) {
        LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Ticket::getTrainId, trainId)
               .eq(Ticket::getSeatNumber, seatNumber)
               .eq(Ticket::getSaleDate, saleDate)
               .eq(Ticket::getStatus, 1);
        return ticketMapper.selectCount(wrapper) == 0;
    }
}
