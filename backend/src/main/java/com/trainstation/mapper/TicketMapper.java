package com.trainstation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.trainstation.entity.Ticket;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TicketMapper extends BaseMapper<Ticket> {
}
