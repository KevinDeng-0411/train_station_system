package com.trainstation.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.trainstation.dto.TicketDetailDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TicketDetailMapper extends BaseMapper<TicketDetailDto> {

    @Select("""
        SELECT
            t.id, t.train_id, tr.train_number, t.passenger_name, t.passenger_id_card,
            t.departure_station_id, ds.station_name AS departure_station_name,
            t.arrival_station_id, as_st.station_name AS arrival_station_name,
            t.seat_number, t.price, t.sale_date, t.sale_time,
            t.salesperson_id, sp.name AS salesperson_name,
            t.status, t.created_at, t.updated_at
        FROM tickets t
        LEFT JOIN trains tr ON t.train_id = tr.id
        LEFT JOIN stations ds ON t.departure_station_id = ds.id
        LEFT JOIN stations as_st ON t.arrival_station_id = as_st.id
        LEFT JOIN salespeople sp ON t.salesperson_id = sp.id
        ${ew.customSqlSegment}
    """)
    Page<TicketDetailDto> selectTicketPage(IPage<TicketDetailDto> page, @Param(Constants.WRAPPER) Wrapper<?> wrapper);
}